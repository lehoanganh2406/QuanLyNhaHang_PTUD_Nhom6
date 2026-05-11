package digLog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dao.CaLamViec_DAO;
import entity.TaiKhoan;

public class TienMoCa_DigLog extends JDialog {

    private JLabel lblNgayGioValue;
    private JLabel lblCaLamValue;
    private JTextField txtTienMoCa;
    private JButton btnHuy;
    private JButton btnXacNhan;

    private TaiKhoan taiKhoanDangNhap;
    private CaLamViec_DAO caLamViecDAO;

    private boolean moCaThanhCong = false;

    public TienMoCa_DigLog(Frame owner, TaiKhoan taiKhoanDangNhap) {
        super(owner, "Nhập tiền mở ca", true);
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        this.caLamViecDAO = new CaLamViec_DAO();

        initComponents();
        napThongTinMacDinh();
        initEvents();

        setSize(520, 310);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 242, 242));

        JPanel pnMain = new JPanel(new BorderLayout(10, 10));
        pnMain.setBackground(new Color(242, 242, 242));
        pnMain.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        setContentPane(pnMain);

        JLabel lblTitle = new JLabel("NHẬP TIỀN MỞ CA");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(210, 155, 70));
        pnMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnCenter = new JPanel();
        pnCenter.setBackground(new Color(242, 242, 242));
        pnCenter.setLayout(new BorderLayout(12, 12));

        JPanel pnInfo = new JPanel(new GridLayout(2, 1, 0, 6));
        pnInfo.setBackground(new Color(242, 242, 242));

        lblNgayGioValue = new JLabel("Ngày giờ: ");
        lblNgayGioValue.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblNgayGioValue.setForeground(Color.BLACK);

        lblCaLamValue = new JLabel("Ca làm: ");
        lblCaLamValue.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblCaLamValue.setForeground(Color.BLACK);

        pnInfo.add(lblNgayGioValue);
        pnInfo.add(lblCaLamValue);

        pnCenter.add(pnInfo, BorderLayout.NORTH);

        txtTienMoCa = new JTextField();
        txtTienMoCa.setFont(new Font("SansSerif", Font.PLAIN, 22));
        txtTienMoCa.setPreferredSize(new Dimension(380, 52));
        txtTienMoCa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JPanel pnInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnInput.setBackground(new Color(242, 242, 242));
        pnInput.add(txtTienMoCa);

        pnCenter.add(pnInput, BorderLayout.CENTER);
        pnMain.add(pnCenter, BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new BorderLayout());
        pnBottom.setBackground(new Color(242, 242, 242));

        btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnHuy.setFocusPainted(false);
        btnHuy.setBackground(new Color(198, 220, 236));
        btnHuy.setPreferredSize(new Dimension(90, 38));

        btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBackground(new Color(236, 213, 177));
        btnXacNhan.setPreferredSize(new Dimension(112, 38));

        JPanel pnLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnLeft.setBackground(new Color(242, 242, 242));
        pnLeft.add(btnHuy);

        JPanel pnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        pnRight.setBackground(new Color(242, 242, 242));
        pnRight.add(btnXacNhan);

        pnBottom.add(pnLeft, BorderLayout.WEST);
        pnBottom.add(pnRight, BorderLayout.EAST);

        pnMain.add(pnBottom, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnXacNhan);
    }

    private void napThongTinMacDinh() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblNgayGioValue.setText("Ngày giờ: " + now.format(dtf));

        String tenCa = caLamViecDAO.layTenCaHienThi();
        lblCaLamValue.setText("Ca làm: " + tenCa);
    }

    private void initEvents() {
        btnHuy.addActionListener(e -> thoatUngDung());

        btnXacNhan.addActionListener(e -> xuLyMoCa());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thoatUngDung();
            }
        });
    }

    private void xuLyMoCa() {
        String tienText = txtTienMoCa.getText().trim();

        if (tienText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tiền mở ca.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTienMoCa.requestFocus();
            return;
        }

        double tienMoCa = parseTienNhap(tienText);

        if (tienMoCa < 0) {
            JOptionPane.showMessageDialog(this,
                    "Tiền mở ca không hợp lệ. Vui lòng nhập số.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtTienMoCa.requestFocus();
            txtTienMoCa.selectAll();
            return;
        }

        if (caLamViecDAO.layCaDangMo() != null) {
            JOptionPane.showMessageDialog(this,
                    "Hiện đang có ca chưa đóng, không thể mở ca mới.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
      

        boolean ok = caLamViecDAO.moCa(tienMoCa, taiKhoanDangNhap);
        if (ok) {
            moCaThanhCong = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Mở ca thất bại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thoatUngDung() {
        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có muốn thoát ứng dụng không?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (chon == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    public boolean isMoCaThanhCong() {
        return moCaThanhCong;
    }
    private double parseTienNhap(String text) {
        try {
            if (text == null) return 0;

            String s = text.trim()
                    .replace("VNĐ", "")
                    .replace("đ", "")
                    .replace(" ", "");

            if (s.matches("\\d{1,3}(\\.\\d{3})+")) {
                s = s.replace(".", "");
            }

            s = s.replace(",", "");

            return Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }
    }
}