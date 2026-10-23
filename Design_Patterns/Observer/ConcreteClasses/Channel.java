package ConcreteClasses;

import java.util.*;
import Interfaces.Ichannel;
import Interfaces.Isubscriber;


public class Channel implements Ichannel {
    private String channelName;
    private List<Isubscriber> subscribers = new ArrayList<>();
    
    public Channel(String channelName) {
        this.channelName = channelName;
    }

    @Override
    
    public void subscribe(Isubscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
            System.out.println(subscriber.getName() + " has subscribed to " + channelName);
        }else {
            System.out.println(subscriber.getName() + " is already subscribed to " + channelName);
        }
        
    }

    @Override
    public void unsubscribe(Isubscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
            System.out.println(subscriber.getName() + " has unsubscribed from " + channelName);
        } else {
            System.out.println(subscriber.getName() + " is not subscribed to " + channelName);
        }
        
    }

    @Override
    public void notifySubscribers(String message) {
        for (Isubscriber subscriber : subscribers) {
            subscriber.update(message);
        }
    }

    
    public void uploadVideo(String videoTitle) {
        System.out.println("New video uploaded: " + videoTitle);
        notifySubscribers(videoTitle);
    }

}