package scripts.usa.api2007;

import org.tribot.api2007.Objects;
import org.tribot.api2007.Projectiles;

public class Cannon {

	private final static int CANNONBALL_GRAPHIC_ID = 53;

	public static boolean exists() {
		return Objects.findNearest(50, "Dwarf multicannon").length > 0;
	}

	public static boolean isFiring() {
		if (!exists())
			return false;

		return Projectiles.getAll(projectile -> {
			return projectile.getGraphicID() == CANNONBALL_GRAPHIC_ID;
		}).length > 0;
	}
}
