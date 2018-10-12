package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.types.*;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.ArrayUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * The Interfaces07 class is a utility class that provides a way to navigate interfaces.
 *
 * @author Nolan
 */
public class Interfaces07 {

    /**
     * A constant array representing the texture ID's of all interfaces that are close buttons.
     */
    private static final int[] CLOSE_BUTTON_TEXTURE_IDS = {535, 539, 541, 831};

    /**
     * The master index of the make set interface.
     */
    public static final int MAKE_SET_MASTER_INDEX = 270;

    /**
     * The child index of the make set interface.
     */
    public static final int MAKE_SET_CHILD_INDEX = 14;

    /**
     * A cache containing the IDs of interfaces with their respective text.
     */
    private static final HashMap<String, Interface> interfaceCache = new HashMap<>();

    /**
     * Gets the interface cache.
     *
     * @return The interface cache.
     */
    public static HashMap<String, Interface> getInterfaceCache() {
        return interfaceCache;
    }

    /**
     * Gets all of the master interfaces that are currently valid in an array.
     *
     * @return The master interfaces.
     */
    public static RSInterfaceMaster[] getAll() {
        return Interfaces.getAll();
    }

    /**
     * Gets the interface at the specified master -> child -> component indexes.
     *
     * @param masterIndex    The master index.
     * @param childIndex     The child index.
     * @param componentIndex The component index.
     * @return The interface.
     * Null if no interface was found.
     */
    public static RSInterfaceComponent get(int masterIndex, int childIndex, int componentIndex) {
        RSInterfaceChild child = get(masterIndex, childIndex);
        if (child != null) {
            return child.getChild(componentIndex);
        }
        return null;
    }

    /**
     * Gets the interface at the specified master -> child indexes.
     *
     * @param masterIndex The master index.
     * @param childIndex  The child index.
     * @return The interface.
     * Null if no interface was found.
     */
    public static RSInterfaceChild get(int masterIndex, int childIndex) {
        return Interfaces.get(masterIndex, childIndex);
    }

    /**
     * Gets the interface at the specified master indexes.
     *
     * @param masterIndex The master index.
     * @return The interface.
     * Null if no interface was found.
     */
    public static RSInterfaceMaster get(int masterIndex) {
        return Interfaces.get(masterIndex);
    }

    /**
     * Gets the first interface found that contains the specified text.
     *
     * @param text The text to search for.
     * @return The first interface found that contains the specified text.
     * Null if no interface was found.
     */
    public static RSInterface get(String text) {
        //Cache interface so that it doesn't take forever when re-getting the same interface.
        if (getInterfaceCache().containsKey(text)) {
            Interface i = getInterfaceCache().get(text);
            if (i.getComponentIndex() != -1) {
                return get(i.getMasterIndex(), i.getChildIndex(), i.getComponentIndex());
            } else if (i.getChildIndex() != -1) {
                return get(i.getMasterIndex(), i.getChildIndex());
            } else {
                return get(i.getMasterIndex());
            }
        }
        RSInterfaceMaster[] all = getAll();
        if (all != null && all.length > 0) {
            for (RSInterfaceMaster master : all) {
                if (master != null) {
                    if (equalsText(master, text)) {
                        getInterfaceCache().put(text, new Interface(master.getIndex(), -1, -1));
                        Client.println("Cached interface at: [" + master.getIndex() + "]");
                        return master;
                    }
                    RSInterfaceChild[] children = master.getChildren();
                    if (children != null && children.length > 0) {
                        for (RSInterfaceChild child : children) {
                            if (child != null) {
                                if (equalsText(child, text)) {
                                    getInterfaceCache().put(text, new Interface(master.getIndex(), child.getIndex(), -1));
                                    Client.println("Cached interface at: [" + master.getIndex() + ", " + child.getIndex() + "]");
                                    return child;
                                }
                                RSInterfaceComponent[] components = child.getChildren();
                                if (components != null && components.length > 0) {
                                    for (RSInterfaceComponent component : components) {
                                        if (component != null) {
                                            if (equalsText(component, text)) {
                                                getInterfaceCache().put(text, new Interface(master.getIndex(), child.getIndex(), component.getIndex()));
                                                Client.println("Cached interface at: [" + master.getIndex() + ", " + child.getIndex() + ", " + component.getIndex() + "]");
                                                return component;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the interface that corresponds to the specified interface wrapper.
     *
     * @param inter The interface wrapper.
     * @return The interface that matches the specified interface wrapper.
     * Null if no interface matches the wrapper.
     */
    public static RSInterface get(Interface inter) {
        if (inter.getComponentIndex() != -1) {
            return get(inter.getMasterIndex(), inter.getChildIndex(), inter.getComponentIndex());
        } else if (inter.getChildIndex() != -1) {
            return get(inter.getMasterIndex(), inter.getChildIndex());
        } else {
            return get(inter.getMasterIndex());
        }
    }

    /**
     * Gets the actions of the specified interface.
     *
     * @param inter The interface.
     * @return The actions as an array of Strings.
     * An empty array is returned if the interface does not exist or the interface has no actions.
     */
    public static String[] getActions(RSInterface inter) {
        if (inter != null) {
            String[] actions = inter.getActions();
            if (actions != null) {
                return actions;
            }
        }
        return new String[0];
    }

    /**
     * Gets the area of the specified interface.
     *
     * @param inter The interface.
     * @return The area of the interface.
     * An empty rectangle is returned if the area could not be found.
     */
    public static Rectangle getArea(RSInterface inter) {
        Rectangle empty = new Rectangle();
        if (inter == null) {
            return empty;
        }
        Rectangle rect = inter.getAbsoluteBounds();
        return rect == null ? empty : rect;
    }

    /**
     * Gets the item ID of the specified interface (if any).
     *
     * @param inter The interface.
     * @return The item ID of the specified interface.
     * -1 is returned if the interface is null.
     */
    public static int getItemId(RSInterface inter) {
        if (inter != null) {
            return inter.getComponentItem();
        }
        return -1;
    }

    /**
     * Gets the items in the specified interface.
     *
     * @param inter The interface.
     * @return The items in the specified interface as an array of RSItem's.
     * An empty array is returned if the interface has no items or was null.
     */
    public static RSItem[] getItems(RSInterface inter) {
        if (inter != null) {
            return inter.getItems();
        }
        return new RSItem[0];
    }

    /**
     * Gets the text of the specified interface.
     *
     * @param inter The interface.
     * @return The text on the specified interface.
     * An empty String is returned if the interface has no text or was null.
     */
    public static String getText(RSInterface inter) {
        if (inter != null) {
            String text = inter.getText();
            if (text != null) {
                return text;
            }
        }
        return "";
    }

    /**
     * Gets the texture ID of the specified interface.
     *
     * @param inter The interface.
     * @return The texture ID of the specified interface.
     * -1 is returned if the interface was null.
     */
    public static int getTextureId(RSInterface inter) {
        if (inter != null) {
            return inter.getTextureID();
        }
        return -1;
    }

    /**
     * Checks to see if the specified interface contains the specified text.
     * This method strips all formatting from the interface text in HTML.
     *
     * @param inter The interface being tested.
     * @param txt   The text to check for.
     * @return True if the specified interface contains the specified text, false otherwise.
     */
    public static boolean containsText(RSInterface inter, String txt) {
        return inter != null && General.stripFormatting(getText(inter).replaceAll("<col=00ff00>", "")).contains(txt);
    }

    /**
     * Checks to see if the specified interface's text equals the specified text.
     * This method strips all formatting from the interface text in HTML.
     *
     * @param inter The interface being tested.
     * @param txt   The text to check for.
     * @return True if the specified interface's text equals the specified text, false otherwise.
     */
    public static boolean equalsText(RSInterface inter, String txt) {
        return inter != null && General.stripFormatting(getText(inter).replaceAll("<col=00ff00>", "")).equals(txt);
    }

    /**
     * Checks to see whether or not any interfaces whose master index match any of the specified indexes are up.
     *
     * @param indexes The indexes of the interfaces.
     * @return True if any interfaces are up, false otherwise.
     */
    public static boolean isUp(int... indexes) {
        for (int index : indexes) {
            if (Interfaces.isInterfaceValid(index)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see whether or not any interfaces whose text matches the text specified are visible.
     *
     * @param string The string to check for.
     * @return True if there is an interface with the specified text that is visible, false otherwise.
     */
    public static boolean isUp(String string) {
        RSInterfaceMaster[] masters = Interfaces.getAll();
        if (masters != null && masters.length > 0) {
            for (RSInterfaceMaster master : masters) {
                if (master != null && !master.isHidden()) {
                    if (equalsText(master, string)) {
                        return true;
                    } else if (isUp(string, master.getIndex())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if a child or component interface of the specified master index is up with the specified text.
     *
     * @param text   The text to look for.
     * @param master The master index to search.
     * @return True if there is an interface up with the specified text, false otherwise.
     */
    public static boolean isUp(String text, int master) {
        RSInterfaceMaster inter = get(master);
        if (inter != null) {
            RSInterfaceChild[] children = inter.getChildren();
            if (children != null && children.length > 0) {
                for (RSInterfaceChild child : children) {
                    if (child != null && !child.isHidden()) {
                        if (containsText(child, text)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified interface is clickable.
     *
     * @param inter The interface to check.
     * @return True if the interface is clickable, false otherwise.
     */
    public static boolean isClickable(RSInterface inter) {
        return inter != null && inter.isClickable();
    }

    /**
     * Checks to see if the specified interface is hidden.
     *
     * @param inter The interface to check.
     * @return True if the interface is hidden, false otherwise.
     */
    public static boolean isHidden(RSInterface inter) {
        return inter == null || inter.isHidden();
    }

    /**
     * Checks to see if the enter amount menu is visible.
     *
     * @return True if it is visible, false otherwise.
     */
    public static boolean isEnterAmountMenuUp() {
        return !isHidden(get("Enter amount:"));
        //return Screen.getColorAt(new Point(260, 428)).equals(new Color(0, 0, 128));
    }

    /**
     * Checks to see if the click continue interface is up.
     *
     * @return True if it is visible, false otherwise.
     */
    public static boolean isClickContinueUp() {
        return NPCChat.getClickContinueInterface() != null;
    }

    /**
     * Checks to see if the select option interface is up.
     *
     * @return True if it is up, false otherwise.
     */
    public static boolean isSelectOptionUp() {
        return NPCChat.getSelectOptionInterface() != null;
    }

    /**
     * Checks to see if the specified interface is a close button.
     *
     * @param inter The interface.
     * @return True if it is a close button, false otherwise.
     */
    public static boolean isCloseButton(RSInterface inter) {
        return ArrayUtil.contains(getTextureId(inter), CLOSE_BUTTON_TEXTURE_IDS);
    }

    /**
     * Closes any "closeable" interfaces that are open.
     * An interface is considered to be closable if it has a close button within it.
     *
     * @return True if an interface was closed, false otherwise.
     */
    public static boolean closeAll() {
        RSInterfaceMaster[] all = getAll();
        if (all.length > 0) {
            for (RSInterfaceMaster master : all) {
                RSInterfaceChild[] children = master.getChildren();
                if (children != null && children.length > 0) {
                    for (RSInterfaceChild child : children) {
                        if (child != null) {
                            if (isCloseButton(child)) {
                                Keyboard.pressKeys(KeyEvent.VK_ESCAPE);
                                return child.isHidden() || Clicking.click(child);
                            } else {
                                RSInterfaceComponent[] components = child.getChildren();
                                if (components != null && components.length > 0) {
                                    for (RSInterfaceComponent component : components) {
                                        if (component != null) {
                                            if (isCloseButton(component)) {
                                                Keyboard.pressKeys(KeyEvent.VK_ESCAPE);
                                                return component.isHidden() || Clicking.click(component);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Wrapper class for interface caching because you cannot create a RSInterface without there being one loaded in the game.
     */
    private static class Interface {

        private final int masterIndex;
        private final int childIndex;
        private final int componentIndex;

        /**
         * Constructs a new Interface.
         *
         * @param masterIndex    The master index.
         * @param childIndex     The child index.
         * @param componentIndex The component index.
         */
        Interface(int masterIndex, int childIndex, int componentIndex) {
            this.masterIndex = masterIndex;
            this.childIndex = childIndex;
            this.componentIndex = componentIndex;
        }

        /**
         * Gets the master index for the interface.
         *
         * @return The master index.
         */
        int getMasterIndex() {
            return masterIndex;
        }

        /**
         * Gets the child index for the interface.
         *
         * @return The child index.
         */
        int getChildIndex() {
            return childIndex;
        }

        /**
         * Gets the component index for the interface.
         *
         * @return The component index.
         */
        int getComponentIndex() {
            return componentIndex;
        }
    }
}
