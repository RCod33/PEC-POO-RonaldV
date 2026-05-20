package AssemblyLine;

import Vehicles.Vehicle;
import Workers.Operator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AssemblyLine {

    private ArrayList<AssemblyModule> modules = new ArrayList<>();
    private Queue<Vehicle> pendingVehicles = new LinkedList<>();
    private Queue<Vehicle> finishedVehicles = new LinkedList<>();

    private LineConfig config;

    public AssemblyLine(LineConfig config) {
        this.config = config;

        for (AssemblyPhase phase : AssemblyPhase.values()) {
            modules.add(new AssemblyModule(phase));
        }
    }

    public ArrayList<AssemblyModule> getModules() {
        return modules;
    }

    public void setModule (AssemblyPhase phase, Operator operator) {
        AssemblyModule module = new AssemblyModule(phase, operator);
        modules.set(phase.ordinal(), module);
    }

    public void updateLine(){

        for (int i = modules.size() - 1; i >= 0; --i) {

            AssemblyModule current = modules.get(i);

            boolean finished = current.work();

            if (!finished) continue;

            Vehicle car = current.getVehicle();
            AssemblyPhase phase = current.getPhase();

            //Necesito separar esto a un applyPhase en assemblymodule y hacer la logica de q tiene q haber stock disponible
            if (car != null) {
                switch (phase) {
                    case MOTOR: car.setEngine(config.getEngine()); break;
                    case TAPICERIA: car.setUpholstery(config.getUpholstery()); break;
                    case RUEDAS: car.setWheel(config.getWheel()); break;
                }
            }

            if (i == modules.size() - 1) {
                Vehicle finishedCar = current.releaseVehicle();
                finishedVehicles.add(finishedCar);
                continue;
            }

            AssemblyModule next = modules.get(i + 1);

            if (next.isFree()) {
                Vehicle v = current.releaseVehicle();
                next.setVehicle(v);
            }
        }

        if (modules.getFirst().isFree() && !pendingVehicles.isEmpty()) modules.getFirst().setVehicle(pendingVehicles.poll());
    }

    public void addVehicle(Vehicle v) {
        pendingVehicles.add(v);
    }

    public Queue<Vehicle> getPendingVehicles() {
        return this.pendingVehicles;
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

                status.add(names[i] + " -> " +
                        v.getColor()
                        + " | Motor:" + (v.getEngine() != null ? "✔" : "✘")
                        + " | Tap:" + (v.getUpholstery() != null ? "✔" : "✘")
                        + " | Ruedas:" + (v.getWheel() != null ? "✔" : "✘"));
            }
        }

        return status;
    }

    //refactorizar a get

    public void printFinishedVehicles() {
        System.out.println("=== VEHICULOS TERMINADOS ===");

        if (finishedVehicles.isEmpty()) {
            System.out.println("No hay vehiculos terminados.");
            return;
        }

        int count = 1;

        for (Vehicle v : finishedVehicles) {
            System.out.println("Coche #" + count++ + ": "
                    + v.getColor()
                    + " | Motor:" + (v.getEngine() != null ? "✔" : "✘")
                    + " | Tapiceria:" + (v.getUpholstery() != null ? "✔" : "✘")
                    + " | Ruedas:" + (v.getWheel() != null ? "✔" : "✘"));
        }
    }

}
