import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class Login {

    public static class LoginPage extends JFrame{
        
        private Connection conn = null;
        private PreparedStatement pst = null;
        private ResultSet rs = null;
        
        private String username;
        private String password;
        private String type;
            
        LoginPage() {

            conn = db.java_db();

            JLabel h1 = new JLabel();
            JLabel h2 = new JLabel();
            JLabel h3 = new JLabel();
            JLabel sub = new JLabel();

            JPanel txtp = new JPanel();
            JPanel passp = new JPanel();

            JLabel bg1 = new JLabel();
            JLabel bg2 = new JLabel();
            JLabel bg3 = new JLabel();
            JLabel bg4 = new JLabel();
            JTextField t_username = new JTextField();
            JPasswordField t_password = new JPasswordField();
            JButton login = new JButton("LOGIN");
        
            ImageIcon bg = new ImageIcon("src/icons/b1.png");
            ImageIcon user = new ImageIcon("src/icons/user.png");
            ImageIcon pass = new ImageIcon(("src/icons/password.png"));
            ImageIcon key = new ImageIcon("src/icons/key.png");

            h1.setText("Welcome to ");
            h1.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 40));
            h1.setBounds(760, -50, 300, 300);

            h2.setText("JJCV's");
            h2.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 40));
            h2.setBounds(730, -45, 500, 400);
            h2.setForeground(new Color(0x8670F));

            h3.setText("SYSTEM");
            h3.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 40));
            h3.setBounds(895, -45, 500, 400);
            h3.setForeground(new Color(0xb2912e));

            sub.setText("Your SATISFACTION is our TOP PRIORITY");
            sub.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 12));
            sub.setForeground(Color.GRAY);
            sub.setBounds(763, 41, 500, 300);
            
            t_username.setFont(new Font("Consolas", Font.PLAIN, 25));
            t_username.setBounds(808, 255, 220, 30);
            t_username.setText("Username");
            t_username.setBorder(null);
            t_username.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent arg0) {
                    if (t_username.getText().equals("Username")) {
                        t_username.setText("");
                    } else {
                        t_username.selectAll();
                    }
                }
                public void focusLost(FocusEvent arg0) {
                    if (t_username.getText().equals("")) {
                        t_username.setText("Username");
                    }
                }
            });
            t_username.addCaretListener(new CaretListener() {
                public void caretUpdate(CaretEvent e) {
                    //checks if username and password has input. If the condition is true, the login button is enabled.
                    if((!t_username.getText().equals("") && !t_username.getText().equals("Username")) &&
                        (!String.valueOf(t_password.getPassword()).equals("") && !String.valueOf(t_password.getPassword()).equals("Password"))) {
                        login.setEnabled(true);                    
                    } else {
                        login.setEnabled(false);
                    }
                }
            });
            t_password.setFont(new Font("Consolas", Font.PLAIN, 25));
            t_password.setBounds(810, 340, 220, 30);
            t_password.setEchoChar((char) 0);
            t_password.setText("Password");
            t_password.setBorder(null);
            t_password.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                    if (String.valueOf(t_password.getPassword()).equals("Password")) {                                 
                        t_password.setEchoChar('●');                 
                        t_password.setText("");
                    } else {
                        t_password.selectAll();
                    }
                }
                public void focusLost(FocusEvent arg0) {
                    if (String.valueOf(t_password.getPassword()).equals("")) {
                        t_password.setEchoChar((char) 0);
                        t_password.setText("Password");
                    }
                }
            });
            t_password.addCaretListener(new CaretListener() {
                public void caretUpdate(CaretEvent e) {
                    //checks if username and password has input. If the condition is true, the login button is enabled.
                    if((!t_username.getText().equals("") && !t_username.getText().equals("Username")) &&
                     (!String.valueOf(t_password.getPassword()).equals("") && !String.valueOf(t_password.getPassword()).equals("Password"))) {
                        login.setEnabled(true);                    
                    } else {
                        login.setEnabled(false);
                    }
                }
            });
            txtp.setBounds(755, 246, 280, 40);
            txtp.setBackground(Color.WHITE);
            txtp.add(bg2);
            txtp.setLayout(new FlowLayout(FlowLayout.LEFT));

            login.setBounds(810, 445, 180, 40);
            login.setBackground(Color.GRAY);
            login.setFont(new Font("Consolas", Font.BOLD, 20));
            login.setFocusable(false);
            login.setEnabled(false);
            login.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {                   
                    username = t_username.getText();
                    password = String.valueOf(t_password.getPassword());
                    
                    String sql = "SELECT type from users WHERE username =?";
                    try {
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, username);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            type = rs.getString("type");
                            System.out.println("type: "+type);
                            if (type.equals("Admin")) {
                                pst.close();
                                rs.close();
                                new Account.Admin(username, password, type).login();
                                LoginPage.this.dispose();
                            } else if (type.equals("User")) {
                                pst.close();
                                rs.close();
                                new Account.User(username, password, type).login();
                                LoginPage.this.dispose();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Username does not exist", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, e1, "login error!", JOptionPane.ERROR_MESSAGE);
                    }
                        System.out.println("Login attempt occured");
                }
            });            
            passp.setBounds(755, 330, 285, 45);
            passp.setBackground(Color.WHITE);
            passp.add(bg3);
            passp.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            bg1.setIcon(bg);
            bg2.setIcon(user);
            bg3.setIcon(pass);
            bg4.setIcon(key);
            
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(1280,720);
            this.setResizable(false);
            this.setVisible(true);
            this.setLocationRelativeTo(null);            

            this.add(h1);            
            this.add(sub);
            this.add(h2);
            this.add(h3);
            this.add(txtp);            
            this.add(t_username);            
            this.add(t_password);
            this.add(passp);
            this.add(login);
            this.add(bg1);
        }
    }
}
