package scripts.magic.tasks;

import org.tribot.api.General;
import scripts.magic.data.Plank;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Mouse07;
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
public class PlankMake
        extends Task {

    private final int COINS_ID = 995;

    private final Spell spell;
    private final Plank plank;
    private final String[] itemNames;

    /**
     * Constructs a new PlankMake task.
     *
     * @param plank The plank being used.
     */
    public PlankMake(Plank plank) {
        this.spell = LunarSpell.PLANK_MAKE;
        this.plank = plank;
        this.itemNames = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), new String[]{"Coins", plank.getLogName()});
    }

    @Override
    public void loop() {
        //If the inventory contains enough coins to make at least one plank and the inventory contains at least one log
        if (Inventory07.getCount(COINS_ID) >= plank.getPlankMakeCost() && Inventory07.contains(plank.getLogName())) {
            //If the bank is open
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                //Close the bank
                Bank.close();
            } else {
                VarsM.get().setStatus("Selecting " + spell.getName());
                //Select the plank make spell
                AntiBan.sleep(General.random(3, 7));
                if (Magic07.selectSpell(spell)) {
                    //Wait until the inventory is open for a maximum of 500 milliseconds
                    if (Waiting.waitUntil(Inventory07::isOpen, 500)) {
                        VarsM.get().setStatus("Casting " + spell.getName() + " on " + plank.getLogName());
                        //Click the log nearest to the pointer
                        AntiBan.sleep(30);
                        if (Clicking07.click("Cast " + spell.getName() + " -> " + plank.getLogName(), Inventory07.getItemClosestToPointer(plank.getLogName()))) {
                            if (Inventory07.getCount(plank.getLogName()) > 1) {
                                Magic07.hoverSpell(spell);
                            }
                            //Wait until the magic interface is open for a maximum of 3000 milliseconds
                            Waiting.waitUntil(Magic07::isOpen, 3000);
                        }
                    } else {
                        Mouse07.fixSelected();
                    }
                }
            }
        } else {
            //If the bank is not open
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Opening bank");
                Mouse07.fixSelected();
                //Open the bank
                Bank.open();
            } else {
                //If the inventory contains items other than the items that are required
                if (!Inventory07.containsOnly(itemNames)) {
                    VarsM.get().setStatus("Depositing items");
                    //Deposit the unnecessary items
                    Bank.depositAllExcept(itemNames);
                } else {
                    //If the inventory contains less coins than are required to make a single plank
                    if (Inventory07.getCount(COINS_ID) < plank.getPlankMakeCost()) {
                        VarsM.get().setStatus("Withdrawing coins");
                        //Withdraw 100,000-250,000 coins
                        if (Bank.withdraw(General.random(100000, 250000), COINS_ID)) {
                            VarsM.get().failedAttempts = 0;
                        } else {
                            VarsM.get().failedAttempts++;
                        }
                        //If the inventory does not contain any logs
                    } else if (!Inventory07.contains(plank.getLogName())) {
                        VarsM.get().setStatus("Withdrawing logs");
                        //Withdraw logs
                        if (Bank.withdraw(0, plank.getLogName())) {
                            VarsM.get().failedAttempts = 0;
                        } else {
                            VarsM.get().failedAttempts++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
        addTerminateCondition(TerminateConditions.countOfItem(995, plank.getPlankMakeCost(), true));
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
