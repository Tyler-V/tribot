package scripts.thugs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Breaking;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.api.v1.api.banking.Bank;
import scripts.api.v1.api.entity.UsaObjects;
import scripts.api.v1.api.equipment.Equip;
import scripts.api.v1.api.generic.Conditional;
import scripts.api.v1.api.items.Consumables;
import scripts.api.v1.api.walking.Walk;
import scripts.api.v1.api.wilderness.Wilderness;
import scripts.api.v1.api.worlds.TYPE;
import scripts.api.v1.api.worlds.WorldHopper;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.responder.AutoResponder;
import scripts.usa.api.combat.CombatTrainer;
import scripts.usa.api.combat.CombatTrainer.TRAINING_MODE;
import scripts.usa.api.combat.GenericCombat;
import scripts.usa.api.items.LootItem;
import scripts.usa.api.items.Looting;
import scripts.usa.api.items.LootingBag;
import scripts.usa.api.teleporting.Teleport;
import scripts.usa.api.ui.Paint;
import scripts.usa.api.web.pricing.Pricing;
import scripts.usa.data.Constants;
import scripts.usa.data.Gear;
import scripts.usa.data.Location;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Thugs")
public class Thugs extends Script implements Painting, MessageListening07, Ending, Breaking {

	public final static String version = "v2.6";
	private String status = "Starting...";

	// Options
	private static String FOOD_NAME = "Lobster";
	private static int FOOD_AMOUNT = 20;
	private static int POTION_AMOUNT = 8;
	private static int MINIMUM_LOOT_VALUE = 1000;
	private static int MAX_LOOT_VALUE = Integer.MAX_VALUE;
	private static boolean loot;
	private static boolean lootingBag;
	private static boolean autoResponder;
	private static boolean evadePlayers;
	private static TRAINING_MODE mode = TRAINING_MODE.TRAIN_COMBAT;
	private static int attack;
	private static int strength;
	private static int defence;

	// Classes
	private Looting looting;
	private LootingBag bag;
	private CombatTrainer trainer;
	private AutoResponder responder;
	private ThreatSearch search;

	// Data
	public static String[] DANGEROUS_EQUIPMENT = { "Zamorak", "Ghostly", "Wizard", "Mystic", "Ahrim", "Mages' book",
			"Occult necklace", "Zamorak", "Guthix", "Saradomin", "Ancient", "Toxic" };

	private final String[] DROP_ITEMS = { "Vial", "Bones", "Iron ore", "Iron bar" };

	private final RSArea EDGEVILLE_DUNGEON_ENTRANCE = new RSArea(
			new RSTile[] { new RSTile(3098, 3468, 0), new RSTile(3092, 3468, 0), new RSTile(3092, 3473, 0),
					new RSTile(3090, 3474, 0), new RSTile(3090, 3482, 0), new RSTile(3097, 3482, 0) });
	private final RSArea EDGEVILLE_DUNGEON_SOUTH_WEST = new RSArea(new RSTile[] { new RSTile(3093, 9915, 0),
			new RSTile(3104, 9915, 0), new RSTile(3104, 9904, 0), new RSTile(3098, 9904, 0), new RSTile(3098, 9886, 0),
			new RSTile(3100, 9879, 0), new RSTile(3099, 9866, 0), new RSTile(3095, 9866, 0), new RSTile(3095, 9879, 0),
			new RSTile(3092, 9879, 0) });
	private final RSArea EDGEVILLE_DUNGEON_NORTH_EAST = new RSArea(new RSTile[] { new RSTile(3127, 9918, 0),
			new RSTile(3135, 9918, 0), new RSTile(3144, 9918, 0), new RSTile(3151, 9916, 0), new RSTile(3151, 9909, 0),
			new RSTile(3151, 9901, 0), new RSTile(3153, 9893, 0), new RSTile(3153, 9878, 0), new RSTile(3136, 9882, 0),
			new RSTile(3137, 9901, 0), new RSTile(3125, 9900, 0), new RSTile(3126, 9906, 0), new RSTile(3104, 9906, 0),
			new RSTile(3104, 9912, 0), new RSTile(3127, 9912, 0) });
	private final RSArea THUG_AREA = new RSArea(new RSTile[] { new RSTile(3129, 9918, 0), new RSTile(3129, 9922, 0),
			new RSTile(3125, 9926, 0), new RSTile(3125, 9934, 0), new RSTile(3130, 9937, 0), new RSTile(3135, 9937, 0),
			new RSTile(3135, 9925, 0), new RSTile(3134, 9918, 0) });

	private final RSTile RESET_TILE = new RSTile(3150, 9891, 0);
	private final RSTile TRAPDOOR_TILE = new RSTile(3094, 3470, 0);
	private final RSTile EDGEVILLE_DUNGEON_GATE_WEST = new RSTile(3103, 9909, 0);
	private final RSTile EDGEVILLE_DUNGEON_GATE_EAST = new RSTile(3104, 9909, 0);
	private final RSTile WILDERNESS_GATE_SOUTH = new RSTile(3132, 9917, 0);
	private final RSTile WILDERNESS_GATE_NORTH = new RSTile(3132, 9918, 0);

	// Variables
	private boolean reset;
	private ThugLocation THUG_LOCATION = ThugLocation.values()[General.random(0, ThugLocation.values().length - 1)];
	private int fail, startXP, startLevels, world_change_attempt;
	private long startTime;
	private List<String> DONT_DEPOSIT = new ArrayList<String>();
	private long next_location_change = 0L;
	private long teleport_block_timer = 0L;

	private static boolean evade = false;
	private boolean run = true;

	private static GUI gui;
	private static boolean gui_is_up = true;

	@Override
	public void run() {

		while (Login.getLoginState() != Login.STATE.INGAME) {
			status = "Setting up...";
			sleep(100);
		}

		gui = new GUI();
		gui.setVisible(true);

		while (gui_is_up) {
			status = "GUI...";
			sleep(100);
		}

		trainer = new CombatTrainer(mode);
		trainer.setTrainer(SKILLS.ATTACK, attack);
		trainer.setTrainer(SKILLS.STRENGTH, strength);
		trainer.setTrainer(SKILLS.DEFENCE, defence);

		if (autoResponder) {
			super.setAutoResponderState(false);
			responder = new AutoResponder();
		}

		if (evadePlayers) {
			System.out.println("Started Threat Searching thread.");
			search = new ThreatSearch();
			Thread searching = new Thread(search);
			searching.start();
		}

		startTime = System.currentTimeMillis();
		startXP = Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.STRENGTH) + Skills.getXP(SKILLS.DEFENCE)
				+ Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.RANGED) + Skills.getXP(SKILLS.MAGIC);
		startLevels = Skills.getActualLevel(SKILLS.ATTACK) + Skills.getActualLevel(SKILLS.STRENGTH)
				+ Skills.getActualLevel(SKILLS.DEFENCE) + Skills.getActualLevel(SKILLS.HITPOINTS)
				+ Skills.getActualLevel(SKILLS.RANGED) + Skills.getActualLevel(SKILLS.MAGIC);

		DONT_DEPOSIT.addAll(Arrays.asList(FOOD_NAME, Constants.AMULET_OF_GLORY[0], Constants.AMULET_OF_GLORY[1],
				Constants.AMULET_OF_GLORY[2], Constants.AMULET_OF_GLORY[3], Constants.AMULET_OF_GLORY[4],
				Constants.AMULET_OF_GLORY[5], Constants.COMBAT_POTION[0], Constants.COMBAT_POTION[1],
				Constants.COMBAT_POTION[2], Constants.COMBAT_POTION[3]));

		if (loot) {
			looting = new Looting(MINIMUM_LOOT_VALUE, MAX_LOOT_VALUE);
			if (lootingBag) {
				bag = new LootingBag();
				looting.put(new LootItem("Looting bag", 11941, 0, 0, true));
				DONT_DEPOSIT.add("Looting bag");
			}
			looting.put(new LootItem("Looting bag", 0, 0, 0, true));
			looting.put(new LootItem("Death rune", 560, Pricing.getPrice("Death rune", 560), 0, true));
			looting.put(new LootItem("Nature rune", 561, Pricing.getPrice("Nature rune", 561), 0, true));
			looting.put(new LootItem("Chaos rune", 562, Pricing.getPrice("Chaos rune", 562), 0, true));
			looting.put(new LootItem("Law rune", 563, Pricing.getPrice("Law rune", 563), 0, true));
			looting.put(new LootItem("Cosmic rune", 564, Pricing.getPrice("Cosmic rune", 564), 0, true));
			looting.put(new LootItem("Coins", 995, 1, 0, false));
			looting.setArea(THUG_AREA);
			for (RSItem item : Inventory.getAll()) {
				RSItemDefinition d = item.getDefinition();
				if (d != null) {
					String name = d.getName();
					int id = item.getID();
					if (name != null) {
						if (!DONT_DEPOSIT.contains(name)) {
							if (!looting.getMap().containsKey(name))
								looting.put(new LootItem(name, id, Pricing.getPrice(name, id), 0, false));
						}
					}
				}
			}
		}

		for (ThugLocation t : ThugLocation.values()) {
			if (t.getArea().contains(Player.getPosition())) {
				THUG_LOCATION = t;
				break;
			}
		}

		Camera.setCameraAngle(100);

		while (run) {
			if (!evade()) {
				ABC.activateRun();
				if (!WorldHopper.onMembersWorld())
					changeWorlds();
				if (needToBank()) {
					if (!Location.Area.EDGEVILLE.getArea().contains(Player.getPosition())) {
						status = "Traveling to Edgeville";
						if (!Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY)) {
							Walk.webWalk(Location.Bank.EDGEVILLE.getTile(), new Condition() {
								public boolean active() {
									return Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY);
								}
							}, 1000);
						}
					} else {
						if (!bank()) {
							fail++;
							if (fail >= 3) {
								println("Failed to bank (3) times, shutting down!");
								run = false;
							}
						} else {
							fail = 0;
						}
					}
				} else {
					if (THUG_AREA.contains(Player.getPosition())) {
						attackThugs();
					} else {
						travelToThugs();
					}
				}
				if (ABC.performAntiban())
					status = "ABC2 Antiban";
			}
			sleep(General.randomSD(200, 100));
		}
	}

	public static void loadGUI() {

		FOOD_NAME = GUI.foodText.getText();
		FOOD_AMOUNT = (int) GUI.foodSpinner.getValue();
		POTION_AMOUNT = (int) GUI.potionSpinner.getValue();
		loot = GUI.lootBox.isSelected();
		MINIMUM_LOOT_VALUE = (int) GUI.lootSpinner.getValue();
		lootingBag = GUI.lootingBagBox.isSelected();
		autoResponder = GUI.autoResponderBox.isSelected();
		evadePlayers = GUI.evadeBox.isSelected();

		mode = (TRAINING_MODE) GUI.modeBox.getSelectedItem();
		attack = (int) GUI.attackSpinner.getValue();
		strength = (int) GUI.strengthSpinner.getValue();
		defence = (int) GUI.defenceSpinner.getValue();

		gui_is_up = false;
		gui.dispose();

	}

	public static void setEvade(boolean option) {
		evade = option;
	}

	public static boolean isEvade() {
		return evade;
	}

	private boolean switchLocations() {
		if (next_location_change > System.currentTimeMillis())
			return false;
		if (Players.getAll(Filters.Players.inArea(THUG_LOCATION.getArea())).length > 1) {
			for (ThugLocation location : ThugLocation.values()) {
				if (location != THUG_LOCATION) {
					if (Players.getAll(Filters.Players.inArea(location.getArea())).length == 0) {
						next_location_change = System.currentTimeMillis() + General.random(150000, 600000);
						THUG_LOCATION = location;
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean changeWorlds() {

		int previousWorld = WorldHopper.getCurrentWorld();
		int newWorld = WorldHopper.getRandomWorld(true);
		if (newWorld == previousWorld)
			return false;

		status = "Changing to world " + newWorld + ".";
		if (WorldHopper.changeWorld(newWorld)) {
			if (previousWorld != WorldHopper.getCurrentWorld()) {
				status = "Changed from " + previousWorld + " to " + newWorld + ".";
				world_change_attempt = 0;
				sleep(General.randomSD(4000, 500));
				return true;
			}
		}

		if (!Location.Area.EDGEVILLE.getArea().contains(Player.getPosition())) {
			world_change_attempt++;
			if (world_change_attempt > 3) {
				world_change_attempt = 0;
				return true;
			}
		}
		return false;
	}

	private boolean bank() {
		if (Bank.open()) {
			if (Bank.depositAllExcept(DONT_DEPOSIT))
				status = "Depositing Items";

			if (bag != null && bag.depositItemsInBank())
				status = "Depositing Bag Items";

			if (!Equip.isEquipped(Constants.AMULET_OF_GLORY)) {
				if (Inventory.getCount(Constants.AMULET_OF_GLORY) == 0) {
					if (Inventory.isFull()) {
						status = "Making Room";
						Banking.deposit(1, FOOD_NAME);
					}
					status = "Withdrawing Glory";
					if (!Bank.withdraw(1, Constants.AMULET_OF_GLORY))
						return false;
				}

				if (Inventory.getCount(Constants.AMULET_OF_GLORY) > 0) {
					status = "Closing Bank";
					if (Bank.close()) {
						status = "Equipping Glory";
						Equip.equipItem(Constants.AMULET_OF_GLORY);
						return true;
					}
				}
			}

			if (upgradeGear())
				status = "Upgraded Gear";

			status = "Withdrawing Items";

			if (lootingBag && Inventory.getCount("Looting bag") == 0) {
				if (Bank.withdraw(1, "Looting bag"))
					status = "Withdrew Looting bag";
			}

			if (!Bank.withdraw(FOOD_AMOUNT, FOOD_NAME)) {
				return false;
			} else {
				status = "Withdrew " + FOOD_NAME;
			}

			if (!Bank.withdraw(POTION_AMOUNT, Constants.COMBAT_POTION)) {
				return false;
			} else {
				status = "Withdrew Combat potion";
			}
		}
		return true;
	}

	private boolean upgradeGear() {
		SLOTS[] slots = new SLOTS[] { SLOTS.WEAPON, SLOTS.HELMET, SLOTS.BODY, SLOTS.LEGS, SLOTS.SHIELD, SLOTS.BOOTS };
		boolean useShield = true;
		List<String> items = new ArrayList<String>();
		for (SLOTS slot : slots) {
			Gear weapon = Gear.getWorn(SLOTS.WEAPON);
			if ((slot == SLOTS.SHIELD) && !useShield || (weapon != null && weapon.isTwoHanded()))
				continue;
			if (!Gear.isWearingBestAvailable(slot)) {
				Gear item = Gear.getBestGearInBank(slot);
				if (item != null && !Equipment.isEquipped(item.getName())) {
					if (item.isTwoHanded()) {
						println(item.getName() + " is two handed, disabling shield.");
						useShield = false;
					}
					status = "Upgrading Gear";
					if (Bank.withdraw(1, item.getName()))
						items.add(item.getName());
				}
			}
		}

		if (!items.isEmpty()) {
			trainer.setSkillBeingTrained(null);
			if (Bank.close()) {
				status = "Equipping Items";
				if (Equip.equipItems(items))
					return true;
			}
		}
		return false;
	}

	private boolean needToBank() {
		if (Inventory.getCount(FOOD_NAME) == 0) {
			if (Inventory.isFull()) {
				if (bag != null && !bag.isFull())
					return false;
				return true;
			}
			if (THUG_AREA.contains(Player.getPosition()) && !ABC.shouldEat())
				return false;
			return true;
		}

		if (Inventory.getCount("Amulet of glory") > 0)
			return true;

		if (Location.Area.EDGEVILLE.getArea().contains(Player.getPosition())
				&& (Inventory.getCount(FOOD_NAME) < (FOOD_AMOUNT - 1)
						|| Inventory.getCount(Constants.COMBAT_POTION) < (POTION_AMOUNT - 1)
						|| !Equip.isEquipped(Constants.AMULET_OF_GLORY)
						|| Inventory.getCount(Constants.AMULET_OF_GLORY) > 0))
			return true;
		return false;
	}

	private boolean evade() {
		if (!evade)
			return false;
		if (Location.Area.LUMBRIDGE.getArea().contains(Player.getPosition())) {
			if (changeWorlds())
				evade = false;
		}

		if (Location.Area.EDGEVILLE.getArea().contains(Player.getPosition())) {
			status = "Teleported to Edgeville";
			if (changeWorlds())
				evade = false;
		} else {
			if (System.currentTimeMillis() > teleport_block_timer) {
				if (Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY)) {
					status = "Teleported to Edgeville";
					println("Teleported safely to Edgeville!");
				}
			} else {
				if (Consumables.eat(FOOD_NAME, false)) {
					status = "Eating " + FOOD_NAME;
				}
				if (Player.getPosition().distanceTo(WILDERNESS_GATE_NORTH) > 2) {
					status = "Walking Outside Wilderness";
					Walk.walkToTile(WILDERNESS_GATE_NORTH, 1, 1, null);
				} else {
					if (changeWorlds()) {
						println("Changed worlds outside Wilderness safely!");
						evade = false;
					}
				}
			}
		}
		return true;
	}

	private boolean attackThugs() {
		if (reset) {

			status = "Reset Aggressiveness";
			if (GenericCombat.checkForCombat(General.random(2000, 4000), new Condition() {
				public boolean active() {
					return !THUG_LOCATION.getArea().contains(Player.getPosition());
				}
			}))
				reset = false;

			if (changeWorlds()) {
				while (reset && !evade) {
					if (!WILDERNESS_GATE_NORTH.isOnScreen()) {
						status = "Walking to Thug Gate";
						Walk.walkToTile(WILDERNESS_GATE_NORTH, 2, 3, new Condition() {
							public boolean active() {
								return Consumables.eat(FOOD_NAME, false);
							}
						}, new Condition() {
							public boolean active() {
								return WILDERNESS_GATE_NORTH.isOnScreen();
							}
						});
					} else if (!PathFinding.canReach(WILDERNESS_GATE_SOUTH, false)) {
						status = "Exiting Gate";
						if (UsaObjects.click("Open", Objects.findNearest(30, "Gate"))) {
							Conditional.sleep(new Condition() {
								public boolean active() {
									return Player.isMoving() || Player.getAnimation() != -1;
								}
							}, new Condition() {
								public boolean active() {
									return PathFinding.canReach(WILDERNESS_GATE_SOUTH, false);
								}
							}, 3000);
						}
					} else {
						if (!RESET_TILE.isOnScreen()) {
							status = "Walking to Reset Tile";
							Walk.walkToTile(RESET_TILE, 2, 2, new Condition() {
								public boolean active() {
									return Consumables.eat(FOOD_NAME, false);
								}
							}, null);
						}

						if (RESET_TILE.isOnScreen()) {
							status = "At Reset Tile";
							reset = false;
						}
					}
				}
			}
		} else {
			if (!trainer.isTrainingCorrectSkill()) {
				if (trainer.isTrainingComplete()) {
					status = "All stat training is complete!";
					println(status);
					run = false;
				} else {
					SKILLS skill = trainer.getSkillToTrain();
					status = "Setting Style to " + skill.toString();
					if (trainer.setAttackStyle(skill)) {
						trainer.setSkillBeingTrained(skill);
						status = "Style set to " + skill.toString();
					}
				}
				return true;
			}

			if (switchLocations()) {
				status = "Switching Thug Locations";
				return true;
			}

			if (Consumables.eat(FOOD_NAME, false)) {
				status = "Eating " + FOOD_NAME;
				return true;
			}

			if (Consumables.drink(Constants.COMBAT_POTION, SKILLS.ATTACK, false)) {
				status = "Drinking Combat potion";
				return true;
			}

			if (Inventory.drop(DROP_ITEMS) > 0) {
				status = "Dropped Items";
				sleep(General.randomSD(1000, 200));
				return true;
			}

			if (bag != null && bag.depositItems(false, looting.getLootNames())) {
				status = "Deposited Items into Bag";
				return true;
			}

			if (loot) {
				if (looting.isInventorySpaceRequired(looting.findLoot())) {
					if (Consumables.eat(FOOD_NAME, true))
						status = "Making room for item";
				}
				try {
					if (looting.loot()) {
						status = "Looted Item";
						return true;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (THUG_LOCATION.getArea().contains(Player.getPosition())) {
				if (GenericCombat.checkForCombat(General.random(10000, 15000), new Condition() {
					public boolean active() {
						return !THUG_LOCATION.getArea().contains(Player.getPosition()) || evade
								|| Consumables.eat(FOOD_NAME, false);
					}
				})) {
					status = "Remaining Idle at Thugs";
					if (ABC.performAntiban())
						status = "ABC2 Antiban";
				} else {
					reset = true;
				}
			} else {
				status = "Walking to " + THUG_LOCATION.toString() + " Thugs";
				Walk.walkToTile(THUG_LOCATION.getRandomTile(), 0, 0, new Condition() {
					public boolean active() {
						return Consumables.eat(FOOD_NAME, false);
					}
				}, new Condition() {
					public boolean active() {
						return THUG_LOCATION.getArea().contains(Player.getPosition());
					}
				});
			}
			sleep(General.randomSD(500, 100));
		}
		return true;
	}

	private void travelToThugs() {
		if (EDGEVILLE_DUNGEON_NORTH_EAST.contains(Player.getPosition())) {
			if (!WILDERNESS_GATE_SOUTH.isOnScreen()) {
				status = "Walking to Thug Gate";
				Walk.walkToTile(WILDERNESS_GATE_SOUTH, 2, 3, new Condition() {
					public boolean active() {
						return Consumables.eat(FOOD_NAME, false);
					}
				}, new Condition() {
					public boolean active() {
						return WILDERNESS_GATE_SOUTH.isOnScreen();
					}
				});
			} else if (!PathFinding.canReach(WILDERNESS_GATE_NORTH, false)) {
				if (!Wilderness.enter()) {
					status = "Opening Gate";
					if (UsaObjects.click("Open", Objects.findNearest(30, "Gate"))) {
						Conditional.sleep(new Condition() {
							public boolean active() {
								return Player.isMoving() || Player.getAnimation() != -1;
							}
						}, new Condition() {
							public boolean active() {
								return Wilderness.isWarningUp() || !PathFinding.canReach(WILDERNESS_GATE_SOUTH, false);
							}
						}, 3000);
					}
				}
			}
		} else if (EDGEVILLE_DUNGEON_SOUTH_WEST.contains(Player.getPosition())) {
			if (!EDGEVILLE_DUNGEON_GATE_WEST.isOnScreen()) {
				status = "Walking to Gate";
				Walk.walkToTile(EDGEVILLE_DUNGEON_GATE_WEST, 2, 3, new Condition() {
					public boolean active() {
						return Consumables.eat(FOOD_NAME, false);
					}
				}, new Condition() {
					public boolean active() {
						return EDGEVILLE_DUNGEON_GATE_WEST.isOnScreen();
					}
				});
			} else {
				if (!PathFinding.canReach(EDGEVILLE_DUNGEON_GATE_EAST, false)) {
					status = "Opening Gate";
					if (UsaObjects.click("Open", Objects.findNearest(30, "Gate"))) {
						Conditional.sleep(new Condition() {
							public boolean active() {
								return Player.isMoving() || Player.getAnimation() != -1;
							}
						}, new Condition() {
							public boolean active() {
								return PathFinding.canReach(EDGEVILLE_DUNGEON_GATE_EAST, false);
							}
						}, 3000);
					}
				} else {
					status = "Walking to Thug Gate";
					Walk.walkToTile(WILDERNESS_GATE_SOUTH, 2, 3, new Condition() {
						public boolean active() {
							return Consumables.eat(FOOD_NAME, false);
						}
					}, new Condition() {
						public boolean active() {
							return WILDERNESS_GATE_SOUTH.isOnScreen();
						}
					});
				}
			}
		} else if (EDGEVILLE_DUNGEON_ENTRANCE.contains(Player.getPosition())) {
			status = "Climbing down Trap Door";
			if (Camera.getCameraAngle() < 100)
				Camera.setCameraAngle(100);
			if (!Player.isMoving()) {
				final RSTile tile = Player.getPosition();
				if (UsaObjects.click(null, Objects.findNearest(30, "Trapdoor"))) {
					Conditional.sleep(new Condition() {
						public boolean active() {
							return Player.isMoving() || Player.getAnimation() != -1;
						}
					}, new Condition() {
						public boolean active() {
							return !PathFinding.canReach(tile, false);
						}
					}, 3000);
				}
			}
		} else if (Location.Area.EDGEVILLE.getArea().contains(Player.getPosition())) {
			status = "Walking to Trap Door";
			Walk.walkToTile(TRAPDOOR_TILE, 1, 2, new Condition() {
				public boolean active() {
					RSObject object = UsaObjects.getObject("Trapdoor");
					return object != null && object.isOnScreen() && object.isClickable();
				}
			});
		} else {
			status = "Traveling to Edgeville";
			if (!Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY)) {
				Walk.webWalk(Location.Bank.EDGEVILLE.getTile(), new Condition() {
					public boolean active() {
						return Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY);
					}
				}, 1000);
			}
		}
	}

	@Override
	public void onPaint(Graphics g) {
		if (THUG_LOCATION != null) {
			Graphics2D g2 = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHints(rh);

			long currentTime = System.currentTimeMillis();
			long time = currentTime - startTime;
			int currentXP = Skills.getXP(SKILLS.ATTACK) + Skills.getXP(SKILLS.STRENGTH) + Skills.getXP(SKILLS.DEFENCE)
					+ Skills.getXP(SKILLS.HITPOINTS) + Skills.getXP(SKILLS.RANGED) + Skills.getXP(SKILLS.MAGIC);
			int gainedXP = currentXP - startXP;
			int xpPerHour = (int) (gainedXP * 3600000D / time);
			int currentLevels = Skills.getActualLevel(SKILLS.ATTACK) + Skills.getActualLevel(SKILLS.STRENGTH)
					+ Skills.getActualLevel(SKILLS.DEFENCE) + Skills.getActualLevel(SKILLS.HITPOINTS)
					+ Skills.getActualLevel(SKILLS.RANGED) + Skills.getActualLevel(SKILLS.MAGIC);
			int gainedLevels = currentLevels - startLevels;
			int profit = 0;
			int profitPerHour = 0;

			if (looting != null) {

				profit = looting.getProfit();
				profitPerHour = (int) (profit * 3600000D / time);

			}

			int border_x, border_y, text_x, text_y, rectangle_height, rectangle_width, row_spacing;

			border_x = 280;
			border_y = 361;

			text_x = border_x + 5;
			text_y = border_y - 2;

			rectangle_height = 14;
			rectangle_width = 214;

			row_spacing = 18;

			Color background = new Color(0, 0, 255, 175);
			Color border = new Color(0, 0, 150, 150);

			g2.setFont(new Font("Tahoma", Font.PLAIN, 12));

			g2.setColor(background);
			g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(border);
			g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(Color.WHITE);
			g2.drawString("                 USA Thugs             " + version, text_x, text_y);

			border_y += row_spacing;
			text_y += row_spacing;

			g2.setColor(background);
			g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(border);
			g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(Color.WHITE);
			g2.drawString("Time: " + Timing.msToString(time), text_x, text_y);

			border_y += row_spacing;
			text_y += row_spacing;

			g2.setColor(background);
			g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(border);
			g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(Color.WHITE);
			g2.drawString("Status: " + status, text_x, text_y);

			border_y += row_spacing;
			text_y += row_spacing;

			if (loot) {
				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("Profit: " + TabletsPaint.formatInteger(profit) + " ("
						+ TabletsPaint.formatInteger(profitPerHour) + "/hr)", text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;
			}

			if (Player.getRSPlayer() != null) {
				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("Levels Gained: " + TabletsPaint.formatInteger(gainedLevels) + " | Combat: "
						+ Player.getRSPlayer().getCombatLevel(), text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;
			}

			g2.setColor(background);
			g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(border);
			g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(Color.WHITE);
			g2.drawString(
					"XP: " + TabletsPaint.formatInteger(gainedXP) + " (" + TabletsPaint.formatInteger(xpPerHour) + "/hr)",
					text_x, text_y);

			border_y += row_spacing;
			text_y += row_spacing;

			long switch_time = next_location_change - System.currentTimeMillis();

			if (switch_time < 0)
				switch_time = 0;

			g2.setColor(background);
			g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(border);
			g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(Color.WHITE);
			g2.drawString("Next Switch: " + Timing.msToString(switch_time), text_x, text_y);

			for (RSTile tile : THUG_LOCATION.getArea().getAllTiles()) {
				g2.setColor(new Color(40, 130, 30, 75));
				if (tile.isOnScreen())
					g2.fillPolygon(Projection.getTileBoundsPoly(tile, 0));
			}

			if (bag != null && GameTab.getOpen() == GameTab.TABS.INVENTORY && bag.getBounds() != null) {
				g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
				if (bag.isFull()) {

					g.setColor(Color.GREEN);
					g.drawString("Full", bag.getBounds().x + (bag.getBounds().width / 4),
							bag.getBounds().y + (bag.getBounds().height / 2));
				} else if (bag.inUse()) {
					g.setColor(Color.YELLOW);
					g.drawString("In Use", bag.getBounds().x + (bag.getBounds().width / 10),
							bag.getBounds().y + (bag.getBounds().height / 2));
				} else if (bag.isEmpty()) {
					g.setColor(Color.RED);
					g.drawString("Empty", bag.getBounds().x + (bag.getBounds().width / 10),
							bag.getBounds().y + (bag.getBounds().height / 2));
				}
				g.drawRoundRect(bag.getBounds().x, bag.getBounds().y, bag.getBounds().width, bag.getBounds().height, 5,
						5);
			}

			if (looting != null && looting.getLootItems().length > 0) {
				g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
				g2.setColor(background);

				int x = 10;
				int y = 42;
				int spacing = 14;

				int height = 25;
				for (LootItem i : looting.getLootItems()) {
					if (i.getCount() > 0)
						height += 14;
				}

				g2.fillRoundRect(x - 5, y - 15, 210, height, 16, 16);

				g2.setColor(Color.WHITE);

				g2.drawRoundRect(x - 5, y - 15, 210, height, 16, 16);

				g2.drawString(TabletsPaint.formatInteger(MINIMUM_LOOT_VALUE) + "+ gp Loot Table", x + 45, y);
				y += 18;
				g2.drawLine(6, 47, 214, 47);

				for (LootItem i : looting.getLootItems()) {
					if (i.getCount() > 0) {
						if (i.getName().equalsIgnoreCase("Looting bag")) {
							g2.drawString(TabletsPaint.formatInteger(i.getCount()) + " " + i.getName(), x, y);
							y += spacing;
						} else {
							g2.drawString(TabletsPaint.formatInteger(i.getCount()) + " " + i.getName() + " ("
									+ TabletsPaint.formatInteger(i.getValue()) + " gp)", x, y);
							y += spacing;
						}
					}
				}
				y += 10;
			}
		}
	}

	@Override
	public void serverMessageReceived(String e) {
		if (e.equals("Oh dear, you are dead!")) {
			println(e);
			if (bag != null) {
				bag.setEmpty(true);
				bag.setBounds(null);
			}
		} else if (e.equals("The bag's too full.")) {
			if (bag != null) {
				println(e);
				bag.setFull(true);
			}
		} else if (e.equals("A teleport block has been cast on you!")) {
			teleport_block_timer = System.currentTimeMillis() + 300000;
			println("We are unable to teleport for the next 5 minutes.");
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
		if (autoResponder)
			responder.generateResponse(username, message);
	}

	@Override
	public void tradeRequestReceived(String message) {
	}

	@Override
	public void onEnd() {
		if (evadePlayers) {
			search.setStop(true);
			System.out.println("Stopping Threat Searching thread.");
		}
	}

	@Override
	public void onBreakEnd() {
	}

	@Override
	public void onBreakStart(long arg0) {
		status = "Traveling to Edgeville";
		while (!Location.Area.EDGEVILLE.getArea().contains(Player.getPosition())) {
			if (!Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY)) {
				Walk.webWalk(Location.Bank.EDGEVILLE.getTile(), new Condition() {
					public boolean active() {
						return Teleport.byItem(Location.Area.EDGEVILLE, Constants.AMULET_OF_GLORY);
					}
				}, 1000);
			}
			sleep(100);
		}
		sleep(General.randomSD(10000, 2000));
	}

}
