package scripts.green_dragons.tasks;

import org.tribot.api2007.Player;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.looting.LootingBag;
import scripts.usa.api2007.looting.LootingBag.BagOption;

public class Loot implements PriorityTask {

	@Override
	public int priority() {
		return 4;
	}

	@Override
	public boolean validate() {
		return Vars.get().location.getArea().contains(Player.getPosition()) && Vars.get().looter.hasFoundLoot();
	}

	@Override
	public void execute() {
		if (LootingBag.hasLootingBag() && LootingBag.isState(BagOption.CLOSE)) {
			Vars.get().status = "Opening Looting Bag";
			LootingBag.select(BagOption.OPEN);
		}
		Vars.get().status = "Looting!";
		Vars.get().looter.loot();
	}
}
