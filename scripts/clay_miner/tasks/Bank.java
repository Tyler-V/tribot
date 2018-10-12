package scripts.clay_miner.tasks;

import org.tribot.api.General;

import scripts.clay_miner.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.teleporting.constants.TeleportConstants;

public class Bank implements PriorityTask {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean validate() {
		if (Inventory.isFull())
			return true;

		if (Banking.isAtBank()) {
			if (!hasRequiredItems() || !isWearingRequiredItems())
				return true;
		}

		return false;
	}

	@Override
	public void execute() {
		if (Banking.isAtBank()) {
			if (!hasRequiredItems()) {
				if (Banking.open()) {
					Banking.depositAllExcept("Bracelet of clay", "Teleport to house", "Ring of dueling");
					if (!Banking.withdraw(1, "Teleport to house")) {
						General.println("Failed withdrawing Teleport to house");
					}
					if (!Equipment.isEquipped("Bracelet of clay")) {
						if (!Banking.withdraw(1, "Bracelet of clay"))
							General.println("Failed withdrawing Bracelet of clay");
					}
					if (!Equipment.isEquipped(TeleportConstants.DUELING_FILTER)) {
						if (!Banking.withdraw(1, TeleportConstants.DUELING_FILTER))
							General.println("Failed withdrawing Ring of dueling");
					}
				}
			}
			if (hasRequiredItems()) {
				if (Banking.close()) {
					Equipment.equip("Bracelet of clay");
					Equipment.equip(TeleportConstants.DUELING_FILTER);
				}
			}
		}
		else {
			Vars.get().status = "Traveling to Castle Wars";
			Walking.travelToBank(scripts.usa.dax_api.api_lib.models.Bank.CASTLE_WARS);
		}
	}

	private boolean hasRequiredItems() {
		if (Inventory.getCount("Teleport to house") == 0)
			return false;
		if (!Equipment.isEquipped("Bracelet of clay") && Inventory.getCount("Bracelet of clay") == 0)
			return false;
		if (!Equipment.isEquipped(TeleportConstants.DUELING_FILTER) && Inventory.getCount(TeleportConstants.DUELING_FILTER) == 0)
			return false;
		return true;
	}

	public static boolean isWearingRequiredItems() {
		return Equipment.isEquipped("Bracelet of clay") && Equipment.isEquipped(TeleportConstants.DUELING_FILTER);
	}
}
