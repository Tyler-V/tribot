package scripts.green_dragons.tasks;

import org.tribot.api2007.Player;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Consumables;

public class Eat implements PriorityTask {

	@Override
	public boolean validate() {
		return Player.getRSPlayer().getHealthPercent() <= Vars.get().getNextEatAt() && Consumables.hasFood();
	}

	@Override
	public void execute() {
		Vars.get().status = "Eating";
		if (Consumables.eat()) {
			Vars.get().setNextEatAt();
			Condition.wait(500, () -> Player.getRSPlayer().getHealthPercent() > Vars.get().getNextEatAt());
		}
	}
}
