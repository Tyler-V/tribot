package scripts;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Randoms", name = "Lamp & Book XP Solver")
public class lampSolver extends Script {

	public int LAMP = 2528;
	public int BOOK = 11640;

	String chosen = "agility";

	@Override
	public void run() {

		while (Inventory.getCount(LAMP) > 0 || Inventory.getCount(BOOK) > 0) { // activator

			RSInterface chooseSkill = Interfaces.get(134, 0);
			RSInterface skill;
			if (chooseSkill != null) {
				if (chosen.equalsIgnoreCase("attack")) {
					skill = Interfaces.get(134, 3);
				} else if (chosen.equalsIgnoreCase("strength")) {
					skill = Interfaces.get(134, 4);
				} else if (chosen.equalsIgnoreCase("ranged")) {
					skill = Interfaces.get(134, 5);
				} else if (chosen.equalsIgnoreCase("magic")) {
					skill = Interfaces.get(134, 6);
				} else if (chosen.equalsIgnoreCase("defence")) {
					skill = Interfaces.get(134, 7);
				} else if (chosen.equalsIgnoreCase("hitpoints")) {
					skill = Interfaces.get(134, 8);
				} else if (chosen.equalsIgnoreCase("prayer")) {
					skill = Interfaces.get(134, 9);
				} else if (chosen.equalsIgnoreCase("agility")) {
					skill = Interfaces.get(134, 10);
				} else if (chosen.equalsIgnoreCase("herblore")) {
					skill = Interfaces.get(134, 11);
				} else if (chosen.equalsIgnoreCase("thieving")) {
					skill = Interfaces.get(134, 12);
				} else if (chosen.equalsIgnoreCase("crafting")) {
					skill = Interfaces.get(134, 13);
				} else if (chosen.equalsIgnoreCase("runecrafting")) {
					skill = Interfaces.get(134, 14);
				} else if (chosen.equalsIgnoreCase("slayer")) {
					skill = Interfaces.get(134, 22);
				} else if (chosen.equalsIgnoreCase("farming")) {
					skill = Interfaces.get(134, 23);
				} else if (chosen.equalsIgnoreCase("mining")) {
					skill = Interfaces.get(134, 15);
				} else if (chosen.equalsIgnoreCase("smithing")) {
					skill = Interfaces.get(134, 16);
				} else if (chosen.equalsIgnoreCase("fishing")) {
					skill = Interfaces.get(134, 17);
				} else if (chosen.equalsIgnoreCase("cooking")) {
					skill = Interfaces.get(134, 18);
				} else if (chosen.equalsIgnoreCase("firemaking")) {
					skill = Interfaces.get(134, 19);
				} else if (chosen.equalsIgnoreCase("woodcutting")) {
					skill = Interfaces.get(134, 20);
				} else if (chosen.equalsIgnoreCase("fletching")) {
					skill = Interfaces.get(134, 21);
				} else if (chosen.equalsIgnoreCase("construction")) {
					skill = Interfaces.get(134, 24);
				} else if (chosen.equalsIgnoreCase("hunter")) {
					skill = Interfaces.get(134, 25);
				} else {
					skill = Interfaces.get(134, 15); // default is
														// mining
				}
				if (skill != null) {
					skill.click();
					sleep(1500, 1750);
				}
				RSInterface confirm = Interfaces.get(134, 26);
				if (confirm != null) {
					confirm.click();
					sleep(400, 500);
				}
			} else {
				if (Inventory.getCount(LAMP) > 0) {
					RSItem[] lamp = Inventory.find(LAMP);
					if (lamp != null && lamp.length > 0) {
						lamp[0].click();
						sleep(1000, 1200);
					}
				} else if (Inventory.getCount(BOOK) > 0) {
					RSItem[] book = Inventory.find(BOOK);
					if (book != null && book.length > 0) {
						book[0].click();
						sleep(1000, 1200);
					}
				}
			}
			sleep(50, 150);
		}
	}
}
