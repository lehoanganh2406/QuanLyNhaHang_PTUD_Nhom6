package entity;

public class PhieuDatMon {
	private static final long serialVersionUID = 1L;
	private PhieuDatBan maPhieuDatBan;
	private MonAn maMon;
	private int soLuong;
	private double donGia;
	private String ghiChu;
	public PhieuDatBan getMaPhieuDatBan() {
		return maPhieuDatBan;
	}
	public void setMaPhieuDatBan(PhieuDatBan maPhieuDatBan) {
		this.maPhieuDatBan = maPhieuDatBan;
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
	public PhieuDatMon(PhieuDatBan maPhieuDatBan, MonAn maMon, int soLuong, double donGia, String ghiChu) {
		super();
		this.maPhieuDatBan = maPhieuDatBan;
		this.maMon = maMon;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
	}
	@Override
	public String toString() {
		return "PhieuDatMon [maPhieuDatBan=" + maPhieuDatBan + ", maMon=" + maMon + ", soLuong=" + soLuong + ", donGia="
				+ donGia + ", ghiChu=" + ghiChu + "]";
	}
	
}
