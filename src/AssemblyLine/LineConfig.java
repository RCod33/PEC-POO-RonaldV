package AssemblyLine;

import Components.Engine;
import Components.Upholstery;
import Components.Wheel;

public class LineConfig {
    private Engine engine;
    private Upholstery upholstery;
    private Wheel wheel;

    public LineConfig(Engine engine, Upholstery upholstery, Wheel wheel) {
        this.engine = engine;
        this.upholstery = upholstery;
        this.wheel = wheel;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Upholstery getUpholstery() {
        return upholstery;
    }

    public void setUpholstery(Upholstery upholstery) {
        this.upholstery = upholstery;
    }

    public Wheel getWheel() {
        return wheel;
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }
}
