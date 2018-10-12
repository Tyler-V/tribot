package scripts.starfox.interfaces.listening;

import org.tribot.api2007.Skills.SKILLS;

/**
 * @author Nolan
 */
public interface EXPListening07 {

    /**
     * Called when exp in the specified skill is gained.
     *
     * @param skill The skill that gained exp.
     * @param exp   The amount of exp gained.
     */
    void expGained(SKILLS skill, int exp);
}
