
import java.net.ConnectException;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/budget_app";
        String user = "root"; // kendi MySQL kullanıcı adın
        String password = "61Bera54."; // kendi şifren

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Veritabanına başarıyla bağlandı!");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n-- bitce takip emnusu ---");
                System.out.println("1. yeni islem eklendi");
                System.out.println("2. tum islemler");
                System.out.println("3. cikis");
                System.out.println("4 islem sil");
                System.out.println("seciminiz");

                int secim= scanner.nextInt();
                scanner.nextLine();
                if (secim == 1) {
                    System.out.println("islem basligi: ");
                    String title = scanner.nextLine();

                    System.out.println("tutar: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.println("gelir/gider");
                    String type = scanner.nextLine();

                    System.out.println("tarih");
                    String date = scanner.nextLine();
                    addTransaction(conn,title,amount,type,date);
                }
                else if (secim == 2) {
                    try {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM transactions");

                        System.out.println("\n--- Kayıtlar ---");
                        while (rs.next()) {
                            String title = rs.getString("title");
                            double amount = rs.getDouble("amount");
                            String type = rs.getString("type");
                            String date = rs.getString("data"); // sütun ismi 'data' idi

                            System.out.println(title + " | " + amount + " | " + type + " | " + date);
                        }
                    } catch (SQLException e) {
                        System.out.println("Hata: " + e.getMessage());
                    }

                }
                else if (secim == 3) {
                    break;
                }
                else if (secim == 4) {
                    System.out.println("silmek istedigni yaz");
                    String titleToDelete = scanner.nextLine();
                    deleteTransaction(conn,titleToDelete);
                }
                else {
                    System.out.println("tekrar dene");
                }

            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }

            }

    public static void addTransaction(Connection conn, String title, double amount, String type, String date){
        String sql = "INSERT INTO transactions (title, amount, type, data) VALUES (?, ?, ?, ?)";
try(PreparedStatement pstmt =conn.prepareStatement(sql)){
    pstmt.setString(1, title);
    pstmt.setDouble(2, amount);
    pstmt.setString(3, type);
    pstmt.setString(4, date);
    int rowsInserted = pstmt.executeUpdate();
    if(rowsInserted > 0){
        System.out.println("islem basarili");
    }

}catch (SQLException e){
    System.out.println("Hata: " + e.getMessage());
}
} public static void deleteTransaction(Connection conn, String title) {
        String sql = "DELETE FROM transactions WHERE title = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ İşlem başarıyla silindi!");
            } else {
                System.out.println("❌ Silinecek işlem bulunamadı.");
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }
}
