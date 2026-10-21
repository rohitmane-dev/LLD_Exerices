package managers;

import java.util.ArrayList;
import java.util.List;
import models.Order;
import models.User;

// Singleton: the single place orders live. The facade holds no order list of its own.
public class OrderManager {
    private static OrderManager instance;

    private final List<Order> orders = new ArrayList<>();

    private OrderManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public boolean cancelOrder(Order order) {
        return orders.remove(order);
    }

    public List<Order> getOrdersForUser(User user) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getUser().getUserId() == user.getUserId()) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> getAllOrders() {
        return List.copyOf(orders);
    }

    public void listAllOrders() {
        System.out.println("\n--- All Orders ---");
        for (Order order : orders) {
            System.out.println("#" + order.getOrderId()
                    + " | " + order.getType()
                    + " | " + order.getUser().getName()
                    + " | " + order.getResto().getName()
                    + " | Total: Rs. " + order.getTotalAmount()
                    + " | At: " + order.getScheduled());
        }
    }
}
