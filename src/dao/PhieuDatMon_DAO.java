package dao;

import connectDB.ConnectDB;
import entity.PhieuDatBan;
import entity.PhieuDatMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PhieuDatMon_DAO {

    private PhieuDatMon_Ban_DAO phieuDatMonBanDAO =
            new PhieuDatMon_Ban_DAO();

    public boolean themPhieuDatMon(
            PhieuDatMon pdm
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                INSERT INTO PhieuDatMon
                (
                    maPhieuDatMon,
                    maPhieuDatBan,
                    hinhThucDatMon,
                    ghiChu,
                    thoiGianTao
                )
                VALUES (?, ?, ?, ?, ?)
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(
                    1,
                    pdm.getMaPhieuDatMon()
            );

            stmt.setString(
                    2,
                    pdm.getPhieuDatBan()
                            .getMaPhieuDatBan()
            );

            stmt.setString(
                    3,
                    pdm.getHinhThucDatMon()
            );

            stmt.setString(
                    4,
                    pdm.getGhiChu()
            );

            stmt.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            pdm.getThoiGianTao()
                    )
            );

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<PhieuDatMon> getDanhSachTheoPhieu(
            String maPhieuDatBan
    ){

        ArrayList<PhieuDatMon> ds =
                new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT *
                FROM PhieuDatMon
                WHERE maPhieuDatBan = ?
                ORDER BY thoiGianTao
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatBan);

            rs = stmt.executeQuery();

            while(rs.next()){

                PhieuDatBan pdb =
                        new PhieuDatBan();

                pdb.setMaPhieuDatBan(
                        maPhieuDatBan
                );

                PhieuDatMon pdm =
                        new PhieuDatMon();

                pdm.setMaPhieuDatMon(
                        rs.getString(
                                "maPhieuDatMon"
                        )
                );

                pdm.setPhieuDatBan(pdb);

                pdm.setHinhThucDatMon(
                        rs.getString(
                                "hinhThucDatMon"
                        )
                );

                pdm.setGhiChu(
                        rs.getString("ghiChu")
                );

                pdm.setThoiGianTao(
                        rs.getTimestamp(
                                "thoiGianTao"
                        ).toLocalDateTime()
                );

                ds.add(pdm);
            }

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(rs, stmt);
        }

        return ds;
    }

    public PhieuDatMon timTheoMa(
            String maPhieuDatMon
    ){

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                SELECT *
                FROM PhieuDatMon
                WHERE maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maPhieuDatMon);

            rs = stmt.executeQuery();

            if(rs.next()){

                PhieuDatBan pdb =
                        new PhieuDatBan();

                pdb.setMaPhieuDatBan(
                        rs.getString(
                                "maPhieuDatBan"
                        )
                );

                PhieuDatMon pdm =
                        new PhieuDatMon();

                pdm.setMaPhieuDatMon(
                        rs.getString(
                                "maPhieuDatMon"
                        )
                );

                pdm.setPhieuDatBan(pdb);

                pdm.setHinhThucDatMon(
                        rs.getString(
                                "hinhThucDatMon"
                        )
                );

                pdm.setGhiChu(
                        rs.getString("ghiChu")
                );

                pdm.setThoiGianTao(
                        rs.getTimestamp(
                                "thoiGianTao"
                        ).toLocalDateTime()
                );

                return pdm;
            }

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            close(rs, stmt);
        }

        return null;
    }

    public boolean capNhatPhieuDatMon(
            PhieuDatMon pdm
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                UPDATE PhieuDatMon
                SET hinhThucDatMon = ?,
                    ghiChu = ?
                WHERE maPhieuDatMon = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(
                    1,
                    pdm.getHinhThucDatMon()
            );

            stmt.setString(
                    2,
                    pdm.getGhiChu()
            );

            stmt.setString(
                    3,
                    pdm.getMaPhieuDatMon()
            );

            return stmt.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();
        }

        return false;
    }

    public boolean xoaPhieuDatMon(
            String maPhieuDatMon
    ){

        Connection con = null;
        PreparedStatement stmt = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """
                DELETE FROM PhieuDatMon
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

    public String layChuoiBanTheoPhieu(
            String maPhieuDatMon
    ){

        try{

            ArrayList<String> ds =
                    phieuDatMonBanDAO
                            .getDanhSachTenBanTheoPhieu(
                                    maPhieuDatMon
                            );

            return ds.isEmpty()
                    ? ""
                    : String.join(", ", ds);

        }catch(Exception e){

            e.printStackTrace();
        }

        return "";
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