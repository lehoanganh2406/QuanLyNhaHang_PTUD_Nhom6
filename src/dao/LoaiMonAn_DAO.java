package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.LoaiMonAn;

public class LoaiMonAn_DAO {

	public LoaiMonAn_DAO() {
	}

	public ArrayList<LoaiMonAn> getAllLoaiMonAn() {
		ArrayList<LoaiMonAn> dsLoai = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT maLoaiMonAn, tenLoaiMonAn FROM LoaiMonAn ORDER BY maLoaiMonAn";

		try (PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maLoai = rs.getString("maLoaiMonAn");
				String tenLoai = rs.getString("tenLoaiMonAn");

				dsLoai.add(new LoaiMonAn(maLoai, tenLoai));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsLoai;
	}

	public LoaiMonAn getLoaiMonAnTheoMa(String maCanTim) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT maLoaiMonAn, tenLoaiMonAn FROM LoaiMonAn WHERE maLoaiMonAn = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maCanTim);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maLoai = rs.getString("maLoaiMonAn");
					String tenLoai = rs.getString("tenLoaiMonAn");
					return new LoaiMonAn(maLoai, tenLoai);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<LoaiMonAn> timLoaiMonAnTheoTen(String tuKhoa) {
		ArrayList<LoaiMonAn> dsLoai = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT maLoaiMonAn, tenLoaiMonAn "
				+ "FROM LoaiMonAn "
				+ "WHERE tenLoaiMonAn LIKE ? "
				+ "ORDER BY maLoaiMonAn";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, "%" + tuKhoa + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maLoai = rs.getString("maLoaiMonAn");
					String tenLoai = rs.getString("tenLoaiMonAn");

					dsLoai.add(new LoaiMonAn(maLoai, tenLoai));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsLoai;
	}

	public boolean themLoaiMonAn(LoaiMonAn loai) {
		Connection con = ConnectDB.getInstance().getConnection();

		// maLoaiMonAn để DB tự sinh
		String sql = "INSERT INTO LoaiMonAn (tenLoaiMonAn) VALUES (?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, loai.getTenLoaiMonAn());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public String themLoaiMonAnTraMa(LoaiMonAn loai) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "INSERT INTO LoaiMonAn (tenLoaiMonAn) "
				+ "OUTPUT INSERTED.maLoaiMonAn "
				+ "VALUES (?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, loai.getTenLoaiMonAn());

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean capNhatLoaiMonAn(LoaiMonAn loai) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "UPDATE LoaiMonAn "
				+ "SET tenLoaiMonAn = ? "
				+ "WHERE maLoaiMonAn = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, loai.getTenLoaiMonAn());
			stmt.setString(2, loai.getMaLoaiMonAn());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean xoaLoaiMonAn(String maLoaiMonAn) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "DELETE FROM LoaiMonAn WHERE maLoaiMonAn = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maLoaiMonAn);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}