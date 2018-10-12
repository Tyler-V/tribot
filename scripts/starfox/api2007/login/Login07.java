package scripts.starfox.api2007.login;

import java.awt.Color;
import java.awt.Point;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Threads;
import scripts.starfox.api.util.Timer;
import static scripts.starfox.api2007.login.LoginMessage.ALREADY_LOGGED_IN;
import static scripts.starfox.api2007.login.LoginMessage.BANNED;
import static scripts.starfox.api2007.login.LoginMessage.CONNECTING;
import static scripts.starfox.api2007.login.LoginMessage.INVALID_ACCOUNT;
import static scripts.starfox.api2007.login.LoginMessage.NEED_MEMBERS;
import static scripts.starfox.api2007.login.LoginMessage.SERVER_UPDATE;

/**
 * The Login07 class is an extension of the TRiBot Login class.
 *
 * @author Nolan
 */
public class Login07
        extends Login {

    /**
     * Gets the login state.
     *
     * @return The login state.
     */
    public static STATE getState() {
        return getLoginState();
    }

    /**
     * gets the login response.
     *
     * @return The login response.
     */
    public static String getResponse() {
        return getLoginResponse();
    }

    /**
     * Checks to see if the game is logged in.
     *
     * @return True if the game is logged in, false otherwise.
     */
    public static boolean isLoggedIn() {
        return getState() == STATE.INGAME && Player.getRSPlayer().getName() != null;
    }

    /**
     * Checks to see if the game is logged out.
     *
     * This method is defined by whether the {@link STATE} is {@link STATE#LOGINSCREEN}.
     *
     * @return True if the game is logged out, false otherwise.
     */
    public static boolean isLoggedOut() {
        return getState() == STATE.LOGINSCREEN;
    }

    /**
     * Checks to see if the game is at the welcome screen.
     *
     * @return True if the game is at the welcome screen, false otherwise.
     */
    public static boolean isAtWelcomeScreen() {
        return getState() == STATE.WELCOMESCREEN;
    }

    /**
     * Returns whether or not the login screen is displaying "Welcome to RuneScape".
     *
     * This is NOT the same as the Welcome to RuneScape screen after you have logged in, this refers to before you have logged in, and "Welcome to RuneScape" is
     * displayed in yellow
     * text.
     *
     * @return Whether or not the login screen is displaying "Welcome to RuneScape".
     */
    public static boolean isWelcomeToRS() {
        return Screen.getColorAt(391, 272).equals(new Color(28, 29, 32));
    }

    /**
     * Waits for the game to be logged in. Times out after 30 seconds.
     *
     * @return True if the game logs in, false otherwise.
     */
    public static boolean waitForLogin() {
        Timer t = new Timer(30000);
        t.start();
        while (!t.timedOut()) {
            if (isLoggedIn()) {
                return true;
            }
            Client.sleep(100);
        }
        return false;
    }

    /**
     * Attempts to log the player in using the specified username and password. Returns true if the login was successful, false otherwise.
     *
     * This method will only try to log in ONCE. After that, the method will return true or it will return false with no exceptions.
     *
     * @param username The username of the account being logged in.
     * @param password The password of the account being logged in.
     * @see #login()
     * @see #login(Account)
     * @return True if the login was successful, false otherwise.
     */
    public static boolean login(final String username, final String password) {
        General.println("Logging in...");
        if (isLoggedIn()) {
            return true;
        }
        Keyboard.setSpeed(0.0);
        if (!fixLogin()) {
            return false;
        }
        final Thread loginThread = getLoginThread(username, password);
        loginThread.start();

        Timer timer = new Timer(45000);
        timer.start();

        while (!isLoggedIn() && !timer.timedOut()) {
            General.sleep(25);
            if (!waitForServer(loginThread) || !waitForInGame(loginThread)) {
                return false;
            }
            if (!NEED_MEMBERS.isActive()) {
                loginThread.stop();
                return isLoggedIn();
            }
        }
        Client.println("Login has timed out.");
        loginThread.stop();
        return false;
    }

    /**
     * Attempts to log the player in using the specified {@link Account} information. Returns true if the login was successful, false otherwise.
     *
     * This method will only try to log in ONCE. After that, the method will return true or it will return false with no exceptions.
     *
     * @param account The account being logged in.
     * @see #login()
     * @see #login(String, String) login(username, password)
     * @return True if the login was successful, false otherwise.
     */
    public static boolean login(Account account) {
        return account != null && login(account.getUsername(), account.getPassword());
    }

    /**
     * Attempts to log the player in using the information from the default TRiBot account manager. Returns true if the login was successful, false otherwise.
     *
     * This method will only try to log in ONCE. After that, the method will return true or it will return false with no exceptions.
     *
     * @see #login(Account)
     * @see #login(String, String) login(username, password)
     * @return True if the login was successful, false otherwise.
     */
    public static boolean login() {
        return login(null, null);
    }

    /**
     * Attempts to pause the login bot. Returns true if the pause is successful, false otherwise.
     *
     * This method will return false if it is called from a thread that isn't static safe, or if the random thread is not contained within the thread group.
     *
     * @return True if the pause is successful, false otherwise.
     */
    public static boolean pause() {
        Client.println("Login-bot paused.");
        return Threads.pauseLoginBot();
    }

    /**
     * Attempts to resume the login bot. Returns true if the resume is successful, false otherwise.
     *
     * This method will return false if it is called from a thread that isn't static safe, or if the random thread is not contained within the thread group.
     *
     * @return True if the resume is successful, false otherwise.
     */
    public static boolean resume() {
        Client.println("Login-bot resumed.");
        return Threads.resumeLoginBot();
    }

    // <editor-fold defaultstate="collapsed" desc="Login Helper Methods">
    private static boolean fixLogin() {
        int numTries = 0;
        do {
            //If the account is at the welcome screen, click the "Existing User" button.
            if (isWelcomeToRS()) {
                General.println("At welcome screen.");
                if (!Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Mouse.hop(new Point(450, 280));
                        General.sleep(25);
                        Mouse.click(1);
                        General.sleep(300);
                        return !isWelcomeToRS();
                    }
                }, 8000)) {
                    General.println("Failed to fix welcome screen. Returning false. (" + numTries + ")");
                    return false;
                }
            }

            General.sleep(250);

            //If the account that is being previously logged in or is banned, click the "Cancel" button.
            if (LoginMessage.isActive(BANNED, ALREADY_LOGGED_IN) && !isWelcomeToRS()) {
                General.println("Old login account is " + (BANNED.isActive() ? "banned" : "already logged in") + ". Clicking cancel.");
                if (!Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        Mouse.hop(new Point(460, 320));
                        General.sleep(25);
                        Mouse.click(1);
                        General.sleep(300);
                        General.println("Login State: " + Login.getLoginState());
                        return isWelcomeToRS();
                    }
                }, 8000)) {
                    General.println("Never reset " + (BANNED.isActive() ? "banned" : "already logged in") + " message. Returning false. (" + numTries + ")");
                    return false;
                }
            }

            numTries++;
            General.sleep(300);
        } while (numTries <= 5
                && (isWelcomeToRS() || LoginMessage.isActive(BANNED, ALREADY_LOGGED_IN)));
        if (isWelcomeToRS() || LoginMessage.isActive(BANNED, ALREADY_LOGGED_IN)) {
            General.println("Failed to init login correctly.");
            return false;
        } else {
            return true;
        }
    }

    private static Thread getLoginThread(final String username, final String password) {
        return new Thread() {
            @Override
            public void run() {
                General.println("Starting login thread.");
                if (username == null || password == null) {
                    Login.login();
                } else {
                    Login.login(username, password);
                }
            }
        };
    }

    private static boolean waitForServer(final Thread loginThread) {
        if (!Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(100);
                return isAtWelcomeScreen() || CONNECTING.isActive();
            }
        }, 8000)) {
            General.println("Never attempted to connect to server. Stopping login thread.");
            loginThread.stop();
            return false;
        } else {
            return true;
        }
    }

    private static boolean waitForInGame(final Thread loginThread) {
        if (isAtWelcomeScreen()) {
            return true;
        } else {
            if (!Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100);
                    return LoginMessage.isActive(INVALID_ACCOUNT, BANNED, SERVER_UPDATE) || isLoggedIn();
                }
            }, 30000)) {
                if (isAtWelcomeScreen()) {
                    Client.println("The login timer has timed out, but the player is at the login screen. Extending timeout and waiting for login to complete.");
                    return waitForInGame(loginThread);
                } else {
                    Client.println("Account was never invalid or in-game. Stopping login thread.");
                    loginThread.stop();
                    return false;
                }
            } else {
                return true;
            }
        }
    }
    // </editor-fold>
}
