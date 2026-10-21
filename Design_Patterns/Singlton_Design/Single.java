class single {

    // EAGER INITIALIZATION:
    // The instance is created when the class is loaded.
    // It is inherently thread-safe because class initialization
    // is handled by the JVM.
    private static final single instance = new single();

    // Private constructor prevents object creation from outside.
    private single() {
    }

    // EAGER INITIALIZATION:
    // No locking is required because the instance already exists.
    public static single getInstance() {
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