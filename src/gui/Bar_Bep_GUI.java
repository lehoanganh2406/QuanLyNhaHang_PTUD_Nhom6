package gui;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;

public class Bar_Bep_GUI extends JPanel {

    private JPanel pnTrai,pnPhai;
    private ChiTietHoaDon_DAO dao=new ChiTietHoaDon_DAO();
    private HoaDon_DAO hdDAO =
            new HoaDon_DAO();
    private Set<String> danhSachDaThongBao = new HashSet<>();

    private final Color BG_MAIN=new Color(245,247,250);
    private final Color BG_HEADER2=new Color(0xD9D9D9);
    private final Color BG_HEADER=new Color(0xEFEDED);
    private final Color BG_SL=new Color(140,152,164);
    private final Color BG_ACTION=new Color(139,195,74);
    private final Color BG_TOTAL=new Color(52,152,219);
    private final Color BG_DONE=new Color(76,175,80);

    public Bar_Bep_GUI(){

        setLayout(new GridLayout(1,2,6,0));

        pnTrai=taoCot();
        pnPhai=taoCot();

        add(taoPanelCoHeader(
                "☕ NHÂN VIÊN ORDER",
                pnTrai
        ));

        add(taoPanelCoHeader(
                "● CHẾ BIẾN XONG => CHỜ GIAO",
                pnPhai
        ));

        loadData();

        new Timer(2000,e->loadData()).start();
    }

    // ================= PANEL =================

    private JPanel taoCot(){

        JPanel p=new JPanel();

        p.setLayout(
                new BoxLayout(
                        p,
                        BoxLayout.Y_AXIS
                )
        );

        p.setBackground(BG_MAIN);

        return p;
    }

    private JPanel taoPanelCoHeader(
            String title,
            JPanel content
    ){

        JPanel root=new JPanel(
                new BorderLayout()
        );

        JPanel top=new JPanel(
                new BorderLayout()
        );

        top.setBackground(BG_HEADER);

        JLabel lbl=new JLabel(title);

        lbl.setForeground(Color.BLACK);

        lbl.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        lbl.setBorder(new EmptyBorder(12,18,12,18));

        top.add(lbl,BorderLayout.NORTH);
        top.add(taoHeader(),BorderLayout.SOUTH);

        root.add(top,BorderLayout.NORTH);
        root.add(taoScroll(content),BorderLayout.CENTER);

        return root;
    }

    private JScrollPane taoScroll(JPanel panel){

        JScrollPane sp=new JScrollPane(panel);

        sp.setBorder(null);

        sp.getVerticalScrollBar()
                .setUnitIncrement(14);

        return sp;
    }

    // ================= HEADER =================

    private JPanel taoHeader(){

        JPanel p=new JPanel(
                new GridLayout(1,4)
        );

        p.setPreferredSize(
                new Dimension(1000,54)
        );

        p.setBackground(BG_HEADER2);

        p.add(header("THỰC ĐƠN"));
        p.add(header("THỜI GIAN"));
        p.add(header("SL"));
        p.add(header("TỔNG"));

        return p;
    }

    private JLabel header(String s){

        JLabel lbl=new JLabel(
                s,
                SwingConstants.CENTER
        );

        lbl.setForeground(Color.BLACK);

        lbl.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        return lbl;
    }

    // ================= LOAD =================

    private void loadData(){

        pnTrai.removeAll();
        pnPhai.removeAll();

        List<ChiTietHoaDon> dsTrai =
                dao.getMonTheoTrangThai(
                        "Đã gửi bếp"
                );

        boolean coMonMoi = false;

        for(ChiTietHoaDon ct : dsTrai){

            String key =
                    ct.getMaHD().getMaHD()
                    + "_"
                    + ct.getMaMon().getMaMon()
                    + "_"
                    + ct.getMaBan().getMaBan();

            if(!danhSachDaThongBao.contains(key)){

                danhSachDaThongBao.add(key);

                coMonMoi = true;
            }
        }

        if(coMonMoi){
            phatAmThanh();
        }

        sapXep(dsTrai);

        for(ChiTietHoaDon ct:dsTrai){

            pnTrai.add(
                    taoCard(
                            ct,
                            false,
                            dsTrai
                    )
            );
        }

        List<ChiTietHoaDon> dsPhai=
                dao.getMonTheoTrangThai(
                        "Đang chế biến"
                );

        sapXep(dsPhai);

        for(ChiTietHoaDon ct:dsPhai){

            pnPhai.add(
                    taoCard(
                            ct,
                            true,
                            dsPhai
                    )
            );
        }

        revalidate();
        repaint();
    }

    private void sapXep(List<ChiTietHoaDon> ds){

        ds.sort((a,b)->{

            long maxA=ds.stream()
                    .filter(x->
                            x.getMaMon()
                            .getMaMon()
                            .equals(
                                    a.getMaMon()
                                    .getMaMon()
                            )
                    )
                    .mapToLong(x->
                            x.getThoiGianGui()==null
                            ? 0
                            : Duration.between(
                                    x.getThoiGianGui(),
                                    LocalDateTime.now()
                            ).toMinutes()
                    )
                    .max()
                    .orElse(0);

            long maxB=ds.stream()
                    .filter(x->
                            x.getMaMon()
                            .getMaMon()
                            .equals(
                                    b.getMaMon()
                                    .getMaMon()
                            )
                    )
                    .mapToLong(x->
                            x.getThoiGianGui()==null
                            ? 0
                            : Duration.between(
                                    x.getThoiGianGui(),
                                    LocalDateTime.now()
                            ).toMinutes()
                    )
                    .max()
                    .orElse(0);

            if(maxA!=maxB){
                return Long.compare(maxB,maxA);
            }

            return a.getMaMon()
                    .getTenMon()
                    .compareToIgnoreCase(
                            b.getMaMon()
                            .getTenMon()
                    );
        });
    }

    private int tinhTongMon(
            List<ChiTietHoaDon> ds,
            String maMon
    ){

        int tong=0;

        for(ChiTietHoaDon ct:ds){

            if(
                    ct.getMaMon()
                    .getMaMon()
                    .equals(maMon)
            ){

                tong+=ct.getSoLuong();
            }
        }

        return tong;
    }

    // ================= CARD =================

    private JPanel taoCard(
            ChiTietHoaDon ct,
            boolean dangCheBien,
            List<ChiTietHoaDon> ds
    ){

        JPanel card = new JPanel(new GridLayout(1,4));

        card.setPreferredSize(new Dimension(0,88));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,88));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(
                0,0,1,0,
                new Color(230,230,230)
        ));

        // ===== MÓN =====

        JPanel pnMon = new JPanel(new FlowLayout(
                FlowLayout.LEFT,10,10
        ));

        pnMon.setOpaque(false);

        JLabel lblTen = new JLabel(ct.getMaMon().getTenMon());
        lblTen.setFont(new Font("Arial",Font.BOLD,18));

        JLabel lblNote = new JLabel(
                "✎ " + (ct.getGhiChu()==null ? "" : ct.getGhiChu())
        );

        lblNote.setForeground(Color.GRAY);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));

        info.add(lblTen);
        info.add(lblNote);

        pnMon.add(info);

        // ===== THỜI GIAN =====

        String thongTinBan;

        if(
                ct.getMaHD()!=null
                &&
                "Mang về".equalsIgnoreCase(
                        ct.getMaHD().getHinhThucPhucVu()
                )
        ){

            thongTinBan=
                    "Bàn "
                    +ct.getMaBan().getMaBan()
                    +" (Mang về)"
                    +" ("
                    +ct.getMaHD().getMaHD()
                    +")";

        }else{
        	String dsBan =
        	        hdDAO.layChuoiBanTheoHD(
        	                ct.getMaHD().getMaHD()
        	        );

        	if(dsBan.contains(",")){

        	    // món đặt trước chung
        	    boolean laMonDatTruocChung =
        	            ct.getTrangThai()
        	            .equalsIgnoreCase("Đã gửi bếp")
        	            &&
        	            ct.getGhiChu() != null
        	            &&
        	            ct.getGhiChu().contains("[DAT_CHUNG]");

        	    if(laMonDatTruocChung){

        	        thongTinBan =
        	                dsBan.replace(", ", "+");
        	    }
        	    else{

        	        // order trực tiếp sau nhận bàn
        	        thongTinBan =
        	                ct.getMaBan().getMaBan();
        	    }

        	}else{

        	    thongTinBan =
        	            ct.getMaBan().getMaBan();
        	}
        }

        long phut = ct.getThoiGianGui()==null
                ? 0
                : Duration.between(
                        ct.getThoiGianGui(),
                        LocalDateTime.now()
                ).toMinutes();

        JPanel pnTime = new JPanel();
        pnTime.setOpaque(false);
        pnTime.setLayout(new BoxLayout(
                pnTime,
                BoxLayout.Y_AXIS
        ));

        JLabel lblBan = new JLabel(thongTinBan);

        lblBan.setOpaque(true);
        lblBan.setBackground(new Color(233,77,61));
        lblBan.setForeground(Color.WHITE);
        lblBan.setFont(new Font("Arial",Font.BOLD,13));

        JLabel lblTime = new JLabel("🕒 " + phut + " phút trước");
        lblTime.setFont(new Font("Arial",Font.PLAIN,12));

        pnTime.add(lblBan);
        pnTime.add(lblTime);

        // ===== SỐ LƯỢNG =====

        JPanel pnSL = new JPanel(new FlowLayout(
                FlowLayout.CENTER,4,12
        ));

        pnSL.setOpaque(false);

        JLabel lblSL = new JLabel(
                String.valueOf(ct.getSoLuong()),
                SwingConstants.CENTER
        );

        lblSL.setOpaque(true);
        lblSL.setBackground(BG_SL);
        lblSL.setForeground(Color.WHITE);
        lblSL.setFont(new Font("Arial",Font.BOLD,24));
        lblSL.setPreferredSize(new Dimension(58,58));

        JButton btn2 = new JButton(
                dangCheBien ? "✓" : ">"
        );

        style(
                btn2,
                dangCheBien ? BG_DONE : BG_ACTION
        );

        btn2.addActionListener(e -> {

            dao.capNhatTrangThai(
                    ct.getMaHD().getMaHD(),
                    ct.getMaMon().getMaMon(),
                    ct.getMaBan().getMaBan(),
                    dangCheBien
                            ? "Hoàn thành"
                            : "Đang chế biến"
            );

            loadData();
        });

        pnSL.add(lblSL);
        pnSL.add(btn2);

        // ===== TỔNG =====

        JPanel pnTong = new JPanel(new FlowLayout(
                FlowLayout.CENTER,4,12
        ));

        pnTong.setOpaque(false);

        boolean hienTong = true;

        for(ChiTietHoaDon x : ds){

            if(x == ct) break;

            if(
                    x.getMaMon().getMaMon()
                    .equals(ct.getMaMon().getMaMon())
            ){
                hienTong = false;
                break;
            }
        }

        if(hienTong){

            JLabel lblTong = new JLabel(
                    String.valueOf(
                            tinhTongMon(
                                    ds,
                                    ct.getMaMon().getMaMon()
                            )
                    ),
                    SwingConstants.CENTER
            );

            lblTong.setOpaque(true);
            lblTong.setBackground(BG_TOTAL);
            lblTong.setForeground(Color.WHITE);
            lblTong.setFont(new Font("Arial",Font.BOLD,24));
            lblTong.setPreferredSize(new Dimension(58,58));

            JButton btnAll = new JButton(
                    dangCheBien ? "✓✓" : ">>"
            );

            style(
                    btnAll,
                    dangCheBien
                    ? new Color(56,142,60)
                    : new Color(255,152,0)
            );

            btnAll.setPreferredSize(
                    new Dimension(90,58)
            );

            btnAll.addActionListener(e -> {

                for(ChiTietHoaDon item : ds){

                    if(
                            item.getMaMon().getMaMon()
                            .equals(ct.getMaMon().getMaMon())
                    ){

                        dao.capNhatTrangThai(
                                item.getMaHD().getMaHD(),
                                item.getMaMon().getMaMon(),
                                item.getMaBan().getMaBan(),
                                dangCheBien
                                        ? "Hoàn thành"
                                        : "Đang chế biến"
                        );
                    }
                }

                loadData();
            });

            pnTong.add(lblTong);
            pnTong.add(btnAll);
        }

        card.add(pnMon);
        card.add(pnTime);
        card.add(pnSL);
        card.add(pnTong);

        return card;
    }

    // ================= STYLE =================

    private void style(
            JButton btn,
            Color color
    ){

        btn.setBackground(color);

        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);

        btn.setBorderPainted(false);

        btn.setContentAreaFilled(true);

        btn.setOpaque(true);

        btn.setMargin(
                new Insets(0,0,0,0)
        );

        btn.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        btn.setPreferredSize(
                new Dimension(58,58)
        );

        btn.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );
    }
    private void phatAmThanh() {

        try {

            File file = new File("libs/notification.wav");

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(file);

            Clip clip = AudioSystem.getClip();

            clip.open(audio);

            clip.start();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}