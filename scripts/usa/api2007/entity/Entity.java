package scripts.usa.api2007.entity;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;

import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.Time;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api2007.Camera;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.selector.PositionableFinder;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.RSItemFinder;

public class Entity {

	private static Positionable current = null;
	private static Positionable next = null;
	private static Camera camera = new Camera();

	public static Positionable getCurrent() {
		return current;
	}

	public static Positionable getNext() {
		return next;
	}

	public static Polygon getCurrentEnclosedArea() {
		return getEnclosedArea(current);
	}

	public static Polygon getNextEnclosedArea() {
		return getEnclosedArea(next);
	}

	public static boolean interact(String action, PositionableFinder<?, ?> positionableFinder, Condition success) {
		return interact(action, positionableFinder, () -> {
			if (success.isTrue())
				return Status.SUCCESS;
			if (Conditions.isPlayerActive().isTrue())
				return Status.RESET;
			return Status.CONTINUE;
		}) == Result.SUCCESS;
	}

	public static Result interact(String action, PositionableFinder<?, ?> positionableFinder, ResultCondition condition) {
		try {
			current = getEntity(positionableFinder);
			if (current == null)
				return Result.ERROR;

			ABC.sleepReactionTime(condition);

			if (ChooseOption.isOpen()) {
				if (isMouseOverMenuOption(current, action) && selectMenuOption(current, action))
					return sleep(positionableFinder, action, condition);
				ChooseOption.close();
				Condition.wait(Conditions.isChooseOptionClosed());
			}

			Clickable07 entity = (Clickable07) current;
			if (entity == null)
				return Result.ERROR;

			if (!entity.isClickable())
				camera.adjustTo(current);

			if (!entity.isClickable())
				Walking.travel(current, 1);

			if (!entity.isClickable())
				return Result.ERROR;

			String entityName = getName(current);
			if (entityName == null)
				return Result.ERROR;

			ScriptVars.get().status = action + " " + entityName;

			if (entity.click(action.contains(entityName) ? action : action + " " + entityName))
				return sleep(positionableFinder, action, condition);

			return Result.ERROR;
		}
		finally {
			current = null;
			if (!isEntityValid(positionableFinder, next))
				next = null;
		}
	}

	public static boolean useItemOn(RSItemFinder<ItemEntity> itemEntity, PositionableFinder<?, ?> positionableFinder, Condition success) {
		return useItemOn(itemEntity, positionableFinder, () -> {
			if (success.isTrue())
				return Status.SUCCESS;
			if (Conditions.isPlayerActive().isTrue())
				return Status.RESET;
			return Status.CONTINUE;
		}) == Result.SUCCESS;
	}

	public static Result useItemOn(RSItemFinder<ItemEntity> itemEntity, PositionableFinder<?, ?> positionableFinder, ResultCondition condition) {
		Interfaces.closeAll();

		current = getEntity(positionableFinder);
		if (current == null)
			return Result.ERROR;

		Clickable07 entity = (Clickable07) current;

		if (!entity.isClickable())
			Walking.travel(current, 1);

		if (!entity.isClickable())
			camera.adjustTo(current);

		if (!entity.isClickable())
			return Result.ERROR;

		RSItem[] items = itemEntity.getResults();
		if (items.length == 0)
			return Result.ERROR;

		RSItem item = items[General.random(0, items.length - 1)];

		RSItemDefinition definition = item.getDefinition();
		if (definition == null)
			return Result.ERROR;

		String itemName = definition.getName();
		if (itemName == null)
			return Result.ERROR;

		ScriptVars.get().status = "Clicking " + itemName;

		if (!Game.isUptext("Use " + itemName + " ->")) {
			if (Inventory.open() && item.click())
				Condition.wait(Conditions.isUptext("Use " + itemName + " ->"));
		}

		if (!Game.isUptext("Use " + itemName + " ->"))
			return Result.ERROR;

		return interact("Use " + itemName + " ->", positionableFinder, condition);
	}

	private static Result sleep(PositionableFinder<?, ?> positionableFinder, String action, ResultCondition condition) {
		try {
			ABC.setTime(Time.START);
			return ResultCondition.wait(condition, () -> {
				if (!isEntityValid(positionableFinder, current))
					return Status.SUCCESS;

				if (Player.getRSPlayer().isInCombat())
					ABC.setTime(Time.COMBAT);

				next = getNextEntity(positionableFinder);

				if (ABC.shouldHover()) {
					if (ABC.shouldOpenMenu()) {
						hoverOverMenuOption(next, action);
					}
					else {
						hover(next);
					}
				}

				return Status.CONTINUE;
			});
		}
		finally {
			ABC.setTime(Time.END);
			current = null;
		}
	}

	private static boolean isEntityValid(PositionableFinder<?, ?> positionableFinder, Positionable positionable) {
		if (positionable == null)
			return false;

		Positionable[] entities = (Positionable[]) positionableFinder.getResults();
		if (entities.length == 0)
			return false;

		return Arrays.stream(entities).anyMatch(entity -> entity.equals(positionable));
	}

	private static Positionable getEntity(PositionableFinder<?, ?> positionableFinder) {
		if (isEntityValid(positionableFinder, current))
			return current;

		if (isEntityValid(positionableFinder, next)) {
			next = null;
			return next;
		}

		return positionableFinder.getFirstResult();
	}

	private static Positionable getNextEntity(PositionableFinder<?, ?> positionableFinder) {
		if (isEntityValid(positionableFinder, next))
			return next;

		Positionable[] entities = Arrays.stream(positionableFinder.getResults())
				.filter(entity -> !entity.equals(current))
				.toArray(Positionable[]::new);
		if (entities.length == 0)
			return null;

		return entities[0];
	}

	private static boolean isHovering(Positionable positionable) {
		if (positionable == null)
			return false;

		Polygon area = getEnclosedArea(positionable);
		if (area == null)
			return false;

		return Conditions.isMouseInPolygon(area).isTrue();
	}

	private static boolean hover(Positionable positionable) {
		if (positionable == null)
			return false;

		if (isHovering(positionable))
			return true;

		Clickable07 clickable = (Clickable07) positionable;

		if (!clickable.isClickable())
			camera.adjustTo(positionable);

		clickable.hover();

		return Condition.wait(() -> isHovering(positionable));
	}

	private static boolean selectMenuOption(Positionable positionable, String action) {
		if (positionable == null || !ChooseOption.isOpen())
			return false;

		if (isMouseOverMenuOption(positionable, action)) {
			Mouse.click(1);
			return Condition.wait(Conditions.isChooseOptionClosed());
		}

		if (!ChooseOption.isOpen())
			return false;

		final int index = getMenuNodeIndex(positionable);
		if (index <= 0)
			return false;

		return ChooseOption.select(new Filter<RSMenuNode>() {
			@Override
			public boolean accept(RSMenuNode node) {
				return node.getAction().equalsIgnoreCase(action) && node.getData3() == index;
			}
		});
	}

	private static boolean isMouseOverMenuOption(Positionable positionable, String action) {
		if (positionable == null || !ChooseOption.isOpen())
			return false;

		RSMenuNode menuNode = getMenuNodeFor(positionable, action);
		if (menuNode == null)
			return false;

		Rectangle area = menuNode.getArea();
		if (area == null)
			return false;

		return area.contains(Mouse.getPos());
	}

	private static boolean hoverOverMenuOption(Positionable positionable, String action) {
		if (positionable == null)
			return false;

		if (isMouseOverMenuOption(positionable, action))
			return true;

		if (ChooseOption.isOpen() && getMenuNodeFor(positionable, action) == null) {
			ChooseOption.close();
			Condition.wait(Conditions.isChooseOptionClosed());
		}

		if (Clicking.hover((Clickable) positionable)) {
			Mouse.click(3);
			Condition.wait(Conditions.isChooseOptionOpen());
		}

		if (!ChooseOption.isOpen())
			return false;

		RSMenuNode menuNode = getMenuNodeFor(positionable, action);
		if (menuNode == null)
			return false;

		Rectangle rectangle = menuNode.getArea();
		if (rectangle == null)
			return false;

		Mouse.moveBox(rectangle);
		return Condition.wait(Conditions.isMouseInRectangle(rectangle));
	}

	private static RSMenuNode getMenuNodeFor(Positionable positionable, String action) {
		if (!ChooseOption.isOpen() || positionable == null)
			return null;

		RSMenuNode[] nodes = ChooseOption.getMenuNodes();
		final int index = getMenuNodeIndex(positionable);
		if (index <= 0)
			return null;

		for (RSMenuNode node : nodes) {
			if (node.getAction().equalsIgnoreCase(action) && node.getData3() == index)
				return node;
		}

		return null;
	}

	private static int getMenuNodeIndex(Positionable positionable) {
		try {
			if (positionable instanceof RSObject) {
				return ((RSObject) positionable).getID();
			}
			else if (positionable instanceof RSNPC) {
				return ((RSNPC) positionable).getIndex();
			}
			else if (positionable instanceof RSPlayer) {
				return ((RSPlayer) positionable).getIndex();
			}
			else if (positionable instanceof RSGroundItem) {
				return ((RSGroundItem) positionable).getID();
			}
			else if (positionable instanceof RSCharacter) {
				return ((RSCharacter) positionable).getIndex();
			}
		}
		catch (Exception e) {
			return -1;
		}
		return -1;
	}

	private static String getName(Positionable positionable) {
		try {
			if (positionable instanceof RSObject) {
				return ((RSObject) positionable).getDefinition().getName();
			}
			else if (positionable instanceof RSNPC) {
				return ((RSNPC) positionable).getDefinition().getName();
			}
			else if (positionable instanceof RSPlayer) {
				return ((RSPlayer) positionable).getName();
			}
			else if (positionable instanceof RSGroundItem) {
				return ((RSGroundItem) positionable).getDefinition().getName();
			}
			else if (positionable instanceof RSCharacter) {
				return ((RSCharacter) positionable).getName();
			}
		}
		catch (Exception e) {
			return null;
		}
		return null;
	}

	private static Polygon getEnclosedArea(Positionable positionable) {
		try {
			if (positionable instanceof RSObject) {
				return ((RSObject) positionable).getModel().getEnclosedArea();
			}
			else if (positionable instanceof RSNPC) {
				return ((RSNPC) positionable).getModel().getEnclosedArea();
			}
			else if (positionable instanceof RSPlayer) {
				return ((RSPlayer) positionable).getModel().getEnclosedArea();
			}
			else if (positionable instanceof RSGroundItem) {
				return ((RSGroundItem) positionable).getModel().getEnclosedArea();
			}
			else if (positionable instanceof RSCharacter) {
				return ((RSCharacter) positionable).getModel().getEnclosedArea();
			}
		}
		catch (Exception e) {
			return null;
		}
		return null;
	}
}
