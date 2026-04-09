package entity;

import java.time.LocalDateTime;

public class KhuyenMai {
	private static final long serialVersionUID = 1L;
	private String maKM;
	private LoaiKhuyenMai maLoaiKM;
	private NhanVien maNV;
	private double giaTri;
	private String tenKhuyenMai;
	private LocalDateTime thoiGianBatDau;
	private LocalDateTime thoiGianKetThuc;
	private String doiTuongApDung;
	private double dieuKienApDung;
	private String ghiChu;
	private String trangThai;
	public String getMaKM() {
		return maKM;
	}
	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}
	public LoaiKhuyenMai getMaLoaiKM() {
		return maLoaiKM;
	}
	public void setMaLoaiKM(LoaiKhuyenMai maLoaiKM) {
		this.maLoaiKM = maLoaiKM;
	}
	public NhanVien getMaNV() {
		return maNV;
	}
	public void setMaNV(NhanVien maNV) {
		this.maNV = maNV;
	}
	public double getGiaTri() {
		return giaTri;
	}
	public void setGiaTri(double giaTri) {
		this.giaTri = giaTri;
	}
	public String getTenKhuyenMai() {
		return tenKhuyenMai;
	}
	public void setTenKhuyenMai(String tenKhuyenMai) {
		this.tenKhuyenMai = tenKhuyenMai;
	}
	public LocalDateTime getThoiGianBatDau() {
		return thoiGianBatDau;
	}
	public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
		this.thoiGianBatDau = thoiGianBatDau;
	}
	public LocalDateTime getThoiGianKetThuc() {
		return thoiGianKetThuc;
	}
	public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
		this.thoiGianKetThuc = thoiGianKetThuc;
	}
	public String getDoiTuongApDung() {
		return doiTuongApDung;
	}
	public void setDoiTuongApDung(String doiTuongApDung) {
		this.doiTuongApDung = doiTuongApDung;
	}
	public double getDieuKienApDung() {
		return dieuKienApDung;
	}
	public void setDieuKienApDung(double dieuKienApDung) {
		this.dieuKienApDung = dieuKienApDung;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public KhuyenMai(String maKM, LoaiKhuyenMai maLoaiKM, NhanVien maNV, double giaTri, String tenKhuyenMai,
			LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc, String doiTuongApDung, double dieuKienApDung,
			String ghiChu, String trangThai) {
		super();
		this.maKM = maKM;
		this.maLoaiKM = maLoaiKM;
		this.maNV = maNV;
		this.giaTri = giaTri;
		this.tenKhuyenMai = tenKhuyenMai;
		this.thoiGianBatDau = thoiGianBatDau;
		this.thoiGianKetThuc = thoiGianKetThuc;
		this.doiTuongApDung = doiTuongApDung;
		this.dieuKienApDung = dieuKienApDung;
		this.ghiChu = ghiChu;
		this.trangThai = trangThai;
	}
	@Override
	public String toString() {
		return "KhuyenMai [maKM=" + maKM + ", maLoaiKM=" + maLoaiKM + ", maNV=" + maNV + ", giaTri=" + giaTri
				+ ", tenKhuyenMai=" + tenKhuyenMai + ", thoiGianBatDau=" + thoiGianBatDau + ", thoiGianKetThuc="
				+ thoiGianKetThuc + ", doiTuongApDung=" + doiTuongApDung + ", dieuKienApDung=" + dieuKienApDung
				+ ", ghiChu=" + ghiChu + ", trangThai=" + trangThai + "]";
	}
	
}
