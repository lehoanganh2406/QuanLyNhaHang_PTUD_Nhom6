package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.MonAn;

public class ChiTietHoaDon_DAO {

    // ================== LẤY DANH SÁCH CHI TIẾT THEO MÃ HÓA ĐƠN ==================
    public List<ChiTietHoaDon> getChiTietTheoMaHD(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT cthd.maHD,
                       cthd.maMon,
                       cthd.soLuong,
                       cthd.donGia,
                       cthd.ghiChu,
                       cthd.trangThai,
                       cthd.lyDoHuy,
                       cthd.soLuongHuy,
                       cthd.thoiGianHuy
                FROM ChiTietHoaDon cthd
                WHERE cthd.maHD = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon(rs.getString("maHD"));
                MonAn mon = new MonAn(rs.getString("maMon"));

                Timestamp tsHuy = rs.getTimestamp("thoiGianHuy");
                LocalDateTime thoiGianHuy = tsHuy != null ? tsHuy.toLocalDateTime() : null;

                ChiTietHoaDon ct = new ChiTietHoaDon(
                        hd,
                        mon,
                        rs.getInt("soLuong"),
                        rs.getDouble("donGia"),
                        rs.getString("ghiChu"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy"),
                        rs.getInt("soLuongHuy"),
                        thoiGianHuy
                );

                ds.add(ct);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // ================== THÊM CHI TIẾT HÓA ĐƠN ==================
    public boolean themChiTietHoaDon(ChiTietHoaDon ct) {
        int n = 0;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO ChiTietHoaDon
                (maHD, maMon, soLuong, donGia, ghiChu, trangThai, lyDoHuy, soLuongHuy, thoiGianHuy)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ct.getMaHD().getMaHD());
            ps.setString(2, ct.getMaMon().getMaMon());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
            ps.setString(5, ct.getGhiChu());
            ps.setString(6, ct.getTrangThai());

            if (ct.getLyDoHuy() == null || ct.getLyDoHuy().trim().isEmpty()) {
                ps.setNull(7, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(7, ct.getLyDoHuy());
            }

            ps.setInt(8, ct.getSoLuongHuy());

            if (ct.getThoiGianHuy() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(ct.getThoiGianHuy()));
            } else {
                ps.setNull(9, java.sql.Types.TIMESTAMP);
            }

            n = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    // ================== CẬP NHẬT CHI TIẾT HÓA ĐƠN ==================
    public boolean capNhatChiTietHoaDon(ChiTietHoaDon ct) {
        int n = 0;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                UPDATE ChiTietHoaDon
                SET soLuong = ?,
                    donGia = ?,
                    ghiChu = ?,
                    trangThai = ?,
                    lyDoHuy = ?,
                    soLuongHuy = ?,
                    thoiGianHuy = ?
                WHERE maHD = ? AND maMon = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getDonGia());
            ps.setString(3, ct.getGhiChu());
            ps.setString(4, ct.getTrangThai());

            if (ct.getLyDoHuy() == null || ct.getLyDoHuy().trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(5, ct.getLyDoHuy());
            }

            ps.setInt(6, ct.getSoLuongHuy());

            if (ct.getThoiGianHuy() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(ct.getThoiGianHuy()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }

            ps.setString(8, ct.getMaHD().getMaHD());
            ps.setString(9, ct.getMaMon().getMaMon());

            n = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    // ================== HỦY MÓN ==================
    public boolean huyMon(String maHD, String maMon, String lyDoHuy, int soLuongHuy) {
        int n = 0;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                UPDATE ChiTietHoaDon
                SET trangThai = ?,
                    lyDoHuy = ?,
                    soLuongHuy = ?,
                    thoiGianHuy = ?
                WHERE maHD = ? AND maMon = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "Đã hủy");
            ps.setString(2, lyDoHuy);
            ps.setInt(3, soLuongHuy);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, maHD);
            ps.setString(6, maMon);

            n = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    // ================== XÓA CHI TIẾT HÓA ĐƠN ==================
    public boolean xoaChiTietHoaDon(String maHD, String maMon) {
        int n = 0;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMon = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            ps.setString(2, maMon);

            n = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    // ================== LẤY 1 CHI TIẾT HÓA ĐƠN ==================
    public ChiTietHoaDon getChiTietHoaDon(String maHD, String maMon) {
        ChiTietHoaDon ct = null;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT maHD, maMon, soLuong, donGia, ghiChu,
                       trangThai, lyDoHuy, soLuongHuy, thoiGianHuy
                FROM ChiTietHoaDon
                WHERE maHD = ? AND maMon = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            ps.setString(2, maMon);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HoaDon hd = new HoaDon(rs.getString("maHD"));
                MonAn mon = new MonAn(rs.getString("maMon"));

                Timestamp tsHuy = rs.getTimestamp("thoiGianHuy");
                LocalDateTime thoiGianHuy = tsHuy != null ? tsHuy.toLocalDateTime() : null;

                ct = new ChiTietHoaDon(
                        hd,
                        mon,
                        rs.getInt("soLuong"),
                        rs.getDouble("donGia"),
                        rs.getString("ghiChu"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy"),
                        rs.getInt("soLuongHuy"),
                        thoiGianHuy
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ct;
    }
}