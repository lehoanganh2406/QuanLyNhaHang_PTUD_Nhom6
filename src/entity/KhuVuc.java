package entity;

public class KhuVuc {
	private static final long serialVersionUID = 1L;
	private String maKhuvuc;
	private String tenKhuVuc;
	private int soLuongBan;
	private String trangThai;
	private String kyHieu;
	public String getMaKhuvuc() {
		return maKhuvuc;
	}
	public void setMaKhuvuc(String maKhuvuc) {
		this.maKhuvuc = maKhuvuc;
	}
	public String getTenKhuVuc() {
		return tenKhuVuc;
	}
	public void setTenKhuVuc(String tenKhuVuc) {
		this.tenKhuVuc = tenKhuVuc;
	}
	public int getSoLuongBan() {
		return soLuongBan;
	}
	public void setSoLuongBan(int soLuongBan) {
		this.soLuongBan = soLuongBan;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public String getKyHieu() {
		return kyHieu;
	}
	public void setKyHieu(String kyHieu) {
		this.kyHieu = kyHieu;
	}
	public KhuVuc(String maKhuvuc, String tenKhuVuc, int soLuongBan, String trangThai, String kyHieu) {
		super();
		this.maKhuvuc = maKhuvuc;
		this.tenKhuVuc = tenKhuVuc;
		this.soLuongBan = soLuongBan;
		this.trangThai = trangThai;
		this.kyHieu = kyHieu;
	}
	@Override
	public String toString() {
		return "KhuVuc [maKhuvuc=" + maKhuvuc + ", tenKhuVuc=" + tenKhuVuc + ", soLuongBan=" + soLuongBan
				+ ", trangThai=" + trangThai + ", kyHieu=" + kyHieu + "]";
	}
	
}
