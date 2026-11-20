package dao;

import database.DBConnection;
import model.Sach;
import model.TacGia;
import model.TheLoai;
import enums.TrangThaiSach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;

public class SachDAO {
    private static final String cols = "maSach, tenSach, nhaXuatBan, namXuatBan, giaTri, soLuong, trangThai, maTheLoai";
    private static final String COLS = "s.maSach, s.tenSach, s.nhaXuatBan, s.namXuatBan, s.giaTri, s.soLuong, s.trangThai, s.maTheLoai";
    private Sach map(ResultSet rs) throws SQLException{
        Sach s = new Sach();
        s.setMaSach(rs.getString("maSach"));
        s.setTenSach(rs.getString("tenSach"));
        s.setNhaXuatBan(rs.getString("nhaXuatBan"));
        s.setNamXuatBan(rs.getInt("namXuatBan"));
        s.setGiaTri(rs.getBigDecimal("giaTri"));
        s.setSoLuong(rs.getInt("soLuong"));
        s.setTrangThai(TrangThaiSach.valueOf(rs.getString("trangThai")));
        s.setMaTheLoai(rs.getString("maTheLoai"));
        return s;
    }

    private void loadTacGia(Connection conn, Sach s) throws SQLException{
        String sql = "SELECT tg.maTacGia, tg.ho, tg.ten FROM tacGia AS tg "
                    + "INNER JOIN sach_tacgia AS st ON tg.maTacGia = st.maTacGia "
                    + "WHERE st.maSach = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, s.getMaSach());
            try(ResultSet rs = ps.executeQuery()){
                List<TacGia> dsTg = new ArrayList<>();
                while(rs.next()){
                    TacGia tg = new TacGia();
                    tg.setMaTacGia(rs.getString("maTacGia"));
                    tg.setHo(rs.getString("ho"));
                    tg.setTen(rs.getString("ten"));
                    dsTg.add(tg);
                }
                s.setDsTacGia(dsTg);
            }
        }
    }

    private void insertLienKetTacGia(Connection conn, Sach s)throws SQLException{
        String sql = "INSERT INTO sach_tacgia(maSach, maTacGia) VALUES(?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            for(TacGia tg : s.getDsTacGia()){
                ps.setString(1, s.getMaSach());
                ps.setString(2, tg.getMaTacGia());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean insert(Sach s) throws SQLException{
        String sql = "INSERT INTO sach(" + cols + ") VALUES(?, ?, ?, ?, ?, ?,?, ?)";
        try(Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
                int row;
                try(PreparedStatement ps = conn.prepareStatement(sql)){
                    ps.setString(1, s.getMaSach());
                    ps.setString(2, s.getTenSach());
                    ps.setString(3, s.getNhaXuatBan());
                    ps.setInt(4, s.getNamXuatBan());
                    ps.setBigDecimal(5, s.getGiaTri() == null ? BigDecimal.ZERO : s.getGiaTri());
                    ps.setInt(6, s.getSoLuong());
                    ps.setString(7, s.getTrangThai().name());
                    ps.setString(8, s.getMaTheLoai());
                    row = ps.executeUpdate();
                }
                insertLienKetTacGia(conn, s);
                conn.commit();
                return row > 0;
            } catch(SQLException se){
                conn.rollback();
                throw se;
            }
        }
    }

    public boolean update(Sach s) throws SQLException{
        String sql = "UPDATE sach SET tenSach = ?, nhaXuatBan = ?, namXuatBan = ?, "
                    + "giaTri = ?, soLuong = ?, trangThai = ?, maTheLoai = ? "
                    + "WHERE maSach = ?";
        try(Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
                int rows;
                try(PreparedStatement ps = conn.prepareStatement(sql)){
                    ps.setString(1, s.getTenSach());
                    ps.setString(2, s.getNhaXuatBan());
                    ps.setInt(3, s.getNamXuatBan());
                    ps.setBigDecimal(4, s.getGiaTri() == null ? BigDecimal.ZERO : s.getGiaTri());
                    ps.setInt(5, s.getSoLuong());
                    ps.setString(6, s.getTrangThai().name());
                    ps.setString(7, s.getMaTheLoai());
                    ps.setString(8, s.getMaSach());
                    rows = ps.executeUpdate();
                }
                try(PreparedStatement del = conn.prepareStatement("DELETE FROM sach_tacgia WHERE maSach = ?")) {
                    del.setString(1, s.getMaSach());
                    del.executeUpdate();
                }
                insertLienKetTacGia(conn, s);
                conn.commit();
                return rows > 0;
            } catch(SQLException se){
                conn.rollback();
                throw se;
            }
        }
    }

    public boolean delete(String maSach) throws SQLException{
        try(Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try{
                try(PreparedStatement del = conn.prepareStatement("DELETE FROM sach_tacgia WHERE maSach = ?")){
                    del.setString(1, maSach);
                    del.executeUpdate();
                }
                int rows;
                try(PreparedStatement ps = conn.prepareStatement("DELETE FROM sach WHERE maSach = ?")){
                    ps.setString(1, maSach);
                    rows = ps.executeUpdate();
                }
                conn.commit();
                return rows > 0;
            } catch(SQLException se){
                conn.rollback();
                throw se;
            }
        }
    }

    public boolean daCoTrongPhieuMuon(String maSach) throws SQLException{
        String sql = "SELECT 1 FROM chitietPhieuMuon WHERE maSach = ? LIMIT 1";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maSach);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next();
                }
            }
    }

    public Sach findbyId(String maSach) throws SQLException{
        String sql = "SELECT " + cols + " FROM sach WHERE maSach = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maSach);
                try(ResultSet rs = ps.executeQuery();){
                    if(!rs.next()){
                        return null;
                    }

                    Sach s = map(rs);
                    loadTacGia(conn, s);
                    return s;
                }
            }
    }

    public List<Sach> findAll() throws SQLException{
        String sql = "SELECT " + cols + " FROM sach";
        List<Sach> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    loadTacGia(conn, map(rs));
                    list.add(map(rs));
                }
            }
        return list;
    }

    public List<Sach> searchByTen(String keyword) throws SQLException{
        String sql = "SELECT " + cols + " FROM sach "
                    + "WHERE tenSach LIKE ? "
                    + "ORDER BY tenSach";
        List<Sach> list = new ArrayList<>();
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

    public List<Sach> searchByTacGia(String keyword) throws SQLException{
        String sql = "SELECT DISTINCT " + COLS + " FROM sach AS s "
                    + "INNER JOIN sach_tacgia AS st ON s.maSach = st.maSach "
                    + "INNER JOIN tacGia AS tg ON tg.maTacGia = st.maTacGia "
                    + "WHERE CONCAT(ho, ' ', ten) LIKE ? " 
                    + "ORDER BY s.tenSach";
        List<Sach> list = new ArrayList<>();
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

    public List<Sach> searchByTheLoai(String maTheLoai) throws SQLException{
        String sql = "SELECT " + cols + " FROM sach WHERE maTheLoai = ? ORDER BY tenSach";
        List<Sach> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, maTheLoai);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        return list;
    }

    public boolean giamSoLuong(Connection conn, String maSach) throws SQLException {
        String sql = "UPDATE sach SET soLuong = soLuong - 1 WHERE maSach = ? AND soLuong > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean tangSoLuong(Connection conn, String maSach) throws SQLException {
        String sql = "UPDATE sach SET soLuong = soLuong + 1 WHERE maSach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    public void napBang(DefaultTableModel model, List<Sach> ds) throws SQLException{
        model.setRowCount(0);
        int stt = 1;
        for(Sach s : ds){
            TheLoai tl = new TheLoaiDAO().findById(s.getMaTheLoai());
            List<String> names = new ArrayList<>();
            String tenTacGia;
            for(TacGia tg : s.getDsTacGia()){
                names.add(tg.getTen());
            }
            tenTacGia = String.join(", ", names);
            model.addRow(new Object[]{
                stt++,
                s.getMaSach(),
                s.getTenSach(),
                tenTacGia,
                s.getNhaXuatBan(),
                s.getNamXuatBan(),
                tl.getTenTheLoai(),
                s.getGiaTri(),
                s.getSoLuong()
            });
        }
    }
}