
import java.sql.Connection;
import java.sql.DriverManager;


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
