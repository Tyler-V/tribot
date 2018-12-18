package scripts.agility;

import java.util.Arrays;
import java.util.Comparator;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.agility.data.Vars;
import scripts.agility.data.courses.rooftops.RooftopCourse;
import scripts.agility.paint.AgilityPaint;
import scripts.agility.tasks.MarkOfGrace;
import scripts.agility.tasks.Rooftop;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

@ScriptManifest(authors = { "Usa" }, category = "Agility", name = "USA Agility", version = 7.0)
public class UsaAgility extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	@Override
	public void init() {
		setVars(new Vars(this));
		setPaint(new AgilityPaint());
		setTasks(new MarkOfGrace(), new Rooftop());
	}

	@Override
	public void onScriptStart() {
		ABC.setAntiban(false);
		ABC.setReactionSleeping(false);
		ABC.setHover(false);

		Comparator<RooftopCourse> closestCourse = new Comparator<RooftopCourse>() {
			@Override
			public int compare(RooftopCourse a, RooftopCourse b) {
				int distanceA = Player.getPosition()
						.distanceTo(a.getObstacles()
								.get(0)
								.getStartTile());
				int distanceB = Player.getPosition()
						.distanceTo(b.getObstacles()
								.get(0)
								.getStartTile());
				return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
			}
		};

		Vars.get().rooftopCourse = Arrays.stream(RooftopCourse.values())
				.sorted(closestCourse)
				.findFirst()
				.get();
	}

	@Override
	public void onScriptLoop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScriptEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		if (change == InventoryChange.INCREASE) {
			if (RSItemUtils.getName(item)
					.equals("Mark of grace"))
				Vars.get().markOfGraces++;
		}

	}

}
