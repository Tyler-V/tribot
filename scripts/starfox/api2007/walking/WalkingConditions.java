package scripts.starfox.api2007.walking;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.PathFinding;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Camera07;
import scripts.starfox.api2007.entities.Entities;

/**
 *
 * @author Spencer
 */
public class WalkingConditions {

    /**
     * Checks whether the movement is valid when walking to the target positionable.
     *
     * Checks if the distance is less than 8, the a* distance is less than or equal to 5, and then if the positionable is on screen.
     *
     * @param target The positionable that is being walked to.
     * @return True if the walking should be stopped, false otherwise.
     */
    public static Condition genericCondition(final Positionable target) {
        return new Condition() {
            @Override
            public boolean active() {
                Client.sleep(5);
                if (Entities.distanceTo(target) <= 7 && Entities.aStarDistanceTo(target) <= 5 && PathFinding.canReach(target, true)) {
                    Timer t = new Timer(5000);
                    t.start();
                    while (!t.timedOut() && !Entities.isCenterOnScreen(target)) {
                        Camera07.turnTo(target.getPosition());
                        Client.sleep(50, 250);
                    }
                    return Entities.isOnScreen(target);
                } else {
                    return false;
                }
            }
        };
    }
}
