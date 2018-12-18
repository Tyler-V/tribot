package scripts.usa.api2007.entity.selector;

import java.util.Comparator;

import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Game;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api.antiban.ABC;

/**
 * @author Laniax
 */
public abstract class PositionableFinder<T extends Positionable & Clickable07, S> extends Finder<T, S> {

	private boolean selectABC;

	/**
	 * Generates a filter which of entities that are inside an area
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public S maxDistance(int distance) {
		PositionableFinder<T, S> self = this;

		filters.add(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return Player.getPosition()
						.distanceTo(t) <= distance;
			}
		});

		return (S) this;
	}

	/**
	 * Generates a filter which of entities that are inside an area
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public S isInArea(RSArea area) {
		if (area != null) {
			PositionableFinder<T, S> self = this;

			filters.add(new Filter<T>() {
				@Override
				public boolean accept(T t) {
					return self.isInArea(area, t);
				}
			});
		}

		return (S) this;
	}

	private boolean isInArea(RSArea area, Positionable positionable) {
		return area.contains(positionable);
	}

	/**
	 * Generates a filter which will only return entities who are inside buildings.
	 * ONLY WORKS IF THE ENTITY IS INSIDE THE CURRENTLY LOADED REGION
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public S isIndoors() {
		PositionableFinder<T, S> self = this;

		filters.add(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return self.isInsideBuilding(t);
			}
		});

		return (S) this;
	}

	/**
	 * Generates a filter which will only return entities who are outside buildings.
	 * ONLY WORKS IF THE ENTITY IS INSIDE THE CURRENTLY LOADED REGION
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public S isOutdoors() {
		PositionableFinder<T, S> self = this;

		filters.add(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return !self.isInsideBuilding(t);
			}
		});

		return (S) this;
	}

	private boolean isInsideBuilding(Positionable positionable) {
		RSTile tile = positionable.getPosition();

		if (tile == null)
			return false;

		tile = tile.toLocalTile();

		int x = tile.getX();
		int y = tile.getY();

		if (x > 0 && x < 104 && y > 0 && y < 104)
			return Game.getSceneFlags()[Game.getPlane()][x][y] >= 4;

		return false;
	}

	@SuppressWarnings("unchecked")
	public S sortByDistance() {
		comparators.add(new Comparator<T>() {
			@Override
			public int compare(Positionable a, Positionable b) {
				int distanceA = Player.getPosition()
						.distanceTo(a);
				int distanceB = Player.getPosition()
						.distanceTo(b);
				return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
			}
		});
		return (S) this;
	}

	public S canReach(boolean isObject) {
		PositionableFinder<T, S> self = this;

		filters.add(new Filter<T>() {
			@Override
			public boolean accept(T t) {
				return PathFinding.canReach(t, isObject);
			}
		});

		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S closestTo(RSTile tile) {
		comparators.add(new Comparator<T>() {
			@Override
			public int compare(Positionable a, Positionable b) {
				int distanceA = tile.distanceTo(a);
				int distanceB = tile.distanceTo(b);
				return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
			}
		});
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S selectABC() {
		sortByDistance();
		this.selectABC = true;
		return (S) this;
	}

	protected boolean isSelectingABCTarget() {
		return this.selectABC;
	}

	protected Positionable selectABCTarget(Positionable[] entities) {
		return ABC.selectTarget(entities);
	}

	public static PositionableFinder<?, ?> getClosest(PositionableFinder<?, ?>... finders) {
		PositionableFinder<?, ?> closest = null;
		int distance = Integer.MAX_VALUE;
		for (PositionableFinder<?, ?> finder : finders) {
			Positionable result = finder.getFirstResult();
			if (result == null)
				continue;
			if (Player.getPosition()
					.distanceTo((Positionable) result) < distance) {
				closest = finder;
				distance = Player.getPosition()
						.distanceTo((Positionable) result);
			}
		}
		return closest;
	}
}
