package dao;

import model.TaiKhoan;
import database.DBConnection;
import enums.Role;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;

public class TaiKhoanDAO {
    private static final String cols = "maTaiKhoan, userName, password, role";

    private TaiKhoan map(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(rs.getString("maTaiKhoan"));
        tk.setUserName(rs.getString("userName"));
        tk.setPassword(rs.getString("password"));
        tk.setRole(Role.valueOf(rs.getString("role")));
        return tk;
    }

    public boolean insert(TaiKhoan tk) throws SQLException{
        try(Connection conn = DBConnection.getConnection()){
            return insert(conn, tk);
        }
    }

    public boolean insert(Connection conn, TaiKhoan tk) throws SQLException{
        String sql = "INSERT INTO TaiKhoan(" + cols + ") VALUES(?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getUserName());
            ps.setString(3, tk.getPassword());
            ps.setString(4, tk.getRole().name());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(TaiKhoan tk) throws SQLException{
        String sql = "UPDATE TaiKhoan SET userName = ?, password = ?, role = ? WHERE maTaiKhoan = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, tk.getUserName());
                ps.setString(2, tk.getPassword());
                ps.setString(3, tk.getRole().name());
                ps.setString(4, tk.getMaTaiKhoan());
                return ps.executeUpdate() > 0;
        }
    }

    public boolean detele(String maTaiKhoan) throws SQLException{
        String sql = "DELETE FROM TaiKhoan WHERE maTaiKhoan = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTaiKhoan);
                return ps.executeUpdate() > 0;
        }
    }

    public TaiKhoan findbyId(String maTaiKhoan) throws SQLException{
        String sql = "SELECT " + cols + " FROM taiKhoan WHERE maTaiKhoan = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTaiKhoan);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? map(rs) : null;
                }
        }
    }

    public TaiKhoan findbyUserName(String userName) throws SQLException{
        String sql = "SELECT " + cols + " FROM TaiKhoan WHERE userName = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, userName);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? map(rs) : null;
                }
            }
    }

    public List<TaiKhoan> findAll() throws SQLException{
        String sql = "SELECT " + cols + " FROM TaiKhoan";
        List<TaiKhoan> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    list.add(map(rs));
            }
        }
        return list;
    }
}