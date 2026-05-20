package Vehicles;

import Components.Engine;
import Components.Upholstery;
import Components.Wheel;

import java.util.Objects;

public class Vehicle {

    private final VehicleType type;
    private final String color;
    private final int numberOfSeats;
    private final double tareWeight;
    private final double maxAllowedWeight;

    private Engine engine;
    private Upholstery upholstery;
    private Wheel wheel;


    public Vehicle(VehicleType type, String color, int numberOfSeats, double tareWeight, double maxAllowedWeight) {
        this.type = type;
        this.color = color;
        this.numberOfSeats = numberOfSeats;
        this.tareWeight = tareWeight;
        this.maxAllowedWeight = maxAllowedWeight;
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

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setUpholstery(Upholstery upholstery) {
        this.upholstery = upholstery;
    }

    public Upholstery getUpholstery() {
        return upholstery;
    }


    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }

    public Wheel getWheel() {
        return wheel;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Vehicle)) {
            return false;
        }

        Vehicle other = (Vehicle) obj;

        return Objects.equals(color, other.color)
                && type == other.type
                && numberOfSeats == other.numberOfSeats
                && tareWeight == other.tareWeight
                && maxAllowedWeight == other.maxAllowedWeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, numberOfSeats, tareWeight, maxAllowedWeight);
    }

}
