package scripts.starfox.graphics.trails;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Nolan
 */
public class OvalTrail
        extends Trail {

    private final int size;
    private Color color;

    /**
     * Constructs a new OvalTrail.
     *
     * @param lastingTime
     * @param size        The size of each oval on the trail.
     * @param color       The color of the trail.
     */
    public OvalTrail(long lastingTime, int size, Color color) {
        super(lastingTime);
        this.size = size;
        this.color = color;
    }

    /**
     * Gets the size of each oval on the trail.
     *
     * @return The size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Gets the color of the oval trail.
     *
     * @return The color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the oval trails color.
     *
     * @param color The color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics g) {
        synchronized (lock) {
            if (getTrailPoints().size() > 1) {
                for (TrailPoint point : getTrailPoints()) {
                    double alpha = point.getTimeLeft() / point.getLastingTime() * 100.0 * ((double) getColor().getAlpha() / 100.0);
                    int tempSize = (int) ((point.getTimeLeft() / point.getLastingTime()) * getSize());
                    Color c = getColor();
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) alpha));
                    g.fillOval(point.x - tempSize / 2, point.y - tempSize / 2, tempSize, tempSize);
                }
            }
        }
    }
}
