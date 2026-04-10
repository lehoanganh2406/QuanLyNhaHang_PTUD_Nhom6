package entity;

import java.time.LocalDateTime;

public class CaLamViec {
	private static final long serialVersionUID = 1L;
	private String maCa;
	private String tenCa;
	private LocalDateTime thoiGianMoCa;
	private LocalDateTime thoiGianDongCa;
	private TaiKhoan maTaiKhoan;
	private double tienMoCa;
	private double tienMatCuoiCa;
	private double tienChuyenKhoanCuoiCa;
	private double tienVisaCuoiCa;
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
	public double getTienMoCa() {
		return tienMoCa;
	}
	public void setTienMoCa(double tienMoCa) {
		this.tienMoCa = tienMoCa;
	}
	public double getTienMatCuoiCa() {
		return tienMatCuoiCa;
	}
	public void setTienMatCuoiCa(double tienMatCuoiCa) {
		this.tienMatCuoiCa = tienMatCuoiCa;
	}
	public double getTienChuyenKhoanCuoiCa() {
		return tienChuyenKhoanCuoiCa;
	}
	public void setTienChuyenKhoanCuoiCa(double tienChuyenKhoanCuoiCa) {
		this.tienChuyenKhoanCuoiCa = tienChuyenKhoanCuoiCa;
	}
	public double getTienVisaCuoiCa() {
		return tienVisaCuoiCa;
	}
	public void setTienVisaCuoiCa(double tienVisaCuoiCa) {
		this.tienVisaCuoiCa = tienVisaCuoiCa;
	}
	public CaLamViec(String maCa, String tenCa, LocalDateTime thoiGianMoCa, LocalDateTime thoiGianDongCa,
			TaiKhoan maTaiKhoan, double tienMoCa, double tienMatCuoiCa, double tienChuyenKhoanCuoiCa,
			double tienVisaCuoiCa) {
		super();
		this.maCa = maCa;
		this.tenCa = tenCa;
		this.thoiGianMoCa = thoiGianMoCa;
		this.thoiGianDongCa = thoiGianDongCa;
		this.maTaiKhoan = maTaiKhoan;
		this.tienMoCa = tienMoCa;
		this.tienMatCuoiCa = tienMatCuoiCa;
		this.tienChuyenKhoanCuoiCa = tienChuyenKhoanCuoiCa;
		this.tienVisaCuoiCa = tienVisaCuoiCa;
	}
	@Override
	public String toString() {
		return "CaLamViec [maCa=" + maCa + ", tenCa=" + tenCa + ", thoiGianMoCa=" + thoiGianMoCa + ", thoiGianDongCa="
				+ thoiGianDongCa + ", maTaiKhoan=" + maTaiKhoan + ", tienMoCa=" + tienMoCa + ", tienMatCuoiCa="
				+ tienMatCuoiCa + ", tienChuyenKhoanCuoiCa=" + tienChuyenKhoanCuoiCa + ", tienVisaCuoiCa="
				+ tienVisaCuoiCa + "]";
	}
	public CaLamViec() {
		// TODO Auto-generated constructor stub
	}
}
