package AssemblyLine;

import DataStore.DataStore;
import Observers.AssemblyLineObserver;
import Vehicles.Vehicle;
import Workers.Operator;

import java.util.*;

public class AssemblyLine {

    private List<AssemblyModule> modules = new ArrayList<>();
    private List<AssemblyLineObserver> observers = new ArrayList<>();
    private Queue<Vehicle> pendingVehicles = new LinkedList<>();
    private Queue<Vehicle> finishedVehicles = new LinkedList<>();
    private LineConfig config;

    public AssemblyLine() {
        for (AssemblyPhase phase : AssemblyPhase.values()) {
            modules.add(new AssemblyModule(phase));
        }
    }

    public void addObserver(AssemblyLineObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AssemblyLineObserver observer) {
        observers.remove(observer);
    }

    public void setModule (AssemblyPhase phase, Operator operator) {
        AssemblyModule module = new AssemblyModule(phase, operator);
        modules.set(phase.ordinal(), module);
    }

    public List<AssemblyModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public void setConfig(LineConfig config) {
        this.config = config;
    }

    public void addVehicle(Vehicle v) {
        pendingVehicles.add(v);
    }

    public Queue<Vehicle> getPendingVehicles() {
        return this.pendingVehicles;
    }

    public Queue<Vehicle> getFinishedVehicles() {
        return finishedVehicles;
    }

    public void updateLine() {
        config.validateLine();

        for (AssemblyModule module : modules) {
            if (module.getOperator() == null) {
                throw new IllegalStateException(
                        "El módulo " + module.getPhase() + " no tiene operario asignado"
                );
            }
        }

        DataStore ds = DataStore.getInstance();

        for (int i = modules.size() - 1; i >= 0; --i) {

            AssemblyModule current = modules.get(i);
            boolean finished = current.work();

            if (!finished) continue;

            Vehicle car = current.getVehicle();
            AssemblyPhase phase = current.getPhase();

            if (car != null) {
                switch (phase) {
                    case MOTOR:
                        if (ds.getEngines().getStock(config.getEngine()) <= 0)
                            throw new IllegalStateException("No hay motores en stock");
                        ds.getEngines().remove(config.getEngine());
                        car.setEngine(config.getEngine());
                        for (AssemblyLineObserver o : observers)
                            o.onComponentConsumed("Motor",
                                    ds.getEngines().getStock(config.getEngine()));
                        break;
                    case TAPICERIA:
                        if (ds.getUpholsteries().getStock(config.getUpholstery()) <= 0)
                            throw new IllegalStateException("No hay tapicerías en stock");
                        ds.getUpholsteries().remove(config.getUpholstery());
                        car.setUpholstery(config.getUpholstery());
                        for (AssemblyLineObserver o : observers)
                            o.onComponentConsumed("Tapicería",
                                    ds.getUpholsteries().getStock(config.getUpholstery()));
                        break;
                    case RUEDAS:
                        if (ds.getWheels().getStock(config.getWheel()) <= 0)
                            throw new IllegalStateException("No hay ruedas en stock");
                        ds.getWheels().remove(config.getWheel());
                        car.setWheel(config.getWheel());
                        for (AssemblyLineObserver o : observers)
                            o.onComponentConsumed("Ruedas",
                                    ds.getWheels().getStock(config.getWheel()));
                        break;
                }
            }

            if (i == modules.size() - 1) {
                Vehicle finishedCar = current.releaseVehicle();
                finishedVehicles.add(finishedCar);
                for (AssemblyLineObserver o : observers)
                    o.onVehicleFinished(finishedCar);
                continue;
            }

            AssemblyModule next = modules.get(i + 1);

            if (next.isFree()) {
                Vehicle v = current.releaseVehicle();
                next.setVehicle(v);
                for (AssemblyLineObserver o : observers)
                    o.onVehicleAdvanced(v, phase, next.getPhase());
            }
        }

        if (modules.getFirst().isFree() && !pendingVehicles.isEmpty()) {
            Vehicle v = pendingVehicles.poll();
            modules.getFirst().setVehicle(v);
            for (AssemblyLineObserver o : observers)
                o.onVehicleEntered(v);
        }
    }

    public List<String> getStatus() {

        List<String> status = new ArrayList<>();

        String[] names = {"CHASIS", "MOTOR", "TAPICERIA", "RUEDAS"};

        for (int i = 0; i < modules.size(); i++) {
            AssemblyModule m = modules.get(i);

            if (m == null || m.getVehicle() == null) {
                status.add(names[i] + " -> VACIO");
            } else {
                Vehicle v = m.getVehicle();

                status.add(names[i] + " -> " +
                        v.getColor()
                        + " | Motor:" + (v.getEngine() != null ? "✔" : "✘")
                        + " | Tap:" + (v.getUpholstery() != null ? "✔" : "✘")
                        + " | Ruedas:" + (v.getWheel() != null ? "✔" : "✘"));
            }
        }

        return status;
    }


}
