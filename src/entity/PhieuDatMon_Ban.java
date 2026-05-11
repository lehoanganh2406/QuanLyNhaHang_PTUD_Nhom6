package entity;

public class PhieuDatMon_Ban {
	private PhieuDatMon phieuDatMon;
	private Ban ban;
	public PhieuDatMon getPhieuDatMon() {
		return phieuDatMon;
	}
	public void setPhieuDatMon(PhieuDatMon phieuDatMon) {
		this.phieuDatMon = phieuDatMon;
	}
	public Ban getBan() {
		return ban;
	}
	public void setBan(Ban ban) {
		this.ban = ban;
	}
	public PhieuDatMon_Ban(PhieuDatMon phieuDatMon, Ban ban) {
		super();
		this.phieuDatMon = phieuDatMon;
		this.ban = ban;
	}
	public PhieuDatMon_Ban() {
		// TODO Auto-generated constructor stub
	}
}
