package scripts.starfox.api2007.entities;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.waiting.Condition07;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.filters.Filters07;
import scripts.starfox.api2007.walking.Walking07;
import scripts.starfox.api2007.walking.WalkingConditions;
import scripts.starfox.api2007.walking.pathfinding.AStarPathfinder;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Entities class is used to get information about entities.
 *
 *
 * @author Nolan
 */
public class Entities {

    /**
     * Gets the area of the specified model.
     *
     *
     * @param model The model.
     * @return The area.
     */
    public static Polygon getModelArea(final RSModel model) {
        if (model == null) {
            return new Polygon();
        }
        return model.getEnclosedArea();
    }

    /**
     * Checks to see if the bot mouse is hovering inside the specified model.
     *
     *
     * @param model The model.
     * @return True if the bot mouse is hovering inside the model, false otherwise.
     */
    public static boolean isHovering(final RSModel model) {
        final Polygon area = getModelArea(model);
        return area != null && area.contains(Mouse.getPos());
    }

    /**
     * Returns an RSTile represented by the specified Point.
     *
     * @param p The Point that is specified.
     * @return An RSTile represented by the specified Point.
     */
    public static RSTile pointToRSTile(Point p) {
        return new RSTile(p.x, p.y);
    }

    /**
     * Returns a string representation of a Positionable.
     *
     * @param pos The Positionable that is being converted into a String.
     * @return A string representation of a Positionable.
     */
    public static String tileToString(final Positionable pos) {
        final RSTile tile = pos != null ? pos.getPosition() : null;
        return tile != null ? "[X: " + tile.getPosition().getX() + " | Y: " + tile.getPosition().getY() + " | P: " + tile.getPosition().getPlane() + "]" : "null";
    }

    /**
     * Returns all of the tiles included in the specified array within the specified range.
     *
     * @param tiles The array of tiles.
     * @param range The range.
     * @return All of the tiles included in the specified array within the specified range.
     */
    public static RSTile[] getWithinRange(RSTile[] tiles, int range) {
        ArrayList<RSTile> tempTiles = new ArrayList<>();
        for (RSTile tile : tiles) {
            if (isWithinRange(tile, range)) {
                tempTiles.add(tile);
            }
        }
        return tempTiles.toArray(new RSTile[tempTiles.size()]);
    }

    /**
     * Checks to see if the player is within range of the specified base Positionable.
     *
     * @param base  The base.
     * @param range The range.
     * @return True if the player is within range, false otherwise.
     */
    public static boolean isWithinRange(Positionable base, int range) {
        return !(Entities.distanceTo(base) > range + 1 && Entities.aStarDistanceTo(base) > range);
    }

    /**
     * Compares the distance between the player and the two specified Positionables using the distance formula. Returns true if the first Positionable is
     * closer, false if the
     * second Positionable is closer.
     *
     * @param pos1 The first Positionable.
     * @param pos2 The second Positionable.
     * @return True if the first Positionable is closer, false if the second Positionable is closer.
     */
    public static boolean compare(final Positionable pos1, final Positionable pos2) {
        return distanceTo(pos1) < distanceTo(pos2);
    }

    /**
     * Compares the distance between the player and the two specified Positionables using pathfinding. Returns true if the first Positionable is closer, false
     * if the second
     * Positionable is closer.
     *
     * @param pos1 The first Positionable.
     * @param pos2 The second Positionable.
     * @return True if the first Positionable is closer, false if the second Positionable is closer.
     */
    public static boolean aStarCompare(final Positionable pos1, final Positionable pos2) {
        return Entities.aStarDistanceTo(pos1) < Entities.aStarDistanceTo(pos2);
    }

    /**
     * Uses the specified RSItem on the specified Clickable. If the Clickable is also a Positionable, the Positionable
     * will be walked to.
     *
     * @param item      The RSItem.
     * @param clickable The Clickable.
     * @return True if the item was successfully used, false otherwise.
     */
    public static boolean useOn(RSItem item, Clickable clickable) {
        if (item == null || clickable == null) {
            return false;
        }
        if (clickable instanceof Positionable) {
            Positionable pos = (Positionable) clickable;
            if (aStarDistanceTo(pos) <= 5 && !isCenterOnScreen((Positionable) clickable)) {
                Camera.turnToTile(pos);
            } else {
                Walking07.aStarWalk(pos, WalkingConditions.genericCondition(pos));
                Camera.turnToTile(pos);
            }
            if (!Entities.isCenterOnScreen(pos)) {
                return false;
            }
        }
        boolean clicked = true;
        RSItemDefinition def = item.getDefinition();
        if (def == null) {
            return false;
        }
        if (!Game.isUptext(def.getName() + " ->")) {
            clicked = Clicking.click("Use", item);
        }
        if (clicked && Timing.waitUptext("->", 750)) {
            for (int i = 0; i < 5; i++) {
                AntiBan.sleep(General.random(0, 2));
                if (Clicking.click("->", clickable)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Interacts with the specified Positionable. Walks to the Positionable if the player is not already close enough
     * to it.
     *
     * @param positionable The Positionable to interact with.
     * @param option       The option to select.
     * @param path         The path to walk.
     * @param condition07  The stop at condition for walking.
     * @return True if the Positionable was interacted with successfully; false otherwise.
     */
    public static boolean interact(Positionable positionable, String option, RSTile[] path, Condition07 condition07) {
        if (isOnScreen(positionable) && isClickable(positionable)) {
            if (Clicking.click(Filters07.getFilter(option, ((Clickable) positionable)), (Clickable07) positionable)) {
                return Waiting.waitUntilMove(condition07, 3000);
            }
        } else {
            if (!Walking07.compareGameDestination(path[path.length - 1])) {
                return Walking.walkPath(Walking.randomizePath(path, 1, 1), new Condition() {
                    @Override
                    public boolean active() {
                        AntiBan.activateRun();
                        if (positionable != null) {
                            if (positionable instanceof RSObject) {
                                RSObject object = Objects07.getObject(((RSObject) positionable).getID(), 50);
                                return isOnScreen(object) && isClickable(object);
                            } else if (positionable instanceof RSNPC) {
                                RSNPC npc = NPCs07.getNPC(((RSNPC) positionable).getID());
                                return isOnScreen(npc) && isClickable(npc);
                            } else if (positionable instanceof RSTile || positionable instanceof RSGroundItem) {
                                return isOnScreen(positionable) && isClickable(positionable);
                            }
                        }
                        return Walking07.compareGameDestination(path[path.length - 1]);
                    }
                }, 250);
            }
            return true;
        }
        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Is On Screen Methods">

    /**
     * Checks to see if the specified positionable is on the screen.
     *
     * @param position The Positionable being checked.
     * @return True if the specified Positionable is on the screen, false otherwise.
     */
    public static boolean isOnScreen(Positionable position) {
        if (position == null) {
            return false;
        }
        if (position instanceof RSObject) {
            return ((RSObject) position).isOnScreen();
        } else if (position instanceof RSTile) {
            return ((RSTile) position).isOnScreen();
        } else if (position instanceof RSCharacter) {
            return ((RSCharacter) position).isOnScreen();
        } else if (position instanceof RSGroundItem) {
            return ((RSGroundItem) position).isOnScreen();
        }
        return false;
    }

    /**
     * Returns true if the specified Positionable is on the screen, false otherwise.
     *
     * In order for this method to return true, the center point of the Positionable's model must be on the screen. If the Positionable's model is null,
     * {@link #isOnScreen(Positionable)} is used.
     *
     * @param position The Positionable being checked.
     * @return True if the specified Positionable is on the screen, false otherwise.
     */
    public static boolean isCenterOnScreen(Positionable position) {
        if (position == null) {
            return false;
        }
        RSModel model;
        if (position instanceof RSObject) {
            model = ((RSObject) position).getModel();
        } else if (position instanceof RSCharacter) {
            model = ((RSCharacter) position).getModel();
        } else if (position instanceof RSGroundItem) {
            model = ((RSGroundItem) position).getModel();
        } else {
            model = null;
        }
        if (model != null) {
            Point p = model.getCentrePoint();
            return p != null && Projection.isInViewport(p);
        } else {
            return isOnScreen(position);
        }
    }

    /**
     * Checks if any of the specified Positionables are on the screen.
     *
     * @param positions The array of Positionables.
     * @return True if any of the specified Positionables are on the screen, false otherwise.
     * @see #isOnScreen(Positionable)
     */
    public static boolean isOnScreen(Positionable... positions) {
        if (positions == null || positions.length == 0) {
            return false;
        }
        for (Positionable position : positions) {
            if (position != null && isOnScreen(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the specified Positionables are on the screen.
     *
     * This method checks for the center point using {@link #isCenterOnScreen(Positionable)}.
     *
     * @param positions The array of Positionables.
     * @return True if any of the specified Positionables are on the screen, false otherwise.
     * @see #isCenterOnScreen(Positionable)
     */
    public static boolean isCenterOnScreen(Positionable... positions) {
        if (positions == null || positions.length == 0) {
            return false;
        }
        for (Positionable position : positions) {
            if (position != null && isCenterOnScreen(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified Positionable is clickable.
     *
     * @param positionable The Positionable being checked.
     * @return True if the specified Positionable is clickable, false otherwise.
     */
    public static boolean isClickable(Positionable positionable) {
        if (positionable != null) {
            if (positionable instanceof RSObject) {
                return ((RSObject) positionable).isClickable();
            } else if (positionable instanceof RSTile) {
                return ((RSTile) positionable).isClickable();
            } else if (positionable instanceof RSCharacter) {
                return ((RSCharacter) positionable).isClickable();
            } else if (positionable instanceof RSGroundItem) {
                return ((RSGroundItem) positionable).isClickable();
            }
            return false;
        }
        return false;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Nearest Methods">

    /**
     * Gets the positionable nearest to your player from the specified array of positionables using pathfinding.
     *
     *
     * @param positionables The positionables to search through.
     * @return The positionable nearest to your player.
     * @see #aStarDistanceTo(org.tribot.api.interfaces.Positionable)
     */
    public static Positionable getAStarNearest(Positionable... positionables) {
        return getAStarNearest(Player.getPosition(), positionables);
    }

    /**
     * Gets the positionable nearest to the specified position from the specified array of positionables using pathfinding.
     *
     *
     * @param position      The position to sort from.
     * @param positionables The positionables to search through.
     * @return The positionable nearest to the specified position.
     * @see #aStarDistanceTo(org.tribot.api.interfaces.Positionable)
     */
    public static Positionable getAStarNearest(Positionable position, Positionable... positionables) {
        Positionable nearest = null;
        for (Positionable positonable : positionables) {
            if (nearest == null || aStarDistanceTo(positonable, position) < aStarDistanceTo(nearest, position)) {
                nearest = positonable;
            }
        }
        return nearest;
    }

    /**
     * Gets the positionable nearest to your player from the specified array of positionables using the distance formula.
     *
     *
     * @param positionables The positionables to search through.
     * @return The positionable nearest to your player.
     * @see #distanceTo(org.tribot.api.interfaces.Positionable)
     */
    public static Positionable getNearest(Positionable... positionables) {
        return getNearest(Player.getPosition(), positionables);
    }

    /**
     * Gets the positionable nearest to the specified position from the specified array of positionables using the distance formula.
     *
     *
     * @param position      The position to sort from.
     * @param positionables The positionables to search through.
     * @return The positionable nearest to the specified position.
     * @see #distanceTo(org.tribot.api.interfaces.Positionable)
     */
    public static Positionable getNearest(Positionable position, Positionable... positionables) {
        Positionable nearest = null;
        for (Positionable positonable : positionables) {
            if (nearest == null || distanceTo(positonable, position) < distanceTo(nearest, position)) {
                nearest = positonable;
            }
        }
        return nearest;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Distance Methods">

    /**
     * Gets the real distance to the specified target from your players position.
     *
     * Real distance refers to the length of a valid path to the target.
     *
     *
     * @param target The target.
     * @return The distance in tiles to the specified target. -1 If the path could not be generated.
     */
    public static final double aStarDistanceTo(Positionable target) {
        ArrayList<RSTile> path = AStarPathfinder.get().getPath(target);
        return path != null ? path.size() : -1;
    }

    /**
     * Gets the real distance to the specified target from the specified start position.
     *
     * Real distance refers to the length of a valid path to the target.
     *
     *
     * @param start  The start position.
     * @param target The target.
     * @return The distance in tiles to the specified target. -1 if the path could not be generated.
     */
    public static final double aStarDistanceTo(Positionable start, Positionable target) {
        ArrayList<RSTile> path = AStarPathfinder.get().getPath(start, target);
        return path != null ? path.size() : -1;
    }

    /**
     * Returns the distance between the player's position and the specified Positionable.
     *
     *
     * @param pos The Positionable being tested.
     * @return The distance between the player's position and the specified Positionable.
     */
    public static final double distanceTo(Positionable pos) {
        return distanceTo(pos, Player.getPosition());
    }

    /**
     * Returns the distance between the two Positionables.
     *
     *
     * @param pos1 The first Positionable being tested.
     * @param pos2 The second Positionable being tested.
     * @return The distance between the two Positionables.
     */
    public static final double distanceTo(Positionable pos1, Positionable pos2) {
        if (pos1 == null || pos2 == null) {
            return -1;
        }
        return pos1.getPosition().distanceToDouble(pos2.getPosition());
    }
//</editor-fold>
}
