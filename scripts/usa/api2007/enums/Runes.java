package scripts.usa.api2007.enums;

import java.util.Arrays;

import scripts.usa.api.util.Strings;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;
import scripts.usa.api2007.Inventory;

public enum Runes {

	MIST_RUNE(6, EssenceType.PURE),
	DUST_RUNE(10, EssenceType.PURE),
	MUD_RUNE(13, EssenceType.PURE),
	SMOKE_RUNE(15, EssenceType.PURE),
	STEAM_RUNE(19, EssenceType.PURE),
	LAVA_RUNE(23, EssenceType.PURE),

	AIR_RUNE(1, EssenceType.NORMAL_OR_PURE, Runes.MIST_RUNE, Runes.DUST_RUNE, Runes.SMOKE_RUNE),
	WATER_RUNE(5, EssenceType.NORMAL_OR_PURE, Runes.MIST_RUNE, Runes.MUD_RUNE, Runes.STEAM_RUNE),
	EARTH_RUNE(9, EssenceType.NORMAL_OR_PURE, Runes.DUST_RUNE, Runes.MUD_RUNE, Runes.LAVA_RUNE),
	FIRE_RUNE(14, EssenceType.NORMAL_OR_PURE, Runes.SMOKE_RUNE, Runes.STEAM_RUNE, Runes.LAVA_RUNE),

	MIND_RUNE(2, EssenceType.NORMAL_OR_PURE),
	BODY_RUNE(20, EssenceType.NORMAL_OR_PURE),

	COSMIC_RUNE(27, EssenceType.PURE),
	CHAOS_RUNE(35, EssenceType.PURE),
	NATURE_RUNE(44, EssenceType.PURE),
	LAW_RUNE(54, EssenceType.PURE),
	DEATH_RUNE(65, EssenceType.PURE),
	BLOOD_RUNE(77, EssenceType.PURE),
	SOUL_RUNE(90, EssenceType.PURE),
	WRATH_RUNE(95, EssenceType.PURE);

	private final int runecraftingLevel;
	private final EssenceType essenceType;
	private final Runes[] alias;

	Runes(int runecraftingLevel, EssenceType essenceType, Runes... alias) {
		this.runecraftingLevel = runecraftingLevel;
		this.essenceType = essenceType;
		this.alias = alias != null ? alias : new Runes[0];
	}

	public String getName() {
		return Strings.toSentenceCase(this.name());
	}

	public int getRunecraftingLevel() {
		return this.runecraftingLevel;
	}

	public EssenceType getEssenceType() {
		return this.essenceType;
	}

	public Runes[] getAlias() {
		return this.alias;
	}

	public OSBuddyItem getOSBuddy() {
		return OSBuddy.get(this.getName());
	}

	public boolean hasRequirement(int count) {
		if (Inventory.getCount(this.getName()) >= count)
			return true;
		for (Runes rune : Runes.values()) {
			if (Arrays.asList(rune.getAlias()).contains(this)) {
				if (Inventory.getCount(rune.getName()) >= count)
					return true;
			}
		}
		return false;
	}

	public enum EssenceType {
		NORMAL_OR_PURE,
		PURE
	}
}
