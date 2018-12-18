package scripts.usa.api2007.enums;

import java.util.function.Predicate;

import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.util.Strings;

public enum Jewelry {

	AMULET_OF_GLORY(Filters.Items.nameContains("Amulet of glory").and(Filters.Items.nameContains("("))),
	GAMES_NECKLACE(Filters.Items.nameContains("Games necklace").and(Filters.Items.nameContains("("))),
	RING_OF_DUELING(Filters.Items.nameContains("Ring of dueling").and(Filters.Items.nameContains("(")));

	private final Predicate<RSItem> predicate;

	Jewelry(Predicate<RSItem> predicate) {
		this.predicate = predicate;
	}

	public String getName() {
		return Strings.toSentenceCase(this.name());
	}

	public Predicate<RSItem> getPredicate() {
		return this.predicate;
	}
}
