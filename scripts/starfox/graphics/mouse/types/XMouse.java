package scripts.starfox.graphics.mouse.types;

import org.tribot.api.ListenerManager;
import scripts.starfox.graphics.mouse.MousePaint;
import scripts.starfox.graphics.shapes.X;
import scripts.starfox.graphics.trails.BasicTrail;
import scripts.starfox.graphics.trails.TrailPoint;

import java.awt.*;
import java.util.LinkedList;

/**
 * @author Nolan
 */
public class XMouse
        extends MousePaint {

    protected final Object lock;

    /**
     * The list of points where the mouse has been clicked.
     */
    private final LinkedList<TrailPoint> xList;

    /**
     * The X shape.
     */
    private final X x;

    /**
     * The color of the X.
     */
    private final Color xColor;

    /**
     * The color of the x list.
     */
    private final Color xListColor;

    /**
     * The lasting time of each trail point in the x list.
     */
    private final long lastingTime;

    /**
     * The trail of the mouse.
     */
    private final BasicTrail trail;

    /**
     * Constructs a new XMouse.
     *
     *
     * @param size        The size of the X.
     * @param xColor      The color of the X.
     * @param xListColor  The color of the x list.
     * @param lastingTime The lasting time (in milliseconds) of each X in the x list.F
     */
    public XMouse(int size, Color xColor, Color xListColor, long lastingTime) {
        this.lock = new Object();
        this.xList = new LinkedList<>();
        this.x = new X(new Point(), size);
        this.xColor = xColor;
        this.xListColor = xListColor;
        this.lastingTime = lastingTime;
        this.trail = new BasicTrail(1000, Color.WHITE);
        ListenerManager.add(this);
    }

    /**
     * Constructs a new XMouse with default values.
     * Size: 6
     * X color: white
     * X list color: red
     * Lasting time: 2000 milliseconds
     */
    public XMouse() {
        this(6, Color.WHITE, Color.RED, 2000);
    }

    /**
     * Gets the X.
     *
     * @return The X.
     */
    public X getX() {
        return this.x;
    }

    /**
     * Gets the x list.
     *
     * @return The x list.
     */
    public LinkedList<TrailPoint> getxList() {
        return this.xList;
    }

    /**
     * Gets the color of the X.
     *
     * @return The color.
     */
    public Color getxColor() {
        return this.xColor;
    }

    /**
     * Gets the x list color.
     *
     * @return The color.
     */
    public Color getxListColor() {
        return this.xListColor;
    }

    /**
     * Gets the lasting time of each X in the x list.
     *
     * @return The lasting time.
     */
    public long getLastingTime() {
        return this.lastingTime;
    }

    @Override
    public void paint(Graphics g) {
        Point mousePoint = getLocation();
        g.setColor(getxColor());
        x.setLocation(mousePoint);
        x.paint(g);
        synchronized (lock) {
            TrailPoint[] xPoints = getxList().toArray(new TrailPoint[getxList().size()]);
            for (TrailPoint xPoint : xPoints) {
                if (!xPoint.isUp()) {
                    getxList().remove(xPoint);
                }
                double alpha = (xPoint.getTimeLeft() / xPoint.getLastingTime()) * 100.0 * ((double) getxListColor().getAlpha() / 100.0);
                if (alpha < 0) {
                    alpha = 0;
                }
                g.setColor(new Color(getxListColor().getRed(), getxListColor().getGreen(), getxListColor().getBlue(), (int) alpha));
                new X(xPoint.getLocation(), x.getSize()).paint(g);
            }
        }
        trail.paint(g);
    }

    @Override
    public void mouseReleased(Point arg0, int arg1, boolean arg2) {
        synchronized (lock) {
            if (xList.size() < 10) {
                xList.add(new TrailPoint(arg0, getLastingTime()));
            }
        }
    }
}
