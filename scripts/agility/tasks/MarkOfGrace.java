package scripts.agility.tasks;

import org.tribot.api2007.Game;

import scripts.agility.data.Vars;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.GroundItemEntity;

public class MarkOfGrace implements PriorityTask {

	@Override
	public boolean validate() {
		return Vars.get().rooftopCourse != null && getMarkOfGraceEntity().getFirstResult() != null;
	}

	@Override
	public void execute() {
		Entity.interact("Take", getMarkOfGraceEntity(), () -> getMarkOfGraceEntity().getFirstResult() == null);

	}

	public static GroundItemEntity getMarkOfGraceEntity() {
		return Entities.find(GroundItemEntity::new)
				.nameEquals("Mark of grace")
				.custom(mark -> mark.getPosition()
						.getPlane() == Game.getPlane())
				.canReach(false);
	}
}
