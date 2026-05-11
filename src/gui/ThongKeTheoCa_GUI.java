package gui;

import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.*;

import connectDB.ConnectDB;
import dao.CaLamViec_DAO;
import digLog.DongCa_DigLog;
import entity.CaLamViec;
import entity.TaiKhoan;

public class ThongKeTheoCa_GUI extends JPanel {


    private TaiKhoan taiKhoanDangNhap;
    private JLabel lblCa;
    private JPanel rightFormContainer;
    private MockAreaChartPanel chartPanel;

    private JTable tblHoaDonMain, tblMonMain;
    private DefaultTableModel modelHoaDonMain, modelMonMain;

    private CaThongKe caDangChon;
    private ThongKeCaData thongKeCa = new ThongKeCaData();

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ThongKeTheoCa_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createMainPanel(), BorderLayout.CENTER);

        loadCaGanNhatVaThongKe();
    }

    public ThongKeTheoCa_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(248, 249, 251));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 28, 36, 28));

        content.add(wrap(createHeaderPanel()));
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(wrap(createControlPanel()));
        content.add(Box.createRigidArea(new Dimension(0, 22)));
        content.add(wrap(createChartsSection()));
        content.add(Box.createRigidArea(new Dimension(0, 22)));
        content.add(wrap(createTablesSection()));

        JPanel scrollBody = new JPanel(new BorderLayout());
        scrollBody.setOpaque(false);
        scrollBody.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(scrollBody);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel wrap(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(c, BorderLayout.NORTH);
        return p;
    }

    private JPanel createHeaderPanel() {
        JPanel hdr = new JPanel(new BorderLayout(18, 0));
        hdr.setBackground(Color.WHITE);
        hdr.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("THỐNG KÊ THEO CA");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));

        

        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        hdr.add(titleBox, BorderLayout.WEST);

        lblCa = new JLabel("Ca --", SwingConstants.CENTER);
        lblCa.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCa.setOpaque(true);
        lblCa.setBackground(new Color(239, 246, 255));
        lblCa.setForeground(new Color(30, 64, 175));
        lblCa.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(191, 219, 254), 1),
                new EmptyBorder(10, 18, 10, 18)
        ));
        hdr.add(lblCa, BorderLayout.CENTER);

        JLabel lblDate = new JLabel("", SwingConstants.RIGHT);
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDate.setForeground(new Color(71, 85, 105));
        hdr.add(lblDate, BorderLayout.EAST);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
        Timer timer = new Timer(1000, e -> lblDate.setText(LocalDateTime.now().format(formatter)));
        timer.start();
        lblDate.setText(LocalDateTime.now().format(formatter));

        return hdr;
    }

    private JPanel createControlPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        p.setOpaque(false);

        JButton btnLamMoi = createButton("Làm mới", new Color(250, 235, 215), new Color(110, 80, 50));
        btnLamMoi.addActionListener(e -> loadCaGanNhatVaThongKe());

        JButton btnTongKet = createButton("Tổng kết ca", new Color(34, 197, 94), Color.WHITE);
        btnTongKet.addActionListener(e -> moDongCaDigLog());

        p.add(btnLamMoi);
        p.add(btnTongKet);

        return p;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(150, 45));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 190, 170), 1),
                new EmptyBorder(9, 22, 9, 22)
        ));
        return btn;
    }

    private void moDongCaDigLog() {
        CaLamViec_DAO caDAO = new CaLamViec_DAO();
        CaLamViec caDangMo = caDAO.layCaDangMo();

        if (caDangMo == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không có ca đang mở để tổng kết.\nVui lòng mở ca trước khi đóng ca.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        DongCa_DigLog dialog = new DongCa_DigLog(owner, caDangMo);
        dialog.setVisible(true);

        if (dialog.isDongCaThanhCong()) {
            loadCaGanNhatVaThongKe();
        }
    }

    private JPanel createChartsSection() {
        JPanel charts = new JPanel(new GridLayout(1, 2, 24, 0));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(0, 360));

        chartPanel = new MockAreaChartPanel();
        charts.add(createCard("DOANH THU THEO GIỜ", chartPanel));

        rightFormContainer = new JPanel(new BorderLayout());
        rightFormContainer.setOpaque(false);
        rightFormContainer.add(createThongTinNhanhPanel(), BorderLayout.CENTER);
        charts.add(rightFormContainer);

        return charts;
    }

    private JPanel createCard(String title, JComponent body) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel createThongTinNhanhPanel() {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("THÔNG TIN CA HIỆN TẠI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 21));
        lblTitle.setForeground(new Color(30, 64, 175));
        w.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(10, 2, 10, 13));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(8, 4, 0, 4));

        addFormRow(form, "Mã ca:", caDangChon == null ? "-" : caDangChon.maCa);
        addFormRow(form, "Tên ca:", caDangChon == null ? "-" : caDangChon.tenCa);
        addFormRow(form, "Thời gian mở:", formatDate(caDangChon == null ? null : caDangChon.moCa));
        addFormRow(form, "Thời gian đóng:", formatDate(caDangChon == null ? null : caDangChon.dongCa));
        addFormRow(form, "Số hóa đơn:", String.valueOf(thongKeCa.soHoaDon));
        addFormRow(form, "Doanh thu:", formatMoney(thongKeCa.doanhThu));
        addFormRow(form, "Tiền mặt:", formatMoney(thongKeCa.tienMat));
        addFormRow(form, "Chuyển khoản:", formatMoney(thongKeCa.tienChuyenKhoan));
        addFormRow(form, "Visa:", formatMoney(thongKeCa.tienVisa));
        addFormRow(form, "Món bán chạy:", thongKeCa.monBanChay == null ? "-" : thongKeCa.monBanChay);

        w.add(form, BorderLayout.CENTER);
        return w;
    }

    private JPanel createTablesSection() {
        JPanel section = new JPanel(new GridLayout(1, 2, 24, 0));
        section.setOpaque(false);
        section.setPreferredSize(new Dimension(0, 370));

        modelHoaDonMain = new DefaultTableModel(new String[]{"Mã HĐ", "Thời gian", "PTTT", "Tổng tiền"}, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelMonMain = new DefaultTableModel(new String[]{"Mã món", "Tên món", "SL", "Thành tiền"}, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDonMain = new JTable(modelHoaDonMain);
        tblMonMain = new JTable(modelMonMain);

        styleTable(tblHoaDonMain);
        styleTable(tblMonMain);

        setFixedWidths(tblHoaDonMain, new int[]{120, 170, 150, 220});
        setFixedWidths(tblMonMain, new int[]{120, 300, 90, 220});

        section.add(createTableBlock("DANH SÁCH THANH TOÁN TRONG CA", tblHoaDonMain));
        section.add(createTableBlock("THỐNG KÊ MÓN ĂN TRONG CA", tblMonMain));

        return section;
    }

    private JPanel createTableBlock(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 19));
        lbl.setForeground(new Color(15, 23, 42));
        panel.add(lbl, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(new Color(203, 213, 225), 1));
        sp.getViewport().setBackground(Color.WHITE);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        // Cho bảng giãn kín vùng hiển thị
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));

        // Không cho kéo đổi vị trí cột, không cho resize cột
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(241, 245, 249));
        headerRenderer.setForeground(new Color(15, 23, 42));
        headerRenderer.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBorder(new LineBorder(new Color(203, 213, 225), 1));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("SansSerif", Font.PLAIN, 15));
                c.setBorder(new EmptyBorder(0, 8, 0, 8));

                if (isSelected) {
                    c.setBackground(new Color(219, 234, 254));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                c.setForeground(Color.BLACK);

                String colName = t.getColumnName(column);
                if (colName.contains("Tên")) {
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                } else if (colName.contains("Tổng") || colName.contains("Thành")) {
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

    private void setFixedWidths(JTable table, int[] widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i);

            col.setPreferredWidth(widths[i]);
            col.setMinWidth(widths[i] / 2);

            // Không set MaxWidth để bảng được giãn kín vùng hiển thị
            // Nhưng vẫn không cho người dùng kéo resize
            col.setResizable(false);
        }

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
    }

    private void addFormRow(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(100, 116, 139));

        JLabel value = new JLabel(valueText == null ? "-" : valueText);
        value.setFont(new Font("SansSerif", Font.BOLD, 15));
        value.setForeground(new Color(15, 23, 42));

        panel.add(label);
        panel.add(value);
    }

    private void loadCaGanNhatVaThongKe() {
        caDangChon = layCaGanNhat();

        if (caDangChon == null) {
            thongKeCa = new ThongKeCaData();
            if (lblCa != null) lblCa.setText("Không có ca");
            refreshThongTinNhanh();
            refreshMainTables();
            return;
        }

        if (lblCa != null) {
            lblCa.setText(caDangChon.maCa + " - " + caDangChon.tenCa);
        }

        thongKeCa = tinhThongKeTheoCa(caDangChon);

        if (chartPanel != null) {
            chartPanel.setDoanhThuTheoGio(thongKeCa.doanhThuTheoGio);
        }

        refreshThongTinNhanh();
        refreshMainTables();
    }

    private void refreshThongTinNhanh() {
        if (rightFormContainer == null) return;

        rightFormContainer.removeAll();
        rightFormContainer.add(createThongTinNhanhPanel(), BorderLayout.CENTER);
        rightFormContainer.revalidate();
        rightFormContainer.repaint();

        if (chartPanel != null) chartPanel.repaint();
    }

    private void refreshMainTables() {
        if (modelHoaDonMain == null || modelMonMain == null) return;

        modelHoaDonMain.setRowCount(0);
        modelMonMain.setRowCount(0);

        for (HoaDonCaRow hd : thongKeCa.dsHoaDon) {
            modelHoaDonMain.addRow(new Object[]{
                    hd.maHD,
                    hd.thoiGian == null ? "-" : hd.thoiGian.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                    hd.phuongThuc,
                    formatMoney(hd.tongTien)
            });
        }

        for (MonThongKeRow m : thongKeCa.dsMon) {
            modelMonMain.addRow(new Object[]{
                    m.maMon,
                    m.tenMon,
                    m.soLuong,
                    formatMoney(m.thanhTien)
            });
        }
    }

    private CaThongKe layCaGanNhat() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            if (con == null) return null;

            String sql = "SELECT TOP 1 maCa, tenCa, thoiGianMoCa, thoiGianDongCa, tienMoCa, "
                    + "tienMatCuoiCa, tienChuyenKhoanCuoiCa, tienVisaCuoiCa "
                    + "FROM CaLamViec "
                    + "ORDER BY CASE WHEN thoiGianDongCa IS NULL THEN 0 ELSE 1 END, thoiGianMoCa DESC";

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                CaThongKe ca = new CaThongKe();
                ca.maCa = rs.getString("maCa");
                ca.tenCa = rs.getString("tenCa");
                ca.moCa = toLocalDateTime(rs.getTimestamp("thoiGianMoCa"));
                ca.dongCa = toLocalDateTime(rs.getTimestamp("thoiGianDongCa"));
                ca.tienMoCa = rs.getDouble("tienMoCa");
                ca.tienMatCuoiCa = rs.getDouble("tienMatCuoiCa");
                ca.tienChuyenKhoanCuoiCa = rs.getDouble("tienChuyenKhoanCuoiCa");
                ca.tienVisaCuoiCa = rs.getDouble("tienVisaCuoiCa");
                return ca;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }

        return null;
    }

    private ThongKeCaData tinhThongKeTheoCa(CaThongKe ca) {
        ThongKeCaData data = new ThongKeCaData();
        if (ca == null || ca.moCa == null) return data;

        LocalDateTime end = ca.dongCa == null ? LocalDateTime.now() : ca.dongCa;

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            if (con == null) return data;

            // 1. Doanh thu từ hóa đơn đã thanh toán
            String sql = "SELECT COUNT(*) AS soHD, "
            		+ "ISNULL(SUM(ISNULL(tongTien,0) + ISNULL(thueVAT,0)), 0) AS doanhThu, "
            		+ "ISNULL(SUM(CASE WHEN phuongThucThanhToan = N'Tiền mặt' THEN ISNULL(tongTien,0)+ISNULL(thueVAT,0) ELSE 0 END), 0) AS tienMat, "
            		+ "ISNULL(SUM(CASE WHEN phuongThucThanhToan = N'Chuyển khoản' THEN ISNULL(tongTien,0)+ISNULL(thueVAT,0) ELSE 0 END), 0) AS tienChuyenKhoan, "
            		+ "ISNULL(SUM(CASE WHEN phuongThucThanhToan IN (N'Visa', N'VISA') THEN ISNULL(tongTien,0)+ISNULL(thueVAT,0) ELSE 0 END), 0) AS tienVisa "
                    + "FROM HoaDon "
                    + "WHERE trangThai = N'Đã thanh toán' "
                    + "AND thoiGianRa >= ? AND thoiGianRa <= ?";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            rs = stmt.executeQuery();

            if (rs.next()) {
                data.soHoaDon = rs.getInt("soHD");
                data.doanhThu = rs.getDouble("doanhThu");
                data.tienMat = rs.getDouble("tienMat");
                data.tienChuyenKhoan = rs.getDouble("tienChuyenKhoan");
                data.tienVisa = rs.getDouble("tienVisa");
            }

            closeQuietly(rs);
            closeQuietly(stmt);

            // 2. Cộng tiền cọc đặt bàn phát sinh trong ca
            sql = "SELECT "
                    + "ISNULL(SUM(tienCoc), 0) AS tongCoc, "
                    + "ISNULL(SUM(CASE WHEN phuongThucThanhToanCoc = N'Tiền mặt' THEN tienCoc ELSE 0 END), 0) AS cocTienMat, "
                    + "ISNULL(SUM(CASE WHEN phuongThucThanhToanCoc = N'Chuyển khoản' THEN tienCoc ELSE 0 END), 0) AS cocChuyenKhoan, "
                    + "ISNULL(SUM(CASE WHEN phuongThucThanhToanCoc IN (N'Visa', N'VISA') THEN tienCoc ELSE 0 END), 0) AS cocVisa "
                    + "FROM PhieuDatBan "
                    + "WHERE thoiGianDatPhieu >= ? "
                    + "AND thoiGianDatPhieu <= ? "
                    + "AND trangThai <> N'Đã hủy'";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            rs = stmt.executeQuery();

            if (rs.next()) {
                double tongCoc = rs.getDouble("tongCoc");
                double cocTienMat = rs.getDouble("cocTienMat");
                double cocChuyenKhoan = rs.getDouble("cocChuyenKhoan");
                double cocVisa = rs.getDouble("cocVisa");

                data.doanhThu += tongCoc;
                data.tienMat += cocTienMat;
                data.tienChuyenKhoan += cocChuyenKhoan;
                data.tienVisa += cocVisa;
            }

            closeQuietly(rs);
            closeQuietly(stmt);

            // 3. Doanh thu theo giờ: hóa đơn + tiền cọc
            sql = "SELECT gio, SUM(doanhThu) AS doanhThu "
                    + "FROM ( "
                    + "    SELECT DATEPART(HOUR, thoiGianRa) AS gio, ISNULL(SUM(tongTien), 0) AS doanhThu "
                    + "    FROM HoaDon "
                    + "    WHERE trangThai = N'Đã thanh toán' "
                    + "    AND thoiGianRa >= ? AND thoiGianRa <= ? "
                    + "    GROUP BY DATEPART(HOUR, thoiGianRa) "
                    + "    UNION ALL "
                    + "    SELECT DATEPART(HOUR, thoiGianDatPhieu) AS gio, ISNULL(SUM(tienCoc), 0) AS doanhThu "
                    + "    FROM PhieuDatBan "
                    + "    WHERE thoiGianDatPhieu >= ? AND thoiGianDatPhieu <= ? "
                    + "    AND trangThai <> N'Đã hủy' "
                    + "    GROUP BY DATEPART(HOUR, thoiGianDatPhieu) "
                    + ") x "
                    + "GROUP BY gio";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            stmt.setTimestamp(3, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(4, Timestamp.valueOf(end));
            rs = stmt.executeQuery();

            while (rs.next()) {
                int gio = rs.getInt("gio");
                if (gio >= 0 && gio < data.doanhThuTheoGio.length) {
                    data.doanhThuTheoGio[gio] = rs.getDouble("doanhThu");
                }
            }

            closeQuietly(rs);
            closeQuietly(stmt);

            // 4. Danh sách hóa đơn thanh toán trong ca
            sql = "SELECT maHD, thoiGianRa, phuongThucThanhToan, "
                    + "ISNULL(tongTien,0)+ISNULL(thueVAT,0) AS tongTien "
                    + "FROM HoaDon "
                    + "WHERE trangThai = N'Đã thanh toán' "
                    + "AND thoiGianRa >= ? AND thoiGianRa <= ? "
                    + "ORDER BY thoiGianRa";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            rs = stmt.executeQuery();

            while (rs.next()) {
                HoaDonCaRow row = new HoaDonCaRow();
                row.maHD = rs.getString("maHD");
                row.thoiGian = toLocalDateTime(rs.getTimestamp("thoiGianRa"));
                row.phuongThuc = rs.getString("phuongThucThanhToan");
                row.tongTien = rs.getDouble("tongTien");
                data.dsHoaDon.add(row);
            }

            closeQuietly(rs);
            closeQuietly(stmt);

            // 5. Thêm dòng tiền cọc vào bảng danh sách thanh toán
            sql = "SELECT maPhieuDatBan, thoiGianDatPhieu, phuongThucThanhToanCoc, tienCoc "
                    + "FROM PhieuDatBan "
                    + "WHERE thoiGianDatPhieu >= ? AND thoiGianDatPhieu <= ? "
                    + "AND trangThai <> N'Đã hủy' "
                    + "AND tienCoc > 0 "
                    + "ORDER BY thoiGianDatPhieu";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            rs = stmt.executeQuery();

            while (rs.next()) {
                HoaDonCaRow row = new HoaDonCaRow();
                row.maHD = "Cọc " + rs.getString("maPhieuDatBan");
                row.thoiGian = toLocalDateTime(rs.getTimestamp("thoiGianDatPhieu"));
                row.phuongThuc = rs.getString("phuongThucThanhToanCoc");
                row.tongTien = rs.getDouble("tienCoc");
                data.dsHoaDon.add(row);
            }

            closeQuietly(rs);
            closeQuietly(stmt);

            // 6. Thống kê món ăn trong ca
            sql = "SELECT m.maMon, m.tenMon, "
                    + "ISNULL(SUM(ct.soLuong - ISNULL(ct.soLuongHuy, 0)), 0) AS tongSL, "
                    + "ISNULL(SUM((ct.soLuong - ISNULL(ct.soLuongHuy, 0)) * ct.donGia), 0) AS thanhTien "
                    + "FROM HoaDon hd "
                    + "JOIN ChiTietHoaDon ct ON hd.maHD = ct.maHD "
                    + "JOIN MonAn m ON ct.maMon = m.maMon "
                    + "WHERE hd.trangThai = N'Đã thanh toán' "
                    + "AND hd.thoiGianRa >= ? AND hd.thoiGianRa <= ? "
                    + "AND (ct.trangThai IS NULL OR ct.trangThai <> N'Đã hủy') "
                    + "GROUP BY m.maMon, m.tenMon "
                    + "ORDER BY tongSL DESC, thanhTien DESC";

            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            rs = stmt.executeQuery();

            while (rs.next()) {
                MonThongKeRow row = new MonThongKeRow();
                row.maMon = rs.getString("maMon");
                row.tenMon = rs.getString("tenMon");
                row.soLuong = rs.getInt("tongSL");
                row.thanhTien = rs.getDouble("thanhTien");
                data.dsMon.add(row);
                data.tongSoLuongMon += row.soLuong;
            }

            data.soLoaiMon = data.dsMon.size();
            if (!data.dsMon.isEmpty()) {
                data.monBanChay = data.dsMon.get(0).tenMon;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }

        return data;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(dateTimeFormat);
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value) + " VNĐ";
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CaThongKe {
        String maCa;
        String tenCa;
        LocalDateTime moCa;
        LocalDateTime dongCa;
        double tienMoCa;
        double tienMatCuoiCa;
        double tienChuyenKhoanCuoiCa;
        double tienVisaCuoiCa;
    }

    private static class HoaDonCaRow {
        String maHD;
        LocalDateTime thoiGian;
        String phuongThuc;
        double tongTien;
    }

    private static class MonThongKeRow {
        String maMon;
        String tenMon;
        int soLuong;
        double thanhTien;
    }

    private static class ThongKeCaData {
        int soHoaDon;
        int tongSoLuongMon;
        int soLoaiMon;
        double doanhThu;
        double tienMat;
        double tienChuyenKhoan;
        double tienVisa;
        String monBanChay;
        double[] doanhThuTheoGio = new double[24];
        List<HoaDonCaRow> dsHoaDon = new ArrayList<>();
        List<MonThongKeRow> dsMon = new ArrayList<>();
    }

    class MockAreaChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private double[] doanhThuTheoGio = new double[24];

        public MockAreaChartPanel() {
            setOpaque(false);
        }

        public void setDoanhThuTheoGio(double[] doanhThuTheoGio) {
            if (doanhThuTheoGio != null && doanhThuTheoGio.length == 24) {
                this.doanhThuTheoGio = doanhThuTheoGio;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft = 55;
            int padBottom = 30;
            int padTop = 20;
            int chartH = h - padBottom - padTop;

            double max = 0;
            for (double v : doanhThuTheoGio) {
                max = Math.max(max, v);
            }
            if (max <= 0) max = 1;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

            for (int i = 0; i < 5; i++) {
                int y = h - padBottom - (i * chartH / 4);

                g2.setColor(new Color(240, 240, 240));
                g2.drawLine(padLeft, y, w - 5, y);

                g2.setColor(Color.GRAY);
                String label = i == 0 ? "0" : moneyFormat.format(max * i / 4 / 1000000) + "M";
                g2.drawString(label, 8, y + 4);
            }

            int n = 24;
            double groupW = (w - padLeft - 10) / (double) n;
            int[] px = new int[n];
            int[] py = new int[n];

            for (int i = 0; i < n; i++) {
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);
                px[i] = cx;

                int lineH = (int) (doanhThuTheoGio[i] / max * chartH);
                py[i] = h - padBottom - lineH;

                if (i % 2 == 0) {
                    String lbl = i + "h";
                    int textW = g2.getFontMetrics().stringWidth(lbl);
                    g2.setColor(Color.GRAY);
                    g2.drawString(lbl, cx - textW / 2, h - 10);
                }
            }

            Polygon poly = new Polygon();
            poly.addPoint(px[0], h - padBottom);
            for (int i = 0; i < n; i++) {
                poly.addPoint(px[i], py[i]);
            }
            poly.addPoint(px[n - 1], h - padBottom);

            g2.setColor(new Color(110, 170, 230, 80));
            g2.fillPolygon(poly);

            g2.setColor(new Color(80, 150, 200));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(2f));

            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            }

            g2.setStroke(oldStr);

            for (int i = 0; i < n; i++) {
                g2.setColor(Color.WHITE);
                g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
                g2.setColor(new Color(80, 150, 200));
                g2.drawOval(px[i] - 3, py[i] - 3, 6, 6);
            }
        }
    }
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            SwingUtilities.invokeLater(this::loadCaGanNhatVaThongKe);
        }
    }
}