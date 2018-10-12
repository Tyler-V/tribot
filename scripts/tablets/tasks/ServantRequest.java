package scripts.tablets.tasks;

import org.tribot.api.General;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.tablets.data.Vars;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.House;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;
import scripts.usa.api2007.servant.Servant;
import scripts.usa.api2007.servant.ServantMaterials;

public class ServantRequest implements Task {

	@Override
	public boolean validate() {
		return Vars.get().servant && House.isInside() && Entities.find(ItemEntity::new).nameEquals("Soft clay").isNotNoted().getFirstResult() == null;
	}

	@Override
	public void execute() {
		if (Servant.isOutOfMaterials()) {
			General.println("We are out of materials in bank!");
			Vars.get().stopScript();
		}

		Vars.get().status = "Talking to Servant";
		if (Servant.request(ServantMaterials.SOFT_CLAY)) {
			RSObject lectern = Entities.find(ObjectEntity::new).nameEquals("Lectern").getFirstResult();
			if (lectern != null) {
				RSTile lecternTile = getLecternTile(lectern);
				if (lecternTile != null && Player.getPosition().distanceTo(lecternTile) >= General.random(1, 3)) {
					Vars.get().status = "Walking to Lectern";
					Walking.travel(getLecternTile(lectern));
				}
			}
			Vars.get().status = "Waiting for Servant";
			ResultCondition.wait(() -> {
				if (Servant.isTalkingTo())
					return Status.SUCCESS;
				if (Servant.getServant() == null)
					return Status.RESET;
				return Status.CONTINUE;
			});
		}
	}

	@SuppressWarnings("deprecation")
	private RSTile getLecternTile(RSObject lectern) {
		if (lectern == null)
			return null;

		RSTile position = lectern.getPosition();

		double distanceNW = 0, distanceSE = 0, distanceNE = 0, distanceSW = 0;

		for (int i = 3; i < 10; i++) {
			distanceNW = PathFinding.distanceBetween(position.translate(-i, i), lectern, false);
			distanceSE = PathFinding.distanceBetween(position.translate(i, -i), lectern, false);
			distanceNE = PathFinding.distanceBetween(position.translate(i, i), lectern, false);
			distanceSW = PathFinding.distanceBetween(position.translate(-i, -i), lectern, false);
			if (distanceNW > 0 && distanceSE > 0 && distanceNE > 0 && distanceSW > 0)
				break;
		}

		double distanceA = Math.abs(distanceNW - distanceSE);
		double distanceB = Math.abs(distanceNE - distanceSW);

		if (distanceA > distanceB) {
			return distanceNW > distanceSE ? position.translate(-1, 0) : position.translate(1, 0);
		}
		else {
			return distanceNE > distanceSW ? position.translate(0, 1) : position.translate(0, -1);
		}
	}
}
