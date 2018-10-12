package scripts.green_dragons.tasks;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;

public class Potions implements PriorityTask {

	@Override
	public int priority() {
		return 5;
	}

	@Override
	public boolean validate() {
		if (Vars.get().potion1 != null && !Vars.get().potion1.isActive())
			return true;
		if (Vars.get().potion2 != null && !Vars.get().potion2.isActive())
			return true;
		if (Vars.get().potion3 != null && !Vars.get().potion3.isActive())
			return true;

		return false;
	}

	@Override
	public void execute() {
		if (Vars.get().potion1 != null && !Vars.get().potion1.isActive())
			Vars.get().potion1.drink();
		if (Vars.get().potion2 != null && !Vars.get().potion2.isActive())
			Vars.get().potion2.drink();
		if (Vars.get().potion3 != null && !Vars.get().potion3.isActive())
			Vars.get().potion3.drink();
	}
}
