package dao;

import model.QuanLy;

import database.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class QuanLyDAO {
    private static final String cols = "maQl, ho, ten, maTaiKhoan";

    private QuanLy map(ResultSet rs) throws SQLException{
        QuanLy ql = new QuanLy();
        ql.setMaQl(rs.getString("maQl"));
        ql.setHo(rs.getString("ho"));
        ql.setTen(rs.getString("ten"));
        ql.setMaTaiKhoan(rs.getString("maTaiKhoan"));
        return ql;
    }

    public boolean insert(QuanLy ql) throws SQLException{
        try(Connection conn = DBConnection.getConnection()){
            return insert(conn, ql);
        }
    }

    public boolean insert(Connection conn, QuanLy ql) throws SQLException{
        String sql = "INSERT INTO quanLy(" + cols + ") VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, ql.getMaQl());
            ps.setString(2, ql.getHo());
            ps.setString(3, ql.getTen());
            ps.setString(4, ql.getMaTaiKhoan());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(QuanLy ql) throws SQLException{
        String sql = "UPDATE quanLy SET maQl = ?, ho = ?, ten = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, ql.getHo());
                ps.setString(2, ql.getTen());
                return ps.executeUpdate() > 0;
            }
    }

    public boolean delete(String maQl) throws SQLException{
        String sql = "DELETE FROM quanLy WHERE maQl = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maQl);
                return ps.executeUpdate() > 0;
            }
    }

    public QuanLy findbyId(String maQl) throws SQLException{
        String sql = "SELECT " + cols + " FROM quanLy WHERE maQl = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maQl);
                try(ResultSet rs = ps.executeQuery()){
                    return rs != null ? map(rs) : null;
                }
            }
    }

    public QuanLy findbyTaiKhoan(String maTaiKhoan) throws SQLException{
        String sql = "SELECT " + cols + " FROM quanLy WHERE maTaiKhoan = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTaiKhoan);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? map(rs) : null;
                }
            }
    }

    public List<QuanLy> findAll() throws SQLException{
        String sql = "SELECT " + cols + " FROM quanLy";
        List<QuanLy> list = new ArrayList<>();
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