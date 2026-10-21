import java.util.List;

import models.Order;
import models.OrderType;
import models.Resto;
import models.User;
import strategies.CreditCardPaymentStrategy;
import strategies.UpiPaymentStrategy;

/**
 * Client / composition root. Knows only the Zomato facade and the models it
 * passes in - never a manager, never a factory.
 *
 * Run with -ea to also execute the asserts at the end of each flow.
 */
public class Main {
    public static void main(String[] args) {
        Zomato zomato = new Zomato();

        // ---------- Flow 1: deliver now, pay by UPI ----------
        User aditya = new User(101, "Aditya", "Delhi");
        System.out.println("User: " + aditya.getName() + " is active.");

        // Guard check: nothing can go in the cart before a restaurant is chosen.
        zomato.addToCart(aditya, "P1");

        List<Resto> delhiRestos = zomato.searchRestos("Delhi");
        System.out.println("\nRestaurants in Delhi:");
        for (Resto resto : delhiRestos) {
            System.out.println(" - " + resto.getName());
        }
        assert delhiRestos.size() == 1 : "expected exactly one Delhi restaurant";

        zomato.selectResto(aditya, delhiRestos.get(0));
        zomato.addToCart(aditya, "P1");
        zomato.addToCart(aditya, "P2");
        zomato.addToCart(aditya, "P9");  // not on the menu
        zomato.printUserCart(aditya);
        assert aditya.getCart().getItems().size() == 2 : "bad cart size";
        assert aditya.getCart().getTotalPrice() == 135.0 : "bad cart total";

        Order deliveryOrder = zomato.checkoutNow(aditya, OrderType.DELIVERY,
                new UpiPaymentStrategy("9876543210"));
        zomato.payForOrder(aditya, deliveryOrder);

        assert deliveryOrder.getType() == OrderType.DELIVERY : "wrong order type";
        assert deliveryOrder.getTotalAmount() == 135.0 : "wrong order total";
        assert aditya.getCart().isEmpty() : "cart should be cleared after payment";
        // The order keeps its items even though the cart was cleared.
        assert deliveryOrder.getItems().size() == 2 : "order items were aliased to the cart";

        // Checking out an emptied cart must not create an order.
        System.out.println();
        assert zomato.checkoutNow(aditya, OrderType.DELIVERY,
                new UpiPaymentStrategy("9876543210")) == null : "empty cart made an order";

        // ---------- Flow 2: scheduled pickup, pay by card ----------
        User bob = new User(102, "Bob", "Kolkata");
        System.out.println("\nUser: " + bob.getName() + " is active.");

        List<Resto> kolkataRestos = zomato.searchRestos("kolkata");  // search is case-insensitive
        zomato.selectResto(bob, kolkataRestos.get(0));
        zomato.addToCart(bob, "P2");
        zomato.addToCart(bob, "P3");
        zomato.printUserCart(bob);

        Order pickUpOrder = zomato.checkoutScheduled(bob, OrderType.PICKUP,
                new CreditCardPaymentStrategy("4111111111111234"), "Sat Oct 25 20:00:00 2026");
        zomato.payForOrder(bob, pickUpOrder);

        assert pickUpOrder.getType() == OrderType.PICKUP : "wrong order type";
        assert pickUpOrder.getScheduled().equals("Sat Oct 25 20:00:00 2026") : "schedule lost";
        assert zomato.getOrdersForUser(bob).size() == 1 : "order not registered for Bob";

        // ---------- Both orders live in the one OrderManager ----------
        zomato.listAllOrders();
        assert zomato.getOrdersForUser(aditya).size() == 1 : "Aditya's order missing";

        System.out.println("\nAll self-checks passed.");
    }
}
