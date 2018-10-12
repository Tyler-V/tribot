package scripts.starfox.api.waiting;

import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Crosshair;

/**
 * The Waiting class contains various utility methods that allow for conditional waiting.
 *
 * @author Nolan
 */
public class Waiting {

    /**
     * Waits until the specified {@link Condition07} is met, or until the specified time-out is reached.
     *
     * @param condition07 The {@link Condition07} being checked.
     * @param time_out    The maximum amount of time to wait (in milliseconds).
     * @return True if the {@link Condition07} was met before the maximum time-out, false otherwise.
     */
    public static boolean waitUntil(Condition07 condition07, long time_out) {
        Timer timer = new Timer(time_out);
        timer.start();
        while (!timer.timedOut()) {
            Client.sleep(50, 75);
            if (condition07.isMet()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Waits until the specified {@link Condition07} is met, or until the specified time-out is reached.
     * The time-out is reset if the local player moves.
     *
     * @param condition07 The {@link Condition07} being checked.
     * @param time_out    The maximum amount of time to wait (in milliseconds).
     * @return True if the {@link Condition07} was met before the maximum time-out, false otherwise.
     */
    public static boolean waitUntilMove(Condition07 condition07, long time_out) {
        Timer timer = new Timer(time_out);
        timer.start();
        RSTile start = Player.getPosition();
        while (!timer.timedOut()) {
            Client.sleep(50, 75);
            if (condition07.isMet()) {
                return true;
            }
            RSTile player = Player.getPosition();
            if (player.distanceTo(start) > 0.0) {
                start = new RSTile(player.getX(), player.getY(), player.getPlane());
                timer.reset();
            }
        }
        return false;
    }

    /**
     * Waits until the condition is met. The timeout is reset if the local player is moving.
     *
     * @param c       The condition.
     * @param timeout The timeout in milliseconds.
     * @return True if the condition was met before the timeout.
     */
    public static boolean waitMoveCondition(Condition c, long timeout) {
        Timer timer = new Timer(timeout);
        timer.start();
        RSTile start = Player.getPosition();
        while (!timer.timedOut()) {
            if (c.active()) {
                return true;
            }
            RSTile player = Player.getPosition();
            if (player.distanceTo(start) > 0.0) {
                start = new RSTile(player.getX(), player.getY(), player.getPlane());
                timer.reset();
            }
            Client.sleep(25);
        }
        return false;
    }

    /**
     * Waits the specified amount of time until the crosshair equals the specified crosshair.
     *
     * @param crosshair The crosshair to wait for.
     * @param wait_time The maximum amount of time to wait (in milliseconds).
     * @return True if the crosshair was equal to the specified crosshair before the specified amount of time, false otherwise.
     */
    public static boolean waitUntilCrosshair(Crosshair crosshair, long wait_time) {
        return Timing.waitCrosshair(wait_time) == crosshair.getValue();
    }
}
