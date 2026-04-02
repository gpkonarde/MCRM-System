package service;

import dao.*;

public class SupplierService {

    SuppliedDAO supplierDAO = new SuppliedDAO();
    PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();

    public void addSupplier(String name, String contact) throws Exception {
        supplierDAO.addSupplier(name, contact);
    }

    public void showSupplier(String name) throws Exception {
        supplierDAO.viewSupplierByName(name);
    }

    public void showPendingOrder(int supplierId) throws Exception {
        purchaseOrderDAO.viewPendingOrder(supplierId);
    }

    public void showAmountOwed(int supplierId) throws Exception {
        purchaseOrderDAO.amountOwed(supplierId);
    }
}
