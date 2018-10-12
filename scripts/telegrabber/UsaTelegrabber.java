package scripts.telegrabber;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Magic;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;

import javafx.scene.control.Tab;
import scripts.api.v1.api.banking.Bank;
import scripts.api.v1.api.viewport.AsynchronousCamera;
import scripts.api.v1.api.walking.Walk;
import scripts.api.v1.api.wilderness.Wilderness;
import scripts.api.v1.api.worlds.WorldHopper;
import scripts.usa.api.equipment.Equipment;
import scripts.usa.api.observers.inventory.InventoryObserver;
import scripts.usa.api.observers.inventory.InventoryObserver.InventoryChange;
import scripts.usa.api.observers.inventory.InventoryObserver.InventoryListener;
import scripts.usa.api.tracking.Tracker;
import scripts.usa.api.ui.Paint;
import scripts.usa.api.web.pricing.Pricing;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Zamorak Telegrabber")
public class UsaTelegrabber extends Script
	implements Painting, Ending, MessageListening07, MouseActions, InventoryListener {

    private String status = "Starting";

    private final String LAW_RUNE = "Law rune";
    private final int LAW_RUNE_ID = 563;
    private Positionable itemPosition = new RSTile(2931, 3515, 0);
    private String itemName = "Wine of zamorak";
    private int itemId = 245;
    private int itemValue = 0;

    private int items = 0;
    private int casts = 0;
    private int worldChanges = 0;

    private int lawRuneValue;

    private RSModel model;

    private boolean run = true;
    private boolean threat;
    private ThreatSearch search;
    private Thread searching;
    private AsynchronousCamera aCamera = new AsynchronousCamera();
    private Tracker tracker = new Tracker();

    @Override
    public void run() {

	itemValue = Pricing.getPrice(itemName, itemId);
	lawRuneValue = Pricing.getPrice(LAW_RUNE, 563);

	// System.out.println("Starting Threat Searching thread.");
	// search = new ThreatSearch();
	// searching = new Thread(search);
	// searching.start();

	super.setAIAntibanState(false);

	InventoryObserver inventoryObserver = new InventoryObserver(new Condition() {
	    public boolean active() {
		return !Banking.isBankScreenOpen();
	    }
	});
	inventoryObserver.addListener(this);
	inventoryObserver.start();

	Camera.setCameraAngle(100);

	while (run) {
	    if (Login.getLoginState() == Login.STATE.INGAME) {
		if (shouldBank()) {
		    bank();
		} else {
		    if (Player.getPosition().distanceTo(itemPosition) > 1) {
			status = "Walking to " + itemName;
			Walk.walkToTile(itemPosition);
		    } else {
			RSGroundItem[] groundItems = GroundItems.findNearest(itemName);
			if (groundItems.length > 0) {
			    if (castTelekenticGrab(groundItems)) {
				status = "Casted Telekinetic Grab";
			    }
			} else {
			    if (!isSpellSelected("Telekinetic Grab")) {
				selectSpell("Telekinetic Grab");
			    } else {
				if (hover(model)) {
				    status = "Hovering over " + itemName;
				} else {
				    status = "Waiting for " + itemName;
				}
			    }
			}
		    }
		}
	    }
	}
    }

    private boolean isSpellSelected(String name) {
	return Magic.isSpellSelected() && Magic.getSelectedSpellName().equalsIgnoreCase(name);
    }

    private boolean isWrongSpellSelected(String name) {
	return Magic.isSpellSelected() && !Magic.getSelectedSpellName().equalsIgnoreCase(name);
    }

    private boolean selectSpell(String name) {
	if (isWrongSpellSelected(name)) {
	    final Rectangle MAGIC_GAME_TAB = new Rectangle(727, 171, 30, 30);
	    Mouse.clickBox(MAGIC_GAME_TAB, 1);
	}
	if (!isSpellSelected(name)) {
	    if (Magic.selectSpell(name)) {
		Timing.waitCondition(new Condition() {
		    public boolean active() {
			return isSpellSelected(name);
		    }
		}, 2000);
	    }
	}
	return isSpellSelected(name);
    }

    private boolean isHovering(RSModel model) {
	if (model == null)
	    return false;
	Polygon area = model.getEnclosedArea();
	if (area == null)
	    return false;
	return area.contains(Mouse.getPos());
    }

    private boolean hover(RSModel model) {
	if (model == null)
	    return false;
	if (isHovering(model))
	    return true;
	return model.hover();
    }

    private boolean castTelekenticGrab(RSGroundItem[] groundItems) {
	if (groundItems.length == 0)
	    return false;

	RSGroundItem item = groundItems[0];
	if (item == null)
	    return false;

	status = "Found " + itemName;
	model = item.getModel();

	if (!isSpellSelected("Telekinetic Grab")) {
	    if (selectSpell("Telekinetic Grab")) {
		status = "Selecting Telekinetic Grab";
	    }
	}

	final int runeCount = Inventory.getCount(LAW_RUNE);
	final int itemCount = Inventory.getCount(itemName);

	if (isSpellSelected("Telekinetic Grab")) {
	    status = "Casting Telekinetic Grab";
	    if (hover(model)) {
		Mouse.click(1);
		Timing.waitCondition(new Condition() {
		    public boolean active() {
			sleep(100);
			return (runeCount != Inventory.getCount(LAW_RUNE) && itemCount != Inventory.getCount(itemName)
				&& Player.getAnimation() == -1);
		    }
		}, General.randomSD(4000, 100));
	    }
	}

	return true;
    }

    private boolean shouldBank() {
	if (Login.getLoginState() == Login.STATE.INGAME) {
	    if (Inventory.isFull()) {
		println("Inventory is full");
		return true;
	    }
	}
	return false;
    }

    private boolean bank() {
	if (!Banking.isInBank()) {
	    status = "Walk to Bank";
	    Walk.walkToBank();
	} else {
	    status = "Opening Bank";
	    if (Bank.open()) {
		status = "Depositing " + itemName;
		Banking.deposit(0, itemName);
	    }
	}
	return true;
    }

    private boolean changeWorlds() {
	int next = WorldHopper.getRandomWorld(true);
	status = "Changing to world " + next;
	int current = WorldHopper.getCurrentWorld();
	while (current == WorldHopper.getCurrentWorld()) {
	    if (WorldHopper.changeWorld(next)) {
		status = "Changed from " + current + " to " + next;
		worldChanges++;
		return true;
	    }
	}
	return false;
    }

    @Override
    public void onPaint(Graphics g) {
	try {
	    Graphics2D g2 = (Graphics2D) g;
	    RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
		    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setRenderingHints(rh);
	    g2.setColor(new Color(255, 0, 0, 150));

	    g2.setFont(new Font("Consolas", Font.PLAIN, 12));
	    Color background = new Color(120, 27, 10, 150);
	    Color border = new Color(0, 0, 0, 255);
	    Color text = Color.WHITE;
	    int height = 15;
	    int width = 300;
	    int spacing = 20;
	    int x = 10;
	    int y = 50;
	    ArrayList<String> list = new ArrayList<String>();

	    double successPercent = ((double) items / (double) (casts > 0 ? casts : 1)) * 100;
	    DecimalFormat df = new DecimalFormat("0.0");
	    int itemsPerHour = (int) (items * 3600000D / tracker.getElapsedTime());
	    int castsPerHour = (int) (casts * 3600000D / tracker.getElapsedTime());
	    int profit = (items * itemValue) - (casts * lawRuneValue);
	    int profitPerHour = (int) (profit * 3600000D / tracker.getElapsedTime());

	    list.add("USA Zamorak Telegrabber");
	    list.add("Time: " + Timing.msToString(tracker.getElapsedTime()));
	    list.add("Status: " + status);
	    list.add("Success rate: " + df.format(successPercent) + " %");
	    list.add("Telekinetic grabs: " + TabletsPaint.format(casts) + " (" + TabletsPaint.format(castsPerHour) + "/hr)");
	    list.add(itemName + "s: " + TabletsPaint.format(items) + " (" + TabletsPaint.format(itemsPerHour) + "/hr)");
	    list.add("Profit: " + TabletsPaint.format(profit) + " gp (" + TabletsPaint.format(profitPerHour) + "/hr)");
	    list.add("Magic level: " + Skills.getCurrentLevel(SKILLS.MAGIC) + " (+" + tracker.getLevelsGained() + ")");
	    list.add("XP: " + tracker.getXPGained() + " (" + tracker.getXPPerHour() + "/hr)");
	    // list.add("World changes: " + worldChanges);

	    for (String item : list) {
		g2.setColor(background);
		g2.fillRoundRect(x, y - height, width, height, 4, 4);
		g2.setColor(border);
		g2.drawRoundRect(x, y - height, width, height, 4, 4);
		g2.setColor(text);
		g2.drawString(item, x + 3, y - 3);
		y += spacing;
	    }
	} catch (Exception e) {
	    System.out.println(e);
	}
    }

    public class ThreatSearch implements Runnable {

	private volatile boolean running = true;
	private long screenShotTime = 0;

	@Override
	public void run() {
	    while (!running) {
		try {
		    if (threat || Login.getLoginState() != Login.STATE.INGAME || Player.getRSPlayer() == null
			    || Player.getRSPlayer().isInCombat())
			continue;
		    String playerName = Player.getRSPlayer().getName();
		    int playerLevel = Player.getRSPlayer().getCombatLevel();
		    if (playerName == null || playerLevel == 0)
			continue;
		    if (Skills.getCurrentLevel(SKILLS.HITPOINTS) == 0 && screenShotTime < System.currentTimeMillis()) {
			try {
			    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh-mm-ss a");
			    Calendar cal = Calendar.getInstance();
			    String date = dateFormat.format(cal.getTime());
			    File folder = new File(Util.getWorkingDirectory() + "/USA Telegrabber/deaths");
			    if (!folder.exists())
				folder.mkdir();
			    ImageIO.write(Screen.getGameImage(), "png",
				    new File(Util.getWorkingDirectory() + "/USA Lava Dragons/deaths",
					    playerName + " " + date + ".png"));
			    screenShotTime = System.currentTimeMillis() + 5000;
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		    int wilderness = Wilderness.getLevel();
		    if (wilderness > 0) {
			RSPlayer[] players = Players
				.getAll(Filters.Players.nameNotEquals(playerName).combine(new Filter<RSPlayer>() {
				    public boolean accept(RSPlayer player) {
					if (player.getCombatLevel() < (playerLevel - wilderness))
					    return false;
					if (player.getCombatLevel() > (playerLevel + wilderness))
					    return false;
					return true;
				    }
				}, false));
			if (players.length > 0) {
			    RSPlayer player = players[0];
			    println("\"" + player.getName() + "\" (" + "Level " + player.getCombatLevel() + ") is "
				    + Player.getPosition().distanceTo(player) + " tile(s) away in wilderness level "
				    + wilderness + ".");
			    println(Equipment.getEquipmentOf(player));
			    threat = true;
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		sleep(50);
	    }
	}

	public void stop() {
	    System.out.println("Stopped Threat Searching thread.");
	    this.running = false;
	}

    }

    @Override
    public void onEnd() {
	search.stop();
    }

    @Override
    public void mouseClicked(Point arg0, int arg1, boolean arg2) {
	// TODO Auto-generated method stub

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
	if (message.contains("Oh dear, you are dead!")) {
	    General.println(message);
	}
    }

    @Override
    public void tradeRequestReceived(String arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onInventoryChange(InventoryChange change, int id, int count) {
	if (change == InventoryChange.INCREASE && id == itemId) {
	    items += count;
	}
	if (change == InventoryChange.DECREASE && id == LAW_RUNE_ID) {
	    casts++;
	}
    }
}
