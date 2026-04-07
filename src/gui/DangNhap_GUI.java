package gui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DangNhap_GUI extends JFrame {

    private JPanel contentPane;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;

//    public static void main(String[] args) {
//        new DangNhap_GUI().setVisible(true);
//    }

    public DangNhap_GUI() {
        setTitle("Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(1, 2));
        setContentPane(contentPane);

        // Panel trái chứa ảnh
        JPanel panelLeft = new JPanel() {
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

        // Panel phải
        JPanel panelRight = new JPanel();
        panelRight.setBackground(new Color(220, 230, 241));
        panelRight.setLayout(null);
        contentPane.add(panelRight);

        // Icon phía trên
        JLabel lblTopIcon = new JLabel("");
        lblTopIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblTopIcon.setBounds(0, 40, 610, 80);

        ImageIcon iconTop = new ImageIcon("img/dn_chuong.png");
        Image imgTop = iconTop.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        lblTopIcon.setIcon(new ImageIcon(imgTop));

        panelRight.add(lblTopIcon);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Hy Vong Restaurant");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Savoye LET", Font.ITALIC, 56));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(60, 160, 500, 60);
        panelRight.add(lblTitle);

        // ===== Ô tên đăng nhập =====
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
        txtTenDangNhap.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        txtTenDangNhap.setBounds(75, 18, 270, 30);
        pnlUser.add(txtTenDangNhap);

        addPlaceholder(txtTenDangNhap, "Tên đăng nhập");

        // ===== Ô mật khẩu =====
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
        txtMatKhau.setFont(new Font("Times New Roman", Font.PLAIN, 24));
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

                // nếu đang là placeholder thì không xử lý
                if (value.equals("Mật khẩu")) {
                    return;
                }

                if (!isPasswordVisible) {
                    // đang off -> bấm vào thì hiện mật khẩu + icon mở
                    txtMatKhau.setEchoChar((char) 0);
                    lblEye.setIcon(new ImageIcon(eyeOpenImg));
                    isPasswordVisible = true;
                } else {
                    // đang mở -> bấm vào thì ẩn mật khẩu + icon off
                    txtMatKhau.setEchoChar('•');
                    lblEye.setIcon(new ImageIcon(eyeCloseImg));
                    isPasswordVisible = false;
                }
            }
        });

        pnlPass.add(lblEye);

        // Checkbox quên mật khẩu
        JCheckBox chkForgot = new JCheckBox("Quên mật khẩu?");
        chkForgot.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        chkForgot.setBackground(new Color(220, 230, 241));
        chkForgot.setForeground(Color.BLACK);
        chkForgot.setBounds(325, 440, 180, 30);
        panelRight.add(chkForgot);

        // Nút đăng nhập
        RoundedButton btnDangNhap = new RoundedButton("Đăng nhập", 30);
        btnDangNhap.setBounds(100, 530, 420, 85);
        btnDangNhap.setFont(new Font("Times New Roman", Font.BOLD, 46));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(221, 169, 73));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        panelRight.add(btnDangNhap);
    }

    // ===== Placeholder cho JTextField =====
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

    // ===== Placeholder cho JPasswordField =====
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

    // ===== Panel bo góc =====
    class RoundedPanel extends JPanel {
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

    // ===== Button bo góc =====
    class RoundedButton extends JButton {
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