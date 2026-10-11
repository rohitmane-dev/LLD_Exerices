
interface Burger {
    void prepare();
}

// Plain burger products
class BasicBurger implements Burger {
    public void prepare() {
        System.out.println("Preparing Basic Burger");
    }
}

class StandardBurger implements Burger {
    public void prepare() {
        System.out.println("Preparing Standard Burger");
    }
}

class PremiumBurger implements Burger {
    public void prepare() {
        System.out.println("Preparing Premium Burger");
    }
}

// Wheat burger products
class BasicWheatBurger implements Burger {
    public void prepare() {
        System.out.println("Preparing Basic Wheat Burger");
    }
}

class StandardWheatBurger implements Burger {
    public void prepare() {
        System.out.println("Preparing Standard Wheat Burger");
    }
}

class PremiumWheatBurger implements Burger {
    public void prepare() {
        System.out.println("Preparing Premium Wheat Burger");
    }
}

// Abstract factory
abstract class BurgerFactory {
    public abstract Burger createBurger(String type);
}

// Concrete factory 1: Plain burgers
class SinghBurger extends BurgerFactory {
    @Override
    public Burger createBurger(String type) {
        switch (type.toLowerCase()) {
            case "basic":
                return new BasicBurger();
            case "standard":
                return new StandardBurger();
            case "premium":
                return new PremiumBurger();
            default:
                throw new IllegalArgumentException(
                    "Unknown plain burger: " + type
                );
        }
    }
}

// Concrete factory 2: Wheat burgers
class KingBurger extends BurgerFactory {
    @Override
    public Burger createBurger(String type) {
        switch (type.toLowerCase()) {
            case "basic":
                return new BasicWheatBurger();
            case "standard":
                return new StandardWheatBurger();
            case "premium":
                return new PremiumWheatBurger();
            default:
                throw new IllegalArgumentException(
                    "Unknown wheat burger: " + type
                );
        }
    }
}

// Client
public class SimpleF {
    public static void main(String[] args) {
        BurgerFactory plainFactory = new SinghBurger();
        BurgerFactory wheatFactory = new KingBurger();

        Burger burger1 = plainFactory.createBurger("basic");
        burger1.prepare();

        Burger burger2 = wheatFactory.createBurger("premium");
        burger2.prepare();
    }
}
