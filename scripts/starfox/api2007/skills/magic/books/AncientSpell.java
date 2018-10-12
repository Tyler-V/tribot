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
 * @author Nolan
 */
public enum AncientSpell
        implements Spell, Serializable, Listable {

    //COMBAT SPELLS
    SMOKE_RUSH("Smoke Rush", 50, new Rune[]{Rune.DEATH, Rune.CHAOS, Rune.FIRE, Rune.AIR}, new int[]{2, 2, 1, 1}, SpellType.COMBAT),
    SHADOW_RUSH("Shadow Rush", 52, new Rune[]{Rune.SOUL, Rune.DEATH, Rune.CHAOS, Rune.AIR}, new int[]{1, 2, 2, 1}, SpellType.COMBAT),
    BLOOD_RUSH("Blood Rush", 56, new Rune[]{Rune.BLOOD, Rune.DEATH, Rune.CHAOS}, new int[]{1, 2, 2}, SpellType.COMBAT),
    ICE_RUSH("Ice Rush", 58, new Rune[]{Rune.DEATH, Rune.CHAOS, Rune.WATER}, new int[]{2, 2, 2}, SpellType.COMBAT),
    SMOKE_BURST("Smoke Burst", 62, new Rune[]{Rune.DEATH, Rune.CHAOS, Rune.FIRE, Rune.AIR}, new int[]{2, 4, 2, 2}, SpellType.COMBAT),
    SHADOW_BURST("Shadow Burst", 64, new Rune[]{Rune.DEATH, Rune.CHAOS, Rune.FIRE, Rune.AIR}, new int[]{2, 4, 2, 2}, SpellType.COMBAT),
    BLOOD_BURST("Blood Burst", 68, new Rune[]{Rune.BLOOD, Rune.DEATH, Rune.CHAOS}, new int[]{2, 2, 4}, SpellType.COMBAT),
    ICE_BURST("Ice Burst", 70, new Rune[]{Rune.DEATH, Rune.CHAOS, Rune.WATER}, new int[]{2, 4, 4}, SpellType.COMBAT),
    SMOKE_BLITZ("Smoke Blitz", 74, new Rune[]{Rune.DEATH, Rune.CHAOS, Rune.FIRE, Rune.AIR}, new int[]{2, 2, 2, 2}, SpellType.COMBAT),
    SHADOW_BLITZ("Shadow Blitz", 76, new Rune[]{Rune.SOUL, Rune.BLOOD, Rune.DEATH, Rune.AIR}, new int[]{2, 2, 2, 2}, SpellType.COMBAT),
    BLOOD_BLITZ("Blood Blitz", 80, new Rune[]{Rune.BLOOD, Rune.DEATH}, new int[]{4, 2}, SpellType.COMBAT),
    ICE_BLITZ("Ice Blitz", 82, new Rune[]{Rune.BLOOD, Rune.DEATH, Rune.WATER}, new int[]{2, 2, 3}, SpellType.COMBAT),
    SMOKE_BARRAGE("Smoke Barrage", 86, new Rune[]{Rune.BLOOD, Rune.DEATH, Rune.FIRE, Rune.AIR}, new int[]{2, 4, 4, 4}, SpellType.COMBAT),
    SHADOW_BARRAGE("Shadow Barrage", 88, new Rune[]{Rune.SOUL, Rune.BLOOD, Rune.DEATH, Rune.AIR}, new int[]{3, 2, 3, 4}, SpellType.COMBAT),
    BLOOD_BARRAGE("Blood Barrage", 92, new Rune[]{Rune.SOUL, Rune.BLOOD, Rune.DEATH}, new int[]{1, 4, 4}, SpellType.COMBAT),
    ICE_BARRAGE("Ice Barrage", 94, new Rune[]{Rune.BLOOD, Rune.DEATH, Rune.WATER}, new int[]{2, 4, 6}, SpellType.COMBAT),
    //TELEPORT SPELLS
    HOME_TELEPORT("Edgeville Home Teleport", 0, new Rune[]{}, new int[]{}, SpellType.TELEPORT),
    PADDEWWA_TELEPORT("Paddewwa Teleport", 54, new Rune[]{Rune.LAW, Rune.FIRE, Rune.AIR}, new int[]{2, 1, 1}, SpellType.TELEPORT),
    SENNTISTEN_TELEPORT("Senntisten Teleport", 60, new Rune[]{Rune.SOUL, Rune.LAW}, new int[]{1, 2}, SpellType.TELEPORT),
    KHARYRLL_TELEPORT("Kharyrll Teleport", 66, new Rune[]{Rune.BLOOD, Rune.LAW}, new int[]{1, 2}, SpellType.TELEPORT),
    LASSAR_TELEPORT("Lassar Teleport", 72, new Rune[]{Rune.LAW, Rune.WATER}, new int[]{2, 4}, SpellType.TELEPORT),
    DAREEYAK_TELEPORT("Dareeyak Teleport", 78, new Rune[]{Rune.LAW, Rune.FIRE, Rune.AIR}, new int[]{2, 3, 2}, SpellType.TELEPORT),
    CARRALLANGAR_TELEPORT("Carrallangar Teleport", 84, new Rune[]{Rune.SOUL, Rune.LAW}, new int[]{2, 2}, SpellType.TELEPORT),
    ANNAKARL_TELEPORT("Annakarl Teleport", 90, new Rune[]{Rune.BLOOD, Rune.LAW}, new int[]{2, 2}, SpellType.TELEPORT),
    GHORROCK_TELEPORT("Ghorrock Teleport", 96, new Rune[]{Rune.LAW, Rune.WATER}, new int[]{2, 8}, SpellType.TELEPORT);

    private final String name;
    private final int requiredLevel;
    private final Rune[] requiredRunes;
    private final int[] numberOfRunes;
    private final SpellType type;

    /**
     * Constructs a new AncientSpell.
     *
     * @param name          The name of the spell.
     * @param requiredLevel The magic level required to cast the spell.
     * @param requiredRunes The runes required to cast the spell.
     * @param numberOfRunes The number of each rune required to cast the spell.
     * @param type          The type of the spell.
     */
    AncientSpell(String name, int requiredLevel, Rune[] requiredRunes, int[] numberOfRunes, SpellType type) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.requiredRunes = requiredRunes;
        this.numberOfRunes = numberOfRunes;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getRequiredLevel() {
        return this.requiredLevel;
    }

    @Override
    public Rune[] getRequiredRunes() {
        return this.requiredRunes;
    }

    @Override
    public int[] getNumberOfRunes() {
        return this.numberOfRunes;
    }

    @Override
    public SpellType getType() {
        return this.type;
    }

    @Override
    public MagicBook getBook() {
        return MagicBook.ANCIENT;
    }

    /**
     * Gets an array of all of the spells whose type matches any of the specified types.
     *
     * @param spellTypes The types to match.
     * @return An array of spells whose type matches any of the specified types.
     */
    public static final AncientSpell[] forType(SpellType... spellTypes) {
        ArrayList<AncientSpell> spells = new ArrayList<>();
        for (AncientSpell spell : values()) {
            if (ArrayUtil.contains(spell, spellTypes)) {
                spells.add(spell);
            }
        }
        return spells.toArray(new AncientSpell[spells.size()]);
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
