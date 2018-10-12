package scripts.crafter.tasks;

import org.tribot.api.General;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.crafter.data.Locations.Type;
import scripts.crafter.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;

public class Craft implements Task {

	@Override
	public boolean validate() {
		return Vars.get().product.getType() == Type.BANK && !Vars.isInterfaceUp();
	}

	@Override
	public void execute() {
		craft();
	}

	private boolean craft() {
		if (Banking.isOpen())
			Banking.close();

		RSItem[] items = Entities.find(ItemEntity::new).nameEquals(Vars.getActionItems()).getResults();
		if (items.length == 0)
			return false;

		RSItem item = items[General.random(0, items.length - 1)];

		RSItemDefinition itemDefinition = item.getDefinition();
		if (itemDefinition == null)
			return false;

		String itemName = itemDefinition.getName();
		if (itemName == null)
			return false;

		Vars.get().status = "Using " + itemName;

		if (item.click("Use " + itemName))
			Condition.wait(Conditions.isUptext("Use " + itemName + " ->"));

		if (!Game.isUptext("Use " + itemName + " ->"))
			return false;

		RSItem[] materials = Entities.find(ItemEntity::new).nameEquals(Vars.getMaterials()).nameNotEquals(itemName).getResults();
		if (materials.length == 0)
			return false;

		RSItem material = materials[General.random(0, materials.length - 1)];

		RSItemDefinition materialDefinition = material.getDefinition();
		if (materialDefinition == null)
			return false;

		String materialName = materialDefinition.getName();
		if (materialName == null)
			return false;

		Vars.get().status = "Using " + itemName + " on " + materialName;
		if (material.click("Use " + itemName + " -> " + materialName))
			Condition.wait(() -> Vars.isInterfaceUp());

		return true;
	}
}
