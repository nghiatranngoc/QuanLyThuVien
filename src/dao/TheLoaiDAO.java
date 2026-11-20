package dao;

import database.DBConnection;
import model.TheLoai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TheLoaiDAO {

    private static final String COLS = "maTheLoai, tenTheLoai";

    private TheLoai map(ResultSet rs) throws SQLException {
        TheLoai t = new TheLoai();
        t.setMaTheLoai(rs.getString("maTheLoai"));
        t.setTenTheLoai(rs.getString("tenTheLoai"));
        return t;
    }

    public boolean insert(TheLoai t) throws SQLException {
        String sql = "INSERT INTO theLoai(" + COLS + ") VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getMaTheLoai());
            ps.setString(2, t.getTenTheLoai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(TheLoai t) throws SQLException {
        String sql = "UPDATE theLoai SET tenTheLoai = ? WHERE maTheLoai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTenTheLoai());
            ps.setString(2, t.getMaTheLoai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maTheLoai) throws SQLException {
        String sql = "DELETE FROM theLoai WHERE maTheLoai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTheLoai);
            return ps.executeUpdate() > 0;
        }
    }

    /** Trả về null nếu không tìm thấy. */
    public TheLoai findById(String maTheLoai) throws SQLException {
        String sql = "SELECT " + COLS + " FROM theLoai WHERE maTheLoai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTheLoai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public TheLoai findByTen(String tenTheLoai) throws SQLException{
        String sql = "SELECT " + COLS + " FROM theLoai WHERE tenTheLoai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenTheLoai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<TheLoai> findAll() throws SQLException {
        String sql = "SELECT " + COLS + " FROM theLoai ORDER BY tenTheLoai";
        List<TheLoai> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<TheLoai> search(String keyword) throws SQLException {
        String sql = "SELECT " + COLS + " FROM theLoai WHERE tenTheLoai LIKE ? ORDER BY tenTheLoai";
        List<TheLoai> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }
}