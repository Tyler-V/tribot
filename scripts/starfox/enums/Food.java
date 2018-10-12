package scripts.starfox.enums;

import scripts.starfox.api.util.Strings;

/**
 * Created by nolan on 11/7/2016.
 */
public enum Food {

    SHRIMP(315, "Shrimps", 3),
    CHICKEN(2140, "Cooked chicken", 3),
    RABBIT(0, "Rabbit", 3);

    private final int id;
    private final String name;
    private final int heal_amount;

    /**
     * Constructs a new Food.
     *
     * @param id          The ID of the food.
     * @param name        The name of the food.
     * @param heal_amount The amount that the food heals.
     */
    Food(final int id, final String name, final int heal_amount) {
        this.id = id;
        this.name = name;
        this.heal_amount = heal_amount;
    }

    /**
     * Gets the ID of the food.
     *
     * @return The ID of the food.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the name of the food.
     *
     * @return The name of the food.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the amount of hitpoints that the food heals.
     *
     * @return The amount of hitpoints that the food heals.
     */
    public int getHealAmount() {
        return this.heal_amount;
    }

    @Override
    public String toString() {
        return Strings.enumToString(name());
    }
}
