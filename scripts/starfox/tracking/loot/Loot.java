package scripts.starfox.tracking.loot;

import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api2007.grandexchange.GEStatic;

/**
 * @author Nolan
 */
public class Loot {

    private final int id;
    private final String name;
    private final int price;
    private int count;

    /**
     * Constructs a new Loot.
     *
     * @param id The ID of the loot.
     * @throws java.lang.Exception If the item definition could not be loaded.
     */
    public Loot(int id) throws Exception {
        this(id, GEStatic.lookup(id).getPrice());
    }

    /**
     * Constructs a new Loot.
     *
     * @param id    The ID of the loot.
     * @param price The price of the loot.
     * @throws java.lang.Exception If the item definition could not be loaded.
     */
    public Loot(int id, int price) throws Exception {
        this.id = id;
        RSItemDefinition def = RSItemDefinition.get(id);
        if (def == null) {
            throw new Exception("Item definition for: " + id + " was null.");
        }
        this.name = def.getName();
        this.price = price;
        this.count = 0;
    }

    /**
     * Gets the ID of the loot.
     *
     * @return The ID.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the name of the loot.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the price of the loot.
     *
     * @return The price.
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Gets the count of the loot.
     *
     * @return The count of the loot.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Adds the specified count to the loot count.
     *
     * @param count The count to add.
     */
    public void addCount(int count) {
        this.count += count;
    }

    @Override
    public String toString() {
        return getName() + " - " + getPrice() + "gp x " + getCount();
    }
}
