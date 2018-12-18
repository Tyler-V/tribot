package scripts.green_dragons.tasks;

import org.tribot.api2007.Player;

import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.worlds.WorldHopper;
import scripts.usa.api2007.worlds.WorldType;

public class ChangeWorlds implements PriorityTask {

	@Override
	public boolean validate() {
		return Vars.get().changeWorlds;
	}

	@Override
	public void execute() {
		if (!Wilderness.isIn())
			Vars.get().changeWorlds = false;

		Vars.get().status = "Changing Worlds";
		if (Player.getRSPlayer().isInCombat()) {
			Walking.travel(Vars.get().location.getTeleportTile(), new WalkingCondition() {
				public State action() {
					if (!Player.getRSPlayer().isInCombat())
						return State.EXIT_OUT_WALKER_SUCCESS;
					return State.CONTINUE_WALKER;
				}
			});
		}
		else {
			int world = WorldHopper.getRandomWorld(WorldType.MEMBERS);
			Vars.get().status = "Changing to World " + world;
			if (WorldHopper.changeWorld(world))
				Vars.get().changeWorlds = false;
		}
	}
}
