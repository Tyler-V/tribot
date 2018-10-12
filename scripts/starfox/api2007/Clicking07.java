package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.waiting.Waiting;

/**
 * Created by Nolan on 10/21/2015.
 */
public class Clicking07 {

    public static Clickable currentClickable;

    static {
        currentClickable = null;
    }

    /**
     * Gets the {@link Clickable} that is currently being clicked.
     *
     * @return The {@link Clickable} that is being clicked.
     * Null if no {@link Clickable} is being clicked at the time of this method call.
     */
    public static Clickable getCurrentClickable() {
        return currentClickable;
    }

    /**
     * Clicks the specified {@link Clickable} with the specified option.
     *
     * @param option     The option to select.
     * @param clickables The {@link Clickable} to click.
     * @return True if the click was successful, false otherwise.
     */
    public static boolean click(String option, Clickable... clickables) {
        if (clickables == null || clickables.length <= 0 || clickables[0] == null || option == null) {
            return false;
        }
        currentClickable = clickables[0];
        Crosshair crosshair = clickables[0] instanceof RSTile ? Crosshair.YELLOW : Crosshair.RED;
        if (option.equals("Cancel") || clickables[0] instanceof RSItem || clickables[0] instanceof RSInterface) {
            crosshair = Crosshair.NONE;
        }
        if (currentClickable instanceof RSItem || currentClickable instanceof RSInterface) {
            if (currentClickable.click(option)) {
                destroy();
                return true;
            }
        }
        if (Clicking.click(option, clickables) && Waiting.waitUntilCrosshair(crosshair, 50)) {
            destroy();
            return true;
        }
        return false;
    }

    /**
     * Clicks the specified {@link Clickable} with the specified {@link RSMenuNode}.
     *
     * @param menuNodeFilter The {@link RSMenuNode} to select.
     * @param clickables     The {@link Clickable} to click.
     * @return True if the click was successful, false otherwise.
     */
    public static boolean click(Filter<RSMenuNode> menuNodeFilter, Clickable07... clickables) {
        if (clickables == null || clickables.length <= 0 || clickables[0] == null || menuNodeFilter == null) {
            return false;
        }
        currentClickable = clickables[0];
        Crosshair crosshair = clickables[0] instanceof RSTile ? Crosshair.YELLOW : Crosshair.RED;
        if (clickables[0] instanceof RSItem || clickables[0] instanceof RSInterface) {
            crosshair = Crosshair.NONE;
        }
        if (Clicking.click(menuNodeFilter, clickables) && Waiting.waitUntilCrosshair(crosshair, 50)) {
            destroy();
            return true;
        }
        return false;
    }

    /**
     * Destroys the current clickable.
     */
    public static void destroy() {
        currentClickable = null;
    }
}
