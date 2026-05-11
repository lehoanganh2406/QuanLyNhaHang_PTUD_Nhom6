package digLog;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_Ban_DAO;
import dao.HoaDon_DAO;

import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TachBan_DigLog extends JDialog {

    // =====================================================
    // DAO
    // =====================================================

    private final Ban_DAO banDAO =
            new Ban_DAO();

    private final HoaDon_DAO hoaDonDAO =
            new HoaDon_DAO();

    private final ChiTietHoaDon_DAO chiTietDAO =
            new ChiTietHoaDon_DAO();

    private final HoaDon_Ban_DAO hoaDonBanDAO =
            new HoaDon_Ban_DAO();

    // =====================================================
    // DATA
    // =====================================================

    private TaiKhoan taiKhoanDangNhap;

    private String maBanHienTai;

    private String maHDHienTai;

    private List<ChiTietHoaDon> dsChiTiet =
            new ArrayList<>();

    private final Set<String> dsBanChon =
            new LinkedHashSet<>();

    // maMon -> (maBan -> spinner)
    private final Map<
            String,
            Map<String, JSpinner>
            > mapSpinnerBan =
            new LinkedHashMap<>();

    private boolean tachThanhCong = false;

    // =====================================================
    // UI
    // =====================================================

    private JPanel pnlBan;

    private JPanel pnlMon;

    private JLabel lblTongSucChua;

    private JCheckBox chkTachHoaDon;

    private JButton btnXacNhan;

    private JButton btnHuy;

    // =====================================================
    // COLOR
    // =====================================================

    private final Color MAU_TRONG =
            new Color(0,123,255);

    private final Color MAU_PHUC_VU =
            new Color(40,167,69);

    private final Color MAU_DAT =
            new Color(220,53,69);

    private final Color MAU_CHON =
            new Color(255,140,0);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TachBan_DigLog(
            Window owner,
            TaiKhoan tk,
            String maBanHienTai,
            String maHDHienTai
    ) {

        super(owner);

        this.taiKhoanDangNhap = tk;

        this.maBanHienTai = maBanHienTai;

        this.maHDHienTai = maHDHienTai;

        setModal(true);

        setTitle("Tách bàn");

        setSize(1550,850);

        setLocationRelativeTo(owner);

        initUI();

        loadBan();

        loadMon();

        updateTongSucChua();
    }

    // =====================================================
    // UI
    // =====================================================

    private void initUI() {

        JPanel root =
                new JPanel(new BorderLayout(15,15));

        root.setBorder(
                new EmptyBorder(15,15,15,15)
        );

        root.setBackground(
                new Color(245,247,250)
        );

        setContentPane(root);

        // =================================================
        // TOP
        // =================================================

        JPanel top =
                new JPanel(new BorderLayout());

        top.setOpaque(false);

        JLabel lblTitle =
                new JLabel(
                        "TÁCH BÀN",
                        SwingConstants.CENTER
                );

        lblTitle.setFont(
                new Font("Arial", Font.BOLD, 32)
        );

        top.add(lblTitle, BorderLayout.CENTER);

        lblTongSucChua =
                new JLabel(
                        "Tổng sức chứa: 0"
                );

        lblTongSucChua.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        top.add(lblTongSucChua, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);

        // =================================================
        // CENTER
        // =================================================

        JSplitPane split =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT
                );

        split.setDividerLocation(700);

        split.setResizeWeight(0.45);

        root.add(split, BorderLayout.CENTER);

        // =================================================
        // LEFT
        // =================================================

        JPanel left =
                new JPanel(new BorderLayout(10,10));

        left.setOpaque(false);

        JLabel lblBan =
                new JLabel("Chọn bàn");

        lblBan.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        left.add(lblBan, BorderLayout.NORTH);

        pnlBan =
                new JPanel(
                        new GridLayout(
                                0,
                                3,
                                18,
                                18
                        )
                );

        pnlBan.setBackground(
                new Color(245,247,250)
        );

        JScrollPane spBan =
                new JScrollPane(pnlBan);

        spBan.setBorder(null);

        left.add(spBan, BorderLayout.CENTER);

        split.setLeftComponent(left);

        // =================================================
        // RIGHT
        // =================================================

        JPanel right =
                new JPanel(new BorderLayout(10,10));

        right.setOpaque(false);

        JPanel topMon =
                new JPanel(new BorderLayout());

        topMon.setOpaque(false);

        JLabel lblMon =
                new JLabel("Chia món theo bàn");

        lblMon.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        topMon.add(lblMon, BorderLayout.WEST);

        chkTachHoaDon =
                new JCheckBox("Tách hóa đơn");

        chkTachHoaDon.setOpaque(false);

        chkTachHoaDon.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        topMon.add(chkTachHoaDon, BorderLayout.EAST);

        right.add(topMon, BorderLayout.NORTH);

        pnlMon =
                new JPanel();

        pnlMon.setLayout(
                new BoxLayout(
                        pnlMon,
                        BoxLayout.Y_AXIS
                )
        );

        pnlMon.setBackground(Color.WHITE);

        JScrollPane spMon =
                new JScrollPane(pnlMon);

        spMon.setBorder(null);

        right.add(spMon, BorderLayout.CENTER);

        split.setRightComponent(right);

        // =================================================
        // BOTTOM
        // =================================================

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                12,
                                0
                        )
                );

        bottom.setOpaque(false);

        btnHuy =
                new JButton("Hủy");

        btnXacNhan =
                new JButton("Tách bàn");

        styleButton(btnHuy, false);

        styleButton(btnXacNhan, true);

        bottom.add(btnHuy);

        bottom.add(btnXacNhan);

        root.add(bottom, BorderLayout.SOUTH);

        // =================================================
        // EVENT
        // =================================================

        btnHuy.addActionListener(
                e -> dispose()
        );

        btnXacNhan.addActionListener(
                e -> xuLyTachBan()
        );
    }

    // =====================================================
    // STYLE BUTTON
    // =====================================================

    private void styleButton(
            JButton btn,
            boolean primary
    ) {

        btn.setPreferredSize(
                new Dimension(170,50)
        );

        btn.setFocusPainted(false);

        btn.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        btn.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btn.setBorderPainted(false);

        btn.setContentAreaFilled(true);

        btn.setOpaque(true);

        btn.setForeground(Color.BLACK);

        if (primary) {

            btn.setBackground(
                    new Color(255,140,0)
            );

        } else {

            btn.setBackground(
            		new Color(220,220,220)
            );
        }
    }

    // =====================================================
    // LOAD BÀN
    // =====================================================

    private void loadBan() {

        pnlBan.removeAll();

        ArrayList<Ban> dsBan =
                banDAO.getAllBan();

        for (Ban ban : dsBan) {

            if (
                    ban.getMaBan()
                            .equals(maBanHienTai)
            ) {
                continue;
            }

            pnlBan.add(
                    new BanCard(ban)
            );
        }

        pnlBan.revalidate();

        pnlBan.repaint();
    }

    // =====================================================
    // CARD BÀN
    // =====================================================

    class BanCard extends JPanel {

        private final Ban ban;

        public BanCard(Ban ban) {

            this.ban = ban;

            setOpaque(false);

            setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            setPreferredSize(
                    new Dimension(180,130)
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            10,
                            10,
                            10,
                            10
                    )
            );

            setLayout(
                    new BoxLayout(
                            this,
                            BoxLayout.Y_AXIS
                    )
            );

            JLabel lblTen =
                    new JLabel(
                            ban.getTenBan(),
                            SwingConstants.CENTER
                    );

            lblTen.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            28
                    )
            );

            lblTen.setForeground(Color.WHITE);

            lblTen.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblSC =
                    new JLabel(
                            "Sức chứa: "
                            + ban.getSoChoNgoi()
                    );

            lblSC.setForeground(Color.WHITE);

            lblSC.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblTT =
                    new JLabel(
                            ban.getTrangThai() == null
                                    ? "Bàn trống"
                                    : ban.getTrangThai()
                    );

            lblTT.setForeground(Color.WHITE);

            lblTT.setAlignmentX(CENTER_ALIGNMENT);

            add(Box.createVerticalStrut(12));

            add(lblTen);

            add(Box.createVerticalStrut(10));

            add(lblSC);

            add(Box.createVerticalStrut(10));

            add(lblTT);

            addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent e
                        ) {

                            toggleSelect();
                        }
                    }
            );
        }

        private void toggleSelect() {

            String tt =
                    ban.getTrangThai();

            if (
                    tt != null
                    &&
                    (
                            tt.toLowerCase().contains("đặt")
                            ||
                            tt.toLowerCase().contains("đang chờ")
                    )
            ) {

                JOptionPane.showMessageDialog(
                        TachBan_DigLog.this,
                        "Không thể chọn bàn đã đặt."
                );

                return;
            }

            String maBan =
                    ban.getMaBan();

            if (
                    dsBanChon.contains(maBan)
            ) {

                dsBanChon.remove(maBan);

            } else {

                dsBanChon.add(maBan);
            }

            updateTongSucChua();

            loadMon();

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color bg;

            if (
                    dsBanChon.contains(
                            ban.getMaBan()
                    )
            ) {

                bg = MAU_CHON;

            } else {

                bg = layMau(
                        ban.getTrangThai()
                );
            }

            // SHADOW

            g2.setColor(
                    new Color(0,0,0,25)
            );

            g2.fillRoundRect(
                    4,
                    4,
                    getWidth()-8,
                    getHeight()-8,
                    35,
                    35
            );

            // BACKGROUND

            g2.setColor(bg);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth()-8,
                    getHeight()-8,
                    35,
                    35
            );

            // BORDER

            g2.setColor(
                    new Color(255,255,255,120)
            );

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth()-8,
                    getHeight()-8,
                    35,
                    35
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =====================================================
    // MÀU
    // =====================================================

    private Color layMau(String tt) {

        if (tt == null)
            return MAU_TRONG;

        tt = tt.trim().toLowerCase();

        if (
                tt.contains("đặt")
                ||
                tt.contains("đang chờ")
        ) {

            return MAU_DAT;
        }

        if (
                tt.contains("phục vụ")
        ) {

            return MAU_PHUC_VU;
        }

        return MAU_TRONG;
    }

    // =====================================================
    // LOAD MÓN
    // =====================================================

    private void loadMon() {

        pnlMon.removeAll();

        mapSpinnerBan.clear();

        dsChiTiet =
                chiTietDAO.getChiTietTheoMaHDVaBan(
                        maHDHienTai,
                        maBanHienTai
                );

        if (dsBanChon.isEmpty()) {

            pnlMon.revalidate();

            pnlMon.repaint();

            return;
        }

        // =================================================
        // HEADER
        // =================================================

        JPanel header =
                new JPanel(
                        new GridLayout(
                                1,
                                2 + dsBanChon.size(),
                                10,
                                10
                        )
                );

        header.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        header.add(
                taoHeader("Tên món")
        );

        header.add(
                taoHeader("Tổng")
        );

        for (String maBan : dsBanChon) {

            header.add(
                    taoHeader(maBan)
            );
        }

        pnlMon.add(header);

        pnlMon.add(Box.createVerticalStrut(10));

        // =================================================
        // DATA
        // =================================================

        for (ChiTietHoaDon ct : dsChiTiet) {

            JPanel row =
                    new JPanel(
                            new GridLayout(
                                    1,
                                    2 + dsBanChon.size(),
                                    10,
                                    10
                            )
                    );

            row.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            52
                    )
            );

            String tenMon =
                    ct.getMaMon().getTenMon();

            if (
                    tenMon == null
                    ||
                    tenMon.trim().isEmpty()
            ) {

                tenMon =
                        ct.getMaMon().getMaMon();
            }

            JLabel lblTen =
                    new JLabel(tenMon);

            JLabel lblTong =
                    new JLabel(
                            String.valueOf(
                                    ct.getSoLuong()
                            ),
                            SwingConstants.CENTER
                    );

            row.add(lblTen);

            row.add(lblTong);

            Map<String, JSpinner> mapBan =
                    new LinkedHashMap<>();

            for (String maBan : dsBanChon) {

                JSpinner sp =
                        new JSpinner(
                                new SpinnerNumberModel(
                                        0,
                                        0,
                                        ct.getSoLuong(),
                                        1
                                )
                        );

                mapBan.put(maBan, sp);

                row.add(sp);
            }

            mapSpinnerBan.put(
                    ct.getMaMon().getMaMon(),
                    mapBan
            );

            pnlMon.add(row);

            pnlMon.add(Box.createVerticalStrut(6));
        }

        pnlMon.revalidate();

        pnlMon.repaint();
    }

    // =====================================================
    // HEADER
    // =====================================================

    private JLabel taoHeader(String text) {

        JLabel lbl =
                new JLabel(
                        text,
                        SwingConstants.CENTER
                );

        lbl.setOpaque(true);

        lbl.setBackground(
                new Color(220,230,240)
        );

        lbl.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        return lbl;
    }

    // =====================================================
    // TỔNG SC
    // =====================================================

    private void updateTongSucChua() {

        int tong = 0;

        for (Ban b : banDAO.getAllBan()) {

            if (
                    b.getMaBan()
                            .equals(maBanHienTai)
            ) {

                tong += b.getSoChoNgoi();
            }

            if (
                    dsBanChon.contains(
                            b.getMaBan()
                    )
            ) {

                tong += b.getSoChoNgoi();
            }
        }

        lblTongSucChua.setText(
                "Tổng sức chứa: "
                + tong
        );
    }

 // =====================================================
 // XỬ LÝ TÁCH BÀN
 // =====================================================

 private void xuLyTachBan() {

     if (dsBanChon.isEmpty()) {

         JOptionPane.showMessageDialog(
                 this,
                 "Chọn bàn cần tách."
         );

         return;
     }

     boolean tachHD =
             chkTachHoaDon.isSelected();

     try {

         // =================================================
         // KHÔNG TÁCH HÓA ĐƠN
         // =================================================

         if (!tachHD) {

             for (ChiTietHoaDon ct : dsChiTiet) {

                 String maMon =
                         ct.getMaMon().getMaMon();

                 Map<String, JSpinner> mapBan =
                         mapSpinnerBan.get(maMon);

                 if (mapBan == null)
                     continue;

                 int tongSLTach = 0;

                 for (String maBanMoi : mapBan.keySet()) {

                     int slTach =
                             (Integer)
                                     mapBan
                                             .get(maBanMoi)
                                             .getValue();

                     tongSLTach += slTach;
                 }

                 // =========================================
                 // CHECK QUÁ SL
                 // =========================================

                 if (tongSLTach > ct.getSoLuong()) {

                     JOptionPane.showMessageDialog(
                             this,
                             "Món "
                             + ct.getMaMon().getTenMon()
                             + " vượt quá số lượng."
                     );

                     return;
                 }

                 // =========================================
                 // TÁCH THEO BÀN
                 // =========================================

                 for (String maBanMoi : mapBan.keySet()) {

                     int slTach =
                             (Integer)
                                     mapBan
                                             .get(maBanMoi)
                                             .getValue();

                     if (slTach <= 0)
                         continue;

                     // =====================================
                     // THÊM BÀN VÀO HÓA ĐƠN
                     // =====================================

                     boolean daTonTai =
                             hoaDonBanDAO
                                     .kiemTraBanThuocHoaDon(
                                             maHDHienTai,
                                             maBanMoi
                                     );

                     if (!daTonTai) {

                         hoaDonBanDAO
                                 .themBanVaoHoaDon(
                                         maHDHienTai,
                                         maBanMoi
                                 );
                     }

                     // =====================================
                     // CHUYỂN MÓN SANG BÀN MỚI
                     // =====================================

                     boolean ok =
                             chiTietDAO.capNhatBanChoMon(
                                     maHDHienTai,
                                     maMon,
                                     maBanHienTai,
                                     maBanMoi,
                                     slTach
                             );

                     if (!ok) {

                         JOptionPane.showMessageDialog(
                                 this,
                                 "Lỗi tách món: "
                                 + ct.getMaMon().getTenMon()
                         );

                         return;
                     }

                     // =====================================
                     // UPDATE BÀN
                     // =====================================

                     banDAO.capNhatTrangThaiBan(
                             maBanMoi,
                             "Đang phục vụ"
                     );
                 }
             }

             hoaDonDAO.capNhatTongTien(
                     maHDHienTai
             );

             JOptionPane.showMessageDialog(
                     this,
                     "Tách bàn thành công!\n"
                     + "Các bàn dùng chung hóa đơn."
             );

             tachThanhCong = true;

             dispose();

             return;
         }

         // =================================================
         // TÁCH HÓA ĐƠN
         // =================================================

         for (ChiTietHoaDon ct : dsChiTiet) {

             String maMon =
                     ct.getMaMon().getMaMon();

             Map<String, JSpinner> mapBan =
                     mapSpinnerBan.get(maMon);

             if (mapBan == null)
                 continue;

             int tongSLTach = 0;

             for (String maBanMoi : mapBan.keySet()) {

                 int slTach =
                         (Integer)
                                 mapBan
                                         .get(maBanMoi)
                                         .getValue();

                 tongSLTach += slTach;
             }

             if (tongSLTach > ct.getSoLuong()) {

                 JOptionPane.showMessageDialog(
                         this,
                         "Món "
                         + ct.getMaMon().getTenMon()
                         + " vượt quá số lượng."
                 );

                 return;
             }

             // =============================================
             // TÁCH HÓA ĐƠN
             // =============================================

             for (String maBanMoi : mapBan.keySet()) {

                 int slTach =
                         (Integer)
                                 mapBan
                                         .get(maBanMoi)
                                         .getValue();

                 if (slTach <= 0)
                     continue;

                 String maHDMoi;

                 HoaDon hdMoi =
                         hoaDonDAO
                                 .timHoaDonChuaThanhToanTheoBan(
                                         maBanMoi
                                 );

                 if (hdMoi != null) {

                     maHDMoi =
                             hdMoi.getMaHD();

                 } else {

                     maHDMoi =
                             hoaDonDAO
                                     .taoMaHoaDonMoi();

                     String maNV =
                             taiKhoanDangNhap
                                     .getMaNV()
                                     .getMaNV();

                     List<String> dsBanMoi =
                    	        new ArrayList<>();

                    	dsBanMoi.add(maBanMoi);

                    	hoaDonDAO.themHoaDonMoi(
                    	        maHDMoi,
                    	        dsBanMoi,
                    	        maNV,
                    	        null,
                    	        null,
                    	        "Tại bàn",
                    	        "Chưa thanh toán"
                    	);
                 }

                 boolean ok =
                         chiTietDAO
                                 .tachMonSangHoaDonKhac(
                                         maHDHienTai,
                                         maHDMoi,
                                         maMon,
                                         slTach,
                                         maBanHienTai,
                                         maBanMoi
                                 );

                 if (!ok) {

                     JOptionPane.showMessageDialog(
                             this,
                             "Tách thất bại: "
                             + ct.getMaMon().getTenMon()
                     );

                     return;
                 }

                 banDAO.capNhatTrangThaiBan(
                         maBanMoi,
                         "Đang phục vụ"
                 );

                 hoaDonDAO.capNhatTongTien(
                         maHDMoi
                 );
             }
         }

         hoaDonDAO.capNhatTongTien(
                 maHDHienTai
         );

         JOptionPane.showMessageDialog(
                 this,
                 "Tách hóa đơn thành công!"
         );

         tachThanhCong = true;

         dispose();

     } catch (Exception e) {

         e.printStackTrace();

         JOptionPane.showMessageDialog(
                 this,
                 "Lỗi khi tách bàn."
         );
     }
 }

    public boolean isTachThanhCong() {
        return tachThanhCong;
    }
}