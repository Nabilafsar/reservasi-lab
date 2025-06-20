
import java.sql.*;
import java.util.Scanner;

public class main {

    static final String URL = "jdbc:mysql://localhost:3306/final_reservasi";
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
        boolean valid = false;
        System.out.println("====== Selamat Datang ======");
        System.out.println("===== DI RESERVASI LAB =====");
        do {
            System.out.print("Masukkan Username : ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            ResultSet rs = userDAO.validateLogin(username, password, conn);
            if (rs.next()) {
                valid = true;
                String role = rs.getString("namaRole");
                System.out.println("Login sebagai: " + role);
                int idUser = rs.getInt("idUser");
                switch (role) {
                    case "admin":
                        adminMenu();
                        break;
                    case "dosen":
                        dosenMenu(idUser);
                        break;
                    case "mahasiswa":
                        mahasiswaMenu(idUser);
                        break;
                    default:
                        System.out.println("Role tidak dikenali.");
                }
            } else {
                System.out.println("Login gagal!");
            }
        } while (valid == false);
    }

    static void adminMenu() throws SQLException {
        int pilih1;
        do {
            System.out.println("\n--- Menu Admin ---");
            System.out.println("1. Lihat Laporan Pemesanan");
            System.out.println("2. Hapus Reservasi");
            System.out.println("3. Lihat rekap bulanan");
            System.out.println("4. Tampilkan pemesanan\nper Prodi");
            System.out.println("5. Tampilkan Reservasi user\nDiatas Rata-rata");
            System.out.println("6. Keluar");
            System.out.print("Pilih: ");
            pilih1 = scanner.nextInt();
            scanner.nextLine();
            switch (pilih1) {
                case 1:
                    tampilkanLaporan();
                    break;

                case 2:
                    hapus_pemesanan();
                    break;
                case 3:
                    tampilkanRekapPemesananBulanan();
                    break;
                case 4:
                    tampilkanTotalPemesananPerProdi();
                    break;
                case 5:
                    tampilkanUserDiAtasRataRata();
                case 6:
                    break;
            }

        } while (pilih1 != 6);
    }

    static void dosenMenu(int idUser) throws SQLException {
        int pilih1;
        do {
            System.out.println("\n--- Menu Dosen ---");
            System.out.println("1. Pesan Laboratorium");
            System.out.println("2. Lihat Reservasi Saya");
            System.out.println("3. Lihat semua Reservas");
            System.out.println("4. edit pesanan");
            System.out.println("5. Keluar");
            System.out.print("Pilih: ");
            pilih1 = scanner.nextInt();
            switch (pilih1) {
                case 1:
                    tambahPemesanan(idUser);
                    break;
                case 2:
                    StatusReservasi(idUser);
                    break;
                case 3:
                    SemuaReservasiDisetujui(conn);
                    break;
                case 4:
                    edit_reservasi(idUser);
                    break;
                case 5:
                    break;

            }

        } while (pilih1 != 5);
    }

    static void mahasiswaMenu(int idUser) throws SQLException {
        int pilih1;
        do {
            System.out.println("\n--- Menu Mahasiswa ---");
            System.out.println("1. Pesan Laboratorium");
            System.out.println("2. Status Reservasi");
            System.out.println("3. Lihat Semua Reservasi ");
            System.out.println("4. Edit Reservasi");
            System.out.println("5. Keluar");
            System.out.print("Pilih: ");
            pilih1 = scanner.nextInt();
            switch (pilih1) {
                case 1:
                    tambahPemesanan(idUser);
                    break;
                case 2:
                    StatusReservasi(idUser);
                    break;
                case 3 :
                    SemuaReservasiDisetujui(conn);
                    break;    
                case 4:
                    edit_reservasi(idUser);
                    break;
                case 5:
                    break;
            }

        } while (pilih1 != 5);
    }

    static void tampilkanLaporan() throws SQLException {
        System.out.print("Masukkan jurusan: ");
        String prodi = scanner.nextLine();
        System.out.print("Masukkan tanggal (YYYY-MM-DD): ");
        String tanggal = scanner.next();

        ResultSet rs1 = pemesananDAO.getLaporanDosen(prodi, tanggal, conn);
        ResultSet rs2 = pemesananDAO.getLaporanMahasiswa(prodi, tanggal, conn);

        System.out.println("\n--- Laporan Pemesanan Dosen ---");
        while (rs1.next()) {
            cetakData(rs1);
        }

        System.out.println("\n--- Laporan Pemesanan Mahasiswa ---");
        while (rs2.next()) {
            cetakData(rs2);
        }
    }

    static void cetakData(ResultSet rs) throws SQLException {
        System.out.println("Pemesanan ID: " + rs.getInt("idPemesanan"));
        System.out.println("User        : " + rs.getString("userName"));
        System.out.println("Mata Kuliah : " + rs.getString("namaMK"));
        System.out.println("Lab         : " + rs.getString("namaLab"));
        System.out.println("Tanggal     : " + rs.getDate("tanggal"));
        System.out.println("Jam         : " + rs.getTime("jamMulai") + " - " + rs.getTime("jamAkhir"));
        System.out.println("Keperluan   : " + rs.getString("keperluan"));
        System.out.println("Status      : " + rs.getString("Status"));
        System.out.println("------------------------------------");
    }

    static void tambahPemesanan(int idUser) {
        try {
            System.out.println("=========== SELAMAT DATANG DI MENU RESERVASI ===========");
            System.out.println("Masukkan 10 jika Anda Mahasiswa atau tidak terkait mata kuliah.\n");

            System.out.println("Daftar Mata Kuliah:");
            String sqlMK = "SELECT idMK, namaMK FROM matakuliah";
            Statement stmtMK = conn.createStatement();
            ResultSet rsMK = stmtMK.executeQuery(sqlMK);
            while (rsMK.next()) {
                System.out.println(rsMK.getInt("idMK") + " - " + rsMK.getString("namaMK"));
            }

            System.out.print("\nPilih ID Mata Kuliah: ");
            int idMK = scanner.nextInt();

            System.out.println("\nDaftar Lab:");
            String sqlLab = "SELECT idLab, namaLab FROM lab";
            Statement stmtLab = conn.createStatement();
            ResultSet rsLab = stmtLab.executeQuery(sqlLab);
            while (rsLab.next()) {
                System.out.println(rsLab.getInt("idLab") + " - " + rsLab.getString("namaLab"));
            }

            System.out.print("\nPilih ID Lab: ");
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

            pemesananDAO.tambahPemesanan(idMK, idLab, tanggal, jamMulai, jamAkhir, keperluan, idUser, conn);
            System.out.println("Pesanan Berhasil Dibuat!");
        } catch (SQLException e) {
            if (e.getMessage().contains("Jadwal bentrok")) {
                System.out.println("Pemesanan gagal: Jadwal bentrok.");
            } else {
                e.printStackTrace();
            }
        }
    }

    static void hapus_pemesanan() throws SQLException {
        System.out.println("======== SELAMAT DATANG DI MENU HAPUS RESERVASI ========");
        System.out.print("Masukkan ID Pemesanan : ");
        int idHapus = scanner.nextInt();
        System.out.println("Apakah Anda Yakin?");
        System.out.println("1. YA\n2. Tidak");
        System.out.print("pilih :");
        int pilih1 = scanner.nextInt();
        if (pilih1 == 1) {
            pemesananDAO.hapus_rerservasi(idHapus, conn);
        }
    }

    static void edit_reservasi(int idUser) throws SQLException {
        System.out.println("======== MENU EDIT RESERVASI ========");
        System.out.println("Daftar reservasi Anda:");

        ResultSet rs = pemesananDAO.getPemesananByUser(idUser, conn);
        while (rs.next()) {
            System.out.println("ID Pemesanan : " + rs.getInt("idPemesanan"));
            System.out.println("Mata Kuliah  : " + rs.getString("namaMK"));
            System.out.println("Lab          : " + rs.getString("namaLab"));
            System.out.println("Tanggal      : " + rs.getDate("tanggal"));
            System.out.println("Jam          : " + rs.getTime("jamMulai") + " - " + rs.getTime("jamAkhir"));
            System.out.println("Keperluan    : " + rs.getString("keperluan"));
            System.out.println("------------------------------------");
        }

        System.out.print("Masukkan ID Pemesanan yang ingin diedit: ");
        int idEdit = scanner.nextInt();
        scanner.nextLine();

        System.out.print("ID Mata Kuliah          :");
        int idMK = scanner.nextInt();
        System.out.print("ID Lab                    : ");
        int idLab = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Tanggal baru (YYYY-MM-DD) : ");
        String tanggal = scanner.nextLine();
        System.out.print("Jam Mulai baru (HH:MM:SS) : ");
        String jamMulai = scanner.nextLine();
        System.out.print("Jam Akhir baru (HH:MM:SS) : ");
        String jamAkhir = scanner.nextLine();
        System.out.print("Keperluan baru            : ");
        String keperluan = scanner.nextLine();

        try {
            int updated = pemesananDAO.updatePemesanan(idEdit, idMK, idLab, tanggal, jamMulai, jamAkhir, keperluan, conn);
            if (updated > 0) {
                System.out.println("Reservasi berhasil diperbarui!");
            } else {
                System.out.println("Reservasi tidak diubah.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Jadwal bentrok")) {
                System.out.println("Gagal memperbarui: Jadwal bentrok dengan pemesanan lain.");
            } else {
                e.printStackTrace();
            }
        }
    }

    static void StatusReservasi(int idUser) throws SQLException {
        System.out.println("===== LIHAT RESERVASI =====");

        String sql = "SELECT p.idPemesanan, mk.namaMK, l.namaLab, p.tanggal, "
                + "p.jamMulai, p.jamAkhir, p.keperluan, hr.status "
                + "FROM pemesanan p "
                + "JOIN matakuliah mk ON p.idMK = mk.idMK "
                + "JOIN lab l ON p.idLab = l.idLab "
                + "LEFT JOIN hasil_reservasi hr ON p.idPemesanan = hr.idPemesanan "
                + "WHERE p.idUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("ID Pemesanan : " + rs.getInt("idPemesanan"));
                System.out.println("Mata Kuliah  : " + rs.getString("namaMK"));
                System.out.println("Lab          : " + rs.getString("namaLab"));
                System.out.println("Tanggal      : " + rs.getDate("tanggal"));
                System.out.println("Jam          : " + rs.getTime("jamMulai") + " - " + rs.getTime("jamAkhir"));
                System.out.println("Keperluan    : " + rs.getString("keperluan"));
                System.out.println("Status       : " + rs.getString("status"));
                System.out.println("====================================================================================================================");
            }
        }
    }

    static void tampilkanRekapPemesananBulanan() throws SQLException {
        ResultSet rs = pemesananDAO.getRekapPemesananPerBulan(conn);

        System.out.println("\n========= REKAP PEMESANAN LABORATORIUM PER BULAN =========");
        System.out.println("Nama Laboratorium             | Jan  | Feb  | Mar  |  Apr  | Mei  | Jun  | Jul  | Ags  | Sep  | Okt  | Nov  | Dec  | ");
        System.out.println("====================================================================================================================");

        while (rs.next()) {
            String namaLab = rs.getString("namaLab");
            int jan = rs.getInt("Jan");
            int feb = rs.getInt("Feb");
            int mar = rs.getInt("Mar");
            int apr = rs.getInt("Apr");
            int mei = rs.getInt("Mei");
            int jun = rs.getInt("Jun");
            int jul = rs.getInt("Jul");
            int ags = rs.getInt("Ags");
            int sep = rs.getInt("Sep");
            int okt = rs.getInt("Okt");
            int nov = rs.getInt("Nov");
            int dec = rs.getInt("Dec");

            String line = namaLab;
            while (line.length() < 30) {
                line += " ";
            }
            line += "|  " + jan + "   |  " + feb + "   |  " + mar + "   |  " + apr + "    |  " + mei + "   |  " + jun + "   |  " + jul + "   |  " + ags + "   |  " + sep + "   |  " + okt + "   |  " + nov + "   |  " + dec + "   |   ";
            System.out.println(line);
        }

        System.out.println("-------------------------------------------------------------");
        System.out.println("Catatan: Data ini menampilkan rekap dari Januari sampai April.");
    }

    static void tampilkanTotalPemesananPerProdi() throws SQLException {
        ResultSet rs = pemesananDAO.getJumlahPemesananGabungProdi(conn);

        System.out.println("\n============ Total Pemesanan per Prodi =============");
        System.out.println("======================================================");
        System.out.println("Prodi                          | Total Reservasi");
        System.out.println("======================================================");

        while (rs.next()) {
            String prodi = rs.getString("namaProdi");
            int total = rs.getInt("totalReservasi");
            String baris = prodi;
            while (baris.length() < 30) {
                baris += " ";
            }
            baris += " | " + total;

            System.out.println(baris);
        }
    }

    static void tampilkanUserDiAtasRataRata() throws SQLException {
        ResultSet rs = pemesananDAO.getUserDiAtasRataRataPemesanan(conn);

        System.out.println("\n===== Pengguna dengan Jumlah Pemesanan Di Atas Rata-rata =====");
        System.out.println("Username            | Jumlah Pemesanan");
        System.out.println("===============================================================");

        while (rs.next()) {
            String username = rs.getString("username");
            int jumlah = rs.getInt("jumlah_pemesanan");
            System.out.println(username + "             " + jumlah);
        }
    }

    static void SemuaReservasiDisetujui(Connection conn) throws SQLException {
        System.out.println("===== SEMUA RESERVASI YANG DISETUJUI =====");

        String sql = "SELECT p.idPemesanan, u.username, mk.namaMK, l.namaLab, p.tanggal, "
                + "p.jamMulai, p.jamAkhir, p.keperluan, hr.status "
                + "FROM pemesanan p "
                + "JOIN users u ON p.idUser = u.idUser "
                + "JOIN matakuliah mk ON p.idMK = mk.idMK "
                + "JOIN lab l ON p.idLab = l.idLab "
                + "JOIN hasil_reservasi hr ON p.idPemesanan = hr.idPemesanan "
                + "WHERE hr.status = 'disetujui' "
                + "ORDER BY p.tanggal ASC, p.jamMulai ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("ID Pemesanan : " + rs.getInt("idPemesanan"));
                System.out.println("Pengguna     : " + rs.getString("username"));
                System.out.println("Mata Kuliah  : " + rs.getString("namaMK"));
                System.out.println("Lab          : " + rs.getString("namaLab"));
                System.out.println("Tanggal      : " + rs.getDate("tanggal"));
                System.out.println("Jam          : " + rs.getTime("jamMulai") + " - " + rs.getTime("jamAkhir"));
                System.out.println("Keperluan    : " + rs.getString("keperluan"));
                System.out.println("Status       : " + rs.getString("status"));
                System.out.println("====================================================================================================================");
            }
        }
    }

}
