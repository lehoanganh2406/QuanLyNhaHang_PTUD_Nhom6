package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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

public class Ban_GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String FILTER_ALL = "ALL";
    private static final String STATUS_TRONG = "Bàn trống";
    private static final String STATUS_DAT = "Đã đặt";
    private static final String STATUS_PHUC_VU = "Đang phục vụ";
    private static final String STATUS_GHEP = "Đang ghép";
    private static final String STATUS_BAO_TRI = "Bảo trì";

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
    private JTextField txtSearch;
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

        setTitle("Quản lý bàn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        JPanel mainPanel = createMainPanel();

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(menu, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();
                mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
                menu.setBounds(0, 0, w, h);
                layeredPane.revalidate();
                layeredPane.repaint();
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 760));
        setLocationRelativeTo(null);

        napDuLieuVaRender(true);
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

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        addFloorFilterButton(filterRow, "Tất cả", FILTER_ALL, true);
        addFloorFilterButton(filterRow, "Tầng 1 (A)", "KV01", false);
        addFloorFilterButton(filterRow, "Tầng 2 (B)", "KV02", false);
        addFloorFilterButton(filterRow, "Sân thượng (C)", "KV03", false);

        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        txtSearch.setToolTipText("Tìm theo mã bàn hoặc tên bàn");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { renderData(); }
            @Override
            public void removeUpdate(DocumentEvent e) { renderData(); }
            @Override
            public void changedUpdate(DocumentEvent e) { renderData(); }
        });
        filterRow.add(Box.createRigidArea(new Dimension(10, 0)));
        filterRow.add(txtSearch);

        JButton btnLamMoi = createActionButton("Làm mới", new Color(245, 235, 220), new Color(120, 90, 70));
        btnLamMoi.addActionListener(e -> napDuLieuVaRender(true));
        filterRow.add(btnLamMoi);

        actionCard.add(filterRow, BorderLayout.NORTH);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);

        JButton btnThem = createActionButton("Thêm bàn", new Color(225, 240, 255), new Color(40, 100, 180));
        btnThem.addActionListener(e -> moDialogThemBan());

        JButton btnSua = createActionButton("Sửa bàn", new Color(230, 244, 234), new Color(46, 125, 50));
        btnSua.addActionListener(e -> moDialogSuaBan());

        JButton btnXoa = createActionButton("Xóa bàn", new Color(255, 235, 238), new Color(198, 40, 40));
        btnXoa.addActionListener(e -> xuLyXoaBan());

        JButton btnGhep = createActionButton("Ghép bàn", new Color(255, 243, 224), new Color(230, 81, 0));
        btnGhep.addActionListener(e -> moDialogGhepBan());

        JButton btnHuyGhep = createActionButton("Hủy ghép", new Color(243, 229, 245), new Color(106, 27, 154));
        btnHuyGhep.addActionListener(e -> moDialogHuyGhepBan());

        JButton btnChuyen = createActionButton("Chuyển khu vực", new Color(232, 245, 253), new Color(2, 119, 189));
        btnChuyen.addActionListener(e -> moDialogChuyenKhuVuc());

        buttonRow.add(btnThem);
        buttonRow.add(btnSua);
        buttonRow.add(btnXoa);
        buttonRow.add(btnGhep);
        buttonRow.add(btnHuyGhep);
        buttonRow.add(btnChuyen);

        actionCard.add(buttonRow, BorderLayout.CENTER);
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
        lblChonTrangThai = addDetailRow(form, gbc, "Trạng thái hiện tại:");

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

        JPanel noteCard = createCard();
        noteCard.setLayout(new BoxLayout(noteCard, BoxLayout.Y_AXIS));
        JLabel lblGuide = new JLabel("Quy tắc xử lý");
        lblGuide.setFont(new Font("SansSerif", Font.BOLD, 17));
        noteCard.add(lblGuide);
        noteCard.add(Box.createRigidArea(new Dimension(0, 8)));
        noteCard.add(createGuideLabel("• Xóa bàn chỉ cho phép khi bàn không có lịch sử hóa đơn/đặt bàn và đang trống."));
        noteCard.add(Box.createRigidArea(new Dimension(0, 6)));
        noteCard.add(createGuideLabel("• Chuyển khu vực sẽ tạo mã bàn mới theo ký hiệu khu vực đích để giữ đúng A/B/C."));
        noteCard.add(Box.createRigidArea(new Dimension(0, 6)));
        noteCard.add(createGuideLabel("• Ghép bàn được lưu bằng trạng thái + ghi chú vì CSDL hiện tại chưa có bảng quan hệ ghép bàn riêng."));

        rightPanel.add(statsCard);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(selectedCard);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(noteCard);
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

    private JLabel createGuideLabel(String text) {
        JLabel label = new JLabel("<html><div style='width:280px; line-height:1.5; color:#555555;'>" + text + "</div></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private void addFloorFilterButton(JPanel parent, String label, String key, boolean selected) {
        JButton button = createFilterButton(label, selected);
        button.addActionListener(e -> {
            floorFilter = key;
            capNhatStyleNutLoc();
            renderData();
        });
        floorButtonMap.put(key, button);
        parent.add(button);
    }

    private void capNhatStyleNutLoc() {
        for (Map.Entry<String, JButton> entry : floorButtonMap.entrySet()) {
            boolean selected = Objects.equals(entry.getKey(), floorFilter);
            styleFilterButton(entry.getValue(), selected);
        }
    }

    private JButton createFilterButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        styleFilterButton(button, selected);
        return button;
    }

    private void styleFilterButton(JButton button, boolean selected) {
        if (selected) {
            button.setBackground(new Color(40, 100, 180));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(40, 100, 180), 1, true),
                    new EmptyBorder(8, 14, 8, 14)));
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(80, 80, 80));
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(8, 14, 8, 14)));
        }
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(bg);
        button.setForeground(fg);
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

        for (Object obj : khuVucDAO.getAllKhuVuc()) {
            if (obj instanceof KhuVuc) {
                KhuVuc kv = (KhuVuc) obj;
                khuVucMap.put(kv.getMaKhuVuc(), kv);
            }
        }

        for (Object obj : banDAO.getAllBan()) {
            if (obj instanceof Ban) {
                dsBan.add((Ban) obj);
            }
        }
        sapXepBan(dsBan);
        napTrangThaiHienTai();

        if (giuLaiLuaChon && maDangChon != null) {
            banDangChon = timBanTheoMa(maDangChon);
        } else {
            banDangChon = null;
        }

        renderData();
        capNhatThongTinChon();
    }

    private void napTrangThaiHienTai() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        for (Object obj : banDAO.getDanhSachBanTheoThoiGian(now)) {
            if (obj instanceof String[]) {
                String[] row = (String[]) obj;
                if (row.length >= 3) {
                    hienThiTrangThaiMap.put(row[0], row[2]);
                }
            }
        }
    }

    private void renderData() {
        floorsContainer.removeAll();
        banCardMap.clear();

        Map<String, List<Ban>> grouped = new LinkedHashMap<>();
        for (String maKV : khuVucMap.keySet()) {
            grouped.put(maKV, new ArrayList<>());
        }

        for (Ban ban : dsBan) {
            if (!phuHopBoLoc(ban)) {
                continue;
            }
            String maKV = ban.getMaKhuVuc() == null ? "" : ban.getMaKhuVuc().getMaKhuVuc();
            grouped.computeIfAbsent(maKV, k -> new ArrayList<>()).add(ban);
        }

        for (Map.Entry<String, KhuVuc> entry : khuVucMap.entrySet()) {
            String maKV = entry.getKey();
            KhuVuc kv = entry.getValue();
            List<Ban> dsTheoKV = grouped.getOrDefault(maKV, Collections.emptyList());
            floorsContainer.add(createFloorSection(kv, dsTheoKV));
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

        String title = xayDungTenHienThiKhuVuc(khuVuc);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel lblSub = new JLabel("Ký hiệu: " + safe(khuVuc.getKyHieu(), "-") + " • Sức chứa khu vực: " + khuVuc.getSoLuongBan() + " bàn");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(110, 110, 110));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        header.add(titlePanel, BorderLayout.WEST);

        JLabel lblCount = new JLabel(dsTheoKV.size() + " / " + khuVuc.getSoLuongBan() + " bàn");
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCount.setForeground(new Color(40, 100, 180));
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
        if (ban == null) {
            return false;
        }
        String maKV = ban.getMaKhuVuc() == null ? "" : ban.getMaKhuVuc().getMaKhuVuc();
        if (!FILTER_ALL.equals(floorFilter) && !Objects.equals(floorFilter, maKV)) {
            return false;
        }
        String keyword = normalize(txtSearch == null ? "" : txtSearch.getText());
        if (keyword.isEmpty()) {
            return true;
        }
        return normalize(ban.getMaBan()).contains(keyword)
                || normalize(ban.getTenBan()).contains(keyword)
                || normalize(layTenKhuVuc(ban)).contains(keyword);
    }

    private void capNhatThongKe() {
        int tong = 0;
        int trong = 0;
        int daDat = 0;
        int dangPhucVu = 0;

        for (Ban ban : dsBan) {
            if (!phuHopBoLoc(ban)) {
                continue;
            }
            tong++;
            String status = layTrangThaiHienThi(ban);
            if (STATUS_TRONG.equalsIgnoreCase(status) || "Trống".equalsIgnoreCase(status)) {
                trong++;
            } else if (STATUS_DAT.equalsIgnoreCase(status)) {
                daDat++;
            } else if (STATUS_PHUC_VU.equalsIgnoreCase(status)) {
                dangPhucVu++;
            }
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
        lblChonTen.setText(safe(banDangChon.getTenBan(), "-"));
        lblChonKhu.setText(safe(layTenKhuVuc(banDangChon), "-"));
        lblChonLoai.setText(safe(layTenLoaiBan(banDangChon), "-"));
        lblChonChoNgoi.setText(xayDungThongTinChoNgoi(banDangChon));
        lblChonTrangThai.setText(safe(layTrangThaiHienThi(banDangChon), "-"));
        txtGhiChu.setText(xayDungGhiChuChiTiet(banDangChon));
    }

    private void resetSelectedInfo() {
        lblDangChon.setText("Chưa chọn bàn");
        lblDangChon.setForeground(new Color(120, 120, 120));
        lblChonMa.setText("-");
        lblChonTen.setText("-");
        lblChonKhu.setText("-");
        lblChonLoai.setText("-");
        lblChonChoNgoi.setText("-");
        lblChonTrangThai.setText("-");
        txtGhiChu.setText("Chọn một bàn ở danh sách bên trái để xem thông tin chi tiết, chỉnh sửa, ghép bàn hoặc chuyển khu vực.");
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

    private void moDialogThemBan() {
        if (khuVucMap.isEmpty()) {
            thongBao("Không có dữ liệu khu vực để thêm bàn.");
            return;
        }

        JComboBox<ComboItem<KhuVuc>> cboKhuVuc = new JComboBox<>(taoDanhSachKhuVuc());
        cboKhuVuc.setRenderer(new ComboRenderer<>());
        JComboBox<ComboItem<String>> cboLoaiBan = new JComboBox<>(taoDanhSachLoaiBan());
        cboLoaiBan.setRenderer(new ComboRenderer<>());
        JTextField txtTenBan = new JTextField();
        JSpinner spnChoNgoi = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_TRONG, STATUS_BAO_TRI });
        JTextArea txtAreaGhiChu = createDialogTextArea();
        JLabel lblPreviewMa = createPreviewLabel();

        Runnable capNhatPreview = () -> {
            @SuppressWarnings("unchecked")
            ComboItem<KhuVuc> item = (ComboItem<KhuVuc>) cboKhuVuc.getSelectedItem();
            if (item == null || item.getValue() == null) {
                lblPreviewMa.setText("Mã bàn dự kiến: -");
                return;
            }
            lblPreviewMa.setText("Mã bàn dự kiến: " + taoMaBanMoi(item.getValue(), null));
        };
        cboKhuVuc.addActionListener(e -> capNhatPreview.run());
        cboLoaiBan.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            ComboItem<String> item = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            if (item != null) {
                spnChoNgoi.setValue(macDinhChoNgoiTheoLoai(item.getValue()));
            }
        });
        capNhatPreview.run();

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Khu vực", cboKhuVuc);
        addFormRow(panel, 1, "Loại bàn", cboLoaiBan);
        addFormRow(panel, 2, "Tên bàn", txtTenBan);
        addFormRow(panel, 3, "Số chỗ ngồi", spnChoNgoi);
        addFormRow(panel, 4, "Trạng thái", cboTrangThai);
        addFormRow(panel, 5, "Ghi chú", new JScrollPane(txtAreaGhiChu));
        addFormRow(panel, 6, "Thông tin", lblPreviewMa);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Thêm bàn mới", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            @SuppressWarnings("unchecked")
            ComboItem<KhuVuc> itemKV = (ComboItem<KhuVuc>) cboKhuVuc.getSelectedItem();
            @SuppressWarnings("unchecked")
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            if (itemKV == null || itemKV.getValue() == null || itemLoai == null || itemLoai.getValue() == null) {
                thongBao("Vui lòng chọn khu vực và loại bàn.");
                continue;
            }

            KhuVuc kv = itemKV.getValue();
            if (!conSucChuaDeThem(kv.getMaKhuVuc(), null)) {
                thongBao("Khu vực này đã đủ số lượng bàn theo cấu hình (" + kv.getSoLuongBan() + " bàn).");
                continue;
            }

            String maMoi = taoMaBanMoi(kv, null);
            String tenBan = safe(txtTenBan.getText(), "").trim();
            if (tenBan.isEmpty()) {
                tenBan = "Bàn " + maMoi;
            }

            int soCho = ((Number) spnChoNgoi.getValue()).intValue();
            String ghiChu = txtAreaGhiChu.getText();
            String trangThai = Objects.toString(cboTrangThai.getSelectedItem(), STATUS_TRONG);

            Ban banMoi = new Ban(maMoi, kv, new LoaiBan(itemLoai.getValue(), itemLoai.getLabel()), tenBan,
                    nullIfBlank(ghiChu), soCho, trangThai);

            if (banDAO.themBan(banMoi)) {
                thongBao("Đã thêm bàn " + maMoi + " thành công.");
                banDangChon = banMoi;
                napDuLieuVaRender(true);
                return;
            }
            thongBaoLoi("Thêm bàn thất bại. Kiểm tra lại dữ liệu hoặc kết nối CSDL.");
            return;
        }
    }

    private void moDialogSuaBan() {
        Ban ban = yeuCauChonBan();
        if (ban == null) {
            return;
        }

        JTextField txtMaBan = new JTextField(ban.getMaBan());
        txtMaBan.setEditable(false);
        JTextField txtKhuVuc = new JTextField(layTenKhuVuc(ban));
        txtKhuVuc.setEditable(false);

        JComboBox<ComboItem<String>> cboLoaiBan = new JComboBox<>(taoDanhSachLoaiBan());
        cboLoaiBan.setRenderer(new ComboRenderer<>());
        chonLoaiBan(cboLoaiBan, ban.getMaLoaiBan() == null ? null : ban.getMaLoaiBan().getMaLoaiBan());

        JTextField txtTenBan = new JTextField(ban.getTenBan());
        JSpinner spnChoNgoi = new JSpinner(new SpinnerNumberModel(ban.getSoChoNgoi(), 1, 50, 1));
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { STATUS_TRONG, STATUS_DAT, STATUS_PHUC_VU, STATUS_GHEP, STATUS_BAO_TRI });
        cboTrangThai.setSelectedItem(chuanHoaTrangThaiDeLuu(ban));
        JTextArea txtAreaGhiChu = createDialogTextArea();
        txtAreaGhiChu.setText(safe(ban.getGhiChu(), ""));

        cboLoaiBan.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            if (itemLoai != null) {
                spnChoNgoi.setValue(macDinhChoNgoiTheoLoai(itemLoai.getValue()));
            }
        });

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Mã bàn", txtMaBan);
        addFormRow(panel, 1, "Khu vực", txtKhuVuc);
        addFormRow(panel, 2, "Loại bàn", cboLoaiBan);
        addFormRow(panel, 3, "Tên bàn", txtTenBan);
        addFormRow(panel, 4, "Số chỗ ngồi", spnChoNgoi);
        addFormRow(panel, 5, "Trạng thái lưu", cboTrangThai);
        addFormRow(panel, 6, "Ghi chú", new JScrollPane(txtAreaGhiChu));

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Sửa thông tin bàn", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            @SuppressWarnings("unchecked")
            ComboItem<String> itemLoai = (ComboItem<String>) cboLoaiBan.getSelectedItem();
            if (itemLoai == null) {
                thongBao("Vui lòng chọn loại bàn.");
                continue;
            }

            String tenBan = safe(txtTenBan.getText(), "").trim();
            if (tenBan.isEmpty()) {
                thongBao("Tên bàn không được để trống.");
                continue;
            }

            ban.setTenBan(tenBan);
            ban.setMaLoaiBan(new LoaiBan(itemLoai.getValue(), itemLoai.getLabel()));
            ban.setSoChoNgoi(((Number) spnChoNgoi.getValue()).intValue());
            ban.setTrangThai(Objects.toString(cboTrangThai.getSelectedItem(), STATUS_TRONG));
            ban.setGhiChu(nullIfBlank(txtAreaGhiChu.getText()));

            if (banDAO.capNhatBan(ban)) {
                thongBao("Đã cập nhật bàn " + ban.getMaBan() + ".");
                banDangChon = ban;
                napDuLieuVaRender(true);
                return;
            }
            thongBaoLoi("Cập nhật thất bại. Kiểm tra lại dữ liệu hoặc kết nối CSDL.");
            return;
        }
    }

    private void xuLyXoaBan() {
        Ban ban = yeuCauChonBan();
        if (ban == null) {
            return;
        }
        String status = layTrangThaiHienThi(ban);
        if (!laBanTrong(status)) {
            thongBao("Chỉ được xóa bàn đang trống. Bàn hiện tại đang ở trạng thái: " + status);
            return;
        }
        if (coDuLieuLienQuan(ban.getMaBan())) {
            thongBao("Không thể xóa vì bàn này đã có dữ liệu hóa đơn hoặc phiếu đặt bàn liên quan.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa bàn " + ban.getMaBan() + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

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
        if (ban == null) {
            return;
        }

        String status = layTrangThaiHienThi(ban);
        if (!laBanTrong(status) && !STATUS_BAO_TRI.equalsIgnoreCase(status)) {
            thongBao("Chỉ chuyển khu vực khi bàn đang trống hoặc bảo trì. Trạng thái hiện tại: " + status);
            return;
        }
        if (coDuLieuLienQuan(ban.getMaBan())) {
            thongBao("Không thể chuyển khu vực cho bàn đã có dữ liệu lịch sử vì mã bàn đang được tham chiếu ở hóa đơn/phiếu đặt bàn.");
            return;
        }

        JComboBox<ComboItem<KhuVuc>> cboKhuVucDich = new JComboBox<>(taoDanhSachKhuVuc());
        cboKhuVucDich.setRenderer(new ComboRenderer<>());
        boChonKhuVuc(cboKhuVucDich, ban.getMaKhuVuc() == null ? null : ban.getMaKhuVuc().getMaKhuVuc());

        JLabel lblMaMoi = createPreviewLabel();
        JTextArea txtLyDo = createDialogTextArea();
        txtLyDo.setRows(4);

        Runnable capNhatMaMoi = () -> {
            @SuppressWarnings("unchecked")
            ComboItem<KhuVuc> item = (ComboItem<KhuVuc>) cboKhuVucDich.getSelectedItem();
            if (item == null || item.getValue() == null) {
                lblMaMoi.setText("Mã bàn mới: -");
                return;
            }
            if (Objects.equals(item.getValue().getMaKhuVuc(), ban.getMaKhuVuc().getMaKhuVuc())) {
                lblMaMoi.setText("Mã bàn mới: giữ nguyên khu vực hiện tại");
            } else {
                lblMaMoi.setText("Mã bàn mới: " + taoMaBanMoi(item.getValue(), null));
            }
        };
        cboKhuVucDich.addActionListener(e -> capNhatMaMoi.run());
        capNhatMaMoi.run();

        JPanel panel = createFormPanel();
        addFormRow(panel, 0, "Bàn hiện tại", new JLabel(ban.getMaBan() + " - " + ban.getTenBan()));
        addFormRow(panel, 1, "Khu vực hiện tại", new JLabel(layTenKhuVuc(ban)));
        addFormRow(panel, 2, "Khu vực đích", cboKhuVucDich);
        addFormRow(panel, 3, "Mã bàn sau khi chuyển", lblMaMoi);
        addFormRow(panel, 4, "Lý do chuyển khu vực", new JScrollPane(txtLyDo));

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Chuyển khu vực bàn", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            @SuppressWarnings("unchecked")
            ComboItem<KhuVuc> item = (ComboItem<KhuVuc>) cboKhuVucDich.getSelectedItem();
            if (item == null || item.getValue() == null) {
                thongBao("Vui lòng chọn khu vực đích.");
                continue;
            }
            KhuVuc khuVucDich = item.getValue();
            if (Objects.equals(khuVucDich.getMaKhuVuc(), ban.getMaKhuVuc().getMaKhuVuc())) {
                thongBao("Vui lòng chọn khu vực khác khu vực hiện tại.");
                continue;
            }
            if (!conSucChuaDeThem(khuVucDich.getMaKhuVuc(), null)) {
                thongBao("Khu vực đích đã đủ số lượng bàn theo cấu hình.");
                continue;
            }
            String lyDo = safe(txtLyDo.getText(), "").trim();
            if (lyDo.isEmpty()) {
                thongBao("Vui lòng nhập lý do chuyển khu vực.");
                continue;
            }

            String maBanMoi = taoMaBanMoi(khuVucDich, null);
            if (thucHienChuyenKhuVuc(ban, khuVucDich, maBanMoi, lyDo)) {
                thongBao("Đã chuyển " + ban.getMaBan() + " sang khu vực " + khuVucDich.getTenKhuVuc() + " với mã mới " + maBanMoi + ".");
                banDangChon = timBanTheoMa(maBanMoi);
                napDuLieuVaRender(true);
            } else {
                thongBaoLoi("Chuyển khu vực thất bại.");
            }
            return;
        }
    }

    private void moDialogGhepBan() {
        Ban banNguon = yeuCauChonBan();
        if (banNguon == null) {
            return;
        }
        if (layThongTinGhepBan(banNguon) != null) {
            thongBao("Bàn đang chọn đã nằm trong một cụm ghép. Vui lòng hủy ghép trước khi ghép lại.");
            return;
        }

        List<Ban> dsBanCoTheGhep = new ArrayList<>();
        for (Ban item : dsBan) {
            if (Objects.equals(item.getMaBan(), banNguon.getMaBan())) {
                continue;
            }
            if (layThongTinGhepBan(item) != null) {
                continue;
            }
            dsBanCoTheGhep.add(item);
        }
        if (dsBanCoTheGhep.isEmpty()) {
            thongBao("Không có bàn trống phù hợp để ghép.");
            return;
        }

        JComboBox<ComboItem<Ban>> cboBanDich = new JComboBox<>(taoDanhSachBan(dsBanCoTheGhep));
        cboBanDich.setRenderer(new ComboRenderer<>());
        JTextArea txtLyDo = createDialogTextArea();
        txtLyDo.setRows(4);
        JLabel lblPreviewCum = createPreviewLabel();

        Runnable capNhatPreview = () -> {
            @SuppressWarnings("unchecked")
            ComboItem<Ban> selected = (ComboItem<Ban>) cboBanDich.getSelectedItem();
            Ban banDich = selected == null ? null : selected.getValue();
            if (banDich == null) {
                lblPreviewCum.setText("Cụm bàn sau ghép: -");
                return;
            }
            lblPreviewCum.setText("Cụm bàn sau ghép: " + taoNhanCumGhep(banNguon, banDich)
                    + " = " + (banNguon.getSoChoNgoi() + banDich.getSoChoNgoi()) + " chỗ");
        };
        cboBanDich.addActionListener(e -> capNhatPreview.run());
        capNhatPreview.run();

        JTextArea txtThongTin = createInfoTextArea(
                "Sau khi ghép, cả 2 bàn sẽ chuyển sang trạng thái \"Đang ghép\". Giao diện sẽ hiển thị cụm ghép như B05 + B06 = 8 chỗ và có thể hủy ghép lại sau đó.");

        JPanel formPanel = createFormPanel();
        addFormRow(formPanel, 0, "Bàn nguồn", createValueLabel(banNguon.getMaBan() + " - " + banNguon.getTenBan()));
        addFormRow(formPanel, 1, "Bàn ghép với", cboBanDich);
        addFormRow(formPanel, 2, "Cụm ghép dự kiến", lblPreviewCum);
        addFormRow(formPanel, 3, "Lý do ghép bàn", new JScrollPane(txtLyDo));
        addFormRow(formPanel, 4, "Lưu ý", txtThongTin);

        JPanel panel = wrapDialogContent(
                "Ghép bàn",
                "Chọn bàn cần ghép cùng và nhập lý do để tạo cụm bàn ghép.",
                formPanel);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Ghép bàn", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            @SuppressWarnings("unchecked")
            ComboItem<Ban> item = (ComboItem<Ban>) cboBanDich.getSelectedItem();
            if (item == null || item.getValue() == null) {
                thongBao("Vui lòng chọn bàn ghép cùng.");
                continue;
            }
            Ban banDich = item.getValue();
            String lyDo = safe(txtLyDo.getText(), "").trim();
            if (lyDo.isEmpty()) {
                thongBao("Vui lòng nhập lý do ghép bàn.");
                continue;
            }
            if (layThongTinGhepBan(banDich) != null) {
                thongBao("Bàn ghép cùng hiện đã nằm trong cụm ghép khác.");
                continue;
            }
            if (STATUS_PHUC_VU.equalsIgnoreCase(layTrangThaiHienThi(banNguon))
                    || STATUS_PHUC_VU.equalsIgnoreCase(layTrangThaiHienThi(banDich))) {
                thongBao("Không hỗ trợ ghép khi một trong hai bàn đang phục vụ.");
                return;
            }

            if (thucHienGhepBan(banNguon, banDich, lyDo)) {
                thongBao("Đã ghép thành công cụm " + taoNhanCumGhep(banNguon, banDich)
                        + " = " + (banNguon.getSoChoNgoi() + banDich.getSoChoNgoi()) + " chỗ.");
                banDangChon = timBanTheoMa(banNguon.getMaBan());
                napDuLieuVaRender(true);
            } else {
                thongBaoLoi("Ghép bàn thất bại.");
            }
            return;
        }
    }

    private void moDialogHuyGhepBan() {
        Ban banChon = yeuCauChonBan();
        if (banChon == null) {
            return;
        }

        GhepBanMeta meta = layThongTinGhepBan(banChon);
        if (meta == null) {
            thongBao("Bàn đang chọn hiện không nằm trong cụm ghép nào.");
            return;
        }

        Ban banConLai = timBanTheoMa(meta.partnerCode);
        if (banConLai == null) {
            thongBaoLoi("Không tìm thấy bàn ghép cùng để hủy ghép.");
            return;
        }

        JTextArea txtLyDo = createDialogTextArea();
        txtLyDo.setRows(4);
        JTextArea txtThongTin = createInfoTextArea(
                "Sau khi hủy ghép, 2 bàn sẽ tách ra và quay về trạng thái trước khi ghép. Lịch sử ghép/hủy vẫn được lưu ở phần ghi chú.");

        JPanel formPanel = createFormPanel();
        addFormRow(formPanel, 0, "Bàn đang chọn", createValueLabel(banChon.getMaBan() + " - " + banChon.getTenBan()));
        addFormRow(formPanel, 1, "Bàn ghép cùng", createValueLabel(banConLai.getMaBan() + " - " + banConLai.getTenBan()));
        addFormRow(formPanel, 2, "Cụm đang ghép", createValueLabel(meta.clusterLabel + " = " + meta.totalSeats + " chỗ"));
        addFormRow(formPanel, 3, "Lý do hủy ghép", new JScrollPane(txtLyDo));
        addFormRow(formPanel, 4, "Lưu ý", txtThongTin);

        JPanel panel = wrapDialogContent(
                "Hủy ghép bàn",
                "Xác nhận tách cụm bàn ghép hiện tại và nhập lý do hủy ghép.",
                formPanel);

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Hủy ghép bàn", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            String lyDo = safe(txtLyDo.getText(), "").trim();
            if (lyDo.isEmpty()) {
                thongBao("Vui lòng nhập lý do hủy ghép bàn.");
                continue;
            }

            if (thucHienHuyGhepBan(banChon, banConLai, meta, lyDo)) {
                thongBao("Đã hủy ghép cụm " + meta.clusterLabel + " thành công.");
                banDangChon = timBanTheoMa(banChon.getMaBan());
                napDuLieuVaRender(true);
            } else {
                thongBaoLoi("Hủy ghép bàn thất bại.");
            }
            return;
        }
    }

    private boolean thucHienChuyenKhuVuc(Ban banCu, KhuVuc khuVucMoi, String maBanMoi, String lyDo) {
        Connection con = null;
        PreparedStatement insertStmt = null;
        PreparedStatement deleteStmt = null;
        try {
            con = ConnectDB.getConnection();
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                String insertSql = "INSERT INTO Ban(maBan, maKhuVuc, maLoaiBan, tenBan, ghiChu, soChoNgoi, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
                insertStmt = con.prepareStatement(insertSql);
                insertStmt.setString(1, maBanMoi);
                insertStmt.setString(2, khuVucMoi.getMaKhuVuc());
                insertStmt.setString(3, banCu.getMaLoaiBan() == null ? "LB02" : banCu.getMaLoaiBan().getMaLoaiBan());
                insertStmt.setString(4, "Bàn " + maBanMoi);
                insertStmt.setString(5, taoGhiChuChuyenKhuVuc(banCu, lyDo));
                insertStmt.setInt(6, banCu.getSoChoNgoi());
                insertStmt.setString(7, chuanHoaTrangThaiDeLuu(banCu));
                insertStmt.executeUpdate();

                deleteStmt = con.prepareStatement("DELETE FROM Ban WHERE maBan = ?");
                deleteStmt.setString(1, banCu.getMaBan());
                deleteStmt.executeUpdate();

                con.commit();
                con.setAutoCommit(oldAutoCommit);
                return true;
            } catch (Exception ex) {
                con.rollback();
                con.setAutoCommit(oldAutoCommit);
                ex.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(deleteStmt);
            closeQuietly(insertStmt);
        }
    }

    private boolean thucHienGhepBan(Ban banNguon, Ban banDich, String lyDo) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = ConnectDB.getConnection();
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                String sql = "UPDATE Ban SET trangThai = ?, ghiChu = ? WHERE maBan = ?";
                stmt = con.prepareStatement(sql);

                stmt.setString(1, STATUS_GHEP);
                stmt.setString(2, taoGhiChuGhepBan(banNguon, banDich, lyDo, true));
                stmt.setString(3, banNguon.getMaBan());
                stmt.executeUpdate();

                stmt.clearParameters();
                stmt.setString(1, STATUS_GHEP);
                stmt.setString(2, taoGhiChuGhepBan(banNguon, banDich, lyDo, false));
                stmt.setString(3, banDich.getMaBan());
                stmt.executeUpdate();

                con.commit();
                con.setAutoCommit(oldAutoCommit);
                return true;
            } catch (Exception ex) {
                con.rollback();
                con.setAutoCommit(oldAutoCommit);
                ex.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(stmt);
        }
    }

    private boolean thucHienHuyGhepBan(Ban banDangChon, Ban banConLai, GhepBanMeta meta, String lyDo) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = ConnectDB.getConnection();
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                GhepBanMeta metaConLai = layThongTinGhepBan(banConLai);
                String trangThaiBanDangChon = meta.selfBeforeStatus;
                String trangThaiBanConLai = metaConLai != null ? metaConLai.selfBeforeStatus : meta.partnerBeforeStatus;

                String sql = "UPDATE Ban SET trangThai = ?, ghiChu = ? WHERE maBan = ?";
                stmt = con.prepareStatement(sql);

                stmt.setString(1, trangThaiBanDangChon);
                stmt.setString(2, taoGhiChuHuyGhepBan(banDangChon, banConLai, lyDo));
                stmt.setString(3, banDangChon.getMaBan());
                stmt.executeUpdate();

                stmt.clearParameters();
                stmt.setString(1, trangThaiBanConLai);
                stmt.setString(2, taoGhiChuHuyGhepBan(banConLai, banDangChon, lyDo));
                stmt.setString(3, banConLai.getMaBan());
                stmt.executeUpdate();

                con.commit();
                con.setAutoCommit(oldAutoCommit);
                return true;
            } catch (Exception ex) {
                con.rollback();
                con.setAutoCommit(oldAutoCommit);
                ex.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(stmt);
        }
    }

    private boolean coDuLieuLienQuan(String maBan) {
        Connection con = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try {
            con = ConnectDB.getConnection();

            stmt1 = con.prepareStatement("SELECT TOP 1 1 FROM HoaDon WHERE maBan = ?");
            stmt1.setString(1, maBan);
            rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                return true;
            }

            stmt2 = con.prepareStatement("SELECT TOP 1 1 FROM PhieuDatBan WHERE maBan = ?");
            stmt2.setString(1, maBan);
            rs2 = stmt2.executeQuery();
            return rs2.next();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            closeQuietly(rs2);
            closeQuietly(stmt2);
            closeQuietly(rs1);
            closeQuietly(stmt1);
        }
    }

    private boolean conSucChuaDeThem(String maKhuVuc, String boQuaMaBan) {
        int dem = 0;
        for (Ban ban : dsBan) {
            if (ban.getMaKhuVuc() == null || !Objects.equals(maKhuVuc, ban.getMaKhuVuc().getMaKhuVuc())) {
                continue;
            }
            if (Objects.equals(boQuaMaBan, ban.getMaBan())) {
                continue;
            }
            dem++;
        }
        KhuVuc kv = khuVucMap.get(maKhuVuc);
        return kv != null && dem < kv.getSoLuongBan();
    }

    private String taoMaBanMoi(KhuVuc khuVuc, String boQuaMaBan) {
        String kyHieu = safe(khuVuc.getKyHieu(), "X").trim().toUpperCase(Locale.ROOT);
        int max = 0;
        Pattern pattern = Pattern.compile("^" + Pattern.quote(kyHieu) + "(\\d{2})$");
        for (Ban ban : dsBan) {
            String maBan = safe(ban.getMaBan(), "");
            if (Objects.equals(maBan, boQuaMaBan)) {
                continue;
            }
            Matcher matcher = pattern.matcher(maBan.toUpperCase(Locale.ROOT));
            if (matcher.matches()) {
                max = Math.max(max, Integer.parseInt(matcher.group(1)));
            }
        }
        return kyHieu + String.format("%02d", max + 1);
    }

    private Ban timBanTheoMa(String maBan) {
        for (Ban ban : dsBan) {
            if (Objects.equals(ban.getMaBan(), maBan)) {
                return ban;
            }
        }
        return null;
    }

    private void sapXepBan(List<Ban> danhSach) {
        danhSach.sort(Comparator
                .comparing((Ban b) -> b.getMaKhuVuc() == null ? "" : safe(b.getMaKhuVuc().getMaKhuVuc(), ""))
                .thenComparing(Ban::getMaBan, Comparator.nullsLast(String::compareToIgnoreCase)));
    }

    private String layTenKhuVuc(Ban ban) {
        if (ban == null || ban.getMaKhuVuc() == null) {
            return "-";
        }
        KhuVuc kv = khuVucMap.get(ban.getMaKhuVuc().getMaKhuVuc());
        return kv == null ? ban.getMaKhuVuc().getMaKhuVuc() : xayDungTenHienThiKhuVuc(kv);
    }

    private String layTenLoaiBan(Ban ban) {
        if (ban == null || ban.getMaLoaiBan() == null) {
            return "-";
        }
        String maLoai = ban.getMaLoaiBan().getMaLoaiBan();
        return loaiBanMap.getOrDefault(maLoai, maLoai);
    }

    private String layTrangThaiHienThi(Ban ban) {
        if (ban == null) {
            return "-";
        }
        String stored = ban.getTrangThai();
        if (STATUS_GHEP.equalsIgnoreCase(stored) || STATUS_BAO_TRI.equalsIgnoreCase(stored)) {
            return stored;
        }
        String current = hienThiTrangThaiMap.get(ban.getMaBan());
        if (current != null && !current.trim().isEmpty()) {
            return "Trống".equalsIgnoreCase(current) ? STATUS_TRONG : current;
        }
        if (stored == null || stored.trim().isEmpty()) {
            return STATUS_TRONG;
        }
        return stored;
    }

    private String chuanHoaTrangThaiDeLuu(Ban ban) {
        if (ban == null || ban.getTrangThai() == null || ban.getTrangThai().trim().isEmpty()) {
            return STATUS_TRONG;
        }
        return ban.getTrangThai();
    }

    private boolean laBanTrong(String status) {
        return STATUS_TRONG.equalsIgnoreCase(status) || "Trống".equalsIgnoreCase(status);
    }

    private String xayDungTenHienThiKhuVuc(KhuVuc kv) {
        if (kv == null) {
            return "-";
        }
        String ten = safe(kv.getTenKhuVuc(), kv.getMaKhuVuc());
        String kyHieu = safe(kv.getKyHieu(), "").trim();
        return kyHieu.isEmpty() ? ten : ten + " (" + kyHieu + ")";
    }

    private String xayDungGhiChuChiTiet(Ban ban) {
        StringBuilder sb = new StringBuilder();
        sb.append("Trạng thái hiển thị hiện tại: ").append(layTrangThaiHienThi(ban));
        sb.append("\nTrạng thái lưu trong bảng Ban: ").append(safe(chuanHoaTrangThaiDeLuu(ban), STATUS_TRONG));
        sb.append("\nCập nhật lúc: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        GhepBanMeta meta = layThongTinGhepBan(ban);
        if (meta != null) {
            sb.append("\n\nCụm ghép hiện tại: ").append(meta.clusterLabel).append(" = ").append(meta.totalSeats).append(" chỗ");
            sb.append("\nBàn ghép cùng: ").append(meta.partnerCode);
            sb.append("\nTrạng thái trước khi ghép: ").append(meta.selfBeforeStatus);
        }

        sb.append("\n\nGhi chú bàn:");
        String ghiChu = lamSachGhiChuDeHienThi(ban.getGhiChu());
        sb.append(ghiChu.isEmpty() ? "\n- Không có ghi chú." : "\n- " + ghiChu.replace("\n", "\n- "));
        return sb.toString();
    }

    private String taoGhiChuChuyenKhuVuc(Ban banCu, String lyDo) {
        String ghiChuCu = safe(banCu.getGhiChu(), "").trim();
        StringBuilder sb = new StringBuilder();
        if (!ghiChuCu.isEmpty()) {
            sb.append(ghiChuCu).append("\n");
        }
        sb.append("[Chuyển khu vực] Từ ").append(banCu.getMaBan())
          .append(" lúc ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
          .append(". Lý do: ").append(lyDo);
        return sb.toString();
    }

    private String taoGhiChuGhepBan(Ban banNguon, Ban banDich, String lyDo, boolean choBanNguon) {
        Ban hienTai = choBanNguon ? banNguon : banDich;
        Ban doiUng = choBanNguon ? banDich : banNguon;
        StringBuilder sb = new StringBuilder();
        String ghiChuCu = safe(hienTai.getGhiChu(), "").trim();
        String ghiChuKhongMeta = xoaDongMetaGhep(ghiChuCu);
        if (!ghiChuKhongMeta.isEmpty()) {
            sb.append(ghiChuKhongMeta).append("\n");
        }

        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String cluster = taoNhanCumGhep(banNguon, banDich);
        int tongCho = banNguon.getSoChoNgoi() + banDich.getSoChoNgoi();
        String trangThaiHienTai = safe(layTrangThaiHienThi(hienTai), STATUS_TRONG);
        String trangThaiDoiUng = safe(layTrangThaiHienThi(doiUng), STATUS_TRONG);

        sb.append(taoDongMetaGhep(cluster, doiUng.getMaBan(), tongCho, trangThaiHienTai, trangThaiDoiUng, thoiGian)).append("\n");
        sb.append("[Ghép bàn] ")
          .append(hienTai.getMaBan())
          .append(" ghép với ")
          .append(doiUng.getMaBan())
          .append(" lúc ")
          .append(thoiGian)
          .append(". Cụm ghép: ")
          .append(cluster)
          .append(" = ")
          .append(tongCho)
          .append(" chỗ. Lý do: ")
          .append(lyDo);
        return sb.toString();
    }

    private String taoGhiChuHuyGhepBan(Ban hienTai, Ban doiUng, String lyDo) {
        StringBuilder sb = new StringBuilder();
        String ghiChuCu = xoaDongMetaGhep(safe(hienTai.getGhiChu(), "").trim());
        if (!ghiChuCu.isEmpty()) {
            sb.append(ghiChuCu).append("\n");
        }
        sb.append("[Hủy ghép bàn] ")
          .append(hienTai.getMaBan())
          .append(" tách khỏi ")
          .append(doiUng.getMaBan())
          .append(" lúc ")
          .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
          .append(". Lý do: ")
          .append(lyDo);
        return sb.toString();
    }

    private String xayDungThongTinChoNgoi(Ban ban) {
        GhepBanMeta meta = layThongTinGhepBan(ban);
        if (meta == null) {
            return String.valueOf(ban.getSoChoNgoi());
        }
        return ban.getSoChoNgoi() + " (cụm ghép: " + meta.totalSeats + ")";
    }

    private String lamSachGhiChuDeHienThi(String ghiChu) {
        return xoaDongMetaGhep(safe(ghiChu, "").trim());
    }

    private String xoaDongMetaGhep(String ghiChu) {
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = ghiChu.split("\\r?\\n");
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            if (line.startsWith("[GHEP_META]|")) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(line);
        }
        return sb.toString().trim();
    }

    private String taoDongMetaGhep(String cluster, String partnerCode, int totalSeats, String selfBeforeStatus,
            String partnerBeforeStatus, String time) {
        return new StringBuilder()
                .append("[GHEP_META]|cluster=").append(cluster)
                .append("|partner=").append(partnerCode)
                .append("|total=").append(totalSeats)
                .append("|selfBefore=").append(selfBeforeStatus)
                .append("|otherBefore=").append(partnerBeforeStatus)
                .append("|time=").append(time)
                .toString();
    }

    private String taoNhanCumGhep(Ban banA, Ban banB) {
        List<String> codes = new ArrayList<>(Arrays.asList(
                safe(banA.getMaBan(), ""),
                safe(banB.getMaBan(), "")));
        codes.sort(String::compareToIgnoreCase);
        return codes.get(0) + " + " + codes.get(1);
    }

    private GhepBanMeta layThongTinGhepBan(Ban ban) {
        if (ban == null) {
            return null;
        }
        String ghiChu = safe(ban.getGhiChu(), "").trim();
        if (ghiChu.isEmpty()) {
            return null;
        }

        String[] lines = ghiChu.split("\\r?\\n");
        for (String line : lines) {
            if (line == null || !line.startsWith("[GHEP_META]|")) {
                continue;
            }
            GhepBanMeta meta = parseDongMetaGhep(line, ban.getMaBan());
            if (meta != null) {
                return meta;
            }
        }

        // Chỉ fallback đọc từ lịch sử ghép bàn khi bản ghi hiện vẫn đang ở trạng thái ghép.
        // Nếu bàn đã hủy ghép rồi thì ghi chú lịch sử "[Ghép bàn] ..." vẫn còn,
        // nhưng không được tiếp tục suy ra rằng bàn vẫn đang nằm trong cụm.
        if (!STATUS_GHEP.equalsIgnoreCase(safe(ban.getTrangThai(), "").trim())) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\[Ghép bàn\\]\\s*(\\S+)\\s*ghép với\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            String ma1 = matcher.group(1);
            String ma2 = matcher.group(2);
            String partner = Objects.equals(ma1, ban.getMaBan()) ? ma2 : ma1;
            Ban doiUng = timBanTheoMa(partner);
            int tongCho = ban.getSoChoNgoi() + (doiUng == null ? 0 : doiUng.getSoChoNgoi());
            String cluster = doiUng == null ? ban.getMaBan() + " + " + partner : taoNhanCumGhep(ban, doiUng);
            return new GhepBanMeta(cluster, partner, tongCho, STATUS_TRONG, STATUS_TRONG, null);
        }
        return null;
    }

    private GhepBanMeta parseDongMetaGhep(String line, String maBanHienTai) {
        String[] parts = line.split("\\|");
        Map<String, String> values = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int idx = part.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            values.put(part.substring(0, idx), part.substring(idx + 1));
        }
        String cluster = values.get("cluster");
        String partner = values.get("partner");
        String totalText = values.get("total");
        int total = 0;
        try {
            total = Integer.parseInt(totalText);
        } catch (Exception e) {
            Ban doiUng = timBanTheoMa(partner);
            Ban hienTai = timBanTheoMa(maBanHienTai);
            total = (hienTai == null ? 0 : hienTai.getSoChoNgoi()) + (doiUng == null ? 0 : doiUng.getSoChoNgoi());
        }
        if (cluster == null || partner == null) {
            return null;
        }
        return new GhepBanMeta(
                cluster,
                partner,
                total,
                values.getOrDefault("selfBefore", STATUS_TRONG),
                values.getOrDefault("otherBefore", STATUS_TRONG),
                values.get("time"));
    }

    private int macDinhChoNgoiTheoLoai(String maLoaiBan) {
        if (maLoaiBan == null) {
            return 4;
        }
        switch (maLoaiBan) {
            case "LB01":
                return 2;
            case "LB02":
                return 4;
            case "LB03":
                return 6;
            case "LB04":
                return 8;
            default:
                return 4;
        }
    }

    private ComboItem<KhuVuc>[] taoDanhSachKhuVuc() {
        List<ComboItem<KhuVuc>> ds = new ArrayList<>();
        for (KhuVuc kv : khuVucMap.values()) {
            ds.add(new ComboItem<>(xayDungTenHienThiKhuVuc(kv), kv));
        }
        @SuppressWarnings("unchecked")
        ComboItem<KhuVuc>[] arr = ds.toArray(new ComboItem[0]);
        return arr;
    }

    private ComboItem<String>[] taoDanhSachLoaiBan() {
        List<ComboItem<String>> ds = new ArrayList<>();
        for (Map.Entry<String, String> entry : loaiBanMap.entrySet()) {
            ds.add(new ComboItem<>(entry.getValue(), entry.getKey()));
        }
        @SuppressWarnings("unchecked")
        ComboItem<String>[] arr = ds.toArray(new ComboItem[0]);
        return arr;
    }

    private ComboItem<Ban>[] taoDanhSachBan(List<Ban> bans) {
        List<ComboItem<Ban>> ds = new ArrayList<>();
        for (Ban ban : bans) {
            GhepBanMeta meta = layThongTinGhepBan(ban);
            String label = ban.getMaBan() + " - " + ban.getTenBan() + " • " + layTenKhuVuc(ban)
                    + " • " + layTrangThaiHienThi(ban);
            if (meta != null) {
                label += " • Cụm: " + meta.clusterLabel + " = " + meta.totalSeats + " chỗ";
            }
            ds.add(new ComboItem<>(label, ban));
        }
        @SuppressWarnings("unchecked")
        ComboItem<Ban>[] arr = ds.toArray(new ComboItem[0]);
        return arr;
    }

    private void chonLoaiBan(JComboBox<ComboItem<String>> comboBox, String maLoaiBan) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem<String> item = comboBox.getItemAt(i);
            if (Objects.equals(item.getValue(), maLoaiBan)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void boChonKhuVuc(JComboBox<ComboItem<KhuVuc>> comboBox, String maKhuVuc) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem<KhuVuc> item = comboBox.getItemAt(i);
            if (!Objects.equals(item.getValue().getMaKhuVuc(), maKhuVuc)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(520, 0));
        return panel;
    }

    private void addFormRow(JPanel panel, int row, String label, Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(6, 0, 6, 12);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        if (comp instanceof JComponent) {
            ((JComponent) comp).setFont(new Font("SansSerif", Font.PLAIN, 13));
        }
        panel.add(comp, gbc);
    }

    private JTextArea createDialogTextArea() {
        JTextArea area = new JTextArea(5, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return area;
    }

    private JTextArea createInfoTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFocusable(false);
        area.setOpaque(true);
        area.setBackground(new Color(248, 249, 251));
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        area.setFont(new Font("SansSerif", Font.ITALIC, 12));
        area.setPreferredSize(new Dimension(0, 72));
        return area;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(40, 40, 40));
        return label;
    }

    private JPanel wrapDialogContent(String title, String subtitle, JPanel formPanel) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(12, 14, 12, 14));
        wrapper.setPreferredSize(new Dimension(680, 320));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(110, 110, 110));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(lblSubtitle);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel createPreviewLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(40, 100, 180));
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
        return safe(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private String safe(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private String nullIfBlank(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
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

        public String getLabel() {
            return label;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class ComboRenderer<T> extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ComboItem) {
                @SuppressWarnings("unchecked")
                ComboItem<T> item = (ComboItem<T>) value;
                setText(item.getLabel());
            }
            return this;
        }
    }

    private static class GhepBanMeta {
        private final String clusterLabel;
        private final String partnerCode;
        private final int totalSeats;
        private final String selfBeforeStatus;
        private final String partnerBeforeStatus;
        @SuppressWarnings("unused")
        private final String time;

        GhepBanMeta(String clusterLabel, String partnerCode, int totalSeats, String selfBeforeStatus,
                String partnerBeforeStatus, String time) {
            this.clusterLabel = clusterLabel;
            this.partnerCode = partnerCode;
            this.totalSeats = totalSeats;
            this.selfBeforeStatus = selfBeforeStatus == null || selfBeforeStatus.trim().isEmpty()
                    ? STATUS_TRONG : selfBeforeStatus;
            this.partnerBeforeStatus = partnerBeforeStatus == null || partnerBeforeStatus.trim().isEmpty()
                    ? STATUS_TRONG : partnerBeforeStatus;
            this.time = time;
        }
    }

    private class BanCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Ban ban;
        private final JLabel lblStatus;
        private final JLabel lblSeats;
        private final JLabel lblCluster;

        BanCard(Ban ban) {
            this.ban = ban;
            setLayout(new BorderLayout(0, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            JLabel lblCode = new JLabel(ban.getMaBan());
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

            JLabel lblName = new JLabel("<html><div style='width:150px;'><b>" + safe(ban.getTenBan(), ban.getMaBan()) + "</b></div></html>");
            lblName.setFont(new Font("SansSerif", Font.PLAIN, 13));
            center.add(lblName);
            center.add(Box.createRigidArea(new Dimension(0, 6)));

            JLabel lblLoai = new JLabel(layTenLoaiBan(ban));
            lblLoai.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblLoai.setForeground(new Color(95, 95, 95));
            center.add(lblLoai);
            center.add(Box.createRigidArea(new Dimension(0, 6)));

            lblCluster = new JLabel(" ");
            lblCluster.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblCluster.setForeground(new Color(106, 27, 154));
            center.add(lblCluster);
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
            GhepBanMeta meta = layThongTinGhepBan(ban);
            Color[] colors = mauTheoTrangThai(status);
            lblStatus.setText(status);
            lblStatus.setBackground(colors[0]);
            lblStatus.setForeground(colors[1]);

            if (meta != null) {
                lblSeats.setText(meta.totalSeats + " chỗ");
                lblCluster.setText("Cụm: " + meta.clusterLabel);
            } else {
                lblSeats.setText(ban.getSoChoNgoi() + " chỗ");
                lblCluster.setText(" ");
            }

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
        if (STATUS_GHEP.equalsIgnoreCase(status)) {
            return new Color[] { new Color(243, 229, 245), new Color(106, 27, 154) };
        }
        if (STATUS_BAO_TRI.equalsIgnoreCase(status)) {
            return new Color[] { new Color(232, 234, 246), new Color(49, 27, 146) };
        }
        return new Color[] { new Color(232, 245, 233), new Color(27, 94, 32) };
    }
}
