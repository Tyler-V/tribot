package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "USA", name = "USA Merchanter")
public class UsaMerchanter extends Script implements Painting,
		MessageListening07, MouseActions {

	String version = "5.3";

	// OPTIONS
	private int maxPlayerTradeAttempts = 0;
	private long tradeTimerLength = 0;
	private long samePlayerTradeTimer = 0;
	private boolean autochat;
	private boolean showItem;
	// END OPTIONS

	private long tradeTimer;
	private long autochatTimer;
	private tradeItem task = null;
	private tradeItem lastTask = null;
	private ArrayList<tradeItem> tradeItems = new ArrayList<tradeItem>();
	private ArrayList<String> blackList = new ArrayList<String>();
	private ArrayList<tradingPlayer> playersTraded = new ArrayList<tradingPlayer>();
	private ArrayList<String> history = new ArrayList<String>();
	private RSTile startPos;

	private int trade = 0;
	private int itemsBought = 0;
	private int itemsSold = 0;
	private int startingGold = 0;

	private String status = "Starting";
	private String trader = "";

	private long startTime;

	private boolean display = true;
	private final Image open = getImage("http://i.imgur.com/rgNz0Pm.png");
	private final Image closed = getImage("http://i.imgur.com/kzjIFiz.png");
	private boolean run = true;
	private boolean show = true;

	private boolean gui_is_up = true;
	gui g = new gui();

	private ABCUtil abc;

	@Override
	public void run() {

		g.setVisible(true);
		while (gui_is_up) {
			sleep(200);
		}

		abc = new ABCUtil();
		startingGold = Inventory.getCount("Coins");
		startTime = System.currentTimeMillis();

		if (Login.getLoginState() == Login.STATE.INGAME) {
			for (int i = 0; i < tradeItems.size(); i++) {
				tradeItems.get(i).setStartAmount(
						Inventory.getCount(tradeItems.get(i).getName()));
			}
			startPos = Player.getPosition();
			openInventory();
			startingGold = Inventory.getCount("Coins");
			startTime = System.currentTimeMillis();
			Camera.setCameraAngle(100);

			while (run) {

				RSTile pos = Player.getPosition();
				if (PathFinding.canReach(startPos, false)
						&& pos.distanceTo(startPos) >= 3) {
					status = "Moving back to start position!";
					if (pos.distanceTo(startPos) <= 5) {
						Walking.clickTileMM(startPos, 1);
					} else {
						PathFinding.aStarWalk(startPos);
					}
					sleep(400, 900);
				} else if (!PathFinding.canReach(startPos, false)) {
					RSObject[] stairs = Objects.findNearest(10, "Staircase");
					if (stairs.length > 0) {
						if (stairs[0] != null) {
							if (objectHasAction(stairs[0], "Climb-up")) {
								status = "Climbing-up stairs";
								final RSTile before = Player.getPosition();
								stairs[0].click("Climb-up");
								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(500);
										return !PathFinding.canReach(before,
												false);
									}
								}, 3000);
							}
						}
					}
					RSObject[] door = Objects.findNearest(10, "Door");
					if (door.length > 0) {
						if (door[0] != null) {
							if (objectHasAction(door[0], "Open")) {
								status = "Opening door";
								door[0].click("Open");
								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(500);
										return PathFinding.canReach(startPos,
												false);
									}
								}, 3000);
							}
						}
					}

				} else if (NPCChat.getClickContinueInterface() != null) {
					status = "Clicking continue";
					NPCChat.clickContinue(true);
					Timing.waitCondition(new Condition() {
						public boolean active() {
							return NPCChat.getClickContinueInterface() == null;
						}
					}, 2000);
				} else {
					openInventory();
					WINDOW_STATE window = Trading.getWindowState();
					if (window == null) {
						show = true;
						task = getCurrentItem();
						if (task != null) {
							if (task != lastTask) {
								status = "New Task: Update";
								lastTask = task;
								for (int i = 0; i < tradeItems.size(); i++) {
									if (tradeItems.get(i) == task) {
										tradeItems.get(i).setStartAmount(
												Inventory.getCount(task
														.getName()));
										println("UPDATED ITEM ("
												+ i
												+ ") "
												+ tradeItems.get(i).getName()
												+ " starting quantity from "
												+ tradeItems.get(i)
														.getStartAmount()
												+ " to "
												+ Inventory.getCount(task
														.getName()));
									}
								}
							}
							if (task.isSelling()) {
								status = "Selling " + task.getName();
							} else {
								status = "Buying " + task.getName();
							}

						} else {

							long check = Timing.currentTimeMillis() + 10000;
							boolean noItems = true;
							while (check > Timing.currentTimeMillis()) {
								if (getCurrentItem() != null) {
									noItems = false;
									break;
								}
							}
							if (noItems) {
								println("-");
								println("--");
								println("---");
								println("----");
								println("-----");
								println("Elapsed Time: "
										+ Timing.msToString(System
												.currentTimeMillis()
												- startTime));
								println("Total Gained Coins: "
										+ addCommasToNumericString(Integer
												.toString(Inventory
														.getCount("Coins")
														- startingGold)));
								println("Total Items Sold: "
										+ addCommasToNumericString(Integer
												.toString(itemsSold)));
								println("Total Items Bought: "
										+ addCommasToNumericString(Integer
												.toString(itemsBought)));
								println("We have completed all trading tasks!");
								println("-----");
								run = false;
								break;
							}
						}

						if (task != null) {
							if (autochat) {
								RSInterfaceChild autochat = Interfaces.get(548,
										119);
								RSInterfaceChild publicChat = Interfaces.get(
										548, 10);
								boolean autoChatActive = false;
								if (publicChat != null
										&& (autochatTimer > Timing
												.currentTimeMillis())) {
									String[] actions = publicChat.getActions();
									if (actions.length > 0) {
										if (containsAction("Pause autochat",
												actions)) {
											autoChatActive = true;
										}
									}
								}
								if ((autochat != null && !autochat.getText()
										.contains(task.getMessage()))
										|| !autoChatActive) {
									if (autochat.isHidden()) {
										if (publicChat != null) {
											publicChat
													.click("Setup your autochat");
											Timing.waitCondition(
													new Condition() {
														public boolean active() {
															return !Interfaces
																	.get(548,
																			123)
																	.isHidden();
														}
													}, 2000);
										}
									} else {
										Keyboard.typeSend(task.getMessage());
										autochatTimer = Timing
												.currentTimeMillis()
												+ General
														.random(300000, 600000);
									}
								}
							} else {
								RSInterfaceChild chat = Interfaces.get(137, 1);
								if (chat != null) {
									String message = chat.getText();
									if (message != null && message.length() > 0) {
										if (message.contains("<col=0000ff>*")) {
											status = "Typing Message";
											Keyboard.typeSend(task.getMessage());
										} else {
											status = "Erasing Chat";
											Condition stopping_condition = new Condition() {
												public boolean active() {
													RSInterfaceChild chat = Interfaces
															.get(137, 1);
													String message = "";
													if (chat != null) {
														message = chat
																.getText();
													}
													return message
															.contains("<col=0000ff>*");
												}
											};
											Keyboard.holdKey(
													(char) KeyEvent.VK_BACK_SPACE,
													KeyEvent.VK_BACK_SPACE,
													stopping_condition);
										}
									}
								}
							}
						}

					} else {

						if (tradeTimer > Timing.currentTimeMillis()) {
							trader = Trading.getOpponentName();
							if (task != null) {
								if (window == WINDOW_STATE.FIRST_WINDOW) {
									boolean goodTrade = false;
									if (task.isSelling()) {
										if (showItem && show) {
											Trading.offer(0, task.getName());
											sleep(1500, 3000);
											show = false;
										}
										int amount = Trading.getCount(false,
												task.getName());
										int cost = (int) Math.floor(Trading
												.getCount(true, "Coins")
												/ task.getPrice());
										int offer = cost - amount;
										if (offer > 0
												&& Inventory.getCount(task
														.getName()) > 0) {
											if (offer > Inventory.getCount(task
													.getName())) {
												offer = Inventory.getCount(task
														.getName());
												status = "Offering " + offer
														+ " " + task.getName();
												Trading.offer(offer,
														task.getName());
											} else {
												status = "Offering " + offer
														+ " " + task.getName();
												Trading.offer(offer,
														task.getName());
											}
										} else if (offer < 0) {
											status = "Removing "
													+ Math.abs(offer) + " "
													+ task.getName();
											Trading.remove(Math.abs(offer),
													task.getName());
										} else if (amount > 0) {
											status = "Good Trade";
											goodTrade = true;
										}
									} else {
										int coins = Trading.getCount(false,
												"Coins");
										int amount = Trading.getCount(true,
												task.getName());
										int leftToBuy = (task.getAmount() - Inventory
												.getCount(task.getName()));
										if (amount > leftToBuy) {
											amount = leftToBuy;
										}
										int cost = amount * task.getPrice();
										int offer = cost - coins;
										if (offer > 0) {
											status = "Offering " + offer
													+ " Coins";
											Trading.offer(offer, "Coins");
										} else if (offer < 0) {
											status = "Removing "
													+ Math.abs(offer)
													+ " Coins";
											Trading.remove(Math.abs(offer),
													"Coins");
										} else if (coins > 0) {
											status = "Good Trade";
											goodTrade = true;
										}
									}

									if (goodTrade) {
										if (!Trading.hasAccepted(false)) {
											status = "Accepting Trade";
											Trading.accept();
										}
									}

								} else if (window == WINDOW_STATE.SECOND_WINDOW) {

									int coins = 0;
									int amount = 0;
									int cost = 0;
									int offer = 0;

									boolean goodTrade = false;
									String trade = "";
									if (task.isSelling()) {
										coins = Trading.getCount(true, "Coins");
										amount = Trading.getCount(false,
												task.getName());
										cost = (int) Math.floor(coins
												/ task.getPrice());
										offer = cost - amount;
										if (offer >= 0) {
											status = "Good Trade";
											trade = "Sold " + amount + " "
													+ task.getName() + " to "
													+ trader + " for " + coins
													+ "gp.";
											goodTrade = true;
										} else {
											status = "Bad Trade";
											trade = "Failed to sell " + cost
													+ " " + task.getName()
													+ " to " + trader + ".";
										}

									} else {
										amount = Trading.getCount(true,
												task.getName());
										cost = amount * task.getPrice();
										offer = cost
												- Trading.getCount(false,
														"Coins");
										if (offer >= 0) {
											status = "Good Trade";
											trade = "Bought " + amount + " "
													+ task.getName() + " from "
													+ trader + " for " + cost
													+ "gp.";
											goodTrade = true;
										} else {
											status = "Bad Trade";
											trade = "Failed to buy " + offer
													+ " " + task.getName()
													+ " to " + trader + ".";
										}
									}

									if (goodTrade) {
										status = "Accepting Trade";
										if (Trading.hasAccepted(true)) {
											Trading.accept();
											if (Trading.getWindowState() == null) {
												if (task.isSelling()) {
													itemsSold += amount;
												} else {
													itemsBought += amount;
												}
												resetTradeCounter(trader);
												history.add(trade);
											}
										}
									} else {
										status = "Closing Trade";
										history.add(trade);
										Trading.close();
									}
								}
							}
						} else {
							println("Trade timer end "
									+ Timing.msToString(tradeTimer));
							status = "Closing Trade";
							Trading.close();
						}
					}
				}
				abc.performRotateCamera();
				abc.performExamineObject();
				abc.performPickupMouse();
				abc.performRandomMouseMovement();
				abc.performRandomRightClick();
				abc.performQuestsCheck();
				abc.performFriendsCheck();
				abc.performMusicCheck();
				abc.performCombatCheck();
				sleep(25);
			}
			Login.logout();
			println("Logging Out!");
		}
	}

	private tradeItem getCurrentItem() {
		for (int i = 0; i < tradeItems.size(); i++) {
			String name = tradeItems.get(i).getName();
			int amount = tradeItems.get(i).getAmount();
			int startAmount = tradeItems.get(i).getStartAmount();
			if (tradeItems.get(i).isSelling()) { // selling
				if (Inventory.getCount(name) > 0
						&& (Inventory.getCount(name) > (startAmount - amount))) {
					// if (debug) {
					// println("[SELL] Name: " + name + " / " + "Amount: "
					// + amount + " / " + " Start Amount: "
					// + startAmount);
					// debug = false;
					// }
					return tradeItems.get(i);
				}
			} else { // buying
				if (Inventory.getCount("Coins") > 0
						&& (Inventory.getCount(name) < amount)) {
					// if (debug) {
					// println("[BUY] Name: " + name + " / " + "Amount: "
					// + amount + " / " + " Start Amount: "
					// + startAmount);
					// debug = false;
					// }
					return tradeItems.get(i);
				}
			}
		}
		return null;
	}

	private boolean containsAction(String action, String[] actions) {
		for (int i = 0; i < actions.length; i++) {
			if (actions[i].contains(action)) {
				return true;
			}
		}
		return false;
	}

	private void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			status = "Opening Inventory";
			GameTab.open(TABS.INVENTORY);
			sleep(50, 150);
		}
	}

	private void resetTradeCounter(String username) {
		for (int i = 0; i < playersTraded.size(); i++) {
			if (playersTraded.get(i).getUsername().equalsIgnoreCase(username)) {
				playersTraded.get(i).setTradeCount(0);
				println("Reset " + username + "'s trade counter.");
				break;
			}
		}
	}

	private boolean validPlayer(String username) {
		for (int i = 0; i < blackList.size(); i++) {
			if (blackList.get(i).equals(username)) {
				println("BLACKLIST: Ignoring \"" + username + "\"");
				return false;
			}
		}

		for (int i = 0; i < playersTraded.size(); i++) {
			tradingPlayer player = playersTraded.get(i);
			if (player.getUsername().equals(username)) {
				if (player.getTradeRequests() >= maxPlayerTradeAttempts) {
					blackList.add(player.getUsername());
					println("BLACKLIST: Ignoring \"" + username + "\"");
					return false;
				} else {
					if ((System.currentTimeMillis() - player
							.getLastTimeTraded()) > samePlayerTradeTimer) {
						player.updateTime(System.currentTimeMillis());
						// println("Traded player at "
						// + Timing.msToString(player.getLastTimeTraded()));
						player.addTrade();
						return true;
					} else {
						println("Time before trading \""
								+ username
								+ "\" is "
								+ (Timing.msToString(samePlayerTradeTimer
										- (System.currentTimeMillis() - player
												.getLastTimeTraded()))));
						return false;
					}
				}
			}
		}
		playersTraded.add(new tradingPlayer(username, 0, System
				.currentTimeMillis()));
		return true;
	}

	private void trade(String user) {
		if (validPlayer(user)) {
			status = "Trading " + user;
			RSPlayer[] p = Players.find(user);
			if (p.length > 0) {
				for (int i = 0; i < p.length; i++) {
					RSPlayer player = p[i];
					if (player != null) {
						String name = player.getName();
						if (name != null) {
							if (name.equals(user)) {
								if (player.isOnScreen()) {
									long trading = Timing.currentTimeMillis() + 20000;
									while (trading > Timing.currentTimeMillis()) {
										if (Trading.getWindowState() != null) {
											break;
										} else {
											if (!ChooseOption.isOpen()) {
												player.hover();
												sleep(50, 100);
												Mouse.click(3);
												Timing.waitCondition(
														new Condition() {
															public boolean active() {
																return ChooseOption
																		.isOpen();
															}
														}, 1000);
											} else {
												tradeTimer = Timing
														.currentTimeMillis()
														+ tradeTimerLength;
												if (ChooseOption
														.select("Trade with "
																+ user)) {
													sleepUntilTradeWindowUp();
												} else {
													ChooseOption
															.select("Cancel");
												}
											}
										}
										sleep(100);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean objectHasAction(RSObject obj, String name) {
		if (obj != null) {
			RSObjectDefinition def = obj.getDefinition();
			if (def != null) {
				String[] actions = def.getActions();
				for (String action : actions) {
					if (action.equalsIgnoreCase(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void sleepUntilTradeWindowUp() {
		println("window up");
		Timing.waitCondition(new Condition() {
			public boolean active() {
				return Trading.getWindowState().equals(
						Trading.WINDOW_STATE.FIRST_WINDOW);
			}
		}, 5000);
	}

	private class tradingPlayer {
		private String username;
		private int tradeRequests;
		private long time;

		public tradingPlayer(String username, int tradeRequests, long time) {
			this.username = username;
			this.tradeRequests = tradeRequests;
			this.time = time;
		}

		public String getUsername() {
			return username;
		}

		public int getTradeRequests() {
			return tradeRequests;
		}

		public void addTrade() {
			tradeRequests++;
		}

		public void setTradeCount(int n) {
			tradeRequests = n;
		}

		public long getLastTimeTraded() {
			return time;
		}

		public void updateTime(long n) {
			time = n;
		}
	}

	private class tradeItem {
		private String name;
		private int amount;
		private int price;
		private int startAmount;
		private String message;
		private boolean selling;

		public tradeItem(String name, int amount, int price, boolean selling,
				String message) {
			this.name = name;
			this.amount = amount;
			this.price = price;
			this.selling = selling;
			this.message = message;
		}

		public String getName() {
			return name;
		}

		public int getAmount() {
			return amount;
		}

		public int getPrice() {
			return price;
		}

		public int getStartAmount() {
			return startAmount;
		}

		public void setStartAmount(int n) {
			startAmount = n;
		}

		public void setMessage(String s) {
			message = s;
		}

		public String getMessage() {
			return message;
		}

		public boolean isSelling() {
			return selling;
		}

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

		for (int i = len - 1; i >= 0; i--) {
			result = digits.charAt(i) + result;
			nDigits++;
			if (((nDigits % 3) == 0) && (i > 0)) {
				result = "," + result;
			}
		}
		return (result);
	}

	private static String parseTime(long millis, boolean newFormat) {
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
		minutes = minutes + " minute"
				+ ((Integer.valueOf(minutes) != 1) ? "s" : "");
		seconds = seconds + " second"
				+ ((Integer.valueOf(seconds) != 1) ? "s" : "");
		// return days + ", " + hours + ", " + minutes + ", " + seconds;
		return seconds;
	}

	public void onPaint(Graphics g) {
		WINDOW_STATE state = Trading.getWindowState();
		long time = System.currentTimeMillis() - startTime;
		int tradesPerHour = (int) (trade * 3600000D / (System
				.currentTimeMillis() - startTime));
		int soldPerHour = (int) (itemsSold * 3600000D / (System
				.currentTimeMillis() - startTime));
		int boughtPerHour = (int) (itemsBought * 3600000D / (System
				.currentTimeMillis() - startTime));

		int coins = startingGold - Inventory.getCount("Coins");

		int x;
		int y;

		if (display) {

			g.setColor(Color.BLACK);
			g.drawRect(6, 6, 400, 100);
			g.drawLine(6, 25, 406, 25);
			g.setColor(new Color(50, 50, 50, 200));
			g.fillRect(7, 7, 399, 99);
			g.setColor(Color.WHITE);

			g.drawImage(open, 7, 313, null); // draw open image

			/*
			 * Column 1
			 */
			x = 235;
			y = 389;
			g.drawString(status, x, y);
			y = 412;
			g.drawString(parseTime((time), false) + "", x, y);
			y = 434;
			if (task != null && task.isSelling()) {
				if (state != null) {
					g.drawString("In trade!", x, y);
				} else {
					g.drawString(
							""
									+ (addCommasToNumericString(Integer
											.toString(itemsSold))) + " ("
									+ soldPerHour + "/hr)", x, y);
				}
			} else {
				g.drawString("Not selling!", x, y);
			}
			y = 455;
			if (tradeItems.size() > 0) {
				if (state != null) {
					g.drawString("In trade!", x, y);
				} else {
					g.drawString(
							""
									+ (addCommasToNumericString(Integer
											.toString(itemsBought))) + " ("
									+ boughtPerHour + "/hr)", x, y);
				}
			} else {
				g.drawString("Not buying!", x, y);
			}
			// END //

			/*
			 * Column 2
			 */
			x = 424;
			y = 411;
			g.drawString("" + blackList.size(), x, y);
			x = 410;
			y = 434;
			g.drawString("" + trade + " (" + tradesPerHour + "/hr)", x, y);
			x = 395;
			y = 455;
			String gold = "null";
			if (state != null) {
				gold = "In trade!";
			} else {
				if (coins > 0) {
					gold = "-"
							+ addCommasToNumericString(Integer.toString(coins));
				} else {
					gold = "+"
							+ addCommasToNumericString(Integer.toString(Math
									.abs(coins)));
				}
			}
			g.drawString(gold, x, y);
			// END //

			Font font = new Font("Verdana", 0, 10);
			g.setFont(font);
			g.drawString("v" + version, 486, 471);

			if (state != null) {
				if (tradeTimer > Timing.currentTimeMillis()) {
					g.drawString(
							"Trade Timer: "
									+ Timing.msToString(tradeTimer
											- Timing.currentTimeMillis()), 25,
							320);
				}
			}

		} else {
			g.drawImage(closed, 7, 313, null); // draw closed image
		}

		x = 85;
		y = 20;
		g.drawString("Recent Trade History", x, y);
		y += 20;
		x = 10;
		if (history.size() > 0) {
			if (history.size() >= 5) {
				for (int i = (history.size() - 1); i > (history.size() - 6); i--) {
					if (history.get(i) != null) {
						g.drawString(history.get(i), x, y);
						y += 15;
					}
				}
			} else {
				for (int i = (history.size() - 1); i >= 0; i--) {
					if (history.get(i) != null) {
						g.drawString(history.get(i), x, y);
						y += 15;
					}
				}
			}
		} else {
			g.drawString("No trades yet!", x, y);
		}
	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void playerMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void serverMessageReceived(String arg0) {
	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void tradeRequestReceived(String player) {
		RSPlayer[] players = Players.getAll();
		for (int i = 0; i < players.length; i++) {
			if (players[i].getName().equalsIgnoreCase(player)) {
				trade++;
				trade(player);
				break;
			}
		}
	}

	@Override
	public void mouseClicked(Point arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		Rectangle rect = new Rectangle(445, 345, 50, 20);
		if (rect.contains(arg0)) {
			display = !display;
		}
	}

	@Override
	public void mouseDragged(Point arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(Point arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(Point arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public class gui extends JFrame {

		private JPanel contentPane;
		private JTextField itemName;
		private JTextField itemPrice;
		private JTextField itemQuantity;
		private JTextField itemMessage;
		private JRadioButton buyButton;
		private JRadioButton sellButton;
		private JTextPane tasks;
		private JSpinner tradeDuration;
		private JSpinner maximumTrades;
		private JSpinner samePlayerTimer;
		private JCheckBox autochatOption;
		private JCheckBox chckbxShowAllItem;

		public gui() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 503, 596);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblAddTask = new JLabel("Add Item");
			lblAddTask.setHorizontalAlignment(SwingConstants.CENTER);
			lblAddTask.setFont(new Font("Verdana", Font.PLAIN, 20));
			lblAddTask.setBounds(15, 53, 203, 32);
			contentPane.add(lblAddTask);

			itemName = new JTextField();
			itemName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			itemName.setBounds(94, 96, 112, 20);
			contentPane.add(itemName);
			itemName.setColumns(10);

			itemPrice = new JTextField();
			itemPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			itemPrice.setBounds(94, 127, 112, 20);
			contentPane.add(itemPrice);
			itemPrice.setColumns(10);

			itemQuantity = new JTextField();
			itemQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			itemQuantity.setBounds(94, 158, 112, 20);
			contentPane.add(itemQuantity);
			itemQuantity.setColumns(10);

			JLabel lblName = new JLabel("Name:");
			lblName.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblName.setBounds(27, 99, 46, 14);
			contentPane.add(lblName);

			JLabel lblPrice = new JLabel("Price:");
			lblPrice.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblPrice.setBounds(27, 130, 46, 14);
			contentPane.add(lblPrice);

			JLabel lblQuantity = new JLabel("Quantity:");
			lblQuantity.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblQuantity.setBounds(27, 161, 59, 14);
			contentPane.add(lblQuantity);

			buyButton = new JRadioButton("Buy");
			buyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (sellButton.isSelected()) {
						sellButton.setSelected(false);
					}
					buyButton.setSelected(true);
				}
			});
			buyButton.setHorizontalAlignment(SwingConstants.CENTER);
			buyButton.setFont(new Font("Verdana", Font.PLAIN, 12));
			buyButton.setBounds(25, 224, 83, 23);
			contentPane.add(buyButton);

			sellButton = new JRadioButton("Sell");
			sellButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (buyButton.isSelected()) {
						buyButton.setSelected(false);
					}
					sellButton.setSelected(true);
				}
			});
			sellButton.setHorizontalAlignment(SwingConstants.CENTER);
			sellButton.setFont(new Font("Verdana", Font.PLAIN, 12));
			sellButton.setBounds(129, 224, 77, 23);
			contentPane.add(sellButton);

			JSeparator separator = new JSeparator();
			separator.setBounds(15, 71, 48, 2);
			contentPane.add(separator);

			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(172, 71, 136, 2);
			contentPane.add(separator_1);

			JSeparator separator_2 = new JSeparator();
			separator_2.setBounds(15, 247, 202, 2);
			contentPane.add(separator_2);

			JSeparator separator_3 = new JSeparator();
			separator_3.setOrientation(SwingConstants.VERTICAL);
			separator_3.setBounds(216, 71, 2, 214);
			contentPane.add(separator_3);

			JSeparator separator_4 = new JSeparator();
			separator_4.setOrientation(SwingConstants.VERTICAL);
			separator_4.setBounds(15, 71, 2, 214);
			contentPane.add(separator_4);

			JSeparator separator_5 = new JSeparator();
			separator_5.setBounds(15, 285, 459, 2);
			contentPane.add(separator_5);

			JLabel lblUsaMerchanter = new JLabel("USA Merchanter");
			lblUsaMerchanter.setForeground(Color.BLACK);
			lblUsaMerchanter.setBackground(Color.WHITE);
			lblUsaMerchanter.setHorizontalAlignment(SwingConstants.CENTER);
			lblUsaMerchanter.setFont(new Font("Levenim MT", Font.BOLD, 30));
			lblUsaMerchanter.setBounds(0, 10, 474, 32);
			contentPane.add(lblUsaMerchanter);

			JSeparator separator_8 = new JSeparator();
			separator_8.setBounds(15, 222, 203, 2);
			contentPane.add(separator_8);

			tasks = new JTextPane();
			tasks.setBackground(Color.BLACK);
			tasks.setForeground(Color.WHITE);
			tasks.setEditable(false);
			tasks.setFont(new Font("Segoe UI", Font.BOLD, 10));
			tasks.setBounds(224, 84, 242, 196);
			contentPane.add(tasks);

			JLabel lblTasks = new JLabel("Tasks");
			lblTasks.setHorizontalAlignment(SwingConstants.CENTER);
			lblTasks.setFont(new Font("Verdana", Font.PLAIN, 20));
			lblTasks.setBounds(228, 53, 222, 32);
			contentPane.add(lblTasks);

			itemMessage = new JTextField();
			itemMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			itemMessage.setBounds(94, 189, 112, 20);
			contentPane.add(itemMessage);
			itemMessage.setColumns(10);

			JLabel lblMessage = new JLabel("Message:");
			lblMessage.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblMessage.setBounds(28, 192, 66, 14);
			contentPane.add(lblMessage);

			JSeparator separator_6 = new JSeparator();
			separator_6.setOrientation(SwingConstants.VERTICAL);
			separator_6.setBounds(117, 224, 2, 22);
			contentPane.add(separator_6);

			JSeparator separator_7 = new JSeparator();
			separator_7.setBounds(373, 71, 101, 2);
			contentPane.add(separator_7);

			JSeparator separator_9 = new JSeparator();
			separator_9.setOrientation(SwingConstants.VERTICAL);
			separator_9.setBounds(473, 71, 2, 214);
			contentPane.add(separator_9);

			JLabel lblOtherOptions = new JLabel("Settings");
			lblOtherOptions.setHorizontalAlignment(SwingConstants.CENTER);
			lblOtherOptions.setFont(new Font("Verdana", Font.PLAIN, 20));
			lblOtherOptions.setBounds(15, 296, 459, 32);
			contentPane.add(lblOtherOptions);

			JSeparator separator_10 = new JSeparator();
			separator_10.setBounds(0, 45, 487, 2);
			contentPane.add(separator_10);

			tradeDuration = new JSpinner();
			tradeDuration.setModel(new SpinnerNumberModel(120, 60, 180, 10));
			tradeDuration.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			tradeDuration.setBounds(188, 336, 46, 20);
			contentPane.add(tradeDuration);

			JLabel lblMaximumTradeLength = new JLabel("Maximum trade duration:");
			lblMaximumTradeLength.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblMaximumTradeLength.setBounds(27, 339, 158, 14);
			contentPane.add(lblMaximumTradeLength);

			JSeparator separator_11 = new JSeparator();
			separator_11.setBounds(15, 312, 187, 2);
			contentPane.add(separator_11);

			JSeparator separator_12 = new JSeparator();
			separator_12.setBounds(288, 312, 186, 2);
			contentPane.add(separator_12);

			JSeparator separator_13 = new JSeparator();
			separator_13.setOrientation(SwingConstants.VERTICAL);
			separator_13.setBounds(15, 312, 2, 182);
			contentPane.add(separator_13);

			JSeparator separator_14 = new JSeparator();
			separator_14.setOrientation(SwingConstants.VERTICAL);
			separator_14.setBounds(473, 312, 2, 182);
			contentPane.add(separator_14);

			JSeparator separator_15 = new JSeparator();
			separator_15.setBounds(15, 494, 459, 2);
			contentPane.add(separator_15);

			JLabel lblTimeBeforeTrading = new JLabel(
					"Delay before trading previous player:");
			lblTimeBeforeTrading.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblTimeBeforeTrading.setBounds(27, 365, 234, 14);
			contentPane.add(lblTimeBeforeTrading);

			samePlayerTimer = new JSpinner();
			samePlayerTimer.setModel(new SpinnerNumberModel(5, 0, 60, 5));
			samePlayerTimer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			samePlayerTimer.setBounds(260, 362, 40, 20);
			contentPane.add(samePlayerTimer);

			JLabel lblMaximumTradeAttempts = new JLabel(
					"Maximum trade attempts before ignoring player:");
			lblMaximumTradeAttempts
					.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblMaximumTradeAttempts.setBounds(27, 391, 305, 14);
			contentPane.add(lblMaximumTradeAttempts);

			maximumTrades = new JSpinner();
			maximumTrades.setModel(new SpinnerNumberModel(new Integer(3),
					new Integer(0), null, new Integer(1)));
			maximumTrades.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			maximumTrades.setBounds(336, 388, 40, 20);
			contentPane.add(maximumTrades);

			autochatOption = new JCheckBox("Use Autochat");
			autochatOption.setSelected(false);
			autochatOption.setFont(new Font("Verdana", Font.PLAIN, 12));
			autochatOption.setBounds(23, 441, 236, 14);
			contentPane.add(autochatOption);

			chckbxShowAllItem = new JCheckBox(
					"Show all of item when trade first opens if selling");
			chckbxShowAllItem.setSelected(true);
			chckbxShowAllItem.setFont(new Font("Verdana", Font.PLAIN, 12));
			chckbxShowAllItem.setBounds(23, 464, 371, 14);
			contentPane.add(chckbxShowAllItem);

			JButton add = new JButton("Add");
			add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String task = "";
					String name = itemName.getText().trim();
					String quantity = itemQuantity.getText().trim();
					String price = itemPrice.getText().trim();
					String message = itemMessage.getText().trim();
					if (buyButton.isSelected()) {
						task = "Buying " + quantity + " " + name + " for "
								+ price + " each.";
						tradeItems.add(new tradeItem(name, Integer
								.parseInt(quantity), Integer.parseInt(price),
								false, message));
					} else if (sellButton.isSelected()) {
						task = "Selling " + quantity + " " + name + " for "
								+ price + " each.";
						tradeItems.add(new tradeItem(name, Integer
								.parseInt(quantity), Integer.parseInt(price),
								true, message));
					}
					tasks.setText(tasks.getText() + task + "\n");
				}
			});
			add.setFont(new Font("Verdana", Font.BOLD, 12));
			add.setBounds(68, 255, 101, 23);
			contentPane.add(add);

			JButton Start = new JButton("Start");
			Start.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					maxPlayerTradeAttempts = (int) maximumTrades.getValue();
					tradeTimerLength = (long) (((int) tradeDuration.getValue()) * 1000);
					samePlayerTradeTimer = (long) (((int) samePlayerTimer
							.getValue()) * 1000);
					autochat = autochatOption.isSelected();
					showItem = chckbxShowAllItem.isSelected();
					println("Maximum Trade Duration: " + tradeTimerLength);
					println("Same Player Trade Timer: " + samePlayerTradeTimer);
					println("Maximum Player Trade Attempts: "
							+ maxPlayerTradeAttempts);
					println("Using autochat? " + autochat);
					println("Show item? " + showItem);

					for (int i = 0; i < tradeItems.size(); i++) {
						tradeItem item = tradeItems.get(i);
						if (item.isSelling()) {
							println("Selling " + item.getAmount() + " "
									+ item.getName() + " for "
									+ item.getPrice() + " each!");
						} else {
							println("Buying " + item.getAmount() + " "
									+ item.getName() + " for "
									+ item.getPrice() + " each!");
						}
					}
					gui_is_up = false;
					g.dispose();
				}
			});
			Start.setFont(new Font("Verdana", Font.BOLD, 12));
			Start.setBounds(153, 511, 179, 29);
			contentPane.add(Start);
		}
	}

}