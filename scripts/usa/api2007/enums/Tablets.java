package scripts.usa.api2007.enums;

import scripts.usa.api.util.Strings;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;

public enum Tablets {

	VARROCK_TELEPORT(Spells.VARROCK_TELEPORT),
	LUMBRIDGE_TELEPORT(Spells.LUMBRIDGE_TELEPORT),
	FALADOR_TELEPORT(Spells.FALADOR_TELEPORT),
	TELEPORT_TO_HOUSE(Spells.TELEPORT_TO_HOUSE),
	CAMELOT_TELEPORT(Spells.CAMELOT_TELEPORT),
	ARDOUGNE_TELEPORT(Spells.ARDOUGNE_TELEPORT),
	WATCHTOWER_TELEPORT(Spells.WATCHTOWER_TELEPORT),

	ENCHANT_SAPPHIRE_OR_OPAL(Spells.LVL_1_ENCHANT),
	ENCHANT_EMERALD_OR_JADE(Spells.LVL_2_ENCHANT),
	ENCHANT_RUBY_OR_TOPAZ(Spells.LVL_3_ENCHANT),
	ENCHANT_DIAMOND(Spells.LVL_4_ENCHANT),
	ENCHANT_DRAGONSTONE(Spells.LVL_5_ENCHANT),
	ENCHANT_ONYX(Spells.LVL_6_ENCHANT),

	BONES_TO_BANANAS(Spells.BONES_TO_BANANAS),
	BONES_TO_PEACHES(Spells.BONES_TO_PEACHES);

	private final String name;
	private final String componentName;
	private final Spells spell;

	Tablets(Spells spell) {
		this.spell = spell;
		this.name = Strings.toSentenceCase(this.name());
		this.componentName = Strings.toProperCase(this.name())
				.replaceAll(" Or.*", "")
				.replace("Teleport To House", "House Teleport")
				.replace("To", "to");
	}

	public String getName() {
		return this.name;
	}

	public String getComponentName() {
		return this.componentName;
	}

	public Spells getSpell() {
		return this.spell;
	}

	public OSBuddyItem getOSBuddyItem() {
		return OSBuddy.get(this.name);
	}
}
