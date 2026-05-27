package digLog;

import java.awt.*;
import java.awt.print.*;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import entity.ChiTietHoaDon;
import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class HoaDonChiTiet_DigLog extends JPanel {


    private final DecimalFormat df = new DecimalFormat("#,##0");
    private JPanel billPanel;
    private String tieuDe = "HÓA ĐƠN THANH TOÁN";

    public HoaDonChiTiet_DigLog(
            String tieuDe,
            String maHD,
            String tenBan,
            String maPhieuDatBan,
            double tienCoc,
            String tenKH,
            String tenNV,
            String thoiGian,
            String tenKM,
            double tienGiam,
            double tongTien,
            double vat,
            double tongCong,
            double tienKhachTra,
            double tienThua,
            String phuongThuc,
            int diemCongThem,
            List<ChiTietHoaDon> dsCT,
            String linkQR
    )  {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        billPanel = new JPanel();
        billPanel.setLayout(new BoxLayout(billPanel, BoxLayout.Y_AXIS));
        billPanel.setBackground(Color.WHITE);
        billPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel(tieuDe, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        billPanel.add(title);
        billPanel.add(Box.createVerticalStrut(18));

        // ===== THÔNG TIN TRÊN =====
        billPanel.add(info("Mã hóa đơn", maHD));
        billPanel.add(info("Bàn", tenBan));
        billPanel.add(info("Phiếu đặt bàn", maPhieuDatBan == null || maPhieuDatBan.trim().isEmpty() ? "Không có" : maPhieuDatBan));
        billPanel.add(info("Khách hàng", tenKH));
        billPanel.add(info("Nhân viên", tenNV));
        billPanel.add(info("Ngày giờ", thoiGian));
        billPanel.add(Box.createVerticalStrut(14));

        // ===== DANH SÁCH MÓN =====
        billPanel.add(headerRow());

        if (dsCT != null) {
            for (ChiTietHoaDon ct : dsCT) {
                String tenMon = "";

                if (ct.getMaMon() != null) {
                    if (ct.getMaMon().getTenMon() != null) {
                        tenMon = ct.getMaMon().getTenMon();
                    } else {
                        tenMon = ct.getMaMon().getMaMon();
                    }
                }

                billPanel.add(itemRow(
                        tenMon,
                        ct.getSoLuong(),
                        ct.getDonGia(),
                        ct.getSoLuong() * ct.getDonGia()
                ));
            }
        }

        // ===== THÔNG TIN TIỀN DƯỚI =====
        billPanel.add(info("Tổng tiền", formatTien(tongTien)));
        billPanel.add(info("VAT", formatTien(vat)));
        billPanel.add(info("Khuyến mãi", formatKhuyenMai(tenKM, tienGiam)));
        billPanel.add(info("Tiền cọc", formatTien(tienCoc)));
        billPanel.add(info("Tổng cộng", formatTien(tongCong)));
        billPanel.add(info("Phương thức", phuongThuc));
        billPanel.add(info("Khách trả", formatTien(tienKhachTra)));
        billPanel.add(info("Tiền thừa", formatTien(tienThua)));
        billPanel.add(info(
                "Điểm tích lũy được cộng",
                tenKH == null
                || tenKH.trim().isEmpty()
                || tenKH.equalsIgnoreCase("Khách lẻ")
                        ? "0"
                        : "+" + diemCongThem
        ));
     // =============================
     // QR THANH TOÁN
     // =============================

        if (
                linkQR != null
                &&
                !linkQR.trim().isEmpty()
        ) {

            try {

                billPanel.add(
                        Box.createVerticalStrut(20)
                );

                JLabel lblQRTitle =
                        new JLabel(
                                "QUÉT QR ĐỂ THANH TOÁN",
                                SwingConstants.CENTER
                        );

                lblQRTitle.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                20
                        )
                );

                lblQRTitle.setAlignmentX(
                        Component.CENTER_ALIGNMENT
                );

                billPanel.add(lblQRTitle);

                billPanel.add(
                        Box.createVerticalStrut(12)
                );

                java.net.URL url =
                        new java.net.URL(linkQR);

                ImageIcon icon =
                        new ImageIcon(url);

                Image img =
                        icon.getImage()
                                .getScaledInstance(
                                        260,
                                        260,
                                        Image.SCALE_SMOOTH
                                );

                JLabel lblQR =
                        new JLabel(
                                new ImageIcon(img)
                        );

                lblQR.setAlignmentX(
                        Component.CENTER_ALIGNMENT
                );

                billPanel.add(lblQR);

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }

        JButton btnIn = new JButton("In hóa đơn / Xuất PDF");
        JButton btnDong = new JButton("Đóng");

        btnIn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnDong.setFont(new Font("SansSerif", Font.BOLD, 18));

        btnIn.setPreferredSize(new Dimension(0, 48));
        btnDong.setPreferredSize(new Dimension(0, 48));

        btnIn.addActionListener(e -> {
            boolean ok = inHoaDon();
            if (ok) {
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) {
                    w.dispose();
                }
            }
        });

        btnDong.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();
            }
        });

        JPanel bottom = new JPanel(new GridLayout(1, 2, 8, 0));
        bottom.setBorder(new EmptyBorder(8, 12, 8, 12));
        bottom.add(btnIn);
        bottom.add(btnDong);

        JScrollPane scroll=
                new JScrollPane(billPanel);

        scroll.setBorder(null);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        scroll.getViewport()
                .setBackground(Color.WHITE);

        add(scroll,BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel info(String left, String right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel l = new JLabel(left);
        JLabel r = new JLabel(right == null ? "" : right, SwingConstants.RIGHT);

        l.setFont(new Font("SansSerif", Font.BOLD, 15));
        r.setFont(new Font("SansSerif", Font.PLAIN, 15));

        p.add(l, BorderLayout.WEST);
        p.add(r, BorderLayout.EAST);
        return p;
    }

    private JPanel headerRow(){

        JPanel p=new JPanel(
                new GridBagLayout()
        );

        p.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        p.setBackground(
        		new Color(245,245,245)
        );
        p.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(210,210,210)
                )
        );

        GridBagConstraints gbc=
                new GridBagConstraints();

        gbc.fill=GridBagConstraints.BOTH;

        gbc.gridy=0;

        gbc.weighty=1;

        gbc.insets=new Insets(0,0,0,0);

        gbc.gridx=0;
        gbc.weightx=5;
        p.add(cell("Món",true,SwingConstants.LEFT),gbc);

        gbc.gridx=1;
        gbc.weightx=1;
        p.add(cell("SL",true,SwingConstants.CENTER),gbc);

        gbc.gridx=2;
        gbc.weightx=2;
        p.add(cell("Đơn giá",true,SwingConstants.RIGHT),gbc);

        gbc.gridx=3;
        gbc.weightx=2;
        p.add(cell("Thành tiền",true,SwingConstants.RIGHT),gbc);

        return p;
    }

    private JPanel itemRow(
            String tenMon,
            int sl,
            double donGia,
            double thanhTien
    ){

        JPanel p=new JPanel(
                new GridBagLayout()
        );

        p.setBackground(Color.WHITE);

        p.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(235,235,235)
                )
        );

        GridBagConstraints gbc=
                new GridBagConstraints();

        gbc.fill=GridBagConstraints.BOTH;

        gbc.insets=new Insets(1,4,1,4);

        gbc.gridy=0;

        gbc.weighty=1;

        gbc.gridx=0;
        gbc.weightx=4;

        p.add(
                cell(
                        tenMon,
                        false,
                        SwingConstants.LEFT
                ),
                gbc
        );

        gbc.gridx=1;
        gbc.weightx=1;

        p.add(
                cell(
                        String.valueOf(sl),
                        false,
                        SwingConstants.CENTER
                ),
                gbc
        );

        gbc.gridx=2;
        gbc.weightx=2;

        p.add(
                cell(
                        formatTien(donGia),
                        false,
                        SwingConstants.RIGHT
                ),
                gbc
        );

        gbc.gridx=3;
        gbc.weightx=2;

        p.add(
                cell(
                        formatTien(thanhTien),
                        false,
                        SwingConstants.RIGHT
                ),
                gbc
        );

        return p;
    }

    private JLabel cell(
            String text,
            boolean bold,
            int align
    ){

        JLabel lbl=new JLabel(text);

        lbl.setHorizontalAlignment(align);
        Dimension size;

        switch (align) {

            case SwingConstants.LEFT:

                size = new Dimension(260, 28);
                break;

            case SwingConstants.CENTER:

                size = new Dimension(70, 28);
                break;

            default:

                size = new Dimension(130, 28);
                break;
        }

        lbl.setPreferredSize(size);
        lbl.setMinimumSize(size);
        lbl.setMaximumSize(size);

        lbl.setVerticalAlignment(
                SwingConstants.CENTER
        );

        lbl.setFont(
                new Font(
                        "SansSerif",
                        bold
                                ? Font.BOLD
                                : Font.PLAIN,
                        14
                )
        );

        lbl.setBorder(
                BorderFactory.createEmptyBorder(
                        3,
                        6,
                        3,
                        6
                )
        );

        return lbl;
    }

    private String formatTien(double v) {
        return df.format(v).replace(",", ".");
    }

    private String formatKhuyenMai(String tenKM, double tienGiam) {
        if (tenKM == null || tenKM.trim().isEmpty() || tenKM.equalsIgnoreCase("Không áp dụng")) {
            return "Không áp dụng - 0";
        }

        return tenKM + " - " + formatTien(tienGiam);
    }

    private boolean inHoaDon() {
        try {
            File dir = new File("PDF");
            if (!dir.exists()) dir.mkdirs();

            String maHD = layGiaTriTuBill("Mã hóa đơn");
            if (maHD == null || maHD.trim().isEmpty()) maHD = "HoaDon";

            String filePath = "PDF/" + maHD + ".pdf";

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            com.itextpdf.text.Font titleFont =
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);

            com.itextpdf.text.Font boldFont =
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD);

            com.itextpdf.text.Font normalFont =
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);

            Paragraph title = new Paragraph("HOA DON THANH TOAN", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(18);
            document.add(title);

            document.add(new Paragraph("Ma hoa don: " + layGiaTriTuBill("Mã hóa đơn"), normalFont));
            document.add(new Paragraph("Ban: " + layGiaTriTuBill("Bàn"), normalFont));
            document.add(new Paragraph("Phieu dat ban: " + layGiaTriTuBill("Phiếu đặt bàn"), normalFont));
            document.add(new Paragraph("Khach hang: " + layGiaTriTuBill("Khách hàng"), normalFont));
            document.add(new Paragraph("Nhan vien: " + layGiaTriTuBill("Nhân viên"), normalFont));
            document.add(new Paragraph("Ngay gio: " + layGiaTriTuBill("Ngày giờ"), normalFont));
            document.add(new Paragraph(" "));

            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2f});

            addPdfCell(table, "Mon", boldFont);
            addPdfCell(table, "SL", boldFont);
            addPdfCell(table, "Don gia", boldFont);
            addPdfCell(table, "Thanh tien", boldFont);

            for (Component c : billPanel.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel p = (JPanel) c;

                    if (p.getComponentCount() == 4) {
                        String c1 = getLabelText(p.getComponent(0));
                        String c2 = getLabelText(p.getComponent(1));
                        String c3 = getLabelText(p.getComponent(2));
                        String c4 = getLabelText(p.getComponent(3));

                        if ("Món".equalsIgnoreCase(c1) || "Mon".equalsIgnoreCase(c1)) continue;

                        addPdfCell(table, removeVietnamese(c1), normalFont);
                        addPdfCell(table, c2, normalFont);
                        addPdfCell(table, c3, normalFont);
                        addPdfCell(table, c4, normalFont);
                    }
                }
            }

            document.add(table);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Tong tien: " + layGiaTriTuBill("Tổng tiền"), normalFont));
            document.add(new Paragraph("VAT: " + layGiaTriTuBill("VAT"), normalFont));
            document.add(new Paragraph("Khuyen mai: " + removeVietnamese(layGiaTriTuBill("Khuyến mãi")), normalFont));
            document.add(new Paragraph("Tien coc: " + layGiaTriTuBill("Tiền cọc"), normalFont));
            document.add(new Paragraph("Tong cong: " + layGiaTriTuBill("Tổng cộng"), boldFont));
            document.add(new Paragraph("Phuong thuc: " + removeVietnamese(layGiaTriTuBill("Phương thức")), normalFont));
            document.add(new Paragraph("Khach tra: " + layGiaTriTuBill("Khách trả"), normalFont));
            document.add(new Paragraph("Tien thua: " + layGiaTriTuBill("Tiền thừa"), normalFont));
            document.add(new Paragraph("Diem tich luy duoc cong: " + layGiaTriTuBill("Điểm tích lũy được cộng"), normalFont));
            document.add(new Paragraph("----------------------------------------", normalFont));

            Paragraph thanks = new Paragraph("Cam on quy khach\nHen gap lai quy khach!", normalFont);
            thanks.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Xuất PDF thành công:\n" + new File(filePath).getAbsolutePath()
            );

            Desktop.getDesktop().open(new File(filePath));
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không xuất được PDF.");
            return false;
        }
    }
    private void addPdfCell(com.itextpdf.text.pdf.PdfPTable table, String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell cell =
                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text == null ? "" : text, font));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private String getLabelText(Component c) {
        if (c instanceof JLabel) {
            return ((JLabel) c).getText();
        }
        return "";
    }

    private String removeVietnamese(String s) {
        if (s == null) return "";
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D");
    }
    private String layGiaTriTuBill(String label) {
        for (Component c : billPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;

                JLabel left = null;
                JLabel right = null;

                for (Component child : p.getComponents()) {
                    if (child instanceof JLabel) {
                        if (left == null) {
                            left = (JLabel) child;
                        } else {
                            right = (JLabel) child;
                        }
                    }
                }

                if (left != null && right != null && label.equalsIgnoreCase(left.getText())) {
                    return right.getText();
                }
            }
        }

        return "";
    }
}