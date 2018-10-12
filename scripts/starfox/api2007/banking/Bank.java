package scripts.starfox.api2007.banking;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.preferences.OpenBankPreference;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api.Printing;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api.waiting.Condition07;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.api2007.Interfaces07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.NPCs07;
import scripts.starfox.api2007.entities.Objects07;
import scripts.starfox.osbuddy.OSBuddy;
import scripts.starfox.osbuddy.OSBuddyItem;

import java.util.ArrayList;

/**
 * @author Nolan
 */
public class Bank {

    private static final int MASTER_INDEX = 12;

    private static final int ITEM_COUNT_INDEX = 5;
    private static final int SEARCH_INDEX = 27;

    //<editor-fold defaultstate="collapsed" desc="Get/Find Methods">

    /**
     * Gets the interface child at the specified index contained within the bank screen master interface.
     *
     * @param childIndex The child index.
     * @return The interface.
     * Null if no interface was found.
     */
    private static RSInterfaceChild getInterface(int childIndex) {
        return Interfaces07.get(MASTER_INDEX, childIndex);
    }

    /**
     * Gets all of the items in the bank.
     *
     * @return The items in the bank.
     * All elements in the array are guaranteed to be non-null.
     * An empty array is returned if the bank is not open.
     */
    public static RSItem[] getAll() {
        return Banking.getAll();
    }

    /**
     * Gets an item in the bank whose ID matches the specified ID.
     *
     * @param id The ID of the item.
     * @return An item with the specified ID.
     * Null if no item was found or the bank was not loaded at the time of the method call.
     */
    public static RSItem getItem(int id) {
        if (!isOpen()) {
            return null;
        }
        for (RSItem item : getAll()) {
            if (item != null) {
                if (item.getID() == id) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Gets an item in the bank whose name matches the specified name.
     *
     * @param name The name of the item.
     * @return An item with the specified name.
     * Null if no item was found or the bank was not loaded at the time of the method call.
     */
    public static RSItem getItem(String name) {
        if (!isOpen()) {
            return null;
        }
        for (RSItem item : getAll()) {
            if (item != null) {
                RSItemDefinition definition = item.getDefinition();
                if (definition != null) {
                    String n = definition.getName();
                    if (n != null && n.equalsIgnoreCase(name)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the gold value of your bank.
     *
     * @return The gold value.
     */
    public static int getValue() {
        if (isOpen()) {
            int value = 0;
            for (RSItem item : getAll()) {
                if (item != null) {
                    if (item.getID() == 995) {
                        value += item.getStack();
                    } else {
                        OSBuddyItem osBuddyItem = OSBuddy.build(item.getID());
                        if (osBuddyItem != null) {
                            value += item.getStack() * osBuddyItem.getAveragePrice();
                        }
                    }
                }
            }
            return value;
        }
        return 0;
    }

    /**
     * Gets the count of an item in the bank whose ID matches the specified ID in the bank.
     *
     * @param id The ID of the item.
     * @return The count.
     * 0 if the bank is not loaded or the specified item is null.
     */
    public static int getCount(int id) {
        return getCount(getItem(id));
    }

    /**
     * Gets the count of an item in the bank whose name matches the specified name in the bank.
     *
     * @param name The name of the item.
     * @return The count.
     * 0 if the bank is not loaded or the specified item is null.
     */
    public static int getCount(String name) {
        return getCount(getItem(name));
    }

    /**
     * Gets the count of the specified item in the bank.
     *
     * @param item The item.
     * @return The count.
     * 0 if the bank is not loaded or the specified item is null.
     */
    public static int getCount(RSItem item) {
        if (!isLoaded()) {
            return 0;
        }
        return item != null ? item.getStack() : 0;
    }

    /**
     * Gets the count of an item in the bank whose ID matches the specified ID in the bank.
     * <p>
     * Waits up to the specified amount of time while the item count is 0 before returning.
     *
     * @param time The maximum amount of time (in milliseconds) to wait before returning.
     * @param id   The ID of the item.
     * @return The count.
     * 0 if the bank is not loaded or the specified item is null.
     */
    public static int waitGetCount(long time, int id) {
        if (!isOpen()) {
            return 0;
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            int count = getCount(id);
            if (count > 0) {
                return count;
            }
            Client.sleep(10);
        }
        return 0;
    }

    /**
     * Gets the count of an item in the bank whose name matches the specified name in the bank.
     * <p>
     * Waits up to the specified amount of time while the item count is 0 before returning.
     *
     * @param time The maximum amount of time (in milliseconds) to wait before returning.
     * @param name The name of the item.
     * @return The count.
     * 0 if the bank is not loaded or the specified item is null.
     */
    public static int waitGetCount(long time, String name) {
        if (!isOpen()) {
            return 0;
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            int count = getCount(name);
            if (count > 0) {
                return count;
            }
            Client.sleep(10);
        }
        return 0;
    }

    /**
     * Gets all of the items in the bank and filters out any items that are not accepted by the specified filter.
     *
     * @param filter The filter to use when searching for items.
     * @return An array of all of the items in the bank that were accepted by the specified filter.
     * All elements in the array are guaranteed to be non-null.
     */
    public static RSItem[] find(Filter<RSItem> filter) {
        if (filter == null) {
            return new RSItem[0];
        }
        return Banking.find(filter);
    }

    /**
     * Gets all items in the bank whose ID matches one of the specified IDs.
     * <p>
     * If the bank is not open, an empty array is returned.
     *
     * @param ids The IDs of the items to look for.
     * @return An array of all of all of the items in the bank whose ID matches one of the specified IDs.
     * All elements in the array are guaranteed to be non-null.
     */
    public static RSItem[] find(int... ids) {
        return Banking.find(ids);
    }

    /**
     * Gets all items in the bank whose name matches one of the specified names.
     * <p>
     * If the bank is not open, an empty array is returned.
     *
     * @param names The names of the items to look for.
     * @return An array of all of all of the items in the bank whose name matches one of the specified names.
     * All elements in the array are guaranteed to be non-null.
     */
    public static RSItem[] find(String... names) {
        return Banking.find(names);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Methods">

    /**
     * Checks to see if the bank screen is open.
     *
     * @return True if it is open, false otherwise.
     */
    public static boolean isOpen() {
        return Banking.isBankScreenOpen();
    }

    /**
     * Checks to see if the bank is fully loaded.
     * <p>
     * The bank is considered to be loaded if the length of the array returned by #getAll() is equal to the total amount of items in the bank derived from the
     * item count interface.
     *
     * @return True if the bank is fully loaded, false otherwise.
     */
    public static boolean isLoaded() {
        if (!isOpen()) {
            return false;
        }
        RSInterfaceChild child = getInterface(ITEM_COUNT_INDEX);
        if (child != null) {
            try {
                String text = child.getText();
                if (text != null) {
                    int bankCount = Integer.parseInt(text.trim());
                    return bankCount == getAll().length;
                }
            } catch (NumberFormatException e) {
                System.out.println("Bank is not loaded...");
                return false;
            }
        }
        return false;
    }

    /**
     * Checks to see if your player is in a bank.
     *
     * @return True if your player is in a bank, false otherwise.
     */
    public static boolean isInBank() {
        return Banking.isInBank();
    }

    /**
     * Checks to see if a bank is on the screen.
     *
     * @return True if a bank is on the screen, false otherwise.
     */
    public static boolean isOnScreen() {
        return Entities.isOnScreen(Objects07.getObject("Bank booth", 12)) || Entities.isOnScreen(NPCs07.getNPC("Banker"))
                || Entities.isOnScreen(Objects07.getObject("Bank chest", 12))
                || Entities.isOnScreen(NPCs07.getNPC("Emerald Benedict"));
    }

    /**
     * Checks to see if the bank contains any item whose ID matches the specified ID.
     *
     * @param id The ID of the item.
     * @return True if the bank contains an item whose ID matches the specified ID, false otherwise.
     */
    public static boolean contains(int id) {
        return isLoaded() && getItem(id) != null;
    }

    /**
     * Checks to see if the bank contains any item whose name matches the specified name.
     *
     * @param name The name of the item.
     * @return True if the bank contains an item whose name matches specified name, false otherwise.
     */
    public static boolean contains(String name) {
        return isLoaded() && getItem(name) != null;
    }

    /**
     * Checks to see if the bank contains at least one item that matches any ID from the IDs specified.
     *
     * @param ids The IDs to check for.
     * @return True if the bank contains at least one item that matches any ID from the IDs specified, false otherwise.
     */
    public static boolean containsOneOf(int... ids) {
        if (!isLoaded()) {
            return false;
        }
        for (int id : ids) {
            if (contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the bank contains at least one item that matches any name from the names specified.
     *
     * @param names The names to check for.
     * @return True if the bank contains at least one item that matches any name from the names specified, false otherwise.
     */
    public static boolean containsOneOf(String... names) {
        if (!isLoaded()) {
            return false;
        }
        for (String name : names) {
            if (contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the bank contains any item whose ID matches the specified ID.
     * <p>
     * This method will wait up to the specified time for the bank to contain an item whose name matches the specified name to return.
     *
     * @param time The maximum amount of time to wait for the bank to contain an item whose ID matches the specified ID (in milliseconds).
     * @param id   The ID of the item.
     * @return True if the bank contains an item whose ID matches the specified ID, false otherwise.
     */
    public static boolean waitContains(long time, int id) {
        if (!isLoaded()) {
            return false;
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            if (contains(id)) {
                return true;
            }
            Client.sleep(25);
        }
        return false;
    }

    /**
     * Checks to see if the bank contains any item whose name matches the specified name.
     * <p>
     * This method will wait up to the specified time for the bank to contain an item whose name matches the specified name to return.
     *
     * @param time The maximum amount of time to wait for the bank to contain an item whose name matches the specified name (in milliseconds).
     * @param name The name of the item.
     * @return True if the bank contains an item whose name matches specified name, false otherwise.
     */
    public static boolean waitContains(long time, String name) {
        if (!isLoaded()) {
            return false;
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            if (contains(name)) {
                return true;
            }
            Client.sleep(25);
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Deposit Methods">

    /**
     * Deposits all items from your players inventory into the bank via the "Deposit inventory" button.
     * <p>
     * This method waits up to 3 seconds for the items in the inventory to be deposited after clicking the "Deposit inventory" button.
     *
     * @return True if the items were deposited successfully, false otherwise.
     * Returns true if the inventory contains no items at the time of this method call.
     */
    public static boolean depositAll() {
        if (!isOpen()) {
            return false;
        }
        if (Inventory07.isEmpty()) {
            return true;
        }
        if (Banking.depositAll() > 0) {
            return Waiting.waitUntil(Inventory07::isEmpty, 3000);
        }
        return false;
    }

    /**
     * Deposits all items that your player has equipped via the "Deposit worn items" button.
     * <p>
     * This method waits up to 3 seconds for the items that your player has equipped to be deposited into the bank after clicking the "Deposit worn items"
     * button.
     *
     * @return True if the equipped items were deposited successfully, false otherwise.
     * Returns true if the player has no items equipped at the time of this method call.
     */
    public static boolean depositEquipment() {
        if (!isOpen()) {
            return false;
        }
        if (Equipment.getItems().length < 1) {
            return true;
        }
        if (Banking.depositEquipment()) {
            return Waiting.waitUntil(() -> Equipment.getItems().length == 0, 3000);
        }
        return false;
    }

    /**
     * Deposits an item whose ID matches the specified ID into the bank.
     * <p>
     * This method waits up to 3 seconds for an item to be deposited into the bank after clicking deposit.
     *
     * @param amount The amount to deposit.
     * @param id     The ID.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(int amount, final int id) {
        RSItem[] items = Inventory07.find(id);
        if (items.length == 0) {
            return false;
        }
        return deposit(amount, items[0]);
    }

    /**
     * Deposits an item whose name matches the specified name into the bank.
     * <p>
     * This method waits up to 3 seconds for an item to be deposited into the bank after clicking deposit.
     *
     * @param amount The amount to deposit.
     * @param name   The name.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(int amount, final String name) {
        RSItem[] items = Inventory07.find(name);
        if (items.length == 0) {
            return false;
        }
        return deposit(amount, items[0]);
    }

    /**
     * Deposits all items in the inventory that have an ID that matches one of the specified IDs.
     * <p>
     * The amount at index 0 will correspond with the ID at index 0 and so forth.
     *
     * @param amounts The amount of each item to deposit.
     * @param ids     The IDs of the items to deposit.
     * @return True if the depositing each item was successful, false otherwise.
     */
    public static boolean deposit(final int[] amounts, final int[] ids) {
        if (amounts.length != ids.length) {
            Printing.err("Illegal argument, array lengths differ for depositing");
            return false;
        }
        for (int i = 0; i < ids.length; i++) {
            if (!deposit(amounts[i], ids[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deposits all items in the inventory that have a name that matches one of the specified names.
     * The amount at index 0 will correspond with the name at index 0 and so forth.
     *
     * @param amounts The amount of each item to deposit.
     * @param names   The names of the items to deposit.
     * @return True if the depositing each item was successful, false otherwise.
     */
    public static boolean deposit(final int[] amounts, final String[] names) {
        if (amounts.length != names.length) {
            Printing.err("Illegal argument, array lengths differ for depositing");
            return false;
        }
        for (int i = 0; i < names.length; i++) {
            if (!deposit(amounts[i], names[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deposits the specified amount of the specified item into the bank.
     * <p>
     * This method waits up to 3 seconds for the item to be deposited into the bank after clicking deposit.
     *
     * @param amount The amount to deposit.
     * @param item   The item to deposit.
     * @return True if the deposit was successful, false otherwise.
     * Returns false if the inventory does not contain the specified item.
     */
    public static boolean deposit(int amount, RSItem item) {
        if (!isOpen() || item == null || amount < 0) {
            return false;
        }
        final int id = item.getID();
        final int startCount = Inventory07.getCount(id);
        if (startCount == 0) {
            return false;
        }
        if (Banking.depositItem(item, amount)) {
            return Waiting.waitUntil(() -> Inventory07.getCount(id) < startCount, 3000);
        }
        return false;
    }

    /**
     * Deposits all items except any items whose IDs match the specified IDs.
     * <p>
     * This method waits up to 3 seconds for the items to be deposited into the bank after clicking deposit.
     *
     * @param ids The IDs to skip when depositing items.
     * @return True if all items except items whose IDs match the specified IDs were deposited, false otherwise.
     * Returns true if the inventory contains only items with the specified IDs at the time of this method call.
     */
    public static boolean depositAllExcept(final int... ids) {
        return depositAllExcept(Inventory07.find(ids));
    }

    /**
     * Deposits all items except any items whose names match the specified names.
     * <p>
     * This method waits up to 3 seconds for the items to be deposited into the bank after clicking deposit on the last item.
     *
     * @param names The names to skip when depositing items.
     * @return True if all items except items whose names match the specified names were deposited, false otherwise.
     * Returns true if the inventory contains only items with the specified names at the time of this method call.
     */
    public static boolean depositAllExcept(final String... names) {
        return depositAllExcept(Inventory07.find(names));
    }

    /**
     * Deposits all of the items in your inventory except the specified items.
     * <p>
     * This method waits up to 3 seconds for the items to be deposited into the bank after clicking deposit on the last item.
     *
     * @param items The items to skip when depositing.
     * @return True if all items except the specified items were deposited successfully, false otherwise.
     */
    public static boolean depositAllExcept(RSItem... items) {
        if (!isOpen()) {
            return false;
        }
        final ArrayList<Integer> idList = new ArrayList<>(items.length);
        for (RSItem item : items) {
            if (item != null) {
                final int id = item.getID();
                if (!idList.contains(id)) {
                    idList.add(id);
                }
            }
        }
        final int[] ids = ArrayUtil.toArrayInt(idList);
        if (Inventory07.containsOnly(ids)) {
            return true;
        }
        if (!Inventory07.contains(ids)) {
            return depositAll();
        }
        RSItem[] depositItems = Inventory07.filterDuplicates(Inventory07.find(Filters.Items.idNotEquals(ids)));
        for (RSItem item : depositItems) {
            int count = Inventory07.getCount(item.getID());
            String option;
            if (count == 1) {
                option = "1";
            } else if (count <= 5) {
                option = "5";
            } else if (count <= 10) {
                option = "10";
            } else {
                option = "All";
            }
            Clicking07.click("Deposit-" + option, item);
        }
        return Waiting.waitUntil(() -> Inventory07.containsOnly(ids), 3000);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Withdraw Methods">

    /**
     * Withdraws an item from the bank whose ID matches the specified ID in item form.
     * <p>
     * This method waits up to 3 seconds for the item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param id     The ID.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, int id) {
        RSItem[] items = find(id);
        if (items.length == 0) {
            return false;
        }
        return withdraw(amount, items[0]);
    }

    /**
     * Withdraws an item from the bank whose name matches the specified name in item form.
     * <p>
     * This method waits up to 3 seconds for the item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param name   The name.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, String name) {
        RSItem[] items = find(name);
        if (items.length == 0) {
            return false;
        }
        return withdraw(amount, items[0]);
    }

    /**
     * Withdraws the first item found that matches an ID from the specified IDs.
     *
     * @param amount The amount to withdraw.
     * @param ids    The IDs to search for.
     * @return True if an item was withdrawn, false otherwise.
     */
    public static boolean withdrawAny(int amount, int... ids) {
        RSItem[] items = find(ids);
        if (items.length == 0) {
            return false;
        }
        return withdraw(amount, items[0]);
    }

    /**
     * Withdraws the first item found that matches a name from the specified names.
     *
     * @param amount The amount to withdraw.
     * @param names  The names to search for.
     * @return True if an item was withdrawn, false otherwise.
     */
    public static boolean withdrawAny(int amount, String... names) {
        RSItem[] items = find(names);
        if (items.length == 0) {
            return false;
        }
        return withdraw(amount, items[0]);
    }

    /**
     * Withdraws all items in the bank that have an ID that matches one of the specified IDs in item form.
     * <p>
     * The amount at index 0 will correspond with the ID at index 0 and so forth.
     *
     * @param amounts The amount of each item to withdraw.
     * @param ids     The IDs of the items to withdraw.
     * @return True if the withdrawals were successful, false otherwise.
     */
    public static boolean withdraw(int[] amounts, int[] ids) {
        if (amounts.length != ids.length) {
            Printing.err("Illegal argument, array lengths differ for depositing");
            return false;
        }
        for (int i = 0; i < ids.length; i++) {
            if (!withdraw(amounts[i], ids[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Withdraws all items in the bank that have a name that matches one of the specified names in item form.
     * <p>
     * The amount at index 0 will correspond with the name at index 0 and so forth.
     *
     * @param amounts The amount of each item to withdraw.
     * @param names   The names of the items to withdraw.
     * @return True if the withdrawals were successful, false otherwise.
     */
    public static boolean withdraw(int[] amounts, String[] names) {
        if (amounts.length != names.length) {
            Printing.err("Illegal argument, array lengths differ for depositing");
            return false;
        }
        for (int i = 0; i < names.length; i++) {
            if (!withdraw(amounts[i], names[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Withdraws the specified item from the bank in item form.
     * <p>
     * This method waits up to 3 seconds for the item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param item   The item to withdraw.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, RSItem item) {
        if (!isOpen() || item == null) {
            return false;
        }
        final int id = item.getID();
        final int startCount = Inventory07.getCount(id);
        return Banking.withdrawItem(item, amount) && Waiting.waitUntil(() -> Inventory07.getCount(id) > startCount, 2750);
    }

    /**
     * Withdraws the noted version of an item in the bank whose ID matches the specified ID.
     *
     * @param amount The amount to withdraw.
     * @param id     The ID of the item to withdraw.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdrawNoted(int amount, int id) {
        return withdrawNoted(amount, getItem(id));
    }

    /**
     * Withdraws the noted version of an item in the bank whose name matches the specified name.
     *
     * @param amount The amount to withdraw.
     * @param name   The name of the item to withdraw.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdrawNoted(int amount, String name) {
        return withdrawNoted(amount, getItem(name));
    }

    /**
     * Withdraws all items in noted form in the bank that have an ID that matches one of the specified IDs.
     * <p>
     * The amount at index 0 will correspond with the ID at index 0 and so forth.
     *
     * @param amounts The amount of each item to withdraw.
     * @param ids     The IDs of the items to withdraw.
     * @return True if the withdrawals were successful, false otherwise.
     */
    public static boolean withdrawNoted(int[] amounts, int[] ids) {
        if (amounts.length != ids.length) {
            Printing.err("Illegal argument, array lengths differ for depositing");
            return false;
        }
        for (int i = 0; i < ids.length; i++) {
            if (!withdrawNoted(amounts[i], ids[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Withdraws all items in noted form in the bank that have a name that matches one of the specified names.
     * <p>
     * The amount at index 0 will correspond with the name at index 0 and so forth.
     *
     * @param amounts The amount of each item to withdraw.
     * @param names   The names of the items to withdraw.
     * @return True if the withdrawals were successful, false otherwise.
     */
    public static boolean withdrawNoted(int[] amounts, String[] names) {
        if (amounts.length != names.length) {
            Printing.err("Illegal argument, array lengths differ for depositing");
            return false;
        }
        for (int i = 0; i < names.length; i++) {
            if (!withdrawNoted(amounts[i], names[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Withdraws the noted version of the specified item from the bank.
     * <p>
     * This method waits up to 3 seconds for the item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param item   The item to withdraw.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdrawNoted(int amount, RSItem item) {
        if (!isOpen() || item == null) {
            return false;
        }
        final int id = item.getID() + 1; //TODO: Change to getNotedItemID when it is fixed.
        final int startCount = Inventory07.getCount(id);
        if (BankButton.NOTE.select() && Banking.withdrawItem(item, amount)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(25);
                    return Inventory07.getCount(id) > startCount;
                }
            }, 3000);
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Misc Methods">

    /**
     * Opens the bank. Supports booths, NPC's with the option "Bank", and bank chests.
     * This method uses anti-ban compliance to decide if the bank will be opened via an NPC or a booth.
     * This method waits up to 5 seconds for the bank to be open after clicking the booth/NPC/chest.
     *
     * @return True if the bank was opened successfully or the bank was already open prior to this method call, false otherwise.
     */
    public static boolean open() {
        if (isOpen()) {
            return true;
        }
        if (!Banking.openBank()) {
            final OpenBankPreference preference = AntiBan.getABCUtil().generateOpenBankPreference();
            switch (preference) {
                case BANKER:
                    openBankBanker();
                    break;
                case BOOTH:
                    openBankBooth();
                    break;
                default:
                    throw new RuntimeException("Unhandled open bank preference.");
            }
        }
        return false;
    }

    /**
     * Opens the bank via the banker NPC if there is one.
     *
     * @return True if the bank was opened, false otherwise.
     */
    public static boolean openBankBanker() {
        return Banking.openBankBanker();
    }

    /**
     * Opens the bank via the bank booth if there is one.
     *
     * @return True if the bank was opened, false otherwise.
     */
    public static boolean openBankBooth() {
        return Banking.openBankBooth();
    }

    /**
     * Closes the bank.
     * This method waits up to 5 seconds for the bank to be closed after clicking the "Close" button.
     *
     * @return True if the bank was closed, false otherwise.
     */
    public static boolean close() {
        if (!isOpen()) {
            return true;
        }
        return Banking.close();
    }

    /**
     * Searches the specified string in the bank.
     *
     * @param string The string to search.
     */
    public static void search(String string) {
        if (!isOpen()) {
            return;
        }
        if (Interfaces07.isHidden(Interfaces07.get("Show items whose names contain the following text:"))) {
            if (Clicking.click(getInterface(SEARCH_INDEX))) {
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Client.sleep(25);
                        return !Interfaces07.isHidden(Interfaces07.get("Show items whose names contain the following text:"));
                    }
                }, 2000);
            }
        }
        if (!Interfaces07.isHidden(Interfaces07.get("Show items whose names contain the following text:"))) {
            Keyboard.typeString(string);
        }
    }
    //</editor-fold>

    public enum BankButton {

        INSERT(18),
        ITEM(21),
        NOTE(23),
        SWAP(16);

        private final int SELECTED_TEXTURE_ID = 1150;

        final int childIndex;

        /**
         * Constructs a new BankButton.
         *
         * @param childIndex The child interface index of the button in the bank master.
         */
        BankButton(int childIndex) {
            this.childIndex = childIndex;
        }

        /**
         * Gets the child index of the bank button.
         *
         * @return The child index.
         */
        public int getChildIndex() {
            return this.childIndex;
        }

        /**
         * Gets the interface that corresponds the bank button.
         *
         * @return The interface.
         */
        public RSInterfaceChild getInterface() {
            return Bank.getInterface(getChildIndex());
        }

        /**
         * Checks to see if the bank button is selected.
         *
         * @return True if it is selected, false otherwise.
         */
        public boolean isSelected() {
            RSInterfaceChild child = getInterface();
            if (child != null) {
                RSInterfaceComponent component = child.getChild(0);
                return component != null && component.getTextureID() == SELECTED_TEXTURE_ID;
            }
            return false;
        }

        /**
         * Selects the bank button.
         * This method waits up to 1 second for the button to be selected before returning.
         *
         * @return True if the selection was successful, false otherwise.
         */
        public boolean select() {
            if (isSelected()) {
                return true;
            }
            RSInterfaceChild child = getInterface();
            if (child != null) {
                if (Clicking.click(child)) {
                    return Waiting.waitUntil(() -> {
                        Client.sleep(25);
                        return isSelected();
                    }, 1000);
                }
            }
            return false;
        }
    }
}
