package AssemblyLine;

import DataStore.DataStore;
import Observers.AssemblyLineObserver;
import Vehicles.Vehicle;

import java.util.List;


/**
 * Representa las distintas fases de ensamblaje.
 *
 * Cada fase encapsula su propia lógica de aplicación
 * sobre un vehículo y el consumo de componentes.
 */
public enum AssemblyPhase {

    CHASIS {
        @Override
        public void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers) {
            // El chasis no consume componentes del almacén
        }
    },
    MOTOR {
        @Override
        public void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers) {
            //si ya tiene el componente montado no lo vuelve a montar
            if(car.getEngine() != null) return;
            // Verifica disponibilidad antes de consumir componentes
            if (ds.getEngines().getStock(config.getEngine()) <= 0)
                throw new IllegalStateException("No hay motores en stock " + config.getEngine());
            ds.getEngines().remove(config.getEngine());
            car.setEngine(config.getEngine());
            for (AssemblyLineObserver o : observers)
                o.onComponentConsumed("Motor", ds.getEngines().getStock(config.getEngine()));
        }
    },
    TAPICERIA {
        @Override
        public void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers) {
            //si ya tiene el componente montado no lo vuelve a montar
            if(car.getUpholstery() != null) return;
            // Verifica disponibilidad antes de consumir componentes
            if (ds.getUpholsteries().getStock(config.getUpholstery()) <= 0)
                throw new IllegalStateException("No hay tapicerías en stock");
            ds.getUpholsteries().remove(config.getUpholstery());
            car.setUpholstery(config.getUpholstery());
            for (AssemblyLineObserver o : observers)
                o.onComponentConsumed("Tapicería", ds.getUpholsteries().getStock(config.getUpholstery()));
        }
    },
    RUEDAS {
        @Override
        public void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers) {
            //si ya tiene el componente montado no lo vuelve a montar
            if(car.getWheel() != null) return;
            // Verifica disponibilidad antes de consumir componentes
            if (ds.getWheels().getStock(config.getWheel()) <= 3)
                throw new IllegalStateException("No hay ruedas suficientes en stock");
            // Un vehículo requiere cuatro ruedas
            for (int i = 0; i < 4; i++) {
                ds.getWheels().remove(config.getWheel());
            }
            car.setWheel(config.getWheel());
            for (AssemblyLineObserver o : observers)
                o.onComponentConsumed("Ruedas", ds.getWheels().getStock(config.getWheel()));
        }
    };

    /**
     * Aplica la lógica de ensamblaje correspondiente a la fase.
     *
     * @param car vehículo procesado
     * @param config configuración actual de la línea
     * @param ds almacén de componentes
     * @param observers observadores de eventos de ensamblaje
     */
    public abstract void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers);
}