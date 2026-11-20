package UI;

import Services.ManagerService;
import Services.SinhVienService;
import dao.PhieuMuonDAO;
import dao.SachDAO;
import dao.SinhVienDAO;
import dao.chiTietPhieuMuonDAO;
//import database.DBConnection;
import enums.TinhTrangMuon;
import model.ChiTietPhieuMuon;
import model.PhieuMuon;
import model.Sach;
import model.SinhVien;
import model.TacGia;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
//import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuanLyMuonTraPanel extends JPanel {
    private final ManagerService managerService = new ManagerService();
    private final SinhVienService svService = new SinhVienService();
    private final PhieuMuonDAO phieuMuonDao = new PhieuMuonDAO();
    private final chiTietPhieuMuonDAO chiTietDao = new chiTietPhieuMuonDAO();
    private final SinhVienDAO svDao = new SinhVienDAO();
    private final SachDAO sachDao = new SachDAO();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Components Tab 1: Quan ly muon tra
    private DefaultTableModel modelMuonTra;
    private JTable bangMuonTra;
    private JTextField txtMaSv;
    private JLabel lbKetQuaSv;
    private JTextField txtMaSach;
    private JLabel lbKetQuaSach;
    private JTextField txtHanTra;
    private JRadioButton rdTimTheoMaSv;
    private JRadioButton rdTimTheoMaSach;
    private JTextField txtTuKhoa;

    // Components Tab 2: Danh sach qua han va thong ke
    private DefaultTableModel modelQuaHan;
    private JTable bangQuaHan;
    private JLabel lbTongSach;
    private JLabel lbSachDangMuon;
    private JLabel lbSachConKho;
    private JLabel lbTongTienPhat;

    public QuanLyMuonTraPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("Quản Lý Mượn Trả", taoTabQuanLyMuonTra());
        tabbedPane.addTab("Danh Sách Quá Hạn Và Thống Kê", taoTabQuaHanVaThongKe());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                taiDuLieuBangMuonTra(null, true);
            } else {
                taiDuLieuThongKeVaQuaHan();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================
    // TAB 1: QUAN LY MUON TRA
    // =========================================================
    private JPanel taoTabQuanLyMuonTra() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        GridBagConstraints c = new GridBagConstraints();

        // 1. Khung Tim Kiem
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 10, 0, 0);
        p.add(new taoDongNoiDung().Label("Tìm kiếm", "Arial", Font.BOLD, 13), c);

        JPanel khungTimKiem = new taoKhung().panel();
        GridBagConstraints cTk = new GridBagConstraints();
        ButtonGroup grTim = new ButtonGroup();
        rdTimTheoMaSv = new JRadioButton("Theo mã sinh viên mượn sách", true);
        rdTimTheoMaSach = new JRadioButton("Theo mã sách được mượn");
        rdTimTheoMaSv.setFocusPainted(false);
        rdTimTheoMaSach.setFocusPainted(false);
        grTim.add(rdTimTheoMaSv);
        grTim.add(rdTimTheoMaSach);

        cTk.gridx = 0;
        cTk.gridy = 0;
        cTk.anchor = GridBagConstraints.WEST;
        khungTimKiem.add(rdTimTheoMaSv, cTk);

        cTk.gridy = 1;
        khungTimKiem.add(rdTimTheoMaSach, cTk);

        txtTuKhoa = new JTextField(22);
        cTk.gridx = 1;
        cTk.gridy = 0;
        cTk.gridheight = 2;
        cTk.fill = GridBagConstraints.HORIZONTAL;
        cTk.weightx = 1;
        cTk.insets = new Insets(0, 15, 0, 15);
        khungTimKiem.add(txtTuKhoa, cTk);

        JButton btnTimKiem = new taoNut().Button("Tìm kiếm");
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        cTk.gridx = 2;
        cTk.fill = GridBagConstraints.NONE;
        cTk.weightx = 0;
        cTk.insets = new Insets(0, 0, 0, 0);
        khungTimKiem.add(btnTimKiem, cTk);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 10, 8, 10);
        p.add(khungTimKiem, c);

        // 2. Khung Thong Tin Muon Sach
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.insets = new Insets(4, 10, 0, 0);
        p.add(new taoDongNoiDung().Label("Thông tin mượn sách", "Arial", Font.BOLD, 13), c);

        JPanel khungThongTin = new taoKhung().panel();
        GridBagConstraints cTt = new GridBagConstraints();
        cTt.insets = new Insets(4, 8, 4, 8);

        // Dong 1: Ma sinh vien + nut Kiem tra
        cTt.gridx = 0;
        cTt.gridy = 0;
        cTt.anchor = GridBagConstraints.WEST;
        khungThongTin.add(new taoDongNoiDung().label("Mã sinh viên:"), cTt);

        txtMaSv = new JTextField(18);
        cTt.gridx = 1;
        cTt.fill = GridBagConstraints.HORIZONTAL;
        cTt.weightx = 1;
        khungThongTin.add(txtMaSv, cTt);

        JButton btnKiemTraSv = new taoNut().Button("Kiểm tra");
        btnKiemTraSv.addActionListener(e -> kiemTraSinhVien());
        cTt.gridx = 2;
        cTt.fill = GridBagConstraints.NONE;
        cTt.weightx = 0;
        khungThongTin.add(btnKiemTraSv, cTt);

        // Dong 2: Ket qua kiem tra SV
        lbKetQuaSv = new JLabel(" ");
        lbKetQuaSv.setFont(new Font("Arial", Font.ITALIC, 12));
        lbKetQuaSv.setForeground(new Color(0, 102, 204));
        cTt.gridx = 1;
        cTt.gridy = 1;
        cTt.gridwidth = 2;
        cTt.anchor = GridBagConstraints.WEST;
        khungThongTin.add(lbKetQuaSv, cTt);

        // Dong 3: Ma sach + nut Kiem tra
        cTt.gridx = 0;
        cTt.gridy = 2;
        cTt.gridwidth = 1;
        khungThongTin.add(new taoDongNoiDung().label("Mã sách:"), cTt);

        txtMaSach = new JTextField(18);
        cTt.gridx = 1;
        cTt.fill = GridBagConstraints.HORIZONTAL;
        cTt.weightx = 1;
        khungThongTin.add(txtMaSach, cTt);

        JButton btnKiemTraSach = new taoNut().Button("Kiểm tra");
        btnKiemTraSach.addActionListener(e -> kiemTraSach());
        cTt.gridx = 2;
        cTt.fill = GridBagConstraints.NONE;
        cTt.weightx = 0;
        khungThongTin.add(btnKiemTraSach, cTt);

        // Dong 4: Ket qua kiem tra Sach
        lbKetQuaSach = new JLabel(" ");
        lbKetQuaSach.setFont(new Font("Arial", Font.ITALIC, 12));
        lbKetQuaSach.setForeground(new Color(0, 102, 204));
        cTt.gridx = 1;
        cTt.gridy = 3;
        cTt.gridwidth = 2;
        cTt.anchor = GridBagConstraints.WEST;
        khungThongTin.add(lbKetQuaSach, cTt);

        // Dong 5: Ngay hen tra
        cTt.gridx = 0;
        cTt.gridy = 4;
        cTt.gridwidth = 1;
        khungThongTin.add(new taoDongNoiDung().label("Ngày hẹn trả (dd/MM/yyyy):"), cTt);

        txtHanTra = new JTextField(LocalDate.now().plusDays(SinhVienService.SO_NGAY_MUON).format(DATE_FMT), 18);
        cTt.gridx = 1;
        cTt.fill = GridBagConstraints.HORIZONTAL;
        cTt.weightx = 1;
        khungThongTin.add(txtHanTra, cTt);

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 10, 8, 10);
        p.add(khungThongTin, c);

        // 3. Bang Danh Sach Muon Tra
        String[] cot = {"STT", "Mã Phiếu", "Mã Sinh Viên", "Mã Sách", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Tiền Phạt", "Trạng Thái"};
        modelMuonTra = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangMuonTra = new JTable(modelMuonTra);
        JTableHeader th = bangMuonTra.getTableHeader();
        th.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        bangMuonTra.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bangMuonTra.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bangMuonTra.getSelectedRow();
                if (row >= 0) {
                    txtMaSv.setText(String.valueOf(modelMuonTra.getValueAt(row, 2)));
                    txtMaSach.setText(String.valueOf(modelMuonTra.getValueAt(row, 3)));
                    txtHanTra.setText(String.valueOf(modelMuonTra.getValueAt(row, 5)));
                    lbKetQuaSv.setText(" ");
                    lbKetQuaSach.setText(" ");
                }
            }
        });

        JScrollPane cuon = new JScrollPane(bangMuonTra);
        cuon.getViewport().setBackground(Color.WHITE);

        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 10, 8, 10);
        p.add(cuon, c);

        // 4. Khung Chuc Nang
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(4, 10, 0, 0);
        p.add(new taoDongNoiDung().Label("Chức năng", "Arial", Font.BOLD, 13), c);

        JPanel khungChucNang = new taoKhung().panel();
        JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 5));

        JButton btnMuonSach = new taoNut().Button("Mượn sách");
        JButton btnSua = new taoNut().Button("Sửa thông tin");
        JButton btnTraSach = new taoNut().Button("Trả sách");
        JButton btnXoa = new taoNut().Button("Xóa");
        JButton btnLamMoi = new taoNut().Button("Làm mới");

        btnMuonSach.addActionListener(e -> xuLyMuonSach());
        btnSua.addActionListener(e -> xuLySuaThongTin());
        btnTraSach.addActionListener(e -> xuLyTraSach());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnLamMoi.addActionListener(e -> lamMoiTabMuonTra());

        hangNut.add(btnMuonSach);
        hangNut.add(btnSua);
        hangNut.add(btnTraSach);
        hangNut.add(btnXoa);
        hangNut.add(btnLamMoi);

        GridBagConstraints cCn = new GridBagConstraints();
        cCn.gridx = 0;
        cCn.gridy = 0;
        cCn.fill = GridBagConstraints.HORIZONTAL;
        cCn.weightx = 1;
        khungChucNang.add(hangNut, cCn);

        c.gridx = 0;
        c.gridy = 6;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(khungChucNang, c);

        taiDuLieuBangMuonTra(null, true);
        return p;
    }

    // =========================================================
    // TAB 2: DANH SACH QUA HAN VA THONG KE
    // =========================================================
    private JPanel taoTabQuaHanVaThongKe() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();

        // Khung Thong Ke
        JPanel khungThongKe = new taoKhung().panel();
        GridBagConstraints cTk = new GridBagConstraints();
        cTk.insets = new Insets(6, 20, 6, 20);
        cTk.anchor = GridBagConstraints.WEST;

        Font fontThongKe = new Font("Arial", Font.BOLD, 18);
        Color mauChu = new Color(40, 55, 71);

        lbTongSach = new JLabel("Tổng số lượng sách: 0");
        lbTongSach.setFont(fontThongKe);
        lbTongSach.setForeground(mauChu);

        lbSachDangMuon = new JLabel("Tổng số lượng sách đang mượn: 0");
        lbSachDangMuon.setFont(fontThongKe);
        lbSachDangMuon.setForeground(mauChu);

        lbSachConKho = new JLabel("Tổng số lượng sách còn trong kho: 0");
        lbSachConKho.setFont(fontThongKe);
        lbSachConKho.setForeground(mauChu);

        lbTongTienPhat = new JLabel("Tổng tiền phạt đã ghi nhận: 0 VNĐ");
        lbTongTienPhat.setFont(new Font("Arial", Font.BOLD, 15));
        lbTongTienPhat.setForeground(new Color(180, 40, 40));

        cTk.gridx = 0;
        cTk.gridy = 0;
        khungThongKe.add(lbTongSach, cTk);
        cTk.gridy = 1;
        khungThongKe.add(lbSachDangMuon, cTk);
        cTk.gridy = 2;
        khungThongKe.add(lbSachConKho, cTk);
        cTk.gridy = 3;
        khungThongKe.add(lbTongTienPhat, cTk);

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 10, 0);
        p.add(khungThongKe, c);

        // Thanh phan cach mau cam giong video
        JPanel thanhCam = new JPanel();
        thanhCam.setBackground(new Color(230, 115, 0));
        thanhCam.setPreferredSize(new Dimension(100, 14));
        c.gridy = 1;
        c.insets = new Insets(0, 0, 12, 0);
        p.add(thanhCam, c);

        // Tieu de bang qua han
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 6, 0);
        p.add(new taoDongNoiDung().Label("Danh sách sinh viên mượn quá hạn", "Arial", Font.BOLD, 14), c);

        // Bang Qua Han
        String[] cotQuaHan = {"STT", "Mã Phiếu", "Mã SV", "Tên SV", "Mã Sách", "Tên Sách", "Hạn Trả", "Ngày Trả", "Quá Hạn (Ngày)", "Tiền Phạt", "Trạng Thái"};
        modelQuaHan = new DefaultTableModel(cotQuaHan, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangQuaHan = new JTable(modelQuaHan);
        bangQuaHan.getTableHeader().setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        JScrollPane cuonQuaHan = new JScrollPane(bangQuaHan);
        cuonQuaHan.getViewport().setBackground(Color.WHITE);

        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        p.add(cuonQuaHan, c);

        return p;
    }

    // =========================================================
    // CAC HAM XU LY NGHIEP VU
    // =========================================================
    private void kiemTraSinhVien() {
        String maSv = txtMaSv.getText().trim();
        if (maSv.isEmpty()) {
            lbKetQuaSv.setForeground(Color.RED);
            lbKetQuaSv.setText("Vui lòng nhập mã sinh viên!");
            return;
        }
        try {
            SinhVien sv = svDao.findbyId(maSv);
            if (sv == null) {
                lbKetQuaSv.setForeground(Color.RED);
                lbKetQuaSv.setText("Mã sinh viên không tồn tại!");
            } else {
                int dangMuon = chiTietDao.demDangMuon(maSv);
                lbKetQuaSv.setForeground(new Color(0, 128, 0));
                lbKetQuaSv.setText("Họ tên: " + sv.getHo() + " " + sv.getTen()
                        + " | Lớp: " + sv.getLop() + " | Đang mượn: " + dangMuon + "/" + SinhVienService.SO_SACH_TOI_DA + " cuốn");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void kiemTraSach() {
        String maSach = txtMaSach.getText().trim();
        if (maSach.isEmpty()) {
            lbKetQuaSach.setForeground(Color.RED);
            lbKetQuaSach.setText("Vui lòng nhập mã sách!");
            return;
        }
        try {
            Sach s = sachDao.findbyId(maSach);
            if (s == null) {
                lbKetQuaSach.setForeground(Color.RED);
                lbKetQuaSach.setText("Mã sách không tồn tại!");
            } else {
                StringBuilder tgStr = new StringBuilder();
                if (s.getDsTacGia() != null) {
                    for (TacGia tg : s.getDsTacGia()) {
                        if (tgStr.length() > 0) tgStr.append(", ");
                        tgStr.append(tg.getHo()).append(" ").append(tg.getTen());
                    }
                }
                lbKetQuaSach.setForeground(new Color(0, 128, 0));
                lbKetQuaSach.setText("Tên sách: " + s.getTenSach()
                        + (tgStr.length() > 0 ? " | Tác giả: " + tgStr : "")
                        + " | Kho còn: " + s.getSoLuong() + " | Trạng thái: " + s.getTrangThai());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void xuLyMuonSach() {
        String maSv = txtMaSv.getText().trim().toUpperCase();
        String maSach = txtMaSach.getText().trim().toUpperCase();
        String strHanTra = txtHanTra.getText().trim();

        if (maSv.isEmpty() || maSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã sinh viên và Mã sách!");
            return;
        }

        LocalDate hanTra;
        try {
            hanTra = LocalDate.parse(strHanTra, DATE_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày hẹn trả phải đúng định dạng dd/MM/yyyy!");
            return;
        }

        if (!hanTra.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày hẹn trả phải lớn hơn ngày hôm nay (" + LocalDate.now().format(DATE_FMT) + ")!");
            return;
        }

        try {
            // Kiem tra sinh vien da muon cuon sach nay ma chua tra hay chua
            List<ChiTietPhieuMuon> dangMuonList = svService.xemSachDangMuon(maSv);
            for (ChiTietPhieuMuon ct : dangMuonList) {
                if (ct.getMaSach().equalsIgnoreCase(maSach)) {
                    JOptionPane.showMessageDialog(this, "Sinh viên " + maSv + " đang mượn cuốn sách " + maSach + " chưa trả!");
                    return;
                }
            }

            List<String> dsMaSach = new ArrayList<>();
            dsMaSach.add(maSach);
            PhieuMuon phieu = svService.muonSach(maSv, dsMaSach);

            // Cap nhat han tra theo ngay quan ly nhap
            ChiTietPhieuMuon ctMoi = chiTietDao.findById(phieu.getMaPhieu(), maSach);
            if (ctMoi != null && !hanTra.equals(ctMoi.getHanTra())) {
                ctMoi.setHanTra(hanTra);
                chiTietDao.update(ctMoi);
            }

            JOptionPane.showMessageDialog(this, "Mượn sách thành công! Mã phiếu: " + phieu.getMaPhieu());
            lamMoiTabMuonTra();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void xuLyTraSach() {
        int row = bangMuonTra.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng để trả sách!");
            return;
        }

        String maPhieu = String.valueOf(modelMuonTra.getValueAt(row, 1));
        String maSv = String.valueOf(modelMuonTra.getValueAt(row, 2));
        String maSach = String.valueOf(modelMuonTra.getValueAt(row, 3));

        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận trả sách \"" + maSach + "\" của sinh viên \"" + maSv + "\" (Phiếu " + maPhieu + ")?",
                "Xác nhận trả sách",
                JOptionPane.YES_NO_OPTION
        );
        if (xacNhan != JOptionPane.YES_OPTION) return;

        try {
            ChiTietPhieuMuon ketQua = svService.traSach(maSv, maPhieu, maSach);
            String thongBao = "Trả sách thành công!\nTrạng thái: " + ketQua.getTinhTrang();
            if (ketQua.getTienPhat() != null && ketQua.getTienPhat().compareTo(BigDecimal.ZERO) > 0) {
                thongBao += "\nTiền phạt quá hạn: " + ketQua.getTienPhat() + " VNĐ";
            }
            JOptionPane.showMessageDialog(this, thongBao);
            taiDuLieuBangMuonTra(null, true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void xuLySuaThongTin() {
        int row = bangMuonTra.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trên bảng để sửa!");
            return;
        }

        String maPhieu = String.valueOf(modelMuonTra.getValueAt(row, 1));
        String maSvCu = String.valueOf(modelMuonTra.getValueAt(row, 2));
        String maSachCu = String.valueOf(modelMuonTra.getValueAt(row, 3));
        String hanTraCu = String.valueOf(modelMuonTra.getValueAt(row, 5));

        JTextField tfMaSv = new JTextField(maSvCu, 15);
        tfMaSv.setEditable(false);
        tfMaSv.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        tfMaSv.setBackground(Color.white);
        JTextField tfHanTra = new JTextField(hanTraCu, 15);

        JPanel panelSua = new JPanel(new GridLayout(3, 2, 8, 8));
        panelSua.add(new JLabel("Mã phiếu:"));
        panelSua.add(new JLabel(maPhieu + " (Sách: " + maSachCu + ")"));
        panelSua.add(new JLabel("Mã sinh viên:"));
        panelSua.add(tfMaSv);
        panelSua.add(new JLabel("Ngày hẹn trả (dd/MM/yyyy):"));
        panelSua.add(tfHanTra);

        int ok = JOptionPane.showConfirmDialog(this, panelSua, "Sửa Thông Tin Mượn Trả", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String maSvMoi = tfMaSv.getText().trim();
            managerService.kiemTraTonTaiMaSinhVien(maSvMoi);
            LocalDate hanTraMoi = LocalDate.parse(tfHanTra.getText().trim(), DATE_FMT);

            PhieuMuon pm = phieuMuonDao.findById(maPhieu);
            ChiTietPhieuMuon ct = chiTietDao.findById(maPhieu, maSachCu);
            if (pm != null && ct != null) {
                pm.setMaSinhVien(maSvMoi);
                phieuMuonDao.update(pm);

                ct.setHanTra(hanTraMoi);
                chiTietDao.update(ct);
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                taiDuLieuBangMuonTra(null, true);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày hẹn trả phải đúng định dạng dd/MM/yyyy!");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void xuLyXoa() {
        int row = bangMuonTra.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trên bảng để xóa!");
            return;
        }

        String maPhieu = String.valueOf(modelMuonTra.getValueAt(row, 1));
        String maSach = String.valueOf(modelMuonTra.getValueAt(row, 3));
        String trangThai = String.valueOf(modelMuonTra.getValueAt(row, 8));

        if ("CHUA_TRA".equals(trangThai) || "Chưa trả".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, "Sách này sinh viên chưa trả, không thể xóa bản ghi mượn!");
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa lịch sử mượn sách \"" + maSach + "\" (Phiếu " + maPhieu + ")?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (xacNhan != JOptionPane.YES_OPTION) return;

        try {
            chiTietDao.delete(maPhieu, maSach);
            if (chiTietDao.findByPhieu(maPhieu).isEmpty()) {
                phieuMuonDao.delete(maPhieu);
            }
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
            taiDuLieuBangMuonTra(null, true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTuKhoa.getText().trim();
        taiDuLieuBangMuonTra(keyword, rdTimTheoMaSv.isSelected());
    }

    private void lamMoiTabMuonTra() {
        txtMaSv.setText("");
        txtMaSach.setText("");
        txtTuKhoa.setText("");
        txtHanTra.setText(LocalDate.now().plusDays(SinhVienService.SO_NGAY_MUON).format(DATE_FMT));
        lbKetQuaSv.setText(" ");
        lbKetQuaSach.setText(" ");
        taiDuLieuBangMuonTra(null, true);
    }

    private void taiDuLieuBangMuonTra(String keyword, boolean timTheoMaSv) {
        modelMuonTra.setRowCount(0);
        try {
            List<PhieuMuon> dsPhieu = phieuMuonDao.findAll();
            Map<String, PhieuMuon> mapPhieu = new HashMap<>();
            for (PhieuMuon p : dsPhieu) {
                mapPhieu.put(p.getMaPhieu(), p);
            }

            List<ChiTietPhieuMuon> dsChiTiet = chiTietDao.findAll();
            int stt = 1;
            String kw = keyword == null ? "" : keyword.trim().toLowerCase();

            for (ChiTietPhieuMuon ct : dsChiTiet) {
                PhieuMuon p = mapPhieu.get(ct.getMaPhieu());
                String maSv = p != null ? p.getMaSinhVien() : "";
                String ngayMuon = (p != null && p.getNgayMuon() != null) ? p.getNgayMuon().format(DATE_FMT) : "";

                if (!kw.isEmpty()) {
                    if (timTheoMaSv && !maSv.toLowerCase().contains(kw)) continue;
                    if (!timTheoMaSv && !ct.getMaSach().toLowerCase().contains(kw)) continue;
                }

                modelMuonTra.addRow(new Object[]{
                        stt++,
                        ct.getMaPhieu(),
                        maSv,
                        ct.getMaSach(),
                        ngayMuon,
                        ct.getHanTra() != null ? ct.getHanTra().format(DATE_FMT) : "",
                        ct.getNgayTra() != null ? ct.getNgayTra().format(DATE_FMT) : "Chưa trả",
                        ct.getTienPhat() != null ? ct.getTienPhat() : BigDecimal.ZERO,
                        ct.getTinhTrang()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách mượn trả: " + ex.getMessage());
        }
    }

    private void taiDuLieuThongKeVaQuaHan() {
        modelQuaHan.setRowCount(0);
        try {
            List<Sach> dsSach = sachDao.searchByTen("");
            Map<String, Sach> mapSach = new HashMap<>();
            int conKho = 0;
            for (Sach s : dsSach) {
                mapSach.put(s.getMaSach(), s);
                conKho += s.getSoLuong();
            }

            List<ChiTietPhieuMuon> dsDangMuon = chiTietDao.findDangMuon();
            int soDangMuon = dsDangMuon.size();
            int tongSoSach = conKho + soDangMuon;

            lbTongSach.setText("Tổng số lượng sách: " + tongSoSach);
            lbSachDangMuon.setText("Tổng số lượng sách đang mượn: " + soDangMuon);
            lbSachConKho.setText("Tổng số lượng sách còn trong kho: " + conKho);
            lbTongTienPhat.setText("Tổng tiền phạt đã ghi nhận: " + managerService.tongTienPhat() + " VNĐ");

            // Load danh sach qua han (bao gom ca da tra qua han VA chua tra nhung qua han)
            List<PhieuMuon> dsPhieu = phieuMuonDao.findAll();
            Map<String, PhieuMuon> mapPhieu = new HashMap<>();
            for (PhieuMuon p : dsPhieu) {
                mapPhieu.put(p.getMaPhieu(), p);
            }

            List<SinhVien> dsSv = svDao.findAll();
            Map<String, SinhVien> mapSv = new HashMap<>();
            for (SinhVien sv : dsSv) {
                mapSv.put(sv.getMaSinhVien(), sv);
            }

            List<ChiTietPhieuMuon> allChiTiet = chiTietDao.findAll();
            LocalDate homNay = LocalDate.now();
            int stt = 1;

            for (ChiTietPhieuMuon ct : allChiTiet) {
                boolean laQuaHan = ct.getTinhTrang() == TinhTrangMuon.QUA_HAN
                        || (ct.getTinhTrang() == TinhTrangMuon.CHUA_TRA
                        && ct.getHanTra() != null && ct.getHanTra().isBefore(homNay));

                if (!laQuaHan) continue;

                PhieuMuon p = mapPhieu.get(ct.getMaPhieu());
                String maSv = p != null ? p.getMaSinhVien() : "";
                SinhVien sv = mapSv.get(maSv);
                String tenSv = sv != null ? (sv.getHo() + " " + sv.getTen()) : "";
                Sach s = mapSach.get(ct.getMaSach());
                String tenSach = s != null ? s.getTenSach() : "";

                LocalDate mocTinh = ct.getNgayTra() != null ? ct.getNgayTra() : homNay;
                long soNgayQuaHan = ct.getHanTra() != null ? Math.max(0, ChronoUnit.DAYS.between(ct.getHanTra(), mocTinh)) : 0;
                BigDecimal tienPhatDuKien = ct.getTinhTrang() == TinhTrangMuon.CHUA_TRA
                        ? SinhVienService.TIEN_PHAT_MOI_NGAY.multiply(BigDecimal.valueOf(soNgayQuaHan))
                        : ct.getTienPhat();

                modelQuaHan.addRow(new Object[]{
                        stt++,
                        ct.getMaPhieu(),
                        maSv,
                        tenSv,
                        ct.getMaSach(),
                        tenSach,
                        ct.getHanTra() != null ? ct.getHanTra().format(DATE_FMT) : "",
                        ct.getNgayTra() != null ? ct.getNgayTra().format(DATE_FMT) : "Chưa trả",
                        soNgayQuaHan,
                        tienPhatDuKien,
                        ct.getTinhTrang()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải thống kê: " + ex.getMessage());
        }
    }
}