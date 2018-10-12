package scripts.green_dragons.tasks;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.teleporting.Teleporting;
import scripts.usa.api2007.teleporting.constants.Destinations;
import scripts.usa.api2007.teleporting.constants.TeleportMethods;

public class Evade implements PriorityTask {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean validate() {
		return Vars.get().evade;
	}

	@Override
	public void execute() {
		Vars.get().status = "Evading!";
		if (Teleporting.teleport(TeleportMethods.GLORY, Destinations.EDGEVILLE))
			Vars.get().evade = false;
	}
}
