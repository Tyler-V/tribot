package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.rs3.Camera;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "Usa Wood Chopper")
public class UsaWoodChopper extends Script implements Painting, Ending, MouseActions, MessageListening07 {

    String version = "1.4";
    private final Image paint_image = getImage("http://i.imgur.com/XrRYzud.png");
    private final Image master_image = getImage("http://i.imgur.com/Yav0I3t.png");
    //
    private final Image tree_image = getImage("http://i.imgur.com/mFBn35M.png");
    //
    private final Image drop_image = getImage("http://i.imgur.com/wOELkAj.png");
    private final Image bank_image = getImage("http://i.imgur.com/LNOqy0v.png");
    private final Image sell_image = getImage("http://i.imgur.com/kE7mTAS.png");
    //
    private final Image log_image = getImage("http://i.imgur.com/p1iJPTT.png");
    private final Image oak_image = getImage("http://i.imgur.com/mNvKOW4.png");
    private final Image willow_image = getImage("http://i.imgur.com/UdJ7URW.png");
    private final Image teak_image = getImage("http://i.imgur.com/kgIuFPw.png");
    private final Image maple_image = getImage("http://i.imgur.com/p0Lj73p.png");
    private final Image yew_image = getImage("http://i.imgur.com/n2GL6eQ.png");
    private final Image magic_image = getImage("http://i.imgur.com/s161TqH.png");
    private int logCount = 0;
    private int oakCount = 0;
    private int willowCount = 0;
    private int teakCount = 0;
    private int mapleCount = 0;
    private int yewCount = 0;
    private int magicCount = 0;
    //
    private final Image transfer_items = getImage("http://i.imgur.com/xt7bxkJ.png");
    private final Image transferring_items = getImage("http://i.imgur.com/DEPvPf5.png");
    //

    //
    private ArrayList<TREE_LOCATIONS> ORDER_LIST = new ArrayList<TREE_LOCATIONS>();
    private TREE_LOCATIONS CURRENT_LOCATION = null;
    private String[] LOGS_TO_BANK;
    //

    // MASTER
    private boolean masterOption = false;
    private String masterUsername;
    private int masterWorld;
    private RSTile masterLocation;
    private boolean isMaster = false;
    private boolean tradeSlave = false;
    private int startLogs = 0;
    private int tradesComplete = 0;
    //
    private String[] slaveUsernames;
    private boolean automaticTrading = false;
    private int automaticLogCount;
    private int startWorld = 0;
    private boolean originalWorld = false;
    private boolean transfer = false;
    // CUSTOM
    private boolean customOption = false;
    // DEPOSIT OPTIONS
    private boolean banking = false; // GUI OPTIONS FOR WHICH LOGS
    private boolean dropping = false; // GUI OPTIONS FOR WHICH LOGS
    private boolean selling = false; // GUI OPTION FOR WHICH LOGS
    // AQUIRE STEEL AXE?
    private boolean getSteelAxe = false;
    private boolean dropGold = false;
    // WIELD HIGHEST LEVEL AXE
    private boolean wieldAxe = false;
    // NESTS
    private boolean nest = false;
    private boolean openNest = false;
    //

    ArrayList<String> ITEMS = new ArrayList<String>();
    private String[] ITEMS_TO_KEEP = null;

    RSTile[] EAGLE_NEST_BANKING = new RSTile[] { new RSTile(2341, 3512, 0), new RSTile(2350, 3511, 0),
	    new RSTile(2359, 3505, 0), new RSTile(2364, 3498, 0), new RSTile(2365, 3487, 0), new RSTile(2371, 3478, 0),
	    new RSTile(2374, 3468, 0), new RSTile(2373, 3458, 0), new RSTile(2372, 3448, 0), new RSTile(2369, 3438, 0),
	    new RSTile(2367, 3428, 0), new RSTile(2370, 3419, 0), new RSTile(2374, 3411, 0), new RSTile(2383, 3406, 0),
	    new RSTile(2393, 3405, 0), new RSTile(2387, 3393, 0), new RSTile(2398, 3391, 0), new RSTile(2407, 3390, 0),
	    new RSTile(2418, 3388, 0), new RSTile(2429, 3383, 0), new RSTile(2439, 3382, 0), new RSTile(2450, 3382, 0),
	    new RSTile(2461, 3382, 0) };

    RSTile[] EAGLE_NEST_TREES = new RSTile[] { new RSTile(2364, 3472, 0), new RSTile(2374, 3468, 0),
	    new RSTile(2373, 3458, 0), new RSTile(2372, 3448, 0), new RSTile(2369, 3438, 0), new RSTile(2367, 3428, 0),
	    new RSTile(2370, 3419, 0), new RSTile(2374, 3411, 0), new RSTile(2383, 3406, 0), new RSTile(2393, 3405, 0),
	    new RSTile(2387, 3393, 0), new RSTile(2398, 3391, 0), new RSTile(2407, 3390, 0), new RSTile(2418, 3388, 0),
	    new RSTile(2429, 3383, 0), new RSTile(2439, 3382, 0), new RSTile(2450, 3382, 0),
	    new RSTile(2461, 3382, 0) };

    RSArea TREE_GNOME_STRONGHOLD = new RSArea(new RSTile[] { new RSTile(2463, 3384, 0), new RSTile(2465, 3386, 0),
	    new RSTile(2466, 3390, 0), new RSTile(2465, 3405, 0), new RSTile(2462, 3420, 0), new RSTile(2458, 3432, 0),
	    new RSTile(2453, 3440, 0), new RSTile(2444, 3440, 0), new RSTile(2438, 3432, 0), new RSTile(2439, 3424, 0),
	    new RSTile(2442, 3414, 0), new RSTile(2451, 3402, 0), new RSTile(2454, 3395, 0), new RSTile(2454, 3391, 0),
	    new RSTile(2457, 3388, 0), new RSTile(2457, 3386, 0), new RSTile(2459, 3384, 0), new RSTile(2463, 3384, 0),
	    new RSTile(2465, 3387, 0) });

    RSArea TREE_GNOME_BANK = new RSArea(new RSTile(2445, 3415, 1), new RSTile(2446, 3434, 1));

    private final String[] LOGS = new String[] { "Logs", "Oak logs", "Willow logs", "Maple logs", "Teak logs",
	    "Yew logs", "Magic logs" };

    int startLVL = 0;
    int startXP = 0;
    int logs = 0;

    private RSTile lastTile = null;
    private RSTile currentTile = null;
    private RSTile nextTile = null;

    private RSObject current = null;
    private RSObject next = null;
    private RSTile[] path = null;

    int count = 0;

    private ABCUtil abc;
    private boolean hover_next = false;
    private boolean go_to_anticipated = false;
    private boolean use_closest = false;
    private boolean waited_delay_time = false;
    private long last_busy_time = 0L;

    private String status = "Starting";
    private boolean run = true;
    private long startTime;

    private boolean gui_is_up = true;
    gui g = new gui();

    public void run() {

	g.setVisible(true);
	while (gui_is_up) {
	    sleep(50);
	}

	ITEMS.add("Bronze axe");
	ITEMS.add("Iron axe");
	ITEMS.add("Steel axe");
	ITEMS.add("Black axe");
	ITEMS.add("Mithril axe");
	ITEMS.add("Adamant axe");
	ITEMS.add("Rune axe");
	ITEMS.add("Coins");

	ITEMS_TO_KEEP = new String[ITEMS.size()];
	ITEMS_TO_KEEP = ITEMS.toArray(ITEMS_TO_KEEP);

	abc = new ABCUtil();
	startTime = Timing.currentTimeMillis();
	Camera.setPitch(true);

	startLVL = Skills.getActualLevel(SKILLS.WOODCUTTING);
	startXP = Skills.getXP(SKILLS.WOODCUTTING);

	if (!isMaster && Player.getRSPlayer().getName().equalsIgnoreCase(masterUsername)) {
	    isMaster = true;
	}

	if (isMaster) {
	    RSItem[] all = Inventory.find(LOGS);
	    if (all.length > 0) {
		int count = 0;
		for (RSItem log : all) {
		    count += log.getStack();
		}
		startLogs = count;
	    }
	}

	startWorld = WorldHopper.getWorld();
	println("Start World: " + startWorld);

	while (run) {

	    if (Login.getLoginState().equals(Login.STATE.INGAME)) {

		if (isMaster) {
		    status = "Master Account";
		    Inventory.open();
		    if (tradeSlave) {
			receiveItemsFromPlayers(slaveUsernames, masterLocation);
		    }
		    RSItem[] all = Inventory.find(LOGS);
		    if (all.length > 0) {
			int count = 0;
			for (RSItem log : all) {
			    count += log.getStack();
			}
			logs = count;
		    }
		} else {
		    if (automaticTrading) {
			if (logCount >= automaticLogCount || oakCount >= automaticLogCount
				|| willowCount >= automaticLogCount || teakCount >= automaticLogCount
				|| mapleCount >= automaticLogCount || yewCount >= automaticLogCount
				|| magicCount >= automaticLogCount) {
			    transfer = true;
			}
		    }

		    while (transfer) {
			if (!hasNotedLogs() && Trading.getWindowState() == null) {
			    if (withdrawValuables(LOGS))
				Banking.close();
			} else {
			    if (WorldHopper.getWorld() != masterWorld) {
				status = "Hopping to " + masterWorld;
				WorldHopper.changeWorld(masterWorld);
			    } else {
				if (giveItemsToPlayer(masterUsername, masterLocation, LOGS)) {
				    logCount = 0;
				    willowCount = 0;
				    oakCount = 0;
				    mapleCount = 0;
				    teakCount = 0;
				    yewCount = 0;
				    magicCount = 0;
				    transfer = false;
				    if (!originalWorld)
					startWorld = WorldHopper.getRandomWorld(true);
				    status = "Hopping to " + startWorld;
				    WorldHopper.changeWorld(startWorld);
				}
			    }
			}
			sleep(200);
		    }

		    if (ORDER_LIST.size() > 0) {
			if (CURRENT_LOCATION == null)
			    CURRENT_LOCATION = ORDER_LIST.get(0);
			int level = SKILLS.WOODCUTTING.getActualLevel();
			if (level >= 1) {
			    TREE_LOCATIONS best = null;
			    int highest = 0;
			    for (TREE_LOCATIONS location : ORDER_LIST) {
				if (level >= location.level && location.level > highest) {
				    best = location;
				    highest = location.level;
				}
			    }
			    if (best != null && !best.equals(CURRENT_LOCATION)) {
				println("Chopped: " + logs + " " + CURRENT_LOCATION.name + " logs");
				logs = 0;
				CURRENT_LOCATION = best;
				println("We are level " + level + ". Changing locations to "
					+ toTitleCase(CURRENT_LOCATION.toString()));
			    }
			}
		    }

		    if (count < Inventory.getCount(LOGS)) {
			logs += Inventory.getCount(LOGS) - count;
			count = Inventory.getCount(LOGS);
		    } else if (count > 0 && Inventory.getCount(LOGS) == 0) {
			count = 0;
		    }

		    GameTab.open(TABS.INVENTORY);

		    if (!customOption) {
			if (CURRENT_LOCATION.equals(TREE_LOCATIONS.LUMBRIDGE_OAKS)
				|| CURRENT_LOCATION.equals(TREE_LOCATIONS.RIMMINGTON_OAKS_NORTH)
				|| CURRENT_LOCATION.equals(TREE_LOCATIONS.RIMMINGTON_WILLOWS_SOUTH)) {
			    banking = false;
			    dropping = false;
			    selling = true;
			} else if (LOGS_TO_BANK != null && Inventory.getCount(LOGS_TO_BANK) > 0) {
			    banking = true;
			    dropping = false;
			    selling = false;
			} else {
			    banking = false;
			    dropping = true;
			    selling = false;
			}
		    }

		    if (!hasAxe(null)) {
			println("No Woodcutting Axe found!");
			run = false;

		    } else if (Inventory.isFull()) {
			current = null;
			next = null;
			if (banking) {
			    bank();
			} else if (selling) {
			    sellLogs();
			} else if (dropping) {
			    status = "Dropping";
			    Inventory.dropAllExcept(ITEMS_TO_KEEP);
			    General.sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());
			    status = "Complete";
			}

		    } else if (Inventory.getCount("Coins") >= 208 && !hasAxe("Steel axe")) {
			status = "Upgrading Axe";
			upgradeAxe();

		    } else if (CURRENT_LOCATION.area != null && !CURRENT_LOCATION.area.contains(Player.getPosition())) {
			walkToTrees();

		    } else if (!openNest() && !pickUpNest()) {

			RSObject[] trees = Objects.findNearest(18, new Filter<RSObject>() {
			    @Override
			    public boolean accept(RSObject obj) {
				if (obj != null) {
				    if (!PathFinding.canReach(obj, true))
					return false;
				    RSObjectDefinition def = obj.getDefinition();
				    if (def != null) {
					String name = def.getName();
					if (name != null) {
					    if (name.equalsIgnoreCase(CURRENT_LOCATION.name))
						return true;
					}
				    }
				}
				return false;
			    }
			});

			if (trees.length > 0) {

			    currentTile = getClosestTile();
			    if (lastTile == null)
				lastTile = currentTile;
			    nextTile = getNextTile();

			    current = null;
			    if (trees.length > 1 && use_closest) {
				if (trees[1].getPosition().distanceToDouble(trees[0]) < 5.0)
				    current = trees[1];
			    }
			    if (current != null)
				current = trees[0];
			    if (trees.length > 1) {
				for (RSObject obj : trees) {
				    if (!obj.equals(current))
					next = obj;
				}
			    }

			    if (isChopping()) {
				if (hover_next) {
				    abc.waitNewOrSwitchDelay(last_busy_time, false);
				    hoverTree();
				} else {
				    status = "Chopping " + CURRENT_LOCATION.name;
				}
			    } else {
				abc.waitNewOrSwitchDelay(last_busy_time, false);
				chopTree();
				hover_next = abc.BOOL_TRACKER.HOVER_NEXT.next();
				go_to_anticipated = abc.BOOL_TRACKER.GO_TO_ANTICIPATED.next();
				use_closest = abc.BOOL_TRACKER.USE_CLOSEST.next();
				abc.BOOL_TRACKER.HOVER_NEXT.reset();
				abc.BOOL_TRACKER.GO_TO_ANTICIPATED.reset();
				abc.BOOL_TRACKER.USE_CLOSEST.reset();
			    }

			} else if (CURRENT_LOCATION.locations.length > 1) {
			    status = "Moving to next tree location";
			    if (currentTile == null)
				currentTile = getClosestTile();
			    nextTile = getNextTile();
			    if (nextTile != currentTile) {
				currentTile = lastTile;
			    }
			    Condition foundTree = new Condition() {
				@Override
				public boolean active() {
				    return Objects.findNearest(15, CURRENT_LOCATION.name).length > 0;
				}
			    };
			    if (!WebWalking.walkTo(nextTile, foundTree, 2000)) {
				status = "Found Tree at " + getClosestTile();
			    }

			} else {
			    status = "Waiting for " + CURRENT_LOCATION.name + " to spawn";
			    current = null;
			    next = null;
			}
		    }
		}
	    }

	    if (Game.getRunEnergy() >= abc.INT_TRACKER.NEXT_RUN_AT.next()) {
		status = "Setting Run";
		Options.setRunOn(true);
		abc.INT_TRACKER.NEXT_RUN_AT.reset();
	    }

	    if (System.currentTimeMillis() >= abc.TIME_TRACKER.ROTATE_CAMERA.next()) {
		status = "Rotating Camera";
		abc.performRotateCamera();
		abc.TIME_TRACKER.ROTATE_CAMERA.reset();
	    }

	    abc.performXPCheck(SKILLS.WOODCUTTING);
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

    private boolean hoverTree() {
	if (next != null && next.isOnScreen()) {
	    RSModel mod = next.getModel();
	    if (mod != null) {
		Polygon enclosed = mod.getEnclosedArea();
		if (enclosed != null) {
		    Point[] points = mod.getAllVisiblePoints();
		    if (points.length > 0) {
			if (!mod.getEnclosedArea().contains(Mouse.getPos())) {
			    status = "Hovering over next tree";
			    Point point = mod.getHumanHoverPoint();
			    if (point != null) {
				Mouse.move(point);
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(50, 200);
					return Game.getUptext().contains(CURRENT_LOCATION.name);
				    }
				}, General.random(600, 800));
				last_busy_time = Timing.currentTimeMillis();
				return true;
			    }
			}
		    }
		}
	    }
	} else if (CURRENT_LOCATION.locations.length > 1) {
	    Rectangle minimapRect = new Rectangle(537, 18, 200, 150);
	    if (!minimapRect.contains(Mouse.getPos())) {
		if (nextTile != null) {
		    RSTile[] tiles = PathFinding.generatePath(Player.getPosition(), nextTile, false);
		    if (tiles.length > 0) {
			RSTile hover = null;
			for (RSTile t : tiles) {
			    if (!isTileOnMinimap(t))
				break;
			    if (t != null)
				hover = t;
			}
			if (hover != null) {
			    status = "Hovering over next location";
			    Point p = Projection.tileToMinimap(hover);
			    if (p != null)
				Mouse.move(p);
			}
		    }
		}
	    }
	    last_busy_time = Timing.currentTimeMillis();
	    return true;
	}
	return false;
    }

    private boolean chopTree() {
	status = "Finding Tree";
	if (current != null) {
	    if (treeIsVisible(current)) {
		status = "Moving Mouse to Tree";
		RSModel mod = current.getModel();
		if (mod != null) {
		    Polygon enclosed = mod.getEnclosedArea();
		    if (enclosed != null) {
			if (!enclosed.contains(Mouse.getPos()) || !Game.getUptext().contains(CURRENT_LOCATION.name)) {
			    Point point = mod.getHumanHoverPoint();
			    if (point != null) {
				Mouse.move(point);
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(50, 200);
					return Game.getUptext().contains(CURRENT_LOCATION.name);
				    }
				}, General.random(600, 800));
			    }
			}
			if (enclosed.contains(Mouse.getPos()) && Game.getUptext().contains(CURRENT_LOCATION.name)) {
			    status = "Clicking Tree";
			    Mouse.click(1);
			    Timing.waitCondition(new Condition() {
				public boolean active() {
				    sleep(200, 500);
				    return isChopping();
				}
			    }, Player.getPosition().distanceTo(current) * 1000);
			    last_busy_time = Timing.currentTimeMillis();
			    return true;
			}
		    }
		}
	    } else {
		if (current != null) {
		    status = "Walking to Tree";
		    UsaUtils.walkToTile(current);
		}
	    }
	}
	return false;
    }

    private void bank() {
	RSTile p = Player.getPosition();
	if (p != null) {
	    if (Banking.isInBank() || TREE_GNOME_BANK.contains(p)) {
		status = "Inside Bank";
		if (Banking.openBank()) {
		    status = "Depositing Items";
		    Banking.depositAllExcept(ITEMS_TO_KEEP);
		    Banking.deposit(0, "Coins");
		    Timing.waitCondition(new Condition() {
			public boolean active() {
			    sleep(300, 500);
			    return Banking.find(LOGS).length > 0;
			}
		    }, 2000);
		    RSItem[] logs = Banking.find(LOGS);
		    for (RSItem item : logs) {
			if (item != null) {
			    String name = item.name;
			    int quantity = item.getStack();
			    if (name != null && quantity > 0) {
				if (name.equalsIgnoreCase("Logs")) {
				    logCount = quantity;
				} else if (name.equalsIgnoreCase("Oak logs")) {
				    oakCount = quantity;
				} else if (name.equalsIgnoreCase("Willow logs")) {
				    willowCount = quantity;
				} else if (name.equalsIgnoreCase("Teak logs")) {
				    teakCount = quantity;
				} else if (name.equalsIgnoreCase("Maple logs")) {
				    mapleCount = quantity;
				} else if (name.equalsIgnoreCase("Yew logs")) {
				    yewCount = quantity;
				} else if (name.equalsIgnoreCase("Magic logs")) {
				    magicCount = quantity;
				}
			    }
			}
		    }
		}
		if (Banking.isBankScreenOpen()) {
		    status = "Closing Bank";
		    Banking.close();
		}
	    } else {
		if (CURRENT_LOCATION.toString().contains("GNOME")) {
		    status = "Gnome Bank";
		    RSTile GNOME_STAIRS_SOUTH = new RSTile(2445, 3416, 0);
		    if (p.distanceTo(GNOME_STAIRS_SOUTH) > 3) {
			if (!Player.isMoving()) {
			    PathFinding.aStarWalk(GNOME_STAIRS_SOUTH);
			}
		    } else {
			climbStairs();
		    }
		} else if (CURRENT_LOCATION.toString().contains("EAGLES")) {
		    if (TREE_GNOME_STRONGHOLD.contains(p)) {
			status = "Gnome Bank";
			RSTile GNOME_STAIRS_SOUTH = new RSTile(2445, 3416, 0);
			if (p.distanceTo(GNOME_STAIRS_SOUTH) > 3) {
			    if (!Player.isMoving()) {
				PathFinding.aStarWalk(GNOME_STAIRS_SOUTH);
			    }
			} else {
			    climbStairs();
			}
		    } else {
			if (!openGate()) {
			    status = "Walking to Gnome Gate";
			    UsaUtils.walkPath(EAGLE_NEST_BANKING, 1);
			}
		    }
		} else {
		    status = "Walking to Bank";
		    WebWalking.walkToBank();
		}
	    }
	}
    }

    private boolean hasNotedLogs() {
	RSItem[] items = Inventory.find(LOGS);
	if (items.length > 0) {
	    for (RSItem item : items) {
		RSItemDefinition def = item.getDefinition();
		if (def != null) {
		    if (def.isNoted())
			return true;
		}
	    }
	}
	return false;
    }

    private boolean withdrawValuables(String[] names) {
	if (!Banking.isInBank()) {
	    status = "Walking to Bank";
	    WebWalking.walkToBank();
	    return false;
	}

	if (Banking.openBank()) {
	    if (Banking.isBankScreenOpen()) {
		Banking.depositAllExcept(ITEMS_TO_KEEP);
		status = "Depositing Items";
		RSInterfaceChild note = Interfaces.get(12, 22);
		if (note != null && !Screen.getColorAt(new Point(300, 321)).equals(new Color(123, 29, 27))) {
		    status = "Note Items";
		    note.click();
		    Timing.waitCondition(new Condition() {
			public boolean active() {
			    sleep(300, 500);
			    return Screen.getColorAt(new Point(300, 321)).equals(new Color(123, 29, 27));
			}
		    }, 3000);
		}
		RSItem[] items = Banking.find(names);
		for (RSItem item : items) {
		    if (item != null) {
			final String name = item.name;
			if (name != null) {
			    final int count = Inventory.getCount(name);
			    status = "Withdrawing all " + name;
			    Banking.withdrawItem(item, 0);
			    Timing.waitCondition(new Condition() {
				public boolean active() {
				    sleep(300, 500);
				    return Inventory.getCount(name) != count;
				}
			    }, 3000);
			}
		    }
		}
		if (Banking.find(names).length == 0) {
		    status = "Got all items";
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean receiveItemsFromPlayers(String[] users, RSTile location) {
	RSTile pos = Player.getPosition();
	if (pos != null) {
	    if (!isTileOnMinimap(location)) {
		status = "Walking to master location";
		if (!Player.isMoving())
		    WebWalking.walkTo(location);
		sleep(500, 1000);
	    } else if (!pos.isOnScreen()) {
		if (!Player.isMoving())
		    Walking.clickTileMM(location, 1);
		sleep(500, 1000);
	    } else {
		if (Trading.getWindowState() == null) {
		    RSPlayer[] player = Players.findNearest(users);
		    if (player.length > 0) {
			if (player[0] != null) {
			    String name = player[0].getName();
			    if (name != null) {
				if (player[0].isOnScreen()) {
				    status = "Trading " + name;
				    player[0].click("Trade with " + name);
				    Timing.waitCondition(new Condition() {
					public boolean active() {
					    sleep(300, 500);
					    return Trading.getWindowState() != null;
					}
				    }, General.random(5000, 10000));
				}
			    }
			}
		    }
		} else {
		    WINDOW_STATE state = Trading.getWindowState();
		    status = "Waiting to accept";
		    if (Trading.hasAccepted(true)) {
			if (state == Trading.WINDOW_STATE.FIRST_WINDOW) {
			    status = "Accepting First Window";
			    Trading.accept();
			} else if (state == Trading.WINDOW_STATE.SECOND_WINDOW) {
			    status = "Accepting Second Window";
			    if (Trading.accept()) {
				tradesComplete++;
				tradeSlave = false;
			    }
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean giveItemsToPlayer(String user, RSTile location, String[] names) {
	RSTile pos = Player.getPosition();
	if (pos != null) {
	    if (!isTileOnMinimap(location)) {
		status = "Walking to master location";
		if (Player.isMoving())
		    WebWalking.walkTo(location);
		sleep(500, 1000);
	    } else {
		if (Trading.getWindowState() == null) {
		    RSPlayer[] player = Players.find(user);
		    if (player.length > 0) {
			if (player[0] != null) {
			    String name = player[0].getName();
			    if (name != null) {
				if (player[0].isOnScreen()) {
				    status = "Trading " + name;
				    player[0].click("Trade with " + name);
				    Timing.waitCondition(new Condition() {
					public boolean active() {
					    sleep(300, 500);
					    return Trading.getWindowState() != null;
					}
				    }, General.random(5000, 10000));
				} else {
				    if (!Player.isMoving())
					Walking.clickTileMM(player[0], 1);
				    sleep(500, 1000);
				}
			    }
			}
		    }
		} else {
		    WINDOW_STATE state = Trading.getWindowState();
		    if (Inventory.getCount(names) == 0) {
			if (state == Trading.WINDOW_STATE.FIRST_WINDOW) {
			    status = "Accepting First Window";
			    Trading.accept();
			} else if (state == Trading.WINDOW_STATE.SECOND_WINDOW) {
			    status = "Accepting Second Window";
			    if (Trading.accept()) {
				return true;
			    }
			}
		    } else {
			for (String name : names) {
			    if (Inventory.getCount(name) > 0) {
				final int before = Trading.getOfferedItems(false).length;
				status = "Offering all " + name;
				Trading.offer(0, name);
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(300, 500);
					return Trading.getOfferedItems(false).length != before;
				    }
				}, 3000);
			    }
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean openGate() {
	RSObject[] gate = Objects.find(15, "Gate");
	if (gate.length > 0) {
	    if (gate[0] != null) {
		if (gate[0].isOnScreen()) {
		    status = "Opening Gate";
		    if (TREE_GNOME_STRONGHOLD.contains(Player.getPosition())) {
			gate[0].click("Open");
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				sleep(300, 500);
				return !TREE_GNOME_STRONGHOLD.contains(Player.getPosition());
			    }
			}, 4000);
		    } else {
			gate[0].click("Open");
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				sleep(300, 500);
				return TREE_GNOME_STRONGHOLD.contains(Player.getPosition());
			    }
			}, 4000);
		    }
		} else {
		    status = "Walking to Gate";
		    if (!Player.isMoving()) {
			Walking.walkTo(gate[0]);
		    }
		}
	    }
	}
	return false;
    }

    private boolean climbStairs() {
	RSObject[] staircase = Objects.findNearest(25, "Staircase");
	if (staircase.length > 0) {
	    if (staircase[0] != null) {
		if (staircase[0].isOnScreen()) {
		    status = "Climbing Stairs";
		    final int plane = Game.getPlane();
		    staircase[0].click("Climb");
		    Timing.waitCondition(new Condition() {
			public boolean active() {
			    sleep(300, 500);
			    return plane != Game.getPlane();
			}
		    }, 3000);
		    if (plane != Game.getPlane()) {
			status = "Complete";
			return true;
		    }
		} else {
		    status = "Walking to Staircase";
		    if (!Player.isMoving()) {
			Walking.walkTo(staircase[0]);
		    }
		}
	    }
	}
	return false;
    }

    private boolean pickUpNest() {
	if (nest && !Inventory.isFull()) {
	    RSGroundItem[] nests = GroundItems.find("Bird nest");
	    if (nests.length > 0) {
		RSGroundItem nest = nests[0];
		if (nest != null) {
		    if (nest.isOnScreen()) {
			if (!Player.isMoving()) {
			    final int count = Inventory.getCount("Bird nest");
			    nest.click("Take Bird nest");
			    Timing.waitCondition(new Condition() {
				public boolean active() {
				    sleep(100, 300);
				    return Inventory.getCount("Bird nest") != count;
				}
			    }, 2000);
			}
		    } else {
			status = "Walking to nest";
			UsaUtils.walkToTile(nest);
		    }
		}
		return true;
	    }
	}
	return false;
    }

    private boolean openNest() {
	if (openNest && !Inventory.isFull()) {
	    RSItem[] item = Inventory.find("Bird nest");
	    if (item.length > 0) {
		for (int i = 0; i < item.length; i++) {
		    if (item[i] != null) {
			RSItemDefinition def = item[i].getDefinition();
			if (def != null) {
			    int length = def.getActions().length;
			    if (length > 1) {
				final RSItem[] before = Inventory.getAll();
				item[i].click();
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(300, 500);
					return before != Inventory.getAll();
				    }
				}, 2000);
				return true;
			    }
			}
		    }
		}
	    }
	}
	return false;
    }

    private void walkToTrees() {
	RSTile p = Player.getPosition();
	if (p != null) {
	    RSTile GNOME_GATE_INSIDE = new RSTile(2461 + General.random(-1, 1), 3385 + General.random(-1, 1), 0);
	    RSTile GNOME_GATE_OUTSIDE = new RSTile(2461 + General.random(-1, 1), 3382 + General.random(-1, 1), 0);
	    if (CURRENT_LOCATION.toString().contains("EAGLES")) {
		if (p.distanceTo(GNOME_GATE_INSIDE) < 200) {
		    if (TREE_GNOME_BANK.contains(p)) {
			climbStairs();
		    } else if (TREE_GNOME_STRONGHOLD.contains(p)) {
			status = "Walking to Gate";
			if (!openGate())
			    PathFinding.aStarWalk(GNOME_GATE_INSIDE);
		    } else {
			UsaUtils.walkPath(Walking.invertPath(EAGLE_NEST_TREES), 1);
		    }
		} else {
		    status = "Web Walking to Gate";
		    WebWalking.walkTo(GNOME_GATE_OUTSIDE);
		}
	    } else {
		status = "Web Walking to " + CURRENT_LOCATION.name + "s";
		WebWalking.walkTo(getClosestTile());
	    }
	}
    }

    private void upgradeAxe() {
	RSTile BOB_LOCATION = new RSTile(3231 + General.random(-1, 1), 3203 + General.random(-1, 1), 0);
	if (!BOB_LOCATION.isOnScreen()) {
	    status = "Walking to Bob's Axes";
	    UsaUtils.walkToTile(BOB_LOCATION);
	} else {
	    if (Interfaces.isInterfaceValid(300)) {
		if (ChooseOption.isOpen())
		    ChooseOption.close();
		Point STEEL_AXE_POINT = new Point(235 + General.random(-5, 5), 85 + General.random(-5, 5));
		Mouse.click(STEEL_AXE_POINT, 3);
		Timing.waitCondition(new Condition() {
		    public boolean active() {
			sleep(300, 500);
			return ChooseOption.isOpen();
		    }
		}, 2000);
		final int count = Inventory.getCount("Coins");
		if (ChooseOption.select("Buy 1 Steel axe")) {
		    Timing.waitCondition(new Condition() {
			public boolean active() {
			    sleep(300, 500);
			    return count != Inventory.getCount("Coins");
			}
		    }, 2000);
		}
	    } else if (!Player.isMoving()) {
		RSNPC[] bob = NPCs.find("Bob");
		if (bob.length > 0) {
		    if (bob[0] != null) {
			bob[0].click("Trade");
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				sleep(300, 500);
				return Interfaces.isInterfaceValid(300);
			    }
			}, 2000);
		    }
		}
	    }
	}
    }

    private void sellLogs() {
	STORE closest = STORE.getClosestStore();
	while (Inventory.getCount(LOGS) > 0) {
	    if (closest != null) {
		Positionable p = Player.getPosition();
		if (!closest.area.contains(p)) {
		    if (p != null) {
			status = "Walking to " + toTitleCase(closest.toString()) + " store";
			WebWalking.walkTo(closest.location);
		    }
		} else {
		    if (Interfaces.isInterfaceValid(300)) {
			status = "Store Is Open";
			sellItems();
		    } else if (!Player.isMoving()) {
			status = "Opening Store";
			RSNPC[] npcs = NPCs.findNearest(closest.names);
			if (npcs.length > 0) {
			    if (npcs[0].isOnScreen()) {
				status = "Trading " + npcs[0].getName();
				npcs[0].click("Trade");
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(300, 500);
					return Interfaces.isInterfaceValid(300);
				    }
				}, 2000);
			    }
			}
		    }
		}
	    }
	    sleep(100);
	}
    }

    private void sellItems() {
	RSItem[] items = Inventory.getAll();
	for (int i = 0; i < items.length; i++) {
	    RSItem item = items[i];
	    if (item != null) {
		RSItemDefinition def = item.getDefinition();
		if (def != null) {
		    String name = def.getName();
		    if (name != null) {
			boolean sell = true;
			for (int j = 0; j < ITEMS_TO_KEEP.length; j++) {
			    if (name.equalsIgnoreCase(ITEMS_TO_KEEP[j])) {
				sell = false;
			    }
			}
			if (sell) {
			    final int count = Inventory.getAll().length;
			    if (Inventory.getCount(name) > 5) {
				status = "Sell 10 " + name;
				item.click("Sell 10");
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(100, 200);
					return Inventory.getAll().length != count;
				    }
				}, 2000);
				General.sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());
				break;
			    } else if (Inventory.getCount(name) > 1) {
				status = "Sell 5 " + name;
				item.click("Sell 5");
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(100, 200);
					return Inventory.getAll().length != count;
				    }
				}, 2000);
				General.sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());
				break;
			    } else {
				status = "Sell 1 " + name;
				item.click("Sell 1");
				Timing.waitCondition(new Condition() {
				    public boolean active() {
					sleep(100, 200);
					return Inventory.getAll().length != count;
				    }
				}, 2000);
				General.sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());
				break;
			    }
			}
		    }
		}
	    }
	}
    }

    /**
     * @return - further tile away from current tile of the locations RSTile[]
     *         array
     */
    private RSTile getNextTile() {
	RSTile[] loc = CURRENT_LOCATION.locations;
	RSTile p = Player.getPosition();
	if (loc.length > 1) {
	    int distance = 1000;
	    RSTile next = null;
	    for (int i = 0; i < loc.length; i++) {
		if (loc.length <= 2) {
		    if (p.distanceTo(loc[i]) < distance && loc[i] != currentTile) {
			distance = p.distanceTo(loc[i]);
			next = loc[i];
		    }
		} else {
		    if (p.distanceTo(loc[i]) < distance && loc[i] != currentTile && loc[i] != lastTile) {
			distance = p.distanceTo(loc[i]);
			next = loc[i];
		    }
		}
	    }
	    return next;
	} else {
	    if (loc[0] != null)
		return loc[0];
	}
	return null;
    }

    /**
     * @return - returns closest tile to player from locations RSTile[]
     */
    private RSTile getClosestTile() {
	RSTile[] loc = CURRENT_LOCATION.locations;
	RSTile p = Player.getPosition();
	if (loc.length > 1) {
	    int distance = loc[0].distanceTo(p);
	    RSTile closest = loc[0];
	    for (int i = 1; i < loc.length; i++) {
		if (p.distanceTo(loc[i]) < distance) {
		    distance = p.distanceTo(loc[i]);
		    closest = loc[i];
		}
	    }
	    return closest;
	} else {
	    if (loc[0] != null)
		return loc[0];
	}
	return null;
    }

    private boolean hasAxe(String name) {
	String search = "axe";
	if (name != null)
	    search = name;

	RSItem[] all = Inventory.getAll();
	if (all.length > 0) {
	    for (int i = 0; i < all.length; i++) {
		RSItem item = all[i];
		if (item != null) {
		    RSItemDefinition def = item.getDefinition();
		    if (def != null) {
			String defName = def.getName();
			if (defName != null) {
			    if (defName.contains(search)) {
				return true;
			    }
			}
		    }
		}
	    }
	}

	RSItem weapon = Equipment.getItem(SLOTS.WEAPON);
	if (weapon != null) {
	    RSItemDefinition def = weapon.getDefinition();
	    if (def != null) {
		String defName = def.getName();
		if (defName != null) {
		    if (defName.contains(search)) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    private boolean treeIsVisible(RSObject tree) {
	if (tree != null) {
	    RSModel mod = tree.getModel();
	    if (mod != null) {
		return mod.getAllVisiblePoints().length > 0;
	    }
	}
	return false;
    }

    private boolean isChopping() {
	return Player.getAnimation() != -1;
    }

    private boolean isTileOnMinimap(RSTile t) {
	return Projection.isInMinimap(Projection.tileToMinimap(t));
    }

    private enum STORE {

	LUMBRIDGE(new String[] { "Shop assistant", "Shop keeper" }, new RSTile(3211, 3246, 0),
		new RSArea(new RSTile(3208, 3243, 0), new RSTile(3214, 3251, 0))),

	RIMMINGTON(new String[] { "Shop assistant", "Shop keeper" }, new RSTile(2948, 3215, 0),
		new RSArea(new RSTile(2946, 3212, 0), new RSTile(2950, 3218, 0)));

	private final String[] names;
	private final RSTile location;
	private final RSArea area;

	STORE(String[] name, RSTile location, RSArea area) {
	    this.names = name;
	    this.location = location;
	    this.area = area;
	}

	static STORE getClosestStore() {
	    RSTile pos = Player.getPosition();
	    if (pos != null) {
		STORE closest = null;
		int distance = 10000;
		for (STORE store : STORE.values()) {
		    int temp = store.location.distanceTo(pos);
		    if (temp < distance) {
			distance = temp;
			closest = store;
		    }
		}
		return closest;
	    }
	    return null;
	}

    }

    private enum TREE_LOCATIONS {

	PORT_SARIM_WILLOWS_EAST("Willow", 30, new RSTile[] { new RSTile(3060, 3253, 0) },
		new RSArea(new RSTile(3055, 3250, 0), new RSTile(3064, 3257, 0))),

	RIMMINGTON_YEWS("Yew", 60, new RSTile[] { new RSTile(2939, 3230, 0) },
		new RSArea(new RSTile(2930, 3223, 0), new RSTile(2944, 3236, 0))),

	FALADOR_YEWS("Yew", 60,
		new RSTile[] { new RSTile(3042, 3318, 0), new RSTile(3020, 3314, 0), new RSTile(2997, 3310, 0) },
		new RSArea(new RSTile(2993, 3308, 0), new RSTile(3048, 3322, 0))),

	LUMBRIDGE_YEWS(
		"Yew", 60, new RSTile[] { new RSTile(3185, 3229, 0), new RSTile(3166, 3222, 0),
			new RSTile(3152, 3233, 0), new RSTile(3149, 3255, 0) },
		new RSArea(new RSTile(3145, 3218, 0), new RSTile(3190, 3260, 0))),

	VARROCK_SAWMILL_YEWS("Yew", 60,
		new RSTile[] { new RSTile(3271, 3473, 0), new RSTile(3267, 3492, 0), new RSTile(3303, 3470, 0) },
		new RSArea(new RSTile(3264, 3465, 0), new RSTile(3310, 3500, 0))),

	VARROCK_CASTLE_YEWS("Yew", 60, new RSTile[] { new RSTile(3220, 3502, 0), new RSTile(3206, 3502, 0) },
		new RSArea(new RSTile(3202, 3498, 0), new RSTile(3225, 3506, 0))),

	PORT_SARIM_TREES("Tree", 1, new RSTile[] { new RSTile(3047, 3264, 0) },
		new RSArea(new RSTile(3032, 3258, 0), new RSTile(3062, 3274, 0))),

	LUMBRIDGE_TREES("Tree", 1, new RSTile[] { new RSTile(3188, 3243, 0) },
		new RSArea(new RSTile(3144, 3217, 0), new RSTile(3198, 3256, 0))),

	LUMBRIDGE_OAKS("Oak", 15, new RSTile[] { new RSTile(3204, 3244, 0) },
		new RSArea(new RSTile(3201, 3238, 0), new RSTile(3207, 3251, 0))),

	PORT_SARIM_OAKS("Oak", 15, new RSTile[] { new RSTile(3057, 3265, 0) },
		new RSArea(new RSTile(3055, 3260, 0), new RSTile(3060, 3271, 0))),

	EDGEVILLE_YEWS("Yew", 60, new RSTile[] { new RSTile(3088, 3471, 0) },
		new RSArea(new RSTile(3085, 3468, 0), new RSTile(3089, 3482, 0))),

	DRAYNOR_OAKS("Oak", 15, new RSTile[] { new RSTile(3099, 3286, 0) },
		new RSArea(new RSTile(3094, 3281, 0), new RSTile(3109, 3292))),

	DRAYNOR_WILLOWS("Willow", 30, new RSTile[] { new RSTile(3087, 3237, 0) },
		new RSArea(new RSTile(3081, 3226, 0), new RSTile(3090, 3239, 0))),

	DRAYNOR_TREES("Tree", 1, new RSTile[] { new RSTile(3082, 3269, 0) },
		new RSArea(new RSTile(3074, 3264, 0), new RSTile(3086, 3275, 0))),

	RIMMINGTON_WILLOWS_SOUTH("Willow", 30, new RSTile[] { new RSTile(2968, 3196, 0) },
		new RSArea(new RSTile(2960, 3191, 0), new RSTile(2976, 3201, 0))),

	RIMMINGTON_WILLOWS_EAST("Willow", 30, new RSTile[] { new RSTile(2990, 3187, 0) },
		new RSArea(new RSTile(2986, 3183, 0), new RSTile(2993, 3192, 0))),

	MUDSKIPPER_POINT_WILLOWS("Willow", 30, new RSTile[] { new RSTile(2999, 3169, 0) },
		new RSArea(new RSTile(2995, 3163, 0), new RSTile(3004, 3172, 0))),

	PORT_SARIM_WILLOWS_SOUTH("Willow", 30, new RSTile[] { new RSTile(3027, 3173, 0) },
		new RSArea(new RSTile(3025, 3167, 0), new RSTile(3030, 3179, 0))),

	RIMMINGTON_OAKS_SOUTH("Oak", 15, new RSTile[] { new RSTile(2983, 3204, 0) },
		new RSArea(new RSTile(2974, 3199, 0), new RSTile(2990, 3210, 0))),

	RIMMINGTON_TREES("Tree", 1, new RSTile[] { new RSTile(2926, 3227, 0) },
		new RSArea(new RSTile(2919, 3219, 0), new RSTile(2932, 3235, 0))),

	RIMMINGTON_OAKS_NORTH("Oak", 15, new RSTile[] { new RSTile(2956, 3233, 0) },
		new RSArea(new RSTile(2951, 3229, 0), new RSTile(2960, 3235, 0))),

	FALADOR_OAKS_SOUTH("Oak", 15, new RSTile[] { new RSTile(3057, 3317, 0) },
		new RSArea(new RSTile(3043, 3308, 0), new RSTile(3066, 3319, 0))),

	BARBARIAN_EVERGREENS("Evergreen", 1, new RSTile[] { new RSTile(3053, 3440, 0) },
		new RSArea(new RSTile(3036, 3425, 0), new RSTile(3066, 3452, 0))),

	VARROCK_TREES_WEST("Tree", 1, new RSTile[] { new RSTile(3165, 3382, 0) },
		new RSArea(new RSTile(3158, 3376, 0), new RSTile(3169, 3400, 0))),

	VARROCK_OAKS_SOUTH("Oak", 15, new RSTile[] { new RSTile(3255, 3367, 0) },
		new RSArea(new RSTile(3248, 3360, 0), new RSTile(3260, 3371, 0))),

	VARROCK_TREES_SAWMILL("Tree", 1, new RSTile[] { new RSTile(3275, 3451, 0) },
		new RSArea(new RSTile(3271, 3443, 0), new RSTile(3281, 3457, 0))),

	VARROCK_OAKS_SAWMILL("Oak", 15, new RSTile[] { new RSTile(3293, 3488, 0) },
		new RSArea(new RSTile(3287, 3483, 0), new RSTile(3298, 3497, 0))),

	TREE_GNOME_YEWS_WEST("Yew", 60, new RSTile[] { new RSTile(2433, 3428, 0), new RSTile(2433, 3439, 0) },
		new RSArea(new RSTile(2426, 3423, 0), new RSTile(2440, 3445, 0))),

	TREE_GNOME_YEWS_EAST("Yew", 60, new RSTile[] { new RSTile(2489, 3398, 0) },
		new RSArea(new RSTile(2486, 3391, 0), new RSTile(2496, 3404, 0))),

	TREE_GNOME_MAGICS("Magic tree", 75,
		new RSTile[] { new RSTile(2432, 3412, 0), new RSTile(2372, 3425, 0), new RSTile(2490, 3413, 0) },
		new RSArea(new RSTile(2360, 3400, 0), new RSTile(2500, 3440, 0))),

	EAGLES_PEAK_YEWS("Yew", 60,
		new RSTile[] { new RSTile(2363, 3477, 0), new RSTile(2355, 3510, 0), new RSTile(2331, 3516, 0) },
		new RSArea(new RSTile(2325, 3465, 0), new RSTile(2370, 3520, 0))),

	LEGENDS_GUILD_YEWS("Yew", 60, new RSTile[] { new RSTile(2734, 3334, 0) },
		new RSArea(new RSTile(2728, 3328, 0), new RSTile(2741, 3340, 0))),

	LEGENDS_GUILD_MAPLES("Maple tree", 45, new RSTile[] { new RSTile(2711, 3383, 0) },
		new RSArea(new RSTile(2707, 3378, 0), new RSTile(2717, 3387, 0))),

	SORCERERS_TOWER_MAGICS("Magic tree", 75, new RSTile[] { new RSTile(2702, 3397, 0) },
		new RSArea(new RSTile(2698, 3395, 0), new RSTile(2706, 3400, 0))),

	SEERS_MAGICS("Magic tree", 75, new RSTile[] { new RSTile(2694, 3425, 0) },
		new RSArea(new RSTile(2688, 3420, 0), new RSTile(2700, 3429, 0))),

	SEERS_OAKS("Oak", 15, new RSTile[] { new RSTile(2696, 3440, 0) },
		new RSArea(new RSTile(2691, 3434, 0), new RSTile(2702, 3445, 0))),

	SEERS_YEWS("Yew", 60, new RSTile[] { new RSTile(2714, 3463, 0) },
		new RSArea(new RSTile(2704, 3456, 0), new RSTile(2717, 3468, 0))),

	SEERS_MAPLES("Maple tree", 45, new RSTile[] { new RSTile(2730, 3500, 0) },
		new RSArea(new RSTile(2720, 3498, 0), new RSTile(2734, 3504, 0))),

	SEERS_WILLOWS("Willow", 30, new RSTile[] { new RSTile(2711, 3510, 0) },
		new RSArea(new RSTile(2706, 3506, 0), new RSTile(2715, 3515, 0))),

	MCGRUBORS_WOOD_TREES("Tree", 1, new RSTile[] { new RSTile(2651, 3466, 0) },
		new RSArea(new RSTile(2629, 3457, 0), new RSTile(2674, 3465, 0))),

	CATHERBY_OAKS("Oak", 15, new RSTile[] { new RSTile(2787, 3436, 0) },
		new RSArea(new RSTile(2783, 3432, 0), new RSTile(2791, 3442, 0))),

	CATHERBY_YEWS("Yew", 60, new RSTile[] { new RSTile(2758, 3432, 0), new RSTile(3355, 3311, 0) },
		new RSArea(new RSTile(2752, 3426, 0), new RSTile(2769, 3436, 0))),

	CASTLE_WARS_TEAK("Teak", 35, new RSTile[] { new RSTile(2335, 3047, 0) },
		new RSArea(new RSTile(2333, 3044, 0), new RSTile(2338, 3050, 0))),

	DUEL_ARENA_MAGICS("Magic tree", 75, new RSTile[] { new RSTile(3369, 3313, 0) },
		new RSArea(new RSTile(3353, 3295, 0), new RSTile(3373, 3324, 0))),

	CUSTOM("", 1, new RSTile[] { new RSTile(1, 1, 0) }, new RSArea(new RSTile(1, 1, 0), 1));

	private String name;
	private int level;
	private RSTile[] locations;
	private RSArea area;

	TREE_LOCATIONS(String name, int level, RSTile[] locations, RSArea area) {
	    this.name = name;
	    this.level = level;
	    this.locations = locations;
	    this.area = area;
	}

	static ArrayList<TREE_LOCATIONS> getLocationsFor(String name) {
	    ArrayList<TREE_LOCATIONS> list = new ArrayList<TREE_LOCATIONS>();
	    for (TREE_LOCATIONS tree : TREE_LOCATIONS.values()) {
		if (tree.name.contains(name)) {
		    list.add(tree);
		}
	    }
	    return list;
	}

	static TREE_LOCATIONS getClosestLocation() {
	    RSTile p = Player.getPosition();
	    int level = SKILLS.WOODCUTTING.getActualLevel();
	    if (p != null && level >= 1) {
		int distance = 10000;
		TREE_LOCATIONS closest = null;
		for (TREE_LOCATIONS tree : TREE_LOCATIONS.values()) {
		    int temp = tree.locations[0].distanceTo(p);
		    if (temp < distance) {
			if (level >= tree.level) {
			    closest = tree;
			    distance = temp;
			}
		    }
		}
		return closest;
	    }
	    return null;
	}

	static ArrayList<String> getRandomOrder() {
	    int level = 1;
	    level = SKILLS.WOODCUTTING.getActualLevel();
	    ArrayList<String> list = new ArrayList<String>();
	    if (level < 15) {
		ArrayList<TREE_LOCATIONS> tree = getLocationsFor("Tree");
		list.add(toTitleCase(tree.get(General.random(1, tree.size() - 1)).toString()));
	    }
	    if (level < 30) {
		ArrayList<TREE_LOCATIONS> oak = getLocationsFor("Oak");
		list.add(toTitleCase(oak.get(General.random(1, oak.size() - 1)).toString()));
	    }
	    if (level < 60) {
		ArrayList<TREE_LOCATIONS> willow = getLocationsFor("Willow");
		list.add(toTitleCase(willow.get(General.random(1, willow.size() - 1)).toString()));
	    }
	    if (level < 75) {
		ArrayList<TREE_LOCATIONS> yew = getLocationsFor("Yew");
		list.add(toTitleCase(yew.get(General.random(1, yew.size() - 1)).toString()));
	    }
	    ArrayList<TREE_LOCATIONS> magic = getLocationsFor("Magic");
	    list.add(toTitleCase(magic.get(General.random(1, magic.size() - 1)).toString()));
	    return list;
	}

	static TREE_LOCATIONS getClosestTree(String name) {
	    TREE_LOCATIONS closest = null;
	    RSTile p = Player.getPosition();
	    if (p != null) {
		int distance = 10000;
		for (TREE_LOCATIONS tree : TREE_LOCATIONS.values()) {
		    int temp = tree.locations[0].distanceTo(p);
		    if (temp < distance) {
			if (tree.name.contains(name)) {
			    closest = tree;
			    distance = temp;
			}
		    }
		}
	    }
	    return closest;
	}

	static ArrayList<String> getClosestOrder() {
	    int level = 1;
	    level = SKILLS.WOODCUTTING.getActualLevel();
	    ArrayList<String> list = new ArrayList<String>();
	    if (level < 15) {
		list.add(toTitleCase(getClosestTree("Tree").toString()));
	    }
	    if (level < 30) {
		list.add(toTitleCase(getClosestTree("Oak").toString()));
	    }
	    if (level < 60) {
		list.add(toTitleCase(getClosestTree("Willow").toString()));
	    }
	    if (level < 75) {
		list.add(toTitleCase(getClosestTree("Yew").toString()));
	    }
	    list.add(toTitleCase(getClosestTree("Magic").toString()));
	    return list;
	}

	static TREE_LOCATIONS getLocation(String name) {
	    for (TREE_LOCATIONS tree : TREE_LOCATIONS.values()) {
		if (toTitleCase(tree.toString()).equalsIgnoreCase(name)) {
		    return tree;
		}
	    }
	    return null;
	}

	static void setCustom(String name, int level, RSTile[] locations, RSArea area) {
	    TREE_LOCATIONS.CUSTOM.name = name;
	    TREE_LOCATIONS.CUSTOM.level = level;
	    TREE_LOCATIONS.CUSTOM.locations = locations;
	    TREE_LOCATIONS.CUSTOM.area = area;
	}
    }

    private static String toTitleCase(String givenString) {
	if (givenString.length() == 0)
	    return null;

	givenString = givenString.toLowerCase();
	String[] arr = givenString.split("_");
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < arr.length; i++) {
	    sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
	}
	return sb.toString().trim();
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
	int currentLVL = Skills.getActualLevel(SKILLS.WOODCUTTING);
	int xpGained = Skills.getXP(SKILLS.WOODCUTTING) - startXP;
	int logsPerHour = (int) (logs * 3600000D / (System.currentTimeMillis() - startTime));
	int xpPerHour = (int) (xpGained * 3600000D / (System.currentTimeMillis() - startTime));
	int tradesPerHour = (int) (tradesComplete * 3600000D / (System.currentTimeMillis() - startTime));
	int masterLogs = logs - startLogs;
	int masterLogsPerHour = (int) (masterLogs * 3600000D / (System.currentTimeMillis() - startTime));

	Font bold = new Font("Tahoma", Font.BOLD, 12);
	int x = 0;
	int y = 0;

	if (masterOption && !isMaster) {
	    if (transfer) {
		g.drawImage(transferring_items, 551, 304, null);
	    } else {
		g.setFont(bold);
		g.setColor(Color.WHITE);
		g.drawImage(transfer_items, 553, 304, null);
		int original_x = 555;
		int original_y = 355;
		x = original_x;
		y = original_y;
		boolean shift = false;
		if (logCount > 0) {
		    g.drawImage(log_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(logCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
		if (shift) {
		    if (x == original_x) {
			x = x + 90;
		    } else {
			x = x - 90;
		    }
		} else {
		    y += 25;
		}
		if (oakCount > 0) {
		    g.drawImage(oak_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(oakCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
		if (shift) {
		    if (x == original_x) {
			x = x + 90;
		    } else {
			x = x - 90;
		    }
		} else {
		    y += 25;
		}
		if (willowCount > 0) {
		    g.drawImage(willow_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(willowCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
		if (shift) {
		    if (x == original_x) {
			x = x + 90;
		    } else {
			x = x - 90;
		    }
		} else {
		    y += 25;
		}
		if (teakCount > 0) {
		    g.drawImage(teak_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(teakCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
		if (shift) {
		    if (x == original_x) {
			x = x + 90;
		    } else {
			x = x - 90;
		    }
		} else {
		    y += 25;
		}
		if (mapleCount > 0) {
		    g.drawImage(maple_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(mapleCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
		if (shift) {
		    if (x == original_x) {
			x = x + 90;
		    } else {
			x = x - 90;
		    }
		} else {
		    y += 25;
		}
		if (yewCount > 0) {
		    g.drawImage(yew_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(yewCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
		if (shift) {
		    if (x == original_x) {
			x = x + 90;
		    } else {
			x = x - 90;
		    }
		} else {
		    y += 25;
		}
		if (magicCount > 0) {
		    g.drawImage(magic_image, x, y, null);
		    g.drawString(" x " + addCommasToNumericString(Integer.toString(magicCount)), x + 33, y + 16);
		    if (shift) {
			shift = false;
		    } else {
			shift = true;
		    }
		}
	    }
	}

	if (isMaster) {
	    g.drawImage(master_image, 288, 345, null);

	    g.setColor(Color.WHITE);
	    Font small = new Font("Segoe UI", Font.PLAIN, 11);
	    g.setFont(small);
	    g.drawString(version, 477, 366);

	    Font main = new Font("Segoe", Font.BOLD, 12);
	    g.setFont(main);
	    x = 302;
	    y = 380;
	    g.drawString(Timing.msToString(time), 329, 384);
	    g.drawString(status, 341, 409);
	    g.drawString(addCommasToNumericString(Integer.toString(masterLogs)) + " ("
		    + addCommasToNumericString(Integer.toString(masterLogsPerHour)) + "/hr)", 369, 437);
	    g.drawString(addCommasToNumericString(Integer.toString(tradesComplete)) + " ("
		    + addCommasToNumericString(Integer.toString(tradesPerHour)) + "/hr)", 403, 463);
	} else {

	    g.drawImage(paint_image, 288, 345, null);

	    g.setColor(Color.WHITE);
	    Font small = new Font("Segoe UI", Font.PLAIN, 11);
	    g.setFont(small);
	    g.drawString(version, 477, 366);

	    Font main = new Font("Segoe", Font.BOLD, 12);
	    g.setFont(main);
	    x = 302;
	    y = 380;
	    g.drawString(Timing.msToString(time), 329, 383);
	    g.drawString(status, 342, 398);
	    g.drawString(addCommasToNumericString(Integer.toString(logs)) + " ("
		    + addCommasToNumericString(Integer.toString(logsPerHour)) + "/hr)", 370, 413);
	    g.drawString(currentLVL + " (+" + (currentLVL - startLVL) + ")", 409, 428);
	    g.drawString(addCommasToNumericString(Integer.toString(xpGained)) + " ("
		    + addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", 358, 443);

	    int xpTNL = Skills.getXPToNextLevel(SKILLS.WOODCUTTING);
	    int percentTNL = Skills.getPercentToNextLevel(SKILLS.WOODCUTTING);
	    long TTNL = 0;
	    if (xpPerHour > 0) {
		TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
	    }
	    int percentFill = (185 * percentTNL) / 100;

	    g.setColor(Color.RED);
	    // g.fillRect(301, 451, 185, 15);
	    g.fillRoundRect(301, 451, 185, 15, 3, 3);
	    Color green = new Color(0, 190, 0);
	    g.setColor(green);
	    // g.fillRect(301, 451, percentFill, 15);
	    g.fillRoundRect(301, 451, percentFill, 15, 3, 3);
	    g.setColor(Color.WHITE);
	    g.drawRoundRect(301, 451, 185, 15, 3, 3);
	    // g.drawRect(301, 451, 185, 15);
	    g.drawString(addCommasToNumericString(Integer.toString(xpTNL)) + " XP to " + (currentLVL + 1) + " | "
		    + Timing.msToString(TTNL), 325, 463);

	    g.setColor(Color.GREEN);
	    if (current != null) {
		RSModel mod = current.getModel();
		if (mod != null) {
		    Polygon p = mod.getEnclosedArea();
		    if (p != null) {
			g.drawPolygon(p);
		    }
		}
	    }

	    g.setColor(Color.RED);
	    if (next != null) {
		RSModel mod = next.getModel();
		if (mod != null) {
		    Polygon p = mod.getEnclosedArea();
		    if (p != null) {
			g.drawPolygon(p);
		    }
		}
	    }

	    g.setColor(Color.GREEN);
	    if (path != null) {
		for (int i = 0; i < path.length; i++) {
		    g.drawPolygon(Projection.getTileBoundsPoly(path[i], 0));
		}
	    }

	    if (CURRENT_LOCATION != null) {
		if (CURRENT_LOCATION.area != null) {
		    RSTile[] tiles = CURRENT_LOCATION.area.getAllTiles();
		    for (int i = 0; i < tiles.length; i++) {
			Point minimapPoint = Projection.tileToMinimap(tiles[i]);
			if (Projection.isInMinimap(minimapPoint)) {
			    g.drawLine(minimapPoint.x, minimapPoint.y, minimapPoint.x + 1, minimapPoint.y + 1);
			}
		    }
		}
		if (CURRENT_LOCATION.locations.length > 1) {
		    x = 565;
		    y = 430;
		    g.setColor(Color.RED);
		    g.drawString("Last Tree: " + lastTile, x, y);
		    y += 15;
		    g.setColor(Color.YELLOW);
		    g.drawString("Current Tree: " + currentTile, x, y);
		    y += 15;
		    g.setColor(Color.GREEN);
		    g.drawString("Next Tree: " + nextTile, x, y);
		}

		if (ORDER_LIST.size() > 0) {
		    x = 30;
		    y = 310;
		    y -= (ORDER_LIST.size() - 1) * 20;
		    g.setFont(bold);
		    g.setColor(Color.WHITE);
		    g.drawString("World: " + WorldHopper.getWorld(), x, y);
		    y += 20;
		    for (int i = 0; i < ORDER_LIST.size(); i++) {
			if (ORDER_LIST.get(i).equals(CURRENT_LOCATION)) {
			    g.setColor(Color.GREEN);
			    g.drawImage(tree_image, x - 23, y - 17, null);
			    String message = toTitleCase(ORDER_LIST.get(i).toString());
			    if (CURRENT_LOCATION.equals(TREE_LOCATIONS.CUSTOM))
				message = message + " - " + TREE_LOCATIONS.CUSTOM.name;
			    g.drawString(message, x, y);
			    FontMetrics metrics = g.getFontMetrics(bold);
			    int width = metrics.stringWidth(message) + x;
			    if (banking) {
				g.drawImage(bank_image, width, y - 14, null);
			    } else if (dropping) {
				g.drawImage(drop_image, width, y - 14, null);
			    } else if (selling) {
				g.drawImage(sell_image, width, y - 14, null);
			    }
			} else {
			    g.setColor(Color.RED);
			    g.drawString(toTitleCase(ORDER_LIST.get(i).toString()), x, y);
			}
			y += 20;
		    }
		}
	    }
	}
    }

    @Override
    public void onEnd() {
	long time = System.currentTimeMillis() - startTime;
	int currentLVL = Skills.getActualLevel(SKILLS.WOODCUTTING);
	int xpGained = Skills.getXP(SKILLS.WOODCUTTING) - startXP;
	int logsPerHour = (int) (logs * 3600000D / (System.currentTimeMillis() - startTime));
	int xpPerHour = (int) (xpGained * 3600000D / (System.currentTimeMillis() - startTime));

	println("-----");
	println("Location: " + toTitleCase(CURRENT_LOCATION.toString()));
	println("Time: " + Timing.msToString(time));
	println("Woodcutting Level: " + currentLVL);
	println("XP Gained: " + addCommasToNumericString(Integer.toString(xpGained)) + " ("
		+ addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)");
	println("Logs: " + addCommasToNumericString(Integer.toString(logs)) + " ("
		+ addCommasToNumericString(Integer.toString(logsPerHour)) + "/hr)");
	println("-----");
    }

    public class gui extends JFrame {

	private JPanel contentPane;

	// Panel 1 Main
	private JPanel panel1;
	private JComboBox treeType;
	private JList treeList;
	private DefaultListModel treeModel;
	private JScrollPane treeScrollPane;
	private JList orderList;
	private DefaultListModel orderModel;
	private JScrollPane orderScrollPane;
	private JComboBox orderOption;
	private JList logList;
	private DefaultListModel logModel;
	private JScrollPane logScrollPane;
	private JComboBox logType;
	private JCheckBox chckbxPickupBirdNests;
	private JCheckBox chckbxOpenBirdNests;

	// Panel 2 Custom Tree
	private JPanel panel2;
	private JPanel customTreePanel;
	private JCheckBox chckbxUseCustomSetup;
	private JComboBox customTreeType;
	private JTextField customTileText;
	private JSpinner radiusSpinner;
	private JComboBox customFullOption;

	// Panel 3 Master/Slave
	private JPanel panel3;
	private JPanel masterSlavePanel;
	private JCheckBox chckbxMasterSlave;
	private JTextField masterUsernameText;
	private JTextField masterWorldText;
	private JTextField masterLocationText;
	private JList slaveList;
	private DefaultListModel slaveModel;
	private JScrollPane slaveScrollPane;
	private JTextField slaveText;
	private JCheckBox chckbxAutomaticTrading;
	private JPanel automaticPanel;
	private JRadioButton rdbtnRandomWorld;
	private JRadioButton rdbtnOriginalWorld;
	private JSpinner automaticLogSpinner;

	// loading/saving
	private ObjectOutputStream save;

	private ArrayList<String> getProfileNames(String name) {
	    File folder = new File(Util.getWorkingDirectory() + "/" + name);
	    if (!folder.exists())
		folder.mkdir();
	    ArrayList<String> files = new ArrayList<>();
	    for (File file : folder.listFiles()) {
		files.add(file.getName().replaceAll(".txt", ""));
	    }
	    return files;
	}

	public void saveSettings(String directory, String name) {
	    try {
		File folder = new File(Util.getWorkingDirectory() + "/" + directory);
		File file = new File(folder.toString() + "/" + name + ".txt");
		if (!folder.exists()) {
		    folder.mkdir();
		}

		if (file.createNewFile()) {
		    System.out.println("Added " + name + " to " + file.toString());
		} else {
		    System.out.println("Updated " + name + " at " + folder.toString());
		}
		if (file.exists()) {
		    Properties prop = new Properties();
		    // panel 1
		    String orders = "";
		    if (orderModel.size() == 1) {
			orders = "" + orderModel.get(0);
		    } else {
			for (int i = 0; i < orderModel.size(); i++) {
			    if ((i + 1) == orderModel.size()) {
				orders = orders + orderModel.get(i);
			    } else {
				orders = orders + orderModel.get(i) + " - ";
			    }
			}
		    }
		    prop.put("treeOrders", orders);
		    prop.put("pickupNest", String.valueOf(Boolean.valueOf(chckbxPickupBirdNests.isSelected())));
		    prop.put("openNest", String.valueOf(Boolean.valueOf(chckbxOpenBirdNests.isSelected())));
		    String logs = "";
		    if (logModel.size() == 1) {
			logs = "" + logModel.get(0);
		    } else {
			for (int i = 0; i < logModel.size(); i++) {
			    if ((i + 1) == logModel.size()) {
				logs = logs + logModel.get(i);
			    } else {
				logs = logs + logModel.get(i) + " - ";
			    }
			}
		    }
		    prop.put("logsToBank", logs);

		    // panel 2
		    prop.put("customSetup", String.valueOf(Boolean.valueOf(chckbxUseCustomSetup.isSelected())));
		    prop.put("treeType", customTreeType.getSelectedItem());
		    prop.put("customLocation", customTileText.getText());
		    prop.put("radius", Integer.toString((int) radiusSpinner.getValue()));
		    prop.put("whenFull", customFullOption.getSelectedItem());

		    // panel 3
		    prop.put("masterSlaveOption", String.valueOf(Boolean.valueOf(chckbxMasterSlave.isSelected())));
		    prop.put("masterUsername", masterUsernameText.getText());
		    prop.put("masterWorld", masterWorldText.getText());
		    prop.put("masterLocation", masterLocationText.getText());
		    String slaves = "";
		    if (slaveModel.size() == 1) {
			slaves = "" + slaveModel.get(0);
		    } else {
			for (int i = 0; i < slaveModel.size(); i++) {
			    if ((i + 1) == slaveModel.size()) {
				slaves = slaves + slaveModel.get(i);
			    } else {
				slaves = slaves + slaveModel.get(i) + " - ";
			    }
			}
		    }
		    prop.put("slaves", slaves);
		    prop.put("automaticTrading", String.valueOf(Boolean.valueOf(chckbxAutomaticTrading.isSelected())));
		    prop.put("automaticValue", Integer.toString((int) automaticLogSpinner.getValue()));
		    prop.put("originalWorld", String.valueOf(Boolean.valueOf(rdbtnOriginalWorld.isSelected())));
		    prop.put("randomWorld", String.valueOf(Boolean.valueOf(rdbtnRandomWorld.isSelected())));
		    prop.store(new FileOutputStream(file), null);
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	private void loadSettings(String directory, String name) {
	    try {
		File folderLocation = new File(Util.getWorkingDirectory() + "/" + directory);
		File newFile = new File(folderLocation.toString() + "/" + name + ".txt");

		if (newFile.exists()) {
		    Properties prop = new Properties();
		    prop.load(new FileInputStream(newFile));
		    // panel 1
		    orderModel.clear();
		    String temp1 = prop.getProperty("treeOrders");
		    String[] temp2 = temp1.split(" - ");
		    if (temp1.length() > 0) {
			for (String temp : temp2) {
			    orderModel.addElement(temp);
			}
		    }
		    chckbxPickupBirdNests.setSelected(Boolean.parseBoolean(prop.getProperty("pickupNest")));
		    chckbxOpenBirdNests.setSelected(Boolean.parseBoolean(prop.getProperty("openNest")));
		    logModel.clear();
		    temp1 = prop.getProperty("logsToBank");
		    temp2 = temp1.split(" - ");
		    if (temp1.length() > 0) {
			for (String temp : temp2) {
			    logModel.addElement(temp);
			}
		    }

		    // panel 2
		    chckbxUseCustomSetup.setSelected(Boolean.parseBoolean(prop.getProperty("customSetup")));
		    customTreeType.setSelectedItem(prop.getProperty("treeType"));
		    customTileText.setText(prop.getProperty("customLocation"));
		    radiusSpinner.setValue(Integer.parseInt(prop.getProperty("radius")));
		    customFullOption.setSelectedItem(prop.getProperty("whenFull"));

		    // panel 3
		    chckbxMasterSlave.setSelected(Boolean.parseBoolean(prop.getProperty("masterSlaveOption")));
		    masterUsernameText.setText(prop.getProperty("masterUsername"));
		    masterWorldText.setText(prop.getProperty("masterWorld"));
		    masterLocationText.setText(prop.getProperty("masterLocation"));
		    slaveModel.clear();
		    temp1 = prop.getProperty("slaves");
		    temp2 = temp1.split(" - ");
		    if (temp1.length() > 0) {
			for (String temp : temp2) {
			    slaveModel.addElement(temp);
			}
		    }
		    chckbxAutomaticTrading.setSelected(Boolean.parseBoolean(prop.getProperty("automaticTrading")));
		    automaticLogSpinner.setValue(Integer.parseInt(prop.getProperty("automaticValue")));
		    rdbtnOriginalWorld.setSelected(Boolean.parseBoolean(prop.getProperty("originalWorld")));
		    rdbtnRandomWorld.setSelected(Boolean.parseBoolean(prop.getProperty("randomWorld")));
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	public gui() {
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setBounds(100, 100, 450, 624);
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(contentPane);
	    contentPane.setLayout(null);

	    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	    tabbedPane.setBounds(25, 55, 385, 485);
	    contentPane.add(tabbedPane);

	    JLabel lblUsaChopper = new JLabel("USA Chopper");
	    lblUsaChopper.setFont(new Font("Britannic Bold", Font.PLAIN, 28));
	    lblUsaChopper.setHorizontalAlignment(SwingConstants.CENTER);
	    lblUsaChopper.setBounds(10, 12, 414, 32);
	    contentPane.add(lblUsaChopper);

	    panel1 = new JPanel();
	    panel1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	    tabbedPane.addTab("     Main     ", null, panel1, null);
	    panel1.setLayout(null);

	    treeModel = new DefaultListModel();

	    treeList = new JList(treeModel);
	    treeList.setVisibleRowCount(10);
	    treeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    treeList.setBounds(20, 54, 159, 137);
	    panel1.add(treeList);

	    treeScrollPane = new JScrollPane();
	    treeScrollPane.setViewportView(treeList);
	    panel1.add(treeScrollPane);
	    treeScrollPane.setBounds(treeList.getBounds());

	    treeType = new JComboBox();
	    treeType.setBounds(87, 23, 92, 20);
	    treeType.setModel(new DefaultComboBoxModel(
		    new String[] { "Tree", "Oak", "Willow", "Teak", "Maple", "Yew", "Magic" }));
	    panel1.add(treeType);
	    treeType.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    treeModel.clear();
		    ArrayList<TREE_LOCATIONS> trees = TREE_LOCATIONS
			    .getLocationsFor(treeType.getSelectedItem().toString());
		    ArrayList<String> locations = new ArrayList<String>();
		    for (int i = 0; i < trees.size(); i++) {
			locations.add(toTitleCase(trees.get(i).toString()));
		    }
		    Collections.sort(locations);
		    for (int i = 0; i < locations.size(); i++) {
			treeModel.addElement(locations.get(i));
		    }
		}
	    });

	    JLabel lblTreeType = new JLabel("Tree Type:");
	    lblTreeType.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblTreeType.setBounds(20, 23, 65, 20);
	    panel1.add(lblTreeType);

	    ArrayList<TREE_LOCATIONS> trees = TREE_LOCATIONS.getLocationsFor(treeType.getSelectedItem().toString());
	    ArrayList<String> locations = new ArrayList<String>();
	    for (int i = 0; i < trees.size(); i++) {
		locations.add(toTitleCase(trees.get(i).toString()));
	    }
	    Collections.sort(locations);
	    for (int i = 0; i < locations.size(); i++) {
		treeModel.addElement(locations.get(i));
	    }

	    JLabel lblTreeOrders = new JLabel("Tree Orders");
	    lblTreeOrders.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblTreeOrders.setHorizontalAlignment(SwingConstants.CENTER);
	    lblTreeOrders.setBounds(201, 23, 147, 20);
	    panel1.add(lblTreeOrders);

	    orderModel = new DefaultListModel();

	    orderList = new JList(orderModel);
	    orderList.setVisibleRowCount(10);
	    orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    orderList.setBounds(201, 54, 159, 137);
	    panel1.add(orderList);

	    orderScrollPane = new JScrollPane();
	    orderScrollPane.setViewportView(orderList);
	    panel1.add(orderScrollPane);
	    orderScrollPane.setBounds(orderList.getBounds());

	    JButton btnAdd = new JButton("Add");
	    btnAdd.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    orderModel.addElement(treeList.getSelectedValue());
		}
	    });
	    btnAdd.setBounds(46, 205, 110, 23);
	    panel1.add(btnAdd);

	    JButton btnAddClosest = new JButton("Add Closest");
	    btnAddClosest.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    orderModel.addElement(toTitleCase(
			    TREE_LOCATIONS.getClosestTree(treeType.getSelectedItem().toString()).toString()));
		}
	    });
	    btnAddClosest.setBounds(46, 240, 110, 23);
	    panel1.add(btnAddClosest);

	    JButton btnRemove = new JButton("Remove");
	    btnRemove.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    orderModel.removeElement(orderList.getSelectedValue());
		}
	    });
	    btnRemove.setBounds(195, 205, 88, 23);
	    panel1.add(btnRemove);

	    JButton btnClear = new JButton("Clear");
	    btnClear.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    orderModel.clear();
		}
	    });
	    btnClear.setBounds(287, 205, 75, 23);
	    panel1.add(btnClear);

	    orderOption = new JComboBox();
	    orderOption.setModel(new DefaultComboBoxModel(new String[] { "Random", "Closest" }));
	    orderOption.setBounds(287, 240, 75, 23);
	    panel1.add(orderOption);

	    JButton btnRandom = new JButton("Auto Order");
	    btnRandom.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    orderModel.clear();
		    if (orderOption.getSelectedIndex() == 0) {
			ArrayList<String> list = TREE_LOCATIONS.getRandomOrder();
			for (int i = 0; i < list.size(); i++) {
			    orderModel.addElement(list.get(i));
			}
		    } else if (orderOption.getSelectedIndex() == 1) {
			ArrayList<String> list = TREE_LOCATIONS.getClosestOrder();
			for (int i = 0; i < list.size(); i++) {
			    orderModel.addElement(list.get(i));
			}
		    }
		}
	    });
	    btnRandom.setFont(new Font("Tahoma", Font.PLAIN, 11));
	    btnRandom.setBounds(195, 240, 88, 23);
	    panel1.add(btnRandom);

	    JLabel lblOtherOptions = new JLabel("Other Options");
	    lblOtherOptions.setHorizontalAlignment(SwingConstants.CENTER);
	    lblOtherOptions.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblOtherOptions.setBounds(9, 282, 181, 20);
	    panel1.add(lblOtherOptions);

	    chckbxPickupBirdNests = new JCheckBox("Pickup Bird Nests");
	    chckbxPickupBirdNests.setBounds(30, 317, 137, 23);
	    panel1.add(chckbxPickupBirdNests);

	    chckbxOpenBirdNests = new JCheckBox("Open Bird Nests");
	    chckbxOpenBirdNests.setBounds(30, 343, 110, 23);
	    panel1.add(chckbxOpenBirdNests);

	    JLabel lblLogsToBank = new JLabel("Logs To Bank");
	    lblLogsToBank.setHorizontalAlignment(SwingConstants.CENTER);
	    lblLogsToBank.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblLogsToBank.setBounds(189, 282, 180, 20);
	    panel1.add(lblLogsToBank);

	    logType = new JComboBox();
	    logType.setModel(new DefaultComboBoxModel(
		    new String[] { "Magic", "Yew", "Maple", "Teak", "Willow", "Oak", "Logs" }));
	    logType.setBounds(280, 384, 80, 23);
	    panel1.add(logType);

	    logModel = new DefaultListModel();
	    logList = new JList(logModel);
	    logList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    logList.setBounds(201, 313, 159, 65);
	    panel1.add(logList);

	    logScrollPane = new JScrollPane();
	    logScrollPane.setViewportView(logList);
	    panel1.add(logScrollPane);
	    logScrollPane.setBounds(logList.getBounds());

	    JButton btnAdd_Logs = new JButton("Add");
	    btnAdd_Logs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String log = logType.getSelectedItem().toString();
		    if (!log.equalsIgnoreCase("logs"))
			log = log + " logs";
		    boolean add = true;
		    for (int i = 0; i < logModel.size(); i++) {
			if (logModel.get(i).equals(log)) {
			    add = false;
			    break;
			}
		    }
		    if (add)
			logModel.addElement(log);
		}
	    });
	    btnAdd_Logs.setBounds(201, 384, 75, 23);
	    panel1.add(btnAdd_Logs);

	    JButton btnRemove_Logs = new JButton("Remove");
	    btnRemove_Logs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    logModel.removeElement(logList.getSelectedValue());
		}
	    });
	    btnRemove_Logs.setBounds(201, 411, 159, 23);
	    panel1.add(btnRemove_Logs);

	    JSeparator separator = new JSeparator();
	    separator.setBounds(10, 11, 359, 1);
	    panel1.add(separator);

	    JSeparator separator_1 = new JSeparator();
	    separator_1.setBounds(10, 268, 359, 1);
	    panel1.add(separator_1);

	    JSeparator separator_2 = new JSeparator();
	    separator_2.setOrientation(SwingConstants.VERTICAL);
	    separator_2.setBounds(10, 11, 1, 258);
	    panel1.add(separator_2);

	    JSeparator separator_3 = new JSeparator();
	    separator_3.setOrientation(SwingConstants.VERTICAL);
	    separator_3.setBounds(369, 11, 1, 258);
	    panel1.add(separator_3);

	    JSeparator separator_4 = new JSeparator();
	    separator_4.setOrientation(SwingConstants.VERTICAL);
	    separator_4.setBounds(189, 11, 2, 258);
	    panel1.add(separator_4);

	    JSeparator separator_5 = new JSeparator();
	    separator_5.setBounds(10, 198, 359, 1);
	    panel1.add(separator_5);

	    JSeparator separator_6 = new JSeparator();
	    separator_6.setBounds(10, 234, 359, 1);
	    panel1.add(separator_6);

	    JSeparator separator_7 = new JSeparator();
	    separator_7.setBounds(10, 280, 359, 1);
	    panel1.add(separator_7);

	    JSeparator separator_8 = new JSeparator();
	    separator_8.setOrientation(SwingConstants.VERTICAL);
	    separator_8.setBounds(369, 280, 1, 166);
	    panel1.add(separator_8);

	    JSeparator separator_9 = new JSeparator();
	    separator_9.setOrientation(SwingConstants.VERTICAL);
	    separator_9.setBounds(10, 280, 1, 166);
	    panel1.add(separator_9);

	    JSeparator separator_10 = new JSeparator();
	    separator_10.setBounds(10, 445, 359, 1);
	    panel1.add(separator_10);

	    JSeparator separator_11 = new JSeparator();
	    separator_11.setOrientation(SwingConstants.VERTICAL);
	    separator_11.setBounds(189, 280, 2, 166);
	    panel1.add(separator_11);

	    JSeparator separator_12 = new JSeparator();
	    separator_12.setBounds(10, 303, 359, 1);
	    panel1.add(separator_12);

	    panel2 = new JPanel();
	    panel2.setBackground(Color.WHITE);
	    panel2.setForeground(Color.BLACK);
	    panel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	    tabbedPane.addTab("     Custom     ", null, panel2, null);
	    panel2.setLayout(null);

	    chckbxUseCustomSetup = new JCheckBox("Use Custom Setup");
	    chckbxUseCustomSetup.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    customTreePanel.setVisible(chckbxUseCustomSetup.isSelected());
		}
	    });
	    chckbxUseCustomSetup.setBackground(Color.WHITE);
	    chckbxUseCustomSetup.setBounds(10, 7, 130, 23);
	    panel2.add(chckbxUseCustomSetup);

	    customTreePanel = new JPanel();
	    customTreePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
	    customTreePanel.setBackground(Color.WHITE);
	    customTreePanel.setBounds(20, 37, 345, 230);
	    customTreePanel.setVisible(false);
	    panel2.add(customTreePanel);
	    customTreePanel.setLayout(null);

	    customTreeType = new JComboBox();
	    customTreeType.setBounds(136, 48, 86, 20);
	    customTreePanel.add(customTreeType);
	    customTreeType.setModel(new DefaultComboBoxModel(
		    new String[] { "Tree", "Evergreen", "Oak", "Willow", "Teak", "Maple tree", "Yew", "Magic tree" }));

	    JLabel lblTree = new JLabel("Tree Type:");
	    lblTree.setBounds(55, 51, 63, 14);
	    customTreePanel.add(lblTree);
	    lblTree.setFont(new Font("Tahoma", Font.BOLD, 11));

	    JLabel lblCustomSetup = new JLabel("Custom Tree Setup");
	    lblCustomSetup.setBounds(0, 11, 345, 14);
	    customTreePanel.add(lblCustomSetup);
	    lblCustomSetup.setHorizontalAlignment(SwingConstants.CENTER);
	    lblCustomSetup.setFont(new Font("Tahoma", Font.BOLD, 12));

	    JLabel lblTile = new JLabel("Tile:");
	    lblTile.setBounds(90, 94, 28, 14);
	    customTreePanel.add(lblTile);
	    lblTile.setFont(new Font("Tahoma", Font.BOLD, 11));

	    customTileText = new JTextField();
	    customTileText.setBounds(136, 91, 86, 20);
	    customTreePanel.add(customTileText);
	    customTileText.setEditable(false);
	    customTileText.setFont(new Font("Tahoma", Font.PLAIN, 11));
	    customTileText.setHorizontalAlignment(SwingConstants.CENTER);
	    customTileText.setText("(9999, 9999, 0)");
	    customTileText.setColumns(10);

	    JButton tileRefreshButton = new JButton("Refresh");
	    tileRefreshButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    customTileText.setText(Player.getPosition().toString());
		}
	    });
	    tileRefreshButton.setBounds(232, 90, 76, 23);
	    customTreePanel.add(tileRefreshButton);

	    JLabel lblRadius = new JLabel("Radius:");
	    lblRadius.setBounds(74, 138, 44, 14);
	    customTreePanel.add(lblRadius);
	    lblRadius.setFont(new Font("Tahoma", Font.BOLD, 11));

	    radiusSpinner = new JSpinner();
	    radiusSpinner.setBounds(136, 135, 41, 20);
	    customTreePanel.add(radiusSpinner);
	    radiusSpinner.setForeground(Color.WHITE);
	    radiusSpinner.setBackground(Color.WHITE);
	    radiusSpinner.setFont(new Font("Tahoma", Font.PLAIN, 11));
	    radiusSpinner.setModel(new SpinnerNumberModel(new Integer(3), null, null, new Integer(2)));

	    JLabel lblFullInventory = new JLabel("When Full:");
	    lblFullInventory.setBounds(58, 181, 60, 14);
	    customTreePanel.add(lblFullInventory);
	    lblFullInventory.setFont(new Font("Tahoma", Font.BOLD, 11));

	    customFullOption = new JComboBox();
	    customFullOption.setBounds(136, 178, 86, 20);
	    customTreePanel.add(customFullOption);
	    customFullOption.setModel(new DefaultComboBoxModel(new String[] { "Drop", "Bank", "Sell" }));

	    JSeparator separator_18 = new JSeparator();
	    separator_18.setBounds(124, 36, 2, 175);
	    customTreePanel.add(separator_18);
	    separator_18.setOrientation(SwingConstants.VERTICAL);

	    JSeparator separator_13 = new JSeparator();
	    separator_13.setBounds(16, 35, 319, 2);
	    customTreePanel.add(separator_13);

	    JSeparator separator_14 = new JSeparator();
	    separator_14.setBounds(16, 211, 319, 2);
	    customTreePanel.add(separator_14);

	    JSeparator separator_15 = new JSeparator();
	    separator_15.setBounds(16, 168, 319, 2);
	    customTreePanel.add(separator_15);

	    JSeparator separator_16 = new JSeparator();
	    separator_16.setBounds(16, 123, 319, 2);
	    customTreePanel.add(separator_16);

	    JSeparator separator_17 = new JSeparator();
	    separator_17.setBounds(16, 79, 319, 2);
	    customTreePanel.add(separator_17);

	    panel3 = new JPanel();
	    panel3.setBackground(Color.BLUE);
	    panel3.setForeground(Color.BLACK);
	    panel3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	    tabbedPane.addTab("     Master/Slave     ", null, panel3, null);
	    panel3.setLayout(null);

	    chckbxMasterSlave = new JCheckBox("Use Master/Slave");
	    chckbxMasterSlave.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    masterSlavePanel.setVisible(chckbxMasterSlave.isSelected());
		}
	    });
	    chckbxMasterSlave.setFont(new Font("Tahoma", Font.BOLD, 11));
	    chckbxMasterSlave.setForeground(Color.WHITE);
	    chckbxMasterSlave.setBackground(Color.BLUE);
	    chckbxMasterSlave.setBounds(10, 7, 134, 23);
	    panel3.add(chckbxMasterSlave);

	    masterSlavePanel = new JPanel();
	    masterSlavePanel.setBorder(new LineBorder(new Color(255, 255, 255), 2));
	    masterSlavePanel.setBounds(35, 33, 313, 418);
	    masterSlavePanel.setVisible(false);
	    panel3.add(masterSlavePanel);
	    masterSlavePanel.setBackground(Color.BLUE);
	    masterSlavePanel.setLayout(null);

	    JLabel lblMaster = new JLabel("Master");
	    lblMaster.setBounds(0, 6, 313, 16);
	    masterSlavePanel.add(lblMaster);
	    lblMaster.setForeground(Color.WHITE);
	    lblMaster.setHorizontalAlignment(SwingConstants.CENTER);
	    lblMaster.setFont(new Font("Tahoma", Font.BOLD, 14));

	    JLabel lblMasterName = new JLabel("Username:");
	    lblMasterName.setBounds(25, 32, 67, 14);
	    masterSlavePanel.add(lblMasterName);
	    lblMasterName.setForeground(Color.WHITE);
	    lblMasterName.setFont(new Font("Tahoma", Font.BOLD, 11));

	    masterUsernameText = new JTextField();
	    masterUsernameText.setBounds(99, 29, 93, 20);
	    masterSlavePanel.add(masterUsernameText);
	    masterUsernameText.setHorizontalAlignment(SwingConstants.CENTER);
	    masterUsernameText.setColumns(10);

	    JLabel lblMasterWorld = new JLabel("World:");
	    lblMasterWorld.setBounds(50, 60, 42, 14);
	    masterSlavePanel.add(lblMasterWorld);
	    lblMasterWorld.setForeground(Color.WHITE);
	    lblMasterWorld.setFont(new Font("Tahoma", Font.BOLD, 11));

	    masterWorldText = new JTextField();
	    masterWorldText.setBounds(99, 57, 40, 20);
	    masterSlavePanel.add(masterWorldText);
	    masterWorldText.setHorizontalAlignment(SwingConstants.CENTER);
	    masterWorldText.setText("81");
	    masterWorldText.setColumns(10);

	    JLabel lblTradeLocation = new JLabel("Location:");
	    lblTradeLocation.setBounds(35, 88, 58, 14);
	    masterSlavePanel.add(lblTradeLocation);
	    lblTradeLocation.setForeground(Color.WHITE);
	    lblTradeLocation.setFont(new Font("Tahoma", Font.BOLD, 11));

	    masterLocationText = new JTextField();
	    masterLocationText.setBounds(99, 85, 93, 20);
	    masterSlavePanel.add(masterLocationText);
	    masterLocationText.setHorizontalAlignment(SwingConstants.CENTER);
	    masterLocationText.setText("(1, 1, 0)");
	    masterLocationText.setColumns(10);

	    JButton locationRefresh = new JButton("Current");
	    locationRefresh.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    masterLocationText.setText(Player.getPosition().toString());
		}
	    });
	    locationRefresh.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    locationRefresh.setBounds(210, 85, 70, 20);
	    masterSlavePanel.add(locationRefresh);

	    JButton worldRefresh = new JButton("Current");
	    worldRefresh.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    masterWorldText.setText(Integer.toString(WorldHopper.getWorld()));
		}
	    });
	    worldRefresh.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    worldRefresh.setBounds(210, 57, 70, 20);
	    masterSlavePanel.add(worldRefresh);

	    JButton usernameRefresh = new JButton("Current");
	    usernameRefresh.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    masterUsernameText.setText(Player.getRSPlayer().getName());
		}
	    });
	    usernameRefresh.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    usernameRefresh.setBounds(210, 29, 70, 20);
	    masterSlavePanel.add(usernameRefresh);

	    JLabel lblSlaveRunescapeUsernames = new JLabel("Slave(s)");
	    lblSlaveRunescapeUsernames.setBounds(0, 116, 313, 16);
	    masterSlavePanel.add(lblSlaveRunescapeUsernames);
	    lblSlaveRunescapeUsernames.setForeground(Color.WHITE);
	    lblSlaveRunescapeUsernames.setHorizontalAlignment(SwingConstants.CENTER);
	    lblSlaveRunescapeUsernames.setFont(new Font("Tahoma", Font.BOLD, 14));

	    slaveModel = new DefaultListModel();

	    slaveList = new JList(slaveModel);
	    slaveList.setVisibleRowCount(10);
	    slaveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    slaveList.setBounds(69, 163, 95, 86);
	    masterSlavePanel.add(slaveList);

	    slaveScrollPane = new JScrollPane();
	    slaveScrollPane.setViewportView(slaveList);
	    masterSlavePanel.add(slaveScrollPane);
	    slaveScrollPane.setBounds(slaveList.getBounds());

	    JButton addSlaveButton = new JButton("Add User");
	    addSlaveButton.setBounds(69, 139, 95, 20);
	    masterSlavePanel.add(addSlaveButton);
	    addSlaveButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    addSlaveButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    slaveModel.addElement(slaveText.getText());
		}
	    });
	    addSlaveButton.setBackground(UIManager.getColor("Button.background"));

	    JButton removeSlaveButton = new JButton("Remove User");
	    removeSlaveButton.setBounds(69, 254, 95, 20);
	    masterSlavePanel.add(removeSlaveButton);
	    removeSlaveButton.setFont(new Font("Tahoma", Font.PLAIN, 10));
	    removeSlaveButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    slaveModel.removeElement(slaveList.getSelectedValue());
		}
	    });
	    removeSlaveButton.setBackground(UIManager.getColor("Button.background"));

	    slaveText = new JTextField();
	    slaveText.setBounds(168, 139, 85, 20);
	    masterSlavePanel.add(slaveText);
	    slaveText.setHorizontalAlignment(SwingConstants.CENTER);
	    slaveText.setColumns(10);

	    JSeparator separator_19 = new JSeparator();
	    separator_19.setBounds(0, 280, 313, 2);
	    masterSlavePanel.add(separator_19);
	    separator_19.setForeground(Color.WHITE);

	    JSeparator separator_20 = new JSeparator();
	    separator_20.setBounds(0, 111, 313, 2);
	    masterSlavePanel.add(separator_20);
	    separator_20.setForeground(Color.WHITE);

	    chckbxAutomaticTrading = new JCheckBox("Automatic Trading");
	    chckbxAutomaticTrading.setBounds(25, 289, 171, 20);
	    masterSlavePanel.add(chckbxAutomaticTrading);
	    chckbxAutomaticTrading.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    automaticPanel.setVisible(chckbxAutomaticTrading.isSelected());
		}
	    });
	    chckbxAutomaticTrading.setForeground(Color.WHITE);
	    chckbxAutomaticTrading.setBackground(Color.BLUE);
	    chckbxAutomaticTrading.setFont(new Font("Tahoma", Font.BOLD, 14));

	    automaticPanel = new JPanel();
	    automaticPanel.setBounds(25, 312, 260, 94);
	    masterSlavePanel.add(automaticPanel);
	    automaticPanel.setBackground(Color.WHITE);
	    automaticPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	    automaticPanel.setVisible(false);
	    automaticPanel.setLayout(null);

	    rdbtnRandomWorld = new JRadioButton("Random World");
	    rdbtnRandomWorld.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (rdbtnRandomWorld.isSelected())
			rdbtnOriginalWorld.setSelected(false);
		}
	    });
	    rdbtnRandomWorld.setBackground(Color.WHITE);
	    rdbtnRandomWorld.setBounds(149, 71, 110, 16);
	    automaticPanel.add(rdbtnRandomWorld);

	    rdbtnOriginalWorld = new JRadioButton("Original World");
	    rdbtnOriginalWorld.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (rdbtnOriginalWorld.isSelected())
			rdbtnRandomWorld.setSelected(false);
		}
	    });
	    rdbtnOriginalWorld.setBackground(Color.WHITE);
	    rdbtnOriginalWorld.setBounds(149, 50, 110, 16);
	    automaticPanel.add(rdbtnOriginalWorld);
	    rdbtnOriginalWorld.setSelected(true);

	    JLabel lblAfterTradingHop = new JLabel("After trading, change to...");
	    lblAfterTradingHop.setBounds(10, 50, 138, 16);
	    automaticPanel.add(lblAfterTradingHop);

	    JLabel lblBankedLogs = new JLabel("banked logs.");
	    lblBankedLogs.setBounds(179, 14, 76, 16);
	    automaticPanel.add(lblBankedLogs);

	    JLabel lblTradeMasterAfter = new JLabel("Trade Master after...");
	    lblTradeMasterAfter.setBounds(10, 14, 109, 16);
	    automaticPanel.add(lblTradeMasterAfter);

	    automaticLogSpinner = new JSpinner();
	    automaticLogSpinner.setBounds(119, 11, 50, 20);
	    automaticPanel.add(automaticLogSpinner);
	    automaticLogSpinner
		    .setModel(new SpinnerNumberModel(new Integer(500), new Integer(1), null, new Integer(50)));

	    JSeparator separator_23 = new JSeparator();
	    separator_23.setBounds(2, 43, 255, 2);
	    automaticPanel.add(separator_23);

	    JButton btnStart = new JButton("Start");
	    btnStart.setFont(new Font("Tahoma", Font.BOLD, 11));
	    btnStart.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    nest = chckbxPickupBirdNests.isSelected();
		    openNest = chckbxOpenBirdNests.isSelected();
		    if (nest) {
			println("We are picking up Bird Nests");
		    } else {
			println("We are NOT picking up Bird Nests");
		    }
		    if (openNest) {
			println("We are opening Bird Nests");
		    } else {
			println("We are NOT opening Bird Nests");
		    }
		    if (chckbxUseCustomSetup.isSelected()) {
			customOption = true;
			String name = customTreeType.getSelectedItem().toString();
			RSTile pos = Player.getPosition();
			if (pos != null) {
			    int radius = (int) radiusSpinner.getValue();
			    TREE_LOCATIONS.setCustom(name, 1, new RSTile[] { pos }, new RSArea(pos, radius));
			    ORDER_LIST.add(TREE_LOCATIONS.CUSTOM);
			    int index = customFullOption.getSelectedIndex();
			    if (index == 0) {
				dropping = true;
			    } else if (index == 1) {
				banking = true;
			    } else if (index == 2) {
				selling = true;
			    }
			}
		    } else {
			if (orderModel.size() > 0) {
			    println("Tree Orders: ");
			    for (int i = 0; i < orderModel.size(); i++) {
				String tree = orderModel.get(i).toString();
				TREE_LOCATIONS loc = TREE_LOCATIONS.getLocation(tree);
				println(tree);
				ORDER_LIST.add(loc);
			    }
			    println("---");
			}
		    }
		    if (logModel.size() > 0) {
			LOGS_TO_BANK = new String[logModel.size()];
			println("Banking:");
			for (int i = 0; i < logModel.size(); i++) {
			    LOGS_TO_BANK[i] = "" + logModel.get(i);
			    println(LOGS_TO_BANK[i]);
			}
			println("---");
		    }
		    if (chckbxMasterSlave.isSelected()) {
			masterOption = true;
			masterUsername = masterUsernameText.getText();
			masterWorld = Integer.parseInt(masterWorldText.getText());
			String text = masterLocationText.getText().replaceAll("\\(", "").replaceAll("\\)", "")
				.replaceAll("\\s", "");
			String[] split = text.split(",");
			masterLocation = new RSTile(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
				Integer.parseInt(split[2]));

			slaveUsernames = new String[slaveModel.size()];
			for (int i = 0; i < slaveModel.size(); i++) {
			    slaveUsernames[i] = (String) slaveModel.get(i);
			}

			automaticTrading = chckbxAutomaticTrading.isSelected();
			automaticLogCount = (int) automaticLogSpinner.getValue();
			originalWorld = rdbtnOriginalWorld.isSelected();
		    }
		    println("Starting!");
		    gui_is_up = false;
		    g.dispose();
		}
	    });
	    btnStart.setBounds(165, 551, 108, 23);
	    contentPane.add(btnStart);

	    JButton loadButton = new JButton("Load Settings");
	    loadButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ArrayList<?> files = getProfileNames("USA Wood Chopper");
		    String[] profiles = (String[]) files.toArray(new String[files.size()]);
		    JComboBox combo = new JComboBox(profiles);
		    JOptionPane.showMessageDialog(null, combo, "Select a saved profile", JOptionPane.QUESTION_MESSAGE);
		    loadSettings("USA Wood Chopper", combo.getSelectedItem().toString());
		}
	    });
	    loadButton.setBounds(30, 551, 108, 23);
	    contentPane.add(loadButton);

	    JButton saveButton = new JButton("Save Settings");
	    saveButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String name = JOptionPane.showInputDialog("Enter a name to save the profile");
		    saveSettings("USA Wood Chopper", name);
		}
	    });
	    saveButton.setBounds(300, 551, 108, 23);
	    contentPane.add(saveButton);
	}
    }

    @Override
    public void mouseClicked(Point p, int button, boolean isBot) {
	Rectangle rect = new Rectangle(564, 318, 15, 15);
	if (rect.contains(p) && !isBot) {
	    println("Transfer");
	    transfer = true;
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
    public void serverMessageReceived(String arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void tradeRequestReceived(String username) {
	for (String slave : slaveUsernames) {
	    if (slave.equalsIgnoreCase(username)) {
		tradeSlave = true;
	    }
	}
    }
}