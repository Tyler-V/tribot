package scripts;

import static java.util.Arrays.asList;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
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
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;
import org.w3c.dom.Document;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Miner")
public class UsaMiner extends Script implements Painting, Ending, MouseActions, MessageListening07 {

	String version = "2.0";
	private final Image TRANSFER_BANK_IMAGE = getImage("http://i.imgur.com/rCSTBvc.png");

	// Options
	private static boolean STATIONARY;
	private boolean useObjectTracking;
	private boolean useAutoResponder;
	private boolean always_hover;
	private boolean always_go_to_anticipated;
	private boolean always_use_closest;

	private int MAX_PLAYER_COUNT;
	private boolean membersWorlds;
	private boolean changeWorldsByTime;
	private int changeWorldsTime;

	private String MULE_USERNAME;
	private int MULE_WORLD;
	private RSTile MULE_LOCATION;
	private boolean automaticTransfer;
	private int transferValue;

	// abc
	private ABCUtil abc;
	private long last_busy_time = 0L;
	private int RIGHT_CLICK = 100;
	private int RIGHT_CLICK_PERCENTAGE;

	// Constants
	private static int[] FURNACE_ID = new int[] { 24009 };
	private static int[] WATER_SOURCE_ID = new int[] { 3043, 884 };
	private int totalOres = 0, totalBars = 0, totalProfit = 0, bankValue = 0, oreCount = 0, barCount = 0, startLVL = 0,
			startXP = 0;
	private long startTime;
	private boolean run = true;
	private final int SOFT_CLAY = 1761;
	private final int HARD_CLAY = 434;
	private final int BUCKET = 1925;
	private final int BUCKET_OF_WATER = 1929;
	private final int[] BUCKETS = new int[] { BUCKET, BUCKET_OF_WATER };
	private final static String[] PICKAXES = { "Bronze pickaxe", "Iron pickaxe", "Steel pickaxe", "Mithril pickaxe",
			"Adamant pickaxe", "Rune pickaxe", "Dragon pickaxe" };
	private final String[] ORE_NAMES = { "Clay", "Tin ore", "Copper ore", "Iron ore", "Silver ore", "Gold ore",
			"Mithril ore", "Adamantite ore", "Runite ore" };
	private final String[] BAR_NAMES = { "Bronze bar", "Iron bar", "Silver bar", "Gold bar", "Mithril bar",
			"Adamantite bar", "Runite bar" };
	private final String[] ITEMS_TO_KEEP = { PICKAXES[0], PICKAXES[1], PICKAXES[2], PICKAXES[3], PICKAXES[4],
			PICKAXES[5], PICKAXES[6], "Bucket", "Bucket of water" };

	// Variables
	private boolean TRANSFER_ITEMS = false;
	private String status = "";

	private ArrayList<ObjectHistory> objectHistory = new ArrayList<ObjectHistory>();
	private ArrayList<MINING_LOCATION> ORDERS = new ArrayList<MINING_LOCATION>();
	private static MINING_LOCATION CURRENT_LOCATION = null;
	private FURNACE furnace = null;
	private WATER WATER_SOURCE = null;
	private BAR CURRENT_BAR = null;
	private ITEM_OPTIONS ITEM_ACTION = null;

	private RSObject[] rocks = null;
	private RSObject current = null;
	private RSObject next = null;
	private RSObject next_to_spawn = null;

	private static RSTile STATIONARY_POSITION = null;
	private RSTile[] PAINT_PATH = null;

	// Auto Responder
	private static ArrayList<Conversation> conversationHistory = new ArrayList<Conversation>();

	// Threads
	private ObjectTracking tracker;

	// GUI
	private boolean gui_is_up = true;
	gui g = new gui();

	@SuppressWarnings("deprecation")
	public void run() {

		RIGHT_CLICK_PERCENTAGE = General.random(5, 25);
		println("We will right click " + RIGHT_CLICK_PERCENTAGE + "% of the time.");

		g.setVisible(true);

		while (gui_is_up) {

			status = "GUI";
			sleep(100);

		}

		if (useAutoResponder)
			super.setAutoResponderState(false);

		if (useObjectTracking) {
			System.out.println("Starting Threat Searching thread.");
			tracker = new ObjectTracking();
			Thread tracking = new Thread(tracker);
			tracking.start();
		}

		abc = new ABCUtil();

		startTime = Timing.currentTimeMillis();

		while (run) {

			if (startLVL == 0 || startXP == 0) {
				startLVL = Skills.getActualLevel(SKILLS.MINING);
				startXP = Skills.getXP(SKILLS.MINING);
			}

			if (Camera.getCameraAngle() < 60)
				Camera.setCameraAngle(100);

			if (TRANSFER_ITEMS) {

				int ORIGINAL_WORLD = WorldHopperV2.getCurrentWorld();

				if (WorldHopperV2.changeWorld(MULE_WORLD)) {

					if (WebWalking.walkTo(MULE_LOCATION)) {

						if (withdrawItems()) {

							if (trade(MULE_USERNAME)) {

								if (offerItems()) {

									if (WorldHopperV2.changeWorld(ORIGINAL_WORLD))
										TRANSFER_ITEMS = false;

								}

							}

						}

					}

				}

			} else {

				if (STATIONARY && STATIONARY_POSITION == null) {
					STATIONARY_POSITION = Player.getPosition();
					println("Stationary power mining position recorded at: " + STATIONARY_POSITION);
				}

				if (ITEM_ACTION == ITEM_OPTIONS.SOFTEN) {

					if (oreCount < Inventory.getCount(SOFT_CLAY)) {

						totalOres += Inventory.getCount(SOFT_CLAY) - oreCount;
						oreCount = Inventory.getCount(SOFT_CLAY);

					} else if (oreCount > 0 && Inventory.getCount(SOFT_CLAY) == 0) {

						oreCount = 0;

					}

				} else {

					if (oreCount < Inventory.getCount(ORE_NAMES)) {

						totalOres += Inventory.getCount(ORE_NAMES) - oreCount;
						oreCount = Inventory.getCount(ORE_NAMES);

					} else if (oreCount > 0 && Inventory.getCount(ORE_NAMES) == 0) {

						oreCount = 0;

					}

				}

				if (ITEM_ACTION == ITEM_OPTIONS.SMELT) {

					if (barCount < Inventory.getCount(BAR_NAMES)) {

						totalBars += Inventory.getCount(BAR_NAMES) - barCount;
						barCount = Inventory.getCount(BAR_NAMES);

					} else if (barCount > 0 && Inventory.getCount(BAR_NAMES) == 0) {

						barCount = 0;

					}

				}

				MINING_LOCATION PREVIOUS_LOCATION = null;

				if (ITEM_ACTION == ITEM_OPTIONS.SMELT) {

					if (CURRENT_LOCATION != null)
						PREVIOUS_LOCATION = CURRENT_LOCATION;

					CURRENT_LOCATION = CURRENT_LOCATION.getRockForMakingBars(ORDERS, CURRENT_BAR);

					if (CURRENT_LOCATION != PREVIOUS_LOCATION)
						objectHistory.clear();

				} else {

					if (CURRENT_LOCATION != null)
						PREVIOUS_LOCATION = CURRENT_LOCATION;

					CURRENT_LOCATION = MINING_LOCATION.getHighestPossible(ORDERS);

					if (CURRENT_LOCATION != PREVIOUS_LOCATION)
						objectHistory.clear();

				}

				if (!PICKAXE.isWieldingPickaxe() && !PICKAXE.isPickaxeInInventory()) {

					status = "We need a pickaxe!";
					bank();

				} else if (Inventory.isFull() || Inventory.getCount(BAR_NAMES) > 0) {

					if (ITEM_ACTION == ITEM_OPTIONS.DROP) {

						status = "Dropping Items";
						Inventory.dropAllExcept(ITEMS_TO_KEEP);

					} else if (ITEM_ACTION == ITEM_OPTIONS.BANK) {

						bank();

					} else if (ITEM_ACTION == ITEM_OPTIONS.SMELT) {

						if (hasMaterialsRequiredFor(CURRENT_BAR)) {

							smelt();

						} else {

							bank();

						}

					} else if (ITEM_ACTION == ITEM_OPTIONS.SOFTEN) {

						if (Inventory.getCount(HARD_CLAY) > 0) {

							soften();

						} else {

							bank();

						}

					}

				} else {

					if (Game.getRunEnergy() >= abc.INT_TRACKER.NEXT_RUN_AT.next()) {
						Options.setRunOn(true);
						abc.INT_TRACKER.NEXT_RUN_AT.reset();
					}

					if (CURRENT_LOCATION != null) {

						if (!CURRENT_LOCATION.area.contains(Player.getPosition())) {

							status = "Walking to " + toTitleCase(CURRENT_LOCATION.toString());

							Condition c = new Condition() {
								@Override
								public boolean active() {
									return CURRENT_LOCATION.area.contains(Player.getPosition()) || TRANSFER_ITEMS;
								}
							};

							walkToTile(CURRENT_LOCATION.area.getRandomTile(), 2, 1, c);

						} else {

							if (!tooManyPlayersIn(CURRENT_LOCATION.getArea())) {

								rocks = getObjectsAtLocation(CURRENT_LOCATION);

								if (rocks.length > 0) {

									if (current != null && isMining(current)) {

										if (abc.BOOL_TRACKER.HOVER_NEXT.next() || always_hover) {

											if (Inventory.getAll().length >= 27) {

												if (ITEM_ACTION == ITEM_OPTIONS.BANK) {

													if (hoverMinimap())
														status = "Hovering over Minimap";

												} else if (ITEM_ACTION == ITEM_OPTIONS.DROP) {

													if (hoverInventory())
														status = "Hovering over Inventory";

												}

											} else if (rocks.length > 1) {

												for (RSObject r : rocks) {
													if (!r.getPosition().equals(current.getPosition())) {
														next = r;
														break;
													}
												}

												if (hoverOver(next, "Mine"))
													status = "Hovering over next "
															+ CURRENT_LOCATION.getRock().getRockName() + " rock";

											} else if (next_to_spawn != null) {

												if (hoverOver(next_to_spawn, "Mine"))
													status = "Hovering over anticipated rock";

											}

										} else {

											next = null;

										}

									} else {

										current = rocks[0];

										if (rocks.length > 1
												&& (abc.BOOL_TRACKER.USE_CLOSEST.next() || always_use_closest)) {

											if (rocks[1].getPosition().distanceToDouble(rocks[0]) < 2.0) {

												status = "Using farther " + CURRENT_LOCATION.getRock().getRockName()
														+ " rock";

												current = rocks[1];

											}

										}

										status = "Clicking " + CURRENT_LOCATION.getRock().getRockName() + " rock";

										if (clickObject(current)) {

											abc.BOOL_TRACKER.HOVER_NEXT.reset();
											abc.BOOL_TRACKER.USE_CLOSEST.reset();
											abc.BOOL_TRACKER.GO_TO_ANTICIPATED.reset();
											last_busy_time = System.currentTimeMillis();

										}

									}

								} else {

									next = null;
									current = null;

									if (next_to_spawn != null) {

										if ((abc.BOOL_TRACKER.GO_TO_ANTICIPATED.next() || always_go_to_anticipated)) {

											Condition c = new Condition() {

												@Override
												public boolean active() {
													return getObjectsAtLocation(CURRENT_LOCATION).length > 0;
												}

											};

											if (PathFinding.distanceBetween(Player.getPosition(), next_to_spawn,
													true) > 1)
												walkToTile(next_to_spawn, 1, 1, c);

											if (hoverOver(next_to_spawn, "Mine"))
												status = "Hovering over anticipated rock";

										}

									}

								}

							}

						}

					}

				}

			}

			abc.performRotateCamera();
			abc.performXPCheck(SKILLS.MINING);
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

	private boolean tooManyPlayersIn(RSArea area) {

		if (area == null || Login.getLoginState() != Login.STATE.INGAME)
			return false;

		int player_count = Players.getAll(new Filter<RSPlayer>() {

			public boolean accept(RSPlayer p) {

				if (p == null)
					return false;

				if (!area.contains(p))
					return false;

				String playerName = Player.getRSPlayer().getName();
				if (playerName == null)
					return false;

				String name = p.getName();
				if (name == null)
					return false;

				if (playerName.equalsIgnoreCase(name))
					return false;

				if (p.getAnimation() == -1)
					return false;

				return true;

			}

		}).length;

		if ((MAX_PLAYER_COUNT > 0 && player_count >= MAX_PLAYER_COUNT)
				|| abc.SWITCH_TRACKER.TOO_MANY_PLAYERS.next(player_count)) {

			println("Found " + player_count + " players in our resource area.");

			if (changeWorlds()) {

				abc.SWITCH_TRACKER.TOO_MANY_PLAYERS.reset();
				return true;

			}

		}

		return false;
	}

	private boolean changeWorlds() {

		int w = WorldHopperV2.getRandomWorld(WorldHopperV2.WORLD_TYPE.FREE_TO_PLAY);

		if (membersWorlds)
			w = WorldHopperV2.getRandomWorld(WorldHopperV2.WORLD_TYPE.MEMBERS);

		status = "Changing to world " + w + ".";
		int current = WorldHopperV2.getCurrentWorld();

		if (WorldHopperV2.changeWorld(w)) {

			status = "Changed from " + current + " to " + w + ".";
			println(status);
			return true;

		}

		return false;
	}

	private boolean hoverOver(final RSObject object, final String text) {

		if (object == null || !object.isOnScreen())
			return false;

		final RSModel mod = object.getModel();
		if (mod == null)
			return false;

		final Polygon area = mod.getEnclosedArea();
		if (area == null)
			return false;

		abc.waitNewOrSwitchDelay(last_busy_time, true);

		if (!area.contains(Mouse.getPos()) || !Game.isUptext(text)) {

			if (object.hover()) {

				last_busy_time = System.currentTimeMillis();
				return true;

			}

		}

		return false;
	}

	private boolean clickObject(final RSObject object) {

		if (object == null)
			return false;

		if (!object.isOnScreen() && abc.BOOL_TRACKER.GO_TO_ANTICIPATED.next()) {
			status = "Turning to " + CURRENT_LOCATION.getRock().getRockName() + " rock";
			Camera.turnToTile(object);
		}

		if (!object.isOnScreen()) {

			Condition c = new Condition() {
				public boolean active() {
					RSObject[] objects = getObjectsAtLocation(CURRENT_LOCATION);
					return objects.length > 0 && objects[0].isOnScreen();
				}
			};

			walkToTile(object, 2, 2, c);

		}

		abc.waitNewOrSwitchDelay(last_busy_time, true);

		if (object.isOnScreen()) {

			long timer = System.currentTimeMillis() + (long) General.randomSD(1000, 100);

			while (timer > System.currentTimeMillis()) {

				if (!object.isOnScreen() || ChooseOption.isOpen())
					break;

				status = "Moving mouse over " + CURRENT_LOCATION.getRock().getRockName() + " rock";

				object.hover();

				if (Player.isMoving()) {

					sleep((long) General.randomSD(100, 25));

				} else {

					sleep((long) General.randomSD(200, 25));

				}

				RSModel model = object.getModel();

				if (model != null) {

					Polygon area = model.getEnclosedArea();

					if (area != null) {

						if (area.contains(Mouse.getPos())) {

							if (RIGHT_CLICK < RIGHT_CLICK_PERCENTAGE || !Game.isUptext("Mine")) {

								status = "Right clicking " + CURRENT_LOCATION.getRock().getRockName() + " rock";

								Mouse.click(3);

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep((long) General.randomSD(100, 25));
										return ChooseOption.isOpen();
									}
								}, General.randomSD(300, 50));

								break;

							} else if (Game.isUptext("Mine")) {

								status = "Mouse over " + CURRENT_LOCATION.getRock().getRockName() + " rock";
								break;

							}

						}

					}

				}

			}

			if (ChooseOption.isOpen()) {

				status = "Selecting option Mine";

				if (ChooseOption.select("Mine")) {

					long sleep = System.currentTimeMillis() + (long) General.randomSD(1000, 200);

					while (sleep > System.currentTimeMillis()) {

						if (isMining(object)) {
							status = "Mining " + CURRENT_LOCATION.getRock().getRockName() + " rock";
							return true;
						}

						sleep((long) General.randomSD(100, 25));

					}

				} else {

					ChooseOption.close();

				}

			} else {

				status = "Clicking " + CURRENT_LOCATION.getRock().getRockName() + " rock";

				if (Game.isUptext("Mine") && object.click()) {

					long sleep = System.currentTimeMillis() + (long) General.randomSD(1000, 200);

					while (sleep > System.currentTimeMillis()) {

						if (isMining(object)) {
							status = "Mining " + CURRENT_LOCATION.getRock().getRockName() + " rock";
							return true;
						}

						sleep((long) General.randomSD(100, 25));

					}

				} else {

					if (Player.getPosition().distanceTo(object) > 3)
						walkToTile(object, 1, 3, null);

				}

			}

			last_busy_time = System.currentTimeMillis();
			RIGHT_CLICK = General.random(0, 100);

		}

		return false;
	}

	private boolean isMining(RSObject obj) {

		if (obj == null)
			return false;

		RSObject[] rock = Objects.getAt(obj, new Filter<RSObject>() {

			@Override
			public boolean accept(RSObject obj) {
				for (int id : CURRENT_LOCATION.getRock().getRockIDs()) {
					if (id == obj.getID())
						return true;
				}
				return false;
			}

		});

		if (rock.length == 0)
			return false;

		long timer = System.currentTimeMillis() + (long) General.randomSD(1500, 200);

		while (timer > System.currentTimeMillis()) {

			if (Player.isMoving())
				timer = System.currentTimeMillis() + (long) General.randomSD(1000, 200);

			if (Player.getAnimation() != -1)
				return true;

			sleep(50);

		}

		return false;

	}

	private static RSObject[] getObjectsAtLocation(MINING_LOCATION location) {

		if (location == null)
			return new RSObject[0];

		RSArea area = location.getArea();
		if (area == null)
			return new RSObject[0];

		RSObject[] objects = Objects.findNearest(30, new Filter<RSObject>() {

			public boolean accept(RSObject object) {

				if (object == null)
					return false;

				if (!area.contains(object))
					return false;

				for (int id : location.getRock().getRockIDs()) {

					if (object.getID() == id) {

						if (!STATIONARY)
							return true;

						if (STATIONARY_POSITION != null && STATIONARY_POSITION.distanceTo(object) <= 1)
							return true;

						return false;

					}

				}

				return false;

			}

		});

		return objects;

	}

	private RSObject[] getObjectsAtLocationWithAction(final String action, MINING_LOCATION location) {

		if (action == null || location == null)
			return new RSObject[0];

		RSArea area = location.getArea();
		if (area == null)
			return new RSObject[0];

		Rectangle bounds = area.polygon.getBounds();
		if (bounds == null)
			return new RSObject[0];

		final RSTile TILE_SW = new RSTile((int) bounds.getX(), (int) bounds.getY(), area.plane);
		final RSTile TILE_NW = new RSTile((int) (bounds.getX() + (bounds.getWidth() - 1)),
				(int) (bounds.getY() + (bounds.getHeight() - 1)), area.plane);

		RSObject[] objects = Objects.getAllIn(TILE_SW, TILE_NW, new Filter<RSObject>() {

			public boolean accept(RSObject object) {

				if (object == null)
					return false;

				RSObjectDefinition d = object.getDefinition();
				if (d == null)
					return false;

				String[] actions = d.getActions();
				if (actions.length == 0)
					return false;

				for (String a : actions) {

					if (a.equalsIgnoreCase(action)) {

						if (!STATIONARY)
							return true;

						if (STATIONARY_POSITION != null && STATIONARY_POSITION.distanceTo(object) <= 1)
							return true;

						return false;

					}

				}

				return false;

			}

		});

		return objects;
	}

	// private static RSObject[] getObjectsAtLocation(MINING_LOCATION location)
	// {
	//
	// if (location == null)
	// return new RSObject[0];
	//
	// RSArea area = location.getArea();
	// if (area == null)
	// return new RSObject[0];
	//
	// Rectangle bounds = area.polygon.getBounds();
	// if (bounds == null)
	// return new RSObject[0];
	//
	// final RSTile TILE_SW = new RSTile((int) bounds.getX(), (int)
	// bounds.getY(), area.plane);
	// final RSTile TILE_NW = new RSTile((int) (bounds.getX() +
	// (bounds.getWidth() - 1)),
	// (int) (bounds.getY() + (bounds.getHeight() - 1)), area.plane);
	//
	// RSObject[] objects = Objects.getAllIn(TILE_SW, TILE_NW, new
	// Filter<RSObject>() {
	//
	// public boolean accept(RSObject object) {
	//
	// if (object == null)
	// return false;
	//
	// for (int id : location.getRock().getRockIDs()) {
	//
	// if (object.getID() == id) {
	//
	// if (!STATIONARY)
	// return true;
	//
	// if (STATIONARY_POSITION != null && STATIONARY_POSITION.distanceTo(object)
	// <= 1)
	// return true;
	//
	// return false;
	//
	// }
	//
	// }
	//
	// return false;
	//
	// }
	//
	// });
	//
	// return objects;
	// }

	// private RSObject[] getObjectsAtLocationWithAction(final String action,
	// MINING_LOCATION location) {
	//
	// if (action == null || location == null)
	// return new RSObject[0];
	//
	// RSArea area = location.getArea();
	// if (area == null)
	// return new RSObject[0];
	//
	// Rectangle bounds = area.polygon.getBounds();
	// if (bounds == null)
	// return new RSObject[0];
	//
	// final RSTile TILE_SW = new RSTile((int) bounds.getX(), (int)
	// bounds.getY(), area.plane);
	// final RSTile TILE_NW = new RSTile((int) (bounds.getX() +
	// (bounds.getWidth() - 1)),
	// (int) (bounds.getY() + (bounds.getHeight() - 1)), area.plane);
	//
	// RSObject[] objects = Objects.getAllIn(TILE_SW, TILE_NW, new
	// Filter<RSObject>() {
	//
	// public boolean accept(RSObject object) {
	//
	// if (object == null)
	// return false;
	//
	// RSObjectDefinition d = object.getDefinition();
	// if (d == null)
	// return false;
	//
	// String[] actions = d.getActions();
	// if (actions.length == 0)
	// return false;
	//
	// for (String a : actions) {
	//
	// if (a.equalsIgnoreCase(action)) {
	//
	// if (!STATIONARY)
	// return true;
	//
	// if (STATIONARY_POSITION != null && STATIONARY_POSITION.distanceTo(object)
	// <= 1)
	// return true;
	//
	// return false;
	//
	// }
	//
	// }
	//
	// return false;
	//
	// }
	//
	// });
	//
	// return objects;
	// }

	public class ObjectTracking implements Runnable {

		private volatile boolean stop = false;
		private boolean search = false;
		private MINING_LOCATION PREVIOUS_LOCATION = null;

		@Override
		public void run() {

			System.out.println("Started Object Tracking thread.");

			while (!stop) {

				try {

					if (CURRENT_LOCATION != null && CURRENT_LOCATION.area.contains(Player.getPosition())) {

						if (PREVIOUS_LOCATION != CURRENT_LOCATION) {

							RSObject[] objects = getObjectsAtLocation(CURRENT_LOCATION);

							for (RSObject object : objects) {

								boolean found = false;

								RSTile position = object.getPosition();

								if (position != null) {

									for (ObjectHistory history : objectHistory) {

										if (history.getPosition().equals(position)) {

											found = true;
											break;

										}

									}

									if (!found)
										objectHistory.add(new ObjectHistory(position.getPosition(), 0, 0, 0));

								}

							}

							PREVIOUS_LOCATION = CURRENT_LOCATION;

						}

						RSObject next = null;

						long closest = Long.MAX_VALUE;

						for (ObjectHistory o : objectHistory) {

							RSObject[] objects = Objects.getAt(o.getPosition(), new Filter<RSObject>() {

								public boolean accept(RSObject object) {

									if (object == null)
										return false;

									for (int id : CURRENT_LOCATION.getRock().getRockIDs()) {

										if (object.getID() == id)
											return true;

									}

									return false;
								}

							});

							if (objects.length > 0) {

								if (o.getTimeAppeared() == 0 && o.getTimeDissapeared() != 0) {
									o.setTimeAppear(System.currentTimeMillis());
									o.setRespawnTime((o.getTimeAppeared() - o.getTimeDissapeared()));
									o.setTimeDissapeared(0);
								}

							} else {

								if (o.getTimeDissapeared() == 0) {
									o.setTimeDissapeared(System.currentTimeMillis());
									o.setTimeAppear(0);
								}

								if (o.getRespawnTime() != 0 && o.getTimeDissapeared() != 0) {

									long time = Math.abs(((o.getTimeDissapeared() + o.getRespawnTime())
											- System.currentTimeMillis()));

									if (time < closest) {

										closest = time;

										RSObject[] object = Objects.getAt(o.getPosition());

										if (object.length > 0)
											next = object[0];

									}

								}

							}

						}

						next_to_spawn = next;

					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				sleep(50);

			}

		}

		public void setStop(boolean stop) {
			System.out.println("Stopped Object Tracking thread.");
			this.stop = stop;
		}
	}

	private class ObjectHistory {

		private Positionable tile;
		private long time_dissapear;
		private long time_appear;
		private long respawn_time;

		public ObjectHistory(Positionable tile, long time_dissapeared, long time_appeared, long respawn_time) {
			this.tile = tile;
			this.time_dissapear = time_dissapeared;
			this.time_appear = time_appeared;
			this.respawn_time = respawn_time;
		}

		private Positionable getPosition() {
			return tile;
		}

		private void setPosition(Positionable tile) {
			this.tile = tile;
		}

		private long getTimeDissapeared() {
			return time_dissapear;
		}

		private void setTimeDissapeared(long time_dissapear) {
			this.time_dissapear = time_dissapear;
		}

		private long getTimeAppeared() {
			return time_appear;
		}

		private void setTimeAppear(long time_appear) {
			this.time_appear = time_appear;
		}

		private long getRespawnTime() {
			return respawn_time;
		}

		private void setRespawnTime(long respawn_time) {
			this.respawn_time = respawn_time;
		}

	}

	private boolean hoverInventory() {

		Rectangle INVENTORY_RECTANGLE = new Rectangle(536, 186, 200, 100);

		if (INVENTORY_RECTANGLE.contains(Mouse.getPos()))
			return true;

		Point center = new Point(643, 216);

		Point p = new Point(1, 1);

		boolean found = false;

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis() && !found) {

			p.x = center.x + General.random(-120, 120);
			p.y = center.y + General.random(-50, 50);

			if (INVENTORY_RECTANGLE.contains(p))
				found = true;

			sleep(25);

		}

		if (found) {

			abc.waitNewOrSwitchDelay(last_busy_time, true);
			Mouse.move(p);
			last_busy_time = System.currentTimeMillis();

			return true;

		}

		return false;

	}

	private boolean hoverMinimap() {

		Polygon MINIMAP_POLYGON = new Polygon(
				new int[] { 638, 629, 625, 617, 607, 603, 598, 594, 590, 586, 583, 580, 577, 575, 574, 572, 570, 571,
						571, 571, 574, 579, 583, 589, 595, 604, 609, 613, 617, 619, 623, 623, 628, 635, 640, 648, 653,
						658, 662, 670, 678, 688, 695, 703, 709, 712, 714, 716, 713, 707, 701, 689, 675, 649 },
				new int[] { 8, 8, 9, 13, 18, 20, 23, 27, 30, 35, 40, 40, 45, 49, 52, 57, 66, 74, 80, 89, 105, 112, 116,
						122, 126, 130, 132, 134, 138, 141, 146, 150, 154, 159, 159, 158, 157, 152, 145, 137, 132, 127,
						122, 115, 107, 89, 77, 68, 58, 44, 35, 23, 14, 8 },
				54);

		if (MINIMAP_POLYGON.contains(Mouse.getPos()))
			return true;

		Point center = new Point(643, 74);

		Point p = new Point(1, 1);

		boolean found = false;

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis() && !found) {

			p.x = center.x + General.random(-75, 75);
			p.y = center.y + General.random(-75, 75);

			if (MINIMAP_POLYGON.contains(p))
				found = true;

			sleep(25);

		}

		if (found) {

			abc.waitNewOrSwitchDelay(last_busy_time, true);
			Mouse.move(p);
			last_busy_time = System.currentTimeMillis();

			return true;

		}

		return false;

	}

	private boolean soften() {

		if (WATER_SOURCE == null)
			WATER_SOURCE = WATER.getClosestLocation();

		if (WATER_SOURCE != null) {

			if (!WATER_SOURCE.getArea().contains(Player.getPosition())) {

				status = "Walking to " + toTitleCase(WATER_SOURCE.toString());

				Condition c = new Condition() {
					public boolean active() {
						return WATER_SOURCE.getArea().contains(Player.getPosition());
					}
				};

				walkToTile(WATER_SOURCE.getArea().getRandomTile(), 1, 2, c);

			} else {

				if (GameTab.getOpen() != TABS.INVENTORY) {
					status = "Opening Inventory";
					GameTab.open(TABS.INVENTORY);
				}

				if (Inventory.getCount(BUCKET_OF_WATER) == 0 && Inventory.getCount(BUCKET) == 0) {
					println("We do not have a bucket! Shutting down.");
					run = false;
					return false;
				}

				if (Inventory.getCount(BUCKET) > 0) {

					if (!Game.isUptext("Use Bucket ->")) {

						RSItem[] items = Inventory.find(BUCKET);

						if (items.length > 0) {

							status = "Clicking Empty Bucket";

							sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

							if (items[0].click()) {

								Timing.waitCondition(new Condition() {

									public boolean active() {
										sleep(50);
										return Game.isUptext("Use Bucket ->");
									}
								}, 1000);

							}

							abc.DELAY_TRACKER.ITEM_INTERACTION.reset();

						}

					}

					if (Game.isUptext("Use Bucket ->")) {

						status = "Using Bucket on " + toTitleCase(WATER_SOURCE.toString());

						RSObject[] objects = Objects.findNearest(10, WATER_SOURCE.getID());

						if (objects.length > 0) {

							abc.waitNewOrSwitchDelay(last_busy_time, true);

							final int count = Inventory.getCount(BUCKET);

							if (objects[0].click()) {

								if (abc.BOOL_TRACKER.HOVER_NEXT.next()) {

									RSItem[] buckets = Inventory.find(BUCKETS);

									if (buckets.length > 0) {

										status = "Hovering over bucket";

										buckets[0].hover();

									}

								}

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(50);
										return Inventory.getCount(BUCKET) != count;
									}
								}, 1000);

								abc.BOOL_TRACKER.HOVER_NEXT.reset();
								last_busy_time = System.currentTimeMillis();

							}

						}

					}

				}

				if (Inventory.getCount(BUCKET) == 0 && Inventory.getCount(BUCKET_OF_WATER) > 0) {

					if (!Game.isUptext("Use Bucket of water ->")) {

						RSItem[] items = Inventory.find(BUCKET_OF_WATER);

						if (items.length > 0)

						{

							sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

							status = "Clicking Bucket of water";

							if (items[0].click()) {

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(50);
										return Game.isUptext("Use Bucket of water ->");
									}
								}, 1000);

							}

							abc.DELAY_TRACKER.ITEM_INTERACTION.reset();

						}

					}

					if (Game.isUptext("Use Bucket of water ->")) {

						RSItem[] items = Inventory.find(HARD_CLAY);

						if (items.length > 0)

						{

							status = "Clicking Clay";

							sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

							final int count = Inventory.getCount(HARD_CLAY);

							if (items[0].click()) {

								if (abc.BOOL_TRACKER.HOVER_NEXT.next()) {

									RSItem[] buckets = Inventory.find(BUCKETS);

									if (buckets.length > 0) {

										status = "Hovering over bucket";

										buckets[0].hover();

									}

								}

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(50);
										return Inventory.getCount(HARD_CLAY) != count;
									}
								}, 1000);

								abc.BOOL_TRACKER.HOVER_NEXT.reset();

							}

							abc.DELAY_TRACKER.ITEM_INTERACTION.reset();

						}

					}

				}

			}

		}

		return true;
	}

	private boolean smelt() {

		if (furnace == null && CURRENT_LOCATION != null) {

			if (CURRENT_LOCATION.toString().contains("AL_KHARID"))
				furnace = FURNACE.AL_KHARID;

			if (furnace == null)
				furnace = FURNACE.getClosestLocation();

		}

		if (furnace != null) {

			if (!furnace.area.contains(Player.getPosition())) {

				status = "Web Walking to " + toTitleCase(furnace.toString() + " furnace");
				if (!Player.isMoving())
					WebWalking.walkTo(furnace.location);

			} else {

				if (Interfaces.isInterfaceValid(311)) {

					final RSInterfaceChild BAR_INTERFACE = Interfaces.get(311, CURRENT_BAR.child);

					if (BAR_INTERFACE != null && !BAR_INTERFACE.isHidden()) {

						Condition c = new Condition() {
							@Override
							public boolean active() {
								return Inventory.getCount(CURRENT_BAR.getOreName_1()) == 0
										|| (CURRENT_BAR.getOreQuantity_2() > 0
												&& Inventory.getCount(CURRENT_BAR.getOreName_2()) == 0)
										|| Interfaces.isInterfaceValid(233);
							}
						};

						if ((Inventory.getCount(CURRENT_BAR.getOreName_1()) / CURRENT_BAR.getOreQuantity_1()) > 10) {

							status = "Smelt X";

							if (BAR_INTERFACE.click("Smelt X")) {

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep((long) General.randomSD(750, 200));
										return enterAmountMenuUp();
									}
								}, 3000);

								if (enterAmountMenuUp()) {

									int count = Inventory.getCount(CURRENT_BAR.getOreName_1());

									status = "Typing " + count;

									Keyboard.typeSend(Integer.toString(count));

									status = "Smelting " + toTitleCase(CURRENT_BAR.toString());
									sleepForCondition(c);

								}

							}

						} else {

							status = "Smelt 10";

							if (BAR_INTERFACE.click("Smelt 10")) {

								status = "Smelting " + toTitleCase(CURRENT_BAR.toString());
								sleepForCondition(c);

							}

						}

					}

				} else {

					RSObject[] obj = Objects.findNearest(20, furnace.ids);

					if (obj.length > 0) {

						status = "Clicking Furnace";

						if (obj[0].click("Smelt"))

						{

							Condition c = new Condition() {

								@Override
								public boolean active() {
									return Interfaces.isInterfaceValid(311);
								}

							};

							sleepForCondition(c);

						}

					}

				}

			}

		}

		return true;

	}

	private boolean sleepForCondition(Condition c) {

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis()) {

			if (Player.isMoving() || Player.getAnimation() != -1 || Player.getRSPlayer().isInCombat())
				timer = System.currentTimeMillis() + 3000;

			if (c != null) {

				status = "Sleeping for Condition";

				if (c.active()) {

					status = "Condition active!";
					return true;

				}

			}

			sleep(50);

		}

		return false;
	}

	private boolean hasMaterialsRequiredFor(BAR bar) {

		if (bar == null)
			return false;

		if (bar.getOreQuantity_2() > 0) {

			if (Inventory.getCount(bar.getOreName_1()) >= bar.getOreQuantity_1()
					&& Inventory.getCount(bar.getOreName_2()) >= bar.getOreQuantity_2())
				return true;

		} else if (Inventory.getCount(bar.getOreName_1()) >= bar.getOreQuantity_1()) {

			return true;

		}

		return false;
	}

	public boolean enterAmountMenuUp() {
		RSInterfaceChild child = Interfaces.get(162, 32);
		return child != null && !child.isHidden();
	}

	private boolean addProfit() {

		for (BAR b : BAR.values()) {

			int count = Inventory.getCount(b.getID());

			if (count > 0) {

				if (b.getValue() == 0) {
					b.setValue(getPrice(b.getID()));
					println(b.getBarName() + " has a value of " + b.getValue() + " gp.");
				}

				if (b.getValue() > 0)
					totalProfit += count * b.getValue();

			}

		}

		for (ROCK r : ROCK.values()) {

			int count = Inventory.getCount(r.getOreID());

			if (count > 0) {

				if (r.getValue() == 0) {
					r.setValue(getPrice(r.getOreID()));
					println(r.getOreName() + " has a value of " + r.getValue() + " gp.");
				}

				if (r.getValue() > 0)
					totalProfit += count * r.getValue();

			}

		}

		for (GEM g : GEM.values()) {

			int count = Inventory.getCount(g.getID());

			if (count > 0) {

				if (g.getValue() == 0) {
					g.setValue(getPrice(g.getID()));
					println(g.getName() + " has a value of " + g.getValue() + " gp.");
				}

				if (g.getValue() > 0)
					totalProfit += count * g.getValue();

			}

		}

		return true;

	}

	private boolean updateBankValue() {

		int value = 0;

		for (BAR b : BAR.values()) {

			RSItem[] item = Banking.find(b.getID());

			if (item.length > 0) {

				int count = item[0].getStack();

				if (count > 0) {

					if (b.getValue() == 0) {
						b.setValue(getPrice(b.getID()));
						println(b.getBarName() + " has a value of " + b.getValue() + " gp.");
					}

					if (b.getValue() > 0)
						value += count * b.getValue();

				}

			}

		}

		for (ROCK r : ROCK.values()) {

			RSItem[] item = Banking.find(r.getOreID());

			if (item.length > 0) {

				int count = item[0].getStack();

				if (count > 0) {

					if (r.getValue() == 0) {
						r.setValue(getPrice(r.getOreID()));
						println(r.getOreName() + " has a value of " + r.getValue() + " gp.");
					}

					if (r.getValue() > 0)
						value += count * r.getValue();

				}

			}

		}

		for (GEM g : GEM.values()) {

			RSItem[] item = Banking.find(g.getID());

			if (item.length > 0) {

				int count = item[0].getStack();

				if (count > 0) {

					if (g.getValue() == 0) {
						g.setValue(getPrice(g.getID()));
						println(g.getName() + " has a value of " + g.getValue() + " gp.");
					}

					if (g.getValue() > 0)
						value += count * g.getValue();

				}

			}

		}

		bankValue = value;

		return true;

	}

	private boolean isBankLoaded() {

		if (!Interfaces.isInterfaceValid(12))
			return false;

		status = "Waiting for bank to load";

		long timer = System.currentTimeMillis() + 5000;

		while (timer > System.currentTimeMillis()) {

			RSInterfaceChild child = Interfaces.get(12, 5);
			if (child == null)
				return false;

			String text = child.getText();
			if (text == null || text.isEmpty())
				return false;

			final int TOTAL_ITEMS = Banking.getAll().length;
			if (TOTAL_ITEMS == 0)
				return false;

			if (Integer.parseInt(text) == TOTAL_ITEMS)
				return true;

			sleep(100);

		}

		status = "Bank is loaded";

		return false;
	}

	private boolean isNoteSelected() {

		if (!Interfaces.isInterfaceValid(12))
			return false;

		RSInterfaceChild child = Interfaces.get(12, 23);
		if (child == null || child.isHidden())
			return false;

		return child.getTextureID() == 813;

	}

	private boolean setNoteSelected(boolean set) {

		if (set) {

			if (isNoteSelected())
				return true;

			RSInterfaceChild child = Interfaces.get(12, 23);
			if (child == null || child.isHidden())
				return false;

			status = "Noting items";

			if (child.click()) {

				Timing.waitCondition(new Condition() {
					public boolean active() {
						sleep(100);
						return isNoteSelected();
					}
				}, 2000);

			}

			if (isNoteSelected()) {
				status = "Items are noted";
				return true;
			}

		} else {

			if (!isNoteSelected())
				return true;

			RSInterfaceChild child = Interfaces.get(12, 21);
			if (child == null || child.isHidden())
				return false;

			status = "Un-noting items";

			if (child.click()) {

				Timing.waitCondition(new Condition() {

					public boolean active() {
						sleep(100);
						return !isNoteSelected();
					}

				}, 2000);

			}

			if (!isNoteSelected()) {
				status = "Items are un-noted";
				return true;
			}

		}

		return false;
	}

	private boolean withdrawItems() {

		status = "Opening Bank";

		if (openBank()) {

			if (Inventory.getAll().length > Inventory.getCount(PICKAXES)) {

				status = "Deposit All";
				Banking.depositAll();

			} else {

				if (setNoteSelected(true)) {

					for (BAR b : BAR.values()) {

						RSItem[] item = Banking.find(b.getID());

						if (item.length > 0) {

							int count = item[0].getStack();

							status = "Withdrawing " + b.getBarName();

							if (Banking.withdrawItem(item[0], 0)) {

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(100);
										return Inventory.getCount(b.getID()) >= count;
									}
								}, 3000);

							}

						}

					}

					for (ROCK r : ROCK.values()) {

						RSItem[] item = Banking.find(r.getOreID());

						if (item.length > 0) {

							int count = item[0].getStack();

							status = "Withdrawing " + r.getOreName();

							if (Banking.withdrawItem(item[0], 0)) {

								Timing.waitCondition(new Condition() {

									public boolean active() {
										sleep(100);
										return Inventory.getCount(r.getOreID()) >= count;
									}
								}, 3000);

							}

						}

					}

					for (

					GEM g : GEM.values()) {

						RSItem[] item = Banking.find(g.getID());

						if (item.length > 0) {

							int count = item[0].getStack();

							status = "Withdrawing " + g.getName();

							if (Banking.withdrawItem(item[0], 0)) {

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(100);
										return Inventory.getCount(g.getID()) >= count;
									}
								}, 3000);

							}

						}

					}

				}

				sleep((long) General.randomSD(1000, 200));

				if (setNoteSelected(false)) {

					sleep((long) General.randomSD(1000, 200));

					if (Banking.close())
						return true;

				}

			}

		}

		return false;

	}

	private void bank() {

		if (openBank()) {

			PICKAXE BEST_PICKAXE = PICKAXE.getBestPickaxe();

			if (Inventory.getCount(BEST_PICKAXE.getName()) == 0) {

				if (!Inventory.isFull() && Banking.withdraw(1, BEST_PICKAXE.getName())) {
					status = "Withdrawing " + BEST_PICKAXE.getName();
					sleep((long) General.randomSD(1000, 200));
				}

			}

			if (Inventory.getCount(PICKAXES) > 1) {

				String[] DONT_DEPOSIT = new String[] { BEST_PICKAXE.getName() };

				if (ITEM_ACTION == ITEM_OPTIONS.SOFTEN)
					DONT_DEPOSIT = new String[] { BEST_PICKAXE.getName(), "Bucket", "Bucket of water" };

				status = "Depositing Extra Pickaxes";
				Banking.depositAllExcept(DONT_DEPOSIT);

			}

			addProfit();

			sleep((long) General.randomSD(1000, 200));

			status = "Depositing Items";
			Banking.depositAllExcept(ITEMS_TO_KEEP);

			sleep((long) General.randomSD(1000, 200));

			updateBankValue();

		}

	}

	private boolean openBank() {

		if (Banking.isInBank()) {

			status = "Opening Bank";

			if (Banking.openBank() && isBankLoaded()) {

				sleep((long) General.randomSD(1000, 200));

				if (Banking.isBankScreenOpen())
					return true;

			}

		} else {

			status = "Web Walking to Bank";

			WebWalking.walkToBank();

		}

		return false;
	}

	private void blindWalkTo(RSTile destination, int offset, int returnDistance, Condition c) {
		RSTile tile = getClosestTileTo(destination);
		clickTileMinimap(tile, offset, returnDistance, c);
	}

	private RSTile getClosestTileTo(RSTile destination) {
		int radius = 9;
		int diameter = (1 + (2 * radius));
		int x = Player.getPosition().getX() - radius;
		int y = Player.getPosition().getY() + radius;
		int p = Player.getPosition().getPlane();

		int distance = Integer.MAX_VALUE;
		RSTile closest = null;

		for (int i = 0; i < diameter; i++) {
			x = Player.getPosition().getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile tile = new RSTile(x, y, p);
				if (tile.distanceTo(destination) < Player.getPosition().distanceTo(destination)) {
					if (destination.distanceTo(tile) < distance) {
						distance = destination.distanceTo(tile);
						closest = tile;
					}
				}
				x += 1;
			}
			y -= 1;
		}
		return closest;
	}

	public boolean walkToTile(final Positionable tile, int offset, int returnTileDistance, Condition c) {

		abc.waitNewOrSwitchDelay(last_busy_time, true);

		if (tile == null)
			return false;

		RSTile position = tile.getPosition();

		if (position == null)
			return false;

		if (position.isOnScreen()
				|| Player.getPosition().distanceTo(position) <= abc.INT_TRACKER.WALK_USING_SCREEN.next()) {

			if (position.isOnScreen()) {

				status = "Clicking Screen Tile";
				PAINT_PATH = new RSTile[] { position };
				clickTileScreen(position, offset, returnTileDistance, c);

			} else {

				status = "Walking Screen Path";
				RSTile[] path = generateScreenPath(Player.getPosition(), position);
				PAINT_PATH = path;
				walkScreenPath(path, 0, returnTileDistance, c);

			}

		} else {

			if (isTileOnMinimap(position)) {

				status = "Clicking Minimap";
				PAINT_PATH = new RSTile[] { position };
				clickTileMinimap(position, offset, returnTileDistance, c);

			} else {

				DPathNavigator d = new DPathNavigator();
				RSTile[] path = d.findPath(tile);

				if (path.length > 0) {

					status = "Walking Path";
					PAINT_PATH = path;
					walkPath(path, offset, returnTileDistance, c);

				} else {

					PAINT_PATH = new RSTile[] { position };
					WebWalking.walkTo(tile, c, 500);

				}

			}

		}

		PAINT_PATH = null;
		abc.INT_TRACKER.WALK_USING_SCREEN.reset();
		last_busy_time = System.currentTimeMillis();

		return true;
	}

	public boolean clickTileScreen(Positionable tile, int offset, int returnTileDistance, Condition c) {
		if (offset > 0)
			tile = randomizeTile(tile, offset);
		if (tile == null)
			return false;
		if (DynamicClicking.clickRSTile(tile, "Walk here"))
			sleepWhileMoving(tile, returnTileDistance, c);
		return true;
	}

	public boolean clickTileMinimap(Positionable tile, int offset, int returnTileDistance, Condition c) {
		if (offset > 0)
			tile = randomizeTile(tile, offset);
		if (tile == null)
			return false;
		if (Walking.clickTileMM(tile, 1))
			sleepWhileMoving(tile, returnTileDistance, c);
		return true;
	}

	public boolean walkPath(RSTile[] path, int offset, int returnTileDistance, Condition c) {
		if (path == null || path.length == 0)
			return false;
		if (PAINT_PATH == null)
			PAINT_PATH = path;
		long timer = System.currentTimeMillis() + 2000;
		while (timer > System.currentTimeMillis() && Player.getPosition().distanceTo(path[path.length - 1]) > offset) {
			if (c != null && c.active()) {
				status = "Condition active";
				break;
			}
			RSTile tile = null;
			if (isTileOnMinimap(path[path.length - 1])) {
				tile = path[path.length - 1];
			} else {
				for (int i = path.length - 1; i > 0; i--) {
					if (isTileOnMinimap(path[i])) {
						tile = path[i];
						break;
					}
				}
			}
			if (tile == null || !PathFinding.canReach(tile, false)) {
				PAINT_PATH = null;
				return false;
			}
			clickTileMinimap(tile, offset, returnTileDistance, c);
			timer = System.currentTimeMillis() + 2000;
			sleep(50);
		}
		PAINT_PATH = null;
		return true;
	}

	public boolean walkScreenPath(RSTile[] path, int offset, int returnTileDistance, Condition c) {
		if (path == null || path.length == 0)
			return false;
		long timer = System.currentTimeMillis() + 2000;
		while (timer > System.currentTimeMillis() && Player.getPosition().distanceTo(path[0]) > offset) {
			if (c != null && c.active()) {
				status = "Condition active";
				break;
			}
			RSTile tile = null;
			for (RSTile t : path) {
				tile = t;
				if (t.isOnScreen())
					break;
			}
			tile = randomizeTile(tile, offset);
			if (tile != null) {
				timer = System.currentTimeMillis() + 2000;
				Walking.clickTileMS(tile, "Walk here");
				sleepWhileMoving(tile, returnTileDistance, c);
			}
			sleep(50);
		}
		return true;
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

	public void sleepWhileMoving(Positionable tile, int distanceTo, Condition c) {
		long sleep = System.currentTimeMillis() + 2000;
		while (sleep > System.currentTimeMillis()) {
			if (Player.isMoving())
				sleep = System.currentTimeMillis() + 1000;
			if (Player.getPosition().distanceTo(tile) <= distanceTo)
				break;
			if (c != null && c.active())
				break;
			sleep(200);
		}
	}

	public RSTile[] generateScreenPath(RSTile start, Positionable end) {
		RSTile[] array = new RSTile[] {};
		if (start == null || end == null)
			return array;
		RSTile[] path = PathFinding.generatePath(start, end, true);
		if (path == null)
			return array;
		ArrayList<RSTile> valid = new ArrayList<RSTile>();
		for (final RSTile tile : path) {
			boolean obstruction = false;
			RSObject[] obj = Objects.getAt(tile);
			if (obj.length > 0) {
				if (obj[0] != null && obj[0].getType().equals(TYPES.INTERACTIVE))
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
		array = new RSTile[valid.size()];
		array = valid.toArray(array);
		return array;
	}

	private enum ITEM_OPTIONS {

		DROP, BANK, SMELT, SOFTEN;

	}

	private enum WATER {

		VARROCK_FOUNTAIN(WATER_SOURCE_ID, new RSTile(3209, 3427, 0),
				new RSArea(new RSTile(3208, 3424, 0), new RSTile(3217, 3433, 0))),

		RIMMINGTON_WELL(WATER_SOURCE_ID, new RSTile(2957, 3214, 0),
				new RSArea(new RSTile(2954, 3210, 0), new RSTile(2959, 3215, 0)));

		private int[] ids;
		private RSTile location;
		private RSArea area;

		WATER(int[] ids, RSTile location, RSArea area) {
			this.ids = ids;
			this.location = location;
			this.area = area;
		}

		private int[] getID() {
			return ids;
		}

		private RSTile getLocation() {
			return location;
		}

		private RSArea getArea() {
			return area;
		}

		static WATER getClosestLocation() {

			int distance = Integer.MAX_VALUE;
			WATER closest = null;

			for (WATER water : WATER.values()) {

				int d = Player.getPosition().distanceTo(water.location);

				if (d < distance) {

					closest = water;
					distance = d;

				}

			}

			return closest;

		}

	}

	private enum PICKAXE {

		BRONZE_PICKAXE("Bronze pickaxe", 1, 1),

		IRON_PICKAXE("Iron pickaxe", 1, 1),

		STEEL_PICKAXE("Steel pickaxe", 5, 6),

		BLACK_PICKAXE("Black pickaxe", 10, 11),

		MITHRIL_PICKAXE("Mithril pickaxe", 20, 21),

		ADAMANT_PICKAXE("Adamant pickaxe", 30, 31),

		RUNE_PICKAXE("Rune pickaxe", 40, 41),

		DRAGON_PICKAXE("Dragon pickaxe", 60, 61),

		INFERNAL_PICKAXE("Infernal pickaxe", 60, 61);

		private String name;
		private int ATTACK_REQUIREMENT;
		private int MINING_REQUIREMENT;

		PICKAXE(String name, int ATTACK_REQUIREMENT, int MINING_REQUIREMENT) {
			this.name = name;
			this.ATTACK_REQUIREMENT = ATTACK_REQUIREMENT;
			this.MINING_REQUIREMENT = MINING_REQUIREMENT;
		}

		private String getName() {
			return name;
		}

		private int getAttackRequirement() {
			return ATTACK_REQUIREMENT;
		}

		private int getMiningRequirement() {
			return MINING_REQUIREMENT;
		}

		static PICKAXE getBestPickaxe() {

			int mining = Skills.getActualLevel(SKILLS.MINING);

			PICKAXE pickaxe = PICKAXE.IRON_PICKAXE;

			for (PICKAXE p : PICKAXE.values()) {

				if (p.getMiningRequirement() <= mining)
					pickaxe = p;

			}

			return pickaxe;

		}

		static boolean canWield(PICKAXE pickaxe) {

			return pickaxe.getAttackRequirement() <= Skills.getActualLevel(SKILLS.ATTACK);

		}

		static boolean isWieldingPickaxe() {

			RSItem WEAPON_SLOT = Equipment.getItem(SLOTS.WEAPON);

			if (WEAPON_SLOT == null)
				return false;

			RSItemDefinition d = WEAPON_SLOT.getDefinition();
			if (d == null)
				return false;

			String name = d.getName();
			if (name == null)
				return false;

			for (PICKAXE p : PICKAXE.values()) {

				if (name.equalsIgnoreCase(p.getName()))
					return true;

			}

			return false;

		}

		static boolean isPickaxeInInventory() {

			final String[] PICKAXES = { "Bronze pickaxe", "Iron pickaxe", "Steel pickaxe", "Mithril pickaxe",
					"Adamant pickaxe", "Rune pickaxe", "Dragon pickaxe" };

			return Inventory.getCount(PICKAXES) > 0;

		}

	}

	private enum GEM {

		SAPPHIRE("Uncut sapphire", 1623, 0),

		EMERALD("Uncut emerald", 1621, 0),

		RUBY("Uncut ruby", 1619, 0),

		DIAMOND("Uncut diamond", 1617, 0);

		private String name;
		private int id;
		private int value;

		GEM(String name, int id, int value) {
			this.name = name;
			this.id = id;
			this.value = value;
		}

		private String getName() {
			return name;
		}

		private int getID() {
			return id;
		}

		private int getValue() {
			return value;
		}

		private void setValue(int n) {
			value = n;
		}

	}

	private enum BAR {

		BRONZE_BAR("Bronze bar", 2349, 1, 4, "Copper ore", 1, "Tin ore", 1, 0),

		IRON_BAR("Iron bar", 2351, 15, 6, "Iron ore", 1, null, 0, 0),

		SILVER_BAR("Silver bar", 2355, 20, 7, "Silver ore", 1, null, 0, 0),

		STEEL_BAR("Steel bar", 2353, 30, 8, "Iron ore", 1, "Coal ore", 2, 0),

		GOLD_BAR("Gold bar", 2357, 40, 9, "Gold ore", 1, null, 0, 0),

		MITHRIL_BAR("Mithril bar", 2359, 50, 10, "Coal ore", 4, "Mithril ore", 1, 0),

		ADAMANTITE_BAR("Adamantite bar", 2361, 70, 11, "Coal ore", 6, "Adamantite ore", 1, 0),

		RUNITE_BAR("Runite bar", 2363, 85, 12, "Coal ore", 8, "Runite ore", 1, 0);

		private String BAR_NAME;
		private int id;
		private int level;
		private int child;
		private String ORE_NAME_1;
		private int ORE_QUANTITY_1;
		private String ORE_NAME_2;
		private int ORE_QUANTITY_2;
		private int value;

		BAR(String BAR_NAME, int id, int level, int child, String ORE_NAME_1, int ORE_QUANTITY_1, String ORE_NAME_2,
				int ORE_QUANTITY_2, int value) {
			this.BAR_NAME = BAR_NAME;
			this.id = id;
			this.level = level;
			this.child = child;
			this.ORE_NAME_1 = ORE_NAME_1;
			this.ORE_QUANTITY_1 = ORE_QUANTITY_1;
			this.ORE_NAME_2 = ORE_NAME_2;
			this.ORE_QUANTITY_2 = ORE_QUANTITY_2;
			this.value = value;
		}

		private String getBarName() {
			return BAR_NAME;
		}

		private int getID() {
			return id;
		}

		private int getLevel() {
			return level;
		}

		private int getChild() {
			return child;
		}

		private String getOreName_1() {
			return ORE_NAME_1;
		}

		private int getOreQuantity_1() {
			return ORE_QUANTITY_1;
		}

		private String getOreName_2() {
			return ORE_NAME_2;
		}

		private int getOreQuantity_2() {
			return ORE_QUANTITY_2;
		}

		private int getValue() {
			return value;
		}

		private void setValue(int n) {
			value = n;
		}

	}

	private enum FURNACE {

		LUMBRIDGE(FURNACE_ID, new RSTile(3227, 3255, 0),
				new RSArea(new RSTile(3222, 3252, 0), new RSTile(3229, 3257, 0))),

		AL_KHARID(FURNACE_ID, new RSTile(3275, 3186, 0),
				new RSArea(new RSTile(3274, 3184, 0), new RSTile(3279, 3188, 0))),

		FALADOR(FURNACE_ID, new RSTile(2974, 3369, 0),
				new RSArea(new RSTile(2970, 3368, 0), new RSTile(2975, 3372, 0)));

		private int[] ids;
		private RSTile location;
		private RSArea area;

		FURNACE(int[] ids, RSTile location, RSArea area) {
			this.ids = ids;
			this.location = location;
			this.area = area;
		}

		static FURNACE getClosestLocation() {
			int distance = 10000;
			FURNACE closest = null;
			for (FURNACE furnace : FURNACE.values()) {
				int d = Player.getPosition().distanceTo(furnace.location);
				if (d < distance) {
					closest = furnace;
					distance = d;
				}
			}
			return closest;
		}
	}

	private enum ROCK {

		CLAY(new int[] { 7487, 7454, 7483, 7481, 13456, 13458 }, 434, 1, "Clay", "Clay", 0),

		SOFT_CLAY(new int[] { 7487, 7454, 7483, 7481, 13456, 13458 }, 1761, 1, "Clay", "Soft clay", 0),

		COPPER(new int[] { 13450, 13451, 13452, 7479, 7478, 7480, 14884, 14885, 14886, 13708 }, 436, 1, "Copper",
				"Copper ore", 0),

		TIN(new int[] { 13447, 13448, 13449, 7484, 7486, 14863, 14864, 14883, 13712 }, 438, 1, "Tin", "Tin ore", 0),

		IRON(new int[] { 13444, 13445, 13446, 7487, 7488, 7489, 13710, 13711 }, 440, 15, "Iron", "Iron ore", 0),

		SILVER(new int[] { 13438, 13439, 13440, 13716, 13717 }, 442, 20, "Silver", "Silver ore", 0),

		COAL(new int[] { 14860, 14861, 14862, 13706, 13714 }, 453, 30, "Coal", "Coal ore", 0),

		GOLD(new int[] { 7492, 7490, 13715, 13707 }, 444, 40, "Gold", "Gold ore", 0),

		MITHRIL(new int[] { 14890, 14949, 14948, 13718, 13719 }, 447, 55, "Mithril", "Mithril ore", 0),

		ADAMANTITE(new int[] { 14887, 14889, 13720, 14168 }, 449, 70, "Adamantite", "Adamantite ore", 0),

		RUNE(new int[] { 0 }, 451, 85, "Runite", "Runite ore", 0);

		private int[] ROCK_IDS;
		private int ORE_ID;
		private int level;
		private String ROCK_NAME;
		private String ROCK_ORE;
		private int value = 0;

		ROCK(int[] ROCK_IDS, int ORE_ID, int level, String ROCK_NAME, String ROCK_ORE, int value) {
			this.ROCK_IDS = ROCK_IDS;
			this.ORE_ID = ORE_ID;
			this.level = level;
			this.ROCK_NAME = ROCK_NAME;
			this.ROCK_ORE = ROCK_ORE;
			this.value = value;
		}

		private int[] getRockIDs() {
			return ROCK_IDS;
		}

		private int getOreID() {
			return ORE_ID;
		}

		private int getLevel() {
			return level;
		}

		private String getRockName() {
			return ROCK_NAME;
		}

		private String getOreName() {
			return ROCK_ORE;
		}

		private int getValue() {
			return value;
		}

		private void setValue(int n) {
			value = n;
		}

	}

	private enum MINING_AREA {

		AL_KHARID(new ArrayList<>(asList(MINING_LOCATION.AL_KHARID_COPPER, MINING_LOCATION.AL_KHARID_TIN,
				MINING_LOCATION.AL_KHARID_IRON, MINING_LOCATION.AL_KHARID_SILVER, MINING_LOCATION.AL_KHARID_COAL,
				MINING_LOCATION.AL_KHARID_GOLD, MINING_LOCATION.AL_KHARID_MITHRIL,
				MINING_LOCATION.AL_KHARID_ADAMANTITE))),

		MINING_GUILD(new ArrayList<>(asList(MINING_LOCATION.MINING_GUILD_COAL, MINING_LOCATION.MINING_GUILD_MITHRIL))),

		LUMBRIDGE_SOUTH(
				new ArrayList<>(asList(MINING_LOCATION.LUMBRIDGE_SOUTH_COPPER, MINING_LOCATION.LUMBRIDGE_SOUTH_TIN))),

		LUMBRIDGE_SWAMP(new ArrayList<>(asList(MINING_LOCATION.LUMBRIDGE_SWAMP_COAL,
				MINING_LOCATION.LUMBRIDGE_SWAMP_MITHRIL, MINING_LOCATION.LUMBRIDGE_SWAMP_ADAMANTITE))),

		RIMMINGTON(new ArrayList<>(asList(MINING_LOCATION.RIMMINGTON_CLAY, MINING_LOCATION.RIMMINGTON_COPPER,
				MINING_LOCATION.RIMMINGTON_TIN, MINING_LOCATION.RIMMINGTON_IRON, MINING_LOCATION.RIMMINGTON_GOLD))),

		VARROCK_EAST(new ArrayList<>(asList(MINING_LOCATION.VARROCK_EAST_COPPER, MINING_LOCATION.VARROCK_EAST_TIN,
				MINING_LOCATION.VARROCK_EAST_IRON))),

		VARROCK_WEST(new ArrayList<>(asList(MINING_LOCATION.VARROCK_WEST_CLAY, MINING_LOCATION.VARROCK_WEST_TIN,
				MINING_LOCATION.VARROCK_WEST_IRON, MINING_LOCATION.VARROCK_WEST_SILVER))),

		BARBARIAN_VILLAGE(
				new ArrayList<>(asList(MINING_LOCATION.BARBARIAN_VILLAGE_TIN, MINING_LOCATION.BARBARIAN_VILLAGE_COAL)));

		private ArrayList<MINING_LOCATION> locations;

		MINING_AREA(ArrayList<MINING_LOCATION> locations) {
			this.locations = locations;
		}

		static String getAreaNameFor(MINING_LOCATION location) {
			for (MINING_AREA area : MINING_AREA.values()) {
				ArrayList<MINING_LOCATION> list = area.locations;
				for (MINING_LOCATION loc : list) {
					if (loc.equals(location)) {
						return area.toString();
					}
				}
			}
			return "";
		}
	}

	private enum MINING_LOCATION {

		AL_KHARID_COPPER(ROCK.COPPER, new RSArea(new RSTile(3296, 3314, 0), new RSTile(3299, 3315, 0))),

		AL_KHARID_TIN(ROCK.TIN, new RSArea(new RSTile(3301, 3315, 0), new RSTile(3302, 3316, 0))),

		AL_KHARID_IRON(ROCK.IRON, new RSArea(new RSTile(3294, 3309, 0), new RSTile(3297, 3312, 0))),

		AL_KHARID_SILVER(ROCK.SILVER, new RSArea(new RSTile(3293, 3300, 0), new RSTile(3303, 3314, 0))),

		AL_KHARID_COAL(ROCK.COAL, new RSArea(new RSTile(3300, 3299, 0), new RSTile(3304, 3317, 0))),

		AL_KHARID_GOLD(ROCK.GOLD, new RSArea(new RSTile(3294, 3286, 0), new RSTile(3295, 3288, 0))),

		AL_KHARID_MITHRIL(ROCK.MITHRIL, new RSArea(new RSTile(3303, 3303, 0), new RSTile(3305, 3305, 0))),

		AL_KHARID_ADAMANTITE(ROCK.ADAMANTITE, new RSArea(new RSTile(3298, 3316, 0), new RSTile(3300, 3318, 0))),

		//

		MINING_GUILD_COAL(ROCK.COAL, new RSArea(new RSTile(3026, 9732, 0), new RSTile(3055, 9756, 0))),

		MINING_GUILD_MITHRIL(ROCK.MITHRIL,
				new RSArea(new RSTile[] { new RSTile(3044, 9732, 0), new RSTile(3044, 9735, 0),
						new RSTile(3047, 9735, 0), new RSTile(3047, 9736, 0), new RSTile(3049, 9739, 0),
						new RSTile(3050, 9741, 0), new RSTile(3054, 9741, 0), new RSTile(3054, 9738, 0),
						new RSTile(3053, 9736, 0), new RSTile(3049, 9735, 0), new RSTile(3048, 9732, 0) })),

		LUMBRIDGE_SOUTH_COPPER(ROCK.COPPER, new RSArea(new RSTile(3227, 3143, 0), new RSTile(3231, 3149, 0))),

		LUMBRIDGE_SOUTH_TIN(ROCK.TIN, new RSArea(new RSTile(3221, 3145, 0), new RSTile(3226, 3149, 0))),

		//

		LUMBRIDGE_SWAMP_COAL(ROCK.COAL, new RSArea(new RSTile(3143, 3148, 0), new RSTile(3147, 3153, 0))),

		LUMBRIDGE_SWAMP_MITHRIL(ROCK.MITHRIL, new RSArea(new RSTile(3142, 3144, 0), new RSTile(3150, 3148, 0))),

		LUMBRIDGE_SWAMP_ADAMANTITE(ROCK.ADAMANTITE, new RSArea(new RSTile(3145, 3147, 0), new RSTile(3149, 3148, 0))),

		//

		RIMMINGTON_CLAY(ROCK.CLAY, new RSArea(new RSTile(2985, 3238, 0), new RSTile(2988, 3241, 0))),

		RIMMINGTON_SOFT_CLAY(ROCK.SOFT_CLAY, new RSArea(new RSTile(2985, 3238, 0), new RSTile(2988, 3241, 0))),

		RIMMINGTON_COPPER(ROCK.COPPER, new RSArea(new RSTile(2975, 3244, 0), new RSTile(2980, 3249, 0))),

		RIMMINGTON_TIN(ROCK.TIN, new RSArea(new RSTile(2983, 3234, 0), new RSTile(2987, 3238, 0))),

		RIMMINGTON_IRON(ROCK.IRON, new RSArea(new RSTile(2967, 3236, 0), new RSTile(2972, 3243, 0))),

		RIMMINGTON_GOLD(ROCK.GOLD, new RSArea(new RSTile(2974, 3232, 0), new RSTile(2978, 3235, 0))),

		//

		VARROCK_EAST_COPPER(ROCK.COPPER, new RSArea(new RSTile(3284, 3360, 0), new RSTile(3291, 3365, 0))),

		VARROCK_EAST_TIN(ROCK.TIN, new RSArea(new RSTile(3280, 3361, 0), new RSTile(3283, 3366, 0))),

		VARROCK_EAST_IRON(ROCK.IRON, new RSArea(new RSTile(3284, 3367, 0), new RSTile(3289, 3371, 0))),

		//

		VARROCK_WEST_CLAY(ROCK.CLAY, new RSArea(new RSTile(3178, 3369, 0), new RSTile(3182, 3373, 0))),

		VARROCK_WEST_SOFT_CLAY(ROCK.SOFT_CLAY, new RSArea(new RSTile(3178, 3369, 0), new RSTile(3182, 3373, 0))),

		VARROCK_WEST_TIN(ROCK.TIN, new RSArea(new RSTile(3180, 3374, 0), new RSTile(3184, 3378, 0))),

		VARROCK_WEST_IRON(ROCK.IRON, new RSArea(new RSTile(3174, 3365, 0), new RSTile(3177, 3369, 0))),

		VARROCK_WEST_SILVER(ROCK.SILVER, new RSArea(new RSTile(3175, 3364, 0), new RSTile(3178, 3370, 0))),

		//

		BARBARIAN_VILLAGE_TIN(ROCK.TIN, new RSArea(new RSTile(3078, 3417, 0), new RSTile(3085, 3423, 0))),

		BARBARIAN_VILLAGE_COAL(ROCK.COAL, new RSArea(new RSTile(3080, 3420, 0), new RSTile(3085, 3423, 0)));

		//

		private ROCK rock;
		private RSArea area;

		MINING_LOCATION(ROCK rock, RSArea area) {
			this.rock = rock;
			this.area = area;
		}

		private ROCK getRock() {
			return rock;
		}

		private RSArea getArea() {
			return area;
		}

		/*
		 * Returns the closest available mining location to your player's
		 * current location.
		 */
		static MINING_LOCATION getUsableClosestLocation() {
			int level = SKILLS.MINING.getActualLevel();
			if (level >= 1) {
				int distance = 10000;
				MINING_LOCATION closest = null;
				for (MINING_LOCATION location : MINING_LOCATION.values()) {
					int d = location.area.getRandomTile().distanceTo(Player.getPosition());
					if (d < distance) {
						if (level >= location.getRock().getLevel()) {
							closest = location;
							distance = d;
						}
					}
				}
				return closest;
			}
			return null;
		}

		/*
		 * Returns the rock in the array that is required to mine in order to
		 * have the required ores for the requested bar
		 */
		static MINING_LOCATION getRockForMakingBars(ArrayList<MINING_LOCATION> locations, BAR bar) {

			if (locations.size() == 1) {

				return locations.get(0);

			} else if (locations.size() == 2) {

				double space = 28.0 - (double) Inventory.getCount(PICKAXES);
				int quantity = (int) Math
						.floor((space / ((double) bar.getOreQuantity_1() + (double) bar.getOreQuantity_2())));
				int amount1 = quantity * bar.getOreQuantity_1();
				int amount2 = quantity * bar.getOreQuantity_2();

				if (Inventory.getCount(bar.getOreName_1()) < amount1) {

					for (MINING_LOCATION location : locations) {
						if (bar.getOreName_1().equalsIgnoreCase(location.getRock().getOreName()))
							return location;
					}

				} else if (Inventory.getCount(bar.getOreName_2()) < amount2) {

					for (MINING_LOCATION location : locations) {
						if (bar.getOreName_2().equalsIgnoreCase(location.getRock().getOreName()))
							return location;
					}

				} else {

					return getHighestPossible(locations);

				}
			}

			System.out.println("You must only have a maximum of (2) rocks added!");
			return null;

		}

		/*
		 * Returns the highest possible location in the array list that your
		 * player can use.
		 */
		static MINING_LOCATION getHighestPossible(ArrayList<MINING_LOCATION> locations) {

			if (locations.size() == 1)
				return locations.get(0);

			MINING_LOCATION location = null;
			int level = SKILLS.MINING.getActualLevel();
			int highest = 0;

			for (MINING_LOCATION m : locations) {

				if (CURRENT_LOCATION != null) {

					if (m.getRock().getLevel() > CURRENT_LOCATION.getRock().getLevel()) {

						if (level >= m.getRock().getLevel() && m.getRock().getLevel() > highest) {

							RSObject[] objects = getObjectsAtLocation(m);

							if (objects.length > 0) {

								location = m;
								highest = m.getRock().getLevel();

							}

						}

					}

				} else {

					if (level >= m.getRock().getLevel() && m.getRock().getLevel() > highest) {

						RSObject[] objects = getObjectsAtLocation(m);

						if (objects.length > 0) {

							location = m;
							highest = m.getRock().getLevel();

						}

					}

				}

			}

			if (location == null) {

				if (CURRENT_LOCATION != null)
					return CURRENT_LOCATION;

				return locations.get(0);
			}

			return location;
		}
	}

	private boolean login(Account account) {

		if (account == null)
			return true;

		if (Login.getLoginState() == Login.STATE.INGAME || account == null)
			return false;

		long timer = System.currentTimeMillis() + 30000;

		status = "Logging in " + account.getDisplayName();

		while (timer > System.currentTimeMillis()) {

			Login.login(account.getEmailName(), account.getPassword());

			sleep(1000);

			if (clickHereToPlay()) {
				status = "Logged In!";
				return true;
			}

			sleep(1000);

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
					sleep((long) General.randomSD(300, 100));
					return Login.getLoginState() != Login.STATE.WELCOMESCREEN;
				}
			}, 2000);

		}

		return Login.getLoginState() == Login.STATE.INGAME && Login.getLoginState() != Login.STATE.WELCOMESCREEN;

	}

	private class Account {

		private int age;
		private String emailName;
		private String emailDomain;
		private String display;
		private String password;
		private int state;

		public Account(int age, String emailName, String emailDomain, String display, String password, int state) {
			this.age = age;
			this.emailName = emailName;
			this.emailDomain = emailDomain;
			this.display = display;
			this.password = password;
			this.state = state;
		}

		private int getAge() {
			return age;
		}

		private String getEmailName() {
			return emailName;
		}

		private String getEmailDomain() {
			return emailDomain;
		}

		private String getDisplayName() {
			return display;
		}

		private void setDisplayName(String s) {
			display = s;
		}

		private String getPassword() {
			return password;
		}

		private int getState() {
			return state;
		}

		private void setState(int n) {
			state = n;
		}

	}

	private Account getAccount() {

		ArrayList<Account> accounts = loadAccounts("USA Miner");

		if (accounts.isEmpty())
			return null;

		if (Login.getLoginState() == Login.STATE.INGAME) {

			String name = Player.getRSPlayer().getName();

			if (name != null && name.length() > 0) {

				for (Account a : accounts) {

					if (a.getDisplayName().equalsIgnoreCase(name)) {

						println("We are currently logged on account \"" + a.getDisplayName() + "\"");

						return a;

					}

				}

				println("Could not find account named \"" + name + "\" in '\\USA Miner\\accounts.txt'");

			}

		} else {

			for (Account a : accounts) {

				if (a.getState() == 0) {

					println("Selected \"" + a.getDisplayName() + "\" from the accounts file.");

					return a;

				}

			}

			println("Could not find any account with \" State 0 \" in '\\USA Miner\\accounts.txt'");

		}

		return null;

	}

	private boolean setState(int state, Account account) {

		if (account == null)
			return false;

		ArrayList<Account> accounts = loadAccounts("USA Miner");

		if (accounts.isEmpty())
			return false;

		for (Account a : accounts) {

			if (a.getDisplayName().equalsIgnoreCase(account.getDisplayName())) {

				a.setState(state);

				if (state == 0) {

					println("Set state to 'LOGGED OFF' on " + a.getDisplayName());

				} else {

					println("Set state to 'IN GAME' on " + a.getDisplayName());

				}

				long timer = System.currentTimeMillis() + 5000;

				while (timer > System.currentTimeMillis()) {

					if (saveAccounts("USA Miner", "accounts", accounts))
						break;

					sleep(500);

				}

				return true;

			}

		}

		return false;
	}

	private boolean saveAccounts(String directory, String name, ArrayList<Account> accounts) {

		try {

			File location = new File(Util.getWorkingDirectory() + "/" + directory);
			File file = new File(location.toString() + "/" + name + ".txt");

			if (file.exists()) {

				try {

					FileWriter fw = new FileWriter(file);

					BufferedWriter bw = new BufferedWriter(fw);

					boolean first = true;

					for (Account a : accounts) {

						if (first) {
							first = false;
						} else {
							bw.newLine();
						}

						bw.write("| Display " + a.getDisplayName() + " | Age " + a.getAge() + " | Email "
								+ a.getEmailName() + " | Password " + a.getPassword() + " | State " + a.getState()
								+ " |");

					}

					bw.close();

					return true;

				} catch (FileNotFoundException ex) {

					System.out.println("Unable to open file '" + name + "'");

				}

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		return false;

	}

	private ArrayList<Account> loadAccounts(String directory) {

		ArrayList<Account> a = new ArrayList<Account>();

		try {

			File location = new File(Util.getWorkingDirectory() + "/" + directory);
			File file = new File(location.toString() + "/accounts.txt");

			if (file.exists()) {

				String line = null;

				try {

					FileReader fr = new FileReader(file);

					BufferedReader br = new BufferedReader(fr);

					while ((line = br.readLine()) != null) {

						String[] data = line.split("\\|");

						if (data.length == 6) {

							String display = data[1].replaceAll(" Display ", "").trim();
							int age = Integer.parseInt(data[2].replaceAll(" Age ", "").trim());
							String email = data[3].replaceAll(" Email ", "").trim();
							String password = data[4].replaceAll(" Password ", "").trim();
							int state = Integer.parseInt(data[5].replaceAll(" State ", "").trim());

							a.add(new Account(age, email, "", display, password, state));

						}

					}

					br.close();

				} catch (FileNotFoundException ex) {

					System.out.println("Unable to open file '\\USA Miner\\accounts.txt'");

				}

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		return a;

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

	private String toTitleCase(String givenString) {
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

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setRenderingHints(rh);

		long currentTime = System.currentTimeMillis();
		long time = currentTime - startTime;
		int barsPerHour = (int) (totalBars * 3600000D / time);
		int oresPerHour = (int) (totalOres * 3600000D / time);
		int profitPerHour = (int) (totalProfit * 3600000D / time);
		int xpGained = Skills.getXP(SKILLS.MINING) - startXP;
		int xpPerHour = (int) (xpGained * 3600000D / time);
		int currentLVL = Skills.getActualLevel(SKILLS.MINING);

		Color background = new Color(24, 36, 82, 150);
		g2.setColor(background);
		g2.fillRoundRect(235, 322, 261, 150, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(235, 322, 261, 150, 10, 10);

		int x = 240;
		int y = 336;
		int spacing = 15;
		g2.setFont(new Font("Tahoma", Font.BOLD, 12));

		g2.drawString("USA Miner                 v" + version, x + 85, y);
		g2.drawLine(235, 340, 495, 340);
		y += spacing + 3;

		g2.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g2.drawString("Status: " + status, x, y);
		y += spacing;

		if (ITEM_ACTION == ITEM_OPTIONS.SMELT) {
			g2.drawString("Bars Gained: " + addCommasToNumericString(Integer.toString(totalBars)) + " ("
					+ addCommasToNumericString(Integer.toString(barsPerHour)) + "/hr)", x, y);
			y += spacing;
		}

		if (ITEM_ACTION == ITEM_OPTIONS.SOFTEN) {
			g2.drawString("Soft Clay Gained: " + addCommasToNumericString(Integer.toString(totalOres)) + " ("
					+ addCommasToNumericString(Integer.toString(oresPerHour)) + "/hr)", x, y);
		} else {
			g2.drawString("Ores Gained: " + addCommasToNumericString(Integer.toString(totalOres)) + " ("
					+ addCommasToNumericString(Integer.toString(oresPerHour)) + "/hr)", x, y);
		}
		y += spacing;

		g2.drawString("Profit: " + addCommasToNumericString(Integer.toString(totalProfit)) + " ("
				+ addCommasToNumericString(Integer.toString(profitPerHour)) + "/hr)", x, y);
		y += spacing;
		g2.drawString("Mining Level: " + currentLVL + " (+" + (currentLVL - startLVL) + ")", x, y);
		y += spacing;
		g2.drawString("XP Gained: " + addCommasToNumericString(Integer.toString(xpGained)) + " ("
				+ addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", x, y);
		y += 6;

		int xpTNL = Skills.getXPToNextLevel(SKILLS.MINING);
		int percentTNL = Skills.getPercentToNextLevel(SKILLS.MINING);
		long TTNL = 0;
		if (xpPerHour > 0) {
			TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
		}
		int percentFill = (250 * percentTNL) / 100;
		g2.setColor(Color.RED);
		g2.fillRoundRect(x, y, 250, 16, 5, 5);
		Color green = new Color(10, 150, 10);
		g2.setColor(green);
		g2.fillRoundRect(x, y, percentFill, 16, 5, 5);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(x, y, 250, 16, 5, 5);
		g2.drawString(addCommasToNumericString(Integer.toString(xpTNL)) + " xp to " + (currentLVL + 1) + " | "
				+ Timing.msToString(TTNL), x + 40, y + 13);

		if (ITEM_ACTION != ITEM_OPTIONS.DROP) {

			g2.drawImage(TRANSFER_BANK_IMAGE, 535, 406, null);

			g2.drawString("Bank Value: " + addCommasToNumericString(Integer.toString(bankValue)) + " gp", 555, 455);

		}

		if (PAINT_PATH != null) {

			g2.setColor(Color.GREEN);

			for (RSTile tile : PAINT_PATH) {

				if (tile.isOnScreen())
					g2.drawPolygon(Projection.getTileBoundsPoly(tile, 0));

				Point point = Projection.tileToMinimap(tile);
				g2.fillRect((int) point.getX(), (int) point.getY(), 2, 2);

			}

		}

		if (CURRENT_LOCATION != null && CURRENT_LOCATION.getArea().contains(Player.getPosition())) {

			g2.setColor(new Color(255, 255, 255, 50));

			RSTile[] tiles = CURRENT_LOCATION.area.getAllTiles();
			for (RSTile tile : tiles) {
				if (tile.isOnScreen())
					g2.drawPolygon(Projection.getTileBoundsPoly(tile, 0));
				Point point = Projection.tileToMinimap(tile);
				g2.fillRect((int) point.getX(), (int) point.getY(), 2, 2);
			}

			g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
			g2.setColor(Color.WHITE);

			if (rocks != null && rocks.length > 0) {

				for (RSObject rock : rocks) {

					if (rock != null && rock.isOnScreen()) {

						RSModel m = rock.getModel();

						if (m != null) {

							Polygon poly = m.getEnclosedArea();

							if (poly != null) {

								Point p = Projection.tileToScreen(rock, 15);
								g2.drawString(CURRENT_LOCATION.getRock().getRockName(), p.x - 10, p.y);
								g2.drawPolygon(poly);

							}

						}

					}

				}

			}

			if (objectHistory.size() > 0) {

				g2.drawString(
						"Tracking " + objectHistory.size() + " " + CURRENT_LOCATION.getRock().getRockName() + " rocks.",
						5, 336);

				g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
				g2.setColor(Color.CYAN);

				for (ObjectHistory o : objectHistory) {

					Point p = Projection.tileToScreen(o.getPosition(), 200);

					x = p.x - 13;
					y = p.y;
					spacing = 12;

					long timer = (o.getTimeDissapeared() + o.getRespawnTime()) - currentTime;

					if (timer > 0) {

						g2.drawString("" + timer + "ms", x, y);

					} else {

						if (o.getRespawnTime() > 0)

							g2.drawString("" + o.getRespawnTime() + "ms", x, y);

					}

					// y -= spacing;
					//
					// if (o.getTimeDissapeared() != 0) {
					//
					// g2.drawString("" + (System.currentTimeMillis() -
					// o.getTimeDissapeared()) + " ms", x, y);
					// y -= spacing;
					//
					// }
					//
					// if (o.getTimeAppeared() != 0) {
					//
					// g2.drawString("" + (System.currentTimeMillis() -
					// o.getTimeAppeared()) + " ms", x, y);
					// y -= spacing;
					//
					// }

				}

			}

			if (next != null) {

				g2.setColor(Color.RED);

				RSModel m = next.getModel();

				if (m != null) {

					Polygon p = m.getEnclosedArea();
					g2.drawPolygon(p);

				}

			}

			if (current != null) {

				g2.setColor(Color.GREEN);

				RSModel m = current.getModel();

				if (m != null) {

					Polygon p = m.getEnclosedArea();
					g2.drawPolygon(p);

				}

			}

			if (next_to_spawn != null) {

				g2.setColor(Color.CYAN);

				RSTile position = next_to_spawn.getPosition();

				if (position != null)
					g2.drawPolygon(Projection.getTileBoundsPoly(position, 0));

			}

		}

	}

	public class gui extends JFrame {

		private JPanel contentPane;

		// MAIN

		private JScrollPane scrollPaneLocations;
		private DefaultListModel<MINING_LOCATION> modelLocations;
		private JList<MINING_LOCATION> locationList;
		private JComboBox actionBox;
		private JCheckBox stationaryBox;
		private JCheckBox trackingBox;
		private JCheckBox responderBox;
		private JCheckBox hoverBox;
		private JCheckBox closestBox;
		private JCheckBox anticipatedBox;

		// WORLD HOPPING
		private JSpinner playerSpinner;
		private JComboBox worldBox;

		// MULE
		private JTextField usernameText;
		private JTextField worldText;
		private JTextField locationText;
		private JCheckBox automaticTransferBox;
		private JTextField wealthText;

		/**
		 * Create the frame.
		 */
		public gui() {

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 462, 496);
			contentPane = new JPanel();
			contentPane.setBackground(new Color(102, 204, 204));
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblUsaMiner = new JLabel("USA Miner");
			lblUsaMiner.setBounds(21, 11, 200, 50);
			lblUsaMiner.setVerticalAlignment(SwingConstants.TOP);
			lblUsaMiner.setHorizontalAlignment(SwingConstants.CENTER);
			lblUsaMiner.setForeground(Color.BLACK);
			lblUsaMiner.setFont(new Font("Segoe UI", Font.BOLD, 36));
			contentPane.add(lblUsaMiner);

			JLabel lblV = new JLabel(version);
			lblV.setBounds(231, 36, 26, 16);
			lblV.setVerticalAlignment(SwingConstants.BOTTOM);
			lblV.setForeground(Color.BLACK);
			lblV.setFont(new Font("Segoe UI", Font.BOLD, 11));
			contentPane.add(lblV);

			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.setBounds(21, 71, 405, 335);
			tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			contentPane.add(tabbedPane);

			JPanel main = new JPanel();
			tabbedPane.addTab("Main", null, main, null);
			main.setLayout(null);

			scrollPaneLocations = new JScrollPane();
			modelLocations = new DefaultListModel();

			for (MINING_LOCATION m : MINING_LOCATION.values()) {
				modelLocations.addElement(m);
			}

			JLabel lblTest = new JLabel("Mining Locations");
			lblTest.setFont(new Font("Segoe UI", Font.BOLD, 12));
			lblTest.setHorizontalAlignment(SwingConstants.CENTER);
			lblTest.setBounds(10, 10, 200, 16);
			main.add(lblTest);

			locationList = new JList(modelLocations);
			locationList.setFont(new Font("Segoe UI", Font.PLAIN, 11));

			scrollPaneLocations.setViewportView(locationList);
			main.add(scrollPaneLocations);
			scrollPaneLocations.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			scrollPaneLocations.setBounds(10, 30, 200, 260);
			scrollPaneLocations.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));

			JSeparator separator_2 = new JSeparator();
			separator_2.setBounds(223, 189, 177, 1);
			main.add(separator_2);

			JSeparator separator = new JSeparator();
			separator.setOrientation(SwingConstants.VERTICAL);
			separator.setBounds(223, 0, 1, 301);
			main.add(separator);

			JLabel lblOtherOptions = new JLabel("Options");
			lblOtherOptions.setHorizontalAlignment(SwingConstants.CENTER);
			lblOtherOptions.setFont(new Font("Segoe UI", Font.BOLD, 12));
			lblOtherOptions.setBounds(226, 10, 174, 16);
			main.add(lblOtherOptions);

			actionBox = new JComboBox(ITEM_OPTIONS.values());
			actionBox.setBounds(244, 39, 143, 20);
			main.add(actionBox);

			stationaryBox = new JCheckBox("Use Stationary Position");
			stationaryBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			stationaryBox.setBounds(240, 66, 160, 23);
			main.add(stationaryBox);

			trackingBox = new JCheckBox("Use Object Tracking");
			trackingBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			trackingBox.setBounds(240, 92, 160, 23);
			main.add(trackingBox);

			responderBox = new JCheckBox("Use Auto Responder V2");
			responderBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			responderBox.setBounds(240, 118, 160, 23);
			main.add(responderBox);

			hoverBox = new JCheckBox("Always Hover");
			hoverBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			hoverBox.setBounds(233, 218, 160, 23);
			main.add(hoverBox);

			closestBox = new JCheckBox("Always Use Closest");
			closestBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			closestBox.setBounds(233, 244, 160, 23);
			main.add(closestBox);

			anticipatedBox = new JCheckBox("Always Go To Anticipated");
			anticipatedBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			anticipatedBox.setBounds(233, 270, 160, 23);
			main.add(anticipatedBox);

			JLabel lblAbclOverride = new JLabel("ABCL Override");
			lblAbclOverride.setHorizontalAlignment(SwingConstants.CENTER);
			lblAbclOverride.setFont(new Font("Segoe UI", Font.BOLD, 12));
			lblAbclOverride.setBounds(223, 196, 177, 16);
			main.add(lblAbclOverride);

			JPanel hopping = new JPanel();
			tabbedPane.addTab("World Hopping", null, hopping, null);
			hopping.setLayout(null);

			JLabel lblMaximumPlayers = new JLabel("Maximum Players:");
			lblMaximumPlayers.setHorizontalAlignment(SwingConstants.LEFT);
			lblMaximumPlayers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			lblMaximumPlayers.setBounds(10, 15, 103, 20);
			hopping.add(lblMaximumPlayers);

			playerSpinner = new JSpinner();
			playerSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			playerSpinner.setBounds(116, 15, 35, 20);
			hopping.add(playerSpinner);

			JLabel lblWorlds = new JLabel("Worlds:");
			lblWorlds.setHorizontalAlignment(SwingConstants.LEFT);
			lblWorlds.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			lblWorlds.setBounds(10, 55, 47, 20);
			hopping.add(lblWorlds);

			worldBox = new JComboBox();
			worldBox.setModel(new DefaultComboBoxModel(new String[] { "Free To Play", "Members" }));
			worldBox.setBounds(58, 56, 93, 20);
			hopping.add(worldBox);

			JSeparator separator_3 = new JSeparator();
			separator_3.setBounds(0, 96, 400, 2);
			hopping.add(separator_3);

			JCheckBox timeBox = new JCheckBox("Change Worlds by Time (Minutes)");
			timeBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			timeBox.setBounds(10, 109, 215, 23);
			hopping.add(timeBox);

			JSlider minutesSlider = new JSlider();
			minutesSlider.setMajorTickSpacing(60);
			minutesSlider.setMinorTickSpacing(30);
			minutesSlider.setMaximum(600);
			minutesSlider.setPaintTicks(true);
			minutesSlider.setPaintLabels(true);
			minutesSlider.setBounds(32, 142, 328, 45);
			hopping.add(minutesSlider);

			JSeparator separator_4 = new JSeparator();
			separator_4.setBounds(0, 208, 400, 2);
			hopping.add(separator_4);

			JPanel mule = new JPanel();
			tabbedPane.addTab("Master", null, mule, null);
			mule.setLayout(null);

			JLabel lblMuleUsername = new JLabel("Mule Username:");
			lblMuleUsername.setHorizontalAlignment(SwingConstants.LEFT);
			lblMuleUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			lblMuleUsername.setBounds(10, 15, 94, 20);
			mule.add(lblMuleUsername);

			usernameText = new JTextField();
			usernameText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			usernameText.setBounds(102, 16, 100, 20);
			mule.add(usernameText);
			usernameText.setColumns(10);

			JLabel lblMuleWorld = new JLabel("Mule World:");
			lblMuleWorld.setHorizontalAlignment(SwingConstants.LEFT);
			lblMuleWorld.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			lblMuleWorld.setBounds(10, 46, 94, 20);
			mule.add(lblMuleWorld);

			worldText = new JTextField();
			worldText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			worldText.setColumns(10);
			worldText.setBounds(102, 47, 100, 20);
			mule.add(worldText);

			locationText = new JTextField();
			locationText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			locationText.setColumns(10);
			locationText.setText("(9999, 9999, 0)");
			locationText.setBounds(102, 78, 100, 20);
			mule.add(locationText);

			JLabel lblMuleLocation = new JLabel("Mule Location:");
			lblMuleLocation.setHorizontalAlignment(SwingConstants.LEFT);
			lblMuleLocation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			lblMuleLocation.setBounds(10, 77, 94, 20);
			mule.add(lblMuleLocation);

			JSeparator separator_5 = new JSeparator();
			separator_5.setBounds(0, 115, 400, 2);
			mule.add(separator_5);

			automaticTransferBox = new JCheckBox("Automatic Transfer when wealth is over");
			automaticTransferBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			automaticTransferBox.setBounds(10, 133, 230, 23);
			mule.add(automaticTransferBox);

			wealthText = new JTextField();
			wealthText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			wealthText.setBounds(245, 135, 105, 20);
			mule.add(wealthText);
			wealthText.setColumns(10);

			JLabel lblGp = new JLabel("gp.");
			lblGp.setBounds(360, 133, 25, 23);
			mule.add(lblGp);

			JSeparator separator_6 = new JSeparator();
			separator_6.setBounds(0, 174, 400, 2);
			mule.add(separator_6);

			JButton btnNewButton = new JButton("Start");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					for (Object o : locationList.getSelectedValues()) {
						println("Added " + o.toString());
						ORDERS.add(MINING_LOCATION.valueOf(o.toString()));
					}

					ITEM_ACTION = ITEM_OPTIONS.valueOf(actionBox.getSelectedItem().toString());
					println("Action is " + ITEM_ACTION.toString());

					STATIONARY = stationaryBox.isSelected();
					STATIONARY_POSITION = Player.getPosition();
					if (STATIONARY)
						println("We will mine at a stationary position recorded as " + STATIONARY_POSITION);

					useObjectTracking = trackingBox.isSelected();

					useAutoResponder = responderBox.isSelected();
					if (useAutoResponder)
						println("We are using Auto Responder V2");

					always_hover = hoverBox.isSelected();
					if (always_hover)
						println("We will always hover over the next rock");

					always_use_closest = closestBox.isSelected();
					if (always_use_closest)
						println("We will use the closest rock");

					always_go_to_anticipated = anticipatedBox.isSelected();
					if (always_go_to_anticipated)
						println("We will always go to the next anticipated rock");

					MAX_PLAYER_COUNT = (int) playerSpinner.getValue();
					if (MAX_PLAYER_COUNT != 0)
						println("We will change worlds if " + MAX_PLAYER_COUNT + " or greater Players are detected");

					int n = worldBox.getSelectedIndex();
					if (n == 1) {
						membersWorlds = true;
						println("Using Members worlds");
					} else {
						membersWorlds = false;
						println("Using Free To Play worlds");
					}

					changeWorldsByTime = timeBox.isSelected();
					changeWorldsTime = (int) minutesSlider.getValue();
					if (changeWorldsByTime)
						println("We will change worlds every " + changeWorldsTime + " minutes +/- (5) minutes");

					if (usernameText.getText() != null && !usernameText.getText().isEmpty()) {
						MULE_USERNAME = usernameText.getText();
						MULE_WORLD = Integer.parseInt(worldText.getText());
						String str = locationText.getText();
						str = str.substring(1, str.length() - 1).replaceAll(" ", "");
						String[] split = str.split(",");
						MULE_LOCATION = new RSTile(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
								Integer.parseInt(split[2]));
						println("Mule username is " + MULE_USERNAME + " located in World " + MULE_WORLD + " on RSTile "
								+ MULE_LOCATION);
					}

					automaticTransfer = automaticTransferBox.isSelected();

					if (automaticTransfer) {
						transferValue = Integer.parseInt(wealthText.getText());
						println("We will automatically transfer all goods when we have accumulated over "
								+ transferValue + " gp");
					}

					gui_is_up = false;
					g.dispose();

				}
			});
			btnNewButton.setBounds(159, 419, 130, 23);
			contentPane.add(btnNewButton);

			JButton btnNewButton_1 = new JButton("Load Settings");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			btnNewButton_1.setBounds(21, 419, 115, 23);
			contentPane.add(btnNewButton_1);

			JButton btnSaveSettings = new JButton("Save Settings");
			btnSaveSettings.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			btnSaveSettings.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			btnSaveSettings.setBounds(311, 419, 115, 23);
			contentPane.add(btnSaveSettings);

		}
	}

	@Override
	public void onEnd() {

		if (tracker != null)
			tracker.setStop(true);

		// setState(0, account);

	}

	public static class WorldHopperV2 {

		private final static int WORLD_SWITCHER_MASTER = 69;
		private final static int WORLD_SWITCHER_CLOSE = 3;
		private final static int WORLD_SWITCHER_MASTER_WORLDS_INTERFACE = 4;
		private final static int WORLD_SWITCHER_SCROLL_BAR = 15;
		private final static int WORLD_SWITCHER_UP_ARROW = 4;
		private final static int WORLD_SWITCHER_DOWN_ARROW = 5;
		private final static int WORLD_LIST_INTERFACE = 7;
		private final static int LOGOUT_MENU_MASTER = 182;
		private final static int LOGOUT_MENU_WORLD_SWITCHER_BUTTON = 5;

		private final static Point COLUMN_1_WORLD = new Point(236, 57);
		private final static int VERTICAL_DISTANCE = 24;
		private final static int HORIZONTAL_DISTANCE = 93;
		private final static Rectangle CLICK_TO_SWITCH = new Rectangle(10, 468, 90, 20);

		private final static int[] ALL_WORLDS = new int[] { 301, 302, 303, 304, 305, 306, 308, 309, 310, 311, 312, 313,
				314, 316, 317, 318, 319, 320, 321, 322, 325, 326, 327, 328, 329, 330, 333, 334, 335, 336, 337, 338, 341,
				342, 343, 344, 345, 346, 349, 350, 351, 353, 354, 357, 358, 359, 360, 361, 362, 365, 366, 368, 369, 370,
				373, 374, 375, 376, 377, 378, 381, 382, 383, 384, 385, 386, 393, 394 };

		private final static int[] MEMBERS_WORLDS = new int[] { 302, 303, 304, 305, 306, 309, 310, 311, 312, 313, 314,
				317, 319, 320, 321, 322, 327, 328, 329, 330, 333, 334, 336, 338, 341, 342, 343, 344, 345, 346, 349, 350,
				351, 354, 357, 358, 359, 360, 362, 368, 369, 370, 374, 375, 376, 377, 378 };

		private final static int[] FREE_WORLDS = new int[] { 301, 308, 316, 326, 335, 381, 382, 383, 384, 393, 394 };

		private final static int[] PVP_WORLDS = new int[] { 318, 325, 337 };

		private final static int[] COLUMN_1_WORLDS = new int[] { 301, 302, 303, 304, 305, 306, 308, 309, 310, 311, 312,
				313, 314, 316, 317, 318, 319, 320 };
		private final static int[] COLUMN_2_WORLDS = new int[] { 321, 322, 325, 326, 327, 328, 329, 330, 333, 334, 335,
				336, 337, 338, 341, 342, 343, 344 };
		private final static int[] COLUMN_3_WORLDS = new int[] { 345, 346, 349, 350, 351, 353, 354, 357, 358, 359, 360,
				361, 362, 365, 366, 368, 369 };
		private final static int[] COLUMN_4_WORLDS = new int[] { 370, 373, 374, 375, 376, 377, 378, 381, 382, 383, 384,
				385, 386, 393, 394 };

		/**
		 * 
		 * @param true
		 *            to sort by lowest first
		 * @return List of <World> sorted by the population
		 */
		private static List<World> sortWorldsByPopulation(boolean lowest) {

			if (!isWorldSwitcherOpen())
				return null;

			List<World> worlds = new ArrayList<World>();

			for (int w : ALL_WORLDS) {
				World e = getWorld(w);
				if (e != null)
					worlds.add(e);
			}

			if (lowest) {
				Collections.sort(worlds, World.COMPARE_BY_PLAYERS_LOW_TO_HIGH);
			} else {
				Collections.sort(worlds, World.COMPARE_BY_PLAYERS_HIGH_TO_LOW);
			}

			return worlds;
		}

		/**
		 * Formats the world to a 3-digit format
		 * 
		 * @param world
		 *            , i.e. (1 -> 301, 334 -> 334)
		 * @return world in a 3-digit format
		 */
		private static int formatWorld(int world) {
			if (world > 300)
				return world;
			if (world < 300)
				return world + 300;
			return 0;
		}

		/**
		 * Changes to the desired world using the in-game world switcher or
		 * logout menu depending on the game state
		 * 
		 * @param world
		 * @param timeout
		 *            : duration the bot should click to the world
		 * @return true if we successfully changed worlds
		 */
		private static boolean changeWorld(int world) {

			if (getCurrentWorld() == world)
				return true;

			int w = formatWorld(world);

			if (Login.getLoginState() == Login.STATE.INGAME) {
				if (Player.getRSPlayer().isInCombat())
					return false;
				if (openWorldSwitcher()) {
					if (Math.random() < 0.5) {
						if (clickToWorld(w)) {
							if (selectWorld(w))
								return true;
						}
					} else {
						if (scrollToWorld(w)) {
							if (selectWorld(w))
								return true;
						}
					}
				}
			} else {
				if (selectWorld(w)) {
					if (Login.login())
						return true;
				}
			}
			return false;
		}

		/**
		 * Will scroll to the desired world if the world switcher menu is open
		 * using the mouse scroll wheel.
		 * 
		 * @param world
		 *            : Desired world, any format.
		 * @param timeout
		 *            : Total timeout duration for the clicking of the up/down
		 *            arrows
		 * @return : true if the world is in view
		 */
		private static boolean scrollToWorld(int world) {

			world = formatWorld(world) - 300;
			if (!isWorldSwitcherOpen())
				return false;
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, 0);
			if (child == null)
				return false;
			final Rectangle WORLD_SWITCHER_MENU = child.getAbsoluteBounds();
			if (WORLD_SWITCHER_MENU == null)
				return false;
			World w = getWorld(world);
			if (w == null)
				return false;
			if (rectangleIsVisible(w.getBounds()))
				return true;
			final int WORLD_SWITCHER_TOP_HEIGHT = getWorldSwitcherMenuHeight(true);
			final int WORLD_SWITCHER_BOTTOM_HEIGHT = getWorldSwitcherMenuHeight(false);
			if (WORLD_SWITCHER_TOP_HEIGHT == 0 || WORLD_SWITCHER_BOTTOM_HEIGHT == 0)
				return false;
			boolean half_ticks = false;
			boolean up = false;
			int ticks = 0;

			long timer = System.currentTimeMillis() + 10000;
			while (timer > System.currentTimeMillis()) {

				if (Player.getRSPlayer().isInCombat())
					return false;

				if (!WORLD_SWITCHER_MENU.contains(Mouse.getPos())) {
					Mouse.moveBox(WORLD_SWITCHER_MENU);
					General.sleep(General.randomSD(0, 500, 75, 25));
				}

				if (ticks == 0)
					ticks = getTicks();

				w = getWorld(world);
				if (w == null)
					return false;

				if (rectangleIsVisible(w.getBounds()))
					return true;

				if (w.getBounds().y < WORLD_SWITCHER_TOP_HEIGHT) {
					Mouse.scroll(true, ticks);
					up = true;
				} else if ((w.getBounds().y + w.getBounds().height) > WORLD_SWITCHER_BOTTOM_HEIGHT) {
					Mouse.scroll(false, ticks);
					up = false;
				}

				w = getWorld(world);
				if (w == null)
					return false;
				if (up && (w.getBounds().y + w.getBounds().height) > WORLD_SWITCHER_BOTTOM_HEIGHT) {
					half_ticks = true;
				} else if (!up && w.getBounds().y < WORLD_SWITCHER_TOP_HEIGHT) {
					half_ticks = true;
				}

				if (half_ticks)
					ticks = (int) Math.ceil(ticks / 2.0D);

				General.sleep(getDelay());

			}

			w = getWorld(world);
			if (w == null)
				return false;
			return rectangleIsVisible(w.getBounds());

		}

		/**
		 * CHARACTER PROFILE
		 * 
		 * @return
		 */
		private static int getTicks() {
			return General.random(1, 5);
		}

		/**
		 * CHARACTER PROFILE
		 * 
		 * @return
		 */
		private static int getDelay() {
			return General.randomSD(0, 200, 50, 25);
		}

		/**
		 * Will scroll to the desired world if the world switcher menu is open.
		 * If the distance of the bounds of the desired world are over (200,
		 * 250) it will click the scroll pane first then navigate by the up and
		 * down arrows.
		 * 
		 * @param world
		 *            : Desired world, any format.
		 * @param timeout
		 *            : Total timeout duration for the clicking of the up/down
		 *            arrows
		 * @return : true if the world is in view
		 */
		private static boolean clickToWorld(int world) {

			world = formatWorld(world) - 300;
			if (!isWorldSwitcherOpen())
				return false;
			World w = getWorld(world);
			if (w == null)
				return false;
			if (rectangleIsVisible(w.getBounds()))
				return true;
			int distance = getBoundsDistanceToScreen(w.getBounds());

			if (Math.abs(distance) > General.random(200, 250)) {
				RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLL_BAR);
				if (child == null || child.isHidden())
					return false;
				RSInterfaceComponent component = null;
				component = child.getChild(0);
				if (component == null || component.isHidden())
					return false;
				Rectangle scrollBarPane = component.getAbsoluteBounds();
				if (scrollBarPane == null)
					return false;
				component = child.getChild(1);
				if (component == null || component.isHidden())
					return false;
				Rectangle scrollBar = component.getAbsoluteBounds();
				if (scrollBar == null)
					return false;
				double MAX_DISTANCE = 927;
				double SCROLL_BAR_PANE_HEIGHT = scrollBarPane.getHeight();
				double SCROLL_BAR_Y = scrollBar.getY();
				double RATIO = SCROLL_BAR_PANE_HEIGHT / MAX_DISTANCE;
				double TRAVEL_DISTANCE = distance * RATIO;
				int x = scrollBar.x + General.random(4, scrollBar.width - 4);
				int y = (int) (SCROLL_BAR_Y + TRAVEL_DISTANCE);
				if (distance < 0)
					y += scrollBar.height;
				if (y == 245)
					y += General.random(2, 15);
				if (y == 417)
					y -= General.random(2, 15);
				Mouse.click(new Point(x, y), 1);
				General.sleep(General.randomSD(0, 500, 75, 25));
			}

			w = getWorld(world);
			if (w == null)
				return false;
			if (rectangleIsVisible(w.getBounds()))
				return true;

			final int WORLD_SWITCHER_TOP_HEIGHT = getWorldSwitcherMenuHeight(true);
			final int WORLD_SWITCHER_BOTTOM_HEIGHT = getWorldSwitcherMenuHeight(false);
			if (WORLD_SWITCHER_TOP_HEIGHT == 0 || WORLD_SWITCHER_BOTTOM_HEIGHT == 0)
				return false;

			if (w.getBounds().y < WORLD_SWITCHER_TOP_HEIGHT) {

				RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLL_BAR);
				if (child == null)
					return false;

				RSInterfaceComponent up = child.getChild(WORLD_SWITCHER_UP_ARROW);
				if (up == null)
					return false;

				long timer = System.currentTimeMillis() + 10000;
				while (timer > System.currentTimeMillis()) {
					if (Player.getRSPlayer().isInCombat())
						return false;
					w = getWorld(world);
					if (w == null)
						return false;
					up.click();
					if (rectangleIsVisible(w.getBounds()))
						return true;
					General.sleep(getDelay());
				}

			} else {

				RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_SCROLL_BAR);
				if (child == null)
					return false;

				RSInterfaceComponent down = child.getChild(WORLD_SWITCHER_DOWN_ARROW);
				if (down == null)
					return false;

				long timer = System.currentTimeMillis() + 10000;
				while (timer > System.currentTimeMillis()) {
					if (Player.getRSPlayer().isInCombat())
						return false;
					w = getWorld(world);
					if (w == null)
						return false;
					down.click();
					if (rectangleIsVisible(w.getBounds()))
						return true;
					General.sleep(getDelay());
				}

			}

			w = getWorld(world);
			if (w == null)
				return false;
			return rectangleIsVisible(w.getBounds());

		}

		/**
		 * @param top
		 *            or bottom of the menu's height
		 * @return
		 */
		private static int getWorldSwitcherMenuHeight(boolean top) {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_MASTER_WORLDS_INTERFACE);
			if (child == null)
				return 0;
			Rectangle bounds = child.getAbsoluteBounds();
			if (bounds == null)
				return 0;
			if (top) {
				return bounds.y;
			} else {
				return bounds.y + bounds.height;
			}
		}

		/**
		 * 
		 * @param Rectangle
		 *            bounds of the world
		 * @return true if the rectangle is visible inside the world switching
		 *         menu
		 */
		private static boolean rectangleIsVisible(Rectangle bounds) {
			return (bounds.y >= getWorldSwitcherMenuHeight(true))
					&& ((bounds.y + bounds.height) <= getWorldSwitcherMenuHeight(false));
		}

		private enum COUNTRY {

			UNITED_STATES("United States", 1133),

			UNITED_KINGDOM("United Kingdom", 1135),

			GERMANY("Germany", 1140);

			private final String name;
			private final int textureID;

			private COUNTRY(String name, int textureID) {
				this.name = name;
				this.textureID = textureID;
			}
		}

		private static class World {
			private int number;
			private COUNTRY location;
			private Integer players;
			private String activity;
			private Rectangle bounds;

			public World(int number, COUNTRY location, int players, String activity, Rectangle bounds) {
				this.number = number;
				this.location = location;
				this.players = players;
				this.activity = activity;
				this.bounds = bounds;
			}

			public int getNumber() {
				return number;
			}

			public COUNTRY getLocation() {
				return location;
			}

			public int getPlayers() {
				return players;
			}

			public String getActivity() {
				return activity;
			}

			public Rectangle getBounds() {
				return bounds;
			}

			public static Comparator<World> COMPARE_BY_PLAYERS_LOW_TO_HIGH = new Comparator<World>() {
				public int compare(World one, World two) {
					return one.players.compareTo(two.players);
				}
			};

			public static Comparator<World> COMPARE_BY_PLAYERS_HIGH_TO_LOW = new Comparator<World>() {
				public int compare(World one, World two) {
					return two.players.compareTo(one.players);
				}
			};

		}

		/**
		 * 
		 * @param The
		 *            world you want to get the Rectangle bounds for
		 * 
		 * @return the World object containing world, location, players,
		 *         activity, and Rectangle bounds.
		 */
		private static World getWorld(int world) {
			if (!isWorldSwitcherOpen())
				return null;

			int w = formatWorld(world) - 300;
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_LIST_INTERFACE);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();
			if (components == null || components.length == 0 || components.length % 6 != 0)
				return null;

			for (int i = 0; i < components.length; i += 6) {

				RSInterfaceComponent a = components[i + 2];

				if (a != null) {

					String world_number = a.getText();

					if (world_number != null && world_number.length() > 0 && Integer.parseInt(world_number) == w) {

						RSInterfaceComponent b = components[i];

						if (b != null) {

							Rectangle world_bounds = b.getAbsoluteBounds();
							RSInterfaceComponent c = components[i + 3];

							if (world_bounds != null && c != null) {

								int world_location = c.getTextureID();
								RSInterfaceComponent d = components[i + 4];

								if (world_location > 0 && d != null) {

									String world_players = d.getText();
									RSInterfaceComponent e = components[i + 5];

									if (world_players != null && e != null) {

										String world_activity = e.getText();

										if (world_activity != null) {

											COUNTRY country = null;
											for (COUNTRY location : COUNTRY.values()) {
												if (location.textureID == world_location) {
													country = location;
													break;
												}
											}

											return new World(Integer.parseInt(world_number), country,
													Integer.parseInt(world_players), world_activity, world_bounds);
										}
									}
								}
							}
						}
						return null;
					}
				}
			}
			return null;
		}

		/**
		 * 
		 * @param The
		 *            rectangle bounds of the world you are checking
		 * @return the distance that the bounds are from the visible world
		 *         switching menu
		 */
		private static int getBoundsDistanceToScreen(Rectangle bounds) {
			final int WORLD_SWITCHER_TOP_HEIGHT = getWorldSwitcherMenuHeight(true);
			final int WORLD_SWITCHER_BOTTOM_HEIGHT = getWorldSwitcherMenuHeight(false);
			if (bounds.y <= WORLD_SWITCHER_TOP_HEIGHT)
				return bounds.y - WORLD_SWITCHER_TOP_HEIGHT;
			if ((bounds.y + bounds.height) >= WORLD_SWITCHER_BOTTOM_HEIGHT)
				return (bounds.y + bounds.height) - WORLD_SWITCHER_BOTTOM_HEIGHT;
			return 0;
		}

		/**
		 * 
		 * @return true if the world switcher is open
		 */
		private static boolean isWorldSwitcherOpen() {
			RSInterfaceChild child = Interfaces.get(WORLD_SWITCHER_MASTER, 0);
			return child != null && !child.isHidden();
		}

		/**
		 * 
		 * @return true if the logout menu is open
		 */
		private static boolean logoutMenuOpen() {
			RSInterfaceChild child = Interfaces.get(LOGOUT_MENU_MASTER, LOGOUT_MENU_WORLD_SWITCHER_BUTTON);
			return child != null && !child.isHidden();
		}

		/**
		 * 
		 * @return true if the world switcher menu is open
		 */
		private static boolean openWorldSwitcher() {
			if (isWorldSwitcherOpen())
				return true;
			if (!logoutMenuOpen()) {
				GameTab.open(TABS.LOGOUT);
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return Interfaces.isInterfaceValid(LOGOUT_MENU_MASTER);
					}
				}, 2000);
			}
			if (logoutMenuOpen()) {
				RSInterfaceChild worldSwitcher = Interfaces.get(LOGOUT_MENU_MASTER, LOGOUT_MENU_WORLD_SWITCHER_BUTTON);
				if (worldSwitcher == null || worldSwitcher.isHidden())
					return false;
				worldSwitcher.click();
				Timing.waitCondition(new Condition() {

					public boolean active() {
						General.sleep(100);
						return isWorldSwitcherOpen();
					}
				}, 2000);

			}
			return isWorldSwitcherOpen();
		}

		/**
		 * 
		 * @return true if the world switcher menu was closed
		 */
		public static boolean closeWorldSwitcher() {
			if (!isWorldSwitcherOpen())
				return true;
			RSInterfaceChild close = Interfaces.get(WORLD_SWITCHER_MASTER, WORLD_SWITCHER_CLOSE);
			if (close == null)
				return false;
			if (close.click()) {
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return !Interfaces.isInterfaceValid(WORLD_SWITCHER_MASTER);
					}
				}, 2000);
			}
			return !isWorldSwitcherOpen();
		}

		/**
		 * 
		 * @return true if the select world option is up
		 */
		private static boolean selectWorldIsUp() {
			return Login.getLoginState() == Login.STATE.LOGINSCREEN && Screen.getColorAt(10, 10).getRed() > 100;
		}

		/**
		 * selects the desired world using the in-game world switcher or logout
		 * menu depending on the game state
		 * 
		 * @param world
		 * @return true if we changed worlds
		 */
		private static boolean selectWorld(int world) {
			if (getCurrentWorld() == world)
				return true;
			if (Login.getLoginState() == Login.STATE.INGAME) {
				if (!Interfaces.isInterfaceValid(219)) {
					World w = getWorld(world);
					if (w == null)
						return false;
					Mouse.clickBox(w.getBounds(), 1);
					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(50);
							return Interfaces.isInterfaceValid(219) || Game.getSetting(18) == 0;
						}
					}, 3000);
					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(General.randomSD(0, 1000, 150, 50));
							return Interfaces.isInterfaceValid(219)
									|| (getCurrentWorld() == world && Game.getSetting(18) == 1);
						}
					}, 3000);
				}
				if (Interfaces.isInterfaceValid(219)) {
					RSInterfaceChild child = Interfaces.get(219, 0);
					if (child == null)
						return false;
					RSInterfaceComponent component = child.getChild(2);
					if (component == null)
						return false;
					String text = component.getText();
					if (text != null && text.contains("only ask this for")) {
						Keyboard.typeSend("2");
					} else {
						Keyboard.typeSend("1");
					}
					General.sleep(General.randomSD(0, 500, 75, 25));
					Timing.waitCondition(new Condition() {

						public boolean active() {
							General.sleep(100);
							return getCurrentWorld() == world;
						}
					}, 3000);
				}
			} else {
				if (Login.getLoginState() != Login.STATE.LOGINSCREEN)
					return false;
				if (!selectWorldIsUp()) {
					Mouse.clickBox(CLICK_TO_SWITCH, 1);
					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(100);
							return selectWorldIsUp();
						}
					}, 3000);
				}
				if (

				selectWorldIsUp()) {
					if (worldListSorted()) {
						Mouse.move(getPoint(world));
						General.sleep(General.randomSD(0, 500, 75, 25));
						Mouse.click(1);
						Timing.waitCondition(new Condition() {
							public boolean active() {
								General.sleep(100);
								return !selectWorldIsUp() && getCurrentWorld() == world;
							}
						}, 3000);
					}
				}
			}

			return

			getCurrentWorld() == world;
		}

		/**
		 * Sorts the logged out world switching menu to the correct order.
		 * 
		 * @return true if the logged out menu is sorted by world order lowest
		 *         to highest.
		 */
		private static boolean worldListSorted() {
			Point arrow = new Point(301, 8);
			if (Screen.getColorAt(arrow).getGreen() > 50)
				return true;
			Mouse.click(arrow, 1);
			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return Screen.getColorAt(arrow).getGreen() > 50;
				}
			}, 3000);
			return Screen.getColorAt(arrow).getGreen() > 50;
		}

		/**
		 * 
		 * @return the current world
		 */
		public static int getCurrentWorld() {
			return Game.getCurrentWorld();
		}

		/**
		 *
		 * enum for the World Type for the getRandomWorld method.
		 */
		private enum WORLD_TYPE {

			FREE_TO_PLAY(FREE_WORLDS),

			MEMBERS(MEMBERS_WORLDS),

			PVP(PVP_WORLDS);

			private final int[] worlds;

			WORLD_TYPE(int[] worlds) {
				this.worlds = worlds;
			}

			private int[] getWorlds() {
				return worlds;
			}
		}

		/**
		 * Selects a random world based on the WORLD_TYPE enum of (F2P, Members,
		 * PVP)
		 * 
		 * @return world
		 */
		public static int getRandomWorld(WORLD_TYPE type) {
			return type.getWorlds()[General.random(0, type.getWorlds().length - 1)];
		}

		/**
		 * Gets the column that the world belongs to in the logged out world
		 * switcher menu
		 * 
		 * @param world
		 * @return integer array[column, position] of the world
		 */
		private static int[] getColumn(int world) {
			for (int i = 0; i < COLUMN_1_WORLDS.length; i++) {
				if (world == COLUMN_1_WORLDS[i])
					return new int[] { 1, i };
			}
			for (int i = 0; i < COLUMN_2_WORLDS.length; i++) {
				if (world == COLUMN_2_WORLDS[i])
					return new int[] { 2, i };
			}
			for (int i = 0; i < COLUMN_3_WORLDS.length; i++) {
				if (world == COLUMN_3_WORLDS[i])
					return new int[] { 3, i };
			}
			for (int i = 0; i < COLUMN_4_WORLDS.length; i++) {
				if (world == COLUMN_4_WORLDS[i])
					return new int[] { 4, i };
			}
			return new int[] { -1, -1 };
		}

		/**
		 * Returns the point from the logged out world switcher menu
		 * 
		 * @param world
		 * @return the point of the world
		 */
		public static Point getPoint(int world) {
			int[] location = getColumn(world);
			if (location.length == 0 || location[0] == -1)
				return null;
			int column = location[0];
			int position = location[1];
			if (column == 1) {
				return new Point(COLUMN_1_WORLD.x, COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
			} else if (column == 2) {
				return new Point(COLUMN_1_WORLD.x + (1 * HORIZONTAL_DISTANCE),
						COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
			} else if (column == 3) {
				return new Point(COLUMN_1_WORLD.x + (2 * HORIZONTAL_DISTANCE),
						COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
			} else if (column == 4) {
				return new Point(COLUMN_1_WORLD.x + (3 * HORIZONTAL_DISTANCE),
						COLUMN_1_WORLD.y + (position * VERTICAL_DISTANCE));
			}
			return null;
		}

	}

	private static class Session {
		private final Map<String, String> vars;
		private final String botid = "b0dafd24ee35a477";

		public Session() {
			vars = new LinkedHashMap<String, String>();
			vars.put("botid", botid);
			vars.put("custid", UUID.randomUUID().toString());
		}
	}

	private static class Message {
		private String username;
		private String message;
		private String response;
		private boolean bot;

		public Message(String username, String message, String response, boolean bot) {
			this.message = message;
			this.response = response;
		}

		public String getUsername() {
			return username;
		}

		public String getMessage() {
			return message;
		}

		public String getResponse() {
			return response;
		}

		public boolean isBot() {
			return bot;
		}
	}

	private static class Conversation {

		private String username;
		private ArrayList<Message> messages;
		private Session session;
		private long time;

		public Conversation(String username, ArrayList<Message> messages, Session session, long time) {
			this.username = username;
			this.messages = messages;
			this.session = session;
			this.time = time;
		}

		public String getUsername() {
			return username;
		}

		public ArrayList<Message> getMessages() {
			return messages;
		}

		public void addMessage(Message message) {
			messages.add(message);
		}

		public Session getSession() {
			return session;
		}

		public void setTime(long t) {
			this.time = t;
		}

		public long getTimeFromLastMessage() {
			return time;
		}
	}

	public static class AutoResponder {

		private final static int MAX_PLAYERS_ON_SCREEN_TO_CHAT = 1;
		private final static int MAX_RESPONSES_TO_EACH_PLAYER = 5;
		private final static long MIN_DELAY_BETWEEN_MESSAGES = 20000;
		private final static long MAX_DELAY_BETWEEN_MESSAGES = 60000;
		private final static double SMILE_PERCENT = 20.0;
		private final static double QUESTION_PERCENT = 20.0;
		private final static double PUNCTUATION_PERCENT = 10.0;

		private final static String CONFUSED[] = new String[] { "what skill", "what skill?", "..?", "..??", "?", "??",
				"???", "huh?", "what", "hm?", "huh", "idk?", "idk", "lol", "lol?" };

		private final static String QUESTIONS[] = new String[] { "why", "y?", "u?", "you?", "hbu", "what about you",
				"and you?", "..you?", "..u?" };

		private final static String PUNCTUATION[] = new String[] { ".", ".", "!", "?" };

		private final static String PREFERENCES[] = { "is annoying", "is so annoying", "is my favorite", "is fun",
				"is so fun", "can be fun", "is not fun", "can be stressful", "is meh", "is interesting",
				"is challenging", "is stressful", "is not my best stat", "is what im working on",
				"is what im trying to level up" };

		private final static String[] LEAVING_KEYWORDS = new String[] { "bye", "cya", "cyah", "later", "goodbye",
				"seeya", "see-ya", "lata", "peace" };

		private final static String LEAVING_RESPONSES[] = new String[] { "ok", "bye", "laterr", "cya", "later", "lata",
				"peace", "good bye", "goodbye", "later man", "later dude", "bye man", "bye dude", "peace man",
				"ok sounds good", "thanks cya", "haha kk", "k", "haha" };

		private final static String[] BOTTING_KEYWORDS = new String[] { "bot", "bots", "botters", "macroers",
				"cheaters", "bottin", "botting", "macro", "macroing", "cheat", "cheating", "cheater", "botter",
				"macroer", "boting", "botin" };

		private final static String BOTTING_RESPONSES[] = new String[] { "no", "nop", "nope", "nah", "nahh", "noo",
				"naw", "nopee", "not a bot", "not botting", "lol whatever", "lol", "nice try", "i thought you were",
				"lol i figured you were", "i guessed u were lol", "r u?", "u sure u arent?", "i think you are",
				"maybe you are", "not me", "haha", "lol", "what?", "?", "??", "???", "...?", "..??", "..?", "no man",
				"no i dont do that", "nah im legit", "im legit", "i am legit", "nah man", "naw man", "rofl", "lol???",
				"dont think so", "you sure?", "hahaha", "lol??" };

		private final static String GENERIC_RESPONSES[] = new String[] { "?", "??", "huh", "what", "wat", "lol",
				"sorry", "idk", "idk sorry", "what do you mean", "not sure", "not sure what you mean",
				"not sure what u mean", "uhh", "hm", "hmm", "how", "why" };

		private final static String SMILE_FACES[] = new String[] { ":)", ":)", ":)", ":D", ":O", ":]", ":(", ":[",
				":X" };

		private final static String LEVEL_RESPONSES[] = new String[] { "level", "level", "level", "idk", "99",
				"i am " + "level" + " in " + "skill", "i have " + "level" + " in " + "skill",
				"i have " + "level" + " " + "skill", "i got " + "level" + " in " + "skill",
				"i got " + "level" + " " + "skill", "i've got " + "level", "lvl " + "level", "level" + " wooo",
				"about to be " + "level1", "Just got " + "level", "meh, " + "level", "like " + "level",
				"gettin close to " + "level1", "idk " + "level", "around " + "level", "almost " + "level1",
				"level1" + " soon", "level" + " " + "skill", "level" + " ish", "recently got " + "level",
				"im around " + "level", "uhh " + "level", "uhh like " + "level",
				"level" + " or " + "level1" + " i think", "level" + " lol", "lol " + "level", "over " + "level",
				"im about " + "level", "idk like " + "level", "im like " + "level", "about " + "level",
				"like almost " + "level1", "almost " + "level1", "closing in on " + "level1",
				"level" + ". " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)],
				"level" + ".. " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)],
				"level" + ", " + "skill" + " " + PREFERENCES[General.random(0, PREFERENCES.length - 1)] };

		public static void checkMessage(String username, String message, ArrayList<Conversation> history) {
			if (playerCommunicatingWithUs(username, message, history)) {
				String response = "";
				Conversation conversation = getChatHistory(username, history);
				if (conversation == null) {
					Session session = new Session();
					conversation = new Conversation(username, new ArrayList<Message>(), session, 0);
					history.add(conversation);
				}
				int delay = General.random((int) MIN_DELAY_BETWEEN_MESSAGES, (int) MAX_DELAY_BETWEEN_MESSAGES);
				if (System.currentTimeMillis() > (conversation.getTimeFromLastMessage() + delay)) {
					String[] m = formatMessage(message);
					if (isLevelResponse(m)) {
						response = generateLevelResponse(getSkill(m));
						conversation.addMessage(new Message(username, message, response, false));
					} else if (isBotResponse(m)) {
						response = generateBottingResponse();
						conversation.addMessage(new Message(username, message, response, false));
					} else if (isLeavingResponse(m)) {
						response = generateLeavingResponse();
						conversation.addMessage(new Message(username, message, response, false));
					} else {
						try {
							response = PandoraBot.ask(conversation.getSession(), message);
							if (!validPandoraResponse(response, history))
								response = generateGenericResponse();
							conversation.addMessage(new Message(username, message, response, true));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					conversation.setTime(System.currentTimeMillis());
					System.out.println(username + ": " + message + " | Response: " + response);
					General.sleep((long) General.randomSD(2000, 500));
					Keyboard.typeSend(response);
				}
			}
		}

		private static boolean validPandoraResponse(String response, ArrayList<Conversation> history) {
			if (response == null || response.isEmpty() || response.contains("www") || response.contains("http")
					|| response.contains("bot") || response.contains("Chomsky") || response.contains("question")
					|| response.contains("talk") || response.contains("Wikipedia")) {
				return false;
			}

			if (response.length() > 25)
				return false;

			if (getPunctuationCount(response, 3) >= 3)
				return false;

			if (responseAlreadyUsed(response, history))
				return false;

			return true;
		}

		private static boolean responseAlreadyUsed(String response, ArrayList<Conversation> history) {
			for (Conversation c : history) {
				for (Message m : c.messages) {
					if (m.getResponse().equalsIgnoreCase(response))
						return true;
				}
			}
			return false;
		}

		private static int getPunctuationCount(String response, int max) {
			char[] punctuation = { '?', '.', '!', ',', '\'' };
			int count = 0;
			for (char m : response.toCharArray()) {
				if (count >= max)
					return count;
				for (char c : punctuation) {
					if (m == c)
						count++;
				}
			}
			return count;
		}

		private static boolean randomNumberLessThan(double percent) {
			double r = General.randomDouble(0.0, 100.0);
			return r <= percent;
		}

		private static String[] formatMessage(String message) {
			String s = message.replaceAll("\\?", "").replaceAll("\\.", "").replaceAll("!", "");
			return s.split(" ");
		}

		/**
		 * Cases for when to engage in conversation with a player.
		 * 
		 * 1. If we've talked to the player before
		 * 
		 * 2. If there is only one other player loaded in your area.
		 * 
		 * 3. If there are <= MAX_PLAYERS_ON_SCREEN_TO_CHAT on screen talking.
		 * 
		 * 4. If the message partially or fully contains your player username.
		 * 
		 * 5. If a player is interacting with your player (i.e.
		 * following/attacking/etc.)
		 * 
		 * 6. If there is only one player on screen and a BOT_KEYWORD is
		 * mentioned
		 */
		private static boolean playerCommunicatingWithUs(String username, String message,
				ArrayList<Conversation> history) {
			if (Player.getRSPlayer().getName().equals(username))
				return false;
			Conversation c = getChatHistory(username, history);
			if (c != null) {
				if (c.getMessages().size() >= MAX_RESPONSES_TO_EACH_PLAYER)
					return false;
				return true;
			}
			if (onePlayerInArea())
				return true;
			if (getPlayersOnScreen() <= MAX_PLAYERS_ON_SCREEN_TO_CHAT)
				return true;
			if (messageContainsOurName(message))
				return true;
			if (playerInteractingWithUs(username))
				return true;
			if (getPlayersOnScreen() == 1 && isBotResponse(formatMessage(message)))
				return true;
			return false;
		}

		private static boolean onePlayerInArea() {
			return Players.getAll(new Filter<RSPlayer>() {
				@Override
				public boolean accept(RSPlayer player) {
					if (player == null)
						return false;
					if (Player.getRSPlayer().getName().equalsIgnoreCase(player.getName()))
						return false;
					return true;
				}
			}).length == 1;
		}

		private static boolean playerInteractingWithUs(String username) {
			return Players.getAll(new Filter<RSPlayer>() {
				@Override
				public boolean accept(RSPlayer player) {
					if (player == null)
						return false;
					if (!player.getName().equalsIgnoreCase(username))
						return false;
					if (!player.isInteractingWithMe())
						return false;
					return true;
				}
			}).length > 0;
		}

		private static boolean messageContainsOurName(String message) {
			String username = Player.getRSPlayer().getName();
			if (username == null || username.length() == 0)
				return false;
			String split[] = username.split(" ");
			for (String s : split) {
				if (message.contains(s))
					return true;
			}
			return false;
		}

		private static int getPlayersOnScreen() {
			return Players.getAll(new Filter<RSPlayer>() {
				@Override
				public boolean accept(RSPlayer player) {
					if (player == null)
						return false;
					if (Player.getRSPlayer().getName().equalsIgnoreCase(player.getName()))
						return false;
					if (!player.isOnScreen() && player.getPosition().distanceTo(Player.getPosition()) > 8)
						return false;
					return true;
				}
			}).length;
		}

		private static Conversation getChatHistory(String username, ArrayList<Conversation> conversation) {
			for (Conversation c : conversation) {
				if (c.username.equals(username))
					return c;
			}
			return null;
		}

		private static boolean isLeavingResponse(String[] message) {
			for (String m : message) {
				for (String k : LEAVING_KEYWORDS) {
					if (m.equalsIgnoreCase(k))
						return true;
				}
			}
			return false;
		}

		private static String generateLeavingResponse() {
			String response = LEAVING_RESPONSES[General.random(0, LEAVING_RESPONSES.length - 1)];
			if (randomNumberLessThan(SMILE_PERCENT))
				response = response + " " + SMILE_FACES[General.random(0, SMILE_FACES.length - 1)];
			return response;
		}

		private static String generateGenericResponse() {
			String response = "";
			if (randomNumberLessThan(SMILE_PERCENT)) {
				response = response + " " + SMILE_FACES[General.random(0, SMILE_FACES.length - 1)];
			} else {
				response = GENERIC_RESPONSES[General.random(0, GENERIC_RESPONSES.length - 1)];
				if (randomNumberLessThan(50.0))
					response = response + "?";
			}
			return response;
		}

		private static boolean isBotResponse(String[] message) {
			for (String m : message) {
				for (String b : BOTTING_KEYWORDS) {
					if (m.equalsIgnoreCase(b))
						return true;
				}
			}
			return false;
		}

		private static String generateBottingResponse() {
			String response = BOTTING_RESPONSES[General.random(0, BOTTING_RESPONSES.length - 1)];
			if (randomNumberLessThan(SMILE_PERCENT))
				response = response + " " + SMILE_FACES[General.random(0, SMILE_FACES.length - 1)];
			return response;
		}

		private static boolean isLevelResponse(String[] message) {
			String[] keywords = new String[] { "lvl", "lvls", "level", "levels", "stat", "stats", "lev" };
			for (String m : message) {
				for (String k : keywords) {
					if (m.equalsIgnoreCase(k))
						return true;
				}
			}
			return false;
		}

		private static SKILL_KEYWORDS getSkill(String[] message) {
			for (SKILL_KEYWORDS skill : SKILL_KEYWORDS.values()) {
				for (String m : message) {
					for (String k : skill.keywords) {
						if (m.equalsIgnoreCase(k))
							return skill;
					}
				}
			}
			return null;
		}

		private static String generateLevelResponse(SKILL_KEYWORDS current) {
			if (current != null) {
				int level = Skills.getActualLevel(current.skill);
				String skill = current.keywords[General.random(0, current.keywords.length - 1)];
				String response = LEVEL_RESPONSES[General.random(0, LEVEL_RESPONSES.length - 1)];
				response = response.replaceAll("level1", Integer.toString(level + 1));
				response = response.replaceAll("level", Integer.toString(level));
				response = response.replaceAll("skill", skill);

				if (randomNumberLessThan(QUESTION_PERCENT)) {
					response = response + " " + QUESTIONS[General.random(0, QUESTIONS.length - 1)];
					if (randomNumberLessThan(50.0))
						response = response + "?";
				} else if (randomNumberLessThan(PUNCTUATION_PERCENT)) {
					response = response + PUNCTUATION[General.random(0, PUNCTUATION.length - 1)];
				}
				if (randomNumberLessThan(SMILE_PERCENT))
					response = response + " " + SMILE_FACES[General.random(0, SMILE_FACES.length - 1)];
				return response;
			} else {
				return CONFUSED[General.random(0, CONFUSED.length - 1)];
			}
		}

		private enum SKILL_KEYWORDS {

			ATTACK(SKILLS.ATTACK, new String[] { "atk", "attk", "attack" }),

			STRENGTH(SKILLS.STRENGTH, new String[] { "str", "strength" }),

			DEFENCE(SKILLS.DEFENCE, new String[] { "def", "defence", "defense" }),

			RANGED(SKILLS.RANGED, new String[] { "rang", "range", "ranged" }),

			PRAYER(SKILLS.PRAYER, new String[] { "pray", "prayer" }),

			MAGIC(SKILLS.MAGIC, new String[] { "mage", "magic" }),

			RUNECRAFTING(SKILLS.RUNECRAFTING, new String[] { "rc", "runecraft", "runecrafting", "runecraftin" }),

			CONSTRUCTION(SKILLS.CONSTRUCTION, new String[] { "con", "construction", "constructing" }),

			HITPOINTS(SKILLS.HITPOINTS, new String[] { "hp", "hitpoint", "hitpoints", "health" }),

			AGILITY(SKILLS.AGILITY, new String[] { "agil", "agile", "agility" }),

			HERBLORE(SKILLS.HERBLORE, new String[] { "herb", "herblore" }),

			THIEVING(SKILLS.THIEVING, new String[] { "thieve", "thievin", "thieving" }),

			CRAFTING(SKILLS.CRAFTING, new String[] { "craft", "craftin", "crafting" }),

			FLETCHING(SKILLS.FLETCHING, new String[] { "fletch", "fletching" }),

			SLAYER(SKILLS.SLAYER, new String[] { "slay", "slayer", "slaying" }),

			HUNTER(SKILLS.HUNTER, new String[] { "hunt", "hunter", "hunting" }),

			MINING(SKILLS.MINING, new String[] { "mine", "minin", "mining" }),

			SMITHING(SKILLS.SMITHING, new String[] { "smith", "smithin", "smithing" }),

			FISHING(SKILLS.FISHING, new String[] { "fish", "fishin", "fishing" }),

			COOKING(SKILLS.COOKING, new String[] { "cook", "cookin", "cooking" }),

			FIREMAKING(SKILLS.FIREMAKING, new String[] { "fire", "fm", "firemakin", "firemaking" }),

			WOODCUTTING(SKILLS.WOODCUTTING, new String[] { "wc", "woodcuttin", "woodcutting" }),

			FARMING(SKILLS.FARMING, new String[] { "farm", "farmin", "farming" });

			private final SKILLS skill;
			private final String[] keywords;

			SKILL_KEYWORDS(SKILLS skill, String[] keywords) {
				this.skill = skill;
				this.keywords = keywords;
			}

		}
	}

	public static class PandoraBot {

		public static String ask(Session session, String text) throws Exception {
			session.vars.put("input", text);

			String response = post("http://www.pandorabots.com/pandora/talk-xml", session.vars);

			return (xPathSearch(response, "//result/that/text()"));
		}

		public static String parametersToWWWFormURLEncoded(Map<String, String> parameters) throws Exception {
			StringBuilder s = new StringBuilder();
			for (Map.Entry<String, String> parameter : parameters.entrySet()) {
				if (s.length() > 0) {
					s.append("&");
				}
				s.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
				s.append("=");
				s.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
			}
			return s.toString();
		}

		public static String md5(String input) throws Exception {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(input.getBytes("UTF-8"));
			BigInteger hash = new BigInteger(1, md5.digest());
			return String.format("%1$032X", hash);
		}

		public static String post(String url, Map<String, String> parameters) throws Exception {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
			osw.write(parametersToWWWFormURLEncoded(parameters));
			osw.flush();
			osw.close();
			Reader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringWriter w = new StringWriter();
			char[] buffer = new char[1024];
			int n = 0;
			while ((n = r.read(buffer)) != -1) {
				w.write(buffer, 0, n);
			}
			r.close();
			return w.toString();
		}

		public static String xPathSearch(String input, String expression) throws Exception {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpression = xPath.compile(expression);
			Document document = documentBuilder.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
			String output = (String) xPathExpression.evaluate(document, XPathConstants.STRING);
			return output == null ? "" : output.trim();
		}

		public static String stringAtIndex(String[] strings, int index) {
			if (index >= strings.length)
				return "";
			return strings[index];
		}

	}

	@Override
	public void mouseClicked(Point p, int button, boolean isBot) {
		Rectangle transferRectangle = new Rectangle(546, 417, 190, 46);
		if (!isBot && transferRectangle.contains(p)) {
			println("You have activated the Force Transfer option.");
			TRANSFER_ITEMS = true;
		}
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

	private boolean offerItems() {

		if (Trading.getWindowState() == null)
			return false;

		int before = 0;

		for (RSItem item : Inventory.getAll()) {
			before += item.getStack();
		}

		long timer = System.currentTimeMillis() + 180000;

		while (timer > System.currentTimeMillis()) {

			int after = 0;

			for (RSItem item : Inventory.getAll()) {
				after += item.getStack();
			}

			if (after > before)
				return true;

			WINDOW_STATE window = Trading.getWindowState();

			if (window != null) {

				if (window == Trading.WINDOW_STATE.FIRST_WINDOW) {

					RSItem[] items = Inventory.getAll();

					for (RSItem item : items) {

						RSItemDefinition d = item.getDefinition();
						if (d != null) {
							String n = d.getName();
							if (n != null) {
								status = "Offering all " + n;
							}
						}

						sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

						Trading.offer(item, 0);

						abc.DELAY_TRACKER.ITEM_INTERACTION.reset();

					}

					items = Inventory.getAll();

					if (Inventory.getCount(PICKAXES) == items.length) {

						if (!Trading.hasAccepted(false)) {

							status = "Accepting First Window";
							Trading.accept();

							Timing.waitCondition(new Condition() {

								public boolean active() {
									sleep((long) General.randomSD(750, 200));
									WINDOW_STATE w = Trading.getWindowState();
									return w != null && w == Trading.WINDOW_STATE.SECOND_WINDOW;
								}
							}, 3000);

							break;

						}

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

			}

			sleep(500);

		}

		return false;

	}

	@Override
	public void tradeRequestReceived(String username) {
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
	public void playerMessageReceived(String username, String message) {
		if (useAutoResponder)
			AutoResponder.checkMessage(username, message, conversationHistory);
	}

	@Override
	public void serverMessageReceived(String arg0) {
	}

}