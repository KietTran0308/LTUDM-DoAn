package api;

import bus.*;
import dao.*;
import dto.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Long currentTableId;

    // Khởi tạo các tầng BUS và DAO của Server
    private final FoodDAO foodDAO = new FoodDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ServerOrderBUS serverOrderBUS = new ServerOrderBUS();
    private final serverAuthBUS authBUS = new serverAuthBUS();
    private final Consumer<SocketPacket> onKitchenNotification;

    public ClientHandler(Socket socket, Consumer<SocketPacket> onKitchenNotification) {
        this.socket = socket;
        this.onKitchenNotification = onKitchenNotification;
    }

    @Override
    public void run() {
        try {
            // Lưu ý quan trọng: Luôn khởi tạo ObjectOutputStream TRƯỚC ObjectInputStream kèm flush() để tránh Deadlock
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                Object received = in.readObject();
                if (received instanceof SocketPacket) {
                    SocketPacket request = (SocketPacket) received;
                    handleClientRequest(request);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ClientHandler] Kết nối bàn " + currentTableId + " đã đóng.");
        } finally {
            SocketServer.removeTableSession(currentTableId);
            closeResources();
        }
    }

    private void handleClientRequest(SocketPacket req) {
        try {
            switch (req.getAction()) {
                case REGISTER_TABLE: {
                    this.currentTableId = req.getTableId();
                    SocketServer.registerTableSession(this.currentTableId, this);
                    sendPacket(new SocketPacket(
                            SocketAction.REGISTER_TABLE, true,
                            "Kết nối máy chủ thành công cho Bàn số " + currentTableId, null, null
                    ));
                    break;
                }

                case GET_MENU_AND_CATEGORIES: {
                    List<FoodDTO> foods = foodDAO.getPublishedFoodsForTablet();
                    List<CategoryDTO> categories = categoryDAO.getAllCategories();
                    // data = List<FoodDTO>, extraData = List<CategoryDTO>
                    sendPacket(new SocketPacket(
                            SocketAction.GET_MENU_AND_CATEGORIES, true,
                            "Tải thực đơn thành công", foods, categories
                    ));
                    break;
                }

                case PLACE_ORDER: {
                    OrderDTO incomingOrder = (OrderDTO) req.getData();
                    String voucherCode = (String) req.getExtraData();

                    // Gọi xuống ServerOrderBUS để kiểm tra đơn gốc parent_order_id, tính tiền, lưu DB & trừ kho
                    OrderDTO savedOrder = serverOrderBUS.processIncomingTableOrder(incomingOrder, voucherCode);

                    // Lấy lại toàn bộ danh sách món đang theo dõi của bàn này trả về cho Tablet
                    List<OrderItemDTO> updatedTrackingList = serverOrderBUS.getTableTrackingItems(req.getTableId());

                    sendPacket(new SocketPacket(
                            SocketAction.PLACE_ORDER, true,
                            "Đặt món thành công! Mã đơn: " + savedOrder.getOrderCode(),
                            savedOrder, updatedTrackingList
                    ));

                    // Báo lên màn hình Bếp / Admin ở Server có đơn hàng mới
                    if (onKitchenNotification != null) {
                        onKitchenNotification.accept(new SocketPacket(
                                SocketAction.PLACE_ORDER, req.getTableId(), savedOrder, null
                        ));
                    }
                    break;
                }

                case GET_TABLE_TRACKING: {
                    List<OrderItemDTO> trackedItems = serverOrderBUS.getTableTrackingItems(req.getTableId());
                    sendPacket(new SocketPacket(
                            SocketAction.GET_TABLE_TRACKING, true,
                            "Đồng bộ trạng thái đơn thành công", trackedItems, null
                    ));
                    break;
                }

                case CALL_STAFF: {
                    if (onKitchenNotification != null) {
                        onKitchenNotification.accept(req);
                    }
                    sendPacket(new SocketPacket(
                            SocketAction.CALL_STAFF, true,
                            "Nhân viên đã nhận được yêu cầu hỗ trợ của Bàn số " + req.getTableId(), null, null
                    ));
                    break;
                }

                case GET_ALL_TABLES: {
                    List<TableDTO> tables = serverOrderBUS.getAllRestaurantTables();
                    sendPacket(new SocketPacket(
                            SocketAction.GET_ALL_TABLES, true, "Tải sơ đồ bàn thành công", tables, null
                    ));
                    break;
                }

                case GET_TABLE_BILL_PREVIEW: {
                    OrderDTO mergedOrder = serverOrderBUS.getTableBillPreview(req.getTableId());
                    if (mergedOrder == null) {
                        sendPacket(new SocketPacket(
                                SocketAction.GET_TABLE_BILL_PREVIEW, false,
                                "Bàn số " + req.getTableId() + " hiện chưa có đơn hàng nào cần thanh toán!", null, null
                        ));
                    } else {
                        sendPacket(new SocketPacket(
                                SocketAction.GET_TABLE_BILL_PREVIEW, true,
                                "Đã gộp toàn bộ các lượt gọi món của Bàn #" + req.getTableId(), mergedOrder, null
                        ));
                    }
                    break;
                }

                case PAY_TABLE_BILL: {
                    BillDTO bill = (BillDTO) req.getData();
                    Long targetTableId = req.getTableId();

                    boolean ok = serverOrderBUS.processTablePayment(bill, targetTableId);
                    if (ok) {
                        // Bắn tín hiệu về Tablet của bàn vừa thanh toán để xóa danh sách món cũ
                        SocketServer.notifyTableCleared(targetTableId);

                        // Trả lại danh sách bàn mới nhất cho Thu ngân
                        List<TableDTO> updatedTables = serverOrderBUS.getAllRestaurantTables();
                        sendPacket(new SocketPacket(
                                SocketAction.PAY_TABLE_BILL, true,
                                "Thanh toán thành công cho Bàn #" + targetTableId + "! Đã xuất hóa đơn & mở trống bàn.",
                                updatedTables, null
                        ));
                    } else {
                        sendPacket(new SocketPacket(
                                SocketAction.PAY_TABLE_BILL, false,
                                "Thanh toán thất bại (Đơn hàng đã được thanh toán hoặc lỗi CSDL)!", null, null
                        ));
                    }
                    break;
                }

                case LOGIN: {
                    String emailOrPhone = (String) req.getData();
                    String password = (String) req.getExtraData();
                    UserDTO loggedUser = authBUS.login(emailOrPhone, password);
                    sendPacket(new SocketPacket(
                            SocketAction.LOGIN, true,
                            "Xin chào " + loggedUser.getFullName() + " (" + loggedUser.getRoleName() + ")",
                            loggedUser, null
                    ));
                    break;
                }

                case REGISTER: {
                    UserDTO regUser = (UserDTO) req.getData();
                    UserDTO createdUser = authBUS.register(regUser);
                    sendPacket(new SocketPacket(
                            SocketAction.REGISTER, true,
                            "Đăng ký tài khoản thành công cho " + createdUser.getFullName() + "!",
                            createdUser, null
                    ));
                    break;
                }

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendPacket(new SocketPacket(
                    req.getAction(), false, "Lỗi xử lý tại Server: " + e.getMessage(), null, null
            ));
        }
    }

    /**
     * Đồng bộ hóa (synchronized) hàm gửi để tránh 2 luồng cùng ghi vào 1 ObjectOutputStream gây lỗi StreamCorruptedException
     */
    public synchronized void sendPacket(SocketPacket packet) {
        try {
            if (out != null && !socket.isClosed()) {
                out.reset(); // Xóa cache tham chiếu cũ của ObjectOutputStream để gửi dữ liệu mới nhất
                out.writeObject(packet);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[ClientHandler] Không thể gửi gói tin về Bàn " + currentTableId);
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}