package scripts.starfox.api.listeners;

import java.util.Arrays;
import java.util.HashSet;
import org.tribot.api2007.Skills.SKILLS;
import scripts.starfox.api2007.Game07;
import scripts.starfox.api2007.login.Login07;
import scripts.starfox.interfaces.listening.EXPListening07;

/**
 * @author Nolan
 */
public class EXPListener
        extends ListenerThread {

    /**
     * The listeners being notified.
     */
    private final HashSet<EXPListening07> listeners;

    /**
     * The array of experience values for each skill.
     * This is updated after each listen to keep track of experience values.
     */
    private int[] last_exp;

    /**
     * Constructs a new EXPListener.
     */
    public EXPListener() {
        this(true);
    }

    /**
     * Constructs a new EXPListener.
     *
     * @param start True to start the listener thread upon construction, false otherwise.
     */
    public EXPListener(boolean start) {
        super(start);
        this.listeners = new HashSet<>();
        this.last_exp = getExpArray();
    }

    /**
     * Adds the specified listener to the exp listener.
     *
     * @param listener The listener to add.
     * @return True if the addition was successful, false otherwise.
     */
    public boolean addListener(EXPListening07 listener) {
        synchronized (lock) {
            return listeners.add(listener);
        }
    }

    /**
     * Removes the specified listener from the listener thread.
     *
     * @param listener The listener to remove.
     * @return True if the listener was removed, false otherwise.
     */
    public boolean removeListener(EXPListening07 listener) {
        synchronized (lock) {
            return listeners.remove(listener);
        }
    }

    /**
     * Gets the exp array.
     *
     * @return The exp array.
     */
    private int[] getExpArray() {
        int[] temp = new int[SKILLS.values().length];
        for (int i = 0; i < SKILLS.values().length; i++) {
            temp[i] = SKILLS.values()[i].getXP();
        }
        return temp;
    }

    @Override
    public void listen() {
        int[] current_exp = getExpArray();
        if (Game07.isGameLoaded() && Login07.isLoggedIn()) {
            for (int i = 0; i < current_exp.length; i++) {
                if (current_exp[i] > last_exp[i] && last_exp.length == current_exp.length) {
                    synchronized (lock) {
                        for (EXPListening07 listener : listeners) {
                            listener.expGained(SKILLS.values()[i], current_exp[i] - last_exp[i]);
                        }
                    }
                }
            }
            last_exp = Arrays.copyOf(current_exp, current_exp.length);
        }
    }
}
