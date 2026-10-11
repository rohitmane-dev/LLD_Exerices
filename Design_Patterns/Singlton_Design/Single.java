
class single {
    private static single instance = null;

    private single() {
        // private constructor to prevent instantiation
    }

    public static single getInstance() {
        if (instance == null) {
            instance = new single();
        }
        return instance;
    }
}

public class Single {
    public static void main(String[] args) {
        single obj1 = single.getInstance();
        single obj2 = single.getInstance();

        if (obj1 == obj2) {
            System.out.println("Both objects are the same instance.");
        } else {
            System.out.println("Objects are different instances.");
        }
    }
}
