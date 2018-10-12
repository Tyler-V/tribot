package scripts.usa.api2007.teleporting.constants;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.teleporting.interfaces.TeleportLimit;

public class TeleportConstants {

	public static final TeleportLimit LEVEL_20_WILDERNESS_LIMIT = () -> !Wilderness.isIn() || Wilderness.isIn() && Wilderness.getLevel() <= 20;

	public static final TeleportLimit LEVEL_30_WILDERNESS_LIMIT = () -> !Wilderness.isIn() || Wilderness.isIn() && Wilderness.getLevel() <= 30;

	public static final Filter<RSItem> GLORY_FILTER = Filters.Items.nameContains("Glory")
			.combine(Filters.Items.nameContains("("), true)
			.combine(notNotedFilter(), false);

	public static final Filter<RSItem> GAMES_FILTER = Filters.Items.nameContains("Games")
			.combine(Filters.Items.nameContains("("), true)
			.combine(notNotedFilter(), false);

	public static final Filter<RSItem> DUELING_FILTER = Filters.Items.nameContains("dueling")
			.combine(Filters.Items.nameContains("("), true)
			.combine(notNotedFilter(), false);

	public static final Filter<RSItem> COMBAT_FILTER = Filters.Items.nameContains("Combat b")
			.combine(Filters.Items.nameContains("("), true)
			.combine(notNotedFilter(), false);

	public static final Filter<RSItem> SKILLS_FILTER = Filters.Items.nameContains("Skills necklace")
			.combine(Filters.Items.nameContains("("), true)
			.combine(notNotedFilter(), false);

	public static final Filter<RSItem> WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth")
			.combine(Filters.Items.nameContains("("), true)
			.combine(notNotedFilter(), false);

	public static final Filter<RSItem> BURNING_FILTER = Filters.Items.nameContains("Burning amulet").combine(Filters.Items.nameContains("("), true);

	private static Filter<RSItem> notNotedFilter() {
		return new Filter<RSItem>() {
			@Override
			public boolean accept(RSItem item) {
				return item.getDefinition() != null && !item.getDefinition().isNoted();
			}
		};
	}
}
