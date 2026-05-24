package Dashboard;

import AssemblyLine.AssemblyLine;
import AssemblyLine.AssemblyPhase;
import DataStore.DataStore;
import Vehicles.Vehicle;
import Vehicles.VehicleType;


/**
 * Implementación de dashboard en consola.
 *
 * Muestra el estado de las líneas de ensamblaje,
 * el almacén y reacciona a eventos del sistema (Observer).
 */
public class ConsoleDashboard implements Dashboard {

    private DataStore dataStore;

    public ConsoleDashboard(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void showAssemblyLines() {
        // Muestra el estado de cada línea de ensamblaje por tipo de vehículo
        for (VehicleType type : VehicleType.values()) {

            System.out.println("\nLINEA: " + type + "\n");

            AssemblyLine line = dataStore.getAssemblyLine(type);

            for (String s : line.getStatus()) {
                System.out.println(s);
            }
        }
    }

    @Override
    public void showWarehouseStatus() {
        System.out.println("\n=== ALMACÉN ===");
        System.out.println("-- Motores --");
        dataStore.getEngines().getInventory().forEach((e, q) ->
                System.out.println("  " + e + " | Stock: " + q));
        System.out.println("-- Tapicerías --");
        dataStore.getUpholsteries().getInventory().forEach((u, q) ->
                System.out.println("  " + u + " | Stock: " + q));
        System.out.println("-- Ruedas --");
        dataStore.getWheels().getInventory().forEach((w, q) ->
                System.out.println("  " + w + " | Stock: " + q));
    }

    @Override
    public void showMessage(String message) {
        System.out.println("[INFO] " + message);
    }

    //Osbserver para la entrada de vehiculos a la linea
    @Override
    public void onVehicleEntered(Vehicle vehicle) {
        System.out.println("[ENTRADA] Vehículo " + vehicle.getColor() + " ha entrado en la línea");
        showAssemblyLines();
    }


    //Osbserver para el avance de vehiculos en linea
    @Override
    public void onVehicleAdvanced(Vehicle vehicle, AssemblyPhase from, AssemblyPhase to) {
        System.out.println("[AVANCE] Vehículo " + vehicle.getColor() +
                " avanza de " + from + " a " + to);
        showAssemblyLines();
    }

    //Osbserver para cuando se monta un componente
    @Override
    public void onComponentConsumed(String component, int remaining) {
        System.out.println("[STOCK] " + component + " consumido | Restante: " + remaining);
        showWarehouseStatus();
    }

    //Osbserver para cuando un vehiculo se ensambla al completo
    @Override
    public void onVehicleFinished(Vehicle vehicle) {
        System.out.println("[FIN] Vehículo " + vehicle.getColor() + " ha terminado la línea");
        showAssemblyLines();
    }

}