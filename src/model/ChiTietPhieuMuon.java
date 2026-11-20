package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import enums.TinhTrangMuon;

public class ChiTietPhieuMuon {
    private String maPhieu;
    private String maSach;
    private LocalDate hanTra;
    private LocalDate ngayTra;
    private TinhTrangMuon tinhTrang;
    private BigDecimal tienPhat;
 
    public ChiTietPhieuMuon() {}
 
    public ChiTietPhieuMuon(String maPhieu, String maSach, LocalDate hanTra, LocalDate ngayTra, TinhTrangMuon tinhTrang, BigDecimal tienPhat) {
        this.maPhieu = maPhieu;
        this.maSach = maSach;
        this.hanTra = hanTra;
        this.ngayTra = ngayTra;
        this.tinhTrang = tinhTrang;
        this.tienPhat = tienPhat;
    }
 
    public String getMaPhieu() { 
        return maPhieu; 
    }
    public void setMaPhieu(String maPhieu) { 
        this.maPhieu = maPhieu; 
    }
 
    public String getMaSach() { 
        return maSach; 
    }
    public void setMaSach(String maSach) { 
        this.maSach = maSach; 
    }
 
    public LocalDate getHanTra() { 
        return hanTra; 
    }
    public void setHanTra(LocalDate hanTra) { 
        this.hanTra = hanTra; 
    }
 
    public LocalDate getNgayTra() { 
        return ngayTra; 
    }
    public void setNgayTra(LocalDate ngayTra) { 
        this.ngayTra = ngayTra; 
    }
 
    public TinhTrangMuon getTinhTrang() { 
        return tinhTrang; 
    }
    public void setTinhTrang(TinhTrangMuon tinhTrang) { 
        this.tinhTrang = tinhTrang; 
    }
 
    public BigDecimal getTienPhat() { 
        return tienPhat; 
    }
    public void setTienPhat(BigDecimal tienPhat) { 
        this.tienPhat = tienPhat; 
    }
}
