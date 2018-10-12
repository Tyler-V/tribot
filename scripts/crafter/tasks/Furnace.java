package scripts.crafter.tasks;

import org.tribot.api2007.ext.Filters;

import scripts.crafter.data.Locations.Type;
import scripts.crafter.data.Vars;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class Furnace implements Task {

	@Override
	public boolean validate() {
		return Vars.get().product.getType() == Type.FURNACE && !Vars.isInterfaceUp();
	}

	@Override
	public void execute() {
		furnace();
	}

	private boolean furnace() {
		if (Vars.get().location.getTile().isOnScreen()) {
			if (hasMould()) {
				Entity.interact("Smelt", Entities.find(ObjectEntity::new)
						.nameEquals(Vars.get().location.getObjectName()), () -> Vars.isInterfaceUp());
			}
			else {
				Entity.useItemOn(Entities.find(ItemEntity::new).nameEquals(Vars.getMaterials()), Entities.find(ObjectEntity::new)
						.nameEquals(Vars.get().location.getObjectName()), () -> {
							if (Vars.isInterfaceUp())
								return Status.SUCCESS;
							if (Conditions.isPlayerActive().isTrue())
								return Status.RESET;
							return Status.CONTINUE;
						});
			}
		}
		else {
			Vars.get().status = "Traveling to " + Vars.get().location.getName() + " " + Vars.get().location.getObjectName();
			Walking.travel(Vars.get().location.getTile());
		}

		return true;
	}

	private boolean hasMould() {
		return Inventory.find(Filters.Items.nameContains("mould")).length > 0;
	}

}
