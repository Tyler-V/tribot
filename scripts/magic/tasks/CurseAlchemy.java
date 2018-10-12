package scripts.magic.tasks;

import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;

/**
 * @author Nolan
 */
public class CurseAlchemy
        extends Task {

    private final Spell alch;
    private final Spell curse;
    private final String itemName;
    private final Alchemy alchemyTask;
    private final Curse curseTask;

    /**
     * Constructs a new CurseAlchemy task.
     *
     * @param alchemy  The alchemy spell being used.
     * @param curse    The curse being used.
     * @param itemName The name of the item being alched.
     * @param npcName  The name of the npc being cursed.
     */
    public CurseAlchemy(Spell alchemy, Spell curse, String itemName, String npcName) {
        this.alch = alchemy;
        this.curse = curse;
        this.itemName = itemName;
        this.alchemyTask = new Alchemy(alchemy, itemName);
        this.curseTask = new Curse(curse, npcName);
    }

    @Override
    public void loop() {
        curseTask.loop();
        alchemyTask.loop();
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(alch));
        addTerminateCondition(TerminateConditions.outOfRunes(curse));
        addTerminateCondition(TerminateConditions.countOfItem(itemName, 1, false));
    }
}
