package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class ThongKeTheoCa_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;
    private JPanel rightFormContainer;
    private JLabel lblCa;
    private MockAreaChartPanel chartPanel;

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

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(22, 28, 36, 28));

        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        contentContainer.add(wrapInNorth(createControlPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 30)));
        contentContainer.add(wrapInNorth(createChartsSection()));

        JPanel smoothScrollWrapper = new JPanel(new BorderLayout());
        smoothScrollWrapper.setOpaque(false);
        smoothScrollWrapper.add(contentContainer, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(smoothScrollWrapper);
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

    private JPanel createControlPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnLamMoi.setBackground(new Color(250, 235, 215));
        btnLamMoi.setForeground(new Color(110, 80, 50));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setOpaque(true);
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 190, 170), 1),
                new EmptyBorder(9, 22, 9, 22)));
        btnLamMoi.addActionListener(e -> loadCaGanNhatVaThongKe());
        p.add(btnLamMoi);

        JButton btnTongKet = new JButton("Tổng kết ca");
        btnTongKet.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnTongKet.setBackground(new Color(34, 197, 94));
        btnTongKet.setForeground(Color.WHITE);
        btnTongKet.setFocusPainted(false);
        btnTongKet.setOpaque(true);
        btnTongKet.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(22, 163, 74), 1, true),
                new EmptyBorder(9, 22, 9, 22)));
        btnTongKet.addActionListener(e -> showTongKetCaForm());
        p.add(btnTongKet);
        return p;
    }

    private JPanel createHeaderPanel() {
        JPanel hdr = new JPanel(new BorderLayout(18, 0));
        hdr.setBackground(Color.WHITE);
        hdr.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 22, 18, 22)));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Thống kê theo ca");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Theo dõi doanh thu, số hóa đơn và tiền cuối ca từ dữ liệu SQL");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(lblSub);
        hdr.add(titleBox, BorderLayout.WEST);

        lblCa = new JLabel("Ca --");
        lblCa.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCa.setHorizontalAlignment(SwingConstants.CENTER);
        lblCa.setOpaque(true);
        lblCa.setBackground(new Color(239, 246, 255));
        lblCa.setForeground(new Color(30, 64, 175));
        lblCa.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(10, 18, 10, 18)));
        hdr.add(lblCa, BorderLayout.CENTER);

        JLabel lblDate = new JLabel();
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDate.setForeground(new Color(71, 85, 105));
        lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
        hdr.add(lblDate, BorderLayout.EAST);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            lblDate.setText(LocalDateTime.now().format(formatter));
        });
        timer.start();
        lblDate.setText(LocalDateTime.now().format(formatter));
        return hdr;
    }

    private JPanel createChartsSection() {
        JPanel charts = new JPanel(new GridLayout(1, 2, 25, 0));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(0, 480));

        chartPanel = new MockAreaChartPanel();
        charts.add(createChartWrapper("DOANH THU THEO GIỜ", chartPanel));

        rightFormContainer = new JPanel(new BorderLayout());
        rightFormContainer.setOpaque(false);
        rightFormContainer.add(createThongTinNhanhPanel(), BorderLayout.CENTER);
        charts.add(rightFormContainer);
        return charts;
    }

    private JPanel createChartWrapper(String title, JPanel chart) {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JLabel lblTop = new JLabel(title);
        lblTop.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTop.setForeground(new Color(51, 65, 85));
        hdr.add(lblTop, BorderLayout.WEST);

        JLabel lblIcons = new JLabel("≡ ️");
        lblIcons.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcons.setForeground(Color.GRAY);
        hdr.add(lblIcons, BorderLayout.EAST);

        w.add(hdr, BorderLayout.NORTH);
        w.add(chart, BorderLayout.CENTER);
        return w;
    }

    private JPanel createThongTinNhanhPanel() {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel("THÔNG TIN CA HIỆN TẠI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 21));
        lblTitle.setForeground(new Color(30, 64, 175));
        w.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 15));
        form.setBackground(Color.WHITE);

        addFormRow(form, "Mã ca:", caDangChon == null ? "-" : caDangChon.maCa);
        addFormRow(form, "Tên ca:", caDangChon == null ? "-" : caDangChon.tenCa);
        addFormRow(form, "Thời gian mở:", formatDate(caDangChon == null ? null : caDangChon.moCa));
        addFormRow(form, "Thời gian đóng:", formatDate(caDangChon == null ? null : caDangChon.dongCa));
        addFormRow(form, "Số hóa đơn:", String.valueOf(thongKeCa.soHoaDon));
        addFormRow(form, "Doanh thu:", formatMoney(thongKeCa.doanhThu));
        addFormRow(form, "Tiền mặt:", formatMoney(thongKeCa.tienMat));
        addFormRow(form, "Chuyển khoản/Visa:", formatMoney(thongKeCa.tienChuyenKhoan + thongKeCa.tienVisa));

        w.add(form, BorderLayout.CENTER);
        return w;
    }

    private void showTongKetCaForm() {
        if (caDangChon == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ca làm việc để tổng kết.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        rightFormContainer.removeAll();

        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("XÁC NHẬN TỔNG KẾT CA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(40, 167, 69));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);
        addFormRow(formPanel, "Mã ca:", caDangChon.maCa);
        addFormRow(formPanel, "Tên ca:", caDangChon.tenCa);
        addFormRow(formPanel, "Thời gian mở:", formatDate(caDangChon.moCa));
        addFormRow(formPanel, "Thời gian đóng:", caDangChon.dongCa == null ? "Sẽ đóng lúc xác nhận" : formatDate(caDangChon.dongCa));
        addFormRow(formPanel, "Tiền mở ca:", formatMoney(caDangChon.tienMoCa));
        addFormRow(formPanel, "Tiền mặt cuối ca:", formatMoney(thongKeCa.tienMat));
        addFormRow(formPanel, "Tiền chuyển khoản:", formatMoney(thongKeCa.tienChuyenKhoan));
        addFormRow(formPanel, "Tiền Visa:", formatMoney(thongKeCa.tienVisa));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnXacNhan = new JButton("Xác Nhận");
        btnXacNhan.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnXacNhan.setPreferredSize(new Dimension(150, 50));
        btnXacNhan.setBackground(new Color(40, 167, 69));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.addActionListener(e -> xacNhanTongKetCa());
        btnPanel.add(btnXacNhan);

        mainPanel.add(btnPanel);
        w.add(mainPanel, BorderLayout.CENTER);

        rightFormContainer.add(w, BorderLayout.CENTER);
        rightFormContainer.revalidate();
        rightFormContainer.repaint();
    }

    private void xacNhanTongKetCa() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận tổng kết ca " + caDangChon.maCa + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) {
                throw new Exception("Không kết nối được CSDL.");
            }

            String sql = "UPDATE CaLamViec "
                    + "SET thoiGianDongCa = ISNULL(thoiGianDongCa, GETDATE()), "
                    + "tienMatCuoiCa = ?, tienChuyenKhoanCuoiCa = ?, tienVisaCuoiCa = ? "
                    + "WHERE maCa = ?";
            stmt = con.prepareStatement(sql);
            stmt.setDouble(1, thongKeCa.tienMat);
            stmt.setDouble(2, thongKeCa.tienChuyenKhoan);
            stmt.setDouble(3, thongKeCa.tienVisa);
            stmt.setString(4, caDangChon.maCa);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Đã lưu tổng kết ca thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCaGanNhatVaThongKe();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ca để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Tổng kết ca thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeQuietly(stmt);
        }
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
            if (lblCa != null) {
                lblCa.setText("Không có ca");
            }
            refreshThongTinNhanh();
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
    }

    private void refreshThongTinNhanh() {
        if (rightFormContainer == null) {
            return;
        }
        rightFormContainer.removeAll();
        rightFormContainer.add(createThongTinNhanhPanel(), BorderLayout.CENTER);
        rightFormContainer.revalidate();
        rightFormContainer.repaint();
        if (chartPanel != null) {
            chartPanel.repaint();
        }
    }

    private CaThongKe layCaGanNhat() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) {
                return null;
            }

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
        if (ca == null || ca.moCa == null) {
            return data;
        }

        LocalDateTime end = ca.dongCa == null ? LocalDateTime.now() : ca.dongCa;

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) {
                return data;
            }

            String sql = "SELECT "
                    + "COUNT(*) AS soHD, "
                    + "ISNULL(SUM(tongTien), 0) AS doanhThu, "
                    + "ISNULL(SUM(CASE WHEN phuongThucThanhToan = N'Tiền mặt' THEN tongTien ELSE 0 END), 0) AS tienMat, "
                    + "ISNULL(SUM(CASE WHEN phuongThucThanhToan = N'Chuyển khoản' THEN tongTien ELSE 0 END), 0) AS tienChuyenKhoan, "
                    + "ISNULL(SUM(CASE WHEN phuongThucThanhToan = N'Visa' THEN tongTien ELSE 0 END), 0) AS tienVisa "
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

            sql = "SELECT DATEPART(HOUR, thoiGianRa) AS gio, ISNULL(SUM(tongTien), 0) AS doanhThu "
                    + "FROM HoaDon "
                    + "WHERE trangThai = N'Đã thanh toán' "
                    + "AND thoiGianRa >= ? AND thoiGianRa <= ? "
                    + "GROUP BY DATEPART(HOUR, thoiGianRa)";
            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(ca.moCa));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            rs = stmt.executeQuery();
            while (rs.next()) {
                int gio = rs.getInt("gio");
                if (gio >= 0 && gio < data.doanhThuTheoGio.length) {
                    data.doanhThuTheoGio[gio] = rs.getDouble("doanhThu");
                }
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
        if (closeable == null) {
            return;
        }
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

    private static class ThongKeCaData {
        int soHoaDon;
        double doanhThu;
        double tienMat;
        double tienChuyenKhoan;
        double tienVisa;
        double[] doanhThuTheoGio = new double[24];
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
            if (max <= 0) {
                max = 1;
            }

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

            java.awt.Polygon poly = new java.awt.Polygon();
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
}
