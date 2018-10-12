package scripts.crafter.tasks;

import java.util.Collections;

import scripts.crafter.data.Material;
import scripts.crafter.data.Tool;
import scripts.crafter.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Banking;

public class Bank implements PriorityTask {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean validate() {
		if (!Vars.hasTools() || !Vars.hasMaterials())
			return true;

		return false;
	}

	@Override
	public void execute() {
		if (Banking.open()) {
			if (Banking.depositExcept(Vars.getSupplies())) {
				if (!Vars.hasTools()) {
					Collections.shuffle(Vars.get().product.getTools());
					for (Tool tool : Vars.get().product.getTools())
						Banking.withdraw(tool.isStackable() ? Integer.MAX_VALUE : 1, tool.getName());
				}
				if (!Vars.hasMaterials()) {
					int tools = Vars.get().product.getTools().size();
					int materials = Vars.get().product.getMaterials().stream().mapToInt(m -> m.getRequiredAmount()).sum();
					int amount = (28 - tools) / materials;
					Collections.shuffle(Vars.get().product.getMaterials());
					for (Material material : Vars.get().product.getMaterials())
						Banking.withdraw(amount * material.getRequiredAmount(), material.getName());
				}
			}
		}
	}
}
