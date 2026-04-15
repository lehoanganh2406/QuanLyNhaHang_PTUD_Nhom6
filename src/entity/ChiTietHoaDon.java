package entity;

import java.time.LocalDateTime;

public class ChiTietHoaDon {
	private static final long serialVersionUID = 1L;
	private HoaDon maHD;
	private MonAn maMon;
	private int soLuong;
	private double donGia;
	private String ghiChu;
	private String trangThai;
	private String lyDoHuy;
	private int soLuongHuy;
	private LocalDateTime thoiGianHuy;
	public HoaDon getMaHD() {
		return maHD;
	}
	public void setMaHD(HoaDon maHD) {
		this.maHD = maHD;
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
	public ChiTietHoaDon(HoaDon maHD, MonAn maMon, int soLuong, double donGia, String ghiChu, String trangThai,
			String lyDoHuy, int soLuongHuy, LocalDateTime thoiGianHuy) {
		super();
		this.maHD = maHD;
		this.maMon = maMon;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
		this.trangThai = trangThai;
		this.lyDoHuy = lyDoHuy;
		this.soLuongHuy = soLuongHuy;
		this.thoiGianHuy = thoiGianHuy;
	}
	@Override
	public String toString() {
		return "ChiTietHoaDon [maHD=" + maHD + ", maMon=" + maMon + ", soLuong=" + soLuong + ", donGia=" + donGia
				+ ", ghiChu=" + ghiChu + ", trangThai=" + trangThai + ", lyDoHuy=" + lyDoHuy + ", soLuongHuy="
				+ soLuongHuy + ", thoiGianHuy=" + thoiGianHuy + "]";
	}
	public ChiTietHoaDon() {
		// TODO Auto-generated constructor stub
	}
}
