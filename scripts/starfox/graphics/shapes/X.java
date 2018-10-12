package scripts.starfox.graphics.shapes;

import java.awt.Graphics;
import java.awt.Point;

import scripts.starfox.interfaces.graphics.Paintable;

/**
 * @author Nolan
 */
public class X
        implements Paintable {

    private Point p;
    private int size;

    /**
     * Constructs a new X shape.
     *
     * @param location The location of the X.
     * @param size     The size in pixels of the X.
     */
    public X(Point location, int size) {
        this.p = new Point(location);
        this.size = size;
    }

    /**
     * Gets the location of the X.
     *
     * @return The location.
     */
    public Point getLocation() {
        return this.p;
    }

    /**
     * Gets the size of the X.
     *
     * @return The size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Sets the location of the X to be the specified location.
     *
     * @param p The point to set.
     */
    public void setLocation(Point p) {
        this.p = new Point(p);
    }

    /**
     * Sets the size of the X.
     *
     * @param size The size to set.
     */
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void paint(Graphics g) {
        int s = getSize();
        g.drawLine(p.x - s, p.y + s, p.x + s, p.y - s);
        g.drawLine(p.x + s, p.y + s, p.x - s, p.y - s);
    }
}
