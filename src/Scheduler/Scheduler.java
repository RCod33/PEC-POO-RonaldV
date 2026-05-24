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
import Workers.Mechanic;
import Workers.Operator;
import Workers.SystemAdministrator;
import DataStore.Stock;

import java.util.*;

public class Scheduler {

    private DataStore dataStore;

    public Scheduler(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // =========================================================================
    // VALIDACIÓN DE STOCK
    // =========================================================================

    public void validateStock() {

        Map<Engine, Integer> requiredEngines = new HashMap<>();
        Map<Upholstery, Integer> requiredUpholsteries = new HashMap<>();
        Map<Wheel, Integer> requiredWheels = new HashMap<>();

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

    // =========================================================================
    // SIMULACIÓN SIMPLE
    // Sin eventos externos. Operarios asignados aleatoriamente.
    // =========================================================================

    public void simpleSim() {

        validateStock();

        List<Operator> operators = dataStore.getOperators();
        assignOperators(operators);

        System.out.println("[SIMPLE] Iniciando simulación simple...");

        while (!allLinesEmpty()) {
            updateAllLines();
            sleep();
        }

        System.out.println("[SIMPLE] Simulación finalizada.");
    }

    // =========================================================================
    // SIMULACIÓN COMPLEJA
    // Entran en juego los mecánicos. Al menos uno de cada perfil (eficiente y
    // estándar) repara al menos 2 averías en cada cadena.
    // =========================================================================

    public void complexSim() {

        validateStock();

        List<Operator> operators = dataStore.getOperators();
        assignOperators(operators);

        List<Mechanic> mechanics = dataStore.getMechanics();
        validateMechanicsForComplexSim(mechanics);

        System.out.println("[COMPLEJA] Iniciando simulación compleja...");

        // Para cada cadena registramos cuántas reparaciones ha acumulado
        Map<VehicleType, Integer> repairsDone = new HashMap<>();
        for (VehicleType vt : VehicleType.values()) repairsDone.put(vt, 0);

        // Mínimo 2 reparaciones por cadena (1 eficiente + 1 estándar = 2)
        final int MIN_REPAIRS_PER_LINE = 2;

        // Seleccionar un mecánico eficiente y uno estándar de la lista
        Mechanic efficientMechanic = getEfficientMechanic(mechanics);
        Mechanic standardMechanic  = getStandardMechanic(mechanics);

        int tick = 0;

        while (!allLinesEmpty()) {

            tick++;

            // Introducir avería aleatoria en alguna cadena que aún no haya
            // alcanzado el mínimo de reparaciones
            for (VehicleType vt : VehicleType.values()) {

                int done = repairsDone.get(vt);

                // Provocar avería cada 4 ticks si aún faltan reparaciones
                if (tick % 4 == 0 && done < MIN_REPAIRS_PER_LINE) {

                    // Elegir el mecánico según la reparación que toca:
                    // primera reparación → eficiente, segunda → estándar
                    Mechanic mechanic = (done == 0) ? efficientMechanic : standardMechanic;

                    System.out.println("[AVERÍA] Avería en cadena " + vt +
                            " | Reparando con mecánico " + mechanic.getPerfil() +
                            " (" + mechanic.getName() + ")");


                    int repairTime = mechanic.getWorkTime();
                    System.out.println("[REPARACIÓN] Tiempo de reparación: " + repairTime + " tick(s)");

                    for (int r = 0; r < repairTime; r++) {
                        sleep();
                    }

                    mechanic.updateWorker();
                    repairsDone.put(vt, done + 1);

                    System.out.println("[REPARACIÓN] Cadena " + vt + " reparada. " +
                            "Total reparaciones en esta cadena: " + repairsDone.get(vt));
                }
            }

            updateAllLines();
            sleep();
        }

        // Forzar las reparaciones pendientes si la simulación terminó antes
        // (cuando hay pocos vehículos y la cadena se vacía rápido)
        for (VehicleType vt : VehicleType.values()) {
            int done = repairsDone.get(vt);
            while (done < MIN_REPAIRS_PER_LINE) {
                Mechanic mechanic = (done == 0) ? efficientMechanic : standardMechanic;
                System.out.println("[REPARACIÓN FORZADA] Cadena " + vt +
                        " | Mecánico " + mechanic.getPerfil());
                int repairTime = mechanic.getWorkTime();
                for (int r = 0; r < repairTime; r++) sleep();
                mechanic.updateWorker();
                done++;
                repairsDone.put(vt, done);
            }
        }

        System.out.println("[COMPLEJA] Simulación compleja finalizada.");
        printRepairSummary(repairsDone);
    }

    // =========================================================================
    // SIMULACIÓN MUY COMPLEJA
    // Perfiles de operarios + mecánicos + administrador del sistema.
    // 2-3 problemas por cadena + al menos 1 apagón gestionado por el admin.
    //
    //  - Apagón: todos los trabajadores se detienen.
    //    Admin tarda 2 ticks en restaurar el sistema de gestión y
    //    3 ticks adicionales en restaurar las cadenas de montaje.
    //  - Averías mecánicas: igual que en la simulación compleja pero
    //    con entre 2 y 3 por cadena.
    // =========================================================================

    public void veryComplexSim() {

        validateStock();

        List<Operator> operators = dataStore.getOperators();
        assignOperators(operators);

        List<Mechanic> mechanics = dataStore.getMechanics();
        validateMechanicsForComplexSim(mechanics);

        SystemAdministrator admin = dataStore.getSystemAdministrator();
        if (admin == null) {
            throw new IllegalStateException(
                    "Se necesita un Administrador del Sistema registrado para la simulación muy compleja"
            );
        }

        System.out.println("[MUY COMPLEJA] Iniciando simulación muy compleja...");
        System.out.println("[MUY COMPLEJA] Administrador: " + admin.getName() + " " + admin.getLastName());

        // Entre 2 y 3 reparaciones por cadena
        Random rng = new Random();
        Map<VehicleType, Integer> repairsTarget = new HashMap<>();
        Map<VehicleType, Integer> repairsDone   = new HashMap<>();
        for (VehicleType vt : VehicleType.values()) {
            repairsTarget.put(vt, 2 + rng.nextInt(2)); // [2, 3]
            repairsDone.put(vt, 0);
        }

        Mechanic efficientMechanic = getEfficientMechanic(mechanics);
        Mechanic standardMechanic  = getStandardMechanic(mechanics);

        // Al menos un apagón durante la simulación
        boolean blackoutDone = false;
        // Programar el apagón en el tick 5 (suficientemente pronto pero no inmediato)
        final int BLACKOUT_TICK = 5;

        int tick = 0;

        while (!allLinesEmpty()) {

            tick++;

            // ---- APAGÓN ----
            if (tick == BLACKOUT_TICK && !blackoutDone) {
                System.out.println("\n[APAGÓN] ¡Se ha producido un apagón!");
                System.out.println("[APAGÓN] Todos los trabajadores se detienen.");
                System.out.println("[APAGÓN] El administrador " + admin.getName() +
                        " comienza la restauración...");

                // 2 ticks para restaurar el sistema de gestión
                System.out.println("[APAGÓN] Restaurando sistema de gestión (2 ticks)...");
                for (int i = 0; i < 2; i++) sleep();

                // 3 ticks para restaurar las cadenas de montaje
                System.out.println("[APAGÓN] Restaurando cadenas de montaje (3 ticks)...");
                for (int i = 0; i < 3; i++) sleep();

                System.out.println("[APAGÓN] Sistema completamente restaurado por " +
                        admin.getName() + " " + admin.getLastName() + ".\n");
                blackoutDone = true;
            }

            // ---- AVERÍAS MECÁNICAS ----
            for (VehicleType vt : VehicleType.values()) {

                int done   = repairsDone.get(vt);
                int target = repairsTarget.get(vt);

                // Avería cada 3 ticks mientras no se alcance el objetivo
                if (tick % 3 == 0 && done < target) {

                    // Alternar: posición par → eficiente, impar → estándar
                    Mechanic mechanic = (done % 2 == 0) ? efficientMechanic : standardMechanic;

                    System.out.println("[AVERÍA] Avería en cadena " + vt +
                            " | Reparación " + (done + 1) +
                            " | Mecánico " + mechanic.getPerfil() +
                            " (" + mechanic.getName() + ")");

                    int repairTime = mechanic.getWorkTime();
                    System.out.println("[REPARACIÓN] Tiempo: " + repairTime + " tick(s)");

                    for (int r = 0; r < repairTime; r++) sleep();

                    mechanic.updateWorker();
                    repairsDone.put(vt, done + 1);

                    System.out.println("[REPARACIÓN] Cadena " + vt + " lista. " +
                            "Reparaciones: " + repairsDone.get(vt) + "/" + target);
                }
            }

            updateAllLines();
            sleep();
        }

        // Forzar apagón si la simulación fue demasiado corta para alcanzar el tick
        if (!blackoutDone) {
            System.out.println("\n[APAGÓN] Apagón al finalizar la producción...");
            for (int i = 0; i < 5; i++) sleep(); // 2 + 3
            System.out.println("[APAGÓN] Sistema restaurado por " +
                    admin.getName() + " " + admin.getLastName() + ".\n");
        }

        // Forzar reparaciones pendientes
        for (VehicleType vt : VehicleType.values()) {
            int done   = repairsDone.get(vt);
            int target = repairsTarget.get(vt);
            while (done < target) {
                Mechanic mechanic = (done % 2 == 0) ? efficientMechanic : standardMechanic;
                System.out.println("[REPARACIÓN FORZADA] Cadena " + vt +
                        " | Mecánico " + mechanic.getPerfil());
                int repairTime = mechanic.getWorkTime();
                for (int r = 0; r < repairTime; r++) sleep();
                mechanic.updateWorker();
                done++;
                repairsDone.put(vt, done);
            }
        }

        System.out.println("[MUY COMPLEJA] Simulación muy compleja finalizada.");
        printRepairSummary(repairsDone);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // =========================================================================

    /** Asigna operarios aleatoriamente a todos los módulos de todas las líneas. */
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

    /**
     * Valida que existan mecánicos de ambos perfiles para la simulación compleja/muy compleja.
     */
    private void validateMechanicsForComplexSim(List<Mechanic> mechanics) {

        if (mechanics.size() < 2) {
            throw new IllegalStateException(
                    "Se necesitan al menos 2 mecánicos (uno eficiente y uno estándar) " +
                            "para iniciar la simulación compleja"
            );
        }

        boolean hasEfficient = mechanics.stream().anyMatch(m -> "EFICIENTE".equals(m.getPerfil()));
        boolean hasStandard  = mechanics.stream().anyMatch(m -> "ESTANDAR".equals(m.getPerfil()));

        if (!hasEfficient) {
            throw new IllegalStateException(
                    "Se necesita al menos un mecánico EFICIENTE (> 20 reparaciones) " +
                            "para la simulación compleja"
            );
        }
        if (!hasStandard) {
            throw new IllegalStateException(
                    "Se necesita al menos un mecánico ESTÁNDAR (≤ 20 reparaciones) " +
                            "para la simulación compleja"
            );
        }
    }

    /** Devuelve el primer mecánico eficiente de la lista. */
    private Mechanic getEfficientMechanic(List<Mechanic> mechanics) {
        return mechanics.stream()
                .filter(m -> "EFICIENTE".equals(m.getPerfil()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay mecánico eficiente"));
    }

    /** Devuelve el primer mecánico estándar de la lista. */
    private Mechanic getStandardMechanic(List<Mechanic> mechanics) {
        return mechanics.stream()
                .filter(m -> "ESTANDAR".equals(m.getPerfil()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay mecánico estándar"));
    }

    /** Avanza un tick en todas las líneas de montaje. */
    private void updateAllLines() {
        for (VehicleType vehicleType : VehicleType.values()) {
            dataStore.getAssemblyLine(vehicleType).updateLine();
        }
    }

    /** Pausa de medio segundo entre ticks. */
    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulación interrumpida", e);
        }
    }

    /** Comprueba si no quedan vehículos en espera ni módulos activos en ninguna línea. */
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

    /** Muestra un resumen de las reparaciones realizadas por cadena. */
    private void printRepairSummary(Map<VehicleType, Integer> repairsDone) {
        System.out.println("\n--- RESUMEN DE REPARACIONES ---");
        for (VehicleType vt : VehicleType.values()) {
            System.out.println("  Cadena " + vt + ": " + repairsDone.get(vt) + " reparación(es)");
        }
    }
}