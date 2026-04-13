package vehicles;

import components.Engine;
import components.Upholstery;
import components.Wheel;

public class Van extends Vehicle{
    public Van(String color, int numberOfSeats, double tareWeight,
               double maxAllowedWeight, Engine engine, Upholstery upholstery, Wheel wheel) {
        super(color, numberOfSeats, tareWeight, maxAllowedWeight, engine, upholstery, wheel);
    }
}

