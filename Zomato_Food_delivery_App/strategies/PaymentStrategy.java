package strategies;

//this is the interface made for implementing the startegy pattern for the 
//payments so that the paymenthod should be selected at the run time.
public interface PaymentStrategy { 
    void pay(double amount);
}
