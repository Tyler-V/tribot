package scripts.agility.data;

import scripts.agility.data.courses.rooftops.RooftopCourse;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.framework.task.TaskScript;

public class Vars extends ScriptVars {

	public Vars(TaskScript taskScript) {
		super(taskScript);
	}

	public static Vars get() {
		return (Vars) ScriptVars.get();
	}

	public int markOfGraces;
	public int failedObstacles;
	public int laps;

	public RooftopCourse rooftopCourse = RooftopCourse.SEERS;

}
