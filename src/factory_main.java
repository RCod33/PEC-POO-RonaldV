import AssemblyLine.LineConfig;
import Components.Engine;
import Components.EngineType;
import Components.Upholstery;
import Components.UpholsteryType;
import Components.Wheel;
import Components.WheelType;
import DataStore.DataStore;
import Dashboard.ConsoleDashboard;
import Scheduler.Scheduler;
import Vehicles.Vehicle;
import Vehicles.VehicleType;
import Workers.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class factory_main {

    private static final Scanner scanner   = new Scanner(System.in);
    private static final DataStore dataStore = DataStore.getInstance();
    private static final ConsoleDashboard dashboard = new ConsoleDashboard(dataStore);

    public static void main(String[] args) {

        for (VehicleType type : VehicleType.values()) {
            dataStore.getAssemblyLine(type).addObserver(dashboard);
        }

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int option = readInt("Selecciona una opción: ");
            switch (option) {
                case 1 -> workersMenu();
                case 2 -> warehouseMenu();
                case 3 -> assemblyLinesMenu();
                case 4 -> runSimpleSimulation();
                case 0 -> exit = true;
                default -> System.out.println("Opción no válida.");
            }
        }
        System.out.println("Hasta luego.");
    }

    // =========================================================================
    // MENÚS
    // =========================================================================

    private static void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║      FÁBRICA DE VEHÍCULOS v1.0       ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Gestión de trabajadores          ║");
        System.out.println("║  2. Gestión de almacén               ║");
        System.out.println("║  3. Cadenas de montaje               ║");
        System.out.println("║  4. Iniciar simulación simple        ║");
        System.out.println("║  0. Salir                            ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private static void workersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- GESTIÓN DE TRABAJADORES ---");
            System.out.println("1. Dar de alta trabajador");
            System.out.println("2. Buscar por DNI");
            System.out.println("3. Buscar por nombre");
            System.out.println("4. Listar todos");
            System.out.println("0. Volver");
            int option = readInt("Selecciona: ");
            switch (option) {
                case 1 -> registerWorker();
                case 2 -> searchWorkerByDNI();
                case 3 -> searchWorkerByName();
                case 4 -> listWorkers();
                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void warehouseMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- GESTIÓN DE ALMACÉN ---");
            System.out.println("1. Añadir motor");
            System.out.println("2. Añadir tapicería");
            System.out.println("3. Añadir ruedas");
            System.out.println("4. Ver estado del almacén");
            System.out.println("5. Ver vehículos ensamblados");
            System.out.println("0. Volver");
            int option = readInt("Selecciona: ");
            switch (option) {
                case 1 -> addEngine();
                case 2 -> addUpholstery();
                case 3 -> addWheels();
                case 4 -> dashboard.showWarehouseStatus();
                case 5 -> showFinishedVehicles();
                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void assemblyLinesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- CADENAS DE MONTAJE ---");
            System.out.println("1. Configurar cadena");
            System.out.println("2. Añadir vehículo a cadena");
            System.out.println("3. Ver estado de las cadenas");
            System.out.println("0. Volver");
            int option = readInt("Selecciona: ");
            switch (option) {
                case 1 -> configureAssemblyLine();
                case 2 -> addVehicleToLine();
                case 3 -> dashboard.showAssemblyLines();
                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // =========================================================================
    // TRABAJADORES
    // =========================================================================

    private static void registerWorker() {
        System.out.println("\nTipo de trabajador:");
        System.out.println("1. Operario");
        System.out.println("2. Mecánico de cinta");
        System.out.println("3. Gestor de planta");
        System.out.println("4. Administrador del sistema");
        int type = readInt("Tipo: ");

        System.out.print("Nombre: ");
        String name = scanner.nextLine().trim();
        System.out.print("Primer apellido: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Segundo apellido: ");
        String secondLastName = scanner.nextLine().trim();
        System.out.print("DNI (8 números + letra): ");
        String dni = scanner.nextLine().trim();
        System.out.print("Dirección: ");
        String address = scanner.nextLine().trim();
        System.out.print("Número SS (XX/XXXXXXXX/XX): ");
        String ss = scanner.nextLine().trim();
        double salary = readDouble("Salario: ");

        try {
            Worker worker = switch (type) {
                case 1 -> {
                    int assemblies = readInt("Número de montajes realizados: ");
                    yield new Operator(name, lastName, secondLastName, dni,
                            address, ss, salary, new Date(), assemblies);
                }
                case 2 -> {
                    int repairs = readInt("Número de reparaciones realizadas: ");
                    yield new Mechanic(name, lastName, secondLastName, dni,
                            address, ss, salary, new Date(), repairs);
                }
                case 3 -> new PlantManager(name, lastName, secondLastName, dni,
                        address, ss, salary, new Date());
                case 4 -> new SystemAdministrator(name, lastName, secondLastName, dni,
                        address, ss, salary, new Date());
                default -> null;
            };

            if (worker == null) { System.out.println("Tipo no válido."); return; }

            dataStore.addWorker(worker);
            System.out.println("Trabajador dado de alta: " + worker);

        } catch (IllegalArgumentException e) {
            System.out.println("Error al dar de alta: " + e.getMessage());
        }
    }

    private static void searchWorkerByDNI() {
        System.out.print("DNI: ");
        String dni = scanner.nextLine().trim();
        Worker worker = dataStore.searchWorkerByDNI(dni);
        if (worker == null) System.out.println("No se encontró ningún trabajador con ese DNI.");
        else                System.out.println("Encontrado: " + worker);
    }

    private static void searchWorkerByName() {
        System.out.print("Nombre o apellido: ");
        String name = scanner.nextLine().trim();
        List<Worker> results = dataStore.searchWorkersByName(name);
        if (results.isEmpty()) System.out.println("No se encontró ningún trabajador.");
        else results.forEach(w -> System.out.println("  " + w));
    }

    private static void listWorkers() {
        List<Worker> workers = dataStore.getWorkers();
        if (workers.isEmpty()) { System.out.println("No hay trabajadores registrados."); return; }
        System.out.println("\n--- LISTADO DE TRABAJADORES ---");
        workers.forEach(w -> System.out.println("  " + w));
    }

    // =========================================================================
    // ALMACÉN
    // =========================================================================

    private static void addEngine() {
        System.out.println("Tipo: 1.ELECTRICO  2.GASOLINA  3.HIBRIDO");
        EngineType type = switch (readInt("Tipo: ")) {
            case 1 -> EngineType.ELECTRICO;
            case 2 -> EngineType.GASOLINA;
            case 3 -> EngineType.HIBRIDO;
            default -> null;
        };
        if (type == null) { System.out.println("Tipo no válido."); return; }

        int displacement    = readInt("Cilindrada (cc): ");
        int power           = readInt("Potencia (CV): ");
        int cylinders       = readInt("Número de cilindros: ");
        int quantity        = readInt("Cantidad: ");

        Engine engine = new Engine(type, displacement, power, cylinders);
        for (int i = 0; i < quantity; i++) dataStore.getEngines().add(engine);
        System.out.println("Añadidos " + quantity + " x " + engine);
    }

    private static void addUpholstery() {
        System.out.println("Tipo: 1.TELA  2.CUERO  3.ALCANTARA");
        UpholsteryType type = switch (readInt("Tipo: ")) {
            case 1 -> UpholsteryType.TELA;
            case 2 -> UpholsteryType.CUERO;
            case 3 -> UpholsteryType.ALCANTARA;
            default -> null;
        };
        if (type == null) { System.out.println("Tipo no válido."); return; }

        System.out.print("Color: ");
        String color    = scanner.nextLine().trim();
        double sqMeters = readDouble("Metros cuadrados: ");
        int quantity    = readInt("Cantidad: ");

        try {
            Upholstery upholstery = new Upholstery(type, color, sqMeters);
            for (int i = 0; i < quantity; i++) dataStore.getUpholsteries().add(upholstery);
            System.out.println("Añadidas " + quantity + " x " + upholstery);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addWheels() {
        System.out.println("Tipo: 1.NORMAL  2.DEPORTIVO  3.TODOTERRENO");
        WheelType type = switch (readInt("Tipo: ")) {
            case 1 -> WheelType.NORMAL;
            case 2 -> WheelType.DEPORTIVO;
            case 3 -> WheelType.TODOTERRENO;
            default -> null;
        };
        if (type == null) { System.out.println("Tipo no válido."); return; }

        int widthMm          = readInt("Ancho (mm): ");
        int rimDiameterInches = readInt("Diámetro de llanta (pulgadas): ");
        int loadIndexKg      = readInt("Índice de carga (kg): ");
        int speedCode        = readInt("Código de velocidad: ");
        int quantity         = readInt("Cantidad: ");

        try {
            Wheel wheel = new Wheel(type, widthMm, rimDiameterInches, loadIndexKg, speedCode);
            for (int i = 0; i < quantity; i++) dataStore.getWheels().add(wheel);
            System.out.println("Añadidas " + quantity + " x " + wheel);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showFinishedVehicles() {
        var inventory = dataStore.getVehicles().getInventory();
        if (inventory.isEmpty()) { System.out.println("No hay vehículos ensamblados todavía."); return; }
        System.out.println("\n--- VEHÍCULOS ENSAMBLADOS ---");
        inventory.forEach((v, q) -> System.out.println("  " + v + " | Unidades: " + q));
    }

    // =========================================================================
    // CADENAS DE MONTAJE
    // =========================================================================

    private static void configureAssemblyLine() {
        VehicleType lineType = selectVehicleType();
        if (lineType == null) return;

        // Motor
        System.out.println("\nMotores disponibles en almacén:");
        dataStore.getEngines().getInventory()
                .forEach((e, q) -> System.out.println("  " + e + " | Stock: " + q));
        System.out.println("Tipo motor: 1.ELECTRICO  2.GASOLINA  3.HIBRIDO");
        EngineType engineType = switch (readInt("Tipo: ")) {
            case 1 -> EngineType.ELECTRICO;
            case 2 -> EngineType.GASOLINA;
            default -> EngineType.HIBRIDO;
        };
        int displacement = readInt("Cilindrada (cc): ");
        int power        = readInt("Potencia (CV): ");
        int cylinders    = readInt("Número de cilindros: ");
        Engine engine    = new Engine(engineType, displacement, power, cylinders);

        // Tapicería
        System.out.println("\nTapicerías disponibles en almacén:");
        dataStore.getUpholsteries().getInventory()
                .forEach((u, q) -> System.out.println("  " + u + " | Stock: " + q));
        System.out.println("Tipo tapicería: 1.TELA  2.CUERO  3.ALCANTARA");
        UpholsteryType upholsteryType = switch (readInt("Tipo: ")) {
            case 1 -> UpholsteryType.TELA;
            case 2 -> UpholsteryType.CUERO;
            default -> UpholsteryType.ALCANTARA;
        };
        System.out.print("Color: ");
        String color      = scanner.nextLine().trim();
        double sqMeters   = readDouble("Metros cuadrados: ");
        Upholstery upholstery = new Upholstery(upholsteryType, color, sqMeters);

        // Ruedas
        System.out.println("\nRuedas disponibles en almacén:");
        dataStore.getWheels().getInventory()
                .forEach((w, q) -> System.out.println("  " + w + " | Stock: " + q));
        System.out.println("Tipo rueda: 1.NORMAL  2.DEPORTIVO  3.TODOTERRENO");
        WheelType wheelType = switch (readInt("Tipo: ")) {
            case 1 -> WheelType.NORMAL;
            case 2 -> WheelType.DEPORTIVO;
            default -> WheelType.TODOTERRENO;
        };
        int widthMm           = readInt("Ancho (mm): ");
        int rimDiameterInches = readInt("Diámetro de llanta (pulgadas): ");
        int loadIndexKg       = readInt("Índice de carga (kg): ");
        int speedCode         = readInt("Código de velocidad: ");
        Wheel wheel           = new Wheel(wheelType, widthMm, rimDiameterInches, loadIndexKg, speedCode);

        dataStore.modifyAssemblyLine(lineType, new LineConfig(engine, upholstery, wheel));
        System.out.println("Cadena " + lineType + " configurada correctamente.");
    }

    private static void addVehicleToLine() {
        VehicleType type = selectVehicleType();
        if (type == null) return;

        System.out.print("Color: ");
        String color         = scanner.nextLine().trim();
        int numberOfSeats    = readInt("Número de plazas: ");
        double tareWeight    = readDouble("Tara (kg): ");
        double maxWeight     = readDouble("Peso máximo autorizado (kg): ");

        Vehicle vehicle = new Vehicle(type, color, numberOfSeats, tareWeight, maxWeight);
        dataStore.getAssemblyLine(type).addVehicle(vehicle);
        System.out.println("Vehículo añadido a la cadena " + type + ": " + vehicle);
    }

    // =========================================================================
    // SIMULACIÓN
    // =========================================================================

    private static void runSimpleSimulation() {
        System.out.println("\nIniciando simulación simple...");
        try {
            new Scheduler(dataStore).simpleSim();

            for (VehicleType type : VehicleType.values()) {
                dataStore.getAssemblyLine(type).getFinishedVehicles()
                        .forEach(v -> dataStore.getVehicles().add(v));
            }

            System.out.println("\nSimulación finalizada.");
            dashboard.showWarehouseStatus();

        } catch (IllegalStateException e) {
            System.out.println("Error en la simulación: " + e.getMessage());
        }
    }

    // =========================================================================
    // UTILIDADES
    // =========================================================================

    private static VehicleType selectVehicleType() {
        System.out.println("Tipo de vehículo: 1.DEPORTIVO  2.FURGONETA  3.TURISMO");
        return switch (readInt("Tipo: ")) {
            case 1 -> VehicleType.DEPORTIVO;
            case 2 -> VehicleType.FURGONETA;
            case 3 -> VehicleType.TURISMO;
            default -> { System.out.println("Tipo no válido."); yield null; }
        };
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero válido.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número decimal válido.");
            }
        }
    }
}