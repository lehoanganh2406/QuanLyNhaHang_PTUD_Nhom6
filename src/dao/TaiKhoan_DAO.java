package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
}