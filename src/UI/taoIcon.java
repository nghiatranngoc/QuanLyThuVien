package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.text.Normalizer;

public class taoIcon implements Icon{
    public enum Loai{
        THEM(new Color(46, 125, 50)),
        SUA(new Color(25, 118, 210)),
        XOA(new Color(198, 40, 40)),
        TIM_KIEM(new Color(25, 118, 210)),
        DANG_NHAP(new Color(25, 118, 210)),
        DANG_KY(new Color(46, 125, 50)),
        QUAY_LAI(new Color(97, 97, 97)),
        XAC_NHAN(new Color(46, 125, 50)),
        HUY(new Color(198, 40, 40)),
        SINH_VIEN(new Color(25, 118, 210)),
        SACH(new Color(121, 85, 72)),
        MUON_TRA(new Color(245, 124, 0));

        private final Color mau;
        Loai(Color mau){
            this.mau = mau;
        }
    }

    private final Loai loai;
    private final int size;

    public taoIcon(Loai loai, int size){
        this.loai = loai;
        this.size = size;
    }

    public static Icon tuTen(String noiDung, int size){
        if(noiDung == null) return null;
        String s = Normalizer.normalize(noiDung, Normalizer.Form.NFC).toLowerCase().trim();

        if(s.contains("mượn trả")) return new taoIcon(Loai.MUON_TRA, size);
        if(s.contains("sinh viên")) return new taoIcon(Loai.SINH_VIEN, size);
        if(s.contains("sách")) return new taoIcon(Loai.SACH, size);
        if(s.contains("đăng nhập")) return new taoIcon(Loai.DANG_NHAP, size);
        if(s.contains("đăng ký")) return new taoIcon(Loai.DANG_KY, size);
        if(s.contains("quay lại")) return new taoIcon(Loai.QUAY_LAI, size);
        if(s.contains("tìm")) return new taoIcon(Loai.TIM_KIEM, size);
        if(s.contains("thêm")) return new taoIcon(Loai.THEM, size);
        if(s.contains("sửa")) return new taoIcon(Loai.SUA, size);
        if(s.contains("xóa")) return new taoIcon(Loai.XOA, size);
        if(s.contains("hủy")) return new taoIcon(Loai.HUY, size);
        if(s.contains("xác nhận") || s.contains("thay đổi")) return new taoIcon(Loai.XAC_NHAN, size);
        return null;
    }

    @Override
    public int getIconWidth(){
        return size;
    }

    @Override
    public int getIconHeight(){
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.setColor(loai.mau);
        float net = Math.max(1.6f, size / 9f);
        g2.setStroke(new BasicStroke(net, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double s = size;

        switch(loai){
            case THEM -> {
                ln(g2, s, 0.2, 0.5, 0.8, 0.5);
                ln(g2, s, 0.5, 0.2, 0.5, 0.8);
            }
            case XAC_NHAN -> {
                Path2D tick = new Path2D.Double();
                tick.moveTo(0.15 * s, 0.55 * s);
                tick.lineTo(0.4 * s, 0.78 * s);
                tick.lineTo(0.85 * s, 0.25 * s);
                g2.draw(tick);
            }
            case HUY -> {
                ln(g2, s, 0.22, 0.22, 0.78, 0.78);
                ln(g2, s, 0.78, 0.22, 0.22, 0.78);
            }
            case QUAY_LAI -> {
                ln(g2, s, 0.85, 0.5, 0.2, 0.5);
                ln(g2, s, 0.2, 0.5, 0.45, 0.25);
                ln(g2, s, 0.2, 0.5, 0.45, 0.75);
            }
            case DANG_NHAP -> {
                ln(g2, s, 0.12, 0.5, 0.68, 0.5);
                ln(g2, s, 0.68, 0.5, 0.46, 0.28);
                ln(g2, s, 0.68, 0.5, 0.46, 0.72);
                ln(g2, s, 0.87, 0.2, 0.87, 0.8);
            }
            case TIM_KIEM -> {
                g2.draw(new Ellipse2D.Double(0.12 * s, 0.12 * s, 0.5 * s, 0.5 * s));
                ln(g2, s, 0.58, 0.58, 0.88, 0.88);
            }
            case SUA -> {
                g2.setStroke(new BasicStroke(net * 1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                ln(g2, s, 0.3, 0.7, 0.75, 0.25);
                g2.setStroke(new BasicStroke(net, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                ln(g2, s, 0.15, 0.88, 0.85, 0.88);
            }
            case XOA -> {
                ln(g2, s, 0.15, 0.25, 0.85, 0.25);
                ln(g2, s, 0.4, 0.15, 0.6, 0.15);
                ln(g2, s, 0.4, 0.15, 0.4, 0.25);
                ln(g2, s, 0.6, 0.15, 0.6, 0.25);
                Path2D than = new Path2D.Double();
                than.moveTo(0.25 * s, 0.33 * s);
                than.lineTo(0.3 * s, 0.88 * s);
                than.lineTo(0.7 * s, 0.88 * s);
                than.lineTo(0.75 * s, 0.33 * s);
                g2.draw(than);
            }
            case DANG_KY, SINH_VIEN -> {
                g2.draw(new Ellipse2D.Double(0.35 * s, 0.12 * s, 0.3 * s, 0.3 * s));
                g2.draw(new Arc2D.Double(0.18 * s, 0.52 * s, 0.64 * s, 0.64 * s, 0, 180, Arc2D.OPEN));
            }
            case SACH -> {
                g2.draw(new RoundRectangle2D.Double(0.2 * s, 0.12 * s, 0.6 * s, 0.76 * s, 0.1 * s, 0.1 * s));
                ln(g2, s, 0.34, 0.12, 0.34, 0.88);
                ln(g2, s, 0.46, 0.35, 0.68, 0.35);
                ln(g2, s, 0.46, 0.5, 0.68, 0.5);
            }
            case MUON_TRA -> {
                ln(g2, s, 0.15, 0.33, 0.85, 0.33);
                ln(g2, s, 0.85, 0.33, 0.68, 0.18);
                ln(g2, s, 0.85, 0.33, 0.68, 0.48);
                ln(g2, s, 0.85, 0.7, 0.15, 0.7);
                ln(g2, s, 0.15, 0.7, 0.32, 0.55);
                ln(g2, s, 0.15, 0.7, 0.32, 0.85);
            }
        }
        g2.dispose();
    }

    private void ln(Graphics2D g2, double s, double x1, double y1, double x2, double y2){
        g2.draw(new Line2D.Double(x1 * s, y1 * s, x2 * s, y2 * s));
    }
}
