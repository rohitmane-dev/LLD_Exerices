package ConcreteClasses;

import Interfaces.Isubscriber;


public class Subscriber implements Isubscriber {
    private String name;

    public Subscriber(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received notification: " + message);
    }

    @Override
    public String getName() {
        return name;
    }
}
