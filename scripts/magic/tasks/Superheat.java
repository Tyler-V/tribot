package scripts.magic.tasks;

import scripts.magic.data.SuperheatBar;
import scripts.magic.data.VarsM;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Mouse07;
import scripts.starfox.api2007.banking.Bank;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.api2007.skills.magic.books.NormalSpell;
import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class Superheat
        extends Task {

    private final Spell spell;
    private final SuperheatBar bar;
    private final String[] itemNames;

    /**
     * Constructs a new Superheat.
     *
     * @param bar The bar being used.
     */
    public Superheat(SuperheatBar bar) {
        this.spell = NormalSpell.SUPERHEAT_ITEM;
        this.bar = bar;
        this.itemNames = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), bar.getRequiredOres());
    }

    @Override
    public void loop() {
        //If we do not have the required ores to make the bar
        if (!bar.hasRequiredOres()) {
            //If the bank is not open
            if (!Bank.isOpen()) {
                Mouse07.fixSelected();
                VarsM.get().setStatus("Opening bank");
                //Open the bank
                Bank.open();
            } else {
                //If the inventory contains items other than the items required
                if (!Inventory07.containsOnly(itemNames)) {
                    VarsM.get().setStatus("Depositing items");
                    //Deposit the unnecessary items
                    Bank.depositAllExcept(itemNames);
                } else {
                    //If the inventory is not full
                    if (!Inventory07.isFull()) {
                        for (int i = 0; i < bar.getRequiredOres().length; i++) {
                            VarsM.get().setStatus("Withdrawing " + bar.getRequiredOres()[i]);
                            //I don't remember writing this logic, but it works so....
                            if (Inventory07.getCount(bar.getRequiredOres()[i]) < bar.numberOfPossibleBars() * bar.getNumberOres()[i]) {
                                if (bar.getRequiredOres().length == 1 || i == 1) {
                                    if (Bank.withdraw(0, bar.getRequiredOres()[i])) {
                                        VarsM.get().failedAttempts = 0;
                                    } else {
                                        VarsM.get().failedAttempts++;
                                    }
                                } else {
                                    if (Bank.withdraw(bar.numberOfPossibleBars() * bar.getNumberOres()[i] - Inventory07.getCount(bar.getRequiredOres()[i]), bar.getRequiredOres()[i])) {
                                        VarsM.get().failedAttempts = 0;
                                    } else {
                                        VarsM.get().failedAttempts++;
                                    }
                                }
                            }
                        }
                    } else {
                        VarsM.get().setStatus("Correcting withdrawal surplus");
                        //Deposit all items except the runes because we somehow fucked up withdrawing the right amount of ores
                        Bank.depositAllExcept(Magic07.getRequiredRuneNames(spell));
                    }
                }
            }
        } else {
            //If the bank is still open
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                //Close it
                Bank.close();
            } else {
                VarsM.get().setStatus("Selecting " + spell.getName());
                Mouse07.fixSelected();
                //Cast the spell on the ore
                if (Magic07.selectSpell(spell)) {
                    if (Waiting.waitUntil(Inventory07::isOpen, 500)) {
                        VarsM.get().setStatus("Casting " + spell.getName() + " on " + bar.getRequiredOres()[0]);
                        if (Clicking07.click("Cast " + spell.getName() + " ->", Inventory07.getItemClosestToPointer(bar.getRequiredOres()[0]))) {
                            Waiting.waitUntil(Magic07::isOpen, 3000);
                        }
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
