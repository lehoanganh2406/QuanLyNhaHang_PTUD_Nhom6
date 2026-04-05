package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Pn_ThanhMenu extends JPanel {

    private final Color BG_TOP = new Color(239, 211, 158);
    private final Color BG_HOVER = new Color(231, 191, 120);
    private final Color BG_SELECTED = new Color(220, 170, 80);
    private final Color BG_SUB = new Color(238, 194, 120);
    private final Color BG_SUB_HOVER = new Color(245, 218, 170);
    private final Color BORDER_COLOR = new Color(201, 155, 86);

    private final JPanel pnHeader;
    private final JPanel pnTopMenu;
    private final JPanel pnUserInfo;
    private final JPanel pnPopupLayer;

    private MenuItemPanel selectedMenu;
    private JPanel currentSubPanel;

    private final JLabel lblUserIcon;
    private final JLabel lblUserText;

    public Pn_ThanhMenu() {
        setLayout(null);
        setOpaque(false);

        pnTopMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnTopMenu.setOpaque(false);
        pnTopMenu.setBorder(new EmptyBorder(0, 8, 0, 8));

        pnUserInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnUserInfo.setOpaque(false);
        pnUserInfo.setBorder(new EmptyBorder(2, 8, 2, 16));

        lblUserIcon = new JLabel();
        lblUserIcon.setIcon(loadIcon("img/mn_acout.png", 32, 32));

        lblUserText = new JLabel("Quản lý: HoàngAnh");
        lblUserText.setFont(new Font("Times New Roman", Font.BOLD, 18));
        lblUserText.setForeground(Color.BLACK);

        pnUserInfo.add(lblUserIcon);
        pnUserInfo.add(lblUserText);

        pnHeader = new JPanel(new BorderLayout());
        pnHeader.setBackground(BG_TOP);
        pnHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        pnHeader.add(pnTopMenu, BorderLayout.WEST);
        pnHeader.add(pnUserInfo, BorderLayout.EAST);

        pnPopupLayer = new JPanel(null);
        pnPopupLayer.setOpaque(false);

        add(pnHeader);
        add(pnPopupLayer);

        initMenu();
        initResponsiveBehavior();
    }

    private void initResponsiveBehavior() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutChildren();
                updatePopupPosition();
                revalidate();
                repaint();
            }
        });
    }

    private void layoutChildren() {
        int headerHeight = 42;
        pnHeader.setBounds(0, 0, getWidth(), headerHeight);
        pnPopupLayer.setBounds(0, headerHeight, getWidth(), Math.max(0, getHeight() - headerHeight));
    }

    @Override
    public Dimension getPreferredSize() {
        int headerHeight = 42;
        int popupHeight = 0;

        if (currentSubPanel != null && currentSubPanel.isVisible()) {
            popupHeight = currentSubPanel.getHeight();
        }

        return new Dimension(100, headerHeight + popupHeight);
    }

    private void initMenu() {
        MenuItemPanel mnHeThong = new MenuItemPanel("Hệ thống", "img/mn_hethong.png");
        mnHeThong.addSubItem("Trang chủ", () -> System.out.println("Trang chủ"));
        mnHeThong.addSubItem("Quản lý tài khoản", () -> System.out.println("Quản lý tài khoản"));
        mnHeThong.addSubItem("Đăng xuất", () -> System.out.println("Đăng xuất"));
        mnHeThong.addSubItem("Hỗ trợ", () -> System.out.println("Hỗ trợ"));

        MenuItemPanel mnDanhMuc = new MenuItemPanel("Danh mục", "img/mn_danhmuc.png");
        mnDanhMuc.addSubItem("Thực đơn", () -> System.out.println("Thực đơn"));
        mnDanhMuc.addSubItem("Khu vực", () -> System.out.println("Khu vực"));
        mnDanhMuc.addSubItem("Nhân viên", () -> System.out.println("Nhân viên"));
        mnDanhMuc.addSubItem("Khách hàng", () -> System.out.println("Khách hàng"));
        mnDanhMuc.addSubItem("Khuyến mãi", () -> System.out.println("Khuyến mãi"));
        mnDanhMuc.addSubItem("Bàn", () -> System.out.println("Bàn"));

        MenuItemPanel mnXuLy = new MenuItemPanel("Xử lý", "img/mn_xuly.png");
        mnXuLy.addSubItem("Order", () -> System.out.println("Order"));
        mnXuLy.addSubItem("Đặt bàn", () -> System.out.println("Đặt bàn"));
        mnXuLy.addSubItem("Hóa đơn", () -> System.out.println("Hóa đơn"));

        MenuItemPanel mnTraCuu = new MenuItemPanel("Tra cứu", "img/mn_tracuu.png");

        MenuItemPanel mnThongKe = new MenuItemPanel("Thống kê", "img/mn_thongke.png");
        mnThongKe.addSubItem("Thống kê theo ca", () -> System.out.println("Thống kê theo ca"));
        mnThongKe.addSubItem("Phân tích bán hàng", () -> System.out.println("Phân tích bán hàng"));
        mnThongKe.addSubItem("Tổng kết bán hàng", () -> System.out.println("Tổng kết bán hàng"));

        addMenu(mnHeThong);
        addMenu(mnDanhMuc);
        addMenu(mnXuLy);
        addMenu(mnTraCuu);
        addMenu(mnThongKe);
    }

    private void addMenu(MenuItemPanel menu) {
        pnTopMenu.add(menu);

        MouseAdapter clickEvent = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedMenu == menu && currentSubPanel != null && currentSubPanel.isVisible()) {
                    hideSubMenu();
                    return;
                }

                selectMenu(menu);
                toggleSubMenu(menu);
            }
        };

        attachMouseListenerRecursive(menu, clickEvent);
    }

    private void attachMouseListenerRecursive(Component comp, MouseListener listener) {
        comp.addMouseListener(listener);
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                attachMouseListenerRecursive(child, listener);
            }
        }
    }

    private void selectMenu(MenuItemPanel menu) {
        if (selectedMenu != null && selectedMenu != menu) {
            selectedMenu.setSelected(false);
        }
        selectedMenu = menu;
        selectedMenu.setSelected(true);
    }

    private void hideSubMenu() {
        if (selectedMenu != null) {
            selectedMenu.setSelected(false);
            selectedMenu = null;
        }

        pnPopupLayer.removeAll();
        currentSubPanel = null;
        revalidate();
        repaint();
    }

    private void toggleSubMenu(MenuItemPanel menu) {
        pnPopupLayer.removeAll();
        currentSubPanel = null;

        if (menu.subItems.isEmpty()) {
            revalidate();
            repaint();
            return;
        }

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(BG_SUB);
        subPanel.setOpaque(true);
        subPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(2, 2, 2, 2)
        ));

        for (int i = 0; i < menu.subItems.size(); i++) {
            SubMenuItemPanel item = menu.subItems.get(i);
            subPanel.add(item);

            if (i < menu.subItems.size() - 1) {
                JPanel line = new JPanel();
                line.setBackground(BORDER_COLOR);
                line.setPreferredSize(new Dimension(1, 1));
                line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                line.setMinimumSize(new Dimension(1, 1));
                line.setAlignmentX(Component.LEFT_ALIGNMENT);
                subPanel.add(line);
            }
        }

        currentSubPanel = subPanel;
        pnPopupLayer.add(subPanel);

        updatePopupPosition();
        revalidate();
        repaint();
    }

    private void updatePopupPosition() {
        if (selectedMenu == null || currentSubPanel == null) {
            return;
        }

        int itemHeight = 38;
        int lineHeight = 1;
        int count = selectedMenu.subItems.size();
        int height = count * itemHeight + Math.max(0, count - 1) * lineHeight + 6;
        int width = selectedMenu.getWidth();

        Point p = SwingUtilities.convertPoint(selectedMenu.getParent(), selectedMenu.getLocation(), pnPopupLayer);
        currentSubPanel.setBounds(p.x, 0, width, height);

        currentSubPanel.revalidate();
        currentSubPanel.repaint();
    }

    public void setUserInfo(String text, String iconPath) {
        lblUserText.setText(text == null ? "" : text);

        if (iconPath != null && !iconPath.trim().isEmpty()) {
            lblUserIcon.setIcon(loadIcon(iconPath, 18, 18));
        } else {
            lblUserIcon.setIcon(null);
        }
    }

    class MenuItemPanel extends JPanel {
        private final JLabel lblIcon;
        private final JLabel lblText;
        private final JLabel lblArrow;
        private boolean selected = false;
        private final List<SubMenuItemPanel> subItems = new ArrayList<>();

        public MenuItemPanel(String text, String iconPath) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 6, 8));
            setBackground(BG_TOP);
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(170, 42));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 185, 120)),
                    new EmptyBorder(0, 14, 0, 14)
            ));

            lblIcon = new JLabel();
            lblIcon.setIcon(loadIcon(iconPath, 16, 16));

            lblText = new JLabel(text);
            lblText.setFont(new Font("Times New Roman", Font.BOLD, 18));
            lblText.setForeground(Color.BLACK);

            lblArrow = new JLabel();
            lblArrow.setIcon(loadIcon("img/mn_muiten.png", 24, 24)); // chỉnh size tùy bạn

            add(lblIcon);
            add(lblText);
            

            MouseAdapter hoverEvent = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!selected) {
                        setBackground(BG_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!selected) {
                        setBackground(BG_TOP);
                    }
                }
            };

            attachMouseListenerRecursive(this, hoverEvent);
        }

        public void addSubItem(String text, Runnable action) {
            if (subItems.isEmpty()) {
                add(lblArrow);   // chỉ add khi có submenu
                revalidate();
                repaint();
            }
            subItems.add(new SubMenuItemPanel(text, action));
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            setBackground(selected ? BG_SELECTED : BG_TOP);
        }
    }

    class SubMenuItemPanel extends JPanel {
        public SubMenuItemPanel(String text, Runnable action) {
            setLayout(new BorderLayout());
            setBackground(BG_SUB);
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            setPreferredSize(new Dimension(0, 38));
            setMinimumSize(new Dimension(0, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setBorder(new EmptyBorder(0, 12, 0, 10));

            JLabel lblText = new JLabel(text);
            lblText.setFont(new Font("Times New Roman", Font.BOLD, 18));
            lblText.setForeground(Color.BLACK);

            add(lblText, BorderLayout.WEST);

            MouseAdapter event = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(BG_SUB_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(BG_SUB);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (action != null) {
                        action.run();
                    }
                }
            };

            attachMouseListenerRecursive(this, event);
        }
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}