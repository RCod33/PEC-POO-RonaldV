package AssemblyLine;

import Vehicles.Vehicle;
import Workers.Operator;

/**
 * Representa un módulo individual de la línea de ensamblaje.
 * Cada módulo procesa una fase concreta mediante un operario.
 */
public class AssemblyModule {
    private final AssemblyPhase phase;
    private Operator operator;
    private Vehicle vehicle;
    private int internalTimer = 0;
    // Tiempo necesario para completar el ensamblaje en este módulo
    private int timePerAssemblies;
    private boolean finished = false;

    public AssemblyModule(AssemblyPhase phase, Operator operator) {
        this.operator = operator;
        this.phase = phase;
        this.timePerAssemblies = operator.getWorkTime();
    }

    public AssemblyModule(AssemblyPhase phase) {
        this.phase = phase;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
        this.timePerAssemblies = operator.getWorkTime();
    }

    public AssemblyPhase getPhase() {
        return phase;
    }

    protected void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public boolean work() {
        if (vehicle == null) return false;
        if (operator == null) throw new IllegalStateException(
                "El módulo " + phase + " no tiene operario asignado"
        );
        if (finished) return true;
        internalTimer++;
        // El trabajo termina cuando se supera el tiempo requerido
        finished = internalTimer >= timePerAssemblies;
        return finished;
    }

    public boolean isFree() {
        return vehicle == null;
    }

    // Reinicia el estado interno del módulo para recibir otro vehículo
    protected Vehicle releaseVehicle() {
        Vehicle v = vehicle;
        vehicle = null;
        internalTimer = 0;
        finished = false;

        operator.updateWorker();
        timePerAssemblies = operator.getWorkTime();

        return v;
    }
}
