package models;

public class DeliveryOrder extends Order {
    private String userAddress;

    public DeliveryOrder() {
        this.userAddress = "";
    }

    @Override
    public OrderType getType() {
        return OrderType.DELIVERY;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
