package scripts.usa.api.responder;

import org.tribot.api.General;
import org.tribot.api2007.Skills.SKILLS;

public class Keywords {

	public static String[] GREETINGS = new String[] { "hola", "hey", "hi", "sup", "yo", "yoo", "hello" };

	public static String[] LEAVING_KEYWORDS = new String[] { "bye", "cya", "cyah", "later", "goodbye", "seeya", "see-ya", "lata", "peace" };

	public static String LEAVING_RESPONSES[] = new String[] { "ok", "bye", "laterr", "cya", "later", "lata", "peace", "good bye", "goodbye",
			"later man", "later dude", "bye man", "bye dude", "peace man", "ok sounds good", "thanks cya", "haha kk", "k", "haha" };

	public static String[] BOTTING_KEYWORDS = new String[] { "bot", "bots", "botters", "macroers", "cheaters", "bottin", "botting", "macro",
			"macroing", "cheat", "cheating", "cheater", "botter", "macroer", "boting", "botin" };

	public static String BOTTING_RESPONSES[] = new String[] { "no", "nop", "nope", "nah", "nahh", "noo", "naw", "nopee", "not a bot", "not botting",
			"lol whatever", "lol", "nice try", "i thought you were", "lol i figured you were", "i guessed u were lol", "r u?", "u sure u arent?",
			"i think you are", "maybe you are", "not me", "haha", "lol", "what?", "?", "??", "???", "...?", "..??", "..?", "no man",
			"no i dont do that", "nah im legit", "im legit", "i am legit", "nah man", "naw man", "rofl", "lol???", "dont think so", "you sure?",
			"hahaha", "lol??" };

	public static String GENERIC_RESPONSES[] = new String[] { "?", "??", "huh", "what", "wat", "lol", "sorry", "idk", "idk sorry", "what do you mean",
			"not sure", "not sure what you mean", "not sure what u mean", "uhh", "hm", "hmm", "how", "why" };

	public static String SMILE_FACES[] = new String[] { ":)", ":)", ";)", ":D", ":O", ":]", ":X", ";]" };

	public static String CONFUSED_RESPONSES[] = new String[] { "what skill", "what do u mean", "which", "what one", "?", "??", "???", "huh",
			"not sure what you mean", "haha what", "what haha", "haha huh", "huh haha", "what", "hm", "huh", "idk", "lol what", "wat", "lol what",
			"lol wat" };

	public static String QUESTION_RESPONSES[] = new String[] { "why", "y", "u", "you", "hbu", "what about you", "how bout yourself", "how about u",
			"how about yourself", "and you", "..you", "..u" };

	public static String PUNCTUATION[] = new String[] { ".", ".", "!", "?" };

	public static String PREFERENCES[] = { "is annoying", "is so annoying", "is my favorite", "is fun", "is so fun", "can be fun", "is not fun",
			"can be stressful", "is meh", "is interesting", "is challenging", "is stressful", "is not my best stat", "is what im working on",
			"is what im trying to level up" };

	public static String LEVEL_KEYWORDS[] = new String[] { "lvl", "lvls", "level", "levels", "stat", "stats", "lev", "combat" };

	public static String LEVEL_RESPONSES[] = new String[] { "level", "level", "level", "idk", "99", "i am " + "level" + " in " + "skill",
			"i have " + "level" + " in " + "skill", "i have " + "level" + " " + "skill", "i got " + "level" + " in " + "skill",
			"i got " + "level" + " " + "skill", "i've got " + "level", "lvl " + "level", "level" + " wooo", "about to be " + "level1",
			"Just got " + "level", "meh, " + "level", "like " + "level", "gettin close to " + "level1", "idk " + "level", "around " + "level",
			"almost " + "level1", "level1" + " soon", "level" + " " + "skill", "level" + " ish", "recently got " + "level", "im around " + "level",
			"uhh " + "level", "uhh like " + "level", "level" + " or " + "level1" + " i think", "level" + " lol", "lol " + "level", "over " + "level",
			"im about " + "level", "idk like " + "level", "im like " + "level", "about " + "level", "like almost " + "level1", "almost " + "level1",
			"closing in on " + "level1", "level" + ". " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)],
			"level" + ".. " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)],
			"level" + ", " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)] };

	public enum Skill {

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

		Skill(SKILLS skill, String[] keywords) {
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

}
