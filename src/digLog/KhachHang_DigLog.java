package digLog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.KhachHang_DAO;
import entity.KhachHang;
import entity.LoaiKhachHang;

public class KhachHang_DigLog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JButton btnLuu;
    private JButton btnHuy;

    private boolean themThanhCong = false;
    private KhachHang khachHangMoi;

    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    public KhachHang_DigLog(Window owner, String sdtMacDinh) {
        super(owner, "Thêm khách hàng", ModalityType.APPLICATION_MODAL);

        initUI();

        if (sdtMacDinh != null) {
            txtSDT.setText(sdtMacDinh);
        }

        setSize(430, 260);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        setContentPane(root);

        JLabel title = new JLabel("THÊM KHÁCH HÀNG", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 14));
        form.setOpaque(false);

        JLabel lblTen = new JLabel("Tên khách hàng:");
        lblTen.setFont(new Font("SansSerif", Font.BOLD, 15));

        JLabel lblSDT = new JLabel("Số điện thoại:");
        lblSDT.setFont(new Font("SansSerif", Font.BOLD, 15));

        txtTenKH = new JTextField();
        txtTenKH.setFont(new Font("SansSerif", Font.PLAIN, 15));

        txtSDT = new JTextField();
        txtSDT.setFont(new Font("SansSerif", Font.PLAIN, 15));

        form.add(lblTen);
        form.add(txtTenKH);
        form.add(lblSDT);
        form.add(txtSDT);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        btnHuy = new JButton("Hủy");
        btnLuu = new JButton("Lưu");

        btnHuy.setPreferredSize(new Dimension(95, 36));
        btnLuu.setPreferredSize(new Dimension(95, 36));

        buttons.add(btnHuy);
        buttons.add(btnLuu);

        root.add(buttons, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> xuLyLuu());
    }

    private void xuLyLuu() {
        String ten = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng.");
            txtTenKH.requestFocus();
            return;
        }

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.");
            txtSDT.requestFocus();
            return;
        }

        if (!sdt.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 đến 11 chữ số.");
            txtSDT.requestFocus();
            return;
        }

        if (khachHangDAO.getKhachHangTheoSDT(sdt) != null) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã tồn tại.");
            return;
        }

        LoaiKhachHang loaiThuong = new LoaiKhachHang("LKH01", "Thường");
        KhachHang kh = new KhachHang(null, ten, sdt, loaiThuong, 0);

        boolean ok = khachHangDAO.themKhachHangKhongCanMa(kh);

        if (ok) {
            khachHangMoi = khachHangDAO.getKhachHangTheoSDT(sdt);
            themThanhCong = true;
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại.");
        }
    }

    public boolean isThemThanhCong() {
        return themThanhCong;
    }

    public KhachHang getKhachHangMoi() {
        return khachHangMoi;
    }
}