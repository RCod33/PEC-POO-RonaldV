package AssemblyLine;

import Components.Engine;
import Components.Upholstery;
import Components.Wheel;

public class LineConfig {

    private final Engine engine;
    private final Upholstery upholstery;
    private final Wheel wheel;

    public LineConfig(Engine engine, Upholstery upholstery, Wheel wheel) {
        this.engine = engine;
        this.upholstery = upholstery;
        this.wheel = wheel;
    }

    public void validateLine() {
        if (engine == null)
            throw new IllegalStateException("La configuración de la línea no tiene motor asignado");
        if (upholstery == null)
            throw new IllegalStateException("La configuración de la línea no tiene tapicería asignada");
        if (wheel == null)
            throw new IllegalStateException("La configuración de la línea no tiene ruedas asignadas");
    }

    public Engine getEngine()         { return engine; }
    public Upholstery getUpholstery() { return upholstery; }
    public Wheel getWheel()           { return wheel; }

    @Override
    public String toString() {
        return "LineConfig{engine=" + engine +
                ", upholstery=" + upholstery +
                ", wheel=" + wheel + "}";
    }
}