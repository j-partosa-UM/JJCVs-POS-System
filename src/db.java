
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

/**
 * db
 */
public class db {

    static Connection conn = null;
    public static Connection java_db() {
        
        try {

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:src/projdatabasev2.0.sqlite");
            return conn;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error!", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}