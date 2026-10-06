// Single responsibility principle (SRP) states that a class should have only one reason to change, meaning it should have only one job or responsibility. This principle helps in making the code more maintainable and easier to understand.

//here's an example of a class that violates the SRP:
/**
 * Single Responsibility Principle (SRP)
 *
 * A class should have only ONE reason to change.
 *
 * In simple terms:
 * One class = one responsibility.
 */
public class SRP {

    // ============================================================
    // ❌ BAD DESIGN — SRP VIOLATION
    // ============================================================

    /**
     * This class has multiple responsibilities:
     *
     * 1. Invoice data
     * 2. Calculating total
     * 3. Saving to database
     * 4. Printing invoice
     * 5. Sending email
     *
     * Therefore, it has multiple reasons to change.
     */
    static class BadInvoice {

        private final String customerName;
        private final double amount;

        BadInvoice(String customerName, double amount) {
            this.customerName = customerName;
            this.amount = amount;
        }

        // Responsibility 1: Invoice calculation
        public double calculateTotal() {
            return amount * 1.18; // 18% tax
        }

        // Responsibility 2: Database persistence
        public void saveToDatabase() {
            System.out.println("Saving invoice to database...");
        }

        // Responsibility 3: Presentation
        public void printInvoice() {
            System.out.println(
                    "Invoice for " + customerName +
                    " | Total: " + calculateTotal()
            );
        }

        // Responsibility 4: Notification
        public void sendEmail() {
            System.out.println("Sending invoice email...");
        }
    }


    // ============================================================
    // ✅ GOOD DESIGN — SRP FOLLOWED
    // ============================================================

    /**
     * Responsibility:
     * Represent invoice data and calculate invoice total.
     *
     * Reason to change:
     * Invoice business rules change.
     */
    static class Invoice {

        private final String customerName;
        private final double amount;

        Invoice(String customerName, double amount) {
            this.customerName = customerName;
            this.amount = amount;
        }

        public double calculateTotal() {
            return amount * 1.18;
        }

        public String getCustomerName() {
            return customerName;
        }
    }


    /**
     * Responsibility:
     * Persist invoice data.
     *
     * Reason to change:
     * Database/storage mechanism changes.
     */
    static class InvoiceRepository {

        public void save(Invoice invoice) {
            System.out.println(
                    "Saving invoice for "
                            + invoice.getCustomerName()
                            + " to database..."
            );
        }
    }


    /**
     * Responsibility:
     * Present/print invoice.
     *
     * Reason to change:
     * Invoice presentation format changes.
     */
    static class InvoicePrinter {

        public void print(Invoice invoice) {
            System.out.println(
                    "================================"
            );

            System.out.println(
                    "Invoice for: "
                            + invoice.getCustomerName()
            );

            System.out.println(
                    "Total: "
                            + invoice.calculateTotal()
            );

            System.out.println(
                    "================================"
            );
        }
    }


    /**
     * Responsibility:
     * Send invoice notifications.
     *
     * Reason to change:
     * Notification mechanism changes.
     */
    static class InvoiceEmailService {

        public void send(Invoice invoice) {
            System.out.println(
                    "Sending invoice email to "
                            + invoice.getCustomerName()
            );
        }
    }


    // ============================================================
    // MAIN
    // ============================================================

    public static void main(String[] args) {

        Invoice invoice =
                new Invoice("Rohit", 1000);

        InvoiceRepository repository =
                new InvoiceRepository();

        InvoicePrinter printer =
                new InvoicePrinter();

        InvoiceEmailService emailService =
                new InvoiceEmailService();


        // Each class performs one responsibility.

        repository.save(invoice);

        printer.print(invoice);

        emailService.send(invoice);
    }
}