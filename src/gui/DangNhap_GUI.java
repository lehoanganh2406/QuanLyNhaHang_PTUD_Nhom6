package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import connectDB.ConnectDB;
import dao.CaLamViec_DAO;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import digLog.TienMoCa_DigLog;
import entity.NhanVien;
import entity.TaiKhoan;
import util.Mail_Util;
import digLog.DongCa_DigLog;
import entity.CaLamViec;

public class DangNhap_GUI extends JFrame {


    private JPanel contentPane;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private RoundedButton btnDangNhap;
    private JCheckBox chkForgot;
    private RoundedPanel pnlUser;
    private RoundedPanel pnlPass;

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

        contentPane = new JPanel(new GridLayout(1, 2));
        setContentPane(contentPane);

        JPanel panelLeft = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon( getClass().getResource("/dangnhap.png"));
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

        ImageIcon iconTop = new ImageIcon(getClass().getResource("/dn_chuong.png"));
        Image imgTop = iconTop.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        lblTopIcon.setIcon(new ImageIcon(imgTop));
        panelRight.add(lblTopIcon);

        JLabel lblTitle = new JLabel("Nhà Hàng HyVong");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Savoye LET", Font.ITALIC, 56));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(60, 160, 500, 60);
        panelRight.add(lblTitle);

        pnlUser = new RoundedPanel(30);
        pnlUser.setBackground(new Color(190, 195, 202));
        pnlUser.setBounds(120, 250, 380, 68);
        pnlUser.setLayout(null);
        panelRight.add(pnlUser);

        JLabel lblUserIcon = new JLabel("");
        lblUserIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserIcon.setBounds(15, 18, 40, 30);

        ImageIcon userIcon = new ImageIcon(getClass().getResource("/dn_icon_ten.png"));
        
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
        txtTenDangNhap.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                setLoiUser(false);
            }
        });

        pnlPass = new RoundedPanel(30);
        pnlPass.setBackground(new Color(190, 195, 202));
        pnlPass.setBounds(120, 350, 380, 68);
        pnlPass.setLayout(null);
        panelRight.add(pnlPass);

        JLabel lblPassIcon = new JLabel("");
        lblPassIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblPassIcon.setBounds(15, 15, 40, 35);

        ImageIcon passIcon = new ImageIcon(getClass().getResource("/dn_icon_mk.png"));
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
        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                setLoiPass(false);
            }
        });

        JLabel lblEye = new JLabel("");
        lblEye.setHorizontalAlignment(SwingConstants.CENTER);
        lblEye.setBounds(325, 18, 35, 30);

        ImageIcon eyeOpenIcon = new ImageIcon(getClass().getResource("/Dn_eye_open.png"));
        Image eyeOpenImg = eyeOpenIcon.getImage().getScaledInstance(27, 27, Image.SCALE_SMOOTH);

        ImageIcon eyeCloseIcon = new ImageIcon(getClass().getResource("/Dn_eye_off.png"));
        
        Image eyeCloseImg = eyeCloseIcon.getImage().getScaledInstance(27, 27, Image.SCALE_SMOOTH);

        lblEye.setIcon(new ImageIcon(eyeCloseImg));
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblEye.addMouseListener(new MouseAdapter() {
            private boolean isPasswordVisible = false;

            @Override
            public void mouseClicked(MouseEvent e) {
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

        chkForgot.addActionListener(e -> {
            if (chkForgot.isSelected()) {
                xuLyQuenMatKhau();
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

        btnDangNhap.addActionListener(e -> xuLyDangNhap());
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhauNhap = String.valueOf(txtMatKhau.getPassword()).trim();

        boolean thieuTenDN = tenDangNhap.isEmpty() || tenDangNhap.equals("Tên đăng nhập");
        boolean thieuMK = matKhauNhap.isEmpty() || matKhauNhap.equals("Mật khẩu");

        setLoiUser(thieuTenDN);
        setLoiPass(thieuMK);

        if (thieuTenDN) {
            txtTenDangNhap.requestFocus();
            return;
        }

        if (thieuMK) {
            txtMatKhau.requestFocus();
            return;
        }

        LoadingDialog loading = new LoadingDialog(this);
        btnDangNhap.setEnabled(false);

        SwingWorker<TaiKhoan, Void> worker = new SwingWorker<TaiKhoan, Void>() {
            @Override
            protected TaiKhoan doInBackground() throws Exception {
                Thread.sleep(700);
                TaiKhoan_DAO tkDao = new TaiKhoan_DAO();
                return tkDao.dangNhap(tenDangNhap, matKhauNhap);
            }

            @Override
            protected void done() {
                loading.dispose();
                btnDangNhap.setEnabled(true);

                try {

                	TaiKhoan_DAO tkDao = new TaiKhoan_DAO();

                	// kiểm tra tên đăng nhập tồn tại không
                	TaiKhoan tkTheoTen = tkDao.getTaiKhoanTheoTenDangNhap(tenDangNhap);

                	if (tkTheoTen == null) {

                	    JOptionPane.showMessageDialog(
                	            DangNhap_GUI.this,
                	            "Tên đăng nhập không đúng!"
                	    );

                	    txtTenDangNhap.requestFocus();
                	    setLoiUser(true);
                	    return;
                	}

                	// kiểm tra mật khẩu
                	TaiKhoan tk = get();

                	if (tk == null) {

                	    JOptionPane.showMessageDialog(
                	            DangNhap_GUI.this,
                	            "Mật khẩu không đúng!"
                	    );

                	    txtMatKhau.requestFocus();
                	    setLoiPass(true);
                	    return;
                	}

                	// đăng nhập thành công
                	String chucVu = tk.getMaNV().getChucVu();

                	if ("Bếp".equalsIgnoreCase(chucVu)) {

                	    TrangChu_GUI trangChu = new TrangChu_GUI(tk);
                	    trangChu.setVisible(true);
                	    dispose();

                	} else {

                	    moTienMoCaSauDangNhap(tk);
                	}

                } catch (Exception ex) {

                    ex.printStackTrace();

                    JOptionPane.showMessageDialog(
                            DangNhap_GUI.this,
                            "Lỗi kết nối hoặc truy vấn dữ liệu!"
                    );
                }
            }
        };

        worker.execute();
        loading.setVisible(true);
    }

    private void moTienMoCaSauDangNhap(TaiKhoan tk) {
        setVisible(false);

        CaLamViec_DAO caDAO = new CaLamViec_DAO();
        CaLamViec caDangMo = caDAO.layCaDangMo();

        if (caDangMo != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String gioMoCa = caDangMo.getThoiGianMoCa() == null
                    ? ""
                    : caDangMo.getThoiGianMoCa().format(dtf);

            Object[] options = {
                    "Tiếp tục ca đang mở",
                    "Đóng ca đang mở, mở ca mới",
                    "Hủy"
            };
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.BOLD, 16));
            UIManager.put("Button.minimumSize", new Dimension(230, 42));


            int chon = JOptionPane.showOptionDialog(
                    this,
                    "Hiện tại đang có ca làm việc chưa đóng.\n"
                            + "Mã ca: " + caDangMo.getMaCa() + "\n"
                            + "Tên ca: " + caDangMo.getTenCa() + "\n"
                            + "Giờ mở ca: " + gioMoCa + "\n\n"
                            + "Bạn muốn tiếp tục ca đang mở hay đóng ca cũ để mở ca mới?",
                    "Thông báo ca đang mở",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (chon == 0) {
                TrangChu_GUI trangChu = new TrangChu_GUI(tk);
                trangChu.setVisible(true);
                dispose();
                return;
            }

            if (chon == 1) {
                DongCa_DigLog dlgDongCa = new DongCa_DigLog(this, caDangMo, tk);
                dlgDongCa.setVisible(true);

                if (!dlgDongCa.isDongCaThanhCong()) {
                    moTienMoCaSauDangNhap(tk);
                    return;
                }

                TienMoCa_DigLog dlgMoCa = new TienMoCa_DigLog(this, tk);
                dlgMoCa.setVisible(true);

                if (dlgMoCa.isMoCaThanhCong()) {
                    TrangChu_GUI trangChu = new TrangChu_GUI(tk);
                    trangChu.setVisible(true);
                    dispose();
                } else {
                    System.exit(0);
                }
                return;
            }

            if (chon == 2 || chon == JOptionPane.CLOSED_OPTION) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn chắc chắn muốn đóng ứng dụng?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    moTienMoCaSauDangNhap(tk);
                }
                return;
            }

            return;
        }

        TienMoCa_DigLog dlgMoCa = new TienMoCa_DigLog(this, tk);
        dlgMoCa.setVisible(true);

        if (dlgMoCa.isMoCaThanhCong()) {
            TrangChu_GUI trangChu = new TrangChu_GUI(tk);
            trangChu.setVisible(true);
            dispose();
        } else {
            System.exit(0);
        }
    }

    private void xuLyQuenMatKhau() {
        String sdt = JOptionPane.showInputDialog(
                DangNhap_GUI.this,
                "Nhập số điện thoại nhân viên:",
                "Quên mật khẩu",
                JOptionPane.PLAIN_MESSAGE
        );

        if (sdt == null) {
            chkForgot.setSelected(false);
            return;
        }

        sdt = sdt.trim();

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống!");
            chkForgot.setSelected(false);
            return;
        }

        NhanVien_DAO nvDao = new NhanVien_DAO();
        TaiKhoan_DAO tkDao = new TaiKhoan_DAO();

        NhanVien nv = nvDao.getNhanVienTheoSDT(sdt);

        if (nv == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có số điện thoại này!");
            chkForgot.setSelected(false);
            return;
        }

        if (nv.getEmail() == null || nv.getEmail().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhân viên này chưa có email!");
            chkForgot.setSelected(false);
            return;
        }

        TaiKhoan tk = tkDao.getTaiKhoanTheoMaNV(nv.getMaNV());

        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Nhân viên này chưa có tài khoản!");
            chkForgot.setSelected(false);
            return;
        }

        if (!tk.isTrangThai()) {
            JOptionPane.showMessageDialog(this, "Tài khoản đang bị khóa!");
            chkForgot.setSelected(false);
            return;
        }

        String ma6So = taoMaNgauNhien6So();
        long thoiGianHetHan = System.currentTimeMillis() + 5 * 60 * 1000;

        JDialog sending = taoDialogDangGuiMail();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return Mail_Util.guiMaQuenMatKhau(nv.getEmail(), ma6So);
            }

            @Override
            protected void done() {
                sending.dispose();

                try {
                    boolean guiThanhCong = get();

                    if (!guiThanhCong) {
                        JOptionPane.showMessageDialog(DangNhap_GUI.this,
                                "Gửi email thất bại!\nKiểm tra Gmail, App Password hoặc Internet.");
                        chkForgot.setSelected(false);
                        return;
                    }

                    JOptionPane.showMessageDialog(DangNhap_GUI.this,
                            "Mã xác nhận 6 số đã được gửi về email:\n" + cheEmail(nv.getEmail()));

                    moFormDoiMatKhau(tk, ma6So, thoiGianHetHan);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DangNhap_GUI.this, "Lỗi khi gửi email!");
                }

                chkForgot.setSelected(false);
            }
        };

        worker.execute();
        sending.setVisible(true);
    }

    private JDialog taoDialogDangGuiMail() {
        JDialog dlg = new JDialog(this, "Đang gửi mã", true);
        dlg.setSize(300, 130);
        dlg.setLocationRelativeTo(this);
        dlg.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        panel.add(bar, BorderLayout.NORTH);

        JLabel lbl = new JLabel("Đang gửi mã về Gmail...", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lbl, BorderLayout.CENTER);

        dlg.setContentPane(panel);
        return dlg;
    }

    private String taoMaNgauNhien6So() {
        int so = 100000 + (int) (Math.random() * 900000);
        return String.valueOf(so);
    }

    private String cheEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String name = parts[0];

        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + parts[1];
        }

        return name.substring(0, 2) + "***@" + parts[1];
    }

    private void moFormDoiMatKhau(TaiKhoan tk, String ma6So, long thoiGianHetHan) {
        JDialog dlg = new JDialog(this, "Đổi mật khẩu", true);
        dlg.setSize(480, 410);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(null);
        dlg.getContentPane().setBackground(new Color(245, 247, 250));
        dlg.setResizable(false);

        ImageIcon eyeOpenIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Dn_eye_open.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)
        );
        ImageIcon eyeCloseIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Dn_eye_off.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)
        );

        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setBounds(0, 20, 480, 35);
        dlg.add(lblTitle);

        JLabel lblMa = new JLabel("Mật khẩu hiện tại / mã 6 số:");
        lblMa.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblMa.setBounds(55, 75, 300, 25);
        dlg.add(lblMa);

        JPasswordField txtMa = new JPasswordField();
        txtMa.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtMa.setBounds(55, 103, 310, 38);
        dlg.add(txtMa);

        JLabel eyeMa = taoNutEye(txtMa, eyeOpenIcon, eyeCloseIcon);
        eyeMa.setBounds(375, 107, 30, 30);
        dlg.add(eyeMa);

        JLabel lblMoi = new JLabel("Mật khẩu mới:");
        lblMoi.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblMoi.setBounds(55, 150, 300, 25);
        dlg.add(lblMoi);

        JPasswordField txtMoi = new JPasswordField();
        txtMoi.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtMoi.setBounds(55, 178, 310, 38);
        dlg.add(txtMoi);

        JLabel eyeMoi = taoNutEye(txtMoi, eyeOpenIcon, eyeCloseIcon);
        eyeMoi.setBounds(375, 182, 30, 30);
        dlg.add(eyeMoi);

        JLabel lblNhapLai = new JLabel("Nhập lại mật khẩu mới:");
        lblNhapLai.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblNhapLai.setBounds(55, 225, 300, 25);
        dlg.add(lblNhapLai);

        JPasswordField txtNhapLai = new JPasswordField();
        txtNhapLai.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtNhapLai.setBounds(55, 253, 310, 38);
        dlg.add(txtNhapLai);

        JLabel eyeNhapLai = taoNutEye(txtNhapLai, eyeOpenIcon, eyeCloseIcon);
        eyeNhapLai.setBounds(375, 257, 30, 30);
        dlg.add(eyeNhapLai);

        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnXacNhan.setBounds(95, 320, 120, 38);
        dlg.add(btnXacNhan);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnHuy.setBounds(255, 320, 120, 38);
        dlg.add(btnHuy);

        btnHuy.addActionListener(e -> dlg.dispose());

        btnXacNhan.addActionListener(e -> {
            String maNhap = String.valueOf(txtMa.getPassword()).trim();
            String mkMoi = String.valueOf(txtMoi.getPassword()).trim();
            String mkNhapLai = String.valueOf(txtNhapLai.getPassword()).trim();

            if (System.currentTimeMillis() > thoiGianHetHan) {
                JOptionPane.showMessageDialog(dlg, "Mã xác nhận đã hết hạn!");
                dlg.dispose();
                return;
            }

            if (maNhap.isEmpty() || mkMoi.isEmpty() || mkNhapLai.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            if (!maNhap.equals(ma6So)) {
                JOptionPane.showMessageDialog(dlg, "Mã xác nhận không đúng!");
                txtMa.requestFocus();
                return;
            }

            if (mkMoi.length() < 6) {
                JOptionPane.showMessageDialog(dlg, "Mật khẩu mới phải từ 6 ký tự trở lên!");
                txtMoi.requestFocus();
                return;
            }

            if (!mkMoi.equals(mkNhapLai)) {
                JOptionPane.showMessageDialog(dlg, "Mật khẩu nhập lại không khớp!");
                txtNhapLai.requestFocus();
                return;
            }

            TaiKhoan_DAO tkDao = new TaiKhoan_DAO();
            boolean ok = tkDao.doiMatKhau(tk.getMaTaiKhoan(), mkMoi);

            if (ok) {
                JOptionPane.showMessageDialog(dlg,
                        "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại bằng mật khẩu mới.");

                txtTenDangNhap.setText(tk.getTenDangNhap());
                txtTenDangNhap.setForeground(Color.WHITE);

                txtMatKhau.setText("");
                txtMatKhau.setEchoChar('•');
                txtMatKhau.setForeground(Color.WHITE);
                txtMatKhau.requestFocus();

                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Đổi mật khẩu thất bại!");
            }
        });

        dlg.setVisible(true);
    }
    private JLabel taoNutEye(JPasswordField passwordField, ImageIcon eyeOpenIcon, ImageIcon eyeCloseIcon) {
        JLabel lblEye = new JLabel();
        lblEye.setHorizontalAlignment(SwingConstants.CENTER);
        lblEye.setIcon(eyeCloseIcon);
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblEye.addMouseListener(new MouseAdapter() {
            private boolean visible = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!visible) {
                    passwordField.setEchoChar((char) 0);
                    lblEye.setIcon(eyeOpenIcon);
                    visible = true;
                } else {
                    passwordField.setEchoChar('•');
                    lblEye.setIcon(eyeCloseIcon);
                    visible = false;
                }
            }
        });

        return lblEye;
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
    private void setLoiUser(boolean loi) {
        pnlUser.setBorder(loi ? BorderFactory.createLineBorder(Color.RED, 3) : null);
        pnlUser.repaint();
    }

    private void setLoiPass(boolean loi) {
        pnlPass.setBorder(loi ? BorderFactory.createLineBorder(Color.RED, 3) : null);
        pnlPass.repaint();
    }
}