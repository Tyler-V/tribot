package scripts.starfox.graphics.colors;

import scripts.starfox.api.util.Strings;

import java.awt.*;

/**
 * Created by Nolan on 10/15/2015.
 */
public enum Colors {

    CADMIUM_YELLOW(new Color(255, 153, 18)),
    CRIMSON(new Color(220, 20, 60)),
    GOLDENROD(new Color(255, 193, 37)),
    ORCHID(new Color(255, 131, 250)),
    PINK(new Color(255,	192, 203)),
    ROYAL_BLUE(new Color(65, 105, 225)),
    VIOLET(new Color(148, 0, 211));

    private final Color color;

    /**
     * Constructs a new {@link Colors}.
     *
     * @param color The {@link Color} for the constant.
     */
    Colors(Color color) {
        this.color = color;
    }

    /**
     * Gets the {@link Color} for the constant.
     *
     * @return The {@link Color}.
     */
    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return Strings.enumToString(name()) + getColor();
    }
}
