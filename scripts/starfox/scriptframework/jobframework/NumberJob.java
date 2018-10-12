package scripts.starfox.scriptframework.jobframework;

/**
 * Created by nolan on 10/5/2016.
 */
public abstract class NumberJob
        extends Job {

    private final int goal;
    private int number;

    /**
     * Constructs a new NumberJob.
     *
     * @param goal The number goal.
     */
    public NumberJob(int goal) {
        this.goal = goal;
        this.number = 0;
    }

    public int getNumber() {
        return this.number;
    }

    @Override
    public boolean isComplete() {
        return number >= goal;
    }
}
