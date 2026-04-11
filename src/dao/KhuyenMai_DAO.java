package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.KhuyenMai;
import entity.LoaiKhuyenMai;
import entity.NhanVien;

public class KhuyenMai_DAO {

	public KhuyenMai_DAO() {
	}

	public ArrayList<KhuyenMai> getAllKhuyenMai() {
		ArrayList<KhuyenMai> ds = new ArrayList<>();
		Connection con = ConnectDB.getConnection();

		String sql = "SELECT km.*, lkm.tenLoaiKM " +
		             "FROM KhuyenMai km " +
		             "INNER JOIN LoaiKhuyenMai lkm ON km.maLoaiKM = lkm.maLoaiKM " +
		             "ORDER BY km.maKM";

		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ds.add(mapKhuyenMai(rs));
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ds;
	}

	public ArrayList<KhuyenMai> timKhuyenMai(String tuKhoa, java.sql.Date tuNgay, java.sql.Date denNgay, String trangThaiLoc) {
		ArrayList<KhuyenMai> ds = new ArrayList<>();
		Connection con = ConnectDB.getConnection();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT km.*, lkm.tenLoaiKM ");
		sql.append("FROM KhuyenMai km ");
		sql.append("INNER JOIN LoaiKhuyenMai lkm ON km.maLoaiKM = lkm.maLoaiKM ");
		sql.append("WHERE 1=1 ");

		if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
			sql.append("AND (km.maKM LIKE ? OR km.tenKhuyenMai LIKE ?) ");
		}

		if (tuNgay != null) {
			sql.append("AND CAST(km.thoiGianBatDau AS DATE) >= ? ");
		}

		if (denNgay != null) {
			sql.append("AND CAST(km.thoiGianBatDau AS DATE) <= ? ");
		}

		if (trangThaiLoc != null && !trangThaiLoc.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(trangThaiLoc)) {
			if ("Sử dụng".equalsIgnoreCase(trangThaiLoc)) {
				sql.append("AND km.trangThai = N'Đang áp dụng' ");
			} else if ("Ngưng sử dụng".equalsIgnoreCase(trangThaiLoc)) {
				sql.append("AND km.trangThai <> N'Đang áp dụng' ");
			}
		}

		sql.append("ORDER BY km.maKM");

		try {
			PreparedStatement stmt = con.prepareStatement(sql.toString());
			int idx = 1;

			if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
				stmt.setString(idx++, "%" + tuKhoa.trim() + "%");
				stmt.setString(idx++, "%" + tuKhoa.trim() + "%");
			}

			if (tuNgay != null) {
				stmt.setDate(idx++, tuNgay);
			}

			if (denNgay != null) {
				stmt.setDate(idx++, denNgay);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ds.add(mapKhuyenMai(rs));
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ds;
	}

	private KhuyenMai mapKhuyenMai(ResultSet rs) throws Exception {
		String maKM = rs.getString("maKM");

		String maLoaiKM = rs.getString("maLoaiKM");
		String tenLoaiKM = rs.getString("tenLoaiKM");

		String maNV = rs.getString("maNV");
		double giaTri = rs.getDouble("giaTri");
		String tenKhuyenMai = rs.getString("tenKhuyenMai");

		Timestamp tsBD = rs.getTimestamp("thoiGianBatDau");
		Timestamp tsKT = rs.getTimestamp("thoiGianKetThuc");

		LocalDateTime batDau = tsBD != null ? tsBD.toLocalDateTime() : null;
		LocalDateTime ketThuc = tsKT != null ? tsKT.toLocalDateTime() : null;

		String doiTuongApDung = rs.getString("doiTuongApDung");
		double dieuKienApDung = rs.getDouble("dieuKienApDung");
		String ghiChu = rs.getString("ghiChu");
		String trangThai = rs.getString("trangThai");

		LoaiKhuyenMai loaiKM = new LoaiKhuyenMai(maLoaiKM, tenLoaiKM);
		NhanVien nv = new NhanVien(maNV); // nhớ entity NhanVien phải có constructor này

		return new KhuyenMai(maKM, loaiKM, nv, giaTri, tenKhuyenMai, batDau, ketThuc,
				doiTuongApDung, dieuKienApDung, ghiChu, trangThai);
	}
}