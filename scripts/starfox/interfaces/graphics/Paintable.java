package scripts.starfox.interfaces.graphics;

import java.awt.Graphics;

/**
 * @author Nolan
 */
public interface Paintable {

    /**
     * Should be used to paint the {@link Paintable}.
     *
     * @param g The The {@link Graphics} to render with.
     */
    void paint(Graphics g);
}
