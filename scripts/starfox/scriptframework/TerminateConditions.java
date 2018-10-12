package scripts.starfox.scriptframework;

import scripts.starfox.api.Client;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Game07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.banking.Bank;
import scripts.starfox.api2007.login.Login07;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;

/**
 * This class houses conditions shared between different scripts that are used to determine whether or not the script should terminate.
 *
 * @author Nolan
 */
public class TerminateConditions {

    /**
     * Gets a terminate condition that is met if the player does not have the required runes to cast the specified spell.
     *
     * @param spell The spell to check the runes of.
     *
     * @return The terminate condition with the criteria above.
     */
    public static TerminateCondition outOfRunes(final Spell spell) {
        return new TerminateCondition() {
            @Override
            public boolean isMet() {
                if (Game07.isGameLoaded() && Login07.isLoggedIn()) {
                    Timer timer = new Timer(3000);
                    timer.start();
                    while (!timer.timedOut()) {
                        if (Magic07.hasRequiredRunes(spell)) {
                            return false;
                        }
                        Client.sleep(10);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public String diagnosis() {
                return "No runes left to cast " + spell + ".";
            }
        };
    }

    /**
     * Builds a terminate condition that will be met when the count of an item with the specified ID is less then the specified required count in the
     * inventory (and bank if specified).
     *
     * @param id            The ID of the item.
     * @param requiredCount The required count for the condition to be met.
     * @param bank          Whether or not the bank should be checked in conjunction with the inventory.
     * @return A terminate condition that is met with the above description.
     */
    public static TerminateCondition countOfItem(final int id, final int requiredCount, final boolean bank) {
        return new TerminateCondition() {
            @Override
            public boolean isMet() {
                if (!Login07.isLoggedIn() || Game07.isGameLoading()) {
                    return false;
                }
                if (bank && !Bank.isLoaded()) {
                    return false;
                }
                int count = 0;
                if (bank && Bank.isLoaded()) {
                    if (Inventory07.getCount(id) >= requiredCount) {
                        return false;
                    }
                    count += Bank.waitGetCount(2500, id);
                    if (count >= requiredCount) {
                        return false;
                    }
                }
                count += Inventory07.waitCount(200, id);
                return count < requiredCount;
            }

            @Override
            public String diagnosis() {
                return "The count of the item (" + id + ") is less than " + requiredCount + ".";
            }
        };
    }

    /**
     * Builds a terminate condition that will be met when the count of an item with the specified name is less then the specified required count in the
     * inventory (and bank if specified).
     *
     * @param name          The name of the item.
     * @param requiredCount The required count for the condition to be met.
     * @param bank          Whether or not the bank should be checked in conjunction with the inventory.
     * @return A terminate condition that is met with the above description.
     */
    public static TerminateCondition countOfItem(final String name, final int requiredCount, final boolean bank) {
        return new TerminateCondition() {
            @Override
            public boolean isMet() {
                if (!Login07.isLoggedIn() || Game07.isGameLoading()) {
                    return false;
                }
                if (bank && !Bank.isLoaded()) {
                    return false;
                }
                int count = 0;
                if (bank && Bank.isLoaded()) {
                    if (Inventory07.getCount(name) >= requiredCount) {
                        return false;
                    }
                    count += Bank.waitGetCount(2500, name);
                    if (count >= requiredCount) {
                        return false;
                    }
                }
                count += Inventory07.waitCount(200, name);
                return count < requiredCount;
            }

            @Override
            public String diagnosis() {
                return "The count of the item (" + name + ") is less than " + requiredCount + ".";
            }
        };
    }
}
