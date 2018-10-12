package scripts.usa.api2007;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.abc.preferences.WalkingPreference;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.api_lib.WebWalkerServerApi;
import scripts.dax_api.api_lib.models.DaxCredentials;
import scripts.dax_api.api_lib.models.DaxCredentialsProvider;
import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.util.Timer;

public class Walking extends org.tribot.api2007.Walking {

	static {
		WebWalkerServerApi.getInstance().setDaxCredentialsProvider(new DaxCredentialsProvider() {
			@Override
			public DaxCredentials getDaxCredentials() {
				return new DaxCredentials("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY");
			}
		});
	}

	private static Camera camera = new Camera();
	private static RSTile[] PATH;

	public static RSTile[] getPaintPath() {
		if (PATH == null || PATH.length == 0)
			return new RSTile[0];
		return PATH;
	}

	public static boolean travelToBank() {
		return DaxWalker.walkToBank();
	}

	public static boolean travelToBank(RunescapeBank bank) {
		return DaxWalker.walkToBank(bank, null);
	}

	public static boolean travelToBank(WalkingCondition condition) {
		return DaxWalker.walkToBank(null, condition);
	}

	public static boolean travelToBank(RunescapeBank bank, WalkingCondition condition) {
		return DaxWalker.walkToBank(bank, condition);
	}

	public static boolean travel(Positionable target) {
		return travel(target, 0, null);
	}

	public static boolean travel(Positionable target, int offset) {
		return travel(target, offset, null);
	}

	public static boolean travel(Positionable target, WalkingCondition condition) {
		return travel(target, 0, condition);
	}

	public static boolean travel(Positionable target, int offset, WalkingCondition condition) {
		Interfaces.closeAll();
		try {
			ABC.activateRun();
			if (target == null)
				return false;
			PATH = new RSTile[] { target.getPosition() };
			if (isTileOnMinimap(target) && PathFinding.canReach(target, false)) {
				if (generateWalkingPreference(target) == WalkingPreference.SCREEN) {
					PATH = generatePath(target);
					return walkPath(Style.Screen, target, PATH);
				}
				else {
					return walkToTile(Style.Minimap, target, offset);
				}
			}
			else {
				return DaxWalker.walkTo(target, condition);
			}
		}
		finally {
			PATH = null;
		}
	}

	public static WalkingPreference generateWalkingPreference(Positionable target) {
		RSTile tile = target.getPosition();
		if (tile == null)
			return WalkingPreference.MINIMAP;
		int distance = Player.getPosition().distanceTo(target);
		if (distance == 0)
			distance = 1;
		double chance = 100.0 - (((double) distance) * 8.0);
		double random = General.randomDouble(0.0, 100.0);
		if (chance > random && (tile.isOnScreen() || distance <= 6))
			return WalkingPreference.SCREEN;
		return WalkingPreference.MINIMAP;
	}

	public static boolean onScreen(RSTile tile) {
		return tile != null && tile.isOnScreen() && tile.getPlane() == Game.getPlane();
	}

	public static boolean isTileOnMinimap(Positionable target) {
		RSTile tile = target.getPosition();
		if (tile == null || tile.getPlane() != Game.getPlane())
			return false;
		return Projection.isInMinimap(Projection.tileToMinimap(tile));
	}

	public enum Style {
		Screen,
		Minimap
	}

	public static boolean walkToTile(Style style, Positionable target, int offset) {
		Interfaces.closeAll();
		if (target == null)
			return false;
		if (Player.getPosition().distanceTo(target) == 0)
			return true;
		switch (style) {
			case Minimap:
				if (Walking.clickTileMM(randomize(target, offset), 1))
					return sleepWhileMoving(target, (offset > 0 ? offset : 1));
				break;
			case Screen:
				if (DynamicClicking.clickRSTile(randomize(target, offset), "Walk here")) {
					return sleepWhileMoving(target, (offset > 0 ? offset : 1));
				}
				break;
		}
		return Player.getPosition().distanceTo(target) <= (offset > 0 ? offset : 1);
	}

	public static boolean sleepWhileMoving(Positionable target, int offset) {
		Timer timer = new Timer(2000);
		while (timer.isRunning()) {
			if (Player.getPosition().distanceTo(target) <= offset)
				return true;
			if (target instanceof RSNPC || target instanceof RSObject || target instanceof RSGroundItem) {
				RSTile tile = target.getPosition();
				if (tile.isOnScreen() && tile.isClickable())
					return true;
			}
			if (Player.isMoving())
				timer.reset();
			General.sleep(50);
		}
		return Player.getPosition().distanceTo(target) <= offset;
	}

	public static boolean walkPath(Style style, Positionable target, RSTile[] path) {
		if (path == null || path.length == 0)
			return false;
		if (Player.getPosition().distanceTo(path[path.length - 1]) == 0)
			return true;
		if (style == Style.Screen) {
			if (!path[path.length - 1].isOnScreen())
				camera.setCamera(camera.generateAngle(80), camera.getRotationTo(path[path.length - 1]));
		}
		Timer timer = new Timer(3000);
		while (timer.isRunning()) {
			if (target instanceof RSNPC) {
				RSTile tile = target.getPosition();
				if (tile.isOnScreen() && tile.isClickable())
					return true;
			}
			for (int i = path.length - 1; i > 0; i--) {
				if ((style == Style.Screen && path[i].isOnScreen()) || (style == Style.Minimap && isTileOnMinimap(path[i]))) {
					if (walkToTile(style, path[i], 0))
						timer.reset();
					break;
				}
			}
			if (target instanceof RSObject || target instanceof RSGroundItem) {
				RSTile tile = target.getPosition();
				if (tile.isOnScreen() && tile.isClickable())
					return true;
			}
			if (Player.getPosition().distanceTo(path[path.length - 1]) <= 1) {
				return true;
			}
			camera.turnTo(path[path.length - 1]);
			General.sleep(50);
		}
		return true;
	}

	public static RSTile randomize(Positionable target, int offset) {
		final RSTile tile = target.getPosition();
		if (tile == null)
			return null;
		if (offset == 0)
			return tile;
		return tile.translate(General.random(-offset, offset), General.random(-offset, offset));
	}

	public static RSTile[] generatePath(Positionable start, Positionable end) {
		try {
			return Walking.invertPath(PathFinding.generatePath(start, end, true));
		}
		catch (Exception e) {
			return new RSTile[0];
		}
	}

	public static RSTile[] generatePath(Positionable end) {
		return generatePath(Player.getPosition(), end);
	}
}
