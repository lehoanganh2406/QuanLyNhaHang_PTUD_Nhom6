package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

import connectDB.ConnectDB;
import dao.KhuVuc_DAO;
import entity.KhuVuc;
import entity.TaiKhoan;

public class KhuVuc_GUI extends JPanel {

    private static final String STATUS_HOAT_DONG = "Hoạt động";
    private static final String STATUS_NGUNG_HOAT_DONG = "Ngưng hoạt động";
    private static final String PLACEHOLDER_SEARCH = "Mã / tên / ký hiệu / trạng thái...";

    private final TaiKhoan taiKhoanDangNhap;
    private final KhuVuc_DAO khuVucDAO = new KhuVuc_DAO();

    private final List<KhuVuc> dsKhuVuc = new ArrayList<>();
    private final Map<String, Integer> soBanTheoKhuVuc = new LinkedHashMap<>();
    private final Map<String, Integer> soBanDangDungTheoKhuVuc = new LinkedHashMap<>();

    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTongKhuVuc;
    private JLabel lblTongBan;
    private JLabel lblTongSucChua;
    private JLabel lblNgungHoatDong;
    private JTextArea txtDetail;

    public KhuVuc_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        add(createMainPanel(), BorderLayout.CENTER);

        napDuLieuVaRender();
    }

    public KhuVuc_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(18, 0));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        mainPanel.add(createLeftPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);
        return mainPanel;
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

        JLabel title = new JLabel("Quản lý khu vực");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        outer.add(title, BorderLayout.NORTH);

        JPanel actionCard = new JPanel(new BorderLayout(0, 10));
        actionCard.setBackground(Color.WHITE);
        actionCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm khu vực:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSearch.setForeground(new Color(80, 80, 80));
        filterRow.add(lblSearch);

        txtSearch = createPlaceholderTextField(PLACEHOLDER_SEARCH, 28);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { renderData(); }
            @Override public void removeUpdate(DocumentEvent e) { renderData(); }
            @Override public void changedUpdate(DocumentEvent e) { renderData(); }
        });
        filterRow.add(txtSearch);

        JButton btnRefresh = createActionButton("Làm mới", new Color(245, 235, 220), new Color(120, 90, 70));
        btnRefresh.addActionListener(e -> napDuLieuVaRender());
        filterRow.add(btnRefresh);
        actionCard.add(filterRow, BorderLayout.NORTH);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnAdd = createActionButton("Thêm khu vực", new Color(225, 240, 255), new Color(40, 100, 180));
        btnAdd.addActionListener(e -> moDialogThemKhuVuc());

        JButton btnEdit = createActionButton("Sửa khu vực", new Color(230, 244, 234), new Color(46, 125, 50));
        btnEdit.addActionListener(e -> moDialogSuaKhuVuc());

        JButton btnDelete = createActionButton("Xóa khu vực", new Color(255, 235, 238), new Color(198, 40, 40));
        btnDelete.addActionListener(e -> xuLyXoaKhuVuc());

        buttonRow.add(btnAdd);
        buttonRow.add(btnEdit);
        buttonRow.add(btnDelete);
        actionCard.add(buttonRow, BorderLayout.CENTER);

        outer.add(actionCard, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createTablePanel() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(14, 14, 14, 14)));

        String[] columns = {
                "Mã khu vực", "Tên khu vực", "Ký hiệu", "Sức chứa", "Số bàn hiện có", "Bàn đang dùng", "Trạng thái"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(new Color(205, 205, 205));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(235, 244, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getSelectionModel().addListSelectionListener(e -> capNhatChiTietKhuVuc());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    moDialogSuaKhuVuc();
                }
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

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(new Color(235, 235, 235), 1));
        card.add(scrollPane, BorderLayout.CENTER);

        JLabel hint = new JLabel("Gợi ý: Không xóa khu vực đã có bàn. Không giảm sức chứa nhỏ hơn số bàn hiện có.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 13));
        hint.setForeground(new Color(120, 120, 120));
        card.add(hint, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(360, 0));

        JPanel statsCard = createCard();
        statsCard.setLayout(new GridLayout(2, 2, 12, 12));
        lblTongKhuVuc = createMetricLabel("0");
        lblTongBan = createMetricLabel("0");
        lblTongSucChua = createMetricLabel("0");
        lblNgungHoatDong = createMetricLabel("0");
        statsCard.add(createMetricPanel("Tổng khu vực", lblTongKhuVuc, new Color(245, 248, 255)));
        statsCard.add(createMetricPanel("Tổng bàn", lblTongBan, new Color(235, 248, 239)));
        statsCard.add(createMetricPanel("Tổng sức chứa", lblTongSucChua, new Color(255, 248, 230)));
        statsCard.add(createMetricPanel("Ngưng hoạt động", lblNgungHoatDong, new Color(255, 239, 239)));

        JPanel detailCard = createCard();
        detailCard.setLayout(new BorderLayout(0, 10));
        JLabel title = new JLabel("Chi tiết khu vực");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        detailCard.add(title, BorderLayout.NORTH);
        txtDetail = new JTextArea(12, 20);
        txtDetail.setEditable(false);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        txtDetail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDetail.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        txtDetail.setText("Chọn một khu vực trong bảng để xem chi tiết.");
        detailCard.add(new JScrollPane(txtDetail), BorderLayout.CENTER);

        

        right.add(statsCard);
        right.add(Box.createRigidArea(new Dimension(0, 16)));
        right.add(detailCard);
        right.add(Box.createRigidArea(new Dimension(0, 16)));

        right.add(Box.createVerticalGlue());
        return right;
    }

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    private JPanel createMetricPanel(String title, JLabel valueLabel, Color bg) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(true);
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(14, 14, 14, 14)));
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

    private JLabel createGuideLabel(String text) {
        JLabel label = new JLabel("<html><div style='width:290px; line-height:1.5; color:#555555;'>" + text + "</div></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private JTextField createPlaceholderTextField(String placeholder, int columns) {
        JTextField tf = new JTextField(columns) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, insets.left, y);
                    g2.dispose();
                }
            }
        };
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setForeground(new Color(45, 45, 45));
        tf.setCaretColor(new Color(40, 100, 180));
        tf.setToolTipText(placeholder);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(Math.max(260, columns * 12), 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        return tf;
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fg.brighter(), 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        return button;
    }

    private void napDuLieuVaRender() {
        dsKhuVuc.clear();
        soBanTheoKhuVuc.clear();
        soBanDangDungTheoKhuVuc.clear();

        for (Object obj : khuVucDAO.getAllKhuVuc()) {
            if (obj instanceof KhuVuc) {
                KhuVuc kv = (KhuVuc) obj;
                dsKhuVuc.add(kv);
                soBanTheoKhuVuc.put(kv.getMaKhuVuc(), demBanTheoKhuVuc(kv.getMaKhuVuc()));
                soBanDangDungTheoKhuVuc.put(kv.getMaKhuVuc(), demBanDangDungTheoKhuVuc(kv.getMaKhuVuc()));
            }
        }
        dsKhuVuc.sort(Comparator.comparing(KhuVuc::getMaKhuVuc, Comparator.nullsLast(this::compareMaTuNhien)));
        renderData();
    }

    private void renderData() {
        tableModel.setRowCount(0);
        for (KhuVuc kv : dsKhuVuc) {
            if (!phuHopTimKiem(kv)) {
                continue;
            }
            String ma = kv.getMaKhuVuc();
            int soBan = soBanTheoKhuVuc.getOrDefault(ma, 0);
            int dangDung = soBanDangDungTheoKhuVuc.getOrDefault(ma, 0);
            tableModel.addRow(new Object[] {
                    ma,
                    kv.getTenKhuVuc(),
                    safe(kv.getKyHieu(), "-"),
                    kv.getSoLuongBan(),
                    soBan,
                    dangDung,
                    chuanHoaTrangThai(kv.getTrangThai())
            });
        }
        capNhatThongKe();
        capNhatChiTietKhuVuc();
    }

    private boolean phuHopTimKiem(KhuVuc kv) {
        String raw = txtSearch == null ? "" : txtSearch.getText();
        if (raw != null && raw.trim().equals(PLACEHOLDER_SEARCH)) raw = "";
        String keyword = normalize(raw);
        if (keyword.isEmpty()) return true;
        return normalize(kv.getMaKhuVuc()).contains(keyword)
                || normalize(kv.getTenKhuVuc()).contains(keyword)
                || normalize(kv.getKyHieu()).contains(keyword)
                || normalize(kv.getTrangThai()).contains(keyword);
    }

    private void capNhatThongKe() {
        int tongKV = 0;
        int tongBan = 0;
        int tongSucChua = 0;
        int ngung = 0;
        for (KhuVuc kv : dsKhuVuc) {
            if (!phuHopTimKiem(kv)) continue;
            tongKV++;
            tongBan += soBanTheoKhuVuc.getOrDefault(kv.getMaKhuVuc(), 0);
            tongSucChua += kv.getSoLuongBan();
            if (STATUS_NGUNG_HOAT_DONG.equalsIgnoreCase(chuanHoaTrangThai(kv.getTrangThai()))) {
                ngung++;
            }
        }
        lblTongKhuVuc.setText(String.valueOf(tongKV));
        lblTongBan.setText(String.valueOf(tongBan));
        lblTongSucChua.setText(String.valueOf(tongSucChua));
        lblNgungHoatDong.setText(String.valueOf(ngung));
    }

    private void capNhatChiTietKhuVuc() {
        KhuVuc kv = layKhuVucDangChon();
        if (kv == null) {
            txtDetail.setText("Chọn một khu vực trong bảng để xem chi tiết.");
            return;
        }
        int soBan = soBanTheoKhuVuc.getOrDefault(kv.getMaKhuVuc(), 0);
        int dangDung = soBanDangDungTheoKhuVuc.getOrDefault(kv.getMaKhuVuc(), 0);
        StringBuilder sb = new StringBuilder();
        sb.append("Mã khu vực: ").append(kv.getMaKhuVuc());
        sb.append("\nTên khu vực: ").append(kv.getTenKhuVuc());
        sb.append("\nKý hiệu: ").append(safe(kv.getKyHieu(), "-"));
        sb.append("\nSức chứa cấu hình: ").append(kv.getSoLuongBan()).append(" bàn");
        sb.append("\nSố bàn hiện có: ").append(soBan).append(" bàn");
        sb.append("\nBàn đang phục vụ/đặt hôm nay: ").append(dangDung).append(" bàn");
        sb.append("\nTrạng thái: ").append(chuanHoaTrangThai(kv.getTrangThai()));
        if (soBan > kv.getSoLuongBan()) {
            sb.append("\n\nCảnh báo: Số bàn hiện có đang vượt sức chứa cấu hình.");
        }
        txtDetail.setText(sb.toString());
    }

    private void moDialogThemKhuVuc() {
        JTextField txtMa = new JTextField(taoMaKhuVucMoi());
        txtMa.setEditable(false);
        JTextField txtTen = new JTextField();
        JSpinner spnSoBan = new JSpinner(new SpinnerNumberModel(10, 0, 200, 1));
        JTextField txtKyHieu = new JTextField();
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_HOAT_DONG, STATUS_NGUNG_HOAT_DONG });

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Mã khu vực", txtMa);
        addFormRow(panel, 1, "Tên khu vực", txtTen);
        addFormRow(panel, 2, "Ký hiệu", txtKyHieu);
        addFormRow(panel, 3, "Sức chứa bàn", spnSoBan);
        addFormRow(panel, 4, "Trạng thái", cboTrangThai);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Thêm khu vực", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            String kyHieu = txtKyHieu.getText().trim().toUpperCase(Locale.ROOT);
            int soBan = ((Number) spnSoBan.getValue()).intValue();
            String trangThai = Objects.toString(cboTrangThai.getSelectedItem(), STATUS_HOAT_DONG);

            if (ten.isEmpty()) {
                thongBao("Tên khu vực không được để trống.");
                continue;
            }
            if (kyHieu.isEmpty()) {
                thongBao("Ký hiệu khu vực không được để trống.");
                continue;
            }
            if (!kyHieu.matches("[A-Z0-9]{1,5}")) {
                thongBao("Ký hiệu chỉ nên gồm chữ/số, tối đa 5 ký tự. Ví dụ: A, B, C.");
                continue;
            }
            if (tonTaiTenKhuVuc(ten, null)) {
                thongBao("Tên khu vực đã tồn tại.");
                continue;
            }
            if (tonTaiKyHieu(kyHieu, null)) {
                thongBao("Ký hiệu khu vực đã tồn tại.");
                continue;
            }

            KhuVuc kv = new KhuVuc(ma, ten, soBan, trangThai, kyHieu);
            if (khuVucDAO.themKhuVuc(kv)) {
                thongBao("Đã thêm khu vực " + ten + ".");
                napDuLieuVaRender();
                chonDongTheoMa(ma);
                return;
            }
            thongBaoLoi("Thêm khu vực thất bại. Kiểm tra lại dữ liệu hoặc kết nối CSDL.");
            return;
        }
    }

    private void moDialogSuaKhuVuc() {
        KhuVuc kv = layKhuVucDangChon();
        if (kv == null) {
            thongBao("Vui lòng chọn một khu vực trước.");
            return;
        }
        int soBanHienCo = soBanTheoKhuVuc.getOrDefault(kv.getMaKhuVuc(), 0);
        int soBanDangDung = soBanDangDungTheoKhuVuc.getOrDefault(kv.getMaKhuVuc(), 0);

        JTextField txtMa = new JTextField(kv.getMaKhuVuc());
        txtMa.setEditable(false);
        JTextField txtTen = new JTextField(safe(kv.getTenKhuVuc(), ""));
        JSpinner spnSoBan = new JSpinner(new SpinnerNumberModel(kv.getSoLuongBan(), 0, 200, 1));
        JTextField txtKyHieu = new JTextField(safe(kv.getKyHieu(), ""));
        if (soBanHienCo > 0) {
            txtKyHieu.setEditable(false);
            txtKyHieu.setToolTipText("Khu vực đã có bàn nên không đổi ký hiệu để tránh sai mã bàn.");
        }
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_HOAT_DONG, STATUS_NGUNG_HOAT_DONG });
        cboTrangThai.setSelectedItem(chuanHoaTrangThai(kv.getTrangThai()));

        JLabel lblInfo = createPreviewLabel("Số bàn hiện có: " + soBanHienCo + " | Bàn đang dùng/đặt: " + soBanDangDung);

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Mã khu vực", txtMa);
        addFormRow(panel, 1, "Tên khu vực", txtTen);
        addFormRow(panel, 2, "Ký hiệu", txtKyHieu);
        addFormRow(panel, 3, "Sức chứa bàn", spnSoBan);
        addFormRow(panel, 4, "Trạng thái", cboTrangThai);
        addFormRow(panel, 5, "Thông tin", lblInfo);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Sửa khu vực", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            String ten = txtTen.getText().trim();
            String kyHieu = txtKyHieu.getText().trim().toUpperCase(Locale.ROOT);
            int sucChua = ((Number) spnSoBan.getValue()).intValue();
            String trangThai = Objects.toString(cboTrangThai.getSelectedItem(), STATUS_HOAT_DONG);

            if (ten.isEmpty()) {
                thongBao("Tên khu vực không được để trống.");
                continue;
            }
            if (kyHieu.isEmpty()) {
                thongBao("Ký hiệu khu vực không được để trống.");
                continue;
            }
            if (sucChua < soBanHienCo) {
                thongBao("Không thể giảm sức chứa xuống " + sucChua + " vì khu vực đang có " + soBanHienCo + " bàn.");
                continue;
            }
            if (tonTaiTenKhuVuc(ten, kv.getMaKhuVuc())) {
                thongBao("Tên khu vực đã tồn tại.");
                continue;
            }
            if (soBanHienCo == 0 && tonTaiKyHieu(kyHieu, kv.getMaKhuVuc())) {
                thongBao("Ký hiệu khu vực đã tồn tại.");
                continue;
            }
            if (STATUS_NGUNG_HOAT_DONG.equalsIgnoreCase(trangThai) && soBanDangDung > 0) {
                thongBao("Không thể ngưng hoạt động khu vực đang có bàn phục vụ hoặc đặt hôm nay.");
                continue;
            }

            kv.setTenKhuVuc(ten);
            kv.setSoLuongBan(sucChua);
            kv.setTrangThai(trangThai);
            kv.setKyHieu(kyHieu);

            if (khuVucDAO.capNhatKhuVuc(kv)) {
                thongBao("Đã cập nhật khu vực " + kv.getMaKhuVuc() + ".");
                napDuLieuVaRender();
                chonDongTheoMa(kv.getMaKhuVuc());
                return;
            }
            thongBaoLoi("Cập nhật khu vực thất bại.");
            return;
        }
    }

    private void xuLyXoaKhuVuc() {
        KhuVuc kv = layKhuVucDangChon();
        if (kv == null) {
            thongBao("Vui lòng chọn một khu vực trước.");
            return;
        }
        int soBan = soBanTheoKhuVuc.getOrDefault(kv.getMaKhuVuc(), 0);
        if (soBan > 0) {
            thongBao("Không thể xóa khu vực vì đang có " + soBan + " bàn thuộc khu vực này.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khu vực " + kv.getMaKhuVuc() + " - " + kv.getTenKhuVuc() + " không?",
                "Xác nhận xóa khu vực",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (khuVucDAO.xoaKhuVuc(kv.getMaKhuVuc())) {
            thongBao("Đã xóa khu vực " + kv.getMaKhuVuc() + ".");
            napDuLieuVaRender();
        } else {
            thongBaoLoi("Xóa khu vực thất bại.");
        }
    }

    private KhuVuc layKhuVucDangChon() {
        int row = table == null ? -1 : table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        String ma = Objects.toString(tableModel.getValueAt(modelRow, 0), "");
        for (KhuVuc kv : dsKhuVuc) {
            if (Objects.equals(kv.getMaKhuVuc(), ma)) return kv;
        }
        return null;
    }

    private void chonDongTheoMa(String ma) {
        if (ma == null) return;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (ma.equals(tableModel.getValueAt(i, 0))) {
                int view = table.convertRowIndexToView(i);
                table.setRowSelectionInterval(view, view);
                table.scrollRectToVisible(table.getCellRect(view, 0, true));
                return;
            }
        }
    }

    private int demBanTheoKhuVuc(String maKhuVuc) {
        String sql = "SELECT COUNT(*) FROM Ban WHERE maKhuVuc = ?";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            stmt.setString(1, maKhuVuc);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int demBanDangDungTheoKhuVuc(String maKhuVuc) {

        String sql = ""
                + "SELECT COUNT(DISTINCT b.maBan) "
                + "FROM Ban b "
                + "WHERE b.maKhuVuc = ? "
                + "AND ( "

                // HÓA ĐƠN ĐANG PHỤC VỤ
                + "    EXISTS ( "
                + "        SELECT 1 "
                + "        FROM HoaDon hd "
                + "        JOIN HoaDon_Ban hdb "
                + "            ON hd.maHD = hdb.maHD "
                + "        WHERE hdb.maBan = b.maBan "
                + "          AND hd.thoiGianRa IS NULL "
                + "          AND ( "
                + "                hd.trangThai IS NULL "
                + "                OR hd.trangThai <> N'Đã hủy' "
                + "          ) "
                + "    ) "

                // PHIẾU ĐẶT BÀN
                + "    OR EXISTS ( "
                + "        SELECT 1 "
                + "        FROM PhieuDatBan pdb "
                + "        JOIN PhieuDatBan_Ban pdbb "
                + "            ON pdb.maPhieuDatBan = pdbb.maPhieuDatBan "
                + "        WHERE pdbb.maBan = b.maBan "
                + "          AND CAST(pdb.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) "
                + "          AND ( "
                + "                pdb.trangThai IS NULL "
                + "                OR pdb.trangThai <> N'Đã hủy' "
                + "          ) "
                + "    ) "

                + ")";

        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

            stmt.setString(1, maKhuVuc);

            try (ResultSet rs = stmt.executeQuery()) {

                return rs.next() ? rs.getInt(1) : 0;
            }

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    private String taoMaKhuVucMoi() {
        int max = 0;
        Pattern pattern = Pattern.compile("^KV(\\d+)$", Pattern.CASE_INSENSITIVE);
        for (KhuVuc kv : dsKhuVuc) {
            Matcher matcher = pattern.matcher(safe(kv.getMaKhuVuc(), ""));
            if (matcher.matches()) {
                try { max = Math.max(max, Integer.parseInt(matcher.group(1))); } catch (Exception ignored) {}
            }
        }
        return "KV" + String.format("%02d", max + 1);
    }

    private boolean tonTaiTenKhuVuc(String ten, String boQuaMa) {
        for (KhuVuc kv : dsKhuVuc) {
            if (Objects.equals(kv.getMaKhuVuc(), boQuaMa)) continue;
            if (safe(kv.getTenKhuVuc(), "").trim().equalsIgnoreCase(ten.trim())) return true;
        }
        return false;
    }

    private boolean tonTaiKyHieu(String kyHieu, String boQuaMa) {
        for (KhuVuc kv : dsKhuVuc) {
            if (Objects.equals(kv.getMaKhuVuc(), boQuaMa)) continue;
            if (safe(kv.getKyHieu(), "").trim().equalsIgnoreCase(kyHieu.trim())) return true;
        }
        return false;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(560, 280));
        return panel;
    }

    private void addFormRow(JPanel panel, int row, String label, Component comp) {
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        gbcLabel.anchor = GridBagConstraints.NORTHWEST;
        gbcLabel.insets = new Insets(8, 0, 8, 14);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setPreferredSize(new Dimension(130, 28));
        panel.add(lbl, gbcLabel);

        GridBagConstraints gbcComp = new GridBagConstraints();
        gbcComp.gridx = 1;
        gbcComp.gridy = row;
        gbcComp.weightx = 1;
        gbcComp.fill = GridBagConstraints.HORIZONTAL;
        gbcComp.insets = new Insets(8, 0, 8, 0);
        if (comp instanceof JComponent) {
            ((JComponent) comp).setFont(new Font("SansSerif", Font.PLAIN, 13));
        }
        comp.setPreferredSize(new Dimension(360, 30));
        panel.add(comp, gbcComp);
    }

    private JLabel createPreviewLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(40, 100, 180));
        return label;
    }

    private String chuanHoaTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) return STATUS_HOAT_DONG;
        String value = trangThai.trim();
        if ("Không hoạt động".equalsIgnoreCase(value)) return STATUS_NGUNG_HOAT_DONG;
        if ("Ngừng hoạt động".equalsIgnoreCase(value)) return STATUS_NGUNG_HOAT_DONG;
        if (STATUS_NGUNG_HOAT_DONG.equalsIgnoreCase(value)) return STATUS_NGUNG_HOAT_DONG;
        return STATUS_HOAT_DONG;
    }

    private String normalize(String value) {
        return safe(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private String safe(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private int compareMaTuNhien(String a, String b) {
        return tachSo(a) != tachSo(b) ? Integer.compare(tachSo(a), tachSo(b)) : safe(a, "").compareToIgnoreCase(safe(b, ""));
    }

    private int tachSo(String text) {
        Matcher matcher = Pattern.compile("(\\d+)").matcher(safe(text, ""));
        if (matcher.find()) {
            try { return Integer.parseInt(matcher.group(1)); } catch (Exception ignored) {}
        }
        return 0;
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void thongBaoLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
