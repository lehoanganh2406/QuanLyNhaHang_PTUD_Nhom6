
package gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import entity.TaiKhoan;

public class TrangChu_GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private Image backgroundImage;
    private TaiKhoan taiKhoanDangNhap;

    public TrangChu_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Trang chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        backgroundImage = new ImageIcon("img/trangchu.png").getImage();

        JPanel backgroundPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        backgroundPanel.add(menu, BorderLayout.NORTH);

        setLocationRelativeTo(null);
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }
}
