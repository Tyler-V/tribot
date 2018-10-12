package scripts.starfox.interfaces.listening;

import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api.listeners.InventoryListener;

public interface InventoryListening07 {

    /**
     * Called when an item is added to the inventory.
     *
     * @param id         The id of the item.
     * @param definition The definition of the item (can never be null).
     * @param amount     The amount added.
     * @param source     The source from which the item came from.
     */
    void itemAdded(int id, RSItemDefinition definition, int amount, InventoryListener.Source source);

    /**
     * Called when an item is removed from the inventory.
     *
     * @param id         The id of the item.
     * @param definition The definition of the item (can never be null).
     * @param amount     The amount removed.
     * @param source     The source that the item was removed to.
     */
    void itemRemoved(int id, RSItemDefinition definition, int amount, InventoryListener.Source source);
}
