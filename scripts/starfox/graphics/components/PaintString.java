package scripts.starfox.graphics.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author Nolan
 */
public abstract class PaintString {

    public static final Font DEFAULT_FONT = new Font("Arial", 0, 12);
    public static final Color DEFAULT_COLOR = Color.WHITE;

    private final Point location;
    private final Font font;
    private final Color color;
    
    /**
     * Constructs a new PaintString.
     *
     * @param x    The x coordinate.
     * @param y    The y coordinate.
     */
    public PaintString(int x, int y) {
        this(x, y, DEFAULT_FONT, DEFAULT_COLOR);
    }

    /**
     * Constructs a new PaintString.
     *
     * @param x    The x coordinate.
     * @param y    The y coordinate.
     * @param font The font to use when painting the string.
     */
    public PaintString(int x, int y, Font font) {
        this(x, y, font, DEFAULT_COLOR);
    }

    /**
     * Constructs a new PaintString.
     *
     * @param x     The x coordinate.
     * @param y     The y coordinate.
     * @param color The color to use when painting the string.
     */
    public PaintString(int x, int y, Color color) {
        this(x, y, DEFAULT_FONT, color);
    }

    /**
     * Constructs a new PaintString.
     *
     * @param x     The x coordinate.
     * @param y     The y coordinate.
     * @param font  The font to use when painting the string.
     * @param color The color to use when painting the string.
     */
    public PaintString(int x, int y, Font font, Color color) {
        this(new Point(x, y), font, color);
    }

    /**
     * Constructs a new PaintString.
     *
     * @param location The location of the paint string.
     * @param font     The font to use when painting the string.
     * @param color    The color to use when painting the string.
     */
    public PaintString(Point location, Font font, Color color) {
        this.location = location;
        this.font = font;
        this.color = color;
    }

    /**
     * Gets the location.
     *
     * @return The location.
     */
    public Point getLocation() {
        return this.location;
    }

    /**
     * Gets the font.
     *
     * @return The font.
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Gets the color.
     *
     * @return The color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Draws the paint string at its location.
     *
     * @param g The graphics to render with.
     */
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.setFont(getFont());
        g.drawString(getString(), getLocation().x, getLocation().y);
    }

    /**
     * Gets the string.
     *
     * @return The string.
     */
    public abstract String getString();
}
