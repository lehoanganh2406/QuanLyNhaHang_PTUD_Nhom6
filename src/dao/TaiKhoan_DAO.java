package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

	public TaiKhoan_DAO() {
	}

	public ArrayList<TaiKhoan> getAllTaiKhoan() {
		ArrayList<TaiKhoan> dsTK = new ArrayList<TaiKhoan>();
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM TaiKhoan";

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

				NhanVien nv = new NhanVien();
				nv.setMaNV(maNhanVien);

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

		String sql = "SELECT * FROM TaiKhoan WHERE maTaiKhoan = ?";

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

				NhanVien nv = new NhanVien();
				nv.setMaNV(maNhanVien);

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

		String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ?";

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

				NhanVien nv = new NhanVien();
				nv.setMaNV(maNhanVien);

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

		String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ? AND trangThai = 1";

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

				NhanVien nv = new NhanVien();
				nv.setMaNV(maNhanVien);

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