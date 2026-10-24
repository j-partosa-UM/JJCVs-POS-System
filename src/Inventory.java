import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class Inventory {
    
    public static class InventorySystem {
        private Connection conn = null;
        private PreparedStatement pst = null;
        private ResultSet rs = null;
    
        private final String[] DEFAULT_CATEGORY = {"Food", "Drink", "Dessert"};
        private String product;
        private int id, stock = 0;
        private double price;
        private String category;
        private byte[] image;
    
        InventorySystem() {
    
            conn = db.java_db();
            
        }
    
        public void addProduct(String id, String name, double price, String category, byte[] image) {
            try {
                
                String sql = "INSERT INTO inventory (id, product, price, quantity, category, image) VALUES (?, ?, ?, ?, ?, ?) ";
                pst = conn.prepareStatement(sql);
    
                pst.setString(1, id);
                pst.setString(2, name);
                pst.setDouble(3, price);
                pst.setInt(4, stock);
                pst.setString(5, category);
                pst.setBytes(6, image);
    
                pst.execute();
                JOptionPane.showMessageDialog(null, "New Product has been added!", "Add Product", JOptionPane.INFORMATION_MESSAGE);
    
                try {
                    String sql2 = "SELECT quantity from stockIn WHERE id = '"+id+"' and name = '"+name+"' and price = '"+price+"' ";
                    pst = conn.prepareStatement(sql2);

                    rs = pst.executeQuery();
                    while (rs.next()) {
                        stock += rs.getInt("quantity");
                    }

                    try {
                        String sql3 = "SELECT quantity from stockOut WHERE id = '"+id+"' and name = '"+name+"' and price = '"+price+"' ";
                        pst = conn.prepareStatement(sql3);

                        rs = pst.executeQuery();
                        while (rs.next()) {
                            stock -= rs.getInt("quantity");
                        }
                        if (stock != 0) {
                            int x = JOptionPane.showConfirmDialog(null, "Stocks have found in the database. Would you like to restore?", null, JOptionPane.YES_NO_OPTION);
                            if(x == 0) {
                                try {
                                    String sql4 = "UPDATE inventory SET quantity = '"+stock+"' WHERE id = '"+id+"' ";
                                    pst = conn.prepareStatement(sql4);
                                    pst.executeUpdate();

                                    JOptionPane.showMessageDialog(null, "Stocks added successfully", null, JOptionPane.INFORMATION_MESSAGE);
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, e.getMessage(), "Restoring Stocks Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Checking Stocks Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Checking Stocks Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Add Product Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                } catch (Exception e) {
                    
                }
            }
        }
    
        public void updateProduct(String id, String name, double price, String category, String image_path) {                      
                                    
            try {
                String sql1 = "UPDATE inventory SET product = '"+name+"', price = '"+price+"', "
                + "category = '"+category+"' WHERE id =  '"+id+"' ";
                
                pst = conn.prepareStatement(sql1);
                pst.executeUpdate();                

                try {
                    String sql2 = "UPDATE inventory SET id = '"+id+"' WHERE product = '"+name+"' ";

                    pst = conn.prepareStatement(sql2);
                    pst.executeUpdate();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Update ID Error!", JOptionPane.ERROR_MESSAGE);
                }
                try {
                    byte[] img;
                    File file = new File(image_path);
                    try (FileInputStream fis = new FileInputStream(file)) {
                        img =  new byte[(int)file.length()];
                        fis.read(img);
                        
                        String sql = "UPDATE inventory SET image =? WHERE id = '"+id+"' ";
    
                        pst = conn.prepareStatement(sql);
                        pst.setBytes(1, img);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Image is now updated", "Update Image", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    
                }
                JOptionPane.showMessageDialog(null, "The product is now updated.", "Update product", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Update Product Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                } catch (Exception e) {
                    
                }
            }
        }
    
        public void removeProduct(int id) {

            try {
                String sql = "DELETE FROM inventory WHERE id = '"+id+"' ";
                
                pst = conn.prepareStatement(sql);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "The product is now removed.", "Remove product", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Remove Product ERROR!", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        public void searchProduct(int id) {

            try {
                String sql = "SELECT * FROM inventory WHERE id = '"+id+"' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                while (rs.next()) {
                    this.id = rs.getInt("id");
                    this.product = rs.getString("product");
                    this.price = rs.getDouble("price");
                    this.stock = rs.getInt("quantity");
                    this.category = rs.getString("category");
                    this.image = rs.getBytes("image");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Search ID Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                } catch (Exception e) {

                }
            }
        }
    
        public void searchProduct(String name) {

            try {
                String sql = "SELECT * FROM inventory WHERE product = '"+name+"' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                while (rs.next()) {
                    this.id = rs.getInt("id");
                    this.product = rs.getString("product");
                    this.price = rs.getDouble("price");
                    this.stock = rs.getInt("quantity");
                    this.category = rs.getString("category");
                    this.image = rs.getBytes("image");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Search Product Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                } catch (Exception e) {
                    
                }
            }
        }
    
        public int getId() {
            return id;
        }
        public String getProduct() {
            return product;
        }
        public double getPrice() {
            return price;
        }
        public int getStock() {
            return stock;
        }
        public String getCategory() {
            return category;
        }
        public byte[] getImage() {
            return image;
        }    
    }

    public static class InventoryPage extends JFrame{
        private InventorySystem is = new InventorySystem();
        private DecimalFormat df = new DecimalFormat("0.00");
        private String filename;
        private byte[] image;

        private JPanel panel = new JPanel();

        private JTextField Id = new JTextField();
        private JTextField Prod_name = new JTextField();
        private JTextField Price = new JTextField();
        private JTextField Quantity = new JTextField();
        private JTextField Search = new JTextField();

        private JLabel inventHead, bg1, prodCategory, prodId, prodName, prodPrice, prodQuantity;

        private JComboBox<String> category = new JComboBox<String>(is.DEFAULT_CATEGORY);

        private JTable table = new JTable();
        private DefaultTableModel model = new DefaultTableModel();        

        private void tableHeader() {
            model = (DefaultTableModel) table.getModel();
            Object [] column = {"Category","Product ID","Product Name", "Image", "Price", "Quantity"};
            model.setColumnIdentifiers(column);
            table.getColumn("Image").setCellRenderer(new myTableCellRenderer());
        }
        class myTableCellRenderer implements TableCellRenderer {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                
                TableColumn tb = table.getColumn("Image");
                tb.setMaxWidth(120);
                tb.setMinWidth(120);
                table.setRowHeight(120);
                table.setRowMargin(5);
                return (Component) value;
            }            
        }
        private void updateTable() {

            tableHeader();
            try {
              String sql = "SELECT * FROM inventory";
              is.pst = is.conn.prepareStatement(sql);
              is.rs = is.pst.executeQuery();
              while (is.rs.next()) {
                String data1 = is.rs.getString("Category");
                String data2 = is.rs.getString("Id");
                String data3 = is.rs.getString("Product");
                byte[] data4 = is.rs.getBytes("Image");        
                JLabel img = new JLabel();        
                Double data5 = is.rs.getDouble("Price");
                int data6 = is.rs.getInt("Quantity");
        
                try {          
                ImageIcon imageIcon = new ImageIcon(data4);
                Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                img.setIcon(new ImageIcon(image));
                } catch (Exception e) {
                  
                }        
                                
                model.addRow(new Object[]{
                         
                  data1,
                  data2,
                  data3,
                  img,
                  df.format(data5),
                  data6
           
                });
              }
              table.setModel(model);
            } catch (Exception e) {
              JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    is.pst.close();
                    is.rs.close();
                } catch (Exception e) {
                    
                }
            }
        }
        public InventoryPage() {

            is.conn = db.java_db();

            bg1 = new JLabel();
            ImageIcon bg = new ImageIcon("src\\icons\\i.png");
            bg1.setIcon(bg);
            bg1.setBounds(0, 0, 1280, 680);
            
            ImageIcon s = new ImageIcon("src\\icons\\s.png");
            ImageIcon r = new ImageIcon("src\\icons\\r.png");
            ImageIcon a = new ImageIcon("src\\icons\\a.png");
            ImageIcon e = new ImageIcon("src\\icons\\e.png");
            ImageIcon addImage = new ImageIcon("src\\icons\\add.png");
            ImageIcon rem = new ImageIcon("src\\icons\\rem.png");
            ImageIcon ret = new ImageIcon("src\\icons\\ret.png");

            JButton add = new JButton("Add New Product", a);
            JButton edit = new JButton("Edit Product", e);
            JButton addimage = new JButton("Add Image", addImage);
            JButton remove = new JButton("Remove Product", rem);
            JButton returnhome = new JButton("Return to Admin Page", ret);

            JScrollPane sp = new JScrollPane(table);
            sp.setBounds(410, 100, 800, 540);
            updateTable();
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int i = table.getSelectedRow();
                    TableModel model = table.getModel();
                    category.setSelectedItem(model.getValueAt(i, 0));
                    Id.setText(model.getValueAt(i, 1).toString());
                    Prod_name.setText(model.getValueAt(i, 2).toString());
                    Price.setText(model.getValueAt(i, 4).toString());
                    Quantity.setText(model.getValueAt(i, 5).toString());
            
                }
            @Override
            public void mouseReleased(MouseEvent e) {
                table.clearSelection();
            }
                });
            inventHead = new JLabel();
            inventHead.setBounds(75, -100, 300, 300);
            inventHead.setText("Inventory");
            inventHead.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 50));
            
            prodCategory = new JLabel("Category");
            prodCategory.setFont(new Font("Consolas", Font.BOLD, 20));
            prodCategory.setBounds(70, 100, 150, 30);
            prodId = new JLabel("Product ID");
            prodId.setFont(new Font("Consolas", Font.BOLD, 20));
            prodId.setBounds(70, 153, 150, 30);
            prodName = new JLabel("Product Name");
            prodName.setFont(new Font("Consolas", Font.BOLD, 20));
            prodName.setBounds(70, 206, 150, 30);
            prodPrice = new JLabel("Product Price");
            prodPrice.setFont(new Font("Consolas", Font.BOLD, 20));
            prodPrice.setBounds(70, 253, 150, 30);
            prodQuantity = new JLabel("Product Quantity");
            prodQuantity.setFont(new Font("Consolas", Font.BOLD, 18));
            prodQuantity.setBounds(70, 303, 200, 30);

            category.setBounds(230, 100, 150, 30);
            Id.setText("Enter ID");
            Id.setBounds(230, 150, 150, 30);
            Id.setFont(new Font("Consolas", Font.PLAIN, 12));
            Id.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                     if (Id.getText().equals("Enter ID")) {
              Id.setText("");					
                                }
                                 else {
                    Id.selectAll();
                                }
                      }
                @Override
                public void focusLost(FocusEvent e) {
                        if (Id.getText().equals("")) {
                            Id.setText("Enter ID");
                        }
                }
            });
            Prod_name.setBounds(230, 200, 150, 30);
            Prod_name.setText("Enter Name");
            Prod_name.setFont(new Font("Consolas", Font.PLAIN, 12));
            Prod_name.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (Prod_name.getText().equals("Enter Name")) {
                Prod_name.setText("");					
                                    }
                                    else {
                        Prod_name.selectAll();
                                    }
                        }
                    @Override
                    public void focusLost(FocusEvent e) {
                            if(Prod_name.getText().equals("")){
                                Prod_name.setText("Enter Name");
                            }		
                    }
                });
            Price.setBounds(230, 250, 150, 30);
            Price.setText("Enter Price");
            Price.setFont(new Font("Consolas", Font.PLAIN, 12));
            Price.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (Price.getText().equals("Enter Price")) {
                Price.setText("");					
                                    }
                                    else {
                        Price.selectAll();
                                    }
                        }
                    @Override
                    public void focusLost(FocusEvent e) {
                            if(Price.getText().equals("")){
                                Price.setText("Enter Price");
                            }		
                    }
                });
            Quantity.setBounds(250, 300, 130, 30);
            Quantity.setFont(new Font("Consolas", Font.PLAIN, 12));
            Quantity.setBackground(Color.WHITE);
            Quantity.setEditable(false);
            Search.setBounds(410, 35, 470, 40);
            Search.setFont(new Font("Consolas", Font.BOLD, 25));
            Search.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (Search.getText().equals("Search...")) {
                Search.setText("");					
                                    }
                                    else {
                        Search.selectAll();
                                    }
                        }
                    @Override
                    public void focusLost(FocusEvent e) {
                            if(Search.getText().equals("")){
                                Search.setText("Search...");
                            }		
                    }
                });

            JButton search = new JButton("Search", s);
            search.setBounds(890, 35, 150, 40);
            search.setFont(new Font("Consolas", Font.BOLD, 15));
            search.setFocusable(false);
            search.setBackground(new Color(0xD6EAF8));
            search.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        search.setBackground(new Color(0x2ECC71));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                search.setBackground(new Color(0xD6EAF8));
                    }
                });
            search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                is.searchProduct(Integer.parseInt(Search.getText()));
                is.searchProduct(Search.getText());;

                category.setSelectedItem(is.getCategory());
                Id.setText(String.valueOf(is.getId()));
                Prod_name.setText(is.getProduct());
                Price.setText(String.valueOf(df.format(is.getPrice())));
                } catch (Exception e1) {

                }
            }
            });
            JButton refresh = new JButton("Refresh", r);
            refresh.setBounds(1055, 35, 150, 40);
            refresh.setFont(new Font("Consolas", Font.BOLD, 15));
            refresh.setFocusable(false);
            refresh.setBackground(new Color(0xD6EAF8));
            refresh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        refresh.setBackground(new Color(0x00FF66));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                refresh.setBackground(new Color(0xD6EAF8));
                    }
                });
            refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  
                InventoryPage.this.dispose();    
                new InventoryPage();                   
            }
            });
            add.setBounds(135, 390, 200, 40);
            add.setFont(new Font("Consolas", Font.BOLD, 15));
            add.setFocusable(false);
            add.setBackground(new Color(0xD6EAF8));
            add.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        add.setBackground(new Color(0x03A9F4 ));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                add.setBackground(new Color(0xD6EAF8));
                    }
                });
            add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(Id.getText().equals("") == false && Id.getText().equals("Enter ID") == false) {
                    if(Prod_name.getText().equals("") == false && Prod_name.getText().equals("Enter Name") == false) {
                        try {
                            String add1 = Id.getText();
                            String add2 = Prod_name.getText();
                            Double add3 = Double.parseDouble(Price.getText());
                            String add4 = category.getSelectedItem().toString();
                            byte[] add5 = image;
                            int x = JOptionPane.showConfirmDialog(null, "Click yes to confirm", "Product Inventory", JOptionPane.YES_NO_OPTION);
                            if(x == 0) {
                                is.addProduct(add1, add2, add3, add4, add5);
                            }
                        } catch(NumberFormatException e1) {
                            JOptionPane.showMessageDialog(null, "Enter the price", null, JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Enter the product name", null, JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Enter the product ID", null, JOptionPane.ERROR_MESSAGE);
                }
            
            }
            });
            edit.setBounds(135, 440, 200, 40);
            edit.setFont(new Font("Consolas", Font.BOLD, 15));
            edit.setFocusable(false);
            edit.setBackground(new Color(0xD6EAF8));
            edit.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        edit.setBackground(new Color(0xA569BD ));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                edit.setBackground(new Color(0xD6EAF8));
                    }
                });
            edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                if(Id.getText().equals("") == false && Id.getText().equals("Enter ID") == false) {
                    if(Prod_name.getText().equals("") == false && Prod_name.getText().equals("Enter Name") == false) {
                        try {
                            String edi1 = Id.getText();
                            String edit2 = Prod_name.getText();
                            Double edit3 = Double.parseDouble(Price.getText());
                            String edit4 = category.getSelectedItem().toString();
                            String edit5 = filename;

                            int x = JOptionPane.showConfirmDialog(null, "Click yes to confirm", "Product Update", JOptionPane.YES_NO_OPTION);
                            if(x == 0 ) {
                                is.updateProduct(edi1, edit2, edit3, edit4, edit5);
                            }
                        } catch(NumberFormatException e1) {
                            JOptionPane.showMessageDialog(null, "Enter the price", null, JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Enter the product name", null, JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Enter the Product ID", null, JOptionPane.ERROR_MESSAGE);
                }
            }
            });
            addimage.setBounds(135, 490, 200, 40);
            addimage.setFont(new Font("Consolas", Font.BOLD, 15));
            addimage.setFocusable(false);
            addimage.setBackground(new Color(0xD6EAF8));
            addimage.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        addimage.setBackground(new Color(0xF1C40F));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        addimage.setBackground(new Color(0xD6EAF8));
                    }
                });
            addimage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();
                
                try {
                    filename = f.getAbsolutePath();
                    File imagef = new File(filename);
                    try (FileInputStream fix = new FileInputStream(imagef)) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
    
                        for (int i = 0; (i = fix.read(buf)) !=-1 ;) {
                            
                                bos.write(buf, 0, i);
                        }
                        image = bos.toByteArray();
                    }
                JOptionPane.showMessageDialog(null, "Image has been added");   
                } catch (Exception e1) {

                }         
            }
            });
            remove.setBounds(135, 540, 200, 40);
            remove.setFont(new Font("Consolas", Font.BOLD, 15));
            remove.setFocusable(false);
            remove.setBackground(new Color(0xD6EAF8));
            remove.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        remove.setBackground(new Color(0xE74C3C));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        remove.setBackground(new Color(0xD6EAF8));
                    }
                });
            remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {                    
                int remove = Integer.parseInt(Id.getText());
                int x = JOptionPane.showConfirmDialog(null, "Click yes to confirm", "Remove Product", JOptionPane.YES_NO_OPTION);
                if (x==0) {
                    is.removeProduct(remove);
                }
                } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
            });
            returnhome.setBounds(110, 590, 250, 40);
            returnhome.setFont(new Font("Consolas", Font.BOLD, 15));
            returnhome.setFocusable(false);
            returnhome.setBackground(new Color(0xD6EAF8));
            returnhome.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        returnhome.setBackground(new Color(0xFBFCFC));
                        InventoryPage.this.dispose();
                        new AdminPage();
                    }

                });
                
            panel.setBounds(85,380,300,260);
            panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            panel.setBackground(new Color(0xd7e3fc));
            panel.setLayout(null);
            
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    this.setSize(1280, 720);
            this.setLayout(null);
		    this.setResizable(false);
            this.setLocationRelativeTo(null);

            this.setVisible(true);
            this.add(inventHead);
            this.add(prodCategory);
            this.add(prodId);
            this.add(prodName);
            this.add(prodPrice);
            this.add(prodPrice);
            this.add(prodQuantity);
            this.add(category);
            this.add(Id);
            this.add(Prod_name);
            this.add(Price);
            this.add(Quantity);
            this.add(Search);
            this.add(sp);
            this.add(search);
            this.add(refresh);
            this.add(addimage);
            this.add(remove);            
            this.add(add);
            this.add(edit);
            this.add(returnhome);
            this.add(panel);
            this.add(bg1);
        }
    }
}
