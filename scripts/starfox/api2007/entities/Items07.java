package scripts.starfox.api2007.entities;

import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.util.Util;
import scripts.starfox.api.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Items07 class is a utility class that provides methods related to RSItem's.
 *
 * @author Nolan
 */
public class Items07 {

    private static final String FILE_URL = Util.getWorkingDirectory().getAbsolutePath() + "/Sigma/item_dump.txt";
    private static final File DUMP_FILE = new File(FILE_URL);

    /**
     * Gets all of the item definitions that exist.
     *
     * @return An array of all of the item definitions.
     */
    public static RSItemDefinition[] getAllDefinitions() {
        List<RSItemDefinition> definitions = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            RSItemDefinition def = RSItemDefinition.get(i);
            if (def != null) {
                definitions.add(def);
            }
        }
        return definitions.toArray(new RSItemDefinition[definitions.size()]);
    }

    public static String[] getActions(RSItem item) {
        if (item != null) {
            RSItemDefinition def = item.getDefinition();
            if (def != null) {
                String[] actions = def.getActions();
                if (actions != null) {
                    return actions;
                }
            }
        }
        return new String[0];
    }

    public static String getName(int id) {
        RSItemDefinition def = RSItemDefinition.get(id);
        if (def != null) {
            return def.getName();
        }
        return null;
    }

    /**
     * Generates a new empty item array with a length of 0.
     *
     * @return A new empty item array.
     */
    public static RSItem[] empty() {
        return new RSItem[0];
    }

    /**
     * Gets the item dump file.
     *
     * @return The item dump file.
     */
    public static File getDumpFile() {
        return FileUtil.getFile(true, "item_dump", "txt");
    }

    /**
     * Checks to see if the items have been dumped yet.
     *
     * @return True if the items have been dumped, false otherwise.
     */
    public static boolean hasDumped() {
        return getDumpFile() != null;
    }

    private static final String dump_url = "http://www.mediafire.com/download/j6s88az8rj2750k/item_dump.txt";

    /**
     * Dumps items into a text file in the Sigma directory of TRiBot.
     */
    public static void dump() {
        System.out.println("Starting item dump...");
        long time_stamp = System.currentTimeMillis();
        if (!DUMP_FILE.exists()) {
            try {
                if (!DUMP_FILE.createNewFile()) {
                    System.out.println("Failed to create a new item dump file.");
                    return;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            try (PrintWriter pw = new PrintWriter(DUMP_FILE)) {
                for (int i = 0; i < 30000; i++) {
                    RSItemDefinition definition = RSItemDefinition.get(i);
                    if (definition != null) {
                        String name = definition.getName();
                        if (name != null && !name.equals("null")) {
                            boolean noted = definition.isNoted();
                            boolean members = definition.isMembersOnly();
                            boolean stackable = definition.isStackable();
                            int alchPrice = definition.getValue();
                            String printable = definition.getName() + " (" + definition.getID() + ")";
                            if (noted) {
                                printable += " (n)";
                            }
                            if (members) {
                                printable += " (m)";
                            }
                            if (stackable) {
                                printable += " (s)";
                            }
                            printable += " (ap " + alchPrice + ")";
                            pw.println(printable);
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        long milliseconds = System.currentTimeMillis() - time_stamp;
        System.out.println("Item list dumped to .../Sigma/item_dump.txt... in " + milliseconds + "ms...");
    }

    /**
     * Gets the data from the item dump.
     *
     * Dumps the item data if a dump file has not yet been created.
     *
     * @return The data.
     */
    public static String[] getDumpData() {
        if (!hasDumped()) {
            dump();
        }
        ArrayList<String> dumpData = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(getDumpFile()));
            String ln;
            while ((ln = br.readLine()) != null) {
                dumpData.add(ln);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dumpData.toArray(new String[dumpData.size()]);
    }

    /**
     * Returns the unnoted id of the specified RSItem, regardless of its state. Returns -1 if the item or item definition is null.
     *
     * @param item The RSItem.
     * @return The unnoted id of the specified RSItem, regardless of its state. Returns -1 if the item or item definition is null.
     */
    public static int getIdUnnoted(RSItem item) {
        if (item != null) {
            RSItemDefinition definition = item.getDefinition();
            if (definition != null) {
                return definition.isNoted() ? item.getID() - 1 : item.getID();
            }
        }
        return -1;
    }

    /**
     * Returns the noted id of the specified RSItem, regardless of its state. Returns -1 if the item or item definition is null.
     *
     * @param item The RSItem.
     * @return The noted id of the specified RSItem, regardless of its state. Returns -1 if the item or item definition is null.
     */
    public static int getIdNoted(RSItem item) {
        if (item != null) {
            RSItemDefinition definition = item.getDefinition();
            if (definition != null && !definition.isStackable()) {
                return definition.isNoted() ? item.getID() : item.getID() + 1;
            }
        }
        return -1;
    }

    /**
     * Returns the current id of the specified RSItem.
     *
     * @param item The RSItem.
     * @return The current id of the specified RSItem.
     */
    public static int getId(RSItem item) {
        return item.getID();
    }

    /**
     * Checks to see if the specified RSItem matches any of the specified ids.
     *
     * @param item              The item being checked.
     * @param checkCurrentState True if the id must match the current state id of the item (noted/unnoted), false if the id can match any state id of the item.
     * @param ids               The ids being checked.
     * @return True if the specified RSItem matches any of the specified ids, false otherwise.
     */
    public static boolean matches(RSItem item, boolean checkCurrentState, int... ids) {
        if (item != null && ids != null) {
            for (int id : ids) {
                if (getId(item) != -1 && (checkCurrentState ? getId(item) == id : (getIdNoted(item) == id || getIdUnnoted(item) == id))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified RSItem matches any of the specified names.
     *
     * The name matching is NOT case sensitive.
     *
     * @param item  The item being checked.
     * @param names The names being checked.
     * @return True if the specified RSItem matches any of the specified names, false otherwise.
     */
    public static boolean matches(RSItem item, String... names) {
        if (item != null && names != null) {
            RSItemDefinition definition = item.getDefinition();
            if (definition != null) {
                String itemName = definition.getName();
                if (itemName != null) {
                    for (String name : names) {
                        if (name != null && itemName.equalsIgnoreCase(name)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
