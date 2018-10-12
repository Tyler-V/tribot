package scripts.starfox.tracking.loot;

import org.tribot.api.util.Screenshots;
import scripts.starfox.api.listeners.InventoryListener;
import scripts.starfox.api2007.grandexchange.GEStatic;
import scripts.starfox.interfaces.listening.InventoryListening07;

import java.util.ArrayList;

/**
 * @author Nolan
 */
public abstract class LootTracker
        implements InventoryListening07 {

    /**
     * The item ID of coins.
     */
    public static final int COINS_ID = 995;

    private final InventoryListener inventoryListener;
    private final ArrayList<Loot> lootList;
    protected final Object lock;

    /**
     * Constructs a new LootTracker.
     */
    public LootTracker() {
        this.inventoryListener = new InventoryListener();
        this.lootList = new ArrayList<>();
        this.lock = new Object();
        this.inventoryListener.addListener(this);
    }

    /**
     * Gets the inventory listener.
     *
     * @return The inventory listener.
     */
    private InventoryListener getInventoryListener() {
        return this.inventoryListener;
    }

    /**
     * Gets the loot list.
     *
     * @return The loot list.
     */
    public ArrayList<Loot> getLootList() {
        return this.lootList;
    }

    /**
     * Gets the total amount of loot acquired.
     * This excludes any loot that has an amount of less than 0.
     *
     * @return The total amount.
     */
    public int getLootCount() {
        int total = 0;
        synchronized (lock) {
            for (Loot loot : getLootList()) {
                if (loot.getCount() > 0) {
                    total += loot.getCount();
                }
            }
        }
        return total;
    }

    /**
     * Gets the total amount of loot lost.
     * This excludes any loot that has an amount of greater than 0.
     *
     * @return The total amount.
     */
    public int getLostCount() {
        int total = 0;
        synchronized (lock) {
            for (Loot loot : getLootList()) {
                if (loot.getCount() < 0) {
                    total += loot.getCount();
                }
            }
        }
        return total;
    }

    /**
     * Gets the total amount of loot that matches the item ID specified that has been acquired.
     *
     * @param id The ID to look for.
     * @return The total amount.
     */
    public int getLootCount(int id) {
        synchronized (lock) {
            for (Loot loot : getLootList()) {
                if (loot.getId() == id) {
                    return loot.getCount();
                }
            }
        }
        return 0;
    }

    /**
     * Gets the total amount of profit gained (or lost).
     *
     * @return The total amount of profit.
     */
    public int getProfit() {
        int profit = 0;
        synchronized (lock) {
            for (Loot loot : getLootList()) {
                profit += loot.getPrice() * loot.getCount();
            }
        }
        return profit;
    }

    /**
     * Checks to see if the loot list contains any loot with the specified ID.
     *
     * @param id The ID to search for.
     * @return True if the loot list contains loot with the specified ID, false otherwise.
     */
    public boolean contains(int id) {
        synchronized (lock) {
            for (Loot loot : getLootList()) {
                if (loot.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the Loot object in the loot list that has the specified ID if the loot list contains loot with the specified
     * ID.
     *
     * @param id The ID of the loot object to get.
     * @return The Loot object.
     */
    public Loot getLoot(int id) {
        synchronized (lock) {
            for (Loot loot : getLootList()) {
                if (loot.getId() == id) {
                    return loot;
                }
            }
        }
        return null;
    }

    /**
     * Adds loot to the loot tracker.
     *
     * @param id    The ID of the loot.
     * @param count The count.
     */
    public void addLoot(int id, int count) {
        if (contains(id)) {
            addLoot(id, getLoot(id).getPrice(), count);
        } else {
            addLoot(id, id == COINS_ID ? 1 : GEStatic.lookup(id).getPrice(), count);
        }
    }

    /**
     * Adds loot to the loot tracker.
     *
     * @param id    The ID of the loot.
     * @param price The price of the loot.
     * @param count The count.
     */
    public void addLoot(int id, int price, int count) {
        if (contains(id)) {
            getLoot(id).addCount(count);
            return;
        }
        Loot newLoot;
        try {
            newLoot = new Loot(id, price);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        newLoot.addCount(count);
        synchronized (lock) {
            getLootList().add(newLoot);
            if (getLoot(id).getPrice() > 1000000) {
                Screenshots.take(true);
                System.out.println("Looted an item worth over 1M coins! Saved screenshot to screenshots folder.");
            }
        }
    }

    /**
     * Starts the loot tracker.
     */
    public void start() {
        getInventoryListener().start();
    }

    /**
     * Forces the loot tracker to stop tracking loot and terminate the inventory listening thread.
     *
     * This method does not clear the already tracked loot, so if loot tracking were to resume then the previously tracked loot would still be valid.
     */
    public void stop() {
        getInventoryListener().stop();
    }
}
