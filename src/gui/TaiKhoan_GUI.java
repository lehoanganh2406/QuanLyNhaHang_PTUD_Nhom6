package gui;

import javax.swing.*;


import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;

import javax.swing.table.*;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import digLog.DoiMatKhau;
import entity.NhanVien;
import entity.TaiKhoan;
import util.PasswordUtil;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.List;

public class TaiKhoan_GUI extends JFrame {



    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(250, 247, 240);
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222);
    private static final Color CLR_BTN_ADD    = new Color(102, 187, 106);
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);
    private static final Color CLR_BTN_RESET  = Color.WHITE;
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246);
    private static final Color CLR_BTN_DELETE = new Color(255, 138, 101);
    private static final Color CLR_BTN_CHPWD  = new Color(206, 147, 216);
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175);
    private static final Color CLR_BORDER     = new Color(170, 155, 130);



    private static final double SCALE = calcScale();

    private static double calcScale() {
        try {
            AffineTransform at = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration()
                    .getDefaultTransform();

            double raw = at.getScaleX();
            if (raw >= 1.5) return 1.0;

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            double byWidth = screen.width / 1440.0;
            return Math.max(0.9, Math.min(1.1, byWidth));
        } catch (Exception e) {
            return 1.0;
        }
    }

    private static int sc(int v) {
        return (int) Math.round(v * SCALE);
    }

    private static Font f(String name, int style, int size) {
        return new Font(name, style, sc(size));
    }

    private JTextField txtMaDangNhap, txtTenDangNhap;

    private JPasswordField txtMatKhau;
    private JComboBox<String> cbVaiTro, cbNhanVien, cbTrangThai;
    private JLabel lblAnh;

    private JButton btnTogglePwd;
    private boolean isShowPassword = false;



    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu, btnXoa, btnCapNhatMK;

    private Connection con;
    private final TaiKhoan_DAO tk_dao = new TaiKhoan_DAO();
    private final NhanVien_DAO nv_dao = new NhanVien_DAO();
    private List<NhanVien> dsNV;
    private List<TaiKhoan> dsTK;

	private TableCellRenderer headerRenderer;


    private static final Dimension FIELD_SIZE = new Dimension(sc(330), sc(45));
    public TaiKhoan_GUI(TaiKhoan tk) {
        setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);


        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);

        con = ConnectDB.getConnection();
        loadData();
    }



    public TaiKhoan_GUI() {
        this(null);
    }

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(new EmptyBorder(sc(16), sc(10), sc(16), sc(10)));

        JLabel lbl = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);


        lbl.setFont(f("SansSerif", Font.BOLD, 34));

        lbl.setForeground(CLR_HEADER_FG);

        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel buildCenterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, sc(10)));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(new EmptyBorder(sc(12), sc(14), sc(12), sc(14)));

        JPanel topArea = new JPanel(new BorderLayout(0, sc(8)));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(), BorderLayout.NORTH);
        topArea.add(buildButtonPanel(), BorderLayout.CENTER);

        pnl.add(topArea, BorderLayout.NORTH);
        pnl.add(buildTablePanel(), BorderLayout.CENTER);

        return pnl;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(sc(12), 0));
        outer.setBackground(CLR_FORM_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CLR_BORDER, 1),
                new EmptyBorder(sc(12), sc(22), sc(12), sc(18))
        ));

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);



        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(sc(6), sc(8), sc(6), sc(10));
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaDangNhap = createTextField();
        txtMaDangNhap.setEnabled(false);
        txtMaDangNhap.setDisabledTextColor(new Color(70, 70, 70));

        cbVaiTro = createRoundedComboBox(new String[]{"Quản lý", "Lễ tân"});
        cbVaiTro.setEnabled(false);
        addRow(pnlFields, gbc, 0, "Mã tài khoản", txtMaDangNhap, "Vai trò", cbVaiTro);

        txtTenDangNhap = createTextField();
//        txtTenDangNhap.setEditable(false);
        txtTenDangNhap.setEditable(true);



        cbNhanVien = createRoundedComboBox(new String[]{});
        cbNhanVien.addItem("--Chọn nhân viên---");


        dsNV = nv_dao.getNhanVienDayDu();
        for (NhanVien nv : dsNV) cbNhanVien.addItem(nv.getHoTen());

//        cbNhanVien.addActionListener(e -> {
//            int index = cbNhanVien.getSelectedIndex();
//            if (index > 0) {
//                NhanVien nv = dsNV.get(index - 1);
//                txtTenDangNhap.setText(nv.getMaNV());
//                loadAnhNhanVien(nv.getAnhNhanVien());
//            }
//        });
        cbNhanVien.addActionListener(e -> {
            int index = cbNhanVien.getSelectedIndex();

            if (index <= 0) {
                txtTenDangNhap.setText("");
                cbVaiTro.setSelectedIndex(0);
                lblAnh.setIcon(null);
                lblAnh.setText("Chưa có ảnh");
                return;
            }

            NhanVien nv = dsNV.get(index - 1);

            txtTenDangNhap.setText(nv.getMaNV());
            txtTenDangNhap.setEditable(false);

            String chucVu = nv.getChucVu();
            if (chucVu != null && chucVu.trim().equalsIgnoreCase("Quản lý")) {
                cbVaiTro.setSelectedItem("Quản lý");
            } else {
                cbVaiTro.setSelectedItem("Lễ tân");
            }

            loadAnhNhanVien(nv.getAnhNhanVien());
        });

        addRow(pnlFields, gbc, 1, "Tên đăng nhập", txtTenDangNhap, "Nhân viên", cbNhanVien);

        txtMatKhau = new JPasswordField();


        txtMatKhau.setEchoChar('•');
        txtMatKhau.setFont(f("SansSerif", Font.PLAIN, 14));
        txtMatKhau.setBorder(new EmptyBorder(0, sc(12), 0, sc(4)));
        txtMatKhau.setOpaque(false);

        btnTogglePwd = new JButton(loadIcon("img/Dn_eye_off.png", sc(17), sc(17)));
        btnTogglePwd.setFocusPainted(false);
        btnTogglePwd.setBorder(null);
        btnTogglePwd.setContentAreaFilled(false);
        btnTogglePwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        btnTogglePwd.setPreferredSize(new Dimension(sc(38), sc(36)));

        JPanel pnlPwd = new RoundedFieldPanel(new BorderLayout());
        pnlPwd.setPreferredSize(FIELD_SIZE);
        pnlPwd.setMinimumSize(FIELD_SIZE);
        pnlPwd.add(txtMatKhau, BorderLayout.CENTER);

        pnlPwd.add(btnTogglePwd, BorderLayout.EAST);

        btnTogglePwd.addActionListener(e -> {
            if (isShowPassword) {
                txtMatKhau.setEchoChar('•');
                btnTogglePwd.setIcon(loadIcon("img/Dn_eye_off.png", sc(17), sc(17)));
            } else {
                txtMatKhau.setEchoChar((char) 0);
                btnTogglePwd.setIcon(loadIcon("img/Dn_eye_open.png", sc(17), sc(17)));
            }
            isShowPassword = !isShowPassword;
        });



        cbTrangThai = createRoundedComboBox(new String[]{"Hoạt động", "Khóa"});
        addRow(pnlFields, gbc, 2, "Mật khẩu", pnlPwd, "Trạng thái", cbTrangThai);

        outer.add(pnlFields, BorderLayout.CENTER);
        outer.add(buildImagePanel(), BorderLayout.EAST);


        return outer;
    }



    private JPanel buildImagePanel() {
        JPanel pnlAnh = new JPanel();
        pnlAnh.setLayout(new BoxLayout(pnlAnh, BoxLayout.Y_AXIS));
        pnlAnh.setOpaque(false);
        pnlAnh.setPreferredSize(new Dimension(sc(170), 0));
        pnlAnh.setBorder(new EmptyBorder(0, sc(8), 0, 0));

        JLabel lblTitle = new JLabel("Ảnh nhân viên", SwingConstants.CENTER);
        lblTitle.setFont(f("SansSerif", Font.BOLD, 14));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(sc(130), sc(145)));
        lblAnh.setMinimumSize(new Dimension(sc(130), sc(145)));
        lblAnh.setMaximumSize(new Dimension(sc(130), sc(145)));
        lblAnh.setFont(f("SansSerif", Font.PLAIN, 12));
        lblAnh.setForeground(new Color(120, 110, 95));
        lblAnh.setOpaque(true);
        lblAnh.setBackground(new Color(255, 253, 248));
        lblAnh.setBorder(new LineBorder(CLR_BORDER, 1));
        lblAnh.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlAnh.add(lblTitle);
        pnlAnh.add(Box.createVerticalStrut(sc(6)));
        pnlAnh.add(lblAnh);

        return pnlAnh;
    }
    
    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String lbl1, JComponent c1,
                        String lbl2, JComponent c2) {

        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        p.add(createLabel(lbl1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.50;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(c1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        p.add(createLabel(lbl2), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.50;


        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(c2, gbc);
    }

    private JPanel buildButtonPanel() {


        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, sc(20), 0));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(new EmptyBorder(sc(4), sc(22), sc(4), 0));

        btnThem      = createFuncButton("+ Thêm", CLR_BTN_ADD, null);
        btnCapNhat   = createFuncButton("Cập nhật", CLR_BTN_UPDATE, "img/cn_capnhat.png");
        btnLamMoi    = createFuncButton("Làm mới", CLR_BTN_RESET, "img/mn_xuly.png");
        btnTraCuu    = createFuncButton("Tra cứu", CLR_BTN_SEARCH, "img/mn_tracuu.png");
        btnXoa       = createFuncButton("Xóa", CLR_BTN_DELETE, null);
        btnCapNhatMK = createFuncButton("Đổi mật khẩu", CLR_BTN_CHPWD, null);

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



        btnThem.addActionListener(e -> themTaiKhoan());
        btnCapNhat.addActionListener(e -> capNhatTaiKhoan());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTraCuu.addActionListener(e -> traCuu());
        btnXoa.addActionListener(e -> xoaTaiKhoan());

        btnCapNhatMK.addActionListener(e -> moDoiMatKhau());

        return pnl;
    }

    private JScrollPane buildTablePanel() {
        String[] cols = {
                "Mã tài khoản", "Tên đăng nhập", "Vai trò",
                "Mật khẩu", "Nhân viên", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);


        table.setFont(f("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(sc(32));

        table.setShowGrid(true);
        table.setGridColor(CLR_BORDER);
        table.setSelectionBackground(new Color(190, 220, 245));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setFont(f("SansSerif", Font.BOLD, 14));
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setPreferredSize(new Dimension(0, sc(34)));

        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }



        int[] widths = {130, 160, 120, 150, 220, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);

                c.setBorder(new EmptyBorder(0, sc(8), 0, sc(8)));
                c.setHorizontalAlignment(col == 4 ? SwingConstants.LEFT : SwingConstants.CENTER);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 247, 240));
                    c.setForeground(Color.BLACK);

                    if (col == 5 && value != null && value.toString().equalsIgnoreCase("Khóa")) {
                        c.setBackground(new Color(255, 220, 220));
                    }
                }

                if (col == 3) {
                    String masked = value == null ? "" : "•".repeat(value.toString().length());
                    c.setText(masked);
                }

                return c;
            }
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(CLR_BORDER, 1));
        scroll.getViewport().setBackground(Color.WHITE);
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



            String vaiTro = cbVaiTro.getSelectedItem().toString();
            boolean trangThai = cbTrangThai.getSelectedItem().toString().equals("Hoạt động");


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
        String maTK = tk.getMaTaiKhoan();

        long soLuongQuanLy = dsTK.stream()
                .filter(t -> t.getPhanQuyen().equalsIgnoreCase("Quản lý"))
                .count();

        if (soLuongQuanLy == 1 && tk.getPhanQuyen().equalsIgnoreCase("Quản lý")) {
            JOptionPane.showMessageDialog(this, "Phải có ít nhất 1 tài khoản quản lý!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tài khoản này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (tk_dao.xoaTaiKhoan(maTK)) {

            loadData();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại!");
        }
    }

//    private void moDoiMatKhau() {
//        int row = table.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần đổi mật khẩu!");
//            return;
//        }
//        TaiKhoan tk = dsTK.get(row);
//        String maNV = tk.getMaNV().getMaNV();
//        Window w = SwingUtilities.getWindowAncestor(this);
//        if (w instanceof JFrame) {
//            DoiMatKhau dialog = new DoiMatKhau((JFrame) w, maNV);
//            dialog.setLocationRelativeTo(w);
//            dialog.setVisible(true);
//        }
//        loadData();
//    }
    
    private void moDoiMatKhau() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chọn tài khoản cần đổi mật khẩu!"
            );
            return;
        }
        TaiKhoan tk = dsTK.get(row);
        String maTaiKhoan = tk.getMaTaiKhoan();
        DoiMatKhau dialog = new DoiMatKhau(this, maTaiKhoan);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        loadData();
    }

    private void lamMoi() {
        txtMaDangNhap.setText("");
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
//        txtTenDangNhap.setEnabled(true);
        txtTenDangNhap.setEditable(true);
        
        txtMatKhau.setEnabled(true);
        txtMatKhau.setEditable(true);
        cbNhanVien.setEnabled(true);
        cbVaiTro.setSelectedIndex(0);
        cbVaiTro.setEnabled(false);
        cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        lblAnh.setIcon(null);
        lblAnh.setText("Chưa có ảnh");
        table.clearSelection();
        loadData();
    }
//
//    private void traCuu() {
//
//
//        String keyword = JOptionPane.showInputDialog(
//                this,
//                "Nhập từ khóa tìm kiếm:",
//                "Tra cứu",
//                JOptionPane.PLAIN_MESSAGE
//        );
//
//        if (keyword == null || keyword.trim().isEmpty()) return;
//
//        String kw = keyword.trim().toLowerCase();
//
//        for (int r = 0; r < tableModel.getRowCount(); r++) {
//            for (int c = 0; c < tableModel.getColumnCount(); c++) {
//                Object val = tableModel.getValueAt(r, c);
//                if (val != null && val.toString().toLowerCase().contains(kw)) {
//                    table.setRowSelectionInterval(r, r);
//                    table.scrollRectToVisible(table.getCellRect(r, 0, true));
//                    return;
//                }
//            }
//        }
//
//        JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!", "Tra cứu", JOptionPane.INFORMATION_MESSAGE);
//    }
    
    private void traCuu() {
        // Xóa dữ liệu cũ trên bảng
        tableModel.setRowCount(0);


        String maTaiKhoan = txtMaDangNhap.getText().trim().toLowerCase();
        String tenDangNhap = txtTenDangNhap.getText().trim().toLowerCase();

        String matKhau = new String(txtMatKhau.getPassword()).trim().toLowerCase();

        String vaiTro = "";
        if (cbVaiTro.getSelectedItem() != null) {
            vaiTro = cbVaiTro.getSelectedItem().toString().trim().toLowerCase();
        }

        String nhanVien = "";
        if (cbNhanVien.getSelectedItem() != null) {
            nhanVien = cbNhanVien.getSelectedItem().toString().trim();

            // bỏ placeholder
            if (nhanVien.equals("--Chọn nhân viên---")) {
                nhanVien = "";
            }
        }
        nhanVien = nhanVien.toLowerCase();

        String trangThai = "";
        if (cbTrangThai.getSelectedItem() != null) {
            trangThai = cbTrangThai.getSelectedItem().toString().trim().toLowerCase();
        }


        boolean coDieuKien =
                !maTaiKhoan.isEmpty() ||
                !tenDangNhap.isEmpty() ||
                !matKhau.isEmpty() ||
                !vaiTro.isEmpty() ||
                !nhanVien.isEmpty() ||
                !trangThai.isEmpty();

        if (!coDieuKien) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập ít nhất 1 tiêu chí tìm kiếm!",
                    "Tra cứu",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean found = false;


        for (TaiKhoan tk : dsTK) {

            String dbMaTK = tk.getMaTaiKhoan() == null
                    ? ""
                    : tk.getMaTaiKhoan().trim().toLowerCase();

            String dbTenDangNhap = tk.getTenDangNhap() == null
                    ? ""
                    : tk.getTenDangNhap().trim().toLowerCase();

            String dbMatKhau = tk.getMatKhau() == null
                    ? ""
                    : tk.getMatKhau().trim().toLowerCase();

            String dbVaiTro = tk.getPhanQuyen() == null
                    ? ""
                    : tk.getPhanQuyen().trim().toLowerCase();

            String dbNhanVien = tk.getMaNV() == null || tk.getMaNV().getHoTen() == null
                    ? ""
                    : tk.getMaNV().getHoTen().trim().toLowerCase();

            String dbTrangThai = tk.isTrangThai()
                    ? "hoạt động"
                    : "khóa";


            if (!maTaiKhoan.isEmpty() && !dbMaTK.contains(maTaiKhoan)) {
                continue;
            }

            if (!tenDangNhap.isEmpty() && !dbTenDangNhap.contains(tenDangNhap)) {
                continue;
            }

            if (!matKhau.isEmpty() && !dbMatKhau.contains(matKhau)) {
                continue;
            }

            if (!vaiTro.isEmpty() && !dbVaiTro.equals(vaiTro)) {
                continue;
            }

            if (!nhanVien.isEmpty() && !dbNhanVien.equals(nhanVien)) {
                continue;
            }

            if (!trangThai.isEmpty() && !dbTrangThai.equals(trangThai)) {
                continue;
            }


            tableModel.addRow(new Object[]{
                    tk.getMaTaiKhoan(),
                    tk.getTenDangNhap(),
                    tk.getPhanQuyen(),
                    tk.getMatKhau(),
                    tk.getMaNV().getHoTen(),
                    tk.isTrangThai() ? "Hoạt động" : "Khóa"
            });

            found = true;
        }

        // ===== Không tìm thấy =====

        if (!found) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy tài khoản phù hợp!",
                    "Tra cứu",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    

    private void loadRowToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtMaDangNhap.setText(tableModel.getValueAt(row, 0).toString());
        txtTenDangNhap.setText(tableModel.getValueAt(row, 1).toString());
        cbVaiTro.setSelectedItem(tableModel.getValueAt(row, 2).toString());

//        String mkThat = tableModel.getValueAt(row, 3).toString();
//        txtMatKhau.setText(PasswordUtil.maHoaMD5(mkThat));
        txtMatKhau.setText(tableModel.getValueAt(row, 3).toString());


        txtTenDangNhap.setEditable(false);

        txtMatKhau.setEditable(false);

        cbNhanVien.setEnabled(true);
        cbNhanVien.setFocusable(false);

        cbVaiTro.setEnabled(false);
        cbVaiTro.setFocusable(false);

        cbTrangThai.setEnabled(true);

        cbNhanVien.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5).toString());

        TaiKhoan tk = dsTK.get(row);
        loadAnhNhanVien(tk.getMaNV().getAnhNhanVien());

        txtMaDangNhap.setForeground(Color.BLACK);
        txtTenDangNhap.setForeground(Color.BLACK);

        cbVaiTro.setForeground(Color.BLACK);
        cbNhanVien.setForeground(Color.BLACK);
        cbTrangThai.setForeground(Color.BLACK);

        txtMatKhau.setForeground(new Color(120, 140, 160));
    }

    private void loadData() {
        dsTK = tk_dao.getAllTaiKhoan();



        tableModel.setRowCount(0);

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



    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(f("SansSerif", Font.BOLD, 15));
        lbl.setForeground(new Color(45, 40, 35));
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

//    private void loadAnhNhanVien(String fileName) {
//        if (fileName != null && !fileName.isEmpty()) {
//            String path = System.getProperty("user.dir") + "/img/" + fileName;
//
//            ImageIcon icon = new ImageIcon(path);
//
//            if (icon.getIconWidth() > 0) {
//                Image img = icon.getImage().getScaledInstance(sc(150), sc(170), Image.SCALE_SMOOTH);
//                lblAnh.setIcon(new ImageIcon(img));
//                lblAnh.setText("");
//            } else {
//                lblAnh.setIcon(null);
//                lblAnh.setText("Không tìm thấy ảnh");
//            }
//
//
//        } else {
//            lblAnh.setIcon(null);
//            lblAnh.setText("Chưa có ảnh");
//        }
//    }
    private void loadAnhNhanVien(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {

            try {
                java.net.URL imgURL = getClass().getResource("/" + fileName);
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    Image img = icon.getImage().getScaledInstance(
                            sc(150),
                            sc(170),
                            Image.SCALE_SMOOTH
                    );
                    lblAnh.setIcon(new ImageIcon(img));
                    lblAnh.setText("");

                } else {
                    lblAnh.setIcon(null);
                    lblAnh.setText("Không tìm thấy ảnh");
                }

            } catch (Exception e) {
                lblAnh.setIcon(null);
                lblAnh.setText("Lỗi tải ảnh");
            }

        } else {
            lblAnh.setIcon(null);
            lblAnh.setText("Chưa có ảnh");
        }
    }



    private JTextField createTextField() {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sc(12), sc(12));

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CLR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sc(12), sc(12));

                g2.dispose();
            }
        };

        tf.setFont(f("SansSerif", Font.PLAIN, 15));
        tf.setPreferredSize(FIELD_SIZE);
        tf.setMinimumSize(FIELD_SIZE);
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(0, sc(12), 0, sc(12)));


        return tf;
    }
    private JComboBox<String> createRoundedComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        styleComboBox(cb);
        return cb;
    }

    private void styleComboBox(JComboBox<?> cb) {


        cb.setFont(f("SansSerif", Font.PLAIN, 15));
        cb.setForeground(Color.BLACK);

        cb.setFocusable(false);
        cb.setPreferredSize(FIELD_SIZE);
        cb.setMinimumSize(FIELD_SIZE);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_SIZE.height));



        cb.setOpaque(false);
        cb.setBorder(new EmptyBorder(0, sc(14), 0, sc(8)));
        cb.setUI(new RoundedComboBoxUI());

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(


                        list, value, index, isSelected, cellHasFocus);

                lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

                if (isSelected) {
                    lbl.setBackground(new Color(224, 207, 180)); // màu khi rê/chọn
                    lbl.setForeground(Color.BLACK);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(Color.BLACK);
                }

                if (index == -1) {
                    lbl.setOpaque(false); // dòng đang hiển thị trên combobox
                } else {
                    lbl.setOpaque(true);  // danh sách xổ xuống
                }



                return lbl;
            }
        });
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            // chuyển "img/abc.png" -> "/abc.png"
            path = "/" + path.replace("img/", "");
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL == null) return null;
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(
                    w, h, Image.SCALE_SMOOTH
            );
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private JButton createFuncButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getModel().isPressed() ? bg.darker() : bg);


                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), sc(12), sc(12)));


                g2.dispose();
                super.paintComponent(g);
            }
        };

        int w = text.length() > 12 ? 210 : 135;
        btn.setPreferredSize(new Dimension(sc(w), sc(42)));
        btn.setForeground(new Color(30, 30, 30));
        btn.setFont(f("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(sc(4), sc(10), sc(4), sc(10)));


        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, sc(18), sc(18)));
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setIconTextGap(sc(7));
        }

        return btn;
    }



    private class RoundedFieldPanel extends JPanel {
        public RoundedFieldPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sc(14), sc(14));

            g2.setColor(CLR_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sc(14), sc(14));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedComboBoxUI extends BasicComboBoxUI {

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

                    g2.drawLine(cx - 7, cy - 4, cx, cy + 4);
                    g2.drawLine(cx, cy + 4, cx + 7, cy - 4);

                    g2.dispose();
                }
            };

            btn.setPreferredSize(new Dimension(sc(40), sc(38)));
            btn.setBorder(null);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // không vẽ nền trắng đè lên bo góc
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = sc(12);
            int w = c.getWidth();
            int h = c.getHeight();

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

            g2.setColor(CLR_BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            g2.dispose();

            super.paint(g, c);
        }
    }
}