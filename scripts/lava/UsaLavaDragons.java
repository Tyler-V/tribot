package scripts.lava;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;

import scripts.lava.Locations.LAVA_DRAGON_LOCATIONS;
import scripts.lava.Spells.CastingStyle;
import scripts.lava.Spells.Item;
import scripts.lava.Spells.Spell;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.Time;
import scripts.usa.api.antiban.responder.AutoResponder;
import scripts.usa.api.tabs.Tab;
import scripts.usa.api.ui.Paint;
import scripts.usa.api.web.pricing.Pricing;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Camera;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.looting.Bag;
import scripts.usa.api2007.looting.LootItem;
import scripts.usa.api2007.looting.Looting;
import scripts.usa.api2007.webwalker_logic.local.walker_engine.WalkingCondition;
import scripts.usa.api2007.webwalker_logic.local.walker_engine.interaction_handling.AccurateMouse.State;
import scripts.usa.api2007.worlds.WorldActivity;
import scripts.usa.api2007.worlds.WorldHopper;
import scripts.usa.api2007.worlds.WorldType;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Lava Dragons")
public class UsaLavaDragons extends Script implements Painting, Ending, MessageListening07, MouseActions, Breaking {

	// General
	public static String version = "v9.9";
	private final Image image = TabletsPaint.getImage("http://i.imgur.com/u8N3sh5.png");
	private String status = "Starting...";
	private boolean paint_toggle = true;
	private boolean run = true;
	private boolean died = false;
	private long startTime = 0;
	private int profit, startLVL, startXP, deaths, hops, trips, kills, fail, tripProfit, expense;
	private boolean trip = true;
	private ArrayList<BankItem> bankItems = new ArrayList<BankItem>();
	private List<String> DONT_DEPOSIT_BANK;
	private List<String> DONT_DEPOSIT_LOOTING_BAG;
	private static ArrayList<String> friendlyPlayers = new ArrayList<String>();
	private static ArrayList<String> startingEquipment = new ArrayList<String>();

	// Areas
	private final RSArea CORPOREAL_BEAST = new RSArea(new RSTile(2962, 4379, 2), new RSTile(2971, 4388, 2));
	private final RSArea LAVA_DRAGON_ISLE = new RSArea(new RSTile[] { new RSTile(3229, 3849, 0), new RSTile(3208, 3856, 0), new RSTile(3198, 3856, 0), new RSTile(3191, 3855, 0), new RSTile(3186, 3847, 0), new RSTile(3179, 3840, 0),
			new RSTile(3179, 3831, 0), new RSTile(3173, 3824, 0), new RSTile(3172, 3811, 0), new RSTile(3177, 3804, 0), new RSTile(3181, 3802, 0), new RSTile(3186, 3803, 0), new RSTile(3191, 3799, 0), new RSTile(3195, 3800, 0),
			new RSTile(3200, 3807, 0), new RSTile(3210, 3802, 0), new RSTile(3225, 3813, 0), new RSTile(3225, 3822, 0), new RSTile(3232, 3833, 0), new RSTile(3233, 3838, 0), new RSTile(3237, 3842, 0), new RSTile(3224, 3850, 0) });

	// Items
	private final int LOOTING_BAG_ID = 11941;
	private final String PESTLE_AND_MORTAR = "Pestle and mortar";
	private final String LAVA_SCALE = "Lava scale";
	private final String LAVA_SCALE_SHARD = "Lava scale shard";
	private final String[] BURNING_AMULET = { "Burning amulet(5)", "Burning amulet(4)", "Burning amule(3)", "Burning amule(2)", "Burning amule(1)" };
	private final String[] GAMES_NECKLACE = { "Games necklace(1)", "Games necklace(2)", "Games necklace(3)", "Games necklace(4)", "Games necklace(5)", "Games necklace(6)", "Games necklace(7)", "Games necklace(8)" };
	private final String[] AMULET_OF_GLORY = { "Amulet of glory(1)", "Amulet of glory(2)", "Amulet of glory(3)", "Amulet of glory(4)", "Amulet of glory(5)", "Amulet of glory(6)" };
	private final String[] ENERGY_POTION = { "Energy potion(4)", "Energy potion(3)", "Energy potion(2)", "Energy potion(1)" };
	private final String[] MAGIC_POTION = { "Magic potion(4)", "Magic potion(3)", "Magic potion(2)", "Magic potion(1)" };
	private final String[] ITEMS_TO_DROP = { "Vial" };

	// Painting
	private RSNPC CURRENT_TARGET = null;

	// Evade
	private Thread thread = null;
	private Threat runnable = null;
	private boolean evade = false;

	// Classes
	private AutoResponder responder;
	private Looting loot;
	private Camera camera = new Camera();

	// Conditions
	private WalkingCondition walkingCondition = new WalkingCondition() {
		public State action() {
			if (evade || died) {
				return State.EXIT_OUT_WALKER_SUCCESS;
			}
			else {
				eat();
				drinkEnergyPotion();
				drop(ITEMS_TO_DROP);
				if (Player.getRSPlayer()
						.isInCombat() && !Game.isRunOn() && Game.getRunEnergy() > 0) {
					Options.setRunOn(true);
				}
				return State.CONTINUE_WALKER;
			}
		}
	};

	// Options
	private static TeleportItems toLavaDragonsItem;
	private static LAVA_DRAGON_LOCATIONS MASTER_LOCATION;
	private static LAVA_DRAGON_LOCATIONS LOCATION;
	private static Spell SPELL;
	private static CastingStyle STYLE;
	private static int SPELLS_PER_TRIP;
	private static int mouseSpeed;
	private static boolean useAutoResponder;
	private static boolean usingOccult;
	private static boolean usingSmoke;
	private static boolean grindScales;
	private static boolean calculateTrueProfit;
	private static boolean ABCAntiban;
	private static boolean ABCReaction;
	private static boolean useLootingBag;
	private static boolean buryBones;
	private static boolean paintLootTable;
	private static String FOOD_NAME;
	private static int FOOD_QUANTITY;
	private static int ENERGY_POTIONS_PER_TRIP;
	private static boolean useEnergyPotion;
	private static int MAGIC_POTIONS_PER_TRIP;
	private static boolean useMagicPotion;
	private static int MINIMUM_LOOT_VALUE;
	private static boolean ignorePlayersBelowLevel;
	private static int minimumPlayerLevel;
	private static WorldType toDragonWorld;
	private static WorldType toBankWorld;
	private static GUI gui;
	private static boolean gui_is_up = true;

	@Override
	public void run() {
		if (openConnection()) {
			println("Successfully connected to statistics server!");
		}
		else {
			println("Unable to connect to statistics server. Shutting down.");
			run = false;
		}
		status = "Logging in...";
		while (Login.getLoginState() != Login.STATE.INGAME)
			sleep(100);
		gui = new GUI();
		gui.setVisible(true);
		status = "GUI";
		while (gui_is_up)
			sleep(100);
		loot = new Looting(MINIMUM_LOOT_VALUE, Integer.MAX_VALUE);
		println("Mouse speed set to " + mouseSpeed);
		Mouse.setSpeed(mouseSpeed);
		if (MASTER_LOCATION == LAVA_DRAGON_LOCATIONS.RANDOM) {
			if (LAVA_DRAGON_ISLE.contains(Player.getPosition())) {
				for (LAVA_DRAGON_LOCATIONS location : LAVA_DRAGON_LOCATIONS.values()) {
					if (Player.getPosition()
							.equals(location.getSafeTile())) {
						LOCATION = location;
						loot.setArea(LOCATION.getSafeTile(), LOCATION.getMaximumDistance());
						println("We are on the safe tile of " + LOCATION + ", using this location!");
						break;
					}
				}
			}
			if (LOCATION == null) {
				LOCATION = LAVA_DRAGON_LOCATIONS.values()[General.random(0, 4)];
				loot.setArea(LOCATION.getSafeTile(), LOCATION.getMaximumDistance());
				println("Random location set to: " + LOCATION);
			}
		}
		else {
			LOCATION = MASTER_LOCATION;
			loot.setArea(LOCATION.getSafeTile(), LOCATION.getMaximumDistance());
			println("Master location set to: " + LOCATION);
		}
		if (!ABCAntiban)
			super.setAIAntibanState(false);
		if (!ABCReaction)
			ABC.setSleepReaction(false);
		if (useAutoResponder) {
			super.setAutoResponderState(false);
			responder = new AutoResponder();
		}
		if (startingEquipment.size() == 0) {
			println("Checking starting equipment...");
			RSItem[] equipment = Equipment.getItems();
			if (equipment.length > 0) {
				for (RSItem e : equipment) {
					if (e != null) {
						RSItemDefinition d = e.getDefinition();
						if (d != null) {
							String name = d.getName();
							if (name != null) {
								println(name);
								startingEquipment.add(name);
							}
						}
					}
				}
			}
			println("---");
		}
		// ADDING BANKING ITEMS
		if (useEnergyPotion)
			bankItems.add(new BankItem(ENERGY_POTION, ENERGY_POTIONS_PER_TRIP, 3008));
		if (useMagicPotion)
			bankItems.add(new BankItem(MAGIC_POTION, MAGIC_POTIONS_PER_TRIP, 3040));
		if (grindScales)
			bankItems.add(new BankItem(PESTLE_AND_MORTAR, 1, 233, true));
		if (toLavaDragonsItem == TeleportItems.BURNING_AMULET) {
			bankItems.add(new BankItem(BURNING_AMULET, 1, 21166, true));
		}
		else if (toLavaDragonsItem == TeleportItems.GAMES_NECKLACE) {
			bankItems.add(new BankItem(GAMES_NECKLACE, 1, 3853, true));
		}
		if (usingSmoke)
			println("We are using Smoke Staff, removing required Air and Fire runes!");
		if (!SPELL.isTrident()) {
			for (Item i : SPELL.getItems()) {
				if (i.getName()
						.equals("Air rune")) {
					if (!usingSmoke) {
						bankItems.add(new BankItem(i.getName(), i.getAmount() * SPELLS_PER_TRIP, i.getID(), true));
						println("Required: " + i.getAmount() + " " + i.getName());
					}
				}
				else {
					bankItems.add(new BankItem(i.getName(), i.getAmount() * SPELLS_PER_TRIP, i.getID(), true));
					println("Required: " + i.getAmount() + " " + i.getName());
				}
			}
		}
		bankItems.add(new BankItem(FOOD_NAME, FOOD_QUANTITY, 0, true));
		// ADDING MANDATORY LOOTING
		loot.put(new LootItem("Draconic visage", 11286, Pricing.getPrice("Draconic visage", 11286), 0, true));
		loot.put(new LootItem("Looting bag", LOOTING_BAG_ID, 0, 0, true));
		loot.put(new LootItem("Fire orb", 570, Pricing.getPrice("Fire orb", 569), 0, false));
		if (grindScales)
			loot.put(new LootItem("Lava scale", 11992, Pricing.getPrice("Lava scale", 11992), 0, true));
		// BANKING AND DEPOSITING ITEMS
		DONT_DEPOSIT_BANK = new ArrayList<String>();
		if (useLootingBag)
			DONT_DEPOSIT_BANK.add("Looting bag");
		DONT_DEPOSIT_BANK.addAll(
				Arrays.asList(
						FOOD_NAME,
						PESTLE_AND_MORTAR,
						ENERGY_POTION[3],
						ENERGY_POTION[2],
						ENERGY_POTION[1],
						ENERGY_POTION[0],
						MAGIC_POTION[3],
						MAGIC_POTION[2],
						MAGIC_POTION[1],
						MAGIC_POTION[0],
						AMULET_OF_GLORY[5],
						AMULET_OF_GLORY[4],
						AMULET_OF_GLORY[3],
						AMULET_OF_GLORY[2],
						AMULET_OF_GLORY[1],
						AMULET_OF_GLORY[0]));
		if (toLavaDragonsItem == TeleportItems.BURNING_AMULET) {
			DONT_DEPOSIT_BANK.addAll(Arrays.asList(BURNING_AMULET[4], BURNING_AMULET[3], BURNING_AMULET[2], BURNING_AMULET[1], BURNING_AMULET[0]));
		}
		else if (toLavaDragonsItem == TeleportItems.GAMES_NECKLACE) {
			DONT_DEPOSIT_BANK.addAll(Arrays.asList(GAMES_NECKLACE[7], GAMES_NECKLACE[6], GAMES_NECKLACE[5], GAMES_NECKLACE[4], GAMES_NECKLACE[3], GAMES_NECKLACE[2], GAMES_NECKLACE[1], GAMES_NECKLACE[0]));
		}
		if (!SPELL.isTrident()) {
			for (Item item : SPELL.getItems())
				DONT_DEPOSIT_BANK.add(item.getName());
		}
		DONT_DEPOSIT_LOOTING_BAG = new ArrayList<String>();
		if (useLootingBag) {
			DONT_DEPOSIT_LOOTING_BAG.addAll(DONT_DEPOSIT_BANK);
			DONT_DEPOSIT_LOOTING_BAG.addAll(Arrays.asList("Draconic visage", "Looting bag", "Vial", "Clue scroll", LAVA_SCALE, AMULET_OF_GLORY[5], AMULET_OF_GLORY[4], AMULET_OF_GLORY[3], AMULET_OF_GLORY[2], AMULET_OF_GLORY[1], AMULET_OF_GLORY[0]));
		}

		runnable = new Threat();
		thread = new Thread(runnable);
		thread.start();
		System.out.println("Starting Threat thread.");

		Camera.setCameraAngle(100);
		startTime = Timing.currentTimeMillis();
		while (run) {
			if (WorldHopper.onWorld(WorldActivity.DEADMAN)) {
				status = "On Deadman Server!";
				changeWorlds();
			}
			if (evade) {
				status = "Evading!";
				if (WorldHopper.logout()) {
					super.setLoginBotState(false);
					if (changeWorlds()) {
						super.setLoginBotState(true);
						evade = false;
					}
				}
			}
			else {
				if (WorldHopper.isInGame()) {
					if (startLVL == 0 || startXP == 0) {
						startLVL = Skills.getActualLevel(SKILLS.MAGIC);
						startXP = Skills.getXP(SKILLS.MAGIC);
					}
					if (ABC.activateRun())
						status = "Activating Run";
					if (needToBank()) {
						if (Wilderness.isIn()) {
							travel(Route.TO_BANK);
						}
						else {
							status = "Banking";
							if (trip) {
								trips++;
								trip = false;
							}
							tripProfit = 0;
							if (!bank()) {
								status = "Failed banking...";
								sleep(3000);
								fail++;
								if (fail >= 3) {
									println("Failed to bank 3 times. Stopping script.");
									run = false;
								}
							}
							else {
								fail = 0;
							}
						}
					}
					else {
						if (!LAVA_DRAGON_ISLE.contains(Player.getPosition())) {
							trip = true;
							travel(Route.TO_DRAGONS);
						}
						else {
							if (loot.findLoot().length > 0) {
								status = "Looting items";
								if (makeRoom()) {
									status = "Making room";
								}
								else {
									loot.loot(new Condition() {
										public boolean active() {
											return evade;
										}
									});
								}
							}
							else if (!onSafeTile()) {
								status = "Walking to Safe Tile";
								Walking.travel(LOCATION.getSafeTile());
							}
							else {
								if (!drop(ITEMS_TO_DROP) && !eat() && !buryLavaDragonBones() && !grindLavaScales() && setSpell(SPELL, STYLE)) {
									RSNPC npc = getNPC("Lava Dragon", LOCATION.getSafeTile(), LOCATION.getMaximumDistance());
									if (npc != null) {
										CURRENT_TARGET = npc;
										if (attackNPC(npc)) {
											if (sleepWhileAttacking(npc)) {
												if (sleepUntilLootAppears()) {
													status = "Found loot!";
													kills++;
												}
												CURRENT_TARGET = null;
											}
										}
									}
									else {
										WorldHopper.hoverOverLogout();
									}
								}
							}
						}
					}
				}
			}
			if (performAntiban()) {
				status = "ABC2 Antiban";
			}
			sleep(General.random(0, 50));
		}
	}

	public static void loadGUI() {
		toLavaDragonsItem = (TeleportItems) GUI.toLavaDragonsItemBox.getSelectedItem();
		MASTER_LOCATION = (LAVA_DRAGON_LOCATIONS) GUI.locationBox.getSelectedItem();
		SPELL = (Spell) GUI.spellBox.getSelectedItem();
		STYLE = (CastingStyle) GUI.castingStyleBox.getSelectedItem();
		SPELLS_PER_TRIP = (int) GUI.spellsSpinner.getValue();
		mouseSpeed = (int) GUI.mouseSlider.getValue();
		usingOccult = GUI.occultBox.isSelected();
		usingSmoke = GUI.smokeBox.isSelected();
		useAutoResponder = GUI.autoResponderBox.isSelected();
		grindScales = GUI.grindScalesBox.isSelected();
		calculateTrueProfit = GUI.trueProfitBox.isSelected();
		useLootingBag = GUI.lootingBagBox.isSelected();
		buryBones = GUI.buryBonesBox.isSelected();
		ABCAntiban = GUI.abcAntibanBox.isSelected();
		ABCReaction = GUI.abcReactionBox.isSelected();
		paintLootTable = GUI.paintLootTableBox.isSelected();
		FOOD_NAME = GUI.foodText.getText();
		FOOD_QUANTITY = (int) GUI.foodSpinner.getValue();
		ENERGY_POTIONS_PER_TRIP = (int) GUI.energyPotionSpinner.getValue();
		if (ENERGY_POTIONS_PER_TRIP > 0)
			useEnergyPotion = true;
		MAGIC_POTIONS_PER_TRIP = (int) GUI.magicPotionSpinner.getValue();
		if (MAGIC_POTIONS_PER_TRIP > 0)
			useMagicPotion = true;
		MINIMUM_LOOT_VALUE = (int) GUI.lootSpinner.getValue();
		String text = GUI.equipmentText.getText();
		Pattern pat = Pattern.compile("[A-Z].*");
		Matcher match = pat.matcher(text);
		ArrayList<String> matches = new ArrayList<String>();
		while (match.find()) {
			matches.add(match.group());
		}
		startingEquipment = matches;
		text = "Starting Equipment: ";
		for (String s : startingEquipment) {
			if (text.length() <= 25) {
				text = text + s;
			}
			else {
				text = text + ", " + s;
			}
		}
		System.out.println(text);
		ignorePlayersBelowLevel = GUI.ignoreBelowBox.isSelected();
		minimumPlayerLevel = (int) GUI.levelSpinner.getValue();
		if (ignorePlayersBelowLevel)
			General.println("We will ignore players below level " + minimumPlayerLevel);
		toDragonWorld = (WorldType) GUI.toDragonBox.getSelectedItem();
		General.println("When traveling to Lava Dragons we will use " + toDragonWorld.toString() + " worlds");
		toBankWorld = (WorldType) GUI.toBankBox.getSelectedItem();
		General.println("When traveling to Level 30 Wilderness we will use " + toBankWorld.toString() + " worlds");
		gui_is_up = false;
		gui.dispose();
	}

	private boolean performAntiban() {
		if (!ABCAntiban || evade)
			return false;
		if (LAVA_DRAGON_ISLE.contains(Player.getPosition()) && !onSafeTile()) {
			return false;
		}
		return ABC.performAntiban();
	}

	private boolean eat() {
		if (ABC.shouldEat() && Consumables.hasFood()) {
			status = "Eating food";
			return Consumables.eat();
		}
		return false;
	}

	private boolean drinkEnergyPotion() {
		if (!useEnergyPotion || Inventory.getCount(ENERGY_POTION) == 0)
			return false;
		if (ABC.shouldDrinkEnergyPotion()) {
			status = "Drinking Energy potion";
			return Consumables.drink(ENERGY_POTION);
		}
		return false;
	}

	private boolean drinkMagicPotion() {
		if (!useMagicPotion || Inventory.getCount(MAGIC_POTION) == 0)
			return false;
		if (Skills.getCurrentLevel(SKILLS.MAGIC) <= Skills.getActualLevel(SKILLS.MAGIC)) {
			status = "Drinking Magic potion";
			return Consumables.drink(MAGIC_POTION);
		}
		return false;
	}

	private boolean buryLavaDragonBones() {
		if (!buryBones)
			return false;
		RSItem[] bones = Inventory.find("Lava dragon bones");
		if (bones.length == 0)
			return false;
		if (bones[0].click()) {
			status = "Burying Bones";
			Timing.waitCondition(new Condition() {
				public boolean active() {
					sleep(100);
					return Player.getAnimation() == -1;
				}
			}, 1000);
		}
		return true;
	}

	private enum Route {
		TO_DRAGONS,
		TO_BANK
	}

	private boolean shouldChangeWorlds(Route route) {
		if (Player.getRSPlayer()
				.isInCombat()) {
			return false;
		}
		int wilderness = Wilderness.getLevel();
		switch (route) {
			case TO_DRAGONS:
				if (wilderness > 40) {
					if (!WorldHopper.onMembersWorld()) {
						return true;
					}
				}
				else if (wilderness > 1) {
					if (toDragonWorld == WorldType.FREE) {
						if (WorldHopper.onMembersWorld()) {
							return true;
						}
					}
				}
				break;
			case TO_BANK:
				if (wilderness > 30) {
					if (toBankWorld == WorldType.FREE) {
						if (WorldHopper.onMembersWorld() && !LAVA_DRAGON_ISLE.contains(Player.getPosition())) {
							return true;
						}
					}
				}
				else if (wilderness <= 30) {
					if (!WorldHopper.onMembersWorld()) {
						return true;
					}
				}
		}
		return false;
	}

	private boolean changeWorlds(Route route) {
		if (Player.getRSPlayer()
				.isInCombat()) {
			return false;
		}
		int wilderness = Wilderness.getLevel();
		switch (route) {
			case TO_DRAGONS:
				if (wilderness > 40) {
					if (!WorldHopper.onMembersWorld()) {
						status = "Changing to a Members World";
						return WorldHopper.changeWorld(WorldHopper.getRandomWorld(WorldType.MEMBERS));
					}
				}
				else if (wilderness > 1) {
					if (toDragonWorld == WorldType.FREE) {
						if (WorldHopper.onMembersWorld()) {
							status = "Changing to a Free World";
							return WorldHopper.changeWorld(WorldHopper.getRandomWorld(WorldType.FREE));
						}
					}
				}
				break;
			case TO_BANK:
				if (wilderness > 30) {
					if (toBankWorld == WorldType.FREE) {
						if (WorldHopper.onMembersWorld() && !LAVA_DRAGON_ISLE.contains(Player.getPosition())) {
							status = "Changing to a Free World";
							return WorldHopper.changeWorld(WorldHopper.getRandomWorld(WorldType.FREE));
						}
					}
				}
				else {
					if (!WorldHopper.onMembersWorld()) {
						status = "Changing to a Members World";
						return WorldHopper.changeWorld(WorldHopper.getRandomWorld(WorldType.MEMBERS));
					}
				}
		}
		return true;
	}

	private boolean travel(Route route) {
		if (shouldChangeWorlds(route)) {
			return changeWorlds(route);
		}
		WalkingCondition changeWorlds = new WalkingCondition() {
			public State action() {
				if (shouldChangeWorlds(route)) {
					return State.EXIT_OUT_WALKER_SUCCESS;
				}
				return State.CONTINUE_WALKER;
			}
		};
		WalkingCondition gloryTeleport = new WalkingCondition() {
			public State action() {
				int wilderness = Wilderness.getLevel();
				if (wilderness > 0 && wilderness <= 30) {
					return State.EXIT_OUT_WALKER_SUCCESS;
				}
				return State.CONTINUE_WALKER;
			}
		};
		WalkingCondition doNotBank = new WalkingCondition() {
			public State action() {
				if (!needToBank()) {
					return State.EXIT_OUT_WALKER_SUCCESS;
				}
				return State.CONTINUE_WALKER;
			}
		};
		switch (route) {
			case TO_DRAGONS:
				status = "Traveling to Lava Dragon Isle";
				return Walking.travel(LOCATION.getSafeTile(), walkingCondition.combine(changeWorlds));
			case TO_BANK:
				status = "Traveling to Bank";
				return Walking.travelToBank(walkingCondition.combine(changeWorlds)
						.combine(gloryTeleport)
						.combine(doNotBank));
		}
		return true;
	}

	private boolean drop(String[] names) {
		int length = Inventory.getAll().length;
		if (Inventory.drop(names) > 0) {
			status = "Dropping Items";
			Timing.waitCondition(new Condition() {
				public boolean active() {
					sleep(100);
					return Inventory.getAll().length != length;
				}
			}, 1000);
			sleep(General.randomSD(700, 200));
			return true;
		}
		return false;
	}

	private boolean isSpellSetByInterface(CastingStyle STYLE) {
		RSInterfaceChild child = Interfaces.get(593, STYLE.getChildID());
		if (child == null)
			return false;
		return child.getTextureID() == 653 ? false : true;
	}

	private boolean setSpell(Spell SPELL, CastingStyle STYLE) {
		if (SPELL.isTrident() || isSpellSetByInterface(STYLE))
			return true;
		if (Tab.open(TABS.COMBAT)) {
			status = "Checking Autocast...";
			Timing.waitCondition(new Condition() {
				public boolean active() {
					sleep(50);
					return evade || isSpellSetByInterface(STYLE);
				}
			}, General.randomSD(1500, 500));
			if (isSpellSetByInterface(STYLE)) {
				status = "Autocast is set!";
				return true;
			}
		}
		if (Tab.open(TABS.COMBAT)) {
			ABC.setTime(Time.ACTION);
			if (!Interfaces.isInterfaceValid(201)) {
				RSInterfaceChild child = Interfaces.get(593, STYLE.getChildID());
				if (child == null)
					return false;
				if (child.click()) {
					status = "Clicking Choose Spell";
					long timer = System.currentTimeMillis() + 2000;
					while (timer > System.currentTimeMillis()) {
						if (evade)
							return false;
						if (Interfaces.isInterfaceValid(201))
							break;
						sleep(50);
					}
				}
			}
			if (Interfaces.isInterfaceValid(201)) {
				RSInterfaceChild child = Interfaces.get(SPELL.getMasterID(), SPELL.getChildID());
				if (child == null)
					return false;
				RSInterfaceComponent component = child.getChild(SPELL.getComponentID());
				if (component == null)
					return false;
				if (component.click()) {
					status = "Selecting " + SPELL.getName();
					long timer = System.currentTimeMillis() + 2000;
					while (timer > System.currentTimeMillis()) {
						if (evade)
							return false;
						if (isSpellSetByInterface(STYLE))
							return true;
						sleep(50);
					}
				}
			}
		}
		return false;
	}

	private boolean canGrindLavaScales() {
		return grindScales && Inventory.getCount(PESTLE_AND_MORTAR) > 0 && Inventory.getCount(LAVA_SCALE) > 0;
	}

	private boolean grindLavaScales() {
		if (!canGrindLavaScales())
			return false;
		if (Inventory.open()) {
			ABC.setTime(Time.ACTION);
			if (!Game.isUptext("Use " + PESTLE_AND_MORTAR + " ->")) {
				RSItem[] items = Inventory.find(PESTLE_AND_MORTAR);
				if (items.length > 0) {
					status = "Clicking " + PESTLE_AND_MORTAR;
					if (items[0].click()) {
						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep(50);
								return Game.isUptext("Use " + PESTLE_AND_MORTAR + " ->");
							}
						}, 1000);
					}
				}
			}
			if (Game.isUptext("Use " + PESTLE_AND_MORTAR + " ->")) {
				RSItem[] items = Inventory.find(LAVA_SCALE);
				if (items.length > 0) {
					status = "Using " + PESTLE_AND_MORTAR + " on " + LAVA_SCALE;
					if (items[0].click()) {
						status = "Grinding";
						int COUNT_BEFORE = Inventory.getCount(LAVA_SCALE_SHARD);
						long timer = System.currentTimeMillis() + 2000;
						while (timer > System.currentTimeMillis()) {
							if (evade)
								return false;
							int CURRENT_COUNT = Inventory.getCount(LAVA_SCALE_SHARD);
							if (CURRENT_COUNT != COUNT_BEFORE) {
								timer = System.currentTimeMillis() + 2000;
								COUNT_BEFORE = CURRENT_COUNT;
							}
							if (Inventory.getCount(LAVA_SCALE) == 0)
								break;
						}
					}
				}
			}
		}
		return true;
	}

	private class BankItem {

		private String[] names;
		private int amount;
		private int id;
		private boolean required;
		private int price;

		private BankItem(String[] names, int amount, int id, boolean required) {
			this.names = names;
			this.amount = amount;
			this.id = id;
			this.required = required;
			this.price = Pricing.getPrice(names[0], id);
		}

		private BankItem(String[] names, int amount, int id) {
			this(names, amount, id, false);
		}

		private BankItem(String name, int amount, int id, boolean required) {
			this(new String[] { name }, amount, id, required);
		}

		public BankItem(String name, int amount, int id) {
			this(new String[] { name }, amount, id, false);
		}

		private String[] getName() {
			return this.names;
		}

		private int getAmount() {
			return this.amount;
		}

		private int getID() {
			return this.id;
		}

		private void setID(int id) {
			this.id = id;
		}

		private int getPrice() {
			if (this.id == 0)
				return 0;
			if (this.price > 0)
				return this.price;
			this.price = Pricing.getPrice(this.getName()[0], this.id);
			return this.price;
		}

		private boolean isRequired() {
			return this.required;
		}
	}

	private boolean onSafeTile() {
		return Player.getPosition()
				.distanceTo(LOCATION.getSafeTile()) == 0;
	}

	private boolean sleepWhileAttacking(RSNPC npc) {
		try {
			if (npc == null)
				return false;
			ABC.setTime(Time.START);
			long timer = System.currentTimeMillis() + General.randomSD(5000, 1000);
			while (timer > System.currentTimeMillis()) {
				if (evade || newLevelInterfaceIsUp() || !onSafeTile() || drinkMagicPotion() || makeRoom()) {
					return false;
				}
				if (Player.getAnimation() != -1) {
					status = "Attacking Lava Dragon";
					ABC.setTime(Time.COMBAT);
					timer = System.currentTimeMillis() + General.randomSD(4000, 500);
				}
				if (performAntiban()) {
					status = "ABC2 Antiban";
					timer = System.currentTimeMillis() + General.randomSD(4000, 500);
				}
				else {
					WorldHopper.hoverOverLogout();
				}
				if (npc == null || npc.getHealthPercent() == 0) {
					status = "Lava Dragon is dead!";
					return true;
				}
				sleep(25);
			}
			return true;
		}
		finally {
			ABC.setTime(Time.END);
			ABC.generateTrackers(ABC.getWaitingTime(), true, ABC.shouldOpenMenu(), ABC.wasCombatRecent(), false);
		}
	}

	private boolean sleepUntilLootAppears() {
		status = "Waiting for loot...";
		long timer = System.currentTimeMillis() + General.randomSD(5000, 500);
		while (timer > System.currentTimeMillis()) {
			if (evade || !onSafeTile())
				return false;
			if (loot.findLoot().length > 0)
				return true;
			sleep(0, 50);
		}
		return false;
	}

	private boolean makeRoom() {
		if (onSafeTile()) {
			if (Inventory.getAll().length <= 24)
				return false;
		}
		else {
			if (!Inventory.isFull())
				return false;
		}
		if (Bag.canDepositExcept(DONT_DEPOSIT_LOOTING_BAG)) {
			status = "Using Looting bag";
			Bag.depositExcept(DONT_DEPOSIT_LOOTING_BAG);
			return true;
		}
		else if (canGrindLavaScales()) {
			status = "Grinding scales";
			grindLavaScales();
			return true;
		}
		else if (Consumables.hasFood()) {
			status = "Eating food";
			Consumables.eat();
			return true;
		}
		else if (Inventory.getCount(MAGIC_POTION) > 0) {
			status = "Dropping magic potion";
			Inventory.drop(1, MAGIC_POTION);
			return true;
		}
		else if (Inventory.getCount(ENERGY_POTION) > 0) {
			if (Game.getRunEnergy() != 100) {
				status = "Drinking energy potion";
				Consumables.drink(ENERGY_POTION);
			}
			else {
				status = "Dropping energy potion";
				Inventory.drop(1, ENERGY_POTION);
			}
			return true;
		}
		else if (Inventory.getCount(PESTLE_AND_MORTAR) > 0) {
			status = "Dropping Pestle and mortar";
			Inventory.drop(1, PESTLE_AND_MORTAR);
			return true;
		}
		return false;
	}

	private boolean newLevelInterfaceIsUp() {
		if (Interfaces.isInterfaceValid(233)) {
			status = "Level Up!";
			return true;
		}
		return false;
	}

	private RSNPC getNPC(String NPC_NAME, RSTile SAFE_TILE, int MAXIMUM_DISTANCE_FROM_SAFE_TILE) {
		RSNPC[] npcs = NPCs.findNearest(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				RSTile tile = npc.getPosition();
				if (tile == null)
					return false;
				if (tile.distanceTo(SAFE_TILE) > MAXIMUM_DISTANCE_FROM_SAFE_TILE)
					return false;
				String name = npc.getName();
				if (name == null)
					return false;
				if (!name.equalsIgnoreCase(NPC_NAME))
					return false;
				return true;
			}
		});
		if (npcs.length == 0)
			return null;
		RSNPC target = null;
		for (RSNPC npc : npcs) {
			if (target == null || (npc.getHealthPercent() < target.getHealthPercent()))
				target = npc;
		}
		return target;
	}

	private boolean attackNPC(RSNPC npc) {
		status = "ABC2 Reaction Sleep";
		try {
			ABC.sleepReactionTime(new Condition() {
				public boolean active() {
					return evade || Player.getRSPlayer()
							.isInCombat() ||
							loot.findLoot().length > 0 ||
							Player.getPosition()
									.distanceTo(LOCATION.getSafeTile()) > 0;
				}
			});
		}
		catch (InterruptedException e) {
		}
		if (npc == null)
			return false;
		String name = npc.getName();
		if (name == null)
			return false;
		ABC.setTime(Time.ACTION);
		if (!npc.isOnScreen() || !npc.isClickable()) {
			status = "Turning Camera to " + name;
			camera.adjustTo(npc);
		}
		status = "Clicking " + name;
		return DynamicClicking.clickRSNPC(npc, "Attack " + name);
	}

	private boolean changeWorlds() {
		int world = WorldHopper.getRandomWorld(WorldType.MEMBERS);
		status = "Changing to world " + world + ".";
		int current = WorldHopper.getCurrentWorld();
		if (WorldHopper.changeWorld(world)) {
			status = "Changed from " + current + " to " + world + ".";
			println(status);
			hops++;
			return true;
		}
		return false;
	}

	private boolean bank() {
		if (Banking.open()) {
			status = "Bank is open";
			if (needEquipment() || died) {
				if (Inventory.getAll().length != 0) {
					status = "Deposit All";
					Banking.depositAll();
				}
				for (String item : startingEquipment) {
					status = "Withdrawing Equipment";
					if (item.contains("glory")) {
						if (!Equipment.isEquipped(AMULET_OF_GLORY) && Inventory.getCount(AMULET_OF_GLORY) == 0) {
							if (!Banking.withdraw(1, AMULET_OF_GLORY))
								println("Failed withdrawing " + item);
						}
					}
					else if (item.contains("rident")) {
						if (!Equipment.isEquipped(Trident.toString(SPELL)) && Inventory.getCount(Trident.toString(SPELL)) == 0) {
							if (!Banking.withdraw(1, Trident.toString(SPELL)))
								println("Failed withdrawing " + item);
						}
					}
					else {
						if (!Equipment.isEquipped(item) && Inventory.getCount(item) == 0) {
							if (!Banking.withdraw(1, item))
								println("Failed withdrawing " + item);
						}
					}
				}
				if (Equipment.equipAll()) {
					if (!needEquipment()) {
						println("Withdrew and equipped starting equipment.");
						died = false;
					}
					else {
						if (Equipment.getItem(SLOTS.WEAPON) == null && Inventory.find(Filters.Items.nameContains("Staff", "Trident")).length == 0)
							println("We are missing a Staff.");
						if (Equipment.getItem(SLOTS.SHIELD) == null && Inventory.getCount("Anti-dragon shield") == 0)
							println("We are missing an Anti-dragon shield.");
						return false;
					}
				}
			}
			else {
				if (Banking.depositExcept(DONT_DEPOSIT_BANK)) {
					status = "Deposited Items";
				}
				if (Inventory.find("Looting bag").length > 0) {
					if (useLootingBag) {
						if (!Bag.Bank.isBagEmpty()) {
							if (Bag.Bank.depositLoot()) {
								status = "Closed Bag";
							}
							return true;
						}
					}
					else {
						Banking.deposit(1, "Looting bag");
					}
				}
				else {
					if (useLootingBag && Banking.find("Looting bag") != null) {
						Banking.withdraw(1, "Looting bag");
					}
				}
				if (SPELL.isTrident() && Trident.isUncharged(SPELL)) {
					status = "Trident is uncharged";
					for (Item i : SPELL.getItems()) {
						if (!Banking.withdraw(i.getAmount() * 2500, i.getName())) {
							println("Failed to withdraw " + i.getName());
						}
						else if (calculateTrueProfit) {
							int value = Pricing.getPrice(i.getName(), i.getID());
							expense += 2500 * value;
							println("Subtracting " + TabletsPaint.format(value * 2500) + " from profit for " + i.getName());
						}
					}
					if (Trident.hasItemsToCharge(SPELL)) {
						if (Banking.close()) {
							status = "Charging " + SPELL.getName();
							if (Trident.chargeTrident(SPELL)) {
								status = SPELL.getName() + " is charged";
							}
							return true;
						}
					}
					else {
						println("We do not have enough supplies to add 2500 charges to " + SPELL.getName() + ".");
						run = false;
						return false;
					}
				}

				for (BankItem bankItem : bankItems) {
					RSItem[] items = Banking.find(bankItem.getName());
					if (items.length == 0) {
						if (bankItem.isRequired()) {
							println("Failed withdrawing " + bankItem.getName()[0]);
							return false;
						}
						if (useEnergyPotion && bankItem.getName()[0].contains("Energy")) {
							if (!Banking.has(bankItem.getName())) {
								println("Unable to find Energy potion.");
								useEnergyPotion = false;
							}
						}
						if (useMagicPotion && bankItem.getName()[0].contains("Magic")) {
							if (!Banking.has(bankItem.getName())) {
								println("Unable to find Magic potion.");
								useMagicPotion = false;
							}
						}
						continue;
					}
					RSItem item = items[0];
					if (item == null)
						continue;
					RSItemDefinition d = item.getDefinition();
					if (d == null) {
						continue;
					}
					String name = d.getName();
					if (name == null) {
						continue;
					}
					if (bankItem.getID() == 0) {
						bankItem.setID(item.getID());
					}
					final int count = Inventory.getCount(bankItem.getName());
					status = "Withdrawing " + name;
					if (Banking.withdraw(bankItem.getAmount(), bankItem.getName())) {
						if (calculateTrueProfit) {
							expense += (Math.abs(count - Inventory.getCount(bankItem.getName())) * bankItem.getPrice());
						}
					}
				}

				if (!Equipment.isEquipped(AMULET_OF_GLORY) && Inventory.getCount(AMULET_OF_GLORY) == 0) {
					if (Inventory.isFull())
						Banking.deposit(1, FOOD_NAME);
					if (Inventory.getCount(AMULET_OF_GLORY) == 0) {
						if (!Banking.withdraw(1, AMULET_OF_GLORY)) {
							println("Failed withdrawing Amulet of glory");
							return false;
						}
					}
					if (!usingOccult && Inventory.getCount(AMULET_OF_GLORY) > 0) {
						if (Equipment.equip(AMULET_OF_GLORY)) {
							status = "Equipped Amulet of glory";
						}
					}
				}
			}
		}
		return true;
	}

	private boolean needToBank() {
		if (!WorldHopper.isInGame())
			return false;
		if (Inventory.getCount("Draconic visage") > 0)
			return true;
		if (needEquipment() || needGlory())
			return true;
		if (Inventory.getCount(FOOD_NAME) == 0 && (ABC.shouldEat() || !LAVA_DRAGON_ISLE.contains(Player.getPosition())))
			return true;
		if (Inventory.isFull() && Inventory.getCount(FOOD_NAME) == 0) {
			if (useLootingBag && Bag.isFull()) {
				if (Inventory.getCount(PESTLE_AND_MORTAR) > 0) {
					if (Inventory.getCount(LAVA_SCALE) == 0)
						return true;
				}
				else {
					return true;
				}
			}
			else {
				if (Inventory.getCount(PESTLE_AND_MORTAR) > 0) {
					if (Inventory.getCount(LAVA_SCALE) == 0)
						return true;
				}
				else {
					return true;
				}
			}
		}
		if (SPELL.isTrident()) {
			if (Trident.isUncharged(SPELL))
				return true;
		}
		else {
			for (Item i : SPELL.getItems()) {
				if (i.getName()
						.equals("Air rune")) {
					if (!usingSmoke) {
						if (Inventory.getCount(i.getID()) < i.getAmount() || ((Inventory.getCount(i.getID()) / i.getAmount()) > SPELLS_PER_TRIP))
							return true;
					}
				}
				else {
					if (Inventory.getCount(i.getID()) < i.getAmount() || ((Inventory.getCount(i.getID()) / i.getAmount()) > SPELLS_PER_TRIP))
						return true;
				}
			}
		}
		if ((!Wilderness.isIn() && !CORPOREAL_BEAST.contains(Player.getPosition())) && ((Inventory.getCount(DONT_DEPOSIT_BANK) != Inventory.getAll().length) || (Inventory.getCount(FOOD_NAME) < FOOD_QUANTITY && !Inventory.isFull()) ||
				(toLavaDragonsItem == TeleportItems.BURNING_AMULET && Inventory.getCount(BURNING_AMULET) == 0) ||
				(toLavaDragonsItem == TeleportItems.GAMES_NECKLACE && Inventory.getCount(GAMES_NECKLACE) == 0) ||
				(!usingOccult && !Equipment.isEquipped(AMULET_OF_GLORY)) ||
				(usingOccult && Inventory.getCount(AMULET_OF_GLORY) == 0) ||
				(grindScales && Inventory.getCount(PESTLE_AND_MORTAR) == 0) ||
				(useEnergyPotion && Inventory.getCount(ENERGY_POTION) < ENERGY_POTIONS_PER_TRIP) ||
				(useMagicPotion && Inventory.getCount(MAGIC_POTION) < MAGIC_POTIONS_PER_TRIP))) {
			return true;
		}
		return false;
	}

	private boolean needGlory() {
		return (!usingOccult && Equipment.getItem(SLOTS.AMULET) == null) || (usingOccult && Inventory.getCount(AMULET_OF_GLORY) == 0);
	}

	private boolean needEquipment() {
		if (Equipment.getItem(SLOTS.WEAPON) == null || Equipment.getItem(SLOTS.SHIELD) == null)
			return true;
		return false;
	}

	@Override
	public void onPaint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		int x, y;
		long currentTime = System.currentTimeMillis();
		long time = currentTime - startTime;
		int deathsPerHour = (int) (deaths * 3600000D / time);
		int hopsPerHour = (int) (hops * 3600000D / time);
		int tripsPerHour = (int) (trips * 3600000D / time);
		int killsPerHour = (int) (kills * 3600000D / time);
		profit = 0;
		if (loot != null)
			profit = loot.getProfit() - expense;
		int profitPerHour = (int) (profit * 3600000D / time);
		int xpGained = Skills.getXP(SKILLS.MAGIC) - startXP;
		int xpPerHour = (int) (xpGained * 3600000D / time);
		int actualLevel = Skills.getActualLevel(SKILLS.MAGIC);
		int currentLevel = Skills.getCurrentLevel(SKILLS.MAGIC);
		Color background = new Color(119, 64, 61, 200);
		if (paintLootTable) {
			if (CURRENT_TARGET != null && CURRENT_TARGET.isOnScreen()) {
				g2.setColor(new Color(119, 64, 61, 75));
				RSModel m = CURRENT_TARGET.getModel();
				if (m != null) {
					Polygon p = m.getEnclosedArea();
					if (p != null)
						g2.fillPolygon(p);
				}
			}
			if (LOCATION != null) {
				if (LOCATION.getSafeTile()
						.isOnScreen()) {
					Polygon p = Projection.getTileBoundsPoly(LOCATION.getSafeTile(), 0);
					if (p != null) {
						g2.setColor(new Color(50, 200, 50, 50));
						g2.fillPolygon(Projection.getTileBoundsPoly(LOCATION.getSafeTile(), 0));
						g2.setColor(new Color(50, 200, 50, 255));
						g2.drawPolygon(Projection.getTileBoundsPoly(LOCATION.getSafeTile(), 0));
					}
				}
			}
			if (Walking.getPaintPath() != null) {
				for (RSTile tile : Walking.getPaintPath()) {
					if (Walking.isTileOnMinimap(tile)) {
						Point point = Projection.tileToMinimap(tile);
						if (point != null) {
							g2.setColor(new Color(50, 200, 50, 255));
							g2.fillRect(point.x, point.y, 2, 2);
						}
						if (tile.isOnScreen()) {
							Polygon poly = Projection.getTileBoundsPoly(tile, 0);
							if (poly != null) {
								g2.setColor(new Color(50, 200, 50, 50));
								g2.fillPolygon(poly);
								g2.setColor(new Color(50, 200, 50, 255));
								g2.drawPolygon(poly);
							}
						}
					}
				}
			}
			if (loot != null && loot.getLootItems().length > 0) {
				g2.setFont(new Font("Tahoma", Font.BOLD, 11));
				g2.setColor(background);
				x = 10;
				y = 42;
				int spacing = 14;
				int height = 25;
				for (LootItem i : loot.getLootItems()) {
					if (i.getCount() > 0 && ((i.getValue() * i.getCount()) >= MINIMUM_LOOT_VALUE))
						height += 14;
				}
				g2.fillRoundRect(x - 5, y - 15, 210, height, 16, 16);
				g2.setColor(Color.WHITE);
				g2.drawRoundRect(x - 5, y - 15, 210, height, 16, 16);
				g2.drawString(TabletsPaint.format(MINIMUM_LOOT_VALUE) + "+ gp Loot Table", x + 40, y);
				y += 18;
				g2.drawLine(6, 47, 214, 47);
				for (LootItem i : loot.getLootItems()) {
					if (i.getCount() > 0 && ((i.getValue() * i.getCount()) >= MINIMUM_LOOT_VALUE)) {
						if (i.getName()
								.equalsIgnoreCase("Looting bag"))
							g2.drawString(TabletsPaint.format(i.getCount()) + " " + i.getName(), x, y);
						else
							g2.drawString(TabletsPaint.format(i.getCount()) + " " + i.getName() + " (" + TabletsPaint.format(i.getValue()) + " gp)", x, y);
						y += spacing;
					}
				}
				y += 10;
			}
		}
		g.setColor(Color.WHITE);
		g2.drawImage(image, 8, 300, null);
		g2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g2.drawString(version, 495, 318);
		g2.setFont(new Font("Tahoma", Font.BOLD, 11));
		g2.drawString(Timing.msToString(time), 288, 334);
		g2.drawString(status, 299, 349);
		if (loot != null)
			g2.drawString(TabletsPaint.format(loot.getItemCount("Draconic visage")), 345, 365);
		g2.drawString(TabletsPaint.format(profit) + " gp (" + TabletsPaint.format(profitPerHour) + "/hr)", 299, 380);
		g2.drawString(TabletsPaint.format(kills) + " (" + TabletsPaint.format(killsPerHour) + "/hr)", 291, 395);
		g2.drawString(TabletsPaint.format(trips) + " (" + TabletsPaint.format(tripsPerHour) + "/hr)", 293, 409);
		g2.drawString(TabletsPaint.format(hops) + " (" + TabletsPaint.format(hopsPerHour) + "/hr)", 342, 424);
		g2.drawString(TabletsPaint.format(deaths) + " (" + TabletsPaint.format(deathsPerHour) + "/hr)", 301, 439);
		g2.drawString(currentLevel + "/" + actualLevel + " (+" + (actualLevel - startLVL) + ")", 322, 455);
		g2.drawString(TabletsPaint.format(xpGained) + " (" + TabletsPaint.format(xpPerHour) + "/hr)", 314, 470);
		x = 260;
		y = 477;
		int xpTNL = Skills.getXPToNextLevel(SKILLS.MAGIC);
		int percentTNL = Skills.getPercentToNextLevel(SKILLS.MAGIC);
		long TTNL = 0;
		if (xpPerHour > 0) {
			TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
		}
		int percentFill = (250 * percentTNL) / 100;
		g2.setColor(Color.RED);
		g2.fillRoundRect(x, y, 250, 16, 8, 8);
		Color green = new Color(10, 150, 10);
		g2.setColor(green);
		g2.fillRoundRect(x, y, percentFill, 16, 8, 58);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(x, y, 250, 16, 8, 8);
		g2.drawString(TabletsPaint.format(xpTNL) + " xp to " + (actualLevel + 1) + " | " + Timing.msToString(TTNL), x + 50, y + 12);
		if (GameTab.getOpen() == GameTab.TABS.INVENTORY) {
			Rectangle bag = Bag.getBounds();
			if (bag != null) {
				g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
				if (Bag.isFull()) {
					g.setColor(Color.GREEN);
					g.drawString("Full", bag.x + (bag.width / 4), bag.y + (bag.height / 2));
				}
				else if (Bag.hasLoot()) {
					g.setColor(Color.YELLOW);
					g.drawString("In Use", bag.x + (bag.width / 10), bag.y + (bag.height / 2));
				}
				else {
					g.setColor(Color.RED);
					g.drawString("Empty", bag.x + (bag.width / 10), bag.y + (bag.height / 2));
				}
				g.drawRoundRect(bag.x, bag.y, bag.width, bag.height, 5, 5);
			}
		}
		if (CURRENT_TARGET != null) {
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
			x = 30;
			y = 470;
			g.drawString("Lava Dragon Health: " + Math.round(CURRENT_TARGET.getHealthPercent() * 100) + "%", x, y);
		}
	}

	public class Threat implements Runnable {

		private volatile boolean running = true;
		private long screenshot = 0;

		public void stop() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					if (Login.getLoginState() == Login.STATE.INGAME) {
						RSPlayer player = Player.getRSPlayer();
						if (player == null)
							continue;
						String name = player.getName();
						if (name == null)
							continue;
						int level = player.getCombatLevel();
						if (Skills.getCurrentLevel(SKILLS.HITPOINTS) <= 5 && screenshot < System.currentTimeMillis()) {
							try {
								SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh-mm-ss a");
								Calendar cal = Calendar.getInstance();
								String date = dateFormat.format(cal.getTime());
								File folder = new File(Util.getWorkingDirectory() + "/USA Lava Dragons/deaths");
								if (!folder.exists())
									folder.mkdir();
								ImageIO.write(Screen.getGameImage(), "png", new File(Util.getWorkingDirectory() + "/USA Lava Dragons/deaths", name + " " + date + ".png"));
								screenshot = System.currentTimeMillis() + 5000;
							}
							catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (!evade) {
							int wilderness = Wilderness.getLevel();
							if (wilderness > 0) {
								RSPlayer[] players = Players.getAll(new Filter<RSPlayer>() {
									public boolean accept(RSPlayer player) {
										String playerName = player.getName();
										if (playerName == null || playerName.equalsIgnoreCase(name))
											return false;
										return player.getCombatLevel() >= (level - wilderness) && player.getCombatLevel() <= (level + wilderness);
									}
								});
								if (players.length > 0) {
									println("\"" + players[0].getName() + "\" (" + "Level " + players[0].getCombatLevel() + ") is " + Player.getPosition()
											.distanceTo(players[0]) + " tile(s) away in wilderness level " + wilderness + ".");
									println(getEquipment(players[0]));
									evade = true;
								}
							}
						}
					}
					Thread.sleep(25);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getEquipment(RSPlayer player) {
		if (player == null)
			return null;
		RSPlayerDefinition playerDefinition = player.getDefinition();
		if (playerDefinition == null)
			return null;
		RSItem[] equipment = playerDefinition.getEquipment();
		if (equipment.length == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (RSItem e : equipment) {
			RSItemDefinition itemDefinition = e.getDefinition();
			if (itemDefinition != null) {
				String name = itemDefinition.getName();
				if (name != null)
					sb.append(name + ", ");
			}
		}
		return sb.toString()
				.substring(0, sb.length() - 2);
	}

	private boolean openConnection() {
		try {
			URL url = new URL("http://usa-tribot.org/lava/statistics.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == 200)
				return true;
			conn.disconnect();
		}
		catch (ConnectException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean sendServerData(String username, long duration, int visage, int profit, int profit_per_hour, int xp, int trips, int hops, int kills, int kills_per_hour, int deaths, String version) {
		try {
			URL url = new URL("http://usa-tribot.org/lava/data.php?username=" + username +
					"&duration=" +
					duration +
					"&visage=" +
					visage +
					"&profit=" +
					profit +
					"&profit_per_hour=" +
					profit_per_hour +
					"&xp=" +
					xp +
					"&trips=" +
					trips +
					"&hops=" +
					hops +
					"&kills=" +
					kills +
					"&kills_per_hour=" +
					kills_per_hour +
					"&deaths=" +
					deaths +
					"&version=" +
					version);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			try {
				String response = br.readLine();
				return Boolean.valueOf(response);
			}
			finally {
				br.close();
			}
		}
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException when sending session data.");
		}
		catch (IOException e) {
			System.out.println("IOException when sending session data.");
		}
		return false;
	}

	@Override
	public void onEnd() {
		if (thread != null) {
			System.out.println("Stopping Threat thread.");
			runnable.stop();
			try {
				thread.join();
			}
			catch (InterruptedException e) {
			}
		}
		String playerName = Player.getRSPlayer()
				.getName();
		if (playerName != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh-mm-ss a");
			Calendar cal = Calendar.getInstance();
			String date = dateFormat.format(cal.getTime());
			File folder = new File(Util.getWorkingDirectory() + "/USA Lava Dragons/sessions");
			if (!folder.exists())
				folder.mkdir();
			try {
				ImageIO.write(Screen.getGameImage(), "png", new File(Util.getWorkingDirectory() + "/USA Lava Dragons/sessions", playerName + " " + date + ".png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		long time = System.currentTimeMillis() - startTime;
		int xp = Skills.getXP(SKILLS.MAGIC) - startXP;
		int profitPerHour = (int) (profit * 3600000D / time);
		int killsPerHour = (int) (kills * 3600000D / time);
		println("------------------------------");
		println("Total time: " + Timing.msToString(time));
		println("Our total profit was: " + TabletsPaint.format(profit) + " gp (" + TabletsPaint.format(profitPerHour) + "/hr)");
		println("We killed " + kills + " Lava Dragons! (" + killsPerHour + "/hr)");
		println("We gained " + xp + " xp.");
		println("We gained " + loot.getItemCount("Draconic visage") + " Draconic visages!");
		println("Thank you for using USA Lava Dragons " + version);
		println("------------------------------");
		if (profit > 0) {
			if (xp < 0)
				xp = 0;
			try {
				if (sendServerData(URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"), time, loot.getItemCount("Draconic visage"), profit, profitPerHour, xp, trips, hops, kills, killsPerHour, deaths, version)) {
					println("Successfully sent session data!");
				}
				else {
					println("Error sending session data!");
				}
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void serverMessageReceived(String message) {
		if (message.contains("The bag's too full.")) {
			Bag.setFull();
		}
		else if (message.contains("Oh dear, you are dead!")) {
			Bag.setEmpty();
			println(message);
			if (calculateTrueProfit) {
				profit -= tripProfit;
				println("Subtracted " + TabletsPaint.format(tripProfit) + " from our profit.");
			}
			died = true;
			deaths++;
			CURRENT_TARGET = null;
			status = "Waiting to change worlds.";
			sleep(General.random(10000, 15000));
			long timer = System.currentTimeMillis() + 20000;
			while (timer > System.currentTimeMillis()) {
				if (changeWorlds())
					break;
			}
		}
	}

	@Override
	public void playerMessageReceived(String username, String message) {
		if (useAutoResponder && Wilderness.isIn())
			responder.generateResponse(username, message);
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
	public void tradeRequestReceived(String arg0) {
	}

	@Override
	public void mouseClicked(Point p, int button, boolean isBot) {
		Rectangle r = new Rectangle(10, 459, 90, 15);
		if (r.contains(p) && button == 1 && !isBot)
			paint_toggle = !paint_toggle;
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
	public void onBreakEnd() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBreakStart(long arg0) {
		if (LAVA_DRAGON_ISLE.contains(Player.getPosition())) {
			if (!onSafeTile()) {
				Walking.travel(LOCATION.getSafeTile());
			}
		}
		Login.logout();
	}
}
