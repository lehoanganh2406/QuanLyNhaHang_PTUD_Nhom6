CREATE DATABASE QL_NhaHang;
GO

USE QL_NhaHang;
GO

CREATE TABLE LoaiMonAn (
    maLoaiMonAn VARCHAR(20) NOT NULL PRIMARY KEY,
    tenLoaiMonAn NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE LoaiKhuyenMai (
    maLoaiKM VARCHAR(20) NOT NULL PRIMARY KEY,
    tenLoaiKM NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE LoaiKhachHang (
    maLoaiKH VARCHAR(20) NOT NULL PRIMARY KEY,
    tenLoaiKH NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE LoaiBan (
    maLoaiBan VARCHAR(20) NOT NULL PRIMARY KEY,
    tenLoaiBan NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE KhuVuc (
    maKhuVuc VARCHAR(20) NOT NULL PRIMARY KEY,
    tenKhuVuc NVARCHAR(100) NOT NULL,
    soLuongBan INT NOT NULL DEFAULT 0,
    trangThai NVARCHAR(50) NULL,
    kyHieu NVARCHAR(20) NULL,
    CONSTRAINT CK_KhuVuc_SoLuongBan CHECK (soLuongBan >= 0)
);
GO

CREATE TABLE MonAn (
    maMon VARCHAR(20) NOT NULL PRIMARY KEY,
    maLoaiMonAn VARCHAR(20) NOT NULL,
    tenMon NVARCHAR(100) NOT NULL,
    anhMon NVARCHAR(255) NULL,
    donGia DECIMAL(18,2) NOT NULL DEFAULT 0,
    moTa NVARCHAR(255) NULL,
    trangThai BIT NOT NULL DEFAULT 1, -- 1: còn bán, 0: ngừng bán
    CONSTRAINT FK_MonAn_LoaiMonAn FOREIGN KEY (maLoaiMonAn) REFERENCES LoaiMonAn(maLoaiMonAn),
    CONSTRAINT CK_MonAn_DonGia CHECK (donGia >= 0)
);
GO

CREATE TABLE NhanVien (
    maNV VARCHAR(20) NOT NULL PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    anhNhanVien NVARCHAR(255) NULL,
    ngaySinh DATE NULL,
    gioiTinh BIT NULL,
    cccd VARCHAR(20) NULL UNIQUE,
    email VARCHAR(100) NULL UNIQUE,
    sdt VARCHAR(15) NULL UNIQUE,
    chucVu NVARCHAR(100) NULL,
    trangThai NVARCHAR(50) NULL
);

CREATE TABLE TaiKhoan (
    maTaiKhoan VARCHAR(20) NOT NULL PRIMARY KEY,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(255) NOT NULL,
    phanQuyen NVARCHAR(50) NOT NULL,
    trangThai BIT NOT NULL DEFAULT 1, -- 1: còn hoạt động, 0: ngưng hoạt động
    maNV VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);
GO

CREATE TABLE CaLamViec (
    maCa VARCHAR(20) NOT NULL PRIMARY KEY,
    tenCa NVARCHAR(100) NOT NULL,
    thoiGianMoCa DATETIME NOT NULL,
    thoiGianDongCa DATETIME NOT NULL,
    maTaiKhoan VARCHAR(20) NOT NULL,
    CONSTRAINT FK_CaLamViec_TaiKhoan FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan),
    CONSTRAINT CK_CaLamViec_ThoiGian CHECK (thoiGianDongCa > thoiGianMoCa)
);
GO

CREATE TABLE KhachHang (
    maKH VARCHAR(20) NOT NULL PRIMARY KEY,
    tenKH NVARCHAR(100) NOT NULL,
    sdt VARCHAR(15) NOT NULL UNIQUE,
    maLoaiKH VARCHAR(20) NOT NULL,
    diemTichLuy INT NOT NULL DEFAULT 0,
    CONSTRAINT FK_KhachHang_LoaiKhachHang FOREIGN KEY (maLoaiKH) REFERENCES LoaiKhachHang(maLoaiKH),
    CONSTRAINT CK_KhachHang_DiemTichLuy CHECK (diemTichLuy >= 0)
);
GO

CREATE TABLE KhuyenMai (
    maKM VARCHAR(20) NOT NULL PRIMARY KEY,
    maLoaiKM VARCHAR(20) NOT NULL,
    maNV VARCHAR(20) NOT NULL,
    giaTri DECIMAL(18,2) NOT NULL DEFAULT 0,
    tenKhuyenMai NVARCHAR(100) NOT NULL,
    thoiGianBatDau DATETIME NOT NULL,
    thoiGianKetThuc DATETIME NOT NULL,
    doiTuongApDung NVARCHAR(100) NULL,
    dieuKienApDung DECIMAL(18,2) NULL,
    ghiChu NVARCHAR(255) NULL,
    trangThai NVARCHAR(50) NULL,
    CONSTRAINT FK_KhuyenMai_LoaiKhuyenMai FOREIGN KEY (maLoaiKM) REFERENCES LoaiKhuyenMai(maLoaiKM),
    CONSTRAINT FK_KhuyenMai_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT CK_KhuyenMai_GiaTri CHECK (giaTri >= 0),
    CONSTRAINT CK_KhuyenMai_DieuKien CHECK (dieuKienApDung IS NULL OR dieuKienApDung >= 0),
    CONSTRAINT CK_KhuyenMai_ThoiGian CHECK (thoiGianKetThuc >= thoiGianBatDau)
);
GO

CREATE TABLE Ban (
    maBan VARCHAR(20) NOT NULL PRIMARY KEY,
    maKhuVuc VARCHAR(20) NOT NULL,
    maLoaiBan VARCHAR(20) NOT NULL,
    tenBan NVARCHAR(100) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    soChoNgoi INT NOT NULL,
    trangThai NVARCHAR(50) NULL,
    CONSTRAINT FK_Ban_KhuVuc FOREIGN KEY (maKhuVuc) REFERENCES KhuVuc(maKhuVuc),
    CONSTRAINT FK_Ban_LoaiBan FOREIGN KEY (maLoaiBan) REFERENCES LoaiBan(maLoaiBan),
    CONSTRAINT CK_Ban_SoChoNgoi CHECK (soChoNgoi > 0)
);
GO

CREATE TABLE PhieuDatBan (
    maPhieuDatBan VARCHAR(20) NOT NULL PRIMARY KEY,
    maBan VARCHAR(20) NOT NULL,
    tenKhach NVARCHAR(100) NOT NULL,
    sdt VARCHAR(15) NOT NULL,
    soLuongNguoi INT NOT NULL,
    thoiGianDen DATETIME NOT NULL,
    tienCoc DECIMAL(18,2) NOT NULL DEFAULT 0,
    ghiChu NVARCHAR(255) NULL,
    trangThai NVARCHAR(50) NULL,
    CONSTRAINT FK_PhieuDatBan_Ban FOREIGN KEY (maBan) REFERENCES Ban(maBan),
    CONSTRAINT CK_PhieuDatBan_SoLuongNguoi CHECK (soLuongNguoi > 0),
    CONSTRAINT CK_PhieuDatBan_TienCoc CHECK (tienCoc >= 0)
);
GO

CREATE TABLE PhieuDatMon (
    maPhieuDatBan VARCHAR(20) NOT NULL,
    maMon VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    CONSTRAINT PK_PhieuDatMon PRIMARY KEY (maPhieuDatBan, maMon),
    CONSTRAINT FK_PhieuDatMon_PhieuDatBan FOREIGN KEY (maPhieuDatBan) REFERENCES PhieuDatBan(maPhieuDatBan),
    CONSTRAINT FK_PhieuDatMon_MonAn FOREIGN KEY (maMon) REFERENCES MonAn(maMon),
    CONSTRAINT CK_PhieuDatMon_SoLuong CHECK (soLuong > 0),
    CONSTRAINT CK_PhieuDatMon_DonGia CHECK (donGia >= 0)
);
GO

CREATE TABLE HoaDon (
    maHD VARCHAR(20) NOT NULL PRIMARY KEY,
    thoiGianVao DATETIME NOT NULL,
    thoiGianRa DATETIME NULL,
    maPhieuDatBan VARCHAR(20) NULL,
    maKH VARCHAR(20) NULL,
    maKM VARCHAR(20) NULL,
    maBan VARCHAR(20) NOT NULL,
    maNV VARCHAR(20) NOT NULL,
    tienKhachTra DECIMAL(18,2) NOT NULL DEFAULT 0,
    thueVAT DECIMAL(18,2) NOT NULL DEFAULT 0,
    tienThua DECIMAL(18,2) NOT NULL DEFAULT 0,
    trangThai NVARCHAR(255) NULL,
    CONSTRAINT FK_HoaDon_PhieuDatBan FOREIGN KEY (maPhieuDatBan) REFERENCES PhieuDatBan(maPhieuDatBan),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM),
    CONSTRAINT FK_HoaDon_Ban FOREIGN KEY (maBan) REFERENCES Ban(maBan),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT CK_HoaDon_TienKhachTra CHECK (tienKhachTra >= 0),
    CONSTRAINT CK_HoaDon_ThueVAT CHECK (thueVAT >= 0),
    CONSTRAINT CK_HoaDon_TienThua CHECK (tienThua >= 0),
    CONSTRAINT CK_HoaDon_ThoiGian CHECK (thoiGianRa IS NULL OR thoiGianRa >= thoiGianVao)
);
GO

CREATE TABLE ChiTietHoaDon (
    maHD VARCHAR(20) NOT NULL,
    maMon VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    thanhTien AS (soLuong * donGia),
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHD, maMon),
    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    CONSTRAINT FK_ChiTietHoaDon_MonAn FOREIGN KEY (maMon) REFERENCES MonAn(maMon),
    CONSTRAINT CK_ChiTietHoaDon_SoLuong CHECK (soLuong > 0),
    CONSTRAINT CK_ChiTietHoaDon_DonGia CHECK (donGia >= 0)
);
GO

/*==========================================================
2. SEQUENCE TỰ TĂNG MÃ
==========================================================*/
CREATE SEQUENCE seq_LoaiMonAn     AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_MonAn         AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_LoaiKhuyenMai AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_KhuyenMai     AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_NhanVien      AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_TaiKhoan      AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_CaLamViec     AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_KhachHang     AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_LoaiKhachHang AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_HoaDon        AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_PhieuDatBan   AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_KhuVuc        AS INT START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_LoaiBan       AS INT START WITH 1 INCREMENT BY 1;
GO

/*==========================================================
3. DEFAULT TỰ SINH MÃ
==========================================================*/
ALTER TABLE LoaiMonAn
ADD CONSTRAINT DF_LoaiMonAn_maLoaiMonAn
DEFAULT ('LM' + RIGHT('00' + CAST(NEXT VALUE FOR seq_LoaiMonAn AS VARCHAR(10)), 2))
FOR maLoaiMonAn;
GO

ALTER TABLE MonAn
ADD CONSTRAINT DF_MonAn_maMon
DEFAULT ('MM' + RIGHT('000' + CAST(NEXT VALUE FOR seq_MonAn AS VARCHAR(10)), 3))
FOR maMon;
GO

ALTER TABLE LoaiKhuyenMai
ADD CONSTRAINT DF_LoaiKhuyenMai_maLoaiKM
DEFAULT ('LKM' + RIGHT('00' + CAST(NEXT VALUE FOR seq_LoaiKhuyenMai AS VARCHAR(10)), 2))
FOR maLoaiKM;
GO

ALTER TABLE KhuyenMai
ADD CONSTRAINT DF_KhuyenMai_maKM
DEFAULT ('KM' + RIGHT('000' + CAST(NEXT VALUE FOR seq_KhuyenMai AS VARCHAR(10)), 3))
FOR maKM;
GO

ALTER TABLE NhanVien
ADD CONSTRAINT DF_NhanVien_maNV
DEFAULT ('NV' + RIGHT('000' + CAST(NEXT VALUE FOR seq_NhanVien AS VARCHAR(10)), 3))
FOR maNV;
GO

ALTER TABLE TaiKhoan
ADD CONSTRAINT DF_TaiKhoan_maTaiKhoan
DEFAULT ('TK' + RIGHT('00' + CAST(NEXT VALUE FOR seq_TaiKhoan AS VARCHAR(10)), 2))
FOR maTaiKhoan;
GO

ALTER TABLE CaLamViec
ADD CONSTRAINT DF_CaLamViec_maCa
DEFAULT ('CL' + RIGHT('000' + CAST(NEXT VALUE FOR seq_CaLamViec AS VARCHAR(10)), 3))
FOR maCa;
GO

ALTER TABLE KhachHang
ADD CONSTRAINT DF_KhachHang_maKH
DEFAULT ('KH' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_KhachHang AS VARCHAR(10)), 5))
FOR maKH;
GO

ALTER TABLE LoaiKhachHang
ADD CONSTRAINT DF_LoaiKhachHang_maLoaiKH
DEFAULT ('LKH' + RIGHT('00' + CAST(NEXT VALUE FOR seq_LoaiKhachHang AS VARCHAR(10)), 2))
FOR maLoaiKH;
GO

ALTER TABLE HoaDon
ADD CONSTRAINT DF_HoaDon_maHD
DEFAULT ('HD' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_HoaDon AS VARCHAR(10)), 5))
FOR maHD;
GO

ALTER TABLE PhieuDatBan
ADD CONSTRAINT DF_PhieuDatBan_maPhieuDatBan
DEFAULT ('PDB' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_PhieuDatBan AS VARCHAR(10)), 5))
FOR maPhieuDatBan;
GO

ALTER TABLE KhuVuc
ADD CONSTRAINT DF_KhuVuc_maKhuVuc
DEFAULT ('KV' + RIGHT('00' + CAST(NEXT VALUE FOR seq_KhuVuc AS VARCHAR(10)), 2))
FOR maKhuVuc;
GO

ALTER TABLE LoaiBan
ADD CONSTRAINT DF_LoaiBan_maLoaiBan
DEFAULT ('LB' + RIGHT('00' + CAST(NEXT VALUE FOR seq_LoaiBan AS VARCHAR(10)), 2))
FOR maLoaiBan;
GO

/*==========================================================
4. PROC THÊM BÀN TỰ ĐỘNG
==========================================================*/

CREATE OR ALTER PROC sp_ThemBanTuDong
    @maKhuVuc VARCHAR(20),
    @maLoaiBan VARCHAR(20),
    @tenBan NVARCHAR(100),
    @soChoNgoi INT,
    @ghiChu NVARCHAR(255) = NULL,
    @trangThai NVARCHAR(50) = N'Bàn trống'
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @kyHieu NVARCHAR(20);
    DECLARE @soThuTu INT;
    DECLARE @maBanMoi VARCHAR(20);

    SELECT @kyHieu = kyHieu
    FROM KhuVuc
    WHERE maKhuVuc = @maKhuVuc;

    IF @kyHieu IS NULL OR LTRIM(RTRIM(@kyHieu)) = ''
    BEGIN
        RAISERROR(N'Khu vực chưa có ký hiệu.', 16, 1);
        RETURN;
    END;

    SELECT @soThuTu = ISNULL(MAX(TRY_CAST(RIGHT(maBan, 2) AS INT)), 0) + 1
    FROM Ban
    WHERE maKhuVuc = @maKhuVuc
      AND maBan LIKE CAST(@kyHieu AS VARCHAR(20)) + '[0-9][0-9]';

    SET @maBanMoi = CAST(@kyHieu AS VARCHAR(20))
                  + RIGHT('00' + CAST(@soThuTu AS VARCHAR(10)), 2);

    INSERT INTO Ban (maBan, maKhuVuc, maLoaiBan, tenBan, ghiChu, soChoNgoi, trangThai)
    VALUES (@maBanMoi, @maKhuVuc, @maLoaiBan, @tenBan, @ghiChu, @soChoNgoi, @trangThai);
END;
GO


/*==========================================================
5. DỮ LIỆU DANH MỤC
==========================================================*/
INSERT INTO LoaiMonAn (maLoaiMonAn, tenLoaiMonAn)
VALUES
('LM01', N'Món khai vị'),
('LM02', N'Món chính'),
('LM03', N'Món phụ'),
('LM04', N'Tráng miệng'),
('LM05', N'Nước uống');
GO

INSERT INTO LoaiKhuyenMai (maLoaiKM, tenLoaiKM)
VALUES
('LKM01', N'Phần trăm'),
('LKM02', N'Số tiền'),
('LKM03', N'Thành viên');
GO

INSERT INTO LoaiKhachHang (maLoaiKH, tenLoaiKH)
VALUES
('LKH01', N'Thường'),
('LKH02', N'Vàng'),
('LKH03', N'Kim cương');
GO

INSERT INTO LoaiBan (maLoaiBan, tenLoaiBan)
VALUES
('LB01', N'Bàn 2 chỗ'),
('LB02', N'Bàn 4 chỗ'),
('LB03', N'Bàn 6 chỗ'),
('LB04', N'Bàn 8 chỗ');
GO

INSERT INTO KhuVuc (maKhuVuc, tenKhuVuc, soLuongBan, trangThai, kyHieu)
VALUES
('KV01', N'Tầng 1', 15, N'Hoạt động', N'A'),
('KV02', N'Tầng 2', 20, N'Hoạt động', N'B'),
('KV03', N'Sân thượng', 20, N'Hoạt động', N'C');
GO

/* đồng bộ sequence sau khi chèn mã tay */
ALTER SEQUENCE seq_LoaiMonAn RESTART WITH 6;
ALTER SEQUENCE seq_LoaiKhuyenMai RESTART WITH 4;
ALTER SEQUENCE seq_LoaiKhachHang RESTART WITH 4;
ALTER SEQUENCE seq_LoaiBan RESTART WITH 5;
ALTER SEQUENCE seq_KhuVuc RESTART WITH 4;
GO

/*==========================================================
6. DỮ LIỆU MÓN ĂN
Lẩu đưa vào Món chính
==========================================================*/
INSERT INTO MonAn (maMon, maLoaiMonAn, tenMon, anhMon, donGia, moTa, trangThai)
VALUES
-- LM01: Món khai vị
('MM001', 'LM01', N'Gỏi cuốn tôm thịt', N'goicuon.png', 45000, N'Khai vị truyền thống', 1),
('MM002', 'LM01', N'Súp cua', N'supcua.png', 55000, N'Súp cua nóng', 1),
('MM003', 'LM01', N'Chả giò hải sản', N'chagiohaisan.png', 65000, N'Chả giò chiên giòn', 1),
('MM004', 'LM01', N'Salad cá ngừ', N'saladcangu.png', 70000, N'Salad tươi mát', 1),
('MM005', 'LM01', N'Khoai tây chiên', N'khoaitaychien.png', 40000, N'Ăn kèm khai vị', 1),
('MM006', 'LM01', N'Cánh gà chiên nước mắm', N'canhga.png', 85000, N'Cánh gà đậm vị', 1),

-- LM02: Món chính
('MM007', 'LM02', N'Cơm chiên hải sản', N'comchienhaisan.png', 120000, N'Cơm chiên hải sản đặc biệt', 1),
('MM008', 'LM02', N'Bò lúc lắc', N'boluclac.png', 165000, N'Bò mềm ăn kèm khoai tây', 1),
('MM009', 'LM02', N'Sườn nướng mật ong', N'suonnuong.png', 150000, N'Sườn nướng thơm ngon', 1),
('MM010', 'LM02', N'Lẩu thái hải sản', N'lauthai.png', 280000, N'Lẩu vị chua cay', 1),
('MM011', 'LM02', N'Lẩu nấm', N'launam.png', 260000, N'Lẩu thanh đạm', 1),
('MM012', 'LM02', N'Mì xào hải sản', N'mixaohaisan.png', 110000, N'Mì xào đậm vị', 1),
('MM013', 'LM02', N'Cơm gà xối mỡ', N'comgaxoimo.png', 90000, N'Cơm gà giòn rụm', 1),
('MM014', 'LM02', N'Cá hồi sốt bơ tỏi', N'cahoi.png', 190000, N'Cá hồi áp chảo', 1),
('MM015', 'LM02', N'Bò né', N'bone.png', 95000, N'Bò né trứng ốp la', 1),
('MM016', 'LM02', N'Gà nướng lu', N'ganuonglu.png', 210000, N'Gà nướng nguyên con', 1),
('MM017', 'LM02', N'Tôm nướng muối ớt', N'tomnuong.png', 175000, N'Tôm nướng cay nhẹ', 1),
('MM018', 'LM02', N'Mực chiên giòn', N'mucchien.png', 130000, N'Mực tươi chiên giòn', 1),
('MM019', 'LM02', N'Cơm chiên cá mặn', N'comchiencaman.png', 105000, N'Cơm chiên cá mặn đậm đà', 1),
('MM020', 'LM02', N'Bún bò Nam Bộ', N'bunbonambo.png', 85000, N'Bún bò trộn rau', 1),

-- LM03: Món phụ
('MM021', 'LM03', N'Cơm trắng', N'comtrang.png', 15000, N'Cơm dùng kèm', 1),
('MM022', 'LM03', N'Bún tươi', N'buntuoi.png', 12000, N'Bún dùng kèm', 1),
('MM023', 'LM03', N'Rau sống', N'rausong.png', 20000, N'Rau ăn kèm', 1),
('MM024', 'LM03', N'Kim chi', N'kimchi.png', 25000, N'Món phụ ăn kèm', 1),
('MM025', 'LM03', N'Bánh mì', N'banhmi.png', 10000, N'Bánh mì nóng giòn', 1),

-- LM04: Tráng miệng
('MM026', 'LM04', N'Bánh flan', N'banhflan.png', 30000, N'Tráng miệng mềm mịn', 1),
('MM027', 'LM04', N'Trái cây thập cẩm', N'traicay.png', 50000, N'Trái cây theo mùa', 1),
('MM028', 'LM04', N'Rau câu dừa', N'raucaudua.png', 28000, N'Mát lạnh', 1),

-- LM05: Nước uống
('MM029', 'LM05', N'Coca Cola', N'coca.png', 20000, N'Nước ngọt có gas', 1),
('MM030', 'LM05', N'Nước suối', N'nuocsuoi.png', 15000, N'Nước suối chai', 1),
('MM031', 'LM05', N'Trà đào', N'tradao.png', 35000, N'Trà đào cam sả', 1),
('MM032', 'LM05', N'Nước cam', N'nuoccam.png', 30000, N'Nước cam tươi', 1),
('MM033', 'LM05', N'Pepsi', N'pepsi.png', 20000, N'Nước ngọt có gas', 1),
('MM034', 'LM05', N'7 Up', N'7up.png', 20000, N'Nước ngọt chanh', 1),
('MM035', 'LM04', N'Kem vani', N'kemvani.png', 35000, N'Kem lạnh tráng miệng', 1);
GO


ALTER SEQUENCE seq_MonAn RESTART WITH 36;
GO

/*==========================================================
7. NHÂN VIÊN / TÀI KHOẢN / CA LÀM
tên đăng nhập và mật khẩu theo bạn sửa
==========================================================*/
INSERT INTO NhanVien (maNV, hoTen, anhNhanVien, ngaySinh, gioiTinh, cccd, email, sdt, chucVu, trangThai)
VALUES
('NV001', N'Lê Hoàng Anh', N'hoanganh.png', '2005-06-24', 1, '079205000001', 'hoanganh@hyv.com', '0901000001', N'Quản lý', N'Đang làm'),
('NV002', N'Trần Quốc Dũng', N'quocdung.png', '2005-03-15', 1, '079205000002', 'quocdung@hyv.com', '0901000002', N'Lễ tân', N'Đang làm'),
('NV003', N'Nguyễn Hạ Ánh Dương', N'anhduong.png', '2005-09-10', 0, '079205000003', 'anhduong@hyv.com', '0901000003', N'Lễ tân', N'Đang làm'),
('NV004', N'Huỳnh Thị Ngọc Tiên', N'ngoctien.png', '2005-11-20', 0, '079205000004', 'ngoctien@hyv.com', '0901000004', N'Lễ tân', N'Đang làm');
GO

INSERT INTO TaiKhoan (maTaiKhoan, tenDangNhap, matKhau, phanQuyen, trangThai, maNV)
VALUES
('TK01', 'NV001', 'quanly', N'Quản lý', 1, 'NV001'),
('TK02', 'NV002', 'nhanvien', N'Lễ tân', 1, 'NV002'),
('TK03', 'NV003', 'thungan', N'Lễ tân', 1, 'NV003'),
('TK04', 'NV004', 'phucvu', N'Lễ tân', 1, 'NV004');
GO

INSERT INTO CaLamViec (maCa, tenCa, thoiGianMoCa, thoiGianDongCa, maTaiKhoan)
VALUES
('CL001', N'Ca sáng 07/04/2026', '2026-04-07 07:00:00', '2026-04-07 14:00:00', 'TK01'),
('CL002', N'Ca chiều 07/04/2026', '2026-04-07 14:00:00', '2026-04-07 22:00:00', 'TK02'),
('CL003', N'Ca sáng 08/04/2026', '2026-04-08 07:00:00', '2026-04-08 14:00:00', 'TK03');
GO

ALTER SEQUENCE seq_NhanVien RESTART WITH 5;
ALTER SEQUENCE seq_TaiKhoan RESTART WITH 5;
ALTER SEQUENCE seq_CaLamViec RESTART WITH 4;
GO

/*==========================================================
8. KHÁCH HÀNG
==========================================================*/
INSERT INTO KhachHang (maKH, tenKH, sdt, maLoaiKH, diemTichLuy)
VALUES
('KH00001', N'Nguyễn Văn Nam', '0911111111', 'LKH01', 5),
('KH00002', N'Trần Thị Mai', '0911111112', 'LKH02', 56),
('KH00003', N'Lê Quốc Bảo', '0911111113', 'LKH01', 12),
('KH00004', N'Phạm Minh Thư', '0911111114', 'LKH03', 120),
('KH00005', N'Đặng Gia Hân', '0911111115', 'LKH01', 3);
GO

ALTER SEQUENCE seq_KhachHang RESTART WITH 6;
GO

/*==========================================================
9. KHUYẾN MÃI
==========================================================*/
INSERT INTO KhuyenMai
(maKM, maLoaiKM, maNV, giaTri, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, doiTuongApDung, dieuKienApDung, ghiChu, trangThai)
VALUES
('KM001', 'LKM01', 'NV001', 10, N'Giảm 10%', '2026-04-01 00:00:00', '2026-04-30 23:59:59', N'Tất cả KH', 500000, N'Hóa đơn từ 500K', N'Đang áp dụng'),
('KM002', 'LKM02', 'NV001', 50000, N'Giảm 50K', '2026-04-01 00:00:00', '2026-05-15 23:59:59', N'Tất cả KH', 1000000, N'Hóa đơn từ 1 triệu', N'Đang áp dụng'),
('KM003', 'LKM03', 'NV001', 15, N'Kim cương', '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'Kim cương', 0, N'Ưu đãi thành viên', N'Đang áp dụng');
GO

ALTER SEQUENCE seq_KhuyenMai RESTART WITH 4;
GO

/*==========================================================
10. BÀN: A 15, B 20, C 20
==========================================================*/

DECLARE @i INT = 1;
DECLARE @maLoaiBan_i VARCHAR(20);
DECLARE @soChoNgoi_i INT;
DECLARE @tenBan_i NVARCHAR(100);

WHILE @i <= 15
BEGIN
    SET @maLoaiBan_i = CASE
                           WHEN @i <= 5 THEN 'LB01'
                           WHEN @i <= 10 THEN 'LB02'
                           WHEN @i <= 13 THEN 'LB03'
                           ELSE 'LB04'
                       END;

    SET @soChoNgoi_i = CASE
                           WHEN @i <= 5 THEN 2
                           WHEN @i <= 10 THEN 4
                           WHEN @i <= 13 THEN 6
                           ELSE 8
                       END;

    SET @tenBan_i = N'Bàn A' + RIGHT('00' + CAST(@i AS VARCHAR(10)), 2);

    EXEC sp_ThemBanTuDong
        @maKhuVuc = 'KV01',
        @maLoaiBan = @maLoaiBan_i,
        @tenBan = @tenBan_i,
        @soChoNgoi = @soChoNgoi_i,
        @ghiChu = N'Khu A',
        @trangThai = N'Bàn trống';

    SET @i = @i + 1;
END
GO

DECLARE @j INT = 1;
DECLARE @maLoaiBan_j VARCHAR(20);
DECLARE @soChoNgoi_j INT;
DECLARE @tenBan_j NVARCHAR(100);

WHILE @j <= 20
BEGIN
    SET @maLoaiBan_j = CASE
                           WHEN @j <= 6 THEN 'LB01'
                           WHEN @j <= 12 THEN 'LB02'
                           WHEN @j <= 17 THEN 'LB03'
                           ELSE 'LB04'
                       END;

    SET @soChoNgoi_j = CASE
                           WHEN @j <= 6 THEN 2
                           WHEN @j <= 12 THEN 4
                           WHEN @j <= 17 THEN 6
                           ELSE 8
                       END;

    SET @tenBan_j = N'Bàn B' + RIGHT('00' + CAST(@j AS VARCHAR(10)), 2);

    EXEC sp_ThemBanTuDong
        @maKhuVuc = 'KV02',
        @maLoaiBan = @maLoaiBan_j,
        @tenBan = @tenBan_j,
        @soChoNgoi = @soChoNgoi_j,
        @ghiChu = N'Khu B',
        @trangThai = N'Bàn trống';

    SET @j = @j + 1;
END
GO

DECLARE @k INT = 1;
DECLARE @maLoaiBan_k VARCHAR(20);
DECLARE @soChoNgoi_k INT;
DECLARE @tenBan_k NVARCHAR(100);

WHILE @k <= 20
BEGIN
    SET @maLoaiBan_k = CASE
                           WHEN @k <= 6 THEN 'LB01'
                           WHEN @k <= 12 THEN 'LB02'
                           WHEN @k <= 17 THEN 'LB03'
                           ELSE 'LB04'
                       END;

    SET @soChoNgoi_k = CASE
                           WHEN @k <= 6 THEN 2
                           WHEN @k <= 12 THEN 4
                           WHEN @k <= 17 THEN 6
                           ELSE 8
                       END;

    SET @tenBan_k = N'Bàn C' + RIGHT('00' + CAST(@k AS VARCHAR(10)), 2);

    EXEC sp_ThemBanTuDong
        @maKhuVuc = 'KV03',
        @maLoaiBan = @maLoaiBan_k,
        @tenBan = @tenBan_k,
        @soChoNgoi = @soChoNgoi_k,
        @ghiChu = N'Khu C',
        @trangThai = N'Bàn trống';

    SET @k = @k + 1;
END
GO

/*==========================================================
11. PHIẾU ĐẶT BÀN
==========================================================*/
INSERT INTO PhieuDatBan (maPhieuDatBan, maBan, tenKhach, sdt, soLuongNguoi, thoiGianDen, tienCoc, ghiChu, trangThai)
VALUES
('PDB00001', 'A01', N'Nguyễn Văn Nam', '0922000001', 2, '2026-04-08 18:30:00', 200000, N'Đặt bàn thường', N'Đang chờ'),
('PDB00002', 'A05', N'Trần Thị Mai', '0922000002', 4, '2026-04-08 19:00:00', 450000, N'Có đặt món trước', N'Đang chờ'),
('PDB00003', 'B10', N'Lê Quốc Bảo', '0922000003', 6, '2026-04-09 18:00:00', 200000, N'Nhóm bạn', N'Đang chờ'),
('PDB00004', 'C15', N'Phạm Minh Thư', '0922000004', 8, '2026-04-09 20:00:00', 600000, N'Sinh nhật', N'Đang chờ'),
('PDB00005', 'B03', N'Đặng Gia Hân', '0922000005', 2, '2026-04-10 17:45:00', 200000, N'Đặt trước', N'Đang chờ');
GO

ALTER SEQUENCE seq_PhieuDatBan RESTART WITH 6;
GO

/*==========================================================
12. PHIẾU ĐẶT MÓN
==========================================================*/
INSERT INTO PhieuDatMon (maPhieuDatBan, maMon, soLuong, donGia, ghiChu)
VALUES
('PDB00002', 'MM003', 2, 120000, N''),
('PDB00002', 'MM010', 3, 20000, N''),
('PDB00004', 'MM006', 1, 280000, N'Ít cay'),
('PDB00004', 'MM009', 1, 50000, N'');
GO

/*==========================================================
13. HÓA ĐƠN
==========================================================*/
INSERT INTO HoaDon
(maHD, thoiGianVao, thoiGianRa, maPhieuDatBan, maKH, maKM, maBan, maNV, tienKhachTra, thueVAT, tienThua, trangThai)
VALUES
('HD00001', '2026-04-06 18:00:00', '2026-04-06 19:30:00', NULL, 'KH00001', 'KM001', 'A02', 'NV003', 500000, 35000, 45000, N'Đã thanh toán'),
('HD00002', '2026-04-06 19:00:00', '2026-04-06 21:00:00', 'PDB00002', 'KH00002', NULL, 'A05', 'NV003', 700000, 49000, 81000, N'Đã thanh toán'),
('HD00003', '2026-04-07 12:00:00', NULL, NULL, 'KH00003', NULL, 'B01', 'NV002', 0, 0, 0, N'Chưa thanh toán');
GO

ALTER SEQUENCE seq_HoaDon RESTART WITH 4;
GO

/*==========================================================
14. CHI TIẾT HÓA ĐƠN
==========================================================*/
INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia, ghiChu)
VALUES
('HD00001', 'MM001', 1, 45000, N''),
('HD00001', 'MM004', 1, 165000, N''),
('HD00001', 'MM010', 2, 20000, N''),
('HD00002', 'MM003', 2, 120000, N''),
('HD00002', 'MM006', 1, 280000, N''),
('HD00002', 'MM012', 2, 35000, N'');
GO


/*==========================================================
15. KIỂM TRA
==========================================================*/
SELECT * FROM LoaiMonAn;
SELECT * FROM MonAn;
SELECT * FROM LoaiKhuyenMai;
SELECT * FROM KhuyenMai;
SELECT * FROM NhanVien;
SELECT * FROM TaiKhoan;
SELECT * FROM CaLamViec;
SELECT * FROM LoaiKhachHang;
SELECT * FROM KhachHang;
SELECT * FROM LoaiBan;
SELECT * FROM KhuVuc;
SELECT * FROM Ban ORDER BY maBan;
SELECT * FROM PhieuDatBan;
SELECT * FROM PhieuDatMon;
SELECT * FROM HoaDon;
SELECT * FROM ChiTietHoaDon;
GO
