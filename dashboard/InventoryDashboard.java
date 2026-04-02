package dashboard;

import service.ProductService;
import java.util.Scanner;

public class InventoryDashboard {

    ProductService service = new ProductService();
    Scanner sc = new Scanner(System.in);

    public void show() throws Exception {

        while (true) {
            System.out.println("\n==== INVENTORY DASHBOARD ====");
            System.out.println("1. View All Stock");
            System.out.println("2. Update Stock");
            System.out.println("0. Back");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    service.showAllStocks();
                    break;

                case 2:
                    System.out.print("Enter Product Name: ");
                    sc.nextLine(); // clear buffer
                    String pname = sc.nextLine();

                    System.out.print("Enter Quantity to Add: ");
                    int qty = Integer.parseInt(sc.nextLine());

                    service.addStocks(pname, qty);
                    break;

                case 0:
                    return;
            }
        }
    }

}
