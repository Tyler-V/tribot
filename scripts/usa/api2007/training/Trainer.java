package scripts.usa.api2007.training;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;

import scripts.usa.api.util.Strings;

public class Trainer {

	private final int MASTER_COMBAT_INTERFACE = 593;
	private HashMap<SKILLS, TrainingLevel> map;
	private SKILLS currentSkill;
	private TrainingMode mode;

	private double check_stats_percent;
	private double check_xp_percent;
	private Boolean should_check_stats;
	private Boolean should_check_xp;

	public Trainer(HashMap<SKILLS, TrainingLevel> map) {
		while (Login.getLoginState() != Login.STATE.INGAME)
			General.sleep(1000);

		this.map = map;
		this.init();
	}

	public Trainer() {
		map = new HashMap<SKILLS, TrainingLevel>();
		map.put(SKILLS.ATTACK, new TrainingLevel(Integer.MAX_VALUE));
		map.put(SKILLS.STRENGTH, new TrainingLevel(Integer.MAX_VALUE));
		map.put(SKILLS.DEFENCE, new TrainingLevel(Integer.MAX_VALUE));
		this.init();
	}

	private void init() {
		this.check_stats_percent = ThreadLocalRandom.current().nextDouble(.25, .50) * 100;
		this.check_xp_percent = ThreadLocalRandom.current().nextDouble(.25, .50) * 100;
		System.out.println("Generated a " + Math.round(check_stats_percent) + "% chance to check stats when changing skills.");
		System.out.println("Generated a " + Math.round(check_xp_percent) + "% chance to check XP when changing skills.");
	}

	private boolean shouldCheckStats() {
		if (should_check_stats == null)
			should_check_stats = this.generateNextStatCheck();
		return this.should_check_stats;
	}

	private boolean shouldCheckXP() {
		if (should_check_xp == null)
			should_check_xp = this.generateNextXPCheck();
		return this.should_check_xp;
	}

	private boolean generateNextStatCheck() {
		Random r = new Random();
		return r.nextDouble() * 100 < this.check_stats_percent;
	}

	private boolean generateNextXPCheck() {
		Random r = new Random();
		return r.nextDouble() * 100 < this.check_xp_percent;
	}

	public void setSkill() {
		if (currentSkill != null) {
			if (map.get(currentSkill).getDesiredLevel() < Skills.getActualLevel(currentSkill))
				return;
			if (Skills.getXP(currentSkill) < map.get(currentSkill).getNextExperience())
				return;
		}

		SKILLS nextSkill = getLowestSkill();

		if (shouldCheckStats()) {
			checkStats();
			if (shouldCheckXP()) {
				checkXP(nextSkill);
			}
		}

		General.println(currentSkill == null ? "Setting attack style to " + nextSkill + "."
				: "Changing from " + currentSkill + " to " + nextSkill + ".");

		if (setAttackStyle(nextSkill)) {
			currentSkill = nextSkill;
			int xpToNextLevel = Skills.getXPToNextLevel(currentSkill);
			Random r = new Random();
			int xpToGain = (int) (xpToNextLevel * r.nextDouble());
			map.get(currentSkill).setNextExperience(Skills.getXP(currentSkill) + xpToGain);
			General.println("After leveling up, we will gain " + Strings.format(xpToGain) + " xp in " + currentSkill + " before changing skills.");
		}
	}

	public void setAttackStyle() {
		setAttackStyle(currentSkill);
	}

	private boolean checkStats() {
		if (GameTab.open(TABS.STATS)) {
			General.sleep(General.randomSD(2000, 1000));
			return true;
		}
		return false;
	}

	private void checkXP(SKILLS skill) {
		if (skill.hover())
			General.sleep(General.randomSD(3000, 1000));
	}

	private SKILLS getLowestSkill() {
		List<SKILLS> lowest = new ArrayList<SKILLS>();
		map.forEach((skill, level) -> {
			int currentLevel = Skills.getActualLevel(skill);
			if (currentLevel < level.getDesiredLevel()) {
				if (lowest.isEmpty()) {
					lowest.add(skill);
				}
				else {
					if (Skills.getActualLevel(lowest.get(0)) > currentLevel) {
						lowest.clear();
						lowest.add(skill);
					}
					else if (Skills.getActualLevel(lowest.get(0)) == currentLevel) {
						lowest.add(skill);
					}
				}
			}
		});
		Collections.shuffle(lowest);
		return lowest.get(0);
	}

	private boolean setAttackStyle(SKILLS SKILL) {
		if (GameTab.open(TABS.COMBAT)) {
			General.sleep(General.randomSD(2000, 500));
			RSInterfaceMaster master = Interfaces.get(MASTER_COMBAT_INTERFACE);
			if (master == null)
				return false;
			RSInterfaceChild[] children = master.getChildren();
			if (children.length == 0)
				return false;
			for (RSInterfaceChild child : children) {
				if (child == null)
					continue;
				String text = child.getText();
				if (text == null)
					continue;
				if (mode == TrainingMode.COMBAT && (text.equals("Lash"))) {
					return clickStyle(child);
				}
				else if (SKILL == SKILLS.ATTACK) {
					if (text.contains("Punch") || text
							.equals("Chop") || text.equals("Stab") || text.contains("Spike") || text.equals("Flick") || text.equals("Bash")) {
						return clickStyle(child);
					}
				}
				else if (SKILL == SKILLS.STRENGTH) {
					if (text.equals("Slash") || text
							.equals("Kick") || text.equals("Hack") || text.equals("Smash") || text.equals("Impale") || text.equals("Pound")) {
						return clickStyle(child);
					}
				}
				else if (SKILL == SKILLS.DEFENCE) {
					if (text.equals("Block") || text.equals("Deflect") || text.equals("Focus")) {
						return clickStyle(child);
					}
				}
			}
		}
		return false;
	}

	private boolean clickStyle(RSInterfaceChild child) {
		if (child == null)
			return false;
		Rectangle rectangle = child.getAbsoluteBounds();
		if (rectangle == null)
			return false;
		Point point = new Point(rectangle.x + rectangle.width / 6, rectangle.y - rectangle.height / 2);
		if (Screen.getColorAt(point).getRed() < 100) {
			Mouse.clickBox(rectangle, 1);
			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(500);
					return Screen.getColorAt(point).getRed() >= 100;
				}
			}, General.randomSD(3000, 500));
		}
		return Screen.getColorAt(point).getRed() >= 100;
	}

	public enum TrainingMode {
		STATS,
		COMBAT
	}
}
