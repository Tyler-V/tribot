package scripts.crafter.tasks;

import org.tribot.api2007.types.RSInterface;

import scripts.crafter.data.Vars;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.Keyboard;

public class SelectProduct implements Task {

	@Override
	public boolean validate() {
		return Vars.isInterfaceUp();
	}

	@Override
	public void execute() {
		selectOption();
	}

	private String getAction(RSInterface inter) {
		if (inter == null)
			return null;
		String[] actions = inter.getActions();
		if (actions == null || actions.length == 0)
			return null;
		if (actions.length == 1)
			return actions[0];
		return "All";
	}

	private boolean selectOption() {
		RSInterface inter = Vars.getInterface();
		if (inter == null)
			return false;

		Vars.get().count = Vars.get().items;

		if (Vars.get().product.useHotKey()) {
			Vars.get().status = "Typing " + Vars.get().product.getKey();
			Keyboard.typeSend(Vars.get().product.getKey());
		}
		else {
			Vars.get().status = "Selecting " + Vars.get().product.getComponentText();
			inter.click(getAction(inter));
		}

		Vars.get().status = "Crafting " + Vars.get().product.getName();
		ResultCondition.wait(() -> {
			if (!Vars.hasMaterials() || !Vars.hasTools() || Conditions.isContinueChatUp().isTrue())
				return Status.SUCCESS;
			if (Conditions.isPlayerActive().isTrue() || Vars.get().count != Vars.get().items) {
				Vars.get().count = Vars.get().items;
				return Status.RESET;
			}
			return Status.CONTINUE;
		});

		return true;
	}
}
