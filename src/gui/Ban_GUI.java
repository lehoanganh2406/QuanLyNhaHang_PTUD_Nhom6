package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import entity.TaiKhoan;

public class Ban_GUI extends JFrame {

    private TaiKhoan taiKhoanDangNhap;

    public Ban_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Quản lý bàn");
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
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(25, 0));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(createLeftPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);
        
        return mainPanel;
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Quản lý bàn");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        
        // Filter Row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterRow.setOpaque(false);
        
        filterRow.add(createFilterButton("Tất cả", true));
        filterRow.add(createFilterButton("Tầng 1 (A)", false));
        filterRow.add(createFilterButton("Tầng 2 (B)", false));
        filterRow.add(createFilterButton("Sân thượng (C)", false));
        
        JTextField txtSearch = new JTextField("Tìm mã bàn...", 15);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(7, 10, 7, 10)
        ));
        txtSearch.setForeground(Color.GRAY);
        filterRow.add(txtSearch);
        
        JButton btnFilter = createActionButton("Lọc", new Color(245, 235, 220), new Color(120, 90, 70));
        filterRow.add(btnFilter);
        
        filterRow.add(Box.createRigidArea(new Dimension(15, 0))); // Add spacing
        
        JButton btnAdd = createActionButton("Thêm bàn", new Color(225, 240, 255), new Color(40, 100, 180));
        JButton btnEdit = createActionButton("Sửa bàn", new Color(230, 244, 234), new Color(46, 125, 50));
        JButton btnDelete = createActionButton("Xóa bàn", new Color(255, 235, 238), new Color(198, 40, 40));
        JButton btnMerge = createActionButton("Ghép bàn", new Color(255, 243, 224), new Color(230, 81, 0));
        
        btnMerge.addActionListener(e -> {
            GhepBanDialog dialog = new GhepBanDialog(Ban_GUI.this);
            dialog.setVisible(true);
        });

        filterRow.add(btnAdd);
        filterRow.add(btnEdit);
        filterRow.add(btnDelete);
        filterRow.add(btnMerge);
        
        headerPanel.add(filterRow, BorderLayout.CENTER);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        leftPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Scroll Content
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        
        contentContainer.add(createFloorSection("Tầng 1 (Khu A)", "Mã bàn: A01 - A15", "15 bàn", 
            new String[]{"A01", "A02", "A03", "A04", "A05", "A06", "A07", "A08", "A09", "A10", "A11", "A12", "A13", "A14", "A15"},
            new int[]{0, 2, 0, 0, 3, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0}
        ));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        contentContainer.add(createFloorSection("Tầng 2 (Khu B)", "Mã bàn: B01 - B10", "10 bàn", 
            new String[]{"B01", "B02", "B03", "B04", "B05", "B06", "B07", "B08", "B09", "B10"},
            new int[]{1, 0, 0, 2, 0, 0, 1, 0, 0, 3}
        ));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        contentContainer.add(createFloorSection("Sân thượng (Khu C)", "Mã bàn: C01 - C10", "10 bàn", 
            new String[]{"C01", "C02", "C03", "C04", "C05", "C06", "C07", "C08", "C09", "C10"},
            new int[]{0, 0, 1, 0, 0, 2, 0, 0, 0, 0}
        ));
        JPanel wrapperForScroll = new JPanel(new BorderLayout());
        wrapperForScroll.setOpaque(false);
        wrapperForScroll.add(contentContainer, BorderLayout.NORTH);
        
        JScrollPane scroll = new JScrollPane(wrapperForScroll);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        leftPanel.add(scroll, BorderLayout.CENTER);
        
        return leftPanel;
    }
    
    private JPanel createFloorSection(String title, String subtitle, String count, String[] tableNames, int[] stati) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setOpaque(true);
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        header.add(titlePanel, BorderLayout.WEST);
        
        JLabel lblCount = new JLabel(count);
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCount.setForeground(new Color(100, 150, 255));
        header.add(lblCount, BorderLayout.EAST);
        
        wrapper.add(header, BorderLayout.NORTH);
        
        JPanel itemsWrap = new JPanel(new GridLayout(0, 5, 15, 15));
        itemsWrap.setOpaque(false);
        
        for (int i=0; i<tableNames.length; i++) {
            itemsWrap.add(createTableCard(tableNames[i], "4 chỗ • " + title.split(" ")[0], stati[i]));
        }
        
        wrapper.add(itemsWrap, BorderLayout.CENTER);
        
        return wrapper;
    }

    private JPanel createTableCard(String name, String detail, int status) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        
        Color bg = Color.WHITE;
        Color fg = Color.BLACK;
        Color border = Color.LIGHT_GRAY;
        String statusText = "";
        
        switch (status) {
            case 0:
                bg = new Color(232, 245, 253);
                fg = new Color(2, 136, 209);
                border = new Color(180, 220, 250);
                statusText = "Trống";
                break;
            case 1:
                bg = new Color(255, 240, 240);
                fg = new Color(211, 47, 47);
                border = new Color(250, 200, 200);
                statusText = "Đang dùng";
                break;
            case 2:
                bg = new Color(255, 248, 225);
                fg = new Color(245, 124, 0);
                border = new Color(255, 230, 180);
                statusText = "Đã đặt";
                break;
            case 3:
                bg = new Color(245, 245, 245);
                fg = new Color(117, 117, 117);
                border = new Color(220, 220, 220);
                statusText = "Bảo trì";
                break;
        }
        
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        
        JPanel top = new JPanel(new BorderLayout(0, 2));
        top.setOpaque(false);
        JLabel lblName = new JLabel("Bàn " + name);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblName.setForeground(Color.BLACK);
        
        JLabel lblDetail = new JLabel(detail);
        lblDetail.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDetail.setForeground(new Color(100, 100, 100));
        top.add(lblName, BorderLayout.NORTH);
        top.add(lblDetail, BorderLayout.CENTER);
        
        card.add(top, BorderLayout.CENTER);
        
        JLabel lblStatus = new JLabel(statusText);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatus.setForeground(fg);
        card.add(lblStatus, BorderLayout.SOUTH);
        
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return card;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(0, 25));
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setOpaque(false);
        
        rightPanel.add(createStatsPanel(), BorderLayout.NORTH);
        rightPanel.add(createInfoPanel(), BorderLayout.CENTER);
        
        return rightPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230), 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel lblTitle = new JLabel("Thống kê trạng thái");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        pnl.add(lblTitle, BorderLayout.NORTH);
        
        JPanel listWrap = new JPanel(new GridLayout(4, 1, 0, 12));
        listWrap.setOpaque(false);
        listWrap.add(createStatRow("Trống", "36", new Color(232, 245, 253), new Color(2, 136, 209)));
        listWrap.add(createStatRow("Đang dùng", "10", new Color(255, 240, 240), new Color(211, 47, 47)));
        listWrap.add(createStatRow("Đã đặt", "6", new Color(255, 248, 225), new Color(245, 124, 0)));
        listWrap.add(createStatRow("Bảo trì", "3", new Color(245, 245, 245), new Color(117, 117, 117)));
        
        pnl.add(listWrap, BorderLayout.CENTER);
        
        return pnl;
    }
    
    private JPanel createInfoPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 25));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230), 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel lblTitle = new JLabel("Thông tin bàn");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        pnl.add(lblTitle, BorderLayout.NORTH);
        
        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setOpaque(false);
        
        JPanel badge = new JPanel(new GridLayout(2, 1, 0, 5));
        badge.setBackground(new Color(240, 245, 255));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 255), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        JLabel bName = new JLabel("Bàn A01");
        bName.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel bStat = new JLabel("Trạng thái: Trống");
        bStat.setFont(new Font("SansSerif", Font.BOLD, 15));
        bStat.setForeground(new Color(46, 125, 50));
        badge.add(bName);
        badge.add(bStat);
        
        content.add(badge, BorderLayout.NORTH);
        
        JPanel form = new JPanel(new GridLayout(3, 2, 15, 20));
        form.setOpaque(false);
        
        form.add(createFieldBox("Mã bàn", "A01"));
        form.add(createFieldBox("Tên bàn", "Bàn A01"));
        form.add(createFieldBox("Khu vực", "Tầng 1"));
        form.add(createFieldBox("Sức chứa", "4 người"));
        form.add(createFieldBox("Kí hiệu khu vực", "A"));
        form.add(createFieldBox("Loại bàn", "Bàn thường"));
        
        content.add(form, BorderLayout.CENTER);
        
        pnl.add(content, BorderLayout.CENTER);
        
        return pnl;
    }

    private JPanel createStatRow(String name, String count, Color bg, Color fg) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(bg);
        row.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblName.setForeground(fg);
        
        JLabel lblCount = new JLabel(count);
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblCount.setForeground(fg);
        
        row.add(lblName, BorderLayout.WEST);
        row.add(lblCount, BorderLayout.EAST);
        
        return row;
    }

    private JPanel createFieldBox(String title, String val) {
        JPanel box = new JPanel(new BorderLayout(0, 8));
        box.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitle.setForeground(Color.GRAY);
        
        JTextField txtVal = new JTextField(val);
        txtVal.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtVal.setEditable(false);
        txtVal.setBackground(Color.WHITE);
        txtVal.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        box.add(lblTitle, BorderLayout.NORTH);
        box.add(txtVal, BorderLayout.CENTER);
        return box;
    }
    
    // UI Helpers
    private JButton createFilterButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        if (active) {
            btn.setBackground(new Color(40, 40, 40));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.DARK_GRAY);
        }
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? new Color(40,40,40) : new Color(220, 220, 220), 1, true),
            new EmptyBorder(7, 15, 7, 15)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(Math.max(0, bg.getRed()-20), Math.max(0, bg.getGreen()-20), Math.max(0, bg.getBlue()-20)), 1, true),
            new EmptyBorder(7, 18, 7, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class GhepBanDialog extends JDialog {
        public GhepBanDialog(JFrame parent) {
            super(parent, "Ghép bàn", true);
            setUndecorated(true);
            setSize(550, 480);
            setLocationRelativeTo(parent);
            
            JPanel main = new JPanel(new BorderLayout(0, 20));
            main.setBackground(Color.WHITE);
            main.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(150, 150, 150), 1),
                new EmptyBorder(30, 40, 30, 40)
            ));
            
            JLabel lblTitle = new JLabel("Phiếu yêu cầu ghép bàn");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
            main.add(lblTitle, BorderLayout.NORTH);
            
            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(12, 0, 12, 10);
            
            gbc.gridy = 0; gbc.gridx = 0;
            form.add(createLabel("Bàn hiện tại"), gbc);
            gbc.gridx = 1;
            form.add(createInput("A05"), gbc);
            
            gbc.gridx = 2;
            form.add(createLabel("Bàn ghép thêm"), gbc);
            gbc.gridx = 3;
            form.add(createInput("A06"), gbc);
            
            gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
            form.add(createLabel("Số lượng khách sau ghép"), gbc);
            gbc.gridx = 2; gbc.gridwidth = 2;
            form.add(createInput("8 khách"), gbc);
            
            gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
            form.add(createLabel("Nhân viên thực hiện"), gbc);
            gbc.gridx = 2; gbc.gridwidth = 2;
            form.add(createInput("HoangAnh"), gbc);
            
            gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 4;
            form.add(createLabel("Lý do ghép bàn"), gbc);
            
            gbc.gridy = 4;
            JTextArea txtReason = new JTextArea("- Khách đi theo nhóm đông hơn số chỗ của bàn hiện tại.\n- Cần ghép bàn để khách ngồi gần nhau và dễ phục vụ.");
            txtReason.setFont(new Font("SansSerif", Font.PLAIN, 14));
            txtReason.setLineWrap(true);
            txtReason.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
            ));
            txtReason.setPreferredSize(new Dimension(460, 90));
            form.add(txtReason, gbc);
            
            gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 1;
            form.add(createLabel("Ghi chú bổ sung"), gbc);
            
            gbc.gridx = 1; gbc.gridwidth = 3;
            JTextField txtNote = createInput("Nhập ghi chú thêm nếu có...");
            txtNote.setForeground(Color.GRAY);
            form.add(txtNote, gbc);
            
            main.add(form, BorderLayout.CENTER);
            
            // Footer buttons
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            footer.setOpaque(false);
            
            JButton btnCancel = new JButton("Hủy");
            btnCancel.setFont(new Font("SansSerif", Font.PLAIN, 15));
            btnCancel.setBackground(Color.WHITE);
            btnCancel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            btnCancel.setPreferredSize(new Dimension(90, 40));
            btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCancel.addActionListener(e -> dispose());
            
            JButton btnConfirm = new JButton("Xác nhận ghép bàn");
            btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 15));
            btnConfirm.setBackground(new Color(255, 240, 225));
            btnConfirm.setForeground(new Color(150, 80, 0));
            btnConfirm.setBorder(BorderFactory.createLineBorder(new Color(230, 200, 180)));
            btnConfirm.setPreferredSize(new Dimension(180, 40));
            btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnConfirm.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Yêu cầu ghép bàn đã được gửi thành công!");
                dispose();
            });
            
            footer.add(btnCancel);
            footer.add(btnConfirm);
            
            main.add(footer, BorderLayout.SOUTH);
            
            setContentPane(main);
        }
        
        private JLabel createLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
            return lbl;
        }

        private JTextField createInput(String text) {
            JTextField tf = new JTextField(text);
            tf.setFont(new Font("SansSerif", Font.PLAIN, 15));
            tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(6, 10, 6, 10)
            ));
            return tf;
        }
    }
}