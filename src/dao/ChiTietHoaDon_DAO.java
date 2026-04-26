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
                AND (cthd.trangThai IS NULL OR cthd.trangThai <> N'Đã hủy')
                AND cthd.soLuong > 0
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
    public boolean tachMonSangHoaDonKhac(String maHDCu, String maHDMoi, String maMon, int soLuongTach) {
        Connection con = ConnectDB.getConnection();

        try {
            con.setAutoCommit(false);

            String sqlGet = """
                SELECT soLuong, donGia, ghiChu, trangThai
                FROM ChiTietHoaDon
                WHERE maHD = ? AND maMon = ?
                  AND (trangThai IS NULL OR trangThai <> N'Đã hủy')
            """;

            PreparedStatement psGet = con.prepareStatement(sqlGet);
            psGet.setString(1, maHDCu);
            psGet.setString(2, maMon);
            ResultSet rs = psGet.executeQuery();

            if (!rs.next()) {
                con.rollback();
                return false;
            }

            int soLuongCu = rs.getInt("soLuong");
            double donGia = rs.getDouble("donGia");
            String ghiChu = rs.getString("ghiChu");

            if (soLuongTach <= 0 || soLuongTach > soLuongCu) {
                con.rollback();
                return false;
            }

            if (soLuongTach == soLuongCu) {
                String sqlDeleteCu = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maMon = ?";
                PreparedStatement psDeleteCu = con.prepareStatement(sqlDeleteCu);
                psDeleteCu.setString(1, maHDCu);
                psDeleteCu.setString(2, maMon);
                psDeleteCu.executeUpdate();
            } else {
                String sqlUpdateCu = """
                    UPDATE ChiTietHoaDon
                    SET soLuong = soLuong - ?
                    WHERE maHD = ? AND maMon = ?
                """;
                PreparedStatement psUpdateCu = con.prepareStatement(sqlUpdateCu);
                psUpdateCu.setInt(1, soLuongTach);
                psUpdateCu.setString(2, maHDCu);
                psUpdateCu.setString(3, maMon);
                psUpdateCu.executeUpdate();
            }

            String sqlCheckMoi = "SELECT soLuong FROM ChiTietHoaDon WHERE maHD = ? AND maMon = ?";
            PreparedStatement psCheckMoi = con.prepareStatement(sqlCheckMoi);
            psCheckMoi.setString(1, maHDMoi);
            psCheckMoi.setString(2, maMon);
            ResultSet rsMoi = psCheckMoi.executeQuery();

            if (rsMoi.next()) {
                String sqlCongMoi = """
                    UPDATE ChiTietHoaDon
                    SET soLuong = soLuong + ?,
                        trangThai = N'Đang phục vụ'
                    WHERE maHD = ? AND maMon = ?
                """;
                PreparedStatement psCongMoi = con.prepareStatement(sqlCongMoi);
                psCongMoi.setInt(1, soLuongTach);
                psCongMoi.setString(2, maHDMoi);
                psCongMoi.setString(3, maMon);
                psCongMoi.executeUpdate();
            } else {
                String sqlInsertMoi = """
                    INSERT INTO ChiTietHoaDon
                    (maHD, maMon, soLuong, donGia, ghiChu, trangThai, lyDoHuy, soLuongHuy, thoiGianHuy)
                    VALUES (?, ?, ?, ?, ?, N'Đang phục vụ', NULL, 0, NULL)
                """;
                PreparedStatement psInsertMoi = con.prepareStatement(sqlInsertMoi);
                psInsertMoi.setString(1, maHDMoi);
                psInsertMoi.setString(2, maMon);
                psInsertMoi.setInt(3, soLuongTach);
                psInsertMoi.setDouble(4, donGia);
                psInsertMoi.setString(5, ghiChu);
                psInsertMoi.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}