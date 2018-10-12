package scripts.planker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.Walking;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;

import scripts.api.v1.api.banking.Bank;
import scripts.api.v1.api.entity.UsaNPCS;
import scripts.api.v1.api.generic.Conditional;
import scripts.api.v1.api.walking.Walk;
import scripts.planker.Planks.PLANK;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.trader.Trader;
import scripts.usa.api.web.pricing.Pricing;

@ScriptManifest(authors = { "Usa" }, category = "Money Making", name = "USA Deadman Planker")
public class Planker extends Script implements Painting, MessageListening07 {

	private String version = "3.5";
	private String status = "Starting...";
	private long startTime = 0L;
	private boolean run = true;

	private final RSTile[] VARROCK_EAST_BANK_TO_SAWMILL = new RSTile[] { new RSTile(3254, 3420, 0),
			new RSTile(3254, 3422, 0), new RSTile(3253, 3423, 0), new RSTile(3253, 3425, 0), new RSTile(3253, 3427, 0),
			new RSTile(3256, 3428, 0), new RSTile(3262, 3429, 0), new RSTile(3265, 3429, 0), new RSTile(3269, 3429, 0),
			new RSTile(3272, 3429, 0), new RSTile(3275, 3431, 0), new RSTile(3278, 3435, 0), new RSTile(3280, 3438, 0),
			new RSTile(3282, 3441, 0), new RSTile(3285, 3445, 0), new RSTile(3287, 3450, 0), new RSTile(3290, 3454, 0),
			new RSTile(3291, 3459, 0), new RSTile(3293, 3464, 0), new RSTile(3295, 3469, 0), new RSTile(3297, 3474, 0),
			new RSTile(3299, 3479, 0), new RSTile(3302, 3483, 0), new RSTile(3302, 3488, 0),
			new RSTile(3302, 3491, 0) };

	private final RSArea VARROCK_EAST_BANK_AREA = new RSArea(new RSTile[] { new RSTile(3250, 3424, 0),
			new RSTile(3258, 3424, 0), new RSTile(3258, 3419, 0), new RSTile(3250, 3419, 0) });

	private final RSArea VARROCK_EAST = new RSArea(new RSTile(3246, 3412, 0), new RSTile(3320, 3510, 0));

	private final RSTile VARROCK_EAST_BANK = new RSTile(3254, 3420, 0);

	private final String SAWMILL_OPERATOR = "Sawmill operator";

	private RSTile START_TILE = null;

	private PLANK plank = null;

	private String MULE_USERNAME = "";

	private ArrayList<String> WORKERS = new ArrayList<String>();

	private int startingPlanks = 0, fail = 0, AMOUNT_OF_PLANKS_TO_MAKE = 0, planks = 0, profit = 0, trips = 0,
			deaths = 0, trades = 0;

	private int PLANK_PRICE = 0;
	private int LOG_PRICE = 0;
	private int PROFIT_PER_PLANK = 0;

	private boolean hasSupplies = true;
	private boolean noPlanks = false;

	private boolean isMule;

	@Override
	public void run() {
		
		super.setAIAntibanState(false);

		// loadSettings("USA Planker", "settings");

		// plank = PLANK.OAK;
		// AMOUNT_OF_PLANKS_TO_MAKE = 54;

		while (Login.getLoginState() != Login.STATE.INGAME) {
			status = "Logging in...";
			sleep(200);
		}

		Camera.setCameraAngle(100);

		if (VARROCK_EAST.contains(Player.getPosition())) {

			status = "Walking to Varrock East Bank";
			Walk.walkPath(Walking.invertPath(VARROCK_EAST_BANK_TO_SAWMILL), 1, 3, new Condition() {
				@Override
				public boolean active() {
					return VARROCK_EAST_BANK_AREA.contains(Player.getPosition());
				}
			}, null);

		} else {

			status = "Web Walking to Varrock East Bank";
			Walk.webWalk(VARROCK_EAST_BANK_AREA.getRandomTile(), null, 0);

		}

		if (plank == null) {
			PLANK[] options = PLANK.values();
			int response = JOptionPane.showOptionDialog(null, "Select the Plank", "USA Planker",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			plank = options[response];
		}

		println("Plank set to " + plank.toString());

		int reply = JOptionPane.showConfirmDialog(null, "Is this the mule?", "USA Planker", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION)
			isMule = true;

		RSPlayer[] players = Players.getAll(Filters.Players.inArea(VARROCK_EAST_BANK_AREA)
				.combine(Filters.Players.nameNotEquals(Player.getRSPlayer().getName()), true));

		ArrayList<String> names = new ArrayList<String>();

		for (RSPlayer p : players) {
			if (p != null) {
				String n = p.getName();
				if (n != null)
					names.add(n);
			}
		}

		String[] array = new String[names.size()];
		array = names.toArray(array);

		if (players.length == 0) {

			run = false;

		} else if (isMule) {

			JList<String> list = new JList<String>(array);

			JOptionPane.showMessageDialog(null, list, "Select the Worker(s)", JOptionPane.PLAIN_MESSAGE);

			Object[] selected = list.getSelectedValues();

			for (Object s : selected) {
				println("Worker: " + s.toString());
				WORKERS.add(s.toString());
			}

			if (AMOUNT_OF_PLANKS_TO_MAKE == 0) {
				SpinnerNumberModel sModel = new SpinnerNumberModel(1000, 0, Integer.MAX_VALUE, 100);
				JSpinner spinner = new JSpinner(sModel);
				JOptionPane.showOptionDialog(null, spinner, "How many logs should we trade?",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				AMOUNT_OF_PLANKS_TO_MAKE = (int) spinner.getValue();
			}

			START_TILE = Player.getPosition();
			println("We are the Mule! Starting tile set to: " + START_TILE);
			startingPlanks = Inventory.getCount(plank.getPlankName());
			println("Starting amount of " + plank.getPlankName() + " is " + startingPlanks);
			println("We will trade supplies for " + AMOUNT_OF_PLANKS_TO_MAKE + " " + plank.getPlankName());

		} else {

			int response = JOptionPane.showOptionDialog(null, "Select the Mule", "USA Planker",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, array, array[0]);

			println(array[response] + " set as mule.");

			MULE_USERNAME = array[response];

		}

		startTime = System.currentTimeMillis();
		PLANK_PRICE = Pricing.getPrice(plank.getPlankName(), plank.getPlankID());
		LOG_PRICE = Pricing.getPrice(plank.getLogName(), plank.getLogID());
		PROFIT_PER_PLANK = PLANK_PRICE - (LOG_PRICE + plank.getCostPerLog());

		println(plank.getPlankName() + " has a value of " + PLANK_PRICE + " gp and " + plank.getLogName()
				+ " has a value of " + LOG_PRICE + " gp, costing " + plank.getCostPerLog()
				+ " per log to convert, for a profit margin of " + PROFIT_PER_PLANK + " gp per log.");

		while (run) {

			if (ABC.activateRun())
				status = "Activated Run";

			if (!isMule) {

				if (!VARROCK_EAST.contains(Player.getPosition())) {

					status = "Web Walking to Varrock East";
					Walk.webWalk(VARROCK_EAST_BANK, null, 0);

				} else {

					if (hasSupplies) {

						if (hasNotedLogs(plank) || Inventory.getCount(plank.getPlankName()) > 0
								|| Inventory.getCount(plank.getLogName()) < 27
								|| Inventory.getCount("Coins") < (27 * plank.getCostPerLog())
								|| Inventory.getCount("Coins") > (27 * plank.getCostPerLog())) {

							if (!bank()) {

								fail++;

								if (fail >= 3) {
									println("We are out of supplies!");
									hasSupplies = false;
								}

							} else {

								fail = 0;

							}

						} else {

							fail = 0;

							if (!nearSawmillOperator()) {

								status = "Walking to Sawmill Operator";

								Walk.walkPath(VARROCK_EAST_BANK_TO_SAWMILL, 1, 3, new Condition() {

									@Override
									public boolean active() {
										return nearSawmillOperator();
									}
								}, null);

							} else {

								buyPlank(plank);

							}

						}

					} else {

						if (Trader.isTradeOpen() || hasNotedPlanks(plank) || noPlanks) {

							if (Bank.close()) {

								if (tradeMule()) {

									status = "We have supplies!";
									println(status);

									hasSupplies = true;
									noPlanks = false;
									fail = 0;

								}

							}

						} else {

							status = "Noting planks";

							if (openBank()) {

								if (Inventory.getAll().length != 0)
									Banking.depositAll();

								sleep((long) General.randomSD(500, 200));

								Timing.waitCondition(new Condition() {

									public boolean active() {
										sleep(100);
										return Inventory.getAll().length == 0;
									}
								}, 2000);

								if (Inventory.getAll().length == 0) {

									if (Bank.setNoteSelected(true)) {

										if (!Bank.withdraw(0, plank.getPlankName())) {

											status = "Failed withdrawing " + plank.getPlankName();
											println(status);
											fail++;

											if (fail >= 3)
												noPlanks = true;

										} else {

											fail = 0;

										}

									}

								}

							}

						}

					}

				}

			} else {

				if (Bank.close()) {

					if (!Trader.isTradeOpen() && Inventory.getCount("Coins") < (plank.getCostPerLog() * 27)
							|| Inventory.getCount(plank.getLogName()) < 27) {

						status = "Not enough supplies";
						println("We are out of supplies!");
						run = false;
						Login.logout();

					} else {

						if (Player.getPosition().distanceTo(START_TILE) > 0) {

							status = "Walking back to Start Tile";
							Walk.walkToTile(START_TILE, 0, 0, null);

						}

						planks = Inventory.getCount(plank.getPlankName()) - startingPlanks;

						profit = planks * PROFIT_PER_PLANK;

					}

				}

			}

			if (ABC.performAntiban())
				status = "ABC2 Antiban";

			sleep(50);

		}

	}

	private boolean tradeWorker(String username) {

		status = "Trading " + username;

		if (Trader.openTrade(username, false)) {

			long timer = System.currentTimeMillis() + General.random(50000, 70000);

			while (timer > System.currentTimeMillis()) {

				WINDOW_STATE window = Trading.getWindowState();

				if (window == WINDOW_STATE.FIRST_WINDOW) {

					if (!Trader.isItemOffered(false, plank.getLogName(), AMOUNT_OF_PLANKS_TO_MAKE)) {

						status = "Offering " + plank.getLogName();
						Trader.offer(plank.getLogName(), AMOUNT_OF_PLANKS_TO_MAKE);
						timer = System.currentTimeMillis() + General.randomSD(10000, 2000);

					} else if (!Trader.isItemOffered(false, "Coins",
							plank.getCostPerLog() * AMOUNT_OF_PLANKS_TO_MAKE)) {

						status = "Offering Coins";
						Trader.offer("Coins", plank.getCostPerLog() * AMOUNT_OF_PLANKS_TO_MAKE);
						timer = System.currentTimeMillis() + General.randomSD(10000, 2000);

					} else {

						if (!Trading.hasAccepted(false))
							status = "Accepting First Window";

						if (Trader.acceptTrade(false)) {
							status = "Accepted First Window";
							timer = System.currentTimeMillis() + General.randomSD(10000, 2000);
						}

					}

				} else if (window == WINDOW_STATE.SECOND_WINDOW) {

					if (!Trading.hasAccepted(false))
						status = "Accepting Second Window";

					if (Trader.acceptTrade(false)) {
						status = "Accepted Second Window";
						return true;
					}

				} else {

					status = "Trade Closed!";
					return true;

				}

			}

		}

		if (Trader.isTradeOpen()) {
			status = "Closing Trade!";
			Trading.close();
		}

		return false;

	}

	private boolean tradeMule() {

		if (!Trader.isTradeOpen()) {
			status = "Trading " + MULE_USERNAME;
		} else {
			status = "Searching for " + MULE_USERNAME;
		}

		if (Trader.openTrade(MULE_USERNAME, true)) {

			if (Inventory.getCount(plank.getPlankName()) > 0) {

				status = "Offering " + plank.getPlankName();

				Trader.offer(plank.getPlankName(), 0);

			} else {

				status = "Accepting Trade";

				if (Trader.acceptTrade(true)) {

					status = "Trade Complete";

					if (Inventory.getCount(plank.getPlankName()) == 0 && Inventory.getCount("Coins") > 0
							&& Inventory.getCount(plank.getLogName()) > 0) {

						return true;

					}

				}

			}

		}

		return false;

	}

	private boolean bank() {

		if (openBank()) {

			if (hasNotedLogs(plank)) {

				status = "Depositing Noted Logs";
				Bank.deposit(0, plank.getLogName());

			}

			if (Inventory.getCount(plank.getPlankName()) > 0) {

				status = "Depositing Planks";

				planks += Inventory.getCount(plank.getPlankName());

				if (Inventory.getAll().length != 0)
					Banking.depositAll();

			}

			if (Inventory.getCount("Coins") < (27 * plank.getCostPerLog())) {

				if (Inventory.isFull())
					Banking.depositAll();

				status = "Withdrawing Coins";

				if (!Bank.withdraw(27 * plank.getCostPerLog(), "Coins")) {
					status = "Failed withdrawing Coins.";
					println(status);
					sleep(General.random(500, 100));
					return false;
				}

			}

			if (Inventory.getCount(plank.getLogName()) < 27) {

				status = "Withdrawing " + plank.getLogName();

				if (!Bank.withdraw(27, plank.getLogName())) {
					status = "Failed withdrawing " + plank.getLogName() + ".";
					println(status);
					sleep(General.random(500, 100));
					return false;
				}

			}

			if (Inventory.getCount("Coins") > (27 * plank.getCostPerLog())) {

				status = "Depositing Extra Coins";

				Bank.deposit(Inventory.getCount("Coins") - (27 * plank.getCostPerLog()), "Coins");

			}

			sleep(General.random(500, 100));

		}

		return true;

	}

	private void loadSettings(String directory, String name) {

		try {

			File folder = new File(Util.getWorkingDirectory() + "/" + directory);
			File file = new File(folder.toString() + "/" + name + ".txt");

			if (file.exists()) {

				println("Loading settings...");

				Properties prop = new Properties();
				prop.load(new FileInputStream(file));

				MULE_USERNAME = prop.getProperty("mule").trim();

				println("Mule: \"" + MULE_USERNAME + "\"");

				String str = prop.getProperty("workers");

				String[] split = str.split(",");

				for (String s : split) {

					println("Worker: \"" + s + "\"");
					WORKERS.add(s.trim());

				}

				plank = PLANK.valueOf(prop.getProperty("plank"));

				println("Plank set to: " + plank);

			} else {

				println("Unable to find file named " + name + ".txt in " + directory);

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	private boolean openBank() {

		if (!VARROCK_EAST_BANK_AREA.contains(Player.getPosition())) {

			if (VARROCK_EAST.contains(Player.getPosition())) {

				status = "Walking to Varrock East Bank";

				Walk.walkPath(Walking.invertPath(VARROCK_EAST_BANK_TO_SAWMILL), 1, 3, new Condition() {
					@Override
					public boolean active() {
						return VARROCK_EAST_BANK_AREA.contains(Player.getPosition());
					}
				}, null);

			} else {

				status = "Web Walking to Varrock East Bank";
				Walk.webWalk(VARROCK_EAST_BANK_AREA.getRandomTile(), null, 0);

			}

		} else {

			if (Banking.isBankScreenOpen())
				return true;

			status = "Opening Bank";

			if (Bank.open())
				status = "Bank is Open";

		}

		return Bank.isOpen();

	}

	private boolean hasNotedLogs(PLANK plank) {

		RSItem[] item = Inventory.find(plank.getLogName());

		if (item.length == 0)
			return false;

		RSItemDefinition d = item[0].getDefinition();

		if (d == null)
			return false;

		return d.isNoted();

	}

	private boolean hasNotedPlanks(PLANK plank) {

		RSItem[] item = Inventory.find(plank.getPlankName());

		if (item.length == 0)
			return false;

		RSItemDefinition d = item[0].getDefinition();

		if (d == null)
			return false;

		return d.isNoted();

	}

	private boolean nearSawmillOperator() {

		RSNPC[] npc = NPCs.find(SAWMILL_OPERATOR);

		if (npc.length == 0)
			return false;

		return Player.getPosition().distanceTo(npc[0]) < 5;

	}

	private boolean buyPlank(PLANK plank) {

		if (plank == null || Inventory.getCount(plank.getLogName()) == 0
				|| Inventory.getCount("Coins") < plank.getCostPerLog())
			return false;

		if (!Interfaces.isInterfaceValid(plank.getMaster())) {

			RSNPC[] npc = NPCs.find(SAWMILL_OPERATOR);

			if (npc.length == 0)
				return false;

			status = "Clicking Sawmill Operator";

			if (UsaNPCS.click("Buy-plank", npc)) {

				Conditional.sleep(new Condition() {
					public boolean active() {
						return Player.isMoving();
					}
				}, new Condition() {
					public boolean active() {
						return Interfaces.isInterfaceValid(plank.getMaster());
					}
				}, 2000);

			}

		}

		if (Interfaces.isInterfaceValid(plank.getMaster())) {

			RSInterfaceChild LOG_INTERFACE = Interfaces.get(plank.getMaster(), plank.getInterface());

			if (LOG_INTERFACE == null)
				return false;

			status = "Buying All";

			sleep(General.randomSD(750, 250));

			if (LOG_INTERFACE.click("Buy All")) {

				trips++;

				Timing.waitCondition(new Condition() {

					public boolean active() {
						sleep(100);
						return Inventory.getCount(plank.getLogName()) == 0
								|| Inventory.getCount("Coins") < plank.getCostPerLog();
					}

				}, 2000);

			}

		}

		return false;
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

	@Override
	public void onPaint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setRenderingHints(rh);

		long currentTime = System.currentTimeMillis();
		long time = currentTime - startTime;
		int planksPerHour = (int) (planks * 3600000D / time);
		int profitPerHour = (int) (profit * 3600000D / time);
		int tripsPerHour = (int) (trips * 3600000D / time);
		int deathsPerHour = (int) (deaths * 3600000D / time);
		int tradesPerHour = (int) (trades * 3600000D / time);

		if (Walk.getPaintPath() != null) {

			for (RSTile t : Walk.getPaintPath()) {

				if (Walk.isTileOnMinimap(t)) {

					Point pt = Projection.tileToMinimap(t);

					if (pt != null) {

						g2.setColor(new Color(50, 200, 50, 255));

						g2.fillRect(pt.x, pt.y, 2, 2);

					}

					if (t.isOnScreen()) {

						Polygon p = Projection.getTileBoundsPoly(t, 0);

						if (p != null) {

							g2.setColor(new Color(50, 200, 50, 50));

							g2.fillPolygon(Projection.getTileBoundsPoly(t, 0));

							g2.setColor(new Color(50, 200, 50, 255));

							g2.drawPolygon(Projection.getTileBoundsPoly(t, 0));

						}

					}

				}

			}

		}

		Color background = new Color(24, 36, 82, 100);
		g2.setColor(background);
		g2.fillRoundRect(235, 322, 261, 150, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(235, 322, 261, 150, 10, 10);

		int x = 240;
		int y = 336;
		int spacing = 17;
		g2.setFont(new Font("Tahoma", Font.BOLD, 12));

		g2.drawString("USA Planker                v" + version, x + 85, y);
		g2.drawLine(235, 340, 495, 340);
		y += spacing + 3;

		g2.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g2.drawString("Status: " + status, x, y);
		y += spacing;
		g2.drawString("Planks: " + addCommasToNumericString(Integer.toString(planks)) + " ("
				+ addCommasToNumericString(Integer.toString(planksPerHour)) + "/hr)", x, y);
		y += spacing;

		if (!isMule) {

			g2.drawString("Trips: " + addCommasToNumericString(Integer.toString(trips)) + " ("
					+ addCommasToNumericString(Integer.toString(tripsPerHour)) + "/hr)", x, y);
			y += spacing;
			g2.drawString("Deaths: " + addCommasToNumericString(Integer.toString(deaths)) + " ("
					+ addCommasToNumericString(Integer.toString(deathsPerHour)) + "/hr)", x, y);
			y += spacing;

		} else {

			g2.drawString("Trades: " + addCommasToNumericString(Integer.toString(trades)) + " ("
					+ addCommasToNumericString(Integer.toString(tradesPerHour)) + "/hr)", x, y);
			y += spacing;
			g2.drawString("Profit: " + addCommasToNumericString(Integer.toString(profit)) + " ("
					+ addCommasToNumericString(Integer.toString(profitPerHour)) + "/hr)", x, y);
			y += spacing;

		}

	}

	@Override
	public void tradeRequestReceived(String username) {

		if (!Trader.isTradeOpen() && isMule
				&& Inventory.getCount("Coins") >= (plank.getCostPerLog() * AMOUNT_OF_PLANKS_TO_MAKE)
				&& Inventory.getCount(plank.getLogName()) >= AMOUNT_OF_PLANKS_TO_MAKE) {

			status = "Traded by \"" + username + "\"";

			if (WORKERS.contains(username)) {

				status = "Trading worker \"" + username + "\"";

				if (tradeWorker(username)) {

					status = "Traded \"" + username + "\"";

					trades++;

				}

			}

		}

	}

	@Override
	public void serverMessageReceived(String message) {

		if (message.contains("Oh dear, you are dead!")) {

			println(message);
			deaths++;

			if (isMule) {

				println("We are the mule and we have died, shutting down.");
				Login.logout();
				run = false;

			}

		}

	}

	@Override
	public void clanMessageReceived(String username, String message) {
	}

	@Override
	public void duelRequestReceived(String username, String message) {
	}

	@Override
	public void personalMessageReceived(String username, String message) {
	}

	@Override
	public void playerMessageReceived(String username, String message) {
	}

}
