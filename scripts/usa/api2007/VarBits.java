package scripts.usa.api2007;

import org.tribot.api2007.types.RSVarBit;

public class VarBits {

	private static final int KEYBINDING_ESC_CLOSES_CURRENT_INTERFACE = 4681;

	public static boolean isEscKeybinded() {
		return RSVarBit.get(KEYBINDING_ESC_CLOSES_CURRENT_INTERFACE).getValue() == 1;
	}

}
