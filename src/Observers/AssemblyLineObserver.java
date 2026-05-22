package Observers;

import Vehicles.Vehicle;
import AssemblyLine.AssemblyPhase;

public interface AssemblyLineObserver {
    void onVehicleEntered(Vehicle vehicle);
    void onVehicleAdvanced(Vehicle vehicle, AssemblyPhase from, AssemblyPhase to);
    void onVehicleFinished(Vehicle vehicle);
    void onComponentConsumed(String component, int remaining);
}