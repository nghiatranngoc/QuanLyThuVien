package dao;

import model.SinhVien;
import enums.Sex;

import java.sql.Connection;
import database.DBConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.lang.Object;

public class SinhVienDAO {
    private static final String cols = "maSinhVien, ho, ten, ngaySinh, gioiTinh, email, sdt, lop, khoa, maTaiKhoan";
    private SinhVien map(ResultSet rs) throws SQLException{
        SinhVien sv = new SinhVien();
        sv.setMaSinhVien(rs.getString("maSinhVien"));
        sv.setHo(rs.getString("ho"));
        sv.setTen(rs.getString("ten"));
        sv.setNgaySinh(DaoUtil.toLocalDate(rs.getDate("ngaySinh")));
        sv.setGioiTinh(Sex.valueOf(rs.getString("gioiTinh")));
        sv.setEmail(rs.getString("email"));
        sv.setSdt(rs.getString("sdt"));
        sv.setLop(rs.getString("lop"));
        sv.setKhoa(rs.getString("khoa"));
        sv.setMaTaiKhoan(rs.getString("maTaiKhoan"));
        return sv;
    }
    public boolean insert(SinhVien sv) throws SQLException{
        String sql = "INSERT INTO sinhVien(" + cols + ") VALUES(?, ? , ? , ? , ?, ? , ?, ?, ?, ?) ";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, sv.getMaSinhVien());
                ps.setString(2, sv.getHo());
                ps.setString(3, sv.getTen());
                ps.setDate(4, DaoUtil.tosqlDate(sv.getNgaySinh()));
                ps.setString(5, sv.getGioiTinh().name());
                ps.setString(6, sv.getEmail());
                ps.setString(7, sv.getSdt());
                ps.setString(8, sv.getLop());
                ps.setString(9, sv.getKhoa());
                ps.setString(10, sv.getMaTaiKhoan());

                return ps.executeUpdate() > 0;
            }
    }

    public boolean update(SinhVien sv) throws SQLException{
        String sql = "UPDATE sinhVien SET ho = ?, ten = ?, ngaySinh = ?, "
                    + "gioiTinh = ?, email = ?, sdt = ?, lop = ?, khoa = ? WHERE maSinhVien = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, sv.getHo());
                ps.setString(2, sv.getTen());
                ps.setDate(3, DaoUtil.tosqlDate(sv.getNgaySinh()));
                ps.setString(4, sv.getGioiTinh().name());
                ps.setString(5, sv.getEmail());
                ps.setString(6, sv.getSdt());
                ps.setString(7, sv.getLop());
                ps.setString(8, sv.getKhoa());
                ps.setString(9, sv.getMaSinhVien());

                return ps.executeUpdate() > 0;
            }
    }

    public boolean delete(String maSinhVien) throws SQLException{
        String sql = "DELETE FROM sinhVien WHERE maSinhVien = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maSinhVien);
                return ps.executeUpdate() > 0;
            }
    }

    public SinhVien findbyId(String maSinhVien) throws SQLException{
        String sql = "SELECT " + cols + " FROM sinhVien WHERE maSinhVien = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maSinhVien);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? map(rs) : null;
                }
            }
    }

    public SinhVien findbyTaiKhoan(String maTaiKhoan) throws SQLException{
        String sql = "SELECT " + cols + " FROM sinhVien WHERE maTaiKhoan = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTaiKhoan);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? map(rs) : null;
                }
            }
    }

    public List<SinhVien> findAll() throws SQLException{
        String sql = "SELECT " + cols + " FROM sinhVien";
        List<SinhVien> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        return list;
    }

    public List<SinhVien> search(String keyword, boolean theoMaSv) throws SQLException{
        String sql = "SELECT " + cols + " FROM sinhVien WHERE "
            + (theoMaSv ? "maSinhVien LIKE ?" : "CONCAT(ho, ' ', ten) LIKE ?")
            + " ORDER BY ten, ho";
        List<SinhVien> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + keyword + "%");
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void napBang(DefaultTableModel model, List<SinhVien> ds){
        model.setRowCount(0);
        int stt = 1;
        for(SinhVien sv : ds){
            model.addRow(new Object[]{
                stt++,
                sv.getMaSinhVien(),
                sv.getMaTaiKhoan(),
                sv.getHo(),
                sv.getTen(),
                sv.getNgaySinh(),
                sv.getGioiTinh(),
                sv.getEmail(),
                sv.getSdt(),
                sv.getLop(),
                sv.getKhoa()
            });
        }
    }

    public boolean existsByEmail(String email, String boQuaMaSv) throws SQLException{
        String sql = "SELECT 1 FROM sinhVien WHERE email = ? AND maSinhVien <> ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, email);
            ps.setString(2, boQuaMaSv);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }

    public boolean existsBySdt(String sdt, String boQuaMaSv) throws SQLException{
        String sql = "SELECT 1 FROM sinhVien WHERE sdt = ? AND maSinhVien <> ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, sdt);
            ps.setString(2, boQuaMaSv);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }
}
