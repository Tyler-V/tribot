package scripts.usa.api.condition;

public interface Condition {

	final static int DEFAULT_TIMEOUT = 3000;

	public boolean isTrue();

	public static boolean wait(long timeout, Condition condition, Callback callback) {
		return ResultCondition.wait(timeout, () -> condition.isTrue() ? Status.SUCCESS : Status.CONTINUE, callback) == Result.SUCCESS;
	}

	public static boolean wait(Condition condition) {
		return wait(DEFAULT_TIMEOUT, condition, null);
	}

	public static boolean wait(long timeout, Condition condition) {
		return wait(timeout, condition, null);
	}

	public static boolean wait(Condition condition, Callback callback) {
		return wait(DEFAULT_TIMEOUT, condition, callback);
	}
}
