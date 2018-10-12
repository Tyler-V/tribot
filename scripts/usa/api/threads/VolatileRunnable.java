package scripts.usa.api.threads;

import org.tribot.api.General;

public abstract class VolatileRunnable implements Runnable {

	private final String name;

	public VolatileRunnable() {
		this.name = getClass().getSimpleName();
	}

	private volatile boolean stop;

	public void stop() {
		System.out.println("Stopped " + name + " Thread");
		stop = true;
	}

	@Override
	public void run() {
		System.out.println("Started " + name + " Thread");
		while (!stop) {
			execute();
			General.sleep(50);
		}
	}

	public abstract void execute();

}
