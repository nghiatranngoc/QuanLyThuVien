package dao;

import database.DBConnection;
import model.PhieuMuon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhieuMuonDAO {

    private static final String COLS = "maPhieu, ngayMuon, maSinhVien";

    private PhieuMuon map(ResultSet rs) throws SQLException {
        PhieuMuon p = new PhieuMuon();
        p.setMaPhieu(rs.getString("maPhieu"));
        p.setNgayMuon(DaoUtil.toLocalDate(rs.getDate("ngayMuon")));
        p.setMaSinhVien(rs.getString("maSinhVien"));
        return p;
    }

    public boolean insert(PhieuMuon p) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, p);
        }
    }

    public boolean insert(Connection conn, PhieuMuon p) throws SQLException {
        String sql = "INSERT INTO phieuMuon(" + COLS + ") VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getMaPhieu());
            ps.setDate(2, DaoUtil.tosqlDate(p.getNgayMuon()));
            ps.setString(3, p.getMaSinhVien());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(PhieuMuon p) throws SQLException {
        String sql = "UPDATE phieuMuon SET ngayMuon = ?, maSinhVien = ? WHERE maPhieu = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, DaoUtil.tosqlDate(p.getNgayMuon()));
            ps.setString(2, p.getMaSinhVien());
            ps.setString(3, p.getMaPhieu());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maPhieu) throws SQLException {
        String sql = "DELETE FROM phieuMuon WHERE maPhieu = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            return ps.executeUpdate() > 0;
        }
    }

    /** Trả về null nếu không tìm thấy. */
    public PhieuMuon findById(String maPhieu) throws SQLException {
        String sql = "SELECT " + COLS + " FROM phieuMuon WHERE maPhieu = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<PhieuMuon> findAll() throws SQLException {
        String sql = "SELECT " + COLS + " FROM phieuMuon ORDER BY ngayMuon DESC, maPhieu";
        List<PhieuMuon> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<PhieuMuon> findBySinhVien(String maSinhVien) throws SQLException {
        String sql = "SELECT " + COLS + " FROM phieuMuon WHERE maSinhVien = ? ORDER BY ngayMuon DESC, maPhieu";
        List<PhieuMuon> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }
}