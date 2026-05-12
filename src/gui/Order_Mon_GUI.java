package gui;

import java.awt.BorderLayout;

import java.awt.Dialog;
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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDateTime;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_Ban_DAO;
import dao.HoaDon_DAO;
import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import dao.PhieuDatBan_DAO;
import dao.ChiTietDatMon_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiMonAn;
import entity.MonAn;
import entity.ChiTietDatMon;
import entity.TaiKhoan;
import digLog.ChuyenBan_DigLog;
import digLog.GhepBan_DigLog;
import digLog.TachBan_DigLog;

public class Order_Mon_GUI extends JPanel {


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
    private static final int HEADER_H = 58;
    private static final int ORDER_ROW_H = 58;
    private static final int RIGHT_W = COL_NAME_W + COL_PRICE_W + COL_QTY_W + COL_TOTAL_W;

    private static final String SEARCH_PLACEHOLDER = "Nhập mã/tên món cần tìm...";
    private static final String SEARCH_ICON_PATH = "img/mn_tracuu.png";

    // ===== DATA =====
    private TaiKhoan taiKhoanDangNhap;
    private String tenBan = "Bàn 02";
    private boolean laOrderRieng = false;
    private String tenBanHienThi;

    private final DecimalFormat df = new DecimalFormat("#,##0");

    private final LoaiMonAn_DAO loaiMonAnDAO = new LoaiMonAn_DAO();
    private final MonAn_DAO monAnDAO = new MonAn_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    private final PhieuDatBan_DAO phieuDatBanDAO = new PhieuDatBan_DAO();
    private final ChiTietDatMon_DAO chiTietDatMonDAO = new ChiTietDatMon_DAO();
    private final HoaDon_Ban_DAO hoaDonBanDAO = new HoaDon_Ban_DAO();

    private List<LoaiMonAn> dsLoai = new ArrayList<>();
    private List<MonAn> dsMon = new ArrayList<>();
    private final Map<String, OrderItem> gioHang = new LinkedHashMap<>();
    private String maLoaiDangChon = "ALL";
    private final Map<String, String> trangThaiMap =
            new LinkedHashMap<>();

    // ===== UI =====
    private JTextField txtSearch;
    private JPanel pnTabs;
    private FoodGridPanel pnFoodGrid;
    private JPanel pnOrderList;
    private JLabel lblTongSoLuong;
    private JLabel lblTongTien;
    private JCheckBox chkMangVe;
    private boolean mangVeMacDinh = false;

    private String maBan;
    private String maHoaDonHienTai;
    private boolean daGuiThucDon = false;
    private boolean coThayDoiChuaGui = false;
    private final Map<String, PendingHuyItem> dsMonChoHuy = new LinkedHashMap<>();
    private final Map<String, Integer> soLuongDaGuiMap = new LinkedHashMap<>();

    private JButton btnGuiThucDon;
    private JButton btnThanhToan;
    private JButton btnTachBan;
    private JButton btnGhepBan;
    private JButton btnChuyenBan;
    private JButton btnQuayLai;

    private String maPhieuDatBan;
    private boolean laBanDangPhucVu;
	private JLabel lblTenBan;
	private ManHinhKhach_GUI manHinhKhach;
    public Order_Mon_GUI(TaiKhoan tk, String maBan, String tenBan, String maPhieuDatBan, boolean laBanDangPhucVu, ManHinhKhach_GUI manHinhKhach) {
        this(tk, maBan, tenBan, maPhieuDatBan, laBanDangPhucVu, false,false,manHinhKhach);
    }

    public Order_Mon_GUI(TaiKhoan tk, String maBan, String tenBan,
            String maPhieuDatBan, boolean laBanDangPhucVu, boolean mangVeMacDinh,boolean laOrderRieng, ManHinhKhach_GUI manHinhKhach) {
        this.taiKhoanDangNhap = tk;
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.tenBanHienThi = tenBan;
        this.maPhieuDatBan = maPhieuDatBan;
        this.laBanDangPhucVu = laBanDangPhucVu;
        this.mangVeMacDinh = mangVeMacDinh;
        this.laOrderRieng = laOrderRieng;
        this.manHinhKhach = manHinhKhach;

        init();
        napDuLieuBanKhiMoForm();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        loadData();
    }

    private void napDuLieuBanKhiMoForm() {

        try {

            gioHang.clear();

            dsMonChoHuy.clear();

            soLuongDaGuiMap.clear();

            // =========================================
            // LOAD CHECK MANG VỀ
            // =========================================

            HoaDon hd =
                    hoaDonDAO
                            .timHoaDonChuaThanhToanTheoBan(
                                    maBan
                            );

            if(
                    hd != null
                    &&
                    hd.getHinhThucPhucVu() != null
            ){

                chkMangVe.setSelected(
                        hd.getHinhThucPhucVu()
                        .equalsIgnoreCase("Mang về")
                );

            }else{

                chkMangVe.setSelected(false);
            }

            // =========================================
            // CHECK THEO CHI TIẾT HÓA ĐƠN
            // =========================================

            boolean coMonDangPhucVu =
                    chiTietHoaDonDAO
                            .kiemTraBanCoMonTheoBan(maBan);

            if (coMonDangPhucVu) {

                loadMonDangPhucVuTheoBan();
                capNhatManHinhKhach();

                return;
            }

            // =========================================
            // LOAD PHIẾU ĐẶT
            // =========================================

            if (
                    maPhieuDatBan != null
                    &&
                    !maPhieuDatBan.trim().isEmpty()
            ) {

                loadMonDatTheoPhieu(maPhieuDatBan);
                capNhatManHinhKhach();

                return;
            }

            renderOrderList();
            capNhatManHinhKhach();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi load dữ liệu bàn."
            );
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

        lblTenBan = new JLabel(tenBanHienThi, SwingConstants.CENTER);
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
        header.setPreferredSize(new Dimension(RIGHT_W, HEADER_H));
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
        chkMangVe.setSelected(mangVeMacDinh);
        chkMangVe.setFocusPainted(false);
        chkMangVe.setOpaque(true);
        chkMangVe.setBackground(Color.WHITE);
        chkMangVe.setFont(new Font("SansSerif", Font.PLAIN, 15));
        chkMangVe.setHorizontalAlignment(SwingConstants.CENTER);
        chkMangVe.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215), 1));
        chkMangVe.addActionListener(e -> {
            if (daGuiThucDon && coThayDoiChuaGui) {
                chkMangVe.setSelected(false);
                JOptionPane.showMessageDialog(
                        this,
                        "Bàn đã có món tại bàn. Không thể chọn Mang về cho món order thêm trong cùng hóa đơn."
                );
                return;
            }

            capNhatTrangThaiNutTheoGuiMon();
        });

        btnGuiThucDon = new JButton("GỬI THỰC ĐƠN [F9]");
        styleMainButton(btnGuiThucDon, BTN_ORDER, Color.BLACK, 14, true);

        btnChuyenBan = new JButton("↔ CHUYỂN BÀN");
        styleMainButton(btnChuyenBan, new Color(249, 232, 198), Color.BLACK, 14, false);
        
        btnGhepBan = new JButton("🪑 GHÉP BÀN");
        styleMainButton(btnGhepBan, new Color(255, 210, 140), Color.BLACK, 14, false);

        btnTachBan = new JButton("✂ TÁCH BÀN");
        styleMainButton(btnTachBan, BTN_SPLIT, Color.BLACK, 15, false);

        btnQuayLai = new JButton("↩ Quay lại");
        styleMainButton(btnQuayLai, BTN_BACK, Color.BLACK, 17, false);

        btnThanhToan = new JButton("Thanh toán [F4]");
        styleMainButton(btnThanhToan, BTN_PAY, Color.BLACK, 20, true);

        btnQuayLai.addActionListener(e -> {

            if (!xacNhanRoiManHinhNeuCoThayDoi()) {
                return;
            }

            if (manHinhKhach != null) {

                manHinhKhach.resetVeMacDinh();
            }

            Window w = SwingUtilities.getWindowAncestor(Order_Mon_GUI.this);

            if (w instanceof TrangChu_GUI) {

                ((TrangChu_GUI) w).showCustomPage(
                        "Order_Ban_GUI",
                        new Order_Ban_GUI(taiKhoanDangNhap)
                );
            }
        });
        btnGuiThucDon.addActionListener(e -> guiThucDonVaLuuCSDL(true));

        btnChuyenBan.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(Order_Mon_GUI.this);

            ChuyenBan_DigLog dlg = new ChuyenBan_DigLog(owner, maBan, tenBan);
            dlg.setVisible(true);

            String maBanMoi = dlg.getMaBanMoi();

            if (maBanMoi == null || maBanMoi.trim().isEmpty()) {
                return;
            }

            try {
                // 1. lấy hóa đơn hiện tại
                HoaDon hd = hoaDonDAO.timHoaDonChuaThanhToanTheoBan(maBan);

                if (hd == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn.");
                    return;
                }

                String maHD = hd.getMaHD();

                // 2. chuyển bàn trong hóa đơn
                boolean okHD =
                        hoaDonBanDAO.capNhatBanHoaDon(
                                maHD,
                                maBan,
                                maBanMoi
                        );

                if (!okHD) {
                    JOptionPane.showMessageDialog(this, "Chuyển bàn thất bại.");
                    return;
                }

                // 3. cập nhật trạng thái bàn
                banDAO.capNhatTrangThaiBan(maBan, "Bàn trống");
                banDAO.capNhatTrangThaiBan(maBanMoi, "Đang phục vụ");

                // 4. load lại Order màn hình bàn mới
                Window w = SwingUtilities.getWindowAncestor(Order_Mon_GUI.this);

                if (w instanceof TrangChu_GUI) {
                    ((TrangChu_GUI) w).showCustomPage(
                            "Order_Mon_GUI",
                            new Order_Mon_GUI(
                                    taiKhoanDangNhap,
                                    maBanMoi,
                                    maBanMoi,
                                    null,
                                    true,
                                    false,
                                    false,
                                    manHinhKhach
                            )
                    );
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi chuyển bàn.");
            }
        });

        btnTachBan.addActionListener(e -> {
            if (!daGuiThucDon) {
                JOptionPane.showMessageDialog(this, "Vui lòng gửi thực đơn trước khi tách bàn.");
                return;
            }

            HoaDon hd = hoaDonDAO.timHoaDonChuaThanhToanTheoBan(maBan);

            if (hd == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn hiện tại.");
                return;
            }

            Window owner = SwingUtilities.getWindowAncestor(Order_Mon_GUI.this);

            TachBan_DigLog dlg = new TachBan_DigLog(
                    owner,
                    taiKhoanDangNhap,
                    maBan,
                    hd.getMaHD()
            );

            dlg.setVisible(true);

            if (dlg.isTachThanhCong()) {
                Window w = SwingUtilities.getWindowAncestor(Order_Mon_GUI.this);

                if (w instanceof TrangChu_GUI) {
                    ((TrangChu_GUI) w).showCustomPage(
                            "Order_Mon_GUI",
                            new Order_Mon_GUI(
                                    taiKhoanDangNhap,
                                    maBan,
                                    tenBan,
                                    null,
                                    true,
                                    false,
                                    false,
                                    manHinhKhach
                            )
                    );
                }
            }
        });
        btnGhepBan.addActionListener(e -> {

            GhepBan_DigLog dlg =
                    new GhepBan_DigLog(
                            null,
                            maBan
                    );

            dlg.setVisible(true);

            // reload lại dữ liệu hóa đơn hiện tại
            loadMonDangPhucVuTheoBan();

            renderOrderList();
            capNhatManHinhKhach();

            revalidate();

            repaint();

        });

        
        btnThanhToan.addActionListener(e -> {

            // =====================================
            // CHECK GIỎ HÀNG
            // =====================================

            if (gioHang.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Bàn hiện tại chưa có món để thanh toán."
                );

                return;
            }

            boolean laMangVe =
                    chkMangVe != null
                    && chkMangVe.isSelected();

            // =====================================
            // TẠI BÀN -> BẮT BUỘC GỬI BẾP
            // =====================================

            if (
                    !laMangVe
                    &&
                    (
                            coThayDoiChuaGui
                            ||
                            !dsMonChoHuy.isEmpty()
                    )
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Có món chưa gửi thực đơn. Vui lòng gửi thực đơn trước khi thanh toán."
                );

                return;
            }

            // =====================================
            // MANG VỀ -> AUTO LƯU + GỬI BẾP
            // =====================================

            if (
                    laMangVe
                    &&
                    (
                            !daGuiThucDon
                            ||
                            coThayDoiChuaGui
                            ||
                            !dsMonChoHuy.isEmpty()
                    )
            ) {

                boolean okLuu =
                        guiThucDonVaLuuCSDL(false);

                if (!okLuu) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Không lưu được hóa đơn mang về."
                    );

                    return;
                }
            }

            // =====================================
            // MỞ THANH TOÁN
            // =====================================

            Window w =
                    SwingUtilities.getWindowAncestor(
                            Order_Mon_GUI.this
                    );

            if (w instanceof TrangChu_GUI) {
            	if (manHinhKhach != null) {

            	    manHinhKhach.hienThiThongTinThanhToan(
            	            "Tiền mặt",
            	            "0",
            	            "0"
            	    );

            	    manHinhKhach.revalidate();
            	    manHinhKhach.repaint();
            	}
                ((TrangChu_GUI) w).showCustomPage(
                        "Order_ThanhToan_GUI",

                        new Order_ThanhToan_GUI(
                                taiKhoanDangNhap,
                                maBan,
                                tenBan,
                                manHinhKhach
                        )
                );
            }
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
        actionPanel.add(btnGhepBan, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        actionPanel.add(btnTachBan, gbc);

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
    private void capNhatCoThayDoiChuaGui() {
        if (!dsMonChoHuy.isEmpty()) {
            coThayDoiChuaGui = true;
            capNhatTrangThaiNutTheoGuiMon();
            return;
        }

        for (OrderItem item : gioHang.values()) {
        	int slDaGui = soLuongDaGuiMap.getOrDefault(
        	        taoKeyGioHang(
        	                item.mon.getMaMon(),
        	                item.maBan
        	        ),
        	        0
        	);

            if (item.soLuong != slDaGui) {
                coThayDoiChuaGui = true;
                capNhatTrangThaiNutTheoGuiMon();
                return;
            }
        }

        for (String maMonDaGui : soLuongDaGuiMap.keySet()) {
            if (!gioHang.containsKey(maMonDaGui)) {
                coThayDoiChuaGui = true;
                capNhatTrangThaiNutTheoGuiMon();
                return;
            }
        }

        coThayDoiChuaGui = false;
        capNhatTrangThaiNutTheoGuiMon();
    }

    private void capNhatTrangThaiNutTheoGuiMon() {

        boolean coMonTrongGio = false;

        // =========================
        // CHECK GIỎ HÀNG
        // =========================

        if (!gioHang.isEmpty()) {
            coMonTrongGio = true;
        }

        // =========================
        // CHECK HÓA ĐƠN DB
        // =========================

        HoaDon hd =
                hoaDonDAO
                .timHoaDonChuaThanhToanTheoBan(maBan);

        if (hd != null) {

            maHoaDonHienTai = hd.getMaHD();

            List<ChiTietHoaDon> dsCT =
                    chiTietHoaDonDAO
                    .getChiTietTheoMaHD(
                            hd.getMaHD()
                    );

            if (dsCT != null && !dsCT.isEmpty()) {
                coMonTrongGio = true;
            }
        }

        boolean coDuLieuDeGui =
                !gioHang.isEmpty()
                || !dsMonChoHuy.isEmpty();

        boolean laMangVe =
                chkMangVe != null
                && chkMangVe.isSelected();

        boolean coMonChuaGui =
                coThayDoiChuaGui
                || !dsMonChoHuy.isEmpty();

        boolean choTamTinhThanhToan =
                laMangVe
                || (
                        maHoaDonHienTai != null
                        && !coMonChuaGui
                   );

        // =========================
        // NÚT THANH TOÁN
        // =========================

        if (btnThanhToan != null) {

            btnThanhToan.setEnabled(
                    coMonTrongGio
                    && choTamTinhThanhToan
            );
        }

        // =========================
        // NÚT GỬI MÓN
        // =========================

        if (btnGuiThucDon != null) {

            btnGuiThucDon.setEnabled(
                    coDuLieuDeGui
                    && (!daGuiThucDon || coThayDoiChuaGui)
            );
        }
    }
    private boolean xacNhanRoiManHinhNeuCoThayDoi() {
        if (!coThayDoiChuaGui && dsMonChoHuy.isEmpty()) {
            return true;
        }

        int chon = JOptionPane.showConfirmDialog(
                this,
                "Có món đã thêm/sửa/hủy nhưng chưa gửi thực đơn.\nBạn có chắc muốn rời khỏi màn hình Order món không?",
                "Cảnh báo chưa gửi thực đơn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return chon == JOptionPane.YES_OPTION;
    }
    private boolean guiThucDonVaLuuCSDL(boolean quayLaiSauKhiGui) {

        if (gioHang.isEmpty() && dsMonChoHuy.isEmpty()) {
            return false;
        }

        try {

            // =================================================
            // TÌM HÓA ĐƠN CHUNG THEO BÀN
            // =================================================

            HoaDon hdCu = hoaDonDAO.timHoaDonChuaThanhToanTheoBan(maBan);

            String maHD;

            // =================================================
            // ĐÃ CÓ HÓA ĐƠN
            // =================================================

            if (hdCu != null) {

                maHD = hdCu.getMaHD();

                maHoaDonHienTai = maHD;

                daGuiThucDon = true;

            }

            // =================================================
            // CHƯA CÓ -> TẠO HÓA ĐƠN
            // =================================================

            else {

                maHD = hoaDonDAO.taoMaHoaDonMoi();

                if (maHD == null || maHD.trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Không tạo được mã hóa đơn."
                    );

                    return false;
                }

                String maNV = null;

                if (
                        taiKhoanDangNhap != null
                        &&
                        taiKhoanDangNhap.getMaNV() != null
                ) {

                    maNV =
                            taiKhoanDangNhap
                                    .getMaNV()
                                    .getMaNV();
                }

                String hinhThucPhucVu =
                        chkMangVe.isSelected()
                        ? "Mang về"
                        : "Tại bàn";
                List<String> dsBan =
                        new ArrayList<>();

                dsBan.add(maBan);
                boolean taoHD =
                        hoaDonDAO.themHoaDonMoi(
                                maHD,
                                dsBan,
                                maNV,
                                maPhieuDatBan,
                                null,
                                hinhThucPhucVu,
                                "Chưa thanh toán"
                        );

                if (!taoHD) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Tạo hóa đơn thất bại."
                    );

                    return false;
                }
                hoaDonBanDAO.themBanVaoHoaDon(
                        maHD,
                        maBan
                );
            }

            // =================================================
            // LƯU / UPDATE MÓN
            // =================================================

            for (OrderItem item : gioHang.values()) {

                // =============================================
                // BỎ QUA MÓN CHỜ HỦY
                // =============================================

                if (
                        dsMonChoHuy.containsKey(
                                taoKeyGioHang(
                                        item.mon.getMaMon(),
                                        item.maBan
                                )
                        )
                ) {

                    continue;
                }

                // =============================================
                // LẤY CHI TIẾT MÓN THEO ĐÚNG BÀN
                // =============================================

                ChiTietHoaDon ctCu =
                        chiTietHoaDonDAO.getChiTietHoaDon(
                                maHD,
                                item.mon.getMaMon(),
                                item.maBan
                        );

                // =============================================
                // CHƯA CÓ -> INSERT
                // =============================================

                if (ctCu == null) {

                    ChiTietHoaDon ctMoi =
                            new ChiTietHoaDon(
                                    new HoaDon(maHD),

                                    new Ban(item.maBan),

                                    new MonAn(
                                            item.mon.getMaMon()
                                    ),

                                    item.soLuong,

                                    item.mon.getDonGia(),

                                    item.ghiChu == null
                                    ? ""
                                    : item.ghiChu,

                                    "Đã gửi bếp",

                                    null,

                                    0,

                                    null,

                                    LocalDateTime.now()
                            );

                    boolean ok =
                            chiTietHoaDonDAO.themChiTietHoaDon(ctMoi);

                    if (!ok) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Thêm món thất bại: "
                                + item.mon.getTenMon()
                        );

                        return false;
                    }
                }

                // =============================================
                // ĐÃ CÓ -> UPDATE
                // =============================================

                else {

                    boolean coThayDoi =
                            ctCu.getSoLuong() != item.soLuong
                            ||
                            !safe(
                                    ctCu.getGhiChu()
                            ).equalsIgnoreCase(
                                    safe(item.ghiChu)
                            );

                    if (!coThayDoi) {
                        continue;
                    }

                    ctCu.setSoLuong(item.soLuong);

                    ctCu.setDonGia(
                            item.mon.getDonGia()
                    );

                    ctCu.setGhiChu(
                            item.ghiChu == null
                            ? ""
                            : item.ghiChu
                    );


                    boolean ok =
                            chiTietHoaDonDAO.capNhatChiTietHoaDon(ctCu);

                    if (!ok) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Cập nhật món thất bại: "
                                + item.mon.getTenMon()
                        );

                        return false;
                    }
                }
            }

            // =================================================
            // HỦY MÓN
            // =================================================

            for (PendingHuyItem huy : dsMonChoHuy.values()) {

                ChiTietHoaDon ctCu =
                        chiTietHoaDonDAO.getChiTietHoaDon(
                                maHD,
                                huy.maMon,
                                huy.maBan
                        );

                if (ctCu == null) {
                    continue;
                }

                OrderItem itemConLai =
                        gioHang.get(
                                taoKeyGioHang(
                                        huy.maMon,
                                        huy.maBan
                                )
                        );

             // =============================================
             // HỦY HẾT
             // =============================================

             if (
                     itemConLai == null
                     ||
                     itemConLai.soLuong <= 0
             ) {

                 String dvt =
                         ctCu.getMaMon().getDonViTinh() == null
                         ? ""
                         : ctCu.getMaMon()
                               .getDonViTinh()
                               .trim()
                               .toLowerCase();

                 boolean laNuocUong =
                         dvt.contains("lon")
                         || dvt.contains("chai")
                         || dvt.contains("ly");

                 // =====================================
                 // NƯỚC UỐNG -> UPDATE SL
                 // =====================================

                 if (laNuocUong) {

                     int slMoi =
                             ctCu.getSoLuong()
                             - huy.soLuongHuy;

                     // ===== HẾT -> XÓA =====

                     if (slMoi <= 0) {

                         boolean ok =
                                 chiTietHoaDonDAO.huyMon(
                                         maHD,
                                         huy.maMon,
                                         huy.maBan,
                                         huy.lyDo,
                                         huy.soLuongHuy
                                 );

                         if (!ok) {

                             JOptionPane.showMessageDialog(
                                     this,
                                     "Hủy món thất bại."
                             );

                             return false;
                         }
                     }

                     // ===== CÒN -> UPDATE SỐ LƯỢNG =====

                     else {

                         ctCu.setSoLuong(slMoi);

                         ctCu.setLyDoHuy(
                                 huy.lyDo
                         );

                         ctCu.setSoLuongHuy(
                                 huy.soLuongHuy
                         );

                         ctCu.setThoiGianHuy(
                                 LocalDateTime.now()
                         );

                         boolean ok =
                                 chiTietHoaDonDAO
                                 .capNhatChiTietHoaDon(ctCu);
                         if (ok) {

                        	    String key =
                        	            taoKeyGioHang(
                        	                    ctCu.getMaMon().getMaMon(),
                        	                    ctCu.getMaBan().getMaBan()
                        	            );

                        	    // GIỮ NGUYÊN TRẠNG THÁI CŨ
                        	    trangThaiMap.put(
                        	            key,
                        	            ctCu.getTrangThai()
                        	    );
                        	}

                         if (!ok) {

                             JOptionPane.showMessageDialog(
                                     this,
                                     "Cập nhật số lượng thất bại."
                             );

                             return false;
                         }
                     }
                 }

                 // =====================================
                 // MÓN THƯỜNG -> HỦY CŨ
                 // =====================================

                 else {

                     boolean ok =
                             chiTietHoaDonDAO.huyMon(
                                     maHD,
                                     huy.maMon,
                                     huy.maBan,
                                     huy.lyDo,
                                     huy.soLuongHuy
                             );

                     if (!ok) {

                         JOptionPane.showMessageDialog(
                                 this,
                                 "Hủy món thất bại."
                         );

                         return false;
                     }
                 }
             }
                // =============================================
                // HỦY 1 PHẦN
                // =============================================

                else {

                    ctCu.setSoLuong(
                            itemConLai.soLuong
                    );

                    ctCu.setDonGia(
                            itemConLai.mon.getDonGia()
                    );

                    ctCu.setGhiChu(
                            itemConLai.ghiChu == null
                            ? ""
                            : itemConLai.ghiChu
                    );

                    ctCu.setLyDoHuy(
                            huy.lyDo
                    );

                    ctCu.setSoLuongHuy(
                            huy.soLuongHuy
                    );

                    ctCu.setThoiGianHuy(
                            LocalDateTime.now()
                    );


                    boolean ok =
                            chiTietHoaDonDAO.capNhatChiTietHoaDon(ctCu);

                    if (!ok) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Cập nhật món hủy thất bại."
                        );

                        return false;
                    }
                }
            }

            // =================================================
            // UPDATE TRẠNG THÁI TẤT CẢ BÀN
            // =================================================

            Set<String> dsBan =
                    new HashSet<>();

            for (OrderItem item : gioHang.values()) {

                dsBan.add(item.maBan);
            }

            for (String mb : dsBan) {

                banDAO.capNhatTrangThaiBan(
                        mb,
                        "Đang phục vụ"
                );
            }

            // =================================================
            // CẬP NHẬT PHIẾU ĐẶT
            // =================================================

            if (
                    maPhieuDatBan != null
                    &&
                    !maPhieuDatBan.trim().isEmpty()
            ) {

                try {

                    phieuDatBanDAO.capNhatTrangThai(
                            maPhieuDatBan,
                            "Đã nhận bàn"
                    );

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            // =================================================
            // RESET
            // =================================================

            maHoaDonHienTai = maHD;

            daGuiThucDon = true;

            coThayDoiChuaGui = false;

            dsMonChoHuy.clear();

            soLuongDaGuiMap.clear();

            for (OrderItem item : gioHang.values()) {

                soLuongDaGuiMap.put(
                        taoKeyGioHang(
                                item.mon.getMaMon(),
                                item.maBan
                        ),
                        item.soLuong
                );
            }

            // =================================================
            // QUAY LẠI
            // =================================================

            if (quayLaiSauKhiGui) {

                Window w =
                        SwingUtilities.getWindowAncestor(
                                Order_Mon_GUI.this
                        );

                if (w instanceof TrangChu_GUI) {

                    ((TrangChu_GUI) w).showCustomPage(
                            "Order_Ban_GUI",
                            new Order_Ban_GUI(
                                    taiKhoanDangNhap
                            )
                    );
                }
            }

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi gửi thực đơn."
            );

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

    private void loadMonDangPhucVuTheoBan() {

        try {

            // =========================================
            // TÌM HÓA ĐƠN THEO BÀN
            // =========================================

            HoaDon hdBan =
                    hoaDonDAO
                    .timHoaDonChuaThanhToanTheoBan(
                            maBan
                    );

            if(hdBan == null){
                return;
            }

            String maHD =
                    hdBan.getMaHD();

            if(maHD == null){
                return;
            }

            maHoaDonHienTai = maHD;

            // =========================================
            // LOAD TÊN BÀN GHÉP
            // =========================================

            try{

                List<String> dsBanChung =
                        hoaDonBanDAO.getDanhSachBanTheoHoaDon(
                                maHoaDonHienTai
                        );

                if(
                        dsBanChung != null
                        &&
                        dsBanChung.size() > 1
                ){

                    tenBanHienThi =
                            String.join(" + ", dsBanChung);

                    if(lblTenBan != null){
                        lblTenBan.setText(tenBanHienThi);
                    }

                }else{

                    tenBanHienThi = tenBan;

                    if(lblTenBan != null){
                        lblTenBan.setText(tenBanHienThi);
                    }
                }

            }catch(Exception ex){

                ex.printStackTrace();

                tenBanHienThi = tenBan;
            }

            daGuiThucDon = true;

            gioHang.clear();

            dsMonChoHuy.clear();

            soLuongDaGuiMap.clear();

            // =========================================
            // LOAD MÓN THEO ĐÚNG BÀN
            // =========================================

            List<ChiTietHoaDon> dsCT =
                    chiTietHoaDonDAO
                    .getChiTietTheoMaHDVaBan(
                            maHoaDonHienTai,
                            maBan
                    );

            // =========================================
            // LOAD GIỎ HÀNG
            // =========================================

            if (dsCT != null) {

                for (ChiTietHoaDon ct : dsCT) {

                    if (ct == null) continue;

                    if (ct.getMaMon() == null) continue;

                    if ("Đã hủy".equalsIgnoreCase(ct.getTrangThai())) {
                        continue;
                    }

                    if (ct.getSoLuong() <= 0) {
                        continue;
                    }

                    MonAn mon =
                            timMonTheoMaLocal(
                                    ct.getMaMon().getMaMon()
                            );

                    if (mon == null) {

                        mon =
                                monAnDAO.getMonAnTheoMa(
                                        ct.getMaMon().getMaMon()
                                );
                    }

                    if (mon == null) {

                        mon = new MonAn();

                        mon.setMaMon(
                                ct.getMaMon().getMaMon()
                        );

                        mon.setTenMon(
                                ct.getMaMon().getTenMon()
                        );
                    }

                    gioHang.put(
                            taoKeyGioHang(
                                    mon.getMaMon(),
                                    ct.getMaBan().getMaBan()
                            ),

                            new OrderItem(
                                    ct.getMaBan().getMaBan(),
                                    mon,
                                    ct.getSoLuong(),
                                    ct.getGhiChu()
                            )
                    );

                    soLuongDaGuiMap.put(
                            taoKeyGioHang(
                                    mon.getMaMon(),
                                    ct.getMaBan().getMaBan()
                            ),
                            ct.getSoLuong()
                    );
                }
            }

            coThayDoiChuaGui = false;

            renderOrderList();
            capNhatManHinhKhach();

            capNhatTrangThaiNutTheoGuiMon();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được món đang phục vụ."
            );
        }
    }
    private MonAn timMonTheoMaLocal(String maMon) {
        if (maMon == null || maMon.trim().isEmpty()) {
            return null;
        }
        for (MonAn mon : dsMon) {
            if (mon != null &&
                maMon.equalsIgnoreCase(mon.getMaMon())) {
                return mon;
            }
        }
        try {
            return monAnDAO.getMonAnTheoMa(maMon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JLabel createHeaderCell(String text, int width, boolean rightBorder) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(RIGHT_HEADER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setPreferredSize(new Dimension(width, HEADER_H));
        lbl.setMinimumSize(new Dimension(width, HEADER_H));
        lbl.setMaximumSize(new Dimension(width, HEADER_H));
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

    private void themMonVaoGio(
            MonAn mon,
            String maBanItem
    ) {
        String key =
                taoKeyGioHang(
                        mon.getMaMon(),
                        maBanItem
                );

        OrderItem item = gioHang.get(key);

        if(item == null){
            gioHang.put(
                    key,
                    new OrderItem(maBanItem,mon,1,""
                    )
            );

        }else{

            item.soLuong++;
        }

        renderOrderList();
        capNhatManHinhKhach();

        capNhatCoThayDoiChuaGui();
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
    private String taoKeyGioHang(String maMon, String maBan) {
        return maMon + "_" + maBan;
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
                    "<html><div style='width:190px;'>"
                    + safe(mon.getTenMon())
                    + "</div></html>"
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
                	themMonVaoGio(mon, maBan);
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
            setMaximumSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            setPreferredSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            setMinimumSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            setBackground(new Color(246, 246, 246));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setPreferredSize(new Dimension(RIGHT_W, ORDER_ROW_H));

            JPanel colTen = new JPanel(new BorderLayout(6, 0));
            colTen.setOpaque(false);
            colTen.setPreferredSize(new Dimension(COL_NAME_W, ORDER_ROW_H));
            colTen.setMinimumSize(new Dimension(COL_NAME_W, ORDER_ROW_H));
            colTen.setMaximumSize(new Dimension(COL_NAME_W, ORDER_ROW_H));
            colTen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                    new EmptyBorder(5, 8, 5, 8)
            ));

            JLabel lblXoa = new JLabel("🗑", SwingConstants.CENTER);
            final boolean[] dangMoPopupHuy = {false};
            lblXoa.setFont(new Font("SansSerif", Font.PLAIN, 17));
            lblXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblXoa.setPreferredSize(new Dimension(28, ORDER_ROW_H - 10));

            JPanel tenNotePanel = new JPanel();
            tenNotePanel.setOpaque(false);
            tenNotePanel.setLayout(new BoxLayout(tenNotePanel, BoxLayout.Y_AXIS));

            JLabel lblTen = new JLabel(
                    String.format(
                            "<html>"
                            + "<table width='180'>"
                            + "<tr>"
                            + "<td>%s</td>"
                            + "<td align='right' width='25'>%s</td>"
                            + "</tr>"
                            + "</table>"
                            + "</html>",
                            safe(item.mon.getTenMon()),
                            getTrangThaiIcon()
                    )
            );
            lblTen.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblGhiChu = new JTextArea(getGhiChuDisplayText());
            lblGhiChu.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblGhiChu.setForeground(Color.GRAY);
            lblGhiChu.setOpaque(false);
            lblGhiChu.setEditable(false);
            lblGhiChu.setLineWrap(false);
            lblGhiChu.setBorder(new EmptyBorder(1, 0, 0, 0));
            lblGhiChu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblGhiChu.setFocusable(false);
            lblGhiChu.setMaximumSize(new Dimension(200, 18));
            lblGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);

            tenNotePanel.add(Box.createVerticalGlue());
            tenNotePanel.add(lblTen);
            tenNotePanel.add(Box.createVerticalStrut(2));
            tenNotePanel.add(lblGhiChu);
            tenNotePanel.add(Box.createVerticalGlue());

            colTen.add(lblXoa, BorderLayout.WEST);
            colTen.add(tenNotePanel, BorderLayout.CENTER);

            JPanel colGia = new JPanel(new BorderLayout());
            colGia.setOpaque(false);
            colGia.setPreferredSize(new Dimension(COL_PRICE_W, ORDER_ROW_H));
            colGia.setMinimumSize(new Dimension(COL_PRICE_W, ORDER_ROW_H));
            colGia.setMaximumSize(new Dimension(COL_PRICE_W, ORDER_ROW_H));
            colGia.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            JLabel lblGia = new JLabel(formatTien(item.mon.getDonGia()), SwingConstants.CENTER);
            lblGia.setFont(new Font("SansSerif", Font.PLAIN, 14));
            colGia.add(lblGia, BorderLayout.CENTER);

            JPanel colSL = new JPanel(new GridBagLayout());
            colSL.setOpaque(false);
            colSL.setPreferredSize(new Dimension(COL_QTY_W, ORDER_ROW_H));
            colSL.setMinimumSize(new Dimension(COL_QTY_W, ORDER_ROW_H));
            colSL.setMaximumSize(new Dimension(COL_QTY_W, ORDER_ROW_H));
            colSL.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

            int slDaGui = soLuongDaGuiMap.getOrDefault(
                    taoKeyGioHang(
                            item.mon.getMaMon(),
                            item.maBan
                    ),
                    0
            );

            JPanel qtyBox = new JPanel(new BorderLayout());
            qtyBox.setPreferredSize(new Dimension(72, 30));
            qtyBox.setBackground(Color.WHITE);
            qtyBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JTextField txtSL = new JTextField(String.valueOf(item.soLuong));
            txtSL.setHorizontalAlignment(SwingConstants.CENTER);
            txtSL.setFont(new Font("SansSerif", Font.BOLD, 14));
            txtSL.setBorder(null);

            JPanel pnArrow = new JPanel(new GridLayout(2, 1, 0, 0));
            pnArrow.setPreferredSize(new Dimension(22, 30));

            JButton btnUp = new JButton("▲");
            JButton btnDown = new JButton("▼");

            btnUp.setFont(new Font("SansSerif", Font.PLAIN, 8));
            btnDown.setFont(new Font("SansSerif", Font.PLAIN, 8));

            btnUp.setMargin(new Insets(0, 0, 0, 0));
            btnDown.setMargin(new Insets(0, 0, 0, 0));

            btnUp.setFocusPainted(false);
            btnDown.setFocusPainted(false);

            btnUp.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.GRAY));
            btnDown.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));

            btnUp.addActionListener(e -> {
                item.soLuong++;
                renderOrderList();
                capNhatManHinhKhach();
                capNhatCoThayDoiChuaGui();
            });

            btnDown.addActionListener(e -> {
            	int slDaGuiNow = soLuongDaGuiMap.getOrDefault(
            	        taoKeyGioHang(
            	                item.mon.getMaMon(),
            	                item.maBan
            	        ),
            	        0
            	);

                if (slDaGuiNow > 0 && item.soLuong - 1 < slDaGuiNow) {
                    moPopupHuyMonTam(lblXoa, item, 1);
                    return;
                }

                item.soLuong--;

                if (item.soLuong <= 0) {
                	gioHang.remove(
                	        taoKeyGioHang(
                	                item.mon.getMaMon(),
                	                item.maBan
                	        )
                	);
                }

                renderOrderList();
                capNhatManHinhKhach();
                capNhatCoThayDoiChuaGui();
            });

            pnArrow.add(btnUp);
            pnArrow.add(btnDown);

            qtyBox.add(txtSL, BorderLayout.CENTER);
            qtyBox.add(pnArrow, BorderLayout.EAST);

            colSL.add(qtyBox);
            txtSL.addActionListener(e -> capNhatSoLuongTuNhap(txtSL, item));

            txtSL.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        if (txtSL.isDisplayable()) {
                            capNhatSoLuongTuNhap(txtSL, item);
                        }
                    });
                }
            });

            
            JPanel colTotal = new JPanel(new BorderLayout());
            colTotal.setOpaque(false);
            colTotal.setPreferredSize(new Dimension(COL_TOTAL_W, ORDER_ROW_H));
            colTotal.setMinimumSize(new Dimension(COL_TOTAL_W, ORDER_ROW_H));
            colTotal.setMaximumSize(new Dimension(COL_TOTAL_W, ORDER_ROW_H));

            JLabel lblTotal = new JLabel(formatTien(item.soLuong * item.mon.getDonGia()), SwingConstants.CENTER);
            lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
            colTotal.add(lblTotal, BorderLayout.CENTER);

            lblXoa.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    try {
                    	int slDaGuiNow =
                                soLuongDaGuiMap.getOrDefault(
                                        taoKeyGioHang(
                                                item.mon.getMaMon(),
                                                item.maBan
                                        ),
                                        0
                                );

                        // ===== CHƯA GỬI =====

                        if (slDaGuiNow <= 0) {

                        	gioHang.remove(
                        	        taoKeyGioHang(
                        	                item.mon.getMaMon(),
                        	                item.maBan
                        	        )
                        	);

                            renderOrderList();
                            capNhatManHinhKhach();

                            capNhatCoThayDoiChuaGui();

                            return;
                        }

                        // ===== ĐÃ GỬI -> CHECK TRẠNG THÁI =====

                        ChiTietHoaDon ct =
                                chiTietHoaDonDAO.getChiTietHoaDon(
                                        maHoaDonHienTai,
                                        item.mon.getMaMon(),
                                        item.maBan
                                );

                        if (ct != null) {

                            String trangThai =
                                    ct.getTrangThai();

                         // ===== CHECK ĐƠN VỊ TÍNH =====

                            String dvt =
                                    item.mon.getDonViTinh() == null
                                    ? ""
                                    : item.mon.getDonViTinh().trim().toLowerCase();

                            boolean laNuocUong =
                                    dvt.contains("lon")
                                    || dvt.contains("chai")
                                    || dvt.contains("chai lớn")
                                    || dvt.contains("chai nhỏ");

                            // ===== KHÔNG CHO HỦY =====

                            if (
                                    !laNuocUong
                                    &&
                                    (
                                        "Đang chế biến".equalsIgnoreCase(trangThai)
                                        ||
                                        "Hoàn thành".equalsIgnoreCase(trangThai)
                                    )
                            ) {

                                JOptionPane.showMessageDialog(
                                        Order_Mon_GUI.this,

                                        "Món đang chế biến hoặc đã hoàn thành.\n"
                                        + "Không được phép hủy."
                                );

                                return;
                            }
                        }

                        // ===== ĐANG PHỤC VỤ -> CHO HỦY =====

                        moPopupHuyMonTam(
                                lblXoa,
                                item,
                                slDaGuiNow
                        );

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        JOptionPane.showMessageDialog(
                                Order_Mon_GUI.this,
                                "Lỗi kiểm tra trạng thái món."
                        );
                    }
                }
            });

            lblGhiChu.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showNotePopup();
                }
            });

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
        private String getTrangThaiIcon() {

            try {

                if(maHoaDonHienTai == null){
                    return "";
                }

                String key =
                        taoKeyGioHang(
                                item.mon.getMaMon(),
                                item.maBan
                        );

                String trangThai =
                        trangThaiMap.get(key);

                if(trangThai == null){

                    ChiTietHoaDon ct =
                            chiTietHoaDonDAO.getChiTietHoaDon(
                                    maHoaDonHienTai,
                                    item.mon.getMaMon(),
                                    item.maBan
                            );

                    if(ct == null || ct.getTrangThai() == null){
                        return "";
                    }

                    trangThai = ct.getTrangThai();

                    trangThaiMap.put(key, trangThai);
                }

                if("Đang phục vụ".equalsIgnoreCase(trangThai)){
                    return " 🟡";
                }

                if("Đang chế biến".equalsIgnoreCase(trangThai)){
                    return " 🍳";
                }

                if("Hoàn thành".equalsIgnoreCase(trangThai)){
                    return " ✅";
                }

            }catch(Exception e){

                e.printStackTrace();
            }

            return "";
        }
        private void moPopupHuyTuSoLuong(JLabel lblXoa, JSpinner spinnerSL, OrderItem item, int slCanHuy) {
            if (lblXoa == null || !lblXoa.isShowing()) {
                return;
            }

            spinnerSL.setValue(
                    soLuongDaGuiMap.getOrDefault(
                            taoKeyGioHang(
                                    item.mon.getMaMon(),
                                    item.maBan
                            ),
                            item.soLuong
                    )
            );

            SwingUtilities.invokeLater(() -> {
                moPopupHuyMonTam(lblXoa, item, slCanHuy);
            });
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
                capNhatCoThayDoiChuaGui();
            }

            if (popupGhiChu != null) {
                popupGhiChu.setVisible(false);
                popupGhiChu.removeAll();
            }
        }
    }
    static class PendingHuyItem {

        String maMon;
        String maBan;

        int soLuongHuy;

        String lyDo;

        PendingHuyItem(
                String maMon,
                String maBan,
                int soLuongHuy,
                String lyDo
        ) {

            this.maMon = maMon;
            this.maBan = maBan;

            this.soLuongHuy = soLuongHuy;

            this.lyDo = lyDo;
        }
    }

    static class OrderItem {
        String maBan;
        MonAn mon;
        int soLuong;
        String ghiChu;
        public OrderItem(
                String maBan,
                MonAn mon,
                int soLuong,
                String ghiChu
        ) {
            this.maBan = maBan;
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

    private void moPopupHuyMonTam(Component parent, OrderItem item) {
        moPopupHuyMonTam(parent, item, 1);
    }

    private void moPopupHuyMonTam(Component parent, OrderItem item, int soLuongMacDinh) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1));

        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));
        root.setPreferredSize(new Dimension(300, 235));

        JPanel qtyPanel = new JPanel(new BorderLayout());
        qtyPanel.setOpaque(false);

        JButton btnMinus = new JButton("⊖");
        JButton btnPlus = new JButton("⊕");

        int slMax = Math.max(1, item.soLuong);
        int slMacDinh = Math.max(1, Math.min(soLuongMacDinh, slMax));

        JLabel lblSL = new JLabel(String.valueOf(slMacDinh), SwingConstants.CENTER);

        btnMinus.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnPlus.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblSL.setFont(new Font("SansSerif", Font.BOLD, 20));

        btnMinus.setPreferredSize(new Dimension(58, 38));
        btnPlus.setPreferredSize(new Dimension(58, 38));

        lblSL.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY));
        btnMinus.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnPlus.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        qtyPanel.add(btnMinus, BorderLayout.WEST);
        qtyPanel.add(lblSL, BorderLayout.CENTER);
        qtyPanel.add(btnPlus, BorderLayout.EAST);

        final int[] soLuongHuy = {slMacDinh};

        btnMinus.addActionListener(e -> {
            if (soLuongHuy[0] > 1) {
                soLuongHuy[0]--;
                lblSL.setText(String.valueOf(soLuongHuy[0]));
            }
        });

        btnPlus.addActionListener(e -> {
            if (soLuongHuy[0] < slMax) {
                soLuongHuy[0]++;
                lblSL.setText(String.valueOf(soLuongHuy[0]));
            }
        });

        JPanel reasonTabs = new JPanel(new GridLayout(1, 2, 8, 0));
        reasonTabs.setOpaque(false);

        JButton btnLyDo1 = new JButton("Khách yêu cầu hủy");
        JButton btnLyDo2 = new JButton("Thao tác sai");

        Color mauGoiY = new Color(220, 220, 220);

        for (JButton b : new JButton[]{btnLyDo1, btnLyDo2}) {
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            b.setBackground(mauGoiY);
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setFocusPainted(false);
        }

        reasonTabs.add(btnLyDo1);
        reasonTabs.add(btnLyDo2);

        JTextArea txtLyDo = new JTextArea("Lý do");
        txtLyDo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtLyDo.setForeground(Color.GRAY);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(new EmptyBorder(8, 10, 8, 10));

        txtLyDo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if ("Lý do".equals(txtLyDo.getText())) {
                    txtLyDo.setText("");
                    txtLyDo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtLyDo.getText().trim().isEmpty()) {
                    txtLyDo.setText("Lý do");
                    txtLyDo.setForeground(Color.GRAY);
                }
            }
        });

        btnLyDo1.addActionListener(e -> {
            txtLyDo.setText("Khách yêu cầu hủy");
            txtLyDo.setForeground(Color.BLACK);
        });

        btnLyDo2.addActionListener(e -> {
            txtLyDo.setText("Thao tác sai");
            txtLyDo.setForeground(Color.BLACK);
        });

        JScrollPane spLyDo = new JScrollPane(txtLyDo);
        spLyDo.setPreferredSize(new Dimension(270, 70));
        spLyDo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton btnDongY = new JButton("Đồng ý");
        btnDongY.setFont(new Font("SansSerif", Font.BOLD, 24));
        btnDongY.setBackground(new Color(188, 222, 242));
        btnDongY.setOpaque(true);
        btnDongY.setContentAreaFilled(true);
        btnDongY.setFocusPainted(false);
        btnDongY.setPreferredSize(new Dimension(270, 44));

        btnDongY.addActionListener(e -> {
            String lyDo = txtLyDo.getText().trim();

            if (lyDo.isEmpty() || "Lý do".equalsIgnoreCase(lyDo)) {
            	txtLyDo.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(
                                        Color.RED,
                                        3
                                ),
                                BorderFactory.createEmptyBorder(
                                        4, 6, 4, 6
                                )
                        )
                );

                txtLyDo.revalidate();
                txtLyDo.repaint();

                txtLyDo.requestFocusInWindow();
                return;
            }
            txtLyDo.getDocument().addDocumentListener(
                    new DocumentListener() {

                        private void resetBorder() {

                            if (!txtLyDo.getText().trim().isEmpty()) {

                                txtLyDo.setBorder(
                                        BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(
                                                        Color.GRAY,
                                                        1
                                                ),
                                                BorderFactory.createEmptyBorder(
                                                        4, 6, 4, 6
                                                )
                                        )
                                );

                                txtLyDo.revalidate();
                                txtLyDo.repaint();
                            }
                        }

                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            resetBorder();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            resetBorder();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            resetBorder();
                        }
                    }
            );

            int slHuy = soLuongHuy[0];
            int slDaGui =
                    soLuongDaGuiMap.getOrDefault(
                            taoKeyGioHang(
                                    item.mon.getMaMon(),
                                    item.maBan
                            ),
                            item.soLuong
                    );

            dsMonChoHuy.put(
                    taoKeyGioHang(
                            item.mon.getMaMon(),
                            item.maBan
                    ),
                    new PendingHuyItem(
                            item.mon.getMaMon(),
                            item.maBan,
                            slHuy,
                            lyDo
                    )
            );

            item.soLuong = Math.max(0, slDaGui - slHuy);

            if (item.soLuong <= 0) {
            	gioHang.remove(
            	        taoKeyGioHang(
            	                item.mon.getMaMon(),
            	                item.maBan
            	        )
            	);
            }

            renderOrderList();
            capNhatManHinhKhach();
            capNhatCoThayDoiChuaGui();
            popup.setVisible(false);
        });

        root.add(qtyPanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(reasonTabs, BorderLayout.NORTH);
        center.add(spLyDo, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(btnDongY, BorderLayout.SOUTH);

        popup.add(root);
        if (parent != null && parent.isShowing()) {
            popup.show(parent, -10, parent.getHeight() + 6);
        }
    }
    private void loadMonDatTheoPhieu(String maPhieuDatBan) {

        try {

            gioHang.clear();

            dsMonChoHuy.clear();

            soLuongDaGuiMap.clear();

            List<ChiTietDatMon> ds;

         // ======================================
         // ORDER RIÊNG
         // ======================================

         if(laOrderRieng){

             ds =
                     chiTietDatMonDAO
                     .getDanhSachTheoPhieuVaBan(
                             maPhieuDatBan,
                             maBan
                     );
         }

         // ======================================
         // ORDER CHUNG
         // ======================================

         else{

             // CHỈ BÀN ĐẦU load món
             String maBanLoad =
                     hoaDonBanDAO
                     .layBanDauTienCuaHoaDonTheoPhieu(
                             maPhieuDatBan
                     );

             if(
                     maBanLoad != null
                     &&
                     maBan.equalsIgnoreCase(maBanLoad)
             ){

                 ds =
                         chiTietDatMonDAO
                         .getDanhSachTheoMaPhieuDatBan(
                                 maPhieuDatBan
                         );

             }else{

                 ds = new ArrayList<>();
             }
         }

            if (ds != null) {

                for (ChiTietDatMon pdm : ds) {

                    if (pdm == null || pdm.getMon() == null)
                        continue;

                    MonAn mon =
                            timMonTheoMaLocal(
                                    pdm.getMon().getMaMon()
                            );

                    if (mon == null) {

                        mon =
                                monAnDAO.getMonAnTheoMa(
                                        pdm.getMon().getMaMon()
                                );
                    }

                    if (mon == null) {

                        mon = new MonAn();

                        mon.setMaMon(
                                pdm.getMon().getMaMon()
                        );

                        mon.setTenMon(
                                pdm.getMon().getTenMon()
                        );
                    }

                    gioHang.put(
                            taoKeyGioHang(
                                    mon.getMaMon(),
                                    maBan
                            ),

                            new OrderItem(
                                    maBan,
                                    mon,
                                    pdm.getSoLuong(),
                                    pdm.getGhiChu() == null
                                            ? ""
                                            : pdm.getGhiChu()
                            )
                    );
                }
            }

            daGuiThucDon = true;

            coThayDoiChuaGui = false;
         // ======================================
         // ĐÃ CÓ MÓN TRONG HD
         // -> KHÔNG LOAD LẠI PHIẾU
         // ======================================

         String maHDCheck =
                 hoaDonBanDAO.timMaHDTheoBan(maBan);

         if(maHDCheck != null){

             List<ChiTietHoaDon> dsCheck =
                     chiTietHoaDonDAO
                     .getChiTietTheoMaHDVaBan(
                             maHDCheck,
                             maBan
                     );

             if(dsCheck != null && !dsCheck.isEmpty()){

                 loadMonDangPhucVuTheoBan();
                 return;
             }
         }
         renderOrderList();
         capNhatManHinhKhach();

         capNhatTrangThaiNutTheoGuiMon();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được món đặt trước."
            );
        }
    }
    private void capNhatSoLuongTuNhap(JTextField txtSL, OrderItem item) {
        try {
            int slMoi = Integer.parseInt(txtSL.getText().trim());

            if (slMoi <= 0) {
            	gioHang.remove(
            	        taoKeyGioHang(
            	                item.mon.getMaMon(),
            	                item.maBan
            	        )
            	);
                renderOrderList();
                capNhatManHinhKhach();
                capNhatCoThayDoiChuaGui();
                return;
            }

            int slDaGui =
                    soLuongDaGuiMap.getOrDefault(
                            taoKeyGioHang(
                                    item.mon.getMaMon(),
                                    item.maBan
                            ),
                            0
                    );

            // Nếu nhập nhỏ hơn số lượng đã gửi -> mở popup hủy
            if (slDaGui > 0 && slMoi < slDaGui) {
                int soLuongCanHuy = slDaGui - slMoi;

                txtSL.setText(String.valueOf(item.soLuong));

                SwingUtilities.invokeLater(() -> {
                    moPopupHuyMonTam(txtSL, item, soLuongCanHuy);
                });

                return;
            }

            item.soLuong = slMoi;
            renderOrderList();
            capNhatManHinhKhach();
            capNhatCoThayDoiChuaGui();

        } catch (Exception ex) {
            txtSL.setText(String.valueOf(item.soLuong));
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên hợp lệ.");
        }
        
    }
    public void capNhatManHinhKhach() {

        manHinhKhach =
                Pn_ThanhMenu.getManHinhKhach();

        if (manHinhKhach == null) {
            return;
        }

        double tongTien = 0;

        for (OrderItem item : gioHang.values()) {

            tongTien +=
                    item.soLuong
                    * item.mon.getDonGia();
        }

        manHinhKhach.capNhatHoaDon(
                tenBanHienThi,
                new LinkedHashMap<>(gioHang),
                formatTien(tongTien) + "đ"
        );

        manHinhKhach.revalidate();
        manHinhKhach.repaint();
    }
    public void setManHinhKhach(ManHinhKhach_GUI manHinhKhach) {
        this.manHinhKhach = manHinhKhach;
    }
}