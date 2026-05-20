import AssemblyLine.AssemblyLine;
import AssemblyLine.LineConfig;
import AssemblyLine.AssemblyPhase;
import Components.Engine;
import Components.EngineType;
import Components.Upholstery;
import Components.UpholsteryType;
import Components.Wheel;
import Components.WheelType;
import Vehicles.Vehicle;
import Vehicles.VehicleType;
import Workers.Operator;

import java.util.Date;

public class factory_main {

    public static void main(String[] args) {

        // 🔧 Componentes
        Engine engine = new Engine(EngineType.GASOLINA, 2000, 150, 4);
        Upholstery upholstery = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel wheel = new Wheel(WheelType.NORMAL, 205, 17, 600, 240);

        // 🏭 Línea
        AssemblyLine line = new AssemblyLine(new LineConfig(engine, upholstery, wheel));

        // 👷 Operarios (unos eficientes y otros no)
        Operator op1 = new Operator("Juan", "Perez", "Lopez", "38201437R", "Calle 1", 1500, new Date(), 8);
        Operator op2 = new Operator("Pedro", "Garcia", "Ruiz", "38201437R", "Calle 2", 1500, new Date(), 12);
        Operator op3 = new Operator("Luis", "Martinez", "Diaz", "38201437R", "Calle 3", 1500, new Date(), 11);
        Operator op4 = new Operator("Ana", "Sanchez", "Gomez", "38201437R", "Calle 4", 1500, new Date(), 15);

        // 🔗 Módulos
        line.setModule(AssemblyPhase.CHASIS, op1);
        line.setModule(AssemblyPhase.MOTOR, op2);
        line.setModule(AssemblyPhase.TAPICERIA, op3);
        line.setModule(AssemblyPhase.RUEDAS, op4);

        // 🚗 Vehículos (usa tus clases concretas)
        Vehicle v0 = new Vehicle( VehicleType.DEPORTIVO,"Blanco", 2, 2, 1100);
        Vehicle v1 = new Vehicle( VehicleType.DEPORTIVO,"Rojo", 2, 2, 1000);
        Vehicle v2 = new Vehicle( VehicleType.DEPORTIVO,"Azul", 2, 2, 1050);
        Vehicle v3 = new Vehicle( VehicleType.DEPORTIVO,"Blanco", 2, 2, 1100);
        Vehicle v4 = new Vehicle( VehicleType.DEPORTIVO,"Rojo", 2,2, 1000);

        // 📥 Cola de entrada
        line.addVehicle(v0);
        line.addVehicle(v1);
        line.addVehicle(v2);
        line.addVehicle(v3);
        line.addVehicle(v4);

        // ⏱ Simulación
        for (int t = 1; t <= 20; t++) {
            System.out.println("---- SEGUNDO " + t + " ----");

            line.updateLine();

            line.printStatus();
        }

        // ✅ Resultado final
        System.out.println("\n=== VEHÍCULOS TERMINADOS ===");
        line.printFinishedVehicles();
    }
}