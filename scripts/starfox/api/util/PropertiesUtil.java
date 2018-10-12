package scripts.starfox.api.util;

import org.tribot.util.Util;
import scripts.starfox.api.Client;

import java.io.*;
import java.util.Properties;

/**
 * The PropertiesUtil class provides an easy way to save and load single file properties.
 * All property files are stored in the .tribot/Sigma/ directory.
 *
 * @author Nolan
 */
public class PropertiesUtil {
    
    /**
     * The working directory for the Sigma folder.
     */
    private static final String directory = Util.getWorkingDirectory().getAbsolutePath() + "/Sigma/";

    /**
     * An object used to store and load properties.
     */
    private static final Properties p = new Properties();


    /**
     * Gets the table of properties.
     *
     * @return The properties.
     */
    public static Properties properties() {
        return p;
    }

    /**
     * Gets the file directory for the Sigma folder.
     *
     * @return The file directory of the Sigma folder.
     */
    public static String directory() {
        return directory;
    }

    /**
     * Adds a string property to the table.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void add(String key, String value) {
        p.setProperty(key, value);
    }

    /**
     * Adds a property to the table.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void add(String key, Object value) {
        add(key, value.toString());
    }

    /**
     * Adds a boolean property to the table.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void add(String key, boolean value) {
        add(key, "" + value);
    }

    /**
     * Adds an int property to the table.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void add(String key, int value) {
        add(key, "" + value);
    }

    /**
     * Adds a double property to the table.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void add(String key, double value) {
        add(key, "" + value);
    }

    /**
     * Adds a float property to the table.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void add(String key, float value) {
        add(key, "" + value);
    }

    /**
     * Clears the all of the properties from the table.
     */
    public static void clear() {
        properties().clear();
    }

    /**
     * Gets the value held by the specified key.
     *
     * @param key The key.
     * @return The value.
     */
    public static String get(String key) {
        return p.getProperty(key);
    }

    /**
     * Gets the value held by the specified key as a boolean.
     *
     * @param key The key.
     * @return The value.
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Gets the value held by the specified key as an integer.
     *
     * @param key The key.
     * @return The value.
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Gets the value held by the specified key as a double.
     *
     * @param key The key.
     * @return The value.
     */
    public static double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    /**
     * Gets the value held by the specified key as a float.
     *
     * @param key The key.
     * @return The value.
     */
    public static double getFloat(String key) {
        return Float.parseFloat(get(key));
    }

    /**
     * Loads the properties contained in the file at the specified path into the table.
     *
     * @param file The file path.
     * @throws java.io.FileNotFoundException If the file could not be found.
     * @throws java.io.IOException           If there is an error reading from the file.
     */
    public static void load(String file) throws FileNotFoundException, IOException {
        try (FileReader fr = new FileReader(directory() + file)) {
            properties().load(fr);
        }
        System.out.println("Successfully loaded properties from: " + directory() + file);
    }

    /**
     * Saves the properties currently stored in the table.
     *
     * @param file The file path to save to.
     * @throws IOException If there is an error creating, finding, or writing to the file.
     */
    public static void save(String file) throws IOException {
        save(file, "");
    }

    /**
     * Saves the properties currently stored in the table.
     *
     * @param file     The file path to save to.
     * @param comments The comments.
     * @throws IOException If there is an error creating, finding, or writing to the file.
     */
    public static void save(String file, String comments) throws IOException {
        File f = new File(directory() + file);
        if (!f.exists()) {
            if (!f.createNewFile()) {
                Client.println("There was a problem creating a new property file at: " + f.getAbsolutePath());
                return;
            }
            System.out.println("Successfully created a new property file at: " + f.getAbsolutePath());
        }
        try (FileWriter fw = new FileWriter(f)) {
            properties().store(fw, comments);
        }
        System.out.println("Successfully stored properties.");
    }
}
