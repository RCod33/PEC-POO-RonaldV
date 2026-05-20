package Components;

import Vehicles.Vehicle;

import java.util.Objects;

public class Engine {
    private final EngineType type; // tipo de motor

    private final int displacement; // cilindrada (cc)
    private final int power; // potencia (CV o kW)
    private final int numberOfCylinders;


    public Engine(EngineType type, int displacement, int power, int numberOfCylinders) {
        this.type = type;
        this.displacement = displacement;
        this.power = power;
        this.numberOfCylinders = numberOfCylinders;
    }

    public EngineType getType() {
        return type;
    }

    public int getDisplacement() {
        return displacement;
    }

    public int getPower() {
        return power;
    }

    public int getNumberOfCylinders() {
        return numberOfCylinders;
    }

    @Override
    public String toString() {
        return type + " " + displacement + "cc " + power + "hp " + numberOfCylinders + " cylinders";
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Engine)) {
            return false;
        }

        Engine other = (Engine) obj;

        return  type == other.type
                && displacement == other.displacement
                && power == other.power
                && numberOfCylinders == other.numberOfCylinders;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, displacement, power, numberOfCylinders);
    }
}
