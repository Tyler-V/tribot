package scripts.starfox.api2007.walking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.entities.Objects07;

/**
 * @author Nolan
 */
public class Obstacles {

    /**
     * A list of obstacles that, when passed, disappear from their current position indefinitely or for a brief moment.
     */
    public static final ArrayList<String> doors = new ArrayList<>(Arrays.asList(new String[]{"Door", "Gate", "Gate of War", "Rickety door", "Large Door",
        "Prison Door"}));

    /**
     * A list of obstacles that, when passed, stay at their current position and never move.
     */
    public static final ArrayList<String> obstacles = new ArrayList<>(Arrays.asList(new String[]{"Wilderness ditch", "Doorway", "Rockfall"}));

    /**
     * A list of obstacles that will change any of the player's values when clicked: x location, y location, or plane.
     */
    public static final ArrayList<String> teleports = new ArrayList<>(Arrays.asList(new String[]{"Stairs", "Staircase", "Cave"}));

    /**
     * A list of options that are used to traverse obstacles.
     */
    public static final ArrayList<String> commands = new ArrayList<>(Arrays.asList(new String[]{"Open", "Cross", "Mine Rockfall", "Climb", "Climb-down",
        "Climb-up", "Enter"}));
    
    public static boolean isDoor(RSObject object) {
        RSObjectDefinition def = object.getDefinition();
        if (def != null) {
            return ArrayUtil.containsPartOf(def.getName(), doors.toArray(new String[0]));
        }
        return false;
    }
    
    /**
     * Gets all of the obstacle names.
     *
     * @return The obstacle names.
     */
    public static final String[] getAll() {
        List<String> names = new ArrayList<>(doors.size() + Obstacles.obstacles.size());
        names.addAll(doors);
        names.addAll(Obstacles.obstacles);
        return names.toArray(new String[names.size()]);
    }

    /**
     * Gets all of the obstacles on the specified path.
     *
     * @param path The path.
     * @return The obstacles on the path.
     */
    public static final RSObject[] getObstaclesOnPath(RSTile[] path) {
        if (path == null || path.length < 1) {
            return new RSObject[0];
        }
        List<RSObject> objects = new ArrayList<>();
        String[] allObjectNames = getAll();
        for (RSTile tile : path) {
            RSObject obstacle = Objects07.getObjectAt(tile, allObjectNames);
            if (obstacle != null) {
                objects.add(obstacle);
            }
        }
        return objects.toArray(new RSObject[objects.size()]);
    }

    /**
     * Gets all of the obstacles on the specified path.
     *
     * @param path The path.
     * @return The obstacles on the path.
     */
    public static final RSObject[] getObstaclesOnPath(List<RSTile> path) {
        if (path == null || path.size() < 1) {
            return new RSObject[0];
        }
        return getObstaclesOnPath(path.toArray(new RSTile[path.size()]));
    }

    /**
     * Gets the next obstacle on the specified path.
     *
     * @param path The path.
     * @return The next obstacle on the path. Null if no obstacles that need to be traversed are on the path.
     */
    public static final RSObject getNextObstacle(RSTile[] path) {
        if (path != null && path.length > 0) {
            RSObject[] objects = getObstaclesOnPath(path);
            for (int i = 0; i < path.length; i++) {
                RSTile t = path[i];
                for (RSObject obstacle : objects) {
                    if (obstacle.getPosition().equals(t)) {
                        if ((i != path.length - 1 && !PathFinding.canReach(path[i + 1], false))
                                || (i == path.length - 1 && !PathFinding.canReach(t, false))) {
                            return obstacle;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks to see if the player is past the specified obstacle.
     *
     * @param path     The path.
     * @param obstacle The obstacle.
     * @return True if the player is past the obstacle, false otherwise.
     */
    public static final boolean isPastObstacle(RSTile[] path, RSObject obstacle) {
        if (path == null || path.length < 1 || obstacle == null) {
            return false;
        }
        RSTile player = Player07.getPosition();
        for (int i = 0; i < path.length; i++) {
            if (obstacle.getPosition().equals(path[i])) {
                for (int j = i + 1; j < path.length; j++) {
                    if (player.equals(path[j])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns all of the obstacles at the specified tile.
     *
     * @param tile The tile being checked.
     * @return All of the obstacles at the specified tile.
     */
    public static boolean contains(RSTile tile) {
        RSObject[] obstcls = Objects.getAt(tile);
        for (RSObject object : obstcls) {
            for (String name : Obstacles.obstacles) {
                RSObjectDefinition def = object.getDefinition();
                if (def != null) {
                    String objectName = def.getName();
                    if (objectName != null && objectName.contains(name)) {
                        return true;
                    }
                }
            }
            for (String name : Obstacles.doors) {
                RSObjectDefinition def = object.getDefinition();
                if (def != null) {
                    String objectName = def.getName();
                    if (objectName != null && objectName.contains(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
