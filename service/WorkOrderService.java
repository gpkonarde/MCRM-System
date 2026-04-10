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

    public void addWorkOrder(String woName, int productId, int pLineId, String priority) throws Exception {
        wDAO.addWorkOrder(woName, productId, pLineId, priority);
    }

    public void assignWorkOrder(int workOrderId) throws Exception {
        wDAO.assignWorkOrder(workOrderId);
    }

    public void autoScheduleWorkOrders() throws Exception {
        wDAO.autoScheduleWorkOrders();
    }

    public void updateWOProgress() throws Exception {
        wDAO.updateWorkOrderProgress();
    }
}
