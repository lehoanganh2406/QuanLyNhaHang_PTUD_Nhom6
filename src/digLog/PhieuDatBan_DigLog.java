package digLog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import com.toedter.calendar.JDateChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;

public class PhieuDatBan_DigLog extends JDialog {

    private JTextField txtMaPhieu;
    private JTextField txtKhachHang;
    private JTextField txtSoDienThoai;
    private JTextField txtGioKhachVao;
    private JSpinner spnSoLuongKhach;
    private JTextField txtBan;
    private JTextField txtTienCoc;
    private JTextArea txtGhiChu;

    private JButton btnCalendar;
    private JButton btnSearchBan;
    private JButton btnThoat;
    private JButton btnDatBan;
    private JButton btnHuy;
    private JButton btnNhanBan;
    private JButton btnLuu;

    private JPopupMenu popupBan;
    private JTable tblBan;
    private DefaultTableModel modelBan;

    private Timestamp thoiGianDaChon;
    private ArrayList<String[]> dsBanTheoGio = new ArrayList<>();
    private String maBanDuocChon = "";

    private String maPhieuHienTai = null;
    private boolean cheDoChiTiet = false;

    private final Color BG_MAIN = new Color(239, 239, 239);
    private final Color BORDER = new Color(170, 170, 170);
    private final Color DISABLED_BG = new Color(222, 222, 222);
    private final Color PLACEHOLDER = new Color(145, 145, 145);

    private static final String PH_KHACH = "Nhập tên khách hàng";
    private static final String PH_SDT = "Nhập số điện thoại";
    private static final String PH_GIO = "Chọn ngày giờ";
    private static final String PH_BAN = "Tìm kiếm bàn...";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                PhieuDatBan_DigLog dialog = new PhieuDatBan_DigLog(null);
                dialog.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PhieuDatBan_DigLog(Frame owner) {
        super(owner, "Phiếu đặt bàn", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(720, 560));
        setSize(700, 880);
        setLocationRelativeTo(owner);

        initUI();
        initEvents();
        loadDanhSachBanTheoGioChon();
        setModeThemMoi();
    }

    public PhieuDatBan_DigLog(Frame owner, String maPhieuDatBan) {
        super(owner, "Phiếu đặt bàn", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(720, 560));
        setSize(700, 880);
        setLocationRelativeTo(owner);

        this.maPhieuHienTai = maPhieuDatBan;
        this.cheDoChiTiet = true;

        initUI();
        initEvents();
        loadDanhSachBanTheoGioChon();
        loadPhieuDatBanLenForm(maPhieuDatBan);
        setModeChiTiet();
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_MAIN);
        setContentPane(contentPane);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(BG_MAIN);
        pnlTitle.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Phiếu đặt bàn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 34));
        lblTitle.setForeground(Color.BLACK);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 1));
        line.setBackground(new Color(150, 150, 150));

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setBackground(BG_MAIN);
        topWrap.add(pnlTitle, BorderLayout.CENTER);
        topWrap.add(line, BorderLayout.SOUTH);
        contentPane.add(topWrap, BorderLayout.NORTH);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(BG_MAIN);
        formWrap.setBorder(new EmptyBorder(18, 24, 12, 24));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_MAIN);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        formWrap.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(formWrap, BorderLayout.CENTER);

        Font lblFont = new Font("Arial", Font.PLAIN, 22);
        Font inputFont = new Font("Arial", Font.PLAIN, 18);

        txtMaPhieu = createTextField("", inputFont);
        txtMaPhieu.setEditable(false);
        txtMaPhieu.setBackground(DISABLED_BG);
        txtMaPhieu.setForeground(Color.DARK_GRAY);

        txtKhachHang = createTextField("", inputFont);
        datPlaceholder(txtKhachHang, PH_KHACH);

        txtSoDienThoai = createTextField("", inputFont);
        datPlaceholder(txtSoDienThoai, PH_SDT);

        txtGioKhachVao = createTextField("", inputFont);
        txtGioKhachVao.setEditable(false);
        txtGioKhachVao.setBackground(Color.WHITE);
        datPlaceholder(txtGioKhachVao, PH_GIO);

        spnSoLuongKhach = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnSoLuongKhach.setFont(inputFont);
        spnSoLuongKhach.setPreferredSize(new Dimension(360, 44));
        spnSoLuongKhach.setBorder(new LineBorder(BORDER, 1));

        JComponent editor = spnSoLuongKhach.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField txt = ((JSpinner.DefaultEditor) editor).getTextField();
            txt.setFont(inputFont);
            txt.setHorizontalAlignment(SwingConstants.LEFT);
            txt.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        txtBan = createTextField("", inputFont);
        txtBan.setToolTipText("Bấm để chọn bàn");
        datPlaceholder(txtBan, PH_BAN);

        txtTienCoc = createTextField("200.000", inputFont);
        txtTienCoc.setBackground(DISABLED_BG);
        txtTienCoc.setForeground(Color.DARK_GRAY);

        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setFont(inputFont);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(new EmptyBorder(10, 12, 10, 12));
        txtGhiChu.setBackground(Color.WHITE);

        btnCalendar = createIconButton("📅", 18);
        btnSearchBan = createIconButton("⌕", 20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addFormRow(formPanel, gbc, row++, "Mã đặt bàn", lblFont, wrapField(txtMaPhieu, null));
        addFormRow(formPanel, gbc, row++, "Khách hàng", lblFont, wrapField(txtKhachHang, null));
        addFormRow(formPanel, gbc, row++, "Điện thoại khách hàng", lblFont, wrapField(txtSoDienThoai, null));
        addFormRow(formPanel, gbc, row++, "Giờ khách vào", lblFont, wrapField(txtGioKhachVao, btnCalendar));
        addFormRow(formPanel, gbc, row++, "Số lượng khách", lblFont, spnSoLuongKhach);
        addFormRow(formPanel, gbc, row++, "Bàn", lblFont, wrapField(txtBan, btnSearchBan));
        addFormRow(formPanel, gbc, row++, "Tiền cọc", lblFont, wrapField(txtTienCoc, null));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.36;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(createLabel("Ghi chú", lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.64;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane scrGhiChu = new JScrollPane(txtGhiChu);
        scrGhiChu.setPreferredSize(new Dimension(360, 100));
        scrGhiChu.setBorder(new LineBorder(BORDER, 1));
        formPanel.add(scrGhiChu, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.36;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(createLabel("Món đặt trước ✍", lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.64;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(Box.createVerticalStrut(24), gbc);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        pnlButtons.setBackground(BG_MAIN);

        btnHuy = new ColoredButton("Hủy", new Color(232, 83, 83));
        btnHuy.setFont(new Font("Arial", Font.PLAIN, 22));
        btnHuy.setPreferredSize(new Dimension(120, 52));

        btnThoat = new ColoredButton("Thoát", new Color(219, 167, 69));
        btnThoat.setFont(new Font("Arial", Font.PLAIN, 22));
        btnThoat.setPreferredSize(new Dimension(120, 52));

        btnNhanBan = new ColoredButton("Nhận bàn", new Color(116, 191, 102));
        btnNhanBan.setFont(new Font("Arial", Font.PLAIN, 22));
        btnNhanBan.setPreferredSize(new Dimension(150, 52));

        btnLuu = new ColoredButton("Lưu", new Color(74, 144, 206));
        btnLuu.setFont(new Font("Arial", Font.PLAIN, 22));
        btnLuu.setPreferredSize(new Dimension(120, 52));

        btnDatBan = new ColoredButton("Đặt bàn", new Color(74, 144, 206));
        btnDatBan.setFont(new Font("Arial", Font.PLAIN, 22));
        btnDatBan.setPreferredSize(new Dimension(140, 52));

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnThoat);
        pnlButtons.add(btnNhanBan);
        pnlButtons.add(btnLuu);
        pnlButtons.add(btnDatBan);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 10, 8, 10);

        JPanel pnlBottomWrap = new JPanel(new BorderLayout());
        pnlBottomWrap.setOpaque(false);
        pnlBottomWrap.add(pnlButtons, BorderLayout.EAST);
        formPanel.add(pnlBottomWrap, gbc);

        initPopupBan();
        chanNhapKhacChoSoDienThoai();
    }

    private void setModeChiTiet() {
        btnDatBan.setVisible(false);
        btnHuy.setVisible(true);
        btnNhanBan.setVisible(true);
        btnLuu.setVisible(true);
    }

    private void setModeThemMoi() {
        btnDatBan.setVisible(true);
        btnHuy.setVisible(false);
        btnNhanBan.setVisible(false);
        btnLuu.setVisible(false);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Font lblFont, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.36;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabel(label, lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.64;
        panel.add(comp, gbc);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(font);
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JTextField createTextField(String text, Font font) {
        JTextField txt = new JTextField(text);
        txt.setFont(font);
        txt.setPreferredSize(new Dimension(360, 44));
        txt.setBorder(new LineBorder(BORDER, 1));
        txt.setBackground(Color.WHITE);
        txt.setMargin(new Insets(0, 12, 0, 12));
        return txt;
    }

    private JPanel wrapField(JTextField txt, JButton rightButton) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(360, 44));

        if (rightButton == null) {
            panel.add(txt, BorderLayout.CENTER);
        } else {
            txt.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, BORDER));
            rightButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER));
            panel.add(txt, BorderLayout.CENTER);
            panel.add(rightButton, BorderLayout.EAST);
        }
        return panel;
    }

    private JButton createIconButton(String text, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, fontSize));
        btn.setPreferredSize(new Dimension(44, 44));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void datPlaceholder(JTextField txt, String placeholder) {
        txt.setText(placeholder);
        txt.setForeground(PLACEHOLDER);

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(PLACEHOLDER);
                }
            }
        });
    }

    private boolean isPlaceholder(JTextField txt, String placeholder) {
        return txt.getText().trim().equals(placeholder);
    }

    private void initEvents() {
        btnThoat.addActionListener(e -> dispose());

        btnCalendar.addActionListener(e -> moDialogChonNgayGio());

        btnSearchBan.addActionListener(e -> {
            if (thoiGianDaChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày giờ trước!");
                return;
            }
            loadDanhSachBanTheoGioChon();
            showPopupBan();
        });

        txtBan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thoiGianDaChon == null) {
                    JOptionPane.showMessageDialog(PhieuDatBan_DigLog.this, "Vui lòng chọn ngày giờ trước!");
                    return;
                }

                if (isPlaceholder(txtBan, PH_BAN)) {
                    txtBan.setText("");
                    txtBan.setForeground(Color.BLACK);
                }

                loadDanhSachBanTheoGioChon();
                locBan(txtBan.getText().trim());
                showPopupBan();
            }
        });

        txtBan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (thoiGianDaChon == null) {
                    return;
                }
                locBan(isPlaceholder(txtBan, PH_BAN) ? "" : txtBan.getText().trim());
                showPopupBan();
            }
        });

        btnDatBan.addActionListener(e -> datBanMoi());

        btnHuy.addActionListener(e -> {
            if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) return;

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn hủy phiếu này không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
                    if (dao.huyPhieuDatBan(maPhieuHienTai)) {
                        JOptionPane.showMessageDialog(this, "Hủy phiếu thành công!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Hủy phiếu thất bại!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnNhanBan.addActionListener(e -> {
            if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) return;

            try {
                PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
                if (dao.capNhatTrangThai(maPhieuHienTai, "Đã xếp bàn")) {
                    JOptionPane.showMessageDialog(this, "Nhận bàn thành công!");
                    loadPhieuDatBanLenForm(maPhieuHienTai);
                } else {
                    JOptionPane.showMessageDialog(this, "Nhận bàn thất bại!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnLuu.addActionListener(e -> capNhatPhieuDatBan());
    }

    private void datBanMoi() {
        if (!validateForm()) {
            return;
        }

        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();

            boolean biTrung = dao.kiemTraTrungLich(maBanDuocChon, thoiGianDaChon, 120);
            if (biTrung) {
                JOptionPane.showMessageDialog(
                        this,
                        "Bàn này đã trùng lịch đặt, vui lòng chọn lại bàn khác!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );
                loadDanhSachBanTheoGioChon();
                return;
            }

            String tienCocText = txtTienCoc.getText().trim()
                    .replace(".", "")
                    .replace(",", "")
                    .replace("đ", "")
                    .replace("Đ", "");

            BigDecimal tienCoc = new BigDecimal(tienCocText);

            String maPhieuMoi = dao.themPhieuDatBan(
                    maBanDuocChon,
                    txtKhachHang.getText().trim(),
                    txtSoDienThoai.getText().trim(),
                    (Integer) spnSoLuongKhach.getValue(),
                    thoiGianDaChon,
                    tienCoc,
                    txtGhiChu.getText().trim(),
                    "Đang chờ"
            );

            if (maPhieuMoi != null) {
                txtMaPhieu.setText(maPhieuMoi);

                JOptionPane.showMessageDialog(
                        this,
                        "Đặt bàn thành công! Mã phiếu: " + maPhieuMoi,
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Lưu phiếu đặt bàn thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Có lỗi khi lưu phiếu đặt bàn!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void capNhatPhieuDatBan() {
        if (maPhieuHienTai == null || maPhieuHienTai.trim().isEmpty()) return;

        if (!validateForm()) {
            return;
        }

        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();

            boolean biTrung = dao.kiemTraTrungLich(maBanDuocChon, thoiGianDaChon, 120);
            if (biTrung) {
                String[] oldData = dao.timTheoMaPhieu(maPhieuHienTai);
                if (oldData != null) {
                    Timestamp oldTime = Timestamp.valueOf(oldData[5]);
                    String oldMaBan = oldData[1];
                    if (!(oldMaBan.equalsIgnoreCase(maBanDuocChon) && oldTime.equals(thoiGianDaChon))) {
                        JOptionPane.showMessageDialog(this, "Bàn này đã trùng lịch đặt!");
                        return;
                    }
                }
            }

            String tienCocText = txtTienCoc.getText().trim()
                    .replace(".", "")
                    .replace(",", "")
                    .replace("đ", "")
                    .replace("Đ", "");

            BigDecimal tienCoc = new BigDecimal(tienCocText);

            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE PhieuDatBan "
                    + "SET maBan = ?, tenKhach = ?, sdt = ?, soLuongNguoi = ?, thoiGianDen = ?, tienCoc = ?, ghiChu = ? "
                    + "WHERE maPhieuDatBan = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBanDuocChon);
            ps.setString(2, txtKhachHang.getText().trim());
            ps.setString(3, txtSoDienThoai.getText().trim());
            ps.setInt(4, (Integer) spnSoLuongKhach.getValue());
            ps.setTimestamp(5, thoiGianDaChon);
            ps.setBigDecimal(6, tienCoc);
            ps.setString(7, txtGhiChu.getText().trim());
            ps.setString(8, maPhieuHienTai);

            int n = ps.executeUpdate();
            ps.close();

            if (n > 0) {
                JOptionPane.showMessageDialog(this, "Lưu cập nhật thành công!");
                loadPhieuDatBanLenForm(maPhieuHienTai);
            } else {
                JOptionPane.showMessageDialog(this, "Lưu cập nhật thất bại!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi khi cập nhật phiếu!");
        }
    }

    private void loadPhieuDatBanLenForm(String maPhieu) {
        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
            String[] row = dao.timTheoMaPhieu(maPhieu);

            if (row == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn!");
                return;
            }

            txtMaPhieu.setText(row[0]);
            maBanDuocChon = row[1];

            txtKhachHang.setText(row[2]);
            txtKhachHang.setForeground(Color.BLACK);

            txtSoDienThoai.setText(row[3]);
            txtSoDienThoai.setForeground(Color.BLACK);

            spnSoLuongKhach.setValue(Integer.parseInt(row[4]));

            Timestamp tg = Timestamp.valueOf(row[5]);
            thoiGianDaChon = tg;
            txtGioKhachVao.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(tg));
            txtGioKhachVao.setForeground(Color.BLACK);

            txtTienCoc.setText(formatTienCoc(row[6]));
            txtGhiChu.setText(row[7] == null ? "" : row[7]);

            loadDanhSachBanTheoGioChon();
            for (String[] ban : dsBanTheoGio) {
                if (ban[0].equalsIgnoreCase(maBanDuocChon)) {
                    txtBan.setText(ban[1]);
                    txtBan.setForeground(Color.BLACK);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải phiếu đặt bàn!");
        }
    }

    private String formatTienCoc(String value) {
        try {
            long so = Long.parseLong(value.split("\\.")[0]);
            return String.format("%,d", so).replace(',', '.');
        } catch (Exception e) {
            return value;
        }
    }

    private void chanNhapKhacChoSoDienThoai() {
        txtSoDienThoai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (isPlaceholder(txtSoDienThoai, PH_SDT)) {
                    txtSoDienThoai.setText("");
                    txtSoDienThoai.setForeground(Color.BLACK);
                }

                char c = e.getKeyChar();
                String current = txtSoDienThoai.getText();

                if (!Character.isDigit(c) || current.length() >= 10) {
                    e.consume();
                }
            }
        });
    }

    private void moDialogChonNgayGio() {
        JDialog dlg = new JDialog(this, "Chọn ngày giờ", true);
        dlg.setSize(380, 230);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNgay = new JLabel("Ngày:");
        lblNgay.setFont(new Font("Arial", Font.PLAIN, 16));

        JDateChooser chooser = new JDateChooser();

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        Date homNay = cal.getTime();

        chooser.setDate(new Date());
        chooser.setMinSelectableDate(homNay);
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setLocale(new Locale("vi", "VN"));
        chooser.setPreferredSize(new Dimension(190, 34));

        JLabel lblGio = new JLabel("Giờ:");
        lblGio.setFont(new Font("Arial", Font.PLAIN, 16));

        String[] dsGio = new String[15];
        for (int i = 0; i < 15; i++) {
            dsGio[i] = String.format("%02d", i + 9);
        }

        String[] dsPhut = new String[60];
        for (int i = 0; i < 60; i++) {
            dsPhut[i] = String.format("%02d", i);
        }

        JComboBox<String> cboGio = new JComboBox<>(dsGio);
        JComboBox<String> cboPhut = new JComboBox<>(dsPhut);

        cboGio.setFont(new Font("Arial", Font.PLAIN, 16));
        cboPhut.setFont(new Font("Arial", Font.PLAIN, 16));

        cboGio.setPreferredSize(new Dimension(80, 34));
        cboPhut.setPreferredSize(new Dimension(80, 34));

        JPanel pnlGioPhut = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlGioPhut.setBackground(Color.WHITE);
        pnlGioPhut.add(cboGio);
        pnlGioPhut.add(new JLabel(":"));
        pnlGioPhut.add(cboPhut);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblNgay, gbc);

        gbc.gridx = 1;
        panel.add(chooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblGio, gbc);

        gbc.gridx = 1;
        panel.add(pnlGioPhut, gbc);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtn.setBackground(Color.WHITE);

        JButton btnOK = new JButton("Chọn");
        JButton btnCancel = new JButton("Hủy");

        pnlBtn.add(btnCancel);
        pnlBtn.add(btnOK);

        dlg.add(panel, BorderLayout.CENTER);
        dlg.add(pnlBtn, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dlg.dispose());

        btnOK.addActionListener(e -> {
            if (chooser.getDate() != null) {
                Date ngay = chooser.getDate();

                int gio = Integer.parseInt(cboGio.getSelectedItem().toString());
                int phut = Integer.parseInt(cboPhut.getSelectedItem().toString());

                java.util.Calendar calNgay = java.util.Calendar.getInstance();
                calNgay.setTime(ngay);
                calNgay.set(java.util.Calendar.HOUR_OF_DAY, gio);
                calNgay.set(java.util.Calendar.MINUTE, phut);
                calNgay.set(java.util.Calendar.SECOND, 0);
                calNgay.set(java.util.Calendar.MILLISECOND, 0);

                thoiGianDaChon = new Timestamp(calNgay.getTimeInMillis());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                txtGioKhachVao.setForeground(Color.BLACK);
                txtGioKhachVao.setText(sdf.format(thoiGianDaChon));

                txtBan.setText(PH_BAN);
                txtBan.setForeground(PLACEHOLDER);
                maBanDuocChon = "";

                loadDanhSachBanTheoGioChon();
                dlg.dispose();
            }
        });

        dlg.setVisible(true);
    }

    private void initPopupBan() {
        popupBan = new JPopupMenu();
        popupBan.setLayout(new BorderLayout());

        modelBan = new DefaultTableModel(new Object[]{"Tên bàn", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBan = new JTable(modelBan);
        tblBan.setRowHeight(28);
        tblBan.setFont(new Font("Arial", Font.PLAIN, 14));
        tblBan.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tblBan);
        scroll.setPreferredSize(new Dimension(360, 180));
        popupBan.add(scroll, BorderLayout.CENTER);

        tblBan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblBan.getSelectedRow();
                if (row >= 0) {
                    txtBan.setForeground(Color.BLACK);
                    txtBan.setText(String.valueOf(modelBan.getValueAt(row, 0)));

                    String tenBanDangChon = String.valueOf(modelBan.getValueAt(row, 0));
                    maBanDuocChon = "";

                    for (String[] ban : dsBanTheoGio) {
                        if (ban[1].equalsIgnoreCase(tenBanDangChon)) {
                            maBanDuocChon = ban[0];
                            break;
                        }
                    }

                    popupBan.setVisible(false);
                }
            }
        });
    }

    private void loadDanhSachBanTheoGioChon() {
        if (modelBan == null) return;

        modelBan.setRowCount(0);
        dsBanTheoGio.clear();

        try {
            Ban_DAO dao = new Ban_DAO();

            if (thoiGianDaChon == null) {
                dsBanTheoGio = dao.getTatCaBanKemTrangThaiMacDinh();
            } else {
                dsBanTheoGio = dao.getDanhSachBanTheoThoiGian(thoiGianDaChon);
            }

            for (String[] ban : dsBanTheoGio) {
                modelBan.addRow(new Object[]{ban[1], ban[2]});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Không tải được danh sách bàn!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void locBan(String keyword) {
        modelBan.setRowCount(0);

        for (String[] ban : dsBanTheoGio) {
            String tenBan = ban[1] == null ? "" : ban[1];

            if (keyword.isEmpty() || tenBan.toLowerCase().contains(keyword.toLowerCase())) {
                modelBan.addRow(new Object[]{ban[1], ban[2]});
            }
        }
    }

    private void showPopupBan() {
        popupBan.show(txtBan, 0, txtBan.getHeight());
    }

    private boolean validateForm() {
        String tenKH = txtKhachHang.getText().trim();
        if (tenKH.isEmpty() || tenKH.equals(PH_KHACH)) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!");
            txtKhachHang.requestFocus();
            return false;
        }

        String sdt = txtSoDienThoai.getText().trim();
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải đúng 10 số!");
            txtSoDienThoai.requestFocus();
            return false;
        }

        String gio = txtGioKhachVao.getText().trim();
        if (gio.isEmpty() || gio.equals(PH_GIO)) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giờ khách vào!");
            return false;
        }

        int soLuong = (int) spnSoLuongKhach.getValue();
        if (soLuong < 1) {
            JOptionPane.showMessageDialog(this, "Số lượng khách phải lớn hơn hoặc bằng 1!");
            return false;
        }

        String ban = txtBan.getText().trim();
        if (ban.isEmpty() || ban.equals(PH_BAN) || maBanDuocChon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn!");
            txtBan.requestFocus();
            return false;
        }

        return true;
    }

    class ColoredButton extends JButton {
        private final Color bgColor;

        public ColoredButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}