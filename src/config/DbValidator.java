package src.config;

import java.sql.*;

public class DbValidator {

    public static void validate() {

        System.out.println("\n🔍 Running DB Validation...\n");

        try (Connection con = DbConnection.getConnection()) {

            if (con == null) {
                System.out.println("❌ Connection failed!");
                return;
            }

            // 1. Check current database
            Statement stmt = con.createStatement();

            ResultSet rsInfo = stmt.executeQuery("SELECT @@hostname, @@port, DATABASE(), USER()");

            if (rsInfo.next()) {
                System.out.println("\n[DB INFO]");
                System.out.println("Host     : " + rsInfo.getString(1));
                System.out.println("Port     : " + rsInfo.getString(2));
                System.out.println("Database : " + rsInfo.getString(3));
                System.out.println("User     : " + rsInfo.getString(4));
            }
            ResultSet rsDb = stmt.executeQuery("SELECT DATABASE()");
            if (rsDb.next()) {
                System.out.println("✅ Connected to DB: " + rsDb.getString(1));
            }

            // 2. List tables
            System.out.println("\n📦 Tables:");
            ResultSet rsTables = stmt.executeQuery("SHOW TABLES");

            boolean hasTables = false;

            while (rsTables.next()) {
                hasTables = true;
                String table = rsTables.getString(1);
                System.out.print(" - " + table);

                // ✅ Use separate statement
                try (Statement countStmt = con.createStatement();
                        ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) FROM " + table)) {

                    if (countRs.next()) {
                        System.out.print(" (rows: " + countRs.getInt(1) + ")");
                    }

                } catch (Exception e) {
                    System.out.print(" (count failed)");
                }

                System.out.println();
            }

            if (!hasTables) {
                System.out.println("❌ No tables found!");
            }

            // 4. Check triggers
            System.out.println("\n⚙️ Triggers:");
            ResultSet rsTriggers = stmt.executeQuery("SHOW TRIGGERS");

            boolean hasTriggers = false;

            while (rsTriggers.next()) {
                hasTriggers = true;
                System.out.println(" - " + rsTriggers.getString("Trigger"));
            }

            if (!hasTriggers) {
                System.out.println("❌ No triggers found!");
            }

            System.out.println("\n✅ DB Validation Complete\n");

        } catch (Exception e) {
            System.out.println("❌ DB Validation Failed:");
            e.printStackTrace();
        }
    }
}