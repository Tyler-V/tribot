package scripts.usa.api2007.minigames;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class ClanWars {

	public static final RSArea CLAN_WARS_AREA = new RSArea(new RSTile[] { new RSTile(3350, 3165, 0), new RSTile(3361, 3174, 0), new RSTile(3365, 3177, 0), new RSTile(3369, 3177, 0), new RSTile(3375, 3173, 0), new RSTile(3391, 3172, 0),
			new RSTile(3391, 3157, 0), new RSTile(3392, 3148, 0), new RSTile(3384, 3148, 0), new RSTile(3366, 3139, 0), new RSTile(3355, 3148, 0) });

	public static final RSTile OUTSIDE_PORTAL = new RSTile(3352, 3163, 0);
	public static final RSTile INSIDE_PORTAL = new RSTile(3327, 4751, 0);
	private static final int CLAN_WARS_MASTER = 199;

	public static boolean inArea() {
		return CLAN_WARS_AREA.contains(Player.getPosition());
	}

	public static boolean isInside() {
		return Interfaces.isInterfaceValid(CLAN_WARS_MASTER);
	}

	public static boolean isOutside() {
		return Walking.isTileOnMinimap(OUTSIDE_PORTAL) && PathFinding.canReach(OUTSIDE_PORTAL, false);
	}

	private static ObjectEntity getEnterPortalEntity() {
		return Entities.find(ObjectEntity::new)
				.nameEquals("Free-for-all portal");
	}

	public static boolean enterPortal() {
		if (isInside())
			return true;
		if (Player.getPosition()
				.distanceTo(OUTSIDE_PORTAL) > 10) {
			Walking.travel(OUTSIDE_PORTAL, new WalkingCondition() {
				public State action() {
					if (getEnterPortalEntity().getFirstResult() != null)
						return State.EXIT_OUT_WALKER_SUCCESS;
					return State.CONTINUE_WALKER;
				}
			});
		}
		return Entity.interact("Enter", getEnterPortalEntity(), () -> isInside());

	}

	private static ObjectEntity getExitPortalEntity() {
		return Entities.find(ObjectEntity::new)
				.nameEquals("Portal");
	}

	public static boolean exitPortal() {
		if (!isInside())
			return true;
		return Entity.interact("Exit", getExitPortalEntity(), () -> !isInside() && Skills.getCurrentLevel(SKILLS.PRAYER) == Skills.getActualLevel(SKILLS.PRAYER) && !Game.isPoisoned() && !Wilderness.isSkulled());
	}
}
