package DataStore;

import AssemblyLine.AssemblyLine;
import Components.Engine;
import Components.Upholstery;
import Components.Wheel;
import Vehicles.Vehicle;
import Vehicles.VehicleType;
import Workers.*;
import AssemblyLine.LineConfig;

import java.util.*;

public class DataStore {

    private static DataStore instance;

    private List<Worker> workers = new ArrayList<>();

    private Stock<Vehicle> vehicles = new Stock<Vehicle>();

    private Stock<Engine> engines = new Stock<Engine>();

    private Stock<Wheel> wheels = new Stock<Wheel>();

    private Stock<Upholstery> upholsteries = new Stock<Upholstery>();

    private Map<VehicleType, AssemblyLine> assemblyLines = new HashMap<>();

    private DataStore() {
        for (VehicleType type : VehicleType.values()) {
            assemblyLines.put(type, new AssemblyLine(this));
        }
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // =========================
    // Workers
    // =========================

    public List<Worker> getWorkers() {
        return workers;
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public Worker searchWorkerByDNI(String DNI) {
        for (Worker worker : workers) {
            if (Objects.equals(worker.getDNI(), DNI)) {
                return worker;
            }
        }
        return null;
    }

    public List<Worker> searchWorkersByName(String name) {
        List<Worker> result = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker.getName().equalsIgnoreCase(name)
                    || worker.getLastName().equalsIgnoreCase(name)) {
                result.add(worker);
            }
        }
        return result;
    }

    public List<Operator> getOperators() {
        List<Operator> result = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker instanceof Operator) {
                result.add((Operator) worker);
            }
        }
        return result;
    }

    public List<Mechanic> getMechanics() {
        List<Mechanic> result = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker instanceof Mechanic) {
                result.add((Mechanic) worker);
            }
        }
        return result;
    }

    public List<PlantManager> getPlantManagers() {
        List<PlantManager> result = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker instanceof PlantManager) {
                result.add((PlantManager) worker);
            }
        }
        return result;
    }

    public List<SystemAdministrator> getSystemAdministrators() {
        List<SystemAdministrator> result = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker instanceof SystemAdministrator) {
                result.add((SystemAdministrator) worker);
            }
        }
        return result;
    }

    // =========================
    // Vehicles
    // =========================

    public Stock<Vehicle> getVehicles() {
        return vehicles;
    }

    // =========================
    // Engines
    // =========================

    public Stock<Engine> getEngines() {
        return engines;
    }

    // =========================
    // Wheels
    // =========================

    public Stock<Wheel> getWheels() {
        return wheels;
    }

    // =========================
    // Upholsteries
    // =========================

    public Stock<Upholstery> getUpholsteries() {
        return upholsteries;
    }

    // =========================
    // Assembly Lines
    // =========================

    public Map<VehicleType, AssemblyLine> getAssemblyLines() {
        return Collections.unmodifiableMap(assemblyLines);
    }

    public AssemblyLine getAssemblyLine(VehicleType type) {
        return assemblyLines.get(type);
    }

    public void modifyAssemblyLine(VehicleType type, LineConfig config) {
        AssemblyLine line = assemblyLines.get(type);
        if (line == null) {
            throw new IllegalStateException("No existe línea para el tipo: " + type);
        }
        line.setConfig(config);
    }
}