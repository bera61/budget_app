import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartPanel;

public class Main {
    public static void main(String[] args) {
        // Veritabanı bağlantısı
        String url = "jdbc:mysql://localhost:3306/budget_app";
        String user = "root";
        String password = "61Bera54.";

        // JFrame başlatılıyor
        JFrame frame = new JFrame("Bütçe Takip Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // JPanel ve layout düzeni
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Bağlantı Butonu
        JButton connectButton = new JButton("Bağlantıyı Kur");
        connectButton.setBounds(10, 10, 150, 25);
        panel.add(connectButton);

        // İşlem Başlığı
        JLabel titleLabel = new JLabel("Başlık:");
        titleLabel.setBounds(10, 50, 80, 25);
        panel.add(titleLabel);

        JTextField titleText = new JTextField();
        titleText.setBounds(100, 50, 160, 25);
        panel.add(titleText);

        // İşlem Tutarı
        JLabel amountLabel = new JLabel("Tutar:");
        amountLabel.setBounds(10, 90, 80, 25);
        panel.add(amountLabel);

        JTextField amountText = new JTextField();
        amountText.setBounds(100, 90, 160, 25);
        panel.add(amountText);

        // Gelir/Gider Seçimi
        JLabel typeLabel = new JLabel("Gelir/Gider:");
        typeLabel.setBounds(10, 130, 80, 25);
        panel.add(typeLabel);

        JComboBox<String> typeComboBox = new JComboBox<>(new String[] {"Gelir", "Gider"});
        typeComboBox.setBounds(100, 130, 160, 25);
        panel.add(typeComboBox);

        // Tarih
        JLabel dateLabel = new JLabel("Tarih:");
        dateLabel.setBounds(10, 170, 80, 25);
        panel.add(dateLabel);

        JTextField dateText = new JTextField();
        dateText.setBounds(100, 170, 160, 25);
        panel.add(dateText);

        // Veritabanına Ekleme Butonu
        JButton addButton = new JButton("İşlem Ekle");
        addButton.setBounds(10, 210, 150, 25);
        panel.add(addButton);

        // Veritabanını Listeleme Butonu
        JButton viewButton = new JButton("İşlemleri Görüntüle");
        viewButton.setBounds(170, 210, 150, 25);
        panel.add(viewButton);

        // Grafik Gösterme Butonu
        JButton chartButton = new JButton("Grafiği Göster");
        chartButton.setBounds(330, 210, 150, 25);
        panel.add(chartButton);

        // İşlem Silme Butonu
        JButton deleteButton = new JButton("İşlem Sil");
        deleteButton.setBounds(490, 210, 100, 25);
        panel.add(deleteButton);

        // Connect Butonuna Tıklanırsa Veritabanı Bağlantısı Yapılır
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_app", "root", "61Bera54.")) {
                    System.out.println("✅ Veritabanına bağlantı kuruldu.");
                } catch (SQLException ex) {
                    System.out.println("Bağlantı hatası: " + ex.getMessage());
                }
            }
        });

        // "İşlem Ekle" Butonuna Tıklanırsa
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = titleText.getText();
                double amount = Double.parseDouble(amountText.getText());
                String type = (String) typeComboBox.getSelectedItem();
                String date = dateText.getText();

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_app", "root", "61Bera54.")) {
                    addTransaction(conn, title, amount, type, date);
                } catch (SQLException ex) {
                    System.out.println("Hata: " + ex.getMessage());
                }
            }
        });

        // "İşlemleri Görüntüle" Butonuna Tıklanırsa
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_app", "root", "61Bera54.")) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM transactions");

                    System.out.println("\n--- Kayıtlar ---");
                    while (rs.next()) {
                        String title = rs.getString("title");
                        double amount = rs.getDouble("amount");
                        String type = rs.getString("type");
                        String date = rs.getString("data");

                        System.out.println(title + " | " + amount + " | " + type + " | " + date);
                    }
                } catch (SQLException ex) {
                    System.out.println("Hata: " + ex.getMessage());
                }
            }
        });

        // "Grafiği Göster" Butonuna Tıklanırsa
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_app", "root", "61Bera54.")) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM transactions");

                    // Sonuçları alıp bir JTable'e eklemek için model hazırlıyoruz
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Sütun adlarını almak
                    String[] columnNames = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames[i - 1] = metaData.getColumnName(i);  // Sütun adlarını doğru şekilde alıyoruz
                    }

                    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

                    // Verileri alıp tabloya ekliyoruz
                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = rs.getObject(i);  // Satır verilerini doğru şekilde alıyoruz
                        }
                        model.addRow(row);
                    }

                    // Tabloyu panel üzerine ekliyoruz
                    JTable table = new JTable(model);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setBounds(10, 250, 560, 100);  // Tabloyu uygun bir alana yerleştiriyoruz
                    panel.add(scrollPane);

                    // Paneli güncelleyip tabloyu gösteriyoruz
                    panel.revalidate();
                    panel.repaint();
                } catch (SQLException ex) {
                    System.out.println("Hata: " + ex.getMessage());
                }
            }
        });

        // "İşlem Sil" Butonuna Tıklanırsa
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog("Silmek istediğiniz işlemin başlığını girin:");
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_app", "root", "61Bera54.")) {
                    deleteTransaction(conn, title);
                } catch (SQLException ex) {
                    System.out.println("Hata: " + ex.getMessage());
                }
            }
        });
    }

    // İşlem Ekleme Fonksiyonu
    public static void addTransaction(Connection conn, String title, double amount, String type, String date) {
        String sql = "INSERT INTO transactions (title, amount, type, data) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, type);
            pstmt.setString(4, date);
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("İşlem başarılı");
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    // İşlem Silme Fonksiyonu
    public static void deleteTransaction(Connection conn, String title) {
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

    public static void showChart(Connection conn) {
        String sql = "SELECT data, SUM(amount) as total FROM transactions GROUP BY data ORDER BY data";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            // Verileri döngüyle alıp dataset'e ekliyoruz
            while (rs.next()) {
                String date = rs.getString("data");
                double total = rs.getDouble("total");
                dataset.addValue(total, "Toplam", date);
            }

            // Grafik oluşturma
            JFreeChart chart = ChartFactory.createBarChart(
                    "Günlük Gelir/Gider",  // Başlık
                    "Tarih",               // X ekseni etiketi
                    "Toplam Tutar",        // Y ekseni etiketi
                    dataset,               // Veriler
                    PlotOrientation.VERTICAL, // Grafik tipi
                    true,                  // Legend (açıklamalar)
                    true,                  // Tooltips (yönlendirmeler)
                    false                  // URL destekleme
            );

            // Grafiği gösterme
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            javax.swing.JFrame frame = new javax.swing.JFrame();
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(chartPanel);
            frame.pack();
            frame.setVisible(true);

        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }
}
