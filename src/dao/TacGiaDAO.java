package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

import model.TacGia;

public class TacGiaDAO {
    private static final String cols = "maTacGia, ho, ten";

    private TacGia map(ResultSet rs) throws SQLException{
        TacGia tg = new TacGia();
        tg.setMaTacGia(rs.getString("maTacGia"));
        tg.setHo(rs.getString("ho"));
        tg.setTen(rs.getString("ten"));

        return tg;
    }

    public boolean insert(TacGia tg) throws SQLException{
        String sql = "INSERT INTO tacGia(" + cols + ") VALUES(?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, tg.getMaTacGia());
                ps.setString(2, tg.getHo());
                ps.setString(3, tg.getTen());

                return ps.executeUpdate() > 0;
            }
    }

    public boolean update(TacGia tg) throws SQLException{
        String sql = "UPDATE tacGia SET ho = ?, ten = ? WHERE maTacGia = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, tg.getHo());
                ps.setString(2, tg.getTen());
                ps.setString(3, tg.getMaTacGia());
                return ps.executeUpdate() > 0;
            }
    }

    public boolean delete(String maTacGia) throws SQLException{
        String sql = "DELETE FROM tacGia WHERE maTacGia = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTacGia);
                return ps.executeUpdate() > 0;
            }
    }

    public TacGia findbyId(String maTacGia) throws SQLException{
        String sql = "SELECT " + cols + " FROM tacGia WHERE maTacGia = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTacGia);
                try(ResultSet rs = ps.executeQuery()){
                    return rs != null ? map(rs) : null;
                }
            }
    }

    public List<TacGia> findAll() throws SQLException{
        String sql = "SELECT " + cols + " FROM tacGia";
        List<TacGia> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    list.add(map(rs));
                }
            }
            return list;
    }

    public List<TacGia> search(String keyword) throws SQLException{
        String sql = "SELECT " + cols + " FROM tacGia"
            + "WHERE maTacGia LIKE ? OR CONCAT(ho,' ', ten) LIKE ? OR ORDER BY ten, ho";
        List<TacGia> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, like);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    list.add(map(rs));
                }
            }
            return list;
    } 
}
