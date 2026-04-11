package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	public ArrayList<String[]> getTatCaBanKemTrangThaiMacDinh() {
	    ArrayList<String[]> ds = new ArrayList<>();
	    Connection con = ConnectDB.getConnection();

	    String sql = "SELECT maBan, tenBan, trangThai FROM Ban ORDER BY tenBan";

	    try {
	        PreparedStatement stmt = con.prepareStatement(sql);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            String trangThai = rs.getString("trangThai");
	            if (trangThai == null || trangThai.trim().isEmpty()) {
	                trangThai = "Bàn trống";
	            }

	            ds.add(new String[] {
	                rs.getString("maBan"),
	                rs.getString("tenBan"),
	                trangThai
	            });
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ds;
	}

	public ArrayList<String[]> getDanhSachBanTheoThoiGian(Timestamp thoiGianChon) {
	    ArrayList<String[]> ds = new ArrayList<>();
	    Connection con = ConnectDB.getConnection();

	    String sql = """
	        SELECT 
	            b.maBan,
	            b.tenBan,
	            CASE
	                WHEN EXISTS (
	                    SELECT 1
	                    FROM HoaDon hd
	                    WHERE hd.maBan = b.maBan
	                      AND hd.thoiGianVao <= ?
	                      AND (hd.thoiGianRa IS NULL OR hd.thoiGianRa >= ?)
	                ) THEN N'Đang phục vụ'

	                WHEN EXISTS (
	                    SELECT 1
	                    FROM PhieuDatBan pdb
	                    WHERE pdb.maBan = b.maBan
	                      AND (pdb.trangThai IS NULL OR pdb.trangThai <> N'Đã hủy')
	                      AND pdb.thoiGianDen < DATEADD(HOUR, 2, ?)
	                      AND DATEADD(HOUR, 2, pdb.thoiGianDen) > ?
	                ) THEN N'Đã đặt'

	                ELSE N'Bàn trống'
	            END AS trangThaiHienTai
	        FROM Ban b
	        ORDER BY b.tenBan
	    """;

	    try {
	        PreparedStatement stmt = con.prepareStatement(sql);
	        stmt.setTimestamp(1, thoiGianChon);
	        stmt.setTimestamp(2, thoiGianChon);
	        stmt.setTimestamp(3, thoiGianChon);
	        stmt.setTimestamp(4, thoiGianChon);

	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            ds.add(new String[] {
	                rs.getString("maBan"),
	                rs.getString("tenBan"),
	                rs.getString("trangThaiHienTai")
	            });
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ds;
	}
	public ArrayList<String[]> getDanhSachBanTheoNgay(java.sql.Date ngayChon) {
	    ArrayList<String[]> ds = new ArrayList<>();
	    Connection con = ConnectDB.getConnection();

	    String sql = """
	        SELECT 
	            b.maBan,
	            b.tenBan,
	            kv.tenKhuVuc,
	            CASE
	                WHEN EXISTS (
	                    SELECT 1
	                    FROM HoaDon hd
	                    WHERE hd.maBan = b.maBan
	                      AND CAST(hd.thoiGianVao AS DATE) = ?
	                      AND (hd.trangThai IS NULL OR hd.trangThai <> N'Đã hủy')
	                      AND hd.thoiGianRa IS NULL
	                ) THEN N'Đang phục vụ'

	                WHEN EXISTS (
	                    SELECT 1
	                    FROM PhieuDatBan pdb
	                    WHERE pdb.maBan = b.maBan
	                      AND CAST(pdb.thoiGianDen AS DATE) = ?
	                      AND (pdb.trangThai IS NULL OR pdb.trangThai <> N'Đã hủy')
	                ) THEN N'Đã đặt'

	                ELSE N'Trống'
	            END AS trangThaiHienTai
	        FROM Ban b
	        JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
	        ORDER BY b.maBan
	    """;

	    try (PreparedStatement stmt = con.prepareStatement(sql)) {
	        stmt.setDate(1, ngayChon);
	        stmt.setDate(2, ngayChon);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                ds.add(new String[] {
	                    rs.getString("maBan"),
	                    rs.getString("tenBan"),
	                    rs.getString("tenKhuVuc"),
	                    rs.getString("trangThaiHienTai")
	                });
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ds;
	}
	public ArrayList<String[]> getDanhSachBanTheoNgayVaTuKhoa(java.sql.Date ngayChon, String tuKhoa) {
	    ArrayList<String[]> ds = new ArrayList<>();
	    Connection con = ConnectDB.getConnection();

	    String sql = """
	        SELECT 
	            b.maBan,
	            b.tenBan,
	            kv.tenKhuVuc,
	            CASE
	                WHEN EXISTS (
	                    SELECT 1
	                    FROM HoaDon hd
	                    WHERE hd.maBan = b.maBan
	                      AND CAST(hd.thoiGianVao AS DATE) = ?
	                      AND (hd.trangThai IS NULL OR hd.trangThai <> N'Đã hủy')
	                      AND hd.thoiGianRa IS NULL
	                ) THEN N'Đang phục vụ'

	                WHEN EXISTS (
	                    SELECT 1
	                    FROM PhieuDatBan pdb
	                    WHERE pdb.maBan = b.maBan
	                      AND CAST(pdb.thoiGianDen AS DATE) = ?
	                      AND (pdb.trangThai IS NULL OR pdb.trangThai <> N'Đã hủy')
	                ) THEN N'Đã đặt'

	                ELSE N'Trống'
	            END AS trangThaiHienTai
	        FROM Ban b
	        JOIN KhuVuc kv ON b.maKhuVuc = kv.maKhuVuc
	        WHERE b.maBan LIKE ? OR b.tenBan LIKE ?
	        ORDER BY b.maBan
	    """;

	    try (PreparedStatement stmt = con.prepareStatement(sql)) {
	        stmt.setDate(1, ngayChon);
	        stmt.setDate(2, ngayChon);
	        stmt.setString(3, "%" + tuKhoa + "%");
	        stmt.setString(4, "%" + tuKhoa + "%");

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                ds.add(new String[] {
	                    rs.getString("maBan"),
	                    rs.getString("tenBan"),
	                    rs.getString("tenKhuVuc"),
	                    rs.getString("trangThaiHienTai")
	                });
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ds;
	}
}