package service;

import src.config.*;
import dao.InventoryDAO;

public class ProductService {

    InventoryDAO iDao = new InventoryDAO();

    // Show stocks
    public void showAllStocks() throws Exception {
        iDao.viewAllStocks();
    }

    public void addStocks(String product_name, int quantity) throws Exception {
        iDao.updateStock(product_name, quantity);
    }

}
