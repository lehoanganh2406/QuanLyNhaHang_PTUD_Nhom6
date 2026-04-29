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

    private boolean saved = false;
    private String duongDanAnh = "";

    private JLabel lblAnh;
    private JTextField txtMaMon, txtTenMon, txtDonGia;
    private JComboBox<String> cbLoaiMon, cbTrangThai;
    private JTextArea txtMoTa;
    private JButton btnThemLoai;

    private final List<LoaiMonAn> dsLoai = new ArrayList<>();

    public XuLyMonAn_DigLog(Frame parent, Mode mode, MonAn monAn, String nextMaMon) {
        super(parent, true);
        this.mode = mode;
        this.monAnCu = monAn;
        this.nextMaMon = nextMaMon;

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

        setSize(500, 720);
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

        txtDonGia = createField();
        JLabel lblVnd = new JLabel("VND");
        lblVnd.setFont(LABEL_FONT);

        JPanel giaPanel = new JPanel(new BorderLayout(8, 0));
        giaPanel.setOpaque(false);
        giaPanel.add(txtDonGia, BorderLayout.CENTER);
        giaPanel.add(lblVnd, BorderLayout.EAST);
        addRow(body, gbc, 3, "Đơn giá:", giaPanel);

        cbLoaiMon = new JComboBox<>();
        for (LoaiMonAn lm : dsLoai) cbLoaiMon.addItem(lm.getTenLoaiMonAn());
        styleComboBox(cbLoaiMon);

        btnThemLoai = new JButton("+");
        btnThemLoai.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnThemLoai.setFocusPainted(false);
        btnThemLoai.setPreferredSize(new Dimension(42, 34));

        JPanel loaiPanel = new JPanel(new BorderLayout(6, 0));
        loaiPanel.setOpaque(false);
        loaiPanel.add(cbLoaiMon, BorderLayout.CENTER);
        loaiPanel.add(btnThemLoai, BorderLayout.EAST);
        addRow(body, gbc, 4, "Loại món:", loaiPanel);

        cbTrangThai = new JComboBox<>(new String[]{"Đang phục vụ", "Ngừng bán"});
        styleComboBox(cbTrangThai);
        addRow(body, gbc, 5, "Trạng thái:", cbTrangThai);

        txtMoTa = new JTextArea(4, 20);
        txtMoTa.setFont(new Font("SansSerif", Font.ITALIC, 14));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBackground(CLR_FIELD);
        txtMoTa.setBorder(new EmptyBorder(7, 9, 7, 9));

        JScrollPane moTaScroll = new JScrollPane(txtMoTa);
        moTaScroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        moTaScroll.setPreferredSize(new Dimension(0, 95));
        addRow(body, gbc, 6, "Mô tả:", moTaScroll);

        gbc.gridy = 7;
        gbc.gridx = 1;
        gbc.weighty = 1;
        body.add(Box.createVerticalGlue(), gbc);

        btnThemLoai.addActionListener(e -> themLoaiMonMoi());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CLR_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
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

    private void fillForm(MonAn mon) {
        txtMaMon.setText(mon.getMaMon());
        txtTenMon.setText(mon.getTenMon());
        txtDonGia.setText(String.format("%,.0f", mon.getDonGia()).replace(",", "."));
        txtMoTa.setText(mon.getMoTa() != null ? mon.getMoTa() : "");

        if (mon.getMaLoaiMonAn() != null) {
            for (int i = 0; i < dsLoai.size(); i++) {
                if (dsLoai.get(i).getMaLoaiMonAn().equals(mon.getMaLoaiMonAn().getMaLoaiMonAn())) {
                    cbLoaiMon.setSelectedIndex(i);
                    break;
                }
            }
        }

        cbTrangThai.setSelectedItem(mon.isTrangThai() ? "Đang phục vụ" : "Ngừng bán");

        duongDanAnh = mon.getAnhMon() != null ? mon.getAnhMon() : "";
        hienThiAnhTuTenFile(duongDanAnh);
    }

    private void hienThiAnhTuTenFile(String tenAnh) {
        if (tenAnh == null || tenAnh.trim().isEmpty()) return;

        String name = tenAnh.trim();
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }

        String baseDir = System.getProperty("user.dir") + File.separator + "img";
        String[] exts = {".png", ".jpg", ".jpeg", ".gif"};

        for (String ext : exts) {
            File file = new File(baseDir + File.separator + name + ext);
            if (file.exists()) {
                setAnhPreview(file);
                break;
            }
        }
    }

    private void setAnhPreview(File file) {
        Image img = new ImageIcon(file.getAbsolutePath())
                .getImage()
                .getScaledInstance(230, 135, Image.SCALE_SMOOTH);

        lblAnh.setIcon(new ImageIcon(img));
        lblAnh.setText("");
    }

    private void save() {
        String tenMon = txtTenMon.getText().trim();
        String giaStr = txtDonGia.getText().trim().replace(".", "").replace(",", "");

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

        if (cbLoaiMon.getSelectedIndex() < 0 || cbLoaiMon.getSelectedIndex() >= dsLoai.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại món!");
            return;
        }

        LoaiMonAn loai = dsLoai.get(cbLoaiMon.getSelectedIndex());
        boolean trangThai = cbTrangThai.getSelectedItem().toString().equals("Đang phục vụ");

        MonAn mon = new MonAn(
                txtMaMon.getText().trim(),
                loai,
                tenMon,
                duongDanAnh,
                donGia,
                txtMoTa.getText().trim(),
                trangThai
        );

        boolean ok = mode == Mode.THEM ? dao.themMonAn(mon) : dao.capNhatMonAn(mon);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    mode == Mode.THEM ? "Thêm món thành công!" : "Cập nhật thành công!");
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/img"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Ảnh (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                File folder = new File(System.getProperty("user.dir") + "/img");
                if (!folder.exists()) folder.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getName();
                File dest = new File(folder, fileName);
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                duongDanAnh = fileName;
                setAnhPreview(dest);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi chọn ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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

    public boolean isSaved() {
        return saved;
    }
}