package scripts.starfox.api2007.login;

import java.io.Serializable;
import org.tribot.api2007.Login;

/**
 *
 * @author Spencer
 */
public enum LoginMessage implements Serializable {

    BANNED("your account has been disabled"), INVALID_ACCOUNT("invalid username"), SERVER_UPDATE("updated"), CONNECTION_ERROR("error connecting to server"),
    NEED_MEMBERS("you need a members account"), ALREADY_LOGGED_IN("your account is already logged in"), CONNECTING("connecting to server"), UNKNOWN;

    final String[] MUST_CONTAINS;

    LoginMessage(String... mustContains) {
        this.MUST_CONTAINS = mustContains;
    }

    /**
     * Returns true if this LoginMessage is currently displaying, false otherwise.
     *
     * NOTE: This method will return true if the Welcome to RuneScape message is being shown and this LoginMessage was the last to be displayed.
     *
     * @see #isActive(String)
     * @see #getLoginMessage()
     * @return True if this LoginMessage is currently displaying, false otherwise.
     */
    public final boolean isActive() {
        return isActive(this);
    }

    /**
     * Returns true if this LoginMessage matches the specified login text, false otherwise.
     *
     * NOTE: This method will return true if the Welcome to RuneScape message is being shown and this LoginMessage was the last to be displayed.
     *
     * @param loginMessage The login text.
     * @see #isActive()
     * @see #getLoginMessage(String)
     * @return True if this LoginMessage matches the specified login text, false otherwise.
     */
    public final boolean isActive(String loginMessage) {
        return LoginMessage.getLoginMessage(loginMessage) == this;
    }

    /**
     * Returns the LoginMessage that matches the specified string, or {@link UNKNOWN} if the login message does not match any LoginMessage values.
     *
     * Does NOT check if the login screen is showing.
     *
     * @param loginMessage The LoginMessage being checked.
     * @see #getLoginMessage()
     * @return The LoginMessage that matches the specified string, or {@link UNKNOWN} if the login message does not match any LoginMessage values.
     */
    public static LoginMessage getLoginMessage(String loginMessage) {
        for (LoginMessage login : values()) {
            for (String mustContain : login.MUST_CONTAINS) {
                if (loginMessage.toLowerCase().contains(mustContain)) {
                    return login;
                }
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the LoginMessage that matches the message currently displayed on the screen, or {@link UNKNOWN} if the login message does not match any LoginMessage values.
     *
     * Returns null if the login screen is not showing.
     *
     * @see #getLoginMessage(String)
     * @return The LoginMessage that matches the message currently displayed on the screen, or {@link UNKNOWN} if the login message does not match any LoginMessage values.
     */
    public static LoginMessage getLoginMessage() {
        if (Login07.isLoggedOut()) {
            return getLoginMessage(Login.getLoginResponse());
        } else {
            return null;
        }
    }

    /**
     * Returns true if any of the specified LoginMessages are active, false otherwise.
     *
     * @param messages The messages that are being checked.
     * @see #isActive(String, LoginMessage...)
     * @see #isActive()
     * @return True if any of the specified LoginMessages are active, false otherwise.
     */
    public static boolean isActive(LoginMessage... messages) {
        return isActive(Login07.getResponse(), messages);
    }

    /**
     * Returns true if any of the specified LoginMessages are active matching the specified string, false otherwise.
     *
     * @param loginMessage The login message.
     * @param messages     The LoginMessages.
     * @see #isActive(LoginMessage...)
     * @see #isActive(String)
     * @return True if any of the specified LoginMessages are active matching the specified string, false otherwise.
     */
    public static boolean isActive(String loginMessage, LoginMessage... messages) {
        for (LoginMessage message : messages) {
            if (message.isActive(loginMessage)) {
                return true;
            }
        }
        return false;
    }
}
