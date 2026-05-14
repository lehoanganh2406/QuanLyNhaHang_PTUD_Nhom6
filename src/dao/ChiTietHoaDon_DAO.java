package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.MonAn;

public class ChiTietHoaDon_DAO {

    // ================== GET DS ==================
    public List<ChiTietHoaDon> getChiTietTheoMaHD(String maHD) {

        List<ChiTietHoaDon> ds = new ArrayList<>();

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    SELECT ct.*,
                           m.tenMon,
                           m.donGia
                    FROM ChiTietHoaDon ct
                    JOIN MonAn m
                        ON ct.maMon = m.maMon
                    WHERE ct.maHD = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ds.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // ================== THÊM ==================
    public boolean themChiTietHoaDon(ChiTietHoaDon ct) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    INSERT INTO ChiTietHoaDon
                    (
                        maHD,
                        maBan,
                        maMon,
                        soLuong,
                        donGia,
                        ghiChu,
                        trangThai,
                        lyDoHuy,
                        soLuongHuy,
                        thoiGianHuy,
                        thoiGianGui
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, ct.getMaHD().getMaHD());
            ps.setString(2, ct.getMaBan().getMaBan());
            ps.setString(3, ct.getMaMon().getMaMon());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getDonGia());
            ps.setString(6, ct.getGhiChu());
            ps.setString(7, ct.getTrangThai());
            ps.setString(8, ct.getLyDoHuy());
            ps.setInt(9, ct.getSoLuongHuy());

            if (ct.getThoiGianHuy() != null) {

                ps.setTimestamp(
                        10,
                        Timestamp.valueOf(
                                ct.getThoiGianHuy()
                        )
                );

            } else {

                ps.setNull(
                        10,
                        Types.TIMESTAMP
                );
            }

            if (ct.getThoiGianGui() != null) {

                ps.setTimestamp(
                        11,
                        Timestamp.valueOf(
                                ct.getThoiGianGui()
                        )
                );

            } else {

                ps.setNull(
                        11,
                        Types.TIMESTAMP
                );
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<ChiTietHoaDon> getChiTietGopTheoHoaDon(
            String maHD
    ) {

        List<ChiTietHoaDon> ds =
                new ArrayList<>();

        try {

            Connection con =
                    ConnectDB.getConnection();

            String sql =
                    """
                    SELECT
                        ct.maMon,
                        m.tenMon,
                        SUM(ct.soLuong) AS tongSL,
                        MAX(ct.donGia) AS donGia
                    FROM ChiTietHoaDon ct
                    JOIN MonAn m
                        ON ct.maMon = m.maMon
                    WHERE ct.maHD = ?
                    AND (
                        ct.trangThai IS NULL
                        OR ct.trangThai <> N'Đã hủy'
                    )
                    GROUP BY
                        ct.maMon,
                        m.tenMon
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                MonAn mon =
                        new MonAn();

                mon.setMaMon(
                        rs.getString("maMon")
                );

                mon.setTenMon(
                        rs.getString("tenMon")
                );
                mon.setDonGia(
                        rs.getDouble("donGia")
                );

                ChiTietHoaDon ct =
                        new ChiTietHoaDon();

                ct.setMaMon(mon);

                ct.setSoLuong(
                        rs.getInt("tongSL")
                );

                ct.setDonGia(
                        rs.getDouble("donGia")
                );

                ds.add(ct);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }

    // ================== GET THEO TRẠNG THÁI ==================
    public List<ChiTietHoaDon> getMonTheoTrangThai(String trangThai) {

        List<ChiTietHoaDon> ds = new ArrayList<>();

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    SELECT
    cthd.*,
    hb.maBan AS maBanHienTai,
    ma.tenMon,
    ma.anhMon,
    hd.hinhThucPhucVu

FROM ChiTietHoaDon cthd

JOIN MonAn ma
    ON ma.maMon = cthd.maMon

JOIN HoaDon hd
    ON hd.maHD = cthd.maHD

JOIN HoaDon_Ban hb
    ON hb.maHD = cthd.maHD

WHERE cthd.trangThai = ?

ORDER BY cthd.thoiGianGui ASC
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, trangThai);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ds.add(map(rs));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }

    // ================== GET 1 CHI TIẾT ==================
    public ChiTietHoaDon getChiTietHoaDon(
            String maHD,
            String maMon,
            String maBan
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    SELECT *
                    FROM ChiTietHoaDon
                    WHERE maHD = ?
                    AND maMon = ?
                    AND maBan = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maMon);
            ps.setString(3, maBan);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return map(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================== UPDATE ==================
    public boolean capNhatChiTietHoaDon(
            ChiTietHoaDon ct
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    UPDATE ChiTietHoaDon
                    SET soLuong = ?,
                        donGia = ?,
                        ghiChu = ?,
                        trangThai = ?
                    WHERE maHD = ?
                    AND maMon = ?
                    AND maBan = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getDonGia());
            ps.setString(3, ct.getGhiChu());
            ps.setString(4, ct.getTrangThai());

            ps.setString(5, ct.getMaHD().getMaHD());
            ps.setString(6, ct.getMaMon().getMaMon());
            ps.setString(7, ct.getMaBan().getMaBan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================== UPDATE TRẠNG THÁI ==================
    public boolean capNhatTrangThai(
            String maHD,
            String maMon,
            String maBan,
            String trangThai
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    UPDATE ChiTietHoaDon
                    SET trangThai = ?
                    WHERE maHD = ?
                    AND maMon = ?
                    AND maBan = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, trangThai);
            ps.setString(2, maHD);
            ps.setString(3, maMon);
            ps.setString(4, maBan);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

 // =====================================================
 // HỦY MÓN
 // =====================================================

 public boolean huyMon(
         String maHD,
         String maMon,
         String maBan,
         String lyDoHuy,
         int soLuongHuy
 ) {

     try {

         Connection con = ConnectDB.getConnection();

         String sql = """
                 UPDATE ChiTietHoaDon
                 SET trangThai = ?,
                     lyDoHuy = ?,
                     soLuongHuy = ?,
                     thoiGianHuy = ?
                 WHERE maHD = ?
                 AND maMon = ?
                 AND maBan = ?
                 """;

         PreparedStatement ps = con.prepareStatement(sql);

         ps.setString(1, "Đã hủy");
         ps.setString(2, lyDoHuy);
         ps.setInt(3, soLuongHuy);

         ps.setTimestamp(
                 4,
                 Timestamp.valueOf(
                         LocalDateTime.now()
                 )
         );

         ps.setString(5, maHD);
         ps.setString(6, maMon);
         ps.setString(7, maBan);

         return ps.executeUpdate() > 0;

     } catch (Exception e) {

         e.printStackTrace();
     }

     return false;
 }

    // ================== XÓA ==================
    public boolean xoaChiTietHoaDon(
            String maHD,
            String maMon,
            String maBan
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                    DELETE FROM ChiTietHoaDon
                    WHERE maHD = ?
                    AND maMon = ?
                    AND maBan = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maMon);
            ps.setString(3, maBan);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================== MAP ==================
    private ChiTietHoaDon map(ResultSet rs)
            throws SQLException {

    	HoaDon hd =
    	        new HoaDon(
    	                rs.getString("maHD")
    	        );

    	try{

    	    hd.setHinhThucPhucVu(
    	            rs.getString("hinhThucPhucVu")
    	    );

    	}catch(Exception e){
    	}

        Ban ban = new Ban();

        ban.setMaBan(
                rs.getString("maBan")
        );

        MonAn mon =
                new MonAn();

        mon.setMaMon(
                rs.getString("maMon")
        );
        mon.setDonGia(
                rs.getDouble("donGia")
        );

        try {

            mon.setTenMon(
                    rs.getString("tenMon")
            );

        } catch (Exception e) {

            mon.setTenMon(
                    rs.getString("maMon")
            );
        }

        Timestamp tsHuy =
                rs.getTimestamp("thoiGianHuy");

        Timestamp tsGui =
                rs.getTimestamp("thoiGianGui");

        return new ChiTietHoaDon(
                hd,
                ban,
                mon,
                rs.getInt("soLuong"),
                rs.getDouble("donGia"),
                rs.getString("ghiChu"),
                rs.getString("trangThai"),
                rs.getString("lyDoHuy"),
                rs.getInt("soLuongHuy"),
                tsHuy != null
                        ? tsHuy.toLocalDateTime()
                        : null,
                tsGui != null
                        ? tsGui.toLocalDateTime()
                        : null
        );
    }

    // ================== TÁCH MÓN ==================
    public boolean tachMonSangHoaDonKhac(
            String maHDCu,
            String maHDMoi,
            String maMon,
            int slTach,
            String maBanCu,
            String maBanMoi
    ) {

        try {

            ChiTietHoaDon ctCu =
                    getChiTietHoaDon(
                            maHDCu,
                            maMon,
                            maBanCu
                    );

            if (ctCu == null) return false;

            int slCon =
                    ctCu.getSoLuong() - slTach;

            if (slCon <= 0) {

                xoaChiTietHoaDon(
                        maHDCu,
                        maMon,
                        maBanCu
                );

            } else {

                ctCu.setSoLuong(slCon);

                capNhatChiTietHoaDon(ctCu);
            }

            ChiTietHoaDon ctMoi =
                    getChiTietHoaDon(
                            maHDMoi,
                            maMon,
                            maBanMoi
                    );

            if (ctMoi != null) {

                ctMoi.setSoLuong(
                        ctMoi.getSoLuong() + slTach
                );

                capNhatChiTietHoaDon(ctMoi);

            } else {

                Ban banMoi = new Ban();

                banMoi.setMaBan(maBanMoi);

                themChiTietHoaDon(
                        new ChiTietHoaDon(
                                new HoaDon(maHDMoi),
                                banMoi,
                                new MonAn(maMon),
                                slTach,
                                ctCu.getDonGia(),
                                ctCu.getGhiChu(),
                                ctCu.getTrangThai(),
                                null,
                                0,
                                null,
                                LocalDateTime.now()
                        )
                );
            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ================== UPDATE ALL STATUS ==================
    public boolean capNhatTatCaTrangThai(
            String maHD,
            String trangThai
    ) {

        Connection con = ConnectDB.getConnection();

        String sql =
            "UPDATE ChiTietHoaDon " +
            "SET trangThai = ? " +
            "WHERE maHD = ?";

        try {

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, trangThai);
            ps.setString(2, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================== GHÉP HÓA ĐƠN ==================
 // =====================================================
 // GHÉP HÓA ĐƠN
 // =====================================================

 public boolean ghepHoaDon(
         String maHDNguon,
         String maHDDich
 ) {

     try {

         Connection con =
                 ConnectDB.getConnection();

         // =============================================
         // LẤY DS MÓN HÓA ĐƠN NGUỒN
         // =============================================

         String sqlSelect =
                 """
                 SELECT *
                 FROM ChiTietHoaDon
                 WHERE maHD = ?
                 """;

         PreparedStatement psSelect =
                 con.prepareStatement(sqlSelect);

         psSelect.setString(1, maHDNguon);

         ResultSet rs =
                 psSelect.executeQuery();

         while (rs.next()) {

             String maMon =
                     rs.getString("maMon");

             String maBan =
                     rs.getString("maBan");

             int soLuong =
                     rs.getInt("soLuong");

             double donGia =
                     rs.getDouble("donGia");

             String ghiChu =
                     rs.getString("ghiChu");

             String trangThai =
                     rs.getString("trangThai");

             // =========================================
             // CHECK MÓN ĐÃ TỒN TẠI
             // =========================================

             String sqlCheck =
                     """
                     SELECT soLuong
                     FROM ChiTietHoaDon
                     WHERE maHD = ?
                     AND maMon = ?
                     AND maBan = ?
                     """;

             PreparedStatement psCheck =
                     con.prepareStatement(sqlCheck);

             psCheck.setString(1, maHDDich);
             psCheck.setString(2, maMon);
             psCheck.setString(3, maBan);

             ResultSet rsCheck =
                     psCheck.executeQuery();

             if (rsCheck.next()) {

                 int slCu =
                         rsCheck.getInt("soLuong");

                 // =====================================
                 // UPDATE SL
                 // =====================================

                 String sqlUpdate =
                         """
                         UPDATE ChiTietHoaDon
                         SET soLuong = ?
                         WHERE maHD = ?
                         AND maMon = ?
                         AND maBan = ?
                         """;

                 PreparedStatement psUpdate =
                         con.prepareStatement(sqlUpdate);

                 psUpdate.setInt(
                         1,
                         slCu + soLuong
                 );

                 psUpdate.setString(2, maHDDich);
                 psUpdate.setString(3, maMon);
                 psUpdate.setString(4, maBan);

                 psUpdate.executeUpdate();

             } else {

                 // =====================================
                 // INSERT MỚI
                 // =====================================

                 String sqlInsert =
                         """
                         INSERT INTO ChiTietHoaDon
                         (
                             maHD,
                             maBan,
                             maMon,
                             soLuong,
                             donGia,
                             ghiChu,
                             trangThai,
                             soLuongHuy
                         )
                         VALUES (?, ?, ?, ?, ?, ?, ?, 0)
                         """;

                 PreparedStatement psInsert =
                         con.prepareStatement(sqlInsert);

                 psInsert.setString(1, maHDDich);
                 psInsert.setString(2, maBan);
                 psInsert.setString(3, maMon);
                 psInsert.setInt(4, soLuong);
                 psInsert.setDouble(5, donGia);
                 psInsert.setString(6, ghiChu);
                 psInsert.setString(7, trangThai);

                 psInsert.executeUpdate();
             }
         }

         // =============================================
         // XÓA HĐ NGUỒN
         // =============================================

         String sqlDelete =
                 """
                 DELETE FROM ChiTietHoaDon
                 WHERE maHD = ?
                 """;

         PreparedStatement psDelete =
                 con.prepareStatement(sqlDelete);

         psDelete.setString(1, maHDNguon);

         psDelete.executeUpdate();

         return true;

     } catch (Exception e) {

         e.printStackTrace();
     }

     return false;
 }
    public ChiTietHoaDon getChiTietHoaDonTheoBan(
            String maHD,
            String maMon,
            String maBan
    ) {

        try {

            Connection con =
                    ConnectDB.getConnection();

            String sql =
                    """
                    SELECT *
                    FROM ChiTietHoaDon
                    WHERE maHD = ?
                    AND maMon = ?
                    AND maBan = ?
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maMon);
            ps.setString(3, maBan);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return map(rs);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
    public List<ChiTietHoaDon> getChiTietTheoMaHDVaBan(
            String maHD,
            String maBan
    ) {

        List<ChiTietHoaDon> ds =
                new ArrayList<>();

        try {

            Connection con =
                    ConnectDB.getConnection();

            String sql =
                    """
                    SELECT ct.*, m.tenMon,m.donGia
FROM ChiTietHoaDon ct
JOIN MonAn m
    ON ct.maMon = m.maMon
WHERE ct.maHD = ?
AND ct.maBan = ?
AND (
    ct.trangThai IS NULL
    OR ct.trangThai <> N'Đã hủy'
)
ORDER BY ct.thoiGianGui
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maBan);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                HoaDon hd =
                        new HoaDon();

                hd.setMaHD(
                        rs.getString("maHD")
                );

                Ban ban =
                        new Ban();

                ban.setMaBan(
                        rs.getString("maBan")
                );

                MonAn mon =
                        new MonAn();

                mon.setMaMon(
                        rs.getString("maMon")
                );

                mon.setTenMon(
                        rs.getString("tenMon")
                );
                mon.setDonGia(
                        rs.getDouble("donGia")
                );

                ChiTietHoaDon ct =
                        new ChiTietHoaDon();

                ct.setMaHD(hd);

                ct.setMaBan(ban);

                ct.setMaMon(mon);

                ct.setSoLuong(
                        rs.getInt("soLuong")
                );

                ct.setDonGia(
                        rs.getDouble("donGia")
                );

                ct.setGhiChu(
                        rs.getString("ghiChu")
                );

                ct.setTrangThai(
                        rs.getString("trangThai")
                );

                ds.add(ct);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }
 // =====================================================
 // CHUYỂN MÓN SANG BÀN KHÁC (CHUNG HÓA ĐƠN)
 // =====================================================

 public boolean capNhatBanChoMon(
         String maHD,
         String maMon,
         String maBanCu,
         String maBanMoi,
         int soLuongTach
 ) {

     try {

         Connection con =
                 ConnectDB.getConnection();

         // =============================================
         // LẤY CHI TIẾT BÀN CŨ
         // =============================================

         ChiTietHoaDon ctCu =
                 getChiTietHoaDon(
                         maHD,
                         maMon,
                         maBanCu
                 );

         if (ctCu == null) {
             return false;
         }

         // =============================================
         // GIẢM SL BÀN CŨ
         // =============================================

         int slCon =
                 ctCu.getSoLuong() - soLuongTach;

         if (slCon <= 0) {

             xoaChiTietHoaDon(
                     maHD,
                     maMon,
                     maBanCu
             );

         } else {

             ctCu.setSoLuong(slCon);

             capNhatChiTietHoaDon(ctCu);
         }

         // =============================================
         // CHECK BÀN MỚI ĐÃ CÓ MÓN CHƯA
         // =============================================

         ChiTietHoaDon ctMoi =
                 getChiTietHoaDon(
                         maHD,
                         maMon,
                         maBanMoi
                 );

         // =============================================
         // ĐÃ CÓ -> CỘNG SL
         // =============================================

         if (ctMoi != null) {

             ctMoi.setSoLuong(
                     ctMoi.getSoLuong()
                             + soLuongTach
             );

             capNhatChiTietHoaDon(ctMoi);
         }

         // =============================================
         // CHƯA CÓ -> INSERT
         // =============================================

         else {

             Ban banMoi =
                     new Ban();

             banMoi.setMaBan(maBanMoi);

             ChiTietHoaDon ctNew =
                     new ChiTietHoaDon(
                             new HoaDon(maHD),
                             banMoi,
                             ctCu.getMaMon(),
                             soLuongTach,
                             ctCu.getDonGia(),
                             ctCu.getGhiChu(),
                             ctCu.getTrangThai(),
                             null,
                             0,
                             null,
                             LocalDateTime.now()
                     );

             themChiTietHoaDon(ctNew);
         }

         return true;

     } catch (Exception e) {

         e.printStackTrace();
     }

     return false;
 }
 public boolean kiemTraBanCoMonTheoBan(String maBan) {

	    try {

	        Connection con =
	                ConnectDB.getConnection();

	        String sql =
	                """
	                SELECT TOP 1 *
	                FROM ChiTietHoaDon ct
	                JOIN HoaDon hd
	                    ON ct.maHD = hd.maHD
	                WHERE ct.maBan = ?
	                AND hd.trangThai = N'Chưa thanh toán'
	                AND (
	                    ct.trangThai IS NULL
	                    OR ct.trangThai <> N'Đã hủy'
	                )
	                """;

	        PreparedStatement ps =
	                con.prepareStatement(sql);

	        ps.setString(1, maBan);

	        ResultSet rs =
	                ps.executeQuery();

	        return rs.next();

	    } catch (Exception e) {

	        e.printStackTrace();
	    }

	    return false;
	}
 public List<ChiTietHoaDon> getChiTietTheoHoaDonVaBan(
	        String maHD,
	        String maBan
	) {

	    List<ChiTietHoaDon> ds = new ArrayList<>();

	    try {

	        Connection con = ConnectDB.getConnection();

	        String sql = """
	            SELECT *
	            FROM ChiTietHoaDon
	            WHERE maHD = ?
	            AND maBan = ?
	        """;

	        PreparedStatement ps = con.prepareStatement(sql);

	        ps.setString(1, maHD);
	        ps.setString(2, maBan);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {

	            ChiTietHoaDon ct = new ChiTietHoaDon();

	            MonAn mon = new MonAn();

	            mon.setMaMon(
	                    rs.getString("maMon")
	            );

	            ct.setMaMon(mon);

	            ct.setSoLuong(
	                    rs.getInt("soLuong")
	            );

	            ct.setDonGia(
	                    rs.getDouble("donGia")
	            );

	            ds.add(ct);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ds;
	}
 public boolean capNhatMaBanTheoHoaDon(
	        String maHD,
	        String maBanCu,
	        String maBanMoi
	) {

	    try {

	        Connection con =
	                ConnectDB.getConnection();

	        String sql =
	                """
	                UPDATE ChiTietHoaDon
	                SET maBan = ?
	                WHERE maHD = ?
	                AND maBan = ?
	                """;

	        PreparedStatement ps =
	                con.prepareStatement(sql);

	        ps.setString(1, maBanMoi);
	        ps.setString(2, maHD);
	        ps.setString(3, maBanCu);

	        return ps.executeUpdate() > 0;

	    } catch (Exception e) {

	        e.printStackTrace();
	    }

	    return false;
	}
}