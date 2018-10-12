package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.walking.Walking07;

import java.awt.*;
import java.util.ArrayList;

/**
 * The Shop class allows you to retrieve information about shops and interact with shops.
 * Note that this class will not allow you to interact with all shops in Old-School RuneScape; it is meant to interact with generic shops.
 *
 * @author Nolan
 */
public class Shop {

    /**
     * A constant representing the generic shop interface master index.
     */
    private static final int SHOP_MASTER_INDEX = 300;

    /**
     * A constant representing the generic shop name interface child index.
     */
    private static final int SHOP_NAME_INDEX = 76;

    /**
     * A constant representing the generic shop item interface child index.
     */
    private static final int SHOP_ITEMS_INDEX = 75;

    /**
     * Prevent instantiation of this class.
     */
    private Shop() {

    }

    /**
     * Checks to see whether or not a shop is open.
     *
     * @return True if a shop is open, false otherwise.
     */
    public static boolean isOpen() {
        return Interfaces07.isUp(SHOP_MASTER_INDEX);
    }

    /**
     * Gets the name of shop that is currently open.
     *
     * @return The name of the shop that is currently open.
     * An empty string is returned if no shop is open.
     */
    public static String getShopName() {
        if (!isOpen()) {
            return "";
        }
        final RSInterface shopNameChild = Interfaces.get(SHOP_MASTER_INDEX, SHOP_NAME_INDEX);
        if (shopNameChild != null) {
            String name = shopNameChild.getText();
            return name != null ? name : "";
        }
        return "";
    }

    /**
     * Attempts to open a shop.
     * If there are no NPC's near you that have the action "Trade", this method will immediately return false.
     *
     * @return True if a shop was opened, false otherwise.
     */
    public static boolean open() {
        final RSNPC[] npcs = NPCs.findNearest(Filters.NPCs.actionsContains("Trade"));
        if (npcs.length > 0) {
            if (Entities.isOnScreen(npcs)) {
                if (Clicking.click("Trade", npcs)) {
                    return Waiting.waitMoveCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return isOpen();
                        }
                    }, 3000);
                }
            } else {
                Walking07.aStarWalk(npcs[0].getPosition(), new Condition() {
                    @Override
                    public boolean active() {
                        return Entities.isOnScreen(npcs);
                    }
                });
            }
        }
        return false;
    }

    /**
     * Closes the shop that is currently open.
     * If no shop is open, this method will immediately return true.
     *
     * @return True if a shop was closed successfully, false otherwise.
     */
    public static boolean close() {
        return !isOpen() || Interfaces07.closeAll();
    }

    /**
     * Gets all of the items in the shop.
     *
     * @return The items in the shop.
     * An empty array is returned if no shop is currently open.
     */
    public static RSItem[] getItems() {
        RSItem[] empty = new RSItem[0];
        if (!isOpen()) {
            return empty;
        }
        final ArrayList<RSItem> items = new ArrayList<>();
        final RSInterface shopInterface = Interfaces.get(300, 2);
        if (shopInterface != null) {
            final RSInterface[] itemChildren = shopInterface.getChildren();
            if (itemChildren != null) {
                for (final RSInterface itemChild : itemChildren) {
                    if (itemChild.getComponentItem() != 6512) {
                        final RSItem item = new RSItem(itemChild.getComponentIndex(), itemChild.getComponentItem(),
                                itemChild.getComponentStack(), RSItem.TYPE.OTHER);
                        item.setArea(itemChild.getAbsoluteBounds());
                        items.add(item);
                    }
                }
                if (!items.isEmpty()) {
                    return items.toArray(new RSItem[items.size()]);
                }
            }
        }
        return empty;
    }

    /**
     * Finds all of the items in the shop that is currently open that match the specified name(s).
     *
     * @param names The name(s).
     * @return The items.
     * An empty array is returned if no shop is open.
     */
    public static RSItem[] find(String... names) {
        ArrayList<RSItem> items = new ArrayList<>();
        for (RSItem item : getItems()) {
            RSItemDefinition definition = item.getDefinition();
            if (definition != null) {
                String name = definition.getName();
                if (name != null && ArrayUtil.contains(name, names)) {
                    items.add(item);
                }
            }
        }
        return items.toArray(new RSItem[items.size()]);
    }

    /**
     * Finds all of the items in the shop that is currently open that match the specified ID(s).
     *
     * @param ids The ID(s).
     * @return The items.
     * An empty array is returned if no shop is open.
     */
    public static RSItem[] find(int... ids) {
        ArrayList<RSItem> items = new ArrayList<>();
        for (RSItem item : getItems()) {
            if (ArrayUtil.contains(item.getID(), ids)) {
                items.add(item);
            }
        }
        return items.toArray(new RSItem[items.size()]);
    }

    /**
     * Gets the stock of items in the currently open shop that match the specified name(s).
     *
     * @param names The name(s).
     * @return The stock of items.
     */
    public static int getStock(String... names) {
        int stock = 0;
        for (RSItem item : find(names)) {
            stock += item.getStack();
        }
        return stock;
    }

    /**
     * Gets the stock of items in the currently open shop that match the specified ID(s).
     *
     * @param ids The ID(s).
     * @return The stock of items.
     */
    public static int getStock(int... ids) {
        int stock = 0;
        for (RSItem item : find(ids)) {
            stock += item.getStack();
        }
        return stock;
    }

    /**
     * Buys the specified amount of items whose name matches the specified name.
     * If the amount of items in stock are less than the amount specified, this method will buy the remaining stock.
     *
     * @param amount The amount to buy.
     * @param name   The name of the item.
     * @return True if the buying was successful, false otherwise.
     */
    public static boolean buy(int amount, final String name) {
        if (amount <= 0) {
            return true;
        }
        for (RSItem item : find(name)) {
            if (item.getStack() > 0) {
                String option = "Buy ";
                if (amount >= 10) {
                    option += "10";
                } else if (amount >= 5) {
                    option += "5";
                } else {
                    option += "1";
                }
                final int prevCount = Inventory07.getCount(name);
                if (Clicking.click(option, item)) {
                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return Inventory07.getCount(name) > prevCount;
                        }
                    }, 4000)) {
                        return buy(amount -= (Inventory.getCount(name) - prevCount), name);
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Buys the specified amount of items whose id matches the specified id.
     * If the amount of items in stock are less than the amount specified, this method will buy the remaining stock.
     *
     * @param amount The amount to buy.
     * @param id     The id of the item.
     * @return True if the buying was successful, false otherwise.
     */
    public static boolean buy(int amount, final int id) {
        if (amount <= 0) {
            return true;
        }
        for (RSItem item : find(id)) {
            if (item.getStack() > 0) {
                String option = "Buy ";
                if (amount >= 10) {
                    option += "10";
                } else if (amount >= 5) {
                    option += "5";
                } else {
                    option += "1";
                }
                final int prevCount = Inventory07.getCount(id);
                if (Clicking.click(option, item)) {
                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return Inventory07.getCount(id) > prevCount;
                        }
                    }, 4000)) {
                        return buy(amount -= (Inventory.getCount(id) - prevCount), id);
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Sells the specified amount of items whose name matches the specified name.
     * If the amount of items in the inventory are less than the amount specified, this method will sell the remaining amount of items in your inventory.
     *
     * @param amount The amount to sell.
     * @param name   The name of the item.
     * @return True if the selling was successful, false otherwise.
     */
    public static boolean sell(int amount, final String name) {
        if (amount <= 0) {
            return true;
        }
        RSItem[] items = Inventory.find(name);
        if (items.length > 0) {
            String option = "Sell ";
            if (amount >= 10) {
                option += "10";
            } else if (amount >= 5) {
                option += "5";
            } else {
                option += "1";
            }
            final int prevCount = Inventory07.getCount(name);
            if (Clicking.click(option, items)) {
                if (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return Inventory07.getCount(name) < prevCount;
                    }
                }, 4000)) {
                    return sell(amount -= (prevCount - Inventory.getCount(name)), name);
                }
            }
        }
        return false;
    }

    /**
     * Sells the specified amount of items whose id matches the specified id.
     * If the amount of items in your inventory are less than the amount specified, this method will sell the remaining amount of items in your inventory.
     *
     * @param amount The amount to sell.
     * @param id     The id of the item.
     * @return True if the selling was successful, false otherwise.
     */
    public static boolean sell(int amount, final int id) {
        if (amount <= 0) {
            return true;
        }
        RSItem[] items = Inventory.find(id);
        if (items.length > 0) {
            String option = "Sell ";
            if (amount >= 10) {
                option += "10";
            } else if (amount >= 5) {
                option += "5";
            } else {
                option += "1";
            }
            final int prevCount = Inventory07.getCount(id);
            if (Clicking.click(option, items)) {
                if (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return Inventory07.getCount(id) < prevCount;
                    }
                }, 4000)) {
                    return sell(amount -= (prevCount - Inventory.getCount(id)), id);
                }
            }
        }
        return false;
    }

    /**
     * Converts the specified item to a shop item.
     *
     * @param item The item to convert.
     */
    public static final void convertToShopItem(RSItem item) {
        if (item != null) {
            int x = ((int) Math.ceil((item.getIndex()) % 8) * 47) + 80;
            int y = ((int) ((Math.floor(item.getIndex()) / 8) % 5) * 47) + 69;
            item.setArea(new Rectangle(x, y, 31, 31));
        }
    }
}
