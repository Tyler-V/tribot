package scripts.magic.data;

/**
 * The Pie enum holds constants that represent pies that can be cooked with the bake pie spell.
 *
 *
 * @author Nolan
 */
public enum Pie {

    REDBERRY("Redberry pie", 10),
    MEAT("Meat pie", 20),
    MUD("Mud pie", 29),
    APPLE("Apple pie", 30),
    GARDEN("Garden pie", 34),
    FISH("Fish pie", 47),
    ADMIRAL("Admiral pie", 70),
    WILD("Wild pie", 85),
    SUMMER("Summer pie", 95);

    private final String name;
    private final int requiredLevel;

    /**
     * Constructs a new Pie.
     *
     * @param name          The name of the pie.
     * @param requiredLevel The required cooking level to bake the pie.
     */
    Pie(String name, int requiredLevel) {
        this.name = name;
        this.requiredLevel = requiredLevel;
    }

    /**
     * Gets the name of the pie.
     *
     * @return The name of the pie.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the name of the raw version of the pie.
     *
     * @return The name of the raw version of the pie.
     */
    public final String getRawName() {
        return "Raw " + getName().toLowerCase();
    }

    /**
     * Gets the required cooking level to bake the pie.
     *
     * @return The required cooking level.
     */
    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public static Pie forName(String name) {
        for (Pie pie : values()) {
            if (pie.getName().equals(name)) {
                return pie;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
