package scripts.usa.api2007;

import org.tribot.api2007.GameTab;

import scripts.usa.api.condition.Condition;

public class Options extends org.tribot.api2007.Options {

	public static final int TAB_MASTER_INDEX = 261;

	public static boolean open() {
		if (GameTab.getOpen() == GameTab.TABS.OPTIONS)
			return true;
		GameTab.open(GameTab.TABS.OPTIONS);
		return Condition.wait(() -> GameTab.getOpen() == GameTab.TABS.OPTIONS);
	}

	public static boolean setRun(boolean active) {
		if (Options.isRunEnabled() == active)
			return true;
		Options.setRun(active);
		return Condition.wait(() -> Options.isRunEnabled() == active);
	}
}
