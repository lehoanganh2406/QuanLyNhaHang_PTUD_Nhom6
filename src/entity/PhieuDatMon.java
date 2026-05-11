package entity;

import java.time.LocalDateTime;

public class PhieuDatMon {
	private String maPhieuDatMon;
	private PhieuDatBan phieuDatBan;
	private String hinhThucDatMon;
	private String ghiChu;
	private LocalDateTime thoiGianTao;
	public String getMaPhieuDatMon() {
		return maPhieuDatMon;
	}
	public void setMaPhieuDatMon(String maPhieuDatMon) {
		this.maPhieuDatMon = maPhieuDatMon;
	}
	public PhieuDatBan getPhieuDatBan() {
		return phieuDatBan;
	}
	public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
		this.phieuDatBan = phieuDatBan;
	}
	public String getHinhThucDatMon() {
		return hinhThucDatMon;
	}
	public void setHinhThucDatMon(String hinhThucDatMon) {
		this.hinhThucDatMon = hinhThucDatMon;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	public LocalDateTime getThoiGianTao() {
		return thoiGianTao;
	}
	public void setThoiGianTao(LocalDateTime thoiGianTao) {
		this.thoiGianTao = thoiGianTao;
	}
	public PhieuDatMon(String maPhieuDatMon, PhieuDatBan phieuDatBan, String hinhThucDatMon, String ghiChu,
			LocalDateTime thoiGianTao) {
		super();
		this.maPhieuDatMon = maPhieuDatMon;
		this.phieuDatBan = phieuDatBan;
		this.hinhThucDatMon = hinhThucDatMon;
		this.ghiChu = ghiChu;
		this.thoiGianTao = thoiGianTao;
	}
	public PhieuDatMon() {
		// TODO Auto-generated constructor stub
	}
}
