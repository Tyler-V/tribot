package scripts.starfox.api2007.walking.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.Client;
import scripts.starfox.api.Printing;
import scripts.starfox.api.util.Calculations;
import scripts.starfox.api.util.DebugTimer;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.entities.Entities;

/**
 * SigmaAStar is a class that is used to generate paths using the A* pathfinding algorithm with a heuristic suited for Old School RuneScape.
 *
 * @author TacoManStan
 */
public class AStarPathfinder {

    private static final Object lock;
    private static AStarPathfinder staticFinder;

    static {
        staticFinder = new AStarPathfinder();
        lock = new Object();
    }

    /**
     * Allows the use of pathfinding in a static context. In all contexts where multithreading is not being used, this is recommended.
     *
     * @return An AStarPathfinder instance that can be used via a static context.
     */
    public static AStarPathfinder get() {
        synchronized (lock) {
            return staticFinder;
        }
    }

    /**
     * Allows the use of pathfinding in a static context. In all contexts where multithreading is not being used, this is recommended.
     *
     * @param weight The weight of the path. A higher weight means a faster path, but a less accurate path.
     * @return An AStarPathfinder instance that can be used via a static context.
     */
    public static AStarPathfinder get(int weight) {
        synchronized (lock) {
            staticFinder.setWeight(weight);
            return staticFinder;
        }
    }

    private final Object instance_lock;

    /**
     * The map of nodes that are currently being used for this AStarPathfinder.
     */
    private final HashMap<RSTile, Node> node_map;

    /**
     * The "visited tile" weight of this AStarPathfinder. A higher weight means a faster but less accurate path. 1 is recommended and default.
     */
    private int weight;

    /**
     * The timeout of this AStarPathfinder. This value is automatically set in the {@link #getPath(Positionable, Positionable)} method.
     */
    private long timeout;

    private long maxTimeout;

    /**
     * Constructs a new AStarPathfinder with the default weight.
     */
    public AStarPathfinder() {
        this(1);
    }

    /**
     * Constructs a new AStarPathfinder with the specified weight.
     *
     * @param weight The "visited tile" weight of the path. A higher weight means a faster but less accurate path. 1 is recommended.
     */
    public AStarPathfinder(int weight) {
        this.node_map = new HashMap<>();
        this.weight = weight;
        this.instance_lock = new Object();
        this.maxTimeout = 45000;
    }

    /**
     * Sets the "visited tile" weight of the path. A higher weight means a faster but less accurate path.
     *
     * 1 (default) is recommended in most scenarios. However, sometimes when large portions of the cache have yet to load, using a fast path to initialize the walking is the best
     * course of action.
     *
     * @param weight The "visited tile" weight of the path. A higher weight means a faster but less accurate path. 1 is recommended.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Sets the maximum amount of time that this {@code AStarPathfinder} is allowed to take generating a path before returning. The default is 45 seconds. -1 indicates no timeout,
     * and is never recommended for anything other than testing purposes.
     *
     * This is the <i>maximum</i> timeout. The regular timeout is calculated by the expected length of the path.
     *
     * This method is synchronized. If this AStarPathfinder is already generating a path or being used in any way, this method will block until the previous actions have been
     * completed.
     *
     * @param timeout The timeout.
     */
    public void setMaxTimeout(int timeout) {
        synchronized (instance_lock) {
            this.maxTimeout = timeout;
        }
    }

    /**
     * Generates a path to the specified tile from the local player's position.
     *
     * @param targetTile The target.
     * @see #getPath(Positionable, Positionable)
     * @return The path generated.
     */
    public ArrayList<RSTile> getPath(final Positionable targetTile) {
        return getPath(Player07.getPosition(), targetTile);
    }

    /**
     * Generates a path to the specified target from the specified start tile.
     *
     * This method takes a long time to return and will block in the process, so it is not advised to call it in a multi-threaded context <i>on the same {@code AStarPathfinder}
     * object</i>.
     *
     * Note the following:
     * <ol>
     * <li>If you are generating a path to a blocked tile, this method will automatically determine the closest reachable tile and generate to it instead of the target.</li>
     * <li>Any tiles (including the start and target) that have not been loaded into the cache are treated as walkable during generation.</li>
     * <li>The maximum time that can be spent generating is determined by {@code Entities.distanceTo(startTile, targetTile) * 750}. However, if that value exceeds the maximum
     * timeout, the maximum timeout is used instead. (refer to {@link #setMaxTimeout(int)}).</li>
     * <li>If the start or target tile is null, this method will return null.</li>
     * <li>If desired, enabling debug messages in the {@link Printing} class
     *
     * @param startTile  The start tile.
     * @param targetTile The target.
     * @see #getPath(Positionable)
     * @return The path generated.
     */
    public ArrayList<RSTile> getPath(final Positionable startTile, Positionable targetTile) {
        synchronized (instance_lock) {
            node_map.clear();
            timeout = (long) (Entities.distanceTo(startTile, targetTile) * 750);
            if (timeout > 45000) {
                timeout = maxTimeout;
            }
            node_map.clear();
            Node.total = 0;
            Node.timesCalled = 0;
            AStarCache.total = 0;
            AStarCache.timesCalled = 0;
            Node closestNodeStart = null;
            Node closestNodeTarget = null;
            if (startTile == null) {
                Client.println("Start Tile == null (1)");
                return null;
            }
            if (targetTile == null) {
                Client.println("Target Tile == null (1)");
                return null;
            }
            final AStarData tempTargetData = AStarCache.getData(targetTile.getPosition());
            if (tempTargetData != null && tempTargetData.isBlocked()) {
                targetTile = AStarCache.getNonBlocked(tempTargetData);
            }
            Node.setHost(this);
            Node start = createNode(startTile.getPosition());
            Node target = createNode(targetTile.getPosition());
            if (start == null) {
                Client.println("Start Node == null (2)");
                return null;
            }
            if (target == null) {
                Client.println("Target Node == null (2)");
                return null;
            }
            ArrayList<Node> openListStart = new ArrayList<>();
            ArrayList<Node> openListTarget = new ArrayList<>();
            ArrayList<Node> closedListStart = new ArrayList<>();
            ArrayList<Node> closedListTarget = new ArrayList<>();
            Node currentNodeStart = start;
            Node currentNodeTarget = target;
            openListStart.add(start);
            openListTarget.add(target);
            int iterations = 0;
            Timer t = new Timer(timeout);
            t.start();
            int i = 0;
            boolean checkingTarget = true;
            boolean pathsMet = false;
            DebugTimer dT = new DebugTimer("Path Generation", 2500);
            while ((maxTimeout == -1 || !t.timedOut()) && !pathsMet && (!openListStart.isEmpty() || !openListTarget.isEmpty())) {
                iterations++;
                checkingTarget = !checkingTarget;
                if (!openListStart.isEmpty() && !checkingTarget) {
                    currentNodeStart = getBestNode(openListStart, target, currentNodeTarget, true);
                    if (isDistance(currentNodeStart, closestNodeStart, target)) {
                        closestNodeStart = currentNodeStart;
                    }
                    openListStart.remove(currentNodeStart);
                    closedListStart.add(currentNodeStart);
                    for (Node neighbor : currentNodeStart.getNeighbors()) {
                        i++;
                        if (!closedListTarget.contains(neighbor)) {
                            if (!closedListStart.contains(neighbor)) {
                                Node oldParent = neighbor.getParent(true);
                                neighbor.setParent(currentNodeStart, true);
                                if (!openListStart.contains(neighbor)) {
                                    neighbor.updateGCost(true);
                                    openListStart.add(neighbor);
                                } else {
                                    if (neighbor.calcG(true) < neighbor.getGCost(true)) {
                                        neighbor.updateGCost(true);
                                    } else {
                                        neighbor.setParent(oldParent, true);
                                    }
                                }
                            }
                        } else {
                            currentNodeTarget = neighbor;
                            pathsMet = true;
                            break;
                        }
                    }
                } else if (!openListTarget.isEmpty() && checkingTarget) {
                    currentNodeTarget = getBestNode(openListTarget, start, currentNodeStart, false);
                    if (isDistance(currentNodeTarget, closestNodeTarget, start)) {
                        closestNodeTarget = currentNodeTarget;
                    }
                    openListTarget.remove(currentNodeTarget);
                    closedListTarget.add(currentNodeTarget);
                    for (Node neighbor : currentNodeTarget.getNeighbors()) {
                        i++;
                        if (!closedListStart.contains(neighbor)) {
                            if (!closedListTarget.contains(neighbor)) {
                                Node oldParent = neighbor.getParent(false);
                                neighbor.setParent(currentNodeTarget, false);
                                if (!openListTarget.contains(neighbor)) {
                                    neighbor.updateGCost(false);
                                    openListTarget.add(neighbor);
                                } else {
                                    if (neighbor.calcG(false) < neighbor.getGCost(false)) {
                                        neighbor.updateGCost(false);
                                    } else {
                                        neighbor.setParent(oldParent, false);
                                    }
                                }
                            }
                        } else {
                            currentNodeStart = neighbor;
                            pathsMet = true;
                            break;
                        }
                    }
                }
            }
            dT.print();
//            Printing.dev("Times Looped: " + i);
//            Printing.dev("Can Walk Time: " + AStarCache.total);
//            Printing.dev("Times Called WT: " + AStarCache.timesCalled);
//            Printing.dev("Cost Per Call WT: " + AStarCache.total / AStarCache.timesCalled);
//            Printing.dev("Get Neighbor Time: " + Node.total);
//            Printing.dev("Times Called GN: " + Node.timesCalled);
//            Printing.dev("Cost Per Call GN: " + Node.total / Node.timesCalled);
            Node toAddNodeStart = !pathsMet ? closestNodeStart : currentNodeStart;
            if (toAddNodeStart == null) {
                return null;
            }
            Node toAddNodeTarget = !pathsMet ? closestNodeTarget : currentNodeTarget;
            if (toAddNodeTarget == null) {
                return null;
            }
            final ArrayList<RSTile> path = new ArrayList<>();
            if ((timeout == -1 || !t.timedOut()) && !openListTarget.isEmpty()) {
                path.add(toAddNodeTarget.toRSTile());
                while ((toAddNodeTarget = toAddNodeTarget.getParent(false)) != null) {
                    path.add(0, toAddNodeTarget.toRSTile());
                }
            }
            path.add(toAddNodeStart.toRSTile());
            while ((toAddNodeStart = toAddNodeStart.getParent(true)) != null) {
                path.add(toAddNodeStart.toRSTile());
            }
            return new ArrayList<>(Arrays.asList(Walking.invertPath(path.toArray(new RSTile[path.size()]))));
        }
    }

    /**
     * Generates a path to the target.
     *
     * If the target is not loaded, a path will be generated to the closest loaded tile.
     *
     * @param target The target.
     * @see #getPath(Positionable, Positionable)
     * @return The path.
     */
    public RSTile[] getTilePath(RSTile target) {
        List<RSTile> tiles = getPath(target);
        return tiles != null ? tiles.toArray(new RSTile[tiles.size()]) : null;
    }

    //<editor-fold defaultstate="collapsed" desc="Helper Methods">
    private boolean isDistance(Node node1, Node node2, Node target) {
        if (node1 == null) {
            return false;
        }
        if (node2 == null) {
            return true;
        }
        double distance1 = Calculations.distanceTo(node1.getX(), node1.getY(), target.getX(), target.getY());
        double distance2 = Calculations.distanceTo(node2.getX(), node2.getY(), target.getX(), target.getY());
        return distance1 < distance2;
    }

    private Node getBestNode(final ArrayList<Node> openSet, final Node target, final Node subTarget, final boolean start) {
        Node bestNode = null;
        ArrayList<Node> bestNodes = new ArrayList<>();
        for (final Node node : openSet) {
            if ((bestNode == null || getF(node, target, start) <= getF(bestNode, target, start))) {
                if (bestNode == null || getF(node, target, start) < getF(bestNode, target, start)) {
                    bestNodes.clear();
                }
                bestNode = node;
                bestNodes.add(node);
            }
        }
        bestNode = null;
        for (final Node node : bestNodes) {
            if ((bestNode == null || getF(node, subTarget, start) < getF(bestNode, subTarget, start))) {
                bestNode = node;
            }
        }
        return bestNode;
    }

    private int getF(Node current, Node target, boolean start) {
        final int g = current.getGCost(start);
        return g + (weight * current.calcH(target, true));
    }

    protected Node createNode(RSTile tile) {
        if (tile == null) {
            return null;
        } else {
            Node tempNode;
            if (node_map.containsKey(tile)) {
                tempNode = node_map.get(tile);
            } else {
                tempNode = new Node(tile);
                node_map.put(tile, tempNode);
            }
            return tempNode;
        }
    }
    //</editor-fold>
}
