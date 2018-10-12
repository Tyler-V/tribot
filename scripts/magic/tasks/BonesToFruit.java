package scripts.magic.tasks;

import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.banking.Bank;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class BonesToFruit
        extends Task {

    private final String[] itemNames;
    private final Spell spell;
    private final String boneName;
    private final int amount;

    /**
     * Constructs a new BonesToFruit task.
     *
     * @param spell    The spell.
     * @param boneName The name of the bone.
     * @param amount   The amount to withdraw.
     */
    public BonesToFruit(Spell spell, String boneName, int amount) {
        this.itemNames = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), new String[]{boneName});
        this.spell = spell;
        this.boneName = boneName;
        this.amount = amount;
    }

    @Override
    public void loop() {
        //If the inventory does not have any bones in it
        if (!Inventory07.contains(boneName)) {
            //If the bank is not open
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Opening bank");
                //Open the bank
                Bank.open();
            } else {
                //If the inventory contains items other than the required items
                if (!Inventory07.containsOnly(itemNames)) {
                    VarsM.get().setStatus("Depositing items");
                    //Deposit the unnecessary items
                    Bank.depositAllExcept(itemNames);
                } else {
                    VarsM.get().setStatus("Withdrawing bones");
                    //Withdraw bones
                    if (Bank.withdraw(amount, boneName)) {
                        VarsM.get().failedAttempts = 0;
                    } else {
                        VarsM.get().failedAttempts++;
                    }
                }
            }
        } else {
            //If the bank is open
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                //Close the bank
                Bank.close();
            } else {
                VarsM.get().setStatus("Casting spell");
                //Select the bones to fruit spell
                if (Magic07.selectSpell(spell)) {
                    VarsM.get().setStatus("Waiting for animation");
                    //Wait for the animation to finish
                    AntiBan.sleep(10);
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
