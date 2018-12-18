package scripts.green_dragons.tasks;

import org.tribot.api2007.Player;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.looting.LootingBag;
import scripts.usa.api2007.looting.LootingBag.BagOption;

public class Loot implements PriorityTask {

	@Override
	public boolean validate() {
		return Vars.get().location.getArea()
				.contains(Player.getPosition()) && (Vars.get().looter.hasFoundLoot() || (Vars.get().ammunitionLooter != null && Vars.get().ammunitionLooter.hasFoundLoot()));
	}

	@Override
	public void execute() {
		if (Vars.get().looter.hasFoundLoot()) {
			if (LootingBag.select(BagOption.OPEN)) {
				Vars.get().looter.loot();
			}
		}
		else if (Vars.get().ammunitionLooter.hasFoundLoot()) {
			if (LootingBag.select(BagOption.CLOSE)) {
				if (Vars.get().ammunitionLooter.loot()) {
					Equipment.equip(Vars.get().arrow.getName());
				}
			}
		}
	}
}
