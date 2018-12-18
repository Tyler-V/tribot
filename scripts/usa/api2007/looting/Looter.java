package scripts.usa.api2007.looting;

import java.util.HashMap;
import java.util.Map;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.GroundItemEntity;
import scripts.usa.api2007.looting.LootingBag.BagOption;

public class Looter {

	private final RSArea area;
	private final int maxDistance;
	private final int minValue;
	private final int maxValue;

	private Map<Object, LootItem> items;

	public Looter(int minValue, int maxValue, RSArea area, int maxDistance) {
		this.area = area;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.maxDistance = maxDistance;
		this.items = new HashMap<Object, LootItem>();
		this.add("Looting bag");
	}

	public Looter(int minValue, RSArea area, int maxDistance) {
		this(minValue, Integer.MAX_VALUE, area, maxDistance);
	}

	public Looter(int minValue, int maxValue, int maxDistance) {
		this(minValue, maxValue, null, maxDistance);
	}

	public Looter(int minValue) {
		this(minValue, null, Integer.MAX_VALUE);
	}

	public int getMinimumValue() {
		return this.minValue;
	}

	public int getMaximumValue() {
		return this.maxValue;
	}

	public Map<Object, LootItem> getItems() {
		return this.items;
	}

	public void add(String name, int minimumStack, Condition condition) {
		items.put(name, new LootItem(name, minimumStack, condition));
	}

	public void add(String name, Condition condition) {
		add(name, 1, condition);
	}

	public void add(String name) {
		add(name, 1, () -> true);
	}

	public void add(int id, int minimumStack, Condition condition) {
		items.put(id, new LootItem(id, minimumStack, condition));
	}

	public void add(int id, Condition condition) {
		add(id, 1, condition);
	}

	public void add(int id) {
		add(id, 1, () -> true);
	}

	private GroundItemEntity getGroundItemEntity() {
		return Entities.find(GroundItemEntity::new)
				.isInArea(this.area)
				.maxDistance(this.maxDistance)
				.isValid(this)
				.sortByDistance();
	}

	public RSGroundItem[] getLoot() {
		return getGroundItemEntity().getResults();
	}

	public boolean hasFoundLoot() {
		return getLoot().length > 0;
	}

	public boolean loot() {
		if (Inventory.isFull()) {
			if (Consumables.hasFood()) {
				ScriptVars.get().status = "Eating to make room";
				Consumables.eat();
			}
			if (Inventory.isFull())
				return false;
		}

		Result result = Entity.interact("Take", getGroundItemEntity(), () -> {
			if (Conditions.isPlayerMoving()
					.isTrue())
				return Status.RESET;
			return Status.CONTINUE;
		});

		return result == Result.SUCCESS;
	}
}
