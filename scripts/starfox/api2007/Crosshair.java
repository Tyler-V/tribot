package scripts.starfox.api2007;

import scripts.starfox.api.util.Strings;

/**
 * Created by Nolan on 10/21/2015.
 */
public enum Crosshair {

    NONE(0),
    YELLOW(1),
    RED(2);

    private final int value;

    /**
     * Constructs a new {@link Crosshair}.
     *
     * @param value The value for the {@link Crosshair}.
     */
    Crosshair(int value) {
        this.value = value;
    }

    /**
     * Gets the value for the {@link Crosshair}.
     * @return The value.
     */
    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Strings.enumToString(name());
    }
}
