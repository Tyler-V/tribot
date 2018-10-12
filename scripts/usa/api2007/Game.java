package scripts.usa.api2007;

import java.awt.Rectangle;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;

public class Game extends org.tribot.api2007.Game {

	private final static int COMBAT_MASTER = 593;
	private final static int COMBAT_USE_SPECIAL_CHILD = 30;

	public static boolean isPoisoned() {
		return Game.getSetting(102) > 0;
	}

	public static boolean isSpecialActivated() {
		return Game.getSetting(301) == 1;
	}

	public static int getSpecialAttackEnergy() {
		return Game.getSetting(300) / 10;
	}

	public static boolean activateSpecial(int energy) {
		if (getSpecialAttackEnergy() < energy)
			return false;
		if (isSpecialActivated())
			return true;
		if (GameTab.open(GameTab.TABS.COMBAT)) {
			RSInterfaceChild child = Interfaces.get(COMBAT_MASTER, COMBAT_USE_SPECIAL_CHILD);
			if (child == null)
				return false;
			Rectangle bounds = child.getAbsoluteBounds();
			if (bounds == null)
				return false;
			Mouse.clickBox(bounds, 1);
			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return isSpecialActivated();
				}
			}, 2000);
		}
		return isSpecialActivated();
	}
}
