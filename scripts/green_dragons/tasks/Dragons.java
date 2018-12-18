package scripts.green_dragons.tasks;

import org.tribot.api.General;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;

import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.green_dragons.data.Vars;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.Status;
import scripts.usa.api.framework.task.PriorityTask;
import scripts.usa.api2007.Consumables;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;

public class Dragons implements PriorityTask {

	@Override
	public boolean validate() {
		if (!Consumables.hasFood()) {
			if (!Vars.get().location.getArea()
					.contains(Player.getPosition()))
				return false;
			if (Player.getRSPlayer()
					.getHealthPercent() < Vars.get().eatHealthPercent)
				return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (!Vars.get().location.getArea()
				.contains(Player.getPosition()) || (!Walking.isTileOnMinimap(Vars.get().location.getCenterTile()) && getGreenDragonEntity().getFirstResult() == null)) {
			Vars.get().status = "Walking to " + Vars.get().location.getName();
			Walking.travel(Vars.get().location.getCenterTile(), new WalkingCondition() {
				public State action() {
					if (Vars.get().location.getArea()
							.contains(Player.getPosition()) && getGreenDragonEntity().getFirstResult() != null)
						return State.EXIT_OUT_WALKER_SUCCESS;
					return State.CONTINUE_WALKER;
				}
			});
		}
		else {
			Vars.get().status = "Searching for Green Dragon";
			Result status = Entity.interact("Attack", getGreenDragonEntity(), () -> {
				Vars.get().status = "Attacking Green Dragon";
				useSpecialAttack();
				setAutoRetaliate();
				if (Conditions.isCharacterDead(Entity.getCurrent())
						.isTrue())
					return Status.SUCCESS;
				if (Vars.get().evade || Conditions.hasFoundLoot(Vars.get().looter)
						.isTrue() ||
						Player.getRSPlayer()
								.getHealthPercent() <= Vars.get().nextEatAt)
					return Status.INTERRUPT;
				if (Conditions.isPlayerActive()
						.isTrue())
					return Status.RESET;
				return Status.CONTINUE;
			});
			if (status == Result.SUCCESS) {
				Vars.get().status = "Waiting for loot";
				if (Condition.wait(General.randomSD(4000, 500), () -> {
					if (Inventory.getAll().length >= 26) {
						Vars.get().status = "Eating to make room";
						Consumables.eat();
					}
					return Conditions.hasFoundLoot(Vars.get().looter)
							.isTrue();
				})) {
					Vars.get().kills++;
				}
			}
		}
	}

	private NpcEntity getGreenDragonEntity() {
		return Entities.find(NpcEntity::new)
				.nameEquals("Green dragon")
				.custom(npc -> {
					if (npc.isInCombat() && !npc.isInteractingWithMe())
						return false;
					return true;
				})
				.sortByInteracting();
	}

	private boolean setAutoRetaliate() {
		if (Combat.isAutoRetaliateOn())
			return false;
		return Combat.setAutoRetaliate(true);
	}

	private boolean useSpecialAttack() {
		if (!Vars.get().useSpecialAttack || Game.isSpecialActivated())
			return false;
		return Game.activateSpecial(Vars.get().specialEnergy);
	}
}
