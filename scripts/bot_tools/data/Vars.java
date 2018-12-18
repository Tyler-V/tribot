package scripts.bot_tools.data;

import java.util.List;

import scripts.bot_tools.models.GEItem;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public List<GEItem> buyingItems;
	public List<GEItem> sellingItems;
}
