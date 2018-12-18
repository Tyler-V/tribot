package scripts.green_dragons.tasks;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.minigames.ClanWars;
import scripts.usa.api2007.teleporting.Teleporting;
import scripts.usa.api2007.teleporting.constants.Destinations;
import scripts.usa.api2007.teleporting.constants.TeleportMethods;

public class ResetPlayer implements PriorityTask {

	@Override
	public boolean validate() {
		return Game.isPoisoned() || Skills.getCurrentLevel(SKILLS.PRAYER) == 0 || Wilderness.isSkulled() || ClanWars.isInside();
	}

	@Override
	public void execute() {
		if (ClanWars.isInside()) {
			Vars.get().status = "Exiting Free-for-all portal";
			ClanWars.exitPortal();
		}
		else {
			if (ClanWars.inArea()) {
				Vars.get().status = "Entering Free-for-all portal";
				ClanWars.enterPortal();
			}
			else {
				if (Vars.get()
						.leaveWilderness()) {
					Vars.get().status = "Teleporting to Clan Wars";
					Teleporting.teleport(TeleportMethods.DUELING_RING, Destinations.CLAN_WARS);
				}
			}
		}
	}
}
