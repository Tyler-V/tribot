package scripts.starfox.interfaces.listening;

/**
 * @author Nolan
 */
public interface SettingListening07 {

    /**
     * Called when a setting value changes.
     *
     * @param settingIndex  The index of the setting that changed.
     * @param previousValue The previous value.
     * @param currentValue  The current value.
     */
    void settingChanged(int settingIndex, int previousValue, int currentValue);
}
