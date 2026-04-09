package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Arrays;

public class TK_TongKetBH_GUI extends JPanel {

    private static final Color CLR_WHITE = Color.WHITE;
    private static final Color CLR_BG = new Color(245, 248, 250);

    public TK_TongKetBH_GUI() {
        setLayout(new BorderLayout());
        setBackground(CLR_BG);
        
        // --- 1. Top Bar ---
        add(buildTopBar(), BorderLayout.NORTH);
        
        // --- 2. Center View ---
        JPanel pnCenter = new JPanel(new BorderLayout(0, 16));
        pnCenter.setOpaque(false);
        pnCenter.setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));
        
        // Summary Cards
        JPanel pnSummary = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 10));
        pnSummary.setOpaque(false);
        pnSummary.add(createCard("Tổng số món bán", "24", new Color(228, 240, 255)));
        pnSummary.add(createCard("Tổng số lượng", "58", new Color(230, 245, 235)));
        pnSummary.add(createCard("Doanh thu", "4.250.000đ", new Color(255, 245, 220)));
        pnSummary.add(createCard("Bán chạy nhất", "Gà nướng", new Color(255, 235, 240)));
        
        JButton btnXuat = new JButton("Xuất báo cáo") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 160, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnXuat.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnXuat.setForeground(CLR_WHITE);
        btnXuat.setFocusPainted(false);
        btnXuat.setBorderPainted(false);
        btnXuat.setContentAreaFilled(false);
        btnXuat.setPreferredSize(new Dimension(140, 48));
        pnSummary.add(btnXuat);
        
        pnCenter.add(pnSummary, BorderLayout.NORTH);
        
        // Main Split Area
        JPanel pnSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        pnSplit.setOpaque(false);
        
        // Left Table
        JPanel pnTableArea = new JPanel(new BorderLayout(0, 10));
        pnTableArea.setOpaque(false);
        JLabel lblTblTitle = new JLabel("DANH SÁCH CHI TIẾT");
        lblTblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        pnTableArea.add(lblTblTitle, BorderLayout.NORTH);
        
        String[] cols = {"STT", "Loại món", "Tên món ăn", "Số HD", "SL", "Thành tiền"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(mdl);
        tbl.setRowHeight(36);
        tbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(235, 240, 255));
        
        mdl.addRow(new Object[]{"1", "Món chính", "Gà nướng", "MHD1", "12", "1.200.000đ"});
        mdl.addRow(new Object[]{"2", "Nước uống", "Trà dâu", "MHD2", "15", "750.000đ"});
        
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pnTableArea.add(scroll, BorderLayout.CENTER);
        
        // Table Footer
        JPanel pnTblFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        pnTblFooter.setBackground(new Color(245, 248, 252));
        pnTblFooter.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pnTblFooter.add(new JLabel("Tổng số món: 24"));
        pnTblFooter.add(new JLabel("Tổng SL: 58"));
        pnTblFooter.add(new JLabel("Tổng tiền: 4.250.000đ"));
        pnTableArea.add(pnTblFooter, BorderLayout.SOUTH);
        
        // Right Charts
        JPanel pnCharts = new JPanel(new GridLayout(2, 1, 0, 16));
        pnCharts.setOpaque(false);
        
        JPanel pTopBar = TK_ChartUtils.createBarChart("BIỂU ĐỒ TOP MÓN BÁN CHẠY", 
                Arrays.asList("Gà nướng", "Trà dâu", "Khoai tây", "Lẩu", "Bia"), 
                Arrays.asList(150d, 120d, 100d, 70d, 50d), 
                new Color(80, 160, 255));
        pTopBar.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel pBotLine = TK_ChartUtils.createLineChart("DOANH THU THEO NGÀY", 
                Arrays.asList("20/04", "21/04", "22/04", "23/04", "24/04", "25/04"), 
                Arrays.asList(30d, 40d, 35d, 60d, 55d, 70d), 
                new Color(40, 110, 230));
        pBotLine.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        pnCharts.add(pTopBar);
        pnCharts.add(pBotLine);
        
        pnSplit.add(pnTableArea);
        pnSplit.add(pnCharts);
        
        pnCenter.add(pnSplit, BorderLayout.CENTER);
        
        add(pnCenter, BorderLayout.CENTER);
    }
    
    private JPanel buildTopBar() {
        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        pnTop.setBackground(CLR_WHITE);
        pnTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        JLabel lblTitle = new JLabel("Tổng kết bán hàng");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JLabel lblStart = new JLabel("Ngày bắt đầu:");
        lblStart.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JDateChooser txtStart = new JDateChooser();
        txtStart.setPreferredSize(new Dimension(130, 26));
        
        JLabel lblEnd = new JLabel("Ngày kết thúc:");
        lblEnd.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JDateChooser txtEnd = new JDateChooser();
        txtEnd.setPreferredSize(new Dimension(130, 26));
        
        JButton btnLoc = new JButton("Lọc") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(245, 235, 210).darker() : new Color(245, 235, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLoc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnLoc.setForeground(new Color(80, 60, 40));
        btnLoc.setFocusPainted(false);
        btnLoc.setBorderPainted(false);
        btnLoc.setContentAreaFilled(false);
        btnLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLoc.setPreferredSize(new Dimension(80, 32));
        
        pnTop.add(lblTitle);
        pnTop.add(Box.createHorizontalStrut(20));
        pnTop.add(lblStart);
        pnTop.add(txtStart);
        pnTop.add(lblEnd);
        pnTop.add(txtEnd);
        pnTop.add(btnLoc);
        
        return pnTop;
    }
    
    private JPanel createCard(String title, String val, Color bg) {
        JPanel p = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(160, 60));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        JLabel lblVal = new JLabel(val, SwingConstants.CENTER);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblVal, BorderLayout.CENTER);
        return p;
    }
}
