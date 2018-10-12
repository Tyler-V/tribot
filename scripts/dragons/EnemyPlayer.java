package scripts.dragons;

public class EnemyPlayer {

	private String username;
	private int level;
	private int count;

	public EnemyPlayer(String username, int level, int count) {
		this.username = username;
		this.level = level;
		this.count = count;
	}

	public String getUsername() {
		return username;
	}

	public int getCombatLevel() {
		return level;
	}

	public int getCount() {
		return count;
	}

	public void add(int n) {
		count += n;
	}

}
