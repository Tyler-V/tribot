package scripts.green_dragons.data;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.General;

import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.green_dragons.models.PlayerEquipment;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.enums.Potions;
import scripts.usa.api2007.looting.Looter;
import scripts.usa.api2007.teleporting.Teleporting;
import scripts.usa.api2007.teleporting.constants.Destinations;
import scripts.usa.api2007.teleporting.constants.TeleportMethods;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public boolean evade;
	public double nextEatAt;
	public boolean changeWorlds;

	public int profit;
	public int kills;
	public int evaded;

	public Locations location;
	public int maxPlayers;
	public boolean dwarfCannon;

	public String foodName;
	public int foodQuantity;
	public double eatHealthPercent;

	public Potions potion1;
	public Potions potion2;
	public Potions potion3;

	public Looter looter;
	public Looter ammunitionLooter;

	public List<PlayerEquipment> playerEquipment = new ArrayList<PlayerEquipment>();
	public PlayerEquipment helmet;
	public PlayerEquipment body;
	public PlayerEquipment legs;
	public PlayerEquipment weapon;
	public PlayerEquipment cape;
	public PlayerEquipment gloves;
	public PlayerEquipment boots;
	public PlayerEquipment arrow;
	public PlayerEquipment shield;
	public PlayerEquipment amulet;
	public PlayerEquipment ring;
	public boolean useSpecialAttack;
	public int specialEnergy;
	public int ammunitionPerTrip;

	public String[] enemyEquipment = new String[0];
	public EvadeOption evadeOption;
	public boolean activateQuickPrayers;

	public boolean useAntiban;
	public boolean useReactionTiming;

	public boolean leaveWilderness() {
		if (!Wilderness.isIn())
			return true;

		if (Vars.get()
				.isTeleblocked()) {
			Vars.get().status = "Teleblocked!";
			Walking.travelToBank(RunescapeBank.EDGEVILLE, new WalkingCondition() {
				public State action() {
					if (!Wilderness.isIn())
						return State.EXIT_OUT_WALKER_SUCCESS;
					return State.CONTINUE_WALKER;
				}
			});
		}
		else {
			if (Vars.get().evade) {
				Vars.get().status = "Evading!";
				Teleporting.teleport(TeleportMethods.GLORY, Destinations.EDGEVILLE);
			}
			else {
				if (Wilderness.getLevel() > 20) {
					Vars.get().status = "Traveling below 20 Wilderness";
					Walking.travel(Vars.get().location.getTeleportTile(), new WalkingCondition() {
						public State action() {
							if (Wilderness.getLevel() <= 20)
								return State.EXIT_OUT_WALKER_SUCCESS;
							return State.CONTINUE_WALKER;
						}
					});
				}
				else {
					Vars.get().status = "Teleporting to Clan Wars";
					Teleporting.teleport(TeleportMethods.DUELING_RING, Destinations.CLAN_WARS);
				}
			}
		}

		return !Wilderness.isIn();
	}

	public void travelToBank() {
		if (leaveWilderness()) {
			if (TeleportMethods.DUELING_RING.canUse()) {
				Vars.get().status = "Traveling to Clan Wars";
				Walking.travelToBank(RunescapeBank.CLAN_WARS);
			}
			else {
				Vars.get().status = "Traveling to Bank";
				Walking.travelToBank();
			}
		}
	}

	public void setNextEatAt() {
		nextEatAt = Math.min(Math.max(0, eatHealthPercent + General.randomDouble(-.1, .1)), 1);
	}

	public double getNextEatAt() {
		if (nextEatAt == 0) {
			setNextEatAt();
		}
		return nextEatAt;
	}
}
