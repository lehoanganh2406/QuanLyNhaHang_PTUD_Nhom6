package entity;

public class ChiTietDatMon {
	private PhieuDatMon phieuDatMon;
	private MonAn mon;
	private int soLuong;
	private double donGia;
	private String ghiChu;
	public PhieuDatMon getPhieuDatMon() {
		return phieuDatMon;
	}
	public void setPhieuDatMon(PhieuDatMon phieuDatMon) {
		this.phieuDatMon = phieuDatMon;
	}
	public MonAn getMon() {
		return mon;
	}
	public void setMon(MonAn mon) {
		this.mon = mon;
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
	public ChiTietDatMon(PhieuDatMon phieuDatMon, MonAn mon, int soLuong, double donGia, String ghiChu) {
		super();
		this.phieuDatMon = phieuDatMon;
		this.mon = mon;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
	}
	public ChiTietDatMon() {
		// TODO Auto-generated constructor stub
	}
	
}
