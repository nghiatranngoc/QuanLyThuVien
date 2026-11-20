package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MaTuDong {
    private static final Set<String> HOP_LE = new HashSet<>(Arrays.asList(
            "phieuMuon.maPhieu",
            "TaiKhoan.maTaiKhoan",
            "tacGia.maTG",
            "theLoai.maTheLoai",
            "quanLy.maQl",
            "sach.maSach",
            "sinhVien.maSinhVien"));

    private MaTuDong() {}

    public static String taoMa(Connection conn, String bang, String cot, String tienTo, int doRong)
            throws SQLException {
        if (!HOP_LE.contains(bang + "." + cot)) {
            throw new IllegalArgumentException("Bang/cot khong duoc ho tro: " + bang + "." + cot);
        }
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(" + cot + ", ?) AS UNSIGNED)), 0) "
                + "FROM " + bang + " WHERE " + cot + " LIKE ?";
        int max = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tienTo.length() + 1);
            ps.setString(2, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    max = rs.getInt(1);
                }
            }
        }
        return tienTo + String.format("%0" + doRong + "d", max + 1);
    }
}