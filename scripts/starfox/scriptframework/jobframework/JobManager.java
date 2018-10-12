package scripts.starfox.scriptframework.jobframework;

import scripts.starfox.api.Client;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nolan on 10/7/2015.
 */
public class JobManager {

    private final ArrayList<Job> jobList;

    /**
     * Constructs a new JobManager.
     *
     * @param jobs The Jobs to add to the JobManager (if any).
     */
    public JobManager(Job... jobs) {
        jobList = new ArrayList<>();
        Collections.addAll(jobList, jobs);
    }

    /**
     * Gets the list of Jobs in the JobManager.
     *
     * @return The list of Jobs.
     */
    public ArrayList<Job> getJobs() {
        return this.jobList;
    }

    /**
     * Gets the first Job in the manager.
     *
     * @return The first Job.
     */
    public Job getFirst() {
        return getJobs().get(0);
    }

    /**
     * Filters through the Jobs in the JobManager and does the first Job that is incomplete.
     * Removes any Jobs from the JobManager that are complete.
     */
    public void doJobs() {
        Job[] jobs = getJobs().toArray(new Job[getJobs().size()]);
        for (Job job : jobs) {
            if (!job.isComplete()) {
                job.doJob();
                break;
            } else {
                Client.println("Job completed: " + job.toString());
                getJobs().remove(job);
            }
        }
    }
}
