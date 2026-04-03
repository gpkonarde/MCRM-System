package dashboard;

import service.WorkOrderService;
import java.util.Scanner;

public class ProductionDashboard {

    WorkOrderService service = new WorkOrderService();
    Scanner sc = new Scanner(System.in);

    public void show() throws Exception {

        while (true) {
            System.out.println("\n=== PRODUCTION DASHBOARD ===");
            System.out.println("1. View Work Orders");
            System.out.println("2. View Production Lines");
            System.out.println("3. View Resources");
            System.out.println("4. Add Work Order");
            System.out.println("5. Assign Work Order");
            System.out.println("6. Auto Schedule All Pending Work Orders");
            System.out.println("0. Back");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    service.viewAllWorkOrders();
                    break;

                case 2:
                    service.viewProductionLines();
                    break;

                case 3:
                    service.viewResources();
                    break;

                case 4:
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();

                    System.out.print("Quantity: ");
                    int qty = sc.nextInt();

                    System.out.print("Priority: ");
                    sc.nextLine();
                    String pr = sc.next();

                    service.addWorkOrder(pid, qty, pr);
                    break;

                case 5:
                    System.out.print("Enter Work Order ID: ");
                    int wid = sc.nextInt();

                    service.assignWorkOrder(wid);
                    break;
                case 6:
                    service.autoScheduleWorkOrders();
                    break;
                case 0:
                    return;
            }
        }
    }
}
