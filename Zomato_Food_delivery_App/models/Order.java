package models;

import java.util.List;
import strategies.PaymentStrategy;

public abstract class Order {
    private static int nextOrderId = 0;

    protected int orderId;
    protected User user;
    protected Resto resto;
    protected List<MenuItems> items;
    protected PaymentStrategy strategy;
    protected double totalAmount;
    protected String scheduled;

    protected Order() {
        this.orderId = ++nextOrderId;
        this.user = null;
        this.resto = null;
        this.items = List.of();
        this.strategy = null;
        this.totalAmount = 0.0;
        this.scheduled = "";
    }

    // Strategy pattern: Order never knows *how* payment happens.
    public boolean processPayment() {
        if (strategy == null) {
            System.out.println("Payment strategy not set. Please choose the payment mode first.");
            return false;
        }
        strategy.pay(totalAmount);
        return true;
    }

    // Each subclass answers for itself - no if/else on type anywhere else.
    public abstract OrderType getType();

    public int getOrderId() {
        return orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resto getResto() {
        return resto;
    }

    public void setResto(Resto resto) {
        this.resto = resto;
    }

    public List<MenuItems> getItems() {
        return items;
    }

    public void setItems(List<MenuItems> items) {
        this.items = items;
    }

    public PaymentStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }
}
