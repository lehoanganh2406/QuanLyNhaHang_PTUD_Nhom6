package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import entity.TaiKhoan;

public class KhachHang_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;

    private JTextField txtMaKH, txtTenKH, txtDiemTichLuy, txtSDT;
    private JComboBox<String> cbLoaiKH;
    private JButton btnThem, btnXoa, btnTraCuu, btnLamMoi, btnCapNhat;
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;

    private final Color BG = new Color(250, 246, 241);
    private final Color BROWN = new Color(98, 67, 48);
    private final Color BORDER = new Color(190, 175, 155);

    public KhachHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        initEvents();
        loadMockData();
    }

    public KhachHang_GUI() {
        this(null);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BROWN);
        panel.setBorder(new EmptyBorder(14, 10, 14, 10));

        JLabel lblTitle = new JLabel("DANH SÁCH KHÁCH HÀNG", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);

        panel.add(lblTitle, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMainContent() {
        JPanel panelContent = new JPanel(new BorderLayout(0, 18));
        panelContent.setBackground(BG);
        panelContent.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel topArea = new JPanel(new BorderLayout(18, 0));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(), BorderLayout.CENTER);
        topArea.add(buildButtonPanel(), BorderLayout.EAST);

        panelContent.add(topArea, BorderLayout.NORTH);
        panelContent.add(buildTablePanel(), BorderLayout.CENTER);

        return panelContent;
    }

    private JPanel buildFormPanel() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(255, 253, 248));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaKH = createTextField();
        txtTenKH = createTextField();
        txtDiemTichLuy = createTextField();
        txtSDT = createTextField();

        cbLoaiKH = new JComboBox<>(new String[]{"Thường", "VIP", "Diamond"});
        styleComboBox(cbLoaiKH);

        addRow(panelForm, gbc, 0, "Mã KH", txtMaKH, "Điểm tích lũy", txtDiemTichLuy);
        addRow(panelForm, gbc, 1, "Tên KH", txtTenKH, "SĐT", txtSDT);
        addRow(panelForm, gbc, 2, "Loại KH", cbLoaiKH, "", new JLabel());

        return panelForm;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String label1, JComponent comp1,
                        String label2, JComponent comp2) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.12;
        p.add(createLabel(label1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        p.add(comp1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.12;
        if (label2 == null || label2.isEmpty()) {
            p.add(new JLabel(), gbc);
        } else {
            p.add(createLabel(label2), gbc);
        }

        gbc.gridx = 3;
        gbc.weightx = 0.38;
        p.add(comp2, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panelButtons = new JPanel(new GridLayout(3, 2, 10, 10));
        panelButtons.setOpaque(false);
        panelButtons.setPreferredSize(new Dimension(285, 150));

        btnThem = createButton("THÊM", new Color(114, 190, 120));
        btnXoa = createButton("XÓA", new Color(235, 125, 125));
        btnTraCuu = createButton("TRA CỨU", new Color(100, 181, 246));
        btnLamMoi = createButton("LÀM MỚI", new Color(230, 230, 230));
        btnCapNhat = createButton("CẬP NHẬT", new Color(150, 175, 245));

        panelButtons.add(btnThem);
        panelButtons.add(btnXoa);
        panelButtons.add(btnTraCuu);
        panelButtons.add(btnLamMoi);
        panelButtons.add(new JLabel());
        panelButtons.add(btnCapNhat);

        return panelButtons;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {
                "Mã khách hàng", "Họ tên", "Điểm tích lũy",
                "Nhân viên", "SĐT", "Tổng chi tiêu", "Loại khách hàng"
        };

        modelKhachHang = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(modelKhachHang);
        tblKhachHang.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tblKhachHang.setRowHeight(36);
        tblKhachHang.setShowGrid(true);
        tblKhachHang.setGridColor(new Color(220, 210, 195));
        tblKhachHang.setSelectionBackground(new Color(238, 225, 205));
        tblKhachHang.setSelectionForeground(Color.BLACK);
        tblKhachHang.setFillsViewportHeight(true);
        tblKhachHang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = tblKhachHang.getTableHeader();
        header.setPreferredSize(new Dimension(100, 38));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setBackground(new Color(208, 144, 106));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("SansSerif", Font.BOLD, 15));
                c.setHorizontalAlignment(JLabel.CENTER);
                c.setOpaque(true);
                return c;
            }
        };

        for (int i = 0; i < tblKhachHang.getColumnModel().getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setBorder(new EmptyBorder(0, 8, 0, 8));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 248, 240));
                    c.setForeground(Color.BLACK);
                }

                if (column == 1) {
                    c.setHorizontalAlignment(JLabel.LEFT);
                } else {
                    c.setHorizontalAlignment(JLabel.CENTER);
                }

                return c;
            }
        };

        for (int i = 0; i < tblKhachHang.getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setCellRenderer(bodyRenderer);
        }

        int[] widths = {130, 200, 120, 150, 140, 150, 150};
        for (int i = 0; i < widths.length; i++) {
            tblKhachHang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        tblKhachHang.getSelectionModel().addListSelectionListener(this::onTableSelected);

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(new LineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private void initEvents() {
        btnThem.addActionListener(e -> themKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
        btnCapNhat.addActionListener(e -> capNhatKhachHang());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTraCuu.addActionListener(e -> traCuu());
    }

    private void onTableSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        int row = tblKhachHang.getSelectedRow();
        if (row == -1) return;

        txtMaKH.setText(getValue(row, 0));
        txtTenKH.setText(getValue(row, 1));
        txtDiemTichLuy.setText(getValue(row, 2));
        txtSDT.setText(getValue(row, 4));

        Object loai = modelKhachHang.getValueAt(row, 6);
        if (loai != null) cbLoaiKH.setSelectedItem(loai.toString());
    }

    private void themKhachHang() {
        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String loaiKH = cbLoaiKH.getSelectedItem().toString();
        String diem = txtDiemTichLuy.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (maKH.isEmpty() || tenKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã và tên khách hàng!");
            return;
        }

        modelKhachHang.addRow(new Object[]{
                maKH, tenKH, diem.isEmpty() ? "0" : diem,
                "Ngọc Tiên", sdt, "0 VNĐ", loaiKH
        });

        lamMoi();
    }

    private void xoaKhachHang() {
        int row = tblKhachHang.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khách hàng này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            modelKhachHang.removeRow(row);
            lamMoi();
        }
    }

    private void capNhatKhachHang() {
        int row = tblKhachHang.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng từ bảng để cập nhật!");
            return;
        }

        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String loaiKH = cbLoaiKH.getSelectedItem().toString();
        String diem = txtDiemTichLuy.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (tenKH.isEmpty() || maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã và tên khách hàng không được để trống!");
            return;
        }

        modelKhachHang.setValueAt(maKH, row, 0);
        modelKhachHang.setValueAt(tenKH, row, 1);
        modelKhachHang.setValueAt(diem.isEmpty() ? "0" : diem, row, 2);
        modelKhachHang.setValueAt(sdt, row, 4);
        modelKhachHang.setValueAt(loaiKH, row, 6);

        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        lamMoi();
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "Nhập từ khóa tìm kiếm:",
                "Tra cứu khách hàng",
                JOptionPane.PLAIN_MESSAGE
        );

        if (keyword == null || keyword.trim().isEmpty()) return;

        String kw = keyword.trim().toLowerCase();

        for (int r = 0; r < modelKhachHang.getRowCount(); r++) {
            for (int c = 0; c < modelKhachHang.getColumnCount(); c++) {
                Object value = modelKhachHang.getValueAt(r, c);
                if (value != null && value.toString().toLowerCase().contains(kw)) {
                    tblKhachHang.setRowSelectionInterval(r, r);
                    tblKhachHang.scrollRectToVisible(tblKhachHang.getCellRect(r, 0, true));
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng phù hợp!");
    }

    private void lamMoi() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtDiemTichLuy.setText("");
        txtSDT.setText("");
        cbLoaiKH.setSelectedIndex(0);
        tblKhachHang.clearSelection();
        txtMaKH.requestFocus();
    }

    private void loadMockData() {
        modelKhachHang.addRow(new Object[]{
                "KH0001", "Lê Thu Minh", "100", "Ngọc Tiên",
                "0123456789", "7.000.000 VNĐ", "VIP"
        });
    }

    private String getValue(int row, int col) {
        Object value = modelKhachHang.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setForeground(new Color(55, 45, 35));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txt.setPreferredSize(new Dimension(260, 38));
        txt.setMinimumSize(new Dimension(180, 38));
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return txt;
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cb.setPreferredSize(new Dimension(260, 38));
        cb.setMinimumSize(new Dimension(180, 38));
        cb.setBackground(Color.WHITE);
        cb.setFocusable(false);
        cb.setBorder(new LineBorder(BORDER, 1));
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(150, 140, 125), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}