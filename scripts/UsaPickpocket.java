package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSNPCDefinition;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Pickpocket")
public class UsaPickpocket extends Script implements Painting, MessageListening07 {

	private String version = "v1.2";
	private boolean run = true;
	private long stunnedTimer;
	private String status = "Starting...";
	private long startTime;
	private int startXP = 0, startLVL = 0, NEXT_EAT_AT = 0;
	private RSTile startTile;
	private boolean SUCCESSFUL_PICKPOCKET = false;

	// NPC
	private String name;

	// PAINTING
	private RSNPC PAINT_NPC = null;
	private RSNPC PAINT_HOVER_NPC = null;
	private RSTile[] PAINT_PATH = null;

	// ABC
	private ABCUtil abc;
	private long last_busy_time;

	@Override
	public void run() {

		String[] options = getThievableNPCs();

		int response = JOptionPane.showOptionDialog(null, "Select the NPC you wish to pickpocket.", "USA Pickpocket",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		println("Selected NPC name \"" + options[response] + "\"");

		name = options[response];

		while (name == null) {
			status = "Waiting for input.";
			sleep(100);
		}

		while (Login.getLoginState() != Login.STATE.INGAME) {
			status = "Logging in...";
			sleep(100);
		}

		NEXT_EAT_AT = nextEatValue();

		startTile = Player.getPosition();

		println("Our starting tile is: " + startTile);

		startTime = System.currentTimeMillis();
		startLVL = Skills.getActualLevel(SKILLS.THIEVING);
		startXP = Skills.getXP(SKILLS.THIEVING);

		abc = new ABCUtil();

		while (run) {

			if (!Game.isRunOn() && Game.getRunEnergy() >= abc.INT_TRACKER.NEXT_RUN_AT.next()) {

				status = "Activating Run";
				Options.setRunOn(true);
				abc.INT_TRACKER.NEXT_RUN_AT.reset();

			}

			if (!isTileOnMinimap(startTile) || !PathFinding.canReach(startTile, false)) {

				walkToTile(startTile, 1, 3, null);

			} else if (!eat()) {

				if (stunnedTimer > System.currentTimeMillis()) {

					if (abc.BOOL_TRACKER.HOVER_NEXT.next() && hoverNPC(name)) {

						status = "Hovering over next NPC";

					} else {

						status = "Sleeping while stunned";

					}

				} else {

					if (clickNPC("Pickpocket", name)) {

						PAINT_NPC = null;
						PAINT_HOVER_NPC = null;

						abc.BOOL_TRACKER.USE_CLOSEST.reset();
						abc.BOOL_TRACKER.HOVER_NEXT.reset();
						abc.BOOL_TRACKER.GO_TO_ANTICIPATED.reset();

						last_busy_time = System.currentTimeMillis();

					}

				}

			}

			abc.performXPCheck(SKILLS.THIEVING);
			abc.performRotateCamera();
			abc.performExamineObject();
			abc.performPickupMouse();
			abc.performRandomMouseMovement();
			abc.performRandomRightClick();
			abc.performQuestsCheck();
			abc.performFriendsCheck();
			abc.performMusicCheck();
			abc.performCombatCheck();

			sleep(50);

		}

	}

	private int nextEatValue() {
		int r = General.random(3, 8);
		println("We will eat the next food at " + r + " health.");
		return r;
	}

	private boolean eat() {

		if (Skills.getCurrentLevel(SKILLS.HITPOINTS) > NEXT_EAT_AT)
			return false;

		if (Inventory.open()) {

			RSItem[] items = Inventory.getAll();
			if (items.length == 0)
				return false;

			for (RSItem item : items) {

				if (item == null)
					return false;

				RSItemDefinition d = item.getDefinition();
				if (d == null)
					return false;

				String name = d.getName();
				if (name == null)
					return false;

				String[] actions = d.getActions();
				if (actions.length == 0)
					return false;

				for (String action : actions) {

					if (action.contains("Eat")) {

						status = "Eating a " + name;

						sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

						final int HEALTH_BEFORE = Skills.getCurrentLevel(SKILLS.HITPOINTS);

						if (item.click()) {

							Timing.waitCondition(new Condition() {

								public boolean active() {
									sleep((long) General.randomSD(200, 50));
									return Skills.getCurrentLevel(SKILLS.HITPOINTS) > (HEALTH_BEFORE + 2)
											|| (Skills.getActualLevel(SKILLS.HITPOINTS) == Skills
													.getCurrentLevel(SKILLS.HITPOINTS));
								}
							}, 2000);

							status = "We ate a " + name;

							abc.DELAY_TRACKER.ITEM_INTERACTION.reset();

							NEXT_EAT_AT = nextEatValue();

							return true;

						}

					}
				}

			}

		}

		return false;
	}

	private boolean hoverNPC(String name) {

		RSNPC npc = getNPC(name);

		if (npc == null)
			return false;

		PAINT_HOVER_NPC = npc;

		if (!npc.isMoving()) {

			if (npc.isOnScreen()) {

				RSModel m = npc.getModel();

				if (m == null)
					return false;

				Polygon area = m.getEnclosedArea();

				if (area == null)
					return false;

				if (area.contains(Mouse.getPos())) {
					status = "Hovering over next npc";
					return true;
				}

				Point p = m.getHumanHoverPoint();
				if (p == null)
					return false;

				status = "Hovering over next npc";

				Mouse.move(p);

				sleep((long) General.randomSD(300, 100));

			} else {

				status = "Turning camera to npc";
				Camera.turnToTile(npc);

			}

		}

		return false;
	}

	private String[] getThievableNPCs() {

		RSNPC[] npcs = NPCs.findNearest(Filters.NPCs.actionsEquals("Pickpocket"));

		ArrayList<String> names = new ArrayList<String>();

		for (RSNPC n : npcs) {

			if (n != null) {

				String name = n.getName();

				if (name != null && !names.contains(name))
					names.add(name);

			}

		}

		return names.toArray(new String[names.size()]);

	}

	private RSNPC getNPC(String name) {

		RSNPC[] npcs = NPCs.findNearest(new Filter<RSNPC>() {

			public boolean accept(RSNPC npc) {

				if (npc == null)
					return false;

				RSNPCDefinition d = npc.getDefinition();
				if (d == null)
					return false;

				String n = d.getName();
				if (n == null)
					return false;

				if (!n.equalsIgnoreCase(name))
					return false;

				if (npc.isInCombat() || npc.isInteractingWithMe())
					return false;

				if (!PathFinding.canReach(npc, false))
					return false;

				return true;
			}

		});

		RSNPC npc = null;

		if (npcs.length > 0) {

			npc = npcs[0];

			if (npcs.length > 1) {

				if (abc.BOOL_TRACKER.USE_CLOSEST.next() && npc.getPosition().distanceTo(npcs[1]) <= 5.0) {

					npc = npcs[1];

				}

			}

		}

		return npc;
	}

	private boolean clickNPC(String action, String name) {

		RSNPC npc = getNPC(name);

		if (npc == null)
			return false;

		PAINT_NPC = npc;

		abc.waitNewOrSwitchDelay(last_busy_time, true);

		if (!npc.isOnScreen() && abc.BOOL_TRACKER.GO_TO_ANTICIPATED.next()) {
			status = "Turning to " + name;
			Camera.turnToTile(npc);
		}

		if (!npc.isOnScreen()) {
			status = "Walking to " + name;
			RSNPC copy = npc;
			Condition c = new Condition() {
				@Override
				public boolean active() {
					return copy.isOnScreen();
				}
			};
			walkToTile(npc, 2, 2, c);
		}

		if (npc.isOnScreen()) {

			long timer = System.currentTimeMillis() + (long) General.randomSD(1500, 200);

			while (timer > System.currentTimeMillis()) {

				status = "Moving mouse over " + name;

				if (!npc.isOnScreen() || ChooseOption.isOpen())
					break;

				RSModel model = npc.getModel();

				if (model != null) {

					Point p = model.getHumanHoverPoint();

					if (p != null) {

						Mouse.move(p);

						if (npc.isMoving() || Player.isMoving()) {

							sleep((long) (General.randomSD(150, 25)) / 2);

						} else {

							sleep((long) General.randomSD(150, 25));

						}

					}

				}

				model = npc.getModel();

				if (model != null) {

					Polygon area = model.getEnclosedArea();

					if (area != null) {

						if (area.contains(Mouse.getPos())) {

							status = "Right clicking " + name;

							Mouse.click(3);

							Timing.waitCondition(new Condition() {
								public boolean active() {
									sleep((long) General.randomSD(75, 25));
									return ChooseOption.isOpen();
								}
							}, General.randomSD(500, 50));

							break;

						}

					}

				}

			}

			status = "Selecting option " + action;
			if (ChooseOption.select(action + " " + name)) {
				sleepWhileThieving();
			} else {
				ChooseOption.close();
			}

		}

		return true;
	}

	private boolean sleepWhileThieving() {

		SUCCESSFUL_PICKPOCKET = false;

		final int count = Inventory.getCount("Coins");

		long sleep = ((long) General.randomSD(1500, 200));

		long timer = System.currentTimeMillis() + sleep;

		while (timer > System.currentTimeMillis()) {

			if (Player.isMoving() || Player.getAnimation() != -1 || Player.getRSPlayer().isInCombat())
				timer = System.currentTimeMillis() + sleep;

			if (Inventory.getCount("Coins") != count || SUCCESSFUL_PICKPOCKET) {
				status = "Successful Pickpocket";
				return true;
			}

			if (stunnedTimer > System.currentTimeMillis())
				break;

			sleep(25);

		}

		return false;
	}

	public boolean walkToTile(final Positionable tile, int offset, int returnTileDistance, Condition c) {

		if (tile == null)
			return false;

		RSTile position = tile.getPosition();

		if (position == null)
			return false;

		if (PathFinding.canReach(tile, false) && (position.isOnScreen()
				|| Player.getPosition().distanceTo(position) <= abc.INT_TRACKER.WALK_USING_SCREEN.next())) {

			if (position.isOnScreen()) {

				status = "Clicking Screen Tile";
				PAINT_PATH = new RSTile[] { position };
				clickTileScreen(position, offset, returnTileDistance, c);

			} else {

				status = "Walking Screen Path";
				RSTile[] path = generateScreenPath(Player.getPosition(), position);
				PAINT_PATH = path;
				walkScreenPath(path, 0, returnTileDistance, c);

			}

		} else {

			DPathNavigator d = new DPathNavigator();

			if (d.traverse(tile)) {

				status = "Walking DPath";

			} else {

				status = "Web Walking";
				WebWalking.walkTo(tile);

			}

		}

		PAINT_PATH = null;
		abc.INT_TRACKER.WALK_USING_SCREEN.reset();

		return true;
	}

	public boolean clickTileScreen(Positionable tile, int offset, int returnTileDistance, Condition c) {

		if (tile == null)
			return false;

		if (offset > 0)
			tile = randomizeTile(tile, offset);

		if (tile == null)
			return false;

		abc.waitNewOrSwitchDelay(last_busy_time, true);

		if (DynamicClicking.clickRSTile(tile, "Walk here"))
			sleepWhileMoving(tile, returnTileDistance, c);

		last_busy_time = System.currentTimeMillis();

		return true;

	}

	public boolean clickTileMinimap(Positionable tile, int offset, int returnTileDistance, Condition c) {

		if (tile == null)
			return false;

		if (offset > 0)
			tile = randomizeTile(tile, offset);

		if (tile == null)
			return false;

		abc.waitNewOrSwitchDelay(last_busy_time, true);

		if (Walking.clickTileMM(tile, 1))
			sleepWhileMoving(tile, returnTileDistance, c);

		last_busy_time = System.currentTimeMillis();

		return true;

	}

	public boolean walkPath(RSTile[] path, int offset, int returnTileDistance, Condition c) {

		if (path == null || path.length == 0)
			return false;

		if (Player.getPosition().distanceTo(path[path.length - 1]) <= 5)
			return true;

		if (PAINT_PATH == null)
			PAINT_PATH = path;

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis() && Player.getPosition().distanceTo(path[path.length - 1]) > 5) {

			if (c != null && c.active()) {
				status = "Condition active";
				break;
			}

			RSTile tile = null;

			if (isTileOnMinimap(path[path.length - 1])) {

				tile = path[path.length - 1];

			} else {

				for (int i = path.length - 1; i > 0; i--) {

					if (isTileOnMinimap(path[i])) {

						tile = path[i];
						break;

					}

				}

			}

			if (tile == null) {
				PAINT_PATH = null;
				return false;
			}

			if (clickTileMinimap(tile, offset, returnTileDistance, c))
				timer = System.currentTimeMillis() + 3000;

			sleep(50);

		}

		PAINT_PATH = null;

		return true;

	}

	public boolean walkScreenPath(RSTile[] path, int offset, int returnTileDistance, Condition c) {

		if (path == null || path.length == 0)
			return false;

		if (PAINT_PATH == null)
			PAINT_PATH = path;

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis() && Player.getPosition().distanceTo(path[path.length - 1]) > 5) {

			if (c != null && c.active()) {
				status = "Condition active";
				break;
			}

			RSTile tile = null;

			for (RSTile t : path) {
				tile = t;
				if (t.isOnScreen())
					break;
			}

			if (tile == null) {
				PAINT_PATH = null;
				return false;
			}

			if (clickTileScreen(tile, offset, returnTileDistance, c))
				timer = System.currentTimeMillis() + 3000;

			sleep(50);

		}

		PAINT_PATH = null;

		return true;

	}

	public boolean isTileOnMinimap(Positionable tile) {

		if (tile == null)
			return false;

		return Projection.isInMinimap(Projection.tileToMinimap(tile));
	}

	public RSTile randomizeTile(Positionable position, int offset) {

		if (position == null)
			return null;

		RSTile t = position.getPosition();
		if (t == null)
			return null;

		return new RSTile(t.getX() + General.random(-offset, offset), t.getY() + General.random(-offset, offset),
				t.getPlane());

	}

	public void sleepWhileMoving(Positionable tile, int distanceTo, Condition c) {

		long sleep = System.currentTimeMillis() + 2000;

		while (sleep > System.currentTimeMillis()) {

			if (Player.isMoving())
				sleep = System.currentTimeMillis() + 1500;

			if (Player.getPosition().distanceTo(tile) <= distanceTo)
				break;

			if (c != null && c.active())
				break;

			sleep(100);

		}

	}

	public RSTile[] generateScreenPath(RSTile start, Positionable end) {

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
				if (obj[0] != null && obj[0].getType().equals(TYPES.INTERACTIVE))
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

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setRenderingHints(rh);

		long currentTime = System.currentTimeMillis();
		long time = currentTime - startTime;
		int xpGained = Skills.getXP(SKILLS.THIEVING) - startXP;
		int xpPerHour = (int) (xpGained * 3600000D / time);
		int currentLVL = Skills.getActualLevel(SKILLS.THIEVING);

		Color background = new Color(160, 32, 240, 150);
		g2.setColor(background);
		g2.fillRoundRect(230, 347, 261, 120, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(230, 347, 261, 120, 10, 10);

		int x = 235;
		int y = 361;
		int spacing = 15;
		g2.setFont(new Font("Tahoma", Font.BOLD, 12));

		g2.drawString("USA Pickpocket             " + version, x + 75, y);
		g2.drawLine(230, 365, 490, 365);
		y += spacing + 3;

		g2.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		if (stunnedTimer > currentTime) {
			g2.drawString("Stunned Timer: " + (stunnedTimer - currentTime) + " ms", x, y);
		} else {
			g2.drawString("Stunned Timer: 0 ms", x, y);
		}
		y += spacing;
		g2.drawString("Status: " + status, x, y);
		y += spacing;
		g2.drawString("Thieving Level: " + currentLVL + " (+" + (currentLVL - startLVL) + ")", x, y);
		y += spacing;
		g2.drawString("XP Gained: " + addCommasToNumericString(Integer.toString(xpGained)) + " ("
				+ addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", x, y);
		y += 6;

		int xpTNL = Skills.getXPToNextLevel(SKILLS.THIEVING);
		int percentTNL = Skills.getPercentToNextLevel(SKILLS.THIEVING);
		long TTNL = 0;
		if (xpPerHour > 0) {
			TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
		}
		int percentFill = (250 * percentTNL) / 100;
		g2.setColor(Color.RED);
		g2.fillRoundRect(x, y, 250, 16, 5, 5);
		Color green = new Color(10, 150, 10);
		g2.setColor(green);
		g2.fillRoundRect(x, y, percentFill, 16, 5, 5);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(x, y, 250, 16, 5, 5);
		g2.drawString(addCommasToNumericString(Integer.toString(xpTNL)) + " xp to " + (currentLVL + 1) + " | "
				+ Timing.msToString(TTNL), x + 40, y + 13);

		if (PAINT_NPC != null) {

			g.setColor(new Color(0, 255, 0, 150));

			RSModel m = PAINT_NPC.getModel();

			if (m != null) {

				Polygon p = m.getEnclosedArea();

				if (p != null) {

					g.drawPolygon(p);

				}

			}

		}

		if (PAINT_HOVER_NPC != null) {

			g.setColor(new Color(255, 0, 0, 150));

			RSModel m = PAINT_HOVER_NPC.getModel();

			if (m != null) {

				Polygon p = m.getEnclosedArea();

				if (p != null) {

					g.drawPolygon(p);

				}

			}

		}

		g2.setColor(Color.BLACK);
		g2.fillRect(10, 459, 90, 15);

	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverMessageReceived(String message) {

		if (message.contains("stunned")) {
			stunnedTimer = System.currentTimeMillis() + (long) General.randomSD(4000, 500);
		} else if (message.contains("You pick")) {
			SUCCESSFUL_PICKPOCKET = true;
		}

	}

	@Override
	public void tradeRequestReceived(String arg0) {
		// TODO Auto-generated method stub

	}

}
