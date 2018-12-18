package scripts.green_dragons.tasks;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Options;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.worlds.WorldHopper;
import scripts.usa.api2007.worlds.WorldType;

public class Evade implements PriorityTask {

	@Override
	public boolean validate() {
		return Vars.get().evade;
	}

	@Override
	public void execute() {
		if (Wilderness.isIn()) {
			if (Vars.get().activateQuickPrayers && Skills.getCurrentLevel(SKILLS.PRAYER) > 0 && !Options.isQuickPrayersEnabled()) {
				Vars.get().status = "Activating Quick Prayers";
				Options.setQuickPrayersEnabled(true);
			}
			Vars.get()
					.leaveWilderness();
		}
		else {
			if (Options.isQuickPrayersEnabled()) {
				Vars.get().status = "Turning off Quick Prayers";
				Options.setQuickPrayersEnabled(false);
			}
			else {
				if (Player.getRSPlayer()
						.isInCombat())
					return;
				int previous = WorldHopper.getCurrentWorld();
				int world = WorldHopper.getRandomWorld(WorldType.MEMBERS);
				Vars.get().status = "Changing to World " + world;
				if (WorldHopper.changeWorld(world)) {
					General.println("Successfully changed from World " + previous + " to " + world);
					Vars.get().evade = false;
					Vars.get().evaded++;
				}
			}
		}
	}
}
