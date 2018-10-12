package scripts.lava;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.interfaces.MessageListening07;

import scripts.lava.Spells.Item;
import scripts.lava.Spells.Spell;

public class Trident implements MessageListening07 {

    private static boolean serverMessage;
    private static int charges;

    public static String[] toString(Spell trident) {
	return new String[] { trident.getChargedName(), trident.getUnchargedName(), trident.getName() };
    }

    public static boolean isWielding(Spell trident) {
	return Equipment.isEquipped(Trident.toString(trident));

    }

    public static boolean isInInventory(Spell trident) {
	return Inventory.find(Trident.toString(trident)).length != 0;

    }

    public static boolean hasTrident(Spell trident) {
	return isWielding(trident) || isInInventory(trident);

    }

    public static boolean isCharged(Spell trident) {
	return Equipment.isEquipped(trident.getChargedName(), trident.getName())
		|| Inventory.find(trident.getChargedName(), trident.getName()).length != 0;

    }

    public static boolean isFullyCharged(Spell trident) {

	return Equipment.isEquipped(trident.getChargedName()) || Inventory.find(trident.getChargedName()).length != 0;

    }

    public static boolean isUncharged(Spell trident) {

	return Equipment.isEquipped(trident.getUnchargedName())
		|| Inventory.find(trident.getUnchargedName()).length != 0;

    }

    public static boolean hasItemsToCharge(Spell trident) {
	for (Item i : trident.getItems()) {
	    if (Inventory.getCount(i.getName()) < (i.getAmount() * 2500)) {
		System.out.println("We need " + (i.getAmount() * 2500) + " " + i.getName() + " and we only have "
			+ Inventory.getCount(i.getName()) + ".");
		return false;
	    }
	}
	return true;
    }

    public static int getCharges(Spell trident) {
	if (isFullyCharged(trident))
	    return 2500;
	if (isUncharged(trident))
	    return 0;
	if (isWielding(trident)) {
	    if (GameTab.open(TABS.EQUIPMENT)) {
		RSItem[] weapon = Equipment.find(SLOTS.WEAPON);
		if (weapon.length > 0) {
		    if (weapon[0].click("Check")) {
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				General.sleep(100);
				return charges != -1;
			    }
			}, 3000);
			return charges;
		    }
		}
	    }
	} else if (isInInventory(trident)) {
	    if (GameTab.open(TABS.INVENTORY)) {
		RSItem[] item = Inventory.find(trident.getName());
		if (item.length > 0) {
		    if (item[0].click("Check")) {
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				General.sleep(100);
				return serverMessage;
			    }
			}, 3000);
			serverMessage = !serverMessage;
			return charges;
		    }
		}
	    }
	}
	return -1;
    }

    private static boolean areHandsFree() {
	return Equipment.getItem(SLOTS.WEAPON) == null && Equipment.getItem(SLOTS.SHIELD) == null;
    }

    public static boolean chargeTrident(Spell trident) {
	if (!hasTrident(trident))
	    return false;
	if (isCharged(trident))
	    return true;
	if (!areHandsFree()) {
	    if (GameTab.open(TABS.EQUIPMENT)) {
		if (Equipment.getItem(SLOTS.WEAPON) != null) {
		    Equipment.remove(SLOTS.WEAPON);
		    Timing.waitCondition(new Condition() {
			public boolean active() {
			    General.sleep(100);
			    return Equipment.getItem(SLOTS.WEAPON) == null;
			}
		    }, 3000);
		}
		if (Equipment.getItem(SLOTS.SHIELD) != null) {
		    Equipment.remove(SLOTS.SHIELD);
		    Timing.waitCondition(new Condition() {
			public boolean active() {
			    General.sleep(100);
			    return Equipment.getItem(SLOTS.SHIELD) == null;
			}
		    }, 3000);
		}
	    }
	}
	if (areHandsFree()) {
	    if (!Game.isUptext("->")) {
		RSItem[] item = null;
		for (Item i : trident.getItems()) {
		    item = Inventory.find(i.getName());
		    if (item.length > 0)
			break;
		}
		if (item.length > 0) {
		    if (item[0].click()) {
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				General.sleep(100);
				return Game.isUptext("->");
			    }
			}, 3000);
		    }
		}
	    }
	    if (Game.isUptext("->")) {
		RSItem[] item = Inventory.find(trident.getName(), trident.getUnchargedName());
		if (item.length > 0) {
		    if (item[0].click()) {
			Timing.waitCondition(new Condition() {
			    public boolean active() {
				General.sleep(100);
				return isCharged(trident);
			    }
			}, 3000);
		    }
		}
	    }
	}
	return isCharged(trident);
    }

    @Override
    public void serverMessageReceived(String s) {
	if (s.contains("Your weapon has")) {
	    serverMessage = true;
	    charges = Integer.parseInt(s.replaceAll("\\D+", ""));
	}
    }

    @Override
    public void clanMessageReceived(String arg0, String arg1) {
    }

    @Override
    public void duelRequestReceived(String arg0, String arg1) {
    }

    @Override
    public void personalMessageReceived(String arg0, String arg1) {
    }

    @Override
    public void playerMessageReceived(String arg0, String arg1) {
    }

    @Override
    public void tradeRequestReceived(String arg0) {
    }
}
