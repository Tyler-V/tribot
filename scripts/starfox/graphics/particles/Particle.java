package scripts.starfox.graphics.particles;

import java.awt.Color;
import java.awt.Graphics;
import scripts.starfox.interfaces.graphics.Paintable;

/**
 * @author Nolan
 */
public class Particle
        implements Paintable {

    private int x;
    private int y;
    private final int sx;
    private final int sy;
    private final int fx;
    private final int fy;
    private final int maxGrowth;
    private int growth;
    private final long clickTime;
    private final long lastingTime;
    private final long finishTime;
    private Color color;

    /**
     * Constructs a new Particle.
     *
     * @param x           The x coordinate of the particle.
     * @param y           The y coordinate of the particle.
     * @param fx          The change in distance per-render of the x coordinate.
     * @param fy          The change in distance per-render of the y coordinate.
     * @param lastingTime The lasting time of the particle.
     * @param maxGrowth   The maximum amount of growth.
     * @param c           The color of the particle.
     */
    public Particle(int x, int y, int fx, int fy, long lastingTime, int maxGrowth, Color c) {
        this.x = sx = x;
        this.y = sy = y;
        this.fx = fx;
        this.fy = fy;
        this.maxGrowth = maxGrowth;
        this.clickTime = System.currentTimeMillis();
        this.finishTime = System.currentTimeMillis() + lastingTime;
        this.lastingTime = lastingTime;
        this.color = c;
    }

    /**
     * Makes an update to the particle.
     *
     * @return True if the particle should update at least once more, false otherwise.
     */
    public boolean update() {
        double offset = (System.currentTimeMillis() - clickTime) / (lastingTime * 1.0);
        if (offset > 1) {
            return false;
        } else {
            x = sx + ((int) (offset * fx));
            y = sy + ((int) (offset * fy));
            growth = (int) (offset * maxGrowth);
            int alpha = (int) (255 - (offset * 255));
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            return System.currentTimeMillis() < finishTime;
        }
    }

    /**
     * Gets the amount of time left until the particle is up.
     *
     * @return The amount of time left (in milliseconds).
     */
    public long getTimeLeft() {
        long timeLeft = finishTime - System.currentTimeMillis();
        return timeLeft >= 0 ? timeLeft : 0;
    }

    @Override
    public void paint(Graphics g) {
        double alpha = ((double) getTimeLeft() / (double) lastingTime) * 100.0 * ((double) color.getAlpha() / 100.0);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) alpha));
        g.fillRect(x, y, 1, 1);
        g.fillOval(x - (growth / 2), y - (growth / 2), growth, growth);
    }
}
