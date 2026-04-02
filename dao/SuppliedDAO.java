package dao;

import src.config.DbConnection;
import java.sql.*;

public class SuppliedDAO {
    // Setter

    public void addSupplier(String name, String contact) throws Exception {
        Connection con = DbConnection.getConnection();

        String query = "Insert into Supplier (name, contact) values (?,?)";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, name);
        ps.setString(2, contact);

        ps.executeUpdate();
        System.out.println("Supplier added");
    }

    // Getter

    public void viewSupplierByName(String name) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "SELECT * FROM supplier WHERE name LIKE ?";

        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, "%" + name + "%"); // partial match

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while (rs.next()) {
            found = true;
            System.out.println(
                    rs.getInt("id") + " | " +
                            rs.getString("scode") + " | " +
                            rs.getString("name") + " | " +
                            rs.getString("contact"));
        }

        if (!found) {
            System.out.println("No supplier found with this name");
        }
    }
}
