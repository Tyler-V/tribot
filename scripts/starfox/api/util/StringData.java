package scripts.starfox.api.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Defines a key and value that can be easily used for options, settings, etc.
 *
 * @author Spencer
 */
public class StringData {

    private final String key;
    private final String value;

    public StringData(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of this StringData.
     *
     * @return The key of this StringData.
     */
    public final String getKey() {
        return key;
    }

    /**
     * Returns the value of this StringData.
     *
     * @return The value of this StringData.
     */
    public final String getValue() {
        return value;
    }

    /**
     * Returns a String representation of the StringData, showing both the key and value.
     *
     * @return A String representation of the StringData, showing both the key and value.
     */
    @Override
    public String toString() {
        return "Key: " + key + " | Value: " + value;
    }

    /**
     * Retrieves all string data from the specified String in the form "{key}data{!key}".
     *
     * If the specified string has any "junk data", it is ignored. IE, in the string "{examp}data{!examp} junk {examp2}data{!examp2}", " junk " would be
     * ignored. There is no limit
     * to how many StringDatas can be retrieved, and duplicates are allowed.
     *
     * @param rawData The string representing all raw data to be extracted into StringData.
     * @return All string data from the specified String.
     */
    public static ArrayList<StringData> getData(String rawData) {
        String[] splits = rawData.split("\\{\\![^\\{\\}]+\\}");
        ArrayList<StringData> data = new ArrayList<>();
        System.out.println("Splits: " + Arrays.toString(splits));
        for (String split : splits) {
            String valueTest = split.replaceAll("\\{[^\\{\\}]+\\}", "DDFFIIDD");
            int valueStart = valueTest.indexOf("DDFFIIDD");
            if (valueStart >= 0) {
                String value = split.replaceAll("\\{[^\\{\\}]+\\}", "").substring(valueStart);
                int keyStart = split.lastIndexOf("{") + 1;
                int keyEnd = split.lastIndexOf("}");
                if (keyStart >= 0 && keyEnd >= 0) {
                    String key = split.substring(keyStart, keyEnd);
                    data.add(new StringData(key, value));
                }
            }
        }
        return data;
    }

    /**
     * Returns the value that is attached to this key using the specified raw data, or null if the raw data or key is null.
     *
     * @param rawData The string that is being checked for StringData.
     * @param key     The key that is being searched for.
     * @return The value that is attached to this key using the specified raw data, or null if the raw data or key is null.
     */
    public static String getValue(String rawData, String key) {
        if (rawData != null && key != null) {
            ArrayList<StringData> datas = getData(rawData);
            for (StringData data : datas) {
                if (data.getKey().equalsIgnoreCase(key)) {
                    return data.getValue();
                }
            }
        }
        return null;
    }
}
