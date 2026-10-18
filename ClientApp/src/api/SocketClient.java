package api;

import dto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketClient {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = false;
    private Long tableId;

    // Callback truyền gói tin nhận được từ Server thẳng lên ClientTabletBUS / GUI
    private Consumer<SocketPacket> onServerPacketReceived;

    public boolean connect(String host, int port, Long tableId, Consumer<SocketPacket> onServerPacketReceived) {
        this.tableId = tableId;
        this.onServerPacketReceived = onServerPacketReceived;

        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;

            // Khởi chạy luồng lắng nghe bất đồng bộ từ Server
            Thread listenerThread = new Thread(this::listenFromServer);
            listenerThread.setDaemon(true);
            listenerThread.start();

            // Vừa kết nối xong -> Đăng ký ngay mã bàn với Server và yêu cầu tải Thực đơn
            sendRequest(new SocketPacket(SocketAction.REGISTER_TABLE, tableId, null, null));
            requestMenuAndCategories();

            return true;
        } catch (IOException e) {
            System.err.println("[SocketClientAPI] Không kết nối được TCP Server (" + host + ":" + port + ").");
            isConnected = false;
            return false;
        }
    }

    private void listenFromServer() {
        try {
            while (isConnected && !socket.isClosed()) {
                Object response = in.readObject();
                if (response instanceof SocketPacket && onServerPacketReceived != null) {
                    onServerPacketReceived.accept((SocketPacket) response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            isConnected = false;
            System.err.println("[SocketClientAPI] Mất kết nối tới Server.");
        }
    }

    public synchronized void sendRequest(SocketPacket packet) {
        if (!isConnected || out == null) return;
        try {
            out.reset();
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1. Yêu cầu lấy danh sách Món ăn (FoodDTO) + Danh mục (CategoryDTO)
    public void requestMenuAndCategories() {
        sendRequest(new SocketPacket(SocketAction.GET_MENU_AND_CATEGORIES, tableId, null, null));
    }

    // 2. Gửi đối tượng OrderDTO và mã Voucher xuống Server
    public void sendOrderToServer(OrderDTO order, String voucherCode) {
        sendRequest(new SocketPacket(SocketAction.PLACE_ORDER, tableId, order, voucherCode));
    }

    // 3. Yêu cầu làm mới bảng Theo dõi đơn hàng của bàn
    public void requestTableTracking() {
        sendRequest(new SocketPacket(SocketAction.GET_TABLE_TRACKING, tableId, null, null));
    }

    // 4. Bấm chuông gọi nhân viên tại bàn
    public void callStaff(String note) {
        sendRequest(new SocketPacket(SocketAction.CALL_STAFF, tableId, note, null));
    }

    public boolean isConnected() {
        return isConnected;
    }
}