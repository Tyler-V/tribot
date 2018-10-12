package scripts.magic.listeners;

import org.tribot.api2007.Skills.SKILLS;
import scripts.starfox.api.listeners.EXPListener;
import scripts.starfox.interfaces.listening.EXPListening07;

/**
 * @author Nolan
 */
public class MagicEXPListener
        implements EXPListening07 {

    private int spellsCasted;
    EXPListener listener;

    /**
     * Constructs a new MagicEXPListener.
     */
    public MagicEXPListener() {
        listener = new EXPListener();
        listener.addListener(this);
        this.spellsCasted = 0;
    }

    /**
     * Gets the amount of spells casted.
     *
     * @return The amount of spells casted.
     */
    public int getSpellsCasted() {
        return this.spellsCasted;
    }

    @Override
    public void expGained(SKILLS skill, int exp) {
        if (skill == SKILLS.MAGIC) {
            spellsCasted++;
        }
    }
}
