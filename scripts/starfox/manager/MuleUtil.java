package scripts.starfox.manager;

import java.awt.Point;
import org.tribot.api.General;
import org.tribot.api2007.Player;

/**
 *
 * @author Spencer
 */
public class MuleUtil {

    public static long generateHash() {
        String tempHash = "";
        for (int i = 0; i < General.random(10, 15); i++) {
            tempHash += "" + General.random(0, 9);
        }
        return Long.parseLong(tempHash);
    }
    
    public static double distanceTo(Point p) {
        return General.distanceTo(new Point(Player.getPosition().getX(), Player.getPosition().getY()), p);
    }
}
