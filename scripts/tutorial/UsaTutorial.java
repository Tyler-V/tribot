package scripts.tutorial;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Magic;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSAnimableEntity;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
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

import scripts.api.v1.api.entity.Entity;
import scripts.api.v1.api.entity.Targets;
import scripts.api.v1.api.entity.UsaObjects;
import scripts.api.v1.api.entity.Entity.Types;
import scripts.api.v1.api.walking.Walk;
import scripts.usa.api.GameSettings.GameSettings;
import scripts.usa.api.GameSettings.RunOptions;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.conditionss.Conditional;
import scripts.usa.api.conditionss.Conditions;
import scripts.usa.api.interfaces.Menu;
import scripts.usa.api.tabs.Tab;
import scripts.usa.api.wagu.Block;
import scripts.usa.api.wagu.Board;
import scripts.usa.api.wagu.Table;
import scripts.usa.api.web.captcha.AccountCreator;
import scripts.usa.api.web.captcha.AccountCreator.Result;
import scripts.usa.api.web.methods.Web;;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Tutorial Island")
public class UsaTutorial extends Script implements Painting, Ending, MouseActions, MessageListening07 {

    // GENERAL
    public static String version = "v4.6";
    private boolean PaintNPC = true;
    private final Image background = Web.getImage("http://i.imgur.com/cJfpRJt.png");
    private final Image logoutImage = Web.getImage("http://i.imgur.com/5IMYF4Z.png");
    private String status = "";
    private boolean run = true;
    private long totalTime;
    private long startTime;
    private boolean FORCE_LOGOUT = false;
    // STRATEGIES
    private static Gender gender;
    private static int ignoreDesignOptions;
    private static int minDesignClicks;
    private static int maxDesignClicks;
    private static boolean useKeyboard;
    private static boolean catchTwoShrimp;
    private static RunOptions runOption;
    private static boolean wieldFromInventory;
    private static int hoverSleep;
    private static int continueChatSleep;
    private static int generalSleep;
    private static RSTile[] questGuidePath;
    private static RSTile[] brotherBracePath;
    private static RSTile[] magicInstructorPath;
    // OTHER OPTIONS
    private static Location travelLocation;
    private static ItemOptions itemOption;
    private static LogOptions logOption;
    private static boolean randomizeProfile;
    private static boolean guiMusicOption;
    private static boolean guiBrightnessOption;
    private static boolean guiRoofOption;
    private static boolean turnOffMusic;
    private static boolean setMaximumBrightness;
    private static boolean removeRoofs;
    private static Account currentAccount = null;
    private static int accountsCompleted = 0;
    // USE ACCOUNTS
    private static boolean useAccounts;
    private static ArrayList<Account> accounts = new ArrayList<Account>();
    private static int loadedAccounts;
    // CREATE ACCOUNTS
    private static boolean createAccounts;
    private static String captchaKey;
    private static int amountToCreate;
    private static int createAge;
    private static int createIndex;
    private static String createDisplay;
    private static String createEmail;
    private static String createPassword;
    // SAVING ACCOUNTS
    private ArrayList<Account> createdAccounts = new ArrayList<Account>();
    // PAINTING
    private long CONDITION_TIMER = 0;
    private boolean PAINT_LOGOUT_IMAGE = false;
    // GUI
    private static boolean gui_is_up = true;
    private static GUI g = new GUI();

    public void run() {
	super.setLoginBotState(false);
	g.setVisible(true);
	status = "Setting up...";
	while (gui_is_up)
	    sleep(100);
	if (!useAccounts && !createAccounts) {
	    super.setLoginBotState(true);
	    status = "Logging in...";
	    while (Login.getLoginState() != Login.STATE.INGAME)
		sleep(100);
	    String username = Player.getRSPlayer().getName();
	    if (username != null) {
		println("Starting Tutorial Island on \"" + username + "\"");
	    } else {
		println("Starting Tutorial Island.");
	    }
	} else {
	    super.setLoginBotState(false);
	}
	status = "Starting";
	Random r = new Random();
	totalTime = System.currentTimeMillis();
	startTime = System.currentTimeMillis();
	while (run) {
	    if (Camera.getCameraAngle() < 100)
		Camera.setCameraAngle(100);
	    if (login(currentAccount)) {
		if ((Variables.LUMBRIDGE_SPAWN.contains(Player.getPosition())
			|| (travelLocation.getTile() != null && Walk.isTileOnMinimap(travelLocation.getTile())))
			|| (currentAccount == null && (useAccounts || createAccounts))) {
		    String username = null;
		    if (Login.getLoginState() == Login.STATE.INGAME)
			username = Player.getRSPlayer().getName();
		    if ((currentAccount == null && !useAccounts && !createAccounts)
			    || (currentAccount != null && (useAccounts || createAccounts))) {
			if (username != null) {
			    println("\"" + username + "\" has completed Tutorial Island in "
				    + Timing.msToString(System.currentTimeMillis() - startTime) + ".");
			} else {
			    println("Completed Tutorial Island in "
				    + Timing.msToString(System.currentTimeMillis() - startTime) + ".");
			}
			accountsCompleted++;
			postTutorialActions();
		    }
		    if (useAccounts) {
			if (accounts.size() == 0) {
			    println("Successfully completed Tutorial Island on all accounts!");
			    run = false;
			    break;
			}
			startTime = System.currentTimeMillis();
			currentAccount = accounts.get(0);
			if (currentAccount != null)
			    println("Starting Tutorial Island on \"" + currentAccount.getEmail() + "\" (Account "
				    + ((loadedAccounts - accounts.size()) + 1) + " of " + loadedAccounts + ").");
		    } else if (createAccounts) {
			if (createdAccounts.size() >= amountToCreate) {
			    println("Successfully completed Tutorial Island on all created accounts!");
			    run = false;
			    break;
			}
			startTime = System.currentTimeMillis();
			currentAccount = createAccount();
			if (currentAccount != null)
			    println("Starting Tutorial Island on \"" + currentAccount.getDisplayName() + "\" (Account "
				    + (createdAccounts.size() + 1) + " of " + amountToCreate + ").");
		    } else {
			run = false;
			break;
		    }
		    if (currentAccount != null) {
			if (randomizeProfile)
			    generateRandomProfile();
			createdAccounts.add(currentAccount);
			removeRoofs = guiRoofOption;
			setMaximumBrightness = guiBrightnessOption;
			turnOffMusic = guiMusicOption;
		    }
		} else if (isOptionAvailable("Yes.")) {
		    status = "Choosing Yes";
		    NPCChat.selectOption("Yes.", true);
		} else if (isOptionAvailable("I am brand new! This is my first time here.")) {
		    status = "What's your experience?";
		    int random = General.random(1, 3);
		    if (useKeyboard) {
			Keyboard.typeSend("" + random);
		    } else {
			if (random == 1) {
			    NPCChat.selectOption("I am brand new! This is my first time here.", true);
			} else if (random == 2) {
			    NPCChat.selectOption("I've played in the past, but not recently.", true);
			} else if (random == 3) {
			    NPCChat.selectOption("I am an experienced player.", true);
			}
		    }
		} else if (isContinueChatUp()) {
		    continueChat();
		} else if (Variables.RUNESCAPE_GUIDE_AREA.contains(Player.getPosition())) {
		    if (randomizeCharacter()) {
			status = "Character is randomized!";
		    } else if (currentTask("Getting started") || currentTask("RuneScape Guide to continue.")
			    || currentTask("display your player controls")) {
			status = "Runescape Guide";
			if (GameSettings.isOptionMenuVisible()) {
			    Tab.open(TABS.OPTIONS);
			    if ((removeRoofs || turnOffMusic || setMaximumBrightness)) {
				Tab.open(TABS.OPTIONS);
				ArrayList<String> options = new ArrayList<String>();
				if (setMaximumBrightness)
				    options.add("brightness");
				if (turnOffMusic)
				    options.add("music");
				if (removeRoofs)
				    options.add("roof");
				Collections.shuffle(options);
				for (String option : options) {
				    if (option.equals("brightness")) {
					setMaximumBrightness = !GameSettings.setMaxBrightness();
				    } else if (option.equals("music")) {
					turnOffMusic = !GameSettings.setVolume();
				    } else if (option.equals("roof")) {
					removeRoofs = !GameSettings.setHideRoofs();
				    }
				}
			    }
			}
			Entity.click(Types.RSNPC, "Talk-to", new Targets("RuneScape Guide"), null,
				Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
		    } else if (currentTask("click on the door")) {
			status = "Opening Door";
			RSTile door = new RSTile(3097, 3107, 0);
			if (open("Open", "Door", door, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
		    }
		} else if (Variables.SURVIVAL_EXPERT_AREA.contains(Player.getPosition())) {
		    if (currentTask("Interacting with scenery") || currentTask("Survival Expert to continue")
			    || currentTask("Moving around")) {
			status = "Talk to Survival Expert";
			Entity.click(Types.RSNPC, "Talk-to", new Targets("Survival Expert"), null,
				Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
		    } else if (currentTask("Viewing the items that you were given.")) {
			status = "Opening Inventory";
			openInventory();
		    } else if (currentTask("Cut down a tree")
			    || ((currentTask("Cooking your shrimp.") || currentTask("Burning your shrimp."))
				    && Objects.find(30, "Fire").length == 0 && Inventory.getCount("Logs") == 0)) {
			status = "Chopping Tree";
			Entity.click(Types.RSObject, "Chop down", new Targets("Tree"),
				new Conditional(Conditions.Moving(), Conditions.Animating(true)),
				Conditions.Animating(true), Conditions.InventoryCountGreaterThan("Logs", 0), 3000);
		    } else if (currentTask("Making a fire")
			    || ((currentTask("Cooking your shrimp.") || currentTask("Burning your shrimp."))
				    && Objects.find(30, "Fire").length == 0 && Inventory.getCount("Logs") > 0)) {
			if (!useItemOnItem("Logs", "Tinderbox", Conditions.InventoryDoesNotContain("Logs")))
			    Walk.walkToTile(Player.getPosition(), 3, 0, null);
		    } else if (currentTask("You gained some experience.")) {
			status = "Opening Stats";
			Tab.open(TABS.STATS);
		    } else if (currentTask("Catch some Shrimp.")) {
			Tab.open(TABS.INVENTORY);
			int amount = 1;
			if (catchTwoShrimp)
			    amount = 2;
			status = "Fishing for " + amount + " Shrimp";
			int count = Inventory.getCount("Raw shrimps");
			Entity.shouldReach = false;
			Entity.click(Types.RSNPC, "Net", new Targets("Fishing spot"),
				new Conditional(Conditions.ActivePlayer(), Conditions.Animating(true)),
				Conditions.ActivePlayer(), Conditions.InventoryCountGreaterThan("Raw shrimps", count),
				5000);
			Entity.shouldReach = true;
		    } else if (currentTask("Cooking your shrimp.") || currentTask("Burning your shrimp.")) {
			final int count = Inventory.getCount("Raw shrimps");
			if (count > 0) {
			    status = "Cooking Raw shrimps";
			    Entity.shouldReach = false;
			    if (Entity.click(Types.RSItem, "Use", new Targets("Raw shrimps"), null, null,
				    Conditions.IsUptext("Use Raw shrimps ->"), 2000)) {
				Entity.click(Types.RSObject, "Use Raw shrimps ->", new Targets("Fire"),
					new Conditional(Conditions.Moving(), Conditions.Animating(true)),
					Conditions.ActivePlayer(), Conditions.InventoryDoesNotContain("Raw shrimps"),
					5000);
			    }
			    Entity.shouldReach = true;
			} else {
			    status = "Fishing for an additional Shrimp";
			    Entity.click(Types.RSNPC, "Net", new Targets("Fishing spot"),
				    new Conditional(Conditions.ActivePlayer(), Conditions.Animating(true)),
				    Conditions.ActivePlayer(),
				    Conditions.InventoryCountGreaterThan("Raw shrimps", count), 5000);
			}
		    } else if (currentTask("cooked your first RuneScape meal")
			    || currentTask("Follow the path until you get to the door")) {
			RSTile gate = new RSTile(3090, 3092, 0);
			if (PathFinding.canReach(gate, false)) {
			    status = "Opening Gate";
			    if (open("Open", "Gate", gate, null))
				Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(gate, false), 3000);
			}
		    }
		} else if (Variables.MASTER_CHEF_AREA.contains(Player.getPosition())) {
		    if (currentTask("Talk to the chef") || currentTask("Follow the path until you get to the door")) {
			RSTile gate = new RSTile(3090, 3092, 0);
			if (PathFinding.canReach(gate, false)) {
			    status = "Opening Gate";
			    if (open("Open", "Gate", gate, null))
				Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(gate, false), 3000);
			} else {
			    RSTile door = new RSTile(3079, 3084, 0);
			    if (PathFinding.canReach(door, false)) {
				status = "Opening Door";
				if (open("Open", "Door", door, null))
				    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
			    } else {
				status = "Talk to Master Chef";
				Entity.click(Types.RSNPC, "Talk-to", new Targets("Master Chef"), null,
					Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
			    }
			}
		    } else if (currentTask("Making dough")) {
			if (r.nextBoolean()) {
			    status = "Using Bucket of water";
			    useItemOnItem("Bucket of water", "Pot of flour",
				    Conditions.InventoryContains("Bread dough"));
			} else {
			    status = "Using Pot of flour";
			    useItemOnItem("Pot of flour", "Bucket of water",
				    Conditions.InventoryContains("Bread dough"));
			}
		    } else if (currentTask("range")) {
			status = "Cooking Bread dough";
			if (Entity.click(Types.RSItem, "Use", new Targets("Bread dough"), null, null,
				Conditions.IsUptext("Use Bread dough ->"), 2000)) {
			    Entity.click(Types.RSObject, "Use Bread dough ->", new Targets("Range"),
				    new Conditional(Conditions.Moving(), Conditions.Animating(true)),
				    Conditions.Animating(true), Conditions.InventoryDoesNotContain("Bread dough"),
				    3000);
			}
		    } else if (currentTask("jukebox")) {
			status = "Opening Music";
			Tab.open(TABS.MUSIC);
		    } else if (currentTask("The music player")) {
			status = "Opening Door";
			RSTile door = new RSTile(3073, 3090, 0);
			if (open("Open", "Door", door, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
		    }
		} else if (Variables.QUEST_GUIDE_AREA.contains(Player.getPosition())
			|| Walk.isTileOnMinimap(Variables.QUEST_BUILDING_UPSTAIRS)) {
		    if (currentTask("Emotes")) {
			status = "Opening Emotes";
			if (Tab.open(TABS.EMOTES))
			    clickRandomEmote();
		    } else if (currentTask("Running")) {
			status = "Setting Run";
			GameSettings.setRun(runOption);
		    } else if (currentTask("run button turned on")) {
			RSTile door = new RSTile(3086, 3126, 0);
			if (open("Open", "Door", door, questGuidePath))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
		    } else if (currentTask("Talk with the Quest Guide") || currentTask("Talk to the Quest Guide")) {
			status = "Talk to Quest Guide";
			if (Game.getPlane() == 1 && PathFinding.canReach(Variables.QUEST_BUILDING_UPSTAIRS, false)) {
			    Entity.click(Types.RSObject, "Climb-down", new Targets("Staircase"), null,
				    Conditions.ActivePlayer(),
				    Conditions.CanReach(Variables.QUEST_BUILDING_UPSTAIRS, false), 3000);
			} else {
			    Entity.click(Types.RSNPC, "Talk-to", new Targets("Quest Guide"), null,
				    Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
			}
		    } else if (currentTask("Open the Quest Journal")) {
			Tab.open(TABS.QUESTS);
		    } else if (currentTask("enter some caves")) {
			RSTile ladder = new RSTile(3088, 3120, 0);
			if (open("Climb-down", "Ladder", ladder, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(ladder, false), 3000);
		    }
		} else if (Variables.MINING_INSTRUCTOR_AREA.contains(Player.getPosition())) {
		    if (currentTask("Next let's get you a weapon") || currentTask("to the Mining Instructor")) {
			status = "Talk to Mining Instructor";
			if (!Entity.click(Types.RSNPC, "Talk-to", new Targets("Mining Instructor"), null,
				Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 5000)) {
			    Walk.clickTileMinimap(new RSTile(3080, 9510, 0), 2, 2, null, null);
			}
		    } else if (currentTask("Prospecting")) {
			status = "Prospect Tin";
			Entity.click(Types.RSObject, "Prospect", new Targets(10080), null, Conditions.ActivePlayer(),
				null, General.random(4000, 5000));
		    } else if (currentTask("the brown ones")) {
			status = "Prospect Copper";
			Entity.click(Types.RSObject, "Prospect", new Targets(10079), null, Conditions.ActivePlayer(),
				null, General.random(4000, 5000));
		    } else if (currentTask("mine one tin ore")) {
			status = "Mine Tin ore";
			Entity.click(Types.RSObject, "Mine", new Targets(10080),
				new Conditional(Conditions.ActivePlayer(), Conditions.Animating(true)),
				Conditions.ActivePlayer(), Conditions.InventoryContains("Tin ore"), 5000);
		    } else if (currentTask("need some copper ore")) {
			status = "Mine Copper ore";
			Entity.click(Types.RSObject, "Mine", new Targets(10079),
				new Conditional(Conditions.ActivePlayer(), Conditions.Animating(true)),
				Conditions.ActivePlayer(), Conditions.InventoryContains("Copper ore"), 5000);
		    } else if (currentTask("make a bronze bar")) {
			String ore = null;
			if (r.nextBoolean()) {
			    ore = "Tin ore";
			} else {
			    ore = "Copper ore";
			}
			status = "Smelting Bronze bar";
			if (Entity.click(Types.RSItem, "Use", new Targets(ore), null, null,
				Conditions.IsUptext("Use " + ore + " ->"), 2000)) {
			    Entity.click(Types.RSObject, "Use " + ore + " ->", new Targets("Furnace"),
				    new Conditional(Conditions.Moving(), Conditions.Animating(true)),
				    Conditions.Animating(true), Conditions.InventoryContains("Bronze bar"), 5000);
			}
		    } else if (currentTask("Smithing a dagger")) {
			status = "Smithing Bronze dagger";
			if (!Interfaces.isInterfaceValid(312)) {
			    if (Entity.click(Types.RSItem, "Use", new Targets("Bronze bar"), null, null,
				    Conditions.IsUptext("Use Bronze bar ->"), 2000)) {
				Entity.click(Types.RSObject, "Use Bronze bar ->", new Targets("Anvil"),
					new Conditional(Conditions.Moving(), Conditions.Animating(true)),
					Conditions.Animating(true), Conditions.InterfaceValid(312), 5000);
			    }
			} else {
			    smithDagger();
			}
		    } else if (currentTask("Go through the gates shown by the arrow")) {
			status = "Opening Gate";
			RSTile gate = new RSTile(3094, 9503, 0);
			if (open("Open", "Gate", gate, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(gate, false), 3000);
		    }
		} else if (Variables.COMBAT_INSTRUCTOR_AREA.contains(Player.getPosition())) {
		    if (currentTask("find out about combat with swords")
			    || currentTask("You're now holding your dagger")) {
			status = "Talk to Combat Instructor";
			Interfaces.closeAll();
			Entity.click(Types.RSNPC, "Talk-to", new Targets("Combat Instructor"), null,
				Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
		    } else if (currentTask("icon of a man")) {
			status = "Opening Equipment";
			Tab.open(TABS.EQUIPMENT);
		    } else if (currentTask("View equipment stats")) {
			status = "Opening Stats";
			viewEquipment();
		    } else if (currentTask("Left click your dagger to")) {
			status = "Wield Bronze dagger";
			if (wieldFromInventory)
			    Interfaces.closeAll();
			clickItem("Bronze dagger");
		    } else if (currentTask("wield the sword and shield")) {
			status = "Wield Equipment";
			if (!wieldFromInventory)
			    viewEquipment();
			if (r.nextBoolean()) {
			    clickItem("Wooden shield");
			    clickItem("Bronze sword");
			} else {
			    clickItem("Bronze sword");
			    clickItem("Wooden shield");
			}
		    } else if (currentTask("flashing crossed swords icon")) {
			status = "Opening Combat";
			Tab.open(TABS.COMBAT);
		    } else if (currentTask("some rats")) {
			status = "Enter Giant rat pit";
			RSTile gate = new RSTile(3111, 9518, 0);
			if (open("Open", "Gate", gate, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(gate, false), 3000);
		    } else if (currentTask("attack the rat")) {
			status = "Attack Giant rat";
			Entity.shouldReach = false;
			Entity.click(Types.RSNPC, "Attack", new Targets("Giant Rat"),
				new Conditional(Conditions.ActivePlayer(), Conditions.InteractingCharacterInCombat()),
				Conditions.ActivePlayer(), Conditions.NpcInteractingWithPlayer(false), 3000);
		    } else if (currentTask("Well done, you've made your first kill!")
			    && !Equipment.isEquipped("Bronze arrow", "Shortbow")
			    && Inventory.getCount(new String[] { "Bronze arrow", "Shortbow" }) == 0) {
			status = "Get Shortbow";
			RSTile gate = new RSTile(3110, 9518, 0);
			if (PathFinding.canReach(gate, false)) {
			    if (open("Open", "Gate", gate, null))
				Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(gate, false), 3000);
			} else {
			    Entity.click(Types.RSNPC, "Talk-to", new Targets("Combat Instructor"), null,
				    Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
			}
		    } else if (currentTask("Rat ranging.") || currentTask("Well done, you've made your first kill!")) {
			status = "Equip ranging gear";
			if (r.nextBoolean()) {
			    clickItem("Shortbow");
			    clickItem("Bronze arrow");
			} else {
			    clickItem("Bronze arrow");
			    clickItem("Shortbow");
			}
			status = "Range Giant rat";
			Entity.shouldReach = false;
			Entity.click(Types.RSNPC, "Attack", new Targets("Giant Rat"),
				new Conditional(Conditions.ActivePlayer(), Conditions.InteractingCharacterInCombat()),
				Conditions.ActivePlayer(), null, 3000);
			Entity.shouldReach = true;
		    } else if (currentTask("ladder shown. If you need to go over any of what you learnt")) {
			status = "Climb-up ladder";
			Entity.click(Types.RSObject, "Climb-up", new Targets("Ladder"), null, Conditions.ActivePlayer(),
				Conditions.CanReach(new RSTile(3111, 9525, 0), false), 3000);
		    }
		} else if (Variables.BANK_AREA.contains(Player.getPosition())
			|| Variables.FINANCIAL_ADVISOR_AREA.contains(Player.getPosition())) {
		    if (currentTask("open your bank box")) {
			status = "Opening Bank";
			if (!NPCChat.selectOption("Yes.", true)) {
			    Entity.click(Types.RSObject, "Use", new Targets("Bank booth"), null,
				    Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
			}
		    } else if (currentTask("visit the poll booth")) {
			status = "Opening Poll Booth";
			if (Menu.closeAll())
			    status = "Closed Menu";
			Entity.click(Types.RSObject, "Use", new Targets("Poll booth"), null, Conditions.ActivePlayer(),
				Conditions.ContinueChatUp(), 3000);
		    } else if (currentTask("move on through the door indicated")) {
			sleep(General.randomSD(1500, 500));
			if (Menu.closeAll())
			    status = "Closed Menu";
			status = "Opening Door";
			RSTile door = new RSTile(3124, 3123, 0);
			Walk.clickTileMinimap(door, 3, 1, null, null);
			if (open("Open", "Door", door, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
		    } else if (currentTask("Financial advice")) {
			status = "Talk to Financial Advisor";
			if (Menu.closeAll())
			    status = "Closed Menu";
			if (!Entity.click(Types.RSNPC, "Talk-to", new Targets("Financial Advisor"), null,
				Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000)) {
			    RSTile door = new RSTile(3124, 3123, 0);
			    if (open("Open", "Door", door, null))
				Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
			}
		    } else if (currentTask("Continue through the next door")) {
			status = "Opening Door";
			RSTile door = new RSTile(3129, 3124, 0);
			if (open("Open", "Door", door, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
		    }
		} else if (Variables.BROTHER_BRACE_AREA.contains(Player.getPosition())) {
		    if (currentTask("Follow the path to the chapel")
			    || currentTask("Brother Brace and he will tell you more")
			    || currentTask("Talk with Brother Brace")) {
			RSTile door = new RSTile(3128, 3106, 0);
			if (!PathFinding.canReach(door, false) || NPCs.find("Brother Brace").length == 0) {
			    status = "Walking to Chapel";
			    if (open("Open", "Large door", door, brotherBracePath))
				Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, true), 3000);
			} else {
			    status = "Talking to Brother Brace";
			    Entity.click(Types.RSNPC, "Talk-to", new Targets("Brother Brace"), null,
				    Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
			}
		    } else if (currentTask("open the Prayer menu")) {
			status = "Opening Prayer";
			Tab.open(TABS.PRAYERS);
		    } else if (currentTask("smiling face to open your friends list")) {
			status = "Opening Friends";
			Tab.open(TABS.FRIENDS);
		    } else if (currentTask("other flashing face to the right of your screen")) {
			status = "Opening Ignore";
			Tab.open(TABS.IGNORE);
			Entity.click(Types.RSNPC, "Talk-to", new Targets("Brother Brace"), null,
				Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
		    } else if (currentTask("door to find the path leading to your final instructor")) {
			status = "Opening Door";
			RSTile door = new RSTile(3122, 3103, 0);
			if (open("Open", "Door", door, null))
			    Conditions.sleep(Conditions.ActivePlayer(), Conditions.CanReach(door, false), 3000);
		    }
		} else if (Variables.MAGIC_INSTRUCTOR_AREA.contains(Player.getPosition())) {
		    if (currentTask("follow the path to the Wizard's house") || currentTask("Ask the mage about it")
			    || currentTask("move on to the mainland")) {
			status = "Talk to Magic Instructor";
			if (!NPCChat.selectOption("Yes", true)) {
			    if (!Walk.isTileOnMinimap(magicInstructorPath[magicInstructorPath.length - 1])) {
				status = "Walking Random Path To Instructor";
				Walk.walkPath(magicInstructorPath, 2, 3, null, null);
			    }
			    Entity.click(Types.RSNPC, "Talk-to", new Targets("Magic Instructor"), null,
				    Conditions.ActivePlayer(), Conditions.ContinueChatUp(), 3000);
			}
		    } else if (currentTask("Open up your final menu")) {
			status = "Opening Magic";
			Tab.open(TABS.MAGIC);
		    } else if (currentTask("Cast Wind Strike")) {
			status = "Cast Wind Strike";
			RSTile chickens = new RSTile(3140, 3090, 0);
			if (Player.getPosition().distanceTo(chickens) > 1) {
			    Walk.walkToTile(chickens, 1, 1, null);
			} else {
			    if (!Game.isUptext("Cast Wind Strike ->")) {
				if (Tab.open(TABS.MAGIC)) {
				    status = "Selecting Wind Strike";
				    Magic.selectSpell("Wind Strike");
				    sleep((long) General.randomSD(generalSleep, 200));
				}
			    }
			    if (Game.isUptext("Cast Wind Strike ->")) {
				final int count = Inventory.getCount("Air rune");
				Entity.shouldReach = false;
				Entity.click(Types.RSNPC, "Cast Wind Strike ->", new Targets("Chicken"), null,
					Conditions.ActivePlayer(),
					Conditions.InventoryCountDoesNotEqual("Air rune", count), 3000);
				Entity.shouldReach = true;
			    }
			}
		    }
		}
	    }
	    if (ABC.performAntiban())
		status = "ABC2 Antiban";
	    sleep(General.random(0, 100));
	}
    }

    private boolean openInventory() {
	RSInterfaceChild child = Interfaces.get(548, 50);
	if (child == null)
	    return false;
	if (child.click()) {
	    status = "Opening Inventory";
	    sleep((long) General.randomSD(generalSleep, 200));
	}
	return true;
    }

    private boolean smithDagger() {
	RSInterfaceChild child = Interfaces.get(312, 2);
	if (child == null)
	    return false;
	final int count = Inventory.getCount("Bronze bar");
	status = "Making Bronze dagger";
	if (child.click())
	    Timing.waitCondition(Conditions.InventoryCountDoesNotEqual("Bronze bar", count), 5000);
	return true;
    }

    private boolean viewEquipment() {
	if (Tab.open(TABS.EQUIPMENT)) {
	    RSInterfaceChild child = Interfaces.get(387, 17);
	    if (child == null)
		return false;
	    if (child.click())
		sleep((long) General.randomSD(generalSleep, 200));
	}
	return false;
    }

    private boolean clickRandomEmote() {
	sleep((long) General.randomSD(generalSleep, 200));
	RSInterfaceChild child = Interfaces.get(216, 1);
	if (child == null || child.isHidden())
	    return false;
	int random = General.random(0, 19);
	RSInterfaceComponent component = child.getChild(random);
	if (component == null || component.isHidden())
	    return false;
	if (component.click()) {
	    status = "Choosing Emote: " + random;
	    sleep((long) General.randomSD(generalSleep, 200));
	}
	return true;
    }

    private boolean clickGender(boolean male) {
	if (male) {
	    status = "Selecting Male";
	    return true;
	}
	RSInterfaceChild child = Interfaces.get(269, 139);
	if (child == null)
	    return false;
	if (child.click()) {
	    status = "Selecting Female";
	    sleep((long) General.randomSD(generalSleep, 200));
	}
	return true;
    }

    private boolean randomizeCharacter() {
	if (!Interfaces.isInterfaceValid(269))
	    return false;
	clickGender(gender == Gender.MALE ? true : false);
	ArrayList<Integer> options = new ArrayList<Integer>(Arrays.asList(105, 106, 107, 108, 109, 110, 111, 112, 113,
		114, 115, 116, 117, 118, 119, 121, 122, 123, 124, 125, 127, 129, 130, 131));
	Collections.shuffle(options);
	for (int i = 0; i < ignoreDesignOptions; i++)
	    options.remove(i);
	for (int i : options) {
	    status = "Character Design";
	    RSInterfaceChild option = Interfaces.get(269, i);
	    if (option != null) {
		int click = General.random(minDesignClicks, maxDesignClicks);
		while (click >= 1) {
		    status = "Clicking " + click + " more time(s)";
		    option.click();
		    click--;
		    sleep((long) General.randomSD(generalSleep, 200));
		}
	    }
	}
	long timer = System.currentTimeMillis() + 10000;
	while (timer > System.currentTimeMillis() && Interfaces.isInterfaceValid(269))
	    if (clickAccept())
		return true;
	return true;
    }

    private boolean clickAccept() {
	RSInterfaceChild child = Interfaces.get(269, 100);
	if (child == null)
	    return false;
	if (child.click()) {
	    status = "Clicking Accept";
	    sleep((long) General.randomSD(generalSleep, 200));
	}
	return !Interfaces.isInterfaceValid(269);
    }

    private boolean isContinueChatUp() {
	int[] masters = new int[] { 217, 231, 229, 193, 11, 548, 162 };
	for (int index : masters) {
	    RSInterfaceMaster master = Interfaces.get(index);
	    if (master != null) {
		for (RSInterface r : master.getChildren()) {
		    if (r != null && !r.isHidden()) {
			String text = r.getText();
			if (text != null) {
			    if (text.equalsIgnoreCase("Click here to continue")
				    || text.equalsIgnoreCase("Click to continue"))
				return true;
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean continueChat() {
	final int[] MENUS = new int[] { 217, 231, 229, 193, 11, 548, 162 };
	for (int i : MENUS) {
	    RSInterfaceMaster master = Interfaces.get(i);
	    if (master != null) {
		RSInterfaceChild[] children = master.getChildren();
		if (children != null) {
		    for (RSInterfaceChild child : children) {
			if (child != null && !child.isHidden()) {
			    String text = child.getText();
			    if (text != null) {
				if (text.equalsIgnoreCase("Click here to continue")
					|| text.equalsIgnoreCase("Click to continue")) {
				    if (useKeyboard && i != 548 && i != 162) {
					status = "Pressing Spacebar to Continue";
					Keyboard.typeString(" ");
				    } else {
					status = "Clicking Continue";
					child.click();
				    }
				    sleep((long) General.randomSD(continueChatSleep, 200));
				    return true;
				}
			    }
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean isOptionAvailable(String text) {
	String[] options = NPCChat.getOptions();
	if (options == null || options.length == 0)
	    return false;
	for (String option : options) {
	    if (option.equalsIgnoreCase(text))
		return true;
	}
	return false;
    }

    private boolean clickItem(String name) {
	RSItem[] item = Inventory.find(name);
	if (item.length == 0)
	    return true;
	if (item[0].click()) {
	    status = "Clicking " + name;
	    sleep((long) General.randomSD(generalSleep, 200));
	}
	return true;
    }

    private boolean useItemOnItem(final String name1, final String name2, Condition condition) {
	if (!clickItem(name1))
	    return false;
	if (!clickItem(name2))
	    return false;
	return Timing.waitCondition(condition, 5000);
    }

    private boolean open(String action, String name, RSTile tile, RSTile[] path) {
	if (path != null && Player.getPosition().distanceTo(path[path.length - 1]) > 3) {
	    status = "Walking path to " + name;
	    Walk.walkPath(path, 2, 3, null, null);
	}
	RSObject[] objects = Objects.findNearest(30, new Filter<RSObject>() {

	    @Override
	    public boolean accept(RSObject object) {
		if (tile.distanceTo(object) > 1)
		    return false;
		RSObjectDefinition definition = object.getDefinition();
		if (definition == null)
		    return false;
		String objectName = definition.getName();
		if (objectName == null)
		    return false;
		return objectName.equalsIgnoreCase(name);
	    }
	});
	if (objects.length == 0) {
	    status = "Blind Walking to " + name;
	    Walk.blindWalkTo(tile, 1, 5, null, new Condition() {

		@Override
		public boolean active() {
		    return Walk.isTileOnMinimap(tile);
		}
	    });
	    return false;
	}
	return UsaObjects.click(action, objects);
    }

    private boolean currentTask(String search) {
	final int[] MASTER_INTERFACES = { 219, 372, 421, 204 };
	for (int i : MASTER_INTERFACES) {
	    RSInterface master = Interfaces.get(i);
	    if (master != null) {
		RSInterface[] children = master.getChildren();
		for (RSInterface child : children) {
		    if (child != null && !child.isHidden()) {
			final String text = child.getText();
			if (text != null && text.length() >= search.length()) {
			    if (text.contains(search))
				return true;
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean sendServerData(String tribot_username, long time, int total_accounts, String version) {
	try {
	    URL url = new URL("http://usa-tribot.org/tutorial/data.php?username=" + tribot_username + "&duration="
		    + time + "&accounts=" + total_accounts + "&version=" + version);
	    URLConnection conn = url.openConnection();
	    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    try {
		String response = br.readLine();
		return Boolean.valueOf(response);
	    } finally {
		br.close();
	    }
	} catch (MalformedURLException e) {
	    System.out.println("MalformedURLException when sending session data.");
	} catch (IOException e) {
	    System.out.println("IOException when sending session data.");
	}
	return false;
    }

    @Override
    public void onEnd() {
	if (!createdAccounts.isEmpty())
	    saveAccounts("USA Tutorial Island/Accounts/", createdAccounts);
	long runtime = System.currentTimeMillis() - totalTime;
	if (createdAccounts.size() != 0 && (runtime > 60000 && runtime < 86400000)) {
	    try {
		if (sendServerData(URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"), runtime,
			createdAccounts.size(), version)) {
		    println("Sent session data!");
		} else {
		    println("Error sending session data!");
		}
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void onPaint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setRenderingHints(rh);
	g2.drawImage(background, 17, 168, null);
	if (PAINT_LOGOUT_IMAGE)
	    g2.drawImage(logoutImage, 546, 423, null);
	long currentTime = System.currentTimeMillis();
	int x = 30, y = 228;
	g2.setFont(new Font("Verdana", Font.PLAIN, 12));
	g2.setColor(Color.WHITE);
	g2.drawString("Total Time: " + Timing.msToString(currentTime - totalTime), x, y);
	y += 20;
	g2.drawString("Current Account: " + Timing.msToString(currentTime - startTime), x, y);
	y += 20;
	g2.drawString("Status: " + status, x, y);
	y += 20;
	if (CONDITION_TIMER > currentTime)
	    g2.drawString("Sleeping: " + (CONDITION_TIMER - currentTime) + " ms", x, y);
	g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	g2.drawString("" + version, 326, 223);
	g2.setColor(new Color(0, 255, 0, 100));
	if (Walk.PAINT_PATH != null) {
	    for (RSTile t : Walk.PAINT_PATH) {
		if (t.isOnScreen())
		    g2.drawPolygon(Projection.getTileBoundsPoly(t, 0));
		Point p = Projection.tileToMinimap(t);
		if (p != null)
		    g2.fillRect(p.x, p.y, 2, 2);
	    }
	}
	if (Entity.current != null && PaintNPC) {
	    g2.setColor(new Color(255, 255, 255, 200));
	    Positionable entity = Entity.current;
	    if (entity != null) {
		RSModel model = null;
		if (entity instanceof RSNPC) {
		    model = ((RSAnimableEntity) entity).getModel();
		} else if (entity instanceof RSObject) {
		    model = ((RSObject) entity).getModel();
		}
		if (model != null && model.isClickable()) {
		    Polygon area = model.getEnclosedArea();
		    if (area != null)
			g2.fillPolygon(area);
		}
	    }
	}
    }

    private boolean postTutorialActions() {
	boolean traveled = false;
	boolean removed = false;
	boolean items = false;
	while (true) {
	    if (travelLocation == Location.NOWHERE)
		traveled = true;
	    if (!traveled) {
		if (Player.getPosition().distanceTo(travelLocation.getTile()) > 5) {
		    status = "Web Walking to " + travelLocation.toString();
		    if (!Player.isMoving())
			WebWalking.walkTo(travelLocation.getTile());
		} else {
		    status = "Arrived at " + travelLocation.toString();
		    traveled = true;
		}
	    } else {
		if (!items && itemOption != ItemOptions.NOTHING) {
		    if (itemOption == ItemOptions.DROP_ALL_ITEMS) {
			status = "Dropping All Items";
			Inventory.dropAllExcept(new String[] { "" });
			items = true;
		    } else if (itemOption == ItemOptions.BANK_ALL_ITEMS) {
			if (!Banking.isInBank()) {
			    status = "Web Walking to Bank";
			    if (!Player.isMoving())
				WebWalking.walkToBank();
			} else {
			    status = "Opening Bank";
			    if (Banking.openBank()) {
				if (Banking.isBankScreenOpen()) {
				    status = "Depositing Items";
				    Banking.depositAll();
				    items = true;
				}
			    }
			}
		    }
		} else {
		    if (Banking.isBankScreenOpen()) {
			status = "Closing Bank";
			Banking.close();
		    } else {
			if (useAccounts && currentAccount.getDisplayName() == null) {
			    if (Login.getLoginState() != Login.STATE.LOGINSCREEN
				    && Login.getLoginState() == Login.STATE.INGAME) {
				String name = Player.getRSPlayer().getName();
				if (name != null)
				    currentAccount.setDisplayName(name);
			    }
			}
			if (logOption == LogOptions.LOGOUT) {
			    status = "Logging Out";
			    if (useAccounts && !removed) {
				accounts.remove(0);
				removed = true;
			    }
			    if (Login.logout()) {
				currentAccount = null;
				return true;
			    }
			} else {
			    if (FORCE_LOGOUT) {
				status = "Logging Out";
				if (useAccounts && !removed) {
				    accounts.remove(0);
				    removed = true;
				}
				if (Login.logout()) {
				    currentAccount = null;
				    FORCE_LOGOUT = false;
				    PAINT_LOGOUT_IMAGE = false;
				    return true;
				}
			    }
			    if (Trading.getWindowState() != null) {
				acceptItems();
			    } else {
				status = "Staying Logged In";
				if (!PAINT_LOGOUT_IMAGE)
				    PAINT_LOGOUT_IMAGE = true;
			    }
			    login(currentAccount);
			}
		    }
		}
	    }
	    if (ABC.performAntiban())
		status = "ABC2 Antiban";
	    sleep(100);
	}
    }

    private boolean acceptItems() {
	long timer = System.currentTimeMillis() + 60000;
	while (timer > System.currentTimeMillis()) {
	    WINDOW_STATE state = Trading.getWindowState();
	    if (state == null)
		return false;
	    status = "In Trade!";
	    if (state == Trading.WINDOW_STATE.FIRST_WINDOW) {
		if (Trading.hasAccepted(true)) {
		    status = "Accepting First Window";
		    Trading.accept();
		    Timing.waitCondition(new Condition() {

			public boolean active() {
			    sleep(100);
			    WINDOW_STATE w = Trading.getWindowState();
			    return w != null && w == Trading.WINDOW_STATE.SECOND_WINDOW;
			}
		    }, 2000);
		}
	    } else if (state == Trading.WINDOW_STATE.SECOND_WINDOW) {
		if (!Trading.hasAccepted(false)) {
		    status = "Accepting Second Window";
		    Trading.accept();
		    Timing.waitCondition(new Condition() {

			public boolean active() {
			    sleep(100);
			    return Trading.getWindowState() == null;
			}
		    }, 2000);
		}
	    }
	    sleep((long) General.randomSD(1000, 200));
	}
	return false;
    }

    private boolean trade(String username) {
	if (Trading.getWindowState() != null)
	    return true;
	long timer = System.currentTimeMillis() + 30000;
	while (timer > System.currentTimeMillis()) {
	    RSPlayer[] players = Players.getAll(Filters.Players.nameEquals(username));
	    if (players.length > 0) {
		status = "Trading " + username;
		if (players[0].click("Trade with " + username)) {
		    long moving = System.currentTimeMillis() + 3000;
		    while (moving > System.currentTimeMillis()) {
			if (Player.isMoving())
			    moving = System.currentTimeMillis() + 3000;
			if (Trading.getWindowState() != null)
			    return true;
			sleep(100);
		    }
		}
	    }
	}
	return false;
    }

    private boolean generateRandomProfile() {
	Random r = new Random();
	int value = 0;
	hoverSleep = General.random(200, 1000);
	continueChatSleep = General.random(1000, 3000);
	generalSleep = General.random(1000, 3000);
	useKeyboard = r.nextBoolean();
	ignoreDesignOptions = General.random(0, 10);
	minDesignClicks = General.random(0, 1);
	maxDesignClicks = General.random(1, 3);
	catchTwoShrimp = r.nextBoolean();
	wieldFromInventory = r.nextBoolean();
	value = General.random(1, 2);
	if (value == 1) {
	    gender = Gender.MALE;
	} else {
	    gender = Gender.FEMALE;
	}
	value = General.random(1, 2);
	if (value == 1) {
	    runOption = RunOptions.DATA_ORBS;
	} else {
	    runOption = RunOptions.OPTIONS_MENU;
	}
	value = General.random(1, 3);
	if (value == 1) {
	    questGuidePath = Variables.QUEST_GUIDE_PATH_1;
	} else if (value == 2) {
	    questGuidePath = Variables.QUEST_GUIDE_PATH_2;
	} else {
	    questGuidePath = Variables.QUEST_GUIDE_PATH_3;
	}
	value = General.random(1, 2);
	if (value == 1) {
	    brotherBracePath = Variables.BROTHER_BRACE_PATH_1;
	} else {
	    brotherBracePath = Variables.BROTHER_BRACE_PATH_2;
	}
	value = General.random(1, 3);
	if (value == 1) {
	    magicInstructorPath = Variables.MAGIC_INSTRUCTOR_PATH_1;
	} else if (value == 2) {
	    magicInstructorPath = Variables.MAGIC_INSTRUCTOR_PATH_2;
	} else {
	    magicInstructorPath = Variables.MAGIC_INSTRUCTOR_PATH_3;
	}
	println("Generated random profile.");
	return true;
    }

    private boolean login(Account account) {
	if (account == null)
	    return true;
	if (Login.getLoginState() != Login.STATE.LOGINSCREEN && Login.getLoginState() == Login.STATE.INGAME) {
	    String name = Player.getRSPlayer().getName();
	    if (name != null) {
		if (account.getDisplayName() != null) {
		    if (account.getDisplayName().equalsIgnoreCase(name)) {
			return true;
		    } else {
			long timer = System.currentTimeMillis() + 30000;
			while (timer > System.currentTimeMillis()) {
			    status = "Logging Out";
			    Login.logout();
			    Timing.waitCondition(new Condition() {

				public boolean active() {
				    sleep(100);
				    return Login.getLoginState() != Login.STATE.INGAME;
				}
			    }, 2000);
			    if (Login.getLoginState() != Login.STATE.INGAME)
				return false;
			}
		    }
		} else {
		    account.setDisplayName(name);
		    return false;
		}
	    }
	} else {
	    long timer = System.currentTimeMillis() + 30000;
	    if (account.getDisplayName() != null) {
		status = "Logging in " + account.getDisplayName();
	    } else {
		status = "Logging in " + account.getEmail();
	    }
	    while (timer > System.currentTimeMillis()) {
		if (Login.login(account.getEmail(), account.getPassword())) {
		    if (clickHereToPlay()) {
			status = "Logged In!";
			return true;
		    }
		}
		sleep(1000);
	    }
	}
	return false;
    }

    private boolean clickHereToPlay() {
	if (Interfaces.isInterfaceValid(378)) {
	    RSInterfaceChild child = Interfaces.get(378, 6);
	    if (child == null)
		return false;
	    Rectangle bounds = child.getAbsoluteBounds();
	    if (bounds == null)
		return false;
	    status = "Click Here To Play";
	    Mouse.clickBox(bounds, 1);
	    Timing.waitCondition(new Condition() {

		public boolean active() {
		    sleep((long) General.randomSD(hoverSleep, 50));
		    return Login.getLoginState() != Login.STATE.WELCOMESCREEN;
		}
	    }, 2000);
	}
	return Login.getLoginState() == Login.STATE.INGAME && Login.getLoginState() != Login.STATE.WELCOMESCREEN;
    }

    private Account createAccount() {
	while (true) {
	    try {
		status = "Creating Account...";
		AccountCreator ac = new AccountCreator(captchaKey);
		status = "Generating Age...";
		int age = createAge + General.random(-3, 3);
		if (age < 13)
		    age = 13 + General.random(0, 10);
		status = "Generating Display...";
		String display = generateString(true, createIndex, createDisplay);
		status = "Generating Email...";
		String email = generateString(createIndex, createEmail);
		status = "Generating Password...";
		String password = generateString(createIndex, createPassword);
		while (!display.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
		    status = "Creating account named \"" + display + "\"";
		    Result result = ac.create(age, display, email, password);
		    if (result == Result.SUCCESS) {
			println("Successfully created account!");
			println("Display: \"" + display + "\" / Email: \"" + email + "\" / Password: "
				+ AccountCreator.obfuscatePassword(password));
			createIndex++;
			return new Account(display, email, password);
		    } else if (result == Result.EMAIL_IN_USE) {
			println("Email \"" + email + "\" is in use.");
			run = false;
			return null;
		    } else if (result == Result.INVALID_EMAIL) {
			println("Email \"" + email + "\" is invalid.");
		    } else if (result == Result.USERNAME_NOT_AVAILABLE) {
			println("Display \"" + display + "\" is not available.");
			break;
		    } else if (result == Result.BLOCKED) {
			status = "Temporarily Blocked";
			println("You are temporarily blocked from creating accounts. Trying again in 10-15 minutes.");
			sleep(General.random(600000, 900000));
			break;
		    } else if (result == Result.INVALID_API_KEY) {
			println("You have an invalid 2Captcha API KEY. Please check your settings and try again!");
			Web.open("https://2captcha.com/setting");
			run = false;
			return null;
		    } else if (result == Result.TIMEOUT) {
			println("POST request has timed out. Trying again!");
		    } else if (result == Result.UNAVAILABLE) {
			println("Service is currently unavailable. Trying again!");
		    }
		    sleep(100);
		}
	    } catch (Exception e) {
		General.println(e);
	    }
	    sleep(100);
	}
    }

    private String generateString(int index, String input) {
	return generateString(false, index, input);
    }

    private String generateString(boolean display, int index, String input) {
	String result = null;
	while (result == null) {
	    result = AccountCreator.generateString(display, index, input);
	    sleep(100);
	}
	return result.trim();
    }

    private boolean saveAccounts(String directory, ArrayList<Account> createdAccounts) {
	try {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh-mm-ss a");
	    Calendar cal = Calendar.getInstance();
	    String name = dateFormat.format(cal.getTime());
	    File folder = new File(Util.getWorkingDirectory() + "/" + directory);
	    if (!folder.exists())
		folder.mkdir();
	    File file = new File(folder.toString() + "/" + name + ".txt");
	    if (file.createNewFile())
		System.out.println("Saving accounts to '" + file.toString() + "'");
	    try {
		if (file.exists()) {
		    List<String> headersList = Arrays.asList("DISPLAY", "EMAIL", "PASSWORD");
		    List<List<String>> rowsList = new ArrayList(new ArrayList<String>());
		    for (Account account : createdAccounts)
			rowsList.add(
				Arrays.asList(account.getDisplayName(), account.getEmail(), account.getPassword()));
		    Board board = new Board(80);
		    Table table = new Table(board, 80, headersList, rowsList);
		    table.setGridMode(Table.GRID_COLUMN);
		    List<Integer> colWidthsList = Arrays.asList(25, 25, 25);
		    List<Integer> colAlignList = Arrays.asList(Block.DATA_CENTER, Block.DATA_CENTER, Block.DATA_CENTER);
		    table.setColWidthsList(colWidthsList);
		    table.setColAlignsList(colAlignList);
		    Block tableBlock = table.tableToBlocks();
		    board.setInitialBlock(tableBlock);
		    board.build();
		    String tableString = board.getPreview();
		    FileWriter fw = new FileWriter(file);
		    BufferedWriter bw = new BufferedWriter(fw);
		    String[] split = tableString.split("\n");
		    for (String s : split) {
			bw.write(s);
			bw.newLine();
		    }
		    bw.close();
		    fw.close();
		}
	    } catch (FileNotFoundException ex) {
		System.out.println("Unable to open file '" + name + "'");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return true;
    }

    public static void loadGUI() {
	travelLocation = (Location) GUI.locationBox.getSelectedItem();
	itemOption = (ItemOptions) GUI.itemBox.getSelectedItem();
	logOption = (LogOptions) GUI.endingActionBox.getSelectedItem();
	randomizeProfile = GUI.randomizeConfigurationOption.isSelected();
	guiMusicOption = GUI.turnOffMusicBox.isSelected();
	guiBrightnessOption = GUI.setMaxBrightnessBox.isSelected();
	setMaximumBrightness = GUI.setMaxBrightnessBox.isSelected();
	guiRoofOption = GUI.removeRoofsBox.isSelected();
	removeRoofs = GUI.removeRoofsBox.isSelected();
	generalSleep = (int) GUI.generalSleepSlider.getValue();
	hoverSleep = (int) GUI.hoverSlider.getValue();
	continueChatSleep = (int) GUI.continueChatSlider.getValue();
	ignoreDesignOptions = (int) GUI.designArrowSpinner.getValue();
	minDesignClicks = (int) GUI.minArrowSpinner.getValue();
	maxDesignClicks = (int) GUI.maxArrowSpinner.getValue();
	gender = (Gender) GUI.genderBox.getSelectedItem();
	runOption = (RunOptions) GUI.runBox.getSelectedItem();
	wieldFromInventory = GUI.wieldWeaponsCheckbox.isSelected();
	catchTwoShrimp = GUI.catchTwoShrimpsCheckbox.isSelected();
	useKeyboard = GUI.useKeyboardCheckbox.isSelected();
	switch (GUI.questGuidePathBox.getSelectedIndex()) {
	case 0:
	    questGuidePath = Variables.QUEST_GUIDE_PATH_1;
	case 1:
	    questGuidePath = Variables.QUEST_GUIDE_PATH_2;
	case 2:
	    questGuidePath = Variables.QUEST_GUIDE_PATH_3;
	}
	switch (GUI.brotherBracePathBox.getSelectedIndex()) {
	case 0:
	    brotherBracePath = Variables.BROTHER_BRACE_PATH_1;
	case 1:
	    brotherBracePath = Variables.BROTHER_BRACE_PATH_2;
	}
	switch (GUI.magicInstructorPathBox.getSelectedIndex()) {
	case 0:
	    magicInstructorPath = Variables.MAGIC_INSTRUCTOR_PATH_1;
	case 1:
	    magicInstructorPath = Variables.MAGIC_INSTRUCTOR_PATH_2;
	case 2:
	    magicInstructorPath = Variables.MAGIC_INSTRUCTOR_PATH_3;
	}
	useAccounts = GUI.useLoadOption.isSelected();
	createAccounts = GUI.useCreateOption.isSelected();
	if (useAccounts) {
	    for (Object account : GUI.accountModel.toArray()) {
		String[] credentials = account.toString().split(":");
		if (credentials.length == 2)
		    accounts.add(new Account(null, credentials[0].trim(), credentials[1].trim()));
	    }
	    loadedAccounts = accounts.size();
	    General.println("We will execute Tutorial Island on " + loadedAccounts + " account(s).");
	} else if (createAccounts) {
	    captchaKey = GUI.captchaText.getText();
	    amountToCreate = (int) GUI.createAccountsSpinner.getValue();
	    createAge = (int) GUI.createAgeSlider.getValue();
	    createDisplay = GUI.createDisplayText.getText();
	    createEmail = GUI.createEmailText.getText();
	    createPassword = GUI.createPasswordText.getText();
	    General.println("We will create " + amountToCreate + " account(s).");
	}
	gui_is_up = false;
	g.dispose();
    }

    @Override
    public void mouseClicked(Point p, int button, boolean isBot) {
	Rectangle logoutRectangle = new Rectangle(548, 426, 189, 34);
	if (!isBot && PAINT_LOGOUT_IMAGE && logoutRectangle.contains(p)) {
	    println("You have activated the Force Logout option.");
	    FORCE_LOGOUT = true;
	}
    }

    @Override
    public void mouseDragged(Point arg0, int arg1, boolean arg2) {
    }

    @Override
    public void mouseMoved(Point arg0, boolean arg1) {
    }

    @Override
    public void mouseReleased(Point p, int button, boolean isBot) {
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

    @Override
    public void tradeRequestReceived(String username) {
	if (logOption == LogOptions.STAY_LOGGED_IN) {
	    trade(username);
	}
    }
}