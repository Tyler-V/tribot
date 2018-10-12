package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Magic;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Planker")
public class UsaPlanker extends Script implements Painting {

	private String LOG_NAME = "Oak logs";

	private final RSArea CAMELOT = new RSArea(new RSTile(2754, 3475, 0),
			new RSTile(2760, 3482, 0));
	private final String[] LOGS = new String[] { "Logs", "Oak logs",
			"Willow logs", "Maple logs", "Teak logs", "Yew logs", "Magic logs" };
	private final String[] PLANKS = new String[] { "Oak plank" };
	private final int HOUSE_PORTAL = 4525;
	private final int[] OUTSIDE_PORTAL = { 15478, 15482, 15479, 15480, 15481,
			15482, 15483, 15484, 15485, 15477 };
	private final int HOUSE_INTERFACE_MASTER = 219;

	private int TELEPORT_COST = 0;
	private int LOG_VALUE = 0;
	private int PLANK_VALUE = 0;
	private int OTHER_COSTS = 0;
	private int PLANK = 0;
	private int fail = 0;
	private String status = "";
	private String SERVANT_NAME = null;
	private String version = "1.0";
	private boolean run = true;
	private long startTime;
	private RSTile[] PAINT_PATH = null;

	public void run() {

		super.setAIAntibanState(false);
		Mouse.setSpeed(150);

		startTime = System.currentTimeMillis();

		TELEPORT_COST = getPrice(563);
		println("Cost per teleport is " + TELEPORT_COST + " gp.");
		LOG_VALUE = getPrice(1521);
		println("Cost per Oak log is " + LOG_VALUE + " gp.");
		PLANK_VALUE = getPrice(8778);
		println("Cost per sawmill for Oak log is 250 gp.");
		println("Value of Oak plank is " + PLANK_VALUE + " for a profit of "
				+ (PLANK_VALUE - LOG_VALUE - 250) + " gp.");

		while (run) {

			STATE state = getState();
			if (state == STATE.OUTSIDE_HOUSE) {
				enterHouse();
			} else if (state == STATE.SEND_SERVANT) {
				sendServant();
			} else if (state == STATE.TO_BANK) {
				toBank();
			} else if (state == STATE.BANK) {
				if (!bank()) {
					fail++;
					if (fail >= 3)
						run = false;
				} else {
					fail = 0;
				}

			} else if (state == STATE.TO_HOUSE) {
				toHouse();
			}

			sleep(50);
		}

	}

	public enum STATE {
		SEND_SERVANT, TO_BANK, BANK, TO_HOUSE, OUTSIDE_HOUSE;
	}

	public STATE getState() {
		if (Inventory.getCount(LOGS) > 0) {
			if (outsideHouse()) {
				return STATE.OUTSIDE_HOUSE;
			} else if (CAMELOT.contains(Player.getPosition())) {
				return STATE.TO_HOUSE;
			} else if (insideHouse()) {
				return STATE.SEND_SERVANT;
			}
		} else {
			if (CAMELOT.contains(Player.getPosition())) {
				return STATE.BANK;
			} else {
				return STATE.TO_BANK;
			}
		}
		return null;
	}

	private boolean sendServant() {
		RSNPC npc = getServant();
		if (npc == null) {
			status = "Waiting for Servant";
			long timer = System.currentTimeMillis() + 5000;
			while (timer > System.currentTimeMillis()) {
				if (getServant() != null)
					break;
				sleep(100);
			}
		}
		RSObject bell = getObject("Bell-pull");
		if (!talkingToServant()) {
			if ((npc != null && npc.isOnScreen())
					|| (npc != null && bell != null && Player.getPosition()
							.distanceTo(npc) < Player.getPosition().distanceTo(
							bell))) {
				if (!npc.isOnScreen()) {
					status = "Turning Camera to Servant";
					Camera.turnToTile(npc);
				}
				if (!npc.isOnScreen()) {
					status = "Moving to Servant";
					Condition c = new Condition() {
						@Override
						public boolean active() {
							return servantOnScreen();
						}
					};
					walkToTile(npc, 1, 2, c);
				}
				if (npc.isOnScreen())
					useLogsOnServant();
			} else if (bell != null) {
				status = "Moving to Bell";
				if (!bell.isOnScreen())
					Camera.turnToTile(bell);
				if (Player.getPosition().distanceTo(bell) >= 5) {
					Condition c = new Condition() {
						@Override
						public boolean active() {
							RSObject bell = getObject("Bell-pull");
							if (bell == null
									|| Player.getPosition().distanceTo(bell) >= 5)
								return false;
							return true;
						}
					};
					walkToTile(bell, 0, 2, c);
				}
				if (bell.isOnScreen()) {
					status = "Ringing Bell";
					if (DynamicClicking.clickRSObject(bell, "Ring")) {
						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep(100);
								return talkingToServant();
							}
						}, 5000);
					}
				}
			} else if (npc != null) {
				Condition c = new Condition() {
					@Override
					public boolean active() {
						return servantOnScreen();
					}
				};
				walkToTile(npc, 1, 2, c);
			}
		}
		if (talkingToServant()) {
			long timer = System.currentTimeMillis() + 10000;
			while (timer > System.currentTimeMillis()) {
				if (talkToServant())
					break;
				sleep(25);
			}
		}
		return true;
	}

	private boolean talkToServant() {
		String[] options = NPCChat.getOptions();
		if (NPCChat.getClickContinueInterface() != null) {
			if (outOfItems()) {
				println("Servant says we are out of items! Stopping script.");
				run = false;
			}
			if (setServantName())
				println("Detected Servant: \"" + SERVANT_NAME + "\"");
			if (servantHoldingItems())
				useLogsOnServant();
			if (sendingServant())
				status = "Pressing Spacebar";
			Keyboard.typeSend(" ");
		} else if (options != null && options.length > 0) {
			if (option("Take them back to the bank", options)) {
				useLogsOnServant();
			} else if (option("Take to sawmill: 25 x Oak logs", options)) {
				status = "Typing 1";
				Keyboard.typeSend("1");
			} else if (option("Take to sawmill: 20 x Oak logs", options)) {
				status = "Typing 1";
				Keyboard.typeSend("1");
			} else if (option("Go to the sawmill...", options)) {
				status = "Typing 3";
				Keyboard.typeSend("3");
			} else if (option("Sawmill", options)) {
				status = "Typing 1";
				Keyboard.typeSend("1");
			} else if (option("Yes", options)) {
				status = "Typing 1";
				Keyboard.typeSend("1");
				PLANK += Inventory.getCount(LOGS);
			} else if (option("Pay servant 5000 coins", options)) {
				status = "Paying Servant 5K";
				Keyboard.typeSend("1");
				OTHER_COSTS += 5000;
			} else if (option("Pay servant 10000 coins", options)) {
				status = "Paying Servant 10K";
				Keyboard.typeSend("1");
				OTHER_COSTS += 10000;
			} else if (option("Something else...", options)) {
				status = "Typing 2";
				Keyboard.typeSend("2");
			}
		} else if (enterAmountMenuUp()) {
			if (SERVANT_NAME == null
					|| (SERVANT_NAME != null && SERVANT_NAME
							.equalsIgnoreCase("Butler"))) {
				int count = 20;
				status = "Typing " + count;
				Keyboard.typeSend(Integer.toString(count));
			} else {
				int count = 25;
				status = "Typing " + count;
				Keyboard.typeSend(Integer.toString(count));
			}
		} else if (!talkingToServant() || !servantOnScreen()) {
			status = "Finished Talking";
			return true;
		}
		return false;
	}

	private boolean outOfItems() {
		String[] responses = new String[] {
				"inform sir that his bank does not contain any of those items.",
				"I cannot conjure items out of the air." };
		String m = NPCChat.getMessage();
		if (m == null || m.isEmpty())
			return false;
		for (String s : responses) {
			if (m.contains(s)) {
				println(m);
				return true;
			}
		}
		return false;
	}

	private boolean servantHoldingItems() {
		String[] responses = new String[] {
				"until sir has space to take them.",
				"As I see thy inventory is full, I shall wait with" };
		String m = NPCChat.getMessage();
		if (m == null || m.isEmpty())
			return false;
		for (String s : responses) {
			if (m.contains(s))
				return true;
		}
		return false;
	}

	private boolean useLogsOnServant() {
		RSNPC servant = getServant();
		if (servant == null)
			return false;
		if (!Game.getUptext().contains("->")) {
			RSItem[] item = Inventory.find(LOGS);
			if (item.length == 0 || item[0] == null)
				return false;
			status = "Clicking log";
			if (item[0].click()) {
				long timer = System.currentTimeMillis() + 2000;
				while (timer > System.currentTimeMillis()) {
					if (Game.getUptext().contains("->"))
						break;
					sleep(100);
				}
			}
		}
		if (Game.getUptext().contains("->")) {
			status = "Clicking " + SERVANT_NAME;
			if (servant.click()) {
				long timer = System.currentTimeMillis() + 1000;
				while (timer > System.currentTimeMillis()) {
					if (Player.isMoving())
						timer = System.currentTimeMillis() + 1000;
					if (!Game.getUptext().contains("->") && talkingToServant())
						break;
					sleep(100);
				}
			}
		}
		return true;
	}

	private boolean servantOnScreen() {
		RSNPC npc = getServant();
		if (npc == null)
			return false;
		if (npc.isOnScreen())
			return true;
		return false;
	}

	private RSObject getObject(String name) {
		RSObject[] obj = Objects.findNearest(30, name);
		if (obj.length == 0 || obj[0] == null)
			return null;
		return obj[0];
	}

	private boolean setServantName() {
		if (SERVANT_NAME != null)
			return false;
		String n = NPCChat.getName();
		if (n == null || n.isEmpty())
			return false;
		SERVANT_NAME = n;
		return true;
	}

	private boolean sendingServant() {
		String[] responses = new String[] { "Very good, sir.",
				"I shall fly on wings of unholy flame to bring you the items you desire" };
		String m = NPCChat.getMessage();
		if (m == null || m.isEmpty())
			return false;
		for (String s : responses) {
			if (s.equalsIgnoreCase(m))
				return true;
		}
		return false;
	}

	private boolean option(String str, String[] options) {
		for (String s : options) {
			if (s.contains(str))
				return true;
		}
		return false;
	}

	private boolean enterAmountMenuUp() {
		RSInterfaceChild child = Interfaces.get(162, 32);
		return child != null && !child.isHidden();
	}

	private boolean talkingToServant() {
		return Interfaces.isInterfaceValid(219)
				|| Interfaces.isInterfaceValid(231);
	}

	private RSNPC getServant() {
		RSNPC[] npc = NPCs.find("Demon butler", "Butler");
		if (npc.length == 0 || npc[0] == null)
			return null;
		return npc[0];
	}

	private boolean bank() {
		if (openBank()) {
			if (Banking.deposit(0, PLANKS))
				status = "Depositing Planks";
			if (!withdraw(25, LOG_NAME))
				return false;
		}
		return true;
	}

	private boolean openBank() {
		if (Banking.isBankScreenOpen())
			return true;
		status = "Opening Bank";
		RSObject bank = getObject("Bank chest");
		if (bank == null)
			return false;
		if (bank.click("Use")) {
			long timer = System.currentTimeMillis() + 2000;
			while (timer > System.currentTimeMillis()) {
				if (Player.isMoving())
					timer = System.currentTimeMillis() + 2000;
				if (Banking.isBankScreenOpen())
					return true;
				sleep(200);
			}
		}
		return Banking.isBankScreenOpen();
	}

	private boolean closeBank() {
		if (!Banking.isBankScreenOpen())
			return true;
		status = "Closing Bank";
		long timer = System.currentTimeMillis() + 3000;
		while (timer > System.currentTimeMillis()) {
			Banking.close();
			if (!Banking.isBankScreenOpen())
				return true;
			sleep(200);
		}
		return false;
	}

	private boolean withdraw(final int count, final String... name) {
		final int before = Inventory.getCount(name);
		if (!Banking.isBankScreenOpen())
			return true;
		if (Banking.find(name).length == 0) {
			println("Could not find " + name[0] + ".");
			return false;
		}
		if (count > 1) {
			status = "Withdrawing " + (count - before) + " " + name[0];
		} else {
			status = "Withdrawing " + name[0];
		}
		if (Banking.withdraw(count - before, name)) {
			long timer = System.currentTimeMillis() + 3000;
			while (timer > System.currentTimeMillis()) {
				if (!hoverBankClose())
					status = "Hovering over close";
				if (Inventory.getCount(name) != before) {
					status = "Withdrew " + name[0];
					return true;
				}
				sleep(100);
			}
		}
		return true;
	}

	private boolean hoverBankClose() {
		if (!Interfaces.isInterfaceValid(12))
			return false;
		RSInterfaceChild child = Interfaces.get(12, 3);
		if (child == null || child.isHidden())
			return false;
		RSInterfaceComponent component = child.getChild(11);
		if (component == null || component.isHidden())
			return false;
		Rectangle bounds = component.getAbsoluteBounds();
		if (bounds == null)
			return false;
		Point p = Mouse.getPos();
		if (p == null)
			return false;
		if (bounds.contains(p))
			return true;
		Mouse.move(new Point(bounds.x + General.random(0, bounds.width),
				bounds.y + General.random(0, bounds.height)));
		return false;
	}

	private boolean outsideHouse() {
		return Objects.find(20, OUTSIDE_PORTAL).length > 0;
	}

	private boolean insideHouse() {
		return Objects.find(50, HOUSE_PORTAL).length > 0;
	}

	private boolean toBank() {
		if (castTeleport("Camelot Teleport")) {
			if (sleepUntilAtCamelot())
				OTHER_COSTS += TELEPORT_COST;
		}
		return true;
	}

	private boolean sleepUntilAtCamelot() {
		status = "Waiting for Camelot";
		long sleep = System.currentTimeMillis() + 3000;
		while (sleep > System.currentTimeMillis()) {
			if (Player.getAnimation() != -1 || Player.isMoving())
				sleep = System.currentTimeMillis() + 3000;
			if (CAMELOT.contains(Player.getPosition())) {
				status = "Arrived at Camelot";
				return true;
			}
			sleep(100);
		}
		return false;
	}

	private boolean toHouse() {
		if (insideHouse())
			return false;
		if (closeBank()) {
			if (castTeleport("Teleport to House")) {
				if (sleepUntilInsideHouse()) {
					OTHER_COSTS += TELEPORT_COST;
					openInventory();
				}
			}
		}
		return true;
	}

	private void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			status = "Opening Inventory";
			GameTab.open(TABS.INVENTORY);
		}
	}

	private void openMagic() {
		if (!GameTab.getOpen().equals(TABS.MAGIC)) {
			status = "Opening Magic Tab";
			Keyboard.pressFunctionKey(6);
		}
	}

	private boolean castTeleport(String spell) {
		openMagic();
		status = "Casting " + spell;
		if (Magic.selectSpell(spell))
			return true;
		return false;
	}

	private boolean sleepUntilInsideHouse() {
		status = "Waiting for House";
		long sleep = System.currentTimeMillis() + 3000;
		while (sleep > System.currentTimeMillis()) {
			if (Player.getAnimation() != -1 || Player.isMoving())
				sleep = System.currentTimeMillis() + 3000;
			if (housePortalOnScreen()) {
				status = "Arrived at House";
				return true;
			}
			sleep(100);
		}
		return false;
	}

	private boolean housePortalOnScreen() {
		RSObject[] portal = Objects.find(10, HOUSE_PORTAL);
		if (portal.length == 0 || portal[0] == null)
			return false;
		if (!portal[0].isOnScreen() || !portal[0].isClickable())
			return false;
		return true;
	}

	private boolean enterHouse() {
		if (!Interfaces.isInterfaceValid(HOUSE_INTERFACE_MASTER)) {
			RSObject[] portal = Objects.find(20, OUTSIDE_PORTAL);
			if (portal.length == 0 || portal[0] == null)
				return false;
			if (Camera.getCameraAngle() < 90)
				Camera.setCameraAngle(General.random(90, 100));
			if (!portal[0].isOnScreen()) {
				status = "Turning To Portal";
				Camera.turnToTile(portal[0]);
			}
			if (portal[0].isOnScreen()) {
				if (!Player.isMoving()) {
					status = "Clicking Portal";
					if (portal[0].click()) {
						long timer = System.currentTimeMillis() + 3000;
						while (timer > System.currentTimeMillis()) {
							if (Player.isMoving()
									|| Player.getAnimation() != -1)
								timer = System.currentTimeMillis() + 3000;
							if (Interfaces
									.isInterfaceValid(HOUSE_INTERFACE_MASTER))
								break;
						}
					}
				}
			} else {
				Condition c = new Condition() {
					@Override
					public boolean active() {
						RSObject[] portal = Objects.find(30, OUTSIDE_PORTAL);
						if (portal.length == 0 || portal[0] == null)
							return false;
						if (!portal[0].isOnScreen())
							return false;
						return true;
					}
				};
				walkToTile(portal[0], 1, 2, c);
			}
		}
		if (Interfaces.isInterfaceValid(HOUSE_INTERFACE_MASTER)) {
			status = "Entering House";
			Keyboard.typeSend("1");
			sleepUntilInsideHouse();
		}
		return true;
	}

	private void blindWalkTo(RSTile destination, int offset,
			int returnDistance, Condition c) {
		RSTile tile = getClosestTileTo(destination);
		clickTileMinimap(tile, offset, returnDistance, c);
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
					if (destination.distanceTo(tile) < distance) {
						distance = destination.distanceTo(tile);
						closest = tile;
					}
				}
				x += 1;
			}
			y -= 1;
		}
		return closest;
	}

	public boolean walkToTile(final Positionable tile, int offset,
			int returnTileDistance, Condition c) {
		if (tile == null)
			return false;
		RSTile position = tile.getPosition();
		if (position == null)
			return false;
		if (position.isOnScreen()
				|| Player.getPosition().distanceTo(position) <= 7) {
			if (position.isOnScreen()) {
				PAINT_PATH = new RSTile[] { position };
				status = "Clicking Tile";
				clickTileScreen(position, offset, returnTileDistance, c);
			} else {
				RSTile[] path = generateScreenPath(Player.getPosition(),
						position);
				PAINT_PATH = path;
				status = "Walking Screen Path";
				walkScreenPath(path, 0, returnTileDistance, c);
			}
		} else {
			if (isTileOnMinimap(position)) {
				PAINT_PATH = new RSTile[] { position };
				status = "Clicking Minimap";
				clickTileMinimap(position, offset, returnTileDistance, c);
			} else {
				DPathNavigator d = new DPathNavigator();
				RSTile[] path = d.findPath(position);
				if (path.length > 0) {
					PAINT_PATH = path;
					walkPath(path, offset, returnTileDistance, c);
				} else {
					status = "Web Walking";
					if (!WebWalking.walkTo(position))
						println("Your current location is not yet supported by the TRiBot Web Walking system!");
				}
			}
		}
		PAINT_PATH = null;
		return true;
	}

	public boolean clickTileScreen(Positionable tile, int offset,
			int returnTileDistance, Condition c) {
		if (offset > 0)
			tile = randomizeTile(tile, offset);
		if (tile == null)
			return false;
		if (DynamicClicking.clickRSTile(tile, "Walk here"))
			sleepWhileMoving(tile, returnTileDistance, c);
		return true;
	}

	public boolean clickTileMinimap(Positionable tile, int offset,
			int returnTileDistance, Condition c) {
		if (offset > 0)
			tile = randomizeTile(tile, offset);
		if (tile == null)
			return false;
		if (Walking.clickTileMM(tile, 1))
			sleepWhileMoving(tile, returnTileDistance, c);
		return true;
	}

	public boolean walkPath(RSTile[] path, int offset, int returnTileDistance,
			Condition c) {
		if (path == null || path.length == 0)
			return false;
		long timer = System.currentTimeMillis() + 2000;
		while (timer > System.currentTimeMillis()
				&& Player.getPosition().distanceTo(path[path.length - 1]) > offset) {
			if (c != null && c.active())
				break;
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
			if (tile != null) {
				clickTileMinimap(tile, offset, returnTileDistance, c);
				timer = System.currentTimeMillis() + 2000;
			}
			sleep(50);
		}
		return true;
	}

	public boolean walkScreenPath(RSTile[] path, int offset,
			int returnTileDistance, Condition c) {
		if (path == null || path.length == 0)
			return false;
		long timer = System.currentTimeMillis() + 2000;
		while (timer > System.currentTimeMillis()
				&& Player.getPosition().distanceTo(path[0]) > offset) {
			if (c != null && c.active())
				break;
			RSTile tile = null;
			for (RSTile t : path) {
				tile = t;
				if (t.isOnScreen())
					break;
			}
			tile = randomizeTile(tile, offset);
			if (tile != null) {
				timer = System.currentTimeMillis() + 2000;
				Walking.clickTileMS(tile, "Walk here");
				sleepWhileMoving(tile, returnTileDistance, c);
			}
			sleep(50);
		}
		return true;
	}

	public boolean isTileOnMinimap(Positionable tile) {
		return Projection.isInMinimap(Projection.tileToMinimap(tile));
	}

	public RSTile randomizeTile(Positionable tile, int offset) {
		int x = tile.getPosition().getX() + General.random(-offset, offset);
		int y = tile.getPosition().getY() + General.random(-offset, offset);
		int p = tile.getPosition().getPlane();
		return new RSTile(x, y, p);
	}

	public void sleepWhileMoving(Positionable tile, int distanceTo, Condition c) {
		long sleep = System.currentTimeMillis() + 2000;
		while (sleep > System.currentTimeMillis()) {
			if (Player.isMoving())
				sleep = System.currentTimeMillis() + 1000;
			if (Player.getPosition().distanceTo(tile) <= distanceTo)
				break;
			if (c != null && c.active())
				break;
			sleep(500);
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

	public int getPrice(final int itemId) {
		try {
			URL url = new URL(
					"https://api.rsbuddy.com/grandExchange?a=guidePrice&i="
							+ itemId);
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream()))) {
				String line = reader.readLine();
				return line == null ? -1 : Integer.parseInt(line.substring(11,
						line.indexOf(',')));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return -1;
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

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public void onPaint(Graphics g) {
		long time = System.currentTimeMillis() - startTime;
		int planksPerHour = (int) (PLANK * 3600000D / (System
				.currentTimeMillis() - startTime));
		int profit = (PLANK * (PLANK_VALUE - LOG_VALUE - 250)) - OTHER_COSTS;
		int profitPerHour = (int) (profit * 3600000D / (System
				.currentTimeMillis() - startTime));

		g.setFont(new Font("Tahoma", Font.BOLD, 15));
		int x = 280;
		int y = 365;
		int spacing = 15;

		g.setColor(Color.WHITE);
		g.drawString("USA Planker", x, y);
		y += spacing;
		g.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g.drawString("Status: " + status, x, y);
		y += spacing;
		g.drawString(
				"Planks: "
						+ addCommasToNumericString(Integer.toString(PLANK))
						+ " ("
						+ addCommasToNumericString(Integer
								.toString(planksPerHour)) + "/hr)", x, y);
		y += spacing;
		g.drawString(
				"Profit: "
						+ addCommasToNumericString(Integer.toString(profit))
						+ " ("
						+ addCommasToNumericString(Integer
								.toString(profitPerHour)) + "/hr)", x, y);
		y += spacing;
	}

}
