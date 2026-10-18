package bus;

import dto.ChiTietDonDTO;
import dto.MonAnDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import api.SocketClient;

public class KhachHangBUS {
    private List<ChiTietDonDTO> gioHang = new ArrayList<>();
    private List<ChiTietDonDTO> lichSuDonHang = new ArrayList<>();
    private SocketClient socketAPI = new SocketClient();
    private int maBan;

    public KhachHangBUS(int maBan, Consumer<String> onStatusUpdate) {
        this.maBan = maBan;
        // Kết nối thử tới Server TCP ở cổng 8888
        socketAPI.connect("localhost", 8888, maBan, onStatusUpdate);
    }

    // Dữ liệu thực đơn mẫu chuẩn Haidilao (Khi chạy thật sẽ lấy từ Server qua Socket)
    public List<MonAnDTO> layDanhSachThựcDon() {
        List<MonAnDTO> menu = new ArrayList<>();
        menu.add(new MonAnDTO(1, "Lẩu Dầu Cay Tứ Xuyên", "Nước Lẩu", 130000, "lau_tu_xuyen.jpg"));
        menu.add(new MonAnDTO(2, "Lẩu Cà Chua Haidilao", "Nước Lẩu", 120000, "lau_ca_chua.jpg"));
        menu.add(new MonAnDTO(3, "Lẩu Nấm Thiên Nhiên", "Nước Lẩu", 120000, "lau_nam.jpg"));
        menu.add(new MonAnDTO(4, "Thịt Bò Bông Tuyết Mỹ", "Thịt & Hải Sản", 210000, "bo_bong_tuyet.jpg"));
        menu.add(new MonAnDTO(5, "Thịt Bò Haidilao Đặc Biệt", "Thịt & Hải Sản", 165000, "bo_haidilao.jpg"));
        menu.add(new MonAnDTO(6, "Tôm Sú Nhảy Múa", "Thịt & Hải Sản", 180000, "tom_su.jpg"));
        menu.add(new MonAnDTO(7, "Múa Mì Kéo Sợi Tại Bàn", "Món Đặc Sắc", 40000, "mua_mi.jpg"));
        menu.add(new MonAnDTO(8, "Đậu Phụ Phô Mai", "Viên Thả Lẩu", 75000, "dau_phu.jpg"));
        menu.add(new MonAnDTO(9, "Combo Rau Nấm Tổng Hợp", "Rau & Nấm", 95000, "combo_rau.jpg"));
        return menu;
    }

    public void themVaoGio(MonAnDTO mon) {
        for (ChiTietDonDTO item : gioHang) {
            if (item.getMonAn().getMaMon() == mon.getMaMon()) {
                item.setSoLuong(item.getSoLuong() + 1);
                return;
            }
        }
        gioHang.add(new ChiTietDonDTO(mon, 1, "Trong giỏ"));
    }

    public void capNhatSoLuong(int maMon, int thayDoi) {
        gioHang.removeIf(item -> {
            if (item.getMonAn().getMaMon() == maMon) {
                item.setSoLuong(item.getSoLuong() + thayDoi);
                return item.getSoLuong() <= 0;
            }
            return false;
        });
    }

    public double tinhTongTienGioHang() {
        return gioHang.stream().mapToDouble(ChiTietDonDTO::getThanhTien).sum();
    }

    public boolean guiDonXuongBep() {
        if (gioHang.isEmpty()) return false;

        List<ChiTietDonDTO> donGuiDi = new ArrayList<>();
        for (ChiTietDonDTO item : gioHang) {
            ChiTietDonDTO monMoi = new ChiTietDonDTO(item.getMonAn(), item.getSoLuong(), "Chờ bếp xác nhận");
            donGuiDi.add(monMoi);
            lichSuDonHang.add(monMoi);
        }

        // Gửi qua TCP Socket
        socketAPI.guiDonDatMon(maBan, donGuiDi);
        gioHang.clear();
        return true;
    }

    public List<ChiTietDonDTO> getGioHang() { return gioHang; }
    public List<ChiTietDonDTO> getLichSuDonHang() { return lichSuDonHang; }
}