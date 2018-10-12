package scripts.starfox.api2007.skills.magic.books;

import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.api2007.skills.magic.data.MagicBook;
import scripts.starfox.api2007.skills.magic.data.SpellType;
import scripts.starfox.api2007.skills.magic.items.Rune;
import scripts.starfox.interfaces.ui.Listable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Constants that represent spells from the Lunar spell book.
 *
 * @author Nolan
 */
public enum LunarSpell
    implements Spell, Serializable, Listable {

    //TELEPORT SPELLS
    MOONCLAN_TELEPORT("Moonclan Teleport", 69, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.EARTH}, new int[]{1, 2, 2}),
    TELE_GROUP_MOONCLAN("Tele Group Moonclan", 70, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.EARTH}, new int[]{1, 2, 4}),
    WATERBIRTH_TELEPORT("Waterbirth Teleport", 72, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{1, 2, 1}),
    TELE_GROUP_WATERBIRTH("Tele Group Waterbirth", 73, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{1, 2, 5}),
    BARBARIAN_TELEPORT("Barbarian Teleport", 75, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.FIRE}, new int[]{2, 2, 3}),
    TELE_GROUP_BARBARIAN("Tele Group Barbarian", 76, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.FIRE}, new int[]{2, 2, 6}),
    KHAZARD_TELEPORT("Khazard Teleport", 78, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{2, 2, 4}),
    TELE_GROUP_KHAZARD("Tele Group Khazard", 79, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{2, 2, 8}),
    FISHING_GUILD_TELEPORT("Fishing Guild Teleport", 85, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{3, 3, 10}),
    TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild", 86, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{3, 3, 14}),
    CATHERBY_TELEPORT("Catherby Teleport", 87, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{3, 3, 10}),
    TELE_GROUP_CATHERBY("Tele Group Catherby", 88, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{3, 3, 15}),
    ICE_PLATEAU_TELEPORT("Ice Plateau Teleport", 89, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{3, 3, 8}),
    TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau", 90, new Rune[]{Rune.LAW, Rune.ASTRAL, Rune.WATER}, new int[]{3, 3, 16}),
    //OTHER SPELLS
    BAKE_PIE("Bake Pie", 65, new Rune[]{Rune.ASTRAL, Rune.FIRE, Rune.WATER}, new int[]{1, 5, 4}),
    HUMIDIFY("Humidify", 68, new Rune[]{Rune.ASTRAL, Rune.FIRE, Rune.WATER}, new int[]{1, 1, 3}),
    HUNTER_KIT("Hunter Kit", 71, new Rune[]{Rune.ASTRAL, Rune.EARTH}, new int[]{2, 3}),
    SUPERGLASS_MAKE("Superglass Make", 77, new Rune[]{Rune.ASTRAL, Rune.FIRE, Rune.AIR}, new int[]{2, 6, 10}),
    STRING_JEWELRY("String Jewellery", 80, new Rune[]{Rune.ASTRAL, Rune.EARTH, Rune.WATER}, new int[]{2, 10, 5}),
    PLANK_MAKE("Plank Make", 86, new Rune[]{Rune.NATURE, Rune.ASTRAL, Rune.EARTH}, new int[]{1, 2, 15});

    private final String name;
    private final int requiredLevel;
    private final Rune[] requiredRunes;
    private final int[] numberOfRunes;

    /**
     * Constructs a new LunarSpell.
     *
     * @param name          The name of the lunar spell. This matches the name on the spell interface.
     * @param requiredLevel The required magic level to cast the spell.
     * @param requiredRunes The runes required to cast the spell.
     * @param numberOfRunes The amount of runes required to cast the spell.
     */
    private LunarSpell(final String name, int requiredLevel, Rune[] requiredRunes, int[] numberOfRunes) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.requiredRunes = requiredRunes;
        this.numberOfRunes = numberOfRunes;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    @Override
    public final Rune[] getRequiredRunes() {
        return this.requiredRunes;
    }

    @Override
    public final int[] getNumberOfRunes() {
        return this.numberOfRunes;
    }
    
    @Override
    public SpellType getType() {
        return SpellType.NONE;
    }

    @Override
    public MagicBook getBook() {
        return MagicBook.LUNAR;
    }
    
    /**
     * Gets an array of spells that have a spell type that matches one of the specified spell types.
     *
     * @param spellTypes The spell types to match.
     * @return An array of spells.
     */
    public static LunarSpell[] forType(SpellType... spellTypes) {
        ArrayList<LunarSpell> spells = new ArrayList<>();
        for (LunarSpell spell : values()) {
            if (ArrayUtil.contains(spell, spellTypes)) {
                spells.add(spell);
            }
        }
        return spells.toArray(new LunarSpell[spells.size()]);
    }

    public static LunarSpell forName(String name) {
        for (LunarSpell spell : values()) {
            if (spell.toString().equals(name)) {
                return spell;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getListDisplay() {
        return getName();
    }

    @Override
    public String searchName() {
        return getName();
    }

    @Override
    public String getPulldownDisplay() {
        return getName();
    }
}
