import ConcreteClasses.Channel;
import ConcreteClasses.Subscriber;


public class Observer{

    public static void main(String[] args) {
        Channel channel = new Channel("TechWorld");
        
        Subscriber subscriber1 = new Subscriber("Alice");
        Subscriber subscriber2 = new Subscriber("Bob");
        
        channel.subscribe(subscriber1);
        channel.subscribe(subscriber2);
        
        channel.uploadVideo("Design Patterns in Java");
        
        channel.unsubscribe(subscriber1);
        
        channel.uploadVideo("Observer Pattern Explained");
    }
}


//Short COmmand to run it :
//cd Design_Patterns/Observer
// javac -d out Observer.java && java -cp out Observer
