package scripts.starfox.graphics.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author Nolan
 */
public class PaintBlock {

    /**
     * The default {@link Font}.
     */
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);

    /**
     * The default text {@link Color}.
     */
    public static final Color DEFAULT_TEXT_COLOR = new Color(255, 255, 255, 200);

    private DynamicString dynamicString;
    private Font font;
    private Color textColor;
    private Point location;

    /**
     * Constructs a new {@link PaintBlock}.
     *
     * The {@link Font} will be set to {@link PaintBlock#DEFAULT_FONT} by default.
     * The {@link Color} of the text will be set to {@link PaintBlock#DEFAULT_TEXT_COLOR} by default.
     * The {@link Point} of the {@link PaintBlock} will be set to the default {@link Point} constructor by default.
     *
     * @param dynamicString The {@link DynamicString} to be painted inside the {@link PaintBlock}.
     */
    public PaintBlock(DynamicString dynamicString) {
        this(dynamicString, DEFAULT_TEXT_COLOR);
    }

    /**
     * Constructs a new {@link PaintBlock}.
     *
     * The {@link Font} will be set to {@link PaintBlock#DEFAULT_FONT} by default.
     * The {@link Point} of the {@link PaintBlock} will be set to the default {@link Point} constructor by default.
     *
     * @param dynamicString The {@link DynamicString} to be painted inside the {@link PaintBlock}.
     * @param textColor     The {@link Color} of the text.
     */
    public PaintBlock(DynamicString dynamicString, Color textColor) {
        this(dynamicString, DEFAULT_FONT, textColor);
    }

    /**
     * Constructs a new {@link PaintBlock}.
     *
     * The {@link Point} of the {@link PaintBlock} will be set to the default {@link Point} constructor by default.
     *
     * @param dynamicString The {@link DynamicString} to be painted inside the {@link PaintBlock}.
     * @param font          The {@link Font} of the {@link PaintBlock}.
     * @param textColor     The {@link Color} of the text.
     */
    public PaintBlock(DynamicString dynamicString, Font font, Color textColor) {
        this(dynamicString, font, textColor, new Point());
    }

    /**
     * Constructs a new {@link PaintBlock}.
     *
     * The {@link Font} will be set to {@link PaintBlock#DEFAULT_FONT} by default.
     *
     * @param dynamicString The {@link DynamicString} to be painted inside the {@link PaintBlock}.
     * @param textColor     The {@link Color} of the text.
     * @param location      The {@link Point} where the {@link PaintBlock} will be painted.
     */
    public PaintBlock(DynamicString dynamicString, Color textColor, Point location) {
        this(dynamicString, DEFAULT_FONT, textColor, location);
    }

    /**
     * Constructs a new {@link PaintBlock}.
     *
     * @param dynamicString The {@link DynamicString} to be painted inside the {@link PaintBlock}.
     * @param font          The {@link Font} of the {@link PaintBlock}.
     * @param textColor     The {@link Color} of the text.
     * @param location      The {@link Point} where the {@link PaintBlock} will be painted.
     */
    public PaintBlock(DynamicString dynamicString, Font font, Color textColor, Point location) {
        this.dynamicString = dynamicString;
        this.font = font;
        this.textColor = textColor;
        this.location = location;
    }

    /**
     * Gets the {@link DynamicString} to be painted inside the {@link PaintBlock}
     *
     * @return The {@link DynamicString}.
     */
    public DynamicString getDynamicString() {
        return this.dynamicString;
    }

    /**
     * Gets the {@link Font} of the {@link PaintBlock}.
     *
     * @return The {@link Font}.
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Gets the {@link Color} of the text.
     *
     * @return The text {@link Color}.
     */
    public Color getTextColor() {
        return this.textColor;
    }

    /**
     * Gets the {@link Point} where the {@link PaintBlock} will be painted at.
     *
     * @return The {@link Point}.
     */
    public Point getLocation() {
        return this.location;
    }

    /**
     * Gets the {@link String} returned by the {@link PaintBlock}'s {@link DynamicString}.
     *
     * @return The {@link String} returned by the {@link PaintBlock}'s {@link DynamicString}.
     */
    public String getString() {
        return this.dynamicString.getString();
    }

    /**
     * Constructs a {@link Rectangle} that fully encapsulates the {@link DynamicString}.
     *
     * This {@link Rectangle} is generated dynamically each time this method is called.
     *
     * @param g The {@link Graphics} to render with.
     * @return A {@link Rectangle} that fully encapsulates the {@link DynamicString}.
     */
    public Rectangle getArea(Graphics g) {
        Rectangle rectangle = g.getFontMetrics(getFont()).getStringBounds(getString(), g).getBounds();
        rectangle.setLocation(getLocation());
        rectangle.setSize(rectangle.width + (getFont().getSize() / 3), rectangle.height);
        return rectangle;
    }

    /**
     * Sets the {@link PaintBlock}'s {@link DynamicString}.
     *
     * @param dynamicString The {@link DynamicString} to set.
     */
    public void setDynamicString(DynamicString dynamicString) {
        this.dynamicString = dynamicString;
    }

    /**
     * Sets the {@link Font} of the {@link PaintBlock}.
     *
     * @param font The {@link Font} to set.
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Sets the {@link Color} of the text.
     *
     * @param textColor The text {@link Color} to set.
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    /**
     * Sets the location of the {@link PaintBlock}.
     *
     * @param location The {@link Point} that will be the {@link PaintBlock}'s location.
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Checks to see if the specified {@link Point} is contained within the {@link Rectangle} returned by
     * {@link #getArea(Graphics)}.
     *
     * @param g     The {@link Graphics} to render with.
     * @param point The {@link Point} to check.
     * @return True if the {@link Point} is contained within the {@link Rectangle} returned by {@link #getArea(Graphics)},
     * false otherwise.
     */
    public boolean contains(Graphics g, Point point) {
        return getArea(g).contains(point);
    }

    /**
     * Paints the {@link PaintBlock}.
     *
     * The {@link PaintBlock} consists of the following:
     * <ul>
     * <li>A {@link DynamicString}</li>
     * <li>A {@link Rectangle} that encapsulates the {@link DynamicString}</li>
     * <li>A {@link Rectangle} that covers the background of the {@link PaintBlock}</li>
     * </ul>
     *
     * The {@link Color} of the border around the {@link PaintBlock} is set to be {@link Color#BLACK} by default.
     * The {@link Color} of the background of the {@link PaintBlock} is set to be a {@link Color} with sRGB components
     * equal to (0, 0, 0, 190).
     *
     * @param g The {@link Graphics} to render with.
     */
    public void paint(Graphics2D g) {
        paint(g, Color.BLACK, new Color(0, 0, 0, 200));
    }

    /**
     * Paints the {@link PaintBlock}.
     *
     * The {@link PaintBlock} consists of the following:
     * <ul>
     * <li>A {@link DynamicString}</li>
     * <li>A {@link Rectangle} that encapsulates the {@link DynamicString}</li>
     * <li>A {@link Rectangle} that covers the background of the {@link PaintBlock}</li>
     * </ul>
     *
     * The {@link Color} of the border around the {@link PaintBlock} is set to be {@link Color#BLACK} by default.
     * The {@link Color} of the background of the {@link PaintBlock} is set to be a {@link Color} with sRGB components
     * equal to (0, 0, 0, 190).
     *
     * @param g               The {@link Graphics} to render with.
     * @param borderColor     The border {@link Color} of the {@link Rectangle} that encapsulates the {@link DynamicString}.
     * @param backgroundColor The background {@link Color} of the {@link Rectangle} that encapsulates the {@link DynamicString}.
     */
    public void paint(Graphics2D g, Color borderColor, Color backgroundColor) {
        Rectangle rectangle = getArea(g);
        g.setColor(backgroundColor);
        g.fill(rectangle);
        //g.setColor(borderColor);
        //g.draw(rectangle);
        g.setColor(getTextColor());
        g.setFont(getFont());
        g.drawString(getString(), rectangle.x + 2, rectangle.y + g.getFontMetrics().getAscent());
    }
}
