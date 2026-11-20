package Services;

import database.DBConnection;
import dao.*;
import enums.*;
import model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SinhVienService {
    public static final int SO_SACH_TOI_DA = 5;
    public static final int SO_NGAY_MUON = 14;

    public static final BigDecimal TIEN_PHAT_MOI_NGAY = new BigDecimal("5000");

    private final SinhVienDAO svDao = new SinhVienDAO();
    private final SachDAO sachDao = new SachDAO();
    private final TheLoaiDAO theLoaiDao = new TheLoaiDAO();
    private final PhieuMuonDAO phieuMuonDao = new PhieuMuonDAO();
    private final chiTietPhieuMuonDAO chiTietDao = new chiTietPhieuMuonDAO();

    public List<Sach> searchSachTheoTen(String keyword) throws SQLException {
        return napTacGia(sachDao.searchByTen(chuanHoaTuKhoa(keyword)));
    }

    public List<Sach> searchSachTheoTacGia(String keyword) throws SQLException {
        return napTacGia(sachDao.searchByTacGia(chuanHoaTuKhoa(keyword)));
    }

    public List<Sach> searchSachTheoTheLoai(String maTheLoai) throws SQLException {
        if (maTheLoai == null || maTheLoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thể loại không thể trống");
        }
        if (theLoaiDao.findById(maTheLoai.trim()) == null) {
            throw new IllegalArgumentException("Mã thể loại " + maTheLoai.trim() + " không tồn tại!");
        }
        return napTacGia(sachDao.searchByTheLoai(maTheLoai.trim()));
    }

    public List<Sach> xemTatCaSach() throws SQLException {
        return napTacGia(sachDao.searchByTen(""));
    }

    /** Thông tin chi tiết một cuốn sách, gồm tác giả và số lượng còn lại. */
    public Sach xemChiTietSach(String maSach) throws SQLException {
        if (maSach == null || maSach.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sách không thể trống");
        }
        Sach s = sachDao.findbyId(maSach.trim());
        if (s == null) {
            throw new IllegalArgumentException("Mã sách " + maSach.trim() + " không tồn tại!");
        }
        return s;
    }

    public List<TheLoai> xemTatCaTheLoai() throws SQLException {
        List<TheLoai> ds = theLoaiDao.findAll();
        return ds == null ? new ArrayList<>() : ds;
    }

    public PhieuMuon muonSach(String maSinhVien, List<String> dsMaSach) throws SQLException {
        String maSv = kiemTraSinhVien(maSinhVien);
        List<String> dsMa = chuanHoaDanhSachMaSach(dsMaSach);

        for (String ma : dsMa) {
            Sach s = sachDao.findbyId(ma);
            if (s == null) {
                throw new IllegalArgumentException("Sách " + ma + " không tồn tại!");
            }
            if (s.getTrangThai() != TrangThaiSach.BINH_THUONG) {
                throw new IllegalArgumentException("Sách \"" + s.getTenSach() + "\" hiện không thể mượn!");
            }
            if (s.getSoLuong() <= 0) {
                throw new IllegalArgumentException("Sách \"" + s.getTenSach() + "\" đã hết, không thể mượn!");
            }
        }

        LocalDate ngayMuon = LocalDate.now();
        LocalDate hanTra = ngayMuon.plusDays(SO_NGAY_MUON);
        PhieuMuon phieu = new PhieuMuon(taoMaPhieu(), ngayMuon, maSv);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int dangMuon = chiTietDao.demDangMuon(conn, maSv);
                if (dangMuon + dsMa.size() > SO_SACH_TOI_DA) {
                    int conDuoc = Math.max(0, SO_SACH_TOI_DA - dangMuon);
                    throw new IllegalArgumentException("Bạn đang mượn " + dangMuon
                            + " cuốn chưa trả, chỉ được mượn thêm tối đa " + conDuoc
                            + " cuốn (giới hạn " + SO_SACH_TOI_DA + " cuốn cùng lúc)!");
                }

                phieuMuonDao.insert(conn, phieu);

                for (String ma : dsMa) {
                    // Chi tru khi con hang: tranh 2 nguoi cung muon cuon cuoi cung
                    if (!sachDao.giamSoLuong(conn, ma)) {
                        throw new IllegalArgumentException(
                                "Sách " + ma + " vừa hết, vui lòng thử lại!");
                    }
                    chiTietDao.insert(conn, new ChiTietPhieuMuon(
                            phieu.getMaPhieu(), ma, hanTra, null,
                            TinhTrangMuon.CHUA_TRA, BigDecimal.ZERO));
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
        return phieu;
    }

    /** Số cuốn sinh viên còn được mượn thêm. */
    public int soSachConDuocMuon(String maSinhVien) throws SQLException {
        String maSv = kiemTraSinhVien(maSinhVien);
        return Math.max(0, SO_SACH_TOI_DA - chiTietDao.demDangMuon(maSv));
    }

    // =====================================================
    // TRA SACH
    // =====================================================

    /**
     * Trả một hoặc nhiều cuốn trong cùng một phiếu.
     * Ghi nhận ngày trả; nếu quá hạn thì tình trạng = QUA_HAN và tính tiền phạt
     * theo số ngày quá hạn; đồng thời cộng lại số lượng sách.
     *
     * @return các dòng chi tiết đã cập nhật (có ngày trả, tình trạng, tiền phạt)
     */
    public List<ChiTietPhieuMuon> traSach(String maSinhVien, String maPhieu, List<String> dsMaSach)
            throws SQLException {
        String maSv = kiemTraSinhVien(maSinhVien);

        if (maPhieu == null || maPhieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu mượn không thể trống");
        }
        String phieuMa = maPhieu.trim();

        PhieuMuon phieu = phieuMuonDao.findById(phieuMa);
        if (phieu == null) {
            throw new IllegalArgumentException("Mã phiếu mượn " + phieuMa + " không tồn tại!");
        }
        if (!maSv.equals(phieu.getMaSinhVien())) {
            throw new IllegalArgumentException("Phiếu mượn này không thuộc về bạn!");
        }

        List<String> dsMa = chuanHoaDanhSachMaSach(dsMaSach);
        LocalDate ngayTra = LocalDate.now();
        List<ChiTietPhieuMuon> ketQua = new ArrayList<>();

        for (String ma : dsMa) {
            ChiTietPhieuMuon ct = chiTietDao.findById(phieuMa, ma);
            if (ct == null) {
                throw new IllegalArgumentException("Sách " + ma + " không có trong phiếu " + phieuMa + "!");
            }
            if (ct.getTinhTrang() != TinhTrangMuon.CHUA_TRA) {
                throw new IllegalArgumentException("Sách " + ma + " đã được trả rồi!");
            }
            capNhatKhiTra(ct, ngayTra);
            ketQua.add(ct);
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (ChiTietPhieuMuon ct : ketQua) {
                    chiTietDao.update(conn, ct);
                    if (!sachDao.tangSoLuong(conn, ct.getMaSach())) {
                        throw new IllegalStateException(
                                "Không cập nhật được số lượng của sách " + ct.getMaSach());
                    }
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
        return ketQua;
    }

    /** Trả một cuốn sách. */
    public ChiTietPhieuMuon traSach(String maSinhVien, String maPhieu, String maSach) throws SQLException {
        List<String> ds = new ArrayList<>();
        ds.add(maSach);
        return traSach(maSinhVien, maPhieu, ds).get(0);
    }

    /** Điền ngày trả, tình trạng và tiền phạt (nếu quá hạn) vào dòng chi tiết. */
    private void capNhatKhiTra(ChiTietPhieuMuon ct, LocalDate ngayTra) {
        ct.setNgayTra(ngayTra);
        if (ngayTra.isAfter(ct.getHanTra())) {
            long soNgayQuaHan = ChronoUnit.DAYS.between(ct.getHanTra(), ngayTra);
            ct.setTinhTrang(TinhTrangMuon.QUA_HAN);
            ct.setTienPhat(TIEN_PHAT_MOI_NGAY.multiply(BigDecimal.valueOf(soNgayQuaHan)));
        } else {
            ct.setTinhTrang(TinhTrangMuon.DUNG_HAN);
            ct.setTienPhat(BigDecimal.ZERO);
        }
    }

    // =====================================================
    // XEM CUA CHINH MINH
    // =====================================================

    /** Các sách sinh viên đang mượn (chưa trả). */
    public List<ChiTietPhieuMuon> xemSachDangMuon(String maSinhVien) throws SQLException {
        String maSv = kiemTraSinhVien(maSinhVien);
        List<ChiTietPhieuMuon> ketQua = new ArrayList<>();
        List<ChiTietPhieuMuon> ds = chiTietDao.findBySinhVien(maSv);
        if (ds != null) {
            for (ChiTietPhieuMuon ct : ds) {
                if (ct.getTinhTrang() == TinhTrangMuon.CHUA_TRA) {
                    ketQua.add(ct);
                }
            }
        }
        return ketQua;
    }

    /** Toàn bộ lịch sử mượn/trả của sinh viên, mới nhất trước. */
    public List<ChiTietPhieuMuon> xemLichSuMuonTra(String maSinhVien) throws SQLException {
        String maSv = kiemTraSinhVien(maSinhVien);
        List<ChiTietPhieuMuon> ds = chiTietDao.findBySinhVien(maSv);
        return ds == null ? new ArrayList<>() : ds;
    }

    // =====================================================
    // DUNG CHUNG
    // =====================================================

    /** Kiểm tra sinh viên tồn tại, trả về mã đã trim. */
    private String kiemTraSinhVien(String maSinhVien) throws SQLException {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không thể trống");
        }
        String ma = maSinhVien.trim();
        if (svDao.findbyId(ma) == null) {
            throw new IllegalArgumentException("Mã sinh viên " + ma + " không tồn tại!");
        }
        return ma;
    }

    /** Danh sách mã sách phải không rỗng, không có phần tử trống, không trùng. */
    private List<String> chuanHoaDanhSachMaSach(List<String> dsMaSach) {
        if (dsMaSach == null || dsMaSach.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một cuốn sách");
        }
        List<String> ketQua = new ArrayList<>();
        Set<String> daCo = new HashSet<>();
        for (String ma : dsMaSach) {
            if (ma == null || ma.trim().isEmpty()) {
                throw new IllegalArgumentException("Có mã sách bị trống trong danh sách");
            }
            String m = ma.trim();
            if (!daCo.add(m)) {
                throw new IllegalArgumentException("Sách " + m + " bị chọn trùng!");
            }
            ketQua.add(m);
        }
        return ketQua;
    }

    /** Sinh mã phiếu dạng PM + 10 chữ số (tối đa 12 ký tự theo DB), đảm bảo chưa tồn tại. */
    private String taoMaPhieu() throws SQLException {
        for (int i = 0; i < 10; i++) {
            long so = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
            String ma = "PM" + so;
            if (phieuMuonDao.findById(ma) == null) {
                return ma;
            }
        }
        throw new IllegalStateException("Không tạo được mã phiếu mượn, vui lòng thử lại!");
    }

    /** Các hàm search của SachDAO không nạp tác giả, nên nạp đầy đủ để UI hiển thị. */
    private List<Sach> napTacGia(List<Sach> ds) throws SQLException {
        List<Sach> ketQua = new ArrayList<>();
        if (ds == null) return ketQua;
        for (Sach s : ds) {
            Sach day = sachDao.findbyId(s.getMaSach());
            ketQua.add(day != null ? day : s);
        }
        return ketQua;
    }

    private String chuanHoaTuKhoa(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }
}