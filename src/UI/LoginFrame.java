package UI;

import javax.swing.*;

import Services.AuthService;

import java.awt.*;
import enums.Role;
import model.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame{
    public LoginFrame(Role role) throws SQLException{
        super("Đăng nhập - " + tenQuyen(role));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(taoNoiDung(role));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public JPanel taoNoiDung(Role role) throws SQLException{
        boolean laManager = (role == Role.MANAGER);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(26, 36, 26, 36));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JLabel tieuDe = new taoTieuDe().label("LOGIN");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        c.insets = new Insets(0, 0, 36, 0);
        p.add(tieuDe, c);

        c.insets = new Insets(6, 6, 6, 6);
        JPanel khungNhap = new taoKhung().panel();

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        String nhanUser = laManager ? "Tên đăng nhập:" : "MSSV:";
        JLabel lbUserName = new taoDongNoiDung().Label(nhanUser, "Arial", Font.BOLD, 15);
        khungNhap.add(lbUserName, c);

        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        JTextField txtUserName = new JTextField(15);
        khungNhap.add(txtUserName, c);

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        JLabel lbPassword = new taoDongNoiDung().Label("Mật Khẩu:", "Arial", Font.BOLD, 15);
        khungNhap.add(lbPassword, c);

        c.gridx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        JPasswordField txtPassword = new JPasswordField(15);
        khungNhap.add(txtPassword, c);

        int hangY = 3;
        if(!laManager){
            c.gridx = 1;
            c.gridy = 3;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.anchor = GridBagConstraints.WEST;
            JLabel lbNote = new JLabel("Note: mật khẩu mặc định là 1234567");
            lbNote.setFont(new Font("Arial", Font.ITALIC, 12));
            khungNhap.add(lbNote, c);
            hangY = 4;
        }

        JPanel hang = new JPanel(new FlowLayout(laManager ? FlowLayout.CENTER : FlowLayout.RIGHT, 15, 5));

        JButton btnDangNhap = new taoNut().Button("Đăng nhập");
        getRootPane().setDefaultButton(btnDangNhap);
        btnDangNhap.addActionListener(e -> {
            try{
                DangNhap(txtUserName, txtPassword, role);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Loi CSDL: " + ex.getMessage());
            }
        });

        hang.add(btnDangNhap);
        if(laManager){
            JButton btnDangKy = new taoNut().Button("Đăng ký");
            btnDangKy.addActionListener(e -> moDangKy());
            hang.add(btnDangKy);
        }

        c.gridx = 0;
        c.gridy = hangY;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(15, 6, 6, 6);

        khungNhap.add(hang, c);

        c.gridy = 1;
        p.add(khungNhap, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        JButton btnQuayLai = new taoNut().Button("Quay Lại");
        btnQuayLai.addActionListener(e->QuayLai());

        p.add(btnQuayLai, c);
        return p;
    }

    private void moDangKy() {
        try{
            new DangKyQuanLyDialog(this).setVisible(true);
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loi CSDL: " + ex.getMessage());
        }
    }

    private void QuayLai() {
        dispose();
        new PhanQuyenFrame().setVisible(true);
    }

    private void DangNhap(JTextField txtUserName, JPasswordField txtPassword, Role role) throws SQLException{
        String userName = txtUserName.getText();
        String password = new String(txtPassword.getPassword());

        AuthService as = new AuthService();
        TaiKhoan tk = as.dangNhap(userName, password);
        if(tk.getRole() != role){
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
        JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");

        if(role == Role.MANAGER){
            dispose();
            new ManagerFrame(userName).setVisible(true);
        } else {
            dispose();
            new SinhVienFrame(userName).setVisible(true);
        }
    }

    private static String tenQuyen(Role role){
        return role == Role.MANAGER ? "Quyền Quản lý" : "Quyền Sinh Viên";
    }
}