package scripts.starfox.api2007.grandexchange;

import java.util.ArrayList;
import org.tribot.api.General;
import scripts.starfox.api.util.Timer;

/**
 * @author Nolan
 */
public class GeLookupItem {

    private final Timer updateTimer;

    private final String name;
    private final int id;
    private final int price;
    private final int buyPrice;
    private final int sellPrice;
    private final int buyCount;
    private final int sellCount;

    private final ArrayList<GeLookupNode> graph;

    public GeLookupItem(String name, int id, int price, int buyPrice, int sellPrice, int buyCount, int sellCount, ArrayList<GeLookupNode> graph) {
        this.updateTimer = new Timer(getRandomTime());
        this.updateTimer.start();

        this.name = name;
        this.id = id;
        this.price = price;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyCount = buyCount;
        this.sellCount = sellCount;

        this.graph = graph;
    }

    /**
     * Returns the name of the item.
     *
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id of the item.
     *
     * @return The id of the item.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the average between the buying and selling price.
     *
     * @return The average between the buying and selling price.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Returns the buy price of the item.
     *
     * @return The buy price of the item.
     */
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Returns the sell price of the item.
     *
     * @return The sell price of the item.
     */
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * Returns the buy volume of the item.
     *
     * @return The buy volume of the item.
     */
    public int getBuyCount() {
        return buyCount;
    }

    /**
     * Returns the sell volume of the item.
     *
     * @return The sell volume of the item.
     */
    public int getSellCount() {
        return sellCount;
    }

    /**
     * Returns the trade history of this GeLookupItem, or null if the history has not been looked up.
     *
     * @return The trade history of this GeLookupItem, or null if the history has not been looked up.
     */
    public ArrayList<GeLookupNode> getHistory() {
        return graph;
    }

    /**
     * Checks to see if this item should be synced again and is out of date.
     *
     * @return True if this item should be synced again and is out of data, false otherwise.
     */
    protected boolean shouldSync() {
        return updateTimer.timedOut();
    }

    /**
     * Returns a new random time for the update timer.
     *
     * @return A new random time for the update timer.
     */
    private long getRandomTime() {
        return General.random(1000 * 60 * 4, 1000 * 60 * 12);
    }

    @Override
    public String toString() {
        return "GeLookupItem{" + "name=" + name + ", id=" + id + ", price=" + price + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + ", buyCount=" + buyCount
                + ", sellCount=" + sellCount + ", graph=" + graph + '}';
    }
}
