package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class ManHinhKhach_GUI extends JFrame {

    private JLabel lblAnhFull;

    private JPanel pnOrderList;

    private JPanel pnlRight;

    private JSplitPane split;

    private JPanel pnlLeft;

    private JLabel lblTongTien;
    private JLabel lblVAT;
    private JLabel lblKhuyenMai;
    private JLabel lblTongCong;

    private JLabel lblPhuongThuc;
    private JLabel lblTienKhachTra;
    private JLabel lblTienThua;
    private boolean dangHienQR = false;

    public ManHinhKhach_GUI() {

        setTitle("Màn hình khách");

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setLayout(new BorderLayout());

        // =====================================
        // PANEL TRÁI
        // =====================================

        pnlLeft =
                new JPanel(
                        new BorderLayout()
                );

        lblAnhFull =
                new JLabel();

        lblAnhFull.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        try {

            ImageIcon icon =
                    new ImageIcon(
                            "img/trangchu.png"
                    );

            Dimension screen =
                    Toolkit.getDefaultToolkit()
                            .getScreenSize();

            Image img =
                    icon.getImage()
                            .getScaledInstance(
                                    screen.width,
                                    screen.height,
                                    Image.SCALE_SMOOTH
                            );

            lblAnhFull.setIcon(
                    new ImageIcon(img)
            );

        } catch (Exception e) {

            lblAnhFull.setText(
                    "KHÔNG TẢI ĐƯỢC ẢNH"
            );
        }

        pnlLeft.add(lblAnhFull);

        // =====================================
        // PANEL RIGHT
        // =====================================

        pnlRight =
                new JPanel(
                        new BorderLayout()
                );

        pnlRight.setPreferredSize(
                new Dimension(520, 0)
        );

        pnlRight.setBackground(Color.WHITE);

        // =====================================
        // TITLE
        // =====================================

        JLabel lblTitle =
                new JLabel(
                        "HÓA ĐƠN TẠM TÍNH",
                        SwingConstants.CENTER
                );

        lblTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        28
                )
        );

        lblTitle.setBorder(
                new EmptyBorder(
                        18,
                        10,
                        18,
                        10
                )
        );

        pnlRight.add(
                lblTitle,
                BorderLayout.NORTH
        );

        // =====================================
        // CENTER
        // =====================================

        JPanel center =
                new JPanel(
                        new BorderLayout()
                );

        center.setBackground(Color.WHITE);

        JPanel header =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                10,
                                0
                        )
                );

        header.setPreferredSize(
                new Dimension(0, 55)
        );

        header.setBackground(
                new Color(240,240,240)
        );

        header.add(createHeader("Tên món"));
        header.add(createHeader("Giá"));
        header.add(createHeader("SL"));
        header.add(createHeader("Thành tiền"));

        center.add(
                header,
                BorderLayout.NORTH
        );

        pnOrderList =
                new JPanel();

        pnOrderList.setLayout(
                new BoxLayout(
                        pnOrderList,
                        BoxLayout.Y_AXIS
                )
        );

        pnOrderList.setBackground(Color.WHITE);

        JScrollPane scroll =
                new JScrollPane(
                        pnOrderList
                );

        scroll.setBorder(null);

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        center.add(
                scroll,
                BorderLayout.CENTER
        );

        pnlRight.add(
                center,
                BorderLayout.CENTER
        );

        // =====================================
        // BOTTOM
        // =====================================

        JPanel bottom =
                new JPanel();

        bottom.setLayout(
                new BoxLayout(
                        bottom,
                        BoxLayout.Y_AXIS
                )
        );

        bottom.setBackground(Color.WHITE);

        bottom.setBorder(
                new EmptyBorder(
                        18,
                        25,
                        25,
                        25
                )
        );

        lblTongTien =
                createInfoLabel(
                        "Tổng tiền: 0đ"
                );

        lblVAT =
                createInfoLabel(
                        "VAT (7%): 0đ"
                );

        lblKhuyenMai =
                createInfoLabel(
                        "Khuyến mãi: 0đ"
                );

        lblTongCong =
                new JLabel(
                        "TỔNG CỘNG: 0đ"
                );

        lblTongCong.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        28
                )
        );

        lblTongCong.setForeground(
                new Color(200,0,0)
        );

        lblTongCong.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        bottom.add(lblTongTien);

        bottom.add(
                Box.createVerticalStrut(8)
        );

        bottom.add(lblVAT);

        bottom.add(
                Box.createVerticalStrut(8)
        );

        bottom.add(lblKhuyenMai);

        bottom.add(
                Box.createVerticalStrut(15)
        );

        bottom.add(lblTongCong);

        bottom.add(
                Box.createVerticalStrut(20)
        );

        bottom.add(
                new JSeparator()
        );

        bottom.add(
                Box.createVerticalStrut(18)
        );

        // =====================================
        // THANH TOÁN
        // =====================================

        lblPhuongThuc =
                createThanhToanLabel();

        lblTienKhachTra =
                createThanhToanLabel();

        lblTienThua =
                createThanhToanLabel();

        lblTienThua.setForeground(
                new Color(0,140,60)
        );

        lblPhuongThuc.setVisible(false);
        lblTienKhachTra.setVisible(false);
        lblTienThua.setVisible(false);

        bottom.add(lblPhuongThuc);

        bottom.add(
                Box.createVerticalStrut(10)
        );

        bottom.add(lblTienKhachTra);

        bottom.add(
                Box.createVerticalStrut(10)
        );

        bottom.add(lblTienThua);

        pnlRight.add(
                bottom,
                BorderLayout.SOUTH
        );

        // =====================================
        // SPLIT
        // =====================================

        split =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        pnlLeft,
                        pnlRight
                );

        split.setEnabled(false);

        add(split);

        // =====================================
        // BAN ĐẦU
        // =====================================

        pnlRight.setVisible(false);

        split.setDividerSize(0);

        split.setDividerLocation(1.0);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
    }

    // =====================================
    // HEADER
    // =====================================

    private JLabel createHeader(String text){

        JLabel lbl =
                new JLabel(
                        text,
                        SwingConstants.CENTER
                );

        lbl.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        return lbl;
    }

    // =====================================
    // INFO LABEL
    // =====================================

    private JLabel createInfoLabel(String text){

        JLabel lbl =
                new JLabel(
                        text,
                        SwingConstants.RIGHT
                );

        lbl.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        18
                )
        );

        lbl.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        return lbl;
    }

    // =====================================
    // THANH TOÁN LABEL
    // =====================================

    private JLabel createThanhToanLabel(){

        JLabel lbl =
                new JLabel();

        lbl.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        lbl.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        return lbl;
    }

    // =====================================
    // CELL
    // =====================================

    private JLabel createCell(
            String text,
            int align
    ){

        if (text.length() > 18) {

            text =
                    text.substring(0, 18)
                            + "...";
        }

        JLabel lbl =
                new JLabel(
                        text,
                        align
                );

        lbl.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );

        lbl.setBorder(
                new EmptyBorder(
                        0,
                        10,
                        0,
                        10
                )
        );

        return lbl;
    }

    // =====================================
    // UPDATE HÓA ĐƠN
    // =====================================

 // =====================================
 // UPDATE HÓA ĐƠN
 // =====================================

 public void capNhatHoaDon(
         String tenBan,
         Map<String, Order_Mon_GUI.OrderItem> gioHang,
         String tongTien
 ){

     if (dangHienQR) {
         return;
     }

     if (
             gioHang == null
             ||
             gioHang.isEmpty()
     ) {
         return;
     }

     // HIỆN PANEL HÓA ĐƠN
     pnlRight.setVisible(true);

     split.setDividerSize(6);

     split.setDividerLocation(0.45);

     // ĐỔI ẢNH BÊN TRÁI
     try {

         ImageIcon icon =
                 new ImageIcon(
                         "img/dangnhap.png"
                 );

         Dimension screen =
                 Toolkit.getDefaultToolkit()
                         .getScreenSize();

         Image img =
                 icon.getImage()
                 .getScaledInstance(
                	        pnlLeft.getWidth(),
                	        pnlLeft.getHeight(),
                	        Image.SCALE_SMOOTH
                	);

         lblAnhFull.setIcon(
                 new ImageIcon(img)
         );

     } catch (Exception e) {

         e.printStackTrace();
     }

     // CLEAR DANH SÁCH
     pnOrderList.removeAll();

     double tong = 0;

     // LOAD MÓN
     for(
             Order_Mon_GUI.OrderItem item
                     : gioHang.values()
     ){

         double thanhTien =
                 item.soLuong
                         * item.mon.getDonGia();

         tong += thanhTien;

         JPanel row =
                 new JPanel(
                         new GridLayout(
                                 1,
                                 4,
                                 10,
                                 0
                         )
                 );

         row.setMaximumSize(
                 new Dimension(
                         Integer.MAX_VALUE,
                         55
                 )
         );

         row.setBackground(Color.WHITE);

         row.setBorder(
                 BorderFactory.createMatteBorder(
                         0,
                         0,
                         1,
                         0,
                         new Color(235,235,235)
                 )
         );

         row.add(
                 createCell(
                         item.mon.getTenMon(),
                         SwingConstants.LEFT
                 )
         );

         row.add(
                 createCell(
                         formatTien(
                                 item.mon.getDonGia()
                         ),
                         SwingConstants.CENTER
                 )
         );

         row.add(
                 createCell(
                         String.valueOf(
                                 item.soLuong
                         ),
                         SwingConstants.CENTER
                 )
         );

         row.add(
                 createCell(
                         formatTien(
                                 thanhTien
                         ),
                         SwingConstants.RIGHT
                 )
         );

         pnOrderList.add(row);
     }

     // TÍNH TIỀN
     double vat = tong * 0.07;

     double tongCong =
             tong + vat;

     lblTongTien.setText(
             "Tổng tiền: "
                     + formatTien(tong)
                     + "đ"
     );

     lblVAT.setText(
             "VAT (7%): "
                     + formatTien(vat)
                     + "đ"
     );

     lblKhuyenMai.setText(
             "Khuyến mãi: 0đ"
     );

     lblTongCong.setText(
             "TỔNG CỘNG: "
                     + formatTien(tongCong)
                     + "đ"
     );

     // ẨN THANH TOÁN
     lblPhuongThuc.setVisible(false);
     lblTienKhachTra.setVisible(false);
     lblTienThua.setVisible(false);

     // REFRESH
     pnOrderList.revalidate();
     pnOrderList.repaint();

     revalidate();
     repaint();
 }

    // =====================================
    // THANH TOÁN
    // =====================================

    public void hienThiThongTinThanhToan(
            String phuongThuc,
            String tienKhachTra,
            String tienThua
    ){

        lblPhuongThuc.setVisible(true);
        lblTienKhachTra.setVisible(true);
        lblTienThua.setVisible(true);

        lblPhuongThuc.setText(
                "Phương thức: "
                        + phuongThuc
        );

        lblTienKhachTra.setText(
                "Tiền khách trả: "
                        + tienKhachTra
                        + "đ"
        );

        lblTienThua.setText(
                "Tiền thừa: "
                        + tienThua
                        + "đ"
        );
    }

    // =====================================
    // RESET
    // =====================================

    public void resetVeMacDinh(){
    	dangHienQR = false;

        pnlRight.setVisible(false);

        split.setDividerSize(0);

        split.setDividerLocation(1.0);

        pnOrderList.removeAll();

        pnOrderList.revalidate();

        pnOrderList.repaint();
        try {

            ImageIcon icon =
                    new ImageIcon(
                            "img/trangchu.png"
                    );

            Dimension screen =
                    Toolkit.getDefaultToolkit()
                            .getScreenSize();

            Image img =
                    icon.getImage()
                            .getScaledInstance(
                                    screen.width,
                                    screen.height,
                                    Image.SCALE_SMOOTH
                            );

            lblAnhFull.setIcon(
                    new ImageIcon(img)
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
 // =====================================
 // HIỂN THỊ QR
 // =====================================

    public void hienThiQR(ImageIcon icon){

        dangHienQR = true;

        pnlRight.setVisible(true);

        split.setDividerSize(6);

        split.setDividerLocation(0.45);

        try {

            Image img =
                    icon.getImage()
                            .getScaledInstance(
                                    pnlLeft.getWidth(),
                                    pnlLeft.getHeight(),
                                    Image.SCALE_SMOOTH
                            );

            lblAnhFull.setIcon(
                    new ImageIcon(img)
            );

        } catch (Exception e) {

            lblAnhFull.setText("Không tải được QR");
        }
    }

    // =====================================
    // FORMAT TIỀN
    // =====================================

    private String formatTien(double tien){

        return String.format(
                "%,.0f",
                tien
        ).replace(",", ".");
    }
}