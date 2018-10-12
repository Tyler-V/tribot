package scripts;

import java.awt.event.KeyEvent;

import org.tribot.api.DynamicClicking;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Randoms", name = "Frog Cave")
public class FrogCave extends Script {

	public void run() {

		Mouse.setSpeed(250);

		while (countFrogs() == 8) {
			RSNPC queen = getQueenFrog();

			if (queen != null) {
				RSTile playerPos = Player.getPosition();
				if (playerPos != null && playerPos.distanceTo(queen.getPosition()) > 3) {
					if (!Player.isMoving()) {
						runTo(queen.getPosition(), true);
					}
				} else {
					// if chat not up...
					queen.click("Talk-to");
					// else
					// use current talking method, everything else should work

				}
			}
		}

	}

	public void runTo(RSTile tile, boolean screenWalk) {
		if (!Player.isMoving() && Player.getPosition().distanceTo(tile) > 1) {
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

	private RSNPC getQueenFrog() {
		RSNPC[] frogs = NPCs.getAll();

		if (frogs != null && frogs.length > 0) {
			for (int i = 0; i < frogs.length; i++) {
				if (frogs[i] != null) {
					String name = frogs[i].getName();
					if (name != null && name.equals("Frog")) {
						int count = 0;
						for (int j = 0; j < frogs.length; j++) {
							if (frogs[i].getID() == frogs[j].getID()) {
								count++;
							}
						}
						if (count == 1) {
							return frogs[i];
						}
					}
				}
			}
		}
		return null;
	}

	private int countFrogs() {
		RSNPC[] frogs = NPCs.getAll();
		int count = 0;

		if (frogs != null && frogs.length > 0) {
			for (int i = 0; i < frogs.length; i++) {
				if (frogs[i] != null) {
					String name = frogs[i].getName();
					if (name != null && name.equals("Frog")) {
						count++;
					}
				}
			}
		}

		return count;
	}

}