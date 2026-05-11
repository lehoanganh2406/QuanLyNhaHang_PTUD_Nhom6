package dao;

import connectDB.ConnectDB;
import entity.Ban;
import entity.HoaDon;
import entity.HoaDon_Ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_Ban_DAO {

    // ================== THÊM ==================

	public boolean themBanVaoHoaDon(
	        String maHD,
	        String maBan
	){

	    Connection con = ConnectDB.getConnection();

	    try{

	        // =====================================
	        // ĐÃ TỒN TẠI -> KHÔNG INSERT LẠI
	        // =====================================

	        if(
	                kiemTraBanThuocHoaDon(
	                        maHD,
	                        maBan
	                )
	        ){

	            return true;
	        }

	        String sql = """
	            INSERT INTO HoaDon_Ban(
	                maHD,
	                maBan
	            )
	            VALUES(?,?)
	        """;

	        PreparedStatement ps =
	                con.prepareStatement(sql);

	        ps.setString(1, maHD);
	        ps.setString(2, maBan);

	        return ps.executeUpdate() > 0;

	    }catch(Exception e){

	        e.printStackTrace();
	    }

	    return false;
	}

    // ================== XÓA 1 BÀN ==================

    public boolean xoaBanKhoiHoaDon(
            String maHD,
            String maBan
    ){

        Connection con = ConnectDB.getConnection();

        String sql = """
            DELETE FROM HoaDon_Ban
            WHERE maHD = ?
            AND maBan = ?
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maBan);

            return ps.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    // ================== TÌM HÓA ĐƠN ==================

    public String timMaHDTheoBan(
            String maBan
    ){

        Connection con = ConnectDB.getConnection();

        String sql = """
            SELECT maHD
            FROM HoaDon_Ban
            WHERE maBan = ?
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maBan);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                return rs.getString("maHD");
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return null;
    }

    // ================== DS BÀN ==================

    public ArrayList<Ban> getDanhSachBanTheoHD(
            String maHD
    ){

        ArrayList<Ban> ds =
                new ArrayList<>();

        Connection con = ConnectDB.getConnection();

        String sql = """
            SELECT b.*
            FROM HoaDon_Ban hdb
            JOIN Ban b
                ON hdb.maBan = b.maBan
            WHERE hdb.maHD = ?
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                Ban ban = new Ban();

                ban.setMaBan(
                        rs.getString("maBan")
                );

                ban.setTenBan(
                        rs.getString("tenBan")
                );

                ban.setTrangThai(
                        rs.getString("trangThai")
                );

                ban.setSoChoNgoi(
                        rs.getInt("soChoNgoi")
                );

                ds.add(ban);
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return ds;
    }

    // ================== KIỂM TRA ==================

    public boolean kiemTraBanThuocHoaDon(
            String maHD,
            String maBan
    ){

        Connection con = ConnectDB.getConnection();

        String sql = """
            SELECT *
            FROM HoaDon_Ban
            WHERE maHD = ?
            AND maBan = ?
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maBan);

            ResultSet rs =
                    ps.executeQuery();

            return rs.next();

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    // ================== XÓA TẤT CẢ ==================

    public boolean xoaTatCaBanKhoiHoaDon(
            String maHD
    ){

        Connection con = ConnectDB.getConnection();

        String sql = """
            DELETE FROM HoaDon_Ban
            WHERE maHD = ?
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            return ps.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    // ================== GET ALL ==================

    public ArrayList<HoaDon_Ban> getAllHoaDonBan(){

        ArrayList<HoaDon_Ban> ds =
                new ArrayList<>();

        Connection con = ConnectDB.getConnection();

        String sql = """
            SELECT *
            FROM HoaDon_Ban
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                HoaDon hd =
                        new HoaDon(
                                rs.getString("maHD")
                        );

                Ban ban = new Ban();

                ban.setMaBan(
                        rs.getString("maBan")
                );

                HoaDon_Ban hdb =
                        new HoaDon_Ban(
                                hd,
                                ban
                        );

                ds.add(hdb);
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return ds;
    }

    // ================== CẬP NHẬT BÀN ==================

    public boolean capNhatBanHoaDon(
            String maHD,
            String maBanCu,
            String maBanMoi
    ){

        try{

            Connection con =
                    ConnectDB.getConnection();

            String sql = """
                UPDATE HoaDon_Ban
                SET maBan = ?
                WHERE maHD = ?
                AND maBan = ?
            """;

            PreparedStatement stmt =
                    con.prepareStatement(sql);

            stmt.setString(1, maBanMoi);
            stmt.setString(2, maHD);
            stmt.setString(3, maBanCu);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }
    public HoaDon timHoaDonTheoMa(
            String maHD
    ){

        HoaDon hd = null;

        try{

            Connection con =
                    ConnectDB.getConnection();

            String sql = """
                SELECT *
                FROM HoaDon
                WHERE maHD = ?
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                hd = new HoaDon();

                hd.setMaHD(
                        rs.getString("maHD")
                );
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return hd;
    }
    public String layBanDauTienCuaHoaDonTheoPhieu(
            String maPDB
    ){

        Connection con =
                ConnectDB.getConnection();

        String sql = """
            SELECT TOP 1 hb.maBan
            FROM HoaDon hd
            JOIN HoaDon_Ban hb
                ON hd.maHD = hb.maHD
            WHERE hd.maPhieuDatBan = ?
            ORDER BY hb.maBan
        """;

        try{

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maPDB);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                return rs.getString("maBan");
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return null;
    }
    public List<String> getDanhSachBanTheoHoaDon(String maHD){

        List<String> ds = new ArrayList<>();

        try{

            Connection con =
                    ConnectDB.getConnection();

            String sql = """
                SELECT maBan
                FROM HoaDon_Ban
                WHERE maHD = ?
                ORDER BY maBan
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                ds.add(
                        rs.getString("maBan")
                );
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return ds;
    }
}