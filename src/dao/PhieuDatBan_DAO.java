package dao;

import connectDB.ConnectDB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBan_DAO {

    private PhieuDatBan_Ban_DAO phieuDatBanBanDAO =
            new PhieuDatBan_Ban_DAO();

    public String themPhieuDatBan(
            List<String> dsMaBan,
            String tenKhach,
            String sdt,
            int soLuongNguoi,
            Timestamp thoiGianDen,
            BigDecimal tienCoc,
            String ghiChu,
            String trangThai,
            String phuongThucThanhToanCoc
    ) {

        Connection con = null;

        PreparedStatement stmt = null;

        ResultSet rs = null;

        try {

            con = ConnectDB.getConnection();

            con.setAutoCommit(false);

            String sql = """
                INSERT INTO PhieuDatBan
                (
                    tenKhach,
                    sdt,
                    soLuongNguoi,
                    thoiGianDen,
                    tienCoc,
                    ghiChu,
                    trangThai,
                    phuongThucThanhToanCoc,
                    thoiGianDatPhieu,
                    phuongThucHoanTien,
                    lyDoHuy,
                    tienHoanTra
                )
                OUTPUT INSERTED.maPhieuDatBan
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, tenKhach);

            stmt.setString(2, sdt);

            stmt.setInt(3, soLuongNguoi);

            stmt.setTimestamp(4, thoiGianDen);

            stmt.setBigDecimal(5, tienCoc);

            if (
                    ghiChu == null
                    ||
                    ghiChu.trim().isEmpty()
            ) {

                stmt.setNull(6, Types.NVARCHAR);

            } else {

                stmt.setString(6, ghiChu.trim());
            }

            stmt.setString(7, trangThai);

            stmt.setString(8, phuongThucThanhToanCoc);

            stmt.setTimestamp(
                    9,
                    new Timestamp(
                            System.currentTimeMillis()
                    )
            );

            stmt.setNull(10, Types.NVARCHAR);

            stmt.setNull(11, Types.NVARCHAR);

            stmt.setBigDecimal(
                    12,
                    BigDecimal.ZERO
            );

            rs = stmt.executeQuery();

            if (rs.next()) {

                String maPhieuDatBan =
                        rs.getString(1);

                String sqlBan = """
                    INSERT INTO PhieuDatBan_Ban
                    (
                        maPhieuDatBan,
                        maBan
                    )
                    VALUES
                    (
                        ?, ?
                    )
                """;

                PreparedStatement psBan =
                        con.prepareStatement(sqlBan);

                for (String maBan : dsMaBan) {

                    psBan.setString(
                            1,
                            maPhieuDatBan
                    );

                    psBan.setString(
                            2,
                            maBan
                    );

                    psBan.addBatch();
                }

                psBan.executeBatch();

                con.commit();

                con.setAutoCommit(true);

                return maPhieuDatBan;
            }

        } catch (Exception e) {

            e.printStackTrace();

            try {

                if (con != null) {

                    con.rollback();

                    con.setAutoCommit(true);
                }

            } catch (Exception ex) {

                ex.printStackTrace();
            }

        } finally {

            closeResources(rs, stmt);
        }

        return null;
    }

    public boolean kiemTraTrungLich(
            String maBan,
            Timestamp thoiGianDen,
            int soPhutMacDinh
    ) {

        Connection con = null;

        PreparedStatement stmt = null;

        ResultSet rs = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """
                SELECT COUNT(*)
                FROM PhieuDatBan pdb
                JOIN PhieuDatBan_Ban pdbb
                    ON pdb.maPhieuDatBan =
                       pdbb.maPhieuDatBan
                WHERE pdbb.maBan = ?
                AND pdb.trangThai IN
                (
                    N'Đang chờ',
                    N'Đã đặt',
                    N'Đã nhận bàn'
                )
                AND ABS(
                    DATEDIFF(
                        MINUTE,
                        pdb.thoiGianDen,
                        ?
                    )
                ) < ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maBan);

            stmt.setTimestamp(2, thoiGianDen);

            stmt.setInt(3, soPhutMacDinh);

            rs = stmt.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            closeResources(rs, stmt);
        }

        return true;
    }

    public ArrayList<String[]> getPhieuDatBanTheoNgay(
            java.sql.Date ngay
    ) {

        ArrayList<String[]> ds =
                new ArrayList<>();

        Connection con = null;

        PreparedStatement stmt = null;

        ResultSet rs = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """

            	    SELECT
            	        pdb.maPhieuDatBan,

            	        STRING_AGG(
            	            pdbb.maBan,
            	            ','
            	        ) AS dsMaBan,

            	        pdb.tenKhach,
            	        pdb.sdt,
            	        pdb.soLuongNguoi,
            	        pdb.thoiGianDen,
            	        pdb.tienCoc,
            	        pdb.ghiChu,
            	        pdb.trangThai,
            	        pdb.phuongThucHoanTien,
            	        pdb.lyDoHuy,
            	        pdb.tienHoanTra,
            	        pdb.phuongThucThanhToanCoc,
            	        pdb.thoiGianDatPhieu

            	    FROM PhieuDatBan pdb

            	    JOIN PhieuDatBan_Ban pdbb
            	        ON pdb.maPhieuDatBan =
            	           pdbb.maPhieuDatBan

            	    WHERE CAST(pdb.thoiGianDen AS DATE)=?

            	    GROUP BY
            	        pdb.maPhieuDatBan,
            	        pdb.tenKhach,
            	        pdb.sdt,
            	        pdb.soLuongNguoi,
            	        pdb.thoiGianDen,
            	        pdb.tienCoc,
            	        pdb.ghiChu,
            	        pdb.trangThai,
            	        pdb.phuongThucHoanTien,
            	        pdb.lyDoHuy,
            	        pdb.tienHoanTra,
            	        pdb.phuongThucThanhToanCoc,
            	        pdb.thoiGianDatPhieu

            	    ORDER BY pdb.thoiGianDen ASC

            	""";

            stmt = con.prepareStatement(sql);

            stmt.setDate(1, ngay);

            rs = stmt.executeQuery();

            while (rs.next()) {

                String maPhieu =
                        rs.getString(
                                "maPhieuDatBan"
                        );

                String[] row =
                        new String[14];

                row[0] = maPhieu;

                row[1] =
                        rs.getString("dsMaBan");

                row[2] =
                        rs.getString("tenKhach");

                row[3] =
                        rs.getString("sdt");

                row[4] =
                        String.valueOf(
                                rs.getInt(
                                        "soLuongNguoi"
                                )
                        );

                row[5] =
                        String.valueOf(
                                rs.getTimestamp(
                                        "thoiGianDen"
                                )
                        );

                row[6] =
                        rs.getBigDecimal(
                                "tienCoc"
                        ) == null
                                ? "0"
                                : rs.getBigDecimal(
                                        "tienCoc"
                                ).toPlainString();

                row[7] =
                        rs.getString("ghiChu");

                row[8] =
                        rs.getString("trangThai");

                row[9] =
                        rs.getString(
                                "phuongThucHoanTien"
                        );

                row[10] =
                        rs.getString("lyDoHuy");

                row[11] =
                        rs.getBigDecimal(
                                "tienHoanTra"
                        ) == null
                                ? "0"
                                : rs.getBigDecimal(
                                        "tienHoanTra"
                                ).toPlainString();

                row[12] =
                        rs.getString(
                                "phuongThucThanhToanCoc"
                        );

                row[13] =
                        String.valueOf(
                                rs.getTimestamp(
                                        "thoiGianDatPhieu"
                                )
                        );

                ds.add(row);
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            closeResources(rs, stmt);
        }

        return ds;
    }

    public String[] timTheoMaPhieu(
            String maPhieuDatBan
    ) {

        Connection con = null;

        PreparedStatement stmt = null;

        ResultSet rs = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """
                SELECT *
                FROM PhieuDatBan
                WHERE maPhieuDatBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(
                    1,
                    maPhieuDatBan
            );

            rs = stmt.executeQuery();

            if (rs.next()) {

                String[] row =
                        new String[14];

                row[0] =
                        rs.getString(
                                "maPhieuDatBan"
                        );

                row[1] =
                        layDanhSachMaBanTheoPhieu(
                                maPhieuDatBan
                        );

                row[2] =
                        rs.getString("tenKhach");

                row[3] =
                        rs.getString("sdt");

                row[4] =
                        String.valueOf(
                                rs.getInt(
                                        "soLuongNguoi"
                                )
                        );

                row[5] =
                        String.valueOf(
                                rs.getTimestamp(
                                        "thoiGianDen"
                                )
                        );

                row[6] =
                        rs.getBigDecimal(
                                "tienCoc"
                        ) == null
                                ? "0"
                                : rs.getBigDecimal(
                                        "tienCoc"
                                ).toPlainString();

                row[7] =
                        rs.getString("ghiChu");

                row[8] =
                        rs.getString("trangThai");

                row[9] =
                        rs.getString(
                                "phuongThucHoanTien"
                        );

                row[10] =
                        rs.getString("lyDoHuy");

                row[11] =
                        rs.getBigDecimal(
                                "tienHoanTra"
                        ) == null
                                ? "0"
                                : rs.getBigDecimal(
                                        "tienHoanTra"
                                ).toPlainString();

                row[12] =
                        rs.getString(
                                "phuongThucThanhToanCoc"
                        );

                row[13] =
                        String.valueOf(
                                rs.getTimestamp(
                                        "thoiGianDatPhieu"
                                )
                        );

                return row;
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            closeResources(rs, stmt);
        }

        return null;
    }
    public String layDanhSachMaBanTheoPhieu(
            String maPhieuDatBan
    ){

        Connection con = null;

        PreparedStatement stmt = null;

        ResultSet rs = null;

        try{

            con = ConnectDB.getConnection();

            String sql = """

                SELECT STRING_AGG(
                    maBan,
                    ','
                ) AS dsMaBan
                FROM PhieuDatBan_Ban
                WHERE maPhieuDatBan = ?

            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(
                    1,
                    maPhieuDatBan
            );

            rs = stmt.executeQuery();

            if(rs.next()){

                return rs.getString(
                        "dsMaBan"
                );
            }

        }catch(Exception e){

            e.printStackTrace();

        }finally{

            closeResources(rs, stmt);
        }

        return "";
    }

    public boolean capNhatTrangThai(
            String maPhieuDatBan,
            String trangThaiMoi
    ) {

        Connection con = null;

        PreparedStatement stmt = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """
                UPDATE PhieuDatBan
                SET trangThai = ?
                WHERE maPhieuDatBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, trangThaiMoi);

            stmt.setString(2, maPhieuDatBan);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            closeResources(null, stmt);
        }

        return false;
    }

   

    public boolean huyPhieuDatBanVaLuuThongTin(
            String maPhieuDatBan,
            String phuongThucHoanTien,
            String lyDoHuy,
            BigDecimal tienHoanTra
    ) {

        Connection con = null;

        PreparedStatement stmt = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """
                UPDATE PhieuDatBan
                SET trangThai = ?,
                    phuongThucHoanTien = ?,
                    lyDoHuy = ?,
                    tienHoanTra = ?
                WHERE maPhieuDatBan = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, "Đã hủy");

            stmt.setString(2, phuongThucHoanTien);

            stmt.setString(3, lyDoHuy);

            stmt.setBigDecimal(
                    4,
                    tienHoanTra == null
                            ? BigDecimal.ZERO
                            : tienHoanTra
            );

            stmt.setString(5, maPhieuDatBan);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            closeResources(null, stmt);
        }

        return false;
    }

    public ArrayList<String[]> getPhieuTreQua30Phut() {

        ArrayList<String[]> ds =
                new ArrayList<>();

        String sql = """
        	    SELECT
        	        maPhieuDatBan,
        	        tenKhach,
        	        sdt,
        	        thoiGianDen
        	    FROM PhieuDatBan
        	    WHERE trangThai IN
        	    (
        	        N'Đang chờ',
        	        N'Đã đặt'
        	    )
        	    AND trangThai <> N'Đã hủy'
        	    AND thoiGianDen IS NOT NULL
        	    AND DATEADD(
        	        MINUTE,
        	        30,
        	        thoiGianDen
        	    ) <= ?
        	""";

        try {

            Connection con =
                    ConnectDB.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setTimestamp(
                    1,
                    new Timestamp(
                            System.currentTimeMillis()
                    )
            );

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                String maPhieu =
                        rs.getString(
                                "maPhieuDatBan"
                        );

                ds.add(new String[]{
                        maPhieu,
                        layChuoiBanTheoPhieu(
                                maPhieu
                        ),
                        rs.getString("tenKhach"),
                        rs.getString("sdt"),
                        String.valueOf(
                                rs.getTimestamp(
                                        "thoiGianDen"
                                )
                        )
                });
            }

            rs.close();

            ps.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }

    public boolean giaHanThoiGianCho(
            String maPhieuDatBan
    ) {

        String sql = """
            UPDATE PhieuDatBan
            SET thoiGianDen =
                DATEADD(
                    MINUTE,
                    30,
                    thoiGianDen
                )
            WHERE maPhieuDatBan = ?
        """;

        try {

            Connection con =
                    ConnectDB.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(
                    1,
                    maPhieuDatBan
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    public String layChuoiBanTheoPhieu(
            String maPhieuDatBan
    ) {

        try {

            ArrayList<String> ds =
                    phieuDatBanBanDAO
                            .getDanhSachTenBanTheoPhieu(
                                    maPhieuDatBan
                            );

            if (ds.isEmpty()) {

                return "";
            }

            return String.join(", ", ds);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }

    private void closeResources(
            ResultSet rs,
            PreparedStatement stmt
    ) {

        try {

            if (rs != null) {

                rs.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        try {

            if (stmt != null) {

                stmt.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}