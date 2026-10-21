package factories;

import java.util.List;
import models.MenuItems;
import models.Order;
import models.OrderType;
import models.Resto;
import models.User;
import strategies.PaymentStrategy;

// Factory Method: one creator per *timing* of the order (now vs scheduled).
public interface OrderFactory {
    Order createOrder(User user, Resto resto, List<MenuItems> items,
                      PaymentStrategy strategy, double totalCost, OrderType orderType);
}
