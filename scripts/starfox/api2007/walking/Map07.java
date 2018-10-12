package scripts.starfox.api2007.walking;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.entities.Entities;

/**
 * The RSMap class provides utility methods related to information about the Old School Runescape map.
 *
 * A map is defined as a 104x104 tile area in which the player resides. The base X and base Y coordinates are the coordinates of the most south-western tile in the current map.
 *
 * @author Nolan
 */
public class Map07 {

    /**
     * Gets the collision data for the specified RSTile.
     *
     * @param tile The RSTile being tested.
     * @return The collision data for the specified RSTile.
     */
    public static int getCollisionData(RSTile tile) {
        int xOffset = Game.getBaseX();
        int yOffset = Game.getBaseY();
        int xT = tile.getX();
        int yT = tile.getY();
        int x = xT - xOffset;
        int y = yT - yOffset;
        int[][] collisionData = PathFinding.getCollisionData();
        if (collisionData == null || x < 0 || y < 0 || x > collisionData.length - 1 || y > collisionData[0].length - 1) {
            return -1;
        }
        return collisionData[x][y];
    }

    /**
     * Gets the tile located at the current map base.
     *
     * The map base is defined as the most south-western tile of the current map.
     *
     * @return The map base.
     */
    public static RSTile getBase() {
        return new RSTile(Game.getBaseX(), Game.getBaseY(), Game.getPlane());
    }

    /**
     * Gets the current map.
     *
     * @return The current map.
     */
    public static RSArea getMap() {
        RSTile base = getBase();
        return new RSArea(base.translate(1, 1), new RSTile(base.getX() + 103, base.getY() + 103, base.getPlane()));
    }

    /**
     * Gets all of the tiles in the current map.
     *
     * @return The tiles in the current map.
     */
    public static RSTile[] getMapTiles() {
        return getMap().getAllTiles();
    }

    /**
     * Gets the loaded tiles as a 2d array.
     *
     * @return A 2d array representing the loaded tiles.
     */
    public static RSTile[][] getMapTiles2D() {
        RSTile base = getBase().translate(1, 1);
        RSTile[] mapTiles = getMapTiles();
        RSTile[][] tiles = new RSTile[103 - 5][103 - 5];
        for (RSTile tile : mapTiles) {
            tiles[tile.getX() - base.getX()][tile.getY() - base.getY()] = tile;
        }
        return tiles;
    }

    /**
     * Checks if the specified tile is loaded.
     *
     * A loaded tile is defined by a tile that is contained in the current map.
     *
     * @param tile The tile.
     * @see #getMapTiles()
     * @return True if the specified tile is loaded, false otherwise.
     */
    public static boolean isTileLoaded(RSTile tile) {
        return ArrayUtil.contains(tile, getMapTiles());
    }

    /**
     * Checks the specified collision flag on the specified collision data. Returns true if the flag exists, false otherwise.
     *
     * @param flag      The collision data that is being tested.
     * @param checkFlag The collision flag that is being checked.
     * @return True if the flag exists, false otherwise.
     */
    public static boolean isFlagOnTile(int flag, int checkFlag) {
        return (flag & checkFlag) == checkFlag;
    }

    /**
     * Returns true if there is any standard pathfinding block on the specified RSTile, false otherwise.
     *
     * @param tile The RSTile that is being checked.
     * @return True if there is any standard pathfinding block on the specified RSTile, false otherwise.
     */
    public static boolean isStandardBlocked(RSTile tile) {
        int flag = getCollisionData(tile);
        return !PathFinding.isTileWalkable(tile)
                || isFlagOnTile(flag, Flags.DECORATION_BLOCK)
                || isFlagOnTile(flag, Flags.OBJECT_BLOCK)
                || isFlagOnTile(flag, Flags.BLOCKED)
                || isFlagOnTile(flag, Flags.OBJECT_TILE);
    }

    /**
     * Gets the nearest non-blocked tile to the specified tile.
     *
     * @param tile The tile.
     * @return The nearest non-blocked tile.
     */
    public static Positionable getNonBlocked(Positionable tile) {
        Positionable leastBlocked = null;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (Math.abs(i) != Math.abs(j)) {
                    RSTile tempTile = new RSTile(tile.getPosition().getX() + i, tile.getPosition().getY() + j);
                    if ((!isStandardBlocked(tempTile) || Obstacles.contains(tempTile)) && canWalkTo(tile.getPosition(), tempTile, false) && PathFinding.canReach(tile, true)
                            && isTileBetter(tile, tempTile, leastBlocked)) {
                        leastBlocked = tempTile;
                    }
                }
            }
        }
        return leastBlocked != null ? leastBlocked : tile;
    }

    /**
     * Checks to see if the "current" tile is a better fit to get to the base tile than the "best" tile.
     *
     * @param base    The base tile in which the overall path is being generated to.
     * @param current The current tile that is being checked.
     * @param best    The current best tile.
     * @return True if the current tile is better than the current best, false otherwise.
     */
    private static boolean isTileBetter(Positionable base, Positionable current, Positionable best) {
        if (current == null || base == null) {
            return false;
        } else if (best == null) {
            return true;
        } else {
            final double currentDistance = Entities.aStarDistanceTo(base, current);
            final double bestDistance = Entities.aStarDistanceTo(base, best);
            return currentDistance < bestDistance || (currentDistance == bestDistance && Entities.distanceTo(current) < Entities.distanceTo(best));
        }
    }

    /**
     * Checks to see if the target tile can be walked to from the start tile.
     *
     * @param start       The start tile.
     * @param target      The target tile.
     * @param checkObject True if objects should be checked, false otherwise.
     * @return True if the target tile can be walked to from the start tile, false otherwise.
     */
    public static boolean canWalkTo(RSTile start, RSTile target, boolean checkObject) {
        if (start == null || target == null) {
            return false;
        }
        int startFlag = getCollisionData(start);
        int targetFlag = getCollisionData(target);
        RSTile north = new RSTile(start.getX(), start.getY() + 1);
        RSTile east = new RSTile(start.getX() + 1, start.getY());
        RSTile south = new RSTile(start.getX(), start.getY() - 1);
        RSTile west = new RSTile(start.getX() - 1, start.getY());
        //Check NORTHWEST
        if (start.getX() - 1 == target.getX() && start.getY() + 1 == target.getY()) {
            if (checkObject && (isStandardBlocked(north) || isStandardBlocked(west))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_NORTH)) || (isFlagOnTile(targetFlag, Flags.WALL_SOUTH))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_WEST)) || (isFlagOnTile(targetFlag, Flags.WALL_EAST))) {
                return false;
            }
        }
        //Check NORTH
        if (start.getY() + 1 == target.getY()) {
            if ((isFlagOnTile(startFlag, Flags.WALL_NORTH) && !isObjectAtTile(start, ArrayUtil.toArrayString(Obstacles.doors)))
                    && (isFlagOnTile(targetFlag, Flags.WALL_SOUTH) && !isObjectAtTile(target, ArrayUtil.toArrayString(Obstacles.doors)))) {
                return false;
            }
        }
        //Check NORTHEAST
        if (start.getX() + 1 == target.getX() && start.getY() + 1 == target.getY()) {
            if (checkObject && (isStandardBlocked(north) || isStandardBlocked(east))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_NORTH)) || (isFlagOnTile(targetFlag, Flags.WALL_SOUTH))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_EAST)) || (isFlagOnTile(targetFlag, Flags.WALL_WEST))) {
                return false;
            }
        }
        //Check EAST
        if (start.getX() + 1 == target.getX()) {
            if ((isFlagOnTile(startFlag, Flags.WALL_EAST) && !isObjectAtTile(start, ArrayUtil.toArrayString(Obstacles.doors)))
                    && (isFlagOnTile(targetFlag, Flags.WALL_WEST) && !isObjectAtTile(target, ArrayUtil.toArrayString(Obstacles.doors)))) {
                return false;
            }
        }
        //Check SOUTHEAST
        if (start.getX() + 1 == target.getX() && start.getY() - 1 == target.getY()) {
            if (checkObject && (isStandardBlocked(south) || isStandardBlocked(east))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_SOUTH)) || (isFlagOnTile(targetFlag, Flags.WALL_NORTH))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_EAST)) || (isFlagOnTile(targetFlag, Flags.WALL_WEST))) {
                return false;
            }
        }
        //Check SOUTH
        if (start.getY() - 1 == target.getY()) {
            if ((isFlagOnTile(startFlag, Flags.WALL_SOUTH) && !isObjectAtTile(start, ArrayUtil.toArrayString(Obstacles.doors)))
                    && (isFlagOnTile(targetFlag, Flags.WALL_NORTH) && !isObjectAtTile(target, ArrayUtil.toArrayString(Obstacles.doors)))) {
                return false;
            }
        }
        //Check SOUTHWEST
        if (start.getX() - 1 == target.getX() && start.getY() - 1 == target.getY()) {
            if (checkObject && (isStandardBlocked(south) || isStandardBlocked(west))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_SOUTH)) || (isFlagOnTile(targetFlag, Flags.WALL_NORTH))) {
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_WEST)) || (isFlagOnTile(targetFlag, Flags.WALL_EAST))) {
                return false;
            }
        }
        //Check WEST
        if (start.getX() - 1 == target.getX()) {
            if ((isFlagOnTile(startFlag, Flags.WALL_WEST) && !isObjectAtTile(start, ArrayUtil.toArrayString(Obstacles.doors)))
                    && (isFlagOnTile(targetFlag, Flags.WALL_EAST) && !isObjectAtTile(target, ArrayUtil.toArrayString(Obstacles.doors)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if any of the objects with the specified names are at the specified positionable, false otherwise.
     *
     * The name only needs to match partially in order for this to return true.
     *
     * @param p      The positionable.
     * @param oNames The names.
     * @return True if any of the objects with the specified names are at the specified positionable, false otherwise.
     */
    public static boolean isObjectAtTile(Positionable p, String... oNames) {
        for (RSObject o : Objects.getAt(p)) {
            RSObjectDefinition def = o.getDefinition();
            if (def != null) {
                String name = def.getName();
                if (name != null) {
                    if (!name.isEmpty() && ArrayUtil.containsPartOf(name, oNames)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
