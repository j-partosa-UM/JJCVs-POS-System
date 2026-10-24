import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;

public class Register{

    public static class registerPage extends JFrame{
        private String username;
        private String password;
        private String type;

        registerPage() {

            JRadioButton r_admin = new JRadioButton("Admin");
            JRadioButton r_user = new JRadioButton("User");

            JPanel p = new JPanel();
            p.setLayout(null);
            p.setBounds(0, -5, 490, 280);

            JLabel bg = new JLabel();
            JLabel l_username = new JLabel("Username");
            l_username.setBounds(50, 30, 120, 30);
            l_username.setFont(new Font("Consola", Font.BOLD, 24));
            l_username.setForeground(new Color(51,51,51));

            JLabel l_password = new JLabel("Password");
            l_password.setBounds(50, 85, 120, 30);
            l_password.setFont(new Font("Consolas", Font.BOLD, 24));
            l_password.setForeground(new Color(51,51,51));
            
            ImageIcon icon = new ImageIcon("src/icons/pic.png");
            bg.setBounds(0, -60, 720, 420);
            bg.setIcon(icon);

            JTextField t_username = new JTextField();
            t_username.setBounds(170, 30, 280, 30);
            t_username.setText(null);

            JPasswordField t_password = new JPasswordField();
            t_password.setBounds(170, 85, 280, 30);
            t_password.setText(null);
            
            r_admin.setBounds(170, 135, 138, 30);
            r_admin.setFocusable(false);
            r_admin.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                        if(r_admin.isSelected() == true) {
                        r_user.setSelected(false);
                        type = "Admin";
                        } else {
                            type = null;
                        }
                }
            });

            r_user.setBounds(312, 135, 138, 30);
            r_user.setFocusable(false);
            r_user.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                        if(r_user.isSelected() == true) {
                            r_admin.setSelected(false);
                            type = "User";
                        } else {
                            type = null;
                        }
                }
            });

            JButton reg = new JButton("Register");
            reg.setBounds(50, 180, 400, 30);
            reg.setBackground(Color.LIGHT_GRAY);
            reg.setFocusable(false);
            reg.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(t_username.getText().equals("") == false) {
                        if(String.valueOf(t_password.getPassword()).equals("") == false ) {
                            username = t_username.getText();
                            password = String.valueOf(t_password.getPassword());
                                if (type == "Admin") {
                                    new Account.Admin().register(username, password, type);
                                } else if (type == "User"){
                                    new Account.User().register(username, password, type);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Select role type", null, JOptionPane.ERROR_MESSAGE);
                                }
                        } else {
                            JOptionPane.showMessageDialog(null, "Enter password", null, JOptionPane.ERROR_MESSAGE);
                        }
                    } else{
                        JOptionPane.showMessageDialog(null, "Enter username", null, JOptionPane.ERROR_MESSAGE);
                    }
                
                }
            });

            JButton back = new JButton("Back");
            back.setBounds(50, 220, 400, 30);
            back.setBackground(Color.LIGHT_GRAY);
            back.setFocusable(false);
            back.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registerPage.this.dispose();
                    new AdminPage();
                }
            });

            p.add(l_username);
            p.add(l_password);
            p.add(t_username);
            p.add(t_password);
            p.add(r_admin);
            p.add(r_user);
            p.add(reg);
            p.add(back);
            p.add(bg);
            
            this.add(p);

            this.setLayout(null);
            this.setSize(500,300);
            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
    }
}
