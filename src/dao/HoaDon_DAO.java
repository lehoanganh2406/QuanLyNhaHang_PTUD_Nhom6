package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class HoaDon_DAO {

    // ================== LOAD TẤT CẢ HÓA ĐƠN ==================
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
                       hd.tienKhachTra,
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
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // ================== LOAD THEO MÃ HÓA ĐƠN ==================
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
                       hd.tienKhachTra,
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
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy")
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================== LỌC THEO TRẠNG THÁI ==================
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
                       hd.tienKhachTra,
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
                        rs.getBigDecimal("tienKhachTra"),
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
            Timestamp thoiGianRa
    ) {
        try {
            Connection con = ConnectDB.getConnection();

            String sql = """
                UPDATE HoaDon
                SET maNV = (SELECT maNV FROM NhanVien WHERE hoTen = ?),
                    maKM = (SELECT maKM FROM KhuyenMai WHERE tenKhuyenMai = ?),
                    trangThai = ?,
                    lyDoHuy = ?,
                    thoiGianRa = ?
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
            ps.setString(6, maHD);

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

    public boolean themHoaDonMoi(String maHD, String maBan, String maNV, String maPhieuDatBan, String maKH, String trangThai) {
        Connection con = ConnectDB.getConnection();

        String sql = """
            INSERT INTO HoaDon
            (maHD, thoiGianVao, thoiGianRa, maPhieuDatBan, maKH, maKM, maBan, maNV, tienKhachTra, thueVAT, tienThua, trangThai)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setNull(3, java.sql.Types.TIMESTAMP);

            if (maPhieuDatBan == null || maPhieuDatBan.trim().isEmpty()) ps.setNull(4, java.sql.Types.VARCHAR);
            else ps.setString(4, maPhieuDatBan);

            if (maKH == null || maKH.trim().isEmpty()) ps.setNull(5, java.sql.Types.VARCHAR);
            else ps.setString(5, maKH);

            ps.setNull(6, java.sql.Types.VARCHAR); // maKM
            ps.setString(7, maBan);

            if (maNV == null || maNV.trim().isEmpty()) ps.setNull(8, java.sql.Types.VARCHAR);
            else ps.setString(8, maNV);

            ps.setDouble(9, 0);
            ps.setDouble(10, 0);
            ps.setDouble(11, 0);
            ps.setString(12, trangThai);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}