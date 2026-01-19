package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectDB {
	private static Connection con = null;
	private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        if (instance == null)
            instance = new ConnectDB();
        return instance;
    }
	public void connect()  {
        String username = "sa";
        String password = "Qazwsxedc@12345";
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyDatBanNhaHangHyVong;encrypt=true;trustServerCertificate=true;loginTimeout=5";
        

        
        try {
        	if (con == null || con.isClosed()) {
        		con = DriverManager.getConnection(url, username, password);
    			System.out.println("Kết nối database thành công.");
            }
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Kết nối thất bại!");
		}
        
    }
	
	public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                ConnectDB.getInstance().connect(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
    
	public static void closeConnection(Connection c) {
		try {
			if (c != null && !c.isClosed()) {
				c.close();
				System.out.println("Đã đóng kết nối database.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
