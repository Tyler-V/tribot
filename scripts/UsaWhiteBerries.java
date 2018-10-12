package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.colour.ColourPoint;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GroundItems;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
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
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSNPCDefinition;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA White Berries")
public class UsaWhiteBerries extends Script implements Painting, Ending {

	String version = "1.3";

	private RSArea LUMBRIDGE = new RSArea(new RSTile(3203, 3206), new RSTile(
			3225, 3235));

	private RSArea MAGE_ARENA_BANK = new RSArea(new RSTile[] {
			new RSTile(2527, 4711, 0), new RSTile(2540, 4708, 0),
			new RSTile(2547, 4709, 0), new RSTile(2549, 4717, 0),
			new RSTile(2547, 4726, 0), new RSTile(2529, 4724, 0) });

	private RSArea MAGE_ARENA_ENTRANCE = new RSArea(new RSTile[] {
			new RSTile(3090, 3959, 0), new RSTile(3090, 3954, 0),
			new RSTile(3092, 3954, 0), new RSTile(3092, 3956, 0),
			new RSTile(3097, 3956, 0), new RSTile(3097, 3959, 0) });

	private RSArea NORTH_OF_GATE = new RSArea(new RSTile(3065, 3904, 0),
			new RSTile(3282, 3980, 0));

	private RSArea SOUTH_OF_GATE = new RSArea(new RSTile(3090, 3700, 0),
			new RSTile(3282, 3903, 0));

	private RSTile[] GATE_TO_MAGE_ARENA = new RSTile[] {
			new RSTile(3224, 3905, 0), new RSTile(3217, 3906, 0),
			new RSTile(3211, 3906, 0), new RSTile(3206, 3906, 0),
			new RSTile(3198, 3906, 0), new RSTile(3194, 3906, 0),
			new RSTile(3191, 3906, 0), new RSTile(3187, 3906, 0),
			new RSTile(3183, 3906, 0), new RSTile(3180, 3906, 0),
			new RSTile(3176, 3906, 0), new RSTile(3172, 3906, 0),
			new RSTile(3166, 3906, 0), new RSTile(3160, 3906, 0),
			new RSTile(3154, 3906, 0), new RSTile(3151, 3906, 0),
			new RSTile(3148, 3906, 0), new RSTile(3144, 3906, 0),
			new RSTile(3140, 3907, 0), new RSTile(3135, 3909, 0),
			new RSTile(3134, 3914, 0), new RSTile(3132, 3919, 0),
			new RSTile(3131, 3923, 0), new RSTile(3131, 3927, 0),
			new RSTile(3131, 3933, 0), new RSTile(3131, 3939, 0),
			new RSTile(3131, 3943, 0), new RSTile(3127, 3949, 0),
			new RSTile(3120, 3954, 0), new RSTile(3116, 3957, 0),
			new RSTile(3112, 3958, 0), new RSTile(3109, 3960, 0),
			new RSTile(3106, 3960, 0), new RSTile(3100, 3959, 0),
			new RSTile(3098, 3957, 0), new RSTile(3095, 3957, 0) };

	private RSTile[] MAGE_ARENA_TO_GATE = Walking
			.invertPath(GATE_TO_MAGE_ARENA);

	private RSTile[] WHITE_BERRIES_TO_GATE = new RSTile[] {
			new RSTile(3219, 3806, 0), new RSTile(3224, 3810, 0),
			new RSTile(3230, 3812, 0), new RSTile(3236, 3817, 0),
			new RSTile(3239, 3826, 0), new RSTile(3238, 3835, 0),
			new RSTile(3242, 3845, 0), new RSTile(3249, 3850, 0),
			new RSTile(3244, 3855, 0), new RSTile(3240, 3859, 0),
			new RSTile(3234, 3864, 0), new RSTile(3231, 3868, 0),
			new RSTile(3228, 3878, 0), new RSTile(3226, 3885, 0),
			new RSTile(3225, 3892, 0), new RSTile(3224, 3899, 0),
			new RSTile(3224, 3902, 0) };

	private RSTile[] GATE_TO_WHITE_BERRIES = Walking
			.invertPath(WHITE_BERRIES_TO_GATE);

	private RSTile[] COMBAT_PATH = new RSTile[] { new RSTile(3223, 3809, 0),
			new RSTile(3229, 3811, 0), new RSTile(3233, 3813, 0),
			new RSTile(3236, 3817, 0), new RSTile(3239, 3822, 0),
			new RSTile(3239, 3826, 0), new RSTile(3239, 3831, 0),
			new RSTile(3235, 3835, 0), new RSTile(3239, 3841, 0),
			new RSTile(3238, 3848, 0) };

	private RSTile MAGE_ARENA_LEVER = new RSTile(3092, 3957, 0);
	private RSTile MAGE_ARENA_EXIT = new RSTile(3098, 3957, 0);

	private boolean run = true;
	private boolean evade = false;
	private ABCUtil abc;
	private boolean go_to_anticipated = false;
	private boolean always_anticipate = true;
	private String status = "Starting";
	private RSTile[] PAINT_PATH;
	private long startTime;
	private int startXP;
	private int startLVL;
	private int berryCount = 0;
	private int totalBerries = 0;
	private int previousWorld = 0;
	private int withdrawAttempt = 0;
	private ArrayList<GroundItemTracker> ground = new ArrayList<GroundItemTracker>();

	// private ThreatSearch search;

	public void run() {

		// WebWalking.walkTo(WHITE_BERRIES_TO_GATE[0]);

		// search = new ThreatSearch();
		// Thread searching = new Thread(search);
		// searching.start();

		Camera.setCameraAngle(100);
		abc = new ABCUtil();
		startTime = System.currentTimeMillis();
		startXP = Skills.getXP(SKILLS.MAGIC);
		startLVL = Skills.getActualLevel(SKILLS.MAGIC);

		while (run) {

			if (LUMBRIDGE.contains(Player.getPosition())) {
				println("Oh dear, you are dead!");
				run = false;
			}

			int count = Inventory.getCount("White berries");
			if (berryCount < count) {
				totalBerries += count - berryCount;
				berryCount = count;
			} else if (berryCount > 0 && count == 0) {
				berryCount = 0;
			}

			if (Game.getRunEnergy() >= abc.INT_TRACKER.NEXT_RUN_AT.next()) {
				Options.setRunOn(true);
				abc.INT_TRACKER.NEXT_RUN_AT.reset();
			}

			if (evade) {
				println("We need to logout immediately!");
				previousWorld = WorldHopper.getWorld();
				if (Login.logout()) {
					int w = WorldHopper.getRandomWorld(true);
					if (w != previousWorld) {
						if (WorldHopper.changeWorld(w))
							evade = false;
					}
				}
			} else {
				if (!Inventory.isFull() && Inventory.getCount("Law rune") > 0
						&& Inventory.getCount("Air rune") > 0) {
					if (MAGE_ARENA_BANK.contains(Player.getPosition())) {
						if (Banking.isBankScreenOpen()) {
							Banking.close();
						} else {
							if (Camera.getCameraRotation() < 140
									|| Camera.getCameraRotation() > 220) {
								status = "Turning camera to face Lever";
								Camera.setCameraRotation(General.random(140,
										220));
							}
							if (clickObject("Pull", "Lever")) {
								status = "Outside bank";
							}
						}
					} else if (NORTH_OF_GATE.contains(Player.getPosition())) {
						if (MAGE_ARENA_ENTRANCE.contains(Player.getPosition())) {
							if (Camera.getCameraRotation() < 230
									|| Camera.getCameraRotation() > 310) {
								status = "Turning camera to face Web";
								Camera.setCameraRotation(General.random(230,
										310));
							}
							if (!PathFinding.canReach(MAGE_ARENA_EXIT, false)) {
								if (clickObject("Slash", "Web")) {
									status = "Slashed Web";
								}
							} else {
								walkToTile(MAGE_ARENA_EXIT);
							}
						} else {
							if (!MAGE_ARENA_TO_GATE[MAGE_ARENA_TO_GATE.length - 1]
									.isOnScreen()) {
								status = "Walking to Gate";
								walkPath(MAGE_ARENA_TO_GATE, 1);
							} else if (!PathFinding.canReach(
									GATE_TO_WHITE_BERRIES[0], false)) {
								if (Camera.getCameraRotation() < 140
										|| Camera.getCameraRotation() > 220) {
									status = "Turning camera to face Gate";
									Camera.setCameraRotation(General.random(
											140, 220));
								}
								if (clickObject("Open", "Gate")) {
									status = "Opened Gate";
								}
							} else if (PathFinding.canReach(
									GATE_TO_WHITE_BERRIES[0], false)) {
								walkToTile(GATE_TO_WHITE_BERRIES[0]);
							}
						}
					} else if (SOUTH_OF_GATE.contains(Player.getPosition())) {
						if (!GATE_TO_WHITE_BERRIES[GATE_TO_WHITE_BERRIES.length - 1]
								.isOnScreen()) {
							status = "Walking to White Berries";
							walkPath(GATE_TO_WHITE_BERRIES, 1);
						} else {
							if (Player
									.getPosition()
									.distanceTo(
											GATE_TO_WHITE_BERRIES[GATE_TO_WHITE_BERRIES.length - 1]) >= 1) {
								status = "Moving to spell tile";
								if (Camera.getCameraAngle() < 60)
									Camera.setCameraAngle(General.random(60,
											100));
								PAINT_PATH = new RSTile[] { GATE_TO_WHITE_BERRIES[GATE_TO_WHITE_BERRIES.length - 1] };

								// DynamicClicking
								// .clickRSTile(
								// GATE_TO_WHITE_BERRIES[GATE_TO_WHITE_BERRIES.length
								// - 1],
								// 1);
								Walking.clickTileMM(
										GATE_TO_WHITE_BERRIES[GATE_TO_WHITE_BERRIES.length - 1],
										1);
								sleepWhileMoving(
										GATE_TO_WHITE_BERRIES[GATE_TO_WHITE_BERRIES.length - 1],
										0);
								PAINT_PATH = null;
							} else {
								if (Player.getRSPlayer().isInCombat()) {
									status = "Player in combat with NPC";
									Options.setRunOn(true);
									while (Player
											.getPosition()
											.distanceTo(
													COMBAT_PATH[COMBAT_PATH.length - 1]) >= 5) {
										walkPath(COMBAT_PATH, 1);
									}
									status = "Walking back";
								} else {
									if (Camera.getCameraRotation() > 52) {
										status = "Turning camera to berries";
										Camera.setCameraRotation(General
												.random(0, 52));
									} else if (Camera.getCameraAngle() < 33
											|| Camera.getCameraAngle() > 57) {
										status = "Setting angle to for grab";
										Camera.setCameraAngle(General.random(
												50, 57));
									} else {
										if (castTelekineticGrab("White berries")) {
											status = "Successful cast";
										}
									}
								}
							}
						}
					}

				} else { // Inventory not full or out of supplies

					if (MAGE_ARENA_BANK.contains(Player.getPosition())) {
						status = "Opening Bank";
						if (Banking.openBank()) {
							sleep((long) General.randomSD(1354.5454545454545,
									236.03006165046871));
							if (Inventory.getCount("White berries") > 0) {
								Banking.depositAll();
								sleep((long) General.randomSD(
										921.5454545454545, 142.03006165046871));
							}
							if (withdraw(26, "Law rune")) {
								withdrawAttempt = 0;
								status = "We have all law runes";
							} else {
								withdrawAttempt++;
							}
							if (withdraw(26, "Air rune")) {
								withdrawAttempt = 0;
								status = "We have all law runes";
							} else {
								withdrawAttempt++;
							}
							Banking.depositAllExcept("Law rune", "Air rune");
							if (withdrawAttempt > 10) {
								println("We are out of supplies, shutting down.");
								run = false;
							}
						}
					} else if (NORTH_OF_GATE.contains(Player.getPosition())) {
						if (MAGE_ARENA_ENTRANCE.contains(Player.getPosition())) {
							if (Camera.getCameraRotation() < 50
									|| Camera.getCameraRotation() > 130) {
								status = "Turning camera to face Web";
								Camera.setCameraRotation(General
										.random(50, 130));
							}
							if (!PathFinding.canReach(MAGE_ARENA_LEVER, false)) {
								if (clickObject("Slash", "Web")) {
									status = "Slashed Web";
								}
							} else {
								if (!MAGE_ARENA_LEVER.isOnScreen()) {
									walkToTile(MAGE_ARENA_LEVER);
								} else {
									if (clickObject("Pull", "Lever")) {
										status = "Inside Mage Arnea";
									}
								}
							}
						} else if (!GATE_TO_MAGE_ARENA[GATE_TO_MAGE_ARENA.length - 1]
								.isOnScreen()) {
							status = "Walking to Mage Arena";
							walkPath(GATE_TO_MAGE_ARENA, 1);
						} else if (GATE_TO_MAGE_ARENA[GATE_TO_MAGE_ARENA.length - 1]
								.isOnScreen()) {
							walkToTile(GATE_TO_MAGE_ARENA[GATE_TO_MAGE_ARENA.length - 1]);
						}
					} else if (SOUTH_OF_GATE.contains(Player.getPosition())) {
						if (!WHITE_BERRIES_TO_GATE[WHITE_BERRIES_TO_GATE.length - 1]
								.isOnScreen()) {
							status = "Walking to Gate";
							walkPath(WHITE_BERRIES_TO_GATE, 1);
						} else if (!PathFinding.canReach(GATE_TO_MAGE_ARENA[0],
								false)) {
							if (Camera.getCameraRotation() > 40
									&& Camera.getCameraRotation() < 320) {
								status = "Turning camera to face Gate";
								int r = General.random(0, 1);
								if (r == 0) {
									Camera.setCameraRotation(General.random(
											320, 360));
								} else {
									Camera.setCameraRotation(General.random(0,
											40));
								}
							}
							RSTile gate = new RSTile(3224, 3903, 0);
							if (Player.getPosition().distanceTo(gate) > 2) {
								status = "Walking to Gate";
								walkToTile(gate);
							} else {
								if (clickObject("Open", "Gate")) {
									status = "Opened Gate";
								}
							}
						} else if (PathFinding.canReach(GATE_TO_MAGE_ARENA[0],
								false)) {
							walkToTile(GATE_TO_MAGE_ARENA[0]);
						}
					}
				}
			}
			sleep(100);
		}
	}

	private boolean castTelekineticGrab(String name) {
		if (GameTab.open(TABS.MAGIC)) {
			if (!Magic.isSpellSelected()) {
				status = "Selecting Telekinetic Grab";
				Magic.selectSpell("Telekinetic Grab");
				return false;
			} else {
				status = "Spell is selected";
				RSGroundItem[] berries = GroundItems.findNearest(name);
				if (berries.length > 0) {
					if (ground.size() < 2) {
						for (RSGroundItem r : berries) {
							RSTile bPos = r.getPosition();
							if (bPos != null) {
								boolean found = false;
								for (GroundItemTracker g : ground) {
									if (g.getPosition().equals(bPos)) {
										found = true;
										break;
									}
								}
								if (!found)
									ground.add(new GroundItemTracker(name,
											bPos, 0));
							}
						}
					}
					RSGroundItem berry = null;
					for (RSGroundItem b : berries) {
						RSTile bPos = b.getPosition();
						if (bPos != null) {
							for (GroundItemTracker g : ground) {
								if (g.getPosition().equals(bPos)) {
									if ((System.currentTimeMillis() - g
											.getTime()) > 1500) {
										berry = b;
										break;
									}
								}
							}
						}
					}
					if (berry != null) {
						RSTile bPos = berry.getPosition();
						if (bPos != null) {
							status = "Clicking White berries";
							if (berry.hover()) {
								Mouse.click(3);
								if (ChooseOption
										.select("Cast Telekinetic Grab -> "
												+ name)) {
									sleep((long) General.randomSD(
											1410.5454545454545,
											212.03006165046871));
									status = "Finished casting spell!";
									for (GroundItemTracker g : ground) {
										if (g.getPosition().equals(bPos)) {
											g.setTime(System
													.currentTimeMillis());
											break;
										}
									}
									go_to_anticipated = abc.BOOL_TRACKER.GO_TO_ANTICIPATED
											.next();
									abc.BOOL_TRACKER.GO_TO_ANTICIPATED.reset();
									return true;
								} else {
									ChooseOption.select("Cancel");
									return false;
								}
							}
						}
					}
				} else if (go_to_anticipated || always_anticipate) {
					GroundItemTracker longest = null;
					for (GroundItemTracker g : ground) {
						if (longest == null
								|| (g.getTime() < longest.getTime()))
							longest = g;
					}
					if (longest != null) {
						status = "Hovering over anticipated tile";
						longest.getPosition().hover();
					}
				}
			}
		}
		return false;
	}

	private boolean withdraw(int amount, String name) {
		if (!Banking.isBankScreenOpen())
			return false;
		if (amount == 0 || name.isEmpty())
			return false;
		int count = Inventory.getCount(name);
		if ((amount - count) > 0) {
			status = "Withdrawing " + (amount - count) + " " + name;
			Banking.withdraw((amount - count), name);
			long sleep = System.currentTimeMillis()
					+ (long) General.randomSD(1644.5454545454545,
							182.03006165046871);
			while (sleep > System.currentTimeMillis()) {
				if (Inventory.getCount(name) != count)
					break;
				sleep((long) General.randomSD(172.5454545454545,
						44.03006165046871));
			}
			if (amount - (Inventory.getCount(name)) == 0)
				return true;
		}
		return false;
	}

	private boolean hoverOverNextToSpawn(final RSTile tile) {
		if (tile == null)
			return false;
		String text = Game.getUptext();
		if (text == null)
			return false;
		Polygon area = Projection.getTileBoundsPoly(tile, 0);
		if (area == null)
			return false;
		if (!area.contains(Mouse.getPos())) {
			Mouse.move(
					(int) area.getBounds().getCenterX()
							+ General.random(-10, 10), (int) area.getBounds()
							.getCenterY() + General.random(-15, 15));
			sleep((long) General.randomSD(116.5454545454545, 56.03006165046871));
			return true;
		} else {
			return false;
		}
	}

	private boolean clickObject(String action, String name) {
		RSObject[] obj = Objects.findNearest(30, name);
		if (obj.length == 0)
			return false;
		if (obj[0] == null)
			return false;
		if (Player.getPosition().distanceTo(obj[0]) <= 5) {
			if (obj[0].isOnScreen()) {
				if (Player.isMoving()) {
					return false;
				}
				if (DynamicClicking.clickRSObject(obj[0], action)) {
					status = "Clicked " + name;
					long hover = System.currentTimeMillis()
							+ (long) General.randomSD(561.5454545454545,
									112.03006165046871);
					long modifier = (long) General.randomSD(1244.5454545454545,
							112.03006165046871);
					long timer = System.currentTimeMillis() + modifier;
					status = "Negotiating with " + name;
					while (timer > System.currentTimeMillis()) {
						if (Player.isMoving() || Player.getAnimation() != -1)
							timer = System.currentTimeMillis() + modifier;
						if (!Game.getUptext().contains(name)
								&& !Player.isMoving()
								&& hover < System.currentTimeMillis()) {
							status = "Hovering over " + name;
							obj[0].hover();
							hover = System.currentTimeMillis()
									+ (long) General.randomSD(
											561.5454545454545,
											112.03006165046871);
						}
						sleep(25);
					}
					return true;
				}
			} else {
				status = "Turning Camera to " + name;
				Camera.turnToTile(obj[0]);
				sleep((long) General.randomSD(223.5625, 62.2426043032937));
			}
		} else {
			status = "Walking to " + name;
			walkToTile(obj[0]);
		}
		return false;
	}

	public void walkToTile(final Positionable tile) {
		if (Camera.getCameraAngle() < 70)
			Camera.setCameraAngle(100);
		RSTile[] path;
		if (PathFinding.canReach(tile, false)) {
			if (Player.getPosition().distanceTo(tile) <= 8) {
				RSTile[] screen_path = generateScreenPath(Player.getPosition(),
						tile);
				PAINT_PATH = screen_path;
				status = "Walking Screen Path";
				walkScreenPath(screen_path);
			} else {
				if (isTileOnMinimap(tile)) {
					RSTile tilePosition = tile.getPosition();
					if (tilePosition != null) {
						PAINT_PATH = new RSTile[] { tilePosition };
					}
					status = "Clicking Minimap";
					clickTileMinimap(tile);
				} else {
					DPathNavigator d = new DPathNavigator();
					path = d.findPath(tile);
					PAINT_PATH = path;
					status = "Walking DPath";
					walkPath(path, 1);
				}
			}
		} else {
			if (isTileOnMinimap(tile)) {
				RSTile tilePosition = tile.getPosition();
				if (tilePosition != null) {
					PAINT_PATH = new RSTile[] { tilePosition };
				}
				status = "Clicking Minimap";
				clickTileMinimap(tile);
			}
		}
		PAINT_PATH = null;
	}

	public void clickTileMinimap(Positionable tile) {
		if (tile != null && !Player.isMoving()) {
			Walking.clickTileMM(tile, 1);
			sleepWhileMoving(tile, 3);
		}
	}

	public void walkPath(RSTile[] path, int offset) {
		PAINT_PATH = path;
		if (path != null && path.length > 0) {
			long timer = System.currentTimeMillis() + 5000;
			while (!evade && timer > System.currentTimeMillis()
					&& !path[path.length - 1].isOnScreen()) {
				RSTile tile = null;
				for (int i = path.length - 1; i > 0; i--) {
					if (isTileOnMinimap(path[i])) {
						tile = path[i];
						break;
					}
				}
				if (tile != null) {
					timer = System.currentTimeMillis() + 5000;
					tile = randomizeTile(tile, offset);
					Walking.clickTileMM(tile, 1);
					sleepWhileMoving(tile, 4);
				}
				sleep(100);
			}
		}
		PAINT_PATH = null;
	}

	public void walkScreenPath(RSTile[] path) {
		if (Camera.getCameraAngle() < 60)
			Camera.setCameraAngle(100);
		if (path.length > 0) {
			long timer = System.currentTimeMillis() + 5000;
			while (!evade && timer > System.currentTimeMillis()
					&& Player.getPosition().distanceTo(path[0]) >= 2) {
				RSTile tile = null;
				for (RSTile t : path) {
					tile = t;
					if (t.isOnScreen())
						break;
				}
				if (tile != null) {
					if (!PathFinding.canReach(tile, false))
						break;
					timer = System.currentTimeMillis() + 5000;
					Walking.clickTileMS(tile, "Walk here");
					sleepWhileMoving(tile, 1);
				}
				sleep(100);
			}
		}
	}

	public boolean isTileOnMinimap(Positionable tile) {
		return Projection.isInMinimap(Projection.tileToMinimap(tile));
	}

	public RSTile randomizeTile(Positionable tile, int offset) {
		int x = tile.getPosition().getX() + General.random(-offset, offset);
		int y = tile.getPosition().getY() + General.random(-offset, offset);
		int p = tile.getPosition().getPlane();
		return new RSTile(x, y, p);
	}

	public void sleepWhileMoving(Positionable tile, int distanceTo) {
		long sleep = System.currentTimeMillis() + 2000;
		while (!evade && sleep > System.currentTimeMillis()) {
			if (Player.isMoving())
				sleep = System.currentTimeMillis() + 1000;
			if (Player.getPosition().distanceTo(tile) <= distanceTo)
				break;
			sleep((long) General.randomSD(216.5625, 62.2426043032937));
		}
	}

	public RSTile[] generateScreenPath(RSTile start, Positionable end) {
		if (start == null || end == null)
			return new RSTile[0];
		RSTile[] path = PathFinding.generatePath(start, end, true);
		if (path == null)
			return new RSTile[0];
		ArrayList<RSTile> valid = new ArrayList<RSTile>();
		for (final RSTile tile : path) {
			boolean obstruction = false;
			RSObject[] obj = Objects.getAt(tile);
			if (obj.length > 0) {
				if (obj[0] != null
						&& obj[0].getType().equals(TYPES.INTERACTIVE))
					obstruction = true;
			}
			if (!obstruction) {
				RSNPC[] npc = NPCs.getAll(new Filter<RSNPC>() {
					@Override
					public boolean accept(RSNPC npc) {
						return npc.getPosition().equals(tile);
					}
				});
				if (npc.length == 0)
					valid.add(tile);
			}
		}
		RSTile[] array = new RSTile[valid.size()];
		array = valid.toArray(array);
		return array;
	}

	public class ThreatSearch implements Runnable {
		private volatile boolean stop = false;
		private int playerCombatLevel = 0;
		private String playerName = "";
		private int wildernessLevel = 0;

		@Override
		public void run() {
			while (!stop) {
				try {
					if (Login.getLoginState().equals(Login.STATE.INGAME)
							&& !evade) {
						RSNPC[] npc = NPCs.find("Vet'ion", "Chaos Elemental");
						if (npc.length > 0) {
							if (npc[0] != null
									&& Player.getPosition().distanceTo(npc[0]) <= 8) {
								println("We found \""
										+ npc[0].getName()
										+ "\" in the wilderness. He is less than "
										+ Player.getPosition().distanceTo(
												npc[0])
										+ " tiles away from our player.");
								evade = true;
							}
						}
						if (playerCombatLevel == 0)
							playerCombatLevel = Player.getRSPlayer()
									.getCombatLevel();
						if (playerName.isEmpty())
							playerName = Player.getRSPlayer().getName();
						if (Interfaces.isInterfaceValid(90)) {
							RSInterfaceChild level = Interfaces.get(90, 24);
							if (level != null) {
								String text = level.getText();
								if (text != null)
									wildernessLevel = Integer.parseInt(text
											.replace("Level: ", ""));
							}
						}
						if (wildernessLevel > 0) {
							RSPlayer[] players = Players
									.getAll(new Filter<RSPlayer>() {
										public boolean accept(RSPlayer p) {
											if (p == null)
												return false;
											if (p.getName().equalsIgnoreCase(
													playerName))
												return false;
											int enemyCombatLevel = p
													.getCombatLevel();
											if ((enemyCombatLevel >= (playerCombatLevel - wildernessLevel))
													&& (enemyCombatLevel <= (playerCombatLevel + wildernessLevel)))
												return true;
											return false;
										}
									});
							if (players.length > 0) {
								if (players[0] != null) {
									println("---");
									println("Player \""
											+ players[0].getName()
											+ "\" ("
											+ "Level "
											+ players[0].getCombatLevel()
											+ ") found "
											+ Player.getPosition().distanceTo(
													players[0])
											+ " tiles away from our player in wilderness level "
											+ wildernessLevel + ".");
									RSPlayerDefinition def = players[0]
											.getDefinition();
									if (def != null) {
										RSItem[] equipment = def.getEquipment();
										println("The user was wearing:");
										for (RSItem e : equipment) {
											RSItemDefinition d = e
													.getDefinition();
											if (d != null)
												println("- " + d.getName());
										}
										println("---");
									}
								}
								evade = true;
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				sleep(100);
			}
		}

		public void setStop(boolean stop) {
			System.out.println("Stopped Threat Searching thread.");
			this.stop = stop;
		}
	}

	private class GroundItemTracker {
		private String name;
		private RSTile tile;
		private long time;

		public GroundItemTracker(String name, RSTile tile, long time) {
			this.name = name;
			this.tile = tile;
			this.time = time;
		}

		private String getName() {
			return name;
		}

		private void setName(String name) {
			this.name = name;
		}

		private RSTile getPosition() {
			return tile;
		}

		private void setPosition(RSTile tile) {
			this.tile = tile;
		}

		private long getTime() {
			return time;
		}

		private void setTime(long time) {
			this.time = time;
		}
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
		long time = System.currentTimeMillis() - startTime;
		int berriesPerHour = (int) (totalBerries * 3600000D / (System
				.currentTimeMillis() - startTime));
		int profit = (totalBerries * 1019);
		int profitPerHour = (int) (profit * 3600000D / (System
				.currentTimeMillis() - startTime));
		int xpGained = Skills.getXP(SKILLS.MAGIC) - startXP;
		int xpPerHour = (int) (xpGained * 3600000D / (System
				.currentTimeMillis() - startTime));
		int currentLVL = Skills.getActualLevel(SKILLS.MAGIC);

		Color background = new Color(24, 36, 82, 200);
		g.setColor(background);
		g.fillRoundRect(235, 345, 261, 132, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(235, 345, 261, 132, 5, 5);

		int x = 240;
		int y = 360;
		int spacing = 15;
		Font bold = new Font("Tahoma", Font.BOLD, 12);
		g.setFont(bold);

		g.drawString("USA White Berry Collector             v" + version,
				x + 15, y);
		g.drawLine(235, 363, 495, 363);
		y += spacing + 3;
		g.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g.drawString("Status: " + status, x, y);
		y += spacing;
		g.drawString(
				"White Berries Gained: "
						+ addCommasToNumericString(Integer
								.toString(totalBerries))
						+ " ("
						+ addCommasToNumericString(Integer
								.toString(berriesPerHour)) + "/hr)", x, y);

		y += spacing;
		g.drawString(
				"Profit Gained: "
						+ addCommasToNumericString(Integer.toString(profit))
						+ " ("
						+ addCommasToNumericString(Integer
								.toString(profitPerHour)) + "/hr)", x, y);

		y += spacing;
		g.drawString("Magic Level: " + currentLVL + " (+"
				+ (currentLVL - startLVL) + ")", x, y);
		y += spacing;
		g.drawString(
				"XP Gained: "
						+ addCommasToNumericString(Integer.toString(xpGained))
						+ " ("
						+ addCommasToNumericString(Integer.toString(xpPerHour))
						+ "/hr)", x, y);
		y += 5;

		int xpTNL = Skills.getXPToNextLevel(SKILLS.MAGIC);
		int percentTNL = Skills.getPercentToNextLevel(SKILLS.MAGIC);
		long TTNL = 0;
		if (xpPerHour > 0) {
			TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
		}
		int percentFill = (250 * percentTNL) / 100;
		g.setColor(Color.RED);
		g.fillRoundRect(x, y, 250, 16, 5, 5);
		Color green = new Color(10, 150, 10);
		g.setColor(green);
		g.fillRoundRect(x, y, percentFill, 16, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(x, y, 250, 16, 5, 5);
		g.drawString(
				addCommasToNumericString(Integer.toString(xpTNL)) + " xp to "
						+ (currentLVL + 1) + " | " + Timing.msToString(TTNL),
				x + 40, y + 13);

		if (ground.size() > 0) {
			g.setColor(Color.WHITE);
			for (GroundItemTracker t : ground) {
				if (t.getPosition().isOnScreen())
					g.drawPolygon(Projection.getTileBoundsPoly(t.getPosition(),
							0));
			}
		}

		if (PAINT_PATH != null) {
			g.setColor(Color.GREEN);
			for (RSTile tile : PAINT_PATH) {
				if (tile.isOnScreen())
					g.drawPolygon(Projection.getTileBoundsPoly(tile, 0));
				Point point = Projection.tileToMinimap(tile);
				g.fillRect((int) point.getX(), (int) point.getY(), 2, 2);
			}
		}
	}

	@Override
	public void onEnd() {
		// search.setStop(true);
	}
}