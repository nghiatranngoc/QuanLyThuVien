package dao;

import database.DBConnection;
import enums.TinhTrangMuon;
import model.ChiTietPhieuMuon;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class chiTietPhieuMuonDAO {

    private static final String COLS = "maPhieu, maSach, hanTra, ngayTra, tinhTrang, tienPhat";

    private ChiTietPhieuMuon map(ResultSet rs) throws SQLException {
        ChiTietPhieuMuon c = new ChiTietPhieuMuon();
        c.setMaPhieu(rs.getString("maPhieu"));
        c.setMaSach(rs.getString("maSach"));
        c.setHanTra(DaoUtil.toLocalDate(rs.getDate("hanTra")));
        c.setNgayTra(DaoUtil.toLocalDate(rs.getDate("ngayTra")));
        c.setTinhTrang(TinhTrangMuon.valueOf(rs.getString("tinhTrang")));
        c.setTienPhat(rs.getBigDecimal("tienPhat"));
        return c;
    }

    public boolean insert(ChiTietPhieuMuon c) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, c);
        }
    }

    public boolean insert(Connection conn, ChiTietPhieuMuon c) throws SQLException {
        String sql = "INSERT INTO chitietPhieuMuon(" + COLS + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getMaPhieu());
            ps.setString(2, c.getMaSach());
            ps.setDate(3, DaoUtil.tosqlDate(c.getHanTra()));
            ps.setDate(4, DaoUtil.tosqlDate(c.getNgayTra()));
            ps.setString(5, c.getTinhTrang().name());
            ps.setBigDecimal(6, c.getTienPhat() == null ? BigDecimal.ZERO : c.getTienPhat());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(ChiTietPhieuMuon c) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return update(conn, c);
        }
    }

    public boolean update(Connection conn, ChiTietPhieuMuon c) throws SQLException {
        String sql = "UPDATE chitietPhieuMuon SET hanTra = ?, ngayTra = ?, tinhTrang = ?, tienPhat = ? "
                + "WHERE maPhieu = ? AND maSach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, DaoUtil.tosqlDate(c.getHanTra()));
            ps.setDate(2, DaoUtil.tosqlDate(c.getNgayTra()));
            ps.setString(3, c.getTinhTrang().name());
            ps.setBigDecimal(4, c.getTienPhat() == null ? BigDecimal.ZERO : c.getTienPhat());
            ps.setString(5, c.getMaPhieu());
            ps.setString(6, c.getMaSach());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maPhieu, String maSach) throws SQLException {
        String sql = "DELETE FROM chitietPhieuMuon WHERE maPhieu = ? AND maSach = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ps.setString(2, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    public ChiTietPhieuMuon findById(String maPhieu, String maSach) throws SQLException {
        String sql = "SELECT " + COLS + " FROM chitietPhieuMuon WHERE maPhieu = ? AND maSach = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ps.setString(2, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<ChiTietPhieuMuon> findByPhieu(String maPhieu) throws SQLException {
        return query("SELECT " + COLS + " FROM chitietPhieuMuon WHERE maPhieu = ? ORDER BY maSach", maPhieu);
    }

    public List<ChiTietPhieuMuon> findAll() throws SQLException {
        return query("SELECT " + COLS + " FROM chitietPhieuMuon ORDER BY maPhieu, maSach", null);
    }

    public List<ChiTietPhieuMuon> findDangMuon() throws SQLException {
        return query("SELECT " + COLS + " FROM chitietPhieuMuon WHERE tinhTrang = 'CHUA_TRA' ORDER BY hanTra", null);
    }

    public List<ChiTietPhieuMuon> findQuaHan() throws SQLException {
        return query("SELECT " + COLS + " FROM chitietPhieuMuon "
                + "WHERE tinhTrang = 'CHUA_TRA' AND hanTra < CURDATE() ORDER BY hanTra", null);
    }

    public List<ChiTietPhieuMuon> findBySinhVien(String maSinhVien) throws SQLException {
        String sql = "SELECT c.maPhieu, c.maSach, c.hanTra, c.ngayTra, c.tinhTrang, c.tienPhat "
                + "FROM chitietPhieuMuon c JOIN phieuMuon p ON c.maPhieu = p.maPhieu "
                + "WHERE p.maSinhVien = ? ORDER BY p.ngayMuon DESC, c.maPhieu, c.maSach";
        return query(sql, maSinhVien);
    }

    public int demDangMuon(String maSinhVien) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return demDangMuon(conn, maSinhVien);
        }
    }

    public int demDangMuon(Connection conn, String maSinhVien) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chitietPhieuMuon c JOIN phieuMuon p ON c.maPhieu = p.maPhieu "
                + "WHERE p.maSinhVien = ? AND c.tinhTrang = 'CHUA_TRA'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public BigDecimal tongTienPhat() throws SQLException {
        String sql = "SELECT COALESCE(SUM(tienPhat), 0) FROM chitietPhieuMuon";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private List<ChiTietPhieuMuon> query(String sql, String param) throws SQLException {
        List<ChiTietPhieuMuon> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }
}