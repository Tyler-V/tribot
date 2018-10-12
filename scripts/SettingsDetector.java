package scripts;

import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Tools", name = "Settings Detector")
public class SettingsDetector extends Script {

	private int[] before = new int[Game.getSettingsArray().length];
	private int[] after = new int[Game.getSettingsArray().length];
	private boolean changes = false;

	@Override
	public void run() {

		for (int i = 0; i < Game.getSettingsArray().length; i++) {
			if (Game.getSetting(i) > 0) {
				sleep(25);
				before[i] = Game.getSetting(i);
			}
		}
		println("Please activate the change in game now!");
		println("5 seconds...");
		sleep(1000);
		println("4 seconds...");
		sleep(1000);
		println("3 seconds...");
		sleep(1000);
		println("2 seconds...");
		sleep(1000);
		println("1 seconds...");
		sleep(1000);
		println("Checking for any changes in the settings!");

		for (int i = 0; i < Game.getSettingsArray().length; i++) {
			if (Game.getSetting(i) > 0) {
				sleep(25);
				after[i] = Game.getSetting(i);
			}
		}

		for (int i = 0; i < after.length; i++) {
			if (before[i] != after[i]) {
				println("Setting [" + i + "] changed from " + before[i]
						+ " to " + after[i]);
				Player.getRSPlayer().getCombatLevel();
				changes = true;
			}
		}

		if (changes) {
			println("We found a change in the settings!");
		} else {
			println("No change detected!");
		}

	}
}