import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;

public class POS {

    public static class POSSystem extends Inventory.InventorySystem {
        protected Connection conn = null;
        protected PreparedStatement pst = null;
        protected ResultSet rs = null;

        private Date currentDate;
        private String dateString ;

        private ArrayList<String> foodList = new ArrayList<String>();
        private ArrayList<String> drinkList = new ArrayList<String>();
        private ArrayList<String> dessertList = new ArrayList<String>();

        private ArrayList<String> orderList = new ArrayList<String>();
        private ArrayList<Integer> quantityList = new ArrayList<Integer>();
        private ArrayList<Double> orderSubTotalList = new ArrayList<Double>();

        private double total;
        private double payment;
        private double change;
        
        private int orderNum = 1;

        POSSystem() {
            this.conn = db.java_db();
            DateFormat date_format = DateFormat.getDateInstance();            
            currentDate = new Date();
            dateString = date_format.format(currentDate);
        }        
        
        public ArrayList<String> getFoodList() {
            try {
                String sql = "SELECT product FROM inventory WHERE category =?";

                pst = conn.prepareStatement(sql);
                pst.setString(1, "Food");
                rs = pst.executeQuery();

                while (rs.next()) {
                    foodList.add(rs.getString(1));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Food inventory is empty", "Database Error!", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Food Listing Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (Exception e) {
                    
                }
            }

            return foodList;
        }
        public ArrayList<String> getDrinkList() {
            try {
                String sql = "SELECT product FROM inventory WHERE category =?";

                pst = conn.prepareStatement(sql);
                pst.setString(1, "Drink");
                rs = pst.executeQuery();
                while (rs.next()) {
                    drinkList.add(rs.getString(1));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Drink inventory is empty", "Database Error!", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Drink Listing Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (Exception e) {
                    
                }
            }
            
            return drinkList;
        }
        public ArrayList<String> getDessertList() {
            try {
                String sql = "SELECT product FROM inventory WHERE category =?";

                pst = conn.prepareStatement(sql);
                pst.setString(1, "Dessert");
                rs = pst.executeQuery();
                
                while (rs.next()) {
                    dessertList.add(rs.getString(1));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Dessert inventory is empty", "Database Error!", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Dessert Listing Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (Exception e) {
                    
                }
            }

            return dessertList;
        }

        public int addQuantity(int index) {
            int currQ = quantityList.get(index);
            int newQ = currQ + 1;
            quantityList.set(index, newQ);
            return newQ;
        }
        public int removeQuantity(int index) {
            int currrQ = quantityList.get(index);
            int newQ = currrQ - 1;
            quantityList.set(index, newQ);
            return newQ;
        }
        public void addOrder(String item) {
            orderList.add(item);
            quantityList.add(1);
        }
        public ArrayList<Double> getOrderSubTotalList() {
            for (int i = 0; i < orderList.size(); i ++) {
                String order = orderList.get(i);
                searchProduct(order);
                int q = quantityList.get(i);
                double p = getPrice();
                double subtotal = q * p;
                orderSubTotalList.add(subtotal);
            }
            return orderSubTotalList;
        }
        public double getTotal() {
            for (int i = 0; i < orderList.size(); i ++) {
                String order = orderList.get(i);
                searchProduct(order);
                int q = quantityList.get(i);
                double p = getPrice();
                double subtotal = q * p;
                total += subtotal;
            }
            return total;
        }
        public void printReceipt(String[][] table, double total, double pay, double change) throws IOException {
            
            DecimalFormat df = new DecimalFormat("0.00");
            SimpleDateFormat sdf = new SimpleDateFormat("-yy-MM-dd-");
            File file = new File("slips/Receipt" + sdf.format(currentDate) + orderNum);
            if (!file.exists()) {                
                file.createNewFile();             
                FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw);
                pw.printf("%-45s %-10s","ORDER SLIP NO.", orderNum);
                pw.printf("%-41s %-10s", "\nDate/Time: ", dateString);
                pw.println("\n----------------------------------------------------");
                pw.printf("%-20s %-10s %-10s %-10s\n\n", "Item", "Quantity", "Price", "Subtotal");
                for(int i = 0; i < table.length; i++) {
                    pw.printf("%-20s %-10s %-10s %-10s\n", table[i][0], table[i][1], table[i][2], table[i][3]);
                }
                pw.println("----------------------------------------------------");
                pw.printf("%-40s %-10s\n", "Total.....................................", df.format(total));
                pw.printf("%-40s %-10s\n", "Payment...................................", df.format(pay));
                pw.printf("%-40s %-10s\n", "Change....................................", df.format(change));
                pw.println("----------------------------------------------------");
                pw.println("\nThis Serves As Your Order Slip\nAlways Ask for an Oficial Receipt\nThank you for your Purchase");
                pw.close();
                
            } else {
                getCurrentOrderNum(); 
                /*The orderNo. must changed after clicking pay button to keep record of the products ordered and
                can be found on stock out database.*/
                printReceipt(table, total, pay, change);
            }
        }
        public int getCurrentOrderNum() {
            try {
                String sql = "SELECT date from stockOut WHERE row_num = (SELECT count() as c from stockOut)";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                String prevDate = rs.getString("date");
                if (prevDate.equals(dateString)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("-yy-MM-dd-");
                    File file = new File("slips/Receipt" + sdf.format(currentDate) + orderNum);

                    if (file.exists()) {
                        orderNum += 1;
                        pst.close();
                        rs.close();
                        getCurrentOrderNum();
                    } else {
                        return orderNum;
                    }
                } else {
                    return orderNum = 1;
                }
                return orderNum;
            } catch (SQLException e) {
                
                if (!e.getMessage().equals("ResultSet closed")) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Order No. Error!", JOptionPane.ERROR_MESSAGE);    
                }
                return orderNum;
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    public static class POSPage extends JFrame implements ActionListener{
        private POSSystem ps = new POSSystem();
        private int orderNum = ps.getCurrentOrderNum();
        private Double total;
        private double payment;
        private double change;

        private String setTotal;
        private String setChange;
        private DecimalFormat df = new DecimalFormat("#,###.00");

        private Date currentDate = ps.currentDate;
        
        private JTabbedPane tab = new JTabbedPane();
        private JPanel categ1 = new JPanel();
        private JPanel categ2 = new JPanel();
        private JPanel categ3 = new JPanel();
        private JPanel buttons = new JPanel();
        private JPanel payRemNew = new JPanel();
        private JPanel ptcc = new JPanel();
        private JPanel printp = new JPanel();
        private JTable table = new JTable();        
        private DefaultTableModel model = new DefaultTableModel();
        private JLabel bg1 = new JLabel();
        private JLabel l_orderNum = new JLabel("Order No.");
        private JLabel l_total = new JLabel("Total");
        private JLabel l_payment = new JLabel("Payment");
        private JLabel l_change = new JLabel("Change");
        private JTextField t_orderNum = new JTextField();
        private JTextField t_total = new JTextField();
        private JTextField t_payment = new JTextField();
        private JTextField t_change = new JTextField();
        private JButton one = new JButton("1");
        private JButton two = new JButton("2");
        private JButton three = new JButton("3");
        private JButton four = new JButton("4");
        private JButton five = new JButton("5");
        private JButton six = new JButton("6");
        private JButton seven = new JButton("7");
        private JButton eight = new JButton("8");
        private JButton nine = new JButton("9");
        private JButton dot = new JButton(".");
        private JButton zero = new JButton("0");
        private JButton c = new JButton("c");
        private JButton pay = new JButton("PAY");
        private JButton remove = new JButton("REMOVE");
        private JButton clear = new JButton("CLEAR");
        private JButton newCustomer = new JButton("NEW CUSTOMER");
        private JButton print = new JButton("PRINT");        
                
        private StringBuilder sb = new StringBuilder();
        private double d_val;
        private String s_val;
        POSPage() {
            
            ImageIcon bg = new ImageIcon("src/icons/i.png");
            bg1.setIcon(bg);
            bg1.setBounds(0, 0, 1280, 680);
            
        // For jtable and scrollpane
            Object[] column = {"Name", "Quantity", "Price"};
            model.setColumnIdentifiers(column);
            table.setModel(model);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int i = table.getSelectedRow();
                    if(table.isRowSelected(i)) {
                        remove.setEnabled(true);
                    } else {
                        remove.setEnabled(false);
                    }
                }
            });
            JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JScrollPane sp1 = new JScrollPane(categ1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JScrollPane sp2 = new JScrollPane(categ2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JScrollPane sp3 = new JScrollPane(categ3, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            sp.setBounds(905, 65, 320, 480);
        
        // For food tab
            ArrayList<String> foods = new ArrayList<String>();
            foods.addAll(ps.getFoodList());
            categ1.setLayout(new GridLayout(0,3,10,15));        
            for (int i = 0; i < foods.size(); i++) {                
                String name = foods.get(i);
                ps.searchProduct(name);
                String price = df.format(ps.getPrice());
                int stock = ps.getStock();
                byte[] byteIcon = ps.getImage();                
                JButton b = new JButton(); 
                try {
                    //Enclosed in try-catch to ignore instances of null ImageIcon errors.
                    ImageIcon icon = new ImageIcon(new ImageIcon(byteIcon).getImage().getScaledInstance(120, 140, Image.SCALE_SMOOTH));                
                    b.setIcon(icon);
                } catch (Exception e) {

                }
                b.setPreferredSize(new Dimension(50, 220));
                b.setFocusable(false);
                b.setIconTextGap(15);
                b.setText(name+" ₱ "+price);
                b.setHorizontalTextPosition(JButton.CENTER);
                b.setVerticalTextPosition(JButton.BOTTOM);
                b.setBackground(new Color(0xD6EAF8));
                b.setName(name);
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {                     
                        if (stock > 0) {
                            if (ps.orderList.contains(name)) {
                                int r = ps.orderList.indexOf(name);
                                int newQuantity = ps.addQuantity(r);
                                model.setValueAt(newQuantity, r, 1);
                            } else {                            
                                ps.addOrder(name);
                                int i = ps.orderList.indexOf(name);
                                model.addRow(new Object[] {
                                    name,
                                    ps.quantityList.get(i),
                                    price
                                });
                            }
                            setTotal = df.format(ps.getTotal());        
                            t_total.setText(setTotal);
                            ps.total = 0;
                            System.out.println("Order List: " + ps.orderList);
                            System.out.println("Quantity List: " + ps.quantityList + "\n");
                        } else {
                            JOptionPane.showMessageDialog(null, "Out of stock!", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                    }                    
                });
                categ1.add(name, b);
            }            

            //For drink tab            
            ArrayList<String> drinks = new ArrayList<String>();
            drinks.addAll(ps.getDrinkList());
            categ2.setLayout(new GridLayout(0,3,10,15));                
            for (int i = 0; i < drinks.size(); i++) {                
                String name = drinks.get(i);
                ps.searchProduct(name);                
                String price = df.format(ps.getPrice());
                int stock = ps.getStock();
                byte[] byteIcon = ps.getImage();                
                JButton b = new JButton();
                try {
                    //Enclosed in try-catch to prevent ignore of null ImageIcon errors.
                    ImageIcon icon = new ImageIcon(new ImageIcon(byteIcon).getImage().getScaledInstance(120, 140, Image.SCALE_SMOOTH));                
                    b.setIcon(icon);
                    } catch (Exception e) {
                        
                    }
                b.setPreferredSize(new Dimension(150, 220));
                b.setFocusable(false);
                b.setIconTextGap(15);
                b.setText(name+" ₱ "+price);
                b.setHorizontalTextPosition(JButton.CENTER);
                b.setVerticalTextPosition(JButton.BOTTOM);
                b.setBackground(new Color(0xD6EAF8));
                b.setName(name);
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (stock > 0) {
                            if (ps.orderList.contains(name)) {
                                int r = ps.orderList.indexOf(name);
                                int newQuantity = ps.addQuantity(r);
                                model.setValueAt(newQuantity, r, 1);
                            } else {                            
                                ps.addOrder(name);
                                int i = ps.orderList.indexOf(name);
                                model.addRow(new Object[] {
                                    name,
                                    ps.quantityList.get(i),
                                    price
                                });
                            }
                            setTotal = df.format(ps.getTotal());
                            t_total.setText(setTotal);
                            ps.total = 0;
                            System.out.println("Order List: " + ps.orderList);
                            System.out.println("Quantity List: " + ps.quantityList + "\n");
                        } else {
                            JOptionPane.showMessageDialog(null, "Out of stock!", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });
                categ2.add(name, b);
            }
            
            //For dessert tab
            ArrayList<String> desserts = new ArrayList<String>();
            desserts.addAll(ps.getDessertList());
            categ3.setLayout(new GridLayout(0,3,10,15));

                for (int i = 0; i < desserts.size(); i++) {
                String name = desserts.get(i);
                ps.searchProduct(name);                
                String price = df.format(ps.getPrice());
                int stock = ps.getStock();
                byte[] byteIcon = ps.getImage();
                JButton b = new JButton();
                try {
                    //Enclosed in try-catch to ignore null ImageIcon error.
                    ImageIcon icon = new ImageIcon(new ImageIcon(byteIcon).getImage().getScaledInstance(120, 140, Image.SCALE_SMOOTH));                
                    b.setIcon(icon);
                    } catch (Exception e) {
                        
                    }
                b.setPreferredSize(new Dimension(150, 220));
                b.setFocusable(false);
                b.setIconTextGap(15);
                b.setText(name+" ₱ "+price);
                b.setHorizontalTextPosition(JButton.CENTER);
                b.setVerticalTextPosition(JButton.BOTTOM);
                b.setBackground(new Color(0xD6EAF8));
                b.setName(name);
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (stock > 0) {
                            if (ps.orderList.contains(name)) {
                                int r = ps.orderList.indexOf(name);
                                int newQuantity = ps.addQuantity(r);
                                model.setValueAt(newQuantity, r, 1);
                            } else {                            
                                ps.addOrder(name);
                                int i = ps.orderList.indexOf(name);
                                model.addRow(new Object[] {
                                    name,
                                    ps.quantityList.get(i),
                                    price
                                });
                            }
                            setTotal = df.format(ps.getTotal());  
                            t_total.setText(setTotal);
                            ps.total = 0;
                            System.out.println("Order List: " + ps.orderList);
                            System.out.println("Quantity List: " + ps.quantityList + "\n");
                        } else {
                            JOptionPane.showMessageDialog(null, "Out of stock!", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });
                categ3.add(name, b);
            }            
            
            tab.add("FOOD", sp1);
            tab.add("DRINK", sp2);
            tab.add("DESSERT", sp3);
            tab.setBounds(60, 45, 505, 620);
            tab.setVisible(true);
            tab.setFocusable(false);

            //numpad
            buttons.setBounds(585, 290, 300, 300);
            buttons.setBorder(new BevelBorder(BevelBorder.LOWERED));
            buttons.setBackground(new Color(0xd7e3fc));
            buttons.setLayout(null);
            seven.setBounds(20, 20, 80, 50);
            seven.setBackground(new Color(0xD6EAF8));
            seven.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            seven.setFocusable(false);
            seven.addActionListener(this);
            eight.setBounds(110, 20, 80, 50);
            eight.setBackground(new Color(0xD6EAF8));
            eight.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            eight.setFocusable(false);
            eight.addActionListener(this);
            nine.setBounds(200, 20, 80, 50);
            nine.setBackground(new Color(0xD6EAF8));
            nine.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            nine.setFocusable(false);
            nine.addActionListener(this);
            four.setBounds(20, 90, 80, 50);
            four.setBackground(new Color(0xD6EAF8));
            four.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            four.setFocusable(false);
            four.addActionListener(this);
            five.setBounds(110, 90, 80, 50);
            five.setBackground(new Color(0xD6EAF8));
            five.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            five.setFocusable(false);
            five.addActionListener(this);
            six.setBounds(200, 90, 80, 50);
            six.setBackground(new Color(0xD6EAF8));
            six.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            six.setFocusable(false);
            six.addActionListener(this);
            one.setBounds(20, 160, 80, 50);
            one.setBackground(new Color(0xD6EAF8));
            one.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            one.setFocusable(false);
            one.addActionListener(this);
            two.setBounds(110, 160, 80, 50);
            two.setBackground(new Color(0xD6EAF8));
            two.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            two.setFocusable(false);
            two.addActionListener(this);
            three.setBounds(200, 160, 80, 50);
            three.setBackground(new Color(0xD6EAF8));
            three.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            three.setFocusable(false);
            three.addActionListener(this);
            zero.setBounds(20, 230, 80, 50);
            zero.setBackground(new Color(0xD6EAF8));
            zero.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            zero.setFocusable(false);
            zero.addActionListener(this);
            dot.setBounds(110, 230, 80, 50);
            dot.setBackground(new Color(0xD6EAF8));
            dot.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            seven.setFocusable(false);
            dot.addActionListener(this);
            c.setBounds(200, 230, 80, 50);
            c.setBackground(Color.WHITE);
            c.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 25));
            c.setFocusable(false);
            c.addActionListener(this);

            //buttons - pay, remove, new
            payRemNew.setBounds(585, 597, 640, 70);
            payRemNew.setBorder(new BevelBorder(BevelBorder.LOWERED));
            payRemNew.setBackground(new Color(0xF5B7B1));
            payRemNew.setLayout(null);
            pay.setBounds(9, 15, 145, 40);
            pay.setBackground(new Color(0xD6EAF8));
            pay.setFont(new Font("Orange Fizz", Font.BOLD, 15));
            pay.setFocusable(false);
            pay.setEnabled(false);
            pay.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    pay.setBackground(new Color(0x66BB6A));
                }
                public void mouseExited(MouseEvent e) {
                    pay.setBackground(new Color(0xD6EAF8));
                }
            });
            pay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pay.setBackground(new Color(0x66BB6A));

                }
            });
            pay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        ps.total = Double.parseDouble(t_total.getText());
                        ps.payment = Double.parseDouble(t_payment.getText());
                        total = ps.total;
                        payment = ps.payment;

                        ps.change = total - payment;                        
                        change = ps.change;
                        if (change <= 0) {                            
                            setChange = df.format(change);
                            t_change.setText(setChange);
                            
                            ArrayList<Double> subtotal = new ArrayList<Double>(ps.getOrderSubTotalList());
                            for (int i = 0; i < ps.orderList.size(); i++) {
                                int stockOut = ps.quantityList.get(i);
                                String name = ps.orderList.get(i);
                                Double total = subtotal.get(i);
                                new Product.stockOut(name, stockOut, total, currentDate);
                            }
                            int[] q_list = new int[ps.quantityList.size()];
                            for (int i = 0; i < q_list.length; i++) {
                                q_list[i] = ps.quantityList.get(i); //Converting ArrayList into Array using for loop.
                            }
                            new StoreStatus.Status().createHistory(orderNum, q_list, total, currentDate);
                            JOptionPane.showMessageDialog(null, "Purchase succeessful", null, JOptionPane.INFORMATION_MESSAGE);
                            t_payment.setEditable(false);
                            clear.setEnabled(false);
                            pay.setEnabled(false);
                            remove.setEnabled(false);
                            print.setEnabled(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Insufficient payment!", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(null, "Invalid input", "Purchase Error!", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e2) {
                        JOptionPane.showMessageDialog(null, e2.getMessage(), "Purchase Error!", JOptionPane.ERROR_MESSAGE);
                    } finally {

                    }
                }
            });
            remove.setBounds(163, 15, 145, 40);
            remove.setBackground(new Color(0xD6EAF8));
            remove.setFont(new Font("Orange Fizz", Font.BOLD, 15));
            remove.setFocusable(false);
            remove.setEnabled(false);
            remove.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    remove.setBackground(new Color(0xD32F2F));
                }
                public void mouseExited(MouseEvent e) {
                    remove.setBackground(new Color(0xD6EAF8));
                }
            });
            remove.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int r = table.getSelectedRow();
                    if (table.isRowSelected(r)) {                    
                            int prevQuantity = ps.quantityList.get(r);
                            int newQuantity = ps.removeQuantity(r);
                            if (prevQuantity > 1) {                        
                                model.setValueAt(newQuantity, r, 1);
                            } else if (newQuantity == 0) {
                                model.removeRow(r);
                                ps.orderList.remove(r);
                                ps.quantityList.remove(r);
                            }                        
                        ps.total = 0;
                        setTotal = df.format(ps.getTotal());
                        t_total.setText(setTotal);
                        System.out.println(ps.getTotal());
                        System.out.println("Order List: " + ps.orderList);
                        System.out.println("Quantity List: " +ps.quantityList + "\n");
                    }
                    if (table.getRowCount() == 0) {
                        remove.setEnabled(false);
                    }
                }
            });
            clear.setBounds(317, 15, 145, 40);
            clear.setBackground(new Color(0xD6EAF8));
            clear.setFont(new Font("Orange Fizz", Font.BOLD, 15));
            clear.setFocusable(false);
            clear.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    clear.setBackground(new Color(0xFFF176));
                }
                public void mouseExited(MouseEvent e) {
                    clear.setBackground(new Color(0xD6EAF8));
                }
            });
            clear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (newCustomer.isEnabled()){
                        orderNum = ps.getCurrentOrderNum();
                        t_orderNum.setText(String.valueOf(orderNum));
                        t_payment.setEditable(true);
                        newCustomer.setEnabled(false);
                        remove.setEnabled(true);
                        dot.setEnabled(true);
                        
                        pay.setEnabled(false);
                        ps.total = 0;
                        t_total.setText("");
                        t_payment.setText("");
                        t_change.setText("");
                        model.setRowCount(0);
                        ps.orderList.clear();
                        ps.quantityList.clear();
                        sb.setLength(0);
                        d_val = 0;
                    } else {
                        pay.setEnabled(false);
                        ps.total = 0;
                        t_total.setText("");
                        t_payment.setText("");
                        t_change.setText("");
                        model.setRowCount(0);
                        remove.setEnabled(false);
                        ps.orderList.clear();
                        ps.quantityList.clear();
                        sb.setLength(0);
                        d_val = 0;
                    }
                }
            });
            newCustomer.setBounds(471, 15, 160, 40);
            newCustomer.setBackground(new Color(0xD6EAF8));
            newCustomer.setFont(new Font("Orange Fizz", Font.BOLD, 15));
            newCustomer.setFocusable(false);
            newCustomer.setEnabled(false);
            newCustomer.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    newCustomer.setBackground(new Color(0x81D4FA));
                }
                public void mouseExited(MouseEvent e) {
                    newCustomer.setBackground(new Color(0xD6EAF8));
                }
            });
            newCustomer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    POSPage.this.dispose();
                    new POSPage();
                }
            });

            //buttons - print
            printp.setBounds(905, 545, 320, 50);
            printp.setLayout(null);
            print.setBounds(30, 5, 260, 35);
            print.setBackground(Color.WHITE);
            print.setFocusable(false);
            print.setEnabled(false);
            print.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> order_l = new ArrayList<String>(ps.orderList);
                    ArrayList<Integer> quantity_l = new ArrayList<Integer>(ps.quantityList);
                    ArrayList<Double> subtotal_l = new ArrayList<Double>(ps.getOrderSubTotalList());
                    String[][] receiptList = new String[order_l.size()][4];

                    for(int i = 0; i < receiptList.length; i++) {
                        receiptList[i][0] = order_l.get(i);
                        receiptList[i][1] = String.valueOf(quantity_l.get(i));
                        ps.searchProduct(order_l.get(i));
                        receiptList[i][2] = df.format(ps.getPrice());
                        receiptList[i][3] = df.format(subtotal_l.get(i));
                    }
                    
                    try {
                        ps.printReceipt(receiptList, total, payment, change);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, e1, "Print Error!", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        print.setEnabled(false);
                        clear.setEnabled(true);
                        newCustomer.setEnabled(true);
                    }
                }
            });
            printp.add(print);

            //LOGOUT BUTTON
            JButton $logout = new JButton("LOGOUT");
            $logout.setBounds(1075,15, 150, 40);
            $logout.setBackground(new Color(0xD6EAF8));
            $logout.setFocusable(false);
            $logout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    POSPage.this.dispose();
                    new Login.LoginPage();
                }
            });

            ptcc.setBounds(575, 65, 325, 200);
            ptcc.setBackground(new Color(0xF6DDCC));
            ptcc.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(128, 128, 128), Color.GRAY));            
            
            t_orderNum = new JTextField();
            t_orderNum.setText(String.valueOf(orderNum));
            t_orderNum.setBounds(715, 95, 170, 30);
            t_orderNum.setBackground(Color.WHITE);
            t_orderNum.setFont(new Font("Consolas", Font.PLAIN, 15));
            t_orderNum.setHorizontalAlignment(JTextField.CENTER);
            t_orderNum.setEditable(false);

            t_total.setBounds(715, 135, 170, 30);
            t_total.setFont(new Font("Consolas", Font.PLAIN, 15));
            t_total.setBackground(Color.WHITE);
            t_total.setHorizontalAlignment(JTextField.CENTER);
            t_total.setEditable(false);
            t_total.addCaretListener(new CaretListener() {

                @Override
                public void caretUpdate(CaretEvent e) {
                    //The pay button is enabled IF the textfields are not "blank" and has not set to ".00", and t_payment is editable.
                    if ((!t_total.getText().equals("") && !t_total.getText().equals(".00")) && (!t_payment.getText().equals("")
                     && !t_payment.getText().equals(".00") && !t_payment.getText().equals("Enter Total Cash")) && t_payment.isEditable()){
                        pay.setEnabled(true);
                    } else {
                        pay.setEnabled(false);
                    }
                }
                
            });

            t_payment.setBounds(715, 175, 170, 30);
            t_payment.setBackground(Color.WHITE);
            t_payment.setFont(new Font("Consolas", Font.PLAIN, 15));
            t_payment.setHorizontalAlignment(JTextField.CENTER);
            t_payment.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (t_payment.getText().equals("Enter Total Cash")) {
                        t_payment.setText("");
                    }
                }
                public void focusLost(FocusEvent e) {
                    if (t_payment.getText().equals("")) {
                        t_payment.setText("Enter Total Cash");
                    }
                }
            });
            t_payment.addCaretListener(new CaretListener() {

                @Override
                public void caretUpdate(CaretEvent e) {
                    //The pay button is enabled IF the textfields are not "blank" and has set to ".00", and t_payment is editable.
                    if ((!t_total.getText().equals("") && !t_total.getText().equals(".00")) && (!t_payment.getText().equals("")
                     && !t_payment.getText().equals(".00") && !t_payment.getText().equals("Enter Total Cash")) && t_payment.isEditable()){
                        pay.setEnabled(true);
                    } else {
                        pay.setEnabled(false);
                    }
                }
                
            });
            t_change.setBounds(715, 215, 170, 30);
            t_change.setFont(new Font("Consolas", Font.PLAIN, 15));
            t_change.setBackground(Color.WHITE);
            t_change.setHorizontalAlignment(JTextField.CENTER);
            t_change.setEditable(false);

            l_orderNum.setBounds(580, -10, 200, 200);
            l_orderNum.setBounds(605, 98, 170, 30);
            l_orderNum.setFont(new Font("Consolas", Font.BOLD, 20));
            l_total.setBounds(605, 138, 170, 30);
            l_total.setFont(new Font("Consolas", Font.BOLD, 20));
            l_payment.setBounds(605, 178, 170, 30);
            l_payment.setFont(new Font("Consolas", Font.BOLD, 20));
            l_change.setBounds(605, 218, 170, 30);
            l_change.setFont(new Font("Consolas", Font.BOLD, 20));
            
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setSize(1296,720);
            this.setLayout(null);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setVisible(true);

            buttons.add(seven);
            buttons.add(eight);
            buttons.add(nine);
            buttons.add(four);
            buttons.add(five);
            buttons.add(six);
            buttons.add(one);
            buttons.add(two);
            buttons.add(three);
            buttons.add(zero);
            buttons.add(dot);
            buttons.add(c);

            payRemNew.add(pay);
            payRemNew.add(clear);
            payRemNew.add(remove);
            payRemNew.add(newCustomer);
            
            this.add(t_orderNum);
            this.add(l_orderNum);
            this.add(t_total);
            this.add(l_total);
            this.add(t_payment);
            this.add(l_payment);
            this.add(t_change);
            this.add(l_change);
            this.add($logout);
            this.add(sp);
            this.add(payRemNew);
            this.add(printp);
            this.add(ptcc);
            this.add(buttons);
            this.add(tab);
            this.add(bg1);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //StringBuilder is a modifiable String and can collect numbers without adding them.
            /*DecimalFormat formats the StringBuilder containing numbers to have 0.00 decimal places
            with #,### grouping size*/
            if (e.getSource() == zero){        
                sb.append("0");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To remove preoccuring zeros "before" entering a number.    
                if ((sb.indexOf("0") != 0 || sb.lastIndexOf("0") != 1) == false) {
                    sb.delete(0, 1);
                }                
                //To limit the number into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == one){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("1");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == two){
                //To remove precurrent zeros after entering a number. 
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("2");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
        }
            if (e.getSource() == three){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("3");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == four){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("4");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == five){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("5");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == six){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("6");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == seven){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("7");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == eight){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("8");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == nine){
                //To remove precurrent zeros after entering a number.
                if(sb.indexOf("0") == 0) {
                    sb.deleteCharAt(0);
                }
                sb.append("9");
                d_val = Double.parseDouble(sb.toString());
                s_val = df.format(d_val);
                t_payment.setText(s_val);
                //To limit the numbers into two decimal places.
                if (sb.indexOf(".")+2 < sb.length()-1 && sb.indexOf(".") != -1) {
                    sb.deleteCharAt(sb.length()-1);
                }
            }
            if (e.getSource() == dot){
                if (sb.isEmpty()) {
                    sb.append("0.");
                    d_val = Double.parseDouble(sb.toString());
                    s_val = df.format(d_val);
                    t_payment.setText(s_val);
                    dot.setEnabled(false);//To disable decimal point button after clicking once.
                } else {
                    sb.append(".");
                    d_val = Double.parseDouble(sb.toString());
                    s_val = df.format(d_val);
                    t_payment.setText(s_val);
                    dot.setEnabled(false);//To disable decimal point button after clicking once.
                }
            }
            if (e.getSource() == c){
                sb.setLength(0);
                d_val = 0;
                t_payment.setText("");
                dot.setEnabled(true);
            }
            System.out.println(sb.toString()); //display stringbuilder
        }        
    }
}
