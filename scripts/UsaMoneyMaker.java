package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSObject.TYPES;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;
import org.tribot.api.util.ABCUtil;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Money Maker")
public class UsaMoneyMaker extends Script implements Painting {

	private String version = "6.2";

	private ABCUtil abc;
	private long last_busy_time;
	private ITEM item = null;
	private RANGE range = null;
	private BANK bank = null;
	private boolean run = true;
	private long startTime;
	private String status = "";
	private int count = 0;
	private boolean hop = false;

	private PlayerSearch search;
	private int previousWorld = 0;
	private int[] WORLDS_TO_AVOID = new int[] { 302, 303, 304, 305, 306, 309,
			310, 318, 330, 351, 358, 370, 377, 378 };

	public void run() {

		Camera.setCameraAngle(100);
		abc = new ABCUtil();
		startTime = System.currentTimeMillis();
		int reply = JOptionPane.showConfirmDialog(null,
				"Would you like to change worlds if a player appears?",
				"World Hopper", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			println("Starting PlayerSearch thread.");
			search = new PlayerSearch();
			Thread searching = new Thread(search);
			searching.start();
		}
		String[] options = new String[] { "Jug", "Pineapple ring", "Anchovies",
				"Arrow shaft", "Plain pizza", "Baked potato" };
		int response = JOptionPane.showOptionDialog(null, "Select your option",
				"USA Money Maker", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (response == 0) {
			item = ITEM.JUG;
		} else if (response == 1) {
			item = ITEM.PINEAPPLE_RING;
		} else if (response == 2) {
			item = ITEM.ANCHOVIES;
			range = RANGE.NARDAH;
			bank = BANK.NARDAH;
		} else if (response == 3) {
			item = ITEM.ARROW_SHAFT;
		} else if (response == 4) {
			item = ITEM.PLAIN_PIZZA;
			range = RANGE.NARDAH;
			bank = BANK.NARDAH;
		} else if (response == 5) {
			item = ITEM.BAKED_POTATO;
			range = RANGE.NARDAH;
			bank = BANK.NARDAH;
		}

		while (run) {
			if (!hop) {
				if ((item.tool != null && Inventory.getCount(item.tool) == 0)
						|| Inventory.getCount(item.material) == 0) {
					if (!bank()) {
						run = false;
						status = "Missing items or out of supplies.";
					}
				} else {
					if (Banking.isBankScreenOpen()) {
						status = "Closing Bank";
						Banking.close();
						sleep((long) General.randomSD(932.5454545454545,
								484.03006165046871));
					}
					if (range != null) {
						if (Player.getPosition().distanceTo(range.location) > 2) {
							walkToTile(range.location, 1, 1);
						} else {
							if (Interfaces.isInterfaceValid(307)) {
								RSInterfaceChild cook = Interfaces.get(307, 6);
								if (cook != null) {
									sleep(abc.DELAY_TRACKER.ITEM_INTERACTION
											.next());
									if (cook.click("Cook All")) {
										status = "Cooking " + item.material;
										hoverBank();
										sleepWhileAnimating();
										status = "Finished Cooking!";
									}
									abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
								}
							} else {
								if (Game.getUptext().contains(
										"Use " + item.material + " ->")) {
									RSObject[] obj = Objects.findNearest(10,
											range.name);
									if (obj.length > 0 && obj[0] != null) {
										status = "Clicking " + range.name;
										sleep(abc.DELAY_TRACKER.NEW_OBJECT_COMBAT
												.next());
										obj[0].click();
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep((long) General.randomSD(
														482.5454545454545,
														142.03006165046871));
												return Interfaces
														.isInterfaceValid(307);
											}
										}, 5000);
										abc.DELAY_TRACKER.NEW_OBJECT_COMBAT
												.reset();
									}
								} else {
									clickItem(item.material);
								}
							}
						}
					} else {
						if (Inventory.open()) {
							RSInterfaceChild enterAmount = Interfaces.get(548,
									119);
							if (enterAmount != null && !enterAmount.isHidden()) {
								sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());
								int r = General.random(0, 3);
								if (r == 0) {
									String value = Integer.toString(Inventory
											.getCount(item.material));
									status = "Typing " + value;
									Keyboard.typeSend(value);
								} else {
									status = "Typing 27";
									Keyboard.typeSend("27");
								}
								hoverBank();
								status = "Making " + item.product;
								sleepWhileAnimating();
								abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
							} else if (Interfaces.isInterfaceValid(140)) {
								RSInterfaceChild slice = Interfaces.get(140, 2);
								if (slice != null && !slice.isHidden()) {
									status = "Slice the pineapple";
									sleep(abc.DELAY_TRACKER.ITEM_INTERACTION
											.next());
									if (slice.click()) {
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep((long) General.randomSD(
														482.5454545454545,
														142.03006165046871));
												return !Interfaces
														.isInterfaceValid(140);
											}
										}, 2000);
									}
									abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
								}
							} else if (Interfaces.isInterfaceValid(309)) {
								RSInterfaceChild make = Interfaces.get(309, 6);
								if (make != null && !make.isHidden()) {
									status = "Make Pineapple ring";
									sleep(abc.DELAY_TRACKER.ITEM_INTERACTION
											.next());
									if (make.click("Make All")) {
										status = "Making Pineapple rings";
										hoverBank();
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep((long) General.randomSD(
														482.5454545454545,
														142.03006165046871));
												return Inventory
														.getCount(item.material) == 0;
											}
										}, 10000);
										status = "We sliced all "
												+ item.material + "s";
									}
									abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
								}
							} else if (Interfaces.isInterfaceValid(305)) {
								RSInterfaceChild make = Interfaces.get(305, 9);
								if (make != null && !make.isHidden()) {
									status = "Make Arrow Shafts";
									sleep(abc.DELAY_TRACKER.ITEM_INTERACTION
											.next());
									if (make.click("Make X")) {
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep((long) General.randomSD(
														482.5454545454545,
														142.03006165046871));
												return Interfaces.get(548, 119) != null
														&& !Interfaces.get(548,
																119).isHidden();
											}
										}, 2000);
									}
									abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
								}
							} else {
								if (item.tool != null
										&& !Game.getUptext().contains("->")) {
									status = "Clicking our " + item.tool;
									RSItem[] tool = Inventory.find(item.tool);
									if (tool.length > 0 && tool[0] != null) {
										sleep(abc.DELAY_TRACKER.ITEM_INTERACTION
												.next());
										tool[0].click();
										abc.DELAY_TRACKER.ITEM_INTERACTION
												.reset();
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep((long) General.randomSD(
														482.5454545454545,
														142.03006165046871));
												return Game.getUptext()
														.contains("->");
											}
										}, 2000);
									}
								} else {
									clickItem(item.material);
								}
							}
						}
					}
				}
			} else {
				if (Login.getLoginState().equals(Login.STATE.INGAME))
					println("We need change worlds!");
				if (WorldHopper.getWorld() != previousWorld)
					previousWorld = WorldHopper.getWorld();
				if (Login.logout()) {
					int w = WorldHopper.getRandomWorld(true);
					if (w != previousWorld) {
						boolean valid = true;
						for (int n : WORLDS_TO_AVOID) {
							if (w == n) {
								println("Random world "
										+ w
										+ " is not a valid world. Let's select another one.");
								valid = false;
								break;
							}
						}
						if (valid) {
							println("Hopping to world " + w);
							if (WorldHopper.changeWorld(w))
								hop = false;
						}
					}
				}
			}

			abc.performRotateCamera();
			abc.performExamineObject();
			abc.performPickupMouse();
			abc.performRandomMouseMovement();
			abc.performRandomRightClick();
			abc.performQuestsCheck();
			abc.performFriendsCheck();
			abc.performCombatCheck();

			sleep((long) General.randomSD(183.5454545454545, 57.03006165046871));
		}

	}

	private void setCamera(int angle, int rotation) {
		abc.waitNewOrSwitchDelay(last_busy_time, true);
		if (Camera.getCameraRotation() > (rotation + 5)
				|| Camera.getCameraRotation() < (rotation - 5)) {
			int random = General.random(rotation - 3, rotation + 3);
			if (random < 0)
				random = 360 + random;
			if (random > 360)
				random = random - 360;
			status = "Setting Rotation to " + random;
			Camera.setCameraRotation(random);
		}
		if (Camera.getCameraAngle() > (angle + 5)
				|| Camera.getCameraAngle() < (angle - 5)) {
			int random = General.random(angle - 3, angle + 3);
			if (random > 100)
				random = 100;
			status = "Setting Angle to " + random;
			Camera.setCameraAngle(random);
		}
		last_busy_time = System.currentTimeMillis();
	}

	private void sleepWhileAnimating() {
		long sleep = (long) General.randomSD(1970.3465346534654,
				130.6103882150994);
		long animating = System.currentTimeMillis() + sleep;
		while (!hop && animating > System.currentTimeMillis()) {
			if (Player.getAnimation() != -1)
				animating = System.currentTimeMillis() + sleep;
			if (Inventory.getCount(item.material) == 0)
				break;
			sleep(25);
		}
	}

	private void clickItem(String name) {
		RSItem[] material = Inventory.find(name);
		if (material.length > 0 && material[0] != null) {
			status = "Clicking " + item.material;
			sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());
			if (material[0].click("Use")) {
				long time = (long) General.randomSD(482.5454545454545,
						142.03006165046871);
				long sleep = System.currentTimeMillis() + time;
				while (sleep > System.currentTimeMillis()) {
					if (Player.getAnimation() != -1) {
						status = "Player animating";
						sleep = System.currentTimeMillis() + time;
					}
					sleep((long) General.randomSD(123.5454545454545,
							72.03006165046871));
				}
			}
			abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
		}
	}

	public void walkToTile(final Positionable tile, int offset,
			int returnTileDistance) {
		abc.waitNewOrSwitchDelay(last_busy_time, true);
		DPathNavigator d = new DPathNavigator();
		RSTile[] path;
		if (Player.getPosition().distanceTo(tile) <= abc.INT_TRACKER.WALK_USING_SCREEN
				.next()) {
			RSTile[] screen_path = generateScreenPath(Player.getPosition(),
					tile);
			status = "Walking Screen Path";
			walkScreenPath(screen_path, returnTileDistance);
		} else {
			if (isTileOnMinimap(tile)) {
				status = "Clicking Minimap";
				clickTileMinimap(tile, offset, returnTileDistance);
			} else {
				path = d.findPath(tile);
				if (path.length > 0) {
					status = "Walking DPath";
					walkPath(path, offset, returnTileDistance);
				} else {
					if (!WebWalking.walkTo(tile)) {
						println("Your current location is not yet supported by the TRiBot Web Walking system!");
						run = false;
					}
				}
			}
		}
		last_busy_time = System.currentTimeMillis();
		abc.INT_TRACKER.WALK_USING_SCREEN.reset();
	}

	public void clickTileMinimap(Positionable tile, int offset,
			int returnTileDistance) {
		if (offset > 0)
			tile = randomizeTile(tile, offset);
		if (tile != null && !Player.isMoving()) {
			Walking.clickTileMM(tile, 1);
			sleepWhileMoving(tile, returnTileDistance);
		}
	}

	public void walkPath(RSTile[] path, int offset, int returnTileDistance) {
		if (path != null && path.length > 0) {
			long timer = System.currentTimeMillis() + 3000;
			while (timer > System.currentTimeMillis()
					&& Player.getPosition().distanceTo(path[path.length - 1]) > 1) {
				RSTile tile = null;
				if (isTileOnMinimap(path[path.length - 1])) {
					tile = path[path.length - 1];
				} else {
					for (int i = path.length - 1; i > 0; i--) {
						if (isTileOnMinimap(path[i])) {
							if (path[i - 1] != null) {
								tile = path[i - 1];
							} else {
								tile = path[i];
							}
							break;
						}
					}
				}
				if (tile != null) {
					tile = randomizeTile(tile, offset);
					Walking.clickTileMM(tile, 1);
					sleepWhileMoving(tile, returnTileDistance);
					timer = System.currentTimeMillis() + 3000;
				}
				sleep(100);
			}
		}
	}

	public void walkScreenPath(RSTile[] path, int returnTileDistance) {
		if (path.length > 0) {
			long timer = System.currentTimeMillis() + 2000;
			while (timer > System.currentTimeMillis()
					&& Player.getPosition().distanceTo(path[0]) == 0) {
				RSTile tile = null;
				for (RSTile t : path) {
					tile = t;
					if (t.isOnScreen())
						break;
				}
				if (tile != null) {
					timer = System.currentTimeMillis() + 2000;
					Walking.clickTileMS(tile, "Walk here");
					sleepWhileMoving(tile, returnTileDistance);
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
		while (sleep > System.currentTimeMillis()) {
			if (Player.isMoving())
				sleep = System.currentTimeMillis() + 1000;
			if (Player.getPosition().distanceTo(tile) <= distanceTo)
				break;
			sleep(500);
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
		array = new RSTile[valid.size()];
		array = valid.toArray(array);
		return array;
	}

	private void hoverBank() {
		RSObject[] bank = Objects.findNearest(20, "Bank booth");
		if (abc.BOOL_TRACKER.HOVER_NEXT.next()) {
			if (bank.length > 0 && bank[0] != null && bank[0].isOnScreen()) {
				status = "Hovering over bank booth";
				sleep((long) General.randomSD(1684.5454545454545,
						682.03006165046871));
				bank[0].hover();
			} else {
				Rectangle minimapRect = new Rectangle(537, 18, 200, 150);
				if (!minimapRect.contains(Mouse.getPos())) {
					status = "Hovering over minimap";
					sleep((long) General.randomSD(1684.5454545454545,
							682.03006165046871));
					Point p = Projection.tileToMinimap(bank[0]);
					p.x += General.random(-5, 5);
					p.y += General.random(-5, 5);
					Mouse.move(p);
				}
			}
		}
		abc.BOOL_TRACKER.HOVER_NEXT.reset();
	}

	private boolean bank() {
		if (bank == null && !Banking.isInBank()) {
			status = "Walking to Bank";
			WebWalking.walkToBank();
		} else if (bank != null && !bank.area.contains(Player.getPosition())) {
			status = "Walking to Bank";
			walkToTile(bank.location, 1, 2);
		} else {
			status = "Opening Bank";
			if (Banking.openBank()) {
				sleep((long) General.randomSD(932.5454545454545,
						123.03006165046871));
				if (Banking.isBankScreenOpen()) {
					if (Banking.getAll().length == 0)
						Banking.close();
					count += Inventory.getCount(item.product);
					if (item.tool == null && Inventory.getAll().length > 0) {
						status = "Depositing All";
						Banking.depositAll();
						sleep((long) General.randomSD(884.5454545454545,
								282.03006165046871));
					} else {
						status = "Depositing Items";
						Banking.depositAllExcept(item.tool);
						sleep((long) General.randomSD(884.5454545454545,
								282.03006165046871));
					}
					if (item.tool != null && Inventory.getCount(item.tool) == 0) {
						status = "Withdrawing " + item.tool;
						if (Banking.withdraw(1, item.tool)) {
							Timing.waitCondition(new Condition() {
								public boolean active() {
									sleep((long) General.randomSD(
											482.5454545454545,
											142.03006165046871));
									return Inventory.getCount(item.tool) > 0;
								}
							}, 2000);
							return true;
						} else {
							println("We do not have a " + item.tool);
							return false;
						}
					}
					if (Banking.withdraw(item.amount, item.material)) {
						if (item.amount == 0) {
							status = "Withdrawing all " + item.material;
						} else {
							status = "Withdrawing " + item.amount + " "
									+ item.material;
						}
						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep((long) General.randomSD(
										482.5454545454545, 142.03006165046871));
								return Inventory.getCount(item.material) > 0;
							}
						}, 2000);
						status = "Withdrawn " + item.material;
						return true;
					} else {
						println("We are out of " + item.material);
						return false;
					}
				}
			}
		}
		return true;
	}

	private enum RANGE {
		NARDAH("Clay Oven", new RSTile(3433, 2887, 0));

		private final String name;
		private final RSTile location;

		RANGE(String name, RSTile location) {
			this.name = name;
			this.location = location;
		}
	}

	private enum BANK {

		NARDAH(new RSTile(3427, 2891, 0), new RSArea(new RSTile(3427, 2889, 0),
				new RSTile(3430, 2894, 0)));

		private final RSTile location;
		private final RSArea area;

		BANK(RSTile location, RSArea area) {
			this.location = location;
			this.area = area;
		}
	}

	private enum ITEM {

		BAKED_POTATO("Potato", 0, null, "Baked potato"),

		PLAIN_PIZZA("Uncooked pizza", 0, null, "Plain pizza"),

		ARROW_SHAFT("Logs", 0, "Knife", "Arrow shaft"),

		ANCHOVIES("Raw anchovies", 0, null, "Anchovies"),

		PINEAPPLE_RING("Pineapple", 7, "Knife", "Pineapple ring"),

		JUG("Jug of wine", 0, null, "Jug");

		private final String material;
		private final int amount;
		private final String tool;
		private final String product;

		ITEM(String material, int amount, String tool, String product) {
			this.material = material;
			this.amount = amount;
			this.tool = tool;
			this.product = product;
		}
	}

	public class PlayerSearch implements Runnable {
		private volatile boolean stop = false;
		private String playerName = "";

		@Override
		public void run() {
			while (!stop) {
				try {
					if (Login.getLoginState().equals(Login.STATE.INGAME)
							&& !hop) {
						if (playerName.isEmpty())
							playerName = Player.getRSPlayer().getName();
						RSPlayer[] players = Players
								.getAll(new Filter<RSPlayer>() {
									public boolean accept(RSPlayer p) {
										if (p == null)
											return false;
										if (p.getName().equalsIgnoreCase(
												playerName))
											return false;
										return true;
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
										+ " tiles away from our player.");
								RSPlayerDefinition def = players[0]
										.getDefinition();
								if (def != null) {
									RSItem[] equipment = def.getEquipment();
									println("The user was wearing:");
									for (RSItem e : equipment) {
										RSItemDefinition d = e.getDefinition();
										if (d != null)
											println("- " + d.getName());
									}
									println("---");
								}
							}
							hop = true;
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

	public String addCommasToNumericString(String digits) {
		String result = "";
		int len = digits.length();
		int nDigits = 0;

		if (digits.length() == 1)
			return "0";

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
		int itemsPerHour = (int) (count * 3600000D / (System
				.currentTimeMillis() - startTime));

		Color background = new Color(24, 36, 82, 150);
		g.setColor(background);
		int spacing = 17;
		int rectX = 250;
		int rectY = 345;
		int width = 260;
		int height = 75;
		g.fillRoundRect(rectX, rectY, width, height, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(rectX, rectY, width, height, 5, 5);
		g.drawLine(rectX, rectY + spacing, rectX + width, rectY + spacing);

		int x = 260;
		int y = 358;
		Font bold = new Font("Tahoma", Font.BOLD, 12);
		g.setFont(bold);

		g.drawString("USA Money Maker                          v" + version, x,
				y);
		y += spacing + 3;
		g.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g.drawString("Status: " + status, x, y);
		y += spacing;
		g.drawString("Made " + count + " " + item.product + " ("
				+ addCommasToNumericString(Integer.toString(itemsPerHour))
				+ "/hr)", x, y);
		y += spacing;
	}
}
