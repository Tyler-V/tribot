package scripts.magic.tasks;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.api2007.walking.Walking07;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class Alchemy
        extends Task {

    private final Spell spell;
    private final String itemName;
    private RSTile startTile;

    /**
     * Constructs a new Alchemy task.
     *
     * @param spell    The spell being cast.
     * @param itemName The name of the item being alched.
     */
    public Alchemy(Spell spell, String itemName) {
        this.spell = spell;
        this.itemName = itemName;
    }

    @Override
    public void loop() {
        if (startTile == null) {
            startTile = Player.getPosition();
        }
        if (Player.getPosition().distanceTo(startTile) > 2) {
            Walking07.straightWalk(startTile);
        } else {
            RSItem item = Inventory07.getItem(itemName);
            if (item != null) {
                VarsM.get().setStatus("Selecting " + spell.getName());
                //Select the spell.
                if (Magic07.selectSpell(spell)) {
                    if (Waiting.waitUntil(Inventory07::isOpen, 500)) {
                        //Cast alchemy on the item.
                        VarsM.get().setStatus("Casting " + spell.getName() + " on " + itemName);
                        if (item.click("Cast " + spell.getName() + " ->")) {
                            Waiting.waitUntil(Magic07::isOpen, 3000);
                            AntiBan.sleep(General.random(0, 2));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
        addTerminateCondition(TerminateConditions.countOfItem(itemName, 1, false));
    }
}
