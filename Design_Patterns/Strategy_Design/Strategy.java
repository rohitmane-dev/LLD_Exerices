
interface Talkable {
    void talk();
}

interface Walkable {
    void walk();
}

interface Flyable {
    void fly();
}

// Concrete strategies for talking
class NormalTalk implements Talkable {
    @Override
    public void talk() {
        System.out.println("Robot talks normally.");
    }
}

class NoTalk implements Talkable {
    @Override
    public void talk() {
        System.out.println("Robot cannot talk.");
    }
}

// Concrete strategies for walking
class NormalWalk implements Walkable {
    @Override
    public void walk() {
        System.out.println("Robot walks normally.");
    }
}

class NoWalk implements Walkable {
    @Override
    public void walk() {
        System.out.println("Robot cannot walk.");
    }
}

// Concrete strategies for flying
class NormalFly implements Flyable {
    @Override
    public void fly() {
        System.out.println("Robot flies normally.");
    }
}

class NoFly implements Flyable {
    @Override
    public void fly() {
        System.out.println("Robot cannot fly.");
    }
}

// Context class
class Robot {
    private Talkable talkBehavior;
    private Walkable walkBehavior;
    private Flyable flyBehavior;

    public Robot(
        Talkable talkBehavior,
        Walkable walkBehavior,
        Flyable flyBehavior
    ) {
        this.talkBehavior = talkBehavior;
        this.walkBehavior = walkBehavior;
        this.flyBehavior = flyBehavior;
    }

    public void performTalk() {
        talkBehavior.talk();
    }

    public void performWalk() {
        walkBehavior.walk();
    }

    public void performFly() {
        flyBehavior.fly();
    }

    // Change behavior at runtime
    public void setTalkBehavior(Talkable talkBehavior) {
        this.talkBehavior = talkBehavior;
    }

    public void setWalkBehavior(Walkable walkBehavior) {
        this.walkBehavior = walkBehavior;
    }

    public void setFlyBehavior(Flyable flyBehavior) {
        this.flyBehavior = flyBehavior;
    }
}

public class Strategy {
    public static void main(String[] args) {

        Robot robot = new Robot(
            new NormalTalk(),
            new NormalWalk(),
            new NoFly()
        );

        robot.performTalk();
        robot.performWalk();
        robot.performFly();

        System.out.println("--- Changing behavior ---");

        robot.setFlyBehavior(new NormalFly());
        robot.performFly();


        //similarly i can create a another robot with different behaviors
        System.out.println("--- Creating another robot with different behaviors ---");
        Robot robo = new Robot(
            new NoTalk(),
            new NoWalk(),
            new NormalFly()
        ); //this robot can fly but cannot talk or walk
        robo.performFly();
        robo.performTalk();
        robo.performWalk();


    }
}
