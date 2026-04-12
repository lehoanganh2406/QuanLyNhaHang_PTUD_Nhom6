package gui;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class NhanVien_GUI extends JFrame {

    // Màu sắc 
    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);   // nâu đậm
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(245, 242, 235); // kem nhạt
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222); // nền chính
    private static final Color CLR_BTN_ADD    = new Color(102, 187, 106); // xanh lá
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);  // vàng
    private static final Color CLR_BTN_RESET  = new Color(255, 255, 255);  // vàng
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246); // xanh dương nhạt
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175); // xám nâu
    private static final Color CLR_BORDER     = new Color(160, 145, 120); // viền

    private JTextField txtMaNV, txtHoTen, txtEmail, txtSDT, txtCCCD;
    private JDateChooser txtNgaySinh;
    private JComboBox<String> cbChucVu, cbTrangThai;
    private JRadioButton rdNam, rdNu;
    private ButtonGroup bgGioiTinh;
    private JLabel lblAnh;
    private Map<String, String> lyDoMap = new HashMap<>();
    private final String FILE_LYDO = "sql/lydo.txt";
    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu;
    private static TaiKhoan taiKhoanDangNhap;
    
    private NhanVien_DAO nv_dao= new NhanVien_DAO();
    private Connection con;
	private String duongDanAnh="";
	private List<NhanVien> dsNV;
	private JLabel lblLyDo;
	private JComponent lblEmpty;

   
    public NhanVien_GUI(TaiKhoan tk) {
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

        SwingUtilities.invokeLater(() -> {
            int w = getContentPane().getWidth();
            int h = getContentPane().getHeight();

            mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
            menu.setBounds(0, 0, w, h);
        });

        con = ConnectDB.getConnection();
        loadData();
        txtMaNV.setText(nv_dao.getNextMaNV());
        loadLyDoFromFile();
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

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lbl.setFont(scaledFontStatic("SansSerif", Font.BOLD, 26));
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
    private static final Dimension FIELD_SIZE =
            new Dimension((int)(320 * SCALE), (int)(32 * SCALE));
    
    
    
    
    
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
        gbc.weightx=1;
        gbc.ipadx=0;
        gbc.insets = new Insets(6,10,6,10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtMaNV  = createTextField(16);
        txtMaNV.setEnabled(false); //  không cho nhập tay
        txtMaNV.setDisabledTextColor(new Color(60, 60, 60));
        cbChucVu = new JComboBox<>(new String[]{"Quản lý", "Lễ tân"});
        styleComboBox(cbChucVu);

        addRow(pnlFields, gbc, 0, "Mã Nhân Viên", txtMaNV, "Chức vụ", cbChucVu);

        // Hàng 1: Họ tên / Email
        txtHoTen = createTextField(16);
        txtEmail = createTextField(16);
        addRow(pnlFields, gbc, 1, "Họ tên", txtHoTen, "Email", txtEmail);

        // Hàng 2: Ngày sinh / SĐT
        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setPreferredSize(FIELD_SIZE);
        txtNgaySinh.setMinimumSize(FIELD_SIZE);

        
        JTextField editor = (JTextField) txtNgaySinh.getDateEditor().getUiComponent();

     // set border giống textfield
     editor.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(CLR_BORDER),
             BorderFactory.createEmptyBorder(3, 8, 3, 8)
     ));
        
        
        txtNgaySinh.setDateFormatString("dd/MM/yyyy");
        txtNgaySinh.getDateEditor().getUiComponent().setEnabled(false);
        ((JTextField) txtNgaySinh.getDateEditor().getUiComponent())
        .setFont(scaledFontStatic("Times New Roman", Font.PLAIN, 13));

        txtSDT      = createTextField(16);
        addRow(pnlFields, gbc, 2, "Ngày sinh", txtNgaySinh, "SĐT:", txtSDT);
        

        // Hàng 3: Giới tính / Trạng thái
        rdNam = new JRadioButton("Nam"); rdNam.setOpaque(false);
        rdNu  = new JRadioButton("Nữ");  rdNu.setOpaque(false);
        bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rdNam); bgGioiTinh.add(rdNu);
        rdNam.setSelected(true);

        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlGT.setOpaque(false);
        pnlGT.add(rdNam); pnlGT.add(rdNu);

        cbTrangThai = new JComboBox<>(new String[]{
        	    "Đang làm",
        	    "Nghỉ việc"
        	});        
        styleComboBox(cbTrangThai);
        cbTrangThai.setSelectedItem("Nghỉ việc");

        addRowCustomLeft(pnlFields, gbc, 3, "Giới tính", pnlGT, "Trạng thái", cbTrangThai);
        
        txtCCCD = createTextField(16);
        lblLyDo = new JLabel("");
        lblLyDo.setForeground(Color.RED);
        addRow(pnlFields, gbc, 4, "CCCD", txtCCCD, "", lblLyDo);
        outer.add(pnlFields, BorderLayout.CENTER);

        // ── Phần phải: ảnh nhân viên 
     // ── Phần phải: ảnh nhân viên ─────────────────────────
        JPanel pnlAnh = new JPanel();
        pnlAnh.setLayout(new BoxLayout(pnlAnh, BoxLayout.Y_AXIS));
        pnlAnh.setOpaque(false);
        pnlAnh.setPreferredSize(new Dimension(180, 0));
        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Ảnh nhân viên");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label ảnh
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

        // Sự kiện chọn ảnh
        lblAnh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chonAnh();
            }
        });

        // Add vào panel
        pnlAnh.add(lblTitle);
        pnlAnh.add(Box.createVerticalStrut(10)); // khoảng cách
        pnlAnh.add(lblAnh);

        // Add vào form chính
        outer.add(pnlAnh, BorderLayout.EAST);

        return outer;
    }

    /** Thêm một hàng có 2 cặp nhãn-component kiểu JTextField */
    private void addRow(JPanel p, GridBagConstraints gbc, int row,
            String lbl1, JComponent comp1,
            String lbl2, JComponent comp2) {

// label trái
gbc.gridx = 0;
gbc.gridy = row;
gbc.weightx = 0.2;
gbc.fill = GridBagConstraints.HORIZONTAL;
p.add(createLabel(lbl1), gbc);

// field trái
gbc.gridx = 1;
gbc.weightx = 0.8;
p.add(comp1, gbc);

// label phải
gbc.gridx = 2;
gbc.weightx = 0.2;
p.add(createLabel(lbl2), gbc);

// field phải
gbc.gridx = 3;
gbc.weightx = 0.8;
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
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.setPreferredSize(new Dimension(140, 35));
        btn.setMinimumSize(new Dimension(140, 40));
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
            "Ngày sinh", "SĐT", "CCCD", "Email","Chức vụ", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String trangThai = table.getValueAt(row, 8).toString();

                if (trangThai.equalsIgnoreCase("Nghỉ việc")) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
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

        // Header
        JTableHeader header = table.getTableHeader();
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
        

      

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        return scroll;
    }

    // 6. LOGIC XỬ LÝ (nghiệp vụ cơ bản 
    private void themNhanVien() {

        try {
            String maNV = nv_dao.getNextMaNV(); // 

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
                    cbTrangThai.getSelectedItem().toString()
            );

            if (nv_dao.themNhanVien(nv)) {

                String ngaySinh = "";
                if (nv.getNgaySinh() != null) {
                    ngaySinh = new SimpleDateFormat("dd/MM/yyyy")
                            .format(nv.getNgaySinh());
                }
            	


                tableModel.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getHoTen(),
                        nv.isGioiTinh() ? "Nam" : "Nữ",
                        ngaySinh,
                        nv.getSdt(),
                        nv.getCccd(),
                        nv.getEmail(),
                        nv.getChucVu(),
                        nv.getTrangThai()
                });
                loadData();

                JOptionPane.showMessageDialog(this, "Thêm thành công!");

                lamMoi();

                txtMaNV.setText(nv_dao.getNextMaNV());

            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi!");
        }
    }

    private void capNhatNhanVien() {

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật!");
            return;
        }

        try {
        	
        	NhanVien nvCu = dsNV.get(row); // lấy dữ liệu cũ

        	String anh = duongDanAnh;

        	// nếu chưa chọn ảnh mới → giữ ảnh cũ
        	if (anh == null || anh.isEmpty()) {
        	    anh = nvCu.getAnhNhanVien();
        	}
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
                    cbTrangThai.getSelectedItem().toString()
            );

          
            if (nv_dao.capNhatNhanVien(nv)) {

                String ngaySinh = "";
                if (nv.getNgaySinh() != null) {
                    ngaySinh = new SimpleDateFormat("dd/MM/yyyy")
                            .format(nv.getNgaySinh());
                }
                
                String trangThai = cbTrangThai.getSelectedItem().toString();

             //  nếu chuyển sang nghỉ việc
                if (trangThai.equalsIgnoreCase("Nghỉ việc")) {

                	String lyDo = JOptionPane.showInputDialog(this, "Nhập lý do nghỉ việc:");

                if (lyDo == null || lyDo.trim().isEmpty()) {
                     JOptionPane.showMessageDialog(this, "Phải nhập lý do!");
                     return;
                 }

                 lyDoMap.put(txtMaNV.getText(), lyDo);
                 saveLyDoToFile();
             }

                // UPDATE TABLE (ĐÚNG INDEX)
                tableModel.setValueAt(nv.getMaNV(), row, 0);
                tableModel.setValueAt(nv.getHoTen(), row, 1);
                tableModel.setValueAt(nv.isGioiTinh() ? "Nam" : "Nữ", row, 2);
                tableModel.setValueAt(ngaySinh, row, 3);
                tableModel.setValueAt(nv.getSdt(), row, 4);
                tableModel.setValueAt(nv.getCccd(), row, 5);
                tableModel.setValueAt(nv.getEmail(), row, 6);
                tableModel.setValueAt(nv.getChucVu(), row, 7);
                tableModel.setValueAt(nv.getTrangThai(), row, 8);
                
                loadData();

                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();

            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu!");
        }
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
        lblAnh.setIcon(null);
        lblAnh.setText("");
        lblLyDo.setText("");
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

        NhanVien nv = dsNV.get(row); 
        duongDanAnh=nv.getAnhNhanVien();

    

        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());

        if (nv.isGioiTinh()) rdNam.setSelected(true);
        else rdNu.setSelected(true);

        txtNgaySinh.setDate(nv.getNgaySinh());

        txtSDT.setText(nv.getSdt());
        txtCCCD.setText(nv.getCccd());
        txtEmail.setText(nv.getEmail());

        cbChucVu.setSelectedItem(nv.getChucVu());
        cbTrangThai.setSelectedItem(nv.getTrangThai());
        
        String maNV = txtMaNV.getText();

        if ("Nghỉ việc".equalsIgnoreCase(cbTrangThai.getSelectedItem().toString())) {
            String lyDo = lyDoMap.get(maNV);

            if (lyDo != null) {
                lblLyDo.setText("Lý do: " + lyDo);
                lblLyDo.setForeground(Color.RED);
            } else {
                lblLyDo.setText("");
            }
        } else {
            lblLyDo.setText("");
        }

        //  LOAD ẢNH
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
                System.out.println("Không tồn tại: " + fullPath);
            }
        } else {
            lblAnh.setIcon(null);
        }
    }

//    private void chonAnh() {
//        JFileChooser fc = new JFileChooser();
//
//        int result = fc.showOpenDialog(this);
//
//        if (result == JFileChooser.APPROVE_OPTION) {
//            try {
//                File file = fc.getSelectedFile();
//                duongDanAnh = file.getAbsolutePath(); // 🔥 lưu đường dẫn
//
//                ImageIcon icon = new ImageIcon(duongDanAnh);
//                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
//
//                lblAnh.setIcon(new ImageIcon(img));
//                lblAnh.setText("");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    
    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")+"/img"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                // thư mục img trong project
                String projectPath = System.getProperty("user.dir");
                File folder = new File(projectPath + "/img");
                if (!folder.exists()) folder.mkdirs();

                // tạo tên file (tránh trùng)
                String fileName = System.currentTimeMillis() + "_" + file.getName();

                File dest = new File(folder, fileName);

                // copy ảnh vào project
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // chỉ lưu tên file
                duongDanAnh = fileName;

                // hiển thị ảnh
                ImageIcon icon = new ImageIcon(dest.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));

            } catch (Exception e) {
                e.printStackTrace();
            }
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
    
    
    
    private void loadData() {
        dsNV = nv_dao.getAllNhanVien();

        tableModel.setRowCount(0);

        for (NhanVien nv : dsNV) {
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";

            String ngaySinh = "";
            if (nv.getNgaySinh() != null) {
                ngaySinh = new SimpleDateFormat("dd/MM/yyyy")
                        .format(nv.getNgaySinh());
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
                    nv.getTrangThai() 
            });
        }
    }
    
    private void saveLyDoToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_LYDO))) {
            for (String maNV : lyDoMap.keySet()) {
                bw.write(maNV + "|" + lyDoMap.get(maNV));
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadLyDoFromFile() {
        lyDoMap.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_LYDO))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    lyDoMap.put(parts[0], parts[1]);
                }
            }
        } catch (Exception e) {
            // file chưa tồn tại → bỏ qua
        }
    }
    

//     8. MAIN – demo độc lập (xoá khi ghép vào JFrame chính)
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale", "auto");

    	UIManager.put("Label.font",   scaledFontStatic("SansSerif", Font.PLAIN, 12));
    	UIManager.put("Button.font",  scaledFontStatic("SansSerif", Font.BOLD, 12));
    	UIManager.put("TextField.font", scaledFontStatic("SansSerif", Font.PLAIN, 13));
    	UIManager.put("Table.font",   scaledFontStatic("SansSerif", Font.PLAIN, 12));
    	UIManager.put("TableHeader.font", scaledFontStatic("SansSerif", Font.BOLD, 12));
    	UIManager.put("ComboBox.font", scaledFontStatic("SansSerif", Font.PLAIN, 12));
    	UIManager.put("ComboBox.listFont", scaledFontStatic("SansSerif", Font.PLAIN, 12));
    	UIManager.put("RadioButton.font", scaledFontStatic("SansSerif", Font.PLAIN, 13));
    	
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
            mainPanel.setVisible(true);
//            Pn_ThanhMenu   menuPanel = new Pn_ThanhMenu();
//            NhanVien_GUI mainPanel = new NhanVien_GUI(null);
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