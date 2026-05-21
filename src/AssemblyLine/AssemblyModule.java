package AssemblyLine;

import Vehicles.Vehicle;
import Workers.Operator;

public class AssemblyModule {
    private final AssemblyPhase phase;
    private Operator operator;
    private Vehicle vehicle;
    private int internalTimer = 0;
    private int timePerAssemblies;
    private boolean finished = false;

    public AssemblyModule(AssemblyPhase phase, Operator operator) {
        this.operator = operator;
        this.phase = phase;
        this.timePerAssemblies = operator.getTimePerAssemblies();
    }

    public AssemblyModule(AssemblyPhase phase) {
        this.phase = phase;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
        this.timePerAssemblies = operator.getTimePerAssemblies();
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
        finished = internalTimer > timePerAssemblies;
        return finished;
    }

    public boolean isFree() {
        return vehicle == null;
    }


    public Vehicle releaseVehicle() {
        Vehicle v = vehicle;
        vehicle = null;
        internalTimer = 0;
        finished = false;

        operator.updateOperator();
        timePerAssemblies = operator.getTimePerAssemblies();

        return v;
    }
}
