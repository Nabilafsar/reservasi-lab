import java.sql.*;

public class userDAO {
    public static ResultSet validateLogin(String username, String password, Connection conn) throws SQLException {
        String sql = "SELECT u.idUser, u.username, r.namaRole FROM users u " +
                     "JOIN role r ON u.idRole = r.idRole WHERE username=? AND password=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        return ps.executeQuery();
    }
}
