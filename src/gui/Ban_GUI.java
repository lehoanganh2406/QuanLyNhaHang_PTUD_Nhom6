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
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private final Map<String, String> loaiBanMap = createLoaiBanMap();
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
        form.add(new JScrollPane(txtGhiChu), gbc);
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

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
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

    private void napDuLieuVaRender(boolean giuLaiLuaChon) {
        String maDangChon = banDangChon == null ? null : banDangChon.getMaBan();

        dsBan.clear();
        khuVucMap.clear();
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

        try {
            for (Object obj : banDAO.getAllBan()) {
                if (obj instanceof Ban) {
                    dsBan.add((Ban) obj);
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

    private void damBaoBoLocHopLe() {
        if (FILTER_ALL.equals(floorFilter)) return;
        if (!khuVucMap.containsKey(floorFilter)) {
            floorFilter = FILTER_ALL;
        }
    }

    private void napTrangThaiHienTai() {
        hienThiTrangThaiMap.clear();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) {
                napTrangThaiTuDAO();
                return;
            }
            String sql =
                    "SELECT b.maBan, " +
                    "CASE " +

                    // ĐANG PHỤC VỤ
                    "WHEN EXISTS ( " +
                    "    SELECT 1 " +
                    "    FROM HoaDon hd " +
                    "    JOIN HoaDon_Ban hdb " +
                    "        ON hd.maHD = hdb.maHD " +
                    "    WHERE hdb.maBan = b.maBan " +
                    "      AND hd.thoiGianRa IS NULL " +
                    "      AND ( " +
                    "            hd.trangThai IS NULL " +
                    "            OR hd.trangThai NOT IN " +
                    "            (N'Đã thanh toán', N'Đã hủy', N'Hủy', N'Hoàn tất', N'Đã hoàn thành') " +
                    "      ) " +
                    ") THEN N'Đang phục vụ' " +

                    // ĐÃ ĐẶT
                    "WHEN EXISTS ( " +
                    "    SELECT 1 " +
                    "    FROM PhieuDatBan pdb " +
                    "    JOIN PhieuDatBan_Ban pdbb " +
                    "        ON pdb.maPhieuDatBan = pdbb.maPhieuDatBan " +
                    "    WHERE pdbb.maBan = b.maBan " +
                    "      AND CAST(pdb.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) " +
                    "      AND ( " +
                    "            pdb.trangThai IS NULL " +
                    "            OR pdb.trangThai NOT IN " +
                    "            (N'Đã hủy', N'Hủy', N'Đã nhận bàn', N'Hoàn tất', N'Đã hoàn thành') " +
                    "      ) " +
                    ") THEN N'Đã đặt' " +

                    // CÒN LẠI
                    "ELSE N'Bàn trống' " +
                    "END AS trangThaiHienTai " +

                    "FROM Ban b";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                hienThiTrangThaiMap.put(rs.getString("maBan"), chuanHoaTrangThai(rs.getString("trangThaiHienTai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            napTrangThaiTuDAO();
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
        }
    }

    private void napTrangThaiTuDAO() {
        try {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            for (Object obj : banDAO.getDanhSachBanTheoThoiGian(now)) {
                if (obj instanceof String[]) {
                    String[] row = (String[]) obj;
                    if (row.length >= 3) {
                        hienThiTrangThaiMap.put(row[0], chuanHoaTrangThai(row[2]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderData() {
        if (floorsContainer == null) return;
        floorsContainer.removeAll();
        banCardMap.clear();

        Map<String, List<Ban>> grouped = new LinkedHashMap<>();
        for (String maKV : khuVucMap.keySet()) {
            grouped.put(maKV, new ArrayList<>());
        }

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

        JPanel itemsWrap = new JPanel(new GridLayout(0, 5, 12, 12));
        itemsWrap.setOpaque(false);
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

    private void chonBan(Ban ban) {
        this.banDangChon = ban;
        capNhatThongTinChon();
        capNhatHighlightLuaChon();
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
            ComboItem<KhuVuc> itemKV = (ComboItem<KhuVuc>) cboKhuVuc.getSelectedItem();
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            if (itemKV == null || itemKV.getValue() == null) {
                lblPreviewMa.setText("Mã bàn dự kiến: -");
                lblPreviewTen.setText("Tên bàn dự kiến: -");
                lblPreviewSoCho.setText("Số chỗ ngồi tự động: -");
                return;
            }
            KhuVuc kv = itemKV.getValue();
            String maMoi = taoMaBanKhaDung(kv, null);
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
        JPanel wrapper = createDialogWrapper("Thêm bàn mới",
                "Mã bàn và tên bàn được sinh theo ký hiệu khu vực đang chọn.", panel, 720, 620);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, wrapper, "Thêm bàn mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;
            ComboItem<KhuVuc> itemKV = (ComboItem<KhuVuc>) cboKhuVuc.getSelectedItem();
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
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
            if (!conSucChuaDeThem(kv.getMaKhuVuc(), null)) {
                thongBao("Khu vực này đã đủ số lượng bàn theo cấu hình (" + kv.getSoLuongBan() + " bàn).");
                continue;
            }
            String maMoi = taoMaBanKhaDung(kv, null);
            String tenMoi = taoTenBanTheoMa(maMoi);
            if (maMoi == null || maMoi.trim().isEmpty()) {
                thongBao("Không thể tự sinh mã bàn. Kiểm tra lại ký hiệu khu vực.");
                continue;
            }
            if (timBanTheoMa(maMoi) != null) {
                thongBao("Mã bàn " + maMoi + " đã tồn tại. Vui lòng làm mới dữ liệu rồi thử lại.");
                return;
            }
            int soCho = macDinhChoNgoiTheoLoai(itemLoai.getValue());
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
        String trangThaiHienTai = layTrangThaiHienThi(ban);
        if (STATUS_DAT.equalsIgnoreCase(trangThaiHienTai) || STATUS_PHUC_VU.equalsIgnoreCase(trangThaiHienTai)) {
            thongBao("Không thể sửa bàn đang ở trạng thái: " + trangThaiHienTai + ".\nVui lòng xử lý phiếu đặt/hóa đơn trước.");
            return;
        }

        JTextField txtMaBan = new JTextField(ban.getMaBan());
        txtMaBan.setEditable(false);
        JTextField txtKhuVuc = new JTextField(layTenKhuVuc(ban));
        txtKhuVuc.setEditable(false);
        JTextField txtTenBan = new JTextField(safe(layTenBanHienThi(ban), ""));
        txtTenBan.setEditable(false);
        JComboBox<ComboItem<String>> cboLoaiBan = new JComboBox<>(taoDanhSachLoaiBan());
        cboLoaiBan.setRenderer(new ComboRenderer());
        chonLoaiBan(cboLoaiBan, ban.getMaLoaiBan() == null ? null : ban.getMaLoaiBan().getMaLoaiBan());
        JLabel lblSoCho = createPreviewLabel();
        lblSoCho.setText("Số chỗ tự động: " + ban.getSoChoNgoi());
        cboLoaiBan.addActionListener(e -> {
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            lblSoCho.setText(itemLoai == null || itemLoai.getValue() == null
                    ? "Số chỗ tự động: -"
                    : "Số chỗ tự động: " + macDinhChoNgoiTheoLoai(itemLoai.getValue()));
        });
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_TRONG, STATUS_BAO_TRI });
        cboTrangThai.setSelectedItem(chuanHoaTrangThaiDeLuu(ban));
        JTextArea txtAreaGhiChu = createDialogTextArea();
        txtAreaGhiChu.setText(safe(ban.getGhiChu(), ""));

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Mã bàn", txtMaBan);
        addFormRow(panel, 1, "Khu vực", txtKhuVuc);
        addFormRow(panel, 2, "Tên bàn", txtTenBan);
        addFormRow(panel, 3, "Loại bàn", cboLoaiBan);
        addFormRow(panel, 4, "Số chỗ", lblSoCho);
        addFormRow(panel, 5, "Trạng thái lưu", cboTrangThai);
        addFormRow(panel, 6, "Ghi chú", createDialogScrollPane(txtAreaGhiChu));

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Sửa thông tin bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            if (itemLoai == null || itemLoai.getValue() == null) {
                thongBao("Vui lòng chọn loại bàn.");
                continue;
            }
            String trangThaiMoi = chuanHoaTrangThai(Objects.toString(cboTrangThai.getSelectedItem(), STATUS_TRONG));
            if (STATUS_BAO_TRI.equalsIgnoreCase(trangThaiMoi) && coDuLieuHoatDong(ban.getMaBan())) {
                thongBao("Không thể chuyển sang bảo trì vì bàn đang có đặt bàn hoặc hóa đơn đang hoạt động.");
                continue;
            }
            int soCho = macDinhChoNgoiTheoLoai(itemLoai.getValue());
            ban.setTenBan(layTenBanHienThi(ban));
            ban.setMaLoaiBan(new LoaiBan(itemLoai.getValue(), itemLoai.getLabel()));
            ban.setSoChoNgoi(soCho);
            ban.setTrangThai(trangThaiMoi);
            ban.setGhiChu(catGhiChuBan(nullIfBlank(txtAreaGhiChu.getText())));
            if (banDAO.capNhatBan(ban)) {
                thongBao("Đã cập nhật bàn " + ban.getMaBan() + ".\nTên bàn: " + layTenBanHienThi(ban) + "\nSố chỗ mới: " + soCho);
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
        String status = layTrangThaiHienThi(ban);
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
        String status = layTrangThaiHienThi(ban);
        if (!laBanTrong(status) && !STATUS_BAO_TRI.equalsIgnoreCase(status)) {
            thongBao("Chỉ chuyển khu vực khi bàn đang trống hoặc bảo trì.\nTrạng thái hiện tại: " + status);
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
        JTextArea txtLyDo = createDialogTextArea();
        txtLyDo.setRows(4);

        Runnable capNhatPreview = () -> {
            ComboItem<KhuVuc> item = (ComboItem<KhuVuc>) cboKhuVucDich.getSelectedItem();
            if (item == null || item.getValue() == null) {
                lblMaBanMoi.setText("Mã bàn sau khi chuyển: -");
                lblTenBanMoi.setText("Tên bàn sau khi chuyển: -");
                return;
            }
            KhuVuc khuVucDich = item.getValue();
            String maMoi = taoMaBanKhaDung(khuVucDich, ban.getMaBan());
            lblMaBanMoi.setText("Mã bàn sau khi chuyển: " + safe(maMoi, "-"));
            lblTenBanMoi.setText("Tên bàn sau khi chuyển: " + safe(taoTenBanTheoMa(maMoi), "-"));
        };
        cboKhuVucDich.addActionListener(e -> capNhatPreview.run());
        capNhatPreview.run();

        JPanel formPanel = createFormPanel();
        addFormRow(formPanel, 0, "Bàn hiện tại", createValueLabel(ban.getMaBan() + " - " + layTenBanHienThi(ban)));
        addFormRow(formPanel, 1, "Khu vực hiện tại", createValueLabel(layTenKhuVuc(ban)));
        addFormRow(formPanel, 2, "Khu vực đích", cboKhuVucDich);
        addFormRow(formPanel, 3, "Mã bàn mới", lblMaBanMoi);
        addFormRow(formPanel, 4, "Tên bàn mới", lblTenBanMoi);
        addFormRow(formPanel, 5, "Lý do chuyển", createDialogScrollPane(txtLyDo));
        JPanel wrapper = createDialogWrapper("Chuyển khu vực bàn",
                "Khi chuyển khu vực, hệ thống đổi cả mã bàn và tên bàn theo ký hiệu khu vực mới, đồng thời cập nhật hóa đơn/phiếu đặt liên quan.",
                formPanel, 760, 600);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, wrapper, "Chuyển khu vực bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;
            ComboItem<KhuVuc> item = (ComboItem<KhuVuc>) cboKhuVucDich.getSelectedItem();
            if (item == null || item.getValue() == null) {
                thongBao("Vui lòng chọn khu vực đích.");
                continue;
            }
            KhuVuc khuVucDich = item.getValue();
            if (Objects.equals(khuVucDich.getMaKhuVuc(), maKhuVucHienTai)) {
                thongBao("Vui lòng chọn khu vực khác khu vực hiện tại.");
                continue;
            }
            if (!khuVucDangHoatDong(khuVucDich)) {
                thongBao("Khu vực đích đang không hoạt động.");
                continue;
            }
            if (!conSucChuaDeThem(khuVucDich.getMaKhuVuc(), ban.getMaBan())) {
                thongBao("Khu vực đích đã đủ số lượng bàn theo cấu hình.");
                continue;
            }
            String lyDo = safe(txtLyDo.getText(), "").trim();
            if (lyDo.isEmpty()) {
                thongBao("Vui lòng nhập lý do chuyển khu vực.");
                continue;
            }
            String maBanMoi = taoMaBanKhaDung(khuVucDich, ban.getMaBan());
            String tenBanMoi = taoTenBanTheoMa(maBanMoi);
            if (maBanMoi == null || maBanMoi.trim().isEmpty()) {
                thongBao("Không thể tự sinh mã bàn mới. Kiểm tra ký hiệu hoặc sức chứa khu vực đích.");
                continue;
            }
            if (timBanTheoMa(maBanMoi) != null) {
                thongBao("Mã bàn " + maBanMoi + " đã tồn tại. Vui lòng làm mới dữ liệu rồi thử lại.");
                return;
            }
            if (thucHienChuyenKhuVuc(ban, khuVucDich, maBanMoi, tenBanMoi, lyDo)) {
                thongBao("Đã chuyển bàn thành công.\nMã cũ: " + ban.getMaBan() + "\nMã mới: " + maBanMoi + "\nTên mới: " + tenBanMoi);
                taiLaiVaChonBan(maBanMoi);
            } else {
                thongBaoLoi("Chuyển khu vực thất bại. Kiểm tra dữ liệu khóa ngoại hoặc kết nối CSDL.");
            }
            return;
        }
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
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(lblDesc);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private boolean thucHienChuyenKhuVuc(Ban banCu, KhuVuc khuVucMoi, String maBanMoi, String tenBanMoi, String lyDo) {
        Connection con = null;
        PreparedStatement insertBan = null;
        PreparedStatement updatePhieuDatBan = null;
        PreparedStatement updateHoaDon = null;
        PreparedStatement deleteBanCu = null;
        boolean oldAutoCommit = true;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return false;
            oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            String maLoaiBan = banCu.getMaLoaiBan() == null ? null : banCu.getMaLoaiBan().getMaLoaiBan();
            String trangThaiLuu = chuanHoaTrangThaiDeLuu(banCu);
            String ghiChuMoi = taoGhiChuChuyenKhuVuc(banCu, khuVucMoi, maBanMoi, tenBanMoi, lyDo);

            insertBan = con.prepareStatement(
                    "INSERT INTO Ban (maBan, maKhuVuc, maLoaiBan, tenBan, ghiChu, soChoNgoi, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?)");
            insertBan.setString(1, maBanMoi);
            insertBan.setString(2, khuVucMoi.getMaKhuVuc());
            insertBan.setString(3, maLoaiBan);
            insertBan.setString(4, tenBanMoi);
            insertBan.setString(5, catGhiChuBan(ghiChuMoi));
            insertBan.setInt(6, banCu.getSoChoNgoi());
            insertBan.setString(7, trangThaiLuu);
            insertBan.executeUpdate();

            updatePhieuDatBan = con.prepareStatement("UPDATE PhieuDatBan SET maBan = ? WHERE maBan = ?");
            updatePhieuDatBan.setString(1, maBanMoi);
            updatePhieuDatBan.setString(2, banCu.getMaBan());
            updatePhieuDatBan.executeUpdate();

            updateHoaDon = con.prepareStatement("UPDATE HoaDon SET maBan = ? WHERE maBan = ?");
            updateHoaDon.setString(1, maBanMoi);
            updateHoaDon.setString(2, banCu.getMaBan());
            updateHoaDon.executeUpdate();

            deleteBanCu = con.prepareStatement("DELETE FROM Ban WHERE maBan = ?");
            deleteBanCu.setString(1, banCu.getMaBan());
            int deleted = deleteBanCu.executeUpdate();
            if (deleted <= 0) {
                con.rollback();
                return false;
            }
            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            closeQuietly(deleteBanCu);
            closeQuietly(updateHoaDon);
            closeQuietly(updatePhieuDatBan);
            closeQuietly(insertBan);
            try {
                if (con != null) con.setAutoCommit(oldAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean coDuLieuLienQuan(String maBan) {
        Connection con = null;
        PreparedStatement stmtHoaDon = null;
        PreparedStatement stmtPhieuDatBan = null;
        ResultSet rsHoaDon = null;
        ResultSet rsPhieuDatBan = null;
        try {
            con = ConnectDB.getConnection();
            if (con == null) return true;
            stmtHoaDon = con.prepareStatement("SELECT TOP 1 1 FROM HoaDon WHERE maBan = ?");
            stmtHoaDon.setString(1, maBan);
            rsHoaDon = stmtHoaDon.executeQuery();
            if (rsHoaDon.next()) return true;

            stmtPhieuDatBan = con.prepareStatement("SELECT TOP 1 1 FROM PhieuDatBan WHERE maBan = ?");
            stmtPhieuDatBan.setString(1, maBan);
            rsPhieuDatBan = stmtPhieuDatBan.executeQuery();
            return rsPhieuDatBan.next();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            closeQuietly(rsPhieuDatBan);
            closeQuietly(stmtPhieuDatBan);
            closeQuietly(rsHoaDon);
            closeQuietly(stmtHoaDon);
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
                    "EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.maBan = b.maBan " +
                    "AND hd.thoiGianRa IS NULL " +
                    "AND (hd.trangThai IS NULL OR hd.trangThai NOT IN (N'Đã thanh toán', N'Đã hủy', N'Hủy'))) " +
                    "OR EXISTS (SELECT 1 FROM PhieuDatBan pdb WHERE pdb.maBan = b.maBan " +
                    "AND CAST(pdb.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) " +
                    "AND (pdb.trangThai IS NULL OR pdb.trangThai NOT IN (N'Đã hủy', N'Hủy', N'Đã nhận bàn', N'Hoàn tất', N'Đã hoàn thành'))))";
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

    private boolean conSucChuaDeThem(String maKhuVuc, String boQuaMaBan) {
        int dem = 0;
        for (Ban ban : dsBan) {
            if (ban.getMaKhuVuc() == null) continue;
            if (!Objects.equals(maKhuVuc, ban.getMaKhuVuc().getMaKhuVuc())) continue;
            if (Objects.equals(boQuaMaBan, ban.getMaBan())) continue;
            dem++;
        }
        KhuVuc kv = khuVucMap.get(maKhuVuc);
        return kv != null && dem < kv.getSoLuongBan();
    }

    private String taoMaBanKhaDung(KhuVuc khuVuc, String boQuaMaBan) {
        if (khuVuc == null) return null;
        String kyHieu = safe(khuVuc.getKyHieu(), "").trim().toUpperCase(Locale.ROOT);
        if (kyHieu.isEmpty()) return null;
        int sucChua = Math.max(1, khuVuc.getSoLuongBan());
        Set<Integer> daDung = new HashSet<>();
        Pattern pattern = Pattern.compile("^" + Pattern.quote(kyHieu) + "(\\d+)$", Pattern.CASE_INSENSITIVE);
        for (Ban ban : dsBan) {
            if (ban == null) continue;
            if (Objects.equals(boQuaMaBan, ban.getMaBan())) continue;
            if (ban.getMaKhuVuc() == null || !Objects.equals(khuVuc.getMaKhuVuc(), ban.getMaKhuVuc().getMaKhuVuc())) continue;
            Matcher matcher = pattern.matcher(safe(ban.getMaBan(), "").trim());
            if (matcher.matches()) {
                try {
                    daDung.add(Integer.parseInt(matcher.group(1)));
                } catch (Exception ignored) {
                }
            }
        }
        for (int i = 1; i <= sucChua; i++) {
            if (!daDung.contains(i)) {
                return kyHieu + String.format("%02d", i);
            }
        }
        return null;
    }

    private String taoTenBanTheoMa(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) return null;
        return "Bàn " + maBan.trim().toUpperCase(Locale.ROOT);
    }

    private boolean tenBanDangDuocSuDungTrongKhuVuc(String maKhuVuc, String tenBan, String boQuaMaBan) {
        if (maKhuVuc == null || tenBan == null || tenBan.trim().isEmpty()) return false;
        String tenCanKiemTra = tenBan.trim();
        for (Ban ban : dsBan) {
            if (ban == null) continue;
            if (Objects.equals(boQuaMaBan, ban.getMaBan())) continue;
            if (ban.getMaKhuVuc() == null) continue;
            if (!Objects.equals(maKhuVuc, ban.getMaKhuVuc().getMaKhuVuc())) continue;
            if (tenCanKiemTra.equalsIgnoreCase(safe(ban.getTenBan(), "").trim())) return true;
        }
        return false;
    }

    private Ban timBanTheoMa(String maBan) {
        for (Ban ban : dsBan) {
            if (Objects.equals(ban.getMaBan(), maBan)) return ban;
        }
        return null;
    }

    private void sapXepBan(List<Ban> danhSach) {
        danhSach.sort(Comparator
                .comparing((Ban b) -> b.getMaKhuVuc() == null ? "" : safe(b.getMaKhuVuc().getMaKhuVuc(), ""))
                .thenComparing(Ban::getMaBan, this::soSanhMaBanTuNhien));
    }

    private int soSanhMaBanTuNhien(String a, String b) {
        String maA = safe(a, "").trim().toUpperCase(Locale.ROOT);
        String maB = safe(b, "").trim().toUpperCase(Locale.ROOT);
        String chuA = maA.replaceAll("\\d+$", "");
        String chuB = maB.replaceAll("\\d+$", "");
        int cmpChu = chuA.compareTo(chuB);
        if (cmpChu != 0) return cmpChu;
        int soA = laySoCuoiMaBan(maA);
        int soB = laySoCuoiMaBan(maB);
        if (soA != soB) return Integer.compare(soA, soB);
        return maA.compareTo(maB);
    }

    private int laySoCuoiMaBan(String maBan) {
        if (maBan == null) return Integer.MAX_VALUE;
        Matcher matcher = Pattern.compile(".*?(\\d+)$").matcher(maBan.trim());
        if (matcher.matches()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (Exception e) {
                return Integer.MAX_VALUE;
            }
        }
        return Integer.MAX_VALUE;
    }

    private String layTenBanHienThi(Ban ban) {
        if (ban == null) return "-";
        String tenLuuTrongSQL = safe(ban.getTenBan(), "").trim();
        if (!tenLuuTrongSQL.isEmpty()) return tenLuuTrongSQL;
        return taoTenBanTheoMa(ban.getMaBan());
    }

    private String layTenKhuVuc(Ban ban) {
        if (ban == null || ban.getMaKhuVuc() == null) return "-";
        KhuVuc kv = khuVucMap.get(ban.getMaKhuVuc().getMaKhuVuc());
        return kv == null ? ban.getMaKhuVuc().getMaKhuVuc() : xayDungTenHienThiKhuVuc(kv);
    }

    private String layTenLoaiBan(Ban ban) {
        if (ban == null || ban.getMaLoaiBan() == null) return "-";
        String maLoai = ban.getMaLoaiBan().getMaLoaiBan();
        return loaiBanMap.getOrDefault(maLoai, maLoai);
    }

    private String layTrangThaiHienThi(Ban ban) {
        if (ban == null) return "-";
        String stored = chuanHoaTrangThai(ban.getTrangThai());
        String currentRaw = hienThiTrangThaiMap.get(ban.getMaBan());
        String current = currentRaw == null ? null : chuanHoaTrangThai(currentRaw);
        if (STATUS_PHUC_VU.equalsIgnoreCase(current)) return STATUS_PHUC_VU;
        if (STATUS_DAT.equalsIgnoreCase(current)) return STATUS_DAT;
        if (STATUS_BAO_TRI.equalsIgnoreCase(stored)) return STATUS_BAO_TRI;
        if (current != null && !current.trim().isEmpty()) return current;
        return stored;
    }

    private String chuanHoaTrangThaiDeLuu(Ban ban) {
        return ban == null ? STATUS_TRONG : chuanHoaTrangThai(ban.getTrangThai());
    }

    private String chuanHoaTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) return STATUS_TRONG;
        String value = trangThai.trim();
        if ("Trống".equalsIgnoreCase(value) || STATUS_TRONG.equalsIgnoreCase(value)) return STATUS_TRONG;
        if ("Đang sử dụng".equalsIgnoreCase(value) || STATUS_PHUC_VU.equalsIgnoreCase(value)) return STATUS_PHUC_VU;
        if ("Đã đặt".equalsIgnoreCase(value) || STATUS_DAT.equalsIgnoreCase(value)) return STATUS_DAT;
        if ("Bảo trì".equalsIgnoreCase(value) || STATUS_BAO_TRI.equalsIgnoreCase(value)) return STATUS_BAO_TRI;
        return value;
    }

    private boolean laBanTrong(String status) {
        return STATUS_TRONG.equalsIgnoreCase(status) || "Trống".equalsIgnoreCase(status);
    }

    private boolean khuVucDangHoatDong(KhuVuc kv) {
        if (kv == null) return false;
        String trangThai = safe(kv.getTrangThai(), "").trim();
        return trangThai.isEmpty()
                || "Hoạt động".equalsIgnoreCase(trangThai)
                || "Đang hoạt động".equalsIgnoreCase(trangThai);
    }

    private String xayDungTenHienThiKhuVuc(KhuVuc kv) {
        if (kv == null) return "-";
        String ten = safe(kv.getTenKhuVuc(), kv.getMaKhuVuc());
        String kyHieu = safe(kv.getKyHieu(), "").trim();
        return kyHieu.isEmpty() ? ten : ten + " (" + kyHieu + ")";
    }

    private String xayDungGhiChuChiTiet(Ban ban) {
        StringBuilder sb = new StringBuilder();
        sb.append("Trạng thái thực tế hiện tại: ").append(layTrangThaiHienThi(ban));
        sb.append("\nMã bàn: ").append(safe(ban.getMaBan(), "-"));
        sb.append("\nTên bàn: ").append(layTenBanHienThi(ban));
        sb.append("\nTrạng thái lưu trong bảng Ban: ").append(safe(chuanHoaTrangThaiDeLuu(ban), STATUS_TRONG));
        sb.append("\nCập nhật lúc: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        sb.append("\n\nGhi chú bàn:");
        String ghiChu = safe(ban.getGhiChu(), "").trim();
        if (ghiChu.isEmpty()) {
            sb.append("\n- Không có ghi chú.");
        } else {
            sb.append("\n- ").append(ghiChu.replace("\n", "\n- "));
        }
        return sb.toString();
    }

    private String taoGhiChuChuyenKhuVuc(Ban banCu, KhuVuc khuVucMoi, String maBanMoi, String tenBanMoi, String lyDo) {
        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String ghiChuCu = safe(banCu.getGhiChu(), "").trim();
        String lyDoGon = catChuoi(safe(lyDo, "").replace("\n", " ").trim(), 70);

        StringBuilder sb = new StringBuilder();
        // Ghi chú của bảng Ban trong SQL chỉ dài NVARCHAR(255), nên ưu tiên lưu lần chuyển mới nhất.
        sb.append("[Chuyển khu vực] ")
                .append(thoiGian)
                .append(" | ").append(banCu.getMaBan())
                .append(" - ").append(layTenBanHienThi(banCu))
                .append(" -> ").append(maBanMoi)
                .append(" - ").append(tenBanMoi)
                .append(" | ").append(layTenKhuVuc(banCu))
                .append(" -> ").append(safe(khuVucMoi.getTenKhuVuc(), khuVucMoi.getMaKhuVuc()))
                .append(" | Lý do: ").append(lyDoGon);

        if (!ghiChuCu.isEmpty()) {
            sb.append("\nGhi chú cũ: ").append(catChuoi(ghiChuCu.replace("\n", " "), 80));
        }
        return catGhiChuBan(sb.toString());
    }

    private String catGhiChuBan(String value) {
        return catChuoi(value, 255);
    }

    private String catChuoi(String value, int maxLength) {
        if (value == null) return null;
        String text = value.trim();
        if (text.length() <= maxLength) return text;
        if (maxLength <= 3) return text.substring(0, maxLength);
        return text.substring(0, maxLength - 3) + "...";
    }

    private int macDinhChoNgoiTheoLoai(String maLoaiBan) {
        if (maLoaiBan == null) return 4;
        switch (maLoaiBan) {
            case "LB01": return 2;
            case "LB02": return 4;
            case "LB03": return 6;
            case "LB04": return 8;
            default: return 4;
        }
    }

    @SuppressWarnings("unchecked")
    private ComboItem<KhuVuc>[] taoDanhSachKhuVuc() {
        List<ComboItem<KhuVuc>> ds = new ArrayList<>();
        for (KhuVuc kv : khuVucMap.values()) {
            ds.add(new ComboItem<>(xayDungTenHienThiKhuVuc(kv), kv));
        }
        return ds.toArray(new ComboItem[0]);
    }

    @SuppressWarnings("unchecked")
    private ComboItem<String>[] taoDanhSachLoaiBan() {
        List<ComboItem<String>> ds = new ArrayList<>();
        for (Map.Entry<String, String> entry : loaiBanMap.entrySet()) {
            ds.add(new ComboItem<>(entry.getValue(), entry.getKey()));
        }
        return ds.toArray(new ComboItem[0]);
    }

    private void chonLoaiBan(JComboBox<ComboItem<String>> comboBox, String maLoaiBan) {
        if (comboBox == null) return;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem<String> item = comboBox.getItemAt(i);
            if (Objects.equals(item.getValue(), maLoaiBan)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void boChonKhuVucKhac(JComboBox<ComboItem<KhuVuc>> comboBox, String maKhuVucHienTai) {
        if (comboBox == null || comboBox.getItemCount() == 0) return;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem<KhuVuc> item = comboBox.getItemAt(i);
            if (item == null || item.getValue() == null) continue;
            if (!Objects.equals(item.getValue().getMaKhuVuc(), maKhuVucHienTai)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.setSelectedIndex(0);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        // Tăng chiều cao form để các ô ghi chú dạng nhiều dòng không bị ép mỏng trong dialog.
        panel.setPreferredSize(new Dimension(620, 500));
        panel.setMinimumSize(new Dimension(620, 470));
        return panel;
    }

    private void addFormRow(JPanel panel, int row, String label, Component comp) {
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridx = 0;
        gbcLabel.gridy = row;
        gbcLabel.anchor = GridBagConstraints.NORTHWEST;
        gbcLabel.insets = new Insets(8, 0, 8, 14);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setPreferredSize(new Dimension(155, 28));
        panel.add(lbl, gbcLabel);

        GridBagConstraints gbcComp = new GridBagConstraints();
        gbcComp.gridx = 1;
        gbcComp.gridy = row;
        gbcComp.weightx = 1;
        gbcComp.fill = GridBagConstraints.HORIZONTAL;
        gbcComp.insets = new Insets(8, 0, 8, 0);
        if (comp instanceof JComponent) ((JComponent) comp).setFont(new Font("SansSerif", Font.PLAIN, 13));
        if (comp instanceof JTextField) {
            comp.setPreferredSize(new Dimension(390, 30));
        } else if (comp instanceof JComboBox) {
            comp.setPreferredSize(new Dimension(390, 30));
        } else if (comp instanceof JScrollPane) {
            // Riêng ô ghi chú cần cao hơn và được fill theo cả 2 chiều để không bị ép thành 1 đường mỏng.
            gbcComp.fill = GridBagConstraints.BOTH;
            gbcComp.weighty = 1;
            comp.setPreferredSize(new Dimension(430, 130));
            comp.setMinimumSize(new Dimension(430, 120));
        } else if (comp instanceof JLabel) {
            comp.setPreferredSize(new Dimension(390, 30));
        }
        panel.add(comp, gbcComp);
    }

    private JTextArea createDialogTextArea() {
        JTextArea area = new JTextArea(5, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        return area;
    }

    private JScrollPane createDialogScrollPane(JTextArea area) {
        area.setRows(5);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(area);
        // Luôn hiện thanh cuộn dọc để người dùng thấy rõ có thể kéo xuống khi ghi chú dài.
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(430, 130));
        scrollPane.setMinimumSize(new Dimension(430, 120));
        scrollPane.setBorder(new LineBorder(new Color(210, 210, 210), 1, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        return scrollPane;
    }

    private JLabel createPreviewLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(40, 100, 180));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(40, 40, 40));
        return label;
    }

    private Ban yeuCauChonBan() {
        if (banDangChon == null) {
            thongBao("Vui lòng chọn một bàn trước.");
        }
        return banDangChon;
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void thongBaoLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private String normalize(String value) {
        String raw = safe(value, "").trim().toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.replace('đ', 'd');
    }

    private String safe(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private String nullIfBlank(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> createLoaiBanMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("LB01", "Bàn 2 chỗ");
        map.put("LB02", "Bàn 4 chỗ");
        map.put("LB03", "Bàn 6 chỗ");
        map.put("LB04", "Bàn 8 chỗ");
        return map;
    }

    private static class ComboItem<T> {
        private final String label;
        private final T value;

        ComboItem(String label, T value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public T getValue() { return value; }
        @Override public String toString() { return label; }
    }

    private static class ComboRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ComboItem) {
                ComboItem<?> item = (ComboItem<?>) value;
                setText(item.getLabel());
            }
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
            g2.setColor(new Color(145, 145, 145));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(prompt, insets.left, y);
            g2.dispose();
        }
    }

    private class BanCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Ban ban;
        private final JLabel lblStatus;
        private final JLabel lblSeats;

        BanCard(Ban ban) {
            this.ban = ban;
            setLayout(new BorderLayout(0, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            JLabel lblCode = new JLabel(layTenBanHienThi(ban));
            lblCode.setFont(new Font("SansSerif", Font.BOLD, 18));
            top.add(lblCode, BorderLayout.WEST);
            lblSeats = new JLabel(ban.getSoChoNgoi() + " chỗ");
            lblSeats.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblSeats.setForeground(new Color(40, 100, 180));
            top.add(lblSeats, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            JLabel lblMaHeThong = new JLabel("Mã bàn: " + safe(ban.getMaBan(), "-"));
            lblMaHeThong.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblMaHeThong.setForeground(new Color(95, 95, 95));
            center.add(lblMaHeThong);
            center.add(Box.createRigidArea(new Dimension(0, 6)));
            JLabel lblLoai = new JLabel(layTenLoaiBan(ban));
            lblLoai.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblLoai.setForeground(new Color(95, 95, 95));
            center.add(lblLoai);
            add(center, BorderLayout.CENTER);

            lblStatus = new JLabel(layTrangThaiHienThi(ban), SwingConstants.CENTER);
            lblStatus.setOpaque(true);
            lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblStatus.setBorder(new EmptyBorder(6, 10, 6, 10));
            add(lblStatus, BorderLayout.SOUTH);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    chonBan(ban);
                }
            });
            capNhatStyle();
        }

        void capNhatStyle() {
            String status = layTrangThaiHienThi(ban);
            Color[] colors = mauTheoTrangThai(status);
            lblStatus.setText(status);
            lblStatus.setBackground(colors[0]);
            lblStatus.setForeground(colors[1]);
            lblSeats.setText(ban.getSoChoNgoi() + " chỗ");
            boolean selected = banDangChon != null && Objects.equals(banDangChon.getMaBan(), ban.getMaBan());
            setBackground(selected ? new Color(235, 244, 255) : Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(selected ? new Color(40, 100, 180) : new Color(228, 228, 228), selected ? 2 : 1, true),
                    new EmptyBorder(12, 12, 12, 12)));
            repaint();
        }
    }

    private Color[] mauTheoTrangThai(String status) {
        if (STATUS_PHUC_VU.equalsIgnoreCase(status)) {
            return new Color[] { new Color(255, 235, 238), new Color(183, 28, 28) };
        }
        if (STATUS_DAT.equalsIgnoreCase(status)) {
            return new Color[] { new Color(255, 248, 225), new Color(230, 81, 0) };
        }
        if (STATUS_BAO_TRI.equalsIgnoreCase(status)) {
            return new Color[] { new Color(232, 234, 246), new Color(49, 27, 146) };
        }
        return new Color[] { new Color(232, 245, 233), new Color(27, 94, 32) };
    }
}
