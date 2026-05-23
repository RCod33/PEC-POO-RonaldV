package Scheduler;

import AssemblyLine.AssemblyLine;
import AssemblyLine.AssemblyPhase;
import AssemblyLine.AssemblyModule;
import AssemblyLine.LineConfig;
import Components.Engine;
import Components.Upholstery;
import Components.Wheel;
import DataStore.DataStore;
import Vehicles.VehicleType;
import Workers.Operator;
import DataStore.Stock;

import java.util.*;

public class Scheduler {

    private DataStore dataStore;

    public Scheduler(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void validateStock() {

        Map<Engine, Integer> requiredEngines = new HashMap<>();
        Map<Upholstery, Integer> requiredUpholsteries = new HashMap<>();
        Map<Wheel, Integer> requiredWheels = new HashMap<>();

        // Calcula piezas necesarias por configuración de línea
        for (VehicleType vehicleType : VehicleType.values()) {

            AssemblyLine line = dataStore.getAssemblyLine(vehicleType);
            LineConfig config = line.getConfig();

            if (config == null)
                throw new IllegalStateException("La configuracion de la cadena '" + vehicleType + "' no puede estar vacia");
            config.validateLine();

            int pending = line.getPendingVehicles().size();
            addRequired(requiredEngines, config.getEngine(), pending);
            addRequired(requiredUpholsteries, config.getUpholstery(), pending);
            addRequired(requiredWheels, config.getWheel(), pending * 4);
        }

        validatePartStock(requiredEngines, dataStore.getEngines(), "motor");
        validatePartStock(requiredUpholsteries, dataStore.getUpholsteries(), "tapiceria");
        validatePartStock(requiredWheels, dataStore.getWheels(), "rueda");
    }

    private <T> void addRequired(Map<T, Integer> map, T item, int amount) {
        map.put(item, map.getOrDefault(item, 0) + amount);
    }

    private <T> void validatePartStock(
            Map<T, Integer> required,
            Stock<T> stock,
            String partName
    ) {

        for (Map.Entry<T, Integer> it : required.entrySet()) {

            T model = it.getKey();
            int requiredAmount = it.getValue();
            int available = stock.getStock(model);

            if (available < requiredAmount) {
                throw new IllegalStateException(
                        "Stock insuficiente de " + partName +
                                "\nModelo: " + model +
                                "\nNecesarios: " + requiredAmount +
                                "\nDisponibles: " + available
                );
            }
        }
    }

    public void simpleSim() {

        validateStock();

        List<Operator> operators = dataStore.getOperators();

        assignOperators(operators);

        while (!allLinesEmpty()) {
            updateAllLines();
            sleep();
        }
    }

    // Asigna operarios a todos los módulos de todas las líneas de forma aleatoria
    private void assignOperators(List<Operator> operators) {

        if (operators.size() < 12) {
            System.out.println(operators.size());
            throw new IllegalStateException(
                    "Se necesitan al menos 12 operarios para iniciar la simulación"
            );
        }

        Collections.shuffle(operators);

        int index = 0;

        for (VehicleType vehicleType : VehicleType.values()) {

            AssemblyLine line = dataStore.getAssemblyLine(vehicleType);

            for (AssemblyPhase phase : AssemblyPhase.values()) {
                line.setModule(phase, operators.get(index++));
            }
        }
    }

    private void updateAllLines() {

        for (VehicleType vehicleType : VehicleType.values()) {
            dataStore.getAssemblyLine(vehicleType).updateLine();
        }
    }

    private void sleep() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulación interrumpida", e);
        }
    }

    // Comprueba si no hay vehículos ni módulos activos
    private boolean allLinesEmpty() {

        for (VehicleType vehicleType : VehicleType.values()) {

            AssemblyLine line = dataStore.getAssemblyLine(vehicleType);

            if (!line.getPendingVehicles().isEmpty()) return false;

            for (AssemblyModule module : line.getModules()) {
                if (!module.isFree()) return false;
            }
        }

        return true;
    }
}