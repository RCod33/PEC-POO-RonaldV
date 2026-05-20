package DataStore;

import AssemblyLine.AssemblyLine;
import Components.Engine;
import Components.Upholstery;
import Components.Wheel;
import Vehicles.Vehicle;
import Vehicles.VehicleType;
import Workers.Worker;

import java.util.*;

public class DataStore {

    private List<Worker> workers = new ArrayList<>();

    private Stock<Vehicle> vehicles = new Stock<Vehicle>();

    private Stock<Engine> engines = new Stock<Engine>();

    private Stock<Wheel> wheels = new Stock<Wheel>();

    private Stock<Upholstery> upholsteries = new Stock<Upholstery>();

    private Map<VehicleType, AssemblyLine> assemblyLines = new HashMap<>();

    public DataStore() {

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

    public Worker searchWorker(String DNI) {

        for (Worker worker : workers) {

            if (Objects.equals(worker.getDNI(), DNI)) {
                return worker;
            }
        }

        return null;
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
        return assemblyLines;
    }

    public AssemblyLine getAssemblyLine(VehicleType type) {
        return assemblyLines.get(type);
    }
}