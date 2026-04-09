package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Arrays;

public class TK_TheoCa_GUI extends JPanel {

    private static final Color CLR_WHITE = Color.WHITE;
    private static final Color CLR_BG = new Color(245, 248, 250);

    public TK_TheoCa_GUI() {
        setLayout(new BorderLayout());
        setBackground(CLR_BG);
        
        // 1. Top Bar
        JPanel pnTop = new JPanel(new BorderLayout());
        pnTop.setBackground(CLR_WHITE);
        pnTop.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        
        JLabel lblTitle = new JLabel("Thống kê theo ca");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JLabel lblCa = new JLabel("Ca 01", SwingConstants.CENTER);
        lblCa.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JLabel lblDate = new JLabel("Ngày 23 tháng 3 năm 2026", SwingConstants.RIGHT);
        lblDate.setFont(new Font("SansSerif", Font.PLAIN, 15));
        
        pnTop.add(lblTitle, BorderLayout.WEST);
        pnTop.add(lblCa, BorderLayout.CENTER);
        pnTop.add(lblDate, BorderLayout.EAST);
        
        add(pnTop, BorderLayout.NORTH);
        
        // 2. Center View
        JPanel pnCenter = new JPanel(new BorderLayout(0, 16));
        pnCenter.setOpaque(false);
        pnCenter.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        // Charts
        JPanel pnCharts = new JPanel(new GridLayout(1, 2, 20, 0));
        pnCharts.setOpaque(false);
        pnCharts.setPreferredSize(new Dimension(800, 250));
        
        JPanel p1 = TK_ChartUtils.createBarChart("DOANH THU THEO NGÀY TRONG TUẦN", 
                Arrays.asList("CN", "T2", "T3", "T4", "T5", "T6", "T7"), 
                Arrays.asList(42d, 22d, 20d, 21d, 22d, 25d, 45d), 
                new Color(33, 115, 180));
        p1.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel p2 = TK_ChartUtils.createAreaChart("DOANH THU THEO GIỜ", 
                Arrays.asList("0h", "3h", "6h", "9h", "12h", "15h", "18h", "21h"), 
                Arrays.asList(0d, 0d, 0d, 15d, 5d, 10d, 120d, 10d), 
                new Color(100, 160, 220));
        p2.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnCharts.add(p1);
        pnCharts.add(p2);
        
        // Legend for Table
        JPanel pnLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnLegend.setOpaque(false);
        JLabel lblLeg = new JLabel("doanh thu theo giờ");
        lblLeg.setIcon(new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(new Color(33, 150, 243));
                g.fillRect(x, y + 2, 12, 12);
            }
            @Override public int getIconWidth() { return 16; }
            @Override public int getIconHeight() { return 16; }
        });
        lblLeg.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pnLegend.add(lblLeg);
        
        // Table Config
        String[] cols = {"Mã HD", "Ngày", "Ca", "SL", "Tổng tiền", "Giảm giá", "Thành tiền"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(mdl);
        tbl.setRowHeight(36);
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(230, 230, 230));
        tbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        // Center text in table
        // We skip custom renderers to keep it simple, letting it use default for now.
        
        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(CLR_WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.getViewport().setBackground(CLR_WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Sample data
        mdl.addRow(new Object[]{"MHD1234", "8/3/2026", "Sáng", "3", "90.000", "50.000", "40.000"});
        mdl.addRow(new Object[]{"MHD1235", "9/3/2026", "Chiều", "2", "60.000", "0", "60.000"});
        
        JPanel pnTableArea = new JPanel(new BorderLayout());
        pnTableArea.setOpaque(false);
        pnTableArea.add(pnLegend, BorderLayout.NORTH);
        pnTableArea.add(scroll, BorderLayout.CENTER);
        
        pnCenter.add(pnCharts, BorderLayout.NORTH);
        pnCenter.add(pnTableArea, BorderLayout.CENTER);
        
        // 3. Bottom Footer
        JPanel pnBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 16));
        pnBot.setOpaque(false);
        JLabel lblTong = new JLabel("Tổng thực nhận: 100.000.000 VNĐ");
        lblTong.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTong.setIcon(new ImageIcon("img/ic_money.png")); // Simulated icon
        pnBot.add(lblTong);
        
        pnCenter.add(pnBot, BorderLayout.SOUTH);
        
        add(pnCenter, BorderLayout.CENTER);
    }
}
