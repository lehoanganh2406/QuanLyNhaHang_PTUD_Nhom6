package entity;

import java.time.LocalDateTime;

public class PhieuDatBan {
	private static final long serialVersionUID = 1L;
	private String maPhieuDatBan;
	private Ban maBan;
	private String tenKhach;
	private String sdt;
	private int soLuongNguoi;
	private LocalDateTime thoiGianDen;
	private double tienCoc;
	private String ghiChu;
	private String trangThai;
	public String getMaPhieuDatBan() {
		return maPhieuDatBan;
	}
	public void setMaPhieuDatBan(String maPhieuDatBan) {
		this.maPhieuDatBan = maPhieuDatBan;
	}
	public Ban getMaBan() {
		return maBan;
	}
	public void setMaBan(Ban maBan) {
		this.maBan = maBan;
	}
	public String getTenKhach() {
		return tenKhach;
	}
	public void setTenKhach(String tenKhach) {
		this.tenKhach = tenKhach;
	}
	public String getSdt() {
		return sdt;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public int getSoLuongNguoi() {
		return soLuongNguoi;
	}
	public void setSoLuongNguoi(int soLuongNguoi) {
		this.soLuongNguoi = soLuongNguoi;
	}
	public LocalDateTime getThoiGianDen() {
		return thoiGianDen;
	}
	public void setThoiGianDen(LocalDateTime thoiGianDen) {
		this.thoiGianDen = thoiGianDen;
	}
	public double getTienCoc() {
		return tienCoc;
	}
	public void setTienCoc(double tienCoc) {
		this.tienCoc = tienCoc;
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
	public PhieuDatBan(String maPhieuDatBan, Ban maBan, String tenKhach, String sdt, int soLuongNguoi,
			LocalDateTime thoiGianDen, double tienCoc, String ghiChu, String trangThai) {
		super();
		this.maPhieuDatBan = maPhieuDatBan;
		this.maBan = maBan;
		this.tenKhach = tenKhach;
		this.sdt = sdt;
		this.soLuongNguoi = soLuongNguoi;
		this.thoiGianDen = thoiGianDen;
		this.tienCoc = tienCoc;
		this.ghiChu = ghiChu;
		this.trangThai = trangThai;
	}
	@Override
	public String toString() {
		return "PhieuDatBan [maPhieuDatBan=" + maPhieuDatBan + ", maBan=" + maBan + ", tenKhach=" + tenKhach + ", sdt="
				+ sdt + ", soLuongNguoi=" + soLuongNguoi + ", thoiGianDen=" + thoiGianDen + ", tienCoc=" + tienCoc
				+ ", ghiChu=" + ghiChu + ", trangThai=" + trangThai + "]";
	}
	public PhieuDatBan(String maPhieuDatBan) {
		super();
		this.maPhieuDatBan = maPhieuDatBan;
	}
	public PhieuDatBan() {
		// TODO Auto-generated constructor stub
	}
}
