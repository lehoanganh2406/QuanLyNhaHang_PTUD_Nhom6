package entity;

public class KhachHang {
	private String maKH;
	private String tenKH;
	private String sdt;
	private LoaiKhachHang maLoaiKH;
	private int diemTichLuy;
	public String getMaKH() {
		return maKH;
	}
	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}
	public String getTenKH() {
		return tenKH;
	}
	public void setTenKH(String tenKH) {
		this.tenKH = tenKH;
	}
	public String getSdt() {
		return sdt;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public LoaiKhachHang getMaLoaiKH() {
		return maLoaiKH;
	}
	public void setMaLoaiKH(LoaiKhachHang maLoaiKH) {
		this.maLoaiKH = maLoaiKH;
	}
	public int getDiemTichLuy() {
		return diemTichLuy;
	}
	public void setDiemTichLuy(int diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}
	public KhachHang(String maKH, String tenKH, String sdt, LoaiKhachHang maLoaiKH, int diemTichLuy) {
		super();
		this.maKH = maKH;
		this.tenKH = tenKH;
		this.sdt = sdt;
		this.maLoaiKH = maLoaiKH;
		this.diemTichLuy = diemTichLuy;
	}
	@Override
	public String toString() {
		return "KhachHang [maKH=" + maKH + ", tenKH=" + tenKH + ", sdt=" + sdt + ", maLoaiKH=" + maLoaiKH
				+ ", diemTichLuy=" + diemTichLuy + "]";
	}
	
	
}
