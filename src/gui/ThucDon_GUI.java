package gui;


import gui.Pn_ThanhMenu;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.*;
import java.util.List;

import connectDB.ConnectDB;
import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import digLog.XuLyMonAn_DigLog;
import entity.LoaiMonAn;
import entity.MonAn;

public class ThucDon_GUI extends JFrame {

    private static final Color C_NAV      = new Color(188, 165, 110);
    private static final Color C_BG       = new Color(242, 236, 218);
    private static final Color C_TAB_BAR  = new Color(228, 218, 192);
    private static final Color C_TAB_ACT  = Color.WHITE;
    private static final Color C_CARD     = Color.WHITE;
    private static final Color C_BORDER   = new Color(215, 208, 190);
    private static final Color C_SELECTED = new Color(100, 162, 232);
    private static final Color C_BROWN    = new Color(90, 65, 35);
    private static final Color C_BTN_ADD  = new Color(45, 170, 75);
    private static final Color C_BTN_UPD  = new Color(210, 165, 35);
    private static final Color C_BTN_DTL  = new Color(55, 130, 210);
    private static final Color C_BTN_REF  = new Color(230, 120, 40);
    private static final Color C_BTN_LOC  = new Color(155, 110, 50);
    private static final Color C_STOP = new Color(255, 55,55); // đỏ nhạt

    private static final int CARD_W = 280;
    private static final int CARD_H = 360;

    private static final int IMG_W  = 250;
    private static final int IMG_H  = 210;
    private static final int GAP    = 12;
    private static final int PAD    = 16;

//    private static final String[][] CATS = {
//        {"ALL",  "Tất cả"},
//        {"LM01", "Món Khai vị"},
//        {"LM02", "Món chính"},
//        {"LM03", "Món phụ"},
//        {"LM04", "Tráng miệng"},
//        {"LM05", "Nước uống"}
//    };
    
    
    private String currentCategory = null; // null = ALL

    private final MonAn_DAO monDAO = new MonAn_DAO();
    private JPanel     pCards;
    private CardLayout cardLayout = new CardLayout();
    private JTextField txtSearch;
    private JButton    btnThem, btnCapNhat, btnChiTiet, btnLamMoi;
    private JPanel     pTabBar;
    private JButton    activeTabBtn;
    private MonAn  selectedMon  = null;
    private JPanel selectedCard = null;
    private String tenHienThi;
    private int    loaiTaiKhoan;
    private String maNV;
    private final Map<String, List<MonAn>> cache = new HashMap<>();
    private JTextField txtGiaTu;
    private JTextField txtGiaDen;
    private JButton    btnLoc;

    private static final double SCALE;
    static {
        AffineTransform at = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getDefaultTransform();
        SCALE = at.getScaleX();
    }

    public ThucDon_GUI() {
        Pn_ThanhMenu menu = new Pn_ThanhMenu(null);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(C_BG);
        mainPanel.add(buildTopBar(),  BorderLayout.NORTH);
        mainPanel.add(buildContent(), BorderLayout.CENTER);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(menu,      JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
            	int w = getWidth();
            	int h = getHeight();
                mainPanel.setBounds(0, 42, w, h - 42);
                menu.setBounds(0, 0, w, 42);
                layeredPane.revalidate();
                layeredPane.repaint();
            }
        });

        List<MonAn> list = monDAO.getAllMonAn();
        cache.put("ALL", list);
        JScrollPane scroll = buildGridScroll(list, "ALL");
        pCards.add(scroll, "ALL");
        cardLayout.show(pCards, "ALL");
    }

    private JButton createBigButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker()
                          : getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Times New Roman", Font.BOLD, (int)(14 * SCALE)));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildTopBar() {
        final int FIELD_H = (int)(40 * SCALE);
        final int LABEL_W = (int)(150 * SCALE);
        final int WIDTH   = (int)(120 * SCALE);

        JPanel actionRow = new JPanel(new BorderLayout((int)(20*SCALE), 0));
        actionRow.setBackground(Color.WHITE);
        actionRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
                BorderFactory.createEmptyBorder(
                        (int)(12*SCALE), (int)(40*SCALE),
                        (int)(12*SCALE), (int)(20*SCALE))));

        // ================= LEFT =================
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        // ===== ROW 1 =====
        JPanel row1 = new JPanel(new BorderLayout((int)(10*SCALE), 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)(34 * SCALE)));

        JLabel lblTitle = new JLabel("Thực đơn");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, (int)(35*SCALE)));
        lblTitle.setPreferredSize(new Dimension(LABEL_W, FIELD_H));

        JPanel searchWrapper = new JPanel(new BorderLayout((int)(6*SCALE), 0));
        searchWrapper.setOpaque(false);
//        searchWrapper.setMaximumSize(new Dimension((int)(420*SCALE), FIELD_H));
        int SEARCH_H = (int)(34 * SCALE);

        searchWrapper.setMaximumSize(new Dimension((int)(420*SCALE), SEARCH_H));

        // SEARCH
        txtSearch = new JTextField("Nhập tên món ăn cần tìm ...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(0, SEARCH_H));
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)(34*SCALE)));


        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Nhập tên món ăn cần tìm ...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Nhập tên món ăn cần tìm ...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190,185,170)),
                BorderFactory.createEmptyBorder(
                        (int)(5*SCALE),(int)(10*SCALE),
                        (int)(5*SCALE),(int)(10*SCALE))));

        JButton btnSearch = new JButton();
        btnSearch.setFocusPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon icon = new ImageIcon("img/mn_tracuu.png");
        Image img = icon.getImage().getScaledInstance((int)(20*SCALE),(int)(20*SCALE), Image.SCALE_SMOOTH);
        btnSearch.setIcon(new ImageIcon(img));

        searchWrapper.add(txtSearch, BorderLayout.CENTER);
        searchWrapper.add(btnSearch, BorderLayout.EAST);

        row1.add(lblTitle, BorderLayout.WEST);
        row1.add(searchWrapper, BorderLayout.CENTER);

        // ===== ROW 2 =====
        JPanel row2 = new JPanel(new BorderLayout((int)(6*SCALE), 0));
        row2.setOpaque(false);

        JLabel lblGia = new JLabel("Tìm theo Giá", SwingConstants.RIGHT);
        lblGia.setFont(new Font("Times New Roman", Font.PLAIN, (int)(20*SCALE)));
        lblGia.setPreferredSize(new Dimension((int)(110*SCALE), FIELD_H));

        JPanel filterInner = new JPanel(new FlowLayout(FlowLayout.LEFT, (int)(8*SCALE), 0));
        filterInner.setOpaque(false);

        txtGiaTu = new JTextField();
        txtGiaDen = new JTextField();

        txtGiaTu.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(190,185,170)), "Từ"));
        txtGiaDen.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(190,185,170)), "Đến"));

        txtGiaTu.setPreferredSize(new Dimension(WIDTH, FIELD_H));
        txtGiaDen.setPreferredSize(new Dimension(WIDTH, FIELD_H));

        // NÚT LỌC
        btnLoc = new JButton("Lọc") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(C_BTN_LOC);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        btnLoc.setBackground(new Color(150, 105, 50)); // màu nâu
        btnLoc.setForeground(Color.WHITE);

        btnLoc.setFocusPainted(false);
        btnLoc.setBorderPainted(false);
        btnLoc.setContentAreaFilled(true); // PHẢI true

        btnLoc.setOpaque(true); // 🔥 QUAN TRỌNG (nhiều người quên)

        btnLoc.setPreferredSize(new Dimension(WIDTH, FIELD_H));

        filterInner.add(txtGiaTu);
        filterInner.add(txtGiaDen);
        filterInner.add(btnLoc);
        
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // đẩy sang phải bằng padding
        wrapper.setBorder(BorderFactory.createEmptyBorder(
            0,
            (int)(36 * SCALE), 
            0,
            0
        ));

        wrapper.add(filterInner, BorderLayout.WEST);

        row2.add(lblGia, BorderLayout.WEST);
        row2.add(wrapper, BorderLayout.CENTER);

        left.add(row1);
        left.add(Box.createVerticalStrut((int)(18*SCALE)));
        left.add(row2);

        // ================= RIGHT (KHÔNG GridLayout) =================
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        int gap = (int)(8 * SCALE);

        JPanel rowTop = new JPanel(new FlowLayout(FlowLayout.CENTER, gap, gap));
        rowTop.setOpaque(false);

        JPanel rowBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, gap, gap));
        rowBottom.setOpaque(false);

        btnThem = createBigButton("+ Thêm", C_BTN_ADD);
        btnCapNhat = createBigButton("Cập nhật", C_BTN_UPD);
        btnChiTiet = createBigButton("Chi tiết", C_BTN_DTL);
        btnLamMoi = createBigButton("Làm mới", C_BTN_REF);

        Font bigFont = new Font("Times New Roman", Font.BOLD, (int)(17*SCALE));

        for (JButton b : new JButton[]{btnThem, btnCapNhat, btnChiTiet, btnLamMoi}) {
            b.setFont(bigFont);
            b.setMargin(new Insets(
            	    (int)(8*SCALE),
            	    (int)(22*SCALE),
            	    (int)(8*SCALE),
            	    (int)(22*SCALE)
            	));
        }

        rowTop.add(btnThem);
        rowTop.add(btnCapNhat);
        rowBottom.add(btnChiTiet);
        rowBottom.add(btnLamMoi);

        right.add(rowTop);
        right.add(rowBottom);

        // ================= ADD =================
        actionRow.add(left, BorderLayout.CENTER);
        actionRow.add(right, BorderLayout.EAST);

        // ================= EVENTS =================
        btnLoc.addActionListener(e -> locTheoGiaVaTen());
        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());
        btnLamMoi.addActionListener(e -> refreshAll());
        
        btnThem.addActionListener(e -> {
            String maMoi = monDAO.getNextMaMon();

            XuLyMonAn_DigLog dlg = new XuLyMonAn_DigLog(
                    this,
                    XuLyMonAn_DigLog.Mode.THEM,
                    null,
                    maMoi
            );

            dlg.setVisible(true);

            if (dlg.isSaved()) {
                refreshAll(); // reload lại card
            }
        });
        
        
        btnCapNhat.addActionListener(e -> {
            if (selectedMon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn món!");
                return;
            }

            XuLyMonAn_DigLog dlg = new XuLyMonAn_DigLog(
                    this,
                    XuLyMonAn_DigLog.Mode.CAP_NHAT,
                    selectedMon,
                    null
            );

            dlg.setVisible(true);

            if (dlg.isSaved()) {
                refreshAll();
            }
        });
        
        
        btnChiTiet.addActionListener(e -> {
            if (selectedMon == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn một món ăn trước!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            XuLyMonAn_DigLog dlg = new XuLyMonAn_DigLog(
                    this,
                    XuLyMonAn_DigLog.Mode.CHI_TIET,
                    selectedMon,
                    null
            );

            dlg.setVisible(true);
        });
        return actionRow;
    }

    private JPanel buildContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_BG);

        pTabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pTabBar.setBackground(C_TAB_BAR);
//        pTabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDER));
        pTabBar.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDER),
        	    BorderFactory.createEmptyBorder(0, PAD+2, 0, 0) // 👉 đẩy qua phải
        	));

        LoaiMonAn_DAO loaiDAO = new LoaiMonAn_DAO();
        List<LoaiMonAn> dsLoai = loaiDAO.getAllLoaiMonAn();

        // ===== TAB ALL =====
        JButton btnAll = new JButton("Tất cả");
        btnAll.setFont(new Font("Times New Roman", Font.BOLD, (int)(16 * SCALE)));
        btnAll.setBackground(C_TAB_ACT);
        btnAll.setBorderPainted(false);
        btnAll.setFocusPainted(false);
        btnAll.setOpaque(true);

        btnAll.addActionListener(e -> {
            currentCategory = null;
            setActiveTab(btnAll);
            showOrLoadCategory("ALL");
        });

        pTabBar.add(btnAll);
        activeTabBtn = btnAll;

        // ===== TAB ĐỘNG =====
        for (LoaiMonAn loai : dsLoai) {

            JButton btn = new JButton(loai.getTenLoaiMonAn());

            btn.setFont(new Font("Times New Roman", Font.PLAIN, (int)(16 * SCALE)));
            btn.setBackground(C_TAB_BAR);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            String maLoai = loai.getMaLoaiMonAn();

            btn.addActionListener(e -> {
                currentCategory = maLoai;
                setActiveTab(btn);
                showOrLoadCategory(maLoai);
            });

            pTabBar.add(btn);
        }

        pCards = new JPanel(cardLayout);
        pCards.setBackground(C_BG);
        pCards.add(makeLoadingPanel("Dang tai du lieu..."), "LOADING");
        cardLayout.show(pCards, "LOADING");

        main.add(pTabBar, BorderLayout.NORTH);
        main.add(pCards,  BorderLayout.CENTER);
        return main;
    }
    
    
    public void reloadTabs() {
        pTabBar.removeAll();

        // gọi lại buildContent phần tab
        LoaiMonAn_DAO loaiDAO = new LoaiMonAn_DAO();
        List<LoaiMonAn> dsLoai = loaiDAO.getAllLoaiMonAn();

        JButton btnAll = new JButton("Tất cả");
        btnAll.addActionListener(e -> {
            currentCategory = null;
            setActiveTab(btnAll);
            showOrLoadCategory("ALL");
        });

        pTabBar.add(btnAll);

        for (LoaiMonAn loai : dsLoai) {
            JButton btn = new JButton(loai.getTenLoaiMonAn());

            String maLoai = loai.getMaLoaiMonAn();

            btn.addActionListener(e -> {
                currentCategory = maLoai;
                setActiveTab(btn);
                showOrLoadCategory(maLoai);
            });

            pTabBar.add(btn);
        }

        pTabBar.revalidate();
        pTabBar.repaint();
    }

    private void locTheoGiaVaTen() {

        String ten = txtSearch.getText().trim();
        if (ten.equalsIgnoreCase("Nhập tên món ăn cần tìm ...")) ten = "";
        ten = ten.toLowerCase();

        double giaTu = 0, giaDen = Double.MAX_VALUE;

        // 🔥 xử lý sạch dữ liệu nhập
        String rawTu  = txtGiaTu.getText().trim().replaceAll("[^0-9]", "");
        String rawDen = txtGiaDen.getText().trim().replaceAll("[^0-9]", "");

        try {
            if (!rawTu.isEmpty())  giaTu  = Double.parseDouble(rawTu);
            if (!rawDen.isEmpty()) giaDen = Double.parseDouble(rawDen);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Giá không hợp lệ! Vui lòng nhập số.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate
        if (!rawTu.isEmpty() && !rawDen.isEmpty() && giaDen < giaTu) {
            JOptionPane.showMessageDialog(this,
                    "Giá đến không được nhỏ hơn giá từ!",
                    "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtGiaDen.requestFocus();
            txtGiaDen.selectAll();
            return;
        }

        // lấy cache
        List<MonAn> all = cache.getOrDefault("ALL", new ArrayList<>());
        if (all.isEmpty()) {
            all = monDAO.getAllMonAn(); // chỉ load 1 lần
            cache.put("ALL", all);
        }

        List<MonAn> result = new ArrayList<>();

        for (MonAn mon : all) {

            String tenMon = mon.getTenMon() == null ? "" : mon.getTenMon().toLowerCase();
            boolean matchTen = ten.isEmpty() || tenMon.contains(ten);

            double gia = mon.getDonGia();
            boolean matchGia = gia >= giaTu && gia <= giaDen;

            // 🔥 FIX CHUẨN THEO ENTITY CỦA BẠN
            boolean matchLoai = currentCategory == null
                    || mon.getMaLoaiMonAn().getMaLoaiMonAn().equals(currentCategory);

            if (matchTen && matchGia && matchLoai) {
                result.add(mon);
            }
        }

        // hiển thị
        for (Component c : pCards.getComponents())
            if ("FILTER".equals(c.getName())) {
                pCards.remove(c);
                break;
            }

        JScrollPane scroll = buildGridScroll(result, "FILTER");
        pCards.add(scroll, "FILTER");

        pCards.revalidate();
        pCards.repaint(); // 🔥 thêm cái này cho chắc
        cardLayout.show(pCards, "FILTER");

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy món ăn phù hợp.",
                    "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void preloadCategory(final String key) {
        if (cache.containsKey(key)) { ensureCardPanelExists(key); cardLayout.show(pCards, key); return; }
        new SwingWorker<List<MonAn>, Void>() {
            @Override protected List<MonAn> doInBackground() {
                return "ALL".equals(key) ? monDAO.getAllMonAn() : monDAO.getMonTheoLoai(key);
            }
            @Override protected void done() {
                try {
                    List<MonAn> list = get();
                    cache.put(key, list);
                    ensureCardPanelExists(key);
                    cardLayout.show(pCards, key);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void ensureCardPanelExists(String key) {
        for (Component c : pCards.getComponents()) if (key.equals(c.getName())) return;
        pCards.add(buildGridScroll(cache.getOrDefault(key, Collections.emptyList()), key), key);
        pCards.revalidate();
    }

    private JScrollPane buildGridScroll(List<MonAn> list, String key) {
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP)) {
            @Override public Dimension getPreferredSize() {
                int total = getComponentCount();
                if (total == 0) return new Dimension(200, 200);
                int vw = (getParent() != null) ? getParent().getWidth() : 900;
                int cols = Math.max(1, vw / (CARD_W + GAP));
                int rows = (int) Math.ceil((double) total / cols);
                return new Dimension(vw, rows * (CARD_H + GAP) + 2*PAD);
            }
        };
        grid.setBackground(C_BG);
        grid.setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
        grid.setName(key);
        for (MonAn mon : list) grid.add(buildCard(mon));
        JScrollPane scroll = new JScrollPane(grid,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.setBorder(null);
        scroll.setName(key);
        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { grid.revalidate(); }
        });
        return scroll;
    }

    private void showOrLoadCategory(String key) {
        selectedCard = null; selectedMon = null; preloadCategory(key);
    }

    private JPanel buildCard(MonAn mon) {

        boolean isStop = !mon.isTrangThai();

        // ===== CARD =====
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(CARD_W, CARD_H));

        Color bgColor = isStop ? C_STOP : C_CARD;
        card.setBackground(bgColor);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // ===== IMAGE WRAPPER =====
        JPanel imgWrapper = new JPanel(new BorderLayout());
        imgWrapper.setBackground(isStop ? C_STOP : new Color(237, 230, 213));
        imgWrapper.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel lblImg = new JLabel("Không có ảnh", SwingConstants.CENTER);
        lblImg.setPreferredSize(new Dimension(IMG_W, IMG_H));
        lblImg.setFont(new Font("Arial", Font.PLAIN, 13));

        loadImgAsync(lblImg, mon.getAnhMon(), IMG_W, IMG_H);

        imgWrapper.add(lblImg, BorderLayout.CENTER);

        // ===== INFO =====
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(bgColor);
        info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ===== NAME =====
        JLabel lblName = new JLabel("<html><b>" + mon.getTenMon() + "</b></html>");
        lblName.setFont(new Font("Times New Roman", Font.BOLD, 17));
        lblName.setForeground(isStop ? Color.WHITE : new Color(50, 40, 25));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== DESC =====
        String desc = (mon.getMoTa() != null && !mon.getMoTa().isEmpty()) ? mon.getMoTa() : " ";
        JLabel lblDesc = new JLabel("<html><i>" + truncate(desc, 35) + "</i></html>");
        lblDesc.setFont(new Font("Arial", Font.ITALIC, 13));
        lblDesc.setForeground(isStop ? Color.WHITE : new Color(130, 120, 100));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== PRICE =====
        JLabel lblPrice = new JLabel(String.format("%,.0f", mon.getDonGia()));
        lblPrice.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblPrice.setForeground(isStop ? Color.WHITE : C_BROWN);
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrice.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== ADD COMPONENT =====
        info.add(Box.createVerticalStrut(6));
        info.add(lblName);
        info.add(Box.createVerticalStrut(4));
        info.add(lblDesc);
        info.add(Box.createVerticalStrut(6));
        info.add(lblPrice);

        // ===== ADD TO CARD =====
        card.add(imgWrapper, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);

        // ===== CLICK SELECT =====
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedCard != null) {
                    deselect(selectedCard);
                }
                select(card, mon, imgWrapper, info);
            }
        });

        return card;
    }

    private void select(JPanel card, MonAn mon, JPanel imgWrapper, JPanel info) {
        selectedCard = card;
        selectedMon = mon;

        Color bgColor = mon.isTrangThai() ? C_CARD : C_STOP;

        card.setBackground(bgColor);
        imgWrapper.setBackground(C_SELECTED);
        info.setBackground(C_SELECTED);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 100, 185), 3),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        card.repaint();
    }

//    private void deselect(JPanel card) {
//        card.setBackground(C_CARD);
//        Component[] c = card.getComponents();
//        if (c.length > 0 && c[0] instanceof JPanel) c[0].setBackground(new Color(237, 230, 213));
//        if (c.length > 1 && c[1] instanceof JPanel) c[1].setBackground(C_CARD);
//        card.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(C_BORDER, 1), BorderFactory.createEmptyBorder(8,8,8,8)));
//        card.repaint();
//    }
    private void deselect(JPanel card) {
        if (card == null) return;

        // lấy lại món tương ứng với card đang bỏ chọn
        MonAn mon = selectedMon;

        Color bgColor = (mon != null && !mon.isTrangThai()) ? C_STOP : C_CARD;

        card.setBackground(bgColor);

        Component[] c = card.getComponents();

        if (c.length > 0 && c[0] instanceof JPanel) {
            c[0].setBackground(mon != null && !mon.isTrangThai()
                    ? C_STOP
                    : new Color(237, 230, 213));
        }

        if (c.length > 1 && c[1] instanceof JPanel) {
            c[1].setBackground(bgColor);
        }

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        card.repaint();
    }

    private void loadImgAsync(JLabel lbl, String path, int w, int h) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    if (path == null || path.trim().isEmpty()) return null;

                    String name = path.trim();

                    // bỏ extension nếu DB ghi sai
                    if (name.contains(".")) {
                        name = name.substring(0, name.lastIndexOf("."));
                    }

                    String baseDir = System.getProperty("user.dir") + File.separator + "img";

                    String[] exts = {".png", ".jpg", ".jpeg"};

                    for (String ext : exts) {
                        File file = new File(baseDir + File.separator + name + ext);

//                        System.out.println("TRY: " + file.getAbsolutePath());

                        if (file.exists()) {
                            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                            return new ImageIcon(img);
                        }
                    }

//                    System.out.println("❌ KHÔNG TÌM THẤY: " + path);
                    return null;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon ic = get();
                    if (ic != null) {
                        lbl.setIcon(ic);
                        lbl.setText("");
                    } else {
                        lbl.setText("Không có ảnh");
                    }
                } catch (Exception e) {
                    lbl.setText("Lỗi ảnh");
                }
            }
        }.execute();
    }

    private void doSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { cardLayout.show(pCards, "ALL"); return; }
        for (Component c : pCards.getComponents())
            if ("SEARCH".equals(c.getName())) { pCards.remove(c); break; }
        List<MonAn> result = monDAO.timMonTheoTen(kw);
        JScrollPane scroll = buildGridScroll(result, "SEARCH");
        pCards.add(scroll, "SEARCH");
        pCards.revalidate();
        cardLayout.show(pCards, "SEARCH");
    }

    private void refreshAll() {

        // ===== reset text =====
        txtSearch.setText("");
        txtGiaTu.setText("");
        txtGiaDen.setText("");

        // ===== reset placeholder (nếu có) =====
        txtSearch.setText("Nhập tên món ăn cần tìm ...");

        // ===== reset selected =====
        selectedMon = null;
        selectedCard = null;

        // ===== load lại ALL =====
        List<MonAn> all = cache.get("ALL");
        if (all == null || all.isEmpty()) {
            all = monDAO.getAllMonAn();
            cache.put("ALL", all);
        }

        // ===== hiển thị lại =====
        for (Component c : pCards.getComponents())
            if ("FILTER".equals(c.getName())) {
                pCards.remove(c);
                break;
            }

        JScrollPane scroll = buildGridScroll(all, "ALL");
        pCards.add(scroll, "ALL");

        pCards.revalidate();
        pCards.repaint();

        cardLayout.show(pCards, "ALL");
    }

    private JPanel makeLoadingPanel(String msg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG); p.setName("LOADING");
        JLabel l = new JLabel(msg, SwingConstants.CENTER);
        l.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        l.setForeground(new Color(150, 140, 120));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private void setActiveTab(JButton btn) {
        for (Component c : pTabBar.getComponents())
            if (c instanceof JButton) {
                c.setBackground(C_TAB_BAR);
                ((JButton) c).setFont(new Font("Times New Roman", Font.PLAIN, (int)(16 * SCALE)));
            }
        btn.setBackground(C_TAB_ACT);
        btn.setFont(new Font("Times New Roman", Font.BOLD, (int)(16 * SCALE)));
        activeTabBtn = btn;
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "..." : s;
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "auto");
        UIManager.put("Label.font",       new Font("SansSerif", Font.PLAIN,  (int)(20 * SCALE)));
        UIManager.put("Button.font",      new Font("SansSerif", Font.BOLD,   (int)(15 * SCALE)));
        UIManager.put("TextField.font",   new Font("SansSerif", Font.PLAIN,  (int)(13 * SCALE)));
        UIManager.put("ComboBox.font",    new Font("SansSerif", Font.PLAIN,  (int)(13 * SCALE)));
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new ThucDon_GUI().setVisible(true);
            
        });
    }
}