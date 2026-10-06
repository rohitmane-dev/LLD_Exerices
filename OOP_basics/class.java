// //Expercies 1 : for creating a class and object

// Design a Car class that remembers both its identity and its current speed as it is driven. The brand and model stay the same for the lifetime of the object, while the speed changes after every acceleration or braking operation.

// Implement the Car class:

// Car(String brand, String model) creates a car with the given brand and model. Every new car starts at 0 km/h.
// int accelerate(int amount) increases the current speed by amount, stores the result, and returns the new speed.
// int brake(int amount) decreases the current speed by amount, stores the result, and returns the new speed. If braking would make the speed negative, set it to 0 instead.
// int getSpeed() returns the car's current speed without changing it.
// String describe() returns the latest state in the exact format "<brand> <model> at <speed> km/h".
// All method calls operate on the same object. For example, accelerating by 20 and then by 15 produces a speed of 35, not 15. Braking by more than 35 then brings the car to a stop at 0.


// Implement class Car

class Car {

    private String brand ;
    private String model ;
    private int speed ;


    public Car(String brand, String model) {
        this.brand = brand;
        this.model = model;
        this.speed =0;



        
    }

    public int accelerate(int amount) {
        speed += amount;
        return speed;
    }

    public int brake(int amount) {

        if(speed - amount < 0)return speed =0;
        return speed = speed - amount;
        
    }

    public int getSpeed() {
        return speed;
    }

    public String describe() {
        String s = brand + " "+ model + " at " + speed +" km/h";
        return s;
        
    }

}

/**
 * Your Car object will be instantiated and called as such:
 * Car obj = new Car(brand, model);
 * int param_1 = obj.accelerate(amount);
 * int param_2 = obj.brake(amount);
 * int param_3 = obj.getSpeed();
 * String param_4 = obj.describe();
 */
