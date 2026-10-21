package services;

import java.util.List;
import models.MenuItems;
import models.Order;

public class Notifications { //this gives the indetailed notification about order

    public static void notify(Order order) {
        System.out.println("\nNotification: New " + order.getType() + " order placed!");
        System.out.println("---------------------------------------------");
        System.out.println("Order ID    : " + order.getOrderId());
        System.out.println("Customer    : " + order.getUser().getName());
        System.out.println("Address     : " + order.getUser().getAddress());
        System.out.println("Restaurant  : " + order.getResto().getName()
                + " (" + order.getResto().getLoc() + ")");
        System.out.println("Items Ordered:");

        List<MenuItems> items = order.getItems();
        for (MenuItems item : items) {
            System.out.println("   - " + item.getItemName() + " (Rs. " + item.getItemPrice() + ")");
        }

        System.out.println("Total       : Rs. " + order.getTotalAmount());
        System.out.println("Scheduled   : " + order.getScheduled());
        System.out.println("Payment     : Done");
        System.out.println("---------------------------------------------");
    }
}
