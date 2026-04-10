package gui;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HoaDon_GUI extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────────────────────
    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(245, 242, 235);
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222);
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175);
    private static final Color CLR_BORDER     = new Color(160, 145, 120);

    private static final Color CLR_BTN_CHITIET = new Color(100, 181, 246); // xanh dương
    private static final Color CLR_BTN_TRACUU  = new Color(102, 187, 106); // xanh lá
    private static final Color CLR_BTN_LAMMOI  = new Color(102, 187, 106); // xanh lá
    private static final Color CLR_BTN_LOC     = new Color(250, 224, 187);  // vàng
    private static final Color CLR_BTN_CAPNHAT = new Color(255, 213, 79);  // vàng

    // ── Trường nhập liệu ─────────────────────────────────────────────────────
    private JTextField    txtTenKhach, txtMaHoaDon, txtTenNhanVien,
                          txtBan, txtTongTien, txtSDT, txtTrangThai;
    private JDateChooser  dtThoiGianVao, dtThoiGianRa;
    private JComboBox<String> txtKhuyenMai;

    // ── Bảng ─────────────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;

    // ── Nút ──────────────────────────────────────────────────────────────────
    private JButton btnChiTiet, btnTraCuu, btnLamMoi, btnLoc, btnCapNhat;

    public HoaDon_GUI() {
        setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);
        add(buildTitlePanel(),  BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    // =========================================================================
    // 1. TIÊU ĐỀ
    // =========================================================================
    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel("DANH SÁCH HÓA ĐƠN", SwingConstants.CENTER);
        lbl.setFont(scaledFontStatic("Times New Roman", Font.BOLD, 26));
        lbl.setForeground(CLR_HEADER_FG);
        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }

    // =========================================================================
    // 2. TRUNG TÂM
    // =========================================================================
    private JPanel buildCenterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topArea = new JPanel(new BorderLayout(0, 4));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(), BorderLayout.CENTER);

        pnl.add(topArea,           BorderLayout.NORTH);
        pnl.add(buildTablePanel(), BorderLayout.CENTER);
        return pnl;
    }

    // =========================================================================
    // 3. FORM NHẬP LIỆU
    // =========================================================================
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 0));
        outer.setBackground(CLR_FORM_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // ── Phần trái: các trường ─────────────────────────────────────────────
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Tên khách / Mã hóa đơn (disabled)
        txtTenKhach  = createTextField();
        txtMaHoaDon  = createTextField();
        txtMaHoaDon.setEnabled(false);
        txtMaHoaDon.setDisabledTextColor(new Color(60, 60, 60));
        addRow(pnlFields, gbc, 0, "Tên khách :", txtTenKhach, "Mã hóa đơn :", txtMaHoaDon);

        // Hàng 1: Tên nhân viên / Mã phiếu ĐB
        txtTenNhanVien = createTextField();
        txtBan= createTextField();
        addRow(pnlFields, gbc, 1, "Tên nhân viên :", txtTenNhanVien, "Bàn :", txtBan);

        // Hàng 2: Tổng tiền / SĐT
        txtTongTien = createTextField();
        txtSDT      = createTextField();
        addRow(pnlFields, gbc, 2, "Tổng tiền :", txtTongTien, "SĐT:", txtSDT);

        // Hàng 3: Thời gian vào (DateChooser) / Thời gian ra (DateChooser)
        dtThoiGianVao = createDateChooser();
        dtThoiGianRa  = createDateChooser();
        addRow(pnlFields, gbc, 3, "Thời gian vào", dtThoiGianVao, "Thời gian ra", dtThoiGianRa);

        // Hàng 4: Trạng thái
//        txtTrangThai = createTextField();
//        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
//        pnlFields.add(createLabel("Trạng thái"), gbc);
//        gbc.gridx = 1; gbc.weightx = 1;
//        pnlFields.add(txtTrangThai, gbc);
        
        txtTrangThai  = createTextField();
        txtKhuyenMai  = new JComboBox<String>(new String[] {"ưu đãi hạng vàng","ưu đãi hạng kim cương","khuyến mãi thường"});
//        txtKhuyenMai.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        txtKhuyenMai.setBackground(Color.WHITE);
        
        
//        txtKhuyenMai.setPreferredSize(new Dimension(100, 28));
        
        addRow(pnlFields, gbc, 4, "Trạng thái", txtTrangThai, "Khuyến mãi", txtKhuyenMai);

        outer.add(pnlFields, BorderLayout.CENTER);

        // ── Phần phải: các nút chức năng ──────────────────────────────────────
        outer.add(buildRightButtons(), BorderLayout.EAST);

        return outer;
    }
    
    private static final double SCALE;

    static {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        AffineTransform at = gc.getDefaultTransform();

        SCALE = at.getScaleX(); // hoặc getScaleY()
    }
    private static Font scaledFontStatic(String name, int style, int size) {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        AffineTransform at = gc.getDefaultTransform();

        double scale = at.getScaleX();

        return new Font(name, style, (int)(size * scale));
    }
    
    private ImageIcon loadIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    private JButton createFuncButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

//        btn.setFont(new Font("Times New Roman", Font.BOLD, 14));
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //  FIX mất chữ
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setIconTextGap(10);
        btn.setMargin(new Insets(2, 8, 2, 8));
//        btn.setPreferredSize(new Dimension(0, 36));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18, 18));
        }

        return btn;
    }

    // ── Nút bên phải form ────────────────────────────────────────────────────
//    private JPanel buildRightButtons() {
//
//        btnChiTiet = createFuncButton("CHI TIẾT HÓA ĐƠN", CLR_BTN_CHITIET, "img/chitiethoadon.png");
//        btnTraCuu  = createFuncButton("TRA CỨU",           CLR_BTN_TRACUU,  "img/mm_tracuu.png");
//        btnLamMoi  = createFuncButton("LÀM MỚI",           CLR_BTN_LAMMOI,  "img/mn_xuly.png");
//        btnLoc     = createFuncButton("LỌC",               CLR_BTN_LOC,     "img/cn_loc.png");
//        btnCapNhat = createFuncButton("CẬP NHẬT",           CLR_BTN_CAPNHAT, null);
//
//        JPanel top = new JPanel(new GridLayout(1, 1));
//        top.setOpaque(false);
//        top.add(btnChiTiet);
//
//        JPanel mid = new JPanel(new GridLayout(1, 2, 6, 0));
//        mid.setOpaque(false);
//        mid.add(btnTraCuu);
//        mid.add(btnLamMoi);
//
//        JPanel bot = new JPanel(new GridLayout(1, 2, 6, 0));
//        bot.setOpaque(false);
//        bot.add(btnLoc);
//        bot.add(btnCapNhat);
//
//        JPanel wrapper = new JPanel(new GridLayout(3, 1, 0, 6));
//        wrapper.setOpaque(false);
//        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
//        wrapper.setPreferredSize(new Dimension(320, 100)); // ← chỉnh width/height ở đây
//        wrapper.add(top);
//        wrapper.add(mid);
//        wrapper.add(bot);
//
//        btnLamMoi.addActionListener(e  -> lamMoi());
//        btnCapNhat.addActionListener(e -> capNhatHoaDon());
//        btnTraCuu.addActionListener(e  -> traCuu());
//
//        return wrapper;
//    }
    
    private JPanel buildRightButtons() {

        btnChiTiet = createFuncButton("CHI TIẾT HÓA ĐƠN", CLR_BTN_CHITIET, "img/chitiethoadon.png");
        btnTraCuu  = createFuncButton("TRA CỨU",           CLR_BTN_TRACUU,  "img/mm_tracuu.png");
        btnLamMoi  = createFuncButton("LÀM MỚI",           CLR_BTN_LAMMOI,  "img/mn_xuly.png");
        btnLoc     = createFuncButton("LỌC",               CLR_BTN_LOC,     "img/cn_loc.png");
        btnCapNhat = createFuncButton("CẬP NHẬT",          CLR_BTN_CAPNHAT, "img/cn_capnhat.png");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        wrapper.setPreferredSize(new Dimension(280, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        // ===== Dòng 1 (nút to)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.2;
        wrapper.add(btnChiTiet, gbc);

        // ===== Dòng 2
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.15;

        gbc.gridx = 0;
        wrapper.add(btnTraCuu, gbc);

        gbc.gridx = 1;
        wrapper.add(btnLamMoi, gbc);

        // ===== Dòng 3
        gbc.gridy = 2;

        gbc.gridx = 0;
        wrapper.add(btnLoc, gbc);

        gbc.gridx = 1;
        wrapper.add(btnCapNhat, gbc);

        // action
        btnLamMoi.addActionListener(e  -> lamMoi());
        btnCapNhat.addActionListener(e -> capNhatHoaDon());
        btnTraCuu.addActionListener(e  -> traCuu());

        return wrapper;
    }

    // =========================================================================
    // 4. BẢNG
    // =========================================================================
    private JScrollPane buildTablePanel() {
        String[] cols = {
            "Mã hóa đơn", "Thời gian tạo","Thời gian ra", "Khách hàng",
            "Nhân viên", "SĐT", "Khuyến mãi", "Bàn", "Tổng tiền", "Trạng thái"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
//        table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(CLR_BORDER);
        table.setSelectionBackground(new Color(180, 210, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Times New Roman", Font.BOLD, 13));
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setReorderingAllowed(false);

        int[] widths = {100, 120, 120, 110, 110, 100, 120, 60, 90, 100};        
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
        });

        // Dữ liệu mẫu
        tableModel.addRow(new Object[]{
            "HD0001", "8:00 03-02-2026","8:01 03-02-2026", "Minh",
            "Ngọc Tiến", "0123456789", "ưu đãi hàng vàng", "B0001", "110.000", "Hoàn thành"
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        return scroll;
    }

    // =========================================================================
    // 5. LOGIC
    // =========================================================================
    private void lamMoi() {
        txtTenKhach.setText("");
        txtMaHoaDon.setText("");
        txtTenNhanVien.setText("");
        txtBan.setText("");
        txtTongTien.setText("");
        txtSDT.setText("");
        txtKhuyenMai.setSelectedIndex(0);
        txtTrangThai.setText("");
        dtThoiGianVao.setDate(null);
        dtThoiGianRa.setDate(null);
        table.clearSelection();
    }

    private void capNhatHoaDon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        tableModel.setValueAt(txtMaHoaDon.getText().trim(),    row, 0);
        tableModel.setValueAt(dtThoiGianVao.getDate() != null ?
                sdf.format(dtThoiGianVao.getDate()) : "",      row, 1);
        tableModel.setValueAt(dtThoiGianRa.getDate() != null ?
                sdf.format(dtThoiGianRa.getDate()) : "",       row, 2);
        tableModel.setValueAt(txtTenKhach.getText().trim(),    row, 3);
        tableModel.setValueAt(txtTenNhanVien.getText().trim(), row, 4);
        tableModel.setValueAt(txtSDT.getText().trim(),         row, 5);
        tableModel.setValueAt(txtKhuyenMai.getSelectedItem(),   row, 6);
        tableModel.setValueAt(txtBan.getText().trim(),   row, 7);
        tableModel.setValueAt(txtTongTien.getText().trim(),    row, 8);
        tableModel.setValueAt(txtTrangThai.getText().trim(),   row, 9);
        lamMoi();
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(this, "Nhập từ khóa:", "Tra cứu", JOptionPane.PLAIN_MESSAGE);
        if (keyword == null || keyword.trim().isEmpty()) return;
        String kw = keyword.trim().toLowerCase();
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                Object val = tableModel.getValueAt(r, c);
                if (val != null && val.toString().toLowerCase().contains(kw)) {
                    table.setRowSelectionInterval(r, r);
                    table.scrollRectToVisible(table.getCellRect(r, 0, true));
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!", "Tra cứu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadRowToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtMaHoaDon.setText(tableModel.getValueAt(row, 0).toString());
        txtTenKhach.setText(tableModel.getValueAt(row, 3).toString());
        txtTenNhanVien.setText(tableModel.getValueAt(row, 4).toString());
        txtSDT.setText(tableModel.getValueAt(row, 5).toString());
        txtKhuyenMai.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        txtBan.setText(tableModel.getValueAt(row, 7).toString());
        txtTongTien.setText(tableModel.getValueAt(row, 8).toString());
        txtTrangThai.setText(tableModel.getValueAt(row, 9).toString());
        // Thời gian vào
        try {
            Date d1 = new SimpleDateFormat("HH:mm dd-MM-yyyy")
                          .parse(tableModel.getValueAt(row, 1).toString());
            dtThoiGianVao.setDate(d1);
        } catch (Exception ignored) {}
        // Thời gian ra
        try {
            Date d2 = new SimpleDateFormat("HH:mm dd-MM-yyyy")
                          .parse(tableModel.getValueAt(row, 2).toString());
            dtThoiGianRa.setDate(d2);
        } catch (Exception ignored) {}
    }

    // =========================================================================
    // 6. HELPER
    // =========================================================================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
//        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
//        tf.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
//        tf.setPreferredSize(new Dimension(160, 30));
        tf.setPreferredSize(null);

        return tf;
    }

    /** JDateChooser – chỉ chọn từ lịch**/
    private JDateChooser createDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("HH:mm dd/MM/yyyy");
//        dc.setPreferredSize(new Dimension(160, 28));
        dc.setPreferredSize(null);
        dc.getDateEditor().getUiComponent().setEnabled(false); // không gõ tay
        ((JTextField) dc.getDateEditor().getUiComponent())
        .setFont(scaledFontStatic("Times New Roman", Font.PLAIN, 13));
        return dc;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String lbl1, JComponent c1, String lbl2, JComponent c2) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; p.add(createLabel(lbl1), gbc);
        gbc.gridx = 1; gbc.weightx = 1;                  p.add(c1, gbc);
        gbc.gridx = 2; gbc.weightx = 0;                  p.add(createLabel(lbl2), gbc);
        gbc.gridx = 3; gbc.weightx = 1;                  p.add(c2, gbc);
    }
    
 

    
//    

    // =========================================================================
    // 7. MAIN – demo (dùng JLayeredPane để menu không đẩy content xuống)
    // =========================================================================
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale", "auto");
    	
//    	chỉnh font
    	UIManager.put("Label.font",   scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("Button.font",  scaledFontStatic("Times New Roman", Font.BOLD, 10));
    	UIManager.put("TextField.font", scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("Table.font",   scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("TableHeader.font", scaledFontStatic("Times New Roman", Font.BOLD, 12));
    	UIManager.put("ComboBox.font", scaledFontStatic("Times New Roman", Font.PLAIN, 13));
    	UIManager.put("ComboBox.listFont", scaledFontStatic("Times New Roman", Font.PLAIN, 13)); // 👈 QUAN TRỌNG    	
    	
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            JFrame frame = new JFrame("Danh Sách Hóa Đơn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            JLayeredPane layeredPane = new JLayeredPane();
            frame.setContentPane(layeredPane);

            HoaDon_GUI   mainPanel = new HoaDon_GUI();
            Pn_ThanhMenu menuPanel = new Pn_ThanhMenu();

            layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(menuPanel, JLayeredPane.PALETTE_LAYER);

            layeredPane.addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    int w = layeredPane.getWidth();
                    int h = layeredPane.getHeight();
                    int menuH = 42;
                    menuPanel.setBounds(0, 0, w, 400);
                    mainPanel.setBounds(0, menuH, w, h - menuH);
                }
            });

            frame.setVisible(true);
        });
    }
}