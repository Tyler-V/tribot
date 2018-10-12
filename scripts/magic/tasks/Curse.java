package scripts.magic.tasks;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.api2007.entities.NPCs07;
import scripts.starfox.api2007.entities.Objects07;
import scripts.starfox.api2007.filters.Filters07;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.api2007.walking.Walking07;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class Curse
        extends Task {

    private final Spell spell;
    private final String npcName;
    private RSTile startTile;

    /**
     * Constructs a new Curse task.
     *
     * @param spell   The spell being used.
     * @param npcName The name of the npc that the curse will be cast upon.
     */
    public Curse(Spell spell, String npcName) {
        this.spell = spell;
        this.npcName = npcName;
    }

    @Override
    public void loop() {
        //If we haven't gotten the start tile, grab it
        if (startTile == null) {
            startTile = Player.getPosition();
        }
        //If we are above ground and we're cursing the monk of zamorak, climb down the stairs
        if (Game.getPlane() > 0 && npcName.startsWith("Monk of")) {
            if (Clicking.click("Climb-down", Objects07.getObject("Staircase", 10))) {
                Waiting.waitUntilMove(() -> Game.getPlane() < 1, 3000);
            }
        } else {
            //If we're over 2 tiles away from our starting point, walk back to the starting point
            if (Entities.distanceTo(startTile) > 2) {
                Walking07.straightWalk(startTile);
            } else {
                VarsM.get().setStatus("Selecting " + spell.getName());
                //Select the curse
                if (Magic07.selectSpell(spell)) {
                    AntiBan.sleep(General.random(0, 5));
                    RSNPC npc = NPCs07.getNPC(npcName);
                    if (npc != null) {
                        if (npc.isOnScreen()) {
                            VarsM.get().setStatus("Casting " + spell.getName() + " on " + npcName);
                            //Cast the curse on the npc
                            if (npc.click(Filters07.getFilter("Cast", npc))) {
                                AntiBan.sleep(General.random(0, 10));
                            }
                        } else {
                            VarsM.get().setStatus("Rotating camera to NPC");
                            //Turn the camera towards the npc
                            Camera.turnToTile(npc);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
    }
}
