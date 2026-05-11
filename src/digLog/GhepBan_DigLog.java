package digLog;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_Ban_DAO;
import dao.HoaDon_DAO;
import dao.KhuVuc_DAO;
import entity.Ban;
import entity.HoaDon;
import entity.KhuVuc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class GhepBan_DigLog extends JDialog {

    private JTabbedPane tabbedPane;
    private JButton btnGhep, btnHuy;
    private JLabel lblTongSucChua;

    private String maBanHienTai;

    private HoaDon_DAO hoaDonDAO;
    private Ban_DAO banDAO;
    private KhuVuc_DAO khuVucDAO;
    private ChiTietHoaDon_DAO cthdDAO;
    private HoaDon_Ban_DAO hoaDonBanDAO;

    private final Set<String> dsBanChon = new LinkedHashSet<>();

    private final Color MAU_TRONG = new Color(188,220,244);
    private final Color MAU_PHUC_VU = new Color(122,201,113);
    private final Color MAU_DAT = new Color(229,57,53);
    private final Color MAU_CHON = new Color(255,170,80);

    public GhepBan_DigLog(Frame owner, String maBanHienTai) {

        super(owner, true);

        this.maBanHienTai = maBanHienTai;

        hoaDonDAO = new HoaDon_DAO();
        banDAO = new Ban_DAO();
        khuVucDAO = new KhuVuc_DAO();
        cthdDAO = new ChiTietHoaDon_DAO();
        hoaDonBanDAO = new HoaDon_Ban_DAO();

        setTitle("Ghép bàn");
        setSize(1250,760);
        setLocationRelativeTo(owner);

        initUI();
        loadTabs();
        updateTongSucChua();
    }

    private void initUI() {

        JPanel root = new JPanel(new BorderLayout(15,15));
        root.setBorder(new EmptyBorder(15,15,15,15));
        root.setBackground(new Color(245,247,250));

        JLabel lblTitle = new JLabel("GHÉP BÀN",SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial",Font.BOLD,30));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        lblTongSucChua = new JLabel("Tổng sức chứa: 0",SwingConstants.RIGHT);
        lblTongSucChua.setFont(new Font("Arial",Font.BOLD,18));

        top.add(lblTitle,BorderLayout.CENTER);
        top.add(lblTongSucChua,BorderLayout.EAST);

        root.add(top,BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial",Font.BOLD,16));

        root.add(tabbedPane,BorderLayout.CENTER);

        btnGhep = new JButton("Ghép bàn");
        btnHuy = new JButton("Hủy");

        styleButton(btnGhep,true);
        styleButton(btnHuy,false);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,0));
        bottom.setOpaque(false);

        bottom.add(btnHuy);
        bottom.add(btnGhep);

        root.add(bottom,BorderLayout.SOUTH);

        setContentPane(root);

        btnHuy.addActionListener(e -> dispose());
        btnGhep.addActionListener(e -> xuLyGhepBan());
    }

    private void styleButton(JButton btn,boolean primary){
    	btn.setFont(new Font("Arial",Font.BOLD,17));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setPreferredSize(
                new Dimension(150,45)
        );

        btn.setBorderPainted(false);

        btn.setOpaque(true);

        btn.setContentAreaFilled(true);

        if(primary){

            btn.setBackground(
                    new Color(255,170,80)
            );

            btn.setForeground(Color.BLACK);

        }else{

            btn.setBackground(
            		new Color(220,220,220)
            );

            btn.setForeground(Color.BLACK);
        }
    }

    private void loadTabs() {

        tabbedPane.removeAll();

        tabbedPane.addTab(
                "Tất cả",
                taoScrollPane(banDAO.getAllBan())
        );

        ArrayList<KhuVuc> dsKV = khuVucDAO.getAllKhuVuc();

        for(KhuVuc kv : dsKV) {

            tabbedPane.addTab(
                    kv.getTenKhuVuc(),
                    taoScrollPane(
                            banDAO.getBanTheoKhuVuc(
                                    kv.getMaKhuVuc()
                            )
                    )
            );
        }
    }

    private JScrollPane taoScrollPane(ArrayList<Ban> dsBan) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245,247,250));

        JPanel pnlGrid = new JPanel(new GridLayout(0,5,20,20));
        pnlGrid.setBorder(new EmptyBorder(20,20,20,20));
        pnlGrid.setBackground(new Color(245,247,250));

        for(Ban ban : dsBan) {

            if(ban.getMaBan().equals(maBanHienTai)) {
                continue;
            }

            pnlGrid.add(new BanCard(ban));
        }

        wrapper.add(pnlGrid,BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(wrapper);
        sp.setBorder(null);

        return sp;
    }

    class BanCard extends JPanel {

        private final Ban ban;

        public BanCard(Ban ban) {

            this.ban = ban;

            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(190,130));

            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

            JLabel lblTen = new JLabel(ban.getTenBan(),SwingConstants.CENTER);
            lblTen.setFont(new Font("Arial",Font.BOLD,28));
            lblTen.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblSC = new JLabel("Sức chứa: " + ban.getSoChoNgoi());
            lblSC.setFont(new Font("Arial",Font.PLAIN,15));
            lblSC.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblTT = new JLabel(ban.getTrangThai());
            lblTT.setFont(new Font("Arial",Font.BOLD,16));
            lblTT.setAlignmentX(CENTER_ALIGNMENT);

            add(Box.createVerticalStrut(14));
            add(lblTen);
            add(Box.createVerticalStrut(10));
            add(lblSC);
            add(Box.createVerticalStrut(10));
            add(lblTT);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    toggleSelect();
                }
            });
        }

        private void toggleSelect() {

            String maBan = ban.getMaBan();

            HoaDon hdBan =
                    hoaDonDAO.timHoaDonChungTheoBan(
                            maBan
                    );

            HoaDon hdChinh =
                    hoaDonDAO.timHoaDonChungTheoBan(
                            maBanHienTai
                    );

            // =========================
            // BÀN ĐÃ THUỘC HÓA ĐƠN CHUNG
            // =========================

            if(
                    hdBan != null
                    &&
                    hdChinh != null
                    &&
                    hdBan.getMaHD()
                    .equals(
                            hdChinh.getMaHD()
                    )
            ){

                JOptionPane.showMessageDialog(
                        GhepBan_DigLog.this,
                        "Bàn này đã nằm trong hóa đơn chung!"
                );

                return;
            }

            // =========================
            // CHỌN / BỎ CHỌN
            // =========================

            if(dsBanChon.contains(maBan)){

                dsBanChon.remove(maBan);

            }else{

                dsBanChon.add(maBan);
            }

            updateTongSucChua();

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color bg = dsBanChon.contains(ban.getMaBan())
                    ? MAU_CHON
                    : layMauTrangThai(ban.getTrangThai());

            g2.setColor(bg);

            g2.fillRoundRect(
                    0,0,
                    getWidth()-1,
                    getHeight()-1,
                    30,30
            );

            g2.setColor(new Color(210,210,210));

            g2.drawRoundRect(
                    0,0,
                    getWidth()-1,
                    getHeight()-1,
                    30,30
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private void updateTongSucChua() {

        int tong = laySucChuaBan(maBanHienTai);

        for(Ban ban : banDAO.getAllBan()) {

            if(dsBanChon.contains(ban.getMaBan())) {
                tong += ban.getSoChoNgoi();
            }
        }

        lblTongSucChua.setText(
                "Tổng sức chứa: " + tong + " người"
        );
    }

    private int laySucChuaBan(String maBan) {

        for(Ban b : banDAO.getAllBan()) {

            if(b.getMaBan().equals(maBan)) {
                return b.getSoChoNgoi();
            }
        }

        return 0;
    }

    private Color layMauTrangThai(String trangThai) {

        if(trangThai == null) {
            return MAU_TRONG;
        }

        String tt = trangThai.trim().toLowerCase();

        if(tt.equals("đang phục vụ") || tt.equals("bàn đang phục vụ")) {
            return MAU_PHUC_VU;
        }

        if(tt.equals("đang chờ")) {
            return MAU_DAT;
        }

        return MAU_TRONG;
    }

    private void xuLyGhepBan() {

        if(dsBanChon.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn bàn để ghép."
            );

            return;
        }

        HoaDon hdChinh =
                hoaDonDAO.timHoaDonChungTheoBan(
                        maBanHienTai
                );

        if(hdChinh == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy hóa đơn chính."
            );

            return;
        }

        for(String maBan : dsBanChon) {

            try {

                HoaDon hdPhu =
                        hoaDonDAO.timHoaDonChungTheoBan(
                                maBan
                        );

                // ====================================
                // BÀN TRỐNG
                // ====================================

                if(hdPhu == null) {

                    hoaDonBanDAO.themBanVaoHoaDon(
                            hdChinh.getMaHD(),
                            maBan
                    );

                    banDAO.capNhatTrangThaiBan(
                            maBan,
                            "Đang phục vụ"
                    );

                    continue;
                }

                // ====================================
                // BÀN ĐÃ CÓ HÓA ĐƠN
                // ====================================

                hoaDonBanDAO.themBanVaoHoaDon(
                        hdChinh.getMaHD(),
                        maBan
                );

                Connection con =
                        ConnectDB.getConnection();

                String sql =
                        """
                        UPDATE ChiTietHoaDon
                        SET maHD = ?
                        WHERE maHD = ?
                        """;

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(1,hdChinh.getMaHD());
                ps.setString(2,hdPhu.getMaHD());

                ps.executeUpdate();

                hoaDonBanDAO.xoaBanKhoiHoaDon(
                        hdPhu.getMaHD(),
                        maBan
                );

                hoaDonDAO.xoaHoaDon(
                        hdPhu.getMaHD()
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        hoaDonDAO.capNhatTongTien(
                hdChinh.getMaHD()
        );

        JOptionPane.showMessageDialog(
                this,
                "Ghép bàn thành công!"
        );

        dispose();
    }
}