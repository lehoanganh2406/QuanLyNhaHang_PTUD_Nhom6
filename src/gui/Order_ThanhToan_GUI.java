package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.KhuyenMai_DAO;
import dao.MonAn_DAO;
import digLog.HoaDonChiTiet_DigLog;
import digLog.KhachHang_DigLog;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.MonAn;
import entity.TaiKhoan;
import dao.PhieuDatBan_DAO;

public class Order_ThanhToan_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Color BG_MAIN = new Color(238, 238, 238);
    private final Color BG_TOP = new Color(245, 245, 245);
    private final Color RIGHT_HEADER = new Color(206, 227, 242);
    private final Color BORDER = new Color(150, 150, 150);

    private final Color BTN_BACK = new Color(225, 170, 65);
    private final Color BTN_ORDER = new Color(246, 210, 210);
    private final Color BTN_PAY = new Color(105, 190, 105);
    private final Color BTN_SPLIT = new Color(188, 222, 242);
    private final Color BTN_PREVIEW = new Color(248, 232, 198);

    private static final int COL_NAME_W = 250;
    private static final int COL_PRICE_W = 95;
    private static final int COL_QTY_W = 105;
    private static final int COL_TOTAL_W = 125;
    private static final int HEADER_H = 58;
    private static final int ORDER_ROW_H = 58;
    private static final int RIGHT_W = COL_NAME_W + COL_PRICE_W + COL_QTY_W + COL_TOTAL_W;

    private TaiKhoan taiKhoanDangNhap;
    private String maBan;
    private String tenBan;
    private String maHD;

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietDAO = new ChiTietHoaDon_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
    private final MonAn_DAO monAnDAO = new MonAn_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();
    private final PhieuDatBan_DAO phieuDatBanDAO = new PhieuDatBan_DAO();

    private final DecimalFormat df = new DecimalFormat("#,##0");

    private JTextField txtSDT;
    private JLabel lblTenKH;
    private JLabel lblKhuyenMai;
    private JLabel lblTongThanhTien;
    private JLabel lblDiemTichLuy;
    private JLabel lblVAT;
    private JLabel lblTongCong;
    private JComboBox<String> cboPhuongThuc;
    private JTextField txtTienKhachTra;
    private JLabel lblTienThua;
    private JButton btnXemKhuyenMai;
    private boolean tuChonKhuyenMai = false;
    private String tenKhuyenMaiTuChon = "";
    
    private JPanel pnOrderList;
    private JLabel lblTongSoLuong;
    private JLabel lblTongTienRight;
    private JLabel lblMaPhieuDatBan;
    private JLabel lblTienCoc;

    private List<ChiTietHoaDon> dsCT = new ArrayList<>();
    private KhachHang khachHang;
    private KhuyenMai khuyenMaiDangDung;

    private double tongTien = 0;
    private double tienGiam = 0;
    private double tienVAT = 0;
    private double tongCong = 0;
    private double tienCoc = 0;

    public Order_ThanhToan_GUI(TaiKhoan tk, String maBan, String tenBan) {
        this.taiKhoanDangNhap = tk;
        this.maBan = maBan;
        this.tenBan = tenBan;

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        initUI();
        loadHoaDon();
    }

    private void initUI() {
        add(createLeftPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(BG_MAIN);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_TOP);
        top.setPreferredSize(new Dimension(0, 82));
        top.setBorder(new EmptyBorder(10, 22, 10, 22));

        JLabel lblTitle = new JLabel("Thanh toán");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 34));

        JLabel lblBan = new JLabel(tenBan, SwingConstants.CENTER);
        lblBan.setFont(new Font("SansSerif", Font.BOLD, 34));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblBan, BorderLayout.CENTER);
        top.add(Box.createHorizontalStrut(150), BorderLayout.EAST);

        left.add(top, BorderLayout.NORTH);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.setBorder(new EmptyBorder(30, 52, 20, 72));

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        RoundedSearchPanel searchRow = new RoundedSearchPanel();
        searchRow.setLayout(new BorderLayout());
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        searchRow.setPreferredSize(new Dimension(0, 48));
        searchRow.setMinimumSize(new Dimension(0, 48));

        txtSDT = new JTextField();
        txtSDT.setFont(new Font("SansSerif", Font.PLAIN, 20));
        txtSDT.setForeground(new Color(150, 150, 150));
        txtSDT.setText("Nhập số điện thoại khách cần tìm...");
        txtSDT.setBorder(new EmptyBorder(0, 18, 0, 8));
        txtSDT.setOpaque(false);
        txtSDT.setBackground(new Color(0, 0, 0, 0));
        txtSDT.setCaretColor(Color.BLACK);

        JButton btnTim = new JButton("+");
        btnTim.setFont(new Font("SansSerif", Font.BOLD, 40));
        btnTim.setForeground(new Color(25, 125, 210));
        btnTim.setPreferredSize(new Dimension(68, 48));
        btnTim.setFocusPainted(false);
        btnTim.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(80, 80, 80)));
        btnTim.setBackground(Color.WHITE);
        btnTim.setOpaque(false);
        btnTim.setContentAreaFilled(false);
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchRow.add(txtSDT, BorderLayout.CENTER);
        searchRow.add(btnTim, BorderLayout.EAST);

        form.add(searchRow);
        form.add(Box.createVerticalStrut(22));

        lblTenKH = new JLabel("Khách vãng lai");
        lblMaPhieuDatBan = new JLabel("Không có");
        lblTienCoc = new JLabel("0");
        lblKhuyenMai = new JLabel("Không áp dụng");
        lblDiemTichLuy = new JLabel("0");
        lblTongThanhTien = new JLabel("0");
        lblVAT = new JLabel("7%");
        lblTongCong = new JLabel("0");
        lblTienThua = new JLabel("0");

        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Visa"});
        cboPhuongThuc.setFont(new Font("SansSerif", Font.PLAIN, 16));

        txtTienKhachTra = new JTextField();
        txtTienKhachTra.setFont(new Font("SansSerif", Font.PLAIN, 16));

        form.add(row("Khách hàng", lblTenKH, true));
        form.add(row("Phiếu đặt bàn", lblMaPhieuDatBan, false));
        form.add(row("Tiền cọc", lblTienCoc, true));
        form.add(row("Khuyến mãi", createKhuyenMaiView(), false));
        form.add(row("Tổng thành tiền", lblTongThanhTien, true));
        form.add(row("Điểm tích lũy", lblDiemTichLuy, false));
        form.add(row("VAT", lblVAT, true));
        form.add(row("Tổng cộng", lblTongCong, false));
        form.add(row("Phương thức thanh toán", cboPhuongThuc, true));
        form.add(row("Tiền khách trả", txtTienKhachTra, false));
        form.add(createMenhGiaPanel());
        form.add(row("Tiền thừa", lblTienThua, true));

        wrap.add(form, BorderLayout.NORTH);
        left.add(wrap, BorderLayout.CENTER);

        btnTim.addActionListener(e -> moThemKhachHang());
        txtSDT.addActionListener(e -> timKhachTheoSDT());

        txtTienKhachTra.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tinhTienThua(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tinhTienThua(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tinhTienThua(); }
        });
        cboPhuongThuc.addActionListener(e -> capNhatTienKhachTraTheoPhuongThuc());
        txtSDT.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if ("Nhập số điện thoại khách cần tìm...".equals(txtSDT.getText())) {
                    txtSDT.setText("");
                    txtSDT.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSDT.getText().trim().isEmpty()) {
                    txtSDT.setText("Nhập số điện thoại khách cần tìm...");
                    txtSDT.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return left;
    }

    private JPanel row(String title, JComponent value, boolean gray) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        row.setPreferredSize(new Dimension(0, 58));
        row.setMinimumSize(new Dimension(0, 58));
        row.setBackground(gray ? new Color(245, 245, 245) : Color.WHITE);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setBorder(new EmptyBorder(0, 26, 0, 0));
        lbl.setPreferredSize(new Dimension(310, 58));

        JPanel valueWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        valueWrap.setOpaque(false);
        valueWrap.setBorder(new EmptyBorder(0, 0, 0, 28));

        if (value instanceof JLabel) {
            JLabel v = (JLabel) value;
            v.setFont(new Font("SansSerif", Font.PLAIN, 18));
            v.setHorizontalAlignment(SwingConstants.RIGHT);
            v.setPreferredSize(new Dimension(260, 34));
            valueWrap.add(v);
        } else {
            value.setFont(new Font("SansSerif", Font.PLAIN, 18));

            if (value instanceof JComboBox) {
                value.setPreferredSize(new Dimension(270, 46));
                value.setMinimumSize(new Dimension(270, 46));
            } else if (value instanceof JTextField) {
                value.setPreferredSize(new Dimension(270, 42));
                value.setMinimumSize(new Dimension(270, 42));
                value.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1),
                        new EmptyBorder(0, 10, 0, 10)
                ));
            } else {
                value.setPreferredSize(new Dimension(260, 38));
            }

            valueWrap.add(value);
        }

        row.add(lbl, BorderLayout.WEST);
        row.add(valueWrap, BorderLayout.EAST);

        return row;
    }
    private void capNhatTienKhachTraTheoPhuongThuc() {
        String pt = cboPhuongThuc.getSelectedItem() == null
                ? ""
                : cboPhuongThuc.getSelectedItem().toString();

        if (!"Tiền mặt".equalsIgnoreCase(pt)) {
            txtTienKhachTra.setText(formatTien(tongCong));
            txtTienKhachTra.setEditable(false);
            txtTienKhachTra.setBackground(new Color(235, 235, 235));
        } else {
            txtTienKhachTra.setEditable(true);
            txtTienKhachTra.setBackground(Color.WHITE);
            txtTienKhachTra.setText("");
        }

        tinhTienThua();
    }
    private JPanel createMenhGiaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        panel.setPreferredSize(new Dimension(0, 58));
        panel.setMinimumSize(new Dimension(0, 58));
        panel.setBackground(Color.WHITE);

        JPanel buttons = new JPanel(new GridLayout(1, 9, 8, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(10, 26, 10, 28));

        int[] menhGia = {
                1000, 2000, 5000, 10000, 20000,
                50000, 100000, 200000, 500000
        };

        for (int mg : menhGia) {
            JButton btn = new JButton(formatTien(mg));
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> congTien(mg));
            buttons.add(btn);
        }

        panel.add(buttons, BorderLayout.CENTER);
        return panel;
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

        JScrollPane scroll = new JScrollPane(pnOrderList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setPreferredSize(new Dimension(RIGHT_W, 120));

        JPanel tongPanel = new JPanel();
        tongPanel.setLayout(new BoxLayout(tongPanel, BoxLayout.X_AXIS));
        tongPanel.setBackground(Color.WHITE);
        tongPanel.setPreferredSize(new Dimension(RIGHT_W, 58));
        tongPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER));

        JLabel lblTongText = new JLabel("Tổng cộng");
        lblTongText.setFont(new Font("SansSerif", Font.PLAIN, 26));
        lblTongText.setBorder(new EmptyBorder(0, 18, 0, 0));
        lblTongText.setPreferredSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));
        lblTongText.setMinimumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));
        lblTongText.setMaximumSize(new Dimension(COL_NAME_W + COL_PRICE_W, 58));

        lblTongSoLuong = new JLabel("0", SwingConstants.CENTER);
        lblTongSoLuong.setFont(new Font("SansSerif", Font.PLAIN, 26));
        lblTongSoLuong.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, BORDER));
        lblTongSoLuong.setPreferredSize(new Dimension(COL_QTY_W, 58));
        lblTongSoLuong.setMinimumSize(new Dimension(COL_QTY_W, 58));
        lblTongSoLuong.setMaximumSize(new Dimension(COL_QTY_W, 58));

        lblTongTienRight = new JLabel("0", SwingConstants.CENTER);
        lblTongTienRight.setFont(new Font("SansSerif", Font.PLAIN, 26));
        lblTongTienRight.setPreferredSize(new Dimension(COL_TOTAL_W, 58));
        lblTongTienRight.setMinimumSize(new Dimension(COL_TOTAL_W, 58));
        lblTongTienRight.setMaximumSize(new Dimension(COL_TOTAL_W, 58));

        tongPanel.add(lblTongText);
        tongPanel.add(lblTongSoLuong);
        tongPanel.add(lblTongTienRight);

        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        
        JButton btnQuayLai = new JButton("‹ Quay lại");
        JButton btnTamTinh = new JButton("Tạm tính");
        JButton btnThanhToan = new JButton("Thanh toán [F4]");

        styleButton(btnQuayLai, BTN_BACK, 20, false);
        styleButton(btnTamTinh, BTN_PREVIEW, 20, true);
        styleButton(btnThanhToan, BTN_PAY, 20, true);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        actionPanel.add(btnQuayLai, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        actionPanel.add(btnTamTinh, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        actionPanel.add(btnThanhToan, gbc);

        bottom.add(tongPanel, BorderLayout.NORTH);
        bottom.add(actionPanel, BorderLayout.CENTER);

        right.add(header, BorderLayout.NORTH);
        right.add(scroll, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        btnQuayLai.addActionListener(e -> quayLaiOrderMon());
        btnTamTinh.addActionListener(e -> moPhieuTamTinh());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());

        return right;
    }

    private JLabel createHeaderCell(String text, int width, boolean rightBorder) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(RIGHT_HEADER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lbl.setPreferredSize(new Dimension(width, HEADER_H));
        lbl.setMinimumSize(new Dimension(width, HEADER_H));
        lbl.setMaximumSize(new Dimension(width, HEADER_H));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, rightBorder ? 1 : 0, BORDER));
        return lbl;
    }

    private void styleButton(JButton btn, Color bg, int fontSize, boolean bold) {
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize));
        btn.setMargin(new Insets(0, 6, 0, 6));
    }

    private void loadHoaDon() {
        HoaDon hd = hoaDonDAO.timHoaDonChuaThanhToanTheoBan(maBan);

        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn chưa thanh toán.");
            return;
        }

        maHD = hd.getMaHD();
        hoaDonDAO.capNhatTongTien(maHD);

        String maPhieu = hd.getMaPhieuDatBan() == null 
                ? null 
                : hd.getMaPhieuDatBan().getMaPhieuDatBan();

        loadThongTinPhieuDatBan(maPhieu);

        dsCT = chiTietDAO.getChiTietTheoMaHD(maHD);
        renderMon();
        tinhTongTien();
    }
    
    private void loadThongTinPhieuDatBan(String maPhieuDatBan) {
        tienCoc = 0;

        if (maPhieuDatBan == null || maPhieuDatBan.trim().isEmpty()) {
            lblMaPhieuDatBan.setText("Không có");
            lblTienCoc.setText("0");
            return;
        }

        lblMaPhieuDatBan.setText(maPhieuDatBan);

        String[] phieu = phieuDatBanDAO.timTheoMaPhieu(maPhieuDatBan);

        if (phieu != null) {
            try {
                tienCoc = Double.parseDouble(phieu[6]);
                lblTienCoc.setText(formatTien(tienCoc));
            } catch (Exception e) {
                tienCoc = 0;
                lblTienCoc.setText("0");
            }
        } else {
            lblTienCoc.setText("0");
        }
    }

    private void renderMon() {
        pnOrderList.removeAll();

        int tongSL = 0;
        double tongRight = 0;

        for (ChiTietHoaDon ct : dsCT) {
            String maMon = ct.getMaMon().getMaMon();
            MonAn mon = monAnDAO.getMonAnTheoMa(maMon);

            String tenMon = mon != null ? mon.getTenMon() : maMon;
            double gia = ct.getDonGia();
            int sl = ct.getSoLuong();
            double thanhTien = gia * sl;

            tongSL += sl;
            tongRight += thanhTien;

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setPreferredSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            row.setMaximumSize(new Dimension(RIGHT_W, ORDER_ROW_H));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            row.add(cell(tenMon, COL_NAME_W, SwingConstants.LEFT));
            row.add(cell(formatTien(gia), COL_PRICE_W, SwingConstants.CENTER));
            row.add(cell(String.valueOf(sl), COL_QTY_W, SwingConstants.CENTER));
            row.add(cell(formatTien(thanhTien), COL_TOTAL_W, SwingConstants.CENTER));

            pnOrderList.add(row);
        }

        pnOrderList.add(Box.createVerticalGlue());
        pnOrderList.revalidate();
        pnOrderList.repaint();

        lblTongSoLuong.setText(String.valueOf(tongSL));
        lblTongTienRight.setText(formatTien(tongRight));
    }

    private JLabel cell(String text, int w, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lbl.setPreferredSize(new Dimension(w, ORDER_ROW_H));
        lbl.setMinimumSize(new Dimension(w, ORDER_ROW_H));
        lbl.setMaximumSize(new Dimension(w, ORDER_ROW_H));
        lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
        return lbl;
    }

    private void tinhTongTien() {
        tongTien = 0;

        for (ChiTietHoaDon ct : dsCT) {
            tongTien += ct.getSoLuong() * ct.getDonGia();
        }

        tienVAT = tongTien * 0.07;
        if (!tuChonKhuyenMai) {
            chonKhuyenMaiTotNhat();
        }

        tongCong = tongTien + tienVAT - tienGiam - tienCoc;
        if (tongCong < 0) {
            tongCong = 0;
        }

        lblTongThanhTien.setText(formatTien(tongTien));
        lblVAT.setText("7%");
        lblTongCong.setText(formatTien(tongCong));
        lblTongTienRight.setText(formatTien(tongTien));

        tinhTienThua();
        capNhatTienKhachTraTheoPhuongThuc();
    }

    private void timKhachTheoSDT() {
    	String sdt = txtSDT.getText().trim();
    	if ("Nhập số điện thoại khách cần tìm...".equals(sdt)) {
    	    sdt = "";
    	}

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng.");
            return;
        }

        khachHang = khachHangDAO.getKhachHangTheoSDT(sdt);

        if (khachHang == null) {
            int chon = JOptionPane.showConfirmDialog(
                    this,
                    "Không tìm thấy khách hàng.\nBạn có muốn thêm khách hàng mới không?",
                    "Thêm khách hàng",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (chon == JOptionPane.YES_OPTION) {
                Window owner = SwingUtilities.getWindowAncestor(this);
                KhachHang_DigLog dlg = new KhachHang_DigLog(owner, sdt);
                dlg.setVisible(true);

                if (dlg.isThemThanhCong()) {
                    khachHang = dlg.getKhachHangMoi();

                    if (khachHang != null) {
                    	lblTenKH.setText(getThongTinKhachHang(khachHang));
                        lblDiemTichLuy.setText(String.valueOf(khachHang.getDiemTichLuy()));
                        tuChonKhuyenMai = false;
                        khuyenMaiDangDung = null;
                        tinhTongTien();
                    }
                }
            }

            return;
        }

        lblTenKH.setText(getThongTinKhachHang(khachHang));
        lblDiemTichLuy.setText(String.valueOf(khachHang.getDiemTichLuy()));

        tuChonKhuyenMai = false;
        khuyenMaiDangDung = null;
        tinhTongTien();
    }

    private void chonKhuyenMaiTotNhat() {
        khuyenMaiDangDung = null;
        tienGiam = 0;

        List<KMOption> list = layDanhSachKhuyenMaiCoTheApDung();

        if (list.isEmpty()) {
            lblKhuyenMai.setText("Không áp dụng");
            return;
        }

        KMOption best = list.get(0);
        khuyenMaiDangDung = best.km;
        tienGiam = best.tienGiam;
        lblKhuyenMai.setText(best.ten + " - giảm " + formatTien(best.tienGiam));
    }

    private void tinhTienThua() {
        double tienTra = parseTien(txtTienKhachTra.getText());
        double thua = tienTra - tongCong;

        lblTienThua.setText(formatTien(thua));

        if (thua < 0) {
            lblTienThua.setForeground(Color.RED);
        } else {
            lblTienThua.setForeground(Color.BLACK);
        }
    }

    private void congTien(int soTien) {
        double hienTai = parseTien(txtTienKhachTra.getText());
        txtTienKhachTra.setText(formatTien(hienTai + soTien));
    }

    private void xuLyThanhToan() {
        String phuongThuc = cboPhuongThuc.getSelectedItem().toString();
        double tienTra = parseTien(txtTienKhachTra.getText());

        if ("Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            if (tienTra < tongCong) {
                JOptionPane.showMessageDialog(this, "Tiền khách trả chưa đủ.");
                return;
            }
        } else if ("Chuyển khoản".equalsIgnoreCase(phuongThuc) ) {
            boolean daXacNhan = hienThiVietQRThanhToan(phuongThuc);
            if (!daXacNhan) {
                return;
            }

            tienTra = tongCong;
            txtTienKhachTra.setText(formatTien(tongCong));
        } else if ("Visa".equalsIgnoreCase(phuongThuc)) {
            tienTra = tongCong;
            txtTienKhachTra.setText(formatTien(tongCong));
        }

        String maKH = khachHang == null ? null : khachHang.getMaKH();
        String maKM = khuyenMaiDangDung == null ? null : khuyenMaiDangDung.getMaKM();
        double tienThua = tienTra - tongCong;
        boolean ok = hoaDonDAO.thanhToanHoaDon(
                maHD,
                maKH,
                maKM,
                tienTra,
                tienVAT,
                tienThua,
                phuongThuc
        );

        if (ok) {
            banDAO.capNhatTrangThaiBan(maBan, "Bàn trống");

            if (khachHang != null) {
            	int diemMoi = khachHang.getDiemTichLuy() + (int) (tongCong / 100000);
                khachHangDAO.capNhatDiemTichLuy(khachHang.getMaKH(), diemMoi);
            }


            JDialog dlg = new JDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Chi tiết hóa đơn",
                    Dialog.ModalityType.APPLICATION_MODAL
            );

            dlg.setContentPane(new HoaDonChiTiet_DigLog(
                    "HÓA ĐƠN THANH TOÁN",
                    maHD,
                    tenBan,
                    lblMaPhieuDatBan.getText(),
                    tienCoc,
                    lblTenKH.getText(),
                    getTenNhanVienDangNhap(),
                    getThoiGianHoaDon(),
                    lblKhuyenMai.getText(),
                    tienGiam,
                    tongTien,
                    tienVAT,
                    tongCong,
                    tienTra,
                    tienThua,
                    phuongThuc,
                    dsCT
            ));
            dlg.setSize(650, 760);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);

            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof TrangChu_GUI) {
                ((TrangChu_GUI) w).showCustomPage(
                        "Order_Ban_GUI",
                        new Order_Ban_GUI(taiKhoanDangNhap)
                );
            }
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại.");
        }
    }

    private void quayLaiOrderMon() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof TrangChu_GUI) {
            ((TrangChu_GUI) w).showCustomPage(
                    "Order_Mon_GUI",
                    new Order_Mon_GUI(taiKhoanDangNhap, maBan, tenBan, null, true, laHoaDonMangVe())
            );
        }
    }
    

    private double parseTien(String text) {
        try {
            if (text == null) return 0;
            text = text.trim().replace(".", "").replace(",", "").replace(" ", "");
            if (text.isEmpty()) return 0;
            return Double.parseDouble(text);
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatTien(double value) {
        return df.format(value).replace(",", ".");
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

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.setColor(new Color(70, 70, 70));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }
    private String getThoiGianHoaDon() {
        Object[] hd = hoaDonDAO.getHoaDonByMa(maHD);

        if (hd == null) return "";

        String vao = hd[1] == null ? "" : hd[1].toString();
        String ra = hd[2] == null ? "" : hd[2].toString();

        return vao + " - " + ra;
    }
    private void moThemKhachHang() {
        String sdt = txtSDT.getText().trim();

        if ("Nhập số điện thoại khách cần tìm...".equals(sdt)) {
            sdt = "";
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        KhachHang_DigLog dlg = new KhachHang_DigLog(owner, sdt);
        dlg.setVisible(true);

        if (dlg.isThemThanhCong()) {
            khachHang = dlg.getKhachHangMoi();

            if (khachHang != null) {
                txtSDT.setText(khachHang.getSdt());
                txtSDT.setForeground(Color.BLACK);

                lblTenKH.setText(getThongTinKhachHang(khachHang));
                lblDiemTichLuy.setText(String.valueOf(khachHang.getDiemTichLuy()));

                tuChonKhuyenMai = false;
                khuyenMaiDangDung = null;
                tinhTongTien();
            }
        }
    }
    private String getThongTinKhachHang(KhachHang kh) {
        if (kh == null) return "";

        String tenKH = kh.getTenKH() == null ? "" : kh.getTenKH();
        String tenLoai = "";

        if (kh.getMaLoaiKH() != null && kh.getMaLoaiKH().getTenLoaiKH() != null) {
            tenLoai = kh.getMaLoaiKH().getTenLoaiKH();
        }

        if (tenLoai.isEmpty()) {
            return tenKH;
        }

        return tenKH + " - " + tenLoai;
    }
    private JPanel createKhuyenMaiView() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(270, 42));

        lblKhuyenMai.setHorizontalAlignment(SwingConstants.RIGHT);
        lblKhuyenMai.setFont(new Font("SansSerif", Font.PLAIN, 18));

        btnXemKhuyenMai = new JButton("🔍");
        btnXemKhuyenMai.setPreferredSize(new Dimension(45, 38));
        btnXemKhuyenMai.setFocusPainted(false);
        btnXemKhuyenMai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXemKhuyenMai.addActionListener(e -> hienThiChonKhuyenMai());

        panel.add(lblKhuyenMai, BorderLayout.CENTER);
        panel.add(btnXemKhuyenMai, BorderLayout.EAST);

        return panel;
    }
    private class KMOption {
        KhuyenMai km;
        String ten;
        double tienGiam;
        boolean duocApDung;
        boolean laTotNhat;
        String lyDo;

        KMOption(KhuyenMai km, String ten, double tienGiam, boolean duocApDung, String lyDo) {
            this.km = km;
            this.ten = ten;
            this.tienGiam = tienGiam;
            this.duocApDung = duocApDung;
            this.lyDo = lyDo;
        }

        @Override
        public String toString() {
            String best = laTotNhat ? "  ⭐ TỐT NHẤT" : "";
            String status = duocApDung ? "Giảm " + formatTien(tienGiam) : "Không đủ điều kiện";
            return ten + " - " + status + best;
        }
    }

    private List<KMOption> layDanhSachKhuyenMaiTatCa() {
        List<KMOption> list = new ArrayList<>();

        String loaiKH = "";
        int diem = 0;

        if (khachHang != null) {
            diem = khachHang.getDiemTichLuy();
            if (khachHang.getMaLoaiKH() != null && khachHang.getMaLoaiKH().getTenLoaiKH() != null) {
                loaiKH = khachHang.getMaLoaiKH().getTenLoaiKH();
            }
        }

        for (KhuyenMai km : khuyenMaiDAO.getAllKhuyenMai()) {
            if (km == null) continue;

            boolean duoc = true;
            String lyDo = "";

            if (!"Đang áp dụng".equalsIgnoreCase(km.getTrangThai())) {
                duoc = false;
                lyDo = "Khuyến mãi không ở trạng thái Đang áp dụng";
            }

            double dieuKien = km.getDieuKienApDung();
            if (duoc && tongTien < dieuKien) {
                duoc = false;
                lyDo = "Hóa đơn chưa đủ " + formatTien(dieuKien);
            }

            String doiTuong = km.getDoiTuongApDung() == null ? "" : km.getDoiTuongApDung();

            boolean laTatCaKH = doiTuong.equalsIgnoreCase("Tất cả KH");
            boolean dungLoaiKH = khachHang != null && doiTuong.equalsIgnoreCase(loaiKH);

            boolean dungDoiTuong = laTatCaKH || dungLoaiKH;

            if (duoc && !dungDoiTuong) {
                duoc = false;
                lyDo = "Không đúng đối tượng áp dụng";
            }

            double tienGiamKM = duoc ? tinhTienGiamTheoKM(km) : 0;
            list.add(new KMOption(km, km.getTenKhuyenMai(), tienGiamKM, duoc, lyDo));
        }

        list.sort((a, b) -> {
            if (a.duocApDung != b.duocApDung) return a.duocApDung ? -1 : 1;
            return Double.compare(b.tienGiam, a.tienGiam);
        });

        for (KMOption o : list) {
            o.laTotNhat = false;
        }

        for (KMOption o : list) {
            if (o.duocApDung) {
                o.laTotNhat = true;
                break;
            }
        }

        return list;
    }

    private List<KMOption> layDanhSachKhuyenMaiCoTheApDung() {
        List<KMOption> result = new ArrayList<>();

        for (KMOption o : layDanhSachKhuyenMaiTatCa()) {
            if (o.duocApDung) {
                result.add(o);
            }
        }

        return result;
    }

    private void hienThiChonKhuyenMai() {
        List<KMOption> list = layDanhSachKhuyenMaiTatCa();

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có khuyến mãi.");
            return;
        }

        String[] cols = {"Khuyến mãi", "Giảm", "Trạng thái", "Chi tiết"};
        Object[][] data = new Object[list.size()][4];

        for (int i = 0; i < list.size(); i++) {
            KMOption o = list.get(i);

            data[i][0] = o.ten + (o.laTotNhat ? "  ⭐ TỐT NHẤT" : "");
            data[i][1] = o.duocApDung ? formatTien(o.tienGiam) : "-";
            data[i][2] = o.duocApDung ? "Có thể chọn" : "Không đủ điều kiện";
            data[i][3] = "<html><u>Chi tiết</u></html>";
        }

        JTable table = new JTable(data, cols) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }

            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                KMOption opt = list.get(row);

                if (!opt.duocApDung) {
                    c.setForeground(Color.GRAY);
                    c.setBackground(new Color(240, 240, 240));
                } else if (isRowSelected(row)) {
                    c.setForeground(Color.BLACK);
                    c.setBackground(new Color(206, 227, 242));
                } else if (opt.laTotNhat) {
                    c.setForeground(new Color(20, 120, 40));
                    c.setBackground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        };

        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(0).setPreferredWidth(260);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).duocApDung) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }

        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Chọn khuyến mãi",
                Dialog.ModalityType.APPLICATION_MODAL
        );

  
        JButton btnDong = new JButton("Đóng");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnDong);

        dlg.setLayout(new BorderLayout(10, 10));
        dlg.add(new JScrollPane(table), BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setSize(720, 420);
        dlg.setLocationRelativeTo(this);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0) return;

                KMOption opt = list.get(row);

                // bấm chi tiết
                if (col == 3) {
                    hienThiChiTietKhuyenMai(opt, dlg);
                    return;
                }

                // không đủ điều kiện thì không cho chọn
                if (!opt.duocApDung) {
                    JOptionPane.showMessageDialog(dlg, "Khuyến mãi này không đủ điều kiện.");
                    return;
                }

                // CHỌN LUÔN
                khuyenMaiDangDung = opt.km;
                tienGiam = opt.tienGiam;
                tenKhuyenMaiTuChon = opt.ten;
                tuChonKhuyenMai = true;

                lblKhuyenMai.setText(opt.ten + " - giảm " + formatTien(opt.tienGiam));

                tongCong = tongTien + tienVAT - tienGiam;
                lblTongCong.setText(formatTien(tongCong));
                tinhTienThua();

                dlg.dispose();
            }
        });

        
        btnDong.addActionListener(e -> dlg.dispose());

        dlg.setVisible(true);
    }
    private void hienThiChiTietKhuyenMai(KMOption opt, Component parent) {
        StringBuilder sb = new StringBuilder();

        sb.append("Tên khuyến mãi: ").append(opt.ten).append("\n");
        sb.append("Trạng thái: ").append(opt.duocApDung ? "Có thể áp dụng" : "Không đủ điều kiện").append("\n");
        sb.append("Tiền giảm: ").append(formatTien(opt.tienGiam)).append("\n");

        if (opt.km != null) {
            sb.append("Mã KM: ").append(opt.km.getMaKM()).append("\n");

            if (opt.km.getMaLoaiKM() != null) {
                sb.append("Loại KM: ").append(opt.km.getMaLoaiKM().getTenLoaiKM()).append("\n");
            }

            sb.append("Đối tượng: ").append(opt.km.getDoiTuongApDung()).append("\n");
            sb.append("Điều kiện: ").append(formatTien(opt.km.getDieuKienApDung())).append("\n");
            sb.append("Giá trị: ").append(formatTien(opt.km.getGiaTri())).append("\n");
            sb.append("Ghi chú: ").append(opt.km.getGhiChu()).append("\n");
            sb.append("Trạng thái: ").append(opt.km.getTrangThai()).append("\n");
        } else {
            sb.append("Loại KM: Thành viên\n");
            sb.append("Nguồn: Theo loại khách hàng/điểm tích lũy\n");
        }

        if (!opt.duocApDung) {
            sb.append("Lý do: ").append(opt.lyDo).append("\n");
        }

        JOptionPane.showMessageDialog(parent, sb.toString(), "Chi tiết khuyến mãi", JOptionPane.INFORMATION_MESSAGE);
    }

    private double tinhTienGiamTheoKM(KhuyenMai km) {
        if (km == null) return 0;

        String maLoaiKM = "";
        if (km.getMaLoaiKM() != null) {
            maLoaiKM = km.getMaLoaiKM().getMaLoaiKM();
        }

        if ("LKM01".equals(maLoaiKM) || "LKM03".equals(maLoaiKM)) {
            return tongTien * km.getGiaTri() / 100.0;
        }

        if ("LKM02".equals(maLoaiKM)) {
            return km.getGiaTri();
        }

        return 0;
    }
    private String getTenNhanVienDangNhap() {
        if (taiKhoanDangNhap == null || taiKhoanDangNhap.getMaNV() == null) {
            return "";
        }

        if (taiKhoanDangNhap.getMaNV().getHoTen() != null 
                && !taiKhoanDangNhap.getMaNV().getHoTen().trim().isEmpty()) {
            return taiKhoanDangNhap.getMaNV().getHoTen();
        }

        return taiKhoanDangNhap.getMaNV().getMaNV();
    }
    private void moPhieuTamTinh() {
        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Phiếu Tạm Tính",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dlg.setContentPane(new HoaDonChiTiet_DigLog(
                "PHIẾU TẠM TÍNH",
                maHD,
                tenBan,
                lblMaPhieuDatBan.getText(),
                tienCoc,
                lblTenKH.getText(),
                getTenNhanVienDangNhap(),
                getThoiGianHoaDonTamTinh(),
                lblKhuyenMai.getText(),
                tienGiam,
                tongTien,
                tienVAT,
                tongCong,
                0,
                0,
                cboPhuongThuc.getSelectedItem().toString(),
                dsCT
        ));

        dlg.setSize(650, 760);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
    private String getThoiGianHoaDonTamTinh() {
        Object[] hd = hoaDonDAO.getHoaDonByMa(maHD);

        if (hd == null) return "";

        String vao = hd[1] == null ? "" : hd[1].toString();
        String hienTai = java.time.LocalDateTime.now().toString();

        return vao + " - " + hienTai;
    }
    private boolean laHoaDonMangVe() {
        Object[] hd = hoaDonDAO.getHoaDonByMa(maHD);

        if (hd == null) return false;

        for (Object o : hd) {
            if (o != null && "Mang về".equalsIgnoreCase(o.toString())) {
                return true;
            }
        }

        return false;
    }
    private boolean hienThiVietQRThanhToan(String phuongThuc) {
        final boolean[] daXacNhan = {false};

        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Thanh toán " + phuongThuc,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel lblTitle = new JLabel("Quét mã VietQR để thanh toán", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel lblInfo = new JLabel(
                "<html><center>"
                        + "Số tiền: <b>" + formatTien(tongCong) + " VNĐ</b><br>"
                        + "Nội dung: <b>" + maHD + "</b>"
                        + "</center></html>",
                SwingConstants.CENTER
        );
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel lblQR = new JLabel("Đang tải QR...", SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(360, 360));
        lblQR.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(taoLinkVietQR()));
            Image img = icon.getImage().getScaledInstance(340, 340, Image.SCALE_SMOOTH);
            lblQR.setText("");
            lblQR.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            lblQR.setText("<html><center>Không tải được QR<br>Kiểm tra mạng</center></html>");
        }

        JButton btnDaThanhToan = new JButton("Xác nhận đã thanh toán");
        JButton btnHuy = new JButton("Hủy");

        btnDaThanhToan.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnHuy.setFont(new Font("SansSerif", Font.BOLD, 16));

        btnDaThanhToan.addActionListener(e -> {
            daXacNhan[0] = true;
            dlg.dispose();
        });

        btnHuy.addActionListener(e -> {
            daXacNhan[0] = false;
            dlg.dispose();
        });

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(lblInfo, BorderLayout.NORTH);
        center.add(lblQR, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        bottom.setOpaque(false);
        bottom.add(btnDaThanhToan);
        bottom.add(btnHuy);

        root.add(lblTitle, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setSize(480, 600);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        return daXacNhan[0];
    }

    private String taoLinkVietQR() {
        String bankId = "970436"; // Vietcombank. Đổi theo ngân hàng của bạn
        String soTaiKhoan = "123456789"; // đổi số tài khoản nhận tiền
        String tenChuTK = "NHA HANG HY VONG";
        String noiDung = maHD;
        long soTien = Math.round(tongCong);

        return "https://img.vietqr.io/image/"
                + bankId + "-" + soTaiKhoan + "-compact2.png"
                + "?amount=" + soTien
                + "&addInfo=" + noiDung
                + "&accountName=" + tenChuTK.replace(" ", "%20");
    }
    
}