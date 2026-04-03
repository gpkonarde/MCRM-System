package service;

import dao.WorkOrderDAO;

public class WorkOrderService {

    WorkOrderDAO wDAO = new WorkOrderDAO();

    public void viewAllWorkOrders() throws Exception {
        wDAO.viewWorkOrders();
    }

    public void viewProductionLines() throws Exception {
        wDAO.viewProductionLines();
    }

    public void viewResources() throws Exception {
        wDAO.viewResources();
    }

    public void addWorkOrder(int productId, int qty, String priority) throws Exception {
        wDAO.addWorkOrder(productId, qty, priority);
    }

    public void assignWorkOrder(int workOrderId) throws Exception {
        wDAO.assignWorkOrder(workOrderId);
    }

    public void autoScheduleWorkOrders() throws Exception {
        wDAO.autoScheduleWorkOrders();
    }

}
