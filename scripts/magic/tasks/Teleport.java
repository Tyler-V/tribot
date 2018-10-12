package scripts.magic.tasks;

import org.tribot.api.General;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class Teleport
        extends Task {

    private final Spell spell;

    /**
     * Constructs a new Teleport.
     *
     * @param spell The spell being used.
     */
    public Teleport(Spell spell) {
        this.spell = spell;
    }

    @Override
    public void loop() {
        VarsM.get().setStatus("Casting " + spell.getName());
        if (Magic07.selectSpell(spell)) {
            VarsM.get().setStatus("Waiting random delay");
            AntiBan.sleep(General.random(6, 10));
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
    }
}
