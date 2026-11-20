package model;

import enums.Role;

public class TaiKhoan {
    private String maTaiKhoan;
    private String userName;
    private String password;
    private Role role;
 
    public TaiKhoan() {}
 
    public TaiKhoan(String maTaiKhoan, String userName, String password, Role role) {
        this.maTaiKhoan = maTaiKhoan;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public String getMaTaiKhoan(){
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan){
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }
}
