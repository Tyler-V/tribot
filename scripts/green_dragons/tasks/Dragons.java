package scripts.green_dragons.tasks;

import org.tribot.api.General;
import org.tribot.api2007.Player;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;

public class Dragons implements PriorityTask {

	@Override
	public int priority() {
		return 6;
	}

	@Override
	public boolean validate() {
		if (!Consumables.hasFood()) {
			if (!Vars.get().location.getArea().contains(Player.getPosition()))
				return false;
			if (Player.getRSPlayer().getHealthPercent() < Vars.get().eatHealthPercent)
				return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (Vars.get().location.getArea().contains(Player.getPosition())) {
			Vars.get().status = "Searching for Green Dragon";
			Result status = Entity.interact("Attack", getGreenDragonEntity(), () -> {
				Vars.get().status = "Attacking Green Dragon";
				if (Conditions.isCharacterDead(Entity.getCurrent()).isTrue())
					return Status.SUCCESS;
				if (Conditions.hasFoundLoot(Vars.get().looter).isTrue() || Player.getRSPlayer().getHealthPercent() < Vars.get().eatHealthPercent)
					return Status.INTERRUPT;
				if (Conditions.isPlayerActive().isTrue())
					return Status.RESET;
				return Status.CONTINUE;
			});
			if (status == Result.SUCCESS) {
				Vars.get().status = "Waiting for loot";
				if (Condition.wait(General.random(3000, 4000), () -> Conditions.hasFoundLoot(Vars.get().looter).isTrue()))
					Vars.get().kills++;
			}
		}
		else {
			Vars.get().status = "Walking to " + Vars.get().location.getName();
			Walking.travel(Vars.get().location.getTile());
		}
	}

	private NpcEntity getGreenDragonEntity() {
		return Entities.find(NpcEntity::new).nameEquals("Green dragon").custom(npc -> {
			if (npc.isInCombat() && !npc.isInteractingWithMe())
				return false;
			return true;
		}).sortByInteracting().selectABC();
	}
}
