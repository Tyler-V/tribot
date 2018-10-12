package scripts.clay_miner.data;

import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public int clay;
}
