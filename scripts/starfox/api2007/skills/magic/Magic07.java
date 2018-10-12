package scripts.starfox.api2007.skills.magic;

import org.tribot.api.Clicking;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Magic;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSInterface;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Mouse07;
import scripts.starfox.api2007.skills.magic.books.AncientSpell;
import scripts.starfox.api2007.skills.magic.books.LunarSpell;
import scripts.starfox.api2007.skills.magic.books.NormalSpell;
import scripts.starfox.api2007.skills.magic.items.Rune;
import scripts.starfox.api2007.skills.magic.items.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nolan
 */
public class Magic07 {

    /**
     * A constant representing the master index of the magic book interface.
     */
    private static final int MAGIC_BOOK_MASTER_INDEX = 218;

    /**
     * Gets the ID's of the required runes to cast the specified spell.
     *
     * @param spell The spell.
     * @return An array of integers representing the ID's of the Runes.
     */
    public static int[] getRequiredRuneIds(Spell spell) {
        int[] temp = new int[spell.getRequiredRunes().length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = spell.getRequiredRunes()[i].getId();
        }
        return temp;
    }

    /**
     * Gets the name's of the required runes to cast the specified spell.
     *
     * @param spell The spell.
     * @return An array of strings representing the name's of the runes.
     */
    public static String[] getRequiredRuneNames(Spell spell) {
        String[] temp = new String[spell.getRequiredRunes().length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = spell.getRequiredRunes()[i].getName();
        }
        return temp;
    }

    /**
     * Gets the amount of the specified rune required to cast the specified spell.
     *
     * @param spell The spell.
     * @param rune  The rune.
     * @return The amount of the rune required.
     */
    public static int getRequiredRuneAmount(Spell spell, Rune rune) {
        for (int i = 0; i < spell.getRequiredRunes().length; i++) {
            if (spell.getRequiredRunes()[i] == rune) {
                return spell.getNumberOfRunes()[i];
            }
        }
        return 0;
    }

    /**
     * Gets the interface of the spell.
     *
     * @param spell The spell.
     * @return The interface of the spell.
     * Null if no interface was found.
     */
    public static RSInterface getInterface(Spell spell) {
        RSInterface master = Interfaces.get(MAGIC_BOOK_MASTER_INDEX);
        if (master != null) {
            RSInterface[] children = master.getChildren();
            if (children != null && children.length > 0) {
                for (RSInterface child : children) {
                    if (child != null) {
                        String child_name = child.getComponentName();
                        if (child_name != null && child_name.contains(spell.getName())) {
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the interface of the spell with the specified name.
     *
     * @param spellName The spell name.
     * @return The interface of the spell with the specified name.
     * Null if no interface was found.
     */
    public static RSInterface getInterface(String spellName) {
        RSInterface master = Interfaces.get(MAGIC_BOOK_MASTER_INDEX);
        if (master != null) {
            RSInterface[] children = master.getChildren();
            if (children != null && children.length > 0) {
                for (RSInterface child : children) {
                    if (child != null) {
                        String child_name = child.getComponentName();
                        if (child_name != null && child_name.contains(spellName)) {
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks to see if the magic interface is open.
     *
     * @return True if it is open, false otherwise.
     */
    public static boolean isOpen() {
        return TABS.MAGIC.isOpen();
    }

    /**
     * Gets the spell that is currently selected.
     *
     * @return The spell that is currently selected.
     * Null if no spell is selected.
     */
    public static Spell getSelectedSpell() {
        for (NormalSpell spell : NormalSpell.values()) {
            if (isSpellSelected(spell)) {
                return spell;
            }
        }
        for (LunarSpell spell : LunarSpell.values()) {
            if (isSpellSelected(spell)) {
                return spell;
            }
        }
        for (AncientSpell spell : AncientSpell.values()) {
            if (isSpellSelected(spell)) {
                return spell;
            }
        }
        return null;
    }

    /**
     * Checks to see if the local player has the required level to cast the specified spell.
     *
     * @param spell The spell.
     * @return True if the local player has the required level, false otherwise.
     */
    public static boolean hasRequiredLevel(Spell spell) {
        return SKILLS.MAGIC.getActualLevel() >= spell.getRequiredLevel();
    }

    /**
     * Checks to see if the local player has the required runes to cast the spell.
     * This method takes into account any staff that the player may have equipped.
     *
     * @param spell The spell.
     * @return True if the local player has the required runes, false otherwise.
     */
    public static boolean hasRequiredRunes(Spell spell) {
        final List<Rune> currentRunes = new ArrayList<>();
        final int requiredAmount = spell.getRequiredRunes().length;

        //check the runes in the inventory
        for (Rune rune : spell.getRequiredRunes()) {
            if (Inventory07.getCount(rune.getId()) >= getRequiredRuneAmount(spell, rune)) {
                currentRunes.add(rune);
            }
        }

        //check to see if the runes in the inventory fully satisfy the required runes before attempting to check for a staff
        if (currentRunes.size() == requiredAmount) {
            return true;
        }

        Staff staff = Staff.getEquipped();

        //check the runes that the staff provides
        if (staff != null && currentRunes.size() < requiredAmount) {
            for (Rune rune : spell.getRequiredRunes()) {
                if (staff.providesRune(rune) && !currentRunes.contains(rune)) {
                    currentRunes.add(rune);
                }
            }
        }

        return currentRunes.size() == requiredAmount;
    }

    /**
     * Checks to see if the local player can cast the specified spell.
     *
     * @param spell The spell.
     * @return True if the local player can cast the specified spell, false otherwise.
     */
    public static boolean isCastable(Spell spell) {
        return hasRequiredLevel(spell) && hasRequiredRunes(spell);
    }

    /**
     * Checks to see if the specified spell is selected.
     *
     * @param spell The spell to check.
     * @return True if the specified spell is selected, false otherwise.
     */
    public static boolean isSpellSelected(Spell spell) {
        return isSpellSelected(spell.getName());
    }

    /**
     * Checks to see if a spell with the specified name is selected.
     *
     * @param spellName The spell name to check.
     * @return True if a spell with the specified name is selected, false otherwise.
     */
    public static boolean isSpellSelected(String spellName) {
        return Game.isUptext(spellName + " ->");
    }

    /**
     * Hovers the specified spell.
     * This method will open the magic tab if it is not already open.
     *
     * @param spell The spell.
     * @return True if the spell was hovered successfully, false otherwise.
     */
    public static boolean hoverSpell(Spell spell) {
        return hoverSpell(spell.getName());
    }

    /**
     * Hovers over the spell with the specified name.
     * This method will open the magic tab if it is not already open.
     *
     * @param spellName The spell name.
     * @return True if the spell with the specified name was hovered successfully, false otherwise.
     */
    public static boolean hoverSpell(String spellName) {
        RSInterface spellInterface = getInterface(spellName);
        return spellInterface != null && Clicking.hover(spellInterface);
    }

    /**
     * Selects the specified spell.
     * This method will open the magic tab if it is not already open.
     *
     * @param spell The spell to select.
     * @return True if the spell was selected successfully, false otherwise.
     * Returns true if the spell was already selected prior to this method being called.
     */
    public static boolean selectSpell(Spell spell) {
        return selectSpell(spell.getName());
    }

    /**
     * Selects a spell with the specified name.
     * This method will open the magic tab if it is not already open.
     *
     * @param spellName The name of the spell to select.
     * @return True if the spell was selected successfully, false otherwise.
     * Returns true if the spell was already selected prior to this method being called.
     */
    public static boolean selectSpell(String spellName) {
        if (isSpellSelected(spellName)) {
            return true;
        }
        Mouse07.fixSelected();
        Clicking07.currentClickable = getInterface(spellName);
        if (Magic.selectSpell(spellName)) {
            Clicking07.currentClickable = null;
            return true;
        }
        return false;
    }
}
