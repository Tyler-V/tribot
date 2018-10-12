package scripts.tablets.tasks;

import scripts.tablets.data.Vars;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.House;
import scripts.usa.api2007.House.HouseMode;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;

public class EnterHouse implements Task {

	@Override
	public boolean validate() {
		return House.isOutside() && (Vars.get().servant || Vars.get().hosting ||
				Entities.find(ItemEntity::new).nameEquals("Soft clay").isNotNoted().getFirstResult() != null);
	}

	@Override
	public void execute() {
		if (Vars.get().houseMode == HouseMode.HOME) {
			Vars.get().status = "Entering House";
			House.enter(HouseMode.HOME);
		}
		else {
			Vars.get().status = "Entering " + Vars.get().friendName + "'s House";
			House.enter(HouseMode.FRIENDS, Vars.get().friendName);
		}
	}
}
