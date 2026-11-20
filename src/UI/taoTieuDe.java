package UI;

import javax.swing.*;
import java.awt.*;

public class taoTieuDe {
    private final Font fontTieuDe = new Font("Arial", Font.BOLD, 20);
    public JLabel label(String noiDung){
        JLabel lb = new JLabel(noiDung);
        lb.setFont(fontTieuDe);
        return lb;
    }
}
