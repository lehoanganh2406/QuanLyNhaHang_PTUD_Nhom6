package digLog;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.TaiKhoan;

public class TachBan_DigLog extends JDialog {
    private static final long serialVersionUID = 1L;

    private Ban_DAO banDAO = new Ban_DAO();
    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private ChiTietHoaDon_DAO chiTietDAO = new ChiTietHoaDon_DAO();

    private TaiKhoan taiKhoanDangNhap;
    private String maBanHienTai;
    private String maHDHienTai;

    private JComboBox<BanItem> cboBan;
    private JPanel pnMon;
    private JButton btnXacNhan;
    private JButton btnHuy;

    private List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
    private Map<String, JSpinner> mapSpinner = new LinkedHashMap<>();

    private boolean tachThanhCong = false;
    private String maBanMoi;

    public TachBan_DigLog(Window owner, TaiKhoan tk, String maBanHienTai, String maHDHienTai) {
        super(owner, "Tách bàn", ModalityType.APPLICATION_MODAL);
        this.taiKhoanDangNhap = tk;
        this.maBanHienTai = maBanHienTai;
        this.maHDHienTai = maHDHienTai;

        initComponents();
        loadBanTrong();
        loadMonDangCo();
        initEvents();

        setSize(620, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        setContentPane(root);

        JLabel lblTitle = new JLabel("TÁCH BÀN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(new Color(60, 100, 140));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel pnTop = new JPanel(new BorderLayout(10, 8));
        pnTop.setOpaque(false);

        JLabel lblBan = new JLabel("Chọn bàn muốn tách sang:");
        lblBan.setFont(new Font("SansSerif", Font.BOLD, 16));

        cboBan = new JComboBox<>();
        cboBan.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cboBan.setPreferredSize(new Dimension(330, 38));

        pnTop.add(lblBan, BorderLayout.WEST);
        pnTop.add(cboBan, BorderLayout.CENTER);

        root.add(pnTop, BorderLayout.BEFORE_FIRST_LINE);

        pnMon = new JPanel();
        pnMon.setLayout(new BoxLayout(pnMon, BoxLayout.Y_AXIS));
        pnMon.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(pnMon);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnBottom.setOpaque(false);

        btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnHuy.setPreferredSize(new Dimension(105, 38));

        btnXacNhan = new JButton("Xác nhận tách");
        btnXacNhan.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnXacNhan.setPreferredSize(new Dimension(155, 38));

        pnBottom.add(btnHuy);
        pnBottom.add(btnXacNhan);
        root.add(pnBottom, BorderLayout.SOUTH);
    }

    private void loadBanTrong() {
        cboBan.removeAllItems();

        ArrayList<Ban> dsBan = banDAO.getAllBan();

        for (Ban b : dsBan) {
            if (b == null) continue;
            if (b.getMaBan() == null) continue;
            if (b.getMaBan().equalsIgnoreCase(maBanHienTai)) continue;

            String trangThai = b.getTrangThai() == null ? "" : b.getTrangThai().trim();

            if (trangThai.equalsIgnoreCase("Bàn trống")
                    || trangThai.equalsIgnoreCase("Trống")
                    || trangThai.isEmpty()) {
                cboBan.addItem(new BanItem(b.getMaBan(), b.getTenBan()));
            }
        }
    }

    private void loadMonDangCo() {
        pnMon.removeAll();
        mapSpinner.clear();

        dsChiTiet = chiTietDAO.getChiTietTheoMaHD(maHDHienTai);

        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không có món nào để tách.", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("SansSerif", Font.ITALIC, 16));
            lblEmpty.setBorder(new EmptyBorder(25, 10, 25, 10));
            pnMon.add(lblEmpty);
            return;
        }

        JPanel header = new JPanel(new GridLayout(1, 4));
        header.setBackground(new Color(206, 227, 242));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        header.add(createHeader("Mã món"));
        header.add(createHeader("Số lượng hiện có"));
        header.add(createHeader("Đơn giá"));
        header.add(createHeader("SL tách"));

        pnMon.add(header);

        for (ChiTietHoaDon ct : dsChiTiet) {
            if (ct == null || ct.getMaMon() == null) continue;

            String maMon = ct.getMaMon().getMaMon();
            int soLuong = ct.getSoLuong();

            JPanel row = new JPanel(new GridLayout(1, 4));
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            JLabel lblMa = createCell(maMon);
            JLabel lblSL = createCell(String.valueOf(soLuong));
            JLabel lblGia = createCell(String.format("%,.0f", ct.getDonGia()));

            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, soLuong, 1));
            spinner.setFont(new Font("SansSerif", Font.PLAIN, 15));

            JPanel spinWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            spinWrap.setOpaque(false);
            spinWrap.add(spinner);

            row.add(lblMa);
            row.add(lblSL);
            row.add(lblGia);
            row.add(spinWrap);

            mapSpinner.put(maMon, spinner);
            pnMon.add(row);
        }

        pnMon.revalidate();
        pnMon.repaint();
    }

    private JLabel createHeader(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JLabel createCell(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return lbl;
    }

    private void initEvents() {
        btnHuy.addActionListener(e -> dispose());
        btnXacNhan.addActionListener(e -> xuLyTachBan());
    }

    private void xuLyTachBan() {
        BanItem banItem = (BanItem) cboBan.getSelectedItem();

        if (banItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn muốn tách sang.");
            return;
        }

        boolean coMonTach = false;
        for (JSpinner sp : mapSpinner.values()) {
            int sl = (Integer) sp.getValue();
            if (sl > 0) {
                coMonTach = true;
                break;
            }
        }

        if (!coMonTach) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn số lượng món cần tách.");
            return;
        }

        maBanMoi = banItem.maBan;

        HoaDon hdBanMoi = hoaDonDAO.timHoaDonChuaThanhToanTheoBan(maBanMoi);
        String maHDMoi;

        if (hdBanMoi != null) {
            maHDMoi = hdBanMoi.getMaHD();
        } else {
            maHDMoi = hoaDonDAO.taoMaHoaDonMoi();

            if (maHDMoi == null || maHDMoi.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tạo được mã hóa đơn mới.");
                return;
            }

            String maNV = null;
            if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNV() != null) {
                maNV = taiKhoanDangNhap.getMaNV().getMaNV();
            }

            boolean taoHD = hoaDonDAO.themHoaDonMoi(
                    maHDMoi,
                    maBanMoi,
                    maNV,
                    null,
                    null,
                    "Tại bàn",
                    "Chưa thanh toán"
            );

            if (!taoHD) {
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn bàn mới thất bại.");
                return;
            }
        }

        for (ChiTietHoaDon ct : dsChiTiet) {
            if (ct == null || ct.getMaMon() == null) continue;

            String maMon = ct.getMaMon().getMaMon();
            JSpinner sp = mapSpinner.get(maMon);
            if (sp == null) continue;

            int soLuongTach = (Integer) sp.getValue();
            int soLuongHienCo = ct.getSoLuong();

            if (soLuongTach <= 0) {
                continue;
            }

            if (soLuongTach > soLuongHienCo) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng tách của món " + maMon + " không được lớn hơn số lượng hiện có.");
                sp.setValue(soLuongHienCo);
                return;
            }

            boolean ok = chiTietDAO.tachMonSangHoaDonKhac(
                    maHDHienTai,
                    maHDMoi,
                    maMon,
                    soLuongTach
            );

            if (!ok) {
                JOptionPane.showMessageDialog(this, "Tách món thất bại: " + maMon);
                return;
            }
        }

        hoaDonDAO.capNhatTongTien(maHDHienTai);
        hoaDonDAO.capNhatTongTien(maHDMoi);

        banDAO.capNhatTrangThaiBan(maBanMoi, "Đang phục vụ");

        if (kiemTraHoaDonConMon(maHDHienTai)) {
            banDAO.capNhatTrangThaiBan(maBanHienTai, "Đang phục vụ");
        } else {
            banDAO.capNhatTrangThaiBan(maBanHienTai, "Bàn trống");
        }

        tachThanhCong = true;
        JOptionPane.showMessageDialog(this, "Tách bàn thành công!");
        dispose();
    }

    private boolean kiemTraHoaDonConMon(String maHD) {
        List<ChiTietHoaDon> ds = chiTietDAO.getChiTietTheoMaHD(maHD);
        return ds != null && !ds.isEmpty();
    }

    public boolean isTachThanhCong() {
        return tachThanhCong;
    }

    public String getMaBanMoi() {
        return maBanMoi;
    }

    static class BanItem {
        String maBan;
        String tenBan;

        BanItem(String maBan, String tenBan) {
            this.maBan = maBan;
            this.tenBan = tenBan;
        }

        @Override
        public String toString() {
            return tenBan + " - " + maBan;
        }
    }
}