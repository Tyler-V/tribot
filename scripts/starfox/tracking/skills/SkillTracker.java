package scripts.starfox.tracking.skills;

import java.util.ArrayList;
import java.util.List;
import org.tribot.api2007.Skills.SKILLS;

/**
 * @author Nolan
 */
public class SkillTracker {

    private Skill[] skills;

    /**
     * Constructs a new SkillTracker.
     *
     * Tracking of skills begins once the tracker has been created.
     */
    public SkillTracker() {
        this.skills = generateSkillArray();
    }

    /**
     * Gets the skills being tracked.
     *
     * @return The skills.
     */
    public Skill[] getSkills() {
        return this.skills;
    }

    /**
     * Gets the Skill object associated with the specified skill.
     * @param skill The skill.
     * @return The Skill object.
     */
    public Skill getSkill(SKILLS skill) {
        return getSkillInArray(skill);
    }

    /**
     * Gets the amount of exp gained in the specified skill(s).
     * @param skills The skill(s).
     * @return The amount of exp gained.
     */
    public int getExpGained(SKILLS... skills) {
        int expGained = 0;
        for (Skill skill : getSkills()) {
            for (SKILLS skill2 : skills) {
                if (skill.getSkill() == skill2) {
                    expGained += skill.getExpGained();
                }
            }
        }
        return expGained;
    }

    /**
     * Gets the amount of levels gained in the specified skill(s).
     * @param skills The skill(s).
     * @return The amount of levels gained.
     */
    public int getLevelsGained(SKILLS... skills) {
        int levelsGained = 0;
        for (Skill skill : getSkills()) {
            for (SKILLS skill2 : skills) {
                if (skill.getSkill() == skill2) {
                    levelsGained += skill.getLevelsGained();
                }
            }
        }
        return levelsGained;
    }

    /**
     * Gets the Skill object in the skill array associated with the specified skill.
     * @param skill The skill.
     * @return The Skill object.
     */
    private Skill getSkillInArray(SKILLS skill) {
        for (Skill s : getSkills()) {
            if (s.getSkill() == skill) {
                return s;
            }
        }
        return null;
    }

    /**
     * Generates the Skill array.
     * @return The Skill array generated.
     */
    private Skill[] generateSkillArray() {
        List<Skill> skillList = new ArrayList<>(SKILLS.values().length);
        for (SKILLS skill : SKILLS.values()) {
            skillList.add(new Skill(skill));
        }
        return skillList.toArray(new Skill[0]);
    }
    
    /**
     * Refreshes the tracker.
     */
    public void refresh() {
        this.skills = generateSkillArray();
    }
}
