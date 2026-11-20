package UI;

import javax.swing.*;
import java.awt.*;
import enums.Role;
import java.sql.SQLException;

public class PhanQuyenFrame extends JFrame{
    public PhanQuyenFrame(){
        super("Hệ thống quản lý thư viện");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(taoNoiDung());
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public JPanel taoNoiDung(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(26, 36, 26, 36));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        JLabel tieuDe = new taoTieuDe().label("CHÀO MỪNG ĐẾN VỚI HỆ THỐNG QUẢN LÝ THỪ VIỆN");
        c.insets = new Insets(0, 0, 36, 0);
        p.add(tieuDe, c);

        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        JLabel lb1 = new taoDongNoiDung().Label("Đăng nhập với quyền", "Arial", Font.BOLD, 18);
        p.add(lb1, c);

        JPanel khung = new taoKhung().panel();
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        JButton btnQuanLy = new taoNut().button("Quyền quản lý", "Arial", Font.BOLD, 20);
        btnQuanLy.addActionListener(e-> {
            try{
                DangNhap(Role.MANAGER);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        khung.add(btnQuanLy, c);

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        JButton btnSinhVien = new taoNut().button("Quyền sinh viên", "Arial", Font.BOLD, 20);
        btnSinhVien.addActionListener(e-> {
            try{
                DangNhap(Role.STUDENT);
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        khung.add(btnSinhVien, c);

        p.add(khung, c);
        return p;
    }

    void DangNhap(Role role) throws SQLException{
        dispose();
        new LoginFrame(role).setVisible(true);
    }
}
