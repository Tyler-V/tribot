package scripts.usa.api2007;

import java.util.Arrays;
import java.util.Optional;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.types.RSInterface;

import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;

public class NPCChat extends org.tribot.api2007.NPCChat {
	
	public static boolean handleConversation(String regex) {
		if(!isUp())
			return false;
		
		while(isUp()) {
			selectContinue();
			selectOption(regex);
		}
		
		return true;
	}

	public static boolean isUp() {
		return hasOptions() || getMessage() != null || getName() != null || Interfaces.enterAmountUp();
	}

	public static boolean isOptionValid(String regex) {
		if (!hasOptions())
			return false;
		return Arrays.stream(getOptions()).anyMatch(o -> o.matches(regex) || o.contains(regex));
	}

	public static boolean hasOptions() {
		String[] options = getOptions();
		return options != null && options.length > 0;
	}

	public static boolean hasOptions(String... options) {
		if (!hasOptions())
			return false;
		return Arrays.equals(getOptions(), options);
	}

	public static String getOption(String regex) {
		if (!hasOptions())
			return null;
		String[] options = getOptions();
		Optional<String> result = Arrays.stream(options).filter(option -> option.matches(regex)).findFirst();
		return result.isPresent() ? result.get() : null;
	}

	public static boolean isContinueChatUp() {
		RSInterface inter = Entities.find(InterfaceEntity::new).textEquals("Click here to continue").getFirstResult();
		return inter != null && !inter.isHidden();
	}

	public static boolean selectContinue() {
		if (!isContinueChatUp())
			return false;
		Chat chat = getChat();
		Keyboard.typeSend(" ");
		return Condition.wait(() -> waitForChange(chat));
	}

	public static boolean selectOption(String regex) {
		if (!hasOptions())
			return false;
		String[] options = getOptions();
		Optional<String> option = Arrays.stream(options).filter(o -> o.matches(regex) || o.contains(regex)).findAny();
		if (!option.isPresent())
			return false;
		int index = Arrays.asList(options).indexOf(option.get()) + 1;
		Chat chat = getChat();
		Keyboard.typeSend("" + index);
		return Condition.wait(() -> waitForChange(chat));
	}

	public static Chat getChat() {
		return new Chat(getName(), getMessage(), getOptions());
	}

	public static boolean waitForChange(Chat previous) {
		try {
			Chat current = getChat();
			return Condition.wait(() -> !previous.equals(current));
		}
		finally {
			General.sleep(General.randomSD(500, 250));
		}
	}

	public static class Chat {
		private final String name;
		private final String message;
		private final String[] options;

		public Chat(String name, String message, String[] options) {
			this.name = name;
			this.message = message;
			this.options = options;
		}

		public String getName() {
			return this.name;
		}

		public String getMessage() {
			return this.message;
		}

		public String[] getOptions() {
			return this.options;
		}
	}
}
