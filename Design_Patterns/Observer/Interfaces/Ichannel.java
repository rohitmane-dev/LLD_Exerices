package Interfaces;


public interface Ichannel {
    void subscribe(Isubscriber subscriber);
    void unsubscribe(Isubscriber subscriber);
    void notifySubscribers(String message);
}
