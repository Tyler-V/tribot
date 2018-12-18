package scripts.green_dragons.threads;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.types.RSPlayer;

import scripts.green_dragons.data.EvadeOption;
import scripts.green_dragons.data.Vars;
import scripts.usa.api.threads.VolatileRunnable;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Wilderness;

public class ThreatSearch extends VolatileRunnable {

	private String playerName;

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

			if (playerName == null)
				playerName = Player.getRSPlayer()
						.getName();

			if (playerName == null)
				return;

			RSPlayer[] players = Players.getAll(p -> {
				if (p.getName()
						.equalsIgnoreCase(Player.getRSPlayer()
								.getName()))
					return false;
				if (p.getCombatLevel() < (Player.getRSPlayer()
						.getCombatLevel() - wilderness))
					return false;
				if (p.getCombatLevel() > (Player.getRSPlayer()
						.getCombatLevel() + wilderness))
					return false;
				if (p.getSkullIcon() == 0 && p.isInteractingWithMe())
					return true;
				if (Vars.get().evadeOption == EvadeOption.SKULLED_THREAT_DETECTED) {
					return p.getSkullIcon() == 0 && Equipment.isPlayerWearing(p, Vars.get().enemyEquipment);
				}
				else if (Vars.get().evadeOption == EvadeOption.THREAT_DETECTED) {
					return Equipment.isPlayerWearing(p, Vars.get().enemyEquipment);
				}
				return false;
			});

			if (players.length > 0) {
				RSPlayer player = players[0];
				Vars.get().evade = true;
				General.println("\"" + player.getName() + "\"" + "(Level: " + player.getCombatLevel() + ")" + ((player.getSkullIcon() == 0 && player.isInteractingWithMe()) ? " is attacking us" : " found") + " in wilderness level " + wilderness);
				General.println("Player was wearing " + Equipment.getEquipment(player));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
