package scripts.starfox.graphics.components;

import java.awt.*;

/**
 * Created by Nolan on 10/14/2015.
 */
public abstract class PaintButton {

    private final Rectangle area;
    private boolean pressed;

    /**
     * Constructs a new {@link PaintButton}.
     *
     * @param area The {@link Rectangle} that the button will be painted in.
     */
    public PaintButton(Rectangle area) {
        this.area = area;
    }

    /**
     * Gets the area of the {@link PaintButton}.
     *
     * @return The area.
     */
    public Rectangle getArea() {
        return this.area;
    }

    /**
     * Checks to see if the {@link PaintButton} is pressed.
     *
     * @return True if it is pressed, false otherwise.
     */
    public boolean isPressed() {
        return this.pressed;
    }

    /**
     * Sets the {@link PaintButton}'s pressed state to be the opposite of what it was prior to this called.
     */
    public void press() {
        this.pressed = !this.pressed;
    }

    /**
     * Should be used to paint the {@link PaintButton}.
     *
     * @param g The {@link Graphics} to render with.
     */
    public abstract void paint(Graphics g);
}
