package scripts.usa.api2007;

import java.util.Arrays;
import java.util.List;

import org.tribot.api2007.types.RSInterface;

import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;

public class Interfaces extends org.tribot.api2007.Interfaces {

	public static final int OPTIONS_MASTER = 261;
	private static final int ENTER_AMOUNT_MASTER_ID = 162;

	public static boolean isEnterAmountUp() {
		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(ENTER_AMOUNT_MASTER_ID)
				.textEquals("*")
				.getFirstResult();
		return Interfaces.isInterfaceSubstantiated(inter);
	}

	private static InterfaceEntity getClosableInterfaceEntity() {
		return Entities.find(InterfaceEntity::new)
				.isNotHidden()
				.actionEquals("Close");
	}

	public static boolean isClosableInterfaceOpen() {
		return getClosableInterfaceEntity().getFirstResult() != null;
	}

	public static void closeAll() {
		RSInterface inter = getClosableInterfaceEntity().getFirstResult();
		if (inter == null)
			return;

		if (inter.click())
			Condition.wait(() -> inter.isHidden());
	}

	public static String getActionFromAmount(RSInterface inter, int amount) {
		if (inter == null || inter.getActions() == null)
			return "All";

		List<String> actions = Arrays.asList(inter.getActions());

		int actionValue = actions.stream()
				.map(a -> {
					a = a.replaceAll("\\D+", "");
					return a.isEmpty() ? 0 : Integer.parseInt(a);
				})
				.filter(v -> v > 0 && v >= amount)
				.mapToInt(v -> v)
				.min()
				.orElse(0);

		String action = null;
		if (actionValue > 0)
			action = actions.stream()
					.filter(a -> {
						a = a.replaceAll("\\D+", "");
						return !a.isEmpty() && Integer.parseInt(a) == actionValue;
					})
					.findFirst()
					.orElse(null);

		if (action == null)
			action = actions.stream()
					.filter(a -> a.toLowerCase()
							.contains("all"))
					.findFirst()
					.orElse(null);

		// General.println(list);
		// General.println(actionValue);
		// General.println(action);
		return action != null ? action : "All";
	}

	public static class PollBooth {

		public static final int POLL_BOOTH_MASTER = 310;

		public static boolean isOpen() {
			return Interfaces.isInterfaceValid(POLL_BOOTH_MASTER);
		}

		public static boolean close() {
			if (!isOpen())
				return true;

			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(POLL_BOOTH_MASTER)
					.actionEquals("Close")
					.getFirstResult();
			if (inter == null)
				return false;

			if (inter.click())
				Condition.wait(() -> !isOpen());

			return !isOpen();
		}
	}
}
