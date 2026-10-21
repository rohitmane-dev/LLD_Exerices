import java.util.ArrayList;
import java.util.List;

import factories.NowOrderFactory;
import factories.OrderFactory;
import factories.ScheduledOrderFactory;
import managers.OrderManager;
import managers.RestoManager;
import models.Cart;
import models.MenuItems;
import models.Order;
import models.OrderType;
import models.Resto;
import models.User;
import services.Notifications;
import strategies.PaymentStrategy;

/**
 * Facade. The only class the client (Main) talks to.
 *
 * It owns no data: restaurants live in RestoManager, orders in OrderManager,
 * the cart lives on the User. This class only sequences the use cases.
 */
public class Zomato {

    public Zomato() {
        initializeRestos();
    }

    private void initializeRestos() {
        RestoManager restoManager = RestoManager.getInstance();

        Resto bikaner = new Resto(1, "Bikaner", "Delhi", new ArrayList<MenuItems>());
        bikaner.addMenuItem(new MenuItems("P1", "Chole Bhature", "Punjabi classic", 120));
        bikaner.addMenuItem(new MenuItems("P2", "Samosa", "Fried potato pastry", 15));

        Resto haldiram = new Resto(2, "Haldiram", "Kolkata", new ArrayList<MenuItems>());
        haldiram.addMenuItem(new MenuItems("P1", "Raj Kachori", "Stuffed kachori chaat", 80));
        haldiram.addMenuItem(new MenuItems("P2", "Pav Bhaji", "Buttery mashed veg", 100));
        haldiram.addMenuItem(new MenuItems("P3", "Dhokla", "Steamed gram flour cake", 50));

        Resto saravana = new Resto(3, "Saravana Bhavan", "Chennai", new ArrayList<MenuItems>());
        saravana.addMenuItem(new MenuItems("P1", "Masala Dosa", "Crisp dosa with potato", 90));
        saravana.addMenuItem(new MenuItems("P2", "Idli Vada", "Steamed idli with vada", 60));
        saravana.addMenuItem(new MenuItems("P3", "Filter Coffee", "South Indian filter kaapi", 30));

        restoManager.addResto(bikaner);
        restoManager.addResto(haldiram);
        restoManager.addResto(saravana);
    }

    public List<Resto> searchRestos(String loc) {
        return RestoManager.getInstance().searchByLocation(loc);
    }

    public Resto getRestoById(int restoId) {
        return RestoManager.getInstance().getRestoById(restoId);
    }

    public void selectResto(User user, Resto resto) {
        user.getCart().setResto(resto);
    }

    public void addToCart(User user, String itemId) {
        Cart cart = user.getCart();
        Resto resto = cart.getResto();
        if (resto == null) {
            System.out.println("Please select a restaurant first.");
            return;
        }
        MenuItems item = resto.findItemById(itemId);
        if (item == null) {
            System.out.println(itemId + " is not on " + resto.getName() + "'s menu.");
            return;
        }
        cart.addItem(item);
    }

    public Order checkoutNow(User user, OrderType orderType, PaymentStrategy strategy) {
        return checkout(user, orderType, strategy, new NowOrderFactory());
    }

    public Order checkoutScheduled(User user, OrderType orderType, PaymentStrategy strategy,
                                   String scheduleTime) {
        return checkout(user, orderType, strategy, new ScheduledOrderFactory(scheduleTime));
    }

    /**
     * Public on purpose: a new kind of order (a party pre-order, say) is a new
     * OrderFactory passed in here, with no edit to this class. Open/Closed.
     */
    public Order checkout(User user, OrderType orderType, PaymentStrategy strategy,
                          OrderFactory orderFactory) {
        Cart cart = user.getCart();
        if (cart.isEmpty()) {
            System.out.println("Cart is empty - nothing to check out.");
            return null;
        }

        Order order = orderFactory.createOrder(user, cart.getResto(), cart.getItems(),
                strategy, cart.getTotalPrice(), orderType);
        OrderManager.getInstance().addOrder(order);
        return order;
    }

    public void payForOrder(User user, Order order) {
        if (order == null) {
            return;
        }
        if (order.processPayment()) {
            Notifications.notify(order);
            user.getCart().clear();
        }
    }

    public void printUserCart(User user) {
        Cart cart = user.getCart();
        System.out.println("\nCart of " + user.getName()
                + (cart.getResto() == null ? "" : " @ " + cart.getResto().getName()));
        System.out.println("------------------------------------");
        for (MenuItems item : cart.getItems()) {
            System.out.println(item.getItemId() + " : " + item.getItemName()
                    + " : Rs. " + item.getItemPrice());
        }
        System.out.println("------------------------------------");
        System.out.println("Grand total : Rs. " + cart.getTotalPrice());
    }

    public List<Order> getOrdersForUser(User user) {
        return OrderManager.getInstance().getOrdersForUser(user);
    }

    public void listAllOrders() {
        OrderManager.getInstance().listAllOrders();
    }
}
