package scripts.starfox.api2007.entities;

import obf.ZE;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItemDefinition;

/**
 * The GroundItems07 class is a utility class that is able to get ground items without array checking.
 *
 * Note that null checking is still required.
 *
 *
 * @author Nolan
 */
public final class GroundItems07 {

    /**
     * Gets all of the ground items that are loaded.
     *
     * @return All of the ground items that are loaded.
     */
    public static RSGroundItem[] getAll() {
        return GroundItems.getAll();
    }

    /**
     * Gets the nearest ground item with the specified name.
     *
     *
     * @param name The name of the item.
     * @return The nearest ground item.
     * Null if no ground items were found.
     */
    public static RSGroundItem getGroundItem(String name) {
        RSGroundItem[] items = GroundItems.find(name);
        Sorting.sortByDistance(items, Player.getPosition(), true);
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Gets the nearest ground item with the specified ID.
     *
     *
     * @param id The ID of the ground item.
     * @return The nearest ground item.
     * Null if no ground items were found.
     */
    public static RSGroundItem getGroundItem(int id) {
        RSGroundItem[] items = GroundItems.find(id);
        Sorting.sortByDistance(items, Player.getPosition(), true);
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Gets the nearest ground item that is accepted by the specified filter.
     *
     *
     * @param filter The filter.
     * @return The nearest ground item.
     * Null if no ground items were found.
     */
    public static RSGroundItem getGroundItem(Filter<RSGroundItem> filter) {
        RSGroundItem[] items = GroundItems.find(filter);
        Sorting.sortByDistance(items, Player.getPosition(), true);
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Gets the first ground item that is on the specified tile.
     *
     *
     * @param positionable The position.
     * @return The first ground item.
     * Null if no ground items were found.
     */
    public static RSGroundItem getGroundItemAt(Positionable positionable) {
        if (positionable == null) {
            return null;
        }
        RSGroundItem[] items = GroundItems.getAt(positionable);
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Gets the ID of the specified ground item.
     *
     * @param groundItem The ground item to get the ID of.
     * @return The ID of the ground item.
     * -1 if the ground item was null or had no ID.
     */
    public static int getId(RSGroundItem groundItem) {
        if (groundItem != null) {
            return groundItem.getID();
        }
        return -1;
    }

    /**
     * Gets the RSItemDefinition of the specified ground item.
     *
     * @param groundItem The ground item to get the definition of.
     * @return The definition of the ground item.
     * Null if no definition was found or the item was null.
     */
    public static RSItemDefinition getDefinition(RSGroundItem groundItem) {
        if (groundItem != null) {
            return groundItem.getDefinition();
        }
        return null;
    }

    /**
     * Gets the name of the specified ground item.
     *
     * @param groundItem The ground item to get the name of.
     * @return The name of the ground item.
     * Returns an empty string if no name was found.
     */
    public static String getName(RSGroundItem groundItem) {
        RSItemDefinition definition = getDefinition(groundItem);
        if (definition != null) {
            String name = definition.getName();
            if (name != null) {
                return name;
            }
        }
        return "";
    }
}
