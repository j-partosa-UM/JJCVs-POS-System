import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class Product {

    protected Connection conn = null;
    protected PreparedStatement pst = null;
    protected ResultSet rs = null;

    private String id;
    private String name;
    private int quantity;
    private double price;
    private String date;

    Product(String name, int quantity, Date date) {
        this.name = name;
        this.quantity = quantity;

        try {
            DateFormat df = DateFormat.getDateInstance();
            this.date = df.format(date);
        } catch (NullPointerException e) {

        }

        conn = db.java_db();
        try {
            String sql = "SELECT id, price from inventory WHERE product= ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            rs = pst.executeQuery();
                id = rs.getString("id");
                price = rs.getDouble("price");

        } catch (SQLException e) {
            if(!e.getMessage().equals("ResultSet closed")) {
            JOptionPane.showMessageDialog(null, e, "Obtaining Data From Inventory Error!", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            try {
                rs.close();
            } catch (Exception e) {
                
            }
        }
    }

    public void storeInventory() {

    }
    
    public static class stockIn extends Product {

    
        stockIn(String name, int quantity, Date date) {
            super(name, quantity, date);

            try {
                String sql2 = "SELECT count() as c from stockIn"; //It assumes that count() is the same as the rownum of the last row.
                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                int row_num = rs.getInt("c") + 1;//Gets the rownum of the new row to be inserted.

                try {
                    String sql = "INSERT INTO stockIn (row_num, id, date, name, quantity, price) VALUES (?, ?, ?, ?, ?, ?)";
        
                    pst = conn.prepareStatement(sql);
                    pst.setInt(1, row_num);
                    pst.setString(2, super.id);
                    pst.setString(3, super.date);
                    pst.setString(4, super.name);
                    pst.setInt(5, super.quantity);
                    pst.setDouble(6, super.price);
        
                    pst.execute();
    
                    JOptionPane.showMessageDialog(null, "Stock added successfully!");
                    storeInventory();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Stock In Error!", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Setting Row Number Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (Exception e) {

                }
            }
        }

        stockIn() {
            super(null, 0, null);
        }
    
        @Override
        public void storeInventory() {
            try {
                String sql = "UPDATE inventory SET quantity = (quantity + '"+super.quantity+"') WHERE product = '"+super.name+"' ";
                pst = conn.prepareStatement(sql);
                pst.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e, "Inventory Update Error!", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void removeStock(String id, int stck_Q, int rownum) {
            try {
                int inv_Q;
                String sql = "SELECT quantity from inventory WHERE id = '"+id+"' ";
                pst = conn.prepareStatement(sql);
                rs =pst.executeQuery();
                inv_Q = rs.getInt("quantity");
                int new_Q;
                if (inv_Q >= stck_Q) {
                    new_Q = inv_Q - stck_Q;    
                } else {
                    new_Q = 0;
                }
                
                rs.close();
                try {
                    String sql2 = "DELETE from stockIn WHERE row_num = '"+rownum+"'";
                    pst = conn.prepareStatement(sql2);
                    pst.executeUpdate();
                            
                    try {
                        String sql3 = "SELECT count() as c from stockIn";
                        pst = conn.prepareStatement(sql3);
                        rs = pst.executeQuery();
                        int next_row = rownum+1;
                        int last_row = rs.getInt("c") + 1;
                        for (int i = next_row; i <= last_row; i++) {
                            int new_rownum = i - 1;
                            //To update the row number of the rows after the deleted row.
                            try {
                                String sql4 = "UPDATE stockIn SET row_num = '"+new_rownum+"' WHERE row_num = '"+i+"' ";
                                pst = conn.prepareStatement(sql4);
                                pst.executeUpdate();
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, e, null, JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e, null, JOptionPane.ERROR_MESSAGE);
                    }

                    try {
                        String sql1 = "UPDATE inventory SET quantity = '"+new_Q+"' WHERE id = '"+id+"' ";
                        pst = conn.prepareStatement(sql1);
                        pst.executeUpdate();
                        
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e, "Update Inventory Error!", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e, "Delete Error!", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(null, "Stock removed succesfully", null, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e, "Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (Exception e) {

                }
            }
        }
    }

    public static class stockOut extends Product {

    
        stockOut(String name, int quantity, double total, Date date) {
            super(name, quantity, date);
    
            try {
                String sql = "SELECT count() AS c from stockOut";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                int row_num = rs.getInt("c") + 1;
                try {
                    String sql1 = "INSERT INTO stockOut (row_num, id, date, name, quantity, price, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    pst = conn.prepareStatement(sql1);
                    pst.setInt(1, row_num);
                    pst.setString(2, super.id);
                    pst.setString(3, super.date);
                    pst.setString(4, super.name);
                    pst.setInt(5, super.quantity);
                    pst.setDouble(6, super.price);
                    pst.setDouble(7, total);
        
                    pst.execute();
                    storeInventory();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Return Error!", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e, "Setting Row Number error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    pst.close();
                } catch (Exception e) {
                    
                }
            }
        }
    
        @Override
        public void storeInventory() {
            try {
                String sql = "UPDATE inventory SET quantity = (quantity - '"+super.quantity+"') WHERE product = '"+super.name+"' ";
                pst = conn.prepareStatement(sql);
                pst.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Update Inventory Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }    
}
