package gui;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.SimpleDateFormat;

public class NhanVien_GUI extends JFrame {

    private static final Color CLR_HEADER_BG = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG = Color.WHITE;
    private static final Color CLR_FORM_BG = new Color(245, 242, 235);
    private static final Color CLR_PANEL_BG = new Color(238, 234, 222);
    private static final Color CLR_BTN_ADD = new Color(102, 187, 106);
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);
    private static final Color CLR_BTN_RESET = new Color(255, 255, 255);
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246);
    private static final Color CLR_TABLE_HDR = new Color(200, 192, 175);
    private static final Color CLR_BORDER = new Color(160, 145, 120);

    private JTextField txtMaNV, txtHoTen, txtEmail, txtSDT, txtCCCD, txtLyDoNghi;
    private JDateChooser txtNgaySinh;
    private JComboBox<String> cbChucVu, cbTrangThai;
    private JRadioButton rdNam, rdNu;
    private ButtonGroup bgGioiTinh;
    private JLabel lblAnh;
    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu;
    private static TaiKhoan taiKhoanDangNhap;

    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private Connection con;
    private String duongDanAnh = "";
    private List<NhanVien> dsNV;
    
    private static Font scaledFontStatic(String name, int style, int size) {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        AffineTransform at = gc.getDefaultTransform();
        double scale = at.getScaleX();
        return new Font(name, style, (int) (size * scale));
    }

    private static final double SCALE;
    static {
        AffineTransform at = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getDefaultTransform();
        SCALE = at.getScaleX();
    }

    private static final Dimension FIELD_SIZE =
            new Dimension((int) (320 * SCALE), (int) (32 * SCALE));

    
   
	public NhanVien_GUI(TaiKhoan tk) {

	    // 🔥 FIX 1: UI SCALE + FONT (QUAN TRỌNG NHẤT)
	    System.setProperty("sun.java2d.uiScale", "auto");

	    UIManager.put("Label.font",   scaledFontStatic("SansSerif", Font.PLAIN, 12));
	    UIManager.put("Button.font",  scaledFontStatic("SansSerif", Font.BOLD, 12));
	    UIManager.put("TextField.font", scaledFontStatic("SansSerif", Font.PLAIN, 13));
	    UIManager.put("Table.font",   scaledFontStatic("SansSerif", Font.PLAIN, 12));
	    UIManager.put("TableHeader.font", scaledFontStatic("SansSerif", Font.BOLD, 12));
	    UIManager.put("ComboBox.font", scaledFontStatic("SansSerif", Font.PLAIN, 12));
	    UIManager.put("ComboBox.listFont", scaledFontStatic("SansSerif", Font.PLAIN, 12));
	    UIManager.put("RadioButton.font", scaledFontStatic("SansSerif", Font.PLAIN, 13));

	    // 🔥 FIX 2: LookAndFeel
	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    setTitle("Quản Lý Nhân Viên");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    JLayeredPane layeredPane = new JLayeredPane();
	    layeredPane.setLayout(null);
	    setContentPane(layeredPane);

	    Pn_ThanhMenu menu = new Pn_ThanhMenu(tk);

	    JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setBackground(CLR_PANEL_BG);
	    mainPanel.add(buildTitlePanel(), BorderLayout.NORTH);
	    mainPanel.add(buildCenterPanel(), BorderLayout.CENTER);

	    layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
	    layeredPane.add(menu, JLayeredPane.PALETTE_LAYER);

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

	    setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setMinimumSize(new Dimension(1280, 720));
	    setLocationRelativeTo(null);


	    // 🔥 FIX 3: đảm bảo layout chạy ngay lần đầu
	    SwingUtilities.invokeLater(() -> {
	        int w = getWidth();
	        int h = getHeight();

	        mainPanel.setBounds(0, 42, w, h - 42);
	        menu.setBounds(0, 0, w, 42);

	        layeredPane.revalidate();
	        layeredPane.repaint();
	    });

	    con = ConnectDB.getConnection();
	    loadData();
	    txtMaNV.setText(nv_dao.getNextMaNV());
	}
    
    
    

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lbl.setFont(scaledFontStatic("SansSerif", Font.BOLD, 26));
        lbl.setForeground(CLR_HEADER_FG);
        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel buildCenterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topArea = new JPanel(new BorderLayout(0, 4));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(), BorderLayout.NORTH);
        topArea.add(buildButtonPanel(), BorderLayout.CENTER);

        pnl.add(topArea, BorderLayout.NORTH);
        pnl.add(buildTablePanel(), BorderLayout.CENTER);
        return pnl;
    }

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
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = createTextField(16);
        txtMaNV.setEnabled(false);
        txtMaNV.setDisabledTextColor(new Color(60, 60, 60));

        cbChucVu = new JComboBox<>(new String[]{"Quản lý", "Lễ tân"});
        styleComboBox(cbChucVu);
        addRow(pnlFields, gbc, 0, "Mã nhân viên", txtMaNV, "Chức vụ", cbChucVu);

        txtHoTen = createTextField(16);
        txtEmail = createTextField(16);
        addRow(pnlFields, gbc, 1, "Họ tên", txtHoTen, "Email", txtEmail);

        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setPreferredSize(FIELD_SIZE);
        txtNgaySinh.setMinimumSize(FIELD_SIZE);
        txtNgaySinh.setDateFormatString("dd/MM/yyyy");

        JTextField editor = (JTextField) txtNgaySinh.getDateEditor().getUiComponent();
        editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        editor.setEnabled(false);
        editor.setFont(scaledFontStatic("Times New Roman", Font.PLAIN, 13));

        txtSDT = createTextField(16);
        addRow(pnlFields, gbc, 2, "Ngày sinh", txtNgaySinh, "SĐT", txtSDT);

        rdNam = new JRadioButton("Nam");
        rdNu = new JRadioButton("Nữ");
        rdNam.setOpaque(false);
        rdNu.setOpaque(false);

        bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rdNam);
        bgGioiTinh.add(rdNu);
        rdNam.setSelected(true);

        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlGT.setOpaque(false);
        pnlGT.add(rdNam);
        pnlGT.add(rdNu);

        cbTrangThai = new JComboBox<>(new String[]{"Đang làm", "Nghỉ việc"});
        styleComboBox(cbTrangThai);
        cbTrangThai.addActionListener(e -> xuLyTrangThai());
        addRowCustomLeft(pnlFields, gbc, 3, "Giới tính", pnlGT, "Trạng thái", cbTrangThai);

        txtCCCD = createTextField(16);
        txtLyDoNghi = createTextField(16);
        txtLyDoNghi.setEnabled(false);
        addRow(pnlFields, gbc, 4, "CCCD", txtCCCD, "Lý do nghỉ", txtLyDoNghi);

        outer.add(pnlFields, BorderLayout.CENTER);

        JPanel pnlAnh = new JPanel();
        pnlAnh.setLayout(new BoxLayout(pnlAnh, BoxLayout.Y_AXIS));
        pnlAnh.setOpaque(false);
        pnlAnh.setPreferredSize(new Dimension(180, 0));
        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JLabel lblTitle = new JLabel("Ảnh nhân viên");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAnh = new JLabel();
        lblAnh.setPreferredSize(new Dimension(150, 190));
        lblAnh.setMinimumSize(new Dimension(150, 150));
        lblAnh.setMaximumSize(new Dimension(150, 200));
        lblAnh.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setVerticalAlignment(SwingConstants.CENTER);
        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        lblAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblAnh.setToolTipText("Nhấn để chọn ảnh");

        lblAnh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chonAnh();
            }
        });

        pnlAnh.add(lblTitle);
        pnlAnh.add(Box.createVerticalStrut(10));
        pnlAnh.add(lblAnh);

        outer.add(pnlAnh, BorderLayout.EAST);

        return outer;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String lbl1, JComponent comp1,
                        String lbl2, JComponent comp2) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        p.add(createLabel(lbl1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        p.add(comp1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.2;
        p.add(createLabel(lbl2), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.8;
        p.add(comp2, gbc);
    }

    private void addRowCustomLeft(JPanel p, GridBagConstraints gbc, int row,
                                  String lbl1, JComponent comp1,
                                  String lbl2, JComponent comp2) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        p.add(createLabel(lbl1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        p.add(comp1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.2;
        p.add(createLabel(lbl2), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.8;
        p.add(comp2, gbc);
    }

    private void xuLyTrangThai() {
        String trangThai = cbTrangThai.getSelectedItem() == null
                ? ""
                : cbTrangThai.getSelectedItem().toString();

        if ("Nghỉ việc".equalsIgnoreCase(trangThai)) {
            txtLyDoNghi.setEnabled(true);
        } else {
            txtLyDoNghi.setText("");
            txtLyDoNghi.setEnabled(false);
        }
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private JButton createFuncButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
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
        btn.setPreferredSize(new Dimension(140, 35));
        btn.setMinimumSize(new Dimension(140, 40));
        btn.setMargin(new Insets(2, 8, 2, 8));
        btn.setIcon(loadIcon(iconPath, 18, 18));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(6);

        return btn;
    }

    private JPanel buildButtonPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        btnThem = createFuncButton("Thêm", CLR_BTN_ADD, "img/cn_them.png");
        btnCapNhat = createFuncButton("Cập nhật", CLR_BTN_UPDATE, "img/cn_capnhat.png");
        btnLamMoi = createFuncButton("Làm mới", CLR_BTN_RESET, "img/mn_xuly.png");
        btnTraCuu = createFuncButton("Tra cứu", CLR_BTN_SEARCH, "img/mn_tracuu.png");

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

    private JScrollPane buildTablePanel() {
        String[] cols = {
                "Mã nhân viên", "Tên nhân viên", "Giới tính",
                "Ngày sinh", "SĐT", "CCCD", "Email", "Chức vụ", "Trạng thái", "Lý do nghỉ"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Object valueTrangThai = table.getValueAt(row, 8);
                String trangThai = valueTrangThai == null ? "" : valueTrangThai.toString();

                if (!isSelected) {
                    c.setForeground("Nghỉ việc".equalsIgnoreCase(trangThai) ? Color.RED : Color.BLACK);
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

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

        int[] widths = {110, 150, 80, 100, 110, 130, 180, 110, 100, 180};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadRowToForm();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        return scroll;
    }

    private void themNhanVien() {
        try {
            String maNV = nv_dao.getNextMaNV();
            String trangThai = cbTrangThai.getSelectedItem().toString();
            String lyDoNghi = txtLyDoNghi.getText().trim();

            if ("Nghỉ việc".equalsIgnoreCase(trangThai) && lyDoNghi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do nghỉ!");
                txtLyDoNghi.requestFocus();
                return;
            }

            if (!"Nghỉ việc".equalsIgnoreCase(trangThai)) {
                lyDoNghi = null;
            }

            NhanVien nv = new NhanVien(
                    maNV,
                    txtHoTen.getText().trim(),
                    duongDanAnh,
                    txtNgaySinh.getDate(),
                    rdNam.isSelected(),
                    txtCCCD.getText().trim(),
                    txtEmail.getText().trim(),
                    txtSDT.getText().trim(),
                    cbChucVu.getSelectedItem().toString(),
                    trangThai,
                    lyDoNghi
            );

            if (nv_dao.themNhanVien(nv)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm nhân viên!");
        }
    }

    private void capNhatNhanVien() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật!");
            return;
        }

        try {


            // ===== LẤY DỮ LIỆU CŨ =====
            NhanVien nvCu = dsNV.get(row);

            String anh = duongDanAnh;

            // nếu chưa chọn ảnh mới → giữ ảnh cũ
            if (anh == null || anh.isEmpty()) {
                anh = nvCu.getAnhNhanVien();
            }

            // ===== TRẠNG THÁI + LÝ DO =====
            String trangThaiCu = nvCu.getTrangThai();
            String trangThaiMoi = cbTrangThai.getSelectedItem().toString();

            String lyDo = nvCu.getLyDo(); // mặc định giữ lý do cũ

            // chỉ hỏi khi chuyển từ Đang làm → Nghỉ việc
            if (trangThaiCu.equalsIgnoreCase("Đang làm")
                    && trangThaiMoi.equalsIgnoreCase("Nghỉ việc")) {

                lyDo = JOptionPane.showInputDialog(this, "Nhập lý do nghỉ việc:");

                if (lyDo == null || lyDo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Phải nhập lý do!");
                    return;
                }
            }

            // nếu chuyển lại Đang làm → xóa lý do
            if (trangThaiMoi.equalsIgnoreCase("Đang làm")) {
                lyDo = null;
            }

            // ===== TẠO OBJECT (QUAN TRỌNG NHẤT) =====
            NhanVien nv = new NhanVien(
                    txtMaNV.getText().trim(),
                    txtHoTen.getText().trim(),
                    anh,
                    txtNgaySinh.getDate(),
                    rdNam.isSelected(),
                    txtCCCD.getText().trim(),
                    txtEmail.getText().trim(),
                    txtSDT.getText().trim(),
                    cbChucVu.getSelectedItem().toString(),

                    trangThaiMoi,
                    lyDo // 🔥 LƯU DB Ở ĐÂY
            );

            // ===== UPDATE DATABASE =====
            if (nv_dao.capNhatNhanVien(nv)) {

                String ngaySinh = "";
                if (nv.getNgaySinh() != null) {
                    ngaySinh = new SimpleDateFormat("dd/MM/yyyy")
                            .format(nv.getNgaySinh());
                }

                // ===== UPDATE TABLE =====
                tableModel.setValueAt(nv.getMaNV(), row, 0);
                tableModel.setValueAt(nv.getHoTen(), row, 1);
                tableModel.setValueAt(nv.isGioiTinh() ? "Nam" : "Nữ", row, 2);
                tableModel.setValueAt(ngaySinh, row, 3);
                tableModel.setValueAt(nv.getSdt(), row, 4);
                tableModel.setValueAt(nv.getCccd(), row, 5);
                tableModel.setValueAt(nv.getEmail(), row, 6);
                tableModel.setValueAt(nv.getChucVu(), row, 7);
                tableModel.setValueAt(nv.getTrangThai(), row, 8);

                // reload lại dữ liệu (để lấy luôn lý do từ DB)
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật nhân viên!");
        }
    }

    private void lamMoi() {
        txtMaNV.setText(nv_dao.getNextMaNV());
        txtHoTen.setText("");
        txtNgaySinh.setDate(null);
        txtEmail.setText("");
        txtSDT.setText("");
        txtCCCD.setText("");
        txtLyDoNghi.setText("");
        txtLyDoNghi.setEnabled(false);

        cbChucVu.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        rdNam.setSelected(true);

        table.clearSelection();
        lblAnh.setIcon(null);
        lblAnh.setText("");
        duongDanAnh = "";
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
        if (row < 0 || row >= dsNV.size()) return;

        NhanVien nv = dsNV.get(row);
        duongDanAnh = nv.getAnhNhanVien();

        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());

        if (nv.isGioiTinh()) {
            rdNam.setSelected(true);
        } else {
            rdNu.setSelected(true);
        }

        txtNgaySinh.setDate(nv.getNgaySinh());
        txtSDT.setText(nv.getSdt());
        txtCCCD.setText(nv.getCccd());
        txtEmail.setText(nv.getEmail());

        cbChucVu.setSelectedItem(nv.getChucVu());
        cbTrangThai.setSelectedItem(nv.getTrangThai());

        if ("Nghỉ việc".equalsIgnoreCase(nv.getTrangThai())) {
            txtLyDoNghi.setEnabled(true);
            txtLyDoNghi.setText(nv.getLyDo() == null ? "" : nv.getLyDo());

        } else {
            txtLyDoNghi.setText("");
            txtLyDoNghi.setEnabled(false);
        }

        String fileName = nv.getAnhNhanVien();
        if (fileName != null && !fileName.isEmpty()) {
            String fullPath = System.getProperty("user.dir") + "/img/" + fileName;
            File file = new File(fullPath);

            if (file.exists()) {
                ImageIcon icon = new ImageIcon(fullPath);
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");
            } else {
                lblAnh.setIcon(null);
                lblAnh.setText("Không tìm thấy ảnh");
            }
        } else {
            lblAnh.setIcon(null);
            lblAnh.setText("");
        }
    }

    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/img"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                String projectPath = System.getProperty("user.dir");
                File folder = new File(projectPath + "/img");
                if (!folder.exists()) folder.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getName();
                File dest = new File(folder, fileName);

                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                duongDanAnh = fileName;

                ImageIcon icon = new ImageIcon(dest.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi chọn ảnh!");
            }
        }
    }

    private JLabel createLabel(String text) {
        return new JLabel(text);
    }

    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setPreferredSize(FIELD_SIZE);
        tf.setMinimumSize(FIELD_SIZE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(Color.WHITE);
        cb.setFocusable(false);
        cb.setPreferredSize(FIELD_SIZE);
        cb.setMinimumSize(FIELD_SIZE);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_SIZE.height));
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(1, 6, 1, 6)
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

    public void loadData() {
        if (tableModel == null) return;

        dsNV = nv_dao.getAllNhanVien();
        tableModel.setRowCount(0);

        for (NhanVien nv : dsNV) {
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
            String ngaySinh = "";

            if (nv.getNgaySinh() != null) {
                ngaySinh = new SimpleDateFormat("dd/MM/yyyy").format(nv.getNgaySinh());
            }

            tableModel.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getHoTen(),
                    gioiTinh,
                    ngaySinh,
                    nv.getSdt(),
                    nv.getCccd(),
                    nv.getEmail(),
                    nv.getChucVu(),
                    nv.getTrangThai(),
                    nv.getLyDo()
            });
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "auto");

        UIManager.put("Label.font", scaledFontStatic("SansSerif", Font.PLAIN, 12));
        UIManager.put("Button.font", scaledFontStatic("SansSerif", Font.BOLD, 12));
        UIManager.put("TextField.font", scaledFontStatic("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.font", scaledFontStatic("SansSerif", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", scaledFontStatic("SansSerif", Font.BOLD, 12));
        UIManager.put("ComboBox.font", scaledFontStatic("SansSerif", Font.PLAIN, 12));
        UIManager.put("ComboBox.listFont", scaledFontStatic("SansSerif", Font.PLAIN, 12));
        UIManager.put("RadioButton.font", scaledFontStatic("SansSerif", Font.PLAIN, 13));

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new NhanVien_GUI(null).setVisible(true);

        });
    }
}
