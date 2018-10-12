package scripts.construction.tasks;

import java.util.Arrays;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.types.RSInterface;

import scripts.construction.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.House;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.NPCChat;
import scripts.usa.api2007.House.HouseMode;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;
import scripts.usa.framework.task.Task;

public class Build implements Task {

	private final static int BUILD_MASTER = 458;

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean validate() {
		return Vars.get().hasMaterials() && Vars.get().hasTools();
	}

	@Override
	public void execute() {
		if (House.isOutside()) {
			House.enter(HouseMode.BUILD);
		}
		else {
			if (canRemoveFurniture()) {
				Vars.get().setStatus("Removing " + Vars.get().furniture.getName());
				removeFurniture();
			}
			else {
				if (!isBuildMenuUp()) {
					Vars.get().setStatus("Building in " + Vars.get().furniture.getBuildingSpace());
					Entity.interact(Entities.find(ObjectEntity::new)
							.nameEquals(Vars.get().furniture.getBuildingSpace()), "Build", () -> isBuildMenuUp());
				}
				if (isBuildMenuUp()) {
					Vars.get().setStatus("Building " + Vars.get().furniture.getName());
					buildFurniture();
				}
			}
		}
	}

	public boolean canRemoveFurniture() {
		return Entities.find(ObjectEntity::new).actionsEquals("Remove").nameEquals(Vars.get().furniture.getRemoveName()).getResults().length > 0;
	}

	public boolean removeFurniture() {
		if (!isRemoveConfirmationUp())
			Entity.interact(Entities.find(ObjectEntity::new)
					.actionsEquals("Remove")
					.nameEquals(Vars.get().furniture.getRemoveName()), "Remove", () -> isRemoveConfirmationUp());
		if (isRemoveConfirmationUp())
			NPCChat.selectOption("Yes");
		return Condition.wait(() -> Entities.find(ObjectEntity::new)
				.actionsEquals("Remove")
				.nameEquals(Vars.get().furniture.getRemoveName())
				.getFirstResult() == null);
	}

	public boolean isRemoveConfirmationUp() {
		return NPCChat.hasOptions("Yes", "No");
	}

	public boolean isBuildMenuUp() {
		return Interfaces.isInterfaceSubstantiated(BUILD_MASTER);
	}

	public boolean buildFurniture() {
		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(BUILD_MASTER)
				.componentNameContains(Vars.get().furniture.getName())
				.actionEquals("Build")
				.getFirstResult();
		if (inter == null)
			return false;
		int key = getFurnitureKey(inter);
		final int count = Entities.find(ObjectEntity::new).nameEquals(Vars.get().furniture.getRemoveName()).getResults().length;
		if (key > 0) {
			Keyboard.typeSend("" + key);
		}
		else {
			inter.click();
		}
		return Condition.wait(() -> count != Entities.find(ObjectEntity::new).nameEquals(Vars.get().furniture.getRemoveName()).getResults().length);
	}

	public int getFurnitureKey(RSInterface inter) {
		try {
			RSInterface[] children = inter.getChildren();
			RSInterface component = children[children.length - 1];
			String text = component.getText();
			return Integer.parseInt(text.replaceAll("\\D+", ""));
		}
		catch (Exception e) {
			return -1;
		}
	}
}
