package scripts.tablets.data;

import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.House.HouseMode;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;
import scripts.usa.api2007.enums.Lecterns;
import scripts.usa.api2007.enums.Tablets;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public Lecterns lectern;
	public Tablets tablet;
	public HouseMode houseMode;
	public String friendName;
	public boolean servant;
	public boolean hosting;
	public boolean antiban;
	public boolean reactionSleep;

	public int tablets;
	public int profit;
}
