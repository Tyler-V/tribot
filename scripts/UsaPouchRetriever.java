package scripts;

import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.tribot.api.DynamicClicking;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "USA", name = "USA Pouch Retriever")
public class UsaPouchRetriever extends Script {

	private final int AIR_RUNE = 556;
	private final int EARTH_RUNE = 557;
	private final int DUST_RUNE = 4696;
	private int LAW_RUNE = 563;
	private final int HOUSE_TELEPORT = 8013;
	private final int SMALL_POUCH = 5509;
	private final int MEDIUM_POUCH = 5510;
	private final int LARGE_POUCH = 5512;
	private final int GIANT_POUCH = 5514;
	private final int[] POUCHES = { SMALL_POUCH, MEDIUM_POUCH, LARGE_POUCH,
			GIANT_POUCH };
	private int FOOD;
	private RSTile fightTile;
	private RSTile resetTile;
	private int count = 0;
	private boolean AFK;

	public void run() {

		Walking.setControlClick(true);
		Object[] possibleValues = { "FIGHT ABYSSALS", "AFK" };
		Object selectedValue = JOptionPane.showInputDialog(null,
				"Would you like to FIGHT or AFK", "Input",
				JOptionPane.INFORMATION_MESSAGE, null, possibleValues,
				possibleValues[0]);
		if (selectedValue.equals(possibleValues[0])) {
			println("Fighting");
			AFK = false;
		} else {
			println("AFK");
			AFK = true;
		}

		// (3013, 4837, 0)
		String afk = JOptionPane.showInputDialog(null,
				"Please enter the FIGHT tile!", "(3013, 4837, 0)");
		afk = afk.replaceAll("\\(", "").replaceAll("\\)", "")
				.replaceAll("\\s", "");
		String[] tile1 = afk.split(",");
		fightTile = new RSTile(Integer.parseInt(tile1[0]),
				Integer.parseInt(tile1[1]), Integer.parseInt(tile1[2]));

		// (3052, 4853, 0)
		if (AFK) {
			String reset = JOptionPane.showInputDialog(null,
					"Please enter the RESET tile!", "(3052, 4853, 0)");
			reset = reset.replaceAll("\\(", "").replaceAll("\\)", "")
					.replaceAll("\\s", "");
			String[] tile2 = reset.split(",");
			resetTile = new RSTile(Integer.parseInt(tile2[0]),
					Integer.parseInt(tile2[1]), Integer.parseInt(tile2[2]));
		}

		String foodid = JOptionPane.showInputDialog(null,
				"Please enter the Food ID!", "379");
		FOOD = Integer.parseInt(foodid);

		println("-----------------------------------------------------------------");
		println("Ensure you have lobsters, a house teleport tablet, and a small pouch in your inventory!");
		println("Have auto-retaliate set to (On) and equip your best Abyss NPC killing setup!");
		println("If you run out of food before you aquire all of the pouches, keep the pouches you did aquire in your inventory and set it up/run again!");
		println("Start inside the Abyss and it will do the rest, enjoy!");
		println("-----------------------------------------------------------------");

		Mouse.setSpeed(200);

		Camera.setCameraAngle(100);
		Camera.setCameraRotation(360);

		while (true) {

			if (Inventory.getCount(SMALL_POUCH) == 0) {
				println("You must have a small pouch in your inventory to aquire additional pouches!");
				break;
			}

			int level = Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING);
			if (level >= 75) {
				if (Inventory.getCount(MEDIUM_POUCH) > 0
						&& Inventory.getCount(LARGE_POUCH) > 0
						&& Inventory.getCount(GIANT_POUCH) > 0) {
					teleportToHouse();
					if (atHouse()) {
						println("-----------------------------------------------------------------");
						println("We've acquired a Giant, Large, and Medium pouch!");
						println("-----------------------------------------------------------------");
						break;
					}
				}
			} else if (level >= 50) {
				if (Inventory.getCount(MEDIUM_POUCH) > 0
						&& Inventory.getCount(LARGE_POUCH) > 0) {
					teleportToHouse();
					if (atHouse()) {
						println("-----------------------------------------------------------------");
						println("We've acquired a Large and Medium pouch!");
						println("-----------------------------------------------------------------");
						break;
					}
				}
			} else if (level >= 25) {
				if (Inventory.getCount(MEDIUM_POUCH) > 0) {
					teleportToHouse();
					if (atHouse()) {
						println("-----------------------------------------------------------------");
						println("We've acquired a Medium pouch!");
						println("-----------------------------------------------------------------");
						break;
					}
				}
			} else {
				println("You are below level 25, you can't get any pouches other than the small one!");
				break;
			}

			if (Inventory.getCount(FOOD) > 0) {
				if ((Skills.getCurrentLevel(Skills.SKILLS.HITPOINTS) + 24) < Skills
						.getActualLevel(Skills.SKILLS.HITPOINTS)) {
					openInventory();
					RSItem[] food = Inventory.find(FOOD);
					if (food != null && food.length > 0) {
						food[0].click();
						sleep(1200, 1300);
					}
				} else {
					if (AFK) {
						if (count > 25) {
							if (Player.getPosition().distanceTo(resetTile) > 2) {
								println("Resetting NPC aggressiveness!");
								PathFinding.aStarWalk(resetTile);
							} else {
								count = 0;
							}
						} else {
							if (Player.getPosition().distanceTo(fightTile) > 0) {
								println("Moving to fighting tile!");
								if (fightTile.isOnScreen()) {
									runTo(fightTile, true);
								} else {
									PathFinding.aStarWalk(fightTile);
								}
							} else {
								if (Player.getRSPlayer()
										.getInteractingCharacter() != null
										&& Player.getRSPlayer()
												.getInteractingCharacter()
												.getCombatCycle() > 0) {
									count = 0;
									RSGroundItem[] pouches = GroundItems
											.find(POUCHES);
									if (Inventory.isFull()) {
										openInventory();
										RSItem[] food = Inventory.find(FOOD);
										if (food != null && food.length > 0) {
											food[0].click();
											sleep(1200, 1300);
										}
									}
									if (!Player.isMoving()) {
										if (pouches != null
												&& pouches.length > 0) {
											if (pouches[0].getPosition()
													.isOnScreen()) {
												Mouse.move(Projection.tileToScreen(
														pouches[0]
																.getPosition(),
														0));
												sleep(200);
												Mouse.click(3);
												if (ChooseOption.isOpen()) {
													ChooseOption
															.select("pouch");
													sleep(800, 1000);
												}
											} else {
												PathFinding
														.aStarWalk(pouches[0]
																.getPosition());
											}
										}
									}
								} else {
									count++;
									sleep(250);
								}
							}
						}

					} else { // FIGHT

						if (Player.getPosition().distanceTo(fightTile) > 15) {
							println("Moving to fighting tile!");
							if (fightTile.isOnScreen()) {
								runTo(fightTile, true);
							} else {
								PathFinding.aStarWalk(fightTile);
							}
						} else {
							if (Player.getRSPlayer().getInteractingCharacter() != null
									&& Player.getRSPlayer()
											.getInteractingCharacter()
											.getCombatCycle() > 0) {
								count = 0;
								RSGroundItem[] pouches = GroundItems
										.find(POUCHES);
								if (Inventory.isFull()) {
									openInventory();
									RSItem[] food = Inventory.find(FOOD);
									if (food != null && food.length > 0) {
										food[0].click();
										sleep(1200, 1300);
									}
								}
								if (!Player.isMoving()) {
									if (pouches != null && pouches.length > 0) {
										if (pouches[0].getPosition()
												.isOnScreen()) {
											Mouse.move(Projection.tileToScreen(
													pouches[0].getPosition(), 0));
											sleep(200);
											Mouse.click(3);
											if (ChooseOption.isOpen()) {
												ChooseOption.select("pouch");
												sleep(800, 1000);
											}
										} else {
											PathFinding.aStarWalk(pouches[0]
													.getPosition());
										}
									}
								}
							} else {
								RSNPC[] npcs = NPCs.find("Abyssal leech");
								RSNPC leech = null;
								int closest = 20;
								if (npcs.length > 0) {
									for (int i = 0; i < npcs.length; i++) {
										if (npcs[i].getPosition().distanceTo(
												fightTile) < 15
												&& !npcs[i].isInCombat()) {
											if (npcs[i]
													.getPosition()
													.distanceTo(
															Player.getPosition()) < closest) {
												leech = npcs[i];
												closest = npcs[i]
														.getPosition()
														.distanceTo(
																Player.getPosition());
											}
										}
									}
								}

								if (leech != null) {
									if (!leech.isOnScreen()) {
										if (!Player.isMoving()) {
											PathFinding.aStarWalk(leech);
										}
									} else {
										leech.click("Attack");
										Timer attacking = new Timer(1000);
										while (attacking.isRunning()) {
											if (Player.isMoving()) {
												attacking.reset();
											}
										}
									}
								}
							}
						}
					}

				}
			} else {
				if (atHouse()) {
					println("Awh shucks! We ran out of lobsters before we could aquire all of the pouches.");
					println("Set it up and run it again, ensure you keep all pouches in your inventory! (even the one's we just aquired)");
					break;
				} else {
					teleportToHouse();
				}
			}
			sleep(400, 500);
		}
	}

	public void runTo(RSTile tile, boolean screenWalk) {
		if (!Player.isMoving() && Player.getPosition().distanceTo(tile) > 0) {
			if (!tile.isOnScreen() || screenWalk == false) {
				Walking.walkTo(tile);

			} else if (tile.isOnScreen() && screenWalk == true) {
				DynamicClicking.clickRSTile(tile, "Walk here");
			}
		}
	}

	private void teleportToHouse() {
		if (Inventory.getCount(HOUSE_TELEPORT) > 0) {
			useTablet();
		} else if (haveRegularRunes() || haveDustRunes()) {
			castTeleport();
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

	private void useTablet() {
		RSItem[] house = Inventory.find(HOUSE_TELEPORT);

		openInventory();

		if (house != null && house.length > 0) {
			if (house[0] != null) {
				house[0].click();
				sleepUntilAtHouse();
			}
		}
	}

	private void castTeleport() {
		if (GameTab.getOpen() != TABS.MAGIC) {
			GameTab.open(TABS.MAGIC);
		}
		RSInterfaceChild teleportSpell = Interfaces.get(192, 23);
		if (teleportSpell != null) {
			teleportSpell.click();
			sleep(300, 500);
			openInventory();
			sleepUntilAtHouse();
		}
	}

	public void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			GameTab.open(TABS.INVENTORY);
			sleep(50, 150);
		}
	}

	private boolean atHouse() {
		Object portal = getObject("Lock", 10);
		if (portal != null) {
			return true;
		} else {
			return false;
		}
	}

	private RSObject getObject(String s, int distance) {
		RSObject[] obj = Objects.getAll(distance);
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null && obj[i].getDefinition() != null) {
					String[] actions = obj[i].getDefinition().getActions();
					if (actions != null && actions.length > 0) {
						for (int j = 0; j < actions.length; j++) {
							if (actions[j] != null) {
								if (actions[j].equalsIgnoreCase(s)) {
									return obj[i];
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void sleepUntilAtHouse() {
		Timer t = new Timer(2000);

		while (t.isRunning()) {
			if (atHouse()) {
				break;
			}
			if (Player.getAnimation() != -1
					&& !Player.getRSPlayer().isInCombat()) {
				t.reset();
			}
			sleep(50, 100);
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
}
