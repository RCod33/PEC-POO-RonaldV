package Dashboard;

import Observers.AssemblyLineObserver;

import java.util.Map;

public interface Dashboard extends AssemblyLineObserver{
    void showAssemblyLines();
    void showWarehouseStatus();
    void showMessage(String message);
}