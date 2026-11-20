DROP DATABASE IF EXISTS HeThongQuanLyThuVien;

CREATE DATABASE IF NOT EXISTS HeThongQuanLyThuVien
	CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    
USE HeThongQuanLyThuVien;

CREATE TABLE TaiKhoan(
	maTaiKhoan VARCHAR(12) NOT NULL,
    userName VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'MANAGER') NOT NULL,
    PRIMARY KEY (maTaiKhoan),
    UNIQUE KEY uk_taikhoan_username (userName)
) ENGINE = InnoDB;

CREATE TABLE sinhVien(
	maSinhVien VARCHAR(12) NOT NULL, 
    ho VARCHAR(50) NOT NULL,
    ten VARCHAR(50) NOT NULL,
    ngaySinh DATE NOT NULL,
    gioiTinh ENUM("NAM", "NU", "KHAC") NOT NULL,
    email VARCHAR(100) NULL,
    sdt VARCHAR(10) NULL,
    lop VARCHAR(50) NOT NULL,
    khoa VARCHAR(50) NOT NULL,
    maTaiKhoan VARCHAR(12) NOT NULL,
    PRIMARY KEY (maSinhVien),
    UNIQUE KEY uk_sinhvien_taikhoan (maTaiKhoan),
    UNIQUE KEY uk_sinhvien_email (email), 
    UNIQUE KEY uk_sinhvien_sdt (sdt),
    CONSTRAINT fk_sinhvien_taikhoan
		FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE quanLy(
	maQl VARCHAR(12) NOT NULL,
	ho VARCHAR(50) NOT NULL,
    ten VARCHAR(50) NOT NULL,
    maTaiKhoan VARCHAR(12) NOT NULL,
    PRIMARY KEY (maQl),
    UNIQUE KEY uk_quanly_taikhoan (maTaiKhoan),
    CONSTRAINT fk_quanly_taikhoan
		FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE tacGia(
	maTacGia VARCHAR(12) NOT NULL,
    ho VARCHAR(50) NOT NULL,
    ten VARCHAR(50) NOT NULL,
    PRIMARY KEY (maTacGia)
) ENGINE = InnoDB;

CREATE TABLE theLoai(
	maTheLoai VARCHAR(12) NOT NULL,
    tenTheLoai VARCHAR(50) NOT NULL,
    PRIMARY KEY (maTheLoai)
) ENGINE = InnoDB;

CREATE TABLE sach(
	maSach VARCHAR(12) NOT NULL,
    tenSach VARCHAR(50) NOT NULL,
    nhaXuatBan VARCHAR(50) NULL,
    namXuatBan YEAR NULL,
    giaTri DECIMAL(10, 2) NOT NULL,
    soLuong INT UNSIGNED NOT NULL DEFAULT 1,
    trangThai ENUM('BINH_THUONG', 'MAT', 'HU_HONG') NOT NULL DEFAULT 'BINH_THUONG',
    maTheLoai VARCHAR(12) NULL,
    PRIMARY KEY (maSach),
    CONSTRAINT fk_ma_the_loai
		FOREIGN KEY (maTheLoai) REFERENCES theLoai(maTheLoai)
        ON UPDATE CASCADE ON DELETE SET NULL,
	CONSTRAINT check_giatri CHECK (giaTri >= 0)
)ENGINE = InnoDB;

CREATE TABLE sach_tacgia(
	maSach VARCHAR(12) NOT NULL,
    maTacGia VARCHAR(12) NOT NULL,
    PRIMARY KEY (maSach, maTacGia),
    CONSTRAINT fk_ma_sach
		FOREIGN KEY (maSach) REFERENCES sach(maSach)
        ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_ma_tac_gia
		FOREIGN KEY (maTacGia) REFERENCES tacGia(maTacGia)
        ON UPDATE CASCADE ON DELETE CASCADE
)ENGINE = InnoDB;

CREATE TABLE phieuMuon(
	maPhieu VARCHAR(12) NOT NULL,
    ngayMuon DATE NOT NULL,
    maSinhVien VARCHAR(12) NOT NULL,
    PRIMARY KEY (maPhieu),
    CONSTRAINT fk_ma_sinh_vien
		FOREIGN KEY (maSinhVien) REFERENCES sinhVien(maSinhVien)
        ON UPDATE CASCADE ON DELETE RESTRICT
)ENGINE = InnoDB;


CREATE TABLE chitietPhieuMuon(
	maPhieu VARCHAR(12) NOT NULL,
    maSach VARCHAR(12) NOT NULL,
    hanTra DATE NOT NULL,
    ngayTra DATE NULL,
    tinhTrang ENUM("DUNG_HAN", "QUA_HAN", "CHUA_TRA", "MAT_SACH") NOT NULL DEFAULT "CHUA_TRA",
    tienPhat DECIMAL(10,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (maPhieu, maSach),
    CONSTRAINT fk_ma_phieu
		FOREIGN KEY (maPhieu) REFERENCES phieuMuon(maPhieu)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_chi_tiet_sach
		FOREIGN KEY (maSach) REFERENCES sach(maSach)
        ON UPDATE CASCADE ON DELETE RESTRICT,
	CONSTRAINT chk_tien_phat CHECK (tienPhat >= 0)
)ENGINE = InnoDB;

INSERT INTO theLoai (maTheLoai, tenTheLoai) VALUES
('TL0001', 'Công nghệ thông tin'),
('TL0002', 'Khoa học máy tính'),
('TL0003', 'Lập trình'),
('TL0004', 'Trí tuệ nhân tạo'),
('TL0005', 'Cơ sở dữ liệu'),
('TL0006', 'Mạng máy tính'),
('TL0007', 'An toàn thông tin'),
('TL0008', 'Kỹ thuật phần mềm'),
('TL0009', 'Điện tử viễn thông'),
('TL0010', 'Toán học'),
('TL0011', 'Vật lý'),
('TL0012', 'Hóa học'),
('TL0013', 'Sinh học'),
('TL0014', 'Y học'),
('TL0015', 'Tâm lý học'),
('TL0016', 'Triết học'),
('TL0017', 'Lịch sử'),
('TL0018', 'Địa lý'),
('TL0019', 'Văn học Việt Nam'),
('TL0020', 'Văn học nước ngoài'),
('TL0021', 'Truyện ngắn'),
('TL0022', 'Tiểu thuyết'),
('TL0023', 'Thơ ca'),
('TL0024', 'Truyện tranh'),
('TL0025', 'Thiếu nhi'),
('TL0026', 'Giáo dục'),
('TL0027', 'Ngoại ngữ'),
('TL0028', 'Tiếng Anh'),
('TL0029', 'Kinh tế'),
('TL0030', 'Quản trị kinh doanh'),
('TL0031', 'Marketing'),
('TL0032', 'Tài chính - Ngân hàng'),
('TL0033', 'Kế toán'),
('TL0034', 'Luật'),
('TL0035', 'Chính trị'),
('TL0036', 'Xã hội học'),
('TL0037', 'Kỹ năng sống'),
('TL0038', 'Phát triển bản thân'),
('TL0039', 'Khởi nghiệp'),
('TL0040', 'Kinh doanh'),
('TL0041', 'Du lịch'),
('TL0042', 'Ẩm thực'),
('TL0043', 'Nghệ thuật'),
('TL0044', 'Âm nhạc'),
('TL0045', 'Hội họa'),
('TL0046', 'Kiến trúc'),
('TL0047', 'Thể thao'),
('TL0048', 'Nông nghiệp'),
('TL0049', 'Môi trường'),
('TL0050', 'Tôn giáo'),
('TL0051', 'Từ điển'),
('TL0052', 'Bách khoa toàn thư'),
('TL0053', 'Sách tham khảo'),
('TL0054', 'Sách giáo khoa'),
('TL0055', 'Sách chuyên khảo');

CREATE INDEX idex_ten_sach ON sach(tenSach);
CREATE INDEX idex_ten_sinh_vien ON sinhVien(ten);
CREATE INDEX idex_tac_gia_ten ON tacGia(ten);
CREATE INDEX idex_phieu_muon ON phieuMuon(maSinhVien);