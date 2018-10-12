package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.colour.ColourPoint;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Mule")
public class UsaMule extends Script implements Painting, MessageListening07 {

	private String version = "1.0";
	private String status = "";

	private long TRADE_TIMER = 0;
	private long START_TIME = 0;

	private int STARTING_INVENTORY_VALUE = 0;
	private int CURRENT_INVENTORY_VALUE = 0;

	private boolean run = true;
	private ABCUtil abc;

	public void run() {

		abc = new ABCUtil();

		START_TIME = System.currentTimeMillis();

		STARTING_INVENTORY_VALUE = getInventoryValue();
		CURRENT_INVENTORY_VALUE = STARTING_INVENTORY_VALUE;

		while (run) {

			if (Camera.getCameraAngle() < 60)
				Camera.setCameraAngle(100);

			if (acceptItems()) {
				status = "Accepted Items";
			} else {
				status = "Waiting for trade request";
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

			sleep(500);

		}

	}

	@Override
	public void onPaint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setRenderingHints(rh);

		long currentTime = System.currentTimeMillis();
		long time = currentTime - START_TIME;
		int profit = CURRENT_INVENTORY_VALUE - STARTING_INVENTORY_VALUE;
		int profitPerHour = (int) (profit * 3600000D / time);

		Color background = new Color(24, 36, 82, 150);
		g2.setColor(background);
		g2.fillRoundRect(235, 322, 261, 100, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(235, 322, 261, 100, 10, 10);

		int x = 240;
		int y = 337;
		int spacing = 15;
		g2.setFont(new Font("Tahoma", Font.BOLD, 12));

		g2.drawString("USA Mule                     v" + version, x + 85, y);
		g2.drawLine(235, 340, 495, 340);
		y += spacing + 3;

		g2.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g2.drawString("Status: " + status, x, y);
		y += spacing;
		g2.drawString(
				"Starting Value: " + addCommasToNumericString(Integer.toString(STARTING_INVENTORY_VALUE)) + " gp.", x,
				y);
		y += spacing;
		g2.drawString("Current Value: " + addCommasToNumericString(Integer.toString(CURRENT_INVENTORY_VALUE)) + " gp.",
				x, y);
		y += spacing;
		g2.drawString("Profit: " + addCommasToNumericString(Integer.toString(profit)) + " gp ("
				+ addCommasToNumericString(Integer.toString(profitPerHour)) + "/hr)", x, y);
		y += spacing;

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

	private int getInventoryValue() {

		status = "Retrieving Inventory Value";

		int total = 0;

		RSItem[] items = Inventory.getAll();

		for (RSItem i : items) {

			int id = i.getID();
			int stack = i.getStack();

			if (id > 0 && stack > 0) {

				total += getPrice(id) * stack;

			}

		}

		return total;
	}

	public int getPrice(final int itemID) {

		try {

			URL u = new URL(
					"http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=" + itemID);
			URLConnection c = u.openConnection();

			BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));

			try {

				String text = r.readLine();
				if (text == null || text.length() == 0)
					return -1;

				String regex = "current\"\\:\\{(.*?)\\}";
				Matcher matcher = Pattern.compile(regex).matcher(text);
				if (matcher.find()) {
					regex = matcher.group().replaceAll("\"|:|}|\\{|,", "").replaceAll("^(.*?)price", "");
					if (regex.contains("m")) {
						return Integer.parseInt(
								String.format("%.0f", Double.parseDouble(regex.replaceAll("[^\\d.]", "")) * 1000000));
					} else if (regex.contains("k")) {
						return Integer.parseInt(
								String.format("%.0f", Double.parseDouble(regex.replaceAll("[^\\d.]", "")) * 1000));
					} else {
						return Integer.parseInt(regex);
					}
				}

			} finally {
				r.close();
			}

		} catch (MalformedURLException e) {
			System.out.println("URLException: No data found for item (ID: " + itemID + ")");
			return 0;
		} catch (IOException e) {
			System.out.println("IOException: No data found for item (ID: " + itemID + ")");
			return 0;
		}

		return 0;
	}

	private boolean acceptItems() {

		if (Trading.getWindowState() == null)
			return false;

		int before = 0;

		for (RSItem item : Inventory.getAll()) {
			before += item.getStack();
		}

		TRADE_TIMER = System.currentTimeMillis() + 180000;

		while (TRADE_TIMER > System.currentTimeMillis()) {

			int after = 0;

			for (RSItem item : Inventory.getAll()) {
				after += item.getStack();
			}

			if (after > before)
				return true;

			WINDOW_STATE window = Trading.getWindowState();

			if (window != null) {

				if (window == Trading.WINDOW_STATE.FIRST_WINDOW) {

					if (Trading.hasAccepted(true)) {

						status = "Accepting First Window";
						Trading.accept();

						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep((long) General.randomSD(750, 200));
								WINDOW_STATE w = Trading.getWindowState();
								return w != null && w == Trading.WINDOW_STATE.SECOND_WINDOW;
							}
						}, 3000);

					}

				} else if (window == Trading.WINDOW_STATE.SECOND_WINDOW) {

					if (!Trading.hasAccepted(false)) {

						status = "Accepting Second Window";
						Trading.accept();

						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep((long) General.randomSD(750, 200));
								WINDOW_STATE w = Trading.getWindowState();
								return w != null && w == Trading.WINDOW_STATE.SECOND_WINDOW;
							}
						}, 3000);

					}

				}

				sleep((long) General.randomSD(1000, 200));

			}

		}

		status = "Closing Trade";
		Trading.close();

		return false;

	}

	private boolean trade(String username) {

		if (Trading.getWindowState() != null)
			return true;

		long timer = System.currentTimeMillis() + 30000;

		while (timer > System.currentTimeMillis()) {

			RSPlayer[] players = Players.getAll(new Filter<RSPlayer>() {

				public boolean accept(RSPlayer p) {

					if (p == null)
						return false;

					if (!p.isOnScreen())
						return false;

					String name = p.getName();
					if (name == null)
						return false;

					if (!username.equalsIgnoreCase(name))
						return false;

					return true;

				}

			});

			if (players.length > 0) {

				status = "Trading " + username;

				if (players[0].click("Trade with " + username)) {

					Timing.waitCondition(new Condition() {
						public boolean active() {
							sleep((long) General.randomSD(750, 200));
							return Trading.getWindowState() != null;
						}
					}, 5000);

					return Trading.getWindowState() != null;

				}

			}

		}

		return false;

	}

	@Override
	public void tradeRequestReceived(String username) {

		status = "Trade Request: " + username;

		trade(username);

	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void playerMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void serverMessageReceived(String arg0) {
	}

}