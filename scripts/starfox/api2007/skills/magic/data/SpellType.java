package scripts.starfox.api2007.skills.magic.data;

import java.io.Serializable;
import scripts.starfox.api.util.Strings;

/**
 * The SpellType enum holds constants representing different types of spells.
 *
 * @author Nolan
 */
public enum SpellType implements Serializable {

    ALCHEMY,
    BOLT_ENCHANTMENT,
    BONES_TO_FRUIT,
    COMBAT,
    CURSE,
    ENCHANTMENT,
    SUPERHEAT_ITEM,
    TELEKINETIC_GRAB,
    TELE_OTHER,
    TELEPORT,
    NONE,
    UNKNOWN;

    @Override
    public String toString() {
        return Strings.enumToString(name());
    }
}
