package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Component;

import entity.CaLamViec;
import entity.TaiKhoan;
import java.time.format.DateTimeFormatter;

public class ThongKeTheoCa_GUI extends JPanel {

    private TaiKhoan taiKhoanDangNhap;
    private JPanel rightFormContainer;

    public ThongKeTheoCa_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createMainPanel(), BorderLayout.CENTER);
    }

    public ThongKeTheoCa_GUI() {
        this(null);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(25, 30, 40, 30));

        // Add Sections
        contentContainer.add(wrapInNorth(createHeaderPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        contentContainer.add(wrapInNorth(createControlPanel()));
        contentContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        contentContainer.add(wrapInNorth(createChartsSection()));

        JPanel smoothScrollWrapper = new JPanel(new BorderLayout());
        smoothScrollWrapper.setOpaque(false);
        smoothScrollWrapper.add(contentContainer, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(smoothScrollWrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel wrapInNorth(JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(comp, BorderLayout.NORTH);
        return p;
    }

    private JPanel createControlPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        JButton btnTongKet = new JButton("Tổng kết ca");
        btnTongKet.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnTongKet.setBackground(new Color(34, 197, 94)); // Green
        btnTongKet.setForeground(Color.WHITE);
        btnTongKet.setFocusPainted(false);
        btnTongKet.setOpaque(true);
        btnTongKet.setBorderPainted(false);
        btnTongKet.addActionListener(e -> {
            showTongKetCaForm();
        });

        p.add(btnTongKet);

        return p;
    }

    private JPanel createHeaderPanel() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);

        JLabel lblTitle = new JLabel("Thống kê theo ca");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        hdr.add(lblTitle, BorderLayout.WEST);

        JLabel lblCa = new JLabel("Ca 01");
        lblCa.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblCa.setHorizontalAlignment(SwingConstants.CENTER);
        hdr.add(lblCa, BorderLayout.CENTER);

        JLabel lblDate = new JLabel();
        lblDate.setFont(new Font("SansSerif", Font.PLAIN, 24));
        lblDate.setForeground(Color.DARK_GRAY);
        hdr.add(lblDate, BorderLayout.EAST);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - 'Ngày' dd 'tháng' MM 'năm' yyyy");
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            lblDate.setText(java.time.LocalDateTime.now().format(formatter));
        });
        timer.start();
        lblDate.setText(java.time.LocalDateTime.now().format(formatter));

        return hdr;
    }

    private JPanel createChartsSection() {
        JPanel charts = new JPanel(new GridLayout(1, 2, 25, 0));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(0, 480));

        charts.add(createChartWrapper("DOANH THU THEO GIỜ", new MockAreaChartPanel()));

        rightFormContainer = new JPanel(new BorderLayout());
        rightFormContainer.setOpaque(false);
        charts.add(rightFormContainer);

        return charts;
    }

    private JPanel createChartWrapper(String title, JPanel chart) {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1), // Subtle frame border
                new EmptyBorder(15, 20, 15, 20)));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JLabel lblTop = new JLabel(title);
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTop.setForeground(new Color(80, 80, 80));
        hdr.add(lblTop, BorderLayout.WEST);

        JLabel lblIcons = new JLabel("≡  🖨️"); // Mocking hamburger & print icons
        lblIcons.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcons.setForeground(Color.GRAY);
        hdr.add(lblIcons, BorderLayout.EAST);

        w.add(hdr, BorderLayout.NORTH);
        w.add(chart, BorderLayout.CENTER);

        return w;
    }

    private void showTongKetCaForm() {
        rightFormContainer.removeAll();

        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(Color.WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("XÁC NHẬN TỔNG KẾT CA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(40, 167, 69));

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);

        addFormRow(formPanel, "Mã ca:", "CL001");
        addFormRow(formPanel, "Tên ca:", "Ca sáng");
        addFormRow(formPanel, "Thời gian mở:", "08:00:00 10/04/2026");
        addFormRow(formPanel, "Thời gian đóng:", "16:00:00 10/04/2026");
        addFormRow(formPanel, "Tiền mở ca:", "1,000,000 VNĐ");
        addFormRow(formPanel, "Tiền mặt cuối ca:", "5,500,000 VNĐ");
        addFormRow(formPanel, "Tiền chuyển khoản:", "12,000,000 VNĐ");

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnXacNhan = new JButton("Xác Nhận");
        btnXacNhan.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnXacNhan.setPreferredSize(new Dimension(150, 50));
        btnXacNhan.setBackground(new Color(40, 167, 69));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setOpaque(true);
        btnXacNhan.setBorderPainted(false);
        btnXacNhan.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Đã lưu tổng kết ca thành công!", "Thành công",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            rightFormContainer.removeAll();
            rightFormContainer.revalidate();
            rightFormContainer.repaint();
        });

        btnPanel.add(btnXacNhan);
        mainPanel.add(btnPanel);

        w.add(mainPanel, BorderLayout.CENTER);
        rightFormContainer.add(w, BorderLayout.CENTER);

        rightFormContainer.revalidate();
        rightFormContainer.repaint();
    }

    private void addFormRow(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(new Color(100, 100, 100)); // Màu xám đậm

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("SansSerif", Font.BOLD, 18));
        value.setForeground(Color.BLACK);

        panel.add(label);
        panel.add(value);
    }

    // --- Mock Component for DOANH THU THEO GIỜ ---
    class MockAreaChartPanel extends JPanel {
        public MockAreaChartPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft = 40;
            int padBottom = 25;
            int padTop = 15;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String[] yLabels = { "0", "20M", "40M", "60M", "80M" };
            int numLines = yLabels.length;
            for (int i = 0; i < numLines; i++) {
                int y = h - padBottom - (i * (h - padBottom - padTop) / (numLines - 1));
                g2.setColor(new Color(240, 240, 240));
                g2.drawLine(padLeft, y, w, y);
                g2.setColor(Color.GRAY);
                g2.drawString(yLabels[i], padLeft - 30, y + 4);
            }

            int n = 24;
            double groupW = (w - padLeft) / (double) n;
            int[] px = new int[n];
            int[] py = new int[n];

            for (int i = 0; i < n; i++) {
                int cx = padLeft + (int) (i * groupW) + (int) (groupW / 2);
                px[i] = cx;
                String lbl = i + "h";
                int textW = g2.getFontMetrics().stringWidth(lbl);
                g2.setColor(Color.GRAY);
                // Only show a label every few hours to not cramp text, but original image shows
                // all labels (tiny)
                g2.drawString(lbl, cx - textW / 2, h - 10);

                double val = 0;
                if (i >= 8 && i <= 14)
                    val = (14 - Math.abs(i - 11)) * 1.5;
                if (i >= 16 && i <= 21) {
                    if (i == 19)
                        val = 71;
                    else if (i == 20)
                        val = 68;
                    else
                        val = (22 - Math.abs(i - 19)) * 2;
                }

                int lineH = (int) (val / 80.0 * (h - padBottom - padTop));
                py[i] = h - padBottom - lineH;
            }

            java.awt.Polygon poly = new java.awt.Polygon();
            poly.addPoint(px[0], h - padBottom);
            for (int i = 0; i < n; i++)
                poly.addPoint(px[i], py[i]);
            poly.addPoint(px[n - 1], h - padBottom);

            g2.setColor(new Color(110, 170, 230, 80));
            g2.fillPolygon(poly);

            g2.setColor(new Color(80, 150, 200));
            Stroke oldStr = g2.getStroke();
            g2.setStroke(new BasicStroke(2f));
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(px[i], py[i], px[i + 1], py[i + 1]);
            }
            g2.setStroke(oldStr);

            g2.setColor(Color.WHITE);
            for (int i = 0; i < n; i++) {
                g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
            }
            g2.setColor(new Color(80, 150, 200));
            for (int i = 0; i < n; i++) {
                g2.drawOval(px[i] - 3, py[i] - 3, 6, 6);
            }

            int tx = px[19];
            int ty = py[19];

            // vertical line highlighting the point
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(tx, padTop, tx, h - padBottom);

            g2.fillOval(tx - 5, ty - 5, 10, 10);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(tx - 200, ty - 35, 195, 30, 10, 10);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(tx - 200, ty - 35, 195, 30, 10, 10);

            g2.setColor(new Color(30, 130, 200));
            g2.fillOval(tx - 190, ty - 25, 8, 8);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("Doanh thu theo giờ: 71,886,000đ", tx - 175, ty - 15);

            // Draw 19h label box
            g2.setColor(new Color(230, 240, 250));
            g2.fillRoundRect(tx - 8, padTop - 10, 25, 15, 5, 5);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("19h", tx - 4, padTop + 2);
        }
    }
    }