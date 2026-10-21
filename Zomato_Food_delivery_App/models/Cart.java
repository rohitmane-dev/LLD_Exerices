package models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Resto resto;
    private final List<MenuItems> items = new ArrayList<>();

    public Cart() {
        this.resto = null;
    }

    public void addItem(MenuItems item) {
        if (resto == null) {
            System.out.println("Cart: select a restaurant before adding items.");
            return;
        }
        items.add(item);
    }

    public void removeItem(MenuItems item) {
        items.remove(item);
    }

    // Returns a snapshot, not the live list: otherwise clear() would also wipe the
    // item list of every Order that was built from this cart.
    public List<MenuItems> getItems() {
        return List.copyOf(items);
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (MenuItems item : items) {
            total += item.getItemPrice();
        }
        return total;
    }

    public boolean isEmpty() {
        return resto == null || items.isEmpty();
    }

    public void clear() {
        items.clear();
        resto = null;
    }

    public Resto getResto() {
        return resto;
    }

    public void setResto(Resto resto) {
        this.resto = resto;
    }
}
