package scripts;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.tribot.api.DynamicClicking;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Randoms", name = "Grave Digger")
public class Gravedigger extends Script {

	private boolean tookCoffins = false;
	private boolean checkedCoffins = false;
	private boolean talkedToLeo = false;

	// COFFIN TYPE DETECTORS
	private final int CRAFTING_BOWL_COFFIN = 7599;
	private final int MINING_PICKAXE_COFFIN = 7607;
	private final int COOKING_CHEF_HAT_COFFIN = 7601;
	private final int FARMING_SEED_COFFIN = 7609;
	private final int WOODCUTTING_HATCHET_COFFIN = 7603;

	// GRAVESTONE DETECTOR
	private final int CRAFTING_POT_GRAVESTONE = 7618;
	private final int FARMING_BUCKET_GRAVESTONE = 7617;
	private final int MINING_PICKAXE_GRAVESTONE = 7616;
	private final int COOKING_CHEF_HAT_GRAVESTONE = 7615;
	private final int WOODCUTTING_HATCHET_GRAVESTONE = 7614;

	private ArrayList<Coffin> coffins = new ArrayList<Coffin>();
	private ArrayList<Integer> solvedGraves = new ArrayList<Integer>();

	public void run() {

		Mouse.setSpeed(250);
		Camera.setCameraAngle(100);
		Camera.setCameraRotation(0);

		RSNPC Leo = findLeo();

		while (Leo != null) {
			if (!talkedToLeo) {
				talkToLeo();
			} else {
				if (!tookCoffins) {
					getCoffins();
				} else {
					if (!checkedCoffins) {
						checkCoffins();
					} else if (coffins() > 0) {
						placeCoffins();
					} else {
						talkToLeo();
					}
				}
			}
			sleep(100);
		}
	}

	private void talkToLeo() {
		RSNPC Leo = findLeo();

		if (Leo != null) {
			RSTile pos = Player.getPosition();
			if (pos != null) {
				if (pos.distanceTo(Leo) >= 3) {
					runTo(Leo.getPosition(), true);
				} else {
					RSInterfaceChild close = Interfaces.get(220, 17);
					if (!inChat()) {
						if (close != null) {
							talkedToLeo = !talkedToLeo;
							close.click();
							sleep(1500);
						} else {
							if (!Player.isMoving()) {
								Leo.click("Talk-to");
							}
						}
					} else {
						if (clickHereToContinue()) {
							NPCChat.clickContinue(true);
						}
					}
				}
			}
		}
	}

	private void placeCoffins() {

		RSTile playerPos = Player.getPosition();
		if (playerPos != null) {
			RSObject gravestone = getGravestone();
			if (gravestone != null) {
				RSTile gravestonePos = gravestone.getPosition();
				if (gravestonePos != null) {
					if (Player.getPosition().distanceTo(gravestonePos) > 5) {
						if (!Player.isMoving()) {
							runTo(gravestonePos, true);
						}
					} else {
						if (!Player.isMoving() && Player.getAnimation() == -1) {
							if (clickObject(gravestone, false, "")) {
								sleepUntilIdle();
								RSInterfaceChild gravestoneModel = Interfaces
										.get(143, 2);
								sleepUntilInterfaceUp(gravestoneModel);
								sleep(1000, 1200);
								String solution = "null";
								if (gravestoneModel != null) {
									int model = gravestoneModel.getModelID();
									if (model == CRAFTING_POT_GRAVESTONE) {
										solution = "Crafting";
									} else if (model == FARMING_BUCKET_GRAVESTONE) {
										solution = "Farming";
									} else if (model == MINING_PICKAXE_GRAVESTONE) {
										solution = "Mining";
									} else if (model == COOKING_CHEF_HAT_GRAVESTONE) {
										solution = "Cooking";
									} else if (model == WOODCUTTING_HATCHET_GRAVESTONE) {
										solution = "Woodcutting";
									}
									println("Grave solution: " + solution);
									RSInterfaceChild close = Interfaces.get(
											143, 3);
									if (close != null
											&& Screen.getColorAt(459, 31)
													.equals(new Color(255, 153,
															0))) {
										close.click();
										sleep(1500);
									}
									RSItem coffin = null;
									RSObject grave = Objects.find(50,
											(gravestone.getID() + 10))[0];
									if (grave != null) {
										for (int i = 0; i < coffins.size(); i++) {
											if (coffins.get(i).getSkill()
													.equals(solution)) {
												coffin = Inventory.find(coffins
														.get(i).getID())[0];
												break;
											}
										}
										// println(grave.getID());
										// println(coffin.getID());
										int count = coffins();
										if (coffin != null) {
											coffin.click("Use");
											sleep(1000, 1300);
											clickObject(grave, false, "");
											sleepUntilIdle();
											if (count != coffins()) {
												solvedGraves.add(gravestone
														.getID());
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

	private void checkCoffins() {
		RSItem[] inventoryCoffins = Inventory.find("Coffin");
		for (int i = 0; i < inventoryCoffins.length; i++) {
			openInventory();
			inventoryCoffins[i].click("Check");
			sleep(2000);
			RSInterfaceMaster coffin = Interfaces.get(141);
			if (coffin != null) {
				for (int k = 3; k < 12; k++) {
					RSInterfaceChild child = coffin.getChild(k);
					if (child != null) {
						int model = child.getModelID();
						if (model == CRAFTING_BOWL_COFFIN) {
							println("Coffin: " + inventoryCoffins[i].getID()
									+ " - Crafting");
							coffins.add(new Coffin(inventoryCoffins[i].getID(),
									"Crafting"));
						} else if (model == MINING_PICKAXE_COFFIN) {
							println("Coffin: " + inventoryCoffins[i].getID()
									+ " - Mining");
							coffins.add(new Coffin(inventoryCoffins[i].getID(),
									"Mining"));
						} else if (model == COOKING_CHEF_HAT_COFFIN) {
							println("Coffin: " + inventoryCoffins[i].getID()
									+ " - Cooking");
							coffins.add(new Coffin(inventoryCoffins[i].getID(),
									"Cooking"));
						} else if (model == FARMING_SEED_COFFIN) {
							println("Coffin: " + inventoryCoffins[i].getID()
									+ " - Farming");
							coffins.add(new Coffin(inventoryCoffins[i].getID(),
									"Farming"));
						} else if (model == WOODCUTTING_HATCHET_COFFIN) {
							println("Coffin: " + inventoryCoffins[i].getID()
									+ " - Woodcutting");
							coffins.add(new Coffin(inventoryCoffins[i].getID(),
									"Woodcutting"));
						}
					}
				}
			} else {
				coffins.clear();
				break;
			}
		}
		if (coffins.size() == 5) {
			checkedCoffins = !checkedCoffins;
			RSInterfaceChild close = Interfaces.get(141, 12);
			if (close != null
					&& Screen.getColorAt(459, 31)
							.equals(new Color(255, 153, 0))) {
				close.click();
				sleep(1500);
			}
		}
	}

	private void sleepUntilInterfaceUp(RSInterface chat) {
		Timer t = new Timer(3000);
		while (t.isRunning()) {
			if (chat != null) {
				break;
			}
			sleep(50, 100);
		}
	}

	private void openInventory() {
		if (GameTab.getOpen() != TABS.INVENTORY) {
			Keyboard.pressKey((char) KeyEvent.VK_ESCAPE);
			sleep(50, 150);
		}
	}

	private void getCoffins() {

		RSTile playerPos = Player.getPosition();
		if (playerPos != null) {
			if (coffins() == 5) {
				tookCoffins = !tookCoffins;
			} else {
				RSObject coffin = getObject("Take-Coffin");
				if (coffin != null) {
					RSTile coffinPos = coffin.getPosition();
					if (coffinPos != null) {
						if (Player.getPosition().distanceTo(coffinPos) > 5) {
							if (!Player.isMoving()) {
								runTo(coffinPos, true);
							}
						} else {
							if (!Player.isMoving()
									&& Player.getAnimation() == -1) {
								if (clickObject(coffin, true, "Take-Coffin")) {
									sleepUntilIdle();
								}
							}
						}
					}
				}
			}
		}
	}

	private void sleepUntilIdle() {
		Timer t = new Timer(1000);

		while (t.isRunning()) {
			if (Player.isMoving() || Player.getAnimation() != -1) {
				t.reset();
			}
			sleep(50, 100);
		}
	}

	private boolean clickObject(RSObject obj, boolean rightClick, String action) {
		Point p = null;
		Polygon model = obj.getModel().getEnclosedArea();
		if (model != null) {
			p = new Point((int) model.getBounds2D().getCenterX(), (int) model
					.getBounds2D().getCenterY());
			if (p != null) {
				Mouse.move(p);
				if (rightClick) {
					Mouse.click(3);
					sleep(100, 250);
					if (ChooseOption.isOpen()) {
						if (ChooseOption.select(action)) {
							return true;
						} else {
							ChooseOption.select("Cancel");
							return false;
						}
					}
				} else {
					Mouse.click(1);
					return true;
				}
			}
		}
		return false;
	}

	private RSObject getGravestone() {
		RSObject[] obj = Objects.getAll(50);
		ArrayList<RSObject> sortedObjects = new ArrayList<RSObject>();
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null && obj[i].getDefinition() != null) {
					String[] actions = obj[i].getDefinition().getActions();
					if (actions != null && actions.length > 0) {
						for (int j = 0; j < actions.length; j++) {
							if (actions[j] != null) {
								if (actions[j].equalsIgnoreCase("Read")) {
									boolean found = false;
									for (int k = 0; k < solvedGraves.size(); k++) {
										if (obj[i].getID() == solvedGraves
												.get(k)) {
											found = true;
										}
									}
									if (!found) {
										sortedObjects.add(obj[i]);
									}
								}
							}
						}
					}
				}
			}
			RSTile playerPos = Player.getPosition();
			if (playerPos != null) {
				RSObject closest = sortedObjects.get(0);
				int distance = playerPos.distanceTo(closest);
				for (int i = 1; i < sortedObjects.size(); i++) {
					RSObject nextObject = sortedObjects.get(i);
					if (nextObject != null) {
						RSTile objectPos = nextObject.getPosition();
						if (objectPos != null) {
							if (objectPos.distanceTo(playerPos) < distance) {
								closest = nextObject;
								distance = objectPos.distanceTo(playerPos);
							}
						}
					}
				}
				return closest;
			}
		}
		return null;
	}

	private RSObject getObject(String s) {
		RSObject[] obj = Objects.getAll(50);
		ArrayList<RSObject> sortedObjects = new ArrayList<RSObject>();
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null && obj[i].getDefinition() != null) {
					String[] actions = obj[i].getDefinition().getActions();
					if (actions != null && actions.length > 0) {
						for (int j = 0; j < actions.length; j++) {
							if (actions[j] != null) {
								if (actions[j].equalsIgnoreCase(s)) {
									sortedObjects.add(obj[i]);
								}
							}
						}
					}
				}
			}
			RSTile playerPos = Player.getPosition();
			if (playerPos != null) {
				RSObject closest = sortedObjects.get(0);
				int distance = playerPos.distanceTo(closest);
				for (int i = 1; i < sortedObjects.size(); i++) {
					RSObject nextObject = sortedObjects.get(i);
					if (nextObject != null) {
						RSTile objectPos = nextObject.getPosition();
						if (objectPos != null) {
							if (objectPos.distanceTo(playerPos) < distance) {
								closest = nextObject;
								distance = objectPos.distanceTo(playerPos);
							}
						}
					}
				}
				return closest;
			}
		}
		return null;
	}

	private int coffins() {
		return Inventory.getCount(new String[] { "Coffin" });
	}

	private boolean clickHereToContinue() {
		if (NPCChat.getClickContinueInterface() != null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean inChat() {
		if (NPCChat.getMessage() != null) {
			return true;
		} else {
			return false;
		}
	}

	public void runTo(RSTile tile, boolean screenWalk) {
		if (!Player.isMoving()) {
			if (!tile.isOnScreen() || screenWalk == false) {
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				Walking.walkTo(tile);
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
			} else if (tile.isOnScreen() && screenWalk == true) {
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				DynamicClicking.clickRSTile(tile, "Walk here");
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
			}
		}
	}

	private RSNPC findLeo() {
		RSNPC[] npcs = NPCs.getAll();

		if (npcs != null && npcs.length == 1) {
			if (npcs[0] != null) {
				String name = npcs[0].getName();
				if (name != null && name.equals("Leo")) {
					return npcs[0];
				}
			}
		}
		return null;
	}

	private class Coffin {
		private int ID;
		private String skill;

		public Coffin(int ID, String skill) {
			this.ID = ID;
			this.skill = skill;
		}

		public int getID() {
			return ID;
		}

		public String getSkill() {
			return skill;
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