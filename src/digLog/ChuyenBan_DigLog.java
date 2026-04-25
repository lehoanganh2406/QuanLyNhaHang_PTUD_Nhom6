package digLog;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.Ban_DAO;

public class ChuyenBan_DigLog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final Ban_DAO banDAO = new Ban_DAO();

    private String maBanHienTai;
    private String tenBanHienTai;
    private String maBanMoi;

    private JComboBox<BanComboItem> cboBanTrong;

    public ChuyenBan_DigLog(Window owner, String maBanHienTai, String tenBanHienTai) {
        super(owner, "Chuyển bàn", ModalityType.APPLICATION_MODAL);
        this.maBanHienTai = maBanHienTai;
        this.tenBanHienTai = tenBanHienTai;

        initUI();
        loadBanTrong();

        setSize(430, 260);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        setContentPane(root);

        JLabel lblTitle = new JLabel("CHUYỂN BÀN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(22, 0, 18, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBanHienTai = new JLabel("Bàn hiện tại:");
        lblBanHienTai.setFont(new Font("SansSerif", Font.BOLD, 15));

        JTextField txtBanHienTai = new JTextField(tenBanHienTai + " (" + maBanHienTai + ")");
        txtBanHienTai.setEditable(false);
        txtBanHienTai.setBackground(new Color(245, 245, 245));

        JLabel lblBanMoi = new JLabel("Chuyển sang:");
        lblBanMoi.setFont(new Font("SansSerif", Font.BOLD, 15));

        cboBanTrong = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lblBanHienTai, gbc);

        gbc.gridx = 1;
        form.add(txtBanHienTai, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(lblBanMoi, gbc);

        gbc.gridx = 1;
        form.add(cboBanTrong, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnHuy = new JButton("Hủy");
        JButton btnDongY = new JButton("Đồng ý");

        btnDongY.setBackground(new Color(188, 222, 242));
        btnDongY.setOpaque(true);
        btnDongY.setContentAreaFilled(false);
        btnDongY.setFocusPainted(false);

        btnHuy.setFocusPainted(false);

        btnHuy.addActionListener(e -> dispose());

        btnDongY.addActionListener(e -> {
            BanComboItem item = (BanComboItem) cboBanTrong.getSelectedItem();

            if (item == null || item.maBan == null || item.maBan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chọn bàn cần chuyển!");
                return;
            }

            maBanMoi = item.maBan;
            dispose();
        });

        actions.add(btnHuy);
        actions.add(btnDongY);
        root.add(actions, BorderLayout.SOUTH);
    }

    private void loadBanTrong() {
        cboBanTrong.removeAllItems();
        cboBanTrong.addItem(new BanComboItem("", "-- Chọn bàn trống --"));

        ArrayList<String[]> ds = banDAO.getTatCaBanKemTrangThaiMacDinh();

        if (ds == null) return;

        for (String[] row : ds) {
            if (row == null || row.length < 3) continue;

            String maBan = row[0];
            String tenBan = row[1];
            String trangThai = row[2];

            if (maBanHienTai.equalsIgnoreCase(maBan)) continue;

            if ("Bàn trống".equalsIgnoreCase(trangThai) || "Trống".equalsIgnoreCase(trangThai)) {
                cboBanTrong.addItem(new BanComboItem(maBan, tenBan));
            }
        }
    }

    public String getMaBanMoi() {
        return maBanMoi;
    }

    private static class BanComboItem {
        String maBan;
        String tenBan;

        BanComboItem(String maBan, String tenBan) {
            this.maBan = maBan;
            this.tenBan = tenBan;
        }

        @Override
        public String toString() {
            if (maBan == null || maBan.isEmpty()) return tenBan;
            return tenBan + " - " + maBan;
        }
    }
}