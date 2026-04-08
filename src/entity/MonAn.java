package entity;

public class MonAn {
	private static final long serialVersionUID = 1L;
	private String maMon;
	private LoaiMonAn maLoaiMonAn;
	private String tenMon;
	private String anhMon;
	private double donGia;
	private String moTa;
	private boolean trangThai;
	public String getMaMon() {
		return maMon;
	}
	public void setMaMon(String maMon) {
		this.maMon = maMon;
	}
	public LoaiMonAn getMaLoaiMonAn() {
		return maLoaiMonAn;
	}
	public void setMaLoaiMonAn(LoaiMonAn maLoaiMonAn) {
		this.maLoaiMonAn = maLoaiMonAn;
	}
	public String getTenMon() {
		return tenMon;
	}
	public void setTenMon(String tenMon) {
		this.tenMon = tenMon;
	}
	public String getAnhMon() {
		return anhMon;
	}
	public void setAnhMon(String anhMon) {
		this.anhMon = anhMon;
	}
	public double getDonGia() {
		return donGia;
	}
	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}
	public String getMoTa() {
		return moTa;
	}
	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}
	public boolean isTrangThai() {
		return trangThai;
	}
	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}
	public MonAn(String maMon, LoaiMonAn maLoaiMonAn, String tenMon, String anhMon, double donGia, String moTa,
			boolean trangThai) {
		super();
		this.maMon = maMon;
		this.maLoaiMonAn = maLoaiMonAn;
		this.tenMon = tenMon;
		this.anhMon = anhMon;
		this.donGia = donGia;
		this.moTa = moTa;
		this.trangThai = trangThai;
	}
	@Override
	public String toString() {
		return "MonAn [maMon=" + maMon + ", maLoaiMonAn=" + maLoaiMonAn + ", tenMon=" + tenMon + ", anhMon=" + anhMon
				+ ", donGia=" + donGia + ", moTa=" + moTa + ", trangThai=" + trangThai + "]";
	}
	public MonAn() {
		// TODO Auto-generated constructor stub
	}
	
}
