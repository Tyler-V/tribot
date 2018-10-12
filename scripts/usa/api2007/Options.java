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

	public static boolean isQuickPrayer(boolean on) {
		return Game.isQuickPrayersOn() == on;
	}

	public static boolean setQuickPrayer(boolean on) {
		if (isQuickPrayer(on))
			return true;
		Options.setQuickPrayersEnabled(on);
		return Condition.wait(() -> isQuickPrayer(on));
	}

	public static boolean setRun(boolean on) {
		if (Options.isRunEnabled() == on)
			return true;
		Options.setRun(on);
		return Condition.wait(() -> Options.isRunEnabled() == on);
	}
}
