package scripts.starfox.api2007.filters;

import org.tribot.api.interfaces.Clickable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSNPC;
import scripts.starfox.api.Client;

/**
 * Created by Nolan on 9/30/2015.
 */
public class Filters07
        extends Filters {

    /**
     * Combines all of the filters specified into one filter.
     *
     * @param filters The filters to combine.
     * @param <T>     The generic type of the filter.
     * @return The combined filter.
     */
    public static <T> Filter<T> combine(Filter<T>... filters) {
        return new Filter<T>() {
            @Override
            public boolean accept(T t) {
                for (Filter<T> filter : filters) {
                    if (!filter.accept(t)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Gets a {@link Filter<RSMenuNode>} that finds a {@link RSMenuNode} with the specified option that correlates to
     * the specified {@link Clickable}.
     *
     * @param option    The option to select.
     * @param clickable The {@link Clickable}.
     * @return The {@link Filter<RSMenuNode>}.
     */
    public static Filter<RSMenuNode> getFilter(String option, Clickable clickable) {
        return new Filter<RSMenuNode>() {
            @Override
            public boolean accept(RSMenuNode rsMenuNode) {
                if (rsMenuNode != null) {
                    return rsMenuNode.contains(option) && rsMenuNode.correlatesTo(clickable);
                }
                return false;
            }
        };
    }

    /**
     * Contains filters that are combat related.
     */
    public static class Combat {

        /**
         * Gets a Filter<RSNPC> that will accept NPCs that have a combat level equal to the combat level specified.
         *
         * @param combatLevel The combat level to compare with.
         * @return The filter.
         */
        public static Filter<RSNPC> combatLevelEquals(int combatLevel) {
            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    if (rsnpc != null) {
                        return rsnpc.getCombatLevel() == combatLevel;
                    }
                    return false;
                }
            };
        }

        /**
         * Gets a Filter<RSNPC> that will accept NPCs that have a combat level greater than the combat level specified.
         *
         * @param combatLevel The combat level to compare with.
         * @return The filter.
         */
        public static Filter<RSNPC> combatLevelGreaterThan(int combatLevel) {
            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    if (rsnpc != null) {
                        return rsnpc.getCombatLevel() > combatLevel;
                    }
                    return false;
                }
            };
        }

        /**
         * Gets a Filter<RSNPC> that will accept NPCs that have a combat level less than the combat level specified.
         *
         * @param combatLevel The combat level to compare with.
         * @return The filter.
         */
        public static Filter<RSNPC> combatLevelLessThan(int combatLevel) {
            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    if (rsnpc != null) {
                        return rsnpc.getCombatLevel() < combatLevel;
                    }
                    return false;
                }
            };
        }

        /**
         * Gets a Filter<RSNPC> that will accept NPCs that are not in combat with another player.
         *
         * @param withMe True to accept NPCs that are in combat with your player, false otherwise.
         * @return The filter.
         */
        public static Filter<RSNPC> notInCombat(boolean withMe) {
            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    if (rsnpc != null) {
                        RSCharacter interacting = rsnpc.getInteractingCharacter();
                        if (interacting == null) {
                            return true;
                        }
                        if (withMe) {
                            return interacting.equals(Player.getRSPlayer());
                        }
                    }
                    return false;
                }
            };
        }
    }
}
