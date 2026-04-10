package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
=======
import java.sql.SQLException;
import java.util.ArrayList;
>>>>>>> 7410f637571f781b5592cefa4094490c4190adc4

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();

            // ⚠️ JOIN để lấy tên nhân viên
            String sql = """
                    SELECT tk.*, nv.hoTen AS tenNhanVien
                    FROM TaiKhoan tk
                    JOIN NhanVien nv ON tk.maNV = nv.maNV
                    """;
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String maTK = rs.getString("maTaiKhoan");
                String tenDN = rs.getString("tenDangNhap");
                String matKhau = rs.getString("matKhau");
                String phanQuyen = rs.getString("phanQuyen");
                boolean trangThai = rs.getBoolean("trangThai");

                // ⚠️ lấy mã NV + tên NV
                String maNV = rs.getString("maNV");
                String tenNV = rs.getString("tenNhanVien");

                // ⚠️ tạo object nhân viên
                NhanVien nv = new NhanVien(maNV, tenNV);

                // tạo tài khoản
                TaiKhoan tk = new TaiKhoan(
                        maTK,
                        tenDN,
                        matKhau,
                        phanQuyen,
                        trangThai,
                        nv
                );

                ds.add(tk);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    

        return ds;
        
    }
    public TaiKhoan_DAO() {
    }

    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        ArrayList<TaiKhoan> dsTK = new ArrayList<TaiKhoan>();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.phanQuyen, tk.trangThai, " +
                     "tk.maNV, nv.hoTen, nv.chucVu " +
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

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNhanVien);
                nv.setHoTen(hoTen);
                nv.setChucVu(chucVu);

                TaiKhoan tk = new TaiKhoan(maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, nv);
                dsTK.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsTK;
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