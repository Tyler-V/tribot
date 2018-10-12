package scripts.starfox.api2007.walking;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.tribot.api.General;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.walking.pathfinding.AStarPathfinder;

/**
 * The TilePath class represents a path made of RSTiles.
 *
 * @author Nolan
 */
public class TilePath {

    private final RSTile[] path;
    private int nextDistance;
    private int xRandom;
    private int yRandom;

    /**
     * Constructs a new TilePath.
     *
     * @param path The tiles on the path.
     */
    public TilePath(RSTile[] path) {
        this.path = path;
        this.nextDistance = General.random(3, 6);
    }

    public TilePath(RSTile target) {
        this.path = AStarPathfinder.get().getTilePath(target);
        this.nextDistance = General.random(3, 6);
    }

    /**
     * Gets the tiles on the path.
     *
     * @return The tiles.
     */
    public RSTile[] getPath() {
        return this.path;
    }

    /**
     * Gets the tiles on the path as an ArrayList.
     *
     * @return The tiles.
     */
    public ArrayList<RSTile> getPathList() {
        return new ArrayList<>(Arrays.asList(path));
    }

    /**
     * Gets the next distance between the game destination and the player position that should be used.
     *
     * @return The next distance.
     */
    public int getNextDistance() {
        return this.nextDistance;
    }

    /**
     * Gets the tile at the end of the path.
     *
     * @return The end tile. Null if path length is 0.
     */
    public RSTile getEnd() {
        if (path.length < 1) {
            return null;
        }
        return path[path.length - 1];
    }

    /**
     * Returns the length of this TilePath.
     *
     * @return The length of this TilePath.
     */
    public int getLength() {
        return path.length;
    }

    /**
     * Gets the next walkable tile on the path.
     *
     * @return The next walkable tile.
     */
    public RSTile getNextTile() {
        if (path == null) {
            return null;
        }
        if (path.length < 2) {
            return getEnd();
        }
        for (int i = path.length - 1; i >= 0; i--) {
            Point mmTile = Projection.tileToMinimap(path[i]);
            if (mmTile != null && Projection.isInMinimap(mmTile)) {
                return path[i];
            }
        }
        return getEnd();
    }

    /**
     * Sets the random x offset.
     *
     * @param x The x value.
     */
    public void setxRandom(int x) {
        this.xRandom = x;
    }

    /**
     * Sets the random y offset.
     *
     * @param y The y value.
     */
    public void setyRandom(int y) {
        this.yRandom = y;
    }

    /**
     * Walks to the next tile on the path if the current game destination is null or the player is within the next distance range of the destination.
     *
     * @return True if successful, false otherwise.
     */
    public boolean walkToNext() {
        RSTile destination = Game.getDestination();
        if (destination != null && Entities.distanceTo(destination) > getNextDistance()) {
            return true;
        }
        resetNextDistance();
        return Walking07.straightWalk(getNextTile());
    }

    /**
     * Walks to the next tile on the path if the current game destination is null or the player is within the next distance range of the destination.
     *
     * @param stopping_condition The stopping condition.
     * @return True if successful, false otherwise.
     */
    public boolean walkToNext(Condition stopping_condition) {
        RSTile destination = Game.getDestination();
        if (destination != null && Entities.distanceTo(destination) > getNextDistance()) {
            return true;
        }
        resetNextDistance();
        return Walking07.straightWalk(getNextTile(), stopping_condition);
    }

    /**
     * Resets the next distance.
     */
    public void resetNextDistance() {
        this.nextDistance = General.random(3, 6);
    }

    /**
     * Returns true if the player is near this path, false otherwise.
     *
     * @return True if the player is near this path, false otherwise.
     */
    public boolean isNear() {
        for (int i = 0; i < (path.length / 3 * 3); i += 3) {
            final RSTile tile = path[i];
            if (Entities.distanceTo(tile) < 12 && Entities.aStarDistanceTo(tile) < 12) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the tile that is closest to the player on this path.
     *
     * @return The tile that is closest to the player on this path.
     */
    public RSTile getClosest() {
        RSTile closest = null;
        RSTile dest = Game.getDestination();
        dest = dest != null ? dest : Player07.getPosition();
        for (RSTile tile : path) {
            if (tile.equals(dest)) {
                return tile;
            }
            if (closest == null || (Entities.distanceTo(dest, tile) <= Entities.distanceTo(dest, closest))) {
                closest = tile;
            }
        }
        return closest;
    }

    /**
     * Returns an updated version of the specified tile path based on the specified target.
     *
     * The target should be the same as the previous target of the specified TilePath in order for this to work correctly. Or, alternatively, passing in a null TilePath will result
     * in the standard generation of a path to the target specified.
     *
     * Will only regenerate a new path to the target if the current path does not use the same target (usually meaning the target was previously out of range of the map) or if the
     * current path is less than 50 tiles long.
     *
     * @param newTarget The target.
     * @param path      The path.
     * @return An updated version of the specified tile path based on the specified target.
     */
    public static TilePath update(RSTile newTarget, TilePath path) {
        if (path == null || (!newTarget.equals(path.getEnd()) && path.getLength() < 10)) {
            return new TilePath(AStarPathfinder.get().getTilePath(newTarget));
        } else {
            ArrayList<RSTile> newPath = new ArrayList<>();
            boolean adding = false;
            final RSTile closest = path.getClosest();
            if (closest == null) {
                return path;
            }
            boolean is = closest.equals(Player07.getPosition());
            for (RSTile tile : path.getPath()) {
                if (adding) {
                    newPath.add(tile);
                }
                if (tile.equals(closest)) {
                    adding = true;
                }
            }
            if (!is) {
                ArrayList<RSTile> path2 = AStarPathfinder.get().getPath(closest);
                if (path2 != null) {
                    Collections.reverse(path2);
                    for (RSTile tempTile : path2) {
                        newPath.add(0, tempTile);
                    }
                }
            } else {
                newPath.add(0, closest);
            }
            return new TilePath(newPath.toArray(new RSTile[0]));
        }
    }
}
