package scripts.usa.api2007.teleporting;

import java.util.Arrays;

import org.tribot.api.General;

import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api2007.teleporting.constants.Destinations;
import scripts.usa.api2007.teleporting.constants.TeleportMethods;

public class Teleporting {

	public static boolean teleport(TeleportMethods method, Destinations destination) {
		if (!method.canUse())
			return false;

		ScriptVars.get().status = "Teleporting to " + destination.getName();

		if (method.toDestination(destination)) {
			Result result = ResultCondition.wait(5000, () -> {
				if (destination.hasArrived())
					return Status.SUCCESS;
				if (Conditions.isPlayerActive().isTrue())
					return Status.RESET;
				return Status.CONTINUE;
			});

			if (result == Result.SUCCESS) {
				ScriptVars.get().status = "Arrived at " + destination.getName();
				General.sleep(General.randomSD(1000, 200));
				return true;
			}
		}

		return false;
	}

	public static boolean teleportTo(Destinations destination) {
		TeleportMethods method = Arrays.stream(TeleportMethods.values())
				.filter(m -> Arrays.asList(m.getDestinations()).contains(destination))
				.findFirst()
				.orElse(null);
		if (method == null)
			return false;

		return teleport(method, destination);
	}
}
