package scripts.magic.tasks;

import org.tribot.api.Clicking;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api.waiting.Condition07;
import scripts.starfox.api.waiting.Waiting;
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
public class Humidify
        extends Task {

    private final int FULL_BOWL = 6668;
    private final Spell spell;
    private final String itemName;
    private final String[] itemNames;

    public Humidify(String itemName) {
        this.spell = LunarSpell.HUMIDIFY;
        this.itemName = itemName;
        this.itemNames = ArrayUtil.concat(Magic07.getRequiredRuneNames(spell), new String[]{itemName});
    }
    boolean b = false;

    @Override
    public void loop() {
        if (!Inventory07.contains(itemName) && !itemName.equals("Fishbowl")) {
            if (!Bank.isOpen()) {
                VarsM.get().setStatus("Opening bank");
                Mouse07.fixSelected();
                Bank.open();
            } else {
                if (!Inventory07.containsOnly(itemNames)) {
                    VarsM.get().setStatus("Depositing items");
                    Bank.depositAllExcept(itemNames);
                } else {
                    VarsM.get().setStatus("Withdrawing " + itemName);
                    if (Bank.withdraw(0, itemName)) {
                        VarsM.get().failedAttempts = 0;
                    } else {
                        VarsM.get().failedAttempts++;
                    }
                }
            }
        } else {
            if (Bank.isOpen()) {
                VarsM.get().setStatus("Closing bank");
                Bank.close();
            } else {
                if (Inventory07.contains(FULL_BOWL)) {
                    VarsM.get().setStatus("Emptying fishbowl");
                    if (Clicking.click("Empty", Inventory07.find(FULL_BOWL))) {
                        AntiBan.sleep(2);
                    }
                } else {
                    VarsM.get().setStatus("Casting " + spell.getName());
                    if (Magic07.selectSpell(spell)) {
                        Waiting.waitUntil(() -> !Inventory07.contains(itemName) || Inventory07.contains(FULL_BOWL), 1000);
                    }
                    Client.sleep(1000);
                }
            }
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
        if (itemName.equalsIgnoreCase("Fishbowl")) {
            addTerminateCondition(new TerminateCondition() {
                @Override
                public boolean isMet() {
                    Timer t = new Timer(2500);
                    t.start();
                    while (!t.timedOut()) {
                        if (Inventory07.contains(6668) || Inventory07.contains(6667)) {
                            return false;
                        }
                        Client.sleep(50);
                    }
                    return true;
                }

                @Override
                public String diagnosis() {
                    return "No fishbowl detected in inventory.";
                }
            });
        } else {
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
}
