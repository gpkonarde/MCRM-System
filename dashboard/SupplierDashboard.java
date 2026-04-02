package dashboard;

import service.SupplierService;
import java.util.*;

public class SupplierDashboard {

    Scanner sc = new Scanner(System.in);
    SupplierService service = new SupplierService();

    public void show() throws Exception {
        while (true) {
            System.out.println("\n==== SUPPLIER DASHBOARD ====");
            System.out.println("1. Add Supplier");
            System.out.println("2. View Suppliers");
            System.out.println("3. View Pending Orders");
            System.out.println("4. View Amount Owed");
            System.out.println("0. Back");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Contact: ");
                    String contact = sc.nextLine();

                    service.addSupplier(name, contact);
                    break;

                case 2:
                    service.showSupplier();
                    break;

                case 3:
                    System.out.print("Enter Supplier ID: ");
                    int sId = sc.nextInt();
                    service.showPendingOrder(sId);
                    break;

                case 4:
                    System.out.print("Enter Supplier ID: ");
                    int sId2 = sc.nextInt();
                    service.showAmountOwed(sId2);
                    break;

                case 0:
                    return;
            }
        }
    }
}
