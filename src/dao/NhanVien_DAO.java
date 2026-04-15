package dao;

import java.sql.*;
import java.util.*;
import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {

    public List<NhanVien> getTenNhanVien() {
        List<NhanVien> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM NhanVien";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String maNV = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                NhanVien nv = new NhanVien(maNV,hoTen); // nếu entity bạn có constructor này
                ds.add(nv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
    
    public List<NhanVien> getAllNhanVien() {
		List<NhanVien> ds= new ArrayList<NhanVien>();
		try {
			Connection con= ConnectDB.getConnection();
			String sql = "SELECT * FROM NhanVien";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	String maNV = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                String anh = rs.getString("anhNhanVien");
//                Date ngaySinh = rs.getDate("ngaySinh");
                java.sql.Date sqlDate = rs.getDate("ngaySinh");
                java.util.Date ngaySinh = new java.util.Date(sqlDate.getTime());
                boolean gioiTinh = rs.getBoolean("gioiTinh");
                String cccd = rs.getString("cccd");
                String email = rs.getString("email");
                String sdt = rs.getString("sdt");
                String chucVu = rs.getString("chucVu");
                String trangThai = rs.getString("trangThai");
                String lyDo = rs.getString("lyDoNghi");

                NhanVien nv = new NhanVien(
                        maNV, hoTen, anh, ngaySinh,
                        gioiTinh, cccd, email, sdt,
                        chucVu, trangThai,
                        lyDo
                );

                ds.add(nv);
            }
            
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
		
	}
    
    
    public boolean themNhanVien(NhanVien nv) {
        int n = 0;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = "INSERT INTO NhanVien (maNV, hoTen, anhNhanVien, ngaySinh, gioiTinh, cccd, email, sdt, chucVu, trangThai, lyDoNghi) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getAnhNhanVien());

            // Date
            java.sql.Date sqlDate = new java.sql.Date(nv.getNgaySinh().getTime());
            ps.setDate(4, sqlDate);

            ps.setBoolean(5, nv.isGioiTinh());
            ps.setString(6, nv.getCccd());
            ps.setString(7, nv.getEmail());
            ps.setString(8, nv.getSdt());
            ps.setString(9, nv.getChucVu());
            ps.setString(10, nv.getTrangThai());
            ps.setString(11, nv.getLyDo());

            n = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n > 0;
    }
    
    
    public boolean capNhatNhanVien(NhanVien nv) {
        int n = 0;

        try {
            Connection con = ConnectDB.getConnection();

            String sql = "UPDATE NhanVien SET hoTen=?, anhNhanVien=?, ngaySinh=?, gioiTinh=?, "
                    + "cccd=?, email=?, sdt=?, chucVu=?, trangThai=?, lyDoNghi=? "
                    + "WHERE maNV=?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getAnhNhanVien());

            java.sql.Date sqlDate = new java.sql.Date(nv.getNgaySinh().getTime());
            ps.setDate(3, sqlDate);

            ps.setBoolean(4, nv.isGioiTinh());
            ps.setString(5, nv.getCccd());
            ps.setString(6, nv.getEmail());
            ps.setString(7, nv.getSdt());
            ps.setString(8, nv.getChucVu());
            ps.setString(9, nv.getTrangThai());

            ps.setString(10, nv.getLyDo());
            ps.setString(11, nv.getMaNV());

            n = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n > 0;
    }
    
    public String getNextMaNV() {
        String maMoi = "NV001";

        try {
            Connection con = ConnectDB.getConnection();

            String sql = "SELECT TOP 1 maNV FROM NhanVien ORDER BY maNV DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String maCu = rs.getString("maNV"); // ví dụ NV004

                int so = Integer.parseInt(maCu.substring(2)); // lấy 004 → 4
                so++; // tăng lên

                maMoi = String.format("NV%03d", so); // NV005
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return maMoi;
    }
    
}