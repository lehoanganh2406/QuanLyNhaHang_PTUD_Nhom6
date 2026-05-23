package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import entity.TaiKhoan;

/**
 * GUI quản lý khuyến mãi dùng dữ liệu thật từ SQL Server.
 * Dialog thêm/sửa dùng JDateChooser + spinner giờ/phút.
 * Khi thêm khuyến mãi: không cho chọn thời gian bắt đầu nhỏ hơn thời gian hiện tại.
 */
public class KhuyenMai_GUI extends JPanel {


    private static final Color BG = new Color(245, 245, 245);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(228, 228, 228);
    private static final Color PRIMARY = new Color(40, 100, 180);
    private static final Color PRIMARY_LIGHT = new Color(225, 240, 255);
    private static final Color GREEN = new Color(46, 125, 50);
    private static final Color GREEN_LIGHT = new Color(230, 244, 234);
    private static final Color RED = new Color(198, 40, 40);
    private static final Color RED_LIGHT = new Color(255, 235, 238);

    private static final String STATUS_DANG_AP_DUNG = "Đang áp dụng";
    private static final String STATUS_NGUNG_AP_DUNG = "Ngưng áp dụng";
    private static final String STATUS_CHUA_AP_DUNG = "Chưa áp dụng";
    private static final String STATUS_HET_HAN = "Hết hạn";
    private static final String PLACEHOLDER_SEARCH = "Tìm theo mã, tên khuyến mãi, loại, đối tượng...";

    private TaiKhoan taiKhoanDangNhap;

    private JTextField txtSearch;
    private JComboBox<String> cboTrangThaiLoc;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTongKM;
    private JLabel lblDangApDung;
    private JLabel lblSapHetHan;
    private JLabel lblNgungHetHan;
    private JTextArea txtDetail;

    private final List<KhuyenMaiRow> dsKhuyenMai = new ArrayList<>();
    private final Map<String, String> loaiKhuyenMaiMap = new LinkedHashMap<>();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public KhuyenMai_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();
        setLayout(new BorderLayout());
        setBackground(BG);
        add(createMainPanel(), BorderLayout.CENTER);
        loadData();
    }

    public KhuyenMai_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(18, 0));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(18, 18, 18, 18));
        main.add(createLeftPanel(), BorderLayout.CENTER);
        main.add(createRightPanel(), BorderLayout.EAST);
        return main;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(0, 14));
        left.setOpaque(false);
        left.add(createHeaderPanel(), BorderLayout.NORTH);
        left.add(createTablePanel(), BorderLayout.CENTER);
        return left;
    }

    private JPanel createHeaderPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setOpaque(false);

        JLabel title = new JLabel("Quản lý khuyến mãi");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        outer.add(title, BorderLayout.NORTH);

        JPanel actionCard = createCardPanel(new BorderLayout(0, 12));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.add(new JLabel("Tìm khuyến mãi:"));
        txtSearch = createPlaceholderTextField(PLACEHOLDER_SEARCH, 32);
        txtSearch.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update(DocumentEvent e) { renderData(); }
        });
        filterRow.add(txtSearch);

        filterRow.add(new JLabel("Trạng thái:"));
        cboTrangThaiLoc = new JComboBox<>(new String[] {
                "Tất cả", STATUS_DANG_AP_DUNG, STATUS_CHUA_AP_DUNG, STATUS_HET_HAN, STATUS_NGUNG_AP_DUNG
        });
        cboTrangThaiLoc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cboTrangThaiLoc.setPreferredSize(new Dimension(170, 38));
        cboTrangThaiLoc.addActionListener(e -> renderData());
        filterRow.add(cboTrangThaiLoc);

        JButton btnRefresh = createButton("Làm mới", new Color(245, 235, 220), new Color(120, 90, 70));
        btnRefresh.addActionListener(e -> loadData());
        filterRow.add(btnRefresh);
        actionCard.add(filterRow, BorderLayout.NORTH);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnAdd = createButton("+ Thêm khuyến mãi", PRIMARY_LIGHT, PRIMARY);
        JButton btnEdit = createButton("Sửa khuyến mãi", GREEN_LIGHT, GREEN);
        JButton btnDelete = createButton("Xóa / Ngưng áp dụng", RED_LIGHT, RED);
        btnAdd.addActionListener(e -> moDialogThemKhuyenMai());
        btnEdit.addActionListener(e -> moDialogSuaKhuyenMai());
        btnDelete.addActionListener(e -> xuLyXoaHoacNgung());
        buttonRow.add(btnAdd);
        buttonRow.add(btnEdit);
        buttonRow.add(btnDelete);
        actionCard.add(buttonRow, BorderLayout.CENTER);

        outer.add(actionCard, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createTablePanel() {
        JPanel card = createCardPanel(new BorderLayout(0, 12));
        String[] columns = { "Mã KM", "Tên khuyến mãi", "Loại", "Giá trị", "Đối tượng", "Điều kiện", "Bắt đầu", "Kết thúc", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                return c;
            }
        };
        table.setRowHeight(44);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(new Color(205, 205, 205));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(235, 244, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) capNhatChiTietKhuyenMai();
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) moDialogSuaKhuyenMai();
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false); // Không cho kéo đổi vị trí cột khi chạy GUI
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setBorder(new LineBorder(new Color(205, 205, 205), 1));
        header.setPreferredSize(new Dimension(0, 42));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);
        left.setBorder(new EmptyBorder(0, 10, 0, 0));
        for (int i = 0; i < columns.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        setColumnWidths(table, 80, 210, 110, 100, 120, 110, 140, 140, 130);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(new Color(235, 235, 235), 1));
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(370, 0));

        JPanel statsCard = createCardPanel(new GridLayout(2, 2, 12, 12));
        lblTongKM = createMetricLabel("0");
        lblDangApDung = createMetricLabel("0");
        lblSapHetHan = createMetricLabel("0");
        lblNgungHetHan = createMetricLabel("0");
        statsCard.add(createMetricPanel("Tổng KM", lblTongKM, new Color(245, 248, 255)));
        statsCard.add(createMetricPanel("Đang áp dụng", lblDangApDung, new Color(235, 248, 239)));
        statsCard.add(createMetricPanel("Sắp hết hạn", lblSapHetHan, new Color(255, 248, 230)));
        statsCard.add(createMetricPanel("Ngưng/Hết hạn", lblNgungHetHan, new Color(255, 239, 239)));

        JPanel detailCard = createCardPanel(new BorderLayout(0, 10));
        JLabel title = new JLabel("Chi tiết khuyến mãi");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        detailCard.add(title, BorderLayout.NORTH);
        txtDetail = new JTextArea(18, 20);
        txtDetail.setEditable(false);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        txtDetail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDetail.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtDetail.setText("Chọn một khuyến mãi trong bảng để xem chi tiết.");
        detailCard.add(new JScrollPane(txtDetail), BorderLayout.CENTER);

        right.add(statsCard);
        right.add(Box.createRigidArea(new Dimension(0, 16)));
        right.add(detailCard);
        right.add(Box.createVerticalGlue());
        return right;
    }

    private JPanel createCardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private JPanel createMetricPanel(String title, JLabel valueLabel, Color bg) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(true);
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(14, 14, 14, 14)));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(90, 90, 90));
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createMetricLabel(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("SansSerif", Font.BOLD, 26));
        label.setForeground(new Color(45, 45, 45));
        return label;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(8, 14, 8, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField createPlaceholderTextField(String placeholder, int columns) {
        JTextField tf = new JTextField(placeholder, columns);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setForeground(Color.GRAY);
        tf.setToolTipText(placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(8, 10, 8, 10)));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                }
            }
        });
        return tf;
    }

    private void setColumnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void loadData() {
        try {
            ConnectDB.getInstance().connect();
            capNhatTrangThaiKhuyenMaiTheoThoiGian();
            loaiKhuyenMaiMap.clear();
            loaiKhuyenMaiMap.putAll(loadLoaiKhuyenMai());
            dsKhuyenMai.clear();
            dsKhuyenMai.addAll(loadKhuyenMai());
            renderData();
            updateStats();
            capNhatChiTietKhuyenMai();
        } catch (Exception ex) {
            showError("Không thể tải dữ liệu khuyến mãi", ex);
        }
    }

    private void capNhatTrangThaiKhuyenMaiTheoThoiGian() {
        String sql = "UPDATE KhuyenMai SET trangThai = CASE "
                + "WHEN trangThai = N'Ngưng áp dụng' THEN trangThai "
                + "WHEN GETDATE() < thoiGianBatDau THEN N'Chưa áp dụng' "
                + "WHEN GETDATE() > thoiGianKetThuc THEN N'Hết hạn' "
                + "ELSE N'Đang áp dụng' END";
        try (Statement st = ConnectDB.getConnection().createStatement()) {
            st.executeUpdate(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map<String, String> loadLoaiKhuyenMai() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT maLoaiKM, tenLoaiKM FROM LoaiKhuyenMai ORDER BY maLoaiKM";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) map.put(rs.getString("maLoaiKM"), rs.getString("tenLoaiKM"));
        }
        return map;
    }

    private List<KhuyenMaiRow> loadKhuyenMai() throws Exception {
        List<KhuyenMaiRow> list = new ArrayList<>();
        String sql = "SELECT km.*, lkm.tenLoaiKM FROM KhuyenMai km INNER JOIN LoaiKhuyenMai lkm ON km.maLoaiKM = lkm.maLoaiKM ORDER BY km.maKM";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhuyenMaiRow km = new KhuyenMaiRow();
                km.maKM = rs.getString("maKM");
                km.maLoaiKM = rs.getString("maLoaiKM");
                km.tenLoaiKM = rs.getString("tenLoaiKM");
                km.maNV = rs.getString("maNV");
                km.giaTri = rs.getDouble("giaTri");
                km.tenKhuyenMai = rs.getString("tenKhuyenMai");
                Timestamp bd = rs.getTimestamp("thoiGianBatDau");
                Timestamp kt = rs.getTimestamp("thoiGianKetThuc");
                km.thoiGianBatDau = bd == null ? null : bd.toLocalDateTime();
                km.thoiGianKetThuc = kt == null ? null : kt.toLocalDateTime();
                km.doiTuongApDung = rs.getString("doiTuongApDung");
                km.dieuKienApDung = rs.getDouble("dieuKienApDung");
                km.ghiChu = rs.getString("ghiChu");
                km.trangThaiDB = rs.getString("trangThai");
                km.trangThaiHienThi = tinhTrangThai(km);
                list.add(km);
            }
        }
        return list;
    }

    private String tinhTrangThai(KhuyenMaiRow km) {
        if (km.trangThaiDB != null && km.trangThaiDB.trim().equalsIgnoreCase(STATUS_NGUNG_AP_DUNG)) return STATUS_NGUNG_AP_DUNG;
        LocalDateTime now = LocalDateTime.now();
        if (km.thoiGianBatDau != null && now.isBefore(km.thoiGianBatDau)) return STATUS_CHUA_AP_DUNG;
        if (km.thoiGianKetThuc != null && now.isAfter(km.thoiGianKetThuc)) return STATUS_HET_HAN;
        return STATUS_DANG_AP_DUNG;
    }

    private void renderData() {
        String keyword = getSearchKeyword();
        String statusFilter = String.valueOf(cboTrangThaiLoc.getSelectedItem());
        tableModel.setRowCount(0);
        for (KhuyenMaiRow km : dsKhuyenMai) {
            if (!matches(km, keyword)) continue;
            if (!"Tất cả".equals(statusFilter) && !statusFilter.equals(km.trangThaiHienThi)) continue;
            tableModel.addRow(new Object[] {
                    km.maKM,
                    km.tenKhuyenMai,
                    km.tenLoaiKM,
                    formatGiaTri(km),
                    nvl(km.doiTuongApDung),
                    moneyFormat.format(km.dieuKienApDung),
                    formatDateTime(km.thoiGianBatDau),
                    formatDateTime(km.thoiGianKetThuc),
                    km.trangThaiHienThi
            });
        }
        updateStats();
    }

    private String getSearchKeyword() {
        String s = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        if (s.equals(PLACEHOLDER_SEARCH)) return "";
        return s.toLowerCase(Locale.ROOT);
    }

    private boolean matches(KhuyenMaiRow km, String keyword) {
        if (keyword == null || keyword.isEmpty()) return true;
        return safe(km.maKM).contains(keyword)
                || safe(km.tenKhuyenMai).contains(keyword)
                || safe(km.tenLoaiKM).contains(keyword)
                || safe(km.doiTuongApDung).contains(keyword)
                || safe(km.ghiChu).contains(keyword)
                || safe(km.trangThaiHienThi).contains(keyword);
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT); }
    private String nvl(String s) { return s == null || s.trim().isEmpty() ? "Tất cả" : s; }
    private String formatDateTime(LocalDateTime dt) { return dt == null ? "" : dt.format(dtf); }

    private String formatGiaTri(KhuyenMaiRow km) {
        String loai = km.tenLoaiKM == null ? "" : km.tenLoaiKM.toLowerCase(Locale.ROOT);
        if (loai.contains("phần") || loai.contains("thành viên")) return moneyFormat.format(km.giaTri) + "%";
        return moneyFormat.format(km.giaTri) + " đ";
    }

    private void updateStats() {
        int tong = dsKhuyenMai.size();
        long dang = dsKhuyenMai.stream().filter(km -> STATUS_DANG_AP_DUNG.equals(km.trangThaiHienThi)).count();
        long sapHetHan = dsKhuyenMai.stream().filter(km -> STATUS_DANG_AP_DUNG.equals(km.trangThaiHienThi)
                && km.thoiGianKetThuc != null
                && !km.thoiGianKetThuc.isBefore(LocalDateTime.now())
                && !km.thoiGianKetThuc.isAfter(LocalDateTime.now().plusDays(7))).count();
        long ngungHetHan = dsKhuyenMai.stream().filter(km -> STATUS_NGUNG_AP_DUNG.equals(km.trangThaiHienThi) || STATUS_HET_HAN.equals(km.trangThaiHienThi)).count();
        lblTongKM.setText(String.valueOf(tong));
        lblDangApDung.setText(String.valueOf(dang));
        lblSapHetHan.setText(String.valueOf(sapHetHan));
        lblNgungHetHan.setText(String.valueOf(ngungHetHan));
    }

    private void capNhatChiTietKhuyenMai() {
        KhuyenMaiRow km = getSelectedKhuyenMai();
        if (km == null) {
            txtDetail.setText("Chọn một khuyến mãi trong bảng để xem chi tiết.");
            return;
        }
        txtDetail.setText("Mã khuyến mãi: " + km.maKM + "\n"
                + "Tên khuyến mãi: " + km.tenKhuyenMai + "\n"
                + "Loại: " + km.tenLoaiKM + "\n"
                + "Giá trị: " + formatGiaTri(km) + "\n"
                + "Đối tượng áp dụng: " + nvl(km.doiTuongApDung) + "\n"
                + "Điều kiện áp dụng: " + moneyFormat.format(km.dieuKienApDung) + " đ\n"
                + "Bắt đầu: " + formatDateTime(km.thoiGianBatDau) + "\n"
                + "Kết thúc: " + formatDateTime(km.thoiGianKetThuc) + "\n"
                + "Trạng thái thực tế: " + km.trangThaiHienThi + "\n"
                + "Nhân viên tạo/sửa: " + nvl(km.maNV) + "\n"
                + "Ghi chú: " + nvl(km.ghiChu));
    }

    private KhuyenMaiRow getSelectedKhuyenMai() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String ma = String.valueOf(table.getValueAt(row, 0));
        for (KhuyenMaiRow km : dsKhuyenMai) if (km.maKM.equals(ma)) return km;
        return null;
    }

    private void moDialogThemKhuyenMai() {
        KhuyenMaiForm form = new KhuyenMaiForm(null, true);
        int result = JOptionPane.showConfirmDialog(this, form, "Thêm khuyến mãi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        if (!form.validateInput()) return;

        try {
            String sql = "INSERT INTO KhuyenMai(maLoaiKM, maNV, giaTri, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, doiTuongApDung, dieuKienApDung, ghiChu, trangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                fillStatementFromForm(ps, form, null);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công.");
            loadData();
        } catch (Exception ex) {
            showError("Không thể thêm khuyến mãi", ex);
        }
    }

    private void moDialogSuaKhuyenMai() {
        KhuyenMaiRow selected = getSelectedKhuyenMai();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Bạn cần chọn một khuyến mãi trước.", "Chưa chọn khuyến mãi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhuyenMaiForm form = new KhuyenMaiForm(selected, false);
        int result = JOptionPane.showConfirmDialog(this, form, "Sửa khuyến mãi " + selected.maKM, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        if (!form.validateInput()) return;

        try {
            String sql = "UPDATE KhuyenMai SET maLoaiKM = ?, maNV = ?, giaTri = ?, tenKhuyenMai = ?, thoiGianBatDau = ?, thoiGianKetThuc = ?, doiTuongApDung = ?, dieuKienApDung = ?, ghiChu = ?, trangThai = ? WHERE maKM = ?";
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                fillStatementFromForm(ps, form, selected.maKM);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công.");
            loadData();
        } catch (Exception ex) {
            showError("Không thể sửa khuyến mãi", ex);
        }
    }

    private void fillStatementFromForm(PreparedStatement ps, KhuyenMaiForm form, String maKMForUpdate) throws Exception {
        LocalDateTime bd = form.getBatDau();
        LocalDateTime kt = form.getKetThuc();
        String trangThai = form.getTrangThaiMuonLuu();
        ps.setString(1, form.getMaLoaiKM());
        ps.setString(2, getCurrentMaNV());
        ps.setDouble(3, form.getGiaTri());
        ps.setString(4, form.getTenKhuyenMai());
        ps.setTimestamp(5, Timestamp.valueOf(bd));
        ps.setTimestamp(6, Timestamp.valueOf(kt));
        ps.setString(7, form.getDoiTuongApDung());
        ps.setDouble(8, form.getDieuKienApDung());
        ps.setString(9, form.getGhiChu());
        ps.setString(10, trangThai);
        if (maKMForUpdate != null) ps.setString(11, maKMForUpdate);
    }

    private String getCurrentMaNV() throws Exception {
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null && taiKhoanDangNhap.getMaNV().getMaNV() != null) {
            return taiKhoanDangNhap.getMaNV().getMaNV();
        }
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("SELECT TOP 1 maNV FROM NhanVien ORDER BY maNV");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("maNV");
        }
        throw new Exception("Không tìm thấy nhân viên để lưu khuyến mãi.");
    }

    private void xuLyXoaHoacNgung() {
        KhuyenMaiRow km = getSelectedKhuyenMai();
        if (km == null) {
            JOptionPane.showMessageDialog(this, "Bạn cần chọn một khuyến mãi trước.", "Chưa chọn khuyến mãi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (daDuocDungTrongHoaDon(km.maKM)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Khuyến mãi này đã được dùng trong hóa đơn nên không xóa thật.\nBạn có muốn chuyển sang trạng thái 'Ngưng áp dụng' không?",
                        "Ngưng áp dụng", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("UPDATE KhuyenMai SET trangThai = N'Ngưng áp dụng' WHERE maKM = ?")) {
                    ps.setString(1, km.maKM);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Đã ngưng áp dụng khuyến mãi.");
            } else {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Khuyến mãi chưa dùng trong hóa đơn. Bạn có muốn xóa khỏi CSDL không?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("DELETE FROM KhuyenMai WHERE maKM = ?")) {
                    ps.setString(1, km.maKM);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công.");
            }
            loadData();
        } catch (Exception ex) {
            showError("Không thể xóa/ngưng khuyến mãi", ex);
        }
    }

    private boolean daDuocDungTrongHoaDon(String maKM) throws Exception {
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("SELECT COUNT(*) FROM HoaDon WHERE maKM = ?")) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void showError(String message, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, message + ":\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private class KhuyenMaiForm extends JPanel {
        private static final long serialVersionUID = 1L;
        private JTextField txtTen;
        private JComboBox<ComboLoaiKM> cboLoai;
        private JTextField txtGiaTri;
        private JComboBox<String> cboDoiTuong;
        private JTextField txtDieuKien;
        private DateTimePickerPanel pickerBatDau;
        private DateTimePickerPanel pickerKetThuc;
        private JTextArea txtGhiChu;
        private JComboBox<String> cboTrangThai;
        private boolean themMoi;

        KhuyenMaiForm(KhuyenMaiRow km, boolean themMoi) {
            this.themMoi = themMoi;
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(650, 520));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(7, 7, 7, 7);
            gbc.anchor = GridBagConstraints.WEST;

            txtTen = new JTextField(28);
            cboLoai = new JComboBox<>();
            DefaultComboBoxModel<ComboLoaiKM> modelLoai = new DefaultComboBoxModel<>();
            for (Map.Entry<String, String> e : loaiKhuyenMaiMap.entrySet()) modelLoai.addElement(new ComboLoaiKM(e.getKey(), e.getValue()));
            cboLoai.setModel(modelLoai);
            txtGiaTri = new JTextField(12);
            cboDoiTuong = new JComboBox<>(new String[] { "Tất cả", "Thường", "Vàng", "Kim cương" });
            txtDieuKien = new JTextField(12);
            pickerBatDau = new DateTimePickerPanel(themMoi ? LocalDateTime.now().plusMinutes(5) : (km == null ? LocalDateTime.now().plusMinutes(5) : km.thoiGianBatDau), true);
            pickerKetThuc = new DateTimePickerPanel(themMoi ? LocalDateTime.now().plusDays(7) : (km == null ? LocalDateTime.now().plusDays(7) : km.thoiGianKetThuc), true);
            txtGhiChu = new JTextArea(4, 28);
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            cboTrangThai = new JComboBox<>(new String[] { STATUS_DANG_AP_DUNG, STATUS_NGUNG_AP_DUNG });

            Font f = new Font("SansSerif", Font.PLAIN, 14);
            txtTen.setFont(f); cboLoai.setFont(f); txtGiaTri.setFont(f); cboDoiTuong.setFont(f); txtDieuKien.setFont(f); txtGhiChu.setFont(f); cboTrangThai.setFont(f);

            if (km != null) {
                txtTen.setText(km.tenKhuyenMai);
                selectLoai(km.maLoaiKM);
                txtGiaTri.setText(removeTrailingZero(km.giaTri));
                cboDoiTuong.setSelectedItem(nvl(km.doiTuongApDung));
                txtDieuKien.setText(removeTrailingZero(km.dieuKienApDung));
                txtGhiChu.setText(km.ghiChu == null ? "" : km.ghiChu);
                cboTrangThai.setSelectedItem(STATUS_NGUNG_AP_DUNG.equals(km.trangThaiDB) ? STATUS_NGUNG_AP_DUNG : STATUS_DANG_AP_DUNG);
            } else {
                txtDieuKien.setText("0");
            }

            addRow(gbc, 0, "Tên khuyến mãi:", txtTen);
            addRow(gbc, 1, "Loại khuyến mãi:", cboLoai);
            addRow(gbc, 2, "Giá trị:", txtGiaTri);
            addRow(gbc, 3, "Đối tượng áp dụng:", cboDoiTuong);
            addRow(gbc, 4, "Điều kiện tối thiểu:", txtDieuKien);
            addRow(gbc, 5, "Thời gian bắt đầu:", pickerBatDau);
            addRow(gbc, 6, "Thời gian kết thúc:", pickerKetThuc);
            addRow(gbc, 7, "Trạng thái lưu:", cboTrangThai);

            gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            add(new JLabel("Ghi chú:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            add(new JScrollPane(txtGhiChu), gbc);

            JLabel hint = new JLabel("Ngày bắt đầu khi thêm mới không được nhỏ hơn thời gian hiện tại; ngày kết thúc phải sau ngày bắt đầu.");
            hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
            hint.setForeground(Color.GRAY);
            gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
            add(hint, gbc);
        }

        private void addRow(GridBagConstraints gbc, int y, String label, JComponent comp) {
            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            add(new JLabel(label), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            add(comp, gbc);
        }

        private void selectLoai(String maLoai) {
            for (int i = 0; i < cboLoai.getItemCount(); i++) {
                if (cboLoai.getItemAt(i).maLoai.equals(maLoai)) {
                    cboLoai.setSelectedIndex(i);
                    return;
                }
            }
        }

        boolean validateInput() {
            if (getTenKhuyenMai().isEmpty()) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Tên khuyến mãi không được để trống.");
                return false;
            }
            if (cboLoai.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Bạn cần chọn loại khuyến mãi.");
                return false;
            }
            double giaTri;
            double dieuKien;
            try {
                giaTri = getGiaTri();
                dieuKien = getDieuKienApDung();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Giá trị và điều kiện phải là số.");
                return false;
            }
            if (giaTri <= 0) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Giá trị khuyến mãi phải lớn hơn 0.");
                return false;
            }
            if (dieuKien < 0) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Điều kiện áp dụng không được âm.");
                return false;
            }
            String loai = ((ComboLoaiKM) cboLoai.getSelectedItem()).tenLoai.toLowerCase(Locale.ROOT);
            if ((loai.contains("phần") || loai.contains("thành viên")) && giaTri > 100) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Khuyến mãi phần trăm/thành viên không được vượt quá 100%.");
                return false;
            }
            LocalDateTime bd = getBatDau();
            LocalDateTime kt = getKetThuc();
            if (bd == null || kt == null) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Bạn cần chọn đủ ngày bắt đầu và ngày kết thúc.");
                return false;
            }
            if (themMoi && bd.isBefore(LocalDateTime.now().minusSeconds(2))) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Thời gian bắt đầu không được nhỏ hơn thời gian hiện tại.");
                return false;
            }
            if (!kt.isAfter(bd)) {
                JOptionPane.showMessageDialog(KhuyenMai_GUI.this, "Thời gian kết thúc phải sau thời gian bắt đầu.");
                return false;
            }
            return true;
        }

        String getTenKhuyenMai() { return txtTen.getText().trim(); }
        String getMaLoaiKM() { return ((ComboLoaiKM) cboLoai.getSelectedItem()).maLoai; }
        double getGiaTri() { return parseNumber(txtGiaTri.getText()); }
        String getDoiTuongApDung() { return String.valueOf(cboDoiTuong.getSelectedItem()); }
        double getDieuKienApDung() { return parseNumber(txtDieuKien.getText()); }
        LocalDateTime getBatDau() { return pickerBatDau.getDateTime(); }
        LocalDateTime getKetThuc() { return pickerKetThuc.getDateTime(); }
        String getGhiChu() { return txtGhiChu.getText().trim(); }
        String getTrangThaiMuonLuu() {
            if (STATUS_NGUNG_AP_DUNG.equals(cboTrangThai.getSelectedItem())) return STATUS_NGUNG_AP_DUNG;
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime bd = getBatDau();
            LocalDateTime kt = getKetThuc();
            if (bd != null && now.isBefore(bd)) return STATUS_CHUA_AP_DUNG;
            if (kt != null && now.isAfter(kt)) return STATUS_HET_HAN;
            return STATUS_DANG_AP_DUNG;
        }
    }

    private class DateTimePickerPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private JDateChooser dateChooser;
        private JSpinner spHour;
        private JSpinner spMinute;

        DateTimePickerPanel(LocalDateTime initial, boolean minToday) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
            setOpaque(false);
            if (initial == null) initial = LocalDateTime.now().plusMinutes(5);
            dateChooser = new JDateChooser();
            dateChooser.setDateFormatString("dd/MM/yyyy");
            dateChooser.setPreferredSize(new Dimension(145, 34));
            dateChooser.setDate(Date.from(initial.atZone(ZoneId.systemDefault()).toInstant()));
            if (minToday) {
                LocalDate today = LocalDate.now();
                dateChooser.setMinSelectableDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            spHour = new JSpinner(new SpinnerNumberModel(initial.getHour(), 0, 23, 1));
            spMinute = new JSpinner(new SpinnerNumberModel(initial.getMinute(), 0, 59, 1));
            spHour.setPreferredSize(new Dimension(62, 34));
            spMinute.setPreferredSize(new Dimension(62, 34));
            add(dateChooser);
            add(new JLabel("Giờ:"));
            add(spHour);
            add(new JLabel("Phút:"));
            add(spMinute);
        }

        LocalDateTime getDateTime() {
            Date d = dateChooser.getDate();
            if (d == null) return null;
            LocalDate date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int hour = ((Number) spHour.getValue()).intValue();
            int minute = ((Number) spMinute.getValue()).intValue();
            return LocalDateTime.of(date, LocalTime.of(hour, minute));
        }
    }

    private static double parseNumber(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Double.parseDouble(s.trim().replace(".", "").replace(",", ""));
    }

    private static String removeTrailingZero(double value) {
        if (value == Math.rint(value)) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    private static abstract class SimpleDocumentListener implements DocumentListener {
        public abstract void update(DocumentEvent e);
        @Override public void insertUpdate(DocumentEvent e) { update(e); }
        @Override public void removeUpdate(DocumentEvent e) { update(e); }
        @Override public void changedUpdate(DocumentEvent e) { update(e); }
    }

    private static class ComboLoaiKM {
        String maLoai;
        String tenLoai;
        ComboLoaiKM(String maLoai, String tenLoai) { this.maLoai = maLoai; this.tenLoai = tenLoai; }
        @Override public String toString() { return maLoai + " - " + tenLoai; }
    }

    private static class KhuyenMaiRow {
        String maKM;
        String maLoaiKM;
        String tenLoaiKM;
        String maNV;
        double giaTri;
        String tenKhuyenMai;
        LocalDateTime thoiGianBatDau;
        LocalDateTime thoiGianKetThuc;
        String doiTuongApDung;
        double dieuKienApDung;
        String ghiChu;
        String trangThaiDB;
        String trangThaiHienThi;
    }
}
