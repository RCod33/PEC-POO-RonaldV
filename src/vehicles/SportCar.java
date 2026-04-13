package vehicles;

import components.Engine;
import components.Upholstery;
import components.Wheel;

public class SportCar extends Vehicle {

    private static final int SEATS = 2;

    public SportCar(String color,
                    double tareWeight,
                    double maxAllowedWeight,
                    Engine engine,
                    Upholstery upholstery,
                    Wheel wheel) {

        super(color, SEATS, tareWeight, maxAllowedWeight, engine, upholstery, wheel);
    }
}

