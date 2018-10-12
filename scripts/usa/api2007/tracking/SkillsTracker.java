package scripts.usa.api2007.tracking;

import java.util.Arrays;
import java.util.HashMap;

import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.usa.api.util.Strings;

public class SkillsTracker {

	private long time;
	private HashMap<SKILLS, Skill> skillsMap = new HashMap<SKILLS, Skill>();

	public SkillsTracker() {
		setSkills();
	}

	public boolean isSet() {
		return !skillsMap.isEmpty();
	}

	public void setSkills() {
		if (Login.getLoginState() != Login.STATE.INGAME || Player.getRSPlayer() == null)
			return;
		time = System.currentTimeMillis();
		Arrays.stream(Skills.SKILLS.values()).forEach(skill -> skillsMap.put(skill, new Skill(Skills.getXP(skill), Skills.getActualLevel(skill))));
	}

	public long getStartTime() {
		return time;
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - time;
	}

	public int getAmountPerHour(int count) {
		return (int) (count * (3600000D / getElapsedTime()));
	}

	public String getAmountPerHourString(int count) {
		return "(" + Strings.format((int) (count * (3600000D / getElapsedTime()))) + "/hr)";
	}

	public String getTotalXPGained() {
		int xp = 0;
		for (SKILLS skill : skillsMap.keySet())
			xp += Skills.getXP(skill) - skillsMap.get(skill).getStartXP();
		return Strings.format(xp);
	}

	public String getXPPerHour() {
		int xp = 0;
		for (SKILLS skill : skillsMap.keySet())
			xp += Skills.getXP(skill) - skillsMap.get(skill).getStartXP();
		return Strings.format((int) (xp * (3600000D / getElapsedTime())));
	}

	public double getXPPerHour(SKILLS skill) {
		return (Skills.getXP(skill) - skillsMap.get(skill).getStartXP()) * (3600000D / getElapsedTime());
	}

	public String getXPPerHourString(SKILLS skill) {
		return "(" + Strings.format((Skills.getXP(skill) - skillsMap.get(skill).getStartXP()) * (3600000D / getElapsedTime())) + "/hr)";
	}

	public String getLevelsGained() {
		int gained = 0;
		for (SKILLS skill : skillsMap.keySet())
			gained += Skills.getActualLevel(skill) - skillsMap.get(skill).getStartLevel();
		return Strings.format(gained);
	}

	public String getXPGained(SKILLS skill) {
		return Strings.format(Skills.getXP(skill) - skillsMap.get(skill).getStartXP());
	}

	public String getLevelsGained(SKILLS skill) {
		return Strings.format(Skills.getActualLevel(skill) - skillsMap.get(skill).getStartLevel());
	}

	public String getStartXP(SKILLS skill) {
		return Strings.format(skillsMap.get(skill).getStartXP());
	}

	public String getStartLevel(SKILLS skill) {
		return Strings.format(skillsMap.get(skill).getStartLevel());
	}

	public long getTimeToNextLevel(SKILLS skill) {
		double xpToNextLevel = Skills.getXPToNextLevel(skill);
		double xpPerHour = getXPPerHour(skill);
		if (xpToNextLevel == 0 || xpPerHour == 0)
			return 0;
		return (long) ((xpToNextLevel / xpPerHour) * 3600000);
	}

	public static String getXPToNextLevel(SKILLS skill) {
		return Strings.format(Skills.getXPToNextLevel(skill));
	}

	public static String getNextLevel(SKILLS skill) {
		return Strings.format(Skills.getCurrentLevel(skill) + 1);
	}

	public class Skill {
		private int startXP;
		private int startLevel;

		public Skill(int startXP, int startLevel) {
			this.startXP = startXP;
			this.startLevel = startLevel;
		}

		public int getStartXP() {
			return this.startXP;
		}

		public int getStartLevel() {
			return this.startLevel;
		}
	}
}
