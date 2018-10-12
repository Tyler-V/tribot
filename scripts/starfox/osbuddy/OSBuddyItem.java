package scripts.starfox.osbuddy;

/**
 * Created by Nolan on 10/2/2015.
 */
public class OSBuddyItem {

    private final int id;
    private final int avgPrice;
    private final int buyPrice;
    private final int buyQuantity;
    private final int sellPrice;
    private final int sellQuantity;

    /**
     * Constructs a new OSBuddyItem.
     *
     * @param id           The ID of the item.
     * @param avgPrice     The average price of the item.
     * @param buyPrice     The average buying price of the item.
     * @param buyQuantity  The average buying quantity of the item.
     * @param sellPrice    The average selling price of the item.
     * @param sellQuantity The average selling quantity of the item.
     */
    public OSBuddyItem(int id, int avgPrice, int buyPrice, int buyQuantity, int sellPrice, int sellQuantity) {
        this.id = id;
        this.avgPrice = avgPrice;
        this.buyPrice = buyPrice;
        this.buyQuantity = buyQuantity;
        this.sellPrice = sellPrice;
        this.sellQuantity = sellQuantity;
    }

    /**
     * Gets the ID of the item.
     * This ID matches that of the item ID in Old-school RuneScape.
     *
     * @return The ID of the item.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the average price of the item.
     *
     * @return The average price of the item.
     */
    public int getAveragePrice() {
        return this.avgPrice;
    }

    /**
     * Gets the average buying price of the item.
     *
     * @return The average buying price of the item.
     */
    public int getBuyPrice() {
        return this.buyPrice;
    }

    /**
     * Gets the average buying quantity of the item.
     *
     * @return The average buying quantity of the item.
     */
    public int getBuyQuantity() {
        return this.buyQuantity;
    }

    /**
     * Gets the average selling price of the item.
     *
     * @return The average selling price of the item.
     */
    public int getSellPrice() {
        return this.sellPrice;
    }

    /**
     * Gets the average selling quantity of the item.
     *
     * @return The average selling quantity of the item.
     */
    public int getSellQuantity() {
        return this.sellQuantity;
    }

    @Override
    public String toString() {
        return getId() + " [averageprice=" + getAveragePrice() + ", buyprice=" + getBuyPrice() + ", buyquantity="
                + getBuyQuantity() + ", sellprice=" + getSellPrice() + ", sellquantity=" + getSellQuantity() + "]";
    }
}
