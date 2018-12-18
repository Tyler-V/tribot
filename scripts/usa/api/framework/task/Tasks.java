package scripts.usa.api.framework.task;

import java.util.Arrays;
import java.util.List;

public class Tasks {

	private List<Task> tasks;
	private int index = 0;

	public Tasks(Task... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

	public List<Task> getTasks() {
		return this.tasks;
	}

	public Task current() {
		return this.tasks.get(this.index);
	}

	public void reset() {
		this.index = 0;
	}

	public void next() {
		if (this.index + 1 >= this.tasks.size()) {
			this.index = 0;
		}
		else {
			this.index++;
		}
	}
}
