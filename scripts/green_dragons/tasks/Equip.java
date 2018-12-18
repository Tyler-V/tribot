package scripts.green_dragons.tasks;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.types.RSItem;

import scripts.green_dragons.data.Vars;
import scripts.green_dragons.models.PlayerEquipment;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Wilderness;

public class Equip implements PriorityTask {

	@Override
	public boolean validate() {
		return !isWearingEquipment() || !hasRequiredAmmunition();
	}

	@Override
	public void execute() {
		if (Banking.isAtBank()) {
			List<Predicate<RSItem>> predicates = Vars.get().playerEquipment.stream()
					.map(e -> e.getPredicate())
					.collect(Collectors.toList());
			if (isMissingEquipment()) {
				Vars.get().status = "Withdrawing Equipment";
				if (Banking.open()) {
					for (PlayerEquipment equipment : getMissingEquipment()) {
						if (Inventory.isFull())
							Banking.depositOneExcept(predicates);
						if (!Banking.withdraw(equipment.getSlot() == SLOTS.ARROW ? getAmmunitionRequired() : 1, equipment.getPredicate()))
							General.println("Failed withdrawing " + equipment.getName());
					}
				}
			}
			else {
				Vars.get().status = "Equipping Items";
				if (Banking.close()) {
					for (Predicate<RSItem> predicate : predicates) {
						Equipment.equip(predicate);
					}
				}
			}
		}
		else {
			Vars.get().status = "Missing Equipment";
			Vars.get()
					.travelToBank();
		}
	}

	private boolean isMissingEquipment() {
		return getMissingEquipment().stream()
				.filter(equipment -> !Inventory.has(equipment.getPredicate()))
				.findAny()
				.isPresent();
	}

	private List<PlayerEquipment> getMissingEquipment() {
		return Vars.get().playerEquipment.stream()
				.filter(equipment -> {
					if (equipment.getSlot() == SLOTS.ARROW) {
						return !hasRequiredAmmunition();
					}
					return !Equipment.isEquipped(equipment.getPredicate());
				})
				.collect(Collectors.toList());
	}

	private boolean isWearingEquipment() {
		for (PlayerEquipment equipment : Vars.get().playerEquipment) {
			if (!Equipment.isEquipped(equipment.getPredicate())) {
				return false;
			}
		}
		return true;
	}

	private int getAmmunitionRequired() {
		RSItem arrow = Equipment.getItem(SLOTS.ARROW);
		return arrow == null ? Vars.get().ammunitionPerTrip : (Vars.get().ammunitionPerTrip - arrow.getStack());
	}

	private boolean hasRequiredAmmunition() {
		if (Vars.get().arrow == null)
			return true;

		if (Wilderness.isIn() && Vars.get().arrow.getId() > 0)
			return true;

		return Equipment.getCount(Vars.get().arrow.getId()) >= Vars.get().ammunitionPerTrip;
	}
}
