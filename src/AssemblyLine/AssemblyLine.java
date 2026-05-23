package AssemblyLine;

import DataStore.DataStore;
import Observers.AssemblyLineObserver;
import Vehicles.Vehicle;
import Workers.Operator;

import java.util.*;

public class AssemblyLine {

    private final DataStore dataStore;
    private List<AssemblyModule> modules = new ArrayList<>();
    private List<AssemblyLineObserver> observers = new ArrayList<>();
    private Queue<Vehicle> pendingVehicles = new LinkedList<>();
    private Queue<Vehicle> finishedVehicles = new LinkedList<>();
    private LineConfig config;

    public AssemblyLine(DataStore dataStore) {
        this.dataStore = dataStore;
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

    public void setModule(AssemblyPhase phase, Operator operator) {
        AssemblyModule module = new AssemblyModule(phase, operator);
        modules.set(phase.ordinal(), module);
    }

    public List<AssemblyModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public void setConfig(LineConfig config) {
        this.config = config;
    }

    public LineConfig getConfig() {
        return config;
    }

    public void addVehicle(Vehicle v) {
        pendingVehicles.add(v);
    }

    public Collection<Vehicle> getPendingVehicles() {
        return Collections.unmodifiableCollection(pendingVehicles);
    }

    public Collection<Vehicle> getFinishedVehicles() {
        return Collections.unmodifiableCollection(finishedVehicles);
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

        for (int i = modules.size() - 1; i >= 0; --i) {

            AssemblyModule current = modules.get(i);
            boolean finished = current.work();

            if (!finished) continue;

            Vehicle car = current.getVehicle();
            AssemblyPhase phase = current.getPhase();

            // Polimorfismo: cada fase sabe qué componente consumir
            if (car != null) {
                phase.apply(car, config, dataStore, observers);
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
                status.add(names[i] + " -> " + v.getColor()
                        + " | Motor:" + (v.getEngine() != null ? "✔" : "✘")
                        + " | Tap:" + (v.getUpholstery() != null ? "✔" : "✘")
                        + " | Ruedas:" + (v.getWheel() != null ? "✔" : "✘"));
            }
        }

        return status;
    }
}