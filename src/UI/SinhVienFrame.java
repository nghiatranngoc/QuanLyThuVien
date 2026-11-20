package UI;

import Services.SinhVienService;
import dao.PhieuMuonDAO;
import dao.SinhVienDAO;
import dao.TaiKhoanDAO;
//import enums.TinhTrangMuon;
import model.ChiTietPhieuMuon;
import model.PhieuMuon;
import model.Sach;
import model.SinhVien;
import model.TacGia;
import model.TaiKhoan;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SinhVienFrame extends JFrame {
    private final String userName;
    private SinhVien sinhVienHienTai;

    private final TaiKhoanDAO tkDao = new TaiKhoanDAO();
    private final SinhVienDAO svDao = new SinhVienDAO();
    private final PhieuMuonDAO phieuMuonDao = new PhieuMuonDAO();
    private final SinhVienService svService = new SinhVienService();

    private DefaultTableModel modelSachDaMuon;
    private JTable bangSachDaMuon;

    private DefaultTableModel modelKhoSach;
    private JTable bangKhoSach;

    private JTextField txtTimKiem;
    private JComboBox<String> cbTieuChi;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SinhVienFrame(String userName) throws SQLException {
        super("Hệ thống thư viện - Sinh Viên");
        this.userName = userName;

        TaiKhoan tk = tkDao.findbyUserName(userName);
        if (tk != null) {
            sinhVienHienTai = svDao.findbyTaiKhoan(tk.getMaTaiKhoan());
        }
        if (sinhVienHienTai == null) {
            sinhVienHienTai = svDao.findbyId(userName);
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(1080, 720));
        setJMenuBar(taoMenu());
        setContentPane(giaoDienChinh());
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JMenuBar taoMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuHeThong = new JMenu("Hệ thống");

        JMenuItem itemTrangChu = new JMenuItem("Trang chủ mượn trả");
        itemTrangChu.addActionListener(e -> {
            setSize(new Dimension(1080, 720));
            setContentPane(giaoDienChinh());
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });

        JMenuItem itemDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        itemDoiMatKhau.addActionListener(e -> {
            setContentPane(giaoDienDoiMatKhau());
            pack();
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });

        JMenuItem itemDangXuat = new JMenuItem("Đăng xuất");
        itemDangXuat.addActionListener(e -> {
            dispose();
            new PhanQuyenFrame().setVisible(true);
        });

        menuHeThong.add(itemTrangChu);
        menuHeThong.add(itemDoiMatKhau);
        menuHeThong.add(itemDangXuat);
        menuBar.add(menuHeThong);
        return menuBar;
    }

    private JPanel giaoDienChinh() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        GridBagConstraints c = new GridBagConstraints();

        // 1. NUA TREN: Trai (Thong tin SV) + Phai (Danh sach sach da muon)
        JPanel phanTren = new JPanel(new GridBagLayout());
        GridBagConstraints cTren = new GridBagConstraints();

        // 1.1 Khung Thong tin ca nhan (Goc tren ben trai)
        JPanel khungCaNhan = new taoKhung().panel();
        khungCaNhan.setBackground(new Color(220, 235, 250));
        GridBagConstraints cCn = new GridBagConstraints();
        cCn.insets = new Insets(6, 10, 6, 10);
        cCn.anchor = GridBagConstraints.WEST;

        cCn.gridx = 0;
        cCn.gridy = 0;
        cCn.gridwidth = 2;
        JLabel lbTieuDeSv = new taoDongNoiDung().Label("Thông tin sinh viên", "Arial", Font.BOLD, 16);
        khungCaNhan.add(lbTieuDeSv, cCn);

        cCn.gridwidth = 1;
        String maSv = sinhVienHienTai != null ? sinhVienHienTai.getMaSinhVien() : userName;
        String hoTen = sinhVienHienTai != null ? (sinhVienHienTai.getHo() + " " + sinhVienHienTai.getTen()) : "Chưa cập nhật";
        String ngaySinh = (sinhVienHienTai != null && sinhVienHienTai.getNgaySinh() != null)
                ? sinhVienHienTai.getNgaySinh().format(DATE_FMT) : "";
        String lop = sinhVienHienTai != null ? sinhVienHienTai.getLop() : "";
        String khoa = sinhVienHienTai != null ? sinhVienHienTai.getKhoa() : "";

        themDongThongTin(khungCaNhan, cCn, 1, "Mã SV:", maSv);
        themDongThongTin(khungCaNhan, cCn, 2, "Họ và tên:", hoTen);
        themDongThongTin(khungCaNhan, cCn, 3, "Ngày sinh:", ngaySinh);
        themDongThongTin(khungCaNhan, cCn, 4, "Lớp:", lop);
        themDongThongTin(khungCaNhan, cCn, 5, "Chuyên ngành:", khoa);

        cTren.gridx = 0;
        cTren.gridy = 0;
        cTren.fill = GridBagConstraints.BOTH;
        cTren.weightx = 0.32;
        cTren.weighty = 1;
        cTren.insets = new Insets(0, 0, 0, 10);
        phanTren.add(khungCaNhan, cTren);

        // 1.2 Khung Danh sach nhung quyen sach da muon (Goc tren ben phai)
        JPanel khungDaMuon = new taoKhung().panel();
        GridBagConstraints cDm = new GridBagConstraints();

        cDm.gridx = 0;
        cDm.gridy = 0;
        cDm.insets = new Insets(0, 0, 6, 0);
        JLabel lbDaMuon = new taoDongNoiDung().Label("Danh sách những quyển sách đã mượn", "Arial", Font.BOLD, 15);
        khungDaMuon.add(lbDaMuon, cDm);

        String[] cotDaMuon = {"STT", "Mã Phiếu", "Mã Sách", "Tên Sách", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Tiền Phạt", "Trạng Thái"};
        modelSachDaMuon = new DefaultTableModel(cotDaMuon, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangSachDaMuon = new JTable(modelSachDaMuon);
        bangSachDaMuon.getTableHeader().setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        bangSachDaMuon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cuonDaMuon = new JScrollPane(bangSachDaMuon);
        cuonDaMuon.getViewport().setBackground(Color.WHITE);

        cDm.gridy = 1;
        cDm.fill = GridBagConstraints.BOTH;
        cDm.weightx = 1;
        cDm.weighty = 1;
        khungDaMuon.add(cuonDaMuon, cDm);

        JButton btnTraSach = new taoNut().Button("Trả sách");
        btnTraSach.addActionListener(e -> xuLyTraSach());
        cDm.gridy = 2;
        cDm.fill = GridBagConstraints.NONE;
        cDm.weightx = 0;
        cDm.weighty = 0;
        cDm.insets = new Insets(8, 0, 0, 0);
        khungDaMuon.add(btnTraSach, cDm);

        cTren.gridx = 1;
        cTren.gridy = 0;
        cTren.fill = GridBagConstraints.BOTH;
        cTren.weightx = 0.68;
        cTren.weighty = 1;
        cTren.insets = new Insets(0, 0, 0, 0);
        phanTren.add(khungDaMuon, cTren);

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.45;
        p.add(phanTren, c);

        // 2. THANH TIM KIEM SACH O GIUA
        JPanel thanhTimKiem = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        thanhTimKiem.add(new taoDongNoiDung().Label("Tìm kiếm:", "Arial", Font.BOLD, 14));
        txtTimKiem = new JTextField(25);
        thanhTimKiem.add(txtTimKiem);

        JButton btnTim = new taoNut().Button("Tìm kiếm");
        btnTim.addActionListener(e -> xuLyTimSach());
        thanhTimKiem.add(btnTim);

        cbTieuChi = new JComboBox<>(new String[]{"Tên sách", "Mã sách", "Tên tác giả"});
        thanhTimKiem.add(cbTieuChi);

        JButton btnTatCa = new taoNut().Button("Tất cả sách");
        btnTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            taiDanhSachKhoSach();
        });
        thanhTimKiem.add(btnTatCa);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.insets = new Insets(6, 0, 6, 0);
        p.add(thanhTimKiem, c);

        // 3. NUA DUOI: Bang Kho Sach Thu Vien + Nut Muon Sach
        String[] cotKhoSach = {"STT", "Mã Sách", "Tên Sách", "Thể Loại", "Tác Giả", "Nhà Xuất Bản", "Năm XB", "Giá Sách", "Số Lượng Còn", "Trạng Thái"};
        modelKhoSach = new DefaultTableModel(cotKhoSach, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangKhoSach = new JTable(modelKhoSach);
        bangKhoSach.getTableHeader().setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        bangKhoSach.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cuonKhoSach = new JScrollPane(bangKhoSach);
        cuonKhoSach.getViewport().setBackground(Color.WHITE);

        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.55;
        c.insets = new Insets(0, 0, 8, 0);
        p.add(cuonKhoSach, c);

        JButton btnMuonSach = new taoNut().Button("Mượn sách");
        btnMuonSach.addActionListener(e -> xuLyMuonSach());
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        p.add(btnMuonSach, c);

        taiDanhSachSachDaMuon();
        taiDanhSachKhoSach();
        return p;
    }

    private void themDongThongTin(JPanel panel, GridBagConstraints c, int row, String nhan, String giaTri) {
        c.gridx = 0;
        c.gridy = row;
        panel.add(new taoDongNoiDung().Label(nhan, "Arial", Font.BOLD, 13), c);
        c.gridx = 1;
        panel.add(new taoDongNoiDung().Label(giaTri, "Arial", Font.PLAIN, 13), c);
    }

    private void xuLyTimSach() {
        String kw = txtTimKiem.getText().trim();
        String tieuChi = (String) cbTieuChi.getSelectedItem();
        try {
            List<Sach> ds;
            if ("Tên tác giả".equals(tieuChi)) {
                ds = svService.searchSachTheoTacGia(kw);
            } else {
                ds = svService.searchSachTheoTen(kw);
                if ("Mã sách".equals(tieuChi) && !kw.isEmpty()) {
                    List<Sach> locMa = new ArrayList<>();
                    for (Sach s : ds) {
                        if (s.getMaSach().toLowerCase().contains(kw.toLowerCase())) {
                            locMa.add(s);
                        }
                    }
                    ds = locMa;
                }
            }
            doDuLieuKhoSach(ds);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm sách: " + ex.getMessage());
        }
    }

    private void xuLyMuonSach() {
        if (sinhVienHienTai == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ sinh viên gắn với tài khoản này!");
            return;
        }
        int row = bangKhoSach.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách bên dưới để mượn!");
            return;
        }

        String maSach = String.valueOf(modelKhoSach.getValueAt(row, 1));
        String tenSach = String.valueOf(modelKhoSach.getValueAt(row, 2));

        try {
            // Kiem tra trung sach dang muon
            List<ChiTietPhieuMuon> dangMuon = svService.xemSachDangMuon(sinhVienHienTai.getMaSinhVien());
            for (ChiTietPhieuMuon ct : dangMuon) {
                if (ct.getMaSach().equalsIgnoreCase(maSach)) {
                    JOptionPane.showMessageDialog(this, "Bạn đang mượn cuốn sách \"" + tenSach + "\" (" + maSach + ") chưa trả!");
                    return;
                }
            }

            int conDuocMuon = svService.soSachConDuocMuon(sinhVienHienTai.getMaSinhVien());
            LocalDate hanTraMacDinh = LocalDate.now().plusDays(SinhVienService.SO_NGAY_MUON);

            int xacNhan = JOptionPane.showConfirmDialog(
                    this,
                    "Xác nhận mượn sách:\n"
                            + "- Mã sách: " + maSach + "\n"
                            + "- Tên sách: " + tenSach + "\n"
                            + "- Hạn trả: " + hanTraMacDinh.format(DATE_FMT) + " (" + SinhVienService.SO_NGAY_MUON + " ngày)\n"
                            + "- Số sách còn được phép mượn thêm hiện tại: " + conDuocMuon + " cuốn",
                    "Xác nhận mượn sách",
                    JOptionPane.YES_NO_OPTION
            );

            if (xacNhan != JOptionPane.YES_OPTION) return;

            List<String> dsMa = new ArrayList<>();
            dsMa.add(maSach);
            PhieuMuon phieu = svService.muonSach(sinhVienHienTai.getMaSinhVien(), dsMa);
            JOptionPane.showMessageDialog(this, "Mượn sách thành công! Mã phiếu mượn: " + phieu.getMaPhieu());

            taiDanhSachSachDaMuon();
            taiDanhSachKhoSach();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void xuLyTraSach() {
        if (sinhVienHienTai == null) return;
        int row = bangSachDaMuon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách trong bảng 'Sách đã mượn' để trả!");
            return;
        }

        String maPhieu = String.valueOf(modelSachDaMuon.getValueAt(row, 1));
        String maSach = String.valueOf(modelSachDaMuon.getValueAt(row, 2));
        String tenSach = String.valueOf(modelSachDaMuon.getValueAt(row, 3));
        String trangThai = String.valueOf(modelSachDaMuon.getValueAt(row, 8));

        if (!"CHUA_TRA".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Sách này đã được trả rồi!");
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn trả cuốn sách \"" + tenSach + "\" (" + maSach + ")?",
                "Xác nhận trả sách",
                JOptionPane.YES_NO_OPTION
        );
        if (xacNhan != JOptionPane.YES_OPTION) return;

        try {
            ChiTietPhieuMuon kq = svService.traSach(sinhVienHienTai.getMaSinhVien(), maPhieu, maSach);
            String msg = "Trả sách thành công!\nTình trạng: " + kq.getTinhTrang();
            if (kq.getTienPhat() != null && kq.getTienPhat().compareTo(BigDecimal.ZERO) > 0) {
                msg += "\nTiền phạt quá hạn: " + kq.getTienPhat() + " VNĐ";
            }
            JOptionPane.showMessageDialog(this, msg);

            taiDanhSachSachDaMuon();
            taiDanhSachKhoSach();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void taiDanhSachSachDaMuon() {
        modelSachDaMuon.setRowCount(0);
        if (sinhVienHienTai == null) return;
        try {
            List<PhieuMuon> dsPhieu = phieuMuonDao.findBySinhVien(sinhVienHienTai.getMaSinhVien());
            Map<String, PhieuMuon> mapPhieu = new HashMap<>();
            for (PhieuMuon p : dsPhieu) {
                mapPhieu.put(p.getMaPhieu(), p);
            }

            List<Sach> tatCaSach = svService.xemTatCaSach();
            Map<String, String> mapTenSach = new HashMap<>();
            for (Sach s : tatCaSach) {
                mapTenSach.put(s.getMaSach(), s.getTenSach());
            }

            List<ChiTietPhieuMuon> dsChiTiet = svService.xemLichSuMuonTra(sinhVienHienTai.getMaSinhVien());
            int stt = 1;
            for (ChiTietPhieuMuon ct : dsChiTiet) {
                PhieuMuon p = mapPhieu.get(ct.getMaPhieu());
                String ngayMuon = (p != null && p.getNgayMuon() != null) ? p.getNgayMuon().format(DATE_FMT) : "";
                String tenSach = mapTenSach.getOrDefault(ct.getMaSach(), "");

                modelSachDaMuon.addRow(new Object[]{
                        stt++,
                        ct.getMaPhieu(),
                        ct.getMaSach(),
                        tenSach,
                        ngayMuon,
                        ct.getHanTra() != null ? ct.getHanTra().format(DATE_FMT) : "",
                        ct.getNgayTra() != null ? ct.getNgayTra().format(DATE_FMT) : "Chưa trả",
                        ct.getTienPhat() != null ? ct.getTienPhat() : BigDecimal.ZERO,
                        ct.getTinhTrang()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void taiDanhSachKhoSach() {
        try {
            List<Sach> ds = svService.xemTatCaSach();
            doDuLieuKhoSach(ds);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void doDuLieuKhoSach(List<Sach> ds) {
        modelKhoSach.setRowCount(0);
        int stt = 1;
        for (Sach s : ds) {
            StringBuilder tgStr = new StringBuilder();
            if (s.getDsTacGia() != null) {
                for (TacGia tg : s.getDsTacGia()) {
                    if (tgStr.length() > 0) tgStr.append(", ");
                    tgStr.append(tg.getHo()).append(" ").append(tg.getTen());
                }
            }
            modelKhoSach.addRow(new Object[]{
                    stt++,
                    s.getMaSach(),
                    s.getTenSach(),
                    s.getMaTheLoai(),
                    tgStr.toString(),
                    s.getNhaXuatBan(),
                    s.getNamXuatBan(),
                    s.getGiaTri(),
                    s.getSoLuong(),
                    s.getTrangThai()
            });
        }
    }

    private JPanel giaoDienDoiMatKhau() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 10, 0);
        JLabel tieuDe = new taoTieuDe().label("ĐỔI MẬT KHẨU");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(tieuDe, c);

        JPanel khung = new taoKhung().panel();
        GridBagConstraints cK = new GridBagConstraints();
        cK.insets = new Insets(6, 6, 6, 6);

        JPasswordField txtCu = new JPasswordField(15);
        JPasswordField txtMoi = new JPasswordField(15);
        JPasswordField txtNhapLai = new JPasswordField(15);

        cK.gridx = 0; cK.gridy = 0; cK.anchor = GridBagConstraints.EAST;
        khung.add(new taoDongNoiDung().Label("Mật khẩu cũ:", "Arial", Font.BOLD, 14), cK);
        cK.gridx = 1; cK.anchor = GridBagConstraints.WEST;
        khung.add(txtCu, cK);

        cK.gridx = 0; cK.gridy = 1; cK.anchor = GridBagConstraints.EAST;
        khung.add(new taoDongNoiDung().Label("Mật khẩu mới:", "Arial", Font.BOLD, 14), cK);
        cK.gridx = 1; cK.anchor = GridBagConstraints.WEST;
        khung.add(txtMoi, cK);

        cK.gridx = 0; cK.gridy = 2; cK.anchor = GridBagConstraints.EAST;
        khung.add(new taoDongNoiDung().Label("Nhập lại mật khẩu mới:", "Arial", Font.BOLD, 14), cK);
        cK.gridx = 1; cK.anchor = GridBagConstraints.WEST;
        khung.add(txtNhapLai, cK);

        JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 8));
        JButton btnLuu = new taoNut().Button("Thay đổi");
        JButton btnHuy = new taoNut().Button("Quay lại");

        btnLuu.addActionListener(e -> {
            try {
                String cu = new String(txtCu.getPassword());
                String moi = new String(txtMoi.getPassword());
                String lai = new String(txtNhapLai.getPassword());
                TaiKhoan tk = tkDao.findbyUserName(userName);
                if (tk == null || !cu.equals(tk.getPassword())) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!");
                    return;
                }
                if (moi.isEmpty() || !moi.equals(lai)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu mới và nhập lại không khớp!");
                    return;
                }
                tk.setPassword(moi);
                tkDao.update(tk);
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                setSize(new Dimension(1080, 720));
                setContentPane(giaoDienChinh());
                setLocationRelativeTo(null);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });

        btnHuy.addActionListener(e -> {
            setSize(new Dimension(1080, 720));
            setContentPane(giaoDienChinh());
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });

        hangNut.add(btnLuu);
        hangNut.add(btnHuy);

        cK.gridx = 0; cK.gridy = 3; cK.gridwidth = 2; cK.anchor = GridBagConstraints.CENTER;
        khung.add(hangNut, cK);

        c.gridy = 1;
        p.add(khung, c);
        return p;
    }
}