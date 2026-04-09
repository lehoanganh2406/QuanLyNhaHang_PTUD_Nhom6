package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import connectDB.ConnectDB;
import entity.CaLamViec;
import entity.TaiKhoan;

public class CaLamViec_DAO {

    public CaLamViec_DAO() {
    }

    public CaLamViec layCaDangMo() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getInstance().getConnection();

            String sql = "SELECT TOP 1 * " +
                         "FROM CaLamViec " +
                         "WHERE thoiGianDongCa IS NULL " +
                         "ORDER BY thoiGianMoCa DESC";

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapCaLamViec(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs, stmt);
        }
        return null;
    }

    public boolean daCoCaSangTrongNgay(LocalDate ngay) {
        return tonTaiCaTheoTenTrongNgay(ngay, "Ca sáng");
    }

    public boolean daCoCaChieuTrongNgay(LocalDate ngay) {
        return tonTaiCaTheoTenTrongNgay(ngay, "Ca chiều");
    }

    private boolean tonTaiCaTheoTenTrongNgay(LocalDate ngay, String tenCa) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getInstance().getConnection();

            String sql = "SELECT COUNT(*) " +
                         "FROM CaLamViec " +
                         "WHERE CAST(thoiGianMoCa AS DATE) = ? " +
                         "AND tenCa = ?";

            stmt = con.prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(ngay));
            stmt.setString(2, tenCa);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs, stmt);
        }

        return false;
    }

    public String xacDinhTenCaMoi() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        CaLamViec caDangMo = layCaDangMo();
        if (caDangMo != null) {
            return null;
        }

        boolean coCaSang = daCoCaSangTrongNgay(today);
        boolean coCaChieu = daCoCaChieuTrongNgay(today);

        if (!coCaSang && !coCaChieu) {
            if (now.getHour() < 17) {
                return "Ca sáng";
            } else {
                return "Ca chiều";
            }
        }

        if (coCaSang && !coCaChieu) {
            return "Ca chiều";
        }

        return null;
    }

    public String layTenCaHienThi() {
        CaLamViec caDangMo = layCaDangMo();
        if (caDangMo != null) {
            return caDangMo.getTenCa() + " (đang mở)";
        }

        String tenCa = xacDinhTenCaMoi();
        if (tenCa != null) {
            return tenCa;
        }

        return "Hôm nay đã đủ ca";
    }

    public boolean moCa(double tienMoCa, TaiKhoan taiKhoan) {
        if (taiKhoan == null) {
            return false;
        }

        String tenCa = xacDinhTenCaMoi();
        if (tenCa == null) {
            return false;
        }

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectDB.getInstance().getConnection();

            String sql = "INSERT INTO CaLamViec " +
                         "(tenCa, thoiGianMoCa, thoiGianDongCa, tienMoCa, " +
                         "tienMatCuoiCa, tienChuyenKhoanCuoiCa, tienVisaCuoiCa, maTaiKhoan) " +
                         "VALUES (?, ?, NULL, ?, 0, 0, 0, ?)";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, tenCa);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(3, tienMoCa);
            stmt.setString(4, taiKhoan.getMaTaiKhoan());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(null, stmt);
        }

        return false;
    }

    private CaLamViec mapCaLamViec(ResultSet rs) throws SQLException {
        CaLamViec ca = new CaLamViec();

        ca.setMaCa(rs.getString("maCa"));
        ca.setTenCa(rs.getString("tenCa"));

        Timestamp tgMo = rs.getTimestamp("thoiGianMoCa");
        if (tgMo != null) {
            ca.setThoiGianMoCa(tgMo.toLocalDateTime());
        }

        Timestamp tgDong = rs.getTimestamp("thoiGianDongCa");
        if (tgDong != null) {
            ca.setThoiGianDongCa(tgDong.toLocalDateTime());
        }

        ca.setTienMoCa(rs.getDouble("tienMoCa"));
        ca.setTienMatCuoiCa(rs.getDouble("tienMatCuoiCa"));
        ca.setTienChuyenKhoanCuoiCa(rs.getDouble("tienChuyenKhoanCuoiCa"));
        ca.setTienVisaCuoiCa(rs.getDouble("tienVisaCuoiCa"));

        return ca;
    }

    private void close(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (stmt != null) stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}