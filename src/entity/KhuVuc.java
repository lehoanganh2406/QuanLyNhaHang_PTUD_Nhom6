package entity;

public class KhuVuc {
	private static final long serialVersionUID = 1L;
	private String maKhuVuc;
	private String tenKhuVuc;
	private int soLuongBan;
	private String trangThai;
	private String kyHieu;
	public String getMaKhuVuc() {
		return maKhuVuc;
	}
	public void setMaKhuVuc(String maKhuVuc) {
		this.maKhuVuc = maKhuVuc;
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
	public KhuVuc(String maKhuVuc, String tenKhuVuc, int soLuongBan, String trangThai, String kyHieu) {
		super();
		this.maKhuVuc = maKhuVuc;
		this.tenKhuVuc = tenKhuVuc;
		this.soLuongBan = soLuongBan;
		this.trangThai = trangThai;
		this.kyHieu = kyHieu;
	}
	@Override
	public String toString() {
		return "KhuVuc [maKhuVuc=" + maKhuVuc + ", tenKhuVuc=" + tenKhuVuc + ", soLuongBan=" + soLuongBan
				+ ", trangThai=" + trangThai + ", kyHieu=" + kyHieu + "]";
	}
	public KhuVuc() {
		// TODO Auto-generated constructor stub
	}
}
