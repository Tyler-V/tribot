package scripts.lava;

import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.interfaces.MessageListening07;

import static java.util.Arrays.asList;

import java.awt.Point;

public class Spells {
    private final static Item AIR_RUNE_2 = new Item("Air rune", 556, 2);
    private final static Item AIR_RUNE_3 = new Item("Air rune", 556, 3);
    private final static Item AIR_RUNE_4 = new Item("Air rune", 556, 4);
    private final static Item AIR_RUNE_5 = new Item("Air rune", 556, 5);

    private final static Item FIRE_RUNE = new Item("Fire rune", 554, 5);

    private final static Item MIND_RUNE = new Item("Mind rune", 558, 1);
    private final static Item CHAOS_RUNE = new Item("Chaos rune", 562, 1);
    private final static Item DEATH_RUNE = new Item("Death rune", 560, 1);
    private final static Item BLOOD_RUNE = new Item("Blood rune", 565, 1);

    private final static Item COINS = new Item("Coins", 0, 10);
    private final static Item ZULRAH_SCALE = new Item("Zulrah's scales", 12934, 1);

    public enum Spell {
	FIRE_STRIKE(false, null, null, "Fire strike", asList(AIR_RUNE_2, MIND_RUNE), 1, 4),

	FIRE_BOLT(false, null, null, "Fire bolt", asList(AIR_RUNE_3, CHAOS_RUNE), 1, 8),

	FIRE_BLAST(false, null, null, "Fire blast", asList(AIR_RUNE_4, DEATH_RUNE), 1, 12),

	FIRE_WAVE(false, null, null, "Fire wave", asList(AIR_RUNE_5, BLOOD_RUNE), 1, 16),

	TRIDENT_OF_THE_SEAS(true, "Trident of the seas (full)", "Uncharged trident", "Trident of the seas",
		asList(DEATH_RUNE, CHAOS_RUNE, FIRE_RUNE, COINS), -1, -1),

	TRIDENT_OF_THE_SWAMP(true, "Trident of the swamp (full)", "Uncharged toxic trident", "Trident of the swamp",
		asList(DEATH_RUNE, CHAOS_RUNE, FIRE_RUNE, ZULRAH_SCALE), -1, -1);

	private final boolean trident;
	private final String fullName;
	private final String name;
	private final String unchargedName;
	private final List<Item> items;
	private final int childID;
	private final int componentID;

	private final int maxCharges = 2500;
	private final int masterID = 201;

	private Spell(boolean trident, String fullName, String unchargedName, String name, List<Item> items,
		int childID, int componentID) {
	    this.trident = trident;
	    this.fullName = fullName;
	    this.name = name;
	    this.unchargedName = unchargedName;
	    this.items = items;
	    this.childID = childID;
	    this.componentID = componentID;
	}

	public boolean isTrident() {
	    return this.trident;
	}

	public String getChargedName() {
	    return this.fullName;
	}

	public String getName() {
	    return this.name;
	}

	public String getUnchargedName() {
	    return this.unchargedName;
	}

	public int getMaxCharges() {
	    return this.maxCharges;
	}

	public List<Item> getItems() {
	    return this.items;
	}

	public int getMasterID() {
	    return this.masterID;
	}

	public int getChildID() {
	    return this.childID;
	}

	public int getComponentID() {
	    return this.componentID;
	}

    }

    public static class Item {
	private final String name;
	private final int id;
	private final int amount;

	public Item(String name, int id, int amount) {
	    this.name = name;
	    this.id = id;
	    this.amount = amount;
	}

	public String getName() {
	    return this.name;
	}

	public int getID() {
	    return this.id;
	}

	public int getAmount() {
	    return this.amount;
	}
    }

    public enum CastingStyle {

	NORMAL(new Point(662, 329), 24), DEFENSIVE(new Point(708, 283), 20);

	private final Point p;
	private final int child;

	CastingStyle(Point p, int child) {
	    this.p = p;
	    this.child = child;
	}

	public Point getPoint() {
	    return this.p;
	}

	public int getChildID() {
	    return this.child;
	}
    }

}
