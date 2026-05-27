package gui;

import java.awt.BorderLayout;
import dao.Ban_DAO;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import dao.PhieuDatBan_DAO;
import java.util.ArrayList;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import java.awt.KeyboardFocusManager;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import entity.TaiKhoan;

public class TrangChu_GUI extends JFrame {



    private Image backgroundImage;
    private TaiKhoan taiKhoanDangNhap;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Timer timerKiemTraQuaGio;

    private final Map<String, JPanel> pageCache = new HashMap<>();
    private final java.util.Set<String> daThongBao = new java.util.HashSet<>();

    public TrangChu_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Trang chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        menu.setNavigator(pageName -> showPage(pageName));

        add(menu, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel trangChuPanel = createTrangChuPanel();

        contentPanel.add(trangChuPanel, "TrangChu_GUI");
        pageCache.put("TrangChu_GUI", trangChuPanel);

        add(contentPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        batDauKiemTraPhieuQuaGio();
    }

    private JPanel createTrangChuPanel() {
        backgroundImage = new ImageIcon(getClass().getResource("/trangchu.png")).getImage();

        JPanel panel = new JPanel(null) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JPanel pnChucNang = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 70, 0));
        pnChucNang.setOpaque(false);

        pnChucNang.add(taoNutTrangChu("img/datban.png", "ĐẶT BÀN", "DatBan_GUI"));
        pnChucNang.add(taoNutTrangChu("img/order.png", "ORDER", "Order_Ban_GUI"));
        pnChucNang.add(taoNutTrangChu("img/tracuu.png", "TRA CỨU", "TraCuu_GUI"));
        pnChucNang.add(taoNutTrangChu("img/ketca.png", "KẾT CA", "ThongKeTheoCa_GUI"));

        panel.add(pnChucNang);

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                pnChucNang.setBounds(0, panel.getHeight() - 180, panel.getWidth(), 150);
            }
        });

        return panel;
    }
    private JPanel taoNutTrangChu(
            String iconPath,
            String text,
            String pageName
    ) {

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setPreferredSize(new java.awt.Dimension(130, 145));
        p.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel();
        lblIcon.setHorizontalAlignment(
                javax.swing.SwingConstants.CENTER
        );

        // "img/abc.png" -> "/abc.png"
        iconPath = "/" + iconPath.replace("img/", "");

        java.net.URL imgURL =
                getClass().getResource(iconPath);

        if (imgURL != null) {

            ImageIcon icon =
                    new ImageIcon(imgURL);

            Image img =
                    icon.getImage()
                            .getScaledInstance(
                                    125,
                                    90,
                                    Image.SCALE_SMOOTH
                            );

            lblIcon.setIcon(
                    new ImageIcon(img)
            );
        }

        JLabel lblText = new JLabel(
                text,
                javax.swing.SwingConstants.CENTER
        );

        lblText.setFont(
                new java.awt.Font(
                        "SansSerif",
                        java.awt.Font.BOLD,
                        18
                )
        );

        lblText.setForeground(
                new java.awt.Color(235, 205, 135)
        );

        p.add(lblIcon, BorderLayout.CENTER);
        p.add(lblText, BorderLayout.SOUTH);

        p.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {
                        showPage(pageName);
                    }
                }
        );

        return p;
    }

    public void showPage(String pageName) {

        if (pageName == null || pageName.trim().isEmpty()) return;

        System.out.println("Đang chuyển tới: " + pageName);

        // KHÔNG remove TrangChu
        if ("TrangChu_GUI".equals(pageName)) {
            cardLayout.show(contentPanel, "TrangChu_GUI");
            contentPanel.revalidate();
            contentPanel.repaint();
            SwingUtilities.invokeLater(() -> kiemTraPhieuQuaGio());
            return;
        }

        // Chỉ remove các page khác
        if (pageCache.containsKey(pageName)) {
            JPanel oldPage = pageCache.get(pageName);
            contentPanel.remove(oldPage);
            pageCache.remove(pageName);
        }

        JPanel page = createPageFromOldFrame(pageName);

        if (page == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Trang " + pageName + " chưa tạo được.",
                    "Lỗi chuyển trang",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        contentPanel.add(page, pageName);
        pageCache.put(pageName, page);

        cardLayout.show(contentPanel, pageName);
        contentPanel.revalidate();
        contentPanel.repaint();

        SwingUtilities.invokeLater(() -> kiemTraPhieuQuaGio());
    }

    private JPanel createPageFromOldFrame(String className) {
        try {
            Class<?> clazz = Class.forName("gui." + className);

            Object obj;

            try {
                Constructor<?> cons = clazz.getConstructor(TaiKhoan.class);
                obj = cons.newInstance(taiKhoanDangNhap);
            } catch (NoSuchMethodException e) {
                Constructor<?> cons = clazz.getConstructor();
                obj = cons.newInstance();
            }

            // Nếu sau này bạn đổi trang thành JPanel thì chạy cực mượt
            if (obj instanceof JPanel) {
                return (JPanel) obj;
            }

            // Tạm hỗ trợ trang cũ đang extends JFrame
            if (obj instanceof JFrame) {
                JFrame frame = (JFrame) obj;

                int w = Math.max(1200, contentPanel.getWidth());
                int h = Math.max(700, contentPanel.getHeight());

                frame.setSize(w, h);
                frame.doLayout();

                Container oldContent = frame.getContentPane();
                removeNestedMenu(oldContent);

                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.add(oldContent, BorderLayout.CENTER);

                frame.setContentPane(new JPanel());
                frame.dispose();

                return wrapper;
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void removeNestedMenu(Container container) {
        if (container == null) return;

        for (Component comp : container.getComponents()) {
            if (comp instanceof Pn_ThanhMenu) {
                container.remove(comp);
            } else if (comp instanceof Container) {
                removeNestedMenu((Container) comp);
            }
        }

        container.revalidate();
        container.repaint();
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    
    public void showCustomPage(String pageName, JPanel page) {
        if (pageName == null || page == null) return;

        if (pageCache.containsKey(pageName)) {
            contentPanel.remove(pageCache.get(pageName));
            pageCache.remove(pageName);
        }

        contentPanel.add(page, pageName);
        pageCache.put(pageName, page);

        cardLayout.show(contentPanel, pageName);
        contentPanel.revalidate();
        contentPanel.repaint();
        SwingUtilities.invokeLater(() -> kiemTraPhieuQuaGio());
    }
    private void batDauKiemTraPhieuQuaGio() {
        timerKiemTraQuaGio = new Timer(10 * 1000, e -> kiemTraPhieuQuaGio());
        timerKiemTraQuaGio.setInitialDelay(3000);
        timerKiemTraQuaGio.start();
    }

    private void kiemTraPhieuQuaGio() {
        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
            ArrayList<String[]> ds = dao.getPhieuTreQua30Phut();

            if (ds == null || ds.isEmpty()) return;

            for (String[] row : ds) {
                String maPhieu = row[0];

                if (daThongBao.contains(maPhieu)) continue;

                String maBan = row[1];
                String tenKhach = row[2];
                String sdt = row[3];
                String gioDen = row[4];
                Window activeWindow = KeyboardFocusManager
                        .getCurrentKeyboardFocusManager()
                        .getActiveWindow();

                Component parent = activeWindow != null ? activeWindow : this;

                int chon = JOptionPane.showConfirmDialog(
                        parent,
                        "Phiếu đặt bàn đã trễ quá 30 phút.\n\n"
                                + "Mã phiếu: " + maPhieu + "\n"
                                + "Bàn: " + maBan + "\n"
                                + "Khách: " + tenKhach + "\n"
                                + "SĐT: " + sdt + "\n"
                                + "Giờ đến: " + gioDen + "\n\n"
                                + "Bạn có muốn gia hạn thêm 30 phút không?",
                        "Cảnh báo quá giờ đặt bàn",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (chon == JOptionPane.YES_OPTION) {

                    dao.giaHanThoiGianCho(maPhieu);

                } else {

                    dao.capNhatTrangThai(
                            maPhieu,
                            "Quá giờ"
                    );

                    Ban_DAO banDAO =
                            new Ban_DAO();

                    banDAO.capNhatTrangThaiBan(
                            maBan,
                            "Bàn trống"
                    );
                }

                SwingUtilities.invokeLater(() -> {

                    if(pageCache.containsKey(
                            "DatBan_GUI"
                    )){

                        Component current =
                                null;

                        for(Component c :
                                contentPanel.getComponents()){

                            if(c.isVisible()){

                                current = c;
                                break;
                            }
                        }

                        contentPanel.remove(
                                pageCache.get(
                                        "DatBan_GUI"
                                )
                        );

                        pageCache.remove(
                                "DatBan_GUI"
                        );

                        JPanel page =
                                createPageFromOldFrame(
                                        "DatBan_GUI"
                                );

                        if(page != null){

                            contentPanel.add(
                                    page,
                                    "DatBan_GUI"
                            );

                            pageCache.put(
                                    "DatBan_GUI",
                                    page
                            );

                            if(current != null){

                                current.setVisible(
                                        true
                                );
                            }

                            contentPanel.revalidate();
                            contentPanel.repaint();
                        }
                    }
                });

                daThongBao.add(maPhieu);
            
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public Component getCurrentPage() {

        if (contentPanel.getComponentCount() > 0) {

            return contentPanel.getComponent(0);
        }

        return null;
    }
}