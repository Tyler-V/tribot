package scripts.starfox.api2007.banking;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.Interfaces07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.Items07;
import scripts.starfox.api2007.entities.Objects07;

/**
 * @author Nolan
 */
public class DepositBox {

    private static final String DEPOSIT_BOX = "Bank deposit box";

    /**
     * The master index of the deposit box screen interface.
     */
    private static final int SCREEN_INDEX = 11;

    /**
     * The index of the items interface on the deposit box.
     */
    private static final int ITEMS_INDEX = 61;

    /**
     * Checks to see whether or not a deposit box is open.
     *
     * @return True if a deposit box is open, false otherwise.
     */
    public static final boolean isOpen() {
        return Interfaces07.isUp(SCREEN_INDEX);
    }

    /**
     * Checks to see if a deposit box is on the screen.
     *
     * @return True if a deposit box is on the screen, false otherwise.
     */
    public static final boolean isOnScreen() {
        return Entities.isOnScreen(Objects07.getObject(DEPOSIT_BOX, 15));
    }

    /**
     * Gets all of the items in the deposit box.
     *
     * @return An array in which each element represents an item in the deposit box. An empty array is returned if no items were found.
     */
    public static final RSItem[] getItems() {
        if (!isOpen()) {
            return Items07.empty();
        }
        RSInterface itemsInterface = Interfaces.get(SCREEN_INDEX, ITEMS_INDEX);
        if (itemsInterface != null) {
            RSItem[] items = itemsInterface.getItems();
            if (items != null) {
                for (RSItem item : items) {
                    convertToDepositBoxItem(item);
                }
                return items;
            }
        }
        return Items07.empty();
    }

    /**
     * Finds any items in the deposit box whose ID's match one of the specified ID's.
     *
     * @param ids The ID's to search for.
     * @return An array of items whose ID's match one of the specified ID's.
     */
    public static RSItem[] find(int... ids) {
        if (!isOpen()) {
            return Items07.empty();
        }
        List<RSItem> items = new ArrayList<>();
        for (RSItem item : getItems()) {
            if (ArrayUtil.contains(item.getID(), ids)) {
                items.add(item);
            }
        }
        return items.toArray(new RSItem[items.size()]);
    }

    /**
     * Finds any items in the deposit box whose name's match one of the specified names.
     *
     * @param names The names to search for.
     * @return An array of items whose name's match one of the specified names.
     */
    public static RSItem[] find(String... names) {
        if (!isOpen()) {
            return Items07.empty();
        }
        List<RSItem> items = new ArrayList<>();
        for (RSItem item : getItems()) {
            if (ArrayUtil.contains(item.getDefinition().getName(), names)) {
                items.add(item);
            }
        }
        return items.toArray(new RSItem[items.size()]);
    }

    /**
     * Attempts to open a deposit box.
     *
     * If no deposit boxes are on the screen, this method will return false.
     *
     * @return True if the deposit box was successfully opened or was already open prior to calling this method, false otherwise.
     */
    public static final boolean open() {
        if (isOpen()) {
            return true;
        }
        if (!isOnScreen()) {
            return false;
        }
        if (Clicking.click("Deposit", Objects07.getObject(DEPOSIT_BOX, 15))) {
            return Waiting.waitMoveCondition(new Condition() {
                @Override
                public boolean active() {
                    return isOpen();
                }
            }, 2000);
        }
        return false;
    }

    /**
     * Deposits the first item found in the inventory whose ID matches one of the specified IDs into a deposit box.
     *
     * @param amount The amount of the item to deposit.
     * @param ids    The list of ID's to consider for depositing.
     * @return True if the deposit was successful, false otherwise.
     */
    public static final boolean deposit(int amount, int... ids) {
        RSItem[] items = Inventory07.find(ids);
        return items.length > 0 && deposit(amount, items[0]);
    }

    /**
     * Deposits the first item found in the inventory whose name matches one of the specified names into a deposit box.
     *
     * @param amount The amount of the item to deposit.
     * @param names  The list of names to consider for depositing.
     * @return True if the deposit was successful, false otherwise.
     */
    public static final boolean deposit(int amount, String... names) {
        RSItem[] items = Inventory07.find(names);
        return items.length > 0 && deposit(amount, items[0]);
    }

    /**
     * Deposits the specified item into a deposit box.
     *
     * @param amount The amount of the item to deposit.
     * @param item   The item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static final boolean deposit(final int amount, final RSItem item) {
        if (item == null || !Inventory07.contains(item.getID())) {
            return false;
        }
        final int startCount = Inventory07.getCount(item.getID());
        RSObject deposit_box = Objects07.getObject("Bank deposit box", 15);
        if (deposit_box != null && deposit_box.isOnScreen()) {
            boolean used = false;
            if (!Interfaces07.isSelectOptionUp()) {
                if (Clicking.click("Use", item)) {
                    if (Clicking.click("Use", deposit_box) && Timing.waitCrosshair(75) == 2) {
                        used = true;
                        if (Inventory.getCount(item.getID()) > 1) {
                            Waiting.waitMoveCondition(new Condition() {
                                @Override
                                public boolean active() {
                                    return Interfaces07.isSelectOptionUp();
                                }
                            }, 2000);
                        }
                    }
                }
            }
            if (Inventory.getCount(item.getID()) == 1 && used) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        AntiBan.sleep();
                        return !Inventory07.contains(item.getID());
                    }
                }, 3000);
            }
            String op = getOption(amount);
            if (NPCChat.selectOption(op, true)) {
                if (op.equals("X")) {
                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            AntiBan.sleep();
                            return Interfaces07.isEnterAmountMenuUp();
                        }
                    }, 3000)) {
                        Keyboard.typeSend("" + amount);
                    }
                }
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        AntiBan.sleep();
                        return amount == 0 ? !Inventory07.contains(item.getID()) : Inventory07.getCount(item.getID()) + amount == startCount;
                    }
                }, 3000);
            }
        }
        return false;
    }

    /**
     * Deposits all items in your inventory except any items that have an ID that matches one of the specified IDs.
     *
     * @param ids The IDs to skip.
     * @return True if the deposit was successful, false otherwise.
     */
    public static final boolean depositAllExcept(int... ids) {
        if (Inventory07.containsOnly(ids)) {
            return true;
        }
        for (RSItem item : Inventory07.getAll()) {
            if (item != null && !ArrayUtil.contains(item.getID(), ids)) {
                deposit(0, item.getID());
            }
        }
        return Inventory07.containsOnly(ids);
    }
    
    /**
     * Deposits all items in your inventory except any items that have a name that matches one of the specified IDs.
     *
     * @param names The names to skip.
     * @return True if the deposit was successful, false otherwise.
     */
    public static final boolean depositAllExcept(String... names) {
        if (Inventory07.containsOnly(names)) {
            return true;
        }
        for (RSItem item : Inventory07.getAll()) {
            if (item != null && !ArrayUtil.contains(item.getDefinition().getName(), names)) {
                deposit(0, item.getDefinition().getName());
            }
        }
        return Inventory07.containsOnly(names);
    }

    /**
     * Gets the option for depositing.
     *
     * @param amount The amount being deposited.
     * @return The option for depositing.
     */
    private static String getOption(int amount) {
        String op;
        if (amount == 0) {
            op = "All";
        } else if (amount == 5) {
            op = "Five";
        } else if (amount == 1) {
            op = "One";
        } else {
            op = "X";
        }
        return op;
    }

    /**
     * Converts the specified item to a deposit box item.
     *
     * This method simply converts the area of the item to the deposit box equivalent area of the item.
     *
     * @param item The item to convert.
     */
    private static void convertToDepositBoxItem(RSItem item) {
        if (item == null) {
            return;
        }
        int x = 131 + ((int) Math.ceil((item.getIndex()) % 7) * 40);
        int y = 75 + ((int) ((Math.floor(item.getIndex()) / 7) % 4) * 42);
        item.setArea(new Rectangle(x, y, 31, 31));
    }
}
