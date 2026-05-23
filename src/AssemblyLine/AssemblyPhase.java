package AssemblyLine;

import DataStore.DataStore;
import Observers.AssemblyLineObserver;
import Vehicles.Vehicle;

import java.util.List;

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
            if (ds.getEngines().getStock(config.getEngine()) <= 0)
                throw new IllegalStateException("No hay motores en stock");
            ds.getEngines().remove(config.getEngine());
            car.setEngine(config.getEngine());
            for (AssemblyLineObserver o : observers)
                o.onComponentConsumed("Motor", ds.getEngines().getStock(config.getEngine()));
        }
    },
    TAPICERIA {
        @Override
        public void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers) {
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
            if (ds.getWheels().getStock(config.getWheel()) <= 0)
                throw new IllegalStateException("No hay ruedas en stock");
            ds.getWheels().remove(config.getWheel());
            car.setWheel(config.getWheel());
            for (AssemblyLineObserver o : observers)
                o.onComponentConsumed("Ruedas", ds.getWheels().getStock(config.getWheel()));
        }
    };

    public abstract void apply(Vehicle car, LineConfig config, DataStore ds, List<AssemblyLineObserver> observers);
}