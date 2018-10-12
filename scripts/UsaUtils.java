package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.colour.ColourPoint;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSNPCDefinition;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Utilities", name = "Usa")
public class UsaUtils {

	public static RSTile[] PATH;

	public static boolean isTileOnMinimap(Positionable tile) {
		return Projection.isInMinimap(Projection.tileToMinimap(tile));
	}

	public static void walkToTile(Positionable tile) {
		RSTile[] path = generateScreenPath(Player.getPosition(), tile);
		if (Player.getPosition().distanceTo(tile) <= 7 && path.length > 0) {
			PATH = path;
			walkScreenPath(path);
		} else if (PathFinding.canReach(tile, false)) {
			DPathNavigator d = new DPathNavigator();
			RSTile[] dPath = d.findPath(tile);
			if (isTileOnMinimap(tile)) {
				PATH = new RSTile[] { tile.getPosition() };
				if (!isMoving())
					Walking.clickTileMM(randomizeTile(tile, 1), 1);
				sleepWhileMoving(tile, 3);
			} else if (dPath.length > 0) {
				PATH = dPath;
				walkPath(dPath, 2);
			}
		} else {
			if (!isMoving())
				WebWalking.walkTo(tile);
		}
		PATH = null;
	}

	public static RSTile randomizeTile(Positionable tile, int offset) {
		int x = tile.getPosition().getX() + General.random(-offset, offset);
		int y = tile.getPosition().getY() + General.random(-offset, offset);
		int p = tile.getPosition().getPlane();
		return new RSTile(x, y, p);
	}

	public static RSTile[] generateScreenPath(RSTile start, Positionable end) {
		RSTile[] array = new RSTile[] {};
		if (start == null || end == null)
			return array;
		RSTile[] path = PathFinding.generatePath(start, end, true);
		if (path == null)
			return array;
		ArrayList<RSTile> valid = new ArrayList<RSTile>();
		for (final RSTile tile : path) {
			boolean obstruction = false;
			RSObject[] obj = Objects.getAt(tile);
			if (obj.length > 0) {
				if (obj[0] != null
						&& obj[0].getType().equals(TYPES.INTERACTIVE))
					obstruction = true;
			}
			if (!obstruction) {
				RSNPC[] npc = NPCs.getAll(new Filter<RSNPC>() {
					@Override
					public boolean accept(RSNPC npc) {
						return npc.getPosition().equals(tile);
					}
				});
				if (npc.length == 0)
					valid.add(tile);
			}
		}
		array = new RSTile[valid.size()];
		array = valid.toArray(array);
		return array;
	}

	public static void walkPath(RSTile[] path, int offset) {
		if (path != null && path.length > 0) {
			long timer = System.currentTimeMillis() + 5000;
			while (timer > System.currentTimeMillis()
					&& Player.getPosition().distanceTo(path[path.length - 1]) > 3) {
				RSTile tile = null;
				for (RSTile t : path) {
					tile = t;
					if (!isTileOnMinimap(t))
						break;
				}
				if (tile != null) {
					tile = randomizeTile(tile, 2);
					Walking.clickTileMM(tile, 1);
					sleepWhileMoving(tile, 3);
				}
			}
		}
	}

	public static void walkScreenPath(RSTile[] path) {
		if (path.length > 0) {
			long timer = System.currentTimeMillis() + 10000;
			while (timer > System.currentTimeMillis()
					&& Player.getPosition().distanceTo(path[0]) >= 3) {
				RSTile tile = null;
				for (RSTile t : path) {
					if (t.isOnScreen()) {
						tile = t;
						break;
					}
				}
				if (tile != null) {
					Walking.clickTileMS(tile, "Walk here");
					sleepWhileMoving(tile, 2);
				}
			}
		}
	}

	public static void sleepWhileMoving(Positionable tile, int distanceTo) {
		long sleep = System.currentTimeMillis() + 2000;
		while (sleep > System.currentTimeMillis()) {
			if (isMoving())
				sleep = System.currentTimeMillis() + 1000;
			if (Player.getPosition().distanceTo(tile) <= distanceTo)
				break;
			General.sleep(500);
		}
	}

	public static boolean isMoving() {
		return Player.isMoving() && Game.getDestination() != null;
	}
}