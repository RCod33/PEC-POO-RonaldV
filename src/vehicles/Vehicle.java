package vehicles;

import components.Engine;
import components.Upholstery;
import components.Wheel;

abstract  public class Vehicle {

    private final String color;
    private final int numberOfSeats;
    private final double tareWeight;
    private final double maxAllowedWeight;

    private final Engine engine;
    private final Upholstery upholstery;
    private final Wheel wheel;


    public Vehicle(String color, int numberOfSeats, double tareWeight, double maxAllowedWeight, Engine engine, Upholstery upholstery, Wheel wheel) {
        this.color = color;
        this.numberOfSeats = numberOfSeats;
        this.tareWeight = tareWeight;
        this.maxAllowedWeight = maxAllowedWeight;
        this.engine = engine;
        this.upholstery = upholstery;
        this.wheel = wheel;
    }

    public String getColor() {
        return color;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public double getTareWeight() {
        return tareWeight;
    }

    public double getMaxAllowedWeight() {
        return maxAllowedWeight;
    }

    public Engine getEngine() {
        return engine;
    }

    public Upholstery getUpholstery() {
        return upholstery;
    }

    public Wheel getWheel() {
        return wheel;
    }

}
