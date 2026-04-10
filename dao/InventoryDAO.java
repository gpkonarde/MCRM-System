package dao;

import src.config.*;
import java.sql.*;

public class InventoryDAO {
    public void viewAllStocks() throws Exception {
        Connection con = DbConnection.getConnection();

        String query = "select p.name,i.quantity_available from product p inner join inventory i on p.hmy = i.hproduct;";

        ResultSet rs = con.createStatement().executeQuery(query);

        while (rs.next()) {
            System.out.println(
                    rs.getString("name") + " | Stock: " +
                            rs.getInt("quantity_available"));
        }
    }

    public void updateStock(String product_name, int quantity) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "UPDATE inventory i " +
                "JOIN product p ON i.hproduct = p.hmy " +
                "SET i.quantity_available = ? " +
                "WHERE p.name = ?";

        PreparedStatement ps = con.prepareStatement(query);

        ps.setInt(1, quantity);
        ps.setString(2, product_name);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Quantity updated");
        } else {
            System.err.println("Product not found in inventory");
        }

    }
}
