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
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComponent;

import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import entity.LoaiMonAn;
import entity.MonAn;
import entity.PhieuDatMon;

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

    private static final int COL_NAME_W = 250;
    private static final int COL_PRICE_W = 105;
    private static final int COL_QTY_W = 120;
    private static final int COL_TOTAL_W = 130;
    private static final int RIGHT_W = COL_NAME_W + COL_PRICE_W + COL_QTY_W + COL_TOTAL_W;
    private static final int HEADER_H = 58;
    private static final int ORDER_ROW_H = 58;

    private static final int CARD_W = 245;
    private static final int CARD_H = 285;
    private static final int FOOD_HGAP = 16;
    private static final int FOOD_VGAP = 16;

    private static final int IMG_BOX_W = 200;
    private static final int IMG_BOX_H = 135;

    private static final String SEARCH_PLACEHOLDER = "Nhập mã/tên món cần tìm...";
    private static final String SEARCH_ICON_PATH = "img/mn_tracuu.png";

    private JTextField txtSearch;
    private JPanel pnTabs;
    private WrapPanel pnFoodGrid;
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
    private ArrayList<PhieuDatMon> dsMonTam = new ArrayList<>();

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

    public DatMon_DigLog(Frame owner, ArrayList<PhieuDatMon> dsMonTam) {
        super(owner, "Đặt món", true);
        if (dsMonTam != null) {
            this.dsMonTam = new ArrayList<>(dsMonTam);
        }
        init();
        napGioHangTuDanhSachTam();
    }

    public DatMon_DigLog(Dialog owner, ArrayList<PhieuDatMon> dsMonTam) {
        super(owner, "Đặt món", true);
        if (dsMonTam != null) {
            this.dsMonTam = new ArrayList<>(dsMonTam);
        }
        init();
        napGioHangTuDanhSachTam();
    }

    public ArrayList<PhieuDatMon> getDanhSachMonTam() {
        return dsMonTam;
    }

    private void init() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);
        add(createMainPanel(), BorderLayout.CENTER);

        loadData();

        setMinimumSize(new Dimension(1280, 760));
        setPreferredSize(new Dimension(1450, 820));
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void napGioHangTuDanhSachTam() {
        gioHang.clear();

        if (dsMonTam == null || dsMonTam.isEmpty()) {
            renderOrderList();
            return;
        }

        for (PhieuDatMon pdm : dsMonTam) {
            if (pdm.getMaMon() == null) continue;
            MonAn mon = pdm.getMaMon();

            gioHang.put(mon.getMaMon(), new OrderItem(
                    mon,
                    pdm.getSoLuong(),
                    pdm.getGhiChu() == null ? "" : pdm.getGhiChu()
            ));
        }

        renderOrderList();
    }

    private void capNhatDanhSachMonTamTuGioHang() {
        dsMonTam.clear();

        for (OrderItem item : gioHang.values()) {
            PhieuDatMon pdm = new PhieuDatMon(
                    null,
                    item.mon,
                    item.soLuong,
                    item.mon.getDonGia(),
                    item.ghiChu == null ? "" : item.ghiChu
            );
            dsMonTam.add(pdm);
        }
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
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(BG_MAIN);

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setBackground(BG_TOP);
        topPanel.setBorder(new EmptyBorder(12, 16, 10, 16));
        topPanel.setPreferredSize(new Dimension(0, 64));

        RoundedSearchPanel searchWrap = new RoundedSearchPanel();
        searchWrap.setPreferredSize(new Dimension(540, 42));
        searchWrap.setLayout(new BorderLayout());

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(new EmptyBorder(0, 14, 0, 8));
        txtSearch.setOpaque(false);
        txtSearch.setBackground(new Color(0, 0, 0, 0));
        txtSearch.setCaretColor(Color.BLACK);
        txtSearch.setSelectionColor(new Color(210, 225, 245));
        txtSearch.setSelectedTextColor(Color.BLACK);
        txtSearch.setText(SEARCH_PLACEHOLDER);
        txtSearch.setForeground(new Color(150, 150, 150));

        JButton btnSearch = new JButton();
        btnSearch.setPreferredSize(new Dimension(46, 42));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(170, 170, 170)));
        btnSearch.setBackground(new Color(248, 248, 248));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon iconSearch = new ImageIcon(SEARCH_ICON_PATH);
        if (iconSearch.getIconWidth() > 0) {
            Image imgSearch = iconSearch.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            btnSearch.setIcon(new ImageIcon(imgSearch));
        } else {
            btnSearch.setText("⌕");
            btnSearch.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }

        searchWrap.add(txtSearch, BorderLayout.CENTER);
        searchWrap.add(btnSearch, BorderLayout.EAST);

        JLabel lblTitle = new JLabel("Đặt món", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 23));

        topPanel.add(searchWrap, BorderLayout.WEST);
        topPanel.add(lblTitle, BorderLayout.CENTER);

        pnTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTabs.setBackground(BG_MAIN);
        pnTabs.setBorder(new EmptyBorder(0, 12, 0, 12));

        JPanel tabWrap = new JPanel(new BorderLayout());
        tabWrap.setBackground(BG_MAIN);
        tabWrap.setPreferredSize(new Dimension(0, 52));
        tabWrap.add(pnTabs, BorderLayout.CENTER);

        pnFoodGrid = new WrapPanel();
        pnFoodGrid.setLayout(new WrapLayout(FlowLayout.LEFT, FOOD_HGAP, FOOD_VGAP));
        pnFoodGrid.setBackground(BG_MAIN);
        pnFoodGrid.setBorder(new EmptyBorder(16, 12, 16, 12));

        JScrollPane spFood = new JScrollPane(pnFoodGrid);
        spFood.setBorder(null);
        spFood.getViewport().setBackground(BG_MAIN);
        spFood.getVerticalScrollBar().setUnitIncrement(18);
        spFood.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setBackground(BG_MAIN);
        centerWrap.add(tabWrap, BorderLayout.NORTH);
        centerWrap.add(spFood, BorderLayout.CENTER);

        left.add(topPanel, BorderLayout.NORTH);
        left.add(centerWrap, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> locDanhSachMon());
        txtSearch.addActionListener(e -> locDanhSachMon());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                locDanhSachMon();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                locDanhSachMon();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                locDanhSachMon();
            }
        });

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(txtSearch.getText())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText(SEARCH_PLACEHOLDER);
                    txtSearch.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return left;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(RIGHT_W, 0));
        right.setMinimumSize(new Dimension(RIGHT_W, 0));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(130, 130, 130)));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBackground(RIGHT_HEADER);
        header.setPreferredSize(new Dimension(RIGHT_W, HEADER_H));
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
        bottom.setPreferredSize(new Dimension(RIGHT_W, 122));

        JPanel tongPanel = new JPanel();
        tongPanel.setLayout(new BoxLayout(tongPanel, BoxLayout.X_AXIS));
        tongPanel.setBackground(Color.WHITE);
        tongPanel.setPreferredSize(new Dimension(RIGHT_W, 52));
        tongPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER));

        JLabel lblTongText = new JLabel("Tổng cộng");
        lblTongText.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTongText.setBorder(new EmptyBorder(0, 18, 0, 0));
        lblTongText.setPreferredSize(new Dimension(COL_NAME_W + COL_PRICE_W, 52));
        lblTongText.setMinimumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 52));
        lblTongText.setMaximumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 52));

        lblTongSoLuong = new JLabel("0", SwingConstants.CENTER);
        lblTongSoLuong.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTongSoLuong.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, BORDER));
        lblTongSoLuong.setPreferredSize(new Dimension(COL_QTY_W, 52));
        lblTongSoLuong.setMinimumSize(new Dimension(COL_QTY_W, 52));
        lblTongSoLuong.setMaximumSize(new Dimension(COL_QTY_W, 52));

        lblTongTien = new JLabel("0", SwingConstants.CENTER);
        lblTongTien.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTongTien.setPreferredSize(new Dimension(COL_TOTAL_W, 52));
        lblTongTien.setMinimumSize(new Dimension(COL_TOTAL_W, 52));
        lblTongTien.setMaximumSize(new Dimension(COL_TOTAL_W, 52));

        tongPanel.add(lblTongText);
        tongPanel.add(lblTongSoLuong);
        tongPanel.add(lblTongTien);

        JPanel actionPanel = new JPanel(new java.awt.GridLayout(1, 2, 0, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setPreferredSize(new Dimension(RIGHT_W, 70));

        JButton btnBack = new JButton("↩ Quay lại");
        styleMainButton(btnBack, BTN_BACK, Color.WHITE);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btnBack.setMargin(new Insets(0, 0, 0, 0));

        JButton btnDatMon = new JButton("ĐẶT MÓN");
        styleMainButton(btnDatMon, BTN_ORDER, Color.BLACK);
        btnDatMon.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnDatMon.setMargin(new Insets(0, 0, 0, 0));

        btnBack.addActionListener(e -> dispose());

        btnDatMon.addActionListener(e -> {
            capNhatDanhSachMonTamTuGioHang();
            dispose();
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
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setPreferredSize(new Dimension(width, HEADER_H));
        lbl.setMinimumSize(new Dimension(width, HEADER_H));
        lbl.setMaximumSize(new Dimension(width, HEADER_H));
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
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 10, 0, 10));
        btn.setPreferredSize(new Dimension(120, 34));
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
        if (SEARCH_PLACEHOLDER.equalsIgnoreCase(tuKhoa)) {
            tuKhoa = "";
        }

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

            pnFoodGrid.add(new MonCard(mon));
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
            setPreferredSize(new Dimension(CARD_W, CARD_H));
            setMinimumSize(new Dimension(CARD_W, CARD_H));
            setMaximumSize(new Dimension(CARD_W, CARD_H));

            JPanel imgWrap = new JPanel(new BorderLayout());
            imgWrap.setOpaque(false);
            imgWrap.setBorder(new EmptyBorder(12, 12, 0, 12));
            imgWrap.setPreferredSize(new Dimension(CARD_W, IMG_BOX_H + 5));

            ImagePanel imgPanel = new ImagePanel(mon.getAnhMon());
            imgPanel.setPreferredSize(new Dimension(IMG_BOX_W, IMG_BOX_H));
            imgPanel.setMinimumSize(new Dimension(IMG_BOX_W, IMG_BOX_H));
            imgPanel.setMaximumSize(new Dimension(IMG_BOX_W, IMG_BOX_H));
            imgPanel.setBackground(Color.WHITE);
            imgPanel.setBorder(new LineBorder(BORDER, 1));
            imgWrap.add(imgPanel, BorderLayout.CENTER);

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(new EmptyBorder(8, 10, 10, 10));

            JLabel lblTen = new JLabel(
                    "<html><div style='text-align:center; width:190px;'>" + safe(mon.getTenMon()) + "</div></html>",
                    SwingConstants.CENTER
            );
            lblTen.setFont(new Font("SansSerif", Font.BOLD, 15));
            lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblMoTa = new JLabel(
                    "<html><div style='text-align:center; width:190px;'>Mô tả: " + safe(mon.getMoTa()) + "</div></html>"
            );
            lblMoTa.setFont(new Font("SansSerif", Font.ITALIC, 12));
            lblMoTa.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblGia = new JLabel(formatTien(mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.BOLD, 20));
            lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);

            info.add(lblTen);
            info.add(Box.createVerticalStrut(6));
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

                    g2.setClip(0, 0, pw, ph);
                    g2.drawImage(img, x, y, nw, nh, this);
                    g2.dispose();
                    return;
                }
            }

            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
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
            setMaximumSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            setPreferredSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            setMinimumSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            setBackground(new Color(246, 246, 246));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(RIGHT_W, ORDER_ROW_H));

            JPanel colTen = new JPanel(new BorderLayout(6, 0));
            colTen.setOpaque(false);
            colTen.setPreferredSize(new Dimension(COL_NAME_W, ORDER_ROW_H));
            colTen.setMinimumSize(new Dimension(COL_NAME_W, ORDER_ROW_H));
            colTen.setMaximumSize(new Dimension(COL_NAME_W, ORDER_ROW_H));
            colTen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                    new EmptyBorder(5, 8, 5, 8)
            ));

            JLabel lblXoa = new JLabel("🗑", SwingConstants.CENTER);
            lblXoa.setFont(new Font("SansSerif", Font.PLAIN, 17));
            lblXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblXoa.setPreferredSize(new Dimension(28, ORDER_ROW_H - 10));

            JPanel tenNotePanel = new JPanel();
            tenNotePanel.setOpaque(false);
            tenNotePanel.setLayout(new BoxLayout(tenNotePanel, BoxLayout.Y_AXIS));

            JLabel lblTen = new JLabel(
                    "<html><div style='width:190px;'>" + safe(item.mon.getTenMon()) + "</div></html>"
            );
            lblTen.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblGhiChu = new JTextArea(getGhiChuDisplayText());
            lblGhiChu.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblGhiChu.setForeground(Color.GRAY);
            lblGhiChu.setOpaque(false);
            lblGhiChu.setEditable(false);
            lblGhiChu.setLineWrap(false);
            lblGhiChu.setBorder(new EmptyBorder(1, 0, 0, 0));
            lblGhiChu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblGhiChu.setFocusable(false);
            lblGhiChu.setMaximumSize(new Dimension(200, 18));
            lblGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);

            tenNotePanel.add(Box.createVerticalGlue());
            tenNotePanel.add(lblTen);
            tenNotePanel.add(Box.createVerticalStrut(2));
            tenNotePanel.add(lblGhiChu);
            tenNotePanel.add(Box.createVerticalGlue());

            colTen.add(lblXoa, BorderLayout.WEST);
            colTen.add(tenNotePanel, BorderLayout.CENTER);

            JPanel colGia = new JPanel(new BorderLayout());
            colGia.setOpaque(false);
            colGia.setPreferredSize(new Dimension(COL_PRICE_W, ORDER_ROW_H));
            colGia.setMinimumSize(new Dimension(COL_PRICE_W, ORDER_ROW_H));
            colGia.setMaximumSize(new Dimension(COL_PRICE_W, ORDER_ROW_H));
            colGia.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JLabel lblGia = new JLabel(formatTien(item.mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.PLAIN, 14));
            colGia.add(lblGia, BorderLayout.CENTER);

            JPanel colSL = new JPanel(new GridBagLayout());
            colSL.setOpaque(false);
            colSL.setPreferredSize(new Dimension(COL_QTY_W, ORDER_ROW_H));
            colSL.setMinimumSize(new Dimension(COL_QTY_W, ORDER_ROW_H));
            colSL.setMaximumSize(new Dimension(COL_QTY_W, ORDER_ROW_H));
            colSL.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JSpinner spinnerSL = new JSpinner(new SpinnerNumberModel(item.soLuong, 1, 999, 1));
            spinnerSL.setFont(new Font("SansSerif", Font.PLAIN, 14));
            spinnerSL.setPreferredSize(new Dimension(Math.max(70, COL_QTY_W - 24), 30));

            JComponent editor = spinnerSL.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                JTextField txt = ((JSpinner.DefaultEditor) editor).getTextField();
                txt.setHorizontalAlignment(JTextField.CENTER);
                txt.setFont(new Font("SansSerif", Font.BOLD, 14));
            }

            spinnerSL.addChangeListener(e -> {
                int newValue = (Integer) spinnerSL.getValue();
                item.soLuong = newValue;
                renderOrderList();
            });

            colSL.add(spinnerSL);

            JPanel colTotal = new JPanel(new BorderLayout());
            colTotal.setOpaque(false);
            colTotal.setPreferredSize(new Dimension(COL_TOTAL_W, ORDER_ROW_H));
            colTotal.setMinimumSize(new Dimension(COL_TOTAL_W, ORDER_ROW_H));
            colTotal.setMaximumSize(new Dimension(COL_TOTAL_W, ORDER_ROW_H));

            JLabel lblTotal = new JLabel(formatTien(item.soLuong * item.mon.getDonGia()), SwingConstants.CENTER);
            lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
            colTotal.add(lblTotal, BorderLayout.CENTER);

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

            row.add(colTen);
            row.add(colGia);
            row.add(colSL);
            row.add(colTotal);

            add(row, BorderLayout.CENTER);
        }

        private String getGhiChuDisplayText() {
            String gc = item.ghiChu == null ? "" : item.ghiChu.trim();
            if (gc.isEmpty()) return "Ghi chú 📝";
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

    static class WrapPanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 60;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    static class WrapLayout extends FlowLayout {
        private static final long serialVersionUID = 1L;

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();

                if (targetWidth <= 0) {
                    Container parent = target.getParent();
                    if (parent != null) {
                        targetWidth = parent.getWidth();
                    }
                }

                if (targetWidth <= 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                Insets insets = target.getInsets();
                int hgap = getHgap();
                int vgap = getVgap();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible()) continue;

                    Dimension d = m.getPreferredSize();

                    if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);

            if (dim.height > 0) {
                dim.height += getVgap();
            }

            dim.height += rowHeight;
        }
    }

    static class RoundedSearchPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        public RoundedSearchPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(248, 248, 248));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(new Color(120, 120, 120));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    }