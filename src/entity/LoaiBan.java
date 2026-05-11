package entity;

public class LoaiBan {
	
	private String maLoaiBan;
	private String tenLoaiBan;
	public String getMaLoaiBan() {
		return maLoaiBan;
	}
	public void setMaLoaiBan(String maLoaiBan) {
		this.maLoaiBan = maLoaiBan;
	}
	public String getTenLoaiBan() {
		return tenLoaiBan;
	}
	public void setTenLoaiBan(String tenLoaiBan) {
		this.tenLoaiBan = tenLoaiBan;
	}
	public LoaiBan(String maLoaiBan, String tenLoaiBan) {
		super();
		this.maLoaiBan = maLoaiBan;
		this.tenLoaiBan = tenLoaiBan;
	}
	@Override
	public String toString() {
		return "LoaiBan [maLoaiBan=" + maLoaiBan + ", tenLoaiBan=" + tenLoaiBan + "]";
	}
	public LoaiBan() {
		// TODO Auto-generated constructor stub
	}
}
