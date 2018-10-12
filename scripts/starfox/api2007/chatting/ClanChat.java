package scripts.starfox.api2007.chatting;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api2007.Interfaces07;

/**
 * The ClanChat class is a utility class that provides a way to navigate the clan chat interface.
 *
 * @author Nolan
 */
public final class ClanChat {

    private static final int CC_MASTER_INDEX = 589;
    private static final int CC_SETUP_MASTER_INDEX = 590;

    private static final int CC_NAME_INDEX = 0;
    private static final int CC_OWNER_INDEX = 1;
    private static final int CC_JOIN_BUTTON_INDEX = 2;
    private static final int CC_LEAVE_BUTTON_INDEX = 2;
    private static final int CC_SETUP_BUTTON_INDEX = 3;
    private static final int CC_MEMEBER_LIST_INDEX = 5;

    /**
     * Checks to see if you're in a clan chat.
     *
     * @return True if you're in a clan chat, false otherwise.
     */
    public static boolean isInCC() {
        RSInterface memberList = Interfaces.get(CC_MASTER_INDEX, CC_MEMEBER_LIST_INDEX);
        return memberList != null && memberList.getChildren() != null;
    }

    /**
     * Gets the name of the clan chat you are in.
     *
     * @return The name of the clan chat you are in.
     *         An empty string is returned if the name could not be found.
     */
    public static String getCCName() {
        RSInterface nameLabel = Interfaces.get(CC_MASTER_INDEX, CC_NAME_INDEX);
        if (nameLabel != null) {
            String text = nameLabel.getText();
            if (text != null) {
                return text.substring(text.indexOf(":") + 2);
            }
        }
        return "";
    }

    /**
     * Gets the name of the owner of clan chat you are in.
     *
     * @return The name of the owner. An empty string is returned if the name could not be found.
     */
    public static String getCCOwnerName() {
        RSInterface ownerLabel = Interfaces.get(CC_MASTER_INDEX, CC_OWNER_INDEX);
        if (ownerLabel != null) {
            String text = ownerLabel.getText();
            if (text != null) {
                return text.substring(text.indexOf(":") + 2);
            }
        }
        return "";
    }

    /**
     * Checks to see if the setup menu is open.
     *
     * @return True if it is open, false otherwise.
     */
    public static boolean isSetupMenuOpen() {
        return Interfaces07.isUp(CC_SETUP_MASTER_INDEX);
    }

    /**
     * Joins the clan chat that has the specified owner name.
     *
     * This method will wait up to 3 seconds for you to be in the clan chat after entering the owner name.
     *
     * @param ownerName The owner name.
     * @return True if you joined the clan chat, false otherwise.
     */
    public static boolean joinCC(final String ownerName) {
        if (getCCOwnerName().equalsIgnoreCase(ownerName)) {
            return true;
        }
        if (TABS.CLAN.open()) {
            if (Clicking.click("Join Chat", Interfaces.get(CC_MASTER_INDEX, CC_JOIN_BUTTON_INDEX))) {
                if (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        AntiBan.sleep();
                        return Interfaces07.isEnterAmountMenuUp();
                    }
                }, 2000)) {
                    Keyboard.typeSend(ownerName);
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            Client.sleep(50);
                            return getCCOwnerName().equalsIgnoreCase(ownerName);
                        }
                    }, 3000);
                }
            }
        }
        return false;
    }

    /**
     * Leaves the clan chat you are in.
     *
     * If you are not in a clan chat, this method will immediately return false.
     *
     * This method will wait up to 3 seconds for you to not be in a clan chat after clicking the leave chat button.
     *
     * @return True if you left a clan chat, false otherwise.
     */
    public static boolean leaveCC() {
        if (!isInCC()) {
            return false;
        }
        if (TABS.CLAN.open()) {
            if (Clicking.click("Leave Chat", Interfaces.get(CC_MASTER_INDEX, CC_LEAVE_BUTTON_INDEX))) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Client.sleep(50);
                        return !isInCC();
                    }
                }, 3000);
            }
        }
        return false;
    }

    /**
     * Opens the clan setup menu.
     *
     * This method will wait up to 3 seconds for the clan setup menu to be open after clicking the clan setup button.
     *
     * @return True if the setup menu was opened, false otherwise.
     */
    public static boolean openSetup() {
        if (TABS.CLAN.open()) {
            if (Clicking.click("Clan Setup", Interfaces.get(CC_MASTER_INDEX, CC_SETUP_BUTTON_INDEX))) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Client.sleep(50);
                        return Interfaces07.isUp(CC_SETUP_MASTER_INDEX);
                    }
                }, 3000);
            }
        }
        return false;
    }

    /**
     * Sends the specified message to the clan chat that you are currently in.
     *
     * Does nothing if you are not currently in a clan chat.
     *
     * @param msg The message to send.
     * The '/' character before the message is optional as this method will handle that if the message does not start with a '/' character.
     */
    public static void sendMessage(Object msg) {
        String string = msg.toString();
        Keyboard.typeSend(string.startsWith("/") ? string : "/" + string);
    }
}
