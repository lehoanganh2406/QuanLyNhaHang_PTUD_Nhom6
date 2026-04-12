package digLog;

import javax.swing.*;
import java.awt.*;

import dao.TaiKhoan_DAO;

public class DoiMatKhau extends JDialog {

    private JTextField txtMaNV;
    private JPasswordField txtMKCu, txtMKMoi, txtNhapLai;
    private TaiKhoan_DAO tk_dao = new TaiKhoan_DAO();

    // 👉 lưu maNV để dùng xuyên suốt (tránh lỗi lambda)
    private String maNV;

    public DoiMatKhau(JFrame parent, String maNV) {
        super(parent, "Đổi mật khẩu", true);

        this.maNV = maNV; // 🔥 QUAN TRỌNG

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
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
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnHuy = createButton("Hủy", new Color(220, 53, 69)); // đỏ đẹp
        JButton btnLuu = createButton("Lưu", new Color(102, 187, 106)); // xanh lá

        pnlBtn.add(btnHuy);
        pnlBtn.add(btnLuu);

        add(pnlBtn, BorderLayout.SOUTH);

        // ===== EVENT =====
        btnHuy.addActionListener(e -> dispose());

        btnLuu.addActionListener(e -> xuLyDoiMatKhau());
    }
    
    
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);

        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);

        btn.setPreferredSize(new Dimension(100, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    // ===== XỬ LÝ NGHIỆP VỤ =====
    private void xuLyDoiMatKhau() {

        String mkCu = new String(txtMKCu.getPassword()).trim();
        String mkMoi = new String(txtMKMoi.getPassword()).trim();
        String nhapLai = new String(txtNhapLai.getPassword()).trim();

        // ===== VALIDATE =====
        if (mkCu.isEmpty() || mkMoi.isEmpty() || nhapLai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống!");
            return;
        }

        if (!mkMoi.equals(nhapLai)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!");
            return;
        }

//        if (mkMoi.length() < 6) {
//            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải >= 6 ký tự!");
//            return;
//        }

        if (mkMoi.equals(mkCu)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải khác mật khẩu cũ!");
            return;
        }

        // ===== GỌI DAO =====
        boolean kq = tk_dao.doiMatKhau(maNV, mkCu, mkMoi);

        if (kq) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!");
        }
    }

    // ===== HELPER =====
    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        p.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(comp, gbc);
    }
}