package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import connectDB.ConnectDB;
import dao.KhachHang_DAO;
import dao.LoaiKhachHang_DAO;
import entity.KhachHang;
import entity.LoaiKhachHang;
import entity.TaiKhoan;

public class KhachHang_GUI extends JPanel {


    private TaiKhoan taiKhoanDangNhap;

    private JTextField txtMaKH, txtTenKH, txtDiemTichLuy, txtSDT;
    private JComboBox<ComboItem<LoaiKhachHang>> cbLoaiKH;
    private JButton btnThem, btnXoa, btnTraCuu, btnLamMoi, btnCapNhat;
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;

    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final LoaiKhachHang_DAO loaiKhachHangDAO = new LoaiKhachHang_DAO();
    private final ArrayList<LoaiKhachHang> dsLoaiKhachHang = new ArrayList<>();

    private final Color BG = new Color(250, 246, 241);
    private final Color BROWN = new Color(98, 67, 48);
    private final Color BORDER = new Color(190, 175, 155);
    private final Color ERROR = new Color(210, 55, 55);

    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150);
    private static final Color INPUT_COLOR = new Color(30, 30, 30);
    private static final Locale VI_LOCALE = new Locale("vi", "VN");

    private final java.text.DecimalFormat moneyFormat = new java.text.DecimalFormat("#,##0");

    private final Set<Integer> highlightedRows = new HashSet<>();

    private final Set<String> hiddenCustomerIds = new HashSet<>();

    // Danh sách khách 6 tháng không hoạt động
    private final Set<String> inactiveCustomerIds = new HashSet<>();
    private boolean daThongBaoKhach6Thang = false;

    // Highlight tạm thời thôi
    private boolean showInactiveHighlight = false;
    private Timer inactiveHighlightTimer;

    // Màu highlight mới: cam đào / đỏ nhạt
    private final Color INACTIVE_BG = new Color(255, 223, 214);
    private final Color INACTIVE_FG = new Color(125, 58, 43);

    public KhachHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        ConnectDB.getInstance().connect();

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        initEvents();
        loadLoaiKhachHangTuSQL();
        loadKhachHangTuSQL();
        thongBaoKhach6ThangNeuCan();
        lamMoi();
    }

    public KhachHang_GUI() {
        this(null);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BROWN);
        panel.setBorder(new EmptyBorder(14, 10, 14, 10));

        JLabel lblTitle = new JLabel("DANH SÁCH KHÁCH HÀNG", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);

        panel.add(lblTitle, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMainContent() {
        JPanel panelContent = new JPanel(new BorderLayout(0, 18));
        panelContent.setBackground(BG);
        panelContent.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel topArea = new JPanel(new BorderLayout(18, 0));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(), BorderLayout.CENTER);
        topArea.add(buildButtonPanel(), BorderLayout.EAST);

        panelContent.add(topArea, BorderLayout.NORTH);
        panelContent.add(buildTablePanel(), BorderLayout.CENTER);

        return panelContent;
    }

    private JPanel buildFormPanel() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(255, 253, 248));
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaKH = createTextField();
        txtTenKH = createTextField();
        txtDiemTichLuy = createTextField();
        txtSDT = createTextField();

        setPlaceholder(txtMaKH, "Tự động sinh mã");
        setPlaceholder(txtTenKH, "Nguyễn Văn A");
        setPlaceholder(txtDiemTichLuy, "0");
        setPlaceholder(txtSDT, "0912345678");

        txtMaKH.setToolTipText("Mã khách hàng được hệ thống tự sinh và không được chỉnh sửa.");
        txtTenKH.setToolTipText("Tên khách hàng phải có ít nhất 2 từ và sẽ được tự động viết hoa chữ cái đầu.");
        txtDiemTichLuy.setToolTipText("Điểm tích lũy không sửa tại đây.\nĐiểm được cộng tự động dựa trên chi tiêu của khách hàng.");
        txtSDT.setToolTipText("Số điện thoại phải gồm 10 số và bắt đầu bằng số 0.\nVí dụ: 0912345678");

        cbLoaiKH = new JComboBox<>();
        styleComboBox(cbLoaiKH);
        cbLoaiKH.setToolTipText("Loại khách hàng tự động theo điểm: <50 Thường, >=50 Vàng, >=100 Kim cương.");

        addRow(panelForm, gbc, 0, "Mã KH", txtMaKH, "Điểm tích lũy", txtDiemTichLuy);
        addRow(panelForm, gbc, 1, "Tên KH", txtTenKH, "SĐT", txtSDT);
        addRow(panelForm, gbc, 2, "Loại KH", cbLoaiKH, "", new JLabel());

        return panelForm;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String label1, JComponent comp1,
                        String label2, JComponent comp2) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.12;
        p.add(createLabel(label1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        p.add(comp1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.12;
        if (label2 == null || label2.isEmpty()) {
            p.add(new JLabel(), gbc);
        } else {
            p.add(createLabel(label2), gbc);
        }

        gbc.gridx = 3;
        gbc.weightx = 0.38;
        p.add(comp2, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panelButtons = new JPanel(new GridLayout(3, 2, 10, 10));
        panelButtons.setOpaque(false);
        panelButtons.setPreferredSize(new Dimension(285, 150));

        btnThem = createButton("THÊM", new Color(114, 190, 120));
        btnXoa = createButton("XÓA", new Color(235, 125, 125));
        btnTraCuu = createButton("TRA CỨU", new Color(100, 181, 246));
        btnLamMoi = createButton("LÀM MỚI", new Color(230, 230, 230));
        btnCapNhat = createButton("CẬP NHẬT", new Color(150, 175, 245));

        panelButtons.add(btnThem);
        panelButtons.add(btnXoa);
        panelButtons.add(btnTraCuu);
        panelButtons.add(btnLamMoi);
        panelButtons.add(new JLabel());
        panelButtons.add(btnCapNhat);

        return panelButtons;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Mã khách hàng", "Họ tên", "SĐT", "Điểm tích lũy", "Tổng chi tiêu", "Loại khách hàng"};

        modelKhachHang = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(modelKhachHang);
        tblKhachHang.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tblKhachHang.setRowHeight(36);
        tblKhachHang.setShowGrid(true);
        tblKhachHang.setGridColor(new Color(220, 210, 195));
        tblKhachHang.setSelectionBackground(new Color(238, 225, 205));
        tblKhachHang.setSelectionForeground(Color.BLACK);
        tblKhachHang.setFillsViewportHeight(true);
        tblKhachHang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = tblKhachHang.getTableHeader();
        header.setPreferredSize(new Dimension(100, 38));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(208, 144, 106));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("SansSerif", Font.BOLD, 15));
                c.setHorizontalAlignment(JLabel.CENTER);
                c.setOpaque(true);
                return c;
            }
        };

        for (int i = 0; i < tblKhachHang.getColumnModel().getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                c.setBorder(new EmptyBorder(0, 8, 0, 8));

                String maKH = "";
                if (row >= 0 && row < modelKhachHang.getRowCount()) {
                    Object v = modelKhachHang.getValueAt(row, 0);
                    maKH = v == null ? "" : v.toString();
                }

                if (isSelected) {
                    c.setBackground(new Color(238, 225, 205));
                    c.setForeground(Color.BLACK);
                } else if (highlightedRows.contains(row)) {
                    c.setBackground(new Color(255, 245, 180));
                    c.setForeground(Color.BLACK);
                } else if (showInactiveHighlight && inactiveCustomerIds.contains(maKH)) {
                    c.setBackground(INACTIVE_BG);
                    c.setForeground(INACTIVE_FG);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 248, 240));
                    c.setForeground(Color.BLACK);
                }

                if (column == 1) {
                    c.setHorizontalAlignment(JLabel.LEFT);
                } else {
                    c.setHorizontalAlignment(JLabel.CENTER);
                }

                return c;
            }
        };

        for (int i = 0; i < tblKhachHang.getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setCellRenderer(bodyRenderer);
        }

        int[] widths = {130, 230, 140, 120, 150, 150};
        for (int i = 0; i < widths.length; i++) {
            tblKhachHang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        tblKhachHang.getSelectionModel().addListSelectionListener(this::onTableSelected);

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(new LineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void initEvents() {
        btnThem.addActionListener(e -> themKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
        btnCapNhat.addActionListener(e -> capNhatKhachHang());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTraCuu.addActionListener(e -> traCuu());
    }

    private void ganSuKienTuDongCapNhatLoaiTheoDiem() {
        txtDiemTichLuy.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                capNhatComboLoaiTheoDiemDangNhap();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                capNhatComboLoaiTheoDiemDangNhap();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                capNhatComboLoaiTheoDiemDangNhap();
            }
        });
    }

    private void capNhatComboLoaiTheoDiemDangNhap() {
        if (txtDiemTichLuy == null || cbLoaiKH == null || cbLoaiKH.getItemCount() == 0) {
            return;
        }

        String diemText = getInputText(txtDiemTichLuy);

        if (diemText == null || diemText.trim().isEmpty()) {
            return;
        }

        if (!diemText.matches("\\d+")) {
            return;
        }

        int diem = Integer.parseInt(diemText);
        LoaiKhachHang loaiKH = getLoaiKhachHangTheoDiem(diem);

        if (loaiKH != null) {
            chonLoaiKhachHang(loaiKH.getMaLoaiKH());
        }
    }

    private void onTableSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        int row = tblKhachHang.getSelectedRow();
        if (row == -1) {
            return;
        }

        resetFieldStyles();

        String maKH = getValue(row, 0);
        KhachHang kh = khachHangDAO.getKhachHangTheoMa(maKH);

        if (kh == null) {
            return;
        }

        setActualText(txtMaKH, kh.getMaKH());
        setActualText(txtTenKH, kh.getTenKH());
        setActualText(txtDiemTichLuy, String.valueOf(kh.getDiemTichLuy()));
        txtDiemTichLuy.setEditable(false);
        txtDiemTichLuy.setFocusable(false);
        txtDiemTichLuy.setBackground(new Color(245, 245, 245));        
        setActualText(txtSDT, kh.getSdt());

        LoaiKhachHang loaiTheoDiem = getLoaiKhachHangTheoDiem(kh.getDiemTichLuy());
        if (loaiTheoDiem != null) {
            chonLoaiKhachHang(loaiTheoDiem.getMaLoaiKH());
        } else {
            chonLoaiKhachHang(kh.getMaLoaiKH() == null ? null : kh.getMaLoaiKH().getMaLoaiKH());
        }

        moCheDoCapNhat();
    }

    private void themKhachHang() {
        resetFieldStyles();

        String tenKH = getInputText(txtTenKH);
        String sdt = getInputText(txtSDT);

        if (tenKH.isEmpty()) {
            baoLoiNhapLieu(txtTenKH, "Tên khách hàng không được để trống.");
            return;
        }

        if (!kiemTraHoTen(tenKH)) {
            baoLoiNhapLieu(txtTenKH,
                    "Tên khách hàng phải có ít nhất 2 từ, chỉ chứa chữ cái và khoảng trắng.\n"
                            + "Ví dụ: Nguyễn Văn A.");
            return;
        }

        tenKH = chuanHoaHoTen(tenKH);

        if (!kiemTraSoDienThoai(sdt)) {
            baoLoiNhapLieu(txtSDT,
                    "Số điện thoại không hợp lệ.\n"
                            + "Vui lòng nhập 10 số và bắt đầu bằng số 0, ví dụ: 0912345678.");
            return;
        }

        if (khachHangDAO.getKhachHangTheoSDT(sdt) != null) {
            baoLoiNhapLieu(txtSDT, "Số điện thoại này đã tồn tại trong SQL.");
            return;
        }

        LoaiKhachHang loaiKHMacDinh = getLoaiKhachHangThuong();
        if (loaiKHMacDinh == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy loại khách hàng 'Thường' trong dữ liệu.");
            return;
        }

        KhachHang kh = new KhachHang(null, tenKH, sdt, loaiKHMacDinh, 0);
        boolean ok = khachHangDAO.themKhachHangKhongCanMa(kh);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            daThongBaoKhach6Thang = false;
            loadKhachHangTuSQL();
            thongBaoKhach6ThangNeuCan();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Thêm khách hàng thất bại.\nKiểm tra lại kết nối SQL hoặc dữ liệu nhập.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        int row = tblKhachHang.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để xóa!");
            return;
        }

        // Lấy thông tin từ bảng
        String maKH = getValue(row, 0);
        String tenKH = getValue(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn XÓA VĨNH VIỄN khách hàng này khỏi cơ sở dữ liệu?\n" 
                + "Mã: " + maKH + " - Tên: " + tenKH,
                "Xác nhận xóa vĩnh viễn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // THỰC HIỆN XÓA TRONG SQL
            boolean result = khachHangDAO.xoaKhachHang(maKH);

            if (result) {
                JOptionPane.showMessageDialog(this, "Đã xóa khách hàng khỏi hệ thống thành công!");
                // Cập nhật lại giao diện ngay lập tức
                loadKhachHangTuSQL(); 
                lamMoi(); 
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể xóa khách hàng này!\nLý do: Khách hàng đã có lịch sử giao dịch (Hóa đơn) trong hệ thống.", 
                    "Lỗi hệ thống", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void capNhatKhachHang() {
        int row = tblKhachHang.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng từ bảng để cập nhật!");
            return;
        }

        resetFieldStyles();

        String maKH = getValue(row, 0);
        String tenKH = getInputText(txtTenKH);
        String sdt = getInputText(txtSDT);

        if (tenKH.isEmpty()) {
            baoLoiNhapLieu(txtTenKH, "Tên khách hàng không được để trống.");
            return;
        }

        if (!kiemTraHoTen(tenKH)) {
            baoLoiNhapLieu(txtTenKH,
                    "Tên khách hàng phải có ít nhất 2 từ, chỉ chứa chữ cái và khoảng trắng.\n"
                            + "Ví dụ: Nguyễn Văn A.");
            return;
        }

        tenKH = chuanHoaHoTen(tenKH);

        if (!kiemTraSoDienThoai(sdt)) {
            baoLoiNhapLieu(txtSDT,
                    "Số điện thoại không hợp lệ.\n"
                            + "Vui lòng nhập 10 số và bắt đầu bằng số 0, ví dụ: 0912345678.");
            return;
        }

        KhachHang khTheoSDT = khachHangDAO.getKhachHangTheoSDT(sdt);
        if (khTheoSDT != null && !maKH.equalsIgnoreCase(khTheoSDT.getMaKH())) {
            baoLoiNhapLieu(txtSDT, "Số điện thoại này đã thuộc về khách hàng khác trong SQL.");
            return;
        }

        KhachHang khCu = khachHangDAO.getKhachHangTheoMa(maKH);
        if (khCu == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy khách hàng cần cập nhật trong SQL.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Chỉ cho sửa tên và số điện thoại.
        // Điểm tích lũy không sửa ở giao diện này, vì điểm được cộng dựa trên chi tiêu/hóa đơn.
        int diemHienTai = khCu.getDiemTichLuy();
        LoaiKhachHang loaiKH = getLoaiKhachHangTheoDiem(diemHienTai);

        if (loaiKH == null) {
            loaiKH = khCu.getMaLoaiKH();
        }

        if (loaiKH == null) {
            loaiKH = getLoaiKhachHangThuong();
        }

        chonLoaiKhachHang(loaiKH == null ? null : loaiKH.getMaLoaiKH());

        KhachHang kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diemHienTai);

        if (khachHangDAO.capNhatKhachHang(kh)) {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật khách hàng thành công!\n"
                            + "Chỉ thay đổi tên và số điện thoại.\n"
                            + "Điểm tích lũy được giữ nguyên: " + diemHienTai);
            daThongBaoKhach6Thang = false;
            loadKhachHangTuSQL();
            thongBaoKhach6ThangNeuCan();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thất bại.\nKiểm tra lại kết nối SQL hoặc dữ liệu nhập.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "Nhập từ khóa tìm kiếm:",
                "Tra cứu khách hàng",
                JOptionPane.PLAIN_MESSAGE
        );

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String kw = keyword.trim().toLowerCase(VI_LOCALE);
        highlightedRows.clear();
        tblKhachHang.clearSelection();

        int firstMatch = -1;

        for (int r = 0; r < modelKhachHang.getRowCount(); r++) {
            boolean matched = false;

            for (int c = 0; c < modelKhachHang.getColumnCount(); c++) {
                Object value = modelKhachHang.getValueAt(r, c);
                if (value != null && value.toString().toLowerCase(VI_LOCALE).contains(kw)) {
                    matched = true;
                    break;
                }
            }

            if (matched) {
                highlightedRows.add(r);
                if (firstMatch == -1) {
                    firstMatch = r;
                }
            }
        }

        tblKhachHang.repaint();

        if (firstMatch != -1) {
            tblKhachHang.setRowSelectionInterval(firstMatch, firstMatch);
            tblKhachHang.scrollRectToVisible(tblKhachHang.getCellRect(firstMatch, 0, true));
            JOptionPane.showMessageDialog(this, "Tìm thấy " + highlightedRows.size() + " kết quả phù hợp.");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng phù hợp!");
        }
    }

    private void lamMoi() {
        resetFieldStyles();
        highlightedRows.clear();
        tblKhachHang.repaint();

        showPlaceholder(txtMaKH);
        showPlaceholder(txtTenKH);
        setActualText(txtDiemTichLuy, "0");
        showPlaceholder(txtSDT);
        chonLoaiKhachHangThuong();

        tblKhachHang.clearSelection();
        khoaCacTruongMacDinh();
    }

    private void khoaCacTruongMacDinh() {
        txtMaKH.setEditable(false);
        txtMaKH.setFocusable(false);
        txtMaKH.setBackground(new Color(245, 245, 245));

        txtTenKH.setEditable(true);
        txtTenKH.setFocusable(true);
        txtTenKH.setBackground(Color.WHITE);

        txtSDT.setEditable(true);
        txtSDT.setFocusable(true);
        txtSDT.setBackground(Color.WHITE);

        txtDiemTichLuy.setEditable(false);
        txtDiemTichLuy.setFocusable(false);
        txtDiemTichLuy.setBackground(new Color(245, 245, 245));

        cbLoaiKH.setEnabled(false);
        cbLoaiKH.setBackground(new Color(245, 245, 245));
    }

    private void moCheDoCapNhat() {
        txtMaKH.setEditable(false);
        txtMaKH.setFocusable(false);
        txtMaKH.setBackground(new Color(245, 245, 245));

        txtTenKH.setEditable(true);
        txtTenKH.setFocusable(true);
        txtTenKH.setBackground(Color.WHITE);

        txtSDT.setEditable(true);
        txtSDT.setFocusable(true);
        txtSDT.setBackground(Color.WHITE);

        txtDiemTichLuy.setEditable(false);
        txtDiemTichLuy.setFocusable(false);
        txtDiemTichLuy.setBackground(new Color(245, 245, 245));

        // Không cho sửa điểm tích lũy và loại khách hàng thủ công.
        // Điểm tích lũy được cộng dựa trên chi tiêu/hóa đơn.
        // Loại khách hàng chỉ phụ thuộc vào điểm tích lũy.
        cbLoaiKH.setEnabled(false);
        cbLoaiKH.setBackground(new Color(245, 245, 245));
    }

    private void loadLoaiKhachHangTuSQL() {
        cbLoaiKH.removeAllItems();
        dsLoaiKhachHang.clear();

        for (Object obj : loaiKhachHangDAO.getAllLoaiKhachHang()) {
            if (obj instanceof LoaiKhachHang) {
                LoaiKhachHang lkh = (LoaiKhachHang) obj;
                dsLoaiKhachHang.add(lkh);
                cbLoaiKH.addItem(new ComboItem<>(lkh.getTenLoaiKH(), lkh));
            }
        }

        if (cbLoaiKH.getItemCount() == 0) {
            LoaiKhachHang thuong = new LoaiKhachHang("LKH01", "Thường");
            LoaiKhachHang vang = new LoaiKhachHang("LKH02", "Vàng");
            LoaiKhachHang kimCuong = new LoaiKhachHang("LKH03", "Kim cương");

            cbLoaiKH.addItem(new ComboItem<>(thuong.getTenLoaiKH(), thuong));
            cbLoaiKH.addItem(new ComboItem<>(vang.getTenLoaiKH(), vang));
            cbLoaiKH.addItem(new ComboItem<>(kimCuong.getTenLoaiKH(), kimCuong));
        }

        chonLoaiKhachHangThuong();
    }

    private void loadKhachHangTuSQL() {
        modelKhachHang.setRowCount(0);
        capNhatDanhSachKhach6Thang();

        for (Object obj : khachHangDAO.getAllKhachHang()) {
            if (!(obj instanceof KhachHang)) {
                continue;
            }

            KhachHang kh = (KhachHang) obj;

            if (hiddenCustomerIds.contains(kh.getMaKH())) {
                continue;
            }

            double tongChiTieu = khachHangDAO.layTongGiaoDichTheoMaKH(kh.getMaKH());

            LoaiKhachHang loaiTheoDiem = getLoaiKhachHangTheoDiem(kh.getDiemTichLuy());
            String tenLoai;

            if (loaiTheoDiem != null) {
                tenLoai = loaiTheoDiem.getTenLoaiKH();
            } else {
                tenLoai = kh.getMaLoaiKH() == null ? "" : kh.getMaLoaiKH().getTenLoaiKH();
            }

            modelKhachHang.addRow(new Object[]{
                    kh.getMaKH(),
                    kh.getTenKH(),
                    kh.getSdt(),
                    kh.getDiemTichLuy(),
                    formatMoney(tongChiTieu),
                    tenLoai
            });
        }
    }

    private void capNhatDanhSachKhach6Thang() {
        inactiveCustomerIds.clear();

        ArrayList<KhachHang> dsKhach6Thang = khachHangDAO.getKhachHangKhongHoatDong6Thang();
        for (KhachHang kh : dsKhach6Thang) {
            inactiveCustomerIds.add(kh.getMaKH());
        }
    }

    private void thongBaoKhach6ThangNeuCan() {
        if (daThongBaoKhach6Thang || inactiveCustomerIds.isEmpty()) {
            return;
        }

        StringBuilder ds = new StringBuilder();
        int dem = 0;

        for (Object obj : khachHangDAO.getKhachHangKhongHoatDong6Thang()) {
            if (obj instanceof KhachHang) {
                KhachHang kh = (KhachHang) obj;
                ds.append("- ").append(kh.getMaKH()).append(" - ").append(kh.getTenKH()).append("\n");
                dem++;
                if (dem == 5) {
                    break;
                }
            }
        }

        // Bật highlight tạm thời
        batHighlightTamThoiKhachKhongHoatDong();

        String noiDung = "Có " + inactiveCustomerIds.size() + " khách hàng đã hơn 6 tháng không phát sinh hoạt động.\n"
                + "Quản lý vui lòng kiểm tra và xử lý.\n\n"
                + "Một số khách hàng:\n" + ds;

        JOptionPane.showMessageDialog(
                this,
                noiDung,
                "Thông báo khách hàng 6 tháng không hoạt động",
                JOptionPane.WARNING_MESSAGE
        );

        daThongBaoKhach6Thang = true;
    }

    private void batHighlightTamThoiKhachKhongHoatDong() {
        showInactiveHighlight = true;
        tblKhachHang.repaint();

        if (inactiveHighlightTimer != null && inactiveHighlightTimer.isRunning()) {
            inactiveHighlightTimer.stop();
        }

        inactiveHighlightTimer = new Timer(9000, e -> {
            showInactiveHighlight = false;
            tblKhachHang.repaint();
        });
        inactiveHighlightTimer.setRepeats(false);
        inactiveHighlightTimer.start();
    }

    private String getValue(int row, int col) {
        Object value = modelKhachHang.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        lbl.setForeground(new Color(55, 45, 35));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txt.setPreferredSize(new Dimension(260, 38));
        txt.setMinimumSize(new Dimension(180, 38));
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return txt;
    }

    private void styleComboBox(JComboBox<ComboItem<LoaiKhachHang>> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cb.setPreferredSize(new Dimension(260, 38));
        cb.setMinimumSize(new Dimension(180, 38));
        cb.setBackground(Color.WHITE);
        cb.setFocusable(false);
        cb.setBorder(new LineBorder(BORDER, 1));
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(150, 140, 125), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setPlaceholder(JTextField textField, String placeholder) {
        textField.putClientProperty("placeholder", placeholder);
        showPlaceholder(textField);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                resetFieldStyle(textField);

                if (isPlaceholder(textField) && textField.isEditable()) {
                    textField.setText("");
                    textField.setForeground(INPUT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.isEditable() && textField.getText().trim().isEmpty()) {
                    showPlaceholder(textField);
                }
            }
        });
    }

    private void showPlaceholder(JTextField textField) {
        Object placeholder = textField.getClientProperty("placeholder");
        if (placeholder == null) {
            return;
        }

        textField.setText(placeholder.toString());
        textField.setForeground(PLACEHOLDER_COLOR);
        resetFieldStyle(textField);
    }

    private boolean isPlaceholder(JTextField textField) {
        Object placeholder = textField.getClientProperty("placeholder");
        if (placeholder == null) {
            return false;
        }

        return textField.getForeground().equals(PLACEHOLDER_COLOR)
                && textField.getText().equals(placeholder.toString());
    }

    private String getInputText(JTextField textField) {
        if (isPlaceholder(textField)) {
            return "";
        }
        return textField.getText().trim();
    }

    private void setActualText(JTextField textField, String value) {
        textField.setText(value == null ? "" : value);
        textField.setForeground(INPUT_COLOR);
        resetFieldStyle(textField);
    }

    private boolean kiemTraHoTen(String hoTen) {
        if (hoTen == null) {
            return false;
        }

        String cleaned = hoTen.replaceAll("[^\\p{L}\\s]", "").trim().replaceAll("\\s+", " ");

        if (cleaned.isEmpty()) {
            return false;
        }

        if (!cleaned.matches("[\\p{L}\\s]+")) {
            return false;
        }

        String[] words = cleaned.split(" ");
        return words.length >= 2;
    }

    private boolean kiemTraDiemTichLuy(String diem) {
        return diem != null && diem.matches("\\d+");
    }

    private boolean kiemTraSoDienThoai(String sdt) {
        return sdt != null && sdt.matches("0\\d{9}");
    }

    private String chuanHoaHoTen(String hoTen) {
        if (hoTen == null) {
            return "";
        }

        hoTen = hoTen.replaceAll("[^\\p{L}\\s]", "");
        hoTen = hoTen.trim().replaceAll("\\s+", " ");

        if (hoTen.isEmpty()) {
            return "";
        }

        String[] words = hoTen.toLowerCase(VI_LOCALE).split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            result.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                result.append(word.substring(1));
            }
            result.append(" ");
        }

        return result.toString().trim();
    }

    private LoaiKhachHang getLoaiKhachHangDangChon() {
        Object selected = cbLoaiKH.getSelectedItem();
        if (selected instanceof ComboItem) {
            @SuppressWarnings("unchecked")
            ComboItem<LoaiKhachHang> item = (ComboItem<LoaiKhachHang>) selected;
            return item.getValue();
        }
        return null;
    }

    private LoaiKhachHang getLoaiKhachHangTheoDiem(int diem) {
        String tenCanTim;
        String maCanTim;

        if (diem >= 100) {
            tenCanTim = "kim cuong";
            maCanTim = "LKH03";
        } else if (diem >= 50) {
            tenCanTim = "vang";
            maCanTim = "LKH02";
        } else {
            tenCanTim = "thuong";
            maCanTim = "LKH01";
        }

        // Ưu tiên tìm theo tên loại để tránh lệ thuộc mã.
        for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(i);
            if (item == null || item.getValue() == null) {
                continue;
            }

            String tenLoai = chuanHoaTenLoaiKH(item.getValue().getTenLoaiKH());
            if (tenLoai.contains(tenCanTim)) {
                return item.getValue();
            }
        }

        // Nếu tên trong SQL khác một chút thì fallback theo mã mặc định.
        for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(i);
            if (item != null
                    && item.getValue() != null
                    && maCanTim.equalsIgnoreCase(item.getValue().getMaLoaiKH())) {
                return item.getValue();
            }
        }

        return null;
    }

    private String chuanHoaTenLoaiKH(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase(VI_LOCALE).trim();
    }

    private LoaiKhachHang getLoaiKhachHangThuong() {
        for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(i);
            if (item != null
                    && item.getValue() != null
                    && chuanHoaTenLoaiKH(item.getValue().getTenLoaiKH()).contains("thuong")) {
                return item.getValue();
            }
        }

        for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(i);
            if (item != null
                    && item.getValue() != null
                    && "LKH01".equalsIgnoreCase(item.getValue().getMaLoaiKH())) {
                return item.getValue();
            }
        }

        if (cbLoaiKH.getItemCount() > 0) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(0);
            return item == null ? null : item.getValue();
        }

        return null;
    }

    private void chonLoaiKhachHang(String maLoaiKH) {
        if (maLoaiKH == null) {
            return;
        }

        for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(i);
            if (item != null
                    && item.getValue() != null
                    && maLoaiKH.equalsIgnoreCase(item.getValue().getMaLoaiKH())) {
                cbLoaiKH.setSelectedIndex(i);
                return;
            }
        }
    }

    private void chonLoaiKhachHangThuong() {
        LoaiKhachHang thuong = getLoaiKhachHangThuong();
        if (thuong != null) {
            chonLoaiKhachHang(thuong.getMaLoaiKH());
            return;
        }

        if (cbLoaiKH.getItemCount() > 0) {
            cbLoaiKH.setSelectedIndex(0);
        }
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value) + " VNĐ";
    }

    private void baoLoiNhapLieu(JTextField field, String message) {
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ERROR, 2),
                new EmptyBorder(4, 10, 4, 10)
        ));
        field.setToolTipText(message);
        field.requestFocus();

        JOptionPane.showMessageDialog(
                this,
                message,
                "Dữ liệu nhập chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void resetFieldStyles() {
        resetFieldStyle(txtMaKH);
        resetFieldStyle(txtTenKH);
        resetFieldStyle(txtDiemTichLuy);
        resetFieldStyle(txtSDT);

        txtMaKH.setToolTipText("Mã khách hàng được hệ thống tự sinh và không được chỉnh sửa.");
        txtTenKH.setToolTipText("Tên khách hàng phải có ít nhất 2 từ và sẽ được tự động viết hoa chữ cái đầu.");
        txtDiemTichLuy.setToolTipText("Điểm tích lũy không sửa tại đây.\nĐiểm được cộng tự động dựa trên chi tiêu của khách hàng.");
        txtSDT.setToolTipText("Số điện thoại phải gồm 10 số và bắt đầu bằng số 0.\nVí dụ: 0912345678");
    }

    private void resetFieldStyle(JTextField field) {
        if (field == null) {
            return;
        }

        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
    }

    private static class ComboItem<T> {
        private final String label;
        private final T value;

        ComboItem(String label, T value) {
            this.label = label;
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
