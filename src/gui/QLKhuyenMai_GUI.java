package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class QLKhuyenMai_GUI extends JPanel {

    private static final Color CLR_BG = new Color(245, 248, 250); // Slight gray-blue
    private static final Color CLR_WHITE = Color.WHITE;
    
    private List<KhuyenMaiInfo> listKM = new ArrayList<>();
    private JPanel pnCardsWrapper;

    public QLKhuyenMai_GUI() {
        setLayout(new BorderLayout());
        setBackground(CLR_BG);
        
        initSampleData();
        
        // --- 1. Top Bar ---
        add(buildTopBar(), BorderLayout.NORTH);
        
        // --- 2. Center Content ---
        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setOpaque(false);
        pnCenter.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JPanel pnMainBg = new JPanel(new BorderLayout());
        pnMainBg.setBackground(CLR_WHITE);
        pnMainBg.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header Hạng thành viên
        JPanel pnGroupHeader = new JPanel(new BorderLayout());
        pnGroupHeader.setOpaque(false);
        pnGroupHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        
        JLabel lblGroupTitle = new JLabel("Khuyến mãi cố định theo hạng thành viên");
        lblGroupTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblGroupTitle.setForeground(new Color(60, 65, 70));
        
        JPanel pnGroupActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnGroupActions.setOpaque(false);
        JButton btnThem = createButton("+ Thêm khuyến mãi", new Color(230, 245, 235), new Color(40, 120, 60));
        JButton btnXoa = createButton("Xóa", new Color(245, 235, 230), new Color(160, 80, 60));
        pnGroupActions.add(btnThem);
        pnGroupActions.add(btnXoa);
        
        pnGroupHeader.add(lblGroupTitle, BorderLayout.WEST);
        pnGroupHeader.add(pnGroupActions, BorderLayout.EAST);
        
        pnMainBg.add(pnGroupHeader, BorderLayout.NORTH);
        
        // Cards
        pnCardsWrapper = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        pnCardsWrapper.setOpaque(false);
        renderCards();
        
        JScrollPane scroll = new JScrollPane(pnCardsWrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(CLR_WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        pnMainBg.add(scroll, BorderLayout.CENTER);
        pnCenter.add(pnMainBg, BorderLayout.CENTER);
        
        add(pnCenter, BorderLayout.CENTER);
    }
    
    private JPanel buildTopBar() {
        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        pnTop.setBackground(CLR_WHITE);
        pnTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        // Search
        JTextField txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm khuyến mãi");
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        // Dates
        JLabel lblStart = new JLabel("Ngày bắt đầu:");
        lblStart.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JDateChooser txtStart = new JDateChooser();
        txtStart.setPreferredSize(new Dimension(130, 26));
        
        JLabel lblEnd = new JLabel("Ngày kết thúc:");
        lblEnd.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JDateChooser txtEnd = new JDateChooser();
        txtEnd.setPreferredSize(new Dimension(130, 26));
        
        JButton btnLoc = createButton("Lọc", new Color(245, 235, 210), new Color(80, 60, 40));
        btnLoc.setPreferredSize(new Dimension(80, 32));
        
        pnTop.add(txtSearch);
        pnTop.add(Box.createHorizontalStrut(10));
        pnTop.add(lblStart);
        pnTop.add(txtStart);
        pnTop.add(lblEnd);
        pnTop.add(txtEnd);
        pnTop.add(btnLoc);
        
        return pnTop;
    }
    
    private void renderCards() {
        pnCardsWrapper.removeAll();
        for (KhuyenMaiInfo km : listKM) {
            pnCardsWrapper.add(new KhuyenMaiCard(km));
        }
        pnCardsWrapper.revalidate();
        pnCardsWrapper.repaint();
    }
    
    // --- LỚP RENDER THẺ KHUYẾN MÃI ---
    class KhuyenMaiCard extends JPanel {
        public KhuyenMaiCard(KhuyenMaiInfo km) {
            setPreferredSize(new Dimension(450, 280));
            setOpaque(false);
            setLayout(new BorderLayout());
            
            JPanel inner = new JPanel(new BorderLayout(0, 12)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(CLR_WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.setColor(new Color(230, 230, 230));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            };
            inner.setOpaque(false);
            inner.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
            
            // Card Header
            JPanel pnHdr = new JPanel();
            pnHdr.setLayout(new BoxLayout(pnHdr, BoxLayout.Y_AXIS));
            pnHdr.setOpaque(false);
            
            JLabel lblTitle = new JLabel(km.ma + " - " + km.ten);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblTitle.setForeground(new Color(40, 40, 40));
            
            JLabel lblSub = new JLabel("Giảm trực tiếp trên tổng hóa đơn");
            lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblSub.setForeground(new Color(110, 110, 110));
            
            pnHdr.add(lblTitle);
            pnHdr.add(Box.createVerticalStrut(6));
            pnHdr.add(lblSub);
            
            // Delineator
            JPanel pnCenter = new JPanel(new BorderLayout(0, 16));
            pnCenter.setOpaque(false);
            
            JSeparator sep = new JSeparator();
            sep.setForeground(new Color(230, 230, 230));
            pnCenter.add(sep, BorderLayout.NORTH);
            
            // Form Detail
            JPanel pnDetails = new JPanel(new GridLayout(4, 2, 8, 8));
            pnDetails.setOpaque(false);
            
            addDetailRow(pnDetails, "Đối tượng áp dụng:", km.doiTuong);
            addDetailRow(pnDetails, "Mức giảm:", km.mucGiam);
            addDetailRow(pnDetails, "Loại khuyến mãi:", km.loai);
            
            // Status row
            JLabel lblStsTitle = new JLabel("Trạng thái:");
            lblStsTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblStsTitle.setForeground(new Color(100, 100, 100));
            pnDetails.add(lblStsTitle);
            
            JPanel pnStsVal = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnStsVal.setOpaque(false);
            
            JLabel lblBadge = new JLabel(km.trangThai) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (km.trangThai.equals("Hoạt động")) {
                        g2.setColor(new Color(225, 245, 232));
                    } else {
                        g2.setColor(new Color(240, 240, 240));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            lblBadge.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (km.trangThai.equals("Hoạt động")) {
                lblBadge.setForeground(new Color(40, 140, 70));
            } else {
                lblBadge.setForeground(new Color(100, 100, 100));
            }
            lblBadge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            pnStsVal.add(lblBadge);
            
            pnDetails.add(pnStsVal);
            pnCenter.add(pnDetails, BorderLayout.CENTER);
            
            // Action bottom
            JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            pnBottom.setOpaque(false);
            JButton btnSua = createButton("Sửa", new Color(245, 235, 225), new Color(120, 80, 60));
            btnSua.setPreferredSize(new Dimension(80, 32));
            pnBottom.add(btnSua);
            
            inner.add(pnHdr, BorderLayout.NORTH);
            inner.add(pnCenter, BorderLayout.CENTER);
            inner.add(pnBottom, BorderLayout.SOUTH);
            
            add(inner, BorderLayout.CENTER);
        }
        
        private void addDetailRow(JPanel p, String label, String val) {
            JLabel lblL = new JLabel(label);
            lblL.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblL.setForeground(new Color(100, 100, 100));
            
            JLabel lblV = new JLabel(val);
            lblV.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblV.setForeground(new Color(60, 60, 60));
            
            p.add(lblL);
            p.add(lblV);
        }
    }
    
    // --- LỚP BUTTON TÙY CHỈNH ---
    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        return btn;
    }
    
    // --- DỮ LIỆU ---
    private void initSampleData() {
        listKM.add(new KhuyenMaiInfo("KM01", "Thành viên thẻ vàng", "Thành viên thẻ vàng", "10%", "Không thời hạn", "Hoạt động"));
        listKM.add(new KhuyenMaiInfo("KM02", "Thành viên thẻ kim cương", "Thành viên kim cương", "15%", "Không thời hạn", "Hoạt động"));
    }
    
    class KhuyenMaiInfo {
        String ma, ten, doiTuong, mucGiam, loai, trangThai;
        public KhuyenMaiInfo(String m, String t, String dt, String mg, String l, String tr) {
            this.ma = m; this.ten = t; this.doiTuong = dt; 
            this.mucGiam = mg; this.loai = l; this.trangThai = tr;
        }
    }
}
