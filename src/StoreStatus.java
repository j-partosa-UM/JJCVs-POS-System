import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StoreStatus {
    public static class Status {

        private Connection conn = null;
        private PreparedStatement pst = null;
        private ResultSet rs = null;
        
        private String currentDate;
        private String currentTime;
        private int totalItems;
        private double grossSales = 0;
        private double netSales = 0;

        Status() {
            this.conn = db.java_db();
        }

        public void createHistory(int orderNum, int[] quantiList, double sales, Date date) {

            DateFormat df = DateFormat.getDateInstance();
            for (int subItems : quantiList) {
                totalItems += subItems;
            }
            try {
                String sql = "INSERT into orderHistory (ordernum, total_Items, sales, date) VALUES(?, ?, ?, ?)";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, orderNum);
                pst.setInt(2, totalItems);
                pst.setDouble(3, sales);
                pst.setString(4, df.format(date));
                pst.execute();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Creating History Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (Exception e) {
                    
                }
            }
        }

        public double getGrossSales() {
            currentDate = getCurrentDate();

            try {
                String sql = "SELECT sales from orderHistory where date = '"+currentDate+"' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    grossSales += rs.getDouble("sales");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Calculating Gross Sales Error", JOptionPane.ERROR_MESSAGE);
            }
            return grossSales;
        }

        public double getNetSales() {
            try{
                String sql = "SELECT sales from orderHistory";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    netSales += rs.getDouble("sales");
                }
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Calculating Net Sales Error", JOptionPane.ERROR_MESSAGE);
            }
            return netSales;
        }

        public String getCurrentDate() {
            java.util.Date d = GregorianCalendar.getInstance().getTime();
            DateFormat df = DateFormat.getDateInstance();
            currentDate = df.format(d);
            
            return currentDate;
        }

        public String getCurrentTime() {
            java.util.Date t = GregorianCalendar.getInstance().getTime();
            DateFormat df = DateFormat.getTimeInstance();
            currentTime = df.format(t);

            return currentTime;
        }
    }

    public static class StatusPage extends JFrame {
        
        DecimalFormat df = new DecimalFormat("₱ #,###.00");
        Status s = new Status();

        JLabel asOf_ = new JLabel("As of: "+s.getCurrentDate()+" "+s.getCurrentTime());

        JLabel grossSales_ = new JLabel("Gross Sales:");
        JTextArea gross_ = new JTextArea("0.00");
        
        JLabel netSales_ = new JLabel("Net Sales: ");
        JTextArea net_ = new JTextArea("0.00");

        DefaultTableModel m = new DefaultTableModel();

        private void displayTable() {
            try {
                String sql = "SELECT * from orderHistory";
                s.pst = s.conn.prepareStatement(sql);
                s.rs = s.pst.executeQuery();

                while (s.rs.next()) {
                    m.addRow(new Object[] {
                        s.rs.getInt("ordernum"),
                        s.rs.getInt("total_Items"),
                        s.rs.getDouble("sales"),
                        s.rs.getString("date")
                    });
                }
            } catch(SQLException e) {

            }
        }
        
        StatusPage() {

            Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
            JLabel bg_ = new JLabel();
            ImageIcon bg = new ImageIcon("src/icons/i.png");
            bg_.setIcon(bg);
            bg_.setBounds(0, 0, 750, 500);

            JPanel p1 = new JPanel();
            p1.setLayout(null);
            p1.setBounds(10, 50, 350, 195);
            p1.setBorder(border);
            
            JPanel p2 = new JPanel();
            p2.setLayout(null);
            p2.setBounds(10, 255, 350, 195);
            p2.setBorder(border);

            JPanel p3 = new JPanel();
            p3.setLayout(null);
            p3.setBounds(370, 50, 355, 400);
            p3.setBackground(Color.GREEN);

            asOf_.setBounds(10, 25, 350, 20);
            asOf_.setFont(new Font("Bodoni MT", Font.PLAIN, 18));
            asOf_.setAlignmentY(JLabel.BOTTOM);

            grossSales_.setBounds(15, 10, 195, 40);
            grossSales_.setOpaque(true);
            grossSales_.setFont(new Font("Bodoni MT", Font.PLAIN, 32));
                        
            gross_.setBounds(25, 55, 300, 130);
            gross_.setOpaque(true);
            gross_.setFont(new Font("Times New Roman", Font.PLAIN, 48));            
            gross_.setText(df.format(s.getGrossSales()));
            gross_.setAlignmentY(JTextArea.TOP_ALIGNMENT);
            gross_.setEditable(false);
            gross_.setLineWrap(true);

            
            netSales_.setBounds(10, 5, 195, 40);
            netSales_.setOpaque(true);
            netSales_.setFont(new Font("Bodoni MT", Font.PLAIN, 32));

            net_.setBounds(25, 55, 300, 130);
            net_.setOpaque(true);
            net_.setFont(new Font("Times New Roman", Font.PLAIN, 48));
            net_.setText(df.format(s.getNetSales()));
            net_.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
            net_.setEditable(false);
            net_.setLineWrap(true);

            Object[] column = {"Order No.", "Total Item", "Sales", "Date"};            
            m.setColumnIdentifiers(column);
            JTable orderH_table = new JTable();
            orderH_table.setModel(m);
            orderH_table.setEnabled(false);
            JScrollPane sp = new JScrollPane(orderH_table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sp.setBounds(0, 0, 355, 400);
            displayTable();

            ImageIcon ref_icon = new ImageIcon("src/icons/r.png");
            JButton $ref = new JButton("Refresh", ref_icon);
            $ref.setBounds(370, 5, 175, 40);
            $ref.setFont(new Font("Consolas", Font.BOLD, 15));
            $ref.setFocusable(false);
            $ref.setBackground(new Color(0xD6EAF8));
            $ref.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StatusPage.this.dispose();
                    new StatusPage();
                }
            });

            ImageIcon ret_icon = new ImageIcon("src/icons/ret.png");
            JButton $ret = new JButton("Return", ret_icon);
            $ret.setBounds(550, 5, 175, 40);
            $ret.setFont(new Font("Consolas", Font.BOLD, 15));
            $ret.setFocusable(false);
            $ret.setBackground(new Color(0xD6EAF8));
            $ret.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StatusPage.this.dispose();
                    new AdminPage();
                }
            });                        

            this.setSize(750, 500);
            this.setResizable(false);
            this.setLayout(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setVisible(true);

            p1.add(grossSales_);
            p1.add(gross_);

            p2.add(netSales_);
            p2.add(net_);

            p3.add(sp);

            this.add(asOf_);
            this.add($ref);
            this.add($ret);
            this.add(p1);
            this.add(p2);
            this.add(p3);            
            this.add(bg_);
        }
    }
}
