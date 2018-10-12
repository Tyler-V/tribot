package scripts.usa.api2007.worlds;

import java.awt.Rectangle;
import java.util.Comparator;

public class World {
	private int number;
	private WorldType type;
	private WorldActivity activity;
	private WorldCountry country;
	private int players;
	private String host;
	private Rectangle worldSelectBounds;

	public World(int number, WorldType type, WorldActivity activity, WorldCountry country, int players, String host) {
		this.number = number;
		this.type = type;
		this.activity = activity;
		this.country = country;
		this.players = players;
		this.host = host;
	}

	public int getNumber() {
		return this.number;
	}

	public WorldType getType() {
		return this.type;
	}

	public WorldActivity getActivity() {
		return this.activity;
	}

	public WorldCountry getCountry() {
		return this.country;
	}

	public int getPlayers() {
		return this.players;
	}

	public String getHost() {
		return this.host;
	}

	public Rectangle getWorldSelectBounds() {
		return worldSelectBounds;
	}

	public void setWorldSelectBounds(Rectangle worldSelectBounds) {
		this.worldSelectBounds = worldSelectBounds;
	}

	public static Comparator<World> WORLD_NUMBER_LOWEST = new Comparator<World>() {
		public int compare(World a, World b) {
			return a.getNumber() - b.getNumber();
		}
	};

	public static Comparator<World> WORLD_NUMBER_HIGHEST = new Comparator<World>() {
		public int compare(World a, World b) {
			return b.getNumber() - a.getNumber();
		}
	};

	public static Comparator<World> PLAYER_COUNT_LOWEST = new Comparator<World>() {
		public int compare(World a, World b) {
			return a.getPlayers() - b.getPlayers();
		}
	};

	public static Comparator<World> PLAYER_COUNT_HIGHEST = new Comparator<World>() {
		public int compare(World a, World b) {
			return b.getPlayers() - a.getPlayers();
		}
	};
}
