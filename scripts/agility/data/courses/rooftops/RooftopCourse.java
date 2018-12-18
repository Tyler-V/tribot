package scripts.agility.data.courses.rooftops;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.tribot.api2007.Game;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;

import scripts.usa.api.util.Strings;

public enum RooftopCourse {

	DRAYNOR_ROOFTOP(10, Arrays.asList(Obstacle.DRAYNOR_ROUGH_WALL, Obstacle.DRAYNOR_TIGHTROPE_1, Obstacle.DRAYNOR_TIGHTROPE_2, Obstacle.DRAYNOR_NARROW_WALL, Obstacle.DRAYNOR_WALL, Obstacle.DRAYNOR_GAP, Obstacle.DRAYNOR_CRATE)),

	AL_KHARID_ROOFTOP(
			20,
			Arrays.asList(Obstacle.AL_KHARID_ROUGH_WALL,
					Obstacle.AL_KHARID_TIGHTROPE_1,
					Obstacle.AL_KHARID_CABLE,
					Obstacle.AL_KHARID_ZIP_LINE,
					Obstacle.AL_KHARID_TROPICAL_TREE,
					Obstacle.AL_KHARID_ROOF_TOP_BEAMS,
					Obstacle.AL_KHARID_TIGHTROPE_2,
					Obstacle.AL_KHARID_GAP)),

	VARROCK_ROOFTOP(
			30,
			Arrays.asList(Obstacle.VARROCK_ROUGH_WALL,
					Obstacle.VARROCK_CLOTHES_LINE,
					Obstacle.VARROCK_GAP_1,
					Obstacle.VARROCK_WALL,
					Obstacle.VARROCK_GAP_2,
					Obstacle.VARROCK_GAP_3,
					Obstacle.VARROCK_GAP_4,
					Obstacle.VARROCK_LEDGE,
					Obstacle.VARROCK_EDGE)),

	FALADOR_ROOFTOP(
			50,
			Arrays.asList(Obstacle.FALADOR_WALL,
					Obstacle.FALADOR_TIGHTROPE_1,
					Obstacle.FALADOR_HAND_HOLDS,
					Obstacle.FALADOR_GAP_1,
					Obstacle.FALADOR_GAP_2,
					Obstacle.FALADOR_TIGHTROPE_2,
					Obstacle.FALADOR_TIGHTROPE_3,
					Obstacle.FALADOR_GAP_3,
					Obstacle.FALADOR_LEDGE_1,
					Obstacle.FALADOR_LEDGE_2,
					Obstacle.FALADOR_LEDGE_3,
					Obstacle.FALADOR_LEDGE_4,
					Obstacle.FALADOR_EDGE)),

	CANIFIS(50, Arrays.asList(Obstacle.CANIFIS_TREE, Obstacle.CANIFIS_GAP_1, Obstacle.CANIFIS_GAP_2, Obstacle.CANIFIS_GAP_3, Obstacle.CANIFIS_GAP_4, Obstacle.CANIFIS_POLE_VAULT, Obstacle.CANIFIS_GAP_5, Obstacle.CANIFIS_GAP_6)),

	SEERS(60, Arrays.asList(Obstacle.SEERS_WALL, Obstacle.SEERS_GAP_1, Obstacle.SEERS_TIGHTROPE, Obstacle.SEERS_GAP_2, Obstacle.SEERS_GAP_3, Obstacle.SEERS_EDGE));

	private final int level;
	private final List<Obstacle> obstacles;

	RooftopCourse(int level, List<Obstacle> Obstacles) {
		this.level = level;
		this.obstacles = Obstacles;
	}

	public String getName() {
		return Strings.toProperCase(this.name()) + " Rooftop";
	}

	public int getLevelRequired() {
		return this.level;
	}

	public List<Obstacle> getObstacles() {
		return this.obstacles;
	}

	public Obstacle getCurrentObstacle() {
		Comparator<Obstacle> closestObstacle = new Comparator<Obstacle>() {
			@Override
			public int compare(Obstacle a, Obstacle b) {
				int distanceA = Player.getPosition()
						.distanceTo(a.getStartTile());
				int distanceB = Player.getPosition()
						.distanceTo(b.getStartTile());
				return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
			}
		};
		return this.obstacles.stream()
				.filter(obstacle -> obstacle.getStartTile()
						.getPlane() == Game.getPlane())
				.sorted(closestObstacle)
				.filter(obstacle -> PathFinding.canReach(obstacle.getStartTile(), false))
				.findFirst()
				.orElse(null);
	}

	public Obstacle getLastObstacle() {
		return this.obstacles.get(this.obstacles.size() - 1);
	}
}
