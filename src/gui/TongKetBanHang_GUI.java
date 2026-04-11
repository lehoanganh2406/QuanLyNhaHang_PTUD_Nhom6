package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import entity.TaiKhoan;

public class TongKetBanHang_GUI extends JFrame {

    private TaiKhoan taiKhoanDangNhap;

    public TongKetBanHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Tổng kết bán hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        JPanel mainPanel = createMainPanel();

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(menu, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();

                mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
                menu.setBounds(0, 0, w, h);

                layeredPane.revalidate();
                layeredPane.repaint();
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 750));
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(25, 40, 40, 40));

        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        contentContainer.add(wrapInNorth(createKpiPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        contentContainer.add(wrapInNorth(createMidScaleSection()));

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

    private JPanel createHeaderPanel() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);

        JLabel lblTitle = new JLabel("Tổng kết bán hàng");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        hdr.add(lblTitle, BorderLayout.WEST);

        JPanel rightHdr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHdr.setOpaque(false);

        JLabel lblStart = new JLabel("Ngày bắt đầu:");
        lblStart.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblStart.setForeground(Color.DARK_GRAY);
        JDateChooser startChooser = new JDateChooser();
        startChooser.setPreferredSize(new Dimension(180, 40));
        startChooser.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel lblEnd = new JLabel("Ngày kết thúc:");
        lblEnd.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblEnd.setForeground(Color.DARK_GRAY);
        JDateChooser endChooser = new JDateChooser();
        endChooser.setPreferredSize(new Dimension(180, 40));
        endChooser.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton btnFilter = new JButton("Lọc");
        btnFilter.setFont(new Font("SansSerif", Font.PLAIN, 18));
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
        JPanel kpi = new JPanel(new GridLayout(1, 5, 25, 0));
        kpi.setOpaque(false);

        kpi.add(createKpiCard("Tổng món bán", "24", new Color(235, 243, 255), new Color(100, 130, 150)));
        kpi.add(createKpiCard("Tổng số lượng", "58", new Color(235, 250, 240), new Color(120, 150, 130)));
        kpi.add(createKpiCard("Doanh thu", "4.250.000đ", new Color(255, 250, 235), new Color(150, 140, 100)));
        kpi.add(createKpiCard("Bán chạy nhất", "Gà nướng", new Color(255, 235, 245), new Color(150, 100, 120)));

        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        JButton btnExport = new JButton("Xuất báo cáo");
        btnExport.setFont(new Font("SansSerif", Font.BOLD, 24));
        btnExport.setBackground(new Color(105, 185, 115));
        btnExport.setForeground(Color.DARK_GRAY);
        btnExport.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90, 160, 90), 1),
                new EmptyBorder(0, 0, 0, 0) // size will stretch
        ));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel innerBtnWrap = new JPanel(new BorderLayout());
        innerBtnWrap.setOpaque(false);
        innerBtnWrap.setBorder(new EmptyBorder(12, 0, 12, 0));
        innerBtnWrap.add(btnExport, BorderLayout.CENTER);

        kpi.add(innerBtnWrap);

        return kpi;
    }

    private JPanel createKpiCard(String title, String value, Color bg, Color titleFg) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 15, 20, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(titleFg);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblVal.setForeground(new Color(40, 40, 40));
        lblVal.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(lblTitle, BorderLayout.NORTH);
        wrap.add(lblVal, BorderLayout.CENTER);

        card.add(wrap, BorderLayout.CENTER);

        return card;
    }

    private JPanel createMidScaleSection() {
        JPanel mid = new JPanel(new BorderLayout(40, 0));
        mid.setOpaque(false);

        // Left Column (Table)
        JPanel leftCol = new JPanel(new BorderLayout(0, 15));
        leftCol.setOpaque(false);

        JLabel lblDetailsTitle = new JLabel("DANH SÁCH CHI TIẾT");
        lblDetailsTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblDetailsTitle.setForeground(new Color(60, 60, 60));
        leftCol.add(lblDetailsTitle, BorderLayout.NORTH);

        String[] cols = { "STT", "Loại món", "Tên món ăn", "Mã món", "SL", "Thành tiền" };
        Object[][] data = {
                { "1", "Món chính", "Gà nướng", "MM01", "12", "1.200.000đ" },
                { "2", "Nước uống", "Trà đào", "NU02", "15", "750.000đ" },
                { "3", "Ăn vặt", "Khoai tây", "AV03", "8", "320.000đ" },
                { "4", "Món chính", "Cơm gà", "MM04", "10", "980.000đ" },
                { "5", "Tráng miệng", "Bánh flan", "TM05", "13", "1.000.000đ" }
        };

        JTable table = new JTable(data, cols);
        table.setFont(new Font("SansSerif", Font.PLAIN, 18));
        table.setRowHeight(50);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(230, 240, 255));

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 18));
        th.setBackground(new Color(235, 240, 250)); // Light blue header
        th.setForeground(new Color(50, 50, 50));
        th.setPreferredSize(new Dimension(0, 50));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        JScrollPane tblScroll = new JScrollPane(table);
        tblScroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        tblScroll.getViewport().setBackground(Color.WHITE);
        tblScroll.setPreferredSize(new Dimension(0, 250));

        // Custom Footer for the table
        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0));
        footer.setOpaque(false);

        footer.add(createFooterCell("Tổng số dòng: 24"));
        footer.add(createFooterCell("Tổng SL: 58"));
        footer.add(createFooterCell("Tổng tiền: 4.250.000đ"));

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.add(tblScroll, BorderLayout.CENTER);
        tableWrap.add(footer, BorderLayout.SOUTH);

        leftCol.add(tableWrap, BorderLayout.CENTER);

        mid.add(leftCol, BorderLayout.CENTER);

        // Right Column (Charts)
        JPanel rightCol = new JPanel(new GridLayout(2, 1, 0, 30));
        rightCol.setOpaque(false);
        rightCol.setPreferredSize(new Dimension(420, 0)); // explicit width restraint

        rightCol.add(createChartWrapper("BIỂU ĐỒ TOP MÓN BÁN CHẠY", new MockBarTopMon()));
        rightCol.add(createChartWrapper("DOANH THU THEO NGÀY", new MockLineDoanhThu()));

        mid.add(rightCol, BorderLayout.EAST);

        return mid;
    }

    private JPanel createChartWrapper(String title, JPanel chart) {
        JPanel w = new JPanel(new BorderLayout(0, 10));
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTop = new JLabel(title);
        lblTop.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTop.setForeground(new Color(50, 50, 50));
        w.add(lblTop, BorderLayout.NORTH);
        w.add(chart, BorderLayout.CENTER);

        return w;
    }

    private JPanel createFooterCell(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(248, 248, 250));
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(10, 20, 10, 20)));
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setForeground(new Color(50, 50, 50));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    // --- Mock Component for TOP MON ---
    class MockBarTopMon extends JPanel {
        public MockBarTopMon() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft = 20;
            int padBottom = 35;

            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom); // X axis
            g2.drawLine(padLeft, 10, padLeft, h - padBottom); // Y axis

            String[] xLabels = { "Gà nướng", "Trà đào", "Cơm gà", "Khoai", "Flan" };
            int[] values = { 85, 70, 55, 35, 25 };

            int n = xLabels.length;
            double groupW = (w - padLeft) / (double) n;
            int barW = 38;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            // Ensure no bars stretch above the panel artificially
            int maxBarHeightPixels = h - padBottom - 20;

            for (int i = 0; i < n; i++) {
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);

                int textW = g2.getFontMetrics().stringWidth(xLabels[i]);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(xLabels[i], cx - textW / 2, h - 12);

                int barH = (int) ((double) values[i] / 100.0 * maxBarHeightPixels);
                int y = h - padBottom - barH;
                g2.setColor(new Color(80, 160, 250)); // Bright sky blue
                
                if (barH > 0) {
                    g2.fillRoundRect(cx - barW / 2, y, barW, barH, 12, 12);
                    // Square off the bottom portion so it doesn't bleed through the axis
                    if (barH > 6) {
                        g2.fillRect(cx - barW / 2, h - padBottom - 6, barW, 6);
                    }
                }
            }

            // Re-draw axis heavily to ensure clean border line
            g2.setColor(new Color(200, 200, 200));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom);
            g2.setStroke(oldStr);
        }
    }

    // --- Mock Component for DOANH THU THEO NGÀY ---
    class MockLineDoanhThu extends JPanel {
        public MockLineDoanhThu() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft = 20;
            int padBottom = 35;

            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(padLeft, h - padBottom, w, h - padBottom);
            g2.drawLine(padLeft, 10, padLeft, h - padBottom);

            String[] xLabels = { "20/03", "21/03", "22/03", "23/03", "24/03", "25/03", "26/03" };
            int[] values = { 30, 45, 38, 65, 55, 80, 75 };

            int n = xLabels.length;
            double groupW = (w - padLeft) / (double) n;
            int[] px = new int[n];
            int[] py = new int[n];

            g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            int maxLineHeight = h - padBottom - 20;

            for (int i = 0; i < n; i++) {
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);
                px[i] = cx;

                int textW = g2.getFontMetrics().stringWidth(xLabels[i]);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(xLabels[i], cx - textW / 2, h - 12);

                int pointH = (int) ((double) values[i] / 100.0 * maxLineHeight);
                py[i] = h - padBottom - pointH;
                
                // Draw connecting dots for aesthetics
                g2.setColor(new Color(30, 90, 250));
                g2.fillOval(cx - 3, py[i] - 3, 6, 6);
            }

            g2.setColor(new Color(30, 90, 250)); // Deep energetic Blue
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(3.0f));
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            }
            g2.setStroke(oldStr);
        }
    }
}