package scripts.starfox.api2007.walking.pathfinding;

import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.walking.Map07;
import scripts.starfox.api2007.walking.Obstacles;

/**
 *
 * @author Spencer
 */
public class AStarData {

    private final int x, y, plane, collisionData;
    private final boolean isBlocked, containsDoor, containsObstacle;
    public final boolean blockedN, blockedS, blockedE, blockedW;
    
    protected final boolean preloaded;

    protected AStarData(int x, int y, int plane, int collisionData, boolean isBlocked, boolean containsDoor, boolean containsObstacle,
            boolean blockedN, boolean blockedS, boolean blockedE, boolean blockedW, boolean preloaded) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.collisionData = collisionData;
        this.isBlocked = isBlocked;
        this.containsDoor = containsDoor;
        this.containsObstacle = containsObstacle;
        this.blockedN = blockedN;
        this.blockedS = blockedS;
        this.blockedE = blockedE;
        this.blockedW = blockedW;
        this.preloaded = preloaded;
    }
    
    protected AStarData(int x, int y, int plane, int collisionData, boolean isBlocked, boolean preloaded) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.collisionData = collisionData;
        this.isBlocked = isBlocked;
        this.containsDoor = false;
        this.containsObstacle = false;
        this.blockedN = false;
        this.blockedS = false;
        this.blockedE = false;
        this.blockedW = false;
        this.preloaded = preloaded;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlane() {
        return plane;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public int getCollisionData() {
        return collisionData;
    }

    public boolean containsDoor() {
        return containsDoor;
    }

    public boolean containsObstacle() {
        return containsObstacle;
    }

    /**
     * Returns an RSTile representation of this AStarData.
     *
     * @return An RSTile representation of this AStarData.
     */
    public RSTile toRSTile() {
        return new RSTile(x, y, plane);
    }

    /**
     * Returns an AStarData object from the specified tile. If the tile is not loaded, this method returns null.
     *
     * @param tile The tile that is being converted into an AStarData.
     * @return An AStarData object from the specified tile.
     */
    public static AStarData fromRSTile(RSTile tile) {
        if (Map07.isTileLoaded(tile)) {
            RSTile north = tile.translate(0, 1);
            RSTile south = tile.translate(0, -1);
            RSTile east = tile.translate(1, 0);
            RSTile west = tile.translate(-1, 0);
            return new AStarData(tile.getX(), tile.getY(), tile.getPlane(),
                    Map07.getCollisionData(tile), Map07.isStandardBlocked(tile), Map07.isObjectAtTile(tile, ArrayUtil.toArrayString(Obstacles.doors)),
                    Obstacles.contains(tile), Map07.isStandardBlocked(north), Map07.isStandardBlocked(south), Map07.isStandardBlocked(east), Map07.isStandardBlocked(west), false);
        }
        return null;
    }
}
