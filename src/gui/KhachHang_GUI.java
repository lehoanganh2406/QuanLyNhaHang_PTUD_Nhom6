package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import entity.TaiKhoan;

public class KhachHang_GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private TaiKhoan taiKhoanDangNhap;

    private JTextField txtMaKH, txtTenKH, txtDiemTichLuy, txtSDT;
    private JComboBox<String> cbLoaiKH;
    private JButton btnThem, btnXoa, btnTraCuu, btnLamMoi, btnCapNhat;
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;

    public KhachHang_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Quản Lý Khách Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu
        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        add(menu, BorderLayout.NORTH);

        // Center Panel
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(250, 246, 241)); // Light beige background

        // Title
        JLabel lblTitle = new JLabel("DANH SÁCH KHÁCH HÀNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(125, 86, 64)); // Dark brown background
        lblTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelCenter.add(lblTitle, BorderLayout.NORTH);

        // Content
        JPanel panelContent = new JPanel(new BorderLayout());
        panelContent.setOpaque(false);
        panelContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Form Section ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(150, 150, 150), 1),
                new EmptyBorder(10, 20, 10, 20)));

        Font fontLabel = new Font("Arial", Font.BOLD, 17);
        Font fontInput = new Font("Arial", Font.PLAIN, 18);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cột 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblMaKH = new JLabel("Mã KH:");
        lblMaKH.setFont(fontLabel);
        panelForm.add(lblMaKH, gbc);

        gbc.gridx = 1;
        txtMaKH = new JTextField(15);
        txtMaKH.setFont(fontInput);
        txtMaKH.setBackground(new Color(217, 217, 217));
        txtMaKH.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        panelForm.add(txtMaKH, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTenKH = new JLabel("Tên KH :");
        lblTenKH.setFont(fontLabel);
        panelForm.add(lblTenKH, gbc);

        gbc.gridx = 1;
        txtTenKH = new JTextField(15);
        txtTenKH.setFont(fontInput);
        txtTenKH.setBackground(new Color(217, 217, 217));
        txtTenKH.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        panelForm.add(txtTenKH, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblLoaiKH = new JLabel("Loại KH:");
        lblLoaiKH.setFont(fontLabel);
        panelForm.add(lblLoaiKH, gbc);

        gbc.gridx = 1;
        cbLoaiKH = new JComboBox<>(new String[] { "Thường", "VIP", "Diamond" });
        cbLoaiKH.setFont(fontInput);
        cbLoaiKH.setBackground(new Color(217, 217, 217));
        cbLoaiKH.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        panelForm.add(cbLoaiKH, gbc);

        // Cột 2
        gbc.insets = new Insets(10, 40, 10, 20);
        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel lblDiem = new JLabel("Điểm tích lũy:");
        lblDiem.setFont(fontLabel);
        panelForm.add(lblDiem, gbc);

        gbc.insets = new Insets(10, 10, 10, 20);
        gbc.gridx = 3;
        txtDiemTichLuy = new JTextField(15);
        txtDiemTichLuy.setFont(fontInput);
        txtDiemTichLuy.setBackground(new Color(217, 217, 217));
        txtDiemTichLuy.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        panelForm.add(txtDiemTichLuy, gbc);

        gbc.insets = new Insets(10, 40, 10, 20);
        gbc.gridx = 2;
        gbc.gridy = 1;
        JLabel lblSDT = new JLabel("SĐT:");
        lblSDT.setFont(fontLabel);
        panelForm.add(lblSDT, gbc);

        gbc.insets = new Insets(10, 10, 10, 20);
        gbc.gridx = 3;
        txtSDT = new JTextField(15);
        txtSDT.setFont(fontInput);
        txtSDT.setBackground(new Color(217, 217, 217));
        txtSDT.setBorder(new LineBorder(new Color(150, 150, 150), 1));
        panelForm.add(txtSDT, gbc);

        Dimension inputDim = new Dimension(240, 36);
        txtMaKH.setPreferredSize(inputDim);
        txtTenKH.setPreferredSize(inputDim);
        cbLoaiKH.setPreferredSize(inputDim);
        txtDiemTichLuy.setPreferredSize(inputDim);
        txtSDT.setPreferredSize(inputDim);

        // --- Buttons Section ---
        JPanel panelButtons = new JPanel(new GridBagLayout());
        panelButtons.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(5, 5, 5, 5);
        gbcBtn.fill = GridBagConstraints.BOTH;

        Font fontBtn = new Font("Arial", Font.BOLD, 16);

        btnThem = new JButton("THÊM");
        btnXoa = new JButton("XÓA");
        btnTraCuu = new JButton("TRA CỨU");
        btnLamMoi = new JButton("LÀM MỚI");
        btnCapNhat = new JButton("CẬP NHẬT");

        JButton[] buttons = { btnThem, btnXoa, btnTraCuu, btnLamMoi, btnCapNhat };
        
        // Sử dụng màu nền nhạt (Pastel) và CHỮ ĐEN để tránh lỗi LookAndFeel nuốt màu nền làm mất chữ trắng
        Color[] colors = {
            new Color(190, 230, 190), // Thêm: Xanh lá nhạt
            new Color(250, 190, 190), // Xóa: Đỏ nhạt
            new Color(190, 230, 240), // Tra cứu: Xanh lơ nhạt
            new Color(225, 225, 225), // Làm mới: Xám nhạt
            new Color(190, 210, 250)  // Cập nhật: Xanh dương nhạt
        };

        for (int i = 0; i < buttons.length; i++) {
            JButton btn = buttons[i];
            btn.setFont(fontBtn);
            btn.setBackground(colors[i]);
            btn.setForeground(Color.BLACK); // CHỮ ĐEN cực kì an toàn
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setFocusPainted(false);
            btn.setBorder(new LineBorder(new Color(150, 150, 150), 1));
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(120, 45));
        }

        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        panelButtons.add(btnThem, gbcBtn);
        gbcBtn.gridx = 1;
        panelButtons.add(btnXoa, gbcBtn);

        gbcBtn.gridx = 0;
        gbcBtn.gridy = 1;
        panelButtons.add(btnTraCuu, gbcBtn);
        gbcBtn.gridx = 1;
        panelButtons.add(btnLamMoi, gbcBtn);

        gbcBtn.gridx = 0;
        gbcBtn.gridy = 2;
        gbcBtn.gridwidth = 2;
        JPanel centerCapNhat = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerCapNhat.setOpaque(false);
        centerCapNhat.add(btnCapNhat);
        panelButtons.add(centerCapNhat, gbcBtn);

        JPanel panelTopArea = new JPanel(new BorderLayout());
        panelTopArea.setOpaque(false);
        panelTopArea.add(panelForm, BorderLayout.WEST);

        JPanel pnlEastButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10)); // Margin from form
        pnlEastButtons.setOpaque(false);
        pnlEastButtons.add(panelButtons);
        panelTopArea.add(pnlEastButtons, BorderLayout.CENTER);

        panelContent.add(panelTopArea, BorderLayout.NORTH);

        // --- Table Section ---
        String[] columns = { "Mã khách hàng", "Họ Tên", "Điểm tích lũy", "Nhân viên", "SĐT", "Tổng chi tiêu",
                "Loại khách hàng" };
        modelKhachHang = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Khóa bảng không cho nhập chữ trực tiếp vào ô
            }
        };
        tblKhachHang = new JTable(modelKhachHang);
        tblKhachHang.setSelectionBackground(new Color(238, 225, 205)); // Đổi màu xanh mặc định thành màu be hạt dẻ nhạt
        tblKhachHang.setSelectionForeground(Color.BLACK); // Giữ text màu đen khi chọn
        tblKhachHang.setRowHeight(40);
        tblKhachHang.setFont(new Font("Arial", Font.PLAIN, 16));
        tblKhachHang.setShowGrid(true);
        tblKhachHang.setGridColor(Color.GRAY);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblKhachHang.getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = tblKhachHang.getTableHeader();
        header.setPreferredSize(new Dimension(100, 40));
        
        // Tạo Renderer riêng cho Header để không bị lỗi đè màu của giao diện hệ thống
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(208, 144, 106)); // Orange brown as per Figma
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Arial", Font.BOLD, 16));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        for (int i = 0; i < tblKhachHang.getColumnModel().getColumnCount(); i++) {
            tblKhachHang.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblKhachHang);
        scrollPane.setBorder(new LineBorder(Color.GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        modelKhachHang.addRow(
                new Object[] { "KH0001", "Lê Thu Minh", "100", "Ngọc Tiên", "0123456789", "7.000.000VND", "VIP" });

        JPanel panelTableWrapper = new JPanel(new BorderLayout());
        panelTableWrapper.setOpaque(false);
        panelTableWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        panelTableWrapper.add(scrollPane, BorderLayout.CENTER);

        panelContent.add(panelTableWrapper, BorderLayout.CENTER);

        panelCenter.add(panelContent, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // Xử lý sự kiện chọn dòng trong bảng
        tblKhachHang.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = tblKhachHang.getSelectedRow();
                    if (row != -1) {
                        txtMaKH.setText(modelKhachHang.getValueAt(row, 0) != null ? modelKhachHang.getValueAt(row, 0).toString() : "");
                        txtTenKH.setText(modelKhachHang.getValueAt(row, 1) != null ? modelKhachHang.getValueAt(row, 1).toString() : "");
                        txtDiemTichLuy.setText(modelKhachHang.getValueAt(row, 2) != null ? modelKhachHang.getValueAt(row, 2).toString() : "");
                        txtSDT.setText(modelKhachHang.getValueAt(row, 4) != null ? modelKhachHang.getValueAt(row, 4).toString() : "");
                        Object loai = modelKhachHang.getValueAt(row, 6);
                        if (loai != null) cbLoaiKH.setSelectedItem(loai.toString());
                    }
                }
            }
        });

        // Xử lý sự kiện nút Thêm
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String maKH = txtMaKH.getText().trim();
                String tenKH = txtTenKH.getText().trim();
                String loaiKH = cbLoaiKH.getSelectedItem().toString();
                String diem = txtDiemTichLuy.getText().trim();
                String sdt = txtSDT.getText().trim();

                if (maKH.isEmpty() || tenKH.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(KhachHang_GUI.this, "Vui lòng nhập mã và tên khách hàng!");
                    return;
                }

                // Cập nhật dữ liệu vào Table model
                modelKhachHang.addRow(new Object[] { maKH, tenKH, diem.isEmpty() ? "0" : diem, "Ngọc Tiên", sdt, "0VND", loaiKH });

                // Reset các trường nhập liệu
                txtMaKH.setText("");
                txtTenKH.setText("");
                txtDiemTichLuy.setText("");
                txtSDT.setText("");
                cbLoaiKH.setSelectedIndex(0);
                txtMaKH.requestFocus();
            }
        });

        // Xử lý sự kiện nút Xóa
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = tblKhachHang.getSelectedRow();
                if (row == -1) {
                    javax.swing.JOptionPane.showMessageDialog(KhachHang_GUI.this, "Vui lòng chọn một khách hàng trong bảng để xóa!");
                    return;
                }
                
                int confirm = javax.swing.JOptionPane.showConfirmDialog(KhachHang_GUI.this,
                        "Bạn có chắc chắn muốn xóa khách hàng này không?", "Xác nhận xóa",
                        javax.swing.JOptionPane.YES_NO_OPTION);
                        
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    modelKhachHang.removeRow(row);
                }
            }
        });

        // Xử lý sự kiện nút Cập nhật
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = tblKhachHang.getSelectedRow();
                if (row == -1) {
                    javax.swing.JOptionPane.showMessageDialog(KhachHang_GUI.this, "Vui lòng chọn một khách hàng từ bảng để cập nhật!");
                    return;
                }

                String maKH = txtMaKH.getText().trim();
                String tenKH = txtTenKH.getText().trim();
                String loaiKH = cbLoaiKH.getSelectedItem().toString();
                String diem = txtDiemTichLuy.getText().trim();
                String sdt = txtSDT.getText().trim();

                if (tenKH.isEmpty() || maKH.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(KhachHang_GUI.this, "Mã và Tên khách hàng không được để trống!");
                    return;
                }

                // Cập nhật lại dữ liệu trực tiếp vào table (modelKhachHang) tại vị trí cũ
                modelKhachHang.setValueAt(maKH, row, 0);
                modelKhachHang.setValueAt(tenKH, row, 1);
                modelKhachHang.setValueAt(diem.isEmpty() ? "0" : diem, row, 2);
                modelKhachHang.setValueAt(sdt, row, 4);
                modelKhachHang.setValueAt(loaiKH, row, 6);
                
                javax.swing.JOptionPane.showMessageDialog(KhachHang_GUI.this, "Cập nhật thành công!");
                
                // Bỏ chọn row và làm mới form
                tblKhachHang.clearSelection();
                txtMaKH.setText("");
                txtTenKH.setText("");
                txtDiemTichLuy.setText("");
                txtSDT.setText("");
                cbLoaiKH.setSelectedIndex(0);
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
}
