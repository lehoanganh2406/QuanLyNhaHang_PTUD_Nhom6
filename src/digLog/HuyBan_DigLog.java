package digLog;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.swing.BorderFactory;
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

import dao.PhieuDatBan_DAO;

public class HuyBan_DigLog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtMaPhieu;
    private JTextField txtTienCoc;
    private JTextField txtHoanTraCoc;
    private JComboBox<String> cboPhuongThuc;
    private JTextArea txtLyDo;
    private JButton btnThoat;
    private JButton btnDongY;

    private final String maPhieuDatBan;
    private final double tienCoc;
    private final Timestamp thoiGianDen;
    private final boolean coDatMon;
    private final boolean cheDoChiXem;

    private boolean huyThanhCong = false;

    private final Color BG_MAIN = new Color(239, 239, 239);
    private final Color BORDER = new Color(170, 170, 170);
    private final Color DISABLED_BG = new Color(236, 236, 236);

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
        this.tienCoc = tienCoc;
        this.thoiGianDen = thoiGianDen;
        this.coDatMon = coDatMon;
        this.cheDoChiXem = cheDoChiXem;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(820, 500));
        setSize(930, 560);
        setLocationRelativeTo(owner);

        initUI();
        initData();
        initEvents();
    }

    public boolean isHuyThanhCong() {
        return huyThanhCong;
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_MAIN);
        setContentPane(contentPane);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setBackground(BG_MAIN);
        pnlTitle.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("Xác nhận hủy đặt bàn");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 1));
        line.setBackground(new Color(165, 165, 165));

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setBackground(BG_MAIN);
        topWrap.add(pnlTitle, BorderLayout.CENTER);
        topWrap.add(line, BorderLayout.SOUTH);

        contentPane.add(topWrap, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_MAIN);
        formPanel.setBorder(new EmptyBorder(26, 26, 12, 26));

        Font lblFont = new Font("Arial", Font.PLAIN, 19);
        Font inputFont = new Font("Arial", Font.PLAIN, 18);

        txtMaPhieu = createReadOnlyField(inputFont);
        txtTienCoc = createReadOnlyField(inputFont);
        txtHoanTraCoc = createReadOnlyField(inputFont);

        cboPhuongThuc = new JComboBox<>(new String[] {
                "Vui lòng chọn phương thức",
                "Tiền mặt",
                "Chuyển khoản"
        });
        cboPhuongThuc.setFont(inputFont);
        cboPhuongThuc.setPreferredSize(new Dimension(470, 44));
        cboPhuongThuc.setBackground(Color.WHITE);
        cboPhuongThuc.setBorder(new LineBorder(BORDER, 1));
        cboPhuongThuc.setFocusable(false);
        cboPhuongThuc.setCursor(new Cursor(Cursor.HAND_CURSOR));

        txtLyDo = new JTextArea(6, 20);
        txtLyDo.setFont(inputFont);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(new EmptyBorder(10, 12, 10, 12));
        txtLyDo.setBackground(Color.WHITE);

        JScrollPane scrLyDo = new JScrollPane(txtLyDo);
        scrLyDo.setPreferredSize(new Dimension(470, 120));
        scrLyDo.setBorder(new LineBorder(BORDER, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "Mã phiếu", lblFont, txtMaPhieu);
        addFormRow(formPanel, gbc, 1, "Tiền cọc", lblFont, txtTienCoc);
        addFormRow(formPanel, gbc, 2, "Hoàn trả cọc", lblFont, txtHoanTraCoc);
        addFormRow(formPanel, gbc, 3, "Phương thức hoàn tiền", lblFont, cboPhuongThuc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.34;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(createLabel("Lý do hủy", lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.66;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrLyDo, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        pnlButtons.setBackground(BG_MAIN);
        pnlButtons.setBorder(new EmptyBorder(0, 0, 4, 18));

        btnThoat = new ColoredButton("Thoát", new Color(219, 167, 69));
        btnThoat.setFont(new Font("Arial", Font.PLAIN, 18));
        btnThoat.setPreferredSize(new Dimension(120, 50));

        btnDongY = new ColoredButton("Xác nhận hủy", new Color(74, 144, 206));
        btnDongY.setFont(new Font("Arial", Font.PLAIN, 18));
        btnDongY.setPreferredSize(new Dimension(170, 50));

        pnlButtons.add(btnThoat);
        pnlButtons.add(btnDongY);

        contentPane.add(pnlButtons, BorderLayout.SOUTH);
    }

    private JTextField createReadOnlyField(Font font) {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        txt.setFont(font);
        txt.setPreferredSize(new Dimension(470, 44));
        txt.setBackground(DISABLED_BG);
        txt.setForeground(Color.DARK_GRAY);
        txt.setBorder(new LineBorder(BORDER, 1));
        txt.setMargin(new Insets(0, 12, 0, 12));
        return txt;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Font lblFont, java.awt.Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.34;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabel(label, lblFont), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.66;
        panel.add(comp, gbc);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(font);
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private void initData() {
        txtMaPhieu.setText(maPhieuDatBan);
        txtTienCoc.setText(formatTienVND(tienCoc));

        if (cheDoChiXem) {
            loadThongTinHuyTuCSDL();

            btnDongY.setEnabled(false);   // không cho bấm
            // nếu muốn ẩn hẳn thì dùng:
            // btnDongY.setVisible(false);

            cboPhuongThuc.setEnabled(false);
            cboPhuongThuc.setBackground(DISABLED_BG);

            txtLyDo.setEditable(false);
            txtLyDo.setBackground(DISABLED_BG);
        } else {
            double tienHoan = tinhTienHoanCoc(tienCoc, thoiGianDen, coDatMon);
            txtHoanTraCoc.setText(formatTienVND(tienHoan));
        }
    }

    private void loadThongTinHuyTuCSDL() {
        try {
            PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
            String[] row = dao.timTheoMaPhieu(maPhieuDatBan);

            if (row == null) {
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

        if ("Vui lòng chọn phương thức".equals(phuongThuc)) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức hoàn tiền!");
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
                huyThanhCong = true;
                JOptionPane.showMessageDialog(this, "Hủy phiếu đặt bàn thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy phiếu đặt bàn thất bại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Có lỗi khi hủy phiếu đặt bàn!");
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

        if (!coDatMon) {
            if (soGioConLai >= 24) return tienCoc * 0.70;
            return tienCoc * 0.50;
        } else {
            if (soGioConLai >= 24) return tienCoc * 0.30;
            return 0;
        }
    }

    private String formatTienVND(double soTien) {
        DecimalFormat df = new DecimalFormat("#,##0");
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            super.paintComponent(g);
            g2.dispose();
        }
    }
   
}