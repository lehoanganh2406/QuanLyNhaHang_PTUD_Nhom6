package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.MonAn;
import entity.PhieuDatBan;
import entity.PhieuDatMon;

public class PhieuDatMon_DAO {

	public ArrayList<PhieuDatMon> getDanhSachTheoMaPhieu(String maPhieuDatBan) {
	    ArrayList<PhieuDatMon> ds = new ArrayList<>();
	    Connection con = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        con = ConnectDB.getConnection();

	        String sql = "SELECT pdm.maMon, pdm.soLuong, pdm.donGia, pdm.ghiChu, ma.tenMon "
	                + "FROM PhieuDatMon pdm "
	                + "JOIN MonAn ma ON pdm.maMon = ma.maMon "
	                + "WHERE pdm.maPhieuDatBan = ?";

	        stmt = con.prepareStatement(sql);
	        stmt.setString(1, maPhieuDatBan);

	        rs = stmt.executeQuery();
	        while (rs.next()) {
	            MonAn mon = new MonAn();
	            mon.setMaMon(rs.getString("maMon"));
	            mon.setTenMon(rs.getString("tenMon"));

	            PhieuDatMon pdm = new PhieuDatMon();
	            pdm.setMaMon(mon);
	            pdm.setSoLuong(rs.getInt("soLuong"));
	            pdm.setDonGia(rs.getDouble("donGia"));
	            pdm.setGhiChu(rs.getString("ghiChu"));

	            ds.add(pdm);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return ds;
	}

    public boolean xoaTheoMaPhieu(String maPhieuDatBan) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "DELETE FROM PhieuDatMon WHERE maPhieuDatBan = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuDatBan);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(null, stmt);
        }

        return false;
    }

    public boolean themPhieuDatMon(PhieuDatMon pdm) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "INSERT INTO PhieuDatMon(maPhieuDatBan, maMon, soLuong, donGia, ghiChu) VALUES (?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, pdm.getMaPhieuDatBan().getMaPhieuDatBan());
            stmt.setString(2, pdm.getMaMon().getMaMon());
            stmt.setInt(3, pdm.getSoLuong());
            stmt.setDouble(4, pdm.getDonGia());
            stmt.setString(5, pdm.getGhiChu());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(null, stmt);
        }

        return false;
    }

    public boolean luuDanhSachMonChoPhieu(String maPhieuDatBan, ArrayList<PhieuDatMon> dsMon) {
        Connection con = null;
        PreparedStatement stmtDelete = null;
        PreparedStatement stmtInsert = null;

        try {
            con = ConnectDB.getInstance().getConnection();
            con.setAutoCommit(false);

            String sqlDelete = "DELETE FROM PhieuDatMon WHERE maPhieuDatBan = ?";
            stmtDelete = con.prepareStatement(sqlDelete);
            stmtDelete.setString(1, maPhieuDatBan);
            stmtDelete.executeUpdate();

            if (dsMon != null && !dsMon.isEmpty()) {
                String sqlInsert = "INSERT INTO PhieuDatMon(maPhieuDatBan, maMon, soLuong, donGia, ghiChu) VALUES (?, ?, ?, ?, ?)";
                stmtInsert = con.prepareStatement(sqlInsert);

                for (PhieuDatMon pdm : dsMon) {
                    stmtInsert.setString(1, maPhieuDatBan);
                    stmtInsert.setString(2, pdm.getMaMon().getMaMon());
                    stmtInsert.setInt(3, pdm.getSoLuong());
                    stmtInsert.setDouble(4, pdm.getDonGia());
                    stmtInsert.setString(5, pdm.getGhiChu());
                    stmtInsert.addBatch();
                }

                stmtInsert.executeBatch();
            }

            con.commit();
            con.setAutoCommit(true);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
                if (con != null) con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            close(null, stmtDelete);
            close(null, stmtInsert);
        }

        return false;
    }

    private void close(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (stmt != null) stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean coDatMonTheoPhieu(String maPhieuDatBan) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            String sql = "SELECT COUNT(*) FROM PhieuDatMon WHERE maPhieuDatBan = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuDatBan);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}