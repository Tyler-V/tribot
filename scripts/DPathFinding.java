package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Walking", name = "DPathFinding")
public class DPathFinding extends Script implements Painting {

	private ArrayList<RSTile> PAINT_TILES = new ArrayList<RSTile>();
	private RSTile PAINT_BEST = null;
	private String status = "";
	private final RSTile ALTAR_LOCATION = new RSTile(3362, 3155, 0);

	public void run() {
		long start = System.currentTimeMillis();
		PAINT_BEST = getClosestTileTo(ALTAR_LOCATION);
		println("Found best tile at " + PAINT_BEST + " in "
				+ (System.currentTimeMillis() - start) + " ms.");
		Mouse.move(Projection.tileToMinimap(PAINT_BEST));
	}

	private void blindWalk(RSTile tile) {
		RSTile closest = getClosestTileTo(tile);
		if (closest != null) {
			if (!Player.isMoving())
				Walking.clickTileMM(closest, 1);
		}
	}

	private RSTile getClosestTileTo(RSTile destination) {
		int radius = 10;
		int diameter = (1 + (2 * radius));
		int x = Player.getPosition().getX() - radius;
		int y = Player.getPosition().getY() + radius;
		int p = Player.getPosition().getPlane();

		int distance = Integer.MAX_VALUE;
		RSTile closest = null;

		for (int i = 0; i < diameter; i++) {
			x = Player.getPosition().getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile tile = new RSTile(x, y, p);
				if (tile.distanceTo(destination) < Player.getPosition()
						.distanceTo(destination)) {
					final RSObject[] obstructions = Objects.getAt(tile,
							new Filter<RSObject>() {
								@Override
								public boolean accept(RSObject obj) {
									if (obj == null)
										return false;
									TYPES type = obj.getType();
									if (type == null)
										return false;
									if (type.equals(RSObject.TYPES.FLOOR))
										return false;
									return true;
								}
							});
					if (obstructions.length == 0) {
						if (PathFinding.canReach(tile, false)) {
							if (destination.distanceTo(tile) < distance) {
								distance = destination.distanceTo(tile);
								closest = tile;
							}
						}
					}
				}
				x += 1;
			}
			y -= 1;
		}
		return closest;
	}

	private ArrayList<RSTile> getAllTiles(RSTile destination) {
		ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		int radius = 10;
		int diameter = (1 + (2 * radius));
		int x = Player.getPosition().getX() - radius;
		int y = Player.getPosition().getY() + radius;
		int p = Player.getPosition().getPlane();

		for (int i = 0; i < diameter; i++) {
			x = Player.getPosition().getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile tile = new RSTile(x, y, p);
				if (tile.distanceTo(destination) < Player.getPosition()
						.distanceTo(destination)) {
					final RSObject[] obstructions = Objects.getAt(tile,
							new Filter<RSObject>() {
								@Override
								public boolean accept(RSObject obj) {
									if (obj == null)
										return false;
									TYPES type = obj.getType();
									if (type == null)
										return false;
									if (type.equals(RSObject.TYPES.FLOOR))
										return false;
									return true;
								}
							});
					if (obstructions.length == 0) {
						if (PathFinding.canReach(tile, false))
							tiles.add(tile);
					}
				}
				x += 1;
			}
			y -= 1;
		}
		return tiles;
	}

	private ArrayList<RSTile> getValidTiles(int radius) {
		ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		int diameter = (1 + (2 * radius));
		int x = Player.getPosition().getX() - radius;
		int y = Player.getPosition().getY() + radius;
		int p = Player.getPosition().getPlane();

		for (int i = 0; i < diameter; i++) {
			x = Player.getPosition().getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile tile = new RSTile(x, y, p);
				final RSObject[] obstructions = Objects.find(radius,
						new Filter<RSObject>() {
							@Override
							public boolean accept(RSObject obj) {
								if (obj != null
										&& obj.getType().equals(
												RSObject.TYPES.BOUNDARY)
										&& obj.getPosition().distanceTo(tile) <= 1) {
									return true;
								}
								return false;
							}
						});
				if (obstructions.length == 0) {
					if (PathFinding.canReach(tile, false))
						tiles.add(tile);
				}
				x += 1;
			}
			y -= 1;
		}
		return tiles;
	}

	public void onPaint(Graphics g) {
		g.setColor(Color.RED);
		if (PAINT_TILES.size() > 0) {
			for (RSTile t : PAINT_TILES) {
				g.drawPolygon(Projection.getTileBoundsPoly(t, 0));
				Point point = Projection.tileToMinimap(t);
				g.fillRect((int) point.getX(), (int) point.getY(), 2, 2);
			}
		}
		g.setColor(Color.GREEN);
		if (PAINT_BEST != null) {
			g.drawPolygon(Projection.getTileBoundsPoly(PAINT_BEST, 0));
			Point point = Projection.tileToMinimap(PAINT_BEST);
			g.fillRect((int) point.getX(), (int) point.getY(), 5, 5);
		}
		g.drawString("Status: " + status, 530, 160);
	}

	private void DTravel(RSTile target) {
		DPathNavigator d = new DPathNavigator();
		ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		tiles = getTiles(Player.getPosition());
		RSTile[] path = null;
		if (!PathFinding.canReach(target, false)) {
			if (tiles.size() > 0) {
				RSTile closest = getClosestTileToTile(target, tiles);
				if (closest != null) {
					status = "Generating Path";
					path = d.findPath(closest);
				}
			}
		} else {
			status = "Generating Path";
			path = d.findPath(target);
		}
		if (path != null && path.length > 0) {
			walkPath(path);
		} else {
			println("No path!");
		}

		String[] options = ChooseOption.getOptions();
		if (options.length > 1) {
			if (options[1] != null)
				ChooseOption.select(options[1]);
		}
	}

	private ArrayList<RSTile> getTiles(RSTile pos) {

		status = "Grabbing All Tiles";

		ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		int radius = 30;
		int diameter = (1 + (2 * radius));
		int plane = pos.getPlane();

		int x = pos.getX() - radius;
		int y = pos.getY() + radius;

		for (int i = 0; i < diameter; i++) {
			x = pos.getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile temp = new RSTile(x, y, plane);
				if (PathFinding.canReach(temp, false)) {
					tiles.add(temp);
				}
				x += 1;
			}
			y -= 1;
		}
		return tiles;
	}

	private RSTile getClosestTileToTile(RSTile target, ArrayList<RSTile> tiles) {
		if (tiles.size() > 0) {
			status = "Searching for Closest Tile";
			RSTile closest = tiles.get(0);
			int distance = target.distanceTo(tiles.get(0));
			for (int i = 1; i < tiles.size(); i++) {
				if (target.distanceTo(tiles.get(i)) < distance) {
					closest = tiles.get(i);
					distance = target.distanceTo(tiles.get(i));
				}
			}
			return closest;
		}
		return null;
	}

	private boolean isTileOnMinimap(RSTile t) {
		if (Projection.isInMinimap(Projection.tileToMinimap(t))) {
			return true;
		} else {
			return false;
		}
	}

	private void walkPath(final RSTile[] path) {
		RSTile pos = Player.getPosition();
		if (pos != null) {
			status = "Navigating Path";
			while (!(path[path.length - 1].distanceTo(pos) <= 2)) {
				RSTile tile = null;
				for (int i = path.length - 1; i >= 0; i--) {
					if (isTileOnMinimap(path[i])) {
						tile = path[i];
						break;
					}
				}
				if (tile != null) {
					Walking.setControlClick(true);
					Walking.clickTileMM(tile, 1);
					Walking.setControlClick(false);
					if (tile.equals(path[path.length - 1])) {
						status = "At End Of Path";
						break;
					}
					final RSTile end = tile;
					Timing.waitCondition(new Condition() {
						public boolean active() {
							sleep(250, 500);
							return Player.getPosition().distanceTo(end) <= 7;
						}
					}, 5000);
				}
			}
		}
	}
}