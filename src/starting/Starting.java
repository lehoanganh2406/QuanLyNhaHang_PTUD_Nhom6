package starting;

import javax.swing.SwingUtilities;
import gui.DangNhap_GUI;
import connectDB.ConnectDB;

public class Starting {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
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