package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import connectDB.ConnectDB;
import entity.CaLamViec;
import entity.TaiKhoan;

public class CaLamViec_DAO {

    public CaLamViec_DAO() {
    }
    public java.util.ArrayList<Object[]> getAllCaLamViecQuanLy() {
        java.util.ArrayList<Object[]> ds = new java.util.ArrayList<>();

        String sql = """
            SELECT 
                ca.maCa,
                ca.tenCa,
                ca.thoiGianMoCa,
                ca.thoiGianDongCa,
                tk.tenDangNhap,
                nv.hoTen,
                ca.tienMoCa,
                CASE WHEN ca.thoiGianDongCa IS NULL THEN
    ca.tienMoCa
    + ISNULL((
        SELECT SUM(hd.tongTien)
        FROM HoaDon hd
        WHERE hd.trangThai = N'Đã thanh toán'
          AND hd.phuongThucThanhToan = N'Tiền mặt'
          AND hd.thoiGianRa >= ca.thoiGianMoCa
          AND hd.thoiGianRa <= GETDATE()
    ), 0)
    + ISNULL((
        SELECT SUM(pdb.tienCoc)
        FROM PhieuDatBan pdb
        WHERE pdb.phuongThucThanhToanCoc = N'Tiền mặt'
          AND pdb.thoiGianDatPhieu >= ca.thoiGianMoCa
          AND pdb.thoiGianDatPhieu <= GETDATE()
          AND pdb.trangThai <> N'Đã hủy'
    ), 0)
ELSE ca.tienMatCuoiCa END AS tienMatCuoiCa,

CASE WHEN ca.thoiGianDongCa IS NULL THEN
    ISNULL((
        SELECT SUM(hd.tongTien)
        FROM HoaDon hd
        WHERE hd.trangThai = N'Đã thanh toán'
          AND hd.phuongThucThanhToan = N'Chuyển khoản'
          AND hd.thoiGianRa >= ca.thoiGianMoCa
          AND hd.thoiGianRa <= GETDATE()
    ), 0)
    + ISNULL((
        SELECT SUM(pdb.tienCoc)
        FROM PhieuDatBan pdb
        WHERE pdb.phuongThucThanhToanCoc = N'Chuyển khoản'
          AND pdb.thoiGianDatPhieu >= ca.thoiGianMoCa
          AND pdb.thoiGianDatPhieu <= GETDATE()
          AND pdb.trangThai <> N'Đã hủy'
    ), 0)
ELSE ca.tienChuyenKhoanCuoiCa END AS tienChuyenKhoanCuoiCa,

CASE WHEN ca.thoiGianDongCa IS NULL THEN
    ISNULL((
        SELECT SUM(hd.tongTien)
        FROM HoaDon hd
        WHERE hd.trangThai = N'Đã thanh toán'
          AND UPPER(hd.phuongThucThanhToan) = N'VISA'
          AND hd.thoiGianRa >= ca.thoiGianMoCa
          AND hd.thoiGianRa <= GETDATE()
    ), 0)
    + ISNULL((
        SELECT SUM(pdb.tienCoc)
        FROM PhieuDatBan pdb
        WHERE UPPER(pdb.phuongThucThanhToanCoc) = N'VISA'
          AND pdb.thoiGianDatPhieu >= ca.thoiGianMoCa
          AND pdb.thoiGianDatPhieu <= GETDATE()
          AND pdb.trangThai <> N'Đã hủy'
    ), 0)
ELSE ca.tienVisaCuoiCa END AS tienVisaCuoiCa,

CASE WHEN ca.thoiGianDongCa IS NULL THEN
    ISNULL((
        SELECT SUM(hd.tongTien)
        FROM HoaDon hd
        WHERE hd.trangThai = N'Đã thanh toán'
          AND hd.thoiGianRa >= ca.thoiGianMoCa
          AND hd.thoiGianRa <= GETDATE()
    ), 0)
    + ISNULL((
        SELECT SUM(pdb.tienCoc)
        FROM PhieuDatBan pdb
        WHERE pdb.thoiGianDatPhieu >= ca.thoiGianMoCa
          AND pdb.thoiGianDatPhieu <= GETDATE()
          AND pdb.trangThai <> N'Đã hủy'
    ), 0)
ELSE ca.tongDoanhThu END AS tongDoanhThu,
                CASE WHEN ca.thoiGianDongCa IS NULL THEN N'Đang mở' ELSE N'Đã đóng' END AS trangThai
            FROM CaLamViec ca
            JOIN TaiKhoan tk ON ca.maTaiKhoan = tk.maTaiKhoan
            JOIN NhanVien nv ON tk.maNV = nv.maNV
            ORDER BY ca.thoiGianMoCa DESC
        """;

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ds.add(new Object[]{
                        rs.getString("maCa"),
                        rs.getString("tenCa"),
                        rs.getTimestamp("thoiGianMoCa"),
                        rs.getTimestamp("thoiGianDongCa"),
                        rs.getString("tenDangNhap"),
                        rs.getString("hoTen"),
                        rs.getDouble("tienMoCa"),
                        rs.getDouble("tienMatCuoiCa"),
                        rs.getDouble("tienChuyenKhoanCuoiCa"),
                        rs.getDouble("tienVisaCuoiCa"),
                        rs.getDouble("tongDoanhThu"),
                        rs.getString("trangThai")
                });
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public boolean moCaVoiTenCa(String tenCa, double tienMoCa, TaiKhoan taiKhoan) {
        if (taiKhoan == null || tenCa == null || tenCa.trim().isEmpty()) {
            return false;
        }

        if (layCaDangMo() != null) {
            return false;
        }

        String sql = """
            INSERT INTO CaLamViec
            (tenCa, thoiGianMoCa, thoiGianDongCa, tienMoCa,
             tienMatCuoiCa, tienChuyenKhoanCuoiCa, tienVisaCuoiCa, tongDoanhThu, maTaiKhoan)
            VALUES (?, ?, NULL, ?, 0, 0, 0, 0, ?)
        """;

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, tenCa.trim());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setDouble(3, tienMoCa);
            ps.setString(4, taiKhoan.getMaTaiKhoan());

            boolean ok = ps.executeUpdate() > 0;
            ps.close();
            return ok;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public int demSoCaTrongNgay(java.time.LocalDate ngay) {
        String sql = """
            SELECT COUNT(*)
            FROM CaLamViec
            WHERE CAST(thoiGianMoCa AS DATE) = ?
        """;

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(ngay));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count;
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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
        LocalDate today = LocalDate.now();

        CaLamViec caDangMo = layCaDangMo();
        if (caDangMo != null) {
            return null;
        }

        boolean coCaSang = tonTaiCaTheoTenTrongNgay(today, "Ca sáng");
        boolean coCaChieu = tonTaiCaTheoTenTrongNgay(today, "Ca chiều");

        if (!coCaSang) {
            return "Ca sáng";
        }

        if (!coCaChieu) {
            return "Ca chiều";
        }

        int soCaTrongNgay = demSoCaTrongNgay(today);
        return "Ca phụ " + (soCaTrongNgay + 1);
    }

    public String layTenCaHienThi() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Kiểm tra có ca đang mở không
        CaLamViec caDangMo = layCaDangMo();
        if (caDangMo != null) {
            return caDangMo.getTenCa() + " (đang mở)";
        }

        // Kiểm tra ca sáng / chiều trong ngày
        boolean coCaSang = tonTaiCaTheoTenTrongNgay(today, "Ca sáng");
        boolean coCaChieu = tonTaiCaTheoTenTrongNgay(today, "Ca chiều");

        // Giờ chuẩn cho ca sáng và chiều
        LocalTime gioBatDauCaSang = LocalTime.of(9, 0);
        LocalTime gioKetThucCaSang = LocalTime.of(17, 0);

        LocalTime gioBatDauCaChieu = LocalTime.of(17, 0);
        LocalTime gioKetThucCaChieu = LocalTime.of(23, 0);

        // Nếu ca sáng chưa mở và giờ hiện tại chưa quá 17h -> mở ca sáng
        if (!coCaSang && now.isBefore(gioKetThucCaSang)) {
            return "Ca sáng";
        }

        // Nếu ca chiều chưa mở và giờ hiện tại trong 17h-23h -> mở ca chiều
        if (!coCaChieu && now.isAfter(gioBatDauCaChieu) && now.isBefore(gioKetThucCaChieu)) {
            return "Ca chiều";
        }

        // Nếu đã qua giờ chiều hoặc cả 2 ca sáng chiều đã tồn tại, tạo ca phụ
        int soCaTrongNgay = demSoCaTrongNgay(today);
        return "Ca phụ " + (soCaTrongNgay + 1);
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
    public double tinhTienCocTheoPhuongThuc(String phuongThuc, LocalDateTime thoiGianMoCa) {
        String sql = """
            SELECT ISNULL(SUM(tienCoc), 0)
            FROM PhieuDatBan
            WHERE LTRIM(RTRIM(phuongThucThanhToanCoc)) = ?
              AND thoiGianDatPhieu IS NOT NULL
              AND thoiGianDatPhieu >= ?
              AND thoiGianDatPhieu <= GETDATE()
              AND trangThai <> N'Đã hủy'
        """;

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, phuongThuc.trim());
            ps.setTimestamp(2, Timestamp.valueOf(thoiGianMoCa));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double tien = rs.getDouble(1);
                rs.close();
                ps.close();
                return tien;
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    public double layTienMatHienTai(String maCa) {
        CaLamViec ca = layCaTheoMa(maCa);
        if (ca == null) return 0; // tránh null
        if (ca.getThoiGianDongCa() == null) {
            double tienMat = ca.getTienMoCa();
            tienMat += tinhTienTheoPhuongThuc("Tiền mặt", ca.getThoiGianMoCa());
            tienMat += tinhTienCocTheoPhuongThuc("Tiền mặt", ca.getThoiGianMoCa());
            return tienMat;
        }
        return ca.getTienMatCuoiCa();
    }

    public double layTongDoanhThuHienTai(String maCa) {
        CaLamViec ca = layCaTheoMa(maCa);
        if (ca == null) return 0; // tránh null
        if (ca.getThoiGianDongCa() == null) {
            return tinhTongHoaDonVaCoc(ca.getThoiGianMoCa());
        }
        return ca.getTongDoanhThu();
    } 
    public double tinhTongHoaDonVaCoc(LocalDateTime thoiGianMoCa) {
        double tong = 0;
        tong += tinhTienTheoPhuongThuc("Tiền mặt", thoiGianMoCa);
        tong += tinhTienTheoPhuongThuc("Chuyển khoản", thoiGianMoCa);
        tong += tinhTienTheoPhuongThuc("VISA", thoiGianMoCa);
        tong += tinhTienCocTheoPhuongThuc("Tiền mặt", thoiGianMoCa);
        tong += tinhTienCocTheoPhuongThuc("Chuyển khoản", thoiGianMoCa);
        tong += tinhTienCocTheoPhuongThuc("VISA", thoiGianMoCa);
        return tong;
    }
    public double layChuyenKhoanHienTai(String maCa) {
        CaLamViec ca = layCaTheoMa(maCa);
        if (ca == null) return 0;
        if (ca.getThoiGianDongCa() == null) {
            double ck = tinhTienTheoPhuongThuc("Chuyển khoản", ca.getThoiGianMoCa());
            ck += tinhTienCocTheoPhuongThuc("Chuyển khoản", ca.getThoiGianMoCa());
            return ck;
        }
        return ca.getTienChuyenKhoanCuoiCa();
    }

    public double layVisaHienTai(String maCa) {
        CaLamViec ca = layCaTheoMa(maCa);
        if (ca == null) return 0;
        if (ca.getThoiGianDongCa() == null) {
            double visa = tinhTienTheoPhuongThuc("VISA", ca.getThoiGianMoCa());
            visa += tinhTienCocTheoPhuongThuc("VISA", ca.getThoiGianMoCa());
            return visa;
        }
        return ca.getTienVisaCuoiCa();
    }
    
    public CaLamViec layCaTheoMa(String maCa) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        CaLamViec ca = null;

        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT * FROM CaLamViec WHERE maCa = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maCa);
            rs = stmt.executeQuery();

            if (rs.next()) {
                ca = mapCaLamViec(rs); // map tất cả cột ra entity
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(rs, stmt);
        }

        return ca; // trả về null nếu không tìm thấy
    }
}