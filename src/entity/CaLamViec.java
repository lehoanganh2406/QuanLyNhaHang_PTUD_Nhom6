package entity;

import java.time.LocalDateTime;

public class CaLamViec {
	private static final long serialVersionUID = 1L;
	private String maCa;
	private String tenCa;
	private LocalDateTime thoiGianMoCa;
	private LocalDateTime thoiGianDongCa;
	private TaiKhoan maTaiKhoan;
	public String getMaCa() {
		return maCa;
	}
	public void setMaCa(String maCa) {
		this.maCa = maCa;
	}
	public String getTenCa() {
		return tenCa;
	}
	public void setTenCa(String tenCa) {
		this.tenCa = tenCa;
	}
	public LocalDateTime getThoiGianMoCa() {
		return thoiGianMoCa;
	}
	public void setThoiGianMoCa(LocalDateTime thoiGianMoCa) {
		this.thoiGianMoCa = thoiGianMoCa;
	}
	public LocalDateTime getThoiGianDongCa() {
		return thoiGianDongCa;
	}
	public void setThoiGianDongCa(LocalDateTime thoiGianDongCa) {
		this.thoiGianDongCa = thoiGianDongCa;
	}
	public TaiKhoan getMaTaiKhoan() {
		return maTaiKhoan;
	}
	public void setMaTaiKhoan(TaiKhoan maTaiKhoan) {
		this.maTaiKhoan = maTaiKhoan;
	}
	public CaLamViec(String maCa, String tenCa, LocalDateTime thoiGianMoCa, LocalDateTime thoiGianDongCa,
			TaiKhoan maTaiKhoan) {
		super();
		this.maCa = maCa;
		this.tenCa = tenCa;
		this.thoiGianMoCa = thoiGianMoCa;
		this.thoiGianDongCa = thoiGianDongCa;
		this.maTaiKhoan = maTaiKhoan;
	}
	@Override
	public String toString() {
		return "CaLamViec [maCa=" + maCa + ", tenCa=" + tenCa + ", thoiGianMoCa=" + thoiGianMoCa + ", thoiGianDongCa="
				+ thoiGianDongCa + ", maTaiKhoan=" + maTaiKhoan + "]";
	}
	
}
