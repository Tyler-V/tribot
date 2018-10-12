package scripts.blastfurnace;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Trading.WINDOW_STATE;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.api.v1.api.banking.Bank;
import scripts.api.v1.api.entity.UsaNPCS;
import scripts.api.v1.api.entity.UsaObjects;
import scripts.api.v1.api.generic.Conditional;
import scripts.api.v1.api.items.Consumables;
import scripts.api.v1.api.walking.Walk;
import scripts.api.v1.api.worlds.WorldHopper;
import scripts.blastfurnace.GUI;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.web.pricing.Pricing;
import scripts.usa.api2007.Visibility;
import scripts.usa.api.trader.Trader;
import scripts.usa.api.ui.Paint;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Blast Furnace")
public class BlastFurnace extends Script implements MessageListening07, Painting {

	public final static String version = "v1.0t";

	// CONSTANTS
	private final String[] ENERGY_POTION = { "Energy potion(4)", "Energy potion(3)", "Energy potion(2)",
			"Energy potion(1)" };
	private final int[] BROKEN_PIPES = { 9121, 9117 };
	private final int[] BROKEN_COGS = { 9105 };
	private final int[] DRIVE_BELT = { 9103 };
	private final RSTile CONVEYER_BELT_TILE = new RSTile(1940, 4965, 0);
	private final RSTile BAR_DISPENSER_TILE = new RSTile(1939, 4963, 0);

	// VARIABLES
	private long LAST_FOREMAN_PAY_TIME = 0L;
	private long WORLD_HOP_TIME = 0L;
	private int ACTIVE_WORLD = 0;
	private TEMPERATURE CURRENT_TEMPERATURE = TEMPERATURE.UNKNOWN;
	private TEMPERATURE LAST_TEMPERATURE = TEMPERATURE.UNKNOWN;
	private INSTRUCTION CURRENT_INSTRUCTION;
	private INSTRUCTION CLAN_CHAT_INSTRUCTION;
	private long LAST_INSTRUCTION_TIME = System.currentTimeMillis();
	private String status = "";
	private int startXP, startLevel, trades, fail, expectedSmithingXP;
	private RSTile startingPosition;
	private long startTime;
	private boolean resupply = false;
	private boolean run = true;

	// GUI OPTIONS
	private static GUI gui;
	private static boolean gui_is_up = true;
	private static TASK ASSIGNED_TASK;
	private static BAR CURRENT_BAR;
	private static boolean progressiveSmithing;
	private static boolean useABCReaction;
	private static boolean useABCAntiban;
	private static String muleUsername;
	private static List<String> accounts = new ArrayList<String>();
	private static long worldHopDelay = 0L;

	@Override
	public void run() {

		while (Login.getLoginState() != Login.STATE.INGAME) {
			status = "Logging in...";
			sleep(200);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui = new GUI();
				gui.setVisible(true);
			}
		});

		while (gui_is_up) {
			status = "GUI";
			sleep(200);
		}

		startingPosition = Player.getPosition();

		status = "Starting...";

		startXP = Skills.getXP(SKILLS.SMITHING) + Skills.getXP(SKILLS.CRAFTING) + Skills.getXP(SKILLS.STRENGTH)
				+ Skills.getXP(SKILLS.FIREMAKING) + Skills.getXP(SKILLS.AGILITY);

		startLevel = Skills.getActualLevel(SKILLS.SMITHING) + Skills.getActualLevel(SKILLS.CRAFTING)
				+ Skills.getActualLevel(SKILLS.STRENGTH) + Skills.getActualLevel(SKILLS.FIREMAKING)
				+ Skills.getActualLevel(SKILLS.AGILITY);

		startTime = System.currentTimeMillis();

		Camera.setCameraAngle(100);

		calculateBars();

		while (run) {

			if (outsideBlastFurnace()) {

				if (enterBlastFurnace())
					status = "Entering Blast Furnace";

			} else {

				if (WORLD_HOP_TIME != 0 && WORLD_HOP_TIME < System.currentTimeMillis()) {

					if (WorldHopper.getCurrentWorld() != ACTIVE_WORLD) {

						status = "Changing to World " + ACTIVE_WORLD;

						prepareToChangeWorlds();

						if (WorldHopper.changeWorld(ACTIVE_WORLD)) {

							sleep(General.randomSD(5000, 1000));

							status = "Changed to World " + ACTIVE_WORLD;
							println(status);
							WORLD_HOP_TIME = 0L;

						}

					}

				} else {

					if (ASSIGNED_TASK == TASK.SMITHER) {

						if (resupply) {

							if (withdraw(CURRENT_BAR)) {

								if (tradeMule())

									resupply = false;

							}

						} else {

							smith(CURRENT_BAR);

						}

					} else if (ASSIGNED_TASK == TASK.OPERATE_PUMP) {

						pump();

					} else if (ASSIGNED_TASK == TASK.FUEL_STOVE) {

						refuelStove();

					} else if (ASSIGNED_TASK == TASK.PEDAL_BELT) {

						if (Inventory.getCount(ENERGY_POTION) != 0) {

							pedalBelt();

						} else if (openBank()) {

							withdrawEnergyPotion();

						}

					} else if (ASSIGNED_TASK == TASK.FIX_FURNACE) {

						fixFurnace();

					} else if (ASSIGNED_TASK == TASK.MULE) {

						if (Player.getPosition().distanceTo(startingPosition) > 2) {
							status = "Walking to Start Tile";
							Walk.walkToTile(startingPosition, 1, 1, null);
						}

					}

				}

			}

			if (ABC.performAntiban())
				status = "ABC2 Antiban";

			sleep(General.random(0, 100));

		}

	}

	private void prepareToChangeWorlds() {

		if (Player.getAnimation() != -1) {

			status = "Clicking Minimap";
			Walk.clickTileMinimap(Player.getPosition(), 1, 0, null, null);
			sleep(General.randomSD(1000, 200));

		}

		status = "Closing Interfaces";

		closeDispenser();
		Bank.close();
		Bank.closeCollectionBox();
		closeTemperatureGauge();
		NPCChat.clickContinue(true);

	}

	private boolean outsideBlastFurnace() {

		return Objects.findNearest(30, Filters.Objects.actionsEquals("Climb-down")).length > 0;

	}

	private boolean enterBlastFurnace() {

		if (!outsideBlastFurnace())
			return true;

		if (Interfaces.isInterfaceValid(231)) {

			status = "Waiting to enter Blast Furnace";
			sleep(General.randomSD(60000, 15000));

		}

		RSObject[] stairs = Objects.findNearest(30, Filters.Objects.actionsEquals("Climb-down"));

		if (stairs.length > 0) {

			if (UsaObjects.click("Climb-down", stairs)) {

				Conditional.sleep(new Condition() {
					public boolean active() {
						return Player.isMoving() || Player.getAnimation() != -1;
					}
				}, new Condition() {
					public boolean active() {
						return Objects.findNearest(30, Filters.Objects.actionsEquals("Climb-down")).length == 0
								|| Interfaces.isInterfaceValid(231);
					}
				}, 5000);

			}

		}

		return !outsideBlastFurnace();

	}

	private INSTRUCTION getInstruction(TEMPERATURE last, TEMPERATURE current) {

		if (current == TEMPERATURE.RED)
			return INSTRUCTION.STOP;

		if (current == TEMPERATURE.UPPER_GREEN
				&& (last == TEMPERATURE.MID_GREEN || last == TEMPERATURE.LOWER_GREEN || last == TEMPERATURE.WHITE))
			return INSTRUCTION.STOP;

		if (current == TEMPERATURE.LOWER_GREEN
				&& (last == TEMPERATURE.MID_GREEN || last == TEMPERATURE.UPPER_GREEN || last == TEMPERATURE.RED))
			return INSTRUCTION.PUMP;

		if (current == TEMPERATURE.WHITE)
			return INSTRUCTION.PUMP;

		return null;

	}

	private boolean fixFurnace() {

		if (fixPipes()) {

			if (readTemperatureGauge()) {

				TEMPERATURE t = getTemperature();

				if (t == TEMPERATURE.UNKNOWN)
					return false;

				if (!isTemperature(t))
					return false;

				if (t == CURRENT_TEMPERATURE)
					return false;

				if (CURRENT_TEMPERATURE != TEMPERATURE.UNKNOWN)
					LAST_TEMPERATURE = CURRENT_TEMPERATURE;

				CURRENT_TEMPERATURE = t;

				println("Current temperature is " + CURRENT_TEMPERATURE);

				INSTRUCTION i = getInstruction(LAST_TEMPERATURE, CURRENT_TEMPERATURE);

				if (i == null)
					return false;

				if (CURRENT_INSTRUCTION == i)
					return false;

				CURRENT_INSTRUCTION = i;

				if (CURRENT_INSTRUCTION.getNextTime() == 0
						|| System.currentTimeMillis() > CURRENT_INSTRUCTION.getNextTime()) {

					println(">>> INSTRUCTION: " + CURRENT_INSTRUCTION.toString());

					status = "Sending " + CURRENT_INSTRUCTION.toString() + " Instruction";
					Keyboard.typeSend("/" + CURRENT_INSTRUCTION.getAction().toLowerCase());

					long time = General.randomSD(1500, 500);
					CURRENT_INSTRUCTION.setNextTime(System.currentTimeMillis() + time);
					println("Next instruction will be after " + time + " ms");

				}

			}

		}

		return true;

	}

	private boolean pump() {

		try {

			if ((System.currentTimeMillis() - LAST_INSTRUCTION_TIME) > 45000) {

				status = "Requesting Instruction";

				Keyboard.typeSend("/?");

				status = "Waiting for Instruction...";

				long timer = System.currentTimeMillis() + General.random(5000, 10000);

				while (timer > System.currentTimeMillis()) {

					if ((System.currentTimeMillis() - LAST_INSTRUCTION_TIME) < 45000)
						break;

					sleep(General.random(0, 100));

				}

				status = "Got our instructions!";

			}

			if (CLAN_CHAT_INSTRUCTION == INSTRUCTION.PUMP) {

				RSObject[] pump = Objects.findNearest(30, Filters.Objects.nameEquals("Pump"));

				if (pump.length == 0)
					return false;

				if (!pump[0].isOnScreen()) {

					status = "Walking to Pump";
					Walk.walkToTile(pump[0], 1, 2, null);

				}

				if (pump[0].isOnScreen()) {

					status = "Operating Pump";

					if (pump[0].click("Operate")) {

						long timer = System.currentTimeMillis() + General.randomSD(1000, 200);

						while (timer > System.currentTimeMillis()) {

							if (Player.isMoving() || Player.getAnimation() != -1)
								timer = System.currentTimeMillis() + General.randomSD(1000, 200);

							if (CLAN_CHAT_INSTRUCTION == INSTRUCTION.STOP)
								break;

							ABC.performAntiban();

							General.sleep(General.randomSD(0, 100));

						}

					}

				}

			} else if (CLAN_CHAT_INSTRUCTION == INSTRUCTION.STOP) {

				if (Player.getAnimation() != -1) {

					status = "Stopping Pump";

					if (Walk.walkToTile(Player.getPosition(), 0, 0, null)) {

						Timing.waitCondition(new Condition() {

							public boolean active() {
								General.sleep(General.random(0, 100));
								return Player.getAnimation() == -1;
							}

						}, 2000);

					}

				}

			}

			return true;

		} finally {

			ABC.resetShouldHover();
			ABC.resetShouldOpenMenu();

		}

	}

	public static void load() {

		ASSIGNED_TASK = (TASK) GUI.taskBox.getSelectedItem();
		CURRENT_BAR = (BAR) GUI.barBox.getSelectedItem();
		progressiveSmithing = GUI.progressiveCheckbox.isSelected();
		useABCReaction = GUI.reactionCheckbox.isSelected();
		ABC.setSleepReaction(useABCReaction);
		useABCAntiban = GUI.antibanCheckbox.isSelected();
		muleUsername = GUI.muleUsernameText.getText().trim();

		if (ASSIGNED_TASK == TASK.MULE) {

			String str = "Smithers: ";

			for (Object o : GUI.accountModel.toArray()) {

				accounts.add(o.toString());

				if (str.length() <= 10)
					str = str + o.toString();
				else
					str = str + ", " + str.toString();

			}

			General.println(str);

		}

		worldHopDelay = ((int) GUI.timeSlider.getValue()) * 60 * 1000;

		if (progressiveSmithing) {

			int level = Skills.getActualLevel(SKILLS.SMITHING);

			if (level >= 30) {

				if (CURRENT_BAR != BAR.STEEL) {
					CURRENT_BAR = BAR.STEEL;
					General.println("We are Level " + level + ", switching to " + CURRENT_BAR.getName() + ".");
				}

			} else if (level >= 15) {

				if (CURRENT_BAR != BAR.IRON) {
					CURRENT_BAR = BAR.IRON;
					General.println("We are Level " + level + ", switching to " + CURRENT_BAR.getName() + ".");
				}

			} else {

				if (CURRENT_BAR != BAR.BRONZE) {
					CURRENT_BAR = BAR.BRONZE;
					General.println("We are Level " + level + ", starting with " + CURRENT_BAR.getName() + ".");
				}

			}

		}

		gui_is_up = false;
		gui.dispose();

	}

	private boolean deselectItem() {

		if (!Game.isUptext("->"))
			return true;

		String name = Game.getSelectedItemName();

		if (name == null)
			return false;

		RSItem[] item = Inventory.find(name);

		if (item.length == 0)
			return false;

		status = "Deselecting Item";

		if (item[0].click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(General.random(0, 100));
					return !Game.isUptext("->");
				}
			}, 2000);

		}

		status = "Item is deselected";

		return !Game.isUptext("->");

	}

	private boolean areTripsComplete(BAR bar) {

		for (TRIP trip : bar.getTrips()) {

			if (!trip.isComplete())
				return false;

		}

		return true;

	}

	private void resetTrips(BAR bar) {

		status = "We have bars!";
		CURRENT_BAR.setCount(Inventory.getCount(bar.getName()));
		Interfaces.closeAll();
		bar.setTrips(false);
		expectedSmithingXP = Skills.getXP(SKILLS.SMITHING) + bar.getExpectedXP();
		println(bar.getName() + " trips reset and randomized!");
		println("---------------------------");

	}

	private boolean smith(BAR bar) {

		if (expectedSmithingXP == 0)
			expectedSmithingXP = Skills.getXP(SKILLS.SMITHING) + bar.getExpectedXP();

		if (needToPayForeman()) {

			if (payForeman())
				LAST_FOREMAN_PAY_TIME = System.currentTimeMillis();

		} else {

			if (areTripsComplete(bar)) {

				Interfaces.closeAll();

				RSObject[] dispenser = Objects.findNearest(30, Filters.Objects.nameEquals("Bar dispenser"));

				if (dispenser.length == 0)
					return false;

				if (Player.getPosition().distanceTo(BAR_DISPENSER_TILE) > 2) {

					status = "Walking to Bar dispenser";

					Walk.walkToTile(BAR_DISPENSER_TILE, 1, 2, null);

				} else {

					DISPENSER_STATUS state = getDispenserStatus();

					if (state == DISPENSER_STATUS.TAKE) {

						if (expectedSmithingXP < Skills.getXP(SKILLS.SMITHING)) {

							status = "Waiting for expected XP...";

							Timing.waitCondition(new Condition() {
								public boolean active() {
									General.sleep(General.random(0, 100));
									return expectedSmithingXP >= Skills.getXP(SKILLS.SMITHING);
								}
							}, General.random(3000, 6000));

							status = "We have the expected XP!";

						}

						if (takeBars(bar)) {

							resetTrips(bar);

							if (progressiveSmithing) {

								int level = Skills.getActualLevel(SKILLS.SMITHING);

								if (level >= 30) {

									if (CURRENT_BAR != BAR.STEEL) {
										CURRENT_BAR = BAR.STEEL;
										General.println("We are Level " + level + ", switching to "
												+ CURRENT_BAR.getName() + ".");
									}

								} else if (level >= 15) {

									if (CURRENT_BAR != BAR.IRON) {
										CURRENT_BAR = BAR.IRON;
										General.println("We are Level " + level + ", switching to "
												+ CURRENT_BAR.getName() + ".");
									}

								} else {

									if (CURRENT_BAR != BAR.BRONZE) {
										CURRENT_BAR = BAR.BRONZE;
										General.println("We are Level " + level + ", starting with "
												+ CURRENT_BAR.getName() + ".");
									}

								}

							}

							if (WORLD_HOP_TIME != 0) {

								// force world hop by removing delay
								WORLD_HOP_TIME = System.currentTimeMillis();
								// force pay foreman
								LAST_FOREMAN_PAY_TIME = 0L;

							}

						}

					} else if (state == DISPENSER_STATUS.COOL) {

						coolBars();

					} else if (state == DISPENSER_STATUS.EMPTY) {

						if (Inventory.getCount(bar.getName()) > 0)
							resetTrips(bar);
						else
							status = "Waiting for bars...";

					} else {

						status = "Dispenser status unknown!";

					}

				}

			} else {

				TRIP trip = bar.getNextTrip();

				if (trip == null)
					return false;

				ITEM item = trip.getItem();

				if (item == null)
					return false;

				while (!trip.isComplete() && !needToPayForeman() && !resupply && !outsideBlastFurnace()) {

					if (!hasItem(item)) {

						withdraw(item);

					} else {

						if (putOreOnConveyor(item)) {

							status = trip.toString() + " is complete!";
							println(status);
							trip.setComplete(true);
							break;

						}

					}

					if (ABC.performAntiban())
						status = "ABC2 Antiban";

					General.sleep(General.random(0, 100));

				}

			}

		}

		return true;

	}

	private boolean hasItem(ITEM item) {

		RSItem[] items = Inventory.find(item.getName());

		if (items.length == 0)
			return false;

		RSItemDefinition d = items[0].getDefinition();

		if (d == null)
			return false;

		if (d.isNoted())
			return false;

		return Inventory.getCount(item.getName()) >= item.getMinimumAmount();

	}

	private boolean isMaximumOreMessageUp() {

		if (!Interfaces.isInterfaceValid(229))
			return false;

		RSInterfaceChild child = Interfaces.get(229, 0);
		if (child == null || child.isHidden())
			return false;

		String text = child.getText();
		if (text == null || text.isEmpty())
			return false;

		return text.contains("make sure all your ore smelts");

	}

	private boolean isCollectBarMenuUp() {

		if (!Interfaces.isInterfaceValid(229))
			return false;

		RSInterfaceChild child = Interfaces.get(229, 0);
		if (child == null || child.isHidden())
			return false;

		String text = child.getText();
		if (text == null || text.isEmpty())
			return false;

		return text.contains("collect your bars before making any more");

	}

	private boolean putOreOnConveyor(ITEM item) {

		if (isForemanPermissionMenuUp()) {
			LAST_FOREMAN_PAY_TIME = 0L;
			return false;
		}

		Interfaces.closeAll();

		status = "Conveyor Belt";

		RSObject[] conveyor = Objects.findNearest(30, Filters.Objects.actionsEquals("Put-ore-on"));

		if (conveyor.length == 0)
			return false;

		if (!Player.isMoving()) {

			if (!conveyor[0].isOnScreen() || !conveyor[0].isClickable()
					|| Player.getPosition().distanceTo(conveyor[0]) > 5) {

				status = "Walking to Conveyor";

				Walk.walkToTile(CONVEYER_BELT_TILE, 1, 1, new Condition() {
					public boolean active() {
						RSObject[] conveyor = Objects.findNearest(30, Filters.Objects.actionsEquals("Put-ore-on"));
						return conveyor.length > 0 && conveyor[0].isOnScreen() && conveyor[0].isClickable();
					}
				});

			} else {

				if (!Interfaces.isInterfaceValid(219)) {

					status = "Put-ore-on Conveyor";

					if (UsaObjects.click("Put-ore-on", conveyor)) {

						Conditional.sleep(new Condition() {

							public boolean active() {
								return Player.isMoving() || Player.getAnimation() != -1;
							}
						}, new Condition() {
							public boolean active() {
								return Interfaces.isInterfaceValid(219) || isForemanPermissionMenuUp()
										|| isCollectBarMenuUp();
							}
						}, 3000);

					}

					if (isForemanPermissionMenuUp()) {
						LAST_FOREMAN_PAY_TIME = 0L;
						return false;
					}

					if (isCollectBarMenuUp()) {
						println("All trips are already completed!");
						CURRENT_BAR.setTrips(true);
						return false;
					}

				}

				if (Interfaces.isInterfaceValid(219)) {

					status = "Typing 1";

					Keyboard.typeString("1");

					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(General.random(0, 100));
							return Inventory.getCount(item.getName()) == 0 || isMaximumOreMessageUp();
						}
					}, 3000);

				}

			}

		}

		return Inventory.getCount(item.getName()) == 0 || isMaximumOreMessageUp();

	}

	private boolean openBank() {

		if (Bank.isOpen())
			return true;

		Interfaces.closeAll();

		if (Bank.closeCollectionBox() && closeTemperatureGauge()) {

			RSObject[] bank = Objects.findNearest(30, Filters.Objects.nameEquals("Bank chest"));

			if (bank.length == 0)
				return false;

			status = "Opening Bank chest";

			if (UsaObjects.click("Use", bank)) {

				Conditional.sleep(new Condition() {
					public boolean active() {
						return Player.isMoving() || Player.getAnimation() != -1;
					}
				}, new Condition() {
					public boolean active() {
						return Bank.isOpen();
					}
				}, 3000);

			}

		}

		return Bank.isOpen();

	}

	private DISPENSER_STATUS getDispenserStatus() {

		RSObject[] dispenser = Objects.findNearest(30, Filters.Objects.nameEquals("Bar dispenser"));

		int id = 0;

		if (dispenser.length > 0) {

			RSObjectDefinition d = dispenser[0].getDefinition();

			if (d != null) {

				id = d.getID();

				for (DISPENSER_STATUS status : DISPENSER_STATUS.values()) {

					for (int statusID : status.getID()) {

						if (statusID == id)

							return status;

					}

				}

			}

		}

		System.out.println("Unable to detect dispenser status! (ID: " + id + ")");
		return null;

	}

	private boolean barsInDispenser() {

		return Objects.findNearest(30, Filters.Objects.nameEquals("Bar dispenser")
				.combine(Filters.Objects.actionsEquals("Search"), false)).length == 0;

	}

	private boolean fillBucket() {

		if (Inventory.getCount("Bucket of water") > 0)
			return true;

		Interfaces.closeAll();

		RSObject[] sink = Objects.findNearest(30, Filters.Objects.nameEquals("Sink"));

		if (sink.length == 0)
			return false;

		status = "Filling Bucket";

		if (UsaObjects.click("Fill-bucket", sink)) {

			Conditional.sleep(new Condition() {
				public boolean active() {
					return Player.isMoving() || Player.getAnimation() != -1;
				}
			}, new Condition() {
				public boolean active() {
					return Inventory.getCount("Bucket of water") > 0;
				}
			}, 3000);

		}

		status = "Bucket is filled";

		return Inventory.getCount("Bucket of water") > 0;

	}

	private boolean coolBars() {

		if (fillBucket()) {

			Interfaces.closeAll();

			RSObject[] dispenser = Objects.findNearest(30, Filters.Objects.nameEquals("Bar dispenser"));

			if (dispenser.length == 0)
				return false;

			status = "Cooling Bars";

			RSItem[] bucket = Inventory.find("Bucket of water");

			if (UsaObjects.useItemOnObject(bucket, dispenser)) {

				Conditional.sleep(new Condition() {
					public boolean active() {
						return Player.isMoving() || Player.getAnimation() != -1;
					}
				}, new Condition() {
					public boolean active() {
						return getDispenserStatus() == DISPENSER_STATUS.TAKE;
					}
				}, 2000);

			}

		}

		return true;

	}

	private boolean barStockInterfaceUp() {

		return Interfaces.isInterfaceValid(28);

	}

	private boolean closeDispenser() {

		if (!Interfaces.isInterfaceValid(28))
			return true;

		RSInterfaceChild child = Interfaces.get(28, 118);

		if (child == null || child.isHidden())
			return false;

		if (child.click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(General.random(0, 100));
					return !Interfaces.isInterfaceValid(28);
				}
			}, 2000);

		}

		return !Interfaces.isInterfaceValid(28);

	}

	private boolean takeBars(BAR bar) {

		if (deselectItem()) {

			if (Inventory.isFull()) {

				status = "Banking Inventory!";

				if (openBank()) {

					boolean COINS_REQUIRED = false;

					if (Skills.getActualLevel(SKILLS.SMITHING) < 60)
						COINS_REQUIRED = true;

					List<String> list = new ArrayList<String>();

					list.add("Bucket");
					list.add("Bucket of water");

					if (COINS_REQUIRED)
						list.add("Coins");

					String[] array = new String[list.size()];
					list.toArray(array);

					if (Banking.depositAllExcept(array) > 0)
						status = "Deposited Items";

				}

			} else {

				if (!barStockInterfaceUp()) {

					RSObject[] dispenser = Objects.findNearest(30, Filters.Objects.nameEquals("Bar dispenser"));

					if (dispenser.length == 0)
						return false;

					status = "Taking Bars";

					if (UsaObjects.click("Take", dispenser)) {

						Conditional.sleep(new Condition() {
							public boolean active() {
								return Player.isMoving() || Player.getAnimation() != -1;
							}
						}, new Condition() {
							public boolean active() {
								return barStockInterfaceUp();
							}
						}, 2000);

					}

				}

				if (barStockInterfaceUp()) {

					status = "Taking All";

					RSInterfaceChild child = Interfaces.get(28, bar.getChild());

					if (child == null)
						return false;

					if (child.click()) {

						Timing.waitCondition(new Condition() {
							public boolean active() {
								General.sleep(General.random(0, 100));
								return Inventory.getCount(bar.getName()) > 0;
							}

						}, 2000);

					}

				}

			}

		}

		return Inventory.getCount(bar.getName()) > 0;

	}

	private boolean withdrawEnergyPotion() {

		if (Banking.depositAllExcept("Hammer") > 0)
			status = "Depositing All";

		status = "Withdrawing Energy Potions";

		if (Bank.withdraw(0, ENERGY_POTION)) {

			fail = 0;

		} else {

			fail++;

			if (fail >= 3) {

				boolean found = false;

				long timer = System.currentTimeMillis() + 3000;

				while (timer > System.currentTimeMillis()) {

					RSItem[] items = Banking.find(ENERGY_POTION);

					if (items.length > 0) {
						found = true;
						break;
					}

					sleep(100);

				}

				if (found) {

					println("Failsafe, we do have Energy potions!");
					fail = 0;

				} else {

					println("We are out of Energy Potions!");
					run = false;
					return false;

				}

			}

		}

		return true;

	}

	private boolean withdraw(BAR bar) {

		if (Inventory.getCount(bar.getName()) > 0)
			return true;

		if (openBank()) {

			if (Banking.depositAllExcept("Coins", "Bucket", "Bucket of water") > 0)
				status = "Depositing All";

			status = "Noting Items";

			if (Bank.setNoteSelected(true)) {

				status = "Withdrawing " + bar.getName();

				if (Bank.withdraw(0, bar.getName())) {

					status = "Withdrew all " + bar.getName() + "s";

				}

			}

		}

		return Inventory.getCount(bar.getName()) > 0;

	}

	private boolean withdraw(ITEM item) {

		if (fillBucket() && openBank()) {

			boolean COINS_REQUIRED = false;

			if (Skills.getActualLevel(SKILLS.SMITHING) < 60)
				COINS_REQUIRED = true;

			List<String> list = new ArrayList<String>();

			list.add("Bucket");
			list.add("Bucket of water");

			if (COINS_REQUIRED)
				list.add("Coins");

			String[] array = new String[list.size()];
			list.toArray(array);

			if (Banking.depositAllExcept(array) > 0)
				status = "Deposited Items";

			if (Bank.setNoteSelected(false)) {

				if (Bank.withdraw(item.getAmount(), item.getName())) {

					status = "Withdrew " + item.getName();
					fail = 0;

				} else {

					fail++;

					if (fail >= 3) {

						boolean found = false;

						long timer = System.currentTimeMillis() + 3000;

						while (timer > System.currentTimeMillis()) {

							RSItem[] items = Banking.find(item.getName());

							if (items.length > 0) {
								found = true;
								break;
							}

							sleep(100);

						}

						if (found) {

							println("Failsafe, we do have ores!");
							fail = 0;

						} else {

							println("We are out of ores!");
							resupply = true;

						}

					}

				}

			}

		}

		return true;

	}

	private boolean isForemanPermissionMenuUp() {

		RSInterfaceChild child = Interfaces.get(229, 0);
		if (child == null || child.isHidden())
			return false;

		String text = child.getText();
		if (text == null)
			return false;

		return text.contains("You must ask the foreman's permission");

	}

	private boolean needToPayForeman() {

		return Skills.getActualLevel(SKILLS.SMITHING) < 60
				&& (LAST_FOREMAN_PAY_TIME == 0 || ((System.currentTimeMillis() - LAST_FOREMAN_PAY_TIME) > 600000));

	}

	private boolean isPayMenuUp() {

		return Interfaces.isInterfaceValid(219);

	}

	private boolean payForeman() {

		if (Inventory.getCount("Coins") < 2500) {
			status = "Out of coins!";
			println(status);
			run = false;
			return false;
		}

		status = "Paying Foreman";

		Interfaces.closeAll();

		if (deselectItem() && Bank.closeCollectionBox() && closeTemperatureGauge()) {

			RSNPC[] foreman = NPCs.findNearest(Filters.NPCs.nameEquals("Blast Furnace Foreman"));

			if (foreman.length == 0)
				return false;

			if (!isPayMenuUp()) {

				status = "Pay Foreman";

				if (UsaNPCS.click("Pay", foreman)) {

					Conditional.sleep(new Condition() {
						public boolean active() {
							return Player.isMoving() || Player.getAnimation() != -1;
						}
					}, new Condition() {
						public boolean active() {
							return isPayMenuUp();
						}
					}, 3000);

				}

				if (isPayMenuUp()) {

					status = "Typing 1";

					sleep(General.randomSD(1000, 200));

					Keyboard.typeString("1");

					Timing.waitCondition(new Condition() {

						public boolean active() {
							General.sleep(General.random(0, 100));
							return NPCChat.getClickContinueInterface() != null;
						}
					}, 2000);

					if (NPCChat.getClickContinueInterface() != null) {

						status = "Pressing Spacebar";

						sleep(General.randomSD(1000, 200));

						Keyboard.typeString(" ");

						Timing.waitCondition(new Condition() {
							public boolean active() {
								General.sleep(General.random(0, 100));
								return NPCChat.getClickContinueInterface() == null;
							}
						}, 3000);

						if (NPCChat.getClickContinueInterface() == null) {

							status = "Paid Foreman!";
							sleep(General.randomSD(1000, 200));
							return true;

						} else {

							status = "Foreman not paid!";
							return false;

						}

					}

				}

			}

		}

		return false;

	}

	private boolean isDriveBeltBroken() {

		return Objects.find(10, Filters.Objects.idEquals(DRIVE_BELT)).length > 0;

	}

	private boolean repairBelt() {

		RSObject[] belt = Objects.find(10, Filters.Objects.idEquals(DRIVE_BELT));

		if (belt.length == 0)
			return false;

		status = "Repairing Belt";

		if (UsaObjects.click("Repair", belt)) {

			Conditional.sleep(new Condition() {
				public boolean active() {
					return Player.isMoving() || Player.getAnimation() != -1;
				}
			}, new Condition() {
				public boolean active() {
					return !isDriveBeltBroken();
				}
			}, 3000);

		}

		return !isDriveBeltBroken();

	}

	private boolean areCogsBroken() {

		return Objects.find(10, Filters.Objects.idEquals(BROKEN_COGS)).length > 0;

	}

	private boolean repairCogs() {

		RSObject[] cogs = Objects.find(10, Filters.Objects.idEquals(BROKEN_COGS));

		if (cogs.length == 0)
			return false;

		status = "Repairing Cogs";

		if (UsaObjects.click("Repair", cogs)) {

			Conditional.sleep(new Condition() {
				public boolean active() {
					return Player.isMoving() || Player.getAnimation() != -1;
				}
			}, new Condition() {
				public boolean active() {
					return !areCogsBroken();
				}
			}, 3000);

		}

		return !areCogsBroken();

	}

	private boolean pedalBelt() {

		if (Inventory.getCount("Hammer") == 0) {
			General.println("We are missing a hammer!");
			return false;
		}

		if (areCogsBroken() && isDriveBeltBroken()) {

			Random r = new Random();

			if (r.nextBoolean())
				repairCogs();
			else
				repairBelt();

		}

		if (isDriveBeltBroken())
			repairBelt();

		if (areCogsBroken())
			repairCogs();

		if (!areCogsBroken() && !isDriveBeltBroken()) {

			RSObject[] pedals = Objects.findNearest(30, Filters.Objects.nameEquals("Pedals"));

			if (pedals.length == 0)
				return false;

			if (Consumables.drink(ENERGY_POTION, null, false)) {

				status = "Drinking before Pedaling";

			} else {

				status = "Pedaling";

				if (UsaObjects.click("Pedal", pedals)) {

					Conditional.sleep(new Condition() {
						public boolean active() {
							return Consumables.drink(ENERGY_POTION, null, false) || Player.isMoving()
									|| Player.getAnimation() != -1;
						}
					}, new Condition() {
						public boolean active() {
							return areCogsBroken() || isDriveBeltBroken() || Inventory.getCount(ENERGY_POTION) == 0
									|| (WORLD_HOP_TIME != 0 && WORLD_HOP_TIME < System.currentTimeMillis());
						}
					}, 3000);

				}

			}

		}

		return true;

	}

	private boolean isStoveFueled() {

		RSObject[] stove = Objects.findNearest(30, Filters.Objects.nameEquals("Stove"));

		if (stove.length == 0)
			return false;

		return stove[0].getID() != 9085;

	}

	private boolean refuelStove() {

		Visibility.setCheck(false);

		if (Inventory.getCount("Spade") == 0 && Inventory.getCount("Spadeful of coke") == 0) {
			General.println("We are missing a Spade!");
			return false;
		}

		if (Inventory.getCount("Spadeful of coke") == 0) {

			RSObject[] coke = Objects.findNearest(30, Filters.Objects.nameEquals("Coke"));

			if (coke.length == 0)
				return false;

			status = "Collecting Coke";

			if (UsaObjects.click("Collect", coke)) {

				Conditional.sleep(new Condition() {
					public boolean active() {
						return Player.isMoving() || Player.getAnimation() != -1;
					}
				}, new Condition() {
					public boolean active() {
						return Inventory.getCount("Spadeful of coke") != 0;
					}
				}, 3000);

			}

		}

		if (!isStoveFueled()) {

			if (Inventory.getCount("Spadeful of coke") > 0) {

				RSObject[] stove = Objects.findNearest(30, Filters.Objects.nameEquals("Stove"));

				if (stove.length == 0)
					return false;

				status = "Refueling Stove";

				if (UsaObjects.click("Refuel", stove)) {

					Conditional.sleep(new Condition() {

						public boolean active() {
							return Player.isMoving() || Player.getAnimation() != -1;
						}
					}, new Condition() {
						public boolean active() {
							return Inventory.getCount("Spadeful of coke") == 0;
						}
					}, 3000);

				}

			}

		}

		return isStoveFueled();

	}

	private boolean fixPipes() {

		RSObject[] pipes = Objects.findNearest(30, Filters.Objects.idEquals(BROKEN_PIPES));

		if (pipes.length == 0)
			return true;

		final int count = pipes.length;

		if (closeTemperatureGauge()) {

			status = "Repairing Broken Pipes";

			if (UsaObjects.click("Repair", pipes)) {

				Conditional.sleep(new Condition() {
					public boolean active() {
						return Player.isMoving() || Player.getAnimation() != -1;
					}
				}, new Condition() {
					public boolean active() {
						return Objects.findNearest(30, Filters.Objects.idEquals(BROKEN_PIPES)).length < count
								|| (WORLD_HOP_TIME != 0 && WORLD_HOP_TIME < System.currentTimeMillis());
					}
				}, 3000);

			}

		}

		return Objects.findNearest(30, Filters.Objects.idEquals(BROKEN_PIPES)).length == 0;

	}

	private boolean readTemperatureGauge() {

		if (isTemperatureGaugeUp())
			return true;

		RSObject[] gauge = Objects.findNearest(30, Filters.Objects.nameEquals("Temperature gauge"));

		if (gauge.length == 0)
			return false;

		status = "Reading Temperature Gauge";

		ABC.setSleepReaction(false);

		if (UsaObjects.click("Read", gauge)) {

			Conditional.sleep(new Condition() {
				public boolean active() {
					return Player.isMoving() || Player.getAnimation() != -1;
				}
			}, new Condition() {
				public boolean active() {
					return Objects.findNearest(30, Filters.Objects.idEquals(BROKEN_PIPES)).length > 0
							|| isTemperatureGaugeUp();
				}
			}, 3000);

		}

		return isTemperatureGaugeUp();

	}

	private boolean isTemperatureGaugeUp() {

		return Interfaces.isInterfaceValid(30);

	}

	private boolean closeTemperatureGauge() {

		if (!isTemperatureGaugeUp())
			return true;

		RSInterfaceChild child = Interfaces.get(30, 4);
		if (child == null || child.isHidden())
			return false;

		status = "Closing Temperature Gauge";

		if (child.click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(General.random(0, 100));
					return !isTemperatureGaugeUp();
				}
			}, 2000);

		}

		return !isTemperatureGaugeUp();

	}

	private boolean isTemperature(TEMPERATURE temperature) {

		for (TEMPERATURE t : TEMPERATURE.values()) {

			if (t != temperature) {

				if (t.getPoints() != null) {

					for (Point p : t.getPoints()) {

						Color c = Screen.getColorAt(p);

						if (c.getRed() <= 50 && c.getRed() <= 50 && c.getGreen() <= 50)

							return false;

					}

				}

			}

		}

		return true;

	}

	private TEMPERATURE getTemperature() {

		for (TEMPERATURE t : TEMPERATURE.values()) {

			if (t.getPoints() != null) {

				for (Point p : t.getPoints()) {

					Color c = Screen.getColorAt(p);

					if (c.getRed() <= 50 && c.getRed() <= 50 && c.getGreen() <= 50)

						return t;

				}

			}

		}

		return TEMPERATURE.UNKNOWN;

	}

	private int getProfitPerBar(BAR bar) {

		int cost = 0;

		for (TRIP trip : bar.getTrips()) {

			ITEM item = trip.getItem();
			int temp = Pricing.getPrice(item.getName(), item.getID());
			println(item.getName() + " costs " + temp + " gp.");
			cost += temp;

		}

		int value = Pricing.getPrice(bar.getName(), bar.getID());
		println(bar.getName() + " has a value of " + TabletsPaint.formatInteger(value) + " gp.");

		return value - cost;

	}

	private void calculateBars() {

		if (ASSIGNED_TASK == TASK.SMITHER) {

			CURRENT_BAR.setProfit(getProfitPerBar(CURRENT_BAR));

			println("Profit per " + CURRENT_BAR.getName() + " is " + TabletsPaint.formatInteger(CURRENT_BAR.getProfit())
					+ " gp.");

		} else if (ASSIGNED_TASK == TASK.MULE) {

			for (BAR bar : BAR.values()) {

				bar.setCount(Inventory.getCount(bar.getName()));
				bar.setProfit(getProfitPerBar(bar));

			}

		}

	}

	@Override
	public void onPaint(Graphics g) {

		if (ASSIGNED_TASK != null) {

			Graphics2D g2 = (Graphics2D) g;

			RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2.setRenderingHints(rh);

			long currentTime = System.currentTimeMillis();
			long time = currentTime - startTime;
			int currentXP = Skills.getXP(SKILLS.SMITHING) + Skills.getXP(SKILLS.CRAFTING)
					+ Skills.getXP(SKILLS.STRENGTH) + Skills.getXP(SKILLS.FIREMAKING) + Skills.getXP(SKILLS.AGILITY);
			int gainedXP = currentXP - startXP;
			int xpPerHour = (int) (gainedXP * 3600000D / time);
			int currentLevel = Skills.getActualLevel(SKILLS.SMITHING) + Skills.getActualLevel(SKILLS.CRAFTING)
					+ Skills.getActualLevel(SKILLS.STRENGTH) + Skills.getActualLevel(SKILLS.FIREMAKING)
					+ Skills.getActualLevel(SKILLS.AGILITY);
			int gainedLevels = currentLevel - startLevel;
			int levelsPerHour = (int) (gainedLevels * 3600000D / time);
			int bars = 0;
			int barsPerHour = 0;
			int profit = 0;
			int profitPerHour = 0;
			int tradesPerHour = 0;

			if (ASSIGNED_TASK == TASK.MULE) {

				for (BAR bar : BAR.values()) {
					profit += (Inventory.getCount(bar.getName()) - bar.getCount()) * bar.getProfit();
					bars += Inventory.getCount(bar.getName()) - bar.getCount();
				}

				profitPerHour = (int) (profit * 3600000D / time);
				tradesPerHour = (int) (trades * 3600000D / time);

			} else if (ASSIGNED_TASK == TASK.SMITHER) {

				profit = CURRENT_BAR.getCount() * CURRENT_BAR.getProfit();
				profitPerHour = (int) (profit * 3600000D / time);
				bars = CURRENT_BAR.getCount();

			}

			barsPerHour = (int) (bars * 3600000D / time);

			int border_x, border_y, text_x, text_y, rectangle_height, rectangle_width, row_spacing;

			border_x = 280;
			border_y = 361;

			text_x = border_x + 5;
			text_y = border_y - 2;

			rectangle_height = 14;
			rectangle_width = 214;

			row_spacing = 18;

			// Color background = new Color(0, 0, 255, 175);
			Color background = new Color(180, 100, 0, 200);
			Color border = new Color(0, 0, 150, 150);

			g2.setFont(new Font("Tahoma", Font.PLAIN, 12));

			g2.setColor(background);
			g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(border);
			g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
			g2.setColor(Color.WHITE);
			g2.drawString("             USA Blast Furnace      " + version, text_x, text_y);

			border_y += row_spacing;
			text_y += row_spacing;

			if (ASSIGNED_TASK != TASK.SMITHER) {

				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("Task: " + ASSIGNED_TASK.toString(), text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;

			}

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

			if (ASSIGNED_TASK != TASK.MULE) {

				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("XP Gained: " + TabletsPaint.formatInteger(gainedXP) + " ("
						+ TabletsPaint.formatInteger(xpPerHour) + "/hr)", text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;

				if (ASSIGNED_TASK == TASK.SMITHER) {

					g2.setColor(background);
					g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
					g2.setColor(border);
					g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
					g2.setColor(Color.WHITE);
					g2.drawString("Smithing Level: " + Skills.getActualLevel(SKILLS.SMITHING) + " | +"
							+ TabletsPaint.formatInteger(gainedLevels) + " (" + TabletsPaint.formatInteger(levelsPerHour)
							+ "/hr)", text_x, text_y);

					border_y += row_spacing;
					text_y += row_spacing;

				} else {

					g2.setColor(background);
					g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
					g2.setColor(border);
					g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
					g2.setColor(Color.WHITE);
					g2.drawString("Levels Gained: " + TabletsPaint.formatInteger(gainedLevels) + " ("
							+ TabletsPaint.formatInteger(levelsPerHour) + "/hr)", text_x, text_y);

					border_y += row_spacing;
					text_y += row_spacing;

				}

			}

			if (ASSIGNED_TASK == TASK.SMITHER || ASSIGNED_TASK == TASK.MULE) {

				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("Bars: " + TabletsPaint.formatInteger(bars) + " (" + TabletsPaint.formatInteger(barsPerHour)
						+ "/hr)", text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;

				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("Profit: " + TabletsPaint.formatInteger(profit) + " gp ("
						+ TabletsPaint.formatInteger(profitPerHour) + "/hr)", text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;

			}

			if (ASSIGNED_TASK == TASK.MULE) {

				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				g2.drawString("Trades: " + TabletsPaint.formatInteger(trades) + " ("
						+ TabletsPaint.formatInteger(tradesPerHour) + "/hr)", text_x, text_y);

				border_y += row_spacing;
				text_y += row_spacing;

			}

			if (ASSIGNED_TASK == TASK.FIX_FURNACE) {

				g2.setColor(background);
				g2.fillRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(border_x, border_y - rectangle_height, rectangle_width, rectangle_height, 4, 4);
				g2.setColor(Color.WHITE);
				if (CURRENT_INSTRUCTION != null) {
					g2.drawString("Pumper Instruction: " + CURRENT_INSTRUCTION.toString(), text_x, text_y);
				} else {
					g2.drawString("Pumper Instruction: NONE", text_x, text_y);
				}

				border_y += row_spacing;
				text_y += row_spacing;

			}

		}

	}

	@Override
	public void clanMessageReceived(String name, String message) {

		if (name != null && message != null) {

			RSPlayer player = Player.getRSPlayer();

			if (player != null) {

				String playerName = player.getName();

				if (playerName != null && !name.equalsIgnoreCase(playerName)) {

					if (ASSIGNED_TASK == TASK.OPERATE_PUMP) {

						for (INSTRUCTION i : INSTRUCTION.values()) {

							if (message.equals(i.getAction())) {

								CLAN_CHAT_INSTRUCTION = i;
								break;

							}

						}

						LAST_INSTRUCTION_TIME = System.currentTimeMillis();
						println(name + " has instructed us to " + CLAN_CHAT_INSTRUCTION.toString() + "!");

					} else if (ASSIGNED_TASK == TASK.FIX_FURNACE) {

						if (message.contains("?")) {

							println(name + " has requested to repeat the last instruction " + CURRENT_INSTRUCTION
									+ "!");
							Keyboard.typeSend("/" + CURRENT_INSTRUCTION.getAction().toLowerCase());

						}

					}

				}

			}

			if (message.contains("W") && message.length() == 4) {

				println("World Hop instruction received!");

				ACTIVE_WORLD = Integer.parseInt(message.substring(1, message.length()));

				long delay = worldHopDelay + General.random(-10000, 10000);

				WORLD_HOP_TIME = System.currentTimeMillis() + delay;

				println("Changing to World " + ACTIVE_WORLD + " in " + Timing.msToString(delay) + ".");

			}

		}

	}

	@Override
	public void duelRequestReceived(String name, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void personalMessageReceived(String name, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerMessageReceived(String name, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverMessageReceived(String message) {
		// TODO Auto-generated method stub

	}

	private BAR getBarOffered() {

		for (BAR bar : BAR.values()) {

			if (Trader.isItemOffered(true, bar.getName(), 0))
				return bar;

		}

		return null;

	}

	private List<ITEM> getOres(BAR bar) {

		List<ITEM> list = new ArrayList<ITEM>();

		if (bar == null)
			return list;

		for (TRIP trip : bar.getTrips()) {

			ITEM item = trip.getItem();
			item.setAmount(item.getMinimumAmount());

			boolean exists = false;

			for (ITEM i : list) {

				if (i.getName().equals(item.getName())) {

					exists = true;
					i.setAmount(i.getAmount() + item.getAmount());
					break;

				}

			}

			if (!exists)
				list.add(item);

		}

		return list;

	}

	private boolean tradeSmither(String username) {

		RSPlayer[] player = Players.find(username);

		if (player.length == 0 || Player.getPosition().distanceTo(player[0]) > 1 || Trader.isTradeOpen())
			return false;

		if (Trader.openTrade(username, false)) {

			long timer = System.currentTimeMillis() + 30000;

			while (timer > System.currentTimeMillis()) {

				WINDOW_STATE state = Trading.getWindowState();

				if (state == WINDOW_STATE.FIRST_WINDOW) {

					if (Trading.hasAccepted(true)) {

						status = "Accepting Trade";
						Trading.accept();

					} else {

						BAR bar = getBarOffered();

						if (bar != null) {

							List<ITEM> list = getOres(bar);

							int amount = Trader.getAmountItemOffered(true, bar.getName());

							for (ITEM item : list) {

								if (Trader.offer(item.getName(), item.getAmount() * amount))
									status = "Offered " + (item.getAmount() * amount) + " " + item.getName();

							}

						}

					}

				} else if (state == WINDOW_STATE.SECOND_WINDOW) {

					if (!Trading.hasAccepted(false)) {

						status = "Accepting Trade";
						Trading.accept();

					} else {

						status = "Waiting accept...";

					}

				} else {

					status = "Trade Complete";
					trades++;
					return true;

				}

				sleep(100);

			}

		}

		return true;

	}

	public boolean tradeMule() {

		status = "Trading " + muleUsername;

		if (Bank.close() && Trader.openTrade(muleUsername, true)) {

			long timer = System.currentTimeMillis() + 60000;

			while (timer > System.currentTimeMillis()) {

				WINDOW_STATE state = Trading.getWindowState();

				if (state == WINDOW_STATE.FIRST_WINDOW) {

					if (!Trader.isItemOffered(false, CURRENT_BAR.getName(), 0)) {

						status = "Offering " + CURRENT_BAR.getName();

						if (Trader.offer(CURRENT_BAR.getName(), 0))
							status = "Offered " + CURRENT_BAR.getName();

					} else {

						status = "Waiting for ores...";

						boolean valid = true;

						for (TRIP trip : CURRENT_BAR.getTrips()) {

							ITEM item = trip.getItem();

							if (!Trader.isItemOffered(true, item.getName(), 0)) {
								valid = false;
								break;
							}

						}

						if (valid && !Trading.hasAccepted(false)) {
							status = "Accepting Trade";
							Trading.accept();
						}

					}

				} else if (state == WINDOW_STATE.SECOND_WINDOW) {

					if (!Trading.hasAccepted(false)) {
						status = "Accepting Trade";
						Trading.accept();
					}

				} else {

					status = "Trade Complete";
					break;

				}

				sleep(100);

			}

		}

		return Inventory.getCount(CURRENT_BAR.getName()) == 0;

	}

	@Override
	public void tradeRequestReceived(String username) {

		if (ASSIGNED_TASK == TASK.MULE) {

			if (!Trader.isTradeOpen()) {

				for (String name : accounts) {

					status = "Trade from " + username;

					if (username.equalsIgnoreCase(name)) {

						status = "Trading " + username;

						tradeSmither(username);

					}

				}

			}

		}

	}

}
