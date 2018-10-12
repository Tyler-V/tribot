package scripts.crafter.data;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api2007.types.RSInterface;

import scripts.crafter.data.Crafting.Products;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public String material;
	public Products product;
	public Locations location;
	public boolean autoProgression;

	public int profit;
	public int items;
	public int count;

	public static boolean hasTools() {
		for (Material tool : Vars.get().product.getTools()) {
			if (Inventory.getCount(tool.getName()) < tool.getRequiredAmount())
				return false;
		}
		return true;
	}

	public static boolean hasMaterials() {
		for (Material material : Vars.get().product.getMaterials()) {
			if (Inventory.getCount(material.getName()) < material.getRequiredAmount())
				return false;
		}
		return true;
	}

	public static RSInterface getInterface() {
		return Entities.find(InterfaceEntity::new)
				.inMaster(Vars.get().product.getMasterIndex())
				.componentNameContains(Vars.get().product.getComponentText())
				.hasActions()
				.getFirstResult();
	}

	public static boolean isInterfaceUp() {
		return getInterface() != null;
	}

	public static String[] getSupplies() {
		List<String> supplies = new ArrayList<String>();
		Vars.get().product.getTools().stream().forEach(t -> supplies.add(t.getName()));
		Vars.get().product.getMaterials().stream().forEach(m -> supplies.add(m.getName()));
		return supplies.toArray(new String[supplies.size()]);
	}

	public static String[] getActionItems() {
		if (Vars.get().product.getTools().size() > 0) {
			if (Vars.get().product.getTools().size() == 1) {
				return new String[] { Vars.get().product.getTools().get(0).getName() };
			}
			return new String[] { Vars.get().product.getTools().stream().filter(t -> t.isActionItem()).findFirst().get().getName() };
		}
		return getMaterials();
	}

	public static String[] getMaterials() {
		List<String> materials = new ArrayList<String>();
		Vars.get().product.getMaterials().stream().forEach(m -> {
			materials.add(m.getName());
		});
		return materials.toArray(new String[materials.size()]);
	}
}
