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
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import dao.PhieuDatBan_DAO;
import dao.ChiTietDatMon_DAO;
import entity.ChiTietDatMon;
import gui.ThongKeTheoCa_GUI;

public class HuyBan_DigLog extends JDialog {

    private JTextField txtMaPhieu;
    private JTextField txtTienCoc;
    private JTextField txtHoanTraCoc;
    private JComboBox<String> cboPhuongThuc;
    private JTextArea txtLyDo;
    private JButton btnThoat;
    private JButton btnDongY;

    private final String maPhieuDatBan;
    private final boolean cheDoChiXem;

    private final double tienCocTruyenVao;
    private final Timestamp thoiGianDenTruyenVao;
    private final boolean coDatMonTruyenVao;

    private double tienCoc = 0;
    private Timestamp thoiGianDen = null;
    private boolean coDatMon = false;

    private boolean huyThanhCong = false;

    private final Color BG_MAIN = Color.WHITE;
    private final Color BORDER = new Color(205, 210, 218);
    private final Color DISABLED_BG = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color SUB_LINE = new Color(185, 185, 185);

    public HuyBan_DigLog(
            Frame owner,
            String maPhieuDatBan,
            double tienCoc,
            Timestamp thoiGianDen,
            boolean coDatMon,
            boolean cheDoChiXem
    ) {
        super(owner, "Xác nhận hủy đặt bàn", true);
        this.maPhieuDatBan = maPhieuDatBan;
        this.tienCocTruyenVao = tienCoc;
        this.thoiGianDenTruyenVao = thoiGianDen;
        this.coDatMonTruyenVao = coDatMon;
        this.cheDoChiXem = cheDoChiXem;

        this.tienCoc = tienCoc;
        this.thoiGianDen = thoiGianDen;
        this.coDatMon = coDatMon;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        apDungKichThuocDialog(owner);

        initUI();
        initData();
        initEvents();
    }

    public boolean isHuyThanhCong() {
        return huyThanhCong;
    }

    private void apDungKichThuocDialog(Frame owner) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int width = (int) (screen.width * 0.22);
        int height = (int) (screen.height * 0.60);

        width = Math.max(720, Math.min(width, 200));
        height = Math.max(520, Math.min(height, 580));

        setMinimumSize(new Dimension(720, 520));
        setSize(width, height);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_MAIN);
        setContentPane(contentPane);

        contentPane.add(createHeader(), BorderLayout.NORTH);
        contentPane.add(createCenterArea(), BorderLayout.CENTER);
        contentPane.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setBackground(BG_MAIN);
        topWrap.setBorder(new EmptyBorder(16, 22, 8, 22));

        JLabel lblTitle = new JLabel("Xác nhận hủy đặt bàn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_COLOR);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 1));
        line.setBackground(SUB_LINE);

        topWrap.add(lblTitle, BorderLayout.CENTER);
        topWrap.add(line, BorderLayout.SOUTH);

        return topWrap;
    }

    private Component createCenterArea() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_MAIN);
        outer.setBorder(new EmptyBorder(10, 24, 6, 24));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_MAIN);

        Font lblFont = new Font("Arial", Font.PLAIN, 15);
        Font inputFont = new Font("Arial", Font.PLAIN, 15);

        txtMaPhieu = createReadOnlyField(inputFont);
        txtTienCoc = createReadOnlyField(inputFont);
        txtHoanTraCoc = createReadOnlyField(inputFont);
        txtHoanTraCoc.setEditable(false);

        cboPhuongThuc = createComboBox(inputFont);
        txtLyDo = createTextArea(inputFont);

        JScrollPane scrLyDo = new JScrollPane(txtLyDo);
        scrLyDo.setBorder(new LineBorder(BORDER, 1, true));
        scrLyDo.setBackground(Color.WHITE);
        scrLyDo.getViewport().setBackground(Color.WHITE);
        scrLyDo.setPreferredSize(new Dimension(100, 135));
        scrLyDo.setMinimumSize(new Dimension(100, 135));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 4, 7, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        addFormRow(formPanel, gbc, 0, "Mã phiếu đặt bàn", lblFont, wrapField(txtMaPhieu));
        addFormRow(formPanel, gbc, 1, "Tiền cọc", lblFont, wrapField(txtTienCoc));
        addFormRow(formPanel, gbc, 2, "Hoàn trả cọc", lblFont, wrapField(txtHoanTraCoc));
        addFormRow(formPanel, gbc, 3, "Phương thức hoàn tiền", lblFont, wrapField(cboPhuongThuc));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.30;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(createLabel("Lý do hủy", lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.70;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrLyDo, gbc);

        outer.add(formPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new EmptyBorder(0, 18, 14, 18));

        btnThoat = new ColoredButton("Thoát", new Color(219, 167, 69));
        btnThoat.setFont(new Font("Arial", Font.PLAIN, 16));
        btnThoat.setPreferredSize(new Dimension(118, 42));

        btnDongY = new ColoredButton("Xác nhận hủy", new Color(74, 144, 206));
        btnDongY.setFont(new Font("Arial", Font.PLAIN, 16));
        btnDongY.setPreferredSize(new Dimension(165, 42));

        footer.add(btnThoat);
        footer.add(btnDongY);
        return footer;
    }

    private JTextField createReadOnlyField(Font font) {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        txt.setFont(font);
        txt.setBackground(DISABLED_BG);
        txt.setForeground(TEXT_COLOR);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 12, 0, 12)
        ));
        txt.setPreferredSize(new Dimension(100, 40));
        txt.setMinimumSize(new Dimension(100, 40));
        return txt;
    }

    private JComboBox<String> createComboBox(Font font) {
        JComboBox<String> combo = new JComboBox<>(new String[] {
                "Vui lòng chọn phương thức",
                "Tiền mặt",
                "Chuyển khoản"
        });

        combo.setFont(font);
        combo.setFocusable(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        combo.setBackground(Color.WHITE);
        combo.setOpaque(true);
        combo.setEditable(false);
        combo.setBorder(new LineBorder(BORDER, 1, true));
        combo.setPreferredSize(new Dimension(100, 40));
        combo.setMinimumSize(new Dimension(100, 40));
        combo.setUI(new BasicComboBoxUI());

        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lb = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lb.setBorder(new EmptyBorder(6, 12, 6, 12));
                lb.setFont(font);
                return lb;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        combo.setRenderer(renderer);

        return combo;
    }

    private JTextArea createTextArea(Font font) {
        JTextArea area = new JTextArea();
        area.setFont(font);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Color.WHITE);
        area.setForeground(TEXT_COLOR);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
        return area;
    }

    private JPanel wrapField(Component comp) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(comp);
        return p;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                            String label, Font lblFont, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.30;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createLabel(label, lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.70;
        panel.add(comp, gbc);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(TEXT_COLOR);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private void initData() {
        txtMaPhieu.setText(maPhieuDatBan);

        if (cheDoChiXem) {
            loadThongTinHuyTuCSDL();

            btnDongY.setEnabled(false);
            cboPhuongThuc.setEnabled(false);
            cboPhuongThuc.setBackground(DISABLED_BG);

            txtLyDo.setEditable(false);
            txtLyDo.setBackground(DISABLED_BG);
        } else {
            loadThongTinTuCSDLVaTinhHoanCoc();
        }
    }

    private void loadThongTinTuCSDLVaTinhHoanCoc() {
        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
            String[] row = dao.timTheoMaPhieu(maPhieuDatBan);

            double tienCocDB = tienCocTruyenVao;
            Timestamp thoiGianDenDB = thoiGianDenTruyenVao;

            if (row != null) {
                try {
                    tienCocDB = Double.parseDouble(row[6]);
                } catch (Exception e) {
                    tienCocDB = tienCocTruyenVao;
                }

                try {
                    thoiGianDenDB = Timestamp.valueOf(row[5]);
                } catch (Exception e) {
                    thoiGianDenDB = thoiGianDenTruyenVao;
                }
            }

            boolean coDatMonDB = coDatMonTruyenVao;
            try {
                ChiTietDatMon_DAO pdmDao = new ChiTietDatMon_DAO();
                ArrayList<ChiTietDatMon> dsMon =
                        pdmDao.getDanhSachTheoMaPhieuDatBan(
                                maPhieuDatBan
                        );
                coDatMonDB = dsMon != null && !dsMon.isEmpty();
            } catch (Exception e) {
                coDatMonDB = coDatMonTruyenVao;
            }

            this.tienCoc = tienCocDB;
            this.thoiGianDen = thoiGianDenDB;
            this.coDatMon = coDatMonDB;

            txtTienCoc.setText(formatTienVND(tienCocDB));

            double tienHoan = tinhTienHoanCoc(tienCocDB, thoiGianDenDB, coDatMonDB);
            txtHoanTraCoc.setText(formatTienVND(tienHoan));

            cboPhuongThuc.setSelectedIndex(0);
            txtLyDo.setText("");

        } catch (Exception e) {
            e.printStackTrace();

            this.tienCoc = tienCocTruyenVao;
            this.thoiGianDen = thoiGianDenTruyenVao;
            this.coDatMon = coDatMonTruyenVao;

            txtTienCoc.setText(formatTienVND(tienCocTruyenVao));
            txtHoanTraCoc.setText(formatTienVND(
                    tinhTienHoanCoc(tienCocTruyenVao, thoiGianDenTruyenVao, coDatMonTruyenVao)
            ));
        }
    }

    private void loadThongTinHuyTuCSDL() {
        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
            String[] row = dao.timTheoMaPhieu(maPhieuDatBan);

            if (row == null) {
                txtTienCoc.setText(formatTienVND(0));
                txtHoanTraCoc.setText(formatTienVND(0));
                cboPhuongThuc.setSelectedIndex(0);
                txtLyDo.setText("");
                return;
            }

            txtTienCoc.setText(formatTienCoc(row[6]));
            txtHoanTraCoc.setText(formatTienCoc(row[11]));

            String phuongThuc = row[9] == null ? "" : row[9].trim();
            if (phuongThuc.equalsIgnoreCase("Tiền mặt")) {
                cboPhuongThuc.setSelectedItem("Tiền mặt");
            } else if (phuongThuc.equalsIgnoreCase("Chuyển khoản")) {
                cboPhuongThuc.setSelectedItem("Chuyển khoản");
            } else {
                cboPhuongThuc.setSelectedIndex(0);
            }

            txtLyDo.setText(row[10] == null ? "" : row[10]);
            txtLyDo.setCaretPosition(0);

        } catch (Exception e) {
            e.printStackTrace();
            txtHoanTraCoc.setText(formatTienVND(0));
        }
    }

    private void initEvents() {
        btnThoat.addActionListener(e -> dispose());

        btnDongY.addActionListener(e -> {
            if (cheDoChiXem) return;
            xuLyHuyPhieu();
        });
    }

    private void xuLyHuyPhieu() {
        String phuongThuc = cboPhuongThuc.getSelectedItem() == null
                ? ""
                : cboPhuongThuc.getSelectedItem().toString().trim();

        String lyDo = txtLyDo.getText().trim();

        double tienHoan = parseTien(txtHoanTraCoc.getText());

        // Chỉ bắt chọn phương thức khi có tiền hoàn
        if (tienHoan > 0 &&
            "Vui lòng chọn phương thức".equals(phuongThuc)) {

            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn phương thức hoàn tiền!");
            return;
        }

        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do hủy!");
            return;
        }

        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();

            boolean ok = dao.huyPhieuDatBanVaLuuThongTin(
                    maPhieuDatBan,
                    phuongThuc,
                    lyDo,
                    BigDecimal.valueOf(parseTien(txtHoanTraCoc.getText()))
            );

            if (ok) {
            	ThongKeTheoCa_GUI
                .dsThoiGianHuy
                .put(
                        maPhieuDatBan,
                        LocalDateTime.now()
                );
                huyThanhCong = true;

                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hủy phiếu đặt bàn thất bại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi hủy phiếu đặt bàn!");
        }
    }

    public static double tinhTienHoanCoc(double tienCoc, Timestamp thoiGianDen, boolean coDatMon) {
        if (thoiGianDen == null || tienCoc <= 0) return 0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gioDen = thoiGianDen.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        long soGioConLai = Duration.between(now, gioDen).toHours();

        if (soGioConLai < 12) {
            return 0;
        }

        if (soGioConLai >= 24) {
            if (coDatMon) {
                return tienCoc * 0.30;
            }
            return tienCoc * 0.70;
        }

        if (!coDatMon) {
            return tienCoc * 0.50;
        }

        return 0;
    }

    private String formatTienVND(double soTien) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0");
        return df.format(soTien).replace(",", ".") + " VNĐ";
    }

    private String formatTienCoc(String value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            return String.format("%,.0f", bd.doubleValue()).replace(',', '.') + " VNĐ";
        } catch (Exception e) {
            return value;
        }
    }

    private long parseTien(String text) {
        if (text == null) return 0;
        String so = text.replace("VNĐ", "")
                .replace("vnđ", "")
                .replace("đ", "")
                .replace("Đ", "")
                .replace(".", "")
                .replace(",", "")
                .trim();
        if (so.isEmpty()) return 0;
        return Long.parseLong(so);
    }

    static class ColoredButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color bgColor;

        public ColoredButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}