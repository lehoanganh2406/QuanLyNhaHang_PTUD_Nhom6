package dao;

import java.sql.*;
import java.util.*;
import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {

    public List<NhanVien> getTenNhanVien() {
        List<NhanVien> ds = new ArrayList<>();

        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM NhanVien";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String maNV = rs.getString("maNV");
                String hoTen = rs.getString("hoTen");
                NhanVien nv = new NhanVien(maNV,hoTen); // nếu entity bạn có constructor này
                ds.add(nv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
}