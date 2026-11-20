package UI;

import javax.swing.*;
import java.awt.*;

public class taoDongNoiDung {
    private final Font fontNoiDung = new Font("Arial", Font.PLAIN, 13);
    public JLabel label(String noiDung){
        JLabel lb = new JLabel(noiDung);
        lb.setFont(fontNoiDung);
        return lb;
    }

    public JLabel Label(String noiDung, String fontChu, int font, int size){
        JLabel lb = new JLabel(noiDung);
        lb.setFont(new Font(fontChu, font, size));
        return lb;
    }
}
