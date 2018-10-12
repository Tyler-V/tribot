package scripts.starfox.api2007.chatting;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import scripts.starfox.api.util.ArrayUtil;

/**
 * @author Nolan
 */
public class Chat {

    private static final int[] NOT_SELECTED_IDS = {1019, 1020};
    private static final int[] SELECTED_IDS = {1022, 1023};

    /**
     * Gets the chat box.
     *
     * @return The chat box. Null if the chat box could not be found.
     */
    public static RSInterfaceChild getChatBox() {
        return Interfaces.get(137, 2);
    }

    /**
     * Gets the chat field (where your message is typed).
     *
     * @return The chat field. Null if the chat field could not be found.
     */
    public static RSInterfaceChild getChatField() {
        return Interfaces.get(137, 1);
    }

    public enum Tab {

        ALL(3),
        GAME(6),
        PUBLIC(10),
        PRIVATE(14),
        CLAN(18),
        TRADE(22);

        private final int childIndex;

        /**
         * Constructs a new Tab.
         *
         * @param childIndex The child index of the tab.
         */
        Tab(int childIndex) {
            this.childIndex = childIndex;
        }

        /**
         * Gets the child index of the tab.
         *
         * @return The child index.
         */
        public final int getChildIndex() {
            return childIndex;
        }

        /**
         * Gets the interface of the tab.
         *
         * @return The interface. Null if the interface could not be found.
         */
        public final RSInterface getInterface() {
            return Interfaces.get(548, getChildIndex());
        }

        /**
         * Checks to see if the tab is selected.
         *
         * @return True if it is selected, false otherwise.
         */
        public final boolean isSelected() {
            RSInterface inter = getInterface();
            return inter != null && ArrayUtil.contains(inter.getTextureID(), SELECTED_IDS);
        }

        /**
         * Selects the tab.
         *
         * @return True if the tab was selected successfully, false otherwise.
         */
        public final boolean select() {
            if (isSelected()) {
                return true;
            }
            if (Clicking.click("Switch tab", getInterface())) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return isSelected();
                    }
                }, 1000);
            }
            return false;
        }
    }
}
