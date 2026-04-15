package digLog;

import dao.MonAn_DAO;
import entity.LoaiMonAn;
import entity.MonAn;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import connectDB.ConnectDB;

public class XuLyMonAn_DigLog extends JDialog {

    // ── Chế độ hiển thị ──────────────────────────────────────────────────────
    public enum Mode { THEM, CAP_NHAT, CHI_TIET }

    // ── Màu sắc ──────────────────────────────────────────────────────────────
    private static final Color CLR_HEADER = new Color(74, 55, 40);
    private static final Color CLR_BG     = new Color(248, 244, 238);
    private static final Color CLR_BORDER = new Color(160, 145, 120);
    private static final Color CLR_BTN_LUU  = new Color(100, 181, 246);
    private static final Color CLR_BTN_HUY  = new Color(250, 200, 120);
    private static final Color CLR_IMG_BG   = new Color(220, 215, 205);
    private static final Color CLR_RED_LINE = new Color(200, 60, 60);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, (int)(15 * SCALE));


    // ── Scale ─────────────────────────────────────────────────────────────────
    private static final double SCALE;
    static {
        AffineTransform at = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
        SCALE = at.getScaleX();
    }

    // ── Dữ liệu ──────────────────────────────────────────────────────────────
    private final Mode      mode;
    private final MonAn     monAnCu;    // null khi THEM
    private final String    nextMaMon;  // chỉ dùng khi THEM
    private final MonAn_DAO dao = new MonAn_DAO();
    private       boolean   saved = false;
    private       String    duongDanAnh = "";

    // ── UI fields ─────────────────────────────────────────────────────────────
    private JLabel     lblAnh;
    private JTextField txtMaMon, txtTenMon, txtDonGia;
    private JComboBox<String> cbLoaiMon, cbTrangThai;
    private JTextArea  txtMoTa;

    // ── Danh sách loại món (mã → tên) ────────────────────────────────────────
    private final List<LoaiMonAn> dsLoai = new ArrayList<>();
	private JButton btnThemLoai;

    // =========================================================================
    public XuLyMonAn_DigLog(Frame parent, Mode mode, MonAn monAn, String nextMaMon) {
        super(parent, true);
        this.mode      = mode;
        this.monAnCu   = monAn;
        this.nextMaMon = nextMaMon;

        String title = switch (mode) {
            case THEM      -> "THÊM MÓN";
            case CAP_NHAT  -> "CẬP NHẬT MÓN";
            case CHI_TIET  -> "CHI TIẾT MÓN ĂN";
        };
        setTitle("GD_XuLyMonAn");

        setSize((int)(440 * SCALE), (int)(640 * SCALE));
        setLocationRelativeTo(parent);
        setResizable(false);

        loadLoaiMonFromDB();

        setLayout(new BorderLayout());
        add(buildHeader(title), BorderLayout.NORTH);
        add(buildBody(),        BorderLayout.CENTER);
        add(buildFooter(),      BorderLayout.SOUTH);

        if (monAn != null) fillForm(monAn);
        if (mode == Mode.THEM && nextMaMon != null) txtMaMon.setText(nextMaMon);
        setReadOnly(mode == Mode.CHI_TIET);
    }

    // =========================================================================
    // HEADER
    // =========================================================================
    private JLabel buildHeader(String title) {
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(CLR_HEADER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, (int)(35 * SCALE)));
        lbl.setPreferredSize(new Dimension(0, (int)(56 * SCALE)));
        lbl.setBorder(null);
        return lbl;
    }

    // =========================================================================
    // BODY
    // =========================================================================
    private JScrollPane buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(CLR_BG);
        body.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 3, CLR_RED_LINE),
                BorderFactory.createEmptyBorder(12, 20, 8, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(5, 4, 5, 4);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // ── Ảnh ──────────────────────────────────────────────────────────────
        lblAnh = new JLabel("[chọn ảnh]", SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension((int)(170 * SCALE), (int)(170 * SCALE)));
        lblAnh.setBackground(CLR_IMG_BG);
        lblAnh.setOpaque(true);
        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        lblAnh.setFont(new Font("SansSerif", Font.PLAIN, (int)(12 * SCALE)));
        lblAnh.setForeground(Color.GRAY);
        if (mode != Mode.CHI_TIET) {
            lblAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblAnh.setToolTipText("Nhấn để chọn ảnh");
            lblAnh.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { chonAnh(); }
            });
        }

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1; gbc.weightx = 0;
//        body.add(new JLabel("Ảnh:"), gbc);
        JLabel lblAnhTitle = new JLabel("Ảnh:");
        lblAnhTitle.setFont(LABEL_FONT);
        body.add(lblAnhTitle, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        body.add(lblAnh, gbc);

        // ── Mã món (disable) ─────────────────────────────────────────────────
        txtMaMon = createField();
        txtMaMon.setEnabled(false);
        txtMaMon.setDisabledTextColor(new Color(50, 50, 50));
        addRow(body, gbc, 1, "Mã món:", txtMaMon, null);

        // ── Tên món ───────────────────────────────────────────────────────────
        txtTenMon = createField();
        addRow(body, gbc, 2, "Tên món:", txtTenMon, null);

        // ── Đơn giá + "VND" ──────────────────────────────────────────────────
        txtDonGia = createField();
        JLabel lblVnd = new JLabel("VND");
        lblVnd.setFont(new Font("SansSerif", Font.PLAIN, (int)(13 * SCALE)));

        JPanel giaPanel = new JPanel(new BorderLayout(4, 0));
        giaPanel.setOpaque(false);
        giaPanel.add(txtDonGia, BorderLayout.CENTER);
        giaPanel.add(lblVnd,    BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
//        body.add(new JLabel("Đơn giá:"), gbc);
        JLabel lblGia = new JLabel("Đơn giá:");
        lblGia.setFont(LABEL_FONT);
        body.add(lblGia, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        body.add(giaPanel, gbc);

        // ── Loại món ──────────────────────────────────────────────────────────
        cbLoaiMon = new JComboBox<>();
        btnThemLoai = new JButton("+");
        btnThemLoai.setPreferredSize(new Dimension(40, 30));
        for (LoaiMonAn lm : dsLoai) cbLoaiMon.addItem(lm.getTenLoaiMonAn());
        styleComboBox(cbLoaiMon);
//        addRow(body, gbc, 4, "Loại món:", cbLoaiMon, null);
        JPanel loaiPanel = new JPanel(new BorderLayout(5, 0));
        loaiPanel.setOpaque(false);
        loaiPanel.add(cbLoaiMon, BorderLayout.CENTER);
        loaiPanel.add(btnThemLoai, BorderLayout.EAST);

        addRow(body, gbc, 4, "Loại món:", loaiPanel, null);

        // ── Trạng thái ────────────────────────────────────────────────────────
        cbTrangThai = new JComboBox<>(new String[]{"Đang phục vụ", "Ngừng bán"});
        styleComboBox(cbTrangThai);
        addRow(body, gbc, 5, "Trang thái:", cbTrangThai, null);

        // ── Mô tả ─────────────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
//        body.add(new JLabel("Mô tả :"), gbc);
        JLabel lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setFont(LABEL_FONT);
        body.add(lblMoTa, gbc);

        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setFont(new Font("SansSerif", Font.ITALIC, (int)(13 * SCALE)));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(new Color(235, 230, 220));
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        JScrollPane moTaScroll = new JScrollPane(txtMoTa);
        moTaScroll.setBorder(null);
        moTaScroll.setPreferredSize(new Dimension(0, (int)(100 * SCALE)));

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        body.add(moTaScroll, gbc);

        // Filler
        gbc.gridy = 7; gbc.weighty = 1;
        body.add(new JLabel(), gbc);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        
        btnThemLoai.addActionListener(e -> {
            String tenLoai = JOptionPane.showInputDialog(this, "Nhập tên loại món:");

            if (tenLoai == null || tenLoai.trim().isEmpty()) return;

            dao.LoaiMonAn_DAO loaiDAO = new dao.LoaiMonAn_DAO();

            entity.LoaiMonAn loai = new entity.LoaiMonAn(null, tenLoai);

            String maMoi = loaiDAO.themLoaiMonAnTraMa(loai);

            if (maMoi != null) {
                loai.setMaLoaiMonAn(maMoi);

                cbLoaiMon.addItem(loai.getTenLoaiMonAn());

                // 🔥 reload tab ngoài
                ((gui.ThucDon_GUI) getParent()).reloadTabs();

                JOptionPane.showMessageDialog(this, "Thêm loại thành công!");
            }
        });
        return scroll;
        
        
    }

    // =========================================================================
    // FOOTER – nút
    // =========================================================================
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footer.setBackground(CLR_BG);
        footer.setBorder(BorderFactory.createMatteBorder(0, 3, 3, 3, CLR_RED_LINE));

        if (mode == Mode.CHI_TIET) {
            JButton btnDong = createBtn("✕ Đóng", CLR_BTN_HUY);
            btnDong.addActionListener(e -> dispose());
            footer.add(btnDong);

        } else {
            JButton btnLuu = createBtn("☑ Lưu", CLR_BTN_LUU);
            JButton btnHuy = createBtn("✕ Hủy", CLR_BTN_HUY);
            btnLuu.addActionListener(e -> save());
            btnHuy.addActionListener(e -> dispose());
            footer.add(btnLuu);
            footer.add(btnHuy);
        }

        return footer;
    }

    // =========================================================================
    // FILL FORM từ MonAn
    // =========================================================================
    private void fillForm(MonAn mon) {
        txtMaMon.setText(mon.getMaMon());
        txtTenMon.setText(mon.getTenMon());
        txtDonGia.setText(String.format("%,.0f", mon.getDonGia()).replace(",", "."));
        txtMoTa.setText(mon.getMoTa() != null ? mon.getMoTa() : "");

        // Loại món
        for (int i = 0; i < dsLoai.size(); i++) {
            if (dsLoai.get(i).getMaLoaiMonAn().equals(mon.getMaLoaiMonAn().getMaLoaiMonAn())) {
                cbLoaiMon.setSelectedIndex(i);
                break;
            }
        }

        // Trạng thái
        cbTrangThai.setSelectedItem(mon.isTrangThai() ? "Đang phục vụ" : "Ngừng bán");

        // Ảnh
        duongDanAnh = mon.getAnhMon() != null ? mon.getAnhMon() : "";
        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {

            String name = duongDanAnh;

            // bỏ extension nếu có
            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf("."));
            }

            String baseDir = System.getProperty("user.dir") + File.separator + "img";

            String[] exts = {".png", ".jpg", ".jpeg"};

            for (String ext : exts) {
                File file = new File(baseDir + File.separator + name + ext);

                if (file.exists()) {
                    int sz = (int)(160 * SCALE);
                    Image img = new ImageIcon(file.getAbsolutePath())
                            .getImage()
                            .getScaledInstance(sz, sz, Image.SCALE_SMOOTH);

                    lblAnh.setIcon(new ImageIcon(img));
                    lblAnh.setText("");
                    break;
                }
            }
        }
    }

    // =========================================================================
    // LƯU
    // =========================================================================
    private void save() {
        // Validate
        String tenMon = txtTenMon.getText().trim();
        String giaStr = txtDonGia.getText().trim().replace(".", "");

        if (tenMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên món!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtTenMon.requestFocus();
            return;
        }

        double donGia;
        try {
            donGia = Double.parseDouble(giaStr);
            if (donGia < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtDonGia.requestFocus();
            return;
        }

        LoaiMonAn loai = dsLoai.get(cbLoaiMon.getSelectedIndex());
        boolean trangThai = cbTrangThai.getSelectedItem().toString().equals("Đang phục vụ");
        String moTa = txtMoTa.getText().trim();

        boolean ok = false;

        if (mode == Mode.THEM) {

            String maMon = txtMaMon.getText(); // đã set từ getNextMaMon()

            MonAn mon = new MonAn(maMon, loai, tenMon, duongDanAnh, donGia, moTa, trangThai);

            ok = dao.themMonAn(mon); // ✅ chỉ dùng cái này

        } else {

            MonAn mon = new MonAn(
                    txtMaMon.getText().trim(),
                    loai,
                    tenMon,
                    duongDanAnh,
                    donGia,
                    moTa,
                    trangThai
            );

            ok = dao.capNhatMonAn(mon);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    mode == Mode.THEM ? "Thêm món thành công!" : "Cập nhật thành công!");
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    // =========================================================================
    // CHỌN ẢNH (copy vào img/ giống NhanVien_GUI)
    // =========================================================================
    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/img"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Ảnh (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                File folder = new File(System.getProperty("user.dir") + "/img");
                if (!folder.exists()) folder.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getName();
                File dest = new File(folder, fileName);
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                duongDanAnh = fileName;

                int sz = (int)(160 * SCALE);
                Image img = new ImageIcon(dest.getAbsolutePath())
                        .getImage().getScaledInstance(sz, sz, Image.SCALE_SMOOTH);
                lblAnh.setIcon(new ImageIcon(img));
                lblAnh.setText("");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi chọn ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // LOAD LOẠI MÓN TỪ DB
    // =========================================================================
    private void loadLoaiMonFromDB() {
        dsLoai.clear();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT maLoaiMonAn, tenLoaiMonAn FROM LoaiMonAn ORDER BY maLoaiMonAn";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dsLoai.add(new LoaiMonAn(rs.getString(1), rs.getString(2)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // READ-ONLY mode (CHI_TIET)
    // =========================================================================
    private void setReadOnly(boolean ro) {
        txtTenMon.setEditable(!ro);
        txtDonGia.setEditable(!ro);
        cbLoaiMon.setEnabled(!ro);
        cbTrangThai.setEnabled(!ro);
        txtMoTa.setEditable(!ro);
        btnThemLoai.setEnabled(!ro);
        if (ro) {
            txtTenMon.setBackground(new Color(235, 230, 220));
            txtDonGia.setBackground(new Color(235, 230, 220));
            txtMoTa.setBackground(new Color(235, 230, 220));
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================
    private JTextField createField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, (int)(13 * SCALE)));
        tf.setPreferredSize(new Dimension(0, (int)(30 * SCALE)));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, (int)(13 * SCALE)));
        cb.setBackground(Color.WHITE);
        cb.setFocusable(false);
        cb.setPreferredSize(new Dimension(0, (int)(30 * SCALE)));
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String labelText, JComponent comp, Object unused) {
        gbc.gridy = row; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(labelText);
//        lbl.setFont(new Font("SansSerif", Font.PLAIN, (int)(13 * SCALE)));
        lbl.setFont(LABEL_FONT);
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        p.add(comp, gbc);
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, (int)(13 * SCALE)));
        btn.setForeground(new Color(30, 30, 30));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension((int)(110 * SCALE), (int)(36 * SCALE)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean isSaved() { return saved; }
}
