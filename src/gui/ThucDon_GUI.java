package gui;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import digLog.XuLyMonAn_DigLog;
import entity.LoaiMonAn;
import entity.MonAn;

public class ThucDon_GUI extends JPanel {


    private static final long serialVersionUID = 1L;

    private static final Color C_BG = new Color(246, 242, 231);
    private static final Color C_TOP = Color.WHITE;
    private static final Color C_TAB_BAR = new Color(232, 224, 202);
    private static final Color C_TAB_ACTIVE = Color.WHITE;
    private static final Color C_CARD = Color.WHITE;
    private static final Color C_CARD_IMG = new Color(239, 234, 218);
    private static final Color C_BORDER = new Color(214, 207, 189);
    private static final Color C_TEXT = new Color(45, 35, 25);
    private static final Color C_MUTED = new Color(120, 110, 95);
    private static final Color C_SELECTED = new Color(86, 153, 232);
    private static final Color C_STOP = new Color(218, 64, 64);

    private static final Color C_BTN_ADD = new Color(42, 170, 76);
    private static final Color C_BTN_UPD = new Color(212, 166, 28);
    private static final Color C_BTN_DTL = new Color(58, 135, 220);
    private static final Color C_BTN_REF = new Color(230, 116, 35);
    private static final Color C_BTN_LOC = new Color(150, 106, 48);

    private static final int CARD_W = 235;
    private static final int CARD_H = 315;
    private static final int IMG_W = 205;
    private static final int IMG_H = 160;
    private static final int GAP = 18;
    private static final int PAD = 22;

    private final MonAn_DAO monDAO = new MonAn_DAO();
    private final Map<String, List<MonAn>> cache = new HashMap<>();

    private String currentCategory = null;
    private MonAn selectedMon = null;
    private JPanel selectedCard = null;

    private CardLayout cardLayout = new CardLayout();
    private JPanel pCards;
    private JPanel pTabBar;
    private JButton activeTabBtn;

    private JTextField txtSearch;
    private RoundedComboBox cboGiaTu;
    private RoundedComboBox cboGiaDen;
    private JTextField txtGiaTu;
    private JTextField txtGiaDen;
    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnChiTiet;
    private JButton btnLamMoi;
    private JButton btnLoc;
   


    private Timer searchTimer;

    private static final double UI_SCALE = calcUiScale();

    private static double calcUiScale() {
        try {
            AffineTransform at = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration()
                    .getDefaultTransform();

            double raw = at.getScaleX();

            // Java Swing trên Mac Retina đã tự scale rồi.
            // Không nhân 2 lần để tránh UI quá to.
            if (raw >= 1.5) {
                return 1.0;
            }

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            double byWidth = screen.width / 1440.0;

            return Math.max(0.88, Math.min(1.12, byWidth));
        } catch (Exception e) {
            return 1.0;
        }
    }
    private final String[] GOI_Y_GIA = {
            "", "30000", "50000", "70000", "100000",
            "150000", "200000", "300000", "500000"
    };

    private static int sc(int value) {
        return (int) Math.round(value * UI_SCALE);
    }

    public ThucDon_GUI() {
        setLayout(new BorderLayout());
        setBackground(C_BG);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        searchTimer = new Timer(280, e -> doSearch());
        searchTimer.setRepeats(false);


        loadAllFirstTime();
    }

    private void loadAllFirstTime() {
        List<MonAn> list = monDAO.getAllMonAn();
        cache.put("ALL", list);

        JScrollPane scroll = buildGridScroll(list, "ALL");
        pCards.add(scroll, "ALL");
        cardLayout.show(pCards, "ALL");
    }


    private JPanel buildTopBar() {
        JPanel root = new JPanel(new BorderLayout(sc(18), 0));
        root.setBackground(C_TOP);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
                new EmptyBorder(sc(18), sc(26), sc(16), sc(26))
        ));

        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, sc(10), sc(10));
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Thực đơn");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, sc(34)));
        lblTitle.setForeground(Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        left.add(lblTitle, gbc);

        JPanel searchWrap = new RoundedPanel(new BorderLayout(sc(8), 0), sc(16));
        searchWrap.setPreferredSize(new Dimension(sc(560), sc(44)));
        searchWrap.setMinimumSize(new Dimension(sc(300), sc(44)));
        searchWrap.setBorder(new EmptyBorder(0, sc(14), 0, sc(12)));
        searchWrap.setMinimumSize(new Dimension(sc(260), sc(42)));

        txtSearch = createSearchField("Nhập tên món ăn cần tìm ...");

        JButton btnSearch = new JButton();
        btnSearch.setIcon(loadIcon("img/mn_tracuu.png", sc(24), sc(24)));
        btnSearch.setFocusPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnSearch.addActionListener(e -> doSearch());

        searchWrap.add(txtSearch, BorderLayout.CENTER);
        searchWrap.add(btnSearch, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        left.add(searchWrap, gbc);

        JLabel lblGia = new JLabel("Tìm theo giá");
        lblGia.setFont(new Font("Times New Roman", Font.BOLD, sc(22)));
        lblGia.setForeground(Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, sc(10));
        left.add(lblGia, gbc);

        cboGiaTu = createGiaCombo();
        cboGiaDen = createGiaCombo();

        txtGiaTu = (JTextField) cboGiaTu.getEditor().getEditorComponent();
        txtGiaDen = (JTextField) cboGiaDen.getEditor().getEditorComponent();

        btnLoc = createSmallButton("Lọc", C_BTN_LOC);
        btnLoc.addActionListener(e -> locTheoGiaVaTen(true));

        JPanel priceWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, sc(10), 0));
        priceWrap.setOpaque(false);

        JLabel lblTu = new JLabel("Từ");
        lblTu.setFont(new Font("SansSerif", Font.BOLD, sc(17)));


        JLabel lblDen = new JLabel("Đến");
        lblDen.setFont(new Font("SansSerif", Font.BOLD, sc(17)));

        priceWrap.add(lblTu);
        priceWrap.add(cboGiaTu);
        priceWrap.add(lblDen);
        priceWrap.add(cboGiaDen);
        priceWrap.add(btnLoc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        left.add(priceWrap, gbc);

        JPanel right = new JPanel(new GridLayout(2, 2, sc(12), sc(12)));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(sc(430), sc(122)));

        btnThem = createBigButton("+ Thêm", C_BTN_ADD);
        btnCapNhat = createBigButton("Cập nhật", C_BTN_UPD);
        btnChiTiet = createBigButton("Chi tiết", C_BTN_DTL);
        btnLamMoi = createBigButton("Làm mới", C_BTN_REF);

        right.add(btnThem);
        right.add(btnCapNhat);
        right.add(btnChiTiet);
        right.add(btnLamMoi);

        root.add(left, BorderLayout.CENTER);
        root.add(right, BorderLayout.EAST);

        initTopEvents();

        return root;
    }


    private void initTopEvents() {
        txtSearch.addActionListener(e -> doSearch());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchTimer.restart();
            }
        });

        btnLamMoi.addActionListener(e -> refreshAll());

        btnThem.addActionListener(e -> {
            JFrame parentFrame = getParentFrame();
            String maMoi = monDAO.getNextMaMon();
            XuLyMonAn_DigLog dlg = new XuLyMonAn_DigLog(

                    parentFrame,
                    XuLyMonAn_DigLog.Mode.THEM,
                    null,
                    maMoi
            );

            dlg.setLocationRelativeTo(parentFrame);
            dlg.setVisible(true);
            if (dlg.isSaved()) refreshAll();
        });


        btnCapNhat.addActionListener(e -> {
            if (selectedMon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn món!");
                return;
            }

            JFrame parentFrame = getParentFrame();

            XuLyMonAn_DigLog dlg = new XuLyMonAn_DigLog(
                    parentFrame,
                    XuLyMonAn_DigLog.Mode.CAP_NHAT,
                    selectedMon,
                    null
            );

            dlg.setLocationRelativeTo(parentFrame);
            dlg.setVisible(true);

            if (dlg.isSaved()) {
                refreshAll();
            }
        });

        btnChiTiet.addActionListener(e -> {
            if (selectedMon == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một món ăn trước!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }


            JFrame parentFrame = getParentFrame();

            XuLyMonAn_DigLog dlg = new XuLyMonAn_DigLog(
                    parentFrame,
                    XuLyMonAn_DigLog.Mode.CHI_TIET,
                    selectedMon,
                    null
            );

            dlg.setLocationRelativeTo(parentFrame);
            dlg.setVisible(true);
        });
    }

    private JFrame getParentFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame) {
            return (JFrame) w;
        }
        return null;
    }

    private JTextField createSearchField(String placeholder) {
        JTextField txt = new JTextField(placeholder);
        txt.setFont(new Font("SansSerif", Font.PLAIN, sc(15)));
        txt.setForeground(Color.GRAY);
        txt.setOpaque(false);
        txt.setBorder(new EmptyBorder(0, sc(14), 0, sc(14)));

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(Color.GRAY);
                }
            }
        });

        return txt;
    }

    private JTextField createPriceField(String title) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(sc(150), sc(42)));
        txt.setFont(new Font("SansSerif", Font.PLAIN, sc(14)));
        txt.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(190, 185, 170), 1),
                title
        ));
        return txt;
    }

    private JButton createBigButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c = bg;
                if (getModel().isPressed()) {
                    c = bg.darker();
                } else if (getModel().isRollover()) {
                    c = bg.brighter();
                }

                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), sc(14), sc(14));
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Times New Roman", Font.BOLD, sc(23)));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JButton createSmallButton(String text, Color bg) {
        JButton btn = createBigButton(text, bg);
        btn.setFont(new Font("SansSerif", Font.BOLD, sc(16)));
        btn.setPreferredSize(new Dimension(sc(95), sc(40)));
        return btn;
    }
    private JPanel buildContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_BG);

        pTabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, sc(8), sc(6)));
        pTabBar.setBackground(C_TAB_BAR);
        pTabBar.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
                new EmptyBorder(0, sc(18), 0, sc(18))
        ));

        buildTabs();


        pCards = new JPanel(cardLayout);
        pCards.setBackground(C_BG);
        pCards.add(makeLoadingPanel("Đang tải dữ liệu..."), "LOADING");
        cardLayout.show(pCards, "LOADING");

        main.add(pTabBar, BorderLayout.NORTH);
        main.add(pCards, BorderLayout.CENTER);

        return main;
    }

    private void buildTabs() {
        pTabBar.removeAll();

        JButton btnAll = createTabButton("Tất cả", true);
        btnAll.addActionListener(e -> {
            currentCategory = null;
            setActiveTab(btnAll);
            showOrLoadCategory("ALL");
        });

        pTabBar.add(btnAll);
        activeTabBtn = btnAll;


        LoaiMonAn_DAO loaiDAO = new LoaiMonAn_DAO();
        List<LoaiMonAn> dsLoai = loaiDAO.getAllLoaiMonAn();

        for (LoaiMonAn loai : dsLoai) {
            JButton btn = createTabButton(loai.getTenLoaiMonAn(), false);
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


    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Times New Roman", active ? Font.BOLD : Font.PLAIN, sc(20)));
        btn.setForeground(Color.BLACK);
        btn.setBackground(active ? C_TAB_ACTIVE : C_TAB_BAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(sc(8), sc(14), sc(8), sc(14)));

        return btn;
    }

    private void setActiveTab(JButton btn) {
        for (Component c : pTabBar.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.setBackground(C_TAB_BAR);
                b.setFont(new Font("Times New Roman", Font.PLAIN, sc(20)));
            }
        }

        btn.setBackground(C_TAB_ACTIVE);
        btn.setFont(new Font("Times New Roman", Font.BOLD, sc(20)));
        activeTabBtn = btn;
    }

    public void reloadTabs() {
        buildTabs();
    }

    private void showOrLoadCategory(String key) {
        selectedMon = null;
        selectedCard = null;
        preloadCategory(key);
    }

    private void preloadCategory(final String key) {
        if (cache.containsKey(key)) {
            ensureCardPanelExists(key);
            cardLayout.show(pCards, key);
            return;
        }

        pCards.add(makeLoadingPanel("Đang tải dữ liệu..."), "LOADING");
        cardLayout.show(pCards, "LOADING");

        new SwingWorker<List<MonAn>, Void>() {
            @Override
            protected List<MonAn> doInBackground() {
                if ("ALL".equals(key)) {
                    return monDAO.getAllMonAn();
                }
                return monDAO.getMonTheoLoai(key);
            }

            @Override
            protected void done() {
                try {
                    List<MonAn> list = get();
                    cache.put(key, list);
                    ensureCardPanelExists(key);
                    cardLayout.show(pCards, key);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void ensureCardPanelExists(String key) {
        for (Component c : pCards.getComponents()) {
            if (key.equals(c.getName())) {
                return;
            }
        }

        JScrollPane scroll = buildGridScroll(cache.getOrDefault(key, Collections.emptyList()), key);
        pCards.add(scroll, key);
        pCards.revalidate();
        pCards.repaint();
    }

    private JScrollPane buildGridScroll(List<MonAn> list, String key) {
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP, GAP)) {

            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                int total = getComponentCount();
                if (total == 0) {
                    return new Dimension(400, 240);
                }

                int vw = 1000;
                if (getParent() != null) {
                    vw = Math.max(700, getParent().getWidth());
                }

                int cols = Math.max(1, (vw - PAD * 2) / (CARD_W + GAP));
                int rows = (int) Math.ceil((double) total / cols);
                int height = rows * (CARD_H + GAP) + PAD * 2;

                return new Dimension(vw, height);
            }
        };

        grid.setName(key);

        grid.setBackground(C_BG);
        grid.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));

        for (MonAn mon : list) {
            grid.add(buildCard(mon));
        }

        JScrollPane scroll = new JScrollPane(
                grid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setName(key);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(24);

        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                grid.revalidate();
                grid.repaint();
            }
        });

        return scroll;
    }

    private JPanel buildCard(MonAn mon) {

        boolean stop = !mon.isTrangThai();

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(CARD_W, CARD_H));
        card.setBackground(stop ? C_STOP : C_CARD);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel imgWrap = new JPanel(new BorderLayout());
        imgWrap.setBackground(stop ? C_STOP : C_CARD_IMG);
        imgWrap.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel lblImg = new JLabel("Không có ảnh", SwingConstants.CENTER);
        lblImg.setPreferredSize(new Dimension(IMG_W, IMG_H));
        lblImg.setFont(new Font("SansSerif", Font.PLAIN, sc(12)));
        lblImg.setForeground(C_MUTED);

        loadImgAsync(lblImg, mon.getAnhMon(), IMG_W, IMG_H);

        imgWrap.add(lblImg, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setBackground(stop ? C_STOP : C_CARD);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(8, 6, 4, 6));


        JLabel lblName = new JLabel(
                "<html><div style='text-align:center; width:190px;'><b>"
                        + safeHtml(mon.getTenMon()) +
                "</b></div></html>",
                SwingConstants.CENTER
        );
        lblName.setFont(new Font("Times New Roman", Font.BOLD, sc(16)));
        lblName.setForeground(stop ? Color.WHITE : C_TEXT);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setHorizontalAlignment(SwingConstants.CENTER);
        lblName.setMaximumSize(new Dimension(Integer.MAX_VALUE, sc(42)));



        String desc = mon.getMoTa() == null ? "" : mon.getMoTa().trim();

        JLabel lblDesc = new JLabel(
                "<html><div style='text-align:center; width:190px;'><i>"
                        + safeHtml(truncate(desc, 32)) +
                "</i></div></html>",
                SwingConstants.CENTER
        );
        lblDesc.setFont(new Font("SansSerif", Font.ITALIC, sc(12)));
        lblDesc.setForeground(stop ? Color.WHITE : C_MUTED);


        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, sc(28)));



        JLabel lblPrice = new JLabel(
                String.format("%,.0f", mon.getDonGia()).replace(",", ".") + " VNĐ",
                SwingConstants.CENTER
        );
        lblPrice.setFont(new Font("Times New Roman", Font.BOLD, sc(17)));
        lblPrice.setForeground(stop ? Color.WHITE : new Color(90, 65, 35));


        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrice.setMaximumSize(new Dimension(Integer.MAX_VALUE, sc(28)));



        info.add(lblName);
        info.add(Box.createVerticalStrut(4));
        info.add(lblDesc);
        info.add(Box.createVerticalStrut(6));
        info.add(lblPrice);



        card.add(imgWrap, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);

        card.putClientProperty("mon", mon);
        card.putClientProperty("imgWrap", imgWrap);
        card.putClientProperty("info", info);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedCard != null && selectedCard != card) {
                    deselect(selectedCard);
                }
                select(card, mon);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedCard != card) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(new Color(170, 160, 135), 2),
                            new EmptyBorder(7, 7, 7, 7)
                    ));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedCard != card) {
                    resetCardBorder(card);
                }


            }
        });
        return card;
    }
    
    private void select(JPanel card, MonAn mon) {
        selectedCard = card;
        selectedMon  = mon;



        JPanel imgWrap = (JPanel) card.getClientProperty("imgWrap");
        JPanel info = (JPanel) card.getClientProperty("info");

        imgWrap.setBackground(C_SELECTED);
        info.setBackground(C_SELECTED);

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(45, 105, 200), 3),
                new EmptyBorder(6, 6, 6, 6)
        ));

        card.repaint();
    }

    private void deselect(JPanel card) {


        MonAn mon = (MonAn) card.getClientProperty("mon");
        boolean stop = mon != null && !mon.isTrangThai();

        JPanel imgWrap = (JPanel) card.getClientProperty("imgWrap");
        JPanel info = (JPanel) card.getClientProperty("info");

        card.setBackground(stop ? C_STOP : C_CARD);
        imgWrap.setBackground(stop ? C_STOP : C_CARD_IMG);
        info.setBackground(stop ? C_STOP : C_CARD);

        resetCardBorder(card);

        card.repaint();
    }

    private void resetCardBorder(JPanel card) {
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C_BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
    }

    private void doSearch() {
        String kw = txtSearch.getText().trim();

        if (kw.equalsIgnoreCase("Nhập tên món ăn cần tìm ...")) {
            kw = "";
        }

        if (kw.isEmpty()) {
            if (currentCategory == null) {
                showOrLoadCategory("ALL");
            } else {
                showOrLoadCategory(currentCategory);
            }
            return;
        }

        List<MonAn> all = cache.get("ALL");
        if (all == null || all.isEmpty()) {
            all = monDAO.getAllMonAn();
            cache.put("ALL", all);
        }

        String keyword = kw.toLowerCase();
        List<MonAn> result = new ArrayList<>();

        for (MonAn mon : all) {
            String tenMon = mon.getTenMon() == null ? "" : mon.getTenMon().toLowerCase();

            boolean matchName = tenMon.contains(keyword);
            boolean matchLoai = currentCategory == null
                    || (mon.getMaLoaiMonAn() != null
                    && currentCategory.equals(mon.getMaLoaiMonAn().getMaLoaiMonAn()));

            if (matchName && matchLoai) {
                result.add(mon);
            }
        }

        showTempResult(result, "SEARCH", false);
    }

    private void locTheoGiaVaTen(boolean canShowError) {
        String ten = txtSearch.getText().trim();
        if (ten.equalsIgnoreCase("Nhập tên món ăn cần tìm ...")) {
            ten = "";
        }
        ten = ten.toLowerCase();

        double giaTu = 0;
        double giaDen = Double.MAX_VALUE;

        String rawTu = txtGiaTu.getText().trim().replaceAll("[^0-9]", "");
        String rawDen = txtGiaDen.getText().trim().replaceAll("[^0-9]", "");

        try {
            if (!rawTu.isEmpty()) {
                giaTu = Double.parseDouble(rawTu);
            }
            if (!rawDen.isEmpty()) {
                giaDen = Double.parseDouble(rawDen);
            }
        } catch (NumberFormatException ex) {
            if (canShowError) {
                JOptionPane.showMessageDialog(this, "Giá không hợp lệ! Vui lòng nhập số.");
            }
            return;
        }

        if (!rawTu.isEmpty() && !rawDen.isEmpty() && giaDen < giaTu) {
            if (canShowError) {
                JOptionPane.showMessageDialog(
                        this,
                        "Giá đến không được nhỏ hơn giá từ!",
                        "Lỗi nhập liệu",
                        JOptionPane.WARNING_MESSAGE
                );
                txtGiaDen.requestFocus();
                txtGiaDen.selectAll();
            }
            return;
        }

        List<MonAn> all = cache.get("ALL");
        if (all == null || all.isEmpty()) {
            all = monDAO.getAllMonAn();
            cache.put("ALL", all);
        }

        List<MonAn> result = new ArrayList<>();

        for (MonAn mon : all) {
            String tenMon = mon.getTenMon() == null ? "" : mon.getTenMon().toLowerCase();

            boolean matchTen = ten.isEmpty() || tenMon.contains(ten);
            boolean matchGia = mon.getDonGia() >= giaTu && mon.getDonGia() <= giaDen;

            boolean matchLoai = currentCategory == null
                    || (mon.getMaLoaiMonAn() != null
                    && currentCategory.equals(mon.getMaLoaiMonAn().getMaLoaiMonAn()));

            if (matchTen && matchGia && matchLoai) {
                result.add(mon);
            }
        }

        showTempResult(result, "FILTER", canShowError);
    }

    private void showTempResult(List<MonAn> result, String key, boolean showEmptyMessage) {
        removeCard(key);

        JScrollPane scroll = buildGridScroll(result, key);
        pCards.add(scroll, key);
        pCards.revalidate();
        pCards.repaint();

        cardLayout.show(pCards, key);

        selectedCard = null;
        selectedMon = null;

        if (showEmptyMessage && result.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy món ăn phù hợp.",
                    "Kết quả",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void removeCard(String key) {
        for (Component c : pCards.getComponents()) {
            if (key.equals(c.getName())) {
                pCards.remove(c);
                return;
            }
        }
    }

    private void refreshAll() {
        txtSearch.setText("Nhập tên món ăn cần tìm ...");
        txtSearch.setForeground(Color.GRAY);
        txtGiaTu.setText("");
        txtGiaDen.setText("");

        selectedMon = null;
        selectedCard = null;
        currentCategory = null;

        cache.clear();
        pCards.removeAll();

        pCards.add(makeLoadingPanel("Đang tải dữ liệu..."), "LOADING");
        cardLayout.show(pCards, "LOADING");

        buildTabs();
        loadAllFirstTime();

        pCards.revalidate();
        pCards.repaint();
    }

    private JPanel makeLoadingPanel(String msg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setName("LOADING");
        p.setBackground(C_BG);

        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, sc(20)));
        lbl.setForeground(C_MUTED);

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private void loadImgAsync(JLabel lbl, String path, int w, int h) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {


                    if (path == null || path.trim().isEmpty()) {
                        return null;
                    }

                    String name = path.trim();

                    if (name.contains(".")) {
                        name = name.substring(0, name.lastIndexOf("."));
                    }

                    String baseDir = System.getProperty("user.dir") + File.separator + "img";
                    String[] exts = {".png", ".jpg", ".jpeg"};

                    for (String ext : exts) {
                        File file = new File(baseDir + File.separator + name + ext);


                        if (file.exists()) {
                            Image img = new ImageIcon(file.getAbsolutePath())
                                    .getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                            return new ImageIcon(img);
                        }
                    }



                    return null;
                } catch (Exception e) {
                    return null;
                }

            }
            @Override
            protected void done() {
                try {


                    ImageIcon icon = get();

                    if (icon != null) {
                        lbl.setIcon(icon);
                        lbl.setText("");
                    } else {
                        lbl.setIcon(null);
                        lbl.setText("Không có ảnh");
                    }
                } catch (Exception e) {
                    lbl.setIcon(null);
                    lbl.setText("Lỗi ảnh");
                }

            }
        }.execute();
    }



    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() <= 0) {
                return null;
            }
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }

    }

    private String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }



    private String safeHtml(String s) {
        if (s == null) {
            return "";
        }

        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
    private RoundedComboBox createGiaCombo() {
        RoundedComboBox cbo = new RoundedComboBox();
        cbo.setModel(new DefaultComboBoxModel<>(GOI_Y_GIA));
        cbo.setEditable(true);
        cbo.setFont(new Font("SansSerif", Font.PLAIN, sc(16)));
        cbo.setPreferredSize(new Dimension(sc(125), sc(42)));
        cbo.setOpaque(false);
        cbo.setBorder(BorderFactory.createEmptyBorder());
        cbo.setUI(new NoArrowComboUI());

        JTextField editor = (JTextField) cbo.getEditor().getEditorComponent();
        editor.setFont(new Font("SansSerif", Font.PLAIN, sc(16)));
        editor.setBorder(new EmptyBorder(0, sc(10), 0, sc(10)));
        editor.setBackground(Color.WHITE);
        editor.setForeground(C_TEXT);
        editor.setOpaque(false);
        editor.setCaretColor(C_TEXT);
        editor.setHorizontalAlignment(SwingConstants.LEFT);

//        editor.addActionListener(e -> locTheoGiaVaTen());

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
            	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            	    locTheoGiaVaTen(true); // Enter mới báo lỗi
            	    return;
            	}

                if (e.getKeyCode() == KeyEvent.VK_UP
                        || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    locGoiYGia(cbo, editor.getText());
                    cbo.showPopup();
                    editor.requestFocus();
                    editor.setCaretPosition(editor.getText().length());
                    locTheoGiaVaTen(false); // gõ thường vẫn lọc nhưng không báo lỗi
                });
            }

        });

        editor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    locGoiYGia(cbo, editor.getText());
                    cbo.showPopup();
                    editor.requestFocus();
                    editor.setCaretPosition(editor.getText().length());
                });
            }
        });

        return cbo;
    }
    private class RoundedPanel extends JPanel {
        private final int arc;

        public RoundedPanel(LayoutManager layout, int arc) {
            super(layout);
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            g2.setColor(new Color(190, 185, 170));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class RoundedComboBox extends JComboBox<String> {
        public RoundedComboBox() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sc(14), sc(14));

            g2.setColor(new Color(190, 185, 170));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sc(14), sc(14));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class NoArrowComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(120, 120, 120));

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    g2.drawLine(cx - sc(7), cy - sc(4), cx, cy + sc(4));
                    g2.drawLine(cx, cy + sc(4), cx + sc(7), cy - sc(4));

                    g2.dispose();
                }
            };

            btn.setPreferredSize(new Dimension(sc(38), sc(38)));
            btn.setBorder(null);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        }
    }
    private void locGoiYGia(JComboBox<String> cbo, String text) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        String keyword = text.trim();

        for (String s : GOI_Y_GIA) {
            if (keyword.isEmpty() || s.startsWith(keyword)) {
                model.addElement(s);
            }
        }

        cbo.setModel(model);
        cbo.setSelectedItem(text);
    }
}