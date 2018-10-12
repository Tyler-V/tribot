package scripts.usa.api2007;

import java.awt.Polygon;
import java.awt.geom.Area;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

public class Visibility {
	private static boolean CHECK_VISIBILITY;

	public static void setCheck(boolean option) {
		CHECK_VISIBILITY = option;
	}

	public static boolean isVisible(final RSObject entity) {
		if (General.isLookingGlass() || !CHECK_VISIBILITY)
			return true;
		if (entity == null)
			return false;
		final int entity_distance = Player.getPosition().distanceTo(entity);
		final RSObject[] visibility_restricting_objects = Objects.find(entity_distance, new Filter<RSObject>() {
			public boolean accept(RSObject o) {
				return entity.getPosition().distanceTo(o) <= entity_distance;
			}
		});
		if (visibility_restricting_objects.length == 0)
			return false;
		final RSModel entity_model = entity.getModel();
		if (entity_model == null)
			return false;
		final Polygon entity_enclosed_area = entity_model.getEnclosedArea();
		if (entity_enclosed_area == null)
			return false;
		Area entity_area = new Area(entity_enclosed_area);
		for (RSObject object : visibility_restricting_objects) {
			RSModel object_model = object.getModel();
			if (object_model != null) {
				Polygon object_enclosed_area = object_model.getEnclosedArea();
				if (object_enclosed_area != null) {
					entity_area.subtract(new Area(object_enclosed_area));
				}
			}
		}
		return !entity_area.isEmpty();
	}

	public static boolean isVisible(final RSNPC entity) {
		if (entity == null)
			return false;
		final int distance = Player.getPosition().distanceTo(entity);
		final RSObject[] obstructions = Objects.find(distance, new Filter<RSObject>() {
			public boolean accept(RSObject o) {
				return entity.getPosition().distanceTo(o) <= distance;
			}
		});
		if (obstructions.length == 0)
			return false;
		final RSModel entityModel = entity.getModel();
		if (entityModel == null)
			return false;
		final Polygon entityPolygon = entityModel.getEnclosedArea();
		if (entityPolygon == null)
			return false;
		Area entityArea = new Area(entityPolygon);
		for (RSObject o : obstructions) {
			RSModel objectModel = o.getModel();
			if (objectModel != null) {
				Polygon objectPolygon = objectModel.getEnclosedArea();
				if (objectPolygon != null)
					entityArea.subtract(new Area(objectPolygon));
			}
		}
		return !entityArea.isEmpty();
	}

	public static boolean isVisible(final RSGroundItem entity) {
		if (entity == null)
			return false;
		final int distance = Player.getPosition().distanceTo(entity);
		final RSObject[] obstructions = Objects.find(distance, new Filter<RSObject>() {
			public boolean accept(RSObject o) {
				return entity.getPosition().distanceTo(o) <= distance;
			}
		});
		if (obstructions.length == 0)
			return false;
		final RSModel entityModel = entity.getModel();
		if (entityModel == null)
			return false;
		final Polygon entityPolygon = entityModel.getEnclosedArea();
		if (entityPolygon == null)
			return false;
		Area entityArea = new Area(entityPolygon);
		for (RSObject object : obstructions) {
			RSModel objectModel = object.getModel();
			if (objectModel != null) {
				Polygon objectPolygon = objectModel.getEnclosedArea();
				if (objectPolygon != null) {
					entityArea.subtract(new Area(objectPolygon));
				}
			}
		}
		return !entityArea.isEmpty();
	}
}
