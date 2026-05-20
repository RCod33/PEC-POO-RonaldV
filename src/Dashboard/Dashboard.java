package Dashboard;

import AssemblyLine.AssemblyLine;
import Components.Engine;
import Vehicles.Vehicle;

import java.util.Map;

public interface Dashboard {

    void showAssemblyLines();

    void showWarehouseStatus();

    void showVehicleUpdate(Vehicle vehicle);

    void showComponentUpdate(String component, int remaining);

    void showMessage(String message);
}