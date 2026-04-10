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
            System.out.println("3. Add Work Order");
            System.out.println("4. Assign Work Order");
            System.out.println("5. Auto Schedule All Pending Work Orders");
            System.out.println("6. Update WO Progress");
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
                    sc.nextLine();

                    System.out.print("Work Order Name: ");
                    String woName = sc.nextLine();

                    System.out.print("Product ID: ");
                    int prdId = sc.nextInt();

                    System.out.print("Production Line ID: ");
                    int plID = sc.nextInt();

                    sc.nextLine();

                    System.out.print("Priority (HIGH/MEDIUM/LOW): ");
                    String priority = sc.nextLine();

                    service.addWorkOrder(woName, prdId, plID, priority);
                    break;

                case 4:
                    System.out.println();
                    System.out.print("Enter Work Order ID: ");
                    int wid = sc.nextInt();

                    service.assignWorkOrder(wid);
                    break;
                case 5:
                    service.autoScheduleWorkOrders();
                    break;
                case 6:
                    service.updateWOProgress();
                case 0:
                    return;
            }
        }
    }
}
