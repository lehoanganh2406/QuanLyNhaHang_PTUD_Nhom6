package entity;

public class Ban {
	
	private String maBan;
	private KhuVuc maKhuVuc;
	private LoaiBan maLoaiBan;
	private String tenBan;
	private String ghiChu;
	private int soChoNgoi;
	private String trangThai;
	public String getMaBan() {
		return maBan;
	}
	public void setMaBan(String maBan) {
		this.maBan = maBan;
	}
	public KhuVuc getMaKhuVuc() {
		return maKhuVuc;
	}
	public void setMaKhuVuc(KhuVuc maKhuVuc) {
		this.maKhuVuc = maKhuVuc;
	}
	public LoaiBan getMaLoaiBan() {
		return maLoaiBan;
	}
	public void setMaLoaiBan(LoaiBan maLoaiBan) {
		this.maLoaiBan = maLoaiBan;
	}
	public String getTenBan() {
		return tenBan;
	}
	public void setTenBan(String tenBan) {
		this.tenBan = tenBan;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	public int getSoChoNgoi() {
		return soChoNgoi;
	}
	public void setSoChoNgoi(int soChoNgoi) {
		this.soChoNgoi = soChoNgoi;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public Ban(String maBan, KhuVuc maKhuVuc, LoaiBan maLoaiBan, String tenBan, String ghiChu, int soChoNgoi,
			String trangThai) {
		super();
		this.maBan = maBan;
		this.maKhuVuc = maKhuVuc;
		this.maLoaiBan = maLoaiBan;
		this.tenBan = tenBan;
		this.ghiChu = ghiChu;
		this.soChoNgoi = soChoNgoi;
		this.trangThai = trangThai;
	}
	public Ban(String maBan) {
	    this.maBan = maBan;
	}
	@Override
	public String toString() {
		return "Ban [maBan=" + maBan + ", maKhuVuc=" + maKhuVuc + ", maLoaiBan=" + maLoaiBan + ", tenBan=" + tenBan
				+ ", ghiChu=" + ghiChu + ", soChoNgoi=" + soChoNgoi + ", trangThai=" + trangThai + "]";
	}
	public Ban() {
		// TODO Auto-generated constructor stub
	}
}
