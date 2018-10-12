package scripts.usa.api2007;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;

public class Wilderness {

	private final static int WILDERNESS_LEVEL_MASTER = 90;
	private final static int ENTER_WILDERNESS_MASTER = 382;
	private final static int ENTER_WILDERNESS_CHILD = 18;

	public static boolean isWarningUp() {
		return Interfaces.isInterfaceValid(ENTER_WILDERNESS_MASTER);
	}

	public static boolean enter() {
		if (!isWarningUp())
			return false;
		RSInterfaceChild child = Interfaces.get(ENTER_WILDERNESS_MASTER, ENTER_WILDERNESS_CHILD);
		if (child == null)
			return false;
		final RSTile tile = Player.getPosition();
		if (child.click()) {
			long timer = System.currentTimeMillis() + 3000;
			while (timer > System.currentTimeMillis()) {
				if (Player.isMoving() || Player.getAnimation() != -1)
					timer = System.currentTimeMillis() + 3000;
				if (!PathFinding.canReach(tile, false))
					return true;
				General.sleep(General.random(0, 100));
			}
		}
		return false;
	}

	public static boolean isSkulled() {
		RSPlayer player = Player.getRSPlayer();
		if (player == null)
			return false;
		return player.getSkullIcon() == 0;
	}

	public static boolean isSkulled(RSPlayer player) {
		if (player == null)
			return false;
		return player.getSkullIcon() == 0;
	}

	public static boolean isIn() {
		return Interfaces.isInterfaceValid(WILDERNESS_LEVEL_MASTER) || Player.getPosition().getY() > 3525;
	}

	public static int getLevel() {
		if (!isIn())
			return 0;

		try {
			RSInterface inter = Entities.find(InterfaceEntity::new).inMaster(WILDERNESS_LEVEL_MASTER).textContains("Level: ").getFirstResult();
			return Integer.parseInt(inter.getText().replace("Level: ", ""));
		}
		catch (Exception e) {
			return 0;
		}
	}
}
