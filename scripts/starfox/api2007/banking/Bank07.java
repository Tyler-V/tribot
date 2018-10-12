package scripts.starfox.api2007.banking;

import java.util.ArrayList;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api.Printing;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Game07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.NPCs07;
import scripts.starfox.api2007.entities.Objects07;
import scripts.starfox.api2007.login.Login07;
import scripts.starfox.api2007.walking.Walking07;
import scripts.starfox.api2007.walking.pathfinding.AStarCache;

/**
 * The Bank07 class is an extension of the TRiBot Banking class. It contains vastly improved methods and contains new methods that the Banking class does not
 * have.
 *
 * @author Nolan
 */
public final class Bank07 {

    private static final BankCache cache;

    static {
        cache = new BankCache();
    }

    private Bank07() {
    }

    //<editor-fold defaultstate="collapsed" desc="Cache Methods">
    /**
     * Caches the bank.
     */
    private static void cache() {
        if (!isOpen() || !Login07.isLoggedIn() || !Game07.isGameLoaded()) {
            return;
        }
        RSItem[] items = Banking.getAll();
        if (items.length < 1) {
            return;
        }
        cache.reset(items);
    }

    /**
     * Checks to see if the bank cache contains any item(s) with the specified id.
     *
     * @param id The id.
     * @return True if the cache contains the id, false otherwise.
     */
    private static boolean cacheContains(int id) {
        return cache.contains(id);
    }

    /**
     * Checks to see if the bank cache contains any item(s) with the specified name.
     *
     * @param name The name.
     * @return True if the cache contains the name, false otherwise.
     */
    private static boolean cacheContains(String name) {
        return cache.contains(name);
    }

    /**
     * Checks to see if the bank cache is empty.
     *
     * @return True if it is empty, false otherwise.
     */
    private static boolean isCacheEmpty() {
        return cache.isEmpty();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Contains Methods">
    /**
     * Checks to see if the bank contains any item whose ID matches the specified ID.
     *
     * This method will wait up to the specified time for the bank to contain an item whose name matches the specified name to return.
     *
     * @param time The maximum amount of time to wait for the bank to contain an item whose ID matches the specified ID (in milliseconds).
     * @param id   The ID of the item.
     * @return True if the bank contains an item whose ID matches the specified ID, false otherwise.
     */
    public static boolean waitContains(long time, int id) {
        if (!isOpen()) {
            return !isCacheEmpty() ? cacheContains(id) : false;
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            Client.sleep(20);
            if (contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the bank contains any item whose name matches the specified name.
     *
     * This method will wait up to the specified time for the bank to contain an item whose name matches the specified name to return.
     *
     * @param time The maximum amount of time to wait for the bank to contain an item whose name matches the specified name (in milliseconds).
     * @param name The name of the item.
     * @return True if the bank contains an item whose name matches specified name, false otherwise.
     */
    public static boolean waitContains(long time, String name) {
        if (!isOpen()) {
            return !isCacheEmpty() ? cacheContains(name) : false;
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            Client.sleep(20);
            if (contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the bank contains any item whose ID matches the specified ID.
     *
     * This method could take up to 5 seconds to return depending on whether or not the items in the bank have not yet been loaded.
     *
     * @param id The ID of the item.
     * @return True if the bank contains an item whose ID matches the specified ID, false otherwise.
     */
    public static boolean contains(int id) {
        if (!isOpen()) {
            return !isCacheEmpty() ? cacheContains(id) : false;
        }
        Timer t = new Timer(5000);
        t.start();
        while (!t.timedOut()) {
            Client.sleep(20);
            cache();
            if (!isCacheEmpty() && cacheContains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the bank contains any item whose name matches the specified name.
     *
     * This method could take up to 5 seconds to return depending on whether or not the items in the bank have not yet been loaded.
     *
     * @param name The name of the item.
     * @return True if the bank contains an item whose name matches specified name, false otherwise.
     */
    public static boolean contains(String name) {
        Client.validate();
        if (!isOpen()) {
            return !isCacheEmpty() ? cacheContains(name) : false;
        }
        Timer t = new Timer(5000);
        t.start();
        while (!t.timedOut()) {
            Client.sleep(25);
            cache();
            if (!isCacheEmpty() && cacheContains(name)) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Bank07 Item Methods">
    /**
     * Gets all of the items in the bank.
     *
     * If the "View all items" bank tab is not selected, it will be selected.
     * If the bank is not open, an empty array is returned.
     *
     * @return An array of all of the items in the bank. All elements in the array will be non-null.
     */
    public static RSItem[] getAll() {
        Client.validate();
        return Banking.getAll();
    }

    /**
     * Gets all of the items in the bank and filters out any items that are not accepted by the specified filter.
     *
     * @param filter The filter to use when searching for items.
     * @return An array of all of the items in the bank that were accepted by the specified filter. All elements in the array will be non-null.
     */
    public static RSItem[] find(Filter<RSItem> filter) {
        if (filter == null) {
            return new RSItem[0];
        }
        return Banking.find(filter);
    }

    /**
     * Gets all items in the bank whose ID matches one of the specified IDs.
     *
     * If the bank is not open, an empty array is returned.
     *
     * @param ids The IDs of the items to look for.
     * @return An array of all of all of the items in the bank whose ID matches one of the specified IDs. All elements in the array will be non-null.
     */
    public static RSItem[] find(int... ids) {
        return Banking.find(ids);
    }

    /**
     * Gets all items in the bank whose name matches one of the specified names.
     *
     * If the bank is not open, an empty array is returned.
     *
     * @param names The names of the items to look for.
     * @return An array of all of all of the items in the bank whose name matches one of the specified names. All elements in the array will be non-null.
     */
    public static RSItem[] find(String... names) {
        return Banking.find(names);
    }

    /**
     * Gets all items in the bank whose ID matches one of the specified IDs.
     *
     * Waits up to the specified amount of time before returning.
     *
     * @param time The maximum amount of time to wait for.
     * @param ids  The IDs to look for.
     * @return The items.
     */
    public static RSItem[] waitFind(long time, int... ids) {
        if (!isOpen()) {
            return new RSItem[0];
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            RSItem[] items = find(ids);
            if (items.length > 0) {
                return items;
            }
        }
        return new RSItem[0];
    }

    /**
     * Gets all items in the bank whose name matches one of the specified names.
     *
     * Waits up to the specified amount of time before returning.
     *
     * @param time  The maximum amount of time to wait for.
     * @param names The names to look for.
     * @return The items.
     */
    public static RSItem[] waitFind(long time, String... names) {
        if (!isOpen()) {
            return new RSItem[0];
        }
        Timer t = new Timer(time);
        t.start();
        while (!t.timedOut()) {
            RSItem[] items = find(names);
            if (items.length > 0) {
                return items;
            }
        }
        return new RSItem[0];
    }

    /**
     * Gets all items in the bank whose ID matches any of the specified IDs.
     *
     * You can pass noted item IDs into this method and it will get the non-noted version of the item in the bank.
     *
     * @param ids The IDs being searched for.
     * @return The items.
     */
    public static RSItem[] findNoted(int... ids) {
        RSItem[] items = Banking.getAll();
        ArrayList<RSItem> rItems = new ArrayList<>();
        for (RSItem item : items) {
            RSItemDefinition def = item.getDefinition();
            if (ArrayUtil.contains(item.getID(), ids) || (def != null && !def.isStackable() && ArrayUtil.contains(item.getID() + 1, ids))) {
                rItems.add(item);
            }
        }
        return rItems.toArray(new RSItem[0]);
    }

    /**
     * Gets an item in the bank whose ID matches the specified ID.
     *
     * @param id The ID of the item.
     * @return An item. Null if no item was found or the bank was not open at the time of the method call.
     */
    public static RSItem getItem(int id) {
        if (!isOpen()) {
            return null;
        }
        cache();
        for (RSItem item : Banking.getAll()) {
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
     * @return An item. Null if no item was found or the bank was not open at the time of the method call.
     */
    public static RSItem getItem(String name) {
        if (!isOpen()) {
            return null;
        }
        cache();
        for (RSItem item : Banking.getAll()) {
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
     * Gets the count of an item in the bank whose ID matches the specified ID in the bank.
     *
     * @param id The ID of the item.
     * @return The count. -1 if the bank is not open.
     */
    public static int getCount(int id) {
        return getCount(getItem(id));
    }

    /**
     * Gets the count of an item in the bank whose name matches the specified name in the bank.
     *
     * @param name The name of the item.
     * @return The count. -1 if the bank is not open.
     */
    public static int getCount(String name) {
        return getCount(getItem(name));
    }

    /**
     * Gets the count of the specified item in the bank.
     *
     * @param item The item.
     * @return The count. -1 if the bank is not open.
     */
    public static int getCount(RSItem item) {
        if (!isOpen()) {
            return 0;
        }
        cache();
        return item != null ? item.getStack() : 0;
    }

    /**
     * Checks whether the item represented by the specified id is stackable or not.
     *
     * @param id The id.
     * @return True if the item represented by the specified id is stackable, false otherwise.
     */
    public static boolean isStackable(int id) {
        RSItem[] bItems = Banking.find(id);
        RSItem bItem = bItems != null && bItems.length != 0 ? bItems[0] : null;
        if (bItem == null) {
            RSItem[] iItems = Inventory07.find(id);
            RSItem iItem = bItems != null && iItems.length != 0 ? iItems[0] : null;
            return isStackable(iItem);
        } else {
            return isStackable(bItem);
        }
    }

    /**
     * Checks whether the specified item is stackable or not.
     *
     * @param item The item.
     * @return True if the specified item is stackable, false otherwise.
     */
    public static boolean isStackable(RSItem item) {
        if (item == null) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            RSItemDefinition def = item.getDefinition();
            if (def != null) {
                return def.isStackable();
            }
        }
        Client.println("Item Def returned null.");
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Deposit Methods">
    /**
     * Deposits all items from your players inventory into the bank via the "Deposit inventory" button.
     *
     * This method waits up to 3 seconds for the items in the inventory to be deposited after clicking the "Deposit inventory" button.
     *
     * @return True if there are no items in your inventory at the end of execution, false otherwise.
     */
    public static boolean depositAll() {
        if (!isOpen()) {
            return false;
        }
        cache();
        if (Inventory07.isEmpty()) {
            return true;
        }
        if (Banking.depositAll() > 0) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(25);
                    return Inventory07.isEmpty();
                }
            }, 3000);
        }
        return false;
    }

    /**
     * Deposits all items that your player has equipped via the "Deposit worn items" button.
     *
     * This method waits up to 3 seconds for the items that your player has equipped to be deposited into the bank after clicking the "Deposit worn items"
     * button.
     *
     * @return True if the equipped items were deposited, false otherwise.
     */
    public static boolean depositEquipment() {
        if (!isOpen()) {
            return false;
        }
        cache();
        if (Equipment.getItems().length < 1) {
            return false;
        }
        if (Banking.depositEquipment()) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(25);
                    return Equipment.getItems().length == 0;
                }
            }, 3000);
        }
        return false;
    }

    /**
     * Deposits an item whose ID matches one of the specified IDs.
     *
     * This method waits up to 3 seconds for an item to be deposited into the bank after clicking deposit.
     *
     * @param amount The amount to deposit.
     * @param ids    The IDs.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(int amount, final int... ids) {
        RSItem[] items = Inventory07.find(ids);
        if (items.length < 1) {
            return false;
        }
        return deposit(amount, items[0]);
    }

    /**
     * Deposits an item whose name matches one of the specified names.
     *
     * This method waits up to 3 seconds for an item to be deposited into the bank after clicking deposit.
     *
     * @param amount The amount to deposit.
     * @param names  The names.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(int amount, final String... names) {
        RSItem[] items = Inventory07.find(names);
        if (items.length < 1) {
            return false;
        }
        return deposit(amount, items[0]);
    }

    /**
     * Deposits the specified item into the bank.
     *
     * This method waits up to 3 seconds for the item to be deposited into the bank after clicking deposit.
     *
     * @param amount The amount to deposit.
     * @param item   The item to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(int amount, RSItem item) {
        if (!isOpen() || item == null) {
            return false;
        }
        cache();
        final int id = item.getID();
        final int startCount = Inventory07.getCount(id);
        if (startCount < 1) {
            return false;
        }
        if (Banking.depositItem(item, amount)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(100);
                    return Inventory07.getCount(id) < startCount;
                }
            }, 3000);
        }
        return false;
    }

    /**
     * Deposits all items except any items whose IDs match the specified IDs.
     *
     * This method waits up to 3 seconds for the items to be deposited into the bank after clicking deposit.
     *
     * @param ids The IDs to skip when depositing items.
     * @return True if all items except items whose IDs match the specified IDs were deposited, false otherwise.
     */
    public static boolean depositAllExcept(final int... ids) {
        if (!isOpen()) {
            return false;
        }
        cache();
        if (Inventory07.containsOnly(ids)) {
            return false;
        }
        if (!Inventory07.contains(ids)) {
            return depositAll();
        }
        RSItem[] items = Inventory07.filterDuplicates(Inventory07.find(Filters.Items.idNotEquals(ids)));
        for (RSItem item : items) {
            int count = Inventory07.getCount(item.getID());
            String option;
            if (count == 1) {
                option = "1";
            } else if (count == 5) {
                option = "5";
            } else if (count == 10) {
                option = "10";
            } else {
                option = "All";
            }
            if (Clicking.click("Deposit-" + option, item)) {
                AntiBan.sleep(General.random(2, 4));
            }
        }
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return Inventory07.containsOnly(ids);
            }
        }, 3500);
    }

    /**
     * Deposits all items except any items whose names match the specified names.
     *
     * This method waits up to 3 seconds for the items to be deposited into the bank after clicking deposit on the last item.
     *
     * @param names The names to skip when depositing items.
     * @return True if all items except items whose names match the specified names were deposited, false otherwise.
     */
    public static boolean depositAllExcept(final String... names) {
        if (!isOpen()) {
            return false;
        }
        cache();
        if (Inventory07.containsOnly(names)) {
            return false;
        }
        if (!Inventory07.contains(names)) {
            return depositAll();
        }
        RSItem[] items = Inventory07.filterDuplicates(Inventory07.find(Filters.Items.nameNotEquals(names)));
        for (RSItem item : items) {
            int count = Inventory07.getCount(item.getID());
            String option;
            if (count == 1) {
                option = "1";
            } else if (count == 5) {
                option = "5";
            } else if (count == 10) {
                option = "10";
            } else {
                option = "All";
            }
            if (Clicking.click("Deposit-" + option, item)) {
                AntiBan.sleep(General.random(2, 4));
            }
        }
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return Inventory07.containsOnly(names);
            }
        }, 3500);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Withdraw Methods">
    /**
     * Clicks the appropriate button for either withdrawing noted or regular items. Returns true if the desired withdraw type is selected at the end of the
     * method (even if it was
     * already selected), false otherwise.
     *
     * @param note True if the bank withdraw setting should be set to note, false if the bank withdraw setting should be set to regular.
     * @return True if the desired withdraw type is selected at the end of the method (even if it was already selected), false otherwise.
     */
    public static boolean changeItemWithdrawMethod(boolean note) {
        Client.validate();
        final RSInterfaceChild button = note ? getBankNotedButton() : getBankItemButton();
        if (Screen.getColorAt(350, 320).getRed() > 100 != note) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return Clicking.click(button);
                }
            }, 1000);
        }
        return true;
    }

    /**
     * Withdraws an item from the bank whose ID matches one of the specified IDs.
     *
     * This method waits up to 3.5 seconds for an item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param ids    The IDs.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, int... ids) {
        RSItem[] items = find(ids);
        if (items == null || items.length < 1) {
            return false;
        }
        return withdraw(amount, items[0], shouldNote(ids));
    }

    /**
     * Withdraws an item from the bank whose name matches one of the specified names.
     *
     * This method waits up to 3.5 seconds for an item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param names  The names.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, String... names) {
        RSItem[] items = Banking.find(names);
        if (items.length < 1) {
            return false;
        }
        return withdraw(amount, items[0]);
    }
    
    /**
     * Checks if the specified IDs should be withdrawn as notes.
     *
     * @param ids The IDs.
     * @return True if the specified IDs should be withdrawn as notes, false otherwise.
     */
    private static boolean shouldNote(int... ids) {
        return findNoted(ids).length != Banking.find(ids).length;
    }

    /**
     * Withdraws the specified item from the bank using the current state of the withdraw method.
     *
     * This method waits up to 3.5 seconds for the item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param item   The item to withdraw.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, RSItem item) {
        if (!isOpen() || item == null) {
            return false;
        }
        cache();
        final int id = item.getID();
        final int startCount = Inventory07.getCount(id);
        if (Banking.withdrawItem(item, amount)) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    Client.sleep(25);
                    return Inventory07.getCount(id) > startCount;
                }
            }, 3500);
        }
        return false;
    }

    /**
     * Withdraws the specified item from the bank.
     *
     * This method waits up to 3.5 seconds for the item withdrawn to be in the inventory after clicking withdraw.
     *
     * @param amount The amount to withdraw.
     * @param item   The item to withdraw.
     * @param noted  True if the item should be withdrawn as a note, false otherwise.
     * @return True if the withdrawal was successful, false otherwise.
     */
    public static boolean withdraw(int amount, RSItem item, boolean noted) {
        if (!isOpen() || item == null) {
            return false;
        }
        RSItemDefinition def = item.getDefinition();
        if (def != null && noted && def.isStackable()) {
            Printing.warn("Attempting to withdraw stackable item as a note, which isn't possible. Trying anyways.");
        }
        cache();
        final int id = (noted ? item.getID() + 1 : item.getID());
        final int startCount = Inventory07.getCount(id);
        if (changeItemWithdrawMethod(noted)) {
            if (Banking.withdrawItem(item, amount)) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Client.sleep(25);
                        return Inventory07.getCount(id) > startCount;
                    }
                }, 3500);
            }
        } else {
            Printing.warn("Could not change withdraw method.");
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Bank07 Object Methods">
    /**
     * Checks to see if the bank is open.
     *
     * @return True if the bank is open, false otherwise.
     */
    public static boolean isOpen() {
        Client.validate();
        return Banking.isBankScreenOpen();
    }

    /**
     * Checks to see if your player is in a bank.
     *
     * @return True if your player is in a bank, false otherwise.
     */
    public static boolean isInBank() {
        Client.validate();
        return Banking.isInBank();
    }

    /**
     * Checks to see if a bank is on the screen.
     *
     * @return True if a bank is on the screen, false otherwise.
     */
    public static boolean isOnScreen() {
        Client.validate();
        return Entities.isCenterOnScreen(Objects07.getObject("Bank", 10)) || Entities.isCenterOnScreen(NPCs07.getNPC("Banker"));
    }

    /**
     * Closes the bank.
     *
     * This method waits up to 5 seconds for the bank to be closed after clicking the "Close" button.
     *
     * @return True if the bank was closed, false otherwise.
     */
    public static boolean close() {
        Client.validate();
        if (!isOpen()) {
            return true;
        }
        cache();
        return Banking.close();
    }

    /**
     * Opens the bank. Supports booths, NPC's with the option "Bank07", and bank chests.

     * This method uses anti-ban compliance to decide if the bank will be opened via an NPC or a booth.
     *
     * This method waits up to 5 seconds for the bank to be open after clicking the booth/NPC/chest.
     *
     * @return True if the bank was opened successfully or the bank was already open prior to this method call, false otherwise.
     */
    public static boolean open() {
        if (isOpen()) {
            cache();
            return true;
        }
        if (Banking.openBank()) {
            cache();
            return true;
        }
        return false;
    }

    /**
     * Attempts to walk to and then open the nearest bank. Uses webwalking if standard pathfinding fails.
     *
     * @return True if the walking was successful, false otherwise.
     */
    public static boolean walkTo() {
        Client.validate();
        if (isOpen()) {
            return true;
        }
        final RSTile bankTile = getNearestTile(200);
        RSTile pP = Player07.getPosition();
        if (pP == null) {
            return false;
        }
        if (bankTile == null || pP.getPlane() != Game.getPlane()) {
            Walking.setWalkingTimeout(1000);
            WebWalking.setUseAStar(true);
            Client.println("Web walking to bank.");
            return WebWalking.walkToBank();
        } else {
            if (Entities.distanceTo(bankTile) < 6 && Entities.aStarDistanceTo(bankTile) < 6 && PathFinding.canReach(bankTile, true)) {
                return true;
            } else {
                return Walking07.aStarWalk(bankTile, new Condition() {
                    @Override
                    public boolean active() {
                        Client.sleep(50);
                        return Entities.distanceTo(bankTile) < 6 && Entities.aStarDistanceTo(bankTile) < 6 && PathFinding.canReach(bankTile, true);
                    }
                });
            }
        }
    }

    /**
     * Returns the nearest bank.
     *
     * @see #getNearestTile()
     * @return The nearest bank.
     */
    public static RSObject getNearest() {
        Client.validate();
        return Objects07.getObject(ArrayUtil.getAsArray("Bank"), ArrayUtil.getAsArray("Bank", "Use"), 104);
    }

    /**
     * Gets the nearest bank tile.
     *
     * This method will check both cached banks and currently loaded banks. The bank that is closest via the distance formula is the bank that is returned, or
     * null if no cached or
     * loaded banks could be found.
     *
     * @return The nearest bank tile.
     */
    public static RSTile getNearestTile() {
        return getNearestTile(-1);
    }

    /**
     * Gets the nearest bank tile.
     *
     * This method will check both cached banks and currently loaded banks. The bank that is closest via the distance formula is the bank that is returned, or
     * null if no cached or
     * loaded banks could be found.
     *
     * @param cacheLimit In order for a cached bank to be returned, it must be within this range. -1 indicates an unlimited range.
     * @return The nearest bank tile.
     */
    public static RSTile getNearestTile(int cacheLimit) {
        final RSObject nearestLoaded = getNearest();
        final RSTile nearestCached = getNearestCached();
        if (nearestLoaded != null) {
            if (nearestCached != null) {
                final int cachedDistance = (int) Entities.distanceTo(nearestCached);
                final int loadedDistance = (int) Entities.distanceTo(nearestLoaded);
                return (cacheLimit == -1 || cachedDistance <= cacheLimit) && cachedDistance <= loadedDistance ? nearestCached : nearestLoaded.getPosition();
            } else {
                return nearestLoaded.getPosition();
            }
        } else {
            return nearestCached;
        }
    }

    /**
     * Gets the nearest bank that has been previously cached.
     *
     * @return The nearest bank that has been previously cached.
     */
    public static RSTile getNearestCached() {
        ArrayList<RSTile> tiles = AStarCache.getBanks();
        if (tiles != null) {
            RSTile[] tiles2 = new RSTile[32];
            return (RSTile) Entities.getNearest(tiles2);
        } else {
            return null;
        }
    }

    /**
     * Checks to see if there is a bank on the specified tile. Does not check for bankers or deposit boxes.
     *
     * @param tile The tile being checked.
     * @return True if there is a bank at the specified tile, false otherwise.
     */
    public static boolean isBankAtTile(RSTile tile) {
        return Objects07.getObjectAt(tile, ArrayUtil.getAsArray("Bank"), ArrayUtil.getAsArray("Bank", "Use")) != null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Helper Methods">
    /**
     * Gets the bank item button.
     *
     * @return The bank item button
     */
    private static RSInterfaceChild getBankItemButton() {
        Client.validate();
        return Interfaces.get(12, 20);
    }

    /**
     * Gets the bank noted button.
     *
     * @return The bank noted button.
     */
    private static RSInterfaceChild getBankNotedButton() {
        Client.validate();
        return Interfaces.get(12, 22);
    }
    //</editor-fold>
}
