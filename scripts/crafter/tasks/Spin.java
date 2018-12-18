package scripts.crafter.tasks;

import scripts.crafter.data.Locations.Type;
import scripts.crafter.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class Spin implements PriorityTask {

	@Override
	public boolean validate() {
		return Vars.get().product.getType() == Type.SPINNING_WHEEL && !Vars.isInterfaceUp();
	}

	@Override
	public void execute() {
		if (Banking.isOpen())
			Banking.close();

		if (Walking.onScreen(Vars.get().location.getTile())) {
			Vars.get().status = "Clicking " + Vars.get().location.getObjectName();
			Entity.interact("Spin", Entities.find(ObjectEntity::new).nameEquals(Vars.get().location.getObjectName()), () -> Vars.isInterfaceUp());
		}
		else {
			Vars.get().status = "Traveling to " + Vars.get().location.getName() + " " + Vars.get().location.getObjectName();
			Walking.travel(Vars.get().location.getTile());
		}
	}
}
