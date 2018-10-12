package scripts.usa.api2007.teleporting.constants;

import static scripts.usa.api2007.teleporting.constants.Destinations.AL_KHARID;
import static scripts.usa.api2007.teleporting.constants.Destinations.ARDOUGNE_MARKET_PLACE;
import static scripts.usa.api2007.teleporting.constants.Destinations.BANDIT_CAMP;
import static scripts.usa.api2007.teleporting.constants.Destinations.BARBARIAN_OUTPOST;
import static scripts.usa.api2007.teleporting.constants.Destinations.BURTHORPE_GAMES_ROOM;
import static scripts.usa.api2007.teleporting.constants.Destinations.CAMELOT;
import static scripts.usa.api2007.teleporting.constants.Destinations.CASTLE_WARS;
import static scripts.usa.api2007.teleporting.constants.Destinations.CHAMPIONS_GUILD;
import static scripts.usa.api2007.teleporting.constants.Destinations.CHAOS_TEMPLE;
import static scripts.usa.api2007.teleporting.constants.Destinations.CLAN_WARS;
import static scripts.usa.api2007.teleporting.constants.Destinations.COOKING_GUILD;
import static scripts.usa.api2007.teleporting.constants.Destinations.CORPOREAL_BEAST;
import static scripts.usa.api2007.teleporting.constants.Destinations.CRAFTING_GUILD;
import static scripts.usa.api2007.teleporting.constants.Destinations.DRAYNOR_VILLAGE;
import static scripts.usa.api2007.teleporting.constants.Destinations.DUEL_ARENA;
import static scripts.usa.api2007.teleporting.constants.Destinations.ECTO;
import static scripts.usa.api2007.teleporting.constants.Destinations.EDGEVILLE;
import static scripts.usa.api2007.teleporting.constants.Destinations.FALADOR_CENTER;
import static scripts.usa.api2007.teleporting.constants.Destinations.FALADOR_PARK;
import static scripts.usa.api2007.teleporting.constants.Destinations.FISHING_GUILD;
import static scripts.usa.api2007.teleporting.constants.Destinations.GRAND_EXCHANGE;
import static scripts.usa.api2007.teleporting.constants.Destinations.HOUSE;
import static scripts.usa.api2007.teleporting.constants.Destinations.KARAMJA_BANANA_PLANTATION;
import static scripts.usa.api2007.teleporting.constants.Destinations.LAVA_MAZE;
import static scripts.usa.api2007.teleporting.constants.Destinations.LUMBRIDGE_CASTLE;
import static scripts.usa.api2007.teleporting.constants.Destinations.MONASTRY_EDGE;
import static scripts.usa.api2007.teleporting.constants.Destinations.MOTHERLOAD_MINE;
import static scripts.usa.api2007.teleporting.constants.Destinations.RANGED_GUILD;
import static scripts.usa.api2007.teleporting.constants.Destinations.VARROCK_CENTER;
import static scripts.usa.api2007.teleporting.constants.Destinations.WARRIORS_GUILD;
import static scripts.usa.api2007.teleporting.constants.Destinations.WINTERTODT_CAMP;
import static scripts.usa.api2007.teleporting.constants.Destinations.WOODCUTTING_GUILD;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;

import scripts.usa.api2007.enums.Spells;
import scripts.usa.api2007.teleporting.interfaces.TeleportLimit;
import scripts.usa.api2007.teleporting.interfaces.TeleportOption;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public enum TeleportMethods implements TeleportOption {

	VARROCK_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, VARROCK_CENTER),
	LUMBRIDGE_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, LUMBRIDGE_CASTLE),
	FALADOR_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, FALADOR_CENTER),
	HOUSE_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, HOUSE),
	CAMELOT_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CAMELOT),
	ARDOUGNE_TELPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, ARDOUGNE_MARKET_PLACE),
	GLORY(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, EDGEVILLE, DRAYNOR_VILLAGE, KARAMJA_BANANA_PLANTATION, AL_KHARID),
	COMBAT_BRACE(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, CHAMPIONS_GUILD, WARRIORS_GUILD, RANGED_GUILD, MONASTRY_EDGE),
	GAMES_NECKLACE(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CORPOREAL_BEAST, BURTHORPE_GAMES_ROOM, WINTERTODT_CAMP, BARBARIAN_OUTPOST),
	DUELING_RING(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, DUEL_ARENA, CASTLE_WARS, CLAN_WARS),
	ECTOPHIAL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, ECTO),
	SKILLS_NECKLACE(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, FISHING_GUILD, MOTHERLOAD_MINE, CRAFTING_GUILD, COOKING_GUILD, WOODCUTTING_GUILD),
	RING_OF_WEALTH(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, GRAND_EXCHANGE, FALADOR_PARK),
	BURNING_AMULET(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CHAOS_TEMPLE, BANDIT_CAMP, LAVA_MAZE);

	private TeleportLimit wildernessRestriction;
	private Destinations[] destinations;

	TeleportMethods(TeleportLimit wildernessRestriction, Destinations... destinations) {
		this.wildernessRestriction = wildernessRestriction;
		this.destinations = destinations;
	}

	public TeleportLimit getWildernessRestriction() {
		return this.wildernessRestriction;
	}

	public Destinations[] getDestinations() {
		return this.destinations;
	}

	@Override
	public boolean canUse() {
		if (!getWildernessRestriction().canCast())
			return false;

		switch (this) {
			case ECTOPHIAL:
				return Inventory.find(Filters.Items.nameContains("Ectophial")).length > 0;
			case VARROCK_TELEPORT:
				return Spells.VARROCK_TELEPORT.canCast() || Inventory.getCount("Varrock teleport") > 0;
			case LUMBRIDGE_TELEPORT:
				return Spells.LUMBRIDGE_TELEPORT.canCast() || Inventory.getCount("Lumbridge teleport") > 0;
			case FALADOR_TELEPORT:
				return Spells.FALADOR_TELEPORT.canCast() || Inventory.getCount("Falador teleport") > 0;
			case HOUSE_TELEPORT:
				return Spells.TELEPORT_TO_HOUSE.canCast() || Inventory.getCount("Teleport to house") > 0;
			case CAMELOT_TELEPORT:
				return Spells.CAMELOT_TELEPORT.canCast() || Inventory.getCount("Camelot teleport") > 0;
			case ARDOUGNE_TELPORT:
				return Game.getSetting(165) >= 30 && (Spells.ARDOUGNE_TELEPORT.canCast() || Inventory.getCount("Ardougne teleport") > 0);
			case GLORY:
				return Inventory.find(TeleportConstants.GLORY_FILTER).length > 0 || Equipment.find(TeleportConstants.GLORY_FILTER).length > 0;
			case COMBAT_BRACE:
				return Inventory.find(TeleportConstants.COMBAT_FILTER).length > 0 || Equipment.find(TeleportConstants.COMBAT_FILTER).length > 0;
			case GAMES_NECKLACE:
				return Inventory.find(TeleportConstants.GAMES_FILTER).length > 0 || Equipment.find(TeleportConstants.GAMES_FILTER).length > 0;
			case DUELING_RING:
				return Inventory.find(TeleportConstants.DUELING_FILTER).length > 0 || Equipment.find(TeleportConstants.DUELING_FILTER).length > 0;
			case SKILLS_NECKLACE:
				return Inventory.find(TeleportConstants.SKILLS_FILTER).length > 0 || Equipment.find(TeleportConstants.SKILLS_FILTER).length > 0;
			case RING_OF_WEALTH:
				return Inventory.find(TeleportConstants.WEALTH_FILTER).length > 0 || Equipment.find(TeleportConstants.WEALTH_FILTER).length > 0;
			case BURNING_AMULET:
				return Inventory.find(TeleportConstants.BURNING_FILTER).length > 0 || Equipment.find(TeleportConstants.BURNING_FILTER).length > 0;
			default:
				break;
		}
		return false;
	}

	public boolean toDestination(Destinations destination) {
		Interfaces.closeAll();

		switch (destination) {
			case VARROCK_CENTER:
				return RSItemUtils.click("Varrock t.*", "Break") || Spells.VARROCK_TELEPORT.cast();
			case LUMBRIDGE_CASTLE:
				return RSItemUtils.click("Lumbridge t.*", "Break") || Spells.LUMBRIDGE_TELEPORT.cast();
			case FALADOR_CENTER:
				return RSItemUtils.click("Falador t.*", "Break") || Spells.FALADOR_TELEPORT.cast();
			case HOUSE:
				return RSItemUtils.click("Teleport to house", "Break") || Spells.TELEPORT_TO_HOUSE.cast();
			case CAMELOT:
				return RSItemUtils.click("Camelot t.*", "Break") || Spells.CAMELOT_TELEPORT.cast();
			case ARDOUGNE_MARKET_PLACE:
				return RSItemUtils.click("Ardougne t.*", "Break") || Spells.ARDOUGNE_TELEPORT.cast();

			case DUEL_ARENA:
				return RSItemUtils.teleport(TeleportConstants.DUELING_FILTER, "(Duel.*|Al K.*)");
			case CASTLE_WARS:
				return RSItemUtils.teleport(TeleportConstants.DUELING_FILTER, "Castle War.*");
			case CLAN_WARS:
				return RSItemUtils.teleport(TeleportConstants.DUELING_FILTER, "Clan Wars.*");

			case AL_KHARID:
				return RSItemUtils.teleport(TeleportConstants.GLORY_FILTER, "Al .*");
			case EDGEVILLE:
				return RSItemUtils.teleport(TeleportConstants.GLORY_FILTER, "Edge.*");
			case KARAMJA_BANANA_PLANTATION:
				return RSItemUtils.teleport(TeleportConstants.GLORY_FILTER, "Karamja.*");
			case DRAYNOR_VILLAGE:
				return RSItemUtils.teleport(TeleportConstants.GLORY_FILTER, "Draynor.*");

			case BURTHORPE_GAMES_ROOM:
				return RSItemUtils.teleport(TeleportConstants.GAMES_FILTER, "Burthorpe.*");
			case WINTERTODT_CAMP:
				return RSItemUtils.teleport(TeleportConstants.GAMES_FILTER, "Winter.*");
			case CORPOREAL_BEAST:
				return RSItemUtils.teleport(TeleportConstants.GAMES_FILTER, "Corp.*");
			case BARBARIAN_OUTPOST:
				return RSItemUtils.teleport(TeleportConstants.GAMES_FILTER, "Barb.*");

			case WARRIORS_GUILD:
				return RSItemUtils.teleport(TeleportConstants.COMBAT_FILTER, "Warrior.*");
			case CHAMPIONS_GUILD:
				return RSItemUtils.teleport(TeleportConstants.COMBAT_FILTER, "Champ.*");
			case MONASTRY_EDGE:
				return RSItemUtils.teleport(TeleportConstants.COMBAT_FILTER, "Mona.*");
			case RANGED_GUILD:
				return RSItemUtils.teleport(TeleportConstants.COMBAT_FILTER, "Rang.*");

			case ECTO:
				return RSItemUtils.click(Filters.Items.nameContains("Ectophial"), "Empty");

			case FISHING_GUILD:
				return RSItemUtils.teleport(TeleportConstants.SKILLS_FILTER, "Fishing.*");
			case MOTHERLOAD_MINE:
				return RSItemUtils.teleport(TeleportConstants.SKILLS_FILTER, "Mother.*");
			case CRAFTING_GUILD:
				return RSItemUtils.teleport(TeleportConstants.SKILLS_FILTER, "Crafting.*");
			case COOKING_GUILD:
				return RSItemUtils.teleport(TeleportConstants.SKILLS_FILTER, "Cooking.*");
			case WOODCUTTING_GUILD:
				return RSItemUtils.teleport(TeleportConstants.SKILLS_FILTER, "Woodcutting.*");

			case GRAND_EXCHANGE:
				return RSItemUtils.teleport(TeleportConstants.WEALTH_FILTER, "Grand.*");
			case FALADOR_PARK:
				return RSItemUtils.teleport(TeleportConstants.WEALTH_FILTER, "Falad.*");

			case CHAOS_TEMPLE:
				return RSItemUtils.teleport(TeleportConstants.BURNING_FILTER, "(Chaos.*|Okay, teleport to level.*)");
			case BANDIT_CAMP:
				return RSItemUtils.teleport(TeleportConstants.BURNING_FILTER, "(Bandit.*|Okay, teleport to level.*)");
			case LAVA_MAZE:
				return RSItemUtils.teleport(TeleportConstants.BURNING_FILTER, "(Lava.*|Okay, teleport to level.*)");

			default:
				break;
		}
		return false;
	}
}
