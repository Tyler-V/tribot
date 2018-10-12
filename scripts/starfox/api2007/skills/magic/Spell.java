package scripts.starfox.api2007.skills.magic;

import scripts.starfox.api2007.skills.magic.data.MagicBook;
import scripts.starfox.api2007.skills.magic.data.SpellType;
import scripts.starfox.api2007.skills.magic.items.Rune;

/**
 * @author Nolan
 */
public interface Spell {

    /**
     * Gets the name of the spell.
     *
     *
     * @return The name of the spell.
     */
    String getName();

    /**
     * Gets the required magic level to cast the spell.
     *
     *
     * @return The required magic level.
     */
    int getRequiredLevel();

    /**
     * Gets the required runes to cast the spell.
     *
     *
     * @return The required runes.
     */
    Rune[] getRequiredRunes();

    /**
     * Gets the number of each rune required to cast the spell.
     *
     * This method returns an array in which each element represents the amount of each rune required to cast the spell.
     *
     *
     * @return The number of each rune required.
     */
    int[] getNumberOfRunes();

    /**
     * Gets the spell type.
     *
     *
     * @return The spell type.
     */
    SpellType getType();

    /**
     * Gets the book that the spell belongs to.
     *
     *
     * @return The book.
     */
    MagicBook getBook();
}
