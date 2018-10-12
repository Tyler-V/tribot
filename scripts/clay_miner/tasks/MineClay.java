package scripts.clay_miner.tasks;

import org.tribot.api2007.types.RSTile;

import scripts.clay_miner.data.Vars;
import scripts.usa.api.condition.ConditionStatus;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.ResultCondition.Result;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;
import scripts.usa.api2007.enums.Rocks;
import scripts.usa.api2007.enums.Rocks.RockStatus;
import scripts.usa.api2007.teleporting.Teleporting;
import scripts.usa.api2007.teleporting.constants.Destinations;

public class MineClay implements PriorityTask {

	@Override
	public int priority() {
		return 2;
	}

	@Override
	public boolean validate() {
		return !Inventory.isFull();
	}

	@Override
	public void execute() {
		if (Banking.isInBank()) {
			Vars.get().status = "Teleporting to house";
			Teleporting.teleportTo(Destinations.HOUSE);
		}
		else {
			if (Entities.find(ObjectEntity::new).idEquals(Rocks.CLAY.getID(RockStatus.EITHER)).getFirstResult() == null) {
				Vars.get().status = "Walking to Clay";
				Walking.travel(new RSTile(2987, 3239, 0));
			}
			else {
				if (Inventory.open()) {
					if (!Equipment.isEquipped("Bracelet of clay") && Inventory.getCount("Bracelet of clay") > 0) {
						Vars.get().status = "Equipping Bracelet of clay";
						Equipment.equip("Bracelet of clay");
					}
					else {
						Vars.get().status = "Mining Clay";
						Result result = Entity.interact("Mine", Entities.find(ObjectEntity::new).idEquals(Rocks.CLAY.getID(RockStatus.VALID)), () -> {
							if (Conditions.isPlayerActive().isTrue())
								return ConditionStatus.RESET;
							return ConditionStatus.CONTINUE;
						});
						if (result == Result.SUCCESS)
							Vars.get().status = "Mined Clay!";
					}
				}
			}
		}
	}

}
