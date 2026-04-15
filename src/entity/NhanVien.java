package entity;

import java.util.Date;

public class NhanVien {
    private static final long serialVersionUID = 1L;

    private String maNV;
    private String hoTen;
    private String anhNhanVien;
    private Date ngaySinh;
    private boolean gioiTinh;
    private String cccd;
    private String email;
    private String sdt;
    private String chucVu;
    private String trangThai;
    private String lyDo; // 🔥 THÊM DÒNG NÀY

    // ================= GET / SET =================
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getAnhNhanVien() {
        return anhNhanVien;
    }

    public void setAnhNhanVien(String anhNhanVien) {
        this.anhNhanVien = anhNhanVien;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    // ================= CONSTRUCTOR =================

    // 🔥 Constructor đầy đủ (quan trọng nhất)
    public NhanVien(String maNV, String hoTen, String anhNhanVien, Date ngaySinh,
                    boolean gioiTinh, String cccd, String email, String sdt,
                    String chucVu, String trangThai, String lyDo) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.anhNhanVien = anhNhanVien;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.email = email;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
        this.lyDo = lyDo;
    }

    // Constructor cũ (giữ lại để không lỗi code cũ)
    public NhanVien(String maNV, String hoTen, String anhNhanVien, Date ngaySinh,
                    boolean gioiTinh, String cccd, String email, String sdt,
                    String chucVu, String trangThai) {
        this(maNV, hoTen, anhNhanVien, ngaySinh, gioiTinh, cccd, email, sdt, chucVu, trangThai, null);
    }

    public NhanVien(String maNV, String hoTen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
    }

    public NhanVien(String maNV) {
        this.maNV = maNV;
    }

    public NhanVien() {
    }

    // ================= TO STRING =================
    @Override
    public String toString() {
        return "NhanVien [maNV=" + maNV 
                + ", hoTen=" + hoTen 
                + ", ngaySinh=" + ngaySinh
                + ", gioiTinh=" + gioiTinh
                + ", cccd=" + cccd 
                + ", email=" + email 
                + ", sdt=" + sdt
                + ", chucVu=" + chucVu 
                + ", trangThai=" + trangThai
                + ", lyDo=" + lyDo + "]";
    }
}