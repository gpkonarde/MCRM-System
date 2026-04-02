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

    public void viewSupplier() throws Exception {
        Connection con = DbConnection.getConnection();

        ResultSet rs = con.createStatement().executeQuery("Select * from supplier");

        while (rs.next()) {
            System.out.println(rs.getInt("id") + " | " + rs.getString("name"));
        }
    }
}
