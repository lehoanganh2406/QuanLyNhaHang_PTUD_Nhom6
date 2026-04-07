package gui;

import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import entity.Ban;
import digLog.PhieuDatBan_DigLog;

public class DatBan_GUI extends JFrame {

    public DatBan_GUI() {
        setTitle("Đặt bàn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Pn_ThanhMenu menu = new Pn_ThanhMenu();
        menu.setPreferredSize(new Dimension(0, 42));
        add(menu, BorderLayout.NORTH);

        add(new DatBanMainPanel(), BorderLayout.CENTER);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConnectDB.getInstance().connect();
                new DatBan_GUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    class DatBanMainPanel extends JPanel {

        private final CardLayout cardLayout = new CardLayout();
        private final JPanel cardPanel = new JPanel(cardLayout);

        private final JButton btnToday = new JButton("Hôm nay");
        private final JButton btnPrev = new JButton("◀");
        private final JButton btnNext = new JButton("▶");

        private final JButton btnDayView = new JButton("Ngày");
        private final JButton btnWeekView = new JButton("Tuần");
        private final JButton btnScheduleView = new JButton("Theo lịch đặt");

        private final JDateChooser dateChooser = new JDateChooser();

        private final Calendar currentCalendar = Calendar.getInstance();
        private ViewMode currentMode = ViewMode.DAY;

        private java.util.List<String> tableNames = new ArrayList<>();
        private Map<String, String> tableNameMap = new HashMap<>();

        private JTextField txtSearchMaPhieu;
        private JTextField txtSearchSdt;

        private final java.util.List<JCheckBox> statusCheckBoxes = new ArrayList<>();
        private final java.util.List<String> statusValues = new ArrayList<>();

        class BookingDisplayItem {
            String maPhieu;
            String maBan;
            String tenKhach;
            String sdt;
            String trangThai;
            Timestamp thoiGianDen;
            int soLuongNguoi;
            String ghiChu;

            public BookingDisplayItem(String maPhieu, String maBan, String tenKhach,
                                      String sdt, String trangThai,
                                      Timestamp thoiGianDen, int soLuongNguoi, String ghiChu) {
                this.maPhieu = maPhieu;
                this.maBan = maBan;
                this.tenKhach = tenKhach;
                this.sdt = sdt;
                this.trangThai = trangThai;
                this.thoiGianDen = thoiGianDen;
                this.soLuongNguoi = soLuongNguoi;
                this.ghiChu = ghiChu;
            }
        }

        public DatBanMainPanel() {
            setLayout(new BorderLayout());

            JPanel leftPanel = createLeftFilterPanel();
            JPanel centerPanel = createCenterPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
            splitPane.setDividerLocation(255);
            splitPane.setDividerSize(2);
            splitPane.setBorder(null);
            splitPane.setResizeWeight(0);

            add(splitPane, BorderLayout.CENTER);

            loadTableNamesFromDB();
            initDateChooser();
            initEvents();
            refreshView();
        }

        private void moChiTietPhieu(BookingDisplayItem item) {
            try {
                PhieuDatBan_DigLog dialog = new PhieuDatBan_DigLog(
                        DatBan_GUI.this,
                        item.maPhieu
                );
                dialog.setLocationRelativeTo(DatBan_GUI.this);
                dialog.setVisible(true);
                refreshView();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        DatBan_GUI.this,
                        "Không mở được chi tiết phiếu đặt bàn!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        private void loadTableNamesFromDB() {
            tableNames.clear();
            tableNameMap.clear();

            try {
                Ban_DAO banDAO = new Ban_DAO();
                ArrayList<Ban> dsBan = banDAO.getAllBan();

                for (Ban ban : dsBan) {
                    if (ban.getTenBan() != null && !ban.getTenBan().trim().isEmpty()) {
                        String tenBan = ban.getTenBan().trim();
                        tableNames.add(tenBan);
                        tableNameMap.put(ban.getMaBan(), tenBan);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Không lấy được danh sách bàn từ cơ sở dữ liệu!",
                        "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        private void initDateChooser() {
            dateChooser.setDate(currentCalendar.getTime());
            dateChooser.setDateFormatString("dd/MM/yyyy");
            dateChooser.setLocale(new Locale("vi", "VN"));
            dateChooser.setPreferredSize(new Dimension(150, 30));
            dateChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JComponent editorComp = dateChooser.getDateEditor().getUiComponent();
            if (editorComp instanceof JTextField) {
                JTextField editor = (JTextField) editorComp;
                editor.setEditable(false);
                editor.setHorizontalAlignment(SwingConstants.LEFT);
                editor.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                editor.setBackground(Color.WHITE);
                editor.setForeground(Color.BLACK);
                editor.setFont(new Font("SansSerif", Font.PLAIN, 14));
            }
        }

        private java.util.List<BookingDisplayItem> getBookingsByDate(Date ngay) {
            java.util.List<BookingDisplayItem> result = new ArrayList<>();

            try {
                PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
                ArrayList<String[]> ds = dao.getPhieuDatBanTheoNgay(new java.sql.Date(ngay.getTime()));

                for (String[] row : ds) {
                    Timestamp tg = Timestamp.valueOf(row[5]);
                    result.add(new BookingDisplayItem(
                            row[0],
                            row[1],
                            row[2],
                            row[3],
                            row[8],
                            tg,
                            Integer.parseInt(row[4]),
                            row[7]
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        private BookingDisplayItem findBookingAt(String tenBan, Date ngay, int hour) {
            java.util.List<BookingDisplayItem> ds = getBookingsByDate(ngay);

            for (BookingDisplayItem item : ds) {
                String tenBanDB = tableNameMap.getOrDefault(item.maBan, item.maBan);

                Calendar cal = Calendar.getInstance();
                cal.setTime(item.thoiGianDen);

                int bookingHour = cal.get(Calendar.HOUR_OF_DAY);

                if (tenBan.equalsIgnoreCase(tenBanDB)) {
                    if (bookingHour == hour && isAcceptedByFilter(item)) {
                        return item;
                    }
                }
            }
            return null;
        }

        private boolean isAcceptedByFilter(BookingDisplayItem item) {
            String maPhieuFilter = getRealText(txtSearchMaPhieu, "Nhập mã phiếu đặt bàn");
            String sdtFilter = getRealText(txtSearchSdt, "Nhập số điện thoại");

            if (!maPhieuFilter.isEmpty()) {
                if (item.maPhieu == null || !item.maPhieu.toLowerCase().contains(maPhieuFilter.toLowerCase())) {
                    return false;
                }
            }

            if (!sdtFilter.isEmpty()) {
                if (item.sdt == null || !item.sdt.contains(sdtFilter)) {
                    return false;
                }
            }

            java.util.List<String> selectedStatuses = getSelectedStatuses();
            if (!selectedStatuses.isEmpty()) {
                if (item.trangThai == null || !selectedStatuses.contains(item.trangThai.trim())) {
                    return false;
                }
            }

            return true;
        }

        private java.util.List<String> getSelectedStatuses() {
            java.util.List<String> selected = new ArrayList<>();
            for (int i = 0; i < statusCheckBoxes.size(); i++) {
                if (statusCheckBoxes.get(i).isSelected()) {
                    selected.add(statusValues.get(i));
                }
            }
            return selected;
        }

        private String getRealText(JTextField txt, String placeholder) {
            if (txt == null) return "";
            String value = txt.getText().trim();
            if (value.equalsIgnoreCase(placeholder)) return "";
            return value;
        }

        private Color getStatusColor(String trangThai) {
            if (trangThai == null) return new Color(220, 170, 76);

            String s = trangThai.trim().toLowerCase();

            if (s.equals("hoàn thành")) return new Color(124, 183, 103);
            if (s.equals("đã xếp bàn")) return new Color(46, 134, 222);
            if (s.equals("đang chờ")) return new Color(227, 177, 30);
            if (s.equals("quá giờ")) return new Color(95, 95, 95);
            if (s.equals("đã hủy")) return new Color(219, 47, 47);

            return new Color(220, 170, 76);
        }

        private JPanel createLeftFilterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(236, 236, 236));
            panel.setBorder(new EmptyBorder(14, 14, 14, 14));
            panel.setPreferredSize(new Dimension(255, 0));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;

            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 26, 0);
            panel.add(createSearchBlock("Tìm kiếm phiếu đặt bàn", "Nhập mã phiếu đặt bàn", true), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 26, 0);
            panel.add(createSearchBlock("Tìm kiếm theo khách hàng", "Nhập số điện thoại", false), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 0, 0);
            panel.add(createStatusBlock(), gbc);

            gbc.gridy = 3;
            gbc.weighty = 1;
            panel.add(Box.createVerticalGlue(), gbc);

            return panel;
        }

        private JPanel createSearchBlock(String title, String hint, boolean maPhieuField) {
            JPanel wrap = new JPanel(new BorderLayout(0, 10));
            wrap.setOpaque(false);

            JPanel top = new JPanel();
            top.setOpaque(false);
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

            JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            titleRow.setOpaque(false);
            titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblIcon = new JLabel();
            lblIcon.setIcon(loadIcon("img/mn_tracuu.png", 20, 20));
            lblIcon.setPreferredSize(new Dimension(22, 22));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblTitle.setForeground(Color.BLACK);
            lblTitle.setBorder(new EmptyBorder(0, 3, 0, 0));

            titleRow.add(lblIcon);
            titleRow.add(lblTitle);

            JSeparator line = new JSeparator();
            line.setForeground(Color.BLACK);
            line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            line.setAlignmentX(Component.LEFT_ALIGNMENT);

            top.add(titleRow);
            top.add(Box.createVerticalStrut(6));
            top.add(line);

            JTextField txt = new JTextField(hint);
            txt.setFont(new Font("SansSerif", Font.PLAIN, 15));
            txt.setForeground(new Color(145, 145, 145));
            txt.setPreferredSize(new Dimension(0, 36));
            txt.setMinimumSize(new Dimension(120, 36));
            txt.setBorder(new LineBorder(new Color(120, 120, 120), 1));
            txt.setBackground(new Color(246, 246, 246));
            txt.setMargin(new Insets(0, 10, 0, 10));

            addPlaceholderBehavior(txt, hint);

            txt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    refreshView();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    refreshView();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    refreshView();
                }
            });

            if (maPhieuField) {
                txtSearchMaPhieu = txt;
            } else {
                txtSearchSdt = txt;
            }

            wrap.add(top, BorderLayout.NORTH);
            wrap.add(txt, BorderLayout.CENTER);

            return wrap;
        }

        private JPanel createStatusBlock() {
            JPanel wrap = new JPanel(new BorderLayout(0, 10));
            wrap.setOpaque(false);

            JPanel top = new JPanel();
            top.setOpaque(false);
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

            JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            titleRow.setOpaque(false);
            titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblIcon = new JLabel();
            lblIcon.setIcon(loadIcon("img/mn_tracuu.png", 20, 20));
            lblIcon.setPreferredSize(new Dimension(22, 22));

            JLabel lblTitle = new JLabel("Tìm kiếm theo trạng thái");
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
            lblTitle.setForeground(Color.BLACK);
            lblTitle.setBorder(new EmptyBorder(0, 3, 0, 0));

            titleRow.add(lblIcon);
            titleRow.add(lblTitle);

            JSeparator line = new JSeparator();
            line.setForeground(Color.BLACK);
            line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            line.setAlignmentX(Component.LEFT_ALIGNMENT);

            top.add(titleRow);
            top.add(Box.createVerticalStrut(6));
            top.add(line);

            JPanel statusList = new JPanel();
            statusList.setOpaque(false);
            statusList.setLayout(new BoxLayout(statusList, BoxLayout.Y_AXIS));

            addStatusItem(statusList, "Hoàn thành", new Color(124, 183, 103));
            addStatusItem(statusList, "Đã xếp bàn", new Color(46, 134, 222));
            addStatusItem(statusList, "Đang chờ", new Color(227, 177, 30));
            addStatusItem(statusList, "Quá giờ", new Color(95, 95, 95));
            addStatusItem(statusList, "Đã hủy", new Color(219, 47, 47));

            wrap.add(top, BorderLayout.NORTH);
            wrap.add(statusList, BorderLayout.CENTER);

            return wrap;
        }

        private void addStatusItem(JPanel container, String text, Color color) {
            JPanel row = new JPanel();
            row.setOpaque(false);
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setPreferredSize(new Dimension(220, 32));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            row.setMinimumSize(new Dimension(220, 32));
            row.setBorder(new EmptyBorder(2, 4, 2, 0));

            JCheckBox chk = new JCheckBox();
            chk.setOpaque(false);
            chk.setFocusPainted(false);
            chk.setMargin(new Insets(0, 0, 0, 0));
            chk.setPreferredSize(new Dimension(26, 22));
            chk.setMaximumSize(new Dimension(26, 22));
            chk.setMinimumSize(new Dimension(26, 22));
            chk.setAlignmentY(Component.CENTER_ALIGNMENT);
            chk.addActionListener(e -> refreshView());

            JLabel lbl = new JLabel(text);
            lbl.setForeground(color);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
            lbl.setAlignmentY(Component.CENTER_ALIGNMENT);

            row.add(chk);
            row.add(Box.createRigidArea(new Dimension(12, 0)));
            row.add(lbl);
            row.add(Box.createHorizontalGlue());

            statusCheckBoxes.add(chk);
            statusValues.add(text);

            container.add(row);
        }

        private void addPlaceholderBehavior(JTextField textField, String placeholder) {
            textField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (textField.getText().equals(placeholder)) {
                        textField.setText("");
                        textField.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (textField.getText().trim().isEmpty()) {
                        textField.setText(placeholder);
                        textField.setForeground(new Color(145, 145, 145));
                    }
                }
            });
        }

        private JPanel createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);

            JLabel lblTitle = new JLabel("Đặt bàn");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));

            JButton btnAdd = new JButton("+ Thêm mới phiếu đặt bàn");
            btnAdd.setBackground(new Color(76, 175, 80));
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setFocusPainted(false);
            btnAdd.setOpaque(true);
            btnAdd.setContentAreaFilled(true);
            btnAdd.setBorderPainted(false);
            btnAdd.setPreferredSize(new Dimension(350, 38));
            btnAdd.setFont(new Font("SansSerif", Font.BOLD, 19));

            btnAdd.addActionListener(e -> {
                try {
                    PhieuDatBan_DigLog dialog = new PhieuDatBan_DigLog(DatBan_GUI.this);
                    dialog.setLocationRelativeTo(DatBan_GUI.this);
                    dialog.setVisible(true);
                    refreshView();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            DatBan_GUI.this,
                            "Không mở được phiếu đặt bàn!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            headerPanel.add(lblTitle, BorderLayout.WEST);
            headerPanel.add(btnAdd, BorderLayout.EAST);

            JPanel northWrap = new JPanel();
            northWrap.setOpaque(false);
            northWrap.setLayout(new BoxLayout(northWrap, BoxLayout.Y_AXIS));
            northWrap.add(headerPanel);
            northWrap.add(Box.createVerticalStrut(10));
            northWrap.add(createControlPanel());

            panel.add(northWrap, BorderLayout.NORTH);
            panel.add(cardPanel, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createControlPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            left.setOpaque(false);

            styleTopButton(btnToday, true);
            styleArrowButton(btnPrev);
            styleArrowButton(btnNext);
            styleDateChooser(dateChooser);

            left.add(btnToday);
            left.add(btnPrev);
            left.add(btnNext);
            left.add(dateChooser);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            right.setOpaque(false);

            right.add(btnDayView);
            right.add(btnWeekView);
            right.add(btnScheduleView);

            panel.add(left, BorderLayout.WEST);
            panel.add(right, BorderLayout.EAST);

            return panel;
        }

        private void styleTopButton(JButton btn, boolean blue) {
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setBackground(blue ? new Color(191, 219, 254) : new Color(240, 240, 240));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
            btn.setMargin(new Insets(8, 12, 8, 12));
        }

        private void styleArrowButton(JButton btn) {
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setMargin(new Insets(6, 4, 6, 4));
        }

        private void styleDateChooser(JDateChooser chooser) {
            chooser.setPreferredSize(new Dimension(150, 30));
            chooser.setBorder(new LineBorder(new Color(180, 180, 180), 1));
        }

        private void configScroll(JScrollPane scrollPane) {
            scrollPane.setBorder(new LineBorder(new Color(210, 210, 210)));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        }

        private void initEvents() {
            btnToday.addActionListener(e -> {
                currentCalendar.setTime(new Date());
                dateChooser.setDate(currentCalendar.getTime());
                refreshView();
            });

            btnPrev.addActionListener(e -> {
                if (currentMode == ViewMode.DAY) {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, -7);
                }
                dateChooser.setDate(currentCalendar.getTime());
                refreshView();
            });

            btnNext.addActionListener(e -> {
                if (currentMode == ViewMode.DAY) {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
                } else {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 7);
                }
                dateChooser.setDate(currentCalendar.getTime());
                refreshView();
            });

            dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
                Date selectedDate = dateChooser.getDate();
                if (selectedDate != null) {
                    currentCalendar.setTime(selectedDate);
                    refreshView();
                }
            });

            btnDayView.addActionListener(e -> {
                currentMode = ViewMode.DAY;
                refreshView();
            });

            btnWeekView.addActionListener(e -> {
                currentMode = ViewMode.WEEK;
                refreshView();
            });

            btnScheduleView.addActionListener(e -> {
                currentMode = ViewMode.SCHEDULE;
                refreshView();
            });
        }

        private void refreshView() {
            updateButtonStyles();
            cardPanel.removeAll();

            if (currentMode == ViewMode.DAY) {
                cardPanel.add(buildDayGrid(), ViewMode.DAY.name());
            } else if (currentMode == ViewMode.WEEK) {
                cardPanel.add(buildWeekGridLikeImage(), ViewMode.WEEK.name());
            } else {
                cardPanel.add(buildScheduleViewLikeImage(), ViewMode.SCHEDULE.name());
            }

            cardLayout.show(cardPanel, currentMode.name());
            revalidate();
            repaint();
        }

        private void updateButtonStyles() {
            styleTopButton(btnDayView, false);
            styleTopButton(btnWeekView, false);
            styleTopButton(btnScheduleView, false);

            if (currentMode == ViewMode.DAY) {
                styleTopButton(btnDayView, true);
            } else if (currentMode == ViewMode.WEEK) {
                styleTopButton(btnWeekView, true);
            } else {
                styleTopButton(btnScheduleView, true);
            }
        }

        private JPanel createFixedTablePanel(JPanel leftPanel, JPanel rightPanel, int fixedWidth) {
            JScrollPane leftScroll = new JScrollPane(leftPanel);
            JScrollPane rightScroll = new JScrollPane(rightPanel);

            configScroll(leftScroll);
            configScroll(rightScroll);

            leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            leftScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            leftScroll.setPreferredSize(new Dimension(fixedWidth + 2, 0));

            rightScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            rightScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            rightScroll.getVerticalScrollBar().addAdjustmentListener(
                    e -> leftScroll.getVerticalScrollBar().setValue(e.getValue())
            );

            MouseWheelListener syncWheel = e -> {
                JScrollBar bar = rightScroll.getVerticalScrollBar();
                int amount = e.getUnitsToScroll() * 16;
                bar.setValue(bar.getValue() + amount);
            };

            leftScroll.addMouseWheelListener(syncWheel);
            leftPanel.addMouseWheelListener(syncWheel);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
            split.setDividerLocation(fixedWidth);
            split.setDividerSize(1);
            split.setEnabled(false);
            split.setBorder(null);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(split, BorderLayout.CENTER);
            return panel;
        }

        private JPanel buildDayGrid() {
            JPanel leftPanel = new JPanel(new GridBagLayout());
            JPanel rightPanel = new JPanel(new GridBagLayout());

            leftPanel.setBackground(Color.WHITE);
            rightPanel.setBackground(Color.WHITE);

            GridBagConstraints gbcL = new GridBagConstraints();
            gbcL.fill = GridBagConstraints.BOTH;
            gbcL.gridx = 0;

            GridBagConstraints gbcR = new GridBagConstraints();
            gbcR.fill = GridBagConstraints.BOTH;

            java.util.List<String> hours = createHoursDay();

            int leftColWidth = 170;
            int hourWidth = 120;
            int rowHeight = 52;

            gbcL.gridy = 0;
            leftPanel.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbcL);

            gbcL.gridy = 1;
            leftPanel.add(createCell("Bàn", leftColWidth, rowHeight, true, SwingConstants.LEFT), gbcL);

            for (int r = 0; r < tableNames.size(); r++) {
                gbcL.gridy = r + 2;
                leftPanel.add(createCell(tableNames.get(r), leftColWidth, rowHeight, false, SwingConstants.LEFT), gbcL);
            }

            gbcR.gridx = 0;
            gbcR.gridy = 0;
            gbcR.gridwidth = hours.size();
            rightPanel.add(
                    createCell(formatDayHeaderFull(currentCalendar), hourWidth * hours.size(), rowHeight, true, SwingConstants.LEFT),
                    gbcR
            );

            for (int i = 0; i < hours.size(); i++) {
                gbcR.gridx = i;
                gbcR.gridy = 1;
                gbcR.gridwidth = 1;
                rightPanel.add(createCell(hours.get(i), hourWidth, rowHeight, true, SwingConstants.CENTER), gbcR);
            }

            for (int r = 0; r < tableNames.size(); r++) {
                for (int c = 0; c < hours.size(); c++) {
                    gbcR.gridx = c;
                    gbcR.gridy = r + 2;
                    gbcR.gridwidth = 1;

                    BookingDisplayItem item = findBookingAt(tableNames.get(r), currentCalendar.getTime(), c);
                    rightPanel.add(createBookingSlotCell(item, hourWidth, rowHeight), gbcR);
                }
            }

            leftPanel.setPreferredSize(new Dimension(leftColWidth, (tableNames.size() + 2) * rowHeight));
            rightPanel.setPreferredSize(new Dimension(hours.size() * hourWidth, (tableNames.size() + 2) * rowHeight));

            return createFixedTablePanel(leftPanel, rightPanel, leftColWidth);
        }

        private JPanel buildWeekGridLikeImage() {
            JPanel leftPanel = new JPanel(new GridBagLayout());
            JPanel rightPanel = new JPanel(new GridBagLayout());

            leftPanel.setBackground(Color.WHITE);
            rightPanel.setBackground(Color.WHITE);

            GridBagConstraints gbcL = new GridBagConstraints();
            gbcL.fill = GridBagConstraints.BOTH;
            gbcL.gridx = 0;

            GridBagConstraints gbcR = new GridBagConstraints();
            gbcR.fill = GridBagConstraints.BOTH;

            java.util.List<String> hours = createHoursWeek2Hours();
            Calendar weekStart = getWeekStart(currentCalendar);

            int leftColWidth = 170;
            int hourWidth = 110;
            int rowHeight = 52;

            gbcL.gridy = 0;
            leftPanel.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbcL);

            gbcL.gridy = 1;
            leftPanel.add(createCell("Bàn", leftColWidth, rowHeight, true, SwingConstants.LEFT), gbcL);

            for (int r = 0; r < tableNames.size(); r++) {
                gbcL.gridy = r + 2;
                leftPanel.add(createCell(tableNames.get(r), leftColWidth, rowHeight, false, SwingConstants.LEFT), gbcL);
            }

            int colIndex = 0;
            for (int d = 0; d < 7; d++) {
                Calendar day = (Calendar) weekStart.clone();
                day.add(Calendar.DAY_OF_MONTH, d);

                gbcR.gridx = colIndex;
                gbcR.gridy = 0;
                gbcR.gridwidth = hours.size();
                rightPanel.add(
                        createCell(formatDayHeader(day), hourWidth * hours.size(), rowHeight, true, SwingConstants.LEFT),
                        gbcR
                );
                colIndex += hours.size();
            }

            colIndex = 0;
            for (int d = 0; d < 7; d++) {
                for (String hour : hours) {
                    gbcR.gridx = colIndex++;
                    gbcR.gridy = 1;
                    gbcR.gridwidth = 1;
                    rightPanel.add(createCell(hour, hourWidth, rowHeight, true, SwingConstants.CENTER), gbcR);
                }
            }

            colIndex = 0;
            for (int d = 0; d < 7; d++) {
                Calendar day = (Calendar) weekStart.clone();
                day.add(Calendar.DAY_OF_MONTH, d);

                for (int h = 0; h < hours.size(); h++) {
                    int startHour = h * 2;

                    for (int r = 0; r < tableNames.size(); r++) {
                        gbcR.gridx = colIndex;
                        gbcR.gridy = r + 2;
                        gbcR.gridwidth = 1;

                        BookingDisplayItem item = findBookingAt(
                                tableNames.get(r),
                                day.getTime(),
                                startHour
                        );
                        rightPanel.add(createBookingSlotCell(item, hourWidth, rowHeight), gbcR);
                    }
                    colIndex++;
                }
            }

            leftPanel.setPreferredSize(new Dimension(leftColWidth, (tableNames.size() + 2) * rowHeight));
            rightPanel.setPreferredSize(new Dimension(7 * hours.size() * hourWidth, (tableNames.size() + 2) * rowHeight));

            return createFixedTablePanel(leftPanel, rightPanel, leftColWidth);
        }

        private JComponent buildScheduleViewLikeImage() {
            JPanel content = new JPanel(new GridBagLayout());
            content.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;

            int colBan = 170;
            int colNgay = 220;
            int colThoiGian = 150;
            int colThongTin = 700;

            int headerH = 42;
            int rowH = 70;

            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 0;
            content.add(createCell("Bàn", colBan, headerH, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 1;
            content.add(createCell("Ngày đặt", colNgay, headerH, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 2;
            content.add(createCell("Thời gian", colThoiGian, headerH, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 3;
            gbc.weightx = 1;
            content.add(createCell("Thông tin", colThongTin, headerH, true, SwingConstants.LEFT), gbc);

            java.util.List<BookingDisplayItem> bookings = getBookingsByDate(currentCalendar.getTime());
            java.util.List<BookingDisplayItem> filtered = new ArrayList<>();

            for (BookingDisplayItem item : bookings) {
                if (isAcceptedByFilter(item)) {
                    filtered.add(item);
                }
            }

            for (int i = 0; i < filtered.size(); i++) {
                BookingDisplayItem item = filtered.get(i);
                int row = i + 1;

                String tenBan = tableNameMap.getOrDefault(item.maBan, item.maBan);

                gbc.gridy = row;
                gbc.gridx = 0;
                gbc.weightx = 0;
                content.add(createCell(tenBan, colBan, rowH, false, SwingConstants.LEFT), gbc);

                gbc.gridx = 1;
                content.add(createCell(formatDateOnly(item.thoiGianDen), colNgay, rowH, false, SwingConstants.CENTER), gbc);

                gbc.gridx = 2;
                content.add(createCell(formatTimeOnly(item.thoiGianDen), colThoiGian, rowH, false, SwingConstants.CENTER), gbc);

                gbc.gridx = 3;
                gbc.weightx = 1;
                content.add(createBookingInfoCell(item, colThongTin, rowH), gbc);
            }

            if (filtered.isEmpty()) {
                gbc.gridy = 1;
                gbc.gridx = 0;
                gbc.gridwidth = 4;

                JPanel empty = new JPanel(new BorderLayout());
                empty.setBackground(Color.WHITE);
                empty.setBorder(new LineBorder(new Color(200, 200, 200)));
                empty.setPreferredSize(new Dimension(colBan + colNgay + colThoiGian + colThongTin, 80));

                JLabel lbl = new JLabel("Không có dữ liệu đặt bàn", SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
                lbl.setForeground(Color.GRAY);
                empty.add(lbl, BorderLayout.CENTER);

                content.add(empty, gbc);
                gbc.gridwidth = 1;
            }

            int totalWidth = colBan + colNgay + colThoiGian + colThongTin;
            int totalHeight = headerH + Math.max(filtered.size(), 1) * rowH;
            content.setPreferredSize(new Dimension(totalWidth, totalHeight));

            JScrollPane scrollPane = new JScrollPane(content);
            configScroll(scrollPane);
            return scrollPane;
        }

        private JPanel createBookingInfoCell(BookingDisplayItem item, int w, int h) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new LineBorder(new Color(200, 200, 200)));
            wrapper.setPreferredSize(new Dimension(w, h));

            JPanel colorBox = new JPanel(new BorderLayout());
            colorBox.setBackground(getStatusColor(item.trangThai));
            colorBox.setBorder(new EmptyBorder(10, 18, 10, 18));
            colorBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

            String text = item.tenKhach + " - " + item.sdt + " - " + item.trangThai;
            JLabel lbl = new JLabel(text);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
            colorBox.add(lbl, BorderLayout.WEST);

            colorBox.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    moChiTietPhieu(item);
                }
            });

            JPanel margin = new JPanel(new BorderLayout());
            margin.setOpaque(false);
            margin.setBorder(new EmptyBorder(8, 18, 8, 18));
            margin.add(colorBox, BorderLayout.CENTER);

            wrapper.add(margin, BorderLayout.CENTER);
            wrapper.setToolTipText(buildToolTip(item));
            return wrapper;
        }

        private JPanel createBookingSlotCell(BookingDisplayItem item, int w, int h) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new LineBorder(new Color(200, 200, 200)));
            wrapper.setPreferredSize(new Dimension(w, h));

            if (item == null) {
                return wrapper;
            }

            JPanel colorBox = new JPanel();
            colorBox.setLayout(new BoxLayout(colorBox, BoxLayout.Y_AXIS));
            colorBox.setBackground(getStatusColor(item.trangThai));
            colorBox.setBorder(new EmptyBorder(4, 8, 4, 8));
            colorBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblPhone = new JLabel("☏ " + item.sdt);
            lblPhone.setForeground(Color.WHITE);
            lblPhone.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblName = new JLabel("👤 " + item.tenKhach);
            lblName.setForeground(Color.WHITE);
            lblName.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

            colorBox.add(lblPhone);
            colorBox.add(Box.createVerticalStrut(2));
            colorBox.add(lblName);

            colorBox.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    moChiTietPhieu(item);
                }
            });

            wrapper.add(colorBox, BorderLayout.CENTER);
            wrapper.setToolTipText(buildToolTip(item));
            return wrapper;
        }

        private String buildToolTip(BookingDisplayItem item) {
            return "<html>"
                    + "Mã phiếu: " + safe(item.maPhieu)
                    + "<br>Bàn: " + safe(tableNameMap.getOrDefault(item.maBan, item.maBan))
                    + "<br>Khách: " + safe(item.tenKhach)
                    + "<br>SĐT: " + safe(item.sdt)
                    + "<br>Số lượng: " + item.soLuongNguoi
                    + "<br>Thời gian: " + formatDateTime(item.thoiGianDen)
                    + "<br>Trạng thái: " + safe(item.trangThai)
                    + "<br>Ghi chú: " + safe(item.ghiChu)
                    + "</html>";
        }

        private String safe(String value) {
            return value == null || value.trim().isEmpty() ? "" : value;
        }

        private JPanel createCell(String text, int w, int h, boolean header, int align) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(header ? new Color(245, 245, 245) : Color.WHITE);
            cell.setBorder(new LineBorder(new Color(200, 200, 200)));
            cell.setPreferredSize(new Dimension(w, h));

            JLabel lbl = new JLabel(text, align);
            lbl.setFont(new Font("SansSerif", header ? Font.BOLD : Font.PLAIN, 15));
            lbl.setBorder(new EmptyBorder(0, 12, 0, 12));

            cell.add(lbl, BorderLayout.CENTER);
            return cell;
        }

        private ImageIcon loadIcon(String path, int w, int h) {
            try {
                ImageIcon icon = new ImageIcon(path);
                if (icon.getIconWidth() <= 0) return null;
                Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } catch (Exception e) {
                return null;
            }
        }

        private java.util.List<String> createHoursDay() {
            java.util.List<String> hours = new ArrayList<>();
            for (int h = 0; h < 24; h++) {
                hours.add(formatHour12(h));
            }
            return hours;
        }

        private java.util.List<String> createHoursWeek2Hours() {
            java.util.List<String> hours = new ArrayList<>();
            for (int h = 0; h < 24; h += 2) {
                hours.add(formatHour12(h));
            }
            return hours;
        }

        private String formatHour12(int h) {
            if (h == 0) return "0 AM";
            if (h < 12) return h + " AM";
            if (h == 12) return "12 PM";
            return (h - 12) + " PM";
        }

        private Calendar getWeekStart(Calendar source) {
            Calendar start = (Calendar) source.clone();
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);
            int diff;
            if (dayOfWeek == Calendar.SUNDAY) {
                diff = -6;
            } else {
                diff = Calendar.MONDAY - dayOfWeek;
            }
            start.add(Calendar.DAY_OF_MONTH, diff);
            return start;
        }

        private String formatDayHeader(Calendar c) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'Tháng' MM", new Locale("vi", "VN"));
            return sdf.format(c.getTime());
        }

        private String formatDayHeaderFull(Calendar c) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'Tháng' MM yyyy", new Locale("vi", "VN"));
            return sdf.format(c.getTime());
        }

        private String formatDateOnly(Date d) {
            return new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")).format(d);
        }

        private String formatTimeOnly(Date d) {
            return new SimpleDateFormat("HH:mm", new Locale("vi", "VN")).format(d);
        }

        private String formatDateTime(Date d) {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN")).format(d);
        }

        enum ViewMode {
            DAY, WEEK, SCHEDULE
        }
    }
}