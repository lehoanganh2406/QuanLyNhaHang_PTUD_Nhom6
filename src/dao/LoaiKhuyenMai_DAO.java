package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.LoaiKhuyenMai;

public class LoaiKhuyenMai_DAO {

	public LoaiKhuyenMai_DAO() {
	}

	public ArrayList<LoaiKhuyenMai> getAllLoaiKhuyenMai() {
		ArrayList<LoaiKhuyenMai> ds = new ArrayList<>();
		Connection con = ConnectDB.getConnection();

		String sql = "SELECT maLoaiKM, tenLoaiKM FROM LoaiKhuyenMai ORDER BY maLoaiKM";
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ds.add(new LoaiKhuyenMai(
						rs.getString("maLoaiKM"),
						rs.getString("tenLoaiKM")
				));
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ds;
	}
}