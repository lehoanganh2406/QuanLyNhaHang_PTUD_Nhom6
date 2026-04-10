package gui;

import javax.swing.*;
import java.awt.*;

public class DoiMatKhau extends JDialog {

    private JTextField txtMaNV;
    private JPasswordField txtMKCu, txtMKMoi, txtNhapLai;

    public DoiMatKhau(JFrame parent, String maNV) {
        super(parent, "Đổi mật khẩu", true);

        setSize(400, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblTitle.setForeground(new Color(200, 140, 50));
        add(lblTitle, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = new JTextField(maNV);
        txtMaNV.setEnabled(false);

        txtMKCu = new JPasswordField();
        txtMKMoi = new JPasswordField();
        txtNhapLai = new JPasswordField();

        addRow(pnl, gbc, 0, "Nhân viên", txtMaNV);
        addRow(pnl, gbc, 1, "Mật khẩu hiện tại", txtMKCu);
        addRow(pnl, gbc, 2, "Mật khẩu mới", txtMKMoi);
        addRow(pnl, gbc, 3, "Nhập lại MK mới", txtNhapLai);

        add(pnl, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(Color.RED);
        btnHuy.setForeground(Color.WHITE);

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(120, 160, 190));
        btnLuu.setForeground(Color.WHITE);

        pnlBtn.add(btnHuy);
        pnlBtn.add(btnLuu);

        add(pnlBtn, BorderLayout.SOUTH);

        // ===== EVENT =====
        btnHuy.addActionListener(e -> dispose());

        btnLuu.addActionListener(e -> {
            String mkCu = new String(txtMKCu.getPassword());
            String mkMoi = new String(txtMKMoi.getPassword());
            String nhapLai = new String(txtNhapLai.getPassword());

            if (mkCu.isEmpty() || mkMoi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không được để trống!");
                return;
            }

            if (!mkMoi.equals(nhapLai)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!");
                return;
            }

            // TODO: check mkCu đúng DB không

            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            dispose();
        });
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel(label), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        p.add(comp, gbc);
    }
}