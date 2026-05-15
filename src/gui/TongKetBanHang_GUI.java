package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.Stroke;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class TongKetBanHang_GUI extends JPanel {


    @SuppressWarnings("unused")
    private TaiKhoan taiKhoanDangNhap;

    private enum PeriodMode {
        NGAY("Ngày"), TUAN("Tuần"), THANG("Tháng"), NAM("Năm");
        private final String text;
        PeriodMode(String text) { this.text = text; }
        public String getText() { return text; }
    }

    private PeriodMode selectedMode = PeriodMode.TUAN;

    private JLabel lblSubTitle;
    private JLabel lblFilterHint;
    private JLabel lblChartDoanhThuTitle;
    private JLabel lblStatusRange;
    private JLabel lblStatusUpdated;

    private JToggleButton btnNgay;
    private JToggleButton btnTuan;
    private JToggleButton btnThang;
    private JToggleButton btnNam;

    private JPanel filterFieldsPanel;

    private JDateChooser startChooser;
    private JDateChooser endChooser;
    private JComboBox<String> cboFromWeek;
    private JComboBox<String> cboToWeek;
    private JComboBox<String> cboWeekYear;
    private JComboBox<String> cboFromMonth;
    private JComboBox<String> cboToMonth;
    private JComboBox<String> cboMonthYear;
    private JComboBox<String> cboFromYear;
    private JComboBox<String> cboToYear;

    private JLabel lblTongMonBan;
    private JLabel lblTongSoLuong;
    private JLabel lblDoanhThu;
    private JLabel lblBanChayNhat;
    private JLabel lblFooterDong;
    private JLabel lblFooterSL;
    private JLabel lblFooterTien;

    private DefaultTableModel tableModel;
    private MockBarTopMon chartTopMon;
    private MockLineDoanhThu chartDoanhThu;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final WeekFields weekFields = WeekFields.ISO;

    public TongKetBanHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createMainPanel(), BorderLayout.CENTER);

        setDefaultDates();
        refreshFilterFields();
        loadData();
    }

    public TongKetBanHang_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(248, 249, 251));

        JPanel contentContainer = new WidthTrackingPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(22, 32, 20, 32));

        contentContainer.add(wrapInNorth(createHeaderTitlePanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 14)));
        contentContainer.add(wrapInNorth(createFilterPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 22)));
        contentContainer.add(wrapInNorth(createKpiPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 22)));
        contentContainer.add(wrapInNorth(createMidScaleSection()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 14)));
        contentContainer.add(wrapInNorth(createStatusBar()));

        JScrollPane scroll = new JScrollPane(contentContainer);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getViewport().setBackground(new Color(248, 249, 251));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel wrapInNorth(JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(comp, BorderLayout.NORTH);
        return p;
    }

    private JPanel createHeaderTitlePanel() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Tổng kết bán hàng");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));

        lblSubTitle = new JLabel("Tổng hợp món bán, số lượng và doanh thu theo khoảng thời gian");
        lblSubTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubTitle.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(lblSubTitle);
        hdr.add(titleBox, BorderLayout.WEST);
        return hdr;
    }

    private JPanel createFilterPanel() {
        initFilterControls();

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 22, 14, 22)));

        JPanel topRow = new JPanel(new GridBagLayout());
        topRow.setOpaque(false);

        JPanel periodBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        periodBox.setOpaque(false);
        JLabel lblMode = new JLabel("Thống kê theo:  ");
        lblMode.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblMode.setForeground(new Color(30, 41, 59));
        periodBox.add(lblMode);
        periodBox.add(btnNgay);
        periodBox.add(btnTuan);
        periodBox.add(btnThang);
        periodBox.add(btnNam);

        filterFieldsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterFieldsPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topRow.add(periodBox, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topRow.add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        topRow.add(filterFieldsPanel, gbc);

        lblFilterHint = new JLabel(" ");
        lblFilterHint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblFilterHint.setForeground(new Color(100, 116, 139));
        lblFilterHint.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(topRow, BorderLayout.CENTER);
        card.add(lblFilterHint, BorderLayout.SOUTH);
        return card;
    }

    private void initFilterControls() {
        btnNgay = createPeriodButton("☼  Ngày", PeriodMode.NGAY);
        btnTuan = createPeriodButton("▣  Tuần", PeriodMode.TUAN);
        btnThang = createPeriodButton("▦  Tháng", PeriodMode.THANG);
        btnNam = createPeriodButton("▥  Năm", PeriodMode.NAM);

        ButtonGroup group = new ButtonGroup();
        group.add(btnNgay);
        group.add(btnTuan);
        group.add(btnThang);
        group.add(btnNam);
        btnTuan.setSelected(true);

        startChooser = new JDateChooser();
        startChooser.setPreferredSize(new Dimension(140, 34));
        startChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));

        endChooser = new JDateChooser();
        endChooser.setPreferredSize(new Dimension(140, 34));
        endChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));

        cboFromWeek = createCombo(190);
        cboToWeek = createCombo(190);
        cboWeekYear = createCombo(100);
        cboWeekYear.addActionListener(e -> rebuildWeekCombos());

        cboFromMonth = createCombo(130);
        cboToMonth = createCombo(130);
        cboMonthYear = createCombo(100);

        cboFromYear = createCombo(120);
        cboToYear = createCombo(120);

        populateYearCombos();
        populateMonthCombos();
    }

    private JToggleButton createPeriodButton(String text, PeriodMode mode) {
        JToggleButton btn = new JToggleButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.addActionListener(e -> {
            selectedMode = mode;
            refreshFilterFields();
            loadData();
        });
        return btn;
    }

    private JComboBox<String> createCombo(int width) {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setPreferredSize(new Dimension(width, 34));
        cbo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cbo.setBackground(Color.WHITE);
        return cbo;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JButton createFilterButton() {
        JButton btnFilter = new JButton("⟳  Lọc dữ liệu");
        btnFilter.setPreferredSize(new Dimension(138, 40));
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnFilter.setBackground(new Color(250, 235, 215));
        btnFilter.setForeground(new Color(110, 80, 50));
        btnFilter.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 190, 170), 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        btnFilter.setFocusPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.addActionListener(e -> loadData());
        return btnFilter;
    }

    private void populateYearCombos() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear - 5; y <= currentYear + 2; y++) {
            cboWeekYear.addItem(String.valueOf(y));
            cboMonthYear.addItem(String.valueOf(y));
            cboFromYear.addItem(String.valueOf(y));
            cboToYear.addItem(String.valueOf(y));
        }
        cboWeekYear.setSelectedItem(String.valueOf(currentYear));
        cboMonthYear.setSelectedItem(String.valueOf(currentYear));
        cboFromYear.setSelectedItem(String.valueOf(currentYear));
        cboToYear.setSelectedItem(String.valueOf(currentYear));
        rebuildWeekCombos();
    }

    private void populateMonthCombos() {
        for (int m = 1; m <= 12; m++) {
            cboFromMonth.addItem("Tháng " + m);
            cboToMonth.addItem("Tháng " + m);
        }
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        cboFromMonth.setSelectedIndex(Math.max(0, currentMonth - 2));
        cboToMonth.setSelectedIndex(currentMonth - 1);
    }

    private void rebuildWeekCombos() {
        if (cboFromWeek == null || cboToWeek == null || cboWeekYear == null || cboWeekYear.getSelectedItem() == null) return;

        int oldFrom = getWeekNumber(cboFromWeek);
        int oldTo = getWeekNumber(cboToWeek);
        int year = Integer.parseInt(cboWeekYear.getSelectedItem().toString());
        int maxWeek = LocalDate.of(year, 12, 28).get(weekFields.weekOfWeekBasedYear());

        cboFromWeek.removeAllItems();
        cboToWeek.removeAllItems();
        for (int w = 1; w <= maxWeek; w++) {
            LocalDate start = getStartOfIsoWeek(year, w);
            LocalDate end = start.plusDays(6);
            String text = String.format("Tuần %02d (%s - %s)", w, start.format(dateFormat), end.format(dateFormat));
            cboFromWeek.addItem(text);
            cboToWeek.addItem(text);
        }

        int currentWeek = LocalDate.now().get(weekFields.weekOfWeekBasedYear());
        int from = oldFrom > 0 ? Math.min(oldFrom, maxWeek) : Math.max(1, currentWeek - 1);
        int to = oldTo > 0 ? Math.min(oldTo, maxWeek) : Math.min(maxWeek, currentWeek);
        cboFromWeek.setSelectedIndex(from - 1);
        cboToWeek.setSelectedIndex(to - 1);
    }

    private void refreshFilterFields() {
        if (filterFieldsPanel == null) return;

        updatePeriodButtonStyle();
        filterFieldsPanel.removeAll();

        if (selectedMode == PeriodMode.NGAY) {
            filterFieldsPanel.add(createFieldLabel("Từ ngày:"));
            filterFieldsPanel.add(startChooser);
            filterFieldsPanel.add(createFieldLabel("Đến ngày:"));
            filterFieldsPanel.add(endChooser);
            filterFieldsPanel.add(createFilterButton());
            lblFilterHint.setText("ⓘ Dữ liệu được thống kê theo khoảng ngày. Có thể chuyển sang Tuần / Tháng / Năm để xem tổng hợp khác.");
        } else if (selectedMode == PeriodMode.TUAN) {
            filterFieldsPanel.add(createFieldLabel("Từ tuần:"));
            filterFieldsPanel.add(cboFromWeek);
            filterFieldsPanel.add(createFieldLabel("Đến tuần:"));
            filterFieldsPanel.add(cboToWeek);
            filterFieldsPanel.add(createFieldLabel("Năm:"));
            filterFieldsPanel.add(cboWeekYear);
            filterFieldsPanel.add(createFilterButton());
            lblFilterHint.setText("ⓘ Dữ liệu được thống kê theo khoảng tuần. Có thể chuyển sang Ngày / Tháng / Năm để xem theo khoảng thời gian khác.");
        } else if (selectedMode == PeriodMode.THANG) {
            filterFieldsPanel.add(createFieldLabel("Từ tháng:"));
            filterFieldsPanel.add(cboFromMonth);
            filterFieldsPanel.add(createFieldLabel("Đến tháng:"));
            filterFieldsPanel.add(cboToMonth);
            filterFieldsPanel.add(createFieldLabel("Năm:"));
            filterFieldsPanel.add(cboMonthYear);
            filterFieldsPanel.add(createFilterButton());
            lblFilterHint.setText("ⓘ Dữ liệu được thống kê theo khoảng tháng. Biểu đồ doanh thu sẽ gom theo từng tháng trong năm đã chọn.");
        } else {
            filterFieldsPanel.add(createFieldLabel("Từ năm:"));
            filterFieldsPanel.add(cboFromYear);
            filterFieldsPanel.add(createFieldLabel("Đến năm:"));
            filterFieldsPanel.add(cboToYear);
            filterFieldsPanel.add(createFilterButton());
            lblFilterHint.setText("ⓘ Dữ liệu được thống kê theo khoảng năm. Phù hợp để xem xu hướng doanh thu dài hạn.");
        }

        filterFieldsPanel.revalidate();
        filterFieldsPanel.repaint();
    }

    private void updatePeriodButtonStyle() {
        stylePeriodButton(btnNgay, selectedMode == PeriodMode.NGAY);
        stylePeriodButton(btnTuan, selectedMode == PeriodMode.TUAN);
        stylePeriodButton(btnThang, selectedMode == PeriodMode.THANG);
        stylePeriodButton(btnNam, selectedMode == PeriodMode.NAM);
    }

    private void stylePeriodButton(JToggleButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(new Color(255, 244, 229));
            btn.setForeground(new Color(80, 55, 20));
            btn.setBorder(new LineBorder(new Color(205, 160, 95), 1, true));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(51, 65, 85));
            btn.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        }
    }

    private JPanel createKpiPanel() {
        JPanel kpi = new JPanel(new GridBagLayout());
        kpi.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 18);

        lblTongMonBan = new JLabel("0");
        lblTongSoLuong = new JLabel("0");
        lblDoanhThu = new JLabel("0đ");
        lblBanChayNhat = new JLabel("-");

        JPanel cardTongMon = createKpiCard("☕", "Tổng món bán", lblTongMonBan, "món", new Color(235, 243, 255), new Color(51, 144, 236));
        JPanel cardTongSL = createKpiCard("▣", "Tổng số lượng", lblTongSoLuong, "suất", new Color(235, 250, 240), new Color(74, 151, 89));
        JPanel cardDoanhThu = createKpiCard("₫", "Doanh thu", lblDoanhThu, "", new Color(255, 247, 225), new Color(198, 143, 39));
        JPanel cardBanChay = createKpiCard("♨", "Bán chạy nhất", lblBanChayNhat, "", new Color(250, 235, 255), new Color(132, 65, 156));

        cardTongMon.setPreferredSize(new Dimension(220, 115));
        cardTongSL.setPreferredSize(new Dimension(220, 115));
        cardDoanhThu.setPreferredSize(new Dimension(220, 115));
        cardBanChay.setPreferredSize(new Dimension(300, 115));

        gbc.gridx = 0;
        gbc.weightx = 0.17;
        kpi.add(cardTongMon, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.17;
        kpi.add(cardTongSL, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.20;
        kpi.add(cardDoanhThu, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.26;
        kpi.add(cardBanChay, gbc);

        JPanel exportCard = createExportCard();
        exportCard.setPreferredSize(new Dimension(250, 115));
        gbc.gridx = 4;
        gbc.weightx = 0.20;
        gbc.insets = new Insets(0, 0, 0, 0);
        kpi.add(exportCard, gbc);

        return kpi;
    }

    private JPanel createKpiCard(String icon, String title, JLabel valueLabel, String unit, Color iconBg, Color accent) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                        new EmptyBorder(16, 18, 16, 18))));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblIcon.setForeground(accent);
        lblIcon.setOpaque(true);
        lblIcon.setBackground(iconBg);
        lblIcon.setPreferredSize(new Dimension(72, 72));
        lblIcon.setBorder(new LineBorder(iconBg, 1, true));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblUnit = new JLabel(unit == null ? "" : unit);
        lblUnit.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUnit.setForeground(new Color(71, 85, 105));

        textBox.add(lblTitle);
        textBox.add(Box.createRigidArea(new Dimension(0, 6)));
        textBox.add(valueLabel);
        textBox.add(Box.createRigidArea(new Dimension(0, 4)));
        textBox.add(lblUnit);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);
        return card;
    }

    private JPanel createExportCard() {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(new Color(101, 180, 110));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(86, 160, 96), 1, true),
                new EmptyBorder(14, 20, 14, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { exportBaoCaoExcel(); }
        });

        JLabel icon = new JLabel("▤", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.BOLD, 32));
        icon.setForeground(Color.WHITE);
        icon.setPreferredSize(new Dimension(56, 60));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Xuất báo cáo");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Excel");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(240, 255, 240));
        textBox.add(Box.createVerticalGlue());
        textBox.add(title);
        textBox.add(Box.createRigidArea(new Dimension(0, 6)));
        textBox.add(sub);
        textBox.add(Box.createVerticalGlue());

        JLabel arrow = new JLabel("›", SwingConstants.RIGHT);
        arrow.setFont(new Font("SansSerif", Font.BOLD, 34));
        arrow.setForeground(Color.WHITE);
        arrow.setPreferredSize(new Dimension(22, 60));

        card.add(icon, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);
        return card;
    }

    private JPanel createMidScaleSection() {
        JPanel mid = new JPanel(new BorderLayout(24, 0));
        mid.setOpaque(false);

        JPanel leftCol = new JPanel(new BorderLayout(0, 15));
        leftCol.setOpaque(false);

        JLabel lblDetailsTitle = new JLabel("DANH SÁCH CHI TIẾT");
        lblDetailsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblDetailsTitle.setForeground(new Color(30, 41, 59));
        leftCol.add(lblDetailsTitle, BorderLayout.NORTH);

        String[] cols = { "STT", "Loại món", "Tên món ăn", "Mã món", "SL", "Thành tiền" };
        tableModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setRowHeight(42);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader th = table.getTableHeader();
        th.setReorderingAllowed(false);
        th.setResizingAllowed(false);
        th.setFont(new Font("SansSerif", Font.BOLD, 15));
        th.setBackground(new Color(235, 240, 250));
        th.setForeground(new Color(50, 50, 50));
        th.setPreferredSize(new Dimension(0, 42));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        int[] widths = { 90, 180, 300, 140, 90, 220 };
        for (int i = 0; i < widths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
            column.setMinWidth(40);
            column.setResizable(false);
        }

        JScrollPane tblScroll = new JScrollPane(table);
        tblScroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        tblScroll.getViewport().setBackground(Color.WHITE);
        tblScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tblScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tableModel.addTableModelListener(new TableModelListener() {
            @Override public void tableChanged(TableModelEvent e) {
                capNhatChieuCaoBang(table, tblScroll);
            }
        });
        capNhatChieuCaoBang(table, tblScroll);

        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0));
        footer.setOpaque(false);
        lblFooterDong = createFooterLabel("Tổng số dòng: 0");
        lblFooterSL = createFooterLabel("Tổng SL: 0");
        lblFooterTien = createFooterLabel("Tổng tiền: 0đ");
        footer.add(createFooterCell(lblFooterDong));
        footer.add(createFooterCell(lblFooterSL));
        footer.add(createFooterCell(lblFooterTien));

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.add(tblScroll, BorderLayout.CENTER);
        tableWrap.add(footer, BorderLayout.SOUTH);

        JPanel tableWrapHolder = new JPanel(new BorderLayout());
        tableWrapHolder.setOpaque(false);
        tableWrapHolder.add(tableWrap, BorderLayout.NORTH);

        leftCol.add(tableWrapHolder, BorderLayout.CENTER);
        mid.add(leftCol, BorderLayout.CENTER);

        JPanel rightCol = new JPanel(new GridLayout(2, 1, 0, 24));
        rightCol.setOpaque(false);
        rightCol.setPreferredSize(new Dimension(620, 440));
        rightCol.setMinimumSize(new Dimension(580, 400));

        chartTopMon = new MockBarTopMon();
        chartDoanhThu = new MockLineDoanhThu();

        JPanel chart1 = createChartWrapper("BIỂU ĐỒ TOP MÓN BÁN CHẠY", chartTopMon, false);
        JPanel chart2 = createChartWrapper("DOANH THU THEO TUẦN", chartDoanhThu, true);
        chart1.setPreferredSize(new Dimension(620, 208));
        chart2.setPreferredSize(new Dimension(620, 208));

        rightCol.add(chart1);
        rightCol.add(chart2);

        mid.add(rightCol, BorderLayout.EAST);
        return mid;
    }

    private void capNhatChieuCaoBang(JTable table, JScrollPane scrollPane) {
        int soDong = Math.max(table.getRowCount(), 1);
        int chieuCaoHeader = table.getTableHeader().getPreferredSize().height;
        int chieuCaoDong = table.getRowHeight();

        int chieuCaoBang = chieuCaoHeader + soDong * chieuCaoDong + 2;
        int chieuCaoToiDa = 430;
        int chieuCaoCuoi = Math.min(chieuCaoBang, chieuCaoToiDa);

        scrollPane.setPreferredSize(new Dimension(0, chieuCaoCuoi));
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    private JLabel createFooterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setForeground(new Color(50, 50, 50));
        return l;
    }

    private JPanel createChartWrapper(String title, JPanel chart, boolean isRevenueChart) {
        JPanel w = new JPanel(new BorderLayout(0, 10));
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)));
        JLabel lblTop = new JLabel(title);
        lblTop.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTop.setForeground(new Color(30, 41, 59));
        if (isRevenueChart) lblChartDoanhThuTitle = lblTop;
        w.add(lblTop, BorderLayout.NORTH);
        w.add(chart, BorderLayout.CENTER);
        return w;
    }

    private JPanel createFooterCell(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(248, 248, 250));
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(10, 20, 10, 20)));
        p.add(label, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setOpaque(false);

        lblStatusRange = new JLabel("▣  Thống kê: -");
        lblStatusRange.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblStatusRange.setForeground(new Color(100, 116, 139));

        lblStatusUpdated = new JLabel("⟳  Cập nhật lúc: -");
        lblStatusUpdated.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblStatusUpdated.setForeground(new Color(100, 116, 139));

        status.add(lblStatusRange, BorderLayout.WEST);
        status.add(lblStatusUpdated, BorderLayout.EAST);
        return status;
    }

    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();
        endChooser.setDate(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -30);
        startChooser.setDate(cal.getTime());

        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        cboWeekYear.setSelectedItem(year);
        cboMonthYear.setSelectedItem(year);
        cboFromYear.setSelectedItem(year);
        cboToYear.setSelectedItem(year);
        rebuildWeekCombos();
    }

    private void loadData() {
        DateRange range = getSelectedDateRange();
        if (range == null) return;

        List<ItemSale> items = loadItemSales(range.start, range.end);
        List<DaySale> trend = loadTrendSales(range.start, range.end);
        updateUIWithData(items, trend, range);
    }

    private DateRange getSelectedDateRange() {
        try {
            if (selectedMode == PeriodMode.NGAY) {
                Date start = startChooser.getDate();
                Date end = endChooser.getDate();
                if (start == null || end == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
                    return null;
                }
                if (start.after(end)) {
                    JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
                    return null;
                }
                LocalDate s = toLocalDate(start);
                LocalDate e = toLocalDate(end);
                return new DateRange(s, e, "từ " + s.format(dateFormat) + " đến " + e.format(dateFormat));
            }

            if (selectedMode == PeriodMode.TUAN) {
                int fromWeek = getWeekNumber(cboFromWeek);
                int toWeek = getWeekNumber(cboToWeek);
                int year = Integer.parseInt(cboWeekYear.getSelectedItem().toString());
                if (fromWeek > toWeek) {
                    JOptionPane.showMessageDialog(this, "Tuần bắt đầu không được lớn hơn tuần kết thúc.");
                    return null;
                }
                LocalDate s = getStartOfIsoWeek(year, fromWeek);
                LocalDate e = getStartOfIsoWeek(year, toWeek).plusDays(6);
                return new DateRange(s, e, "từ tuần " + fromWeek + " đến tuần " + toWeek + ", năm " + year);
            }

            if (selectedMode == PeriodMode.THANG) {
                int fromMonth = cboFromMonth.getSelectedIndex() + 1;
                int toMonth = cboToMonth.getSelectedIndex() + 1;
                int year = Integer.parseInt(cboMonthYear.getSelectedItem().toString());
                if (fromMonth > toMonth) {
                    JOptionPane.showMessageDialog(this, "Tháng bắt đầu không được lớn hơn tháng kết thúc.");
                    return null;
                }
                LocalDate s = LocalDate.of(year, fromMonth, 1);
                LocalDate e = LocalDate.of(year, toMonth, 1).withDayOfMonth(LocalDate.of(year, toMonth, 1).lengthOfMonth());
                return new DateRange(s, e, "từ tháng " + fromMonth + " đến tháng " + toMonth + ", năm " + year);
            }

            int fromYear = Integer.parseInt(cboFromYear.getSelectedItem().toString());
            int toYear = Integer.parseInt(cboToYear.getSelectedItem().toString());
            if (fromYear > toYear) {
                JOptionPane.showMessageDialog(this, "Năm bắt đầu không được lớn hơn năm kết thúc.");
                return null;
            }
            LocalDate s = LocalDate.of(fromYear, 1, 1);
            LocalDate e = LocalDate.of(toYear, 12, 31);
            return new DateRange(s, e, "từ năm " + fromYear + " đến năm " + toYear);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Dữ liệu lọc không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private int getWeekNumber(JComboBox<String> cbo) {
        if (cbo == null || cbo.getSelectedItem() == null) return -1;
        String text = cbo.getSelectedItem().toString();
        String number = text.replace("Tuần", "").trim();
        int space = number.indexOf(' ');
        if (space > 0) number = number.substring(0, space);
        return Integer.parseInt(number);
    }

    private LocalDate getStartOfIsoWeek(int year, int week) {
        return LocalDate.of(year, 1, 4)
                .with(weekFields.weekOfWeekBasedYear(), week)
                .with(weekFields.dayOfWeek(), 1);
    }

    private List<ItemSale> loadItemSales(LocalDate start, LocalDate end) {
        List<ItemSale> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return list;
            String sql = "SELECT lm.tenLoaiMonAn, m.tenMon, m.maMon, "
                    + "ISNULL(SUM(ct.soLuong - ISNULL(ct.soLuongHuy,0)),0) AS soLuong, "
                    + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy,0)) * ct.donGia),0) AS thanhTien "
                    + "FROM ChiTietHoaDon ct "
                    + "JOIN HoaDon hd ON hd.maHD = ct.maHD "
                    + "JOIN MonAn m ON m.maMon = ct.maMon "
                    + "JOIN LoaiMonAn lm ON lm.maLoaiMonAn = m.maLoaiMonAn "
                    + "WHERE hd.trangThai = N'Đã thanh toán' "
                    + "AND hd.thoiGianRa >= ? AND hd.thoiGianRa < ? "
                    + "AND (ct.trangThai IS NULL OR ct.trangThai <> N'Đã hủy') "
                    + "GROUP BY lm.tenLoaiMonAn, m.tenMon, m.maMon "
                    + "ORDER BY thanhTien DESC";
            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            rs = stmt.executeQuery();
            while (rs.next()) {
                ItemSale item = new ItemSale();
                item.loaiMon = rs.getString("tenLoaiMonAn");
                item.tenMon = rs.getString("tenMon");
                item.maMon = rs.getString("maMon");
                item.soLuong = rs.getInt("soLuong");
                item.thanhTien = rs.getDouble("thanhTien");
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải tổng kết bán hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
        return list;
    }

    private List<DaySale> loadTrendSales(LocalDate start, LocalDate end) {
        List<DaySale> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return list;

            String sql;
            if (selectedMode == PeriodMode.NGAY) {
                sql = "SELECT CAST(thoiGianRa AS DATE) AS nhom, ISNULL(SUM(tongTien),0) AS doanhThu "
                        + "FROM HoaDon "
                        + "WHERE trangThai = N'Đã thanh toán' "
                        + "AND thoiGianRa >= ? AND thoiGianRa < ? "
                        + "GROUP BY CAST(thoiGianRa AS DATE) "
                        + "ORDER BY nhom";
            } else if (selectedMode == PeriodMode.TUAN) {
                sql = "SELECT DATEPART(YEAR, thoiGianRa) AS nam, DATEPART(ISO_WEEK, thoiGianRa) AS tuan, ISNULL(SUM(tongTien),0) AS doanhThu "
                        + "FROM HoaDon "
                        + "WHERE trangThai = N'Đã thanh toán' "
                        + "AND thoiGianRa >= ? AND thoiGianRa < ? "
                        + "GROUP BY DATEPART(YEAR, thoiGianRa), DATEPART(ISO_WEEK, thoiGianRa) "
                        + "ORDER BY nam, tuan";
            } else if (selectedMode == PeriodMode.THANG) {
                sql = "SELECT DATEPART(YEAR, thoiGianRa) AS nam, DATEPART(MONTH, thoiGianRa) AS thang, ISNULL(SUM(tongTien),0) AS doanhThu "
                        + "FROM HoaDon "
                        + "WHERE trangThai = N'Đã thanh toán' "
                        + "AND thoiGianRa >= ? AND thoiGianRa < ? "
                        + "GROUP BY DATEPART(YEAR, thoiGianRa), DATEPART(MONTH, thoiGianRa) "
                        + "ORDER BY nam, thang";
            } else {
                sql = "SELECT DATEPART(YEAR, thoiGianRa) AS nam, ISNULL(SUM(tongTien),0) AS doanhThu "
                        + "FROM HoaDon "
                        + "WHERE trangThai = N'Đã thanh toán' "
                        + "AND thoiGianRa >= ? AND thoiGianRa < ? "
                        + "GROUP BY DATEPART(YEAR, thoiGianRa) "
                        + "ORDER BY nam";
            }

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            rs = stmt.executeQuery();

            while (rs.next()) {
                DaySale day = new DaySale();
                if (selectedMode == PeriodMode.NGAY) {
                    day.label = rs.getDate("nhom").toLocalDate().format(dateFormat);
                } else if (selectedMode == PeriodMode.TUAN) {
                    day.label = "Tuần " + rs.getInt("tuan");
                } else if (selectedMode == PeriodMode.THANG) {
                    day.label = "Tháng " + rs.getInt("thang") + "/" + rs.getInt("nam");
                } else {
                    day.label = String.valueOf(rs.getInt("nam"));
                }
                day.doanhThu = rs.getDouble("doanhThu");
                list.add(day);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
        return list;
    }

    private void updateUIWithData(List<ItemSale> items, List<DaySale> trend, DateRange range) {
        tableModel.setRowCount(0);
        int totalQty = 0;
        double totalMoney = 0;
        int stt = 1;
        for (ItemSale item : items) {
            totalQty += item.soLuong;
            totalMoney += item.thanhTien;
            tableModel.addRow(new Object[] {
                    stt++,
                    item.loaiMon,
                    item.tenMon,
                    item.maMon,
                    item.soLuong,
                    formatMoney(item.thanhTien)
            });
        }

        lblTongMonBan.setText(String.valueOf(items.size()));
        lblTongSoLuong.setText(moneyFormat.format(totalQty));
        lblDoanhThu.setText(formatMoney(totalMoney));
        if (items.isEmpty()) {
            lblBanChayNhat.setText("-");
        } else {
            ItemSale top = items.get(0);
            lblBanChayNhat.setText("<html><b>" + escapeHtml(top.tenMon) + "</b><br><span style='font-size:11px;'>"
                    + formatMoney(top.thanhTien) + "</span></html>");
        }

        lblFooterDong.setText("Tổng số dòng: " + items.size());
        lblFooterSL.setText("Tổng SL: " + moneyFormat.format(totalQty));
        lblFooterTien.setText("Tổng tiền: " + formatMoney(totalMoney));

        chartTopMon.setData(items);
        chartDoanhThu.setData(trend);

        String modeText = selectedMode.getText().toLowerCase();
        lblSubTitle.setText("Tổng hợp món bán, số lượng và doanh thu theo " + modeText + " / khoảng thời gian");
        if (lblChartDoanhThuTitle != null) {
            lblChartDoanhThuTitle.setText("DOANH THU THEO " + selectedMode.getText().toUpperCase());
        }
        if (lblStatusRange != null) {
            lblStatusRange.setText("▣  Thống kê " + range.description + " (" + selectedMode.getText() + ")");
        }
        if (lblStatusUpdated != null) {
            lblStatusUpdated.setText("⟳  Cập nhật lúc: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }
    }

    private void exportBaoCaoExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất báo cáo.");
            return;
        }

        DateRange range = getSelectedDateRange();
        if (range == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo doanh thu");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel 97-2003 (*.xls)", "xls"));
        chooser.setSelectedFile(new File(taoTenFileBaoCao(range)));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xls")) {
            file = new File(file.getAbsolutePath() + ".xls");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write('\uFEFF');
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<?mso-application progid=\"Excel.Sheet\"?>\n");
            writer.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
            writer.write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
            writer.write(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
            writer.write(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
            writer.write(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");

            writer.write("<Styles>\n");
            writer.write("<Style ss:ID=\"Default\" ss:Name=\"Normal\"><Alignment ss:Vertical=\"Center\"/><Font ss:FontName=\"Arial\" ss:Size=\"11\"/></Style>\n");
            writer.write("<Style ss:ID=\"Title\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/><Font ss:FontName=\"Arial\" ss:Bold=\"1\" ss:Size=\"18\"/></Style>\n");
            writer.write("<Style ss:ID=\"SubInfo\"><Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/><Font ss:FontName=\"Arial\" ss:Size=\"11\"/></Style>\n");
            writer.write("<Style ss:ID=\"Header\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/><Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/></Borders><Font ss:FontName=\"Arial\" ss:Bold=\"1\" ss:Size=\"12\"/><Interior ss:Color=\"#E9EEF7\" ss:Pattern=\"Solid\"/></Style>\n");
            writer.write("<Style ss:ID=\"CellCenter\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/><Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/></Borders><Font ss:FontName=\"Arial\" ss:Size=\"12\"/></Style>\n");
            writer.write("<Style ss:ID=\"CellLeft\"><Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/><Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/></Borders><Font ss:FontName=\"Arial\" ss:Size=\"12\"/></Style>\n");
            writer.write("<Style ss:ID=\"CellMoney\"><Alignment ss:Horizontal=\"Right\" ss:Vertical=\"Center\"/><Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/></Borders><Font ss:FontName=\"Arial\" ss:Size=\"12\"/></Style>\n");
            writer.write("<Style ss:ID=\"Footer\"><Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/><Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/><Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/></Borders><Font ss:FontName=\"Arial\" ss:Bold=\"1\" ss:Size=\"13\"/><Interior ss:Color=\"#F4F4F6\" ss:Pattern=\"Solid\"/></Style>\n");
            writer.write("</Styles>\n");

            writer.write("<Worksheet ss:Name=\"BaoCaoDoanhThu\">\n");
            writer.write("<Table>\n");
            writer.write("<Column ss:Width=\"70\"/><Column ss:Width=\"130\"/><Column ss:Width=\"190\"/><Column ss:Width=\"100\"/><Column ss:Width=\"70\"/><Column ss:Width=\"130\"/>\n");

            writer.write("<Row ss:Height=\"28\"><Cell ss:MergeAcross=\"5\" ss:StyleID=\"Title\"><Data ss:Type=\"String\">BÁO CÁO DOANH THU</Data></Cell></Row>\n");
            writer.write("<Row ss:Height=\"22\"><Cell ss:MergeAcross=\"5\" ss:StyleID=\"SubInfo\"><Data ss:Type=\"String\">Kiểu thống kê: "
                    + escapeXml(selectedMode.getText()) + "    |    Khoảng thời gian: " + escapeXml(range.description)
                    + "</Data></Cell></Row>\n");
            writer.write("<Row ss:Height=\"22\"><Cell ss:MergeAcross=\"5\" ss:StyleID=\"SubInfo\"><Data ss:Type=\"String\">Tổng món bán: "
                    + escapeXml(lblTongMonBan.getText()) + "    |    Tổng số lượng: "
                    + escapeXml(lblTongSoLuong.getText()) + "    |    Doanh thu: "
                    + escapeXml(lblDoanhThu.getText()) + "    |    Bán chạy nhất: "
                    + escapeXml(stripHtml(lblBanChayNhat.getText())) + "</Data></Cell></Row>\n");
            writer.write("<Row ss:Height=\"12\"></Row>\n");

            writer.write("<Row ss:Height=\"24\">");
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write("<Cell ss:StyleID=\"Header\"><Data ss:Type=\"String\">" + escapeXml(tableModel.getColumnName(i)) + "</Data></Cell>");
            }
            writer.write("</Row>\n");

            for (int r = 0; r < tableModel.getRowCount(); r++) {
                writer.write("<Row ss:Height=\"24\">");
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object value = tableModel.getValueAt(r, c);
                    String text = value == null ? "" : value.toString();
                    String styleId = c == 2 ? "CellLeft" : (c == 5 ? "CellMoney" : "CellCenter");
                    writer.write("<Cell ss:StyleID=\"" + styleId + "\"><Data ss:Type=\"String\">"
                            + escapeXml(text) + "</Data></Cell>");
                }
                writer.write("</Row>\n");
            }

            writer.write("<Row ss:Height=\"28\">");
            writer.write("<Cell ss:MergeAcross=\"1\" ss:StyleID=\"Footer\"><Data ss:Type=\"String\">" + escapeXml(lblFooterDong.getText()) + "</Data></Cell>");
            writer.write("<Cell ss:MergeAcross=\"1\" ss:StyleID=\"Footer\"><Data ss:Type=\"String\">" + escapeXml(lblFooterSL.getText()) + "</Data></Cell>");
            writer.write("<Cell ss:MergeAcross=\"1\" ss:StyleID=\"Footer\"><Data ss:Type=\"String\">" + escapeXml(lblFooterTien.getText()) + "</Data></Cell>");
            writer.write("</Row>\n");

            writer.write("</Table>\n</Worksheet>\n</Workbook>\n");

            JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!\nFile Excel: " + file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Xuất báo cáo thất bại:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String taoTenFileBaoCao(DateRange range) {
        if (range == null) {
            return "BaoCaoDoanhThu.xls";
        }

        if (selectedMode == PeriodMode.NGAY) {
            if (range.start.equals(range.end)) {
                return "BaoCaoDoanhThu_Ngay_" + dinhDangNgayChoFile(range.start) + ".xls";
            }

            return "BaoCaoDoanhThu_TuNgay_" + dinhDangNgayChoFile(range.start)
                    + "_DenNgay_" + dinhDangNgayChoFile(range.end) + ".xls";
        }

        if (selectedMode == PeriodMode.TUAN) {
            int fromWeek = getWeekNumber(cboFromWeek);
            int toWeek = getWeekNumber(cboToWeek);
            int year = Integer.parseInt(cboWeekYear.getSelectedItem().toString());

            if (fromWeek == toWeek) {
                return String.format("BaoCaoDoanhThu_Tuan_%02d_Nam_%d.xls", fromWeek, year);
            }

            return String.format("BaoCaoDoanhThu_Tuan_%02d_DenTuan_%02d_Nam_%d.xls", fromWeek, toWeek, year);
        }

        if (selectedMode == PeriodMode.THANG) {
            int fromMonth = cboFromMonth.getSelectedIndex() + 1;
            int toMonth = cboToMonth.getSelectedIndex() + 1;
            int year = Integer.parseInt(cboMonthYear.getSelectedItem().toString());

            if (fromMonth == toMonth) {
                return String.format("BaoCaoDoanhThu_Thang_%02d_Nam_%d.xls", fromMonth, year);
            }

            return String.format("BaoCaoDoanhThu_Thang_%02d_DenThang_%02d_Nam_%d.xls", fromMonth, toMonth, year);
        }

        int fromYear = Integer.parseInt(cboFromYear.getSelectedItem().toString());
        int toYear = Integer.parseInt(cboToYear.getSelectedItem().toString());

        if (fromYear == toYear) {
            return "BaoCaoDoanhThu_Nam_" + fromYear + ".xls";
        }

        return "BaoCaoDoanhThu_TuNam_" + fromYear + "_DenNam_" + toYear + ".xls";
    }

    private String dinhDangNgayChoFile(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value) + "đ";
    }

    private String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String stripHtml(String value) {
        if (value == null) return "";
        return value.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) return;
        try { closeable.close(); } catch (Exception e) { e.printStackTrace(); }
    }

    private static class DateRange {
        LocalDate start;
        LocalDate end;
        String description;
        DateRange(LocalDate start, LocalDate end, String description) {
            this.start = start;
            this.end = end;
            this.description = description;
        }
    }

    private static class ItemSale {
        String loaiMon;
        String tenMon;
        String maMon;
        int soLuong;
        double thanhTien;
    }

    private static class DaySale {
        String label;
        double doanhThu;
    }

    private static class WidthTrackingPanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 80; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    class MockBarTopMon extends JPanel {
        private static final long serialVersionUID = 1L;
        private List<ItemSale> data = new ArrayList<>();

        public MockBarTopMon() { setOpaque(false); }
        public void setData(List<ItemSale> data) { this.data = data == null ? new ArrayList<>() : data; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int padLeft = 58;
            int padBottom = 42;
            int padTop = 10;
            int right = w - 8;
            int bottom = h - padBottom;

            drawGrid(g2, padLeft, padTop, right, bottom, 4, 0);

            if (data.isEmpty()) {
                drawNoData(g2, w, h);
                return;
            }

            int n = Math.min(data.size(), 5);
            double max = 0;
            for (int i = 0; i < n; i++) max = Math.max(max, data.get(i).thanhTien);
            if (max <= 0) max = 1;

            drawYAxisLabels(g2, padLeft, padTop, bottom, max, true);

            double groupW = (right - padLeft) / (double) n;
            int barW = 42;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            int maxBarHeightPixels = bottom - padTop - 10;

            for (int i = 0; i < n; i++) {
                ItemSale item = data.get(i);
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);
                String label = shorten(item.tenMon, 10);
                int textW = fm.stringWidth(label);

                g2.setColor(new Color(71, 85, 105));
                g2.drawString(label, Math.max(0, cx - textW / 2), h - 14);

                int barH = (int) (item.thanhTien / max * maxBarHeightPixels);
                int y = bottom - barH;

                g2.setColor(new Color(66, 153, 225));
                if (barH > 0) {
                    g2.fillRoundRect(cx - barW / 2, y, barW, barH, 10, 10);
                    if (barH > 6) g2.fillRect(cx - barW / 2, bottom - 6, barW, 6);
                }
            }
        }
    }

    class MockLineDoanhThu extends JPanel {
        private static final long serialVersionUID = 1L;
        private List<DaySale> data = new ArrayList<>();

        public MockLineDoanhThu() { setOpaque(false); }
        public void setData(List<DaySale> data) { this.data = data == null ? new ArrayList<>() : data; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int padLeft = 58;
            int padBottom = 48;
            int padTop = 10;
            int right = w - 8;
            int bottom = h - padBottom;

            drawGrid(g2, padLeft, padTop, right, bottom, 4, 0);

            if (data.isEmpty()) {
                drawNoData(g2, w, h);
                return;
            }

            int n = Math.min(data.size(), 8);
            double max = 0;
            for (int i = 0; i < n; i++) max = Math.max(max, data.get(i).doanhThu);
            if (max <= 0) max = 1;

            drawYAxisLabels(g2, padLeft, padTop, bottom, max, true);

            double groupW = n <= 1 ? (right - padLeft) : (right - padLeft) / (double) (n - 1);
            int[] px = new int[n];
            int[] py = new int[n];
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            int maxLineHeight = bottom - padTop - 10;

            for (int i = 0; i < n; i++) {
                DaySale d = data.get(i);
                int cx = n <= 1 ? (padLeft + right) / 2 : padLeft + (int) (i * groupW);
                px[i] = cx;

                int pointH = (int) (d.doanhThu / max * maxLineHeight);
                py[i] = bottom - pointH;

                String label = shorten(d.label, 12);
                int textW = g2.getFontMetrics().stringWidth(label);
                g2.setColor(new Color(71, 85, 105));
                g2.drawString(label, Math.max(0, cx - textW / 2), h - 16);
            }

            g2.setColor(new Color(37, 99, 235));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(3.0f));
            for (int i = 0; i < n - 1; i++) g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            g2.setStroke(oldStr);

            for (int i = 0; i < n; i++) {
                g2.setColor(new Color(37, 99, 235));
                g2.fillOval(px[i] - 5, py[i] - 5, 10, 10);
                g2.setColor(Color.WHITE);
                g2.fillOval(px[i] - 2, py[i] - 2, 4, 4);
            }
        }
    }

    private void drawGrid(Graphics2D g2, int left, int top, int right, int bottom, int lines, int dummy) {
        g2.setColor(new Color(229, 231, 235));
        for (int i = 0; i <= lines; i++) {
            int y = bottom - (int) ((bottom - top) * i / (double) lines);
            g2.drawLine(left, y, right, y);
        }
        g2.setColor(new Color(200, 200, 200));
        g2.drawLine(left, bottom, right, bottom);
        g2.drawLine(left, top, left, bottom);
    }

    private void drawYAxisLabels(Graphics2D g2, int left, int top, int bottom, double max, boolean compactMoney) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(100, 116, 139));
        for (int i = 0; i <= 4; i++) {
            double value = max * i / 4.0;
            int y = bottom - (int) ((bottom - top) * i / 4.0);
            String text = compactMoney ? formatCompact(value) : String.valueOf((int) value);
            int textW = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, left - textW - 8, y + 4);
        }
    }

    private void drawNoData(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.setColor(Color.GRAY);
        g2.drawString("Chưa có dữ liệu", w / 2 - 65, h / 2);
    }

    private String shorten(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, Math.max(0, max - 3)) + "..." : text;
    }

    private String formatCompact(double value) {
        if (value >= 1_000_000_000) return moneyFormat.format(value / 1_000_000_000.0) + "B";
        if (value >= 1_000_000) return moneyFormat.format(value / 1_000_000.0) + "M";
        if (value >= 1_000) return moneyFormat.format(value / 1_000.0) + "K";
        return moneyFormat.format(value);
    }
}
