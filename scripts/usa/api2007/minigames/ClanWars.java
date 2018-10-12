package scripts.usa.api2007.minigames;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class ClanWars {

	public static final RSTile OUTSIDE_PORTAL = new RSTile(3352, 3163, 0);
	public static final RSTile INSIDE_PORTAL = new RSTile(3327, 4751, 0);
	private static final int CLAN_WARS_MASTER = 199;

	public static boolean isInside() {
		return Interfaces.isInterfaceValid(CLAN_WARS_MASTER);
	}

	public static boolean isOutside() {
		return Walking.isTileOnMinimap(OUTSIDE_PORTAL) && PathFinding.canReach(OUTSIDE_PORTAL, false);
	}

	public static boolean enterPortal() {
		if (isInside())
			return true;
		return Entity.interact("Enter", Entities.find(ObjectEntity::new).nameEquals("Free-for-all portal"), () -> isInside());

	}

	public static boolean exitPortal() {
		if (!isInside())
			return true;
		return Entity.interact("Exit", Entities.find(ObjectEntity::new).nameEquals("Portal"), () -> !isInside());
	}
}
