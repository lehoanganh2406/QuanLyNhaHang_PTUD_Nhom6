package entity;

import java.time.LocalDateTime;

public class ChiTietHoaDon {

    private HoaDon maHD;
    private Ban maBan;
    private MonAn maMon;

    private int soLuong;
    private double donGia;

    private String ghiChu;
    private String trangThai;

    private String lyDoHuy;
    private int soLuongHuy;

    private LocalDateTime thoiGianHuy;
    private LocalDateTime thoiGianGui;

    // ================= GETTER SETTER =================

    public HoaDon getMaHD() {
        return maHD;
    }

    public void setMaHD(HoaDon maHD) {
        this.maHD = maHD;
    }

    public Ban getMaBan() {
        return maBan;
    }

    public void setMaBan(Ban maBan) {
        this.maBan = maBan;
    }

    public MonAn getMaMon() {
        return maMon;
    }

    public void setMaMon(MonAn maMon) {
        this.maMon = maMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public int getSoLuongHuy() {
        return soLuongHuy;
    }

    public void setSoLuongHuy(int soLuongHuy) {
        this.soLuongHuy = soLuongHuy;
    }

    public LocalDateTime getThoiGianHuy() {
        return thoiGianHuy;
    }

    public void setThoiGianHuy(LocalDateTime thoiGianHuy) {
        this.thoiGianHuy = thoiGianHuy;
    }

    public LocalDateTime getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(LocalDateTime thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    // ================= THÀNH TIỀN =================

    public double getThanhTien() {
        return soLuong * donGia;
    }

    // ================= CONSTRUCTOR =================

    public ChiTietHoaDon(HoaDon maHD, Ban maBan, MonAn maMon,
                         int soLuong, double donGia,
                         String ghiChu, String trangThai,
                         String lyDoHuy, int soLuongHuy,
                         LocalDateTime thoiGianHuy,
                         LocalDateTime thoiGianGui) {

        this.maHD = maHD;
        this.maBan = maBan;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
        this.lyDoHuy = lyDoHuy;
        this.soLuongHuy = soLuongHuy;
        this.thoiGianHuy = thoiGianHuy;
        this.thoiGianGui = thoiGianGui;
    }

    public ChiTietHoaDon() {
    }
}