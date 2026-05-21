package digLog;

import dao.Ban_DAO;
import dao.KhuVuc_DAO;
import entity.Ban;
import entity.KhuVuc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ChuyenBan_DigLog extends JDialog {

    private final Ban_DAO banDAO=new Ban_DAO();
    private final KhuVuc_DAO khuVucDAO=new KhuVuc_DAO();

    private String maBanHienTai;
    private String tenBanHienTai;
    private String maBanMoi;

    private JTabbedPane tabbedPane;

    private final Color MAU_TRONG=new Color(188,220,244);
    private final Color MAU_PHUC_VU=new Color(122,201,113);
    private final Color MAU_DAT=new Color(239,83,80);
    private final Color MAU_CHON=new Color(255,170,80);

    public ChuyenBan_DigLog(
            Window owner,
            String maBanHienTai,
            String tenBanHienTai
    ){

        super(
                owner,
                "Chuyển bàn",
                ModalityType.APPLICATION_MODAL
        );

        this.maBanHienTai=maBanHienTai;
        this.tenBanHienTai=tenBanHienTai;

        initUI();

        loadTabs();

        setSize(1150,720);

        setLocationRelativeTo(owner);
    }

    // ================= UI =================

    private void initUI(){

        JPanel root=new JPanel(
                new BorderLayout(15,15)
        );

        root.setBorder(
                new EmptyBorder(15,15,15,15)
        );

        root.setBackground(
                new Color(245,247,250)
        );

        // ===== TOP =====

        JPanel top=new JPanel(
                new BorderLayout()
        );

        top.setOpaque(false);

        JLabel lblTitle=new JLabel(
                "CHUYỂN BÀN",
                SwingConstants.CENTER
        );

        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        JLabel lblBan=new JLabel(
                "Bàn hiện tại: "
                +tenBanHienTai
                +" ("
                +maBanHienTai
                +")"
        );

        lblBan.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        top.add(lblTitle,BorderLayout.CENTER);
        top.add(lblBan,BorderLayout.WEST);

        root.add(top,BorderLayout.NORTH);

        // ===== CENTER =====

        tabbedPane=new JTabbedPane();

        tabbedPane.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        root.add(tabbedPane,BorderLayout.CENTER);

        // ===== BOTTOM =====

        JPanel bottom=new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        15,
                        0
                )
        );

        bottom.setOpaque(false);

        JButton btnHuy=new JButton("Hủy");
        JButton btnDongY=new JButton("Chuyển bàn");

        styleButton(
                btnDongY,
                new Color(255,170,80)
        );

        styleButton(
                btnHuy,
                new Color(220,220,220)
        );

        btnHuy.addActionListener(e->dispose());

        btnDongY.addActionListener(e->{

            if(
                    maBanMoi==null
                    ||
                    maBanMoi.trim().isEmpty()
            ){

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn bàn cần chuyển!"
                );

                return;
            }

            dispose();
            
        });

        bottom.add(btnHuy);
        bottom.add(btnDongY);

        root.add(bottom,BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ================= LOAD TAB =================

    private void loadTabs(){

        tabbedPane.removeAll();

        // ===== TAB TẤT CẢ =====
        ArrayList<String[]> dsTatCa =
                banDAO.getDanhSachBanTheoThoiGian(
                        new java.sql.Timestamp(
                                System.currentTimeMillis()
                        )
                );

        tabbedPane.addTab(
                "Tất cả",
                taoScrollPaneRealtime(
                        dsTatCa,
                        null
                )
        );

        // ===== TAB KHU VỰC =====
        ArrayList<KhuVuc> dsKV =
                khuVucDAO.getAllKhuVuc();

        for(KhuVuc kv : dsKV){

            ArrayList<String[]> dsKVRealtime =
                    banDAO.getDanhSachBanTheoThoiGian(
                            new java.sql.Timestamp(
                                    System.currentTimeMillis()
                            )
                    );

            tabbedPane.addTab(
                    kv.getTenKhuVuc(),
                    taoScrollPaneRealtime(
                            dsKVRealtime,
                            kv.getTenKhuVuc()
                    )
            );
        }
    }
    private JScrollPane taoScrollPaneRealtime(
            ArrayList<String[]> ds,
            String tenKhuVuc
    ){

        ArrayList<Ban> dsBan =
                new ArrayList<>();

        for(String[] row : ds){

            // lọc theo khu vực
            if(
                    tenKhuVuc != null
                    &&
                    !tenKhuVuc.equalsIgnoreCase(
                            row[4]
                    )
            ){
                continue;
            }

            Ban ban = new Ban();

            ban.setMaBan(row[0]);
            ban.setTenBan(row[1]);
            ban.setSoChoNgoi(
                    Integer.parseInt(row[2])
            );

            // realtime từ DB
            ban.setTrangThai(row[3]);

            dsBan.add(ban);
        }

        return taoScrollPane(dsBan);
    }

    // ================= PANEL =================

    private JScrollPane taoScrollPane(
            ArrayList<Ban> dsBan
    ){

        JPanel wrapper=new JPanel(
                new BorderLayout()
        );

        wrapper.setBackground(
                new Color(245,247,250)
        );

        JPanel grid=new JPanel(
                new GridLayout(
                        0,
                        5,
                        20,
                        20
                )
        );

        grid.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        grid.setBackground(
                new Color(245,247,250)
        );

        for(Ban ban:dsBan){

            if(
                    ban.getMaBan()
                    .equalsIgnoreCase(maBanHienTai)
            ){
                continue;
            }

            grid.add(
                    new BanCard(ban)
            );
        }

        wrapper.add(grid,BorderLayout.NORTH);

        JScrollPane sp=
                new JScrollPane(wrapper);

        sp.setBorder(null);

        sp.getVerticalScrollBar()
                .setUnitIncrement(14);

        return sp;
    }

    // ================= CARD =================

    class BanCard extends JPanel {

        private final Ban ban;

        public BanCard(Ban ban){

            this.ban=ban;

            setOpaque(false);

            setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            setPreferredSize(
                    new Dimension(190,130)
            );

            setLayout(
                    new BoxLayout(
                            this,
                            BoxLayout.Y_AXIS
                    )
            );

            JLabel lblTen=new JLabel(
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

            lblTen.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblSC=new JLabel(
                    "Sức chứa: "
                    +ban.getSoChoNgoi()
            );

            lblSC.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            15
                    )
            );

            lblSC.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lblTT=new JLabel(
                    ban.getTrangThai()
            );

            lblTT.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            16
                    )
            );

            lblTT.setAlignmentX(CENTER_ALIGNMENT);

            add(Box.createVerticalStrut(14));
            add(lblTen);
            add(Box.createVerticalStrut(10));
            add(lblSC);
            add(Box.createVerticalStrut(10));
            add(lblTT);

            addMouseListener(
                    new java.awt.event.MouseAdapter(){

                    	@Override
                    	public void mouseClicked(
                    	        java.awt.event.MouseEvent e
                    	){

                    		Ban banMoi =
                    	            banDAO.getBanTheoMa(
                    	                    ban.getMaBan()
                    	            );

                    		String tt =
                    		        banMoi != null
                    		        && banMoi.getTrangThai() != null
                    		        ? banMoi.getTrangThai()
                    		                .trim()
                    		                .toLowerCase()
                    		        : "";

                    	    if(
                    	            tt.contains("đang phục vụ")
                    	            ||
                    	            tt.contains("đang chờ")
                    	    ){

                    	        JOptionPane.showMessageDialog(
                    	                ChuyenBan_DigLog.this,
                    	                "Không thể chuyển vào bàn đang sử dụng!"
                    	        );

                    	        return;
                    	    }

                    	    maBanMoi =
                    	            ban.getMaBan();

                    	    repaint();

                    	    tabbedPane.repaint();
                    	}
                    }
            );
        }

        @Override
        protected void paintComponent(Graphics g){

            Graphics2D g2=
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color bg;

            if(
                    ban.getMaBan()
                    .equals(maBanMoi)
            ){

                bg=MAU_CHON;

            }else{

                bg=layMauTrangThai(
                        ban.getTrangThai()
                );
            }

            g2.setColor(bg);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth()-1,
                    getHeight()-1,
                    30,
                    30
            );

            g2.setColor(
                    new Color(210,210,210)
            );

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth()-1,
                    getHeight()-1,
                    30,
                    30
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // ================= STYLE =================

    private void styleButton(
            JButton btn,
            Color color
    ){

        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        btn.setPreferredSize(
                new Dimension(160,45));
        btn.setFont(new Font("Arial",Font.BOLD,17));
    }

    private Color layMauTrangThai(
            String trangThai
    ){

        if(trangThai==null){
            return MAU_TRONG;
        }

        String tt=
                trangThai
                .trim()
                .toLowerCase();

        if(
                tt.contains("đang phục vụ")
        ){

            return MAU_PHUC_VU;
        }

        if(
                tt.contains("đang chờ")
        ){

            return MAU_DAT;
        }

        return MAU_TRONG;
    }

    // ================= GET =================

    public String getMaBanMoi(){

        return maBanMoi;
    }
}