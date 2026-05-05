package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import dao.PhieuDatMon_DAO;
import digLog.HuyBan_DigLog;
import digLog.PhieuDatBan_DigLog;
import entity.Ban;
import entity.PhieuDatMon;
import entity.TaiKhoan;

public class DatBan_GUI extends JPanel {
    private static final long serialVersionUID = 1L;
    

    private TaiKhoan taiKhoanDangNhap;

    public DatBan_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        DatBanMainPanel mainPanel = new DatBanMainPanel();
        add(mainPanel, BorderLayout.CENTER);
    }

    class DatBanMainPanel extends JPanel {
        private static final long serialVersionUID = 1L;

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
        private JLabel lblSearchInfo;

        private Timer resizeTimer;

        private final java.util.List<JCheckBox> statusCheckBoxes = new ArrayList<>();
        private final java.util.List<String> statusValues = new ArrayList<>();
        private final java.util.List<JComponent> matchedComponents = new ArrayList<>();

        private boolean searchByEnterRequested = false;

        private JScrollPane currentMainScrollPane;
        private JScrollPane currentLeftScrollPane;

        private Date cachedDate = null;
        private final java.util.List<BookingDisplayItem> cachedBookings = new ArrayList<>();
        private final java.util.List<BookingDisplayItem> cachedFilteredBookings = new ArrayList<>();

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
            initResponsiveEvents();
            refreshView();
        }

        private void initResponsiveEvents() {
            resizeTimer = new Timer(220, e -> {
                revalidate();
                repaint();
            });
            resizeTimer.setRepeats(false);

            ComponentAdapter resizeHandler = new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (resizeTimer != null) {
                        resizeTimer.restart();
                    }
                }
            };

            DatBanMainPanel.this.addComponentListener(resizeHandler);
            cardPanel.addComponentListener(resizeHandler);
        }
        private double parseMoney(String text) {
            if (text == null) return 0;
            String so = text.trim()
                    .replace("VNĐ", "")
                    .replace("vnđ", "")
                    .replace("đ", "")
                    .replace("Đ", "")
                    .replace(".", "")
                    .replace(",", "")
                    .trim();
            if (so.isEmpty()) return 0;
            try {
                return Double.parseDouble(so);
            } catch (Exception e) {
                return 0;
            }
        }

        private boolean coDatMonTheoPhieu(String maPhieu) {
            try {
                PhieuDatMon_DAO dao = new PhieuDatMon_DAO();
                ArrayList<PhieuDatMon> ds = dao.getDanhSachTheoMaPhieu(maPhieu);
                return ds != null && !ds.isEmpty();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private void moChiTietPhieu(BookingDisplayItem item) {
            try {
                boolean daHuy = item.trangThai != null
                        && item.trangThai.trim().equalsIgnoreCase("Đã hủy");

                if (daHuy) {
                    PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
                    String[] row = dao.timTheoMaPhieu(item.maPhieu);

                    if (row == null) {
                        JOptionPane.showMessageDialog(
                                DatBan_GUI.this,
                                "Không tìm thấy phiếu đặt bàn!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double tienCoc = parseMoney(row[6]);
                    Timestamp thoiGianDen = Timestamp.valueOf(row[5]);
                    boolean coDatMon = coDatMonTheoPhieu(item.maPhieu);

                    HuyBan_DigLog dialog = new HuyBan_DigLog(
                            (JFrame) SwingUtilities.getWindowAncestor(DatBan_GUI.this),
                            item.maPhieu,
                            tienCoc,
                            thoiGianDen,
                            coDatMon,
                            true
                    );
                    dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(DatBan_GUI.this));
                    dialog.setVisible(true);

                } else {
                	PhieuDatBan_DigLog dialog = new PhieuDatBan_DigLog(
                	        (JFrame) SwingUtilities.getWindowAncestor(DatBan_GUI.this),
                	        item.maPhieu,
                	        taiKhoanDangNhap
                	);
                	dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(DatBan_GUI.this));
                	dialog.setVisible(true);
                }

                clearBookingCache();
                refreshView();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        DatBan_GUI.this,
                        "Không mở được chi tiết phiếu đặt bàn!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearBookingCache() {
            cachedDate = null;
            cachedBookings.clear();
            cachedFilteredBookings.clear();
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
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void initDateChooser() {
            Date today = getTodayStartDate();

            currentCalendar.setTime(today);
            dateChooser.setDate(today);
            dateChooser.setDateFormatString("dd/MM/yyyy");
            dateChooser.setLocale(new Locale("vi", "VN"));
            dateChooser.setPreferredSize(new Dimension(165, 34));
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
            updateDateChooserSize();
            updateDisplayedDateText();
        }

        private void updateDisplayedDateText() {
            JComponent editorComp = dateChooser.getDateEditor().getUiComponent();
            if (!(editorComp instanceof JTextField)) return;

            JTextField editor = (JTextField) editorComp;

            if (currentMode == ViewMode.WEEK) {
                Calendar start = getWeekStart(currentCalendar);
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.DAY_OF_MONTH, 6);

                String text = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")).format(start.getTime())
                        + " - "
                        + new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")).format(end.getTime());

                editor.setText(text);
            } else {
                editor.setText(new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"))
                        .format(currentCalendar.getTime()));
            }
        }

        private void updateDateChooserSize() {
            if (currentMode == ViewMode.WEEK) {
                dateChooser.setPreferredSize(new Dimension(260, 34));
                dateChooser.setMinimumSize(new Dimension(260, 34));
            } else {
                dateChooser.setPreferredSize(new Dimension(165, 34));
                dateChooser.setMinimumSize(new Dimension(165, 34));
            }

            dateChooser.revalidate();
            dateChooser.repaint();
        }

        private boolean isSameDay(Date d1, Date d2) {
            if (d1 == null || d2 == null) return false;

            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(d1);
            c2.setTime(d2);

            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
        }

        private void loadBookingsCache(Date ngay) {
            Date normalized = normalizeDate(ngay);

            if (isSameDay(cachedDate, normalized)) {
                return;
            }

            cachedDate = normalized;
            cachedBookings.clear();
            cachedFilteredBookings.clear();

            try {
                PhieuDatBan_DAO dao = new PhieuDatBan_DAO();
                ArrayList<String[]> ds = dao.getPhieuDatBanTheoNgay(new java.sql.Date(normalized.getTime()));

                for (String[] row : ds) {
                    Timestamp tg = Timestamp.valueOf(row[5]);
                    cachedBookings.add(new BookingDisplayItem(
                            row[0],
                            row[1],
                            row[2],
                            row[3],
                            row[8],
                            tg,
                            Integer.parseInt(row[4]),
                            row[7]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void rebuildFilteredCache() {
            cachedFilteredBookings.clear();
            for (BookingDisplayItem item : cachedBookings) {
                if (isAcceptedByFilter(item)) {
                    cachedFilteredBookings.add(item);
                }
            }
        }
        private java.util.List<BookingDisplayItem> getFilteredBookingsByWeek(Date date) {
            java.util.List<BookingDisplayItem> result = new ArrayList<>();

            Calendar weekStart = getWeekStart(currentCalendar);

            for (int i = 0; i < 7; i++) {
                Calendar d = (Calendar) weekStart.clone();
                d.add(Calendar.DAY_OF_MONTH, i);

                result.addAll(getFilteredBookingsByDate(d.getTime()));
            }

            return result;
        }

        private java.util.List<BookingDisplayItem> getFilteredBookingsByDate(Date ngay) {
            loadBookingsCache(ngay);
            rebuildFilteredCache();
            return new ArrayList<>(cachedFilteredBookings);
        }

        private Map<String, BookingDisplayItem> buildDaySlotMap(Date ngay) {
            Map<String, BookingDisplayItem> map = new HashMap<>();
            java.util.List<BookingDisplayItem> ds = getFilteredBookingsByDate(ngay);

            for (BookingDisplayItem item : ds) {
                String tenBanDB = tableNameMap.getOrDefault(item.maBan, item.maBan);

                Calendar cal = Calendar.getInstance();
                cal.setTime(item.thoiGianDen);
                int bookingHour = cal.get(Calendar.HOUR_OF_DAY);

                String key = tenBanDB.toLowerCase() + "_" + bookingHour;
                map.put(key, item);
            }
            return map;
        }

        private Map<String, BookingDisplayItem> buildWeekSlotMap(Calendar weekStart, java.util.List<Integer> hours) {
            Map<String, BookingDisplayItem> map = new HashMap<>();

            for (int d = 0; d < 7; d++) {
                Calendar day = (Calendar) weekStart.clone();
                day.add(Calendar.DAY_OF_MONTH, d);

                java.util.List<BookingDisplayItem> ds = getFilteredBookingsByDate(day.getTime());

                for (BookingDisplayItem item : ds) {
                    String tenBanDB = tableNameMap.getOrDefault(item.maBan, item.maBan);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(item.thoiGianDen);
                    int bookingHour = cal.get(Calendar.HOUR_OF_DAY);

                    for (int h = 0; h < hours.size(); h++) {
                        int startHour = hours.get(h);
                        int endHourExclusive = (h == hours.size() - 1) ? 24 : hours.get(h + 1);

                        if (bookingHour >= startHour && bookingHour < endHourExclusive) {
                            String key = d + "_" + tenBanDB.toLowerCase() + "_" + startHour;
                            map.put(key, item);
                            break;
                        }
                    }
                }
            }

            return map;
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
                if (item.trangThai == null) {
                    return false;
                }

                String trangThai = item.trangThai.trim();
                if (!selectedStatuses.contains(trangThai)) {
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

        private boolean hasActiveFilter() {
            return !getRealText(txtSearchMaPhieu, "Nhập mã phiếu đặt bàn").isEmpty()
                    || !getRealText(txtSearchSdt, "Nhập số điện thoại").isEmpty()
                    || !getSelectedStatuses().isEmpty();
        }

        private int countMatchesCurrentMode() {
            int count = 0;

            if (currentMode == ViewMode.DAY || currentMode == ViewMode.SCHEDULE) {
                count = getFilteredBookingsByDate(currentCalendar.getTime()).size();
            } else if (currentMode == ViewMode.WEEK) {
                Calendar weekStart = getWeekStart(currentCalendar);
                for (int d = 0; d < 7; d++) {
                    Calendar day = (Calendar) weekStart.clone();
                    day.add(Calendar.DAY_OF_MONTH, d);
                    count += getFilteredBookingsByDate(day.getTime()).size();
                }
            }

            return count;
        }

        private Color getStatusColor(String trangThai) {
            if (trangThai == null) return new Color(220, 170, 76);

            String s = trangThai.trim().toLowerCase();

            if (s.equals("hoàn thành")) return new Color(124, 183, 103);
            if (s.equals("đã nhận bàn")) return new Color(46, 134, 222);
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
            gbc.insets = new Insets(0, 0, 16, 0);
            panel.add(createStatusBlock(), gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 0, 0);
            panel.add(createSearchInfoBlock(), gbc);

            gbc.gridy = 4;
            gbc.weighty = 1;
            panel.add(Box.createVerticalGlue(), gbc);

            return panel;
        }

        private JPanel createSearchInfoBlock() {
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);

            lblSearchInfo = new JLabel(" ");
            lblSearchInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblSearchInfo.setForeground(new Color(90, 90, 90));
            lblSearchInfo.setVerticalAlignment(SwingConstants.TOP);
            lblSearchInfo.setBorder(new EmptyBorder(4, 4, 4, 4));

            wrap.add(lblSearchInfo, BorderLayout.CENTER);
            return wrap;
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

            txt.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    searchByEnterRequested = false;
                    refreshView();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    searchByEnterRequested = false;
                    refreshView();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    searchByEnterRequested = false;
                    refreshView();
                }
            });

            txt.addActionListener(e -> {
                searchByEnterRequested = true;
                refreshView();
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
            addStatusItem(statusList, "Đã nhận bàn", new Color(46, 134, 222));
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
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(DatBan_GUI.this);

                    PhieuDatBan_DigLog dialog = new PhieuDatBan_DigLog(parentFrame);
                    dialog.setLocationRelativeTo(parentFrame);
                    dialog.setVisible(true);

                    clearBookingCache();
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
            chooser.setPreferredSize(new Dimension(165, 34));
            chooser.setBorder(new LineBorder(new Color(180, 180, 180), 1));
        }

        private void refreshView() {
            matchedComponents.clear();
            currentMainScrollPane = null;
            currentLeftScrollPane = null;

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

            SwingUtilities.invokeLater(this::afterRefreshFocusAndMessage);
        }

        private void afterRefreshFocusAndMessage() {
            if (!hasActiveFilter()) {
                lblSearchInfo.setText(" ");
                return;
            }

            int count = countMatchesCurrentMode();

            if (count <= 0 || matchedComponents.isEmpty()) {
                lblSearchInfo.setText(" ");
                if (searchByEnterRequested) {
                    searchByEnterRequested = false;
                    JOptionPane.showMessageDialog(
                            DatBan_GUI.this,
                            "Không tìm thấy lịch bàn phù hợp!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }

            lblSearchInfo.setText(" ");
            scrollToComponent(matchedComponents.get(0));
            searchByEnterRequested = false;
        }

        private void scrollToComponent(JComponent comp) {
            if (comp == null || currentMainScrollPane == null) return;

            JViewport viewport = currentMainScrollPane.getViewport();
            if (viewport == null || viewport.getView() == null) return;

            Rectangle bounds = SwingUtilities.convertRectangle(
                    comp.getParent(),
                    comp.getBounds(),
                    viewport.getView());

            int targetX = Math.max(0, bounds.x - 120);
            int targetY = Math.max(0, bounds.y - 80);

            viewport.setViewPosition(new Point(targetX, targetY));

            if (currentLeftScrollPane != null) {
                currentLeftScrollPane.getViewport().setViewPosition(new Point(0, targetY));
            }

            Object oldBorder = comp.getClientProperty("oldBorder");
            if (oldBorder == null) {
                oldBorder = comp.getBorder();
                comp.putClientProperty("oldBorder", oldBorder);
            }

            comp.setBorder(new LineBorder(new Color(255, 80, 80), 3));

            Timer timer = new Timer(1500, e -> {
                Object old = comp.getClientProperty("oldBorder");
                if (old instanceof javax.swing.border.Border) {
                    comp.setBorder((javax.swing.border.Border) old);
                }
            });
            timer.setRepeats(false);
            timer.start();
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

        private JPanel createFrozenTablePanel(
                JPanel topLeftPanel,
                JPanel headerPanel,
                JPanel leftBodyPanel,
                JPanel bodyPanel,
                int fixedWidth,
                int headerHeight) {

            JPanel topLeftWrap = new JPanel(new BorderLayout());
            topLeftWrap.setBackground(Color.WHITE);
            topLeftWrap.add(topLeftPanel, BorderLayout.CENTER);

            JPanel headerWrap = new JPanel(new BorderLayout());
            headerWrap.setBackground(Color.WHITE);
            headerWrap.add(headerPanel, BorderLayout.WEST);

            JPanel leftBodyWrap = new JPanel(new BorderLayout());
            leftBodyWrap.setBackground(Color.WHITE);
            leftBodyWrap.add(leftBodyPanel, BorderLayout.NORTH);

            JPanel bodyWrap = new JPanel(new BorderLayout());
            bodyWrap.setBackground(Color.WHITE);
            bodyWrap.add(bodyPanel, BorderLayout.NORTH);

            JScrollPane topLeftScroll = new JScrollPane(topLeftWrap);
            JScrollPane headerScroll = new JScrollPane(headerWrap);
            JScrollPane leftBodyScroll = new JScrollPane(leftBodyWrap);
            JScrollPane bodyScroll = new JScrollPane(bodyWrap);

            currentMainScrollPane = bodyScroll;
            currentLeftScrollPane = leftBodyScroll;

            configScroll(topLeftScroll);
            configScroll(headerScroll);
            configScroll(leftBodyScroll);
            configScroll(bodyScroll);

            topLeftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            topLeftScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

            headerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            headerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

            leftBodyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            leftBodyScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

            bodyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            bodyScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            bodyScroll.getHorizontalScrollBar().addAdjustmentListener(e -> {
                JViewport hv = headerScroll.getViewport();
                Point p = hv.getViewPosition();
                hv.setViewPosition(new Point(e.getValue(), p.y));
            });

            bodyScroll.getVerticalScrollBar().addAdjustmentListener(e -> {
                JViewport lv = leftBodyScroll.getViewport();
                Point p = lv.getViewPosition();
                lv.setViewPosition(new Point(p.x, e.getValue()));
            });

            topLeftScroll.setPreferredSize(new Dimension(fixedWidth, headerHeight));
            headerScroll.setPreferredSize(new Dimension(0, headerHeight));
            leftBodyScroll.setPreferredSize(new Dimension(fixedWidth, 0));

            int vScrollWidth = bodyScroll.getVerticalScrollBar().getPreferredSize().width;
            headerScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, vScrollWidth));

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(topLeftScroll, BorderLayout.WEST);
            topPanel.add(headerScroll, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(leftBodyScroll, BorderLayout.WEST);
            bottomPanel.add(bodyScroll, BorderLayout.CENTER);

            JPanel root = new JPanel(new BorderLayout());
            root.add(topPanel, BorderLayout.NORTH);
            root.add(bottomPanel, BorderLayout.CENTER);

            return root;
        }

        private JPanel buildDayGrid() {
            JPanel topLeftPanel = new JPanel(new GridBagLayout());
            JPanel headerPanel = new JPanel(new GridBagLayout());
            JPanel leftBodyPanel = new JPanel(new GridBagLayout());
            JPanel bodyPanel = new JPanel(new GridBagLayout());

            topLeftPanel.setBackground(Color.WHITE);
            headerPanel.setBackground(Color.WHITE);
            leftBodyPanel.setBackground(Color.WHITE);
            bodyPanel.setBackground(Color.WHITE);

            GridBagConstraints gbcTL = new GridBagConstraints();
            gbcTL.fill = GridBagConstraints.BOTH;
            gbcTL.gridx = 0;

            GridBagConstraints gbcH = new GridBagConstraints();
            gbcH.fill = GridBagConstraints.BOTH;

            GridBagConstraints gbcL = new GridBagConstraints();
            gbcL.fill = GridBagConstraints.BOTH;
            gbcL.gridx = 0;

            GridBagConstraints gbcB = new GridBagConstraints();
            gbcB.fill = GridBagConstraints.BOTH;

            java.util.List<Integer> hours = createHourValuesDay();
            java.util.List<String> visibleTables = new ArrayList<>(tableNames);
            Map<String, BookingDisplayItem> daySlotMap = buildDaySlotMap(currentCalendar.getTime());

            int availableWidth = getCenterAvailableWidth();
            int rowHeight = 52;
            int headerHeight = rowHeight * 2;

            int leftColWidth = clamp((int) (availableWidth * 0.16), 170, 230);
            int gridWidth = Math.max(1200, availableWidth - leftColWidth);
            int[] hourWidths = buildEqualWidths(gridWidth, hours.size(), 110);

            int totalGridWidth = 0;
            for (int w : hourWidths) totalGridWidth += w;

            gbcTL.gridy = 0;
            topLeftPanel.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbcTL);
            gbcTL.gridy = 1;
            topLeftPanel.add(createCell("Bàn", leftColWidth, rowHeight, true, SwingConstants.LEFT), gbcTL);

            gbcH.gridx = 0;
            gbcH.gridy = 0;
            gbcH.gridwidth = hours.size();
            headerPanel.add(
                    createCell(formatDayHeaderFull(currentCalendar), totalGridWidth, rowHeight, true, SwingConstants.LEFT),
                    gbcH
            );

            for (int i = 0; i < hours.size(); i++) {
                gbcH.gridx = i;
                gbcH.gridy = 1;
                gbcH.gridwidth = 1;
                headerPanel.add(
                        createCell(formatHour24(hours.get(i)), hourWidths[i], rowHeight, true, SwingConstants.CENTER),
                        gbcH
                );
            }

            for (int r = 0; r < visibleTables.size(); r++) {
                gbcL.gridy = r;
                leftBodyPanel.add(
                        createCell(visibleTables.get(r), leftColWidth, rowHeight, false, SwingConstants.LEFT),
                        gbcL
                );
            }

            for (int r = 0; r < visibleTables.size(); r++) {
                for (int c = 0; c < hours.size(); c++) {
                    gbcB.gridx = c;
                    gbcB.gridy = r;
                    gbcB.gridwidth = 1;

                    String key = visibleTables.get(r).toLowerCase() + "_" + hours.get(c);
                    BookingDisplayItem item = daySlotMap.get(key);
                    bodyPanel.add(createBookingSlotCell(item, hourWidths[c], rowHeight), gbcB);
                }
            }

            topLeftPanel.setPreferredSize(new Dimension(leftColWidth, headerHeight));
            headerPanel.setPreferredSize(new Dimension(totalGridWidth, headerHeight));
            leftBodyPanel.setPreferredSize(new Dimension(leftColWidth, Math.max(1, visibleTables.size()) * rowHeight));
            bodyPanel.setPreferredSize(new Dimension(totalGridWidth, Math.max(1, visibleTables.size()) * rowHeight));

            return createFrozenTablePanel(topLeftPanel, headerPanel, leftBodyPanel, bodyPanel, leftColWidth, headerHeight);
        }
        private int[] buildEqualWidths(int totalWidth, int columnCount, int minWidth) {
            int[] widths = new int[columnCount];
            if (columnCount <= 0) return widths;

            int base = Math.max(minWidth, totalWidth / columnCount);
            int used = base * columnCount;
            int remain = totalWidth - used;

            for (int i = 0; i < columnCount; i++) {
                widths[i] = base;
            }

            int i = 0;
            while (remain > 0) {
                widths[i % columnCount]++;
                remain--;
                i++;
            }

            return widths;
        }

        private JPanel buildWeekGridLikeImage() {
            JPanel topLeftPanel = new JPanel(new GridBagLayout());
            JPanel headerPanel = new JPanel(new GridBagLayout());
            JPanel leftBodyPanel = new JPanel(new GridBagLayout());
            JPanel bodyPanel = new JPanel(new GridBagLayout());

            topLeftPanel.setBackground(Color.WHITE);
            headerPanel.setBackground(Color.WHITE);
            leftBodyPanel.setBackground(Color.WHITE);
            bodyPanel.setBackground(Color.WHITE);

            GridBagConstraints gbcTL = new GridBagConstraints();
            gbcTL.fill = GridBagConstraints.BOTH;
            gbcTL.gridx = 0;

            GridBagConstraints gbcH = new GridBagConstraints();
            gbcH.fill = GridBagConstraints.BOTH;

            GridBagConstraints gbcL = new GridBagConstraints();
            gbcL.fill = GridBagConstraints.BOTH;
            gbcL.gridx = 0;

            GridBagConstraints gbcB = new GridBagConstraints();
            gbcB.fill = GridBagConstraints.BOTH;

            java.util.List<Integer> hours = createHourValuesWeek();
            Calendar weekStart = getWeekStart(currentCalendar);
            java.util.List<String> visibleTables = new ArrayList<>(tableNames);
            Map<String, BookingDisplayItem> weekSlotMap = buildWeekSlotMap(weekStart, hours);

            int availableWidth = getCenterAvailableWidth();
            int rowHeight = 48;
            int headerHeight = rowHeight * 2;

            int leftColWidth = clamp((int) (availableWidth * 0.11), 120, 160);
            int totalHourColumns = 7 * hours.size();

            int gridWidth = Math.max(980, availableWidth - leftColWidth);
            int[] hourWidths = buildEqualWidths(gridWidth, totalHourColumns, 72);

            int totalGridWidth = 0;
            for (int w : hourWidths) totalGridWidth += w;

            gbcTL.gridy = 0;
            topLeftPanel.add(createCell("", leftColWidth, rowHeight, true, SwingConstants.CENTER), gbcTL);
            gbcTL.gridy = 1;
            topLeftPanel.add(createCell("Bàn", leftColWidth, rowHeight, true, SwingConstants.LEFT), gbcTL);

            int colIndex = 0;
            for (int d = 0; d < 7; d++) {
                Calendar day = (Calendar) weekStart.clone();
                day.add(Calendar.DAY_OF_MONTH, d);

                int dayWidth = 0;
                for (int h = 0; h < hours.size(); h++) {
                    dayWidth += hourWidths[colIndex + h];
                }

                gbcH.gridx = colIndex;
                gbcH.gridy = 0;
                gbcH.gridwidth = hours.size();
                headerPanel.add(
                        createCell(formatDayHeader(day), dayWidth, rowHeight, true, SwingConstants.LEFT),
                        gbcH
                );
                colIndex += hours.size();
            }

            colIndex = 0;
            for (int d = 0; d < 7; d++) {
                for (int h = 0; h < hours.size(); h++) {
                    gbcH.gridx = colIndex;
                    gbcH.gridy = 1;
                    gbcH.gridwidth = 1;
                    headerPanel.add(
                            createCell(formatHour24(hours.get(h)), hourWidths[colIndex], rowHeight, true, SwingConstants.CENTER),
                            gbcH
                    );
                    colIndex++;
                }
            }

            for (int r = 0; r < visibleTables.size(); r++) {
                gbcL.gridy = r;
                leftBodyPanel.add(
                        createCell(visibleTables.get(r), leftColWidth, rowHeight, false, SwingConstants.LEFT),
                        gbcL
                );
            }

            colIndex = 0;
            for (int d = 0; d < 7; d++) {
                for (int h = 0; h < hours.size(); h++) {
                    int startHour = hours.get(h);

                    for (int r = 0; r < visibleTables.size(); r++) {
                        gbcB.gridx = colIndex;
                        gbcB.gridy = r;
                        gbcB.gridwidth = 1;

                        String key = d + "_" + visibleTables.get(r).toLowerCase() + "_" + startHour;
                        BookingDisplayItem item = weekSlotMap.get(key);
                        bodyPanel.add(createBookingSlotCell(item, hourWidths[colIndex], rowHeight), gbcB);
                    }
                    colIndex++;
                }
            }

            topLeftPanel.setPreferredSize(new Dimension(leftColWidth, headerHeight));
            headerPanel.setPreferredSize(new Dimension(totalGridWidth, headerHeight));
            leftBodyPanel.setPreferredSize(new Dimension(leftColWidth, Math.max(1, visibleTables.size()) * rowHeight));
            bodyPanel.setPreferredSize(new Dimension(totalGridWidth, Math.max(1, visibleTables.size()) * rowHeight));

            return createFrozenTablePanel(topLeftPanel, headerPanel, leftBodyPanel, bodyPanel, leftColWidth, headerHeight);
        }

        private JComponent buildScheduleViewLikeImage() {
            JPanel content = new JPanel(new java.awt.GridBagLayout());
            content.setBackground(Color.WHITE);

            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.fill = java.awt.GridBagConstraints.BOTH;

            int availableWidth = getCenterAvailableWidth();
            int colBan = clamp((int) (availableWidth * 0.12), 110, 140);
            int colNgay = clamp((int) (availableWidth * 0.15), 130, 170);
            int colThoiGian = clamp((int) (availableWidth * 0.12), 110, 140);
            int colThongTin = Math.max(450, availableWidth - colBan - colNgay - colThoiGian);

            int headerH = 42;
            int rowH = 64;

            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            content.add(createCell("Bàn", colBan, headerH, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 1;
            content.add(createCell("Ngày đặt", colNgay, headerH, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 2;
            content.add(createCell("Thời gian", colThoiGian, headerH, true, SwingConstants.LEFT), gbc);

            gbc.gridx = 3;
            gbc.weightx = 1;
            content.add(createCell("Thông tin", colThongTin, headerH, true, SwingConstants.LEFT), gbc);

            java.util.List<BookingDisplayItem> filtered = getFilteredBookingsByWeek(currentCalendar.getTime());
            if (!filtered.isEmpty()) {
                for (int i = 0; i < filtered.size(); i++) {
                    BookingDisplayItem item = filtered.get(i);
                    int row = i + 1;

                    String tenBan = tableNameMap.getOrDefault(item.maBan, item.maBan);

                    gbc.gridy = row;
                    gbc.gridx = 0;
                    gbc.weightx = 0;
                    gbc.weighty = 0;
                    content.add(createCell(tenBan, colBan, rowH, false, SwingConstants.LEFT), gbc);

                    gbc.gridx = 1;
                    content.add(createCell(formatDateOnly(item.thoiGianDen), colNgay, rowH, false, SwingConstants.CENTER), gbc);

                    gbc.gridx = 2;
                    content.add(createCell(formatTimeOnly(item.thoiGianDen), colThoiGian, rowH, false, SwingConstants.CENTER), gbc);

                    gbc.gridx = 3;
                    gbc.weightx = 1;
                    content.add(createBookingInfoCell(item, colThongTin, rowH), gbc);
                }
            } else {
                gbc.gridy = 1;
                gbc.gridx = 0;
                gbc.gridwidth = 4;
                gbc.weightx = 1;
                gbc.weighty = 0;

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
            int totalHeight = headerH + (filtered.isEmpty() ? 80 : filtered.size() * rowH);
            content.setPreferredSize(new Dimension(totalWidth, totalHeight));

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.add(content, BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(wrapper);
            configScroll(scrollPane);

            currentMainScrollPane = scrollPane;
            currentLeftScrollPane = null;

            return scrollPane;
        }

        private JPanel createBookingInfoCell(BookingDisplayItem item, int w, int h) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new LineBorder(new Color(200, 200, 200)));
            wrapper.setPreferredSize(new Dimension(w, h));
            wrapper.putClientProperty("oldBorder", wrapper.getBorder());

            JPanel colorBox = new JPanel(new BorderLayout());
            colorBox.setBackground(getStatusColor(item.trangThai));
            colorBox.setBorder(new EmptyBorder(10, 18, 10, 18));
            colorBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

            String text = safe(item.maPhieu) + "        " + safe(item.sdt) + "        " + safe(item.tenKhach);
            JLabel lbl = new JLabel(text);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
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

            if (hasActiveFilter() && isAcceptedByFilter(item)) {
                matchedComponents.add(wrapper);
            }

            return wrapper;
        }

        private JPanel createBookingSlotCell(BookingDisplayItem item, int w, int h) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(new LineBorder(new Color(200, 200, 200)));
            wrapper.setPreferredSize(new Dimension(w, h));
            wrapper.setMinimumSize(new Dimension(w, h));
            wrapper.setMaximumSize(new Dimension(w, h));
            wrapper.putClientProperty("oldBorder", wrapper.getBorder());

            if (item == null) {
                return wrapper;
            }

            JPanel colorBox = new JPanel();
            colorBox.setLayout(new BoxLayout(colorBox, BoxLayout.Y_AXIS));
            colorBox.setBackground(getStatusColor(item.trangThai));
            colorBox.setBorder(new EmptyBorder(4, 8, 4, 8));
            colorBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblPhone = new JLabel(item.sdt == null ? "" : item.sdt);
            lblPhone.setForeground(Color.WHITE);
            lblPhone.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblName = new JLabel(item.tenKhach == null ? "" : item.tenKhach);
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

            if (hasActiveFilter() && isAcceptedByFilter(item)) {
                matchedComponents.add(wrapper);
            }

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
            cell.setMinimumSize(new Dimension(w, h));
            cell.setMaximumSize(new Dimension(w, h));

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

        private java.util.List<Integer> createHourValuesDay() {
            java.util.List<Integer> hours = new ArrayList<>();
            for (int h = 9; h <= 23; h++) {
                hours.add(h);
            }
            return hours;
        }

        private java.util.List<Integer> createHourValuesWeek() {
            java.util.List<Integer> hours = new ArrayList<>();
            for (int h = 9; h <= 23; h += 2) {
                hours.add(h);
            }
            return hours;
        }

        private String formatHour24(int h) {
            return String.format("%02d:00", h);
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

        private Date getTodayStartDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        private Date normalizeDate(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        private boolean isBeforeToday(Date date) {
            return normalizeDate(date).before(getTodayStartDate());
        }

        private int getCenterAvailableWidth() {
            int width = 0;

            if (cardPanel != null && cardPanel.isShowing()) {
                width = cardPanel.getWidth();
            }

            if (width <= 0 && currentMainScrollPane != null) {
                JViewport vp = currentMainScrollPane.getViewport();
                if (vp != null) {
                    width = vp.getWidth();
                }
            }

            if (width <= 0 && DatBanMainPanel.this.getParent() != null) {
                width = DatBanMainPanel.this.getParent().getWidth() - 24;
            }

            if (width <= 0) {
                width = DatBanMainPanel.this.getWidth() - 24;
            }

            return Math.max(900, width);
        }

        private int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }

        private void initEvents() {
            btnToday.addActionListener(e -> {
                Date today = getTodayStartDate();
                currentCalendar.setTime(today);
                dateChooser.setDate(today);
                clearBookingCache();
                updateDisplayedDateText();
                refreshView();
            });

            btnPrev.addActionListener(e -> {
                Calendar temp = (Calendar) currentCalendar.clone();

                if (currentMode == ViewMode.DAY || currentMode == ViewMode.SCHEDULE) {
                    temp.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    temp.add(Calendar.DAY_OF_MONTH, -7);
                }

                
                currentCalendar.setTime(normalizeDate(temp.getTime()));
                dateChooser.setDate(currentCalendar.getTime());
                clearBookingCache();
                updateDisplayedDateText();
                refreshView();
            });

            btnNext.addActionListener(e -> {
                if (currentMode == ViewMode.DAY || currentMode == ViewMode.SCHEDULE) {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
                } else {
                    currentCalendar.add(Calendar.DAY_OF_MONTH, 7);
                }

                currentCalendar.setTime(normalizeDate(currentCalendar.getTime()));
                dateChooser.setDate(currentCalendar.getTime());
                clearBookingCache();
                updateDisplayedDateText();
                refreshView();
            });

            dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
                Date selectedDate = dateChooser.getDate();
                if (selectedDate != null) {
                    currentCalendar.setTime(normalizeDate(selectedDate));

                    clearBookingCache();
                    updateDisplayedDateText();
                    refreshView();
                }
            });

            btnDayView.addActionListener(e -> {
                currentMode = ViewMode.DAY;
                clearBookingCache();
                updateDateChooserSize();
                updateDisplayedDateText();
                refreshView();
            });

            btnWeekView.addActionListener(e -> {
                currentMode = ViewMode.WEEK;
                clearBookingCache();
                updateDateChooserSize();
                updateDisplayedDateText();
                refreshView();
            });

            btnScheduleView.addActionListener(e -> {
                currentMode = ViewMode.SCHEDULE;
                clearBookingCache();
                updateDateChooserSize();
                updateDisplayedDateText();
                refreshView();
            });
        }

        private void configScroll(JScrollPane sp) {
            sp.setBorder(null);
            sp.getViewport().setBackground(Color.WHITE);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(true);
            sp.getVerticalScrollBar().setUnitIncrement(16);
            sp.getHorizontalScrollBar().setUnitIncrement(16);
        }

        enum ViewMode {
            DAY, WEEK, SCHEDULE
        }
    }
    
}