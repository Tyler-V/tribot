package scripts.starfox.api;

import java.util.HashMap;

/**
 * The Argument class is used to safely process the script arguments.
 *
 * @author Nolan
 */
public class Argument {

    /**
     * Gets the script arguments from the specified map.
     *
     * @param argMap The map of arguments, if any.
     * @return The script arguments.
     *         Null if there are no arguments.
     */
    public static String getArguments(HashMap<String, String> argMap) {
        if (argMap == null || argMap.isEmpty()) {
            return null;
        }
        for (String key : argMap.keySet()) {
            String value = argMap.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }
}
