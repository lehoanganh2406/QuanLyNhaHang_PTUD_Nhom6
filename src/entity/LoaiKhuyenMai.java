package entity;

public class LoaiKhuyenMai {
	private static final long serialVersionUID = 1L;
	private String maLoaiKM;
	private String tenLoaiKM;
	public String getMaLoaiKM() {
		return maLoaiKM;
	}
	public void setMaLoaiKM(String maLoaiKM) {
		this.maLoaiKM = maLoaiKM;
	}
	public String getTenLoaiKM() {
		return tenLoaiKM;
	}
	public void setTenLoaiKM(String tenLoaiKM) {
		this.tenLoaiKM = tenLoaiKM;
	}
	public LoaiKhuyenMai(String maLoaiKM, String tenLoaiKM) {
		super();
		this.maLoaiKM = maLoaiKM;
		this.tenLoaiKM = tenLoaiKM;
	}
	@Override
	public String toString() {
		return "LoaiKhuyenMai [maLoaiKM=" + maLoaiKM + ", tenLoaiKM=" + tenLoaiKM + "]";
	}
	
	
}
