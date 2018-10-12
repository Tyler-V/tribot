package scripts.usa.api2007.enums;

import java.util.function.Predicate;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.timer.Timer;
import scripts.usa.api.util.Strings;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;

public enum Potions {

	COMBAT_POTION(
			Filters.Items.nameContains("Combat potion").and(Filters.Items.nameContains("(")),
			() -> Skills.getCurrentLevel(SKILLS.STRENGTH) != Skills.getActualLevel(SKILLS.STRENGTH)),

	SUPER_ATTACK(
			Filters.Items.nameContains("Super attack").and(Filters.Items.nameContains("(")),
			() -> Skills.getCurrentLevel(SKILLS.STRENGTH) != Skills.getActualLevel(SKILLS.STRENGTH)),

	SUPER_STRENGTH(
			Filters.Items.nameContains("Super strength").and(Filters.Items.nameContains("(")),
			() -> Skills.getCurrentLevel(SKILLS.STRENGTH) != Skills.getActualLevel(SKILLS.STRENGTH)),

	ANTIFIRE(Filters.Items.nameContains("Antifire").and(Filters.Items.nameContains("(")), 360000),

	EXTENDED_ANTIFIRE(Filters.Items.nameContains("Extended antifire").and(Filters.Items.nameContains("(")), 720000);

	private final Predicate<RSItem> predicate;
	private Effect effect;
	private static Timer timer;
	private long time;

	Potions(Predicate<RSItem> predicate, Effect effect) {
		this.predicate = predicate;
		this.effect = effect;
	}

	Potions(Predicate<RSItem> predicate, long time) {
		this(predicate, () -> timer.isRunning());
		this.time = time;
	}

	public String getName() {
		return Strings.toSentenceCase(this.name());
	}

	public Predicate<RSItem> getPredicate() {
		return this.predicate;
	}

	public boolean isActive() {
		return this.effect.isActive();
	}

	private boolean isTimed() {
		return this.time > 0;
	}

	public boolean drink() {
		if (this.isActive())
			return true;

		RSItem item = Entities.find(ItemEntity::new).nameContains(this.getName()).getFirstResult();
		if (item == null)
			return false;

		Interfaces.closeAll();

		if (Inventory.open()) {
			ScriptVars.get().status = "Drinking " + this.getName();
			if (item.click()) {
				if (this.isTimed())
					timer = new Timer(this.time);
				return Condition.wait(() -> this.isActive());
			}
		}

		return this.isActive();
	}

	private interface Effect {
		public boolean isActive();
	}
}
