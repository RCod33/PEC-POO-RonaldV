
import AssemblyLine.*;
import Components.*;
import DataStore.*;
import Observers.AssemblyLineObserver;
import Scheduler.Scheduler;
import Vehicles.*;
import Workers.*;
import DataStore.*;
import java.util.*;

/**
 * Suite de tests unitarios para el proyecto Fábrica de Vehículos.
 * Diseñado para ejecutarse en IntelliJ IDEA sin dependencias externas.
 */
public class TestRunner {

    // ─── CONTADORES Y SEGUIMIENTO ────────────────────────────────────────────
    private static int passed = 0;
    private static int failed = 0;
    private static List<String> failures = new ArrayList<>();

    // ─── ARBITROS DE ASERCIÓN (HELPERS) ──────────────────────────────────────
    static void assertTrue(String name, boolean condition) {
        if (condition) {
            System.out.println("  ✔ " + name);
            passed++;
        } else {
            System.out.println("  ✘ " + name);
            failed++;
            failures.add(name);
        }
    }

    static void assertEquals(String name, Object expected, Object actual) {
        boolean ok = Objects.equals(expected, actual);
        if (ok) {
            System.out.println("  ✔ " + name);
            passed++;
        } else {
            System.out.println("  ✘ " + name + "  [esperado=" + expected + " actual=" + actual + "]");
            failed++;
            failures.add(name);
        }
    }

    static void assertThrows(String name, Runnable code) {
        try {
            code.run();
            System.out.println("  ✘ " + name + "  [no lanzó excepción]");
            failed++;
            failures.add(name);
        } catch (Exception e) {
            System.out.println("  ✔ " + name + "  [" + e.getClass().getSimpleName() + "]");
            passed++;
        }
    }

    static void section(String title) {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("══════════════════════════════════════════");
    }

    // ─── GENERADORES DE DATOS DE PRUEBA ──────────────────────────────────────
    static String validDNI(int number) {
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return String.format("%08d", number) + letras.charAt(number % 23);
    }

    static Operator makeOperator(int assemblies) {
        return new Operator("Ana", "López", "Martín",
                validDNI(12345678), "Calle A 1", "28/12345678/90",
                1500.0, new Date(), assemblies);
    }

    static Operator makeEfficientOperator(int seed) {
        int num = 10000000 + seed;
        String ss = String.format("28/%08d/90", num);
        return new Operator("Op" + seed, "Apellido", "Seg",
                validDNI(num), "Calle " + seed, ss, 1500.0, new Date(), 15);
    }

    static Mechanic makeMechanic(int repairs) {
        return new Mechanic("Pedro", "García", "Ruiz",
                validDNI(11111111), "Calle B 2", "28/11111111/90",
                1400.0, new Date(), repairs);
    }

    /** Limpia la instancia Singleton de DataStore antes de cada test */
    static void resetDataStore() {
        try {
            java.lang.reflect.Field f = DataStore.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
        } catch (Exception ex) {
            System.out.println("  [WARN] No se pudo resetear DataStore: " + ex.getMessage());
        }
    }

    // =========================================================================
    // BLOQUES DE TEST
    // =========================================================================

    static void testEngine() {
        section("Engine");
        Engine e1 = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Engine e2 = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Engine e3 = new Engine(EngineType.ELECTRICO, 0, 150, 0);

        assertEquals("getType()",            EngineType.GASOLINA, e1.getType());
        assertEquals("getDisplacement()",    1600,                e1.getDisplacement());
        assertEquals("getPower()",           120,                 e1.getPower());
        assertEquals("getCylinders()",       4,                   e1.getNumberOfCylinders());
        assertTrue("equals mismo objeto",    e1.equals(e1));
        assertTrue("equals copia igual",     e1.equals(e2));
        assertTrue("no equals distinto",     !e1.equals(e3));
        assertTrue("hashCode consistente",   e1.hashCode() == e2.hashCode());
        assertTrue("toString no vacío",      !e1.toString().isEmpty());
    }

    static void testUpholstery() {
        section("Upholstery");
        Upholstery u1 = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Upholstery u2 = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Upholstery u3 = new Upholstery(UpholsteryType.TELA,  "Rojo",  3.0);

        assertEquals("getType()",            UpholsteryType.CUERO, u1.getType());
        assertEquals("getColor()",           "Negro",              u1.getColor());
        assertEquals("getSquareMeters()",    5.0,                  u1.getSquareMeters());
        assertTrue("equals copia igual",     u1.equals(u2));
        assertTrue("no equals distinto",     !u1.equals(u3));
        assertTrue("hashCode consistente",   u1.hashCode() == u2.hashCode());
        assertThrows("squareMeters = 0 lanza excepción",
                () -> new Upholstery(UpholsteryType.TELA, "Azul", 0));
        assertThrows("squareMeters negativo lanza excepción",
                () -> new Upholstery(UpholsteryType.TELA, "Azul", -1));
        assertThrows("color null lanza excepción",
                () -> new Upholstery(UpholsteryType.TELA, null, 2.0));
        assertThrows("color vacío lanza excepción",
                () -> new Upholstery(UpholsteryType.TELA, "  ", 2.0));
    }

    static void testWheel() {
        section("Wheel");
        Wheel w1 = new Wheel(WheelType.DEPORTIVO, 225, 18, 500, 240);
        Wheel w2 = new Wheel(WheelType.DEPORTIVO, 225, 18, 500, 240);
        Wheel w3 = new Wheel(WheelType.NORMAL,    195, 15, 400, 180);

        assertEquals("getType()",               WheelType.DEPORTIVO, w1.getType());
        assertEquals("getWidthMm()",            225,                 w1.getWidthMm());
        assertEquals("getRimDiameterInches()",  18,                  w1.getRimDiameterInches());
        assertEquals("getLoadIndexKg()",        500,                 w1.getLoadIndexKg());
        assertEquals("getSpeedCode()",          240,                 w1.getSpeedCode());
        assertTrue("equals copia igual",        w1.equals(w2));
        assertTrue("no equals distinto",        !w1.equals(w3));
        assertTrue("hashCode consistente",      w1.hashCode() == w2.hashCode());
        assertThrows("widthMm = 0 lanza excepción",     () -> new Wheel(WheelType.NORMAL,  0, 15, 400, 180));
        assertThrows("rimDiameter = 0 lanza excepción", () -> new Wheel(WheelType.NORMAL, 195,  0, 400, 180));
        assertThrows("loadIndex = 0 lanza excepción",   () -> new Wheel(WheelType.NORMAL, 195, 15,   0, 180));
        assertThrows("speedCode = 0 lanza excepción",   () -> new Wheel(WheelType.NORMAL, 195, 15, 400,   0));
    }

    static void testVehicle() {
        section("Vehicle");
        Vehicle v1 = new Vehicle(VehicleType.TURISMO, "Rojo", 5, 1200.0, 1800.0);
        Vehicle v3 = new Vehicle(VehicleType.DEPORTIVO, "Azul", 2, 900.0, 1400.0);

        assertEquals("getType()",             VehicleType.TURISMO, v1.getType());
        assertEquals("getColor()",            "Rojo",              v1.getColor());
        assertEquals("getNumberOfSeats()",    5,                   v1.getNumberOfSeats());
        assertEquals("getTareWeight()",       1200.0,              v1.getTareWeight());
        assertEquals("getMaxAllowedWeight()", 1800.0,              v1.getMaxAllowedWeight());
        assertTrue("engine null por defecto",     v1.getEngine()     == null);
        assertTrue("upholstery null por defecto", v1.getUpholstery() == null);
        assertTrue("wheel null por defecto",      v1.getWheel()      == null);

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        v1.setEngine(e);     assertTrue("setEngine funciona",     v1.getEngine() == e);
        v1.setUpholstery(u); assertTrue("setUpholstery funciona", v1.getUpholstery() == u);
        v1.setWheel(w);      assertTrue("setWheel funciona",      v1.getWheel() == w);

        Vehicle v2 = new Vehicle(VehicleType.TURISMO, "Rojo", 5, 1200.0, 1800.0);
        assertTrue("equals copia igual",  v1.equals(v2));
        assertTrue("no equals distinto", !v1.equals(v3));
        assertTrue("toString no vacío",  !v1.toString().isEmpty());
    }

    static void testWorker() {
        section("Worker / Operator");
        Operator op = makeOperator(5);

        assertEquals("getName()",              "Ana",      op.getName());
        assertEquals("getLastName()",          "López",    op.getLastName());
        assertEquals("getRole()",              "Operario", op.getRole());
        assertEquals("getPerfil() estándar",   "ESTANDAR", op.getPerfil());
        assertEquals("getWorkTime() estándar", 3,           op.getWorkTime());

        Operator opEficiente = makeOperator(11);
        assertEquals("getPerfil() eficiente",    "EFICIENTE", opEficiente.getPerfil());
        assertEquals("getWorkTime() eficiente",  1,           opEficiente.getWorkTime());
        assertEquals("perfil con 10 montajes exactos", "ESTANDAR", makeOperator(10).getPerfil());
        assertEquals("perfil con 11 montajes",         "EFICIENTE", makeOperator(11).getPerfil());

        int antes = op.getNumOfAssemblies();
        op.updateWorker();
        assertEquals("updateWorker incrementa", antes + 1, op.getNumOfAssemblies());

        assertThrows("DNI inválido lanza excepción",
                () -> new Operator("X","Y","Z","00000000A","dir","28/00000000/00",1000.0, new Date(), 0));
        assertThrows("Salario negativo lanza excepción",
                () -> new Operator("X","Y","Z", validDNI(99999999),"dir","28/99999999/00",-1.0, new Date(), 0));
        assertThrows("numOfAssemblies negativo lanza excepción",
                () -> makeOperator(-1));

        Operator op2 = new Operator("Otro","Apellido","Seg",
                validDNI(12345678),"Otra dir","28/12345678/90",2000.0, new Date(), 0);
        assertTrue("equals mismo DNI",       op.equals(op2));
        assertTrue("hashCode mismo DNI",     op.hashCode() == op2.hashCode());
    }

    static void testMechanic() {
        section("Mechanic");
        Mechanic m = makeMechanic(5);

        assertEquals("getRole()",              "Mecánico de Cinta", m.getRole());
        assertEquals("getPerfil() estándar",   "ESTANDAR",           m.getPerfil());
        assertEquals("getPerfil() eficiente",  "EFICIENTE",          makeMechanic(21).getPerfil());
        assertEquals("getWorkTime() eficiente", 1,                   makeMechanic(21).getWorkTime());
        assertEquals("perfil con 20 reparaciones", "ESTANDAR",       makeMechanic(20).getPerfil());

        for (int i = 0; i < 30; i++) {
            int t = makeMechanic(1).getWorkTime();
            assertTrue("getWorkTime() estándar en rango [2,5]", t >= 2 && t <= 5);
        }

        int antes = m.getNumOfRepairs();
        m.updateWorker();
        assertEquals("updateWorker incrementa reparaciones", antes + 1, m.getNumOfRepairs());
        assertThrows("numOfRepairs negativo lanza excepción", () -> makeMechanic(-1));
    }

    static void testStock() {
        section("Stock<Engine>");
        Stock<Engine> stock = new Stock<Engine>();
        Engine e  = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Engine e2 = new Engine(EngineType.ELECTRICO, 0,   200, 0);

        assertEquals("stock inicial es 0", 0, stock.getStock(e));
        stock.add(e);
        assertEquals("stock tras 1 add = 1", 1, stock.getStock(e));
        stock.add(e);
        assertEquals("stock tras 2 adds = 2", 2, stock.getStock(e));
        stock.remove(e);
        assertEquals("stock tras 1 remove = 1", 1, stock.getStock(e));
        stock.remove(e);
        assertEquals("stock tras 2 removes = 0", 0, stock.getStock(e));
        assertThrows("remove sin stock lanza excepción", () -> stock.remove(e));

        stock.add(e);
        stock.add(e2);
        assertEquals("inventario tiene 2 entradas", 2, stock.getInventory().size());
        assertThrows("getInventory() es inmutable", () -> stock.getInventory().put(e, 99));
    }

    static void testLineConfig() {
        section("LineConfig");
        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);
        LineConfig cfg = new LineConfig(e, u, w);

        assertTrue("getEngine()",     cfg.getEngine()     == e);
        assertTrue("getUpholstery()", cfg.getUpholstery() == u);
        assertTrue("getWheel()",      cfg.getWheel()      == w);

        cfg.validateLine();
        assertTrue("validateLine() completo no lanza", true);
        assertThrows("validateLine sin motor lanza excepción",
                () -> new LineConfig(null, u, w).validateLine());
        assertThrows("validateLine sin tapicería lanza excepción",
                () -> new LineConfig(e, null, w).validateLine());
        assertThrows("validateLine sin ruedas lanza excepción",
                () -> new LineConfig(e, u, null).validateLine());
    }

    static void testAssemblyLineEndToEnd() {
        section("AssemblyLine end-to-end (1 vehículo, operarios eficientes)");
        resetDataStore();
        DataStore ds = DataStore.getInstance();

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        ds.getEngines().add(e);
        ds.getUpholsteries().add(u);
        ds.getWheels().add(w);

        AssemblyLine line = ds.getAssemblyLine(VehicleType.TURISMO);
        line.setConfig(new LineConfig(e, u, w));
        line.setModule(AssemblyPhase.CHASIS,    makeEfficientOperator(1));
        line.setModule(AssemblyPhase.MOTOR,     makeEfficientOperator(2));
        line.setModule(AssemblyPhase.TAPICERIA, makeEfficientOperator(3));
        line.setModule(AssemblyPhase.RUEDAS,    makeEfficientOperator(4));

        Vehicle v = new Vehicle(VehicleType.TURISMO, "Rojo", 5, 1200.0, 1800.0);
        line.addVehicle(v);

        int[] events = {0, 0, 0, 0};
        line.addObserver(new AssemblyLineObserver() {
            public void onVehicleEntered(Vehicle v)                                    { events[0]++; }
            public void onVehicleAdvanced(Vehicle v, AssemblyPhase f, AssemblyPhase t) { events[1]++; }
            public void onVehicleFinished(Vehicle v)                                   { events[2]++; }
            public void onComponentConsumed(String c, int r)                           { events[3]++; }
        });

        int ticks = 0;
        while (line.getFinishedVehicles().isEmpty() && ticks < 100) {
            line.updateLine();
            ticks++;
        }

        assertTrue("Vehículo terminado antes de 100 ticks", !line.getFinishedVehicles().isEmpty());
        assertTrue("Cola pendiente vacía al finalizar",      line.getPendingVehicles().isEmpty());

        Vehicle finished = line.getFinishedVehicles().iterator().next();
        assertTrue("Vehículo tiene motor",     finished.getEngine()     != null);
        assertTrue("Vehículo tiene tapicería", finished.getUpholstery() != null);
        assertTrue("Vehículo tiene ruedas",    finished.getWheel()      != null);

        assertEquals("Motor correcto",     e, finished.getEngine());
        assertEquals("Tapicería correcta", u, finished.getUpholstery());
        assertEquals("Ruedas correctas",   w, finished.getWheel());

        assertEquals("Stock motor consumido = 0",     0, ds.getEngines().getStock(e));
        assertEquals("Stock tapicería consumida = 0", 0, ds.getUpholsteries().getStock(u));
        assertEquals("Stock ruedas consumidas = 0",   0, ds.getWheels().getStock(w));

        assertTrue("onVehicleEntered disparado",       events[0] >= 1);
        assertTrue("onVehicleAdvanced disparado ≥ 3",  events[1] >= 3);
        assertTrue("onVehicleFinished disparado = 1",  events[2] == 1);
        assertTrue("onComponentConsumed disparado ≥ 3", events[3] >= 3);
    }

    static void testAssemblyLineMultipleVehicles() {
        section("AssemblyLine - 2 vehículos en secuencia");
        resetDataStore();
        DataStore ds = DataStore.getInstance();

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        ds.getEngines().add(e); ds.getEngines().add(e);
        ds.getUpholsteries().add(u); ds.getUpholsteries().add(u);
        ds.getWheels().add(w); ds.getWheels().add(w);

        AssemblyLine line = ds.getAssemblyLine(VehicleType.DEPORTIVO);
        line.setConfig(new LineConfig(e, u, w));

        for (AssemblyPhase phase : AssemblyPhase.values())
            line.setModule(phase, makeEfficientOperator(phase.ordinal() + 1));

        line.addVehicle(new Vehicle(VehicleType.DEPORTIVO, "Rojo",  2, 900.0, 1400.0));
        line.addVehicle(new Vehicle(VehicleType.DEPORTIVO, "Azul",  2, 900.0, 1400.0));

        int ticks = 0;
        while (line.getFinishedVehicles().size() < 2 && ticks < 200) {
            line.updateLine();
            ticks++;
        }

        assertEquals("2 vehículos terminados", 2, line.getFinishedVehicles().size());
        assertTrue("Cola pendiente vacía", line.getPendingVehicles().isEmpty());
    }

    static void testAssemblyLineNoStock() {
        section("AssemblyLine sin stock → excepción");
        resetDataStore();
        DataStore ds = DataStore.getInstance();

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        AssemblyLine line = ds.getAssemblyLine(VehicleType.DEPORTIVO);
        line.setConfig(new LineConfig(e, u, w));

        for (AssemblyPhase phase : AssemblyPhase.values())
            line.setModule(phase, makeEfficientOperator(phase.ordinal() + 1));

        line.addVehicle(new Vehicle(VehicleType.DEPORTIVO, "Azul", 2, 900.0, 1400.0));
        assertThrows("Falla con excepción si no hay stock",
                () -> { for (int i = 0; i < 20; i++) line.updateLine(); });
    }

    static void testAssemblyLineNoOperator() {
        section("AssemblyLine sin operario → excepción");
        resetDataStore();
        DataStore ds = DataStore.getInstance();

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        ds.getEngines().add(e);
        ds.getUpholsteries().add(u);
        ds.getWheels().add(w);

        AssemblyLine line = ds.getAssemblyLine(VehicleType.FURGONETA);
        line.setConfig(new LineConfig(e, u, w));

        line.addVehicle(new Vehicle(VehicleType.FURGONETA, "Blanco", 3, 1500.0, 3000.0));
        assertThrows("Falla con excepción si módulo sin operario", () -> line.updateLine());
    }

    static void testSchedulerValidateStock() {
        section("Scheduler.validateStock()");
        resetDataStore();
        DataStore ds = DataStore.getInstance();

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        for (VehicleType type : VehicleType.values())
            ds.getAssemblyLine(type).setConfig(new LineConfig(e, u, w));

        ds.getAssemblyLine(VehicleType.TURISMO).addVehicle(new Vehicle(VehicleType.TURISMO, "Rojo", 5, 1200.0, 1800.0));

        Scheduler sched = new Scheduler(ds);
        assertThrows("validateStock falla sin stock", sched::validateStock);

        ds.getEngines().add(e);
        ds.getUpholsteries().add(u);
        ds.getWheels().add(w);
        ds.getWheels().add(w);
        ds.getWheels().add(w);
        ds.getWheels().add(w);

        sched.validateStock();
        assertTrue("validateStock pasa con stock suficiente", true);
    }

    static void testDataStore() {
        section("DataStore singleton y búsquedas");
        resetDataStore();
        DataStore ds1 = DataStore.getInstance();
        DataStore ds2 = DataStore.getInstance();
        assertTrue("DataStore es singleton (misma referencia)", ds1 == ds2);

        Operator op = makeOperator(5);
        ds1.addWorker(op);

        Worker found = ds1.searchWorkerByDNI(validDNI(12345678));
        assertTrue("searchWorkerByDNI encuentra trabajador",      found != null);
        assertTrue("searchWorkerByDNI devuelve el correcto",      found.equals(op));

        Worker notFound = ds1.searchWorkerByDNI("00000000T");
        assertTrue("searchWorkerByDNI devuelve null si no existe", notFound == null);

        List<Worker> byName = ds1.searchWorkersByName("Ana");
        assertTrue("searchWorkersByName encuentra por nombre",    !byName.isEmpty());
        assertTrue("getOperators() devuelve ≥ 1",                 ds1.getOperators().size() >= 1);
        assertThrows("getWorkers() es inmutable",                  () -> ds1.getWorkers().add(op));

        assertTrue("getAssemblyLine TURISMO",   ds1.getAssemblyLine(VehicleType.TURISMO)   != null);
        assertTrue("getAssemblyLine DEPORTIVO", ds1.getAssemblyLine(VehicleType.DEPORTIVO) != null);
        assertTrue("getAssemblyLine FURGONETA", ds1.getAssemblyLine(VehicleType.FURGONETA) != null);
    }

    static void testAssemblyModule() {
        section("AssemblyModule - via AssemblyLine");
        resetDataStore();
        DataStore ds = DataStore.getInstance();

        Engine e     = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Upholstery u = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Wheel w      = new Wheel(WheelType.NORMAL, 195, 15, 400, 180);

        ds.getEngines().add(e);
        ds.getUpholsteries().add(u);
        ds.getWheels().add(w);


        AssemblyLine line = ds.getAssemblyLine(VehicleType.TURISMO);
        line.setConfig(new LineConfig(e, u, w));

        for (AssemblyPhase phase : AssemblyPhase.values())
            line.setModule(phase, makeEfficientOperator(phase.ordinal() + 1));

        for (AssemblyModule m : line.getModules())
            assertTrue("Módulo " + m.getPhase() + " libre al inicio", m.isFree());

        line.addVehicle(new Vehicle(VehicleType.TURISMO, "Verde", 5, 1200.0, 1800.0));
        line.updateLine();

        boolean anyOccupied = line.getModules().stream().anyMatch(m -> !m.isFree());
        assertTrue("Al menos un módulo ocupado tras primer tick", anyOccupied);

        AssemblyPhase[] phases = AssemblyPhase.values();
        List<AssemblyModule> modules = line.getModules();
        for (int i = 0; i < phases.length; i++)
            assertEquals("Módulo " + i + " tiene fase correcta", phases[i], modules.get(i).getPhase());
    }

    static void testOtherWorkers() {
        section("PlantManager y SystemAdministrator");
        PlantManager pm = new PlantManager("Carlos","Sanz","Gil",
                validDNI(22222222),"Calle C","28/22222222/90",2500.0, new Date());
        assertEquals("PlantManager getRole()", "Gestor de Planta", pm.getRole());

        SystemAdministrator sa = new SystemAdministrator("Laura","Vega","Ríos",
                validDNI(33333333),"Calle D","28/33333333/90",3000.0, new Date());
        assertEquals("SystemAdministrator getRole()", "Administrador del Sistema", sa.getRole());
    }

    static void testWorkerValidations() {
        section("Worker - validaciones SS y setters");
        Operator op = makeOperator(0);

        op.setNumeroSS("12/87654321/33");
        assertEquals("setNumeroSS válido acepta", "12/87654321/33", op.getNumeroSS());
        assertThrows("SS formato inválido lanza excepción", () -> op.setNumeroSS("1234567890"));
        assertThrows("SS null lanza excepción",             () -> op.setNumeroSS(null));

        op.setSalary(2000.0);
        assertEquals("setSalary válido", 2000.0, op.getSalary());
        assertThrows("setSalary negativo lanza excepción", () -> op.setSalary(-100.0));
        assertThrows("setSalary 0 lanza excepción",        () -> op.setSalary(0.0));
        assertThrows("setDNI inválido lanza excepción",    () -> op.setDNI("12345678A"));
    }

    static void testAssemblyLineStatus() {
        section("AssemblyLine.getStatus()");
        resetDataStore();
        DataStore ds = DataStore.getInstance();
        AssemblyLine line = ds.getAssemblyLine(VehicleType.TURISMO);

        List<String> status = line.getStatus();
        assertEquals("getStatus() devuelve 4 entradas", 4, status.size());

        for (String s : status)
            assertTrue("Cada entrada de status no está vacía", s != null && !s.isEmpty());

        assertTrue("Status inicial contiene VACIO", status.get(0).contains("VACIO"));
    }

    static void testEqualsAndHashCodeContracts() {
        section("Contratos equals/hashCode en todos los modelos");

        Engine e1 = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        Engine e2 = new Engine(EngineType.GASOLINA, 1600, 120, 4);
        assertTrue("Engine: reflexivo",         e1.equals(e1));
        assertTrue("Engine: simétrico",         e1.equals(e2) && e2.equals(e1));
        assertTrue("Engine: no equals null",    !e1.equals(null));
        assertTrue("Engine: no equals otro tipo", !e1.equals("string"));

        Upholstery u1 = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        Upholstery u2 = new Upholstery(UpholsteryType.CUERO, "Negro", 5.0);
        assertTrue("Upholstery: reflexivo",      u1.equals(u1));
        assertTrue("Upholstery: simétrico",      u1.equals(u2) && u2.equals(u1));
        assertTrue("Upholstery: no equals null", !u1.equals(null));

        Wheel w1 = new Wheel(WheelType.DEPORTIVO, 225, 18, 500, 240);
        Wheel w2 = new Wheel(WheelType.DEPORTIVO, 225, 18, 500, 240);
        assertTrue("Wheel: reflexivo",           w1.equals(w1));
        assertTrue("Wheel: simétrico",           w1.equals(w2) && w2.equals(w1));
        assertTrue("Wheel: no equals null",      !w1.equals(null));

        Vehicle v1 = new Vehicle(VehicleType.TURISMO, "Rojo", 5, 1200.0, 1800.0);
        Vehicle v2 = new Vehicle(VehicleType.TURISMO, "Rojo", 5, 1200.0, 1800.0);
        assertTrue("Vehicle: reflexivo",         v1.equals(v1));
        assertTrue("Vehicle: simétrico",         v1.equals(v2) && v2.equals(v1));
        assertTrue("Vehicle: no equals null",    !v1.equals(null));
    }

    // =========================================================================
    // EJECUTOR PRINCIPAL (MAIN)
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   SUITE DE TESTS - Fábrica de Vehículos  ║");
        System.out.println("╚══════════════════════════════════════════╝");

        testEngine();
        testUpholstery();
        testWheel();
        testVehicle();
        testWorker();
        testMechanic();
        testStock();
        testLineConfig();
        testAssemblyLineEndToEnd();
        testAssemblyLineMultipleVehicles();
        testAssemblyLineNoStock();
        testAssemblyLineNoOperator();
        testSchedulerValidateStock();
        testDataStore();
        testAssemblyModule();
        testOtherWorkers();
        testWorkerValidations();
        testAssemblyLineStatus();
        testEqualsAndHashCodeContracts();

        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  RESULTADO FINAL");
        System.out.println("══════════════════════════════════════════");
        System.out.printf("  ✔ Pasados : %d%n", passed);
        System.out.printf("  ✘ Fallidos: %d%n", failed);

        if (!failures.isEmpty()) {
            System.out.println("\n  Tests fallidos:");
            failures.forEach(f -> System.out.println("    - " + f));
        }
        System.out.println();
        System.exit(failed > 0 ? 1 : 0);
    }
}