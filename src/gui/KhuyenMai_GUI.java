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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser;

import entity.TaiKhoan;

public class KhuyenMai_GUI extends JFrame {

    private TaiKhoan taiKhoanDangNhap;

    private JTextField txtSearch;

    public KhuyenMai_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Quản lý khuyến mãi");
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
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // --- TOP SEARCH & FILTER PANEL ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)));

        // LEFT: Search Box
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(5, 10, 5, 10)));

        JLabel lblSearchIcon = new JLabel("🔍"); // Unicode magnifying glass
        lblSearchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblSearchIcon.setForeground(Color.GRAY);
        lblSearchIcon.setBorder(new EmptyBorder(0, 0, 0, 5));

        txtSearch = new JTextField("Tìm kiếm khuyến mãi", 25);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(null);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm khuyến mãi")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm khuyến mãi");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        searchBox.add(lblSearchIcon, BorderLayout.WEST);
        searchBox.add(txtSearch, BorderLayout.CENTER);

        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftTopPanel.setOpaque(false);
        leftTopPanel.add(searchBox);
        topPanel.add(leftTopPanel, BorderLayout.WEST);

        // RIGHT: Date Filter
        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTopPanel.setOpaque(false);

        JLabel lblStartDate = new JLabel("Ngày bắt đầu:");
        lblStartDate.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JDateChooser dateStart = new JDateChooser();
        dateStart.setPreferredSize(new Dimension(130, 30));
        dateStart.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel lblEndDate = new JLabel("Ngày kết thúc:");
        lblEndDate.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JDateChooser dateEnd = new JDateChooser();
        dateEnd.setPreferredSize(new Dimension(130, 30));
        dateEnd.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton btnFilter = new JButton("Lọc");
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnFilter.setBackground(new Color(245, 235, 225));
        btnFilter.setForeground(new Color(120, 90, 70));
        btnFilter.setFocusPainted(false);
        btnFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 215, 195), 1),
                new EmptyBorder(5, 20, 5, 20)));
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightTopPanel.add(lblStartDate);
        rightTopPanel.add(dateStart);
        rightTopPanel.add(lblEndDate);
        rightTopPanel.add(dateEnd);
        rightTopPanel.add(btnFilter);

        topPanel.add(rightTopPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Content Header
        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Khuyến mãi cố định theo hạng thành viên");
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblTitle.setForeground(Color.DARK_GRAY);
        contentHeader.add(lblTitle, BorderLayout.WEST);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnAdd = new JButton("+ Thêm khuyến mãi");
        btnAdd.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnAdd.setBackground(new Color(230, 244, 234));
        btnAdd.setForeground(new Color(46, 125, 50));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnDelete.setBackground(new Color(245, 235, 225));
        btnDelete.setForeground(new Color(120, 90, 70));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 215, 195), 1, true),
                BorderFactory.createEmptyBorder(7, 20, 7, 20)));
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        actionButtons.add(btnAdd);
        actionButtons.add(btnDelete);
        contentHeader.add(actionButtons, BorderLayout.EAST);

        centerPanel.add(contentHeader, BorderLayout.NORTH);

        // Cards list
        JPanel cardsContainer = new JPanel(new GridLayout(0, 2, 20, 20));
        cardsContainer.setOpaque(false);

        cardsContainer.add(new PromotionCard(
                "KM01 - Thành viên thẻ vàng",
                "Giảm trực tiếp trên tổng hóa đơn",
                "Thành viên thẻ vàng",
                "10%",
                "Không thời hạn",
                "Hoạt động"));

        cardsContainer.add(new PromotionCard(
                "KM02 - Thành viên thẻ kim cương",
                "Giảm trực tiếp trên tổng hóa đơn",
                "Thành viên kim cương",
                "15%",
                "Không thời hạn",
                "Hoạt động"));

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.add(cardsContainer, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(cardsWrapper);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        centerWrapper.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return mainPanel;
    }

    static class PromotionCard extends JPanel {
        public PromotionCard(String title, String subtitle, String target, String discount, String type,
                String status) {
            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));

            // Header Panel
            JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
            headerPanel.setOpaque(false);
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
            lblTitle.setForeground(new Color(30, 30, 30));

            JLabel lblSubtitle = new JLabel(subtitle);
            lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblSubtitle.setForeground(new Color(80, 80, 80));

            headerPanel.add(lblTitle);
            headerPanel.add(lblSubtitle);

            add(headerPanel, BorderLayout.NORTH);

            // Details Panel
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 0, 10, 15);

            addDetailRow(detailsPanel, gbc, 0, "Đối tượng áp dụng:", target);
            addDetailRow(detailsPanel, gbc, 1, "Mức giảm:", discount);
            addDetailRow(detailsPanel, gbc, 2, "Loại khuyến mãi:", type);

            // Status Row
            gbc.gridy = 3;
            gbc.gridx = 0;
            gbc.weightx = 0;
            JLabel lblStatusKey = new JLabel("Trạng thái:");
            lblStatusKey.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblStatusKey.setForeground(Color.DARK_GRAY);
            detailsPanel.add(lblStatusKey, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            JLabel lblStatusVal = new JLabel(status);
            lblStatusVal.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblStatusVal.setOpaque(true);
            lblStatusVal.setBackground(new Color(230, 244, 234));
            lblStatusVal.setForeground(new Color(46, 125, 50));
            lblStatusVal.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

            JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            statusWrap.setOpaque(false);
            statusWrap.add(lblStatusVal);
            detailsPanel.add(statusWrap, gbc);

            add(detailsPanel, BorderLayout.CENTER);

            // Footer Panel (Edit Button)
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            footerPanel.setOpaque(false);
            JButton btnEdit = new JButton("Sửa");
            btnEdit.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btnEdit.setBackground(new Color(245, 235, 225));
            btnEdit.setForeground(new Color(120, 90, 70));
            btnEdit.setFocusPainted(false);
            btnEdit.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 215, 195), 1, true),
                    BorderFactory.createEmptyBorder(6, 25, 6, 25)));
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            footerPanel.add(btnEdit);

            add(footerPanel, BorderLayout.SOUTH);
        }

        private void addDetailRow(JPanel pnl, GridBagConstraints gbc, int row, String key, String value) {
            gbc.gridy = row;

            gbc.gridx = 0;
            gbc.weightx = 0;
            JLabel lblKey = new JLabel(key);
            lblKey.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblKey.setForeground(Color.DARK_GRAY);
            pnl.add(lblKey, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            JLabel lblVal = new JLabel(value);
            lblVal.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblVal.setForeground(Color.BLACK);
            pnl.add(lblVal, gbc);
        }
    }
}