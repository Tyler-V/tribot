package scripts.usa.api.threads;

public class SafeThread {

	private final VolatileRunnable runnable;
	private Thread thread;

	public SafeThread(VolatileRunnable runnable) {
		this.runnable = runnable;
		this.thread = new Thread(runnable);
		this.thread.start();
	}

	public void stop() {
		runnable.stop();
	}

	public Thread getThread() {
		return this.thread;
	}

	public VolatileRunnable getVolatileRunnable() {
		return this.runnable;
	}
}
