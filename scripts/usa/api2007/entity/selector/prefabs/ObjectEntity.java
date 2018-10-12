package scripts.usa.api2007.entity.selector.prefabs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import scripts.usa.api2007.entity.selector.PositionableFinder;

/**
 * @author Laniax
 */
public class ObjectEntity extends PositionableFinder<RSObject, ObjectEntity> {

	private int distance = 20;

	/**
	 * Set the max search distance (in tiles). Default is 20.
	 *
	 * @param distance
	 * @return
	 */
	public ObjectEntity setDistance(int distance) {
		this.distance = distance;
		return this;
	}

	/**
	 * {@link Filters.Objects#idEquals}
	 */
	public ObjectEntity idEquals(int... id) {
		filters.add(Filters.Objects.idEquals(id));
		return this;
	}

	/**
	 * {@link Filters.Objects#idNotEquals}
	 */
	public ObjectEntity idNotEquals(int... id) {
		filters.add(Filters.Objects.idNotEquals(id));
		return this;
	}

	/**
	 * {@link Filters.Objects#inArea}
	 */
	public ObjectEntity inArea(RSArea area) {
		filters.add(Filters.Objects.inArea(area));
		return this;
	}

	/**
	 * {@link Filters.Objects#notInArea}
	 */
	public ObjectEntity notInArea(RSArea area) {
		filters.add(Filters.Objects.notInArea(area));
		return this;
	}

	/**
	 * {@link Filters.Objects#tileEquals}
	 */
	public ObjectEntity tileEquals(Positionable... positionable) {

		Filter<RSObject> filter = null;

		for (Positionable pos : positionable) {

			if (filter == null)
				filter = Filters.Objects.tileEquals(pos);
			else
				filter.or(Filters.Objects.tileEquals(pos), false);
		}

		if (filter != null)
			filters.add(filter);

		return this;
	}

	/**
	 * {@link Filters.Objects#tileNotEquals}
	 */
	public ObjectEntity tileNotEquals(Positionable... positionable) {

		Filter<RSObject> filter = null;

		for (Positionable pos : positionable) {

			if (filter == null)
				filter = Filters.Objects.tileNotEquals(pos);
			else
				filter.or(Filters.Objects.tileNotEquals(pos), false);
		}

		if (filter != null)
			filters.add(filter);

		return this;
	}

	/**
	 * {@link Filters.Objects#actionsEquals}
	 */
	public ObjectEntity actionsEquals(String... actions) {
		filters.add(Filters.Objects.actionsEquals(actions));
		return this;
	}

	/**
	 * {@link Filters.Objects#actionsNotEquals}
	 */
	public ObjectEntity actionsNotEquals(String... actions) {
		filters.add(Filters.Objects.actionsNotEquals(actions));
		return this;
	}

	/**
	 * {@link Filters.Objects#actionsContains}
	 */
	public ObjectEntity actionsContains(String... actions) {
		filters.add(Filters.Objects.actionsContains(actions));
		return this;
	}

	/**
	 * {@link Filters.Objects#actionsNotContains}
	 */
	public ObjectEntity actionsNotContains(String... actions) {
		filters.add(Filters.Objects.actionsNotContains(actions));
		return this;
	}

	/**
	 * {@link Filters.Objects#nameEquals}
	 */
	public ObjectEntity nameEquals(String... names) {
		filters.add(Filters.Objects.nameEquals(names));
		return this;
	}

	/**
	 * {@link Filters.Objects#nameNotEquals}
	 */
	public ObjectEntity nameNotEquals(String... names) {
		filters.add(Filters.Objects.nameNotEquals(names));
		return this;
	}

	/**
	 * {@link Filters.Objects#nameContains}
	 */
	public ObjectEntity nameContains(String... names) {
		filters.add(Filters.Objects.nameContains(names));
		return this;
	}

	/**
	 * {@link Filters.Objects#nameNotContains}
	 */
	public ObjectEntity nameNotContains(String... names) {
		filters.add(Filters.Objects.nameNotContains(names));
		return this;
	}

	/**
	 * {@link Filters.Objects#modelIndexCount}
	 */
	public ObjectEntity modelIndexCount(int... counts) {
		filters.add(Filters.Objects.modelIndexCount(counts));
		return this;
	}

	/**
	 * {@link Filters.Objects#modelVertexCount}
	 */
	public ObjectEntity modelVertexCount(int... counts) {
		filters.add(Filters.Objects.modelVertexCount(counts));
		return this;
	}

	/**
	 * Generates a filter that filters objects on the given type.
	 * 
	 * @return
	 */
	public ObjectEntity typeEquals(RSObject.TYPES type) {
		filters.add(new Filter<RSObject>() {
			@Override
			public boolean accept(RSObject rsObject) {
				return rsObject.getType() == type;
			}
		});

		return this;
	}

	/**
	 * Generates a filter that filters objects on the given type.
	 * 
	 * @return
	 */
	public ObjectEntity typeNotEquals(RSObject.TYPES type) {
		filters.add(new Filter<RSObject>() {
			@Override
			public boolean accept(RSObject rsObject) {
				return rsObject.getType() == type;
			}
		});

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public RSObject[] getResults() {
		Filter<RSObject> filter = super.buildFilter();

		RSObject[] objects = Objects.getAll(this.distance, filter);
		if (objects.length == 0)
			return objects;

		if (super.shouldSort()) {
			Comparator<RSObject> comparator = super.buildComparator();
			objects = Arrays.stream(objects).sorted(comparator).toArray(RSObject[]::new);
		}

		return objects;
	}
}
