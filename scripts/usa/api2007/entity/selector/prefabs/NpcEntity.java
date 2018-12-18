package scripts.usa.api2007.entity.selector.prefabs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;

import scripts.usa.api.antiban.ABC;
import scripts.usa.api2007.entity.selector.PositionableFinder;

/**
 * @author Laniax
 */
public class NpcEntity extends PositionableFinder<RSNPC, NpcEntity> {

	private boolean sortByInteracting;

	/**
	 * {@link Filters.NPCs#idEquals}
	 */
	public NpcEntity idEquals(int... id) {
		filters.add(Filters.NPCs.idEquals(id));
		return this;
	}

	/**
	 * {@link Filters.NPCs#idNotEquals}
	 */
	public NpcEntity idNotEquals(int... id) {
		filters.add(Filters.NPCs.idNotEquals(id));
		return this;
	}

	/**
	 * {@link Filters.NPCs#actionsContains}
	 */
	public NpcEntity actionsContains(String... actions) {
		filters.add(Filters.NPCs.actionsContains(actions));
		return this;
	}

	/**
	 * {@link Filters.NPCs#actionsEquals}
	 */
	public NpcEntity actionsEquals(String... actions) {
		filters.add(Filters.NPCs.actionsEquals(actions));
		return this;
	}

	/**
	 * {@link Filters.NPCs#actionsNotContains}
	 */
	public NpcEntity actionsNotContains(String... actions) {
		filters.add(Filters.NPCs.actionsNotContains(actions));
		return this;
	}

	/**
	 * {@link Filters.NPCs#actionsNotEquals}
	 */
	public NpcEntity actionsNotEquals(String... actions) {
		filters.add(Filters.NPCs.actionsNotEquals(actions));
		return this;
	}

	/**
	 * {@link Filters.NPCs#inArea}
	 */
	public NpcEntity inArea(RSArea area) {
		filters.add(Filters.NPCs.inArea(area));
		return this;
	}

	/**
	 * {@link Filters.NPCs#notInArea}
	 */
	public NpcEntity notInArea(RSArea area) {
		filters.add(Filters.NPCs.notInArea(area));
		return this;
	}

	/**
	 * {@link Filters.NPCs#modelIndexCount}
	 */
	public NpcEntity modelIndexCount(int... counts) {
		filters.add(Filters.NPCs.modelIndexCount(counts));
		return this;
	}

	/**
	 * {@link Filters.NPCs#modelVertexCount}
	 */
	public NpcEntity modelVertexCount(int... counts) {
		filters.add(Filters.NPCs.modelVertexCount(counts));
		return this;
	}

	/**
	 * {@link Filters.NPCs#nameContains}
	 */
	public NpcEntity nameContains(String... names) {
		filters.add(Filters.NPCs.nameContains(names));
		return this;
	}

	/**
	 * {@link Filters.NPCs#nameNotContains}
	 */
	public NpcEntity nameNotContains(String... names) {
		filters.add(Filters.NPCs.nameNotContains(names));
		return this;
	}

	/**
	 * {@link Filters.NPCs#nameEquals}
	 */
	public NpcEntity nameEquals(String... names) {
		filters.add(Filters.NPCs.nameEquals(names));
		return this;
	}

	/**
	 * {@link Filters.NPCs#nameNotEquals}
	 */
	public NpcEntity nameNotEquals(String... names) {
		filters.add(Filters.NPCs.nameNotEquals(names));
		return this;
	}

	/**
	 * {@link Filters.NPCs#tileEquals}
	 */
	public NpcEntity tileEquals(Positionable positionable) {
		filters.add(Filters.NPCs.tileEquals(positionable));
		return this;
	}

	/**
	 * {@link Filters.NPCs#tileNotEquals}
	 */
	public NpcEntity tileNotEquals(Positionable positionable) {
		filters.add(Filters.NPCs.tileNotEquals(positionable));
		return this;
	}

	/**
	 * {@link Filters.NPCs#interactingWithMe}
	 */
	public NpcEntity interactingWithMe() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.isInteractingWithMe();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#notInteractingWithMe}
	 */
	public NpcEntity notInteractingWithMe() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return !npc.isInteractingWithMe();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#inCombat}
	 */
	public NpcEntity inCombat() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.isInCombat();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#notInCombat}
	 */
	public NpcEntity notInCombat() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return !npc.isInCombat();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#onScreen}
	 */
	public NpcEntity onScreen() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.isOnScreen();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#notOnScreen}
	 */
	public NpcEntity notOnScreen() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return !npc.isOnScreen();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#isMoving}
	 */
	public NpcEntity isMoving() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.isMoving();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#notMoving}
	 */
	public NpcEntity notMoving() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return !npc.isMoving();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#isClickable}
	 */
	public NpcEntity isClickable() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.isClickable();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#notClickable}
	 */
	public NpcEntity notClickable() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return !npc.isClickable();
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#healthPercentAbove}
	 */
	public NpcEntity isHealthPercentAbove(double healthPercent) {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.getHealthPercent() >= healthPercent;
			}
		});
		return this;
	}

	/**
	 * {@link Filters.NPCs#isFullHealth}
	 */
	public NpcEntity isFullHealth() {
		filters.add(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				return npc.getHealthPercent() == 1.0;
			}
		});
		return this;
	}

	public NpcEntity sortByInteracting() {
		comparators.add(new Comparator<RSNPC>() {
			@Override
			public int compare(RSNPC a, RSNPC b) {
				boolean interactingA = a.isInteractingWithMe();
				boolean interactingB = b.isInteractingWithMe();
				if (interactingA && !interactingB) {
					return -1;
				}
				else if (!interactingA && interactingB) {
					return 1;
				}
				return Integer.compare((int) a.getHealthPercent(), (int) b.getHealthPercent());
			}
		});
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public RSNPC[] getResults() {
		Filter<RSNPC> filter = super.buildFilter();

		List<RSNPC> npcs = Arrays.asList(NPCs.getAll(filter));
		if (npcs.size() == 0)
			return new RSNPC[0];

		if (super.shouldSort()) {
			Comparator<RSNPC> comparator = super.buildComparator();
			npcs.sort(comparator);
		}

		return npcs.toArray(new RSNPC[npcs.size()]);
	}
}
