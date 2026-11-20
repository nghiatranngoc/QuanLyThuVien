package Services;

import model.*;
import dao.*;
import database.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import enums.*;

public class AuthService {
    private SinhVienDAO svDao = new SinhVienDAO();
    private QuanLyDAO qlDao = new QuanLyDAO();
    public TaiKhoan dangNhap(String userName, String password) throws SQLException{
        if(userName == null || userName.trim().isEmpty()){
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập");
        }

        if(password == null || password.trim().isEmpty()){
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu");
        }

        TaiKhoanDAO tkDao = new TaiKhoanDAO();
        TaiKhoan tk = tkDao.findbyUserName(userName);
        if(tk == null){
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        if(!password.equals(tk.getPassword())){
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        return tk;
    }

    public String layTenHienThi(TaiKhoan tk) throws SQLException {
        if (tk.getRole() == Role.STUDENT) {
            SinhVien sv = svDao.findbyTaiKhoan(tk.getMaTaiKhoan());
            if (sv != null) {
                return sv.getHo() + " " + sv.getTen();
            }
        } else {
            QuanLy ql = qlDao.findbyTaiKhoan(tk.getMaTaiKhoan());
            if (ql != null) {
                return ql.getHo() + " " + ql.getTen();
            }
        }
        return tk.getUserName();
    }

    public String xemTruocMaTaiKhoan() throws SQLException {
    try (Connection conn = DBConnection.getConnection()) {
        return MaTuDong.taoMa(conn, "TaiKhoan", "maTaiKhoan", "TK", 6);
    }
}

    public String xemTruocMaSinhVien(String maKhoa) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return MaTuDong.taoMa(conn, "sinhVien", "maSinhVien", maKhoa, 5);
        }
    }

    public String xemTruocMaTaiKhoanQuanLy() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return MaTuDong.taoMa(conn, "TaiKhoan", "maTaiKhoan", "TKMNG", 3);
        }
    }

    public String xemTruocMaQuanLy() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return MaTuDong.taoMa(conn, "quanLy", "maQl", "MNG", 4);
        }
    }

    public void kiemTraDangKyQuanLy(String userName, String password, String nhapLai, String ho, String ten)
            throws SQLException {
        if (ho == null || ho.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập họ");
        }
        if (ten == null || ten.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập");
        }
        if (userName.trim().contains(" ")) {
            throw new IllegalArgumentException("Tên đăng nhập không được chứa khoảng trắng");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu");
        }
        if (!password.equals(nhapLai)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp");
        }
        if (new TaiKhoanDAO().findbyUserName(userName.trim()) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
    }

    public void dangKyQuanLy(String userName, String password, String nhapLai, String ho, String ten)
            throws SQLException {
        kiemTraDangKyQuanLy(userName, password, nhapLai, ho, ten);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String maTk = MaTuDong.taoMa(conn, "TaiKhoan", "maTaiKhoan", "TKMNG", 3);
                String maQl = MaTuDong.taoMa(conn, "quanLy", "maQl", "MNG", 4);

                TaiKhoan tk = new TaiKhoan();
                tk.setMaTaiKhoan(maTk);
                tk.setUserName(userName.trim());
                tk.setPassword(password);
                tk.setRole(Role.MANAGER);

                QuanLy ql = new QuanLy();
                ql.setMaQl(maQl);
                ql.setHo(ho.trim());
                ql.setTen(ten.trim());
                ql.setMaTaiKhoan(maTk);

                new TaiKhoanDAO().insert(conn, tk);
                qlDao.insert(conn, ql);
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}