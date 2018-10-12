package scripts.starfox.api2007.walking;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Game07;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.banking.Bank07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.Objects07;
import scripts.starfox.api2007.walking.pathfinding.AStarCache;
import scripts.starfox.manager.moveSet.ActionSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nolan
 */
public class Walking07 {

    private static ArrayList<RSTile> current_path;
    private static final Object path_lock;

    static {
        path_lock = new Object();
    }

    public static ArrayList<RSTile> getCurrentPath() {
        if (current_path == null) {
            return null;
        }
        synchronized (path_lock) {
            return (ArrayList<RSTile>) current_path.clone();
        }
    }

    /**
     * Walks to the nearest bank using a* to generate the path to the target.
     *
     * Does not use presets or WebWalking backups like {@link Bank07#walkTo()} does.
     *
     *
     * @return True if the walking was successful, false otherwise.
     * @see Bank07#walkTo()
     */
    public static boolean aStarWalkToBank07() {
        return aStarWalk(Bank07.getNearest());
    }

    /**
     * Walks to the nearest object with the specified names and/or options using a* to generate the path to the target.
     *
     * If either names or options is null, the
     *
     *
     * @param names   The names of the objects.
     * @param options The options of the objects.
     * @return True if the walking was successful, false otherwise.
     * @see Objects07#getObject(String[], String[], int)
     */
    public static boolean aStarWalkToObject(final String[] names, final String[] options) {
        return aStarWalk(Objects07.getObject(names, options, 104));
    }

    /**
     * Walks to the specified target using a* to generate the path to the target.
     *
     * This method will return true when the following are true:
     * <ol>
     * <li>The target is within 5 tiles of the player.</li>
     * <li>The target's center is on the screen.</li>
     * <li>If the target's center cannot be turned to, the target must be on the screen.</li>
     * <li>If the target is reachable.</li>
     * </ol>
     *
     * This means that any precise movements should NOT use this method, or you should use {@link #aStarWalk(Positionable, Condition)} instead and provide your own condition.
     *
     * @param target The target.
     * @return True if the walking was successful, false otherwise.
     */
    public static boolean aStarWalk(final Positionable target) {
        return aStarWalk(target, WalkingConditions.genericCondition(target));
    }

    /**
     * Walks to the specified target using a* to generate the path to the target.
     *
     * If the target is more than
     *
     *
     * @param target             The target.
     * @param stopping_condition The condition that, when true, will cause this method to stop execution and return true.
     * @return True if the walking was successful, false otherwise.
     */
    public static boolean aStarWalk(final Positionable target, final Condition stopping_condition) {
        if (target == null) {
            return false;
        }
        if (stopping_condition == null) {
            return aStarWalk(target);
        }
        Timer t = new Timer(20000);
        AStarCache.update();
        TilePath tilePath = new TilePath(target.getPosition());
        t.start();
        int i = 0;
        while (!t.timedOut()) {
            i = i < 5 ? i += 1 : 0;
            if (stopping_condition.active() || comparePlayerPosition(target.getPosition())) {
                synchronized (path_lock) {
                    current_path = null;
                }
                return true;
            }
            if (Entities.distanceTo(tilePath.getEnd()) <= 1.5) {
                current_path = null;
                return false;
            }
            if (Player.isMoving()) {
                t.reset();
            }
            if (Game07.isGameLoaded()) {
                AntiBan.activateRun();
                if (i == 5 && AStarCache.update()) {
                    current_path = null;
                    return false;
                }
                tilePath = TilePath.update(target.getPosition(), tilePath);
//                tilePath = new TilePath(AStarCachefinder.getTilePath(target.getPosition()));
                synchronized (path_lock) {
                    current_path = tilePath.getPathList();
                }
                final RSObject obstacle = Obstacles.getNextObstacle(tilePath.getPath());
                final TilePath finalPath = tilePath;
                if (obstacle != null) {
                    if (Entities.isCenterOnScreen(obstacle)) {
                        if (Clicking.click(ArrayUtil.toArrayString(Obstacles.commands), obstacle) && Timing.waitCrosshair(50) == 2) {
                            Waiting.waitMoveCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    return !Objects.isAt(obstacle, obstacle.getID()) || Obstacles.isPastObstacle(finalPath.getPath(), obstacle);
                                }
                            }, 2500);
                        }
                    } else {
                        TilePath obstaclePath = new TilePath(obstacle.getPosition());
                        obstaclePath.walkToNext(WalkingConditions.genericCondition(obstacle));
                    }
                } else {
                    tilePath.walkToNext(stopping_condition);
                }
                Client.sleep(50);
            }
        }
        current_path = null;
        return false;
    }

    /**
     * Walks the specified path using a* to generate the path to each target.
     *
     * The tiles in the path should be reasonably spaced apart, but it is not a requirement. After each step in the path (with the exception of the last one), any nearby obstacles
     * will be handled, if necessary.
     *
     *
     * @param stopping_condition The condition that, when true, will cause this method to stop execution and return true.
     * @param path               The path that is being walked.
     * @return True if the walking was successful, false otherwise.
     * @see #aStarWalk(Positionable, Condition)
     */
    public static boolean aStarWalkPath(Condition stopping_condition, RSTile... path) {
        path = getRefinedPath(path);
        final boolean isNull = stopping_condition == null;
        for (int i = 0; i < path.length; i++) {
            final RSTile tile = path[i];
            final RSTile target = path[path.length - 1];
            final RSTile nextTile = !tile.equals(target) ? path[i + 1] : null;
            if (isNull) {
                stopping_condition = WalkingConditions.genericCondition(tile);
            }
            if (!aStarWalk(tile, stopping_condition) || (nextTile != null && !ObstacleHandler.handleTeleport(tile, nextTile, tile))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Walks the specified path using a* to generate the path to each target.
     *
     * The tiles in the path should be reasonably spaced apart, but it is not a requirement. After each step in the path (with the exception of the last one), any nearby obstacles
     * will be handled, if necessary.
     *
     *
     * @param path The path that is being walked.
     * @return True if the walking was successful, false otherwise.
     * @see #aStarWalk(Positionable)
     */
    public static boolean aStarWalkPath(RSTile... path) {
        return aStarWalkPath(null, path);
    }

    /**
     * Walks the specified path using a* to generate the path to each target.
     *
     * The tiles in the path should be reasonably spaced apart, but it is not a requirement. After each step in the path (with the exception of the last one), any nearby obstacles
     * will be handled, if necessary.
     *
     *
     * @param path The path that is being walked.
     * @return True if the walking was successful, false otherwise.
     * @see #aStarWalkPath(RSTile...)
     * @see #aStarWalk(Positionable)
     */
    public static boolean aStarWalkPath(ArrayList<RSTile> path) {
        return aStarWalkPath(path.toArray(new RSTile[0]));
    }

    /**
     * Walks the specified path using a* to generate the path to each target.
     *
     * The tiles in the path should be reasonably spaced apart, but it is not a requirement. After each step in the path (with the exception of the last one), any nearby obstacles
     * will be handled, if necessary.
     *
     *
     * @param stopping_condition The condition that, when true, will cause this method to stop execution and return true.
     * @param path               The path that is being walked.
     * @return True if the walking was successful, false otherwise.
     * @see #aStarWalkPath(Condition, RSTile...)
     * @see #aStarWalk(Positionable, Condition)
     */
    public static boolean aStarWalkPath(final Condition stopping_condition, ArrayList<RSTile> path) {
        return aStarWalkPath(stopping_condition, path.toArray(new RSTile[0]));
    }

    /**
     * Walks the specified preset movement path.
     *
     * Currently only used in the bot farm manager. Going to be removed once walking is upgraded.
     *
     *
     * @param target  The target.
     * @param presets The presets.
     * @return True if the preset was walked correctly, false otherwise.
     */
    public static boolean walkPresetMovement(Positionable target, MovementPreset... presets) {
        if (target == null || presets == null) {
            return false;
        }
        Client.println("Executing Presets");
        Positionable source = Player07.getPosition();
        MovementPreset getOutMovement = null;
        MovementPreset getToMovement = null;
        for (MovementPreset preset : presets) {
            if (preset.isGetOut() && preset.contains(source) && !preset.contains(target)) {
                getOutMovement = preset;
            }
            if (!preset.isGetOut() && preset.contains(target) && !preset.contains(source)) {
                getToMovement = preset;
            }
            if (getOutMovement != null && getToMovement != null) {
                break;
            }
        }
        boolean failed = false;
        if (getOutMovement != null && !getOutMovement.execute()) {
            Client.println("Get out execution failed.");
            failed = true;
        }
        if (getToMovement != null && !getToMovement.execute()) {
            Client.println("Get to execution failed.");
            failed = true;
        }
        return !failed;
    }

    /**
     * Returns the rest of the path relative to where the player is currently standing.
     *
     *
     * @param originPath The original path.
     * @return The rest of the path relative to where the player is currently standing.
     */
    private static RSTile[] getRefinedPath(RSTile[] originPath) {
        Client.println("Getting refined path...");
        final RSTile[] withinRange = Entities.getWithinRange(originPath, 104);
        originPath = withinRange.length == 0 ? originPath : withinRange;
        List<RSTile> allTiles = Arrays.asList(originPath);
        RSTile closestTile = null;
        ArrayList<RSTile> closeTiles = new ArrayList<>();
        Client.println("Running loop...");
        for (int i = 0; i < originPath.length; i++) {
            Client.println("Running loop " + i);
            final RSTile tile = originPath[i];
            final RSTile target = originPath[originPath.length - 1];
            final RSTile previousTile = i != 0 ? originPath[i - 1] : null;
            if (closestTile == null
                    || (ArrayUtil.contains(tile, withinRange) && Entities.aStarDistanceTo(closestTile) > Entities.aStarDistanceTo(tile))) {
//            if (closestTile == null
//                    || (previousTile != null && !(new TilePath(AStarPathfinder.getTilePath(previousTile)).isNear()))
//                    || Entities.aStarDistanceTo(closestTile) > Entities.aStarDistanceTo(tile)) {
                closestTile = tile;
                closeTiles.clear();
            }
            closeTiles.add(tile);
        }
        return closeTiles.toArray(new RSTile[closeTiles.size()]);
    }

    /**
     * Walks in a straight line towards the destination.
     *
     *
     * @param tile The destination.
     * @return True if we reached the destination, false otherwise.
     */
    public static boolean straightWalk(final RSTile tile) {
        if (tile == null) {
            return false;
        }
        if (compareGameDestination(tile) || comparePlayerPosition(tile)) {
            return true;
        }
        return Walking.blindWalkTo(tile, new Condition() {
            @Override
            public boolean active() {
                AntiBan.activateRun();
                return compareGameDestination(tile);
            }
        }, 500);
    }

    /**
     * Walks in a straight line towards the destination.
     *
     *
     * @param tile      The destination.
     * @param condition The stopping condition.
     * @return True if we reached the destination, false otherwise.
     */
    public static boolean straightWalk(final RSTile tile, final Condition condition) {
        if (tile == null) {
            return false;
        }
        if (compareGameDestination(tile) || comparePlayerPosition(tile)) {
            return true;
        }
        return Walking.blindWalkTo(tile, new Condition() {
            @Override
            public boolean active() {
                Client.sleep(100);
                AntiBan.activateRun();
                if (condition != null && condition.active()) {
                    return true;
                }
                return compareGameDestination(tile);
            }
        }, 500);
    }

    /**
     * Attempts to screen-walk to a random reachable nearby tile.
     *
     * @return True if the walking was successful, false otherwise.
     */
    public static boolean walkNearbyTile() {
        final RSTile pos = Player07.getPosition();
        if (pos != null) {
            RSTile tile = null;
            while (tile == null || !Map07.isStandardBlocked(tile)) {
                tile = pos.translate(General.random(-5, 5), General.random(-5, 5));
            }
            return Walking.clickTileMS(tile, "Walk here");
        }
        return false;
    }

    /**
     * Attempts to walk directly to a target tile using the screen.
     *
     * This method will attempt to reach the EXACT tile that you pass in for 7.5 seconds. If the tile that is passed in is not pathable, this method returns false.
     *
     * @param target The target tile.
     * @return True if the target was reached, false otherwise.
     */
    public static boolean walkDirect(RSTile target) {
        RSTile destination = Game.getDestination();
        if (destination != null && destination.equals(target)) {
            return true;
        }
        final RSTile pos = Player07.getPosition();
        if (target != null && pos != null) {
            if (Map07.isStandardBlocked(target)) {
                return false;
            }
            if (!target.isOnScreen()) {
                Camera.turnToTile(target);
                Client.sleep(150, 300);
            }
            Timer timer = new Timer(7500);
            timer.start();
            while (!timer.timedOut() && !Player07.getPosition().equals(target)) {
                Client.sleep(25, 200);
                AntiBan.moveCamera();
                if (!Player.isMoving()) {
                    Walking.clickTileMS(target, "Walk here");
                }
            }
        }
        return false;
    }

    /**
     * Walks to the destination via web walking.
     *
     *
     * @param tile The destination.
     * @return True if we reached the destination, false otherwise.
     */
    public static boolean webWalk(final RSTile tile) {
        if (tile == null) {
            return false;
        }
        return webWalk(tile, null, 500);
    }

    /**
     * Walks to the destination via web walking.
     *
     * @param tile       The destination.
     * @param condition  The stopping condition.
     * @param check_time The time between checks of the stopping condition.
     * @return True if we reached the destination, false otherwise.
     */
    public static boolean webWalk(RSTile tile, Condition condition, long check_time) {
        if (tile == null) {
            return false;
        }
        return WebWalking.walkTo(tile, new Condition() {
            @Override
            public boolean active() {
                AntiBan.activateRun();
                boolean bool = true;
                if (condition != null) {
                    bool = condition.active();
                }
                return bool || compareGameDestination(tile);
            }
        }, check_time);
    }

    /**
     * Walks to the nearest bank via {@link Bank07#walkTo()}.
     *
     *
     * @return True if we reached a bank, false otherwise.
     * @see Bank07#walkTo()
     */
    public static boolean walkToBank07() {
        return Bank07.walkTo();
    }

    /**
     * Compares the game destination to the specified tile to see if they are related.
     *
     *
     * @param tile The tile to compare.
     * @return True if the game destination and the specified tile are related, false otherwise.
     */
    public static boolean compareGameDestination(RSTile tile) {
        RSTile game_destination = Game.getDestination();
        if (tile == null || game_destination == null) {
            return false;
        }
        return tile.distanceTo(game_destination) <= 2;
    }

    /**
     * Compares the player position to the specified tile to see if they are related.
     *
     *
     * @param tile The tile to compare.
     * @return True if the game destination and the specified tile are related, false otherwise.
     */
    private static boolean comparePlayerPosition(RSTile tile) {
        RSTile player_position = Player07.getPosition();
        if (tile == null || player_position == null) {
            return false;
        }
        return tile.equals(player_position);
    }

    /**
     * Executes the specified of ActionSets in order.
     *
     *
     * @param actionSets The ActionSets.
     * @return True if the ActionSets were successfully executed, false otherwise.
     */
    public static boolean executeActionSets(ActionSet... actionSets) {
        for (ActionSet actionSet : actionSets) {
            if (!actionSet.execute()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes the specified of ActionSets in order.
     *
     *
     * @param actionSets The ActionSets.
     * @return True if the ActionSets were successfully executed, false otherwise.
     */
    public static boolean executeActionSets(List<ActionSet> actionSets) {
        return executeActionSets(actionSets.toArray(new ActionSet[actionSets.size()]));
    }
}
