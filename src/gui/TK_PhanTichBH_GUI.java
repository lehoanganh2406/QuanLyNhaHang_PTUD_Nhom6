package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Arrays;

public class TK_PhanTichBH_GUI extends JPanel {

    private static final Color CLR_WHITE = Color.WHITE;
    private static final Color CLR_BG = new Color(245, 248, 250);

    public TK_PhanTichBH_GUI() {
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
        pnSummary.add(createCard("Doanh thu thực tế", "12.500.000đ", new Color(228, 240, 255)));
        pnSummary.add(createCard("Khuyến mãi/Giảm giá", "8.200.000đ", new Color(220, 245, 230)));
        pnSummary.add(createCard("Lợi nhuận", "4.300.000đ", new Color(255, 245, 220)));
        pnSummary.add(createCard("Trạng thái", "Lãi", new Color(255, 230, 240)));
        
        pnCenter.add(pnSummary, BorderLayout.NORTH);
        
        // Main Middle Split Area
        JPanel pnSplit = new JPanel(new BorderLayout(20, 0));
        pnSplit.setOpaque(false);
        
        // Left Chart
        JPanel pChart = TK_ChartUtils.createMixedChart("BIỂU ĐỒ DOANH THU - CHI PHÍ - LỢI NHUẬN", 
                Arrays.asList("Ca 1", "Ca 2", "Ca 3", "Ca 4"), 
                Arrays.asList(80d, 50d, 90d, 60d), 
                Arrays.asList(50d, 35d, 60d, 40d),
                Arrays.asList(30d, 15d, 30d, 20d));
        pChart.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pChart.setPreferredSize(new Dimension(800, 300));
        
        pnSplit.add(pChart, BorderLayout.CENTER);
        
        // Right Analysis Card
        JPanel pnAnalysis = new JPanel(new BorderLayout());
        pnAnalysis.setOpaque(false);
        pnAnalysis.setPreferredSize(new Dimension(300, 300));
        
        JLabel lblAnaTitle = new JLabel("Phân tích doanh số", SwingConstants.CENTER);
        lblAnaTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblAnaTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pnAnalysis.add(lblAnaTitle, BorderLayout.NORTH);
        
        JPanel pnAnaContent = new JPanel();
        pnAnaContent.setLayout(new BoxLayout(pnAnaContent, BoxLayout.Y_AXIS));
        pnAnaContent.setBackground(CLR_WHITE);
        pnAnaContent.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        JPanel pnAnaRow1 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnAnaRow1.setOpaque(false);
        pnAnaRow1.add(createAnaSmallCard("Tỷ suất lợi nhuận", "34%"));
        pnAnaRow1.add(createAnaSmallCard("Mức sinh lợi", "Ổn định"));
        
        pnAnaContent.add(pnAnaRow1);
        pnAnaContent.add(Box.createVerticalStrut(16));
        
        JLabel lblNx = new JLabel("Nhận xét:");
        lblNx.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNx.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnAnaContent.add(lblNx);
        pnAnaContent.add(Box.createVerticalStrut(6));
        
        JTextArea txtNx = new JTextArea("• Lợi nhuận duy trì tốt ở mức >30%\n• Tiểm năng đẩy mạnh doanh thu ở ca 3.\n• Cần tối ưu chi phí nguyên liệu ở ca tối.");
        txtNx.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtNx.setLineWrap(true);
        txtNx.setWrapStyleWord(true);
        txtNx.setOpaque(false);
        txtNx.setEditable(false);
        txtNx.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnAnaContent.add(txtNx);
        
        pnAnalysis.add(pnAnaContent, BorderLayout.CENTER);
        
        pnSplit.add(pnAnalysis, BorderLayout.EAST);
        
        pnCenter.add(pnSplit, BorderLayout.CENTER);
        
        // Bottom Table
        JPanel pnTableArea = new JPanel(new BorderLayout(0, 10));
        pnTableArea.setOpaque(false);
        pnTableArea.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel lblTblTitle = new JLabel("PHÂN TÍCH TỔNG QUAN");
        lblTblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        pnTableArea.add(lblTblTitle, BorderLayout.NORTH);
        
        String[] cols = {"STT", "Phân tích", "Số ĐH", "Doanh thu", "Chi phí", "Lợi nhuận", "Ghi chú"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(mdl);
        tbl.setRowHeight(36);
        tbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(235, 240, 255));
        
        mdl.addRow(new Object[]{"1", "Ngày thường", "76", "7.800.000đ", "5.100.000đ", "2.700.000đ", "Lãi tốt"});
        mdl.addRow(new Object[]{"2", "Cuối tuần", "45", "4.700.000đ", "3.100.000đ", "1.600.000đ", "Ổn định"});
        
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width, 150));
        pnTableArea.add(scroll, BorderLayout.CENTER);
        
        pnCenter.add(pnTableArea, BorderLayout.SOUTH);
        
        add(pnCenter, BorderLayout.CENTER);
    }
    
    private JPanel buildTopBar() {
        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        pnTop.setBackground(CLR_WHITE);
        pnTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        JLabel lblTitle = new JLabel("Phân tích bán hàng");
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
        p.setPreferredSize(new Dimension(180, 60));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        JLabel lblVal = new JLabel(val, SwingConstants.CENTER);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblVal, BorderLayout.CENTER);
        return p;
    }
    
    private JPanel createAnaSmallCard(String title, String val) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblVal.setForeground(Color.DARK_GRAY);
        
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblVal, BorderLayout.CENTER);
        return p;
    }
}
