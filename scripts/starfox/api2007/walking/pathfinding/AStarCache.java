package scripts.starfox.api2007.walking.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.Printing;
import scripts.starfox.api.util.DebugTimer;
import scripts.starfox.api.util.FileUtil;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.banking.Bank07;
import scripts.starfox.api2007.walking.Flags;
import scripts.starfox.api2007.walking.Map07;
import static scripts.starfox.api2007.walking.Map07.isFlagOnTile;

/**
 *
 * @author Spencer
 */
public class AStarCache {

    private static final Object lock;
    private static final Point bottomLeft;
    private static final Point topRight;
    private static boolean hasLoaded;

    private static final ArrayList<RSTile> banks;

    private static final MapCacheContainer[] cache;

    static {
        hasLoaded = false;
        lock = new Object();
        bottomLeft = new Point(-1, -1);
        topRight = new Point(-1, -1);
        cache = new MapCacheContainer[3];
        banks = new ArrayList<>();
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new MapCacheContainer();
        }
    }

    public static boolean isBlocked(int x, int y, int plane) {
        AStarData data = getData(new RSTile(x, y, plane));
        return data != null ? isStandardBlocked(data) && !data.containsObstacle() : false;
    }

    public static Point getBottomLeft() {
        return bottomLeft;
    }

    public static Point getTopRight() {
        return topRight;
    }

    public static final boolean update() {
        int trans = 103 - 5;
        final RSTile cornerSW = Map07.getBase();
        final RSTile cornerNE = cornerSW.translate(trans, trans);
        final RSTile cornerSE = cornerSW.translate(trans, 0);
        final RSTile cornerNW = cornerSW.translate(0, trans);
        boolean updated = false;
        synchronized (lock) {
            if (!isLoaded(cornerSW) || !isLoaded(cornerNE) || !isLoaded(cornerSE) || !isLoaded(cornerNW)) {
                RSTile[] mapTiles = Map07.getMapTiles();
                for (RSTile tile : mapTiles) {
                    if (!Map07.getBase().equals(cornerSW)) {
                        return false;
                    }
                    RSTile north = tile.translate(0, 1);
                    RSTile south = tile.translate(0, -1);
                    RSTile east = tile.translate(1, 0);
                    RSTile west = tile.translate(-1, 0);
                    if (!isLoaded(tile)) {
                        boolean bN = Map07.isStandardBlocked(north);
                        boolean bS = Map07.isStandardBlocked(south);
                        boolean bE = Map07.isStandardBlocked(east);
                        boolean bW = Map07.isStandardBlocked(west);
                        AStarData tempData = null;
                        if (Map07.getCollisionData(tile) != 0 || bN || bS || bE || bW) {
                            tempData = AStarData.fromRSTile(tile);
                            if (!banks.contains(tile) && Bank07.isBankAtTile(tile)) {
                                banks.add(tile);
                            }
                            if (tempData != null) {
                                if (bottomLeft.x == -1 || bottomLeft.x > tempData.getX()) {
                                    bottomLeft.x = tempData.getX();
                                }
                                if (bottomLeft.y == -1 || bottomLeft.y > tempData.getY()) {
                                    bottomLeft.y = tempData.getY();
                                }
                                if (topRight.x == -1 || topRight.x < tempData.getX()) {
                                    topRight.x = tempData.getX();
                                }
                                if (topRight.y == -1 || topRight.y < tempData.getY()) {
                                    topRight.y = tempData.getY();
                                }
                            }
                        }
                        if (cache[tile.getPlane()].addData(new Point(tile.getX(), tile.getY()), tempData)) {
                            updated = true;
                        }
                    }
                }
            }
            if (!isLoaded(Player07.getPosition())) {
                Printing.err("Player position is not loaded after updating cache.");
            }
        }
        return updated;
    }

    public static ArrayList<AStarData> getDatas() {
        synchronized (lock) {
            return cache[0].getAll();
        }
    }

    public static ArrayList<Point> getLoadedDatas() {
        synchronized (lock) {
            return (ArrayList<Point>) (cache[0].getAllLoaded().clone());
        }
    }

    public static ArrayList<RSTile> getBanks() {
        synchronized (lock) {
            return banks;
        }
    }

    /**
     * Returns the AStarData that is mapped to the specified RSTile, or null if no AStarData is mapped to the specified RSTile or the specified tile is null.
     *
     * @param tile The tile.
     * @return The AStarData that is mapped to the specified RSTile, or null if no AStarData is mapped to the specified RSTile or the specified tile is null.
     */
    public static AStarData getData(RSTile tile) {
        synchronized (lock) {
            return tile != null ? cache[tile.getPlane()].getData(new Point(tile.getX(), tile.getY())) : null;
        }
    }

    /**
     * Checks to see if the specified RSTile is loaded in the cache.
     *
     * If the specified tile is null, this method immediately returns false.
     *
     * @param tile The tile.
     * @return True if the specified tile is loaded in the cache, false otherwise.
     */
    public static boolean isLoaded(RSTile tile) {
        synchronized (lock) {
            if (cache != null && tile != null) {
                return cache[tile.getPlane()].isLoaded(new Point(tile.getX(), tile.getY()));
            } else {
                return false;
            }
        }
    }

    public static boolean canWalkTo(int x1, int y1, int x2, int y2, int plane1, int plane2) {
        return canWalkTo(x1, y1, x2, y2, plane1, plane2, false);
    }

    public static boolean canWalkTo(int x1, int y1, int x2, int y2, int plane1, int plane2, boolean checkObject) {
        if (plane1 != plane2) {
            return false;
        } else {
            AStarData data1 = getData(new RSTile(x1, y1, plane1));
            AStarData data2 = getData(new RSTile(x2, y2, plane2));
            data1 = data1 == null ? new AStarData(x1, y1, plane1, 0, true, false) : data1;
            data2 = data2 == null ? new AStarData(x2, y2, plane2, 0, true, false) : data2;
            return canWalkTo(data1, data2, checkObject);
        }
    }

    public static boolean canWalkTo(AStarData start, AStarData target) {
        return canWalkTo(start, target, true);
    }

    public static double total = 0;
    public static int timesCalled = 0;

    public static boolean canWalkTo(AStarData start, AStarData target, boolean checkObject) {
        if (start == null || target == null) {
            return false;
        }
        int dX = start.getX() - target.getX();
        int dY = start.getY() - target.getY();
        if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
            return false;
        }
        boolean canWalkTo = canWalkToSub(start, target, checkObject);
        return canWalkTo;
    }

    private static boolean canWalkToSub(AStarData start, AStarData target, boolean checkObject) {
        DebugTimer t = new DebugTimer("Can Walk To", 500);
        timesCalled++;
        int startFlag = start.getCollisionData();
        int targetFlag = target.getCollisionData();
        boolean northS = start.blockedN;
        boolean eastS = start.blockedE;
        boolean southS = start.blockedS;
        boolean westS = start.blockedW;
        boolean northT = target.blockedN;
        boolean eastT = target.blockedE;
        boolean southT = target.blockedS;
        boolean westT = target.blockedW;
        //Check NORTHWEST
        if (start.getX() - 1 == target.getX() && start.getY() + 1 == target.getY()) {
            if (check(northS, westS, checkObject) || check(southT, eastT, checkObject)) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_NORTH)) || (isFlagOnTile(targetFlag, Flags.WALL_SOUTH))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_WEST)) || (isFlagOnTile(targetFlag, Flags.WALL_EAST))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check NORTH
        if (start.getY() + 1 == target.getY()) {
            if (!start.containsDoor() && !target.containsDoor() && (isFlagOnTile(startFlag, Flags.WALL_NORTH) || isFlagOnTile(targetFlag, Flags.WALL_SOUTH))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check NORTHEAST
        if (start.getX() + 1 == target.getX() && start.getY() + 1 == target.getY()) {
            if (check(northS, eastS, checkObject) || check(southT, westT, checkObject)) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_NORTH)) || (isFlagOnTile(targetFlag, Flags.WALL_SOUTH))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_EAST)) || (isFlagOnTile(targetFlag, Flags.WALL_WEST))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check EAST
        if (start.getX() + 1 == target.getX()) {
            if (!start.containsDoor() && !target.containsDoor() && (isFlagOnTile(startFlag, Flags.WALL_EAST) || isFlagOnTile(targetFlag, Flags.WALL_WEST))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check SOUTHEAST
        if (start.getX() + 1 == target.getX() && start.getY() - 1 == target.getY()) {
            if (check(southS, eastS, checkObject) || check(northT, westT, checkObject)) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_SOUTH)) || (isFlagOnTile(targetFlag, Flags.WALL_NORTH))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_EAST)) || (isFlagOnTile(targetFlag, Flags.WALL_WEST))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check SOUTH
        if (start.getY() - 1 == target.getY()) {
            if (!start.containsDoor() && !target.containsDoor() && (isFlagOnTile(startFlag, Flags.WALL_SOUTH) || isFlagOnTile(targetFlag, Flags.WALL_NORTH))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check SOUTHWEST
        if (start.getX() - 1 == target.getX() && start.getY() - 1 == target.getY()) {
            if (check(southS, westS, checkObject) || check(northT, eastT, checkObject)) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_SOUTH)) || (isFlagOnTile(targetFlag, Flags.WALL_NORTH))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
            if ((isFlagOnTile(startFlag, Flags.WALL_WEST)) || (isFlagOnTile(targetFlag, Flags.WALL_EAST))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        //Check WEST
        if (start.getX() - 1 == target.getX()) {
            if (!start.containsDoor() && !target.containsDoor() && (isFlagOnTile(startFlag, Flags.WALL_WEST) || isFlagOnTile(targetFlag, Flags.WALL_EAST))) {
                total += t.getElapsed();
                t.print();
                return false;
            }
        }
        total += t.getElapsed();
        t.print();
        return true;
    }

    private static boolean check(boolean blocked1, boolean blocked2, boolean checkObject) {
        if (checkObject) {
            return blocked1 || blocked2;
        } else {
            return blocked1 && blocked2;
        }
    }

    public static boolean isStandardBlocked(AStarData data) {
        if (data != null) {
            final int flag = data.getCollisionData();
            return data.isBlocked()
                    || isFlagOnTile(flag, Flags.DECORATION_BLOCK)
                    || isFlagOnTile(flag, Flags.OBJECT_BLOCK)
                    || isFlagOnTile(flag, Flags.BLOCKED)
                    || isFlagOnTile(flag, Flags.OBJECT_TILE);
        } else {
            return false;
        }
    }

    public static Positionable getNonBlocked(AStarData data) {
        AStarData leastBlocked = null;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (Math.abs(i) != Math.abs(j)) {
                    AStarData tempData = getData(data.toRSTile().translate(i, j));
                    if ((!isStandardBlocked(tempData)) && canWalkTo(data, tempData)) {
                        leastBlocked = tempData;
                    }
                }
            }
        }
        return leastBlocked != null ? leastBlocked.toRSTile() : data.toRSTile();
    }

    public static void save() {
        Printing.status("Synching map cache.");
        load();
        Printing.status("Map cache synched. Saving map cache.");
        int savedCount = 0;
        for (int i = 0; i < 3; i++) {
            savedCount += cache[i].save(i);
        }
        String text = "";
        text += topRight.x + "\n";
        text += topRight.y + "\n";
        text += bottomLeft.x + "\n";
        text += bottomLeft.y + "\n";
        FileUtil.writeTextFileContents(text, "m_B", "cache", true, "Walking");
        text = "";
        for (RSTile tile : banks) {
            text += tile.getX() + ":";
            text += tile.getY() + ":";
            text += tile.getPlane() + "\n";
        }
        FileUtil.writeTextFileContents(text, "banks", "cache", true, "Walking");
        cache[0].clear();
        cache[1].clear();
        cache[2].clear();
        cache[0] = null;
        cache[1] = null;
        cache[2] = null;
        System.gc();
        Printing.status("The map cache has been saved (" + savedCount + ").");
    }

    public static void load() {
        Printing.status(hasLoaded ? "Map cache already loaded. Synching map cache." : "Loading map cache...");
        for (int i = 0; i < 3; i++) {
            cache[i].load(i);
        }
        FileUtil.TextTraversable traversable = new FileUtil.TextTraversable() {
            @Override
            public void traverseNext(String next, int lineNumber) {
                switch (lineNumber) {
                    case 1:
                        topRight.x = Integer.parseInt(next);
                        break;
                    case 2:
                        topRight.y = Integer.parseInt(next);
                        break;
                    case 3:
                        bottomLeft.x = Integer.parseInt(next);
                        break;
                    case 4:
                        bottomLeft.y = Integer.parseInt(next);
                        break;
                }
            }
        };
        FileUtil.traverseTextFile(traversable, "m_B", "cache", "Walking");
        traversable = new FileUtil.TextTraversable() {
            @Override
            public void traverseNext(String next, int lineNumber) {
                if (!next.isEmpty()) {
                    String[] keys = next.split(":");
                    banks.add(new RSTile(parseInt(keys, 0), parseInt(keys, 1), parseInt(keys, 2)));
                }
            }
        };
        FileUtil.traverseTextFile(traversable, "banks", "cache", "Walking");
        Printing.status("Map cache loaded.");
        hasLoaded = true;
    }

    private static int parseInt(String[] nexts, int index) {
        return nexts != null && nexts.length != 0 ? Integer.parseInt(nexts[index]) : -1;
    }
}
