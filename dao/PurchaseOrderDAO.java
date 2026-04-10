package dao;

import src.config.*;
import java.sql.*;

public class PurchaseOrderDAO {

    // View pending orders
    public void viewPendingOrder(int supplierid) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "Select po.hmy, pr.name, po.qt_purchased, po.sStatus\n" + //
                "from product pr\n" + //
                "inner join purchase_order po on pr.hmy = po.hProduct\n" + //
                "inner join vendor v on v.hmy = po.hVendor\n" + //
                "where v.vCode = ?\n" + //
                "and po.sStatus = 'Pending';\n" + //
                "";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, supplierid);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println();
            System.out.println("--------------- Purchase Order ---------------");
            System.out.println("PO ID      : " + rs.getInt("hmy"));
            System.out.println("Product    : " + rs.getString("name"));
            System.out.println("Quantity   : " + rs.getInt("qt_purchased"));
            System.out.println("Status     : " + rs.getString("sStatus"));
            System.out.println("----------------------------------------------\n");
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
