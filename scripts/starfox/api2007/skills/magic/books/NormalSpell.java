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
 * Constants that represent spells from the normal magic book.
 *
 * @author Starfox
 */
public enum NormalSpell
        implements Spell, Serializable, Listable {

    //COMBAT SPELLS
    WIND_STRIKE("Wind Strike", 1, new Rune[]{Rune.MIND, Rune.AIR}, new int[]{1, 1}, SpellType.COMBAT),
    WATER_STRIKE("Water Strike", 5, new Rune[]{Rune.MIND, Rune.WATER, Rune.AIR}, new int[]{1, 1, 1}, SpellType.COMBAT),
    EARTH_STRIKE("Earth Strike", 9, new Rune[]{Rune.MIND, Rune.EARTH, Rune.AIR}, new int[]{1, 2, 1}, SpellType.COMBAT),
    FIRE_STRIKE("Fire Strike", 13, new Rune[]{Rune.MIND, Rune.FIRE, Rune.AIR}, new int[]{1, 3, 2}, SpellType.COMBAT),
    WIND_BOLT("Wind Bolt", 17, new Rune[]{Rune.CHAOS, Rune.AIR}, new int[]{1, 2}, SpellType.COMBAT),
    WATER_BOLT("Water Bolt", 23, new Rune[]{Rune.CHAOS, Rune.WATER, Rune.AIR}, new int[]{1, 2, 2}, SpellType.COMBAT),
    EARTH_BOLT("Earth Bolt", 29, new Rune[]{Rune.CHAOS, Rune.EARTH, Rune.AIR}, new int[]{1, 3, 2}, SpellType.COMBAT),
    FIRE_BOLT("Fire Bolt", 23, new Rune[]{Rune.CHAOS, Rune.FIRE, Rune.AIR}, new int[]{1, 4, 3}, SpellType.COMBAT),
    CRUMBLE_UNDEAD("Crumble Undead", 39, new Rune[]{Rune.CHAOS, Rune.EARTH, Rune.AIR}, new int[]{1, 2, 2}, SpellType.COMBAT),
    WIND_BLAST("Wind Blast", 41, new Rune[]{Rune.DEATH, Rune.AIR}, new int[]{1, 3}, SpellType.COMBAT),
    WATER_BLAST("Water Blast", 47, new Rune[]{Rune.DEATH, Rune.WATER, Rune.AIR}, new int[]{1, 3, 3}, SpellType.COMBAT),
    IBAN_BLAST("Iban Blast", 50, new Rune[]{Rune.DEATH, Rune.FIRE}, new int[]{1, 5}, SpellType.COMBAT),
    MAGIC_DART("Magic Dart", 50, new Rune[]{Rune.DEATH, Rune.MIND}, new int[]{1, 4}, SpellType.COMBAT),
    EARTH_BLAST("Earth Blast", 53, new Rune[]{Rune.DEATH, Rune.EARTH, Rune.AIR}, new int[]{1, 4, 3}, SpellType.COMBAT),
    FIRE_BLAST("Fire Blast", 59, new Rune[]{Rune.DEATH, Rune.FIRE, Rune.AIR}, new int[]{1, 5, 4}, SpellType.COMBAT),
    WIND_WAVE("Wind Wave", 62, new Rune[]{Rune.BLOOD, Rune.AIR}, new int[]{1, 5}, SpellType.COMBAT),
    WATER_WAVE("Water Wave", 65, new Rune[]{Rune.BLOOD, Rune.WATER, Rune.AIR}, new int[]{1, 7, 5}, SpellType.COMBAT),
    EARTH_WAVE("Earth Wave", 70, new Rune[]{Rune.BLOOD, Rune.EARTH, Rune.AIR}, new int[]{1, 7, 5}, SpellType.COMBAT),
    FIRE_WAVE("Fire Wave", 75, new Rune[]{Rune.BLOOD, Rune.FIRE, Rune.AIR}, new int[]{1, 7, 5}, SpellType.COMBAT),
    //CURSE SPELLS
    CONFUSE("Confuse", 3, new Rune[]{Rune.BODY, Rune.EARTH, Rune.WATER}, new int[]{1, 2, 3}, SpellType.CURSE),
    WEAKEN("Weaken", 11, new Rune[]{Rune.BODY, Rune.EARTH, Rune.WATER}, new int[]{1, 2, 3}, SpellType.CURSE),
    CURSE("Curse", 19, new Rune[]{Rune.BODY, Rune.EARTH, Rune.WATER}, new int[]{1, 3, 2}, SpellType.CURSE),
    VULNERABILITY("Vulnerability", 66, new Rune[]{Rune.SOUL, Rune.EARTH, Rune.WATER}, new int[]{1, 5, 5}, SpellType.CURSE),
    ENFEEBLE("Enfeeble", 73, new Rune[]{Rune.SOUL, Rune.EARTH, Rune.WATER}, new int[]{1, 8, 8}, SpellType.CURSE),
    STUN("Stun", 80, new Rune[]{Rune.SOUL, Rune.EARTH, Rune.WATER}, new int[]{1, 12, 12}, SpellType.CURSE),
    BIND("Bind", 20, new Rune[]{Rune.NATURE, Rune.EARTH, Rune.WATER}, new int[]{2, 3, 3}, SpellType.CURSE),
    SNARE("Snare", 50, new Rune[]{Rune.NATURE, Rune.EARTH, Rune.WATER}, new int[]{3, 4, 4}, SpellType.CURSE),
    ENTANGLE("Entangle", 79, new Rune[]{Rune.NATURE, Rune.EARTH, Rune.WATER}, new int[]{4, 5, 4}, SpellType.CURSE),
    //TELEPORT SPELLS
    LUMBRIDGE_HOME_TELEPORT("Lumbridge Home Teleport", 0, new Rune[]{}, new int[]{}, SpellType.TELEPORT),
    VARROCK_TELEPORT("Varrock Teleport", 25, new Rune[]{Rune.LAW, Rune.AIR, Rune.FIRE}, new int[]{1, 3, 1}, SpellType.TELEPORT),
    LUMBRIDGE_TELEPORT("Lumbridge Teleport", 31, new Rune[]{Rune.LAW, Rune.AIR, Rune.EARTH}, new int[]{1, 3, 1}, SpellType.TELEPORT),
    FALADOR_TELEPORT("Falador Teleport", 37, new Rune[]{Rune.LAW, Rune.AIR, Rune.WATER}, new int[]{1, 3, 1}, SpellType.TELEPORT),
    HOUSE_TELEPORT("Teleport to House", 40, new Rune[]{Rune.LAW, Rune.EARTH, Rune.AIR}, new int[]{1, 1, 1}, SpellType.TELEPORT),
    CAMELOT_TELEPORT("Camelot Teleport", 45, new Rune[]{Rune.LAW, Rune.AIR}, new int[]{1, 5}, SpellType.TELEPORT),
    ARDOUGNE_TELEPORT("Ardougne Teleport", 51, new Rune[]{Rune.LAW, Rune.WATER}, new int[]{2, 2}, SpellType.TELEPORT),
    WATCHTOWER_TELEPORT("Watchtower Teleport", 58, new Rune[]{Rune.LAW, Rune.EARTH}, new int[]{2, 2}, SpellType.TELEPORT),
    TROLLHEIM_TELEPORT("Trollheim Teleport", 61, new Rune[]{Rune.LAW, Rune.FIRE}, new int[]{2, 2}, SpellType.TELEPORT),
    //TELEOTHER SPELLS
    TELE_OTHER_LUMBRIDGE("Teleother Lumbridge", 74, new Rune[]{Rune.SOUL, Rune.LAW, Rune.EARTH}, new int[]{1, 1, 1}, SpellType.TELE_OTHER),
    TELE_OTHER_FALADOR("Teleother Falador", 82, new Rune[]{Rune.SOUL, Rune.LAW, Rune.WATER}, new int[]{1, 1, 1}, SpellType.TELE_OTHER),
    TELE_OTHER_CAMELOT("Teleother Camelot", 74, new Rune[]{Rune.SOUL, Rune.LAW}, new int[]{2, 1}, SpellType.TELE_OTHER),
    //TELEKINETIC GRAB
    TELEKINETIC_GRAB("Telekinetic Grab", 31, new Rune[]{Rune.LAW, Rune.AIR}, new int[]{1, 1}, SpellType.TELEKINETIC_GRAB),
    //ALCHEMY SPELLS
    LOW_ALCHEMY("Low Level Alchemy", 21, new Rune[]{Rune.NATURE, Rune.FIRE}, new int[]{1, 3}, SpellType.ALCHEMY),
    HIGH_ALCHEMY("High Level Alchemy", 55, new Rune[]{Rune.NATURE, Rune.FIRE}, new int[]{1, 5}, SpellType.ALCHEMY),
    //BONE SPELLS
    BONES_TO_BANANAS("Bones to Bananas", 15, new Rune[]{Rune.NATURE, Rune.EARTH, Rune.WATER}, new int[]{1, 2, 2}, SpellType.BONES_TO_FRUIT),
    BONES_TO_PEACHES("Bones to Peaches", 60, new Rune[]{Rune.NATURE, Rune.EARTH, Rune.WATER}, new int[]{2, 4, 4}, SpellType.BONES_TO_FRUIT),
    //SUPERHEAT
    SUPERHEAT_ITEM("Superheat Item", 43, new Rune[]{Rune.NATURE, Rune.FIRE}, new int[]{1, 4}, SpellType.SUPERHEAT_ITEM),
    //ENCHANTMENT SPELLS
    ENCHANT_LEVEL_ONE("Lvl-1 Enchant", 7, new Rune[]{Rune.COSMIC, Rune.WATER}, new int[]{1, 1}, SpellType.ENCHANTMENT),
    ENCHANT_LEVEL_TWO("Lvl-2 Enchant", 27, new Rune[]{Rune.COSMIC, Rune.AIR}, new int[]{1, 3}, SpellType.ENCHANTMENT),
    ENCHANT_LEVEL_THREE("Lvl-3 Enchant", 49, new Rune[]{Rune.COSMIC, Rune.FIRE}, new int[]{1, 5}, SpellType.ENCHANTMENT),
    ENCHANT_LEVEL_FOUR("Lvl-4 Enchant", 57, new Rune[]{Rune.COSMIC, Rune.EARTH}, new int[]{1, 10}, SpellType.ENCHANTMENT),
    ENCHANT_LEVEL_FIVE("Lvl-5 Enchant", 68, new Rune[]{Rune.COSMIC, Rune.EARTH, Rune.WATER}, new int[]{1, 15, 15}, SpellType.ENCHANTMENT),
    ENCHANT_LEVEL_SIX("Lvl-6 Enchant", 87, new Rune[]{Rune.COSMIC, Rune.FIRE, Rune.EARTH}, new int[]{1, 20, 20}, SpellType.ENCHANTMENT),
    //BOLT ENCHANTMENT SPELLS
    OPAL_BOLT_ENCHANT("Opal bolt enchant", 4, new Rune[]{Rune.COSMIC, Rune.AIR}, new int[]{1, 2}, SpellType.BOLT_ENCHANTMENT),
    SAPPHIRE_BOLT_ENCHANT("Sapphire bolt enchant", 7, new Rune[]{Rune.COSMIC, Rune.WATER, Rune.MIND}, new int[]{1, 1, 1}, SpellType.BOLT_ENCHANTMENT),
    JADE_BOLT_ENCHANT("Jade bolt enchant", 14, new Rune[]{Rune.COSMIC, Rune.EARTH}, new int[]{1, 2}, SpellType.BOLT_ENCHANTMENT),
    PEARL_BOLT_ENCHANT("Pearl bolt enchant", 24, new Rune[]{Rune.COSMIC, Rune.WATER}, new int[]{1, 2}, SpellType.BOLT_ENCHANTMENT),
    EMERALD_BOLT_ENCHANT("Emerald bolt enchant", 27, new Rune[]{Rune.COSMIC, Rune.NATURE, Rune.AIR}, new int[]{1, 1, 3}, SpellType.BOLT_ENCHANTMENT),
    TOPAZ_BOLT_ENCHANT("Topaz bolt enchant", 29, new Rune[]{Rune.COSMIC, Rune.FIRE}, new int[]{1, 2}, SpellType.BOLT_ENCHANTMENT),
    RUBY_BOLT_ENCHANT("Ruby bolt enchant", 49, new Rune[]{Rune.COSMIC, Rune.BLOOD, Rune.FIRE}, new int[]{1, 1, 5}, SpellType.BOLT_ENCHANTMENT),
    DIAMOND_BOLT_ENCHANT("Diamond bolt enchant", 57, new Rune[]{Rune.COSMIC, Rune.LAW, Rune.EARTH}, new int[]{1, 2, 10}, SpellType.BOLT_ENCHANTMENT),
    DRAGONSTONE_BOLT_ENCHANT("Dragonstone bolt enchant", 68, new Rune[]{Rune.COSMIC, Rune.SOUL, Rune.EARTH}, new int[]{1, 1, 15}, SpellType.BOLT_ENCHANTMENT),
    ONYX_BOLT_ENCHANT("Onyx bolt enchant", 87, new Rune[]{Rune.COSMIC, Rune.DEATH, Rune.FIRE}, new int[]{1, 1, 20}, SpellType.BOLT_ENCHANTMENT),
    //CHARGE SPELLS
    //CHARGE_AIR_ORB("Charge Air Orb", 66, new Rune[]{Rune.COSMIC, Rune.AIR}, new int[]{3, 30}, SpellType.UNKNOWN),
    //COMBO SPELLS
    CURSE_ALCHEMY("Curse/Alchemy", 0, new Rune[]{}, new int[]{}, SpellType.UNKNOWN);

    private final String name;
    private final int requiredLevel;
    private final Rune[] requiredRunes;
    private final int[] numberOfRunes;
    private final SpellType type;

    /**
     * Constructs a new NormalSpell.
     *
     * @param name          The name of the spell. This matches the name on the spell interface.
     * @param requiredLevel The magic level required to cast the spell.
     * @param requiredRunes The runes required to cast the spell.
     * @param numberOfRunes The amount of each rune required to cast the spell.
     */
    NormalSpell(String name, int requiredLevel, Rune[] requiredRunes, int[] numberOfRunes, SpellType type) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.requiredRunes = requiredRunes;
        this.numberOfRunes = numberOfRunes;
        this.type = type;
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
    public final SpellType getType() {
        return this.type;
    }

    @Override
    public final MagicBook getBook() {
        return MagicBook.NORMAL;
    }

    /**
     * Gets an array of spells that have a spell type that matches one of the specified spell types.
     *
     * @param spellTypes The spell types to match.
     * @return An array of spells.
     */
    public static NormalSpell[] forType(SpellType... spellTypes) {
        ArrayList<NormalSpell> spells = new ArrayList<>();
        for (NormalSpell spell : values()) {
            if (ArrayUtil.contains(spell, spellTypes)) {
                spells.add(spell);
            }
        }
        return spells.toArray(new NormalSpell[spells.size()]);
    }

    public static NormalSpell forName(String name) {
        for (NormalSpell spell : values()) {
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
