package factories;

import java.util.List;
import models.MenuItems;
import models.Order;
import models.OrderType;
import models.Resto;
import models.User;
import strategies.PaymentStrategy;

public class ScheduledOrderFactory implements OrderFactory {
    private final String scheduleTime;

    public ScheduledOrderFactory(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Override
    public Order createOrder(User user, Resto resto, List<MenuItems> items,
                             PaymentStrategy strategy, double totalCost, OrderType orderType) {
        Order order = OrderAssembler.newOrderOfType(user, resto, orderType);
        OrderAssembler.fill(order, user, resto, items, strategy, totalCost, scheduleTime);
        return order;
    }
}
