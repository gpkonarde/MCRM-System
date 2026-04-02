package dao;

import src.config.*;
import java.sql.*;

public class PurchaseOrderDAO {

    // View pending orders
    public void viewPendingOrder(int supplierid) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "Select * from purchase_order where supplier_id = ? and status = 'Pending'";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, supplierid);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println(
                    "PO ID: " + rs.getInt("id") +
                            " | Product: " + rs.getInt("product_id") +
                            " | Qty: " + rs.getInt("quantity") +
                            " | Status: " + rs.getString("status"));
        }
    }

    public double amountOwed(int supplierid) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "Select Sum(quantity * price) As total_amount_owed from purchase_order where supplied_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, supplierid);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getDouble("total: ");
        }
        return 0;
    }
}
