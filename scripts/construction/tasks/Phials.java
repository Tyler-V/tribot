package scripts.construction.tasks;

import scripts.construction.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.House;
import scripts.usa.api2007.NPCChat;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;
import scripts.usa.framework.task.Task;

public class Phials implements Task {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean validate() {
		return !Vars.get().hasMaterials();
	}

	@Override
	public void execute() {
		if (House.isInside()) {
			Vars.get().setStatus("Exiting House");
			House.exit();
		}
		if (House.isOutside()) {
			if (!NPCChat.isOptionValid("Exchange All")) {
				Vars.get().setStatus("Interacting with Phials");
				Entity.useItemOn(Entities.find(ItemEntity::new).idEquals(Vars.get().getNotedMaterial()), Entities.find(NpcEntity::new)
						.nameEquals("Phials"), () -> NPCChat.isOptionValid("Exchange All"));
			}

			if (NPCChat.isOptionValid("Exchange All")) {
				Vars.get().setStatus("Exchanging Item");
				NPCChat.selectOption("Exchange All");
				Condition.wait(() -> Vars.get().hasMaterials());
			}
		}
	}
}
