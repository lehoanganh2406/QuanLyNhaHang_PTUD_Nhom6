package gui;

import javax.swing.*;
import javax.swing.table.*;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import digLog.DoiMatKhau;
import entity.NhanVien;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.List;

public class TaiKhoan_GUI extends JFrame {

    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(245, 242, 235);
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222);
    private static final Color CLR_BTN_ADD    = new Color(102, 187, 106);
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);
    private static final Color CLR_BTN_RESET  = new Color(255, 255, 255);
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246);
    private static final Color CLR_BTN_DELETE = new Color(255, 138, 101);
    private static final Color CLR_BTN_CHPWD  = new Color(206, 147, 216);
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175);
    private static final Color CLR_BORDER     = new Color(160, 145, 120);

    private static final Dimension FIELD_SIZE = new Dimension(220, 36);

    private JTextField     txtMaDangNhap, txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbVaiTro, cbNhanVien, cbTrangThai;
    private JLabel lblAnh;

    private JButton btnTogglePwd;
    private boolean isShowPassword = false;

    private JTable            table;
    private DefaultTableModel tableModel;

    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu, btnXoa, btnCapNhatMK;
    private Connection con;
    private TaiKhoan_DAO tk_dao = new TaiKhoan_DAO();
    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private List<NhanVien> dsNV;
    private List<TaiKhoan> dsTK;

    public TaiKhoan_GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Quản Lý Tài Khoản");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        Pn_ThanhMenu menu = new Pn_ThanhMenu(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CLR_PANEL_BG);
        mainPanel.add(buildTitlePanel(),  BorderLayout.NORTH);
        mainPanel.add(buildCenterPanel(), BorderLayout.CENTER);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(menu,      JLayeredPane.PALETTE_LAYER);

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

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 26));
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
        topArea.add(buildFormPanel(),   BorderLayout.NORTH);
        topArea.add(buildButtonPanel(), BorderLayout.CENTER);

        pnl.add(topArea,           BorderLayout.NORTH);
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
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Mã tài khoản / Vai trò
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
        cbNhanVien.addItem("--Chọn nhân viên---");
        dsNV = nv_dao.getTenNhanVien();
        for (NhanVien nv : dsNV) cbNhanVien.addItem(nv.getHoTen());

        cbNhanVien.addActionListener(e -> {
            int index = cbNhanVien.getSelectedIndex();
            if (index > 0) {
                NhanVien nv = dsNV.get(index - 1);
                txtTenDangNhap.setText(nv.getMaNV());
                loadAnhNhanVien(nv.getAnhNhanVien());
            }
        });
        addRow(pnlFields, gbc, 1, "Tên đăng nhập", txtTenDangNhap, "Nhân viên:", cbNhanVien);

        // Hàng 2: Mật khẩu / Trạng thái
        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 15));
        txtMatKhau.setPreferredSize(FIELD_SIZE);
        txtMatKhau.setMinimumSize(FIELD_SIZE);
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
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
        btnTogglePwd.setPreferredSize(new Dimension(32, 32));

        pnlPwd.add(txtMatKhau,   BorderLayout.CENTER);
        pnlPwd.add(btnTogglePwd, BorderLayout.EAST);

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

        // Ảnh nhân viên
        JPanel pnlAnh = new JPanel(new BorderLayout(0, 4));
        pnlAnh.setOpaque(false);
        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JLabel lblTitle = new JLabel("Ảnh nhân viên", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));

        lblAnh = new JLabel();
        lblAnh.setPreferredSize(new Dimension(140, 140));
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
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        p.add(createLabel(lbl1), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(c1, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        p.add(createLabel(lbl2), gbc);

        gbc.gridx = 3; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(c2, gbc);
    }

    private JPanel buildButtonPanel() {
        // 👉 spacing hợp lý hơn
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        btnThem      = createFuncButton("Thêm",              CLR_BTN_ADD,    "img/cn_them.png");
        btnCapNhat   = createFuncButton("Cập nhật",          CLR_BTN_UPDATE, "img/cn_capnhat.png");
        btnLamMoi    = createFuncButton("Làm mới",           CLR_BTN_RESET,  "img/mn_xuly.png");
        btnTraCuu    = createFuncButton("Tra cứu",           CLR_BTN_SEARCH, "img/mn_tracuu.png");
        btnXoa       = createFuncButton("Xóa",               CLR_BTN_DELETE, "img/cn_xoa.png");
        btnCapNhatMK = createFuncButton("Cập nhật mật khẩu", CLR_BTN_CHPWD,  "img/cn_capnhatmk.png");

        // 👉 fix kích thước tối thiểu (QUAN TRỌNG)
        Dimension btnSize = new Dimension(150, 40);

        btnThem.setPreferredSize(btnSize);
        btnCapNhat.setPreferredSize(btnSize);
        btnLamMoi.setPreferredSize(btnSize);
        btnTraCuu.setPreferredSize(btnSize);
        btnXoa.setPreferredSize(btnSize);
        btnCapNhatMK.setPreferredSize(new Dimension(200, 40)); // nút dài hơn

        pnl.add(btnThem);
        pnl.add(btnCapNhat);
        pnl.add(btnLamMoi);
        pnl.add(btnTraCuu);
        pnl.add(btnXoa);
        pnl.add(btnCapNhatMK);

        // action
        btnThem.addActionListener(e      -> themTaiKhoan());
        btnCapNhat.addActionListener(e   -> capNhatTaiKhoan());
        btnLamMoi.addActionListener(e    -> lamMoi());
        btnTraCuu.addActionListener(e    -> traCuu());
        btnXoa.addActionListener(e       -> xoaTaiKhoan());
        btnCapNhatMK.addActionListener(e -> moDoiMatKhau());

        return pnl;
    }

    private JScrollPane buildTablePanel() {
        String[] cols = {
            "Mã tài khoản", "Tên đăng nhập", "Vai trò",
            "Mật khẩu", "Nhân viên", "Trạng thái"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(CLR_BORDER);
        table.setSelectionBackground(new Color(180, 210, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        // Renderer cột Trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground("Khóa".equalsIgnoreCase(value == null ? "" : value.toString())
                            ? new Color(255, 200, 200) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        // Renderer cột Mật khẩu (ẩn)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                String masked = value == null ? "" : "•".repeat(value.toString().length());
                return super.getTableCellRendererComponent(t, masked, isSelected, hasFocus, row, col);
            }
        });

        // Header renderer
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                c.setBackground(CLR_TABLE_HDR);
                c.setForeground(new Color(50, 40, 30));
                c.setFont(new Font("Arial", Font.BOLD, 15));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        int[] widths = {120, 130, 100, 100, 120, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        return scroll;
    }

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
        for (TaiKhoan tk : dsTK) {
            if (tk.getTenDangNhap().equalsIgnoreCase(tenDangNhap)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
                lamMoi();
                return;
            }
        }
        String matKhau = new String(txtMatKhau.getPassword()).trim();
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phải nhập mật khẩu!");
            return;
        }
        NhanVien nv = dsNV.get(cbNhanVien.getSelectedIndex() - 1);
        String maTK = taoMaTK();
        TaiKhoan tk = new TaiKhoan(
                maTK, tenDangNhap, matKhau,
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
            TaiKhoan tkCu = dsTK.get(row);
            TaiKhoan tkMoi = new TaiKhoan(
                    tkCu.getMaTaiKhoan(), tkCu.getTenDangNhap(), tkCu.getMatKhau(),
                    cbVaiTro.getSelectedItem().toString(),
                    cbTrangThai.getSelectedItem().toString().equals("Hoạt động"),
                    tkCu.getMaNV()
            );
            if (tk_dao.capNhatTaiKhoan(tkMoi)) {
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
        TaiKhoan tk = dsTK.get(row);
        long soQuanLy = dsTK.stream()
                .filter(t -> t.getPhanQuyen().equalsIgnoreCase("Quản lý")).count();
        if (soQuanLy == 1 && tk.getPhanQuyen().equalsIgnoreCase("Quản lý")) {
            JOptionPane.showMessageDialog(this, "Phải có ít nhất 1 tài khoản quản lý!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (tk_dao.xoaTaiKhoan(tk.getMaTaiKhoan())) {
            loadData();
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
        String maNV = dsTK.get(row).getMaNV().getMaNV();
        new DoiMatKhau(this, maNV).setVisible(true);
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
        cbVaiTro.setEnabled(true);
        cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        lblAnh.setIcon(null);
        table.clearSelection();
        loadData();
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(this,
                "Nhập từ khóa tìm kiếm:", "Tra cứu", JOptionPane.PLAIN_MESSAGE);
        if (keyword == null || keyword.trim().isEmpty()) return;

        String kw = keyword.trim().toLowerCase();
        tableModel.setRowCount(0);
        boolean found = false;

        for (TaiKhoan tk : dsTK) {
            boolean match = tk.getMaTaiKhoan().toLowerCase().contains(kw)
                    || tk.getTenDangNhap().toLowerCase().contains(kw)
                    || tk.getPhanQuyen().toLowerCase().contains(kw)
                    || tk.getMaNV().getHoTen().toLowerCase().contains(kw)
                    || (tk.isTrangThai() ? "hoạt động" : "khóa").contains(kw);
            if (match) {
                tableModel.addRow(new Object[]{
                        tk.getMaTaiKhoan(), tk.getTenDangNhap(), tk.getPhanQuyen(),
                        tk.getMatKhau(), tk.getMaNV().getHoTen(),
                        tk.isTrangThai() ? "Hoạt động" : "Khóa"
                });
                found = true;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!");
            loadData();
        }
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
        cbVaiTro.setEnabled(false);

        cbNhanVien.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5).toString());

        TaiKhoan tk = dsTK.get(row);
        loadAnhNhanVien(tk.getMaNV().getAnhNhanVien());
    }

    private void loadData() {
        dsTK = tk_dao.getAllTaiKhoan();
        tableModel.setRowCount(0);
        for (TaiKhoan tk : dsTK) {
            tableModel.addRow(new Object[]{
                    tk.getMaTaiKhoan(), tk.getTenDangNhap(), tk.getPhanQuyen(),
                    tk.getMatKhau(), tk.getMaNV().getHoTen(),
                    tk.isTrangThai() ? "Hoạt động" : "Khóa"
            });
        }
    }

    private void loadAnhNhanVien(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            String path = System.getProperty("user.dir") + "/img/" + fileName;
            Image img = new ImageIcon(path).getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            lblAnh.setIcon(new ImageIcon(img));
        } else {
            lblAnh.setIcon(null);
        }
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

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 15));
        tf.setPreferredSize(FIELD_SIZE);
        tf.setMinimumSize(FIELD_SIZE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("Arial", Font.PLAIN, 15));
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
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
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
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        // ✅ LUÔN LUÔN MÀU ĐEN
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(Color.BLACK);

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ✅ padding lớn hơn → nút to theo chữ
        btn.setMargin(new Insets(8, 20, 8, 20));

        // ✅ auto size theo nội dung
        btn.setPreferredSize(null);

        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18, 18));
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setIconTextGap(8);
        }

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ConnectDB.getInstance().connect();
                new TaiKhoan_GUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}