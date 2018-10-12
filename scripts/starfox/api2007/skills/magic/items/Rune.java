package scripts.starfox.api2007.skills.magic.items;

import java.io.Serializable;

/**
 * The Rune enum holds constants representing runes that are required to cast spells.
 *
 * @author Starfox
 */
public enum Rune implements Serializable {

    AIR("Air rune", 556),
    FIRE("Fire rune", 554),
    WATER("Water rune", 555),
    EARTH("Earth rune", 557),
    MIND("Mind rune", 558),
    BODY("Body rune", 559),
    COSMIC("Cosmic rune", 564),
    CHAOS("Chaos rune", 562),
    NATURE("Nature rune", 561),
    LAW("Law rune", 563),
    ASTRAL("Astral rune", 9075),
    SOUL("Soul rune", 566),
    BLOOD("Blood rune", 565),
    DEATH("Death rune", 560);

    private final String name;
    private final int id;

    /**
     * Constructs a new Rune.
     *
     * @param name The name of the rune.
     * @param id   The ID of the rune.
     */
    private Rune(String name, int id) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the name of the Rune.
     *
     * @return The name of the Rune.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the ID of the Rune.
     *
     * @return The ID of the Rune.
     */
    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
