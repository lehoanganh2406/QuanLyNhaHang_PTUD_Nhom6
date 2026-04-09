package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import entity.TaiKhoan;

public class TrangChu_GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private Image backgroundImage;

    private CardLayout cardLayout;
    private JPanel pnContent;

    public TrangChu_GUI() {
        setTitle("Trang chủ - Quản Lý Nhà Hàng");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        // Ảnh nền trang chủ
        backgroundImage = new ImageIcon("img/trangchu.png").getImage();

        // Panel gốc vẽ ảnh nền phía dưới
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setOpaque(true);
        setContentPane(backgroundPanel);


        // ── Vùng nội dung trung tâm dùng CardLayout để swap panel ──
        cardLayout = new CardLayout();
        pnContent  = new JPanel(cardLayout);
        pnContent.setOpaque(false);

        // Card: trang chủ (trong suốt – hiện ảnh nền)
        JPanel pnHome = new JPanel();
        pnHome.setOpaque(false);
        pnContent.add(pnHome, "TrangChu");

        // Card: quản lý khu vực
        pnContent.add(new QLKhuVuc_GUI(), "KhuVuc");
        // Card: quản lý bàn
        pnContent.add(new QLBan_GUI(), "Ban");
        // Card: quản lý khuyến mãi
        pnContent.add(new QLKhuyenMai_GUI(), "KhuyenMai");
        // Các card Thống kê
        pnContent.add(new TK_TheoCa_GUI(), "TK_TheoCa");
        pnContent.add(new TK_PhanTichBH_GUI(), "TK_PhanTich");
        pnContent.add(new TK_TongKetBH_GUI(), "TK_TongKet");

        // ── Thanh menu ──
        Pn_ThanhMenu menu = new Pn_ThanhMenu();

        // Truyền callback điều hướng cho menu
        menu.setPageSwitcher(key -> {
            cardLayout.show(pnContent, key);
            menu.hideSubMenu();
        });

        backgroundPanel.add(menu,      BorderLayout.NORTH);
        backgroundPanel.add(pnContent, BorderLayout.CENTER);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }
}