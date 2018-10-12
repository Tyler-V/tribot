package scripts.tablets.tasks;

import org.tribot.api2007.types.RSInterface;

import scripts.tablets.data.Vars;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.House;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class MakeTablets implements Task {

	private final int TABLET_MASTER = 79;

	@Override
	public boolean validate() {
		return !Vars.get().hosting && House.isInside() &&
				Entities.find(ItemEntity::new).nameEquals("Soft clay").isNotNoted().getFirstResult() != null;
	}

	@Override
	public void execute() {
		Inventory.open();
		if (!Interfaces.isInterfaceValid(TABLET_MASTER)) {
			Vars.get().status = "Clicking Lectern";
			Entity.interact("Study", Entities.find(ObjectEntity::new)
					.idEquals(Vars.get().lectern.getId()), () -> Interfaces.isInterfaceValid(TABLET_MASTER));
		}
		if (Interfaces.isInterfaceValid(TABLET_MASTER)) {
			Vars.get().status = "Selecting Tablet";
			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(TABLET_MASTER)
					.componentNameContains(Vars.get().tablet.getComponentName())
					.getFirstResult();

			if (inter != null && inter.click(Interfaces.getActionFromAmount(inter, Inventory.getCount("Soft clay")))) {
				Vars.get().status = "Making Tablets";
				ResultCondition.wait(() -> {
					if (Entities.find(ItemEntity::new).nameEquals("Soft clay").isNotNoted().getFirstResult() == null)
						return Status.SUCCESS;
					if (Conditions.isPlayerActive().isTrue())
						return Status.RESET;
					return Status.CONTINUE;
				});
			}
		}
	}
}
