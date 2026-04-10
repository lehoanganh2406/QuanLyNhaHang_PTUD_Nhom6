package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.List;

public class TaiKhoan_GUI extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────────────────────
    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(245, 242, 235);
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222);
    private static final Color CLR_BTN_ADD    = new Color(102, 187, 106);
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);
    private static final Color CLR_BTN_RESET  = new Color(255, 255, 255);
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246);
    private static final Color CLR_BTN_DELETE = new Color(255, 138, 101);  // cam đỏ
    private static final Color CLR_BTN_CHPWD  = new Color(206, 147, 216);  // tím nhạt
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175);
    private static final Color CLR_BORDER     = new Color(160, 145, 120);

    // ── Scale theo DPI màn hình ───────────────────────────────────────────────
    private static final double SCALE;
    static {
        AffineTransform at = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getDefaultTransform();
        SCALE = at.getScaleX();
    }
    private static Font scaledFont(String name, int style, int size) {
        return new Font(name, style, (int)(size * SCALE));
    }

    // ── Trường nhập liệu ─────────────────────────────────────────────────────
    private JTextField    txtMaDangNhap, txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbVaiTro, cbNhanVien, cbTrangThai;
    private JLabel        lblAnh;
    
    private JButton btnTogglePwd;
    private boolean isShowPassword = false;

    // ── Bảng ─────────────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;

    // ── Nút ──────────────────────────────────────────────────────────────────
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu, btnXoa, btnCapNhatMK;
	private Connection con;
	private TaiKhoan_DAO tk_dao= new TaiKhoan_DAO();
	private NhanVien_DAO nv_dao= new NhanVien_DAO();

    public TaiKhoan_GUI() {
        setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);
        add(buildTitlePanel(),  BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        con= ConnectDB.getConnection();
        loadData();
    }

    // =========================================================================
    // 1. TIÊU ĐỀ
    // =========================================================================
    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lbl.setFont(scaledFont("Times New Roman", Font.BOLD, 26));
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
        topArea.add(buildFormPanel(),   BorderLayout.NORTH);
        topArea.add(buildButtonPanel(), BorderLayout.CENTER);

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

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6 ,6 ,6 ,6 );
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Mã đăng nhập (disabled) / Vai trò
        txtMaDangNhap = createTextField();
        txtMaDangNhap.setEnabled(false);
        txtMaDangNhap.setDisabledTextColor(new Color(60, 60, 60));
        cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Nhân viên lễ tân"});
        styleComboBox(cbVaiTro);
        addRow(pnlFields, gbc, 0, "Mã tài khoản", txtMaDangNhap, "Vai trò", cbVaiTro);

        // Hàng 1: Tên đăng nhập / Nhân viên
        txtTenDangNhap = createTextField();
        cbNhanVien = new JComboBox<>();
        styleComboBox(cbNhanVien);
        cbNhanVien.addItem("--Chọn nhân viên ---");
        List<NhanVien> ds= nv_dao.getTenNhanVien();
        for (NhanVien nv : ds) {
			cbNhanVien.addItem(nv.getHoTen());
		}
        addRow(pnlFields, gbc, 1, "Tên đăng nhập", txtTenDangNhap, "Nhân viên:", cbNhanVien);

        // Hàng 2: Mật khẩu / Trạng thái
        txtMatKhau = new JPasswordField();
        txtMatKhau.setEnabled(false);
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(4,6,4,6)
        ));
        
 
        txtMatKhau.setEchoChar('•');
        JPanel pnlPwd = new JPanel(new BorderLayout());
        pnlPwd.setOpaque(false);

        btnTogglePwd = new JButton(loadIcon("img/Dn_eye_off.png", 18, 18));
        btnTogglePwd.setFocusPainted(false);
        btnTogglePwd.setBorder(null);
        btnTogglePwd.setContentAreaFilled(false);
        btnTogglePwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTogglePwd.setPreferredSize(new Dimension((int)(30*SCALE),(int)(30*SCALE)));
        
        pnlPwd.add(txtMatKhau, BorderLayout.CENTER);
        pnlPwd.add(btnTogglePwd, BorderLayout.EAST);

        // toggle
        btnTogglePwd.addActionListener(e -> {
            if (isShowPassword) {
                txtMatKhau.setEchoChar('•');
                btnTogglePwd.setIcon(loadIcon("img/Dn_eye_off.png", 18, 18));
            } else {
                txtMatKhau.setEchoChar((char) 0);
                btnTogglePwd.setIcon(loadIcon("img/Dn_eye_open.png", 18, 18));
            }
            isShowPassword = !isShowPassword;
        });
        

        
        cbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Khoá"});
        styleComboBox(cbTrangThai);
        addRow(pnlFields, gbc, 2, "Mật khẩu", pnlPwd, "Trạng thái", cbTrangThai);

        outer.add(pnlFields, BorderLayout.CENTER);

        // ── Ảnh nhân viên ────────────────────────────────────────────────────
        JPanel pnlAnh = new JPanel(new BorderLayout(0, 4));
        pnlAnh.setOpaque(false);
        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JLabel lblTitle = new JLabel("Ảnh nhân viên", SwingConstants.CENTER);

        lblAnh = new JLabel() {};
        lblAnh.setPreferredSize(new Dimension(
        	    (int)(140 * SCALE),
        	    (int)(100 * SCALE)
        	));
        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setVerticalAlignment(SwingConstants.CENTER);

        pnlAnh.add(lblTitle, BorderLayout.NORTH);
        pnlAnh.add(lblAnh,   BorderLayout.CENTER);
        outer.add(pnlAnh, BorderLayout.EAST);

        return outer;
    }

//    private void addRow(JPanel p, GridBagConstraints gbc, int row,
//                        String lbl1, JComponent c1, String lbl2, JComponent c2) {
//        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; p.add(createLabel(lbl1), gbc);
//        gbc.gridx = 1; gbc.weightx = 1;                  p.add(c1, gbc);
//        gbc.gridx = 2; gbc.weightx = 0;                  p.add(createLabel(lbl2), gbc);
//        gbc.gridx = 3; gbc.weightx = 1;                  p.add(c2, gbc);
//    }
    
    private void addRow(JPanel p, GridBagConstraints gbc, int row,
            String lbl1, JComponent c1, String lbl2, JComponent c2) {
gbc.gridx = 0;
gbc.gridy = row;
gbc.weightx = 0;
gbc.fill = GridBagConstraints.NONE;
p.add(createLabel(lbl1), gbc);

gbc.gridx = 1;
gbc.weightx = 1;
gbc.fill = GridBagConstraints.HORIZONTAL;
p.add(c1, gbc);

gbc.gridx = 2;
gbc.weightx = 0;
gbc.fill = GridBagConstraints.NONE;
p.add(createLabel(lbl2), gbc);

gbc.gridx = 3;
gbc.weightx = 1;
gbc.fill = GridBagConstraints.HORIZONTAL;
p.add(c2, gbc);
}

    // =========================================================================
    // 4. NÚT CHỨC NĂNG
    // =========================================================================
    private JPanel buildButtonPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        btnThem      = createFuncButton("Thêm",              CLR_BTN_ADD,    "img/cn_them.png");
        btnCapNhat   = createFuncButton("Cập nhật",          CLR_BTN_UPDATE, "img/cn_capnhat.png");
        btnLamMoi    = createFuncButton("Làm mới",           CLR_BTN_RESET,  "img/mn_xuly.png");
        btnTraCuu    = createFuncButton("Tra cứu",           CLR_BTN_SEARCH, "img/mn_tracuu.png");
        btnXoa       = createFuncButton("Xóa",               CLR_BTN_DELETE, "img/cn_xoa.png");
        btnCapNhatMK = createFuncButton("Cập nhật mật khẩu", CLR_BTN_CHPWD,  "img/cn_capnhatmk.png");

        pnl.add(btnThem);
        pnl.add(btnCapNhat);
        pnl.add(btnLamMoi);
        pnl.add(btnTraCuu);
        pnl.add(btnXoa);
        pnl.add(btnCapNhatMK);

        btnThem.addActionListener(e      -> themTaiKhoan());
        btnCapNhat.addActionListener(e   -> capNhatTaiKhoan());
        btnLamMoi.addActionListener(e    -> lamMoi());
        btnTraCuu.addActionListener(e    -> traCuu());
        btnXoa.addActionListener(e       -> xoaTaiKhoan());
        btnCapNhatMK.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn tài khoản trước!");
                return;
            }

            String maNV = tableModel.getValueAt(row, 4).toString();

            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            new DoiMatKhau(parent, maNV).setVisible(true);
        });
        return pnl;
    }

    // =========================================================================
    // 5. BẢNG
    // =========================================================================
    private JScrollPane buildTablePanel() {
        String[] cols = {
            "Mã tài khoản", "Tên đăng nhập", "Vai trò",
            "Mật khẩu", "Nhân viên", "Trạng thái"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(CLR_BORDER);
        table.setSelectionBackground(new Color(180, 210, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setReorderingAllowed(false);

        int[] widths = {120, 130, 100, 100, 120, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
        });

        table.getColumnModel().getColumn(3).setCellRenderer(
        	    new DefaultTableCellRenderer() {
        	        @Override
        	        public Component getTableCellRendererComponent(
        	                JTable t, Object value, boolean isSelected,
        	                boolean hasFocus, int row, int col) {
        	            // Hiển thị • thay vì mật khẩu thật
        	            String masked = value == null ? "" 
        	                    : "•".repeat(value.toString().length());
        	            return super.getTableCellRendererComponent(
        	                    t, masked, isSelected, hasFocus, row, col);
        	        }
        	    }
        	);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        return scroll;
    }

    // =========================================================================
    // 6. LOGIC
    // =========================================================================
    private void themTaiKhoan() {
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.addRow(new Object[]{
            txtMaDangNhap.getText().trim(),
            txtTenDangNhap.getText().trim(),
            cbVaiTro.getSelectedItem(),
//            new String(txtMatKhau.getPassword()).isEmpty() ? "" : "******",
            new String(txtMatKhau.getPassword()),
            cbNhanVien.getSelectedItem(),
            "",
            cbTrangThai.getSelectedItem()
        });
        lamMoi();
    }

    private void capNhatTaiKhoan() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        tableModel.setValueAt(txtMaDangNhap.getText().trim(),  row, 0);
        tableModel.setValueAt(txtTenDangNhap.getText().trim(), row, 1);
        tableModel.setValueAt(cbVaiTro.getSelectedItem(),      row, 2);
        tableModel.setValueAt(new String(txtMatKhau.getPassword()), // FIX
        	                       row, 3);
        tableModel.setValueAt(cbNhanVien.getSelectedItem(),    row, 4);
        tableModel.setValueAt(cbTrangThai.getSelectedItem(),   row, 5);
        lamMoi();
    }

    private void xoaTaiKhoan() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(row);
            lamMoi();
        }
    }

//    private void capNhatMatKhau() {
//        int row = table.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần đổi mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        String newPwd = new String(txtMatKhau.getPassword()).trim();
//        if (newPwd.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới!", "Thông báo", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        tableModel.setValueAt(newPwd, row, 3);
//        JOptionPane.showMessageDialog(this, "Cập nhật mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//        lamMoi();
//    }

    private void lamMoi() {
        txtMaDangNhap.setText("");
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        cbVaiTro.setSelectedIndex(0);
        cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(this, "Nhập từ khóa tìm kiếm:", "Tra cứu", JOptionPane.PLAIN_MESSAGE);
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
        txtMaDangNhap.setText(tableModel.getValueAt(row, 0).toString());
        txtTenDangNhap.setText(tableModel.getValueAt(row, 1).toString());
        cbVaiTro.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        txtMatKhau.setText(tableModel.getValueAt(row, 3).toString());
        cbNhanVien.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }
    
    private void loadData() {
        List<TaiKhoan> ds = tk_dao.getAllTaiKhoan();

        tableModel.setRowCount(0); // xoá dữ liệu cũ

        for (TaiKhoan tk : ds) {
            tableModel.addRow(new Object[]{
                    tk.getMaTaiKhoan(),
                    tk.getTenDangNhap(),
                    tk.getPhanQuyen(),
                    tk.getMatKhau(),
                    tk.getMaNV().getHoTen(), // ⚠️ lấy từ object
                    tk.isTrangThai() ? "Hoạt động" : "Khoá"
            });
        }
    }

    // =========================================================================
    // 7. HELPER
    // =========================================================================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
//        tf.setPreferredSize(new Dimension(0, (int)(38 * SCALE)));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(4,6,4,6)
                
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        cb.setPreferredSize(new Dimension(0, (int)(30 * SCALE)));

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
        
        FontMetrics fm = btn.getFontMetrics(btn.getFont());
        int textWidth = fm.stringWidth(text);
        int iconWidth = (iconPath != null) ? (int)(20 * SCALE) : 0;

        int width = textWidth + iconWidth + (int)(30 * SCALE);
        int height = (int)(30 * SCALE);

        btn.setPreferredSize(new Dimension(width, height));
        
        
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 10, 4, 10));
        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18, 18));
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setIconTextGap(6);
        }
        return btn;
    }
    
    private Dimension getFieldSize() {
        return new Dimension((int)(520 * SCALE), (int)(32 * SCALE));
    }

    // =========================================================================
    // 8. MAIN
    // =========================================================================
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "auto");

        UIManager.put("Label.font",       scaledFont("Times New Roman", Font.PLAIN,  12));
        UIManager.put("Button.font",      scaledFont("Times New Roman", Font.BOLD,   12));
        UIManager.put("TextField.font",   scaledFont("Times New Roman", Font.PLAIN,  12));
        UIManager.put("PasswordField.font", scaledFont("Times New Roman", Font.PLAIN, 12));
        UIManager.put("Table.font",       scaledFont("Times New Roman", Font.PLAIN,  12));
        UIManager.put("TableHeader.font", scaledFont("Times New Roman", Font.BOLD,   12));
        UIManager.put("ComboBox.font",    scaledFont("Times New Roman", Font.PLAIN,  12));
        
        ConnectDB.getInstance().connect();

        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            JFrame frame = new JFrame("Quản Lý Tài Khoản");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            JLayeredPane layeredPane = new JLayeredPane();
            frame.setContentPane(layeredPane);

            TaiKhoan_GUI mainPanel = new TaiKhoan_GUI();
            Pn_ThanhMenu   menuPanel = new Pn_ThanhMenu();

            layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(menuPanel, JLayeredPane.PALETTE_LAYER);

            layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override public void componentResized(java.awt.event.ComponentEvent e) {
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