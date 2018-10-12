package scripts.starfox.graphics.mouse;

import org.tribot.api.ListenerManager;
import org.tribot.script.interfaces.MouseActions;
import scripts.starfox.api2007.Mouse07;
import scripts.starfox.interfaces.graphics.Paintable;

import java.awt.*;

/**
 * @author Nolan
 */
public abstract class MousePaint
        implements Paintable, MouseActions {

    /**
     * Constructs a new MousePaint.
     */
    public MousePaint() {
        ListenerManager.add(this);
    }

    /**
     * Gets the location of the mouse paint.
     *
     * @return The location.
     */
    public Point getLocation() {
        return Mouse07.getLocation();
    }

    @Override
    public void mouseReleased(Point arg0, int arg1, boolean arg2) {
    }

    @Override
    public void mouseClicked(Point arg0, int ag1, boolean ag2) {
    }

    @Override
    public void mouseDragged(Point p, int arg1, boolean arg2) {
    }

    @Override
    public void mouseMoved(Point p, boolean arg1) {
    }
}
