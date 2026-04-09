package entity;

public class LoaiKhachHang {
	private static final long serialVersionUID = 1L;
	private String maLoaiKH;
	private String tenLoaiKH;
	public String getMaLoaiKH() {
		return maLoaiKH;
	}
	public void setMaLoaiKH(String maLoaiKH) {
		this.maLoaiKH = maLoaiKH;
	}
	public String getTenLoaiKH() {
		return tenLoaiKH;
	}
	public void setTenLoaiKH(String tenLoaiKH) {
		this.tenLoaiKH = tenLoaiKH;
	}
	public LoaiKhachHang(String maLoaiKH, String tenLoaiKH) {
		super();
		this.maLoaiKH = maLoaiKH;
		this.tenLoaiKH = tenLoaiKH;
	}
	@Override
	public String toString() {
		return "LoaiKhachHang [maLoaiKH=" + maLoaiKH + ", tenLoaiKH=" + tenLoaiKH + "]";
	}
	
}
