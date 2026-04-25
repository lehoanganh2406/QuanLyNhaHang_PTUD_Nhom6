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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import entity.TaiKhoan;

public class PhanTichBanHang_GUI extends JPanel {

    private TaiKhoan taiKhoanDangNhap;

    public PhanTichBanHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createMainPanel(), BorderLayout.CENTER);
    }

    public PhanTichBanHang_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(25, 40, 40, 40));

        // Sections
        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        contentContainer.add(wrapInNorth(createKpiPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 50)));

        contentContainer.add(wrapInNorth(createMiddleSection()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 40))); // Reduced to pull table up

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
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);

        JLabel lblTitle = new JLabel("Phân tích bán hàng");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        hdr.add(lblTitle, BorderLayout.WEST);

        JPanel rightHdr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHdr.setOpaque(false);

        JLabel lblStart = new JLabel("Ngày bắt đầu:");
        lblStart.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JDateChooser startChooser = new JDateChooser();
        startChooser.setPreferredSize(new Dimension(180, 40));
        startChooser.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel lblEnd = new JLabel("Ngày kết thúc:");
        lblEnd.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JDateChooser endChooser = new JDateChooser();
        endChooser.setPreferredSize(new Dimension(180, 40));
        endChooser.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton btnFilter = new JButton("Lọc");
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnFilter.setBackground(new Color(250, 235, 215));
        btnFilter.setForeground(new Color(110, 80, 50));
        btnFilter.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 190, 170), 1),
                new EmptyBorder(8, 25, 8, 25)));
        btnFilter.setFocusPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        kpi.add(createKpiCard("Doanh thu thuần", "12.500.000đ", new Color(225, 240, 255), new Color(130, 160, 200)));
        kpi.add(createKpiCard("Chi phí nguyên liệu", "8.200.000đ", new Color(225, 245, 230), new Color(130, 180, 150)));
        kpi.add(createKpiCard("Lợi nhuận", "4.300.000đ", new Color(255, 240, 225), new Color(200, 160, 120)));
        kpi.add(createKpiCard("Trạng thái", "Lãi", new Color(255, 230, 235), new Color(180, 130, 150)));

        return kpi;
    }

    private JPanel createKpiCard(String title, String value, Color bg, Color titleFg) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1, true), // slight border matching bg
                new EmptyBorder(12, 10, 12, 10)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(titleFg);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblVal.setForeground(new Color(50, 50, 50));
        lblVal.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);

        return card;
    }

    private JPanel createMiddleSection() {
        JPanel mid = new JPanel(new BorderLayout(50, 0));
        mid.setOpaque(false);

        // Left: Chart Panel
        JPanel chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);
        chartArea.setPreferredSize(new Dimension(750, 360));

        JLabel lblChartTitle = new JLabel("BIỂU ĐỒ DOANH THU - CHI PHÍ - LỢI NHUẬN");
        lblChartTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblChartTitle.setForeground(new Color(50, 50, 50));
        lblChartTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        chartArea.add(lblChartTitle, BorderLayout.NORTH);

        chartArea.add(new MockChartPanel(), BorderLayout.CENTER);

        mid.add(chartArea, BorderLayout.CENTER);

        // Right: Tóm tắt
        JPanel rightArea = new JPanel(new BorderLayout(0, 15));
        rightArea.setOpaque(false);
        rightArea.setPreferredSize(new Dimension(450, 0));

        JLabel lblSummaryTitle = new JLabel("TÓM TẮT LỜI / LỖ");
        lblSummaryTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblSummaryTitle.setForeground(new Color(50, 50, 50));
        lblSummaryTitle.setHorizontalAlignment(SwingConstants.CENTER);
        rightArea.add(lblSummaryTitle, BorderLayout.NORTH);

        JPanel pnlSummaryBoxes = new JPanel(new BorderLayout(0, 15));
        pnlSummaryBoxes.setOpaque(false);

        JPanel topTwo = new JPanel(new GridLayout(1, 2, 15, 0));
        topTwo.setOpaque(false);
        topTwo.add(createSummaryBox("Tỷ suất lợi nhuận", "34%"));
        topTwo.add(createSummaryBox("Mức đánh giá", "Ổn định"));
        pnlSummaryBoxes.add(topTwo, BorderLayout.NORTH);

        JPanel bottomBox = new JPanel(new BorderLayout(0, 10)); // Reduced gap from 15 to 10
        bottomBox.setBackground(Color.WHITE);
        bottomBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20) // Reduced padding giving more vertical room
        ));
        JLabel lblQuick = new JLabel("Nhận xét nhanh");
        lblQuick.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblQuick.setForeground(Color.GRAY);
        bottomBox.add(lblQuick, BorderLayout.NORTH);

        JTextArea txtBox = new JTextArea(
                "• Lợi nhuận duy trì dương trong toàn kỳ\n" +
                        "• Tuần 3 có doanh thu cao nhất\n" +
                        "• Cần tối ưu chi phí ở tuần 2 và tuần 4");
        txtBox.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtBox.setForeground(new Color(60, 60, 60));
        txtBox.setLineWrap(true);
        txtBox.setWrapStyleWord(true);
        txtBox.setEditable(false);
        txtBox.setOpaque(false);
        bottomBox.add(txtBox, BorderLayout.CENTER);

        pnlSummaryBoxes.add(bottomBox, BorderLayout.CENTER);

        JPanel boundedRightArea = new JPanel(new BorderLayout());
        boundedRightArea.setBackground(new Color(252, 252, 252));
        boundedRightArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                new EmptyBorder(15, 15, 15, 15) // Reduced padding to expand text space
        ));
        boundedRightArea.add(pnlSummaryBoxes, BorderLayout.CENTER);

        rightArea.add(boundedRightArea, BorderLayout.CENTER);

        mid.add(rightArea, BorderLayout.EAST);

        return mid;
    }

    private JPanel createSummaryBox(String title, String val) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblT.setForeground(Color.GRAY);

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblV.setForeground(new Color(50, 50, 50));

        p.add(lblT);
        p.add(lblV);
        return p;
    }

    class MockChartPanel extends JPanel {
        public MockChartPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft = 40;
            int padBottom = 40;
            int padTop = 30;

            // Legend
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

            // Axes
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(210, 210, 210));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom); // X axis
            g2.drawLine(padLeft, padTop, padLeft, h - padBottom); // Y axis

            // Mock Data
            int[] pointY = { 130, 200, 100, 170 };
            String[] labels = { "Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4" };

            int n = 4;
            int groupW = (w - padLeft) / n;
            int barW = 45;

            int[] px = new int[n];
            int[] py = new int[n];

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fmL = g2.getFontMetrics();

            for (int i = 0; i < n; i++) {
                int cx = padLeft + i * groupW + groupW / 2;

                int textW = fmL.stringWidth(labels[i]);
                g2.setColor(Color.GRAY);
                g2.drawString(labels[i], cx - textW / 2, h - 15);

                px[i] = cx;
                py[i] = pointY[i];

                if (i == 0) {
                    g2.setColor(new Color(110, 170, 230));
                    g2.fillRect(cx - barW / 2, h - padBottom - 140, barW, 140);
                } else if (i == 1) {
                    g2.setColor(new Color(140, 200, 130));
                    g2.fillRect(cx - barW / 2, h - padBottom - 95, barW, 95);
                } else if (i == 2) {
                    g2.setColor(new Color(110, 170, 230));
                    g2.fillRect(cx - barW / 2, h - padBottom - 160, barW, 160);
                } else if (i == 3) {
                    g2.setColor(new Color(140, 200, 130));
                    g2.fillRect(cx - barW / 2, h - padBottom - 85, barW, 85);
                }
            }

            // Line
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(3.5f));
            g2.setColor(new Color(250, 180, 100)); // Vivid orange
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            }
            g2.setStroke(oldStr);
        }
    }

    private JPanel createTableSection() {
        JPanel tableSec = new JPanel(new BorderLayout(0, 20));
        tableSec.setOpaque(false);

        JLabel lblTitle = new JLabel("BẢNG PHÂN TÍCH MÓN ĂN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        tableSec.add(lblTitle, BorderLayout.NORTH);

        String[] columns = { "STT", "Tên món", "SL bán", "Doanh thu", "Chi phí", "Lợi nhuận", "Ghi chú" };
        Object[][] data = {
                { "1", "Gà nướng", "12", "1.800.000đ", "1.000.000đ", "800.000đ", "Lãi cao" },
                { "2", "Trà đào", "15", "750.000đ", "400.000đ", "350.000đ", "Ổn định" }
        };

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent keyboard editing
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 18));
        table.setRowHeight(50);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(238, 225, 205)); // Pastel beige
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setBackground(new Color(235, 240, 250)); // Light blue/grey header bg
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 50));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // alignment center for data cells
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(800, 250));

        tableSec.add(scroll, BorderLayout.CENTER);

        return tableSec;
    }
}