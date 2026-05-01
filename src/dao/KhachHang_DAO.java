package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.LoaiKhachHang;

public class KhachHang_DAO {

	public KhachHang_DAO() {
	}

	public ArrayList<KhachHang> getAllKhachHang() {
		ArrayList<KhachHang> dsKH = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT kh.maKH, kh.tenKH, kh.sdt, kh.diemTichLuy, "
				+ "lkh.maLoaiKH, lkh.tenLoaiKH "
				+ "FROM KhachHang kh "
				+ "JOIN LoaiKhachHang lkh ON kh.maLoaiKH = lkh.maLoaiKH";

		try (PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maKH = rs.getString("maKH");
				String tenKH = rs.getString("tenKH");
				String sdt = rs.getString("sdt");
				int diemTichLuy = rs.getInt("diemTichLuy");

				String maLoaiKH = rs.getString("maLoaiKH");
				String tenLoaiKH = rs.getString("tenLoaiKH");

				LoaiKhachHang loaiKH = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
				KhachHang kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diemTichLuy);

				dsKH.add(kh);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsKH;
	}

	public KhachHang getKhachHangTheoMa(String maTim) {
		KhachHang kh = null;
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT kh.maKH, kh.tenKH, kh.sdt, kh.diemTichLuy, "
				+ "lkh.maLoaiKH, lkh.tenLoaiKH "
				+ "FROM KhachHang kh "
				+ "JOIN LoaiKhachHang lkh ON kh.maLoaiKH = lkh.maLoaiKH "
				+ "WHERE kh.maKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maTim);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maKH = rs.getString("maKH");
					String tenKH = rs.getString("tenKH");
					String sdt = rs.getString("sdt");
					int diemTichLuy = rs.getInt("diemTichLuy");

					String maLoaiKH = rs.getString("maLoaiKH");
					String tenLoaiKH = rs.getString("tenLoaiKH");

					LoaiKhachHang loaiKH = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
					kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diemTichLuy);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return kh;
	}

	public KhachHang getKhachHangTheoSDT(String sdtTim) {
		KhachHang kh = null;
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT kh.maKH, kh.tenKH, kh.sdt, kh.diemTichLuy, "
				+ "lkh.maLoaiKH, lkh.tenLoaiKH "
				+ "FROM KhachHang kh "
				+ "JOIN LoaiKhachHang lkh ON kh.maLoaiKH = lkh.maLoaiKH "
				+ "WHERE kh.sdt = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, sdtTim);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maKH = rs.getString("maKH");
					String tenKH = rs.getString("tenKH");
					String sdt = rs.getString("sdt");
					int diemTichLuy = rs.getInt("diemTichLuy");

					String maLoaiKH = rs.getString("maLoaiKH");
					String tenLoaiKH = rs.getString("tenLoaiKH");

					LoaiKhachHang loaiKH = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
					kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diemTichLuy);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return kh;
	}

	public ArrayList<KhachHang> timKhachHangTheoTen(String tenTim) {
		ArrayList<KhachHang> dsKH = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT kh.maKH, kh.tenKH, kh.sdt, kh.diemTichLuy, "
				+ "lkh.maLoaiKH, lkh.tenLoaiKH "
				+ "FROM KhachHang kh "
				+ "JOIN LoaiKhachHang lkh ON kh.maLoaiKH = lkh.maLoaiKH "
				+ "WHERE kh.tenKH LIKE ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, "%" + tenTim + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maKH = rs.getString("maKH");
					String tenKH = rs.getString("tenKH");
					String sdt = rs.getString("sdt");
					int diemTichLuy = rs.getInt("diemTichLuy");

					String maLoaiKH = rs.getString("maLoaiKH");
					String tenLoaiKH = rs.getString("tenLoaiKH");

					LoaiKhachHang loaiKH = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
					KhachHang kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diemTichLuy);

					dsKH.add(kh);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsKH;
	}

	public boolean themKhachHang(KhachHang kh) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "INSERT INTO KhachHang(maKH, tenKH, sdt, maLoaiKH, diemTichLuy) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, kh.getMaKH());
			stmt.setString(2, kh.getTenKH());
			stmt.setString(3, kh.getSdt());
			stmt.setString(4, kh.getMaLoaiKH().getMaLoaiKH());
			stmt.setInt(5, kh.getDiemTichLuy());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean themKhachHangKhongCanMa(KhachHang kh) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "INSERT INTO KhachHang(tenKH, sdt, maLoaiKH, diemTichLuy) VALUES (?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, kh.getTenKH());
			stmt.setString(2, kh.getSdt());
			stmt.setString(3, kh.getMaLoaiKH().getMaLoaiKH());
			stmt.setInt(4, kh.getDiemTichLuy());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatKhachHang(KhachHang kh) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "UPDATE KhachHang "
				+ "SET tenKH = ?, sdt = ?, maLoaiKH = ?, diemTichLuy = ? "
				+ "WHERE maKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, kh.getTenKH());
			stmt.setString(2, kh.getSdt());
			stmt.setString(3, kh.getMaLoaiKH().getMaLoaiKH());
			stmt.setInt(4, kh.getDiemTichLuy());
			stmt.setString(5, kh.getMaKH());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean capNhatDiemTichLuy(String maKH, int diemMoi) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "UPDATE KhachHang SET diemTichLuy = ? WHERE maKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, diemMoi);
			stmt.setString(2, maKH);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean xoaKhachHang(String maKH) {
		Connection con = ConnectDB.getInstance().getConnection();
		String sql = "DELETE FROM KhachHang WHERE maKH = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maKH);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public double layTongGiaoDichTheoMaKH(String maKH) {
		double tong = 0;
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT ISNULL(SUM(tienKhachTra), 0) AS tongGiaoDich "
				+ "FROM HoaDon WHERE maKH = ? AND trangThai = N'Đã thanh toán'";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maKH);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					tong = rs.getDouble("tongGiaoDich");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tong;
	}

	// ===== PHẦN MỚI: PHÁT HIỆN KHÁCH HÀNG 6 THÁNG KHÔNG HOẠT ĐỘNG =====

	public ArrayList<KhachHang> getKhachHangKhongHoatDong6Thang() {
		ArrayList<KhachHang> dsKH = new ArrayList<>();
		Connection con = ConnectDB.getInstance().getConnection();

		String sql = "SELECT kh.maKH, kh.tenKH, kh.sdt, kh.diemTichLuy, "
				+ "lkh.maLoaiKH, lkh.tenLoaiKH, "
				+ "MAX(ISNULL(hd.thoiGianRa, hd.thoiGianVao)) AS lanHoatDongCuoi "
				+ "FROM KhachHang kh "
				+ "JOIN LoaiKhachHang lkh ON kh.maLoaiKH = lkh.maLoaiKH "
				+ "JOIN HoaDon hd ON hd.maKH = kh.maKH "
				+ "GROUP BY kh.maKH, kh.tenKH, kh.sdt, kh.diemTichLuy, lkh.maLoaiKH, lkh.tenLoaiKH "
				+ "HAVING MAX(hd.thoiGianVao) < DATEADD(MONTH, -6, GETDATE())";
		try (PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maKH = rs.getString("maKH");
				String tenKH = rs.getString("tenKH");
				String sdt = rs.getString("sdt");
				int diemTichLuy = rs.getInt("diemTichLuy");

				String maLoaiKH = rs.getString("maLoaiKH");
				String tenLoaiKH = rs.getString("tenLoaiKH");

				LoaiKhachHang loaiKH = new LoaiKhachHang(maLoaiKH, tenLoaiKH);
				KhachHang kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diemTichLuy);

				dsKH.add(kh);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsKH;
	}
}