package scripts.usa.api.framework.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tribot.api.General;

public class Tasks extends TreeSet<Task> {

	private static final long serialVersionUID = 1480107716982998226L;
	private static Boolean priorityTask;

	private static Comparator<? super Task> comparator = (Task t1, Task t2) -> {
		if (t1 instanceof PriorityTask) {
			if (t2 instanceof PriorityTask)
				return Integer.compare(((PriorityTask) t1).priority(), ((PriorityTask) t2).priority()) < 0 ? -1 : 1;
			return -1;
		}
		if (t2 instanceof PriorityTask)
			return 1;
		return Integer.compare(t1.hashCode(), t2.hashCode());
	};

	public Tasks() {
		super(comparator);
	}

	public Tasks(Comparator<? super Task> comparator) {
		super(comparator);
	}

	public Tasks(Task... tasks) {
		super(comparator);
		super.addAll(Arrays.asList(tasks));
	}

	public Tasks(Collection<? extends Task> c) {
		this(c.toArray(new Task[c.size()]));
	}

	public Tasks(SortedSet<Task> s) {
		this(s.toArray(new Task[s.size()]));
	}

	public boolean hasPriorityTask() {
		if (priorityTask != null)
			return priorityTask.booleanValue();

		priorityTask = false;
		Iterator<Task> itr = iterator();
		while (itr.hasNext()) {
			Task task = itr.next();
			if (task instanceof PriorityTask) {
				priorityTask = true;
				break;
			}
		}

		return priorityTask.booleanValue();
	}
}
