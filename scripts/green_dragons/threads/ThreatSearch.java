package scripts.green_dragons.threads;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.types.RSPlayer;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.threads.VolatileRunnable;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Wilderness;

public class ThreatSearch extends VolatileRunnable {

	@Override
	public void execute() {
		try {
			if (Vars.get().evade)
				return;

			int wilderness = Wilderness.getLevel();
			if (wilderness <= 0)
				return;

			if (Wilderness.isSkulled()) {
				General.println("We are skulled!");
				Vars.get().evade = true;
			}

			if (Game.isPoisoned()) {
				General.println("We are poisoned!");
				Vars.get().evade = true;
			}

			RSPlayer[] players = Players.getAll(p -> {
				if (p.getName().equalsIgnoreCase(Player.getRSPlayer().getName()))
					return false;
				if (p.getCombatLevel() < (Player.getRSPlayer().getCombatLevel() - wilderness))
					return false;
				if (p.getCombatLevel() > (Player.getRSPlayer().getCombatLevel() + wilderness))
					return false;
				return p.getSkullIcon() == 0 && p.isInteractingWithMe() || Equipment.isPlayerWearing(p, Vars.get().enemyEquipment);
			});

			if (players.length > 0) {
				RSPlayer player = players[0];
				Vars.get().evade = true;
				General.println("\"" + player.getName() +
						"\"" +
						"(Level: " +
						player.getCombatLevel() +
						")" +
						((player.getSkullIcon() == 0 && player.isInteractingWithMe()) ? " is attacking us" : " found") +
						" in wilderness level " +
						wilderness);
				General.println("Equipment: " + Equipment.getEquipment(player));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
