package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Strings;

/**
 * @author Nolan
 */
public class Options07 {

    //The master interface ID for the Options tab.
    public static final int TAB_MASTER_INDEX = 261;

    //DISPLAY SETTINGS
    private static final int BRIGHTNESS_SETTING = 166;
    private static final int DATA_ORBS_SETTING = 1055;
    private static final int EXP_SETTING = 427;

    //SOUND SETTINGS
    private static final int AREA_VOLUME_SETTING = 872;
    private static final int SOUND_EFFECTS_VOLUME_SETTING = 169;
    private static final int MUSIC_VOLUME_SETTING = 168;

    //CHAT SETTINGS
    private static final int CHAT_EFFECTS_SETTING = 171;
    private static final int PRIVATE_CHAT_SETTING = 287;
    private static final int PROFANITY_FILTER_SETTING = 1074;
    private static final int LOGIN_TIMEOUT_SETTING = 1055;

    //CONTROL SETTINGS
    private static final int MOUSE_BUTTON_SETTING = 170;
    private static final int CAMERA_SETTING = 1055;
    private static final int ATTACK_PRIORITY_SETTING = 1107;

    //GENERAL SETTINGS
    private static final int AID_SETTING = 427;
    private static final int RUN_SETTING = 173;

    //OPTIONS MASTER INDEX
    private static final int MASTER_INDEX = 261;

    //DISPLAY INTERFACES
    private static final int[] BRIGHTNESS_INDEXES = {5, 6, 7, 8};
    private static final int DATA_ORBS_INDEX = 9;
    private static final int ROOF_INDEX = 11;
    private static final int EXP_INDEX = 13;

    //SOUND INTERFACES
    private static final int[] MUSIC_INDEXES = {17, 18, 19, 20, 21};
    private static final int[] SOUND_EFFECT_INDEXES = {23, 24, 25, 26, 27};
    private static final int[] AREA_SOUND_INDEXES = {29, 30, 31, 32, 33};

    //CHAT INTERFACES
    private static final int CHAT_EFFECTS_INDEX = 35;
    private static final int PRIVATE_CHAT_INDEX = 37;
    private static final int PROFANITY_FILTER_INDEX = 39;
    private static final int LOGIN_TIMEOUT_INDEX = 41;

    //CONTROL INTERFACES
    private static final int MOUSE_BUTTONS_INDEX = 44;
    private static final int CAMERA_INDEX = 46;
    private static final int[] ATTACK_PRIORITY_INDEXES = {49, 51, 50};

    //GENERAL INTERFACES
    private static final int AID_INDEX = 53;
    private static final int RUN_INDEX = 55;
    private static final int RUN_ORB_INDEX = 21;

    /**
     * Constants representing each tab on the options interface.
     */
    public enum Tab {

        DISPLAY(0),
        SOUND(2),
        CHAT(4),
        CONTROLS(6);

        private final int TABS_INDEX = 1;
        private final int OPEN_TEXTURE = 762;

        private final int index;

        /**
         * Constructs a new Tab.
         *
         * @param index The component index of the tab.
         */
        Tab(int index) {
            this.index = index;
        }

        /**
         * Gets the component index of the tab.
         *
         * @return The index.
         */
        public int getIndex() {
            return this.index;
        }

        /**
         * Gets the component of the tab.
         *
         * @return The component.
         */
        public RSInterfaceComponent getComponent() {
            RSInterfaceChild tabs = Interfaces07.get(MASTER_INDEX, TABS_INDEX);
            if (tabs != null) {
                return tabs.getChild(getIndex());
            }
            return null;
        }

        /**
         * Checks to see if the tab is open.
         *
         * @return True if it is open, false otherwise.
         */
        public boolean isOpen() {
            RSInterface tab = getComponent();
            return tab != null && tab.getTextureID() == OPEN_TEXTURE;
        }

        /**
         * Opens the tab.
         *
         * @return True if the tab was opened successfully or if the tab was already opened at the time of execution, false otherwise.
         */
        public boolean open() {
            if (isOpen()) {
                return true;
            }
            RSInterface tab = getComponent();
            if (tab != null) {
                if (Clicking.click(tab)) {
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            Client.sleep(10);
                            return isOpen();
                        }
                    }, 500);
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return Strings.enumToString(name());
        }
    }

    /**
     * Gets the screen brightness.
     *
     * @return The screen brightness as an integer.
     * This value will always be from 0 to 3 inclusive.
     */
    public static int getBrightness() {
        return Settings.get(BRIGHTNESS_SETTING) - 1;
    }

    /**
     * Checks to see if data orbs are enabled.
     *
     * @return True if they are enabled, false otherwise.
     */
    public static boolean isDataOrbsEnabled() {
        return Settings.get(DATA_ORBS_SETTING) == 0;
    }

    /**
     * Checks to see if the toggle roofs option is enabled.
     *
     * @return True if it is enabled, false otherwise.
     * Roofs hidden = true
     * Roofs showing = false
     */
    public static boolean isToggleRoofsEnabled() {
        RSInterface roofs = Interfaces07.get(MASTER_INDEX, ROOF_INDEX);
        return roofs != null && roofs.getTextureID() == 762;
    }

    /**
     * Checks to see if the exp to next level is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isExpNextLevelEnabled() {
        return (Settings.get(EXP_SETTING) & 0x2) == 0;
    }

    /**
     * Gets the music volume. This value will always be from 0 to 4 inclusive.
     *
     * @return The music volume as an integer.
     */
    public static int getMusicVolume() {
        return Math.abs(4 - Settings.get(MUSIC_VOLUME_SETTING));
    }

    /**
     * Gets the sound effects volume. This value will always be from 0 to 4 inclusive.
     *
     * @return The sound effects volume as an integer.
     */
    public static int getSoundEffectsVolume() {
        return Math.abs(4 - Settings.get(SOUND_EFFECTS_VOLUME_SETTING));
    }

    /**
     * Gets the area volume. This value will always be from 0 to 4 inclusive.
     *
     * @return The area volume as an integer.
     */
    public static int getAreaVolume() {
        return Math.abs(4 - Settings.get(AREA_VOLUME_SETTING));
    }

    /**
     * Checks to see if chat effects are enabled.
     *
     * @return True if they are enabled, false otherwise.
     */
    public static boolean isChatEffectsEnabled() {
        return Settings.get(CHAT_EFFECTS_SETTING) == 0;
    }

    /**
     * Checks to see if split private chat is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isSplitPrivateChatEnabled() {
        return Settings.get(PRIVATE_CHAT_SETTING) == 1;
    }

    /**
     * Checks to see if the profanity filter is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isProfanityFilterEnabled() {
        return Settings.get(PROFANITY_FILTER_SETTING) == 0;
    }

    /**
     * Checks to see if login notifications are enabled.
     *
     * @return True if they are enabled, false otherwise.
     */
    public static boolean isLoginNotificationEnabled() {
        return (Settings.get(LOGIN_TIMEOUT_SETTING) & 0x80) == 0;
    }

    /**
     * Checks to see if the 2 mouse button option is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isMouseButtonsEnabled() {
        return Settings.get(MOUSE_BUTTON_SETTING) == 0;
    }

    /**
     * Checks to see if the camera mouse is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isCameraMouseEnabled() {
        return (Settings.get(CAMERA_SETTING) & 0x20) == 0;
    }

    /**
     * Gets the attack priority. This value will always be from 0 to 2 inclusive.
     *
     * @return The attack priority.
     * 0 = Depends on combat level.
     * 1 = Always right-click.
     * 2 = Left-click where available.
     */
    public static int getAttackPriority() {
        return Settings.get(ATTACK_PRIORITY_SETTING);
    }

    /**
     * Checks to see if accept aid is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isAcceptAidEnabled() {
        return (Settings.get(AID_SETTING) & 0x1) == 1;
    }

    /**
     * Checks to see if run is enabled.
     *
     * @return True if it is enabled, false otherwise.
     */
    public static boolean isRunEnabled() {
        return Settings.get(RUN_SETTING) == 1;
    }

    /**
     * Opens the options tab.
     *
     * @return True if the tab was opened successfully or the tab was already opened at the time of execution, false otherwise.
     */
    public static boolean open() {
        return TABS.OPTIONS.open();
    }

    /**
     * Sets an option.
     *
     * @param index The interface index of the option.
     * @param tab   The tab the option is located on.
     * @return True if successful, false otherwise.
     */
    private static boolean setOption(int index, Tab tab) {
        if (open() && tab == null || tab.open()) {
            if (Clicking.click(Interfaces07.get(MASTER_INDEX, index))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets a slider to the specified index.
     *
     * @param index         The index.
     * @param sliderIndexes The indexes of the slider.
     * @param tab           The tab that the slider belongs to.
     * @return True if successful, false otherwise.
     */
    private static boolean setSlider(int index, int[] sliderIndexes, Tab tab) {
        if (index < 0 || index > sliderIndexes.length - 1) {
            throw new IllegalArgumentException("Index must be between 0 and " + (sliderIndexes.length - 1) + " inclusive.");
        }
        if (open() && tab.open()) {
            if (Clicking.click(Interfaces07.get(MASTER_INDEX, sliderIndexes[index]))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the brightness. Opens the options tab if it is not already open.
     *
     * @param brightness The brightness to set.
     *                   This value must be between 0 and 3 inclusive.
     * @return True if the brightness was set successfully, false otherwise.
     */
    public static boolean setBrightness(final int brightness) {
        if (setSlider(brightness, BRIGHTNESS_INDEXES, Tab.DISPLAY)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return getBrightness() == brightness;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets data orbs to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setDataOrbs(final boolean on) {
        if (isDataOrbsEnabled() == on) {
            return true;
        }
        if (setOption(DATA_ORBS_INDEX, Tab.DISPLAY)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isDataOrbsEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the toggle roof option to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True of on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean toggleRoofs(final boolean on) {
        if (isToggleRoofsEnabled() == on) {
            return true;
        }
        if (setOption(ROOF_INDEX, Tab.DISPLAY)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isToggleRoofsEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the display of exp to next level to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setExpNextLevel(final boolean on) {
        if (isExpNextLevelEnabled() == on) {
            return true;
        }
        if (setOption(EXP_INDEX, Tab.DISPLAY)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isExpNextLevelEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the music volume. Opens the options tab if it is not already open.
     *
     * @param volume The music volume to set.
     *               Must be between 0 and 4 inclusive.
     * @return True if setting the music volume was successful, false otherwise.
     */
    public static boolean setMusicVolume(final int volume) {
        if (getMusicVolume() == volume) {
            return true;
        }
        if (setSlider(volume, MUSIC_INDEXES, Tab.SOUND)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return getMusicVolume() == volume;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the sound effects volume. Opens the options tab if it is not already open.
     *
     * @param volume The sound effects volume to set.
     *               Must be between 0 and 4 inclusive.
     * @return True if setting the sound effects volume was successful, false otherwise.
     */
    public static boolean setSoundEffectsVolume(final int volume) {
        if (getSoundEffectsVolume() == volume) {
            return true;
        }
        if (setSlider(volume, SOUND_EFFECT_INDEXES, Tab.SOUND)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return getSoundEffectsVolume() == volume;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the area volume. Opens the options tab if it is not already open.
     *
     * @param volume The area volume to set.
     *               Must be between 0 and 4 inclusive.
     * @return True if setting the area volume was successful, false otherwise.
     */
    public static boolean setAreaVolume(final int volume) {
        if (getAreaVolume() == volume) {
            return true;
        }
        if (setSlider(volume, AREA_SOUND_INDEXES, Tab.SOUND)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return getAreaVolume() == volume;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the chat effects to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setChatEffects(final boolean on) {
        if (isChatEffectsEnabled() == on) {
            return true;
        }
        if (setOption(CHAT_EFFECTS_INDEX, Tab.CHAT)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isChatEffectsEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the split private chat option to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setSplitPrivateChat(final boolean on) {
        if (isSplitPrivateChatEnabled() == on) {
            return true;
        }
        if (setOption(PRIVATE_CHAT_INDEX, Tab.CHAT)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isSplitPrivateChatEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the profanity filter to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setProfanityFilter(final boolean on) {
        if (isProfanityFilterEnabled() == on) {
            return true;
        }
        if (setOption(PROFANITY_FILTER_INDEX, Tab.CHAT)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isProfanityFilterEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the login notification option to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setLoginNotification(final boolean on) {
        if (isLoginNotificationEnabled() == on) {
            return true;
        }
        if (setOption(LOGIN_TIMEOUT_INDEX, Tab.CHAT)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isLoginNotificationEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the mouse buttons option to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setMouseButtons(final boolean on) {
        if (isMouseButtonsEnabled() == on) {
            return true;
        }
        if (setOption(MOUSE_BUTTONS_INDEX, Tab.CONTROLS)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isMouseButtonsEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the camera mouse option to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setCameraMouse(final boolean on) {
        if (isCameraMouseEnabled() == on) {
            return true;
        }
        if (setOption(CAMERA_INDEX, Tab.CONTROLS)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isCameraMouseEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the attack priority. Opens the options tab if it is not already open.
     *
     * @param priority The attack priority to set.
     *                 Must be between 0 and 2 inclusive.
     *                 0 = Depends on combat level.
     *                 1 = Always right-click.
     *                 2 = Left-click where available.
     * @return True if attack priority was set successfully, false otherwise.
     */
    public static boolean setAttackPriority(final int priority) {
        if (getAttackPriority() == priority) {
            return true;
        }
        if (setSlider(priority, ATTACK_PRIORITY_INDEXES, Tab.CONTROLS)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return getAttackPriority() == priority;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets the accept aid option to be on or off. Opens the options tab if it is not already open.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setAcceptAid(final boolean on) {
        if (isAcceptAidEnabled() == on) {
            return true;
        }
        if (setOption(AID_INDEX, null)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(10);
                    return isAcceptAidEnabled() == on;
                }
            }, 500);
        }
        return false;
    }

    /**
     * Sets run to be on or off. Opens the options tab if it is not already open and data orbs are not enabled.
     * This method uses the data orb to set run if data orbs are enabled.
     *
     * @param on True for on, false for off.
     * @return True if setting the option was successful, false otherwise.
     */
    public static boolean setRun(final boolean on) {
        if (isRunEnabled() == on) {
            Client.println(isRunEnabled());
            return true;
        }
        boolean orb = isDataOrbsEnabled();
        int master = orb ? 160 : MASTER_INDEX;
        int child = orb ? RUN_ORB_INDEX : RUN_INDEX;
        if (orb || (Tab.DISPLAY.open())) {
            if (Clicking.click(Interfaces07.get(master, child))) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Client.sleep(10);
                        return isRunEnabled() == on;
                    }
                }, 500);
            }
        }
        return false;
    }
}
