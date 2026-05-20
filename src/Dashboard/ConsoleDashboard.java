package Dashboard;

import AssemblyLine.AssemblyLine;
import DataStore.DataStore;
import Vehicles.Vehicle;
import Vehicles.VehicleType;

public class ConsoleDashboard implements Dashboard {

    private DataStore dataStore;

    public ConsoleDashboard(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void showAssemblyLines() {

        for (VehicleType type : VehicleType.values()) {

            System.out.println("\nLINEA: " + type);

            AssemblyLine line = dataStore.getAssemblyLine(type);

            for (String s : line.getStatus()) {
                System.out.println(s);
            }
        }
    }

    @Override
    public void showWarehouseStatus() {

    }

    @Override
    public void showVehicleUpdate(Vehicle vehicle) {

    }

    @Override
    public void showComponentUpdate(String component, int remaining) {

    }

    @Override
    public void showMessage(String message) {

    }
}