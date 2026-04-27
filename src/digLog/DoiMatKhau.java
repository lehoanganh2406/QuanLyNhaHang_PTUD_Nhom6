package digLog;

import javax.swing.*;
import java.awt.*;
import dao.TaiKhoan_DAO;

public class DoiMatKhau extends JDialog {

    private JTextField txtMaTK;
    private JPasswordField txtMKMoi, txtNhapLai;

    private TaiKhoan_DAO tk_dao = new TaiKhoan_DAO();

    // phải là maTaiKhoan
    private String maTaiKhoan;

    public DoiMatKhau(JFrame parent, String maTaiKhoan) {
        super(parent, "Reset mật khẩu", true);

        this.maTaiKhoan = maTaiKhoan;

        setSize(430, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(200, 140, 50));
        add(lblTitle, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaTK = new JTextField(maTaiKhoan);
        txtMaTK.setEnabled(false);

        txtMKMoi = new JPasswordField();
        txtNhapLai = new JPasswordField();

        addRow(pnl, gbc, 0, "Mã tài khoản", txtMaTK);
        addRow(pnl, gbc, 1, "Mật khẩu mới", txtMKMoi);
        addRow(pnl, gbc, 2, "Nhập lại MK mới", txtNhapLai);

        add(pnl, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnHuy = createButton("Hủy", new Color(220, 53, 69));
        JButton btnLuu = createButton("Lưu", new Color(102, 187, 106));

        pnlBtn.add(btnHuy);
        pnlBtn.add(btnLuu);

        add(pnlBtn, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> xuLyResetMatKhau());
    }

    private void xuLyResetMatKhau() {
        String mkMoi = new String(txtMKMoi.getPassword()).trim();
        String nhapLai = new String(txtNhapLai.getPassword()).trim();

        if (mkMoi.isEmpty() || nhapLai.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không được để trống mật khẩu mới!");
            return;
        }

        if (!mkMoi.equals(nhapLai)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu nhập lại không khớp!");
            return;
        }

        if (mkMoi.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới phải từ 6 ký tự trở lên!");
            return;
        }

        // truyền đúng maTaiKhoan
        boolean kq = tk_dao.doiMatKhau(maTaiKhoan, mkMoi);

        if (kq) {
            JOptionPane.showMessageDialog(this,
                    "Reset mật khẩu thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Reset mật khẩu thất bại!");
        }
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 38));
        return btn;
    }

    private void addRow(JPanel p, GridBagConstraints gbc,
                        int row, String label, JComponent comp) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        p.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(comp, gbc);
    }
}