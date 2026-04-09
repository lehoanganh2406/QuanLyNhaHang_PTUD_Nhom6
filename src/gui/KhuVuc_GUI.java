package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

import entity.TaiKhoan;

public class KhuVuc_GUI extends JFrame {

    private TaiKhoan taiKhoanDangNhap;

    public KhuVuc_GUI(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;

        setTitle("Bàn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu
        Pn_ThanhMenu menu = new Pn_ThanhMenu(taiKhoanDangNhap);
        add(menu, BorderLayout.NORTH);

        // TODO: phần giao diện còn lại
        // add(panelCenter, BorderLayout.CENTER);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
}