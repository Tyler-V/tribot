package scripts.green_dragons.data;

import org.tribot.api.General;

import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.enums.Potions;
import scripts.usa.api2007.looting.Looter;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public int profit;
	public int kills;

	public boolean evade;
	public Locations location = Locations.GRAVEYARD;
	public Looter looter = new Looter(location.getArea(), 500);

	public String foodName = "Tuna";
	public int foodQuantity = 18;
	public double eatHealthPercent = General.randomDouble(.6, .8);

	public Potions potion1;
	public Potions potion2;
	public Potions potion3;

	public String[] enemyEquipment;
}
