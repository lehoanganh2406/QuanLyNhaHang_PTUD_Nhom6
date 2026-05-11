package entity;

public class MonAn {
    

    private String maMon;
    private LoaiMonAn maLoaiMonAn;
    private String tenMon;
    private String anhMon;

    private double giaGoc;   // ✅ thêm
    private double donGia;   // chỉ đọc

    private String moTa;
    private boolean trangThai;

    public MonAn() {}

    public MonAn(String maMon) {
        this.maMon = maMon;
    }

    public MonAn(String maMon, LoaiMonAn maLoaiMonAn, String tenMon,
                 String anhMon, double giaGoc, double donGia,
                 String moTa, boolean trangThai) {
        this.maMon = maMon;
        this.maLoaiMonAn = maLoaiMonAn;
        this.tenMon = tenMon;
        this.anhMon = anhMon;
        this.giaGoc = giaGoc;
        this.donGia = donGia;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    // ================= GET/SET =================

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public LoaiMonAn getMaLoaiMonAn() { return maLoaiMonAn; }
    public void setMaLoaiMonAn(LoaiMonAn maLoaiMonAn) { this.maLoaiMonAn = maLoaiMonAn; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public String getAnhMon() { return anhMon; }
    public void setAnhMon(String anhMon) { this.anhMon = anhMon; }

    public double getGiaGoc() { return giaGoc; }
    public void setGiaGoc(double giaGoc) { this.giaGoc = giaGoc; }

    public double getDonGia() { return donGia; }
    // ❌ KHÔNG setDonGia vì DB tự tính

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return "MonAn [maMon=" + maMon +
                ", tenMon=" + tenMon +
                ", giaGoc=" + giaGoc +
                ", donGia=" + donGia +
                "]";
    }
}