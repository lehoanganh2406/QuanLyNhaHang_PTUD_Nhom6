package gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TrangChu_GUI extends JFrame {

    private Image backgroundImage;

    public TrangChu_GUI() {
        setTitle("Trang chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Đọc ảnh từ thư mục img cùng cấp với src
        backgroundImage = new ImageIcon("img/trangchu.png").getImage();

        // Panel nền có vẽ ảnh
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Menu trên cùng
        Pn_ThanhMenu menu = new Pn_ThanhMenu();
        backgroundPanel.add(menu, BorderLayout.NORTH);

        setExtendedState(JFrame.MAXIMIZED_BOTH); // full màn hình
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrangChu_GUI frame = new TrangChu_GUI();
            frame.setVisible(true);
        });
    }
}