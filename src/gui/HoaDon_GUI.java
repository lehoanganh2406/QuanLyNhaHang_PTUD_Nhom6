package gui;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.HoaDon_DAO;
import digLog.HoaDonChiTiet_DigLog;
import dao.ChiTietHoaDon_DAO;
import entity.ChiTietHoaDon;
import java.util.List;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class HoaDon_GUI extends JFrame {


    private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(250, 248, 242);
    private static final Color CLR_PANEL_BG   = new Color(242, 238, 225);
    private static final Color CLR_TABLE_HDR  = new Color(222, 214, 196);
    private static final Color CLR_BORDER     = new Color(175, 160, 135);

    private static final Color CLR_BTN_CHITIET = new Color(100, 181, 246);
    private static final Color CLR_BTN_TRACUU = new Color(244, 164, 96);
    private static final Color CLR_BTN_LAMMOI  = new Color(102, 187, 106);
    private static final Color CLR_BTN_LOC     = new Color(250, 224, 187);
    private static final Color CLR_BTN_CAPNHAT = new Color(255, 213, 79);
    
    private static final Dimension FIELD_SIZE = new Dimension(260, 38);
    private static final int FIELD_ARC = 12;
    private static final String PH_NV = "Vui lòng chọn tên nhân viên";
    private static final String PH_KM = "Vui lòng chọn khuyến mãi";
    private static final String PH_TT = "Vui lòng chọn trạng thái";
    private static final String PH_PTTT = "Vui lòng chọn phương thức";
    private static final String PH_HTPV = "Vui lòng chọn hình thức";

    private JTextField txtTenKhach, txtMaHoaDon, txtBan, txtTongTien, txtSDT, txtLyDoHuy;
    private JDateChooser dtThoiGianVao, dtThoiGianRa;
    private JComboBox<String> txtKhuyenMai;
    private JComboBox<String> cbTrangThai;
    private JComboBox<String> cbNhanVien;
    private JComboBox<String> cboPhuongThucThanhToan;
    private JComboBox<String> cboHinhThucPhucVu;

    private JTable table;
    private DefaultTableModel tableModel;

    private HoaDon_DAO hd_dao = new HoaDon_DAO();
    private ChiTietHoaDon_DAO cthdDAO = new ChiTietHoaDon_DAO();

    private JButton btnChiTiet, btnTraCuu, btnLamMoi, btnLoc, btnCapNhat;


    private Connection con;
    private TaiKhoan taiKhoanDangNhap;

    public HoaDon_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);


        con = ConnectDB.getConnection();
        loadData();
//        disableFormFields();
    }

    public HoaDon_GUI() {
        this(null);
    }

    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));

        JLabel lbl = new JLabel("DANH SÁCH HÓA ĐƠN", SwingConstants.CENTER);


        lbl.setFont(new Font("SansSerif", Font.BOLD, 34));

        lbl.setForeground(CLR_HEADER_FG);

        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel buildCenterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        pnl.add(buildFormPanel(), BorderLayout.NORTH);
        pnl.add(buildTablePanel(), BorderLayout.CENTER);

        return pnl;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(14, 0));
        outer.setBackground(CLR_FORM_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTenKhach = createTextField();
        txtMaHoaDon = createTextField();
        txtMaHoaDon.setEnabled(false);
        txtMaHoaDon.setDisabledTextColor(new Color(60, 60, 60));
        addRow(pnlFields, gbc, 0, "Tên khách", txtTenKhach, "Mã hóa đơn", txtMaHoaDon);

        cbNhanVien = createComboBox(new String[]{PH_NV});
        loadNhanVienToCombo();

        txtBan = createTextField();
        addRow(pnlFields, gbc, 1, "Tên nhân viên", cbNhanVien, "Bàn", txtBan);

        txtTongTien = createTextField();
        txtSDT = createTextField();
        addRow(pnlFields, gbc, 2, "Tổng tiền", txtTongTien, "SĐT", txtSDT);

        dtThoiGianVao = createDateChooser();
        dtThoiGianRa = createDateChooser();
        addRow(pnlFields, gbc, 3, "Thời gian vào", dtThoiGianVao, "Thời gian ra", dtThoiGianRa);

        cbTrangThai = createComboBox(new String[]{
                PH_TT, "Đã thanh toán", "Hủy", "Chưa thanh toán"
        });

        txtKhuyenMai = createComboBox(new String[]{PH_KM});
        loadKhuyenMaiToCombo();

        addRow(pnlFields, gbc, 4, "Trạng thái", cbTrangThai, "Khuyến mãi", txtKhuyenMai);

        txtLyDoHuy = createTextField();
        txtLyDoHuy.setEnabled(false);

        cboPhuongThucThanhToan = createComboBox(new String[]{
                PH_PTTT, "Tiền mặt", "Chuyển khoản", "Visa"
        });

        addRow(pnlFields, gbc, 5, "Lý do hủy", txtLyDoHuy, "Phương thức TT", cboPhuongThucThanhToan);

        cboHinhThucPhucVu = createComboBox(new String[]{
                PH_HTPV, "Tại bàn", "Mang về"
        });

        addRow(pnlFields, gbc, 6, "Hình thức phục vụ", cboHinhThucPhucVu, "", new JLabel());
        cbTrangThai.addActionListener(e -> {
            xuLyTrangThai();
            updateLyDoHuyStatus(); 
        });

        outer.add(pnlFields, BorderLayout.CENTER);
        outer.add(buildRightButtons(), BorderLayout.EAST);

        return outer;
    }


    private JPanel buildRightButtons() {
        btnChiTiet = createFuncButton("CHI TIẾT HÓA ĐƠN", CLR_BTN_CHITIET, "img/chitiethoadon.png");
        btnTraCuu = createFuncButton("TRA CỨU", CLR_BTN_TRACUU, "img/mm_tracuu.png");
        btnLamMoi = createFuncButton("LÀM MỚI", CLR_BTN_LAMMOI, "img/mn_xuly.png");
        btnLoc = createFuncButton("LỌC", CLR_BTN_LOC, "img/cn_loc.png");
        btnCapNhat = createFuncButton("CẬP NHẬT", CLR_BTN_CAPNHAT, "img/cn_capnhat.png");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);


        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        wrapper.setPreferredSize(new Dimension(310, 190));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;


        gbc.weighty = 1;


        gbc.gridx = 0;
        gbc.gridwidth = 1;

        gbc.gridy = 0;

        gbc.gridwidth = 2;
        wrapper.add(btnChiTiet, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;

        gbc.gridx = 0;

        wrapper.add(btnTraCuu, gbc);

        gbc.gridy = 2;
        wrapper.add(btnLamMoi, gbc);

        gbc.gridy = 3;
        wrapper.add(btnLoc, gbc);

        gbc.gridy = 4;
        wrapper.add(btnCapNhat, gbc);

        btnLamMoi.addActionListener(e -> lamMoi());
        btnCapNhat.addActionListener(e -> capNhatHoaDon());
        btnTraCuu.addActionListener(e -> traCuu());
        btnLoc.addActionListener(e -> locHoaDon());

        btnChiTiet.addActionListener(e -> moChiTietHoaDonDangChon());
        return wrapper;
    }

    private JScrollPane buildTablePanel() {
    	String[] cols = {
    	        "Mã hóa đơn", "Thời gian tạo", "Thời gian ra", "Khách hàng",
    	        "Nhân viên", "SĐT", "Khuyến mãi", "Bàn", "Tổng tiền",
    	        "Phương thức TT", "Hình thức PV", "Trạng thái", "Lý do hủy"
    	};

        tableModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);


        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setRowHeight(34);

        table.setShowGrid(true);
        table.setGridColor(new Color(215, 205, 185));
        table.setSelectionBackground(new Color(190, 220, 245));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                String trangThai = table.getValueAt(row, 11) == null
                        ? ""
                        : table.getValueAt(row, 11).toString();

                if (!isSelected) {
                    c.setForeground(("Hủy".equalsIgnoreCase(trangThai)
                            || "Đã hủy".equalsIgnoreCase(trangThai)) ? Color.RED : Color.BLACK);
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 248, 242));
                }

                if (column == 3 || column == 4 || column == 12) {
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    moChiTietHoaDonDangChon();
                }
            }
        });

        // Dùng custom renderer cho header giống KhachHang_GUI để tránh lỗi LookAndFeel
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(CLR_TABLE_HDR);
                c.setForeground(new Color(50, 40, 30));
                c.setFont(new Font("Arial", Font.BOLD, 15));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

        JTableHeader header = table.getTableHeader();


        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(CLR_TABLE_HDR);
        header.setForeground(new Color(50, 40, 30));
        header.setPreferredSize(new Dimension(0, 36));

        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        int[] widths = {110, 140, 140, 140, 150, 120, 140, 70, 110, 130, 120, 120, 190};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowToForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private void xuLyTrangThai() {
        String trangThai = cbTrangThai.getSelectedItem() == null
                ? ""
                : cbTrangThai.getSelectedItem().toString();

        if ("Hủy".equalsIgnoreCase(trangThai) || "Đã hủy".equalsIgnoreCase(trangThai)) {
            txtLyDoHuy.setEnabled(true);
        } else {
            txtLyDoHuy.setText("");
            txtLyDoHuy.setEnabled(false);
        }
    }
    
    private void updateLyDoHuyStatus() {
        String trangThai = cbTrangThai.getSelectedItem() == null
                ? ""
                : cbTrangThai.getSelectedItem().toString();
     
        if ("Hủy".equalsIgnoreCase(trangThai) || "Đã hủy".equalsIgnoreCase(trangThai)) {
            txtLyDoHuy.setEditable(true);
            txtLyDoHuy.setForeground(Color.BLACK);
        } else {
            txtLyDoHuy.setText("");
            txtLyDoHuy.setEditable(false);
            txtLyDoHuy.setForeground(Color.GRAY);
        }
    }

    private void loadNhanVienToCombo() {
        cbNhanVien.removeAllItems();
        cbNhanVien.addItem(PH_NV);

        for (String ten : hd_dao.getAllTenNhanVien()) {
            cbNhanVien.addItem(ten);
        }

        cbNhanVien.setSelectedIndex(0);
    }

    private void loadKhuyenMaiToCombo() {
        txtKhuyenMai.removeAllItems();
        txtKhuyenMai.addItem(PH_KM);

        for (String ten : hd_dao.getAllTenKhuyenMai()) {
            txtKhuyenMai.addItem(ten);
        }

        txtKhuyenMai.setSelectedIndex(0);
    }

    private void lamMoi() {
        txtTenKhach.setText("");
        txtMaHoaDon.setText("");

        if (cbNhanVien.getItemCount() > 0) cbNhanVien.setSelectedIndex(0);
        if (cboPhuongThucThanhToan.getItemCount() > 0) {
            cboPhuongThucThanhToan.setSelectedIndex(0);
        }
        if (cboHinhThucPhucVu.getItemCount() > 0) {
            cboHinhThucPhucVu.setSelectedIndex(0);
        }

        txtBan.setText("");
        txtTongTien.setText("");
        txtSDT.setText("");

        txtKhuyenMai.setSelectedIndex(0);

        cbTrangThai.setSelectedIndex(0);
        txtLyDoHuy.setText("");
        txtLyDoHuy.setEnabled(false);



        dtThoiGianVao.setDate(null);
        dtThoiGianRa.setDate(null);

        table.clearSelection();


        enableFormFields();
        loadData();

    }

    private void capNhatHoaDon() {
        int row = table.getSelectedRow();
     
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn cần cập nhật!");
            return;
        }
     
        String maHD = txtMaHoaDon.getText().trim();
        String trangThai = getComboValue(cbTrangThai);
        String lyDoHuy = txtLyDoHuy.getText().trim();
        String phuongThucThanhToan = getComboValue(cboPhuongThucThanhToan);
     
        if (trangThai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái!");
            cbTrangThai.requestFocus();
            return;
        }
     
        // ===== KIỂM TRA ĐIỀU KIỆN: NẾU CHỌN "HỦY" THÌ BẮT BUỘC NHẬP LÝ DO =====
        if (("Hủy".equalsIgnoreCase(trangThai) || "Đã hủy".equalsIgnoreCase(trangThai)) 
                && lyDoHuy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do hủy!");
            txtLyDoHuy.requestFocus();
            return;
        }
     
        // Nếu không phải "Hủy", xóa lý do hủy (set thành null)
        if (!"Hủy".equalsIgnoreCase(trangThai) && !"Đã hủy".equalsIgnoreCase(trangThai)) {
            lyDoHuy = null;
        }
     
        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!");
            return;
        }
     
        String tenNV = getSafe(tableModel.getValueAt(row, 4));
        String tenKM = getSafe(tableModel.getValueAt(row, 6));
        String hinhThucPhucVu = getSafe(tableModel.getValueAt(row, 10));
     
        Timestamp thoiGianRa = null;
        Date d = dtThoiGianRa.getDate();
        if (d != null) {
            thoiGianRa = new Timestamp(d.getTime());
        }
     
        // ===== CẬP NHẬT HÓA ĐƠN =====
        boolean kq = hd_dao.updateHoaDon(
                maHD,
                tenNV,
                tenKM,
                trangThai,              
                lyDoHuy,                
                thoiGianRa,
                phuongThucThanhToan,   
                hinhThucPhucVu
        );
     
        if (kq) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

//    private void traCuu() {
//        String keyword = JOptionPane.showInputDialog(
//                this,
//                "Nhập từ khóa:",
//                "Tra cứu",
//                JOptionPane.PLAIN_MESSAGE
//        );
//
//        if (keyword == null || keyword.trim().isEmpty()) return;
//
//        String kw = keyword.trim().toLowerCase();
//
//        for (int r = 0; r < tableModel.getRowCount(); r++) {
//            for (int c = 0; c < tableModel.getColumnCount(); c++) {
//                Object val = tableModel.getValueAt(r, c);
//
//                if (val != null && val.toString().toLowerCase().contains(kw)) {
//                    table.setRowSelectionInterval(r, r);
//                    table.scrollRectToVisible(table.getCellRect(r, 0, true));
//                    return;
//                }
//            }
//        }
//
//        JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!", "Tra cứu", JOptionPane.INFORMATION_MESSAGE);
//    }
    
    
    private void traCuu() {
        // Xóa bảng trước khi tra cứu
        tableModel.setRowCount(0);

        // ===== Lấy dữ liệu textfield =====
        String tenKhach = txtTenKhach.getText().trim().toLowerCase();
        String sdt = txtSDT.getText().trim().toLowerCase();
        String ban = txtBan.getText().trim().toLowerCase();
        String tongTien = txtTongTien.getText().trim().toLowerCase();

        // Không dùng mã hóa đơn để tra cứu
        txtMaHoaDon.setText("");

        // ===== Lấy dữ liệu combobox =====
        String tenNhanVien = "";
        if (cbNhanVien.getSelectedItem() != null) {
            tenNhanVien = cbNhanVien.getSelectedItem().toString().trim();
            if (tenNhanVien.equals(PH_NV)) {
                tenNhanVien = "";
            }
        }

        String trangThai = "";
        if (cbTrangThai.getSelectedItem() != null) {
            trangThai = cbTrangThai.getSelectedItem().toString().trim();
            if (trangThai.equals(PH_TT)) {
                trangThai = "";
            }
        }

        String khuyenMai = "";
        if (txtKhuyenMai.getSelectedItem() != null) {
            khuyenMai = txtKhuyenMai.getSelectedItem().toString().trim();
            if (khuyenMai.equals(PH_KM)) {
                khuyenMai = "";
            }
        }

        String phuongThucTT = "";
        if (cboPhuongThucThanhToan.getSelectedItem() != null) {
            phuongThucTT = cboPhuongThucThanhToan.getSelectedItem().toString().trim();
            if (phuongThucTT.equals(PH_PTTT)) {
                phuongThucTT = "";
            }
        }

        String hinhThucPV = "";
        if (cboHinhThucPhucVu.getSelectedItem() != null) {
            hinhThucPV = cboHinhThucPhucVu.getSelectedItem().toString().trim();
            if (hinhThucPV.equals(PH_HTPV)) {
                hinhThucPV = "";
            }
        }

        // lowercase để so sánh
        tenNhanVien = tenNhanVien.toLowerCase();
        trangThai = trangThai.toLowerCase();
        khuyenMai = khuyenMai.toLowerCase();
        phuongThucTT = phuongThucTT.toLowerCase();
        hinhThucPV = hinhThucPV.toLowerCase();

        // ===== Bắt buộc phải nhập ít nhất 1 điều kiện =====
        boolean coDieuKien =
                !tenKhach.isEmpty() ||
                !sdt.isEmpty() ||
                !ban.isEmpty() ||
                !tongTien.isEmpty() ||
                !tenNhanVien.isEmpty() ||
                !trangThai.isEmpty() ||
                !khuyenMai.isEmpty() ||
                !phuongThucTT.isEmpty() ||
                !hinhThucPV.isEmpty();

        if (!coDieuKien) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập ít nhất 1 tiêu chí tìm kiếm!",
                    "Tra cứu",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        boolean found = false;

        // ===== DUYỆT TOÀN BỘ HÓA ĐƠN =====
        for (Object[] row : hd_dao.getAllHoaDon()) {

            String dbTenKhach = row[3] == null ? "" : row[3].toString().trim().toLowerCase();
            String dbTenNhanVien = row[4] == null ? "" : row[4].toString().trim().toLowerCase();
            String dbSDT = row[5] == null ? "" : row[5].toString().trim().toLowerCase();
            String dbKhuyenMai = row[6] == null ? "" : row[6].toString().trim().toLowerCase();
            String dbBan = row[7] == null ? "" : row[7].toString().trim().toLowerCase();
            String dbTongTien = row[8] == null ? "" : row[8].toString().trim().toLowerCase();
            String dbPhuongThuc = row[10] == null ? "" : row[10].toString().trim().toLowerCase();
            String dbHinhThuc = row[11] == null ? "" : row[11].toString().trim().toLowerCase();
            String dbTrangThai = row[12] == null ? "" : row[12].toString().trim().toLowerCase();

            /*
               LOGIC AND THẬT SỰ:
               Chỉ cần SAI 1 điều kiện -> bỏ hóa đơn đó ngay
            */

            boolean match = true;

            if (!tenKhach.isEmpty()) {
                if (!dbTenKhach.contains(tenKhach)) {
                    match = false;
                }
            }

            if (!tenNhanVien.isEmpty()) {
                if (!dbTenNhanVien.equals(tenNhanVien)) {
                    match = false;
                }
            }

            if (!sdt.isEmpty()) {
                if (!dbSDT.contains(sdt)) {
                    match = false;
                }
            }

            if (!ban.isEmpty()) {
                if (!dbBan.contains(ban)) {
                    match = false;
                }
            }

            if (!tongTien.isEmpty()) {
                if (!dbTongTien.contains(tongTien)) {
                    match = false;
                }
            }

            if (!trangThai.isEmpty()) {
                if (!dbTrangThai.equals(trangThai)) {
                    match = false;
                }
            }

            if (!khuyenMai.isEmpty()) {
                if (!dbKhuyenMai.contains(khuyenMai)) {
                    match = false;
                }
            }

            if (!phuongThucTT.isEmpty()) {
                if (!dbPhuongThuc.equals(phuongThucTT)) {
                    match = false;
                }
            }

            if (!hinhThucPV.isEmpty()) {
                if (!dbHinhThuc.equals(hinhThucPV)) {
                    match = false;
                }
            }

            // CHỈ add khi TẤT CẢ đều đúng
            if (match) {
                Timestamp vao = (Timestamp) row[1];
                Timestamp ra = (Timestamp) row[2];

                tableModel.addRow(new Object[]{
                        row[0],
                        vao != null ? sdf.format(vao) : "",
                        ra != null ? sdf.format(ra) : "",
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        formatMoneyValue(row[8]),
                        row[10],
                        row[11],
                        row[12],
                        row[13]
                });

                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không có hóa đơn phù hợp với tiêu chí tìm kiếm!",
                    "Tra cứu",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    
//    private void locHoaDon() {
//    	String trangThai = getComboValue(cbTrangThai);
//
//    	if (trangThai.isEmpty()) {
//    	    JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái cần lọc!");
//    	    return;
//    	}
//
//        tableModel.setRowCount(0);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
//        java.util.List<Object[]> ds = hd_dao.getHoaDonByTrangThai(trangThai);
//
//        if (ds.isEmpty()) {
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Không có hóa đơn nào với trạng thái: " + trangThai,
//                    "Thông báo",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
//            return;
//        }
//
//        for (Object[] row : ds) {
//            Timestamp vao = (Timestamp) row[1];
//            Timestamp ra = (Timestamp) row[2];
//
//            tableModel.addRow(new Object[]{
//            	    row[0],
//            	    vao != null ? sdf.format(vao) : "",
//            	    ra != null ? sdf.format(ra) : "",
//            	    row[3],   // khách hàng
//            	    row[4],   // nhân viên
//            	    row[5],   // sdt
//            	    row[6],   // khuyến mãi
//            	    row[7],   // bàn
//            	    formatMoneyValue(row[8]),   // tổng tiền
//            	    row[10],  // phương thức TT
//            	    row[11],  // hình thức PV
//            	    row[12],  // trạng thái
//            	    row[13]   // lý do hủy
//            	});
//        }
//    }
    
    private void locHoaDon() {
        Date tuNgay = dtThoiGianVao.getDate();
        Date denNgay = dtThoiGianRa.getDate();

        // Trạng thái là điều kiện phụ, không bắt buộc
        String trangThai = getComboValue(cbTrangThai).trim().toLowerCase();

        // Không có điều kiện nào thì báo
        if (tuNgay == null && denNgay == null && trangThai.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn thời gian hoặc trạng thái để lọc!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Timestamp tsTu = (tuNgay != null) ? toStartOfDay(tuNgay) : null;
        Timestamp tsDen = (denNgay != null) ? toEndOfDay(denNgay) : null;

        if (tsTu != null && tsDen != null && tsTu.after(tsDen)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Thời gian bắt đầu không được lớn hơn thời gian kết thúc!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        boolean found = false;
        List<Object[]> ds = hd_dao.getAllHoaDon();

        for (Object[] row : ds) {
            Timestamp vao = (Timestamp) row[1];
            String dbTrangThai = row[12] == null ? "" : row[12].toString().trim().toLowerCase();

            boolean match = true;

            // Lọc theo thời gian vào
            if (tsTu != null) {
                if (vao == null || vao.before(tsTu)) {
                    match = false;
                }
            }

            if (tsDen != null) {
                if (vao == null || vao.after(tsDen)) {
                    match = false;
                }
            }

            // Lọc thêm theo trạng thái nếu có chọn
            if (!trangThai.isEmpty()) {
                if (!dbTrangThai.equalsIgnoreCase(trangThai)) {
                    match = false;
                }
            }

            if (match) {
                Timestamp ra = (Timestamp) row[2];

                tableModel.addRow(new Object[]{
                        row[0],
                        vao != null ? sdf.format(vao) : "",
                        ra != null ? sdf.format(ra) : "",
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        formatMoneyValue(row[8]),
                        row[10],
                        row[11],
                        row[12],
                        row[13]
                });

                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không có hóa đơn phù hợp với điều kiện lọc!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    
    private Timestamp toStartOfDay(Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }

    private Timestamp toEndOfDay(Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    private String getComboValue(JComboBox<String> cb) {
        Object value = cb.getSelectedItem();
        if (value == null) return "";

        String s = value.toString().trim();

        if (PH_NV.equals(s) || PH_KM.equals(s) || PH_TT.equals(s)
                || PH_PTTT.equals(s) || PH_HTPV.equals(s)) {
            return "";
        }

        return s;
    }

    private String getSafe(Object val) {
        return val == null ? "" : val.toString();
    }

    private void loadRowToForm() {
        int row = table.getSelectedRow();

        if (row < 0) return;

        txtMaHoaDon.setText(getSafe(tableModel.getValueAt(row, 0)));
        txtTenKhach.setText(getSafe(tableModel.getValueAt(row, 3)));
        cbNhanVien.setSelectedItem(getSafe(tableModel.getValueAt(row, 4)));
        txtSDT.setText(getSafe(tableModel.getValueAt(row, 5)));
        txtBan.setText(getSafe(tableModel.getValueAt(row, 7)));
        txtTongTien.setText(getSafe(tableModel.getValueAt(row, 8)));
        txtKhuyenMai.setSelectedItem(getSafe(tableModel.getValueAt(row, 6)));
        cboPhuongThucThanhToan.setSelectedItem(getSafe(tableModel.getValueAt(row, 9)));
        cboHinhThucPhucVu.setSelectedItem(getSafe(tableModel.getValueAt(row, 10)));
        cbTrangThai.setSelectedItem(getSafe(tableModel.getValueAt(row, 11)));
        txtLyDoHuy.setText(getSafe(tableModel.getValueAt(row, 12)));
        xuLyTrangThai();

        try {
            String vao = getSafe(tableModel.getValueAt(row, 1));



            if (!vao.isEmpty()) {
                dtThoiGianVao.setDate(new SimpleDateFormat("HH:mm dd-MM-yyyy").parse(vao));
            } else {
                dtThoiGianVao.setDate(null);
            }

        } catch (Exception e) {
            dtThoiGianVao.setDate(null);
        }

        try {
            String ra = getSafe(tableModel.getValueAt(row, 2));



            if (!ra.isEmpty()) {
                dtThoiGianRa.setDate(new SimpleDateFormat("HH:mm dd-MM-yyyy").parse(ra));
            } else {
                dtThoiGianRa.setDate(null);
            }

        } catch (Exception e) {
            dtThoiGianRa.setDate(null);
        }

        disableFormFields();
    }

//    private void disableFormFields() {
//        txtTenKhach.setEnabled(false);
//        txtMaHoaDon.setEnabled(false);
//        txtBan.setEnabled(false);
//        txtTongTien.setEnabled(false);
//        txtSDT.setEnabled(false);
//        cbNhanVien.setEnabled(false);
//        txtKhuyenMai.setEnabled(false);
//        cboPhuongThucThanhToan.setEnabled(false);
//        cboHinhThucPhucVu.setEnabled(true);
//
//        dtThoiGianVao.setEnabled(false);
//        dtThoiGianRa.setEnabled(false);
//
//        cbTrangThai.setEnabled(true);
//        txtLyDoHuy.setEnabled("Hủy".equalsIgnoreCase(String.valueOf(cbTrangThai.getSelectedItem()))
//                || "Đã hủy".equalsIgnoreCase(String.valueOf(cbTrangThai.getSelectedItem())));
//    }
    
    private void disableFormFields() {
    	 
        // ===== KHÓA các trường KHÔNG được phép sửa =====
        txtTenKhach.setEditable(false);
        txtMaHoaDon.setEditable(false);
        txtBan.setEditable(false);
        txtTongTien.setEditable(false);
        txtSDT.setEditable(false);
        txtKhuyenMai.setEnabled(false);
     
        dtThoiGianVao.setEnabled(false);
        dtThoiGianVao.setFocusable(false);
     
        dtThoiGianRa.setEnabled(false);
        dtThoiGianRa.setFocusable(false);
     
        cbNhanVien.setEnabled(false);
        cbNhanVien.setFocusable(false);
     
        cboHinhThucPhucVu.setEnabled(false);
        cboHinhThucPhucVu.setFocusable(false);
     
        // ===== CÓ THỂ CẬP NHẬT CÁC TRƯỜNG NÀY =====
        cboPhuongThucThanhToan.setEnabled(true);
        cboPhuongThucThanhToan.setFocusable(true);
     
        cbTrangThai.setEnabled(true);
     
        updateLyDoHuyStatus();
     

    }

//    private void enableFormFields() {
//        txtTenKhach.setEnabled(true);
//        txtMaHoaDon.setEnabled(false);
//        txtBan.setEnabled(true);
//        txtTongTien.setEnabled(true);
//        txtSDT.setEnabled(true);
//
//        cbNhanVien.setEnabled(true);
//        txtKhuyenMai.setEnabled(true);
//
//        dtThoiGianVao.setEnabled(true);
//        dtThoiGianRa.setEnabled(true);
//        cboPhuongThucThanhToan.setEnabled(true);
//        cboHinhThucPhucVu.setEnabled(true);
//
//
//        cbTrangThai.setEnabled(true);
//        xuLyTrangThai();
//    }
    private void enableFormFields() {

        txtTenKhach.setEditable(true);
        txtMaHoaDon.setEditable(false); // mã hóa đơn vẫn khóa

        txtBan.setEditable(true);
        txtTongTien.setEditable(true);
        txtSDT.setEditable(true);

        txtKhuyenMai.setEditable(true);

        cbNhanVien.setEnabled(true);
        txtKhuyenMai.setEnabled(true);

        dtThoiGianVao.setEnabled(true);
        dtThoiGianRa.setEnabled(true);

        cboPhuongThucThanhToan.setEnabled(true);
        cboHinhThucPhucVu.setEnabled(true);

        cbTrangThai.setEnabled(true);

        xuLyTrangThai();
    }

    private void loadData() {
        if (tableModel == null) return;

        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        for (Object[] row : hd_dao.getAllHoaDon()) {
            Timestamp vao = (Timestamp) row[1];
            Timestamp ra = (Timestamp) row[2];

//            tableModel.addRow(new Object[]{
//                    row[0],
//                    vao != null ? sdf.format(vao) : "",
//                    ra != null ? sdf.format(ra) : "",
//                    		row[3], row[4], row[5], row[6], row[7], row[8],
//                    		row[9], row[10], row[11], row[12]
//            });
            tableModel.addRow(new Object[]{
            	    row[0],
            	    vao != null ? sdf.format(vao) : "",
            	    ra != null ? sdf.format(ra) : "",
            	    row[3],   // khách hàng
            	    row[4],   // nhân viên
            	    row[5],   // sdt
            	    row[6],   // khuyến mãi
            	    row[7],   // bàn
            	    formatMoneyValue(row[8]),   // tổng tiền
            	    row[10],  // phương thức TT
            	    row[11],  // hình thức PV
            	    row[12],  // trạng thái
            	    row[13]   // lý do hủy
            	});
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);


        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(new Color(45, 45, 45));

        return lbl;
    }

    private JTextField createTextField() {


        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, FIELD_ARC, FIELD_ARC);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(CLR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, FIELD_ARC, FIELD_ARC);

                g2.dispose();
            }
        };

        tf.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tf.setPreferredSize(FIELD_SIZE);
        tf.setMinimumSize(new Dimension(180, 38));
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));


        return tf;
    }
    
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);

        styleComboBox(cb);
        return cb;
    }


    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cb.setForeground(Color.BLACK);
        cb.setFocusable(false);


        cb.setPreferredSize(new Dimension(260, 38));
        cb.setMinimumSize(new Dimension(180, 38));
        cb.setOpaque(false);
        cb.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        cb.setUI(new RoundedComboBoxUI());


        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(


                        list, value, index, isSelected, cellHasFocus);

                lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

                String text = value == null ? "" : value.toString();

                boolean isPlaceholder =
                        PH_NV.equals(text)
                        || PH_KM.equals(text)
                        || PH_TT.equals(text)
                        || PH_PTTT.equals(text);

                if (isSelected) {
                    lbl.setBackground(new Color(224, 207, 180));
                } else {
                    lbl.setBackground(Color.WHITE);
                }

                lbl.setForeground(isPlaceholder ? Color.GRAY : Color.BLACK);

                if (index == -1) {
                    lbl.setOpaque(false);
                } else {
                    lbl.setOpaque(true);
                }


                return lbl;
            }
        });
        cb.addActionListener(e -> {
            Object selected = cb.getSelectedItem();
            if (selected == null) return;

            String s = selected.toString();

            if (PH_NV.equals(s) || PH_KM.equals(s) || PH_TT.equals(s)) {
                cb.setForeground(Color.GRAY);
            } else {
                cb.setForeground(Color.BLACK);
            }
        });
    }

    private JDateChooser createDateChooser() {
        JDateChooser dc = new JDateChooser() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, FIELD_ARC, FIELD_ARC);

                g2.setColor(CLR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, FIELD_ARC, FIELD_ARC);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        dc.setDateFormatString("HH:mm dd/MM/yyyy");
        dc.setPreferredSize(FIELD_SIZE);
        dc.setMinimumSize(new Dimension(180, 38));
        dc.setOpaque(false);
        dc.setBorder(null);

        JTextField editor = (JTextField) dc.getDateEditor().getUiComponent();
        editor.setFont(new Font("SansSerif", Font.PLAIN, 15));
        editor.setOpaque(false);
        editor.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 8));
        editor.setEditable(false);
        editor.setForeground(Color.BLACK);
        editor.setDisabledTextColor(Color.BLACK);

        JButton btn = (JButton) dc.getCalendarButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(38, 38));

        return dc;
    }

    private JButton createFuncButton(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c = bg;
                if (getModel().isPressed()) {
                    c = bg.darker();
                } else if (getModel().isRollover()) {
                    c = bg.brighter();
                }

                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(new Color(30, 30, 30));
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(8);
        btn.setMargin(new Insets(4, 8, 4, 8));

        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18, 18));
        }

        return btn;
    }
    
    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);

            if (icon.getIconWidth() <= 0) return null;

            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,


                        String lbl1, JComponent c1,
                        String lbl2, JComponent c2) {


        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.12;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(createLabel(lbl1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(c1, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.12;
        gbc.anchor = GridBagConstraints.EAST;

        if (lbl2 == null || lbl2.trim().isEmpty()) {
            p.add(new JLabel(), gbc);
        } else {
            p.add(createLabel(lbl2), gbc);
        }

        gbc.gridx = 3;
        gbc.weightx = 0.38;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(c2, gbc);
    }



    private JFrame getParentFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame) {
            return (JFrame) w;
        }
        return null;
    }
    private static class RoundedComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(120, 120, 120));

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    g2.drawLine(cx - 7, cy - 4, cx, cy + 4);
                    g2.drawLine(cx, cy + 4, cx + 7, cy - 4);

                    g2.dispose();
                }
            };

            btn.setPreferredSize(new Dimension(40, 38));
            btn.setBorder(null);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);

            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);

            g2.setColor(new Color(175, 160, 135));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);

            g2.dispose();

            super.paint(g, c);
        }

    }
    private void moChiTietHoaDonDangChon() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hóa đơn!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String maHD = getSafe(tableModel.getValueAt(modelRow, 0));

        Object[] hd = hd_dao.getHoaDonByMa(maHD);
        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
            return;
        }

        List<ChiTietHoaDon> dsCT = cthdDAO.getChiTietTheoMaHD(maHD);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        Timestamp vao = (Timestamp) hd[1];
        Timestamp ra = (Timestamp) hd[2];

        String thoiGian = (vao == null ? "" : sdf.format(vao))
                + " - "
                + (ra == null ? "" : sdf.format(ra));

        String tenKH = getSafe(hd[3]);
        String tenNV = getSafe(hd[4]);
        String tenKM = getSafe(hd[6]);
        String tenBan = getSafe(hd[7]);

        double tongTien = toDouble(hd[8]);
        double tienKhachTra = toDouble(hd[9]);
        double vat = toDouble(hd[10]);
        double tienThua = toDouble(hd[11]);
        String phuongThuc = getSafe(hd[12]);

        String maPhieuDatBan = getSafe(hd[16]);
        double tienCoc = toDouble(hd[17]);

        double tongCong = tongTien;
        double tienGiam = 0;

        int diemCongThem = (int) (tongTien / 100000);

        JDialog dlg = new JDialog(
                getParentFrame(),
                "Chi tiết hóa đơn",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dlg.setContentPane(new HoaDonChiTiet_DigLog(
                "HÓA ĐƠN THANH TOÁN",
                maHD,
                tenBan,
                maPhieuDatBan,
                tienCoc,
                tenKH,
                tenNV,
                thoiGian,
                tenKM,
                tienGiam,
                tongTien,
                vat,
                tongCong,
                tienKhachTra,
                tienThua,
                phuongThuc,
                diemCongThem,
                dsCT,
                null
        ));

        Dimension screen=
                Toolkit.getDefaultToolkit()
                        .getScreenSize();

        int w=
                Math.min(
                        850,
                        screen.width-120
                );

        int h=
                Math.min(
                        900,
                        screen.height-80
                );

        dlg.setSize(w,h);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
    private double toDouble(Object value) {
        try {
            if (value == null) return 0;
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }

            String s = value.toString()
                    .replace("VNĐ", "")
                    .replace("đ", "")
                    .replace(".", "")
                    .replace(",", "")
                    .trim();

            if (s.isEmpty()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
    private String formatMoneyValue(Object value) {
        try {
            if (value == null) return "0";

            double money;
            if (value instanceof Number) {
                money = ((Number) value).doubleValue();
            } else {
                String s = value.toString()
                        .replace("VNĐ", "")
                        .replace("đ", "")
                        .replace(".", "")
                        .replace(",", "")
                        .trim();
                if (s.isEmpty()) return "0";
                money = Double.parseDouble(s);
            }

            return String.format("%,.0f", money).replace(",", ".");
        } catch (Exception e) {
            return value == null ? "0" : value.toString();
        }
    }
}