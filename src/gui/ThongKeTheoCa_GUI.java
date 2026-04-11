package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
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

import entity.TaiKhoan;

public class ThongKeTheoCa_GUI extends JFrame {

    private TaiKhoan taiKhoanDangNhap;

    public ThongKeTheoCa_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Thống kê theo ca");
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
        
        // Add Sections
        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 30)));
        
        contentContainer.add(wrapInNorth(createChartsSection()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        
        contentContainer.add(wrapInNorth(createTableSection()));
        
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
        
        JLabel lblTitle = new JLabel("Thống kê theo ca");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        hdr.add(lblTitle, BorderLayout.WEST);
        
        JLabel lblCa = new JLabel("Ca 01");
        lblCa.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblCa.setHorizontalAlignment(SwingConstants.CENTER);
        hdr.add(lblCa, BorderLayout.CENTER);
        
        JLabel lblDate = new JLabel("Ngày 23 tháng 3 năm 2026");
        lblDate.setFont(new Font("SansSerif", Font.PLAIN, 24));
        lblDate.setForeground(Color.DARK_GRAY);
        hdr.add(lblDate, BorderLayout.EAST);
        
        return hdr;
    }
    
    private JPanel createChartsSection() {
        JPanel charts = new JPanel(new GridLayout(1, 2, 25, 0));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(1000, 320));
        
        charts.add(createChartWrapper("DOANH THU THEO NGÀY TRONG TUẦN", new MockBarChartPanel()));
        charts.add(createChartWrapper("DOANH THU THEO GIỜ", new MockAreaChartPanel()));
        
        return charts;
    }
    
    private JPanel createChartWrapper(String title, JPanel chart) {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1), // Subtle frame border
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JLabel lblTop = new JLabel(title);
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTop.setForeground(new Color(80, 80, 80));
        hdr.add(lblTop, BorderLayout.WEST);
        
        JLabel lblIcons = new JLabel("≡  🖨️"); // Mocking hamburger & print icons
        lblIcons.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcons.setForeground(Color.GRAY);
        hdr.add(lblIcons, BorderLayout.EAST);
        
        w.add(hdr, BorderLayout.NORTH);
        w.add(chart, BorderLayout.CENTER);
        
        return w;
    }
    
    private JPanel createTableSection() {
        JPanel tableSec = new JPanel(new BorderLayout(0, 10));
        tableSec.setOpaque(false);
        
        // Title and Legend Square wrapper
        JPanel hdrWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        hdrWrap.setOpaque(false);
        hdrWrap.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JPanel legendBox = new JPanel();
        legendBox.setPreferredSize(new Dimension(18, 18));
        legendBox.setBackground(new Color(30, 130, 200)); 
        
        JLabel lblTitle = new JLabel("doanh thu theo giờ");
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        
        hdrWrap.add(legendBox);
        hdrWrap.add(lblTitle);
        
        tableSec.add(hdrWrap, BorderLayout.NORTH);
        
        // Table Config
        String[] cols = {"Mã HD", "Ngày", "Ca", "SL", "Tổng tiền", "Giảm giá", "Thành tiền"};
        Object[][] data = {
            {"MHD1234", "8/3/2026", "Sáng", "3", "90.000", "50.000", "40.000"},
            {"", "", "", "", "", "", ""} // visual padding mock
        };
        
        JTable table = new JTable(data, cols);
        table.setFont(new Font("SansSerif", Font.PLAIN, 18));
        table.setRowHeight(50);
        table.setGridColor(Color.GRAY);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(230, 240, 255));
        
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif", Font.PLAIN, 18)); 
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(50, 50, 50));
        th.setPreferredSize(new Dimension(0, 50));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(Color.GRAY, 1)); // Bold border around table matching design
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(0, 150));
        
        tableSec.add(scroll, BorderLayout.CENTER);
        
        // Bottom Total
        JPanel bottomWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomWrap.setOpaque(false);
        
        JLabel lblTotalIcon = new JLabel("💰"); // Hand with money representation
        lblTotalIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        
        JLabel lblTotal = new JLabel("Tổng thực nhận: 100.000.000 VND");
        lblTotal.setFont(new Font("SansSerif", Font.PLAIN, 22));
        lblTotal.setForeground(new Color(50, 50, 50));
        
        bottomWrap.add(lblTotalIcon);
        bottomWrap.add(lblTotal);
        
        tableSec.add(bottomWrap, BorderLayout.SOUTH);
        
        return tableSec;
    }
    
    // --- Mock Component for DOANH THU THEO NGÀY TRONG TUẦN ---
    class MockBarChartPanel extends JPanel {
        public MockBarChartPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int padLeft = 40;
            int padBottom = 25;
            int padTop = 15;
            
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            // y axis lines
            String[] yLabels = {"0", "10M", "20M", "30M", "40M", "50M"};
            int numLines = yLabels.length;
            for(int i = 0; i < numLines; i++) {
                int y = h - padBottom - (i * (h - padBottom - padTop) / (numLines - 1));
                g2.setColor(new Color(240, 240, 240));
                g2.drawLine(padLeft, y, w, y);
                g2.setColor(Color.GRAY);
                g2.drawString(yLabels[i], padLeft - 30, y + 4);
            }
            
            String[] xLabels = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
            int[] values = {42, 31, 30, 31, 32, 35, 42}; 
            
            int n = xLabels.length;
            double groupW = (w - padLeft) / (double)n;
            int barW = (int) (groupW * 0.6); // 60% of group width
            
            for(int i = 0; i < n; i++) {
                int cx = padLeft + (int)(i * groupW) + (int)(groupW / 2);
                
                int textW = g2.getFontMetrics().stringWidth(xLabels[i]);
                g2.setColor(Color.GRAY);
                g2.drawString(xLabels[i], cx - textW / 2, h - 10);
                
                int barH = (int) ((double)values[i] / 50.0 * (h - padBottom - padTop));
                g2.setColor(new Color(30, 130, 200)); 
                g2.fillRect(cx - barW / 2, h - padBottom - barH, barW, barH);
            }
        }
    }
    
    // --- Mock Component for DOANH THU THEO GIỜ ---
    class MockAreaChartPanel extends JPanel {
        public MockAreaChartPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int padLeft = 40;
            int padBottom = 25;
            int padTop = 15;
            
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String[] yLabels = {"0", "20M", "40M", "60M", "80M"};
            int numLines = yLabels.length;
            for(int i = 0; i < numLines; i++) {
                int y = h - padBottom - (i * (h - padBottom - padTop) / (numLines - 1));
                g2.setColor(new Color(240, 240, 240));
                g2.drawLine(padLeft, y, w, y);
                g2.setColor(Color.GRAY);
                g2.drawString(yLabels[i], padLeft - 30, y + 4);
            }
            
            int n = 24;
            double groupW = (w - padLeft) / (double)n;
            int[] px = new int[n];
            int[] py = new int[n];
            
            for(int i = 0; i < n; i++) {
                int cx = padLeft + (int)(i * groupW) + (int)(groupW / 2);
                px[i] = cx;
                String lbl = i + "h";
                int textW = g2.getFontMetrics().stringWidth(lbl);
                g2.setColor(Color.GRAY);
                // Only show a label every few hours to not cramp text, but original image shows all labels (tiny)
                g2.drawString(lbl, cx - textW / 2, h - 10);
                
                double val = 0;
                if (i >= 8 && i <= 14) val = (14 - Math.abs(i - 11)) * 1.5; 
                if (i >= 16 && i <= 21) {
                    if (i == 19) val = 71;
                    else if (i == 20) val = 68;
                    else val = (22 - Math.abs(i - 19)) * 2; 
                }
                
                int lineH = (int) (val / 80.0 * (h - padBottom - padTop));
                py[i] = h - padBottom - lineH;
            }
            
            java.awt.Polygon poly = new java.awt.Polygon();
            poly.addPoint(px[0], h - padBottom);
            for(int i = 0; i < n; i++) poly.addPoint(px[i], py[i]);
            poly.addPoint(px[n-1], h - padBottom);
            
            g2.setColor(new Color(110, 170, 230, 80)); 
            g2.fillPolygon(poly);
            
            g2.setColor(new Color(80, 150, 200));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(2f));
            for(int i = 0; i < n - 1; i++) {
                g2.drawLine(px[i], py[i], px[i+1], py[i+1]);
            }
            g2.setStroke(oldStr);
            
            g2.setColor(Color.WHITE);
            for(int i = 0; i < n; i++) {
                g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
            }
            g2.setColor(new Color(80, 150, 200));
            for(int i = 0; i < n; i++) {
                g2.drawOval(px[i] - 3, py[i] - 3, 6, 6);
            }
            
            int tx = px[19];
            int ty = py[19];
            
            // vertical line highlighting the point
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(tx, padTop, tx, h - padBottom);
            
            g2.fillOval(tx - 5, ty - 5, 10, 10); 
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(tx - 200, ty - 35, 195, 30, 10, 10);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(tx - 200, ty - 35, 195, 30, 10, 10);
            
            g2.setColor(new Color(30, 130, 200));
            g2.fillOval(tx - 190, ty - 25, 8, 8);
            
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("Doanh thu theo giờ: 71,886,000đ", tx - 175, ty - 15);
            
            // Draw 19h label box
            g2.setColor(new Color(230, 240, 250));
            g2.fillRoundRect(tx - 8, padTop - 10, 25, 15, 5, 5);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("19h", tx - 4, padTop + 2);
        }
    }
    public static void main(String[] args) {
		new ThongKeTheoCa_GUI(null).setVisible(true);
	}
}