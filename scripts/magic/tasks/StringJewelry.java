package scripts.magic.tasks;

import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Interfaces07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.banking.Bank;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.api2007.skills.magic.books.LunarSpell;
import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class StringJewelry
        extends Task {

    private final Spell spell;
    private final String itemName;
    private final int unstrungId; //The unstrung amulet ID.
    private final int[] itemIDs; //The items that need to be in the inventory to cast String Jewelry.

    /**
     * Constructs a new StringJewelry task.
     *
     * @param itemName The name of the jewelry to string.
     */
    public StringJewelry(String itemName) {
        this.spell = LunarSpell.STRING_JEWELRY;
        this.itemName = itemName;
        switch (itemName) {
            case "Gold amulet":
                this.unstrungId = 1673;
                break;
            case "Sapphire amulet":
                this.unstrungId = 1675;
                break;
            case "Emerald amulet":
                this.unstrungId = 1677;
                break;
            case "Ruby amulet":
                this.unstrungId = 1679;
                break;
            case "Diamond amulet":
                this.unstrungId = 1681;
                break;
            case "Dragonstone ammy":
                this.unstrungId = 1683;
                break;
            case "Onyx amulet":
                this.unstrungId = 6579;
                break;
            default:
                this.unstrungId = -1;
        }
        this.itemIDs = ArrayUtil.concat(Magic07.getRequiredRuneIds(spell), new int[]{unstrungId});
    }

    @Override
    public void loop() {
        //If the inventory does not contain any unstrung amulets.
        if (!Inventory07.contains(unstrungId)) {
            //If the bank is not open, open it.
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Opening bank");
                Bank.open();
            } else {
                //If the bank is open and we have items other than the items required to cast String Jewelry, deposit everything except the required items.
                if (!Inventory07.containsOnly(itemIDs)) {
                    VarsM.get().setStatus("Depositing items");
                    Bank.depositAllExcept(itemIDs);
                } else {
                    //We have only the required items in our inventory, but do not have any unstrung amulets.
                    VarsM.get().setStatus("Withdrawing " + itemName);
                    if (Bank.withdraw(0, unstrungId)) {
                        VarsM.get().failedAttempts = 0;
                    } else {
                        VarsM.get().failedAttempts++;
                    }
                }
            }
        } else {
            //We have all of the required items to cast String Jewelry, but the bank is open, so we must close it.
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                Bank.close();
            } else {
                VarsM.get().setStatus("Casting " + spell.getName());
                //If the bank is close, cast String Jewelry.
                if (Magic07.selectSpell(spell)) {
                    Inventory07.open();
                    Timer t = new Timer(2500);
                    t.start();
                    VarsM.get().setStatus("Waiting");
                    while (!t.timedOut() && !Interfaces07.isClickContinueUp() && Inventory07.contains(unstrungId)) {
                        AntiBan.timedActions();
                        if (Player07.isAnimating()) {
                            t.reset();
                        }
                        AntiBan.sleep();
                    }
                }
            }
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
        addTerminateCondition(new TerminateCondition() {
            @Override
            public boolean isMet() {
                if (!Bank.isLoaded()) {
                    VarsM.get().failedAttempts = 0;
                    return false;
                }
                return VarsM.get().failedAttempts > 10;
            }

            @Override
            public String diagnosis() {
                return "Ran out of supplies.";
            }
        });
    }
}
