package scripts.starfox.api2007.skills.magic.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.tribot.api2007.Equipment;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Strings;
import scripts.starfox.api2007.skills.magic.Spell;

/**
 * The Staff enum holds constants representing different staves the player can wield that provide an unlimited amount of
 * certain runes when wielded.
 *
 *
 * @author Starfox
 */
public enum Staff implements Serializable {

    STAFF_OF_AIR("Staff of air", 1381, new Rune[]{Rune.AIR}),
    AIR_BATTLESTAFF("Air battlestaff", 1397, new Rune[]{Rune.AIR}),
    MYSTIC_AIR_STAFF("Mystic air staff", 1405, new Rune[]{Rune.AIR}),
    STAFF_OF_WATER("Staff of water", 1383, new Rune[]{Rune.WATER}),
    WATER_BATTLESTAFF("Water battlestaff", 1395, new Rune[]{Rune.WATER}),
    MYSTIC_WATER_STAFF("Mystic water staff", 1403, new Rune[]{Rune.WATER}),
    STAFF_OF_EARTH("Staff of earth", 1385, new Rune[]{Rune.EARTH}),
    EARTH_BATTLESTAFF("Earth battlestaff", 1399, new Rune[]{Rune.EARTH}),
    MYSTIC_EARTH_STAFF("Mystic earth staff", 1407, new Rune[]{Rune.EARTH}),
    STAFF_OF_FIRE("Staff of fire", 1387, new Rune[]{Rune.FIRE}),
    FIRE_BATTLESTAFF("Fire battlestaff", 1393, new Rune[]{Rune.FIRE}),
    MYSTIC_FIRE_STAFF("Mystic fire staff", 1401, new Rune[]{Rune.FIRE}),
    LAVA_BATTLESTAFF("Lava battlestaff", 3053, new Rune[]{Rune.FIRE, Rune.EARTH}),
    MYSTIC_LAVA_STAFF("Mystic lava staff", 3054, new Rune[]{Rune.FIRE, Rune.EARTH}),
    MUD_BATTLESTAFF("Mud battlestaff", 6562, new Rune[]{Rune.WATER, Rune.EARTH}),
    MYSTIC_MUD_STAFF("Mystic mud staff", 6563, new Rune[]{Rune.WATER, Rune.EARTH}),
    STEAM_BATTLESTAFF("Steam battlestaff", 11787, new Rune[]{Rune.WATER, Rune.FIRE}),
    MYSTIC_STEAM_STAFF("Mystic steam staff", 11789, new Rune[]{Rune.WATER, Rune.FIRE}),
    STEAM_BATTLESTAFF_U("Steam battlestaff", 12795, new Rune[]{Rune.WATER, Rune.FIRE}),
    MYSTIC_STEAM_STAFF_U("Mystic steam staff", 12796, new Rune[]{Rune.WATER, Rune.FIRE}),
    SMOKE_BATTLESTAFF("Smoke battlestaff", 11998, new Rune[]{Rune.FIRE, Rune.AIR}),
    MYSTIC_SMOKE_STAFF("Mystic smoke staff", 12000, new Rune[]{Rune.FIRE, Rune.AIR}),
    TOME_OF_FIRE("Tome of fire", 20714, new Rune[]{Rune.FIRE});

    private final String name;
    private final int id;
    private final Rune[] runes;

    /**
     * Constructs a new Staff.
     *
     *
     * @param name  The name of the staff.
     * @param id    The ID of the staff.
     * @param runes The runes that the staff provides.
     */
    Staff(String name, int id, Rune[] runes) {
        this.name = name;
        this.id = id;
        this.runes = runes;
    }

    /**
     * Gets the name of the staff.
     *
     *
     * @return The name of the staff.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the ID of the staff.
     *
     *
     * @return The ID of the staff.
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Gets the runes that the staff provides.
     *
     *
     * @return The runes that the staff provides.
     */
    public final Rune[] getRunes() {
        return this.runes;
    }

    /**
     * Checks to see if the local player has the staff equipped.
     *
     *
     * @return True if the staff is equipped, false otherwise.
     */
    public final boolean isEquipped() {
        return Equipment.isEquipped(getId());
    }

    /**
     * Checks to see if the staff provides the specified rune.
     *
     *
     * @param rune The rune to check.
     * @return True if the staff provides the rune, false otherwise.
     */
    public final boolean providesRune(Rune rune) {
        return ArrayUtil.contains(rune, getRunes());
    }

    /**
     * Gets the staff that the player has equipped.
     *
     *
     * @return The staff that is equipped.
     * Null if no staff is equipped.
     */
    public static Staff getEquipped() {
        for (Staff staff : values()) {
            if (Equipment.isEquipped(staff.getId())) {
                return staff;
            }
        }
        return null;
    }

    /**
     * Gets any staves that provide any of the specified runes.
     *
     *
     * @param runes The runes.
     * @return The staves that provide any of the specified runes.
     */
    public static Staff[] forRunes(Rune... runes) {
        List<Staff> staves = new ArrayList<>();
        for (Staff staff : Staff.values()) {
            for (Rune r : runes) {
                if (staff.providesRune(r)) {
                    staves.add(staff);
                    break;
                }
            }
        }
        return staves.toArray(new Staff[staves.size()]);
    }

    /**
     * Gets any staves that provide runes that are required to cast the specified spell.
     *
     *
     * @param spell The spell.
     * @return The staves that provide runes for the specified spell.
     */
    public static Staff[] forSpell(Spell spell) {
        return forRunes(spell.getRequiredRunes());
    }

    @Override
    public String toString() {
        return Strings.capitalizeFirst(Strings.replaceUnderscores(name().toLowerCase()));
    }
}
