package scripts.starfox.api2007.zybez;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a wrapper class that holds data correlating to the
 * <a href="http://forums.zybez.net/runescape-2007-prices">Official Zybez Old-school Price Guide</a>.
 *
 * @author Nolan
 * @version 1.0
 */
public final class ZybezItem {

    /**
     * A null equivalent of a {@link ZybezItem}.
     */
    public static final ZybezItem NIL = new ZybezItem(0, 0, 0, 0, 0, 0, "NIL", new Offer[0], null);

    private final int zybezId,
            rsId,
            highAlchmeyValue;
    private final double averageSellPrice,
            averageBuyPrice,
            averagePrice;
    private final String name;
    private final Offer[] offers;
    private final BufferedImage image;

    /**
     * Constructs a new ZybezItem.
     *
     * @param zybezId          The zybez ID of the item.
     * @param rsId
     * @param name             The name of the item.
     * @param averageSellPrice The recent high price.
     * @param averageBuyPrice  The recent low price.
     * @param averagePrice     The averagePrice price.
     * @param highAlchmeyValue The high alchemy value.
     * @param offers           The offers for the item.
     * @param image            The image of the item.
     */
    public ZybezItem(int zybezId, int rsId, int highAlchmeyValue, double averageSellPrice, double averageBuyPrice, double averagePrice, String name, Offer[] offers,
            BufferedImage image) {
        this.zybezId = zybezId;
        this.rsId = rsId;
        this.highAlchmeyValue = highAlchmeyValue;
        this.averageSellPrice = averageSellPrice;
        this.averageBuyPrice = averageBuyPrice;
        this.averagePrice = averagePrice;
        this.name = name;
        this.offers = offers;
        this.image = image;
    }

    /**
     * Gets the zybez ID of the zybez item.
     *
     * @return The zybez ID.
     */
    public int getZybezID() {
        return zybezId;
    }

    /**
     * Gets the runescape ID of the zybez item.
     *
     * @return The runescape ID.
     */
    public int getRuneScapeID() {
        return this.rsId;
    }

    /**
     * Gets the high alchemy value.
     *
     * @return The high alchemy value.
     */
    public int getHighAlchmeyValue() {
        return highAlchmeyValue;
    }

    /**
     * Gets the average sell price of the zybez item.
     *
     * @return The average sell price.
     */
    public double getAverageSellPrice() {
        return averageSellPrice;
    }

    /**
     * Gets the average buy price of the zybez item.
     *
     * @return The average buy price.
     */
    public double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    /**
     * Gets the average price of the zybez item.
     *
     * @return The average price.
     */
    public double getAverage() {
        return averagePrice;
    }

    /**
     * Gets the name of the zybez item.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the offers for the zybez item.
     *
     * @return An array of offers for the zybez item.
     */
    public Offer[] getOffers() {
        return offers;
    }

    /**
     * Gets the image of the zybez item.
     *
     * @return The image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Gets all the offers that were posted after the specified date.
     *
     * @param date The date.
     * @return The offers.
     */
    public Offer[] getOffersAfter(Date date) {
        List<Offer> goodOffers = new ArrayList<>();
        for (Offer offer : getOffers()) {
            if (offer.getDate().compareTo(date) > 0) {
                goodOffers.add(offer);
            }
        }
        return goodOffers.toArray(new Offer[goodOffers.size()]);
    }

    @Override
    public String toString() {
        return getName() + ": [Zybez id=" + getZybezID() + "], [Rs id=" + getRuneScapeID() + "], [Avg. Price=" + getAverage() 
                + "], [Avg. Sell Price=" + getAverageSellPrice() + "], [Avg. Buy Price=" + getAverageBuyPrice() + "]";
    }
}
