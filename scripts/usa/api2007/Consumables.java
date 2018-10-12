package scripts.usa.api2007;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;

public class Consumables {

	public static boolean hasFood() {
		return Entities.find(ItemEntity::new).actionsEquals("Eat").getFirstResult() != null;
	}

	public static boolean eat() {
		return eat(Entities.find(ItemEntity::new).actionsEquals("Eat").getFirstResult());
	}

	public static boolean eat(String... names) {
		return eat(Entities.find(ItemEntity::new).nameEquals(names).getFirstResult());
	}

	private static boolean eat(RSItem item) {
		if (item == null)
			return false;

		Interfaces.closeAll();

		if (Inventory.open()) {
			final int count = Entities.find(ItemEntity::new).actionsEquals("Eat").getResults().length;
			if (item.click())
				return Condition.wait(() -> count != Entities.find(ItemEntity::new).actionsEquals("Eat").getResults().length);
		}

		return false;
	}

	public static boolean shouldDrink(SKILLS skill) {
		return skill.getCurrentLevel() <= skill.getActualLevel();
	}

	public static boolean drink(SKILLS skill, String... names) {
		if (skill.getCurrentLevel() > skill.getActualLevel())
			return false;
		return drink(names);
	}

	public static boolean drink(String... names) {
		return drink(Entities.find(ItemEntity::new).nameEquals(names).getFirstResult());
	}

	public static boolean drink(RSItem item) {
		if (item == null)
			return false;

		Interfaces.closeAll();

		if (Inventory.open()) {
			final RSItem[] inventory = Inventory.getAll();
			if (item.click())
				return Condition.wait(Conditions.inventoryChanged(inventory));
		}

		return false;
	}
}
