package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;

public class Ban_DAO {

	public Ban_DAO() {
	}

	public ArrayList<Ban> getAllBan() {
		ArrayList<Ban> dsBan = new ArrayList<Ban>();
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM Ban";

		try {
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				String maBan = rs.getString("maBan");
				String maKhuVucStr = rs.getString("maKhuVuc");
				String maLoaiBanStr = rs.getString("maLoaiBan");
				String tenBan = rs.getString("tenBan");
				String ghiChu = rs.getString("ghiChu");
				int soChoNgoi = rs.getInt("soChoNgoi");
				String trangThai = rs.getString("trangThai");

				KhuVuc khuVuc = new KhuVuc();
				khuVuc.setMaKhuVuc(maKhuVucStr);

				LoaiBan loaiBan = new LoaiBan();
				loaiBan.setMaLoaiBan(maLoaiBanStr);

				Ban ban = new Ban(maBan, khuVuc, loaiBan, tenBan, ghiChu, soChoNgoi, trangThai);
				dsBan.add(ban);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsBan;
	}

	public Ban getBanTheoMa(String maBanCanTim) {
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM Ban WHERE maBan = ?";

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maBanCanTim);
			rs = stmt.executeQuery();

			if (rs.next()) {
				String maBan = rs.getString("maBan");
				String maKhuVucStr = rs.getString("maKhuVuc");
				String maLoaiBanStr = rs.getString("maLoaiBan");
				String tenBan = rs.getString("tenBan");
				String ghiChu = rs.getString("ghiChu");
				int soChoNgoi = rs.getInt("soChoNgoi");
				String trangThai = rs.getString("trangThai");

				KhuVuc khuVuc = new KhuVuc();
				khuVuc.setMaKhuVuc(maKhuVucStr);

				LoaiBan loaiBan = new LoaiBan();
				loaiBan.setMaLoaiBan(maLoaiBanStr);

				return new Ban(maBan, khuVuc, loaiBan, tenBan, ghiChu, soChoNgoi, trangThai);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<Ban> getBanTheoKhuVuc(String maKhuVucCanTim) {
		ArrayList<Ban> dsBan = new ArrayList<Ban>();
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM Ban WHERE maKhuVuc = ?";

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maKhuVucCanTim);
			rs = stmt.executeQuery();

			while (rs.next()) {
				String maBan = rs.getString("maBan");
				String maKhuVucStr = rs.getString("maKhuVuc");
				String maLoaiBanStr = rs.getString("maLoaiBan");
				String tenBan = rs.getString("tenBan");
				String ghiChu = rs.getString("ghiChu");
				int soChoNgoi = rs.getInt("soChoNgoi");
				String trangThai = rs.getString("trangThai");

				KhuVuc khuVuc = new KhuVuc();
				khuVuc.setMaKhuVuc(maKhuVucStr);

				LoaiBan loaiBan = new LoaiBan();
				loaiBan.setMaLoaiBan(maLoaiBanStr);

				Ban ban = new Ban(maBan, khuVuc, loaiBan, tenBan, ghiChu, soChoNgoi, trangThai);
				dsBan.add(ban);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsBan;
	}

	public boolean themBan(Ban ban) {
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;

		String sql = "INSERT INTO Ban(maBan, maKhuVuc, maLoaiBan, tenBan, ghiChu, soChoNgoi, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, ban.getMaBan());
			stmt.setString(2, ban.getMaKhuVuc().getMaKhuVuc());
			stmt.setString(3, ban.getMaLoaiBan().getMaLoaiBan());
			stmt.setString(4, ban.getTenBan());
			stmt.setString(5, ban.getGhiChu());
			stmt.setInt(6, ban.getSoChoNgoi());
			stmt.setString(7, ban.getTrangThai());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatBan(Ban ban) {
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;

		String sql = "UPDATE Ban SET maKhuVuc = ?, maLoaiBan = ?, tenBan = ?, ghiChu = ?, soChoNgoi = ?, trangThai = ? WHERE maBan = ?";

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, ban.getMaKhuVuc().getMaKhuVuc());
			stmt.setString(2, ban.getMaLoaiBan().getMaLoaiBan());
			stmt.setString(3, ban.getTenBan());
			stmt.setString(4, ban.getGhiChu());
			stmt.setInt(5, ban.getSoChoNgoi());
			stmt.setString(6, ban.getTrangThai());
			stmt.setString(7, ban.getMaBan());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean xoaBan(String maBan) {
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;

		String sql = "DELETE FROM Ban WHERE maBan = ?";

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maBan);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
		Connection con = ConnectDB.getConnection();
		PreparedStatement stmt = null;

		String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, trangThaiMoi);
			stmt.setString(2, maBan);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}