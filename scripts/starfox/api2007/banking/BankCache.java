package scripts.starfox.api2007.banking;

import java.util.ArrayList;
import java.util.Arrays;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

/**
 *
 * @author Spencer
 */
public class BankCache {

    private final Object lock;
    private final ArrayList<RSItem> cache;

    protected BankCache() {
        lock = new Object();
        cache = new ArrayList<>();
    }

    /**
     * Gets the bank cache. The bank is cached every time a bank method is called when the bank is open.
     *
     * @return The bank cache.
     */
    public final ArrayList<RSItem> getCache() {
        synchronized (lock) {
            return cache;
        }
    }

    /**
     * Resets the bank cache using the specified array of items.
     *
     * @param items The items.
     */
    public final void reset(RSItem[] items) {
        synchronized (lock) {
            cache.clear();
            cache.addAll(Arrays.asList(items));
        }
    }

    /**
     * Checks to see if the cache contains any item(s) with the specified id.
     *
     * @param id The id.
     * @return True if the cache contains the id, false otherwise.
     */
    public final boolean contains(int id) {
        synchronized (lock) {
            for (RSItem item : cache) {
                if (item != null && item.getID() == id) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks to see if the cache contains any item(s) with the specified name.
     *
     * @param name The name.
     * @return True if the cache contains the name, false otherwise.
     */
    public final boolean contains(String name) {
        synchronized (lock) {
            for (RSItem item : cache) {
                if (item != null) {
                    RSItemDefinition def = item.getDefinition();
                    if (def != null) {
                        String def_name = def.getName();
                        if (def_name != null && def_name.equalsIgnoreCase(name)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /**
     * Checks to see if the bank cache is empty.
     *
     * @return True if it is empty, false otherwise.
     */
    public final boolean isEmpty() {
        synchronized (lock) {
            return cache != null && cache.isEmpty();
        }
    }
}
