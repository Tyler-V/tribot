package scripts.usa.api2007;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.util.Strings;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class House {

	public static final int HOUSE_OPTIONS_MASTER = 370;
	private static final int HOUSE_OPTION_SELECTED_TEXTURE_ID = 699;

	private static final int HOUSE_LOADING_MASTER = 71;

	private static final int TELEPORT_INSIDE_SETTING_CHILD = 1047;

	private static final int ENTER_FRIEND_NAME_MASTER = 162;
	private static final int ENTER_FRIEND_NAME_CHILD = 33;
	private static final int ENTER_FRIEND_NAME_COMPONENT = 0;

	public enum HouseMode {
		HOME,
		BUILD,
		FRIENDS
	}

	public static boolean enter(HouseMode houseMode) {
		return enter(houseMode, null);
	}

	private static ObjectEntity getPortalEntity() {
		return Entities.find(ObjectEntity::new).nameEquals("Portal");
	}

	public static boolean enter(HouseMode houseMode, String friendName) {
		if (isInside())
			return true;

		switch (houseMode) {
			case HOME:
				return Entity.interact("Home", getPortalEntity(), () -> House.isInside());
			case BUILD:
				return Entity.interact("Build mode", getPortalEntity(), () -> House.isInside());
			case FRIENDS:
				if (Entity.interact("Friend's house", getPortalEntity(), () -> enterNameUp())) {
					Keyboard.typeSend(getFriendName().equalsIgnoreCase(friendName) ? "" : friendName);
					return Condition.wait(() -> House.isInside());
				}
				break;
		}

		return isInside();
	}

	private static boolean enterNameUp() {
		RSInterfaceChild child = Interfaces.get(ENTER_FRIEND_NAME_MASTER, ENTER_FRIEND_NAME_CHILD);
		if (child == null || child.isHidden())
			return false;

		RSInterfaceComponent component = child.getChild(ENTER_FRIEND_NAME_COMPONENT);
		return component != null && !component.isHidden();
	}

	private static String getFriendName() {
		RSInterfaceChild child = Interfaces.get(ENTER_FRIEND_NAME_MASTER, ENTER_FRIEND_NAME_CHILD);
		if (child == null || child.isHidden())
			return null;

		RSInterfaceComponent component = child.getChild(ENTER_FRIEND_NAME_COMPONENT);
		if (component == null || component.isHidden())
			return null;

		String text = component.getText();
		if (text == null || text.length() == 0)
			return null;

		String regex = "<col=000000>Last name:<\\/col>\\s(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		return m.find() ? m.group(1) : null;
	}

	public static boolean exit() {
		if (isOutside())
			return true;
		return Entity.interact("Enter", Entities.find(ObjectEntity::new).nameEquals("Portal"), () -> isOutside());
	}

	/**
	 * Checks to see if the house setting for teleporting inside is on.
	 *
	 * @return True if the player will teleport inside the house, false if the
	 *         player will teleport outside the house.
	 */
	public static boolean isTeleportingInside() {
		return Settings.get(TELEPORT_INSIDE_SETTING_CHILD) >> 23 == 0;
	}

	/**
	 * Gets the amount of tasks that your servant will do before asking for
	 * payment.
	 *
	 * @return The amount of tasks.
	 */
	public static int getServantTasks() {
		return (Settings.get(Settings.Indexes.POH.SERVANT_TASK_INDEX) >> 21) & 0xF;
	}

	/**
	 * Checks to see whether or not you are in building mode.
	 *
	 * @return True if you are in building mode, false otherwise.
	 */
	public static boolean isInBuildingMode() {
		return House.isInside() && Game.getSetting(Settings.Indexes.POH.BUILDING_MODE_INDEX) >> 10 == 1;
	}

	/**
	 * Checks to see whether or not you are in your house.
	 *
	 * @return True if you are in your POH, false otherwise.
	 */
	public static boolean isInside() {
		return !isLoading() && Entities.find(ObjectEntity::new).nameEquals("Portal").actionsEquals("Lock").getFirstResult() != null;
	}

	/**
	 * Checks to see whether or not you are outside your house.
	 *
	 * @return True if you are outside your house, false otherwise.
	 */
	public static boolean isOutside() {
		return !isLoading() && Entities.find(ObjectEntity::new).nameEquals("Portal").actionsEquals("Home").getFirstResult() != null;
	}

	/**
	 * Checks to see whether or not your POH is being loaded.
	 *
	 * @return True if it is being loaded, false otherwise.
	 */
	public static boolean isLoading() {
		return Interfaces.isInterfaceValid(HOUSE_LOADING_MASTER);
	}

	/**
	 * Checks to see if the house options interface is open.
	 *
	 * @return True if the house options interface is open, false otherwise.
	 */
	public static boolean isHouseOptionsOpen() {
		return Interfaces.isInterfaceValid(370);
	}

	public static boolean openHouseOptions() {
		if (isHouseOptionsOpen())
			return true;

		Options.open();

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(Interfaces.OPTIONS_MASTER)
				.actionEquals("View House Options")
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click())
			return Condition.wait(() -> isHouseOptionsOpen());

		return isHouseOptionsOpen();
	}

	public static boolean setOption(HouseOption houseOption, HouseOptionOptions houseOptionOptions) {
		if (openHouseOptions()) {
			if (houseOption.isOptionSet(houseOptionOptions))
				return true;

			RSInterface inter = houseOption.getInterface(houseOptionOptions);
			if (inter == null)
				return false;

			if (inter.click())
				return Condition.wait(() -> houseOption.isOptionSet(houseOptionOptions));
		}

		return houseOption.isOptionSet(houseOptionOptions);
	}

	public enum HouseOptionOptions {
		ON,
		OFF;
	}

	public enum HouseOption {
		BUILDING_MODE,
		TELEPORT_INSIDE;

		public RSInterface getInterface(HouseOptionOptions houseOptionOptions) {
			if (!isHouseOptionsOpen())
				return null;

			RSInterface inter = Entities.find(InterfaceEntity::new)
					.inMaster(HOUSE_OPTIONS_MASTER)
					.textEquals(Strings.toProperCase(this.name()))
					.getFirstResult();
			if (inter == null)
				return null;
			int index = houseOptionOptions == HouseOptionOptions.ON ? inter.getIndex() + 1 : inter.getIndex() + 2;
			return Interfaces.get(HOUSE_OPTIONS_MASTER, index);
		}

		public boolean isOptionSet(HouseOptionOptions houseOptionOptions) {
			if (!isHouseOptionsOpen())
				return false;

			RSInterface inter = getInterface(houseOptionOptions);
			if (inter == null)
				return false;

			return inter.getTextureID() == HOUSE_OPTION_SELECTED_TEXTURE_ID;
		}
	}

	public enum HouseDoorOptions {
		CLOSED,
		OPEN,
		DO_NOT_RENDER;

		// tbd
	}
}
