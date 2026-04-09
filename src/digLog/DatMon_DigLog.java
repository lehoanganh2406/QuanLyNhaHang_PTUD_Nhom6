package digLog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import entity.LoaiMonAn;
import entity.MonAn;

public class DatMon_DigLog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final Color BG_MAIN = new Color(238, 238, 238);
    private final Color BG_TOP = new Color(245, 245, 245);

    private final Color TAB_BG = new Color(247, 241, 232);
    private final Color TAB_SELECTED = new Color(235, 218, 190);
    private final Color TAB_BORDER = new Color(225, 215, 198);

    private final Color CARD_BG = new Color(244, 236, 223);
    private final Color CARD_HOVER = new Color(236, 226, 209);

    private final Color BORDER = new Color(150, 150, 150);
    private final Color RIGHT_HEADER = new Color(206, 227, 242);

    private final Color BTN_BACK = new Color(224, 174, 70);
    private final Color BTN_ORDER = new Color(230, 146, 155);

    private static final int COL_NAME_W = 280;
    private static final int COL_PRICE_W = 120;
    private static final int COL_QTY_W = 130;
    private static final int COL_TOTAL_W = 150;
    private static final int RIGHT_W = COL_NAME_W + COL_PRICE_W + COL_QTY_W + COL_TOTAL_W;

    private JTextField txtSearch;
    private JPanel pnTabs;
    private JPanel pnFoodGrid;
    private JPanel pnOrderList;
    private JLabel lblTongSoLuong;
    private JLabel lblTongTien;

    private final DecimalFormat df = new DecimalFormat("#,##0");

    private final LoaiMonAn_DAO loaiMonAnDAO = new LoaiMonAn_DAO();
    private final MonAn_DAO monAnDAO = new MonAn_DAO();

    private List<LoaiMonAn> dsLoai = new ArrayList<>();
    private List<MonAn> dsMon = new ArrayList<>();
    private final Map<String, OrderItem> gioHang = new LinkedHashMap<>();

    private String maLoaiDangChon = "ALL";

    public DatMon_DigLog(Frame owner) {
        super(owner, "Đặt món", true);
        init();
    }

    public DatMon_DigLog(Dialog owner) {
        super(owner, "Đặt món", true);
        init();
    }

    public DatMon_DigLog() {
        super((Frame) null, "Đặt món", true);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);
        add(createMainPanel(), BorderLayout.CENTER);

        loadData();

        setMinimumSize(new Dimension(1500, 820));
        setPreferredSize(new Dimension(1650, 900));
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);

        JPanel left = createLeftPanel();
        JPanel right = createRightPanel();

        root.add(left, BorderLayout.CENTER);
        root.add(right, BorderLayout.EAST);
        return root;
    }

    private JPanel createLeftPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_TOP);
        topPanel.setBorder(new EmptyBorder(10, 14, 8, 14));

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        searchWrap.setPreferredSize(new Dimension(340, 36));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 190, 190), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton btnSearch = new JButton("⌕");
        btnSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnSearch.setPreferredSize(new Dimension(36, 36));
        btnSearch.setFocusPainted(false);
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setOpaque(true);

        JLabel lblTitle = new JLabel("Đặt món", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));

        searchWrap.add(txtSearch, BorderLayout.CENTER);
        searchWrap.add(btnSearch, BorderLayout.EAST);

        topPanel.add(searchWrap, BorderLayout.WEST);
        topPanel.add(lblTitle, BorderLayout.CENTER);

        pnTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnTabs.setBackground(BG_MAIN);
        pnTabs.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel tabWrap = new JPanel(new BorderLayout());
        tabWrap.setBackground(BG_MAIN);
        tabWrap.setBorder(new EmptyBorder(0, 0, 0, 0));
        tabWrap.add(pnTabs, BorderLayout.CENTER);

        pnFoodGrid = new JPanel(new GridLayout(0, 3, 18, 18));
        pnFoodGrid.setBackground(BG_MAIN);
        pnFoodGrid.setBorder(new EmptyBorder(18, 18, 18, 18));

        JScrollPane spFood = new JScrollPane(pnFoodGrid);
        spFood.setBorder(null);
        spFood.getViewport().setBackground(BG_MAIN);
        spFood.getVerticalScrollBar().setUnitIncrement(18);
        spFood.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel wrapper = new JPanel(null);
        wrapper.setBackground(BG_MAIN);

        wrapper.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = wrapper.getWidth();
                int h = wrapper.getHeight();
                int topH = 56;
                int tabH = 48;

                topPanel.setBounds(0, 0, w, topH);
                tabWrap.setBounds(0, topH, w, tabH);
                spFood.setBounds(0, topH + tabH, w, h - topH - tabH);
            }
        });

        wrapper.add(topPanel);
        wrapper.add(tabWrap);
        wrapper.add(spFood);

        btnSearch.addActionListener(e -> locDanhSachMon());
        txtSearch.addActionListener(e -> locDanhSachMon());

        return wrapper;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(RIGHT_W, 0));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(130, 130, 130)));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBackground(RIGHT_HEADER);
        header.setPreferredSize(new Dimension(RIGHT_W, 92));

        header.add(createHeaderCell("Tên món", COL_NAME_W, true));
        header.add(createHeaderCell("Giá", COL_PRICE_W, true));
        header.add(createHeaderCell("Số lượng", COL_QTY_W, true));
        header.add(createHeaderCell("Tổng tiền", COL_TOTAL_W, false));

        pnOrderList = new JPanel();
        pnOrderList.setLayout(new BoxLayout(pnOrderList, BoxLayout.Y_AXIS));
        pnOrderList.setBackground(Color.WHITE);

        JScrollPane spOrder = new JScrollPane(pnOrderList);
        spOrder.setBorder(null);
        spOrder.getViewport().setBackground(Color.WHITE);
        spOrder.getVerticalScrollBar().setUnitIncrement(18);
        spOrder.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setPreferredSize(new Dimension(RIGHT_W, 132));
        bottom.setBorder(BorderFactory.createEmptyBorder());

        JPanel tongPanel = new JPanel();
        tongPanel.setLayout(new BoxLayout(tongPanel, BoxLayout.X_AXIS));
        tongPanel.setBackground(Color.WHITE);
        tongPanel.setPreferredSize(new Dimension(RIGHT_W, 58));
        tongPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER));

        JLabel lblTongText = new JLabel("Tổng cộng");
        lblTongText.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTongText.setBorder(new EmptyBorder(0, 18, 0, 0));
        lblTongText.setPreferredSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));
        lblTongText.setMinimumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));
        lblTongText.setMaximumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));

        lblTongSoLuong = new JLabel("0", SwingConstants.CENTER);
        lblTongSoLuong.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTongSoLuong.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, BORDER));
        lblTongSoLuong.setPreferredSize(new Dimension(COL_QTY_W, 58));
        lblTongSoLuong.setMinimumSize(new Dimension(COL_QTY_W, 58));
        lblTongSoLuong.setMaximumSize(new Dimension(COL_QTY_W, 58));

        lblTongTien = new JLabel("0", SwingConstants.CENTER);
        lblTongTien.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTongTien.setPreferredSize(new Dimension(COL_TOTAL_W, 58));
        lblTongTien.setMinimumSize(new Dimension(COL_TOTAL_W, 58));
        lblTongTien.setMaximumSize(new Dimension(COL_TOTAL_W, 58));

        tongPanel.add(lblTongText);
        tongPanel.add(lblTongSoLuong);
        tongPanel.add(lblTongTien);

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder());
        actionPanel.setPreferredSize(new Dimension(RIGHT_W, 74));

        JButton btnBack = new JButton("↩ Quay lại");
        styleMainButton(btnBack, BTN_BACK, Color.WHITE);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btnBack.setMargin(new Insets(0, 0, 0, 0));

        JButton btnDatMon = new JButton("ĐẶT MÓN");
        styleMainButton(btnDatMon, BTN_ORDER, Color.BLACK);
        btnDatMon.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnDatMon.setMargin(new Insets(0, 0, 0, 0));

        btnBack.addActionListener(e -> dispose());

        btnDatMon.addActionListener(e -> {
            if (gioHang.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa chọn món nào.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Đặt món thành công.");
        });

        actionPanel.add(btnBack);
        actionPanel.add(btnDatMon);

        bottom.add(tongPanel, BorderLayout.NORTH);
        bottom.add(actionPanel, BorderLayout.CENTER);

        right.add(header, BorderLayout.NORTH);
        right.add(spOrder, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        return right;
    }

    private JLabel createHeaderCell(String text, int width, boolean rightBorder) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(RIGHT_HEADER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setPreferredSize(new Dimension(width, 92));
        lbl.setMinimumSize(new Dimension(width, 92));
        lbl.setMaximumSize(new Dimension(width, 92));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, rightBorder ? 1 : 0, BORDER));
        return lbl;
    }

    private void styleMainButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadData() {
        try {
            dsLoai = loaiMonAnDAO.getAllLoaiMonAn();
            dsMon = monAnDAO.getAllMonAn();
            taoTabsLoai();
            locDanhSachMon();
            renderOrderList();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu món ăn.");
        }
    }

    private void taoTabsLoai() {
        pnTabs.removeAll();
        pnTabs.add(createTabButton("Tất cả", "ALL"));

        for (LoaiMonAn loai : dsLoai) {
            pnTabs.add(createTabButton(loai.getTenLoaiMonAn(), loai.getMaLoaiMonAn()));
        }

        pnTabs.revalidate();
        pnTabs.repaint();
    }

    private JButton createTabButton(String text, String maLoai) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setMinimumSize(new Dimension(120, 36));
        btn.setMaximumSize(new Dimension(120, 36));
        btn.setBorder(new LineBorder(TAB_BORDER, 1));
        btn.setBackground(maLoaiDangChon.equals(maLoai) ? TAB_SELECTED : TAB_BG);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addActionListener(e -> {
            maLoaiDangChon = maLoai;
            taoTabsLoai();
            locDanhSachMon();
        });

        return btn;
    }

    private void locDanhSachMon() {
        pnFoodGrid.removeAll();

        String tuKhoa = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        List<MonAn> filtered = new ArrayList<>();

        for (MonAn mon : dsMon) {
            if (!mon.isTrangThai()) continue;

            boolean dungLoai = maLoaiDangChon.equals("ALL")
                    || (mon.getMaLoaiMonAn() != null
                    && maLoaiDangChon.equals(mon.getMaLoaiMonAn().getMaLoaiMonAn()));

            if (!dungLoai) continue;

            boolean dungTuKhoa = tuKhoa.isEmpty()
                    || safe(mon.getTenMon()).toLowerCase().contains(tuKhoa)
                    || safe(mon.getMaMon()).toLowerCase().contains(tuKhoa);

            if (!dungTuKhoa) continue;

            filtered.add(mon);
        }

        int soDu = filtered.size() % 3;
        int soThem = soDu == 0 ? 0 : 3 - soDu;

        for (MonAn mon : filtered) {
            pnFoodGrid.add(new MonCard(mon));
        }

        for (int i = 0; i < soThem; i++) {
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            pnFoodGrid.add(filler);
        }

        pnFoodGrid.revalidate();
        pnFoodGrid.repaint();
    }

    private void themMonVaoGio(MonAn mon) {
        OrderItem item = gioHang.get(mon.getMaMon());
        if (item == null) {
            gioHang.put(mon.getMaMon(), new OrderItem(mon, 1, ""));
        } else {
            item.soLuong++;
        }
        renderOrderList();
    }

    private void renderOrderList() {
        pnOrderList.removeAll();

        for (OrderItem item : gioHang.values()) {
            pnOrderList.add(new OrderItemPanel(item));
        }

        pnOrderList.add(Box.createVerticalGlue());
        pnOrderList.revalidate();
        pnOrderList.repaint();

        lblTongSoLuong.setText(String.valueOf(tinhTongSoLuong()));
        lblTongTien.setText(formatTien(tinhTongTien()));
    }

    private int tinhTongSoLuong() {
        int tong = 0;
        for (OrderItem item : gioHang.values()) {
            tong += item.soLuong;
        }
        return tong;
    }

    private double tinhTongTien() {
        double tong = 0;
        for (OrderItem item : gioHang.values()) {
            tong += item.soLuong * item.mon.getDonGia();
        }
        return tong;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formatTien(double value) {
        return df.format(value).replace(",", ".");
    }

    private ImageIcon loadImageMon(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return null;

        String[] paths = {
                "img/" + fileName,
                "image/" + fileName,
                "images/" + fileName
        };

        for (String path : paths) {
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) return icon;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private void addMouseAll(Component comp, java.awt.event.MouseAdapter adapter) {
        comp.addMouseListener(adapter);
        if (comp instanceof Container) {
            for (Component c : ((Container) comp).getComponents()) {
                addMouseAll(c, adapter);
            }
        }
    }

    class MonCard extends JPanel {
        private static final long serialVersionUID = 1L;

        public MonCard(MonAn mon) {
            setLayout(new BorderLayout());
            setBackground(CARD_BG);
            setBorder(new LineBorder(BORDER, 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(285, 345));

            JPanel imgWrap = new JPanel(new BorderLayout());
            imgWrap.setOpaque(false);
            imgWrap.setBorder(new EmptyBorder(18, 18, 0, 18));
            imgWrap.setPreferredSize(new Dimension(285, 185));

            JPanel imgPanel = new ImagePanel(mon.getAnhMon());
            imgPanel.setBackground(new Color(232, 221, 203));
            imgPanel.setBorder(new LineBorder(BORDER, 1));
            imgWrap.add(imgPanel, BorderLayout.CENTER);

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(new EmptyBorder(10, 12, 14, 12));

            JLabel lblTen = new JLabel(
                    "<html><div style='text-align:center; width:220px;'>" + safe(mon.getTenMon()) + "</div></html>",
                    SwingConstants.CENTER
            );
            lblTen.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblMoTa = new JLabel(
                    "<html><div style='text-align:center; width:220px;'>Mô tả: " + safe(mon.getMoTa()) + "</div></html>"
            );
            lblMoTa.setFont(new Font("SansSerif", Font.ITALIC, 13));
            lblMoTa.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblGia = new JLabel(formatTien(mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.BOLD, 29));
            lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);

            info.add(lblTen);
            info.add(Box.createVerticalStrut(8));
            info.add(lblMoTa);
            info.add(Box.createVerticalGlue());
            info.add(Box.createVerticalStrut(8));
            info.add(lblGia);

            add(imgWrap, BorderLayout.NORTH);
            add(info, BorderLayout.CENTER);

            java.awt.event.MouseAdapter click = new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    themMonVaoGio(mon);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(CARD_HOVER);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setBackground(CARD_BG);
                }
            };
            addMouseAll(this, click);
        }
    }

    class ImagePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final String fileName;

        public ImagePanel(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ImageIcon icon = loadImageMon(fileName);
            if (icon != null) {
                Image img = icon.getImage();

                int pw = getWidth();
                int ph = getHeight();
                int iw = img.getWidth(this);
                int ih = img.getHeight(this);

                if (iw > 0 && ih > 0) {
                    double scale = Math.max((double) pw / iw, (double) ph / ih);
                    int nw = (int) Math.round(iw * scale);
                    int nh = (int) Math.round(ih * scale);

                    int x = (pw - nw) / 2;
                    int y = (ph - nh) / 2;

                    g2.drawImage(img, x, y, nw, nh, this);
                    g2.dispose();
                    return;
                }
            }

            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 16));
            String text = "Không có ảnh";
            int sw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (getWidth() - sw) / 2, getHeight() / 2);
            g2.dispose();
        }
    }

    class OrderItemPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final OrderItem item;
        private JTextArea lblGhiChu;
        private JPopupMenu popupGhiChu;
        private JTextField txtNote;

        public OrderItemPanel(OrderItem item) {
            this.item = item;

            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(RIGHT_W, 82));
            setPreferredSize(new Dimension(RIGHT_W, 82));
            setBackground(new Color(246, 246, 246));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(RIGHT_W, 82));

            JPanel colTen = new JPanel();
            colTen.setOpaque(false);
            colTen.setLayout(new BoxLayout(colTen, BoxLayout.Y_AXIS));
            colTen.setPreferredSize(new Dimension(COL_NAME_W, 82));
            colTen.setMinimumSize(new Dimension(COL_NAME_W, 82));
            colTen.setMaximumSize(new Dimension(COL_NAME_W, 82));
            colTen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                    new EmptyBorder(8, 10, 6, 8)
            ));

            JPanel tenRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            tenRow.setOpaque(false);
            tenRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblXoa = new JLabel("🗑");
            lblXoa.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblTen = new JLabel(
                    "<html><div style='width:220px;'>" + safe(item.mon.getTenMon()) + "</div></html>"
            );
            lblTen.setFont(new Font("SansSerif", Font.PLAIN, 15));

            tenRow.add(lblXoa);
            tenRow.add(lblTen);

            lblGhiChu = new JTextArea(getGhiChuDisplayText());
            lblGhiChu.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblGhiChu.setForeground(Color.GRAY);
            lblGhiChu.setOpaque(false);
            lblGhiChu.setEditable(false);
            lblGhiChu.setLineWrap(true);
            lblGhiChu.setWrapStyleWord(true);
            lblGhiChu.setBorder(new EmptyBorder(0, 24, 0, 0));
            lblGhiChu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblGhiChu.setFocusable(false);
            lblGhiChu.setMaximumSize(new Dimension(240, 28));
            lblGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);

            colTen.add(tenRow);
            colTen.add(Box.createVerticalStrut(2));
            colTen.add(lblGhiChu);

            JPanel colGia = new JPanel(new BorderLayout());
            colGia.setOpaque(false);
            colGia.setPreferredSize(new Dimension(COL_PRICE_W, 82));
            colGia.setMinimumSize(new Dimension(COL_PRICE_W, 82));
            colGia.setMaximumSize(new Dimension(COL_PRICE_W, 82));
            colGia.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JLabel lblGia = new JLabel(formatTien(item.mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.PLAIN, 15));
            colGia.add(lblGia, BorderLayout.CENTER);

            JPanel colSL = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 18));
            colSL.setOpaque(false);
            colSL.setPreferredSize(new Dimension(COL_QTY_W, 82));
            colSL.setMinimumSize(new Dimension(COL_QTY_W, 82));
            colSL.setMaximumSize(new Dimension(COL_QTY_W, 82));
            colSL.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JButton btnMinus = new JButton("⊖");
            JButton btnPlus = new JButton("⊕");
            JLabel lblSL = new JLabel(String.valueOf(item.soLuong), SwingConstants.CENTER);

            styleQtyButton(btnMinus);
            styleQtyButton(btnPlus);

            lblSL.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblSL.setBorder(new LineBorder(Color.GRAY, 1));
            lblSL.setPreferredSize(new Dimension(34, 28));

            btnMinus.addActionListener(e -> {
                item.soLuong--;
                if (item.soLuong <= 0) {
                    gioHang.remove(item.mon.getMaMon());
                }
                renderOrderList();
            });

            btnPlus.addActionListener(e -> {
                item.soLuong++;
                renderOrderList();
            });

            lblXoa.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    gioHang.remove(item.mon.getMaMon());
                    renderOrderList();
                }
            });

            lblGhiChu.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showNotePopup();
                }
            });

            colSL.add(btnMinus);
            colSL.add(lblSL);
            colSL.add(btnPlus);

            JPanel colTotal = new JPanel(new BorderLayout());
            colTotal.setOpaque(false);
            colTotal.setPreferredSize(new Dimension(COL_TOTAL_W, 82));
            colTotal.setMinimumSize(new Dimension(COL_TOTAL_W, 82));
            colTotal.setMaximumSize(new Dimension(COL_TOTAL_W, 82));

            JLabel lblTotal = new JLabel(formatTien(item.soLuong * item.mon.getDonGia()), SwingConstants.CENTER);
            lblTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
            colTotal.add(lblTotal, BorderLayout.CENTER);

            row.add(colTen);
            row.add(colGia);
            row.add(colSL);
            row.add(colTotal);

            add(row, BorderLayout.CENTER);
        }

        private String getGhiChuDisplayText() {
            String gc = item.ghiChu == null ? "" : item.ghiChu.trim();
            if (gc.isEmpty()) return "Ghi chú ..";
            return gc;
        }

        private void showNotePopup() {
            if (popupGhiChu != null && popupGhiChu.isVisible()) {
                popupGhiChu.setVisible(false);
            }

            popupGhiChu = new JPopupMenu();
            popupGhiChu.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(170, 170, 170), 1),
                    new EmptyBorder(4, 4, 4, 4)
            ));

            txtNote = new JTextField(item.ghiChu == null ? "" : item.ghiChu);
            txtNote.setFont(new Font("SansSerif", Font.PLAIN, 12));
            txtNote.setPreferredSize(new Dimension(220, 28));
            txtNote.setBorder(new EmptyBorder(4, 6, 4, 6));

            txtNote.addActionListener(e -> saveAndHidePopup());
            txtNote.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        if (txtNote != null && !txtNote.hasFocus()) {
                            saveAndHidePopup();
                        }
                    });
                }
            });

            popupGhiChu.add(txtNote);
            popupGhiChu.show(lblGhiChu, 0, lblGhiChu.getHeight() + 2);

            SwingUtilities.invokeLater(() -> {
                txtNote.requestFocusInWindow();
                txtNote.selectAll();
            });
        }

        private void saveAndHidePopup() {
            if (txtNote != null) {
                item.ghiChu = txtNote.getText().trim();
                lblGhiChu.setText(getGhiChuDisplayText());
            }
            if (popupGhiChu != null) {
                popupGhiChu.setVisible(false);
            }
        }

        private void styleQtyButton(JButton btn) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
            btn.setPreferredSize(new Dimension(28, 28));
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(0, 0, 0, 0));
        }
    }

    static class OrderItem {
        MonAn mon;
        int soLuong;
        String ghiChu;

        public OrderItem(MonAn mon, int soLuong, String ghiChu) {
            this.mon = mon;
            this.soLuong = soLuong;
            this.ghiChu = ghiChu;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatMon_DigLog dlg = new DatMon_DigLog((Frame) null);
            dlg.setVisible(true);
        });
    }
}