package scripts.magic.tasks;

import org.tribot.api.General;
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
import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class Enchant
        extends Task {

    private final Spell spell;
    private final String itemName;
    private final String[] itemNames;

    public Enchant(Spell spell, String itemName) {
        this.spell = spell;
        this.itemName = itemName;
        this.itemNames = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), new String[]{itemName});
    }

    @Override
    public void loop() {
        //If the inventory does not contain any of the jewelry we are enchanting
        if (!Inventory07.contains(itemName)) {
            //If the bank is not open
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Opening bank");
                Mouse07.fixSelected();
                //Open that shit boi
                Bank.open();
            } else {
                //If the inventory contains items other than the runes and the items we are enchanting
                if (!Inventory07.containsOnly(itemNames)) {
                    VarsM.get().setStatus("Depositing items");
                    //Deposit the junk ass items
                    Bank.depositAllExcept(itemNames);
                } else {
                    VarsM.get().setStatus("Withdrawing " + itemName);
                    //Withdraw some of that sweet sweet smack
                    if (Bank.withdraw(0, itemName)) {
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
                //Tell the dealer we ain't want some jank shit
                Bank.close();
            } else {
                Mouse07.fixSelected();
                VarsM.get().setStatus("Selecting " + spell.getName());
                //Do the the wizardry
                if (Magic07.selectSpell(spell)) {
                    AntiBan.sleep();
                    if (Waiting.waitUntil(Inventory07::isOpen, 500)) {
                        VarsM.get().setStatus("Casting " + spell.getName() + " on " + itemName);
                        //Inventory07.InventoryPreference preference = Inventory07.getPreference();
                        if (Clicking07.click("Cast " + spell.getName() + " ->", Inventory07.getItem(itemName))) {
                            AntiBan.sleep(General.random(0, 2));
                            if (Inventory07.getCount(itemName) > 1) {
                                AntiBan.sleep();
                                Magic07.hoverSpell(spell);
                            }
                            Waiting.waitUntil(Magic07::isOpen, 2000);
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
