package model;

public class TacGia {
    private String maTacGia;
    private String ho;
    private String ten;

    public TacGia(){}
    public TacGia(String maTacGia, String ho, String ten){
        this.maTacGia = maTacGia;
        this.ho = ho;
        this.ten = ten;
    }

    public String getMaTacGia(){
        return maTacGia;
    }
    public void setMaTacGia(String maTacGia){
        this.maTacGia = maTacGia;
    }

    public String getHo(){
        return ho;
    }
    public void setHo(String ho){
        this.ho = ho;
    }

    public String getTen(){
        return ten;
    }
    public void setTen(String ten){
        this.ten = ten;
    }
}
