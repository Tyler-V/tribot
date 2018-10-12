package scripts.tablets.tasks;

import org.tribot.api.General;
import org.tribot.api2007.types.RSItem;

import scripts.tablets.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.House;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.NPCChat;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;

public class Phials implements Task {

	@Override
	public boolean validate() {
		if (Vars.get().hosting || Vars.get().servant)
			return false;

		RSItem softClay = Entities.find(ItemEntity::new).nameEquals("Soft clay").isNotNoted().getFirstResult();
		if (softClay != null)
			return false;

		RSItem softClayNoted = Entities.find(ItemEntity::new).nameEquals("Soft clay").isNoted().getFirstResult();
		if (Inventory.getCount(softClayNoted) == 0) {
			General.println("We are out of noted Soft clay!");
			Vars.get().stopScript();
			return false;
		}

		if (Inventory.getCount("Coins") < 5) {
			General.println("We are out of coins!");
			Vars.get().stopScript();
			return false;
		}

		return true;
	}

	@Override
	public void execute() {
		if (House.isInside()) {
			Vars.get().status = "Exiting House";
			House.exit();
		}
		if (House.isOutside()) {
			if (!NPCChat.isOptionValid("Exchange All")) {
				Vars.get().status = "Interacting with Phials";
				Entity.useItemOn(Entities.find(ItemEntity::new).nameEquals("Soft clay").isNoted(), Entities.find(NpcEntity::new)
						.nameEquals("Phials"), () -> NPCChat.isOptionValid("Exchange All"));
			}

			if (NPCChat.isOptionValid("Exchange All")) {
				Vars.get().status = "Exchanging Soft clay";
				NPCChat.selectOption("Exchange All");
				Condition.wait(Conditions.inventoryContains(Entities.find(ItemEntity::new).nameEquals("Soft clay").isNotNoted().getFirstResult()));
			}
		}
	}
}
