package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Collections;

public class TK_ChartUtils {
    
    // --- 1. Biểu đồ cột (Bar Chart) ---
    public static JPanel createBarChart(String title, List<String> labels, List<Double> values, Color barColor) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int padding = 40;
                
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);
                
                // Draw title
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(title, padding, 20);
                
                if (values.isEmpty()) return;
                
                double max = Collections.max(values);
                if (max == 0) max = 1;
                
                int chartW = w - padding * 2;
                int chartH = h - padding * 2;
                
                // Axes
                g2.setColor(new Color(200, 200, 200));
                g2.drawLine(padding, h - padding, w - padding, h - padding); // X
                g2.drawLine(padding, padding, padding, h - padding); // Y
                
                // Grid horizontal
                int marks = 4;
                for (int i = 0; i <= marks; i++) {
                    int y = h - padding - (i * chartH / marks);
                    g2.setColor(new Color(230, 230, 230));
                    g2.drawLine(padding, y, w - padding, y);
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    String valStr = String.format("%.0f", max * i / marks);
                    g2.drawString(valStr, padding - 30, y + 4);
                }
                
                // Bars
                int n = values.size();
                int barWidth = chartW / (n * 2);
                for (int i = 0; i < n; i++) {
                    int x = padding + (i * 2 + 1) * chartW / (n * 2) - barWidth / 2;
                    int barH = (int) ((values.get(i) / max) * chartH);
                    int y = h - padding - barH;
                    
                    g2.setColor(barColor);
                    g2.fillRect(x, y, barWidth, barH);
                    
                    // Label
                    g2.setColor(Color.GRAY);
                    String lbl = labels.get(i);
                    int lblW = g2.getFontMetrics().stringWidth(lbl);
                    g2.drawString(lbl, x + barWidth/2 - lblW/2, h - padding + 15);
                }
            }
        };
    }
    
    // --- 2. Biểu đồ mảng (Area Chart) ---
    public static JPanel createAreaChart(String title, List<String> labels, List<Double> values, Color lineColor) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight(), padding = 40;
                
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);
                
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(title, padding, 20);
                
                if (values.isEmpty()) return;
                
                double max = Collections.max(values);
                if (max == 0) max = 1;
                
                int chartW = w - padding * 2, chartH = h - padding * 2;
                
                // Grid
                int marks = 4;
                for (int i = 0; i <= marks; i++) {
                    int y = h - padding - (i * chartH / marks);
                    g2.setColor(new Color(230, 230, 230));
                    g2.drawLine(padding, y, w - padding, y);
                }
                
                int n = values.size();
                int[] xPoints = new int[n + 2];
                int[] yPoints = new int[n + 2];
                
                xPoints[0] = padding; yPoints[0] = h - padding;
                for (int i = 0; i < n; i++) {
                    xPoints[i+1] = padding + i * chartW / Math.max(1, n - 1);
                    yPoints[i+1] = h - padding - (int) ((values.get(i) / max) * chartH);
                    
                    if (n <= 10) { // Render labels if not too many
                        g2.setColor(Color.GRAY);
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                        String lbl = labels.get(i);
                        g2.drawString(lbl, xPoints[i+1] - 5, h - padding + 15);
                    }
                }
                xPoints[n+1] = padding + chartW; yPoints[n+1] = h - padding;
                
                // Draw Area
                g2.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 60)); // Transparent fill
                g2.fillPolygon(xPoints, yPoints, n + 2);
                
                // Draw Line
                g2.setColor(lineColor);
                g2.setStroke(new BasicStroke(2f));
                for (int i = 1; i < n; i++) {
                    g2.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
                }
                
                // Draw dots
                for (int i = 1; i <= n; i++) {
                    g2.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
                }
                
                // Axes
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(padding, h - padding, w - padding, h - padding);
                g2.drawLine(padding, padding, padding, h - padding);
            }
        };
    }
    
    // --- 3. Biểu đồ đường (Line Chart) ---
    public static JPanel createLineChart(String title, List<String> labels, List<Double> values, Color lineColor) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight(), padding = 40;
                
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);
                
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(title, padding, 20);
                
                if (values.isEmpty()) return;
                
                double max = Collections.max(values);
                if (max == 0) max = 1;
                
                int chartW = w - padding * 2, chartH = h - padding * 2;
                
                int marks = 4;
                for (int i = 0; i <= marks; i++) {
                    int y = h - padding - (i * chartH / marks);
                    g2.setColor(new Color(240, 240, 240));
                    g2.drawLine(padding, y, w - padding, y);
                }
                
                int n = values.size();
                int[] xPoints = new int[n];
                int[] yPoints = new int[n];
                
                for (int i = 0; i < n; i++) {
                    xPoints[i] = padding + i * chartW / Math.max(1, n - 1);
                    yPoints[i] = h - padding - (int) ((values.get(i) / max) * chartH);
                    
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    String lbl = labels.get(i);
                    g2.drawString(lbl, xPoints[i] - 10, h - padding + 15);
                }
                
                g2.setColor(lineColor);
                g2.setStroke(new BasicStroke(2f));
                for (int i = 0; i < n - 1; i++) {
                    g2.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
                }
            }
        };
    }
    
    // --- 4. Biểu đồ hỗn hợp (Mixed Bar + Line) ---
    public static JPanel createMixedChart(String title, List<String> labels, List<Double> values1, List<Double> values2, List<Double> lineValues) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight(), padding = 40;
                
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);
                
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(title, padding, 20);
                
                if (values1.isEmpty()) return;
                
                double max = Math.max(Collections.max(values1), Collections.max(values2));
                max = Math.max(max, Collections.max(lineValues));
                if (max == 0) max = 1;
                
                int chartW = w - padding * 2, chartH = h - padding * 2;
                
                 // Sub title labels/legend
                g2.setColor(new Color(60, 150, 220)); g2.fillRect(padding + 20, 30, 10, 10);
                g2.setColor(Color.GRAY); g2.drawString("Doanh thu", padding + 35, 40);
                
                g2.setColor(new Color(130, 200, 120)); g2.fillRect(padding + 100, 30, 10, 10);
                g2.setColor(Color.GRAY); g2.drawString("Chi phí", padding + 115, 40);
                
                g2.setColor(Color.ORANGE); g2.drawLine(padding + 170, 35, padding + 185, 35);
                g2.setColor(Color.GRAY); g2.drawString("Lợi nhuận", padding + 190, 40);

                int marks = 4;
                for (int i = 0; i <= marks; i++) {
                    int y = h - padding - (i * chartH / marks);
                    g2.setColor(new Color(230, 230, 230));
                    g2.drawLine(padding, y, w - padding, y);
                }
                
                int n = values1.size();
                int segmentW = chartW / n;
                int barW = segmentW / 3;
                
                int[] lxPoints = new int[n];
                int[] lyPoints = new int[n];
                
                for (int i = 0; i < n; i++) {
                    int cx = padding + i * segmentW + segmentW / 2;
                    
                    int b1H = (int) ((values1.get(i) / max) * chartH);
                    g2.setColor(new Color(80, 160, 220)); // Blue
                    g2.fillRect(cx - barW + 2, h - padding - b1H, barW - 4, b1H);
                    
                    int b2H = (int) ((values2.get(i) / max) * chartH);
                    g2.setColor(new Color(140, 200, 130)); // Green
                    g2.fillRect(cx + 2, h - padding - b2H, barW - 4, b2H);
                    
                    lxPoints[i] = cx;
                    lyPoints[i] = h - padding - (int) ((lineValues.get(i) / max) * chartH);
                    
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    String lbl = labels.get(i);
                    g2.drawString(lbl, cx - 10, h - padding + 15);
                }
                
                g2.setColor(new Color(240, 150, 40));
                g2.setStroke(new BasicStroke(1.5f));
                for (int i = 0; i < n - 1; i++) {
                    g2.drawLine(lxPoints[i], lyPoints[i], lxPoints[i+1], lyPoints[i+1]);
                }
                
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(padding, h - padding, w - padding, h - padding);
                g2.drawLine(padding, padding, padding, h - padding);
            }
        };
    }
}
