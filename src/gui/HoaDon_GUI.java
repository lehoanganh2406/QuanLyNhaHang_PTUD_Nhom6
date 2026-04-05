package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.toedter.calendar.JDateChooser;

public class HoaDon_GUI extends JPanel{
	private static final Color CLR_HEADER_BG  = new Color(74, 55, 40);   // nâu đậm
    private static final Color CLR_HEADER_FG  = Color.WHITE;
    private static final Color CLR_FORM_BG    = new Color(245, 242, 235); // kem nhạt
    private static final Color CLR_PANEL_BG   = new Color(238, 234, 222); // nền chính
    private static final Color CLR_BTN_ADD    = new Color(102, 187, 106); // xanh lá
    private static final Color CLR_BTN_UPDATE = new Color(255, 213, 79);  // vàng
    private static final Color CLR_BTN_RESET  = new Color(255, 213, 79);  // vàng
    private static final Color CLR_BTN_SEARCH = new Color(100, 181, 246); // xanh dương nhạt
    private static final Color CLR_TABLE_HDR  = new Color(200, 192, 175); // xám nâu
    private static final Color CLR_BORDER     = new Color(160, 145, 120); // viền
    
    public HoaDon_GUI() {
    	setLayout(new BorderLayout());
        setBackground(CLR_PANEL_BG);
        
        add(buildTitlePanel(),  BorderLayout.NORTH);

	}
    private JPanel buildTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(CLR_HEADER_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lbl = new JLabel("DANH SÁCH HÓA ĐƠN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setForeground(CLR_HEADER_FG);
        lbl.setOpaque(false);
        pnl.add(lbl, BorderLayout.CENTER);
        return pnl;
    }
 // 2. PHẦN TRUNG TÂM (form + nút + bảng)
    private JPanel buildCenterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(CLR_PANEL_BG);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topArea = new JPanel(new BorderLayout(0, 4));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel(),   BorderLayout.NORTH);
//        topArea.add(buildButtonPanel(), BorderLayout.CENTER);

        pnl.add(topArea,           BorderLayout.NORTH);
//        pnl.add(buildTablePanel(), BorderLayout.CENTER);
        return pnl;
    }

    // 3. FORM NHẬP LIỆU
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 0));
        outer.setBackground(CLR_FORM_BG);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // ── Phần trái: các trường thông tin 
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

//        txtMaNV  = createTextField(16);
//        txtMaNV.setEnabled(false); //  không cho nhập tay
//        txtMaNV.setDisabledTextColor(new Color(60, 60, 60));
//        cbChucVu = new JComboBox<>(new String[]{"Quản lý", "Nhân viên lễ tân"});
//        styleComboBox(cbChucVu);
//
//        addRow(pnlFields, gbc, 0, "Mã Nhân Viên", txtMaNV, "Chức vụ", cbChucVu);
//
//        // Hàng 1: Họ tên / Email
//        txtHoTen = createTextField(16);
//        txtEmail = createTextField(16);
//        addRow(pnlFields, gbc, 1, "Họ tên", txtHoTen, "Email", txtEmail);
//
//        // Hàng 2: Ngày sinh / SĐT
////        txtNgaySinh = createTextField(16);
//        txtNgaySinh = new JDateChooser();
//        txtNgaySinh.setDateFormatString("dd/MM/yyyy");
//        txtNgaySinh.setPreferredSize(new Dimension(160, 28));
//        txtSDT      = createTextField(16);
//        addRow(pnlFields, gbc, 2, "Ngày sinh", txtNgaySinh, "SĐT:", txtSDT);
//        
//
//        // Hàng 3: Giới tính / Trạng thái
//        rdNam = new JRadioButton("Nam"); rdNam.setOpaque(false);
//        rdNu  = new JRadioButton("Nữ");  rdNu.setOpaque(false);
//        rdNam.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        rdNu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        bgGioiTinh = new ButtonGroup();
//        bgGioiTinh.add(rdNam); bgGioiTinh.add(rdNu);
//        rdNam.setSelected(true);
//
//        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
//        pnlGT.setOpaque(false);
//        pnlGT.add(rdNam); pnlGT.add(rdNu);
//
//        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc"});
//        styleComboBox(cbTrangThai);
//        cbTrangThai.setSelectedItem("Nghỉ việc");
//
//        addRowCustomLeft(pnlFields, gbc, 3, "Giới tính", pnlGT, "Trạng thái", cbTrangThai);
//        
//        txtCCCD = createTextField(16);
//        JLabel lblEmpty = new JLabel("");  // ô trái bỏ trống
//        addRow(pnlFields, gbc, 4, "CCCD", txtCCCD, "", lblEmpty);
//        outer.add(pnlFields, BorderLayout.CENTER);
//
//        // ── Phần phải: ảnh nhân viên 
//        JPanel pnlAnh = new JPanel(new BorderLayout(0, 4));
//        pnlAnh.setOpaque(false);
//        pnlAnh.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
//
//        JLabel lblTitle = new JLabel("Ảnh nhân viên", SwingConstants.CENTER);
//        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//
//        lblAnh = new JLabel() ;
////        {
////            @Override
////            protected void paintComponent(Graphics g) {
////                super.paintComponent(g);
////                Graphics2D g2 = (Graphics2D) g.create();
////                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
////                // Nền xám nhạt
////                g2.setColor(new Color(210, 205, 195));
////                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
////                // Icon avatar đơn giản
////                g2.setColor(new Color(160, 155, 145));
////                int cx = getWidth() / 2;
////                int cy = getHeight() / 2 - 8;
////                g2.fillOval(cx - 18, cy - 20, 36, 36);          // đầu
////                g2.fillRoundRect(cx - 28, cy + 18, 56, 32, 28, 28); // thân
////                g2.dispose();
////            }
////        };
//        lblAnh.setPreferredSize(new Dimension(150, 150));
//        lblAnh.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
//        lblAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        lblAnh.setToolTipText("Nhấn để chọn ảnh");
//        lblAnh.addMouseListener(new MouseAdapter() {
//            @Override public void mouseClicked(MouseEvent e) { chonAnh(); }
//        });
//
//        pnlAnh.add(lblTitle, BorderLayout.NORTH);
//        pnlAnh.add(lblAnh,   BorderLayout.CENTER);
//        outer.add(pnlAnh, BorderLayout.EAST);

        return outer;
    }
    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
//            catch (Exception ignored) {}
//
//            JFrame frame = new JFrame("Quản Lý hóa đơn ");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////            frame.setSize(900, 580);
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // bỏ setSize
//            frame.setLocationRelativeTo(null);
//
//            // ── Giả lập khoảng trắng cho MenuBar sẽ ghép sau ─────────────────
//            JPanel menuPlaceholder = new JPanel();
//            menuPlaceholder.setBackground(new Color(74, 55, 40));
//            menuPlaceholder.setPreferredSize(new Dimension(0, 35));
//            JLabel note = new JLabel("  [ MenuBar ]");
//            note.setForeground(new Color(200, 190, 170));
//            note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
//            menuPlaceholder.setLayout(new BorderLayout());
//            menuPlaceholder.add(note, BorderLayout.WEST);
//
//            frame.setLayout(new BorderLayout());
//            frame.add(menuPlaceholder,        BorderLayout.NORTH);
//            frame.add(new HoaDon_GUI(), BorderLayout.CENTER);
//            frame.setVisible(true);
//        });
//    }

}
