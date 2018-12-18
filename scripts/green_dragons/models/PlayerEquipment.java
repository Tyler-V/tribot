package scripts.green_dragons.models;

import java.util.function.Predicate;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import javafx.scene.image.WritableImage;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyUtils;

public class PlayerEquipment {

	private SLOTS slot;
	private int id;
	private Predicate<RSItem> predicate;

	public PlayerEquipment(SLOTS slot, int id, Predicate<RSItem> predicate) {
		this.slot = slot;
		this.id = id;
		this.predicate = predicate;
	}

	public PlayerEquipment(SLOTS slot, String name, Predicate<RSItem> predicate) {
		this(slot,
				OSBuddy.get(name)
						.getId(),
				predicate);
	}

	public PlayerEquipment(SLOTS slot, int id) {
		this(slot, id, Filters.Items.idEquals(id));
	}

	public PlayerEquipment(SLOTS slot, String name) {
		this(slot, name, Filters.Items.nameEquals(name));
	}

	public PlayerEquipment(SLOTS slot) {
		this(slot,
				Equipment.getItem(slot)
						.getID());
	}

	public SLOTS getSlot() {
		return this.slot;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		try {
			return OSBuddy.get(this.id)
					.getName();
		}
		catch (Exception e) {
			return "(ID: " + this.id + ")";
		}
	}

	public Predicate<RSItem> getPredicate() {
		return this.predicate;
	}

	public WritableImage getWritableImage() {
		return OSBuddyUtils.getWritableImage(this.id);
	}
}
