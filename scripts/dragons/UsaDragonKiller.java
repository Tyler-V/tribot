package scripts.dragons;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSAnimableEntity;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.responder.AutoResponder;
import scripts.usa.api.teleporting.Teleport;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Options;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.entity.Npc;
import scripts.usa.api2007.entity.Targets;
import scripts.usa.api2007.entity.preferences.NpcPreferences;
import scripts.usa.api2007.looting.Bag;
import scripts.usa.api2007.looting.LootItem;
import scripts.usa.api2007.looting.Looting;
import scripts.usa.api2007.minigames.ClanWars;
import scripts.usa.api2007.util.condition.Conditional;
import scripts.usa.api2007.util.condition.Conditions;
import scripts.usa.api2007.webwalker_logic.local.walker_engine.WalkingCondition;
import scripts.usa.api2007.worlds.WorldHopper;
import scripts.usa.api2007.worlds.WorldType;
import scripts.usa.data.Constants;
import scripts.usa.data.Location;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Dragon Killer")
public class UsaDragonKiller extends Script implements Painting, Ending, MessageListening07, Breaking {

	public static String version = "v13.0";
	private String status = "Starting...";
	private final Image background = getImage("http://i.imgur.com/NsouSeR.png");
	private boolean run = true;

	// Options
	private static int minLootValue;
	private static int maxLootValue;
	private static int foodQuantity;
	private static String foodName;
	private static int maxPlayers;
	private static int specialAttackEnergy;
	private static boolean pickupClueScrolls;
	private static LOOTING_BAG_OPTION bagOption;
	private static boolean useSpecialAttack;
	private static boolean useQuickPrayer;
	private static boolean useAutoResponder;
	private static boolean useCombatPotions;
	private static boolean useSuperPotions;
	private static boolean deathWalk;
	private static boolean useABC2;
	private static DRAGON dragon;
	private static String[] dangerousEquipment;
	private static String[] startingEquipment;
	private boolean died = false;
	private boolean evade = false;
	private long teleportBlockTime;
	private int startXP = 0;
	private long startTime = 0;
	private int kills = 0;
	private int trips = 0;
	private int escape = 0;
	private int death = 0;
	private int fail = 0;

	// Classes
	private AutoResponder responder;
	private Looting loot;

	// Constants
	private final RSTile CASTLE_WARS_BANK = new RSTile(2443, 3083, 0);
	private final String[] GAMES_NECKLACE = { "Games necklace(1)", "Games necklace(2)", "Games necklace(3)", "Games necklace(4)", "Games necklace(5)", "Games necklace(6)", "Games necklace(7)", "Games necklace(8)" };
	private final String[] RING_OF_DUELING = { "Ring of dueling(1)", "Ring of dueling(2)", "Ring of dueling(3)", "Ring of dueling(4)", "Ring of dueling(5)", "Ring of dueling(6)", "Ring of dueling(7)", "Ring of dueling(8)" };
	private final String[] AMULET_OF_GLORY = { "Amulet of glory(1)", "Amulet of glory(2)", "Amulet of glory(3)", "Amulet of glory(4)", "Amulet of glory(5)", "Amulet of glory(6)" };
	private final String[] COMBAT_POTION = { "Combat potion(4)", "Combat potion(3)", "Combat potion(2)", "Combat potion(1)" };
	private final String[] SUPER_ATTACK_POTION = { "Super attack(4)", "Super attack(3)", "Super attack(2)", "Super attack(1)" };
	private final String[] SUPER_STRENGTH_POTION = { "Super strength(4)", "Super strength(3)", "Super strength(2)", "Super strength(1)" };
	private final String[] DROP_ITEMS = { "Vial" };
	private List<String> DONT_DEPOSIT_BANK;
	private List<String> DONT_DEPOSIT_LOOTING_BAG;

	// Evade
	private ThreatSearch search;
	private boolean search_started = false;

	// Enemy Player
	private ArrayList<EnemyPlayer> ENEMY_PLAYERS = new ArrayList<EnemyPlayer>();
	private static boolean gui_is_up = true;
	private static GUI gui;

	public void run() {

		while (Login.getLoginState() != Login.STATE.INGAME) {
			status = "Logging in...";
			sleep(100);
		}

		gui = new GUI();
		gui.setVisible(true);
		while (gui_is_up) {
			status = "GUI";
			sleep(200);
		}

		loot = new Looting(minLootValue, maxLootValue);
		loot.put(new LootItem("Looting bag", 11941, 0, 0, true));
		loot.put(new LootItem("Ensouled dragon head", 13511));
		if (pickupClueScrolls)
			loot.put(new LootItem("Clue scroll (hard)", 0, 0, 0, true));

		DONT_DEPOSIT_BANK = new ArrayList<String>();
		DONT_DEPOSIT_BANK.addAll(
				Arrays.asList(
						foodName,
						"Looting bag",
						GAMES_NECKLACE[7],
						GAMES_NECKLACE[6],
						GAMES_NECKLACE[5],
						GAMES_NECKLACE[4],
						GAMES_NECKLACE[3],
						GAMES_NECKLACE[2],
						GAMES_NECKLACE[1],
						GAMES_NECKLACE[0],
						RING_OF_DUELING[7],
						RING_OF_DUELING[6],
						RING_OF_DUELING[5],
						RING_OF_DUELING[4],
						RING_OF_DUELING[3],
						RING_OF_DUELING[2],
						RING_OF_DUELING[1],
						RING_OF_DUELING[0]));
		DONT_DEPOSIT_LOOTING_BAG = new ArrayList<String>();
		DONT_DEPOSIT_LOOTING_BAG.addAll(
				Arrays.asList(
						"Vial",
						"Clue scroll (hard)",
						foodName,
						"Looting bag",
						GAMES_NECKLACE[7],
						GAMES_NECKLACE[6],
						GAMES_NECKLACE[5],
						GAMES_NECKLACE[4],
						GAMES_NECKLACE[3],
						GAMES_NECKLACE[2],
						GAMES_NECKLACE[1],
						GAMES_NECKLACE[0],
						RING_OF_DUELING[7],
						RING_OF_DUELING[6],
						RING_OF_DUELING[5],
						RING_OF_DUELING[4],
						RING_OF_DUELING[3],
						RING_OF_DUELING[2],
						RING_OF_DUELING[1],
						RING_OF_DUELING[0]));

		if (useCombatPotions) {
			DONT_DEPOSIT_BANK.addAll(Arrays.asList(COMBAT_POTION[0], COMBAT_POTION[1], COMBAT_POTION[2], COMBAT_POTION[3]));
			DONT_DEPOSIT_LOOTING_BAG.addAll(Arrays.asList(COMBAT_POTION[0], COMBAT_POTION[1], COMBAT_POTION[2], COMBAT_POTION[3]));
		}
		else if (useSuperPotions) {
			DONT_DEPOSIT_BANK
					.addAll(Arrays.asList(SUPER_ATTACK_POTION[0], SUPER_ATTACK_POTION[1], SUPER_ATTACK_POTION[2], SUPER_ATTACK_POTION[3], SUPER_STRENGTH_POTION[0], SUPER_STRENGTH_POTION[1], SUPER_STRENGTH_POTION[2], SUPER_STRENGTH_POTION[3]));
			DONT_DEPOSIT_LOOTING_BAG
					.addAll(Arrays.asList(SUPER_ATTACK_POTION[0], SUPER_ATTACK_POTION[1], SUPER_ATTACK_POTION[2], SUPER_ATTACK_POTION[3], SUPER_STRENGTH_POTION[0], SUPER_STRENGTH_POTION[1], SUPER_STRENGTH_POTION[2], SUPER_STRENGTH_POTION[3]));
		}

		if (useAutoResponder) {
			super.setAutoResponderState(false);
			responder = new AutoResponder();
		}

		if (startingEquipment.length == 0) {
			println("Please restart the script and set your Starting Equipment by hitting Refresh!");
			run = false;
		}

		Camera.setCameraAngle(100);
		println("Starting PlayerSearch thread.");
		search = new ThreatSearch();
		Thread searching = new Thread(search);
		searching.start();
		search_started = true;
		startXP = Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.STRENGTH) + Skills.getXP(SKILLS.DEFENCE) + Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.RANGED) + Skills.getXP(SKILLS.MAGIC);
		startTime = System.currentTimeMillis();

		while (run) {
			if (!evade) {
				if (ABC.activateRun()) {
					status = "Activating Run";
				}
				else if (!WorldHopper.onMembersWorld()) {
					status = "We're on F2P World!";
					println("Current world \"" + WorldHopper.getCurrentWorld() + "\" is not a members world!");
					changeWorlds();
				}
				else if (eat()) {
					status = "Eating";
				}
				else if (needReset() || ClanWars.isInside()) {
					if (ClanWars.isInside()) {
						status = "Leaving clan wars";
						ClanWars.exitPortal();
					}
					else {
						travel(Route.TO_CLAN_WARS);
					}
				}
				else if (needToBank()) {
					if (Wilderness.isIn()) {
						travel(Route.TO_BANK);
					}
					else {
						if (ClanWars.isInside()) {
							status = "Exiting Portal";
							ClanWars.exitPortal();
						}
						else {
							if (!bank()) {
								fail++;
								if (fail >= 3) {
									println("Failed to bank 3 times, shutting down.");
									run = false;
								}
								sleep(General.randomSD(1000, 250));
								Banking.close();
							}
							else {
								fail = 0;
							}
						}
					}
				}
				else {
					if (!dragon.getArea()
							.contains(Player.getPosition())) {
						status = "Walking to Dragons";
						travel(Route.TO_DRAGONS);
					}
					else {
						if (tooManyPlayers(maxPlayers)) {
							status = "Too many players";
						}
						else if (drop(DROP_ITEMS)) {
							status = "Dropping Items";
						}
						else if (Inventory.isFull() && bagOption == LOOTING_BAG_OPTION.INVENTORY_IS_FULL && Bag.depositExcept(DONT_DEPOSIT_LOOTING_BAG)) {
							status = "Using Looting Bag";
						}
						else if (loot.findLoot().length > 0) {
							if (Inventory.isFull() && Consumables.hasFood()) {
								status = "Eating to make room";
								Consumables.eat();
							}
							status = "Looting";
							loot.loot();
						}
						else {
							status = "Attacking Green Dragon";
							if (Npc.interact("Attack", new Targets(dragon.getName()), new NpcPreferences(true, false), new Conditional(Conditions.ActivePlayer(), new Condition() {
								public boolean active() {
									if (evade || loot.findLoot().length > 0 || eat())
										return true;
									if (Player.getRSPlayer()
											.isInCombat()) {
										if (bagOption == LOOTING_BAG_OPTION.IN_COMBAT_OR_FULL) {
											Bag.depositExcept(DONT_DEPOSIT_LOOTING_BAG);
										}
										drinkPotions();
										activateSpecial();
									}
									General.sleep(50);
									return false;
								}
							}))) {
								status = "Waiting for loot...";
								Timing.waitCondition(Conditions.FoundLoot(loot), General.random(1000, 2000));
								if (Conditions.FoundLoot(loot)
										.active()) {
									kills++;
								}
							}
						}
					}
				}
			}
			else {
				status = "Evading";
				if (Wilderness.getLevel() > 0) {
					setQuickPrayer(true);
					if (isTeleportBlocked()) {
						status = "We are teleport blocked!";
						travel(Route.TO_BANK);
					}
					else {
						Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY);
					}
				}
				else {
					if (setQuickPrayer(false)) {
						if (changeWorlds()) {
							escape++;
							evade = false;
						}
					}
				}
			}
			if (ABC.performAntiban())
				status = "ABC2 Antiban";
			sleep(100);
		}
	}

	private enum Route {
		TO_DRAGONS,
		TO_BANK,
		TO_CLAN_WARS
	}

	private boolean travel(Route route) {
		switch (route) {
			case TO_BANK:
				if (Wilderness.getLevel() <= 20) {
					status = "Traveling to Castle Wars Bank";
					return Walking.travel(CASTLE_WARS_BANK);
				}
				else {
					status = "Traveling below 20 wilderness";
					WalkingCondition canTeleport = new WalkingCondition() {
						public State action() {
							if (Wilderness.getLevel() <= 20) {
								return State.EXIT_OUT_WALKER_SUCCESS;
							}
							return State.CONTINUE_WALKER;
						}
					};
					return Walking.travel(new RSTile(Player.getPosition()
							.getX(), 3650, 0), canTeleport);
				}
			case TO_DRAGONS:
				status = "Traveling to Dragons";
				return Walking.travel(dragon.getPath()[dragon.getPath().length - 1]);
			case TO_CLAN_WARS:
				if (ClanWars.isOutside()) {
					status = "Entering portal";
					ClanWars.enterPortal();
				}
				else {
					status = "Traveling to Clan Wars";
					Walking.travel(ClanWars.OUTSIDE_PORTAL);
				}
		}
		return true;
	}

	private boolean eat() {
		if (ABC.shouldEat() && Consumables.hasFood()) {
			status = "Eating " + foodName;
			return Consumables.eat();
		}
		return false;
	}

	public Condition conditions() {
		return new Condition() {
			public boolean active() {
				if (evade || loot.findLoot().length > 0)
					return true;
				activateSpecial();
				drinkPotions();
				eat();
				if (bagOption == LOOTING_BAG_OPTION.IN_COMBAT_OR_FULL) {
					Bag.depositExcept(DONT_DEPOSIT_LOOTING_BAG);
				}
				General.sleep(50);
				return false;
			}
		};
	}

	private boolean drop(String[] DROP_ITEMS) {
		int before = Inventory.getAll().length;
		if (Inventory.drop(DROP_ITEMS) > 0) {
			status = "Dropping items";
			Timing.waitCondition(new Condition() {
				public boolean active() {
					sleep(100);
					return Inventory.getAll().length != before;
				}
			}, 1000);
			return true;
		}
		return false;
	}

	private boolean tooManyPlayers(int amount) {
		int count = getPlayersNearMe();
		if (count < amount)
			return false;
		long timer = System.currentTimeMillis() + 20000;
		while (timer > System.currentTimeMillis()) {
			if (evade)
				return true;
			if (Player.getRSPlayer()
					.isInCombat()) {
				status = "Too many players";
				Walking.travel(dragon.getTeleportTile());
			}
			else {
				if (changeWorlds()) {
					println("Found " + count + " players in area, changing worlds!");
					return true;
				}
			}
			sleep(200);
		}
		return true;
	}

	private int getPlayersNearMe() {
		RSPlayer player = Player.getRSPlayer();
		if (player == null)
			return 0;
		String name = player.getName();
		if (name == null)
			return 0;
		return Players.getAll(Filters.Players.nameNotEquals(name)).length;
	}

	private boolean setQuickPrayer(boolean on) {
		if (!useQuickPrayer)
			return true;
		if (Options.isQuickPrayer(on))
			return true;
		if (Options.setQuickPrayer(on)) {
			status = "Turning quick prayer " + (on ? "on" : "off");
		}
		return Options.isQuickPrayer(on);
	}

	private boolean changeWorlds() {
		int w = WorldHopper.getRandomWorld(WorldType.MEMBERS);
		status = "Changing to world " + w + ".";
		int current = WorldHopper.getCurrentWorld();
		if (WorldHopper.changeWorld(w)) {
			status = "Changed from " + current + " to " + w + ".";
			println(status);
			return true;
		}
		return false;
	}

	private boolean bank() {
		status = "Banking";
		if (Banking.open()) {
			if (died || needEquipment()) {
				for (String item : startingEquipment) {
					if (item.contains("Amulet of glory")) {
						if (!Equipment.isEquipped(AMULET_OF_GLORY) && Inventory.getCount(AMULET_OF_GLORY) == 0) {
							if (!Banking.withdraw(1, AMULET_OF_GLORY)) {
								println("Could not find a charged amulet of glory!");
								return false;
							}
						}
					}
					else {
						if (!Equipment.isEquipped(item) && Inventory.getCount(item) == 0) {
							if (!Banking.withdraw(1, item))
								println("Could not find " + item + "!");
						}
					}
				}
				Equipment.equipAll();
				if (!needEquipment()) {
					println("Withdrew and equipped saved starting equipment.");
					died = false;
				}
				else {
					if (Equipment.getItem(SLOTS.WEAPON) == null)
						println("We are missing a weapon!");
					if (Equipment.getItem(SLOTS.BODY) == null)
						println("We are missing body armor!");
					if (Equipment.getItem(SLOTS.LEGS) == null)
						println("We are missing leg armor!");
					if (Equipment.getItem(SLOTS.SHIELD) == null)
						println("We are missing a shield!");
					println("Trying to withdraw equipment again...");
				}
			}
			else {
				if (Banking.depositAllExcept(DONT_DEPOSIT_BANK.toArray(new String[0])) > 0) {
					status = "Deposited items";
				}
				if (Bag.hasLootingBag()) {
					if (!Bag.Bank.isBagEmpty())
						Bag.setFull();
					if (!Bag.Bank.isBagEmpty()) {
						status = "Depositing bag items";
						if (Bag.Bank.depositLoot())
							status = "Deposited bag items";
					}
				}
				if (!Equipment.isEquipped(AMULET_OF_GLORY) && Inventory.getCount(AMULET_OF_GLORY) == 0) {
					status = "Withdrawing Amulet of Glory";
					if (!Banking.withdraw(1, AMULET_OF_GLORY)) {
						println("Failed withdrawing Amulet of Glory");
						return false;
					}
					if (Inventory.getCount(AMULET_OF_GLORY) > 0) {
						status = "Equipping Amulet of Glory";
						Equipment.equip(AMULET_OF_GLORY);
					}
				}
				if (Inventory.getCount(RING_OF_DUELING) == 0) {
					status = "Withdrawing Ring of Dueling";
					if (!Banking.withdraw(1, RING_OF_DUELING)) {
						println("Failed withdrawing Ring of Dueling");
						return false;
					}
				}
				if (Inventory.getCount(GAMES_NECKLACE) == 0) {
					status = "Withdrawing Games necklace";
					if (!Banking.withdraw(1, GAMES_NECKLACE)) {
						println("Failed withdrawing Games necklace");
						return false;
					}
				}
				if (!Bag.hasLootingBag() && Banking.find("Looting bag") != null) {
					status = "Withdrawing Looting bag";
					Banking.withdraw(1, "Looting bag");
				}
				if (needCombatPotion()) {
					status = "Withdrawing Combat potion";
					if (!Banking.withdraw(1, COMBAT_POTION)) {
						println("Failed withdrawing Combat potion");
						if (!Banking.has(COMBAT_POTION)) {
							println("Disabling option");
							useCombatPotions = false;
						}
					}
				}
				else if (needSuperPotions()) {
					if (Inventory.getCount(SUPER_ATTACK_POTION) == 0) {
						status = "Withdrawing Super attack potion";
						if (!Banking.withdraw(1, SUPER_ATTACK_POTION)) {
							println("Failed withdrawing Super attack potion");
							if (!Banking.has(SUPER_ATTACK_POTION)) {
								println("Disabling option");
								useSuperPotions = false;
							}
						}
					}
					if (Inventory.getCount(SUPER_STRENGTH_POTION) == 0) {
						status = "Withdrawing Super strength potion";
						if (!Banking.withdraw(1, SUPER_STRENGTH_POTION)) {
							println("Failed withdrawing Super strength potion");
							if (!Banking.has(SUPER_STRENGTH_POTION)) {
								println("Disabling option");
								useSuperPotions = false;
							}
						}
					}
				}
				if (Inventory.getCount(foodName) < foodQuantity) {
					status = "Withdrawing " + foodName;
					if (!Banking.withdraw(foodQuantity, foodName)) {
						println("Failed withdrawing " + foodName);
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean drinkPotions() {
		if (useCombatPotions) {
			return Consumables.drink(SKILLS.STRENGTH, COMBAT_POTION);
		}
		if (useSuperPotions) {
			return Consumables.drink(SKILLS.ATTACK, SUPER_ATTACK_POTION) || Consumables.drink(SKILLS.STRENGTH, SUPER_STRENGTH_POTION);
		}
		return false;
	}

	private boolean activateSpecial() {
		if (!useSpecialAttack)
			return false;
		if (Game.activateSpecial(specialAttackEnergy)) {
			status = "Using special attack";
		}
		return Game.isSpecialActivated();
	}

	private boolean needReset() {
		return Wilderness.isSkulled() || Game.isPoisoned() || (useQuickPrayer && Skills.getCurrentLevel(SKILLS.PRAYER) == 0);
	}

	private boolean needEquipment() {
		return Equipment.getItem(SLOTS.WEAPON) == null || Equipment.getItem(SLOTS.BODY) == null || Equipment.getItem(SLOTS.SHIELD) == null || Equipment.getItem(SLOTS.LEGS) == null;
	}

	private boolean needCombatPotion() {
		return useCombatPotions && Inventory.getCount(COMBAT_POTION) == 0;
	}

	private boolean needSuperPotions() {
		return useSuperPotions && (Inventory.getCount(SUPER_ATTACK_POTION) == 0 || Inventory.getCount(SUPER_STRENGTH_POTION) == 0);
	}

	private boolean needToBank() {
		if (needEquipment() || Inventory.getCount(foodName) == 0) {
			return true;
		}
		if (Inventory.isFull() && Inventory.getCount(foodName) == 0) {
			return !Bag.hasLootingBag() || Bag.isFull();
		}
		if (!Wilderness.isIn()) {
			if (Bag.hasLoot() || Bag.isFull()) {
				return true;
			}
			if (Inventory.getCount(foodName) < foodQuantity) {
				return true;
			}
			if (Inventory.getCount(RING_OF_DUELING) == 0 || Inventory.getCount(GAMES_NECKLACE) == 0) {
				return true;
			}
			if (!Equipment.isEquipped(AMULET_OF_GLORY) || Inventory.getCount("Amulet of glory") > 0) {
				return true;
			}
			if (needCombatPotion() || needSuperPotions()) {
				return true;
			}
		}
		return false;
	}

	private boolean isTeleportBlocked() {
		return (teleportBlockTime + 300000) > System.currentTimeMillis();
	}

	private void resetTeleportBlock() {
		teleportBlockTime = 0L;
	}

	private EnemyPlayer getPlayer(String username, ArrayList<EnemyPlayer> ENEMY_PLAYERS) {
		for (EnemyPlayer e : ENEMY_PLAYERS) {
			if (e.getUsername()
					.equalsIgnoreCase(username))
				return e;
		}
		return null;
	}

	public class ThreatSearch implements Runnable {
		private volatile boolean stop = false;
		private int PLAYER_LEVEL = 0;
		private String PLAYER_NAME = null;
		private int wilderness = 0;

		@Override
		public void run() {
			while (!stop) {
				try {
					if (!evade) {
						if (Login.getLoginState()
								.equals(Login.STATE.INGAME)) {
							wilderness = Wilderness.getLevel();
							if (Combat.getHPRatio() < 20 && wilderness != 0) {
								println("Health below 20%, lets Glory Teleport!");
								evade = true;
							}
							else if (Wilderness.isSkulled()) {
								println("Our player is skulled! Whoops, let's reset that at Clan Wars.");
								evade = true;
							}
							else {
								if (PLAYER_LEVEL == 0)
									PLAYER_LEVEL = Player.getRSPlayer()
											.getCombatLevel();
								if (PLAYER_NAME == null)
									PLAYER_NAME = Player.getRSPlayer()
											.getName();
								if (wilderness > 0) {
									RSPlayer[] players = Players.getAll(new Filter<RSPlayer>() {
										public boolean accept(RSPlayer p) {
											if (p == null)
												return false;
											String name = p.getName();
											if (name == null || name.equalsIgnoreCase(PLAYER_NAME))
												return false;
											if (name.toLowerCase()
													.contains("mod"))
												return true;
											if (ENEMY_PLAYERS.size() > 0) {
												for (EnemyPlayer e : ENEMY_PLAYERS) {
													if (name.equalsIgnoreCase(e.getUsername()))
														return true;
												}
											}
											if (p.getCombatLevel() < (PLAYER_LEVEL - wilderness))
												return false;
											if (p.getCombatLevel() > (PLAYER_LEVEL + wilderness))
												return false;
											return true;
										}
									});
									if (players.length > 0) {
										for (RSPlayer p : players) {
											if (p != null) {
												String item = playerIsWearing(p, dangerousEquipment);
												String equipment = getEquipment(p);
												int skull = p.getSkullIcon();
												int level = p.getCombatLevel();
												String name = p.getName();
												if (name != null && name.toLowerCase()
														.contains("mod")) {
													evade = true;
													println("Found potential Jagex Moderator, \"" + name + "\"");
												}
												else if (item != null) {
													evade = true;
													if (name != null)
														println("\"" + name + "\" (" + "Level " + level + ") is " + Player.getPosition()
																.distanceTo(p) + " tiles away in wilderness level " + wilderness + " with item \"" + item + "\"");
												}
												else if (p.isInteractingWithMe()) {
													evade = true;
													if (name != null)
														println("\"" + name + "\" (" + "Level " + level + ") is " + Player.getPosition()
																.distanceTo(p) + " tiles away and interacting with us in wilderness level " + wilderness + ". (Skull Icon: " + skull + ")");
												}
												else {
													if (name != null) {
														for (EnemyPlayer e : ENEMY_PLAYERS) {
															if (e.getUsername()
																	.equalsIgnoreCase(name)) {
																evade = true;
																e.add(1);
																println("\"" + p.getName() + "\" (" + "Level " + level + ") is " + Player.getPosition()
																		.distanceTo(p) + " tiles away in wilderness level " + wilderness + ". We have seen this player " + e.getCount() + " times.");
																break;
															}
														}
													}
												}
												if (evade) {
													println(equipment);
													EnemyPlayer enemy = getPlayer(name, ENEMY_PLAYERS);
													if (name != null && enemy == null)
														ENEMY_PLAYERS.add(new EnemyPlayer(name, level, 1));
													break;
												}
											}
										}
									}
								}
							}
						}
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				sleep(200);
			}
		}

		public void setStop(boolean stop) {
			System.out.println("Stopped Threat Searching thread.");
			this.stop = stop;
		}
	}

	private String getEquipment(RSPlayer player) {
		if (player == null)
			return null;
		RSPlayerDefinition playerD = player.getDefinition();
		if (playerD == null)
			return null;
		RSItem[] equipment = playerD.getEquipment();
		if (equipment.length == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (RSItem e : equipment) {
			RSItemDefinition itemD = e.getDefinition();
			if (itemD != null) {
				String name = itemD.getName();
				if (name != null)
					sb.append(name + ", ");
			}
		}
		return sb.toString()
				.substring(0, sb.length() - 2);
	}

	private String playerIsWearing(RSPlayer p, String... dangerousEquipment) {
		if (p == null)
			return null;
		RSPlayerDefinition def = p.getDefinition();
		if (def == null)
			return null;
		RSItem[] equipment = def.getEquipment();
		if (equipment.length == 0)
			return null;
		for (RSItem e : equipment) {
			RSItemDefinition d = e.getDefinition();
			if (d != null) {
				String name = d.getName();
				if (name != null) {
					String copy = name;
					name = name.toLowerCase();
					for (String s : dangerousEquipment) {
						if (name.contains(s.toLowerCase()))
							return copy;
					}
				}
			}
		}
		return null;
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
		}
		catch (IOException e) {
			return null;
		}
	}

	@Override
	public void onPaint(Graphics g) {
		long time = System.currentTimeMillis() - startTime;
		int killsPerHour = (int) (kills * 3600000D / time);
		int profit = 0;
		int profitPerHour = 0;
		if (loot != null) {
			profit = loot.getProfit();
			profitPerHour = (int) (profit * 3600000D / time);
		}
		int tripsPerHour = (int) (trips * 3600000D / time);
		int escapesPerHour = (int) (escape * 3600000D / time);
		int deathsPerHour = (int) (death * 3600000D / time);
		int xpGained = (Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.STRENGTH) + Skills.getXP(SKILLS.DEFENCE) + Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.RANGED) + Skills.getXP(SKILLS.MAGIC)) - startXP;
		int xpPerHour = (int) (xpGained * 3600000D / time);
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.drawImage(background, 245, 293, null);
		int x = 329;
		int y = 355;
		int spacing = 17;
		Color color_green = new Color(0, 150, 0, 100);
		g2.setColor(Color.BLACK);
		g2.fillRect(10, 459, 90, 15);
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g2.drawString(version, 488, 328);
		g2.setFont(new Font("Tahoma", Font.BOLD, 11));
		g2.drawString(Timing.msToString(time), x, y);
		y += spacing;
		g2.drawString(status, x, y);
		y += spacing;
		g2.drawString(addCommasToNumericString(Integer.toString(xpGained)) + " (" + addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", x, y);
		y += 16;
		g2.drawString(addCommasToNumericString(Integer.toString(kills)) + " (" + addCommasToNumericString(Integer.toString(killsPerHour)) + "/hr)", x, y);
		y += spacing;
		g2.drawString(addCommasToNumericString(Integer.toString(profit)) + " gp (" + addCommasToNumericString(Integer.toString(profitPerHour)) + "/hr)", x, y);
		y += 16;
		g2.drawString(addCommasToNumericString(Integer.toString(trips)) + " (" + addCommasToNumericString(Integer.toString(tripsPerHour)) + "/hr)", x, y);
		y += spacing;
		g2.drawString(addCommasToNumericString(Integer.toString(escape)) + " (" + addCommasToNumericString(Integer.toString(escapesPerHour)) + "/hr)", x, y);
		y += spacing;
		g2.drawString(addCommasToNumericString(Integer.toString(death)) + " (" + addCommasToNumericString(Integer.toString(deathsPerHour)) + "/hr)", x, y);
		if (GameTab.getOpen() == GameTab.TABS.INVENTORY) {
			g.setFont(new Font("Tahoma", Font.PLAIN, 10));
			Rectangle bounds = Bag.getBounds();
			if (bounds != null) {
				if (Bag.isFull()) {
					g.setColor(Color.GREEN);
					g.drawString("Full", bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 2));
				}
				else if (Bag.hasLoot()) {
					g.setColor(Color.YELLOW);
					g.drawString("In Use", bounds.x + (bounds.width / 10), bounds.y + (bounds.height / 2));
				}
				else if (Bag.isEmpty()) {
					g.setColor(Color.RED);
					g.drawString("Empty", bounds.x + (bounds.width / 10), bounds.y + (bounds.height / 2));
				}
				g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 5, 5);
			}
		}
		if (loot != null && loot.getLootItems().length > 0) {
			g2.setFont(new Font("Tahoma", Font.PLAIN, 11));
			g2.setColor(color_green);
			x = 10;
			y = 42;
			spacing = 14;
			int height = 25;
			for (LootItem i : loot.getLootItems()) {
				if (i.getCount() > 0 && ((i.getValue() * i.getCount()) >= minLootValue))
					height += 14;
			}
			g2.fillRoundRect(x - 5, y - 15, 210, height, 16, 16);
			g2.setColor(Color.WHITE);
			g2.drawRoundRect(x - 5, y - 15, 210, height, 16, 16);
			g2.drawString(addCommasToNumericString(Integer.toString(minLootValue)) + "+ gp Loot Table", x + 40, y);
			y += 18;
			g2.drawLine(6, 47, 214, 47);
			for (LootItem i : loot.getLootItems()) {
				if (i.getCount() > 0 && ((i.getValue() * i.getCount()) >= minLootValue)) {
					if (i.getName()
							.equals("Looting bag")) {
						g2.drawString(addCommasToNumericString(Integer.toString(i.getCount())) + " " + i.getName(), x, y);
						y += spacing;
					}
					else {
						g2.drawString(addCommasToNumericString(Integer.toString(i.getCount())) + " " + i.getName() + " (" + addCommasToNumericString(Integer.toString(i.getValue())) + " gp)", x, y);
						y += spacing;
					}
				}
			}
			y += 10;
		}
		if (Npc.current != null) {
			g2.setColor(new Color(0, 255, 0, 50));
			Positionable entity = Npc.current;
			if (entity != null) {
				RSModel model = null;
				if (entity instanceof RSNPC) {
					model = ((RSAnimableEntity) entity).getModel();
				}
				else if (entity instanceof RSObject) {
					model = ((RSObject) entity).getModel();
				}
				if (model != null && model.isClickable()) {
					Polygon area = model.getEnclosedArea();
					if (area != null)
						g2.fillPolygon(area);
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
	}

	public void playerMessageReceived(String username, String message) {
		if (Wilderness.getLevel() > 0 && useAutoResponder)
			responder.generateResponse(username, message);
	}

	@Override
	public void serverMessageReceived(String e) {
		if (e.contains("The bag's too full.")) {
			Bag.setFull();
		}
		else if (e.contains("Oh dear, you are dead!")) {
			resetTeleportBlock();
			if (!deathWalk) {
				run = false;
				println("Oops, we died. Shutting down.");
			}
			else {
				println(e);
				death++;
				evade = true;
				died = true;
				Bag.setEmpty();
			}
		}
		else if (e.contains("A magical force stops you from moving.")) {
			if (!evade) {
				evade = true;
				println("We're under attack, \"" + e + "\"");
			}
		}
		else if (e.contains("You have been poisoned")) {
			if (!evade) {
				evade = true;
				println("We're under attack, \"" + e + "\"");
			}
		}
		else if (e.contains("A teleport block has been cast on you!")) {
			teleportBlockTime = System.currentTimeMillis();
			evade = true;
			println(e);
		}
	}

	public void clanMessageReceived(String arg0, String arg1) {
	}

	public void duelRequestReceived(String arg0, String arg1) {
	}

	public void personalMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void tradeRequestReceived(String arg0) {
	}

	@Override
	public void onEnd() {
		if (search_started)
			search.setStop(true);
		println("------------------------------");
		long time = System.currentTimeMillis() - startTime;
		int xpGained = (Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.STRENGTH) + Skills.getXP(SKILLS.DEFENCE) + Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.RANGED) + Skills.getXP(SKILLS.MAGIC)) - startXP;
		println("Total time: " + Timing.msToString(time));
		println("We collected a total of...");
		println(addCommasToNumericString(Integer.toString(kills)) + " Dragon bones");
		int profitPerHour = (int) (loot.getProfit() * 3600000D / time);
		println("Our total profit was: " + addCommasToNumericString(Integer.toString(loot.getProfit())) + " gp (" + addCommasToNumericString(Integer.toString(profitPerHour)) + "/hr)");
		println("We gained " + xpGained + " xp.");
		println("Thank you for using USA Dragon Fighter " + version);
		if (loot.getProfit() > 0 && time > 60000) {
			if (xpGained < 0)
				xpGained = 0;
			try {
				if (sendServerData(URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"), time, loot.getProfit(), profitPerHour, xpGained, trips, escape, death, version)) {
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
		else {
			println("You had no profit or your time was less than 1 minute, not recording this session!");
		}
		println("------------------------------");
	}

	private boolean sendServerData(String username, long duration, int profit, int profit_per_hour, int xp, int trips, int escapes, int deaths, String version) {
		try {
			URL url = new URL("http://usa-tribot.org/dragon/data.php?username=" + username +
					"&duration=" +
					duration +
					"&profit=" +
					profit +
					"&profit_per_hour=" +
					profit_per_hour +
					"&xp=" +
					xp +
					"&trips=" +
					trips +
					"&escapes=" +
					escapes +
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

	public static void loadGUI() {
		String text = gui.startingEquipmentText.getText();
		Pattern pat = Pattern.compile("[A-Z].*");
		Matcher match = pat.matcher(text);
		ArrayList<String> matches = new ArrayList<String>();
		while (match.find()) {
			matches.add(match.group());
		}
		startingEquipment = new String[matches.size()];
		startingEquipment = matches.toArray(startingEquipment);
		text = "Starting Equipment: ";
		for (String str : startingEquipment) {
			if (text.length() <= 25) {
				text = text + str;
			}
			else {
				text = text + ", " + str;
			}
		}
		General.println(text);
		text = gui.dangerousEquipmentText.getText();
		pat = Pattern.compile("[A-Z].*");
		match = pat.matcher(text);
		matches = new ArrayList<String>();
		while (match.find()) {
			matches.add(match.group());
		}
		dangerousEquipment = new String[matches.size()];
		dangerousEquipment = matches.toArray(dangerousEquipment);
		text = "Dangerous Equipment: ";
		for (String str : dangerousEquipment) {
			if (text.length() <= 25) {
				text = text + str;
			}
			else {
				text = text + ", " + str;
			}
		}
		General.println(text);
		dragon = (DRAGON) gui.locationComboBox.getSelectedItem();
		General.println("Dragon Location set to " + dragon);
		minLootValue = (int) gui.lootMinSpinner.getValue();
		maxLootValue = (int) gui.lootMaxSpinner.getValue();
		General.println("Looting any item over " + minLootValue + " gp and below " + maxLootValue + " gp");
		foodQuantity = (int) gui.foodQuantitySpinner.getValue();
		foodName = gui.foodNameTextField.getText();
		General.println("Using " + foodQuantity + " " + foodName + " per trip.");
		maxPlayers = (int) gui.maxPlayersSpinner.getValue();
		if (maxPlayers > 0)
			General.println("We will change worlds if there are more than " + maxPlayers + " players detected.");
		useQuickPrayer = gui.quickPrayerCheckBox.isSelected();
		if (useQuickPrayer)
			General.println("Using Quick Prayer when evading.");
		useAutoResponder = gui.autoResponderCheckBox.isSelected();
		if (useAutoResponder)
			General.println("Using Auto Responder V2.");
		deathWalk = gui.deathWalkCheckBox.isSelected();
		if (deathWalk)
			General.println("We are Death Walking.");
		useABC2 = gui.useABC2Box.isSelected();
		ABC.setSleepReaction(useABC2);
		useCombatPotions = gui.useCombatPotionsCheckBox.isSelected();
		if (useCombatPotions)
			General.println("Use Combat Potions.");
		useSuperPotions = gui.useSuperPotionsCheckBox.isSelected();
		if (useSuperPotions)
			General.println("Using Super Potions.");
		pickupClueScrolls = gui.clueScrollsCheckBox.isSelected();
		if (pickupClueScrolls)
			General.println("Picking up Clue Scrolls.");
		bagOption = (LOOTING_BAG_OPTION) gui.lootingBagOptionBox.getSelectedItem();
		General.println("We will deposit items into the looting bag when " + bagOption.toString());
		useSpecialAttack = gui.specialAttackCheckBox.isSelected();
		specialAttackEnergy = (int) gui.specialAttackSpinner.getValue();
		if (useSpecialAttack)
			General.println("Using Special Attack at " + specialAttackEnergy + "% energy.");
		gui_is_up = false;
		gui.dispose();
	}

	@Override
	public void onBreakEnd() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBreakStart(long arg0) {
		long timer = System.currentTimeMillis() + 30000;
		while (timer > System.currentTimeMillis() && Wilderness.getLevel() != 0) {
			println("Teleporting to Edgeville to begin breaking.");
			Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY);
		}
	}
}
