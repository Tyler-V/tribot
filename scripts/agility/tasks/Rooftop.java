package scripts.agility.tasks;

import org.tribot.api.General;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.agility.data.Vars;
import scripts.agility.data.courses.rooftops.Obstacle;
import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;

public class Rooftop implements PriorityTask {

	@Override
	public boolean validate() {
		return Vars.get().rooftopCourse != null;
	}

	@Override
	public void execute() {
		Obstacle obstacle = Vars.get().rooftopCourse.getCurrentObstacle() != null ? Vars.get().rooftopCourse.getCurrentObstacle() : rooftopGlitch() != null ? rooftopGlitch() : null;
		if (obstacle == null)
			return;

		ObjectEntity obstacleEntity = getEntity(obstacle);

		if (obstacleEntity.getFirstResult() == null) {
			Vars.get().status = "Walking to " + obstacle.getName();
			Walking.travel(obstacle.getStartTile(), new WalkingCondition() {
				public State action() {
					RSObject object = obstacleEntity.getFirstResult();
					if (object != null && ((object.isClickable() && object.isOnScreen()) || Player.getPosition()
							.distanceTo(object) <= 5)) {
						return State.EXIT_OUT_WALKER_SUCCESS;
					}
					return State.CONTINUE_WALKER;
				}
			});
		}
		else {
			Result result = Entity.interact(obstacle.getAction(), obstacleEntity, () -> {
				Vars.get().status = "Negotiating " + obstacle.getName();
				if (atEnd(obstacle)) {
					Vars.get().status = "Completed " + obstacle.getName();
					return Status.SUCCESS;
				}
				if (MarkOfGrace.getMarkOfGraceEntity()
						.getFirstResult() != null) {
					Vars.get().status = "Found Mark of grace!";
					return Status.INTERRUPT;
				}
				if (obstacle.getStartTile()
						.getPlane() > 0 && Game.getPlane() == 0) {
					Vars.get().status = "Failed " + obstacle.getName();
					Vars.get().failedObstacles++;
					return Status.ERROR;
				}
				if (Conditions.isPlayerActive()
						.isTrue()) {
					setNextRotation(obstacle);
					return Status.RESET;
				}

				return Status.CONTINUE;
			});

			if (result == Result.SUCCESS) {
				if (obstacle == Vars.get().rooftopCourse.getLastObstacle()) {
					Vars.get().laps++;
				}
			}
		}
	}

	private Obstacle rooftopGlitch() {
		final RSTile CANIFIS_GAP_1 = new RSTile(3505, 3489, 2);
		final RSTile CANIFIS_GAP_5 = new RSTile(3487, 3476, 3);
		final RSTile POLLNIVNEACH_GAP_1 = new RSTile(3351, 2962, 1);
		if (Player.getPosition()
				.equals(CANIFIS_GAP_1)) {
			General.println("Canifis rooftop Pole-vault glitch detected!");
			return Obstacle.CANIFIS_GAP_1;
		}
		else if (Player.getPosition()
				.equals(CANIFIS_GAP_5)) {
			General.println("Canifis rooftop Tree glitch detected!");
			return Obstacle.CANIFIS_GAP_5;
		}
		else if (Player.getPosition()
				.equals(POLLNIVNEACH_GAP_1)) {
			General.println("Pollnivneach rooftop glitch detected!");
			return null;
		}
		return null;
	}

	private boolean atEnd(Obstacle obstacle) {
		return Player.getPosition()
				.distanceTo(obstacle.getEndTile()) <= 1 &&
				Game.getPlane() == obstacle.getEndTile()
						.getPlane();
	}

	private ObjectEntity getEntity(Obstacle obstacle) {
		return Entities.find(ObjectEntity::new)
				.nameEquals(obstacle.getName())
				.actionsEquals(obstacle.getAction())
				.closestTo(obstacle.getStartTile());
	}

	private boolean setNextRotation(Obstacle obstacle) {
		if (obstacle.getNextRotation() <= 0)
			return false;
		final int threshold = 15;
		if (isRotationSet(obstacle.getNextRotation(), threshold))
			return false;
		setRotation(obstacle.getNextRotation(), threshold);
		return true;
	}

	private boolean isRotationSet(int rotation, int threshold) {
		rotation = rotation >= 360 ? rotation % 360 : rotation;
		int min = rotation - threshold < 0 ? (360 + rotation - threshold) : rotation - threshold;
		int max = rotation + threshold > 360 ? (rotation + threshold) % 360 : rotation + threshold;
		int current = Camera.getCameraRotation();
		return current >= min && current <= max;
	}

	private void setRotation(int rotation, int threshold) {
		if (isRotationSet(rotation, threshold))
			return;
		Camera.setCameraRotation(rotation + General.random(-threshold, threshold));
	}
}
