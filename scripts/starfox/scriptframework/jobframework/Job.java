package scripts.starfox.scriptframework.jobframework;

/**
 * Created by Nolan on 10/7/2015.
 */
public abstract class Job {

    public static final Job NULL = new Job() {
        @Override
        public void doJob() {

        }

        @Override
        public boolean isComplete() {
            return true;
        }
    };

    /**
     * This method should be used to do the job.
     */
    public abstract void doJob();

    /**
     * This method should be used to check if the job is complete or not.
     *
     * @return True if the job is complete, false otherwise.
     */
    public abstract boolean isComplete();
}
