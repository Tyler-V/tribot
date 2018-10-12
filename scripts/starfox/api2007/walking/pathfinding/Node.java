package scripts.starfox.api2007.walking.pathfinding;

import java.util.ArrayList;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.util.DebugTimer;

/**
 * @author TacoManStan
 */
public class Node {

    private static AStarPathfinder host;

    private Node parentStart;
    private Node parentTarget;
    private ArrayList<Node> neighbors;
    private final int x, y, plane;
    private int gCostStart;
    private int gCostTarget;
    private final AStarData cacheData;
    private boolean loadedNeighbors;
    private boolean isLoaded;

    protected Node(int x, int y, int plane) {
        this(new RSTile(x, y, plane));
    }

    protected Node(RSTile tile) {
        parentStart = null;
        parentTarget = null;
        neighbors = new ArrayList<>();
        this.x = tile.getX();
        this.y = tile.getY();
        this.plane = tile.getPlane();
        AStarData data = AStarCache.getData(tile);
        this.cacheData = data != null ? data : new AStarData(getX(), getY(), getPlane(), 0, false, false);
        gCostStart = Integer.MAX_VALUE;
        gCostTarget = Integer.MAX_VALUE;
        loadedNeighbors = false;
        this.neighborLock = new Object();
//        isLoaded = AStarCache.isLoaded(tile);
    }

    public final Node getParent(boolean start) {
        return start ? parentStart : parentTarget;
    }

    public final void setParent(Node parent, boolean start) {
        if (start) {
            this.parentStart = parent;
        } else {
            this.parentTarget = parent;
        }
    }

    public static int timesCalled = 0;

    public ArrayList<Node> getNeighbors() {
        timesCalled++;
        long startTime;
        long endTime;
        startTime = System.nanoTime();
        if (!loadedNeighbors && neighbors.isEmpty()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        addCacheNeighbor(i, j);
                    }
                }
            }
        }
        endTime = System.nanoTime() - startTime;
        total += (endTime / 1000000d);
        return neighbors;
    }

    private final Object neighborLock;

    public final Node addNeighbor(Node neighbor) {
        synchronized (neighborLock) {
            if (neighbors.contains(neighbor)) {
                return null;
            }
        }
        if (neighbor.canWalkTo(this)) {
            synchronized (neighborLock) {
                neighbors.add(neighbor);
            }
            return neighbor;
        } else {
            return null;
        }
    }

    private Node addCacheNeighbor(int xOffset, int yOffset) {
        DebugTimer t = new DebugTimer("Add Cache Neighbor", 500);
        RSTile tempTile = toRSTile();
        tempTile = tempTile != null ? tempTile.translate(xOffset, yOffset) : null;
        t.print();
        return addNeighbor(host.createNode(tempTile));
//        return !AStarCache.isGlobalLoaded(tempTile) ? null : addNeighbor(host.createNode(tempTile));
    }

    public static double total = 0;

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getPlane() {
        return plane;
    }

    public int getGCost(boolean start) {
        return start ? gCostStart : gCostTarget;
    }

    public void updateGCost(boolean start) {
        if (start) {
            this.gCostStart = calcG(start);
        } else {
            this.gCostTarget = calcG(start);
        }
    }

    public int calcG(boolean start) {
        Node current = this;
        int value = 0;
        do {
            value += current.getCost(start);
        } while ((current = current.getParent(start)).getParent(start) != null);
        return value;
    }

    public int calcH(final Node target, boolean start) {
        return calcH(target, false, start);
    }

    public int calcH(final Node target, final boolean weighted, final boolean start) {
        int xTemp = Math.abs(getX() - target.getX());
        int yTemp = Math.abs(getY() - target.getY());
        int h;
        if (xTemp >= yTemp) {
            h = (yTemp * 14) + ((xTemp - yTemp) * 10);
        } else {
            h = (xTemp * 14) + ((yTemp - xTemp) * 10);
        }
        if (weighted) {
            if (Math.abs(target.x - getParent(start).x) >= Math.abs(target.y - getParent(start).y)) {
                if ((target.x - x > 0) == (getParent(start).x - x >= 0)) {
                    h -= 4;
                }
            }
            if (Math.abs(target.y - getParent(start).y) >= Math.abs(target.x - getParent(start).x)) {
                if ((target.y - y > 0) == (getParent(start).y - y >= 0)) {
                    h -= 4;
                }
            }
        }
        return h;
    }

    private boolean parentIsDiag(boolean start) {
        return getParent(start).getX() != x && getParent(start).getY() != y;
    }

    public final int getCost(boolean start) {
        int value = parentIsDiag(start) ? 14 : 10;
//        if (!isLoaded) {
//            value *= 2;
//        }
        return value;
    }

    public RSTile toRSTile() {
        return new RSTile(x, y, plane);
    }

    public static Node sFromRSTile(RSTile tile) {
        return new Node(tile.getX(), tile.getY(), tile.getPlane());
    }

    public final boolean canWalkTo(Node source) {
        boolean canWalkTo = (!isStandardBlocked() || containsObstacle()) && subCanWalkTo(source);
        return canWalkTo;
    }

    private AStarData getCacheData() {
        return cacheData;
    }

    protected static void setHost(AStarPathfinder host) {
        Node.host = host;
    }

    protected boolean isStandardBlocked() {
        return cacheData != null && cacheData.isBlocked();
    }

    protected boolean containsObstacle() {
        return cacheData != null && cacheData.containsObstacle();
    }

    protected boolean subCanWalkTo(Node source) {
        return AStarCache.canWalkTo(cacheData, source.getCacheData());
    }

    public Node fromRSTile(RSTile tile) {
        return host.createNode(tile);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.y;
        hash = 59 * hash + this.plane;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return this.plane == other.plane;
    }
}
