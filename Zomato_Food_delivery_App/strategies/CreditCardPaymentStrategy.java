package strategies;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    private final String cardNumber;

    public CreditCardPaymentStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        // Only the last 4 digits - never print a full card number.
        String masked = "****" + cardNumber.substring(Math.max(0, cardNumber.length() - 4));
        System.out.println("Paid Rs. " + amount + " using Credit Card (" + masked + ")");
    }
}
