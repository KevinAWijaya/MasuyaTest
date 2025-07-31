package salesapp;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.SwingUtilities;
import salesapp.utils.database.DBHelper;
import salesapp.view.MainMenu;

public class SalesApp {

    public static void main(String[] args) {
        // test connection
        try (Connection conn = DBHelper.getInstance().getConnection()) {
            System.out.println("Connection successful");
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
            return;
        }

        // Jalankan MainMenu saat aplikasi mulai
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
}
