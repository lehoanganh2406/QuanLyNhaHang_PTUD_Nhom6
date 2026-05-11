package entity;

import java.time.LocalDateTime;

public class PhieuDatBan {

	private String maPhieuDatBan;
	private String tenKhach;
	private String sdt;
	private int soLuongNguoi;
	private LocalDateTime thoiGianDen;
	private double tienCoc;
	private String ghiChu;
	private String trangThai;
	private String phuongThucThanhToanCoc;
	private LocalDateTime thoiGianDatPhieu;

	// thêm 3 cột mới
	private String phuongThucHoanTien;
	private String lyDoHuy;
	private double tienHoanTra;
	public String getPhuongThucThanhToanCoc() {
	    return phuongThucThanhToanCoc;
	}

	public void setPhuongThucThanhToanCoc(String phuongThucThanhToanCoc) {
	    this.phuongThucThanhToanCoc = phuongThucThanhToanCoc;
	}

	public LocalDateTime getThoiGianDatPhieu() {
	    return thoiGianDatPhieu;
	}

	public void setThoiGianDatPhieu(LocalDateTime thoiGianDatPhieu) {
	    this.thoiGianDatPhieu = thoiGianDatPhieu;
	}

	public String getMaPhieuDatBan() {
		return maPhieuDatBan;
	}

	public void setMaPhieuDatBan(String maPhieuDatBan) {
		this.maPhieuDatBan = maPhieuDatBan;
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

	public String getPhuongThucHoanTien() {
		return phuongThucHoanTien;
	}

	public void setPhuongThucHoanTien(String phuongThucHoanTien) {
		this.phuongThucHoanTien = phuongThucHoanTien;
	}

	public String getLyDoHuy() {
		return lyDoHuy;
	}

	public void setLyDoHuy(String lyDoHuy) {
		this.lyDoHuy = lyDoHuy;
	}

	public double getTienHoanTra() {
		return tienHoanTra;
	}

	public void setTienHoanTra(double tienHoanTra) {
		this.tienHoanTra = tienHoanTra;
	}

	
	public PhieuDatBan(String maPhieuDatBan, String tenKhach, String sdt, int soLuongNguoi, LocalDateTime thoiGianDen,
			double tienCoc, String ghiChu, String trangThai, String phuongThucThanhToanCoc,
			LocalDateTime thoiGianDatPhieu, String phuongThucHoanTien, String lyDoHuy, double tienHoanTra) {
		super();
		this.maPhieuDatBan = maPhieuDatBan;
		this.tenKhach = tenKhach;
		this.sdt = sdt;
		this.soLuongNguoi = soLuongNguoi;
		this.thoiGianDen = thoiGianDen;
		this.tienCoc = tienCoc;
		this.ghiChu = ghiChu;
		this.trangThai = trangThai;
		this.phuongThucThanhToanCoc = phuongThucThanhToanCoc;
		this.thoiGianDatPhieu = thoiGianDatPhieu;
		this.phuongThucHoanTien = phuongThucHoanTien;
		this.lyDoHuy = lyDoHuy;
		this.tienHoanTra = tienHoanTra;
	}

	public PhieuDatBan(String maPhieuDatBan) {
		super();
		this.maPhieuDatBan = maPhieuDatBan;
	}

	public PhieuDatBan() {
	}

}