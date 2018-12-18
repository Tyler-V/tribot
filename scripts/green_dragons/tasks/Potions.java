package scripts.green_dragons.tasks;

import org.tribot.api2007.Player;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Inventory;

public class Potions implements PriorityTask {

	@Override
	public boolean validate() {
		if (!Vars.get().location.getArea()
				.contains(Player.getPosition()))
			return false;

		if (Vars.get().potion1 != null && !Vars.get().potion1.isActive() && Inventory.has(Vars.get().potion1.getPredicate()))
			return true;

		if (Vars.get().potion2 != null && !Vars.get().potion2.isActive() && Inventory.has(Vars.get().potion2.getPredicate()))
			return true;

		if (Vars.get().potion3 != null && !Vars.get().potion3.isActive() && Inventory.has(Vars.get().potion3.getPredicate()))
			return true;

		return false;
	}

	@Override
	public void execute() {
		if (Vars.get().potion1 != null && !Vars.get().potion1.isActive() && Inventory.has(Vars.get().potion1.getPredicate()))
			Vars.get().potion1.drink();

		if (Vars.get().potion2 != null && !Vars.get().potion2.isActive() && Inventory.has(Vars.get().potion2.getPredicate()))
			Vars.get().potion2.drink();

		if (Vars.get().potion3 != null && !Vars.get().potion3.isActive() && Inventory.has(Vars.get().potion3.getPredicate()))
			Vars.get().potion3.drink();
	}
}
