package digLog;

import dao.MonAn_DAO;
import entity.LoaiMonAn;
import entity.MonAn;
import connectDB.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XuLyMonAn_DigLog extends JDialog {
    public enum Mode { THEM, CAP_NHAT, CHI_TIET }

    private static final double SCALE = 1.0;

    private static final Color CLR_HEADER = new Color(74, 55, 40);
    private static final Color CLR_BG = new Color(248, 244, 238);
    private static final Color CLR_FIELD = Color.WHITE;
    private static final Color CLR_BORDER = new Color(205, 195, 178);
    private static final Color CLR_BTN_LUU = new Color(100, 181, 246);
    private static final Color CLR_BTN_HUY = new Color(250, 200, 120);
    private static final Color CLR_IMG_BG = new Color(224, 218, 207);

    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 16);
    private final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 15);

    private final Mode mode;
    private final MonAn monAnCu;
    private final String nextMaMon;
    private final MonAn_DAO dao = new MonAn_DAO();
    private String anhGoc = "";

    
    private gui.ThucDon_GUI parentGUI;

    private boolean saved = false;
    private String duongDanAnh = "";

    private JLabel lblAnh;
    private JTextField txtMaMon, txtTenMon, txtGiaGoc, txtDonGia;
    private JComboBox<String> cbLoaiMon, cbTrangThai;
    private JComboBox<String> cboDonViTinh;
    private JTextArea txtMoTa;
    private JButton btnThemLoai;

    private final List<LoaiMonAn> dsLoai = new ArrayList<>();

	private String donViTinh;


    public XuLyMonAn_DigLog(Frame parent,gui.ThucDon_GUI parentGUI, Mode mode, MonAn monAn, String nextMaMon) {
        super(parent, true);
        this.mode = mode;
        this.monAnCu = monAn;
        this.nextMaMon = nextMaMon;
        this.parentGUI = parentGUI;

        String title = switch (mode) {
            case THEM -> "THÊM MÓN";
            case CAP_NHAT -> "CẬP NHẬT MÓN";
            case CHI_TIET -> "CHI TIẾT MÓN";
        };

        setTitle("GD_XuLyMonAn");
        setLayout(new BorderLayout());
        setResizable(false);

        loadLoaiMonFromDB();

        add(buildHeader(title), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        if (monAn != null) fillForm(monAn);
        if (mode == Mode.THEM && nextMaMon != null) txtMaMon.setText(nextMaMon);

        setReadOnly(mode == Mode.CHI_TIET);
        setMinimumSize(new Dimension(500, 800));
        setMaximumSize(new Dimension(1200, 800));
        setSize(600, 780);
        setLocationRelativeTo(parent);
    }

    private JLabel buildHeader(String title) {
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(CLR_HEADER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 34));
        lbl.setPreferredSize(new Dimension(0, 66));
        return lbl;
    }

    private JScrollPane buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(CLR_BG);
        body.setBorder(new EmptyBorder(16, 22, 12, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblAnh = new JLabel("[chọn ảnh]", SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(250, 145));
        lblAnh.setBackground(CLR_IMG_BG);
        lblAnh.setOpaque(true);
        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        lblAnh.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblAnh.setForeground(Color.GRAY);

        if (mode != Mode.CHI_TIET) {
            lblAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblAnh.setToolTipText("Nhấn để chọn ảnh");
            lblAnh.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chonAnh();
                }
            });
        }

        addRow(body, gbc, 0, "Ảnh:", lblAnh);

        txtMaMon = createField();
        txtMaMon.setEnabled(false);
        txtMaMon.setDisabledTextColor(new Color(40, 40, 40));
        addRow(body, gbc, 1, "Mã món:", txtMaMon);

        txtTenMon = createField();
        addRow(body, gbc, 2, "Tên món:", txtTenMon);

        txtGiaGoc = createField();
        JLabel lblVnd1 = new JLabel("VND");
        lblVnd1.setFont(LABEL_FONT);

        JPanel giaGocPanel = new JPanel(new BorderLayout(8, 0));
        giaGocPanel.setOpaque(false);
        giaGocPanel.add(txtGiaGoc, BorderLayout.CENTER);
        giaGocPanel.add(lblVnd1, BorderLayout.EAST);
        addRow(body, gbc, 3, "Giá gốc:", giaGocPanel);

        txtDonGia = createField();
        txtDonGia.setEditable(false);
        txtDonGia.setBackground(new Color(235, 230, 220));
        

        JLabel lblVnd2 = new JLabel("VND");
        lblVnd2.setFont(LABEL_FONT);

        JPanel donGiaPanel = new JPanel(new BorderLayout(8, 0));
        donGiaPanel.setOpaque(false);
        donGiaPanel.add(txtDonGia, BorderLayout.CENTER);
        donGiaPanel.add(lblVnd2, BorderLayout.EAST);
        addRow(body, gbc, 4, "Giá bán:", donGiaPanel);
        
        cboDonViTinh = new JComboBox<>(new String[]{"phần","tô","đĩa","nồi","ly","lon","chai",
                "cái","con","chén","ổ"});

        cboDonViTinh.setEditable(true);

        styleComboBox(cboDonViTinh);

        addRow(body, gbc, 5, "Đơn vị tính:", cboDonViTinh);

        txtGiaGoc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateDonGia() {
                try {
                    String raw = txtGiaGoc.getText().trim().replace(".", "").replace(",", "");
                    if (raw.isEmpty()) {
                        txtDonGia.setText("");
                        return;
                    }

                    double giaGoc = Double.parseDouble(raw);
                    double donGia = giaGoc * 1.4;

                    txtDonGia.setText(String.format("%,.0f", donGia).replace(",", "."));
                } catch (Exception e) {
                    txtDonGia.setText("");
                }
            }

            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateDonGia(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateDonGia(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateDonGia(); }
        });

        cbLoaiMon = new JComboBox<>();

//        btnThemLoai = new JButton("+");
//        btnThemLoai.setPreferredSize(new Dimension(40, 30));
        btnThemLoai = new JButton();
        java.net.URL imgURL = getClass().getResource("/cn_them.png");
        if (imgURL != null) {
            ImageIcon iconPlus = new ImageIcon(imgURL);
            Image img = iconPlus.getImage()
                            .getScaledInstance(
                                    18,
                                    18,
                                    Image.SCALE_SMOOTH
                            );
            btnThemLoai.setIcon(
                    new ImageIcon(img)
            );
        }
        btnThemLoai.setPreferredSize(new Dimension(50, 30));

        btnThemLoai.setFocusPainted(false);
        btnThemLoai.setContentAreaFilled(false);
        btnThemLoai.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        btnThemLoai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        for (LoaiMonAn lm : dsLoai) cbLoaiMon.addItem(lm.getTenLoaiMonAn());
        styleComboBox(cbLoaiMon);

//        btnThemLoai = new JButton("+");
//        btnThemLoai.setFont(new Font("SansSerif", Font.BOLD, 16));
//        btnThemLoai.setFocusPainted(false);
//        btnThemLoai.setPreferredSize(new Dimension(42, 34));

        JPanel loaiPanel = new JPanel(new BorderLayout(6, 0));
        loaiPanel.setOpaque(false);
        loaiPanel.add(cbLoaiMon, BorderLayout.CENTER);
        loaiPanel.add(btnThemLoai, BorderLayout.EAST);
        addRow(body, gbc, 6, "Loại món:", loaiPanel);

        cbTrangThai = new JComboBox<>(new String[]{"Đang phục vụ", "Ngừng bán"});
        styleComboBox(cbTrangThai);
        addRow(body, gbc, 7, "Trạng thái:", cbTrangThai);

        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setFont(new Font("SansSerif", Font.ITALIC, 14));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(CLR_FIELD);
        txtMoTa.setBorder(new EmptyBorder(7, 9, 7, 9));

        JScrollPane moTaScroll = new JScrollPane(txtMoTa);
        moTaScroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        moTaScroll.setPreferredSize(new Dimension(0, 95));
        addRow(body, gbc, 8, "Mô tả:", moTaScroll);

        gbc.gridy = 8;
        gbc.gridx = 1;
        gbc.weighty = 1;
        body.add(Box.createVerticalGlue(), gbc);

//        btnThemLoai.addActionListener(e -> themLoaiMonMoi());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CLR_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        
        btnThemLoai.addActionListener(e -> {
            dao.LoaiMonAn_DAO loaiDAO = new dao.LoaiMonAn_DAO();

            while (true) {
                String tenLoai = JOptionPane.showInputDialog(
                        this,
                        "Nhập tên loại món:"
                );

                // bấm Cancel
                if (tenLoai == null) {
                    return;
                }

                tenLoai = tenLoai.trim();

                // nhập rỗng
                if (tenLoai.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Tên loại món không được để trống!",
                            "Lỗi nhập liệu",
                            JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                // kiểm tra trùng
                boolean biTrung = false;

                for (LoaiMonAn lm : dsLoai) {
                    if (lm.getTenLoaiMonAn()
                            .trim()
                            .equalsIgnoreCase(tenLoai)) {
                        biTrung = true;
                        break;
                    }
                }

                if (biTrung) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Tên loại món đã tồn tại!\nVui lòng nhập tên khác.",
                            "Trùng dữ liệu",
                            JOptionPane.WARNING_MESSAGE
                    );
                    continue; // quay lại nhập tiếp
                }

                // hợp lệ → thêm mới
                entity.LoaiMonAn loai =
                        new entity.LoaiMonAn(null, tenLoai);

                String maMoi =
                        loaiDAO.themLoaiMonAnTraMa(loai);

                if (maMoi != null) {
                    loai.setMaLoaiMonAn(maMoi);

                    loadLoaiMonFromDB();

                    cbLoaiMon.removeAllItems();

                    for (LoaiMonAn lm : dsLoai) {
                        cbLoaiMon.addItem(lm.getTenLoaiMonAn());
                    }

                    cbLoaiMon.setSelectedItem(loai.getTenLoaiMonAn());

                    if (parentGUI != null) {
                        parentGUI.reloadTabs();
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            "Thêm loại món thành công!"
                    );
                    return ; 
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Không thể thêm loại món!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                break; // thoát vòng lặp sau khi thêm thành công
            }
        });
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 12));
        footer.setBackground(CLR_BG);
        footer.setBorder(new EmptyBorder(0, 16, 12, 16));

        if (mode == Mode.CHI_TIET) {
            JButton btnDong = createBtn("Đóng", CLR_BTN_HUY);
            btnDong.addActionListener(e -> dispose());
            footer.add(btnDong);
        } else {
            JButton btnLuu = createBtn("Lưu", CLR_BTN_LUU);
            JButton btnHuy = createBtn("Hủy", CLR_BTN_HUY);
            btnLuu.addActionListener(e -> save());
            btnHuy.addActionListener(e -> dispose());
            footer.add(btnLuu);
            footer.add(btnHuy);
        }

        return footer;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String labelText, JComponent comp) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = row == 6 ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST;

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setPreferredSize(new Dimension(120, 34));
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = row == 6 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;

        p.add(comp, gbc);
    }

    private JTextField createField() {
        JTextField tf = new JTextField();
        tf.setFont(FIELD_FONT);
        tf.setPreferredSize(new Dimension(0, 34));
        tf.setBackground(CLR_FIELD);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                new EmptyBorder(5, 9, 5, 9)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FIELD_FONT);
        cb.setBackground(Color.WHITE);
        cb.setForeground(new Color(35, 35, 35));
        cb.setFocusable(false);
        cb.setPreferredSize(new Dimension(0, 38));
        cb.setBorder(BorderFactory.createLineBorder(new Color(185, 178, 165), 1));
        cb.setOpaque(true);

        cb.setUI(new BasicComboBoxUI() {
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

                btn.setPreferredSize(new Dimension(38, 38));
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(Color.WHITE);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c = getModel().isPressed() ? bg.darker() : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(new Color(30, 30, 30));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void themLoaiMonMoi() {
        String tenLoai = JOptionPane.showInputDialog(this, "Nhập tên loại món:");
        if (tenLoai == null || tenLoai.trim().isEmpty()) return;

        dao.LoaiMonAn_DAO loaiDAO = new dao.LoaiMonAn_DAO();
        LoaiMonAn loai = new LoaiMonAn(null, tenLoai.trim());

        String maMoi = loaiDAO.themLoaiMonAnTraMa(loai);

        if (maMoi != null) {
            loai.setMaLoaiMonAn(maMoi);
            dsLoai.add(loai);

            cbLoaiMon.addItem(loai.getTenLoaiMonAn());
            cbLoaiMon.setSelectedItem(loai.getTenLoaiMonAn());

            JOptionPane.showMessageDialog(this, "Thêm loại thành công!");
        }
    }

//    private void fillForm(MonAn mon) {
//        txtMaMon.setText(mon.getMaMon());
//        txtTenMon.setText(mon.getTenMon());
//        txtGiaGoc.setText(String.format("%,.0f", mon.getGiaGoc()).replace(",", "."));
//        txtDonGia.setText(String.format("%,.0f", mon.getDonGia()).replace(",", "."));
//        cboDonViTinh.setSelectedItem(mon.getDonViTinh());
//        txtMoTa.setText(mon.getMoTa() != null ? mon.getMoTa() : "");
//
//
//        // Loại món
//        for (int i = 0; i < dsLoai.size(); i++) {
//            if (dsLoai.get(i).getMaLoaiMonAn().equals(mon.getMaLoaiMonAn().getMaLoaiMonAn())) {
//                cbLoaiMon.setSelectedIndex(i);
//                break;
//            }
//        }
//
//        // Trạng thái
//        cbTrangThai.setSelectedItem(mon.isTrangThai() ? "Đang phục vụ" : "Ngừng bán");
//
//        // Ảnh
//        duongDanAnh = mon.getAnhMon() != null ? mon.getAnhMon() : "";
//        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
//
//            String name = duongDanAnh;
//
//            if (name.contains(".")) {
//                name = name.substring(0, name.lastIndexOf("."));
//            }
//
//            String baseDir = System.getProperty("user.dir") + File.separator + "img";
//
//            String[] exts = {".png", ".jpg", ".jpeg"};
//
//            for (String ext : exts) {
//                File file = new File(baseDir + File.separator + name + ext);
//
//                if (file.exists()) {
//                    int sz = (int)(160 * SCALE);
//                    Image img = new ImageIcon(file.getAbsolutePath())
//                            .getImage()
//                            .getScaledInstance(sz, sz, Image.SCALE_SMOOTH);
//
//                    lblAnh.setIcon(new ImageIcon(img));
//                    lblAnh.setText("");
//                    break;
//                }
//            }
//        }
//
//        cbTrangThai.setSelectedItem(mon.isTrangThai() ? "Đang phục vụ" : "Ngừng bán");
//
//        duongDanAnh = mon.getAnhMon() != null ? mon.getAnhMon() : "";
//        hienThiAnhTuTenFile(duongDanAnh);
//    }
    private void fillForm(MonAn mon) {
        txtMaMon.setText(mon.getMaMon());
        txtTenMon.setText(mon.getTenMon());
        txtGiaGoc.setText(String.format("%,.0f", mon.getGiaGoc()).replace(",", "."));
        txtDonGia.setText(String.format("%,.0f", mon.getDonGia()).replace(",", "."));
        cboDonViTinh.setSelectedItem(mon.getDonViTinh());
        txtMoTa.setText(mon.getMoTa() != null ? mon.getMoTa() : "");

        for (int i = 0; i < dsLoai.size(); i++) {
            if (dsLoai.get(i).getMaLoaiMonAn().equals(mon.getMaLoaiMonAn().getMaLoaiMonAn())) {
                cbLoaiMon.setSelectedIndex(i);
                break;
            }
        }

        cbTrangThai.setSelectedItem(mon.isTrangThai() ? "Đang phục vụ" : "Ngừng bán");

        anhGoc = mon.getAnhMon() != null ? mon.getAnhMon().trim() : "";
        duongDanAnh = "";
        hienThiAnhTuTenFile(anhGoc);
    }

    private void hienThiAnhTuTenFile(String tenAnh) {

        lblAnh.setIcon(null);
        lblAnh.setText("[chọn ảnh]");

        if (tenAnh == null || tenAnh.trim().isEmpty()) {
            return;
        }

        try {

            String fileName = tenAnh.trim();

            // nếu DB lỡ lưu img/abc.png
            fileName = fileName.replace("img/", "");

            java.net.URL imgURL =
                    getClass().getResource(
                            "/" + fileName
                    );

            if (imgURL != null) {

                Image img =
                        new ImageIcon(imgURL)
                                .getImage()
                                .getScaledInstance(
                                        230,
                                        135,
                                        Image.SCALE_SMOOTH
                                );

                lblAnh.setIcon(
                        new ImageIcon(img)
                );

                lblAnh.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chonAnh() {

        JFileChooser fc = new JFileChooser();

        String baseDir =
                System.getProperty("user.dir")
                        + File.separator
                        + "img";

        File imgFolder = new File(baseDir);

        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }

        fc.setCurrentDirectory(imgFolder);

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Ảnh (*.png, *.jpg, *.jpeg, *.gif)",
                        "png",
                        "jpg",
                        "jpeg",
                        "gif"
                )
        );

        fc.setAcceptAllFileFilterUsed(false);

        int result = fc.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fc.getSelectedFile();

        if (selectedFile == null || !selectedFile.exists()) {
            return;
        }

        try {

            String tenFile = selectedFile.getName();

            File destFile = new File(imgFolder, tenFile);

            // copy nếu file chưa nằm trong img
            if (!selectedFile.getAbsolutePath()
                    .equalsIgnoreCase(destFile.getAbsolutePath())) {

                Files.copy(
                        selectedFile.toPath(),
                        destFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            // chỉ lưu tên file vào DB
            duongDanAnh = destFile.getName();
            setAnhPreview(destFile);

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi chọn ảnh:\n" + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

//    private void hienThiAnhTuTenFile(String tenAnh) {
//        if (tenAnh == null || tenAnh.trim().isEmpty()) return;
//
//        String name = tenAnh.trim();
//        if (name.contains(".")) {
//            name = name.substring(0, name.lastIndexOf("."));
//        }
//
//        String baseDir = System.getProperty("user.dir") + File.separator + "img";
//        String[] exts = {".png", ".jpg", ".jpeg", ".gif"};
//
//        for (String ext : exts) {
//            File file = new File(baseDir + File.separator + name + ext);
//            if (file.exists()) {
//                setAnhPreview(file);
//                break;
//            }
//        }
//    }
    

    private void setAnhPreview(File file) {
        Image img = new ImageIcon(file.getAbsolutePath())
                .getImage()
                .getScaledInstance(230, 135, Image.SCALE_SMOOTH);

        lblAnh.setIcon(new ImageIcon(img));
        lblAnh.setText("");
    }

//    private void save() {
//        String tenMon = txtTenMon.getText().trim();
//        String giaStr = txtGiaGoc.getText().trim().replace(".", "").replace(",", "");
//        String donViTinh =
//                cboDonViTinh
//                .getEditor()
//                .getItem()
//                .toString()
//                .trim();
//        if (tenMon.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên món!", "Lỗi", JOptionPane.WARNING_MESSAGE);
//            txtTenMon.requestFocus();
//            return;
//        }
//
//        double giaGoc;
//        try {
//            giaGoc = Double.parseDouble(giaStr);
//            if (giaGoc < 0) throw new NumberFormatException();
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Giá gốc không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
//            txtGiaGoc.requestFocus();
//            return;
//        }
//
//        if (cbLoaiMon.getSelectedIndex() < 0 || cbLoaiMon.getSelectedIndex() >= dsLoai.size()) {
//            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại món!");
//            return;
//        }
//
//        LoaiMonAn loai = dsLoai.get(cbLoaiMon.getSelectedIndex());
//        boolean trangThai = cbTrangThai.getSelectedItem().toString().equals("Đang phục vụ");
//
//        MonAn mon = new MonAn(
//                txtMaMon.getText().trim(),
//                loai,
//                tenMon,
//                duongDanAnh,
//                donViTinh,
//                giaGoc,
//                giaGoc * 1.4,
//                txtMoTa.getText().trim(),
//                trangThai
//        );
//
//        boolean ok = mode == Mode.THEM ? dao.themMonAn(mon) : dao.capNhatMonAn(mon);
//
//        if (ok) {
//            JOptionPane.showMessageDialog(this,
//                    mode == Mode.THEM ? "Thêm món thành công!" : "Cập nhật thành công!");
//            saved = true;
//            dispose();
//        } else {
//            JOptionPane.showMessageDialog(this, "Lưu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    private void save() {

        String tenMon = txtTenMon.getText().trim();

        String giaStr = txtGiaGoc.getText()
                .trim()
                .replace(".", "")
                .replace(",", "");

        String donViTinh = cboDonViTinh
                .getEditor()
                .getItem()
                .toString()
                .trim();

        // validate tên món
        if (tenMon.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tên món!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE
            );

            txtTenMon.requestFocus();
            return;
        }

        // validate giá
        double giaGoc;

        try {

            giaGoc = Double.parseDouble(giaStr);

            if (giaGoc < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Giá gốc không hợp lệ!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE
            );

            txtGiaGoc.requestFocus();
            return;
        }

        // validate loại món
        if (cbLoaiMon.getSelectedIndex() < 0
                || cbLoaiMon.getSelectedIndex() >= dsLoai.size()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn loại món!"
            );

            return;
        }

        int selectedIndex = cbLoaiMon.getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= dsLoai.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Loại món không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LoaiMonAn loai = dsLoai.get(selectedIndex);

        boolean trangThai =
                cbTrangThai.getSelectedItem()
                        .toString()
                        .equals("Đang phục vụ");

        // ảnh để lưu
        String anhDeLuu = (duongDanAnh != null && !duongDanAnh.trim().isEmpty())
                ? duongDanAnh
                : anhGoc;

        if (duongDanAnh != null
                && !duongDanAnh.trim().isEmpty()) {

            // có chọn ảnh mới
            anhDeLuu = duongDanAnh;

        } else {

            // giữ ảnh cũ
            anhDeLuu = anhGoc;
        }

        MonAn mon = new MonAn(
        	    txtMaMon.getText().trim(),
        	    loai,
        	    tenMon,
        	    donViTinh,
        	    anhDeLuu,
        	    giaGoc,
        	    giaGoc * 1.4,
        	    txtMoTa.getText().trim(),
        	    trangThai
        	);

        boolean ok;

        if (mode == Mode.THEM) {
            ok = dao.themMonAn(mon);
        } else {
            ok = dao.capNhatMonAn(mon);
        }

        if (ok) {

            JOptionPane.showMessageDialog(
                    this,
                    mode == Mode.THEM
                            ? "Thêm món thành công!"
                            : "Cập nhật thành công!"
            );

            saved = true;

            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Lưu thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    

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

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setReadOnly(boolean ro) {
        txtTenMon.setEditable(!ro);
        txtGiaGoc.setEditable(!ro);
        txtDonGia.setEditable(false);
        cbLoaiMon.setEnabled(!ro);
        cbTrangThai.setEnabled(!ro);
        cboDonViTinh.setEnabled(!ro);
        txtMoTa.setEditable(!ro);
        btnThemLoai.setEnabled(!ro);

        if (ro) {
            txtTenMon.setBackground(new Color(235, 230, 220));
            txtGiaGoc.setBackground(new Color(235, 230, 220));
            txtDonGia.setBackground(new Color(235, 230, 220));
            txtMoTa.setBackground(new Color(235, 230, 220));
        }
    }

    public boolean isSaved() {
        return saved;
    }


//    private void styleComboBox(JComboBox<?> cb) {
//        cb.setFont(new Font("SansSerif", Font.PLAIN, (int)(13 * SCALE)));
//        cb.setBackground(Color.WHITE);
//        cb.setFocusable(false);
//        cb.setPreferredSize(new Dimension(0, (int)(30 * SCALE)));
//        cb.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(CLR_BORDER),
//                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
//    }
//    private void styleComboBox(JComboBox<?> cb) {
//        cb.setFont(new Font("SansSerif", Font.PLAIN, (int)(13 * SCALE)));
//        cb.setBackground(Color.WHITE);
//        cb.setFocusable(false);
//        cb.setPreferredSize(new Dimension(0, (int)(30 * SCALE)));
//        cb.setBorder(BorderFactory.createEmptyBorder());
//
//        cb.setOpaque(true);
//        cb.setLightWeightPopupEnabled(false);
//    }

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

//    private JButton createBtn(String text, Color bg) {
//        JButton btn = new JButton(text) {
//            @Override protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
//                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
//                g2.dispose();
//                super.paintComponent(g);
//            }
//        };
//        btn.setFont(new Font("SansSerif", Font.BOLD, (int)(13 * SCALE)));
//        btn.setForeground(new Color(30, 30, 30));
//        btn.setContentAreaFilled(false);
//        btn.setBorderPainted(false);
//        btn.setFocusPainted(false);
//        btn.setPreferredSize(new Dimension((int)(110 * SCALE), (int)(36 * SCALE)));
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        return btn;
//    }

//    public boolean isSaved() { return saved; }
}
