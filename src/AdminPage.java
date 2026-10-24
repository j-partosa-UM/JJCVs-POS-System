import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
public class AdminPage extends JFrame {

    AdminPage() {
        
        JLabel bg_ = new JLabel();
        ImageIcon icon = new ImageIcon("src/icons/Welcome.png");
        bg_.setIcon(icon);

        JLabel h1 = new JLabel("Welcome ADMIN!");

        ImageIcon store = new ImageIcon("src/icons/store.png");
        ImageIcon exit = new ImageIcon("src/icons/exit.png");
        ImageIcon reg = new ImageIcon("src/icons/REG.png");
        ImageIcon invent = new ImageIcon("src/icons/inventory.png");
        ImageIcon stock = new ImageIcon(new ImageIcon("src/icons/stock.png").getImage().getScaledInstance(240, 150, Image.SCALE_SMOOTH));

        h1.setBounds(150, 180, 230, 150);
        h1.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 60));

        JButton inventory = new JButton();
        inventory.setBounds(220, 380, 230, 150);
        inventory.setIcon(invent);
        inventory.setBackground(new Color(0xE1B894));
        inventory.setFocusable(false);
        inventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminPage.this.dispose();
                new Inventory.InventoryPage();
            }
        });
        JButton status = new JButton();
        status.setBounds(220, 180, 230, 150);
        status.setIcon(store);
        status.setBackground(new Color(0xFF6962));
        status.setFocusable(false);
        status.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminPage.this.dispose();
                new StoreStatus.StatusPage();
            }       
        });
        JButton purchase = new JButton();
        purchase.setBounds(820, 180, 230, 150);
        purchase.setIcon(stock);
        purchase.setBackground(Color.WHITE);
        purchase.setFocusable(false);
        purchase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminPage.this.dispose();
                new Stock.StockMain();
            }
        });
        JButton register = new JButton();
        register.setBounds(520, 180, 230, 150);
        register.setIcon(reg);
        register.setBackground(new Color(0xFFB347));
        register.setFocusable(false);
        register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminPage.this.dispose();
                new Register.registerPage();
            }
        });
        JButton back = new JButton();
        back.setBounds(520, 380, 230, 150);
        back.setIcon(exit);
        back.setBackground(new Color(0xE9E2D7));
        back.setFocusable(false);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminPage.this.dispose();
                new Login.LoginPage();
            }
        });

        this.add(inventory);
        this.add(status);
        this.add(purchase);
        this.add(register);
        this.add(back);
        this.add(bg_);

        this.setTitle("Admin Page");
        this.setSize(1280,720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }
}
