package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import dao.PhieuDatBan_DAO;
import dao.PhieuDatMon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiMonAn;
import entity.MonAn;
import entity.PhieuDatMon;
import entity.TaiKhoan;

public class Order_Mon_GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    // ===== MÀU =====
    private final Color BG_MAIN = new Color(238, 238, 238);
    private final Color BG_TOP = new Color(245, 245, 245);

    private final Color TAB_BG = new Color(247, 241, 232);
    private final Color TAB_SELECTED = new Color(235, 218, 190);
    private final Color TAB_BORDER = new Color(225, 215, 198);

    private final Color CARD_BG = new Color(244, 236, 223);
    private final Color CARD_HOVER = new Color(236, 226, 209);

    private final Color BORDER = new Color(150, 150, 150);
    private final Color RIGHT_HEADER = new Color(206, 227, 242);

    private final Color BTN_BACK = new Color(160, 165, 175);
    private final Color BTN_ORDER = new Color(205, 150, 165);
    private final Color BTN_PAY = new Color(150, 190, 160);
    private final Color BTN_SPLIT = new Color(120, 170, 205);
    private final Color BTN_PREVIEW = new Color(205, 185, 150);

    // ===== SIZE MÓN =====
    private static final int CARD_W = 245;
    private static final int CARD_H = 285;
    private static final int FOOD_HGAP = 16;
    private static final int FOOD_VGAP = 16;

    private static final int IMG_BOX_W = 200;
    private static final int IMG_BOX_H = 135;

    // ===== GIỎ HÀNG =====
    private static final int COL_NAME_W = 250;
    private static final int COL_PRICE_W = 95;
    private static final int COL_QTY_W = 105;
    private static final int COL_TOTAL_W = 125;
    private static final int RIGHT_W = COL_NAME_W + COL_PRICE_W + COL_QTY_W + COL_TOTAL_W;

    private static final String SEARCH_PLACEHOLDER = "Nhập mã/tên món cần tìm...";
    private static final String SEARCH_ICON_PATH = "img/mn_tracuu.png";

    // ===== DATA =====
    private TaiKhoan taiKhoanDangNhap;
    private String tenBan = "Bàn 02";

    private final DecimalFormat df = new DecimalFormat("#,##0");

    private final LoaiMonAn_DAO loaiMonAnDAO = new LoaiMonAn_DAO();
    private final MonAn_DAO monAnDAO = new MonAn_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    private final PhieuDatBan_DAO phieuDatBanDAO = new PhieuDatBan_DAO();
    private final PhieuDatMon_DAO phieuDatMonDAO = new PhieuDatMon_DAO();

    private List<LoaiMonAn> dsLoai = new ArrayList<>();
    private List<MonAn> dsMon = new ArrayList<>();
    private final Map<String, OrderItem> gioHang = new LinkedHashMap<>();
    private String maLoaiDangChon = "ALL";

    // ===== UI =====
    private JTextField txtSearch;
    private JPanel pnTabs;
    private FoodGridPanel pnFoodGrid;
    private JPanel pnOrderList;
    private JLabel lblTongSoLuong;
    private JLabel lblTongTien;
    private JCheckBox chkMangVe;

    private String maBan;
    private String maHoaDonHienTai;
    private boolean daGuiThucDon = false;

    private JButton btnGuiThucDon;
    private JButton btnTamTinh;
    private JButton btnThanhToan;
    private JButton btnTachBan;
    private JButton btnChuyenBan;
    private JButton btnQuayLai;

    private String maPhieuDatBan;
    private boolean laBanDangPhucVu;

    public Order_Mon_GUI(TaiKhoan tk, String maBan, String tenBan) {
        this(tk, maBan, tenBan, null, false);
    }

    public Order_Mon_GUI(TaiKhoan tk, String maBan, String tenBan, String maPhieuDatBan, boolean laBanDangPhucVu) {
        this.taiKhoanDangNhap = tk;
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.maPhieuDatBan = maPhieuDatBan;
        this.laBanDangPhucVu = laBanDangPhucVu;

        init();
        napDuLieuBanKhiMoForm();
    }

    private void init() {
        setTitle("Order món");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        JPanel mainPanel = createMainPanel();
        Pn_ThanhMenu pnMenu = new Pn_ThanhMenu(taiKhoanDangNhap);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(pnMenu, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();

                mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
                pnMenu.setBounds(0, 0, w, h);

                layeredPane.revalidate();
                layeredPane.repaint();
            }
        });

        loadData();
        configResponsiveWindow();

        SwingUtilities.invokeLater(() -> {
            int w = getContentPane().getWidth();
            int h = getContentPane().getHeight();

            mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
            pnMenu.setBounds(0, 0, w, h);

            layeredPane.revalidate();
            layeredPane.repaint();
        });
    }

    private void napDuLieuBanKhiMoForm() {
        try {
            if (maPhieuDatBan != null && !maPhieuDatBan.trim().isEmpty()) {
                loadMonDatTheoPhieu(maPhieuDatBan);
                return;
            }

            if (laBanDangPhucVu) {
                loadMonDangPhucVuTheoBan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);

        root.add(createLeftPanel(), BorderLayout.CENTER);
        root.add(createRightPanel(), BorderLayout.EAST);

        return root;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(BG_MAIN);

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setBackground(BG_TOP);
        topPanel.setBorder(new EmptyBorder(12, 16, 10, 16));
        topPanel.setPreferredSize(new Dimension(0, 68));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftTop.setOpaque(false);

        JLabel lblTitle = new JLabel("Order");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));

        RoundedSearchPanel searchWrap = new RoundedSearchPanel();
        searchWrap.setPreferredSize(new Dimension(430, 42));
        searchWrap.setLayout(new BorderLayout());

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtSearch.setBorder(new EmptyBorder(0, 14, 0, 8));
        txtSearch.setOpaque(false);
        txtSearch.setBackground(new Color(0, 0, 0, 0));
        txtSearch.setCaretColor(Color.BLACK);
        txtSearch.setText(SEARCH_PLACEHOLDER);
        txtSearch.setForeground(new Color(150, 150, 150));

        JButton btnSearch = new JButton();
        btnSearch.setPreferredSize(new Dimension(42, 42));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(170, 170, 170)));
        btnSearch.setBackground(new Color(248, 248, 248));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon iconSearch = new ImageIcon(SEARCH_ICON_PATH);
        if (iconSearch.getIconWidth() > 0) {
            Image imgSearch = iconSearch.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
            btnSearch.setIcon(new ImageIcon(imgSearch));
        } else {
            btnSearch.setText("⌕");
            btnSearch.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }

        searchWrap.add(txtSearch, BorderLayout.CENTER);
        searchWrap.add(btnSearch, BorderLayout.EAST);

        leftTop.add(lblTitle);
        leftTop.add(searchWrap);

        JLabel lblTenBan = new JLabel(tenBan, SwingConstants.CENTER);
        lblTenBan.setFont(new Font("SansSerif", Font.BOLD, 28));

        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrap.setOpaque(false);
        centerWrap.add(lblTenBan);

        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(centerWrap, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(1), BorderLayout.EAST);

        pnTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnTabs.setBackground(BG_MAIN);
        pnTabs.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel tabWrap = new JPanel(new BorderLayout());
        tabWrap.setBackground(BG_MAIN);
        tabWrap.setPreferredSize(new Dimension(0, 58));
        tabWrap.add(pnTabs, BorderLayout.CENTER);

        pnFoodGrid = new FoodGridPanel();
        pnFoodGrid.setBackground(BG_MAIN);
        pnFoodGrid.setBorder(new EmptyBorder(14, 14, 18, 14));

        JScrollPane spFood = new JScrollPane(pnFoodGrid);
        spFood.setBorder(null);
        spFood.getViewport().setBackground(BG_MAIN);
        spFood.getVerticalScrollBar().setUnitIncrement(18);
        spFood.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel bodyWrap = new JPanel(new BorderLayout());
        bodyWrap.setBackground(BG_MAIN);
        bodyWrap.add(tabWrap, BorderLayout.NORTH);
        bodyWrap.add(spFood, BorderLayout.CENTER);

        left.add(topPanel, BorderLayout.NORTH);
        left.add(bodyWrap, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> locDanhSachMon());
        txtSearch.addActionListener(e -> locDanhSachMon());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                locDanhSachMon();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                locDanhSachMon();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                locDanhSachMon();
            }
        });

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(txtSearch.getText())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText(SEARCH_PLACEHOLDER);
                    txtSearch.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return left;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(RIGHT_W, 0));
        right.setMinimumSize(new Dimension(RIGHT_W, 0));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(130, 130, 130)));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBackground(RIGHT_HEADER);
        header.setPreferredSize(new Dimension(RIGHT_W, 82));

        header.add(createHeaderCell("Tên món", COL_NAME_W, true));
        header.add(createHeaderCell("Giá", COL_PRICE_W, true));
        header.add(createHeaderCell("Số lượng", COL_QTY_W, true));
        header.add(createHeaderCell("Thành tiền", COL_TOTAL_W, false));

        pnOrderList = new JPanel();
        pnOrderList.setLayout(new BoxLayout(pnOrderList, BoxLayout.Y_AXIS));
        pnOrderList.setBackground(Color.WHITE);

        JScrollPane spOrder = new JScrollPane(pnOrderList);
        spOrder.setBorder(null);
        spOrder.getViewport().setBackground(Color.WHITE);
        spOrder.getVerticalScrollBar().setUnitIncrement(18);
        spOrder.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setPreferredSize(new Dimension(RIGHT_W, 230));

        JPanel tongPanel = new JPanel();
        tongPanel.setLayout(new BoxLayout(tongPanel, BoxLayout.X_AXIS));
        tongPanel.setBackground(Color.WHITE);
        tongPanel.setPreferredSize(new Dimension(RIGHT_W, 58));
        tongPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER));

        JLabel lblTongText = new JLabel("Tổng cộng");
        lblTongText.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTongText.setBorder(new EmptyBorder(0, 18, 0, 0));
        lblTongText.setPreferredSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));
        lblTongText.setMinimumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));
        lblTongText.setMaximumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));

        lblTongSoLuong = new JLabel("0", SwingConstants.CENTER);
        lblTongSoLuong.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTongSoLuong.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, BORDER));
        lblTongSoLuong.setPreferredSize(new Dimension(COL_QTY_W, 58));
        lblTongSoLuong.setMinimumSize(new Dimension(COL_QTY_W, 58));
        lblTongSoLuong.setMaximumSize(new Dimension(COL_QTY_W, 58));

        lblTongTien = new JLabel("0", SwingConstants.CENTER);
        lblTongTien.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTongTien.setPreferredSize(new Dimension(COL_TOTAL_W, 58));
        lblTongTien.setMinimumSize(new Dimension(COL_TOTAL_W, 58));
        lblTongTien.setMaximumSize(new Dimension(COL_TOTAL_W, 58));

        tongPanel.add(lblTongText);
        tongPanel.add(lblTongSoLuong);
        tongPanel.add(lblTongTien);

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        chkMangVe = new JCheckBox("Mang về");
        chkMangVe.setFocusPainted(false);
        chkMangVe.setOpaque(true);
        chkMangVe.setBackground(Color.WHITE);
        chkMangVe.setFont(new Font("SansSerif", Font.PLAIN, 15));
        chkMangVe.setHorizontalAlignment(SwingConstants.CENTER);
        chkMangVe.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215), 1));

        btnGuiThucDon = new JButton("GỬI THỰC ĐƠN [F9]");
        styleMainButton(btnGuiThucDon, BTN_ORDER, Color.BLACK, 14, true);

        btnChuyenBan = new JButton("↔ CHUYỂN BÀN");
        styleMainButton(btnChuyenBan, new Color(249, 232, 198), Color.BLACK, 14, false);

        btnTachBan = new JButton("✂ TÁCH BÀN");
        styleMainButton(btnTachBan, BTN_SPLIT, Color.BLACK, 15, false);

        btnTamTinh = new JButton("TẠM TÍNH [F3]");
        styleMainButton(btnTamTinh, BTN_PREVIEW, Color.BLACK, 14, false);

        btnQuayLai = new JButton("↩ Quay lại");
        styleMainButton(btnQuayLai, BTN_BACK, Color.BLACK, 17, false);

        btnThanhToan = new JButton("Thanh toán [F4]");
        styleMainButton(btnThanhToan, BTN_PAY, Color.BLACK, 20, true);

        btnQuayLai.addActionListener(e -> {
            dispose();
            new Order_Ban_GUI(taiKhoanDangNhap).setVisible(true);
        });

        btnGuiThucDon.addActionListener(e -> guiThucDonVaLuuCSDL());

        btnChuyenBan.addActionListener(e -> {
            String msg = chkMangVe.isSelected()
                    ? "Đang chọn chuyển bàn kèm trạng thái mang về."
                    : "Đang chọn chuyển bàn.";
            JOptionPane.showMessageDialog(this, msg);
        });

        btnTachBan.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Bạn xử lý chức năng tách bàn ở bước sau.")
        );

        btnTamTinh.addActionListener(e -> {
            if (gioHang.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa có món nào để tạm tính.");
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Tạm tính: " + formatTien(tinhTongTien()) + "\nSố lượng món: " + tinhTongSoLuong());
        });

        btnThanhToan.addActionListener(e -> {
            if (gioHang.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa có món nào để thanh toán.");
                return;
            }
            String msg = chkMangVe.isSelected()
                    ? "Thanh toán đơn mang về."
                    : "Thanh toán tại bàn.";
            JOptionPane.showMessageDialog(this, msg);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        actionPanel.add(chkMangVe, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        actionPanel.add(btnGuiThucDon, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        actionPanel.add(btnChuyenBan, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        actionPanel.add(btnTachBan, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        actionPanel.add(btnTamTinh, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        actionPanel.add(btnQuayLai, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        actionPanel.add(btnThanhToan, gbc);

        bottom.add(tongPanel, BorderLayout.NORTH);
        bottom.add(actionPanel, BorderLayout.CENTER);

        right.add(header, BorderLayout.NORTH);
        right.add(spOrder, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        capNhatTrangThaiNutTheoGuiMon();
        return right;
    }

    private void capNhatTrangThaiNutTheoGuiMon() {
        if (btnTamTinh != null) {
            btnTamTinh.setEnabled(daGuiThucDon);
        }
        if (btnThanhToan != null) {
            btnThanhToan.setEnabled(daGuiThucDon);
        }
        if (btnGuiThucDon != null) {
            btnGuiThucDon.setEnabled(!gioHang.isEmpty() && !daGuiThucDon);
        }
    }

    private boolean guiThucDonVaLuuCSDL() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có món nào để gửi.");
            return false;
        }

        try {
            if (daGuiThucDon && maHoaDonHienTai != null && !maHoaDonHienTai.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bàn này đã gửi thực đơn trước đó.");
                return false;
            }

            String maHD = hoaDonDAO.taoMaHoaDonMoi();
            if (maHD == null || maHD.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tạo được mã hóa đơn.");
                return false;
            }

            String maNV = null;
            if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null) {
                maNV = taiKhoanDangNhap.getMaNV().getMaNV();
            }

            boolean taoHD = hoaDonDAO.themHoaDonMoi(
                    maHD,
                    maBan,
                    maNV,
                    null,
                    null,
                    "Chưa thanh toán"
            );

            if (!taoHD) {
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại.");
                return false;
            }

            for (OrderItem item : gioHang.values()) {
                ChiTietHoaDon ct = new ChiTietHoaDon(
                        new HoaDon(maHD),
                        new MonAn(item.mon.getMaMon()),
                        item.soLuong,
                        item.mon.getDonGia(),
                        item.ghiChu == null ? "" : item.ghiChu,
                        "Đang phục vụ",
                        null,
                        0,
                        null
                );

                boolean ok = chiTietHoaDonDAO.themChiTietHoaDon(ct);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Lưu chi tiết hóa đơn thất bại ở món: " + item.mon.getTenMon());
                    return false;
                }
            }

            boolean capNhatBan = banDAO.capNhatTrangThaiBan(maBan, "Đang phục vụ");
            if (!capNhatBan) {
                JOptionPane.showMessageDialog(this, "Không cập nhật được trạng thái bàn.");
                return false;
            }

            if (maPhieuDatBan != null && !maPhieuDatBan.trim().isEmpty()) {
                try {
                    phieuDatBanDAO.capNhatTrangThai(maPhieuDatBan, "Đã nhận bàn");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            maHoaDonHienTai = maHD;
            daGuiThucDon = true;
            capNhatTrangThaiNutTheoGuiMon();

            JOptionPane.showMessageDialog(this, "Đã gửi thực đơn.");
            dispose();
            new Order_Ban_GUI(taiKhoanDangNhap).setVisible(true);
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi gửi thực đơn.");
            return false;
        }
    }

    private void loadData() {
        try {
            dsLoai = loaiMonAnDAO.getAllLoaiMonAn();
            dsMon = monAnDAO.getAllMonAn();
            taoTabsLoai();
            locDanhSachMon();
            renderOrderList();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không tải được dữ liệu món ăn.");
        }
    }

    private void loadMonDatTheoPhieu(String maPhieu) {
        try {
            ArrayList<PhieuDatMon> dsMonDat = phieuDatMonDAO.getDanhSachTheoMaPhieu(maPhieu);
            gioHang.clear();

            if (dsMonDat != null) {
                for (PhieuDatMon pdm : dsMonDat) {
                    if (pdm == null || pdm.getMaMon() == null) continue;

                    MonAn mon = timMonTheoMaLocal(pdm.getMaMon().getMaMon());
                    if (mon == null) {
                        mon = new MonAn();
                        mon.setMaMon(pdm.getMaMon().getMaMon());
                        mon.setTenMon(pdm.getMaMon().getTenMon());
                        mon.setDonGia(pdm.getDonGia());
                    }

                    gioHang.put(mon.getMaMon(), new OrderItem(
                            mon,
                            pdm.getSoLuong(),
                            pdm.getGhiChu() == null ? "" : pdm.getGhiChu()
                    ));
                }
            }

            renderOrderList();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không tải được món đặt trước.");
        }
    }

    private void loadMonDangPhucVuTheoBan() {
        try {
            HoaDon hd = null;
            try {
                hd = hoaDonDAO.timHoaDonChuaThanhToanTheoBan(maBan);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (hd == null) return;

            maHoaDonHienTai = hd.getMaHD();
            daGuiThucDon = true;
            gioHang.clear();

            List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.getChiTietTheoMaHD(maHoaDonHienTai);
            if (dsCT != null) {
                for (ChiTietHoaDon ct : dsCT) {
                    if (ct == null || ct.getMaMon() == null) continue;

                    MonAn mon = timMonTheoMaLocal(ct.getMaMon().getMaMon());
                    if (mon == null) {
                        mon = new MonAn();
                        mon.setMaMon(ct.getMaMon().getMaMon());
                        mon.setTenMon(ct.getMaMon().getTenMon());
                        mon.setDonGia(ct.getDonGia());
                    }

                    gioHang.put(mon.getMaMon(), new OrderItem(
                            mon,
                            ct.getSoLuong(),
                            ct.getGhiChu() == null ? "" : ct.getGhiChu()
                    ));
                }
            }

            renderOrderList();
            capNhatTrangThaiNutTheoGuiMon();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không tải được món đang phục vụ.");
        }
    }

    private MonAn timMonTheoMaLocal(String maMon) {
        if (maMon == null || maMon.trim().isEmpty()) return null;

        for (MonAn mon : dsMon) {
            if (mon != null && maMon.equalsIgnoreCase(mon.getMaMon())) {
                return mon;
            }
        }

        try {
            if (monAnDAO.getMonAnTheoMa(maMon) != null) {
                return monAnDAO.getMonAnTheoMa(maMon);
            }
        } catch (Exception e) {
        }

        return null;
    }

    private JLabel createHeaderCell(String text, int width, boolean rightBorder) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(RIGHT_HEADER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setPreferredSize(new Dimension(width, 82));
        lbl.setMinimumSize(new Dimension(width, 82));
        lbl.setMaximumSize(new Dimension(width, 82));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, rightBorder ? 1 : 0, BORDER));
        return lbl;
    }

    private void styleMainButton(JButton btn, Color bg, Color fg, int fontSize, boolean bold) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215), 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize));
        btn.setMargin(new Insets(0, 6, 0, 6));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(4);
    }

    private void taoTabsLoai() {
        pnTabs.removeAll();
        pnTabs.add(createTabButton("Tất cả", "ALL"));

        for (LoaiMonAn loai : dsLoai) {
            pnTabs.add(createTabButton(loai.getTenLoaiMonAn(), loai.getMaLoaiMonAn()));
        }

        pnTabs.revalidate();
        pnTabs.repaint();
    }

    private JButton createTabButton(String text, String maLoai) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 10, 0, 10));
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setBorder(new LineBorder(TAB_BORDER, 1));
        btn.setBackground(maLoaiDangChon.equals(maLoai) ? TAB_SELECTED : TAB_BG);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addActionListener(e -> {
            maLoaiDangChon = maLoai;
            taoTabsLoai();
            locDanhSachMon();
        });

        return btn;
    }

    private void locDanhSachMon() {
        List<MonAn> dsLoc = new ArrayList<>();

        String tuKhoa = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        if (SEARCH_PLACEHOLDER.equalsIgnoreCase(tuKhoa)) {
            tuKhoa = "";
        }

        for (MonAn mon : dsMon) {
            if (!mon.isTrangThai()) continue;

            boolean dungLoai = maLoaiDangChon.equals("ALL")
                    || (mon.getMaLoaiMonAn() != null
                    && maLoaiDangChon.equals(mon.getMaLoaiMonAn().getMaLoaiMonAn()));

            if (!dungLoai) continue;

            boolean dungTuKhoa = tuKhoa.isEmpty()
                    || safe(mon.getTenMon()).toLowerCase().contains(tuKhoa)
                    || safe(mon.getMaMon()).toLowerCase().contains(tuKhoa);

            if (!dungTuKhoa) continue;

            dsLoc.add(mon);
        }

        pnFoodGrid.setFoods(dsLoc);
    }

    private void themMonVaoGio(MonAn mon) {
        OrderItem item = gioHang.get(mon.getMaMon());
        if (item == null) {
            gioHang.put(mon.getMaMon(), new OrderItem(mon, 1, ""));
        } else {
            item.soLuong++;
        }
        renderOrderList();
    }

    private void renderOrderList() {
        pnOrderList.removeAll();

        for (OrderItem item : gioHang.values()) {
            pnOrderList.add(new OrderItemPanel(item));
        }

        pnOrderList.add(Box.createVerticalGlue());
        pnOrderList.revalidate();
        pnOrderList.repaint();

        lblTongSoLuong.setText(String.valueOf(tinhTongSoLuong()));
        lblTongTien.setText(formatTien(tinhTongTien()));
        capNhatTrangThaiNutTheoGuiMon();
    }

    private int tinhTongSoLuong() {
        int tong = 0;
        for (OrderItem item : gioHang.values()) {
            tong += item.soLuong;
        }
        return tong;
    }

    private double tinhTongTien() {
        double tong = 0;
        for (OrderItem item : gioHang.values()) {
            tong += item.soLuong * item.mon.getDonGia();
        }
        return tong;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formatTien(double value) {
        return df.format(value).replace(",", ".");
    }

    private ImageIcon loadImageMon(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return null;

        String[] paths = {
                fileName,
                "img/" + fileName,
                "image/" + fileName,
                "images/" + fileName
        };

        for (String path : paths) {
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() > 0) return icon;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private void addMouseAll(Component comp, java.awt.event.MouseAdapter adapter) {
        comp.addMouseListener(adapter);
        if (comp instanceof Container) {
            for (Component c : ((Container) comp).getComponents()) {
                addMouseAll(c, adapter);
            }
        }
    }

    private void configResponsiveWindow() {
        Dimension screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds().getSize();

        setSize(screen.width, screen.height);
        setMinimumSize(new Dimension(1280, 760));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    class MonCard extends JPanel {
        private static final long serialVersionUID = 1L;

        public MonCard(MonAn mon) {
            setLayout(new BorderLayout());
            setBackground(CARD_BG);
            setBorder(new LineBorder(BORDER, 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(CARD_W, CARD_H));
            setMinimumSize(new Dimension(CARD_W, CARD_H));
            setMaximumSize(new Dimension(CARD_W, CARD_H));

            JPanel imgWrap = new JPanel(new BorderLayout());
            imgWrap.setOpaque(false);
            imgWrap.setBorder(new EmptyBorder(12, 12, 0, 12));
            imgWrap.setPreferredSize(new Dimension(CARD_W, IMG_BOX_H + 5));

            ImagePanel imgPanel = new ImagePanel(mon.getAnhMon());
            imgPanel.setPreferredSize(new Dimension(IMG_BOX_W, IMG_BOX_H));
            imgPanel.setMinimumSize(new Dimension(IMG_BOX_W, IMG_BOX_H));
            imgPanel.setMaximumSize(new Dimension(IMG_BOX_W, IMG_BOX_H));
            imgPanel.setBackground(Color.WHITE);
            imgPanel.setBorder(new LineBorder(BORDER, 1));
            imgWrap.add(imgPanel, BorderLayout.CENTER);

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(new EmptyBorder(8, 10, 10, 10));

            JLabel lblTen = new JLabel(
                    "<html><div style='text-align:center; width:190px;'>" + safe(mon.getTenMon()) + "</div></html>",
                    SwingConstants.CENTER
            );
            lblTen.setFont(new Font("SansSerif", Font.BOLD, 15));
            lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblMoTa = new JLabel(
                    "<html><div style='text-align:center; width:190px;'>Mô tả: " + safe(mon.getMoTa()) + "</div></html>"
            );
            lblMoTa.setFont(new Font("SansSerif", Font.ITALIC, 12));
            lblMoTa.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblGia = new JLabel(formatTien(mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.BOLD, 20));
            lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);

            info.add(lblTen);
            info.add(Box.createVerticalStrut(6));
            info.add(lblMoTa);
            info.add(Box.createVerticalGlue());
            info.add(Box.createVerticalStrut(8));
            info.add(lblGia);

            add(imgWrap, BorderLayout.NORTH);
            add(info, BorderLayout.CENTER);

            java.awt.event.MouseAdapter click = new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    themMonVaoGio(mon);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(CARD_HOVER);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setBackground(CARD_BG);
                }
            };
            addMouseAll(this, click);
        }
    }

    class ImagePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final String fileName;

        public ImagePanel(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ImageIcon icon = loadImageMon(fileName);
            if (icon != null) {
                Image img = icon.getImage();

                int pw = getWidth();
                int ph = getHeight();
                int iw = img.getWidth(this);
                int ih = img.getHeight(this);

                if (iw > 0 && ih > 0) {
                    double scale = Math.max((double) pw / iw, (double) ph / ih);

                    int nw = (int) Math.round(iw * scale);
                    int nh = (int) Math.round(ih * scale);

                    int x = (pw - nw) / 2;
                    int y = (ph - nh) / 2;

                    g2.setClip(0, 0, pw, ph);
                    g2.drawImage(img, x, y, nw, nh, this);
                    g2.dispose();
                    return;
                }
            }

            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
            String text = "Không có ảnh";
            int sw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (getWidth() - sw) / 2, getHeight() / 2);
            g2.dispose();
        }
    }

    class OrderItemPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final OrderItem item;
        private JTextArea lblGhiChu;
        private JPopupMenu popupGhiChu;
        private JTextField txtNote;

        public OrderItemPanel(OrderItem item) {
            this.item = item;

            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(RIGHT_W, 76));
            setPreferredSize(new Dimension(RIGHT_W, 76));
            setBackground(new Color(246, 246, 246));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(RIGHT_W, 76));

            JPanel colTen = new JPanel();
            colTen.setOpaque(false);
            colTen.setLayout(new BoxLayout(colTen, BoxLayout.Y_AXIS));
            colTen.setPreferredSize(new Dimension(COL_NAME_W, 76));
            colTen.setMinimumSize(new Dimension(COL_NAME_W, 76));
            colTen.setMaximumSize(new Dimension(COL_NAME_W, 76));
            colTen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                    new EmptyBorder(7, 10, 5, 8)
            ));

            JPanel tenRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            tenRow.setOpaque(false);
            tenRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblXoa = new JLabel("🗑");
            lblXoa.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblTen = new JLabel(
                    "<html><div style='width:220px;'>" + safe(item.mon.getTenMon()) + "</div></html>"
            );
            lblTen.setFont(new Font("SansSerif", Font.PLAIN, 14));

            tenRow.add(lblXoa);
            tenRow.add(lblTen);

            lblGhiChu = new JTextArea(getGhiChuDisplayText());
            lblGhiChu.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblGhiChu.setForeground(Color.GRAY);
            lblGhiChu.setOpaque(false);
            lblGhiChu.setEditable(false);
            lblGhiChu.setLineWrap(true);
            lblGhiChu.setWrapStyleWord(true);
            lblGhiChu.setBorder(new EmptyBorder(0, 24, 0, 0));
            lblGhiChu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblGhiChu.setFocusable(false);
            lblGhiChu.setMaximumSize(new Dimension(240, 24));
            lblGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);

            colTen.add(tenRow);
            colTen.add(Box.createVerticalStrut(2));
            colTen.add(lblGhiChu);

            JPanel colGia = new JPanel(new BorderLayout());
            colGia.setOpaque(false);
            colGia.setPreferredSize(new Dimension(COL_PRICE_W, 76));
            colGia.setMinimumSize(new Dimension(COL_PRICE_W, 76));
            colGia.setMaximumSize(new Dimension(COL_PRICE_W, 76));
            colGia.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JLabel lblGia = new JLabel(formatTien(item.mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.PLAIN, 14));
            colGia.add(lblGia, BorderLayout.CENTER);

            JPanel colSL = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 16));
            colSL.setOpaque(false);
            colSL.setPreferredSize(new Dimension(COL_QTY_W, 76));
            colSL.setMinimumSize(new Dimension(COL_QTY_W, 76));
            colSL.setMaximumSize(new Dimension(COL_QTY_W, 76));
            colSL.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JButton btnMinus = new JButton("⊖");
            JButton btnPlus = new JButton("⊕");
            JLabel lblSL = new JLabel(String.valueOf(item.soLuong), SwingConstants.CENTER);

            styleQtyButton(btnMinus);
            styleQtyButton(btnPlus);

            lblSL.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblSL.setBorder(new LineBorder(Color.GRAY, 1));
            lblSL.setPreferredSize(new Dimension(34, 28));

            btnMinus.addActionListener(e -> {
                item.soLuong--;
                if (item.soLuong <= 0) {
                    gioHang.remove(item.mon.getMaMon());
                }
                renderOrderList();
            });

            btnPlus.addActionListener(e -> {
                item.soLuong++;
                renderOrderList();
            });

            lblXoa.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!daGuiThucDon) {
                        gioHang.remove(item.mon.getMaMon());
                        renderOrderList();
                        return;
                    }

                    moDialogHuyMon(item);
                }
            });

            lblGhiChu.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showNotePopup();
                }
            });

            colSL.add(btnMinus);
            colSL.add(lblSL);
            colSL.add(btnPlus);

            JPanel colTotal = new JPanel(new BorderLayout());
            colTotal.setOpaque(false);
            colTotal.setPreferredSize(new Dimension(COL_TOTAL_W, 76));
            colTotal.setMinimumSize(new Dimension(COL_TOTAL_W, 76));
            colTotal.setMaximumSize(new Dimension(COL_TOTAL_W, 76));

            JLabel lblTotal = new JLabel(formatTien(item.soLuong * item.mon.getDonGia()), SwingConstants.CENTER);
            lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
            colTotal.add(lblTotal, BorderLayout.CENTER);

            row.add(colTen);
            row.add(colGia);
            row.add(colSL);
            row.add(colTotal);

            add(row, BorderLayout.CENTER);
        }

        private String getGhiChuDisplayText() {
            String gc = item.ghiChu == null ? "" : item.ghiChu.trim();
            if (gc.isEmpty()) return "Ghi chú 📝";
            return gc;
        }

        private void showNotePopup() {
            if (popupGhiChu != null && popupGhiChu.isVisible()) {
                popupGhiChu.setVisible(false);
            }

            popupGhiChu = new JPopupMenu();
            popupGhiChu.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(170, 170, 170), 1),
                    new EmptyBorder(4, 4, 4, 4)
            ));

            txtNote = new JTextField(item.ghiChu == null ? "" : item.ghiChu);
            txtNote.setFont(new Font("SansSerif", Font.PLAIN, 12));
            txtNote.setPreferredSize(new Dimension(220, 28));
            txtNote.setBorder(new EmptyBorder(4, 6, 4, 6));

            txtNote.addActionListener(e -> saveAndHidePopup());
            txtNote.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        if (txtNote != null && !txtNote.hasFocus()) {
                            saveAndHidePopup();
                        }
                    });
                }
            });

            popupGhiChu.add(txtNote);
            popupGhiChu.show(lblGhiChu, 0, lblGhiChu.getHeight() + 2);

            SwingUtilities.invokeLater(() -> {
                txtNote.requestFocusInWindow();
                txtNote.selectAll();
            });
        }

        private void saveAndHidePopup() {
            if (txtNote != null) {
                item.ghiChu = txtNote.getText().trim();
                lblGhiChu.setText(getGhiChuDisplayText());
            }
            if (popupGhiChu != null) {
                popupGhiChu.setVisible(false);
            }
        }

        private void styleQtyButton(JButton btn) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setPreferredSize(new Dimension(28, 28));
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    static class OrderItem {
        MonAn mon;
        int soLuong;
        String ghiChu;

        public OrderItem(MonAn mon, int soLuong, String ghiChu) {
            this.mon = mon;
            this.soLuong = soLuong;
            this.ghiChu = ghiChu;
        }
    }

    static class WrapLayout extends FlowLayout {
        private static final long serialVersionUID = 1L;

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();

                if (targetWidth <= 0) {
                    Container parent = target.getParent();
                    if (parent != null) {
                        targetWidth = parent.getWidth();
                    }
                }

                if (targetWidth <= 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                Insets insets = target.getInsets();
                int hgap = getHgap();
                int vgap = getVgap();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible()) continue;

                    Dimension d = m.getPreferredSize();

                    if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);

            if (dim.height > 0) {
                dim.height += getVgap();
            }

            dim.height += rowHeight;
        }
    }

    class FoodGridPanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        private List<MonAn> dsFoods = new ArrayList<>();

        public FoodGridPanel() {
            setLayout(new WrapLayout(FlowLayout.LEFT, FOOD_HGAP, FOOD_VGAP));
            setOpaque(false);

            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    revalidate();
                    repaint();
                }
            });
        }

        public void setFoods(List<MonAn> dsMon) {
            dsFoods = dsMon == null ? new ArrayList<>() : new ArrayList<>(dsMon);
            rebuildGrid();
        }

        private void rebuildGrid() {
            removeAll();

            if (dsFoods == null || dsFoods.isEmpty()) {
                add(Box.createVerticalStrut(10));
                revalidate();
                repaint();
                return;
            }

            for (MonAn mon : dsFoods) {
                add(new MonCard(mon));
            }

            revalidate();
            repaint();
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 60;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    static class RoundedSearchPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        public RoundedSearchPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(248, 248, 248));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(new Color(120, 120, 120));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void moDialogHuyMon(OrderItem item) {
        JTextField txtLyDo = new JTextField();
        Object[] message = {
                "Lý do hủy món:", txtLyDo
        };

        int chon = JOptionPane.showConfirmDialog(
                this,
                message,
                "Hủy món",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (chon != JOptionPane.OK_OPTION) return;

        String lyDo = txtLyDo.getText().trim();
        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phải nhập lý do hủy.");
            return;
        }

        boolean ok = chiTietHoaDonDAO.huyMon(
                maHoaDonHienTai,
                item.mon.getMaMon(),
                lyDo,
                item.soLuong
        );

        if (ok) {
            gioHang.remove(item.mon.getMaMon());
            renderOrderList();
            JOptionPane.showMessageDialog(this, "Đã hủy món.");
        } else {
            JOptionPane.showMessageDialog(this, "Hủy món thất bại.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Order_Mon_GUI gui = new Order_Mon_GUI(null, "B02", "Bàn 02");
            gui.setVisible(true);
        });
    }
}