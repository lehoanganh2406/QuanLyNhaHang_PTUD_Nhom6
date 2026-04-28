package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.*;
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
    private static final long serialVersionUID = 1L;

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

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

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

        setPlaceholder(txtMaKH, "Bỏ trống để SQL tự sinh, VD: KH00001");
        setPlaceholder(txtTenKH, "Nguyễn Văn A");
        setPlaceholder(txtDiemTichLuy, "0");
        setPlaceholder(txtSDT, "0912345678");

        txtMaKH.setToolTipText("Nếu nhập mã thủ công, mã phải đúng dạng KH + 5 số, ví dụ: KH00001. Có thể bỏ trống để SQL tự sinh.");
        txtTenKH.setToolTipText("Họ tên sẽ được chuẩn hóa viết hoa chữ cái đầu. Ví dụ: Nguyễn Văn A");
        txtDiemTichLuy.setToolTipText("Nếu không nhập, điểm tích lũy mặc định là 0");
        txtSDT.setToolTipText("Số điện thoại phải gồm 10 số và bắt đầu bằng số 0. Ví dụ: 0912345678");

        cbLoaiKH = new JComboBox<>();
        styleComboBox(cbLoaiKH);

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
        String[] columns = {
                "Mã khách hàng", "Họ tên", "SĐT",
                "Điểm tích lũy", "Tổng chi tiêu", "Loại khách hàng"
        };

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

                if (!isSelected) {
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

    private void onTableSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        int row = tblKhachHang.getSelectedRow();
        if (row == -1) return;

        resetFieldStyles();

        String maKH = getValue(row, 0);
        KhachHang kh = khachHangDAO.getKhachHangTheoMa(maKH);
        if (kh == null) {
            return;
        }

        setActualText(txtMaKH, kh.getMaKH());
        setActualText(txtTenKH, kh.getTenKH());
        setActualText(txtDiemTichLuy, String.valueOf(kh.getDiemTichLuy()));
        setActualText(txtSDT, kh.getSdt());
        chonLoaiKhachHang(kh.getMaLoaiKH() == null ? null : kh.getMaLoaiKH().getMaLoaiKH());
    }

    private void themKhachHang() {
        resetFieldStyles();

        String maKH = getInputText(txtMaKH).toUpperCase();
        String tenKH = getInputText(txtTenKH);
        String diemText = getInputText(txtDiemTichLuy);
        String sdt = getInputText(txtSDT);
        LoaiKhachHang loaiKH = getLoaiKhachHangDangChon();

        if (!maKH.isEmpty() && !kiemTraMaKH(maKH)) {
            baoLoiNhapLieu(txtMaKH, "Mã khách hàng phải đúng dạng KH + 5 số theo SQL, ví dụ: KH00001. Có thể bỏ trống để hệ thống tự sinh.");
            return;
        }

        if (!maKH.isEmpty() && khachHangDAO.getKhachHangTheoMa(maKH) != null) {
            baoLoiNhapLieu(txtMaKH, "Mã khách hàng này đã tồn tại trong SQL. Vui lòng nhập mã khác hoặc bỏ trống để tự sinh.");
            return;
        }

        if (tenKH.isEmpty()) {
            baoLoiNhapLieu(txtTenKH, "Tên khách hàng không được để trống.");
            return;
        }

        if (!kiemTraHoTen(tenKH)) {
            baoLoiNhapLieu(txtTenKH, "Họ tên chỉ được chứa chữ cái và khoảng trắng. Ví dụ đúng: Nguyễn Văn A.");
            return;
        }

        tenKH = chuanHoaHoTen(tenKH);

        if (diemText.isEmpty()) {
            diemText = "0";
        }

        if (!kiemTraDiemTichLuy(diemText)) {
            baoLoiNhapLieu(txtDiemTichLuy, "Điểm tích lũy phải là số nguyên không âm. Nếu không nhập, hệ thống mặc định là 0.");
            return;
        }

        int diem = Integer.parseInt(diemText);

        if (!kiemTraSoDienThoai(sdt)) {
            baoLoiNhapLieu(txtSDT, "Số điện thoại không hợp lệ. Vui lòng nhập 10 số và bắt đầu bằng số 0, ví dụ: 0912345678.");
            return;
        }

        if (khachHangDAO.getKhachHangTheoSDT(sdt) != null) {
            baoLoiNhapLieu(txtSDT, "Số điện thoại này đã tồn tại trong SQL.");
            return;
        }

        if (loaiKH == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khách hàng hợp lệ từ SQL.");
            return;
        }

        boolean ok;
        if (maKH.isEmpty()) {
            KhachHang kh = new KhachHang(null, tenKH, sdt, loaiKH, diem);
            ok = khachHangDAO.themKhachHangKhongCanMa(kh);
        } else {
            KhachHang kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diem);
            ok = khachHangDAO.themKhachHang(kh);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            loadKhachHangTuSQL();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại. Kiểm tra lại kết nối SQL hoặc dữ liệu nhập.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        int row = tblKhachHang.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để xóa!");
            return;
        }

        String maKH = getValue(row, 0);
        double tongChiTieu = khachHangDAO.layTongGiaoDichTheoMaKH(maKH);
        if (tongChiTieu > 0) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa khách hàng này vì đã có hóa đơn đã thanh toán.\n" +
                    "Để đảm bảo an toàn dữ liệu, chỉ nên cập nhật thông tin thay vì xóa.",
                    "Không thể xóa", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khách hàng " + maKH + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDAO.xoaKhachHang(maKH)) {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
                loadKhachHangTuSQL();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa khách hàng thất bại. Khách hàng có thể đang được tham chiếu bởi hóa đơn.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        String diemText = getInputText(txtDiemTichLuy);
        String sdt = getInputText(txtSDT);
        LoaiKhachHang loaiKH = getLoaiKhachHangDangChon();

        if (tenKH.isEmpty()) {
            baoLoiNhapLieu(txtTenKH, "Tên khách hàng không được để trống.");
            return;
        }

        if (!kiemTraHoTen(tenKH)) {
            baoLoiNhapLieu(txtTenKH, "Họ tên chỉ được chứa chữ cái và khoảng trắng. Ví dụ đúng: Nguyễn Văn A.");
            return;
        }

        tenKH = chuanHoaHoTen(tenKH);

        if (diemText.isEmpty()) {
            diemText = "0";
        }

        if (!kiemTraDiemTichLuy(diemText)) {
            baoLoiNhapLieu(txtDiemTichLuy, "Điểm tích lũy phải là số nguyên không âm. Nếu không nhập, hệ thống mặc định là 0.");
            return;
        }

        int diem = Integer.parseInt(diemText);

        if (!kiemTraSoDienThoai(sdt)) {
            baoLoiNhapLieu(txtSDT, "Số điện thoại không hợp lệ. Vui lòng nhập 10 số và bắt đầu bằng số 0, ví dụ: 0912345678.");
            return;
        }

        KhachHang khTheoSDT = khachHangDAO.getKhachHangTheoSDT(sdt);
        if (khTheoSDT != null && !maKH.equalsIgnoreCase(khTheoSDT.getMaKH())) {
            baoLoiNhapLieu(txtSDT, "Số điện thoại này đã thuộc về khách hàng khác trong SQL.");
            return;
        }

        if (loaiKH == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khách hàng hợp lệ từ SQL.");
            return;
        }

        KhachHang kh = new KhachHang(maKH, tenKH, sdt, loaiKH, diem);
        if (khachHangDAO.capNhatKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
            loadKhachHangTuSQL();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Kiểm tra lại kết nối SQL hoặc dữ liệu nhập.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void traCuu() {
        String keyword = JOptionPane.showInputDialog(
                this,
                "Nhập từ khóa tìm kiếm:",
                "Tra cứu khách hàng",
                JOptionPane.PLAIN_MESSAGE
        );

        if (keyword == null || keyword.trim().isEmpty()) return;

        String kw = keyword.trim().toLowerCase(VI_LOCALE);

        for (int r = 0; r < modelKhachHang.getRowCount(); r++) {
            for (int c = 0; c < modelKhachHang.getColumnCount(); c++) {
                Object value = modelKhachHang.getValueAt(r, c);
                if (value != null && value.toString().toLowerCase(VI_LOCALE).contains(kw)) {
                    tblKhachHang.setRowSelectionInterval(r, r);
                    tblKhachHang.scrollRectToVisible(tblKhachHang.getCellRect(r, 0, true));
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng phù hợp!");
    }

    private void lamMoi() {
        resetFieldStyles();

        showPlaceholder(txtMaKH);
        showPlaceholder(txtTenKH);
        showPlaceholder(txtDiemTichLuy);
        showPlaceholder(txtSDT);

        if (cbLoaiKH.getItemCount() > 0) {
            cbLoaiKH.setSelectedIndex(0);
        }
        tblKhachHang.clearSelection();
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
    }

    private void loadKhachHangTuSQL() {
        modelKhachHang.setRowCount(0);

        for (Object obj : khachHangDAO.getAllKhachHang()) {
            if (!(obj instanceof KhachHang)) {
                continue;
            }

            KhachHang kh = (KhachHang) obj;
            double tongChiTieu = khachHangDAO.layTongGiaoDichTheoMaKH(kh.getMaKH());
            String tenLoai = kh.getMaLoaiKH() == null ? "" : kh.getMaLoaiKH().getTenLoaiKH();

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

                if (isPlaceholder(textField)) {
                    textField.setText("");
                    textField.setForeground(INPUT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    showPlaceholder(textField);
                }
            }
        });
    }

    private void showPlaceholder(JTextField textField) {
        Object placeholder = textField.getClientProperty("placeholder");
        if (placeholder == null) return;

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

    private boolean kiemTraMaKH(String maKH) {
        return maKH != null && maKH.matches("KH\\d{5}");
    }

    private boolean kiemTraHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return false;
        }

        return hoTen.trim().matches("[\\p{L}\\s]+");
    }

    private boolean kiemTraDiemTichLuy(String diem) {
        if (diem == null || diem.trim().isEmpty()) {
            return true;
        }

        return diem.trim().matches("\\d+");
    }

    private boolean kiemTraSoDienThoai(String sdt) {
        return sdt != null && sdt.matches("0\\d{9}");
    }

    private String chuanHoaHoTen(String hoTen) {
        if (hoTen == null) {
            return "";
        }

        String[] words = hoTen.trim().toLowerCase(VI_LOCALE).split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            String first = word.substring(0, 1).toUpperCase(VI_LOCALE);
            String rest = word.length() > 1 ? word.substring(1) : "";

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(first).append(rest);
        }

        return result.toString();
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

    private void chonLoaiKhachHang(String maLoaiKH) {
        if (maLoaiKH == null) {
            return;
        }

        for (int i = 0; i < cbLoaiKH.getItemCount(); i++) {
            ComboItem<LoaiKhachHang> item = cbLoaiKH.getItemAt(i);
            if (item != null && item.getValue() != null
                    && maLoaiKH.equalsIgnoreCase(item.getValue().getMaLoaiKH())) {
                cbLoaiKH.setSelectedIndex(i);
                return;
            }
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

        txtMaKH.setToolTipText("Nếu nhập mã thủ công, mã phải đúng dạng KH + 5 số, ví dụ: KH00001. Có thể bỏ trống để SQL tự sinh.");
        txtTenKH.setToolTipText("Họ tên sẽ được chuẩn hóa viết hoa chữ cái đầu. Ví dụ: Nguyễn Văn A");
        txtDiemTichLuy.setToolTipText("Nếu không nhập, điểm tích lũy mặc định là 0");
        txtSDT.setToolTipText("Số điện thoại phải gồm 10 số và bắt đầu bằng số 0. Ví dụ: 0912345678");
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
