package Services;

import model.*;
import dao.*;
import enums.Role;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManagerService {

    private static final String REGEX_MA = "[A-Za-z0-9]{6,12}";
    private static final String REGEX_SDT = "0[35789][0-9]{8}";
    private static final String REGEX_EMAIL = "[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}";

    private static final int NAM_XUAT_BAN_MIN = 1901;
    private static final int SO_LUONG_SACH_TOI_DA = 10000;

    private final SinhVienDAO svDao = new SinhVienDAO();
    private final TacGiaDAO tgDao = new TacGiaDAO();
    private final TheLoaiDAO theLoaiDao = new TheLoaiDAO();
    private final SachDAO sachDao = new SachDAO();
    private final PhieuMuonDAO phieuMuonDao = new PhieuMuonDAO();
    private final chiTietPhieuMuonDAO chiTietDao = new chiTietPhieuMuonDAO();
    private final TaiKhoanDAO tkDao = new TaiKhoanDAO();


    public void themSinhVien(SinhVien sv) throws SQLException {
        chuanHoaSinhVien(sv);
        kiemTraDinhDangSinhVien(sv);

        if (svDao.findbyId(sv.getMaSinhVien()) != null) {
            throw new IllegalArgumentException("Mã sinh viên " + sv.getMaSinhVien() + " đã tồn tại!");
        }
        try {
            TaiKhoan tk = new TaiKhoan();
            tk.setMaTaiKhoan(sv.getMaTaiKhoan());
            tk.setUserName(sv.getMaSinhVien());
            tk.setPassword("1234567");
            tk.setRole(Role.STUDENT);
            tkDao.insert(tk);
            svDao.insert(sv);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void suaThongTinSinhVien(SinhVien sv) throws SQLException {
        chuanHoaSinhVien(sv);
        kiemTraDinhDangSinhVien(sv);
        kiemTraTonTaiMaSinhVien(sv.getMaSinhVien());
        try {
            svDao.update(sv);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void xoaSinhVien(String maSinhVien) throws SQLException {
        kiemTraTonTaiMaSinhVien(maSinhVien);
        try {
            svDao.delete(maSinhVien.trim());
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public SinhVien findSinhVienById(String maSinhVien) throws SQLException {
        kiemTraTonTaiMaSinhVien(maSinhVien);
        return svDao.findbyId(maSinhVien.trim());
    }

    public List<SinhVien> searchSinhVien(String keyword, boolean theoMaSv) throws SQLException {
        return svDao.search(chuanHoaTuKhoa(keyword), theoMaSv);
    }

    public List<SinhVien> xemTatCaSinhVien() throws SQLException {
        List<SinhVien> ds = svDao.findAll();
        return ds == null ? new ArrayList<>() : ds;
    }

    public void kiemTraTonTaiMaSinhVien(String maSinhVien) throws SQLException {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không thể trống");
        }
        if (svDao.findbyId(maSinhVien.trim()) == null) {
            throw new IllegalArgumentException("Mã sinh viên " + maSinhVien.trim() + " không tồn tại!");
        }
    }

    public void kiemTraDinhDangSinhVien(SinhVien sv) throws SQLException{
        if (sv == null) {
            throw new IllegalArgumentException("Sinh viên không thể null");
        }

        String ma = sv.getMaSinhVien();
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không thể trống");
        }
        if (!ma.trim().matches(REGEX_MA)) {
            throw new IllegalArgumentException("Mã sinh viên gồm 6 đến 12 ký tự chữ và số, không có dấu cách!");
        }

        kiemTraHoTen(sv.getHo(), "Họ");
        kiemTraHoTen(sv.getTen(), "Tên");

        if (sv.getNgaySinh() == null) {
            throw new IllegalArgumentException("Ngày sinh không thể trống");
        }
        if (sv.getNgaySinh().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không thể ở tương lai!");
        }

        if (sv.getGioiTinh() == null) {
            throw new IllegalArgumentException("Giới tính không thể trống");
        }

        if(sv.getLop() == null || sv.getLop().trim().isEmpty()){
            throw new IllegalArgumentException("Lớp không thể trống"); 
        }

        if(sv.getKhoa() == null || sv.getLop().trim().isEmpty()){
            throw new IllegalArgumentException("Khoa không thể trống");
        }

        String sdt = sv.getSdt();
        if (sdt != null && !sdt.trim().isEmpty() && !sdt.trim().matches(REGEX_SDT)) {
            throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng [03, 05, 07, 08, 09] và đủ 10 số!");
        }

        if(svDao.existsBySdt(sdt, ma)){
            throw new IllegalArgumentException("Số điện thoại này đã được sinh viên khác dùng");
        }

        String email = sv.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            if (email.trim().length() > 100) {
                throw new IllegalArgumentException("Email tối đa 100 ký tự");
            }
            if (!email.trim().matches(REGEX_EMAIL)) {
                throw new IllegalArgumentException("Email không đúng định dạng (ví dụ: ten@gmail.com)!");
            }
        }

        if(svDao.existsByEmail(sdt, ma)){
            throw new IllegalArgumentException("Email này đã được sinh viên khác dùng");
        }

        kiemTraDoDaiTuyChon(sv.getLop(), "Lớp", 50);
        kiemTraDoDaiTuyChon(sv.getKhoa(), "Khoa", 50);

        String maTk = sv.getMaTaiKhoan();
        if (maTk == null || maTk.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tài khoản không thể trống");
        }
        if (maTk.trim().length() > 12) {
            throw new IllegalArgumentException("Mã tài khoản tối đa 12 ký tự");
        }
    }

    private void chuanHoaSinhVien(SinhVien sv) {
        if (sv == null) {
            throw new IllegalArgumentException("Sinh viên không thể null");
        }
        if (sv.getMaSinhVien() != null) sv.setMaSinhVien(sv.getMaSinhVien().trim());
        if (sv.getHo() != null) sv.setHo(chuanHoaKhoangTrang(sv.getHo()));
        if (sv.getTen() != null) sv.setTen(chuanHoaKhoangTrang(sv.getTen()));
        sv.setSdt(chuanHoaTuyChon(sv.getSdt()));
        sv.setEmail(chuanHoaTuyChon(sv.getEmail()));
        if(sv.getLop() != null) sv.setLop(sv.getLop().trim());
        if(sv.getKhoa() != null) sv.setKhoa(sv.getKhoa().trim());
        if (sv.getMaTaiKhoan() != null) sv.setMaTaiKhoan(sv.getMaTaiKhoan().trim());
    }

    public void themTacGia(TacGia tg) throws SQLException {
        chuanHoaTacGia(tg);
        kiemTraDinhDangTacGia(tg);

        if (tgDao.findbyId(tg.getMaTacGia()) != null) {
            throw new IllegalArgumentException("Tác giả với mã " + tg.getMaTacGia() + " đã tồn tại!");
        }
        try {
            tgDao.insert(tg);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void suaThongTinTacGia(TacGia tg) throws SQLException {
        chuanHoaTacGia(tg);
        kiemTraDinhDangTacGia(tg);
        kiemTraTonTaiMaTacGia(tg.getMaTacGia());
        try {
            tgDao.update(tg);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void xoaTacGia(String maTacGia) throws SQLException {
        kiemTraTonTaiMaTacGia(maTacGia);
        try {
            tgDao.delete(maTacGia.trim());
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public TacGia findTacGiaById(String maTacGia) throws SQLException {
        kiemTraTonTaiMaTacGia(maTacGia);
        return tgDao.findbyId(maTacGia.trim());
    }

    public List<TacGia> searchTacGia(String keyword) throws SQLException {
        List<TacGia> ds = tgDao.search(chuanHoaTuKhoa(keyword));
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<TacGia> xemTatCaTacGia() throws SQLException {
        List<TacGia> ds = tgDao.findAll();
        return ds == null ? new ArrayList<>() : ds;
    }

    public void kiemTraTonTaiMaTacGia(String maTacGia) throws SQLException {
        if (maTacGia == null || maTacGia.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tác giả không thể trống");
        }
        if (tgDao.findbyId(maTacGia.trim()) == null) {
            throw new IllegalArgumentException("Mã tác giả " + maTacGia.trim() + " không tồn tại!");
        }
    }

    public void kiemTraDinhDangTacGia(TacGia tg) {
        if (tg == null) {
            throw new IllegalArgumentException("Tác giả không thể null");
        }

        String ma = tg.getMaTacGia();
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tác giả không thể trống");
        }
        if (!ma.trim().matches(REGEX_MA)) {
            throw new IllegalArgumentException("Mã tác giả gồm 6 đến 12 ký tự chữ và số, không có dấu cách!");
        }

        kiemTraHoTen(tg.getHo(), "Họ");
        kiemTraHoTen(tg.getTen(), "Tên");
    }

    private void chuanHoaTacGia(TacGia tg) {
        if (tg == null) {
            throw new IllegalArgumentException("Tác giả không thể null");
        }
        if (tg.getMaTacGia() != null) tg.setMaTacGia(tg.getMaTacGia().trim());
        if (tg.getHo() != null) tg.setHo(chuanHoaKhoangTrang(tg.getHo()));
        if (tg.getTen() != null) tg.setTen(chuanHoaKhoangTrang(tg.getTen()));
    }


    public void themTheLoai(TheLoai t) throws SQLException {
        chuanHoaTheLoai(t);
        kiemTraDinhDangTheLoai(t);

        if (theLoaiDao.findById(t.getMaTheLoai()) != null) {
            throw new IllegalArgumentException("Thể loại với mã " + t.getMaTheLoai() + " đã tồn tại!");
        }
        try {
            theLoaiDao.insert(t);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void suaThongTinTheLoai(TheLoai t) throws SQLException {
        chuanHoaTheLoai(t);
        kiemTraDinhDangTheLoai(t);
        kiemTraTonTaiMaTheLoai(t.getMaTheLoai());
        try {
            theLoaiDao.update(t);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void xoaTheLoai(String maTheLoai) throws SQLException {
        kiemTraTonTaiMaTheLoai(maTheLoai);
        try {
            theLoaiDao.delete(maTheLoai.trim());
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public TheLoai findTheLoaiById(String maTheLoai) throws SQLException {
        kiemTraTonTaiMaTheLoai(maTheLoai);
        return theLoaiDao.findById(maTheLoai.trim());
    }

    public List<TheLoai> searchTheLoai(String keyword) throws SQLException {
        List<TheLoai> ds = theLoaiDao.search(chuanHoaTuKhoa(keyword));
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<TheLoai> xemTatCaTheLoai() throws SQLException {
        List<TheLoai> ds = theLoaiDao.findAll();
        return ds == null ? new ArrayList<>() : ds;
    }

    public void kiemTraTonTaiMaTheLoai(String maTheLoai) throws SQLException {
        if (maTheLoai == null || maTheLoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thể loại không thể trống");
        }
        if (theLoaiDao.findById(maTheLoai.trim()) == null) {
            throw new IllegalArgumentException("Mã thể loại " + maTheLoai.trim() + " không tồn tại!");
        }
    }

    public void kiemTraDinhDangTheLoai(TheLoai t) {
        if (t == null) {
            throw new IllegalArgumentException("Thể loại không thể null");
        }

        String ma = t.getMaTheLoai();
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã thể loại không thể trống");
        }
        if (!ma.trim().matches(REGEX_MA)) {
            throw new IllegalArgumentException("Mã thể loại gồm 6 đến 12 ký tự chữ và số, không có dấu cách!");
        }

        String ten = t.getTenTheLoai();
        if (ten == null || ten.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thể loại không thể trống");
        }
        if (ten.trim().length() > 50) {
            throw new IllegalArgumentException("Tên thể loại tối đa 50 ký tự");
        }
    }

    private void chuanHoaTheLoai(TheLoai t) {
        if (t == null) {
            throw new IllegalArgumentException("Thể loại không thể null");
        }
        if (t.getMaTheLoai() != null) t.setMaTheLoai(t.getMaTheLoai().trim());
        if (t.getTenTheLoai() != null) t.setTenTheLoai(chuanHoaKhoangTrang(t.getTenTheLoai()));
    }

    public void themSach(Sach s) throws SQLException {
        chuanHoaSach(s);
        kiemTraDinhDangSach(s);
        kiemTraThamChieuCuaSach(s);

        if (sachDao.findbyId(s.getMaSach()) != null) {
            throw new IllegalArgumentException("Mã sách " + s.getMaSach() + " đã tồn tại!");
        }
        try {
            sachDao.insert(s);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void suaThongTinSach(Sach s) throws SQLException {
        chuanHoaSach(s);
        kiemTraDinhDangSach(s);
        kiemTraThamChieuCuaSach(s);
        kiemTraTonTaiMaSach(s.getMaSach());
        try {
            sachDao.update(s);
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public void xoaSach(String maSach) throws SQLException {
        kiemTraTonTaiMaSach(maSach);
        if (sachDao.daCoTrongPhieuMuon(maSach.trim())) {
            throw new IllegalArgumentException("Không thể xóa sách đang nằm trong phiếu mượn!");
        }
        try {
            sachDao.delete(maSach.trim());
        } catch (SQLException e) {
            chuyenLoiCSDL(e);
        }
    }

    public Sach findSachById(String maSach) throws SQLException {
        kiemTraTonTaiMaSach(maSach);
        return sachDao.findbyId(maSach.trim());
    }

    public List<Sach> searchSachTheoTen(String keyword) throws SQLException {
        return napTacGia(sachDao.searchByTen(chuanHoaTuKhoa(keyword)));
    }

    public List<Sach> searchSachTheoTacGia(String keyword) throws SQLException {
        return napTacGia(sachDao.searchByTacGia(chuanHoaTuKhoa(keyword)));
    }

    public List<Sach> searchSachTheoTheLoai(String maTheLoai) throws SQLException {
        kiemTraTonTaiMaTheLoai(maTheLoai);
        return napTacGia(sachDao.searchByTheLoai(maTheLoai.trim()));
    }

    public List<Sach> xemTatCaSach() throws SQLException {
        return napTacGia(sachDao.searchByTen(""));
    }

    public void kiemTraTonTaiMaSach(String maSach) throws SQLException {
        if (maSach == null || maSach.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sách không thể trống");
        }
        if (sachDao.findbyId(maSach.trim()) == null) {
            throw new IllegalArgumentException("Mã sách " + maSach.trim() + " không tồn tại!");
        }
    }

    public void kiemTraDinhDangSach(Sach s) {
        if (s == null) {
            throw new IllegalArgumentException("Sách không thể null");
        }

        String ma = s.getMaSach();
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sách không thể trống");
        }
        if (!ma.trim().matches(REGEX_MA)) {
            throw new IllegalArgumentException("Mã sách gồm 6 đến 12 ký tự chữ và số, không có dấu cách!");
        }

        String ten = s.getTenSach();
        if (ten == null || ten.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sách không thể trống");
        }
        if (ten.trim().length() > 50) {
            throw new IllegalArgumentException("Tên sách tối đa 50 ký tự");
        }

        kiemTraDoDaiTuyChon(s.getNhaXuatBan(), "Nhà xuất bản", 50);

        Integer nam = s.getNamXuatBan();
        if (nam != null) {
            int namHienTai = LocalDate.now().getYear();
            if (nam < NAM_XUAT_BAN_MIN || nam > namHienTai) {
                throw new IllegalArgumentException(
                        "Năm xuất bản phải từ " + NAM_XUAT_BAN_MIN + " đến " + namHienTai + "!");
            }
        }

        if (s.getSoLuong() < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }
        if (s.getSoLuong() > SO_LUONG_SACH_TOI_DA) {
            throw new IllegalArgumentException("Số lượng sách tối đa là " + SO_LUONG_SACH_TOI_DA
                    + ", bạn đã nhập " + s.getSoLuong() + "!");
        }

        BigDecimal giaTri = s.getGiaTri();
        if (giaTri != null && giaTri.signum() < 0) {
            throw new IllegalArgumentException("Giá trị sách không được âm");
        }

        if (s.getTrangThai() == null) {
            throw new IllegalArgumentException("Trạng thái sách không thể trống");
        }

        String maTl = s.getMaTheLoai();
        if (maTl != null && !maTl.trim().isEmpty() && maTl.trim().length() > 12) {
            throw new IllegalArgumentException("Mã thể loại tối đa 12 ký tự");
        }

        if (s.getDsTacGia() != null) {
            Set<String> daCo = new HashSet<>();
            for (TacGia tg : s.getDsTacGia()) {
                if (tg == null || tg.getMaTacGia() == null || tg.getMaTacGia().trim().isEmpty()) {
                    throw new IllegalArgumentException("Danh sách tác giả có phần tử thiếu mã tác giả");
                }
                if (!daCo.add(tg.getMaTacGia().trim())) {
                    throw new IllegalArgumentException(
                            "Tác giả " + tg.getMaTacGia().trim() + " bị chọn trùng trong cùng một sách!");
                }
            }
        }
    }

    private void kiemTraThamChieuCuaSach(Sach s) throws SQLException {
        if (s.getMaTheLoai() != null) {
            kiemTraTonTaiMaTheLoai(s.getMaTheLoai());
        }
        if (s.getDsTacGia() != null) {
            for (TacGia tg : s.getDsTacGia()) {
                if (tgDao.findbyId(tg.getMaTacGia()) == null) {
                    throw new IllegalArgumentException("Tác giả " + tg.getMaTacGia() + " không tồn tại!");
                }
            }
        }
    }

    private void chuanHoaSach(Sach s) {
        if (s == null) {
            throw new IllegalArgumentException("Sách không thể null");
        }
        if (s.getMaSach() != null) s.setMaSach(s.getMaSach().trim());
        if (s.getTenSach() != null) s.setTenSach(chuanHoaKhoangTrang(s.getTenSach()));
        s.setNhaXuatBan(chuanHoaTuyChon(s.getNhaXuatBan()));
        s.setMaTheLoai(chuanHoaTuyChon(s.getMaTheLoai()));

        if (s.getDsTacGia() == null) {
            s.setDsTacGia(new ArrayList<>());
        }
        for (TacGia tg : s.getDsTacGia()) {
            if (tg != null && tg.getMaTacGia() != null) {
                tg.setMaTacGia(tg.getMaTacGia().trim());
            }
        }
    }

    private List<Sach> napTacGia(List<Sach> ds) throws SQLException {
        List<Sach> kq = new ArrayList<>();
        if (ds == null) return kq;
        for (Sach s : ds) {
            Sach day = sachDao.findbyId(s.getMaSach());
            kq.add(day != null ? day : s);
        }
        return kq;
    }

    public List<PhieuMuon> xemTatCaPhieuMuon() throws SQLException {
        List<PhieuMuon> ds = phieuMuonDao.findAll();
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<PhieuMuon> xemPhieuMuonCuaSinhVien(String maSinhVien) throws SQLException {
        kiemTraTonTaiMaSinhVien(maSinhVien);
        List<PhieuMuon> ds = phieuMuonDao.findBySinhVien(maSinhVien.trim());
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<ChiTietPhieuMuon> xemChiTietPhieuMuon(String maPhieu) throws SQLException {
        if (maPhieu == null || maPhieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu mượn không thể trống");
        }
        if (phieuMuonDao.findById(maPhieu.trim()) == null) {
            throw new IllegalArgumentException("Mã phiếu mượn " + maPhieu.trim() + " không tồn tại!");
        }
        List<ChiTietPhieuMuon> ds = chiTietDao.findByPhieu(maPhieu.trim());
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<ChiTietPhieuMuon> xemTatCaLichSuMuonTra() throws SQLException {
        List<ChiTietPhieuMuon> ds = chiTietDao.findAll();
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<ChiTietPhieuMuon> xemLichSuMuonTraCuaSinhVien(String maSinhVien) throws SQLException {
        kiemTraTonTaiMaSinhVien(maSinhVien);
        List<ChiTietPhieuMuon> ds = chiTietDao.findBySinhVien(maSinhVien.trim());
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<ChiTietPhieuMuon> xemSachDangMuon() throws SQLException {
        List<ChiTietPhieuMuon> ds = chiTietDao.findDangMuon();
        return ds == null ? new ArrayList<>() : ds;
    }

    public List<ChiTietPhieuMuon> xemSachQuaHan() throws SQLException {
        List<ChiTietPhieuMuon> ds = chiTietDao.findQuaHan();
        return ds == null ? new ArrayList<>() : ds;
    }

    public int demSachDangMuon(String maSinhVien) throws SQLException {
        kiemTraTonTaiMaSinhVien(maSinhVien);
        return chiTietDao.demDangMuon(maSinhVien.trim());
    }

    public BigDecimal tongTienPhat() throws SQLException {
        BigDecimal tong = chiTietDao.tongTienPhat();
        return tong == null ? BigDecimal.ZERO : tong;
    }

    public void kiemTraHoTen(String giaTri, String nhan) {
        if (giaTri == null || giaTri.trim().isEmpty()) {
            throw new IllegalArgumentException(nhan + " không thể trống");
        }
        String s = chuanHoaKhoangTrang(giaTri);
        if (s.length() > 50) {
            throw new IllegalArgumentException(nhan + " tối đa 50 ký tự");
        }
        if (!s.matches("\\p{L}+( \\p{L}+)*")) {
            throw new IllegalArgumentException(nhan + " chỉ được chứa chữ cái và dấu cách!");
        }
    }

    private void kiemTraDoDaiTuyChon(String giaTri, String nhan, int toiDa) {
        if (giaTri != null && giaTri.trim().length() > toiDa) {
            throw new IllegalArgumentException(nhan + " tối đa " + toiDa + " ký tự");
        }
    }

    private String chuanHoaKhoangTrang(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private String chuanHoaTuyChon(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private String chuanHoaTuKhoa(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }


    private void chuyenLoiCSDL(SQLException e) throws SQLException {
        int code = e.getErrorCode();
        String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (code == 1062) { // trung gia tri UNIQUE / PRIMARY KEY
            if (msg.contains("uk_sinhvien_email")) {
                throw new IllegalArgumentException("Email này đã được sinh viên khác sử dụng!");
            }
            if (msg.contains("uk_sinhvien_sdt")) {
                throw new IllegalArgumentException("Số điện thoại này đã được sinh viên khác sử dụng!");
            }
            if (msg.contains("uk_sinhvien_taikhoan")) {
                throw new IllegalArgumentException("Tài khoản này đã được gắn với sinh viên khác!");
            }
            throw new IllegalArgumentException("Dữ liệu bị trùng với bản ghi đã có!");
        }

        if (code == 1452) { // khoa ngoai tro toi ban ghi khong ton tai
            if (msg.contains("fk_ma_the_loai")) {
                throw new IllegalArgumentException("Mã thể loại không tồn tại!");
            }
            if (msg.contains("fk_sinhvien_taikhoan")) {
                throw new IllegalArgumentException("Mã tài khoản không tồn tại!");
            }
            if (msg.contains("fk_ma_tac_gia")) {
                throw new IllegalArgumentException("Có mã tác giả không tồn tại!");
            }
            throw new IllegalArgumentException("Dữ liệu tham chiếu không tồn tại!");
        }

        if (code == 1451) { // xoa ban ghi dang duoc tham chieu
            if (msg.contains("fk_chi_tiet_sach")) {
                throw new IllegalArgumentException("Không thể xóa sách đang nằm trong phiếu mượn!");
            }
            if (msg.contains("fk_ma_sinh_vien")) {
                throw new IllegalArgumentException("Không thể xóa sinh viên đã có phiếu mượn!");
            }
            throw new IllegalArgumentException("Dữ liệu đang được sử dụng ở nơi khác nên không thể xóa!");
        }

        throw e;
    }
}