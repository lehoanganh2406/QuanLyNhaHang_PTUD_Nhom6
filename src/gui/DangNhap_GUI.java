package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import connectDB.ConnectDB;
import dao.TaiKhoan_DAO;
import digLog.TienMoCa_DigLog;
import entity.TaiKhoan;

public class DangNhap_GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private RoundedButton btnDangNhap;
    private JCheckBox chkForgot;

    private String maTamThoi = null;
    private String tenDangNhapMaTam = null;
    private long thoiGianHetHanMaTam = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConnectDB.getInstance().connect();
                new DangNhap_GUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Không thể kết nối cơ sở dữ liệu!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public DangNhap_GUI() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(1, 2));
        setContentPane(contentPane);

        JPanel panelLeft = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon("img/dangnhap.png");
                Image img = bgIcon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelLeft.setLayout(null);
        contentPane.add(panelLeft);

        JPanel panelRight = new JPanel();
        panelRight.setBackground(new Color(220, 230, 241));
        panelRight.setLayout(null);
        contentPane.add(panelRight);

        JLabel lblTopIcon = new JLabel("");
        lblTopIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblTopIcon.setBounds(0, 40, 610, 80);

        ImageIcon iconTop = new ImageIcon("img/dn_chuong.png");
        Image imgTop = iconTop.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        lblTopIcon.setIcon(new ImageIcon(imgTop));
        panelRight.add(lblTopIcon);

        JLabel lblTitle = new JLabel("Hy Vong Restaurant");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Savoye LET", Font.ITALIC, 56));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(60, 160, 500, 60);
        panelRight.add(lblTitle);

        RoundedPanel pnlUser = new RoundedPanel(30);
        pnlUser.setBackground(new Color(190, 195, 202));
        pnlUser.setBounds(120, 250, 380, 68);
        pnlUser.setLayout(null);
        panelRight.add(pnlUser);

        JLabel lblUserIcon = new JLabel("");
        lblUserIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserIcon.setBounds(15, 18, 40, 30);

        ImageIcon userIcon = new ImageIcon("img/dn_icon_ten.png");
        Image userImg = userIcon.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
        lblUserIcon.setIcon(new ImageIcon(userImg));
        pnlUser.add(lblUserIcon);

        txtTenDangNhap = new JTextField("Tên đăng nhập");
        txtTenDangNhap.setBorder(null);
        txtTenDangNhap.setOpaque(false);
        txtTenDangNhap.setForeground(Color.WHITE);
        txtTenDangNhap.setFont(new Font("SansSerif", Font.PLAIN, 24));
        txtTenDangNhap.setBounds(75, 18, 270, 30);
        pnlUser.add(txtTenDangNhap);

        addPlaceholder(txtTenDangNhap, "Tên đăng nhập");

        RoundedPanel pnlPass = new RoundedPanel(30);
        pnlPass.setBackground(new Color(190, 195, 202));
        pnlPass.setBounds(120, 350, 380, 68);
        pnlPass.setLayout(null);
        panelRight.add(pnlPass);

        JLabel lblPassIcon = new JLabel("");
        lblPassIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblPassIcon.setBounds(15, 15, 40, 35);

        ImageIcon passIcon = new ImageIcon("img/dn_icon_mk.png");
        Image passImg = passIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        lblPassIcon.setIcon(new ImageIcon(passImg));
        pnlPass.add(lblPassIcon);

        txtMatKhau = new JPasswordField("Mật khẩu");
        txtMatKhau.setBorder(null);
        txtMatKhau.setOpaque(false);
        txtMatKhau.setForeground(Color.WHITE);
        txtMatKhau.setFont(new Font("SansSerif", Font.PLAIN, 24));
        txtMatKhau.setEchoChar((char) 0);
        txtMatKhau.setBounds(75, 18, 220, 30);
        pnlPass.add(txtMatKhau);

        addPasswordPlaceholder(txtMatKhau, "Mật khẩu");

        JLabel lblEye = new JLabel("");
        lblEye.setHorizontalAlignment(SwingConstants.CENTER);
        lblEye.setBounds(325, 18, 35, 30);

        ImageIcon eyeOpenIcon = new ImageIcon("img/Dn_eye_open.png");
        Image eyeOpenImg = eyeOpenIcon.getImage().getScaledInstance(27, 27, Image.SCALE_SMOOTH);

        ImageIcon eyeCloseIcon = new ImageIcon("img/Dn_eye_off.png");
        Image eyeCloseImg = eyeCloseIcon.getImage().getScaledInstance(27, 27, Image.SCALE_SMOOTH);

        lblEye.setIcon(new ImageIcon(eyeCloseImg));
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblEye.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean isPasswordVisible = false;

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String value = String.valueOf(txtMatKhau.getPassword());

                if (value.equals("Mật khẩu")) {
                    return;
                }

                if (!isPasswordVisible) {
                    txtMatKhau.setEchoChar((char) 0);
                    lblEye.setIcon(new ImageIcon(eyeOpenImg));
                    isPasswordVisible = true;
                } else {
                    txtMatKhau.setEchoChar('•');
                    lblEye.setIcon(new ImageIcon(eyeCloseImg));
                    isPasswordVisible = false;
                }
            }
        });

        pnlPass.add(lblEye);

        chkForgot = new JCheckBox("Quên mật khẩu?");
        chkForgot.setFont(new Font("SansSerif", Font.PLAIN, 20));
        chkForgot.setBackground(new Color(220, 230, 241));
        chkForgot.setForeground(Color.BLACK);
        chkForgot.setFocusPainted(false);
        chkForgot.setBounds(300, 440, chkForgot.getPreferredSize().width, 30);
        panelRight.add(chkForgot);

        chkForgot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkForgot.isSelected()) {
                    xuLyQuenMatKhau();
                }
            }
        });

        btnDangNhap = new RoundedButton("Đăng nhập", 30);
        btnDangNhap.setBounds(100, 530, 420, 85);
        btnDangNhap.setFont(new Font("SansSerif", Font.BOLD, 46));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(221, 169, 73));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        panelRight.add(btnDangNhap);

        getRootPane().setDefaultButton(btnDangNhap);

        btnDangNhap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhauNhap = String.valueOf(txtMatKhau.getPassword()).trim();

        if (tenDangNhap.equals("") || tenDangNhap.equals("Tên đăng nhập")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!");
            txtTenDangNhap.requestFocus();
            return;
        }

        if (matKhauNhap.equals("") || matKhauNhap.equals("Mật khẩu")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu hoặc mã tạm!");
            txtMatKhau.requestFocus();
            return;
        }

        LoadingDialog loading = new LoadingDialog(this);
        btnDangNhap.setEnabled(false);

        SwingWorker<TaiKhoan, Void> worker = new SwingWorker<TaiKhoan, Void>() {
            @Override
            protected TaiKhoan doInBackground() throws Exception {
                Thread.sleep(1000);

                TaiKhoan_DAO tkDao = new TaiKhoan_DAO();

                TaiKhoan tk = tkDao.dangNhap(tenDangNhap, matKhauNhap);
                if (tk != null) {
                    return tk;
                }

                if (kiemTraMaTamHopLe(tenDangNhap, matKhauNhap)) {
                    TaiKhoan tkTam = tkDao.getTaiKhoanTheoTenDangNhap(tenDangNhap);
                    if (tkTam != null && tkTam.isTrangThai()) {
                        return tkTam;
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                loading.dispose();
                btnDangNhap.setEnabled(true);

                try {
                    TaiKhoan tk = get();

                    if (tk != null) {
                        moTienMoCaSauDangNhap(tk);
                    } else {
                        JOptionPane.showMessageDialog(DangNhap_GUI.this,
                                "Sai tên đăng nhập, sai mật khẩu, sai mã tạm hoặc mã đã hết hiệu lực!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DangNhap_GUI.this,
                            "Lỗi kết nối hoặc truy vấn dữ liệu!");
                }
            }
        };

        worker.execute();
        loading.setVisible(true);
    }

    private void moTienMoCaSauDangNhap(TaiKhoan tk) {
        setVisible(false);

        TienMoCa_DigLog dlg = new TienMoCa_DigLog(this, tk);
        dlg.setVisible(true);

        if (dlg.isMoCaThanhCong()) {
            TrangChu_GUI trangChu = new TrangChu_GUI(tk);
            trangChu.setVisible(true);
            dispose();
        }
    }

    private void xuLyQuenMatKhau() {
        String tenDN = JOptionPane.showInputDialog(
                DangNhap_GUI.this,
                "Nhập tên đăng nhập để nhận mã đăng nhập tạm:",
                "Quên mật khẩu",
                JOptionPane.PLAIN_MESSAGE
        );

        if (tenDN == null) {
            chkForgot.setSelected(false);
            return;
        }

        tenDN = tenDN.trim();

        if (tenDN.isEmpty()) {
            JOptionPane.showMessageDialog(DangNhap_GUI.this, "Tên đăng nhập không được để trống!");
            chkForgot.setSelected(false);
            return;
        }

        TaiKhoan_DAO tkDao = new TaiKhoan_DAO();
        TaiKhoan tk = tkDao.getTaiKhoanTheoTenDangNhap(tenDN);

        if (tk == null) {
            JOptionPane.showMessageDialog(DangNhap_GUI.this, "Tên đăng nhập không tồn tại!");
            chkForgot.setSelected(false);
            return;
        }

        if (!tk.isTrangThai()) {
            JOptionPane.showMessageDialog(DangNhap_GUI.this, "Tài khoản đang bị khóa!");
            chkForgot.setSelected(false);
            return;
        }

        maTamThoi = taoMaNgauNhien5So();
        tenDangNhapMaTam = tenDN;
        thoiGianHetHanMaTam = System.currentTimeMillis() + 5 * 60 * 1000;

        JOptionPane.showMessageDialog(
                DangNhap_GUI.this,
                "Mã đăng nhập tạm của bạn là: " + maTamThoi
                        + "\nMã chỉ dùng 1 lần và có hiệu lực trong 5 phút.",
                "Mã đăng nhập tạm",
                JOptionPane.INFORMATION_MESSAGE
        );

        txtTenDangNhap.setText(tenDN);
        txtTenDangNhap.setForeground(Color.WHITE);
        txtMatKhau.setText("");
        txtMatKhau.setEchoChar('•');
        txtMatKhau.setForeground(Color.WHITE);
        txtMatKhau.requestFocus();

        chkForgot.setSelected(false);
    }

    private String taoMaNgauNhien5So() {
        int so = 10000 + (int) (Math.random() * 90000);
        return String.valueOf(so);
    }

    private boolean kiemTraMaTamHopLe(String tenDangNhap, String maNhap) {
        if (maTamThoi == null || tenDangNhapMaTam == null) {
            return false;
        }

        long hienTai = System.currentTimeMillis();

        if (hienTai >= thoiGianHetHanMaTam) {
            maTamThoi = null;
            tenDangNhapMaTam = null;
            thoiGianHetHanMaTam = 0;
            return false;
        }

        if (tenDangNhap.equals(tenDangNhapMaTam) && maNhap.equals(maTamThoi)) {
            maTamThoi = null;
            tenDangNhapMaTam = null;
            thoiGianHetHanMaTam = 0;
            return true;
        }

        return false;
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.WHITE);
                }
            }
        });
    }

    private void addPasswordPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String value = String.valueOf(passwordField.getPassword());
                if (value.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String value = String.valueOf(passwordField.getPassword());
                if (value.trim().isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(Color.WHITE);
                }
            }
        });
    }

    class LoadingDialog extends JDialog {
        private static final long serialVersionUID = 1L;

        private JLabel lblLoading;
        private Timer timer;
        private int dotCount = 0;
        private final String baseText = "Đang đăng nhập";

        public LoadingDialog(JFrame parent) {
            super(parent, true);
            setUndecorated(true);
            setSize(320, 160);
            setLocationRelativeTo(parent);

            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            panel.setLayout(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));
            setContentPane(panel);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(new Dimension(260, 20));
            panel.add(progressBar, BorderLayout.NORTH);

            lblLoading = new JLabel(baseText, SwingConstants.CENTER);
            lblLoading.setFont(new Font("SansSerif", Font.BOLD, 24));
            panel.add(lblLoading, BorderLayout.CENTER);

            timer = new Timer(400, e -> {
                dotCount = (dotCount + 1) % 4;
                StringBuilder text = new StringBuilder(baseText);
                for (int i = 0; i < dotCount; i++) {
                    text.append(".");
                }
                lblLoading.setText(text.toString());
            });
            timer.start();
        }

        @Override
        public void dispose() {
            if (timer != null) {
                timer.stop();
            }
            super.dispose();
        }
    }

    class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedButton extends JButton {
        private static final long serialVersionUID = 1L;
        private int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }
}