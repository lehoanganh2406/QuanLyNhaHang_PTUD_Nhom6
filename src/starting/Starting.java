package starting;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.DangNhap_GUI;
import connectDB.ConnectDB;

public class Starting {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
            	UIManager.put("Button.contentAreaFilled", false);
            	UIManager.put("Button.focus", null);
            	UIManager.put("Button.select", new Color(0,0,0,0));
            	UIManager.put("CheckBox.background", Color.WHITE);
            	UIManager.put("Panel.background", new Color(245,245,245));
                // Kết nối cơ sở dữ liệu
                ConnectDB.getInstance().connect();

                // Mở form đăng nhập
                new DangNhap_GUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Không thể kết nối cơ sở dữ liệu!",
                        "Lỗi",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}