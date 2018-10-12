package scripts.starfox.manager;

import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Trading;
import scripts.starfox.api.Client;
import scripts.starfox.api2007.Trading07;
import scripts.starfox.api2007.banking.Bank07;
import scripts.starfox.manager.orders.MuleOrder;
import scripts.starfox.manager.orders.MuleOrderPart;

/**
 * @author Starfox
 */
public class MuleTrading {

    /**
     * Attempts to accept the final trade. If it can't, it attempts to decline whatever trade window is open.
     */
    public static void resetTradeScene() {
        if (Trading07.isSecond()) {
            if (!Trading.accept()) {
                Trading.close();
            }
        }
    }

    /**
     * Executes the entire trade that is attached to the specified executingSlave. Returns true if the trade was successfully completed, false otherwise.
     *
     * @param rsn                The rsn of the player that the order is being issued to.
     * @param order              The order that is being issued.
     * @param tradeMule          True if the mule is being traded, false if the slave is being traded.
     * @param terminateCondition If this Condition is ever true, then the method will instantly return false;
     * @param acceptCondition    In order for the trade to be accepted, this method must return true.
     * @return true if the trade was successfully completed, false otherwise.
     */
    public static boolean executeTrade(String rsn, final MuleOrder order, final boolean tradeMule, final Condition terminateCondition,
            Condition acceptCondition) {
        acceptCondition = (acceptCondition == null ? new Condition() {
            @Override
            public boolean active() {
                return true;
            }
        } : acceptCondition);
        Client.println("Executing Trade with " + rsn);
        Bank07.close();
        GameTab.open(GameTab.TABS.INVENTORY);
        if (Trading07.initiatePlayer(false, rsn)) {
            Client.println("Initiate Trade: Successful");
            Client.sleep(200);
            if (offerItems(order, tradeMule, terminateCondition)) {
                Client.sleep(200);
                boolean traded = acceptTrade(order, tradeMule, terminateCondition, acceptCondition);
                Client.sleep(200);
                if (traded) {
                    for (MuleOrderPart part : order.getParts()) {
                        if (part.isBank()) {
                            Client.sleep(200, 350);
                            Bank07.deposit(part.getAmount(), part.getItemId());
                            //SigmaBankingActions.deposit(part.getAmount(), part.getItem().getId(), part.getItem().getNotedId(), part.getItemId() == part.getItem().getNotedId());
                        }
                    }
                    Bank07.close();
                }
                return traded;
            }
        }
        Client.println("Initiate Trade: Failed");
        return false;
    }

    /**
     * Offers all of the items attached to the current order of the executingSlave. Returns true if the items were successfully added, false otherwise.
     *
     * @return true if the items were successfully added, false otherwise.
     */
    private static boolean offerItems(final MuleOrder order, final boolean tradeMule, final Condition terminateCondition) {
        //You're definitely going to need to do something more accurate here...
        if (Trading07.isSecond()) {
            return true;
        }
        if (order == null) {
            return false;
        }
        for (MuleOrderPart part : order.getParts()) {
            if (part.isTradeMule() != tradeMule) {
                continue;
            }
            if (!offerItem(part, terminateCondition)) {
                Client.println("Offering Items: Failed");
                return false;
            } else {
                Client.sleep(200);
            }
        }
        Client.println("Offering Items: Successful");
        return true;
    }

    /**
     * Offers the item attached to the specified MuleOrderPart. Returns true if the item was correctly offered, false otherwise. This method will try to offer the item for 1.5
     * seconds before returning false.
     *
     * @param part The MuleOrderPart that is having its item offered.
     * @return true if the item was correctly offered, false otherwise.
     */
    private static boolean offerItem(final MuleOrderPart part, final Condition terminateCondition) {
        boolean offeredItem = Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                if (terminateCondition.active()) {
                    return false;
                }
                int id = part.isNoted() ? part.getItem().getNotedId() : part.getItem().getId();
                int currentAmount = Trading.getCount(false, id);
                int totalAmount;
                if (part.getAmount() != 0) {
                    totalAmount = part.getAmount() - currentAmount;
                    if (totalAmount == 0) {
                        return true;
                    }
                } else {
                    totalAmount = 0;
                }
                return Trading.offer(totalAmount, id);
            }
        }, 1500);
        if (terminateCondition.active()) {
            return false;
        } else {
            return offeredItem;
        }
    }

    /**
     * Goes through both trade screens and presses accept. The first trade screen is checked for appropriate items, but because the slave is also being controlled by the mule,
     * there is no need for a scam check on the second trade screen. Returns true if the entire trade was successfully accepted, and false otherwise.
     *
     * @return true if the entire trade was successfully accepted, and false otherwise.
     */
    private static boolean acceptTrade(final MuleOrder order, final boolean tradeMule, final Condition terminateCondition, final Condition acceptCondition) {
        if (clickTradeAccept(order, tradeMule, terminateCondition, acceptCondition)) {
            Client.println("Pressing Accept One: Successful");
            boolean secondTradeOpen = Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    if (terminateCondition.active()) {
                        return true;
                    }
                    return Trading07.isSecond();
                }
            }, 3000);
            if (terminateCondition.active()) {
                return false;
            } else {
                if (secondTradeOpen) {
                    Client.println("Second Trade Screen Is Open");
                    boolean clickedAccept = Trading.accept();
                    if (clickedAccept) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                return Trading.getWindowState() != Trading.WINDOW_STATE.FIRST_WINDOW && Trading.getWindowState() != Trading.WINDOW_STATE.SECOND_WINDOW;
                            }
                        }, 8000);
                        Client.sleep(200);
                    }
                    return clickedAccept;
                } else {
                    return false;
                }
            }
        } else {
            Client.println("Pressing Accept One: Failed");
            return false;
        }
    }

    /**
     * Continuously attempts to get to the second trade screen. Will try for 10 seconds before returning false. Returns true if the second trade screen is open within 10 seconds of
     * calling this method.
     *
     * @return True if the second trade screen is open within 10 seconds of calling this method.
     */
    private static boolean clickTradeAccept(final MuleOrder order, final boolean tradeMule, final Condition terminateCondition, final Condition acceptCondition) {
        boolean didClick = Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                if (terminateCondition.active()) {
                    return true;
                }
                return pressTradeAccept(order, tradeMule, terminateCondition, acceptCondition);
            }
        }, 10000);
        if (terminateCondition.active()) {
            return false;
        } else {
            return didClick;
        }
    }

    /**
     * Presses the accept button on the first trade screen. Returns true if the button was successfully pressed, false otherwise.
     *
     * This method will only attempt to press the button if the slave has the appropriate offers up.
     *
     * @return true if the button was successfully pressed, false otherwise.
     */
    private static boolean pressTradeAccept(final MuleOrder order, final boolean tradeMule, final Condition terminateCondition, final Condition acceptCondition) {
        if (Trading07.isSecond()) {
            return true;
        }
        if (acceptCondition.active() && areSlaveOffersUpWait(order, tradeMule, terminateCondition)) {
            return Trading.accept();
        } else {
            return false;
        }
    }

    /**
     * Returns true if the slave has the appropriate offers up. This method will wait 10 seconds for the items to be put up before returning false.
     *
     * @return true if the slave has the appropriate offers up. This method will wait 10 seconds for the items to be put up before returning false.
     */
    private static boolean areSlaveOffersUpWait(final MuleOrder order, final boolean tradeMule, final Condition terminateCondition) {
        //Make sure that every time a new item is put up, this method resets its timer.
        boolean offersUp = Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                if (terminateCondition.active()) {
                    return true;
                }
                return areSlaveOffersUp(order, tradeMule);
            }
        }, 10000);
        if (terminateCondition.active()) {
            return false;
        } else {
            return offersUp;
        }
    }

    /**
     * Returns true if the slave has the proper items up, false otherwise.
     *
     * @return true if the slave has the proper items up, false otherwise.
     */
    private static boolean areSlaveOffersUp(final MuleOrder order, final boolean tradeMule) {
        if (order != null) {
            if (Trading07.isSecond()) {
                return true;
            }
            for (MuleOrderPart part : order.getParts()) {
                if (part.isTradeMule() != tradeMule && !(Trading.getCount(true, part.getItemId()) >= part.getAmount())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
