package DataStore;

import java.util.HashMap;
import java.util.Map;

public class Stock<T> {

    private Map<T, Integer> inventory = new HashMap<>();

    public void add(T item) {
        inventory.put(item,
                inventory.getOrDefault(item, 0) + 1);
    }

    public void remove(T item) {

        if(!inventory.containsKey(item)) {
            return;
        }

        int quantity = inventory.get(item);

        if(quantity <= 1) {
            inventory.remove(item);
        } else {
            inventory.put(item, quantity - 1);
        }
    }

    public int getStock(T item) {

        return inventory.getOrDefault(item, 0);
    }

    public Map<T, Integer> getInventory() {

        return inventory;
    }
}