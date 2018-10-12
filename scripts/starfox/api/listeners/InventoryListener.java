package scripts.starfox.api.listeners;

import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api.util.Strings;
import scripts.starfox.api2007.Game07;
import scripts.starfox.api2007.Shop;
import scripts.starfox.api2007.banking.Bank;
import scripts.starfox.api2007.login.Login07;
import scripts.starfox.interfaces.listening.InventoryListening07;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The InventoryListener class allows you to listen for changes in the inventory.
 *
 * @author Nolan
 */
public final class InventoryListener
        extends ListenerThread {

    /**
     * The set of listeners that are being notified.
     */
    private final HashSet<InventoryListening07> listeners;

    /**
     * A map representing the last mapped inventory.
     * The key is the ID of an item, and the value is the stack or count of the item.
     */
    private HashMap<Integer, Integer> last_inventory;

    /**
     * Constructs a new InventoryListener.
     */
    public InventoryListener() {
        this(true);
    }

    /**
     * Constructs a new InventoryListener.
     *
     * @param start True to start the listening thread upon construction, false otherwise.
     */
    public InventoryListener(boolean start) {
        super(start);
        this.listeners = new HashSet<>();
        this.last_inventory = getInventoryMap();
    }

    /**
     * Gets the listeners that have been added to the inventory listener.
     *
     * @return The listeners.
     */
    public final HashSet<InventoryListening07> getListeners() {
        return this.listeners;
    }

    /**
     * Adds a listener to the inventory listener.
     *
     * @param listener The listener to add.
     * @return True if the inventory listener did not already contain the specified listener, false otherwise.
     */
    public final boolean addListener(InventoryListening07 listener) {
        return getListeners().add(listener);
    }

    /**
     * Removes a listener from the inventory listener.
     *
     * @param listener The listener to remove.
     * @return True if the inventory listener contained the specified listener, false otherwise
     */
    public final boolean removeListener(InventoryListening07 listener) {
        return getListeners().remove(listener);
    }

    @Override
    public void listen() {
        //The current inventory
        HashMap<Integer, Integer> current = getInventoryMap();

        //Only check for changes if the game is loaded.
        if (Game07.isGameLoaded() && Login07.isLoggedIn() && last_inventory != null) {
            //Check for any items added to the inventory between the last loop and right now.
            for (int id : current.keySet()) {
                int current_count = current.get(id);
                RSItemDefinition definition = RSItemDefinition.get(id);
                //Skip any items that do not have a valid definition.
                if (definition != null) {
                    if (last_inventory.containsKey(id)) {
                        int last_count = last_inventory.get(id);
                        if (current_count > last_count) {
                            notifyAddition(id, definition, current_count - last_count);
                        }
                    } else {
                        notifyAddition(id, definition, current_count);
                    }
                }
            }

            //Check for any items removed from the inventory between the last loop and right now.
            for (int id : last_inventory.keySet()) {
                int last_count = last_inventory.get(id);
                RSItemDefinition definition = RSItemDefinition.get(id);
                //Skip any items that do not have a valid definition.
                if (definition != null) {
                    if (!current.containsKey(id)) {
                        notifyRemoval(id, definition, last_count);
                    } else {
                        int current_count = current.get(id);
                        if (last_count > current_count) {
                            notifyRemoval(id, definition, last_count - current_count);
                        }
                    }
                }
            }
        }

        //Update the last inventory.
        last_inventory = current;
    }

    /**
     * Notifies all of the listeners of an item addition.
     *
     * @param id         The id of the item.
     * @param definition The definition of the item.
     * @param amount     The amount of the item added to the inventory.
     */
    private void notifyAddition(int id, RSItemDefinition definition, int amount) {
        for (InventoryListening07 listener : listeners) {
            //Check what source the item came from.
            Source source = Source.UNKNOWN;
            if (Bank.isOpen()) {
                source = Source.BANK;
            } else if (Shop.isOpen()) {
                source = Source.SHOP;
            } else if (GrandExchange.getWindowState() != null) {
                source = Source.GE;
            }
            //Notify the listener.
            listener.itemAdded(id, definition, amount, source);
        }
    }

    /**
     * Notifies all of the listeners of an item removal.
     *
     * @param id         The id of the item.
     * @param definition The definition of the item.
     * @param amount     The amount of the item removed from the inventory.
     */
    private void notifyRemoval(int id, RSItemDefinition definition, int amount) {
        for (InventoryListening07 l : listeners) {
            //Check what source the item was removed to.
            Source source = Source.UNKNOWN;
            if (Bank.isOpen()) {
                source = Source.BANK;
            } else if (Shop.isOpen()) {
                source = Source.SHOP;
            } else if (GrandExchange.getWindowState() != null) {
                source = Source.GE;
            }
            //Notify the listener.
            l.itemRemoved(id, definition, amount, source);
        }
    }

    /**
     * Generates a map representing the inventory where the key is an id of the item, and the value is the stack or count of the item.
     *
     * @return A map representing the inventory.
     */
    private HashMap<Integer, Integer> getInventoryMap() {
        final HashMap<Integer, Integer> map = new HashMap<>();
        for (RSItem item : Inventory.getAll()) {
            if (item != null) {
                int id = item.getID();
                int stack = item.getStack();
                if (!map.containsKey(id)) {
                    map.put(id, stack);
                } else {
                    map.put(id, map.get(id) + stack);
                }
            }
        }
        return map;
    }

    /**
     * Used to determine where an item came from or went to.
     */
    public enum Source {

        BANK, SHOP, GE, UNKNOWN;

        @Override
        public String toString() {
            return Strings.capitalizeFirst(name().toLowerCase());
        }
    }
}
