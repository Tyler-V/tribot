package scripts.usa.api2007.worlds;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Status;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.NPCChat;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;

public class WorldHopper {

	public static List<World> worlds;

	private final static int WORLD_SWITCHER_MASTER = 69;
	private final static int WORLD_SWITCHER_WORLDS_PANE = 8;
	private final static int WORLD_SWITCHER_WORLDS = 15;

	private final static int WORLD_SWITCHER_SCROLLBAR = 16;
	private final static int WORLD_SWITCHER_SCROLLBAR_PANE = 0;
	private final static int WORLD_SWITCHER_SCROLLBAR_UP_ARROW = 4;
	private final static int WORLD_SWITCHER_SCROLLBAR_DOWN_ARROW = 5;

	private final static int WORLD_SWITCHER_WARNING_DIALOG = 219;
	private final static int WORLD_SWITCHER_WARNING_DIALOG_CONFIRM = 2;

	private final static int LOGOUT_MENU_MASTER = 182;

	private final static Rectangle CLICK_TO_SWITCH = new Rectangle(10, 468, 90, 20);
	private final static Point WORLD_SELECTOR_TOP_RIGHT = new Point(762, 23);
	private final static int WORLD_WIDTH = 87;
	private final static int WORLD_HEIGHT = 18;
	private final static int WORLD_PADDING = 4;
	private final static int SCREEN_HEIGHT = 502;

	// UPDATE
	private final static Point COLUMN_1_WORLD = new Point(189, 46); // 46y / 58y
	private final static int VERTICAL_DISTANCE = 24;
	private final static int HORIZONTAL_DISTANCE = 93;
	private final static int[] COLUMN_1_WORLDS = new int[] { 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320 };
	private final static int[] COLUMN_2_WORLDS = new int[] { 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340 };
	private final static int[] COLUMN_3_WORLDS = new int[] { 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360 };
	private final static int[] COLUMN_4_WORLDS = new int[] { 361, 362, 365, 366, 367, 368, 369, 370, 373, 374, 375, 376, 377, 378, 381, 382, 383, 384, 385, 386 };
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
		try (DataInputStream dis = new DataInputStream(new URL("http://oldschool.runescape.com/slr").openConnection()
				.getInputStream())) {
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
	 *                  , i.e. (1 -> 301, 334 -> 334)
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
	 * Changes to the desired world using the in-game world switcher or logout menu
	 * depending on the game state
	 * 
	 * @param world
	 * @param timeout
	 *                    : duration the bot should click to the world
	 * @return true if we successfully changed worlds
	 */
	public static boolean changeWorld(int world) {
		world = formatWorld(world);
		if (Login.getLoginState() == Login.STATE.INGAME) {
			if (getCurrentWorld() == world) {
				System.out.println("WorldHopper: Already on world " + world + ".");
				return true;
			}
			if (Player.getRSPlayer()
					.isInCombat()) {
				System.out.println("WorldHopper: Unable to change worlds while in combat.");
				return false;
			}
			System.out.println("WorldHopper: Opening world switcher.");
			if (!isWorldSwitcherOpen())
				Interfaces.closeAll();
			if (openWorldSwitcher()) {
				System.out.println("WorldHopper: Navigating to world " + world);
				if (navigateToWorld(world)) {
					System.out.println("WorldHopper: Selecting world " + world);
					if (selectWorld(world)) {
						System.out.println("WorldHopper: Changed to world " + world);
						return true;
					}
				}
			}
		}
		else {
			if (selectWorld(world)) {
				if (Login.login())
					return true;
			}
		}
		return false;
	}

	/**
	 * Will click to the desired world or scroll depending on the distance of world
	 * in the pane.
	 * 
	 * @param world
	 *                    : Desired world, any format.
	 * @param timeout
	 *                    : Total timeout duration for the clicking of the up/down
	 *                    arrows
	 * @return : true if the world is in view
	 */
	public static boolean navigateToWorld(final int world) {
		if (!isWorldSwitcherOpen())
			return false;

		if (isWorldVisible(world))
			return true;

		int distanceToPane = getWorldDistanceToPane(world);

		if (distanceToPane > General.random(50, 150)) {
			RSInterfaceChild scrollbar = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLLBAR);
			if (scrollbar == null)
				return false;

			RSInterfaceComponent scrollbarPane = scrollbar.getChild(WORLD_SWITCHER_SCROLLBAR_PANE);
			if (scrollbarPane == null)
				return false;

			Rectangle scrollbarPaneBounds = scrollbarPane.getAbsoluteBounds();
			if (scrollbarPaneBounds == null)
				return false;

			double paneHeight = getScrollbarPaneHeight();
			double relativePosition = getRelativePosition(world);
			double ratio = relativePosition / paneHeight;
			double scrollbarPosition = ratio * scrollbarPaneBounds.getHeight();

			int x = scrollbarPaneBounds.x + General.random(5, scrollbarPaneBounds.width - 5);
			int y = scrollbarPaneBounds.y + Math.max(1, Math.min(scrollbarPane.getHeight() - 1, (int) scrollbarPosition));

			Mouse.click(new Point(x, y), 1);
			if (isWorldVisible(world))
				return true;
		}

		RSInterfaceChild worldSwitcherPane = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS_PANE);
		if (worldSwitcherPane == null)
			return false;

		final Rectangle worldSwitcherPaneBounds = worldSwitcherPane.getAbsoluteBounds();
		if (worldSwitcherPaneBounds == null)
			return false;

		Condition.wait(() -> {
			if (!worldSwitcherPaneBounds.contains(Mouse.getPos())) {
				Mouse.moveBox(worldSwitcherPaneBounds);
				Condition.wait(() -> worldSwitcherPaneBounds.contains(Mouse.getPos()));
			}

			if (Player.getRSPlayer()
					.isInCombat())
				return false;

			if (isWorldVisible(world))
				return true;

			Direction direction = getWorldDirectionInPane(world);
			int ticks = getTicks(world);

			if (direction == Direction.UP) {
				Mouse.scroll(true, ticks);
			}
			else {
				Mouse.scroll(false, ticks);
			}

			General.sleep(getDelay());
			return false;
		});

		return isWorldVisible(world);
	}

	/**
	 * Will scroll to the desired world if the world switcher menu is open using the
	 * mouse scroll wheel.
	 * 
	 * @param world
	 *                    : Desired world, any format.
	 * @param timeout
	 *                    : Total timeout duration for the clicking of the up/down
	 *                    arrows
	 * @return : true if the world is in view
	 */
	public static boolean scrollToWorld(final int world) {
		if (!isWorldSwitcherOpen())
			return false;

		if (isWorldVisible(world))
			return true;

		RSInterfaceChild worldSwitcherPane = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS_PANE);
		if (worldSwitcherPane == null)
			return false;

		final Rectangle worldSwitcherPaneBounds = worldSwitcherPane.getAbsoluteBounds();
		if (worldSwitcherPaneBounds == null)
			return false;

		Condition.wait(() -> {
			if (!worldSwitcherPaneBounds.contains(Mouse.getPos())) {
				Mouse.moveBox(worldSwitcherPaneBounds);
				Condition.wait(() -> worldSwitcherPaneBounds.contains(Mouse.getPos()));
			}

			if (Player.getRSPlayer()
					.isInCombat())
				return false;

			if (isWorldVisible(world))
				return true;

			Direction direction = getWorldDirectionInPane(world);
			int ticks = getTicks(world);

			if (direction == Direction.UP) {
				Mouse.scroll(true, ticks);
			}
			else {
				Mouse.scroll(false, ticks);
			}

			General.sleep(getDelay());
			return false;
		});

		return isWorldVisible(world);
	}

	/**
	 * Will scroll to the desired world if the world switcher menu is open. If the
	 * distance of the bounds of the desired world are over (200, 250) it will click
	 * the scroll pane first then navigate by the up and down arrows.
	 * 
	 * @param world
	 *                    : Desired world, any format.
	 * @param timeout
	 *                    : Total timeout duration for the clicking of the up/down
	 *                    arrows
	 * @return : true if the world is in view
	 */
	public static boolean clickToWorld(final int world) {
		if (!isWorldSwitcherOpen())
			return false;

		if (isWorldVisible(world))
			return true;

		int distanceToPane = getWorldDistanceToPane(world);

		if (distanceToPane > General.random(0, 50)) {
			RSInterfaceChild scrollbar = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLLBAR);
			if (scrollbar == null)
				return false;

			RSInterfaceComponent scrollbarPane = scrollbar.getChild(WORLD_SWITCHER_SCROLLBAR_PANE);
			if (scrollbarPane == null)
				return false;

			Rectangle scrollbarPaneBounds = scrollbarPane.getAbsoluteBounds();
			if (scrollbarPaneBounds == null)
				return false;

			double paneHeight = getScrollbarPaneHeight();
			double relativePosition = getRelativePosition(world);
			double ratio = relativePosition / paneHeight;
			double scrollbarPosition = ratio * scrollbarPaneBounds.getHeight();

			int x = scrollbarPaneBounds.x + General.random(5, scrollbarPaneBounds.width - 5);
			int y = scrollbarPaneBounds.y + Math.max(1, Math.min(scrollbarPane.getHeight() - 1, (int) scrollbarPosition));

			Mouse.click(new Point(x, y), 1);
		}

		if (isWorldVisible(world))
			return true;

		RSInterface button;
		Direction direction = getWorldDirectionInPane(world);

		if (direction == Direction.UP) {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLLBAR);
			if (child == null)
				return false;
			button = child.getChild(WORLD_SWITCHER_SCROLLBAR_UP_ARROW);
		}
		else {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLLBAR);
			if (child == null)
				return false;
			button = child.getChild(WORLD_SWITCHER_SCROLLBAR_DOWN_ARROW);
		}

		if (button == null)
			return false;

		Condition.wait(() -> {
			if (Player.getRSPlayer()
					.isInCombat())
				return false;

			if (isWorldVisible(world))
				return true;

			if (button.click())
				General.sleep(getDelay());

			return false;
		});

		return isWorldVisible(world);
	}

	private static int getTicks(final int world) {
		int distanceToPane = getWorldDistanceToPane(world);
		return Math.max(General.random(1, 3), distanceToPane / General.random(50, 100));
	}

	private static int getDelay() {
		return General.randomSD(100, 50);
	}

	private enum Direction {
		UP,
		DOWN;
	}

	private static Direction getWorldDirectionInPane(final int world) {
		Rectangle worldSwitcherPaneBounds = getWorldSwitcherPaneBounds();
		if (worldSwitcherPaneBounds == null)
			return null;

		Rectangle worldBounds = getAbsoluteBounds(world);
		if (worldBounds == null)
			return null;

		return worldBounds.y < worldSwitcherPaneBounds.y ? Direction.UP : Direction.DOWN;
	}

	private static int getWorldDistanceToPane(final int world) {
		RSInterfaceChild worldSwitcherPane = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS_PANE);
		if (worldSwitcherPane == null)
			return 0;

		Rectangle worldSwitcherPaneBounds = worldSwitcherPane.getAbsoluteBounds();
		if (worldSwitcherPaneBounds == null)
			return 0;

		int scrollPosition = worldSwitcherPane.getScrollY();

		Rectangle worldBounds = getAbsoluteBounds(world);
		if (worldBounds == null)
			return 0;

		int worldRelativePosition = getRelativePosition(world);

		return Math.abs((scrollPosition + worldSwitcherPaneBounds.height - worldBounds.height) - worldRelativePosition);
	}

	private static int getScrollbarPaneHeight() {
		RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS_PANE);
		if (child == null)
			return 0;

		RSInterfaceComponent[] components = child.getChildren();
		if (components == null || components.length == 0)
			return 0;

		return components[components.length - 1].getY();
	}

	private static Rectangle getWorldSwitcherPaneBounds() {
		RSInterfaceChild worldSwitcherPane = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS_PANE);
		if (worldSwitcherPane == null)
			return null;

		return worldSwitcherPane.getAbsoluteBounds();
	}

	private static int getRelativePosition(int world) {
		if (!isWorldSwitcherOpen())
			return 0;

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMasterAndChild(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS)
				.componentNameContains(Integer.toString(formatWorld(world)))
				.getFirstResult();

		return inter == null ? 0 : inter.getY();
	}

	private static Rectangle getAbsoluteBounds(int world) {
		if (!isWorldSwitcherOpen())
			return null;

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMasterAndChild(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS)
				.componentNameContains(Integer.toString(formatWorld(world)))
				.getFirstResult();

		return inter == null ? null : inter.getAbsoluteBounds();
	}

	private static boolean isWorldVisible(int world) {
		if (!isWorldSwitcherOpen())
			return false;

		RSInterfaceChild worldsPane = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS_PANE);
		if (worldsPane == null)
			return false;

		Rectangle worldsPaneBounds = worldsPane.getAbsoluteBounds();
		if (worldsPaneBounds == null)
			return false;

		Rectangle worldBounds = getAbsoluteBounds(world);
		if (worldBounds == null)
			return false;

		return worldsPaneBounds.contains(worldBounds);
	}

	/**
	 * @return true if the world switcher is open
	 */
	public static boolean isWorldSwitcherOpen() {
		if (!Interfaces.isInterfaceSubstantiated(WORLD_SWITCHER_MASTER))
			return false;

		RSInterface child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_WORLDS);
		if (child == null || child.isHidden())
			return false;

		return Condition.wait(() -> {
			RSInterface[] components = child.getChildren();
			return components != null && components.length > 0;
		});
	}

	/**
	 * Get bounds of logout button from the World Switcher or Logout menu.
	 * 
	 * @return Rectangle bounds
	 */
	private static Rectangle getLogoutBounds() {
		if (WorldHopper.isWorldSwitcherOpen()) {
			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(WORLD_SWITCHER_MASTER)
					.isSubstantiated()
					.actionEquals("Logout")
					.getFirstResult();
			return inter == null ? null : inter.getAbsoluteBounds();
		}
		else {
			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(LOGOUT_MENU_MASTER)
					.isSubstantiated()
					.textEquals("Click here to logout")
					.getFirstResult();
			return inter == null ? null : inter.getAbsoluteBounds();
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

		if (GameTab.open(TABS.LOGOUT)) {
			Rectangle bounds = WorldHopper.getLogoutBounds();
			if (bounds == null)
				return false;

			if (bounds.contains(Mouse.getPos()))
				return true;

			Mouse.moveBox(bounds);

			return Condition.wait(() -> bounds.contains(Mouse.getPos()));
		}

		return false;
	}

	public static boolean logout() {
		if (Login.getLoginState() == Login.STATE.LOGINSCREEN)
			return true;

		if (GameTab.open(TABS.LOGOUT)) {
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

	/**
	 * 
	 * @return true if the world switcher menu is open
	 */
	public static boolean openWorldSwitcher() {
		if (isWorldSwitcherOpen())
			return true;

		if (GameTab.open(TABS.LOGOUT)) {
			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(LOGOUT_MENU_MASTER)
					.actionEquals("World Switcher")
					.getFirstResult();
			if (inter == null)
				return false;

			if (inter.click())
				return Condition.wait(() -> isWorldSwitcherOpen());
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

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(WORLD_SWITCHER_MASTER)
				.actionEquals("Close")
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click())
			return Condition.wait(() -> !isWorldSwitcherOpen());

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

	public static boolean isWorldSwitcherWarningUp() {
		return Entities.find(InterfaceEntity::new)
				.inMaster(WORLD_SWITCHER_WARNING_DIALOG)
				.textMatches("World \\d+ is a .* world!", "Switch to World .*")
				.getFirstResult() != null;
	}

	private static boolean waitForChangeWorld(int world, RSItem[] inventory) {
		Condition.wait(() -> getCurrentWorld() == world || Game.getSetting(18) == 0, () -> {
			if (Player.getRSPlayer()
					.isInCombat() || isWorldSwitcherWarningUp())
				return Status.INTERRUPT;
			return Status.CONTINUE;
		});
		if (Condition.wait(() -> Login.getLoginState() == STATE.INGAME && getCurrentWorld() == world && Game.getSetting(18) == 1 && inventory.length == Inventory.getAll().length, () -> {
			if (Player.getRSPlayer()
					.isInCombat() || isWorldSwitcherWarningUp())
				return Status.INTERRUPT;
			return Status.CONTINUE;
		})) {
			General.sleep(1000);
			return true;
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
			General.sleep(General.randomSD(500, 100));
			Rectangle bounds = getAbsoluteBounds(world);
			if (bounds == null)
				return false;

			RSItem[] inventory = Inventory.getAll();
			Mouse.clickBox(bounds, 1);
			Condition.wait(1000, () -> NPCChat.isContinueChatUp() || isWorldSwitcherWarningUp());
			if (NPCChat.isContinueChatUp() || isWorldSwitcherWarningUp()) {
				if (NPCChat.isContinueChatUp()) {
					NPCChat.selectContinue();
				}
				if (isWorldSwitcherWarningUp()) {
					if (NPCChat.selectOption("Yes. In future, only warn about dangerous worlds") || NPCChat.selectOption("Switch to the .*")) {
						if (onWorld(WorldActivity.DEADMAN))
							Condition.wait(10000,
									() -> !Player.getRSPlayer()
											.isInCombat());
						return waitForChangeWorld(world, inventory);
					}
				}
			}
			else {
				return waitForChangeWorld(world, inventory);
			}
		}
		else {
			if (Login.getLoginState() != Login.STATE.LOGINSCREEN)
				return false;

			if (!isSelectWorldIsUp()) {
				Mouse.clickBox(CLICK_TO_SWITCH, 1);
				Condition.wait(() -> isSelectWorldIsUp());
			}

			if (isSelectWorldIsUp()) {
				if (worldListSorted()) {
					Rectangle bounds = getLoginScreenWorldBounds(world);
					if (bounds == null)
						return false;

					Mouse.clickBox(bounds, 1);
					return Condition.wait(() -> !isSelectWorldIsUp() && getCurrentWorld() == world);
				}
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
				worlds.get(i)
						.setWorldSelectBounds(buttons.get(i));
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
	 *                  number
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
		if (Screen.getColorAt(arrow)
				.getGreen() > 50)
			return true;

		Mouse.click(arrow, 1);
		return Condition.wait(() -> Screen.getColorAt(arrow)
				.getGreen() > 50);
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
	 *                 type of world
	 * @return true if it is part of that world list
	 */
	public static boolean onWorld(WorldActivity activity) {
		int number = getCurrentWorld();
		for (World world : worlds) {
			if (world.getNumber() == number)
				return world.getActivity() == activity;
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
			if (world.getNumber() == number)
				return world.getType() == WorldType.MEMBERS;
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
			random = list.get(General.random(0, list.size() - 1))
					.getNumber();
		}
		return random;
	}

	/**
	 * Gets the column that the world belongs to in the logged out world switcher
	 * menu
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

		Condition.wait(() -> !Interfaces.isInterfaceValid(50));

		return Game.getGameState() == 30;
	}
}
