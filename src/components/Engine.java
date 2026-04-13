package components;

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
}
