package digLog;

import java.awt.*;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.CaLamViec_DAO;
import entity.CaLamViec;
import gui.DangNhap_GUI;

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
    private JLabel lblTongDoanhThu;

    private JButton btnDongCa;
    private JButton btnHuy;

    private CaLamViec caDangMo;
    private CaLamViec_DAO caDAO;

    private double tienMatCuoiCa;
    private double tienChuyenKhoanCuoiCa;
    private double tienVisaCuoiCa;
    private double tongDoanhThu;

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

        JPanel pnCenter = new JPanel(new GridLayout(9, 2, 12, 12));
        pnCenter.setBackground(new Color(245, 247, 250));

        lblMaCa = taoLabelValue();
        lblTenCa = taoLabelValue();
        lblThoiGianMo = taoLabelValue();
        lblThoiGianDong = taoLabelValue();
        lblTienMoCa = taoLabelMoney();

        lblTienMat = taoLabelMoney();
        lblChuyenKhoan = taoLabelMoney();
        lblVisa = taoLabelMoney();
        lblTongDoanhThu = taoLabelMoney();

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
        
        pnCenter.add(taoLabelTitle("Tổng doanh thu:"));
        pnCenter.add(lblTongDoanhThu);

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

        double tienMatBanHang = caDAO.tinhTienTheoPhuongThuc("Tiền mặt", caDangMo.getThoiGianMoCa());
        double tienCKBanHang = caDAO.tinhTienTheoPhuongThuc("Chuyển khoản", caDangMo.getThoiGianMoCa());
        double tienVisaBanHang = caDAO.tinhTienTheoPhuongThuc("Visa", caDangMo.getThoiGianMoCa());

        tienMatCuoiCa = caDangMo.getTienMoCa() + tienMatBanHang;
        tienChuyenKhoanCuoiCa = tienCKBanHang;
        tienVisaCuoiCa = tienVisaBanHang;

        tongDoanhThu = tienMatCuoiCa + tienChuyenKhoanCuoiCa + tienVisaCuoiCa;

        lblTienMat.setText(formatTien(tienMatCuoiCa));
        lblChuyenKhoan.setText(formatTien(tienChuyenKhoanCuoiCa));
        lblVisa.setText(formatTien(tienVisaCuoiCa));
        lblTongDoanhThu.setText(formatTien(tongDoanhThu));
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
                tienVisaCuoiCa,
                tongDoanhThu
        );

        if (ok) {
            dongCaThanhCong = true;

            hienThiFormTongKetCa();

            Window owner = SwingUtilities.getWindowAncestor(this);
            dispose();

            if (owner != null) {
                owner.dispose();
            }

            DangNhap_GUI dangNhap = new DangNhap_GUI();
            dangNhap.setVisible(true);

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
    private void xuatPDFTongKetCa() {
        try {
            java.io.File folder = new java.io.File("PDF");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = "TongKetCa_" + caDangMo.getMaCa() + ".pdf";
            String path = folder.getAbsolutePath() + java.io.File.separator + fileName;

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();

            com.itextpdf.text.Font titleFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            com.itextpdf.text.Font normalFont =
                    FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("TONG KET CA LAM VIEC", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            doc.add(title);

            doc.add(new Paragraph("Mã ca: " + caDangMo.getMaCa(), normalFont));
            doc.add(new Paragraph("Tên ca: " + caDangMo.getTenCa(), normalFont));
            doc.add(new Paragraph("Thời gian mở ca: " + lblThoiGianMo.getText(), normalFont));
            doc.add(new Paragraph("Thời gian đóng ca: " + lblThoiGianDong.getText(), normalFont));
            doc.add(new Paragraph("Tiền mở ca: " + lblTienMoCa.getText() + " VND", normalFont));
            doc.add(new Paragraph("Tiền mặt cuối ca: " + lblTienMat.getText() + " VND", normalFont));
            doc.add(new Paragraph("Chuyển khoản cuối ca: " + lblChuyenKhoan.getText() + " VND", normalFont));
            doc.add(new Paragraph("Visa cuối ca: " + lblVisa.getText() + " VND", normalFont));
            doc.add(new Paragraph("Tổng doanh thu: " + formatTien(tongDoanhThu) + " VND", normalFont));

            doc.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất PDF thành công!\nFile được lưu tại:\n" + path,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Xuất PDF thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void hienThiFormTongKetCa() {
        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Tổng kết ca",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(20, 26, 20, 26));

        JLabel lblTitle = new JLabel("TỔNG KẾT CA LÀM VIỆC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(210, 155, 70));

        JPanel pnInfo = new JPanel(new GridLayout(9, 2, 12, 12));
        pnInfo.setBackground(Color.WHITE);

        pnInfo.add(taoLabelTitle("Mã ca:"));
        pnInfo.add(taoValuePopup(caDangMo.getMaCa()));

        pnInfo.add(taoLabelTitle("Tên ca:"));
        pnInfo.add(taoValuePopup(caDangMo.getTenCa()));

        pnInfo.add(taoLabelTitle("Thời gian mở ca:"));
        pnInfo.add(taoValuePopup(lblThoiGianMo.getText()));

        pnInfo.add(taoLabelTitle("Thời gian đóng ca:"));
        pnInfo.add(taoValuePopup(lblThoiGianDong.getText()));

        pnInfo.add(taoLabelTitle("Tiền mở ca:"));
        pnInfo.add(taoValuePopup(lblTienMoCa.getText()));

        pnInfo.add(taoLabelTitle("Tiền mặt cuối ca:"));
        pnInfo.add(taoValuePopup(lblTienMat.getText()));

        pnInfo.add(taoLabelTitle("Chuyển khoản cuối ca:"));
        pnInfo.add(taoValuePopup(lblChuyenKhoan.getText()));

        pnInfo.add(taoLabelTitle("Visa cuối ca:"));
        pnInfo.add(taoValuePopup(lblVisa.getText()));

        pnInfo.add(taoLabelTitle("Tổng doanh thu:"));
        pnInfo.add(taoValuePopup(formatTien(tongDoanhThu)));

        JButton btnXuatPDF = new JButton("Xuất PDF");
        JButton btnHuy = new JButton("Hủy");

        btnXuatPDF.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnHuy.setFont(new Font("SansSerif", Font.BOLD, 16));

        btnXuatPDF.setPreferredSize(new Dimension(130, 40));
        btnHuy.setPreferredSize(new Dimension(100, 40));

        btnXuatPDF.addActionListener(e -> xuatPDFTongKetCa());
        btnHuy.addActionListener(e -> dlg.dispose());

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnBottom.setBackground(Color.WHITE);
        pnBottom.add(btnHuy);
        pnBottom.add(btnXuatPDF);

        root.add(lblTitle, BorderLayout.NORTH);
        root.add(pnInfo, BorderLayout.CENTER);
        root.add(pnBottom, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setSize(620, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
    private JLabel taoValuePopup(String text) {
        JLabel lbl = new JLabel(text == null ? "" : text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(new Color(30, 90, 60));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(248, 248, 248));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return lbl;
    }
}