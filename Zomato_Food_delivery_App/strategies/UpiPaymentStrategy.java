package strategies;

public class UpiPaymentStrategy implements PaymentStrategy {
    private final String mobileNumber;

    public UpiPaymentStrategy(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid Rs. " + amount + " using UPI (" + mobileNumber + ")");
    }
}
