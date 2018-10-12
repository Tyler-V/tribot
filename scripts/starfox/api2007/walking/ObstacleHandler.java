package scripts.starfox.api2007.walking;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.Objects07;

/**
 * Handles obstacles that are "teleports". For more information, see {@link Obstacles#teleports}.
 *
 *
 * @author Spencer
 */
public class ObstacleHandler {

    /**
     * If the player is more than 250 tiles away from or is not on the same plane as the next tile, then attempt to handle a nearby "teleport" object.
     * If this method will first try to handle known level-changers.
     *
     * @param targetTile  The target tile.
     * @param nextTile    The next tile.
     * @param finalTarget The final target.
     * @return True if the teleport was successfully handled OR if it didn't need to be handled, false otherwise.
     */
    public static boolean handleTeleport(final RSTile targetTile, final RSTile nextTile, final RSTile finalTarget) {
        if ((nextTile.getPlane() != finalTarget.getPlane() || Entities.distanceTo(targetTile, nextTile) > 250)
                && !handleLevelChanger(targetTile.getPlane())) {
            final RSObject o = (RSObject) Entities.getAStarNearest(nextTile, Objects.find(10, ArrayUtil.toArrayString(Obstacles.teleports)));
            if (!clickTeleport(o)) {
                return false;
            }
        }
        return true;
    }

    private static boolean handleLevelChanger(final int targetPlane) {
        System.out.println("Handling level changer");
        final int currentPlane = Player07.getPosition().getPlane();
        String option = null;
        if (currentPlane < targetPlane) {
            option = "Climb-up";
        } else if (currentPlane > targetPlane) {
            option = "Climb-down";
        }
        RSObject o = Objects07.getObject(null, ArrayUtil.getAsArray(option), 15);
        if (o != null && option != null) {
            return clickTeleport(o);
        } else {
            return false;
        }
    }

    private static boolean clickTeleport(RSObject o) {
        final RSTile t = Player07.getPosition();
        final RSTile originalPosition = new RSTile(t.getX(), t.getY(), t.getPlane());
        if (Clicking.click(ArrayUtil.toArrayString(Obstacles.commands), o)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return originalPosition.distanceTo(Player07.getPosition()) > 15 || originalPosition.getPlane() != Player07.getPosition().getPlane();
                }
            }, 5000);
        } else {
            return false;
        }
    }
}
