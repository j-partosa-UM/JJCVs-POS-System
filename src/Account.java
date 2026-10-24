import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Account {
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private String username;
    private String password;
    private String type;

    Account(String username, String password, String type) {
        this.username = username;
        this.password = password;
        this.type = type;

        conn = db.java_db();
    }

    Account() {
        conn = db.java_db();
    }

    void login() {
        
    }

    void register(String username, String password, String type) {
        if (!username.equals("Username")) {
            if (!password.equals("Password")) {
                try {
                    String sql = "SELECT count(*) as total from users WHERE username =?";
        
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, username);
                    rs = pst.executeQuery();
        
                        if (rs.getInt("total") == 0) {
                            try {
                                String sql2 = "INSERT INTO users (username, password, type) VALUES (?, ?, ?)";
        
                                pst = conn.prepareStatement(sql2);
                                pst.setString(1, username);
                                pst.setString(2, password);
                                pst.setString(3, type);
                                pst.execute();
                                JOptionPane.showMessageDialog(null, "Registration successful", null, JOptionPane.INFORMATION_MESSAGE);
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Username already exists!", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                    
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        pst.close();
                        rs.close();
                    } catch (Exception e) {
                        
                    }
                }

            } else {
                JOptionPane.showMessageDialog(null, "Create another password.", null, JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Create another username.", null, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static class Admin extends Account {
        private Connection conn = null;
        private PreparedStatement pst = null;
        private ResultSet rs = null;
        Admin(String username, String password, String type) {
            super(username, password, type);
            this.conn = super.conn;
        }

        Admin() {
            super();
            this.conn = super.conn;
        }

        @Override
        void login() {            
            try {
                String sql = "SELECT password from users WHERE username =? and type =?";
                
                pst = conn.prepareStatement(sql);
                pst.setString(1, super.username);
                pst.setString(2, super.type);
                rs = pst.executeQuery();
                while (rs.next()) {
                    String valid = rs.getString("password");
                    if (super.password.equals(valid)) {
                        JOptionPane.showMessageDialog(null, "Login Successful!", null, JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showMessageDialog(null, "Welcome Admin!", null, JOptionPane.INFORMATION_MESSAGE);
                        pst.close();
                        rs.close();
                        new AdminPage();
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect username or password!", null, JOptionPane.INFORMATION_MESSAGE);
                        pst.close();
                        rs.close();
                        new Login.LoginPage();
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
            }
            

        }
    }

    public static class User extends Account {
        private Connection conn = null;
        private PreparedStatement pst = null;
        private ResultSet rs = null;
        User(String username, String password, String type) {
            super(username, password, type);
            this.conn = super.conn;
        }
        User() {
            super();
            this.conn = super.conn;
        }

        @Override
        void login() {
            try {
                String sql = "SELECT password from users WHERE username =? and type =?";
                
                pst = conn.prepareStatement(sql);
                pst.setString(1, super.username);
                pst.setString(2, super.type);
                rs = pst.executeQuery();
                while (rs.next()) {
                    String valid = rs.getString("password");    
                    if (super.password.equals(valid)) {
                        JOptionPane.showMessageDialog(null, "Login Successful!", null, JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showMessageDialog(null, "Welcome "+super.username, null, JOptionPane.INFORMATION_MESSAGE);
                        pst.close();
                        rs.close();                  
                        new POS.POSPage();
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect username or password!", null, JOptionPane.INFORMATION_MESSAGE);
                        pst.close();
                        rs.close();
                        new Login.LoginPage();
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public String getUser() {
            return super.username;
        }
    }
}
