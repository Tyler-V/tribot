package scripts.usa.api2007.worlds;

public enum WorldActivity {

	PVP,
	DEADMAN,
	BOUNTY,
	TOURNAMENT,
	HIGH_RISK,
	SKILL_TOTAL,
	LMS,
	ELDER,
	PRIVATE_PRACTICE;

	public static WorldActivity get(String activity) {
		if (activity.contains("pvp")) {
			return WorldActivity.PVP;
		}
		else if (activity.contains("deadman")) {
			return WorldActivity.DEADMAN;
		}
		else if (activity.contains("bounty")) {
			return WorldActivity.BOUNTY;
		}
		else if (activity.contains("tournament")) {
			return WorldActivity.TOURNAMENT;
		}
		else if (activity.contains("risk")) {
			return WorldActivity.HIGH_RISK;
		}
		else if (activity.contains("skill")) {
			return WorldActivity.SKILL_TOTAL;
		}
		else if (activity.contains("lms")) {
			return WorldActivity.LMS;
		}
		else if (activity.contains("elder")) {
			return WorldActivity.ELDER;
		}
		else if (activity.contains("private")) {
			return WorldActivity.PRIVATE_PRACTICE;
		}
		return null;
	}

}
