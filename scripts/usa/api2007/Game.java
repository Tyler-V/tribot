package scripts.usa.api2007;

import java.awt.Rectangle;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.types.RSInterface;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;

public class Game extends org.tribot.api2007.Game {

	private final static int COMBAT_MASTER = 593;

	public static boolean isPoisoned() {
		return Game.getSetting(102) > 0;
	}

	public static boolean isSpecialActivated() {
		return Game.getSetting(301) == 1;
	}

	public static int getSpecialAttackEnergy() {
		return Game.getSetting(300) / 10;
	}

	public static boolean activateSpecial(int requiredEnergy) {
		if (getSpecialAttackEnergy() < requiredEnergy)
			return false;

		if (isSpecialActivated())
			return true;

		ScriptVars.get().status = "Using Special Attack";

		if (GameTab.open(GameTab.TABS.COMBAT)) {
			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(COMBAT_MASTER)
					.actionContains("Special Attack")
					.getFirstResult();
			if (inter == null)
				return false;

			Rectangle bounds = inter.getAbsoluteBounds();
			if (bounds == null)
				return false;

			Mouse.clickBox(bounds, 1);
			return Condition.wait(() -> isSpecialActivated());
		}

		return isSpecialActivated();
	}
}
