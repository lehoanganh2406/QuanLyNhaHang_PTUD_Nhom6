package dao;

import connectDB.ConnectDB;
import entity.ChiTietDatMon;
import entity.MonAn;
import entity.PhieuDatMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChiTietDatMon_DAO {

    public ArrayList<ChiTietDatMon> getDanhSachTheoMaPhieuDatMon(
            String maPhieuDatMon
    ){

        ArrayList<ChiTietDatMon> ds = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT ctdm.maMon,
                       ctdm.soLuong,
                       ctdm.donGia,
                       ctdm.ghiChu,
                       ma.tenMon
                FROM ChiTietDatMon ctdm
                JOIN MonAn ma
                    ON ctdm.maMon = ma.maMon
                WHERE ctdm.maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

            rs = stmt.executeQuery();

            while(rs.next()){

                MonAn mon = new MonAn();
                mon.setMaMon(rs.getString("maMon"));
                mon.setTenMon(rs.getString("tenMon"));

                PhieuDatMon pdm = new PhieuDatMon();
                pdm.setMaPhieuDatMon(maPhieuDatMon);

                ChiTietDatMon ct = new ChiTietDatMon();

                ct.setPhieuDatMon(pdm);
                ct.setMon(mon);
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                ct.setGhiChu(rs.getString("ghiChu"));

                ds.add(ct);
            }

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(rs, stmt);
        }

        return ds;
    }

    public boolean xoaTheoPhieu(
            String maPhieuDatMon
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                DELETE FROM ChiTietDatMon
                WHERE maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(null, stmt);
        }

        return false;
    }

    public boolean themChiTietDatMon(
            ChiTietDatMon ct
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO ChiTietDatMon
                (
                    maPhieuDatMon,
                    maMon,
                    soLuong,
                    donGia,
                    ghiChu
                )
                VALUES (?, ?, ?, ?, ?)
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(
                    1,
                    ct.getPhieuDatMon().getMaPhieuDatMon()
            );

            stmt.setString(
                    2,
                    ct.getMon().getMaMon()
            );

            stmt.setInt(3, ct.getSoLuong());

            stmt.setDouble(4, ct.getDonGia());

            stmt.setString(5, ct.getGhiChu());

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(null, stmt);
        }

        return false;
    }

    public boolean luuDanhSachMonTheoPhieu(
            String maPhieuDatMon,
            ArrayList<ChiTietDatMon> dsMon
    ){

        Connection con = null;

        PreparedStatement stmtDelete = null;
        PreparedStatement stmtInsert = null;

        try{

            con = ConnectDB.getConnection();

            con.setAutoCommit(false);

            String sqlDelete = """
                DELETE FROM ChiTietDatMon
                WHERE maPhieuDatMon = ?
            """;

            stmtDelete = con.prepareStatement(sqlDelete);

            stmtDelete.setString(1, maPhieuDatMon);

            stmtDelete.executeUpdate();

            if(dsMon != null && !dsMon.isEmpty()){

                String sqlInsert = """
                    INSERT INTO ChiTietDatMon
                    (
                        maPhieuDatMon,
                        maMon,
                        soLuong,
                        donGia,
                        ghiChu
                    )
                    VALUES (?, ?, ?, ?, ?)
                """;

                stmtInsert = con.prepareStatement(sqlInsert);

                for(ChiTietDatMon ct : dsMon){

                    stmtInsert.setString(1, maPhieuDatMon);
                    stmtInsert.setString(2, ct.getMon().getMaMon());
                    stmtInsert.setInt(3, ct.getSoLuong());
                    stmtInsert.setDouble(4, ct.getDonGia());
                    stmtInsert.setString(5, ct.getGhiChu());

                    stmtInsert.addBatch();
                }

                stmtInsert.executeBatch();
            }

            con.commit();

            con.setAutoCommit(true);

            return true;

        }catch(Exception e){

            e.printStackTrace();

            try{

                if(con != null){

                    con.rollback();

                    con.setAutoCommit(true);
                }

            }catch(Exception ex){

                ex.printStackTrace();
            }

        }finally{

            close(null, stmtDelete);
            close(null, stmtInsert);
        }

        return false;
    }

    public boolean coDatMonTheoPhieu(
            String maPhieuDatMon
    ){

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT COUNT(*)
                FROM ChiTietDatMon
                WHERE maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

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

    public ArrayList<ChiTietDatMon> getDanhSachTheoMaPhieuDatBan(
            String maPhieuDatBan
    ){

        ArrayList<ChiTietDatMon> ds = new ArrayList<>();

        String sql = """
            SELECT ctdm.maPhieuDatMon,
                   ctdm.maMon,
                   ctdm.soLuong,
                   ctdm.donGia,
                   ctdm.ghiChu,
                   ma.tenMon
            FROM ChiTietDatMon ctdm
            JOIN MonAn ma
                ON ctdm.maMon = ma.maMon
            JOIN PhieuDatMon pdm
                ON ctdm.maPhieuDatMon =
                   pdm.maPhieuDatMon
            WHERE pdm.maPhieuDatBan = ?
        """;

        try(
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql)
        ){

            stmt.setString(1, maPhieuDatBan);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){

                MonAn mon = new MonAn();
                mon.setMaMon(rs.getString("maMon"));
                mon.setTenMon(rs.getString("tenMon"));

                PhieuDatMon pdm = new PhieuDatMon();
                pdm.setMaPhieuDatMon(
                        rs.getString("maPhieuDatMon")
                );

                ChiTietDatMon ct = new ChiTietDatMon();

                ct.setPhieuDatMon(pdm);
                ct.setMon(mon);
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                ct.setGhiChu(rs.getString("ghiChu"));

                ds.add(ct);
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return ds;
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
    public ArrayList<ChiTietDatMon>
    getDanhSachTheoBan(
            String maBan
    ){

        ArrayList<ChiTietDatMon> ds =
                new ArrayList<>();

        String sql = """
            SELECT ctdm.maPhieuDatMon,
                   ctdm.maMon,
                   ctdm.soLuong,
                   ctdm.donGia,
                   ctdm.ghiChu,
                   ma.tenMon
            FROM PhieuDatMon_Ban pdb
            JOIN PhieuDatMon pdm
                 ON pdb.maPhieuDatMon =
                    pdm.maPhieuDatMon
            JOIN ChiTietDatMon ctdm
                 ON pdm.maPhieuDatMon =
                    ctdm.maPhieuDatMon
            JOIN MonAn ma
                 ON ctdm.maMon =
                    ma.maMon
            WHERE pdb.maBan = ?
        """;

        try(
                Connection con =
                        ConnectDB.getConnection();

                PreparedStatement stmt =
                        con.prepareStatement(sql)
        ){

            stmt.setString(1, maBan);

            ResultSet rs =
                    stmt.executeQuery();

            while(rs.next()){

                MonAn mon =
                        new MonAn();

                mon.setMaMon(
                        rs.getString("maMon")
                );

                mon.setTenMon(
                        rs.getString("tenMon")
                );

                PhieuDatMon pdm =
                        new PhieuDatMon();

                pdm.setMaPhieuDatMon(
                        rs.getString("maPhieuDatMon")
                );

                ChiTietDatMon ct =
                        new ChiTietDatMon();

                ct.setPhieuDatMon(pdm);
                ct.setMon(mon);

                ct.setSoLuong(
                        rs.getInt("soLuong")
                );

                ct.setDonGia(
                        rs.getDouble("donGia")
                );

                ct.setGhiChu(
                        rs.getString("ghiChu")
                );

                ds.add(ct);
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return ds;
    }
    public ArrayList<ChiTietDatMon>
    getDanhSachTheoPhieuVaBan(
            String maPhieuDatBan,
            String maBan
    ){

        ArrayList<ChiTietDatMon> ds =
                new ArrayList<>();

        try{

            Connection con =
                    ConnectDB.getConnection();

            String sql = """
                SELECT ctdm.*
                FROM ChiTietDatMon ctdm
                JOIN PhieuDatMon pdm
                    ON ctdm.maPhieuDatMon =
                       pdm.maPhieuDatMon
                JOIN PhieuDatMon_Ban map
                    ON map.maPhieuDatMon =
                       pdm.maPhieuDatMon
                WHERE pdm.maPhieuDatBan = ?
                AND map.maBan = ?
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maPhieuDatBan);

            ps.setString(2, maBan);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                ChiTietDatMon ct =
                        new ChiTietDatMon();

                MonAn mon =
                        new MonAn();

                mon.setMaMon(
                        rs.getString("maMon")
                );

                ct.setMon(mon);

                ct.setSoLuong(
                        rs.getInt("soLuong")
                );

                ct.setDonGia(
                        rs.getDouble("donGia")
                );

                ct.setGhiChu(
                        rs.getString("ghiChu")
                );

                ds.add(ct);
            }

        }catch(Exception e){

            e.printStackTrace();
        }

        return ds;
    }
}