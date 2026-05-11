package gui;

import java.awt.*;
import dao.CaLamViec_DAO;
import entity.CaLamViec;
import digLog.DongCa_DigLog;

import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import entity.TaiKhoan;

public class Pn_ThanhMenu extends JPanel {

    private final Color BG_TOP = new Color(239, 211, 158);
    private final Color BG_HOVER = new Color(231, 191, 120);
    private final Color BG_SELECTED = new Color(220, 170, 80);

    private final Color BG_SUB = new Color(238, 194, 120);
    private final Color BG_SUB_HOVER = new Color(245, 218, 170);

    private final Color BORDER_COLOR = new Color(201, 155, 86);

    private final Color FG_NORMAL = Color.BLACK;
    private final Color FG_DISABLED = new Color(140, 140, 140);
    private final Color BG_DISABLED = new Color(232, 220, 198);

    private final JPanel pnHeader;
    private final JPanel pnTopMenu;
    private final JPanel pnUserInfo;

    private final JLabel lblUserIcon;
    private final JLabel lblUserText;

    private MenuItemPanel selectedMenu;
    private JPopupMenu currentPopupMenu;
    private static final int MENU_MIN_WIDTH = 145;
    private static final int MENU_HEIGHT = 42;
    private static final int SUB_ITEM_HEIGHT = 38;

    private final TaiKhoan taiKhoanDangNhap;
    public interface Navigator {
        void goTo(String pageName);
    }

    private Navigator navigator;

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public Pn_ThanhMenu(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;

        setLayout(null);
        setOpaque(false);

        pnTopMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnTopMenu.setOpaque(false);
        pnTopMenu.setBorder(new EmptyBorder(0, 8, 0, 8));

        pnUserInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnUserInfo.setOpaque(false);
        pnUserInfo.setBorder(new EmptyBorder(2, 8, 2, 16));

        lblUserIcon = new JLabel();
        lblUserIcon.setIcon(loadIcon("img/mn_acout.png", 30, 30));

        lblUserText = new JLabel(buildUserText());
        lblUserText.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblUserText.setForeground(Color.BLACK);

        pnUserInfo.add(lblUserIcon);
        pnUserInfo.add(lblUserText);

        pnHeader = new JPanel(new BorderLayout());
        pnHeader.setBackground(BG_TOP);
        pnHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        pnHeader.add(pnTopMenu, BorderLayout.WEST);
        pnHeader.add(pnUserInfo, BorderLayout.EAST);

        add(pnHeader);

        initMenu();
        initResponsiveBehavior();
        installGlobalHidePopup();
    }

    private void initResponsiveBehavior() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutChildren();
                revalidate();
                repaint();
            }
        });
    }

    private void installGlobalHidePopup() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent)) return;
            MouseEvent me = (MouseEvent) event;
            if (me.getID() != MouseEvent.MOUSE_PRESSED) return;

            if (currentPopupMenu == null || !currentPopupMenu.isVisible()) return;

            Object src = me.getSource();
            if (!(src instanceof Component)) return;

            Component clicked = (Component) src;

            if (SwingUtilities.isDescendingFrom(clicked, this)) return;
            if (SwingUtilities.isDescendingFrom(clicked, currentPopupMenu)) return;

            hideSubMenu();
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    private void layoutChildren() {
        pnHeader.setBounds(0, 0, getWidth(), 42);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 42);
    }

    private String buildUserText() {
        String hoTen = "";
        String vaiTro = "";

        try {
            if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null) {
                if (taiKhoanDangNhap.getMaNV().getHoTen() != null) {
                    hoTen = taiKhoanDangNhap.getMaNV().getHoTen().trim();
                }
                if (taiKhoanDangNhap.getMaNV().getChucVu() != null) {
                    vaiTro = taiKhoanDangNhap.getMaNV().getChucVu().trim();
                }
            }

            if ((vaiTro == null || vaiTro.isEmpty()) && taiKhoanDangNhap != null && taiKhoanDangNhap.getPhanQuyen() != null) {
                vaiTro = taiKhoanDangNhap.getPhanQuyen().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hoTen.isEmpty()) hoTen = "Người dùng";
        if (vaiTro.isEmpty()) return hoTen;

        return vaiTro + ": " + hoTen;
    }

    private void initMenu() {
        MenuItemPanel mnHeThong = new MenuItemPanel("Hệ thống", "img/mn_hethong.png");
        mnHeThong.addSubItem("Trang chủ", "TrangChu_GUI");
        mnHeThong.addSubItem("Quản lý tài khoản", "TaiKhoan_GUI");
        mnHeThong.addSubItem("Quản lý ca", "QuanLyCa_GUI");
        mnHeThong.addSubItem("Đăng xuất", "DangNhap_GUI");
        mnHeThong.addSubItem("Hỗ trợ", "__HOTRO__");

        MenuItemPanel mnDanhMuc = new MenuItemPanel("Danh mục", "img/mn_danhmuc.png");
        mnDanhMuc.addSubItem("Thực đơn", "ThucDon_GUI");
        mnDanhMuc.addSubItem("Khu vực", "KhuVuc_GUI");
        mnDanhMuc.addSubItem("Nhân viên", "NhanVien_GUI");
        mnDanhMuc.addSubItem("Khách hàng", "KhachHang_GUI");
        mnDanhMuc.addSubItem("Khuyến mãi", "KhuyenMai_GUI");
        mnDanhMuc.addSubItem("Bàn", "Ban_GUI");

        MenuItemPanel mnXuLy = new MenuItemPanel("Xử lý", "img/mn_xuly.png");
        mnXuLy.addSubItem("Order", "Order_Ban_GUI");
        mnXuLy.addSubItem("Đặt bàn", "DatBan_GUI");
        mnXuLy.addSubItem("Bar/Bếp", "Bar_Bep_GUI");
        mnXuLy.addSubItem("Hóa đơn", "HoaDon_GUI");

        MenuItemPanel mnTraCuu = new MenuItemPanel("Tra cứu", "img/mn_tracuu.png");
        mnTraCuu.addDirectPage("TraCuu_GUI");

        MenuItemPanel mnThongKe = new MenuItemPanel("Thống kê", "img/mn_thongke.png");
        mnThongKe.addSubItem("Thống kê theo ca", "ThongKeTheoCa_GUI");
        mnThongKe.addSubItem("Phân tích bán hàng", "PhanTichBanHang_GUI");
        mnThongKe.addSubItem("Tổng kết bán hàng", "TongKetBanHang_GUI");

        applyPermissionForRole(mnHeThong, mnDanhMuc, mnXuLy, mnTraCuu, mnThongKe);
        pnTopMenu.add(createLogoHome());


        if (isLeTan()) {
            addMenu(mnHeThong); // Trang chủ, Đăng xuất, Hỗ trợ
            addMenu(mnXuLy);    // Order, Đặt bàn
            addMenu(mnTraCuu);  // Tra cứu
            addMenu(mnThongKe); // Thống kê theo ca
        } else {
            addMenu(mnHeThong);
            addMenu(mnDanhMuc);
            addMenu(mnXuLy);
            addMenu(mnTraCuu);
            addMenu(mnThongKe);
        }

        SwingUtilities.invokeLater(() -> {
            updateAllMenuWidths();
            revalidate();
            repaint();
        });
    }

    private void applyPermissionForRole(MenuItemPanel mnHeThong,
                                        MenuItemPanel mnDanhMuc,
                                        MenuItemPanel mnXuLy,
                                        MenuItemPanel mnTraCuu,
                                        MenuItemPanel mnThongKe) {

    	if (isLeTan()) {
    	    mnHeThong.removeSubItem("Quản lý tài khoản");

    	    mnXuLy.removeSubItem("Hóa đơn");

    	    mnThongKe.removeSubItem("Phân tích bán hàng");
    	    mnThongKe.removeSubItem("Tổng kết bán hàng");

    	    mnTraCuu.setDirectPageEnabled(true);
    	} else {
    	    mnHeThong.setAllSubItemsEnabled(true);
    	    mnDanhMuc.setAllSubItemsEnabled(true);
    	    mnXuLy.setAllSubItemsEnabled(true);
    	    mnTraCuu.setDirectPageEnabled(true);
    	    mnThongKe.setAllSubItemsEnabled(true);
    	}

        mnHeThong.refreshParentEnabledState();
        mnDanhMuc.refreshParentEnabledState();
        mnXuLy.refreshParentEnabledState();
        mnTraCuu.refreshParentEnabledState();
        mnThongKe.refreshParentEnabledState();
    }

    private boolean isLeTan() {
        String phanQuyen = "";
        String chucVu = "";

        try {
            if (taiKhoanDangNhap != null) {
                if (taiKhoanDangNhap.getPhanQuyen() != null) {
                    phanQuyen = taiKhoanDangNhap.getPhanQuyen().trim().toLowerCase();
                }
                if (taiKhoanDangNhap.getMaNV() != null && taiKhoanDangNhap.getMaNV().getChucVu() != null) {
                    chucVu = taiKhoanDangNhap.getMaNV().getChucVu().trim().toLowerCase();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return phanQuyen.contains("lễ tân")
                || phanQuyen.contains("le tan")
                || chucVu.contains("lễ tân")
                || chucVu.contains("le tan");
    }

    private void addMenu(MenuItemPanel menu) {
        pnTopMenu.add(menu);

        MouseAdapter clickEvent = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!menu.isMenuUsable()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                if (menu.hasSubMenu()) {
                    if (selectedMenu == menu && currentPopupMenu != null && currentPopupMenu.isVisible()) {
                        hideSubMenu();
                        return;
                    }

                    selectMenu(menu);
                    toggleSubMenu(menu);
                } else {
                    selectMenu(menu);
                    hideOnlyPopup();

                    if (menu.directPageEnabled) {
                        navigateTo(menu.directPageClassName);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
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

    private void hideOnlyPopup() {
        if (currentPopupMenu != null) {
            currentPopupMenu.setVisible(false);
            currentPopupMenu = null;
        }
    }

    private void hideSubMenu() {
        if (selectedMenu != null) {
            selectedMenu.setSelected(false);
            selectedMenu = null;
        }
        hideOnlyPopup();
        revalidate();
        repaint();
    }

    private void toggleSubMenu(MenuItemPanel menu) {
        hideOnlyPopup();

        if (!menu.hasEnabledSubMenu()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, BORDER_COLOR));
        popup.setBackground(BG_SUB);
        popup.setOpaque(true);
        popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));

        int itemHeight = SUB_ITEM_HEIGHT;

        int popupWidth = menu.getWidth() > 0 ? menu.getWidth() : menu.getPreferredSize().width;
        int itemWidth = popupWidth - 2; // trừ viền trái + phải của popup

        for (int i = 0; i < menu.subItems.size(); i++) {
            SubMenuItemPanel src = menu.subItems.get(i);
            SubMenuItemPanel item = new SubMenuItemPanel(src.menuText, src.targetClassName, src.enabled);

            item.setPreferredSize(new Dimension(itemWidth, itemHeight));
            item.setMinimumSize(new Dimension(itemWidth, itemHeight));
            item.setMaximumSize(new Dimension(itemWidth, itemHeight));

            popup.add(item);

            if (i < menu.subItems.size() - 1) {
                JPanel line = new JPanel();
                line.setBackground(BORDER_COLOR);
                line.setPreferredSize(new Dimension(itemWidth, 1));
                line.setMaximumSize(new Dimension(itemWidth, 1));
                line.setMinimumSize(new Dimension(itemWidth, 1));
                line.setAlignmentX(Component.LEFT_ALIGNMENT);
                popup.add(line);
            }
        }

        popup.setPopupSize(popupWidth, popup.getPreferredSize().height);

        currentPopupMenu = popup;
        currentPopupMenu.show(menu, 0, menu.getHeight() - 1);
    }

    private void updateAllMenuWidths() {
        for (Component c : pnTopMenu.getComponents()) {
            if (c instanceof MenuItemPanel) {
                ((MenuItemPanel) c).updateWidthByContent();
            }
        }
    }

    private void navigateTo(String targetClassName) {
        if (targetClassName == null || targetClassName.trim().isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

//        if ("__HOTRO__".equals(targetClassName)) {
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Liên hệ quản lý hệ thống hoặc bộ phận kỹ thuật để được hỗ trợ.",
//                    "Hỗ trợ",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
//            hideSubMenu();
//            return;
//        }
        
        if ("__HOTRO__".equals(targetClassName)) {
            try {
                Desktop.getDesktop().browse(
                    new java.net.URI("https://quanlynhahang-ptud-nhom6.netlify.app/")
                );
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Không mở được trang hỗ trợ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            hideSubMenu();
            return;
        }

        if ("DangNhap_GUI".equals(targetClassName)) {
            xuLyDangXuat();
            return;
        }

        hideSubMenu();

        if (navigator != null) {
            navigator.goTo(targetClassName);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private JFrame createFrame(String className) throws Exception {
        Class<?> clazz = Class.forName("gui." + className);

        try {
            Constructor<?> cons = clazz.getConstructor(TaiKhoan.class);
            Object obj = cons.newInstance(taiKhoanDangNhap);
            if (obj instanceof JFrame) return (JFrame) obj;
        } catch (NoSuchMethodException e) {
            // bỏ qua để thử constructor rỗng
        }

        Constructor<?> emptyCons = clazz.getConstructor();
        Object obj = emptyCons.newInstance();
        if (obj instanceof JFrame) return (JFrame) obj;

        return null;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() <= 0) return null;
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    class MenuItemPanel extends JPanel {
        private final JLabel lblIcon;
        private final JLabel lblText;
        private final JLabel lblArrow;

        private boolean selected = false;
        private boolean menuUsable = true;

        private final List<SubMenuItemPanel> subItems = new ArrayList<>();

        private String directPageClassName;
        private boolean directPageEnabled = true;
        public void removeSubItem(String text) {
            subItems.removeIf(item -> item.menuText.equalsIgnoreCase(text));

            if (subItems.isEmpty()) {
                remove(lblArrow);
            }

            revalidate();
            repaint();
        }

        public MenuItemPanel(String text, String iconPath) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 6, 8));
            setBackground(BG_TOP);
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 185, 120)),
                    new EmptyBorder(0, 14, 0, 14)
            ));
            setPreferredSize(new Dimension(MENU_MIN_WIDTH, MENU_HEIGHT));
            setMinimumSize(new Dimension(MENU_MIN_WIDTH, MENU_HEIGHT));
            setMaximumSize(new Dimension(MENU_MIN_WIDTH, MENU_HEIGHT));

            lblIcon = new JLabel();
            lblIcon.setIcon(loadIcon(iconPath, 16, 16));

            lblText = new JLabel(text);
            lblText.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblText.setForeground(FG_NORMAL);

            lblArrow = new JLabel();
            lblArrow.setIcon(loadIcon("img/mn_muiten.png", 20, 20));

            add(lblIcon);
            add(lblText);

            MouseAdapter hoverEvent = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!selected && menuUsable) {
                        setBackground(BG_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!selected) {
                        setBackground(menuUsable ? BG_TOP : BG_DISABLED);
                    }
                }
            };

            attachMouseListenerRecursive(this, hoverEvent);
        }

        public void addSubItem(String text, String targetClassName) {
            if (subItems.isEmpty()) {
                add(lblArrow);
                revalidate();
                repaint();
            }
            subItems.add(new SubMenuItemPanel(text, targetClassName, true));
        }

        public void addDirectPage(String targetClassName) {
            this.directPageClassName = targetClassName;
        }

        public boolean hasSubMenu() {
            return !subItems.isEmpty();
        }

        public boolean hasEnabledSubMenu() {
            for (SubMenuItemPanel item : subItems) {
                if (item.enabled) return true;
            }
            return false;
        }

        public void setSubItemEnabled(String text, boolean enabled) {
            for (SubMenuItemPanel item : subItems) {
                if (item.menuText.equalsIgnoreCase(text)) {
                    item.enabled = enabled;
                }
            }
        }

        public void setAllSubItemsEnabled(boolean enabled) {
            for (SubMenuItemPanel item : subItems) {
                item.enabled = enabled;
            }
        }

        public void setDirectPageEnabled(boolean enabled) {
            this.directPageEnabled = enabled;
        }

        public void refreshParentEnabledState() {
            if (hasSubMenu()) {
                this.menuUsable = hasEnabledSubMenu();
            } else {
                this.menuUsable = directPageEnabled;
            }

            lblText.setForeground(menuUsable ? FG_NORMAL : FG_DISABLED);
            setBackground(menuUsable ? BG_TOP : BG_DISABLED);
            setCursor(menuUsable ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }

        public boolean isMenuUsable() {
            return menuUsable;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (!menuUsable) {
                setBackground(BG_DISABLED);
            } else {
                setBackground(selected ? BG_SELECTED : BG_TOP);
            }
        }

        public void updateWidthByContent() {
            Font font = new Font("SansSerif", Font.BOLD, 18);
            FontMetrics fm = getFontMetrics(font);

            int parentWidth = 14 + 16 + 6 + fm.stringWidth(lblText.getText()) + 14;

            if (hasSubMenu()) {
                parentWidth += 6 + 20;
            }

            int subMaxWidth = 0;
            for (SubMenuItemPanel item : subItems) {
                int w = 12 + fm.stringWidth(item.menuText) + 30;
                subMaxWidth = Math.max(subMaxWidth, w);
            }

            int finalWidth = Math.max(parentWidth, subMaxWidth);
            finalWidth = Math.max(MENU_MIN_WIDTH, finalWidth);

            setPreferredSize(new Dimension(finalWidth, MENU_HEIGHT));
            setMinimumSize(new Dimension(finalWidth, MENU_HEIGHT));
            setMaximumSize(new Dimension(finalWidth, MENU_HEIGHT));
        }
    }

    class SubMenuItemPanel extends JPanel {
        private final String menuText;
        private final String targetClassName;
        private boolean enabled;

        public SubMenuItemPanel(String text, String targetClassName, boolean enabled) {
            this.menuText = text;
            this.targetClassName = targetClassName;
            this.enabled = enabled;

            setLayout(new BorderLayout());
            setOpaque(true);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(0, 12, 0, 10));
            setPreferredSize(new Dimension(1, 38));
            setMinimumSize(new Dimension(1, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

            JLabel lblText = new JLabel(text);
            lblText.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblText.setForeground(enabled ? FG_NORMAL : FG_DISABLED);

            add(lblText, BorderLayout.WEST);

            refreshStyle(lblText);

            MouseAdapter event = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (enabled) {
                        setBackground(BG_SUB_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    refreshStyle(lblText);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!enabled) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    navigateTo(targetClassName);
                }
            };

            attachMouseListenerRecursive(this, event);
        }

        private void refreshStyle(JLabel lblText) {
            setBackground(enabled ? BG_SUB : BG_DISABLED);
            lblText.setForeground(enabled ? FG_NORMAL : FG_DISABLED);
            setCursor(enabled ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    private void xuLyDangXuat() {
        hideSubMenu();

        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn đóng ca trước khi đăng xuất không?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (chon == JOptionPane.YES_OPTION) {
            try {
                CaLamViec_DAO caDAO = new CaLamViec_DAO();
                CaLamViec caDangMo = caDAO.layCaDangMo();

                if (caDangMo == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Không có ca nào đang mở.",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    moLaiDangNhap();
                    return;
                }

                Window owner = SwingUtilities.getWindowAncestor(this);

                DongCa_DigLog dlg;
                if (owner instanceof Frame) {
                    dlg = new DongCa_DigLog((Frame) owner, caDangMo);
                } else {
                    dlg = new DongCa_DigLog(null, caDangMo);
                }

                dlg.setVisible(true);

                if (dlg.isDongCaThanhCong()) {
                    moLaiDangNhap();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi đóng ca!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } else if (chon == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }
    private void moLaiDangNhap() {
        Window w = SwingUtilities.getWindowAncestor(this);

        if (w != null) {
            w.dispose();
        }

        DangNhap_GUI dangNhap = new DangNhap_GUI();
        dangNhap.setVisible(true);
    }
    private JPanel createLogoHome() {
        ImageIcon icon = new ImageIcon("img/logo.png");

        JPanel pnLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (icon.getIconWidth() <= 0) return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Image img = icon.getImage();

                int w = 50; // chiều ngang ảnh
                int h = 42; // chiều cao ảnh

                int x = (getWidth() - w) / 2;
                int y = (getHeight() - h) / 2;

                g2.drawImage(img, x, y, w, h, this);
                g2.dispose();
            }
        };

        pnLogo.setPreferredSize(new Dimension(60, MENU_HEIGHT));
        pnLogo.setMinimumSize(new Dimension(60, MENU_HEIGHT));
        pnLogo.setMaximumSize(new Dimension(60, MENU_HEIGHT));

        pnLogo.setOpaque(true);
        pnLogo.setBackground(BG_TOP);
        pnLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnLogo.setToolTipText("Trang chủ");

        pnLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideSubMenu();
                navigateTo("TrangChu_GUI");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pnLogo.setBackground(BG_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pnLogo.setBackground(BG_TOP);
            }
        });

        return pnLogo;
    }
}