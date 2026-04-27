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
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
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
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import entity.TaiKhoan;

public class KhuyenMai_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String STATUS_DANG_AP_DUNG = "Đang áp dụng";
    private static final String STATUS_NGUNG_AP_DUNG = "Ngưng áp dụng";
    private static final String STATUS_CHUA_AP_DUNG = "Chưa áp dụng";
    private static final String STATUS_HET_HAN = "Hết hạn";

    private final TaiKhoan taiKhoanDangNhap;
    private final KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

    private final List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
    private final Map<String, String> loaiKhuyenMaiMap = new LinkedHashMap<>();

    private JTextField txtSearch;
    private JComboBox<String> cboTrangThaiLoc;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTongKM;
    private JLabel lblDangApDung;
    private JLabel lblSapHetHan;
    private JLabel lblNgungApDung;
    private JTextArea txtDetail;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    public KhuyenMai_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        add(createMainPanel(), BorderLayout.CENTER);

        napDuLieuVaRender();
    }

    public KhuyenMai_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(18, 0));
        main.setBackground(new Color(245, 245, 245));
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

        JPanel actionCard = new JPanel(new BorderLayout(0, 10));
        actionCard.setBackground(Color.WHITE);
        actionCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        txtSearch = new JTextField(26);
        txtSearch.setToolTipText("Tìm theo mã, tên khuyến mãi, loại, đối tượng hoặc ghi chú");
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { renderData(); }
            @Override public void removeUpdate(DocumentEvent e) { renderData(); }
            @Override public void changedUpdate(DocumentEvent e) { renderData(); }
        });
        filterRow.add(txtSearch);

        cboTrangThaiLoc = new JComboBox<>(new String[] {
                "Tất cả", STATUS_DANG_AP_DUNG, STATUS_CHUA_AP_DUNG, STATUS_HET_HAN, STATUS_NGUNG_AP_DUNG
        });
        cboTrangThaiLoc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cboTrangThaiLoc.setPreferredSize(new Dimension(170, 36));
        cboTrangThaiLoc.addActionListener(e -> renderData());
        filterRow.add(cboTrangThaiLoc);

        JButton btnRefresh = createActionButton("Làm mới", new Color(245, 235, 220), new Color(120, 90, 70));
        btnRefresh.addActionListener(e -> napDuLieuVaRender());
        filterRow.add(btnRefresh);
        actionCard.add(filterRow, BorderLayout.NORTH);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnAdd = createActionButton("Thêm khuyến mãi", new Color(225, 240, 255), new Color(40, 100, 180));
        btnAdd.addActionListener(e -> moDialogThemKhuyenMai());

        JButton btnEdit = createActionButton("Sửa khuyến mãi", new Color(230, 244, 234), new Color(46, 125, 50));
        btnEdit.addActionListener(e -> moDialogSuaKhuyenMai());

        JButton btnDelete = createActionButton("Xóa / Ngưng áp dụng", new Color(255, 235, 238), new Color(198, 40, 40));
        btnDelete.addActionListener(e -> xuLyXoaKhuyenMai());

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
                "Mã KM", "Tên khuyến mãi", "Loại", "Giá trị", "Đối tượng", "Điều kiện", "Bắt đầu", "Kết thúc", "Trạng thái"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(44);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(new Color(235, 244, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getSelectionModel().addListSelectionListener(e -> capNhatChiTietKhuyenMai());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    moDialogSuaKhuyenMai();
                }
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
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
        table.getColumnModel().getColumn(7).setCellRenderer(center);
        table.getColumnModel().getColumn(8).setCellRenderer(center);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(135);
        table.getColumnModel().getColumn(7).setPreferredWidth(135);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(new Color(235, 235, 235), 1));
        card.add(scrollPane, BorderLayout.CENTER);

        JLabel hint = new JLabel("Gợi ý: Khuyến mãi đã dùng trong hóa đơn sẽ được ngưng áp dụng thay vì xóa khỏi CSDL.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 13));
        hint.setForeground(new Color(120, 120, 120));
        card.add(hint, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(370, 0));

        JPanel statsCard = createCard();
        statsCard.setLayout(new GridLayout(2, 2, 12, 12));
        lblTongKM = createMetricLabel("0");
        lblDangApDung = createMetricLabel("0");
        lblSapHetHan = createMetricLabel("0");
        lblNgungApDung = createMetricLabel("0");
        statsCard.add(createMetricPanel("Tổng KM", lblTongKM, new Color(245, 248, 255)));
        statsCard.add(createMetricPanel("Đang áp dụng", lblDangApDung, new Color(235, 248, 239)));
        statsCard.add(createMetricPanel("Sắp hết hạn", lblSapHetHan, new Color(255, 248, 230)));
        statsCard.add(createMetricPanel("Ngưng/Hết hạn", lblNgungApDung, new Color(255, 239, 239)));

        JPanel detailCard = createCard();
        detailCard.setLayout(new BorderLayout(0, 10));
        JLabel title = new JLabel("Chi tiết khuyến mãi");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        detailCard.add(title, BorderLayout.NORTH);
        txtDetail = new JTextArea(14, 20);
        txtDetail.setEditable(false);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        txtDetail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDetail.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        txtDetail.setText("Chọn một khuyến mãi trong bảng để xem chi tiết.");
        detailCard.add(new JScrollPane(txtDetail), BorderLayout.CENTER);

        JPanel noteCard = createCard();
        noteCard.setLayout(new BoxLayout(noteCard, BoxLayout.Y_AXIS));
        JLabel noteTitle = new JLabel("Quy tắc xử lý");
        noteTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        noteCard.add(noteTitle);
        noteCard.add(Box.createRigidArea(new Dimension(0, 8)));
        noteCard.add(createGuideLabel("• Giảm phần trăm không vượt quá 100%."));
        noteCard.add(Box.createRigidArea(new Dimension(0, 6)));
        noteCard.add(createGuideLabel("• Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu."));
        noteCard.add(Box.createRigidArea(new Dimension(0, 6)));
        noteCard.add(createGuideLabel("• Khuyến mãi đã dùng trong hóa đơn không xóa thật, chỉ ngưng áp dụng."));

        right.add(statsCard);
        right.add(Box.createRigidArea(new Dimension(0, 16)));
        right.add(detailCard);
        right.add(Box.createRigidArea(new Dimension(0, 16)));
        right.add(noteCard);
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
        JLabel label = new JLabel("<html><div style='width:300px; line-height:1.5; color:#555555;'>" + text + "</div></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
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
        loaiKhuyenMaiMap.clear();
        napLoaiKhuyenMai();

        dsKhuyenMai.clear();
        for (Object obj : khuyenMaiDAO.getAllKhuyenMai()) {
            if (obj instanceof KhuyenMai) {
                dsKhuyenMai.add((KhuyenMai) obj);
            }
        }
        renderData();
    }

    private void napLoaiKhuyenMai() {
        String sql = "SELECT maLoaiKM, tenLoaiKM FROM LoaiKhuyenMai ORDER BY maLoaiKM";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loaiKhuyenMaiMap.put(rs.getString("maLoaiKM"), rs.getString("tenLoaiKM"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            loaiKhuyenMaiMap.put("LKM01", "Phần trăm");
            loaiKhuyenMaiMap.put("LKM02", "Số tiền");
            loaiKhuyenMaiMap.put("LKM03", "Thành viên");
        }
    }

    private void renderData() {
        tableModel.setRowCount(0);
        for (KhuyenMai km : dsKhuyenMai) {
            if (!phuHopBoLoc(km)) continue;
            tableModel.addRow(new Object[] {
                    km.getMaKM(),
                    km.getTenKhuyenMai(),
                    layTenLoaiKM(km),
                    formatGiaTri(km),
                    safe(km.getDoiTuongApDung(), "-"),
                    formatTien(km.getDieuKienApDung()),
                    formatDate(km.getThoiGianBatDau()),
                    formatDate(km.getThoiGianKetThuc()),
                    layTrangThaiHienThi(km)
            });
        }
        capNhatThongKe();
        capNhatChiTietKhuyenMai();
    }

    private boolean phuHopBoLoc(KhuyenMai km) {
        String keyword = normalize(txtSearch == null ? "" : txtSearch.getText());
        String trangThaiLoc = Objects.toString(cboTrangThaiLoc == null ? "Tất cả" : cboTrangThaiLoc.getSelectedItem(), "Tất cả");
        String trangThai = layTrangThaiHienThi(km);
        if (!"Tất cả".equalsIgnoreCase(trangThaiLoc) && !trangThaiLoc.equalsIgnoreCase(trangThai)) {
            return false;
        }
        if (keyword.isEmpty()) return true;
        return normalize(km.getMaKM()).contains(keyword)
                || normalize(km.getTenKhuyenMai()).contains(keyword)
                || normalize(layTenLoaiKM(km)).contains(keyword)
                || normalize(km.getDoiTuongApDung()).contains(keyword)
                || normalize(km.getGhiChu()).contains(keyword)
                || normalize(trangThai).contains(keyword);
    }

    private void capNhatThongKe() {
        int tong = 0;
        int dang = 0;
        int sapHetHan = 0;
        int ngungHoacHetHan = 0;
        LocalDateTime now = LocalDateTime.now();
        for (KhuyenMai km : dsKhuyenMai) {
            if (!phuHopBoLoc(km)) continue;
            tong++;
            String status = layTrangThaiHienThi(km);
            if (STATUS_DANG_AP_DUNG.equalsIgnoreCase(status)) dang++;
            if (km.getThoiGianKetThuc() != null
                    && !km.getThoiGianKetThuc().isBefore(now)
                    && km.getThoiGianKetThuc().isBefore(now.plusDays(7))) {
                sapHetHan++;
            }
            if (STATUS_NGUNG_AP_DUNG.equalsIgnoreCase(status) || STATUS_HET_HAN.equalsIgnoreCase(status)) {
                ngungHoacHetHan++;
            }
        }
        lblTongKM.setText(String.valueOf(tong));
        lblDangApDung.setText(String.valueOf(dang));
        lblSapHetHan.setText(String.valueOf(sapHetHan));
        lblNgungApDung.setText(String.valueOf(ngungHoacHetHan));
    }

    private void capNhatChiTietKhuyenMai() {
        KhuyenMai km = layKhuyenMaiDangChon();
        if (km == null) {
            txtDetail.setText("Chọn một khuyến mãi trong bảng để xem chi tiết.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Mã KM: ").append(km.getMaKM());
        sb.append("\nTên: ").append(km.getTenKhuyenMai());
        sb.append("\nLoại: ").append(layTenLoaiKM(km));
        sb.append("\nGiá trị: ").append(formatGiaTri(km));
        sb.append("\nĐối tượng: ").append(safe(km.getDoiTuongApDung(), "-"));
        sb.append("\nĐiều kiện áp dụng: ").append(formatTien(km.getDieuKienApDung()));
        sb.append("\nBắt đầu: ").append(formatDate(km.getThoiGianBatDau()));
        sb.append("\nKết thúc: ").append(formatDate(km.getThoiGianKetThuc()));
        sb.append("\nTrạng thái lưu: ").append(safe(km.getTrangThai(), "-"));
        sb.append("\nTrạng thái hiển thị: ").append(layTrangThaiHienThi(km));
        sb.append("\nSố hóa đơn đã dùng: ").append(demHoaDonDungKM(km.getMaKM()));
        sb.append("\n\nGhi chú:\n").append(safe(km.getGhiChu(), "Không có ghi chú."));
        txtDetail.setText(sb.toString());
    }

    private void moDialogThemKhuyenMai() {
        KhuyenMaiForm form = new KhuyenMaiForm(null);
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, form.panel, "Thêm khuyến mãi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;
            KhuyenMaiData data = form.layDuLieu();
            String loi = kiemTraDuLieu(data);
            if (loi != null) {
                thongBao(loi);
                continue;
            }
            if (themKhuyenMai(data)) {
                thongBao("Đã thêm khuyến mãi " + data.maKM + ".");
                napDuLieuVaRender();
                chonDongTheoMa(data.maKM);
                return;
            }
            thongBaoLoi("Thêm khuyến mãi thất bại.");
            return;
        }
    }

    private void moDialogSuaKhuyenMai() {
        KhuyenMai km = layKhuyenMaiDangChon();
        if (km == null) {
            thongBao("Vui lòng chọn một khuyến mãi trước.");
            return;
        }
        KhuyenMaiForm form = new KhuyenMaiForm(km);
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, form.panel, "Sửa khuyến mãi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;
            KhuyenMaiData data = form.layDuLieu();
            String loi = kiemTraDuLieu(data);
            if (loi != null) {
                thongBao(loi);
                continue;
            }
            if (capNhatKhuyenMai(data)) {
                thongBao("Đã cập nhật khuyến mãi " + data.maKM + ".");
                napDuLieuVaRender();
                chonDongTheoMa(data.maKM);
                return;
            }
            thongBaoLoi("Cập nhật khuyến mãi thất bại.");
            return;
        }
    }

    private void xuLyXoaKhuyenMai() {
        KhuyenMai km = layKhuyenMaiDangChon();
        if (km == null) {
            thongBao("Vui lòng chọn một khuyến mãi trước.");
            return;
        }
        int soHD = demHoaDonDungKM(km.getMaKM());
        String msg;
        if (soHD > 0) {
            msg = "Khuyến mãi này đã được dùng trong " + soHD + " hóa đơn.\n"
                    + "Hệ thống sẽ không xóa khỏi CSDL mà chuyển sang trạng thái Ngưng áp dụng.\n\n"
                    + "Bạn có muốn tiếp tục không?";
        } else {
            msg = "Bạn có chắc muốn xóa khuyến mãi " + km.getMaKM() + " không?";
        }
        int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = soHD > 0 ? ngungApDungKhuyenMai(km.getMaKM()) : xoaKhuyenMai(km.getMaKM());
        if (ok) {
            thongBao(soHD > 0 ? "Đã ngưng áp dụng khuyến mãi." : "Đã xóa khuyến mãi.");
            napDuLieuVaRender();
        } else {
            thongBaoLoi("Xử lý khuyến mãi thất bại.");
        }
    }

    private String kiemTraDuLieu(KhuyenMaiData data) {
        if (data.tenKhuyenMai.isEmpty()) return "Tên khuyến mãi không được để trống.";
        if (data.maLoaiKM == null || data.maLoaiKM.isEmpty()) return "Vui lòng chọn loại khuyến mãi.";
        if (data.giaTri <= 0) return "Giá trị khuyến mãi phải lớn hơn 0.";
        String tenLoai = loaiKhuyenMaiMap.getOrDefault(data.maLoaiKM, "");
        if ((tenLoai.toLowerCase(Locale.ROOT).contains("phần trăm") || tenLoai.toLowerCase(Locale.ROOT).contains("thành viên"))
                && data.giaTri > 100) {
            return "Khuyến mãi phần trăm/thành viên không được vượt quá 100%.";
        }
        if (data.thoiGianBatDau == null || data.thoiGianKetThuc == null) return "Vui lòng nhập thời gian bắt đầu và kết thúc.";
        if (data.thoiGianKetThuc.isBefore(data.thoiGianBatDau)) return "Thời gian kết thúc phải lớn hơn hoặc bằng thời gian bắt đầu.";
        if (data.dieuKienApDung < 0) return "Điều kiện áp dụng không được âm.";
        return null;
    }

    private boolean themKhuyenMai(KhuyenMaiData d) {
        String sql = "INSERT INTO KhuyenMai(maKM, maLoaiKM, maNV, giaTri, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, doiTuongApDung, dieuKienApDung, ghiChu, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            ganThamSoKhuyenMai(stmt, d, true);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean capNhatKhuyenMai(KhuyenMaiData d) {
        String sql = "UPDATE KhuyenMai SET maLoaiKM = ?, maNV = ?, giaTri = ?, tenKhuyenMai = ?, thoiGianBatDau = ?, thoiGianKetThuc = ?, "
                + "doiTuongApDung = ?, dieuKienApDung = ?, ghiChu = ?, trangThai = ? WHERE maKM = ?";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            ganThamSoKhuyenMai(stmt, d, false);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void ganThamSoKhuyenMai(PreparedStatement stmt, KhuyenMaiData d, boolean insert) throws Exception {
        if (insert) {
            stmt.setString(1, d.maKM);
            stmt.setString(2, d.maLoaiKM);
            stmt.setString(3, d.maNV);
            stmt.setDouble(4, d.giaTri);
            stmt.setString(5, d.tenKhuyenMai);
            stmt.setTimestamp(6, Timestamp.valueOf(d.thoiGianBatDau));
            stmt.setTimestamp(7, Timestamp.valueOf(d.thoiGianKetThuc));
            stmt.setString(8, nullIfBlank(d.doiTuongApDung));
            stmt.setDouble(9, d.dieuKienApDung);
            stmt.setString(10, nullIfBlank(d.ghiChu));
            stmt.setString(11, d.trangThai);
        } else {
            stmt.setString(1, d.maLoaiKM);
            stmt.setString(2, d.maNV);
            stmt.setDouble(3, d.giaTri);
            stmt.setString(4, d.tenKhuyenMai);
            stmt.setTimestamp(5, Timestamp.valueOf(d.thoiGianBatDau));
            stmt.setTimestamp(6, Timestamp.valueOf(d.thoiGianKetThuc));
            stmt.setString(7, nullIfBlank(d.doiTuongApDung));
            stmt.setDouble(8, d.dieuKienApDung);
            stmt.setString(9, nullIfBlank(d.ghiChu));
            stmt.setString(10, d.trangThai);
            stmt.setString(11, d.maKM);
        }
    }

    private boolean xoaKhuyenMai(String maKM) {
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement("DELETE FROM KhuyenMai WHERE maKM = ?")) {
            stmt.setString(1, maKM);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean ngungApDungKhuyenMai(String maKM) {
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement("UPDATE KhuyenMai SET trangThai = N'Ngưng áp dụng' WHERE maKM = ?")) {
            stmt.setString(1, maKM);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int demHoaDonDungKM(String maKM) {
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement("SELECT COUNT(*) FROM HoaDon WHERE maKM = ?")) {
            stmt.setString(1, maKM);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private KhuyenMai layKhuyenMaiDangChon() {
        int row = table == null ? -1 : table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        String ma = Objects.toString(tableModel.getValueAt(modelRow, 0), "");
        for (KhuyenMai km : dsKhuyenMai) {
            if (Objects.equals(km.getMaKM(), ma)) return km;
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

    private String taoMaKhuyenMaiMoi() {
        int max = 0;
        Pattern pattern = Pattern.compile("(\\d+)");
        for (KhuyenMai km : dsKhuyenMai) {
            Matcher matcher = pattern.matcher(safe(km.getMaKM(), ""));
            while (matcher.find()) {
                try { max = Math.max(max, Integer.parseInt(matcher.group(1))); } catch (Exception ignored) {}
            }
        }
        return "KM" + String.format("%03d", max + 1);
    }

    private String layMaNhanVienHienTai() {
        try {
            if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null
                    && taiKhoanDangNhap.getMaNV().getMaNV() != null) {
                return taiKhoanDangNhap.getMaNV().getMaNV();
            }
        } catch (Exception ignored) {}

        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement("SELECT TOP 1 maNV FROM NhanVien ORDER BY maNV");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NV001";
    }

    private String layTenLoaiKM(KhuyenMai km) {
        if (km == null || km.getMaLoaiKM() == null) return "-";
        String maLoai = km.getMaLoaiKM().getMaLoaiKM();
        String tenLoai = km.getMaLoaiKM().getTenLoaiKM();
        if (tenLoai != null && !tenLoai.trim().isEmpty()) return tenLoai;
        return loaiKhuyenMaiMap.getOrDefault(maLoai, maLoai);
    }

    private String layTrangThaiHienThi(KhuyenMai km) {
        if (km == null) return "-";
        String stored = safe(km.getTrangThai(), STATUS_DANG_AP_DUNG).trim();
        if (STATUS_NGUNG_AP_DUNG.equalsIgnoreCase(stored)) return STATUS_NGUNG_AP_DUNG;
        LocalDateTime now = LocalDateTime.now();
        if (km.getThoiGianBatDau() != null && now.isBefore(km.getThoiGianBatDau())) return STATUS_CHUA_AP_DUNG;
        if (km.getThoiGianKetThuc() != null && now.isAfter(km.getThoiGianKetThuc())) return STATUS_HET_HAN;
        return stored.isEmpty() ? STATUS_DANG_AP_DUNG : stored;
    }

    private String formatGiaTri(KhuyenMai km) {
        String tenLoai = layTenLoaiKM(km).toLowerCase(Locale.ROOT);
        if (tenLoai.contains("phần trăm") || tenLoai.contains("thành viên")) {
            return removeTrailingZero(km.getGiaTri()) + "%";
        }
        return formatTien(km.getGiaTri());
    }

    private String formatTien(double value) {
        if (value <= 0) return "0";
        return moneyFormat.format(value) + " đ";
    }

    private String removeTrailingZero(double value) {
        if (value == Math.rint(value)) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dtf.format(dateTime);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(650, 520));
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
        lbl.setPreferredSize(new Dimension(145, 28));
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
        if (comp instanceof JScrollPane) {
            comp.setPreferredSize(new Dimension(430, 90));
        } else {
            comp.setPreferredSize(new Dimension(430, 30));
        }
        panel.add(comp, gbcComp);
    }

    private JLabel createPreviewLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(40, 100, 180));
        return label;
    }

    private JTextArea createDialogTextArea() {
        JTextArea area = new JTextArea(4, 24);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(6, 6, 6, 6));
        return area;
    }

    private Date toDate(LocalDateTime ldt) {
        LocalDateTime value = ldt == null ? LocalDateTime.now() : ldt;
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private ComboItem<String>[] taoDanhSachLoaiKM() {
        List<ComboItem<String>> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : loaiKhuyenMaiMap.entrySet()) {
            items.add(new ComboItem<>(entry.getValue(), entry.getKey()));
        }
        @SuppressWarnings("unchecked")
        ComboItem<String>[] arr = items.toArray(new ComboItem[0]);
        return arr;
    }

    private void chonLoaiKM(JComboBox<ComboItem<String>> combo, String maLoai) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (Objects.equals(combo.getItemAt(i).getValue(), maLoai)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String normalize(String value) {
        return safe(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private String safe(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private String nullIfBlank(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void thongBaoLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static class ComboItem<T> {
        private final String label;
        private final T value;
        ComboItem(String label, T value) { this.label = label; this.value = value; }
        public String getLabel() { return label; }
        public T getValue() { return value; }
        @Override public String toString() { return label; }
    }

    private static class ComboRenderer<T> extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ComboItem) {
                @SuppressWarnings("unchecked")
                ComboItem<T> item = (ComboItem<T>) value;
                setText(item.getLabel());
            }
            return this;
        }
    }

    private static class KhuyenMaiData {
        String maKM;
        String maLoaiKM;
        String maNV;
        double giaTri;
        String tenKhuyenMai;
        LocalDateTime thoiGianBatDau;
        LocalDateTime thoiGianKetThuc;
        String doiTuongApDung;
        double dieuKienApDung;
        String ghiChu;
        String trangThai;
    }

    private class KhuyenMaiForm {
        JPanel panel;
        JTextField txtMa;
        JTextField txtTen;
        JComboBox<ComboItem<String>> cboLoai;
        JSpinner spnGiaTri;
        JComboBox<String> cboDoiTuong;
        JSpinner spnDieuKien;
        JSpinner spnBatDau;
        JSpinner spnKetThuc;
        JComboBox<String> cboTrangThai;
        JTextArea txtGhiChu;

        KhuyenMaiForm(KhuyenMai km) {
            txtMa = new JTextField(km == null ? taoMaKhuyenMaiMoi() : km.getMaKM());
            txtMa.setEditable(false);

            txtTen = new JTextField(km == null ? "" : safe(km.getTenKhuyenMai(), ""));
            cboLoai = new JComboBox<>(taoDanhSachLoaiKM());
            cboLoai.setRenderer(new ComboRenderer<>());
            if (km != null && km.getMaLoaiKM() != null) chonLoaiKM(cboLoai, km.getMaLoaiKM().getMaLoaiKM());

            spnGiaTri = new JSpinner(new SpinnerNumberModel(km == null ? 10.0 : km.getGiaTri(), 0.0, 999999999.0, 1.0));
            cboDoiTuong = new JComboBox<>(new String[] { "Tất cả KH", "Thường", "Vàng", "Kim cương" });
            if (km != null && km.getDoiTuongApDung() != null) cboDoiTuong.setSelectedItem(km.getDoiTuongApDung());
            spnDieuKien = new JSpinner(new SpinnerNumberModel(km == null ? 0.0 : km.getDieuKienApDung(), 0.0, 999999999.0, 10000.0));

            spnBatDau = new JSpinner(new SpinnerDateModel(toDate(km == null ? LocalDateTime.now() : km.getThoiGianBatDau()), null, null, java.util.Calendar.MINUTE));
            spnBatDau.setEditor(new JSpinner.DateEditor(spnBatDau, "dd/MM/yyyy HH:mm"));
            spnKetThuc = new JSpinner(new SpinnerDateModel(toDate(km == null ? LocalDateTime.now().plusMonths(1) : km.getThoiGianKetThuc()), null, null, java.util.Calendar.MINUTE));
            spnKetThuc.setEditor(new JSpinner.DateEditor(spnKetThuc, "dd/MM/yyyy HH:mm"));

            cboTrangThai = new JComboBox<>(new String[] { STATUS_DANG_AP_DUNG, STATUS_NGUNG_AP_DUNG });
            cboTrangThai.setSelectedItem(km == null ? STATUS_DANG_AP_DUNG : safe(km.getTrangThai(), STATUS_DANG_AP_DUNG));

            txtGhiChu = createDialogTextArea();
            txtGhiChu.setText(km == null ? "" : safe(km.getGhiChu(), ""));

            panel = createFormPanel();
            addFormRow(panel, 0, "Mã khuyến mãi", txtMa);
            addFormRow(panel, 1, "Tên khuyến mãi", txtTen);
            addFormRow(panel, 2, "Loại khuyến mãi", cboLoai);
            addFormRow(panel, 3, "Giá trị", spnGiaTri);
            addFormRow(panel, 4, "Đối tượng áp dụng", cboDoiTuong);
            addFormRow(panel, 5, "Điều kiện hóa đơn", spnDieuKien);
            addFormRow(panel, 6, "Thời gian bắt đầu", spnBatDau);
            addFormRow(panel, 7, "Thời gian kết thúc", spnKetThuc);
            addFormRow(panel, 8, "Trạng thái", cboTrangThai);
            addFormRow(panel, 9, "Ghi chú", new JScrollPane(txtGhiChu));
        }

        KhuyenMaiData layDuLieu() {
            KhuyenMaiData d = new KhuyenMaiData();
            d.maKM = txtMa.getText().trim();
            @SuppressWarnings("unchecked")
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoai.getSelectedItem();
            d.maLoaiKM = itemLoai == null ? null : itemLoai.getValue();
            d.maNV = layMaNhanVienHienTai();
            d.giaTri = ((Number) spnGiaTri.getValue()).doubleValue();
            d.tenKhuyenMai = txtTen.getText().trim();
            d.thoiGianBatDau = toLocalDateTime((Date) spnBatDau.getValue());
            d.thoiGianKetThuc = toLocalDateTime((Date) spnKetThuc.getValue());
            d.doiTuongApDung = Objects.toString(cboDoiTuong.getSelectedItem(), "Tất cả KH");
            d.dieuKienApDung = ((Number) spnDieuKien.getValue()).doubleValue();
            d.ghiChu = txtGhiChu.getText();
            d.trangThai = Objects.toString(cboTrangThai.getSelectedItem(), STATUS_DANG_AP_DUNG);
            return d;
        }
    }
}
