package gui;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;

import entity.TaiKhoan;

import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NhanVien_GUI extends JPanel {

    // Màu sắc 
    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);   // nâu đậm
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(245, 242, 235); // kem nhạt
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222); // nền chính
    private static final Color CLR_BTN_ADD    = new Color(102, 187, 106); // xanh lá
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);  // vàng
    private static final Color CLR_BTN_RESET  = new Color(255, 213, 79);  // vàng
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246); // xanh dương nhạt
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175); // xám nâu
    private static final Color CLR_BORDER     = new Color(160, 145, 120); // viền

    private JTextField txtMaNV, txtHoTen, txtEmail, txtSDT, txtCCCD;
    private JDateChooser txtNgaySinh;
    private JComboBox<String> cbChucVu, cbTrangThai;
    private JRadioButton rdNam, rdNu;
    private ButtonGroup bgGioiTinh;
    private JLabel lblAnh;

    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu;
    private static TaiKhoan taiKhoanDangNhap;

    public NhanVien_GUI(TaiKhoan tk) {
        setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);

        add(buildTitlePanel(),  BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lbl.setFont(scaledFontStatic("Times New Roman", Font.BOLD, 26));
        lbl.setForeground(CLR_HEADER_FG);
        lbl.setOpaque(false);
        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }

    // 2. PHẦN TRUNG TÂM (form + nút + bảng)
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

    // 3. FORM NHẬP LIỆU
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 0));
        outer.setBackground(CLR_FORM_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // ── Phần trái: các trường thông tin 
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtMaNV  = createTextField(16);
        txtMaNV.setEnabled(false); //  không cho nhập tay
        txtMaNV.setDisabledTextColor(new Color(60, 60, 60));
        cbChucVu = new JComboBox<>(new String[]{"Quản lý", "Nhân viên lễ tân"});
        styleComboBox(cbChucVu);

        addRow(pnlFields, gbc, 0, "Mã Nhân Viên", txtMaNV, "Chức vụ", cbChucVu);

        // Hàng 1: Họ tên / Email
        txtHoTen = createTextField(16);
        txtEmail = createTextField(16);
        addRow(pnlFields, gbc, 1, "Họ tên", txtHoTen, "Email", txtEmail);

        // Hàng 2: Ngày sinh / SĐT
        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setDateFormatString("dd/MM/yyyy");
        txtNgaySinh.setPreferredSize(null);
        txtNgaySinh.getDateEditor().getUiComponent().setEnabled(false);
        ((JTextField) txtNgaySinh.getDateEditor().getUiComponent())
        .setFont(scaledFontStatic("Times New Roman", Font.PLAIN, 13));

        txtSDT      = createTextField(16);
        addRow(pnlFields, gbc, 2, "Ngày sinh", txtNgaySinh, "SĐT:", txtSDT);
        

        // Hàng 3: Giới tính / Trạng thái
        rdNam = new JRadioButton("Nam"); rdNam.setOpaque(false);
        rdNu  = new JRadioButton("Nữ");  rdNu.setOpaque(false);
//        rdNam.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//        rdNu.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rdNam); bgGioiTinh.add(rdNu);
        rdNam.setSelected(true);

        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlGT.setOpaque(false);
        pnlGT.add(rdNam); pnlGT.add(rdNu);

        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc"});
        styleComboBox(cbTrangThai);
        cbTrangThai.setSelectedItem("Nghỉ việc");

        addRowCustomLeft(pnlFields, gbc, 3, "Giới tính", pnlGT, "Trạng thái", cbTrangThai);
        
        txtCCCD = createTextField(16);
        JLabel lblEmpty = new JLabel("");  // ô trái bỏ trống
        addRow(pnlFields, gbc, 4, "CCCD", txtCCCD, "", lblEmpty);
        outer.add(pnlFields, BorderLayout.CENTER);

        // ── Phần phải: ảnh nhân viên 
        JPanel pnlAnh = new JPanel(new BorderLayout(0, 4));
        pnlAnh.setOpaque(false);
        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JLabel lblTitle = new JLabel("Ảnh nhân viên", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 12));

        lblAnh = new JLabel() ;
        lblAnh.setPreferredSize(new Dimension(150, 150));
        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        lblAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblAnh.setToolTipText("Nhấn để chọn ảnh");
        lblAnh.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { chonAnh(); }
        });

        pnlAnh.add(lblTitle, BorderLayout.NORTH);
        pnlAnh.add(lblAnh,   BorderLayout.CENTER);
        outer.add(pnlAnh, BorderLayout.EAST);

        return outer;
    }

    /** Thêm một hàng có 2 cặp nhãn-component kiểu JTextField */
    private void addRow(JPanel p, GridBagConstraints gbc,
                        int row,
                        String lbl1, JComponent comp1,
                        String lbl2, JComponent comp2) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel(lbl1), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(comp1, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        p.add(createLabel(lbl2), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        p.add(comp2, gbc);
    }

    /** Hàng trái có component tuỳ ý (ví dụ: radio group) */
    private void addRowCustomLeft(JPanel p, GridBagConstraints gbc,
                                  int row,
                                  String lbl1, JComponent comp1,
                                  String lbl2, JComponent comp2) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(createLabel(lbl1), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(comp1, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        p.add(createLabel(lbl2), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        p.add(comp2, gbc);
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
        return new Font(name, style, (int)(size * SCALE));
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
//        btn.setFont(new Font("Times New Roman", Font.BOLD, 13));
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.setMargin(new Insets(2, 8, 2, 8));
        btn.setIconTextGap(10);

        // ── Thêm icon trước chữ ──────────────────────────────
        btn.setIcon(loadIcon(iconPath, 18, 18));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT); // chữ bên phải icon
        btn.setIconTextGap(6);                               // khoảng cách icon - chữ

        return btn;
    }

    // 4. CÁC NÚT CHỨC NĂNG
    private JPanel buildButtonPanel() {
//        JPanel pnl = new JPanel(new GridLayout(1,4,15,0));
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));

        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        btnThem    = createFuncButton("Thêm",    CLR_BTN_ADD,"img/cn_them.png");
        btnCapNhat = createFuncButton("Cập nhật", CLR_BTN_UPDATE,"img/cn_capnhat.png");
        btnLamMoi  = createFuncButton("Làm mới", CLR_BTN_RESET,"img/mn_xuly.png");
        btnTraCuu  = createFuncButton("Tra cứu", CLR_BTN_SEARCH,"img/mn_tracuu.png");

        pnl.add(btnThem);
        pnl.add(btnCapNhat);
        pnl.add(btnLamMoi);
        pnl.add(btnTraCuu);

        btnThem.addActionListener(e -> themNhanVien());
        btnCapNhat.addActionListener(e -> capNhatNhanVien());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTraCuu.addActionListener(e -> traCuu());

        return pnl;
    }
    
    
    

    // 5. BẢNG DANH SÁCH
    private JScrollPane buildTablePanel() {
        String[] cols = {
            "Mã nhân viên", "Tên nhân viên", "Giới Tính",
            "Ngày sinh", "SĐT", "CCCD", "Email", "Trạng thái"
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

        // Header
        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Times New Roman", Font.BOLD, 13));
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setReorderingAllowed(false);

        // Cột hẹp / rộng
        int[] widths = {110, 130, 80, 100, 110, 130, 160, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Chọn dòng → điền form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
        });
        
        tableModel.addRow(new Object[]{
        "NV001","Hoàng Anh","Nữ","03/02/2000","0123456789","012222345678","anh12@gmail.com","Đang làm việc"        
        });

      

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
//        scroll.setPreferredSize(new Dimension(0, 200));
        return scroll;
    }

    // 6. LOGIC XỬ LÝ (nghiệp vụ cơ bản – bạn mở rộng kết nối DB)
    private void themNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên chưa load!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Date date = txtNgaySinh.getDate();
        String ngaySinhStr = "";

        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngaySinhStr = sdf.format(date);
        }
        tableModel.addRow(new Object[]{
            txtMaNV.getText().trim(),
            txtHoTen.getText().trim(),
            rdNam.isSelected() ? "Nam" : "Nữ",
            ngaySinhStr,
            txtSDT.getText().trim(),
            txtCCCD.getText().trim(),
            txtEmail.getText().trim(),
            cbTrangThai.getSelectedItem()
        });
        lamMoi();
    }

    private void capNhatNhanVien() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Date date = txtNgaySinh.getDate();
        String ngaySinhStr = "";

        if (date != null) {
            ngaySinhStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
        }
        tableModel.setValueAt(txtMaNV.getText().trim(),          row, 0);
        tableModel.setValueAt(txtHoTen.getText().trim(),         row, 1);
        tableModel.setValueAt(rdNam.isSelected() ? "Nam" : "Nữ",row, 2);
        tableModel.setValueAt(ngaySinhStr, row, 3);
        tableModel.setValueAt(txtSDT.getText().trim(),           row, 4);
        tableModel.setValueAt(txtCCCD.getText().trim(),          row, 5);
        tableModel.setValueAt(txtEmail.getText().trim(),         row, 6);
        tableModel.setValueAt(cbTrangThai.getSelectedItem(),     row, 7);
        lamMoi();
    }

    private void lamMoi() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setDate(null);
        txtEmail.setText("");
        txtSDT.setText("");
        txtCCCD.setText("");
        cbChucVu.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        rdNam.setSelected(true);
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
        txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        String gt = tableModel.getValueAt(row, 2).toString();
        if (gt.equalsIgnoreCase("Nam")) rdNam.setSelected(true); else rdNu.setSelected(true);
        
        String dateStr = tableModel.getValueAt(row, 3).toString();

        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
            txtNgaySinh.setDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        txtNgaySinh.setText(tableModel.getValueAt(row, 3).toString());
        txtSDT.setText(tableModel.getValueAt(row, 4).toString());
        txtCCCD.setText(tableModel.getValueAt(row, 5).toString());
        txtEmail.setText(tableModel.getValueAt(row, 6).toString());
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 7).toString());
    }

    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Ảnh (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ImageIcon icon = new ImageIcon(fc.getSelectedFile().getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(lblAnh.getWidth(), lblAnh.getHeight(), Image.SCALE_SMOOTH);
            lblAnh.setIcon(new ImageIcon(scaled));
            lblAnh.setText("");
        }
    }

    // 7. HELPER – tạo component phụ
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
//        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
//        tf.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        tf.setPreferredSize(null);
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
//        cb.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        cb.setPreferredSize(null); 
    }

    /** Nút tròn bo góc với màu nền tuỳ chỉnh */
    private JButton createFuncButton(String text, Color bg) {
        JButton btn = new JButton(text)
        {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
//        btn.setFont(new Font("Times New Roman", Font.BOLD, 13));
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 34));
        return btn;
    }

//     8. MAIN – demo độc lập (xoá khi ghép vào JFrame chính)
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale", "auto");

    	UIManager.put("Label.font",   scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("Button.font",  scaledFontStatic("Times New Roman", Font.BOLD, 12));
    	UIManager.put("TextField.font", scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("Table.font",   scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("TableHeader.font", scaledFontStatic("Times New Roman", Font.BOLD, 12));
    	UIManager.put("ComboBox.font", scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	UIManager.put("ComboBox.listFont", scaledFontStatic("Times New Roman", Font.PLAIN, 12));
    	
    	
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            JFrame frame = new JFrame("Quản Lý Nhân Viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // ── Dùng JLayeredPane để menu đè lên content, không đẩy content xuống ──
            JLayeredPane layeredPane = new JLayeredPane();
            frame.setContentPane(layeredPane);

            NhanVien_GUI mainPanel = new NhanVien_GUI(null);
            Pn_ThanhMenu menuPanel = new Pn_ThanhMenu(taiKhoanDangNhap);

            // Layer thấp: content chính
            layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
            // Layer cao: menu đè lên trên
            layeredPane.add(menuPanel, JLayeredPane.PALETTE_LAYER);

            // Resize cả 2 theo kích thước frame
            layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int w = layeredPane.getWidth();
                    int h = layeredPane.getHeight();
                    int menuHeaderH = 42; // chiều cao cố định phần header menu

                    // Menu trải full width, đủ cao để popup hiện không bị cắt
                    menuPanel.setBounds(0, 0, w, 400);

                    // Content bắt đầu từ dưới header menu, không bị đẩy khi popup mở
                    mainPanel.setBounds(0, menuHeaderH, w, h - menuHeaderH);
                }
            });

            frame.setVisible(true);
        });
    }
}