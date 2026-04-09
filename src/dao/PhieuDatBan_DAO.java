package dao;

import connectDB.ConnectDB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PhieuDatBan_DAO {

    /**
     * Thêm mới phiếu đặt bàn.
     * maPhieuDatBan được SQL Server tự sinh bằng DEFAULT.
     * Hàm trả về mã phiếu vừa tạo, ví dụ: PDB00006
     */
    public String themPhieuDatBan(
            String maBan,
            String tenKhach,
            String sdt,
            int soLuongNguoi,
            Timestamp thoiGianDen,
            BigDecimal tienCoc,
            String ghiChu,
            String trangThai
    ) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            String sql = "INSERT INTO PhieuDatBan "
                    + "(maBan, tenKhach, sdt, soLuongNguoi, thoiGianDen, tienCoc, ghiChu, trangThai) "
                    + "OUTPUT INSERTED.maPhieuDatBan "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            stmt.setString(2, tenKhach);
            stmt.setString(3, sdt);
            stmt.setInt(4, soLuongNguoi);
            stmt.setTimestamp(5, thoiGianDen);
            stmt.setBigDecimal(6, tienCoc);
            stmt.setString(7, (ghiChu == null || ghiChu.trim().isEmpty()) ? null : ghiChu.trim());
            stmt.setString(8, trangThai);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        return null;
    }

    /**
     * Kiểm tra bàn có bị trùng lịch trong vòng soPhutMacDinh phút hay không.
     * Ví dụ 120 phút = 2 tiếng.
     */
    public boolean kiemTraTrungLich(String maBan, Timestamp thoiGianDen, int soPhutMacDinh) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            String sql = "SELECT COUNT(*) "
                    + "FROM PhieuDatBan "
                    + "WHERE maBan = ? "
                    + "AND trangThai IN (N'Đang chờ', N'Đã đặt') "
                    + "AND ABS(DATEDIFF(MINUTE, thoiGianDen, ?)) < ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            stmt.setTimestamp(2, thoiGianDen);
            stmt.setInt(3, soPhutMacDinh);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        return true;
    }

    /**
     * Lấy tất cả phiếu đặt bàn theo ngày.
     */
    public ArrayList<String[]> getPhieuDatBanTheoNgay(java.sql.Date ngay) {
        ArrayList<String[]> ds = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            String sql = "SELECT maPhieuDatBan, maBan, tenKhach, sdt, soLuongNguoi, "
                    + "thoiGianDen, tienCoc, ghiChu, trangThai "
                    + "FROM PhieuDatBan "
                    + "WHERE CAST(thoiGianDen AS DATE) = ? "
                    + "ORDER BY thoiGianDen, maBan";

            stmt = con.prepareStatement(sql);
            stmt.setDate(1, ngay);

            rs = stmt.executeQuery();
            while (rs.next()) {
                String[] row = new String[9];
                row[0] = rs.getString("maPhieuDatBan");
                row[1] = rs.getString("maBan");
                row[2] = rs.getString("tenKhach");
                row[3] = rs.getString("sdt");
                row[4] = String.valueOf(rs.getInt("soLuongNguoi"));
                row[5] = String.valueOf(rs.getTimestamp("thoiGianDen"));
                row[6] = rs.getBigDecimal("tienCoc") == null ? "0" : rs.getBigDecimal("tienCoc").toPlainString();
                row[7] = rs.getString("ghiChu");
                row[8] = rs.getString("trangThai");
                ds.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }

        return ds;
    }

    /**
     * Tìm theo mã phiếu.
     */
    public String[] timTheoMaPhieu(String maPhieuDatBan) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            String sql = "SELECT maPhieuDatBan, maBan, tenKhach, sdt, soLuongNguoi, "
                    + "thoiGianDen, tienCoc, ghiChu, trangThai "
                    + "FROM PhieuDatBan "
                    + "WHERE maPhieuDatBan = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuDatBan);

            rs = stmt.executeQuery();
            if (rs.next()) {
                String[] row = new String[9];
                row[0] = rs.getString("maPhieuDatBan");
                row[1] = rs.getString("maBan");
                row[2] = rs.getString("tenKhach");
                row[3] = rs.getString("sdt");
                row[4] = String.valueOf(rs.getInt("soLuongNguoi"));
                row[5] = String.valueOf(rs.getTimestamp("thoiGianDen"));
                row[6] = rs.getBigDecimal("tienCoc") == null ? "0" : rs.getBigDecimal("tienCoc").toPlainString();
                row[7] = rs.getString("ghiChu");
                row[8] = rs.getString("trangThai");
                return row;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }

        return null;
    }

    /**
     * Cập nhật trạng thái phiếu đặt bàn.
     * Ví dụ: Đã đặt / Đang chờ / Đã hủy / Hoàn thành
     */
    public boolean capNhatTrangThai(String maPhieuDatBan, String trangThaiMoi) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectDB.getConnection();

            String sql = "UPDATE PhieuDatBan SET trangThai = ? WHERE maPhieuDatBan = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maPhieuDatBan);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt);
        }

        return false;
    }

    /**
     * Hủy phiếu đặt bàn.
     */
    public boolean huyPhieuDatBan(String maPhieuDatBan) {
        return capNhatTrangThai(maPhieuDatBan, "Đã hủy");
    }

    private void closeResources(ResultSet rs, PreparedStatement stmt) {
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