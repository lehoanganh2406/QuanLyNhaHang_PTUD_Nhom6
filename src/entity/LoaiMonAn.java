package entity;

public class LoaiMonAn {
	private static final long serialVersionUID = 1L;
	private String maLoaiMonAn;
	private String tenLoaiMonAn;
	public String getMaLoaiMonAn() {
		return maLoaiMonAn;
	}
	public void setMaLoaiMonAn(String maLoaiMonAn) {
		this.maLoaiMonAn = maLoaiMonAn;
	}
	public String getTenLoaiMonAn() {
		return tenLoaiMonAn;
	}
	public void setTenLoaiMonAn(String tenLoaiMonAn) {
		this.tenLoaiMonAn = tenLoaiMonAn;
	}
	public LoaiMonAn(String maLoaiMonAn, String tenLoaiMonAn) {
		super();
		this.maLoaiMonAn = maLoaiMonAn;
		this.tenLoaiMonAn = tenLoaiMonAn;
	}
	@Override
	public String toString() {
		return "LoaiMonAn [maLoaiMonAn=" + maLoaiMonAn + ", tenLoaiMonAn=" + tenLoaiMonAn + "]";
	}
	
}
