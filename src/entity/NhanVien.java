package entity;

import java.sql.Date;

public class NhanVien {
	private static final long serialVersionUID = 1L;
	private String maNV;
	private String hoTen;
	private String anhNhanVien;
	private Date ngaySinh;
	private boolean gioiTinh;
	private String cccd;
	private String email;
	private String sdt;
	private String chucVu;
	private String trangThai;
	public String getMaNV() {
		return maNV;
	}
	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
	public String getHoTen() {
		return hoTen;
	}
	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}
	public String getAnhNhanVien() {
		return anhNhanVien;
	}
	public void setAnhNhanVien(String anhNhanVien) {
		this.anhNhanVien = anhNhanVien;
	}
	public Date getNgaySinh() {
		return ngaySinh;
	}
	public void setNgaySinh(Date ngaySinh) {
		this.ngaySinh = ngaySinh;
	}
	public boolean isGioiTinh() {
		return gioiTinh;
	}
	public void setGioiTinh(boolean gioiTinh) {
		this.gioiTinh = gioiTinh;
	}
	public String getCccd() {
		return cccd;
	}
	public void setCccd(String cccd) {
		this.cccd = cccd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSdt() {
		return sdt;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public String getChucVu() {
		return chucVu;
	}
	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public NhanVien(String maNV, String hoTen, String anhNhanVien, Date ngaySinh, boolean gioiTinh, String cccd,
			String email, String sdt, String chucVu, String trangThai) {
		super();
		this.maNV = maNV;
		this.hoTen = hoTen;
		this.anhNhanVien = anhNhanVien;
		this.ngaySinh = ngaySinh;
		this.gioiTinh = gioiTinh;
		this.cccd = cccd;
		this.email = email;
		this.sdt = sdt;
		this.chucVu = chucVu;
		this.trangThai = trangThai;
	}
	@Override
	public String toString() {
		return "NhanVien [maNV=" + maNV + ", hoTen=" + hoTen + ", anhNhanVien=" + anhNhanVien + ", ngaySinh=" + ngaySinh
				+ ", gioiTinh=" + gioiTinh + ", cccd=" + cccd + ", email=" + email + ", sdt=" + sdt + ", chucVu="
				+ chucVu + ", trangThai=" + trangThai + "]";
	}
	public NhanVien(String maNV, String hoTen) {
		super();
		this.maNV = maNV;
		this.hoTen = hoTen;
	}
	
	
	
	
}
