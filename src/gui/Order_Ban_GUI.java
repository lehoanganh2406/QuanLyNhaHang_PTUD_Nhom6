package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import dao.Ban_DAO;
import dao.KhuVuc_DAO;
import dao.PhieuDatBan_DAO;
import entity.Ban;
import entity.KhuVuc;
import entity.TaiKhoan;

import java.sql.Timestamp;

public class Order_Ban_GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoanDangNhap;

    private JPanel contentPane;
    private JPanel pnlBody;
    private JPanel pnlLeft;
    private JPanel pnlCenter;
    private JPanel pnlTabsKhuVuc;
    private JPanel pnlBanContainer;

    private JScrollPane scrBan;

    private JLabel lblGio;
    private JLabel lblNgay;

    private JButton btnHomNay;
    private JButton btnPrevNgay;
    private JButton btnNextNgay;
    private JDateChooser dcNgayChon;

    private final Ban_DAO banDAO = new Ban_DAO();
    private final KhuVuc_DAO khuVucDAO = new KhuVuc_DAO();
    private final PhieuDatBan_DAO phieuDatBanDAO = new PhieuDatBan_DAO();

    private final List<String[]> dsTatCaBanTheoNgay = new ArrayList<>();
    private final List<KhuVuc> dsKhuVuc = new ArrayList<>();

    private Timer timerDongHo;
    private Timer timerReloadBan;

    private String maKhuVucDangChon = "ALL";
    private String tenKhuVucDangChon = "Tất cả";

    private final Color BG_APP = new Color(221, 221, 221);
    private final Color BG_LEFT = new Color(232, 232, 232);
    private final Color BG_CENTER = Color.WHITE;
    private final Color BG_TIME = new Color(245, 245, 245);

    private final Color TAB_NORMAL = new Color(246, 246, 246);
    private final Color TAB_SELECTED = new Color(239, 221, 199);
    private final Color TAB_BORDER = new Color(226, 226, 226);

    private final Color BTN_DATE = new Color(183, 209, 232);

    private final Color BAN_TRONG = new Color(176, 206, 229);
    private final Color BAN_DAT = new Color(229, 121, 121);
    private final Color BAN_PHUC_VU = new Color(110, 190, 98);
    private final Color BAN_MAC_DINH = new Color(200, 200, 200);

    public Order_Ban_GUI() {
        this(null);
    }

    public Order_Ban_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        initUI();
        napKhuVucTuCSDL();
        datNgayMacDinhHomNay();
        taiDanhSachBanTheoNgay();
        khoiDongDongHo();
    }

    private void initUI() {
        setTitle("Order bàn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_APP);
        setContentPane(contentPane);

        Pn_ThanhMenu pnThanhMenu = new Pn_ThanhMenu(taiKhoanDangNhap);
        pnThanhMenu.setPreferredSize(new Dimension(100, 42));
        contentPane.add(pnThanhMenu, BorderLayout.NORTH);

        pnlBody = new JPanel(new BorderLayout());
        pnlBody.setBackground(BG_APP);
        contentPane.add(pnlBody, BorderLayout.CENTER);

        pnlLeft = createLeftPanel();
        pnlCenter = createCenterPanel();

        pnlBody.add(pnlLeft, BorderLayout.WEST);
        pnlBody.add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_LEFT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(215, 100));
        panel.setBorder(new EmptyBorder(0, 6, 0, 8));

        JPanel pnlTimeWrap = new JPanel(new BorderLayout());
        pnlTimeWrap.setOpaque(false);
        pnlTimeWrap.setBorder(new EmptyBorder(4, 0, 12, 0));

        JPanel pnlTime = new JPanel();
        pnlTime.setBackground(BG_TIME);
        pnlTime.setLayout(new BoxLayout(pnlTime, BoxLayout.Y_AXIS));
        pnlTime.setBorder(new EmptyBorder(14, 10, 10, 10));
        pnlTime.setPreferredSize(new Dimension(198, 90));
        pnlTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblGio = new JLabel("00:00:00", SwingConstants.CENTER);
        lblGio.setFont(new Font("SansSerif", Font.PLAIN, 28));
        lblGio.setForeground(Color.BLACK);
        lblGio.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNgay = new JLabel("01/01/2026", SwingConstants.CENTER);
        lblNgay.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNgay.setForeground(Color.BLACK);
        lblNgay.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlTime.add(lblGio);
        pnlTime.add(Box.createVerticalStrut(2));
        pnlTime.add(lblNgay);
        pnlTimeWrap.add(pnlTime, BorderLayout.CENTER);

        JLabel lblTrangThai = new JLabel("Trạng thái bàn");
        lblTrangThai.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTrangThai.setForeground(Color.BLACK);
        lblTrangThai.setBorder(new EmptyBorder(0, 18, 0, 0));

        JPanel line = new JPanel();
        line.setBackground(Color.BLACK);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        line.setPreferredSize(new Dimension(190, 2));

        panel.add(pnlTimeWrap);
        panel.add(lblTrangThai);
        panel.add(Box.createVerticalStrut(6));
        panel.add(line);
        panel.add(Box.createVerticalStrut(14));
        panel.add(createLegendBox("Bàn trống", BAN_TRONG));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createLegendBox("Bàn đặt", BAN_DAT));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createLegendBox("Bàn đang phục vụ", BAN_PHUC_VU));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createLegendBox(String text, Color color) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(color);
        box.setPreferredSize(new Dimension(178, 40));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(Color.BLACK);

        box.add(lbl, BorderLayout.CENTER);
        wrap.add(box);
        return wrap;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(10, 12, 12, 12));

        JPanel pnlTopCenter = new JPanel();
        pnlTopCenter.setOpaque(false);
        pnlTopCenter.setLayout(new BoxLayout(pnlTopCenter, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Order");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 42));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBorder(new EmptyBorder(0, 4, 8, 0));

        JPanel pnlHeaderRow = new JPanel(new BorderLayout(12, 0));
        pnlHeaderRow.setOpaque(false);

        pnlTabsKhuVuc = new JPanel(new WrapLayout(FlowLayout.LEFT, 0, 0));
        pnlTabsKhuVuc.setOpaque(false);

        JPanel pnlDateWrap = createDatePanel();

        pnlHeaderRow.add(pnlTabsKhuVuc, BorderLayout.CENTER);
        pnlHeaderRow.add(pnlDateWrap, BorderLayout.EAST);

        pnlTopCenter.add(lblTitle);
        pnlTopCenter.add(pnlHeaderRow);

        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(BG_CENTER);
        pnlContent.setBorder(new EmptyBorder(18, 18, 18, 18));

        pnlBanContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 42, 34));
        pnlBanContainer.setBackground(BG_CENTER);

        scrBan = new JScrollPane(pnlBanContainer);
        scrBan.setBorder(null);
        scrBan.getViewport().setBackground(BG_CENTER);
        scrBan.getVerticalScrollBar().setUnitIncrement(16);
        scrBan.getHorizontalScrollBar().setUnitIncrement(16);

        pnlContent.add(scrBan, BorderLayout.CENTER);

        panel.add(pnlTopCenter, BorderLayout.NORTH);
        panel.add(pnlContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDatePanel() {
        JPanel pnlDateWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlDateWrap.setOpaque(false);

        btnHomNay = new JButton("Hôm nay");
        styleDateButton(btnHomNay, 92, 38);

        btnPrevNgay = new JButton("‹");
        styleDateButton(btnPrevNgay, 42, 38);

        btnNextNgay = new JButton("›");
        styleDateButton(btnNextNgay, 42, 38);

        dcNgayChon = new JDateChooser();
        dcNgayChon.setDateFormatString("dd/MM/yyyy");
        dcNgayChon.setPreferredSize(new Dimension(135, 38));

        pnlDateWrap.add(btnHomNay);
        pnlDateWrap.add(btnPrevNgay);
        pnlDateWrap.add(btnNextNgay);
        pnlDateWrap.add(dcNgayChon);

        btnHomNay.addActionListener(e -> {
            datNgayMacDinhHomNay();
            taiDanhSachBanTheoNgay();
        });

        btnPrevNgay.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date ngayDangChon = dcNgayChon.getDate() == null ? new Date() : dcNgayChon.getDate();
            cal.setTime(ngayDangChon);
            cal.add(Calendar.DATE, -1);
            dcNgayChon.setDate(cal.getTime());
            taiDanhSachBanTheoNgay();
        });

        btnNextNgay.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            Date ngayDangChon = dcNgayChon.getDate() == null ? new Date() : dcNgayChon.getDate();
            cal.setTime(ngayDangChon);
            cal.add(Calendar.DATE, 1);
            dcNgayChon.setDate(cal.getTime());
            taiDanhSachBanTheoNgay();
        });

        dcNgayChon.getDateEditor().addPropertyChangeListener("date", evt -> {
            if (dcNgayChon.getDate() != null) {
                taiDanhSachBanTheoNgay();
            }
        });

        return pnlDateWrap;
    }

    private void styleDateButton(JButton btn, int w, int h) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(BTN_DATE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
    }

    private void napKhuVucTuCSDL() {
        pnlTabsKhuVuc.removeAll();
        dsKhuVuc.clear();

        ButtonGroup group = new ButtonGroup();

        maKhuVucDangChon = "ALL";
        tenKhuVucDangChon = "Tất cả";

        JToggleButton btnTatCa = taoTabKhuVuc("Tất cả", false);
        group.add(btnTatCa);
        pnlTabsKhuVuc.add(btnTatCa);

        btnTatCa.addActionListener(e -> {
            maKhuVucDangChon = "ALL";
            tenKhuVucDangChon = "Tất cả";
            btnTatCa.setSelected(true);
            doDuLieuBanLenGiaoDien();
        });

        ArrayList<KhuVuc> ds = khuVucDAO.getAllKhuVuc();
        if (ds != null) {
            dsKhuVuc.addAll(ds);
        }

        for (KhuVuc kv : dsKhuVuc) {
            JToggleButton btnTab = taoTabKhuVuc(kv.getTenKhuVuc(), false);

            final String maKhuVuc = kv.getMaKhuVuc();
            final String tenKhuVuc = kv.getTenKhuVuc();

            btnTab.addActionListener(e -> {
                maKhuVucDangChon = maKhuVuc;
                tenKhuVucDangChon = tenKhuVuc;
                btnTab.setSelected(true);
                doDuLieuBanLenGiaoDien();
            });

            group.add(btnTab);
            pnlTabsKhuVuc.add(btnTab);
        }

        btnTatCa.setSelected(true);

        pnlTabsKhuVuc.revalidate();
        pnlTabsKhuVuc.repaint();
    }

    private JToggleButton taoTabKhuVuc(String tenKhuVuc, boolean selected) {
        JToggleButton btn = new JToggleButton(tenKhuVuc);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 22));
        btn.setPreferredSize(new Dimension(190, 50));
        btn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, TAB_BORDER));
        btn.setBackground(selected ? TAB_SELECTED : TAB_NORMAL);
        btn.setOpaque(true);

        btn.addItemListener(e -> btn.setBackground(btn.isSelected() ? TAB_SELECTED : TAB_NORMAL));
        return btn;
    }

    private void taiDanhSachBanTheoNgay() {
        try {
            if (dcNgayChon.getDate() == null) {
                datNgayMacDinhHomNay();
            }

            Date ngayChon = boTime(dcNgayChon.getDate());
            java.sql.Date ngaySql = new java.sql.Date(ngayChon.getTime());

            dsTatCaBanTheoNgay.clear();

            List<String[]> ds = banDAO.getDanhSachBanTheoNgay(ngaySql);
            if (ds != null) {
                for (String[] row : ds) {
                    if (row == null || row.length < 4) continue;

                    String trangThaiGoc = row[3];

                    // chỉ giữ đỏ nếu thực sự đang phục vụ
                    if (!"Bàn đang phục vụ".equalsIgnoreCase(chuanHoaTrangThai(trangThaiGoc))) {
                        row[3] = "Bàn trống";
                    }

                    dsTatCaBanTheoNgay.add(row);
                }
            }

            // sau đó mới xét phiếu đặt để chuyển đỏ đúng mốc 1 tiếng trước giờ đến
            capNhatTrangThaiBanDatTuPhieuDat(ngaySql);

            doDuLieuBanLenGiaoDien();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách bàn theo ngày.");
        }
    }

    private void capNhatTrangThaiBanDatTuPhieuDat(java.sql.Date ngaySql) {
        try {
            ArrayList<String[]> dsPhieu = phieuDatBanDAO.getPhieuDatBanTheoNgay(ngaySql);
            if (dsPhieu == null || dsPhieu.isEmpty()) return;

            Timestamp now = new Timestamp(System.currentTimeMillis());

            for (String[] phieu : dsPhieu) {
                if (phieu == null || phieu.length < 9) continue;

                String maBan = phieu[1];
                String thoiGianDenStr = phieu[5];
                String trangThaiPhieu = phieu[8];

                if (maBan == null || maBan.trim().isEmpty()) continue;
                if (thoiGianDenStr == null || thoiGianDenStr.trim().isEmpty()) continue;

                if (!"Đang chờ".equalsIgnoreCase(trangThaiPhieu)
                        && !"Đã đặt".equalsIgnoreCase(trangThaiPhieu)) {
                    continue;
                }

                Timestamp thoiGianDen;
                try {
                    thoiGianDen = Timestamp.valueOf(thoiGianDenStr);
                } catch (Exception ex) {
                    continue;
                }

                long motTieng = 60L * 60L * 1000L;
                Timestamp mocBatDauBanDat = new Timestamp(thoiGianDen.getTime() - motTieng);

                // chỉ từ 1 tiếng trước đến trước giờ đến mới chuyển đỏ
                if (!now.before(mocBatDauBanDat) && now.before(thoiGianDen)) {
                    for (String[] banRow : dsTatCaBanTheoNgay) {
                        if (banRow == null || banRow.length < 4) continue;

                        String maBanRow = banRow[0];
                        String trangThaiBan = banRow[3];

                        if (maBan.equalsIgnoreCase(maBanRow)) {
                            if (!"Bàn đang phục vụ".equalsIgnoreCase(chuanHoaTrangThai(trangThaiBan))) {
                                banRow[3] = "Bàn đặt";
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] timPhieuCanhBaoSapDat(String maBan) {
        try {
            if (dcNgayChon.getDate() == null) return null;

            java.sql.Date ngaySql = new java.sql.Date(boTime(dcNgayChon.getDate()).getTime());
            ArrayList<String[]> dsPhieu = phieuDatBanDAO.getPhieuDatBanTheoNgay(ngaySql);
            if (dsPhieu == null || dsPhieu.isEmpty()) return null;

            Timestamp now = new Timestamp(System.currentTimeMillis());

            for (String[] phieu : dsPhieu) {
                if (phieu == null || phieu.length < 9) continue;

                String maBanPhieu = phieu[1];
                String thoiGianDenStr = phieu[5];
                String trangThaiPhieu = phieu[8];

                if (maBanPhieu == null || !maBanPhieu.equalsIgnoreCase(maBan)) continue;
                if (thoiGianDenStr == null || thoiGianDenStr.trim().isEmpty()) continue;

                if (!"Đang chờ".equalsIgnoreCase(trangThaiPhieu)
                        && !"Đã đặt".equalsIgnoreCase(trangThaiPhieu)) {
                    continue;
                }

                Timestamp thoiGianDen;
                try {
                    thoiGianDen = Timestamp.valueOf(thoiGianDenStr);
                } catch (Exception ex) {
                    continue;
                }

                long motTieng = 60L * 60L * 1000L;
                long muoiLamPhut = 15L * 60L * 1000L;

                Timestamp mocBatDauBanDat = new Timestamp(thoiGianDen.getTime() - motTieng);   // 18:00
                Timestamp mocCanhBao = new Timestamp(mocBatDauBanDat.getTime() - muoiLamPhut); // 17:45

                // chỉ cảnh báo từ 17:45 đến trước 18:00
                if (!now.before(mocCanhBao) && now.before(mocBatDauBanDat)) {
                    return phieu;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void doDuLieuBanLenGiaoDien() {
        pnlBanContainer.removeAll();
        pnlBanContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 42, 34));

        if (dsTatCaBanTheoNgay.isEmpty()) {
            hienThiThongBaoTrong("Không có dữ liệu bàn.");
            return;
        }

        int count = 0;

        for (String[] item : dsTatCaBanTheoNgay) {
            if (item == null || item.length < 4) continue;

            String maBan = item[0];
            String tenBan = item[1];
            String tenKhuVuc = item[2];
            String trangThai = item[3];

            if (!"ALL".equalsIgnoreCase(maKhuVucDangChon)) {
                if (tenKhuVucDangChon != null && !tenKhuVucDangChon.trim().isEmpty()) {
                    if (!tenKhuVucDangChon.equalsIgnoreCase(tenKhuVuc)) {
                        continue;
                    }
                }
            }

            Ban ban = taoBanTam(maBan, tenBan, trangThai);
            pnlBanContainer.add(new TableCard(ban));
            count++;
        }

        if (count == 0) {
            hienThiThongBaoTrong("Ngày này khu vực này chưa có bàn hiển thị.");
            return;
        }

        pnlBanContainer.revalidate();
        pnlBanContainer.repaint();
    }

    private Ban taoBanTam(String maBan, String tenBan, String trangThai) {
        Ban ban = new Ban();
        ban.setMaBan(maBan);
        ban.setTenBan(tenBan);
        ban.setTrangThai(chuanHoaTrangThai(trangThai));
        ban.setSoChoNgoi(0);
        ban.setGhiChu("");
        return ban;
    }

    private void hienThiThongBaoTrong(String text) {
        pnlBanContainer.removeAll();
        pnlBanContainer.setLayout(new GridLayout(1, 1));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 24));
        lbl.setForeground(new Color(120, 120, 120));

        pnlBanContainer.add(lbl);
        pnlBanContainer.revalidate();
        pnlBanContainer.repaint();
    }

    private void datNgayMacDinhHomNay() {
        Date homNay = boTime(new Date());
        dcNgayChon.setDate(homNay);
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

    private void khoiDongDongHo() {
        capNhatThoiGian();
        timerDongHo = new Timer(1000, (ActionEvent e) -> capNhatThoiGian());
        timerDongHo.start();

        timerReloadBan = new Timer(30000, e -> taiDanhSachBanTheoNgay());
        timerReloadBan.start();
    }

    private void capNhatThoiGian() {
        Date now = new Date();
        lblGio.setText(new SimpleDateFormat("HH:mm:ss").format(now));
        lblNgay.setText(new SimpleDateFormat("dd/MM/yyyy").format(now));
    }

    private Color layMauTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            return BAN_TRONG;
        }

        String tt = trangThai.trim().toLowerCase();

        if (tt.equals("đang phục vụ") || tt.equals("dang phuc vu") || tt.equals("bàn đang phục vụ")) {
            return BAN_PHUC_VU;
        }
        if (tt.equals("đã đặt") || tt.equals("da dat") || tt.equals("bàn đặt") || tt.equals("ban dat")) {
            return BAN_DAT;
        }
        if (tt.equals("trống") || tt.equals("bàn trống") || tt.equals("ban trong")) {
            return BAN_TRONG;
        }

        return BAN_MAC_DINH;
    }

    private String chuanHoaTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            return "Bàn trống";
        }

        String tt = trangThai.trim().toLowerCase();

        if (tt.equals("đang phục vụ") || tt.equals("dang phuc vu") || tt.equals("bàn đang phục vụ")) {
            return "Bàn đang phục vụ";
        }
        if (tt.equals("đã đặt") || tt.equals("da dat") || tt.equals("bàn đặt") || tt.equals("ban dat")) {
            return "Bàn đặt";
        }
        if (tt.equals("trống") || tt.equals("bàn trống") || tt.equals("ban trong")) {
            return "Bàn trống";
        }

        return trangThai;
    }

    private class TableCard extends JButton {
        private static final long serialVersionUID = 1L;
        private final Ban ban;

        public TableCard(Ban ban) {
            this.ban = ban;

            setText(ban.getTenBan());
            setFont(new Font("SansSerif", Font.BOLD, 28));
            setForeground(Color.BLACK);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);

            int chieuRong = tinhDoRongNut(ban.getTenBan());
            setPreferredSize(new Dimension(chieuRong, 98));

            addActionListener(e -> xuLyChonBan());
        }

        private int tinhDoRongNut(String text) {
            Font font = new Font("SansSerif", Font.BOLD, 28);
            FontMetrics fm = getFontMetrics(font);
            int widthText = fm.stringWidth(text);
            int width = widthText + 85;

            if (width < 230) width = 230;
            if (width > 285) width = 285;

            return width;
        }

        private void xuLyChonBan() {
            String[] phieuCanhBao = timPhieuCanhBaoSapDat(ban.getMaBan());

            if (phieuCanhBao != null) {
                String tenKhach = phieuCanhBao[2] == null ? "" : phieuCanhBao[2];
                String thoiGianDen = phieuCanhBao[5] == null ? "" : phieuCanhBao[5];

                int chon = JOptionPane.showConfirmDialog(
                        Order_Ban_GUI.this,
                        "Bàn này sắp có khách đặt.\n"
                                + "Khách: " + tenKhach + "\n"
                                + "Giờ đến: " + thoiGianDen + "\n\n"
                                + "Bạn có chắc chắn muốn tiếp tục mở bàn không?",
                        "Cảnh báo bàn sắp đặt",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (chon != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // mở Order_Mon_GUI
            try {
            	Order_Mon_GUI orderMonGUI = new Order_Mon_GUI(
            	        taiKhoanDangNhap,
            	        ban.getMaBan(),
            	        ban.getTenBan()
            	);
            	orderMonGUI.setVisible(true);
            	dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        Order_Ban_GUI.this,
                        "Không thể mở màn hình order món.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(layMauTrangThai(ban.getTrangThai()));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    @Override
    public void dispose() {
        if (timerDongHo != null) {
            timerDongHo.stop();
        }
        if (timerReloadBan != null) {
            timerReloadBan.stop();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Order_Ban_GUI().setVisible(true));
    }

    public static class WrapLayout extends FlowLayout {
        private static final long serialVersionUID = 1L;

        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;

                Container container = target;
                while (container.getSize().width == 0 && container.getParent() != null) {
                    container = container.getParent();
                }

                targetWidth = container.getSize().width;

                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);

                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
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
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
                if (scrollPane != null) {
                    dim.width -= (hgap + 1);
                }

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
}