package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import src.config.DbConnection;

public class WorkOrderDAO {
    public void viewWorkOrders() throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "SELECT wo.id, p.name, wo.quantity, wo.priority, wo.status " +
                "FROM work_order wo " +
                "JOIN product p ON wo.product_id = p.id";

        ResultSet rs = con.createStatement().executeQuery(query);

        while (rs.next()) {
            System.out.println(
                    "WO#" + rs.getInt("id") + " | " +
                            rs.getString("name") + " | Qty: " +
                            rs.getInt("quantity") + " | Priority: " +
                            rs.getString("priority") + " | Status: " +
                            rs.getString("status"));
        }
    }

    public void viewProductionLines() throws Exception {

        Connection con = DbConnection.getConnection();

        ResultSet rs = con.createStatement().executeQuery(
                "SELECT * FROM production_line");

        while (rs.next()) {
            System.out.println(
                    rs.getInt("id") + " | " +
                            rs.getString("name") + " | Capacity: " +
                            rs.getInt("capacity") + " | Status: " +
                            rs.getString("status") + " | Time: " +
                            rs.getInt("std_time") + " minutes");
        }
    }

    public void viewResources() throws Exception {

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

    public void addWorkOrder(int productId, int qty, String priority) throws Exception {

        Connection con = DbConnection.getConnection();

        String query = "INSERT INTO work_order (product_id, quantity, priority, status) VALUES (?, ?, ?, 'PENDING')";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, productId);
        ps.setInt(2, qty);
        ps.setString(3, priority);

        ps.executeUpdate();

        System.out.println("✅ Work Order Created");
    }

    public void assignWorkOrder(int workOrderId) throws Exception {

        Connection con = DbConnection.getConnection();

        try {
            con.setAutoCommit(false); // ✅ Start transaction

            // ✅ Get ONLY pending work order
            String woQuery = "SELECT * FROM work_order WHERE id = ? AND status = 'PENDING'";
            PreparedStatement ps1 = con.prepareStatement(woQuery);
            ps1.setInt(1, workOrderId);
            ResultSet wo = ps1.executeQuery();

            if (!wo.next()) {
                System.out.println("❌ Work order not found OR already assigned");
                return;
            }

            int qty = wo.getInt("quantity");

            // ✅ Get best production line
            String lineQuery = "SELECT * FROM production_line WHERE status = 'ACTIVE'";
            ResultSet lines = con.createStatement().executeQuery(lineQuery);

            int bestLine = -1;
            int minTime = Integer.MAX_VALUE;

            while (lines.next()) {
                int lineId = lines.getInt("id");
                int stdTime = lines.getInt("std_time");
                int time = stdTime * qty;

                if (time < minTime) {
                    minTime = time;
                    bestLine = lineId;
                }
            }

            if (bestLine == -1) {
                System.out.println("❌ No available production line");
                return;
            }

            // ✅ Get resource from that line
            String resQuery = "SELECT id FROM resource WHERE line_id = ? LIMIT 1";
            PreparedStatement ps2 = con.prepareStatement(resQuery);
            ps2.setInt(1, bestLine);
            ResultSet res = ps2.executeQuery();

            if (!res.next()) {
                System.out.println("❌ No resource available for selected line");
                return;
            }

            int resourceId = res.getInt("id");

            // ✅ Update ONLY if still PENDING (double safety)
            String update = "UPDATE work_order SET line_id=?, assigned_resource_id=?, estimated_time=?, status='IN_PROGRESS' WHERE id=? AND status='PENDING'";
            PreparedStatement ps3 = con.prepareStatement(update);

            ps3.setInt(1, bestLine);
            ps3.setInt(2, resourceId);
            ps3.setInt(3, minTime);
            ps3.setInt(4, workOrderId);

            int rows = ps3.executeUpdate();

            if (rows == 0) {
                System.out.println("❌ Work order was already assigned by another process");
                con.rollback();
                return;
            }

            con.commit(); // ✅ Success

            System.out.println("✅ Assigned to Line " + bestLine + " | Time: " + minTime + " mins");

        } catch (Exception e) {
            con.rollback(); // ✅ Rollback on error
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}
