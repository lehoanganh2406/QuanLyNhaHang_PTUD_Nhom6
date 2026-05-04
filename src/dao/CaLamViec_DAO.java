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

        if (!coCaSang) {
            return "Ca sáng";
        }

        if (!coCaChieu) {
            return "Ca chiều";
        }

        return "Ca phụ 3";
    }

    public String layTenCaHienThi() {
        CaLamViec caDangMo = layCaDangMo();
        if (caDangMo != null) {
            return caDangMo.getTenCa() + " (đang mở)";
        }

        return xacDinhTenCaMoi();
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
                    "tienMatCuoiCa, tienChuyenKhoanCuoiCa, tienVisaCuoiCa, tongDoanhThu, maTaiKhoan) " +
                    "VALUES (?, ?, NULL, ?, 0, 0, 0, 0, ?)";

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
        ca.setTongDoanhThu(rs.getDouble("tongDoanhThu"));

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
    public boolean dongCa(String maCa, double tienMat, double tienChuyenKhoan, double tienVisa, double tongDoanhThu) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectDB.getInstance().getConnection();

            String sql = """
                UPDATE CaLamViec
                SET thoiGianDongCa = ?,
                    tienMatCuoiCa = ?,
                    tienChuyenKhoanCuoiCa = ?,
                    tienVisaCuoiCa = ?,
                    tongDoanhThu = ?
                WHERE maCa = ?
                  AND thoiGianDongCa IS NULL
            """;

            stmt = con.prepareStatement(sql);

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(2, tienMat);
            stmt.setDouble(3, tienChuyenKhoan);
            stmt.setDouble(4, tienVisa);
            stmt.setDouble(5, tongDoanhThu);
            stmt.setString(6, maCa);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(null, stmt);
        }

        return false;
    }
    public double tinhTienTheoPhuongThuc(String phuongThuc, LocalDateTime thoiGianMoCa) {
        String sql = """
            SELECT ISNULL(SUM(tongTien), 0)
            FROM HoaDon
            WHERE LTRIM(RTRIM(trangThai)) = N'Đã thanh toán'
              AND LTRIM(RTRIM(phuongThucThanhToan)) = ?
              AND thoiGianRa IS NOT NULL
              AND thoiGianRa >= ?
        """;

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, phuongThuc.trim());
            ps.setTimestamp(2, Timestamp.valueOf(thoiGianMoCa));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}