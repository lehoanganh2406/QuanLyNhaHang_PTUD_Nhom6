package entity;

import java.time.LocalDateTime;

public class HoaDon {
    private static final long serialVersionUID = 1L;

    private String maHD;
    private LocalDateTime thoiGianVao;
    private LocalDateTime thoiGianRa;
    private PhieuDatBan maPhieuDatBan;
    private KhachHang maKH;
    private KhuyenMai maKM;
    private Ban maBan;
    private NhanVien maNV;
    private double tongTien;
    private double tienKhachTra;
    private double thueVAT;
    private double tienThua;
    private String trangThai;
    private String lyDoHuy;
    private String phuongThucThanhToan;

    public HoaDon() {
    }

    public HoaDon(String maHD) {
        this.maHD = maHD;
    }

    

    public HoaDon(String maHD, LocalDateTime thoiGianVao, LocalDateTime thoiGianRa, PhieuDatBan maPhieuDatBan,
			KhachHang maKH, KhuyenMai maKM, Ban maBan, NhanVien maNV, double tongTien, double tienKhachTra,
			double thueVAT, double tienThua, String trangThai, String lyDoHuy, String phuongThucThanhToan) {
		super();
		this.maHD = maHD;
		this.thoiGianVao = thoiGianVao;
		this.thoiGianRa = thoiGianRa;
		this.maPhieuDatBan = maPhieuDatBan;
		this.maKH = maKH;
		this.maKM = maKM;
		this.maBan = maBan;
		this.maNV = maNV;
		this.tongTien = tongTien;
		this.tienKhachTra = tienKhachTra;
		this.thueVAT = thueVAT;
		this.tienThua = tienThua;
		this.trangThai = trangThai;
		this.lyDoHuy = lyDoHuy;
		this.phuongThucThanhToan = phuongThucThanhToan;
	}

	public String getPhuongThucThanhToan() {
		return phuongThucThanhToan;
	}

	public void setPhuongThucThanhToan(String phuongThucThanhToan) {
		this.phuongThucThanhToan = phuongThucThanhToan;
	}

	public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public LocalDateTime getThoiGianVao() {
        return thoiGianVao;
    }

    public void setThoiGianVao(LocalDateTime thoiGianVao) {
        this.thoiGianVao = thoiGianVao;
    }

    public LocalDateTime getThoiGianRa() {
        return thoiGianRa;
    }

    public void setThoiGianRa(LocalDateTime thoiGianRa) {
        this.thoiGianRa = thoiGianRa;
    }

    public PhieuDatBan getMaPhieuDatBan() {
        return maPhieuDatBan;
    }

    public void setMaPhieuDatBan(PhieuDatBan maPhieuDatBan) {
        this.maPhieuDatBan = maPhieuDatBan;
    }

    public KhachHang getMaKH() {
        return maKH;
    }

    public void setMaKH(KhachHang maKH) {
        this.maKH = maKH;
    }

    public KhuyenMai getMaKM() {
        return maKM;
    }

    public void setMaKM(KhuyenMai maKM) {
        this.maKM = maKM;
    }

    public Ban getMaBan() {
        return maBan;
    }

    public void setMaBan(Ban maBan) {
        this.maBan = maBan;
    }

    public NhanVien getMaNV() {
        return maNV;
    }

    public void setMaNV(NhanVien maNV) {
        this.maNV = maNV;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public double getTienKhachTra() {
        return tienKhachTra;
    }

    public void setTienKhachTra(double tienKhachTra) {
        this.tienKhachTra = tienKhachTra;
    }

    public double getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(double thueVAT) {
        this.thueVAT = thueVAT;
    }

    public double getTienThua() {
        return tienThua;
    }

    public void setTienThua(double tienThua) {
        this.tienThua = tienThua;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

	@Override
	public String toString() {
		return "HoaDon [maHD=" + maHD + ", thoiGianVao=" + thoiGianVao + ", thoiGianRa=" + thoiGianRa
				+ ", maPhieuDatBan=" + maPhieuDatBan + ", maKH=" + maKH + ", maKM=" + maKM + ", maBan=" + maBan
				+ ", maNV=" + maNV + ", tongTien=" + tongTien + ", tienKhachTra=" + tienKhachTra + ", thueVAT="
				+ thueVAT + ", tienThua=" + tienThua + ", trangThai=" + trangThai + ", lyDoHuy=" + lyDoHuy
				+ ", phuongThucThanhToan=" + phuongThucThanhToan + "]";
	}

    
}