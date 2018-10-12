package scripts.starfox.api2007.login;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

/**
 * @author Spencer
 */
public class Account implements Externalizable {

    private static final long serialVersionUID = 1L;

    private String rsn;
    private String type;
    private String username;
    private String password;

    public Account() {
        //wtf?
    }

    /**
     * Constructs a new Account.
     *
     * @param rsn      The RuneScape username of the account (display name).
     * @param type     The type of the account. This is does affect anything other than how the bot manager server manages accounts.
     * @param username The login username of the account.
     * @param password The login password of the account.
     */
    public Account(String rsn, String type, String username, String password) {
        this.rsn = rsn;
        this.type = type;
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the RSN.
     *
     * @return The RSN.
     */
    public String getRSN() {
        return rsn;
    }

    /**
     * Gets the account type.
     *
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the account login username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the account login password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Checks to see if the account is the same type as the specified type.
     *
     * @param type The type to test for.
     * @return True if the account is the same type as the specified type, false otherwise.i
     */
    public boolean isType(String type) {
        return this.type.equals(type);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(rsn);
        out.writeObject(type);
        out.writeObject(username);
        out.writeObject(password);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        rsn = (String) in.readObject();
        type = (String) in.readObject();
        username = (String) in.readObject();
        password = (String) in.readObject();
    }

    @Override
    public String toString() {
        return "[Type: " + type + " | Rsn: " + rsn + " | Email: " + username + " | Password: " + password + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.rsn);
        hash = 23 * hash + Objects.hashCode(this.username);
        hash = 23 * hash + Objects.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Account other = (Account) obj;
        if (!Objects.equals(this.rsn, other.rsn)) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return Objects.equals(this.password, other.password);
    }
}
