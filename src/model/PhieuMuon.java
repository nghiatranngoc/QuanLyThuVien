package model;

import java.time.LocalDate;

public class PhieuMuon {
    private String maPhieu;
    private LocalDate ngayMuon;
    private String maSinhVien;
 
    public PhieuMuon() {}
 
    public PhieuMuon(String maPhieu, LocalDate ngayMuon, String maSinhVien) {
        this.maPhieu = maPhieu;
        this.ngayMuon = ngayMuon;
        this.maSinhVien = maSinhVien;
    }
 
    public String getMaPhieu() { 
        return maPhieu; 
    }
    public void setMaPhieu(String maPhieu) { 
        this.maPhieu = maPhieu; 
    }
 
    public LocalDate getNgayMuon() { 
        return ngayMuon; 
    }
    public void setNgayMuon(LocalDate ngayMuon) { 
        this.ngayMuon = ngayMuon; 
    }
 
    public String getMaSinhVien() { 
        return maSinhVien; 
    }
    public void setMaSinhVien(String maSinhVien) { 
        this.maSinhVien = maSinhVien; 
    }
}
