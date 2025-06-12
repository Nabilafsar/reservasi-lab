
import java.sql.Connection;
import java.sql.DriverManager;

// public class DBConnection {

//     private static final String URL = "jdbc:mariadb://localhost:3306/last_lab";
//     private static final String USER = "root";
//     private static final String PASSWORD = "Permataandalan1";

//     public static Connection getConn() {
//         try {
//             System.out.println("Memuat driver...");
//             Class.forName("com.mysql.cj.jdbc.Driver");
//             Class.forName("org.mariadb.jdbc.Driver");
//             Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             return conn;
//         } catch (ClassNotFoundException e) {
//             System.out.println("Driver MySQL tidak ditemukan!");
//             e.printStackTrace();
//         } catch (SQLException e) {
//             System.out.println("Koneksi ke database gagal!");
//             e.printStackTrace();
//         }
//         // return conn;
//     }
// }

public class DBConnection {
    public static Connection getCon() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/last_lab", "root", "Permataandalan1");
            return con;
        } catch (Exception e){
            return null;
        }
    }
    
}
