package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import src.config.DbConnection;

public class WorkOrderDAO {
    public void viewWorkOrders() throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "SELECT " +
                "wo.wo_name, " +
                "pl.pl_name, " +
                "p.name AS person_name, " +
                "pr.name AS product_name, " +
                "wo.dtStart, " +
                "wo.dtEnd, " +
                "wo.remaining_time " +
                "FROM work_order wo " +
                "INNER JOIN product pr ON pr.hmy = wo.hProduct " +
                "INNER JOIN production_line pl ON pl.hmy = wo.hPLine " +
                "INNER JOIN person p ON p.hmy = pl.hPerson;";

        ResultSet rs = con.createStatement().executeQuery(query);

        while (rs.next()) {
            System.out.println();
            System.out.println("------------- Work Order -------------");
            System.out.println("WO Name      : " + rs.getString("wo_name"));
            System.out.println("Production   : " + rs.getString("pl_name"));
            System.out.println("Operator     : " + rs.getString("person_name"));
            System.out.println("Product      : " + rs.getString("product_name"));
            System.out.println("Start Date   : " + rs.getDate("dtStart"));
            System.out.println("End Date     : " + rs.getDate("dtEnd"));
            System.out.println("Remaining    : " + rs.getInt("remaining_time"));
            System.out.println("--------------------------------------\n");
        }
    }

    public void viewProductionLines() throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "SELECT " +
                "pl.pcode AS line_code, " +
                "pl.pl_name AS line_name, " +
                "pl.capacity, " +
                "pl.avg_time " +
                "FROM production_line pl " +
                "INNER JOIN person p ON p.hmy = pl.hperson;";

        ResultSet rs = con.createStatement().executeQuery(query);

        while (rs.next()) {
            System.out.println(
                    "Line Code: " + rs.getInt("line_code") + " | " +
                            "Name: " + rs.getString("line_name") + " | " +
                            "Capacity: " + rs.getInt("capacity") + " | " +
                            "Avg Time: " + rs.getInt("avg_time") + " mins");
        }
    }

    /* not working */ public void viewResources() throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "SELECT r.name, r.skill, pl.name AS line " +
                "FROM resource r " +
                "JOIN production_line pl ON r.line_id = pl.id";

        ResultSet rs = con.createStatement().executeQuery(query);

        while (rs.next()) {
            System.out.println(
                    rs.getString("name") + " | " +
                            rs.getString("skill") + " | Line: " +
                            rs.getString("line"));
        }
    }

    public void addWorkOrder(String woName, int productId, int pLineId, String priority) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "INSERT INTO work_order " +
                "(wo_name, hProduct, hPLine, priority, dtStart, dtEnd, remaining_time, status) " +
                "VALUES (?, ?, ?, ?, NULL, NULL, 0, 'Pending')";

        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, woName);
        ps.setInt(2, productId);
        ps.setInt(3, pLineId);
        ps.setString(4, priority);

        ps.executeUpdate();

        System.out.println("Work Order Created Successfully");
    }

    public void assignWorkOrder(int workOrderId) throws Exception {

        Connection con = DbConnection.getConnection();

        try {
            con.setAutoCommit(false);

            // ✅ Get pending work order
            String woQuery = "SELECT * FROM work_order WHERE hmy = ? AND status = 'Pending'";
            PreparedStatement ps1 = con.prepareStatement(woQuery);
            ps1.setInt(1, workOrderId);
            ResultSet wo = ps1.executeQuery();

            if (!wo.next()) {
                System.out.println("❌ Work order not found or already assigned");
                return;
            }

            int productId = wo.getInt("hProduct");

            // ✅ Get available production lines (respect UNIQUE hPLine)
            String lineQuery = "SELECT pl.hmy, pl.avg_time " +
                    "FROM production_line pl " +
                    "LEFT JOIN work_order wo ON wo.hPLine = pl.hmy AND wo.status = 'In Progress' " +
                    "WHERE wo.hmy IS NULL";

            ResultSet lines = con.createStatement().executeQuery(lineQuery);

            int bestLine = -1;
            int minTime = Integer.MAX_VALUE;

            while (lines.next()) {
                int lineId = lines.getInt("hmy");
                int avgTime = lines.getInt("avg_time");

                if (avgTime < minTime) {
                    minTime = avgTime;
                    bestLine = lineId;
                }
            }

            if (bestLine == -1) {
                System.out.println("❌ No available production line");
                con.rollback();
                return;
            }

            // ✅ Get available person (respect UNIQUE hPerson)
            String personQuery = "SELECT p.hmy " +
                    "FROM person p " +
                    "LEFT JOIN work_order wo ON wo.hPerson = p.hmy AND wo.status = 'In Progress' " +
                    "WHERE wo.hmy IS NULL LIMIT 1";

            ResultSet persons = con.createStatement().executeQuery(personQuery);

            if (!persons.next()) {
                System.out.println("❌ No available person");
                con.rollback();
                return;
            }

            int personId = persons.getInt("hmy");

            // ✅ Update work order
            String update = "UPDATE work_order SET " +
                    "hPLine = ?, " +
                    "hPerson = ?, " +
                    "remaining_time = ?, " +
                    "dtStart = NOW(), " +
                    "status = 'In Progress' " +
                    "WHERE hmy = ? AND status = 'Pending'";

            PreparedStatement ps3 = con.prepareStatement(update);

            ps3.setInt(1, bestLine);
            ps3.setInt(2, personId);
            ps3.setInt(3, minTime);
            ps3.setInt(4, workOrderId);

            int rows = ps3.executeUpdate();

            if (rows == 0) {
                System.out.println("❌ Already assigned by another process");
                con.rollback();
                return;
            }

            con.commit();

            System.out.println("✅ Assigned → Line: " + bestLine +
                    " | Person: " + personId +
                    " | Time: " + minTime + " mins");

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void autoScheduleWorkOrders() throws Exception {

        Connection con = DbConnection.getConnection();

        try {
            con.setAutoCommit(false);

            // ✅ Get pending work orders sorted by priority
            String woQuery = "SELECT * FROM work_order WHERE status = 'Pending' " +
                    "ORDER BY CASE " +
                    "WHEN priority = 'Critical' THEN 1 " +
                    "WHEN priority = 'HIGH' THEN 2 " +
                    "WHEN priority = 'MEDIUM' THEN 3 " +
                    "WHEN priority = 'LOW' THEN 4 END";

            ResultSet workOrders = con.createStatement().executeQuery(woQuery);

            boolean anyAssigned = false;

            while (workOrders.next()) {

                int workOrderId = workOrders.getInt("hmy");
                String woName = workOrders.getString("wo_name");

                // ✅ Get available production lines (not already in progress)
                String lineQuery = "SELECT pl.hmy, pl.avg_time " +
                        "FROM production_line pl " +
                        "LEFT JOIN work_order wo ON wo.hPLine = pl.hmy AND wo.status = 'In Progress' " +
                        "WHERE wo.hmy IS NULL";

                ResultSet lines = con.createStatement().executeQuery(lineQuery);

                int bestLine = -1;
                int minTime = Integer.MAX_VALUE;

                while (lines.next()) {
                    int lineId = lines.getInt("hmy");
                    int avgTime = lines.getInt("avg_time");

                    if (avgTime < minTime) {
                        minTime = avgTime;
                        bestLine = lineId;
                    }
                }

                if (bestLine == -1) {
                    System.out.println("❌ No available line for WO#" + workOrderId);
                    continue;
                }

                // ✅ Get available person
                String personQuery = "SELECT p.hmy " +
                        "FROM person p " +
                        "LEFT JOIN work_order wo ON wo.hPerson = p.hmy AND wo.status = 'In Progress' " +
                        "WHERE wo.hmy IS NULL LIMIT 1";

                ResultSet persons = con.createStatement().executeQuery(personQuery);

                if (!persons.next()) {
                    System.out.println("❌ No available person for WO#" + workOrderId);
                    continue;
                }

                int personId = persons.getInt("hmy");

                // ✅ Assign work order
                String update = "UPDATE work_order SET " +
                        "hPLine = ?, " +
                        "hPerson = ?, " +
                        "remaining_time = ?, " +
                        "dtStart = NOW(), " +
                        "status = 'In Progress' " +
                        "WHERE hmy = ? AND status = 'Pending'";

                PreparedStatement ps = con.prepareStatement(update);

                ps.setInt(1, bestLine);
                ps.setInt(2, personId);
                ps.setInt(3, minTime);
                ps.setInt(4, workOrderId);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    anyAssigned = true;
                    System.out.println("✅ WO#" + woName +
                            " → Line " + bestLine +
                            " | Person " + personId +
                            " | Time: " + minTime + " mins");
                } else {
                    System.out.println("Skipped WO#" + workOrderId);
                }
            }

            if (!anyAssigned) {
                System.out.println("No work orders assigned");
            }

            con.commit();

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void updateWorkOrderProgress() throws Exception {

        Connection con = DbConnection.getConnection();

        try {
            con.setAutoCommit(false);

            // ✅ Get all active work orders
            String query = "SELECT * FROM work_order WHERE status = 'In Progress'";
            ResultSet rs = con.createStatement().executeQuery(query);

            while (rs.next()) {

                int id = rs.getInt("hmy");
                String woName = rs.getString("wo_name");
                int remainingTime = rs.getInt("remaining_time");

                java.sql.Timestamp lastModified = rs.getTimestamp("dtLastModified");
                java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());

                // ⏱ Time difference in minutes
                long diffMillis = currentTime.getTime() - lastModified.getTime();
                int minutesPassed = (int) (diffMillis / (1000 * 60));

                // Skip if no time passed
                if (minutesPassed <= 0)
                    continue;

                minutesPassed = Math.min(minutesPassed, remainingTime);

                int updatedRemaining = remainingTime - minutesPassed;

                if (updatedRemaining <= 0) {

                    // ✅ COMPLETE WORK ORDER
                    String completeQuery = "UPDATE work_order SET " +
                            "status='Completed', " +
                            "remaining_time=0, " +
                            "dtEnd = NOW() " +
                            "WHERE hmy=?";

                    PreparedStatement ps1 = con.prepareStatement(completeQuery);
                    ps1.setInt(1, id);
                    ps1.executeUpdate();

                    System.out.println("✅ WO#" + woName + " COMPLETED");

                } else {

                    // ✅ UPDATE remaining time (dtLastModified auto-updates)
                    String updateQuery = "UPDATE work_order SET remaining_time=? WHERE hmy=?";

                    PreparedStatement ps2 = con.prepareStatement(updateQuery);
                    ps2.setInt(1, updatedRemaining);
                    ps2.setInt(2, id);
                    ps2.executeUpdate();

                    System.out.println("⏳ WO#" + woName +
                            " remaining: " + updatedRemaining + " mins");
                }
            }

            con.commit();

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}
