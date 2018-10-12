package scripts.usa.api2007.enums;

import java.util.Arrays;

import org.tribot.api2007.Magic;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.usa.api.util.Strings;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Inventory;

public enum Spells {

	LVL_1_ENCHANT(7, new Rune(Runes.COSMIC_RUNE, 1), new Rune(Runes.WATER_RUNE, 1)),
	LVL_2_ENCHANT(27, new Rune(Runes.COSMIC_RUNE, 1), new Rune(Runes.AIR_RUNE, 3)),
	LVL_3_ENCHANT(49, new Rune(Runes.COSMIC_RUNE, 1), new Rune(Runes.FIRE_RUNE, 5)),
	LVL_4_ENCHANT(57, new Rune(Runes.COSMIC_RUNE, 1), new Rune(Runes.EARTH_RUNE, 10)),
	LVL_5_ENCHANT(68, new Rune(Runes.COSMIC_RUNE, 1), new Rune(Runes.WATER_RUNE, 15), new Rune(Runes.EARTH_RUNE, 15)),
	LVL_6_ENCHANT(87, new Rune(Runes.COSMIC_RUNE, 1), new Rune(Runes.EARTH_RUNE, 20), new Rune(Runes.FIRE_RUNE, 20)),
	LVL_7_ENCHANT(93, new Rune(Runes.SOUL_RUNE, 20), new Rune(Runes.BLOOD_RUNE, 20), new Rune(Runes.COSMIC_RUNE, 1)),

	BONES_TO_BANANAS(15, new Rune(Runes.NATURE_RUNE, 1), new Rune(Runes.WATER_RUNE, 2), new Rune(Runes.EARTH_RUNE, 2)),
	BONES_TO_PEACHES(60, new Rune(Runes.NATURE_RUNE, 2), new Rune(Runes.WATER_RUNE, 4), new Rune(Runes.EARTH_RUNE, 2)),

	VARROCK_TELEPORT(25, new Rune(Runes.LAW_RUNE, 1), new Rune(Runes.FIRE_RUNE, 1), new Rune(Runes.AIR_RUNE, 3)),
	LUMBRIDGE_TELEPORT(31, new Rune(Runes.LAW_RUNE, 1), new Rune(Runes.FIRE_RUNE, 1), new Rune(Runes.AIR_RUNE, 3)),
	FALADOR_TELEPORT(37, new Rune(Runes.LAW_RUNE, 1), new Rune(Runes.WATER_RUNE, 1), new Rune(Runes.AIR_RUNE, 3)),
	TELEPORT_TO_HOUSE(40, new Rune(Runes.LAW_RUNE, 1), new Rune(Runes.WATER_RUNE, 1), new Rune(Runes.AIR_RUNE, 1)),
	CAMELOT_TELEPORT(45, new Rune(Runes.LAW_RUNE, 1), new Rune(Runes.AIR_RUNE, 5)),
	ARDOUGNE_TELEPORT(51, new Rune(Runes.LAW_RUNE, 2), new Rune(Runes.WATER_RUNE, 2)),
	WATCHTOWER_TELEPORT(58, new Rune(Runes.LAW_RUNE, 1), new Rune(Runes.WATER_RUNE, 2));

	private final int levelRequired;
	private final Rune[] runes;

	Spells(int levelRequired, Rune... runes) {
		this.levelRequired = levelRequired;
		this.runes = runes;
	}

	public String getName() {
		return Strings.toProperCase(this.name()).replace("To", "to").replace("Lvl ", "Lvl-");
	}

	public int getLevelRequired() {
		return this.levelRequired;
	}

	public Rune[] getRunesRequired() {
		return this.runes;
	}

	public boolean hasRequiredLevel() {
		return Skills.getCurrentLevel(SKILLS.MAGIC) >= this.levelRequired;
	}

	public boolean hasRequiredRunes() {
		Staffs staff = Equipment.getStaff();
		for (Rune rune : this.runes) {
			if (Inventory.getCount(rune.getRune().getName()) < rune.getAmount()) {
				if (staff == null)
					return false;
				if (!Arrays.asList(staff.getProviders()).contains(rune.getRune()))
					return false;
			}
		}
		return true;
	}

	public boolean canCast() {
		return hasRequiredLevel() && hasRequiredRunes();
	}

	public boolean cast() {
		return canCast() && Magic.selectSpell(getName());
	}

	public static class Rune {
		private final Runes rune;
		private final int amount;

		public Rune(Runes rune, int amount) {
			this.rune = rune;
			this.amount = amount;
		}

		public Runes getRune() {
			return this.rune;
		}

		public int getAmount() {
			return this.amount;
		}
	}
}
