package entity;

public class LoaiKhuyenMai {
	
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

	
	
}
