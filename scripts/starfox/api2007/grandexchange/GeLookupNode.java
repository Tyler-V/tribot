package scripts.starfox.api2007.grandexchange;

/**
 *
 * @author Spencer
 */
public class GeLookupNode {

    private final long time;
    private final int price;
    private final int count;
    private final int buyPrice;
    private final int sellPrice;
    private final int buyCount;
    private final int sellCount;

    /**
     * An object representing a node on the trade history graph.
     *
     * @param time      The time in which the node was recorded, in milliseconds.
     * @param price     The average between the buying and selling price.
     * @param count     The average between the buying and selling counts trade count.
     * @param buyPrice  The average buy price of the item.
     * @param sellPrice The average sell price of the item.
     * @param buyCount  The average buy volume of the item.
     * @param sellCount The average sell volume of the item.
     */
    public GeLookupNode(long time, int price, int count, int buyPrice, int sellPrice, int buyCount, int sellCount) {
        this.time = time;
        this.price = price;
        this.count = count;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyCount = buyCount;
        this.sellCount = sellCount;
    }

    /**
     * Returns the time in which the node was recorded, in milliseconds.
     *
     * @return The time in which the node was recorded, in milliseconds.
     */
    public long getTime() {
        return time;
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
     * Returns the average between the buying and selling counts trade count.
     *
     * @return The average between the buying and selling counts trade count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns The average buy price of the item.
     *
     * @return The average buy price of the item.
     */
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Returns The average sell price of the item.
     *
     * @return The average sell price of the item.
     */
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * Returns the lesser of the two prices: buy or sell.
     *
     * @return The lesser of the two prices: buy or sell.
     */
    public int getMinPrice() {
        return buyPrice < sellPrice ? buyPrice : sellPrice;
    }

    /**
     * Returns the larger of the two prices: buy or sell.
     *
     * @return The larger of the two prices: buy or sell.
     */
    public int getMaxPrice() {
        return sellPrice > buyPrice ? sellPrice : buyPrice;
    }

    /**
     * Returns the price of the item based on the specified GeLookupType.
     *
     * @param type The lookup type.
     * @return The price of the item based on the specified GeLookupType.
     */
    public int getPrice(GeLookupPriceType type) {
        if (type == GeLookupPriceType.MIN) {
            return getMinPrice();
        } else if (type == GeLookupPriceType.MAX) {
            return getMaxPrice();
        } else if (type == GeLookupPriceType.BUY) {
            return getBuyPrice();
        } else if (type == GeLookupPriceType.SELL) {
            return getSellPrice();
        } else {
            return getPrice();
        }
    }

    /**
     * Returns The average buy volume of the item.
     *
     * @return The average buy volume of the item.
     */
    public int getBuyCount() {
        return buyCount;
    }

    /**
     * Returns The average sell volume of the item.
     *
     * @return The average sell volume of the item.
     */
    public int getSellCount() {
        return sellCount;
    }

    @Override
    public String toString() {
        return "GeLookupNode{" + "time=" + time + ", price=" + price + ", count=" + count + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + ", buyCount=" + buyCount
                + ", sellCount=" + sellCount + '}';
    }
}
