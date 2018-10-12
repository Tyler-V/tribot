package scripts.green_dragons.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;

import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.enums.Jewelry;
import scripts.usa.api2007.looting.LootingBag;
import scripts.usa.api2007.teleporting.constants.TeleportConstants;

public class Bank implements PriorityTask {

	@Override
	public int priority() {
		return 2;
	}

	@Override
	public boolean validate() {
		if (Inventory.isFull() && !Consumables.hasFood())
			return true;

		if (Wilderness.isIn() && !Consumables.hasFood()) {
			if (!Vars.get().location.getArea().contains(Player.getPosition()))
				return true;
			if (Player.getRSPlayer().getHealthPercent() < Vars.get().eatHealthPercent)
				return true;
		}

		if (Banking.isAtBank()) {
			if (Banking.isOpen()) {
				if (LootingBag.hasItems())
					return true;
			}
			if (!hasRequiredItems() || !isWearingRequiredItems())
				return true;
		}

		return false;
	}

	@Override
	public void execute() {
		if (Banking.isAtBank()) {
			Vars.get().status = "Opening Bank";
			if (Banking.open()) {
				if (!LootingBag.hasLootingBag()) {
					if (Banking.find("Looting bag") != null) {
						Banking.withdraw(1, "Looting bag");
					}
				}
				if (LootingBag.hasItems()) {
					Vars.get().status = "Depositing Loot";
					Vars.get().profit += LootingBag.Banking.getValue();
					LootingBag.Banking.depositLoot();
				}
				if (!hasRequiredItems()) {
					List<Predicate<RSItem>> predicates = new ArrayList<Predicate<RSItem>>();
					predicates.add(Filters.Items.nameEquals("Looting bag"));
					predicates.add(Filters.Items.nameEquals(Vars.get().foodName));
					predicates.add(Jewelry.GAMES_NECKLACE.getPredicate());
					predicates.add(Jewelry.RING_OF_DUELING.getPredicate());
					if (Vars.get().potion1 != null)
						predicates.add(Vars.get().potion1.getPredicate());
					if (Vars.get().potion2 != null)
						predicates.add(Vars.get().potion2.getPredicate());
					if (Vars.get().potion3 != null)
						predicates.add(Vars.get().potion3.getPredicate());

					if (Banking.depositExcept(predicates)) {
						if (Inventory.getCount(Jewelry.GAMES_NECKLACE.getPredicate()) == 0) {
							if (!Banking.withdraw(1, TeleportConstants.GAMES_FILTER)) {
								General.println("Failed withdrawing Games necklace!");
							}
						}
						if (!Equipment.isEquipped(Jewelry.RING_OF_DUELING.getPredicate()) &&
								Inventory.getCount(Jewelry.RING_OF_DUELING.getPredicate()) == 0) {
							if (!Banking.withdraw(1, TeleportConstants.DUELING_FILTER)) {
								General.println("Failed withdrawing Ring of dueling");
							}
						}
						if (Inventory.getCount(Vars.get().foodName) < Vars.get().foodQuantity) {
							if (!Banking.withdraw(Vars.get().foodQuantity, Vars.get().foodName))
								General.println("Failed withdrawing " + Vars.get().foodName);
						}
						if (Vars.get().potion1 != null && Inventory.getCount(Vars.get().potion1.getPredicate()) == 0) {
							if (!Banking.withdraw(1, Vars.get().potion1.getPredicate())) {
								General.println("Failed withdrawing " + Vars.get().potion1.getName());
								Vars.get().potion1 = null;
							}
						}
						if (Vars.get().potion2 != null && Inventory.getCount(Vars.get().potion2.getPredicate()) == 0) {
							if (!Banking.withdraw(1, Vars.get().potion2.getPredicate())) {
								General.println("Failed withdrawing " + Vars.get().potion2.getName());
								Vars.get().potion2 = null;
							}
						}
						if (Vars.get().potion3 != null && Inventory.getCount(Vars.get().potion3.getPredicate()) == 0) {
							if (!Banking.withdraw(1, Vars.get().potion3.getPredicate())) {
								General.println("Failed withdrawing " + Vars.get().potion3.getName());
								Vars.get().potion3 = null;
							}
						}
					}
				}
			}
			if (hasRequiredItems()) {
				if (!isWearingRequiredItems()) {
					Equipment.equip(Jewelry.RING_OF_DUELING.getPredicate());
				}
			}
		}
		else {
			if (Wilderness.getLevel() > 20) {
				Vars.get().status = "Traveling below 20 Wilderness";
				Walking.travel(new RSTile(Player.getPosition().getX(), 3630, 0), new WalkingCondition() {
					public State action() {
						if (Wilderness.getLevel() <= 20)
							return State.EXIT_OUT_WALKER_SUCCESS;
						return State.CONTINUE_WALKER;
					}
				});
			}
			else {
				Vars.get().status = "Traveling to Clan Wars";
				Walking.travelToBank(RunescapeBank.CLAN_WARS);
			}
		}
	}

	private boolean hasRequiredItems() {
		if (Inventory.getCount(Jewelry.GAMES_NECKLACE.getPredicate()) == 0)
			return false;

		if (!Equipment.isEquipped(Jewelry.RING_OF_DUELING.getPredicate())) {
			if (Inventory.getCount(Jewelry.RING_OF_DUELING.getPredicate()) == 0)
				return false;
		}

		if (Inventory.getCount(Vars.get().foodName) < Vars.get().foodQuantity)
			return false;

		if (Vars.get().potion1 != null && Inventory.getCount(Vars.get().potion1.getPredicate()) == 0)
			return false;

		if (Vars.get().potion2 != null && Inventory.getCount(Vars.get().potion2.getPredicate()) == 0)
			return false;

		if (Vars.get().potion3 != null && Inventory.getCount(Vars.get().potion3.getPredicate()) == 0)
			return false;

		return true;
	}

	private boolean isWearingRequiredItems() {
		if (!Equipment.isEquipped(Jewelry.RING_OF_DUELING.getPredicate()))
			return false;

		return true;
	}
}
