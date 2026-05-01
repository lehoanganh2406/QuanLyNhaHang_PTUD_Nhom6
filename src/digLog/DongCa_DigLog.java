package digLog;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.CaLamViec_DAO;
import entity.CaLamViec;

public class DongCa_DigLog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JLabel lblMaCa;
    private JLabel lblTenCa;
    private JLabel lblThoiGianMo;
    private JLabel lblThoiGianDong;
    private JLabel lblTienMoCa;

    private JLabel lblTienMat;
    private JLabel lblChuyenKhoan;
    private JLabel lblVisa;

    private JButton btnDongCa;
    private JButton btnHuy;

    private CaLamViec caDangMo;
    private CaLamViec_DAO caDAO;

    private double tienMatCuoiCa;
    private double tienChuyenKhoanCuoiCa;
    private double tienVisaCuoiCa;

    private boolean dongCaThanhCong = false;

    public DongCa_DigLog(Frame owner, CaLamViec caDangMo) {
        super(owner, "Đóng ca làm việc", true);
        this.caDangMo = caDangMo;
        this.caDAO = new CaLamViec_DAO();

        initComponents();
        loadDuLieuCa();
        initEvents();

        setSize(590, 475);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void initComponents() {
        JPanel pnMain = new JPanel(new BorderLayout(12, 12));
        pnMain.setBackground(new Color(245, 247, 250));
        pnMain.setBorder(new EmptyBorder(20, 28, 20, 28));
        setContentPane(pnMain);

        JLabel lblTitle = new JLabel("KẾT THÚC CA LÀM VIỆC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(210, 155, 70));
        pnMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnCenter = new JPanel(new GridLayout(8, 2, 12, 12));
        pnCenter.setBackground(new Color(245, 247, 250));

        lblMaCa = taoLabelValue();
        lblTenCa = taoLabelValue();
        lblThoiGianMo = taoLabelValue();
        lblThoiGianDong = taoLabelValue();
        lblTienMoCa = taoLabelMoney();

        lblTienMat = taoLabelMoney();
        lblChuyenKhoan = taoLabelMoney();
        lblVisa = taoLabelMoney();

        pnCenter.add(taoLabelTitle("Mã ca:"));
        pnCenter.add(lblMaCa);

        pnCenter.add(taoLabelTitle("Tên ca:"));
        pnCenter.add(lblTenCa);

        pnCenter.add(taoLabelTitle("Thời gian mở ca:"));
        pnCenter.add(lblThoiGianMo);

        pnCenter.add(taoLabelTitle("Thời gian đóng ca:"));
        pnCenter.add(lblThoiGianDong);

        pnCenter.add(taoLabelTitle("Tiền mở ca:"));
        pnCenter.add(lblTienMoCa);

        pnCenter.add(taoLabelTitle("Tiền mặt cuối ca:"));
        pnCenter.add(lblTienMat);

        pnCenter.add(taoLabelTitle("Chuyển khoản cuối ca:"));
        pnCenter.add(lblChuyenKhoan);

        pnCenter.add(taoLabelTitle("Visa cuối ca:"));
        pnCenter.add(lblVisa);

        pnMain.add(pnCenter, BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        pnBottom.setBackground(new Color(245, 247, 250));

        btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnHuy.setPreferredSize(new Dimension(105, 38));

        btnDongCa = new JButton("Xác nhận");
        btnDongCa.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnDongCa.setPreferredSize(new Dimension(125, 38));

        pnBottom.add(btnHuy);
        pnBottom.add(btnDongCa);

        pnMain.add(pnBottom, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnDongCa);
    }

    private JLabel taoLabelTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JLabel taoLabelValue() {
        JLabel lbl = new JLabel("");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JLabel taoLabelMoney() {
        JLabel lbl = new JLabel("0");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(new Color(30, 90, 60));
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return lbl;
    }

    private void loadDuLieuCa() {
        if (caDangMo == null) {
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        lblMaCa.setText(caDangMo.getMaCa());
        lblTenCa.setText(caDangMo.getTenCa());

        LocalDateTime thoiGianMo = caDangMo.getThoiGianMoCa();
        lblThoiGianMo.setText(thoiGianMo == null ? "" : thoiGianMo.format(dtf));

        lblThoiGianDong.setText(LocalDateTime.now().format(dtf));

        lblTienMoCa.setText(formatTien(caDangMo.getTienMoCa()));

        double tienMatBanHang = caDAO.tinhTienTheoPhuongThuc("Tiền mặt");
        double tienCKBanHang = caDAO.tinhTienTheoPhuongThuc("Chuyển khoản");
        double tienVisaBanHang = caDAO.tinhTienTheoPhuongThuc("Visa");

        tienMatCuoiCa = caDangMo.getTienMoCa() + tienMatBanHang;
        tienChuyenKhoanCuoiCa = tienCKBanHang;
        tienVisaCuoiCa = tienVisaBanHang;

        lblTienMat.setText(formatTien(tienMatCuoiCa));
        lblChuyenKhoan.setText(formatTien(tienChuyenKhoanCuoiCa));
        lblVisa.setText(formatTien(tienVisaCuoiCa));
    }

    private void initEvents() {
        btnHuy.addActionListener(e -> {
            dongCaThanhCong = false;
            dispose();
        });

        btnDongCa.addActionListener(e -> xuLyDongCa());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dongCaThanhCong = false;
                dispose();
            }
        });
    }

    private void xuLyDongCa() {
        if (caDangMo == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ca đang mở!");
            return;
        }

        boolean ok = caDAO.dongCa(
                caDangMo.getMaCa(),
                tienMatCuoiCa,
                tienChuyenKhoanCuoiCa,
                tienVisaCuoiCa
        );

        if (ok) {
            dongCaThanhCong = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đóng ca thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatTien(double value) {
        return String.format("%,.0f", value);
    }

    public boolean isDongCaThanhCong() {
        return dongCaThanhCong;
    }
}