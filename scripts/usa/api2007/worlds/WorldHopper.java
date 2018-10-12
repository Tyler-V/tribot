package scripts.usa.api2007.worlds;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

import scripts.usa.api2007.Interfaces;

public class WorldHopper {

	private final static int WORLD_SWITCHER_MASTER = 69;
	private final static int WORLD_SWITCHER_CLOSE_BUTTON = 3;
	private final static int WORLD_SWITCHER_CONTAINER = 4;
	private final static int WORLD_SWITCHER_SCROLL_BAR = 15;
	private final static int WORLD_SWITCHER_UP_ARROW = 4;
	private final static int WORLD_SWITCHER_DOWN_ARROW = 5;
	private final static int WORLD_SWITCHER_WORLDS = 7;
	private final static int WORLD_SWITCHER_LOGOUT_BUTTON = 19;

	private final static int WORLD_SWITCHER_WARNING_DIALOG = 219;
	private final static int WORLD_SWITCHER_WARNING_DIALOG_CONFIRM = 2;

	private final static int LOGOUT_MENU_MASTER = 182;
	private final static int LOGOUT_MENU_WORLD_SWITCHER_BUTTON = 3;
	private final static int LOGOUT_MENU_LOGOUT_BUTTON = 12;

	private final static Rectangle CLICK_TO_SWITCH = new Rectangle(10, 468, 90, 20);
	private final static Point WORLD_SELECTOR_TOP_RIGHT = new Point(762, 23);
	private final static int WORLD_WIDTH = 87;
	private final static int WORLD_HEIGHT = 18;
	private final static int WORLD_PADDING = 4;
	private final static int SCREEN_HEIGHT = 502;

	public static List<World> worlds;

	// UPDATE
	private final static Point COLUMN_1_WORLD = new Point(189, 46); // 46y / 58y
	private final static int VERTICAL_DISTANCE = 24;
	private final static int HORIZONTAL_DISTANCE = 93;
	private final static int[] COLUMN_1_WORLDS = new int[] { 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318,
			319, 320 };
	private final static int[] COLUMN_2_WORLDS = new int[] { 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338,
			339, 340 };
	private final static int[] COLUMN_3_WORLDS = new int[] { 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358,
			359, 360 };
	private final static int[] COLUMN_4_WORLDS = new int[] { 361, 362, 365, 366, 367, 368, 369, 370, 373, 374, 375, 376, 377, 378, 381, 382, 383, 384,
			385, 386 };
	private final static int[] COLUMN_5_WORLDS = new int[] { 387, 388, 389, 390, 391, 392, 393, 394, 403, 404, 405, 406, 407, 408, 410, 411, 412 };

	static {
		if (worlds == null) {
			try {
				worlds = WorldHopper.getWorlds();
			}
			catch (IOException e) {
				System.out.println("Unable to retrieve current World list... " + e);
			}
		}
	}

	public static void updateWorlds() {
		try {
			worlds = WorldHopper.getWorlds();
		}
		catch (IOException e) {
			System.out.println("Unable to retrieve current World list... " + e);
		}
	}

	/**
	 * Reads data stream, adds Worlds Credit: AlphaDog
	 * 
	 * @return List<WORLD>
	 * @throws IOException
	 */
	public static List<World> getWorlds() throws IOException {
		List<World> list = new ArrayList<World>();
		try (DataInputStream dis = new DataInputStream(new URL("http://oldschool.runescape.com/slr").openConnection().getInputStream())) {
			@SuppressWarnings("unused")
			int size = dis.readInt() & 0xFF;
			int count = dis.readShort();
			for (int i = 0; i < count; i++) {
				int number = (int) (dis.readShort() & 0xFFFF);
				int flag = dis.readInt();
				WorldType type = (flag & 0x1) == 1 ? WorldType.MEMBERS : WorldType.FREE;
				String host = null, activity = null;
				StringBuilder sb = new StringBuilder();
				byte b;
				while (true) {
					b = dis.readByte();
					if (b == 0) {
						if (host == null) {
							host = sb.toString();
							sb = new StringBuilder();
						}
						else {
							activity = sb.toString();
							break;
						}
					}
					else {
						sb.append((char) b);
					}
				}
				WorldCountry country = WorldCountry.get(dis.readByte() & 0xFF);
				int players = (int) dis.readShort();
				// if (number > 400)
				// type = WorldType.TOURNAMENT;
				list.add(new World(number, type, WorldActivity.get(activity.toLowerCase()), country, players, host));
			}
		}
		Collections.sort(list, World.WORLD_NUMBER_LOWEST);
		return list;
	}

	/**
	 * Sorts Worlds by Player Count
	 * 
	 * @param lowest
	 */
	public static List<World> sortWorlds(WorldSorting sort) {
		switch (sort) {
			case PLAYER_COUNT_LOWEST:
				Collections.sort(worlds, World.PLAYER_COUNT_LOWEST);
				break;
			case WORLD_NUMBER_LOWEST:
				Collections.sort(worlds, World.WORLD_NUMBER_LOWEST);
				break;
			case PLAYER_COUNT_HIGHEST:
				Collections.sort(worlds, World.PLAYER_COUNT_HIGHEST);
				break;
			case WORLD_NUMBER_HIGHEST:
				Collections.sort(worlds, World.WORLD_NUMBER_HIGHEST);
				break;
		}
		return worlds;
	}

	/**
	 * Formats the world to a 3-digit format
	 * 
	 * @param world
	 *            , i.e. (1 -> 301, 334 -> 334)
	 * @return world in a 3-digit format
	 */
	private static int formatWorld(int world) {
		if (world > 300)
			return world;
		if (world < 300)
			return world + 300;
		return 0;
	}

	/**
	 * Changes to the desired world using the in-game world switcher or logout
	 * menu depending on the game state
	 * 
	 * @param world
	 * @param timeout
	 *            : duration the bot should click to the world
	 * @return true if we successfully changed worlds
	 */
	public static boolean changeWorld(int world) {
		int w = formatWorld(world);
		if (Login.getLoginState() == Login.STATE.INGAME) {
			Interfaces.closeAll();
			if (getCurrentWorld() == world) {
				System.out.println("WorldHopper: Already on world " + world + ".");
				return true;
			}
			if (Player.getRSPlayer().isInCombat()) {
				System.out.println("WorldHopper: Unable to change worlds while in combat.");
				return false;
			}
			if (openWorldSwitcher()) {
				System.out.println("WorldHopper: Opening world switcher.");
				if (Math.random() < 0) {
					System.out.println("WorldHopper: Clicking to world " + w);
					if (clickToWorld(w)) {
						System.out.println("WorldHopper: Selecting world " + w);
						if (selectWorld(w)) {
							System.out.println("WorldHopper: Changed to world " + w);
							return true;
						}
					}
				}
				else {
					System.out.println("WorldHopper: Scrolling to world " + w);
					if (scrollToWorld(w)) {
						System.out.println("WorldHopper: Selecting world " + w);
						if (selectWorld(w)) {
							System.out.println("WorldHopper: Changed to world " + w);
							return true;
						}
					}
				}
			}
		}
		else {
			if (selectWorld(w)) {
				if (Login.login())
					return true;
			}
		}
		return false;
	}

	public static boolean inGameWorldSwitcherLoading() {
		if (isWorldSwitcherOpen())
			return false;
		RSInterfaceChild child = Interfaces.get(69, 2);
		if (child == null)
			return false;
		String text = child.getText();
		if (text == null)
			return false;
		return text.contains("Loading");
	}

	/**
	 * Will scroll to the desired world if the world switcher menu is open using
	 * the mouse scroll wheel.
	 * 
	 * @param world
	 *            : Desired world, any format.
	 * @param timeout
	 *            : Total timeout duration for the clicking of the up/down
	 *            arrows
	 * @return : true if the world is in view
	 */
	public static boolean scrollToWorld(int world) {
		world = formatWorld(world) - 300;
		if (!isWorldSwitcherOpen())
			return false;
		RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_CONTAINER);
		if (child == null)
			return false;
		final Rectangle WORLD_SWITCHER_MENU = child.getAbsoluteBounds();
		if (WORLD_SWITCHER_MENU == null)
			return false;
		Rectangle bounds = getBounds(world);
		if (bounds == null)
			return false;
		if (rectangleIsVisible(bounds))
			return true;
		final int WORLD_SWITCHER_TOP_HEIGHT = getWorldSwitcherMenuHeight(true);
		final int WORLD_SWITCHER_BOTTOM_HEIGHT = getWorldSwitcherMenuHeight(false);
		if (WORLD_SWITCHER_TOP_HEIGHT == 0 || WORLD_SWITCHER_BOTTOM_HEIGHT == 0)
			return false;
		boolean half_ticks = false;
		boolean up = false;
		int ticks = 0;
		long timer = System.currentTimeMillis() + 10000;
		while (timer > System.currentTimeMillis()) {
			if (Player.getRSPlayer().isInCombat())
				return false;
			if (!WORLD_SWITCHER_MENU.contains(Mouse.getPos())) {
				Mouse.moveBox(WORLD_SWITCHER_MENU);
				General.sleep(General.randomSD(0, 500, 75, 25));
			}
			if (ticks == 0)
				ticks = getTicks();
			bounds = getBounds(world);
			if (bounds == null)
				return false;
			if (rectangleIsVisible(bounds))
				return true;
			if (bounds.y < WORLD_SWITCHER_TOP_HEIGHT) {
				Mouse.scroll(true, ticks);
				up = true;
			}
			else if ((bounds.y + bounds.height) > WORLD_SWITCHER_BOTTOM_HEIGHT) {
				Mouse.scroll(false, ticks);
				up = false;
			}
			bounds = getBounds(world);
			if (bounds == null)
				return false;
			if (up && (bounds.y + bounds.height) > WORLD_SWITCHER_BOTTOM_HEIGHT) {
				half_ticks = true;
			}
			else if (!up && bounds.y < WORLD_SWITCHER_TOP_HEIGHT) {
				half_ticks = true;
			}
			if (half_ticks)
				ticks = (int) Math.ceil(ticks / 2.0D);
			General.sleep(getDelay());
		}
		return rectangleIsVisible(getBounds(world));
	}

	/**
	 * CHARACTER PROFILE
	 * 
	 * @return
	 */
	private static int getTicks() {
		return General.random(1, 5);
	}

	/**
	 * CHARACTER PROFILE
	 * 
	 * @return
	 */
	private static int getDelay() {
		return General.randomSD(0, 200, 50, 25);
	}

	/**
	 * Will scroll to the desired world if the world switcher menu is open. If
	 * the distance of the bounds of the desired world are over (200, 250) it
	 * will click the scroll pane first then navigate by the up and down arrows.
	 * 
	 * @param world
	 *            : Desired world, any format.
	 * @param timeout
	 *            : Total timeout duration for the clicking of the up/down
	 *            arrows
	 * @return : true if the world is in view
	 */
	public static boolean clickToWorld(int world) {
		world = formatWorld(world) - 300;
		if (!isWorldSwitcherOpen())
			return false;
		Rectangle bounds = null;
		long timer;
		timer = System.currentTimeMillis() + 3000;
		while (timer > System.currentTimeMillis()) {
			bounds = getBounds(world);
			if (bounds != null)
				break;
			General.sleep(100);
		}
		if (bounds == null)
			return false;
		if (rectangleIsVisible(bounds))
			return true;
		int distance = getBoundsDistanceToScreen(bounds);
		if (Math.abs(distance) > General.random(200, 250)) {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLL_BAR);
			if (child == null || child.isHidden())
				return false;
			RSInterfaceComponent component = null;
			component = child.getChild(0);
			if (component == null || component.isHidden())
				return false;
			Rectangle scrollBarPane = component.getAbsoluteBounds();
			if (scrollBarPane == null)
				return false;
			component = child.getChild(1);
			if (component == null || component.isHidden())
				return false;
			Rectangle scrollBar = component.getAbsoluteBounds();
			if (scrollBar == null)
				return false;
			double MAX_DISTANCE = 927;
			double SCROLL_BAR_PANE_HEIGHT = scrollBarPane.getHeight();
			double SCROLL_BAR_Y = scrollBar.getY();
			double RATIO = SCROLL_BAR_PANE_HEIGHT / MAX_DISTANCE;
			double TRAVEL_DISTANCE = distance * RATIO;
			int x = scrollBar.x + General.random(4, scrollBar.width - 4);
			int y = (int) (SCROLL_BAR_Y + TRAVEL_DISTANCE);
			if (distance < 0)
				y += scrollBar.height;
			if (y == 245)
				y += General.random(2, 15);
			if (y == 417)
				y -= General.random(2, 15);
			Mouse.click(new Point(x, y), 1);
			General.sleep(General.randomSD(0, 500, 75, 25));
		}
		if (rectangleIsVisible(getBounds(world)))
			return true;
		final int WORLD_SWITCHER_TOP_HEIGHT = getWorldSwitcherMenuHeight(true);
		final int WORLD_SWITCHER_BOTTOM_HEIGHT = getWorldSwitcherMenuHeight(false);
		if (WORLD_SWITCHER_TOP_HEIGHT == 0 || WORLD_SWITCHER_BOTTOM_HEIGHT == 0)
			return false;
		if (bounds.y < WORLD_SWITCHER_TOP_HEIGHT) {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLL_BAR);
			if (child == null)
				return false;
			RSInterfaceComponent up = child.getChild(WORLD_SWITCHER_UP_ARROW);
			if (up == null)
				return false;
			timer = System.currentTimeMillis() + 10000;
			while (timer > System.currentTimeMillis()) {
				if (Player.getRSPlayer().isInCombat())
					return false;
				up.click();
				if (rectangleIsVisible(getBounds(world)))
					return true;
				General.sleep(getDelay());
			}
		}
		else {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLL_BAR);
			if (child == null)
				return false;
			RSInterfaceComponent down = child.getChild(WORLD_SWITCHER_DOWN_ARROW);
			if (down == null)
				return false;
			timer = System.currentTimeMillis() + 10000;
			while (timer > System.currentTimeMillis()) {
				if (Player.getRSPlayer().isInCombat())
					return false;
				down.click();
				if (rectangleIsVisible(getBounds(world)))
					return true;
				General.sleep(getDelay());
			}
		}
		return rectangleIsVisible(getBounds(world));
	}

	/**
	 * @param top
	 *            or bottom of the menu's height
	 * @return
	 */
	private static int getWorldSwitcherMenuHeight(boolean top) {
		RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_CONTAINER);
		if (child == null)
			return 0;
		Rectangle bounds = child.getAbsoluteBounds();
		if (bounds == null)
			return 0;
		if (top) {
			return bounds.y;
		}
		else {
			return bounds.y + bounds.height;
		}
	}

	/**
	 * 
	 * @param Rectangle
	 *            bounds of the world
	 * @return true if the rectangle is visible inside the world switching menu
	 */
	public static boolean rectangleIsVisible(Rectangle bounds) {
		if (bounds == null)
			return false;
		return (bounds.y >= getWorldSwitcherMenuHeight(true)) && ((bounds.y + bounds.height) <= getWorldSwitcherMenuHeight(false));
	}

	public static Rectangle getBounds(int world) {
		if (!isWorldSwitcherOpen())
			return null;
		int w = formatWorld(world) - 300;
		RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS);
		if (child == null)
			return null;
		RSInterfaceComponent[] components = child.getChildren();
		if (components == null || components.length == 0 || components.length % 6 != 0)
			return null;
		for (int i = 0; i < components.length; i += 6) {
			RSInterfaceComponent a = components[i + 2];
			if (a != null) {
				String number = a.getText();
				if (number != null && number.length() > 0 && Integer.parseInt(number) == w) {
					RSInterfaceComponent b = components[i];
					if (b != null)
						return b.getAbsoluteBounds();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param The
	 *            rectangle bounds of the world you are checking
	 * @return the distance that the bounds are from the visible world switching
	 *         menu
	 */
	private static int getBoundsDistanceToScreen(Rectangle bounds) {
		final int WORLD_SWITCHER_TOP_HEIGHT = getWorldSwitcherMenuHeight(true);
		final int WORLD_SWITCHER_BOTTOM_HEIGHT = getWorldSwitcherMenuHeight(false);
		if (bounds.y <= WORLD_SWITCHER_TOP_HEIGHT)
			return bounds.y - WORLD_SWITCHER_TOP_HEIGHT;
		if ((bounds.y + bounds.height) >= WORLD_SWITCHER_BOTTOM_HEIGHT)
			return (bounds.y + bounds.height) - WORLD_SWITCHER_BOTTOM_HEIGHT;
		return 0;
	}

	/**
	 * 
	 * @return true if the world switcher is open
	 */
	public static boolean isWorldSwitcherOpen() {
		RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, 0);
		return child != null && !child.isHidden(true);
	}

	/**
	 * 
	 * @return true if the logout menu is open
	 */
	public static boolean isLogoutMenuOpen() {
		return GameTab.getOpen() == TABS.LOGOUT;
	}

	/**
	 * Get bounds of logout button from the World Switcher or Logout menu.
	 * 
	 * @return Rectangle bounds
	 */
	private static Rectangle getLogoutBounds() {
		if (WorldHopper.isWorldSwitcherOpen()) {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_LOGOUT_BUTTON);
			if (child == null || child.isHidden())
				return null;
			Rectangle bounds = child.getAbsoluteBounds();
			return bounds;
		}
		else {
			RSInterfaceChild child = Interfaces.get(LOGOUT_MENU_MASTER, LOGOUT_MENU_LOGOUT_BUTTON);
			if (child == null || child.isHidden())
				return null;
			Rectangle bounds = child.getAbsoluteBounds();
			return bounds;
		}
	}

	/**
	 * Hovers over the logout button in game.
	 * 
	 * @return true mouse is inside the bounds of the button.
	 */
	public static boolean hoverOverLogout() {
		if (Login.getLoginState() != Login.STATE.INGAME)
			return false;
		if (open(TABS.LOGOUT)) {
			Rectangle bounds = WorldHopper.getLogoutBounds();
			if (bounds == null)
				return false;
			if (bounds.contains(Mouse.getPos()))
				return true;
			Mouse.moveBox(bounds);
			return true;
		}
		return false;
	}

	public static boolean logout() {
		if (Login.getLoginState() == Login.STATE.LOGINSCREEN)
			return true;
		if (open(TABS.LOGOUT)) {
			Rectangle bounds = WorldHopper.getLogoutBounds();
			if (bounds == null)
				return false;
			if (bounds.contains(Mouse.getPos())) {
				Mouse.click(1);
			}
			else {
				Mouse.clickBox(bounds, 1);
			}
		}
		return Login.getLoginState() == Login.STATE.LOGINSCREEN;
	}

	private static boolean open(GameTab.TABS tab) {
		if (GameTab.getOpen() == tab)
			return true;
		if (GameTab.open(tab)) {
			Timing.waitCondition(new Condition() {

				public boolean active() {
					return GameTab.getOpen() == tab;
				}
			}, 1000);
		}
		return GameTab.getOpen() == tab;
	}

	/**
	 * 
	 * @return true if the world switcher menu is open
	 */
	public static boolean openWorldSwitcher() {
		if (isWorldSwitcherOpen())
			return true;
		if (open(TABS.LOGOUT)) {
			RSInterfaceChild child = Interfaces.get(LOGOUT_MENU_MASTER, LOGOUT_MENU_WORLD_SWITCHER_BUTTON);
			if (child == null || child.isHidden())
				return false;
			if (child.click()) {
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(100);
						return isWorldSwitcherOpen();
					}
				}, 5000);
			}
		}
		return isWorldSwitcherOpen();
	}

	/**
	 * 
	 * @return true if the world switcher menu was closed
	 */
	public static boolean closeWorldSwitcher() {
		if (!isWorldSwitcherOpen())
			return true;
		RSInterfaceChild close = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_CLOSE_BUTTON);
		if (close == null)
			return false;
		if (close.click()) {
			Timing.waitCondition(new Condition() {

				public boolean active() {
					General.sleep(100);
					return !Interfaces.isInterfaceValid(WORLD_SWITCHER_MASTER);
				}
			}, 2000);
		}
		return !isWorldSwitcherOpen();
	}

	/**
	 * 
	 * @return true if the select world option is up
	 */
	public static boolean isSelectWorldIsUp() {
		if (Login.getLoginState() == Login.STATE.LOGINSCREEN) {
			Color color = Screen.getColorAt(10, 250);
			Color black = new Color(0, 0, 0);
			return color.equals(black);
		}
		return false;
	}

	/**
	 * selects the desired world using the in-game world switcher or logout menu
	 * depending on the game state
	 * 
	 * @param world
	 * @return true if we changed worlds
	 */
	public static boolean selectWorld(int world) {
		if (Login.getLoginState() == Login.STATE.INGAME) {
			if (!Interfaces.isInterfaceValid(WORLD_SWITCHER_WARNING_DIALOG)) {
				Rectangle bounds = getBounds(world);
				if (bounds == null)
					return false;
				int count = Inventory.getAll().length;
				Mouse.clickBox(bounds, 1);
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(50);
						return Player.getRSPlayer().isInCombat() || Interfaces.isInterfaceValid(WORLD_SWITCHER_WARNING_DIALOG) ||
								Game.getSetting(18) == 0;
					}
				}, 3000);
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(50);
						return Player.getRSPlayer().isInCombat() || Interfaces.isInterfaceValid(WORLD_SWITCHER_WARNING_DIALOG) ||
								(getCurrentWorld() == world && Game.getSetting(18) == 1 && count == Inventory.getAll().length);
					}
				}, 3000);
			}
			if (Interfaces.isInterfaceValid(WORLD_SWITCHER_WARNING_DIALOG)) {
				RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_WARNING_DIALOG, 0);
				if (child == null)
					return false;
				RSInterfaceComponent component = child.getChild(WORLD_SWITCHER_WARNING_DIALOG_CONFIRM);
				if (component == null)
					return false;
				String text = component.getText();
				if (text == null)
					return false;
				if (text.contains("Yes. In future, only warn about dangerous worlds.")) {
					Keyboard.typeSend("2");
				}
				else {
					Keyboard.typeSend("1");
				}
				General.sleep(General.randomSD(0, 500, 75, 25));
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(100);
						return Player.getRSPlayer().isInCombat() || getCurrentWorld() == world;
					}
				}, 3000);
			}
		}
		else {
			if (Login.getLoginState() != Login.STATE.LOGINSCREEN)
				return false;
			if (!isSelectWorldIsUp()) {
				Mouse.clickBox(CLICK_TO_SWITCH, 1);
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(100);
						return isSelectWorldIsUp();
					}
				}, 3000);
			}
			if (isSelectWorldIsUp()) {
				if (worldListSorted()) {
					Rectangle bounds = getLoginScreenWorldBounds(world);
					if (bounds == null)
						return false;
					Mouse.clickBox(bounds, 1);
					Timing.waitCondition(new Condition() {

						public boolean active() {
							General.sleep(100);
							return !isSelectWorldIsUp() && getCurrentWorld() == world;
						}
					}, 3000);
				}
			}
		}
		if (onWorld(WorldActivity.DEADMAN)) {
			Timing.waitCondition(new Condition() {

				public boolean active() {
					General.sleep(100);
					return NPCChat.getClickContinueInterface() != null;
				}
			}, 3000);
			NPCChat.clickContinue(false);
			Timing.waitCondition(new Condition() {

				public boolean active() {
					General.sleep(100);
					return Interfaces.isInterfaceValid(WORLD_SWITCHER_WARNING_DIALOG);
				}
			}, 3000);
			if (Interfaces.isInterfaceValid(WORLD_SWITCHER_WARNING_DIALOG)) {
				General.sleep(General.randomSD(1000, 300));
				RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_WARNING_DIALOG, 0);
				if (child == null)
					return false;
				RSInterfaceComponent component = child.getChild(WORLD_SWITCHER_WARNING_DIALOG_CONFIRM);
				if (component == null)
					return false;
				String text = component.getText();
				if (text == null)
					return false;
				if (text.contains("Yes. In future, only warn about dangerous worlds.")) {
					Keyboard.typeSend("2");
				}
				else {
					Keyboard.typeSend("1");
				}
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(100);
						return getCurrentWorld() == world;
					}
				}, 10000);
			}
		}
		return getCurrentWorld() == world;
	}

	/**
	 * Return sorted Rectangle list grouped by columns
	 */
	private static class RectangleComparator implements Comparator<Rectangle> {

		public int compare(Rectangle a, Rectangle b) {
			return a.x - b.x;
		}
	}

	/**
	 * Verify if the Point is a world button.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean isWorldButton(int x, int y) {
		Color color = Screen.getColorAt(x, y);
		if (color.getRed() < 100 || color.getGreen() < 100 || color.getBlue() < 100) {
			return false;
		}
		return true;
	}

	/**
	 * Set the world select bounds property in the worlds object.
	 * 
	 * @return
	 */
	public static List<World> getLoginScreenWorlds() {
		List<Rectangle> buttons = new ArrayList<Rectangle>();
		for (int y = WORLD_SELECTOR_TOP_RIGHT.y; y < SCREEN_HEIGHT; y++) {
			Rectangle bounds = null;
			for (int x = WORLD_SELECTOR_TOP_RIGHT.x; x > 0; x--) {
				if (isWorldButton(x, y)) {
					bounds = new Rectangle(x - WORLD_WIDTH, y, WORLD_WIDTH, WORLD_HEIGHT);
					buttons.add(bounds);
					x -= (bounds.width + WORLD_PADDING);
				}
			}
			if (bounds != null) {
				y += (bounds.height + WORLD_PADDING);
				bounds = null;
			}
		}
		try {
			Collections.sort(buttons, new RectangleComparator());
			List<World> worlds = WorldHopper.getWorlds();
			if (worlds.size() != buttons.size())
				return new ArrayList<World>();
			for (int i = 0; i < worlds.size(); i++)
				worlds.get(i).setWorldSelectBounds(buttons.get(i));
			return worlds;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the bounds of the world at the login screen by world number.
	 * 
	 * @param world
	 *            number
	 * @return Rectangle bounds of the world button
	 */
	private static Rectangle getLoginScreenWorldBounds(int number) {
		for (World world : getLoginScreenWorlds()) {
			if (world.getNumber() == number)
				return world.getWorldSelectBounds();
		}
		return null;
	}

	/**
	 * Sorts the logged out world switching menu to the correct order.
	 * 
	 * @return true if the logged out menu is sorted by world order lowest to
	 *         highest.
	 */
	private static boolean worldListSorted() {
		Point arrow = new Point(301, 8);
		if (Screen.getColorAt(arrow).getGreen() > 50)
			return true;
		Mouse.click(arrow, 1);
		Timing.waitCondition(new Condition() {

			public boolean active() {
				General.sleep(100);
				return Screen.getColorAt(arrow).getGreen() > 50;
			}
		}, 3000);
		return Screen.getColorAt(arrow).getGreen() > 50;
	}

	/**
	 * @return the current world
	 */
	public static int getCurrentWorld() {
		return Game.getCurrentWorld();
	}

	/**
	 * Checks if the current world you are on is of TYPE type
	 * 
	 * @param TYPE
	 *            type of world
	 * @return true if it is part of that world list
	 */
	public static boolean onWorld(WorldActivity activity) {
		int number = getCurrentWorld();
		for (World world : worlds) {
			if (world.getNumber() == number) {
				return world.getActivity() == activity;
			}
		}
		return false;
	}

	/**
	 * Checks if the current world you are on is members
	 * 
	 * @param boolean
	 *            members
	 * @return true if it is members
	 */
	public static boolean onMembersWorld() {
		int number = getCurrentWorld();
		for (World world : worlds) {
			if (world.getNumber() == number) {
				return world.getType() == WorldType.MEMBERS;
			}
		}
		return false;
	}

	/**
	 * Selects a random world based of type (Members, Free)
	 * 
	 * @return world
	 */
	public static int getRandomWorld(WorldType type) {
		List<World> list = new ArrayList<World>();
		for (World world : worlds) {
			if (world.getActivity() != null)
				continue;
			if (world.getType() == type)
				list.add(world);
		}
		Collections.shuffle(list);
		if (list.size() == 0)
			return -1;
		int current = getCurrentWorld();
		int random = current;
		while (random == current) {
			random = list.get(General.random(0, list.size() - 1)).getNumber();
		}
		return random;
	}

	/**
	 * Gets the column that the world belongs to in the logged out world
	 * switcher menu
	 * 
	 * @param world
	 * @return integer array[column, position] of the world
	 */
	public static int[] getColumn(int world) {
		for (int i = 0; i < COLUMN_1_WORLDS.length; i++) {
			if (world == COLUMN_1_WORLDS[i])
				return new int[] { 1, i };
		}
		for (int i = 0; i < COLUMN_2_WORLDS.length; i++) {
			if (world == COLUMN_2_WORLDS[i])
				return new int[] { 2, i };
		}
		for (int i = 0; i < COLUMN_3_WORLDS.length; i++) {
			if (world == COLUMN_3_WORLDS[i])
				return new int[] { 3, i };
		}
		for (int i = 0; i < COLUMN_4_WORLDS.length; i++) {
			if (world == COLUMN_4_WORLDS[i])
				return new int[] { 4, i };
		}
		for (int i = 0; i < COLUMN_5_WORLDS.length; i++) {
			if (world == COLUMN_5_WORLDS[i])
				return new int[] { 5, i };
		}
		return new int[] { 0, 0 };
	}

	/**
	 * Returns the point from the logged out world switcher menu
	 * 
	 * @param world
	 * @return the point of the world
	 */
	public static Point getPoint(int world) {
		int[] location = getColumn(world);
		int column = location[0];
		int position = location[1];
		if (column == 0) {
			return null;
		}
		else if (column == 1) {
			return new Point(COLUMN_1_WORLD.x, COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
		}
		else if (column == 2) {
			return new Point(COLUMN_1_WORLD.x + (1 * HORIZONTAL_DISTANCE), COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
		}
		else if (column == 3) {
			return new Point(COLUMN_1_WORLD.x + (2 * HORIZONTAL_DISTANCE), COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
		}
		else if (column == 4) {
			return new Point(COLUMN_1_WORLD.x + (3 * HORIZONTAL_DISTANCE), COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
		}
		else if (column == 5) {
			return new Point(COLUMN_1_WORLD.x + (4 * HORIZONTAL_DISTANCE), COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
		}
		return null;
	}

	public static boolean isInGame() {
		if (Game.getGameState() != 30)
			return false;
		Timing.waitCondition(new Condition() {

			public boolean active() {
				General.sleep(500);
				return !Interfaces.isInterfaceValid(50);
			}
		}, 3000);
		return Game.getGameState() == 30;
	}
}
