package scripts.starfox.api2007.skills.magic.data;

import scripts.starfox.api.util.Strings;

/**
 * @author Nolan
 */
public enum MagicBook {
    
    ANCIENT,
    LUNAR,
    NORMAL;
    
    @Override
    public String toString() {
        return Strings.enumToString(name());
    }
}
