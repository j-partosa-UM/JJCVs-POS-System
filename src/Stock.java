import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import com.toedter.calendar.JDateChooser;

public class Stock {
    
  public static class StockMain extends JFrame {
      
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private JPanel panel = new JPanel();
    private JPanel stockInpanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JPanel stockOutpanel = new JPanel();

    private DefaultTableModel model = new DefaultTableModel();
    private DefaultTableModel stockInModel = new DefaultTableModel();
    private DefaultTableModel stockOutModel = new DefaultTableModel();

    private static JLabel background;
    private JLabel title;
    private JButton admin;
    private JButton add;
    private JButton ref;
    private JButton rem;

    
    private void prodTable() {
      try {
        String sql = "SELECT id,product,image,quantity FROM inventory";
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        while (rs.next()) {
          String id = rs.getString("id");
          String name = rs.getString("product");
          byte[] image = rs.getBytes("image");
          int quantity = rs.getInt("quantity");
          String warning;

          JLabel img = new JLabel();

          try {
            ImageIcon imageicon = new ImageIcon(image);
            Image icon = imageicon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(icon));
          } catch (Exception e) {

          }

          if ( quantity > 30) {
            warning = "Good";
          } else {
            warning = "Add Stock!";
          }

          model.addRow(new Object[] {
            id,
            name,
            img,
            quantity,
            warning

          });
        }
        
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Product Table Error!", JOptionPane.ERROR_MESSAGE);
      }
    }

    class myTableCellRenderer implements TableCellRenderer {

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        
            TableColumn tc = table.getColumn("Image");
            tc.setMinWidth(120);
            tc.setMaxWidth(120);
            table.setRowHeight(120);
            table.setRowMargin(5);
        return (Component) value;
      }
      
    }
    
    private void stockInTable() {
      try {
        String sql = "SELECT * FROM stockIn";
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        while (rs.next()) {
          String id = rs.getString("id");
          String date = rs.getString("date");
          String name = rs.getString("name");
          int quantity = rs.getInt("quantity");

          stockInModel.addRow(new Object[] {
            id,
            date,
            name,
            quantity
          });
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Stock In Table Error!", JOptionPane.ERROR_MESSAGE);
      } finally {
        try {
          pst.close();
          rs.close();
        } catch (Exception e) {

        }
      }
    }
    
    private void stockOutTable() {
      try {
        String sql = "SELECT * FROM stockOut";
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        while (rs.next()) {
          String date = rs.getString("date");
          String name = rs.getString("name");
          int quantity = rs.getInt("quantity");

          stockOutModel.addRow(new Object[] {
            date,
            name,
            quantity,
          });
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Stock Out Table Error!", JOptionPane.ERROR_MESSAGE);
      } finally {
        try {
          pst.close();
          rs.close();
        } catch (Exception e) {

        }
      }
    }

    public StockMain() {
        conn = db.java_db();

        ImageIcon bg = new ImageIcon("JJCV's System v2.0/src/icons/i.png");
        background = new JLabel();
        background.setIcon(bg);
        background.setBounds(-15,0,1280,680);

        title = new JLabel("Stock In and Stock Out");
        title.setBounds(375, 0, 500, 80);
        title.setFont(new Font("Cooper Black" , Font.BOLD,40));
    
        panel.setBounds(630, 80 , 590 , 455);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panel.setBackground(new Color(0xd7e3fc));
        panel.setLayout(null);
    
        stockInpanel.setBounds(25, 80 , 590 , 270);
        stockInpanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        stockInpanel.setBackground(new Color(0xFCF3CF));
        stockInpanel.setLayout(null);
    
        buttonPanel.setBounds(630, 550 , 590 ,80);
        buttonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        buttonPanel.setBackground(new Color(0xBFC9CA));
        buttonPanel.setLayout(null);
    
        stockOutpanel.setBounds(25, 360 , 590 ,270);
        stockOutpanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        stockOutpanel.setBackground(new Color(0xD5F5E3));
        stockOutpanel.setLayout(null);
    
        JLabel StockIn = new JLabel();
        StockIn.setText("Purchase Report");
        StockIn.setBounds(170, -65, 300 , 200);
        StockIn.setFont(new Font ("Consolas" , Font.BOLD , 30));
    
        JLabel StockOut = new JLabel();
        StockOut.setText("Purchase Return");
        StockOut.setBounds(170, -65, 300 , 200);
        StockOut.setFont(new Font ("Consolas" , Font.BOLD , 30));
    
        JLabel Total = new JLabel();
        Total.setText("Total Income : ");
        Total.setBounds(80, 420, 300 , 200);
        Total.setFont(new Font ("Consolas" , Font.BOLD , 30));

        //STOCK IN TABLE
        Object[] column1 = {"ID", "Date", "Product Name", "Quantity"};
        stockInModel.setColumnIdentifiers(column1);
        JTable table1 = new JTable();
        table1.setModel(stockInModel);
        JScrollPane sp1 = new JScrollPane(table1);
        stockInTable();
        table1.addMouseListener(new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            int i = table1.getSelectedRow();
            if(table1.isRowSelected(i)) {
              rem.setEnabled(true);
            } else {
              rem.setEnabled(false);
            }
          }
        });

        //STOCK OUT TABLE
        Object[] column2 = {"Date", "Product Name", "Quantity"};
        stockOutModel.setColumnIdentifiers(column2);
        JTable table2 = new JTable();
        table2.setModel(stockOutModel);
        JScrollPane sp2 = new JScrollPane(table2);
        table2.setEnabled(false);
        stockOutTable();

        //PRODUCT TABLE
        Object[] column3 = {"Inventory", "Product Name", "Image", "Quantity", "Procurement Suggestion"};
        model.setColumnIdentifiers(column3);
        JTable table3 = new JTable();
        table3.setModel(model);
        JScrollPane sp3 = new JScrollPane(table3);
        table3.getColumn("Image").setCellRenderer(new myTableCellRenderer());
        table3.setEnabled(false);
        prodTable();

        //BUTTONS
        admin = new JButton("Return");
        admin.setBounds(450, 20, 125, 40);
        admin.setFocusable(false);
        admin.setBackground(new Color(0xD6EAF8));
        admin.setFont(new Font("Consolas", Font.PLAIN, 20));
        admin.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            StockMain.this.dispose();
            new AdminPage();
          }

        });
        
        add = new JButton ("Add Stock");
        add.setBounds(15, 20, 155, 40);
        add.setFocusable(false);
        add.setBackground(Color.GRAY);
        add.setFont(new Font("Consolas", Font.PLAIN, 20));
        add.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            new Stock.StockInFrame();
          }
        });
        ref = new JButton("Refresh");
        ref.setBounds(180, 20, 125, 40);
        ref.setFocusable(false);
        ref.setBackground(new Color(0xD6EAF8));
        ref.setFont(new Font("Consolas", Font.PLAIN, 20));
        ref.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            StockMain.this.dispose();
            model.setRowCount(0);
            stockInModel.setRowCount(0);
            stockOutModel.setRowCount(0);
            new StockMain();
          }
        });

        rem = new JButton("Remove");
        rem.setBounds(315, 20, 125, 40);
        rem.setFocusable(false);
        rem.setBackground(new Color(0xD6EAF8));
        rem.setFont(new Font("Consolas", Font.PLAIN, 20));
        rem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
                
              int i = table1.getSelectedRow();
              String id = table1.getValueAt(i, 0).toString();
              int quantity = Integer.parseInt(table1.getValueAt(i, 3).toString());
                  
              new Product.stockIn().removeStock(id, quantity, i + 1);

            } catch (IndexOutOfBoundsException e1) {
              JOptionPane.showMessageDialog(null, "No selected row", null, JOptionPane.ERROR_MESSAGE);
            } finally {
              StockMain.this.dispose();
              model.setRowCount(0);
              stockInModel.setRowCount(0);
              stockOutModel.setRowCount(0);
              new StockMain();
            }
          }
        });
        rem.setEnabled(false);

        sp1.setBounds(37, 60, 515, 200);
        sp2.setBounds(37, 60, 515, 200);
        sp3.setBounds(15, 20, 560, 410);

        stockInpanel.add(StockIn);
        stockInpanel.add(sp1);

        stockOutpanel.add(StockOut);
        stockOutpanel.add(sp2);

        panel.add(sp3);

        buttonPanel.add(ref);
        buttonPanel.add(add);
        buttonPanel.add(rem);
        buttonPanel.add(admin);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(1280,720);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        this.add(title);
        this.add(panel);
        this.add(stockInpanel);
        this.add(buttonPanel);
        this.add(stockOutpanel);
        this.add(background);
        this.setVisible(true);
    }
  }

    public static class StockInFrame extends JFrame {

      private String  name;
      private int quantity;

      StockInFrame() {
        POS.POSSystem ps = new POS.POSSystem();
        SimpleDateFormat sdf = new SimpleDateFormat("DD-MM-YYYY");

        ArrayList<String> allList = new ArrayList<String>();
        allList.addAll(ps.getFoodList());
        allList.addAll(ps.getDrinkList());
        allList.addAll(ps.getDessertList());        

        JLabel bg = new JLabel();
        ImageIcon icon = new ImageIcon("icons/pic.png");
        bg.setIcon(icon);
        bg.setBounds(0,0,500,300);
        
        JLabel l_date = new JLabel("Select Date");
        l_date.setFont(new Font("Heavitas", Font.PLAIN, 20));
        l_date.setBounds(35, 30, 150, 40);
        this.add(l_date);

        JDateChooser dc = new JDateChooser();
        dc.setBounds(200, 30, 200, 40);
        dc.setDateFormatString(sdf.getDateFormatSymbols().toString());
        this.add(dc);

        JLabel l_select = new JLabel("Select Item");
        l_select.setFont(new Font("Heavitas", Font.PLAIN, 20));
        l_select.setBounds(35, 45, 150, 100);
        this.add(l_select);

        JComboBox<String> c_list = new JComboBox<String>(allList.toArray(new String[0]));
        c_list.setBounds(200, 75, 200, 40);
        this.add(c_list);


        JLabel l_add = new JLabel("Add Stock");
        l_add.setFont(new Font("Heavitas", Font.PLAIN, 20));
        l_add.setBounds(35, 90, 150, 100);
        this.add(l_add);

        JTextField t_add = new JTextField();
        t_add.setBounds(200, 120, 200, 40);
        t_add.setPreferredSize(new Dimension(200,30));
        this.add(t_add);

        JButton confirm = new JButton("Confirm");
        confirm.setBounds(200,200,100,50);
        confirm.setFocusable(false);
        confirm.setBackground(Color.GRAY);
        confirm.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              Date sqlDate = new Date(dc.getDate().getTime());
              name = c_list.getSelectedItem().toString();
              quantity = Integer.parseInt(t_add.getText());

              new Product.stockIn(name, quantity, sqlDate);

            } catch (NullPointerException e1) {
              JOptionPane.showMessageDialog(null, "Input the date", null, JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e1) {
              JOptionPane.showMessageDialog(null, "Input the quantity", null, JOptionPane.INFORMATION_MESSAGE);
            }
          }
        });
        this.add(confirm);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(300, 200, 100, 50);
        cancel.setFocusable(false);
        cancel.setBackground(Color.WHITE);
        cancel.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            StockInFrame.this.dispose();
          }
        });
        this.add(cancel);
        this.add(StockMain.background);

        this.setSize(500,300);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
      }

    }
}
