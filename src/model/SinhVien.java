package model;

import java.time.LocalDate;
import enums.Sex;

public class SinhVien {
    private String maSinhVien;
    private String ho;
    private String ten;
    private LocalDate ngaySinh;
    private Sex gioiTinh;
    private String email;
    private String sdt;
    private String lop;
    private String khoa;
    private String maTaiKhoan;

    public SinhVien(){}

    public SinhVien(String maSinhVien, String ho, String ten, LocalDate ngaySinh, 
        Sex gioiTinh, String email, String sdt, String lop, String khoa, String maTaiKhoan){
        this.maSinhVien = maSinhVien;
        this.ho = ho;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.sdt = sdt;
        this.lop = lop;
        this.khoa = khoa;
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaSinhVien() { 
        return maSinhVien; 
    }
    public void setMaSinhVien(String maSinhVien) { 
        this.maSinhVien = maSinhVien; 
    }
 
    public String getHo() { 
        return ho; 
    }
    public void setHo(String ho) { 
        this.ho = ho; 
    }
 
    public String getTen() { 
        return ten; 
    }
    public void setTen(String ten) { 
        this.ten = ten; 
    }
 
    public LocalDate getNgaySinh() { 
        return ngaySinh; 
    }
    public void setNgaySinh(LocalDate ngaySinh) { 
        this.ngaySinh = ngaySinh; 
    }

    public Sex getGioiTinh(){
        return gioiTinh;
    }
    public void setGioiTinh(Sex gioiTinh){
        this.gioiTinh = gioiTinh;
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
 
    public String getLop() { 
        return lop; 
    }
    public void setLop(String lop) { 
        this.lop = lop; 
    }
 
    public String getKhoa() { 
        return khoa; 
    }
    public void setKhoa(String khoa) { 
        this.khoa = khoa; 
    }
 
    public String getMaTaiKhoan() { 
        return maTaiKhoan; 
    }
    public void setMaTaiKhoan(String maTaiKhoan) { 
        this.maTaiKhoan = maTaiKhoan; 
    }
}
