import java.sql.*;

public class userDAO {

    public static ResultSet validateLogin(String username, String password, Connection conn) {
        String sql = "SELECT u.idUser, r.namaRole FROM users u JOIN role r ON u.idRole = r.idRole WHERE u.username = ? AND u.password = ?";
        ResultSet rs = null;
        try {    
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
}
