package digLog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

import connectDB.ConnectDB;

public class ChiTietHoaDon_DigLog extends JDialog {

    private String maHD;

    // ── Màu sắc ──────────────────────────────────────────────────────────────
    private static final Color CLR_HEADER    = new Color(74, 55, 40);
    private static final Color CLR_SECTION   = new Color(50, 40, 30);
    private static final Color CLR_TABLE_HDR = new Color(160, 100, 60);
    private static final Color CLR_RED       = new Color(200, 50, 50);
    private static final Color CLR_GREEN     = new Color(40, 130, 60);
    private static final Color CLR_BTN       = new Color(210, 150, 70);
    private static final Color CLR_BG        = Color.WHITE;
    private static final Color CLR_DIVIDER   = new Color(200, 190, 175);

    // ── Labels thông tin chung ────────────────────────────────────────────────
    private JLabel lblMaHD, lblThoiGian, lblKhach, lblNV, lblSDT, lblMaPhieu;

    // ── Labels thanh toán ────────────────────────────────────────────────────
    private JLabel lblTongTien, lblKhuyenMai, lblDiemTichLuy;
    private JLabel lblThanhToan, lblKhachTra, lblTienThoi;

    // ── Bảng món ─────────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel model;

    // =========================================================================
    public ChiTietHoaDon_DigLog(JFrame parent, String maHD) {
        super(parent, "Chi tiết hóa đơn", true);
        this.maHD = maHD;

        setSize(520, 840);
        setLocationRelativeTo(parent);
        setResizable(false);

        setLayout(new BorderLayout());
        add(buildHeader(),     BorderLayout.NORTH);
        add(buildScrollBody(), BorderLayout.CENTER);

        loadData();
    }

    // =========================================================================
    // HEADER – sát viền
    // =========================================================================
    private JLabel buildHeader() {
        JLabel title = new JLabel("CHI TIẾT HÓA ĐƠN", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(CLR_HEADER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setPreferredSize(new Dimension(0, 64));
        title.setBorder(null);
        return title;
    }

    // =========================================================================
    // BODY
    // =========================================================================
    private JScrollPane buildScrollBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(CLR_BG);

        body.add(sectionTitle("THÔNG TIN CHUNG"));
        body.add(buildInfoPanel());
        body.add(divider());

        body.add(sectionTitle("DANH SÁCH MÓN ĂN"));
        body.add(buildTablePanel());
        body.add(divider());

        body.add(sectionTitle("THANH TOÁN"));
        body.add(buildPayPanel());

        body.add(buildCloseButton());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // =========================================================================
    // THÔNG TIN CHUNG
    // =========================================================================
    private JPanel buildInfoPanel() {
        lblMaHD     = valueLabel();
        lblThoiGian = valueLabel();
        lblKhach    = valueLabel();
        lblNV       = valueLabel();
        lblSDT      = valueLabel();
        lblMaPhieu  = valueLabel();

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CLR_BG);
        p.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 20, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.NONE;

        int y = 0;
        addInfoRow(p, gbc, y++, "Mã hóa đơn :",   lblMaHD);
        addInfoRow(p, gbc, y++, "Thời gian tạo :", lblThoiGian);
        addInfoRow(p, gbc, y++, "Khách hàng :",    lblKhach);
        addInfoRow(p, gbc, y++, "Nhân viên :",     lblNV);
        addInfoRow(p, gbc, y++, "SĐT :",           lblSDT);
        addInfoRow(p, gbc, y,   "Mã P.Đặt bàn :", lblMaPhieu);

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height + 10));
        return p;
    }

    // =========================================================================
    // BẢNG MÓN ĂN
    // =========================================================================
    private JPanel buildTablePanel() {
        String[] cols = {"Mã", "Tên món", "SL", "Đơn giá", "T.Tiền"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 210, 195));
        table.setBackground(CLR_BG);

        JTableHeader header = table.getTableHeader();
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i : new int[]{0, 2}) table.getColumnModel().getColumn(i).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        for (int i : new int[]{3, 4}) table.getColumnModel().getColumn(i).setCellRenderer(right);

        int[] widths = {55, 160, 40, 100, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(470, 160));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
        wrapper.setBackground(CLR_BG);
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.add(scroll);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        return wrapper;
    }

    // =========================================================================
    // THANH TOÁN
    // =========================================================================
    private JPanel buildPayPanel() {
        lblTongTien    = valueLabel();
        lblKhuyenMai   = valueLabel();
        lblDiemTichLuy = valueLabel();
        lblThanhToan   = valueLabel();
        lblKhachTra    = valueLabel();
        lblTienThoi    = valueLabel();

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CLR_BG);
        p.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        int y = 0;
        addPayRow(p, gbc, y++, "Tổng tiền :",            lblTongTien,    false, true);
        addPayRow(p, gbc, y++, "Khuyến mãi",             lblKhuyenMai,   false, false);
        addPayRow(p, gbc, y++, "Điểm tích lũy :",        lblDiemTichLuy, false, false);
        addPayRow(p, gbc, y++, "Số tiền cần thanh toán", lblThanhToan,   true,  true);
        addPayRow(p, gbc, y++, "Khách trả :",            lblKhachTra,    false, false);
        addPayRow(p, gbc, y,   "Tiền thối :",            lblTienThoi,    false, false);

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height + 16));
        return p;
    }

    // =========================================================================
    // NÚT THOÁT
    // =========================================================================
    private JPanel buildCloseButton() {
        JButton btn = new JButton("Thoát") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? CLR_BTN.darker() : CLR_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.setBackground(CLR_BG);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(btn);
        return panel;
    }

    // =========================================================================
    // LOAD DATA TỪ DATABASE
    // =========================================================================
    private void loadData() {
        try {
            Connection con = ConnectDB.getConnection();

            // ── Thông tin hóa đơn ────────────────────────────────────────────
            String sql = """
                SELECT hd.maHD,
                       hd.thoiGianVao,
                       kh.tenKH,
                       nv.hoTen,
                       kh.sdt,
                       pd.maPhieuDatBan,
                       km.tenKhuyenMai,
                       km.giaTri
                FROM HoaDon hd
                LEFT JOIN PhieuDatBan pd ON hd.maPhieuDatBan = pd.maPhieuDatBan
                LEFT JOIN KhachHang   kh ON hd.maKH  = kh.maKH
                LEFT JOIN NhanVien    nv ON hd.maNV   = nv.maNV
                LEFT JOIN KhuyenMai   km ON hd.maKM   = km.maKM
                WHERE hd.maHD = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();

            String tenKM    = "";
            double giaTriKM = 0;

            if (rs.next()) {
                lblMaHD.setText(rs.getString("maHD"));

                Timestamp ts = rs.getTimestamp("thoiGianVao");
                lblThoiGian.setText(ts != null
                        ? new SimpleDateFormat("HH:mm 'Ngày' dd-MM-yyyy").format(ts) : "");

                lblKhach.setText(nullSafe(rs.getString("tenKH")));
                lblNV   .setText(nullSafe(rs.getString("hoTen")));
                lblSDT  .setText(nullSafe(rs.getString("sdt")));

                String maPhieu = rs.getString("maPhieuDatBan");
                lblMaPhieu.setText(nullSafe(maPhieu));
                lblMaPhieu.setVisible(maPhieu != null && !maPhieu.isBlank());

                tenKM    = nullSafe(rs.getString("tenKhuyenMai"));
                giaTriKM = rs.getDouble("giaTri");
            }

            // ── Chi tiết món ─────────────────────────────────────────────────
            String sqlMon = """
                SELECT ct.maMon, m.tenMon, ct.soLuong, m.donGia,
                       ct.soLuong * m.donGia AS thanhTien
                FROM ChiTietHoaDon ct
                JOIN MonAn m ON ct.maMon = m.maMon
                WHERE ct.maHD = ?
            """;

            ps = con.prepareStatement(sqlMon);
            ps.setString(1, maHD);
            rs = ps.executeQuery();

            model.setRowCount(0);
            double tongTien = 0;

            while (rs.next()) {
                double tt = rs.getDouble("thanhTien");
                tongTien += tt;
                model.addRow(new Object[]{
                        rs.getString("maMon"),
                        rs.getString("tenMon"),
                        rs.getInt("soLuong"),
                        formatTien(rs.getDouble("donGia")),
                        formatTien(tt)
                });
            }

            // ── Tính khuyến mãi ──────────────────────────────────────────────
            // giaTri <= 100 → phần trăm | giaTri > 100 → tiền cố định
            double soTienGiam = 0;

            if (!tenKM.isEmpty()) {
                if (giaTriKM > 0 && giaTriKM <= 100) {
                    soTienGiam = tongTien * giaTriKM / 100.0;
                    lblKhuyenMai.setText(tenKM + "  (-" + (int) giaTriKM + "%)");
                } else {
                    soTienGiam = giaTriKM;
                    lblKhuyenMai.setText(tenKM + "  (-" + formatTien(giaTriKM) + ")");
                }
                lblKhuyenMai.setForeground(CLR_GREEN);
            } else {
                lblKhuyenMai.setText("Không có");
                lblKhuyenMai.setForeground(Color.GRAY);
            }

            double thanhToan = tongTien - soTienGiam;

            lblTongTien.setText(formatTien(tongTien));
            lblTongTien.setForeground(CLR_RED);

            long diem = (long) (thanhToan / 1000);
            lblDiemTichLuy.setText("+" + diem + " điểm");
            lblDiemTichLuy.setForeground(new Color(30, 100, 200));

            lblThanhToan.setText(formatTien(thanhToan));

            // ── Tiền khách trả: đọc từ DB + ràng buộc hiển thị ──────────────
            try {
                String sqlTra = "SELECT tienKhachTra FROM HoaDon WHERE maHD = ?";
                PreparedStatement psTra = con.prepareStatement(sqlTra);
                psTra.setString(1, maHD);
                ResultSet rsTra = psTra.executeQuery();

                if (rsTra.next()) {
                    double tienTra = rsTra.getDouble("tienKhachTra");

                    if (tienTra > 0) {
                        lblKhachTra.setText(formatTien(tienTra));

                        // ── RÀNG BUỘC: tienKhachTra phải >= thanhToan ────────
                        if (tienTra < thanhToan) {
                            // Dữ liệu vi phạm → cảnh báo đỏ + tooltip
                            lblKhachTra.setForeground(CLR_RED);
                            lblKhachTra.setToolTipText(
                                    "⚠ Tiền khách trả (" + formatTien(tienTra)
                                    + ") nhỏ hơn số tiền cần thanh toán!");
                            lblTienThoi.setText("⚠ Không hợp lệ");
                            lblTienThoi.setForeground(CLR_RED);
                        } else {
                            // Hợp lệ → tính tiền thối bình thường
                            lblKhachTra.setForeground(Color.BLACK);
                            lblTienThoi.setText(formatTien(tienTra - thanhToan));
                            lblTienThoi.setForeground(Color.BLACK);
                        }

                    } else {
                        lblKhachTra.setText("Chưa thanh toán");
                        lblKhachTra.setForeground(Color.GRAY);
                        lblTienThoi.setText("—");
                        lblTienThoi.setForeground(Color.GRAY);
                    }
                }
            } catch (Exception ignored) {
                // Cột tienKhachTra chưa có trong DB → để trống, không crash
                lblKhachTra.setText("—");
                lblTienThoi.setText("—");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================
    private JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setForeground(CLR_SECTION);
        lbl.setBorder(new EmptyBorder(10, 0, 6, 0));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return lbl;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(CLR_DIVIDER);
        sep.setAlignmentX(LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    private JLabel valueLabel() {
        JLabel lbl = new JLabel();
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return lbl;
    }

    private void addInfoRow(JPanel p, GridBagConstraints gbc, int y,
                            String labelText, JLabel value) {
        gbc.gridy = y;
        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        p.add(value, gbc);
    }

    private void addPayRow(JPanel p, GridBagConstraints gbc, int y,
                           String labelText, JLabel value,
                           boolean bold, boolean red) {
        gbc.gridy = y;
        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 13));
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        value.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 13));
        value.setHorizontalAlignment(JLabel.RIGHT);
        if (red) value.setForeground(CLR_RED);
        p.add(value, gbc);
    }

    private String nullSafe(String s) { return s != null ? s : ""; }

    private String formatTien(double t) {
        return String.format("%,.0f", t).replace(",", ".") + " VND";
    }
}