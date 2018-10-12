package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JOptionPane;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Soft Clay Maker")
public class UsaClayMiner extends Script implements Painting {

	private final String version = "3.0";

	// OPTIONS
	boolean TO_BANK_BY_GLORY = false;
	boolean TO_BANK_BY_RING = false;
	boolean TO_BANK_BY_WALKING = false;
	boolean TO_BANK_BY_DEPOSIT_BOX = false;
	boolean MAKE_CLAY_EDGEVILLE = false;
	//
	private final RSArea CASTLE_WARS = new RSArea(new RSTile(2435, 3070),
			new RSTile(2460, 3105, 0));
	private final RSArea EDGEVILLE = new RSArea(new RSTile(3077, 3475, 0),
			new RSTile(3125, 3520, 0));
	private final RSArea RIMMINGTON_MINE = new RSArea(new RSTile(2972, 3229),
			new RSTile(2990, 3251, 0));
	private final RSArea WELL_AREA = new RSArea(new RSTile(2952, 3208),
			new RSTile(2960, 3216, 0));
	private final RSTile CLAY_LOCATION = new RSTile(2986, 3240, 0);
	private final RSTile WELL_LOCATION = new RSTile(2957, 3214, 0);
	private final RSTile HOUSE_LOCATION = new RSTile(2953, 3223, 0);
	private final RSTile DEPOSIT_BOX_LOCATION = new RSTile(3045, 3235, 0);

	private final int[] RING_OF_DUELLING = { 2552, 2554, 2556, 2558, 2560,
			2562, 2564, 2566 };
	private final int[] PICKAXES = new int[] { 1275, 1273, 1271, 1269, 1267,
			1265 };
	private final int[] PICKAXE_HEADS = { 480, 482, 484, 486, 488, 490 };
	private final int PICKAXE_HANDLE = 466;
	private final int[] CLAY_ROCKS = new int[] { 7481, 7483 };
	private final int[] CLAY_ROCKS_MINED = new int[] { 11173, 11171 };
	private final int WELL = 884;
	private final int HOUSE_TABLET = 8013;
	private final int SOFT_CLAY = 1761;
	private final int CLAY = 434;
	private final int BUCKET_EMPTY = 1925;
	private final int BUCKET_FULL = 1929;
	private final int[] BUCKETS = new int[] { BUCKET_EMPTY, BUCKET_FULL };
	private final int OUTSIDE_PORTAL = 15478;
	private final int HOUSE_GLORY = 13523;
	private final int HOUSE_PORTAL = 4525;
	private final int MOUNTED_GLORY = 13523;
	private final int[] DONT_DROP = new int[] { BUCKET_EMPTY, BUCKET_FULL,
			CLAY, HOUSE_TABLET, 2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566,
			1275, 1273, 1271, 1269, 1267, 1265, 1275, 1273, 1271, 1269, 1267,
			1265, 480, 482, 484, 486, 488, 490, 466 };
	private Point EMPTY_BUCKET_POSITION = null;
	private boolean equipped = false;

	private int SOFT_CLAY_COUNT = 0;
	private int trips = 0;
	private boolean run = true;
	private STATE state = null;

	private long startTime;
	private boolean gui_is_up = true;
	private ABCUtil abc;

	public void run() {

		String[] options = new String[] { "Mounted Glory", "Ring of Duelling",
				"Deposit Box", "Walking", "Edgeville Soft Clay Maker" };
		int response = JOptionPane.showOptionDialog(null,
				"What method would you like to use?", "USA Soft Clay Maker v"
						+ version, JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (response == 0) {
			println("Using Mounted Glory");
			TO_BANK_BY_GLORY = true;
		} else if (response == 1) {
			println("Using Ring of Duelling");
			TO_BANK_BY_RING = true;
		} else if (response == 2) {
			println("Using Deposit Box");
			TO_BANK_BY_DEPOSIT_BOX = true;
		} else if (response == 3) {
			println("Walking to Bank");
			TO_BANK_BY_WALKING = true;
		} else if (response == 4) {
			println("Making Soft Clay in Edgeville");
			MAKE_CLAY_EDGEVILLE = true;
		}

		abc = new ABCUtil();
		startTime = System.currentTimeMillis();
		Camera.setCameraAngle(100);

		while (run) {

			state = getState();

			if (state == STATE.EDGEVILLE_WELL) {
				if (!Player.isMoving()) {
					RSObject[] objs = Objects.find(30, "Well");
					if (objs.length > 0) {
						RSObject well = objs[0];
						if (well != null) {
							if (well.isOnScreen()) {
								openInventory();
								if (Inventory.getCount(BUCKET_EMPTY) != 0) {
									RSItem bucket = Inventory
											.find(BUCKET_EMPTY)[0];
									if (bucket != null) {
										bucket.click();
										sleep(500, 900);
										well.click();
										Timing.waitCondition(
												new Condition() {
													public boolean active() {
														sleep(300, 500);
														return Inventory
																.getCount(BUCKET_EMPTY) == 0;
													}
												},
												Inventory
														.getCount(BUCKET_EMPTY) * 2000);
									}
								} else {
									RSItem bucket = Inventory.find(BUCKET_FULL)[0];
									if (bucket != null) {
										RSItem clay = Inventory.find(CLAY)[0];
										if (clay != null) {
											bucket.click();
											Timing.waitCondition(
													new Condition() {
														public boolean active() {
															sleep(200, 300);
															return Game
																	.getUptext()
																	.contains(
																			"->");
														}
													}, 2000);
											clay.click();
											Timing.waitCondition(
													new Condition() {
														public boolean active() {
															sleep(200, 300);
															return Interfaces
																	.isInterfaceValid(309);
														}
													}, 2000);
											RSInterfaceChild menu = Interfaces
													.get(309, 6);
											sleep(200, 500);
											if (menu != null) {
												menu.click("Make All");
												Timing.waitCondition(
														new Condition() {
															public boolean active() {
																sleep(500, 1000);
																return Inventory
																		.getCount(CLAY) == 0;
															}
														}, 2000 * Inventory
																.getCount(CLAY));
											}
											if (Inventory.getCount(CLAY) == 0) {
												SOFT_CLAY_COUNT += Inventory
														.getCount(SOFT_CLAY);
												trips++;
											}
										}
									}
								}
							} else {
								if (!Player.isMoving()) {
									Walking.setControlClick(true);
									PathFinding.aStarWalk(new RSTile(3086,
											3502, 0));
									Walking.setControlClick(false);
								}
							}
						}
					}
				}
			} else if (state == STATE.TO_MINE) {
				if (!Player.isMoving()) {
					WebWalking.walkTo(CLAY_LOCATION);
					sleep(250, 500);
				}
			} else if (state == STATE.MINE_CLAY) {
				if (isEquipped(PICKAXES) || Inventory.getCount(PICKAXES) > 0) {
					if (Inventory.getAll().length != (Inventory
							.getCount(BUCKETS) + Inventory.getCount(CLAY))) {
						openInventory();
						Inventory.dropAllExcept(DONT_DROP);
					}

					if (Player.getPosition().distanceTo(CLAY_LOCATION) >= 5) {
						Walking.walkTo(CLAY_LOCATION);
					} else if (!Player.isMoving()) {
						RSObject[] rock = Objects.findNearest(10, CLAY_ROCKS);
						RSObject[] mined = Objects.find(10, CLAY_ROCKS_MINED);
						if (rock.length > 0) {
							if (rock[0] != null) {
								RSModel model = rock[0].getModel();
								if (model != null) {
									Point[] points = model
											.getAllVisiblePoints();
									if (points.length > 0) {
										Point p = getBestPoint(points);
										if (p != null) {
											if (Mouse.getPos().distance(p) > 10) {
												Mouse.click(p, 1);
											} else {
												Mouse.click(1);
											}
											if (rock.length > 1
													&& rock[1] != null) {
												rock[1].hover();
											} else if (mined.length > 0
													&& mined[0] != null) {
												mined[0].hover();
											}
											final int count = Inventory
													.getCount(CLAY);
											Timing.waitCondition(
													new Condition() {
														public boolean active() {
															sleep(50, 150);
															return count != Inventory
																	.getCount(CLAY);
														}
													}, General.random(2500,
															3000));
										}
									}
								}
							}
						}
					}
				}
			} else if (state == STATE.TO_WELL) {
				if (!Player.isMoving()) {
					WebWalking.walkTo(WELL_LOCATION);
					sleep(250, 500);
				}
			} else if (state == STATE.MAKE_SOFT_CLAY) {
				RSItem[] full = Inventory.find(BUCKET_FULL);
				openInventory();
				if (full.length > 0) {
					if (full[0] != null) {
						RSItem[] clay = Inventory.find(CLAY);
						if (clay[0] != null) {
							final int count = Inventory.getCount(SOFT_CLAY);
							Point pFull = getPoint(full[0]);
							if (pFull != null) {
								Mouse.hop(pFull);
								sleep(50, 100);
								Mouse.click(1);
								sleep(50, 150);
								Point pClay = getPoint(clay[0]);
								if (pClay != null) {
									Mouse.hop(pClay);
									sleep(50, 100);
									Mouse.click(1);
									sleep(100, 300);
								}
							}
							if (EMPTY_BUCKET_POSITION != null)
								Mouse.move(
										EMPTY_BUCKET_POSITION.x
												+ General.random(-15, 15),
										EMPTY_BUCKET_POSITION.y
												+ General.random(-15, 15));
							Timing.waitCondition(new Condition() {
								public boolean active() {
									sleep(50, 150);
									return Inventory.getCount(SOFT_CLAY) != count;
								}
							}, 1500);
							if (Inventory.getCount(SOFT_CLAY) != count) {
								SOFT_CLAY_COUNT++;
							}
						}
					}
				} else {
					RSObject[] well = Objects.find(20, WELL);
					if (well.length > 0) {
						if (well[0] != null) {
							if (well[0].isOnScreen()) {
								RSItem[] empty = Inventory.find(BUCKET_EMPTY);
								if (empty.length > 0) {
									if (empty[0] != null) {
										if (EMPTY_BUCKET_POSITION == null) {
											int x = (int) empty[0].getArea()
													.getCenterX();
											int y = (int) empty[0].getArea()
													.getCenterY();
											EMPTY_BUCKET_POSITION = new Point(
													x, y);
										}
										Mouse.move(EMPTY_BUCKET_POSITION);
										sleep(50, 100);
										Mouse.click(1);
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep(50, 150);
												return Game
														.getUptext()
														.contains(
																"Use Bucket ->");
											}
										}, 1500);
										if (Game.getUptext().contains(
												"Use Bucket ->")) {
											if (well[0] != null) {
												RSModel model = well[0]
														.getModel();
												if (model != null) {
													Point p = getBestPoint(model
															.getAllVisiblePoints());
													Mouse.hop(p);
													sleep(50, 100);
													Mouse.click(1);
													Timing.waitCondition(
															new Condition() {
																public boolean active() {
																	sleep(50,
																			150);
																	return Inventory
																			.getCount(BUCKET_FULL) > 0;
																}
															}, 1500);
												}
											}
										}
									}
								}
							} else {
								if (!Player.isMoving()) {
									Walking.walkTo(WELL_LOCATION);
									sleep(250, 500);
								}
							}
						}
					}
				}
			} else if (state == STATE.WALK_TO_HOUSE) {
				if (Interfaces.isInterfaceValid(232)) {
					RSInterfaceChild myHouse = Interfaces.get(232, 1);
					myHouse.click();
					sleepUntilAtHouse();
				} else {
					RSObject[] portal = Objects.find(30, OUTSIDE_PORTAL);
					if (portal.length > 0) {
						if (portal[0] != null) {
							Camera.setCameraRotation(90);
							if (portal[0].isOnScreen()) {
								if (!Player.isMoving()) {
									portal[0].click();
									Timing.waitCondition(new Condition() {
										public boolean active() {
											sleep(50, 150);
											return Interfaces
													.isInterfaceValid(232);
										}
									}, 3000);
								}
							} else {
								if (!Player.isMoving()) {
									Walking.walkTo(HOUSE_LOCATION);
									sleep(250, 500);
								}
							}
						}
					}
				}
			} else if (state == STATE.MOUNTED_GLORY) {
				RSObject[] glory = Objects.find(20, MOUNTED_GLORY);
				if (glory.length > 0) {
					if (glory[0] != null) {
						if (!glory[0].isOnScreen()) {
							Camera.turnToTile(glory[0]);
						}
						if (glory[0].isOnScreen()) {
							if (!Player.isMoving()) {
								if (clickModel(glory[0])) {
									Timing.waitCondition(new Condition() {
										public boolean active() {
											sleep(200, 500);
											return at(EDGEVILLE);
										}
									}, 4000);
								}
							}
						} else {
							if (!Player.isMoving()) {
								Walking.walkTo(glory[0]);
								sleep(250, 500);
							}
						}
					}
				}
			} else if (state == STATE.BANK) {
				if (Banking.isBankScreenOpen()) {

					Banking.depositAllExcept(DONT_DROP);

					if (TO_BANK_BY_RING || TO_BANK_BY_GLORY) {
						if (Inventory.getCount(HOUSE_TABLET) == 0) {
							if (bankContainsItem(HOUSE_TABLET)) {
								Banking.withdraw(1, HOUSE_TABLET);
								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(200, 500);
										return Inventory.getCount(HOUSE_TABLET) > 0;
									}
								}, 2000);
							} else {
								println("We are out of House Tablets!");
								run = false;
							}
						}
					}

					if (TO_BANK_BY_RING && !isEquipped(RING_OF_DUELLING)) {
						if (Inventory.getCount(RING_OF_DUELLING) > 0) {
							Banking.close();
							Timing.waitCondition(new Condition() {
								public boolean active() {
									sleep(200, 500);
									return !Banking.isBankScreenOpen();
								}
							}, 2000);
							if (!Banking.isBankScreenOpen()) {
								RSItem[] ring = Inventory
										.find(RING_OF_DUELLING);
								if (ring.length > 0) {
									openInventory();
									if (ring[0] != null) {
										ring[0].click();
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep(200, 500);
												return isEquipped(RING_OF_DUELLING);
											}
										}, 2000);
									}
								}
							}
						} else {
							if (bankContainsItems(RING_OF_DUELLING)) {
								Banking.withdraw(1, RING_OF_DUELLING);
								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(200, 500);
										return Inventory
												.getCount(RING_OF_DUELLING) > 0;
									}
								}, 2000);
							} else {
								println("We are out of Ring of Duelling!");
								run = false;
							}
						}
					}

					if (MAKE_CLAY_EDGEVILLE) {
						int empty = Inventory.getCount(BUCKET_FULL);
						int full = Inventory.getCount(BUCKET_EMPTY);
						if ((empty + full) < 14) {
							if (!Banking.withdraw(14 - (empty + full),
									BUCKET_EMPTY))
								run = false;
						}
						if (Inventory.getCount(CLAY) < 14) {
							if (!Banking.withdraw(14, CLAY))
								run = false;
							Timing.waitCondition(new Condition() {
								public boolean active() {
									sleep(200, 500);
									return Inventory.getCount(CLAY) >= 14;
								}
							}, 2000);
						}
					}
				} else {

					if (MAKE_CLAY_EDGEVILLE) {
						if (!Banking.openBank()) {
							if (!Player.isMoving()) {
								PathFinding
										.aStarWalk(new RSTile(3095, 3495, 0));
							}
						}
					} else {
						if (!Player.isMoving()) {
							Banking.openBank();
						}
					}
				}
			} else if (state == STATE.TELEPORT_TO_HOUSE) {
				if (Banking.isBankScreenOpen()) {
					if (Banking.close()) {
						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep(200, 500);
								return !Banking.isBankScreenOpen();
							}
						}, 2000);
					}
				} else {
					trips++;
					breakTablet();
				}
			} else if (state == STATE.LEAVE_HOUSE) {
				RSObject[] portal = Objects.find(20, HOUSE_PORTAL);
				if (portal.length > 0) {
					Camera.setCameraRotation(180);
					if (portal[0] != null) {
						portal[0].click();
						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep(200, 500);
								return outsideHouse();
							}
						}, 3000);
					}
				}
			} else if (state == STATE.TO_CASTLE_WARS) {
				if (isEquipped(RING_OF_DUELLING)) {
					openEquipmentTab();
					RSItem[] ring = Equipment.find(RING_OF_DUELLING);
					if (ring.length > 0) {
						if (ring[0] != null) {
							ring[0].click("Castle Wars");
							Timing.waitCondition(new Condition() {
								public boolean active() {
									sleep(200, 500);
									return at(CASTLE_WARS);
								}
							}, 3000);
						}
					}
				} else {
					println("We do not have a Ring of Duelling!");
					run = false;
				}
			} else if (state == STATE.WALK_TO_BANK) {
				if (!Player.isMoving()) {
					WebWalking.walkToBank();
				}
			} else if (state == STATE.DEPOSIT_BOX) {
				if (Player.getPosition().distanceTo(DEPOSIT_BOX_LOCATION) > 3) {
					WebWalking.walkTo(DEPOSIT_BOX_LOCATION);
				} else {
					RSItem[] clay = Inventory.find(SOFT_CLAY);
					if (clay.length > 0) {
						RSObject[] box = Objects.find(10, 26254);
						if (box.length > 0) {
							if (clay[0] != null) {
								clay[0].click();
								sleep(800, 1000);
								if (box[0] != null) {
									box[0].click();
									Timing.waitCondition(new Condition() {
										public boolean active() {
											sleep(200, 500);
											return Interfaces
													.isInterfaceValid(232);
										}
									}, 2000);
									RSInterfaceChild deposit = Interfaces.get(
											232, 4);
									if (deposit != null && !deposit.isHidden()) {
										deposit.click();
										Timing.waitCondition(new Condition() {
											public boolean active() {
												sleep(200, 500);
												return Inventory
														.getCount(SOFT_CLAY) == 0;
											}
										}, 2000);
									}
								}
							}
						}
					}
				}
			}
			abc.performXPCheck(SKILLS.MINING);
			abc.performRotateCamera();
			abc.performExamineObject();
			abc.performPickupMouse();
			abc.performRandomMouseMovement();
			abc.performRandomRightClick();
			abc.performFriendsCheck();
			abc.performLeaveGame();
			abc.performCombatCheck();
			sleep(50);
		}
	}

	public enum STATE {
		MINE_CLAY, TO_WELL, MAKE_SOFT_CLAY, WALK_TO_HOUSE, MOUNTED_GLORY, TELEPORT_TO_HOUSE, BANK, TO_MINE, TO_CASTLE_WARS, LEAVE_HOUSE, WALK_TO_BANK, DEPOSIT_BOX, EDGEVILLE_WELL
	}

	public STATE getState() {
		if (Inventory.isFull()) {
			if (Inventory.getCount(CLAY) > 0) {
				if (MAKE_CLAY_EDGEVILLE) {
					return STATE.EDGEVILLE_WELL;
				} else if (at(WELL_AREA)) {
					return STATE.MAKE_SOFT_CLAY;
				} else {
					return STATE.TO_WELL;
				}
			} else {
				if (Banking.isInBank() || at(EDGEVILLE) || at(CASTLE_WARS)) {
					return STATE.BANK;
				} else {
					if (TO_BANK_BY_RING) {
						return STATE.TO_CASTLE_WARS;
					} else if (TO_BANK_BY_GLORY) {
						if (outsideHouse()) {
							return STATE.WALK_TO_HOUSE;
						} else if (insideHouse()) {
							return STATE.MOUNTED_GLORY;
						}
					} else if (TO_BANK_BY_WALKING) {
						return STATE.WALK_TO_BANK;
					} else if (TO_BANK_BY_DEPOSIT_BOX) {
						return STATE.DEPOSIT_BOX;
					}
				}
			}
		} else {
			if (Banking.isInBank() || at(EDGEVILLE) || at(CASTLE_WARS)) {
				if (Inventory.getCount(HOUSE_TABLET) == 0
						|| (TO_BANK_BY_RING && !isEquipped(RING_OF_DUELLING))) {
					return STATE.BANK;
				} else {
					return STATE.TELEPORT_TO_HOUSE;
				}
			} else if (insideHouse()) {
				return STATE.LEAVE_HOUSE;
			} else if (at(RIMMINGTON_MINE)) {
				return STATE.MINE_CLAY;
			} else {
				return STATE.TO_MINE;
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

	private boolean clickModel(RSObject obj) {
		if (obj != null) {
			RSObjectDefinition def = obj.getDefinition();
			if (def != null) {
				final String name = def.getName();
				if (name != null) {
					RSModel model = obj.getModel();
					if (model != null) {
						Point[] points = model.getAllVisiblePoints();
						if (points.length > 0) {
							for (int i = 0; i < points.length; i++) {
								Mouse.move(points[i * 3]);
								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep(200, 500);
										return Game.getUptext().contains(name);
									}
								}, General.random(100, 500));
								if (Game.getUptext().contains(name)) {
									break;
								}
							}
							if (Game.getUptext().contains(name)) {
								Mouse.click(1);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private Point getPoint(RSItem item) {
		Point p = null;
		int x = (int) item.getArea().getCenterX();
		int y = (int) item.getArea().getCenterY();
		if (x > 0 && y > 0) {
			p = new Point(x + General.random(-10, 10), y
					+ General.random(-10, 10));
			return p;
		}
		return p;
	}

	private void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			GameTab.open(TABS.INVENTORY);
		}
	}

	private void openEquipmentTab() {
		if (GameTab.getOpen() != TABS.EQUIPMENT) {
			GameTab.open(TABS.EQUIPMENT);
		}
	}

	private void breakTablet() {
		if (Inventory.getCount(HOUSE_TABLET) > 0) {
			RSItem[] house = Inventory.find(HOUSE_TABLET);
			if (house.length > 0) {
				openInventory();
				if (house[0] != null) {
					house[0].click();
					sleepUntilAtHouse();
				}
			}
		}
	}

	private boolean sleepUntilAtHouse() {
		Timing.waitCondition(new Condition() {
			public boolean active() {
				sleep(200, 500);
				return insideHouse();
			}
		}, 6000);
		if (insideHouse()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean at(RSArea area) {
		RSTile pos = Player.getPosition();
		return area.contains(pos);
	}

	private boolean outsideHouse() {
		return Objects.find(50, OUTSIDE_PORTAL).length > 0;
	}

	private boolean insideHouse() {
		return Objects.find(50, HOUSE_PORTAL).length > 0;
	}

	private boolean isEquipped(int[] id) {
		RSItem[] equipment = Equipment.getItems();
		for (int i = 0; i < equipment.length; i++) {
			for (int j = 0; j < id.length; j++) {
				RSItem item = equipment[i];
				if (item != null) {
					if (item.getID() == id[j]) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean bankContainsItems(int[] ID) {
		long timer = Timing.currentTimeMillis() + 10000;
		while (timer > Timing.currentTimeMillis()) {
			if (Banking.isBankScreenOpen()) {
				if (Banking.find(ID).length > 0) {
					return true;
				}
			}
			sleep(500);
		}
		return false;
	}

	private boolean bankContainsItem(int ID) {
		long timer = Timing.currentTimeMillis() + 10000;
		while (timer > Timing.currentTimeMillis()) {
			if (Banking.isBankScreenOpen()) {
				if (Banking.find(ID).length > 0) {
					return true;
				}
			}
			sleep(500);
		}
		return false;
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

	@Override
	public void onPaint(Graphics g) {

		int clayPerHour = (int) (SOFT_CLAY_COUNT * 3600000D / (System
				.currentTimeMillis() - startTime));
		int profitEach = 0;
		if (MAKE_CLAY_EDGEVILLE) {
			profitEach = 90;
		} else {
			profitEach = 190;
		}
		int profit = SOFT_CLAY_COUNT * profitEach;
		int profitPerHour = Math.round(((int) (profit * 3600000D / (System
				.currentTimeMillis() - startTime))) / 1000);
		int tripsPerHour = (int) (trips * 3600000D / (System
				.currentTimeMillis() - startTime));

		g.setColor(Color.BLUE);
		g.fillRect(296, 356, 195, 93);
		g.setColor(Color.WHITE);
		g.drawRect(295, 355, 197, 95);
		Font font = new Font("Verdana", 0, 14);
		g.setFont(font);
		int x = 300;
		int y = 370;
		g.setColor(Color.WHITE);
		g.drawString("USA Soft Clay Maker v" + version, x, y);
		y += 15;
		g.setColor(Color.WHITE);
		g.drawString(
				"Time: "
						+ Timing.msToString(System.currentTimeMillis()
								- startTime), x, y);
		y += 15;
		g.drawString("State: " + state, x, y);
		y += 15;
		g.drawString("Trips: " + trips + " (" + tripsPerHour + "/HR)", x, y);
		y += 15;
		g.drawString("Soft Clay: " + SOFT_CLAY_COUNT + " (" + clayPerHour
				+ "/HR)", x, y);
		y += 15;
		g.drawString("Profit: " + profit + " GP (" + profitPerHour + "K/HR)",
				x, y);
	}

}