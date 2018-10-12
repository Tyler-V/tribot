package scripts.usa.api2007.looting;

import java.util.HashMap;
import java.util.Map;

import org.tribot.api.General;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;

import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.Status;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.GroundItemEntity;
import scripts.usa.api2007.looting.LootingBag.BagOption;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class Looter {

	private final RSArea area;
	private final int minimum;
	private final int maximum;

	private Map<Object, LootItem> items;

	public Looter(RSArea area, int minimum, int maximum) {
		this.area = area;
		this.minimum = minimum;
		this.maximum = maximum;
		this.items = new HashMap<Object, LootItem>();
		this.add("Looting bag");
	}

	public Looter(RSArea area, int minimum) {
		this.area = area;
		this.minimum = minimum;
		this.maximum = Integer.MAX_VALUE;
		this.items = new HashMap<Object, LootItem>();
		this.add("Looting bag");
	}

	public Looter(int minimum, int maximum) {
		this(null, minimum, maximum);
	}

	public Looter(int minimum) {
		this(null, minimum);
	}

	public int getMinimumValue() {
		return this.minimum;
	}

	public int getMaximumValue() {
		return this.maximum;
	}

	public Map<Object, LootItem> getItems() {
		return this.items;
	}

	public void add(String name) {
		items.put(name, new LootItem(name, true));
	}

	public void add(int id) {
		items.put(id, new LootItem(id, true));
	}

	private GroundItemEntity getGroundItemEntity() {
		return Entities.find(GroundItemEntity::new).isInArea(area).isValid(this).sortByValue();
	}

	public RSGroundItem[] getLoot() {
		return getGroundItemEntity().getResults();
	}

	public boolean hasFoundLoot() {
		return getLoot().length > 0;
	}

	public boolean loot() {
		if (Inventory.isFull()) {
			Consumables.eat();
			if (Inventory.isFull())
				return false;
		}

		Result result = Entity.interact("Take", getGroundItemEntity(), () -> {
			if (Conditions.isPlayerMoving().isTrue())
				return Status.RESET;
			return Status.CONTINUE;
		});

		return result == Result.SUCCESS;
	}
}
