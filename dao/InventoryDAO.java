package dao;

import src.config.*;
import java.sql.*;

public class InventoryDAO {
    public void viewAllStocks() throws Exception {
        Connection con = DbConnection.getConnection();

        String query = "Select p.name, i.quantity_available, p.type, p.price\n" + //
                "from product p\n" + //
                "inner join inventory i on p.id = i.product_id\n" + //
                "order by p.name asc";

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
                "JOIN product p ON i.product_id = p.id " +
                "SET i.quantity_available = i.quantity_available + ? " +
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
