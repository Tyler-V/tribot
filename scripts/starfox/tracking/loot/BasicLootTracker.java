package scripts.starfox.tracking.loot;

import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api.listeners.InventoryListener;

/**
 * The BasicLootTracker class provides the most simple functionality for loot tracking. This class can be used for most basic scripts that do not require any
 * additional calculations to be done inside the itemAdded and itemRemoved methods.
 *
 * @author Nolan
 */
public class BasicLootTracker
        extends LootTracker {

    @Override
    public void itemAdded(int id, RSItemDefinition definition, int amount, InventoryListener.Source source) {
        if (source == InventoryListener.Source.UNKNOWN) {
            addLoot(definition.isNoted() ? id - 1 : id, amount);
        }
    }

    @Override
    public void itemRemoved(int id, RSItemDefinition definition, int amount, InventoryListener.Source source) {
        if (source == InventoryListener.Source.UNKNOWN) {
            addLoot(definition.isNoted() ? id - 1 : id, -amount);
        }
    }
}
