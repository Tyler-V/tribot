package scripts.usa.api.condition;

import org.tribot.api.General;

import scripts.usa.api.antiban.ABC;
import scripts.usa.api.util.Timer;

public interface ResultCondition {

	final static int DEFAULT_TIMEOUT = 3000;

	public Status getStatus();

	public static Result wait(long timeout, ResultCondition condition, Callback callback) {
		Timer timer = new Timer(timeout);
		while (timer.isRunning()) {
			switch (condition.getStatus()) {
				case INTERRUPT:
					return Result.INTERRUPTED;
				case SUCCESS:
					return Result.SUCCESS;
				case RESET:
					timer.reset();
					break;
				default:
					break;
			}

			if (callback != null) {
				switch (callback.getStatus()) {
					case INTERRUPT:
						return Result.INTERRUPTED;
					case SUCCESS:
						return Result.SUCCESS;
					case RESET:
						timer.reset();
						break;
					default:
						break;
				}
			}

			if (ABC.performAntiban())
				timer.reset();

			General.sleep(250);
		}
		return Result.TIMEOUT;
	}

	public static Result wait(ResultCondition condition) {
		return wait(DEFAULT_TIMEOUT, condition, null);
	}

	public static Result wait(long timeout, ResultCondition condition) {
		return wait(timeout, condition, null);
	}

	public static Result wait(ResultCondition condition, Callback callback) {
		return wait(DEFAULT_TIMEOUT, condition, callback);
	}

	public static boolean waitFor(ResultCondition condition, Result result) {
		return wait(condition) == result;
	}

	public static class Success {
		public static boolean wait(ResultCondition condition) {
			return waitFor(condition, Result.SUCCESS);
		}
	}
}
