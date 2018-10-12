package scripts.starfox.api2007.zybez;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import org.tribot.api.General;
import org.tribot.util.Util;
import scripts.starfox.api.util.Downloader;
import scripts.starfox.api2007.zybez.json.JsonArray;
import scripts.starfox.api2007.zybez.json.JsonObject;
import scripts.starfox.api2007.zybez.json.JsonValue;

/**
 * @author Nolan
 */
public class Zybez {

    /**
     * User agent to connect with to mask connection from TRiBot.
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    /**
     * The URL at which the JSON string for an item is located.
     */
    private static final String ZYBEZ_API_URL = "http://forums.zybez.net/runescape-2007-prices/api/item/";

    /**
     * The directory at which the zybez ID cache is located.
     */
    private static final String CACHE_DIR = "/Sigma/Zybez Cache";

    /**
     * A cache that will be filled with {@link ZybezItem}s that have been previously looked up during run-time.
     */
    private static final ConcurrentHashMap<Integer, ZybezItem> lookup_cache = new ConcurrentHashMap<>();

    /**
     * The url at which the id cache is located at.
     */
    private static volatile String idCacheUrl;

    static {
        idCacheUrl = "https://raw.githubusercontent.com/9Ox/Zybez-Cache/master/cache.dat";
        downloadCache(true);
    }

    private Zybez() {
    }

    /**
     * Gets the cache file.
     * @return The cache file.
     */
    private static File getCacheFile() {
        return new File(Util.getWorkingDirectory().getAbsolutePath() + CACHE_DIR + "/cache.dat");
    }

    /**
     * Checks to see if the lookup cache contains a zybez item with the specified runescape ID.
     * @param rsId The runescape ID.
     * @return True if the cache contains the item, false otherwise.
     */
    public static boolean cacheContains(int rsId) {
        synchronized (lookup_cache) {
            for (int id : lookup_cache.keySet()) {
                if (id == rsId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the ID cache url.
     * @param url The url to set.
     */
    public static void setIdCacheUrl(String url) {
        idCacheUrl = url;
    }

    /**
     * Clears the specified runescape IDs from the lookup cache.
     * @param rsIds The runescape IDs to clear. Provide nothing to clear the entire cache.
     */
    public static void clearLookupCache(int... rsIds) {
        synchronized (lookup_cache) {
            if (rsIds.length == 0) {
                lookup_cache.clear();
            } else {
                for (int rsId : rsIds) {
                    lookup_cache.remove(rsId);
                }
            }
        }
    }

    /**
     * Looks up the zybez item with the specified runescape item ID.
     * @param rsId The runescape item ID.
     * @return The zybez item found.
     *         A null equivalent of a zybez item is returned if no zybez item could be found.
     */
    public static ZybezItem lookup(int rsId) {
        synchronized (lookup_cache) {
            if (lookup_cache.containsKey(rsId)) {
                return lookup_cache.get(rsId);
            }
        }
        return parseItem(getZybezId(rsId));
    }

    /**
     * Looks up the zybez item with the specified zybez item name.
     * @param zybezItemName The zybez item name.
     * @return The zybez item found.
     *         A null equivalent of a zybez item is returned if no zybez item could be found.
     */
    public static ZybezItem lookup(String zybezItemName) {
        int zybezId = getZybezId(zybezItemName);
        synchronized (lookup_cache) {
            for (int rsId : lookup_cache.keySet()) {
                ZybezItem zybezItem = lookup_cache.get(rsId);
                if (zybezItem.getName().equals(zybezItemName)) {
                    return zybezItem;
                }
            }
        }
        return parseItem(zybezId);
    }

    /**
     * Gets the zybez ID associated with the specified runescape ID.
     * @param rsId The runescape ID.
     * @return The zybez ID.
     */
    public static int getZybezId(int rsId) {
        try {
            downloadCache(false);
            try (BufferedReader br = new BufferedReader(new FileReader(getCacheFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.split(":")[0].equals("" + rsId)) {
                        return Integer.parseInt(line.split(":")[1]);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        synchronized (lookup_cache) {
            lookup_cache.putIfAbsent(rsId, ZybezItem.NIL);
        }

        System.out.println("Failed to get zybez id: " + rsId);
        return -1;
    }

    /**
     * Gets the zybez ID associated with the specified zybez item name.
     * @param itemName The name of the item.
     * @return The zybez ID.
     */
    public static int getZybezId(String itemName) {
        try {
            JsonArray array = JsonArray.readFrom(getJSON("http://forums.zybez.net/runescape-2007-prices/api/" + formatName(itemName)));
            for (JsonValue value : array.values()) {
                if (value.asObject().get("name").asString().equalsIgnoreCase(itemName)) {
                    return value.asObject().get("id").asInt();
                }
            }
        } catch (Exception ex) {
            System.out.println("Failed to get zybez id: " + itemName);
        }
        return -1;
    }

    /**
     * Gets the data from the zybez ID cache.
     * Downloads the cache if it has not yet been downloaded.
     * @return The data.
     */
    public static String[] getCacheData() {
        downloadCache(false);
        ArrayList<String> data = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(getCacheFile()))) {
                String ln;
                while ((ln = br.readLine()) != null) {
                    data.add(ln);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data.toArray(new String[data.size()]);
    }

    /**
     * Downloads the zybez ID cache and replaces the current cache if there is already one present.
     */
    private static void downloadCache(boolean refresh) {
        File cache = getCacheFile();
        if (refresh || !cache.exists()) {
            if (cache != null) {
                cache.delete();
            }
            Downloader.download(idCacheUrl, Util.getWorkingDirectory().getAbsolutePath() + CACHE_DIR, "cache.dat");
        }
    }

    /**
     * Attempts to parse a {@link ZybezItem} from the specified url string.
     * @param zybezId The zybez ID.
     * @return The {@link ZybezItem} parsed.
     */
    public static ZybezItem parseItem(int zybezId) {
        String json;
        try {
            json = getJSON(ZYBEZ_API_URL + zybezId);
        } catch (Exception e) {
            System.out.println("Failed to get json string for: " + zybezId);
            return ZybezItem.NIL;
        }
        try {
            JsonObject object = JsonObject.readFrom(json);

            if (object == null) {
                System.out.println("Failed to parse json object: " + zybezId);
                return ZybezItem.NIL;
            }

            ArrayList<Offer> offers = new ArrayList<>();
            if (object.get("offers") != null) {
                JsonArray offerObjects = object.get("offers").asArray();
                for (JsonValue offer : offerObjects) {
                    JsonObject offerObject = offer.asObject();
                    offers.add(new Offer(
                            offerObject.get("quantity").asInt(),
                            offerObject.get("price").asInt(),
                            offerObject.get("selling").asInt() == 1 ? Offer.Type.SELLING : Offer.Type.BUYING,
                            offerObject.get("rs_name").asString(),
                            offerObject.get("notes").asString(),
                            offerObject.get("contact").asString().equalsIgnoreCase("cc") ? Offer.ContactMethod.CC : Offer.ContactMethod.PM,
                            new Date(offerObject.get("date").asInt() * 1000L)));
                }
            }

            ZybezItem zybezItem = new ZybezItem(
                    zybezId,
                    object.get("rs_id").asInt(),
                    object.get("high_alch").asInt(),
                    object.get("recent_high").asDouble(),
                    object.get("recent_low").asDouble(),
                    object.get("average").asDouble(),
                    object.get("name").asString(),
                    offers.toArray(new Offer[offers.size()]),
                    getImage(object.get("image").asString()));

            synchronized (lookup_cache) {
                lookup_cache.putIfAbsent(zybezItem.getRuneScapeID(), zybezItem);
            }

            return zybezItem;
        } catch (Exception e) {
            System.out.println("Failed to parse item: " + zybezId);
            return ZybezItem.NIL;
        }
    }

    /**
     * Formats the specified item name to work with the zybez price API.
     * @param itemName The name of the item.
     * @return The formatted name.
     */
    private static String formatName(String itemName) {
        return itemName.replaceAll(" ", "+").replaceAll("'", "_");
    }

    /**
     * Converts the string to be URL compatible.
     * @param s The string to convert.
     * @return The converted string.
     */
    private static String convertURLCompatible(String s) {
        return s.replaceAll(" ", "%20");
    }

    /**
     * Posts a zybez offer.
     * @param curseUser     The curse account username.
     * @param cursePassword The curse account password.
     * @param zybezItemName The zybez item name.
     * @param buying        Provide true for buying offer, false for selling offer.
     * @param quantity      The quantity of the offer.
     * @param price         The price per item of the offer.
     * @param rsUsername    The runescape username.
     * @param notes         The notes.
     * @param contact       The contact method.
     * @return True if the post was successful, false otherwise.
     */
    public static boolean postOffer(String curseUser, String cursePassword, String zybezItemName, boolean buying, int quantity, int price, String rsUsername,
            String notes, int contact) {
        try {
            final Map<String, String> data = new HashMap<>();
            new Curse(curseUser, cursePassword).login();
            synchronized (data) {
                data.put("auth", getAuth(zybezItemName));
                data.put("id", "" + getZybezId(zybezItemName));
                int buyInt = (buying ? 0 : 1);
                data.put("type", "" + buyInt);
                data.put("qty", "" + quantity);
                data.put("price", "" + price);
                data.put("character_id", getCharacterID(zybezItemName, rsUsername));
                data.put("contact", "" + contact);
                data.put("notes", notes);
                doSubmit(zybezItemName, "http://forums.zybez.net/index.php?app=priceguide&module=public&section=action&do=trade-add", data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Removes an offer from zybez.
     * @param curseUser     The curse account username.
     * @param cursePassword The curse account password.
     * @param zybezName     The zybez item name.
     * @param rsName        The runescape username.
     * @param isCompleted   Provide true to mark the offer as completed, false to remove the offer completely.
     * @return True if the offer was removed successfully, false otherwise.
     */
    public static boolean removeOffer(String curseUser, String cursePassword, String zybezName, String rsName, boolean isCompleted) {
        String action = isCompleted ? "complete" : "delete";
        try {
            final Map<String, String> data = new HashMap<>();
            new Curse(curseUser, cursePassword).login();
            synchronized (data) {
                data.put("id", "" + getZybezId(zybezName));
                data.put("tid", getTID(zybezName));
                data.put("auth", getAuth(zybezName));
                doSubmit(zybezName, "http://forums.zybez.net/index.php?app=priceguide&module=public&section=action&do=trade-" + action, data);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Gets the character ID for the specified runescape username.
     * @param itemName The zybez item name.
     * @param rsName   The runescape username.
     * @return The character ID.
     * @throws Exception If the character ID could not be found.
     */
    private static String getCharacterID(String itemName, String rsName) throws Exception {
        String siteUrlString = "http://forums.zybez.net/runescape-2007-prices/" + getZybezId(itemName) + "-" + convertURLCompatible(itemName).replaceAll("\\s", "-");
        URL siteUrl = new URL(siteUrlString);
        HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
        conn.addRequestProperty("User-Agent", Curse.USER_AGENT);
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.flush();
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            final String searchLine = "name=\"character_id\" value=\"";
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains(searchLine.toLowerCase())) {
                    String value = line.substring(line.indexOf(searchLine) + searchLine.length(), line.indexOf("\" />"));
                    General.println("Value: " + value);
                    return value;
                }
                if (line.toLowerCase().contains(rsName.toLowerCase())) {
                    String temp = line;
                    String searchString = "value=\"";
                    int idx = temp.indexOf(searchString);
                    if (idx >= 0) {
                        idx += searchString.length(); // The length of "value="" string;
                        return temp.substring(idx, temp.indexOf("\">"));
                    }
                }

            }
        }
        return "";
    }

    /**
     * Gets the TID for the specified item name.
     * @param itemName The zybez item name.
     * @return The TID.
     * @throws Exception If the TID could not be found.
     */
    private static String getTID(String itemName) throws Exception {
        String siteUrlString = "http://forums.zybez.net/runescape-2007-prices/" + getZybezId(itemName) + "-" + convertURLCompatible(itemName).replaceAll("\\s", "-");
        URL siteUrl = new URL(siteUrlString);
        HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
        conn.addRequestProperty("User-Agent", Curse.USER_AGENT);
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.flush();
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains("tid=")) {
                    String temp = line;
                    int idx = temp.indexOf("tid=");
                    if (idx >= 0) {
                        idx += 4; // The length of "tid=" string;
                        return temp.substring(idx, temp.indexOf("&amp", idx));
                    }

                }

            }
        }
        return "";
    }

    /**
     * Submits the specified map of data to the specified URL.
     * @param zybezName The zybez item name.
     * @param url       The URL.
     * @param data      The data map.
     * @throws Exception If the submission failed.
     */
    private static void doSubmit(String zybezName, String url, final Map<String, String> data) throws Exception {
        URL siteUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
        conn.addRequestProperty("User-Agent", Curse.USER_AGENT);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            Set keys = data.keySet();
            Iterator keyIter = keys.iterator();
            String content = "";
            for (int i = 0; keyIter.hasNext(); i++) {
                Object key = keyIter.next();
                if (i != 0) {
                    content += "&";
                }
                content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
            }
            out.writeBytes(content);
            out.flush();
        } catch (Exception e) {
            General.println("Exception thrown: " + e.getMessage());
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("Your trade has been added to the system.")) {
                } else if (line.contains("You have already posted")) {
                    in.close();
                    General.println("You have already posted a Zybez message for " + zybezName + "s.");
                    throw new Exception();
                } else if (line.contains("Sorry, that RuneScape Name is reserved")) {
                    in.close();
                    General.println("\"Runescape name is reserved\" error.");
                    throw new Exception();
                }
            }
        }
    }

    /**
     * Gets the authorization key for the specified item name.
     * @param itemName The zybez item name.
     * @return The authorization key.
     * @throws Exception If the authorization key could not be found.
     */
    private static String getAuth(String itemName) throws Exception {
        String siteUrlString = "http://forums.zybez.net/runescape-2007-prices/" + getZybezId(itemName) + "-" + convertURLCompatible(itemName).replaceAll("\\s", "-");
        URL siteUrl = new URL(siteUrlString);
        HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
        conn.addRequestProperty("User-Agent", Curse.USER_AGENT);
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.flush();
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            final String searchLine = "name=\"auth\" value=\"";
            while ((line = in.readLine()) != null) {
                if (line.contains(searchLine)) {
                    String valueAuth = line.substring(line.indexOf(searchLine) + searchLine.length(), line.indexOf("\" />"));
                    return valueAuth;
                }
            }
        }
        return "";
    }

    /**
     * Gets the image from the specified url string as a BufferedImage.
     * @param url The url string.
     * @return The BufferedImage retrieved from the url string. Null if no image was found.
     */
    private static BufferedImage getImage(String url) {
        try {
            url = url.replaceAll(" ", "%20");
            URLConnection cn = new URL(url).openConnection();
            cn.setRequestProperty("User-Agent", USER_AGENT);
            BufferedInputStream in = new BufferedInputStream(cn.getInputStream());
            return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the JSON string for the specified url string.
     * @param url The url string.
     * @return The JSON string created.
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
            System.out.println(e.getMessage());
            return null;
        }
    }
}
