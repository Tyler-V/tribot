package scripts.usa.api.condition;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;

import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.looting.Looter;

public class Conditions {

	public static Condition isContinueChatUp() {
		return () -> NPCChat.getClickContinueInterface() != null;
	}

	public static Condition isChooseOptionOpen() {
		return () -> ChooseOption.isOpen();
	}

	public static Condition isChooseOptionClosed() {
		return () -> !ChooseOption.isOpen();
	}

	public static Condition isUptext(String text) {
		return () -> Game.isUptext(text);
	}

	public static Condition isMoving() {
		return () -> Player.isMoving();
	}

	public static Condition isAnimation(int id) {
		return () -> Player.getAnimation() == id;
	}

	public static Condition isAnimating() {
		return () -> Player.getAnimation() != -1;
	}

	public static Condition isPlayerActive() {
		return () -> Player.isMoving() || Player.getAnimation() != -1;
	}

	public static Condition isPlayerMoving() {
		return () -> Player.isMoving();
	}

	public static Condition hasPlayerMoved(final RSTile startingPosition) {
		return () -> Player.getPosition()
				.distanceTo(startingPosition) > 1;
	}

	public static Condition isPlayerInteracting() {
		return () -> {
			try {
				return Player.getRSPlayer()
						.getInteractingIndex() != -1;
			}
			catch (Exception e) {
				return false;
			}
		};
	}

	public static Condition isPlayerInteractingWith(String... names) {
		return () -> {
			try {
				if (Player.getRSPlayer()
						.getInteractingIndex() != -1) {
					RSCharacter character = Player.getRSPlayer()
							.getInteractingCharacter();
					if (Arrays.stream(names)
							.anyMatch(name -> name.equalsIgnoreCase(character.getName())))
						return true;
				}
				return false;
			}
			catch (Exception e) {
				return false;
			}
		};
	}

	public static Condition isInteractingCharacterInCombat() {
		return () -> {
			try {
				return Player.getRSPlayer()
						.getInteractingCharacter()
						.isInCombat();
			}
			catch (Exception e) {
				return false;
			}
		};
	}

	public static Condition isInCombat() {
		return () -> {
			try {
				return Player.getRSPlayer()
						.isInCombat();
			}
			catch (Exception e) {
				return false;
			}
		};
	}

	public static Condition canReach(final RSTile tile) {
		return () -> PathFinding.canReach(tile, false);
	}

	public static Condition inventoryChanged(RSItem[] inventory) {
		return () -> Inventory.getAll().length != inventory.length;
	}

	public static Condition inventoryContains(final RSItem item) {
		return () -> item == null ? false : Inventory.getCount(item.getID()) > 0;
	}

	public static Condition inventoryContains(final String... name) {
		return () -> Inventory.getCount(name) > 0;
	}

	public static Condition inventoryContains(final int... id) {
		return () -> Inventory.getCount(id) > 0;
	}

	public static Condition inventoryDoesNotContain(final RSItem item) {
		return () -> item == null ? true : Inventory.getCount(item) == 0;
	}

	public static Condition inventoryDoesNotContain(final String... name) {
		return () -> Inventory.getCount(name) == 0;
	}

	public static Condition inventoryOnlyContains(final String... name) {
		return () -> Inventory.getCount(name) == Inventory.getAll().length;
	}

	public static Condition inventoryDoesNotContain(final int... id) {
		return () -> Inventory.getCount(id) == 0;
	}

	public static Condition isInterfaceValid(final int index) {
		return () -> Interfaces.isInterfaceValid(index);
	}

	public static Condition isMouseInRectangle(final Rectangle rectangle) {
		return () -> rectangle.contains(Mouse.getPos());
	}

	public static Condition isMouseInPolygon(Polygon area) {
		return () -> area.contains(Mouse.getPos());
	}

	public static Condition isOnScreen(final Positionable positionable) {
		return () -> positionable.getPosition()
				.isOnScreen();
	}

	public static Condition hasFoundLoot(Looter looter) {
		return () -> looter.hasFoundLoot();
	}

	public static Condition isCharacterDead(Positionable positionable) {
		return () -> {
			if (positionable == null)
				return false;
			RSCharacter character = (RSCharacter) positionable;
			return character != null && character.getHealthPercent() == 0.0;
		};
	}
}
