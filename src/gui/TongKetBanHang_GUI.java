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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;

    private JDateChooser startChooser;
    private JDateChooser endChooser;
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

    public TongKetBanHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(createMainPanel(), BorderLayout.CENTER);

        setDefaultDates();
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
        contentContainer.setBorder(new EmptyBorder(22, 32, 36, 32));

        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 28)));
        contentContainer.add(wrapInNorth(createKpiPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 28)));
        contentContainer.add(wrapInNorth(createMidScaleSection()));

        JScrollPane scroll = new JScrollPane(contentContainer);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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

        JLabel lblTitle = new JLabel("Tổng kết bán hàng");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Tổng hợp món bán, số lượng và doanh thu theo khoảng ngày");
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
        JPanel kpi = new JPanel(new GridBagLayout());
        kpi.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 18);

        lblTongMonBan = new JLabel("0", SwingConstants.CENTER);
        lblTongSoLuong = new JLabel("0", SwingConstants.CENTER);
        lblDoanhThu = new JLabel("0đ", SwingConstants.CENTER);
        lblBanChayNhat = new JLabel("-", SwingConstants.CENTER);

        JPanel cardTongMon = createKpiCard("Tổng món bán", lblTongMonBan, new Color(235, 243, 255), new Color(100, 130, 150));
        JPanel cardTongSL = createKpiCard("Tổng số lượng", lblTongSoLuong, new Color(235, 250, 240), new Color(120, 150, 130));
        JPanel cardDoanhThu = createKpiCard("Doanh thu", lblDoanhThu, new Color(255, 250, 235), new Color(150, 140, 100));
        JPanel cardBanChay = createKpiCard("Bán chạy nhất", lblBanChayNhat, new Color(255, 235, 245), new Color(150, 100, 120));

        cardTongMon.setPreferredSize(new Dimension(220, 115));
        cardTongSL.setPreferredSize(new Dimension(220, 115));
        cardDoanhThu.setPreferredSize(new Dimension(220, 115));
        cardBanChay.setPreferredSize(new Dimension(300, 115));

        gbc.gridx = 0;
        gbc.weightx = 0.15;
        kpi.add(cardTongMon, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.15;
        kpi.add(cardTongSL, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.17;
        kpi.add(cardDoanhThu, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.31;
        kpi.add(cardBanChay, gbc);

        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        JButton btnExport = new JButton("Xuất báo cáo");
        btnExport.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnExport.setBackground(new Color(105, 185, 115));
        btnExport.setForeground(Color.DARK_GRAY);
        btnExport.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90, 160, 90), 1),
                new EmptyBorder(0, 0, 0, 0)));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> exportBaoCaoExcel());

        JPanel innerBtnWrap = new JPanel(new BorderLayout());
        innerBtnWrap.setOpaque(false);
        innerBtnWrap.setBorder(new EmptyBorder(12, 0, 12, 0));
        innerBtnWrap.add(btnExport, BorderLayout.CENTER);
        btnWrap.add(innerBtnWrap, BorderLayout.CENTER);
        btnWrap.setPreferredSize(new Dimension(230, 115));

        gbc.gridx = 4;
        gbc.weightx = 0.18;
        gbc.insets = new Insets(0, 0, 0, 0);
        kpi.add(btnWrap, gbc);

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

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);
        wrap.add(lblTitle, BorderLayout.NORTH);
        wrap.add(valueLabel, BorderLayout.CENTER);
        card.add(wrap, BorderLayout.CENTER);
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
        th.setReorderingAllowed(false); // Khóa không cho kéo đổi vị trí cột
        th.setResizingAllowed(false);   // Khóa không cho kéo thay đổi độ rộng cột
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

        // Chỉ khóa thao tác kéo cột, KHÔNG setMaxWidth để JTable tự dàn đều hết chiều ngang.
        // Nếu setMaxWidth = PreferredWidth thì bảng sẽ bị co lại và dư một mảng trống màu xanh ở bên phải.
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
            @Override
            public void tableChanged(TableModelEvent e) {
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

        // Giữ bảng nằm gọn ở phía trên. Nếu để tableWrap trực tiếp ở CENTER,
        // khi tăng chiều cao biểu đồ bên phải thì bảng bên trái sẽ bị kéo cao theo.
        JPanel tableWrapHolder = new JPanel(new BorderLayout());
        tableWrapHolder.setOpaque(false);
        tableWrapHolder.add(tableWrap, BorderLayout.NORTH);

        leftCol.add(tableWrapHolder, BorderLayout.CENTER);
        mid.add(leftCol, BorderLayout.CENTER);

        JPanel rightCol = new JPanel(new GridLayout(2, 1, 0, 24));
        rightCol.setOpaque(false);
        // Tăng chiều rộng cột biểu đồ để biểu đồ dịch sang trái một chút,
        // đồng thời làm bảng bên trái hẹp lại vừa phải.
        rightCol.setPreferredSize(new Dimension(620, 440));
        rightCol.setMinimumSize(new Dimension(580, 400));

        chartTopMon = new MockBarTopMon();
        chartDoanhThu = new MockLineDoanhThu();

        JPanel chart1 = createChartWrapper("BIỂU ĐỒ TOP MÓN BÁN CHẠY", chartTopMon);
        JPanel chart2 = createChartWrapper("DOANH THU THEO NGÀY", chartDoanhThu);
        // Cho mỗi biểu đồ cao hơn để nhìn thoáng và kéo dài xuống dưới hơn.
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

    private void exportBaoCaoExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất báo cáo.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo doanh thu");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel 97-2003 (*.xls)", "xls"));
        chooser.setSelectedFile(new File("BaoCaoDoanhThu.xls"));

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

            writer.write("<Style ss:ID=\"Default\" ss:Name=\"Normal\">");
            writer.write("<Alignment ss:Vertical=\"Center\"/>");
            writer.write("<Borders/>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Size=\"11\"/>");
            writer.write("<Interior/>");
            writer.write("<NumberFormat/>");
            writer.write("<Protection/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"Title\">");
            writer.write("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Bold=\"1\" ss:Size=\"18\"/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"SubInfo\">");
            writer.write("<Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Size=\"11\"/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"Header\">");
            writer.write("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>");
            writer.write("<Borders>");
            writer.write("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("</Borders>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Bold=\"1\" ss:Size=\"12\"/>");
            writer.write("<Interior ss:Color=\"#E9EEF7\" ss:Pattern=\"Solid\"/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"CellCenter\">");
            writer.write("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>");
            writer.write("<Borders>");
            writer.write("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("</Borders>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Size=\"12\"/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"CellLeft\">");
            writer.write("<Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>");
            writer.write("<Borders>");
            writer.write("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("</Borders>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Size=\"12\"/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"CellMoney\">");
            writer.write("<Alignment ss:Horizontal=\"Right\" ss:Vertical=\"Center\"/>");
            writer.write("<Borders>");
            writer.write("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("</Borders>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Size=\"12\"/>");
            writer.write("</Style>\n");

            writer.write("<Style ss:ID=\"Footer\">");
            writer.write("<Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>");
            writer.write("<Borders>");
            writer.write("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>");
            writer.write("</Borders>");
            writer.write("<Font ss:FontName=\"Arial\" ss:Bold=\"1\" ss:Size=\"13\"/>");
            writer.write("<Interior ss:Color=\"#F4F4F6\" ss:Pattern=\"Solid\"/>");
            writer.write("</Style>\n");

            writer.write("</Styles>\n");

            writer.write("<Worksheet ss:Name=\"BaoCaoDoanhThu\">\n");
            writer.write("<Table>\n");

            writer.write("<Column ss:Width=\"70\"/>");
            writer.write("<Column ss:Width=\"130\"/>");
            writer.write("<Column ss:Width=\"190\"/>");
            writer.write("<Column ss:Width=\"100\"/>");
            writer.write("<Column ss:Width=\"70\"/>");
            writer.write("<Column ss:Width=\"130\"/>");

            writer.write("<Row ss:Height=\"28\">");
            writer.write("<Cell ss:MergeAcross=\"5\" ss:StyleID=\"Title\"><Data ss:Type=\"String\">BÁO CÁO DOANH THU</Data></Cell>");
            writer.write("</Row>\n");

            writer.write("<Row ss:Height=\"22\">");
            writer.write("<Cell ss:MergeAcross=\"5\" ss:StyleID=\"SubInfo\"><Data ss:Type=\"String\">Từ ngày: "
                    + escapeXml(getDateText(startChooser))
                    + "    |    Đến ngày: "
                    + escapeXml(getDateText(endChooser))
                    + "</Data></Cell>");
            writer.write("</Row>\n");

            writer.write("<Row ss:Height=\"22\">");
            writer.write("<Cell ss:MergeAcross=\"5\" ss:StyleID=\"SubInfo\"><Data ss:Type=\"String\">Tổng món bán: "
                    + escapeXml(lblTongMonBan.getText())
                    + "    |    Tổng số lượng: "
                    + escapeXml(lblTongSoLuong.getText())
                    + "    |    Doanh thu: "
                    + escapeXml(lblDoanhThu.getText())
                    + "    |    Bán chạy nhất: "
                    + escapeXml(lblBanChayNhat.getText())
                    + "</Data></Cell>");
            writer.write("</Row>\n");

            writer.write("<Row ss:Height=\"12\"></Row>\n");

            writer.write("<Row ss:Height=\"24\">");
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write("<Cell ss:StyleID=\"Header\"><Data ss:Type=\"String\">"
                        + escapeXml(tableModel.getColumnName(i))
                        + "</Data></Cell>");
            }
            writer.write("</Row>\n");

            for (int r = 0; r < tableModel.getRowCount(); r++) {
                writer.write("<Row ss:Height=\"24\">");
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object value = tableModel.getValueAt(r, c);
                    String text = value == null ? "" : value.toString();

                    String styleId;
                    if (c == 2) {
                        styleId = "CellLeft";
                    } else if (c == 5) {
                        styleId = "CellMoney";
                    } else {
                        styleId = "CellCenter";
                    }

                    writer.write("<Cell ss:StyleID=\"" + styleId + "\"><Data ss:Type=\"String\">"
                            + escapeXml(text)
                            + "</Data></Cell>");
                }
                writer.write("</Row>\n");
            }

            writer.write("<Row ss:Height=\"28\">");
            writer.write("<Cell ss:MergeAcross=\"1\" ss:StyleID=\"Footer\"><Data ss:Type=\"String\">"
                    + escapeXml(lblFooterDong.getText())
                    + "</Data></Cell>");
            writer.write("<Cell ss:MergeAcross=\"1\" ss:StyleID=\"Footer\"><Data ss:Type=\"String\">"
                    + escapeXml(lblFooterSL.getText())
                    + "</Data></Cell>");
            writer.write("<Cell ss:MergeAcross=\"1\" ss:StyleID=\"Footer\"><Data ss:Type=\"String\">"
                    + escapeXml(lblFooterTien.getText())
                    + "</Data></Cell>");
            writer.write("</Row>\n");

            writer.write("</Table>\n");
            writer.write("</Worksheet>\n");
            writer.write("</Workbook>\n");

            JOptionPane.showMessageDialog(this,
                    "Xuất báo cáo thành công!\nFile Excel: " + file.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Xuất báo cáo thất bại:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getDateText(JDateChooser chooser) {
        Date date = chooser.getDate();
        if (date == null) return "";
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(dateFormat);
    }

    private String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private JLabel createFooterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setForeground(new Color(50, 50, 50));
        return l;
    }

    private JPanel createChartWrapper(String title, JPanel chart) {
        JPanel w = new JPanel(new BorderLayout(0, 10));
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)));
        JLabel lblTop = new JLabel(title);
        lblTop.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTop.setForeground(new Color(30, 41, 59));
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
        List<DaySale> days = loadDaySales(startDate, endDate);
        updateUIWithData(items, days);
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

    private List<DaySale> loadDaySales(LocalDate start, LocalDate end) {
        List<DaySale> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return list;
            String sql = "SELECT CAST(thoiGianRa AS DATE) AS ngay, ISNULL(SUM(tongTien),0) AS doanhThu "
                    + "FROM HoaDon "
                    + "WHERE trangThai = N'Đã thanh toán' "
                    + "AND thoiGianRa >= ? AND thoiGianRa < ? "
                    + "GROUP BY CAST(thoiGianRa AS DATE) ORDER BY ngay";
            stmt = con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            rs = stmt.executeQuery();
            while (rs.next()) {
                DaySale day = new DaySale();
                day.label = rs.getDate("ngay").toLocalDate().format(dateFormat);
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

    private void updateUIWithData(List<ItemSale> items, List<DaySale> days) {
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
        lblTongSoLuong.setText(String.valueOf(totalQty));
        lblDoanhThu.setText(formatMoney(totalMoney));
        lblBanChayNhat.setText(items.isEmpty() ? "-" : items.get(0).tenMon);
        lblFooterDong.setText("Tổng số dòng: " + items.size());
        lblFooterSL.setText("Tổng SL: " + totalQty);
        lblFooterTien.setText("Tổng tiền: " + formatMoney(totalMoney));

        chartTopMon.setData(items);
        chartDoanhThu.setData(days);
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value) + "đ";
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) return;
        try { closeable.close(); } catch (Exception e) { e.printStackTrace(); }
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
            int padLeft = 20;
            int padBottom = 40;

            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom);
            g2.drawLine(padLeft, 10, padLeft, h - padBottom);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.setColor(Color.GRAY);
                g2.drawString("Chưa có dữ liệu", w / 2 - 65, h / 2);
                return;
            }

            int n = Math.min(data.size(), 5);
            double max = 0;
            for (int i = 0; i < n; i++) max = Math.max(max, data.get(i).soLuong);
            if (max <= 0) max = 1;

            double groupW = (w - padLeft) / (double) n;
            int barW = 42;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            int maxBarHeightPixels = h - padBottom - 25;

            for (int i = 0; i < n; i++) {
                ItemSale item = data.get(i);
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);
                String label = item.tenMon.length() > 8 ? item.tenMon.substring(0, 8) + "..." : item.tenMon;
                int textW = fm.stringWidth(label);

                g2.setColor(Color.DARK_GRAY);
                g2.drawString(label, cx - textW / 2, h - 12);

                int barH = (int) (item.soLuong / max * maxBarHeightPixels);
                int y = h - padBottom - barH;

                g2.setColor(new Color(80, 160, 250));
                if (barH > 0) {
                    g2.fillRoundRect(cx - barW / 2, y, barW, barH, 12, 12);
                    if (barH > 6) g2.fillRect(cx - barW / 2, h - padBottom - 6, barW, 6);
                }
            }

            g2.setColor(new Color(200, 200, 200));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom);
            g2.setStroke(oldStr);
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
            int padLeft = 20;
            int padBottom = 40;

            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom);
            g2.drawLine(padLeft, 10, padLeft, h - padBottom);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.setColor(Color.GRAY);
                g2.drawString("Chưa có dữ liệu", w / 2 - 65, h / 2);
                return;
            }

            int n = Math.min(data.size(), 7);
            double max = 0;
            for (int i = 0; i < n; i++) max = Math.max(max, data.get(i).doanhThu);
            if (max <= 0) max = 1;

            double groupW = (w - padLeft) / (double) n;
            int[] px = new int[n];
            int[] py = new int[n];
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            int maxLineHeight = h - padBottom - 25;

            for (int i = 0; i < n; i++) {
                DaySale d = data.get(i);
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);
                px[i] = cx;

                int textW = g2.getFontMetrics().stringWidth(d.label);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(d.label, cx - textW / 2, h - 12);

                int pointH = (int) (d.doanhThu / max * maxLineHeight);
                py[i] = h - padBottom - pointH;

                g2.setColor(new Color(30, 90, 250));
                g2.fillOval(cx - 4, py[i] - 4, 8, 8);
            }

            g2.setColor(new Color(30, 90, 250));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(3.0f));
            for (int i = 0; i < n - 1; i++) g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            g2.setStroke(oldStr);
        }
    }
}