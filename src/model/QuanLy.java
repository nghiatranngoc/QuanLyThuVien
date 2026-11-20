package model;

public class QuanLy {
    private String maQl;
    private String ho;
    private String ten;
    private String maTaiKhoan;
 
    public QuanLy() {}
 
    public QuanLy(String maQl, String ho, String ten, String maTaiKhoan) {
        this.maQl = maQl;
        this.ho = ho;
        this.ten = ten;
        this.maTaiKhoan = maTaiKhoan;
    }
 
    public String getMaQl() { 
        return maQl; 
    }
    public void setMaQl(String maQl) { 
        this.maQl = maQl; 
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
 
    public String getMaTaiKhoan() { 
        return maTaiKhoan; 
    }
    public void setMaTaiKhoan(String maTaiKhoan) { 
        this.maTaiKhoan = maTaiKhoan; 
    }
}
