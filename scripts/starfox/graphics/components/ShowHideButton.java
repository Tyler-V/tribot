package scripts.starfox.graphics.components;

import scripts.starfox.graphics.GraphicsUtil;

import java.awt.*;

/**
 * @author Nolan
 */
public class ShowHideButton
        extends PaintButton {

    /**
     * The default rectangle for the button.
     */
    public static final Rectangle DEFAULT_RECTANGLE = new Rectangle(482, 348, 10, 10);

    /**
     * The default show color for the button.
     */
    public static final Color DEFAULT_SHOW_COLOR = new Color(50, 205, 50, 200);

    /**
     * The default hide color for the button.
     */
    public static final Color DEFAULT_HIDE_COLOR = new Color(225, 20, 50, 200);

    private final Color show;
    private final Color hide;

    /**
     * Constructs a new {@link ShowHideButton}.
     *
     * The area of the {@link PaintButton} will be set to {@link ShowHideButton#DEFAULT_RECTANGLE} by default.
     * The show {@link Color} of the {@link PaintButton} will be set to {@link ShowHideButton#DEFAULT_SHOW_COLOR} by
     * default.
     * The hide {@link Color} of the {@link PaintButton} will be set to {@link ShowHideButton#DEFAULT_HIDE_COLOR} by
     * default.
     */
    public ShowHideButton() {
        this(DEFAULT_RECTANGLE, DEFAULT_SHOW_COLOR, DEFAULT_HIDE_COLOR);
    }

    /**
     * Constructs a new {@link ShowHideButton}.
     *
     * @param rectangle The {@link Rectangle}.
     * @param show      The {@link Color} of the {@link PaintButton} when the paint should be showing.
     * @param hide      The {@link Color} of the {@link PaintButton} when the paint should be hidden.
     */
    public ShowHideButton(Rectangle rectangle, Color show, Color hide) {
        super(rectangle);
        this.show = show;
        this.hide = hide;
    }

    /**
     * Gets the show {@link Color}.
     *
     * @return The show {@link Color}.
     */
    public Color getShowColor() {
        return this.show;
    }

    /**
     * Gets the hide {@link Color}.
     *
     * @return The hide {@link Color}.
     */
    public Color getHideColor() {
        return this.hide;
    }

    /**
     * Gets the display {@link Color}.
     *
     * @return The display {@link Color}.
     */
    public Color getDisplayColor() {
        return isPressed() ? getShowColor() : getHideColor();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = GraphicsUtil.create2D(g);
        g2.setColor(getDisplayColor());
        g2.fill(getArea());
        g2.setColor(Color.BLACK);
        g2.draw(getArea());
        g2.dispose();
    }
}
