package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QLBan_GUI extends JPanel {

    // ──────────────── Màu sắc ────────────────
    private static final Color CLR_BG = new Color(245, 245, 245);
    private static final Color CLR_WHITE = Color.WHITE;
    
    // Status colors
    private static final Color CLR_TRONG_BG = new Color(227, 242, 253);  // Blue nhạt
    private static final Color CLR_TRONG_FG = new Color(17, 163, 60);    // Xanh lục
    
    private static final Color CLR_DANGDUNG_BG = new Color(253, 235, 236); // Hồng nhạt
    private static final Color CLR_DANGDUNG_FG = new Color(211, 47, 47);   // Đỏ
    
    private static final Color CLR_DADAT_BG = new Color(255, 243, 224);  // Cam nhạt
    private static final Color CLR_DADAT_FG = new Color(245, 124, 0);    // Cam đậm
    
    private static final Color CLR_BAOTRI_BG = new Color(238, 238, 238); // Xám nhạt
    private static final Color CLR_BAOTRI_FG = new Color(97, 97, 97);    // Xám đậm
    
    private static final Color CLR_SELECTED_BORDER = new Color(124, 77, 255); // Tím
    
    // ──────────────── Dữ liệu ────────────────
    private List<BanInfo> allBans = new ArrayList<>();
    private BanInfo selectedBan = null;
    private List<BanCard> currentCards = new ArrayList<>();

    // ──────────────── UI Components ────────────────
    private JPanel pnDanhSachBan;
    private JLabel lblStatTrong, lblStatDangDung, lblStatDaDat, lblStatBaoTri;
    
    private JLabel lblHeaderInfoTen, lblHeaderInfoTrangThai;
    private JTextField txtMaBan, txtTenBan, txtKhuVuc, txtSucChua, txtKiHieu, txtLoaiBan;

    public QLBan_GUI() {
        setLayout(new BorderLayout(16, 16));
        setBackground(CLR_BG);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        
        initSampleData();
        
        JPanel pnLeft = buildLeftPanel();
        JPanel pnRight = buildRightPanel();
        
        pnRight.setPreferredSize(new Dimension(320, 0));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnLeft, pnRight);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(16);
        splitPane.setResizeWeight(1.0); // cho left chiếm hết khi resize
        splitPane.setContinuousLayout(true);


        add(splitPane, BorderLayout.CENTER);
        
        renderDanhSachBan();
        updateStats();
        if(!allBans.isEmpty()) {
            selectBan(allBans.get(0));
        }
    }
    
    // ──────────────── TẠO PANEL TRÁI (QUẢN LÝ BÀN) ────────────────
    private JPanel buildLeftPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 16));
        pnl.setOpaque(false);
        
        // --- Top Bar ---
        JPanel pnTop = new JPanel(new BorderLayout());
        pnTop.setOpaque(false);
        
        // Chips
        JPanel pnChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnChips.setOpaque(false);
        ButtonGroup grpChips = new ButtonGroup();
        String[] filters = {"Tất cả", "Tầng 1 (A)", "Tầng 2 (B)", "Sân thượng (C)"};
        for(int i=0; i<filters.length; i++) {
            JToggleButton chip = createChip(filters[i], i==0);
            grpChips.add(chip);
            pnChips.add(chip);
        }
        
        // Actions
        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnActions.setOpaque(false);
        
        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(140, 32));
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm mã bàn...");
        
        pnActions.add(txtSearch);
        pnActions.add(createActionButton("Lọc", new Color(235, 230, 220), new Color(60, 60, 60)));
        pnActions.add(createActionButton("Thêm bàn", new Color(227, 242, 253), new Color(25, 118, 210)));
        pnActions.add(createActionButton("Sửa bàn", new Color(232, 245, 233), new Color(46, 125, 50)));
        JButton btnGhep = createActionButton("Ghép bàn", new Color(243, 235, 216), new Color(60, 60, 60));
        btnGhep.addActionListener(e -> showGhepBanDialog());
        pnActions.add(btnGhep);
        pnActions.add(createActionButton("Xóa bàn", new Color(255, 235, 238), new Color(198, 40, 40)));
        
        pnTop.add(pnChips, BorderLayout.WEST);
        pnTop.add(pnActions, BorderLayout.EAST);
        
        // --- Center Scroll ---
        // ScrollablePanel to force children to expand width
        pnDanhSachBan = new ScrollablePanel();
        pnDanhSachBan.setLayout(new BoxLayout(pnDanhSachBan, BoxLayout.Y_AXIS));
        pnDanhSachBan.setOpaque(true);
        pnDanhSachBan.setBackground(Color.WHITE);
        pnDanhSachBan.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        // Create an outer rounded panel to hold scroll
        JPanel pnScrollBg = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pnScrollBg.setOpaque(false);
        pnScrollBg.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
        JScrollPane scroll = new JScrollPane(pnDanhSachBan);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        pnScrollBg.add(scroll, BorderLayout.CENTER);
        
        scroll.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                pnDanhSachBan.revalidate();
            }
        });
        
        pnl.add(pnTop, BorderLayout.NORTH);
        pnl.add(pnScrollBg, BorderLayout.CENTER);
        
        return pnl;
    }
    
    // ──────────────── TẠO PANEL PHẢI (THỐNG KÊ & TT) ────────────────
    private JPanel buildRightPanel() {
        JPanel pnRight = new JPanel(new BorderLayout(0, 16));
        pnRight.setOpaque(false);

        // 1. Panel Thống Kê
        JPanel pnStatBg = new JPanel();
        pnStatBg.setLayout(new BoxLayout(pnStatBg, BoxLayout.Y_AXIS));
        pnStatBg.setOpaque(false);
        pnStatBg.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        JPanel pnStatWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        pnStatWrap.setOpaque(false);
        
        JLabel lblStatTitle = new JLabel("Thống kê trạng thái");
        lblStatTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        
        lblStatTrong = new JLabel("0");
        lblStatDangDung = new JLabel("0");
        lblStatDaDat = new JLabel("0");
        lblStatBaoTri = new JLabel("0");
        
        pnStatBg.add(lblStatTitle);
        pnStatBg.add(Box.createVerticalStrut(16));
        pnStatBg.add(createStatCard("Trống", lblStatTrong, CLR_TRONG_BG, CLR_TRONG_FG, new Color(33, 150, 243)));
        pnStatBg.add(Box.createVerticalStrut(10));
        pnStatBg.add(createStatCard("Đang dùng", lblStatDangDung, CLR_DANGDUNG_BG, CLR_DANGDUNG_FG, null));
        pnStatBg.add(Box.createVerticalStrut(10));
        pnStatBg.add(createStatCard("Đã đặt", lblStatDaDat, CLR_DADAT_BG, CLR_DADAT_FG, null));
        pnStatBg.add(Box.createVerticalStrut(10));
        pnStatBg.add(createStatCard("Bảo trì", lblStatBaoTri, CLR_BAOTRI_BG, CLR_BAOTRI_FG, null));
        
        pnStatWrap.add(pnStatBg, BorderLayout.CENTER);
        
        // 2. Panel Thông tin bàn
        JPanel pnInfoWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        pnInfoWrap.setOpaque(false);
        
        JPanel pnInfoBg = new JPanel(new BorderLayout(0, 16));
        pnInfoBg.setOpaque(false);
        pnInfoBg.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        JLabel lblInfoTitle = new JLabel("Thông tin bàn");
        lblInfoTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblInfoTitle.setForeground(new Color(30, 40, 50));
        
        // Header Info Card (Bàn A01 - Trống)
        JPanel pnHdrCardWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(224, 238, 249)); // mầu nền xanh nhạt
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(33, 150, 243)); // viền xanh đậm
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        pnHdrCardWrap.setOpaque(false);
        pnHdrCardWrap.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        
        JPanel pnHdrInner = new JPanel();
        pnHdrInner.setLayout(new BoxLayout(pnHdrInner, BoxLayout.Y_AXIS));
        pnHdrInner.setOpaque(false);
        
        lblHeaderInfoTen = new JLabel("Bàn A01");
        lblHeaderInfoTen.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeaderInfoTen.setForeground(new Color(40, 45, 55));
        
        lblHeaderInfoTrangThai = new JLabel("Trạng thái: Trống");
        lblHeaderInfoTrangThai.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        pnHdrInner.add(lblHeaderInfoTen);
        pnHdrInner.add(Box.createVerticalStrut(6));
        pnHdrInner.add(lblHeaderInfoTrangThai);
        pnHdrCardWrap.add(pnHdrInner, BorderLayout.CENTER);
        
        // Form Info Grid
        JPanel pnFields = new JPanel(new GridLayout(3, 2, 16, 16));
        pnFields.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnFields.setOpaque(false);
        
        txtMaBan = createField("Mã bàn");
        txtTenBan = createField("Tên bàn");
        txtKhuVuc = createField("Khu vực");
        txtSucChua = createField("Sức chứa");
        txtKiHieu = createField("Kí hiệu khu vực");
        txtLoaiBan = createField("Loại bàn");
        
        JPanel c1 = createFieldWrapper("Mã bàn", txtMaBan);
        JPanel c2 = createFieldWrapper("Tên bàn", txtTenBan);
        JPanel c3 = createFieldWrapper("Khu vực", txtKhuVuc);
        JPanel c4 = createFieldWrapper("Sức chứa", txtSucChua);
        JPanel c5 = createFieldWrapper("Kí hiệu khu vực", txtKiHieu);
        JPanel c6 = createFieldWrapper("Loại bàn", txtLoaiBan);
        
        pnFields.add(c1); pnFields.add(c2);
        pnFields.add(c3); pnFields.add(c4);
        pnFields.add(c5); pnFields.add(c6);
        
        JPanel pnFieldsWrapper = new JPanel(new BorderLayout());
        pnFieldsWrapper.setOpaque(false);
        pnFieldsWrapper.add(pnFields, BorderLayout.NORTH);
        
        JPanel pnInfoContent = new JPanel(new BorderLayout(0, 16));
        pnInfoContent.setOpaque(false);
        pnInfoContent.add(pnHdrCardWrap, BorderLayout.NORTH);
        pnInfoContent.add(pnFieldsWrapper, BorderLayout.CENTER);
        
        pnInfoBg.add(lblInfoTitle, BorderLayout.NORTH);
        pnInfoBg.add(pnInfoContent, BorderLayout.CENTER);
        
        pnInfoWrap.add(pnInfoBg, BorderLayout.CENTER);
        
        // Add to right panel
        pnRight.add(pnStatWrap, BorderLayout.NORTH);
        pnRight.add(pnInfoWrap, BorderLayout.CENTER);

        return pnRight;
    }

    // ──────────────── RENDER & LOGIC ────────────────
    private void renderDanhSachBan() {
        pnDanhSachBan.removeAll();
        currentCards.clear();
        
        String[] khuVucs = {"Tầng 1", "Tầng 2", "Sân thượng"};
        String[] mieuTas = {"Khu A", "Khu B", "Khu C"};
        String[] maGoc   = {"A01 - A15", "B01 - B20", "C01 - C20"};
        
        for (int i = 0; i < khuVucs.length; i++) {
            String kv = khuVucs[i];
            List<BanInfo> bansHere = allBans.stream().filter(b -> b.khuVuc.equals(kv)).collect(Collectors.toList());
            if(bansHere.isEmpty()) continue;
            
            // Header khu vực
            JPanel pnHeader = new JPanel(new BorderLayout());
            pnHeader.setOpaque(false);
            pnHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            JPanel pnTitle = new JPanel();
            pnTitle.setLayout(new BoxLayout(pnTitle, BoxLayout.Y_AXIS));
            pnTitle.setOpaque(false);
            
            JLabel lblTitle = new JLabel(kv + " (" + mieuTas[i] + ")");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
            lblTitle.setForeground(new Color(40, 40, 40));
            
            JLabel lblSub = new JLabel("Mã bàn: " + maGoc[i]);
            lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblSub.setForeground(new Color(130, 130, 130));
            
            pnTitle.add(lblTitle);
            pnTitle.add(lblSub);
            pnHeader.add(pnTitle, BorderLayout.WEST);
            
            // Badge Số bàn
            JPanel pnlBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(240, 243, 255)); // Light bluish-purple
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.dispose();
                }
            };
            pnlBadge.setOpaque(false);
            pnlBadge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
            JLabel lblBadge = new JLabel(bansHere.size() + " bàn");
            lblBadge.setForeground(new Color(76, 56, 219)); // Dark purple
            lblBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
            pnlBadge.add(lblBadge);
            
            JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightFlow.setOpaque(false);
            rightFlow.add(pnlBadge);
            pnHeader.add(rightFlow, BorderLayout.EAST);
            
            // Danh sách cards
            JPanel pnCards = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
            pnCards.setOpaque(false);
            for(BanInfo ban : bansHere) {
                BanCard card = new BanCard(ban);
                currentCards.add(card);
                card.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        selectBan(ban);
                    }
                });
                pnCards.add(card);
            }
            
            JPanel pnGroup = new JPanel(new BorderLayout());
            pnGroup.setOpaque(false);
            pnGroup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 24, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                )
            ));
            
            JPanel pnGroupRounded = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.setColor(new Color(236, 236, 236));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            };
            pnGroupRounded.setOpaque(false);
            pnGroupRounded.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
            pnGroupRounded.add(pnHeader, BorderLayout.NORTH);
            pnGroupRounded.add(pnCards, BorderLayout.CENTER);
            
            JPanel pnWrapper = new JPanel(new BorderLayout());
            pnWrapper.setOpaque(false);
            pnWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
            pnWrapper.add(pnGroupRounded, BorderLayout.CENTER);
            
            pnDanhSachBan.add(pnWrapper);
        }
        
        pnDanhSachBan.revalidate();
        pnDanhSachBan.repaint();
    }
    
    private void selectBan(BanInfo ban) {
        this.selectedBan = ban;
        for(BanCard card : currentCards) {
            card.isSelected = (card.ban == ban);
            card.repaint();
        }
        
        if (ban != null) {
            lblHeaderInfoTen.setText(ban.tenBan);
            lblHeaderInfoTrangThai.setText("Trạng thái: " + ban.trangThai);
            
            switch (ban.trangThai) {
                case "Trống": lblHeaderInfoTrangThai.setForeground(CLR_TRONG_FG); break;
                case "Đang dùng": lblHeaderInfoTrangThai.setForeground(CLR_DANGDUNG_FG); break;
                case "Đã đặt": lblHeaderInfoTrangThai.setForeground(CLR_DADAT_FG); break;
                case "Bảo trì": lblHeaderInfoTrangThai.setForeground(CLR_BAOTRI_FG); break;
                default: lblHeaderInfoTrangThai.setForeground(Color.BLACK); break;
            }
            
            txtMaBan.setText(ban.maBan);
            txtTenBan.setText(ban.tenBan);
            txtKhuVuc.setText(ban.khuVuc);
            txtSucChua.setText(ban.sucChua + " người");
            txtKiHieu.setText(ban.kiHieuKV);
            txtLoaiBan.setText(ban.loaiBan);
        }
    }
    
    private void updateStats() {
        int trong = 0, dung = 0, dat = 0, bao = 0;
        for (BanInfo b : allBans) {
            switch(b.trangThai) {
                case "Trống": trong++; break;
                case "Đang dùng": dung++; break;
                case "Đã đặt": dat++; break;
                case "Bảo trì": bao++; break;
            }
        }
        lblStatTrong.setText(String.valueOf(trong));
        lblStatDangDung.setText(String.valueOf(dung));
        lblStatDaDat.setText(String.valueOf(dat));
        lblStatBaoTri.setText(String.valueOf(bao));
    }
    
    private void showGhepBanDialog() {
        if (selectedBan == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn hiện tại để ghép!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ghép bàn", true);
        dlg.setSize(580, 480);
        dlg.setLocationRelativeTo(this);
        
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0); 
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("Phiếu yêu cầu ghép bàn");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 16, 0);
        pnl.add(lblTitle, gbc);
        
        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(8, 0, 8, 10);
        
        fc.gridx = 0; fc.gridy = 0; fc.weightx = 0;
        JLabel lblBanHT = new JLabel("Bàn hiện tại");
        lblBanHT.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(lblBanHT, fc);
        
        fc.gridx = 1; fc.weightx = 0.5;
        JTextField txtBanHienTai = createStyledTextField();
        txtBanHienTai.setText(selectedBan.maBan);
        txtBanHienTai.setEditable(false);
        txtBanHienTai.setBackground(Color.WHITE);
        form.add(txtBanHienTai, fc);
        
        fc.gridx = 2; fc.weightx = 0;
        JLabel lblBanGhep = new JLabel("Bàn ghép thêm");
        lblBanGhep.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(lblBanGhep, fc);
        
        fc.gridx = 3; fc.weightx = 0.5; fc.insets = new Insets(8, 0, 8, 0);
        JTextField txtBanGhep = createStyledTextField();
        form.add(txtBanGhep, fc);
        
        fc.gridy++;
        fc.gridx = 0; fc.weightx = 0; fc.gridwidth = 1; fc.insets = new Insets(8, 0, 8, 10);
        JLabel lblSL = new JLabel("Số lượng khách sau ghép");
        lblSL.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(lblSL, fc);
        
        fc.gridx = 1; fc.weightx = 1.0; fc.gridwidth = 3; fc.insets = new Insets(8, 0, 8, 0);
        JTextField txtSL = createStyledTextField();
        txtSL.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)
        ));
        form.add(txtSL, fc);
        
        fc.gridy++;
        fc.gridx = 0; fc.weightx = 0; fc.gridwidth = 1; fc.insets = new Insets(8, 0, 8, 10);
        JLabel lblNV = new JLabel("Nhân viên thực hiện");
        lblNV.setFont(new Font("SansSerif", Font.PLAIN, 14));
        form.add(lblNV, fc);
        
        fc.gridx = 1; fc.weightx = 1.0; fc.gridwidth = 3; fc.insets = new Insets(8, 0, 8, 0);
        JTextField txtNV = createStyledTextField();
        txtNV.setText("HoangAnh");
        txtNV.setEditable(false);
        txtNV.setBackground(Color.WHITE);
        form.add(txtNV, fc);
        
        gbc.gridy = 1; gbc.gridwidth = 2;
        pnl.add(form, gbc);
        
        JLabel lblLyDo = new JLabel("Lý do ghép bàn");
        lblLyDo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 2; gbc.insets = new Insets(4, 0, 4, 0);
        pnl.add(lblLyDo, gbc);
        
        JTextArea txtLyDo = new JTextArea("- Khách đi theo nhóm đông hơn số chỗ của bàn hiện tại.\n- Cần ghép bàn để khách ngồi gần nhau và dễ phục vụ.");
        txtLyDo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtLyDo.setForeground(new Color(80, 80, 80));
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 12, 0);
        pnl.add(txtLyDo, gbc);
        
        JPanel row5 = new JPanel(new BorderLayout(10, 0));
        row5.setOpaque(false);
        JLabel lblGhiChu = new JLabel("Ghi chú bổ sung");
        lblGhiChu.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblGhiChu.setPreferredSize(new Dimension(165, 30));
        row5.add(lblGhiChu, BorderLayout.WEST);
        JTextField txtGhiChu = createStyledTextField();
        txtGhiChu.putClientProperty("JTextField.placeholderText", "Nhập ghi chú thêm nếu có...");
        row5.add(txtGhiChu, BorderLayout.CENTER);
        
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 16, 0);
        pnl.add(row5, gbc);
        
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);
        
        JButton btnHuy = new JButton("Hủy") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnHuy.setContentAreaFilled(false);
        btnHuy.setBorderPainted(false);
        btnHuy.setFocusPainted(false);
        btnHuy.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnHuy.setPreferredSize(new Dimension(80, 36));
        btnHuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> dlg.dispose());
        
        JButton btnXacNhan = new JButton("Xác nhận ghép bàn") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(243, 235, 216));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnXacNhan.setContentAreaFilled(false);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXacNhan.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnXacNhan.setPreferredSize(new Dimension(150, 36));

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnXacNhan);
        
        gbc.gridy = 5;
        pnl.add(pnlBtns, gbc);

        gbc.gridy = 6; gbc.weighty = 1.0;
        pnl.add(Box.createGlue(), gbc);
        
        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }
    
    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return txt;
    }
    
    // ──────────────── UTILS & COMPONENTS ────────────────
    private JTextField createField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("SansSerif", Font.BOLD, 13));
        txt.setForeground(new Color(40, 45, 55));
        txt.setEditable(false);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return txt;
    }
    
    private JPanel createFieldWrapper(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(125, 130, 135));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
    
    private JPanel createStatCard(String title, JLabel lblVal, Color bg, Color fg, Color border) {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if(border != null) {
                    g2.setColor(border);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(800, 34));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        p.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitle.setForeground(fg);
        
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblVal.setForeground(fg);
        
        p.add(lblTitle, BorderLayout.WEST);
        p.add(lblVal, BorderLayout.EAST);
        return p;
    }
    
    private JToggleButton createChip(String text, boolean active) {
        JToggleButton btn = new JToggleButton(text, active) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) g2.setColor(new Color(45, 55, 72));
                else g2.setColor(new Color(240, 240, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(active ? Color.WHITE : new Color(80, 80, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 14, 4, 14));
        btn.addItemListener(e -> {
            btn.setForeground(btn.isSelected() ? Color.WHITE : new Color(80, 80, 80));
        });
        return btn;
    }
    
    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        int btnWidth = 90;
        if(text.equals("Lọc")) btnWidth = 60;
        else if(text.equals("Ghép bàn")) btnWidth = 95;
        
        btn.setPreferredSize(new Dimension(btnWidth, 30));
        return btn;
    }
    
    // ──────────────── LỚP DỮ LIỆU & RENDER THẺ ────────────────
    class BanInfo {
        String maBan, tenBan, khuVuc, kiHieuKV, loaiBan, trangThai;
        int sucChua;
        public BanInfo(String m, String t, String k, int s, String kh, String l, String tr) {
            this.maBan=m; this.tenBan=t; this.khuVuc=k; this.sucChua=s; 
            this.kiHieuKV=kh; this.loaiBan=l; this.trangThai=tr;
        }
    }
    
    class BanCard extends JPanel {
        BanInfo ban;
        boolean isSelected;
        public BanCard(BanInfo ban) {
            this.ban = ban;
            setPreferredSize(new Dimension(100, 84));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            JLabel lblTen = new JLabel(ban.tenBan);
            lblTen.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblTen.setForeground(new Color(40, 40, 40));
            
            JLabel lblInfo = new JLabel("<html>"+ban.sucChua+" chỗ • " + ban.khuVuc + "</html>");
            lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lblInfo.setForeground(new Color(130, 130, 130));
            
            JLabel lblStatus = new JLabel(ban.trangThai);
            lblStatus.setFont(new Font("SansSerif", Font.BOLD, 11));
            switch (ban.trangThai) {
                case "Trống": lblStatus.setForeground(CLR_TRONG_FG); break;
                case "Đang dùng": lblStatus.setForeground(CLR_DANGDUNG_FG); break;
                case "Đã đặt": lblStatus.setForeground(CLR_DADAT_FG); break;
                case "Bảo trì": lblStatus.setForeground(CLR_BAOTRI_FG); break;
            }
            
            lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            add(lblTen);
            add(Box.createVerticalStrut(4));
            add(lblInfo);
            add(Box.createVerticalStrut(6));
            add(lblStatus);
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color bg = CLR_TRONG_BG;
            switch(ban.trangThai) {
                case "Đang dùng": bg = CLR_DANGDUNG_BG; break;
                case "Đã đặt": bg = CLR_DADAT_BG; break;
                case "Bảo trì": bg = CLR_BAOTRI_BG; break;
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            
            if (isSelected) {
                g2.setColor(CLR_SELECTED_BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // ──────────────── DỮ LIỆU MẪU ────────────────
    private void initSampleData() {
        allBans.add(new BanInfo("A01", "Bàn A01", "Tầng 1", 4, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A02", "Bàn A02", "Tầng 1", 4, "A", "Bàn thường", "Đã đặt"));
        allBans.add(new BanInfo("A03", "Bàn A03", "Tầng 1", 6, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A04", "Bàn A04", "Tầng 1", 2, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A05", "Bàn A05", "Tầng 1", 8, "A", "Bàn VIP", "Bảo trì"));
        allBans.add(new BanInfo("A06", "Bàn A06", "Tầng 1", 6, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A07", "Bàn A07", "Tầng 1", 8, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A08", "Bàn A08", "Tầng 1", 2, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A09", "Bàn A09", "Tầng 1", 4, "A", "Bàn thường", "Đang dùng"));
        allBans.add(new BanInfo("A10", "Bàn A10", "Tầng 1", 4, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A11", "Bàn A11", "Tầng 1", 6, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A12", "Bàn A12", "Tầng 1", 2, "A", "Bàn thường", "Đã đặt"));
        allBans.add(new BanInfo("A13", "Bàn A13", "Tầng 1", 8, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A14", "Bàn A14", "Tầng 1", 8, "A", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("A15", "Bàn A15", "Tầng 1", 8, "A", "Bàn VIP", "Trống"));

        allBans.add(new BanInfo("B01", "Bàn B01", "Tầng 2", 4, "B", "Bàn thường", "Đang dùng"));
        allBans.add(new BanInfo("B02", "Bàn B02", "Tầng 2", 4, "B", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("B03", "Bàn B03", "Tầng 2", 6, "B", "Bàn VIP", "Trống"));
        allBans.add(new BanInfo("B04", "Bàn B04", "Tầng 2", 2, "B", "Bàn thường", "Đã đặt"));
        allBans.add(new BanInfo("B05", "Bàn B05", "Tầng 2", 8, "B", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("B06", "Bàn B06", "Tầng 2", 4, "B", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("B07", "Bàn B07", "Tầng 2", 4, "B", "Bàn thường", "Đang dùng"));
        allBans.add(new BanInfo("B08", "Bàn B08", "Tầng 2", 6, "B", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("B09", "Bàn B09", "Tầng 2", 2, "B", "Bàn thường", "Trống"));
        allBans.add(new BanInfo("B10", "Bàn B10", "Tầng 2", 8, "B", "Bàn VIP", "Bảo trì"));

        allBans.add(new BanInfo("C01", "Bàn C01", "Sân thượng", 4, "C", "Bàn ngoài trời", "Trống"));
        allBans.add(new BanInfo("C02", "Bàn C02", "Sân thượng", 4, "C", "Bàn ngoài trời", "Trống"));
        allBans.add(new BanInfo("C03", "Bàn C03", "Sân thượng", 6, "C", "Bàn VIP", "Đang dùng"));
        allBans.add(new BanInfo("C04", "Bàn C04", "Sân thượng", 2, "C", "Bàn ngoài trời", "Trống"));
        allBans.add(new BanInfo("C05", "Bàn C05", "Sân thượng", 8, "C", "Bàn tiệc", "Trống"));
        allBans.add(new BanInfo("C06", "Bàn C06", "Sân thượng", 4, "C", "Bàn ngoài trời", "Đã đặt"));
        allBans.add(new BanInfo("C07", "Bàn C07", "Sân thượng", 4, "C", "Bàn ngoài trời", "Trống"));
        allBans.add(new BanInfo("C08", "Bàn C08", "Sân thượng", 6, "C", "Bàn tiệc", "Trống"));
        allBans.add(new BanInfo("C09", "Bàn C09", "Sân thượng", 2, "C", "Bàn ngoài trời", "Trống"));
        allBans.add(new BanInfo("C10", "Bàn C10", "Sân thượng", 8, "C", "Bàn tiệc", "Trống"));
    }
}

// ──────────────── LỚP HỖ TRỢ WRAP BỐ CỤC ────────────────
class WrapLayout extends FlowLayout {
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
    @Override public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }
    @Override public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
            int hgap = getHgap(), vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;
            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0, rowHeight = 0;
            for (int i = 0; i < target.getComponentCount(); i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth) {
                        dim.width = Math.max(dim.width, rowWidth);
                        dim.height += rowHeight + vgap;
                        rowWidth = 0; rowHeight = 0;
                    }
                    if (rowWidth != 0) rowWidth += hgap;
                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
            dim.width = Math.max(dim.width, rowWidth);
            dim.height += rowHeight + vgap;
            dim.width += insets.left + insets.right + hgap * 2;
            dim.height += insets.top + insets.bottom + vgap * 2;
            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.isValid()) dim.width -= (hgap + 1);
            return dim;
        }
    }
}

class ScrollablePanel extends JPanel implements Scrollable {
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
    @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 64; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
