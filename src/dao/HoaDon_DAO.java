package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Ban;
import entity.HoaDon;
import entity.PhieuDatBan;

public class HoaDon_DAO {

    private HoaDon_Ban_DAO hoaDonBanDAO =
            new HoaDon_Ban_DAO();

    // ====================== LẤY TOÀN BỘ HÓA ĐƠN ======================

    public List<Object[]> getAllHoaDon() {

        List<Object[]> ds = new ArrayList<>();

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT hd.maHD,
                       hd.thoiGianVao,
                       hd.thoiGianRa,
                       kh.tenKH AS tenKH,
                       nv.hoTen AS tenNV,
                       kh.sdt,
                       km.tenKhuyenMai AS tenKM,
                       hd.tongTien,
                       hd.tienKhachTra,
                       hd.phuongThucThanhToan,
                       hd.hinhThucPhucVu,
                       hd.trangThai,
                       hd.lyDoHuy
                FROM HoaDon hd
                LEFT JOIN KhachHang kh
                    ON hd.maKH = kh.maKH
                LEFT JOIN NhanVien nv
                    ON hd.maNV = nv.maNV
                LEFT JOIN KhuyenMai km
                    ON hd.maKM = km.maKM
                ORDER BY hd.thoiGianVao DESC
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ds.add(new Object[]{
                        rs.getString("maHD"),
                        rs.getTimestamp("thoiGianVao"),
                        rs.getTimestamp("thoiGianRa"),
                        rs.getString("tenKH"),
                        rs.getString("tenNV"),
                        rs.getString("sdt"),
                        rs.getString("tenKM") != null
                                ? rs.getString("tenKM")
                                : "",
                        layChuoiBanTheoHD(
                                rs.getString("maHD")
                        ),
                        rs.getBigDecimal("tongTien"),
                        rs.getBigDecimal("tienKhachTra"),
                        rs.getString("phuongThucThanhToan"),
                        rs.getString("hinhThucPhucVu"),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy")
                });
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }

    // ====================== TÌM HÓA ĐƠN CHƯA THANH TOÁN ======================

    public HoaDon timHoaDonChuaThanhToanTheoBan(
            String maBan
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT TOP 1 hd.*
                FROM HoaDon hd
                JOIN HoaDon_Ban hdb
                    ON hd.maHD = hdb.maHD
                WHERE hdb.maBan = ?
                AND hd.trangThai = N'Chưa thanh toán'
                ORDER BY hd.thoiGianVao DESC
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maBan);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                HoaDon hd = new HoaDon();

                hd.setMaHD(rs.getString("maHD"));

                hd.setTrangThai(
                        rs.getString("trangThai")
                );

                hd.setHinhThucPhucVu(
                        rs.getString("hinhThucPhucVu")
                );

                return hd;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    // ====================== LẤY HÓA ĐƠN THEO MÃ ======================

    public Object[] getHoaDonByMa(
            String maHD
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT hd.maHD,
                       hd.thoiGianVao,
                       hd.thoiGianRa,
                       kh.tenKH,
                       nv.hoTen,
                       kh.sdt,
                       km.tenKhuyenMai,
                       hd.tongTien,
                       hd.tienKhachTra,
                       hd.thueVAT,
                       hd.tienThua,
                       hd.phuongThucThanhToan,
                       hd.hinhThucPhucVu,
                       hd.trangThai,
                       hd.lyDoHuy,
                       hd.maPhieuDatBan,
                       ISNULL(pdb.tienCoc,0) AS tienCoc
                FROM HoaDon hd
                LEFT JOIN KhachHang kh
                    ON hd.maKH = kh.maKH
                LEFT JOIN NhanVien nv
                    ON hd.maNV = nv.maNV
                LEFT JOIN KhuyenMai km
                    ON hd.maKM = km.maKM
                LEFT JOIN PhieuDatBan pdb
                    ON hd.maPhieuDatBan =
                       pdb.maPhieuDatBan
                WHERE hd.maHD = ?
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Object[]{
                        rs.getString("maHD"),
                        rs.getTimestamp("thoiGianVao"),
                        rs.getTimestamp("thoiGianRa"),
                        rs.getString("tenKH"),
                        rs.getString("hoTen"),
                        rs.getString("sdt"),
                        rs.getString("tenKhuyenMai"),
                        layChuoiBanTheoHD(
                                rs.getString("maHD")
                        ),
                        rs.getDouble("tongTien"),
                        rs.getDouble("tienKhachTra"),
                        rs.getDouble("thueVAT"),
                        rs.getDouble("tienThua"),
                        rs.getString(
                                "phuongThucThanhToan"
                        ),
                        rs.getString(
                                "hinhThucPhucVu"
                        ),
                        rs.getString("trangThai"),
                        rs.getString("lyDoHuy"),
                        rs.getString("maPhieuDatBan"),
                        rs.getDouble("tienCoc")
                };
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    // ====================== TẠO MÃ HÓA ĐƠN ======================

    public String taoMaHoaDonMoi() {

        Connection con = ConnectDB.getConnection();

        String sql =
                "SELECT NEXT VALUE FOR seq_HoaDon AS nextVal";

        try {

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int so = rs.getInt("nextVal");

                return String.format(
                        "HD%05d",
                        so
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    // ====================== THÊM HÓA ĐƠN ======================

    public boolean themHoaDonMoi(
            String maHD,
            List<String> dsBan,
            String maNV,
            String maPhieuDatBan,
            String maKH,
            String hinhThucPhucVu,
            String trangThai
    ) {

        try {

            Connection con =
                    ConnectDB.getConnection();

            String sql = """
                INSERT INTO HoaDon(
                    maHD,
                    maNV,
                    maKH,
                    maPhieuDatBan,
                    thoiGianVao,
                    hinhThucPhucVu,
                    trangThai
                )
                VALUES(?,?,?,?,?,?,?)
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maNV);

            if (maKH == null || maKH.trim().isEmpty())
                ps.setNull(3, Types.VARCHAR);
            else
                ps.setString(3, maKH);

            if (
                    maPhieuDatBan == null ||
                    maPhieuDatBan.trim().isEmpty()
            ) {
                ps.setNull(4, Types.VARCHAR);

            } else {
                ps.setString(4, maPhieuDatBan);
            }
            ps.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            LocalDateTime.now()
                    )
            );
            ps.setString(6, hinhThucPhucVu);
            ps.setString(7, trangThai);

            boolean ok =
                    ps.executeUpdate() > 0;

            if (!ok) {
                return false;
            }

            if (dsBan != null) {

                HoaDon_Ban_DAO hdbDAO =
                        new HoaDon_Ban_DAO();

                for (String maBan : dsBan) {

                    hdbDAO.themBanVaoHoaDon(
                            maHD,
                            maBan
                    );
                }
            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ====================== CẬP NHẬT TỔNG TIỀN ======================

    public boolean capNhatTongTien(
            String maHD
    ) {

        String sql = """
            UPDATE HoaDon
            SET tongTien = ISNULL((
                SELECT SUM(soLuong * donGia)
                FROM ChiTietHoaDon
                WHERE maHD = ?
                AND (
                    trangThai IS NULL
                    OR trangThai <> N'Đã hủy'
                )
            ),0)
            WHERE maHD = ?
        """;

        try {

            Connection con = ConnectDB.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);
            ps.setString(2, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ====================== THANH TOÁN HÓA ĐƠN ======================

    public boolean thanhToanHoaDon(
            String maHD,
            String maKH,
            String maKM,
            double tienKhachTra,
            double thueVAT,
            double tienThua,
            String phuongThucThanhToan
    ) {

        Connection con = null;
        PreparedStatement stmt = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """
                UPDATE HoaDon
                SET maKH = ?,
                    maKM = ?,
                    tienKhachTra = ?,
                    thueVAT = ?,
                    tienThua = ?,
                    phuongThucThanhToan = ?,
                    thoiGianRa = ?,
                    trangThai = N'Đã thanh toán'
                WHERE maHD = ?
            """;

            stmt = con.prepareStatement(sql);

            if (maKH == null || maKH.trim().isEmpty()) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, maKH);
            }

            if (maKM == null || maKM.trim().isEmpty()) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, maKM);
            }

            stmt.setDouble(3, tienKhachTra);
            stmt.setDouble(4, thueVAT);
            stmt.setDouble(5, tienThua);
            stmt.setString(6, phuongThucThanhToan);
            stmt.setTimestamp(
                    7,
                    Timestamp.valueOf(
                            LocalDateTime.now()
                    )
            );
            stmt.setString(8, maHD);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ====================== XÓA HÓA ĐƠN ======================

    public boolean xoaHoaDon(
            String maHD
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql =
                    "DELETE FROM HoaDon WHERE maHD=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ====================== CHUYỂN BÀN ======================

    public boolean chuyenBan(
            String maHD,
            String maBanMoi
    ) {

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                UPDATE HoaDon_Ban
                SET maBan = ?
                WHERE maHD = ?
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, maBanMoi);
            ps.setString(2, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ====================== LẤY CHUỖI BÀN ======================

    public String layChuoiBanTheoHD(
            String maHD
    ) {

        try {

            ArrayList<Ban> ds =
                    hoaDonBanDAO
                            .getDanhSachBanTheoHD(maHD);

            if (ds.isEmpty()) {

                return "";
            }

            StringBuilder sb =
                    new StringBuilder();

            for (int i = 0; i < ds.size(); i++) {

                Ban b = ds.get(i);

                sb.append(b.getTenBan());

                if (i < ds.size() - 1) {

                    sb.append(", ");
                }
            }

            return sb.toString();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }

    // ====================== LẤY DANH SÁCH NHÂN VIÊN ======================

    public List<String> getAllTenNhanVien() {

        List<String> ds = new ArrayList<>();

        try {

            Connection con = ConnectDB.getConnection();

            String sql = "SELECT hoTen FROM NhanVien";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ds.add(rs.getString("hoTen"));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }

    // ====================== LẤY DANH SÁCH KHUYẾN MÃI ======================

    public List<String> getAllTenKhuyenMai() {

        List<String> ds = new ArrayList<>();

        try {

            Connection con = ConnectDB.getConnection();

            String sql = """
                SELECT tenKhuyenMai
                FROM KhuyenMai
                WHERE trangThai = N'Đang áp dụng'
            """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ds.add(rs.getString("tenKhuyenMai"));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ds;
    }
    public boolean updateHoaDon(
            String maHD,
            String tenNV,
            String tenKM,
            String trangThai,
            String lyDoHuy,
            Timestamp thoiGianRa,
            String phuongThucThanhToan,
            String hinhThucPhucVu
    ) {

        Connection con = null;
        PreparedStatement stmt = null;

        try {

            con = ConnectDB.getConnection();

            String maNV = null;
            String maKM = null;

            // ===== lấy mã nhân viên =====

            if(
                    tenNV != null &&
                    !tenNV.trim().isEmpty()
            ){

                String sqlNV = """
                    SELECT maNV
                    FROM NhanVien
                    WHERE hoTen = ?
                """;

                PreparedStatement psNV =
                        con.prepareStatement(sqlNV);

                psNV.setString(1, tenNV);

                ResultSet rsNV =
                        psNV.executeQuery();

                if(rsNV.next()){

                    maNV = rsNV.getString("maNV");
                }
            }

            // ===== lấy mã khuyến mãi =====

            if(
                    tenKM != null &&
                    !tenKM.trim().isEmpty()
            ){

                String sqlKM = """
                    SELECT maKM
                    FROM KhuyenMai
                    WHERE tenKhuyenMai = ?
                """;

                PreparedStatement psKM =
                        con.prepareStatement(sqlKM);

                psKM.setString(1, tenKM);

                ResultSet rsKM =
                        psKM.executeQuery();

                if(rsKM.next()){

                    maKM = rsKM.getString("maKM");
                }
            }

            // ===== update =====

            String sql = """
                UPDATE HoaDon
                SET maNV = ?,
                    maKM = ?,
                    trangThai = ?,
                    lyDoHuy = ?,
                    thoiGianRa = ?,
                    phuongThucThanhToan = ?,
                    hinhThucPhucVu = ?
                WHERE maHD = ?
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maNV);
            stmt.setString(2, maKM);
            stmt.setString(3, trangThai);

            if(
                    lyDoHuy == null ||
                    lyDoHuy.trim().isEmpty()
            ){
                stmt.setNull(4, Types.NVARCHAR);

            }else{

                stmt.setString(4, lyDoHuy);
            }

            stmt.setTimestamp(5, thoiGianRa);
            stmt.setString(6, phuongThucThanhToan);
            stmt.setString(7, hinhThucPhucVu);
            stmt.setString(8, maHD);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
    public HoaDon timHoaDonChungTheoBan(
            String maBan
    ) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            con = ConnectDB.getConnection();

            String sql = """
                SELECT TOP 1 hd.*
                FROM HoaDon hd
                JOIN HoaDon_Ban hdb
                    ON hd.maHD = hdb.maHD
                WHERE hdb.maBan = ?
                AND hd.trangThai = N'Chưa thanh toán'
                ORDER BY hd.thoiGianVao DESC
            """;

            stmt = con.prepareStatement(sql);

            stmt.setString(1, maBan);

            rs = stmt.executeQuery();

            if(rs.next()){

                HoaDon hd = new HoaDon();

                hd.setMaHD(
                        rs.getString("maHD")
                );

                hd.setTrangThai(
                        rs.getString("trangThai")
                );

                hd.setHinhThucPhucVu(
                        rs.getString("hinhThucPhucVu")
                );

                return hd;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }
}