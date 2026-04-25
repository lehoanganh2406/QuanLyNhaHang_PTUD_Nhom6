package gui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser;

import entity.TaiKhoan;

public class KhuyenMai_GUI extends JPanel {

    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;
    private JTextField txtSearch;

    private final Color BG = new Color(245, 245, 245);
    private final Color BORDER = new Color(220, 220, 220);

    public KhuyenMai_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(createMainPanel(), BorderLayout.CENTER);
    }

    public KhuyenMai_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterWrapper(), BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setBackground(BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setPreferredSize(new Dimension(320, 38));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSearchIcon.setForeground(Color.GRAY);
        lblSearchIcon.setBorder(new EmptyBorder(0, 0, 0, 6));

        txtSearch = new JTextField("Tìm kiếm khuyến mãi");
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

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTopPanel.setOpaque(false);

        JLabel lblStartDate = createFilterLabel("Ngày bắt đầu:");
        JDateChooser dateStart = createDateChooser();

        JLabel lblEndDate = createFilterLabel("Ngày kết thúc:");
        JDateChooser dateEnd = createDateChooser();

        JButton btnFilter = createSmallButton("Lọc");

        rightTopPanel.add(lblStartDate);
        rightTopPanel.add(dateStart);
        rightTopPanel.add(lblEndDate);
        rightTopPanel.add(dateEnd);
        rightTopPanel.add(btnFilter);

        topPanel.add(rightTopPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createCenterWrapper() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        centerPanel.add(createContentHeader(), BorderLayout.NORTH);
        centerPanel.add(createCardsScroll(), BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        centerWrapper.add(centerPanel, BorderLayout.CENTER);

        return centerWrapper;
    }

    private JPanel createContentHeader() {
        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Khuyến mãi cố định theo hạng thành viên");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(Color.DARK_GRAY);
        contentHeader.add(lblTitle, BorderLayout.WEST);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnAdd = createAddButton("+ Thêm khuyến mãi");
        JButton btnDelete = createSmallButton("Xóa");

        actionButtons.add(btnAdd);
        actionButtons.add(btnDelete);

        contentHeader.add(actionButtons, BorderLayout.EAST);

        return contentHeader;
    }

    private JScrollPane createCardsScroll() {
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

        return scrollPane;
    }

    private JLabel createFilterLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return lbl;
    }

    private JDateChooser createDateChooser() {
        JDateChooser chooser = new JDateChooser();
        chooser.setPreferredSize(new Dimension(135, 32));
        chooser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return chooser;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(245, 235, 225));
        btn.setForeground(new Color(120, 90, 70));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 215, 195), 1, true),
                new EmptyBorder(7, 20, 7, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createAddButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(230, 244, 234));
        btn.setForeground(new Color(46, 125, 50));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static class PromotionCard extends JPanel {
        private static final long serialVersionUID = 1L;

        public PromotionCard(String title, String subtitle, String target,
                             String discount, String type, String status) {

            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(15, 20, 15, 20)
            ));

            add(createHeader(title, subtitle), BorderLayout.NORTH);
            add(createDetails(target, discount, type, status), BorderLayout.CENTER);
            add(createFooter(), BorderLayout.SOUTH);
        }

        private JPanel createHeader(String title, String subtitle) {
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

            return headerPanel;
        }

        private JPanel createDetails(String target, String discount, String type, String status) {
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 0, 10, 15);

            addDetailRow(detailsPanel, gbc, 0, "Đối tượng áp dụng:", target);
            addDetailRow(detailsPanel, gbc, 1, "Mức giảm:", discount);
            addDetailRow(detailsPanel, gbc, 2, "Loại khuyến mãi:", type);

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
            lblStatusVal.setBorder(new EmptyBorder(4, 12, 4, 12));

            JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            statusWrap.setOpaque(false);
            statusWrap.add(lblStatusVal);

            detailsPanel.add(statusWrap, gbc);

            return detailsPanel;
        }

        private JPanel createFooter() {
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            footerPanel.setOpaque(false);

            JButton btnEdit = new JButton("Sửa");
            btnEdit.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btnEdit.setBackground(new Color(245, 235, 225));
            btnEdit.setForeground(new Color(120, 90, 70));
            btnEdit.setFocusPainted(false);
            btnEdit.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(230, 215, 195), 1, true),
                    new EmptyBorder(6, 25, 6, 25)
            ));
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

            footerPanel.add(btnEdit);
            return footerPanel;
        }

        private void addDetailRow(JPanel pnl, GridBagConstraints gbc,
                                  int row, String key, String value) {
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