package UI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.BevelBorder;

public class taoNut {
    private final Font fontNut = new Font("Arial", Font.BOLD, 13);
    public JButton button(String noiDung, String fontChu, int font, int size){
        JButton bt = new JButton(noiDung);
        bt.setFont(new Font(fontChu, font, size));
        bt.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        Dimension buttonSize = new Dimension(110, 35);
        bt.setPreferredSize(buttonSize);
        bt.setFocusPainted(false);
        ganIcon(bt, noiDung, size + 3);
        return bt;
    }

    public JButton Button(String noiDung){
        JButton bt = new JButton(noiDung);
        bt.setFont(fontNut);
        bt.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        Dimension buttonSize = new Dimension(110, 25);
        bt.setPreferredSize(buttonSize);
        bt.setFocusPainted(false);
        ganIcon(bt, noiDung, 16);
        return bt;
    }

    private void ganIcon(JButton bt, String noiDung, int kichThuoc){
        Icon icon = taoIcon.tuTen(noiDung, kichThuoc);
        if(icon != null){
            bt.setIcon(icon);
            bt.setIconTextGap(8);
        }
    }
}