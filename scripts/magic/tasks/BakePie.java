package scripts.magic.tasks;

import org.tribot.api2007.Player;
import scripts.magic.data.Pie;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Interfaces07;
import scripts.starfox.api2007.Inventory07;
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
public class BakePie
        extends Task {

    private final Spell spell;
    private final Pie pie;
    private final String[] itemNames;

    /**
     * Constructs a new Bake pie task.
     *
     *
     * @param pie The pie being baked.
     */
    public BakePie(Pie pie) {
        this.spell = LunarSpell.BAKE_PIE;
        this.pie = pie;
        this.itemNames = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), new String[]{pie.getRawName()});
    }

    @Override
    public void loop() {
        //If the inventory does not contain any raw pies.
        if (!Inventory07.contains(pie.getRawName())) {
            //If the bank is not open.
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Opening bank");
                //Open the bank.
                Bank.open();
            } else {
                //If the inventory does not contain only the runes and raw pie, deposit any other items.
                if (!Inventory07.containsOnly(itemNames)) {
                    VarsM.get().setStatus("Depositing items");
                    Bank.depositAllExcept(itemNames);
                } else {
                    VarsM.get().setStatus("Withdrawing " + pie.getRawName());
                    //Withdraw raw pies.
                    if (Bank.withdraw(0, pie.getRawName())) {
                        VarsM.get().failedAttempts = 0;
                    } else {
                        VarsM.get().failedAttempts++;
                    }
                }
            }
        } else {
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                //Close the bank.
                Bank.close();
            } else {
                VarsM.get().setStatus("Casting " + spell.getName());
                //Cast the spell.
                if (Magic07.selectSpell(spell)) {
                    Timer t = new Timer(2500);
                    t.start();
                    VarsM.get().setStatus("Waiting...");
                    while (!t.timedOut() && !Interfaces07.isClickContinueUp() && Inventory07.contains(pie.getRawName())) {
                        AntiBan.timedActions();
                        if (Player.getAnimation() != -1) {
                            t.reset();
                        }
                        Client.sleep(50);
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
