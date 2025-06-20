
import java.sql.*;

public class pemesananDAO {

    public static void tambahPemesanan(int idMK, int idLab, String tanggal, String jamMulai, String jamAkhir, String keperluan, int idUser, Connection conn) throws SQLException {
        String sql = "INSERT INTO pemesanan (idMK, idLab, tanggal, jamMulai, jamAkhir, keperluan, idUser) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idMK);
        ps.setInt(2, idLab);
        ps.setString(3, tanggal);
        ps.setString(4, jamMulai);
        ps.setString(5, jamAkhir);
        ps.setString(6, keperluan);
        ps.setInt(7, idUser);
        ps.executeUpdate();
    }

    public static ResultSet getLaporanDosen(String prodi, String tanggal, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call laporan_pemesanan_by_prodi_tanggal_new(?, ?)}");
        cs.setString(1, prodi);
        cs.setString(2, tanggal);
        return cs.executeQuery();
    }

    public static ResultSet getLaporanMahasiswa(String prodi, String tanggal, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call laporan_pemesanan_by_prodi_tanggal_mahasiswa_new(?, ?)}");
        cs.setString(1, prodi);
        cs.setString(2, tanggal);
        return cs.executeQuery();
    }

    public static ResultSet hapus_rerservasi(int idHapus, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call hapus_reservasi(?)}");
        cs.setInt(1, idHapus);
        return cs.executeQuery();
    }

    public static ResultSet getPemesananByUser(int idUser, Connection conn) throws SQLException {
        String sql = "SELECT p.idPemesanan, mk.namaMK, l.namaLab, p.tanggal, p.jamMulai, p.jamAkhir, p.keperluan "
                + "FROM pemesanan p "
                + "JOIN matakuliah mk ON p.idMK = mk.idMK "
                + "JOIN lab l ON p.idLab = l.idLab "
                + "WHERE p.idUser = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idUser);
        return ps.executeQuery();
    }

    public static int updatePemesanan(int idPemesanan, int idMK, int idLab, String tanggal, String jamMulai, String jamAkhir, String keperluan, Connection conn) throws SQLException {
        String sql = "UPDATE pemesanan SET idMK = ?, idLab = ?,tanggal = ?, jamMulai = ?, jamAkhir = ?, keperluan = ? WHERE idPemesanan = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idMK);
        ps.setInt(2, idLab);
        ps.setString(3, tanggal);
        ps.setString(4, jamMulai);
        ps.setString(5, jamAkhir);
        ps.setString(6, keperluan);
        ps.setInt(7, idPemesanan);
        return ps.executeUpdate();
    }

    public static ResultSet getRekapPemesananPerBulan(Connection conn) throws SQLException {
        String sql = "SELECT l.namaLab, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 1 THEN 1 ELSE 0 END) AS `Jan`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 2 THEN 1 ELSE 0 END) AS `Feb`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 3 THEN 1 ELSE 0 END) AS `Mar`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 4 THEN 1 ELSE 0 END) AS `Apr`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 5 THEN 1 ELSE 0 END) AS `Mei`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 6 THEN 1 ELSE 0 END) AS `Jun`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 7 THEN 1 ELSE 0 END) AS `Jul`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 8 THEN 1 ELSE 0 END) AS `Ags`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 9 THEN 1 ELSE 0 END) AS `Sep`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 10 THEN 1 ELSE 0 END) AS `Okt`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 11 THEN 1 ELSE 0 END) AS `Nov`, "
                + "SUM(CASE WHEN MONTH(p.tanggal) = 12 THEN 1 ELSE 0 END) AS `Dec` "
                + "FROM lab l "
                + "LEFT JOIN pemesanan p ON l.idLab = p.idLab "
                + "GROUP BY l.namaLab";

        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeQuery();
    }

    public static ResultSet getJumlahPemesananGabungProdi(Connection conn) throws SQLException {
        String sql
                = "WITH PemesananDenganProdi AS ( "
                + "  SELECT p.idPemesanan, p.tanggal, COALESCE(m.idProdi, d.idProdi) AS idProdi "
                + "  FROM pemesanan p "
                + "  JOIN users u ON p.idUser = u.idUser "
                + "  LEFT JOIN mahasiswa m ON u.idUser = m.idUser "
                + "  LEFT JOIN dosen d ON u.idUser = d.idUser "
                + ") "
                + "SELECT pr.namaProdi, COUNT(*) AS totalReservasi "
                + "FROM PemesananDenganProdi pdp "
                + "JOIN prodi pr ON pdp.idProdi = pr.idProdi "
                + "GROUP BY pr.namaProdi "
                + "ORDER BY totalReservasi DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeQuery();
    }

    public static ResultSet getUserDiAtasRataRataPemesanan(Connection conn) throws SQLException {
    String sql = 
        "SELECT u.username, COUNT(p.idPemesanan) AS jumlah_pemesanan " +
        "FROM users u " +
        "JOIN pemesanan p ON u.idUser = p.idUser " +
        "GROUP BY u.idUser " +
        "HAVING COUNT(p.idPemesanan) > (" +
        "   SELECT AVG(jml) FROM (" +
        "       SELECT COUNT(idPemesanan) AS jml " +
        "       FROM pemesanan " +
        "       GROUP BY idUser" +
        "   ) AS avg_table" +
        ")";
    
    PreparedStatement ps = conn.prepareStatement(sql);
    return ps.executeQuery();
}


}
