package entity;

public class ChiTietHoaDon {
	private static final long serialVersionUID = 1L;
	private HoaDon maHD;
	private MonAn maMon;
	private int soLuong;
	private double donGia;
	private String ghiChu;
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
	public ChiTietHoaDon(HoaDon maHD, MonAn maMon, int soLuong, double donGia, String ghiChu) {
		super();
		this.maHD = maHD;
		this.maMon = maMon;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
	}
	@Override
	public String toString() {
		return "ChiTietHoaDon [maHD=" + maHD + ", maMon=" + maMon + ", soLuong=" + soLuong + ", donGia=" + donGia
				+ ", ghiChu=" + ghiChu + "]";
	}
	public double tinhThanhTien() {
	    return soLuong * donGia;
	}
	
	
}
