package scripts.starfox.api2007;

import org.tribot.api.ListenerManager;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.interfaces.MouseActions;
import scripts.starfox.api.Client;

import java.awt.*;

/**
 * The Mouse07 class provides utility methods related to the mouse.
 *
 *
 * @author Nolan
 */
public class Mouse07
        implements MouseActions {

    /**
     * The instance of this class.
     */
    private static Mouse07 mouse07;

    /**
     * A constant representing the left mouse button.
     */
    public static final int LEFT_BUTTON = 1;

    /**
     * A constant representing the middle mouse button.
     */
    public static final int MIDDLE_BUTTON = 2;

    /**
     * A constant representing the right mouse button.
     */
    public static final int RIGHT_BUTTON = 3;

    /**
     * The location of the mouse.
     */
    private Point location;

    /**
     * The point at which the mouse was last clicked.
     */
    private Point lastClickLocation;

    /**
     * Constructs a new Mouse07.
     */
    private Mouse07() {
        this.location = Mouse.getPos();
        this.lastClickLocation = new Point();
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    private static Mouse07 get() {
        if (mouse07 == null) {
            mouse07 = new Mouse07();
            ListenerManager.add(mouse07);
        }
        return mouse07;
    }

    /**
     * Sets the mouse speed to the specified value.
     *
     * @param speed The speed to set the mouse to.
     */
    public static void setSpeed(int speed) {
        Mouse.setSpeed(speed);
    }

    /**
     * Increments the mouse speed by 1.
     */
    public static void incrementSpeed() {
        Mouse.setSpeed(Mouse.getSpeed() + 1);
    }

    /**
     * Decrements the mouse speed by 1.
     */
    public static void decrementSpeed() {
        Mouse.setSpeed(Mouse.getSpeed() - 1);
    }

    /**
     * Makes the mouse leave the game screen.
     *
     * @param loseFocus True to have the client lose focus after the mouse is moved, false otherwise.
     */
    public static void leaveGame(boolean loseFocus) {
        Mouse.leaveGame(loseFocus);
    }

    /**
     * Scrolls the mouse up or down the specified amount of ticks.
     *
     * @param up    True to scroll up, false to scroll down.
     * @param ticks The amount of ticks to scroll.
     */
    public static void scroll(boolean up, int ticks) {
        Mouse.scroll(up, ticks);
    }

    /**
     * Sends a mouse pressed event to the Point specified using the specified mouse button.
     *
     * @param p      The point to send the event to.
     * @param button The button to use when sending the event.
     */
    public static void sendPressEvent(Point p, int button) {
        Mouse.sendPress(p, button);
    }

    /**
     * Sends a mouse released event to the Point specified using the specified mouse button.
     *
     * @param p      The point to send the event to.
     * @param button The button to use when sending the event.
     */
    public static void sendReleaseEvent(Point p, int button) {
        Mouse.sendRelease(p, button);
    }

    /**
     * Sends a mouse clicked event to the Point specified using the specified mouse button.
     *
     * @param p      The point to send the event to.
     * @param button The button to use when sending the event.
     */
    public static void sendClickEvent(Point p, int button) {
        Mouse.sendClickEvent(p, button);
    }

    /**
     * Sends a mouse click to the Point specified using the specified mouse button.
     *
     * @param p      The point to send the event to.
     * @param button The button to use when sending the event.
     */
    public static void sendClick(Point p, int button) {
        Mouse.sendClick(p, button);
    }

    /**
     * Sends a press, release, and click event to the specified point with the specified mouse button.
     *
     * @param p      The point.
     * @param button The mouse button.
     */
    public static void sendEvents(Point p, int button) {
        sendPressEvent(p, button);
        sendReleaseEvent(p, button);
        sendClickEvent(p, button);
    }

    /**
     * Gets the location of the mouse.
     *
     * @return The location of the mouse.
     */
    public static Point getLocation() {
        return get().location;
    }

    /**
     * Gets the current destination of the mouse.
     *
     * @return The destination of the mouse.
     * Null if the mouse has no destination.
     */
    public static Point getDestination() {
        Point destination = Mouse.getDestination();
        if (destination.x == -1 && destination.y == -1) {
            return null;
        }
        return destination;
    }

    /**
     * Fixes the mouse if there is something selected.
     *
     *
     * @return True if it was fixed or had nothing selected in the first place, false otherwise.
     */
    public static boolean fixSelected() {
        if (Game.isUptext("->")) {
            if (!ChooseOption.isOpen()) {
                Mouse.click(3);
            }
            if (Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return ChooseOption.isOpen();
                }
            }, 250)) {
                if (ChooseOption.select("Cancel")) {
                    System.out.println("Fixed selected mouse.");
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Hops the mouse to the specified point and then clicks the specified button.
     *
     * @param p      The point to hop to.
     * @param button The button to click.
     * @param delay  The delay between the hop and the click.
     */
    public static void hopClick(Point p, int button, long delay) {
        if (p == null) {
            return;
        }
        Mouse.hop(p);
        Client.sleep(delay);
        sendEvents(p, button);
    }

    /**
     * Hops the mouse to the specified x and y coordinate and then clicks the specified button.
     *
     * @param x      The x coordinate.
     * @param y      The y coordinate.
     * @param button The button to click.
     * @param delay  The delay between the hop and the click.
     */
    public static void hopClick(int x, int y, int button, long delay) {
        hopClick(new Point(x, y), button, delay);
    }

    /**
     * Moves the mouse to the specified point and then clicks the specified button.
     *
     * @param p      The point to move to.
     * @param button The button to click.
     * @param delay  The delay between the movement and the click.
     */
    public static void moveClick(Point p, int button, long delay) {
        if (p == null) {
            return;
        }
        Mouse.move(p);
        Client.sleep(delay);
        sendEvents(p, button);
    }

    /**
     * Moves the mouse to the specified x and y coordinate and then clicks the specified button.
     *
     * @param x      The x coordinate.
     * @param y      The y coordinate.
     * @param button The button to click.
     * @param delay  The delay between the movement and the click.
     */
    public static void moveClick(int x, int y, int button, long delay) {
        moveClick(new Point(x, y), button, delay);
    }

    /**
     * Clicks a random point inside the specified area.
     *
     * @param area   The area to click in.
     * @param button The button to click.
     */
    public static void clickArea(Rectangle area, int button) {
        if (area == null) {
            return;
        }
        Mouse.clickBox(area, button);
    }

    /**
     * Clicks a random point inside the specified area.
     *
     * @param x1     The x coordinate of the top left of the area.
     * @param y1     The y coordinate of the top left of the area.
     * @param x2     The x coordinate of the bottom right of the area.
     * @param y2     The y coordinate of the bottom right of the area.
     * @param button The button to click.
     */
    public static void clickArea(int x1, int y1, int x2, int y2, int button) {
        clickArea(new Rectangle(x1, y1, x2 - x1, y2 - y1), button);
    }

    /**
     * Checks to see if the mouse is on the specified tile.
     *
     * This method will also return the tile on the mini-map.
     *
     * @param tile The tile that is being tested.
     * @return True if the mouse is on the specified tile, false otherwise.
     */
    public static boolean isMouseOnTile(RSTile tile) {
        if (tile != null) {
            if (tile.isOnScreen()) {
                Polygon polygon = Projection.getTileBoundsPoly(tile, 0);
                return polygon != null && polygon.contains(getLocation());
            }
        }
        return false;
    }

    /**
     * Gets the tile that the mouse is currently hovering.
     *
     * @return The tile that the mouse is currently hovering.
     * Null if no tile is being hovered.
     */
    public static RSTile getMouseTile() {
        for (RSTile tile : new RSArea(Player.getPosition(), 20).getAllTiles()) {
            if (isMouseOnTile(tile)) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(Point p, int arg1, boolean arg2) {}

    @Override
    public void mouseMoved(Point point, boolean arg1) {
        get().location = new Point(point);
    }

    @Override
    public void mouseDragged(Point point, int arg1, boolean arg2) {
        get().location = new Point(point);
    }

    @Override
    public void mouseReleased(Point p, int arg1, boolean arg2) {
        get().lastClickLocation = new Point(p);
    }
}
