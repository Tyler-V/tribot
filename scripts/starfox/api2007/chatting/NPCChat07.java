package scripts.starfox.api2007.chatting;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.NPCChat;

/**
 * @author Spencer
 */
public class NPCChat07 {

    /**
     * Clicks the Continue message for an NPC.
     * This method will wait for the NPC chat to be open for 2.5 seconds before returning false.
     *
     * @return True if the dialog was clicked, false otherwise.
     */
    public static boolean clickContinue() {
        return clickContinue(null);
    }

    /**
     * Clicks the Continue message for an NPC.
     * This method will wait for the NPC chat to be open for 2.5 seconds before returning false. If the message requirement is null or empty, this method will always click
     * continue.
     *
     * @param messageReq The message that must be present in order for the dialog to be clicked.
     * @return True if the dialog was clicked, false otherwise.
     */
    public static boolean clickContinue(final String messageReq) {
        if (Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return isNPCChatOpen() && (messageReq == null || NPCChat.getMessage().toLowerCase().contains(messageReq.toLowerCase()));
            }
        }, 2500)) {
            return NPCChat.clickContinue(true);
        } else {
            return false;
        }
    }

    /**
     * Clicks the specified NPC option.
     *
     * @param option The option being clicked.
     * @return True if the option was successfully clicked, false otherwise.
     */
    public static boolean clickNPCOption(final String option) {
        if (Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                boolean contains = false;
                if (isNPCChatOpen()) {
                    for (String s : NPCChat.getOptions()) {
                        if (s.contains(option)) {
                            contains = true;
                        }
                    }
                } else {
                    contains = true;
                }
                return contains;
            }
        }, 2500)) {
            General.println("Selecting Option");
            return NPCChat.selectOption(option, true);
        } else {
            return false;
        }
    }

    /**
     * Checks to see if the NPC chat dialog is open.
     *
     * @return Returns true if the NPC chat dialog is open false otherwise.
     */
    public static boolean isNPCChatOpen() {
        return NPCChat.getClickContinueInterface() != null || NPCChat.getSelectOptionInterface() != null || NPCChat.getSelectOptionInterfaces() != null;
    }
}
