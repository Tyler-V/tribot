package scripts.starfox.graphics.particles;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 * @author Nolan
 */
public class ParticleContainer {

    private final Object lock;
    private final ArrayList<Particle> particles;

    /**
     * Constructs a new ParticleContainer.
     */
    public ParticleContainer() {
        this(new ArrayList<Particle>());
    }

    /**
     * Constructs a new ParticleContainer.
     *
     * @param particles The list of particles to add to the container.
     */
    public ParticleContainer(ArrayList<Particle> particles) {
        this.lock = new Object();
        this.particles = particles;
    }

    /**
     * Gets the particles in the container.
     *
     * @return The particles.
     */
    public ArrayList<Particle> getParticles() {
        return this.particles;
    }

    /**
     * Adds a particle to the particle container.
     *
     * @param particle The particle to add.
     */
    public void addParticle(Particle particle) {
        synchronized (lock) {
            getParticles().add(particle);
        }
    }

    /**
     * Renders all of the particles in the container.
     *
     * @param g The graphics to render with.
     */
    public void renderParticles(Graphics g) {
        synchronized (lock) {
            Particle[] tempParticles = getParticles().toArray(new Particle[0]);
            for (int i = 0; i < tempParticles.length; i++) {
                Particle particle = tempParticles[i];
                if (particle == null || !particle.update()) {
                    getParticles().remove(i);
                } else {
                    particle.paint(g);
                }
            }
        }
    }
}
