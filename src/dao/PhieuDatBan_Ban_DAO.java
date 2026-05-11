package dao;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBan_Ban_DAO {

    public boolean themBanVaoPhieu(
            String maPhieuDatBan,
            String maBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO PhieuDatBan_Ban
                (
                    maPhieuDatBan,
                    maBan
                )
                VALUES (?, ?)
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatBan);
            stmt.setString(2, maBan);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public boolean themNhieuBanVaoPhieu(
            String maPhieuDatBan,
            List<String> dsMaBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO PhieuDatBan_Ban
                (
                    maPhieuDatBan,
                    maBan
                )
                VALUES (?, ?)
            """;

            stmt = con.prepareStatement(sql);

            for(String maBan : dsMaBan){

                stmt.setString(1, maPhieuDatBan);
                stmt.setString(2, maBan);

                stmt.addBatch();
            }

            stmt.executeBatch();

            return true;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public boolean xoaTatCaBanKhoiPhieu(
            String maPhieuDatBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                DELETE FROM PhieuDatBan_Ban
                WHERE maPhieuDatBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatBan);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<String> getDanhSachMaBanTheoPhieu(
            String maPhieuDatBan
    ){

        ArrayList<String> ds =
                new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT maBan
                FROM PhieuDatBan_Ban
                WHERE maPhieuDatBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatBan);

            rs = stmt.executeQuery();

            while(rs.next()){

                ds.add(rs.getString("maBan"));
            }

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(rs, stmt);
        }

        return ds;
    }

    public ArrayList<String> getDanhSachTenBanTheoPhieu(
            String maPhieuDatBan
    ){

        ArrayList<String> ds =
                new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT b.tenBan
                FROM PhieuDatBan_Ban pdbb
                JOIN Ban b
                    ON pdbb.maBan = b.maBan
                WHERE pdbb.maPhieuDatBan = ?
                ORDER BY b.tenBan
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatBan);

            rs = stmt.executeQuery();

            while(rs.next()){

                ds.add(rs.getString("tenBan"));
            }

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(rs, stmt);
        }

        return ds;
    }

    public boolean kiemTraBanThuocPhieu(
            String maPhieuDatBan,
            String maBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT COUNT(*)
                FROM PhieuDatBan_Ban
                WHERE maPhieuDatBan = ?
                AND maBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatBan);
            stmt.setString(2, maBan);

            rs = stmt.executeQuery();

            if(rs.next())
                return rs.getInt(1) > 0;

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(rs, stmt);
        }

        return false;
    }

    private void close(
            ResultSet rs,
            PreparedStatement stmt
    ){

        try{

            if(rs != null){

                rs.close();
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        try{

            if(stmt != null){

                stmt.close();
            }

        }catch(Exception e){

            e.printStackTrace();
        }
    }
}