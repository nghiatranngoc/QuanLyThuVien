package UI;

import javax.swing.*;

import Services.AuthService;

import java.awt.*;
import java.sql.SQLException;

public class DangKyQuanLyDialog extends JDialog{
    private final AuthService as = new AuthService();

    private final JTextField txtMaTaiKhoan = new JTextField(18);
    private final JTextField txtMaQuanLy = new JTextField(18);
    private final JTextField txtHo = new JTextField(18);
    private final JTextField txtTen = new JTextField(18);
    private final JTextField txtUserName = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JPasswordField txtNhapLai = new JPasswordField(18);

    public DangKyQuanLyDialog(Frame owner) throws SQLException{
        super(owner, "Đăng ký tài khoản Quản lý", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtMaTaiKhoan.setText(as.xemTruocMaTaiKhoanQuanLy());
        txtMaTaiKhoan.setEditable(false);
        txtMaQuanLy.setText(as.xemTruocMaQuanLy());
        txtMaQuanLy.setEditable(false);

        setContentPane(taoNoiDung());
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private JPanel taoNoiDung(){
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel tieuDe = new taoTieuDe().label("ĐĂNG KÝ QUẢN LÝ");
        tieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(tieuDe, BorderLayout.NORTH);

        JPanel khung = new taoKhung().panel();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        themDong(khung, c, 0, "Mã tài khoản", txtMaTaiKhoan);
        themDong(khung, c, 1, "Mã quản lý", txtMaQuanLy);
        themDong(khung, c, 2, "Họ", txtHo);
        themDong(khung, c, 3, "Tên", txtTen);
        themDong(khung, c, 4, "Tên đăng nhập", txtUserName);
        themDong(khung, c, 5, "Mật khẩu", txtPassword);
        themDong(khung, c, 6, "Nhập lại mật khẩu", txtNhapLai);

        JPanel hang = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton btnXacNhan = new taoNut().Button("Xác nhận");
        JButton btnHuy = new taoNut().Button("Hủy");
        btnXacNhan.addActionListener(e -> xacNhan());
        btnHuy.addActionListener(e -> huy());
        getRootPane().setDefaultButton(btnXacNhan);
        hang.add(btnXacNhan);
        hang.add(btnHuy);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(15, 6, 6, 6);
        khung.add(hang, c);

        p.add(khung, BorderLayout.CENTER);
        return p;
    }

    private void themDong(JPanel khung, GridBagConstraints c, int y, String nhan, JComponent o){
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        khung.add(new taoDongNoiDung().Label(nhan, "Arial", Font.BOLD, 14), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        khung.add(o, c);
    }

    private void xacNhan(){
        String ho = txtHo.getText();
        String ten = txtTen.getText();
        String userName = txtUserName.getText();
        String password = new String(txtPassword.getPassword());
        String nhapLai = new String(txtNhapLai.getPassword());

        try{
            as.kiemTraDangKyQuanLy(userName, password, nhapLai, ho, ten);

            int chon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng ký tài khoản này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            if(chon != JOptionPane.YES_OPTION){
                return;
            }

            as.dangKyQuanLy(userName, password, nhapLai, ho, ten);
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            dispose();
        } catch(IllegalArgumentException ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Loi CSDL: " + ex.getMessage());
        }
    }

    private void huy(){
        int chon = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn hủy đăng ký?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if(chon == JOptionPane.YES_OPTION){
            dispose();
        }
    }
}