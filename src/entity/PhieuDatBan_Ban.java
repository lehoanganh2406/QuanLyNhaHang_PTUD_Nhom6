package entity;

public class PhieuDatBan_Ban {
	private PhieuDatBan phieuDatBan;
	private Ban ban;
	public PhieuDatBan getPhieuDatBan() {
		return phieuDatBan;
	}
	public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
		this.phieuDatBan = phieuDatBan;
	}
	public Ban getBan() {
		return ban;
	}
	public void setBan(Ban ban) {
		this.ban = ban;
	}
	public PhieuDatBan_Ban(PhieuDatBan phieuDatBan, Ban ban) {
		super();
		this.phieuDatBan = phieuDatBan;
		this.ban = ban;
	}
	public PhieuDatBan_Ban() {
		// TODO Auto-generated constructor stub
	}
}
