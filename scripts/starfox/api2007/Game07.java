package scripts.starfox.api2007;

import org.tribot.api2007.Game;
import scripts.starfox.api.util.Strings;

/**
 * The Game07 class is a utility class that provides information about the game state.
 *
 * @author Nolan
 */
public class Game07 {

    /**
     * Gets the game state.
     *
     * @return The game state.
     */
    public static GameState getState() {
        for (GameState state : GameState.values()) {
            if (state.getValue() == Game.getGameState()) {
                return state;
            }
        }
        return GameState.NONE;
    }

    /**
     * Checks to see if the game is loaded.
     *
     * @return True if the game is loaded, false otherwise.
     */
    public static boolean isGameLoaded() {
        return getState() == GameState.LOADED;
    }

    /**
     * Checks to see if the game is loading.
     *
     * @return True if the game is loading, false otherwise.
     */
    public static boolean isGameLoading() {
        return getState() == GameState.LOADING;
    }

    /**
     * Checks to see if the game is logged out.
     *
     * @return True if the game is logged out, false otherwise.
     */
    public static boolean isGameLoggedOut() {
        return getState() == GameState.LOGGED_OUT;
    }

    /**
     * Checks to see if the game is logging in.
     *
     * @return True if the game is logging in, false otherwise.
     */
    public static boolean isGameLoggingIn() {
        return getState() == GameState.LOGGING_IN;
    }

    /**
     * Checks to see if the game is switching worlds.
     *
     * @return True if the game is switching worlds, false otherwise..
     */
    public static boolean isGameSwitchingWorlds() {
        return getState() == GameState.SWITCHING_WORLDS;
    }

    /**
     * Constants for different game states.
     */
    public enum GameState {
        LOGGED_OUT(10),
        LOGGING_IN(20),
        LOADING(25),
        LOADED(30),
        SWITCHING_WORLDS(45),
        NONE(-1);

        private final int value;

        /**
         * Constructs a new GameState.
         *
         * @param value The value for the game state.
         */
        GameState(int value) {
            this.value = value;
        }

        /**
         * Gets the value of the game state.
         *
         * @return The value.
         */
        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return Strings.enumToString(name());
        }
    }
}
