package scripts.starfox.api2007.grandexchange;

import scripts.starfox.api.Client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Spencer
 */
public class GeLookupItemContainer {

    private final ReentrantLock LOCK;
    private final ConcurrentHashMap<Integer, GeLookupItem> lookups;
    private final boolean autoUpdate;

    public GeLookupItemContainer(boolean autoUpdate) {
        this.LOCK = new ReentrantLock();
        this.autoUpdate = autoUpdate;
        this.lookups = new ConcurrentHashMap<>();
    }

    protected GeLookupItem getItem(int id, long startTime, int gap, boolean forceUpdate) {
        final GeLookupItem item = lookups.get(id);
        if (forceUpdate) {
            LOCK.lock();
        } else {
            if (!LOCK.tryLock()) {
                return item != null ? item : GeLookup.NIL;
            }
        }
        GeLookupItem returnItem;
        if ((item == null && (autoUpdate || forceUpdate)) || ((autoUpdate || forceUpdate) && lookups.get(id).shouldSync())) {
            returnItem = GeLookup.staticLookup(id, startTime, gap);
            if (item != null) {
                System.out.println("Reloading " + returnItem.getName());
                lookups.replace(id, returnItem);
            } else {
                if (lookups.get(id) == null) {
                    System.out.println("Loading " + returnItem.getName() + " for first time");
                }
                lookups.put(id, returnItem);
            }
        } else {
            returnItem = item == null ? GeLookup.NIL : item;
        }
        LOCK.unlock();
        return returnItem;
    }

    protected void testPrint() {
        for (GeLookupItem lookup : lookups.values()) {
            Client.println("Lookup: " + lookup);
        }
    }
}
