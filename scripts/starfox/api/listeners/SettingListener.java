package scripts.starfox.api.listeners;

import scripts.starfox.api2007.Settings;
import scripts.starfox.interfaces.listening.SettingListening07;

import java.util.Arrays;
import java.util.HashSet;

/**
 * The SettingListener class listens for changes in setting values.
 *
 * @author Nolan
 */
public class SettingListener
        extends ListenerThread {

    /**
     * A set of the listeners being notified.
     */
    private final HashSet<SettingListening07> listeners;

    /**
     * An array containing the last scanned value of each setting.
     */
    private int[] last_settings;

    /**
     * Constructs a new SettingListener.
     */
    public SettingListener() {
        this(true);
    }

    /**
     * Constructs a new SettingListener.
     *
     * @param start True to start the listener thread upon construction, false otherwise.
     */
    public SettingListener(boolean start) {
        super(start);
        this.listeners = new HashSet<>();
        this.last_settings = Settings.getAll();
    }

    /**
     * Gets the set of listeners being notifiedF.
     *
     * @return The listeners.
     */
    public final HashSet<SettingListening07> getListeners() {
        return this.listeners;
    }

    /**
     * Adds the specified listener to the setting listener.
     *
     * @param listener The listener to add.
     * @return True if the setting listener did not already contain the specified listener, false otherwise.
     */
    public final boolean addListener(SettingListening07 listener) {
        return getListeners().add(listener);
    }

    /**
     * Removes the specified listener from the setting listener.
     *
     * If the setting listener does not contain the specified listener, it will not be removed.
     *
     * @param listener The listener to remove.
     * @return True if the setting listener contained the specified listener, false otherwise.
     */
    public final boolean removeListener(SettingListening07 listener) {
        return getListeners().remove(listener);
    }

    /**
     * Notifies all of the listeners of a setting value change.
     *
     * @param settingIndex  The index of the setting.
     * @param previousValue The previous value of the setting.
     * @param currentValue  The current value of the setting or what the value changed to.
     */
    private void notifyListeners(int settingIndex, int previousValue, int currentValue) {
        for (SettingListening07 listener : getListeners()) {
            listener.settingChanged(settingIndex, previousValue, currentValue);
        }
    }

    @Override
    public void listen() {
        //An array representing the current setting values.
        int[] current_settings = Settings.getAll();

        if (last_settings != null) {
            for (int i = 0; i < last_settings.length && i < current_settings.length; i++) {
                int previousValue = last_settings[i];
                int currentValue = current_settings[i];
                if (previousValue != currentValue) {
                    notifyListeners(i, previousValue, currentValue);
                }
            }
        }

        //Update the last setting values scanned.
        last_settings = Arrays.copyOf(current_settings, current_settings.length);
    }
}
