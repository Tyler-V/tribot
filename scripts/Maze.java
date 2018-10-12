package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Randoms", name = "Maze Random")
public class Maze extends Script implements Painting {

	private ArrayList<RSTile> tiles = new ArrayList<RSTile>();
	private ArrayList<RSTile> usedDoors = new ArrayList<RSTile>();
	String version = "9/23/2013 9:09PM EST";

	@Override
	public void run() {

		Camera.setCameraAngle(100);
		super.setRandomSolverState(false);

		while (getDoors().size() > 1) {

			if (touchShrine()) {
				println("Maze Random Complete by Usa!");
			} else {
				tiles.clear();
				getTiles(Player.getPosition(), 50, tiles);
				RSObject door = validDoor();

				if (door != null) {
					if (!Player.isMoving()) {
						if (door.getPosition().distanceTo(Player.getPosition()) > 2) {
							if (!isTileOnMinimap(door.getPosition())) {
								PathFinding.aStarWalk(door.getPosition());
							} else {
								runTo(door.getPosition(), false);
							}
						} else {
							boolean before = PathFinding.canReach(
									door.getPosition(), false);
							println("<<<<<<<<<< >>>>>>>>>>");
							println("Door " + (usedDoors.size() + 1));
							println("Can reach door before: " + before);
							if (openDoor(door)) {
								sleepUntilIdle();
								boolean after = PathFinding.canReach(
										door.getPosition(), false);
								println("Can reach door after: " + after);
								if (before != after) {
									println("Success! Removing "
											+ door.getPosition());
									usedDoors.add(door.getPosition());
								} else {
									println("Error, trying again!");
								}
								println("<<<<<<<<<< >>>>>>>>>>");
							}
						}
					}
				}
			}
		}
	}

	public boolean openDoor(RSObject door) {
		Timer timer = new Timer(5000);

		if (door != null) {
			RSTile pos = door.getPosition();
			println(pos);
			if (pos != null) {
				RSTile pos1 = new RSTile(pos.getX() - 1, pos.getY(),
						pos.getPlane());
				RSTile pos2 = new RSTile(pos.getX() + 1, pos.getY(),
						pos.getPlane());
				RSObject[] obj1 = Objects.getAt(pos1);
				RSObject[] obj2 = Objects.getAt(pos2);
				boolean obj1_wall = false;
				boolean obj2_wall = false;
				for (int i = 0; i < obj1.length; i++) {
					if (obj1[i] != null) {
						if (obj1[i].getDefinition() != null) {
							if (obj1[i].getDefinition().getName()
									.equals("Wall")) {
								obj1_wall = true;
								break;
							}
						}
					}
				}
				for (int i = 0; i < obj2.length; i++) {
					if (obj2[i] != null) {
						if (obj2[i].getDefinition() != null) {
							if (obj2[i].getDefinition().getName()
									.equals("Wall")) {
								obj2_wall = true;
								break;
							}
						}
					}
				}
				if (obj1_wall && obj2_wall) {
					int random = General.random(0, 1);
					if (random == 0) {
						Camera.setCameraRotation(0);
					} else {
						Camera.setCameraRotation(180);
					}
				} else {
					int random = General.random(0, 1);
					if (random == 0) {
						Camera.setCameraRotation(90);
					} else {
						Camera.setCameraRotation(270);
					}
				}
			}

			while (timer.isRunning()) {
				Point p = null;
				Polygon model = door.getModel().getEnclosedArea();
				p = new Point((int) model.getBounds2D().getCenterX(),
						(int) model.getBounds2D().getCenterY());

				if (p != null) {
					Mouse.move(p);
					Mouse.click(3);
					sleep(50, 100);
					if (ChooseOption.isOpen()) {
						if (ChooseOption.select("Open")) {
							sleep(50, 100);
							return true;
						} else {
							ChooseOption.select("Cancel");
							sleep(50, 100);
						}
					}
				}
			}
		}

		return false;
	}

	private ArrayList<RSObject> getDoors() {
		RSObject[] unsortedObjects = Objects.getAll(50);
		RSObject[] obj = Objects.sortByDistance(Player.getPosition(),
				unsortedObjects);
		ArrayList<RSObject> list = new ArrayList<RSObject>();
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != null) {
				RSObjectDefinition definition = obj[i].getDefinition();
				if (definition != null) {
					String[] actions = definition.getActions();
					if (actions != null && actions.length > 0
							&& actions[0].equalsIgnoreCase("Open")) {
						int length = 0;
						length = obj[i].getModel().getPoints().length;
						if (length == 1128) {
							list.add(obj[i]);
						}
					}
				}
			}
		}
		return list;
	}

	private boolean touchShrine() {
		RSObject shrine = getObject("Touch", 10);

		if (tiles.size() == 1) {
			if (shrine != null) {
				if (!Player.isMoving()) {
					if (shrine.click("Touch")) {
						sleep(2000);
						return true;
					}
				}
			}
		}
		return false;
	}

	private RSObject getObject(String s, int distance) {
		RSObject[] obj = Objects.getAll(distance);
		for (int i = 0; i < obj.length; i++) {
			if (obj[i].getDefinition() != null) {
				String[] actions = obj[i].getDefinition().getActions();
				for (int j = 0; j < actions.length; j++) {
					if (actions[j].equalsIgnoreCase(s)) {
						return obj[i];
					}
				}
			}
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

	private void runTo(RSTile tile, boolean screenWalk) {
		if (!Player.isMoving() && Player.getPosition().distanceTo(tile) > 0) {
			if (!tile.isOnScreen() || screenWalk == false) {
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				Walking.walkTo(tile);
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);

			} else if (tile.isOnScreen() && screenWalk == true) {
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				DynamicClicking.clickRSTile(tile, 1);
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
			}
		}
	}

	private void sleepUntilIdle() {
		Timer t = new Timer(1500);

		while (t.isRunning()) {
			if (Player.isMoving() || Player.getAnimation() != -1) {
				t.reset();
			}
			sleep(50, 100);
		}
	}

	private boolean fourDoorSpecialCase() {
		ArrayList<RSObject> door = getDoors();
		int count = 0;

		if (tiles.size() == 66) {
			if (door != null && door.size() > 0) {
				for (int i = 0; i < door.size(); i++) {
					if (door.get(i).getPosition() != null
							&& canReachDoor(door.get(i).getPosition(), 1, 3)) {
						count++;
						if (count == 4) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private RSObject validDoor() {

		ArrayList<RSObject> door = getDoors();

		RSTile first = null;
		RSTile second = null;

		if (door != null && door.size() > 0) {
			if (fourDoorSpecialCase()) {
				// println("Special case!");
				int doorID = 0;
				RSObject fourthDoor = null;
				for (int i = 0; i < door.size(); i++) {
					if (canReachDoor(door.get(i).getPosition(), 1, 3)) {
						if (door.get(i).getID() > doorID) {
							doorID = door.get(i).getID();
							fourthDoor = door.get(i);
						}
					}
				}
				// println(doorID);
				// println(fourthDoor.getPosition());
				return fourthDoor;
			} else {
				for (int i = 0; i < door.size(); i++) {
					if (door.get(i).getPosition() != null
							&& canReachDoor(door.get(i).getPosition(), 1, 3)
							&& !isUsedDoor(door.get(i).getPosition())) {
						if (usedDoors.size() == 0) {
							if (first == null) {
								first = door.get(i).getPosition();
							} else if (second == null) {
								second = door.get(i).getPosition();
							}
							if (first != null && second != null) {
								if (possibleDeadEnd(first, second)) {
									// println("Found a dead end! Removing "
									// + first);
									usedDoors.add(first);
									return null;
								}
							}
						}
						return door.get(i);
					}
				}
			}
		}
		return null;
	}

	private boolean possibleDeadEnd(RSTile door1, RSTile door2) {
		if (door1.distanceTo(door2) == 8 || door1.distanceTo(door2) == 5) {
			return true;
		}
		return false;
	}

	private boolean isUsedDoor(RSTile tile) {

		for (int i = 0; i < usedDoors.size(); i++) {
			if (usedDoors.get(i) != null && tile != null) {
				if (usedDoors.get(i).equals(tile)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean canReachDoor(RSTile tile, int distance, int tilesTouching) {

		int count = 0;
		for (int i = 0; i < tiles.size(); i++) {
			int x = tile.getX();
			int y = tile.getY();
			RSTile temp = tiles.get(i);
			if (temp != null && tile != null) {
				if (temp.distanceTo(tile) <= distance) {
					count++;
				}
			}
			if (count >= tilesTouching) {
				return true;
			}
		}

		return false;
	}

	private void getTiles(RSTile pos, int radius, ArrayList<RSTile> list) {

		int diameter = (1 + (2 * radius));
		int plane = pos.getPlane();

		int x = pos.getX() - radius;
		int y = pos.getY() + radius;

		for (int i = 0; i < diameter; i++) {
			x = pos.getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile temp = new RSTile(x, y, plane);
				if (PathFinding.canReach(temp, false)) {
					list.add(temp);
				}
				x += 1;
			}
			y -= 1;
		}
	}

	private class Timer {

		private long end;
		private final long start;
		private final long period;

		public Timer(final long period) {
			this.period = period;
			start = System.currentTimeMillis();
			end = start + period;
		}

		public Timer(final long period, long addition) {
			this.period = period;
			start = System.currentTimeMillis() + addition;
			end = start + period;
		}

		public long getElapsed() {
			return System.currentTimeMillis() - start;
		}

		public long getRemaining() {
			if (isRunning()) {
				return end - System.currentTimeMillis();
			}
			return 0;
		}

		public boolean isRunning() {
			return System.currentTimeMillis() < end;
		}

		public void reset() {
			end = System.currentTimeMillis() + period;
		}

		public long setEndIn(final long ms) {
			end = System.currentTimeMillis() + ms;
			return end;
		}

		public String toElapsedString() {
			return format(getElapsed());
		}

		public String toRemainingString() {
			return format(getRemaining());
		}

		public String format(final long time) {
			final StringBuilder t = new StringBuilder();
			final long total_secs = time / 1000;
			final long total_mins = total_secs / 60;
			final long total_hrs = total_mins / 60;
			final int secs = (int) total_secs % 60;
			final int mins = (int) total_mins % 60;
			final int hrs = (int) total_hrs % 60;
			if (hrs < 10) {
				t.append("0");
			}
			t.append(hrs);
			t.append(":");
			if (mins < 10) {
				t.append("0");
			}
			t.append(mins);
			t.append(":");
			if (secs < 10) {
				t.append("0");
			}
			t.append(secs);
			return t.toString();
		}
	}

	public void onPaint(Graphics g) {
		g.setColor(Color.RED);

		for (int i = 0; i < tiles.size(); i++) {
			g.drawPolygon(Projection.getTileBoundsPoly(tiles.get(i), 0));
		}
	}
}