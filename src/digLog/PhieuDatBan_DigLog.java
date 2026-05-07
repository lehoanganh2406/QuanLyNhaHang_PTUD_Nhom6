package digLog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.MonAn_DAO;
import dao.PhieuDatBan_DAO;
import dao.PhieuDatMon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.MonAn;
import entity.PhieuDatMon;
import entity.TaiKhoan;
import gui.Order_Mon_GUI;
import gui.TrangChu_GUI;
import dao.KhachHang_DAO;
import entity.KhachHang;
import entity.LoaiKhachHang;

public class PhieuDatBan_DigLog extends JDialog {

    private JTextField txtMaPhieu;
    private JTextField txtKhachHang;
    private JTextField txtSoDienThoai;
    private JTextField txtGioKhachVao;
    private JSpinner spnSoLuongKhach;
    private JTextField txtBan;
    private JTextField txtTienCoc;
    private JTextArea txtGhiChu;

    private JButton btnCalendar;
    private JButton btnSearchBan;
    private JButton btnThoat;
    private JButton btnDatBan;
    private JButton btnHuy;
    private JButton btnNhanBan;
    private JButton btnLuu;

    private JLabel lblMonDatTruoc;
    private JTable tblMonDatTruoc;
    private DefaultTableModel modelMonDatTruoc;

    private JPopupMenu popupBan;
    private JTable tblBan;
    private DefaultTableModel modelBan;
    private JScrollPane scrMonDatTruoc;
    private TaiKhoan taiKhoanDangNhap;
    private JComboBox<String> cboPTThanhToanCoc;

    private Timestamp thoiGianDaChon;
    private ArrayList<String[]> dsBanTheoGio = new ArrayList<>();
    private String maBanDuocChon = "";

    private String maPhieuHienTai = null;
    private String trangThaiHienTai = "";
    private boolean cheDoChiTiet = false;

    private ArrayList<PhieuDatMon> dsMonDatTam = new ArrayList<>();

    private final Color BG_MAIN = new Color(239, 239, 239);
    private final Color BORDER = new Color(170, 170, 170);
    private final Color DISABLED_BG = new Color(222, 222, 222);
    private final Color PLACEHOLDER = new Color(145, 145, 145);
    
    private static final Dimension SIZE_KHONG_MON_THEM = new Dimension(700, 680);
    private static final Dimension SIZE_CO_MON_THEM = new Dimension(700, 840);

    private static final Dimension SIZE_KHONG_MON_CHI_TIET = new Dimension(700, 660);
    private static final Dimension SIZE_CO_MON_CHI_TIET = new Dimension(700, 840);

    private static final String PH_KHACH = "Nhập tên khách hàng";
    private static final String PH_SDT = "Nhập số điện thoại";
    private static final String PH_GIO = "Chọn ngày giờ";
    private static final String PH_BAN = "Tìm kiếm bàn...";
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();

 
    public PhieuDatBan_DigLog(Frame owner) {
        super(owner, "Phiếu đặt bàn", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 760));
        setSize(700, 820);
        setLocationRelativeTo(owner);

        initUI();
        initEvents();
        loadDanhSachBanTheoGioChon();
        setModeThemMoi();
        loadMonDatTruocLenBang();
        capNhatTienCocTheoMonDatTruoc();
    }

    public PhieuDatBan_DigLog(Frame owner, String maPhieuDatBan, TaiKhoan tk) {
        super(owner, "Phiếu đặt bàn", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 720));
        setSize(700, 800);
        setLocationRelativeTo(owner);

        this.maPhieuHienTai = maPhieuDatBan;
        this.cheDoChiTiet = true;
        this.taiKhoanDangNhap = tk;

        initUI();
        initEvents();
        setModeChiTiet();
        loadPhieuDatBanLenForm(maPhieuDatBan);
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_MAIN);
        setContentPane(contentPane);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(BG_MAIN);
        pnlTitle.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel("Phiếu đặt bàn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLACK);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 1));
        line.setBackground(new Color(150, 150, 150));

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setBackground(BG_MAIN);
        topWrap.add(pnlTitle, BorderLayout.CENTER);
        topWrap.add(line, BorderLayout.SOUTH);
        contentPane.add(topWrap, BorderLayout.NORTH);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(BG_MAIN);
        formWrap.setBorder(new EmptyBorder(12, 18, 10, 18));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_MAIN);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        formWrap.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(formWrap, BorderLayout.CENTER);

        Font lblFont = new Font("Arial", Font.PLAIN, 16);
        Font inputFont = new Font("Arial", Font.PLAIN, 15);

        txtMaPhieu = createTextField("", inputFont);
        txtMaPhieu.setEditable(false);
        txtMaPhieu.setBackground(DISABLED_BG);
        txtMaPhieu.setForeground(Color.DARK_GRAY);

        txtKhachHang = createTextField("", inputFont);
        datPlaceholder(txtKhachHang, PH_KHACH);

        txtSoDienThoai = createTextField("", inputFont);
        datPlaceholder(txtSoDienThoai, PH_SDT);

        txtGioKhachVao = createTextField("", inputFont);
        txtGioKhachVao.setEditable(false);
        txtGioKhachVao.setBackground(Color.WHITE);
        datPlaceholder(txtGioKhachVao, PH_GIO);

        spnSoLuongKhach = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnSoLuongKhach.setFont(inputFont);
        spnSoLuongKhach.setPreferredSize(new Dimension(300, 36));
        spnSoLuongKhach.setBorder(new LineBorder(BORDER, 1));

        JComponent editor = spnSoLuongKhach.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField txt = ((JSpinner.DefaultEditor) editor).getTextField();
            txt.setFont(inputFont);
            txt.setHorizontalAlignment(SwingConstants.LEFT);
            txt.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        txtBan = createTextField("", inputFont);
        txtBan.setToolTipText("Bấm để chọn bàn");
        datPlaceholder(txtBan, PH_BAN);

        txtTienCoc = createTextField("200.000", inputFont);
        cboPTThanhToanCoc = new javax.swing.JComboBox<>(new String[]{
                "Tiền mặt", "Chuyển khoản", "VISA"
        });
        cboPTThanhToanCoc.setFont(inputFont);
        cboPTThanhToanCoc.setPreferredSize(new Dimension(300, 36));
        cboPTThanhToanCoc.setBorder(new LineBorder(BORDER, 1));
        cboPTThanhToanCoc.setBackground(Color.WHITE);
        txtTienCoc.setEditable(false);
        txtTienCoc.setBackground(DISABLED_BG);
        txtTienCoc.setForeground(Color.DARK_GRAY);

        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setFont(inputFont);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(new EmptyBorder(8, 10, 8, 10));
        txtGhiChu.setBackground(Color.WHITE);

        btnCalendar = createIconButton("📅", 14);
        btnSearchBan = createIconButton("⌕", 25);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addFormRow(formPanel, gbc, row++, "Mã đặt bàn", lblFont, wrapField(txtMaPhieu, null));
        addFormRow(formPanel, gbc, row++, "Khách hàng", lblFont, wrapField(txtKhachHang, null));
        addFormRow(formPanel, gbc, row++, "Điện thoại khách hàng", lblFont, wrapField(txtSoDienThoai, null));
        addFormRow(formPanel, gbc, row++, "Giờ khách vào", lblFont, wrapField(txtGioKhachVao, btnCalendar));
        addFormRow(formPanel, gbc, row++, "Số lượng khách", lblFont, spnSoLuongKhach);
        addFormRow(formPanel, gbc, row++, "Bàn", lblFont, wrapField(txtBan, btnSearchBan));
        addFormRow(formPanel, gbc, row++, "Tiền cọc", lblFont, wrapField(txtTienCoc, null));
        addFormRow(formPanel, gbc, row++, "PT thanh toán cọc", lblFont, cboPTThanhToanCoc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.34;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(createLabel("Ghi chú", lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.66;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane scrGhiChu = new JScrollPane(txtGhiChu);
        scrGhiChu.setPreferredSize(new Dimension(300, 80));
        scrGhiChu.setBorder(new LineBorder(BORDER, 1));
        formPanel.add(scrGhiChu, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.34;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        lblMonDatTruoc = createLabel("Món đặt trước ✍", lblFont);
        lblMonDatTruoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblMonDatTruoc.setForeground(new Color(30, 30, 30));
        formPanel.add(lblMonDatTruoc, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.66;
        gbc.fill = GridBagConstraints.BOTH;

        modelMonDatTruoc = new DefaultTableModel(
                new String[] { "Tên món", "Số lượng" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMonDatTruoc = new JTable(modelMonDatTruoc);
        tblMonDatTruoc.setRowHeight(30);
        tblMonDatTruoc.setFont(new Font("Arial", Font.PLAIN, 14));
        tblMonDatTruoc.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblMonDatTruoc.getTableHeader().setReorderingAllowed(false);
        tblMonDatTruoc.setShowGrid(true);
        tblMonDatTruoc.setGridColor(new Color(190, 190, 190));
        tblMonDatTruoc.setIntercellSpacing(new Dimension(1, 1));
        tblMonDatTruoc.setSelectionBackground(new Color(230, 240, 250));
        tblMonDatTruoc.setRowSelectionAllowed(false);
        tblMonDatTruoc.setCellSelectionEnabled(false);
        tblMonDatTruoc.setFillsViewportHeight(true);

        tblMonDatTruoc.getColumnModel().getColumn(0).setPreferredWidth(220);
        tblMonDatTruoc.getColumnModel().getColumn(0).setMinWidth(180);

        tblMonDatTruoc.getColumnModel().getColumn(1).setPreferredWidth(75);
        tblMonDatTruoc.getColumnModel().getColumn(1).setMinWidth(55);
        tblMonDatTruoc.getColumnModel().getColumn(1).setMaxWidth(75);

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblMonDatTruoc.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        javax.swing.table.DefaultTableCellRenderer headerRenderer =
                (javax.swing.table.DefaultTableCellRenderer) tblMonDatTruoc.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        scrMonDatTruoc = new JScrollPane(tblMonDatTruoc);
        scrMonDatTruoc.setPreferredSize(new Dimension(300, 150));
        scrMonDatTruoc.setBorder(new LineBorder(BORDER, 1));
        scrMonDatTruoc.getViewport().setBackground(Color.WHITE);
        scrMonDatTruoc.setVisible(false);

        formPanel.add(scrMonDatTruoc, gbc);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlButtons.setBackground(BG_MAIN);

        btnHuy = new ColoredButton("Hủy", new Color(232, 83, 83));
        btnHuy.setFont(new Font("Arial", Font.PLAIN, 16));
        btnHuy.setPreferredSize(new Dimension(92, 40));

        btnThoat = new ColoredButton("Thoát", new Color(219, 167, 69));
        btnThoat.setFont(new Font("Arial", Font.PLAIN, 16));
        btnThoat.setPreferredSize(new Dimension(92, 40));

        btnNhanBan = new ColoredButton("Nhận bàn", new Color(116, 191, 102));
        btnNhanBan.setFont(new Font("Arial", Font.PLAIN, 16));
        btnNhanBan.setPreferredSize(new Dimension(120, 40));

        btnLuu = new ColoredButton("Lưu", new Color(74, 144, 206));
        btnLuu.setFont(new Font("Arial", Font.PLAIN, 16));
        btnLuu.setPreferredSize(new Dimension(92, 40));

        btnDatBan = new ColoredButton("Đặt bàn", new Color(74, 144, 206));
        btnDatBan.setFont(new Font("Arial", Font.PLAIN, 16));
        btnDatBan.setPreferredSize(new Dimension(110, 40));

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnThoat);
        pnlButtons.add(btnNhanBan);
        pnlButtons.add(btnLuu);
        pnlButtons.add(btnDatBan);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 8, 6, 8);

        JPanel pnlBottomWrap = new JPanel(new BorderLayout());
        pnlBottomWrap.setOpaque(false);
        pnlBottomWrap.add(pnlButtons, BorderLayout.EAST);
        formPanel.add(pnlBottomWrap, gbc);

        initPopupBan();
        chanNhapKhacChoSoDienThoai();
    }
    private void capNhatKichThuocTheoMonDatTruoc(boolean coMon) {
        Dimension size;

        if (cheDoChiTiet) {
            size = coMon ? SIZE_CO_MON_CHI_TIET : SIZE_KHONG_MON_CHI_TIET;
        } else {
            size = coMon ? SIZE_CO_MON_THEM : SIZE_KHONG_MON_THEM;
        }

        setSize(size);
        setMinimumSize(new Dimension(680, 640));
        setLocationRelativeTo(getOwner());
    }

    private void setModeChiTiet() {
        btnDatBan.setVisible(false);
        btnHuy.setVisible(true);
        btnNhanBan.setVisible(true);
        btnLuu.setVisible(true);
    }

    private void setModeThemMoi() {
        btnDatBan.setVisible(true);
        btnHuy.setVisible(false);
        btnNhanBan.setVisible(false);
        btnLuu.setVisible(false);

        trangThaiHienTai = "Đang chờ";
        setEditableThongTin(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Font lblFont, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.34;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabel(label, lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.66;
        panel.add(comp, gbc);
    }

    private void loadMonDatTruocLenBang() {
        if (modelMonDatTruoc == null || scrMonDatTruoc == null) return;

        modelMonDatTruoc.setRowCount(0);

        if (dsMonDatTam == null || dsMonDatTam.isEmpty()) {
            scrMonDatTruoc.setVisible(false);
            scrMonDatTruoc.setPreferredSize(new Dimension(300, 0));
            scrMonDatTruoc.setMinimumSize(new Dimension(300, 0));

            capNhatKichThuocTheoMonDatTruoc(false);

            revalidate();
            repaint();
            return;
        }

        for (PhieuDatMon pdm : dsMonDatTam) {
            String tenMon = "";
            if (pdm.getMaMon() != null) {
                tenMon = pdm.getMaMon().getTenMon();
                if (tenMon == null || tenMon.trim().isEmpty()) {
                    tenMon = pdm.getMaMon().getMaMon();
                }
            }

            modelMonDatTruoc.addRow(new Object[] {
                    tenMon,
                    pdm.getSoLuong()
            });
        }

        boolean coMon = modelMonDatTruoc.getRowCount() > 0;
        scrMonDatTruoc.setVisible(coMon);

        if (coMon) {
            int soDong = modelMonDatTruoc.getRowCount();
            int rowHeight = tblMonDatTruoc.getRowHeight();
            int headerHeight = tblMonDatTruoc.getTableHeader().getPreferredSize().height;

            int chieuCaoBang = headerHeight + (Math.min(soDong, 4) * rowHeight) + 6;
            chieuCaoBang = Math.max(90, Math.min(chieuCaoBang, 140));

            scrMonDatTruoc.setPreferredSize(new Dimension(300, chieuCaoBang));
            scrMonDatTruoc.setMinimumSize(new Dimension(300, chieuCaoBang));
            scrMonDatTruoc.setBorder(new LineBorder(new Color(150, 150, 150), 1, true));
        } else {
            scrMonDatTruoc.setPreferredSize(new Dimension(300, 0));
            scrMonDatTruoc.setMinimumSize(new Dimension(300, 0));
            scrMonDatTruoc.setBorder(new LineBorder(BORDER, 1));
        }

        capNhatKichThuocTheoMonDatTruoc(coMon);

        revalidate();
        repaint();
    }

    private void capNhatTienCocTheoMonDatTruoc() {
        double tongTienMon = 0;

        if (dsMonDatTam != null) {
            for (PhieuDatMon pdm : dsMonDatTam) {
                tongTienMon += pdm.getSoLuong() * pdm.getDonGia();
            }
        }

        double tienCoc = Math.max(200000, tongTienMon);
        txtTienCoc.setText(formatTienVND(tienCoc));
    }

    private String formatTienVND(double soTien) {
        return String.format("%,.0f", soTien).replace(",", ".");
    }

    private double parseMoney(String text) {
        try {
            if (text == null) return 0;
            String value = text.replace("VNĐ", "")
                    .replace("đ", "")
                    .replace("Đ", "")
                    .replace(".", "")
                    .replace(",", "")
                    .trim();
            if (value.isEmpty()) return 0;
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean phieuDaHuy() {
        return trangThaiHienTai != null && trangThaiHienTai.trim().equalsIgnoreCase("Đã hủy");
    }

    private boolean phieuDaNhanBan() {
        return trangThaiHienTai != null && trangThaiHienTai.trim().equalsIgnoreCase("Đã nhận bàn");
    }

    private boolean phieuHoanThanh() {
        return trangThaiHienTai != null && trangThaiHienTai.trim().equalsIgnoreCase("Hoàn thành");
    }

    private boolean phieuQuaGio() {
        return trangThaiHienTai != null && trangThaiHienTai.trim().equalsIgnoreCase("Quá giờ");
    }

    private boolean coDatMonTheoPhieu() {
        try {
            if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) return false;

            if (dsMonDatTam != null && !dsMonDatTam.isEmpty()) return true;

            PhieuDatMon_DAO dao = new PhieuDatMon_DAO();
            return dao.coDatMonTheoPhieu(maPhieuHienTai);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(font);
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JTextField createTextField(String text, Font font) {
        JTextField txt = new JTextField(text);
        txt.setFont(font);
        txt.setPreferredSize(new Dimension(300, 36));
        txt.setBorder(new LineBorder(BORDER, 1));
        txt.setBackground(Color.WHITE);
        txt.setMargin(new Insets(0, 10, 0, 10));
        return txt;
    }

    private JPanel wrapField(JTextField txt, JButton rightButton) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 36));

        if (rightButton == null) {
            panel.add(txt, BorderLayout.CENTER);
        } else {
            txt.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, BORDER));
            rightButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER));
            panel.add(txt, BorderLayout.CENTER);
            panel.add(rightButton, BorderLayout.EAST);
        }
        return panel;
    }

    private JButton createIconButton(String text, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void datPlaceholder(JTextField txt, String placeholder) {
        txt.setText(placeholder);
        txt.setForeground(PLACEHOLDER);

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!txt.isEditable()) return;

                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!txt.isEditable()) return;

                if (txt.getText().trim().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(PLACEHOLDER);
                }
            }
        });
    }

    private boolean isPlaceholder(JTextField txt, String placeholder) {
        return txt.getText().trim().equals(placeholder);
    }

    private void initEvents() {
        btnThoat.addActionListener(e -> dispose());
        txtSoDienThoai.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                tuDongDienKhachHangTheoSDT();
            }
        });
        spnSoLuongKhach.addChangeListener(e -> {
            if (thoiGianDaChon != null) {
                loadDanhSachBanTheoGioChon();
            }
        });

        txtSoDienThoai.addActionListener(e -> tuDongDienKhachHangTheoSDT());

        btnCalendar.addActionListener(e -> {
            if (!btnCalendar.isEnabled()) return;
            moDialogChonNgayGio();
        });

        btnSearchBan.addActionListener(e -> {
            if (!btnSearchBan.isEnabled()) return;

            if (thoiGianDaChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày giờ trước!");
                return;
            }
            loadDanhSachBanTheoGioChon();
            showPopupBan();
        });

        txtBan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!txtBan.isEnabled() || !txtBan.isEditable()) return;

                if (thoiGianDaChon == null) {
                    JOptionPane.showMessageDialog(PhieuDatBan_DigLog.this, "Vui lòng chọn ngày giờ trước!");
                    return;
                }

                if (isPlaceholder(txtBan, PH_BAN)) {
                    txtBan.setText("");
                    txtBan.setForeground(Color.BLACK);
                }

                loadDanhSachBanTheoGioChon();
                locBan(txtBan.getText().trim());
                showPopupBan();
            }
        });

        txtBan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtBan.isEnabled() || !txtBan.isEditable()) return;
                if (thoiGianDaChon == null) return;

                locBan(isPlaceholder(txtBan, PH_BAN) ? "" : txtBan.getText().trim());
                showPopupBan();
            }
        });

        if (lblMonDatTruoc != null) {
            lblMonDatTruoc.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (lblMonDatTruoc.isEnabled()) {
                        moDatMonTruoc();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (lblMonDatTruoc.isEnabled()) {
                        lblMonDatTruoc.setForeground(new Color(40, 120, 220));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lblMonDatTruoc.setForeground(new Color(30, 30, 30));
                }
            });
        }

        btnDatBan.addActionListener(e -> {
            if (!btnDatBan.isEnabled()) return;
            datBanMoi();
        });

        btnHuy.addActionListener(e -> {
            if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) return;

            try {
                boolean daHuy = trangThaiHienTai != null
                        && trangThaiHienTai.trim().equalsIgnoreCase("Đã hủy");

                Frame ownerFrame = null;
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w instanceof Frame) {
                    ownerFrame = (Frame) w;
                }

                boolean coDatMon = coDatMonTheoPhieu();
                double tienCoc = parseMoney(txtTienCoc.getText());

                HuyBan_DigLog dialog = new HuyBan_DigLog(
                        ownerFrame,
                        maPhieuHienTai,
                        tienCoc,
                        thoiGianDaChon,
                        coDatMon,
                        daHuy
                );
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

                if (!daHuy && dialog.isHuyThanhCong()) {
                    Ban_DAO banDAO = new Ban_DAO();
                    if (maBanDuocChon != null && !maBanDuocChon.trim().isEmpty()) {
                        banDAO.capNhatTrangThaiBan(maBanDuocChon, "Trống");
                    }

                    trangThaiHienTai = "Đã hủy";
                    JOptionPane.showMessageDialog(this, "Hủy phiếu thành công!");
                    dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Có lỗi khi xử lý hủy phiếu!");
            }
        });

        btnNhanBan.addActionListener(e -> {
            try {
                if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không có phiếu đặt bàn.");
                    return;
                }

                // 1. Cập nhật trạng thái phiếu
                PhieuDatBan_DAO phieuDAO = new PhieuDatBan_DAO();
                phieuDAO.capNhatTrangThai(maPhieuHienTai, "Đã nhận bàn");

                // 2. Cập nhật trạng thái bàn
                Ban_DAO banDAO = new Ban_DAO();
                banDAO.capNhatTrangThaiBan(maBanDuocChon, "Đang phục vụ");

                // 3. Tạo hóa đơn ngay
                HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
                String maHD = hoaDonDAO.taoMaHoaDonMoi();

                String maNV = null;
                if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null) {
                    maNV = taiKhoanDangNhap.getMaNV().getMaNV();
                }

                boolean taoHD = hoaDonDAO.themHoaDonMoi(
                        maHD,
                        maBanDuocChon,
                        maNV,
                        maPhieuHienTai,
                        null,
                        "Tại bàn",
                        "Chưa thanh toán"
                );

                if (!taoHD) {
                    JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại!");
                    return;
                }

                // 4. Lấy món đặt trước
                PhieuDatMon_DAO phieuDatMonDAO = new PhieuDatMon_DAO();
                ArrayList<PhieuDatMon> dsMon = phieuDatMonDAO.getDanhSachTheoMaPhieu(maPhieuHienTai);

                ChiTietHoaDon_DAO ctDAO = new ChiTietHoaDon_DAO();

                for (PhieuDatMon pdm : dsMon) {
                    ChiTietHoaDon ct = new ChiTietHoaDon(
                            new HoaDon(maHD),
                            new MonAn(pdm.getMaMon().getMaMon()),
                            pdm.getSoLuong(),
                            pdm.getDonGia(),
                            pdm.getGhiChu() == null ? "" : pdm.getGhiChu(),
                            "Đang phục vụ",
                            null,
                            0,
                            null
                    );

                    ctDAO.themChiTietHoaDon(ct);
                }

                dispose();

                Window w = getOwner();
                if (w instanceof TrangChu_GUI) {
                    ((TrangChu_GUI) w).showCustomPage(
                            "Order_Mon_GUI",
                            new Order_Mon_GUI(
                                    taiKhoanDangNhap,
                                    maBanDuocChon,
                                    txtBan.getText().trim(),
                                    maPhieuHienTai,
                                    true
                            )
                    );
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi nhận bàn!");
            }
        });

        btnLuu.addActionListener(e -> {
            if (!btnLuu.isEnabled()) return;
            capNhatPhieuDatBan();
        });
    }

    private void moDatMonTruoc() {
        try {
            DatMon_DigLog dlg = new DatMon_DigLog((Frame) getOwner(), dsMonDatTam);
            dlg.setLocationRelativeTo(this);

            dlg.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    dsMonDatTam = dlg.getDanhSachMonTam();
                    loadMonDatTruocLenBang();
                    capNhatTienCocTheoMonDatTruoc();
                    toFront();
                    requestFocus();
                }
            });

            dlg.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không mở được màn hình đặt món trước!");
        }
    }

    private void datBanMoi() {
        if (!validateForm()) {
            return;
        }
        themKhachHangNeuChuaCo();

        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();

            boolean biTrung = dao.kiemTraTrungLich(maBanDuocChon, thoiGianDaChon, 120);
            if (biTrung) {
                JOptionPane.showMessageDialog(
                        this,
                        "Bàn này đã trùng lịch đặt, vui lòng chọn lại bàn khác!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );
                loadDanhSachBanTheoGioChon();
                return;
            }

            String tienCocText = txtTienCoc.getText().trim()
                    .replace(".", "")
                    .replace(",", "")
                    .replace("đ", "")
                    .replace("Đ", "");

            BigDecimal tienCoc = new BigDecimal(tienCocText);

            String phuongThucCoc = cboPTThanhToanCoc.getSelectedItem().toString();

            String maPhieuMoi = dao.themPhieuDatBan(
                    maBanDuocChon,
                    txtKhachHang.getText().trim(),
                    txtSoDienThoai.getText().trim(),
                    (Integer) spnSoLuongKhach.getValue(),
                    thoiGianDaChon,
                    tienCoc,
                    txtGhiChu.getText().trim(),
                    "Đang chờ",
                    phuongThucCoc
            );

            if (maPhieuMoi != null) {
                maPhieuHienTai = maPhieuMoi;
                txtMaPhieu.setText(maPhieuMoi);

                PhieuDatMon_DAO pdmDAO = new PhieuDatMon_DAO();
                boolean luuMonOK = pdmDAO.luuDanhSachMonChoPhieu(maPhieuMoi, dsMonDatTam);

                Ban_DAO banDAO = new Ban_DAO();
                boolean capNhatBanOK = banDAO.capNhatTrangThaiBan(maBanDuocChon, "Đang chờ");

                if (luuMonOK && capNhatBanOK) {
                    
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Lưu phiếu thành công nhưng lưu món hoặc cập nhật trạng thái bàn thất bại!",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Lưu phiếu đặt bàn thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Có lỗi khi lưu phiếu đặt bàn!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    

    private void capNhatPhieuDatBan() {
        if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) return;

        if (!validateForm()) {
            return;
        }

        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();

            boolean biTrung = dao.kiemTraTrungLich(maBanDuocChon, thoiGianDaChon, 120);
            if (biTrung) {
                String[] oldData = dao.timTheoMaPhieu(maPhieuHienTai);
                if (oldData != null) {
                    Timestamp oldTime = Timestamp.valueOf(oldData[5]);
                    String oldMaBan = oldData[1];
                    if (!(oldMaBan.equalsIgnoreCase(maBanDuocChon) && oldTime.equals(thoiGianDaChon))) {
                        JOptionPane.showMessageDialog(this, "Bàn này đã trùng lịch đặt!");
                        return;
                    }
                }
            }

            String tienCocText = txtTienCoc.getText().trim()
                    .replace(".", "")
                    .replace(",", "")
                    .replace("đ", "")
                    .replace("Đ", "");

            BigDecimal tienCoc = new BigDecimal(tienCocText);

            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE PhieuDatBan "
                    + "SET maBan = ?, tenKhach = ?, sdt = ?, soLuongNguoi = ?, thoiGianDen = ?, tienCoc = ?, ghiChu = ? "
                    + "WHERE maPhieuDatBan = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBanDuocChon);
            ps.setString(2, txtKhachHang.getText().trim());
            ps.setString(3, txtSoDienThoai.getText().trim());
            ps.setInt(4, (Integer) spnSoLuongKhach.getValue());
            ps.setTimestamp(5, thoiGianDaChon);
            ps.setBigDecimal(6, tienCoc);
            ps.setString(7, txtGhiChu.getText().trim());
            ps.setString(8, maPhieuHienTai);

            int n = ps.executeUpdate();
            ps.close();

            if (n > 0) {
                PhieuDatMon_DAO pdmDAO = new PhieuDatMon_DAO();
                pdmDAO.luuDanhSachMonChoPhieu(maPhieuHienTai, dsMonDatTam);

                Ban_DAO banDAO = new Ban_DAO();
                banDAO.capNhatTrangThaiBan(maBanDuocChon, "Đang chờ");

                JOptionPane.showMessageDialog(this, "Lưu cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu cập nhật thất bại!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi khi cập nhật phiếu!");
        }
    }

    private void loadPhieuDatBanLenForm(String maPhieu) {
        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
            String[] row = dao.timTheoMaPhieu(maPhieu);

            if (row == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn!");
                return;
            }

            txtMaPhieu.setText(row[0]);
            maPhieuHienTai = row[0];
            maBanDuocChon = row[1];

            txtKhachHang.setText(row[2]);
            txtKhachHang.setForeground(Color.BLACK);

            txtSoDienThoai.setText(row[3]);
            txtSoDienThoai.setForeground(Color.BLACK);

            spnSoLuongKhach.setValue(Integer.parseInt(row[4]));

            Timestamp tg = Timestamp.valueOf(row[5]);
            thoiGianDaChon = tg;
            txtGioKhachVao.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(tg));
            txtGioKhachVao.setForeground(Color.BLACK);

            txtTienCoc.setText(formatTienCoc(row[6]));
            txtGhiChu.setText(row[7] == null ? "" : row[7]);

            if (row.length > 8 && row[8] != null) {
                trangThaiHienTai = row[8].trim();
            } else {
                trangThaiHienTai = "";
            }

            PhieuDatMon_DAO pdmDAO = new PhieuDatMon_DAO();
            ArrayList<PhieuDatMon> dsDB = pdmDAO.getDanhSachTheoMaPhieu(maPhieu);
            dsMonDatTam.clear();

            if (dsDB != null && !dsDB.isEmpty()) {
                MonAn_DAO monDAO = new MonAn_DAO();
                for (PhieuDatMon pdm : dsDB) {
                    try {
                        MonAn mon = monDAO.getMonAnTheoMa(pdm.getMaMon().getMaMon());
                        if (mon != null) {
                            pdm.setMaMon(mon);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    dsMonDatTam.add(pdm);
                }
            }

            loadDanhSachBanTheoGioChon();
            boolean daGanTenBan = false;

            for (String[] ban : dsBanTheoGio) {
                if (ban[0].equalsIgnoreCase(maBanDuocChon)) {
                    txtBan.setText(ban[1]);
                    txtBan.setForeground(Color.BLACK);
                    daGanTenBan = true;
                    break;
                }
            }

            if (!daGanTenBan) {
                try {
                    Ban_DAO banDAO = new Ban_DAO();
                    ArrayList<String[]> tatCaBan = banDAO.getTatCaBanKemTrangThaiMacDinh();
                    for (String[] ban : tatCaBan) {
                        if (ban[0].equalsIgnoreCase(maBanDuocChon)) {
                            txtBan.setText(ban[1]);
                            txtBan.setForeground(Color.BLACK);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            loadMonDatTruocLenBang();

            if (dsMonDatTam == null || dsMonDatTam.isEmpty()) {
                txtTienCoc.setText(formatTienCoc(row[6]));
            } else {
                capNhatTienCocTheoMonDatTruoc();
            }

            apDungTrangThaiForm(trangThaiHienTai);
            SwingUtilities.invokeLater(() -> kiemTraPhieuDangMoCoQuaGio());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải phiếu đặt bàn!");
        }
    }

    private void apDungTrangThaiForm(String trangThai) {
        String tt = trangThai == null ? "" : trangThai.trim().toLowerCase();

        boolean choSuaThongTin = false;
        boolean choLuu = false;
        boolean choHuy = false;
        boolean choNhanBan = false;

        if (tt.equals("đang chờ")) {
            choSuaThongTin = true;
            choLuu = true;
            choHuy = true;
            choNhanBan = true;
        } else if (tt.equals("hoàn thành")) {
            choSuaThongTin = false;
            choLuu = false;
            choHuy = false;
            choNhanBan = false;
        } else if (tt.equals("đã nhận bàn")) {
            choSuaThongTin = false;
            choLuu = false;
            choHuy = false;
            choNhanBan = false;
        } else if (tt.equals("đã hủy") || tt.equals("quá giờ")) {
            choSuaThongTin = false;
            choLuu = false;
            choHuy = false;
            choNhanBan = false;
        } else {
            choSuaThongTin = false;
            choLuu = false;
            choHuy = false;
            choNhanBan = false;
        }

        setEditableThongTin(choSuaThongTin);
        setButtonEnabledVisual(btnLuu, choLuu);
        setButtonEnabledVisual(btnHuy, choHuy);
        setButtonEnabledVisual(btnNhanBan, choNhanBan);

        if (tt.equals("đã hủy")) {
            btnHuy.setText("Đã hủy");
        } else {
            btnHuy.setText("Hủy");
        }
    }

    private void setEditableThongTin(boolean editable) {
        setTextFieldEditable(txtKhachHang, editable, PH_KHACH);
        setTextFieldEditable(txtSoDienThoai, editable, PH_SDT);
        setTextFieldEditable(txtBan, editable, PH_BAN);

        spnSoLuongKhach.setEnabled(editable);
        txtGhiChu.setEditable(editable);
        txtGhiChu.setEnabled(true);
        txtGhiChu.setBackground(editable ? Color.WHITE : DISABLED_BG);
        txtGhiChu.setForeground(Color.BLACK);

        txtGioKhachVao.setBackground(editable ? Color.WHITE : DISABLED_BG);
        txtGioKhachVao.setForeground(
                (txtGioKhachVao.getText().trim().isEmpty() || txtGioKhachVao.getText().trim().equals(PH_GIO))
                        ? PLACEHOLDER : Color.BLACK
        );

        btnCalendar.setEnabled(editable);
        btnSearchBan.setEnabled(editable);

        lblMonDatTruoc.setEnabled(editable);

        styleSmallButton(btnCalendar, editable);
        styleSmallButton(btnSearchBan, editable);
    }

    private void setTextFieldEditable(JTextField txt, boolean editable, String placeholder) {
        txt.setEditable(editable);
        txt.setEnabled(true);

        String value = txt.getText().trim();

        if (editable) {
            txt.setBackground(Color.WHITE);

            if (value.isEmpty()) {
                txt.setText(placeholder);
                txt.setForeground(PLACEHOLDER);
            } else if (!value.equals(placeholder)) {
                txt.setForeground(Color.BLACK);
            }
        } else {
            txt.setBackground(DISABLED_BG);

            if (value.isEmpty()) {
                txt.setText(placeholder);
                txt.setForeground(PLACEHOLDER);
            } else if (!value.equals(placeholder)) {
                txt.setForeground(Color.BLACK);
            }
        }
    }

    private void styleSmallButton(JButton btn, boolean enabled) {
        btn.setEnabled(enabled);
        btn.setCursor(new Cursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        btn.setBackground(enabled ? Color.WHITE : DISABLED_BG);
        btn.setForeground(enabled ? Color.BLACK : new Color(130, 130, 130));
    }

    private void setButtonEnabledVisual(JButton button, boolean enabled) {
        if (button == null) return;
        button.setEnabled(enabled);
        button.setCursor(new Cursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        button.repaint();
    }

    private String formatTienCoc(String value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            return String.format("%,.0f", bd.doubleValue()).replace(',', '.');
        } catch (Exception e) {
            return value;
        }
    }

    private void chanNhapKhacChoSoDienThoai() {
        txtSoDienThoai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!txtSoDienThoai.isEditable()) {
                    e.consume();
                    return;
                }

                if (isPlaceholder(txtSoDienThoai, PH_SDT)) {
                    txtSoDienThoai.setText("");
                    txtSoDienThoai.setForeground(Color.BLACK);
                }

                char c = e.getKeyChar();
                String current = txtSoDienThoai.getText();

                if (!Character.isDigit(c) || current.length() >= 10) {
                    e.consume();
                }
            }
        });
    }

    private void moDialogChonNgayGio() {
        JDialog dlg = new JDialog(this, "Chọn ngày giờ", true);
        dlg.setSize(340, 210);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNgay = new JLabel("Ngày:");
        lblNgay.setFont(new Font("Arial", Font.PLAIN, 14));

        JDateChooser chooser = new JDateChooser();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date homNay = cal.getTime();

        chooser.setDate(new Date());
        chooser.setMinSelectableDate(homNay);
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setLocale(new Locale("vi", "VN"));
        chooser.setPreferredSize(new Dimension(170, 32));

        JLabel lblGio = new JLabel("Giờ:");
        lblGio.setFont(new Font("Arial", Font.PLAIN, 14));

        String[] dsGio = new String[14];
        for (int i = 0; i < 14; i++) {
            dsGio[i] = String.format("%02d", i + 9);
        }

        String[] dsPhut = new String[60];
        for (int i = 0; i < 60; i++) {
            dsPhut[i] = String.format("%02d", i);
        }

        javax.swing.JComboBox<String> cboGio = new javax.swing.JComboBox<>(dsGio);
        javax.swing.JComboBox<String> cboPhut = new javax.swing.JComboBox<>(dsPhut);

        cboGio.setFont(new Font("Arial", Font.PLAIN, 14));
        cboPhut.setFont(new Font("Arial", Font.PLAIN, 14));

        cboGio.setPreferredSize(new Dimension(70, 32));
        cboPhut.setPreferredSize(new Dimension(70, 32));

        Calendar nowPlus30p = Calendar.getInstance();
        nowPlus30p.add(Calendar.MINUTE, 30);

        int gioMacDinh = nowPlus30p.get(Calendar.HOUR_OF_DAY);
        int phutMacDinh = nowPlus30p.get(Calendar.MINUTE);

        if (gioMacDinh < 9) {
            gioMacDinh = 9;
            phutMacDinh = 0;
        } else if (gioMacDinh > 22) {
            gioMacDinh = 22;
            phutMacDinh = 0;
        }

        cboGio.setSelectedItem(String.format("%02d", gioMacDinh));
        cboPhut.setSelectedItem(String.format("%02d", phutMacDinh));

        JPanel pnlGioPhut = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlGioPhut.setBackground(Color.WHITE);
        pnlGioPhut.add(cboGio);
        pnlGioPhut.add(new JLabel(":"));
        pnlGioPhut.add(cboPhut);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblNgay, gbc);

        gbc.gridx = 1;
        panel.add(chooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblGio, gbc);

        gbc.gridx = 1;
        panel.add(pnlGioPhut, gbc);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtn.setBackground(Color.WHITE);

        JButton btnOK = new JButton("Chọn");
        JButton btnCancel = new JButton("Hủy");

        pnlBtn.add(btnCancel);
        pnlBtn.add(btnOK);

        dlg.add(panel, BorderLayout.CENTER);
        dlg.add(pnlBtn, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dlg.dispose());

        btnOK.addActionListener(e -> {
            if (chooser.getDate() == null) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày!");
                return;
            }

            Date ngay = chooser.getDate();

            int gio = Integer.parseInt(cboGio.getSelectedItem().toString());
            int phut = Integer.parseInt(cboPhut.getSelectedItem().toString());

            Calendar calNgay = Calendar.getInstance();
            calNgay.setTime(ngay);
            calNgay.set(Calendar.HOUR_OF_DAY, gio);
            calNgay.set(Calendar.MINUTE, phut);
            calNgay.set(Calendar.SECOND, 0);
            calNgay.set(Calendar.MILLISECOND, 0);

            Timestamp tgChon = new Timestamp(calNgay.getTimeInMillis());

            Calendar homNayCal = Calendar.getInstance();
            Calendar ngayChon = Calendar.getInstance();
            ngayChon.setTime(ngay);

            boolean cungNgay = homNayCal.get(Calendar.YEAR) == ngayChon.get(Calendar.YEAR)
                    && homNayCal.get(Calendar.DAY_OF_YEAR) == ngayChon.get(Calendar.DAY_OF_YEAR);

            if (cungNgay) {
            	Timestamp mocToiThieu = new Timestamp(nowPlus30p.getTimeInMillis());

            	if (tgChon.before(mocToiThieu)) {
            	    JOptionPane.showMessageDialog(
            	            dlg,
            	            "Nếu đặt trong hôm nay thì giờ vào phải sau thời điểm hiện tại ít nhất 30 phút!"
            	    );
            	    return;
            	}
            }

            thoiGianDaChon = tgChon;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            txtGioKhachVao.setForeground(Color.BLACK);
            txtGioKhachVao.setText(sdf.format(thoiGianDaChon));

            txtBan.setText(PH_BAN);
            txtBan.setForeground(PLACEHOLDER);
            maBanDuocChon = "";

            loadDanhSachBanTheoGioChon();
            dlg.dispose();
        });

        dlg.setVisible(true);
    }

    private void initPopupBan() {
        popupBan = new JPopupMenu();
        popupBan.setLayout(new BorderLayout());

        modelBan = new DefaultTableModel(new Object[]{"Tên bàn", "Số chỗ", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBan = new JTable(modelBan);
        tblBan.setRowHeight(24);
        tblBan.setFont(new Font("Arial", Font.PLAIN, 13));
        tblBan.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tblBan);
        scroll.setPreferredSize(new Dimension(300, 160));
        popupBan.add(scroll, BorderLayout.CENTER);

        tblBan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!txtBan.isEditable() || !txtBan.isEnabled()) return;

                int row = tblBan.getSelectedRow();
                if (row >= 0) {
                    txtBan.setForeground(Color.BLACK);
                    txtBan.setText(String.valueOf(modelBan.getValueAt(row, 0)));

                    String tenBanDangChon = String.valueOf(modelBan.getValueAt(row, 0));
                    maBanDuocChon = "";

                    for (String[] ban : dsBanTheoGio) {
                        if (ban[1].equalsIgnoreCase(tenBanDangChon)) {
                            maBanDuocChon = ban[0];
                            break;
                        }
                    }

                    popupBan.setVisible(false);
                }
            }
        });
    }

    private void loadDanhSachBanTheoGioChon() {
        if (modelBan == null) return;

        modelBan.setRowCount(0);
        dsBanTheoGio.clear();

        try {
            Ban_DAO dao = new Ban_DAO();

            if (thoiGianDaChon == null) {
                dsBanTheoGio = dao.getTatCaBanKemTrangThaiMacDinh();
            } else {
                dsBanTheoGio = dao.getDanhSachBanTheoThoiGian(thoiGianDaChon);
            }

            for (String[] ban : dsBanTheoGio) {
            	int soKhach = (int) spnSoLuongKhach.getValue();
            	int soCho = Integer.parseInt(ban[2]);

            	if (soCho >= soKhach) {
            	    modelBan.addRow(new Object[]{
            	        ban[1],   // tên bàn
            	        ban[2],   // số chỗ
            	        ban[3]    // trạng thái
            	    });
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được danh sách bàn!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void locBan(String keyword) {
        modelBan.setRowCount(0);

        int soKhach = (int) spnSoLuongKhach.getValue();

        for (String[] ban : dsBanTheoGio) {
            String tenBan = ban[1];
            int soCho = Integer.parseInt(ban[2]);

            if ((keyword.isEmpty() || tenBan.toLowerCase().contains(keyword.toLowerCase()))
                    && soCho >= soKhach) {

                modelBan.addRow(new Object[]{
                        ban[1],
                        ban[2],
                        ban[3]
                });
            }
        }
    }

    private void showPopupBan() {
        popupBan.show(txtBan, 0, txtBan.getHeight());
    }

    private boolean validateForm() {
        String tenKH = txtKhachHang.getText().trim();
        if (tenKH.isEmpty() || tenKH.equals(PH_KHACH)) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!");
            txtKhachHang.requestFocus();
            return false;
        }

        String sdt = txtSoDienThoai.getText().trim();
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải đúng 10 số!");
            txtSoDienThoai.requestFocus();
            return false;
        }

        String gio = txtGioKhachVao.getText().trim();
        if (gio.isEmpty() || gio.equals(PH_GIO)) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giờ khách vào!");
            return false;
        }

        int soLuong = (int) spnSoLuongKhach.getValue();
        if (soLuong < 1) {
            JOptionPane.showMessageDialog(this, "Số lượng khách phải lớn hơn hoặc bằng 1!");
            return false;
        }

        String ban = txtBan.getText().trim();
        if (ban.isEmpty() || ban.equals(PH_BAN) || maBanDuocChon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn!");
            txtBan.requestFocus();
            return false;
        }

        return true;
    }

    class ColoredButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color bgColor;

        public ColoredButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setCursor(new Cursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color mauVe = bgColor;
            if (!isEnabled()) {
                mauVe = new Color(195, 195, 195);
                setForeground(new Color(120, 120, 120));
            } else {
                setForeground(Color.WHITE);
            }

            g2.setColor(mauVe);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            super.paintComponent(g);
            g2.dispose();
        }
    }
    private void tuDongDienKhachHangTheoSDT() {
        String sdt = txtSoDienThoai.getText().trim();

        if (sdt.isEmpty() || sdt.equals(PH_SDT) || !sdt.matches("\\d{10}")) {
            return;
        }

        KhachHang kh = khachHangDAO.getKhachHangTheoSDT(sdt);

        if (kh != null) {
            txtKhachHang.setText(kh.getTenKH());
            txtKhachHang.setForeground(Color.BLACK);
        }
    }

    private void themKhachHangNeuChuaCo() {
        String sdt = txtSoDienThoai.getText().trim();
        String tenKH = txtKhachHang.getText().trim();

        if (sdt.isEmpty() || sdt.equals(PH_SDT)) return;
        if (tenKH.isEmpty() || tenKH.equals(PH_KHACH)) return;

        KhachHang khCu = khachHangDAO.getKhachHangTheoSDT(sdt);
        if (khCu != null) return;

        LoaiKhachHang loai = new LoaiKhachHang();
        loai.setMaLoaiKH("LKH01"); // loại khách thường / mặc định

        KhachHang khMoi = new KhachHang(
                null,
                tenKH,
                sdt,
                loai,
                0
        );

        khachHangDAO.themKhachHangKhongCanMa(khMoi);
    }
    private void kiemTraPhieuDangMoCoQuaGio() {
        try {
            if (maPhieuHienTai == null || thoiGianDaChon == null) return;

            if (!"Đang chờ".equalsIgnoreCase(trangThaiHienTai)
                    && !"Đã đặt".equalsIgnoreCase(trangThaiHienTai)) {
                return;
            }

            long phutTre = (System.currentTimeMillis() - thoiGianDaChon.getTime()) / (60 * 1000);

            if (phutTre < 30) return;

            int chon = JOptionPane.showConfirmDialog(
                    this,
                    "Phiếu đặt bàn đã trễ quá 30 phút.\n\n"
                            + "Mã phiếu: " + maPhieuHienTai + "\n"
                            + "Bàn: " + txtBan.getText().trim() + "\n"
                            + "Khách: " + txtKhachHang.getText().trim() + "\n"
                            + "SĐT: " + txtSoDienThoai.getText().trim() + "\n"
                            + "Giờ đến: " + txtGioKhachVao.getText().trim() + "\n\n"
                            + "Bạn có muốn gia hạn thêm 30 phút không?",
                    "Cảnh báo quá giờ đặt bàn",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();

            if (chon == JOptionPane.YES_OPTION) {
                dao.giaHanThoiGianCho(maPhieuHienTai);
                loadPhieuDatBanLenForm(maPhieuHienTai);
            } else {
                dao.capNhatTrangThai(maPhieuHienTai, "Quá giờ");

                Ban_DAO banDAO = new Ban_DAO();
                banDAO.capNhatTrangThaiBan(maBanDuocChon, "Bàn trống");

                trangThaiHienTai = "Quá giờ";
                apDungTrangThaiForm(trangThaiHienTai);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}