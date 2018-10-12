package scripts.green_dragons.tasks;

import org.tribot.api2007.ext.Filters;

import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Inventory;

public class Drop implements PriorityTask {

	@Override
	public int priority() {
		return 3;
	}

	@Override
	public boolean validate() {
		if (Inventory.has(Filters.Items.nameEquals("Vial")))
			return true;

		return false;
	}

	@Override
	public void execute() {
		Inventory.drop(Filters.Items.nameEquals("Vial"));
	}
}
