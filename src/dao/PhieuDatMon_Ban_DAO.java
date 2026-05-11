package dao;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatMon_Ban_DAO {

    public boolean themBanVaoPhieu(
            String maPhieuDatMon,
            String maBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO PhieuDatMon_Ban
                (
                    maPhieuDatMon,
                    maBan
                )
                VALUES (?, ?)
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);
            stmt.setString(2, maBan);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public boolean themNhieuBanVaoPhieu(
            String maPhieuDatMon,
            List<String> dsMaBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO PhieuDatMon_Ban
                (
                    maPhieuDatMon,
                    maBan
                )
                VALUES (?, ?)
            """;

            stmt = con.prepareStatement(sql);

            for(String maBan : dsMaBan){

                stmt.setString(1, maPhieuDatMon);
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
            String maPhieuDatMon
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                DELETE FROM PhieuDatMon_Ban
                WHERE maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<String> getDanhSachMaBanTheoPhieu(
            String maPhieuDatMon
    ){

        ArrayList<String> ds = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT maBan
                FROM PhieuDatMon_Ban
                WHERE maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

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
            String maPhieuDatMon
    ){

        ArrayList<String> ds = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT b.tenBan
                FROM PhieuDatMon_Ban pdmb
                JOIN Ban b
                    ON pdmb.maBan = b.maBan
                WHERE pdmb.maPhieuDatMon = ?
                ORDER BY b.tenBan
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

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
            String maPhieuDatMon,
            String maBan
    ){

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT COUNT(*)
                FROM PhieuDatMon_Ban
                WHERE maPhieuDatMon = ?
                AND maBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);
            stmt.setString(2, maBan);

            rs = stmt.executeQuery();

            if(rs.next()){

                return rs.getInt(1) > 0;
            }

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