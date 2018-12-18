package scripts.usa.api2007.servant;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSNPC;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api2007.House;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.Keyboard;
import scripts.usa.api2007.NPCChat;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;

public class Servant {

	public static NpcEntity getServantEntity() {
		return Entities.find(NpcEntity::new)
				.nameEquals(Servants.getNames());
	}

	public static RSNPC getServant() {
		return getServantEntity().getFirstResult();
	}

	private static boolean hasServantArrived() {
		RSNPC npc = Servant.getServant();
		return npc != null && Player.getPosition()
				.distanceTo(npc) <= 1;
	}

	private static boolean servantIsMoving() {
		RSNPC npc = getServant();
		return npc != null && npc.isMoving();
	}

	public static boolean callServant() {
		if (House.openHouseOptions()) {
			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(House.HOUSE_OPTIONS_MASTER)
					.actionEquals("Call Servant")
					.getFirstResult();
			if (inter == null)
				return false;

			if (inter.click()) {
				Result result = ResultCondition.wait(() -> {
					if (hasServantArrived())
						return Status.SUCCESS;
					if (servantIsMoving())
						return Status.RESET;
					return Status.CONTINUE;
				});
				if (result == Result.SUCCESS)
					return true;
			}
		}

		return hasServantArrived();
	}

	public static boolean talkTo() {
		if (isTalkingTo())
			return true;

		RSNPC npc = getServant();
		if (npc == null || npc != null && Player.getPosition()
				.distanceTo(npc) > 1) {
			callServant();
			Condition.wait(General.randomSD(1000, 500), () -> isTalkingTo());
		}

		if (isTalkingTo())
			return true;

		return Entity.interact("Talk-to", getServantEntity(), () -> isTalkingTo());
	}

	public static boolean isTalkingTo() {
		return NPCChat.isUp() && isInteractingWithMe();
	}

	public static boolean isInteractingWithMe() {
		RSNPC npc = getServant();
		if (npc == null)
			return false;
		return npc.isInteractingWithMe();
	}

	public static boolean isOutOfMaterials() {
		String message = NPCChat.getMessage();
		if (message == null)
			return false;

		String[] messages = { "You do not have" };
		return Arrays.stream(messages)
				.anyMatch(message::contains);
	}

	public static boolean request(int amount, ServantMaterials material) {
		if (!isTalkingTo())
			talkTo();

		if (!isTalkingTo())
			return false;

		RSNPC npc = getServant();
		if (npc == null)
			return false;

		Servants servant = Servants.getServant(npc);
		if (servant == null)
			return false;

		NPCChat.selectContinue();

		if (Interfaces.isEnterAmountUp()) {
			Keyboard.typeSend(amount);
			return Condition.wait(() -> getServant() == null);
		}
		else if (isLastTaskUp()) {
			if (isLastTaskValid(amount, material)) {
				if (NPCChat.selectOption("Fetch from bank"))
					return Condition.wait(() -> getServant() == null);
			}
			else {
				NPCChat.selectOption("Something else...");
			}
		}
		else {
			NPCChat.selectOption("coins");
			NPCChat.selectOption("Go to the bank...");
			NPCChat.selectOption("Bring something from the bank");
			if (!NPCChat.selectOption(material.getName())) {
				NPCChat.selectOption("More...");
			}
		}

		return false;
	}

	public static boolean request(ServantMaterials material) {
		Servants servant = Servants.getServant(Entities.find(NpcEntity::new)
				.nameEquals(Servants.getNames())
				.getFirstResult());
		return request(servant != null ? servant.getCapacity() : 26, material);
	}

	private static boolean isLastTaskUp() {
		return NPCChat.isOptionValid("Fetch from bank");
	}

	private static boolean isLastTaskValid(int amount, ServantMaterials material) {
		String option = NPCChat.getOption("Fetch from bank.*");
		if (option == null)
			return false;
		String regex = "Fetch from bank: (\\d+) x (.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(option);
		if (!m.find())
			return false;
		return Integer.parseInt(m.group(1)) == amount && m.group(2)
				.equals(material.getName());
	}
}
