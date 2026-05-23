package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.KhuVuc_DAO;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;
import entity.TaiKhoan;

public class Ban_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String FILTER_ALL = "ALL";
    private static final String STATUS_TRONG = "Bàn trống";
    private static final String STATUS_DAT = "Đã đặt";
    private static final String STATUS_PHUC_VU = "Đang phục vụ";
    private static final String STATUS_BAO_TRI = "Bảo trì";

    @SuppressWarnings("unused")
    private final TaiKhoan taiKhoanDangNhap;
    private final Ban_DAO banDAO = new Ban_DAO();
    private final KhuVuc_DAO khuVucDAO = new KhuVuc_DAO();

    private final List<Ban> dsBan = new ArrayList<>();
    private final Map<String, KhuVuc> khuVucMap = new LinkedHashMap<>();
    private final Map<String, String> loaiBanMap = new LinkedHashMap<>();
    private final Map<String, String> hienThiTrangThaiMap = new HashMap<>();
    private final Map<String, JButton> floorButtonMap = new LinkedHashMap<>();
    private final Map<String, BanCard> banCardMap = new HashMap<>();

    private JPanel floorsContainer;
    private JPanel tabFilterPanel;
    private PromptTextField txtSearch;

    private JLabel lblTongBan;
    private JLabel lblBanTrong;
    private JLabel lblDaDat;
    private JLabel lblDangPhucVu;
    private JLabel lblDangChon;
    private JLabel lblChonMa;
    private JLabel lblChonTen;
    private JLabel lblChonKhu;
    private JLabel lblChonLoai;
    private JLabel lblChonChoNgoi;
    private JLabel lblChonTrangThai;
    private JTextArea txtGhiChu;

    private String floorFilter = FILTER_ALL;
    private Ban banDangChon;

    public Ban_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        add(createMainPanel(), BorderLayout.CENTER);
        napDuLieuVaRender(false);
    }

    public Ban_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(24, 0));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        mainPanel.add(createLeftPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);
        return mainPanel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 16));
        leftPanel.setOpaque(false);
        leftPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        floorsContainer = new JPanel();
        floorsContainer.setLayout(new BoxLayout(floorsContainer, BoxLayout.Y_AXIS));
        floorsContainer.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(floorsContainer, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý bàn");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        outer.add(lblTitle, BorderLayout.NORTH);

        JPanel actionCard = new JPanel(new BorderLayout(0, 12));
        actionCard.setBackground(Color.WHITE);
        actionCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        tabFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabFilterPanel.setOpaque(false);
        actionCard.add(tabFilterPanel, BorderLayout.NORTH);

        JPanel centerRows = new JPanel();
        centerRows.setOpaque(false);
        centerRows.setLayout(new BoxLayout(centerRows, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm bàn:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));

        txtSearch = new PromptTextField("Nhập mã bàn, tên bàn, khu vực, loại bàn hoặc trạng thái...");
        txtSearch.setPreferredSize(new Dimension(360, 36));
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(215, 215, 215), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { renderData(); }
            @Override public void removeUpdate(DocumentEvent e) { renderData(); }
            @Override public void changedUpdate(DocumentEvent e) { renderData(); }
        });

        JButton btnLamMoi = createActionButton("Làm mới", new Color(245, 235, 220), new Color(120, 90, 70));
        btnLamMoi.addActionListener(e -> napDuLieuVaRender(true));

        searchRow.add(lblSearch);
        searchRow.add(txtSearch);
        searchRow.add(btnLamMoi);
        centerRows.add(searchRow);
        centerRows.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnThem = createActionButton("Thêm bàn", new Color(225, 240, 255), new Color(40, 100, 180));
        btnThem.addActionListener(e -> moDialogThemBan());

        JButton btnSua = createActionButton("Sửa bàn", new Color(230, 244, 234), new Color(46, 125, 50));
        btnSua.addActionListener(e -> moDialogSuaBan());

        JButton btnXoa = createActionButton("Xóa bàn", new Color(255, 235, 238), new Color(198, 40, 40));
        btnXoa.addActionListener(e -> xuLyXoaBan());

        JButton btnChuyen = createActionButton("Chuyển khu vực", new Color(232, 245, 253), new Color(2, 119, 189));
        btnChuyen.addActionListener(e -> moDialogChuyenKhuVuc());

        buttonRow.add(btnThem);
        buttonRow.add(btnSua);
        buttonRow.add(btnXoa);
        buttonRow.add(btnChuyen);
        centerRows.add(buttonRow);

        actionCard.add(centerRows, BorderLayout.CENTER);
        outer.add(actionCard, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(350, 0));

        JPanel statsCard = createCard();
        statsCard.setLayout(new GridLayout(2, 2, 12, 12));
        lblTongBan = createMetricLabel("0");
        lblBanTrong = createMetricLabel("0");
        lblDaDat = createMetricLabel("0");
        lblDangPhucVu = createMetricLabel("0");
        statsCard.add(createMetricPanel("Tổng bàn", lblTongBan, new Color(245, 248, 255)));
        statsCard.add(createMetricPanel("Bàn trống", lblBanTrong, new Color(235, 248, 239)));
        statsCard.add(createMetricPanel("Đã đặt", lblDaDat, new Color(255, 248, 230)));
        statsCard.add(createMetricPanel("Đang phục vụ", lblDangPhucVu, new Color(255, 239, 239)));

        JPanel selectedCard = createCard();
        selectedCard.setLayout(new BorderLayout(0, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblDetail = new JLabel("Chi tiết bàn đang chọn");
        lblDetail.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(lblDetail, BorderLayout.WEST);

        lblDangChon = new JLabel("Chưa chọn bàn");
        lblDangChon.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblDangChon.setForeground(new Color(120, 120, 120));
        top.add(lblDangChon, BorderLayout.EAST);
        selectedCard.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        lblChonMa = addDetailRow(form, gbc, "Mã bàn:");
        lblChonTen = addDetailRow(form, gbc, "Tên bàn:");
        lblChonKhu = addDetailRow(form, gbc, "Khu vực:");
        lblChonLoai = addDetailRow(form, gbc, "Loại bàn:");
        lblChonChoNgoi = addDetailRow(form, gbc, "Số chỗ ngồi:");
        lblChonTrangThai = addDetailRow(form, gbc, "Trạng thái thực tế:");

        gbc.gridy++;
        JLabel lblNote = new JLabel("Ghi chú / nhật ký:");
        lblNote.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(lblNote, gbc);

        gbc.gridy++;
        txtGhiChu = new JTextArea(8, 20);
        txtGhiChu.setEditable(false);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        txtGhiChu.setBackground(new Color(250, 250, 250));

        JScrollPane noteScroll = new JScrollPane(txtGhiChu);
        noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        form.add(noteScroll, gbc);

        selectedCard.add(form, BorderLayout.CENTER);
        rightPanel.add(statsCard);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(selectedCard);
        rightPanel.add(Box.createVerticalGlue());
        resetSelectedInfo();
        return rightPanel;
    }

    private JPanel createMetricPanel(String title, JLabel valueLabel, Color bg) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(true);
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(14, 14, 14, 14)));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(90, 90, 90));
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createMetricLabel(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("SansSerif", Font.BOLD, 28));
        label.setForeground(new Color(45, 45, 45));
        return label;
    }

    private JLabel addDetailRow(JPanel container, GridBagConstraints gbc, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        container.add(lbl, gbc);
        gbc.gridy++;

        JLabel value = new JLabel("-");
        value.setOpaque(true);
        value.setBackground(Color.WHITE);
        value.setFont(new Font("SansSerif", Font.PLAIN, 14));
        value.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        container.add(value, gbc);
        gbc.gridy++;
        return value;
    }

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fg.brighter(), 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        return button;
    }

    private JButton createFilterButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        styleFilterButton(button, selected);
        return button;
    }

    private void styleFilterButton(JButton button, boolean selected) {
        if (selected) {
            button.setBackground(new Color(40, 100, 180));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(25, 75, 145), 1, true),
                    new EmptyBorder(9, 16, 9, 16)));
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(55, 55, 55));
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(215, 215, 215), 1, true),
                    new EmptyBorder(9, 16, 9, 16)));
        }
    }

    private void napDuLieuVaRender(boolean giuLaiLuaChon) {
        String maDangChon = banDangChon == null ? null : banDangChon.getMaBan();
        dsBan.clear();
        khuVucMap.clear();
        loaiBanMap.clear();
        hienThiTrangThaiMap.clear();
        banCardMap.clear();

        try {
            for (Object obj : khuVucDAO.getAllKhuVuc()) {
                if (obj instanceof KhuVuc) {
                    KhuVuc kv = (KhuVuc) obj;
                    khuVucMap.put(kv.getMaKhuVuc(), kv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            thongBaoLoi("Không tải được danh sách khu vực từ CSDL.");
        }

        napLoaiBanTuCSDL();

        try {
            for (Object obj : banDAO.getAllBan()) {
                if (obj instanceof Ban) {
                    Ban ban = (Ban) obj;
                    boSungThongTinBan(ban);
                    dsBan.add(ban);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            thongBaoLoi("Không tải được danh sách bàn từ CSDL.");
        }

        sapXepBan(dsBan);
        napTrangThaiHienTai();
        damBaoBoLocHopLe();
        buildFilterButtons();

        banDangChon = giuLaiLuaChon && maDangChon != null ? timBanTheoMa(maDangChon) : null;
        renderData();
        capNhatThongTinChon();
    }

    private void napLoaiBanTuCSDL() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return;
            String sql = "SELECT maLoaiBan, tenLoaiBan FROM LoaiBan ORDER BY maLoaiBan";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                loaiBanMap.put(rs.getString("maLoaiBan"), rs.getString("tenLoaiBan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }

        if (loaiBanMap.isEmpty()) {
            loaiBanMap.put("LB001", "Bàn 2 người");
            loaiBanMap.put("LB002", "Bàn 4 người");
            loaiBanMap.put("LB003", "Bàn 6 người");
            loaiBanMap.put("LB004", "Bàn 8 người");
        }
    }

    private void boSungThongTinBan(Ban ban) {
        if (ban == null) return;

        if (ban.getMaKhuVuc() != null) {
            String maKV = ban.getMaKhuVuc().getMaKhuVuc();
            KhuVuc kvDayDu = khuVucMap.get(maKV);
            if (kvDayDu != null) ban.setMaKhuVuc(kvDayDu);
        }

        if (ban.getMaLoaiBan() != null) {
            String maLoai = ban.getMaLoaiBan().getMaLoaiBan();
            String tenLoai = loaiBanMap.get(maLoai);
            if (tenLoai != null) ban.setMaLoaiBan(new LoaiBan(maLoai, tenLoai));
        }
    }

    private void napTrangThaiHienTai() {
        hienThiTrangThaiMap.clear();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return;

            String sql =
                    "SELECT b.maBan, " +
                    "CASE " +
                    "WHEN b.trangThai = N'Bảo trì' THEN N'Bảo trì' " +
                    "WHEN EXISTS ( " +
                    "   SELECT 1 FROM HoaDon_Ban hdb " +
                    "   JOIN HoaDon hd ON hd.maHD = hdb.maHD " +
                    "   WHERE hdb.maBan = b.maBan " +
                    "   AND hd.thoiGianRa IS NULL " +
                    "   AND (hd.trangThai IS NULL OR hd.trangThai NOT IN " +
                    "       (N'Đã thanh toán', N'Đã hủy', N'Hủy', N'Hoàn tất', N'Đã hoàn thành')) " +
                    ") THEN N'Đang phục vụ' " +
                    "WHEN EXISTS ( " +
                    "   SELECT 1 FROM PhieuDatBan_Ban pdbb " +
                    "   JOIN PhieuDatBan pdb ON pdb.maPhieuDatBan = pdbb.maPhieuDatBan " +
                    "   WHERE pdbb.maBan = b.maBan " +
                    "   AND CAST(pdb.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) " +
                    "   AND (pdb.trangThai IS NULL OR pdb.trangThai IN " +
                    "       (N'Đang chờ', N'Đã đặt', N'Chờ nhận bàn')) " +
                    ") THEN N'Đã đặt' " +
                    "ELSE N'Bàn trống' END AS trangThaiHienTai " +
                    "FROM Ban b";

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                hienThiTrangThaiMap.put(rs.getString("maBan"), chuanHoaTrangThai(rs.getString("trangThaiHienTai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
    }

    private String layTrangThaiHienTaiTuCSDL(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) return STATUS_TRONG;

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return STATUS_PHUC_VU;

            String sql =
                    "SELECT CASE " +
                    "WHEN b.trangThai = N'Bảo trì' THEN N'Bảo trì' " +
                    "WHEN EXISTS ( " +
                    "   SELECT 1 FROM HoaDon_Ban hdb " +
                    "   JOIN HoaDon hd ON hd.maHD = hdb.maHD " +
                    "   WHERE hdb.maBan = b.maBan " +
                    "   AND hd.thoiGianRa IS NULL " +
                    "   AND (hd.trangThai IS NULL OR hd.trangThai NOT IN " +
                    "       (N'Đã thanh toán', N'Đã hủy', N'Hủy', N'Hoàn tất', N'Đã hoàn thành')) " +
                    ") THEN N'Đang phục vụ' " +
                    "WHEN EXISTS ( " +
                    "   SELECT 1 FROM PhieuDatBan_Ban pdbb " +
                    "   JOIN PhieuDatBan pdb ON pdb.maPhieuDatBan = pdbb.maPhieuDatBan " +
                    "   WHERE pdbb.maBan = b.maBan " +
                    "   AND CAST(pdb.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) " +
                    "   AND (pdb.trangThai IS NULL OR pdb.trangThai IN " +
                    "       (N'Đang chờ', N'Đã đặt', N'Chờ nhận bàn')) " +
                    ") THEN N'Đã đặt' " +
                    "ELSE N'Bàn trống' END AS trangThaiHienTai " +
                    "FROM Ban b WHERE b.maBan = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            rs = stmt.executeQuery();
            if (rs.next()) return chuanHoaTrangThai(rs.getString("trangThaiHienTai"));
            return STATUS_TRONG;
        } catch (Exception e) {
            e.printStackTrace();
            return STATUS_PHUC_VU;
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
    }

    private boolean laBanDangDatHoacPhucVu(String maBan) {
        String trangThai = layTrangThaiHienTaiTuCSDL(maBan);
        return STATUS_DAT.equalsIgnoreCase(trangThai) || STATUS_PHUC_VU.equalsIgnoreCase(trangThai);
    }

    private void buildFilterButtons() {
        if (tabFilterPanel == null) return;
        tabFilterPanel.removeAll();
        floorButtonMap.clear();
        addFloorFilterButton("Tất cả", FILTER_ALL);
        for (KhuVuc kv : khuVucMap.values()) {
            addFloorFilterButton(labelTabKhuVuc(kv), kv.getMaKhuVuc());
        }
        capNhatStyleNutLoc();
        tabFilterPanel.revalidate();
        tabFilterPanel.repaint();
    }

    private String labelTabKhuVuc(KhuVuc kv) {
        if (kv == null) return "Khu vực";
        String kyHieu = safe(kv.getKyHieu(), "").trim();
        String ten = safe(kv.getTenKhuVuc(), kv.getMaKhuVuc()).trim();
        return kyHieu.isEmpty() ? ten : ten + " (" + kyHieu + ")";
    }

    private void addFloorFilterButton(String label, String key) {
        JButton button = createFilterButton(label, Objects.equals(key, floorFilter));
        button.addActionListener(e -> {
            floorFilter = key;
            capNhatStyleNutLoc();
            renderData();
        });
        floorButtonMap.put(key, button);
        tabFilterPanel.add(button);
    }

    private void capNhatStyleNutLoc() {
        for (Map.Entry<String, JButton> entry : floorButtonMap.entrySet()) {
            styleFilterButton(entry.getValue(), Objects.equals(entry.getKey(), floorFilter));
        }
    }

    private void damBaoBoLocHopLe() {
        if (!FILTER_ALL.equals(floorFilter) && !khuVucMap.containsKey(floorFilter)) {
            floorFilter = FILTER_ALL;
        }
    }

    private void renderData() {
        if (floorsContainer == null) return;
        floorsContainer.removeAll();
        banCardMap.clear();

        Map<String, List<Ban>> grouped = new LinkedHashMap<>();
        for (String maKV : khuVucMap.keySet()) grouped.put(maKV, new ArrayList<>());

        for (Ban ban : dsBan) {
            if (!phuHopBoLoc(ban)) continue;
            String maKV = ban.getMaKhuVuc() == null ? "" : ban.getMaKhuVuc().getMaKhuVuc();
            grouped.computeIfAbsent(maKV, k -> new ArrayList<>()).add(ban);
        }

        for (Map.Entry<String, KhuVuc> entry : khuVucMap.entrySet()) {
            String maKV = entry.getKey();
            if (!FILTER_ALL.equals(floorFilter) && !Objects.equals(floorFilter, maKV)) continue;
            floorsContainer.add(createFloorSection(entry.getValue(), grouped.getOrDefault(maKV, Collections.emptyList())));
            floorsContainer.add(Box.createRigidArea(new Dimension(0, 16)));
        }

        capNhatThongKe();
        capNhatHighlightLuaChon();
        floorsContainer.revalidate();
        floorsContainer.repaint();
    }

    private JPanel createFloorSection(KhuVuc khuVuc, List<Ban> dsTheoKV) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(true);
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel(xayDungTenHienThiKhuVuc(khuVuc));
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel lblSub = new JLabel("Ký hiệu: " + safe(khuVuc.getKyHieu(), "-")
                + " • Sức chứa cấu hình: " + khuVuc.getSoLuongBan()
                + " bàn • Đang có: " + dsTheoKV.size() + " bàn");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(110, 110, 110));
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        header.add(titlePanel, BorderLayout.WEST);

        JLabel lblCount = new JLabel(dsTheoKV.size() + " / " + khuVuc.getSoLuongBan() + " bàn");
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCount.setForeground(dsTheoKV.size() > khuVuc.getSoLuongBan() ? new Color(198, 40, 40) : new Color(40, 100, 180));
        header.add(lblCount, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        if (dsTheoKV.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setOpaque(true);
            empty.setBackground(new Color(249, 249, 249));
            empty.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(235, 235, 235), 1, true),
                    new EmptyBorder(20, 20, 20, 20)));
            JLabel lblEmpty = new JLabel("Không có bàn nào khớp bộ lọc ở khu vực này.", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 14));
            lblEmpty.setForeground(new Color(130, 130, 130));
            empty.add(lblEmpty, BorderLayout.CENTER);
            wrapper.add(empty, BorderLayout.CENTER);
            return wrapper;
        }

        JPanel itemsWrap = new JPanel(new GridLayout(0, 5, 12, 16));
        itemsWrap.setOpaque(false);
        for (Ban ban : dsTheoKV) {
            BanCard card = new BanCard(ban);
            banCardMap.put(ban.getMaBan(), card);
            itemsWrap.add(card);
        }
        wrapper.add(itemsWrap, BorderLayout.CENTER);
        return wrapper;
    }

    private boolean phuHopBoLoc(Ban ban) {
        if (ban == null) return false;
        String maKV = ban.getMaKhuVuc() == null ? "" : ban.getMaKhuVuc().getMaKhuVuc();
        if (!FILTER_ALL.equals(floorFilter) && !Objects.equals(floorFilter, maKV)) return false;

        String keyword = normalize(txtSearch == null ? "" : txtSearch.getText());
        if (keyword.isEmpty()) return true;

        return normalize(ban.getMaBan()).contains(keyword)
                || normalize(layTenBanHienThi(ban)).contains(keyword)
                || normalize(layTenKhuVuc(ban)).contains(keyword)
                || normalize(layTenLoaiBan(ban)).contains(keyword)
                || normalize(layTrangThaiHienThi(ban)).contains(keyword);
    }

    private void capNhatThongKe() {
        int tong = 0;
        int trong = 0;
        int daDat = 0;
        int dangPhucVu = 0;

        for (Ban ban : dsBan) {
            if (!phuHopBoLoc(ban)) continue;
            tong++;
            String status = layTrangThaiHienThi(ban);
            if (laBanTrong(status)) trong++;
            else if (STATUS_DAT.equalsIgnoreCase(status)) daDat++;
            else if (STATUS_PHUC_VU.equalsIgnoreCase(status)) dangPhucVu++;
        }

        lblTongBan.setText(String.valueOf(tong));
        lblBanTrong.setText(String.valueOf(trong));
        lblDaDat.setText(String.valueOf(daDat));
        lblDangPhucVu.setText(String.valueOf(dangPhucVu));
    }

    private void chonBan(Ban ban) {
        this.banDangChon = ban;
        capNhatThongTinChon();
        capNhatHighlightLuaChon();
    }

    private void capNhatThongTinChon() {
        if (banDangChon == null) {
            resetSelectedInfo();
            return;
        }
        lblDangChon.setText("Đã chọn");
        lblDangChon.setForeground(new Color(46, 125, 50));
        lblChonMa.setText(safe(banDangChon.getMaBan(), "-"));
        lblChonTen.setText(safe(layTenBanHienThi(banDangChon), "-"));
        lblChonKhu.setText(safe(layTenKhuVuc(banDangChon), "-"));
        lblChonLoai.setText(safe(layTenLoaiBan(banDangChon), "-"));
        lblChonChoNgoi.setText(String.valueOf(banDangChon.getSoChoNgoi()));
        lblChonTrangThai.setText(safe(layTrangThaiHienThi(banDangChon), "-"));
        txtGhiChu.setText(xayDungGhiChuChiTiet(banDangChon));
    }

    private void resetSelectedInfo() {
        if (lblDangChon == null) return;
        lblDangChon.setText("Chưa chọn bàn");
        lblDangChon.setForeground(new Color(120, 120, 120));
        lblChonMa.setText("-");
        lblChonTen.setText("-");
        lblChonKhu.setText("-");
        lblChonLoai.setText("-");
        lblChonChoNgoi.setText("-");
        lblChonTrangThai.setText("-");
        txtGhiChu.setText("Chọn một bàn ở danh sách bên trái để xem thông tin chi tiết, chỉnh sửa, xóa hoặc chuyển khu vực.");
    }

    private void capNhatHighlightLuaChon() {
        for (BanCard card : banCardMap.values()) {
            card.capNhatStyle();
        }
    }

    private void taiLaiVaChonBan(String maBanCanChon) {
        napDuLieuVaRender(false);
        banDangChon = timBanTheoMa(maBanCanChon);
        capNhatThongTinChon();
        capNhatHighlightLuaChon();
    }

    private void moDialogThemBan() {
        if (khuVucMap.isEmpty()) {
            thongBao("Không có dữ liệu khu vực để thêm bàn.");
            return;
        }

        JComboBox<ComboItem<KhuVuc>> cboKhuVuc = new JComboBox<>(taoDanhSachKhuVuc());
        cboKhuVuc.setRenderer(new ComboRenderer());
        JComboBox<ComboItem<String>> cboLoaiBan = new JComboBox<>(taoDanhSachLoaiBan());
        cboLoaiBan.setRenderer(new ComboRenderer());
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_TRONG, STATUS_BAO_TRI });
        JTextArea txtAreaGhiChu = createDialogTextArea();

        JLabel lblPreviewMa = createPreviewLabel();
        JLabel lblPreviewTen = createPreviewLabel();
        JLabel lblPreviewSoCho = createPreviewLabel();

        Runnable capNhatPreview = () -> {
            ComboItem<KhuVuc> itemKV = getSelectedComboItem(cboKhuVuc);
            ComboItem<String> itemLoai = getSelectedComboItem(cboLoaiBan);
            if (itemKV == null || itemKV.getValue() == null) {
                lblPreviewMa.setText("Mã bàn dự kiến: -");
                lblPreviewTen.setText("Tên bàn dự kiến: -");
                lblPreviewSoCho.setText("Số chỗ ngồi tự động: -");
                return;
            }
            String maMoi = taoMaBanKhaDung(itemKV.getValue(), null);
            lblPreviewMa.setText("Mã bàn dự kiến: " + safe(maMoi, "-"));
            lblPreviewTen.setText("Tên bàn dự kiến: " + safe(taoTenBanTheoMa(maMoi), "-"));
            lblPreviewSoCho.setText(itemLoai == null || itemLoai.getValue() == null
                    ? "Số chỗ ngồi tự động: -"
                    : "Số chỗ ngồi tự động: " + macDinhChoNgoiTheoLoai(itemLoai.getValue()));
        };
        cboKhuVuc.addActionListener(e -> capNhatPreview.run());
        cboLoaiBan.addActionListener(e -> capNhatPreview.run());
        capNhatPreview.run();

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Khu vực", cboKhuVuc);
        addFormRow(panel, 1, "Loại bàn", cboLoaiBan);
        addFormRow(panel, 2, "Trạng thái", cboTrangThai);
        addFormRow(panel, 3, "Ghi chú", createDialogScrollPane(txtAreaGhiChu));
        addFormRow(panel, 4, "Mã bàn", lblPreviewMa);
        addFormRow(panel, 5, "Tên bàn", lblPreviewTen);
        addFormRow(panel, 6, "Số chỗ", lblPreviewSoCho);

        JPanel wrapper = createDialogWrapper("Thêm bàn mới", "Mã bàn và tên bàn được sinh theo ký hiệu khu vực đang chọn.", panel, 720, 620);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, wrapper, "Thêm bàn mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            ComboItem<KhuVuc> itemKV = getSelectedComboItem(cboKhuVuc);
            ComboItem<String> itemLoai = getSelectedComboItem(cboLoaiBan);
            if (itemKV == null || itemKV.getValue() == null) {
                thongBao("Vui lòng chọn khu vực.");
                continue;
            }
            if (itemLoai == null || itemLoai.getValue() == null) {
                thongBao("Vui lòng chọn loại bàn.");
                continue;
            }

            KhuVuc kv = itemKV.getValue();
            if (!khuVucDangHoatDong(kv)) {
                thongBao("Khu vực này đang không hoạt động nên không thể thêm bàn.");
                continue;
            }

            String maMoi = taoMaBanKhaDung(kv, null);
            if (maMoi == null || maMoi.trim().isEmpty()) {
                thongBao("Không thể tự sinh mã bàn. Khu vực có thể đã đủ sức chứa hoặc chưa có ký hiệu.");
                continue;
            }
            if (timBanTheoMa(maMoi) != null) {
                thongBao("Mã bàn " + maMoi + " đã tồn tại. Vui lòng làm mới dữ liệu rồi thử lại.");
                return;
            }

            int soCho = macDinhChoNgoiTheoLoai(itemLoai.getValue());
            String tenMoi = taoTenBanTheoMa(maMoi);
            String trangThai = chuanHoaTrangThai(Objects.toString(cboTrangThai.getSelectedItem(), STATUS_TRONG));
            String ghiChu = catGhiChuBan(nullIfBlank(txtAreaGhiChu.getText()));

            Ban banMoi = new Ban(maMoi, kv, new LoaiBan(itemLoai.getValue(), itemLoai.getLabel()), tenMoi, ghiChu, soCho, trangThai);
            if (banDAO.themBan(banMoi)) {
                thongBao("Đã thêm bàn thành công.\nMã bàn: " + maMoi + "\nTên bàn: " + tenMoi + "\nSố chỗ: " + soCho);
                taiLaiVaChonBan(maMoi);
                return;
            }
            thongBaoLoi("Thêm bàn thất bại. Kiểm tra lại dữ liệu hoặc kết nối CSDL.");
            return;
        }
    }

    private void moDialogSuaBan() {
        Ban ban = yeuCauChonBan();
        if (ban == null) return;

        JComboBox<ComboItem<String>> cboLoaiBan = new JComboBox<>(taoDanhSachLoaiBan());
        cboLoaiBan.setRenderer(new ComboRenderer());
        chonLoaiBanTrongCombo(cboLoaiBan, ban.getMaLoaiBan() == null ? null : ban.getMaLoaiBan().getMaLoaiBan());

        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_TRONG, STATUS_BAO_TRI });
        String status = layTrangThaiHienThi(ban);
        cboTrangThai.setSelectedItem(STATUS_BAO_TRI.equalsIgnoreCase(status) ? STATUS_BAO_TRI : STATUS_TRONG);

        JTextArea txtAreaGhiChu = createDialogTextArea();
        txtAreaGhiChu.setText(safe(ban.getGhiChu(), ""));

        JLabel lblMaBan = createPreviewLabel("Mã bàn: " + ban.getMaBan());
        JLabel lblTenBan = createPreviewLabel("Tên bàn: " + layTenBanHienThi(ban));
        JLabel lblKhuVuc = createPreviewLabel("Khu vực: " + layTenKhuVuc(ban));

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Mã bàn", lblMaBan);
        addFormRow(panel, 1, "Tên bàn", lblTenBan);
        addFormRow(panel, 2, "Khu vực", lblKhuVuc);
        addFormRow(panel, 3, "Loại bàn", cboLoaiBan);
        addFormRow(panel, 4, "Trạng thái", cboTrangThai);
        addFormRow(panel, 5, "Ghi chú", createDialogScrollPane(txtAreaGhiChu));

        JPanel wrapper = createDialogWrapper("Sửa thông tin bàn", "Không cho sửa mã bàn trực tiếp. Nếu muốn đổi khu vực hãy dùng chức năng Chuyển khu vực.", panel, 720, 560);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, wrapper, "Sửa thông tin bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            ComboItem<String> itemLoai = getSelectedComboItem(cboLoaiBan);
            if (itemLoai == null || itemLoai.getValue() == null) {
                thongBao("Vui lòng chọn loại bàn.");
                continue;
            }

            String trangThaiMoi = chuanHoaTrangThai(Objects.toString(cboTrangThai.getSelectedItem(), STATUS_TRONG));
            if (STATUS_BAO_TRI.equalsIgnoreCase(trangThaiMoi) && coDuLieuHoatDong(ban.getMaBan())) {
                thongBao("Không thể chuyển sang bảo trì vì bàn đang có đặt bàn hoặc hóa đơn đang hoạt động.");
                continue;
            }

            ban.setTenBan(layTenBanHienThi(ban));
            ban.setMaLoaiBan(new LoaiBan(itemLoai.getValue(), itemLoai.getLabel()));
            ban.setSoChoNgoi(macDinhChoNgoiTheoLoai(itemLoai.getValue()));
            ban.setTrangThai(trangThaiMoi);
            ban.setGhiChu(catGhiChuBan(nullIfBlank(txtAreaGhiChu.getText())));

            if (banDAO.capNhatBan(ban)) {
                thongBao("Đã cập nhật bàn " + ban.getMaBan() + ".");
                taiLaiVaChonBan(ban.getMaBan());
                return;
            }
            thongBaoLoi("Cập nhật thất bại. Kiểm tra lại dữ liệu hoặc kết nối CSDL.");
            return;
        }
    }

    private void xuLyXoaBan() {
        Ban ban = yeuCauChonBan();
        if (ban == null) return;

        String status = layTrangThaiHienTaiTuCSDL(ban.getMaBan());
        if (!laBanTrong(status)) {
            thongBao("Chỉ được xóa bàn đang trống.\nBàn hiện tại đang ở trạng thái: " + status);
            return;
        }

        if (coDuLieuLienQuan(ban.getMaBan())) {
            thongBao("Không thể xóa vì bàn này đã có dữ liệu hóa đơn hoặc phiếu đặt bàn liên quan.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa bàn " + ban.getMaBan() + " không?\nBàn sẽ bị xóa khỏi cơ sở dữ liệu nếu chưa từng phát sinh hóa đơn hoặc phiếu đặt.",
                "Xác nhận xóa bàn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (banDAO.xoaBan(ban.getMaBan())) {
            thongBao("Đã xóa bàn " + ban.getMaBan() + ".");
            banDangChon = null;
            napDuLieuVaRender(false);
        } else {
            thongBaoLoi("Xóa bàn thất bại.");
        }
    }

    private void moDialogChuyenKhuVuc() {
        Ban ban = yeuCauChonBan();
        if (ban == null) return;

        // CHẶN 1: bàn nguồn đang Đã đặt / Đang phục vụ thì không cho chuyển.
        String trangThaiNguon = layTrangThaiHienTaiTuCSDL(ban.getMaBan());
        if (!laBanTrong(trangThaiNguon) && !STATUS_BAO_TRI.equalsIgnoreCase(trangThaiNguon)) {
            thongBao("Không thể chuyển bàn.\nBàn nguồn " + ban.getMaBan()
                    + " đang ở trạng thái: " + trangThaiNguon
                    + ".\nChỉ được chuyển bàn trống hoặc bàn bảo trì.");
            return;
        }

        if (khuVucMap.size() < 2) {
            thongBao("Cần có ít nhất 2 khu vực để chuyển bàn.");
            return;
        }

        JComboBox<ComboItem<KhuVuc>> cboKhuVucDich = new JComboBox<>(taoDanhSachKhuVuc());
        cboKhuVucDich.setRenderer(new ComboRenderer());
        String maKhuVucHienTai = ban.getMaKhuVuc() == null ? null : ban.getMaKhuVuc().getMaKhuVuc();
        boChonKhuVucKhac(cboKhuVucDich, maKhuVucHienTai);

        JLabel lblMaBanMoi = createPreviewLabel();
        JLabel lblTenBanMoi = createPreviewLabel();
        JLabel lblCanhBao = createPreviewLabel();
        JTextArea txtLyDo = createDialogTextArea();
        txtLyDo.setRows(4);

        Runnable capNhatPreview = () -> {
            ComboItem<KhuVuc> item = getSelectedComboItem(cboKhuVucDich);
            if (item == null || item.getValue() == null) {
                lblMaBanMoi.setText("Mã bàn sau khi chuyển: -");
                lblTenBanMoi.setText("Tên bàn sau khi chuyển: -");
                lblCanhBao.setText("Trạng thái bàn đích: -");
                return;
            }
            KhuVuc khuVucDich = item.getValue();
            String maMoi = taoMaBanKhaDung(khuVucDich, ban.getMaBan());
            lblMaBanMoi.setText("Mã bàn sau khi chuyển: " + safe(maMoi, "-"));
            lblTenBanMoi.setText("Tên bàn sau khi chuyển: " + safe(taoTenBanTheoMa(maMoi), "-"));
            if (maMoi == null) {
                lblCanhBao.setText("Trạng thái bàn đích: Không còn mã bàn trống phù hợp trong khu vực này");
            } else {
                Ban banDich = timBanTheoMa(maMoi);
                lblCanhBao.setText(banDich == null
                        ? "Trạng thái bàn đích: Mã mới chưa tồn tại, có thể chuyển"
                        : "Trạng thái bàn đích: " + layTrangThaiHienTaiTuCSDL(maMoi));
            }
        };
        cboKhuVucDich.addActionListener(e -> capNhatPreview.run());
        capNhatPreview.run();

        JPanel formPanel = createFormPanel();
        addFormRow(formPanel, 0, "Bàn hiện tại", createPreviewLabel(ban.getMaBan() + " - " + layTenBanHienThi(ban)));
        addFormRow(formPanel, 1, "Khu vực hiện tại", createPreviewLabel(layTenKhuVuc(ban)));
        addFormRow(formPanel, 2, "Khu vực đích", cboKhuVucDich);
        addFormRow(formPanel, 3, "Mã mới", lblMaBanMoi);
        addFormRow(formPanel, 4, "Tên mới", lblTenBanMoi);
        addFormRow(formPanel, 5, "Kiểm tra bàn đích", lblCanhBao);
        addFormRow(formPanel, 6, "Lý do chuyển", createDialogScrollPane(txtLyDo));

        JPanel wrapper = createDialogWrapper(
                "Chuyển khu vực bàn",
                "Hệ thống kiểm tra trạng thái thực tế trước khi chuyển. Không cho chuyển bàn nguồn đang đặt/đang phục vụ và không cho chuyển sang bàn đích đang đặt/đang phục vụ.",
                formPanel, 760, 620);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, wrapper, "Chuyển khu vực bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            ComboItem<KhuVuc> item = getSelectedComboItem(cboKhuVucDich);
            if (item == null || item.getValue() == null) {
                thongBao("Vui lòng chọn khu vực đích.");
                continue;
            }

            KhuVuc khuVucDich = item.getValue();
            if (!khuVucDangHoatDong(khuVucDich)) {
                thongBao("Khu vực đích đang không hoạt động nên không thể chuyển bàn.");
                continue;
            }

            String lyDo = safe(txtLyDo.getText(), "").trim();
            if (lyDo.isEmpty()) {
                thongBao("Vui lòng nhập lý do chuyển khu vực.");
                continue;
            }

            // CHẶN 2: kiểm tra lại bàn nguồn ngay lúc bấm OK để tránh dữ liệu bị thay đổi sau khi mở dialog.
            String trangThaiNguonMoiNhat = layTrangThaiHienTaiTuCSDL(ban.getMaBan());
            if (!laBanTrong(trangThaiNguonMoiNhat) && !STATUS_BAO_TRI.equalsIgnoreCase(trangThaiNguonMoiNhat)) {
                thongBao("Không thể chuyển bàn.\nBàn nguồn " + ban.getMaBan()
                        + " hiện đang ở trạng thái: " + trangThaiNguonMoiNhat + ".");
                continue;
            }

            String maBanMoi = taoMaBanKhaDung(khuVucDich, ban.getMaBan());
            String tenBanMoi = taoTenBanTheoMa(maBanMoi);

            if (maBanMoi == null || maBanMoi.trim().isEmpty()) {
                thongBao("Không thể tự sinh mã bàn mới. Khu vực đích có thể đã đủ bàn hoặc không còn mã bàn trống.");
                continue;
            }

            Ban banDichDaTonTai = timBanTheoMa(maBanMoi);
            if (banDichDaTonTai != null) {
                String trangThaiDich = layTrangThaiHienTaiTuCSDL(maBanMoi);
                // CHẶN 3: bàn đích đang Đã đặt / Đang phục vụ thì không cho chuyển.
                if (STATUS_DAT.equalsIgnoreCase(trangThaiDich) || STATUS_PHUC_VU.equalsIgnoreCase(trangThaiDich)) {
                    thongBao("Không thể chuyển sang bàn " + maBanMoi
                            + " vì bàn này đang ở trạng thái: " + trangThaiDich + ".");
                    continue;
                }
                thongBao("Không thể chuyển sang mã " + maBanMoi
                        + " vì mã bàn này đã tồn tại. Vui lòng làm mới dữ liệu rồi thử lại.");
                continue;
            }

            if (thucHienChuyenKhuVuc(ban, khuVucDich, maBanMoi, tenBanMoi, lyDo)) {
                thongBao("Đã chuyển bàn thành công.\nMã cũ: " + ban.getMaBan()
                        + "\nMã mới: " + maBanMoi + "\nTên mới: " + tenBanMoi);
                taiLaiVaChonBan(maBanMoi);
            } else {
                thongBaoLoi("Chuyển khu vực thất bại. Kiểm tra dữ liệu khóa ngoại hoặc kết nối CSDL.");
            }
            return;
        }
    }

    private boolean thucHienChuyenKhuVuc(Ban banCu, KhuVuc khuVucDich, String maBanMoi, String tenBanMoi, String lyDo) {
        Connection con = null;
        PreparedStatement stmtInsert = null;
        PreparedStatement stmtUpdateHDB = null;
        PreparedStatement stmtUpdatePDBB = null;
        PreparedStatement stmtDelete = null;
        boolean oldAutoCommit = true;

        try {
            con = ConnectDB.getConnection();
            if (con == null) return false;

            oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            String ghiChuMoi = capNhatGhiChuSauKhiChuyen(banCu, khuVucDich, maBanMoi, lyDo);
            String maLoaiBan = banCu.getMaLoaiBan() == null ? null : banCu.getMaLoaiBan().getMaLoaiBan();
            if (maLoaiBan == null || maLoaiBan.trim().isEmpty()) maLoaiBan = layMaLoaiBanDauTien();

            String sqlInsert = "INSERT INTO Ban(maBan, maKhuVuc, maLoaiBan, tenBan, ghiChu, soChoNgoi, trangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtInsert = con.prepareStatement(sqlInsert);
            stmtInsert.setString(1, maBanMoi);
            stmtInsert.setString(2, khuVucDich.getMaKhuVuc());
            stmtInsert.setString(3, maLoaiBan);
            stmtInsert.setString(4, tenBanMoi);
            stmtInsert.setString(5, ghiChuMoi);
            stmtInsert.setInt(6, banCu.getSoChoNgoi());
            stmtInsert.setString(7, chuanHoaTrangThai(banCu.getTrangThai()));
            stmtInsert.executeUpdate();

            stmtUpdateHDB = con.prepareStatement("UPDATE HoaDon_Ban SET maBan = ? WHERE maBan = ?");
            stmtUpdateHDB.setString(1, maBanMoi);
            stmtUpdateHDB.setString(2, banCu.getMaBan());
            stmtUpdateHDB.executeUpdate();

            stmtUpdatePDBB = con.prepareStatement("UPDATE PhieuDatBan_Ban SET maBan = ? WHERE maBan = ?");
            stmtUpdatePDBB.setString(1, maBanMoi);
            stmtUpdatePDBB.setString(2, banCu.getMaBan());
            stmtUpdatePDBB.executeUpdate();

            stmtDelete = con.prepareStatement("DELETE FROM Ban WHERE maBan = ?");
            stmtDelete.setString(1, banCu.getMaBan());
            stmtDelete.executeUpdate();

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            closeQuietly(stmtDelete);
            closeQuietly(stmtUpdatePDBB);
            closeQuietly(stmtUpdateHDB);
            closeQuietly(stmtInsert);
            if (con != null) {
                try { con.setAutoCommit(oldAutoCommit); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private boolean coDuLieuHoatDong(String maBan) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return true;

            String sql =
                    "SELECT TOP 1 1 FROM Ban b WHERE b.maBan = ? AND (" +
                    "EXISTS ( " +
                    "   SELECT 1 FROM HoaDon_Ban hdb " +
                    "   JOIN HoaDon hd ON hd.maHD = hdb.maHD " +
                    "   WHERE hdb.maBan = b.maBan " +
                    "   AND hd.thoiGianRa IS NULL " +
                    "   AND (hd.trangThai IS NULL OR hd.trangThai NOT IN " +
                    "       (N'Đã thanh toán', N'Đã hủy', N'Hủy', N'Hoàn tất', N'Đã hoàn thành')) " +
                    ") OR EXISTS ( " +
                    "   SELECT 1 FROM PhieuDatBan_Ban pdbb " +
                    "   JOIN PhieuDatBan pdb ON pdb.maPhieuDatBan = pdbb.maPhieuDatBan " +
                    "   WHERE pdbb.maBan = b.maBan " +
                    "   AND CAST(pdb.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) " +
                    "   AND (pdb.trangThai IS NULL OR pdb.trangThai IN " +
                    "       (N'Đang chờ', N'Đã đặt', N'Chờ nhận bàn')) " +
                    "))";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
    }

    private boolean coDuLieuLienQuan(String maBan) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return true;

            String sql =
                    "SELECT " +
                    "(SELECT COUNT(*) FROM HoaDon_Ban WHERE maBan = ?) + " +
                    "(SELECT COUNT(*) FROM PhieuDatBan_Ban WHERE maBan = ?) AS soDong";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            stmt.setString(2, maBan);
            rs = stmt.executeQuery();
            return rs.next() && rs.getInt("soDong") > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
    }

    private String taoMaBanKhaDung(KhuVuc kv, String maBanDangChuyen) {
        if (kv == null) return null;
        String prefix = layKyHieuKhuVuc(kv);
        if (prefix.isEmpty()) return null;

        int gioiHan = Math.max(kv.getSoLuongBan(), 99);
        for (int i = 1; i <= gioiHan; i++) {
            String ma = prefix + String.format("%02d", i);
            if (ma.equalsIgnoreCase(maBanDangChuyen)) continue;

            Ban banTonTai = timBanTheoMa(ma);
            if (banTonTai == null) {
                return ma;
            }

            // Nếu sau này có logic chọn trực tiếp mã bàn đích, đoạn này vẫn đảm bảo không chọn bàn đã đặt/đang phục vụ.
            if (laBanDangDatHoacPhucVu(ma)) {
                continue;
            }
        }
        return null;
    }

    private String taoTenBanTheoMa(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) return null;
        Matcher matcher = Pattern.compile("(\\d+)$").matcher(maBan.trim());
        if (matcher.find()) {
            try {
                return "Bàn " + Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
                return "Bàn " + matcher.group(1);
            }
        }
        return "Bàn " + maBan.trim();
    }

    private String layKyHieuKhuVuc(KhuVuc kv) {
        String kyHieu = safe(kv.getKyHieu(), "").trim().toUpperCase(Locale.ROOT);
        if (!kyHieu.isEmpty()) return kyHieu;

        String ten = normalize(kv.getTenKhuVuc()).toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        if (!ten.isEmpty()) return ten.substring(0, 1);

        String ma = safe(kv.getMaKhuVuc(), "").trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        return ma.isEmpty() ? "B" : ma.substring(0, 1);
    }

    private String capNhatGhiChuSauKhiChuyen(Ban banCu, KhuVuc khuVucDich, String maBanMoi, String lyDo) {
        String ghiChuCu = safe(banCu.getGhiChu(), "").trim();
        String dongMoi = "Chuyển từ " + banCu.getMaBan() + " sang " + maBanMoi
                + " - Khu vực: " + safe(khuVucDich.getTenKhuVuc(), khuVucDich.getMaKhuVuc())
                + " - Lý do: " + lyDo;
        return catGhiChuBan(ghiChuCu.isEmpty() ? dongMoi : ghiChuCu + "\n" + dongMoi);
    }

    private String layMaLoaiBanDauTien() {
        if (!loaiBanMap.isEmpty()) return loaiBanMap.keySet().iterator().next();
        return "LB001";
    }

    private boolean khuVucDangHoatDong(KhuVuc kv) {
        if (kv == null) return false;
        String tt = normalize(kv.getTrangThai());
        return tt.isEmpty() || tt.contains("hoat dong") || tt.contains("dang hoat dong");
    }

    private int macDinhChoNgoiTheoLoai(String maLoaiBan) {
        String ten = loaiBanMap.get(maLoaiBan);
        Matcher matcher = Pattern.compile("(\\d+)").matcher(safe(ten, ""));
        if (matcher.find()) {
            try { return Integer.parseInt(matcher.group(1)); } catch (Exception ignored) {}
        }
        String ma = safe(maLoaiBan, "").toUpperCase(Locale.ROOT);
        if (ma.endsWith("1") || ma.endsWith("001")) return 2;
        if (ma.endsWith("2") || ma.endsWith("002")) return 4;
        if (ma.endsWith("3") || ma.endsWith("003")) return 6;
        if (ma.endsWith("4") || ma.endsWith("004")) return 8;
        return 4;
    }

    private ComboItem<KhuVuc>[] taoDanhSachKhuVuc() {
        @SuppressWarnings("unchecked")
        ComboItem<KhuVuc>[] arr = new ComboItem[khuVucMap.size()];
        int i = 0;
        for (KhuVuc kv : khuVucMap.values()) {
            arr[i++] = new ComboItem<>(kv, labelTabKhuVuc(kv));
        }
        return arr;
    }

    private ComboItem<String>[] taoDanhSachLoaiBan() {
        @SuppressWarnings("unchecked")
        ComboItem<String>[] arr = new ComboItem[loaiBanMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : loaiBanMap.entrySet()) {
            arr[i++] = new ComboItem<>(entry.getKey(), safe(entry.getValue(), entry.getKey()));
        }
        return arr;
    }

    private void boChonKhuVucKhac(JComboBox<ComboItem<KhuVuc>> combo, String maKhuVucHienTai) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            ComboItem<KhuVuc> item = combo.getItemAt(i);
            if (item != null && item.getValue() != null
                    && !Objects.equals(item.getValue().getMaKhuVuc(), maKhuVucHienTai)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void chonLoaiBanTrongCombo(JComboBox<ComboItem<String>> combo, String maLoaiBan) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            ComboItem<String> item = combo.getItemAt(i);
            if (item != null && Objects.equals(item.getValue(), maLoaiBan)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        return panel;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(field, gbc);
    }

    private JPanel createDialogWrapper(String title, String desc, JPanel formPanel, int width, int height) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(12, 14, 12, 14));
        wrapper.setPreferredSize(new Dimension(width, height));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel lblDesc = new JLabel("<html><body style='width:" + (width - 80) + "px'>" + desc + "</body></html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 100, 100));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(lblTitle);
        header.add(Box.createRigidArea(new Dimension(0, 6)));
        header.add(lblDesc);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JTextArea createDialogTextArea() {
        JTextArea area = new JTextArea(5, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(215, 215, 215), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        return area;
    }

    private JScrollPane createDialogScrollPane(JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(430, 130));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private JLabel createPreviewLabel() {
        return createPreviewLabel("-");
    }

    private JLabel createPreviewLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(new Color(250, 250, 250));
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(228, 228, 228), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        return label;
    }

    private Ban yeuCauChonBan() {
        if (banDangChon == null) {
            thongBao("Vui lòng chọn một bàn trước.");
            return null;
        }
        Ban moiNhat = timBanTheoMa(banDangChon.getMaBan());
        if (moiNhat == null) {
            thongBao("Bàn đang chọn không còn tồn tại. Vui lòng làm mới dữ liệu.");
            napDuLieuVaRender(false);
            return null;
        }
        boSungThongTinBan(moiNhat);
        banDangChon = moiNhat;
        return moiNhat;
    }

    private Ban timBanTheoMa(String maBan) {
        if (maBan == null) return null;
        for (Ban ban : dsBan) {
            if (ban != null && maBan.equalsIgnoreCase(safe(ban.getMaBan(), ""))) {
                return ban;
            }
        }

        try {
            Ban ban = banDAO.getBanTheoMa(maBan);
            if (ban != null) boSungThongTinBan(ban);
            return ban;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sapXepBan(List<Ban> list) {
        Collections.sort(list, Comparator
                .comparing((Ban b) -> b.getMaKhuVuc() == null ? "" : safe(b.getMaKhuVuc().getMaKhuVuc(), ""))
                .thenComparingInt(b -> laySoThuTuBan(b.getMaBan()))
                .thenComparing(b -> safe(b.getMaBan(), "")));
    }

    private int laySoThuTuBan(String maBan) {
        Matcher matcher = Pattern.compile("(\\d+)$").matcher(safe(maBan, ""));
        if (matcher.find()) {
            try { return Integer.parseInt(matcher.group(1)); } catch (Exception ignored) {}
        }
        return Integer.MAX_VALUE;
    }

    private String layTenBanHienThi(Ban ban) {
        if (ban == null) return "-";
        String ten = safe(ban.getTenBan(), "").trim();
        return ten.isEmpty() ? taoTenBanTheoMa(ban.getMaBan()) : ten;
    }

    private String layTenKhuVuc(Ban ban) {
        if (ban == null || ban.getMaKhuVuc() == null) return "-";
        KhuVuc kv = ban.getMaKhuVuc();
        String ten = safe(kv.getTenKhuVuc(), "").trim();
        return ten.isEmpty() ? safe(kv.getMaKhuVuc(), "-") : ten;
    }

    private String layTenLoaiBan(Ban ban) {
        if (ban == null || ban.getMaLoaiBan() == null) return "-";
        String maLoai = safe(ban.getMaLoaiBan().getMaLoaiBan(), "");
        String ten = safe(ban.getMaLoaiBan().getTenLoaiBan(), "").trim();
        if (!ten.isEmpty()) return ten;
        return safe(loaiBanMap.get(maLoai), maLoai);
    }

    private String layTrangThaiHienThi(Ban ban) {
        if (ban == null) return STATUS_TRONG;
        String status = hienThiTrangThaiMap.get(ban.getMaBan());
        if (status != null && !status.trim().isEmpty()) return chuanHoaTrangThai(status);
        return chuanHoaTrangThai(ban.getTrangThai());
    }

    private boolean laBanTrong(String status) {
        String normalized = normalize(status);
        return normalized.isEmpty() || normalized.equals("ban trong") || normalized.equals("trong");
    }

    private String chuanHoaTrangThai(String status) {
        String s = safe(status, "").trim();
        String n = normalize(s);
        if (n.contains("phuc vu") || n.contains("dang phuc vu") || n.contains("da nhan ban")) return STATUS_PHUC_VU;
        if (n.contains("da dat") || n.contains("dang cho") || n.contains("cho nhan ban")) return STATUS_DAT;
        if (n.contains("bao tri")) return STATUS_BAO_TRI;
        return STATUS_TRONG;
    }

    private String xayDungTenHienThiKhuVuc(KhuVuc kv) {
        if (kv == null) return "Khu vực";
        String ten = safe(kv.getTenKhuVuc(), kv.getMaKhuVuc());
        String kyHieu = safe(kv.getKyHieu(), "").trim();
        return kyHieu.isEmpty() ? ten : ten + " - " + kyHieu;
    }

    private String xayDungGhiChuChiTiet(Ban ban) {
        if (ban == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("Mã bàn: ").append(safe(ban.getMaBan(), "-")).append('\n');
        sb.append("Tên bàn: ").append(layTenBanHienThi(ban)).append('\n');
        sb.append("Khu vực: ").append(layTenKhuVuc(ban)).append('\n');
        sb.append("Loại bàn: ").append(layTenLoaiBan(ban)).append('\n');
        sb.append("Số chỗ: ").append(ban.getSoChoNgoi()).append('\n');
        sb.append("Trạng thái thực tế: ").append(layTrangThaiHienThi(ban)).append('\n');
        String ghiChu = safe(ban.getGhiChu(), "").trim();
        if (!ghiChu.isEmpty()) {
            sb.append("\nGhi chú:\n").append(ghiChu);
        }
        return sb.toString();
    }

    private String catGhiChuBan(String text) {
        String s = safe(text, "").trim();
        return s.length() > 1000 ? s.substring(0, 1000) : s;
    }

    private String nullIfBlank(String text) {
        return text == null || text.trim().isEmpty() ? null : text.trim();
    }

    private String safe(Object value, String defaultValue) {
        if (value == null) return defaultValue;
        String s = String.valueOf(value);
        return s == null ? defaultValue : s;
    }

    private String normalize(String input) {
        String s = safe(input, "").trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        temp = temp.replaceAll("\\p{M}", "");
        temp = temp.replace('đ', 'd').replace('Đ', 'D');
        return temp.replaceAll("\\s+", " ");
    }

    private void thongBao(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void thongBaoLoi(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void closeQuietly(AutoCloseable c) {
        if (c == null) return;
        try { c.close(); } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedComboItem(JComboBox<ComboItem<T>> combo) {
        Object obj = combo.getSelectedItem();
        return obj instanceof ComboItem ? (ComboItem<T>) obj : null;
    }

    private Color mauTheoTrangThai(String status) {
        if (STATUS_PHUC_VU.equalsIgnoreCase(status)) return new Color(255, 239, 239);
        if (STATUS_DAT.equalsIgnoreCase(status)) return new Color(255, 248, 230);
        if (STATUS_BAO_TRI.equalsIgnoreCase(status)) return new Color(238, 238, 238);
        return new Color(235, 248, 239);
    }

    private Color mauChuTheoTrangThai(String status) {
        if (STATUS_PHUC_VU.equalsIgnoreCase(status)) return new Color(198, 40, 40);
        if (STATUS_DAT.equalsIgnoreCase(status)) return new Color(230, 126, 34);
        if (STATUS_BAO_TRI.equalsIgnoreCase(status)) return new Color(90, 90, 90);
        return new Color(46, 125, 50);
    }

    private class BanCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Ban ban;
        private final JLabel lblMa;
        private final JLabel lblTen;
        private final JLabel lblLoai;
        private final JLabel lblStatus;

        BanCard(Ban ban) {
            this.ban = ban;

            // Bản sửa UI: tăng chiều cao card và dùng BoxLayout để không bị cắt chữ
            setLayout(new BorderLayout(0, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(190, 170));
            setMinimumSize(new Dimension(190, 170));
            setBackground(Color.WHITE);
            setOpaque(true);

            lblMa = new JLabel(safe(ban.getMaBan(), "-"));
            lblMa.setFont(new Font("SansSerif", Font.BOLD, 21));
            lblMa.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblTen = new JLabel(layTenBanHienThi(ban));
            lblTen.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblLoai = new JLabel(layTenLoaiBan(ban) + " • " + ban.getSoChoNgoi() + " chỗ");
            lblLoai.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblLoai.setForeground(new Color(95, 95, 95));
            lblLoai.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblStatus = new JLabel(layTrangThaiHienThi(ban));
            lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
            lblStatus.setOpaque(true);
            lblStatus.setBorder(new EmptyBorder(7, 8, 7, 8));

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBorder(new EmptyBorder(8, 10, 6, 10));
            textPanel.add(lblMa);
            textPanel.add(Box.createVerticalStrut(6));
            textPanel.add(lblTen);
            textPanel.add(Box.createVerticalStrut(6));
            textPanel.add(lblLoai);

            JPanel statusWrap = new JPanel(new BorderLayout());
            statusWrap.setOpaque(false);
            statusWrap.setBorder(new EmptyBorder(0, 10, 10, 10));
            statusWrap.add(lblStatus, BorderLayout.CENTER);

            add(textPanel, BorderLayout.CENTER);
            add(statusWrap, BorderLayout.SOUTH);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    chonBan(ban);
                }
            });

            capNhatStyle();
        }

        void capNhatStyle() {
            boolean selected = banDangChon != null && Objects.equals(banDangChon.getMaBan(), ban.getMaBan());
            String status = layTrangThaiHienThi(ban);

            setBackground(selected ? new Color(230, 240, 255) : Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(selected ? new Color(40, 100, 180) : new Color(225, 225, 225),
                            selected ? 2 : 1, true),
                    new EmptyBorder(4, 4, 4, 4)));

            lblStatus.setText(status);
            lblStatus.setBackground(mauTheoTrangThai(status));
            lblStatus.setForeground(mauChuTheoTrangThai(status));
        }
    }

    private static class ComboItem<T> {
        private final T value;
        private final String label;

        ComboItem(T value, String label) {
            this.value = value;
            this.label = label;
        }

        T getValue() { return value; }
        String getLabel() { return label; }
        @Override public String toString() { return label; }
    }

    private static class ComboRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ComboItem) setText(((ComboItem<?>) value).getLabel());
            setBorder(new EmptyBorder(6, 8, 6, 8));
            return this;
        }
    }

    private static class PromptTextField extends JTextField {
        private static final long serialVersionUID = 1L;
        private final String prompt;

        PromptTextField(String prompt) {
            this.prompt = prompt;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText() != null && !getText().isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(160, 160, 160));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            g2.drawString(prompt, insets.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
            g2.dispose();
        }
    }
}
