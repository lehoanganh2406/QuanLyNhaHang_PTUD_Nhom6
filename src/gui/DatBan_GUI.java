package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser;

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
        SwingUtilities.invokeLater(() -> new DatBan_GUI().setVisible(true));
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

        private final JLabel lblDateRange = new JLabel();

        private final Calendar currentCalendar = Calendar.getInstance();
        private ViewMode currentMode = ViewMode.DAY;

        private final JScrollPane dayScrollPane;
        private final JScrollPane weekScrollPane;
        private final JScrollPane scheduleScrollPane;

        private final JPanel dayGridPanel = new JPanel();
        private final JPanel weekGridPanel = new JPanel();
        private final JPanel scheduleListPanel = new JPanel();

        private final java.util.List<String> tableNames = createTableNames();

        public DatBanMainPanel() {
            setLayout(new BorderLayout());

            JPanel leftPanel = createLeftFilterPanel();
            JPanel centerPanel = createCenterPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
            splitPane.setDividerLocation(215);
            splitPane.setDividerSize(2);
            splitPane.setBorder(null);
            splitPane.setResizeWeight(0);

            add(splitPane, BorderLayout.CENTER);

            dayScrollPane = new JScrollPane(dayGridPanel);
            weekScrollPane = new JScrollPane(weekGridPanel);
            scheduleScrollPane = new JScrollPane(scheduleListPanel);

            configScroll(dayScrollPane);
            configScroll(weekScrollPane);
            configScroll(scheduleScrollPane);

            cardPanel.add(dayScrollPane, ViewMode.DAY.name());
            cardPanel.add(weekScrollPane, ViewMode.WEEK.name());
            cardPanel.add(scheduleScrollPane, ViewMode.SCHEDULE.name());

            initEvents();
            refreshView();
        }

        // ================= LEFT PANEL =================
        private JPanel createLeftFilterPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(231, 231, 231));
            panel.setBorder(new EmptyBorder(12, 8, 12, 8));
            panel.setPreferredSize(new Dimension(215, 0));

            panel.add(createSearchBlock("Tìm kiếm bàn", "Nhập tên bàn"));
            panel.add(Box.createVerticalStrut(52));
            panel.add(createSearchBlock("Tìm kiếm theo khách hàng", "Nhập số điện thoại"));
            panel.add(Box.createVerticalStrut(52));
            panel.add(createStatusBlock());

            return panel;
        }

        private JPanel createSearchBlock(String title, String hint) {
            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.setAlignmentX(LEFT_ALIGNMENT);

            JPanel titlePanel = new JPanel(new BorderLayout(4, 0));
            titlePanel.setOpaque(false);
            titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            titlePanel.setAlignmentX(LEFT_ALIGNMENT);

            JLabel lblIcon = new JLabel();
            lblIcon.setPreferredSize(new Dimension(18, 18));
            lblIcon.setIcon(loadIcon("img/ic_search.png", 18, 18));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTitle.setForeground(Color.BLACK);

            titlePanel.add(lblIcon, BorderLayout.WEST);
            titlePanel.add(lblTitle, BorderLayout.CENTER);

            JSeparator line = new JSeparator();
            line.setForeground(Color.BLACK);
            line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            line.setPreferredSize(new Dimension(190, 2));

            JTextField txt = new JTextField();
            txt.setFont(new Font("Arial", Font.PLAIN, 13));
            txt.setForeground(Color.GRAY);
            txt.setText(hint);
            txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            txt.setPreferredSize(new Dimension(190, 34));
            txt.setBorder(new LineBorder(new Color(80, 80, 80), 1));
            txt.setBackground(new Color(245, 245, 245));
            txt.setMargin(new Insets(0, 10, 0, 10));

            addPlaceholderBehavior(txt, hint);

            wrap.add(titlePanel);
            wrap.add(Box.createVerticalStrut(4));
            wrap.add(line);
            wrap.add(Box.createVerticalStrut(14));
            wrap.add(txt);

            return wrap;
        }

        private JPanel createStatusBlock() {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setAlignmentX(LEFT_ALIGNMENT);

            JPanel titlePanel = new JPanel(new BorderLayout(4, 0));
            titlePanel.setOpaque(false);
            titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

            JLabel lblIcon = new JLabel();
            lblIcon.setPreferredSize(new Dimension(18, 18));
            lblIcon.setIcon(loadIcon("img/ic_search.png", 18, 18));

            JLabel lblTitle = new JLabel("Tìm kiếm theo trạng thái");
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTitle.setForeground(Color.BLACK);

            titlePanel.add(lblIcon, BorderLayout.WEST);
            titlePanel.add(lblTitle, BorderLayout.CENTER);

            JSeparator line = new JSeparator();
            line.setForeground(Color.BLACK);
            line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

            p.add(titlePanel);
            p.add(Box.createVerticalStrut(4));
            p.add(line);
            p.add(Box.createVerticalStrut(12));

            p.add(createStatusItem("Hoàn thành", new Color(124, 183, 103)));
            p.add(createStatusItem("Đã xếp bàn", new Color(46, 134, 222)));
            p.add(createStatusItem("Đang chờ", new Color(227, 177, 30)));
            p.add(createStatusItem("Quá giờ", new Color(95, 95, 95)));
            p.add(createStatusItem("Đã hủy", new Color(219, 47, 47)));

            return p;
        }

        private JPanel createStatusItem(String text, Color color) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

            JCheckBox chk = new JCheckBox();
            chk.setOpaque(false);
            chk.setPreferredSize(new Dimension(18, 18));
            chk.setFocusPainted(false);

            JLabel lbl = new JLabel(text);
            lbl.setForeground(color);
            lbl.setFont(new Font("Arial", Font.PLAIN, 14));

            row.add(Box.createHorizontalStrut(2));
            row.add(chk);
            row.add(lbl);
            return row;
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
                        textField.setForeground(Color.GRAY);
                    }
                }
            });
        }

        // ================= CENTER PANEL =================
        private JPanel createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);

            JLabel lblTitle = new JLabel("Đặt bàn");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 28));

            JButton btnAdd = new JButton("+ Thêm mới phiếu đặt bàn");
            btnAdd.setBackground(new Color(186, 230, 170));
            btnAdd.setFocusPainted(false);
            btnAdd.setPreferredSize(new Dimension(235, 38));

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

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            left.setOpaque(false);

            styleTopButton(btnToday, true);
            styleArrowButton(btnPrev);
            styleArrowButton(btnNext);

            lblDateRange.setFont(new Font("Arial", Font.PLAIN, 16));
            lblDateRange.setForeground(Color.BLACK);
            lblDateRange.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblDateRange.setBorder(new EmptyBorder(0, 2, 0, 2));

            left.add(btnToday);
            left.add(btnPrev);
            left.add(btnNext);
            left.add(lblDateRange);

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
            btn.setFont(new Font("Arial", Font.PLAIN, 15));
            btn.setMargin(new Insets(8, 12, 8, 12));
        }

        private void styleArrowButton(JButton btn) {
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.setMargin(new Insets(6, 4, 6, 4));
        }

        private void configScroll(JScrollPane scrollPane) {
            scrollPane.setBorder(new LineBorder(new Color(210, 210, 210)));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        }

        // ================= EVENTS =================
        private void initEvents() {
            btnToday.addActionListener(e -> {
                currentCalendar.setTime(new Date());
                refreshView();
            });

            btnPrev.addActionListener(e -> {
                if (currentMode == ViewMode.DAY) {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, -7);
                }
                refreshView();
            });

            btnNext.addActionListener(e -> {
                if (currentMode == ViewMode.DAY) {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
                } else {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 7);
                }
                refreshView();
            });

            lblDateRange.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblDateRange.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    lblDateRange.setForeground(new Color(0, 102, 204));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lblDateRange.setForeground(Color.BLACK);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    openDateChooserDialog();
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

        private void openDateChooserDialog() {
            final JDateChooser chooser = new JDateChooser();
            chooser.setDate(currentCalendar.getTime());
            chooser.setDateFormatString("dd/MM/yyyy");
            chooser.setLocale(new Locale("vi", "VN"));
            chooser.setPreferredSize(new Dimension(170, 30));

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            panel.add(chooser);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Chọn ngày",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION && chooser.getDate() != null) {
                currentCalendar.setTime(chooser.getDate());
                refreshView();
            }
        }

        // ================= REFRESH =================
        private void refreshView() {
            updateButtonStyles();
            updateDateLabel();

            if (currentMode == ViewMode.DAY) {
                buildDayGrid();
            } else if (currentMode == ViewMode.WEEK) {
                buildWeekGridLikeImage();
            } else {
                buildScheduleViewLikeImage();
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

        private void updateDateLabel() {
            if (currentMode == ViewMode.DAY) {
                SimpleDateFormat sdfDay = new SimpleDateFormat("dd 'Tháng' MM yyyy", new Locale("vi", "VN"));
                lblDateRange.setText(sdfDay.format(currentCalendar.getTime()));
            } else {
                Calendar start = getWeekStart(currentCalendar);
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.DAY_OF_MONTH, 6);

                SimpleDateFormat sdf = new SimpleDateFormat("dd 'Tháng' MM yyyy", new Locale("vi", "VN"));
                lblDateRange.setText(sdf.format(start.getTime()) + " - " + sdf.format(end.getTime()));
            }
        }

        // ================= DAY VIEW =================
        private void buildDayGrid() {
            dayGridPanel.removeAll();
            dayGridPanel.setLayout(new BorderLayout());
            dayGridPanel.setBackground(Color.WHITE);

            JPanel content = new JPanel(new GridBagLayout());
            content.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;

            java.util.List<String> hours = createHoursDay();

            int leftColWidth = 90;
            int hourWidth = 120;
            int rowHeight = 38;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            content.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = hours.size();
            content.add(createCell(formatDayHeaderFull(currentCalendar), hourWidth * hours.size(), rowHeight, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            content.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbc);

            for (int i = 0; i < hours.size(); i++) {
                gbc.gridx = i + 1;
                gbc.gridy = 1;
                gbc.gridwidth = 1;
                content.add(createCell(hours.get(i), hourWidth, rowHeight, true, SwingConstants.CENTER), gbc);
            }

            for (int r = 0; r < tableNames.size(); r++) {
                gbc.gridx = 0;
                gbc.gridy = r + 2;
                gbc.gridwidth = 1;
                content.add(createCell(tableNames.get(r), leftColWidth, rowHeight, false, SwingConstants.CENTER), gbc);

                for (int c = 0; c < hours.size(); c++) {
                    gbc.gridx = c + 1;
                    gbc.gridy = r + 2;
                    gbc.gridwidth = 1;
                    content.add(createCell("", hourWidth, rowHeight, false, SwingConstants.CENTER), gbc);
                }
            }

            int totalWidth = leftColWidth + hours.size() * hourWidth;
            int totalHeight = (tableNames.size() + 2) * rowHeight;
            content.setPreferredSize(new Dimension(totalWidth, totalHeight));

            dayGridPanel.add(content, BorderLayout.CENTER);
        }

        // ================= WEEK VIEW =================
        private void buildWeekGridLikeImage() {
            weekGridPanel.removeAll();
            weekGridPanel.setLayout(new BorderLayout());
            weekGridPanel.setBackground(Color.WHITE);

            JPanel content = new JPanel(new GridBagLayout());
            content.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;

            java.util.List<String> hours = createHoursWeek2Hours();
            Calendar weekStart = getWeekStart(currentCalendar);

            int leftColWidth = 90;
            int hourWidth = 110;
            int rowHeight = 38;

            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            content.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbc);

            int colIndex = 1;
            for (int d = 0; d < 7; d++) {
                Calendar day = (Calendar) weekStart.clone();
                day.add(Calendar.DAY_OF_MONTH, d);

                gbc.gridx = colIndex;
                gbc.gridy = 0;
                gbc.gridwidth = hours.size();
                content.add(createCell(formatDayHeader(day), hourWidth * hours.size(), rowHeight, true, SwingConstants.LEFT), gbc);

                colIndex += hours.size();
            }

            gbc.gridy = 1;
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            content.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbc);

            colIndex = 1;
            for (int d = 0; d < 7; d++) {
                for (String hour : hours) {
                    gbc.gridx = colIndex++;
                    gbc.gridy = 1;
                    gbc.gridwidth = 1;
                    content.add(createCell(hour, hourWidth, rowHeight, true, SwingConstants.CENTER), gbc);
                }
            }

            for (int r = 0; r < tableNames.size(); r++) {
                gbc.gridy = r + 2;
                gbc.gridx = 0;
                gbc.gridwidth = 1;
                content.add(createCell(tableNames.get(r), leftColWidth, rowHeight, false, SwingConstants.CENTER), gbc);

                colIndex = 1;
                for (int d = 0; d < 7; d++) {
                    for (int h = 0; h < hours.size(); h++) {
                        gbc.gridx = colIndex++;
                        gbc.gridy = r + 2;
                        gbc.gridwidth = 1;
                        content.add(createCell("", hourWidth, rowHeight, false, SwingConstants.CENTER), gbc);
                    }
                }
            }

            int totalWidth = leftColWidth + (7 * hours.size() * hourWidth);
            int totalHeight = (tableNames.size() + 2) * rowHeight;
            content.setPreferredSize(new Dimension(totalWidth, totalHeight));

            weekGridPanel.add(content, BorderLayout.CENTER);
        }

        // ================= SCHEDULE VIEW =================
        private void buildScheduleViewLikeImage() {
            scheduleListPanel.removeAll();
            scheduleListPanel.setLayout(new BorderLayout());
            scheduleListPanel.setBackground(Color.WHITE);

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(Color.WHITE);

            JPanel header = new JPanel(new GridBagLayout());
            header.setBackground(Color.WHITE);
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;

            int h = 42;
            int rowH = 64;

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            header.add(createCell("Bàn", 80, h, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 1; gbc.weightx = 0;
            header.add(createCell("Ngày đặt", 240, h, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 2; gbc.weightx = 0;
            header.add(createCell("Thời gian", 170, h, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 3; gbc.weightx = 1;
            header.add(createCell("Thông tin", 700, h, true, SwingConstants.LEFT), gbc);

            content.add(header);

            JPanel row = new JPanel(new GridBagLayout());
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowH));

            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            row.add(createCell("A01", 80, rowH, false, SwingConstants.CENTER), gbc);

            gbc.gridx = 1; gbc.weightx = 0;
            row.add(createCell("25 Tháng 03 2026", 240, rowH, false, SwingConstants.CENTER), gbc);

            gbc.gridx = 2; gbc.weightx = 0;
            row.add(createCell("10 AM", 170, rowH, false, SwingConstants.CENTER), gbc);

            gbc.gridx = 3; gbc.weightx = 1;
            row.add(createBookingInfoCell("", 700, rowH), gbc);

            content.add(row);
            content.add(Box.createVerticalGlue());

            scheduleListPanel.add(content, BorderLayout.NORTH);
        }

        // ================= COMMON COMPONENTS =================
        private JPanel createBookingInfoCell(String text, int w, int h) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new LineBorder(new Color(200, 200, 200)));
            wrapper.setPreferredSize(new Dimension(w, h));

            JPanel orangeBox = new JPanel(new BorderLayout());
            orangeBox.setBackground(new Color(220, 170, 76));
            orangeBox.setBorder(new EmptyBorder(10, 18, 10, 18));

            JLabel lbl = new JLabel(text);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 15));
            orangeBox.add(lbl, BorderLayout.WEST);

            JPanel margin = new JPanel(new BorderLayout());
            margin.setOpaque(false);
            margin.setBorder(new EmptyBorder(8, 18, 8, 18));
            margin.add(orangeBox, BorderLayout.CENTER);

            wrapper.add(margin, BorderLayout.CENTER);
            return wrapper;
        }

        private JPanel createCell(String text, int w, int h, boolean header, int align) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(header ? new Color(245, 245, 245) : Color.WHITE);
            cell.setBorder(new LineBorder(new Color(200, 200, 200)));
            cell.setPreferredSize(new Dimension(w, h));

            JLabel lbl = new JLabel(text, align);
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 15));
            lbl.setBorder(new EmptyBorder(0, 14, 0, 14));

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

        // ================= DATA =================
        private java.util.List<String> createTableNames() {
            java.util.List<String> list = new ArrayList<>();

            for (int i = 1; i <= 15; i++) list.add(String.format("A%02d", i));
            for (int i = 1; i <= 20; i++) list.add(String.format("B%02d", i));
            for (int i = 1; i <= 20; i++) list.add(String.format("C%02d", i));

            return list;
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

        enum ViewMode {
            DAY, WEEK, SCHEDULE
        }
    }
}