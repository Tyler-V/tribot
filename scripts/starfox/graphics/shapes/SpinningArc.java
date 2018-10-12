package scripts.starfox.graphics.shapes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import scripts.starfox.interfaces.graphics.Paintable;

/**
 * @author Nolan
 */
public class SpinningArc
        extends Arc2D.Double
        implements Paintable {

    private double spinRate;
    private double progress;
    private boolean reverse;

    /**
     * Constructs a new SpinningArc.
     *
     * @param x        The x coordinate.
     * @param y        The y coordinate.
     * @param w        The width.
     * @param h        The height.
     * @param start    The starting angle.
     * @param extent   The extent of the arc.
     * @param type     The type of the arc.
     * @param spinRate The spin rate of the arc.
     */
    public SpinningArc(double x, double y, double w, double h, double start, double extent, int type, double spinRate) {
        super(x, y, w, h, start, extent, type);
        this.spinRate = spinRate;
        this.progress = start / 360D;
        this.reverse = false;
    }

    /**
     * Gets the spin rate of the arc.
     *
     * @return The spin rate.
     */
    public double getSpinRate() {
        return this.spinRate;
    }

    /**
     * Checks to see if the arc is reversed.
     *
     * @return True if it is reversed, false otherwise.
     */
    public boolean isReverse() {
        return this.reverse;
    }

    /**
     * Sets the spin rate of the arc.
     *
     * @param spinRate The spin rate to set.
     */
    public void setSpinRate(double spinRate) {
        this.spinRate = spinRate;
    }

    /**
     * Sets the arc to be reversed or not.
     *
     * @param reverse True for reversed, false otherwise.
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Sets the location of the arc.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Spins the arc by the spin rate.
     */
    public void spin() {
        start = 360D * progress;
        progress += reverse ? -spinRate : spinRate;
        progress = (progress > 1.0 ? 0 : progress < 0.0 ? 1.0 : progress);
    }

    @Override
    public void paint(Graphics g) {
        spin();
        ((Graphics2D) g).draw(this);
    }
}
