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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class PhanTichBanHang_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;

    private JDateChooser startChooser;
    private JDateChooser endChooser;

    private JLabel lblDoanhThuThuan;
    private JLabel lblChiPhi;
    private JLabel lblLoiNhuan;
    private JLabel lblTrangThai;
    private JLabel lblTySuat;
    private JLabel lblMucDanhGia;
    private JTextArea txtNhanXet;

    private DefaultTableModel tableModel;
    private JTable table;
    private MockChartPanel chartPanel;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PhanTichBanHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createMainPanel(), BorderLayout.CENTER);

        setDefaultDates();
        loadData();
    }

    public PhanTichBanHang_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(248, 249, 251));

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(22, 32, 36, 32));

        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 40)));
        contentContainer.add(wrapInNorth(createKpiPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 50)));
        contentContainer.add(wrapInNorth(createMiddleSection()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 40)));
        contentContainer.add(wrapInNorth(createTableSection()));

        JScrollPane scroll = new JScrollPane(wrapInNorth(contentContainer));
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
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

    private JPanel createHeaderPanel() {
        JPanel hdr = new JPanel(new BorderLayout(20, 0));
        hdr.setBackground(Color.WHITE);
        hdr.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 22, 18, 22)));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Phân tích bán hàng");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("So sánh doanh thu, chi phí nguyên liệu thực tế và lợi nhuận theo khoảng ngày");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(lblSub);

        hdr.add(titleBox, BorderLayout.WEST);

        JPanel rightHdr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHdr.setOpaque(false);

        JLabel lblStart = new JLabel("Từ ngày:");
        lblStart.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblStart.setForeground(new Color(71, 85, 105));

        startChooser = new JDateChooser();
        startChooser.setPreferredSize(new Dimension(145, 34));
        startChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel lblEnd = new JLabel("Đến ngày:");
        lblEnd.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEnd.setForeground(new Color(71, 85, 105));

        endChooser = new JDateChooser();
        endChooser.setPreferredSize(new Dimension(145, 34));
        endChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton btnFilter = new JButton("Lọc dữ liệu");
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnFilter.setBackground(new Color(250, 235, 215));
        btnFilter.setForeground(new Color(110, 80, 50));
        btnFilter.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 190, 170), 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        btnFilter.setFocusPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.addActionListener(e -> loadData());

        rightHdr.add(lblStart);
        rightHdr.add(startChooser);
        rightHdr.add(lblEnd);
        rightHdr.add(endChooser);
        rightHdr.add(btnFilter);

        hdr.add(rightHdr, BorderLayout.EAST);
        return hdr;
    }

    private JPanel createKpiPanel() {
        JPanel kpi = new JPanel(new GridLayout(1, 4, 30, 0));
        kpi.setOpaque(false);

        lblDoanhThuThuan = new JLabel("0đ", SwingConstants.CENTER);
        lblChiPhi = new JLabel("0đ", SwingConstants.CENTER);
        lblLoiNhuan = new JLabel("0đ", SwingConstants.CENTER);
        lblTrangThai = new JLabel("-", SwingConstants.CENTER);

        kpi.add(createKpiCard("Doanh thu thuần", lblDoanhThuThuan, new Color(225, 240, 255), new Color(130, 160, 200)));
        kpi.add(createKpiCard("Chi phí nguyên liệu", lblChiPhi, new Color(225, 245, 230), new Color(130, 180, 150)));
        kpi.add(createKpiCard("Lợi nhuận", lblLoiNhuan, new Color(255, 240, 225), new Color(200, 160, 120)));
        kpi.add(createKpiCard("Trạng thái", lblTrangThai, new Color(255, 230, 235), new Color(180, 130, 150)));

        return kpi;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color bg, Color titleFg) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, titleFg),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                        new EmptyBorder(16, 14, 16, 14))));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 116, 139));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createMiddleSection() {
        JPanel mid = new JPanel(new BorderLayout(50, 0));
        mid.setOpaque(false);

        JPanel chartArea = new JPanel(new BorderLayout());
        chartArea.setBackground(Color.WHITE);
        chartArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)));
        chartArea.setPreferredSize(new Dimension(750, 360));

        JLabel lblChartTitle = new JLabel("BIỂU ĐỒ DOANH THU - CHI PHÍ - LỢI NHUẬN");
        lblChartTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblChartTitle.setForeground(new Color(30, 41, 59));
        lblChartTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        chartPanel = new MockChartPanel();

        chartArea.add(lblChartTitle, BorderLayout.NORTH);
        chartArea.add(chartPanel, BorderLayout.CENTER);

        mid.add(chartArea, BorderLayout.CENTER);

        JPanel rightArea = new JPanel(new BorderLayout(0, 15));
        rightArea.setOpaque(false);
        rightArea.setPreferredSize(new Dimension(450, 0));

        JLabel lblSummaryTitle = new JLabel("TÓM TẮT LỜI / LỖ");
        lblSummaryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSummaryTitle.setForeground(new Color(30, 41, 59));
        lblSummaryTitle.setHorizontalAlignment(SwingConstants.CENTER);
        rightArea.add(lblSummaryTitle, BorderLayout.NORTH);

        JPanel pnlSummaryBoxes = new JPanel(new BorderLayout(0, 15));
        pnlSummaryBoxes.setOpaque(false);

        JPanel topTwo = new JPanel(new GridLayout(1, 2, 15, 0));
        topTwo.setOpaque(false);

        lblTySuat = new JLabel("0%", SwingConstants.LEFT);
        lblMucDanhGia = new JLabel("-", SwingConstants.LEFT);

        topTwo.add(createSummaryBox("Tỷ suất lợi nhuận", lblTySuat));
        topTwo.add(createSummaryBox("Mức đánh giá", lblMucDanhGia));

        pnlSummaryBoxes.add(topTwo, BorderLayout.NORTH);

        JPanel bottomBox = new JPanel(new BorderLayout(0, 10));
        bottomBox.setBackground(Color.WHITE);
        bottomBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblQuick = new JLabel("Nhận xét nhanh");
        lblQuick.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblQuick.setForeground(Color.GRAY);

        txtNhanXet = new JTextArea("Chưa có dữ liệu phân tích.");
        txtNhanXet.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtNhanXet.setForeground(new Color(60, 60, 60));
        txtNhanXet.setLineWrap(true);
        txtNhanXet.setWrapStyleWord(true);
        txtNhanXet.setEditable(false);
        txtNhanXet.setOpaque(false);

        bottomBox.add(lblQuick, BorderLayout.NORTH);
        bottomBox.add(txtNhanXet, BorderLayout.CENTER);

        pnlSummaryBoxes.add(bottomBox, BorderLayout.CENTER);

        JPanel boundedRightArea = new JPanel(new BorderLayout());
        boundedRightArea.setBackground(Color.WHITE);
        boundedRightArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        boundedRightArea.add(pnlSummaryBoxes, BorderLayout.CENTER);
        rightArea.add(boundedRightArea, BorderLayout.CENTER);

        mid.add(rightArea, BorderLayout.EAST);
        return mid;
    }

    private JPanel createSummaryBox(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblT.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(new Color(50, 50, 50));

        p.add(lblT);
        p.add(valueLabel);

        return p;
    }

    private JPanel createTableSection() {
        JPanel tableSec = new JPanel(new BorderLayout(0, 20));
        tableSec.setOpaque(false);

        JLabel lblTitle = new JLabel("BẢNG PHÂN TÍCH MÓN ĂN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));

        tableSec.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {
                "STT", "Tên món", "SL bán", "Giá gốc", "Giá bán",
                "Doanh thu", "Chi phí", "Lợi nhuận", "Ghi chú"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // Đã thu hẹp STT và SL bán, mở rộng Ghi chú
        setTableWidths(table, new int[]{40, 250, 55, 110, 110, 125, 125, 125, 320});

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(800, 270));

        tableSec.add(scroll, BorderLayout.CENTER);
        return tableSec;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setRowHeight(42);
        table.setGridColor(new Color(226, 232, 240));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(238, 225, 205));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(new Color(235, 240, 250));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(235, 240, 250));
        headerRenderer.setForeground(new Color(50, 50, 50));
        headerRenderer.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBorder(new LineBorder(new Color(210, 220, 235), 1));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("SansSerif", Font.PLAIN, 15));
                c.setBorder(new EmptyBorder(0, 8, 0, 8));

                if (isSelected) {
                    c.setBackground(new Color(238, 225, 205));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                c.setForeground(Color.BLACK);

                String col = t.getColumnName(column);

                if (col.contains("Tên") || col.contains("Ghi chú")) {
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                } else if (col.contains("Giá") || col.contains("Doanh") || col.contains("Chi") || col.contains("Lợi")) {
                    c.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(bodyRenderer);
        }
    }

    private void setTableWidths(JTable table, int[] widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
            col.setMinWidth(Math.max(35, widths[i] / 2));
            col.setResizable(false);
        }

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
    }

    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();
        endChooser.setDate(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, -30);
        startChooser.setDate(cal.getTime());
    }

    private void loadData() {
        Date start = startChooser.getDate();
        Date end = endChooser.getDate();

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
            return;
        }

        if (start.after(end)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
            return;
        }

        LocalDate startDate = toLocalDate(start);
        LocalDate endDate = toLocalDate(end);

        List<ItemSale> items = loadItemSales(startDate, endDate);
        List<PeriodSummary> periods = loadPeriodSummary(startDate, endDate);

        double revenue = 0;
        double cost = 0;
        double profit = 0;
        int totalQty = 0;

        for (ItemSale item : items) {
            revenue += item.doanhThu;
            cost += item.chiPhi;
            profit += item.loiNhuan;
            totalQty += item.soLuong;
        }

        double margin = revenue <= 0 ? 0 : profit * 100.0 / revenue;

        lblDoanhThuThuan.setText(formatMoney(revenue));
        lblChiPhi.setText(formatMoney(cost));
        lblLoiNhuan.setText(formatMoney(profit));
        lblTrangThai.setText(profit >= 0 ? "Lãi" : "Lỗ");
        lblTySuat.setText(String.format("%.1f%%", margin));
        lblMucDanhGia.setText(margin >= 30 ? "Tốt" : margin >= 15 ? "Ổn định" : revenue > 0 ? "Cần cải thiện" : "Chưa có dữ liệu");

        txtNhanXet.setText(buildNhanXet(items, revenue, profit, margin, totalQty, startDate, endDate));
        updateTable(items);
        chartPanel.setData(periods);
    }

    private List<ItemSale> loadItemSales(LocalDate start, LocalDate end) {
        List<ItemSale> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            if (con == null) {
                return list;
            }

            String sql =
                    "SELECT m.maMon, m.tenMon, m.giaGoc, m.donGia AS giaBan, "
                            + "ISNULL(SUM(ct.soLuong - ISNULL(ct.soLuongHuy, 0)), 0) AS soLuong, "
                            + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * ct.donGia), 0) AS doanhThu, "
                            + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * m.giaGoc), 0) AS chiPhi, "
                            + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * ct.donGia), 0) "
                            + "- ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * m.giaGoc), 0) AS loiNhuan "
                            + "FROM ChiTietHoaDon ct "
                            + "JOIN HoaDon hd ON hd.maHD = ct.maHD "
                            + "JOIN MonAn m ON m.maMon = ct.maMon "
                            + "WHERE hd.trangThai = N'Đã thanh toán' "
                            + "AND hd.thoiGianRa >= ? AND hd.thoiGianRa < ? "
                            + "AND (ct.trangThai IS NULL OR ct.trangThai <> N'Đã hủy') "
                            + "GROUP BY m.maMon, m.tenMon, m.giaGoc, m.donGia "
                            + "ORDER BY loiNhuan DESC";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                ItemSale item = new ItemSale();
                item.maMon = rs.getString("maMon");
                item.tenMon = rs.getString("tenMon");
                item.giaGoc = rs.getDouble("giaGoc");
                item.giaBan = rs.getDouble("giaBan");
                item.soLuong = rs.getInt("soLuong");
                item.doanhThu = rs.getDouble("doanhThu");
                item.chiPhi = rs.getDouble("chiPhi");
                item.loiNhuan = rs.getDouble("loiNhuan");
                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi tải dữ liệu phân tích: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }

        return list;
    }

    private List<PeriodSummary> loadPeriodSummary(LocalDate start, LocalDate end) {
        List<PeriodSummary> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();

            if (con == null) {
                return list;
            }

            String sql =
                    "SELECT CAST(hd.thoiGianRa AS DATE) AS ngay, "
                            + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * ct.donGia), 0) AS doanhThu, "
                            + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * m.giaGoc), 0) AS chiPhi, "
                            + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * ct.donGia), 0) "
                            + "- ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * m.giaGoc), 0) AS loiNhuan "
                            + "FROM HoaDon hd "
                            + "JOIN ChiTietHoaDon ct ON hd.maHD = ct.maHD "
                            + "JOIN MonAn m ON ct.maMon = m.maMon "
                            + "WHERE hd.trangThai = N'Đã thanh toán' "
                            + "AND hd.thoiGianRa >= ? AND hd.thoiGianRa < ? "
                            + "AND (ct.trangThai IS NULL OR ct.trangThai <> N'Đã hủy') "
                            + "GROUP BY CAST(hd.thoiGianRa AS DATE) "
                            + "ORDER BY ngay";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                PeriodSummary p = new PeriodSummary();
                p.label = rs.getDate("ngay").toLocalDate().format(dateFormat);
                p.doanhThu = rs.getDouble("doanhThu");
                p.chiPhi = rs.getDouble("chiPhi");
                p.loiNhuan = rs.getDouble("loiNhuan");
                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }

        return list;
    }

    private void updateTable(List<ItemSale> items) {
        tableModel.setRowCount(0);

        int stt = 1;

        for (ItemSale item : items) {
            double margin = item.doanhThu <= 0 ? 0 : item.loiNhuan * 100.0 / item.doanhThu;

            String note;

            if (item.loiNhuan < 0) {
                note = String.format("Lỗ %.1f%% - cần kiểm tra giá gốc/giá bán", margin);
            } else if (margin >= 30) {
                note = String.format("Lãi cao theo tỷ suất %.1f%%", margin);
            } else if (margin >= 20) {
                note = String.format("Ổn định, tỷ suất %.1f%%", margin);
            } else if (margin >= 10) {
                note = String.format("Lãi thấp, tỷ suất %.1f%%", margin);
            } else {
                note = String.format("Biên lợi nhuận thấp %.1f%%", margin);
            }

            tableModel.addRow(new Object[]{
                    stt++,
                    item.tenMon,
                    item.soLuong,
                    formatMoney(item.giaGoc),
                    formatMoney(item.giaBan),
                    formatMoney(item.doanhThu),
                    formatMoney(item.chiPhi),
                    formatMoney(item.loiNhuan),
                    note
            });
        }
    }

    private String buildNhanXet(List<ItemSale> items, double revenue, double profit, double margin, int totalQty, LocalDate start, LocalDate end) {
        if (revenue <= 0) {
            return "• Chưa có hóa đơn đã thanh toán trong khoảng " + start.format(dateFormat) + " - " + end.format(dateFormat) + ".\n"
                    + "• Cần kiểm tra lại khoảng ngày hoặc dữ liệu bán hàng.";
        }

        String top = items.isEmpty() ? "-" : items.get(0).tenMon;

        return "• Tổng số lượng món bán: " + totalQty + "\n"
                + "• Món có lợi nhuận cao nhất: " + top + "\n"
                + "• Lợi nhuận " + (profit >= 0 ? "dương" : "âm") + ", tỷ suất khoảng " + String.format("%.1f%%", margin) + "\n";
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value) + "đ";
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ItemSale {
        String maMon;
        String tenMon;
        int soLuong;
        double giaGoc;
        double giaBan;
        double doanhThu;
        double chiPhi;
        double loiNhuan;
    }

    private static class PeriodSummary {
        String label;
        double doanhThu;
        double chiPhi;
        double loiNhuan;
    }

    class MockChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private List<PeriodSummary> data = new ArrayList<>();

        public MockChartPanel() {
            setOpaque(false);
        }

        public void setData(List<PeriodSummary> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int padLeft = 45;
            int padBottom = 45;
            int padTop = 35;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));

            int legY = 15;
            int cx1 = 70;

            g2.setColor(new Color(110, 170, 230));
            g2.fillRect(cx1, legY - 10, 14, 14);

            g2.setColor(Color.GRAY);
            g2.drawString("Doanh thu", cx1 + 22, legY + 2);

            int cx2 = cx1 + 120;

            g2.setColor(new Color(140, 200, 130));
            g2.fillRect(cx2, legY - 10, 14, 14);

            g2.setColor(Color.GRAY);
            g2.drawString("Chi phí", cx2 + 22, legY + 2);

            int cx3 = cx2 + 100;

            g2.setColor(new Color(250, 180, 100));
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(cx3, legY - 3, cx3 + 20, legY - 3);

            g2.setColor(Color.GRAY);
            g2.drawString("Lợi nhuận", cx3 + 28, legY + 2);

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(210, 210, 210));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom);
            g2.drawLine(padLeft, padTop, padLeft, h - padBottom);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.setColor(Color.GRAY);
                g2.drawString("Chưa có dữ liệu", w / 2 - 70, h / 2);
                return;
            }

            int n = Math.min(data.size(), 8);

            double max = 0;

            for (int i = 0; i < n; i++) {
                max = Math.max(max, data.get(i).doanhThu);
            }

            if (max <= 0) {
                max = 1;
            }

            int groupW = (w - padLeft) / n;
            int barW = Math.max(18, Math.min(45, groupW / 4));

            int[] px = new int[n];
            int[] py = new int[n];

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fmL = g2.getFontMetrics();

            for (int i = 0; i < n; i++) {
                PeriodSummary p = data.get(i);

                int cx = padLeft + i * groupW + groupW / 2;

                String label = p.label;
                int textW = fmL.stringWidth(label);

                g2.setColor(Color.GRAY);
                g2.drawString(label, cx - textW / 2, h - 15);

                int revH = (int) (p.doanhThu / max * (h - padBottom - padTop - 15));
                int costH = (int) (p.chiPhi / max * (h - padBottom - padTop - 15));

                g2.setColor(new Color(110, 170, 230));
                g2.fillRect(cx - barW - 3, h - padBottom - revH, barW, revH);

                g2.setColor(new Color(140, 200, 130));
                g2.fillRect(cx + 3, h - padBottom - costH, barW, costH);

                px[i] = cx;
                py[i] = h - padBottom - (int) (p.loiNhuan / max * (h - padBottom - padTop - 15));
            }

            Stroke oldStr = g2.getStroke();

            g2.setStroke(new BasicStroke(3.5f));
            g2.setColor(new Color(250, 180, 100));

            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            }

            g2.setStroke(oldStr);
        }
    }
}