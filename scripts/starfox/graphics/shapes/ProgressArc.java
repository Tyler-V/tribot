package scripts.starfox.graphics.shapes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import scripts.starfox.interfaces.graphics.Paintable;

/**
 * @author Nolan
 */
public class ProgressArc
        extends Arc2D.Double
        implements Paintable {

    private double progressRate;
    private double progress;
    private boolean fill;

    /**
     * Constructs a new ProgressArc.
     *
     * @param x            The x coordinate.
     * @param y            The y coordinate.
     * @param w            The width.
     * @param h            The height.
     * @param start        The starting angle.
     * @param extent       The extent of the arc.
     * @param type         The type of the arc.
     * @param progressRate The rate of progress of the arc.
     */
    public ProgressArc(double x, double y, double w, double h, double start, double extent, int type, double progressRate) {
        super(x, y, w, h, start, extent, type);
        this.progressRate = progressRate;
        this.progress = start / 360D;
        this.fill = true;
    }

    /**
     * Gets the progress of the arc.
     *
     * @return The progress.
     */
    public double getProgress() {
        return this.progress;
    }

    /**
     * Gets the progress rate of the arc.
     *
     * @return The progress rate.
     */
    public double getProgressRate() {
        return progressRate;
    }

    /**
     * Checks whether or not the arc is filling up or emptying.
     *
     * @return True if it is filling, false if it is emptying.
     */
    public boolean isFill() {
        return this.fill;
    }

    /**
     * Sets the progress rate.
     *
     * @param progressRate The progress rate to set.
     */
    public void setProgressRate(double progressRate) {
        this.progressRate = progressRate;
    }

    /**
     * Sets the progress of the arc.
     *
     * @param progress The progress.
     */
    public void setProgress(double progress) {
        this.progress = progress;
    }

    /**
     * Sets the arc to be filling or emptying.
     *
     * @param fill True for filling, false for emptying.
     */
    public void setFill(boolean fill) {
        this.fill = fill;
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
     * Progresses the arc by the progress rate.
     */
    public void progress() {
        extent = 360D * progress;
        extent = isFill() ? -extent : 360 - extent;
        setProgress(getProgress() + progressRate);
        if (progress > 1.0) {
            setFill(!isFill());
            setProgress(0);
        }
    }

    @Override
    public void paint(Graphics g) {
        progress();
        ((Graphics2D) g).draw(this);
    }
}
