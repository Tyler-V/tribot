package scripts.starfox.graphics.mouse.types;

import org.tribot.api.General;
import scripts.starfox.graphics.mouse.MousePaint;
import scripts.starfox.graphics.particles.Particle;
import scripts.starfox.graphics.particles.ParticleContainer;
import scripts.starfox.graphics.trails.OvalTrail;

import java.awt.*;

/**
 * @author Nolan
 */
public class ParticleMouse
        extends MousePaint {

    private final ParticleContainer particleContainer;
    private final OvalTrail ovalTrail;
    private final Color[] colors;
    private final Color colorVariant;
    private final int amount;
    private final long lastingTimeMin;
    private final long lastingTimeMax;
    private final int size;
    private final int minGrowth;
    private final int maxGrowth;

    /**
     * Constructs a new {@link ParticleMouse}.
     *
     * @param colors         The colors.
     * @param colorVariant   The color variant.
     * @param amount         The amount of particles to add per-render.
     * @param lastingTimeMin The minimum lasting time of a particle.
     * @param lastingTimeMax The maximum lasting time of a particle.
     * @param size           The size of each particle.
     * @param minGrowth      The minimum growth of a particle.
     * @param maxGrowth      The maximum growth of a particle.
     */
    public ParticleMouse(Color[] colors, Color colorVariant, int amount, long lastingTimeMin, long lastingTimeMax, int size, int minGrowth, int maxGrowth) {
        super();
        if (colors.length < 1) {
            throw new IllegalArgumentException("colors length outside of expected range: " + colors.length);
        }
        this.colors = colors;
        this.colorVariant = colorVariant;
        this.amount = amount;
        this.lastingTimeMin = lastingTimeMin;
        this.lastingTimeMax = lastingTimeMax;
        this.size = size;
        this.minGrowth = minGrowth;
        this.maxGrowth = maxGrowth;
        this.particleContainer = new ParticleContainer();
        this.ovalTrail = new OvalTrail(750, 12, colors[General.random(0, colors.length - 1)]);
    }

    /**
     * Generates a new particle.
     *
     * @return A particle.
     */
    private Particle generateParticle(int multiplier) {
        Point mouse = getLocation();
        Color color1 = colors[General.random(0, colors.length - 1)];
        int rV = (General.random(-1 * colorVariant.getRed(), colorVariant.getRed())) + color1.getRed();
        int gV = (General.random(-1 * colorVariant.getGreen(), colorVariant.getGreen())) + color1.getGreen();
        int bV = (General.random(-1 * colorVariant.getBlue(), colorVariant.getBlue())) + color1.getBlue();
        rV = rV > 0 ? rV : 0;
        rV = rV < 255 ? rV : 255;
        gV = gV > 0 ? gV : 0;
        gV = gV < 255 ? gV : 255;
        bV = bV > 0 ? bV : 0;
        bV = bV < 255 ? bV : 255;
        Color color2 = new Color(rV, gV, bV);
        double o = (25 / (((22 / 3) * Math.random()) + 1)) - 3;
        int dx = (int) (Math.random() * o * size * multiplier);
        int dy = (int) (Math.random() * o * size * multiplier);
        dx = (int) (dx * (Math.random() < .5 ? -1 : 1));
        dy = (int) (dy * (Math.random() < .5 ? -1 : 1));
        return new Particle(mouse.x, mouse.y, dx, dy, General.random((int) lastingTimeMin, (int) lastingTimeMax),
                General.random(minGrowth * multiplier / 2, maxGrowth * multiplier / 2), color2);
    }

    @Override
    public void paint(Graphics g) {
        ovalTrail.setColor(colors[General.random(0, colors.length - 1)]);
        ovalTrail.paint(g);
        for (int i = 0; i < amount; i++) {
            if (particleContainer.getParticles().size() < 1000) {
                particleContainer.addParticle(generateParticle(1));
            }
        }
        particleContainer.renderParticles(g);
    }

    @Override
    public void mouseReleased(Point arg0, int arg1, boolean arg2) {
        for (int i = 0; i < amount * 15; i++) {
            if (particleContainer.getParticles().size() < 1000) {
                particleContainer.addParticle(generateParticle(4));
            }
        }
    }
}
