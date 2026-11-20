package model;

import enums.TrangThaiSach;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class Sach {
    private String maSach;
    private String tenSach;
    private String nhaXuatBan;
    private Integer namXuatBan;
    private int soLuong;
    private BigDecimal giaTri;
    private TrangThaiSach trangThai;
    private String maTheLoai;
    private List<TacGia> dsTacGia = new ArrayList<>();
 
    public Sach() {}
 
    public Sach(String maSach, String tenSach, String nhaXuatBan, Integer namXuatBan, BigDecimal giaTri, int soLuong ,TrangThaiSach trangThai, String maTheLoai) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.nhaXuatBan = nhaXuatBan;
        this.namXuatBan = namXuatBan;
        this.soLuong = soLuong;
        this.giaTri = giaTri;
        this.trangThai = trangThai;
        this.maTheLoai = maTheLoai;
    }
 
    public String getMaSach() { 
        return maSach; 
    }
    public void setMaSach(String maSach) { 
        this.maSach = maSach; 
    }
 
    public String getTenSach() { 
        return tenSach; 
    }
    public void setTenSach(String tenSach) { 
        this.tenSach = tenSach; 
    }
 
    public String getNhaXuatBan() { 
        return nhaXuatBan; 
    }
    public void setNhaXuatBan(String nhaXuatBan) { 
        this.nhaXuatBan = nhaXuatBan; 
    }
 
    public Integer getNamXuatBan() { 
        return namXuatBan; 
    }
    public void setNamXuatBan(Integer namXuatBan) { 
        this.namXuatBan = namXuatBan; 
    }
 
    public int getSoLuong() { 
        return soLuong; 
    }
    public void setSoLuong(int soLuong) { 
        this.soLuong = soLuong;
    }

    public BigDecimal getGiaTri(){
        return giaTri;
    }
    public void setGiaTri(BigDecimal giaTri){
        this.giaTri = giaTri;
    }

    public TrangThaiSach getTrangThai() { 
        return trangThai; 
    }
    public void setTrangThai(TrangThaiSach trangThai) { 
        this.trangThai = trangThai; 
    }
 
    public String getMaTheLoai() { 
        return maTheLoai; 
    }
    public void setMaTheLoai(String maTheLoai) { 
        this.maTheLoai = maTheLoai; 
    }

    public List<TacGia> getDsTacGia(){
        return dsTacGia;
    }
    public void setDsTacGia(List<TacGia> dsTacGia){
        this.dsTacGia = dsTacGia;
    }
}
