package scripts.starfox.osbuddy;

import java.util.LinkedHashMap;

/**
 * Created by Nolan on 10/2/2015.
 */
public class OSBuddyCache {

    private static final Object lock = new Object();

    private static final LinkedHashMap<Integer, OSBuddyItem> cache = new LinkedHashMap<>();

    /**
     * Gets the cache of OSBuddyItem's that have been looked up.
     *
     * @return The cache.
     */
    public static LinkedHashMap<Integer, OSBuddyItem> getCache() {
        return cache;
    }

    /**
     * Gets the OSBuddyItem that matches the specified ID from the cache.
     * If the item is not already in the cache, it will be loaded and then put in the cache.
     *
     * @param id     The ID of the item to get.
     * @param reload True to force reload the item, false to get from the cache.
     * @return The item.
     */
    public static OSBuddyItem getItem(int id, boolean reload) {
        synchronized (lock) {
            if (getCache().containsKey(id)) {
                if (reload) {
                    return getCache().replace(id, OSBuddy.build(id));
                }
                return getCache().get(id);
            }
            return getCache().put(id, OSBuddy.build(id));
        }
    }
}
