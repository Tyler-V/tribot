package scripts.usa.api2007.observers.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.threads.VolatileRunnable;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class InventoryObserver extends VolatileRunnable implements InventoryListener {

	private Condition ignoreCondition;
	private Map<Integer, InventoryItem> inventoryMap;
	private ArrayList<InventoryListener> listeners;

	public InventoryObserver(InventoryListener listener, Condition ignoreCondition) {
		super();
		this.listeners = new ArrayList<InventoryListener>();
		this.addListener(listener);
		this.ignoreCondition = ignoreCondition;
	}

	public InventoryObserver(InventoryListener listener) {
		this(listener, () -> Banking.isBankScreenOpen());
	}

	@Override
	public void execute() {
		if (Login.getLoginState() != Login.STATE.INGAME)
			return;

		if (ignoreCondition.isTrue()) {
			inventoryMap = null;
			return;
		}

		if (inventoryMap == null) {
			inventoryMap = getInventoryMap();
			if (inventoryMap == null)
				return;
		}

		Map<Integer, InventoryItem> currentInventoryMap = getInventoryMap();
		Set<Integer> keySet = Stream.of(inventoryMap, currentInventoryMap).flatMap(map -> map.keySet().stream()).collect(Collectors.toSet());
		keySet.forEach(id -> {
			int count = (currentInventoryMap.containsKey(id) ? currentInventoryMap.get(id).getCount() : 0) -
					(inventoryMap.containsKey(id) ? inventoryMap.get(id).getCount() : 0);

			if (inventoryMap.containsKey(id)) {
				inventoryMap.get(id).setCount();
			}
			else {
				inventoryMap.put(id, new InventoryItem(id));
			}

			if (count != 0)
				onInventoryChange(count > 0 ? InventoryChange.INCREASE : InventoryChange.DECREASE, inventoryMap.get(id).getItem(), Math.abs(count));
		});
	}

	public Map<Integer, InventoryItem> getInventoryMap() {
		return Arrays.asList(Inventory.getAll())
				.stream()
				.collect(Collectors.toMap(item -> item.getID(), item -> new InventoryItem(item.getID()), (item1, item2) -> item1));
	}

	public class InventoryItem {
		private final int id;
		private final RSItem item;
		private int count;

		InventoryItem(int id) {
			this.id = id;
			this.item = Inventory.find(id)[0];
			this.count = Inventory.getCount(id);
		}

		public RSItem getItem() {
			return this.item;
		}

		public int getCount() {
			return this.count;
		}

		public void setCount() {
			this.count = Inventory.getCount(id);
		}
	}

	public void addListener(InventoryListener listener) {
		if (listener == null)
			return;
		this.listeners.add(listener);
	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		System.out.println(RSItemUtils.getName(item) + " (" + item.getID() + ") " + change.toString().toLowerCase() + "d by " + count);

		this.listeners.forEach(l -> {
			l.onInventoryChange(change, item, count);
		});
	}
}
