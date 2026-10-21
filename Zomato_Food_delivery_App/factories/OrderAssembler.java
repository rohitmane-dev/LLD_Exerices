package factories;

import java.util.List;
import models.DeliveryOrder;
import models.MenuItems;
import models.Order;
import models.OrderType;
import models.PickUpOrder;
import models.Resto;
import models.User;
import strategies.PaymentStrategy;

// Shared wiring for both factories. Without it, NowOrderFactory and
// ScheduledOrderFactory are byte-identical except for one line (the timestamp).
final class OrderAssembler {

    private OrderAssembler() {
    }

    static Order newOrderOfType(User user, Resto resto, OrderType orderType) {
        if (orderType == OrderType.DELIVERY) {
            DeliveryOrder deliveryOrder = new DeliveryOrder();
            deliveryOrder.setUserAddress(user.getAddress());
            return deliveryOrder;
        }
        PickUpOrder pickUpOrder = new PickUpOrder();
        pickUpOrder.setRestoAddress(resto.getLoc());
        return pickUpOrder;
    }

    static void fill(Order order, User user, Resto resto, List<MenuItems> items,
                     PaymentStrategy strategy, double totalCost, String scheduled) {
        order.setUser(user);
        order.setResto(resto);
        order.setItems(items);
        order.setStrategy(strategy);
        order.setTotalAmount(totalCost);
        order.setScheduled(scheduled);
    }
}
