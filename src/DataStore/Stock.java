package DataStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Estructura genérica de almacenamiento con control de stock.
 *
 * Mantiene una cantidad asociada a cada tipo de elemento.
 */
public class Stock<T> {

    // Mapa de inventario: elemento -> cantidad disponible
    private Map<T, Integer> inventory = new HashMap<>();

    public void add(T item) {
        inventory.put(item,
                inventory.getOrDefault(item, 0) + 1);
    }

    public void remove(T item) {

        if (!inventory.containsKey(item)) {
            throw new IllegalStateException("No hay stock del componente: " + item);
        }

        int quantity = inventory.get(item);

        if (quantity > 0) {
            inventory.put(item, quantity - 1);
        }
    }

    public int getStock(T item) {
        return inventory.getOrDefault(item, 0);
    }

    // Devuelve una vista inmutable del inventario para evitar modificaciones externas
    public Map<T, Integer> getInventory() {
        return Collections.unmodifiableMap(inventory);
    }
}