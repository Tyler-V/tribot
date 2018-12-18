package scripts.usa.api2007.tracking;

import java.util.Arrays;
import java.util.Comparator;
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
		Arrays.stream(Skills.SKILLS.values())
				.forEach(skill -> skillsMap.put(skill, new Skill(Skills.getXP(skill), Skills.getActualLevel(skill))));
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

	public SKILLS getSkillBeingTrained() {
		Comparator<SKILLS> xpGained = new Comparator<SKILLS>() {
			@Override
			public int compare(SKILLS a, SKILLS b) {
				double xpPerHourA = getXPPerHour(a);
				double xpPerHourB = getXPPerHour(b);
				if (xpPerHourA > xpPerHourB) {
					return -1;
				}
				else if (xpPerHourA < xpPerHourB) {
					return 1;
				}
				else {
					int xpToNextLevelA = Skills.getXPToNextLevel(a);
					int xpToNextLevelB = Skills.getXPToNextLevel(b);
					return xpToNextLevelA < xpToNextLevelB ? -1 : (xpToNextLevelA > xpToNextLevelB ? 1 : 0);
				}
			}
		};
		return skillsMap.keySet()
				.stream()
				.filter(skill -> getXPGained(skill) > 0)
				.sorted(xpGained)
				.findFirst()
				.get();
	}

	public String getTotalXPGained() {
		int xp = 0;
		for (SKILLS skill : skillsMap.keySet())
			xp += Skills.getXP(skill) - skillsMap.get(skill)
					.getStartXP();
		return Strings.format(xp);
	}

	public String getXPPerHour() {
		int xp = 0;
		for (SKILLS skill : skillsMap.keySet())
			xp += Skills.getXP(skill) - skillsMap.get(skill)
					.getStartXP();
		return Strings.format((int) (xp * (3600000D / getElapsedTime())));
	}

	public double getXPPerHour(SKILLS skill) {
		return (Skills.getXP(skill) - skillsMap.get(skill)
				.getStartXP()) * (3600000D / getElapsedTime());
	}

	public String getXPPerHourString(SKILLS skill) {
		return "(" + Strings.format((Skills.getXP(skill) - skillsMap.get(skill)
				.getStartXP()) * (3600000D / getElapsedTime())) + "/hr)";
	}

	public String getLevelsGained() {
		int gained = 0;
		for (SKILLS skill : skillsMap.keySet())
			gained += Skills.getActualLevel(skill) - skillsMap.get(skill)
					.getStartLevel();
		return Strings.format(gained);
	}

	public int getXPGained(SKILLS skill) {
		return Skills.getXP(skill) - skillsMap.get(skill)
				.getStartXP();
	}

	public String getXPGainedString(SKILLS skill) {
		return Strings.format(getXPGained(skill));
	}

	public int getLevelsGained(SKILLS skill) {
		return Skills.getActualLevel(skill) - skillsMap.get(skill)
				.getStartLevel();
	}

	public String getLevelsGainedString(SKILLS skill) {
		return Strings.format(getLevelsGained(skill));
	}

	public String getStartXP(SKILLS skill) {
		return Strings.format(skillsMap.get(skill)
				.getStartXP());
	}

	public String getStartLevel(SKILLS skill) {
		return Strings.format(skillsMap.get(skill)
				.getStartLevel());
	}

	public long getTimeToNextLevel(SKILLS skill) {
		double xpToNextLevel = Skills.getXPToNextLevel(skill);
		double xpPerHour = getXPPerHour(skill);
		if (xpToNextLevel == 0 || xpPerHour == 0)
			return 0;
		return (long) ((xpToNextLevel / xpPerHour) * 3600000);
	}

	public static int getXPToNextLevel(SKILLS skill) {
		return Skills.getXPToNextLevel(skill);
	}

	public static String getXPToNextLevelString(SKILLS skill) {
		return Strings.format(getXPToNextLevel(skill));
	}

	public static String getNextLevel(SKILLS skill) {
		return Strings.format(Math.min(Skills.getActualLevel(skill) + 1, 99));
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
