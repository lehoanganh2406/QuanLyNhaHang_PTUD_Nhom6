package gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class NhanVien_GUI extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color CLR_HEADER_BG = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG = Color.WHITE;
    private static final Color CLR_FORM_BG = new Color(250, 248, 242);
    private static final Color CLR_PANEL_BG = new Color(242, 238, 225);
    private static final Color CLR_BTN_ADD = new Color(102, 187, 106);
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);
    private static final Color CLR_BTN_RESET = Color.WHITE;
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246);
    private static final Color CLR_TABLE_HDR = new Color(222, 214, 196);
    private static final Color CLR_BORDER = new Color(175, 160, 135);
    
    private static final Dimension FIELD_SIZE = new Dimension(280, 38);

    private JTextField txtMaNV, txtHoTen, txtEmail, txtSDT, txtCCCD, txtLyDoNghi;
    private JDateChooser txtNgaySinh;
    private JComboBox<String> cbChucVu, cbTrangThai;
    private JRadioButton rdNam, rdNu;
    private ButtonGroup bgGioiTinh;
    private JLabel lblAnh;
    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnThem, btnCapNhat, btnLamMoi, btnTraCuu;

    private TaiKhoan taiKhoanDangNhap;
    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private Connection con;
    private String duongDanAnh = "";
    private List<NhanVien> dsNV;

    public NhanVien_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);

        con = ConnectDB.getConnection();
        loadData();
        txtMaNV.setText(nv_dao.getNextMaNV());
    }

    public NhanVien_GUI() {
        this(null);
    }

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));

        JLabel lbl = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 34));
        lbl.setForeground(CLR_HEADER_FG);

        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel buildCenterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel topArea = new JPanel(new BorderLayout(0, 8));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(), BorderLayout.CENTER);
        topArea.add(buildButtonPanel(), BorderLayout.SOUTH);

        pnl.add(topArea, BorderLayout.NORTH);
        pnl.add(buildTablePanel(), BorderLayout.CENTER);

        return pnl;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(14, 0));
        outer.setBackground(CLR_FORM_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = createTextField();
        txtMaNV.setEnabled(false);
        txtMaNV.setDisabledTextColor(new Color(60, 60, 60));

        cbChucVu = new JComboBox<>(new String[]{"Quản lý", "Lễ tân"});
        styleComboBox(cbChucVu);
        addRow(pnlFields, gbc, 0, "Mã nhân viên", txtMaNV, "Chức vụ", cbChucVu);

        txtHoTen = createTextField();
        txtEmail = createTextField();
        addRow(pnlFields, gbc, 1, "Họ tên", txtHoTen, "Email", txtEmail);

        txtNgaySinh = new RoundedDateChooser();
        txtSDT = createTextField();
        addRow(pnlFields, gbc, 2, "Ngày sinh", txtNgaySinh, "SĐT", txtSDT);

        rdNam = new JRadioButton("Nam");
        rdNu = new JRadioButton("Nữ");
        rdNam.setOpaque(false);
        rdNu.setOpaque(false);
        rdNam.setFont(new Font("SansSerif", Font.PLAIN, 17));
        rdNu.setFont(new Font("SansSerif", Font.PLAIN, 17));

        bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rdNam);
        bgGioiTinh.add(rdNu);
        rdNam.setSelected(true);

        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlGT.setOpaque(false);
        pnlGT.add(rdNam);
        pnlGT.add(rdNu);

        cbTrangThai = new JComboBox<>(new String[]{"Đang làm", "Nghỉ việc"});
        styleComboBox(cbTrangThai);
        cbTrangThai.addActionListener(e -> xuLyTrangThai());

        addRow(pnlFields, gbc, 3, "Giới tính", pnlGT, "Trạng thái", cbTrangThai);

        txtCCCD = createTextField();
        txtLyDoNghi = createTextField();
        txtLyDoNghi.setEnabled(false);

        addRow(pnlFields, gbc, 4, "CCCD", txtCCCD, "Lý do nghỉ", txtLyDoNghi);

        outer.add(pnlFields, BorderLayout.CENTER);
        outer.add(buildImagePanel(), BorderLayout.EAST);

        return outer;
    }

    private JPanel buildImagePanel() {
        JPanel pnlAnh = new JPanel();
        pnlAnh.setLayout(new BoxLayout(pnlAnh, BoxLayout.Y_AXIS));
        pnlAnh.setOpaque(false);
        pnlAnh.setPreferredSize(new Dimension(190, 0));
        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel lblTitle = new JLabel("Ảnh nhân viên");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAnh = new JLabel("Chọn ảnh", SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(150, 180));
        lblAnh.setMinimumSize(new Dimension(150, 160));
        lblAnh.setMaximumSize(new Dimension(150, 190));
        lblAnh.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setVerticalAlignment(SwingConstants.CENTER);
        lblAnh.setFont(new Font("SansSerif", Font.PLAIN, 13));
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

        return pnlAnh;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String lbl1, JComponent comp1,
                        String lbl2, JComponent comp2) {

        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.12;
        p.add(createLabel(lbl1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        p.add(comp1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.12;
        p.add(createLabel(lbl2), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.38;
        p.add(comp2, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

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
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(new Color(215, 205, 185));
        table.setSelectionBackground(new Color(190, 220, 245));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                Object valueTrangThai = table.getValueAt(row, 8);
                String trangThai = valueTrangThai == null ? "" : valueTrangThai.toString();

                if (!isSelected) {
                    c.setForeground("Nghỉ việc".equalsIgnoreCase(trangThai) ? Color.RED : Color.BLACK);
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 248, 242));
                }

                if (column == 1 || column == 6 || column == 9) {
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        });

        int[] widths = {110, 180, 80, 110, 120, 130, 210, 110, 110, 180};
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
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
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
            NhanVien nvCu = dsNV.get(row);

            String anh = duongDanAnh;

            if (anh == null || anh.isEmpty()) {
                anh = nvCu.getAnhNhanVien();
            }

            String trangThaiCu = nvCu.getTrangThai();
            String trangThaiMoi = cbTrangThai.getSelectedItem().toString();

            String lyDo = nvCu.getLyDo();

            if (trangThaiCu.equalsIgnoreCase("Đang làm")
                    && trangThaiMoi.equalsIgnoreCase("Nghỉ việc")) {

                lyDo = JOptionPane.showInputDialog(this, "Nhập lý do nghỉ việc:");

                if (lyDo == null || lyDo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Phải nhập lý do!");
                    return;
                }
            }

            if (trangThaiMoi.equalsIgnoreCase("Đang làm")) {
                lyDo = null;
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
                    trangThaiMoi,
                    lyDo
            );

            if (nv_dao.capNhatNhanVien(nv)) {
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
        lblAnh.setText("Chọn ảnh");
        duongDanAnh = "";
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "Nhập từ khóa tìm kiếm:",
                "Tra cứu",
                JOptionPane.PLAIN_MESSAGE
        );

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

        loadAnhNhanVien(nv.getAnhNhanVien());
    }

    private void loadAnhNhanVien(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            String fullPath = System.getProperty("user.dir") + "/img/" + fileName;
            File file = new File(fullPath);

            if (file.exists()) {
                ImageIcon icon = new ImageIcon(fullPath);
                Image img = icon.getImage().getScaledInstance(140, 160, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");
            } else {
                lblAnh.setIcon(null);
                lblAnh.setText("Không tìm thấy ảnh");
            }
        } else {
            lblAnh.setIcon(null);
            lblAnh.setText("Chọn ảnh");
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
                Image img = icon.getImage().getScaledInstance(140, 160, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi chọn ảnh!");
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setForeground(new Color(45, 45, 45));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(CLR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
            }
        };

        tf.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tf.setPreferredSize(FIELD_SIZE);
        tf.setMinimumSize(new Dimension(180, 38));
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        return tf;
    }
    private class RoundedDateChooser extends JDateChooser {
        public RoundedDateChooser() {
            super();
            setDateFormatString("dd/MM/yyyy");
            setPreferredSize(FIELD_SIZE);
            setMinimumSize(new Dimension(180, 38));
            setOpaque(false);
            setBorder(null);

            JTextField editor = (JTextField) getDateEditor().getUiComponent();
            editor.setFont(new Font("SansSerif", Font.PLAIN, 16));
            editor.setOpaque(false);
            editor.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 8));

            JButton btn = (JButton) getCalendarButton();
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(null);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(38, 38));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

            g2.setColor(CLR_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    }
    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cb.setForeground(Color.BLACK);
        cb.setFocusable(false);

        cb.setPreferredSize(FIELD_SIZE);
        cb.setMinimumSize(new Dimension(180, 38));
        cb.setOpaque(false);
        cb.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        cb.setUI(new RoundedComboBoxUI());

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

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
    private JButton createFuncButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(new Color(30, 30, 30));
        btn.setFont(new Font("SansSerif", Font.BOLD, 17));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(145, 40));
        btn.setMargin(new Insets(4, 10, 4, 10));
        btn.setIcon(loadIcon(iconPath, 18, 18));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(8);

        return btn;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() <= 0) return null;

            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);

        } catch (Exception e) {
            return null;
        }
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

            btn.setPreferredSize(new Dimension(40, 38));
            btn.setBorder(null);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);

            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);

            g2.setColor(new Color(175, 160, 135));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);

            g2.dispose();

            super.paint(g, c);
        }
    }
}