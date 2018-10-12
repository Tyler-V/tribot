package scripts.starfox.api2007;

import org.tribot.api.General;
import org.tribot.api2007.Camera;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.AntiBan;

/**
 * @author Spencer
 */
public class Camera07 {

    private static long lastFixTime;
    private static long fix_delay;

    static {
        lastFixTime = -1;
        fix_delay = -1;
    }

    /**
     * Checks whether the camera should be fixed or not.
     * This method checks a timeout (to make sure the camera is not fixed constantly in state and event frameworks) and also the camera angle.
     * {@link #fix()} uses this method to check for it's rotation, so there is no need to call this method before calling {@code #fix()}.
     *
     * @return True if the camera should be fixed, false otherwise.
     * @see #fix()
     */
    public static boolean shouldFix() {
        return System.currentTimeMillis() > lastFixTime + fix_delay && Camera.getCameraAngle() < 70;
    }

    /**
     * Initializes the camera by setting it to a position that isn't directly forward. This method can wait up to 1.5 seconds before the actual camera movement is executed.
     *
     * @see #shouldFix()
     */
    public static void fix() {
        if (shouldFix()) {
            General.sleep(0, 1500);
            AntiBan.moveCamera();
            Camera.setCameraAngle(General.random(70, 100));
            resetFixDelay();
        }
    }

    /**
     * A helper method that resets the cameras fix delay to a new value. Additionally, this method will set the lastFixTime to the current time, so as a result, this method should
     * only be called inside of the {@link #fix()} method.
     */
    private static void resetFixDelay() {
        fix_delay = General.random(3000, 30000);
        lastFixTime = System.currentTimeMillis();
    }

    /**
     * Rotates the camera to a tile that is near the target, but not necessarily the target.
     *
     * @param tile The tile.
     */
    public static void turnTo(RSTile tile) {
        tile = tile.translate(General.random(-5, 5), General.random(-5, 5));
        Camera.turnToTile(tile);
    }

}
