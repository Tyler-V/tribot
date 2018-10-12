package scripts;

import java.awt.event.KeyEvent;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Randoms", name = "Freaky Forester")
public class FreakyForester extends Script {

	private int PORTAL = 8972;
	private int PHEASANT = 0;
	private int ONE = 574, TWO = 575, THREE = 576, FOUR = 577;
	private int[] RAW_PHEASANT = { 6178, 6179 };
	private boolean pickedUpRawPheasant = false;

	@Override
	public void run() {

		// super.setRandomSolverState(false);
		Mouse.setSpeed(200);
		RSNPC[] freakyForester = NPCs.find("Freaky Forester");
		println("Starting Freaky Forester!");

		while (freakyForester != null) {

			RSInterface chat = Interfaces.get(242, 2);
			RSInterface valuablesOnFloor = Interfaces.get(566, 18);

			if (valuablesOnFloor != null) {
				println("Closing Valuables Screen!");
				valuablesOnFloor.click();
				sleep(1000);
			} else {

				if (PHEASANT == 0) {
					if (chat != null && !chat.isHidden()) {
						if (PHEASANT == 0) {
							if (chat.getText().contains("one")) {
								println("We've been assigned to kill a one tailed pheasant!");
								PHEASANT = ONE;
							} else if (chat.getText().contains("two")) {
								println("We've been assigned to kill a two tailed pheasant!");
								PHEASANT = TWO;
							} else if (chat.getText().contains("three")) {
								println("We've been assigned to kill a three tailed pheasant!");
								PHEASANT = THREE;
							} else if (chat.getText().contains("four")) {
								println("We've been assigned to kill a four tailed pheasant!");
								PHEASANT = FOUR;
							}
							println("Attacking pheasant!");
						}
					} else {
						if (freakyForester != null && freakyForester.length > 0) {
							if (freakyForester[0].isOnScreen()
									&& !Player.isMoving()) {
								DynamicClicking.clickRSNPC(freakyForester[0],
										"Talk-to");
								println("Talking to Freaky Forester!");
								waitUntilIdle();
							} else {
								runTo(freakyForester[0].getPosition());
								waitUntilIdle();
							}
						}
					}
				} else {
					if (Inventory.getCount(RAW_PHEASANT) == 0
							&& !pickedUpRawPheasant) {
						RSNPC[] npc = NPCs.find(PHEASANT);
						RSGroundItem[] raw = GroundItems
								.findNearest(RAW_PHEASANT);
						if (raw != null && raw.length > 0) {
							if (raw[0].isOnScreen()) {
								if (Inventory.isFull()) {
									RSItem[] items = Inventory.getAll();
									if (items != null && items.length >= 0) {
										items[items.length - 1].click("Drop");
										sleep(700, 900);
									}
								}
								raw[0].click("Take");
								waitUntilIdle();
								if (Inventory.getCount(RAW_PHEASANT) > 0) {
									pickedUpRawPheasant = true;
									println("We have raw pheasant!");
								}
							} else {
								runTo(raw[0].getPosition());
								waitUntilIdle();
							}
						} else {
							if (npc != null && npc.length > 0) {
								if (npc[0].isOnScreen()) {
									if (!Player.getRSPlayer().isInCombat()) {
										DynamicClicking.clickRSNPC(npc[0],
												"Attack");
									}
									waitUntilIdle();
								} else {
									runTo(npc[0].getPosition());
									waitUntilIdle();
								}
							}
						}
					} else if (Inventory.getCount(RAW_PHEASANT) == 1
							&& pickedUpRawPheasant) {
						if (freakyForester != null && freakyForester.length > 0) {
							if (freakyForester[0].isOnScreen()
									&& !Player.isMoving()) {
								DynamicClicking.clickRSNPC(freakyForester[0],
										"Talk-to");
								println("Giving Freaky Forester the raw pheasant!");
								waitUntilIdle();
							} else {
								runTo(freakyForester[0].getPosition());
								waitUntilIdle();
							}
						}
					} else if (Inventory.getCount(RAW_PHEASANT) == 0
							&& pickedUpRawPheasant) {
						RSObject[] portal = Objects.find(50, PORTAL);
						if (portal != null && portal.length > 0) {
							if (portal[0].isOnScreen() && !Player.isMoving()) {
								DynamicClicking.clickRSObject(portal[0],
										"Enter");
								println("Leaving Portal, Freaky Forester Complete!");
								waitUntilIdle();
							} else {
								runTo(portal[0].getPosition());
								waitUntilIdle();
							}
						}
					}
				}
			}
		}
	}

	public void runTo(RSTile tile) {
		if (!Player.isMoving() && Player.getPosition().distanceTo(tile) > 1) {
			if (!tile.isOnScreen()) {
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				Walking.walkTo(tile);
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
				waitUntilIdle();
			} else {
				Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
				DynamicClicking.clickRSTile(tile, "Walk here");
				Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
				waitUntilIdle();
			}
		}
	}

	public void waitUntilIdle() {
		long t = System.currentTimeMillis();

		while (Timing.timeFromMark(t) < General.random(1500, 2000)) {
			if (Player.isMoving() || Player.getAnimation() != -1
					|| Player.getRSPlayer().isInCombat()) {
				t = System.currentTimeMillis();
			} else {
				break;
			}
			sleep(50, 150);
		}
	}
}