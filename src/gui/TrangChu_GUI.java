package gui;

import java.awt.BorderLayout;
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import entity.TaiKhoan;

public class TrangChu_GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private Image backgroundImage;
    private TaiKhoan taiKhoanDangNhap;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Timer timerKiemTraQuaGio;

    private final Map<String, JPanel> pageCache = new HashMap<>();

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
        backgroundImage = new ImageIcon("img/trangchu.png").getImage();

        JPanel panel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        return panel;
    }

    public void showPage(String pageName) {
        if (pageName == null || pageName.trim().isEmpty()) return;

        System.out.println("Đang chuyển tới: " + pageName);

        if ("TrangChu_GUI".equals(pageName)) {
            cardLayout.show(contentPanel, "TrangChu_GUI");
            return;
        }

        if (!pageCache.containsKey(pageName)) {
            JPanel page = createPageFromOldFrame(pageName);

            if (page == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Trang " + pageName + " chưa tạo được.\nKiểm tra class có tồn tại không hoặc constructor có TaiKhoan không.",
                        "Lỗi chuyển trang",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            contentPanel.add(page, pageName);
            pageCache.put(pageName, page);
        }

        cardLayout.show(contentPanel, pageName);
        contentPanel.revalidate();
        contentPanel.repaint();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrangChu_GUI(null).setVisible(true);
        });
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
    }
    private void batDauKiemTraPhieuQuaGio() {
        timerKiemTraQuaGio = new Timer(60 * 1000, e -> kiemTraPhieuQuaGio());
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
                String maBan = row[1];
                String tenKhach = row[2];
                String sdt = row[3];
                String gioDen = row[4];

                int chon = JOptionPane.showConfirmDialog(
                        this,
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
                    dao.capNhatTrangThai(maPhieu, "Quá giờ");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}