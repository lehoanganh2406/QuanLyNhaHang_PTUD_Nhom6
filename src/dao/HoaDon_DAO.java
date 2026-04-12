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
                       hd.trangThai
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
                        rs.getString("tenKM"),
                        rs.getString("maBan"),
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("trangThai")
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
                       hd.trangThai
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
                        rs.getString("trangThai")
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
                       hd.trangThai
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
                        rs.getString("trangThai")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    
}