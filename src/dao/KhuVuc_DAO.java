package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhuVuc;

public class KhuVuc_DAO {

	public KhuVuc_DAO() {
	}

	public ArrayList<KhuVuc> getAllKhuVuc() {
		ArrayList<KhuVuc> dsKhuVuc = new ArrayList<>();
		Connection con = ConnectDB.getConnection();

		String sql = "SELECT maKhuVuc, tenKhuVuc, soLuongBan, trangThai, kyHieu FROM KhuVuc ORDER BY tenKhuVuc";

		try (PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maKhuVuc = rs.getString("maKhuVuc");
				String tenKhuVuc = rs.getString("tenKhuVuc");
				int soLuongBan = rs.getInt("soLuongBan");
				String trangThai = rs.getString("trangThai");
				String kyHieu = rs.getString("kyHieu");

				KhuVuc kv = new KhuVuc(maKhuVuc, tenKhuVuc, soLuongBan, trangThai, kyHieu);
				dsKhuVuc.add(kv);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsKhuVuc;
	}

	public KhuVuc getKhuVucTheoMa(String maCanTim) {
		KhuVuc kv = null;
		Connection con = ConnectDB.getConnection();

		String sql = "SELECT maKhuVuc, tenKhuVuc, soLuongBan, trangThai, kyHieu FROM KhuVuc WHERE maKhuVuc = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maCanTim);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maKhuVuc = rs.getString("maKhuVuc");
					String tenKhuVuc = rs.getString("tenKhuVuc");
					int soLuongBan = rs.getInt("soLuongBan");
					String trangThai = rs.getString("trangThai");
					String kyHieu = rs.getString("kyHieu");

					kv = new KhuVuc(maKhuVuc, tenKhuVuc, soLuongBan, trangThai, kyHieu);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return kv;
	}

	public boolean themKhuVuc(KhuVuc kv) {
		Connection con = ConnectDB.getConnection();

		String sql = "INSERT INTO KhuVuc(maKhuVuc, tenKhuVuc, soLuongBan, trangThai, kyHieu) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, kv.getMaKhuVuc());
			stmt.setString(2, kv.getTenKhuVuc());
			stmt.setInt(3, kv.getSoLuongBan());
			stmt.setString(4, kv.getTrangThai());
			stmt.setString(5, kv.getKyHieu());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatKhuVuc(KhuVuc kv) {
		Connection con = ConnectDB.getConnection();

		String sql = "UPDATE KhuVuc SET tenKhuVuc = ?, soLuongBan = ?, trangThai = ?, kyHieu = ? WHERE maKhuVuc = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, kv.getTenKhuVuc());
			stmt.setInt(2, kv.getSoLuongBan());
			stmt.setString(3, kv.getTrangThai());
			stmt.setString(4, kv.getKyHieu());
			stmt.setString(5, kv.getMaKhuVuc());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean xoaKhuVuc(String maKhuVuc) {
		Connection con = ConnectDB.getConnection();

		String sql = "DELETE FROM KhuVuc WHERE maKhuVuc = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maKhuVuc);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}