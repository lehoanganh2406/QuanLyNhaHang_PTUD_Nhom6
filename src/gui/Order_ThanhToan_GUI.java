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
import digLog.KhachHang_DigLog;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.MonAn;
import entity.TaiKhoan;

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

    private JPanel pnOrderList;
    private JLabel lblTongSoLuong;
    private JLabel lblTongTienRight;

    private List<ChiTietHoaDon> dsCT = new ArrayList<>();
    private KhachHang khachHang;
    private KhuyenMai khuyenMaiDangDung;

    private double tongTien = 0;
    private double tienGiam = 0;
    private double tienVAT = 0;
    private double tongCong = 0;

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

        lblTenKH = new JLabel("");
        lblKhuyenMai = new JLabel("");
        lblTongThanhTien = new JLabel("0");
        lblDiemTichLuy = new JLabel("0");
        lblVAT = new JLabel("7%");
        lblTongCong = new JLabel("0");
        lblTienThua = new JLabel("0");

        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Visa"});
        cboPhuongThuc.setFont(new Font("SansSerif", Font.PLAIN, 16));

        txtTienKhachTra = new JTextField();
        txtTienKhachTra.setFont(new Font("SansSerif", Font.PLAIN, 16));

        form.add(row("Khách hàng", lblTenKH, true));
        form.add(row("Khuyến mãi", lblKhuyenMai, false));
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
        JButton btnThanhToan = new JButton("Thanh toán [F4]");

        styleButton(btnQuayLai, BTN_BACK, 24, false);
        styleButton(btnThanhToan, BTN_PAY, 24, true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        actionPanel.add(btnQuayLai, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        actionPanel.add(btnThanhToan, gbc);

        bottom.add(tongPanel, BorderLayout.NORTH);
        bottom.add(actionPanel, BorderLayout.CENTER);

        right.add(header, BorderLayout.NORTH);
        right.add(scroll, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        btnQuayLai.addActionListener(e -> quayLaiOrderMon());
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

        dsCT = chiTietDAO.getChiTietTheoMaHD(maHD);
        renderMon();
        tinhTongTien();
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
        tienGiam = 0;

        if (khuyenMaiDangDung != null && tongTien >= khuyenMaiDangDung.getDieuKienApDung()) {
            tienGiam = tongTien * khuyenMaiDangDung.getGiaTri() / 100.0;
        }

        tongCong = tongTien + tienVAT - tienGiam;

        lblTongThanhTien.setText(formatTien(tongTien));
        lblVAT.setText("7%");
        lblKhuyenMai.setText(khuyenMaiDangDung == null ? "" :
                khuyenMaiDangDung.getTenKhuyenMai() + " - " + df.format(khuyenMaiDangDung.getGiaTri()) + "%");
        lblTongCong.setText(formatTien(tongCong));
        lblTongTienRight.setText(formatTien(tongTien));

        tinhTienThua();
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
                        khuyenMaiDangDung = null;
                        tinhTongTien();
                    }
                }
            }

            return;
        }

        lblTenKH.setText(getThongTinKhachHang(khachHang));
        lblDiemTichLuy.setText(String.valueOf(khachHang.getDiemTichLuy()));

        chonKhuyenMaiTotNhat();
        tinhTongTien();
    }

    private void chonKhuyenMaiTotNhat() {
        khuyenMaiDangDung = null;

        for (KhuyenMai km : khuyenMaiDAO.getAllKhuyenMai()) {
            if (km == null) continue;
            if (!"Đang áp dụng".equalsIgnoreCase(km.getTrangThai())) continue;
            if (tongTien < km.getDieuKienApDung()) continue;

            if (khuyenMaiDangDung == null || km.getGiaTri() > khuyenMaiDangDung.getGiaTri()) {
                khuyenMaiDangDung = km;
            }
        }
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
        double tienTra = parseTien(txtTienKhachTra.getText());

        if (tienTra < tongCong) {
            JOptionPane.showMessageDialog(this, "Tiền khách trả chưa đủ.");
            return;
        }

        String maKH = khachHang == null ? null : khachHang.getMaKH();
        String maKM = khuyenMaiDangDung == null ? null : khuyenMaiDangDung.getMaKM();
        String phuongThuc = cboPhuongThuc.getSelectedItem().toString();
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
                int diemMoi = khachHang.getDiemTichLuy() + (int) (tongCong / 10000);
                khachHangDAO.capNhatDiemTichLuy(khachHang.getMaKH(), diemMoi);
            }

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");

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
                    new Order_Mon_GUI(taiKhoanDangNhap, maBan, tenBan, null, true)
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
}