package scripts.magic.tasks;

import org.tribot.api.General;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.waiting.Waiting;
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
public class SuperglassMake
        extends Task {

    private final String BUCKET_OF_SAND = "Bucket of sand";

    private final Spell spell;
    private final String itemName;
    private final String[] requiredItems;

    public SuperglassMake(String itemName) {
        this.spell = LunarSpell.SUPERGLASS_MAKE;
        this.itemName = itemName;
        this.requiredItems = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), new String[]{"Bucket of sand", itemName});
    }

    @Override
    public void loop() {
        if (Inventory07.contains(BUCKET_OF_SAND) && Inventory07.contains(itemName)) {
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                Bank.close();
            } else {
                VarsM.get().setStatus("Casting " + spell.getName());
                if (Magic07.selectSpell(spell)) {
                    AntiBan.sleep(General.random(4, 8));
                }
            }
        } else {
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Waiting for animation cancel");
                Waiting.waitUntil(() -> !Player07.isAnimating(), 1000);
                AntiBan.sleep(3);
                VarsM.get().setStatus("Opening bank");
                Bank.open();
            } else {
                if (!Inventory07.containsOnly(requiredItems)) {
                    VarsM.get().setStatus("Depositing items");
                    Bank.depositAllExcept(requiredItems);
                } else {
                    int sandCount = Inventory07.getCount(BUCKET_OF_SAND);
                    int itemCount = Inventory07.getCount(itemName);
                    if (sandCount > 13) {
                        VarsM.get().setStatus("Depositing extra sand");
                        Bank.deposit(sandCount - 13, BUCKET_OF_SAND);
                    } else if (itemCount > 13) {
                        VarsM.get().setStatus("Depositing extra " + itemName);
                        Bank.deposit(itemCount - 13, itemName);
                    } else if (sandCount < getNumberOfGlass()) {
                        VarsM.get().setStatus("Withdrawing buckets of sand");
                        if (Bank.withdraw(getNumberOfGlass() - Inventory07.getCount(BUCKET_OF_SAND), BUCKET_OF_SAND)) {
                            VarsM.get().failedAttempts = 0;
                        } else {
                            VarsM.get().failedAttempts++;
                        }
                    } else if (itemCount < getNumberOfGlass()) {
                        VarsM.get().setStatus("Withdrawing " + itemName);
                        if (Bank.withdraw(getNumberOfGlass() - Inventory07.getCount(itemName), itemName)) {
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

    private int getNumberOfGlass() {
        int free = 28 - (Inventory07.getAll().length - Inventory07.getCount("Bucket of sand") - Inventory07.getCount(itemName));
        return free / 2;
    }
}
