package scripts.starfox.api2007.grandexchange;

import java.util.ArrayList;

/**
 *
 * @author Spencer
 */
public class GeLookupProcessor {

    /**
     * Gets the average price of the specified item.
     *
     * @param item       The item.
     * @param lookupType The lookup price type of the price.
     * @return The average price of the specified item.
     */
    public static int getAveragePrice(GeLookupItem item, GeLookupPriceType lookupType) {
        int total = 0;
        int size = 0;
        for (GeLookupNode node : item.getHistory()) {
            total += node.getPrice(lookupType);
            size++;
        }
        return size != 0 ? total / size : 1;
    }

    /**
     * Gets the average volatility of the specified item.
     *
     * Volatility is defined by how far a particular price deviates from the average price.
     *
     * @param item       The item.
     * @param lookupType The lookup price type.
     * @param percent    True if the value should be returned as a percent of the average price, false if the value should be returned as a gp value.
     * @return The average volatility of the specified item.
     */
    public static double getAverageVolatility(GeLookupItem item, GeLookupPriceType lookupType, boolean percent) {
        double average = getAveragePrice(item, lookupType);
        double total = 0;
        int size = 0;
        for (GeLookupNode node : item.getHistory()) {
            total += Math.abs(node.getPrice(lookupType) - average);
            size++;
        }
        return percent ? (average != 0 ? (total / size) / average : 0) : (size != 0 ? (int) (total / size) : 0);
    }

    /**
     * Gets the average amount that the item changes prices between every node.
     *
     * @param item       The item.
     * @param lookupType The lookup price type.
     * @param type       The change type.
     * @param percent    True if the value should be returned as a percent of the average price, false if the value should be returned as a gp value.
     * @return The average amount that the item changes prices between every node.
     */
    public static double getAveragePriceCheckChange(GeLookupItem item, GeLookupPriceType lookupType, GeLookupChangeType type, boolean percent) {
        double totalChange = 0;
        int total = 0;
        ArrayList<GeLookupNode> tempNodes = item.getHistory();
        for (int i = 0; i < tempNodes.size() - 1; i++) {
            GeLookupNode current = tempNodes.get(i);
            GeLookupNode next = tempNodes.get(i + 1);
            int currentPrice = current.getPrice(lookupType);
            int nextPrice = next.getPrice(lookupType);
            double priceChange = percent ? (1d - ((double) currentPrice / (double) nextPrice)) : (nextPrice - currentPrice);
            boolean check1 = type == GeLookupChangeType.NEGATIVE && priceChange < 0;
            boolean check2 = type == GeLookupChangeType.POSITIVE && priceChange > 0;
            boolean check3 = type == GeLookupChangeType.BOTH || type == GeLookupChangeType.BOTH_ABSOLUTE;
            if (check1 || check2 || check3) {
                if (type == GeLookupChangeType.BOTH_ABSOLUTE) {
                    priceChange = Math.abs(priceChange);
                }
                totalChange += priceChange;
                total++;
            }
        }
        if (percent) {
            totalChange = (double) totalChange / (double) total;
        } else {
            totalChange = (int) Math.round((double) totalChange / (double) total);
        }
        return totalChange;
    }

    /**
     * Gets the average margin value of all of the nodes for the specified item.
     *
     * @param item          The item.
     * @param absoluteValue True if the margin should be returned as an absolute value, false otherwise.
     *                      Note that this applies to every node included in the price check. Calling absolute value on a returned result will not guarantee the same value.
     * @param percent       True if the value should be returned as a percent of the average price, false if the value should be returned as a gp value.
     * @return The average margin value of all of the nodes for the specified item.
     */
    public static double getAverageMargin(GeLookupItem item, boolean absoluteValue, boolean percent) {
        double totalMargins = 0;
        int total = 0;
        ArrayList<GeLookupNode> tempNodes = item.getHistory();
        for (int i = 0; i < tempNodes.size(); i++) {
            GeLookupNode current = tempNodes.get(i);
            int buyPrice = current.getBuyPrice();
            int sellPrice = current.getSellPrice();
            double currentMargin = percent ? (1 - ((double) buyPrice / (double) sellPrice)) : (sellPrice - buyPrice);
            if (absoluteValue) {
                currentMargin = Math.abs(currentMargin);
            }
            totalMargins += currentMargin;
            total++;
        }
        if (percent) {
            totalMargins = (double) totalMargins / (double) total;
        } else {
            totalMargins = (int) Math.round((double) totalMargins / (double) total);
        }
        return totalMargins;
    }
}
