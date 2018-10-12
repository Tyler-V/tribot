package scripts.usa.api.responder;

import org.tribot.api2007.Skills.SKILLS;

public enum SKILL_KEYWORDS {

    ATTACK(SKILLS.ATTACK, new String[] { "atk", "attk", "attack" }),

    STRENGTH(SKILLS.STRENGTH, new String[] { "str", "strength" }),

    DEFENCE(SKILLS.DEFENCE, new String[] { "def", "defence", "defense" }),

    RANGED(SKILLS.RANGED, new String[] { "rang", "range", "ranged" }),

    PRAYER(SKILLS.PRAYER, new String[] { "pray", "prayer" }),

    MAGIC(SKILLS.MAGIC, new String[] { "mage", "magic" }),

    RUNECRAFTING(SKILLS.RUNECRAFTING, new String[] { "rc", "runecraft", "runecrafting", "runecraftin" }),

    CONSTRUCTION(SKILLS.CONSTRUCTION, new String[] { "con", "construction", "constructing" }),

    HITPOINTS(SKILLS.HITPOINTS, new String[] { "hp", "hitpoint", "hitpoints", "health" }),

    AGILITY(SKILLS.AGILITY, new String[] { "agil", "agile", "agility" }),

    HERBLORE(SKILLS.HERBLORE, new String[] { "herb", "herblore" }),

    THIEVING(SKILLS.THIEVING, new String[] { "thieve", "thievin", "thieving" }),

    CRAFTING(SKILLS.CRAFTING, new String[] { "craft", "craftin", "crafting" }),

    FLETCHING(SKILLS.FLETCHING, new String[] { "fletch", "fletching" }),

    SLAYER(SKILLS.SLAYER, new String[] { "slay", "slayer", "slaying" }),

    HUNTER(SKILLS.HUNTER, new String[] { "hunt", "hunter", "hunting" }),

    MINING(SKILLS.MINING, new String[] { "mine", "minin", "mining" }),

    SMITHING(SKILLS.SMITHING, new String[] { "smith", "smithin", "smithing" }),

    FISHING(SKILLS.FISHING, new String[] { "fish", "fishin", "fishing" }),

    COOKING(SKILLS.COOKING, new String[] { "cook", "cookin", "cooking" }),

    FIREMAKING(SKILLS.FIREMAKING, new String[] { "fire", "fm", "firemakin", "firemaking" }),

    WOODCUTTING(SKILLS.WOODCUTTING, new String[] { "wc", "woodcuttin", "woodcutting" }),

    FARMING(SKILLS.FARMING, new String[] { "farm", "farmin", "farming" });

    private final SKILLS skill;
    private final String[] keywords;

    SKILL_KEYWORDS(SKILLS skill, String[] keywords) {
	this.skill = skill;
	this.keywords = keywords;
    }

    public SKILLS getSkill() {
	return this.skill;
    }

    public String[] getKeywords() {
	return this.keywords;
    }
}
