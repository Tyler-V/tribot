package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "USA", name = "USA Flax Spinner")
public class UsaFlaxSpinner extends Script implements Painting {

	private String version = "6.0";
	private long startTime;
	private int flaxSpun = 0;
	private int startCraftingLevel = 0;
	private int startCraftingXP = 0;
	private String status = "";
	private int animationCount = 0;

	private final int FLAX_ID = 1779;
	private final int BOW_STRING_ID = 1777;
	private final RSTile BANK_TILE = new RSTile(3209, 3219, 2);
	private final RSTile STAIRS_TILE_2 = new RSTile(3205, 3209, 2);
	private final RSTile STAIRS_TILE_1 = new RSTile(3206, 3208, 1);
	private final RSTile STAIRS_TILE_0 = new RSTile(3207, 3210, 0);
	private final RSTile DOOR_TILE_ENTER = new RSTile(3207, 3214, 1);
	private final RSTile TRAPPED_DOOR = new RSTile(3207, 3210, 1);
	private final RSTile TRAPPED_DOOR_WALK = new RSTile(3208, 3210, 1);
	private final RSTile WHEEL_TILE = new RSTile(3210, 3213, 1);

	private final RSArea WHEEL_AREA = new RSArea(new RSTile(3208, 3212, 1), new RSTile(3212, 3217, 1));
	private final RSArea TRAPPED_AREA = new RSArea(new RSTile(3208, 3209, 1), new RSTile(3216, 3211, 1));
	private final RSArea BANK_AREA = new RSArea(new RSTile(3207, 3218, 2), new RSTile(3210, 3220, 2));
	private final RSArea LUMBRIDGE_CELLAR = new RSArea(new RSTile(3208, 9615, 0), new RSTile(3219, 9625, 0));
	private final RSArea LUMBRIDGE = new RSArea(new RSTile(3203, 3206), new RSTile(3225, 3235));

	RSTile[] lumbridgeToStairs = new RSTile[] { new RSTile(3215, 3218, 0), new RSTile(3208, 3211, 0) };

	private final Image open = getImage("http://i.imgur.com/4ewbmRU.png");

	int error = 1;
	private boolean run = true;

	private ABCUtil abc;

	public void run() {

		abc = new ABCUtil();

		startTime = System.currentTimeMillis();

		int mouseSpeed = General.random(105, 125);
		Mouse.setSpeed(mouseSpeed);
		println("Set mouse speed to " + mouseSpeed);

		startCraftingXP = Skills.getXP(Skills.SKILLS.CRAFTING);
		startCraftingLevel = Skills.getActualLevel(Skills.SKILLS.CRAFTING);

		while (run) {

			if (!Game.isRunOn() && (Game.getRunEnergy() >= abc.INT_TRACKER.NEXT_RUN_AT.next())) {
				Options.setRunOn(true);
				abc.INT_TRACKER.NEXT_RUN_AT.reset();
			}

			if (Camera.getCameraAngle() < 80) {
				Camera.setCameraAngle(95);
			} else if (Camera.getCameraRotation() < 80 || Camera.getCameraRotation() > 90) {
				Camera.setCameraRotation(85);
			}

			if (Game.getPlane() == 2 && inArea(LUMBRIDGE)) {
				if (inventoryContains(BOW_STRING_ID) || !inventoryContains(FLAX_ID)) {
					if (atBank()) {
						if (error > 10) {
							run = false;
							println("Failed to bank 10 times");
						}

						if (Banking.openBank()) {
							status = "Depositing";
							flaxSpun += Inventory.getCount(BOW_STRING_ID);
							if (Inventory.getAll().length != Inventory.getCount(FLAX_ID))
								Banking.depositAll();
							RSItem[] flax = Banking.find(FLAX_ID);
							if (flax.length > 0) {
								if (flax[0] != null) {
									final int count = Inventory.getCount(FLAX_ID);
									if (Banking.withdrawItem(flax[0], 0)) {
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep(100, 250);
												return Inventory.getCount(FLAX_ID) != count;
											}
										}, 1000);
										error = 1;
									} else {
										error++;
										status = "Error finding Flax";
										println(error + ": Failed to find Flax");
										Banking.close();
									}

								}
							}
						}
					} else {
						status = "Moving To Bank";
						runTo(BANK_TILE, true);
						sleepUntilNotMoving(BANK_TILE);
					}
				} else if (inventoryContains(FLAX_ID)) {
					RSObject stairs = getObject("Climb-down", 20);
					if (stairs != null) {
						if (stairs.isOnScreen() && Player.getPosition().distanceTo(stairs) <= 3) {
							status = "Going Down Stairs";
							int currentPlane = Game.getPlane();
							if (clickObject(stairs, "Climb-down")) {
								sleepUntilPlaneChange(currentPlane);
							}
						} else {
							status = "Moving To Stairs";
							runTo(STAIRS_TILE_2, true);
							sleepUntilNotMoving(STAIRS_TILE_2);
						}
					}
				}
			} else if (Game.getPlane() == 1 && inArea(LUMBRIDGE)) {
				RSInterface spinFlax = Interfaces.get(459, 94);
				if (inventoryContains(FLAX_ID) && !Player.getRSPlayer().isInCombat()) {
					if (atWheel()) {
						status = "At Wheel";
						if (Player.getAnimation() == -1) {
							openInventory();
							RSObject wheel = getObject("Spin", 10);
							if (inventoryContains(FLAX_ID)) {
								if (Interfaces.isInterfaceValid(459)) {
									if (Inventory.getCount(FLAX_ID) > 0) {
										status = "Making X";
										spinFlax.click("Make X");
										sleepUntilEnterAmountIsUp();
										if (enterAmountMenuUp()) {
											status = "Spinning " + Inventory.getCount(FLAX_ID) + " Flax";
											Keyboard.typeSend("" + Inventory.getCount(FLAX_ID));
											sleep(800);
											sleepWhileSpinning();
										}
									}
								} else {
									if (wheel != null) {
										if (wheel.isOnScreen() && Player.getPosition().distanceTo(wheel) <= 3) {
											if (Inventory.getCount(FLAX_ID) > 0) {
												status = "Clicking Wheel";
												if (wheel.click("Spin")) {
													sleep(1300, 1500);
												}
											}
										}
									}
								}
							}
						}
					} else {
						status = "Second Floor";
						RSObject[] closedDoor1 = Objects.getAt(DOOR_TILE_ENTER);
						RSObject[] closedDoor2 = Objects.getAt(TRAPPED_DOOR);
						if (inArea(TRAPPED_AREA) && closedDoor2 != null && closedDoor2.length > 0
								&& closedDoor2[0].getPosition().distanceTo(TRAPPED_DOOR) < 2) {
							status = "We're trapped!";
							if (Player.getPosition().distanceTo(TRAPPED_DOOR_WALK) > 1) {
								status = "Moving To Door";
								runTo(TRAPPED_DOOR_WALK, true);
								sleepUntilNotMoving(TRAPPED_DOOR_WALK);
							} else {
								if (closedDoor2 != null) {
									status = "Opening Door";
									closedDoor2[0].click("Open");
									waitUntilIdle();
								}
							}
						} else if (closedDoor1 != null && closedDoor1.length > 0
								&& closedDoor1[0].getPosition().distanceTo(DOOR_TILE_ENTER) < 2) {
							status = "Door Is Closed";
							if (Player.getPosition().distanceTo(DOOR_TILE_ENTER) > 1) {
								status = "Moving To Door";
								runTo(DOOR_TILE_ENTER, true);
								sleepUntilNotMoving(DOOR_TILE_ENTER);
							} else {
								if (closedDoor1 != null) {
									status = "Opening Door";
									closedDoor1[0].click("Open");
									waitUntilIdle();
								}
							}
						} else {
							runTo(WHEEL_TILE, true);
						}
					}

				} else {
					status = "Leaving Wheel";

					RSInterface closeMenu = Interfaces.get(459, 131);
					if (closeMenu != null) {
						status = "Closing Spin Menu";
						closeMenu.click();
						sleep(1000);
					} else {
						RSObject[] closedDoor = Objects.getAt(DOOR_TILE_ENTER);
						if (Player.getPosition().getX() > 3207 && closedDoor != null && closedDoor.length > 0
								&& closedDoor[0].getPosition().distanceTo(DOOR_TILE_ENTER) < 3) {
							status = "Opening Door";
							closedDoor[0].click("Open");
							waitUntilIdle();
						} else {
							RSObject stairs = getObject("Climb-up", 20);
							if (stairs != null) {
								if (stairs.isOnScreen() && Player.getPosition().distanceTo(STAIRS_TILE_1) <= 3) {
									status = "Climbing Up Stairs";
									int currentPlane = Game.getPlane();
									if (clickObject(stairs, "Climb-up")) {
										sleepUntilPlaneChange(currentPlane);
									}
								} else {
									status = "Moving To Stairs";
									runTo(STAIRS_TILE_1, true);
									sleepUntilNotMoving(STAIRS_TILE_1);
								}
							}
						}
					}
				}
			} else if (Game.getPlane() == 0) {
				status = "First Floor";
				RSObject stairs = getObject("Climb-up", 20);

				if (stairs != null) {
					if (!stairs.isOnScreen()) {
						status = "Moving To Stairs";
						runTo(STAIRS_TILE_0, true);
						sleepUntilNotMoving(STAIRS_TILE_0);
					} else {
						if (stairs != null && stairs.isOnScreen()) {
							status = "Climbing Up Stairs";
							int currentPlane = Game.getPlane();
							if (clickObject(stairs, "Climb-up")) {
								sleepUntilPlaneChange(currentPlane);
							}
						}
					}
				} else {
					status = "Walking to Stairs";
					if (!Player.isMoving())
						Walking.walkPath(lumbridgeToStairs);
				}
			}
		}
		abc.performXPCheck(SKILLS.CRAFTING);
		abc.performRotateCamera();
		abc.performExamineObject();
		abc.performPickupMouse();
		abc.performRandomMouseMovement();
		abc.performRandomRightClick();
		abc.performQuestsCheck();
		abc.performFriendsCheck();
		abc.performMusicCheck();
		abc.performCombatCheck();
		sleep(100);
	}

	private boolean clickObject(RSObject obj, String action) {
		if (obj != null) {
			Point p = null;
			RSModel model = obj.getModel();
			if (model != null) {
				Polygon area = model.getEnclosedArea();
				p = new Point((int) area.getBounds2D().getCenterX(), (int) area.getBounds2D().getCenterY());
				if (p != null) {
					Mouse.move(p);
					Mouse.click(3);
					sleep(50, 100);
					if (ChooseOption.isOpen()) {
						if (ChooseOption.select(action)) {
							return true;
						} else {
							ChooseOption.select("Cancel");
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	private RSObject getObject(String s, int distance) {
		RSObject[] obj = Objects.getAll(distance);
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null) {
					RSObjectDefinition def = obj[i].getDefinition();
					if (def != null) {
						String[] actions = def.getActions();
						if (actions != null && actions.length > 0) {
							for (int j = 0; j < actions.length; j++) {
								if (actions[j] != null) {
									if (actions[j].equalsIgnoreCase(s)) {
										return obj[i];
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			status = "Opening Inventory";
			Inventory.open();
			sleep(50, 150);
		}
	}

	private void sleepUntilEnterAmountIsUp() {
		Timer t = new Timer(2000);
		while (t.isRunning()) {
			if (enterAmountMenuUp()) {
				break;
			}
			sleep(50, 100);
		}
	}

	private boolean enterAmountMenuUp() {
		return !Interfaces.get(162, 32).isHidden();
	}

	public boolean isTileOnMinimap(RSTile t) {
		if (Projection.isInMinimap(Projection.tileToMinimap(t))) {
			return true;
		} else {
			return false;
		}
	}

	private boolean inArea(RSArea area) {
		if (area.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean inventoryOnlyContains(final int... ids) {
		ArrayList<RSItem> approvedItems = new ArrayList<RSItem>();

		for (RSItem i : Inventory.getAll()) {
			for (int j = 0; j < ids.length; j++) {
				if (i.getID() == ids[j]) {
					approvedItems.add(i);
				}
			}
		}
		if (Inventory.getAll().length == approvedItems.size()) {
			return true;
		} else {
			return false;
		}
	}

	private void sleepUntilNotMoving(RSTile dest) {
		Timer t = new Timer(1500);

		while (t.isRunning()) {
			if (!Player.isMoving() || Player.getPosition().distanceTo(dest) <= 3) {
				break;
			}
			if (Player.isMoving()) {
				t.reset();
			}
			sleep(50, 100);
		}
	}

	public void sleepUntilPlaneChange(int plane) {
		Timer t = new Timer(5000);
		while (t.isRunning()) {
			status = "Using Stairs!";
			if (Game.getPlane() != plane) {
				status = "Changed floors!";
				break;
			}
			if (Player.isMoving() || Player.getAnimation() != -1) {
				t.reset();
			}
			sleep(50, 100);
		}
	}

	public void sleepWhileSpinning() {
		Timer t = new Timer(50000);
		int startLevel = Skills.getCurrentLevel(Skills.SKILLS.CRAFTING);

		while (t.isRunning()) {
			status = "Spinning Flax";
			if (!inventoryContains(FLAX_ID) || startLevel != Skills.getCurrentLevel(Skills.SKILLS.CRAFTING)
					|| Player.getRSPlayer().isInCombat() || animationCount > 25) {
				status = "Done Spinning";
				animationCount = 0;
				break;
			}
			if (Player.getAnimation() == -1) {
				animationCount++;
			} else {
				animationCount = 0;
			}
			sleep(100);
		}
	}

	public boolean atBank() {
		if (BANK_AREA.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean atWheel() {
		if (WHEEL_AREA.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean depositAllExcept(final int... ids) {
		Arrays.sort(ids);
		for (RSItem i : Inventory.getAll()) {
			if (Arrays.binarySearch(ids, i.getID()) < 0) {
				if (!Banking.depositItem(i, 0)) {
					sleep(200, 300);
					return false;
				}

			}
		}
		return true;
	}

	public boolean inventoryContains(final int... ids) {
		for (RSItem i : Inventory.getAll()) {
			for (int j = 0; j < ids.length; j++) {
				if (i.getID() == ids[j]) {
					return true;
				}
			}
		}
		return false;
	}

	public void waitUntilIdle() {
		long t = System.currentTimeMillis();

		while (Timing.timeFromMark(t) < General.random(800, 1000)) {
			status = "Sleeping";
			if (Player.isMoving() || Player.getAnimation() != -1) {
				t = System.currentTimeMillis();
			} else {
				break;
			}
			sleep(50, 100);
		}
	}

	public void runTo(RSTile tile, boolean screenWalk) {
		if (!Player.isMoving() && Player.getPosition().distanceTo(tile) > 1) {
			if (!tile.isOnScreen() || screenWalk == false) {
				Walking.walkTo(tile);
			} else if (tile.isOnScreen() && screenWalk == true) {
				DynamicClicking.clickRSTile(tile, "Walk here");
			}
		}
	}

	/**
	 * @author Jewtage
	 */

	public class RSArea {
		private final Polygon area;
		private final int plane;

		public RSArea(final RSTile[] tiles, final int plane) {
			area = tilesToPolygon(tiles);
			this.plane = plane;
		}

		public RSArea(final RSTile[] tiles) {
			this(tiles, 0);
		}

		public RSArea(final RSTile southwest, final RSTile northeast) {
			this(southwest, northeast, 0);
		}

		public RSArea(final int swX, final int swY, final int neX, final int neY) {
			this(new RSTile(swX, swY), new RSTile(neX, neY), 0);
		}

		public RSArea(final int swX, final int swY, final int neX, final int neY, final int plane) {
			this(new RSTile(swX, swY), new RSTile(neX, neY), plane);
		}

		public RSArea(final RSTile southwest, final RSTile northeast, final int plane) {
			this(new RSTile[] { southwest, new RSTile(northeast.getX() + 1, southwest.getY()),
					new RSTile(northeast.getX() + 1, northeast.getY() + 1),
					new RSTile(southwest.getX(), northeast.getY() + 1) }, plane);
		}

		public boolean contains(final RSTile... tiles) {
			final RSTile[] areaTiles = getTiles();
			for (final RSTile check : tiles) {
				for (final RSTile space : areaTiles) {
					if (check.equals(space)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean contains(final int x, final int y) {
			return this.contains(new RSTile(x, y));
		}

		public boolean contains(final int plane, final RSTile... tiles) {
			return this.plane == plane && this.contains(tiles);
		}

		public Rectangle getDimensions() {
			return new Rectangle(area.getBounds().x + 1, area.getBounds().y + 1, getWidth(), getHeight());
		}

		public RSTile getNearestTile(final RSTile base) {
			RSTile tempTile = null;
			for (final RSTile tile : getTiles()) {
				if (tempTile == null || distanceBetween(base, tile) < distanceBetween(tempTile, tile)) {
					tempTile = tile;
				}
			}
			return tempTile;
		}

		public int getPlane() {
			return plane;
		}

		public Polygon getPolygon() {
			return area;
		}

		public RSTile[] getTiles() {
			ArrayList<RSTile> tiles = new ArrayList<RSTile>();
			for (int x = getX(); x <= getX() + getWidth(); x++) {
				for (int y = getY(); y <= getY() + getHeight(); y++) {
					if (area.contains(x, y)) {
						tiles.add(new RSTile(x, y));
					}
				}
			}
			return tiles.toArray(new RSTile[tiles.size()]);
		}

		public int getWidth() {
			return area.getBounds().width;
		}

		public int getHeight() {
			return area.getBounds().height;
		}

		public int getX() {
			return area.getBounds().x;
		}

		public int getY() {
			return area.getBounds().y;
		}

		public Polygon tilesToPolygon(final RSTile[] tiles) {
			final Polygon polygon = new Polygon();
			for (final RSTile t : tiles) {
				polygon.addPoint(t.getX(), t.getY());
			}
			return polygon;
		}

		public double distanceBetween(RSTile curr, RSTile dest) {
			return Math.sqrt((curr.getX() - dest.getX()) * (curr.getX() - dest.getX())
					+ (curr.getY() - dest.getY()) * (curr.getY() - dest.getY()));
		}
	}

	public class Timer {

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

	public static String parseTime(long millis, boolean newFormat) {
		long time = millis / 1000;
		String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		String days = Integer.toString((int) (time / (3600 * 24)));
		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2)
				seconds = "0" + seconds;
			if (minutes.length() < 2)
				minutes = "0" + minutes;
			if (hours.length() < 2)
				hours = "0" + hours;
		}
		if (!newFormat)
			return hours + ":" + minutes + ":" + seconds;
		days = days + " day" + ((Integer.valueOf(days) != 1) ? "s" : "");
		hours = hours + " hour" + ((Integer.valueOf(hours) != 1) ? "s" : "");
		minutes = minutes + " minute" + ((Integer.valueOf(minutes) != 1) ? "s" : "");
		seconds = seconds + " second" + ((Integer.valueOf(seconds) != 1) ? "s" : "");
		return days + ", " + hours + ", " + minutes + ", " + seconds;
	}

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	private String addCommasToNumericString(String digits) {
		String result = "";
		int len = digits.length();
		int nDigits = 0;

		if (digits.length() < 4)
			return digits;

		for (int i = len - 1; i >= 0; i--) {
			result = digits.charAt(i) + result;
			nDigits++;
			if (((nDigits % 3) == 0) && (i > 0)) {
				result = "," + result;
			}
		}
		return (result);
	}

	public void onPaint(Graphics g) {

		long time = System.currentTimeMillis() - startTime;

		int levelsGained = Skills.getCurrentLevel(Skills.SKILLS.CRAFTING) - startCraftingLevel;
		int craftingXPGained = Skills.getXP(Skills.SKILLS.CRAFTING) - startCraftingXP;
		int xpPerHour = (int) (craftingXPGained * 3600000D / (System.currentTimeMillis() - startTime));
		int flaxPerHour = (int) (flaxSpun * 3600000D / (System.currentTimeMillis() - startTime));

		Font small = new Font("Verdana", 0, 10);
		Font font = new Font("Verdana", 0, 12);
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawImage(open, 7, 275, null); // draw open image

		g.setFont(small);
		g.drawString("v" + version, 470, 354);

		g.setFont(font);
		int x = 342;
		int y = 367;
		g.drawString("" + parseTime((time), false) + "", x, y);
		y += 22;
		g.drawString("" + status, x, y);
		y += 25;
		g.drawString("" + addCommasToNumericString(Integer.toString(craftingXPGained)) + " ("
				+ addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", x, y);
		y += 25;
		g.drawString("" + levelsGained + " (Current: " + Skills.getCurrentLevel(Skills.SKILLS.CRAFTING) + ")", x, y);
		y += 26;
		g.drawString("" + addCommasToNumericString(Integer.toString(flaxSpun)) + " ("
				+ addCommasToNumericString(Integer.toString(flaxPerHour)) + "/hr)", x, y);
	}
}