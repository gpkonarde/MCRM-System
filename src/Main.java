package src;

import dashboard.InventoryDashboard;
import dashboard.SupplierDashboard;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n====== MCRM SYSTEM ======");
            System.out.println("1. Supplier (Sourcing)");
            System.out.println("2. Inventory");
            System.out.println("3. Scheduling");
            System.out.println("4. Production");
            System.out.println("5. Dispatch");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            try {
                switch (choice) {

                    case 1:
                        // Supplier Dashboard
                        SupplierDashboard supplierDashboard = new SupplierDashboard();
                        supplierDashboard.show();
                        break;

                    case 2:
                        // Inventory Dashboard
                        new InventoryDashboard().show();
                        break;

                    case 3:
                        System.out.println("Scheduling Module (Coming Soon)");
                        break;

                    case 4:
                        System.out.println("Production Module (Coming Soon)");
                        break;

                    case 5:
                        System.out.println("Dispatch Module (Coming Soon)");
                        break;

                    case 0:
                        System.out.println("Exiting MCRM System...");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
