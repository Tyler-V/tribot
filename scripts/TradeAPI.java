package scripts;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Screen;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "API", name = "Trade")
public class TradeAPI extends Script {

	public void run() {

		while (true) {
			if (Login.getLoginState() != Login.STATE.INGAME) {
				sleep(100);
			} else {
				println("-----");
				println("In First Trade Window: " + Trade.inFirstTradeWindow());
				println("In Second Trade Window: "
						+ Trade.inSecondTradeWindow());
				println("State: " + Trade.getState());
				println("Player Accepted: " + Trade.accepted(false));
				println("Other Player Accepted: " + Trade.accepted(true));
				RSItem[] items1 = Trade.getOfferedItems(true);
				if (items1 != null && items1.length > 0) {
					println("First Window Offered: ");
					for (int i = 0; i < items1.length; i++) {
						if (items1[i].getStack() > 0) {
							println(items1[i].getDefinition().getName() + " ("
									+ items1[i].getID() + ") x "
									+ items1[i].getStack());
						}
					}
				}
				ArrayList<Trade.item> items2 = Trade.getConfirmedItems(true);
				if (items2 != null && items2.size() > 0) {
					println("Second Window Offered: ");
					for (int i = 0; i < items2.size(); i++) {
						println(items2.get(i).getName() + " x "
								+ items2.get(i).getQuantity());
					}
				}
				if (Trade.inFirstTradeWindow()) {
					int ID = 561;
					println("Offering 10 of ITEM ID (" + ID + ")");
					Trade.offer(Inventory.find(ID)[0], 10);
					println("Offering 10 of ITEM ID (" + ID + ")");
					Trade.remove(Inventory.find(ID)[0], 10);
				}

				println("-----");
			}
			sleep(2000);
		}
	}

	public static class Trade {

		public static enum TRADE {
			FIRST_WINDOW, FIRST_WINDOW_ACCEPTED, FIRST_WINDOW_WAITING, SECOND_WINDOW, SECOND_WINDOW_ACCEPTED, SECOND_WINDOW_WAITING
		}

		/**
		 * Returns the current trade state your player is in.
		 * 
		 * @return WINDOW_ACCEPTED will return true if "accepted" appears on
		 *         your player's screen, meaning the opposing player has
		 *         accepted the trade. WINDOW_WAITING will return true if
		 *         "waiting" appears on your player's screen, meaning you have
		 *         accepted the trade and are waiting for the other player to
		 *         accept as well.
		 * 
		 */
		public static TRADE getState() {
			if (inFirstTradeWindow()) {
				if (accepted(true)) {
					return TRADE.FIRST_WINDOW_ACCEPTED;
				} else if (accepted(false)) {
					return TRADE.FIRST_WINDOW_WAITING;
				} else {
					return TRADE.FIRST_WINDOW;
				}
			} else if (inSecondTradeWindow()) {
				if (accepted(true)) {
					return TRADE.SECOND_WINDOW_ACCEPTED;
				} else if (accepted(false)) {
					return TRADE.SECOND_WINDOW_WAITING;
				} else {
					return TRADE.SECOND_WINDOW;
				}
			}
			return null;
		}

		private static ArrayList<RSItem> addItemsFirst(RSInterfaceChild window) {

			ArrayList<RSItem> items = new ArrayList<RSItem>();

			for (int i = 0; i < 28; i++) {
				RSInterfaceComponent item = window.getChild(i);
				if (item != null) {
					String name = item.getComponentName().replaceAll(
							"<col=ff981f>", "");
					int stack = Integer.parseInt(Integer.toString(
							item.getComponentStack()).replaceAll(",", ""));
					items.add(new RSItem(name, null, item.getComponentIndex(),
							item.getComponentItem(), stack, null)); // RSItem.TYPE.TRADE
				}
			}

			return items;
		}

		private static ArrayList<item> addItemsSecond(RSInterfaceChild window) {

			ArrayList<item> items = new ArrayList<item>();

			String[] list = window.getText().split("<br>");
			for (int i = 0; i < list.length; i++) {
				String item = list[i];
				if (list[i].contains("<col=ffffff> x <col=")) {
					Pattern pattern1 = Pattern
							.compile("<col=ff9040>(.+?)<col=ffffff>");
					Matcher matcher1 = pattern1.matcher(item);
					matcher1.find();
					String name = matcher1.group(1);
					String amount = item.substring(item.lastIndexOf('>') + 1);
					if (amount.contains("(")) {
						Pattern pattern2 = Pattern.compile("\\((.*?)\\)");
						Matcher matcher2 = pattern2.matcher(amount);
						matcher2.find();
						amount = matcher2.group(1);
					}
					amount = amount.replaceAll(",", "");
					items.add(new item(name, Integer.parseInt(amount)));
				} else {
					item = item.replaceAll("<col=ff9040>", "");
					if (!item.equals("Absoutely nothing!")) {
						items.add(new item(item, 1));
					}
				}
			}

			return items;
		}

		/**
		 * Returns the offered items in the first trade window, from either the
		 * player you are trading with or your own player.
		 * 
		 * @param other_player
		 *            - True for items offered by the other player; False for
		 *            items offered by your own player
		 * @return returns an array RSItem[]
		 */
		public static RSItem[] getOfferedItems(boolean other_player) {

			final TRADE state = getState();

			if (state == null)
				return null;

			ArrayList<RSItem> items = new ArrayList<RSItem>();
			RSInterfaceChild window;

			if (state == TRADE.FIRST_WINDOW) {
				if (other_player) {
					window = Interfaces.get(335, 50);
					if (window == null)
						return null;
					items = addItemsFirst(window);
				} else {
					window = Interfaces.get(335, 48);
					if (window == null)
						return null;
					items = addItemsFirst(window);
				}
			}
			return items.toArray(new RSItem[items.size()]);
		}

		/**
		 * Returns the offered items in the second trade window, from either the
		 * player you are trading with or your own player.
		 * 
		 * @param other_player
		 *            - True for items offered by the other player; False for
		 *            items offered by your own player
		 * @return returns an array RSItem[]
		 */
		public static ArrayList<item> getConfirmedItems(boolean other_player) {

			final TRADE state = getState();

			if (state == null)
				return null;

			ArrayList<item> items = new ArrayList<item>();
			RSInterfaceChild window;

			if (state == TRADE.SECOND_WINDOW) {
				if (other_player) {
					window = Interfaces.get(334, 40);
					if (window == null)
						return null;
					items = addItemsSecond(window);
				} else {
					window = Interfaces.get(334, 37);
					if (window == null)
						return null;
					items = addItemsSecond(window);
				}
			}
			return items;
		}

		/**
		 * Offers a desired amount of a specific item in the first trade window.
		 * 
		 * @param item
		 *            - RSItem you wish to offer
		 * @param amount
		 *            - integer of the amount you want to offer
		 */
		public static void offer(final RSItem item, int amount) {

			final TRADE state = getState();

			if (state != null && state == TRADE.FIRST_WINDOW) {
				if (item != null && amount > 0) {
					final int before = item.getStack();
					item.click("Offer-X");
					Timing.waitCondition(new Condition() {
						public boolean active() {
							return enterAmountMenuUp();
						}
					}, 2000);
					if (enterAmountMenuUp()) {
						Keyboard.typeSend(Integer.toString(amount));
						Timing.waitCondition(new Condition() {
							public boolean active() {
								return before != item.getStack();
							}
						}, 2000);
					}
				}
			}
		}

		/**
		 * Removes a desired amount of a specific item from the first trade
		 * window.
		 * 
		 * @param item
		 *            - RSItem you wish to remove
		 * @param amount
		 *            - integer of the amount you want to remove
		 */
		public static void remove(final RSItem item, int amount) {

			final TRADE state = getState();

			if (state != null && state == TRADE.FIRST_WINDOW) {
				if (item != null && amount > 0) {
					final int before = item.getStack();
					RSItem[] items = getOfferedItems(false);
					for (int i = 0; i < items.length; i++) {
						RSItem tradeItem = items[i];
						if (tradeItem != null) {
							RSItemDefinition def = tradeItem.getDefinition();
							if (def != null) {
								int id = def.getID();
								if (id > 0) {
									if (id == item.getID()) {
										// click("Remove-X");
										break;
									}
								}
							}
						}
					}
					Timing.waitCondition(new Condition() {
						public boolean active() {
							return enterAmountMenuUp();
						}
					}, 2000);
					if (enterAmountMenuUp()) {
						Keyboard.typeSend(Integer.toString(amount));
						Timing.waitCondition(new Condition() {
							public boolean active() {
								return before != item.getStack();
							}
						}, 2000);
					}
				}
			}
		}

		/**
		 * Used to determine whether the enter amount menu is up for offering or
		 * removing items.
		 * 
		 * @return - True if the menu is up; False if not
		 */
		public static boolean enterAmountMenuUp() {
			Color blue = new Color(0, 0, 128);
			Point starPosition = new Point(260, 428);
			if (Screen.getColorAt(starPosition).equals(blue)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Used to determine whether the trade has been accepted by the other
		 * player or your own player in the first or second trade screen
		 * 
		 * @param other_player
		 *            - True to check if the other player accepted the trade;
		 *            False to check if your player accepted the trade.
		 * @return
		 */
		public static boolean accepted(boolean other_player) {

			if (inFirstTradeWindow()) {
				if (other_player) {
					return Interfaces.get(335, 56).getText()
							.contains("accepted");
				} else {
					return Interfaces.get(335, 56).getText().contains("other");
				}
			} else if (inSecondTradeWindow()) {
				if (other_player) {
					return Interfaces.get(334, 33).getText()
							.contains("accepted");
				} else {
					return Interfaces.get(334, 33).getText().contains("other");
				}
			}

			return false;
		}

		/**
		 * Used to determine if the first trade window is open
		 * 
		 * @return - True if the first trade window is open; false if not
		 */
		public static boolean inFirstTradeWindow() {
			return (Interfaces.get(335) != null);
		}

		/**
		 * Used to determine if the second trade window is open
		 * 
		 * @return - True if the second trade window is open; false if not
		 */
		public static boolean inSecondTradeWindow() {
			return (Interfaces.get(334) != null);
		}

		/**
		 * Class item, String name: Name of item int quantity: Quantity of item
		 */
		public static class item {
			private final String name;
			private final int quantity;

			public item(String name, int quantity) {
				this.name = name;
				this.quantity = quantity;
			}

			public String getName() {
				return this.name;
			}

			public int getQuantity() {
				return this.quantity;
			}
		}

	}

}