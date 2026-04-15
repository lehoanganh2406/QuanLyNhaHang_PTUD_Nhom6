package gui;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.HoaDon_DAO;
import digLog.ChiTietHoaDon_DigLog;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HoaDon_GUI extends JFrame {

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
    
    private HoaDon_DAO hd_dao= new HoaDon_DAO();

    // ── Nút ──────────────────────────────────────────────────────────────────
    private JButton btnChiTiet, btnTraCuu, btnLamMoi, btnLoc, btnCapNhat;
	private JComboBox<String> cbTrangThai;
	private Connection con;
	private JComboBox<String> cbNhanVien;
    private static TaiKhoan taiKhoanDangNhap;
    public HoaDon_GUI(TaiKhoan tk) {
    	
    	System.setProperty("sun.java2d.uiScale", "auto");

        String fontName = "SansSerif";

        UIManager.put("Label.font",   scaledFontStatic(fontName, Font.PLAIN, 13));
        UIManager.put("Button.font",  scaledFontStatic(fontName, Font.BOLD, 13));
        UIManager.put("TextField.font", scaledFontStatic(fontName, Font.PLAIN, 13));
        UIManager.put("Table.font",   scaledFontStatic(fontName, Font.PLAIN, 13));
        UIManager.put("TableHeader.font", scaledFontStatic(fontName, Font.BOLD, 13));
        UIManager.put("ComboBox.font", scaledFontStatic(fontName, Font.PLAIN, 13));
        UIManager.put("ComboBox.listFont", scaledFontStatic(fontName, Font.PLAIN, 13));

        // 🔥 FIX 2: LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        setTitle("Danh Sách Hóa Đơn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        // ===== MENU
        Pn_ThanhMenu menu = new Pn_ThanhMenu(tk);

        // ===== PANEL CHÍNH (GIỮ NGUYÊN CODE CŨ)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CLR_PANEL_BG);
        mainPanel.add(buildTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(buildCenterPanel(), BorderLayout.CENTER);

        // ===== ADD LAYER
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(menu, JLayeredPane.PALETTE_LAYER);

        // ===== RESIZE
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();

                mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
                menu.setBounds(0, 0, w, h);

                layeredPane.revalidate();
                layeredPane.repaint();
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);

        // ===== INIT SIZE BAN ĐẦU
        SwingUtilities.invokeLater(() -> {
            int w = getContentPane().getWidth();
            int h = getContentPane().getHeight();

            mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
            menu.setBounds(0, 0, w, h);
        });

        // ===== DB
        con = ConnectDB.getConnection();
        loadData();
    }

    // =========================================================================
    // 1. TIÊU ĐỀ
    // =========================================================================
    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel("DANH SÁCH HÓA ĐƠN", SwingConstants.CENTER);
        lbl.setFont(scaledFontStatic("SansSerif", Font.BOLD, 26));
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
        gbc.insets = new Insets(8, 8, 8, 8); // 🔥 tăng đều khoảng cách
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Tên khách / Mã hóa đơn (disabled)
        txtTenKhach  = createTextField();
        txtMaHoaDon  = createTextField();
        txtMaHoaDon.setEnabled(false);
        txtMaHoaDon.setDisabledTextColor(new Color(60, 60, 60));
        addRow(pnlFields, gbc, 0, "Tên khách :", txtTenKhach, "Mã hóa đơn :", txtMaHoaDon);

        // Hàng 1: Tên nhân viên / Mã phiếu ĐB
//        txtTenNhanVien = createTextField();
//        cbNhanVien= new JComboBox<>();
        cbNhanVien= createComboBox(new String[] {});
        styleComboBox(cbNhanVien);
        loadNhanVienToCombo();
        txtBan= createTextField();
        addRow(pnlFields, gbc, 1, "Tên nhân viên :", cbNhanVien, "Bàn :", txtBan);

        // Hàng 2: Tổng tiền / SĐT
        txtTongTien = createTextField();
        txtSDT      = createTextField();
        addRow(pnlFields, gbc, 2, "Tổng tiền :", txtTongTien, "SĐT:", txtSDT);

        // Hàng 3: Thời gian vào (DateChooser) / Thời gian ra (DateChooser)
        dtThoiGianVao = createDateChooser();
        dtThoiGianRa  = createDateChooser();
        addRow(pnlFields, gbc, 3, "Thời gian vào", dtThoiGianVao, "Thời gian ra", dtThoiGianRa);

        // Hàng 4: Trạng thái

        cbTrangThai= createComboBox(new String[] {
        		"Đã thanh toán","Hủy","Chưa thanh toán"
        });
        styleComboBox(cbTrangThai);
        txtKhuyenMai = createComboBox(new String[]{});
        loadKhuyenMaiToCombo();
        
        styleComboBox(txtKhuyenMai);
        txtKhuyenMai.setBackground(Color.WHITE);
        
        
        
        addRow(pnlFields, gbc, 4, "Trạng thái", cbTrangThai, "Khuyến mãi", txtKhuyenMai);

        outer.add(pnlFields, BorderLayout.CENTER);

        // ── Phần phải: các nút chức năng ──────────────────────────────────────
        outer.add(buildRightButtons(), BorderLayout.EAST);

        return outer;
    }
    
    private void loadNhanVienToCombo() {
        cbNhanVien.removeAllItems();

        for (String ten : hd_dao.getAllTenNhanVien()) {
            cbNhanVien.addItem(ten);
        }
    }
    
    private void loadKhuyenMaiToCombo() {
        txtKhuyenMai.removeAllItems();

        for (String ten : hd_dao.getAllTenKhuyenMai()) {
            txtKhuyenMai.addItem(ten);
        }
    }
    
    private static final double SCALE;

    static {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        AffineTransform at = gc.getDefaultTransform();

        SCALE = at.getScaleX(); 
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

        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setIconTextGap(10);
        btn.setMargin(new Insets(2, 8, 2, 8));

        btn.setPreferredSize(new Dimension(
        	    (int)(110 * SCALE),
        	    (int)(32 * SCALE)
        	));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18, 18));
        }

        return btn;
    }

    // ── Nút bên phải form ────────────────────────────────────────────────────

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
        btnLoc.addActionListener(e     -> locHoaDon());
        btnChiTiet.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn hóa đơn!");
                return;
            }

            String maHD = table.getValueAt(row, 0).toString();

            new ChiTietHoaDon_DigLog(this, maHD).setVisible(true);
        });

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

        table.setRowHeight((int)(32 * SCALE));
        table.setShowGrid(true);
        table.setGridColor(CLR_BORDER);
        table.setSelectionBackground(new Color(180, 210, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();

        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setReorderingAllowed(false);

        int[] widths = {100, 120, 120, 110, 140, 100, 120, 60, 90, 100};        
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
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
//        txtTenNhanVien.setText("");
        cbNhanVien.setSelectedIndex(0);
        txtBan.setText("");
        txtTongTien.setText("");
        txtSDT.setText("");
        txtKhuyenMai.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        dtThoiGianVao.setDate(null);
        dtThoiGianRa.setDate(null);
        table.clearSelection();
    }

    private void capNhatHoaDon() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần cập nhật!");
            return;
        }

        String maHD = txtMaHoaDon.getText().trim();
        String tenNV = cbNhanVien.getSelectedItem().toString();
        String tenKM = txtKhuyenMai.getSelectedItem().toString();
        String trangThai = cbTrangThai.getSelectedItem().toString();

        Timestamp thoiGianRa = null;
        Date d = dtThoiGianRa.getDate();
        if (d != null) {
            thoiGianRa = new Timestamp(d.getTime());
        }

        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!");
            return;
        }

        boolean kq = hd_dao.updateHoaDon(
                maHD,
                tenNV,
                tenKM,
                trangThai,
                thoiGianRa
        );

        if (kq) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
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
    
    
    private void locHoaDon() {

        String trangThai = cbTrangThai.getSelectedItem().toString();

        tableModel.setRowCount(0); // xóa bảng

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        java.util.List<Object[]> ds = hd_dao.getHoaDonByTrangThai(trangThai);


        if (ds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có hóa đơn nào với trạng thái: " + trangThai,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        for (Object[] row : ds) {

            Timestamp vao = (Timestamp) row[1];
            Timestamp ra  = (Timestamp) row[2];

            tableModel.addRow(new Object[]{
                    row[0],
                    vao != null ? sdf.format(vao) : "",
                    ra  != null ? sdf.format(ra)  : "",
                    row[3],
                    row[4],
                    row[5],
                    row[6],
                    row[7],
                    row[8],
                    row[9]
            });
        }
    }
    
    private String getSafe(Object val) {
        return val == null ? "" : val.toString();
    }

    private void loadRowToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
   
        txtMaHoaDon.setText(getSafe(tableModel.getValueAt(row, 0)));
        txtTenKhach.setText(getSafe(tableModel.getValueAt(row, 3)));
        cbNhanVien.setSelectedItem(getSafe(tableModel.getValueAt(row, 4)));
        txtSDT.setText(getSafe(tableModel.getValueAt(row, 5)));
        txtBan.setText(getSafe(tableModel.getValueAt(row, 7)));
        txtTongTien.setText(getSafe(tableModel.getValueAt(row, 8)));
        
        txtKhuyenMai.setSelectedItem(getSafe(tableModel.getValueAt(row, 6)));
        
        cbTrangThai.setSelectedItem(getSafe(tableModel.getValueAt(row, 9)));
        // Thời gian vào
        try {
            String vao = getSafe(tableModel.getValueAt(row, 1));
            if (!vao.isEmpty()) {
                dtThoiGianVao.setDate(new SimpleDateFormat("HH:mm dd-MM-yyyy").parse(vao));
            }
        } catch (Exception e) {}

        try {
            String ra = getSafe(tableModel.getValueAt(row, 2));
            if (!ra.isEmpty()) {
                dtThoiGianRa.setDate(new SimpleDateFormat("HH:mm dd-MM-yyyy").parse(ra));
            }
        } catch (Exception e) {}
        disableFormFields();
    }
    
    private void disableFormFields() {

        txtTenKhach.setEnabled(false);
        txtMaHoaDon.setEnabled(false);
        txtBan.setEnabled(false);
        txtTongTien.setEnabled(false);
        txtSDT.setEnabled(false);

        cbNhanVien.setEnabled(false);
        txtKhuyenMai.setEnabled(false);

        dtThoiGianVao.setEnabled(false);
        dtThoiGianRa.setEnabled(false);

        // 🔥 CHỈ CHO PHÉP TRẠNG THÁI
        cbTrangThai.setEnabled(true);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        for (Object[] row : hd_dao.getAllHoaDon()) {

            Timestamp vao = (Timestamp) row[1];
            Timestamp ra  = (Timestamp) row[2];

            tableModel.addRow(new Object[]{
                    row[0],
                    vao != null ? sdf.format(vao) : "",
                    ra  != null ? sdf.format(ra)  : "",
                    row[3],
                    row[4],
                    row[5],
                    row[6],
                    row[7],
                    row[8],
                    row[9]
            });
        }
    }

    // =========================================================================
    // 6. HELPER
    // =========================================================================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);

        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(scaledFontStatic("SansSerif", Font.PLAIN, 13));

        int padV = (int)(6 * SCALE);
        int padH = (int)(8 * SCALE);

        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(padV, padH, padV, padH)
        ));

        int height = (int)(36 * SCALE);

        tf.setPreferredSize(new Dimension(0, height)); 


        return tf;
    }
    
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);

        cb.setFont(scaledFontStatic("SansSerif", Font.PLAIN, 15));
        cb.setBackground(Color.WHITE);

        int height = (int)(36 * SCALE);

        cb.setPreferredSize(new Dimension(0, height));

        // padding giống textfield
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        return cb;
    }

    /** JDateChooser – chỉ chọn từ lịch**/
    private JDateChooser createDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("HH:mm dd/MM/yyyy");
        dc.setPreferredSize(new Dimension(0, (int)(36 * SCALE)));
//        dc.setPreferredSize(null);
        dc.getDateEditor().getUiComponent().setEnabled(false); // không gõ tay
        JTextField editor = (JTextField) dc.getDateEditor().getUiComponent();
        editor.setFont(scaledFontStatic("SansSerif", Font.PLAIN, 13));
        return dc;
    }
    
    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(Color.WHITE);

        // 🔥 TẮT VIỀN ĐEN (QUAN TRỌNG NHẤT)
        cb.setFocusable(false);
        cb.setRequestFocusEnabled(false);

        int height = (int)(36 * SCALE);

        cb.setPreferredSize(new Dimension(0, height));
        cb.setMinimumSize(new Dimension(0, height));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, false);

                lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                return lbl;
            }
        });
    }


    
    
    private void addRow(JPanel p, GridBagConstraints gbc, int row,
            String lbl1, JComponent c1, String lbl2, JComponent c2) {

gbc.gridy = row;

// LABEL 1
gbc.gridx = 0;
gbc.weightx = 0;
gbc.gridwidth = 1;
gbc.anchor = GridBagConstraints.EAST;
p.add(createLabel(lbl1), gbc);

// FIELD 1
gbc.gridx = 1;
gbc.weightx = 1;
gbc.anchor = GridBagConstraints.WEST;
p.add(c1, gbc);

// LABEL 2
gbc.gridx = 2;
gbc.weightx = 0;
gbc.anchor = GridBagConstraints.EAST;
p.add(createLabel(lbl2), gbc);

// FIELD 2
gbc.gridx = 3;
gbc.weightx = 1;
gbc.anchor = GridBagConstraints.WEST;
p.add(c2, gbc);
}
 

    
//    

    // =========================================================================
    // 7. MAIN – demo (dùng JLayeredPane để menu không đẩy content xuống)
    // =========================================================================
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale", "auto");
    	 	
    	String fontName = "SansSerif"; 

    	UIManager.put("Label.font",   scaledFontStatic(fontName, Font.PLAIN, 13));
    	UIManager.put("Button.font",  scaledFontStatic(fontName, Font.BOLD, 13));
    	UIManager.put("TextField.font", scaledFontStatic(fontName, Font.PLAIN, 13));
    	UIManager.put("Table.font",   scaledFontStatic(fontName, Font.PLAIN, 13));
    	UIManager.put("TableHeader.font", scaledFontStatic(fontName, Font.BOLD, 13));
    	UIManager.put("ComboBox.font", scaledFontStatic(fontName, Font.PLAIN, 13));
    	UIManager.put("ComboBox.listFont", scaledFontStatic(fontName, Font.PLAIN, 13));
    	
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            new HoaDon_GUI(null).setVisible(true);
        });
    }
}