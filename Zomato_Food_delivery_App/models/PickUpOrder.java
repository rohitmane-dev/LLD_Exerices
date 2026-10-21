package models;

public class PickUpOrder extends Order {
    private String restoAddress;

    public PickUpOrder() {
        this.restoAddress = "";
    }

    @Override
    public OrderType getType() {
        return OrderType.PICKUP;
    }

    public String getRestoAddress() {
        return restoAddress;
    }

    public void setRestoAddress(String restoAddress) {
        this.restoAddress = restoAddress;
    }
}
