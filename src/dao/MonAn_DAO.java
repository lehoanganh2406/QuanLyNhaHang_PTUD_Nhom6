package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.LoaiMonAn;
import entity.MonAn;

public class MonAn_DAO {

	public MonAn_DAO() {
	}

	public ArrayList<MonAn> getAllMonAn() {
		ArrayList<MonAn> dsMon = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT m.maMon, m.maLoaiMonAn, l.tenLoaiMonAn, "
				+ "m.tenMon, m.anhMon, m.donGia, m.moTa, m.trangThai "
				+ "FROM MonAn m "
				+ "JOIN LoaiMonAn l ON m.maLoaiMonAn = l.maLoaiMonAn "
				+ "ORDER BY m.maMon";

		try (PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maMon = rs.getString("maMon");
				String maLoai = rs.getString("maLoaiMonAn");
				String tenLoai = rs.getString("tenLoaiMonAn");
				String tenMon = rs.getString("tenMon");
				String anhMon = rs.getString("anhMon");
				double donGia = rs.getDouble("donGia");
				String moTa = rs.getString("moTa");
				boolean trangThai = rs.getBoolean("trangThai");

				LoaiMonAn loai = new LoaiMonAn(maLoai, tenLoai);
				MonAn mon = new MonAn(maMon, loai, tenMon, anhMon, donGia, moTa, trangThai);

				dsMon.add(mon);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsMon;
	}

	public MonAn getMonAnTheoMa(String maCanTim) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT m.maMon, m.maLoaiMonAn, l.tenLoaiMonAn, "
				+ "m.tenMon, m.anhMon, m.donGia, m.moTa, m.trangThai "
				+ "FROM MonAn m "
				+ "JOIN LoaiMonAn l ON m.maLoaiMonAn = l.maLoaiMonAn "
				+ "WHERE m.maMon = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maCanTim);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maMon = rs.getString("maMon");
					String maLoai = rs.getString("maLoaiMonAn");
					String tenLoai = rs.getString("tenLoaiMonAn");
					String tenMon = rs.getString("tenMon");
					String anhMon = rs.getString("anhMon");
					double donGia = rs.getDouble("donGia");
					String moTa = rs.getString("moTa");
					boolean trangThai = rs.getBoolean("trangThai");

					LoaiMonAn loai = new LoaiMonAn(maLoai, tenLoai);
					return new MonAn(maMon, loai, tenMon, anhMon, donGia, moTa, trangThai);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<MonAn> timMonTheoTen(String tuKhoa) {
		ArrayList<MonAn> dsMon = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT m.maMon, m.maLoaiMonAn, l.tenLoaiMonAn, "
				+ "m.tenMon, m.anhMon, m.donGia, m.moTa, m.trangThai "
				+ "FROM MonAn m "
				+ "JOIN LoaiMonAn l ON m.maLoaiMonAn = l.maLoaiMonAn "
				+ "WHERE m.tenMon LIKE ? "
				+ "ORDER BY m.maMon";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, "%" + tuKhoa + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maMon = rs.getString("maMon");
					String maLoai = rs.getString("maLoaiMonAn");
					String tenLoai = rs.getString("tenLoaiMonAn");
					String tenMon = rs.getString("tenMon");
					String anhMon = rs.getString("anhMon");
					double donGia = rs.getDouble("donGia");
					String moTa = rs.getString("moTa");
					boolean trangThai = rs.getBoolean("trangThai");

					LoaiMonAn loai = new LoaiMonAn(maLoai, tenLoai);
					MonAn mon = new MonAn(maMon, loai, tenMon, anhMon, donGia, moTa, trangThai);

					dsMon.add(mon);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsMon;
	}

	public ArrayList<MonAn> getMonTheoLoai(String maLoaiCanTim) {
		ArrayList<MonAn> dsMon = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT m.maMon, m.maLoaiMonAn, l.tenLoaiMonAn, "
				+ "m.tenMon, m.anhMon, m.donGia, m.moTa, m.trangThai "
				+ "FROM MonAn m "
				+ "JOIN LoaiMonAn l ON m.maLoaiMonAn = l.maLoaiMonAn "
				+ "WHERE m.maLoaiMonAn = ? "
				+ "ORDER BY m.maMon";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maLoaiCanTim);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maMon = rs.getString("maMon");
					String maLoai = rs.getString("maLoaiMonAn");
					String tenLoai = rs.getString("tenLoaiMonAn");
					String tenMon = rs.getString("tenMon");
					String anhMon = rs.getString("anhMon");
					double donGia = rs.getDouble("donGia");
					String moTa = rs.getString("moTa");
					boolean trangThai = rs.getBoolean("trangThai");

					LoaiMonAn loai = new LoaiMonAn(maLoai, tenLoai);
					MonAn mon = new MonAn(maMon, loai, tenMon, anhMon, donGia, moTa, trangThai);

					dsMon.add(mon);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsMon;
	}

	public boolean themMonAn(MonAn mon) {
		Connection con = ConnectDB.getInstance().getConnection();

		// maMon để DB tự sinh
		String sql = "INSERT INTO MonAn (maLoaiMonAn, tenMon, anhMon, donGia, moTa, trangThai) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, mon.getMaLoaiMonAn().getMaLoaiMonAn());
			stmt.setString(2, mon.getTenMon());
			stmt.setString(3, mon.getAnhMon());
			stmt.setDouble(4, mon.getDonGia());
			stmt.setString(5, mon.getMoTa());
			stmt.setBoolean(6, mon.isTrangThai());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public String themMonAnTraMa(MonAn mon) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "INSERT INTO MonAn (maLoaiMonAn, tenMon, anhMon, donGia, moTa, trangThai) "
				+ "OUTPUT INSERTED.maMon "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, mon.getMaLoaiMonAn().getMaLoaiMonAn());
			stmt.setString(2, mon.getTenMon());
			stmt.setString(3, mon.getAnhMon());
			stmt.setDouble(4, mon.getDonGia());
			stmt.setString(5, mon.getMoTa());
			stmt.setBoolean(6, mon.isTrangThai());

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

	public boolean capNhatMonAn(MonAn mon) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "UPDATE MonAn "
				+ "SET maLoaiMonAn = ?, tenMon = ?, anhMon = ?, donGia = ?, moTa = ?, trangThai = ? "
				+ "WHERE maMon = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, mon.getMaLoaiMonAn().getMaLoaiMonAn());
			stmt.setString(2, mon.getTenMon());
			stmt.setString(3, mon.getAnhMon());
			stmt.setDouble(4, mon.getDonGia());
			stmt.setString(5, mon.getMoTa());
			stmt.setBoolean(6, mon.isTrangThai());
			stmt.setString(7, mon.getMaMon());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatTrangThai(String maMon, boolean trangThai) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "UPDATE MonAn SET trangThai = ? WHERE maMon = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setBoolean(1, trangThai);
			stmt.setString(2, maMon);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean xoaMonAn(String maMon) {
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "DELETE FROM MonAn WHERE maMon = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maMon);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}