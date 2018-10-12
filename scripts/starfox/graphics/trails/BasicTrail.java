package scripts.starfox.graphics.trails;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * @author Nolan
 */
public class BasicTrail
        extends Trail {

    private final Color color;

    /**
     * Constructs a new BasicTrail.
     *
     * @param lastingTime The lasting time of each trail point.
     * @param color       The color of the trail.
     */
    public BasicTrail(long lastingTime, Color color) {
        super(lastingTime);
        this.color = color;
    }

    /**
     * Gets the color of the basic trail.
     *
     * @return The color of the basic trail.
     */
    public Color getColor() {
        return this.color;
    }

    @Override
    public void paint(Graphics g) {
        TrailPoint lastPoint = null;
        synchronized (lock) {
            for (TrailPoint point : getTrailPoints()) {
                if (lastPoint != null) {
                    double alpha = point.getTimeLeft() / point.getLastingTime() * 100.0 * ((double) getColor().getAlpha() / 100.0);
                    double position = (double) getTrailPoints().indexOf(point) / (double) getTrailPoints().size();
                    float stroke = 0f + (1.5f * (float) position);
                    Color c = getColor();
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) alpha));
                    ((Graphics2D) g).setStroke(new BasicStroke(stroke));
                    g.drawLine(lastPoint.x, lastPoint.y, point.x, point.y);
                }
                lastPoint = point;
            }
        }
    }
}
