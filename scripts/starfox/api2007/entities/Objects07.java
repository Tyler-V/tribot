package scripts.starfox.api2007.entities;

import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Objects07 class is a utility class that can get RSObject's without the need for array checking. Note that null checking is still required.
 *
 * @author Nolan
 */
public class Objects07 {

    /**
     * Gets the specified objects name.
     *
     * @param object The object to get the name of.
     * @return The name of the object.
     *         Null if the object or the objects definition is null.
     */
    public static String getName(RSObject object) {
        if (object != null) {
            RSObjectDefinition definition = object.getDefinition();
            if (definition != null) {
                return definition.getName();
            }
        }
        return null;
    }

    /**
     * Checks to see if the specified object is valid.
     *
     * An object is considered valid if it is non-null, on screen, clickable, and reachable.
     *
     * @param object The object being tested.
     * @param reach  Whether or not to check if you can reach the object or not.
     * @return True if the specified object is valid, false otherwise.
     */
    public static boolean isValid(RSObject object, boolean reach) {
        if (reach) {
            if (!PathFinding.canReach(object, true)) {
                return false;
            }
        }
        return object != null && object.isOnScreen() && object.isClickable();
    }

    /**
     * Gets the nearest object with the specified ID.
     *
     * @param id   The ID of the object.
     * @param dist The distance away from the player.
     * @return The nearest object. Null if no objects were found.
     */
    public static RSObject getObject(final int id, final int dist) {
        RSObject[] objs = Objects.find(dist, id);
        Sorting.sortByDistance(objs, Player.getPosition(), true);
        return AntiBan.selectNextTarget(objs);
    }

    /**
     * Gets the nearest object with the specified name.
     *
     * @param name     The name of the object.
     * @param distance The distance away from the player.
     * @return The nearest object. Null if no objects were found.
     */
    public static RSObject getObject(String name, int distance) {
        if (name == null) {
            return null;
        }
        RSObject[] objs = Objects.find(distance, name);
        Sorting.sortByDistance(objs, Player.getPosition(), true);
        return objs.length > 0 ? objs[0] : null;
    }

    /**
     * Gets the nearest object that is accepted by the specified filter.
     *
     * @param filter   The filter.
     * @param distance The distance away from the player.
     * @return The nearest object. Null if no objects were found.
     */
    public static RSObject getObject(Filter<RSObject> filter, int distance) {
        if (filter == null) {
            return null;
        }
        RSObject[] objs = Objects.find(distance, filter);
        Sorting.sortByDistance(objs, Player.getPosition(), true);
        return objs.length > 0 ? objs[0] : null;
    }

    /**
     * Gets the first object that is on the specified tile.
     *
     * @param tile The tile.
     * @return The first object. Null if no objects were found.
     */
    public static RSObject getObjectAt(RSTile tile) {
        if (tile == null) {
            return null;
        }
        RSObject[] objs = Objects.getAt(tile);
        return objs.length > 0 ? objs[0] : null;
    }

    /**
     * Gets the first object whose name matches one of the specified names that is on the specified tile.
     *
     * @param tile  The tile.
     * @param names The names to look for.
     * @return The first object. Null if no objects were found.
     */
    public static RSObject getObjectAt(RSTile tile, String... names) {
        if (tile == null || names == null || names.length < 1) {
            return null;
        }
        RSObject[] objs = Objects.getAt(tile);
        for (RSObject object : objs) {
            String name = getName(object);
            if (name != null && ArrayUtil.containsPartOf(name, names)) {
                return object;
            }
        }
        return null;
    }

    /**
     * Gets the objects at the specified tile with any of the specified names and any of the specified options.
     *
     * If either names or options is null, they are ignored, and all objects with any name/option respectively will be included in the search.
     *
     * @param tile    The tile being checked.
     * @param names   The names being searched for.
     * @param options The options being searched for.
     * @return The object at the specified tile matching the specified filter, or null if no objects matching the specified filter are at the specified tile.
     */
    public static RSObject getObjectAt(RSTile tile, String[] names, String[] options) {
        if (tile == null || names == null || options == null) {
            return null;
        }
        RSObject[] objs = Objects.getAt(tile, getNameFilter(names, options));
        if (objs != null) {
            for (RSObject object : objs) {
                if (object != null) {
                    return object;
                }
            }
        }
        return null;
    }

    /**
     * Gets the nearest object whose name matches the specified name that is inside the specified area.
     *
     * @param name The name of the object.
     * @param area The area.
     * @return The object closest to you inside the specified area.
     */
    public static RSObject getObjectIn(final String name, final RSArea area) {
        RSObject[] objs = Objects.find(104, new Filter<RSObject>() {
            @Override
            public boolean accept(RSObject o) {
                String n = getName(o);
                return n != null && n.equals(name) && area.contains(o);
            }
        });
        return objs.length > 0 ? objs[0] : null;
    }

    /**
     * Gets the closest Object with any of the specified names and any of the specified options.
     *
     * If either names or options is null, they are ignored, and all objects with any name/option respectively will be included in the search.
     *
     * @param names    The names being searched for.
     * @param options  The options being searched for.
     * @param distance The distance range.
     * @see #getObject(Filter, int)
     * @return The closest object. Null if no objects were found.
     */
    public static RSObject getObject(final String[] names, final String[] options, final int distance) {
        return getObject(getNameFilter(names, options), distance);
    }

    /**
     * Gets an array of RSObjects with any of the specified names and any of the specified options within the specified range.
     *
     * If either names or options is null, they are ignored, and all objects with any name/option respectively will be included in the search.
     *
     * @param names    The names being searched for.
     * @param options  The options being searched for.
     * @param distance The distance range.
     * @see #getObject(Filter, int)
     * @return An array of RSObjects with any of the specified names and any of the specified options within the specified range. Null if no objects are found.
     */
    public static RSObject[] getObjects(final String[] names, final String[] options, final int distance) {
        return Objects.find(distance, getNameFilter(names, options));
    }

    /**
     * Returns an ArrayList of RSTiles that represent the tiles that contain the specified object.
     *
     * @param object The object.
     * @return An ArrayList of RSTiles that represent the tiles that contain the specified object.
     */
    public static ArrayList<RSTile> getRealPosition(final RSObject object) {
        final RSTile[] tiles = object.getAllTiles();
        final ArrayList<RSTile> listTiles = new ArrayList<>();
        if (tiles == null || tiles.length == 0) {
            listTiles.add(object.getPosition());
        } else {
            listTiles.addAll(Arrays.asList(tiles));
        }
        return listTiles;
    }

    private static Filter getNameFilter(final String[] names, final String[] options) {
        return new Filter<RSObject>() {
            @Override
            public boolean accept(RSObject o) {
                if (o != null) {
                    RSObjectDefinition oDef = o.getDefinition();
                    if (oDef != null) {
                        String name = oDef.getName();
                        if (names == null || (name != null && ArrayUtil.containsPartOf(name, names))) {
                            String[] actions = oDef.getActions();
                            if (options == null) {
                                return true;
                            } else {
                                if (actions != null && actions.length != 0) {
                                    ArrayList<String> actionList = new ArrayList<>();
                                    actionList.addAll(Arrays.asList(actions));
                                    return ArrayUtil.containsPartOf(actionList, options);
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };
    }
}
