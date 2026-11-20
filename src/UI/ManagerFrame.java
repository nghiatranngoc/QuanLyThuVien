package UI;

import javax.swing.*;

import java.awt.*;
import java.math.BigDecimal;

import dao.*;
import model.*;
import java.sql.SQLException;
import java.sql.Connection;
import database.DBConnection;
import enums.Sex;
import enums.TrangThaiSach;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;
import java.util.ArrayList;

import Services.ManagerService;

import javax.swing.border.BevelBorder;

public class ManagerFrame extends JFrame{
    private final TaiKhoanDAO tkDao = new TaiKhoanDAO();
    private final QuanLyDAO qlDao = new QuanLyDAO();
    private DefaultTableModel model;
    private JTable bang;

    public ManagerFrame(String userName) throws SQLException{
        super("Hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(720, 460));
        setJMenuBar(taoMenu(userName));
        setContentPane(trangChu());
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel trangChu(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(26, 36, 26, 36));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 26, 0);
        JLabel tieuDe = new taoTieuDe().label("QUẢN LÝ THƯ VIỆN");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(tieuDe, c);
        c.insets = new Insets(6, 6, 6, 6);

        JPanel khung = new taoKhung().panel();

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        JButton btnSinhVien = new taoNut().button("Quản lý sinh viên", "Arial", Font.BOLD, 20);
        btnSinhVien.addActionListener(e -> {
            setSize(new Dimension(1080, 720));
            setContentPane(giaoDienQlSinhVien());
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });
        khung.add(btnSinhVien, c);

        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JButton btnSach = new taoNut().button("Quản lý sách", "Arial", Font.BOLD, 20);
        btnSach.addActionListener(e -> {
            setSize(new Dimension(1080, 720));
            setContentPane(giaoDienQlSach());
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });
        khung.add(btnSach, c);

        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JButton btnMuonTra = new taoNut().button("Quản lý mượn trả", "Arial", Font.BOLD, 20);
        btnMuonTra.addActionListener(e -> {
            setSize(new Dimension(1080, 720));
            setContentPane(new QuanLyMuonTraPanel());
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });
        khung.add(btnMuonTra, c);

        p.add(khung, c);
        return p;
    }

    private JMenuBar taoMenu(String userName) throws SQLException{
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Tài khoản");

        JMenuItem itemThongTin = new JMenuItem("Thông tin tài khoản");
        itemThongTin.addActionListener(e -> {
            try{
                setContentPane(thongTinTaiKhoan(userName));
                pack();
                setLocationRelativeTo(null);
                revalidate();
                repaint();
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        JMenuItem itemDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        itemDoiMatKhau.addActionListener(e -> {
            setContentPane(giaoDienDoiMatKhau(userName));
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
        JMenuItem itemQuayLai = new JMenuItem("Quay lại");
        itemQuayLai.addActionListener(e ->{
            setSize(new Dimension(720, 460));
            setContentPane(trangChu());
            setLocationRelativeTo(null);
            revalidate();
            repaint();
        });

        menu.add(itemThongTin);
        menu.add(itemDoiMatKhau);
        menu.add(itemQuayLai);
        menu.add(itemDangXuat);

        menuBar.add(menu);
        return menuBar;
    }

    private JPanel thongTinTaiKhoan(String userName) throws SQLException {
        TaiKhoan tk = tkDao.findbyUserName(userName);
        QuanLy ql = qlDao.findbyTaiKhoan(tk.getMaTaiKhoan());

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(26, 36, 26, 36));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 16, 0);
        JLabel tieuDe = new taoTieuDe().label("THÔNG TIN CÁ NHÂN");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(tieuDe, c);

        JPanel khung = new taoKhung().panel();
        GridBagConstraints k = new GridBagConstraints();
        khung.setBackground(new Color(220, 235, 250));
        k.insets = new Insets(10, 20, 10, 20);

        k.gridx = 0;
        k.gridy = 0;
        k.anchor = GridBagConstraints.WEST;
        khung.add(new taoDongNoiDung().Label("Mã tài khoản", "Arial", Font.BOLD, 15), k);

        k.gridx = 1;
        khung.add(new taoDongNoiDung().Label(ql.getMaTaiKhoan(), "Arial", Font.BOLD, 15), k);

        k.gridx = 0;
        k.gridy = 1;
        khung.add(new taoDongNoiDung().Label("Mã quản lý", "Arial", Font.BOLD, 15), k);

        k.gridx = 1;
        khung.add(new taoDongNoiDung().Label(ql.getMaQl(), "Arial", Font.BOLD, 15), k);

        k.gridx = 0;
        k.gridy = 2;
        khung.add(new taoDongNoiDung().Label("Họ Tên", "Arial", Font.BOLD, 15), k);

        k.gridx = 1;
        khung.add(new taoDongNoiDung().Label(ql.getHo() + " " + ql.getTen(), "Arial", Font.BOLD, 15), k);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(khung, c);

        return p;
    }

    private JPanel giaoDienDoiMatKhau(String userName){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        GridBagConstraints c = new GridBagConstraints();

        //tieu de
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 10, 0);
        JLabel tieuDe = new taoTieuDe().label("ĐỔI MẬT KHẨU");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(tieuDe, c);

        //khung nhap du lieu
        JPanel khung = new taoKhung().panel();
        GridBagConstraints cKhung = new GridBagConstraints();
        JPasswordField txtMatKhauCu = new JPasswordField(15);
        JPasswordField txtMatKhauMoi = new JPasswordField(15);
        JPasswordField txtNhapLaiMK = new JPasswordField(15);

        cKhung.gridx = 0;
        cKhung.gridy = 0;
        cKhung.gridwidth = 1;
        cKhung.fill = GridBagConstraints.NONE;
        cKhung.weightx = 0;
        cKhung.anchor = GridBagConstraints.EAST;
        cKhung.insets = new Insets(6, 6, 6, 6);
        JLabel mk = new taoDongNoiDung().Label("Nhập mật khẩu cũ: ", "Arial", Font.BOLD, 15);
        khung.add(mk, cKhung);

        cKhung.gridx = 1;
        cKhung.fill = GridBagConstraints.HORIZONTAL;
        cKhung.weightx = 1;
        cKhung.anchor = GridBagConstraints.WEST;
        khung.add(txtMatKhauCu, cKhung);

        cKhung.gridx = 0;
        cKhung.gridy = 1;
        cKhung.gridwidth = 1;
        cKhung.fill = GridBagConstraints.NONE;
        cKhung.weightx = 0;
        cKhung.anchor = GridBagConstraints.EAST;
        cKhung.insets = new Insets(6, 6, 6, 6);
        JLabel mkMoi = new taoDongNoiDung().Label("Nhập mật khẩu mới: ", "Arial", Font.BOLD, 15);
        khung.add(mkMoi, cKhung);

        cKhung.gridx = 1;
        cKhung.fill = GridBagConstraints.HORIZONTAL;
        cKhung.weightx = 1;
        cKhung.anchor = GridBagConstraints.WEST;
        khung.add(txtMatKhauMoi, cKhung);

        cKhung.gridx = 0;
        cKhung.gridy = 2;
        cKhung.gridwidth = 1;
        cKhung.fill = GridBagConstraints.NONE;
        cKhung.weightx = 0;
        cKhung.anchor = GridBagConstraints.EAST;
        cKhung.insets = new Insets(6, 6, 6, 6);
        JLabel nhapLaiMk = new taoDongNoiDung().Label("Nhập lại mật khẩu mới: ", "Arial", Font.BOLD, 15);
        khung.add(nhapLaiMk, cKhung);

        cKhung.gridx = 1;
        cKhung.fill = GridBagConstraints.HORIZONTAL;
        cKhung.weightx = 1;
        cKhung.anchor = GridBagConstraints.WEST;
        khung.add(txtNhapLaiMK, cKhung);

        JPanel nut = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        JButton btnThayDoi = new taoNut().Button("Thay đổi");
        getRootPane().setDefaultButton(btnThayDoi);
        btnThayDoi.addActionListener(e -> {
            try{
                thayDoiMatKhau(userName, txtMatKhauMoi, txtMatKhauCu, txtNhapLaiMK);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        JButton btnHuy = new taoNut().Button("Hủy");
        btnHuy.addActionListener(e -> {
            int xacNhan = JOptionPane.showConfirmDialog(
                this, 
                "Bạn có chắc là muốn hủy thay đổi mật khẩu không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            if(xacNhan == JOptionPane.YES_OPTION){
                setSize(new Dimension(720, 460));
                setContentPane(trangChu());
                setLocationRelativeTo(null);
            }
        });

        nut.add(btnThayDoi);
        nut.add(btnHuy);

        cKhung.gridx = 0;
        cKhung.gridy = 3;
        cKhung.gridwidth = 2;
        cKhung.fill = GridBagConstraints.HORIZONTAL;
        cKhung.weightx = 1;
        cKhung.anchor = GridBagConstraints.CENTER;
        khung.add(nut, cKhung);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        p.add(khung, c);
        return p;
    }

    private void thayDoiMatKhau(String userName, JPasswordField txtMatKhauMoi, JPasswordField txtMatKhauCu, JPasswordField txtNhapLaiMK) throws SQLException{
        String matKhauMoi = new String(txtMatKhauMoi.getPassword());
        String matKhauCu = new String(txtMatKhauCu.getPassword());
        String nhapLaiMatKhau = new String(txtNhapLaiMK.getPassword());
        if(matKhauCu == null || matKhauCu.isEmpty()){
            throw new IllegalArgumentException("Vui lòng nhập lại mật khẩu cũ");
        }
        if(matKhauMoi == null || matKhauMoi.isEmpty()){
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu mới");
        }
        if(nhapLaiMatKhau == null || nhapLaiMatKhau.isEmpty()){
            throw new IllegalArgumentException("Vui lòng nhập lại mật khẩu mới");
        }
        TaiKhoanDAO tkDao = new TaiKhoanDAO();
        TaiKhoan tk = new TaiKhoan();
        tk = tkDao.findbyUserName(userName);
        if(!matKhauCu.equals(tk.getPassword())){
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        if(!nhapLaiMatKhau.equals(matKhauMoi)){
            throw new IllegalArgumentException("Nhập lại mật khẩu mới không đúng");
        }
        if(matKhauCu.equals(matKhauMoi)){
            throw new IllegalArgumentException("Trùng mật khẩu cũ! Vui lòng nhập mật khẩu mới khác");
        }
        int xacNhan = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc muốn thay đổi mật khẩu không?",
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION
        );

        if(xacNhan == JOptionPane.YES_OPTION){
            tk.setPassword(new String(txtMatKhauMoi.getPassword()));
            tkDao.update(tk);
            JOptionPane.showMessageDialog(this, "Bạn đã thay đổi mật khẩu thành công!");
            setSize(new Dimension(720, 460));
            setContentPane(trangChu());
            setLocationRelativeTo(null);
        } 
    }

    private JPanel giaoDienQlSinhVien(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        GridBagConstraints c = new GridBagConstraints();
        
        //tim kiem
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 0, 0);
        JLabel timKiem = new taoDongNoiDung().Label("Tìm kiếm", "Arial", Font.BOLD, 13);
        p.add(timKiem, c);

        JPanel khungTimKiem = new taoKhung().panel();
        GridBagConstraints cKhungTk = new GridBagConstraints();
        ButtonGroup gr = new ButtonGroup();
        JRadioButton findByMaSv = new JRadioButton("Tìm kiếm theo mã sinh viên");
        findByMaSv.setFocusPainted(false);
        cKhungTk.gridx = 0;
        cKhungTk.gridy = 0;
        cKhungTk.fill = GridBagConstraints.NONE;
        cKhungTk.weightx = 0;
        cKhungTk.anchor = GridBagConstraints.WEST;
        khungTimKiem.add(findByMaSv, cKhungTk);

        cKhungTk.gridy = 1;
        JRadioButton findByTen = new JRadioButton("Tìm kiếm theo tên");
        findByTen.setFocusPainted(false);
        khungTimKiem.add(findByTen, cKhungTk);

        gr.add(findByMaSv);
        gr.add(findByTen);

        cKhungTk.gridx = 1;
        cKhungTk.gridy = 0;
        cKhungTk.gridheight = 2;
        cKhungTk.fill = GridBagConstraints.HORIZONTAL;
        cKhungTk.weightx = 1;
        cKhungTk.insets = new Insets(0, 10, 0, 10);
        cKhungTk.anchor = GridBagConstraints.WEST;
        JTextField nhapKeyWord = new JTextField(20);
        nhapKeyWord.setHorizontalAlignment(JTextField.CENTER);
        khungTimKiem.add(nhapKeyWord, cKhungTk);

        JButton btnTimKiem = new taoNut().Button("Tìm kiếm");
        btnTimKiem.addActionListener(e -> {
            timKiem(nhapKeyWord, findByTen, findByMaSv);
        });
        cKhungTk.gridx = 2;
        khungTimKiem.add(btnTimKiem, cKhungTk);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(khungTimKiem, c);

        //thong tin sinh vien

        JPanel khungThongTinSv = new taoKhung().panel();
        GridBagConstraints tt = new GridBagConstraints();

        JLabel thongTinSv = new taoDongNoiDung().Label("Thông tin sinh viên", "Arial", Font.BOLD, 13);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 0, 0);
        p.add(thongTinSv, c);

        JPanel trai = new JPanel(new GridBagLayout());
        GridBagConstraints cTrai = new GridBagConstraints();
        JTextField txtMaSv = new JTextField(13);
        txtMaSv.setEditable(false);
        txtMaSv.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        txtMaSv.setBackground(Color.white);
        JLabel maSv = new taoDongNoiDung().label("Mã SV");
        cTrai.gridx = 0;
        cTrai.gridy = 0;
        cTrai.fill = GridBagConstraints.NONE;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        cTrai.insets = new Insets(6, 6, 6, 6);
        trai.add(maSv, cTrai);
        
        cTrai.gridx = 1;
        cTrai.fill = GridBagConstraints.HORIZONTAL;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        trai.add(txtMaSv, cTrai);

        JTextField txtHo = new JTextField(13);
        JLabel ho = new taoDongNoiDung().label("Họ");
        cTrai.gridx = 0;
        cTrai.gridy = 1;
        cTrai.fill = GridBagConstraints.NONE;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        cTrai.insets = new Insets(6, 6, 6, 6);
        trai.add(ho, cTrai);
        
        cTrai.gridx = 1;
        cTrai.fill = GridBagConstraints.HORIZONTAL;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        trai.add(txtHo, cTrai);

        JTextField txtTen = new JTextField(13);
        JLabel Ten = new taoDongNoiDung().label("Tên");
        cTrai.gridx = 0;
        cTrai.gridy = 2;
        cTrai.fill = GridBagConstraints.NONE;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        cTrai.insets = new Insets(6, 6, 6, 6);
        trai.add(Ten, cTrai);
        
        cTrai.gridx = 1;
        cTrai.fill = GridBagConstraints.HORIZONTAL;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        trai.add(txtTen, cTrai);

        String[] khoa = {"Công Nghệ Thông Tin", "An Toàn Thông Tin", "Điện - Điện Tử", "Quản Trị Kinh Doanh", "Công Nghệ Đa Phương Tiện"};
        JComboBox<String> bKhoa = new JComboBox<>(khoa);
        bKhoa.addActionListener(e -> {
            try{
                ganMaSv(bKhoa, txtMaSv);
            } catch(SQLException ex){
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });

        try{
            ganMaSv(bKhoa, txtMaSv);
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
        JLabel lbKhoa = new taoDongNoiDung().label("Khoa");
        cTrai.gridx = 0;
        cTrai.gridy = 3;
        cTrai.fill = GridBagConstraints.NONE;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        cTrai.insets = new Insets(6, 6, 6, 6);
        trai.add(lbKhoa, cTrai);

        cTrai.gridx = 1;
        cTrai.fill = GridBagConstraints.HORIZONTAL;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        trai.add(bKhoa, cTrai);

        JTextField txtNgaySinh = new JTextField(13);
        JLabel ngaySinh = new taoDongNoiDung().label("Ngày sinh(dd/mm/yyyy)");
        cTrai.gridx = 0;
        cTrai.gridy = 4;
        cTrai.fill = GridBagConstraints.NONE;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        cTrai.insets = new Insets(6, 6, 6, 6);
        trai.add(ngaySinh, cTrai);

        cTrai.gridx = 1;
        cTrai.fill = GridBagConstraints.HORIZONTAL;
        cTrai.weightx = 0;
        cTrai.anchor = GridBagConstraints.WEST;
        trai.add(txtNgaySinh, cTrai);

        tt.gridx = 0;
        tt.gridy = 0;
        tt.fill = GridBagConstraints.HORIZONTAL;
        tt.weightx = 1;
        tt.anchor = GridBagConstraints.WEST;
        tt.insets = new Insets(0, 10, 0, 10);
        khungThongTinSv.add(trai, tt);

        JPanel phai = new JPanel(new GridBagLayout());
        GridBagConstraints cPhai = new GridBagConstraints();

        JTextField txtMaTk = new JTextField(13);
        txtMaTk.setEditable(false);
        txtMaTk.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        txtMaTk.setBackground(Color.white);
        try(Connection conn = DBConnection.getConnection()){
            txtMaTk.setText(MaTuDong.taoMa(conn, "TaiKhoan", "maTaiKhoan", "TK", 4));
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
        
        JLabel maTk = new taoDongNoiDung().label("Mã tài khoản");
        cPhai.gridx = 0;
        cPhai.gridy = 0;
        cPhai.fill = GridBagConstraints.NONE;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        cPhai.insets = new Insets(6, 6, 6, 6);
        phai.add(maTk, cPhai);

        cPhai.gridx = 1;
        cPhai.gridy = 0;
        cPhai.fill = GridBagConstraints.HORIZONTAL;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(txtMaTk, cPhai);

        JPanel gioiTinh = new JPanel(new GridBagLayout());
        
        JLabel lbGt = new taoDongNoiDung().label("Giới tính");
        JRadioButton rdNam = new JRadioButton("Nam");
        JRadioButton rdNu = new JRadioButton("Nữ");
        JRadioButton rdKhac = new JRadioButton("Khác");
        ButtonGroup grGt = new ButtonGroup();
        grGt.add(rdNam);
        grGt.add(rdNu);
        grGt.add(rdKhac);

        gioiTinh.add(lbGt);
        gioiTinh.add(rdNam);
        gioiTinh.add(rdNu);
        gioiTinh.add(rdKhac);

        cPhai.gridx = 0;
        cPhai.gridy = 1;
        cPhai.gridwidth = 2;
        cPhai.fill = GridBagConstraints.NONE;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        cPhai.insets = new Insets(6, 6, 6, 6);
        phai.add(gioiTinh, cPhai);

        JLabel lbSdt = new taoDongNoiDung().label("Số điện thoại");
        JTextField txtSdt = new JTextField(13);
        cPhai.gridx = 0;
        cPhai.gridy = 2;
        cPhai.gridwidth = 1;
        cPhai.fill = GridBagConstraints.NONE;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(lbSdt, cPhai);

        cPhai.gridx = 1;
        cPhai.gridy = 2;
        cPhai.gridwidth = 1;
        cPhai.fill = GridBagConstraints.HORIZONTAL;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(txtSdt, cPhai);

        JLabel lbEmail = new taoDongNoiDung().label("Email");
        JTextField txtEmail = new JTextField(13);
        cPhai.gridx = 0;
        cPhai.gridy = 3;
        cPhai.gridwidth = 1;
        cPhai.fill = GridBagConstraints.NONE;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(lbEmail, cPhai);

        cPhai.gridx = 1;
        cPhai.gridy = 3;
        cPhai.gridwidth = 1;
        cPhai.fill = GridBagConstraints.HORIZONTAL;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(txtEmail, cPhai);

        JLabel lbLop = new taoDongNoiDung().label("Lớp");
        JTextField txtLop = new JTextField(13);
        cPhai.gridx = 0;
        cPhai.gridy = 4;
        cPhai.gridwidth = 1;
        cPhai.fill = GridBagConstraints.NONE;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(lbLop, cPhai);

        cPhai.gridx = 1;
        cPhai.gridy = 4;
        cPhai.gridwidth = 1;
        cPhai.fill = GridBagConstraints.HORIZONTAL;
        cPhai.weightx = 0;
        cPhai.anchor = GridBagConstraints.WEST;
        phai.add(txtLop, cPhai);

        tt.gridx = 1;
        tt.gridy = 0;
        tt.fill = GridBagConstraints.HORIZONTAL;
        tt.weightx = 1;
        tt.anchor = GridBagConstraints.WEST;
        tt.insets = new Insets(0, 10, 0, 10);
        khungThongTinSv.add(phai, tt);

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(khungThongTinSv, c);

        //Danh sach sinh vien
        String[] cot = {"STT", "Mã SV","Mã TK", "Họ", "Tên", "Ngày Sinh", "Giới Tính", "Email", "SĐT", "Lớp", "Khoa"};
        model = new DefaultTableModel(cot, 0);
        bang = new JTable(model);
        JTableHeader th = bang.getTableHeader();
        th.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        JScrollPane cuon = new JScrollPane(bang);
        cuon.getViewport().setBackground(Color.white);

        try{
            new SinhVienDAO().napBang(model, new SinhVienDAO().findAll());
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(cuon, c);

        JLabel lbChucNang = new taoDongNoiDung().Label("Chức năng", "Arial", Font.BOLD, 13);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 0, 0);
        p.add(lbChucNang, c);

        JPanel khungChucNang = new taoKhung().panel();
        GridBagConstraints cCn = new GridBagConstraints();
        JPanel tmp = new JPanel(new FlowLayout(FlowLayout.CENTER, 150, 5));
        JButton btnThem = new taoNut().Button("Thêm");
        btnThem.addActionListener(e ->{
            try{
                themSinhVien(txtMaSv, txtMaTk, txtHo, txtTen, 
                    txtNgaySinh, rdNam, rdNu, rdKhac, txtEmail, txtSdt, txtLop, bKhoa
                );
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                lamMoiFormSinhVien(txtMaSv, txtMaTk, txtHo, txtTen, txtNgaySinh, 
                    txtSdt, txtEmail, txtLop, grGt, bKhoa);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        JButton btnSua = new taoNut().Button("Sửa");
        btnSua.addActionListener(e -> {
            int r = bang.getSelectedRow();
            if(r == -1){
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên");
                return;
            }
            String maSinhVien = (String) model.getValueAt(r, 1);
            try{
                SinhVien sv = new ManagerService().findSinhVienById(maSinhVien);
                moDialogSua(sv);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        JButton btnXoa = new taoNut().Button("Xóa");
        btnXoa.addActionListener(e -> {
            int r = bang.getSelectedRow();
            if(r == -1){
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên");
                return;
            }
            String maSinhVien = (String) model.getValueAt(r, 1);
            try{
                int xacNhan = JOptionPane.showConfirmDialog(
                    this, "Bạn có chắc là xóa sinh viên này", "Xác nhận", JOptionPane.YES_NO_OPTION
                );
                if(xacNhan == JOptionPane.YES_OPTION){
                    SinhVien sv = new ManagerService().findSinhVienById(maSinhVien);
                    new ManagerService().xoaSinhVien(maSinhVien);
                    new TaiKhoanDAO().detele(sv.getMaTaiKhoan());
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    new SinhVienDAO().napBang(model, new SinhVienDAO().findAll());
                }
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        tmp.add(btnThem);
        tmp.add(btnSua);
        tmp.add(btnXoa);

        cCn.gridx = 0;
        cCn.gridy = 0;
        cCn.gridwidth = 4;
        cCn.fill = GridBagConstraints.HORIZONTAL;
        cCn.weightx = 1;
        cCn.anchor = GridBagConstraints.CENTER;
        cCn.insets = new Insets(10, 10, 10, 10);
        khungChucNang.add(tmp, cCn);

        c.gridx = 0;
        c.gridy = 7;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(khungChucNang, c);
        return p;
    }

    private JPanel giaoDienQlSach(){
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JLabel lbTimKiem = new taoDongNoiDung().Label("Tìm kiếm", "Arial", Font.BOLD, 13);
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 10, 0, 0);
        p.add(lbTimKiem, c);

        //khung tim kiem
        JPanel khungTk = new taoKhung().panel();
        GridBagConstraints gbcKhung = new GridBagConstraints();
        JRadioButton rdFindByTen = new JRadioButton("Tìm kiếm theo tên sách");
        rdFindByTen.setFocusPainted(false);
        JRadioButton rdFindByMa = new JRadioButton("Tìm kiếm theo mã sách");
        rdFindByMa.setFocusPainted(false);
        JRadioButton rdFindByTheLoai = new JRadioButton("Tìm kiếm theo thể loại");
        rdFindByTheLoai.setFocusPainted(false);
        JRadioButton rdFindByTacGia = new JRadioButton("Tìm kiếm theo tác giả");
        rdFindByTacGia.setFocusPainted(false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rdFindByTen);
        bg.add(rdFindByMa);
        bg.add(rdFindByTheLoai);
        bg.add(rdFindByTacGia);

        gbcKhung.gridx = 0;
        gbcKhung.gridy = 0;
        gbcKhung.fill = GridBagConstraints.HORIZONTAL;
        gbcKhung.weightx = 0;
        gbcKhung.anchor = GridBagConstraints.WEST;
        gbcKhung.insets = new Insets(6, 6, 6, 6);
        khungTk.add(rdFindByTen, gbcKhung);

        gbcKhung.gridx = 0;
        gbcKhung.gridy = 1;
        gbcKhung.fill = GridBagConstraints.HORIZONTAL;
        gbcKhung.weightx = 0;
        gbcKhung.anchor = GridBagConstraints.WEST;
        gbcKhung.insets = new Insets(6, 6, 6, 6);
        khungTk.add(rdFindByMa, gbcKhung);

        gbcKhung.gridx = 0;
        gbcKhung.gridy = 2;
        gbcKhung.fill = GridBagConstraints.HORIZONTAL;
        gbcKhung.weightx = 0;
        gbcKhung.anchor = GridBagConstraints.WEST;
        gbcKhung.insets = new Insets(6, 6, 6, 6);
        khungTk.add(rdFindByTheLoai, gbcKhung);

        gbcKhung.gridx = 0;
        gbcKhung.gridy = 3;
        gbcKhung.fill = GridBagConstraints.HORIZONTAL;
        gbcKhung.weightx = 0;
        gbcKhung.anchor = GridBagConstraints.WEST;
        gbcKhung.insets = new Insets(6, 6, 6, 6);
        khungTk.add(rdFindByTacGia, gbcKhung);

        JTextField txtNhapKeyWord = new JTextField(20);
        txtNhapKeyWord.setHorizontalAlignment(JTextField.CENTER);
        gbcKhung.gridx = 1;
        gbcKhung.gridy = 0;
        gbcKhung.gridheight = 4;
        gbcKhung.fill = GridBagConstraints.HORIZONTAL;
        gbcKhung.weightx = 1;
        gbcKhung.anchor = GridBagConstraints.CENTER;
        gbcKhung.insets = new Insets(10, 10, 10, 10);
        khungTk.add(txtNhapKeyWord, gbcKhung);

        JButton btnTimKiem = new taoNut().button("Tìm kiếm", "Arial", Font.BOLD, 13);
        btnTimKiem.addActionListener(e -> {
            try{
                timKiemSach(txtNhapKeyWord, rdFindByMa, rdFindByTen, rdFindByTacGia, rdFindByTheLoai);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        gbcKhung.gridx = 2;
        gbcKhung.gridy = 0;
        gbcKhung.gridheight = 4;
        gbcKhung.fill = GridBagConstraints.HORIZONTAL;
        gbcKhung.weightx = 1;
        gbcKhung.anchor = GridBagConstraints.CENTER;
        gbcKhung.insets = new Insets(10, 10, 10, 10);
        khungTk.add(btnTimKiem, gbcKhung);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(khungTk, c);

        // khung nhap thong tin sach
        JLabel lbThongTinSach = new taoDongNoiDung().Label("Thông tin sách", "Arial", Font.BOLD, 13);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 0 , 0);
        p.add(lbThongTinSach, c);
        JPanel khungTt = new taoKhung().panel();
        GridBagConstraints k = new GridBagConstraints();

        JPanel trai = new JPanel(new GridBagLayout());
        GridBagConstraints cT = new GridBagConstraints();
        JLabel lbMaSach = new JLabel("Mã sách");
        lbMaSach.setFont(new Font("Arial", Font.BOLD, 13));
        cT.gridx = 0;
        cT.gridy = 0;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(lbMaSach, cT);

        JTextField txtMaSach = new JTextField(20);
        txtMaSach.setEditable(false);
        txtMaSach.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        txtMaSach.setBackground(Color.white);
        try(Connection conn = DBConnection.getConnection()){
            txtMaSach.setText(MaTuDong.taoMa(conn, "sach", "maSach", "TNNS", 4));
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
        cT.gridx = 1;
        cT.gridy = 0;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(txtMaSach, cT);

        JLabel lbTenSach = new taoDongNoiDung().Label("Tên sách", "Arial", Font.BOLD, 13);
        cT.gridx = 0;
        cT.gridy = 1;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(lbTenSach, cT);

        JTextField txtTenSach = new JTextField(20);
        cT.gridx = 1;
        cT.gridy = 1;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(txtTenSach, cT);

        JLabel lbNhaXb = new taoDongNoiDung().Label("Nhà xuất bản", "Arial", Font.BOLD, 13);
        cT.gridx = 0;
        cT.gridy = 2;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(lbNhaXb, cT);

        JTextField txtNhaXb = new JTextField(20);
        cT.gridx = 1;
        cT.gridy = 2;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(txtNhaXb, cT);

        JLabel lbNamXb = new taoDongNoiDung().Label("Năm xuất bản", "Arial", Font.BOLD, 13);
        cT.gridx = 0;
        cT.gridy = 3;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(lbNamXb, cT);

        JTextField txtNamXb = new JTextField(20);
        cT.gridx = 1;
        cT.gridy = 3;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(txtNamXb, cT);

        JLabel lbTacGia = new taoDongNoiDung().Label("Tác giả", "Arial", Font.BOLD, 13);
        cT.gridx = 0;
        cT.gridy = 4;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(lbTacGia, cT);

        JTextField txtTacGia = new JTextField(20);
        cT.gridx = 1;
        cT.gridy = 4;
        cT.fill = GridBagConstraints.HORIZONTAL;
        cT.weightx = 0;
        cT.anchor = GridBagConstraints.WEST;
        cT.insets = new Insets(6, 6, 6, 6);
        trai.add(txtTacGia, cT);

        k.gridx = 0;
        k.gridy = 0;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 1;
        k.anchor = GridBagConstraints.NORTHWEST;
        k.insets = new Insets(0, 0, 0, 10);
        khungTt.add(trai, k);

        JPanel phai = new JPanel(new GridBagLayout());
        GridBagConstraints cP = new GridBagConstraints();

        JLabel lbMaTheLoai = new JLabel("Mã thể loại");
        lbMaTheLoai.setFont(new Font("Arial", Font.BOLD, 13));
        cP.gridx = 0;
        cP.gridy = 0;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(lbMaTheLoai, cP);

        JTextField txtMaTheLoai = new JTextField(20);
        txtMaTheLoai.setEditable(false);
        txtMaTheLoai.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        txtMaTheLoai.setBackground(Color.white);
        cP.gridx = 1;
        cP.gridy = 0;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(txtMaTheLoai, cP);

        JComboBox<TheLoai> cbTheLoai = new JComboBox<>();
        try{
            for(TheLoai tl : new TheLoaiDAO().findAll()){
                cbTheLoai.addItem(tl);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Lỗi CSDL: " + ex.getMessage());
        }
        cbTheLoai.setFont(new Font("Arial", Font.BOLD, 13));
        cbTheLoai.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        cbTheLoai.addActionListener(e -> {
            TheLoai tl = (TheLoai) cbTheLoai.getSelectedItem();
            if(tl == null) return;
            txtMaTheLoai.setText(tl.getMaTheLoai());
        });
        if(cbTheLoai.getItemCount() > 0){
            cbTheLoai.setSelectedIndex(-1);
            cbTheLoai.setSelectedIndex(0);
        }

        JLabel lbTheLoai = new taoDongNoiDung().Label("Thể loại", "Arial", Font.BOLD, 13);
        cP.gridx = 0;
        cP.gridy = 1;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(lbTheLoai, cP);

        cP.gridx = 1;
        cP.gridy = 1;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(cbTheLoai, cP);

        JLabel lbGiaTri = new JLabel("Giá trị");
        lbGiaTri.setFont(new Font("Arial", Font.BOLD, 13));
        cP.gridx = 0;
        cP.gridy = 2;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(lbGiaTri, cP);

        JTextField txtGiaTri = new JTextField(20);
        cP.gridx = 1;
        cP.gridy = 2;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(txtGiaTri, cP);

        JLabel lbSoLuong = new JLabel("Số lượng");
        lbSoLuong.setFont(new Font("Arial", Font.BOLD, 13));
        cP.gridx = 0;
        cP.gridy = 3;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(lbSoLuong, cP);

        SpinnerNumberModel modelNumber = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);//(gia tri ban dau, gia tri toi thieu, gia tri toi da, moi lan tang them)
        JSpinner spSoLuong = new JSpinner(modelNumber);
        spSoLuong.setFont(new Font("Arial", Font.BOLD, 13));
        spSoLuong.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        cP.gridx = 1;
        cP.gridy = 3;
        cP.fill = GridBagConstraints.HORIZONTAL;
        cP.weightx = 0;
        cP.anchor = GridBagConstraints.WEST;
        cP.insets = new Insets(6, 6, 6, 6);
        phai.add(spSoLuong, cP);

        k.gridx = 1;
        k.gridy = 0;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 1;
        k.anchor = GridBagConstraints.NORTHWEST;
        k.insets = new Insets(0, 10, 0, 0);
        khungTt.add(phai, k);

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 10, 10 , 10);
        p.add(khungTt, c);

        String[] cot = {"STT", "Mã sách", "Tên sách", "Tác giả", "Nhà xuất bản", "Năm xuất bản", "Thể loại", "Giá trị", "Số lượng"};
        model = new DefaultTableModel(cot, 0);
        bang = new JTable(model);
        JScrollPane cuon = new JScrollPane(bang);
        JTableHeader th = bang.getTableHeader();
        th.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        cuon.getViewport().setBackground(Color.white);

        try{
            new SachDAO().napBang(model, new SachDAO().findAll());
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(10, 10, 10, 10);
        p.add(cuon, c);

        //Khung chuc nang
        JLabel lbChucNang = new taoDongNoiDung().Label("Chức năng", "Arial", Font.BOLD, 13);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 10, 0, 10);
        p.add(lbChucNang, c);

        JPanel khungCn = new taoKhung().panel();
        GridBagConstraints cCn = new GridBagConstraints();
        JButton btnThem = new taoNut().button("Thêm", "Arial", Font.BOLD, 13);
        btnThem.addActionListener(e -> {
            try{
                themSach(txtMaSach, txtTenSach, txtNhaXb, txtGiaTri, txtNamXb, txtTacGia, 
                    txtMaTheLoai, spSoLuong);
                new SachDAO().napBang(model, new SachDAO().findAll());
                try(Connection conn = DBConnection.getConnection()){
                    txtMaSach.setText(MaTuDong.taoMa(conn, "sach", "maSach", "TNNS", 4));
                }
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        cCn.gridx = 0;
        cCn.gridy = 0;
        cCn.fill = GridBagConstraints.HORIZONTAL;
        cCn.weightx = 1;
        cCn.anchor = GridBagConstraints.CENTER;
        cCn.insets = new Insets(10, 10, 10, 10);
        khungCn.add(btnThem, cCn);

        JButton btnSua = new taoNut().button("Sửa", "Arial", Font.BOLD, 13);
        btnSua.addActionListener(e -> {
            int r = bang.getSelectedRow();
            if(r == -1){
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách");
                return;
            }
            String maSach = (String) model.getValueAt(r, 1);
            try{
                moDialogSuaSach(new ManagerService().findSachById(maSach));
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        cCn.gridx = 1;
        cCn.gridy = 0;
        cCn.fill = GridBagConstraints.HORIZONTAL;
        cCn.weightx = 1;
        cCn.anchor = GridBagConstraints.CENTER;
        cCn.insets = new Insets(10, 10, 10, 10);
        khungCn.add(btnSua, cCn);

        JButton btnXoa = new taoNut().button("Xóa", "Arial", Font.BOLD, 13);
        btnXoa.addActionListener(e -> {
            int r = bang.getSelectedRow();
            if(r == -1){
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách");
                return;
            }
            String maSach = (String) model.getValueAt(r, 1);
            int xacNhan = JOptionPane.showConfirmDialog(
                this, "Bạn có chắc là muốn xóa sách " + maSach + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION
            );
            if(xacNhan != JOptionPane.YES_OPTION){
                return;
            }
            try{
                new ManagerService().xoaSach(maSach);
                JOptionPane.showMessageDialog(this, "Xóa sách thành công!");
                new SachDAO().napBang(model, new SachDAO().findAll());
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        cCn.gridx = 2;
        cCn.gridy = 0;
        cCn.fill = GridBagConstraints.HORIZONTAL;
        cCn.weightx = 1;
        cCn.anchor = GridBagConstraints.CENTER;
        cCn.insets = new Insets(10, 10, 10, 10);
        khungCn.add(btnXoa, cCn);

        c.gridx = 0;
        c.gridy = 6;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 10, 10, 10);
        p.add(khungCn, c);
        return p;
    }

    private void timKiemSach(JTextField txtKeyWord, JRadioButton rdFindById, 
        JRadioButton rdFindByTen, JRadioButton rdFindByTacGia, JRadioButton rdFindByTheLoai
    ) throws SQLException {
        if(!rdFindById.isSelected() && !rdFindByTacGia.isSelected() && !rdFindByTen.isSelected()
            && !rdFindByTheLoai.isSelected()){
                throw new IllegalArgumentException("Vui lòng chọn một phương thức tìm kiếm");
        }

        if(rdFindById.isSelected()){
            Sach s = new ManagerService().findSachById(txtKeyWord.getText().trim());
            List<Sach> ds = new ArrayList<>();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách có mã: " + txtKeyWord.getText().trim());
            }
            if(s != null) ds.add(s);
            new SachDAO().napBang(model, ds);
        } else if(rdFindByTen.isSelected()){
            List<Sach> ds = new ManagerService().searchSachTheoTen(txtKeyWord.getText().trim());
            new SachDAO().napBang(model, ds);
        } else if(rdFindByTacGia.isSelected()){
            List<Sach> ds = new ManagerService().searchSachTheoTacGia(txtKeyWord.getText().trim());
            new SachDAO().napBang(model, ds);
        } else {
            TheLoai tl = new TheLoaiDAO().findByTen(txtKeyWord.getText().trim());
            List<Sach> ds = new ManagerService().searchSachTheoTheLoai(tl.getMaTheLoai());
            new SachDAO().napBang(model, ds);
        }
    }

    private void themSach(JTextField txtMaSach, JTextField txtTenSach, JTextField txtNhaXb, JTextField txtGiaTri
        ,JTextField txtNamXb, JTextField txtTacGia, JTextField txtMaTheLoai, JSpinner spSoLuong
    ) throws SQLException {
        String maSach = txtMaSach.getText().trim();
        String tenSach = txtTenSach.getText().trim();
        String nhaXuatBan = txtNhaXb.getText().trim();
        int namXuatBan;
        BigDecimal giaTri;
        try{
            namXuatBan = Integer.parseInt(txtNamXb.getText().trim());
        } catch(NumberFormatException ex){
            throw new IllegalArgumentException("Năm xuất bản phải là số nguyên");
        }
        String maTheLoai = txtMaTheLoai.getText().trim();
        try {
            giaTri = new BigDecimal(txtGiaTri.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Giá trị phải là số");
        }
        int soLuong = (int) spSoLuong.getValue();

        Sach sach = new Sach(maSach, tenSach, nhaXuatBan, namXuatBan, giaTri, soLuong, TrangThaiSach.BINH_THUONG, maTheLoai);
        new ManagerService().themSach(sach);
    }

    private void ganMaSv(JComboBox<String> cbKhoa, JTextField maSv) throws SQLException{
        String khoa = (String) cbKhoa.getSelectedItem();
        try(Connection conn = DBConnection.getConnection()){
            String maSinhVien;
            if(khoa.equals("Điện - Điện Tử")){
                maSinhVien = MaTuDong.taoMa(conn, "sinhVien", "maSinhVien", "DDT", 4);
            } else {
                maSinhVien = MaTuDong.taoMa(conn, "sinhVien", "maSinhVien", vietTat(khoa), 4);
            }
            maSv.setText(maSinhVien);
        }
    }

    private String vietTat(String s){
        String[] tu = s.split("\\s+");
        String vt = "";
        for (String tmp : tu){
            vt += tmp.substring(0, 1).toUpperCase(); 
        }
        return vt;
    }

    private LocalDate chuyenStrSangLocalDate(String s){
        try{
            return LocalDate.parse(s.trim(), DateTimeFormatter.ofPattern("d/M/uuuu"));
        } catch(DateTimeParseException ex){
            throw new IllegalArgumentException("Ngày sinh phải đúng dạng dd/mm/yyyy (ví dụ 12/03/2005)");
        }
    }

    private void timKiem(JTextField txtKeyWord, JRadioButton findByTen, JRadioButton findByMaSv){
        String keyword = txtKeyWord.getText().trim();
        if(!findByTen.isSelected() && !findByMaSv.isSelected()){
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức tìm kiếm");
            return;
        }
        try{
            List<SinhVien> ds = new ManagerService().searchSinhVien(keyword, findByMaSv.isSelected());
            new SinhVienDAO().napBang(model, ds);
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
    }

    private void themSinhVien(
        JTextField txtMaSv, JTextField txtMaTk, JTextField txtHo, JTextField txtTen,
        JTextField txtNgaySinh, JRadioButton rdNam, JRadioButton rdNu, JRadioButton rdKhac, 
        JTextField txtEmail, JTextField txtSdt, JTextField txtLop, JComboBox<String> cbKhoa
    ) throws SQLException {
        String maSv = txtMaSv.getText().trim();
        String maTk = txtMaTk.getText().trim();
        String ho = txtHo.getText().trim();
        String ten = txtTen.getText().trim();
        LocalDate ngaySinh = chuyenStrSangLocalDate(txtNgaySinh.getText());
        String email = txtEmail.getText().trim();
        String sdt = txtSdt.getText().trim();
        String lop = txtLop.getText().trim();
        String khoa = (String) cbKhoa.getSelectedItem();
        Sex gioiTinh = null;
        if(rdNam.isSelected()) gioiTinh = Sex.NAM;
        else if(rdNu.isSelected()) gioiTinh = Sex.NU;
        else if(rdKhac.isSelected()) gioiTinh = Sex.KHAC;

        SinhVien sv = new SinhVien();
        sv.setMaSinhVien(maSv);
        sv.setMaTaiKhoan(maTk);
        sv.setHo(ho);
        sv.setTen(ten);
        sv.setGioiTinh(gioiTinh);
        sv.setNgaySinh(ngaySinh);
        sv.setEmail(email);
        sv.setSdt(sdt);
        sv.setLop(lop);
        sv.setKhoa(khoa);
        new ManagerService().themSinhVien(sv);
    }

    private void ganMaTk(JTextField txtMaTk) throws SQLException{
    try(Connection conn = DBConnection.getConnection()){
        txtMaTk.setText(MaTuDong.taoMa(conn, "TaiKhoan", "maTaiKhoan", "TK", 4));
    }
}

    private void lamMoiFormSinhVien(JTextField txtMaSv, JTextField txtMaTk, JTextField txtHo, JTextField txtTen,
            JTextField txtNgaySinh, JTextField txtSdt, JTextField txtEmail, JTextField txtLop,
            ButtonGroup grGt, JComboBox<String> bKhoa) throws SQLException{
        txtHo.setText("");
        txtTen.setText("");
        txtNgaySinh.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        txtLop.setText("");
        grGt.clearSelection();
        ganMaSv(bKhoa, txtMaSv);
        ganMaTk(txtMaTk);
        new SinhVienDAO().napBang(model, new SinhVienDAO().findAll());
    }

    private void moDialogSuaSach(Sach sach){
        JDialog dl = new JDialog(this, "Sửa thông tin sách", true);
        String maTheLoaiCu = sach.getMaTheLoai();

        JPanel p = new JPanel(new GridBagLayout());
        JLabel tieuDe = new taoTieuDe().label("Sửa Thông Tin Sách");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(10, 10, 10, 10);
        p.add(tieuDe, c);

        JTextField txtMaSach = new JTextField(sach.getMaSach(), 20);
        txtMaSach.setEditable(false);
        JTextField txtTenSach = new JTextField(sach.getTenSach(), 20);
        JTextField txtMaTheLoai = new JTextField(maTheLoaiCu == null ? "" : maTheLoaiCu, 20);
        txtMaTheLoai.setEditable(false);
        JTextField txtNhaXb = new JTextField(sach.getNhaXuatBan() == null ? "" : sach.getNhaXuatBan(), 20);
        JTextField txtNamXb = new JTextField(String.valueOf(sach.getNamXuatBan()), 20);
        JTextField txtGiaTri = new JTextField(sach.getGiaTri() == null ? "" : sach.getGiaTri().toPlainString(), 20);
        JSpinner spSoLuong = new JSpinner(new SpinnerNumberModel(sach.getSoLuong(), 0, Integer.MAX_VALUE, 1));

        JComboBox<TheLoai> cbTheLoai = new JComboBox<>();
        try{
            boolean timThay = false;
            for(TheLoai tl : new TheLoaiDAO().findAll()){
                cbTheLoai.addItem(tl);
                if(tl.getMaTheLoai().equals(maTheLoaiCu)){
                    cbTheLoai.setSelectedItem(tl);
                    timThay = true;
                }
            }
            if(!timThay){
                cbTheLoai.setSelectedIndex(-1);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
        }
        cbTheLoai.addActionListener(e -> {
            TheLoai tl = (TheLoai) cbTheLoai.getSelectedItem();
            if(tl == null) return;
            txtMaTheLoai.setText(tl.getMaTheLoai());
        });

        JPanel khung = new taoKhung().panel();
        GridBagConstraints k = new GridBagConstraints();
        themDongDialog(khung, k, 0, "Mã sách", txtMaSach);
        themDongDialog(khung, k, 1, "Tên sách", txtTenSach);
        themDongDialog(khung, k, 2, "Nhà xuất bản", txtNhaXb);
        themDongDialog(khung, k, 3, "Năm xuất bản", txtNamXb);
        themDongDialog(khung, k, 4, "Mã thể loại", txtMaTheLoai);
        themDongDialog(khung, k, 5, "Thể loại", cbTheLoai);
        themDongDialog(khung, k, 6, "Giá trị", txtGiaTri);
        themDongDialog(khung, k, 7, "Số lượng", spSoLuong);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        p.add(khung, c);

        JButton btnThayDoi = new taoNut().Button("Thay đổi");
        btnThayDoi.addActionListener(e -> {
            try{
                thayDoiSach(sach, txtTenSach, txtNhaXb, txtNamXb, txtGiaTri, spSoLuong, cbTheLoai);
                JOptionPane.showMessageDialog(this, "Thay đổi thông tin sách thành công!");
                dl.dispose();
                new SachDAO().napBang(model, new SachDAO().findAll());
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        JButton btnHuy = new taoNut().Button("Hủy");
        btnHuy.addActionListener(e -> dl.dispose());
        JPanel nut = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        nut.add(btnThayDoi);
        nut.add(btnHuy);

        c.gridx = 0;
        c.gridy = 2;
        p.add(nut, c);

        dl.setContentPane(p);
        dl.pack();
        dl.setResizable(false);
        dl.setLocationRelativeTo(this);
        dl.setVisible(true);
    }

    private void themDongDialog(JPanel khung, GridBagConstraints k, int y, String nhan, JComponent o){
        k.gridx = 0;
        k.gridy = y;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(6, 10, 6, 10);
        khung.add(new taoDongNoiDung().Label(nhan, "Arial", Font.BOLD, 15), k);
        k.gridx = 1;
        khung.add(o, k);
    }

    private void thayDoiSach(Sach sach, JTextField txtTenSach,
        JTextField txtNhaXb, JTextField txtNamXb, JTextField txtGiaTri, JSpinner spSoLuong,
        JComboBox<TheLoai> cbTheLoai
    ) throws SQLException {
        int namXuatBan;
        BigDecimal giaTri;
        try{
            namXuatBan = Integer.parseInt(txtNamXb.getText().trim());
        } catch(NumberFormatException ex){
            throw new IllegalArgumentException("Năm xuất bản phải là số nguyên");
        }
        try{
            giaTri = new BigDecimal(txtGiaTri.getText().trim());
        } catch(NumberFormatException ex){
            throw new IllegalArgumentException("Giá trị phải là số");
        }
        TheLoai tl = (TheLoai) cbTheLoai.getSelectedItem();

        sach.setTenSach(txtTenSach.getText().trim());
        sach.setNhaXuatBan(txtNhaXb.getText().trim());
        sach.setNamXuatBan(namXuatBan);
        sach.setGiaTri(giaTri);
        sach.setSoLuong((int) spSoLuong.getValue());
        sach.setMaTheLoai(tl == null ? null : tl.getMaTheLoai());
        new ManagerService().suaThongTinSach(sach);
    }

    private void moDialogSua(SinhVien sv){
        JDialog dl = new JDialog(this, "Sửa thông tin sinh viên", true);

        JPanel p = new JPanel(new GridBagLayout());
        JLabel tieuDe = new taoTieuDe().label("Sửa Thông Tin Sinh Viên");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        c.insets = new Insets(10, 10, 10, 10);
        p.add(tieuDe, c);

        //khung 
        JPanel khung = new taoKhung().panel();
        GridBagConstraints k = new GridBagConstraints();
        JLabel lbMaSv = new taoDongNoiDung().Label("Mã SV", "Arial", Font.BOLD, 15);
        JTextField txtMaSv = new JTextField(sv.getMaSinhVien(), 20);
        txtMaSv.setEditable(false);
        k.gridx = 0;
        k.gridy = 0;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbMaSv, k);

        k.gridx = 1;
        k.gridy = 0;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtMaSv, k);

        JLabel lbHoSv = new taoDongNoiDung().Label("Họ SV", "Arial", Font.BOLD, 15);
        JTextField txtHoSv = new JTextField(sv.getHo(), 20);
        k.gridx = 0;
        k.gridy = 1;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbHoSv, k);

        k.gridx = 1;
        k.gridy = 1;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtHoSv, k);

        JLabel lbTenSv = new taoDongNoiDung().Label("Tên SV", "Arial", Font.BOLD, 15);
        JTextField txtTenSv = new JTextField(sv.getTen(), 20);
        k.gridx = 0;
        k.gridy = 2;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbTenSv, k);

        k.gridx = 1;
        k.gridy = 2;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtTenSv, k);

        String[] sex = {"Nam", "Nữ", "Khác"};
        JComboBox<String> cbGioiTinh = new JComboBox<>(sex);
        if (sv.getGioiTinh() != null) {
            switch (sv.getGioiTinh()) {
                case NAM  -> cbGioiTinh.setSelectedItem("Nam");
                case NU   -> cbGioiTinh.setSelectedItem("Nữ");
                case KHAC -> cbGioiTinh.setSelectedItem("Khác");
            }
        }
        JLabel lbGioiTinh = new taoDongNoiDung().Label("Giới tính", "Arial", Font.BOLD, 15);
        k.gridx = 0;
        k.gridy = 3;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbGioiTinh, k);

        k.gridx = 1;
        k.gridy = 3;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(cbGioiTinh, k);

        JLabel lbNgaySinh = new taoDongNoiDung().Label("Ngày sinh", "Arial", Font.BOLD, 15);
        JTextField txtNgaySinh = new JTextField(sv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 20);
        k.gridx = 0;
        k.gridy = 4;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbNgaySinh, k);

        k.gridx = 1;
        k.gridy = 4;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtNgaySinh, k);

        JLabel lbSdt = new taoDongNoiDung().Label("Số điện thoại", "Arial", Font.BOLD, 15);
        JTextField txtSdt = new JTextField(sv.getSdt(), 20);
        k.gridx = 0;
        k.gridy = 5;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbSdt, k);

        k.gridx = 1;
        k.gridy = 5;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtSdt, k);

        JLabel lbEmail = new taoDongNoiDung().Label("Email", "Arial", Font.BOLD, 15);
        JTextField txtEmail = new JTextField(sv.getEmail(), 20);
        k.gridx = 0;
        k.gridy = 6;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbEmail, k);

        k.gridx = 1;
        k.gridy = 6;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtEmail, k);

        JLabel lbLop = new taoDongNoiDung().Label("Lớp", "Arial", Font.BOLD, 15);
        JTextField txtLop = new JTextField(sv.getLop(), 20);
        k.gridx = 0;
        k.gridy = 7;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbLop, k);

        k.gridx = 1;
        k.gridy = 7;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtLop, k);

        JLabel lbKhoa = new taoDongNoiDung().Label("Khoa", "Arial", Font.BOLD, 15);
        JTextField txtKhoa = new JTextField(sv.getKhoa(), 20);
        txtKhoa.setEditable(false);
        k.gridx = 0;
        k.gridy = 8;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(0, 10, 10, 10);
        khung.add(lbKhoa, k);

        k.gridx = 1;
        k.gridy = 8;
        k.fill = GridBagConstraints.HORIZONTAL;
        k.weightx = 0;
        k.anchor = GridBagConstraints.WEST;
        k.insets = new Insets(10, 10, 10, 10);
        khung.add(txtKhoa, k);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(20, 10, 10, 10);
        p.add(khung, c);

        JButton btnThayDoiTTSV = new taoNut().Button("Thay đổi");
        btnThayDoiTTSV.addActionListener(e -> {
            try{
                thayDoiTTSV(txtMaSv, txtHoSv, txtTenSv, txtNgaySinh, cbGioiTinh, txtEmail, txtSdt, txtLop, sv);
                JOptionPane.showMessageDialog(this, "Thay đổi thông tin sinh viên thành công!");
                dl.dispose();
                new SinhVienDAO().napBang(model, new SinhVienDAO().findAll());
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage());
            }
        });
        JButton btnHuySua = new taoNut().Button("Hủy");
        btnHuySua.addActionListener(e -> {
            dl.dispose();
        });
        JPanel nut = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));

        nut.add(btnThayDoiTTSV);
        nut.add(btnHuySua);

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        p.add(nut, c);
        
        dl.setContentPane(p);
        dl.pack();
        dl.setResizable(false);
        dl.setLocationRelativeTo(this);
        dl.setVisible(true);
    }

    private void thayDoiTTSV(
        JTextField txtMaSv, JTextField txtHo, JTextField txtTen,
        JTextField txtNgaySinh, JComboBox<String> cbGioiTinh, 
        JTextField txtEmail, JTextField txtSdt, JTextField txtLop, SinhVien sv
    ) throws SQLException {
        String ho = txtHo.getText().trim();
        String ten = txtTen.getText().trim();
        LocalDate ngaySinh = chuyenStrSangLocalDate(txtNgaySinh.getText());
        String email = txtEmail.getText().trim();
        String sdt = txtSdt.getText().trim();
        String lop = txtLop.getText().trim();
        String chon = (String) cbGioiTinh.getSelectedItem();
        Sex gioiTinh = null;
        if("Nam".equals(chon)) gioiTinh = Sex.NAM;
        else if("Nữ".equals(chon)) gioiTinh = Sex.NU;
        else if("Khác".equals(chon)) gioiTinh = Sex.KHAC;

        sv.setHo(ho);
        sv.setTen(ten);
        sv.setGioiTinh(gioiTinh);
        sv.setNgaySinh(ngaySinh);
        sv.setEmail(email);
        sv.setSdt(sdt);
        sv.setLop(lop);
        new ManagerService().suaThongTinSinhVien(sv);
    }
}