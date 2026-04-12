package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import digLog.DoiMatKhau;
import entity.NhanVien;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.List;

public class TaiKhoan_GUI extends JFrame {

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
	private List<NhanVien> dsNV;
	private List<TaiKhoan> dsTK;

    
    
    public TaiKhoan_GUI() {
        setTitle("Quản Lý Tài Khoản");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        Pn_ThanhMenu menu = new Pn_ThanhMenu(null);

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
    }

    // =========================================================================
    // 1. TIÊU ĐỀ
    // =========================================================================
    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lbl.setFont(scaledFont("SansSerif", Font.BOLD, 26));
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
    
    private static final Dimension FIELD_SIZE =
            new Dimension((int)(320 * SCALE), (int)(32 * SCALE));
    
    
    
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
        gbc.weightx = 1;
        gbc.ipady = 0;
        gbc.insets = new Insets(6 ,6 ,6 ,6 );
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Mã đăng nhập (disabled) / Vai trò
        txtMaDangNhap = createTextField();
        txtMaDangNhap.setEnabled(false);
        txtMaDangNhap.setDisabledTextColor(new Color(60, 60, 60));
        cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Lễ tân"});
        styleComboBox(cbVaiTro);
        addRow(pnlFields, gbc, 0, "Mã tài khoản", txtMaDangNhap, "Vai trò", cbVaiTro);

        // Hàng 1: Tên đăng nhập / Nhân viên
        txtTenDangNhap = createTextField();
        txtTenDangNhap.setEditable(false);
        cbNhanVien = new JComboBox<>();
        styleComboBox(cbNhanVien);
        cbNhanVien.addItem("--Chọn nhân viên ---");
        dsNV= nv_dao.getTenNhanVien();
        for (NhanVien nv : dsNV) {
			cbNhanVien.addItem(nv.getHoTen());
		}
        
        cbNhanVien.addActionListener(e -> {
            int index = cbNhanVien.getSelectedIndex();

            if (index > 0) {
                NhanVien nv = dsNV.get(index - 1);

                //tên đăng nhập = mã NV
                txtTenDangNhap.setText(nv.getMaNV());

                //load ảnh luôn
                loadAnhNhanVien(nv.getAnhNhanVien());
            }
        });
        addRow(pnlFields, gbc, 1, "Tên đăng nhập", txtTenDangNhap, "Nhân viên:", cbNhanVien);

        // Hàng 2: Mật khẩu / Trạng thái
        txtMatKhau = new JPasswordField();
//        txtMatKhau.setEnabled(false);
        txtMatKhau.setPreferredSize(FIELD_SIZE);
        txtMatKhau.setMinimumSize(FIELD_SIZE);
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
//                BorderFactory.createEmptyBorder(6, 6, 6, 6)
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
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
        

        
        cbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Khóa"});
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
        	    (int)(140 * SCALE)
        	));
        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setVerticalAlignment(SwingConstants.CENTER);

        pnlAnh.add(lblTitle, BorderLayout.NORTH);
        pnlAnh.add(lblAnh,   BorderLayout.CENTER);
        outer.add(pnlAnh, BorderLayout.EAST);

        return outer;
    }
    
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
        btnCapNhatMK.addActionListener(e -> moDoiMatKhau());

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
        
        
        table.getColumnModel().getColumn(5).setCellRenderer(
        	    new DefaultTableCellRenderer() {
        	        @Override
        	        public Component getTableCellRendererComponent(JTable table, Object value,
        	                boolean isSelected, boolean hasFocus, int row, int column) {

        	            Component c = super.getTableCellRendererComponent(
        	                    table, value, isSelected, hasFocus, row, column);

        	            String trangThai = value.toString();

        	            // 🔥 ƯU TIÊN selection trước
        	            if (isSelected) {
        	                c.setBackground(table.getSelectionBackground());
        	                c.setForeground(table.getSelectionForeground());
        	            } else {
        	                if (trangThai.equalsIgnoreCase("Khóa")) {
        	                    c.setBackground(new Color(255, 200, 200));
        	                } else {
        	                    c.setBackground(Color.WHITE);
        	                }
        	                c.setForeground(Color.BLACK);
        	            }

        	            return c;
        	        }
        	    }
        	);

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

        if (cbNhanVien.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Phải chọn nhân viên!");
            return;
        }

        String tenDangNhap = txtTenDangNhap.getText().trim();

        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được rỗng!");
            return;
        }

        //  CHECK TRÙNG USERNAME
        for (TaiKhoan tk : dsTK) {
            if (tk.getTenDangNhap().equalsIgnoreCase(tenDangNhap)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
                lamMoi();
                return;
            }
        }

        // mật khẩu
        String matKhau = new String(txtMatKhau.getPassword()).trim();
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phải nhập mật khẩu!");
            return;
        }

        int index = cbNhanVien.getSelectedIndex() - 1;
        NhanVien nv = dsNV.get(index);

        String maTK = taoMaTK();

        TaiKhoan tk = new TaiKhoan(
                maTK,
                tenDangNhap,
                matKhau,
                cbVaiTro.getSelectedItem().toString(),
                cbTrangThai.getSelectedItem().toString().equals("Hoạt động"),
                nv
        );

        if (tk_dao.themTaiKhoan(tk)) {
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
            lamMoi();
        }
    }

    private void capNhatTaiKhoan() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật!");
            return;
        }

        try {
            // lấy tài khoản cũ
            TaiKhoan tkCu = dsTK.get(row);

            // chỉ sửa vai trò + trạng thái (đúng nghiệp vụ)
            String vaiTro = cbVaiTro.getSelectedItem().toString();
            boolean trangThai = cbTrangThai.getSelectedItem().toString().equals("Hoạt động");

            TaiKhoan tkMoi = new TaiKhoan(
                    tkCu.getMaTaiKhoan(),
                    tkCu.getTenDangNhap(),
                    tkCu.getMatKhau(),
                    vaiTro,
                    trangThai,
                    tkCu.getMaNV()
            );

            if (tk_dao.capNhatTaiKhoan(tkMoi)) {

                // reload giống NhanVien_GUI
                loadData();

                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoi();

            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaTaiKhoan() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần xóa!");
            return;
        }

        // lấy tài khoản đang chọn
        TaiKhoan tk = dsTK.get(row);
        String maTK = tk.getMaTaiKhoan();

        // ===== NGHIỆP VỤ 1: không cho xóa chính mình =====
        // 👉 bạn cần có biến lưu tài khoản đang đăng nhập
        // ví dụ:
//        String maTKDangNhap = TaiKhoan_DangNhap.getMaTaiKhoan(); // 🔥 bạn thay theo project của bạn

//        if (maTK.equals(maTKDangNhap)) {
//            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản đang đăng nhập!");
//            return;
//        }

        // ===== NGHIỆP VỤ 2: phải còn ít nhất 1 quản lý =====
        long soLuongQuanLy = dsTK.stream()
                .filter(t -> t.getPhanQuyen().equalsIgnoreCase("Quản lý"))
                .count();

        if (soLuongQuanLy == 1 && tk.getPhanQuyen().equalsIgnoreCase("Quản lý")) {
            JOptionPane.showMessageDialog(this, "Phải có ít nhất 1 tài khoản quản lý!");
            return;
        }

        // ===== XÁC NHẬN =====
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tài khoản này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // ===== GỌI DAO =====
        if (tk_dao.xoaTaiKhoan(maTK)) {

            loadData(); // 🔥 reload lại

            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();

        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại!");
        }
    }

    private void moDoiMatKhau() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần đổi mật khẩu!");
            return;
        }

        // lấy mã nhân viên từ object (chuẩn hơn lấy từ table)
        TaiKhoan tk = dsTK.get(row);
        String maNV = tk.getMaNV().getMaNV();

        // mở dialog
        DoiMatKhau dialog = new DoiMatKhau(this, maNV);
       
        dialog.setVisible(true);
        
        loadData();
    }

    private void lamMoi() {
        txtMaDangNhap.setText("");
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");

        txtTenDangNhap.setEnabled(true);
        txtMatKhau.setEnabled(true);
        cbNhanVien.setEnabled(true);

        cbVaiTro.setSelectedIndex(0);
        cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);

        lblAnh.setIcon(null);
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

        txtTenDangNhap.setEnabled(false);
        txtMatKhau.setEnabled(false);
        cbNhanVien.setEnabled(false);

        cbNhanVien.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5).toString());

        // 🔥 THÊM ĐOẠN NÀY
        TaiKhoan tk = dsTK.get(row);
        NhanVien nv = tk.getMaNV();

        loadAnhNhanVien(nv.getAnhNhanVien());
    }
    
    private void loadData() {
        dsTK = tk_dao.getAllTaiKhoan();

        tableModel.setRowCount(0); // xoá dữ liệu cũ

        for (TaiKhoan tk : dsTK) {
            tableModel.addRow(new Object[]{
                    tk.getMaTaiKhoan(),
                    tk.getTenDangNhap(),
                    tk.getPhanQuyen(),
                    tk.getMatKhau(),
                    tk.getMaNV().getHoTen(), 
                    tk.isTrangThai() ? "Hoạt động" : "Khóa"
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
    
    
    
    private String taoMaTK() {
        int max = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ma = tableModel.getValueAt(i, 0).toString();
            int num = Integer.parseInt(ma.substring(2));
            if (num > max) max = num;
        }

        return String.format("TK%02d", max + 1);
    }

    
    private void loadAnhNhanVien(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            String path = System.getProperty("user.dir") + "/img/" + fileName;

            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);

            lblAnh.setIcon(new ImageIcon(img));
        } else {
            lblAnh.setIcon(null);
        }
    }
    
    
    private JTextField createTextField() {
        JTextField tf = new JTextField();
//        tf.setPreferredSize(new Dimension(0, (int)(38 * SCALE)));
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
    
    

    // =========================================================================
    // 8. MAIN
    // =========================================================================
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.setProperty("sun.java2d.uiScale", "auto");

                UIManager.put("Label.font",         scaledFont("SansSerif", Font.PLAIN, 12));
                UIManager.put("Button.font",        scaledFont("SansSerif", Font.BOLD, 12));
                UIManager.put("TextField.font",     scaledFont("SansSerif", Font.PLAIN, 12));
                UIManager.put("PasswordField.font", scaledFont("SansSerif", Font.PLAIN, 12));
                UIManager.put("Table.font",         scaledFont("SansSerif", Font.PLAIN, 12));
                UIManager.put("TableHeader.font",   scaledFont("SansSerif", Font.BOLD, 12));
                UIManager.put("ComboBox.font",      scaledFont("SansSerif", Font.PLAIN, 12));

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                ConnectDB.getInstance().connect();

                new TaiKhoan_GUI().setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}