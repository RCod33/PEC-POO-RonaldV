package AssemblyLine;

import DataStore.DataStore;
import Observers.AssemblyLineObserver;
import Vehicles.Vehicle;
import Vehicles.VehicleType;
import Workers.Operator;

import java.util.*;

public class AssemblyLine {

    private final DataStore dataStore;
    private final VehicleType lineType;          // tipo de cadena (para marcar el vehículo)
    private List<AssemblyModule> modules = new ArrayList<>();
    private List<AssemblyLineObserver> observers = new ArrayList<>();
    private Queue<Vehicle> pendingVehicles = new LinkedList<>();
    private Queue<Vehicle> finishedVehicles = new LinkedList<>();
    private LineConfig config;


    // Inicializa automáticamente todos los módulos de la línea
    // siguiendo el orden definido en AssemblyPhase
    public AssemblyLine(DataStore dataStore, VehicleType lineType) {
        this.dataStore = dataStore;
        this.lineType  = lineType;
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
        modules.set(phase.ordinal(), new AssemblyModule(phase, operator));
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

    /**
     * Ejecuta un ciclo completo de simulación de la línea de ensamblaje.
     *
     * Cada módulo trabaja sobre su vehículo actual y, si termina,
     * el vehículo avanza al siguiente módulo o sale de la línea.
     */
    public void updateLine() {
        config.validateLine();

        for (AssemblyModule module : modules) {
            if (module.getOperator() == null) {
                throw new IllegalStateException(
                        "El módulo " + module.getPhase() + " no tiene operario asignado"
                );
            }
        }

        // Se recorre de atrás hacia adelante para evitar sobrescribir
        // vehículos al moverlos entre módulos en el mismo ciclo
        for (int i = modules.size() - 1; i >= 0; --i) {

            AssemblyModule current = modules.get(i);
            boolean finished = current.work();

            if (!finished) continue;

            Vehicle car   = current.getVehicle();
            AssemblyPhase phase = current.getPhase();

            if (car != null) {
                // Aplica la lógica específica de la fase actual al vehículo
                phase.apply(car, config, dataStore, observers);
            }

            // Último módulo: el vehículo sale de la línea de ensamblaje
            // Marca el vehículo como ensamblado y lo mueve al historial finalizado
            if (i == modules.size() - 1) {
                Vehicle finishedCar = current.releaseVehicle();
                // Registrar fecha y línea de ensamblaje
                finishedCar.markAssembled(lineType);
                finishedVehicles.add(finishedCar);
                for (AssemblyLineObserver o : observers)
                    o.onVehicleFinished(finishedCar);
                continue;
            }

            // Avanza el vehículo al siguiente módulo si está libre
            AssemblyModule next = modules.get(i + 1);
            if (next.isFree()) {
                Vehicle v = current.releaseVehicle();
                next.setVehicle(v);
                for (AssemblyLineObserver o : observers)
                    o.onVehicleAdvanced(v, phase, next.getPhase());
            }
        }

        // Introduce un nuevo vehículo en la línea si el primer módulo está libre
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
                        + " | Motor:"  + (v.getEngine()     != null ? "✔" : "✘")
                        + " | Tap:"    + (v.getUpholstery() != null ? "✔" : "✘")
                        + " | Ruedas:" + (v.getWheel()      != null ? "✔" : "✘"));
            }
        }
        return status;
    }

    public void clearFinishedVehicles() {
        finishedVehicles.clear();
    }

}