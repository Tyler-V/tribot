package scripts.starfox.api2007;

import scripts.starfox.api2007.banking.Bank07;
import java.util.ArrayList;
import java.util.List;
import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api.util.ArrayUtil;

/**
 * @author Nolan
 */
public class LootingBag {

    public static final int LOOTING_BAG_ID = 11941;

    private static final int LOOTING_BAG_BANK_MASTER_INDEX = 15;

    private static final int LOOTING_BAG_BANK_ITEMS_INDEX = 12;

    /**
     * Checks to see whether or not the looting bag deposit screen is open.
     *
     * @return True if it is open, false otherwise.
     */
    public static boolean isDepositScreenOpen() {
        RSInterface depositScreen = Interfaces.get(LOOTING_BAG_BANK_MASTER_INDEX, LOOTING_BAG_BANK_ITEMS_INDEX);
        return depositScreen != null && !depositScreen.isHidden();
    }

    /**
     * Checks to see if the looting bag has items in it.
     *
     * Note that this method will not work as intended if the bank screen is not open.
     *
     * @return True if it has items in it, false otherwise.
     */
    public static boolean hasItems() {
        RSInterfaceChild bagChild = Interfaces.get(LOOTING_BAG_BANK_MASTER_INDEX, 2);
        return bagChild != null && bagChild.getChildren() != null;
    }

    /**
     * Gets the items from the looting bag.
     *
     * @return An array of items that are inside the looting bag.
     */
    public static RSItem[] getItems() {
        if (!Interfaces07.isUp(LOOTING_BAG_BANK_MASTER_INDEX)) {
            return new RSItem[0];
        }
        RSInterfaceChild itemsInterface = Interfaces.get(LOOTING_BAG_BANK_MASTER_INDEX, LOOTING_BAG_BANK_ITEMS_INDEX);
        if (itemsInterface != null) {
            RSInterfaceComponent[] components = itemsInterface.getChildren();
            if (components != null) {
                List<RSItem> items = new ArrayList<>();
                for (RSInterfaceComponent component : components) {
                    if (component != null) {
                        String name = component.getComponentName();
                        String[] actions = component.getActions();
                        if (name != null && actions != null) {
                            RSItem item = new RSItem(
                                    name.substring(name.indexOf(">") + 1, name.indexOf("</")),
                                    component.getActions(),
                                    component.getComponentIndex(),
                                    component.getComponentItem(),
                                    component.getComponentStack(),
                                    RSItem.TYPE.OTHER);
                            item.setArea(component.getAbsoluteBounds());
                            items.add(item);
                        }
                    }
                }
                return items.toArray(new RSItem[items.size()]);
            }
        }
        return new RSItem[0];
    }

    /**
     * Finds items in the looting bag whose ID matches one of the specified IDs.
     *
     * @param ids The IDs to look for.
     * @return An array of items in the looting bag whose ID matches one of the specified IDs.
     */
    public static RSItem[] find(int... ids) {
        List<RSItem> items = new ArrayList<>();
        for (RSItem item : getItems()) {
            if (item != null) {
                if (ArrayUtil.contains(item.getID(), ids)) {
                    items.add(item);
                }
            }
        }
        return items.toArray(new RSItem[items.size()]);
    }

    /**
     * Finds items in the looting bag whose name matches one of the specified names.
     *
     * @param names The names to look for.
     * @return An array of items in the looting bag whose name matches one of the specified names.
     */
    public static RSItem[] find(String... names) {
        List<RSItem> items = new ArrayList<>();
        for (RSItem item : getItems()) {
            if (item != null) {
                RSItemDefinition def = item.getDefinition();
                if (def != null) {
                    String name = def.getName();
                    if (name != null && ArrayUtil.contains(name, names)) {
                        items.add(item);
                    }
                }
            }
        }
        return items.toArray(new RSItem[items.size()]);
    }

    /**
     * Deposits an item in the inventory whose name matches the specified name into the looting bag.
     *
     * If your player is not in the wilderness this method will immediately return false.
     *
     * @param name   The name of the item to deposit.
     * @param amount The amount of the item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(String name, int amount) {
        return deposit(Inventory07.getItem(name), amount);
    }

    /**
     * Deposits an item in the inventory whose ID matches the specified ID into the looting bag.
     *
     * If your player is not in the wilderness this method will immediately return false.
     *
     * @param id     The ID of the item to deposit.
     * @param amount The amount of the item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(int id, int amount) {
        return deposit(Inventory07.getItem(id), amount);
    }

    /**
     * Deposits the specified item into the looting bag.
     *
     * If your player is not in the wilderness this method will immediately return false.
     *
     * @param item   The item to deposit.
     * @param amount The amount of the item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(RSItem item, int amount) {
        if (!Wilderness.isInWilderness() || item == null || !Inventory07.contains(LOOTING_BAG_ID)) {
            return false;
        }
        if (!Interfaces07.isSelectOptionUp()) {
            if (Clicking.click("Use", item)) {
                if (Clicking.click("Use", Inventory07.find(LOOTING_BAG_ID))) {
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return Interfaces07.isSelectOptionUp();
                        }
                    }, 3000);
                }
            }
        }
        String option;
        if (amount >= Inventory07.getCount(item.getID()) || amount == 0) {
            option = "All";
        } else if (amount > 1) {
            option = "X";
        } else {
            option = "One";
        }
        if (option.equals("X")) {
            if (NPCChat.selectOption(option, true)) {
                if (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return Interfaces07.isEnterAmountMenuUp();
                    }
                }, 3000)) {
                    Keyboard.typeSend("" + amount);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return NPCChat.selectOption(option, true);
    }

    /**
     * Deposit an item whose name matches the specified name from the looting bag into the bank.
     *
     * @param name   The name of the item to deposit.
     * @param amount The amount of the item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean depositBank(String name, int amount) {
        RSItem[] items = find(name);
        return items.length > 0 ? depositBank(items[0], amount) : false;
    }

    /**
     * Deposit an item whose ID matches the specified ID from the looting bag into the bank.
     *
     * @param id     The ID of the item to deposit.
     * @param amount The amount of the item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean depositBank(int id, int amount) {
        RSItem[] items = find(id);
        return items.length > 0 ? depositBank(items[0], amount) : false;
    }

    /**
     * Deposit the specified item from the looting bag into the bank.
     *
     * @param item   The item to deposit.
     * @param amount The amount of the item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean depositBank(RSItem item, int amount) {
        if (!Bank07.isOpen() || item == null || !Inventory07.contains(LOOTING_BAG_ID)) {
            return false;
        }
        if (!isDepositScreenOpen()) {
            if (Clicking.click("View", Inventory07.getItem(LOOTING_BAG_ID))) {
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return isDepositScreenOpen();
                    }
                }, 3000);
            }
        }
        if (isDepositScreenOpen()) {
            String option;
            if (amount >= item.getStack() || amount == 0) {
                option = "Deposit-All";
            } else if (amount > 5 && amount < item.getStack()) {
                option = "Deposit-X";
            } else if (amount > 1) {
                option = "Deposit-5";
            } else {
                option = "Deposit-1";
            }
            if (Clicking.click(option, item)) {
                if (option.contains("X")) {
                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return Interfaces07.isEnterAmountMenuUp();
                        }
                    }, 3000)) {
                        Keyboard.typeSend("" + amount);
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
