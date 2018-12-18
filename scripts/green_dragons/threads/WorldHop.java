package scripts.green_dragons.threads;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;

import scripts.green_dragons.data.Vars;
import scripts.usa.api.threads.VolatileRunnable;
import scripts.usa.api2007.Cannon;
import scripts.usa.api2007.worlds.WorldHopper;

public class WorldHop extends VolatileRunnable {

	@Override
	public void execute() {
		try {
			if (!Vars.get().location.getArea().contains(Player.getPosition()) || Vars.get().changeWorlds)
				return;

			if (getPlayersInArea() > Vars.get().maxPlayers) {
				Vars.get().changeWorlds = true;
				General.println("Changing from world " + WorldHopper.getCurrentWorld() + ", found " + getPlayersInArea() + " players in the area");
				return;
			}

			if (Vars.get().dwarfCannon && Cannon.isFiring()) {
				Vars.get().changeWorlds = true;
				General.println("Changing from world " + WorldHopper.getCurrentWorld() + ", found active dwarf cannon");
				return;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getPlayersInArea() {
		return Players.getAll(player -> {
			if (Player.getRSPlayer() == player)
				return false;
			if (!Vars.get().location.getArea().contains(player))
				return false;
			return true;
		}).length;
	}
}
