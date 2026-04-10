package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import entity.TaiKhoan;

public class KhuVuc_GUI extends JFrame {

    private TaiKhoan taiKhoanDangNhap;
    
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;

    public KhuVuc_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Quản lý khu vực");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        JPanel mainPanel = createMainPanel();

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(menu, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();

                mainPanel.setBounds(0, 42, w, Math.max(0, h - 42));
                menu.setBounds(0, 0, w, h);

                layeredPane.revalidate();
                layeredPane.repaint();
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // --- TOP SEARCH PANEL ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setOpaque(false);
        
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblSearchIcon = new JLabel("🔍"); // Unicode magnifying glass
        lblSearchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblSearchIcon.setForeground(Color.GRAY);
        lblSearchIcon.setBorder(new EmptyBorder(0, 0, 0, 5));
        
        txtSearch = new JTextField("Tìm kiếm khu vực", 30);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setBorder(null);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm khu vực")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm khu vực");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });
        
        searchBox.add(lblSearchIcon, BorderLayout.WEST);
        searchBox.add(txtSearch, BorderLayout.CENTER);
        
        searchPanel.add(searchBox);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // --- CENTER PANEL ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top of Center: "+ Thêm khu vực" Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        
        JButton btnAdd = new JButton("+ Thêm khu vực");
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAdd.setBackground(new Color(230, 244, 234));
        btnAdd.setForeground(new Color(46, 125, 50));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionPanel.add(btnAdd);
        
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Mã", "Tên khu vực", "Số bàn", "Sức chứa", "Kí hiệu khu vực", "Thao tác"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column editable
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(44);
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(new Color(245, 245, 245));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.DARK_GRAY);
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 15, 0, 0)); // Padding for text
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionEditor(new JCheckBox()));
        
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Form Paging
        JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pagingPanel.setOpaque(false);
        pagingPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnPrev = new JButton("‹");
        JButton btnPage = new JButton("1");
        JButton btnNext = new JButton("›");
        
        stylePagingButton(btnPrev);
        stylePagingButton(btnPage);
        stylePagingButton(btnNext);
        btnPage.setBackground(new Color(245, 235, 220));
        
        pagingPanel.add(btnPrev);
        pagingPanel.add(btnPage);
        pagingPanel.add(btnNext);
        
        centerPanel.add(pagingPanel, BorderLayout.SOUTH);
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        centerWrapper.add(centerPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        
        loadMockData();
        
        return mainPanel;
    }
    
    private void stylePagingButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        btn.setPreferredSize(new Dimension(32, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void loadMockData() {
        tableModel.addRow(new Object[]{"KV01", "Tầng 1", 12, 60, "A", ""});
        tableModel.addRow(new Object[]{"KV02", "Tầng 2", 8, 40, "B", ""});
        tableModel.addRow(new Object[]{"KV03", "Sân thượng", 4, 24, "C", ""});
    }

    class ActionPanel extends JPanel {
        JButton btnEdit;
        JButton btnDelete;

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 2));
            setOpaque(true);
            setBackground(Color.WHITE);

            btnEdit = createActionButton("Sửa");
            btnDelete = createActionButton("Xóa");

            add(btnEdit);
            add(btnDelete);
        }

        private JButton createActionButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setBackground(new Color(245, 235, 225));
            btn.setForeground(new Color(120, 90, 70));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 215, 195), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }
    }

    class ActionRenderer extends ActionPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        protected ActionPanel actionPanel;

        public ActionEditor(JCheckBox checkBox) {
            super(checkBox);
            actionPanel = new ActionPanel();
            
            actionPanel.btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                if (row >= 0) {
                    JOptionPane.showMessageDialog(KhuVuc_GUI.this, "Chức năng Sửa khu vực: " + tableModel.getValueAt(row, 1));
                }
            });
            
            actionPanel.btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(KhuVuc_GUI.this, 
                        "Bạn có chắc chắn muốn xóa khu vực này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        tableModel.removeRow(row);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            actionPanel.setBackground(table.getSelectionBackground());
            return actionPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
    }
}