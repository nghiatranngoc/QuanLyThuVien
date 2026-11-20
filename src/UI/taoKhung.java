package UI;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class taoKhung {
    public JPanel panel(){
        JPanel pn = new JPanel(new GridBagLayout());
        pn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(BevelBorder.LOWERED),
            BorderFactory.createEmptyBorder(16, 26, 16, 26)
        ));
        return pn;
    }
}
