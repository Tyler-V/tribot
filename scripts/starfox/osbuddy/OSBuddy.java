package scripts.starfox.osbuddy;

import scripts.starfox.api.util.Website;
import scripts.starfox.api2007.zybez.json.JsonObject;

import java.io.IOException;

/**
 * Created by Nolan on 10/2/2015.
 */
public class OSBuddy {

    public static final String QUERY_URL = "http://api.rsbuddy.com/grandExchange?a=guidePrice&i=";

    /**
     * Builds an OSBuddyItem from the ID specified.
     *
     * @param id The ID of the item to build.
     * @return The item built.
     * Null if the ID specified does not match any items.
     */
    public static OSBuddyItem build(int id) {
        JsonObject jsonObject;
        try {
            jsonObject = JsonObject.readFrom(Website.html(QUERY_URL + "" + id));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int avgPrice = jsonObject.get("overall").asInt();
        int buyPrice = jsonObject.get("buying").asInt();
        int buyQuantity = jsonObject.get("buyingQuantity").asInt();
        int sellPrice = jsonObject.get("selling").asInt();
        int sellQuantity = jsonObject.get("sellingQuantity").asInt();

        OSBuddyItem osBuddyItem = new OSBuddyItem(id, avgPrice, buyPrice, buyQuantity, sellPrice, sellQuantity);
        return osBuddyItem;
    }
}
