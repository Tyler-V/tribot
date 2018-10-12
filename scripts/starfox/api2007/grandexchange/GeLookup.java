package scripts.starfox.api2007.grandexchange;

import scripts.starfox.api.Client;
import scripts.starfox.api2007.entities.Items07;
import scripts.starfox.api2007.zybez.json.JsonArray;
import scripts.starfox.api2007.zybez.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * @author Nolan
 */
public class GeLookup {

    public static final GeLookupItem NIL = new GeLookupItem("nil", -1, -1, -1, -1, -1, -1, new ArrayList<GeLookupNode>());

    public static final long HOURS_24 = 1000 * 60 * 60 * 24;
    public static final long HOURS_12 = 1000 * 60 * 60 * 12;
    public static final long HOURS_6 = 1000 * 60 * 60 * 6;
    public static final long HOURS_3 = 1000 * 60 * 60 * 3;
    public static final long HOURS_2 = 1000 * 60 * 60 * 2;
    public static final long HOURS_1 = 1000 * 60 * 60 * 1;

    private static final String STATS_URL = "https://api.rsbuddy.com/grandExchange?a=guidePrice&i=";
    private static final String NAMES_URL = "https://rsbuddy.com/exchange/names.json";
    private static final String GRAPH_URL = "https://api.rsbuddy.com/grandExchange?a=graph";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    private final GeLookupItemContainer lookupContainer;

    public GeLookup(boolean autoUpdate) {
        this.lookupContainer = new GeLookupItemContainer(autoUpdate);
    }

    public void testPrint() {
        lookupContainer.testPrint();
    }

    /**
     * Connects to all of the web-pages used to look up items.
     * This method can be used to force the tribot firewall message to appear if this is the first time these pages are being requested.
     */
    public static void connect() {
        Client.println("Connecting to OSBuddy exchange");
        getJSON(STATS_URL);
        getJSON(NAMES_URL);
        getJSON(GRAPH_URL);
        Client.println("Successfully connected to OSBuddy exchange");
    }

    /**
     * Looks up a GeLookupItem with the specified ID. The specified ID must be the non-noted ID of the item.
     *
     * Includes graph data.
     *
     * @param id The ID of the item.
     * @return A GeLookupItem built from the specified ID.
     *         GeLookup#NIL is returned if one could not be built.
     */
    public GeLookupItem lookup(int id) {
        return lookup(id, 0, 0, false);
    }

    /**
     * Looks up a GeLookupItem with the specified ID. The specified ID must be the non-noted ID of the item.
     *
     * Includes graph data.
     *
     * @param id          The ID of the item.
     * @param forceUpdate Forces the item to be updated, regardless of the autoupdate status.
     * @return A GeLookupItem built from the specified ID.
     *         GeLookup#NIL is returned if one could not be built.
     */
    public GeLookupItem lookup(int id, boolean forceUpdate) {
        return lookup(id, 0, 0, forceUpdate);
    }

    /**
     * Looks up a GeLookupItem with the specified ID. The specified ID must be the non-noted ID of the item.
     *
     * @param id          The ID of the item.
     * @param startTime   The amount of time the trade history graph should record. 0 is default.
     * @param gap         The gap in between each node in the trade history. 0 is default.
     * @param forceUpdate Forces the item to be updated, regardless of the autoupdate status.
     * @return A GeLookupItem built from the specified ID.
     *         GeLookup#NIL is returned if one could not be built.
     */
    public GeLookupItem lookup(int id, long startTime, int gap, boolean forceUpdate) {
        return lookupContainer.getItem(id, startTime, gap, forceUpdate);
    }

    /**
     * Looks up a GeLookupItem with the specified ID. The specified ID must be the non-noted ID of the item.
     *
     * DOES NOT include graph data.
     *
     * @param id The ID of the item.
     * @return A GeLookupItem built from the specified ID.
     *         Null is returned if one could not be built.
     */
    public static GeLookupItem staticLookup(int id) {
        return build(id, -1, -1);
    }

    /**
     * Looks up a GeLookupItem with the specified ID. The specified ID must be the non-noted ID of the item.
     *
     * @param id        The ID of the item.
     * @param startTime The amount of time the trade history graph should record. 0 is default, -1 is no graph.
     * @param gap       The gap in between each node in the trade history. 0 is default, -1 is no graph.
     * @return A GeLookupItem built from the specified ID.
     *         Null is returned if one could not be built.
     */
    public static GeLookupItem staticLookup(int id, long startTime, int gap) {
        return build(id, startTime, gap);
    }

    /**
     * Looks up a GeLookupItem with the specified ID. The specified ID must be the non-noted ID of the item.
     *
     * @param id           The ID of the item.
     * @param getGraphData True if graph data should be retrieved for the specified item, false otherwise.
     * @return A GeLookupItem built from the specified ID.
     *         Null is returned if one could not be built.
     */
    public static GeLookupItem staticLookup(int id, boolean getGraphData) {
        return build(id, getGraphData ? 0 : -1, getGraphData ? 0 : -1);
    }

    /**
     * Builds a GeLookupItem from the specified ID.
     *
     * @param id        The ID of the item.
     * @param startTime The amount of time the trade history graph should record. 0 is default, -1 is no graph.
     * @param gap       The gap in between each node in the trade history. 0 is default, -1 is no graph.
     * @return A GeLookupItem built from the specified ID.
     *         Null is returned if one could not be built.
     */
    private static GeLookupItem build(int id, long startTime, int gap) {
        String json;
        try {
            //Client.println("Getting json for item " + id);
            json = getJSON(STATS_URL + id);
        } catch (Exception e) {
            System.out.println("Failed to get json string for: " + id);
            return NIL;
        }
        try {
            JsonObject object = JsonObject.readFrom(json);
            if (object == null) {
                System.out.println("Failed to parse json object: " + id);
                return NIL;
            }
            String name = Client.isLoaded() ? Items07.getName(id) : getName(id);
            int price = object.get("overall").asInt();
            int buyPrice = object.get("buying").asInt();
            int sellPrice = object.get("selling").asInt();
            int buyCount = object.get("buyingQuantity").asInt();
            int sellCount = object.get("sellingQuantity").asInt();
            ArrayList<GeLookupNode> nodes;
            int tries = 0;
            while ((nodes = getGraph(id, startTime, gap)) == null && tries < 3) {
                Client.sleep(50, 250);
                tries++;
            }
            if (nodes == null) {
                nodes = new ArrayList<>();
            }
            return new GeLookupItem(name, id, price, buyPrice, sellPrice, buyCount, sellCount, nodes);
        } catch (Exception e) {
            System.out.println("Failed to parse item: " + id);
            e.printStackTrace();
            return NIL;
        }
    }

    /**
     * Returns the name of an item based on the specified ID.
     *
     * @param id The ID of the item.
     * @return The name of the item based on the specified ID.
     */
    private static String getName(int id) {
        String json;
        try {
            json = getJSON(NAMES_URL);
        } catch (Exception e) {
            System.out.println("Failed to get json name string for: " + id);
            return null;
        }
        try {
            JsonObject object = JsonObject.readFrom(json);
            JsonObject object2 = object.get("" + id).asObject();
            return object2.get("name").asString();
        } catch (Exception e) {
            System.out.println("Failed to parse item name: " + id);
            return null;
        }
    }

    /**
     * Returns the graph of trade data for the item based on the specified ID.
     *
     * @param id        The ID of the item.
     * @param startTime The amount of time the trade history graph should record. 0 is default, -1 is no graph.
     * @param gap       The gap in between each node in the trade history. 0 is default, -1 is no graph.
     * @return The graph of trade data for the item based on the specified ID. Null is no graph is to be loaded or if there was an error when loading the graph.
     */
    private static ArrayList<GeLookupNode> getGraph(int id, long startTime, int gap) {
        if (startTime != -1 && gap != -1) {
            String json;
            try {
                gap = gap == 0 ? 180 : gap;
                String startTimeString = startTime == 0 ? "" : "&start=" + (System.currentTimeMillis() - startTime);
                json = getJSON(GRAPH_URL + "&g=" + gap + startTimeString + "&i=" + id);
            } catch (Exception e) {
                System.out.println("Failed to get json name string for: " + id);
                return null;
            }
            try {
                JsonArray array = JsonArray.readFrom(json); //gnf
                ArrayList<GeLookupNode> nodes = new ArrayList<>();
                if (array.size() > 2) {
                    for (int i = 1; i < array.size() - 1; i++) {
                        try {
                            JsonObject element = array.get(i).asObject();
                            long time = element.get("ts").asLong();
                            int price = element.get("overallPrice").asInt();
                            int buyPrice = element.get("buyingPrice").asInt();
                            int sellPrice = element.get("sellingPrice").asInt();
                            int count = element.get("overallCompleted").asInt();
                            int buyCount = element.get("buyingCompleted").asInt();
                            int sellCount = element.get("sellingCompleted").asInt();
                            nodes.add(new GeLookupNode(time, price, count, buyPrice, sellPrice, buyCount, sellCount));
                        } catch (Exception e) {
                        }
                    }
                }
                return nodes;
            } catch (Exception e) {
                System.out.println("Failed to parse item graph: " + id);
                String startTimeString = startTime == 0 ? "" : "&start=" + (System.currentTimeMillis() - startTime);
                System.out.println(GRAPH_URL + "&g=" + gap + startTimeString + "&i=" + id);
//                e.printStackTrace();
                return null;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Returns the json string from the specified URL.
     *
     * @param url The URL.
     * @return The json string from the specified URL.
     */
    private static String getJSON(String url) {
        try {
            URLConnection cn = new URL(url).openConnection();
            cn.setRequestProperty("User-Agent", USER_AGENT);
            StringBuilder stringBuilder;
            try (InputStreamReader in = new InputStreamReader(cn.getInputStream()); BufferedReader br = new BufferedReader(in)) {
                stringBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            //System.out.println(e.getMessage());
            return null;
        }
    }
}
