package scripts.starfox.tracking.skills;

import org.tribot.api2007.Skills.SKILLS;

/**
 * @author Nolan
 */
public class Skill {

    private final SKILLS skill;
    private final int startExp;
    private final int startLevel;

    /**
     * Constructs a new Skill.
     * The start exp and level will be marked once the Skill has been constructed.
     *
     * @param skill The skill to track.
     */
    public Skill(SKILLS skill) {
        this.skill = skill;
        this.startExp = skill.getXP();
        this.startLevel = skill.getActualLevel();
    }

    /**
     * Gets the skill.
     *
     * @return The skill.
     */
    public SKILLS getSkill() {
        return this.skill;
    }

    /**
     * Gets the start exp of the skill.
     *
     * @return The start exp.
     */
    public int getStartExp() {
        return this.startExp;
    }

    /**
     * Gets the start level of the skill.
     *
     * @return The start level.
     */
    public int getStartLevel() {
        return this.startLevel;
    }

    /**
     * Gets the amount of exp gained in this skill.
     *
     * @return The amount of exp gained.
     */
    public int getExpGained() {
        return getSkill().getXP() - getStartExp();
    }

    /**
     * Gets the amount of levels gained in this skill.
     *
     * @return The amount of levels gained.
     */
    public int getLevelsGained() {
        return getSkill().getActualLevel() - getStartLevel();
    }

    /**
     * Gets the boost amount for the skill. (only for super+)
     *
     * @return The boost amount.
     */
    public int getBoostAmount() {
        return (int) ((getSkill().getActualLevel() * .15) + 5);
    }

    /**
     * Checks to see if the skill should be boosted.
     *
     * @return True if it should be boosted, false otherwise.
     */
    public boolean shouldBoost() {
        return getSkill().getCurrentLevel() - getSkill().getActualLevel() <= (getBoostAmount() / 2);
    }
}
