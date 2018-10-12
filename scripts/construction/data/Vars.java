package scripts.construction.data;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.framework.ScriptVars;
import scripts.usa.framework.task.TaskScript;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public Furniture furniture;

	public boolean hasMaterials() {
		for (Material material : furniture.getMaterials()) {
			RSItem[] items = Entities.find(ItemEntity::new).nameEquals(material.getName()).isNoted(false).getResults();
			if (items.length < material.getRequiredAmount())
				return false;
		}
		return true;
	}

	public boolean hasTools() {
		return Inventory.getCount("Hammer") > 0 && Inventory.getCount("Saw") > 0;
	}

	public int getNotedMaterial() {
		for (Material material : furniture.getMaterials()) {
			RSItem item = Entities.find(ItemEntity::new).nameEquals(material.getName()).isNoted(true).getFirstResult();
			if (item != null)
				return item.getID();
		}
		return -1;
	}
}
