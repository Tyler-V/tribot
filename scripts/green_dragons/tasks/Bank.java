package scripts.green_dragons.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.enums.Jewelry;
import scripts.usa.api2007.looting.LootingBag;
import scripts.usa.api2007.teleporting.constants.TeleportConstants;

public class Bank implements PriorityTask {

	@Override
	public boolean validate() {
		if (!Consumables.hasFood()) {
			if (Inventory.isFull())
				return true;
			if (Wilderness.isIn()) {
				if (!Vars.get().location.getArea()
						.contains(Player.getPosition()))
					return true;
				if (Player.getRSPlayer()
						.getHealthPercent() < Vars.get().eatHealthPercent)
					return true;
			}
		}

		if (Banking.isAtBank()) {
			if (Banking.isOpen()) {
				if (LootingBag.hasItems())
					return true;
			}
			if (!hasRequiredItems())
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
		}
		else {
			Vars.get()
					.travelToBank();
		}
	}

	private boolean hasRequiredItems() {
		if (Inventory.getCount(Jewelry.GAMES_NECKLACE.getPredicate()) == 0)
			return false;

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
}
