package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

    

    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        ArrayList<TaiKhoan> dsTK = new ArrayList<TaiKhoan>();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

//        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
//                     "tk.maNV, nv.hoTen, nv.chucVu " +
//                     "FROM TaiKhoan tk " +
//                     "JOIN NhanVien nv ON tk.maNV = nv.maNV";
        
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
                "tk.maNV, nv.hoTen, nv.chucVu, nv.anhNhanVien " +
                "FROM TaiKhoan tk " +
                "JOIN NhanVien nv ON tk.maNV = nv.maNV";

        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String maTaiKhoan = rs.getString("maTaiKhoan");
                String tenDangNhap = rs.getString("tenDangNhap");
                String matKhau = rs.getString("matKhau");
                String phanQuyen = rs.getString("phanQuyen");
                boolean trangThai = rs.getBoolean("trangThai");
                String maNhanVien = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                String chucVu = rs.getString("chucVu");
                String anh= rs.getString("anhNhanVien");

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNhanVien);
                nv.setHoTen(hoTen);
                nv.setChucVu(chucVu);
                nv.setAnhNhanVien(anh);

                TaiKhoan tk = new TaiKhoan(maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, nv);
                dsTK.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsTK;
    }
    public TaiKhoan getTaiKhoanTheoMaNV(String maNVCanTim) {
        try {
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
                         "tk.maNV, nv.hoTen, nv.chucVu " +
                         "FROM TaiKhoan tk " +
                         "JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                         "WHERE tk.maNV = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maNVCanTim);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setChucVu(rs.getString("chucVu"));

                return new TaiKhoan(
                        rs.getString("maTaiKhoan"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau"),
                        rs.getString("phanQuyen"),
                        rs.getBoolean("trangThai"),
                        nv
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean doiMatKhau(String maNV, String mkCu, String mkMoi) {
        try {
            Connection con = ConnectDB.getConnection();

            // 1. kiểm tra mật khẩu cũ
            String sqlCheck = "SELECT * FROM TaiKhoan WHERE maNV = ? AND matKhau = ?";
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setString(1, maNV);
            psCheck.setString(2, mkCu);

            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                return false; // sai mật khẩu cũ
            }

            // 2. update mật khẩu mới
            String sqlUpdate = "UPDATE TaiKhoan SET matKhau = ? WHERE maNV = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setString(1, mkMoi);
            psUpdate.setString(2, maNV);

            return psUpdate.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public TaiKhoan getTaiKhoanTheoMa(String maTKCanTim) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
                     "tk.maNV, nv.hoTen, nv.chucVu " +
                     "FROM TaiKhoan tk " +
                     "JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                     "WHERE tk.maTaiKhoan = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maTKCanTim);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maTaiKhoan = rs.getString("maTaiKhoan");
                String tenDangNhap = rs.getString("tenDangNhap");
                String matKhau = rs.getString("matKhau");
                String phanQuyen = rs.getString("phanQuyen");
                boolean trangThai = rs.getBoolean("trangThai");
                String maNhanVien = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                String chucVu = rs.getString("chucVu");

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNhanVien);
                nv.setHoTen(hoTen);
                nv.setChucVu(chucVu);

                return new TaiKhoan(maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TaiKhoan getTaiKhoanTheoTenDangNhap(String tenDNCanTim) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
                     "tk.maNV, nv.hoTen, nv.chucVu " +
                     "FROM TaiKhoan tk " +
                     "JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                     "WHERE tk.tenDangNhap = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tenDNCanTim);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maTaiKhoan = rs.getString("maTaiKhoan");
                String tenDangNhap = rs.getString("tenDangNhap");
                String matKhau = rs.getString("matKhau");
                String phanQuyen = rs.getString("phanQuyen");
                boolean trangThai = rs.getBoolean("trangThai");
                String maNhanVien = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                String chucVu = rs.getString("chucVu");

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNhanVien);
                nv.setHoTen(hoTen);
                nv.setChucVu(chucVu);

                return new TaiKhoan(maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
                     "tk.maNV, nv.hoTen, nv.chucVu " +
                     "FROM TaiKhoan tk " +
                     "JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                     "WHERE tk.tenDangNhap = ? AND tk.matKhau = ? AND tk.trangThai = 1";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maTaiKhoan = rs.getString("maTaiKhoan");
                String phanQuyen = rs.getString("phanQuyen");
                boolean trangThai = rs.getBoolean("trangThai");
                String maNhanVien = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                String chucVu = rs.getString("chucVu");

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNhanVien);
                nv.setHoTen(hoTen);
                nv.setChucVu(chucVu);

                return new TaiKhoan(maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean themTaiKhoan(TaiKhoan tk) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;

        String sql = "INSERT INTO TaiKhoan(maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, maNV) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tk.getMaTaiKhoan());
            stmt.setString(2, tk.getTenDangNhap());
            stmt.setString(3, tk.getMatKhau());
            stmt.setString(4, tk.getPhanQuyen());
            stmt.setBoolean(5, tk.isTrangThai());
            stmt.setString(6, tk.getMaNV().getMaNV());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;

        String sql = "UPDATE TaiKhoan SET tenDangNhap = ?, matKhau = ?, phanQuyen = ?, trangThai = ?, maNV = ? WHERE maTaiKhoan = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tk.getTenDangNhap());
            stmt.setString(2, tk.getMatKhau());
            stmt.setString(3, tk.getPhanQuyen());
            stmt.setBoolean(4, tk.isTrangThai());
            stmt.setString(5, tk.getMaNV().getMaNV());
            stmt.setString(6, tk.getMaTaiKhoan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean xoaTaiKhoan(String maTK) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;

        String sql = "DELETE FROM TaiKhoan WHERE maTaiKhoan = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maTK);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean doiMatKhau(String maTK, String matKhauMoi) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;

        String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE maTaiKhoan = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, matKhauMoi);
            stmt.setString(2, maTK);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean capNhatTrangThai(String maTK, boolean trangThai) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;

        String sql = "UPDATE TaiKhoan SET trangThai = ? WHERE maTaiKhoan = ?";

        try {
            stmt = con.prepareStatement(sql);
            stmt.setBoolean(1, trangThai);
            stmt.setString(2, maTK);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}