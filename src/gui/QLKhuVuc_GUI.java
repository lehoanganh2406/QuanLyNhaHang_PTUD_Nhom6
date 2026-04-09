package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class QLKhuVuc_GUI extends JPanel {

    // ──────────────── Màu sắc ────────────────
    private static final Color CLR_BG          = new Color(245, 245, 245);
    private static final Color CLR_WHITE        = Color.WHITE;
    private static final Color CLR_BORDER       = new Color(218, 218, 218);
    private static final Color CLR_BTN_ADD_BG   = new Color(232, 245, 233);   // xanh lá nhạt
    private static final Color CLR_BTN_ADD_FG   = new Color(46, 125, 50);     // xanh lá đậm
    private static final Color CLR_BTN_EDIT_BG  = new Color(243, 235, 216); // beige
    private static final Color CLR_BTN_EDIT_FG  = new Color(60, 60, 60);
    private static final Color CLR_BTN_DEL_BG   = new Color(243, 235, 216); // beige
    private static final Color CLR_BTN_DEL_FG   = new Color(60, 60, 60);    // chữ đen thay vì đỏ do ảnh chỉ có viền / nền cùng màu
    private static final Color CLR_TBL_HDR_BG   = Color.WHITE;
    private static final Color CLR_TBL_HDR_FG   = new Color(60, 60, 60);
    private static final Color CLR_TBL_ROW_ODD  = Color.WHITE;
    private static final Color CLR_TBL_ROW_EVEN = new Color(252, 252, 252);
    private static final Color CLR_TEXT_MAIN    = new Color(60, 60, 60);
    private static final Color CLR_SEARCH_BG    = Color.WHITE;
    private static final Color CLR_PAGE_ACTIVE  = new Color(238, 225, 205);   // beige sáng
    private static final Color CLR_PAGE_INACTIVE= new Color(245, 245, 245);

    // ──────────────── Dữ liệu ────────────────
    /** Mỗi khu vực: [Mã, Tên khu vực, Số bàn, Sức chứa, Kí hiệu] */
    private final List<Object[]> allData = new ArrayList<>();
    private final List<Object[]> filteredData = new ArrayList<>();

    private static final int PAGE_SIZE = 7;
    private int currentPage = 1;

    // ──────────────── Components ────────────────
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JLabel lblPageNum;
    private JButton btnPrev, btnNext;

    // ──────────────── Constructor ────────────────
    public QLKhuVuc_GUI() {
        setLayout(new BorderLayout());
        setBackground(CLR_BG);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        initSampleData();

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCardPanel(), BorderLayout.CENTER);
    }

    // ────────────────────────────────────────────
    //  1. THANH TÌM KIẾM + NÚT THÊM
    // ────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Ô tìm kiếm
        JPanel searchWrap = new JPanel(new BorderLayout(6, 0));
        searchWrap.setBackground(CLR_SEARCH_BG);
        searchWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchWrap.setPreferredSize(new Dimension(220, 36));

        JLabel iconSearch = new JLabel("🔍");
        iconSearch.setFont(new Font("SansSerif Emoji", Font.PLAIN, 13));
        iconSearch.setForeground(new Color(160, 160, 160));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createEmptyBorder());
        txtSearch.setOpaque(false);
        txtSearch.setForeground(new Color(60, 60, 60));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm khu vực");

        // Placeholder text
        txtSearch.setForeground(new Color(160, 160, 160));
        txtSearch.setText("Tìm kiếm khu vực");
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm khu vực")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(CLR_TEXT_MAIN);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(new Color(160, 160, 160));
                    txtSearch.setText("Tìm kiếm khu vực");
                }
            }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { doSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { doSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { doSearch(); }
        });

        searchWrap.add(iconSearch, BorderLayout.WEST);
        searchWrap.add(txtSearch,  BorderLayout.CENTER);

        // Nút "+ Thêm khu vực"
        JButton btnAdd = createRoundButton("+ Thêm khu vực", CLR_BTN_ADD_BG, CLR_BTN_ADD_FG, 8);
        btnAdd.setPreferredSize(new Dimension(150, 36));
        btnAdd.addActionListener(e -> showAddDialog());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(btnAdd);

        bar.add(searchWrap, BorderLayout.WEST);
        bar.add(right,      BorderLayout.EAST);
        return bar;
    }

    // ────────────────────────────────────────────
    //  2. CARD CHỨA BẢNG + PHÂN TRANG
    // ────────────────────────────────────────────
    private JPanel buildCardPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(CLR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        card.add(buildTable(),      BorderLayout.CENTER);
        card.add(buildPagination(), BorderLayout.SOUTH);
        // Gọi sau khi lblPageNum, btnPrev, btnNext đã được khởi tạo
        refreshTable();
        return card;
    }

    // ────────────────────────────────────────────
    //  3. BẢNG DỮ LIỆU
    // ────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"Mã", "Tên khu vực", "Số bàn", "Sức chứa", "Kí hiệu khu vực", "Thao tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(44);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setGridColor(CLR_BORDER);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(248, 248, 248));
        table.setSelectionForeground(CLR_TEXT_MAIN);
        table.setFillsViewportHeight(true);
        table.setBackground(CLR_WHITE);

        // ── Header ──
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(CLR_TBL_HDR_BG);
        header.setForeground(CLR_TBL_HDR_FG);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(CLR_TBL_HDR_BG);
                setForeground(CLR_TBL_HDR_FG);
                setFont(new Font("SansSerif", Font.PLAIN, 13));
                if (column == 0 || column == 1) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 1, 1, CLR_BORDER),
                            BorderFactory.createEmptyBorder(0, 14, 0, 8)));
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 1, column == 5 ? 0 : 1, CLR_BORDER),
                            BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                }
                return this;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // ── Column widths ──
        int[] widths = {70, 220, 90, 90, 150, 140};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // ── Cell renderers ──
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(sel ? t.getSelectionBackground()
                        : (row % 2 == 0 ? CLR_TBL_ROW_ODD : CLR_TBL_ROW_EVEN));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, col == 5 ? 0 : 1, CLR_BORDER),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                return this;
            }
        };

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBackground(sel ? t.getSelectionBackground()
                        : (row % 2 == 0 ? CLR_TBL_ROW_ODD : CLR_TBL_ROW_EVEN));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, CLR_BORDER),
                        BorderFactory.createEmptyBorder(0, 14, 0, 8)));
                return this;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // ── "Thao tác" column: nút Sửa + Xóa ──
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionCellEditor());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    Rectangle cellRect = table.getCellRect(row, col, true);
                    int relX = e.getX() - cellRect.x;
                    if (relX < cellRect.width / 2) {
                        onSua(row);
                    } else {
                        onXoa(row);
                    }
                }
            }
        });

        // refreshTable() sẽ được gọi từ buildCardPanel() sau khi pagination sẵn sàng

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(CLR_WHITE);
        return scroll;
    }

    // ────────────────────────────────────────────
    //  4. PHÂN TRANG
    // ────────────────────────────────────────────
    private JPanel buildPagination() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 10));
        pnl.setBackground(CLR_WHITE);
        pnl.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CLR_BORDER));

        btnPrev = new JButton("‹");
        btnPrev.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnPrev.setPreferredSize(new Dimension(30, 28));
        btnPrev.setBackground(CLR_WHITE);
        btnPrev.setForeground(new Color(120, 120, 120));
        btnPrev.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1, true));
        btnPrev.setFocusPainted(false);
        btnPrev.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; refreshTable(); }});

        lblPageNum = new JLabel("1");
        lblPageNum.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPageNum.setOpaque(true);
        lblPageNum.setBackground(CLR_PAGE_ACTIVE);
        lblPageNum.setForeground(CLR_TEXT_MAIN);
        lblPageNum.setPreferredSize(new Dimension(28, 28));
        lblPageNum.setHorizontalAlignment(SwingConstants.CENTER);
        lblPageNum.setBorder(BorderFactory.createLineBorder(new Color(220, 205, 185), 1, true));

        btnNext = new JButton("›");
        btnNext.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnNext.setPreferredSize(new Dimension(30, 28));
        btnNext.setBackground(CLR_WHITE);
        btnNext.setForeground(new Color(120, 120, 120));
        btnNext.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1, true));
        btnNext.setFocusPainted(false);
        btnNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> {
            int totalPages = Math.max(1, (int) Math.ceil((double) filteredData.size() / PAGE_SIZE));
            if (currentPage < totalPages) { currentPage++; refreshTable(); }
        });

        pnl.add(btnPrev);
        pnl.add(lblPageNum);
        pnl.add(btnNext);
        return pnl;
    }

    // ────────────────────────────────────────────
    //  5. LOGIC XỬ LÝ
    // ────────────────────────────────────────────
    private void initSampleData() {
        allData.add(new Object[]{"KV01", "Tầng 1",      12, 60, "A"});
        allData.add(new Object[]{"KV02", "Tầng 2",       8, 40, "B"});
        allData.add(new Object[]{"KV03", "Sân thượng",   4, 24, "C"});
        filteredData.addAll(allData);
    }

    private void doSearch() {
        String kw = txtSearch.getText().trim().toLowerCase();
        if (kw.equals("tìm kiếm khu vực")) kw = "";
        filteredData.clear();
        for (Object[] row : allData) {
            boolean match = false;
            for (Object cell : row) {
                if (cell != null && cell.toString().toLowerCase().contains(kw)) {
                    match = true;
                    break;
                }
            }
            if (match || kw.isEmpty()) filteredData.add(row);
        }
        currentPage = 1;
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredData.size() / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        int from = (currentPage - 1) * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, filteredData.size());
        for (int i = from; i < to; i++) {
            Object[] d = filteredData.get(i);
            tableModel.addRow(new Object[]{d[0], d[1], d[2], d[3], d[4], "action"});
        }

        lblPageNum.setText(String.valueOf(currentPage));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm khu vực", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CLR_WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField fTen     = createStyledField("Tên khu vực");
        JSpinner   fSoBan   = createStyledSpinner();
        JSpinner   fSucChua = createStyledSpinner();
        JTextField fKiHieu  = createStyledField("Kí hiệu");

        addFormRow(form, gbc, 0, "Tên khu vực:",   fTen);
        addFormRow(form, gbc, 1, "Số bàn:",         fSoBan);
        addFormRow(form, gbc, 2, "Sức chứa:",       fSucChua);
        addFormRow(form, gbc, 3, "Kí hiệu:",        fKiHieu);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(CLR_WHITE);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));

        JButton btnCancel = createRoundButton("Huỷ",   new Color(200,200,200), new Color(60,60,60), 6);
        JButton btnSave   = createRoundButton("Lưu",   CLR_BTN_ADD_BG,         CLR_BTN_ADD_FG, 6);
        btnCancel.setPreferredSize(new Dimension(80, 32));
        btnSave.setPreferredSize(new Dimension(80, 32));

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            String ten = fTen.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập tên khu vực!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String ma = "KV" + String.format("%02d", allData.size() + 1);
            Object[] row = {ma, ten, fSoBan.getValue(), fSucChua.getValue(), fKiHieu.getText().trim()};
            allData.add(row);
            doSearch();
            dlg.dispose();
        });

        btnRow.add(btnCancel);
        btnRow.add(btnSave);

        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void onSua(int viewRow) {
        int dataIdx = (currentPage - 1) * PAGE_SIZE + viewRow;
        if (dataIdx < 0 || dataIdx >= filteredData.size()) return;
        Object[] orig = filteredData.get(dataIdx);

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa khu vực", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CLR_WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField fTen     = createStyledField("");
        fTen.setText(orig[1].toString());
        JSpinner   fSoBan   = createStyledSpinner();
        fSoBan.setValue(orig[2]);
        JSpinner   fSucChua = createStyledSpinner();
        fSucChua.setValue(orig[3]);
        JTextField fKiHieu  = createStyledField("");
        fKiHieu.setText(orig[4].toString());

        addFormRow(form, gbc, 0, "Tên khu vực:", fTen);
        addFormRow(form, gbc, 1, "Số bàn:",      fSoBan);
        addFormRow(form, gbc, 2, "Sức chứa:",    fSucChua);
        addFormRow(form, gbc, 3, "Kí hiệu:",     fKiHieu);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(CLR_WHITE);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));

        JButton btnCancel = createRoundButton("Huỷ",        new Color(200,200,200), new Color(60,60,60), 6);
        JButton btnSave   = createRoundButton("Cập nhật",   CLR_BTN_ADD_BG,         CLR_BTN_ADD_FG, 6);
        btnCancel.setPreferredSize(new Dimension(90, 32));
        btnSave.setPreferredSize(new Dimension(100, 32));

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            orig[1] = fTen.getText().trim();
            orig[2] = fSoBan.getValue();
            orig[3] = fSucChua.getValue();
            orig[4] = fKiHieu.getText().trim();
            doSearch();
            dlg.dispose();
        });

        btnRow.add(btnCancel);
        btnRow.add(btnSave);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void onXoa(int viewRow) {
        int dataIdx = (currentPage - 1) * PAGE_SIZE + viewRow;
        if (dataIdx < 0 || dataIdx >= filteredData.size()) return;
        Object[] item = filteredData.get(dataIdx);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa khu vực \"" + item[1] + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            allData.remove(item);
            doSearch();
        }
    }

    // ────────────────────────────────────────────
    //  6. CELL RENDERER / EDITOR cho cột Thao tác
    // ────────────────────────────────────────────
    private class ActionCellRenderer implements TableCellRenderer {
        private final JPanel pnl  = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton edit = makeActionBtn("Sửa",  CLR_BTN_EDIT_BG, CLR_BTN_EDIT_FG, new Color(220, 210, 195));
        private final JButton del  = makeActionBtn("Xóa",  CLR_BTN_DEL_BG,  CLR_BTN_DEL_FG,  new Color(220, 210, 195));

        ActionCellRenderer() {
            pnl.setOpaque(true);
            pnl.add(edit);
            pnl.add(del);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                       boolean foc, int row, int col) {
            pnl.setBackground(sel ? t.getSelectionBackground()
                    : (row % 2 == 0 ? CLR_TBL_ROW_ODD : CLR_TBL_ROW_EVEN));
            pnl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_BORDER));
            return pnl;
        }
    }

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel   pnl  = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton  edit = makeActionBtn("Sửa", CLR_BTN_EDIT_BG, CLR_BTN_EDIT_FG, new Color(220, 210, 195));
        private final JButton  del  = makeActionBtn("Xóa", CLR_BTN_DEL_BG,  CLR_BTN_DEL_FG,  new Color(220, 210, 195));

        ActionCellEditor() {
            pnl.setOpaque(true);
            pnl.setBackground(CLR_WHITE);
            pnl.add(edit);
            pnl.add(del);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int row, int col) {
            pnl.setBackground(t.getSelectionBackground());
            pnl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_BORDER));
            return pnl;
        }

        @Override public Object getCellEditorValue() { return "action"; }
    }

    private JButton makeActionBtn(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :(getModel().isRollover() ? bg.darker() : bg));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(52, 28));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ────────────────────────────────────────────
    //  7. HELPERS
    // ────────────────────────────────────────────
    private JButton createRoundButton(String text, Color bg, Color fg, int radius) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField createStyledField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        tf.setPreferredSize(new Dimension(220, 32));
        return tf;
    }

    private JSpinner createStyledSpinner() {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        sp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sp.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        sp.setPreferredSize(new Dimension(220, 32));
        return sp;
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(60, 60, 60));
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        p.add(comp, gbc);
    }

    // ────────────────────────────────────────────
    //  8. MAIN – chạy độc lập để demo
    // ────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            JFrame frame = new JFrame("GD_QuanLyKhuVuc");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            frame.setLayout(new BorderLayout());
            frame.add(new QLKhuVuc_GUI(), BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
