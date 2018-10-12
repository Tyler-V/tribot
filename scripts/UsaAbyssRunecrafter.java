package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Magic;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "USA", name = "USA Abyss Runecrafter")
public class UsaAbyssRunecrafter extends Script implements Painting,
		MouseActions {

	private String version = "16.8";
	private final RSTile DITCH_TILE = new RSTile(3104, 3520, 0);

	private final RSTile[] BANK_TO_DITCH = new RSTile[] {
			new RSTile(3094, 3497, 0), new RSTile(3095, 3502, 0),
			new RSTile(3100, 3504, 0), new RSTile(3104, 3507, 0),
			new RSTile(3104, 3512, 0), new RSTile(3104, 3517, 0),
			new RSTile(3104, 3520, 0) };

	private final RSTile[] DITCH_TO_MAGE = new RSTile[] {
			new RSTile(3104, 3523, 0), new RSTile(3104, 3529, 0),
			new RSTile(3104, 3534, 0), new RSTile(3104, 3539, 0),
			new RSTile(3104, 3544, 0), new RSTile(3103, 3549, 0),
			new RSTile(3103, 3554, 0), new RSTile(3106, 3558, 0),
			new RSTile(3110, 3561, 0) };

	private final RSArea BANK_AREA = new RSArea(new RSTile(3091, 3488, 0),
			new RSTile(3098, 3499, 0));
	private final RSArea EDGEVILLE_AREA = new RSArea(new RSTile(3077, 3475, 0),
			new RSTile(3125, 3522, 0));
	private final RSArea ABYSS_RING_AREA = new RSArea(
			new RSTile(3024, 4817, 0), new RSTile(3054, 4846, 0));
	private final RSArea ABYSS_AREA = new RSArea(new RSTile(3000, 4800, 0),
			new RSTile(3075, 4875, 0));
	private final RSArea WILDERNESS = new RSArea(new RSTile(3080, 3523, 0),
			new RSTile(3119, 3571, 0));
	private final RSArea LUMBRIDGE = new RSArea(new RSTile(3203, 3206),
			new RSTile(3225, 3235));
	private final RSArea FALADOR = new RSArea(new RSTile(2965, 3337),
			new RSTile(2799, 3348));
	private final RSArea LUMBRIDGE_BASEMENT = new RSArea(new RSTile(3208, 9615,
			0), new RSTile(3219, 9624, 0));
	private final RSArea WIZARD_TOWER = new RSArea(new RSTile(3103, 3161, 0),
			new RSTile(3105, 3165, 0));

	private final RSTile BANK_TILE = new RSTile(3093, 3491);
	private final RSTile DARK_MAGE_TILE = new RSTile(3039, 4830, 0);

	private final RSTile DEATH_RIFT_TILE = new RSTile(3050, 4837, 0);
	private final RSArea DEATH_ALTAR = new RSArea(new RSTile(2192, 4823, 0),
			new RSTile(2220, 4850, 0));
	private String DEATH_NAME = "Death";
	private static int DEATH_RUNE = 560;

	private final RSTile NATURE_RIFT_TILE = new RSTile(3035, 4842, 0);
	private final RSArea NATURE_ALTAR = new RSArea(new RSTile(2390, 4832, 0),
			new RSTile(2409, 4851, 0));
	private String NATURE_NAME = "Nature";
	private static int NATURE_RUNE = 561;

	private final RSTile LAW_RIFT_TILE = new RSTile(3049, 4839, 0);
	private final RSArea LAW_ALTAR = new RSArea(new RSTile(2443, 4811, 0),
			new RSTile(2484, 4852, 0));
	private String LAW_NAME = "Law";
	private static int LAW_RUNE = 563;

	private final RSTile COSMIC_RIFT_TILE = new RSTile(3028, 4837, 0);
	private final RSArea COSMIC_ALTAR = new RSArea(new RSTile(2119, 4810, 0),
			new RSTile(2165, 4856, 0));
	private String COSMIC_NAME = "Cosmic";
	private static int COSMIC_RUNE = 564;

	private final RSTile CHAOS_RIFT_TILE = new RSTile(3044, 4842, 0);
	private final RSArea CHAOS_ALTAR = new RSArea(new RSTile(2266, 4837, 0),
			new RSTile(2276, 4847, 0));
	private String CHAOS_NAME = "Chaos";
	private static int CHAOS_RUNE = 562;

	private final RSTile BODY_RIFT_TILE = new RSTile(3039, 4821, 0);
	private final RSArea BODY_ALTAR = new RSArea(new RSTile(2511, 4831, 0),
			new RSTile(2533, 4849, 0));
	private String BODY_NAME = "Body";
	private static int BODY_RUNE = 559;

	private final RSTile FIRE_RIFT_TILE = new RSTile(3029, 4830, 0);
	private final RSArea FIRE_ALTAR = new RSArea(new RSTile(2568, 4825, 0),
			new RSTile(2600, 4855, 0));
	private String FIRE_NAME = "Fire";
	private static int FIRE_RUNE = 554;

	private final RSTile EARTH_RIFT_TILE = new RSTile(3031, 4825, 0);
	private final RSArea EARTH_ALTAR = new RSArea(new RSTile(2640, 4817, 0),
			new RSTile(2672, 4854, 0));
	private String EARTH_NAME = "Earth";

	private final RSTile WATER_RIFT_TILE = new RSTile(3051, 4833, 0);
	private final RSArea WATER_ALTAR = new RSArea(new RSTile(2705, 4822, 0),
			new RSTile(2733, 4848, 0));
	private String WATER_NAME = "Water";
	private static int WATER_RUNE = 555;

	private final RSTile MIND_RIFT_TILE = new RSTile(3044, 4822, 0);
	private final RSArea MIND_ALTAR = new RSArea(new RSTile(2760, 4819, 0),
			new RSTile(2802, 4859, 0));
	private String MIND_NAME = "Mind";
	private static int MIND_RUNE = 558;

	private final RSTile AIR_RIFT_TILE = new RSTile(3047, 4825, 0);
	private final RSArea AIR_ALTAR = new RSArea(new RSTile(2837, 4826, 0),
			new RSTile(2850, 4841, 0));
	private String AIR_NAME = "Air";

	private RSArea ALTAR_AREA;
	private RSTile RIFT_TILE;
	private int RUNE_TYPE;
	private String RUNE_NAME;
	private int FOOD;
	private int HEAL_AMOUNT;
	private int PERCENT_TO_EAT;
	private int RANDOMIZED_HEAL;
	private boolean mountedGlory;
	private int EMERGENCY_PERCENT;
	private boolean ROCK_OBSTACLE;
	private boolean TENDRILS_OBSTACLE;
	private boolean EYES_OBSTACLE;
	private boolean BOIL_OBSTACLE;
	private boolean SQUEEZE_OBSTACLE;
	private int mountedGloryRotation;
	private int rune_value;
	private int essence_cost;
	private int food_cost;
	private int teleport_cost;
	private int potion_cost;
	private boolean teleportRegular;
	private boolean teleportDust;
	private boolean teleportTablet;
	private boolean paintObstacles;
	private boolean potions;

	private String status = "";
	private int[] CHARGED_GLORY = { 11978, 11976, 1712, 1710, 1708, 1706 };
	private int[] STAMINA_POTIONS = { 12625, 12627, 12629, 12631 };

	RSTile obstacle1 = new RSTile(3038, 4853, 0);
	RSTile obstacle2 = new RSTile(3028, 4849, 0);
	RSTile obstacle3 = new RSTile(3021, 4842, 0);
	RSTile obstacle4 = new RSTile(3018, 4833, 0);
	RSTile obstacle5 = new RSTile(3018, 4821, 0);
	RSTile obstacle6 = new RSTile(3026, 4813, 0);
	RSTile obstacle7 = new RSTile(3041, 4811, 0);
	RSTile obstacle8 = new RSTile(3049, 4813, 0);
	RSTile obstacle9 = new RSTile(3057, 4821, 0);
	RSTile obstacle10 = new RSTile(3060, 4830, 0);
	RSTile obstacle11 = new RSTile(3058, 4839, 0);
	RSTile obstacle12 = new RSTile(3049, 4849, 0);

	private final int rocksPoints = 1488;
	private final int tendrilsPoints = 1449;
	private final int boilPoints = 900;
	private final int eyesPoints = 1362;
	private final int gapPoints = 1026;
	private final int passagePoints = 801;
	private final int blockagePoints = 1068;

	private ArrayList<RSTile> obstacleLocations = new ArrayList<RSTile>();

	private final int AIR_RUNE = 556;
	private final int EARTH_RUNE = 557;
	private final int DUST_RUNE = 4696;
	private final int[] OUTSIDE_PORTAL = { 15478, 15482, 15479, 15480, 15481,
			15482, 15483, 15484, 15485, 15477 };
	private final int HOUSE_GLORY = 13523;
	private final int HOUSE_PORTAL = 4525;
	private final int HOUSE_TABLET = 8013;
	private final int ZAMORAK_MAGE_POINTS = 1920;
	private final int DARK_MAGE_POINTS = 1572;
	private final int GLORY_1 = 1706;
	private final int GLORY_2 = 1708;
	private final int GLORY_3 = 1710;
	private final int GLORY_4 = 1712;
	private final int UNCHARGED_GLORY = 1704;
	private final int TINDERBOX = 590;
	private final int PURE_ESSENCE = 7936;
	private final int SMALL_POUCH = 5509;
	private final int MEDIUM_POUCH = 5510;
	private final int LARGE_POUCH = 5512;
	private final int GIANT_POUCH = 5514;
	private final int MEDIUM_POUCH_DAMAGED = 5511;
	private final int LARGE_POUCH_DAMAGED = 5513;
	private final int GIANT_POUCH_DAMAGED = 5515;
	private int[] POUCHES = { SMALL_POUCH, MEDIUM_POUCH, LARGE_POUCH,
			MEDIUM_POUCH_DAMAGED, LARGE_POUCH_DAMAGED };
	private int[] POUCHES_DAMAGED = { MEDIUM_POUCH_DAMAGED,
			LARGE_POUCH_DAMAGED, GIANT_POUCH_DAMAGED };
	private final int BRONZE_HATCHET = 1351;
	private final int IRON_HATCHET = 1351;
	private final int STEEL_HATCHET = 1351;
	private final int MITH_HATCHET = 1351;
	private final int ADAMANT_HATCHET = 1351;
	private final int RUNE_HATCHET = 1351;
	private final int BRONZE_PICKAXE = 1265;
	private final int IRON_PICKAXE = 1267;
	private final int STEEL_PICKAXE = 1269;
	private final int MITH_PICKAXE = 1273;
	private final int ADAMANT_PICKAXE = 1271;
	private final int RUNE_PICKAXE = 1275;
	private final int SUPER_ENERGY_1 = 3022;
	private final int SUPER_ENERGY_2 = 3020;
	private final int SUPER_ENERGY_3 = 3018;
	private final int SUPER_ENERGY_4 = 3016;
	private final int EMPTY_VIAL = 229;
	private final int[] POTIONS = { SUPER_ENERGY_1, SUPER_ENERGY_2,
			SUPER_ENERGY_3, SUPER_ENERGY_4 };
	private final int[] HATCHETS = { BRONZE_HATCHET, IRON_HATCHET,
			STEEL_HATCHET, MITH_HATCHET, ADAMANT_HATCHET, RUNE_HATCHET };
	private final int[] PICKAXES = { BRONZE_PICKAXE, IRON_PICKAXE,
			STEEL_PICKAXE, MITH_PICKAXE, ADAMANT_PICKAXE, RUNE_PICKAXE };
	private int[] DONT_DEPOSIT = { PURE_ESSENCE, GLORY_1, GLORY_2, GLORY_3,
			GLORY_4, SMALL_POUCH, MEDIUM_POUCH, LARGE_POUCH, GIANT_POUCH,
			MEDIUM_POUCH_DAMAGED, LARGE_POUCH_DAMAGED, GIANT_POUCH_DAMAGED,
			FOOD, HOUSE_TABLET, TINDERBOX, BRONZE_HATCHET, IRON_HATCHET,
			STEEL_HATCHET, MITH_HATCHET, ADAMANT_HATCHET, RUNE_HATCHET,
			BRONZE_PICKAXE, IRON_PICKAXE, STEEL_PICKAXE, MITH_PICKAXE,
			ADAMANT_PICKAXE, RUNE_PICKAXE, AIR_RUNE, EARTH_RUNE, LAW_RUNE,
			DUST_RUNE };

	private boolean depositLaws = true;
	private boolean printOnce = true;
	private boolean setAngle = false;
	private boolean hopWorlds = false;
	private boolean firstTrip = true;
	private boolean countedPouchRepair = false;
	private boolean countedEmergency = false;
	private boolean countedPlayerKill = false;
	private boolean countedObstacle = false;
	private boolean countRunes = true;
	private boolean pouchesFull = false;
	private boolean giantPouchFull = false;
	private int runes = 0;
	private int startRunecraftingXP = 0;
	private int startRunecraftingLevel = 0;
	private int trips = 0;
	private int attemptedPlayerKill = 0;
	private int emergencyTeleport = 0;
	private int foodUsed = 0;
	private int pouchesRepaired = 0;
	private int passage = 0;
	private int rocks = 0;
	private int squeeze = 0;
	private int eyes = 0;
	private int boil = 0;
	private int tendrils = 0;
	private int cost = 0;
	private int fail = 0;
	private long startTime;
	private ArrayList<RSObject> obstacles = new ArrayList<RSObject>();
	private RSTile closestTile;

	private boolean clickedMinimap;
	private boolean gui_is_up = true;
	gui g = new gui();

	private boolean display = true;
	private final Image open = getImage("http://i.imgur.com/RoDv8CR.png");
	private final Image closed = getImage("http://i.imgur.com/4OlwNUH.png");

	// ArrayList<blacklistPlayer> blacklist = new ArrayList<blacklistPlayer>();

	int myLevel;
	ArrayList<String> weapons = new ArrayList<String>();
	ArrayList<String> avoidedPlayers = new ArrayList<String>();
	private int mouseSpeed = 0;

	private ABCUtil abc;

	private long last_busy_time;

	private boolean run = true;

	@Override
	public void run() {

		abc = new ABCUtil();

		g.setVisible(true);
		while (gui_is_up) {
			sleep(50);
		}

		while (Login.getLoginState() != Login.getLoginState().INGAME) {
			sleep(100);
		}

		startTime = System.currentTimeMillis();
		startRunecraftingXP = Skills.SKILLS.RUNECRAFTING.getXP();
		startRunecraftingLevel = Skills.SKILLS.RUNECRAFTING.getActualLevel();

		setVariables();

		myLevel = Player.getRSPlayer().getCombatLevel();

		Mouse.setSpeed(mouseSpeed);

		while (run) {

			if (jagexModerator()) {
				if (Inventory.getCount(HOUSE_TABLET) > 0) {
					useTablet();
				} else if (teleportRegular || teleportDust) {
					castTeleport();
				}
				super.setLoginBotState(false);
				Login.logout();
				break;
			} else if (outsidePortal()) {
				status = "Entering House";
				RSObject[] portal = Objects.findNearest(20, OUTSIDE_PORTAL);
				if (portal != null && portal.length > 0) {
					RSInterfaceChild enterHouse = Interfaces.get(232, 1);
					if (enterHouse != null) {
						enterHouse.click();

					} else {
						if (portal[0].isOnScreen()
								&& Player.getPosition().distanceTo(
										portal[0].getPosition()) < 5) {
							if (!Player.isMoving()) {
								portal[0].click("Enter");
								sleepUntilAtHouse();
							}
						} else {
							runTo(portal[0].getPosition(), true);
						}
					}
				}
			} else if (LUMBRIDGE.contains(Player.getPosition())
					|| FALADOR.contains(Player.getPosition())) {
				if (Inventory.getCount(HOUSE_TABLET) > 0) {
					println("In lumbridge/falador somehow, possibly recovered from Ring of life");
					println("We have tablets, we will continue on!");
					useTablet();
				} else if (((teleportRegular && haveRegularRunes()) || (teleportDust && haveDustRunes()))) {
					println("In lumbridge/falador somehow, possibly recovered from Ring of life");
					println("We have runes, we will continue on!");
					castTeleport();
				} else {
					println("Oh dear, you are dead!");
					break;
				}

			} else {

				if (needBank() || atHouse() || hopWorlds) {
					if (countRunes) {
						status = "Counting Runes";
						openInventory();
						if (RUNE_TYPE == LAW_RUNE) {
							if (teleportRegular) {
								int air = Inventory.getCount(AIR_RUNE);
								int law = Inventory.getCount(LAW_RUNE);
								runes += law - air;
							} else if (teleportDust) {
								int dust = Inventory.getCount(DUST_RUNE);
								int law = Inventory.getCount(LAW_RUNE);
								runes += law - dust;
							} else {
								runes += Inventory.getCount(LAW_RUNE);
							}
						} else {
							runes += Inventory.getCount(RUNE_TYPE);
						}
						trips++;
						countRunes = false;
					} else if (atEdgeville()) {
						if (hopWorlds) {
							super.setLoginBotState(false);
							println("Avoiding enemy, Changing Worlds");
							if (WorldHopper.changeWorld(WorldHopper
									.getRandomWorld(true)) == true) {
								hopWorlds = false;
							}
							println("Logging in!");
							super.setLoginBotState(true);
						} else {
							if (wearingUnchargedGlory()) {
								unequip(UNCHARGED_GLORY, "necklace");
							} else {
								if (!atBank()) {
									setPosition(100, 0);
									runTo(BANK_TILE, true);
								}
								if (!bank()) {
									fail++;
									if (fail >= 3) {
										println("Failed to bank, 3 times! Stopping Script");
										break;
									}
								}
							}
						}
					} else {
						if (mountedGlory) {
							teleportMountedGlory();
						} else {
							status = "Using Glory Teleport";
							if (isEquipped(CHARGED_GLORY)) {
								teleportGlory();
							}
						}
					}

				} else {

					if (atAltar()) {
						if (!countRunes)
							countRunes = true;
						craftRune();
					} else if (insideAbyss() && !insideAbyssRing()) {
						abyssObstacle();
					} else if (atEdgeville() && southOfDitch()) {
						setVariables();
						status = "Moving to Ditch";
						setPosition(100, 0);
						if (Player.getPosition().distanceTo(DITCH_TILE) > 3) {
							if (!Player.isMoving()) {
								abc.waitNewOrSwitchDelay(last_busy_time, true);
								walkPath(BANK_TO_DITCH);
								last_busy_time = Timing.currentTimeMillis();
							}
						} else {
							crossDitch();
						}
					} else if (insideWild()) {

						if (avoidEnemy()) {
							if (!countedEmergency) {
								emergencyTeleport++;
								countedEmergency = true;
							}

							if (Inventory.getCount(HOUSE_TABLET) > 0) {
								useTablet();
							} else if (teleportRegular || teleportDust) {
								castTeleport();
							}

						} else {
							RSNPC zamorak = getNPC(ZAMORAK_MAGE_POINTS);
							if (zamorak != null) {
								Camera.setCameraAngle(80);
								status = "Teleporting to Abyss";
								RSTile pos = zamorak.getPosition();
								if (pos != null) {
									if (!clickedMinimap) {
										Walking.clickTileMM(pos, 1);
										clickedMinimap = true;
									}
									if (!insideAbyss() && zamorak != null) {
										if (zamorak.isOnScreen()) {
											abc.waitNewOrSwitchDelay(
													last_busy_time, true);
											zamorak.hover();
											Mouse.click(3);
											if (ChooseOption.select("Teleport")) {
												sleepUntilAtAbyss();
											}
											last_busy_time = Timing
													.currentTimeMillis();
										} else {
											Camera.turnToTile(pos);
											if (!zamorak.isOnScreen()) {
												if (!Player.isMoving()) {
													runTo(pos, true);
												}
											}
										}
									}
								}
							} else {
								status = "Running to Zamorak Mage";
								if (!Player.isMoving()) {
									walkPath(DITCH_TO_MAGE);
								}
							}
						}
					} else if (insideAbyssRing()) {
						if (pouchIsDamaged()) {
							status = "Damaged Pouches!";
							repairPouches();
						} else {
							useRift();
						}
					} else if (!insideWild() && !insideAbyssRing()
							&& !insideAbyss() && !atBank() && !atEdgeville()
							&& !atHouse() && !atAltar()) {
						status = "We are lost!";
						Timer t = new Timer(10000);
						boolean lost = true;
						while (t.isRunning()) {
							if (insideWild() || insideAbyssRing()
									|| insideAbyss() || atBank()
									|| atEdgeville() || atHouse() || atAltar()) {
								lost = false;
								break;
							}
							sleep(1000);
						}
						if (lost) {
							if (Inventory.getCount(HOUSE_TABLET) > 0) {
								useTablet();
							} else if (teleportRegular || teleportDust) {
								castTeleport();
							}
						}
					}
				}
			}

			if (Game.getRunEnergy() >= abc.INT_TRACKER.NEXT_RUN_AT.next()) {
				Options.setRunOn(true);
				abc.INT_TRACKER.NEXT_RUN_AT.reset();
			}

			abc.performXPCheck(SKILLS.RUNECRAFTING);
			abc.performRotateCamera();
			abc.performExamineObject();
			abc.performPickupMouse();
			abc.performRandomMouseMovement();
			abc.performRandomRightClick();
			abc.performQuestsCheck();
			abc.performFriendsCheck();
			abc.performMusicCheck();
			abc.performCombatCheck();

			sleep(100);
		}
	}

	private RSNPC getNPC(int p) {
		RSNPC[] npcs = NPCs.getAll();
		if (npcs != null && npcs.length > 0) {
			for (int i = 0; i < npcs.length; i++) {
				RSNPC npc = npcs[i];
				if (npc != null) {
					RSModel model = npc.getModel();
					if (model != null) {
						Point[] points = model.getPoints();
						if (points != null) {
							if (points.length == p) {
								return npcs[i];
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void setVariables() {
		obstacleLocations.clear();
		obstacleLocations.add(obstacle1);
		obstacleLocations.add(obstacle2);
		obstacleLocations.add(obstacle3);
		obstacleLocations.add(obstacle4);
		obstacleLocations.add(obstacle5);
		obstacleLocations.add(obstacle6);
		obstacleLocations.add(obstacle7);
		obstacleLocations.add(obstacle8);
		obstacleLocations.add(obstacle9);
		obstacleLocations.add(obstacle10);
		obstacleLocations.add(obstacle11);
		obstacleLocations.add(obstacle12);
		countedPlayerKill = false;
		countedEmergency = false;
		countedObstacle = false;
		countedPouchRepair = false;
		clickedMinimap = false;
		setAngle = false;
		printOnce = true;
		depositLaws = true;
		fail = 0;
	}

	private void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			status = "Opening Inventory";
			GameTab.open(TABS.INVENTORY);
		}
	}

	private void openMagicTab() {
		if (GameTab.getOpen() != TABS.MAGIC) {
			status = "Opening Magic Tab";
			GameTab.open(TABS.MAGIC);
		}
	}

	private void useTablet() {
		openInventory();
		if (Inventory.getCount(HOUSE_TABLET) > 0) {
			RSItem[] house = Inventory.find(HOUSE_TABLET);
			if (house.length > 0) {
				if (house[0] != null) {
					status = "Teleporting To House";
					house[0].click();
					sleepUntilAtHouse();
				}
			}
		}
	}

	private void castTeleport() {
		openMagicTab();
		Magic.selectSpell("Teleport to House");
		sleepUntilAtHouse();
	}

	private void sleepUntilAtHouse() {
		status = "Waiting for House";
		Timing.waitCondition(new Condition() {
			public boolean active() {
				sleep(200, 500);
				return atHouse();
			}
		}, 6000);
		status = "Arrived at House";
		sleep(1000, 1200);
	}

	private boolean avoidEnemy() {
		RSPlayer[] players = Players.getAll();
		for (int i = 0; i < players.length; i++) {
			int enemyLevel = 0;
			enemyLevel = players[i].getCombatLevel();
			if (enemyLevel > 0) {
				if (Math.abs(myLevel - enemyLevel) <= 5) { // CHANGE TO 5
					String name = players[i].getName();
					if (name != null) {
						if (name != Player.getRSPlayer().getName()) {
							if (name != Player.getRSPlayer().getName()) {
								RSPlayerDefinition playerDef = players[i]
										.getDefinition();
								if (playerDef != null) {
									RSItem[] equipment = playerDef
											.getEquipment();
									for (int j = 0; j < equipment.length; j++) {
										if (equipment[j] != null) {
											RSItemDefinition equipDef = equipment[j]
													.getDefinition();
											if (equipDef != null) {
												String equip = equipment[j]
														.getDefinition()
														.getName();
												equip = equip.trim();
												equip = equip.toLowerCase();
												if (equip != null) {
													for (int k = 0; k < weapons
															.size(); k++) {
														if (equip
																.contains(weapons
																		.get(k))) {
															String player = "\""
																	+ name
																	+ "\" (Level: "
																	+ enemyLevel
																	+ ") using "
																	+ equip;
															avoidedPlayers
																	.add(player);
															attemptedPlayerKill++;
															countedPlayerKill = true;
															hopWorlds = true;
															return true;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean outsidePortal() {
		return Objects.find(10, OUTSIDE_PORTAL).length > 0;
	}

	private RSTile getClosestTile(ArrayList<RSTile> list) {
		RSTile closestObstacle = null;
		int closestLength = 500;

		for (int i = 0; i < list.size(); i++) {
			if (Player.getPosition().distanceTo(list.get(i)) < closestLength) {
				closestLength = Player.getPosition().distanceTo(list.get(i));
				closestObstacle = list.get(i);
			}
		}
		return closestObstacle;
	}

	private void setPosition(int angle, int rotation) {
		// status = "Setting Angle";
		Camera.setCameraAngle(angle);
		// status = "Setting Rotation";
		Camera.setCameraRotation(rotation);
	}

	private void abyssObstacle() {
		if (getHPPercent() <= EMERGENCY_PERCENT) {
			if (!countedEmergency) {
				println("Health is " + getHPPercent()
						+ "%, we need to teleport!");
				emergencyTeleport++;
				countedEmergency = true;
			}

			if (Inventory.getCount(HOUSE_TABLET) > 0) {
				useTablet();
			} else if (teleportRegular || teleportDust) {
				castTeleport();
			}

		} else {

			closestTile = getClosestTile(obstacleLocations);
			RSTile position = Player.getPosition();

			if (position != null && closestTile != null
					&& position.distanceTo(closestTile) >= 7) {
				status = "Moving to Obstacle";
				if (!Player.isMoving()) {
					runTo(closestTile, false);
				}
			} else {
				if (printOnce) {
					println("----- Trip #" + trips + " -----");
					printOnce = false;
				}

				getAbyssObstacles(new int[] { rocksPoints, tendrilsPoints,
						boilPoints, eyesPoints, gapPoints, passagePoints,
						blockagePoints });

				if (obstacles != null && obstacles.size() > 0) {

					RSObject currentObstacle = null;

					for (int i = 0; i < obstacles.size(); i++) {
						if (obstacles.get(i) != null) {
							RSTile obsPos = obstacles.get(i).getPosition();
							if (obsPos != null && closestTile != null
									&& obsPos.distanceTo(closestTile) <= 3) {
								currentObstacle = obstacles.get(i);
								break;
							}
							sleep(50, 100);
						}
					}

					int obstaclePoints = 0;

					if (currentObstacle != null) {
						RSModel model = currentObstacle.getModel();
						if (model != null) {
							Point[] points = model.getPoints();
							if (points != null) {
								obstaclePoints = points.length;
							}
						}
					}

					if (currentObstacle != null) {
						RSTile obsPos = currentObstacle.getPosition();
						if (obsPos != null) {
							if (obstaclePoints == passagePoints) {
								if (!countedObstacle && obsPos != null) {
									println("Found Go-through Passage at "
											+ obsPos);
									println("--------------------");
									passage++;
									countedObstacle = true;
								}
								status = "Go-through Passage";
								interactWithObstacle(currentObstacle,
										"Go-through");

							} else if (obstaclePoints == rocksPoints) {
								if (ROCK_OBSTACLE) {
									if (!countedObstacle) {
										println("Found Mine Rocks at " + obsPos);
										println("--------------------");
										rocks++;
										countedObstacle = true;
									}
									status = "Mine Rocks";
									interactWithObstacle(currentObstacle,
											"Mine");
								} else {
									removeObstacle(obsPos);
								}

							} else if (obstaclePoints == tendrilsPoints) {
								if (TENDRILS_OBSTACLE) {
									if (!countedObstacle) {
										println("Found Chop Tendrils at "
												+ obsPos);
										println("--------------------");
										tendrils++;
										countedObstacle = true;
									}
									status = "Chop Tendrils";
									interactWithObstacle(currentObstacle,
											"Chop");
								} else {
									removeObstacle(obsPos);
								}

							} else if (obstaclePoints == boilPoints) {
								if (BOIL_OBSTACLE) {
									if (!countedObstacle) {
										println("Found Burn-down Boil at "
												+ obsPos);
										println("--------------------");
										boil++;
										countedObstacle = true;
									}
									status = "Burn-down Boil";
									interactWithObstacle(currentObstacle,
											"Burn-down");
								} else {
									removeObstacle(obsPos);
								}

							} else if (obstaclePoints == eyesPoints) {
								if (EYES_OBSTACLE) {
									if (!countedObstacle) {
										println("Found Distract Eyes at "
												+ obsPos);
										println("--------------------");
										eyes++;
										countedObstacle = true;
									}
									status = "Distract Eyes";
									interactWithObstacle(currentObstacle,
											"Distract");
								} else {
									removeObstacle(obsPos);
								}

							} else if (obstaclePoints == gapPoints) {
								if (SQUEEZE_OBSTACLE) {
									if (!countedObstacle) {
										println("Found Squeeze-through at "
												+ obsPos);
										println("--------------------");
										squeeze++;
										countedObstacle = true;
									}
									status = "Squeeze-through Gap";
									interactWithObstacle(currentObstacle,
											"Squeeze-through");
								} else {
									removeObstacle(obsPos);
								}
							} else if (obstaclePoints == blockagePoints) {
								removeObstacle(obsPos);
							} else {
								removeObstacle(obsPos);
							}
						}
					}
				}
			}
		}
	}

	private void removeObstacle(RSTile t) {
		if (t != null) {
			for (int i = 0; i < obstacleLocations.size(); i++) {
				if (obstacleLocations.get(i) != null
						&& t.equals(obstacleLocations.get(i))) {
					println("Removed Obstacle at " + obstacleLocations.get(i));
					obstacleLocations.remove(i);
					break;
				}
			}
		}
	}

	private void getAbyssObstacles(int[] points) {
		obstacles.clear();
		RSObject[] obj = Objects.getAll(10);
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				RSObject object = obj[i];
				if (object != null) {
					RSModel model = object.getModel();
					if (model != null) {
						Point[] point = object.getModel().getPoints();
						if (point != null && point.length > 0) {
							int modelPoints = point.length;
							if (modelPoints > 800) {
								for (int j = 0; j < points.length; j++) {
									if (modelPoints == points[j]) {
										for (int k = 0; k < obstacleLocations
												.size(); k++) {
											RSTile objPos = obj[i]
													.getPosition();
											if (objPos != null) {
												if (obstacleLocations.get(k)
														.equals(objPos)) {
													// println(modelPoints
													// + " = "
													// + points[j]
													// + " Tile: "
													// + obj[i].getPosition());
													obstacles.add(obj[i]);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void interactWithObstacle(RSObject obstacle, String s) {
		if (obstacle != null && s != null) {
			status = s + " Obstacle!";
			RSTile position = Player.getPosition();
			if (position != null && obstacle.getPosition() != null
					&& position.distanceTo(obstacle.getPosition()) <= 3) {
				Camera.turnToTile(obstacle);
				obstacle.hover();
				Mouse.click(3);
				sleep(25, 75);
				if (ChooseOption.isOpen()) {
					if (ChooseOption.select(s)) {
						sleep(150, 250);
					} else {
						Point pos = Mouse.getPos();
						Mouse.move(pos.x += General.random(-50, 50),
								pos.y += General.random(-50, 50));
					}
				}
			} else {
				if (obstacle.getPosition() != null) {
					if (!Player.isMoving()) {
						runTo(obstacle.getPosition(), false);
						Camera.turnToTile(obstacle);
					}
				}
			}
		}
	}

	private RSObject getRift(RSTile t) {
		RSObject[] objs = Objects.getAt(t);

		for (int i = 0; i < objs.length; i++) {
			RSObject obj = objs[i];
			if (obj != null) {
				RSObjectDefinition def = obj.getDefinition();
				if (def != null) {
					String name = def.getName();
					if (name.contains("rift")) {
						return obj;
					}
				}
			}
		}
		return null;
	}

	public Point getBestPoint(Point[] points) {
		int x = 0, y = 0;
		int avgX, avgY;
		int total = points.length;

		for (Point p : points) {
			x += p.getX();
			y += p.getY();
		}

		avgX = x / total;
		avgY = y / total;
		return new Point(avgX, avgY);
	}

	private void useRift() {
		if (Player.getPosition().distanceTo(RIFT_TILE) < 2) {
			if (RUNE_TYPE == NATURE_RUNE) {
				setPosition(100, 180);
			} else if (RUNE_TYPE == CHAOS_RUNE) {
				setPosition(100, 180);
			} else if (RUNE_TYPE == LAW_RUNE) {
				setPosition(100, 90);
			} else if (RUNE_TYPE == COSMIC_RUNE) {
				setPosition(100, 270);
			} else if (RUNE_TYPE == BODY_RUNE) {
				setPosition(100, 0);
			} else if (RUNE_TYPE == FIRE_RUNE) {
				setPosition(100, 270);
			} else if (RUNE_TYPE == EARTH_RUNE) {
				setPosition(100, 270);
			} else if (RUNE_TYPE == WATER_RUNE) {
				setPosition(100, 90);
			} else if (RUNE_TYPE == MIND_RUNE) {
				setPosition(100, 0);
			} else if (RUNE_TYPE == AIR_RUNE) {
				setPosition(100, 0);
			}
			RSObject rift = getRift(RIFT_TILE);
			if (rift != null) {
				status = "Exiting-through Rift!";
				abc.waitNewOrSwitchDelay(last_busy_time, true);
				if (RUNE_TYPE != LAW_RUNE && RUNE_TYPE != CHAOS_RUNE
						&& RUNE_TYPE != BODY_RUNE) {
					RSModel model = rift.getModel();
					if (model != null) {
						Point[] points = model.getAllVisiblePoints();
						if (points != null && points.length > 0) {
							Point p = getBestPoint(points);
							p.x += General.random(-3, 3);
							p.y += General.random(-3, 3);
							if (!Game.getUptext().contains("Exit-through")) {
								Mouse.move(p);
								sleep(25, 75);
							}
							if (Game.getUptext().contains("Exit-through")) {
								Mouse.click(3);
								sleep(50, 125);
								if (ChooseOption.isOpen()) {
									if (ChooseOption.select("Exit-through")) {
										status = "Sleeping For Altar";
										Timer t = new Timer(3000);
										while (t.isRunning()) {
											if (atAltar()) {
												status = "We've arrived!";
												break;
											}
											sleep(500);
										}
									} else {
										Point pos = Mouse.getPos();
										Mouse.move(
												pos.x += General
														.random(-50, 50),
												pos.y += General
														.random(-50, 50));
									}
								}
							}
						}
					}
				} else {
					Point p = (Projection.tileToScreen(
							rift.getAnimablePosition(),
							General.random(265, 285)));
					p.x += General.random(-3, 3);
					p.y += General.random(-3, 3);
					if (!Game.getUptext().contains("Exit-through")) {
						Mouse.move(p);
						sleep(25, 75);
					}
					if (Game.getUptext().contains("Exit-through")) {
						Mouse.click(3);
						sleep(50, 125);
						if (ChooseOption.isOpen()) {
							if (ChooseOption.select("Exit-through")) {
								status = "Sleeping For Altar";
								Timer t = new Timer(3000);
								while (t.isRunning()) {
									if (Player.getPosition() != null
											&& Player.getPosition().distanceTo(
													RIFT_TILE) > 10) {
										break;
									}
									if (Player.isMoving()
											|| Player.getAnimation() != -1) {
										t.reset();
									}
								}
								sleep(200, 300);
							} else {
								Point pos = Mouse.getPos();
								Mouse.move(pos.x += General.random(-50, 50),
										pos.y += General.random(-50, 50));
							}
						}
					}
				}
			}
			last_busy_time = Timing.currentTimeMillis();
		} else {
			status = "Setting Tile!";
			if (!Player.isMoving()) {
				abc.waitNewOrSwitchDelay(last_busy_time, true);
				runTo(RIFT_TILE, true);
				last_busy_time = Timing.currentTimeMillis();
			}
			if (!setAngle) {
				if (RUNE_TYPE == NATURE_RUNE) {
					setPosition(100, 180);
				} else if (RUNE_TYPE == CHAOS_RUNE) {
					setPosition(100, 180);
				} else if (RUNE_TYPE == LAW_RUNE) {
					setPosition(100, 90);
				} else if (RUNE_TYPE == COSMIC_RUNE) {
					setPosition(100, 270);
				} else if (RUNE_TYPE == BODY_RUNE) {
					setPosition(100, 0);
				} else if (RUNE_TYPE == FIRE_RUNE) {
					setPosition(100, 270);
				} else if (RUNE_TYPE == EARTH_RUNE) {
					setPosition(100, 270);
				} else if (RUNE_TYPE == WATER_RUNE) {
					setPosition(100, 90);
				} else if (RUNE_TYPE == MIND_RUNE) {
					setPosition(100, 0);
				} else if (RUNE_TYPE == AIR_RUNE) {
					setPosition(100, 0);
				}
				setAngle = true;
			}
		}
	}

	private void repairPouches() {
		RSNPC mage = getNPC(DARK_MAGE_POINTS);
		if (mage != null) {
			Camera.turnToTile(mage.getPosition());
			if (Player.getPosition().distanceTo(mage.getPosition()) > 3) {
				if (!Player.isMoving()) {
					runTo(mage.getPosition(), true);
				}
			} else {
				if (!countedPouchRepair) {
					countedPouchRepair = true;
					pouchesRepaired++;
				}
				abc.waitNewOrSwitchDelay(last_busy_time, true);
				mage.click("Repairs");
				status = "Repairing Pouches";
				Timer repair = new Timer(3000);
				while (repair.isRunning()) {
					if (!pouchIsDamaged()) {
						break;
					}
					sleep(500);
				}
				last_busy_time = Timing.currentTimeMillis();
			}
		} else {
			if (!Player.isMoving()) {
				status = "Rift Not Visible!";
				Camera.turnToTile(DARK_MAGE_TILE);
				Point p = Projection.tileToMinimap(DARK_MAGE_TILE);
				Mouse.click(p, 1);
			}
		}
	}

	private boolean jagexModerator() {
		RSPlayer[] players = Players.getAll();
		if (players != null && players.length > 0) {
			for (int i = 0; i < players.length; i++) {
				if (players[i] != null && players[i].getName() != null) {
					if (players[i].getName().toLowerCase().contains("mod")) {
						println("Found possible Jagex Moderator: "
								+ players[i].getName());
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean pouchIsDamaged() {
		if (inventoryContains(POUCHES_DAMAGED)) {
			return true;
		} else {
			return false;
		}
	}

	private RSObject getClosestObjectByName(String s) {

		RSObject[] obj = Objects.getAll(30);
		ArrayList<RSObject> sortedObjects = new ArrayList<RSObject>();
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null) {
					RSObjectDefinition def = obj[i].getDefinition();
					if (def != null) {
						String name = def.getName();
						if (name != null) {
							if (name.equalsIgnoreCase(s)) {
								sortedObjects.add(obj[i]);
								break;
							}
						}
					}
				}
			}
		}

		RSObject closest = null;

		if (sortedObjects.size() > 0) {
			RSTile pos = Player.getPosition();
			int distance = 0;
			if (pos != null) {
				distance = sortedObjects.get(0).getPosition().distanceTo(pos);
				closest = sortedObjects.get(0);
				for (int i = 1; i < sortedObjects.size(); i++) {
					if (sortedObjects.get(i).getPosition().distanceTo(pos) < distance) {
						distance = sortedObjects.get(i).getPosition()
								.distanceTo(pos);
						closest = sortedObjects.get(i);
					}
				}
			}
		}

		if (closest != null) {
			return closest;
		} else {
			return null;
		}
	}

	private boolean atAltar() {
		RSTile pos = Player.getPosition();
		if (pos != null) {
			if (ALTAR_AREA != null) {
				if (ALTAR_AREA.contains(pos)) {
					status = "At Altar!";
					return true;
				}
			}
		}
		return false;
	}

	private void craftRune() {

		RSObject altar = getClosestObjectByName("Altar");

		if (altar != null) {
			if (firstTrip) {
				firstTrip = false;
			}
			if (Player.getPosition().distanceTo(altar.getPosition()) > 5) {
				if (!Player.isMoving()) {
					runTo(altar.getPosition(), true);
				}
			} else {
				openInventory();
				if (!hasEssence() && hasGiantPouch() && giantPouchFull) {

					RSItem[] inventory = Inventory.getAll();
					for (int i = 0; i < inventory.length; i++) {
						if (inventory[i].getID() == GIANT_POUCH) {
							status = "Emptying Giant Pouch";
							int count = Inventory.find(PURE_ESSENCE).length;
							inventory[i].click("Empty");
							Timer emptying = new Timer(1000);
							while (emptying.isRunning()) {
								if (count != Inventory.find(PURE_ESSENCE).length) {
									status = "Giant Pouch Empty!";
									break;
								}
								sleep(100);
							}
							break;
						}
					}

					status = "Giant Pouch Emptied!";
					giantPouchFull = false;

				} else if (!hasEssence() && inventoryContains(POUCHES)
						&& pouchesFull) {

					RSItem[] inventory = Inventory.getAll();
					for (int i = 0; i < inventory.length; i++) {
						for (int j = 0; j < POUCHES.length; j++) {
							if ((inventory[i].getID() == POUCHES[j])
									&& (inventory[i].getID() != GIANT_POUCH)) {
								status = "Emptying Pouches!";
								int count = Inventory.find(PURE_ESSENCE).length;
								inventory[i].click("Empty");
								Timer emptying = new Timer(1000);
								while (emptying.isRunning()) {
									if (count != Inventory.find(PURE_ESSENCE).length) {
										status = "Pouch Empty!";
										break;
									}
									sleep(100);
								}
								break;
							}
						}
					}

					status = "All Pouches Emptied!";
					pouchesFull = false;

				} else if (hasEssence()) {
					if (altar != null) {
						RSModel model = altar.getModel();
						if (model != null) {
							Point[] points = model.getAllVisiblePoints();
							if (points != null && points.length > 0) {
								Point altarPoint = getBestPoint(points);
								abc.waitNewOrSwitchDelay(last_busy_time, true);
								Mouse.move(altarPoint);
								if (Game.getUptext().contains("Craft-rune")) {
									Mouse.click(1);
									int count = Inventory
											.getCount(PURE_ESSENCE);
									Point p = new Point(640, 187);
									status = "Crafting " + RUNE_NAME
											+ " runes!";
									Mouse.move(p.x + General.random(-50, 50),
											p.y + General.random(-50, 50));
									sleepUntilIdle();
									if (count != Inventory
											.getCount(PURE_ESSENCE)) {
										cost += (count * essence_cost);
									}
								}
								last_busy_time = Timing.currentTimeMillis();
							}
						}
					}
				}
			}
		} else {
			status = "Null Altar";
		}
	}

	private boolean insideAbyss() {
		if (ABYSS_AREA.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean insideWild() {
		if (WILDERNESS.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean insideAbyssRing() {
		if (ABYSS_RING_AREA.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isTileOnMinimap(RSTile t) {
		if (Projection.isInMinimap(Projection.tileToMinimap(t))) {
			return true;
		} else {
			return false;
		}
	}

	private void crossDitch() {
		RSInterfaceChild wildernessWarning = Interfaces.get(382, 24);
		if (wildernessWarning != null) {
			status = "Wilderness Warning!";
			if (wildernessWarning.click()) {
				Timing.waitCondition(new Condition() {
					public boolean active() {
						sleep(250, 500);
						return !Interfaces.isInterfaceValid(382);
					}
				}, 2500);
			}
		} else {
			RSObject[] wildernessDitch = Objects.findNearest(10, 23271);
			if (wildernessDitch.length > 0 && wildernessDitch[0] != null) {
				status = "Crossing Ditch";
				wildernessDitch[0].click("Cross");
				long timer = System.currentTimeMillis() + 2000;
				while (timer > System.currentTimeMillis()) {
					if (Player.getAnimation() != -1 || Player.isMoving())
						timer = System.currentTimeMillis() + 1500;
					if (Player.getPosition().getY() >= 3523) {
						status = "Over Ditch";
						break;
					}
					sleep(1000);
				}
			}
		}
	}

	private boolean southOfDitch() {
		return Player.getPosition().getY() <= 3520;
	}

	private void EAT_DRINK_EQUIP() {
		if (Banking.isBankScreenOpen()) {
			status = "Closing Bank";
			Banking.close();
			Timing.waitCondition(new Condition() {
				public boolean active() {
					sleep(250, 500);
					return !Banking.isBankScreenOpen();
				}
			}, 3000);

		}

		if (!Banking.isBankScreenOpen()) {

			openInventory();

			if (inventoryContains(CHARGED_GLORY)) {
				Timer t = new Timer(3000);
				while (inventoryContains(CHARGED_GLORY) && t.isRunning()) {
					RSItem[] glory = Inventory.find(CHARGED_GLORY);
					if (glory.length > 0) {
						status = "Equipping Glory";
						glory[0].click();
						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep(250, 500);
								return Equipment.isEquipped(CHARGED_GLORY);
							}
						}, 3000);
					}
				}
			}

			if (inventoryContains(FOOD)) {
				status = "Eating Food";
				Timer t = new Timer(3000);
				while (inventoryContains(FOOD) && t.isRunning()) {
					if (Banking.isBankScreenOpen()) {
						Banking.close();
					} else {
						RSItem[] food = Inventory.find(FOOD);
						if (food != null && food.length > 0) {
							for (int i = 0; i < food.length; i++) {
								food[i].click();
								foodUsed++;
								cost += food_cost;
								sleep(1600, 1700);
								t.reset();
							}
						}
						int value = RANDOMIZED_HEAL + (General.random(-5, 5));
						PERCENT_TO_EAT = value;
						println("Healing if below " + value + "% health");
					}
				}
			}

			if (inventoryContains(STAMINA_POTIONS)) {
				status = "Stamina Potions";
				if (Game.getSetting(638) == 0) {
					RSItem[] find = Inventory.find(STAMINA_POTIONS);
					if (find != null && find.length > 0) {
						RSItem potion = find[0];
						if (potion != null) {
							RSItemDefinition def = potion.getDefinition();
							if (def != null) {
								String name = def.getName();
								if (name != null) {
									status = "Drinking " + name;
									potion.click();
									cost += potion_cost;
									Timing.waitCondition(new Condition() {
										public boolean active() {
											sleep(250, 500);
											return Game.getSetting(638) != 0;
										}
									}, 3000);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean bank() {
		if (!Banking.isBankScreenOpen()) {
			status = "Opening Bank";
			openInventory();
			Banking.openBank();
			return true;

		} else {

			status = "Depositing";

			if (depositLaws && (RUNE_TYPE == LAW_RUNE)) {
				status = "Depositing Law Runes";
				if (teleportDust) {
					int law = Inventory.getCount(LAW_RUNE);
					int dust = Inventory.getCount(DUST_RUNE);
					if (law > dust) {
						Banking.deposit(law - dust, LAW_RUNE);
					}
				} else if (teleportRegular) {
					int law = Inventory.getCount(LAW_RUNE);
					int air = Inventory.getCount(AIR_RUNE);
					if (law > air) {
						Banking.deposit(law - air, LAW_RUNE);
					}
				} else {
					Banking.deposit(0, LAW_RUNE);
				}
				depositLaws = false;
			}
			if (teleportDust || teleportTablet) {
				if (Inventory.getCount(AIR_RUNE) > 0) {
					Banking.deposit(0, AIR_RUNE);
				}
				if (Inventory.getCount(EARTH_RUNE) > 0) {
					Banking.deposit(0, EARTH_RUNE);
				}
			}
			if (teleportRegular || teleportTablet) {
				if (Inventory.getCount(DUST_RUNE) > 0) {
					Banking.deposit(0, DUST_RUNE);
				}
			}
			if (teleportTablet) {
				if (Inventory.getCount(LAW_RUNE) > 0) {
					Banking.deposit(0, LAW_RUNE);
				}
			}

			depositAllExcept(DONT_DEPOSIT);

			boolean close = false;

			if (!mountedGlory && !isEquipped(CHARGED_GLORY)
					&& !inventoryContains(CHARGED_GLORY)) {

				status = "Need Charged Glory!";

				if (Inventory.isFull()) {
					Banking.deposit(1, PURE_ESSENCE);
				}

				RSItem findGlory = Banking.find(CHARGED_GLORY)[0];
				close = true;
				if (!withdrawItem(findGlory.getID(), 1, "Glory")) {
					return false;
				}
			}

			if (potions
					&& (Game.getSetting(638) == 0 && Game.getRunEnergy() < 80)) {
				if (!hasPotion()) {
					RSItem[] stamina = Banking.find(STAMINA_POTIONS);
					if (stamina != null && stamina.length > 0) {
						RSItemDefinition def = stamina[0].getDefinition();
						if (def != null) {
							String name = def.getName();
							if (name != null) {
								if (Inventory.isFull()) {
									Banking.deposit(1, PURE_ESSENCE);
								}
								close = true;
								if (!withdrawItem(stamina[0].getID(), 1, name)) {
									return false;
								}
							}
						}
					}
				}
			}

			if (getHPPercent() <= PERCENT_TO_EAT) {
				int healthToHeal = Skills.SKILLS.HITPOINTS.getActualLevel()
						- Skills.SKILLS.HITPOINTS.getCurrentLevel();
				int foodNeeded = (int) Math.ceil((double) healthToHeal
						/ (double) HEAL_AMOUNT);
				if ((28 - Inventory.getAll().length) < foodNeeded) {
					Banking.deposit(0, PURE_ESSENCE);
					sleep(1000);
				}

				close = true;
				if (!withdrawItem(FOOD, foodNeeded, "Food")) {
					return false;
				}
			}

			if (close) {
				EAT_DRINK_EQUIP();

			} else {

				if (teleportTablet && Inventory.getCount(HOUSE_TABLET) == 0) {
					if (!withdrawItem(HOUSE_TABLET, 10, "House Teleport")) {
						return false;
					}
				} else if (((teleportRegular && !haveRegularRunes()) || (teleportDust && !haveDustRunes()))) {
					if (teleportRegular) {
						if (Inventory.getCount(AIR_RUNE) == 0) {
							if (!withdrawItem(AIR_RUNE, 10, "Air Rune")) {
								return false;
							}
						}
						if (Inventory.getCount(EARTH_RUNE) == 0) {
							if (!withdrawItem(EARTH_RUNE, 10, "Earth Rune")) {
								return false;
							}
						}
						if (Inventory.getCount(LAW_RUNE) == 0) {
							if (!withdrawItem(LAW_RUNE, 10, "Law Rune")) {
								return false;
							}
						}
					} else if (teleportDust) {
						if (Inventory.getCount(DUST_RUNE) == 0) {
							if (!withdrawItem(DUST_RUNE, 10, "Dust Rune")) {
								return false;
							}
						}
						if (Inventory.getCount(LAW_RUNE) == 0) {
							if (!withdrawItem(LAW_RUNE, 10, "Law Rune")) {
								return false;
							}
						}
					}
				} else if (hasGiantPouch() && !giantPouchFull) {
					if (!Inventory.isFull()) {
						if (!withdrawItem(PURE_ESSENCE, 0, "Pure Essence")) {
							return false;
						}
					}

					if (Banking.isBankScreenOpen()) {
						status = "Closing Bank";
						Banking.close();
						Timer closeBank = new Timer(2000);
						while (closeBank.isRunning()) {
							if (!Banking.isBankScreenOpen()) {
								break;
							}
							sleep(200, 250);
						}
					}

					if (!Banking.isBankScreenOpen()) {
						RSItem[] inventory = Inventory.getAll();
						for (int i = 0; i < inventory.length; i++) {
							if (inventory[i].getID() == GIANT_POUCH) {
								status = "Filling Giant Pouch";
								int essCount = Inventory.find(PURE_ESSENCE).length;
								inventory[i].hover();
								inventory[i].click();
								Timer t = new Timer(1000);
								while (t.isRunning()) {
									if (essCount != Inventory
											.find(PURE_ESSENCE).length) {
										break;
									}
									sleep(100);
								}
								status = "Giant Pouch Filled!";
								giantPouchFull = true;
								break;
							}
						}
					}

				} else if (hasPouches() && !pouchesFull) {
					if (!Inventory.isFull()) {
						if (!withdrawItem(PURE_ESSENCE, 0, "Pure Essence")) {
							return false;
						}
					}

					if (Banking.isBankScreenOpen()) {
						status = "Closing Bank";
						Banking.close();
						Timer closeBank = new Timer(2000);
						while (closeBank.isRunning()) {
							if (!Banking.isBankScreenOpen()) {
								break;
							}
							sleep(200, 250);
						}
					}

					if (!Banking.isBankScreenOpen()) {
						RSItem[] inventory = Inventory.getAll();
						for (int i = 0; i < inventory.length; i++) {
							for (int j = 0; j < POUCHES.length; j++) {
								if (inventory[i].getID() == POUCHES[j]) {
									status = "Filling Regular Pouches";
									int essCount = Inventory.find(PURE_ESSENCE).length;
									inventory[i].hover();
									inventory[i].click();
									Timer t = new Timer(1000);
									while (t.isRunning()) {
										if (essCount != Inventory
												.find(PURE_ESSENCE).length) {
											break;
										}
										sleep(100);
									}
									status = "Pouches Filled!";
									pouchesFull = true;
									break;
								}
							}
						}
					}
				} else {
					if (!withdrawItem(PURE_ESSENCE, 0, "Pure Essence")) {
						return false;
					}
				}
			}
			return true;
		}
	}

	private boolean withdrawItem(final int ID, int quantity, String name) {
		status = "Withdrawing " + quantity + " " + name;
		openInventory();
		final int count = Inventory.getCount(ID);
		if (bankContains(ID, name)) {
			if (name.equals("Pure essence")) {
				Banking.withdraw(0, ID);
			} else {
				Banking.withdraw(quantity, ID);
			}
			Timing.waitCondition(new Condition() {
				public boolean active() {
					sleep(250, 500);
					return Inventory.getCount(ID) != count;
				}
			}, 3000);
			status = "We have " + name;
			return true;
		} else {
			println("We are out of " + name + "!");
			return false;
		}
	}

	private boolean bankContains(int ID, String name) {
		long timer = Timing.currentTimeMillis() + 10000;
		while (timer > Timing.currentTimeMillis()) {
			status = "Searching for " + name;
			if (Banking.isBankScreenOpen()) {
				if (Banking.find(ID).length > 0) {
					status = "Found " + name;
					return true;
				}
			}
			sleep(500);
		}
		return false;
	}

	private int getHPPercent() {
		double currentHP = Skills.SKILLS.HITPOINTS.getCurrentLevel();
		double totalHP = Skills.SKILLS.HITPOINTS.getActualLevel();
		return (int) (currentHP / totalHP * 100);
	}

	private boolean atHouse() {
		return Objects.find(50, HOUSE_PORTAL).length > 0;
	}

	private void sleepUntilAtAbyss() {
		Timer t = new Timer(3000);

		while (t.isRunning()) {
			status = "Waiting for Abyss";
			if (insideAbyss()) {
				status = "We are at Abyss!";
				break;
			}
			sleep(300, 500);
		}
	}

	private void depositAllExcept(final int... ids) {
		Arrays.sort(ids);
		for (RSItem i : Inventory.getAll()) {
			if (Arrays.binarySearch(ids, i.getID()) < 0) {
				int count = Inventory.getCount(i.getID());
				if (count > 0) {
					Banking.depositItem(i, 0);
					Timer t = new Timer(1000);
					while (t.isRunning()
							&& Inventory.getCount(i.getID()) == count) {
						sleep(200, 250);
					}
				}
			}
		}
	}

	private boolean needBank() {
		if (!hasEssence() && !pouchesFull && !giantPouchFull) {
			// println("1");
			return true;
		} else if (atBank() && !Inventory.isFull()) {
			// println("2");
			return true;
		} else if (atBank()
				&& ((hasPouches() && !pouchesFull) || (hasGiantPouch() && !giantPouchFull))) {
			// println("3");
			return true;
		} else if (teleportTablet && Inventory.getCount(HOUSE_TABLET) == 0) {
			// println("4");
			return true;
		} else if (teleportRegular && !haveRegularRunes()) {
			// println("5");
			return true;
		} else if (teleportDust && !haveDustRunes()) {
			// println("6");
			return true;
		} else if (!mountedGlory && !isEquipped(CHARGED_GLORY)
				&& !inventoryContains(CHARGED_GLORY)) {
			// println("7");
			return true;
		} else if ((hasPouches() && !pouchesFull && !hasEssence())
				&& (!insideAbyssRing() || atAltar())) {
			// println("8");
			return true;
		} else if ((getHPPercent() <= PERCENT_TO_EAT) && atEdgeville()) {
			// println("9");
			return true;
		} else {
			// println("false");
			return false;
		}
	}

	private boolean haveRegularRunes() {
		if (Inventory.getCount(AIR_RUNE) > 0
				&& Inventory.getCount(EARTH_RUNE) > 0
				&& Inventory.getCount(LAW_RUNE) > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean haveDustRunes() {
		if (Inventory.getCount(DUST_RUNE) > 0
				&& Inventory.getCount(LAW_RUNE) > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void openEquipment() {
		if (GameTab.getOpen() != TABS.EQUIPMENT) {
			status = "Opening Equipment";
			GameTab.open(TABS.EQUIPMENT);
		}
	}

	private boolean hasVial() {
		if (inventoryContains(EMPTY_VIAL)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasPotion() {
		if (inventoryContains(STAMINA_POTIONS)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasEssence() {
		if (inventoryContains(PURE_ESSENCE)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasGiantPouch() {
		if (inventoryContains(GIANT_POUCH)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasPouches() {
		if (inventoryContains(POUCHES)) {
			return true;
		} else {
			return false;
		}
	}

	private void unequip(int item, String slot) {
		RSInterfaceChild equipment = Interfaces.get(387, 28);
		RSInterfaceChild necklace = Interfaces.get(387, 14);

		if (GameTab.getOpen() != TABS.EQUIPMENT) {
			openEquipment();
		} else {
			if (equipment != null) {
				RSItem[] items = equipment.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getID() == item) {
						if (slot.equalsIgnoreCase("necklace")
								|| slot.equalsIgnoreCase("amulet")) {
							necklace.hover();
							sleep(250, 300);
							if (necklace.getAbsoluteBounds().contains(
									Mouse.getPos())) {
								status = "Unequipping " + slot;
								necklace.click();
							}
						}
					}
				}
			}
		}
	}

	private boolean wearingUnchargedGlory() {
		if (wearingItem(UNCHARGED_GLORY)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean wearingItem(int id) {
		int[] equipment = getEquipment();
		for (int i = 0; i < equipment.length; i++) {
			if (equipment[i] == id) {
				return true;
			}
		}
		return false;
	}

	private void teleportMountedGlory() {
		if (atHouse()) {
			RSObject[] glory = Objects.findNearest(50, HOUSE_GLORY);
			if (glory != null && glory.length > 0) {
				abc.waitNewOrSwitchDelay(last_busy_time, true);
				Camera.setCameraRotation(mountedGloryRotation);
				Camera.setCameraAngle(50);
				if (!glory[0].isOnScreen()) {
					status = "Moving To Mounted Glory";
					if (!Player.isMoving()) {
						runTo(glory[0].getAnimablePosition(), true);
					}
				} else {
					Timer clicking = new Timer(1000);
					while (clicking.isRunning()
							&& !Game.getUptext().contains("Edgeville")) {
						status = "Clicking Mounted Glory";
						glory[0].hover();
						sleep(100, 200);
					}
					if (Game.getUptext().contains("Edgeville")) {
						Mouse.click(1);
						Timer t = new Timer(1500);
						while (t.isRunning()) {
							if (Player.isMoving()) {
								t.reset();
							}
							sleep(200, 250);
						}
						sleepUntilAtEdgeville();
					}
					last_busy_time = Timing.currentTimeMillis();
				}
			}
		} else {
			status = "Teleporting To House";
			if (Inventory.getCount(HOUSE_TABLET) > 0) {
				useTablet();
			} else if (teleportRegular || teleportDust) {
				castTeleport();
			}
		}
	}

	private void teleportGlory() {
		RSInterfaceComponent necklaceSlot = Interfaces.get(387, 8).getChild(0);

		if (GameTab.getOpen() != TABS.EQUIPMENT) {
			status = "Opening Equipment";
			GameTab.open(TABS.EQUIPMENT);
		} else {
			if (necklaceSlot != null) {
				status = "Rubbing Glory";
				necklaceSlot.click("Edgeville");
				sleepUntilAtEdgeville();
			}
		}
	}

	private void sleepUntilAtEdgeville() {
		Timer t = new Timer(5000);

		setPosition(100, 0);

		while (t.isRunning()) {
			status = "Teleporting To Edgeville";
			if (atEdgeville()) {
				status = "We have arrived";
				break;
			}
			sleep(200, 250);
		}
	}

	private boolean atBank() {
		if (BANK_AREA.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean atEdgeville() {
		if (EDGEVILLE_AREA.contains(Player.getPosition())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isEquipped(int[] id) {
		if (Equipment.isEquipped(CHARGED_GLORY)) {
			return true;
		} else {
			return false;
		}
	}

	private int[] getEquipment() {
		ArrayList<Integer> idList = new ArrayList<Integer>();

		RSInterfaceChild equip = Interfaces.get(387, 28);
		if (equip != null) {
			RSItem[] items = equip.getItems();
			for (RSItem item : items)
				idList.add(item.getID());
		}

		int[] result = new int[idList.size()];
		for (int i = 0; i < idList.size(); i++)
			result[i] = idList.get(i);

		return result;
	}

	private boolean inventoryContains(final int... ids) {
		for (RSItem i : Inventory.getAll()) {
			for (int j = 0; j < ids.length; j++) {
				if (i.getID() == ids[j]) {
					return true;
				}
			}
		}
		return false;
	}

	private void sleepUntilIdle() {
		long t = System.currentTimeMillis();

		while (Timing.timeFromMark(t) < General.random(1500, 2000)) {
			if (Player.isMoving() || Player.getAnimation() != -1) {
				t = System.currentTimeMillis();
			} else {
				break;
			}
			sleep(50, 100);
		}
	}

	private void DTravel(RSTile target) {
		DPathNavigator d = new DPathNavigator();
		ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		tiles = getTiles(Player.getPosition());
		RSTile[] path = null;
		if (!PathFinding.canReach(target, false)) {
			if (tiles.size() > 0) {
				RSTile closest = getClosestTileToTile(target, tiles);
				if (closest != null) {
					status = "Generating Path";
					path = d.findPath(closest);
				}
			}
		} else {
			status = "Generating Path";
			path = d.findPath(target);
		}
		if (path != null && path.length > 0) {
			walkPath(path);
		} else {
			println("No path!");
		}
	}

	private ArrayList<RSTile> getTiles(RSTile pos) {

		status = "Grabbing All Tiles";

		ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		int radius = 30;
		int diameter = (1 + (2 * radius));
		int plane = pos.getPlane();

		int x = pos.getX() - radius;
		int y = pos.getY() + radius;

		for (int i = 0; i < diameter; i++) {
			x = pos.getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile temp = new RSTile(x, y, plane);
				if (PathFinding.canReach(temp, false)) {
					tiles.add(temp);
				}
				x += 1;
			}
			y -= 1;
		}
		return tiles;
	}

	private RSTile getClosestTileToTile(RSTile target, ArrayList<RSTile> tiles) {
		if (tiles.size() > 0) {
			status = "Searching for Closest Tile";
			RSTile closest = tiles.get(0);
			int distance = target.distanceTo(tiles.get(0));
			for (int i = 1; i < tiles.size(); i++) {
				if (target.distanceTo(tiles.get(i)) < distance) {
					closest = tiles.get(i);
					distance = target.distanceTo(tiles.get(i));
				}
			}
			return closest;
		}
		return null;
	}

	private void walkPath(RSTile[] path) {
		RSTile pos = Player.getPosition();
		if (pos != null) {
			status = "Navigating Path";
			Timer walking = new Timer(3000);
			while (!(path[path.length - 1].distanceTo(pos) <= 2)
					&& walking.isRunning()) {
				RSTile tile = null;
				for (int i = path.length - 1; i >= 0; i--) {
					if (isTileOnMinimap(path[i])) {
						tile = path[i];
						break;
					}
				}
				if (tile != null) {
					walking.reset();
					Walking.clickTileMM(tile, 1);
					if (tile.equals(path[path.length - 1])) {
						status = "At End Of Path";
						break;
					}
					Timer moving = new Timer(1000);
					while (moving.isRunning()) {
						if (Player.isMoving()) {
							moving.reset();
						}
						if (Player.getPosition().distanceTo(tile) <= 7) {
							break;
						}
						sleep(50, 100);
					}
				}
			}
		}
	}

	private void runTo(RSTile target, boolean screenWalk) {
		if (!isTileOnMinimap(target)) {
			status = "DTravel";
			DTravel(target);
		} else if (!screenWalk || !target.isOnScreen()) {
			status = "Minimap Walk";
			Walking.walkTo(target);
		} else if (target.isOnScreen() && screenWalk) {
			status = "Screen Walk";
			Point p = Projection.tileToScreen(target, 0);
			Mouse.click(p, 1);
		}
	}

	private class Timer {

		private long end;
		private final long start;
		private final long period;

		public Timer(final long period) {
			this.period = period;
			start = System.currentTimeMillis();
			end = start + period;
		}

		public Timer(final long period, long addition) {
			this.period = period;
			start = System.currentTimeMillis() + addition;
			end = start + period;
		}

		public long getElapsed() {
			return System.currentTimeMillis() - start;
		}

		public long getRemaining() {
			if (isRunning()) {
				return end - System.currentTimeMillis();
			}
			return 0;
		}

		public boolean isRunning() {
			return System.currentTimeMillis() < end;
		}

		public void reset() {
			end = System.currentTimeMillis() + period;
		}

		public long setEndIn(final long ms) {
			end = System.currentTimeMillis() + ms;
			return end;
		}

		public String toElapsedString() {
			return format(getElapsed());
		}

		public String toRemainingString() {
			return format(getRemaining());
		}

		public String format(final long time) {
			final StringBuilder t = new StringBuilder();
			final long total_secs = time / 1000;
			final long total_mins = total_secs / 60;
			final long total_hrs = total_mins / 60;
			final int secs = (int) total_secs % 60;
			final int mins = (int) total_mins % 60;
			final int hrs = (int) total_hrs % 60;
			if (hrs < 10) {
				t.append("0");
			}
			t.append(hrs);
			t.append(":");
			if (mins < 10) {
				t.append("0");
			}
			t.append(mins);
			t.append(":");
			if (secs < 10) {
				t.append("0");
			}
			t.append(secs);
			return t.toString();
		}
	}

	/**
	 * @author Jewtage
	 */

	private class RSArea {
		private final Polygon area;
		private final int plane;

		public RSArea(final RSTile[] tiles, final int plane) {
			area = tilesToPolygon(tiles);
			this.plane = plane;
		}

		public RSArea(final RSTile[] tiles) {
			this(tiles, 0);
		}

		public RSArea(final RSTile southwest, final RSTile northeast) {
			this(southwest, northeast, 0);
		}

		public RSArea(final int swX, final int swY, final int neX, final int neY) {
			this(new RSTile(swX, swY), new RSTile(neX, neY), 0);
		}

		public RSArea(final int swX, final int swY, final int neX,
				final int neY, final int plane) {
			this(new RSTile(swX, swY), new RSTile(neX, neY), plane);
		}

		public RSArea(final RSTile southwest, final RSTile northeast,
				final int plane) {
			this(new RSTile[] { southwest,
					new RSTile(northeast.getX() + 1, southwest.getY()),
					new RSTile(northeast.getX() + 1, northeast.getY() + 1),
					new RSTile(southwest.getX(), northeast.getY() + 1) }, plane);
		}

		public boolean contains(final RSTile... tiles) {
			final RSTile[] areaTiles = getTiles();
			for (final RSTile check : tiles) {
				for (final RSTile space : areaTiles) {
					if (check.equals(space)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean contains(final int x, final int y) {
			return this.contains(new RSTile(x, y));
		}

		public boolean contains(final int plane, final RSTile... tiles) {
			return this.plane == plane && this.contains(tiles);
		}

		public Rectangle getDimensions() {
			return new Rectangle(area.getBounds().x + 1,
					area.getBounds().y + 1, getWidth(), getHeight());
		}

		public RSTile getNearestTile(final RSTile base) {
			RSTile tempTile = null;
			for (final RSTile tile : getTiles()) {
				if (tempTile == null
						|| distanceBetween(base, tile) < distanceBetween(
								tempTile, tile)) {
					tempTile = tile;
				}
			}
			return tempTile;
		}

		public int getPlane() {
			return plane;
		}

		public Polygon getPolygon() {
			return area;
		}

		public RSTile[] getTiles() {
			ArrayList<RSTile> tiles = new ArrayList<RSTile>();
			for (int x = getX(); x <= getX() + getWidth(); x++) {
				for (int y = getY(); y <= getY() + getHeight(); y++) {
					if (area.contains(x, y)) {
						tiles.add(new RSTile(x, y));
					}
				}
			}
			return tiles.toArray(new RSTile[tiles.size()]);
		}

		public int getWidth() {
			return area.getBounds().width;
		}

		public int getHeight() {
			return area.getBounds().height;
		}

		public int getX() {
			return area.getBounds().x;
		}

		public int getY() {
			return area.getBounds().y;
		}

		public Polygon tilesToPolygon(final RSTile[] tiles) {
			final Polygon polygon = new Polygon();
			for (final RSTile t : tiles) {
				polygon.addPoint(t.getX(), t.getY());
			}
			return polygon;
		}

		public double distanceBetween(RSTile curr, RSTile dest) {
			return Math.sqrt((curr.getX() - dest.getX())
					* (curr.getX() - dest.getX()) + (curr.getY() - dest.getY())
					* (curr.getY() - dest.getY()));
		}
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
		return days + ", " + hours + ", " + minutes + ", " + seconds;
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

	public void onPaint(Graphics g) {

		long time = System.currentTimeMillis() - startTime;

		int levelsGained = Skills.SKILLS.RUNECRAFTING.getActualLevel()
				- startRunecraftingLevel;
		int runecraftingXPGained = Skills.SKILLS.RUNECRAFTING.getXP()
				- startRunecraftingXP;
		int xpPerHour = (int) (runecraftingXPGained * 3600000D / (System
				.currentTimeMillis() - startTime));
		int runesPerHour = (int) (runes * 3600000D / (System
				.currentTimeMillis() - startTime));
		int tripsPerHour = (int) (trips * 3600000D / (System
				.currentTimeMillis() - startTime));
		int profit = (runes * rune_value) - cost;
		int profitPerHour = Math.round(((int) (profit * 3600000D / (System
				.currentTimeMillis() - startTime))) / 1000);

		Font small = new Font("Verdana", 0, 10);
		Font font = new Font("Verdana", 0, 12);
		g.setFont(font);

		if (display) {
			g.drawImage(open, 7, 275, null); // draw open image
			g.setColor(Color.RED);
			g.fillRect(15, 452, 201, 15); // red background for progress bar
			int percentToNextLevel = Skills
					.getPercentToNextLevel(Skills.SKILLS.RUNECRAFTING);
			Color green = new Color(0, 140, 0);
			g.setColor(green);
			g.fillRect(16, 453, (percentToNextLevel * 2), 14); // green filled
																// rectangle for
																// progress
																// display
			g.setColor(Color.WHITE);
			g.drawRect(15, 452, 201, 15); // white border for progress bar
			g.setFont(small);
			g.drawString("v" + version, 485, 336);
			g.setFont(font);
			int x = 298;
			int y = 357;
			g.drawString("" + passage, x, y);
			y += 15;
			g.drawString("" + rocks, x, y);
			y += 15;
			g.drawString("" + squeeze, x, y);
			y += 15;
			g.drawString("" + eyes, x, y);
			y += 15;
			g.drawString("" + boil, x, y);
			y += 15;
			g.drawString("" + tendrils, x, y);
			y += 15;

			x = 478;
			y = 416;
			g.drawString("" + emergencyTeleport, x, y);
			y += 18;
			g.drawString("" + pouchesRepaired, x, y);
			y += 18;
			g.drawString("" + attemptedPlayerKill, x, y);
			y += 18;
			g.drawString("" + foodUsed, x, y);
			y += 18;

			g.drawString("" + status, 61, 365);
			g.drawString("" + parseTime((time), false) + "", 56, 384);
			g.drawString("" + trips + " (" + tripsPerHour + "/hr)", 50, 404);
			g.drawString("" + runecraftingXPGained + " (" + xpPerHour + "/hr)",
					100, 426);
			g.drawString(
					"" + levelsGained + " (Current: "
							+ Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING)
							+ ")", 124, 445);
			StringBuilder builder1 = new StringBuilder();
			builder1.append("");
			builder1.append(Skills.getXPToNextLevel(Skills.SKILLS.RUNECRAFTING));
			String xpToNextLevel = builder1.toString();
			g.drawString(addCommasToNumericString(xpToNextLevel) + " XP to "
					+ (Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING) + 1),
					65, 464);

			x = 394;
			y = 366;
			g.drawString("" + runes + " (" + runesPerHour + "/hr)", x, y);
			y += 24;
			if (profit > 0) {
				StringBuilder builder2 = new StringBuilder();
				builder2.append("");
				builder2.append(profit);
				String profitString = builder2.toString();
				g.drawString("" + addCommasToNumericString(profitString) + " ("
						+ profitPerHour + "K/hr)", x, y);
			} else {
				g.drawString("No profit yet!", x, y);
			}

		} else {
			g.drawImage(closed, 7, 275, null); // draw closed image
		}
		if (avoidedPlayers.size() > 0) {
			g.setFont(small);
			int x = 5;
			int y = 15;
			g.setColor(Color.WHITE);
			g.drawString("Avoided Players: ", x, y);
			for (int i = 0; i < avoidedPlayers.size(); i++) {
				y += 12;
				g.drawString(i + 1 + ") " + avoidedPlayers.get(i), x, y);
			}
		}
		if (paintObstacles && !insideAbyssRing()) {
			if (obstacles != null && obstacles.size() > 0) {
				if (obstacles.get(0).isOnScreen()) {
					if (obstacles.get(0).getModel().getPoints().length == rocksPoints) {
						g.setColor(Color.BLUE);
					} else if (obstacles.get(0).getModel().getPoints().length == eyesPoints) {
						g.setColor(Color.MAGENTA);
					} else if (obstacles.get(0).getModel().getPoints().length == tendrilsPoints) {
						g.setColor(Color.GREEN);
					} else if (obstacles.get(0).getModel().getPoints().length == gapPoints) {
						g.setColor(Color.YELLOW);
					} else if (obstacles.get(0).getModel().getPoints().length == boilPoints) {
						g.setColor(Color.RED);
					} else if (obstacles.get(0).getModel().getPoints().length == passagePoints) {
						g.setColor(Color.PINK);
					} else if (obstacles.get(0).getModel().getPoints().length == blockagePoints) {
						g.setColor(Color.WHITE);
					}
					Polygon[] triangles = obstacles.get(0).getModel()
							.getTriangles();
					for (int i = 0; i < triangles.length; i++) {
						g.drawPolygon(triangles[i]);
					}
				}
			}
		}
	}

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	private class gui extends JFrame {

		private JPanel contentPane;
		private JTextField food_id;
		private JTextField heal_amount;
		private final Action action = new SwingAction();
		private JTextField essenceCost;
		private JTextField foodCost;
		private JTextField teleportCost;
		private JTextField runeCost;
		private JTextField potionCost;
		private JCheckBox squeeze;
		private JCheckBox mine;
		private JCheckBox distract;
		private JCheckBox chop;
		private JCheckBox burn;
		private JComboBox altarOption;
		private JSpinner heal_percent;
		private JComboBox houseTeleportOption;
		private JSpinner emergency_percentage;
		private JComboBox orientationOption;
		private JComboBox bankTeleportOption;
		private JCheckBox chckbxPaintObstacles;
		private JCheckBox usePotions;
		private JTextArea weaponsGUI;
		private JSlider slider;

		public gui() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 647, 459);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblUsaAbyssRunecrafter = new JLabel("USA Abyss Runecrafter");
			lblUsaAbyssRunecrafter
					.setHorizontalAlignment(SwingConstants.CENTER);
			lblUsaAbyssRunecrafter.setForeground(Color.RED);
			lblUsaAbyssRunecrafter.setBackground(Color.WHITE);
			lblUsaAbyssRunecrafter.setFont(new Font("Verdana", Font.BOLD, 22));
			lblUsaAbyssRunecrafter.setBounds(72, 16, 375, 35);
			contentPane.add(lblUsaAbyssRunecrafter);

			JLabel lblWwwtribotorg = new JLabel("www.tribot.org");
			lblWwwtribotorg.setFont(new Font("Verdana", Font.PLAIN, 10));
			lblWwwtribotorg.setBounds(5, 42, 87, 14);
			contentPane.add(lblWwwtribotorg);

			JLabel lblObstacles = new JLabel("Obstacles");
			lblObstacles.setFont(new Font("Verdana", Font.PLAIN, 14));
			lblObstacles.setBounds(325, 76, 68, 20);
			contentPane.add(lblObstacles);

			squeeze = new JCheckBox("Squeeze-through Gap");
			squeeze.setSelected(true);
			squeeze.setBounds(296, 100, 151, 23);
			contentPane.add(squeeze);

			mine = new JCheckBox("Mine Rocks");
			mine.setSelected(true);
			mine.setBounds(296, 126, 138, 23);
			contentPane.add(mine);

			distract = new JCheckBox("Distract Eyes");
			distract.setSelected(true);
			distract.setBounds(296, 152, 138, 23);
			contentPane.add(distract);

			chop = new JCheckBox("Chop Tendrils");
			chop.setSelected(false);
			chop.setBounds(296, 178, 138, 23);
			contentPane.add(chop);

			burn = new JCheckBox("Burn-down Boil");
			burn.setSelected(false);
			burn.setBounds(296, 204, 138, 23);
			contentPane.add(burn);

			JSeparator obstacles_seperator_1 = new JSeparator();
			obstacles_seperator_1.setBounds(288, 95, 170, 2);
			contentPane.add(obstacles_seperator_1);

			JSeparator obstacles_seperator_2 = new JSeparator();
			obstacles_seperator_2.setOrientation(SwingConstants.VERTICAL);
			obstacles_seperator_2.setBounds(288, 95, 2, 137);
			contentPane.add(obstacles_seperator_2);

			JSeparator obstacles_seperator_3 = new JSeparator();
			obstacles_seperator_3.setBounds(289, 232, 170, 2);
			contentPane.add(obstacles_seperator_3);

			altarOption = new JComboBox();
			altarOption.setBounds(98, 88, 172, 20);
			altarOption.setModel(new DefaultComboBoxModel(new String[] {
					"Nature Altar", "Law Altar", "Death Altar", "Cosmic Altar",
					"Chaos Altar", "Body Altar", "Fire Altar", "Earth Altar",
					"Water Altar", "Mind Altar", "Air Altar" }));
			altarOption.setSelectedIndex(0);
			contentPane.add(altarOption);

			JLabel lblChooseAltar = new JLabel("Choose Altar:");
			lblChooseAltar.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblChooseAltar.setBounds(5, 90, 87, 17);
			contentPane.add(lblChooseAltar);

			JLabel lblFood = new JLabel("Food");
			lblFood.setFont(new Font("Verdana", Font.PLAIN, 14));
			lblFood.setBounds(325, 250, 68, 20);
			contentPane.add(lblFood);

			JSeparator food_seperator_1 = new JSeparator();
			food_seperator_1.setBounds(288, 269, 170, 2);
			contentPane.add(food_seperator_1);

			JSeparator food_seperator_2 = new JSeparator();
			food_seperator_2.setBounds(288, 349, 170, 2);
			contentPane.add(food_seperator_2);

			JSeparator food_seperator_3 = new JSeparator();
			food_seperator_3.setOrientation(SwingConstants.VERTICAL);
			food_seperator_3.setBounds(288, 269, 2, 79);
			contentPane.add(food_seperator_3);

			JLabel lblFoodId = new JLabel("Food ID:");
			lblFoodId.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblFoodId.setBounds(296, 276, 64, 14);
			contentPane.add(lblFoodId);

			JLabel lblHealAmount = new JLabel("Heal Amount:");
			lblHealAmount.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblHealAmount.setBounds(296, 301, 93, 14);
			contentPane.add(lblHealAmount);

			JLabel lblHealthTo = new JLabel("Health % to Heal:");
			lblHealthTo.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblHealthTo.setBounds(296, 326, 112, 12);
			contentPane.add(lblHealthTo);

			food_id = new JTextField();
			food_id.setText("379");
			food_id.setBounds(354, 275, 93, 18);
			contentPane.add(food_id);
			food_id.setColumns(10);

			heal_amount = new JTextField();
			heal_amount.setText("12");
			heal_amount.setColumns(10);
			heal_amount.setBounds(384, 299, 63, 18);
			contentPane.add(heal_amount);

			heal_percent = new JSpinner();
			heal_percent.setModel(new SpinnerNumberModel(70, 1, 99, 3));
			heal_percent.setBounds(412, 324, 35, 18);
			contentPane.add(heal_percent);

			JLabel lblGloryTeleport = new JLabel("To Edge:");
			lblGloryTeleport.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblGloryTeleport.setBounds(5, 120, 64, 17);
			contentPane.add(lblGloryTeleport);

			houseTeleportOption = new JComboBox();
			houseTeleportOption.setModel(new DefaultComboBoxModel(new String[] {
					"Mounted Glory", "Amulet of Glory (4)" }));
			houseTeleportOption.setSelectedIndex(0);
			houseTeleportOption.setBounds(67, 119, 130, 20);
			contentPane.add(houseTeleportOption);

			usePotions = new JCheckBox(" Use Stamina Potions?");
			usePotions.setFont(new Font("Verdana", Font.PLAIN, 12));
			usePotions.setBounds(2, 174, 268, 20);
			contentPane.add(usePotions);

			JSeparator separator = new JSeparator();
			separator.setBounds(0, 60, 460, 2);
			contentPane.add(separator);

			JSeparator separator_1 = new JSeparator();
			separator_1.setOrientation(SwingConstants.VERTICAL);
			separator_1.setBounds(276, 60, 2, 317);
			contentPane.add(separator_1);

			JSeparator separator_2 = new JSeparator();
			separator_2.setBounds(0, 376, 633, 2);
			contentPane.add(separator_2);

			JButton btnStart = new JButton("Start");
			btnStart.setAction(action);
			btnStart.setBounds(105, 387, 430, 23);
			contentPane.add(btnStart);

			emergency_percentage = new JSpinner();
			emergency_percentage.setModel(new SpinnerNumberModel(30, 1, 90, 3));
			emergency_percentage.setBounds(233, 198, 37, 18);
			contentPane.add(emergency_percentage);

			orientationOption = new JComboBox();
			orientationOption.setModel(new DefaultComboBoxModel(new String[] {
					"N", "S", "E", "W" }));
			orientationOption.setSelectedIndex(2);
			orientationOption.setBounds(239, 119, 35, 20);
			contentPane.add(orientationOption);

			JLabel lblOrientation = new JLabel("Face:");
			lblOrientation.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblOrientation.setBounds(201, 120, 35, 14);
			contentPane.add(lblOrientation);

			JLabel v = new JLabel("v" + version);
			v.setBounds(420, 46, 30, 14);
			contentPane.add(v);

			essenceCost = new JTextField();
			essenceCost.setText("30");
			essenceCost.setBounds(240, 304, 32, 18);
			contentPane.add(essenceCost);
			essenceCost.setColumns(10);

			foodCost = new JTextField();
			foodCost.setText("200");
			foodCost.setBounds(240, 320, 32, 18);
			contentPane.add(foodCost);
			foodCost.setColumns(10);

			JLabel lblRunePrice = new JLabel("Rune:");
			lblRunePrice.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblRunePrice.setBounds(195, 290, 38, 14);
			contentPane.add(lblRunePrice);

			JLabel lblPureEssencePrice = new JLabel("Essence:");
			lblPureEssencePrice.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblPureEssencePrice.setBounds(176, 306, 57, 14);
			contentPane.add(lblPureEssencePrice);

			teleportCost = new JTextField();
			teleportCost.setText("250");
			teleportCost.setColumns(10);
			teleportCost.setBounds(240, 336, 32, 18);
			contentPane.add(teleportCost);

			potionCost = new JTextField();
			potionCost.setText("1250");
			potionCost.setColumns(10);
			potionCost.setBounds(240, 352, 32, 18);
			contentPane.add(potionCost);

			JLabel lblEnergyPotionCost = new JLabel("Potion (1):");
			lblEnergyPotionCost.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblEnergyPotionCost.setBounds(167, 354, 68, 14);
			contentPane.add(lblEnergyPotionCost);

			JLabel lblFoodCost = new JLabel("Food:");
			lblFoodCost.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblFoodCost.setBounds(198, 322, 36, 14);
			contentPane.add(lblFoodCost);

			JLabel lblTeleportCost = new JLabel("Teleport:");
			lblTeleportCost.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblTeleportCost.setBounds(177, 338, 57, 14);
			contentPane.add(lblTeleportCost);

			runeCost = new JTextField();
			runeCost.setText("275");
			runeCost.setColumns(10);
			runeCost.setBounds(240, 288, 32, 18);
			contentPane.add(runeCost);

			JSeparator separator_3 = new JSeparator();
			separator_3.setBounds(155, 283, 121, 2);
			contentPane.add(separator_3);

			JSeparator separator_4 = new JSeparator();
			separator_4.setOrientation(SwingConstants.VERTICAL);
			separator_4.setBounds(155, 284, 2, 93);
			contentPane.add(separator_4);

			JLabel lblToBank = new JLabel("Teleport:");
			lblToBank.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblToBank.setBounds(5, 150, 64, 14);
			contentPane.add(lblToBank);

			bankTeleportOption = new JComboBox();
			bankTeleportOption.setModel(new DefaultComboBoxModel(new String[] {
					"House tablet", "Teleport to House (Regular runes)",
					"Teleport to House (Dust runes)" }));
			bankTeleportOption.setSelectedIndex(0);
			bankTeleportOption.setBounds(67, 148, 203, 20);
			contentPane.add(bankTeleportOption);

			slider = new JSlider();
			slider.setBorder(null);
			slider.setToolTipText("Mouse Speed");
			slider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
			slider.setValue(105);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setMajorTickSpacing(20);
			slider.setMinorTickSpacing(5);
			slider.setMinimum(105);
			slider.setMaximum(185);
			slider.setBounds(5, 239, 265, 38);
			contentPane.add(slider);

			JLabel lblMouseSpeed = new JLabel("Mouse Speed");
			lblMouseSpeed.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblMouseSpeed.setHorizontalAlignment(SwingConstants.CENTER);
			lblMouseSpeed.setBounds(5, 223, 265, 14);
			contentPane.add(lblMouseSpeed);

			chckbxPaintObstacles = new JCheckBox("Paint Obstacles");
			chckbxPaintObstacles.setFont(new Font("Verdana", Font.PLAIN, 12));
			chckbxPaintObstacles.setBounds(5, 350, 123, 23);
			chckbxPaintObstacles.setSelected(true);
			contentPane.add(chckbxPaintObstacles);

			JLabel lblEmergencyTeleportBelow = new JLabel(
					"Emergency Teleport below HP %:");
			lblEmergencyTeleportBelow.setForeground(Color.RED);
			lblEmergencyTeleportBelow.setBackground(Color.WHITE);
			lblEmergencyTeleportBelow
					.setFont(new Font("Verdana", Font.BOLD, 12));
			lblEmergencyTeleportBelow.setBounds(5, 198, 223, 20);
			contentPane.add(lblEmergencyTeleportBelow);

			JSeparator separator_5 = new JSeparator();
			separator_5.setOrientation(SwingConstants.VERTICAL);
			separator_5.setBounds(459, 60, 2, 317);
			contentPane.add(separator_5);

			JLabel lblAvoidPlayers = new JLabel("Evade Players in");
			lblAvoidPlayers.setFont(new Font("Verdana", Font.BOLD, 12));
			lblAvoidPlayers.setHorizontalAlignment(SwingConstants.CENTER);
			lblAvoidPlayers.setBounds(468, 76, 154, 23);
			contentPane.add(lblAvoidPlayers);

			JLabel lblcaseInsensitive = new JLabel("1. Case Insensitive");
			lblcaseInsensitive.setFont(new Font("Tahoma", Font.ITALIC, 11));
			lblcaseInsensitive.setBounds(470, 301, 149, 14);
			contentPane.add(lblcaseInsensitive);

			JLabel lblpartialPhrasesAllowed = new JLabel(
					"2. Partial Phrases accepted");
			lblpartialPhrasesAllowed
					.setFont(new Font("Tahoma", Font.ITALIC, 11));
			lblpartialPhrasesAllowed.setBounds(470, 315, 151, 14);
			contentPane.add(lblpartialPhrasesAllowed);

			JLabel lblWildernessWearing = new JLabel("Wilderness wearing...");
			lblWildernessWearing.setHorizontalAlignment(SwingConstants.CENTER);
			lblWildernessWearing.setFont(new Font("Verdana", Font.BOLD, 12));
			lblWildernessWearing.setBounds(471, 100, 151, 14);
			contentPane.add(lblWildernessWearing);

			JSeparator separator_6 = new JSeparator();
			separator_6.setBounds(459, 60, 174, 2);
			contentPane.add(separator_6);

			JLabel lblExactSpelling = new JLabel("3. Exact spelling required");
			lblExactSpelling.setFont(new Font("Tahoma", Font.ITALIC, 11));
			lblExactSpelling.setBounds(470, 328, 152, 14);
			contentPane.add(lblExactSpelling);

			weaponsGUI = new JTextArea();
			weaponsGUI.setFont(new Font("Verdana", Font.PLAIN, 12));
			weaponsGUI
					.setText("abyssal\r\nwand\r\nghostly\r\ndragon\r\nancient");
			weaponsGUI.setRows(10);
			weaponsGUI.setBounds(471, 125, 151, 164);
			contentPane.add(weaponsGUI);
		}

		private class SwingAction extends AbstractAction {
			public SwingAction() {
				putValue(NAME, "Start");
				putValue(SHORT_DESCRIPTION, "Some short description");
			}

			public void actionPerformed(ActionEvent e) {

				if (altarOption.getSelectedIndex() == 0) {
					RIFT_TILE = NATURE_RIFT_TILE;
					RUNE_TYPE = NATURE_RUNE;
					RUNE_NAME = NATURE_NAME;
					ALTAR_AREA = NATURE_ALTAR;
				} else if (altarOption.getSelectedIndex() == 1) {
					RIFT_TILE = LAW_RIFT_TILE;
					RUNE_TYPE = LAW_RUNE;
					RUNE_NAME = LAW_NAME;
					ALTAR_AREA = LAW_ALTAR;
				} else if (altarOption.getSelectedIndex() == 2) {
					RIFT_TILE = DEATH_RIFT_TILE;
					RUNE_TYPE = DEATH_RUNE;
					RUNE_NAME = DEATH_NAME;
					ALTAR_AREA = DEATH_ALTAR;
				} else if (altarOption.getSelectedIndex() == 3) {
					RIFT_TILE = COSMIC_RIFT_TILE;
					RUNE_TYPE = COSMIC_RUNE;
					RUNE_NAME = COSMIC_NAME;
					ALTAR_AREA = COSMIC_ALTAR;
				} else if (altarOption.getSelectedIndex() == 4) {
					RIFT_TILE = CHAOS_RIFT_TILE;
					RUNE_TYPE = CHAOS_RUNE;
					RUNE_NAME = CHAOS_NAME;
					ALTAR_AREA = CHAOS_ALTAR;
				} else if (altarOption.getSelectedIndex() == 5) {
					RIFT_TILE = BODY_RIFT_TILE;
					RUNE_TYPE = BODY_RUNE;
					RUNE_NAME = BODY_NAME;
					ALTAR_AREA = BODY_ALTAR;
				} else if (altarOption.getSelectedIndex() == 6) {
					RIFT_TILE = FIRE_RIFT_TILE;
					RUNE_TYPE = FIRE_RUNE;
					RUNE_NAME = FIRE_NAME;
					ALTAR_AREA = FIRE_ALTAR;
				} else if (altarOption.getSelectedIndex() == 7) {
					RIFT_TILE = EARTH_RIFT_TILE;
					RUNE_TYPE = EARTH_RUNE;
					RUNE_NAME = EARTH_NAME;
					ALTAR_AREA = EARTH_ALTAR;
				} else if (altarOption.getSelectedIndex() == 8) {
					RIFT_TILE = WATER_RIFT_TILE;
					RUNE_TYPE = WATER_RUNE;
					RUNE_NAME = WATER_NAME;
					ALTAR_AREA = WATER_ALTAR;
				} else if (altarOption.getSelectedIndex() == 9) {
					RIFT_TILE = MIND_RIFT_TILE;
					RUNE_TYPE = MIND_RUNE;
					RUNE_NAME = MIND_NAME;
					ALTAR_AREA = MIND_ALTAR;
				} else if (altarOption.getSelectedIndex() == 10) {
					RIFT_TILE = AIR_RIFT_TILE;
					RUNE_TYPE = AIR_RUNE;
					RUNE_NAME = AIR_NAME;
					ALTAR_AREA = AIR_ALTAR;
				}

				if (bankTeleportOption.getSelectedIndex() == 0) {
					teleportTablet = true;
					println("We are using house teleport tablets!");
				} else if (bankTeleportOption.getSelectedIndex() == 1) {
					teleportRegular = true;
					println("We are using regular runes to teleport to the house!");
				} else if (bankTeleportOption.getSelectedIndex() == 2) {
					teleportDust = true;
					println("We are using dust runes to teleport to the house!");
				}

				if (houseTeleportOption.getSelectedIndex() == 0) {
					if (orientationOption.getSelectedIndex() == 0) {
						println("Using Mounted Glory in House, facing North!");
						mountedGloryRotation = 0;
					} else if (orientationOption.getSelectedIndex() == 1) {
						println("Using Mounted Glory in House, facing South!");
						mountedGloryRotation = 180;
					} else if (orientationOption.getSelectedIndex() == 2) {
						println("Using Mounted Glory in House, facing East!");
						mountedGloryRotation = 270;
					} else if (orientationOption.getSelectedIndex() == 3) {
						println("Using Mounted Glory in House, facing West!");
						mountedGloryRotation = 90;
					}
					mountedGlory = true;
				} else {
					println("Using Amulet of Glory!");
					mountedGlory = false;
				}

				FOOD = Integer.parseInt(food_id.getText());
				HEAL_AMOUNT = Integer.parseInt(heal_amount.getText());
				PERCENT_TO_EAT = (int) heal_percent.getValue();
				RANDOMIZED_HEAL = PERCENT_TO_EAT;
				println("Food ID: " + FOOD);
				println("Heal Amount: " + HEAL_AMOUNT);
				println("Healing in bank below " + PERCENT_TO_EAT + "%");

				if (squeeze.isSelected()) {
					println("We are using the agility obstacle!");
					SQUEEZE_OBSTACLE = true;
				} else {
					println("We are NOT using the agility obstacle!");
					SQUEEZE_OBSTACLE = false;
				}
				if (mine.isSelected()) {
					println("We are using the mining obstacle!");
					ROCK_OBSTACLE = true;
				} else {
					println("We are NOT using the mining obstacle!");
					ROCK_OBSTACLE = false;
				}
				if (distract.isSelected()) {
					println("We are using the thieving obstacle!");
					EYES_OBSTACLE = true;
				} else {
					println("We are NOT using the thieving obstacle!");
					EYES_OBSTACLE = false;
				}
				if (chop.isSelected()) {
					println("We are using the woodcutting obstacle!");
					TENDRILS_OBSTACLE = true;
				} else {
					println("We are NOT using the woodcutting obstacle!");
					TENDRILS_OBSTACLE = false;
				}
				if (burn.isSelected()) {
					println("We are using the firemaking obstacle!");
					BOIL_OBSTACLE = true;
				} else {
					println("We are NOT using the firemaking obstacle!");
					BOIL_OBSTACLE = false;
				}

				rune_value = Integer.parseInt(runeCost.getText());
				essence_cost = Integer.parseInt(essenceCost.getText());
				teleport_cost = Integer.parseInt(teleportCost.getText());
				food_cost = Integer.parseInt(foodCost.getText());
				potion_cost = Integer.parseInt(potionCost.getText());

				println("We are looking at a profit of "
						+ (rune_value - essence_cost) + " per rune!");

				if (chckbxPaintObstacles.isSelected()) {
					paintObstacles = true;
				} else {
					paintObstacles = false;
				}

				EMERGENCY_PERCENT = (int) emergency_percentage.getValue();
				println("Emergency Teleporting if below " + EMERGENCY_PERCENT
						+ "% health!");

				if (usePotions.isSelected()) {
					potions = true;
					println("We are using Super Energy potions (3) or (4)!");
				} else {
					potions = false;
					println("We are NOT using any Super Energy potions");
				}

				Element paragraph = weaponsGUI.getDocument()
						.getDefaultRootElement();
				int count = paragraph.getElementCount();
				for (int i = 0; i < count; i++) {
					Element ele = paragraph.getElement(i);
					int rangeStart = ele.getStartOffset();
					int rangeEnd = ele.getEndOffset();
					String line = null;
					try {
						line = weaponsGUI.getText(rangeStart, rangeEnd
								- rangeStart);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (line.length() > 0) {
						line = line.toLowerCase();
						line = line.trim();
						weapons.add(line);
					}
				}

				for (int i = 0; i < weapons.size(); i++) {
					println("Avoding players wearing \"" + weapons.get(i)
							+ "\"");
				}

				mouseSpeed = slider.getValue();

				println("Mouse Speed set to " + mouseSpeed);

				gui_is_up = false;
				g.dispose();
			}
		}
	}

	@Override
	public void mouseClicked(Point point, int arg1, boolean isBot) {
		// TODO Auto-generated method stub
		Rectangle rect = new Rectangle(445, 345, 50, 20);
		if (rect.contains(point) && !isBot) {
			display = !display;
		}
	}

	@Override
	public void mouseDragged(Point arg0, int arg1, boolean arg2) {

	}

	@Override
	public void mouseMoved(Point arg0, boolean arg1) {

	}

	@Override
	public void mouseReleased(Point arg0, int arg1, boolean arg2) {

	}

}