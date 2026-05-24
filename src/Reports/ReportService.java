package Reports;

import Components.EngineType;
import Components.UpholsteryType;
import Components.WheelType;
import DataStore.DataStore;
import Vehicles.Vehicle;
import Vehicles.VehicleType;
import Workers.Operator;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servicio de generación de informes del sistema de producción.
 *
 * Permite consultar operarios, vehículos ensamblados,
 * configuraciones más producidas y estadísticas por fecha.
 *
 * Actúa como capa de reporting sobre el DataStore.
 */
public class ReportService {

    // Formateador de fechas para reportes
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    private final DataStore dataStore;

    public ReportService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // Listado de operarios con filtro por perfil y ordenacion
    public void reportOperarios(String profileFilter, boolean alphabeticalOrder) {

        List<Operator> list = new ArrayList<>(dataStore.getOperators());

        if (profileFilter != null) {
            list.removeIf(o -> !profileFilter.equalsIgnoreCase(o.getPerfil()));
        }

        if (alphabeticalOrder) {
            list.sort(Comparator
                    .comparing(Operator::getLastName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Operator::getName, String.CASE_INSENSITIVE_ORDER));
        } else {
            list.sort(Comparator.comparingInt(Operator::getNumOfAssemblies).reversed());
        }

        System.out.println("\n=== LISTADO DE OPERARIOS ===");
        System.out.println("Filtro: " + (profileFilter != null ? profileFilter : "TODOS") +
                " | Orden: " + (alphabeticalOrder ? "ALFABETICO" : "POR MONTAJES"));

        if (list.isEmpty()) {
            System.out.println("  No hay operarios que coincidan.");
            return;
        }

        System.out.printf("  %-25s %-12s %s%n", "NOMBRE", "PERFIL", "MONTAJES");
        for (Operator op : list) {
            System.out.printf("  %-25s %-12s %d%n",
                    op.getLastName() + ", " + op.getName(),
                    op.getPerfil(),
                    op.getNumOfAssemblies());
        }
        System.out.println("  Total: " + list.size() + " operario(s).");
    }

    // Listado de vehiculos ensamblados con filtros por componentes y ordenacion
    public void reportVehiculos(EngineType engineFilter, UpholsteryType upholsteryFilter,
                                WheelType wheelFilter, boolean alphabeticalOrder) {

        List<Vehicle> list = new ArrayList<>(dataStore.getVehicles().getInventory().keySet());

        if (engineFilter != null)
            list.removeIf(v -> v.getEngine() == null || v.getEngine().getType() != engineFilter);
        if (upholsteryFilter != null)
            list.removeIf(v -> v.getUpholstery() == null || v.getUpholstery().getType() != upholsteryFilter);
        if (wheelFilter != null)
            list.removeIf(v -> v.getWheel() == null || v.getWheel().getType() != wheelFilter);

        if (alphabeticalOrder)
            list.sort(Comparator.comparing(Vehicle::getColor, String.CASE_INSENSITIVE_ORDER));

        System.out.println("\n=== VEHICULOS ENSAMBLADOS ===");
        System.out.println("Motor: " + (engineFilter != null ? engineFilter : "TODOS") +
                " | Tapiceria: " + (upholsteryFilter != null ? upholsteryFilter : "TODAS") +
                " | Ruedas: " + (wheelFilter != null ? wheelFilter : "TODAS"));

        if (list.isEmpty()) {
            System.out.println("  No hay vehiculos que coincidan.");
            return;
        }

        for (Vehicle v : list) {
            String date = v.getAssembledDate() != null ? SDF.format(v.getAssembledDate()) : "-";
            System.out.println("  " + v);
            System.out.println("    Motor:     " + (v.getEngine()     != null ? v.getEngine()     : "-"));
            System.out.println("    Tapiceria: " + (v.getUpholstery() != null ? v.getUpholstery() : "-"));
            System.out.println("    Ruedas:    " + (v.getWheel()      != null ? v.getWheel()      : "-"));
            System.out.println("    Cadena:    " + (v.getAssembledLineType() != null ? v.getAssembledLineType() : "-"));
            System.out.println("    Fecha:     " + date + " | Unidades: " + dataStore.getVehicles().getStock(v));
            System.out.println();
        }
        System.out.println("  Total: " + list.size() + " modelo(s).");
    }

    // Configuraciones de componentes ordenadas por numero de unidades producidas
    public void reportConfiguracionesMasEnsambladas() {

        Map<Vehicle, Integer> inventory = dataStore.getVehicles().getInventory();

        if (inventory.isEmpty()) {
            System.out.println("\nNo hay vehiculos ensamblados todavia.");
            return;
        }

        Map<String, Integer> count = new HashMap<>();
        for (Map.Entry<Vehicle, Integer> entry : inventory.entrySet()) {
            String key = getConfigKey(entry.getKey());
            count.put(key, count.getOrDefault(key, 0) + entry.getValue());
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(count.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        System.out.println("\n=== CONFIGURACIONES MAS ENSAMBLADAS ===");
        System.out.printf("  %-50s %s%n", "CONFIGURACION", "UNIDADES");
        for (Map.Entry<String, Integer> e : list) {
            System.out.printf("  %-50s %d%n", e.getKey(), e.getValue());
        }
    }

    private String getConfigKey(Vehicle v) {
        String engine = v.getEngine()     != null ? v.getEngine().toString()     : "Sin motor";
        String uph    = v.getUpholstery() != null ? v.getUpholstery().toString() : "Sin tapiceria";
        String wheel  = v.getWheel()      != null ? v.getWheel().toString()       : "Sin ruedas";
        return engine + " | " + uph + " | " + wheel;
    }

    // Vehicles produced in each line on a given date
    public void reportCadenasPorFecha(Date date) {

        String targetDate = SDF.format(date);
        boolean hasResults = false;

        System.out.println("\n=== CADENAS DE MONTAJE - FECHA: " + targetDate + " ===");

        for (VehicleType lineType : VehicleType.values()) {

            List<Vehicle> finished = new ArrayList<>();
            for (Vehicle v : dataStore.getVehicles().getInventory().keySet()) {
                if (v.getAssembledLineType() == lineType
                        && v.getAssembledDate() != null
                        && SDF.format(v.getAssembledDate()).equals(targetDate)) {
                    finished.add(v);
                }
            }

            if (finished.isEmpty()) continue;

            finished.sort(Comparator.comparing(Vehicle::getColor, String.CASE_INSENSITIVE_ORDER));
            hasResults = true;

            System.out.println("\n  Cadena: " + lineType);
            for (Vehicle v : finished) {
                System.out.println("    " + v);
                System.out.println("      Motor:     " + (v.getEngine()     != null ? v.getEngine()     : "-"));
                System.out.println("      Tapiceria: " + (v.getUpholstery() != null ? v.getUpholstery() : "-"));
                System.out.println("      Ruedas:    " + (v.getWheel()      != null ? v.getWheel()      : "-"));
                System.out.println("      Unidades:  " + dataStore.getVehicles().getStock(v));
            }
        }

        if (!hasResults)
            System.out.println("  No se ensamblaron vehiculos en esa fecha.");
    }
}