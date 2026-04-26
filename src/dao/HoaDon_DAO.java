package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Ban;
import entity.HoaDon;

public class HoaDon_DAO {

    public List<Object[]> getAllHoaDon() {
        List<Object[]> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT hd.maHD,
                       hd.thoiGianVao,
                       hd.thoiGianRa,
                       kh.tenKH AS tenKH,
                       nv.hoTen AS tenNV,
                       kh.sdt,
                       km.tenKhuyenMai AS tenKM,
                       hd.maBan,
                       hd.tongTien,
                       hd.tienKhachTra,
                       hd.phuongThucThanhToan,
                       hd.trangThai,
                       hd.lyDoHuy
                FROM HoaDon hd
                LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
                LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
                LEFT JOIN KhuyenMai km ON hd.maKM = km.maKM
                ORDER BY hd.thoiGianVao DESC
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ds.add(new Object[]{
                        rs.getString("maHD"),
                        rs.getTimestamp("thoiGianVao"),
                        rs.getTimestamp("thoiGianRa"),
                        rs.getString("tenKH"),
                        rs.getString("tenNV"),
                        rs.getString("sdt"),
                        rs.getString("tenKM") != null ? rs.getString("tenKM") : "",
                        rs.getString("maBan"),
                        rs.getBigDecimal("tongTien"),
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public HoaDon timHoaDonChuaThanhToanTheoBan(String maBan) {
        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT TOP 1 maHD, maPhieuDatBan, maKH, maKM, maBan, maNV,
                       thoiGianVao, thoiGianRa, tongTien, tienKhachTra, thueVAT, tienThua,
                       phuongThucThanhToan, trangThai, lyDoHuy
                FROM HoaDon
                WHERE maBan = ? AND trangThai = N'Chưa thanh toán'
                ORDER BY thoiGianVao DESC
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));

                Ban ban = new Ban();
                ban.setMaBan(rs.getString("maBan"));
                hd.setMaBan(ban);

                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setTienKhachTra(rs.getDouble("tienKhachTra"));
                hd.setThueVAT(rs.getDouble("thueVAT"));
                hd.setTienThua(rs.getDouble("tienThua"));
                hd.setPhuongThucThanhToan(rs.getString("phuongThucThanhToan"));
                hd.setTrangThai(rs.getString("trangThai"));
                hd.setLyDoHuy(rs.getString("lyDoHuy"));

                return hd;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object[] getHoaDonByMa(String maHD) {
        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT hd.maHD,
                       hd.thoiGianVao,
                       hd.thoiGianRa,
                       kh.tenKH,
                       nv.hoTen,
                       kh.sdt,
                       km.tenKhuyenMai,
                       hd.maBan,
                       hd.tongTien,
                       hd.tienKhachTra,
                       hd.phuongThucThanhToan,
                       hd.trangThai,
                       hd.lyDoHuy
                FROM HoaDon hd
                LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
                LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
                LEFT JOIN KhuyenMai km ON hd.maKM = km.maKM
                WHERE hd.maHD = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Object[]{
                        rs.getString("maHD"),
                        rs.getTimestamp("thoiGianVao"),
                        rs.getTimestamp("thoiGianRa"),
                        rs.getString("tenKH"),
                        rs.getString("hoTen"),
                        rs.getString("sdt"),
                        rs.getString("tenKhuyenMai"),
                        rs.getString("maBan"),
                        rs.getBigDecimal("tongTien"),
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy")
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Object[]> getHoaDonByTrangThai(String trangThai) {
        List<Object[]> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT hd.maHD,
                       hd.thoiGianVao,
                       hd.thoiGianRa,
                       kh.tenKH,
                       nv.hoTen,
                       kh.sdt,
                       km.tenKhuyenMai,
                       hd.maBan,
                       hd.tongTien,
                       hd.tienKhachTra,
                       hd.phuongThucThanhToan,
                       hd.trangThai,
                       hd.lyDoHuy
                FROM HoaDon hd
                LEFT JOIN KhachHang kh ON hd.maKH = kh.maKH
                LEFT JOIN NhanVien nv ON hd.maNV = nv.maNV
                LEFT JOIN KhuyenMai km ON hd.maKM = km.maKM
                WHERE hd.trangThai = ?
                ORDER BY hd.thoiGianVao DESC
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trangThai);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ds.add(new Object[]{
                        rs.getString("maHD"),
                        rs.getTimestamp("thoiGianVao"),
                        rs.getTimestamp("thoiGianRa"),
                        rs.getString("tenKH"),
                        rs.getString("hoTen"),
                        rs.getString("sdt"),
                        rs.getString("tenKhuyenMai"),
                        rs.getString("maBan"),
                        rs.getBigDecimal("tongTien"),
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public List<String> getAllTenNhanVien() {
        List<String> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT hoTen FROM NhanVien";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ds.add(rs.getString("hoTen"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public boolean chuyenBan(String maHD, String maBanMoi) {
        try {
            Connection con = ConnectDB.getConnection();

            String sql = "UPDATE HoaDon SET maBan = ? WHERE maHD = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBanMoi);
            ps.setString(2, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getAllTenKhuyenMai() {
        List<String> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT tenKhuyenMai
                FROM KhuyenMai
                WHERE trangThai = N'Đang áp dụng'
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ds.add(rs.getString("tenKhuyenMai"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public boolean updateHoaDon(
            String maHD,
            String tenNV,
            String tenKM,
            String trangThai,
            String lyDoHuy,
            Timestamp thoiGianRa,
            String phuongThucThanhToan
    ) {
        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                UPDATE HoaDon
                SET maNV = (SELECT maNV FROM NhanVien WHERE hoTen = ?),
                    maKM = (SELECT maKM FROM KhuyenMai WHERE tenKhuyenMai = ?),
                    trangThai = ?,
                    lyDoHuy = ?,
                    thoiGianRa = ?,
                    phuongThucThanhToan = ?
                WHERE maHD = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenNV);

            if (tenKM == null || tenKM.trim().isEmpty()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, tenKM);
            }

            ps.setString(3, trangThai);

            if (lyDoHuy == null || lyDoHuy.trim().isEmpty()) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setString(4, lyDoHuy);
            }

            ps.setTimestamp(5, thoiGianRa);

            if (phuongThucThanhToan == null || phuongThucThanhToan.trim().isEmpty()) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setString(6, phuongThucThanhToan);
            }

            ps.setString(7, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String taoMaHoaDonMoi() {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT NEXT VALUE FOR seq_HoaDon AS nextVal";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int so = rs.getInt("nextVal");
                return String.format("HD%05d", so);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean themHoaDonMoi(String maHD, String maBan, String maNV,
                                 String maPhieuDatBan, String maKH, String trangThai) {
        Connection con = ConnectDB.getConnection();

        String sql = """
            INSERT INTO HoaDon
            (maHD, thoiGianVao, thoiGianRa, maPhieuDatBan, maKH, maKM, maBan, maNV,
             tongTien, tienKhachTra, thueVAT, tienThua, phuongThucThanhToan, trangThai, lyDoHuy)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setNull(3, Types.TIMESTAMP);

            if (maPhieuDatBan == null || maPhieuDatBan.trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, maPhieuDatBan);
            }

            if (maKH == null || maKH.trim().isEmpty()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, maKH);
            }

            ps.setNull(6, Types.VARCHAR);
            ps.setString(7, maBan);

            if (maNV == null || maNV.trim().isEmpty()) {
                ps.setNull(8, Types.VARCHAR);
            } else {
                ps.setString(8, maNV);
            }

            ps.setDouble(9, 0);
            ps.setDouble(10, 0);
            ps.setDouble(11, 0);
            ps.setDouble(12, 0);
            ps.setNull(13, Types.NVARCHAR);
            ps.setString(14, trangThai);
            ps.setNull(15, Types.NVARCHAR);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean capNhatTongTien(String maHD) {
        String sql = """
            UPDATE HoaDon
            SET tongTien = ISNULL((
                SELECT SUM(soLuong * donGia)
                FROM ChiTietHoaDon
                WHERE maHD = ?
                  AND (trangThai IS NULL OR trangThai <> N'Đã hủy')
            ), 0)
            WHERE maHD = ?
        """;

        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}