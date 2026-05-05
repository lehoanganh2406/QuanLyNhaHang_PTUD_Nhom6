package gui;

import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import dao.CaLamViec_DAO;
import digLog.DongCa_DigLog;
import entity.CaLamViec;
import entity.TaiKhoan;

public class QuanLyCa_GUI extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Color BG = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color DARK = new Color(0x6B4F3B);
    private final Color BLUE = new Color(80, 145, 220);
    private final Color GREEN = new Color(72, 180, 120);
    private final Color ORANGE = new Color(235, 170, 80);
    private final Color RED = new Color(230, 110, 100);
    private final Color BORDER = new Color(220, 225, 232);

    private TaiKhoan taiKhoanDangNhap;
    private final CaLamViec_DAO caDAO = new CaLamViec_DAO();

    private JTable table;
    private DefaultTableModel model;

    private JLabel lblTongCa;
    private JLabel lblCaDangMo;
    private JLabel lblDoanhThu;
    private JLabel lblTienMat;

    private JTextField txtTuKhoa;
    private JComboBox<String> cboTrangThai;

    private JButton btnChiTiet;
    private JButton btnLamMoi;

    private ArrayList<Object[]> dsCa = new ArrayList<>();

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

    public QuanLyCa_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        initEvents();
        loadData();
    }

    public QuanLyCa_GUI() {
        this(null);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK);
        header.setBorder(new EmptyBorder(18, 26, 18, 26));

        JLabel title = new JLabel("QUẢN LÝ CA LÀM VIỆC");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(4));

        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        root.add(buildSummaryCards(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);

        return root;
    }

    private JPanel buildSummaryCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);

        lblTongCa = new JLabel("0");
        lblCaDangMo = new JLabel("0");
        lblDoanhThu = new JLabel("0 VNĐ");
        lblTienMat = new JLabel("0 VNĐ");

        cards.add(card("Tổng số ca", lblTongCa, BLUE));
        cards.add(card("Ca đang mở", lblCaDangMo, GREEN));
        cards.add(card("Tổng doanh thu", lblDoanhThu, ORANGE));
        cards.add(card("Tiền mặt cuối ca", lblTienMat, RED));

        return cards;
    }

    private JPanel card(String title, JLabel value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblTitle.setForeground(new Color(100, 105, 115));

        value.setFont(new Font("SansSerif", Font.BOLD, 25));
        value.setForeground(DARK);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(6, 0));
        line.setBackground(accent);

        p.add(line, BorderLayout.WEST);
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        center.add(buildToolbar(), BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);

        return center;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(CARD);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        txtTuKhoa = new JTextField();
        txtTuKhoa.setPreferredSize(new Dimension(280, 40));
        txtTuKhoa.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtTuKhoa.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(205, 210, 220), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang mở", "Đã đóng"});
        cboTrangThai.setPreferredSize(new Dimension(150, 40));
        cboTrangThai.setFont(new Font("SansSerif", Font.PLAIN, 15));

        left.add(new JLabel("Tìm kiếm:"));
        left.add(txtTuKhoa);
        left.add(new JLabel("Trạng thái:"));
        left.add(cboTrangThai);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        btnChiTiet = button("Chi tiết ca", BLUE);
        btnLamMoi = button("Làm mới", ORANGE);

        right.add(btnChiTiet);
        right.add(btnLamMoi);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);

        return toolbar;
    }

    private JScrollPane buildTable() {
        String[] cols = {
                "Mã ca", "Tên ca", "Giờ mở", "Giờ đóng",
                "Tài khoản", "Nhân viên", "Tiền mở ca",
                "Tiền mặt", "Chuyển khoản", "Visa",
                "Tổng doanh thu", "Trạng thái"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(38);
        table.setGridColor(new Color(230, 234, 240));
        table.setSelectionBackground(new Color(210, 232, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(DARK);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("SansSerif", Font.BOLD, 14));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);

                c.setBorder(new EmptyBorder(0, 8, 0, 8));

                String tt = String.valueOf(t.getValueAt(row, 11));

                if (!isSelected) {
                    if ("Đang mở".equalsIgnoreCase(tt)) {
                        c.setBackground(new Color(225, 248, 232));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    }
                    c.setForeground(Color.BLACK);
                }

                if (col == 5) {
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                }

                if (col == 11) {
                    c.setFont(new Font("SansSerif", Font.BOLD, 14));
                    c.setForeground("Đang mở".equalsIgnoreCase(tt)
                            ? new Color(30, 140, 80)
                            : new Color(90, 95, 105));
                }

                return c;
            }
        });

        int[] widths = {80, 105, 145, 145, 95, 180, 115, 115, 125, 100, 135, 105};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    moChiTietCaDangChon();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private void initEvents() {
        btnLamMoi.addActionListener(e -> loadData());
        btnChiTiet.addActionListener(e -> moChiTietCaDangChon());
        

        txtTuKhoa.addActionListener(e -> locDuLieu());
        cboTrangThai.addActionListener(e -> locDuLieu());

        txtTuKhoa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { locDuLieu(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { locDuLieu(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { locDuLieu(); }
        });
    }

    private void loadData() {
        dsCa = caDAO.getAllCaLamViecQuanLy();
        render(dsCa);
        capNhatThongKe(dsCa);
    }

    private void render(ArrayList<Object[]> data) {
        model.setRowCount(0);

        for (Object[] r : data) {
            model.addRow(new Object[]{
                    r[0],
                    r[1],
                    formatDate(r[2]),
                    formatDate(r[3]),
                    r[4],
                    r[5],
                    formatMoney(r[6]),
                    formatMoney(r[7]),
                    formatMoney(r[8]),
                    formatMoney(r[9]),
                    formatMoney(r[10]),
                    r[11]
            });
        }
    }

    private void capNhatThongKe(ArrayList<Object[]> data) {
        int tongCa = data.size();
        int dangMo = 0;
        double doanhThu = 0;
        double tienMat = 0;

        for (Object[] r : data) {
            if ("Đang mở".equalsIgnoreCase(safe(r[11]))) {
                dangMo++;
            }
            doanhThu += toDouble(r[10]);
            tienMat += toDouble(r[7]);
        }

        lblTongCa.setText(String.valueOf(tongCa));
        lblCaDangMo.setText(String.valueOf(dangMo));
        lblDoanhThu.setText(formatMoney(doanhThu));
        lblTienMat.setText(formatMoney(tienMat));
    }

    private void locDuLieu() {
        String kw = txtTuKhoa.getText().trim().toLowerCase();
        String tt = cboTrangThai.getSelectedItem() == null ? "Tất cả" : cboTrangThai.getSelectedItem().toString();

        ArrayList<Object[]> kq = new ArrayList<>();

        for (Object[] r : dsCa) {
            String all = (
                    safe(r[0]) + " " + safe(r[1]) + " " + safe(r[4]) + " " + safe(r[5])
            ).toLowerCase();

            boolean matchKeyword = kw.isEmpty() || all.contains(kw);
            boolean matchTrangThai = "Tất cả".equalsIgnoreCase(tt) || safe(r[11]).equalsIgnoreCase(tt);

            if (matchKeyword && matchTrangThai) {
                kq.add(r);
            }
        }

        render(kq);
        capNhatThongKe(kq);
    }

  

    private void moChiTietCaDangChon() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);

        String maCa = safe(model.getValueAt(modelRow, 0));
        Object[] ca = timCaTheoMa(maCa);

        if (ca == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin ca.");
            return;
        }

        String trangThai = safe(ca[11]);

        if ("Đang mở".equalsIgnoreCase(trangThai)) {
            CaLamViec caDangMo = caDAO.layCaDangMo();

            if (caDangMo != null && maCa.equalsIgnoreCase(caDangMo.getMaCa())) {
                Window w = SwingUtilities.getWindowAncestor(this);
                DongCa_DigLog dlg;

                if (w instanceof Frame) {
                    dlg = new DongCa_DigLog((Frame) w, caDangMo);
                } else {
                    dlg = new DongCa_DigLog(null, caDangMo);
                }

                dlg.setVisible(true);
                loadData();
                return;
            }
        }

        moPopupChiTietCa(ca);
    }

    private Object[] timCaTheoMa(String maCa) {
        for (Object[] r : dsCa) {
            if (safe(r[0]).equalsIgnoreCase(maCa)) {
                return r;
            }
        }
        return null;
    }

    private void moPopupChiTietCa(Object[] ca) {
        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Chi tiết ca",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel title = new JLabel("CHI TIẾT CA LÀM VIỆC", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(DARK);

        JPanel info = new JPanel(new GridLayout(12, 2, 12, 10));
        info.setBackground(Color.WHITE);

        info.add(label("Mã ca"));
        info.add(value(safe(ca[0])));

        info.add(label("Tên ca"));
        info.add(value(safe(ca[1])));

        info.add(label("Thời gian mở"));
        info.add(value(formatDate(ca[2])));

        info.add(label("Thời gian đóng"));
        info.add(value(formatDate(ca[3])));

        info.add(label("Tài khoản"));
        info.add(value(safe(ca[4])));

        info.add(label("Nhân viên"));
        info.add(value(safe(ca[5])));

        info.add(label("Tiền mở ca"));
        info.add(value(formatMoney(ca[6])));

        info.add(label("Tiền mặt cuối ca"));
        info.add(value(formatMoney(ca[7])));

        info.add(label("Chuyển khoản cuối ca"));
        info.add(value(formatMoney(ca[8])));

        info.add(label("Visa cuối ca"));
        info.add(value(formatMoney(ca[9])));

        info.add(label("Tổng doanh thu"));
        info.add(value(formatMoney(ca[10])));

        info.add(label("Trạng thái"));
        info.add(value(safe(ca[11])));

        JButton btnDong = button("Đóng", BLUE);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.add(btnDong);

        btnDong.addActionListener(e -> dlg.dispose());

        root.add(title, BorderLayout.NORTH);
        root.add(info, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setSize(620, 620);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(new Color(80, 85, 95));
        return lbl;
    }

    private JLabel value(String text) {
        JLabel lbl = new JLabel(text == null ? "" : text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(DARK);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(247, 249, 252));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 232), 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return lbl;
    }

    private JButton button(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String getNhanVienDangNhap() {
        try {
            if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null) {
                return taiKhoanDangNhap.getMaNV().getHoTen();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String formatDate(Object obj) {
        if (obj == null) return "";

        try {
            if (obj instanceof Timestamp) {
                return dateFormat.format((Timestamp) obj);
            }
            return obj.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String formatMoney(Object obj) {
        return formatMoney(toDouble(obj));
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value).replace(",", ".") + " VNĐ";
    }

    private double toDouble(Object obj) {
        try {
            if (obj == null) return 0;

            if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            }

            return parseTienNhap(obj.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }
    private double parseTienNhap(String text) {
        try {
            if (text == null) return 0;

            String s = text.trim()
                    .replace("VNĐ", "")
                    .replace("đ", "")
                    .replace(" ", "");

            // Nhập kiểu Việt: 3.000.000
            if (s.matches("\\d{1,3}(\\.\\d{3})+")) {
                s = s.replace(".", "");
            }

            // Nhập kiểu SQL/java: 3000000.0 thì giữ dấu .
            s = s.replace(",", "");

            return Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }
    }
}