package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.LoaiKhachHang;

public class LoaiKhachHang_DAO {

	public LoaiKhachHang_DAO() {
	}

	public ArrayList<LoaiKhachHang> getAllLoaiKhachHang() {
		ArrayList<LoaiKhachHang> dsLoaiKH = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT maLoaiKH, tenLoaiKH FROM LoaiKhachHang";

		try (PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maLoaiKH = rs.getString("maLoaiKH");
				String tenLoaiKH = rs.getString("tenLoaiKH");

				LoaiKhachHang lkh = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
				dsLoaiKH.add(lkh);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsLoaiKH;
	}

	public LoaiKhachHang getLoaiKhachHangTheoMa(String maTim) {
		LoaiKhachHang lkh = null;
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT maLoaiKH, tenLoaiKH FROM LoaiKhachHang WHERE maLoaiKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maTim);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maLoaiKH = rs.getString("maLoaiKH");
					String tenLoaiKH = rs.getString("tenLoaiKH");

					lkh = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lkh;
	}

	public boolean themLoaiKhachHang(LoaiKhachHang lkh) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "INSERT INTO LoaiKhachHang(maLoaiKH, tenLoaiKH) VALUES (?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, lkh.getMaLoaiKH());
			stmt.setString(2, lkh.getTenLoaiKH());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean themLoaiKhachHangKhongCanMa(LoaiKhachHang lkh) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "INSERT INTO LoaiKhachHang(tenLoaiKH) VALUES (?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, lkh.getTenLoaiKH());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatLoaiKhachHang(LoaiKhachHang lkh) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "UPDATE LoaiKhachHang SET tenLoaiKH = ? WHERE maLoaiKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, lkh.getTenLoaiKH());
			stmt.setString(2, lkh.getMaLoaiKH());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean xoaLoaiKhachHang(String maLoaiKH) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "DELETE FROM LoaiKhachHang WHERE maLoaiKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maLoaiKH);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}