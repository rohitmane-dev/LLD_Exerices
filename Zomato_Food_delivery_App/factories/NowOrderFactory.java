package factories;

import java.util.List;
import models.MenuItems;
import models.Order;
import models.OrderType;
import models.Resto;
import models.User;
import strategies.PaymentStrategy;
import utils.TimeUtils;

public class NowOrderFactory implements OrderFactory {
    @Override
    public Order createOrder(User user, Resto resto, List<MenuItems> items,
                             PaymentStrategy strategy, double totalCost, OrderType orderType) {
        Order order = OrderAssembler.newOrderOfType(user, resto, orderType);
        OrderAssembler.fill(order, user, resto, items, strategy, totalCost, TimeUtils.getCurrentTime());
        return order;
    }
}
