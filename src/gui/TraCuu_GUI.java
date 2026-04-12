package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.toedter.calendar.JDateChooser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dao.Ban_DAO;
import dao.KhachHang_DAO;
import dao.KhuVuc_DAO;
import dao.LoaiMonAn_DAO;
import dao.MonAn_DAO;
import entity.KhachHang;
import entity.KhuVuc;
import entity.LoaiMonAn;
import entity.MonAn;
import entity.TaiKhoan;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;

public class TraCuu_GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;

    private JPanel contentPane;
    private JPanel pnlCards;
    private CardLayout cardLayout;

    // ===== TAB MÓN ĂN =====
    private HintTextField txtTimKiemMon;
    private RoundedComboBox cboGiaTu;
    private RoundedComboBox cboGiaDen;
    private JTextField txtGiaTu;
    private JTextField txtGiaDen;

    private JButton btnTim;
    private JButton btnLamMoi;
    private JPanel pnlTabsLoaiMon;

    private JPanel pnlDanhSachMon;
    private JScrollPane scrDanhSachMon;

    private final MonAn_DAO monAnDAO = new MonAn_DAO();
    private final LoaiMonAn_DAO loaiMonAnDAO = new LoaiMonAn_DAO();

    private final List<MonAn> dsTatCaMon = new ArrayList<>();
    private final List<LoaiMonAn> dsLoaiMon = new ArrayList<>();
    private final List<MonAn> dsDangHienThi = new ArrayList<>();

    private String maLoaiDangChon = "ALL";
    private MonRowPanel currentExpandedRow;

    // ===== TAB BÀN =====
    private HintTextField txtTimKiemBan;
    private JButton btnTimBan;
    private JButton btnHomNay;
    private JButton btnPrevNgay;
    private JButton btnNextNgay;
    private JDateChooser dcNgayTimBan;

    private JPanel pnlTabsBan;
    private JTableCustomBan tblBan;
    private DefaultTableModel modelBan;

    private final Ban_DAO banDAO = new Ban_DAO();
    private final KhuVuc_DAO khuVucDAO = new KhuVuc_DAO();

    private final List<String[]> dsTatCaBanTheoNgay = new ArrayList<>();
    private String filterTrangThaiBan = "Tất cả";
    private String filterKhuVucBan = "Tất cả";

    // ===== TAB KHÁCH HÀNG =====
    private HintTextField txtTimKiemKH;
    private JButton btnTimKH;
    private JPanel pnlDanhSachKH;
    private JScrollPane scrDanhSachKH;
    private KhachHangRowPanel currentExpandedKhachHangRow;
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private final DecimalFormat df = new DecimalFormat("#,##0");
    private final SimpleDateFormat sdfNgay = new SimpleDateFormat("dd 'Tháng' MM yyyy");

    private static final int COL_ICON = 34;
    private static final int COL_MA = 108;
    private static final int COL_GIA = 132;
    private static final int ROW_HEIGHT = 38;
    private static final int DETAIL_HEIGHT = 250;

    private static final int KH_COL_MA = 180;
    private static final int KH_COL_TONG = 150;
    private static final int KH_ROW_HEIGHT = 38;
    private static final int KH_DETAIL_HEIGHT = 290;

    private final String[] GOI_Y_GIA = {
            "",
            "30000",
            "50000",
            "70000",
            "100000",
            "150000",
            "200000",
            "300000",
            "500000"
    };

    private final Color BG_FRAME = new Color(243, 243, 243);
    private final Color BG_LEFT = new Color(221, 221, 221);
    private final Color BG_LEFT_SELECTED = new Color(206, 206, 206);

    private final Color BG_SEARCH = Color.WHITE;
    private final Color BG_TAB = new Color(244, 238, 229);
    private final Color BG_TAB_SELECTED = new Color(231, 213, 183);

    private final Color BG_WHITE = Color.WHITE;
    private final Color BG_ROW_HOVER = new Color(248, 248, 248);
    private final Color BG_DETAIL_HEADER = new Color(191, 217, 239);

    private final Color BORDER = new Color(187, 187, 187);
    private final Color GRID = new Color(212, 212, 212);
    private final Color COLUMN_LINE = new Color(205, 205, 205);
    private final Color TEXT = new Color(28, 28, 28);
    private final Color HINT = new Color(158, 158, 158);
    private final Color BTN_TIM = new Color(0xB7, 0xD0, 0xB7);
    private final Color BTN_NGAY = new Color(183, 209, 232);
    private final Color ARROW_COLOR = new Color(120, 120, 120);
    // ===== TAB KHUYẾN MÃI =====
    private HintTextField txtTimKiemKM;
    private JButton btnTimKM;
    private JButton btnLamMoiKM;
    private JDateChooser dcTuNgayKM;
    private JDateChooser dcDenNgayKM;

    private JPanel pnlTabsKM;
    private JPanel pnlDanhSachKM;
    private JScrollPane scrDanhSachKM;

    private final KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
    private final List<KhuyenMai> dsTatCaKM = new ArrayList<>();
    private final List<KhuyenMai> dsDangHienThiKM = new ArrayList<>();

    private KhuyenMaiRowPanel currentExpandedKhuyenMaiRow;
    private String filterTrangThaiKM = "Tất cả";

    private static final int KM_COL_MA = 150;
    private static final int KM_COL_GIA_TRI = 170;
    private static final int KM_COL_NGAY = 210;
    private static final int KM_COL_TRANG_THAI = 150;
    private static final int KM_ROW_HEIGHT = 40;
    private static final int KM_DETAIL_HEIGHT = 360;

    private final DateTimeFormatter dtfKM = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");

    private final String SEARCH_ICON_PATH = "img/mn_tracuu.png";

    public TraCuu_GUI() {
        this(null);
    }

    public TraCuu_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        initUI();
        napDuLieuBanDau();
    }

    private void initUI() {
        setTitle("Tra cứu");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1250, 740));
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_FRAME);
        setContentPane(contentPane);

        Pn_ThanhMenu pnThanhMenu = new Pn_ThanhMenu(taiKhoanDangNhap);
        pnThanhMenu.setPreferredSize(new Dimension(100, 42));
        contentPane.add(pnThanhMenu, BorderLayout.NORTH);

        JPanel pnlBody = new JPanel(new BorderLayout());
        pnlBody.setBackground(BG_FRAME);
        contentPane.add(pnlBody, BorderLayout.CENTER);

        pnlBody.add(createLeftMenu(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setOpaque(false);
        pnlCards.setBorder(new EmptyBorder(18, 0, 0, 0));

        pnlCards.add(createPanelTimMon(), "MON_AN");
        pnlCards.add(createPanelTimBan(), "BAN");
        pnlCards.add(createPanelTimKhachHang(), "KHACH_HANG");
        pnlCards.add(createPanelTimKhuyenMai(), "KHUYEN_MAI");

        pnlBody.add(pnlCards, BorderLayout.CENTER);
        cardLayout.show(pnlCards, "MON_AN");
    }

    private JPanel createLeftMenu() {
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBackground(BG_LEFT);
        pnlLeft.setPreferredSize(new Dimension(250, 100));
        pnlLeft.setMinimumSize(new Dimension(230, 100));

        ButtonGroup group = new ButtonGroup();

        JToggleButton btnMon = createLeftButton("Tìm kiếm món ăn", true);
        JToggleButton btnBan = createLeftButton("Tìm kiếm bàn", false);
        JToggleButton btnKhach = createLeftButton("Tìm kiếm khách hàng", false);
        JToggleButton btnKM = createLeftButton("Tìm kiếm khuyến mãi", false);

        group.add(btnMon);
        group.add(btnBan);
        group.add(btnKhach);
        group.add(btnKM);

        btnMon.addActionListener(e -> {
            btnMon.setSelected(true);
            cardLayout.show(pnlCards, "MON_AN");
            pnlLeft.repaint();
        });

        btnBan.addActionListener(e -> {
            btnBan.setSelected(true);
            cardLayout.show(pnlCards, "BAN");
            pnlLeft.repaint();
        });

        btnKhach.addActionListener(e -> {
            btnKhach.setSelected(true);
            cardLayout.show(pnlCards, "KHACH_HANG");
            pnlLeft.repaint();
        });

        btnKM.addActionListener(e -> {
            btnKM.setSelected(true);
            cardLayout.show(pnlCards, "KHUYEN_MAI");
            pnlLeft.repaint();
        });

        pnlLeft.add(btnMon);
        pnlLeft.add(btnBan);
        pnlLeft.add(btnKhach);
        pnlLeft.add(btnKM);
        pnlLeft.add(Box.createVerticalGlue());

        return pnlLeft;
    }

    private JToggleButton createLeftButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text, selected);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btn.setForeground(Color.BLACK);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        btn.setPreferredSize(new Dimension(250, 104));
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(236, 236, 236)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 26, 0, 10));

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);

        btn.setBackground(selected ? BG_LEFT_SELECTED : BG_LEFT);

        btn.addChangeListener(e -> {
            btn.setBackground(btn.isSelected() ? BG_LEFT_SELECTED : BG_LEFT);
            btn.repaint();
        });

        return btn;
    }

    // ========================= MÓN ĂN =========================

    private JPanel createPanelTimMon() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(0, 6, 8, 12));

        root.add(createHeaderSearchPanel(), BorderLayout.NORTH);
        root.add(createMainMonPanel(), BorderLayout.CENTER);

        return root;
    }

    private JPanel createHeaderSearchPanel() {
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 12, 10, 12));

        JLabel lblTitle = new JLabel("Tra cứu");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblTitle.setForeground(Color.BLACK);

        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setOpaque(false);

        JPanel pnlSearchCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlSearchCenter.setOpaque(false);

        JPanel pnlSearchBox = new RoundedPanel(18, BG_SEARCH);
        pnlSearchBox.setLayout(new BorderLayout(8, 0));
        pnlSearchBox.setPreferredSize(new Dimension(520, 44));
        pnlSearchBox.setBorder(new EmptyBorder(0, 14, 0, 12));

        txtTimKiemMon = new HintTextField("Nhập mã/tên món cần tìm...");
        txtTimKiemMon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtTimKiemMon.setBorder(null);
        txtTimKiemMon.setOpaque(false);
        txtTimKiemMon.setForeground(TEXT);

        JLabel lblIconSearch = new JLabel();
        lblIconSearch.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconSearch.setPreferredSize(new Dimension(22, 22));

        ImageIcon iconSearch = loadIcon(SEARCH_ICON_PATH, 16, 16);
        if (iconSearch != null) {
            lblIconSearch.setIcon(iconSearch);
        } else {
            lblIconSearch.setText("⌕");
            lblIconSearch.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblIconSearch.setForeground(new Color(90, 90, 90));
        }

        pnlSearchBox.add(txtTimKiemMon, BorderLayout.CENTER);
        pnlSearchBox.add(lblIconSearch, BorderLayout.EAST);
        pnlSearchCenter.add(pnlSearchBox);

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilterRight.setOpaque(false);

        JLabel lblLocGia = new JLabel("Lọc theo giá:");
        lblLocGia.setFont(new Font("SansSerif", Font.PLAIN, 17));

        JLabel lblTu = new JLabel("Từ");
        lblTu.setFont(new Font("SansSerif", Font.PLAIN, 17));

        cboGiaTu = createGiaCombo();
        cboGiaDen = createGiaCombo();

        txtGiaTu = (JTextField) cboGiaTu.getEditor().getEditorComponent();
        txtGiaDen = (JTextField) cboGiaDen.getEditor().getEditorComponent();

        styleComboEditor(txtGiaTu);
        styleComboEditor(txtGiaDen);

        JLabel lblDen = new JLabel("Đến");
        lblDen.setFont(new Font("SansSerif", Font.PLAIN, 17));

        btnTim = new JButton("Tìm");
        btnTim.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnTim.setPreferredSize(new Dimension(82, 40));
        btnTim.setFocusPainted(false);
        btnTim.setBackground(BTN_TIM);
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLamMoi.setPreferredSize(new Dimension(105, 40));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setBackground(BG_TAB);
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlFilterRight.add(lblLocGia);
        pnlFilterRight.add(lblTu);
        pnlFilterRight.add(cboGiaTu);
        pnlFilterRight.add(lblDen);
        pnlFilterRight.add(cboGiaDen);
        pnlFilterRight.add(btnTim);
        pnlFilterRight.add(btnLamMoi);

        pnlRight.add(pnlSearchCenter, BorderLayout.CENTER);
        pnlRight.add(pnlFilterRight, BorderLayout.EAST);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlRight, BorderLayout.CENTER);

        pnlTabsLoaiMon = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTabsLoaiMon.setOpaque(false);
        pnlTabsLoaiMon.setBorder(new EmptyBorder(0, 12, 0, 12));

        pnlTop.add(pnlHeader);
        pnlTop.add(pnlTabsLoaiMon);

        btnTim.addActionListener(e -> locDuLieuMonAn());
        btnLamMoi.addActionListener(e -> lamMoiBoLoc());

        txtTimKiemMon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                locDuLieuMonAn();
            }
        });

        KeyAdapter keyGia = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    locDuLieuMonAn();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                JTextField src = (JTextField) e.getSource();
                JComboBox<String> cbo = (src == txtGiaTu) ? cboGiaTu : cboGiaDen;

                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_UP
                        || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    locGoiYGia(cbo, src.getText());
                    cbo.showPopup();
                    src.setText(src.getText());
                    src.setCaretPosition(src.getText().length());
                    locDuLieuMonAn();
                });
            }
        };
        txtGiaTu.addKeyListener(keyGia);
        txtGiaDen.addKeyListener(keyGia);

        return pnlTop;
    }

    private RoundedComboBox createGiaCombo() {
        RoundedComboBox cbo = new RoundedComboBox();
        cbo.setModel(new DefaultComboBoxModel<>(GOI_Y_GIA));
        cbo.setEditable(true);
        cbo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cbo.setPreferredSize(new Dimension(118, 40));
        cbo.setOpaque(false);
        cbo.setBorder(BorderFactory.createEmptyBorder());
        cbo.setUI(new NoArrowComboUI());

        JTextField editor = (JTextField) cbo.getEditor().getEditorComponent();

        editor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    locGoiYGia(cbo, editor.getText());
                    cbo.showPopup();
                    editor.requestFocus();
                    editor.setCaretPosition(editor.getText().length());
                });
            }
        });

        editor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    locGoiYGia(cbo, editor.getText());
                    cbo.showPopup();
                    editor.setCaretPosition(editor.getText().length());
                });
            }
        });

        return cbo;
    }

    private void locGoiYGia(JComboBox<String> cbo, String keyword) {
        String key = keyword == null ? "" : keyword.trim();

        Vector<String> ds = new Vector<>();
        for (String s : GOI_Y_GIA) {
            if (key.isEmpty() || s.startsWith(key)) {
                ds.add(s);
            }
        }

        if (ds.isEmpty()) {
            ds.add(key);
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(ds);
        cbo.setModel(model);
        cbo.setSelectedItem(key);
    }

    private void styleComboEditor(JTextField txt) {
        txt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txt.setBorder(new EmptyBorder(0, 10, 0, 10));
        txt.setBackground(Color.WHITE);
        txt.setForeground(TEXT);
        txt.setOpaque(false);
        txt.setCaretColor(TEXT);
        txt.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private JPanel createMainMonPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(0, 12, 10, 12));

        JPanel pnlOuter = new JPanel(new BorderLayout());
        pnlOuter.setBackground(BG_WHITE);
        pnlOuter.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel pnlTableContent = new JPanel();
        pnlTableContent.setLayout(new BoxLayout(pnlTableContent, BoxLayout.Y_AXIS));
        pnlTableContent.setBackground(BG_WHITE);

        JPanel pnlHeaderTable = new JPanel(new GridBagLayout());
        pnlHeaderTable.setBackground(BG_WHITE);
        pnlHeaderTable.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));
        pnlHeaderTable.setPreferredSize(new Dimension(100, 42));
        pnlHeaderTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pnlHeaderTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridx = 0;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("", COL_ICON, SwingConstants.CENTER, false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Mã món", COL_MA, SwingConstants.LEFT, true), gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        pnlHeaderTable.add(createHeaderCell("Tên món", 0, SwingConstants.LEFT, true), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Giá bán", COL_GIA, SwingConstants.RIGHT, false), gbc);

        pnlDanhSachMon = new JPanel();
        pnlDanhSachMon.setLayout(new BoxLayout(pnlDanhSachMon, BoxLayout.Y_AXIS));
        pnlDanhSachMon.setBackground(BG_WHITE);
        pnlDanhSachMon.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTableContent.add(pnlHeaderTable);
        pnlTableContent.add(pnlDanhSachMon);

        scrDanhSachMon = new JScrollPane(pnlTableContent);
        scrDanhSachMon.setBorder(null);
        scrDanhSachMon.getViewport().setBackground(BG_WHITE);
        scrDanhSachMon.getVerticalScrollBar().setUnitIncrement(16);
        scrDanhSachMon.getHorizontalScrollBar().setUnitIncrement(16);
        scrDanhSachMon.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        pnlOuter.add(scrDanhSachMon, BorderLayout.CENTER);
        pnlMain.add(pnlOuter, BorderLayout.CENTER);

        return pnlMain;
    }

    // ========================= BÀN =========================

    private JPanel createPanelTimBan() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(0, 6, 8, 12));

        root.add(createHeaderSearchBan(), BorderLayout.NORTH);
        root.add(createMainBanPanel(), BorderLayout.CENTER);

        return root;
    }

    private JPanel createHeaderSearchBan() {
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        JPanel pnlHeader = new JPanel(new BorderLayout(12, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 12, 10, 12));

        JLabel lblTitle = new JLabel("Tra cứu");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblTitle.setForeground(Color.BLACK);

        JPanel pnlCenter = new JPanel(new BorderLayout(12, 0));
        pnlCenter.setOpaque(false);

        JPanel pnlSearchWrap = new JPanel(new BorderLayout());
        pnlSearchWrap.setOpaque(false);

        JPanel pnlSearchBox = new RoundedPanel(18, BG_SEARCH);
        pnlSearchBox.setLayout(new BorderLayout(8, 0));
        pnlSearchBox.setPreferredSize(new Dimension(760, 44));
        pnlSearchBox.setBorder(new EmptyBorder(0, 14, 0, 8));

        txtTimKiemBan = new HintTextField("Nhập mã/tên bàn cần tìm...");
        txtTimKiemBan.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtTimKiemBan.setBorder(null);
        txtTimKiemBan.setOpaque(false);
        txtTimKiemBan.setForeground(TEXT);

        btnTimBan = new JButton();
        btnTimBan.setFocusPainted(false);
        btnTimBan.setBorder(BorderFactory.createEmptyBorder());
        btnTimBan.setContentAreaFilled(false);
        btnTimBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTimBan.setPreferredSize(new Dimension(38, 38));

        ImageIcon iconSearch = loadIcon(SEARCH_ICON_PATH, 18, 18);
        if (iconSearch != null) {
            btnTimBan.setIcon(iconSearch);
        } else {
            btnTimBan.setText("⌕");
            btnTimBan.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnTimBan.setForeground(new Color(90, 90, 90));
        }

        pnlSearchBox.add(txtTimKiemBan, BorderLayout.CENTER);
        pnlSearchBox.add(btnTimBan, BorderLayout.EAST);
        pnlSearchWrap.add(pnlSearchBox, BorderLayout.CENTER);

        JPanel pnlDateWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlDateWrap.setOpaque(false);

        btnHomNay = new JButton("Hôm nay");
        btnHomNay.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnHomNay.setFocusPainted(false);
        btnHomNay.setBackground(BTN_NGAY);
        btnHomNay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHomNay.setPreferredSize(new Dimension(92, 36));

        btnPrevNgay = new JButton("‹");
        btnPrevNgay.setFocusPainted(false);
        btnPrevNgay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrevNgay.setPreferredSize(new Dimension(42, 36));

        btnNextNgay = new JButton("›");
        btnNextNgay.setFocusPainted(false);
        btnNextNgay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNextNgay.setPreferredSize(new Dimension(42, 36));

        dcNgayTimBan = new JDateChooser();
        dcNgayTimBan.setDateFormatString("dd/MM/yyyy");
        dcNgayTimBan.setPreferredSize(new Dimension(135, 36));
        dcNgayTimBan.setMinSelectableDate(boTime(new Date()));

        pnlDateWrap.add(btnHomNay);
        pnlDateWrap.add(btnPrevNgay);
        pnlDateWrap.add(btnNextNgay);
        pnlDateWrap.add(dcNgayTimBan);

        pnlCenter.add(pnlSearchWrap, BorderLayout.CENTER);
        pnlCenter.add(pnlDateWrap, BorderLayout.EAST);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlCenter, BorderLayout.CENTER);

        pnlTabsBan = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTabsBan.setOpaque(false);
        pnlTabsBan.setBorder(new EmptyBorder(0, 12, 0, 12));

        pnlTop.add(pnlHeader);
        pnlTop.add(pnlTabsBan);

        btnTimBan.addActionListener(e -> taiDanhSachBanTheoNgay());

        txtTimKiemBan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                taiDanhSachBanTheoNgay();
            }
        });

        btnHomNay.addActionListener(e -> {
            datNgayMacDinhHomNay();
            taiDanhSachBanTheoNgay();
        });

        btnPrevNgay.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date ngayDangChon = dcNgayTimBan.getDate() == null ? new Date() : dcNgayTimBan.getDate();
            cal.setTime(ngayDangChon);
            cal.add(Calendar.DATE, -1);

            Date homNay = boTime(new Date());
            Date ngayMoi = boTime(cal.getTime());

            if (ngayMoi.before(homNay)) {
                ngayMoi = homNay;
            }

            dcNgayTimBan.setDate(ngayMoi);
            taiDanhSachBanTheoNgay();
        });

        btnNextNgay.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date ngayDangChon = dcNgayTimBan.getDate() == null ? new Date() : dcNgayTimBan.getDate();
            cal.setTime(ngayDangChon);
            cal.add(Calendar.DATE, 1);
            dcNgayTimBan.setDate(cal.getTime());
            taiDanhSachBanTheoNgay();
        });

        dcNgayTimBan.getDateEditor().addPropertyChangeListener("date", evt -> {
            if (dcNgayTimBan.getDate() != null) {
                Date homNay = boTime(new Date());
                Date ngayChon = boTime(dcNgayTimBan.getDate());

                if (ngayChon.before(homNay)) {
                    dcNgayTimBan.setDate(homNay);
                    return;
                }
                taiDanhSachBanTheoNgay();
            }
        });

        return pnlTop;
    }

    private JPanel createMainBanPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(0, 12, 10, 12));

        JPanel pnlOuter = new JPanel(new BorderLayout());
        pnlOuter.setBackground(BG_WHITE);
        pnlOuter.setBorder(BorderFactory.createLineBorder(BORDER));

        modelBan = new DefaultTableModel(new String[] { "Mã bàn", "Tên bàn", "Khu vực", "Trạng thái" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBan = new JTableCustomBan(modelBan);
        tblBan.setRowHeight(39);
        tblBan.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tblBan.setShowGrid(true);
        tblBan.setGridColor(new Color(210, 210, 210));
        tblBan.setSelectionBackground(new Color(235, 235, 235));
        tblBan.setSelectionForeground(TEXT);
        tblBan.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = tblBan.getTableHeader();
        header.setFont(new Font("SansSerif", Font.PLAIN, 16));
        header.setPreferredSize(new Dimension(100, 42));

        tblBan.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblBan.getColumnModel().getColumn(1).setPreferredWidth(720);
        tblBan.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblBan.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(tblBan);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);

        pnlOuter.add(scroll, BorderLayout.CENTER);
        pnlMain.add(pnlOuter, BorderLayout.CENTER);

        return pnlMain;
    }

    private void napTabKhuVucBan() {
        if (pnlTabsBan == null) return;

        pnlTabsBan.removeAll();

        ButtonGroup group = new ButtonGroup();

        String[] tabsCoDinh = { "Tất cả", "Bàn đặt", "Bàn đang phục vụ", "Bàn trống" };
        for (int i = 0; i < tabsCoDinh.length; i++) {
            final String tenTab = tabsCoDinh[i];
            JToggleButton btn = createTabButton(tenTab, i == 0);
            btn.addActionListener(e -> {
                filterTrangThaiBan = tenTab;
                filterKhuVucBan = "Tất cả";
                taiDanhSachBanTheoNgay();
            });
            group.add(btn);
            pnlTabsBan.add(btn);
        }

        try {
            ArrayList<KhuVuc> dsKhuVuc = khuVucDAO.getAllKhuVuc();
            if (dsKhuVuc != null) {
                for (KhuVuc kv : dsKhuVuc) {
                    if (kv == null) continue;
                    final String tenKhuVuc = safe(kv.getTenKhuVuc());
                    JToggleButton btn = createTabButton(tenKhuVuc, false);
                    btn.addActionListener(e -> {
                        filterTrangThaiBan = "Tất cả";
                        filterKhuVucBan = tenKhuVuc;
                        taiDanhSachBanTheoNgay();
                    });
                    group.add(btn);
                    pnlTabsBan.add(btn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pnlTabsBan.revalidate();
        pnlTabsBan.repaint();
    }

    private void taiDanhSachBanTheoNgay() {
        if (dcNgayTimBan == null || modelBan == null) return;

        try {
            if (dcNgayTimBan.getDate() == null) {
                datNgayMacDinhHomNay();
            }

            Date ngayChon = boTime(dcNgayTimBan.getDate());
            java.sql.Date ngaySql = new java.sql.Date(ngayChon.getTime());
            String tuKhoa = safe(txtTimKiemBan == null ? "" : txtTimKiemBan.getText());

            dsTatCaBanTheoNgay.clear();

            if (tuKhoa.isEmpty()) {
                List<String[]> ds = banDAO.getDanhSachBanTheoNgay(ngaySql);
                if (ds != null) dsTatCaBanTheoNgay.addAll(ds);
            } else {
                List<String[]> ds = banDAO.getDanhSachBanTheoNgayVaTuKhoa(ngaySql, tuKhoa);
                if (ds != null) dsTatCaBanTheoNgay.addAll(ds);
            }

            doDuLieuBanLenBang(dsTatCaBanTheoNgay);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách bàn theo ngày.");
        }
    }

    private void doDuLieuBanLenBang(List<String[]> ds) {
        if (modelBan == null) return;
        modelBan.setRowCount(0);

        if (ds == null) return;

        for (String[] item : ds) {
            if (item == null || item.length < 4) continue;

            String maBan = item[0];
            String tenBan = item[1];
            String khuVuc = item[2];
            String trangThai = item[3];

            boolean hopTrangThai = true;
            if ("Bàn đặt".equalsIgnoreCase(filterTrangThaiBan)) {
                hopTrangThai = "Đã đặt".equalsIgnoreCase(trangThai);
            } else if ("Bàn đang phục vụ".equalsIgnoreCase(filterTrangThaiBan)) {
                hopTrangThai = "Đang phục vụ".equalsIgnoreCase(trangThai);
            } else if ("Bàn trống".equalsIgnoreCase(filterTrangThaiBan)) {
                hopTrangThai = "Trống".equalsIgnoreCase(trangThai) || "Bàn trống".equalsIgnoreCase(trangThai);
            }

            boolean hopKhuVuc = "Tất cả".equalsIgnoreCase(filterKhuVucBan)
                    || filterKhuVucBan.equalsIgnoreCase(khuVuc);

            if (hopTrangThai && hopKhuVuc) {
                modelBan.addRow(new Object[] { maBan, tenBan, khuVuc, trangThai });
            }
        }
    }

    private void datNgayMacDinhHomNay() {
        if (dcNgayTimBan == null) return;
        Date homNay = boTime(new Date());
        dcNgayTimBan.setMinSelectableDate(homNay);
        dcNgayTimBan.setDate(homNay);
    }

    private Date boTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // ========================= KHÁCH HÀNG =========================

    private JPanel createPanelTimKhachHang() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(0, 6, 8, 12));

        root.add(createHeaderSearchKhachHang(), BorderLayout.NORTH);
        root.add(createMainKhachHangPanel(), BorderLayout.CENTER);

        return root;
    }

    private JPanel createHeaderSearchKhachHang() {
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 12, 10, 12));

        JLabel lblTitle = new JLabel("Tra cứu");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblTitle.setForeground(Color.BLACK);

        JPanel pnlSearchWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlSearchWrap.setOpaque(false);

        JPanel pnlSearchBox = new RoundedPanel(18, BG_SEARCH);
        pnlSearchBox.setLayout(new BorderLayout(8, 0));
        pnlSearchBox.setPreferredSize(new Dimension(820, 44));
        pnlSearchBox.setBorder(new EmptyBorder(0, 14, 0, 8));

        txtTimKiemKH = new HintTextField("Nhập số điện thoại khách cần tìm...");
        txtTimKiemKH.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtTimKiemKH.setBorder(null);
        txtTimKiemKH.setOpaque(false);
        txtTimKiemKH.setForeground(TEXT);

        btnTimKH = new JButton();
        btnTimKH.setFocusPainted(false);
        btnTimKH.setBorder(BorderFactory.createEmptyBorder());
        btnTimKH.setContentAreaFilled(false);
        btnTimKH.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTimKH.setPreferredSize(new Dimension(38, 38));

        ImageIcon iconSearch = loadIcon(SEARCH_ICON_PATH, 18, 18);
        if (iconSearch != null) {
            btnTimKH.setIcon(iconSearch);
        } else {
            btnTimKH.setText("⌕");
            btnTimKH.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnTimKH.setForeground(new Color(90, 90, 90));
        }

        pnlSearchBox.add(txtTimKiemKH, BorderLayout.CENTER);
        pnlSearchBox.add(btnTimKH, BorderLayout.EAST);

        pnlSearchWrap.add(pnlSearchBox);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearchWrap, BorderLayout.CENTER);

        pnlTop.add(pnlHeader);

        btnTimKH.addActionListener(e -> timKhachHangTheoSDT());

        txtTimKiemKH.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKhachHangTheoSDT();
                }
            }
        });

        return pnlTop;
    }

    private JPanel createMainKhachHangPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(0, 12, 10, 12));

        JPanel pnlOuter = new JPanel(new BorderLayout());
        pnlOuter.setBackground(BG_WHITE);
        pnlOuter.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel pnlTableContent = new JPanel();
        pnlTableContent.setLayout(new BoxLayout(pnlTableContent, BoxLayout.Y_AXIS));
        pnlTableContent.setBackground(BG_WHITE);

        JPanel pnlHeaderTable = new JPanel(new GridBagLayout());
        pnlHeaderTable.setBackground(BG_WHITE);
        pnlHeaderTable.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));
        pnlHeaderTable.setPreferredSize(new Dimension(100, 42));
        pnlHeaderTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pnlHeaderTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridx = 0;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("", COL_ICON, SwingConstants.CENTER, false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Mã khách hàng", KH_COL_MA, SwingConstants.LEFT, true), gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        pnlHeaderTable.add(createHeaderCell("Tên khách hàng", 0, SwingConstants.LEFT, true), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Tổng giao dịch", KH_COL_TONG, SwingConstants.RIGHT, false), gbc);

        pnlDanhSachKH = new JPanel();
        pnlDanhSachKH.setLayout(new BoxLayout(pnlDanhSachKH, BoxLayout.Y_AXIS));
        pnlDanhSachKH.setBackground(BG_WHITE);
        pnlDanhSachKH.setAlignmentX(Component.LEFT_ALIGNMENT);

        hienThiTrangThaiRongKhachHang("Nhập số điện thoại để tìm khách hàng.");

        pnlTableContent.add(pnlHeaderTable);
        pnlTableContent.add(pnlDanhSachKH);

        scrDanhSachKH = new JScrollPane(pnlTableContent);
        scrDanhSachKH.setBorder(null);
        scrDanhSachKH.getViewport().setBackground(BG_WHITE);
        scrDanhSachKH.getVerticalScrollBar().setUnitIncrement(16);
        scrDanhSachKH.getHorizontalScrollBar().setUnitIncrement(16);
        scrDanhSachKH.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        pnlOuter.add(scrDanhSachKH, BorderLayout.CENTER);
        pnlMain.add(pnlOuter, BorderLayout.CENTER);

        return pnlMain;
    }

    private void timKhachHangTheoSDT() {
        String sdt = safe(txtTimKiemKH.getText());

        if (sdt.isEmpty()) {
            hienThiTrangThaiRongKhachHang("Nhập số điện thoại để tìm khách hàng.");
            return;
        }

        if (!sdt.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại chỉ được chứa chữ số.");
            txtTimKiemKH.requestFocus();
            txtTimKiemKH.selectAll();
            return;
        }

        try {
            KhachHang kh = khachHangDAO.getKhachHangTheoSDT(sdt);

            if (kh == null) {
                hienThiTrangThaiRongKhachHang("Không tìm thấy khách hàng.");
                return;
            }

            double tongGiaoDich = khachHangDAO.layTongGiaoDichTheoMaKH(kh.getMaKH());
            doDuLieuKhachHangLenDanhSach(kh, tongGiaoDich);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tìm dữ liệu khách hàng từ cơ sở dữ liệu.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDuLieuKhachHangLenDanhSach(KhachHang kh, double tongGiaoDich) {
        pnlDanhSachKH.removeAll();
        currentExpandedKhachHangRow = null;

        KhachHangRowPanel row = new KhachHangRowPanel(kh, tongGiaoDich);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        pnlDanhSachKH.add(row);

        pnlDanhSachKH.add(Box.createVerticalGlue());
        pnlDanhSachKH.revalidate();
        pnlDanhSachKH.repaint();
    }

    private void hienThiTrangThaiRongKhachHang(String text) {
        pnlDanhSachKH.removeAll();
        currentExpandedKhachHangRow = null;

        JPanel pnlEmpty = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        pnlEmpty.setOpaque(false);
        pnlEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlEmpty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lbl.setForeground(new Color(120, 120, 120));
        pnlEmpty.add(lbl);

        pnlDanhSachKH.add(pnlEmpty);
        pnlDanhSachKH.add(Box.createVerticalGlue());
        pnlDanhSachKH.revalidate();
        pnlDanhSachKH.repaint();
    }
    // ========================= KHUYẾN MÃI =========================

    private JPanel createPanelTimKhuyenMai() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(0, 6, 8, 12));

        root.add(createHeaderSearchKhuyenMai(), BorderLayout.NORTH);
        root.add(createMainKhuyenMaiPanel(), BorderLayout.CENTER);

        return root;
    }

    private JPanel createHeaderSearchKhuyenMai() {
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);

        JPanel pnlHeader = new JPanel(new BorderLayout(12, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 12, 10, 12));

        JLabel lblTitle = new JLabel("Tra cứu");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblTitle.setForeground(Color.BLACK);

        JPanel pnlCenter = new JPanel(new BorderLayout(12, 0));
        pnlCenter.setOpaque(false);

        JPanel pnlSearchWrap = new JPanel(new BorderLayout());
        pnlSearchWrap.setOpaque(false);

        JPanel pnlSearchBox = new RoundedPanel(18, BG_SEARCH);
        pnlSearchBox.setLayout(new BorderLayout(8, 0));
        pnlSearchBox.setPreferredSize(new Dimension(460, 44));
        pnlSearchBox.setBorder(new EmptyBorder(0, 14, 0, 8));

        txtTimKiemKM = new HintTextField("Nhập mã/tên khuyến mãi cần tìm...");
        txtTimKiemKM.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtTimKiemKM.setBorder(null);
        txtTimKiemKM.setOpaque(false);
        txtTimKiemKM.setForeground(TEXT);

        btnTimKM = new JButton();
        btnTimKM.setFocusPainted(false);
        btnTimKM.setBorder(BorderFactory.createEmptyBorder());
        btnTimKM.setContentAreaFilled(false);
        btnTimKM.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTimKM.setPreferredSize(new Dimension(38, 38));

        ImageIcon iconSearch = loadIcon(SEARCH_ICON_PATH, 18, 18);
        if (iconSearch != null) {
            btnTimKM.setIcon(iconSearch);
        } else {
            btnTimKM.setText("⌕");
            btnTimKM.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btnTimKM.setForeground(new Color(90, 90, 90));
        }

        pnlSearchBox.add(txtTimKiemKM, BorderLayout.CENTER);
        pnlSearchBox.add(btnTimKM, BorderLayout.EAST);
        pnlSearchWrap.add(pnlSearchBox, BorderLayout.CENTER);

        JPanel pnlDateWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlDateWrap.setOpaque(false);

        btnLamMoiKM = new JButton("Làm mới");
        btnLamMoiKM.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnLamMoiKM.setFocusPainted(false);
        btnLamMoiKM.setBackground(BG_TAB);
        btnLamMoiKM.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLamMoiKM.setPreferredSize(new Dimension(100, 36));

        dcTuNgayKM = new JDateChooser();
        dcTuNgayKM.setDateFormatString("dd/MM/yyyy");
        dcTuNgayKM.setPreferredSize(new Dimension(135, 36));

        dcDenNgayKM = new JDateChooser();
        dcDenNgayKM.setDateFormatString("dd/MM/yyyy");
        dcDenNgayKM.setPreferredSize(new Dimension(135, 36));

        pnlDateWrap.add(new JLabel("Từ ngày"));
        pnlDateWrap.add(dcTuNgayKM);
        pnlDateWrap.add(new JLabel("Đến ngày"));
        pnlDateWrap.add(dcDenNgayKM);
        pnlDateWrap.add(btnLamMoiKM);

        pnlCenter.add(pnlSearchWrap, BorderLayout.CENTER);
        pnlCenter.add(pnlDateWrap, BorderLayout.EAST);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlCenter, BorderLayout.CENTER);

        pnlTabsKM = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTabsKM.setOpaque(false);
        pnlTabsKM.setBorder(new EmptyBorder(0, 12, 0, 12));

        pnlTop.add(pnlHeader);
        pnlTop.add(pnlTabsKM);

        btnTimKM.addActionListener(e -> locDuLieuKhuyenMai());

        txtTimKiemKM.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                locDuLieuKhuyenMai();
            }
        });

        btnLamMoiKM.addActionListener(e -> lamMoiBoLocKhuyenMai());

        dcTuNgayKM.getDateEditor().addPropertyChangeListener("date", evt -> locDuLieuKhuyenMai());
        dcDenNgayKM.getDateEditor().addPropertyChangeListener("date", evt -> locDuLieuKhuyenMai());

        return pnlTop;
    }

    private JPanel createMainKhuyenMaiPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setOpaque(false);
        pnlMain.setBorder(new EmptyBorder(0, 12, 10, 12));

        JPanel pnlOuter = new JPanel(new BorderLayout());
        pnlOuter.setBackground(BG_WHITE);
        pnlOuter.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel pnlTableContent = new JPanel();
        pnlTableContent.setLayout(new BoxLayout(pnlTableContent, BoxLayout.Y_AXIS));
        pnlTableContent.setBackground(BG_WHITE);

        JPanel pnlHeaderTable = new JPanel(new GridBagLayout());
        pnlHeaderTable.setBackground(BG_WHITE);
        pnlHeaderTable.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));
        pnlHeaderTable.setPreferredSize(new Dimension(100, 42));
        pnlHeaderTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pnlHeaderTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridx = 0;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("", COL_ICON, SwingConstants.CENTER, false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Mã khuyến mãi", KM_COL_MA, SwingConstants.LEFT, true), gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        pnlHeaderTable.add(createHeaderCell("Tên khuyến mãi", 0, SwingConstants.LEFT, true), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Giá trị", KM_COL_GIA_TRI, SwingConstants.RIGHT, true), gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Thời gian bắt đầu", KM_COL_NGAY, SwingConstants.CENTER, true), gbc);

        gbc.gridx = 5;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Thời gian kết thúc", KM_COL_NGAY, SwingConstants.CENTER, true), gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        pnlHeaderTable.add(createHeaderCell("Trạng thái", KM_COL_TRANG_THAI, SwingConstants.CENTER, false), gbc);

        pnlDanhSachKM = new JPanel();
        pnlDanhSachKM.setLayout(new BoxLayout(pnlDanhSachKM, BoxLayout.Y_AXIS));
        pnlDanhSachKM.setBackground(BG_WHITE);
        pnlDanhSachKM.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTableContent.add(pnlHeaderTable);
        pnlTableContent.add(pnlDanhSachKM);

        scrDanhSachKM = new JScrollPane(pnlTableContent);
        scrDanhSachKM.setBorder(null);
        scrDanhSachKM.getViewport().setBackground(BG_WHITE);
        scrDanhSachKM.getVerticalScrollBar().setUnitIncrement(16);
        scrDanhSachKM.getHorizontalScrollBar().setUnitIncrement(16);
        scrDanhSachKM.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        pnlOuter.add(scrDanhSachKM, BorderLayout.CENTER);
        pnlMain.add(pnlOuter, BorderLayout.CENTER);

        return pnlMain;
    }

    private void taiTatCaKhuyenMai() {
        dsTatCaKM.clear();
        List<KhuyenMai> ds = khuyenMaiDAO.getAllKhuyenMai();
        if (ds != null) {
            dsTatCaKM.addAll(ds);
        }
    }

    private void taoTabKhuyenMai() {
        if (pnlTabsKM == null) return;

        pnlTabsKM.removeAll();
        ButtonGroup group = new ButtonGroup();

        String[] tabs = { "Tất cả", "Sử dụng", "Ngưng sử dụng" };
        for (int i = 0; i < tabs.length; i++) {
            final String tenTab = tabs[i];
            JToggleButton btn = createTabButton(tenTab, i == 0);
            btn.addActionListener(e -> {
                filterTrangThaiKM = tenTab;
                locDuLieuKhuyenMai();
            });
            group.add(btn);
            pnlTabsKM.add(btn);
        }

        pnlTabsKM.revalidate();
        pnlTabsKM.repaint();
    }

    private void locDuLieuKhuyenMai() {
        if (pnlDanhSachKM == null) return;

        String tuKhoa = safe(txtTimKiemKM == null ? "" : txtTimKiemKM.getText()).toLowerCase().trim();
        Date tuNgay = dcTuNgayKM == null ? null : dcTuNgayKM.getDate();
        Date denNgay = dcDenNgayKM == null ? null : dcDenNgayKM.getDate();

        Date d1 = (tuNgay == null) ? null : boTime(tuNgay);
        Date d2 = (denNgay == null) ? null : boTime(denNgay);

        if (d1 != null && d2 != null && d1.after(d2)) {
            JOptionPane.showMessageDialog(this, "Từ ngày không được lớn hơn đến ngày.");
            return;
        }

        List<KhuyenMai> ketQua = new ArrayList<>();

        for (KhuyenMai km : dsTatCaKM) {
            if (km == null) continue;

            String maKM = safe(km.getMaKM()).toLowerCase();
            String tenKM = safe(km.getTenKhuyenMai()).toLowerCase();
            String trangThai = safe(km.getTrangThai());

            boolean hopTuKhoa = tuKhoa.isEmpty()
                    || maKM.contains(tuKhoa)
                    || tenKM.contains(tuKhoa);

            if (!hopTuKhoa) continue;

            boolean hopTrangThai = true;
            if ("Sử dụng".equalsIgnoreCase(filterTrangThaiKM)) {
                hopTrangThai = "Đang áp dụng".equalsIgnoreCase(trangThai)
                        || "Sử dụng".equalsIgnoreCase(trangThai);
            } else if ("Ngưng sử dụng".equalsIgnoreCase(filterTrangThaiKM)) {
                hopTrangThai = !("Đang áp dụng".equalsIgnoreCase(trangThai)
                        || "Sử dụng".equalsIgnoreCase(trangThai));
            }

            if (!hopTrangThai) continue;

            LocalDateTime batDau = km.getThoiGianBatDau();
            LocalDateTime ketThuc = km.getThoiGianKetThuc();

            if (batDau == null || ketThuc == null) continue;

            Calendar calBD = Calendar.getInstance();
            calBD.set(batDau.getYear(), batDau.getMonthValue() - 1, batDau.getDayOfMonth(), 0, 0, 0);
            calBD.set(Calendar.MILLISECOND, 0);
            Date ngayBatDau = calBD.getTime();

            Calendar calKT = Calendar.getInstance();
            calKT.set(ketThuc.getYear(), ketThuc.getMonthValue() - 1, ketThuc.getDayOfMonth(), 0, 0, 0);
            calKT.set(Calendar.MILLISECOND, 0);
            Date ngayKetThuc = calKT.getTime();

            boolean hopNgay = true;

            if (d1 != null && d2 == null) {
                // Chỉ chọn "từ ngày": lấy KM mà ngày này nằm trong khoảng hiệu lực
                hopNgay = !d1.before(ngayBatDau) && !d1.after(ngayKetThuc);
            } else if (d1 == null && d2 != null) {
                // Chỉ chọn "đến ngày": lấy KM mà ngày này nằm trong khoảng hiệu lực
                hopNgay = !d2.before(ngayBatDau) && !d2.after(ngayKetThuc);
            } else if (d1 != null && d2 != null) {
                // Có cả từ-ngày đến-ngày: chỉ cần khoảng user chọn giao với khoảng KM là lấy
                hopNgay = !(ngayKetThuc.before(d1) || ngayBatDau.after(d2));
            }

            if (hopNgay) {
                ketQua.add(km);
            }
        }

        doDuLieuKhuyenMaiLenDanhSach(ketQua);
    }

    private void doDuLieuKhuyenMaiLenDanhSach(List<KhuyenMai> ds) {
        pnlDanhSachKM.removeAll();
        dsDangHienThiKM.clear();
        currentExpandedKhuyenMaiRow = null;

        if (ds != null) {
            dsDangHienThiKM.addAll(ds);
        }

        if (dsDangHienThiKM.isEmpty()) {
            JPanel pnlEmpty = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
            pnlEmpty.setOpaque(false);
            pnlEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlEmpty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel lbl = new JLabel("Không có khuyến mãi phù hợp.");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
            lbl.setForeground(new Color(120, 120, 120));
            pnlEmpty.add(lbl);

            pnlDanhSachKM.add(pnlEmpty);
        } else {
            for (KhuyenMai km : dsDangHienThiKM) {
                KhuyenMaiRowPanel row = new KhuyenMaiRowPanel(km);
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
                pnlDanhSachKM.add(row);
            }
        }

        pnlDanhSachKM.add(Box.createVerticalGlue());
        pnlDanhSachKM.revalidate();
        pnlDanhSachKM.repaint();
    }

    private String formatDateTimeKM(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(dtfKM);
    }

    private String formatGiaTriKhuyenMai(KhuyenMai km) {
        if (km == null || km.getMaLoaiKM() == null) return formatMoney(km.getGiaTri());

        String tenLoai = safe(km.getMaLoaiKM().getTenLoaiKM()).toLowerCase();
        if (tenLoai.contains("phần trăm") || tenLoai.contains("phan tram")) {
            if (km.getGiaTri() == (long) km.getGiaTri()) {
                return String.format("%.0f %%", km.getGiaTri());
            }
            return km.getGiaTri() + " %";
        }
        return formatMoney(km.getGiaTri());
    }

    private class KhuyenMaiRowPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final KhuyenMai khuyenMai;
        private final JPanel pnlRow;
        private final JPanel pnlDetail;
        private final TriangleIcon rowArrow;
        private boolean expanded = false;

        public KhuyenMaiRowPanel(KhuyenMai km) {
            this.khuyenMai = km;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG_WHITE);
            setOpaque(true);

            pnlRow = new JPanel(new GridBagLayout());
            pnlRow.setOpaque(true);
            pnlRow.setBackground(BG_WHITE);
            pnlRow.setPreferredSize(new Dimension(100, KM_ROW_HEIGHT));
            pnlRow.setMinimumSize(new Dimension(100, KM_ROW_HEIGHT));
            pnlRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, KM_ROW_HEIGHT));
            pnlRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(0, 0, 0, 0);

            rowArrow = new TriangleIcon();
            rowArrow.setExpanded(false);

            gbc.gridx = 0;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKM(rowArrow, COL_ICON, SwingConstants.CENTER, false), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKM(createPlainLabelKM(safe(km.getMaKM()), SwingConstants.LEFT, TEXT),
                    KM_COL_MA, SwingConstants.LEFT, true), gbc);

            gbc.gridx = 2;
            gbc.weightx = 1.0;
            pnlRow.add(createBodyCellKM(createPlainLabelKM(safe(km.getTenKhuyenMai()), SwingConstants.LEFT, TEXT),
                    0, SwingConstants.LEFT, true), gbc);

            gbc.gridx = 3;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKM(createPlainLabelKM(formatGiaTriKhuyenMai(km), SwingConstants.RIGHT, TEXT),
                    KM_COL_GIA_TRI, SwingConstants.RIGHT, true), gbc);

            gbc.gridx = 4;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKM(createPlainLabelKM(formatDateTimeKM(km.getThoiGianBatDau()), SwingConstants.CENTER, TEXT),
                    KM_COL_NGAY, SwingConstants.CENTER, true), gbc);

            gbc.gridx = 5;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKM(createPlainLabelKM(formatDateTimeKM(km.getThoiGianKetThuc()), SwingConstants.CENTER, TEXT),
                    KM_COL_NGAY, SwingConstants.CENTER, true), gbc);

            Color mauTrangThai = ("Đang áp dụng".equalsIgnoreCase(safe(km.getTrangThai()))
                    || "Sử dụng".equalsIgnoreCase(safe(km.getTrangThai())))
                    ? new Color(63, 160, 42)
                    : new Color(180, 60, 60);

            gbc.gridx = 6;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKM(createPlainLabelKM(safe(km.getTrangThai()), SwingConstants.CENTER, mauTrangThai),
                    KM_COL_TRANG_THAI, SwingConstants.CENTER, false), gbc);

            pnlDetail = createDetailPanelKM(km);
            pnlDetail.setVisible(false);

            add(pnlRow);
            add(pnlDetail);

            setMaximumSize(new Dimension(Integer.MAX_VALUE, KM_ROW_HEIGHT));
            setPreferredSize(new Dimension(100, KM_ROW_HEIGHT));

            MouseAdapter clickAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleExpand();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!expanded) pnlRow.setBackground(BG_ROW_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!expanded) pnlRow.setBackground(BG_WHITE);
                }
            };

            attachClickRecursive(pnlRow, clickAdapter);
        }

        private void toggleExpand() {
            if (!expanded && currentExpandedKhuyenMaiRow != null && currentExpandedKhuyenMaiRow != this) {
                currentExpandedKhuyenMaiRow.setExpanded(false);
            }
            setExpanded(!expanded);
            currentExpandedKhuyenMaiRow = expanded ? this : null;
        }

        private void setExpanded(boolean value) {
            expanded = value;
            rowArrow.setExpanded(value);
            pnlDetail.setVisible(value);
            pnlRow.setBackground(value ? new Color(245, 245, 245) : BG_WHITE);

            int h = value ? KM_ROW_HEIGHT + KM_DETAIL_HEIGHT : KM_ROW_HEIGHT;
            setPreferredSize(new Dimension(100, h));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, h));

            revalidate();
            repaint();
            pnlDanhSachKM.revalidate();
            pnlDanhSachKM.repaint();
        }

        private JLabel createPlainLabelKM(String text, int align, Color color) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lbl.setForeground(color);
            lbl.setHorizontalAlignment(align);
            lbl.setVerticalAlignment(SwingConstants.CENTER);
            return lbl;
        }

        private JPanel createBodyCellKM(JComponent comp, int width, int align, boolean drawRightBorder) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setOpaque(false);
            cell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, drawRightBorder ? 1 : 0, COLUMN_LINE));

            if (width > 0) {
                Dimension d = new Dimension(width, KM_ROW_HEIGHT);
                cell.setPreferredSize(d);
                cell.setMinimumSize(d);
                cell.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
            }

            if (comp instanceof JLabel) {
                ((JLabel) comp).setHorizontalAlignment(align);
                ((JLabel) comp).setVerticalAlignment(SwingConstants.CENTER);
                ((JLabel) comp).setBorder(new EmptyBorder(0, 10, 0, 10));
                cell.add(comp, BorderLayout.CENTER);
            } else {
                JPanel holder = new JPanel(new GridBagLayout());
                holder.setOpaque(false);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.CENTER;
                holder.add(comp, gbc);

                cell.add(holder, BorderLayout.CENTER);
            }

            return cell;
        }

        private JPanel createDetailPanelKM(KhuyenMai km) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(2, 34, 12, 12));
            wrapper.setPreferredSize(new Dimension(100, KM_DETAIL_HEIGHT));
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, KM_DETAIL_HEIGHT));

            JPanel inner = new JPanel(new BorderLayout());
            inner.setBackground(BG_WHITE);
            inner.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

            JLabel lblTitle = new JLabel("Chi tiết");
            lblTitle.setOpaque(true);
            lblTitle.setBackground(BG_DETAIL_HEADER);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblTitle.setBorder(new EmptyBorder(8, 12, 8, 12));

            JPanel info = new JPanel(new GridBagLayout());
            info.setOpaque(true);
            info.setBackground(BG_WHITE);
            info.setBorder(new EmptyBorder(10, 12, 12, 12));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addInfoRowStyled(info, gbc, 0, "Mã khuyến mãi", safe(km.getMaKM()), 220, 34);
            addInfoRowStyled(info, gbc, 1, "Tên khuyến mãi", safe(km.getTenKhuyenMai()), 220, 34);
            addInfoRowStyled(info, gbc, 2, "Loại khuyến mãi",
                    km.getMaLoaiKM() == null ? "" : safe(km.getMaLoaiKM().getTenLoaiKM()), 220, 34);
            addInfoRowStyled(info, gbc, 3, "Giá trị", formatGiaTriKhuyenMai(km), 220, 34);
            addInfoRowStyled(info, gbc, 4, "Thời gian bắt đầu", formatDateTimeKM(km.getThoiGianBatDau()), 220, 34);
            addInfoRowStyled(info, gbc, 5, "Thời gian kết thúc", formatDateTimeKM(km.getThoiGianKetThuc()), 220, 34);
            addInfoRowStyled(info, gbc, 6, "Đối tượng áp dụng", safe(km.getDoiTuongApDung()), 220, 34);
            addInfoRowStyled(info, gbc, 7, "Điều kiện áp dụng",
                    km.getDieuKienApDung() == 0 ? "0" : formatMoney(km.getDieuKienApDung()), 220, 34);
            addInfoRowStyled(info, gbc, 8, "Trạng thái", safe(km.getTrangThai()), 220, 34);
            addInfoRowStyled(info, gbc, 9, "Ghi chú", safe(km.getGhiChu()), 220, 34);

            inner.add(lblTitle, BorderLayout.NORTH);
            inner.add(info, BorderLayout.CENTER);

            wrapper.add(inner, BorderLayout.CENTER);
            return wrapper;
        }
    }
    private void lamMoiBoLocKhuyenMai() {
        if (txtTimKiemKM != null) {
            txtTimKiemKM.setText("");
        }

        if (dcTuNgayKM != null) {
            dcTuNgayKM.setDate(null);
        }

        if (dcDenNgayKM != null) {
            dcDenNgayKM.setDate(null);
        }

        filterTrangThaiKM = "Tất cả";

        if (pnlTabsKM != null && pnlTabsKM.getComponentCount() > 0 && pnlTabsKM.getComponent(0) instanceof JToggleButton) {
            ((JToggleButton) pnlTabsKM.getComponent(0)).setSelected(true);
        }

        doDuLieuKhuyenMaiLenDanhSach(dsTatCaKM);
    }

    // ========================= DÙNG CHUNG =========================

    private JPanel createHeaderCell(String text, int width, int align, boolean drawRightBorder) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setOpaque(true);
        cell.setBackground(BG_WHITE);
        cell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, drawRightBorder ? 1 : 0, COLUMN_LINE));

        if (width > 0) {
            Dimension d = new Dimension(width, 42);
            cell.setPreferredSize(d);
            cell.setMinimumSize(d);
            cell.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        }

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lbl.setForeground(new Color(85, 85, 85));
        lbl.setHorizontalAlignment(align);
        lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
        cell.add(lbl, BorderLayout.CENTER);

        return cell;
    }

    private void addInfoRowStyled(JPanel panel, GridBagConstraints gbc, int row, String title, String value, int titleWidth, int rowHeight) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(true);
        rowPanel.setBackground(row % 2 == 0 ? new Color(235, 235, 235) : BG_WHITE);
        rowPanel.setPreferredSize(new Dimension(100, rowHeight));
        rowPanel.setMinimumSize(new Dimension(100, rowHeight));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));

        JLabel lblTitle = createDetailLabel(title, true);
        lblTitle.setBorder(new EmptyBorder(6, 12, 6, 12));
        lblTitle.setPreferredSize(new Dimension(titleWidth, rowHeight));

        JLabel lblValue = createDetailLabel(value, false);
        lblValue.setBorder(new EmptyBorder(6, 0, 6, 12));

        rowPanel.add(lblTitle, BorderLayout.WEST);
        rowPanel.add(lblValue, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(rowPanel, gbc);
    }

    private JPanel createComingSoonPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        lbl.setForeground(new Color(100, 100, 100));
        panel.add(lbl);

        return panel;
    }

    private void napDuLieuBanDau() {
        try {
            dsTatCaMon.clear();
            dsLoaiMon.clear();

            List<MonAn> dsMon = monAnDAO.getAllMonAn();
            if (dsMon != null) {
                dsTatCaMon.addAll(dsMon);
            }

            List<LoaiMonAn> dsLoai = loaiMonAnDAO.getAllLoaiMonAn();
            if (dsLoai != null) {
                dsLoaiMon.addAll(dsLoai);
            }

            taoTabLoaiMon();
            doDuLieuLenDanhSach(dsTatCaMon);

            napTabKhuVucBan();
            datNgayMacDinhHomNay();
            taiDanhSachBanTheoNgay();
            
            taiTatCaKhuyenMai();
            taoTabKhuyenMai();
            doDuLieuKhuyenMaiLenDanhSach(dsTatCaKM);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể tải dữ liệu tra cứu từ cơ sở dữ liệu.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taoTabLoaiMon() {
        pnlTabsLoaiMon.removeAll();

        ButtonGroup group = new ButtonGroup();

        JToggleButton btnTatCa = createTabButton("Tất cả", "ALL".equals(maLoaiDangChon));
        btnTatCa.addActionListener(e -> {
            maLoaiDangChon = "ALL";
            btnTatCa.setSelected(true);
            locDuLieuMonAn();
        });
        group.add(btnTatCa);
        pnlTabsLoaiMon.add(btnTatCa);

        for (LoaiMonAn loai : dsLoaiMon) {
            if (loai == null) continue;

            String maLoai = safe(loai.getMaLoaiMonAn());
            String tenLoai = safe(loai.getTenLoaiMonAn());

            JToggleButton btnLoai = createTabButton(tenLoai, maLoai.equals(maLoaiDangChon));
            btnLoai.addActionListener(e -> {
                maLoaiDangChon = maLoai;
                btnLoai.setSelected(true);
                locDuLieuMonAn();
            });

            group.add(btnLoai);
            pnlTabsLoaiMon.add(btnLoai);
        }

        pnlTabsLoaiMon.revalidate();
        pnlTabsLoaiMon.repaint();
    }

    private JToggleButton createTabButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text, selected);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 17));
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 20, 9, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(selected ? BG_TAB_SELECTED : BG_TAB);

        btn.addChangeListener(e -> {
            btn.setBackground(btn.isSelected() ? BG_TAB_SELECTED : BG_TAB);
            btn.repaint();
        });

        return btn;
    }

    private void doDuLieuLenDanhSach(List<MonAn> ds) {
        pnlDanhSachMon.removeAll();
        dsDangHienThi.clear();
        currentExpandedRow = null;

        if (ds != null) {
            dsDangHienThi.addAll(ds);
        }

        if (dsDangHienThi.isEmpty()) {
            JPanel pnlEmpty = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
            pnlEmpty.setOpaque(false);
            pnlEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlEmpty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel lbl = new JLabel("Không có món ăn phù hợp.");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
            lbl.setForeground(new Color(120, 120, 120));
            pnlEmpty.add(lbl);

            pnlDanhSachMon.add(pnlEmpty);
        } else {
            for (MonAn mon : dsDangHienThi) {
                MonRowPanel row = new MonRowPanel(mon);
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
                pnlDanhSachMon.add(row);
            }
        }

        pnlDanhSachMon.add(Box.createVerticalGlue());
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }

    private void locDuLieuMonAn() {
        String tuKhoa = safe(txtTimKiemMon.getText()).toLowerCase();
        Double giaTu = parseMoney(safe(txtGiaTu.getText()));
        Double giaDen = parseMoney(safe(txtGiaDen.getText()));

        if (!safe(txtGiaTu.getText()).isEmpty() && giaTu == null) {
            JOptionPane.showMessageDialog(this, "Giá từ không hợp lệ.");
            txtGiaTu.requestFocus();
            return;
        }

        if (!safe(txtGiaDen.getText()).isEmpty() && giaDen == null) {
            JOptionPane.showMessageDialog(this, "Giá đến không hợp lệ.");
            txtGiaDen.requestFocus();
            return;
        }

        if (giaTu != null && giaDen != null && giaTu > giaDen) {
            JOptionPane.showMessageDialog(this, "Giá từ không được lớn hơn giá đến.");
            txtGiaTu.requestFocus();
            return;
        }

        List<MonAn> ketQua = new ArrayList<>();

        for (MonAn mon : dsTatCaMon) {
            if (mon == null) continue;

            boolean hopLoai = "ALL".equals(maLoaiDangChon)
                    || (mon.getMaLoaiMonAn() != null
                    && maLoaiDangChon.equalsIgnoreCase(safe(mon.getMaLoaiMonAn().getMaLoaiMonAn())));

            if (!hopLoai) continue;

            String maMon = safe(mon.getMaMon()).toLowerCase();
            String tenMon = safe(mon.getTenMon()).toLowerCase();

            boolean hopTuKhoa = tuKhoa.isEmpty()
                    || maMon.contains(tuKhoa)
                    || tenMon.contains(tuKhoa);

            if (!hopTuKhoa) continue;

            double gia = mon.getDonGia();
            boolean hopGiaTu = (giaTu == null) || (gia >= giaTu);
            boolean hopGiaDen = (giaDen == null) || (gia <= giaDen);

            if (hopGiaTu && hopGiaDen) {
                ketQua.add(mon);
            }
        }

        doDuLieuLenDanhSach(ketQua);
    }

    private void lamMoiBoLoc() {
        txtTimKiemMon.setText("");
        cboGiaTu.setSelectedItem("");
        cboGiaDen.setSelectedItem("");
        maLoaiDangChon = "ALL";

        if (pnlTabsLoaiMon.getComponentCount() > 0 && pnlTabsLoaiMon.getComponent(0) instanceof JToggleButton) {
            ((JToggleButton) pnlTabsLoaiMon.getComponent(0)).setSelected(true);
        }

        doDuLieuLenDanhSach(dsTatCaMon);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private Double parseMoney(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            String cleaned = s.replace(".", "").replace(",", "").replace(" ", "");
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatMoney(double value) {
        return df.format(value);
    }

    private JLabel createDetailLabel(String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 17));
        lbl.setForeground(TEXT);
        return lbl;
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() <= 0) return null;
            java.awt.Image img = icon.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private void attachClickRecursive(Component comp, MouseAdapter adapter) {
        comp.addMouseListener(adapter);

        if (comp instanceof JComponent) {
            ((JComponent) comp).setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                attachClickRecursive(child, adapter);
            }
        }
    }

    private class KhachHangRowPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final KhachHang khachHang;
        private final double tongGiaoDich;
        private final JPanel pnlRow;
        private final JPanel pnlDetail;
        private final TriangleIcon rowArrow;
        private boolean expanded = false;

        public KhachHangRowPanel(KhachHang khachHang, double tongGiaoDich) {
            this.khachHang = khachHang;
            this.tongGiaoDich = tongGiaoDich;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG_WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));
            setOpaque(true);

            pnlRow = new JPanel(new GridBagLayout());
            pnlRow.setOpaque(true);
            pnlRow.setBackground(BG_WHITE);
            pnlRow.setPreferredSize(new Dimension(100, KH_ROW_HEIGHT));
            pnlRow.setMinimumSize(new Dimension(100, KH_ROW_HEIGHT));
            pnlRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, KH_ROW_HEIGHT));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(0, 0, 0, 0);

            rowArrow = new TriangleIcon();
            rowArrow.setExpanded(false);

            gbc.gridx = 0;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKH(rowArrow, COL_ICON, SwingConstants.CENTER, false), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKH(createPlainLabelKH(safe(khachHang.getMaKH()), SwingConstants.LEFT), KH_COL_MA, SwingConstants.LEFT, true), gbc);

            gbc.gridx = 2;
            gbc.weightx = 1.0;
            pnlRow.add(createBodyCellKH(createPlainLabelKH(safe(khachHang.getTenKH()), SwingConstants.LEFT), 0, SwingConstants.LEFT, true), gbc);

            gbc.gridx = 3;
            gbc.weightx = 0;
            pnlRow.add(createBodyCellKH(createPlainLabelKH(formatMoney(tongGiaoDich), SwingConstants.RIGHT), KH_COL_TONG, SwingConstants.RIGHT, false), gbc);

            pnlDetail = createDetailPanelKH(khachHang);
            pnlDetail.setVisible(false);

            add(pnlRow);
            add(pnlDetail);

            setMaximumSize(new Dimension(Integer.MAX_VALUE, KH_ROW_HEIGHT));
            setPreferredSize(new Dimension(100, KH_ROW_HEIGHT));

            MouseAdapter clickAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleExpand();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!expanded) {
                        pnlRow.setBackground(BG_ROW_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!expanded) {
                        pnlRow.setBackground(BG_WHITE);
                    }
                }
            };

            attachClickRecursive(pnlRow, clickAdapter);
        }

        private JLabel createPlainLabelKH(String text, int align) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lbl.setForeground(TEXT);
            lbl.setHorizontalAlignment(align);
            lbl.setVerticalAlignment(SwingConstants.CENTER);
            return lbl;
        }

        private JPanel createBodyCellKH(JComponent comp, int width, int align, boolean drawRightBorder) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setOpaque(false);
            cell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, drawRightBorder ? 1 : 0, COLUMN_LINE));

            if (width > 0) {
                Dimension d = new Dimension(width, KH_ROW_HEIGHT);
                cell.setPreferredSize(d);
                cell.setMinimumSize(d);
                cell.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
            }

            if (comp instanceof JLabel) {
                ((JLabel) comp).setHorizontalAlignment(align);
                ((JLabel) comp).setVerticalAlignment(SwingConstants.CENTER);
                ((JLabel) comp).setBorder(new EmptyBorder(0, 10, 0, 10));
                cell.add(comp, BorderLayout.CENTER);
            } else {
                JPanel holder = new JPanel(new GridBagLayout());
                holder.setOpaque(false);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.fill = GridBagConstraints.NONE;
                holder.add(comp, gbc);

                cell.add(holder, BorderLayout.CENTER);
            }

            return cell;
        }

        private JPanel createDetailPanelKH(KhachHang kh) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(2, 34, 12, 12));
            wrapper.setPreferredSize(new Dimension(100, KH_DETAIL_HEIGHT));
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, KH_DETAIL_HEIGHT));

            JPanel inner = new JPanel(new BorderLayout());
            inner.setBackground(BG_WHITE);
            inner.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

            JLabel lblTitle = new JLabel("Chi tiết");
            lblTitle.setOpaque(true);
            lblTitle.setBackground(BG_DETAIL_HEADER);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblTitle.setBorder(new EmptyBorder(8, 12, 8, 12));

            JPanel info = new JPanel(new GridBagLayout());
            info.setOpaque(true);
            info.setBackground(BG_WHITE);
            info.setBorder(new EmptyBorder(10, 12, 12, 12));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addInfoRowStyled(info, gbc, 0, "Mã khách hàng", safe(kh.getMaKH()), 180, 34);
            addInfoRowStyled(info, gbc, 1, "Tên khách hàng", safe(kh.getTenKH()), 180, 34);
            addInfoRowStyled(info, gbc, 2, "Loại khách hàng",
                    kh.getMaLoaiKH() == null ? "" : safe(kh.getMaLoaiKH().getTenLoaiKH()), 180, 34);
            addInfoRowStyled(info, gbc, 3, "Số điện thoại", safe(kh.getSdt()), 180, 34);
            addInfoRowStyled(info, gbc, 4, "Điểm tích lũy", String.valueOf(kh.getDiemTichLuy()), 180, 34);
            addInfoRowStyled(info, gbc, 5, "Ghi chú", "", 180, 34);

            inner.add(lblTitle, BorderLayout.NORTH);
            inner.add(info, BorderLayout.CENTER);
            wrapper.add(inner, BorderLayout.CENTER);

            return wrapper;
        }

        private void toggleExpand() {
            if (!expanded) {
                if (currentExpandedKhachHangRow != null && currentExpandedKhachHangRow != this) {
                    currentExpandedKhachHangRow.collapse();
                }
                expand();
                currentExpandedKhachHangRow = this;
            } else {
                collapse();
                currentExpandedKhachHangRow = null;
            }

            pnlDanhSachKH.revalidate();
            pnlDanhSachKH.repaint();

            SwingUtilities.invokeLater(() -> {
                if (expanded) {
                    Rectangle bounds = SwingUtilities.convertRectangle(
                            this.getParent(),
                            this.getBounds(),
                            scrDanhSachKH.getViewport()
                    );
                    scrDanhSachKH.getViewport().scrollRectToVisible(bounds);
                }
            });
        }

        private void expand() {
            expanded = true;
            rowArrow.setExpanded(true);
            pnlDetail.setVisible(true);
            pnlRow.setBackground(BG_WHITE);

            int totalHeight = KH_ROW_HEIGHT + KH_DETAIL_HEIGHT + 14;
            setPreferredSize(new Dimension(100, totalHeight));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));
            revalidate();
        }

        private void collapse() {
            expanded = false;
            rowArrow.setExpanded(false);
            pnlDetail.setVisible(false);
            pnlRow.setBackground(BG_WHITE);

            setPreferredSize(new Dimension(100, KH_ROW_HEIGHT));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, KH_ROW_HEIGHT));
            revalidate();
        }
    }

    private class MonRowPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final MonAn monAn;
        private final JPanel pnlRow;
        private final JPanel pnlDetail;
        private final TriangleIcon rowArrow;
        private boolean expanded = false;

        public MonRowPanel(MonAn monAn) {
            this.monAn = monAn;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG_WHITE);
            setOpaque(true);

            pnlRow = new JPanel(new GridBagLayout());
            pnlRow.setOpaque(true);
            pnlRow.setBackground(BG_WHITE);
            pnlRow.setPreferredSize(new Dimension(100, ROW_HEIGHT));
            pnlRow.setMinimumSize(new Dimension(100, ROW_HEIGHT));
            pnlRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
            pnlRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(0, 0, 0, 0);

            rowArrow = new TriangleIcon();
            rowArrow.setExpanded(false);

            gbc.gridx = 0;
            gbc.weightx = 0;
            pnlRow.add(createBodyCell(rowArrow, COL_ICON, SwingConstants.CENTER, false, true), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0;
            pnlRow.add(createBodyCell(createPlainLabel(safe(monAn.getMaMon()), SwingConstants.LEFT), COL_MA, SwingConstants.LEFT, true, false), gbc);

            gbc.gridx = 2;
            gbc.weightx = 1.0;
            pnlRow.add(createBodyCell(createPlainLabel(safe(monAn.getTenMon()), SwingConstants.LEFT), 0, SwingConstants.LEFT, true, false), gbc);

            gbc.gridx = 3;
            gbc.weightx = 0;
            pnlRow.add(createBodyCell(createPlainLabel(formatMoney(monAn.getDonGia()), SwingConstants.RIGHT), COL_GIA, SwingConstants.RIGHT, false, false), gbc);

            pnlDetail = createDetailPanel(monAn);
            pnlDetail.setVisible(false);

            add(pnlRow);
            add(pnlDetail);

            setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
            setPreferredSize(new Dimension(100, ROW_HEIGHT));

            MouseAdapter clickAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleExpand();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!expanded) {
                        pnlRow.setBackground(BG_ROW_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!expanded) {
                        pnlRow.setBackground(BG_WHITE);
                    }
                }
            };

            attachClickRecursive(pnlRow, clickAdapter);
        }

        private JLabel createPlainLabel(String text, int align) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lbl.setForeground(TEXT);
            lbl.setHorizontalAlignment(align);
            lbl.setVerticalAlignment(SwingConstants.CENTER);
            return lbl;
        }

        private JPanel createBodyCell(JComponent comp, int width, int align, boolean drawRightBorder, boolean centerComp) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setOpaque(false);
            cell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, drawRightBorder ? 1 : 0, COLUMN_LINE));

            if (width > 0) {
                Dimension d = new Dimension(width, ROW_HEIGHT);
                cell.setPreferredSize(d);
                cell.setMinimumSize(d);
                cell.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
            }

            if (comp instanceof JLabel) {
                ((JLabel) comp).setHorizontalAlignment(align);
                ((JLabel) comp).setVerticalAlignment(SwingConstants.CENTER);
                ((JLabel) comp).setBorder(new EmptyBorder(0, 10, 0, 10));
                cell.add(comp, BorderLayout.CENTER);
            } else {
                JPanel holder = new JPanel(new GridBagLayout());
                holder.setOpaque(false);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.fill = GridBagConstraints.NONE;
                holder.add(comp, gbc);

                cell.add(holder, BorderLayout.CENTER);
            }

            return cell;
        }

        private JPanel createDetailPanel(MonAn mon) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(2, 34, 12, 12));
            wrapper.setPreferredSize(new Dimension(100, DETAIL_HEIGHT));
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, DETAIL_HEIGHT));
            wrapper.setMinimumSize(new Dimension(100, DETAIL_HEIGHT));

            JPanel inner = new JPanel(new BorderLayout());
            inner.setBackground(BG_WHITE);
            inner.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

            JLabel lblTitle = new JLabel("Chi tiết");
            lblTitle.setOpaque(true);
            lblTitle.setBackground(BG_DETAIL_HEADER);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblTitle.setBorder(new EmptyBorder(8, 12, 8, 12));

            JPanel info = new JPanel(new GridBagLayout());
            info.setOpaque(true);
            info.setBackground(BG_WHITE);
            info.setBorder(new EmptyBorder(10, 12, 12, 12));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addInfoRowStyled(info, gbc, 0, "Mã món", safe(mon.getMaMon()), 180, 34);
            addInfoRowStyled(info, gbc, 1, "Tên món", safe(mon.getTenMon()), 180, 34);
            addInfoRowStyled(info, gbc, 2, "Loại món",
                    mon.getMaLoaiMonAn() == null ? "" : safe(mon.getMaLoaiMonAn().getTenLoaiMonAn()), 180, 34);
            addInfoRowStyled(info, gbc, 3, "Giá món", formatMoney(mon.getDonGia()), 180, 34);
            addInfoRowStyled(info, gbc, 4, "Ghi chú", safe(mon.getMoTa()), 180, 34);

            inner.add(lblTitle, BorderLayout.NORTH);
            inner.add(info, BorderLayout.CENTER);
            wrapper.add(inner, BorderLayout.CENTER);

            return wrapper;
        }

        private void toggleExpand() {
            if (!expanded) {
                if (currentExpandedRow != null && currentExpandedRow != this) {
                    currentExpandedRow.collapse();
                }
                expand();
                currentExpandedRow = this;
            } else {
                collapse();
                currentExpandedRow = null;
            }

            pnlDanhSachMon.revalidate();
            pnlDanhSachMon.repaint();

            SwingUtilities.invokeLater(() -> {
                if (expanded) {
                    Rectangle bounds = SwingUtilities.convertRectangle(
                            this.getParent(),
                            this.getBounds(),
                            scrDanhSachMon.getViewport()
                    );
                    scrDanhSachMon.getViewport().scrollRectToVisible(bounds);
                }
            });
        }

        private void expand() {
            expanded = true;
            rowArrow.setExpanded(true);
            pnlDetail.setVisible(true);
            pnlRow.setBackground(BG_WHITE);

            int totalHeight = ROW_HEIGHT + DETAIL_HEIGHT + 14;
            setPreferredSize(new Dimension(100, totalHeight));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));
            revalidate();
        }

        private void collapse() {
            expanded = false;
            rowArrow.setExpanded(false);
            pnlDetail.setVisible(false);
            pnlRow.setBackground(BG_WHITE);

            setPreferredSize(new Dimension(100, ROW_HEIGHT));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
            revalidate();
        }
    }

    private class JTableCustomBan extends JTable {
        private static final long serialVersionUID = 1L;

        public JTableCustomBan(DefaultTableModel model) {
            super(model);
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);

            if (!isRowSelected(row)) {
                c.setBackground(BG_WHITE);
                c.setForeground(TEXT);
            }

            Object value = getValueAt(row, column);
            if (column == 3 && value != null) {
                String tt = value.toString();
                if ("Đã đặt".equalsIgnoreCase(tt)) {
                    c.setForeground(new Color(45, 170, 45));
                } else if ("Đang phục vụ".equalsIgnoreCase(tt)) {
                    c.setForeground(new Color(0, 120, 215));
                } else {
                    c.setForeground(TEXT);
                }
            } else if (!isRowSelected(row)) {
                c.setForeground(TEXT);
            }

            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                if (column == 3) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
            }

            return c;
        }
    }

    private class TriangleIcon extends JComponent {
        private static final long serialVersionUID = 1L;
        private boolean expanded = false;

        public TriangleIcon() {
            setPreferredSize(new Dimension(10, 10));
            setMinimumSize(new Dimension(10, 10));
            setMaximumSize(new Dimension(10, 10));
            setOpaque(false);
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ARROW_COLOR);

            Path2D p = new Path2D.Double();
            if (!expanded) {
                p.moveTo(2, 1);
                p.lineTo(8, 5);
                p.lineTo(2, 9);
                p.closePath();
            } else {
                p.moveTo(1, 2);
                p.lineTo(9, 2);
                p.lineTo(5, 8);
                p.closePath();
            }

            g2.fill(p);
            g2.dispose();
        }
    }

    private class NoArrowComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton();
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }
    }

    private class HintTextField extends JTextField {
        private static final long serialVersionUID = 1L;
        private final String hint;

        public HintTextField(String hint) {
            this.hint = hint;
            setOpaque(false);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(HINT);
                g2.setFont(getFont());
                Insets ins = getInsets();
                g2.drawString(hint, ins.left + 2, getHeight() / 2 + 6);
                g2.dispose();
            }
        }
    }

    private class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color fillColor;

        public RoundedPanel(int radius, Color fillColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(fillColor);
            g2.fill(shape);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(BORDER);
            g2.draw(shape);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class RoundedComboBox extends JComboBox<String> {
        private static final long serialVersionUID = 1L;

        public RoundedComboBox() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(Color.WHITE);
            g2.fill(shape);
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(shape);

            int cx = getWidth() - 20;
            int cy = getHeight() / 2 + 1;

            Path2D p = new Path2D.Double();
            p.moveTo(cx - 6, cy - 4);
            p.lineTo(cx, cy + 2);
            p.lineTo(cx + 6, cy - 4);

            g2.setColor(ARROW_COLOR);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(p);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TraCuu_GUI frame = new TraCuu_GUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}