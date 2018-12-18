package scripts.usa.api2007.observers.teleblock;

import org.tribot.api.General;

import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.threads.VolatileRunnable;
import scripts.usa.api2007.Wilderness;

public class Teleblock extends VolatileRunnable {

	@Override
	public void execute() {
		try {
			if (ScriptVars.get().teleblockTimer == null)
				return;

			if (!Wilderness.isIn()) {
				General.println("The effect of the teleblock has expired by leaving the Wilderness.");
				ScriptVars.get().teleblockTimer = null;
			}

			if (!ScriptVars.get().teleblockTimer.isRunning()) {
				General.println("The effect of the teleblock has expired by time.");
				ScriptVars.get().teleblockTimer = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
