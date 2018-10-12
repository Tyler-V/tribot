package scripts.starfox.api2007.skills.mining;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.Settings;

/**
 * @author Nolan
 */
public final class Mining {

    private static final int MOTHERLODE_SACK_SETTING_INDEX = 375;

    /**
     *
     * SETTINGS
     *
     */
    
    /**
     * Checks to see if there are any ores that need to be collected from the sack in the motherlode mine.
     *
     * @return True if there are any ores, false otherwise.
     */
    public static final boolean isOreInSack() {
        int setting = Settings.get(MOTHERLODE_SACK_SETTING_INDEX);
        return setting == 4 || setting == 12;
    }

    /**
     *
     * ROCK METHODS
     *
     */
    
    /**
     * Gets the nearest rock that matches one of the specified rock types that is inside the distance range specified.
     *
     * @param distance The distance range.
     * @param rocks The rock types to look for.
     * @return A rock that matches one of the rocks specified. Null if no rocks were found.
     */
    public static final RSObject getRock(int distance, Rock... rocks) {
        RSObject[] objects = getRocks(distance, rocks);
        return objects.length > 0 ? objects[0] : null;
    }

    /**
     * Gets all of the rocks in the distance range that match any of the specified rock types.
     *
     * @param distance The distance range.
     * @param rocks The rock types to look for.
     * @return The rocks. An empty array is returned if no rocks were found.
     */
    public static final RSObject[] getRocks(int distance, Rock... rocks) {
        RSObject[] objects = Objects.findNearest(distance, getMinableRockFilter(rocks));
        return objects;
    }

    /**
     * Gets the rock type of the specified object.
     *
     * @param object The object.
     * @return The rock type.
     */
    public static final Rock getRockType(final RSObject object) {
        if (object == null) {
            return Rock.UNKNOWN;
        }
        final RSObjectDefinition object_definition = object.getDefinition();
        if (object_definition == null) {
            return Rock.UNKNOWN;
        }
        final short[] modified_colors = object_definition.getModifiedColors();
        for (Rock rock : Rock.values()) {
            for (short s : modified_colors) {
                if (ArrayUtil.contains(s, rock.getModifiedColours())) {
                    return rock;
                }
            }
        }
        return Rock.UNKNOWN;
    }

    /**
     * Checks to see if there is a rock that is mineable at the specified tile.
     *
     * @param tile The tile.
     * @return True if there is a mineable rock at the specified tile, false otherwise.
     */
    public static final boolean isRockMinableAt(final RSTile tile) {
        if (tile == null) {
            return false;
        }
        final RSObject[] objects = Objects.getAt(tile);
        for (RSObject object : objects) {
            if (isMinableRock(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the object specified is a minable rock.
     *
     * @param object The object.
     * @return True if it is minable, false otherwise.
     */
    public static final boolean isMinableRock(final RSObject object) {
        return object != null && isRock(object) && !isDepleted(object);
    }

    /**
     * Checks to see if the specified object is a depleted rock.
     *
     * @param object The object.
     * @return True if it is a depleted rock, false otherwise.
     */
    public static final boolean isDepleted(final RSObject object) {
        return object != null && isRock(object) && getRockType(object) == Rock.UNKNOWN;
    }
    
    /**
     * Checks to see if the specified object is a rock.
     * 
     * @param object The object.
     * @return True if it is a rock, false otherwise.
     */
    public static final boolean isRock(final RSObject object) {
        if (object != null) {
            final RSObjectDefinition object_definition = object.getDefinition();
            if (object_definition != null) {
                final String name = object_definition.getName();
                return name != null && name.equalsIgnoreCase("Rocks");
            }
        }
        return false;
    }
    
    /**
     * Gets the filter that accepts minable rocks.
     * 
     * @param rocks The type of rocks to accept.
     * @return The filter generated.
     */
    private static Filter<RSObject> getMinableRockFilter(final Rock... rocks) {
        return new Filter<RSObject>() {
            @Override
            public boolean accept(RSObject object) {
                for (Rock rock : rocks) {
                    if (getRockType(object) == rock) {
                        return isMinableRock(object);
                    }
                }
                return false;
            }
        };
    }
}
