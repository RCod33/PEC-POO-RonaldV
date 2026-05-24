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

import Reports.ReportService;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class factory_main {

    private static final Scanner scanner    = new Scanner(System.in);
    private static final DataStore dataStore  = DataStore.getInstance();
    private static final ConsoleDashboard dashboard = new ConsoleDashboard(dataStore);
    private static final ReportService reports = new ReportService(dataStore);

    /**
     * Inicializa el sistema con datos de prueba:
     * - 12 operarios (6 eficientes con >20 montajes, 6 estándar con ≤20 montajes)
     * - 1 administrador del sistema
     * - 1 gestor de planta
     * - 20 vehículos distribuidos entre las 3 variantes
     * - Configuración de las 3 líneas de ensamblaje
     */
    private static void init() {
        System.out.println("\n=== INICIALIZANDO SISTEMA CON DATOS DE PRUEBA ===\n");

        // ========================================================================
        // 1. CREAR TRABAJADORES
        // ========================================================================

        // Fecha actual para todos los trabajadores
        Date now = new Date();

        // 12 Operarios (6 eficientes con >20 montajes, 6 estándar con ≤20 montajes)
        String[] nombres = {"Juan", "María", "Carlos", "Ana", "Luis", "Elena",
                "Pedro", "Laura", "Javier", "Sofia", "Diego", "Carmen"};
        String[] apellidos1 = {"García", "Martínez", "López", "Sánchez", "Pérez", "Gómez",
                "Fernández", "Díaz", "Álvarez", "Ruiz", "Torres", "Ramos"};
        String[] apellidos2 = {"Pérez", "Gómez", "Fernández", "Díaz", "Álvarez", "Ruiz",
                "Torres", "Ramos", "Flores", "Vega", "Silva", "Castro"};

        // Operarios eficientes (montajes > 20)
        int[] montajesEficientes = {1, 2, 4, 35, 22, 40};
        for (int i = 0; i < 6; i++) {
            String dni = String.format("%08d", 10000000 + i) + letraDNI(10000000 + i);
            Operator op = new Operator(
                    nombres[i], apellidos1[i], apellidos2[i], dni,
                    "Calle " + (i + 1) + ", Nº " + (i + 1),
                    String.format("%02d/%08d/%02d", 10 + i, 50000000 + i, 1 + i),
                    1800.0 + (i * 100), now, montajesEficientes[i]
            );
            dataStore.addWorker(op);
            System.out.println("✓ Operario eficiente creado: " + op.getName() + " " + op.getLastName() +
                    " (montajes: " + montajesEficientes[i] + ")");
        }

        // Operarios estándar (montajes ≤ 20)
        int[] montajesEstandar = {10, 8, 15, 12, 5, 18};
        for (int i = 0; i < 6; i++) {
            int idx = i + 6;
            String dni = String.format("%08d", 10000000 + idx) + letraDNI(10000000 + idx);
            Operator op = new Operator(
                    nombres[idx], apellidos1[idx], apellidos2[idx], dni,
                    "Calle " + (idx + 1) + ", Nº " + (idx + 1),
                    String.format("%02d/%08d/%02d", 15 + i, 60000000 + i, 2 + i),
                    1500.0 + (i * 50), now, montajesEstandar[i]
            );
            dataStore.addWorker(op);
            System.out.println("✓ Operario estándar creado: " + op.getName() + " " + op.getLastName() +
                    " (montajes: " + montajesEstandar[i] + ")");
        }

        // Mecánicos (1 eficiente con >20 reparaciones, 1 estándar con ≤20 reparaciones)
        Mechanic mechanicEfficient = new Mechanic(
                "Roberto", "Mecánico", "Eficiente", "12345678Z",
                "Taller 1, Nº 1", "01/12345678/01", 2200.0, now, 30
        );
        dataStore.addWorker(mechanicEfficient);
        System.out.println("✓ Mecánico eficiente creado: " + mechanicEfficient.getName() +
                " (reparaciones: 30)");

        Mechanic mechanicStandard = new Mechanic(
                "Patricia", "Mecánico", "Estándar", "12345678Z",
                "Taller 2, Nº 2", "02/87654321/02", 1900.0, now, 1
        );
        dataStore.addWorker(mechanicStandard);
        System.out.println("✓ Mecánico estándar creado: " + mechanicStandard.getName() +
                " (reparaciones: 15)");

        // Administrador del sistema
        SystemAdministrator admin = new SystemAdministrator(
                "Fernando", "Admin", "Sistema", "12345678Z",
                "Oficina Admin, Nº 1", "99/99999999/99", 3000.0, now
        );
        dataStore.addWorker(admin);
        System.out.println("✓ Administrador del sistema creado: " + admin.getName());

        // Gestor de planta
        PlantManager plantManager = new PlantManager(
                "Laura", "Gestora", "Planta", "12345678Z",
                "Oficina Gestión, Nº 1", "88/88888888/88", 2800.0, now
        );
        dataStore.addWorker(plantManager);
        System.out.println("✓ Gestor de planta creado: " + plantManager.getName());

        // ========================================================================
        // 2. CONFIGURAR LÍNEAS DE ENSAMBLAJE
        // ========================================================================

        System.out.println("\n--- CONFIGURANDO LÍNEAS DE ENSAMBLAJE ---");

        // Configuración para DEPORTIVO (motor GASOLINA, tapicería CUERO, ruedas DEPORTIVO)
        Engine engineSport = new Engine(EngineType.GASOLINA, 3000, 350, 6);
        Upholstery upholsterySport = new Upholstery(UpholsteryType.CUERO, "Rojo", 5.5);
        Wheel wheelSport = new Wheel(WheelType.DEPORTIVO, 245, 19, 750, 240);
        dataStore.modifyAssemblyLine(VehicleType.DEPORTIVO, new LineConfig(engineSport, upholsterySport, wheelSport));
        System.out.println("✓ Línea DEPORTIVO configurada: Motor GASOLINA 3000cc, Tapicería CUERO roja, Ruedas DEPORTIVO");

        // Configuración para FURGONETA (motor HIBRIDO, tapicería TELA, ruedas NORMAL)
        Engine engineVan = new Engine(EngineType.HIBRIDO, 1800, 150, 4);
        Upholstery upholsteryVan = new Upholstery(UpholsteryType.TELA, "Gris", 8.0);
        Wheel wheelVan = new Wheel(WheelType.NORMAL, 215, 16, 950, 180);
        dataStore.modifyAssemblyLine(VehicleType.FURGONETA, new LineConfig(engineVan, upholsteryVan, wheelVan));
        System.out.println("✓ Línea FURGONETA configurada: Motor HIBRIDO 1800cc, Tapicería TELA gris, Ruedas NORMAL");

        // Configuración para TURISMO (motor ELECTRICO, tapicería ALCANTARA, ruedas NORMAL)
        Engine engineCar = new Engine(EngineType.ELECTRICO, 0, 180, 0);
        Upholstery upholsteryCar = new Upholstery(UpholsteryType.ALCANTARA, "Negro", 4.5);
        Wheel wheelCar = new Wheel(WheelType.NORMAL, 225, 17, 650, 210);
        dataStore.modifyAssemblyLine(VehicleType.TURISMO, new LineConfig(engineCar, upholsteryCar, wheelCar));
        System.out.println("✓ Línea TURISMO configurada: Motor ELECTRICO, Tapicería ALCANTARA negra, Ruedas NORMAL");

        // ========================================================================
        // 3. CREAR VEHÍCULOS (20 en total)
        // ========================================================================

        System.out.println("\n--- CREANDO VEHÍCULOS PARA ENSAMBLAJE ---");

        String[] colores = {"Rojo", "Azul", "Blanco", "Negro", "Gris", "Plata", "Verde", "Amarillo"};
        int contador = 0;

        // 7 Deportivos
        for (int i = 0; i < 7; i++) {
            Vehicle v = new Vehicle(
                    VehicleType.DEPORTIVO,
                    colores[i % colores.length],
                    2 + (i % 2),  // 2 o 3 plazas
                    1200.0 + (i * 20),
                    1600.0 + (i * 30)
            );
            dataStore.getAssemblyLine(VehicleType.DEPORTIVO).addVehicle(v);
            System.out.println("  ✓ Deportivo " + (i+1) + ": " + v);
            contador++;
        }

        // 7 Furgonetas
        for (int i = 0; i < 7; i++) {
            Vehicle v = new Vehicle(
                    VehicleType.FURGONETA,
                    colores[(i+2) % colores.length],
                    2,
                    1800.0 + (i * 50),
                    2800.0 + (i * 80)
            );
            dataStore.getAssemblyLine(VehicleType.FURGONETA).addVehicle(v);
            System.out.println("  ✓ Furgoneta " + (i+1) + ": " + v);
            contador++;
        }

        // 6 Turismos
        for (int i = 0; i < 6; i++) {
            Vehicle v = new Vehicle(
                    VehicleType.TURISMO,
                    colores[(i+4) % colores.length],
                    4 + (i % 2),  // 4 o 5 plazas
                    1300.0 + (i * 30),
                    1800.0 + (i * 40)
            );
            dataStore.getAssemblyLine(VehicleType.TURISMO).addVehicle(v);
            System.out.println("  ✓ Turismo " + (i+1) + ": " + v);
            contador++;
        }

        // ========================================================================
        // 4. AÑADIR COMPONENTES AL ALMACÉN (para que haya stock)
        // ========================================================================

        System.out.println("\n--- ABASTECIENDO ALMACÉN ---");

        // Motores
        dataStore.getEngines().add(engineSport);
        dataStore.getEngines().add(engineVan);
        dataStore.getEngines().add(engineCar);
        for (int i = 0; i < 12; i++) dataStore.getEngines().add(engineSport);
        for (int i = 0; i < 12; i++) dataStore.getEngines().add(engineVan);
        for (int i = 0; i < 12; i++) dataStore.getEngines().add(engineCar);
        System.out.println("✓ Motores añadidos al almacén");

        // Tapicerías
        dataStore.getUpholsteries().add(upholsterySport);
        dataStore.getUpholsteries().add(upholsteryVan);
        dataStore.getUpholsteries().add(upholsteryCar);
        for (int i = 0; i < 12; i++) dataStore.getUpholsteries().add(upholsterySport);
        for (int i = 0; i < 12; i++) dataStore.getUpholsteries().add(upholsteryVan);
        for (int i = 0; i < 12; i++) dataStore.getUpholsteries().add(upholsteryCar);
        System.out.println("✓ Tapicerías añadidas al almacén");

        // Ruedas
        dataStore.getWheels().add(wheelSport);
        dataStore.getWheels().add(wheelVan);
        dataStore.getWheels().add(wheelCar);
        for (int i = 0; i < 70; i++) dataStore.getWheels().add(wheelSport);
        for (int i = 0; i < 70; i++) dataStore.getWheels().add(wheelVan);
        for (int i = 0; i < 60; i++) dataStore.getWheels().add(wheelCar);
        System.out.println("✓ Ruedas añadidas al almacén");

        System.out.println("\n=== INICIALIZACIÓN COMPLETADA ===");
        System.out.println("Resumen:");
        System.out.println("  - Trabajadores: " + dataStore.getWorkers().size() +
                " (12 operarios, 2 mecánicos, 1 admin, 1 gestor)");
        System.out.println("  - Vehículos en cola: " + contador + " (7 deportivos, 7 furgonetas, 6 turismos)");
        System.out.println("  - Líneas configuradas: 3\n");
    }

    /**
     * Calcula la letra del DNI según el número solo se usa en el init
     */
    private static char letraDNI(int numero) {
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letras.charAt(numero % 23);
    }

    public static void main(String[] args) {

        init();

        for (VehicleType type : VehicleType.values()) {
            dataStore.getAssemblyLine(type).addObserver(dashboard);
        }

        /**
         * Main menu
         * **/
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int option = readInt("Selecciona una opción: ");
            switch (option) {
                case 1 -> workersMenu();
                case 2 -> warehouseMenu();
                case 3 -> assemblyLinesMenu();
                case 4 -> runSimpleSimulation();
                case 5 -> runComplexSimulation();
                case 6 -> runVeryComplexSimulation();
                case 7 -> reportsMenu();
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
        System.out.println("║  5. Iniciar simulación compleja      ║");
        System.out.println("║  6. Iniciar simulación muy compleja  ║");
        System.out.println("║  7. Informes y estadísticas          ║");
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
            System.out.println("0. Volver");
            int option = readInt("Selecciona: ");
            switch (option) {
                case 1 -> addEngine();
                case 2 -> addUpholstery();
                case 3 -> addWheels();
                case 4 -> dashboard.showWarehouseStatus();
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
            System.out.println("4. Ver vehículos pendientes de ensamblar");
            System.out.println("0. Volver");
            int option = readInt("Selecciona: ");
            switch (option) {
                case 1 -> configureAssemblyLine();
                case 2 -> addVehicleToLine();
                case 3 -> dashboard.showAssemblyLines();
                case 4 -> showPendingVehicles();
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

        int widthMm           = readInt("Ancho (mm): ");
        int rimDiameterInches = readInt("Diámetro de llanta (pulgadas): ");
        int loadIndexKg       = readInt("Índice de carga (kg): ");
        int speedCode         = readInt("Código de velocidad: ");
        int quantity          = readInt("Cantidad: ");

        try {
            Wheel wheel = new Wheel(type, widthMm, rimDiameterInches, loadIndexKg, speedCode);
            for (int i = 0; i < quantity; i++) dataStore.getWheels().add(wheel);
            System.out.println("Añadidas " + quantity + " x " + wheel);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
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
            case 3 -> EngineType.HIBRIDO;
            default -> null;
        };
        if (engineType == null) { System.out.println("Tipo no válido."); return; }
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
            case 3 -> UpholsteryType.ALCANTARA;
            default -> null;
        };
        if (upholsteryType == null) { System.out.println("Tipo no válido."); return; }

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
            case 3 -> WheelType.TODOTERRENO;
            default -> null;
        };
        if (wheelType == null) { System.out.println("Tipo no válido."); return; }

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

    private static void showPendingVehicles() {
        var inventory = dataStore.getPendingVehicles().getInventory();
        if (inventory.isEmpty()) { System.out.println("No hay vehículos pendientes."); return; }
        System.out.println("\n--- VEHÍCULOS PENDIENTES ---");
        inventory.forEach((v, q) -> System.out.println("  " + v + " | Unidades: " + q));
        System.out.println("\nTotal: " + inventory.size());
    }
    // =========================================================================
    // SIMULACIONES
    // =========================================================================

    /**
     * Simulación Simple: sin eventos externos.
     * Los operarios son asignados aleatoriamente.
     */
    private static void runSimpleSimulation() {
        System.out.println("\nIniciando simulación simple...");
        try {
            new Scheduler(dataStore).simpleSim();
            collectFinishedVehicles();
            System.out.println("\nSimulación finalizada.");
            dashboard.showWarehouseStatus();
        } catch (IllegalStateException e) {
            System.out.println("Error en la simulación: " + e.getMessage());
        }
    }

    /**
     * Simulación Compleja: intervienen mecánicos.
     * Al menos uno de cada perfil (eficiente y estándar) repara
     * al menos 2 averías en cada cadena.
     * Requiere: ≥12 operarios + ≥1 mecánico eficiente + ≥1 mecánico estándar.
     */
    private static void runComplexSimulation() {
        System.out.println("\nIniciando simulación compleja...");
        System.out.println("Requisitos: ≥12 operarios, ≥1 mecánico eficiente (>20 rep.), " +
                "≥1 mecánico estándar (≤20 rep.)");
        try {
            new Scheduler(dataStore).complexSim();
            collectFinishedVehicles();
            System.out.println("\nSimulación compleja finalizada.");
            dashboard.showWarehouseStatus();
        } catch (IllegalStateException e) {
            System.out.println("Error en la simulación: " + e.getMessage());
        }
    }

    /**
     * Simulación Muy Compleja: perfiles de operarios + mecánicos + administrador.
     * 2-3 averías por cadena + al menos 1 apagón gestionado por el administrador
     * (2 ticks para restaurar el sistema de gestión + 3 ticks para las cadenas).
     * Requiere: ≥12 operarios + ≥1 mecánico eficiente + ≥1 mecánico estándar
     *           + ≥1 administrador del sistema.
     */
    private static void runVeryComplexSimulation() {
        System.out.println("\nIniciando simulación muy compleja...");
        System.out.println("Requisitos: ≥12 operarios, ≥1 mecánico eficiente, " +
                "≥1 mecánico estándar, ≥1 administrador del sistema.");
        try {
            new Scheduler(dataStore).veryComplexSim();
            collectFinishedVehicles();
            System.out.println("\nSimulación muy compleja finalizada.");
            dashboard.showWarehouseStatus();
        } catch (IllegalStateException e) {
            System.out.println("Error en la simulación: " + e.getMessage());
        }
    }

    /** Mueve los vehículos terminados al stock del almacén. */
    private static void collectFinishedVehicles() {
        for (VehicleType type : VehicleType.values()) {
            dataStore.getAssemblyLine(type).getFinishedVehicles()
                    .forEach(v -> dataStore.getVehicles().add(v));
        }
    }

    // =========================================================================
    // INFORMES Y ESTADÍSTICAS
    // =========================================================================

    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- INFORMES Y ESTADÍSTICAS ---");
            System.out.println("1. Listado de operarios");
            System.out.println("2. Listado de vehículos ensamblados");
            System.out.println("3. Configuraciones con mayor tasa de ensamblaje");
            System.out.println("4. Cadenas de montaje por fecha");
            System.out.println("0. Volver");
            int option = readInt("Selecciona: ");
            switch (option) {
                case 1 -> reportOperarios();
                case 2 -> reportVehiculos();
                case 3 -> reports.reportConfiguracionesMasEnsambladas();
                case 4 -> reportCadenasPorFecha();
                case 0 -> back = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void reportOperarios() {
        System.out.println("Filtrar por perfil: 1.EFICIENTE  2.ESTANDAR  3.TODOS");
        String perfil = switch (readInt("Perfil: ")) {
            case 1 -> "EFICIENTE";
            case 2 -> "ESTANDAR";
            default -> null;
        };
        System.out.println("Ordenación: 1.Alfabética  2.Por número de montajes (desc)");
        boolean alfa = readInt("Orden: ") == 1;
        reports.reportOperarios(perfil, alfa);
    }

    private static void reportVehiculos() {
        System.out.println("Filtrar por motor (0 para omitir): 1.ELECTRICO  2.GASOLINA  3.HIBRIDO");
        EngineType ef = switch (readInt("Motor: ")) {
            case 1 -> EngineType.ELECTRICO;
            case 2 -> EngineType.GASOLINA;
            case 3 -> EngineType.HIBRIDO;
            default -> null;
        };
        System.out.println("Filtrar por tapicería (0 para omitir): 1.TELA  2.CUERO  3.ALCANTARA");
        UpholsteryType uf = switch (readInt("Tapicería: ")) {
            case 1 -> UpholsteryType.TELA;
            case 2 -> UpholsteryType.CUERO;
            case 3 -> UpholsteryType.ALCANTARA;
            default -> null;
        };
        System.out.println("Filtrar por ruedas (0 para omitir): 1.NORMAL  2.DEPORTIVO  3.TODOTERRENO");
        WheelType wf = switch (readInt("Ruedas: ")) {
            case 1 -> WheelType.NORMAL;
            case 2 -> WheelType.DEPORTIVO;
            case 3 -> WheelType.TODOTERRENO;
            default -> null;
        };
        System.out.println("Ordenación alfabética por color: 1.Sí  2.No");
        boolean alfa = readInt("Orden: ") == 1;
        reports.reportVehiculos(ef, uf, wf, alfa);
    }

    private static void reportCadenasPorFecha() {
        System.out.print("Introduce la fecha (dd/MM/yyyy): ");
        String input = scanner.nextLine().trim();
        try {
            Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(input);
            reports.reportCadenasPorFecha(fecha);
        } catch (ParseException e) {
            System.out.println("Formato de fecha incorrecto. Usa dd/MM/yyyy.");
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