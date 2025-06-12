
import java.sql.*;
import java.util.Scanner;

public class main {

    static final String URL = "jdbc:mysql://localhost:3306/last_lab";
    static final String USER = "root";
    static final String PASSWORD = "Permataandalan1";

    static Connection conn;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            login();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void login() throws SQLException {
        System.out.println("====== Selamat Datang ======");
        System.out.println("===== DI RESERVASI LAB =====");
        Scanner sc = new Scanner(System.in);
        System.out.print("Masukkan Username : "); 
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        ResultSet rs = userDAO.validateLogin(username, password, conn);

        if (rs.next()) {
            String role = rs.getString("namaRole");
            System.out.println("Login sebagai: " + role);
            switch (role) {
                case "admin":
                    adminMenu();
                    break;
                case "dosen":
                    dosenMenu(rs.getInt("idUser"));
                    break;
                case "mahasiswa":
                    mahasiswaMenu(rs.getInt("idUser"));
                    break;
                default:
                    System.out.println("Role tidak memiliki akses.");
            }
        } else {
            System.out.println("Login gagal!");
        }
    }

    static void adminMenu() throws SQLException {
        while (true) {
            System.out.println("\n--- Menu Admin ---");
            System.out.println("1. Lihat Laporan Pemesanan");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            int pilih = scanner.nextInt();
            scanner.nextLine();

            switch (pilih) {
                case 1:
                    tampilkanLaporan();
                    break;
                case 0:
                    return;
            }
        }
    }

    static void tampilkanLaporan() throws SQLException {
        CallableStatement cs = conn.prepareCall("{call laporan_pemesanan_by_prodi_tanggal(?, ?)}");
        System.out.print("masukkan jurusan : ");
        String nm = scanner.nextLine();
        cs.setString(1, nm);
        System.out.print("masukkan Tanggal pemesanan\nYYYY-MM-DD :");
        String tgl = scanner.next();
        cs.setString(2, tgl);

        ResultSet rs = cs.executeQuery();
        System.out.println("\n--- Laporan Pemesanan Lab ---");
        while (rs.next()) {
            System.out.println("Pemesanan ID: " + rs.getInt("idPemesanan"));
            System.out.println("User        : " + rs.getString("userName"));
            System.out.println("Mata Kuliah : " + rs.getString("namaMK"));
            System.out.println("Lab         : " + rs.getString("namaLab"));
            System.out.println("Tanggal     : " + rs.getDate("tanggal"));
            System.out.println("Jam         : " + rs.getTime("jamMulai") + " - " + rs.getTime("jamAkhir"));
            System.out.println("Keperluan   : " + rs.getString("keperluan"));
            System.out.println("------------------------------------");
        }
        CallableStatement cs2 = conn.prepareCall("{call laporan_pemesanan_by_prodi_tanggal_mahasiswa(?, ?)}");
        cs2.setString(1, nm);
        cs2.setString(2, tgl);

        ResultSet rs2 = cs2.executeQuery();
        while (rs2.next()) {
            System.out.println("Pemesanan ID: " + rs2.getInt("idPemesanan"));
            System.out.println("User        : " + rs2.getString("userName"));
            System.out.println("Mata Kuliah : " + rs2.getString("namaMK"));
            System.out.println("Lab         : " + rs2.getString("namaLab"));
            System.out.println("Tanggal     : " + rs2.getDate("tanggal"));
            System.out.println("Jam         : " + rs2.getTime("jamMulai") + " - " + rs2.getTime("jamAkhir"));
            System.out.println("Keperluan   : " + rs2.getString("keperluan"));
            System.out.println("------------------------------------");
        }
    }

    static void dosenMenu(int idUser) throws SQLException {
        while (true) {
            System.out.println("\n--- Menu Dosen ---");
            System.out.println("1. Pesan Laboratorium");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            int pilih = scanner.nextInt();
            scanner.nextLine();

            if (pilih == 1) {
                tambahPemesanan(idUser);
            } else if (pilih == 0) {
                break;
            }
        }
    }

    static void mahasiswaMenu(int idUser) throws SQLException {

        while (true) {
            System.out.println("\n==== Menu Mahasiswa ====");
            System.out.println("1. Pesan Laboratorium");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            int pilih = scanner.nextInt();
            scanner.nextLine();

            if (pilih == 1) {
                tambahPemesanan(idUser);
            } else if (pilih == 0) {
                break;
            }
        }
    }

    static void tambahPemesanan(int idUser) throws SQLException {
        String sql = "INSERT INTO pemesanan (idMK, idLab, tanggal, jamMulai, jamAkhir, keperluan,idUser) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("'masukkan 10 jika anda Mahasiswa\natau tidak berhubungan dengan mata kuliah'");
            System.out.print("\nID Mata Kuliah: ");
            int idMK = scanner.nextInt();

            System.out.print("ID Lab: ");
            int idLab = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Tanggal (YYYY-MM-DD): ");
            String tanggal = scanner.nextLine();

            System.out.print("Jam Mulai (HH:MM:SS): ");
            String jamMulai = scanner.nextLine();

            System.out.print("Jam Akhir (HH:MM:SS): ");
            String jamAkhir = scanner.nextLine();

            System.out.print("Keperluan: ");
            String keperluan = scanner.nextLine();
            ps.setInt(1, idMK);
            ps.setInt(2, idLab);
            ps.setString(3, tanggal);
            ps.setString(4, jamMulai);
            ps.setString(5, jamAkhir);
            ps.setString(6, keperluan);
            ps.setInt(7, idUser);
            int inserted = ps.executeUpdate();
            System.out.println("Pesanan Berhasil Dibuat");

        } catch (SQLException e) {
            if (e.getMessage().contains("Jadwal bentrok")) {
                System.out.println("Pemesanan gagal: Jadwal bentrok dengan pemesanan yang sudah ada.");
            } else {
                e.printStackTrace();
            }
        }

    }
}